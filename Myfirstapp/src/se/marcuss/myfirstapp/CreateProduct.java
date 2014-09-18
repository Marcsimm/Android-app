package se.marcuss.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class CreateProduct extends MainMenuActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_products);
		 startService(new Intent(this, background.class));
		new GetCategoriesFromServer(ip).execute();
		
		Button button = (Button) findViewById(R.id.create_product);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ListView category = (ListView) findViewById(R.id.categorylist);
				List<View> rawCheckBoxes = category.getTouchables();
				List<CheckBox> checkBoxes = new ArrayList<>();
				
				for(int i = 0; i < rawCheckBoxes.size(); i++) {
					if(rawCheckBoxes.get(i) instanceof CheckBox) {
						CheckBox cb = (CheckBox) rawCheckBoxes.get(i);
						if(cb.isChecked()) {
							checkBoxes.add(cb);
						}
					}
				}
				
				
							
				EditText productName = (EditText) findViewById(R.id.product_text);
				EditText description = (EditText) findViewById(R.id.description_text);
				EditText cost = (EditText) findViewById(R.id.cost_text);
				EditText rrp = (EditText) findViewById(R.id.rrp_text);
				boolean ok = true;
				
				if(productName.getText().toString().equals("")){
					productName.setError("Productname must be filed");
					ok = false;
				}

				if(description.getText().toString().equals("")){
					 description.setError("Description must be filed");
					 ok = false;
				}
				
				if(cost.getText().toString().equals("")){
					 cost.setError("Cost must be filed");
					 ok = false;
				}
				
				if(rrp.getText().toString().equals("")){
					 rrp.setError("Rrp must be filed");
					 ok = false;
				}
				
				 
				
				CreateProductOnServer createProductOnServer = new CreateProductOnServer(ip);
				
				createProductOnServer.setProductName(productName.getText().toString());
				createProductOnServer.setDescription(description.getText().toString());
				createProductOnServer.setCost(cost.getText().toString());
				createProductOnServer.setRrp(rrp.getText().toString());
				
				List<String> categories = new ArrayList<>();
				
				for(CheckBox cb : checkBoxes) {
					categories.add(Integer.toString(cb.getId()));
				}
				
				createProductOnServer.setCategories(categories);
				
				if(ok){
				createProductOnServer.execute();
				}
			}
				
		});
	}
	
	class GetCategoriesFromServer extends AsyncTask<Void, Void, JSONArray> {
		private String ip;
		
		public GetCategoriesFromServer(String ip) {
			this.ip = ip;
		}
		
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/api/categories"),
						new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		protected void onPostExecute(JSONArray result) {
			ListView spinner = (ListView) findViewById(R.id.categorylist);
			spinner.setAdapter(new CategoryAdapter(result));
		}
	}
	
	class CategoryAdapter extends BaseAdapter implements SpinnerAdapter {
		private JSONArray categories;
		
		public CategoryAdapter(JSONArray categories) {
			this.categories = categories;
		}
		
		@Override
		public int getCount() {
			return categories.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return categories.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			try {
				return categories.getJSONObject(index).getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			View categoriesListItem = getLayoutInflater().inflate(
					R.layout.categories_list_item, parent, false);
			
			CheckBox categoryName = (CheckBox) categoriesListItem
					.findViewById(R.id.checkbox_category);
			
			try {
				JSONObject category = categories.getJSONObject(index);
				categoryName.setId(Integer.parseInt(category.getString("id")));
				categoryName.setText(category.getString("categoryname"));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			
			return categoriesListItem;
		}
	}
	
	class CreateProductOnServer extends AsyncTask<Void, Void, Boolean>{
		private String ip;
		private String Productname;
		private String Description;
		private String Cost;
		private String Rrp;
		private List<String> categories;
		
		public CreateProductOnServer(String ip) {
			this.ip = ip;
		}
		
		public void setProductName(String Productname) {
			this.Productname = Productname;
		}
		
		public void setDescription(String Description) {
			this.Description = Description;
		}
		
		public void setCost(String Cost) {
			this.Cost = Cost;
		}
		
		public void setRrp(String Rrp) {
			this.Rrp = Rrp;
		}
		
		public void setCategories(List<String> categoryid) {
			this.categories = categoryid;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {

				HttpPost postProduct = new HttpPost("http://" + ip + ":9000/createproduct");
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("Productname", Productname));
				nameValuePairs.add(new BasicNameValuePair("Description", Description));
				nameValuePairs.add(new BasicNameValuePair("Cost", Cost));
				nameValuePairs.add(new BasicNameValuePair("Rrp", Rrp));
				
				for (String c : categories) {

		             nameValuePairs.add(new BasicNameValuePair("categoryid", c));

		        }
				
				postProduct.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				
				new DefaultHttpClient()
				.execute(postProduct, new BasicResponseHandler());
				
				return true;
			} catch (Exception e) {
				Log.e("Error creating product", e.getMessage());
				
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean success) {
			if(success = true) {
				Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Big problem!", Toast.LENGTH_LONG).show();
			}
		}
	}
}
