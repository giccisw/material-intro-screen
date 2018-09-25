package it.giccisw.util.introscreen.listeners;

import java.util.ArrayList;
import java.util.List;

import it.giccisw.util.introscreen.adapter.SlidesAdapter;
import it.giccisw.util.introscreen.animations.ViewTranslationWrapper;
import androidx.viewpager.widget.ViewPager;

/** Handles slide page changes */
public class SlideOnPageChangeListener implements ViewPager.OnPageChangeListener {

    private final SlidesAdapter adapter;

    private List<IPageSelectedListener> listeners = new ArrayList<>();
    private List<ViewTranslationWrapper> wrappers = new ArrayList<>();
    private List<IPageScrolledListener> pageScrolledListeners = new ArrayList<>();

    public SlideOnPageChangeListener(SlidesAdapter adapter) {
        this.adapter = adapter;
    }

    public SlideOnPageChangeListener registerPageSelectedListener(
            IPageSelectedListener pageSelectedListener) {
        listeners.add(pageSelectedListener);
        return this;
    }

    public SlideOnPageChangeListener registerViewTranslationWrapper(
            ViewTranslationWrapper wrapper) {
        wrappers.add(wrapper);
        return this;
    }

    public SlideOnPageChangeListener registerOnPageScrolled(
            IPageScrolledListener pageScrolledListener) {
        pageScrolledListeners.add(pageScrolledListener);
        return this;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isFirstSlide(position)) {
            for (ViewTranslationWrapper wrapper : wrappers) {
                wrapper.enterTranslate(positionOffset);
            }
        } else if (adapter.isLastSlide(position)) {
            for (ViewTranslationWrapper wrapper : wrappers) {
                wrapper.exitTranslate(positionOffset);
            }
        } else {
            for (ViewTranslationWrapper wrapper : wrappers) {
                wrapper.defaultTranslate(positionOffset);
            }
        }

        for (IPageScrolledListener pageScrolledListener : pageScrolledListeners) {
            pageScrolledListener.pageScrolled(position, positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (IPageSelectedListener pageSelectedListener : listeners) {
            pageSelectedListener.pageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //This method is intentionally left blank, as it should do nothing
    }

    private boolean isFirstSlide(int position) {
        return position == 0;
    }
}