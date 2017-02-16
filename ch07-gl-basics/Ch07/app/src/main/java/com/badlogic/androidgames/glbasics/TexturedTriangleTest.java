package com.badlogic.androidgames.glbasics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TexturedTriangleTest extends GLGame {
    int mProgram;
    int mPositionHandle;
    int coordsPerVertex;
    int coordsPerTexture;
    int vertexStride;
    int textureStride;
    int mMVPMatrixHandle;
    int textureId;
    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    FloatBuffer textureBuffer;
    float triangleCoords[] = {
            0f, 1f, 0f,   // top
            -1f, -1f,0f,   // bottom left
            1f, -1f, 0f    // bottom right
    };

    final short drawOrder[] = { 0, 1, 2 };
    float texture[] = {
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f,
    };

    public Screen getStartScreen() {
            return new TexturedTriangleScreen(this);
        }

    class TexturedTriangleScreen extends Screen {
        GLGraphics glGraphics;

        public TexturedTriangleScreen(GLGame game) {
            super(game);
            glGraphics = ((GLGame) game).getGLGraphics();
            textureId = this.loadTexture("bobrgb888.png");
        }

        @Override
        public void present(float[] matrix) {
            float[] mViewMatrix =new float[16];

            // Draw background color
            glGraphics.clearScreen(0f,0f,0f,1f);

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mViewMatrix, 0, 0f,0f, -3f, 0f, 0f, 0, 0, 1, 0);

            Matrix.multiplyMM(matrix, 0,mProjectionMatrix , 0, mViewMatrix, 0);

            glGraphics.setVertexShaderCode("uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 TexCoordIn;" +
                    "varying vec2 TexCoordOut;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  TexCoordOut = TexCoordIn;" +
                    "}");
            glGraphics.setFragmentShaderCode("precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D TexCoordIn;" +
                    "uniform float scroll;" +
                    "varying vec2 TexCoordOut;" +
                    "void main() {" +
                    " gl_FragColor = texture2D(TexCoordIn, vec2(TexCoordOut.x ,TexCoordOut.y + scroll));" +
                    "}");

            mProgram = glGraphics.getGLProgram();
            coordsPerVertex = glGraphics.getCoordsPerVertex();
            vertexStride = glGraphics.getVertexStride();
            coordsPerTexture = glGraphics.getCoordsPerTexture();
            textureStride = glGraphics.getTextureStride();
            ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);

            bb = ByteBuffer.allocateDirect(texture.length * 4);
            bb.order(ByteOrder.nativeOrder());
            textureBuffer = bb.asFloatBuffer();
            textureBuffer.put(texture);
            textureBuffer.position(0);

            ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

            final int vertexCount = triangleCoords.length / coordsPerVertex;

            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            GLES20.glEnableVertexAttribArray(mPositionHandle);

            int vsTextureCoord = GLES20.glGetAttribLocation(mProgram, "TexCoordIn");


            GLES20.glVertexAttribPointer(mPositionHandle, coordsPerVertex,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);


            GLES20.glVertexAttribPointer(vsTextureCoord, coordsPerTexture,
                    GLES20.GL_FLOAT, false,
                    textureStride, textureBuffer);

            GLES20.glEnableVertexAttribArray(vsTextureCoord);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            int fsTexture = GLES20.glGetUniformLocation(mProgram, "TexCoordOut");
            int fsScroll = GLES20.glGetUniformLocation(mProgram, "scroll");
            GLES20.glUniform1i(fsTexture, 0);
            GLES20.glUniform1f(fsScroll, 0);
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkGlError("glGetUniformLocation");
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);
            checkGlError("glUniformMatrix4fv");
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

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

        public int loadTexture(String fileName) {
            int[] textures = new int[1];
            InputStream imagestream = null;
            Bitmap bitmap = null;

            android.graphics.Matrix flip = new android.graphics.Matrix();
            flip.postScale(-1f, -1f);

            try {
                imagestream = game.getFileIO().readAsset("bobrgb888.png");

                bitmap = BitmapFactory.decodeStream(imagestream);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), flip, false);

            } catch (Exception e) {

            } finally {
                try {
                    imagestream.close();
                    imagestream = null;
                } catch (IOException e) {
                }
            }

            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();

            return textures[0];
        }
    }
}
