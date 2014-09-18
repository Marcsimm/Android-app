package se.marcuss.myfirstapp;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.marcuss.myfirstapp.R;
import android.content.Intent;
//import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListofCategory extends MainMenuActivity {

	//k??r xml filer
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_of_categorires);
		 startService(new Intent(this, background.class));
		new GetCategoriesTask().execute();
	}



	class GetCategoriesTask extends AsyncTask<Void, Void, JSONArray> {
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
			ListView listView = (ListView) findViewById(R.id.list_of_categories);
			listView.setAdapter(new CategoriesListAdapter(result));
		}
	}

	class CategoriesListAdapter extends BaseAdapter {
		private JSONArray categories;

		public CategoriesListAdapter(JSONArray items) {
			this.categories = items;
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
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			View categoriesListItem = getLayoutInflater().inflate(
					R.layout.category_list_item, parent, false);

			TextView categoryName = (TextView) categoriesListItem
					.findViewById(R.id.category_name);

			//			TextView staff = (TextView) categoriesListItem
			//					.findViewById(R.id.staffid);

			try {
				JSONObject category = categories.getJSONObject(index);

				categoryName.setText(category.getString("categoryname"));

				//				staff.setText("Staff with id: "+category.getInt("id"));

			} catch (JSONException e) {
				throw new RuntimeException(e);
			}

			return categoriesListItem;
		}


	}


}
