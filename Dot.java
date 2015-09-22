package com.example.ben.ripples;

public class Dot extends Circle {

    private boolean tapped;

    public Dot(float x, float y, float r) {
        super(x,y,r);
        tapped = false;
    }

    public void setTapped(boolean fact) {
        tapped = fact;
    }

    public boolean getTapped() {
        return tapped;
    }
}
