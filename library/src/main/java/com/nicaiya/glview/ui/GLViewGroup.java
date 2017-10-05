package com.nicaiya.glview.ui;

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.nicaiya.glview.anim.Animation;
import com.nicaiya.glview.anim.CanvasAnimation;
import com.nicaiya.glview.glrender.GLCanvas;

import java.util.ArrayList;

public abstract class GLViewGroup extends GLView {

    private static final String TAG = "GLViewGroup";

    private ArrayList<GLView> mChildren;
    private GLView mMotionTarget;

    public GLViewGroup() {
    }

    public void addView(GLView view, LayoutParams params) {
        if (view.mParent != null) {
            throw new IllegalStateException();
        }
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(view);
        view.mParent = this;

        if (mRoot != null) {
            view.onAttachToRoot(mRoot);
        }

        view.setLayoutParams(params);
    }

    public boolean removeView(GLView view) {
        if (mChildren == null) {
            return false;
        }
        if (mChildren.remove(view)) {
            removeOneView(view);
            requestLayout();
            return true;
        }
        return false;
    }

    public void removeAllView() {
        if (mChildren == null) {
            return;
        }
        for (int i = 0, n = mChildren.size(); i < n; ++i) {
            removeOneView(mChildren.get(i));
        }
        mChildren.clear();
        requestLayout();
    }

    private void removeOneView(GLView view) {
        if (mMotionTarget == view) {
            long now = SystemClock.uptimeMillis();
            MotionEvent cancelEvent = MotionEvent.obtain(
                    now, now, MotionEvent.ACTION_CANCEL, 0, 0, 0);
            dispatchTouchEvent(cancelEvent);
            cancelEvent.recycle();
        }
        view.onDetachFromRoot();
        view.mParent = null;
    }

    public GLView getChild(int index) {
        if (mChildren == null) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return mChildren.get(index);
    }

    public int getChildCount() {
        return mChildren == null ? 0 : mChildren.size();
    }

    protected void onSetLayoutParams(GLView child, GLViewGroup.LayoutParams layoutParams) {
        requestLayout();
    }

    protected void measureChildren() {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final GLView child = getChild(i);
            if (child.getVisibility() == GLView.VISIBLE) {
                measureChild(child);
            }
        }
    }

    protected void measureChild(GLView child) {
        final GLViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp != null) {
            child.measure(lp.width, lp.height);
        }
    }

    @Override
    protected abstract void onLayout(boolean changeSize, int left, int top, int right, int bottom);

    @Override
    protected boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        if (mMotionTarget != null) {
            if (action == MotionEvent.ACTION_DOWN) {
                MotionEvent cancel = MotionEvent.obtain(event);
                cancel.setAction(MotionEvent.ACTION_CANCEL);
                dispatchTouchEvent(cancel, x, y, mMotionTarget, false);
                mMotionTarget = null;
            } else {
                dispatchTouchEvent(event, x, y, mMotionTarget, false);
                if (action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_UP) {
                    mMotionTarget = null;
                }
                return true;
            }
        }
        if (action == MotionEvent.ACTION_DOWN) {
            // in the reverse rendering order
            for (int i = getChildCount() - 1; i >= 0; --i) {
                GLView child = getChild(i);
                if (child.getVisibility() != GLView.VISIBLE) continue;
                if (dispatchTouchEvent(event, x, y, child, true)) {
                    mMotionTarget = child;
                    return true;
                }
            }
        }
        return onTouchEvent(event);
    }

    protected boolean dispatchTouchEvent(MotionEvent event, int x, int y, GLView component,
                                         boolean checkBounds) {
        Rect rect = component.mBounds;
        int left = rect.left;
        int top = rect.top;
        if (!checkBounds || rect.contains(x, y)) {
            event.offsetLocation(-left, -top);
            if (component.dispatchTouchEvent(event)) {
                event.offsetLocation(left, top);
                return true;
            }
            event.offsetLocation(left, top);
        }
        return false;
    }

    @Override
    protected void onAttachToRoot(GLRoot root) {
        super.onAttachToRoot(root);
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            getChild(i).onAttachToRoot(root);
        }
    }

    @Override
    protected void onDetachFromRoot() {
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            getChild(i).onDetachFromRoot();
        }
        super.onDetachFromRoot();
    }

    @Override
    protected void onVisibilityChanged(int visibility) {
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            GLView child = getChild(i);
            if (child.getVisibility() == GLView.VISIBLE) {
                child.onVisibilityChanged(visibility);
            }
        }
    }

    @Override
    protected void render(GLCanvas canvas) {
        super.render(canvas);
        canvas.save();
        for (int i = 0, n = getChildCount(); i < n; ++i) {
            GLView child = getChild(i);
            renderChild(canvas, child);
        }
        canvas.restore();
    }

    protected void renderChild(GLCanvas canvas, GLView view) {
        if (view.getVisibility() != GLView.VISIBLE
                && view.mAnimation == null) return;

        int xoffset = view.mBounds.left - mScrollX;
        int yoffset = view.mBounds.top - mScrollY;

        canvas.translate(xoffset, yoffset);

        Animation anim = view.mAnimation;
        if (anim != null && anim instanceof CanvasAnimation) {
            CanvasAnimation canvasAnimation = (CanvasAnimation) anim;
            canvas.save(canvasAnimation.getCanvasSaveFlags());
            if (canvasAnimation.calculate(AnimationTime.get())) {
                invalidate();
            } else {
                view.mAnimation = null;
            }
            canvasAnimation.apply(canvas);
        }
        view.render(canvas);
        if (anim != null) canvas.restore();
        canvas.translate(-xoffset, -yoffset);
    }

    public static class LayoutParams {

        public int width;
        public int height;

        public LayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

}
