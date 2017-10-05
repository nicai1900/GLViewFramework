package com.nicaiya.glview.ui;

import android.content.Context;
import android.graphics.Matrix;

import com.nicaiya.glview.anim.Animation;
import com.nicaiya.glview.glrender.GLCanvas;

public interface GLRoot {

    // Listener will be called when GL is idle AND before each frame.
    // Mainly used for uploading textures.
    interface OnGLIdleListener {
        boolean onGLIdle(GLCanvas canvas, boolean renderRequested);
    }

    void addOnGLIdleListener(OnGLIdleListener listener);

    void registerLaunchedAnimation(Animation animation);

    void requestRenderForced();

    void requestRender();

    void requestLayoutContentPane();

    void lockRenderThread();

    void unlockRenderThread();

    void setContentPane(GLView content);

    void setOrientationSource(OrientationSource source);

    int getDisplayRotation();

    int getCompensation();

    Matrix getCompensationMatrix();

    void freeze();

    void unfreeze();

    void setLightsOutMode(boolean enabled);

    Context getContext();
}
