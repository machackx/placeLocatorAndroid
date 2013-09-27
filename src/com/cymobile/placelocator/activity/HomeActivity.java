package com.cymobile.placelocator.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.cymobile.placelocator.R;

public class HomeActivity extends Activity {
	
	private final static String TAG = "HomeActivity";
	private final static String _getPlaceLatitudeURL = "http://maps.googleapis.com/maps/api/geocode/json?address={address},IL, US&sensor=false";
	private final static String _getPlaceListURL = "http://gaminggeo.com/getPlace.php?longitude={longitude}&latitude={latitude}";
	private double _longitude;
	private double _latitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
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
						
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
		};
		task.execute();
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
					JSONObject json = getJSONFromURL(request);
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
