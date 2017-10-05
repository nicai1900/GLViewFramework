package com.nicaiya.glview.ui;

import android.graphics.Color;

import com.nicaiya.glview.glrender.GLCanvas;
import com.nicaiya.glview.texture.StringTexture;

/**
 * GLTextView
 * Created by zhengjie on 2017/10/5.
 */
public class GLTextView extends GLView {

    private String mText;
    private float mTextSize;
    private int mTextColor;

    private StringTexture mStringTexture;

    public GLTextView() {
        mTextColor = Color.BLACK;
        mTextSize = 20;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        mText = text;
        if (mStringTexture != null) {
            mStringTexture.recycle();
            mStringTexture = null;
        }
        mStringTexture = StringTexture.newInstance(mText, mTextSize, mTextColor);
        requestLayout();
    }

    public void setTextSize(float size) {
        if (mTextSize != size) {
            if (mStringTexture != null) {
                mStringTexture.recycle();
                mStringTexture = null;
            }
            mTextSize = size;
            mStringTexture = StringTexture.newInstance(mText, mTextSize, mTextColor);
            requestLayout();
        }
    }

    public void setTextColor(int color) {
        if (mTextColor != color) {
            if (mStringTexture != null) {
                mStringTexture.recycle();
                mStringTexture = null;
            }
            mTextColor = color;
            mStringTexture = StringTexture.newInstance(mText, mTextSize, mTextColor);
            invalidate();
        }
    }

    @Override
    protected void render(GLCanvas canvas) {
        super.render(canvas);
        if (mStringTexture != null) {
            mStringTexture.draw(canvas, 0, 0);
        }
    }

}
