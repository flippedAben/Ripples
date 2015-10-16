package com.example.ben.ripples;

public class Dot extends Circle {

    boolean tapped;

    public Dot(float x, float y, float r) {
        super(x,y,r);
        tapped = false;
    }
}
