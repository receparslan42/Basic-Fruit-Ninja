package com.recep.basicfruitninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fruit {

    // Radius of the fruit
    static final float radius = Math.max(Gdx.graphics.getHeight(), Gdx.graphics.getWidth()) / 40f;

    // Types of the fruit
    enum Type { APPLE, CHERRY, PEAR, BOMB }

    // Type of the fruit
    Type type;

    // Position and velocity of the fruit
    private final Vector2 position;
    private final Vector2 velocity;

    // State of the fruit
    boolean isAlive = true;

    // Constructor of the fruit class with position and velocity
    Fruit(Vector2 position,Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
        type = Type.PEAR;
    }

    // Check if the fruit is clicked
    public boolean isClicked(Vector2 clickPosition) {
        Vector2 center = new Vector2(position.x+radius, position.y+radius);
        return center.dst2(clickPosition) <= radius*radius+1;
    }

    // Get the position of the fruit
    public final Vector2 getPosition() {
        return position;
    }

    // Check if the fruit is out of the screen
    public boolean outOfScreen() {
        return position.y < -2*radius;
    }

    // Update the fruit position
    public void update(float deltaFrame) {
        velocity.x -= deltaFrame* Gdx.graphics.getWidth()*velocity.x*0.00005f;
        velocity.y -= deltaFrame * Gdx.graphics.getHeight();
        position.mulAdd(velocity, deltaFrame);
    }
}
