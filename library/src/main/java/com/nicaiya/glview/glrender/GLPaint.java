package com.nicaiya.glview.glrender;

import com.nicaiya.glview.utils.Utils;

public class GLPaint {

    private float mLineWidth = 1.0f;
    private int mColor = 0;

    public void setColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void setLineWidth(float width) {
        Utils.assertTrue(width >= 0);
        mLineWidth = width;
    }

    public float getLineWidth() {
        return mLineWidth;
    }
}
