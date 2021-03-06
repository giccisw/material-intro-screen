package it.giccisw.util.introscreen;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import it.giccisw.util.introscreen.adapter.SlidesAdapter;
import it.giccisw.util.introscreen.animations.ButtonHandler;
import it.giccisw.util.introscreen.fragments.SlideFragmentBase;
import it.giccisw.util.introscreen.listeners.IPageScrolledListener;
import it.giccisw.util.introscreen.listeners.ParallaxScrollListener;
import it.giccisw.util.introscreen.widgets.InkPageIndicator;

public class MaterialIntroActivity extends AppCompatActivity {

    /** The log tag */
    private final static String TAG = "MaterialIntroActivity";

    // the views
    private ViewPager viewPager;
    private InkPageIndicator pageIndicator;
    private SlidesAdapter adapter;
    private ImageButton backButton;
    private ImageButton skipButton;
    private ImageButton nextButton;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout navigationView;

    /** Button handlers */
    ButtonHandler backButtonHandler, nextButtonHandler;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // translucent windows where available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set our layout
        setContentView(R.layout.mis_activity_material_intro);

        // get views
        viewPager = findViewById(R.id.mis_swipeable_view_pager);
        pageIndicator = findViewById(R.id.indicator);
        backButton = findViewById(R.id.button_back);
        nextButton = findViewById(R.id.button_next);
        skipButton = findViewById(R.id.button_skip);
        coordinatorLayout = findViewById(R.id.coordinator_layout_slide);
        navigationView = findViewById(R.id.navigation_view);

        // create the adapter for the slides
        adapter = new SlidesAdapter(getSupportFragmentManager());

        // create the button handlers
        backButtonHandler = new ButtonHandler(backButton);
        nextButtonHandler = new ButtonHandler(nextButton);

