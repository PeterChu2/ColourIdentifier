package Chu.Peter.colouridentifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Circle extends View {
    private Paint p=new Paint();
    private int x_coord;
    private int y_coord;
    Display display;
    WindowManager wm;
    Point startingPoint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.RED);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth((float) 5.0);
        p.setAlpha(200);
        canvas.drawCircle(x_coord, y_coord, 20, p);
    }

    public Circle(Context context) {
        super(context);
        wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display= wm.getDefaultDisplay();
        //place the cursor in the centre of the screen initially
        //api >=13 uses getSize, api <13 uses getWidth(), getHeight()
        if (android.os.Build.VERSION.SDK_INT >= 13){
        	startingPoint=new Point();
        	display.getSize(startingPoint);
        	this.setx(startingPoint.x/2);
        	this.sety(startingPoint.y/2);
        }
        else{
        	this.setx(display.getWidth()/2);
        	this.sety(display.getHeight()/2);
        }
        
    }
    
    
    public void setx(int x){
    	x_coord=x;
    }
    public void sety(int y)
    {
    	y_coord=y;
    	this.invalidate();
    }
    public int getx()
    {
    	return x_coord;
    }
    public int gety()
    {
    	return y_coord;
    }
    
}