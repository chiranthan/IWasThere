package com.iwasthere;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class MySQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "locations.db";
	private static final int DATABASE_VERSION = 1;

	// Table name
	public static final String TABLE = "locations";

	// Columns
	public static final String NAME = "name";
	public static final String lonVALUE = "longitude";
	public static final String latVALUE = "latitude";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + "( " + BaseColumns._ID
				+ " integer primary key autoincrement, " + NAME + " text not null, "
				+ lonVALUE + " text not null, " + latVALUE + " text not null);";
		//Log.d("EventsData", "onCreate: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion)
			return;

		String sql = null;
		if (oldVersion == 1) 
			sql = "alter table " + TABLE + " add note text;";
		if (oldVersion == 2)
			sql = "";

		//Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
	}

}