        // attach actions to buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveForward();
            }
        });

        // configure the view pager
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        ViewPagerObserver viewPagerObserver = new ViewPagerObserver();
        viewPager.addOnPageChangeListener(viewPagerObserver);
        viewPager.getAdapter().registerDataSetObserver(viewPagerObserver);

        // attach the page indicator to the view pager
        pageIndicator.setViewPager(viewPager);

        // show back button by default
        setBackButtonVisible();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (adapter.getCount() == 0) finish();
        else {
            int currentItem = viewPager.getCurrentItem();
            nextButtonBehaviour(currentItem, adapter.getItem(currentItem));
        }
    }

    @Override
    public void onBackPressed() {
        // if back key has been pressed, move to previous slide if any
        moveBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                getCurrentSlide().onDPadCenter();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                moveForward();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveBack();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    /**
     * Returns the current slide
     * @return The current slide
     */
    protected SlideFragmentBase getCurrentSlide() {
        return adapter.getItem(viewPager.getCurrentItem());
    }

    /**
     * Add SlideFragmentBase to IntroScreen
     * @param slideFragmentBase Fragment to add
     */
    public void addSlide(SlideFragmentBase slideFragmentBase) {
        adapter.addSlide(slideFragmentBase);
    }

    /**
     * Changes the possibility to pass over the specified slide
     * @param fragmentBase The slide's fragment
     * @param canMoveFurther true if the slide can be passed
     */
    public void setCanMoveFurther(SlideFragmentBase fragmentBase, boolean canMoveFurther) {
        adapter.setSlide(fragmentBase, canMoveFurther);
    }

    /** Set skip button instead of back button */
    public void setSkipButtonVisible() {
        backButton.setVisibility(View.GONE);

        skipButton.setVisibility(View.VISIBLE);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int position = viewPager.getCurrentItem(); position < adapter.getCount();
                        position++) {
                    if (!adapter.canMoveFurther(position)) {
                        viewPager.setCurrentItem(position, true);
                        showError(getString(adapter.getItem(position).cantMoveFurtherErrorString()));
                        return;
                    }
                }
                viewPager.setCurrentItem(adapter.getLastItemPosition(), true);
            }
        });
    }

    /** Set back button visible */
    public void setBackButtonVisible() {
        skipButton.setVisibility(View.GONE);

        backButton.setVisibility(View.VISIBLE);
    }

    /** Hides back  button */
    public void hideBackSkipButton() {
        backButton.setVisibility(View.INVISIBLE);
        skipButton.setVisibility(View.GONE);
    }

    /**
     * Show snackbar message
     * @param message Message which will be visible to user
     */
    public void showMessage(String message) {
        showError(message);
    }

    /** Override in order to perform some action after passing last slide */
    public void onLastSlidePassed() {
        // This method is intentionally empty, because we didn't want to make this method
        // abstract as it would force user to implement this, even if he wouldn't like to.
    }

    private void nextButtonBehaviour(final int position, final SlideFragmentBase fragment)
    {
        if (adapter.isLastSlide(position)) {
            nextButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mis_ic_finish));
        } else {
            nextButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mis_ic_next));
        }
    }

    private void performFinish() {
        onLastSlidePassed();
        finish();
    }

    /** Move to previous slide */
    protected void moveBack()
    {
        if (viewPager.getCurrentItem() > 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }

    /** Move to next slide */
    protected void moveForward()
    {
        int position = viewPager.getCurrentItem();
        if (!adapter.canMoveFurther(position)) errorOccurred(adapter.getItem(position));
        else if (position == adapter.getTotalCount() - 1) performFinish();
        else viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    private void errorOccurred(SlideFragmentBase slideFragmentBase) {
//        nextButtonTranslationWrapper.error();
        showError(getString(slideFragmentBase.cantMoveFurtherErrorString()));
    }

    private void showError(String error) {
        Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_SHORT)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        navigationView.setTranslationY(0f);
                        super.onDismissed(snackbar, event);
                    }
                }).show();
    }

    /** Reacts to changes in the ViewPager position */
    private class ViewPagerObserver extends DataSetObserver implements ViewPager.OnPageChangeListener {

        /** Handlers for views translations */
        private IPageScrolledListener colorTransitionListener = new ColorTransitionScrollListener(),
                parallaxListener = new ParallaxScrollListener(adapter);

        /** Scroll state */
        private int state;

        /** Selected page, if state != 0 */
        private int selected = -1;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (BuildConfig.DEBUG) Log.d(TAG, "onPageScrolled position=" + position +
                    " offset=" + positionOffset +
                    " pixels=" + positionOffsetPixels);

            // back button
            if (position == 0) {
                // we are moving from/to the first slide
                backButtonHandler.activate(positionOffset);
            }

            // next button
            if (state == 0) nextButtonHandler.activate(adapter.canMoveFurther(position));
            else {
                int x = (adapter.isLastSlide(position) || adapter.canMoveFurther(position + 1) ? 1 : 0) -
                        (adapter.canMoveFurther(position) ? 1 : 0);
                if (x == 1) nextButtonHandler.activate(positionOffset);
                else if (x == -1) nextButtonHandler.activate(1 - positionOffset);
            }

            // apply transitions
            colorTransitionListener.pageScrolled(position, positionOffset);
            parallaxListener.pageScrolled(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onPageSelected position=" + position);
            this.selected = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onPageScrollStateChanged state=" + state);
            this.state = state;
            if (state == 0) {
                selected = -1;
            }
        }

        @Override
        public void onChanged() {
            if (BuildConfig.DEBUG) Log.d(TAG, "onChanged");
        }
    }

    private class ColorTransitionScrollListener implements IPageScrolledListener {

        /** Calculates color transitions */
        private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

        @Override
        public void pageScrolled(int position, float offset) {
            if (position < adapter.getCount() - 1) {
                setViewsColor(position, offset);
            } else if (adapter.getCount() == 1 || isOnLastSlide(position, offset)) {
                viewPager.setBackgroundColor(getBackgroundColor(position));
                pageIndicator.setPageIndicatorColor(getButtonsColor(position));

                tintButtons(ColorStateList.valueOf(getButtonsColor(position)));
            }
        }

        private void setViewsColor(int position, float offset) {
            int backgroundColor = getBackgroundEvaluatedColor(position, offset);
            viewPager.setBackgroundColor(backgroundColor);

            int buttonsColor = getButtonsEvaluatedColor(position, offset);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(buttonsColor);
            }
            pageIndicator.setPageIndicatorColor(buttonsColor);

            tintButtons(ColorStateList.valueOf(buttonsColor));
        }

        private boolean isOnLastSlide(int position, float offset) {
            return position == adapter.getLastItemPosition() && offset == 0;
        }

        @ColorInt
        private int getColorFromRes(@ColorRes int color) {
            return ContextCompat.getColor(MaterialIntroActivity.this, color);
        }

        private int getButtonsColor(int position) {
            return getColorFromRes(adapter.getItem(position).buttonsColor());
        }

        private int getBackgroundColor(int position) {
            return getColorFromRes(adapter.getItem(position).backgroundColor());
        }

        private int getBackgroundEvaluatedColor(int position, float positionOffset) {
            return (int) argbEvaluator.evaluate(positionOffset, getBackgroundColor(position),
                    getBackgroundColor(position + 1));
        }

        private int getButtonsEvaluatedColor(int position, float positionOffset) {
            return (int) argbEvaluator
                    .evaluate(positionOffset, getButtonsColor(position), getButtonsColor(position + 1));
        }

        private void tintButtons(ColorStateList color) {
            ViewCompat.setBackgroundTintList(nextButton, color);
            ViewCompat.setBackgroundTintList(backButton, color);
            ViewCompat.setBackgroundTintList(skipButton, color);
        }
    }
}