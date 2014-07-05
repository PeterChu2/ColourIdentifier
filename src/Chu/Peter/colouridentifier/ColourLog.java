package Chu.Peter.colouridentifier;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ListView;

public class ColourLog extends Activity 
{
	private ListView logListView;
	LoaderManager lm;
	DatabaseConnector dc;
	private CustomAdapter colourAdapter;
	private GestureDetectorCompat mDetector;
	private Cursor cursor;

	
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	//.initLoader(0,null, this);
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_log);
		dc = new DatabaseConnector(ColourLog.this);
		logListView = (ListView) findViewById(R.id.logListView);
//		cursor = dc.getAllRecords();
		colourAdapter = new CustomAdapter(ColourLog.this, cursor, 0);
		if(logListView == null){Log.d("DEBUG","TESRASD");
		}
		if(colourAdapter == null){Log.d("DEBUG","2222222");
		}

		dc.open();
		populateListViewFromDB();
		logListView.setAdapter(colourAdapter);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());
		// Create customAdapter to map log items to the listView
	    
	    
//		getLoaderManager().initLoader(0, null,  (android.app.LoaderManager.LoaderCallbacks<Cursor>) this);
		
	} // end method onCreate

	private void populateListViewFromDB() {
		cursor = dc.getAllRecords();
	    colourAdapter.changeCursor(cursor);
	    
		// Set the adapter for the list view
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_menu, menu);
		return true;
	} // end method onCreateOptionsMenu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.clearLog:
			dc.open();
			dc.deleteRecords();
			populateListViewFromDB();
			dc.close();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	} // end method onOptionsItemSelected
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
		private static final String DEBUG_TAG = "Gestures"; 
		private int swipe_Min_Distance = 40;
		private int swipe_Max_Distance = 350;
		private int swipe_Min_Velocity = 30;
		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) 
		{
			final float xDistance = Math.abs(event1.getX() - event2.getX());
			final float yDistance = Math.abs(event1.getY() - event2.getY());
			velocityX = Math.abs(velocityX);
			velocityY = Math.abs(velocityY);

			if(xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
			{return false;}

			if(velocityX > this.swipe_Min_Velocity && xDistance > this.swipe_Min_Distance){
				if(event1.getX() > event2.getX())
				{// right to left
					Log.d(DEBUG_TAG, "onFling: swipe to left" );
					
				}
				else
				{//swipe left to right
					Log.d(DEBUG_TAG, "onFling: swipe to right" );
					finish();
					overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
				}
			}
			return true;
		} 
	}
	@Override
	protected void onResume() 
	{
		super.onResume(); // call super's onResume method

		// create new GetContactsTask and execute it 
//		new GetContactsTask().execute((Object[]) null);
	} // end method onResume

	@Override
	protected void onStop() 
	{
		super.onStop();
		try{
			dc.close();
			cursor.close();
		}
		catch(NullPointerException e)
		{
			Log.d("DEBUG", "cursor is null");
		}
	} // end method onStop
	
	@Override
	public void onBackPressed()
	{
		this.finish();
		overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
	}

}
