package com.recep.basicfruitninja;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

/**
 * {@link ApplicationListener} implementation shared by all platforms.
 */
public class BasicFruitNinja extends ApplicationAdapter implements InputProcessor {

    // SpriteBatch for drawing
    private SpriteBatch batch;

    // Textures for background, apple, cherry, pear and bomb
    private Texture background;
    private Texture apple;
    private Texture cherry;
    private Texture pear;
    private Texture bomb;

    // BitmapFont for drawing text
    private BitmapFont font;

    // Lives and score
    private int lives = -1;
    private int score = 0;

    // Generation speed of the fruits
    private final float startGenSpeed = 1.3f;
    private float genCounter = 0;
    private float genSpeed = startGenSpeed;

    // Random object
    private final Random random = new Random();

    // ArrayList for fruits
    private final ArrayList<Fruit> fruitArrayList = new ArrayList<>();

    @Override
    public void create() {
        // Set the input processor
        Gdx.input.setInputProcessor(this);

        // Create the batch
        batch = new SpriteBatch();

        // Load the textures
        background = new Texture("background.png");
        apple = new Texture("apple.png");
        cherry = new Texture("cherry.png");
        pear = new Texture("pear.png");
        bomb = new Texture("bomb.png");


        // Set roboto font
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 120;
        parameter.characters = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890.:,;'\"(!?)+-*/=";
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);
        generator.dispose();
    }

    @Override
    public void render() {

        // Start the batch
        batch.begin();

        // Draw the background
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Check if the game is restarted
        if (lives == 0 && Gdx.input.isTouched())
            lives = -1;

        // Check the game state
        if (lives == -1) {
            // Game start
            font.draw(batch, "Cut To Play", Gdx.graphics.getWidth() / 2f - 301, Gdx.graphics.getHeight() / 2f + 43);
        } else if (lives == 0) {
            // Game Over
            font.draw(batch, "Click To Restart", Gdx.graphics.getWidth() / 2f - 418.5f, Gdx.graphics.getHeight() / 2f + 153);
            font.setColor(Color.RED);
            font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2f - 291, Gdx.graphics.getHeight() / 2f + 43);
            font.setColor(Color.BLACK);
            font.draw(batch, "Your Score : " + score, Gdx.graphics.getWidth() / 2f - 405, Gdx.graphics.getHeight() / 2f - 67);
        } else {
            // Game Running

            // Increase the generation speed
            genSpeed -= Gdx.graphics.getDeltaTime() * score * 0.00001f;
            if (genCounter >= genSpeed) {
                addItem();
                genCounter = 0;
            } else
                genCounter += Gdx.graphics.getDeltaTime();

            // Draw the lives
            for (int i = 0; i < lives; i++)
                batch.draw(apple, 25 + i * 100, Gdx.graphics.getHeight() - 100, 75, 75);

            // Draw the fruits
            for (Fruit fruit : fruitArrayList) {
                fruit.update(Gdx.graphics.getDeltaTime()); // Update the position of the fruit

                switch (fruit.type) {
                    case APPLE:
                        batch.draw(apple, fruit.getPosition().x, fruit.getPosition().y, Fruit.radius * 2, Fruit.radius * 2);
                        break;
                    case CHERRY:
                        batch.draw(cherry, fruit.getPosition().x, fruit.getPosition().y, Fruit.radius * 2, Fruit.radius * 2);
                        break;
                    case BOMB:
                        batch.draw(bomb, fruit.getPosition().x, fruit.getPosition().y, Fruit.radius * 2, Fruit.radius * 2);
                        break;
                    default:
                        batch.draw(pear, fruit.getPosition().x, fruit.getPosition().y, Fruit.radius * 2, Fruit.radius * 2);
                        break;
                }
            }

            // Check if the fruit is out of the screen
            ArrayList<Fruit> toRemove = new ArrayList<>();
            for (Fruit fruit : fruitArrayList) {
                if (fruit.outOfScreen()) {
                    toRemove.add(fruit);
                    if (fruit.isAlive && fruit.type == Fruit.Type.PEAR) {
                        lives--;
                        for (Fruit f : fruitArrayList)
                            f.isAlive = false;
                    }
                }
            }
            fruitArrayList.removeAll(toRemove);
        }

        // Draw the score
        font.draw(batch, "Score = " + score, Gdx.graphics.getWidth() - 710, Gdx.graphics.getHeight() - 14);

        // End the batch
        batch.end();
    }

    // Add a new fruit to the screen
    private void addItem() {
        // Random X position for the fruit
        int randomX = random.nextInt(Gdx.graphics.getWidth() - 100) + 50;

        // Position and velocity of the fruit
        Vector2 position = new Vector2(randomX, -2 * Fruit.radius);
        Vector2 velocity = new Vector2(Gdx.graphics.getWidth() * random.nextFloat() * 0.5f, Gdx.graphics.getHeight() * (random.nextInt(4) + 6) * 0.15f);

        // Random direction of the fruit
        if (randomX > Gdx.graphics.getWidth() / 2)
            velocity.x = -velocity.x;

        // Create a new fruit
        Fruit fruit = new Fruit(position, velocity);

        // Random type of the fruit
        float randomType = random.nextFloat();
        if (randomType < 0.55)
            fruit.type = Fruit.Type.PEAR;
        else if (randomType < 0.75)
            fruit.type = Fruit.Type.CHERRY;
        else if (randomType < 0.85)
            fruit.type = Fruit.Type.APPLE;
        else
            fruit.type = Fruit.Type.BOMB;

        // Add the fruit to the ArrayList
        fruitArrayList.add(fruit);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Check if the game is running or not
        if (lives == -1) {
            // Start the game
            lives = 4;
            score = 0;
            genSpeed = startGenSpeed;
            fruitArrayList.clear();
        } else {
            // Game is running
            Vector2 clickPosition = new Vector2(screenX, Gdx.graphics.getHeight() - screenY); // Position of the click

            // Check if the fruit is clicked
            ArrayList<Fruit> toRemove = new ArrayList<>();
            for (Fruit fruit : fruitArrayList) {
                if (fruit.isClicked(clickPosition)) {
                    if (fruit.type == Fruit.Type.BOMB)
                        lives--;
                    else if (fruit.type == Fruit.Type.APPLE)
                        lives++;
                    else if (fruit.type == Fruit.Type.CHERRY)
                        score += 3;
                    else
                        score++;
                    toRemove.add(fruit);
                }
            }
            fruitArrayList.removeAll(toRemove);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
