package agency.tango.materialintroscreen;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.BackButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.NextButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.PageIndicatorTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.SkipButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.ViewPagerTranslationWrapper;
import agency.tango.materialintroscreen.behaviours.MessageButtonBehaviour;
import agency.tango.materialintroscreen.fragments.SlideFragmentBase;
import agency.tango.materialintroscreen.listeners.IFinishListener;
import agency.tango.materialintroscreen.listeners.IPageScrolledListener;
import agency.tango.materialintroscreen.listeners.IPageSelectedListener;
import agency.tango.materialintroscreen.listeners.MessageButtonBehaviourOnPageSelected;
import agency.tango.materialintroscreen.listeners.ViewBehavioursOnPageChangeListener;
import agency.tango.materialintroscreen.listeners.click.PermissionNotGrantedClickListener;
import agency.tango.materialintroscreen.listeners.scroll.ParallaxScrollListener;
import agency.tango.materialintroscreen.widgets.InkPageIndicator;
import agency.tango.materialintroscreen.widgets.OverScrollViewPager;
import agency.tango.materialintroscreen.widgets.SwipeableViewPager;

@SuppressWarnings("unused")
public abstract class MaterialIntroActivity extends AppCompatActivity {

    // the views
    private SwipeableViewPager viewPager;
    private InkPageIndicator pageIndicator;
    private SlidesAdapter adapter;
    private ImageButton backButton;
    private ImageButton skipButton;
    private ImageButton nextButton;
    private CoordinatorLayout coordinatorLayout;
    private Button messageButton;
    private LinearLayout navigationView;
    private OverScrollViewPager overScrollLayout;

    /** Calculates color tansitions */
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private ViewTranslationWrapper nextButtonTranslationWrapper;
    private ViewTranslationWrapper backButtonTranslationWrapper;
    private ViewTranslationWrapper pageIndicatorTranslationWrapper;
    private ViewTranslationWrapper viewPagerTranslationWrapper;
    private ViewTranslationWrapper skipButtonTranslationWrapper;

    private MessageButtonBehaviourOnPageSelected messageButtonBehaviourOnPageSelected;

    private View.OnClickListener permissionNotGrantedClickListener;
    private View.OnClickListener finishScreenClickListener;

    /** Configuration of message buttons */
    private SparseArray<MessageButtonBehaviour> messageButtonBehaviours = new SparseArray<>();

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
        overScrollLayout = findViewById(R.id.view_pager_slides);
        viewPager = overScrollLayout.getOverScrollView();
        pageIndicator = findViewById(R.id.indicator);
        backButton = findViewById(R.id.button_back);
        nextButton = findViewById(R.id.button_next);
        skipButton = findViewById(R.id.button_skip);
        messageButton = findViewById(R.id.button_message);
        coordinatorLayout = findViewById(R.id.coordinator_layout_slide);
        navigationView = findViewById(R.id.navigation_view);

        // create the adapter for the slides
        adapter = new SlidesAdapter(getSupportFragmentManager());

        nextButtonTranslationWrapper = new NextButtonTranslationWrapper(nextButton);
        backButtonTranslationWrapper = new BackButtonTranslationWrapper(backButton);
        pageIndicatorTranslationWrapper = new PageIndicatorTranslationWrapper(pageIndicator);
        viewPagerTranslationWrapper = new ViewPagerTranslationWrapper(viewPager);
        skipButtonTranslationWrapper = new SkipButtonTranslationWrapper(skipButton);

        messageButtonBehaviourOnPageSelected = new MessageButtonBehaviourOnPageSelected(
                messageButton, adapter, messageButtonBehaviours);

        // register the finish action for the over scroll layout
        overScrollLayout.registerFinishListener(new IFinishListener() {
            @Override
            public void onFinish() {
                performFinish();
            }
        });

