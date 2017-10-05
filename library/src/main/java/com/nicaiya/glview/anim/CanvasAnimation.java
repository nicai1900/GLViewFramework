package com.nicaiya.glview.anim;

import com.nicaiya.glview.glrender.GLCanvas;

public abstract class CanvasAnimation extends Animation {

    public abstract int getCanvasSaveFlags();
    public abstract void apply(GLCanvas canvas);
}
