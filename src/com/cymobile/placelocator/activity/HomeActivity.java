package com.cymobile.placelocator.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.adaptor.PlaceListAdaptor;
import com.cymobile.placelocator.model.CYPlace;

public class HomeActivity extends Activity {
	
	private final static String TAG = "HomeActivity";
	private final static String _getPlaceLatitudeURL = "http://maps.googleapis.com/maps/api/geocode/json?address={address},IL, US&sensor=false";
	private final static String _getPlaceListURL = "http://gaminggeo.com/getPlace.php?longitude={longitude}&latitude={latitude}";
	private double _longitude;
	private double _latitude;
	
	private ListView listView;
	Context mContext;
	private ArrayList<CYPlace> placeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		listView = (ListView) findViewById(R.id.placeList); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				CYPlace selectedPlace = placeList.get(position);
				Log.w(TAG, selectedPlace.getPlaceName());
				
				Intent myIntent = new Intent(HomeActivity.this, PlaceMapActivity.class);
				myIntent.putExtra("CYPlace", placeList.get(position));
				startActivity(myIntent);	
			}
		});
		
		getPlaceLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	private void getPlaceList(){
		AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try{
					String request = _getPlaceListURL;
					request = request.replace("{longitude}", Double.toString(_longitude));
					request = request.replace("{latitude}", Double.toString(_latitude));
					request = request.replaceAll(" ", "%20");
					JSONObject json = getJSONFromURL(request);
					int resultNumber = json.getInt("result");
					if(resultNumber > 0){
						JSONArray jsonArray = json.getJSONArray("places");
						placeList = new ArrayList<CYPlace>();
						for(int i = 0 ; i<resultNumber; i++)
						{
							JSONObject anItem = (JSONObject) jsonArray.get(i);
							JSONObject aPlace = anItem.getJSONObject("place");
							
							CYPlace place = new CYPlace();
							place.setPlaceId(aPlace.getString("place_id"));
							place.setCityName(aPlace.getString("city_name"));
							place.setPhoneNumber(aPlace.getString("phone_number"));
							place.setCloseTime(aPlace.getString("close_time"));
							place.setZipCode(aPlace.getString("zip_code"));
							place.setPlaceAddress(aPlace.getString("place_address"));
							place.setReportNumbers(aPlace.getString("report_numbers"));
							place.setPlaceName(aPlace.getString("place_name"));
							place.setStateName(aPlace.getString("state_name"));
							place.setPublicComments(aPlace.getString("public_comments"));
							place.setCountryName(aPlace.getString("country_name"));
							place.setNumberGames(aPlace.getString("number_games"));
							place.setDistance(aPlace.getString("distance"));
							place.setRatingNumbers(aPlace.getString("rating_numbers"));
							place.setStartTime(aPlace.getString("start_time"));
							place.setLongitude(aPlace.getString("longitude"));
							place.setLatitude(aPlace.getString("latitude"));
							place.setPersonalNotes(aPlace.getString("personal_notes"));
							place.setRating(aPlace.getString("rating"));
							place.setPaidCustomer(aPlace.getString("paid_customer"));
							
							placeList.add(place);
						}
						
						
						
						runOnUiThread(new Runnable() {
							public void run() {
								setTitle("We have found " + Integer.toString(placeList.size()) + " results");
								mContext = getApplicationContext();
								LayoutInflater layoutInflator = LayoutInflater.from(mContext);
								PlaceListAdaptor listAdaptor =  new PlaceListAdaptor(mContext, layoutInflator, placeList);
								listView.setAdapter(listAdaptor);
							}
						});
						
						
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								setTitle("Sorry, no place around you.");
							}
						});
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
		};
		task.execute();
	}
	
	private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("placeSample.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
	
	private void getPlaceLocation(){
		AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try{
					String request = _getPlaceLatitudeURL;
					request = request.replace("{address}", "freeburg");
					request = request.replaceAll(" ", "%20");
					/*
					 * get JSON from test string
					 */
					//JSONObject json = getJSONFromURL(request);
					JSONObject json =  new JSONObject(loadJSONFromAsset());
					
					String status = json.getString("status");
					if(status.equals("OK")){
						JSONArray resultArray = json.getJSONArray("results");
						JSONObject geometryResult = resultArray.getJSONObject(0).getJSONObject("geometry");
						_latitude = geometryResult.getJSONObject("location").getDouble("lat");
						_longitude = geometryResult.getJSONObject("location").getDouble("lng");
						getPlaceList();
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				/*
				if(result){
					return;
				}
				*/
			}
			
		};
		task.execute();
	}
	
	private JSONObject getJSONFromURL(String url){
		InputStream inputStream = null;
		String result = "";
		JSONObject jArray = null;
		HttpClient httpClient = new DefaultHttpClient();
		
		try{
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			inputStream = entity.getContent();
		} catch(Exception e) {
			Log.e(TAG, "Error http connection, detail" + e.getMessage());
			httpClient = new DefaultHttpClient();
		}
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
			inputStream.close();
			result = sb.toString();
		}catch (Exception e) {
			Log.e(TAG, "error converting result");	
		}
		
		Log.w(TAG, "Response String:" + result);
		
		try{
			if(null != result && 0 < result.length()){
				jArray = new JSONObject(result);
			}
		}catch (Exception e){
			Log.e(TAG, "Error parsing data");
		}
		return jArray;
	}

}
