package com.example.appointmaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.internal.mi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.R;



public class MainActivity extends FragmentActivity implements
		OnMapLongClickListener, ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	private TextView mMessageView;  // location information (TextView on the top)
	private LatLng addAPositionLatLng;
	private static int appointmentsCounter = 0;
	private static Location myLocation;
    private Marker mMarker; // last created marker
	private int mId = 0;
    private String durationString; // get duration of the route from server in String


	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.example.appointmaps.R.layout.activity_main);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		mMessageView = (TextView) findViewById(com.example.appointmaps.R.id.message_text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpGoogleApiClientIfNeeded();
		mGoogleApiClient.connect();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(com.example.appointmaps.R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				mMap.setOnMapLongClickListener(this);

                // get current location and set a marker on it
				getMyLocation();
				// Get latitude of the current location
				double latitude = myLocation.getLatitude();

				// Get longitude of the current location
				double longitude = myLocation.getLongitude();

				// Create a LatLng object for the current location
				LatLng latLng = new LatLng(latitude, longitude);

				// Show the current location in Google Map
				mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

				// Zoom in the Google Map
				mMap.animateCamera(CameraUpdateFactory.zoomTo(15.5f));

				mMap.addMarker(new MarkerOptions().position(
						new LatLng(latitude, longitude)).title("You are here!"));

			}
		}
	}

	private void getMyLocation() {
		// LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Get the name of the best provider
		String provider = locationManager.getBestProvider(new Criteria(), true);

		// Get Current Location
		Location nowLocation = locationManager.getLastKnownLocation(provider);

		myLocation = nowLocation;
	}

    // set up google api client
	private void setUpGoogleApiClientIfNeeded() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
	}



	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		mMessageView.setText("Location = " + location);
	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}
/*
	public boolean showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
		return true;
	}

	public boolean showTitleDialog(View v) {
		TitleFragment titleFragment = new TitleFragment();
		titleFragment.show(getFragmentManager(), "titleSetter");
		return true;
	}
*/
	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
				.show();
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.example.appointmaps.R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == com.example.appointmaps.R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


    // communication with the setting activity
    // receive the param from the other activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case RESULT_OK:

                Bundle bundle = data.getExtras();
                String title =  bundle.getString("title");
                int hour = bundle.getInt("hour");
                int minute = bundle.getInt("minute");
                Appointments app = new Appointments(title, hour, minute);

                // update the message on marker
                ChangeMarker(title);
                // set notification
                SetNotification(hour, minute, title);

                break;
            default:
                break;
        }
    }

    // update the message on marker
    public void ChangeMarker(String title){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int durationTime = Integer.valueOf(durationString).intValue();
        mMarker.setTitle(title);
        mMarker.setSnippet("Time: "+ calendar.get(Calendar.HOUR)+ ":" + calendar.get(Calendar.MINUTE)
                +"  Duration: "+ durationTime/60+"min");
    }


    // set notification
    public void SetNotification(int hour, int minute, String title){

        int durationTime = Integer.valueOf(durationString).intValue();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),hour,minute);
        calendar.add(Calendar.SECOND, 0 - durationTime);

        Toast.makeText(MainActivity.this, title, Toast.LENGTH_LONG).show();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(com.example.appointmaps.R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText("You should set off at "+ calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE));
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }


    // start of interaction
    @Override
	public void onMapLongClick(LatLng point) {

        // call the setting activity
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, 0);
           addAPositionLatLng = point;


        // set a marker on map (in order to get the click position)
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(addAPositionLatLng)
                .draggable(true)
                .title("Appointment #" + appointmentsCounter)
                .snippet(
                        "Location: " + addAPositionLatLng.latitude + " ,"
                                + addAPositionLatLng.longitude));


		getMyLocation();
        // construct the url of http request
		String url = "http://54.191.229.191/index.php?" + "origin="
				+ myLocation.getLatitude() + "%2c" + myLocation.getLongitude()
				+ "&destination=" + addAPositionLatLng.latitude + "%2c"
				+ addAPositionLatLng.longitude + "&sensor=true";

		Log.i("Ming", url);

        // do http request by GET
		durationString = doGet(url);
		// Toast.makeText(MainActivity.this, reString, Toast.LENGTH_LONG).show();
		appointmentsCounter++;

	}

    // http request
	public String doGet(String url) {
		String result = "";

        // http client - a toolkit in Android SDK
		HttpClient httpclient = new DefaultHttpClient();

        // GET connection
		HttpGet httpget = new HttpGet(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {

            // do request and get response
			result = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("Ming", "Fail!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("Ming", result);
		return result;
	}
}