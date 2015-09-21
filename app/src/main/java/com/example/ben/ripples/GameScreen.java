package com.example.ben.ripples;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Bundle;
import android.util.AttributeSet;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends Activity {

    private ArrayList<Circle> circleCenters = new ArrayList<>();
    private ArrayList<Circle> randDots = new ArrayList<>();

    Random rand = new Random();
    private final static long seconds = 1;
    private final static long randDotRadius = 10;
    private int width;
    private int height;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < circleCenters.size(); i++)
                circleCenters.get(i).addRadius(3,700);
            handler.postDelayed(runnable, seconds * 50);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        setContentView(new MyView(this));
        Thread mythread = new Thread(runnable);
        mythread.start();
        //handler.postDelayed(runnable, seconds * 500);
    }

    class MyView extends View {

        Paint paint;

        public MyView(Context context) {
            super(context);
            init();
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MyView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
        }

        private void initCircle() {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
        }

        private void initDot() {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            initCircle();
            paint.setStyle(Paint.Style.STROKE);
            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(circleCenters.get(i).getCenterX(), circleCenters.get(i).getCenterY(), circleCenters.get(i).getRadius(), paint);
            }

            initDot();
            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(randDots.get(i).getCenterX(), randDots.get(i).getCenterY(), randDots.get(i).getRadius(), paint);
            }

            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                randDots.add(new Circle(rand.nextInt(width), rand.nextInt(height), randDotRadius));
                circleCenters.add(new Circle(event.getX(),event.getY(),0));
            }
            return true;
        }
    }
}
