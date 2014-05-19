package Chu.Peter.colouridentifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Circle extends View {
    Paint p=new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        p.setColor(Color.RED);
        p.setAlpha(200);
        canvas.drawCircle(100, 100, 10, p);
    }

    public Circle(Context context) {
        super(context);
    }
    
    
}