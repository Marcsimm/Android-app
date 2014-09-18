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
import android.widget.Toast;


public class ListofProducts extends MainMenuActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_of_products);
		 startService(new Intent(this, background.class));

		new GetProducts().execute();

	}

	class AddCart extends AsyncTask<Void, Void, Boolean>{
		private String ip;
		private String proquantity;
		private String productId;
		private String useractive;

		public String getUseractive() {
			return useractive;
		}
		public void setUseractive(String useractive) {
			this.useractive = useractive;
		}
		public String getProquantity() {
			return proquantity;
		}
		public void setProquantity(String proquantity) {
			this.proquantity = proquantity;
		}
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}

		public AddCart(String ip){
			this.ip = ip;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				HttpPost post = new HttpPost("http://" + ip + ":9000/addtobasket");
				DefaultHttpClient client = new DefaultHttpClient();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						client.getCookieStore().addCookie(cookie);
					}
				}

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("quantity", proquantity));
				nameValuePairs.add(new BasicNameValuePair("product-id", productId));
				nameValuePairs.add(new BasicNameValuePair("userActive", useractive));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


				client.execute(post, new BasicResponseHandler());
				cookies = client.getCookieStore().getCookies();

				return true;
			} catch(Exception e){
				Log.e("Error adding product", e.getMessage());
				return false;
			}	
		}
		@Override
		protected void onPostExecute(Boolean success) {		
			if(success == true){

				Toast.makeText(getApplicationContext(), "Product added to your basket",
						Toast.LENGTH_LONG).show();

			} else{
				Toast.makeText(getApplicationContext(), "You must be logged in to buy something",
						Toast.LENGTH_LONG).show();

			}

		}

	}


	class GetProducts extends AsyncTask<Void, Void, JSONArray>{
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				String myResponse = new DefaultHttpClient()
				.execute(
						new HttpGet("http://" + ip + ":9000/api/products"),   
						new BasicResponseHandler()
						);

				return new JSONArray(myResponse);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			ListView listView = (ListView) findViewById(R.id.products_list);
			listView.setAdapter(new ProductAdapter(result));

		}
	}

	class ProductAdapter extends BaseAdapter {
		private JSONArray products;

		public ProductAdapter(JSONArray products) {
			this.products = products;
		}

		@Override
		public int getCount() {
			return products.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return products.getJSONObject(index);
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

			View productListItem = getLayoutInflater().inflate(R.layout.products_list_item, parent, false);

			TextView productName = (TextView) productListItem
					.findViewById(R.id.product_name);

			TextView productDescription = (TextView) productListItem
					.findViewById(R.id.product_description);

			TextView productCost = (TextView) productListItem
					.findViewById(R.id.product_cost);
			
			TextView category = (TextView) productListItem
					.findViewById(R.id.product_category);
			

			final TextView productId = (TextView) productListItem
					.findViewById(R.id.id);

			Button button = (Button) productListItem
					.findViewById(R.id.button1);


			try {

				final JSONObject product = products.getJSONObject(index);
			
				productName.setText(product.getString("productName"));
				productDescription.setText(product.getString("description"));
				productCost.setText((product.getString("cost")+" "+"kr"));
				productId.setText((product.getString("id")));
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						AddCart addcart = new AddCart(ip);


						Spinner numbers = (Spinner) findViewById(R.id.numbers);
						String proquantity = numbers.getSelectedItem().toString();

						addcart.setProquantity(proquantity);
						addcart.setProductId(productId.getText().toString());
						addcart.execute();
					}
				});

			} catch (JSONException e){
				throw new RuntimeException(e);
			}

			return productListItem;	
		}

	}

}


