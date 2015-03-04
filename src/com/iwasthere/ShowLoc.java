package com.iwasthere;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLoc extends Activity{
	private Camera mCamera;
    private CameraPreview mPreview;
	private FrameLayout preview;
	private DrawcompView cv;
	private DrawacclView av;
	
	
	private Double locx = 1.00;
	private Double locy = 1.00;
	
	//All Sensor code:
	private List<Sensor> sensors;
	private SensorManager mSensorManager;
    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private Location loc2 = new Location("dummyprovider");
    private TextView txtgps;
    private TextView txtcomp;
    private float angle = 0;
    private float distance = 0;
    private float azimuth;
    private float st_look;
    private float actx, acty, accx = 50, accy = 50;
    private Display display;
    private int width = 1;
    private int height = 1;
    private Point size = new Point();
	private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @SuppressLint("NewApi")
		@Override
	    public void onSensorChanged(SensorEvent event) {
	        Sensor sensor = event.sensor;
	        int tx = 0;// treshold
	        int ty = 0;
	        
	        //benchmark values: Nexus 4
	        float snx = (width/22); //sensitivity
	        float sny = (height/4);
	        int wd = 96;
	        int hd = 148;
	        
	        accx = (width/2);
	        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
		        if(event.values[0] > tx || event.values[0] < -tx){
		        	 actx = -(event.values[0]);
		        	 accx = actx*snx + (width/2);
		        }
		        if(event.values[1] > ty || event.values[1] < -ty){
		        	 acty = (event.values[1]);
		        	 accy = acty*sny - (height/hd)*sny;
		        }
		    	av.setY(accy); 
	         }
	    	 if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
	         	azimuth = Math.round(event.values[0]);
	         	cv.setRotation(-azimuth +angle);
	         	st_look = (360 - (azimuth - angle));
	         	av.setX(accx + (st_look*(width/wd)));
	         	
	         	if(st_look > 360)
	         		st_look = st_look % 360;
	         	if(st_look < 45 && st_look > -45 && acty > 8 ){
	         		cv.setAlpha(0);
	         		av.setAlpha(100);
	         		txtgps.setX(accx + (st_look*(width/wd)));
	         		txtgps.setY(accy);
	         		
	         	}
	         	else{
	         		cv.setAlpha(100);
	         		av.setAlpha(0);
	         		txtgps.setX(0);
	         		txtgps.setY(0);
	         	}
	         	//use below code for testing sensor data
	         	//txtcomp.setText("actx:"+ actx + "\nacty:" + acty + "\nscreen size: " + width + "x" + height + "\nNorth at: " + Float.toString(azimuth) + "\nPlace at: " + Float.toString(st_look));
        	}
	    }
	
	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }

	};

	private void setupSensors() {
        
     mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
     sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
	 mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	 mlocListener = new LocationListener(){
  	 	public void onLocationChanged(Location loc) {
	  		//if location of device changes
		    //locx = 0.00;
		    //locy = 0.00;
	    //calculate bearing and distance
	    //loc2.setLatitude(locy);
	    //loc2.setLongitude(locx);
	    angle = loc.bearingTo(loc2);
	    distance = loc2.distanceTo(loc);
	  	int dist = Math.round(distance);
        txtgps.setText(dist + " m \n");
    if(loc.getAccuracy() > 5)
    	txtcomp.setText("GPS Accuracy is very low\nAccuracy:" + loc.getAccuracy() + " m");
    else
    	txtcomp.setText("");
   	}
  	 	@Override
   	    public void onProviderDisabled(String provider) {
   	        Toast.makeText(getApplicationContext(), "Disable",Toast.LENGTH_SHORT).show();
   	    }

   	    @Override
   	    public void onProviderEnabled(String provider) {
   	        Toast.makeText(getApplicationContext(), "Enable",Toast.LENGTH_SHORT).show();
   	    }

   	    @Override
   	    public void onStatusChanged(String provider, int status, Bundle extras) {}

     };
    }

   	protected void onResume() {
	     super.onResume();
	     setupSensors();
	     mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	     mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
	     mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, mlocListener);
	     mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    
	    return c; // returns null if camera is unavailable
	}
	
	protected void onPause() {
	     super.onPause();

	     Log.i("LoggerActivity", "onPause called");

	     // Unregister sensor listeners
	     for (Sensor s : sensors) {
	         mSensorManager.unregisterListener(mSensorEventListener, s);
	     }


	   Log.i("LoggerActivity", "onPause finished.");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		preview = new FrameLayout(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		preview.setLayoutParams(lp);
		setContentView(preview);
        mCamera = getCameraInstance();
        
        Bundle extras = getIntent().getExtras();
        locx = extras.getDouble("locx");
        locy = extras.getDouble("locy");
        loc2.setLongitude(locx);
        loc2.setLatitude(locy);
        
        
        display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        
        //screen dimensions
        width = size.x;
        height = size.y;
              
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        av = new DrawacclView(this,accx,accy);
        cv = new DrawcompView(this,height,width);
        txtgps = new TextView(getBaseContext());
        txtcomp = new TextView(getBaseContext());
        txtgps.setTextSize(17);
        txtcomp.setTextSize(10);
        preview.addView(av);
        preview.addView(cv);
        preview.addView(txtgps);
        preview.addView(txtcomp);
        txtgps.setText("Waiting for information from GPS...\nCurrently showing North");
        txtcomp.setGravity(Gravity.TOP);
        txtcomp.setGravity(Gravity.RIGHT);
        txtcomp.setTextColor(Color.RED);
        txtcomp.setText("Showing to " + loc2.getLatitude() +"\n"+ loc2.getLongitude());
        //displaying building name(for checking)
		}
		protected void onDestroy() {
		    //option 1: callback before or ...
			super.onDestroy();
			//option 2: callback after super.onDestroy();
			mlocManager.removeUpdates(mlocListener);
			mlocManager = null;
			mCamera.stopPreview();
			mCamera.release();
	        mCamera = null;
		}
		public void back()
		 {
			 onDestroy();
			 Intent intent = new Intent(this, MainActivity.class);
			 startActivity(intent);
		 }

}
