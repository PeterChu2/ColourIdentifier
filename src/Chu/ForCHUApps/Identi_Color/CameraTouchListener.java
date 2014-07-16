package Chu.ForCHUApps.Identi_Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
public class CameraTouchListener implements OnTouchListener{
	private CrossHairView crossHairs;
	public CameraTouchListener(CrossHairView crossHairs) {
		this.crossHairs=crossHairs;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		crossHairs.setx((int) event.getX());
		crossHairs.sety((int) event.getY());
		return false;
	}
}
