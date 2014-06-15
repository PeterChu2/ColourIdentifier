package Chu.Peter.colouridentifier;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColourBox extends View{
	private static Paint p=new Paint();
	private Paint borderp=new Paint();

    public ColourBox(Context context) {
		super(context);
		p.setColor(Color.YELLOW);
	    borderp.setColor(Color.BLACK);
	}
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    borderp.setStyle(Paint.Style.STROKE);
	    canvas.drawCircle(getWidth()/2, getHeight()/2, 27, p);
	    canvas.drawCircle(getWidth()/2, getHeight()/2, 27, borderp);
	}
	public ColourBox(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    p.setColor(Color.YELLOW);
	}
	public ColourBox(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    p.setColor(Color.YELLOW);
	}
	public static void setColour(int colour)
	{
		p.setARGB(255,Color.red(colour),Color.green(colour),Color.blue(colour));
	}
}
