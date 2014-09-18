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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CreateCategory extends MainMenuActivity {
	@Override
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_categories);
		 startService(new Intent(this, background.class));
		new GetStaffFromServer().execute();

		Button button = (Button) findViewById(R.id.create_category);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				CreateCategoryOnServer createCategoryOnServer = new CreateCategoryOnServer(ip);
				
				boolean allok = true;
				
				EditText categoryname = (EditText) findViewById(R.id.category_names);
				Spinner staff = (Spinner) findViewById(R.id.staff_id);
				
				
				if(categoryname.getText().toString().equals("")){
					categoryname.setError("Categoryname must be filed");
					allok = false;
				}
				
				String staffId = Integer.toString((int) staff.getSelectedItemId());
			
				createCategoryOnServer.setCategoryname(categoryname.getText().toString());
				createCategoryOnServer.setStaffid(staffId);

				if(allok){
				createCategoryOnServer.execute();
				}
			}
		});
	}

	class CreateCategoryOnServer extends AsyncTask<Void, Void, Boolean>{
		private String ip;
		private String categoryname;
		private String Staffid;


		public String getCategoryname() {
			return categoryname;
		}
		public void setCategoryname(String categoryname) {
			this.categoryname = categoryname;
		}

		public String getStaffid() {
			return Staffid;
		}
		public void setStaffid(String Staffid) {
			this.Staffid = Staffid;
		}


		public CreateCategoryOnServer(String ip){
			this.ip = ip;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				HttpPost Post = new HttpPost("http://" + ip + ":9000/createcategory");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("Categoryname",categoryname));
				nameValuePairs.add(new BasicNameValuePair("staffid", Staffid));
				Post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				new DefaultHttpClient().execute(Post, new BasicResponseHandler());
				return true;
			} catch(Exception e){
				Log.e("Error creating category", e.getMessage());
				return false;
			}	
		}
		@Override
		protected void onPostExecute(Boolean success) {		
			if(success == true){
				Toast.makeText(getApplicationContext(), "Success!",
						Toast.LENGTH_LONG).show();
			} else{
				Toast.makeText(getApplicationContext(), "Failed",
						Toast.LENGTH_LONG).show();
			}

		}
	}
	class GetStaffFromServer extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				String response = new DefaultHttpClient().execute(new HttpGet(
						"http://" + ip + ":9000/api/staffs"),
						new BasicResponseHandler());
				return new JSONArray(response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Spinner spinner = (Spinner) findViewById(R.id.staff_id);
			spinner.setAdapter(new StaffAdapter(result));
		}
	}

	class StaffAdapter extends BaseAdapter implements SpinnerAdapter {
		private JSONArray staffs;

		public StaffAdapter(JSONArray staffs) {
			this.staffs= staffs;
		}

		@Override
		public int getCount() {
			return staffs.length();
		}

		@Override
		public Object getItem(int index) {
			try {
				return staffs.getJSONObject(index);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long getItemId(int index) {
			try {
				return staffs.getJSONObject(index).getInt("id");
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			

			try {
				JSONObject category = staffs.getJSONObject(index);
				textView.setText(category.getString("firstName")+" "+category.getString("surName"));
				
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			textView.setTextSize(18);
			textView.setTextColor(Color.parseColor("#000000"));
			return textView;
		}
	}
	
}
