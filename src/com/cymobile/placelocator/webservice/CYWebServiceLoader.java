package com.cymobile.placelocator.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class CYWebServiceLoader extends AsyncTaskLoader<CYWebServiceLoader.RestResponse>{
	
		private static final String TAG = CYWebServiceLoader.class.getName();
		private static final long STALE_DELTA = 600000;
		
	  public static class RestResponse {
	  	private String data;
	  	private int returnCode;
	  	
	  	public RestResponse (){
	  		
	  	}
	  	
	  	public String getData() {
				return data;
			}

			public void setData(String data) {
				this.data = data;
			}

			public int getReturnCode() {
				return returnCode;
			}

			public void setReturnCode(int returnCode) {
				this.returnCode = returnCode;
			}

			public RestResponse(String data, int returnCode) {
				this.data = data;
				this.returnCode = returnCode;
			}
	  }
		
		private Uri kAction;
		private RestResponse kRestResponse;
		private CYRequestMethod kRequestMethod;
		private List<BasicNameValuePair> kParams;
		
		private long kLastLoad;
		
		public CYWebServiceLoader (Context context) {
			super(context);
		}
		
		public CYWebServiceLoader(Context context, Uri pAction, CYRequestMethod pVerb) {
			super(context);
			this.kAction = pAction;
			this.kRequestMethod = pVerb;
		}
		
		public CYWebServiceLoader(Context context, Uri pAction, CYRequestMethod pVerb, List<BasicNameValuePair> pParams) {
			super(context);
			this.kAction = pAction;
			this.kRequestMethod = pVerb;
			this.kParams = pParams;
		}

		@Override
		public RestResponse loadInBackground() {
			try {
				if (kAction == null) {
					Log.e(TAG, "Action not defined");
					return new RestResponse();
				}
				HttpRequestBase request = null;
				
				switch (kRequestMethod) {
				case GET:{
					request = new HttpGet();
					attachUriWithQuery(request, kAction, kParams);
					break;
				}
				case DELETE:{
					request = new HttpDelete();
					attachUriWithQuery(request, kAction, kParams);
					break;
				}
				case POST: {
					request = new HttpPost();
					request.setURI(new URI(kAction.toString()));
					HttpPost postRequest = (HttpPost) request;
				}
				case PUT: {
					request = new HttpPut();
					request.setURI(new URI(kAction.toString()));
					HttpPut putRequest = (HttpPut) request;		
					// TODO in case of changes in WS
					break;
				}
			}

				if (request != null) {
					// We add the headers
					request.addHeader("Accept", "*/*");
					request.addHeader("Accept", "application/json");
					request.addHeader("Accept-Encoding", "gzip, deflate");
	                request.addHeader("Content-type", "application/json; charset=UTF-8");
	                request.addHeader("Accept-Language", "fr");
	                
					//HttpClient client = new DefaultHttpClient();
	                HttpClient client = getNewHttpClient();
	                
					Log.d(TAG, "Executing request: " + verbToString(kRequestMethod) + ": "
							+ request.getURI());

					HttpResponse response = client.execute(request);

					HttpEntity responseEntity = response.getEntity();
					StatusLine responseStatus = response.getStatusLine();
					int statusCode = responseStatus != null ? responseStatus
							.getStatusCode() : 0;
					
					// We create the return response with the entity (JSON or XML depending on the case) and the return code
					RestResponse restResponse = new RestResponse(responseEntity != null ? EntityUtils.toString(responseEntity, HTTP.UTF_8)
									: null, statusCode);
					
					return restResponse;
				}

				return new RestResponse();
			} catch (URISyntaxException e) {
				Log.e(TAG, "URI syntax was incorrect. " + verbToString(kRequestMethod)
						+ " : " + kAction.toString(), e);
				return new RestResponse();
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG,
						"A UrlEncodedFormEntity was created with an unsupported encoding.",
						e);
				return new RestResponse();
			} catch (ClientProtocolException e) {
				Log.e(TAG, "There was a problem when sending the request.", e);
				return new RestResponse();
			} catch (IOException e) {
				Log.e(TAG, "There was a problem when sending the request.", e);
				return new RestResponse();
			}
		}
		
		private void attachUriWithQuery(HttpRequestBase request, Uri uri, List<BasicNameValuePair> params) {
			try {
				if (params == null) {
					request.setURI(new URI(uri.toString()));
				} else {
						Uri.Builder uriBuilder = uri.buildUpon();
				
						for (BasicNameValuePair param : params) {
							try {
								uriBuilder.appendQueryParameter(param.getName(),
										 URLEncoder.encode(param.getValue(), "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				
						uri = uriBuilder.build();
						request.setURI(new URI(uri.toString()));
				}
				
			} catch (URISyntaxException e) {
				Log.e(TAG, "URI syntax was incorrect: " + uri.toString());
			}
		}
		
		public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new CYSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
		
		private static String verbToString(CYRequestMethod verb) {
			switch (verb) {
				case GET:
					return "GET";
		
				case POST:
					return "POST";
		
				case PUT:
					return "PUT";
		
				case DELETE:
					return "DELETE";
				}

			return "";
		}
}
