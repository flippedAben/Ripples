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

    private long randDotRadius = 10;
    private long seconds = 1;
    private int width;
    private int height;

    private ArrayList<Circle> circleCenters = new ArrayList<>();
    private ArrayList<Dot> randDots = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < circleCenters.size(); i++)
                circleCenters.get(i).addRadius(3,700);
            handler.postDelayed(runnable, seconds * 10);
        }
    };

    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        setContentView(new MyView(this));
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    class MyView extends View {

        Paint paintCircle;
        Paint paintDot;
        Paint paintTappedDot;

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

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(circleCenters.get(i).getCenterX(), circleCenters.get(i).getCenterY(), circleCenters.get(i).getRadius(), paintCircle);
            }

            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(randDots.get(i).getCenterX(), randDots.get(i).getCenterY(), randDots.get(i).getRadius(), paintDot);
                if(randDots.get(i).getTapped() == true)
                    canvas.drawCircle(randDots.get(i).getCenterX(), randDots.get(i).getCenterY(), randDots.get(i).getRadius(), paintTappedDot);
            }

            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                for(int i = 0; i < randDots.size(); i++){
                    float xdiff = randDots.get(i).getCenterX() - event.getX();
                    float ydiff = randDots.get(i).getCenterY() - event.getY();
                    if(xdiff*xdiff + ydiff*ydiff <= randDotRadius * randDotRadius){
                        randDots.get(i).setTapped(true);
                    }
                }
                randDots.add(new Dot(rand.nextInt(width), rand.nextInt(height), randDotRadius));
                circleCenters.add(new Circle(event.getX(),event.getY(),0));
            }
            return true;
        }

        private void init() {
            paintCircle = new Paint();
            paintDot = new Paint();
            paintTappedDot = new Paint();

            paintCircle.setColor(Color.BLUE);
            paintCircle.setStrokeWidth(3);
            paintCircle.setStyle(Paint.Style.STROKE);

            paintDot.setColor(Color.RED);
            paintDot.setStrokeWidth(0);
            paintDot.setStyle(Paint.Style.FILL);

            paintTappedDot.setColor(Color.GREEN);
            paintTappedDot.setStrokeWidth(0);
            paintTappedDot.setStyle(Paint.Style.FILL);
        }
    }
}
