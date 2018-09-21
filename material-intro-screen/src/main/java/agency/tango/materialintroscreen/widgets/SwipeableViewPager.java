package agency.tango.materialintroscreen.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import agency.tango.materialintroscreen.ISlideErrorHandler;
import agency.tango.materialintroscreen.adapter.SlidesAdapter;

/** Disables swiping when needed */
public class SwipeableViewPager extends ViewPager {

    private boolean swipingAllowed;

    private float startPos = 0;
    private int currentIt;
    private boolean alphaExitTransitionEnabled = false;
    private ISlideErrorHandler errorHandler;

    public SwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        swipingAllowed = true;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event)
    {
        switch (event.getActionMasked()) {
            case (MotionEvent.ACTION_DOWN):
                return super.onInterceptTouchEvent(event);
            case (MotionEvent.ACTION_MOVE):
                if (!swipingAllowed) return false;
                return super.onInterceptTouchEvent(event);
            case (MotionEvent.ACTION_UP):
                if (!swipingAllowed) return false;
                return super.onInterceptTouchEvent(event);
            default:
                return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        switch (event.getActionMasked()) {
            case (MotionEvent.ACTION_DOWN):
                startPos = event.getX();
                currentIt = getCurrentItem();
                resolveSwipingRightAllowed();
                return super.onTouchEvent(event);
            case (MotionEvent.ACTION_MOVE):
                if (isSwipingNotAllowed(event)) {
                    errorHandler.handleError();
                    return true;
                }
                return super.onTouchEvent(event);
            case (MotionEvent.ACTION_UP):
                if (isSwipingNotAllowed(event)) {
                    // FIXME
//                    smoothScrollTo(getWidth() * currentIt, 0);
                    errorHandler.handleError();
                    return true;
                }
                startPos = 0;
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public SlidesAdapter getAdapter() {
        return (SlidesAdapter) super.getAdapter();
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return false;
    }

    public void registerSlideErrorHandler(ISlideErrorHandler handler) {
        errorHandler = handler;
    }

    public void moveToNextPage() {
        setCurrentItem(getCurrentItem() + 1, true);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    public int getPreviousItem() {
        return getCurrentItem() - 1;
    }

    public void setSwipingRightAllowed(boolean allowed) {
        swipingAllowed = allowed;
    }

    public void alphaExitTransitionEnabled(boolean alphaExitTransitionEnabled) {
        this.alphaExitTransitionEnabled = alphaExitTransitionEnabled;
    }

    public boolean alphaExitTransitionEnabled() {
        return alphaExitTransitionEnabled && swipingAllowed;
    }

    private boolean isSwipingNotAllowed(MotionEvent event) {
        return !swipingAllowed && startPos - event.getX() > 16;
    }

    private void resolveSwipingRightAllowed() {
        if (getAdapter().shouldLockSlide(getCurrentItem())) {
            setSwipingRightAllowed(false);
        } else {
            setSwipingRightAllowed(true);
        }
    }
}