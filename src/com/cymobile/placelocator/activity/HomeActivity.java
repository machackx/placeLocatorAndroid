package com.cymobile.placelocator.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.adaptor.PlaceListAdaptor;
import com.cymobile.placelocator.model.CYPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class HomeActivity extends Activity  implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{
	
	private final static String TAG = "HomeActivity";
	private final static String _getPlaceLatitudeURL = "http://maps.googleapis.com/maps/api/geocode/json?address={address},IL, US&sensor=false";
	private final static String _getPlaceListURL = "http://gaminggeo.com/getPlace.php?longitude={longitude}&latitude={latitude}";
	private double _longitude;
	private double _latitude;
	private String placeName;
	private static final int CONTACT_ADMIN = 2;
	private PullToRefreshListView listView;
	Context mContext;
	private ArrayList<CYPlace> placeList;
	private ProgressBar progressBar;
	private LocationClient mLocationClient = null;
	private AlertDialog.Builder builder;
	private AlertDialog alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		/*
		// Set location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	  locationListener = new MyLocationListener();
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
		*/
		mLocationClient = new LocationClient(HomeActivity.this, this, this);
		
		// Set progress bar
		progressBar= (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		/*
		progressBar = new android.widget.ProgressBar(
                HomeActivity.this,
                null,
                android.R.attr.progressBarStyle);
*/
		progressBar.getIndeterminateDrawable().setColorFilter(0xff888888, android.graphics.PorterDuff.Mode.MULTIPLY);
		
		// set dialog
		builder = new AlertDialog.Builder(HomeActivity.this);
		
		builder.setMessage("Please enter a zip code or your city name:");
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		builder.setView(input);
		
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				progressBar.setVisibility(View.VISIBLE);
				placeName = input.getText().toString();
				Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
				List<Address> placeList = new ArrayList<Address>();
  				try{
  					placeList = geocoder.getFromLocationName(placeName + "IL, US", 10);
  				}catch (Exception e) {
  					e.printStackTrace();
  				}
				
  				if (placeList.size() > 0) {
  					Address placeAddress = placeList.get(0);
  					_latitude = placeAddress.getLatitude();
  					_longitude = placeAddress.getLongitude();
  					progressBar.setVisibility(View.VISIBLE);
  					getPlaceList();
  				} else {
  					Toast.makeText(HomeActivity.this, "No places founded.", Toast.LENGTH_LONG).show();
  				}
  				alert.dismiss();
				
			  }
			});
		alert = builder.create();
		alert.show();
		
		listView = (PullToRefreshListView) findViewById(R.id.placeList); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				CYPlace selectedPlace = placeList.get(position - 1);
				Log.w(TAG, selectedPlace.getPlaceName());
				
				Intent myIntent = new Intent(HomeActivity.this, PlaceMapActivity.class);
				myIntent.putExtra("CYPlace", placeList.get(position - 1));
				startActivity(myIntent);	
			}
		});
		
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new GetDataTask().execute();
			}
			
		});
		
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.disconnect();
		super.onStop();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent i;
		switch (item.getItemId()) {

      case R.id.menu_use_current_location:
      	progressBar.setVisibility(View.VISIBLE);
      	Location myCurrentLocation = mLocationClient.getLastLocation();
      	_latitude = myCurrentLocation.getLatitude();
  			_longitude = myCurrentLocation.getLongitude();
  			getPlaceList();
      	/*
          // Use GPS to get Geo coordinates
    	  if(displayGpsStatus()) {
    		    
        	  
    	  } else {
    		  	Toast.makeText(HomeActivity.this, "Your GPS is OFF", Toast.LENGTH_LONG).show();
    	  }
    	  */
          break;
      case R.id.menu_contact_admin:
      	 i = new Intent(this, ContactAdminActivity.class);
         startActivityForResult(i, CONTACT_ADMIN);
         break;

    }

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
								progressBar.setVisibility(View.GONE);
							}
						});
						
						
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								setTitle("Sorry, no place around you.");
								mContext = getApplicationContext();
								LayoutInflater layoutInflator = LayoutInflater.from(mContext);
								PlaceListAdaptor listAdaptor =  new PlaceListAdaptor(mContext, layoutInflator, new ArrayList<CYPlace>());
								listView.setAdapter(listAdaptor);
								progressBar.setVisibility(View.GONE);
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
					request = request.replace("{address}", placeName);
					request = request.replaceAll(" ", "%20");
					/*
					 * get JSON from test string
					 */
					JSONObject json = getJSONFromURL(request);
					//JSONObject json =  new JSONObject(loadJSONFromAsset());
					
					String status = json.getString("status");
					if(status.equals("OK")){
						JSONArray resultArray = json.getJSONArray("results");
						JSONObject geometryResult = resultArray.getJSONObject(0).getJSONObject("geometry");
						_latitude = geometryResult.getJSONObject("location").getDouble("lat");
						_longitude = geometryResult.getJSONObject("location").getDouble("lng");
						//Refresh List View
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
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private Boolean displayGpsStatus() {
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			alert.show();
			listView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

}
