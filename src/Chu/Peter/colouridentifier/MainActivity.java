package Chu.Peter.colouridentifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Create an Intent that will start the main activity.
            	MainActivity.this.finish();
                Intent cameraViewer = new Intent(MainActivity.this, CameraActivity.class);
        		startActivityForResult(cameraViewer, 1); 
            }
        }, 1500);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
