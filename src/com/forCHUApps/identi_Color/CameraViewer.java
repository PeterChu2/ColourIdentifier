package com.forCHUApps.identi_Color;

import java.io.IOException;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

public class CameraViewer extends Activity{
	private TextureView textureView; // displays camera preview
	private boolean isPreviewing; // true if camera preview is on
	private Camera camera; // captures image data
	private CameraTouchListener touchListener;
	private CrossHairView crosshairs;
	private ToggleButton flashButton;
	private ZoomControls zoomControls;
	private ImageView refreshButton;
	private ImageView logButton;
	private int zoom=5;
	private int maxZoom;
	private ColourBox colourBox;
	private TextView colourText;
	private Camera.Parameters cparams;
	private Point startingPoint;
	private int index=0;
	private QueryColour queryColour;
	private Thread qc;
	private int frameWidth, frameHeight;
	private Display display;
	private boolean manualMode;
	private SurfaceTextureListener surfaceTextureListener;
	private byte[] buffer;
	private DatabaseConnector dc;
	private ListView logListView;
	private CustomAdapter colourAdapter;
	private Cursor cursor;
	SurfaceTexture surfaceTexture;
	private ViewPager pager;

	@Override
	public void onCreate(Bundle bundle)
	{
		manualMode = false;
		super.onCreate(bundle);
		//Make the app full-screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_viewer);
		crosshairs=new CrossHairView(this);
		colourBox=(ColourBox)findViewById(R.id.colourBox);
		FrameLayout fl = (FrameLayout)findViewById(R.id.frameLayout);
		fl.addView((View) crosshairs);
		//initialize textureView + set its surface Texture
		touchListener=new CameraTouchListener(crosshairs);
		textureView=(TextureView) findViewById(R.id.cameraTextureView);
		textureView.setOnTouchListener(touchListener);
		textureView.setSurfaceTextureListener(surfaceTextureListener);
		flashButton=(ToggleButton) findViewById(R.id.button1);
		zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
		refreshButton = (ImageView) findViewById(R.id.refreshButton);
		logButton = (ImageView) findViewById(R.id.logButton);
		colourText = (TextView) findViewById(R.id.colourText);
		queryColour = new QueryColour(this);
		dc = new DatabaseConnector(this);
	    // Set the ViewPager adapter
	    ViewPagerAdapter adapter = new ViewPagerAdapter();
	    pager = (ViewPager) findViewById(R.id.pager);
	    pager.setAdapter(adapter);
	    logListView = (ListView) findViewById(R.id.logListView);
		colourAdapter = new CustomAdapter(CameraViewer.this, cursor, 0);
		logListView.setAdapter(colourAdapter);
		dc.open();
		populateListViewFromDB();
		dc.close();

		surfaceTextureListener =
				new SurfaceTextureListener()
		{
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface,
					int width, int height) {

				WindowManager wm =  (WindowManager) getSystemService(WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				display= wm.getDefaultDisplay();
				//place the cursor in the centre of the screen initially

				startingPoint=new Point();
				display.getSize(startingPoint);
				crosshairs.setx(startingPoint.x/2);
				crosshairs.sety(startingPoint.y/2);

				try 
				{
					camera.setPreviewTexture(surface);
					camera.startPreview();
				} catch (IOException ioe) {
					// Something bad happened
				}
				cparams = camera.getParameters();
				frameHeight = cparams.getPreviewSize().height;
				frameWidth = cparams.getPreviewSize().width;
				camera.setDisplayOrientation(90);
				maxZoom=cparams.getMaxZoom();
				zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(zoom>maxZoom-2)
						{
							zoom=maxZoom;
						}
						else{zoom+=2;}
						cparams.setZoom(zoom);
						camera.setParameters(cparams);
					}
				});
				zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if(zoom<2)
						{
							zoom=0;
						}

