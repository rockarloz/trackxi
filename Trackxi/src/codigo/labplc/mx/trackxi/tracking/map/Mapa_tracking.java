package codigo.labplc.mx.trackxi.tracking.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import codigo.labplc.mx.trackxi.R;
import codigo.labplc.mx.trackxi.fonts.fonts;
import codigo.labplc.mx.trackxi.network.NetworkUtils;
import codigo.labplc.mx.trackxi.tracking.Califica_taxi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Mapa_tracking extends Activity implements OnItemClickListener {
	 private GoogleMap map;
	private double latitud=0;
	private double longitud=0;
	private MarkerOptions marker;
	private MarkerOptions marker_taxi;
	private Button mapa_tracking_terminoviaje;
	private AutoCompleteTextView actvDestination;
	private MarkerOptions marker_taxi_destino;
	Double latini;
	Double lonini;
	Double latfin;
	Double lonfin;
	ArrayList<String> pointsLat;
	ArrayList<String> pointsLon;
	ArrayList<InfoPoint> InfoPoint = null;
	private LatLng taxiPosition = null;
	private boolean isFirstLocation= true;
	private String tiempo;
	private String distancia;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa_tracking);
		
		//propiedades del action bar
		 final ActionBar ab = getActionBar();
	     ab.setDisplayShowHomeEnabled(false);
	     ab.setDisplayShowTitleEnabled(false);     
	     

	     
	     //instancias
	     final LayoutInflater inflater = (LayoutInflater)getSystemService("layout_inflater");
	     View view = inflater.inflate(R.layout.abs_layout,null);   
	     ((TextView) view.findViewById(R.id.abs_layout_tv_titulo)).setTypeface(new fonts(Mapa_tracking.this).getTypeFace(fonts.FLAG_MAMEY));
	     ((TextView) view.findViewById(R.id.abs_layout_tv_titulo)).setText("MI VIAJE");
	     ((TextView)findViewById(R.id.mitaxi_googlemaps_tv_destination)).setTypeface(new fonts(Mapa_tracking.this).getTypeFace(fonts.FLAG_MAMEY));
	     ((TextView)findViewById(R.id.mitaxi_googlemaps_tv_destination)).setTextColor(new fonts(Mapa_tracking.this).getColorTypeFace(fonts.FLAG_GRIS_OBSCURO));
	     
	     //instancias en  
	     ab.setDisplayShowCustomEnabled(true);
	     ab.setCustomView(view,new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
	     ab.setCustomView(view);

	     //obtenemos los adicionales 
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			latitud = bundle.getDouble("latitud_inicial");	
			longitud = bundle.getDouble("longitud_inicial");


		}
		
		//escucha de botones
		mapa_tracking_terminoviaje =(Button)findViewById(R.id.mapa_tracking_terminoviaje);
		mapa_tracking_terminoviaje.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Intent intent_califica = new Intent(Mapa_tracking.this, Califica_taxi.class);
				startActivity(intent_califica);
				finish();
			}
		});
		
		
		actvDestination = (AutoCompleteTextView) findViewById(R.id.mitaxi_googlemaps_actv_destination);
	    actvDestination.setAdapter(new PlacesAutocompleteAdapter(this, R.layout.places_list_item));
	    actvDestination.setOnItemClickListener(this);
		
	     Button mitaxi_googlemaps_btn_destino =(Button)findViewById(R.id.mitaxi_googlemaps_btn_destino);
	     mitaxi_googlemaps_btn_destino.setOnClickListener(new View.OnClickListener() {
		
	    	 @Override
			public void onClick(View v) {
		           if(!actvDestination.getText().equals("")){
		            	String destino = actvDestination.getText().toString();
		            	destino = destino.replaceAll(" ", "+");
		            	String consulta = "http://maps.googleapis.com/maps/api/geocode/json?address="+destino+"&sensor=true";
						String querty = NetworkUtils.doHttpConnection(consulta);
						InfoPoint = null;
						InfoPoint = parsePoints(querty);
						map.clear();
						marker_taxi_destino = new MarkerOptions().position(new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude())).title("Mi destino");
						marker_taxi_destino.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_fin_viaje));
						map.addMarker(marker_taxi_destino);
						marker.position(new LatLng(latini,lonini));
						map.addMarker(marker);
						marker_taxi.position(new LatLng(latfin,lonfin));
						map.addMarker(marker_taxi);
						//.zoom(21);
						CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latfin, lonfin)).zoom(map.getCameraPosition().zoom).build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						 for (int i = 0; i < pointsLat.size() - 1; i++) {
							 LatLng src = new LatLng(Double.parseDouble(pointsLat.get(i)),Double.parseDouble(pointsLon.get(i)));
							 LatLng dest = new LatLng(Double.parseDouble(pointsLat.get(i+1)),Double.parseDouble(pointsLon.get(i+1)));
							 Polyline line = map.addPolyline(new PolylineOptions() //mMap is the Map Object
							 .add(new LatLng(src.latitude, src.longitude),
							 new LatLng(dest.latitude,dest.longitude))
							 .width(8).color(Color.BLUE).geodesic(true));
						  }

		           }/*else{
		        	   if(direccion_destino!=null){
		        		   actvDestination.setText(direccion_destino);
							marker_taxi_destino = new MarkerOptions().position(new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude())).title("Mi destino");
							marker_taxi_destino.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_fin_rojo));
							map.addMarker(marker_taxi_destino);
							
								String url = getDirectionsUrl(new LatLng(latfin,lonfin),new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude()));				
								
								DownloadTask downloadTask = new DownloadTask();
								downloadTask.execute(url);
						}
		           }
				*/
			}
		});
		setUpMapIfNeeded();
	}

	
	
	 private void setUpMapIfNeeded() {
			if (map == null) {
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mitaxi_trip_map)).getMap();
				if (map != null) {
					if(setUpMap()) {
						initMap();
					}
				}
			}
		}
		
		public void initMap() {
			map.setMyLocationEnabled(false);//quitar circulo azul;
			map.setBuildingsEnabled(true);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.getUiSettings().setZoomControlsEnabled(true); //ZOOM
			map.getUiSettings().setCompassEnabled(true); //COMPASS
			map.getUiSettings().setZoomGesturesEnabled(true); //GESTURES ZOOM
			map.getUiSettings().setRotateGesturesEnabled(true); //ROTATE GESTURES
			map.getUiSettings().setScrollGesturesEnabled(true); //SCROLL GESTURES
			map.getUiSettings().setTiltGesturesEnabled(true); //TILT GESTURES
			
			// create marker
			marker = new MarkerOptions().position(new LatLng(latitud, longitud)).title("Inicio del viaje");
			marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_chinche_llena));
			
			
			CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitud, longitud)).zoom(21).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			 
			 marker_taxi = new MarkerOptions().position(new LatLng(latitud, longitud)).title("Mi posici�n");
			 marker_taxi.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_taxi));
			// adding marker
			map.addMarker(marker);
			map.addMarker(marker_taxi);	
		}
		
		public boolean setUpMap() {
			if (!checkReady()) {
	            return false;
	        } else {
	        	return true;
	        }
		}
	    
		private boolean checkReady() {
	        if (map == null) {
	            return false;
	        }
	        return true;
	    }

		/**
		 * manejo de transmiciones
		 */
		private BroadcastReceiver onBroadcast = new BroadcastReceiver() {

			@Override
			public void onReceive(Context ctxt, Intent t) {
				
				 pointsLat = t.getStringArrayListExtra("latitud");
				 pointsLon = t.getStringArrayListExtra("longitud");
				
				 latini= Double.parseDouble(pointsLat.get(0));
				 lonini= Double.parseDouble(pointsLon.get(0));
				 latfin= Double.parseDouble(pointsLat.get(pointsLat.size()-1));
				 lonfin= Double.parseDouble(pointsLon.get(pointsLon.size()-1));
				 
				 if(InfoPoint!=null){
					 String consulta2 = "http://datos.labplc.mx/~mikesaurio/taxi.php?act=chofer&type=getGoogleData&lato="
								+latfin+"&lngo="+lonfin
								+"&latd="+InfoPoint.get(0).getDblLatitude()+"&lngd="+InfoPoint.get(0).getDblLongitude()+"&filtro=velocidad";
						String querty2 = NetworkUtils.doHttpConnection(consulta2);
						querty2= querty2.replaceAll("\"","");
						String[] Squerty2 = querty2.split(",");
						tiempo = Squerty2[0];
						distancia =Squerty2[1];
					 
					 	map.clear();
						marker.position(new LatLng(latini,lonini));
						map.addMarker(marker);
						
						marker_taxi.title("Estas a "+distancia+", "+tiempo+" de tu destino");
						marker_taxi.position(new LatLng(latfin,lonfin));
						map.addMarker(marker_taxi);
						
						//.zoom(21);map.getCameraPosition().zoom
						CameraPosition cameraPosition;
						if(isFirstLocation){
							 cameraPosition = new CameraPosition.Builder().target(new LatLng(latfin, lonfin)).zoom(21).build();
							 isFirstLocation= false;
						}else{
							 cameraPosition = new CameraPosition.Builder().target(new LatLng(latfin, lonfin)).zoom(map.getCameraPosition().zoom).build();
							
						}
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						 for (int i = 0; i < pointsLat.size() - 1; i++) {
							 LatLng src = new LatLng(Double.parseDouble(pointsLat.get(i)),Double.parseDouble(pointsLon.get(i)));
							 LatLng dest = new LatLng(Double.parseDouble(pointsLat.get(i+1)),Double.parseDouble(pointsLon.get(i+1)));
							 Polyline line = map.addPolyline(new PolylineOptions() //mMap is the Map Object
							 .add(new LatLng(src.latitude, src.longitude),
							 new LatLng(dest.latitude,dest.longitude))
							 .width(8).color(Color.BLUE).geodesic(true));
						  }
					 
						marker_taxi_destino = new MarkerOptions().position(new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude())).title("Mi destino");
						marker_taxi_destino.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_fin_rojo));
						map.addMarker(marker_taxi_destino);	
						
							String url = getDirectionsUrl(new LatLng(latfin,lonfin),new LatLng(InfoPoint.get(0).getDblLatitude(), InfoPoint.get(0).getDblLongitude()));					
							DownloadTask downloadTask = new DownloadTask();
							downloadTask.execute(url);
						// drawRouteBetweenTwoPositions(new LatLng(latini, lonini), new LatLng(latfin, lonfin));
					}else{
						
						map.clear();
						marker.position(new LatLng(latini,lonini));
						map.addMarker(marker);
						
						marker_taxi.position(new LatLng(latfin,lonfin));
						map.addMarker(marker_taxi);
						
						//.zoom(21);map.getCameraPosition().zoom
						CameraPosition cameraPosition;
						if(isFirstLocation){
							 cameraPosition = new CameraPosition.Builder().target(new LatLng(latfin, lonfin)).zoom(21).build();
							 isFirstLocation= false;
						}else{
							 cameraPosition = new CameraPosition.Builder().target(new LatLng(latfin, lonfin)).zoom(map.getCameraPosition().zoom).build();
							
						}
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						 for (int i = 0; i < pointsLat.size() - 1; i++) {
							 LatLng src = new LatLng(Double.parseDouble(pointsLat.get(i)),Double.parseDouble(pointsLon.get(i)));
							 LatLng dest = new LatLng(Double.parseDouble(pointsLat.get(i+1)),Double.parseDouble(pointsLon.get(i+1)));
							 Polyline line = map.addPolyline(new PolylineOptions() //mMap is the Map Object
							 .add(new LatLng(src.latitude, src.longitude),
							 new LatLng(dest.latitude,dest.longitude))
							 .width(8).color(Color.BLUE).geodesic(true));
						  }
						
					}
				

	
				//new Dialogos().Toast(getBaseContext(), "zazazazazazaa "+datosLat[i]+", "+tokens[1], Toast.LENGTH_LONG);
			}
		};
	
		
		@Override
		protected void onPause() {
			unregisterReceiver(onBroadcast);
			super.onPause();
		}

		@Override
		protected void onResume() {
			registerReceiver(onBroadcast, new IntentFilter("key"));
			super.onResume();
		}
		
		
		 public void clickEvent(View v) {
		        if (v.getId() == R.id.abs_layout_iv_menu) {
		            showPopup(v);
		        }

		       
		    }
		
		 public void showPopup(View v) {
			    PopupMenu popup = new PopupMenu(Mapa_tracking.this, v);
			    MenuInflater inflater = popup.getMenuInflater();
			    inflater.inflate(R.menu.popup, popup.getMenu());
			    popup.show();
			}
		
		 @Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				//Dialogues.Toast(getApplicationContext(), str, Toast.LENGTH_SHORT);
			}
		 
		 
		 private ArrayList<InfoPoint> parsePoints(String strResponse) {
		        ArrayList<InfoPoint> result=new ArrayList<InfoPoint>();
		        try {
		            JSONObject obj=new JSONObject(strResponse);
		            JSONArray array=obj.getJSONArray("results");
		            for(int i=0;i<array.length();i++)
		            {
		                InfoPoint point=new InfoPoint();
		                JSONObject item=array.getJSONObject(i);
		                ArrayList<HashMap<String, Object>> tblPoints=new ArrayList<HashMap<String,Object>>();
		                JSONArray jsonTblPoints=item.getJSONArray("address_components");
		                for(int j=0;j<jsonTblPoints.length();j++)
		                {
		                    JSONObject jsonTblPoint=jsonTblPoints.getJSONObject(j);
		                    HashMap<String, Object> tblPoint=new HashMap<String, Object>();
		                    Iterator<String> keys=jsonTblPoint.keys();
		                    while(keys.hasNext())
		                    {
		                        String key=(String) keys.next();
		                        if(tblPoint.get(key) instanceof JSONArray)
		                        {
		                            tblPoint.put(key, jsonTblPoint.getJSONArray(key));
		                        }
		                        tblPoint.put(key, jsonTblPoint.getString(key));
		                    }
		                    tblPoints.add(tblPoint);
		                }
		                point.setAddressFields(tblPoints);
		                point.setStrFormattedAddress(item.getString("formatted_address"));
		                JSONObject geoJson=item.getJSONObject("geometry");
		                JSONObject locJson=geoJson.getJSONObject("location");
		                point.setDblLatitude(Double.parseDouble(locJson.getString("lat")));
		                point.setDblLongitude(Double.parseDouble(locJson.getString("lng")));
		                result.add(point);
		            }
		        } catch (JSONException e) {
		            e.printStackTrace();
		        }

		        return result;
		    }
			
		 private String getDirectionsUrl(LatLng origin,LatLng dest){
				
				// Origin of route
				String str_origin = "origin="+origin.latitude+","+origin.longitude;
				
				// Destination of route
				String str_dest = "destination="+dest.latitude+","+dest.longitude;		
				// Sensor enabled
				String sensor = "sensor=false";			
							
				// Building the parameters to the web service
				String parameters = str_origin+"&"+str_dest+"&"+sensor;
							
				// Output format
				String output = "json";
				
				// Building the url to the web service
				String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
				
				
				return url;
			}
		 
		// Fetches data from url passed
			private class DownloadTask extends AsyncTask<String, Void, String>{			
						
				// Downloading data in non-ui thread
				@Override
				protected String doInBackground(String... url) {
						
					// For storing data from web service
					String data = "";
							
					try{
						// Fetching the data from web service
						data = downloadUrl(url[0]);
					}catch(Exception e){
						Log.d("Background Task",e.toString());
					}
					return data;		
				}
				
				// Executes in UI thread, after the execution of
				// doInBackground()
				@Override
				protected void onPostExecute(String result) {			
					super.onPostExecute(result);			
					
					ParserTask parserTask = new ParserTask();
					
					// Invokes the thread for parsing the JSON data
					parserTask.execute(result);
						
				}		
			}
			
			/** A method to download json data from url */
		    private String downloadUrl(String strUrl) throws IOException{
		        String data = "";
		        InputStream iStream = null;
		        HttpURLConnection urlConnection = null;
		        try{
		                URL url = new URL(strUrl);

		                // Creating an http connection to communicate with url 
		                urlConnection = (HttpURLConnection) url.openConnection();

		                // Connecting to url 
		                urlConnection.connect();

		                // Reading data from url 
		                iStream = urlConnection.getInputStream();

		                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

		                StringBuffer sb  = new StringBuffer();

		                String line = "";
		                while( ( line = br.readLine())  != null){
		                        sb.append(line);
		                }
		                
		                data = sb.toString();

		                br.close();

		        }catch(Exception e){
		                Log.d("Exception while downloading url", e.toString());
		        }finally{
		                iStream.close();
		                urlConnection.disconnect();
		        }
		        return data;
		     }

		    /** A class to parse the Google Places in JSON format */
		    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
		    	
		    	// Parsing the data in non-ui thread    	
				@Override
				protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
					
					JSONObject jObject;	
					List<List<HashMap<String, String>>> routes = null;			           
		            
		            try{
		            	jObject = new JSONObject(jsonData[0]);
		            	DirectionsJSONParser parser = new DirectionsJSONParser();
		            	
		            	// Starts parsing data
		            	routes = parser.parse(jObject);    
		            }catch(Exception e){
		            	e.printStackTrace();
		            }
		            return routes;
				}
				
				// Executes in UI thread, after the parsing process
				@Override
				protected void onPostExecute(List<List<HashMap<String, String>>> result) {
					ArrayList<LatLng> points = null;
					PolylineOptions lineOptions = null;
					MarkerOptions markerOptions = new MarkerOptions();
					
					// Traversing through all the routes
					for(int i=0;i<result.size();i++){
						points = new ArrayList<LatLng>();
						lineOptions = new PolylineOptions();
						
						// Fetching i-th route
						List<HashMap<String, String>> path = result.get(i);
						
						// Fetching all the points in i-th route
						for(int j=0;j<path.size();j++){
							HashMap<String,String> point = path.get(j);					
							
							double lat = Double.parseDouble(point.get("lat"));
							double lng = Double.parseDouble(point.get("lng"));
							LatLng position = new LatLng(lat, lng);	
							
							points.add(position);						
						}
						
						// Adding all the points in the route to LineOptions
						lineOptions.addAll(points);
						lineOptions.width(8);
						lineOptions.color(Color.RED);	
						
					}
					
					// Drawing polyline in the Google Map for the i-th route
					map.addPolyline(lineOptions);							
				}			
		    }   

}
