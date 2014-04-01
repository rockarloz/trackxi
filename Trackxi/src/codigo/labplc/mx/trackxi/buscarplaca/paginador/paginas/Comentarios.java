package codigo.labplc.mx.trackxi.buscarplaca.paginador.paginas;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import codigo.labplc.mx.trackxi.R;
import codigo.labplc.mx.trackxi.buscarplaca.bean.AutoBean;
import codigo.labplc.mx.trackxi.dialogos.Dialogos;
import codigo.labplc.mx.trackxi.facebook.FacebookLogin;
import codigo.labplc.mx.trackxi.facebook.FacebookLogin.OnGetFriendsFacebookListener;
import codigo.labplc.mx.trackxi.facebook.FacebookLogin.OnLoginFacebookListener;
import codigo.labplc.mx.trackxi.fonts.fonts;
import codigo.labplc.mx.trackxi.network.NetworkUtils;

import com.facebook.Response;
import com.facebook.model.GraphUser;

public class Comentarios extends View {

	private View view;
	private View view_row;
	private Activity context;
	private LinearLayout container;
	private AutoBean autoBean;
	LinearLayout adeudos_ll_contenedor_fotos;
	private FacebookLogin facebookLogin;
	
	
	public Comentarios(Activity context) {
		super(context);
		this.context = context;
	}

