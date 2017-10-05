package com.nicaiya.glview.texture;

import android.util.Log;

import com.nicaiya.glview.glrender.GLCanvas;
import com.nicaiya.glview.glrender.GLId;

import javax.microedition.khronos.opengles.GL11;

public class RawTexture extends BasicTexture {
    private static final String TAG = "RawTexture";

    private final boolean mOpaque;
    private boolean mIsFlipped;

    public RawTexture(int width, int height, boolean opaque) {
        mOpaque = opaque;
        setSize(width, height);
    }

    @Override
    public boolean isOpaque() {
        return mOpaque;
    }

    @Override
    public boolean isFlippedVertically() {
        return mIsFlipped;
    }

    public void setIsFlippedVertically(boolean isFlipped) {
        mIsFlipped = isFlipped;
    }

    public void prepare(GLCanvas canvas) {
        GLId glId = canvas.getGLId();
        mId = glId.generateTexture();
        canvas.initializeTextureSize(this, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
        canvas.setTextureParameters(this);
        mState = STATE_LOADED;
        setAssociatedCanvas(canvas);
    }

    @Override
    public boolean onBind(GLCanvas canvas) {
        if (isLoaded()) return true;
        Log.w(TAG, "lost the content due to context change");
        return false;
    }

    @Override
     public void yield() {
         // we cannot free the texture because we have no backup.
     }

    @Override
    public int getTarget() {
        return GL11.GL_TEXTURE_2D;
    }
}
