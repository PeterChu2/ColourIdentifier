package Chu.Peter.colouridentifier;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	public void SetRawSQL(String rawsql)
	{
		this.rawsql=rawsql;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		dbHelper=new ExternalDbOpenHelper(c,"rgbvaluesdb");
		db=dbHelper.getDb();
		cursor=db.rawQuery(rawsql, null);
	}
	
}
