package Chu.Peter.colouridentifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector {

	// database name
	private static final String DATABASE_NAME = "ColourLog";
	private SQLiteDatabase database; // database object
	private DatabaseOpenHelper databaseOpenHelper; // database helper

	// public constructor for DatabaseConnector
	public DatabaseConnector(Context context) 
	{
		// create a new DatabaseOpenHelper
		databaseOpenHelper = 
				new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
	} // end DatabaseConnector constructor

	// open the database connection
	public void open() throws SQLException 
	{
		// create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
	} // end method open

	// close the database connection
	public void close() 
	{
		if (database != null)
			database.close(); // close the database connection
	} // end method close

	// inserts a new colour in the database
	public void insertRecord(String colourName, String hex, int colour)
	{
		ContentValues newRecord = new ContentValues();
		newRecord.put("colourName", colourName);
		newRecord.put("hex", hex);
		newRecord.put("colour", colour);
		database.insert("ColourLog", null, newRecord);
	} // end method insertRecord

	// return a Cursor with all contact information in the database
	public Cursor getAllRecords() 
	{
		return database.query("ColourLog", null, 
				null, null, null, null, "_id DESC");
	} // end method getAllRecords

	// get a Cursor containing all information about the record specified
	// by the given id
	public Cursor getOneRecord(long id) 
	{
		return database.query(
				"ColourLog", new String[]{"colourName","hex","colour"}, "_id=" + id, null, null, null, null);
	} // end method getOneRecord

	public void deleteRecords()
	{
		database.delete("colourLog",null,null);
	}

	// delete the contact specified by the given String name
	public void deleteRecord(long id)
	{
		open(); // open the database
		database.delete("ColourLog", "_id=" + id, null);
		close(); // close the database
	} // end method deleteContact

	private class DatabaseOpenHelper extends SQLiteOpenHelper 
	{
		// public constructor
		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) 
		{
			super(context, name, factory, version);
		} // end DatabaseOpenHelper constructor

		// creates the contacts table when the database is created
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			// query to create a new table named contacts
			String createQuery = "CREATE TABLE ColourLog" +
					"(_id integer primary key autoincrement," +
					"colourName TEXT, hex TEXT,"+" colour integer);";
			db.execSQL(createQuery); // execute the query
		} // end method onCreate

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
	} // end class DatabaseOpenHelper
} // end class DatabaseConnector

