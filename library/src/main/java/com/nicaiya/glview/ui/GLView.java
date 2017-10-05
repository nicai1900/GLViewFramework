package com.nicaiya.glview.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.nicaiya.glview.anim.Animation;
import com.nicaiya.glview.glrender.GLCanvas;
import com.nicaiya.glview.texture.BasicTexture;
import com.nicaiya.glview.texture.BitmapTexture;
import com.nicaiya.glview.texture.ResourceTexture;
import com.nicaiya.glview.utils.Utils;

/**
 * GLView is a UI component. It can render to a GLCanvas and accept touch
 * events. A GLView may have zero or more child GLView and they form a tree
 * structure. The rendering and event handling will pass through the tree
 * structure.
 * <p>
 * A GLView tree should be attached to a GLRoot before event dispatching and
 * rendering happens. GLView asks GLRoot to re-render or re-layout the
 * GLView hierarchy using requestRender() and requestLayoutContentPane().
 * <p>
 * The render() method is called in a separate thread. Before calling
 * dispatchTouchEvent() and layout(), GLRoot acquires a lock to avoid the
 * rendering thread running at the same time. If there are other entry points
 * from main thread (like a Handler) in your GLView, you need to call
 * lockRendering() if the rendering thread should not run at the same time.
 */
public class GLView {

    private static final String TAG = "GLView";

    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;

    private static final int FLAG_INVISIBLE = 1;
    private static final int FLAG_SET_MEASURED_SIZE = 2;
    private static final int FLAG_LAYOUT_REQUESTED = 4;

    private OnClickListener mOnClickListener;

    private OnLongClickListener mOnLongClickListener;

    private OnZOrderChangedListener mOnZOrderChangedListener;

    private OnTouchListener mOnTouchListener;

    protected final Rect mBounds;
    protected final Rect mPaddings;

    protected GLRoot mRoot;
    protected GLView mParent;

    protected Animation mAnimation;

    protected int mViewFlags = 0;
    protected int mZOrder;
    protected int mMeasuredWidth = 0;
    protected int mMeasuredHeight = 0;

    private int mLastWidthSpec = -1;
    private int mLastHeightSpec = -1;

    protected int mScrollY = 0;
    protected int mScrollX = 0;
    protected int mScrollHeight = 0;
    protected int mScrollWidth = 0;

    //private float[] mBackgroundColor;
    protected BasicTexture mBackground;
    protected int mBackgroundColor = Color.BLACK;
    protected int mBackgroundResource = 0;

    protected GLViewGroup.LayoutParams mLayoutParams;

    public GLView() {
        mBounds = new Rect();
        mPaddings = new Rect();
        mViewFlags = 0;
    }

    public void setLayoutParams(GLViewGroup.LayoutParams params) {
        mLayoutParams = params;
        if (mParent instanceof GLViewGroup) {
            ((GLViewGroup) mParent).onSetLayoutParams(this, params);
        }
        requestLayout();
    }

    public GLViewGroup.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public void startAnimation(Animation animation) {
        GLRoot root = getGLRoot();
        if (root == null) throw new IllegalStateException();
        mAnimation = animation;
        if (mAnimation != null) {
            mAnimation.start();
            root.registerLaunchedAnimation(mAnimation);
        }
        invalidate();
    }

    // Sets the visiblity of this GLView (either GLView.VISIBLE or
    // GLView.INVISIBLE).
    public void setVisibility(int visibility) {
        if (visibility == getVisibility()) return;
        if (visibility == VISIBLE) {
            mViewFlags &= ~FLAG_INVISIBLE;
        } else {
            mViewFlags |= FLAG_INVISIBLE;
        }
        onVisibilityChanged(visibility);
        invalidate();
    }

    // Returns GLView.VISIBLE or GLView.INVISIBLE
    public int getVisibility() {
        return (mViewFlags & FLAG_INVISIBLE) == 0 ? VISIBLE : INVISIBLE;
    }

    public void setZorder(int newOrder) {
        int oldOrder = mZOrder;
        if (oldOrder != newOrder) {
            mZOrder = newOrder;
            if (mOnZOrderChangedListener != null) {
                mOnZOrderChangedListener.onZOrderChanged(this, mZOrder, oldOrder);
            }
        }
    }

    public int getZOrder() {
        return mZOrder;
    }

    // This should only be called on the content pane (the topmost GLView).
    public void attachToRoot(GLRoot root) {
        Utils.assertTrue(mParent == null && mRoot == null);
        onAttachToRoot(root);
    }

    // This should only be called on the content pane (the topmost GLView).
    public void detachFromRoot() {
        Utils.assertTrue(mParent == null && mRoot != null);
        onDetachFromRoot();
    }

    public Rect bounds() {
        return mBounds;
    }

    public int getWidth() {
        return mBounds.right - mBounds.left;
    }

    public int getHeight() {
        return mBounds.bottom - mBounds.top;
    }

    public void setPaddings(int left, int top, int right, int bottom) {
        mPaddings.set(left, top, right, bottom);
    }

    public GLRoot getGLRoot() {
        return mRoot;
    }

    // Request re-rendering of the view hierarchy.
    // This is used for animation or when the contents changed.
    public void invalidate() {
        GLRoot root = getGLRoot();
        if (root != null) root.requestRender();
    }

