package com.forCHUApps.identi_Color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CrossHairView extends View {
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
        
        float startX = x_coord - 40;
    	float startY = y_coord - 40; 
    	float stopX = x_coord + 40;
    	float stopY = y_coord + 40;
    	
        canvas.drawLine(startX, y_coord, stopX, y_coord, p);
        canvas.drawLine(x_coord, startY, x_coord, stopY, p);
        
//        canvas.drawCircle(x_coord, y_coord, 20, p);
    }
    public CrossHairView(Context context) {
        super(context);
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
