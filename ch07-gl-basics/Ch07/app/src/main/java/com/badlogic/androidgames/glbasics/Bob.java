package com.badlogic.androidgames.glbasics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.gl.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

class Bob {
    static final Random rand = new Random();
    public float x, y;
    float dirX, dirY;
    int mProgram;
    int mPositionHandle;
    int coordsPerVertex;
    int coordsPerTexture;
    int vertexStride;
    int textureStride;
    int mMVPMatrixHandle;
    int mColorHandle;
    float[] color = {1f,1f,1f,1f};
    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    FloatBuffer textureBuffer;
    float squareCoords[] = {
            -0.05f,  0.05f, 0.0f,   // top left
            -0.05f, -0.05f, 0.0f,   // bottom left
            0.05f, -0.05f, 0.0f,   // bottom right
            0.05f,  0.05f, 0.0f }; // top right


    float texture[] = {
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f,
    };

    final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
    Texture bobTexture;
    GLGraphics glGraphics;

    public Bob(GLGame game) {
        x = rand.nextFloat();
        y = rand.nextFloat() * 1.5f;
        dirX = .01f;
        dirY = .01f;
        glGraphics = (game).getGLGraphics();
        bobTexture = new Texture(game, "bobrgb888.png");

    }

    public void draw(float[] matrix) {

        float[] mTranslationMatrix = new float[16];

        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, x, y,0);
        Matrix.multiplyMM(matrix, 0,matrix , 0,mTranslationMatrix , 0);

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
                " gl_FragColor = vColor * texture2D(TexCoordIn, vec2(TexCoordOut.x ,TexCoordOut.y + scroll));" +
                "}");

        mProgram = glGraphics.getGLProgram();
        coordsPerVertex = glGraphics.getCoordsPerVertex();
        vertexStride = glGraphics.getVertexStride();
        coordsPerTexture = glGraphics.getCoordsPerTexture();
        textureStride = glGraphics.getTextureStride();
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
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

        final int vertexCount = squareCoords.length / coordsPerVertex;

        GLES20.glUseProgram(mProgram);


        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        int vsTextureCoord = GLES20.glGetAttribLocation(mProgram, "TexCoordIn");

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glVertexAttribPointer(mPositionHandle, coordsPerVertex,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        GLES20.glVertexAttribPointer(vsTextureCoord, coordsPerTexture,
                GLES20.GL_FLOAT, false,
                textureStride, textureBuffer);

        GLES20.glEnableVertexAttribArray(vsTextureCoord);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        bobTexture.bind();

        int fsTexture = GLES20.glGetUniformLocation(mProgram, "TexCoordOut");
        int fsScroll = GLES20.glGetUniformLocation(mProgram, "scroll");
        GLES20.glUniform1i(fsTexture, 0);
        GLES20.glUniform1f(fsScroll, 0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void update(float deltaTime) {
        if (x < 0) {
            dirX = -dirX;
            x = 0;
        }

        if (x > 1) {
            dirX = -dirX;
            x = .8f;
        }

        if (y < 0) {
            dirY = -dirY;
            y = 0;
        }

        if (y > 1.5) {
            dirY = -dirY;
            y = 1.4f;
        }

        x = x + dirX ;
        y = y + dirY ;
    }
}

