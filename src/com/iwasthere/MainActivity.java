package com.iwasthere;


import java.util.ArrayList;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final Context context = this;
    MySQLiteHelper locsData;
    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private TextView txtgps;
    private ListView locationList;
	private Location location = new Location("dummyprovider");
	private Location currLoc = new Location("anotherdummyprovider"); 
	private ImageView markLocation;
	final ArrayList<String> ret = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locsData = new MySQLiteHelper(this);
		txtgps = (TextView)findViewById(R.id.gps);
		locationList = (ListView)findViewById(R.id.locsList);
		markLocation = (ImageView)findViewById(R.id.mark);
		markLocation.setEnabled(false);
		markLocation.setVisibility(View.GONE);
		location.setLatitude(0);
		location.setLongitude(0);
		Cursor cursor = getLocations();
	    showLocs(cursor);
	    locationList.setEnabled(false);
		locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos,
					long id) {
				String  itemValue    = (String) locationList.getItemAtPosition(pos);
				SQLiteDatabase db = locsData.getReadableDatabase();
				Cursor cursor = db.query(MySQLiteHelper.TABLE, null, MySQLiteHelper.NAME + " = '" + itemValue + "'" , null, null, null, null);
				cursor.moveToFirst();
				String longitude = cursor.getString(2);
			    String latitude = cursor.getString(3);
				//Toast.makeText(getApplicationContext(), longitude + latitude, Toast.LENGTH_SHORT).show();
			    
			    findLocation(latitude,longitude);
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onResume() {
	     super.onResume();
	     setupSensors();
	     mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, mlocListener);
	     
	}
	
	public void markLoc(View view) {
		location.set(currLoc);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.prompt, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set prompts.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);
		final EditText input = (EditText) promptView.findViewById(R.id.userInput);
		// setup a dialog window
	
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                        // get user input and set it to result
	                		addLocation(input.getText().toString());
	                    }
	                })
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
	}
	
	private void addLocation(String locName){
		SQLiteDatabase db = locsData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.NAME, locName);
		values.put(MySQLiteHelper.lonVALUE, location.getLongitude());
		values.put(MySQLiteHelper.latVALUE, location.getLatitude());
		db.insert(MySQLiteHelper.TABLE, null, values);
		Toast.makeText(getApplicationContext(), locName + " added", Toast.LENGTH_SHORT).show();
		Cursor cursor = getLocations();
	    showLocs(cursor);
	}
	
	private Cursor getLocations() {
		SQLiteDatabase db = locsData.getReadableDatabase();
		Cursor cursor = db.query(MySQLiteHelper.TABLE, null, null, null, null, null, null);
		startManagingCursor(cursor);
		//Toast.makeText(getApplicationContext(),cursor.toString(), Toast.LENGTH_SHORT).show();
		return cursor;
	}
	
	private void showLocs(Cursor cursor) {
		
	    //StringBuilder ret = new StringBuilder("Saved Locations:\n\n");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1,
	            ret);
	    locationList.setAdapter(adapter);
	    adapter.clear();
	    while (cursor.moveToNext()) {
	      //long id = cursor.getLong(0);
	      String title = cursor.getString(1);
	      
	      ret.add(title);
	    }
	}
	
	public void clearLocations(View view){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
 
			// set title
			alertDialogBuilder.setTitle("Your Title");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Sure?! All locations will be forgotten")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						SQLiteDatabase db = locsData.getReadableDatabase();
						db.delete(MySQLiteHelper.TABLE, null, null);
						Cursor cursor = getLocations();
					    showLocs(cursor);
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		
	}
	
	private void findLocation(String latitude, String longitude){
		Intent intent = new Intent(this, ShowLoc.class);
		intent.putExtra("locx", Double.valueOf(longitude));
		intent.putExtra("locy", Double.valueOf(latitude));
	    startActivity(intent);
	}
	
	
	private void setupSensors() {
		 mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		 mlocListener = new LocationListener(){
	  	 	public void onLocationChanged(Location loc) {
	  	 		
		  		//if location of device changes
	  	 		txtgps.setText("Current Location:\n" + loc.getLatitude() + "\n" + loc.getLongitude());
	  	 		currLoc.set(loc);
	  	 		markLocation.setVisibility(View.VISIBLE);
	  	 		markLocation.setEnabled(true);
	  	 		locationList.setEnabled(true);
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
}
