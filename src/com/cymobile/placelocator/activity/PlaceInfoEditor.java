package com.cymobile.placelocator.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cymobile.placelocator.R;

public class PlaceInfoEditor extends Activity  {
	
	private static final int RESULT_SETTINGS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_info);
		showUserSettings();
	}
	
	@Override
  public boolean onCreateOptionsMenu(Menu menu) {
      //getMenuInflater().inflate(R.menu.settings, menu);
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {

      case R.id.menu_settings:
          Intent i = new Intent(this, PlaceInfoActivity.class);
          startActivityForResult(i, RESULT_SETTINGS);
          break;

      }

      return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      switch (requestCode) {
      case RESULT_SETTINGS:
          showUserSettings();
          break;

      }

  }

  private void showUserSettings() {
      SharedPreferences sharedPrefs = PreferenceManager
              .getDefaultSharedPreferences(this);

      StringBuilder builder = new StringBuilder();

      builder.append("\n Username: "
              + sharedPrefs.getString("prefUsername", "NULL"));

      builder.append("\n Send report:"
              + sharedPrefs.getBoolean("prefSendReport", false));

      builder.append("\n Sync Frequency: "
              + sharedPrefs.getString("prefSyncFrequency", "NULL"));

      TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);

      settingsTextView.setText(builder.toString());
  }
}
