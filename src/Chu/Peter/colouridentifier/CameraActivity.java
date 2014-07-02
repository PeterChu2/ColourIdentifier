package Chu.Peter.colouridentifier;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

public class CameraActivity extends Activity {
	private SurfaceView surfaceView; // displays camera preview
	private SurfaceHolder surfaceHolder; // manages SurfaceView changes
	private boolean isPreviewing; // true if camera preview is on
	private Camera camera; // captures image data
	private CameraTouchListener touchListener;
	private CrossHairView crossHairs;
	private ToggleButton button1;
	private ZoomControls zoomControls;
	private ImageView refreshButton;
	private int zoom = 5;
	private int maxZoom;
	private ColourBoxView colourBox;
	private TextView colourText;
	private Camera.Parameters cparams;
	private Point startingPoint;
	private int index = 0;
	private QueryColour queryColour;
	private Thread qc;
	private int frameWidth, frameHeight;
	private Display display;
	private boolean manualMode;

	@Override
	public void onCreate(Bundle bundle) {
		manualMode = false;
		super.onCreate(bundle);
		// Make the app full-screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.camera_viewer);
		crossHairs = new CrossHairView(this);
		colourBox = (ColourBoxView) findViewById(R.id.colourBox);
		final FrameLayout fl = (FrameLayout) findViewById(R.id.frameLayout);
		fl.addView((View) crossHairs);
		
		// initialize surfaceview + set its holder
		touchListener = new CameraTouchListener(crossHairs);
		surfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
		surfaceView.setOnTouchListener(touchListener);
		// get the holder from the surfaceview and add callback
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceCB);
		// for android versions before 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		button1 = (ToggleButton) findViewById(R.id.button1);
		zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
		refreshButton = (ImageView) findViewById(R.id.refreshButton);
		colourText = (TextView) findViewById(R.id.colourText);
		queryColour = new QueryColour(this, colourBox);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// release the camera
		try {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		surfaceHolder.removeCallback(surfaceCB);
		queryColour.setIsRunning(false);
		try {
			qc.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		camera = Camera.open();

		camera.setPreviewCallback(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if (display.getRotation() == 0) {
					//same
					index = frameWidth * (frameHeight - crossHairs.getx()) + crossHairs.gety();
				} else if (display.getRotation() == 1) {
					index = frameWidth * (crossHairs.gety())
							+ crossHairs.getx();
				} else {
					index = frameWidth * (frameHeight - crossHairs.gety())
							+ (frameWidth - crossHairs.getx());
				}
				if ((index <= 0) || (index > frameHeight * frameWidth)) {
					index = 1;
				}
				
				//encapsulate in method
				if (button1.isChecked()) {
					cparams.setFlashMode(Parameters.FLASH_MODE_TORCH);
				} else {
					cparams.setFlashMode(Parameters.FLASH_MODE_OFF);
				}
				camera.setParameters(cparams);
				//end
				
				try {
					queryColour.createRawSQL(data, frameWidth, frameHeight,
							index);
				} catch (IndexOutOfBoundsException e) {
				}
				colourText.setText(queryColour.getText());

			}
		});
		super.onResume();
		surfaceHolder.addCallback(surfaceCB);
		queryColour.setIsRunning(true);
		camera.startPreview();
		if (manualMode == false) {
			qc = new Thread(queryColour);
			qc.start();
		}
		isPreviewing = true;
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		// release the camera
		try {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		surfaceHolder.removeCallback(surfaceCB);
		queryColour.setIsRunning(false);
		try {
			qc.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cameramenu, menu);
		return true;
	} // end method onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.onesec:
			if (queryColour.getIsRunning() == false) {
				manualMode = false;
				queryColour.setIsRunning(true);
				qc = new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(1000);
			return true;
		case R.id.twosecs:
			if (queryColour.getIsRunning() == false) {
				manualMode = false;
				queryColour.setIsRunning(true);
				qc = new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(2000);
			return true;
		case R.id.threesecs:
			if (queryColour.getIsRunning() == false) {
				manualMode = false;
				queryColour.setIsRunning(true);
				qc = new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(3000);
			return true;
		case R.id.fivesecs:
			if (queryColour.getIsRunning() == false) {
				manualMode = false;
				queryColour.setIsRunning(true);
				qc = new Thread(queryColour);
				qc.start();
			}
			queryColour.setSleepTime(5000);
			return true;
		case R.id.manual:
			queryColour.setIsRunning(false);
			manualMode = true;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	} // end method onOptionsItemSelected

	private SurfaceHolder.Callback surfaceCB = new SurfaceHolder.Callback() {
		@Override
		// initialize camera on surfaceview creation
		public void surfaceCreated(SurfaceHolder sh) {
			cparams = camera.getParameters();
			frameHeight = cparams.getPreviewSize().height;
			frameWidth = cparams.getPreviewSize().width;
			camera.setDisplayOrientation(90);
			camera.setParameters(cparams);
			maxZoom = cparams.getMaxZoom();
			zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (zoom > maxZoom - 2) {
						zoom = maxZoom;
					} else {
						zoom += 2;
					}
					cparams.setZoom(zoom);
					camera.setParameters(cparams);
				}
			});
			zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (zoom < 2) {
						zoom = 0;
					}

					else {
						zoom -= 2;
					}
					cparams.setZoom(zoom);
					camera.setParameters(cparams);
				}
			});
			refreshButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					queryColour.refreshStatus();
				}
			});
		}

		@Override
		// release resources when the surfaceview is destroyed
		public void surfaceDestroyed(SurfaceHolder sh) {
			if (isPreviewing) {
				camera.stopPreview();
				isPreviewing = false;
			}
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
		public void surfaceChanged(SurfaceHolder sh, int format, int width,
				int height) {
			// determine screen orientation and adjust the camera display
			// accordingly
			WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			display = wm.getDefaultDisplay();
			// place the cursor in the centre of the screen initially
			// api >=13 uses getSize, api <13 uses getWidth(), getHeight()
			if (android.os.Build.VERSION.SDK_INT >= 13) {
				startingPoint = new Point();
				display.getSize(startingPoint);
				crossHairs.setx(startingPoint.x / 2);
				crossHairs.sety(startingPoint.y / 2);
			} else {
				crossHairs.setx(display.getWidth() / 2);
				crossHairs.sety(display.getHeight() / 2);
			}
			if (display.getRotation() == Surface.ROTATION_0) {
				camera.setDisplayOrientation(90);
				//same
				index = frameWidth * (frameHeight - crossHairs.getx()) + crossHairs.gety();
			} else if (display.getRotation() == Surface.ROTATION_270) {
				camera.setDisplayOrientation(180);
			} else {
				camera.setDisplayOrientation(0);
			}
			try {
				camera.setPreviewDisplay(sh); // display using holder
			} // end try
			catch (IOException e) {
				e.printStackTrace();
			} // end catch
			isPreviewing = true;
		}
	};// ends sh declaration
}
