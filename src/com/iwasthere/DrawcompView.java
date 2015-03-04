package com.iwasthere;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;

public class DrawcompView extends SurfaceView{
private Paint stickPaint = new Paint();
private Paint headPaint = new Paint();
private int h;
private int w;

 
 public DrawcompView(Context context, int height, int width) {
 super(context);
 h = height;
 w = width;
 // Create out paint to use for drawing
 stickPaint.setARGB(150, 0, 0, 255);
 headPaint.setARGB(255, 100, 0, 255);
 stickPaint.setStrokeWidth(20);
 headPaint.setStrokeWidth(15);
 // This call is necessary, or else the 
 // draw method will not be called.
 setWillNotDraw(false);
 }
 
 @Override
 protected void onDraw(Canvas canvas){
 // A Simple Text Render to test the display
	canvas.drawLine(w/2, (h/3), w/2, (h/2 - w/2), stickPaint);
 	canvas.drawLine((w/2 - w/8), ((h/2 - w/2) + w/8), w/2, (h/2 - w/2), headPaint);
 	canvas.drawLine((w/2 + w/8), ((h/2 - w/2) + w/8), w/2, (h/2 - w/2), headPaint);
 }
}