	public Comentarios(Activity context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public Comentarios(Activity context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}
	
	public void init(AutoBean autoBean){
		this.autoBean=autoBean;
		init();
	}
	

	public void init() {
		facebookLogin = new FacebookLogin(context);

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.activity_adeudos, null);
		
		TextView adeudos_titulo_main = (TextView)view.findViewById(R.id.adeudos_titulo_main);
		adeudos_titulo_main.setText(getResources().getString(R.string.titulo_tres_comentarios));
		adeudos_titulo_main.setTypeface(new fonts(context).getTypeFace(fonts.FLAG_MAMEY));
		adeudos_titulo_main.setTextColor(new fonts(context).getColorTypeFace(fonts.FLAG_GRIS_OBSCURO));
		container=(LinearLayout)view.findViewById(R.id.adeudos_ll_contenedor);
		
		adeudos_ll_contenedor_fotos = (LinearLayout)view.findViewById(R.id.adeudos_ll_contenedor_fotos);
		TextView adeudos_tv_ningun_amigos = (TextView)view.findViewById(R.id.adeudos_tv_ningun_amigos);
		adeudos_tv_ningun_amigos.setTypeface(new fonts(context).getTypeFace(fonts.FLAG_MAMEY));
		adeudos_tv_ningun_amigos.setTextColor(new fonts(context).getColorTypeFace(fonts.FLAG_GRIS_OBSCURO));
		
		
		
		TextView adeudos_titulo_tv_amigos=(TextView)view.findViewById(R.id.adeudos_titulo_tv_amigos);
		adeudos_titulo_tv_amigos.setTypeface(new fonts(context).getTypeFace(fonts.FLAG_MAMEY));
		adeudos_titulo_tv_amigos.setTextColor(new fonts(context).getColorTypeFace(fonts.FLAG_GRIS_OBSCURO));
		
		for(int i = 0;i< autoBean.getArrayComentarioBean().size();i++){
		llenarComentario(autoBean.getArrayComentarioBean().get(i).getComentario(),autoBean.getArrayComentarioBean().get(i).getCalificacion(),i);
		}
		
		if(facebookLogin.isSession()){
			facebookLogin.loginFacebook();
			facebookLogin.setOnLoginFacebookListener(new OnLoginFacebookListener() {
				@Override
				public void onLoginFacebook(boolean status) {
					loginFacebook(status);
				}
			});
		}
		

	}

	
	public void llenarComentario( String concepto, float valor,int i) {
	final	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view_row = inflater.inflate(R.layout.comentarios_row, null);
		
	final	TextView comentarios_row_tv_descripcion = (TextView)view_row.findViewById(R.id.comentarios_row_tv_descripcion);
	comentarios_row_tv_descripcion.setTypeface(new fonts(context).getTypeFace(fonts.FLAG_MAMEY));
	comentarios_row_tv_descripcion.setTextColor(new fonts(context).getColorTypeFace(fonts.FLAG_GRIS_OBSCURO));
	comentarios_row_tv_descripcion.setText(concepto);

		
	final ImageView rating1_comentarios = (ImageView)view_row.findViewById(R.id.rating1_comentarios);
	rating1_comentarios.setTag(i+"img1");
	final ImageView rating2_comentarios = (ImageView)view_row.findViewById(R.id.rating2_comentarios);
	rating1_comentarios.setTag(i+"img2");
	final ImageView rating3_comentarios = (ImageView)view_row.findViewById(R.id.rating3_comentarios);
	rating1_comentarios.setTag(i+"img3");
	final ImageView rating4_comentarios = (ImageView)view_row.findViewById(R.id.rating4_comentarios);
	rating1_comentarios.setTag(i+"img4");
	final ImageView rating5_comentarios = (ImageView)view_row.findViewById(R.id.rating5_comentarios);
	rating1_comentarios.setTag(i+"img5");
		
		
		if(valor==0.5){
		rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star3));
		}
		if(valor==1.0){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
		}
		if(valor==1.5){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star3));
			}
		if(valor==2.0){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			}
		if(valor==2.5){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star3));
			}
		if(valor==3.0){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			}
		if(valor==3.5){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating4_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star3));
			}
		if(valor==4.0){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating4_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			}
		if(valor==4.5){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating4_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating5_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star3));
			}
		if(valor==5.0){
			rating1_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating2_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating3_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating4_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			rating5_comentarios.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_star2));
			}
		container.addView(view_row,i);
		
	}

	/**
	 * Login in to Facebook
	 * 
	 * @param status
	 */
	public void loginFacebook(boolean status) {
		if(status) {
		Toast.makeText(context, "Welcome!! :D", Toast.LENGTH_SHORT).show();
			
		//	ImageView ivUserImageProfile = (ImageView) findViewById(R.id.iv_UserImageProfile);
		//	facebookLogin.loadImageProfileToImageView(facebookLogin.getUserId(), ivUserImageProfile);
			
	//		TextView tvUserName = (TextView) findViewById(R.id.tv_UserName);
		//	TextView tvUserId = (TextView) findViewById(R.id.tv_UserId);
			
	//		tvUserName.setText(facebookLogin.getUserName());
	//		tvUserId.setText("ID: " + facebookLogin.getUserId());

			
			getListOfFriends(facebookLogin.getUserId());
			
		} else {
			Toast.makeText(context, "Algo fall� al conectar con facebook", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Get a friends list from an user id
	 * 
	 * @param userId
	 */
	public void getListOfFriends(String userId) {
		facebookLogin.getListOfFriends();
		facebookLogin.setOnGetFriendsFacebookListener(new OnGetFriendsFacebookListener() {
			@Override
			public void onGetFriendsFacebook(List<GraphUser> users, Response response) {
				int i=-1;
				JSONObject json = new JSONObject();
				for (GraphUser user : users) {
					try {
						json.put("id",user.getId());
					} catch (JSONException e) {
						e.printStackTrace();
					}
						i+=1;
						if(i==0){
							adeudos_ll_contenedor_fotos.removeAllViews();
						}
						View viewFriend = addUserFriend(user,i);
						if(viewFriend != null) {
							adeudos_ll_contenedor_fotos.addView(viewFriend);
					}
				}
				//JSONObject jsonObjRecv = NetworkUtils.SendHttpPost(URL, json);
			}
		});
	}
	
	/**
	 * Add a user friend view to the layout
	 * 
	 * @param user
	 * @return a view
	 */
	public View addUserFriend(final GraphUser user, int id) {
		View viewFriend = context.getLayoutInflater().inflate(R.layout.listitem, null);
		
	//	TextView tvFriendName = (TextView) viewFriend.findViewById(R.id.tv_FriendName);
	//	tvFriendName.setText("User: " + user.getName());
		
	//	TextView tvFriendId = (TextView) viewFriend.findViewById(R.id.tv_FriendId);
	//	tvFriendId.setText("Id: " + user.getId());
		
		ImageView ivFriendImageProfile = (ImageView) viewFriend.findViewById(R.id.iv_FriendImageProfile);
		ivFriendImageProfile.setTag(id);
		ivFriendImageProfile.setId(id);
		ivFriendImageProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Dialogos.Toast(context, user.getName(), Toast.LENGTH_LONG);
			}
		});
		facebookLogin.loadImageProfileToImageView(user.getId(), ivFriendImageProfile);
		
		
		return viewFriend;
	}
	
	
	
	
	public View getView() {
		return view;
	}

}
