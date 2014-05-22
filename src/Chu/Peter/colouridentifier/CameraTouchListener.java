package Chu.Peter.colouridentifier;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
public class CameraTouchListener implements OnTouchListener{

	private Circle circle;
	public CameraTouchListener(Circle circle) {
		this.circle=circle;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		String TAG = "COORDINATE";

		circle.setx((int) event.getX());
		circle.sety((int) event.getY());	

		//maxX = (float)view.getWidth();
		//maxY = (float)view.getHeight();
		String message = "x: " + event.getX() + " y: "+ event.getY();
		Log.d(TAG, message);



		return false;
	}
	

}
