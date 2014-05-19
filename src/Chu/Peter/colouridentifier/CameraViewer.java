package Chu.Peter.colouridentifier;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class CameraViewer extends Activity{
	private static final String TAG = "CAMERA_VIEWER"; // for logging errors
	private SurfaceView surfaceView; // displays camera preview     
	private SurfaceHolder surfaceHolder; // manages SurfaceView changes
	private boolean isPreviewing; // true if camera preview is on
	private Camera camera; // captures image data
	private List<Camera.Size> sizes; // supported preview sizes for camera
	private CameraTouchListener touchListener;
	private Circle circle;
	private FrameLayout fl;
	
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.camera_viewer);
		
		//initialize surfaceview + set its holder
		surfaceView=(SurfaceView) findViewById(R.id.cameraSurfaceView);
		surfaceView.setOnTouchListener(touchListener);
		
		//get the holder from the surfaceview and add callback
		surfaceHolder =surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCB);
		
		//for android versions before 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
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
			 camera.setDisplayOrientation(90);
			 sizes = camera.getParameters().getSupportedPreviewSizes();  
			 camera.setPreviewCallback(new PreviewCallback() {
				    @Override
				    public void onPreviewFrame(byte[] data, Camera camera) {
				        int frameHeight = camera.getParameters().getPreviewSize().height;
				        int frameWidth = camera.getParameters().getPreviewSize().width;
				        // number of pixels//transforms NV21 pixel data into RGB pixels  
				        int rgb[] = new int[frameWidth * frameHeight];
				        // convertion
				        //int[] myPixels = decodeYUV420SP(rgb, data, frameWidth, frameHeight);
				    }
				});
			 Camera.Parameters cameraParam = camera.getParameters();
			 cameraParam.setFlashMode(Parameters.FLASH_MODE_TORCH);
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
	 
	 
	 
	 
}
