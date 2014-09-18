package se.marcuss.myfirstapp;

import java.util.List;

import org.apache.http.cookie.Cookie;


//import se.marcuss.myfirstapp.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

//import android.widget.Toast;

public class MainMenuActivity extends Activity {

	static protected final String ip = "85.229.187.213";
	static protected List<Cookie> cookies;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_menu);
		 startService(new Intent(this, background.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.create_products).setVisible(false);
		menu.findItem(R.id.create_categories).setVisible(false);
		menu.findItem(R.id.log_out).setVisible(false);
		menu.findItem(R.id.list_of_cart).setVisible(false);

		boolean loggedIn = false;
		if(cookies != null){
			for (Cookie cookie : cookies) {
				if ("PLAY_SESSION".equals(cookie.getName())) {
					
					loggedIn = true;
				}
			}
			if (loggedIn) {
				MenuItem item = menu.findItem(R.id.log_ins);
				item.setVisible(false);	
				menu.findItem(R.id.create_products).setVisible(true);
				menu.findItem(R.id.create_categories).setVisible(true);
				menu.findItem(R.id.log_out).setVisible(true);
				menu.findItem(R.id.list_of_cart).setVisible(true);
			}
		}
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {



		if(item.getItemId() == R.id.log_ins){
			startActivity(new Intent(this, Login.class));

			return true;
		}

		if(item.getItemId() == R.id.log_out){
			startActivity(new Intent(this, LogOutActivity.class));

			return true;
		}

		if(item.getItemId() == R.id.create_products){
			startActivity(new Intent(this, CreateProduct.class));

			return true;
		}

		if(item.getItemId() == R.id.list_of_products){
			startActivity(new Intent(this, ListofProducts.class));

			return true;
		}

		if(item.getItemId() == R.id.create_categories){
			startActivity(new Intent(this,CreateCategory.class));

			return true;
		}

		if(item.getItemId() == R.id.list_of_categories){
			startActivity(new Intent(this, ListofCategory.class));

			return true;
		}
		
		if(item.getItemId() == R.id.list_of_cart){
			startActivity(new Intent(this, ListofShooppingbasket.class));

			return true;
		}
		return false;
	}
}
	
