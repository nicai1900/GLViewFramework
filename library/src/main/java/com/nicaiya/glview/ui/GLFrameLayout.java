package com.nicaiya.glview.ui;

public class GLFrameLayout extends GLViewGroup {

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;

        for (int i = 0; i < count; i++) {
            GLView child = getChild(i);
            measureChild(child);
            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        setMeasuredSize(maxWidth, maxHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            GLView child = getChild(i);
            child.layout(l, t, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

}
