package com.cymobile.placelocator.activity;

import com.cymobile.placelocator.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity{
	private final int SPLASH_DISPLAY_LENGTH = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent mainIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
				SplashScreenActivity.this.startActivity(mainIntent);
				SplashScreenActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
	}

}
