package com.cymobile.placelocator.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.R.integer;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.model.CYPlace;

public class PlaceInfoActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private final static String TAG = "PlaceInfoActivity";
	private final static String ERROR_REPORTING_URL_PROD = "http://gaminggeo.com/errorReporting.php?place_id=%@";
	private CYPlace selectedPlace;
	private int selectedValue = 0;
	
	@SuppressWarnings("deprecation")
	@Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      addPreferencesFromResource(R.xml.preference);
      //SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
      selectedPlace = (CYPlace) getIntent().getExtras().getSerializable("CYPlace");
      Preference placeNamePreference = (Preference) findPreference("prefPlaceName");
      placeNamePreference.setSummary(selectedPlace.getPlaceName());
      
      Preference placeAddressPreference = (Preference) findPreference("prefPlaceAddress");
      placeAddressPreference.setSummary(selectedPlace.getPlaceAddress());
      
      Preference placePhonePreference = (Preference) findPreference("prefPlacePhone");
      placePhonePreference.setSummary(selectedPlace.getPhoneNumber());
      		
      Preference placeMachinesPreference = (Preference) findPreference("prefPlaceMachines");
      placeMachinesPreference.setSummary(selectedPlace.getNumberGames());
      
      Preference publicNotesPreference = (Preference) findPreference("prefPublicNotes");
      publicNotesPreference.setSummary(selectedPlace.getPublicComments());
      
      Preference ratingPreference = (Preference) findPreference("prefRating");
      // set OnPref change listener
      OnPreferenceChangeListener changeListener = new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					// TODO Auto-generated method stub
				  selectedValue = Integer.valueOf(newValue.toString());
					return false;
				}
			};
      
      ratingPreference.setOnPreferenceChangeListener(changeListener);
      ratingPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
					if (selectedValue > 0) {
						preference.setEnabled(false);
						Toast.makeText(PlaceInfoActivity.this, "Sorry, you have already rated this place.", Toast.LENGTH_LONG ).show();
					} 
					return false;
				}
			});
      
      Preference errorReportPreference = (Preference) findPreference("prefFeedBack");
      errorReportPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
					sendPlaceInfoError();
					return false;
				}
			});
      
  }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	private void sendPlaceInfoError() {
		AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				try{
					String request = ERROR_REPORTING_URL_PROD;
					request = request.replace("%@", selectedPlace.getPlaceId());
					JSONObject json = getJSONFromURL(request);
					String result = json.getString("result");
					if(result.length() > 0){
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(PlaceInfoActivity.this, "Thank you.  The administrator has been notified to review this entry.", Toast.LENGTH_LONG).show();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
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
