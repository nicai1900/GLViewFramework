package com.nicaiya.glview.anim;

import com.nicaiya.glview.glrender.GLCanvas;
import com.nicaiya.glview.utils.Utils;

public class AlphaAnimation extends CanvasAnimation {

    private final float mStartAlpha;
    private final float mEndAlpha;
    private float mCurrentAlpha;

    public AlphaAnimation(float from, float to) {
        mStartAlpha = from;
        mEndAlpha = to;
        mCurrentAlpha = from;
    }

    @Override
    public void apply(GLCanvas canvas) {
        canvas.multiplyAlpha(mCurrentAlpha);
    }

    @Override
    public int getCanvasSaveFlags() {
        return GLCanvas.SAVE_FLAG_ALPHA;
    }

    @Override
    protected void onCalculate(float progress) {
        mCurrentAlpha = Utils.clamp(mStartAlpha
                + (mEndAlpha - mStartAlpha) * progress, 0f, 1f);
    }
}
