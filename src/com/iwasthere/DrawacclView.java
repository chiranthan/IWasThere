package com.iwasthere;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;

public class DrawacclView extends SurfaceView{
private Paint textPaint = new Paint();
float x, y;
 
 public DrawacclView(Context context, float a,float b) {
 super(context);
 x = a;
 y = b;
 // Create out paint to use for drawing
 textPaint.setARGB(255, 200, 0, 0);
 textPaint.setTextSize(10);
 // This call is necessary, or else the 
 // draw method will not be called. 
 setWillNotDraw(false);
 }
 
 @Override
 protected void onDraw(Canvas canvas){
 // A Simple Text Render to test the display
 canvas.drawCircle(x, y, 50, textPaint );
 }
}