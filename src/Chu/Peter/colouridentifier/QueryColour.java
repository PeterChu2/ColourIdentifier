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
	public QueryColour(Context c)
	{
		this.c=c;
	}
	public void setRawSQL(int r, int g, int b)
	{
		this.rawsql=String.format("SELECT text FROM RGBValues WHERE red=%d and green=%d and blue=%d", r, g, b);
		Log.d("TESTSQL", rawsql);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(db==null)
		{
			dbHelper=new ExternalDbOpenHelper(c,"RGBValues");
			db=dbHelper.getDb();
		}
		cursor=db.rawQuery(rawsql, null);
//		Log.d("COLOUR", cursor.getString(0));
	}
	
}
