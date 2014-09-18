package se.marcuss.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.marcuss.myfirstapp.ListofProducts.AddCart;
import se.marcuss.myfirstapp.ListofShooppingbasket.GetBasketsTask.CreateOrderOnServer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ListofShooppingbasket extends MainMenuActivity {

	// k??r xml filer
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_of_basket);
		startService(new Intent(this, background.class));
		
		new GetBasketsTask().execute();

	}

	class GetBasketsTask extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

				String response = client.execute(new HttpGet("http://" + ip
						+ ":9000/api/basket"), new BasicResponseHandler());

				cookies = client.getCookieStore().getCookies();

				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		class CreateOrderOnServer extends AsyncTask<Void, Void, Boolean>{
			private String ip;
			private String productname;
			private String proquantity;
			private String useractive;
			private String deleteall;


			public String getProductname() {
				return productname;
			}


			public void setProductname(String productname) {
				this.productname = productname;
			}


			public String getProquantity() {
				return proquantity;
			}


			public void setProquantity(String proquantity) {
				this.proquantity = proquantity;
			}


			public String getUseractive() {
				return useractive;
			}


			public void setUseractive(String useractive) {
				this.useractive = useractive;
			}


			public String getDeleteall() {
				return deleteall;
			}


			public void setDeleteall(String deleteall) {
				this.deleteall = deleteall;
			}

			public CreateOrderOnServer(String ip){
				this.ip = ip;
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				try{
					HttpPost Post = new HttpPost("http://" + ip + ":9000/order");
					DefaultHttpClient client = new DefaultHttpClient();
					
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							client.getCookieStore().addCookie(cookie);
						}
					}

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("deleteall", deleteall));
					nameValuePairs.add(new BasicNameValuePair("userActive", useractive));
					nameValuePairs.add(new BasicNameValuePair("quantity", proquantity));
					
					
					Post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					client.execute(Post, new BasicResponseHandler());
					cookies = client.getCookieStore().getCookies();
					
					return true;
				} catch(Exception e){
					Log.e("Error basket order", e.getMessage());
					return false;
				}	
			}

		}

		@Override
		protected void onPostExecute(JSONArray result) {
			ListView listView = (ListView) findViewById(R.id.list_of_baskets);
			listView.setAdapter(new BasketListAdapter(result));

		}
	}

	class BasketListAdapter extends BaseAdapter {
		private JSONArray baskets;

		public BasketListAdapter(JSONArray baskets) {
			this.baskets = baskets;
		}

		@Override
		public int getCount() {
			return baskets.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return baskets.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			View productsListItem = getLayoutInflater().inflate(
					R.layout.basket_list_item, parent, false);

			final TextView productbasket = (TextView) productsListItem
					.findViewById(R.id.product_name_basket);

			TextView descriptionbasket = (TextView) productsListItem
					.findViewById(R.id.description_basket);

			TextView costbasket = (TextView) productsListItem
					.findViewById(R.id.cost_basket);

			final TextView quantitybasket = (TextView) productsListItem
					.findViewById(R.id.quantity_basket);

			Button button = (Button) productsListItem
					.findViewById(R.id.orderbutton);



			try {
				JSONObject basket = baskets.getJSONObject(index);

				productbasket.setText(basket.getJSONObject("product").getString("productName"));
				descriptionbasket.setText(basket.getJSONObject("product").getString("description"));
				quantitybasket.setText(basket.getString("quantity"));
				costbasket.setText(basket.getJSONObject("product").getString("cost"));

//				button.setOnClickListener(new View.OnClickListener() {

//					@Override
//					public void onClick(View view) {
//						CreateOrderOnServer order  = new CreateOrderOnServer(ip);
//
//					
//						order.setProductname(productbasket.getText().toString());
//						order.setProquantity(quantitybasket.getText().toString());
//						
//						order.execute();
//					}
//				});




			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return productsListItem;
		}

	}

}
