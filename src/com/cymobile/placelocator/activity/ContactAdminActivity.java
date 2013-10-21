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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.cymobile.placelocator.R;

public class ContactAdminActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private final static String ADD_ADVERTISER_URL_PROD = "http://gaminggeo.com/addAdvertiser.php?name={name}&phone={phone}&email={email}&message={message}";
	private final static String TAG = "ContactAdminActivity";
	private EditTextPreference userNamePreference;
	private EditTextPreference userPhonePreference;
	private EditTextPreference userEmailPreference;
	private EditTextPreference userMessage;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    addPreferencesFromResource(R.xml.contact_admin_preference);
    userNamePreference = (EditTextPreference) findPreference("prefUserName");
    userPhonePreference = (EditTextPreference) findPreference("prefUserPhone");
    userEmailPreference =  (EditTextPreference) findPreference("prefUserEmail");
    userMessage =  (EditTextPreference) findPreference("prefUserMessage");
    
    Preference sendMessagePreference = (Preference) findPreference("prefSendMessage");
    sendMessagePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				sendMessage();
				return false;
			}
		});
    /*
    Preference userNamePreference = (Preference) findPreference("prefPlaceName");
    userNamePreference.getSummary();
    
    Preference errorReportPreference = (Preference) findPreference("prefFeedBack");
    errorReportPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				sendPlaceInfoError();
				return false;
			}
		});
		*/
    
}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		// TODO Auto-generated method stub
		if (key.equals("prefUserName")) {
			userNamePreference.setSummary(sharedPreferences.getString(key, ""));
		}
		Log.w(TAG, key);
	}
	
	private void sendMessage() {
		AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				try{
					String request = ADD_ADVERTISER_URL_PROD;
					request = request.replace("{name}", userNamePreference.getSummary());
					request = request.replace("{phone}", userPhonePreference.getSummary());
					request = request.replace("{email}", userEmailPreference.getSummary());
					request = request.replace("{message}", userMessage.getSummary());
					JSONObject json = getJSONFromURL(request);
					String result = json.getString("result");
					if(result.length() > 0){
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(ContactAdminActivity.this, "Thank you, your message is sent.", Toast.LENGTH_LONG).show();
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
