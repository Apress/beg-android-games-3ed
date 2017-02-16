package com.badlogic.androidgames.glbasics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class FirstTriangleTest extends GLGame {
    int mProgram;
    int mPositionHandle;
    int coordsPerVertex;
    int vertexStride;
    int mColorHandle;
    float color[] = { 1f, 0f, 0f, 1f };
    FloatBuffer vertexBuffer;
    float triangleCoords[] = {
            0.0f, 1f, 0.0f,   // top
            -1f, -1f, 0.0f,   // bottom left
            1f, -1f, 0.0f    // bottom right
    };

    public Screen getStartScreen() {
        return new FirstTriangleScreen(this);
    }
    class FirstTriangleScreen extends Screen {
        GLGraphics glGraphics;
        FloatBuffer vertices;

        public FirstTriangleScreen(GLGame game) {
            super(game);
            glGraphics = (game).getGLGraphics();

        }

        @Override
        public void present(float[] matrix) {
            float[] mViewMatrix =new float[16];

            // Draw background color
            glGraphics.clearScreen(0f,0f,0f,1f);

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, 0f,0f, -3f, 0f, 0f, 0, 0, 1, 0);



            Matrix.multiplyMM(matrix, 0,mProjectionMatrix , 0, mViewMatrix, 0);

            glGraphics.setVertexShaderCode("attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}");
            glGraphics.setFragmentShaderCode("precision mediump float;"  +
                    " uniform vec4 vColor;" +
                    " void main() {" +
                    " gl_FragColor = vColor;" +
                    " }");

            mProgram = glGraphics.getGLProgram();
            coordsPerVertex = glGraphics.getCoordsPerVertex();
            vertexStride = glGraphics.getVertexStride();
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    triangleCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());


            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);

            final int vertexCount = triangleCoords.length / coordsPerVertex;

            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            GLES20.glVertexAttribPointer(mPositionHandle, coordsPerVertex,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);


            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

            GLES20.glUniform4fv(mColorHandle, 1, color, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }

        @Override
        public void update(float deltaTime) {
            game.getInput().getTouchEvents();
            game.getInput().getKeyEvents();
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
