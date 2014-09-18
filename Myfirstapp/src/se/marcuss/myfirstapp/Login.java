package se.marcuss.myfirstapp;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends MainMenuActivity { 
	boolean loggedIn = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_ins);
		 startService(new Intent(this, background.class));
		Button button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				LogInTask logInServer = new LogInTask(ip);

				EditText email = (EditText) findViewById(R.id.email);
				EditText passwd = (EditText) findViewById(R.id.password);

				if(email.getText().toString().equals("")){
					email.setError("Email must be filed");
				}

				if(passwd.getText().toString().equals("")){
					passwd.setError("Password must be filed");
				}

				if(email.getText().toString().equals("")&&(passwd.getText().toString().equals(""))){
					email.setError("Email must filed");
					passwd.setError("Password must be filed");
				}

				logInServer.setEmail(email.getText().toString());
				logInServer.setPassword(passwd.getText().toString());

				logInServer.execute();
			}
		});
	}

	class LogInTask extends AsyncTask<Void, Void, Boolean>{
		private String ip;
		private String email;
		private String password;



		public void setEmail(String email) {
			this.email = email;
		}



		public void setPassword(String password) {
			this.password = password;
		}

		public LogInTask(String ip){
			this.ip = ip;
		}


		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				HttpPost post = new HttpPost("http://" + ip + ":9000/");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", email));
				nameValuePairs.add(new BasicNameValuePair("passwd", password));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				DefaultHttpClient client =  new DefaultHttpClient();
				client.execute(post, new BasicResponseHandler());
				cookies = client.getCookieStore().getCookies();

				for(Cookie cookie : cookies){
					if(cookie.getName().equals("PLAY_FLASH")){
						return false;


					}
				}


				return true;
			}	catch(Exception e){
				throw new RuntimeException(e);

			}	 

		}

		@Override
		protected void onPostExecute(Boolean success) {
			if(success == true){
				Toast.makeText(getApplicationContext(), "Welcome!",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
				startActivity(intent);

			} else{
				Toast.makeText(getApplicationContext(), "Failed",
						Toast.LENGTH_LONG).show();
			}

			//			for(Cookie cookie : cookies){
			//				if(cookie.getName().equals("PLAY_FLASH")){
			//					Toast.makeText(getApplicationContext(), "yes",Toast.LENGTH_LONG).show();
			//					
			//				}
			//			}

		}
	}
}