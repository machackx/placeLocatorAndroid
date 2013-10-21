package com.cymobile.placelocator.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.model.CYPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceMapActivity extends FragmentActivity {
	
	private final static String TAG = "PlaceMapActivity";
	private GoogleMap googleMap;
	private CYPlace selectedPlace; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_map);
		selectedPlace = (CYPlace) getIntent().getSerializableExtra("CYPlace");
		Log.w(TAG, "Place Address:" + selectedPlace.getPlaceAddress());
		
		double latitude = Double.valueOf(selectedPlace.getLatitude());
		double longitude = Double.valueOf(selectedPlace.getLongitude());
		String placeName = selectedPlace.getPlaceName();
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		
		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}
		else {
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			// Get Map object
			googleMap = fm.getMap();
			googleMap.setMyLocationEnabled(true);
			
			if(latitude != 0 && longitude != 0) {
				LatLng placeLatLng = new LatLng(latitude, longitude);
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLatLng, 10);
				googleMap.animateCamera(cameraUpdate);
				googleMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName).snippet(selectedPlace.getPlaceAddress()));
			}
			
			
			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker arg0) {
					// TODO Auto-generated method stub
					Log.w(TAG, arg0.getTitle());
					//Intent myIntent = new Intent(PlaceMapActivity.this, PlaceInfoEditor.class);
					Intent myIntent = new Intent(PlaceMapActivity.this, PlaceInfoActivity.class);
					myIntent.putExtra("CYPlace", selectedPlace);
          //startActivityForResult(i, RESULT_SETTINGS);					
					startActivity(myIntent);	
				}
			});	
		}	
	}
}
