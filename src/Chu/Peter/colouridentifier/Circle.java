package Chu.Peter.colouridentifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class Circle extends View {
    private Paint p=new Paint();
    private int x_coord=0;
    private int y_coord=0;

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