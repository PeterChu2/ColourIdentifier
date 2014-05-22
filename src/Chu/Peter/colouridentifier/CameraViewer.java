package Chu.Peter.colouridentifier;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

public class CameraViewer extends Activity{
	private static final String TAG = "CAMERA_VIEWER"; // for logging errors
	private SurfaceView surfaceView; // displays camera preview     
	private SurfaceHolder surfaceHolder; // manages SurfaceView changes
	private boolean isPreviewing; // true if camera preview is on
	private Camera camera; // captures image data
	private List<Camera.Size> sizes; // supported preview sizes for camera
	private CameraTouchListener touchListener;
	private Circle circle;
	private ToggleButton button1;
	private SeekBar zoomLevel;
	private ZoomControls zoomControls;
	private int zoom=5;
	private int maxZoom;
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.camera_viewer);
		circle=new Circle(this);
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
		
		zoomControls= (ZoomControls) findViewById(R.id.zoomControls);
		
		
		
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    try
	    {    
	        // release the camera immediately on pause event   
	        //releaseCamera();
	         camera.stopPreview(); 
	         camera.setPreviewCallback(null);
	         surfaceHolder.removeCallback(surfaceCB);
	         camera.release();
	         camera = null;

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
			 
			 final Camera.Parameters cameraParam = camera.getParameters();
			 camera.setDisplayOrientation(90);
			 sizes = cameraParam.getSupportedPreviewSizes();  
			 camera.setParameters(cameraParam);
			 maxZoom=cameraParam.getMaxZoom();
			 zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if(zoom>maxZoom-2)
						{
							zoom=maxZoom;
						}
						
						else{zoom+=2;}
						cameraParam.setZoom(zoom);
						camera.setParameters(cameraParam);
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
						cameraParam.setZoom(zoom);
						camera.setParameters(cameraParam);
					}
				});
			 
			 camera.setPreviewCallback(new PreviewCallback() {
				    @Override
				    public void onPreviewFrame(byte[] data, Camera camera) {
				    	Camera.Parameters cameraParam = camera.getParameters();
				    	if(button1.isChecked())
				    	{
				    		cameraParam.setFlashMode(Parameters.FLASH_MODE_TORCH);
				    	}
				    	else
				    	{
				    		cameraParam.setFlashMode(Parameters.FLASH_MODE_OFF);
				    	}
				    	
				    	camera.setParameters(cameraParam);
				    	
				        int frameHeight = camera.getParameters().getPreviewSize().height;
				        int frameWidth = camera.getParameters().getPreviewSize().width;
				        // number of pixels//transforms NV21 pixel data into RGB pixels  
				        int rgb[] = new int[frameWidth * frameHeight];
				        // conversion
				        //int[] myPixels = decodeYUV420SP(rgb, data, frameWidth, frameHeight);
				        
				    }
				});
			 
			 camera.startPreview();
		 }
		 
		 
		 @Override
		 //release resources when the surfaceview is destroyed
		 public void surfaceDestroyed(SurfaceHolder sh)
		 {
			 camera.stopPreview();
			 isPreviewing = false;
			 camera.release();
		 }
		 
		 @Override
		 public void surfaceChanged(SurfaceHolder sh, int format, int width, int height)
		 {
			 WindowManager mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
			 Display mDisplay = mWindowManager.getDefaultDisplay();
			 if(mDisplay.getRotation()==0)
			 {
				 camera.setDisplayOrientation(90);
			 }
			 else if(mDisplay.getRotation()==3)
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
			 
			 Camera.Parameters parameters=camera.getParameters();
			 parameters.setPreviewSize(sizes.get(0).width, sizes.get(0).height);
			 camera.setParameters(parameters);
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
	 
	 public static void PlaceCursor(int x, int y){
		 
	 }
	 
	 
}
