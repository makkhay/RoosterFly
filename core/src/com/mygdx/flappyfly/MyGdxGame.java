package com.mygdx.flappyfly;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;    // Texture means images

    Texture gameOver;
    Texture instr;   // instruction image

    Circle birdCircle; // for collision detection
    ShapeRenderer shapeRenderer;

    Rectangle [] topTuberectangles;  // for collision detection
    Rectangle [] bottomTuberectangles;  // for collision detection




    Texture[] birds;     // Image of a bird
    int flapState = 0;

    float birdY = 0;
    float velocity = 0;
    int gameState = 0;  // keep track of the state of the game
    float gravity = 2;

    Texture topTubes;  // Image of tubes
    Texture bottomTubes;  // Image of tubes
    float gap = 500;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;

    int score = 0;
    int scoringTube = 0;

    BitmapFont font;
    private Music music;

    private TextButton playButton, restartButton, exitButton;
    private TextButton.TextButtonStyle style;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        birds = new Texture[3];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birds[2] = new Texture("bird3.png");
        gameOver = new Texture("gameover.png");
        instr = new Texture("instr.png");




        music = Gdx.audio.newMusic(Gdx.files.internal("Music.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();


        // for collision detection
       // shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();

        //font = new BitmapFont();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(7);

        // for collision detection
        topTuberectangles = new Rectangle[numberOfTubes];
        bottomTuberectangles = new Rectangle[numberOfTubes];


        topTubes = new Texture("toptube.png");
        bottomTubes = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth()  * 3 / 4;
        startGame();





    }

    public void startGame(){


        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            // first tube starts at the right
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTubes.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

            // initializing the rectangles
            topTuberectangles[i] = new Rectangle();
            bottomTuberectangles[i] = new Rectangle();

        }

    }



    @Override
    public void render() {


        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());// setting background image

        if (gameState == 1) {

             // keep tracks of score
            if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
                score++;
                Gdx.app.log("Score",  String.valueOf(score));
                if(scoringTube < numberOfTubes - 1 ){

                    scoringTube++;

                } else {

                     scoringTube = 0;

                }
            }

            // input for our game, if touch is detected
            if (Gdx.input.justTouched()) {

                velocity = -30;  // velocity for bird to fly upward after a touch


            }

            // Creates infinite pipe loop
            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTubes.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;


                }

                tubeX[i] = tubeX[i] - tubeVelocity;

                batch.draw(topTubes, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTubes, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTubes.getHeight() + tubeOffset[i]);

                topTuberectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTubes.getWidth(), topTubes.getHeight());
                bottomTuberectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTubes.getHeight() + tubeOffset[i], bottomTubes.getWidth(),bottomTubes.getHeight());

            }

            //Put this condition for testing purpose so that the bird won't fall off, remove this later.
            /* if (birdY > 0 || velocity < 0)
            *
             */

            if (birdY > 0 ) {
                // increases the velocity every time the render loop is called
                velocity = velocity + gravity;
                // decrease the yCordinate by velocity
                birdY -= velocity;
            }else {
                gameState = 2;
            }


        } else if ( gameState == 0){
            // instruction picture
            batch.draw(instr, Gdx.graphics.getWidth() / 2 - instr.getWidth() / 2, Gdx.graphics.getHeight() / 2 - instr.getHeight() / 2);

            // input for our game, if touch is detected
            if (Gdx.input.justTouched()) {

                gameState = 1;

            }


        } else if (gameState ==2){    // Game 2 means game over and 1 means not over

            batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth() /2, Gdx.graphics.getHeight()/2 - gameOver.getHeight() /2 );
            font.draw(batch, String.valueOf("Your Score: " + score),Gdx.graphics.getWidth()/ 3 - gameOver.getWidth() /2, Gdx.graphics.getHeight()/2 - gameOver.getHeight() /  2 );
            font.draw(batch, String.valueOf("Tap To Restart!"),Gdx.graphics.getWidth()/ 3 - gameOver.getWidth() /2, Gdx.graphics.getHeight()/4 - gameOver.getHeight() / 4 );

            // restart game once touched
            if( Gdx.input.justTouched()){

                gameState = 1;
                startGame();
                score = 0;
                scoringTube =0;
                velocity =0;

            }


        }


        // Stops flapping if game is over
        if(gameState == 1) {

            // Helps switch back between two images
            if (flapState == 0) {
                flapState = 2;
            } else {
                flapState = 0;
            }
        }



        // putting bird into the center
        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);





        // Text for score
        if(gameState ==1 ) {
            font.draw(batch, String.valueOf(score), 100, 200);
        }
        batch.end();

        // for collision detection and painting
       // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);

        // gets the coordinates of the bird
        birdCircle.set(Gdx.graphics.getWidth() / 2 , birdY + birds[flapState].getHeight() /2 , birds[flapState].getWidth() /2);
       // shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);





        // setting up red rectangles
        for (int i = 0; i < numberOfTubes; i++) {
            // paints pipes into red
           // shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTubes.getWidth(), topTubes.getHeight());
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTubes.getHeight() + tubeOffset[i], bottomTubes.getWidth(),bottomTubes.getHeight());

            if(Intersector.overlaps(birdCircle,topTuberectangles[i]) || Intersector.overlaps(birdCircle, bottomTuberectangles[i])){
               gameState = 2; // means gameover
            }


        }
       // shapeRenderer.end();




    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        music.dispose();
    }

    private  void initButtons(){

        style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.DARK_GRAY;
        playButton = new TextButton("Play", style);
        playButton.setPosition(110,260);
        playButton.getLabel().setFontScale(2);

    }



}
