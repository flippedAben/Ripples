package com.example.ben.ripples;

public class Circle {

    float centerX;
    float centerY;
    float radius;

    public Circle(float x, float y, float r){
        centerX = x;
        centerY = y;
        radius = r;
    }

    public void addRadius(float r, float limit){
        radius = (radius + r)%limit;
    }
}
