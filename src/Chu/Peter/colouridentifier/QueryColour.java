package Chu.Peter.colouridentifier;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

public class QueryColour implements Runnable{
	private Context c;
	private SQLiteDatabase db;
	private ExternalDbOpenHelper dbHelper;
	private Cursor cursor;
	private String rawsql;
	private String colourText="";
	private int rgb[];
	private boolean first=true;
	private boolean isRunning;
	private int sleepTime=2000;
	private ColourBox colourBox;

	public void setSleepTime(int sleepTime)
	{
		this.sleepTime=sleepTime;
	}
	
	public QueryColour(Context c, ColourBox colourBox)
	{
		this.colourBox=colourBox;
		this.c=c;
		isRunning=true;
	}

	@Override
	public void run() {
		dbHelper=new ExternalDbOpenHelper(c,"RGBValues");
		db=dbHelper.getDb();
		while(isRunning==true)
		{
			if(rawsql != null){
				cursor=db.rawQuery(rawsql, null);
				if (cursor.moveToFirst()) {
					try
					{
						colourText = cursor.getString(0);
						colourBox.postInvalidate();
					}
					catch(Exception e)
					{
						Log.d("ERROR", "Error in returning text from cursor");
					}
				}
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setRawSQL(int r, int g, int b)
	{
		//round values to the nearest 10, as the database only supports these values
		r = round10(r);
		g = round10(g);
		b = round10(b);
		this.rawsql=String.format("SELECT text FROM RGBValues WHERE red=%d and green=%d and blue=%d", r, g, b);
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
	public void createRawSQL(byte[] data, int width, int height, int index)
	{
		if (first==true)
		{rgb = new int[width*height];first = false;}
		decodeYUV420SP(rgb, data, width, height);
		this.setRawSQL((int)(Color.red(rgb[index])),(int)(Color.green(rgb[index])),(int)(Color.blue(rgb[index])));
		ColourBox.setColour(rgb[index]);
	}
	
	public void setIsRunning(boolean isRunning)
	{
		this.isRunning=isRunning;
	}

	//Method from Ketai project! Not mine! See below...  
	private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {  

		final int frameSize = width * height;  

		for (int j = 0, yp = 0; j < height; j++) {       int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
			for (int i = 0; i < width; i++, yp++) {  
				int y = (0xff & ((int) yuv420sp[yp])) - 16;  
				if (y < 0)  
					y = 0;  
				if ((i & 1) == 0) {  
					v = (0xff & yuv420sp[uvp++]) - 128;  
					u = (0xff & yuv420sp[uvp++]) - 128;  
				}
				int y1192 = 1192 * y;  
				int r = (y1192 + 1634 * v);  
				int g = (y1192 - 833 * v - 400 * u);  
				int b = (y1192 + 2066 * u);  
	
				if (r < 0)                  r = 0;               else if (r > 262143)  
					r = 262143;  
				if (g < 0)                  g = 0;               else if (g > 262143)  
					g = 262143;  
				if (b < 0)                  b = 0;               else if (b > 262143)  
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
			}  
		}  
	}
}
