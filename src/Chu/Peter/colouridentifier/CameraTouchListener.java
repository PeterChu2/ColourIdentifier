package Chu.Peter.colouridentifier;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CameraTouchListener implements OnTouchListener {
	private CrossHairView circle;

	public CameraTouchListener(CrossHairView circle) {
		this.circle = circle;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		circle.setx((int) event.getX());
		circle.sety((int) event.getY());
		return false;
	}
}
