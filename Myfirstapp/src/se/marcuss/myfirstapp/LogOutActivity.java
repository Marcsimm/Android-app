package se.marcuss.myfirstapp;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class LogOutActivity extends MainMenuActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 startService(new Intent(this, background.class));         
		new LogOutTask().execute();
	}

	class LogOutTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpPost post = new HttpPost("http://" + ip + ":9000/Out");

				DefaultHttpClient client = new DefaultHttpClient();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

				String response = client.execute(post,
						new BasicResponseHandler());

				cookies = client.getCookieStore().getCookies();

				return response;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), "You logged out",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
			 startActivity(intent);

		}
	}
}
