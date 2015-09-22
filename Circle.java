package com.example.ben.ripples;

public class Circle {

    private float centerX;
    private float centerY;
    private float radius;

    public Circle(float x, float y, float r){
        centerX = x;
        centerY = y;
        radius = r;
    }

    public float getCenterX(){
        return centerX;
    }

    public float getCenterY(){
        return centerY;
    }

    public float getRadius(){
        return radius;
    }

    public void addRadius(float r, float limit){
        radius = (radius + r)%limit;
    }
}
