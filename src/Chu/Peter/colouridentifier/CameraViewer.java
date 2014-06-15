package Chu.Peter.colouridentifier;

import java.io.IOException;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

public class CameraViewer extends Activity{
	private static final String TAG = "CAMERA_VIEWER"; // for logging errors
	private SurfaceView surfaceView; // displays camera preview     
	private SurfaceHolder surfaceHolder; // manages SurfaceView changes
	private boolean isPreviewing; // true if camera preview is on
	private Camera camera; // captures image data
	private CameraTouchListener touchListener;
	private Circle circle;
	private ToggleButton button1;
	private ZoomControls zoomControls;
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
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.camera_viewer);
		circle=new Circle(this);
		colourBox=(ColourBox)findViewById(R.id.colourBox);
		FrameLayout fl = (FrameLayout)findViewById(R.id.frameLayout);
		fl.addView((View) circle);
		//initialize surfaceview + set its holder
		touchListener=new CameraTouchListener(circle);
		surfaceView=(SurfaceView) findViewById(R.id.cameraSurfaceView);
		surfaceView.setOnTouchListener(touchListener);
		//get the holder from the surfaceview and add callback
		surfaceHolder =surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCB);
		//for android versions before 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		button1=(ToggleButton) findViewById(R.id.button1);
		zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
		colourText = (TextView) findViewById(R.id.colourText);
		queryColour = new QueryColour(this);
	}
	@Override
	protected void onPause() {
	    super.onPause();
        // release the camera immediately on pause event   
        //releaseCamera();
        camera.stopPreview(); 
        camera.setPreviewCallback(null);
        surfaceHolder.removeCallback(surfaceCB);
        camera.release();
        camera = null;
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
	    super.onCreateOptionsMenu(menu);
	    return true;
	 } // end method onCreateOptionsMenu
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) 
	 {
		return true;
	 } // end method onOptionsItemSelected
	 private SurfaceHolder.Callback surfaceCB =
		new SurfaceHolder.Callback()
	 {
		 @Override
		 //initialize camera on surfaceview creation
		 public void surfaceCreated(SurfaceHolder sh)
		 {
			 camera=Camera.open();
			 cparams = camera.getParameters();
			 camera.setDisplayOrientation(90);
			 camera.setParameters(cparams);
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
			 
			 camera.setPreviewCallback(new PreviewCallback() {
				    @Override
				    public void onPreviewFrame(byte[] data, Camera camera) {
				    	Log.d("ORIENTATION", Integer.toString(display.getOrientation()));
				    	Camera.Parameters cparams = camera.getParameters();
				    	if(display.getRotation()==0)
						{
							index=frameWidth*(frameHeight-circle.getx())+circle.gety();
						}
						else if(display.getRotation()==1)
						{
							index=frameWidth*(circle.gety())+circle.getx();
						}
						else 
						{
							index = frameWidth*(frameHeight-circle.gety())+(frameWidth-circle.getx());
						}
				    	if(button1.isChecked())
				    	{
				    		cparams.setFlashMode(Parameters.FLASH_MODE_TORCH);
				    	}
				    	else
				    	{
				    		cparams.setFlashMode(Parameters.FLASH_MODE_OFF);
				    	}
				    	camera.setParameters(cparams);
				    	
				        frameHeight = camera.getParameters().getPreviewSize().height;
				        frameWidth = camera.getParameters().getPreviewSize().width;
				        queryColour.createRawSQL(data, frameWidth, frameHeight, index);

					    colourText.setText(queryColour.getText());
					    colourBox.invalidate();
				    }
				});
			 qc=new Thread(queryColour);
			 qc.start();
		 }
		 @Override
		 //release resources when the surfaceview is destroyed
		 public void surfaceDestroyed(SurfaceHolder sh)
		 {
			 camera.stopPreview(); 
		     camera.setPreviewCallback(null);
		     surfaceHolder.removeCallback(surfaceCB);
		     camera.release();
		     camera = null;
		     queryColour.setIsRunning(false);
			 try {
				qc.join();
			 } catch (InterruptedException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 }
		 @Override
		 public void surfaceChanged(SurfaceHolder sh, int format, int width, int height)
		 {
			 
			 //determine screen orientation and adjust the camera display accordingly
			 WindowManager wm =  (WindowManager) getSystemService(WINDOW_SERVICE);
			 display = wm.getDefaultDisplay();
		     display= wm.getDefaultDisplay();
		     //place the cursor in the centre of the screen initially
		     //api >=13 uses getSize, api <13 uses getWidth(), getHeight()
		     if (android.os.Build.VERSION.SDK_INT >= 13){
		        startingPoint=new Point();
		        display.getSize(startingPoint);
		        circle.setx(startingPoint.x/2);
		        circle.sety(startingPoint.y/2);
		     }
		     else{
		        circle.setx(display.getWidth()/2);
		        circle.sety(display.getHeight()/2);
		     }
			 if(display.getRotation()==0)
			 {
				 camera.setDisplayOrientation(90);
				 index=frameWidth*(frameHeight-circle.getx())+circle.gety();
			 }
			 else if(display.getRotation()==3)
			 {
				 camera.setDisplayOrientation(180);
			 }
			 else
			 {
				 camera.setDisplayOrientation(0);
			 }
			 if(isPreviewing)
			 {
				 camera.stopPreview();
			 }
			 try 
	         {
	            camera.setPreviewDisplay(sh); // display using holder
	         } // end try
	         catch (IOException e) 
	         {
	            Log.v(TAG, e.toString());
	         } // end catch
			 camera.startPreview(); // begin the preview
	         isPreviewing = true;
		 }
	 };//ends sh declaration
}
