package Chu.Peter.colouridentifier;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QueryColour implements Runnable{
	Context c;
	SQLiteDatabase db;
	ExternalDbOpenHelper dbHelper;
	Cursor cursor;
	String rawsql;
	String colourText="";
	
	public QueryColour(Context c)
	{
		this.c=c;
	}
	public void setRawSQL(int r, int g, int b)
	{
		//round values to the nearest 10, as the database only supports these values
		r = round10(r);
		g = round10(g);
		b = round10(b);
		this.rawsql=String.format("SELECT text FROM RGBValues WHERE red=%d and green=%d and blue=%d", r, g, b);
		Log.d("TESTSQL", rawsql);
	}
	@Override
	public void run() {
		//only run this if block during the first run
		if(db==null)
		{
			dbHelper=new ExternalDbOpenHelper(c,"RGBValues");
			db=dbHelper.getDb();
		}
		cursor=db.rawQuery(rawsql, null);
		if (cursor.moveToFirst()) {
			try
			{
				colourText = cursor.getString(0);
			}
			catch(Exception e)
			{
				Log.d("ERROR", "Error in returning text from cursor");
			}
		}
	}
	//rounding function to the nearest 10
	private static int round10(int num)
	{
		//ensure that value does not round to 260, as the max value is 256 for RGB
		if(num >= 255)
		{
			return 250;
		}
		return ((num + 5)/10)*10;
	}
	public String getText()
	{
		return colourText;
	}
	public void createRawSQL(byte[] data, int width, int height, int x, int y)
	{
		Log.d("DATALENGTH", Integer.toString(data.length));
		Log.d("WIDTH", Integer.toString(width));
		Log.d("HEIGHT", Integer.toString(height));
		this.setRawSQL((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
	}
}
