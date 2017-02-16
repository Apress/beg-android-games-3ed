package com.badlogic.androidgames.framework.impl;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class GLGraphics {
    GLSurfaceView glView;
    GLES20 gl;
    private final int COORDS_PER_VERTEX = 3;
    private final int COORDS_PER_TEXTURE = 2;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int textureStride = COORDS_PER_TEXTURE * 4;

    private  String vertexShaderCode = "";
    private  String fragmentShaderCode = "";


    public GLGraphics(GLSurfaceView glView) {
        this.glView = glView;
    }

    public GLES20 getGL() {
        return gl;
    }

    public void setGL(GLES20 gl) {
        this.gl = gl;
    }

    public int getWidth() {
        return glView.getWidth();
    }

    public int getHeight() {
        return glView.getHeight();
    }

    public int getCoordsPerVertex(){
        return COORDS_PER_VERTEX;
    }

    public int getCoordsPerTexture(){
        return COORDS_PER_TEXTURE;
    }

    public int getVertexStride(){
        return vertexStride;
    }

    public int getTextureStride(){
        return textureStride;
    }

    public void clearScreen(float red, float green, float blue, float alpha){
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(red, green, blue, alpha);
    }

    public int getGLProgram() {

        int vertexShader= gl.glCreateShader(GLES20.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        int fragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        int mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        return  mProgram;
    }

    public void setVertexShaderCode(String shaderCode){
        this.vertexShaderCode = shaderCode;
    }

    public void setFragmentShaderCode(String shaderCode){
        this.fragmentShaderCode = shaderCode;
    }

}
