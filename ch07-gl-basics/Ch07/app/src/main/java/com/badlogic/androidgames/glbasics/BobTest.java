package com.badlogic.androidgames.glbasics;

import android.opengl.Matrix;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class BobTest extends GLGame{

    public Screen getStartScreen() {
        return new BobScreen(this);
    }

    class BobScreen extends Screen {
        static final int NUM_BOBS = 100;
        GLGraphics glGraphics;
        Bob[] bobs;


        public BobScreen(GLGame game) {
            super(game);
            glGraphics = (game).getGLGraphics();
            bobs = new Bob[NUM_BOBS];
            for(int i = 0; i < NUM_BOBS; i++) {
                bobs[i] = new Bob(game);
            }

        }

        @Override
        public void present(float[] mMVPMatrix) {

            float[] mViewMatrix = new float[16];
            float[] matrix = new float[16];
            // Draw background color
            glGraphics.clearScreen(0f,0f,0f,1f);

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, .5f, .7f, -3f, .5f, .7f, 0, 0, 1, 0);

            Matrix.multiplyMM(matrix, 0,mProjectionMatrix , 0, mViewMatrix, 0);

            for(int i = 0; i < NUM_BOBS; i++) {
            matrix = new float[16];
            Matrix.multiplyMM(matrix, 0,mProjectionMatrix , 0, mViewMatrix, 0);
                bobs[i].draw(matrix);
            }
        }

        @Override
        public void update(float deltaTime) {
            game.getInput().getTouchEvents();
            game.getInput().getKeyEvents();
            for(int i = 0; i < NUM_BOBS; i++) {
                bobs[i].update(deltaTime);
            }

        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void dispose() {

        }
    }
}
