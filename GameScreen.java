package com.example.ben.ripples;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends Activity {

    private int randDotRadius = 50;
    private int seconds = 2;
    private int width;
    private int height;
    private boolean playing;
    private int drawBlank;

    private ArrayList<Circle> circleCenters = new ArrayList<>();
    private ArrayList<Dot> randDots = new ArrayList<>();

    private Handler handler = new Handler();
    Runnable incRad = new Runnable() {
        @Override
        public void run() {
            for(int i = 0; i < circleCenters.size(); i++) {
                circleCenters.get(i).addRadius(1, 700);
            }
            handler.postDelayed(incRad, seconds * 10);
        }
    };

    Runnable checkLoss = new Runnable() {
        @Override
        public void run() {
            while(playing){
                if(hasLost(circleCenters,randDots)) {
                    circleCenters.clear();
                    randDots.clear();
                    drawBlank = 1;
                }
            }
        }
    };

    Random rand = new Random();

    AlertDialog.Builder builder;
    AlertDialog askReplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        playing = true;
        drawBlank = 0;

        setContentView(new GameView(this));

        builder = new AlertDialog.Builder(GameScreen.this)
                .setTitle("Game Over")
                .setMessage("Replay?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        circleCenters.clear();
                        randDots.clear();
                        drawBlank = 1;
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GameScreen.this.finish();
                    }
                });

        Thread start = new Thread(checkLoss);
        start.start();
        handler.postDelayed(incRad, seconds * 10);
    }

    private boolean hasLost(ArrayList<Circle> circles, ArrayList<Dot> dots) {
        // Current code is in O(n^2), but can we do better?
        float rad, xC, yC;
        for(int i = 0; i < circles.size(); i++) {
            for(int j = 0; j < dots.size(); j++) {
                rad = circles.get(i).getRadius();
                xC = circles.get(i).getCenterX() - dots.get(j).getCenterX();
                yC = circles.get(i).getCenterY() - dots.get(j).getCenterY();
                if(!dots.get(j).getTapped() && rad*rad <= xC*xC + yC*yC + 2*randDotRadius*randDotRadius && rad*rad >= xC*xC + yC*yC - 2*randDotRadius*randDotRadius) {
                    return true;
                }
            }
        }
        return false;
    }

    class GameView extends View {

        Paint bg;
        Paint paintCircle;
        Paint paintDot;
        Paint paintTappedDot;

        public GameView(Context context) {
            super(context);
            init();
        }

        public GameView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public GameView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(drawBlank == 1) {
                canvas.drawRect((float)0, (float)0, (float)width, (float)height,bg);
                drawBlank = 0;
            }
            super.onDraw(canvas);
            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(randDots.get(i).getCenterX(), randDots.get(i).getCenterY(), randDots.get(i).getRadius(), paintDot);
                if(randDots.get(i).getTapped())
                    canvas.drawCircle(randDots.get(i).getCenterX(), randDots.get(i).getCenterY(), randDots.get(i).getRadius(), paintTappedDot);
            }

            for(int i = 0; i < circleCenters.size(); i++) {
                canvas.drawCircle(circleCenters.get(i).getCenterX(), circleCenters.get(i).getCenterY(), circleCenters.get(i).getRadius(), paintCircle);
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
                randDots.add(new Dot(randDotRadius+rand.nextInt(width-2*randDotRadius), randDotRadius+rand.nextInt(height-4*randDotRadius), randDotRadius));
                circleCenters.add(new Circle(event.getX(),event.getY(),2*randDotRadius));
            }
            return true;
        }

        private void init() {
            bg = new Paint();
            paintCircle = new Paint();
            paintDot = new Paint();
            paintTappedDot = new Paint();

            bg.setColor(Color.WHITE);

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