						else{zoom-=2;}
						cparams.setZoom(zoom);
						camera.setParameters(cparams);
					}
				});
				refreshButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						queryColour.createRawSQL();
						queryColour.refreshStatus();
					}
				});
				flashButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if(isChecked)
						{
							cparams.setFlashMode(Parameters.FLASH_MODE_TORCH);
						}
						else
						{
							cparams.setFlashMode(Parameters.FLASH_MODE_OFF);
						}
						camera.setParameters(cparams);
					}
				});
				buffer = new byte[frameWidth*frameHeight*(ImageFormat.getBitsPerPixel(cparams.getPreviewFormat()))/8];
				camera.addCallbackBuffer(buffer);
				queryColour.setWidth(frameWidth);
				queryColour.setHeight(frameHeight);
				camera.setPreviewCallbackWithBuffer(new CameraCallback());
				logButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dc.open();
						dc.insertRecord((String) colourText.getText(), String.format("#%06X", (0xFFFFFF & colourBox.getP().getColor())), colourBox.getP().getColor());
						populateListViewFromDB();
						colourAdapter.notifyDataSetChanged();
						dc.close();
						Toast t = Toast.makeText(CameraViewer.this, "Colour logged! Swipe left to view log!", Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER, 0, 0);
						t.show();
					}
				});
			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
					int width, int height) {
				//determine screen orientation and adjust the camera display accordingly
				WindowManager wm =  (WindowManager) getSystemService(WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				display= wm.getDefaultDisplay();
				//place the cursor in the centre of the screen initially

				startingPoint=new Point();
				display.getSize(startingPoint);
				crosshairs.setx(startingPoint.x/2);
				crosshairs.sety(startingPoint.y/2);

				camera.setDisplayOrientation(90);
				index=frameWidth*(frameHeight-crosshairs.getx())+crosshairs.gety();

				try
				{
					camera.setPreviewTexture(surface); // display using holder
				} // end try
				catch (IOException e) 
				{
					e.printStackTrace();
				} // end catch
				camera.startPreview();
				isPreviewing = true;
			}

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			}
			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				if(isPreviewing)
				{
					isPreviewing = false;
				}
				return true;
			}
		};//ends surfaceTextureListener
	}
	@Override
	protected void onPause() {
		super.onPause();
		// release the camera
		try{
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		queryColour.setIsRunning(false);
		try
		{    
			qc.join();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		camera=Camera.open();

		super.onResume();
		textureView.setSurfaceTextureListener(surfaceTextureListener);
		queryColour.setIsRunning(true);
		surfaceTexture=textureView.getSurfaceTexture();
		try {
			camera.setPreviewTexture(surfaceTexture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.setDisplayOrientation(90);
		camera.startPreview();
		if(manualMode == false)
		{
			qc=new Thread(queryColour);
			qc.start();
		}
		isPreviewing = true;
		camera.setPreviewCallbackWithBuffer(new CameraCallback());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// release the camera
		try{
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
			dc.close();
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}

		queryColour.setIsRunning(false);
		try
		{    
			qc.join();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cameramenu, menu);
		return true;
	} // end method onCreateOptionsMenu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.onesec:
			if(queryColour.getIsRunning() == false)
			{
				manualMode=false;
				queryColour.setIsRunning(true);
				qc=new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(1000);
			return true;
		case R.id.twosecs:
			if(queryColour.getIsRunning() == false)
			{
				manualMode=false;
				queryColour.setIsRunning(true);
				qc=new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(2000);
			return true;
		case R.id.threesecs:
			if(queryColour.getIsRunning() == false)
			{
				manualMode = false;
				queryColour.setIsRunning(true);
				qc=new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(3000);
			return true;
		case R.id.fivesecs:
			if(queryColour.getIsRunning() == false)
			{
				manualMode = false;
				queryColour.setIsRunning(true);
				qc=new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(5000);
			return true;
		case R.id.manual:
			queryColour.setIsRunning(false);
			manualMode = true;
			return true;
		case R.id.clearLog:
			dc.open();
			dc.deleteRecords();
			populateListViewFromDB();
			dc.close();
			colourAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	} // end method onOptionsItemSelected
	
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
			e.printStackTrace();
		}
	} // end method onStop
	
	@Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }
	
    class ViewPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
            case 0:
                resId = R.id.frameLayout;
                break;
            case 1:
                resId = R.id.logListView;
                break;
            }
            return findViewById(resId);
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }
    private void populateListViewFromDB() {
		cursor = dc.getAllRecords();
	    colourAdapter.changeCursor(cursor);
    }
    private class CameraCallback implements PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			index=frameWidth*(frameHeight-crosshairs.getx())+crosshairs.gety();

			if ((index <= 0)||(index > frameHeight*frameWidth))
			{
				index=1;
			}
			queryColour.setIndex(index);
			queryColour.setData(data);
			camera.addCallbackBuffer(data);
		}
    }
}
