package com.example.ben.ripples;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private int screenWidth;
    private int screenHeight;

    private ArrayList<Circle> ripples = new ArrayList<Circle>();
    private ArrayList<Dot> dots = new ArrayList<Dot>();

    private Paint p = new Paint();

    private final static int chgRadius = 2;
    private final static int dotRadius = 20;
    private Random rand = new Random();

    private final static int MAX_FPS = 60;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    private boolean isRunning = false;
    private Thread gameThread;

    private SurfaceHolder holder;

    AlertDialog.Builder builder;
    private boolean whiteOut = true;
    private boolean fingerOK = true;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                screenHeight = height;
                screenWidth = width;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        builder = new AlertDialog.Builder(context);
        builder.setMessage("Game Over");
        builder.setCancelable(false);
        builder.setPositiveButton("Replay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        whiteOut = true;
                        fingerOK = true;
                        dialog.cancel();
                    }
                });
    }

    @Override
    synchronized public boolean onTouchEvent(MotionEvent event) {
        if(fingerOK && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (dots.size() > 0) {
                float xdiff = dots.get(dots.size() - 1).centerX - event.getX();
                float ydiff = dots.get(dots.size() - 1).centerY - event.getY();
                if (xdiff * xdiff + ydiff * ydiff <= dotRadius * dotRadius) {
                    dots.get(dots.size() - 1).tapped = true;
                    dots.add(new Dot(dotRadius + rand.nextInt(screenWidth - 2 * dotRadius), dotRadius + rand.nextInt(screenHeight - 4 * dotRadius), dotRadius));
                }
            }
            else
                dots.add(new Dot(dotRadius+rand.nextInt(screenWidth-2*dotRadius), dotRadius + rand.nextInt(screenHeight - 4 * dotRadius), dotRadius));
            ripples.add(new Circle(event.getX(), event.getY(), 2 * dotRadius));
        }
        return true;
    }

    @Override
    public void run() {
        while(isRunning) {
            if(!holder.getSurface().isValid())
                continue;

            long started = System.currentTimeMillis();

            Canvas canvas = holder.lockCanvas();
            if(nextFrameAndCheck()) {
                whiteOut = false;
                fingerOK = false;
                dots.clear();
                ripples.clear();
                GameActivity.runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                holder.unlockCanvasAndPost(canvas);
            } else {
                if (canvas != null) {
                    render(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                float deltaTime = System.currentTimeMillis() - started;
                int sleepTime = (int) (FRAME_PERIOD - deltaTime);
                if (sleepTime > 0) {
                    try {
                        gameThread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    // Increases the radius of each ripple
    // Returns true if player lost, false if player is still playing
    protected boolean nextFrameAndCheck() {
        for(Circle ripple : ripples) {
            ripple.addRadius(chgRadius, (screenHeight > screenWidth) ? screenHeight : screenWidth);
        }
        if(dots.size() > 0) {
            float rad, xC, yC;
            Dot udot = dots.get(dots.size() - 1);
            for (Circle ripple : ripples) {
                rad = ripple.radius;
                xC = ripple.centerX - udot.centerX;
                yC = ripple.centerY - udot.centerY;
                // If the dot is still red and a ripple passes over it, the player loses
                if (!udot.tapped && rad * rad <= xC * xC + yC * yC + 2 * dotRadius * dotRadius &&
                        rad * rad >= xC * xC + yC * yC - 2 * dotRadius * dotRadius) {
                    return true;
                }
            }
        }
        return false;
    }

    // Draw the dots and ripples
    protected void render(Canvas canvas) {
        if(whiteOut)
            canvas.drawColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        for(Dot dot : dots) {
            if(dot.tapped) {
                p.setColor(Color.GREEN);
                canvas.drawCircle(dot.centerX,dot.centerY,dotRadius,p);
            } else {
                p.setColor(Color.RED);
                canvas.drawCircle(dot.centerX,dot.centerY,dotRadius,p);
            }
        }
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        for(Circle ripple : ripples) {
            p.setColor(Color.BLUE);
            canvas.drawCircle(ripple.centerX,ripple.centerY,ripple.radius,p);
        }
    }

    public void resume() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isRunning = false;
        boolean retry = true;
        while(retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
