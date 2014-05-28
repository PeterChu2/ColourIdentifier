package Chu.Peter.colouridentifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColourBox extends View{
	private Paint p=new Paint();
	private Paint borderp=new Paint();
	
    public ColourBox(Context context) {
		super(context);
	}
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    p.setColor(Color.RED);
	    borderp.setColor(Color.BLACK);
	    borderp.setStyle(Paint.Style.STROKE);
	    canvas.drawCircle(getWidth()/2, getHeight()/2, 27, p);
	    canvas.drawCircle(getWidth()/2, getHeight()/2, 27, borderp);
	}
	
	public ColourBox(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public ColourBox(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
}