        // configure the view pager
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.registerSlideErrorHandler(new ISlideErrorHandler() {
            @Override
            public void handleError() {
                errorOccurred(adapter.getItem(viewPager.getCurrentItem()));
            }
        });
        viewPager.addOnPageChangeListener(new ViewBehavioursOnPageChangeListener(adapter)
                .registerViewTranslationWrapper(nextButtonTranslationWrapper)
                .registerViewTranslationWrapper(backButtonTranslationWrapper)
                .registerViewTranslationWrapper(pageIndicatorTranslationWrapper)
                .registerViewTranslationWrapper(viewPagerTranslationWrapper)
                .registerViewTranslationWrapper(skipButtonTranslationWrapper)

                .registerOnPageScrolled(new IPageScrolledListener() {
                    @Override
                    public void pageScrolled(final int position, float offset) {
                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter.getItem(position).hasNeededPermissionsToGrant()
                                        || !adapter.canMoveFurther(position)) {
                                    viewPager.setCurrentItem(position, true);
                                    pageIndicator.clearJoiningFractions();
                                }
                            }
                        });
                    }
                })
                .registerOnPageScrolled(new ColorTransitionScrollListener())
                .registerOnPageScrolled(new ParallaxScrollListener(adapter))

                .registerPageSelectedListener(messageButtonBehaviourOnPageSelected)
                .registerPageSelectedListener(new IPageSelectedListener() {
                    @Override
                    public void pageSelected(int position) {
                        nextButtonBehaviour(position, adapter.getItem(position));

                        if (adapter.shouldFinish(position)) {
                            performFinish();
                        }
                    }
                }));

        // attach the page indicator to the view pager
        pageIndicator.setViewPager(viewPager);

        permissionNotGrantedClickListener = new PermissionNotGrantedClickListener(this,
                nextButtonTranslationWrapper);
        finishScreenClickListener = new FinishScreenClickListener();

        // show back button by default
        setBackButtonVisible();

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                if (adapter.getCount() == 0) {
                    finish();
                } else {
                    int currentItem = viewPager.getCurrentItem();
                    messageButtonBehaviourOnPageSelected.pageSelected(currentItem);
                    nextButtonBehaviour(currentItem, adapter.getItem(currentItem));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        SlideFragmentBase fragment = adapter.getItem(viewPager.getCurrentItem());
        boolean hasPermissionToGrant = fragment.hasNeededPermissionsToGrant();
        if (!hasPermissionToGrant) {
//            viewPager.setSwipingRightAllowed(true);
            nextButtonBehaviour(viewPager.getCurrentItem(), fragment);
            messageButtonBehaviourOnPageSelected.pageSelected(viewPager.getCurrentItem());
        } else {
            showPermissionsNotGrantedError();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        moveBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (messageButtonBehaviours.get(viewPager.getCurrentItem()) != null)
                    messageButton.performClick();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                int position = viewPager.getCurrentItem();
                if (adapter.isLastSlide(position) && adapter.canMoveFurther(position)) {
                    performFinish();
                } else if (adapter.shouldLockSlide(position)) {
                    errorOccurred(adapter.getItem(position));
                } else {
                    viewPager.moveToNextPage();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                moveBack();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showPermissionsNotGrantedError() {
        showError(getString(adapter.getItem(viewPager.getCurrentItem()).grantPermissionErrorStringRes()));
    }

    /**
     * Add SlideFragmentBase to IntroScreen
     * @param slideFragmentBase Fragment to add
     */
    public void addSlide(SlideFragmentBase slideFragmentBase) {
        adapter.setSlide(slideFragmentBase, true);
    }

    /**
     * Add SlideFragment to IntroScreen
     * @param slideFragmentBase      Fragment to add
     * @param messageButtonBehaviour Add behaviour for message button
     */
    public void addSlide(SlideFragmentBase slideFragmentBase,
            MessageButtonBehaviour messageButtonBehaviour) {
        adapter.setSlide(slideFragmentBase, true);
        messageButtonBehaviours.put(adapter.getLastItemPosition(), messageButtonBehaviour);
    }

    public void canMoveChange(SlideFragmentBase fragment, boolean canMoveFurther)
    {
        adapter.setSlide(fragment, canMoveFurther);
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
                        showError(adapter.getItem(position).cantMoveFurtherErrorMessage());
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getPreviousItem(), true);
            }
        });
    }

    /** Hides back  button */
    public void hideBackSkipButton() {
        backButton.setVisibility(View.INVISIBLE);
        skipButton.setVisibility(View.GONE);
    }

    /**
     * Get translation wrapper for next button
     * @return ViewTranslationWrapper
     */
    public ViewTranslationWrapper getNextButtonTranslationWrapper() {
        return nextButtonTranslationWrapper;
    }

    /**
     * Get translation wrapper for back button
     * @return ViewTranslationWrapper
     */
    public ViewTranslationWrapper getBackButtonTranslationWrapper() {
        return backButtonTranslationWrapper;
    }

    /**
     * Get translation wrapper for page indicator
     * @return ViewTranslationWrapper
     */
    public ViewTranslationWrapper getPageIndicatorTranslationWrapper() {
        return pageIndicatorTranslationWrapper;
    }

    /**
     * Get translation wrapper for view pager
     *
     * @return ViewTranslationWrapper
     */
    public ViewTranslationWrapper getViewPagerTranslationWrapper() {
        return viewPagerTranslationWrapper;
    }

    /**
     * Get translation wrapper for skip button
     * @return ViewTranslationWrapper
     */
    public ViewTranslationWrapper getSkipButtonTranslationWrapper() {
        return skipButtonTranslationWrapper;
    }

    /**
     * Set if last screen should be able to exit with alpha transition
     * @param enableAlphaExitTransition should enable alpha exit transition
     */
    public void enableLastSlideAlphaExitTransition(boolean enableAlphaExitTransition) {
        viewPager.alphaExitTransitionEnabled(enableAlphaExitTransition);
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

    @SuppressWarnings("PointlessBooleanExpression")
    private void nextButtonBehaviour(final int position, final SlideFragmentBase fragment) {
        boolean hasPermissionToGrant = fragment.hasNeededPermissionsToGrant();
        if (hasPermissionToGrant) {
            nextButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mis_ic_next));
            nextButton.setOnClickListener(permissionNotGrantedClickListener);
        } else if (adapter.isLastSlide(position)) {
            nextButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mis_ic_finish));
            nextButton.setOnClickListener(finishScreenClickListener);
        } else {
            nextButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mis_ic_next));
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.canMoveFurther(position)) {
                        viewPager.moveToNextPage();
                    } else {
                        errorOccurred(fragment);
                    }
                }
            });
        }
    }

    private void performFinish() {
        onLastSlidePassed();
        finish();
    }

    private void moveBack() {
        if (viewPager.getCurrentItem() == 0) {
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getPreviousItem(), true);
        }
    }

    private void errorOccurred(SlideFragmentBase slideFragmentBase) {
        nextButtonTranslationWrapper.error();
        showError(slideFragmentBase.cantMoveFurtherErrorMessage());
    }

    private void showError(String error) {
        Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_SHORT)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        navigationView.setTranslationY(0f);
                        super.onDismissed(snackbar, event);
                    }
                }).show();
    }

    private int getBackgroundEvaluatedColor(int position, float positionOffset) {
        return (int) argbEvaluator.evaluate(positionOffset, getBackgroundColor(position),
                getBackgroundColor(position + 1));
    }

    private int getButtonsEvaluatedColor(int position, float positionOffset) {
        return (int) argbEvaluator
                .evaluate(positionOffset, getButtonsColor(position), getButtonsColor(position + 1));
    }

    @ColorInt
    private int getColorFromRes(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private int getButtonsColor(int position) {
        return getColorFromRes(adapter.getItem(position).buttonsColor());
    }

    private int getBackgroundColor(int position) {
        return getColorFromRes(adapter.getItem(position).backgroundColor());
    }

    public void setCanMoveFurther(SlideFragmentBase fragmentBase, boolean canMoveFurther)
    {
        adapter.setSlide(fragmentBase, canMoveFurther);
    }

    private class ColorTransitionScrollListener implements IPageScrolledListener {

        @Override
        public void pageScrolled(int position, float offset) {
            if (position < adapter.getCount() - 1) {
                setViewsColor(position, offset);
            } else if (adapter.getCount() == 1 || isOnLastSlide(position, offset)) {
                viewPager.setBackgroundColor(getBackgroundColor(position));
                messageButton.setTextColor(getBackgroundColor(position));
                pageIndicator.setPageIndicatorColor(getButtonsColor(position));

                tintButtons(ColorStateList.valueOf(getButtonsColor(position)));
            }
        }

        private void setViewsColor(int position, float offset) {
            int backgroundColor = getBackgroundEvaluatedColor(position, offset);
            viewPager.setBackgroundColor(backgroundColor);
            messageButton.setTextColor(backgroundColor);

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

        private void tintButtons(ColorStateList color) {
            ViewCompat.setBackgroundTintList(nextButton, color);
            ViewCompat.setBackgroundTintList(backButton, color);
            ViewCompat.setBackgroundTintList(skipButton, color);
        }
    }

    private class FinishScreenClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int n = adapter.getLastItemPosition();
            SlideFragmentBase slideFragment = adapter.getItem(n);
            if (!adapter.canMoveFurther(n)) {
                errorOccurred(slideFragment);
            } else {
                performFinish();
            }
        }
    }
}