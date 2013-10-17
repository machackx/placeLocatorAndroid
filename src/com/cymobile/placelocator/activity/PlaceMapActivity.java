package com.cymobile.placelocator.activity;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.model.CYPlace;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class PlaceMapActivity extends FragmentActivity {
	
	private final static String TAG = "PlaceMapActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_map);
		CYPlace selectedPlace = (CYPlace) getIntent().getSerializableExtra("CYPlace");
		Log.w(TAG, "Place Address:" + selectedPlace.getPlaceAddress());
	}
	

}