    // Request re-layout of the view hierarchy.
    public void requestLayout() {
        mViewFlags |= FLAG_LAYOUT_REQUESTED;
        mLastHeightSpec = -1;
        mLastWidthSpec = -1;
        if (mParent != null) {
            mParent.requestLayout();
        } else {
            // Is this a content pane ?
            GLRoot root = getGLRoot();
            if (root != null) root.requestLayoutContentPane();
        }
    }

    protected void render(GLCanvas canvas) {
        renderBackground(canvas);
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    public void setBackground(int colorRes) {
        if (mBackgroundResource != colorRes) {
            GLRoot root = getGLRoot();
            if (root != null) {
                if (mBackground != null) {
                    mBackground.recycle();
                    mBackground = null;
                }
                if (colorRes != 0) {
                    mBackgroundResource = colorRes;
                    mBackground = new ResourceTexture(root.getContext(), colorRes);
                }
            }
            invalidate();
        }
    }

    public void setBackground(Bitmap bmp) {
        if (bmp != null) {
            if (mBackground != null) {
                mBackground.recycle();
                mBackground = null;
            }
            mBackground = new BitmapTexture(bmp);
            mBackgroundResource = 0;
            invalidate();
        }
    }

    protected void renderBackground(GLCanvas canvas) {
        if (mBackground != null) {
            mBackground.draw(canvas, 0, 0, getWidth(), getHeight());
        } else {
            canvas.fillRect(0.0f, 0.9f, getWidth(), getHeight(), mBackgroundColor);
        }
    }

    protected boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    protected boolean dispatchTouchEvent(MotionEvent event) {
        int visibility = getVisibility();
        if (visibility == VISIBLE) {
            if (mOnTouchListener != null) {
                return mOnTouchListener.onTouch(this, event);
            } else {
                return onTouchEvent(event);
            }
        }
        return false;
    }

    public Rect getPaddings() {
        return mPaddings;
    }

    public void layout(int left, int top, int right, int bottom) {
        boolean sizeChanged = setBounds(left, top, right, bottom);
        mViewFlags &= ~FLAG_LAYOUT_REQUESTED;
        // We call onLayout no matter sizeChanged is true or not because the
        // orientation may change without changing the size of the View (for
        // example, rotate the device by 180 degrees), and we want to handle
        // orientation change in onLayout.
        onLayout(sizeChanged, left, top, right, bottom);
    }

    private boolean setBounds(int left, int top, int right, int bottom) {
        boolean sizeChanged = (right - left) != (mBounds.right - mBounds.left)
                || (bottom - top) != (mBounds.bottom - mBounds.top);
        mBounds.set(left, top, right, bottom);
        return sizeChanged;
    }

    void measure(int widthSpec, int heightSpec) {
        if (widthSpec == mLastWidthSpec && heightSpec == mLastHeightSpec
                && (mViewFlags & FLAG_LAYOUT_REQUESTED) == 0) {
            return;
        }

        mLastWidthSpec = widthSpec;
        mLastHeightSpec = heightSpec;

        mViewFlags &= ~FLAG_SET_MEASURED_SIZE;
        onMeasure(widthSpec, heightSpec);
        if ((mViewFlags & FLAG_SET_MEASURED_SIZE) == 0) {
            throw new IllegalStateException(getClass().getName()
                    + " should call setMeasuredSize() in onMeasure()");
        }
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredSize(widthSpec, heightSpec);
    }

    void setMeasuredSize(int width, int height) {
        mViewFlags |= FLAG_SET_MEASURED_SIZE;
        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    protected void onLayout(boolean changeSize, int left, int top, int right, int bottom) {
    }

    /**
     * Gets the bounds of the given descendant that relative to this view.
     */
    public boolean getBoundsOf(GLView descendant, Rect out) {
        int xoffset = 0;
        int yoffset = 0;
        GLView view = descendant;
        while (view != this) {
            if (view == null) return false;
            Rect bounds = view.mBounds;
            xoffset += bounds.left;
            yoffset += bounds.top;
            view = view.mParent;
        }
        out.set(xoffset, yoffset, xoffset + descendant.getWidth(),
                yoffset + descendant.getHeight());
        return true;
    }

    protected void onVisibilityChanged(int visibility) {
    }

    protected void onAttachToRoot(GLRoot root) {
        mRoot = root;
    }

    protected void onDetachFromRoot() {
        mRoot = null;
    }

    public void lockRendering() {
        if (mRoot != null) {
            mRoot.lockRenderThread();
        }
    }

    public void unlockRendering() {
        if (mRoot != null) {
            mRoot.unlockRenderThread();
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    public void setOnZOrderChangedListener(OnZOrderChangedListener listener) {
        mOnZOrderChangedListener = listener;
    }

    public interface OnZOrderChangedListener {
        void onZOrderChanged(GLView view, int newOrder, int oldOrder);
    }

    public interface OnTouchListener {
        boolean onTouch(GLView view, MotionEvent event);
    }

    public interface OnClickListener {
        void onClick(GLView view);
    }

    public interface OnLongClickListener {
        void onLongClick(GLView view);
    }

}
