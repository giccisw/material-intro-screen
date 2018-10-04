package it.giccisw.util.introscreen.adapter;

import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import it.giccisw.util.introscreen.BuildConfig;
import it.giccisw.util.introscreen.fragments.SlideFragmentBase;

/** Stores the fragments for the slides, organized in sections */
public class SlidesAdapter extends FragmentPagerAdapter {

    /** The log tag */
    private static final String TAG = "SlidesAdapter";

    /** List of available slides, all of them */
    private final List<SlideFragmentBase> fragments = new ArrayList<>();

    /** If true, the user can pass past that slide */
    private final List<Boolean> canPass = new ArrayList<>();

    /** The number of accessible slide */
    private int numAccessibleSlides;

    public SlidesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public SlideFragmentBase getItem(int position) {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SlideFragmentBase f = (SlideFragmentBase) super.instantiateItem(container, position);
        fragments.set(position, f);
        return f;
    }

    @Override
    public int getCount() {
        return numAccessibleSlides;
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        // we do not support remove, so all fragments are unchanged, except those no longer visible
        //noinspection SuspiciousMethodCalls
        return fragments.indexOf(object) < numAccessibleSlides ? POSITION_UNCHANGED : POSITION_NONE;
    }

    /**
     * Returns the total number of slides in all sections
     * @return The total number of slides in all sections
     */
    public int getTotalCount() {
        return fragments.size();
    }

    /**
     * Adds a slide to current section, if not already present
     * @param fragment The slide to be added
     */
    public void addSlide(SlideFragmentBase fragment)
    {
        // check if we have it
        if (fragments.contains(fragment)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Slide " + fragment + " already present");
            return;
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "Adding new slide " + fragment);
        fragments.add(fragment);
        canPass.add(true);

        // recalculate the number of accessible slides and notify data set change
        recalculateAccessibleSlides();
        notifyDataSetChanged();

        if (BuildConfig.DEBUG) Log.d(TAG, "Total slides=" + canPass.size() +
                " numAccessibleSlides=" + numAccessibleSlides);
    }

    /**
     * Updates a slide if it is already in list
     * @param fragment The slide to be updated
     * @param canMoveFurther If true the user can move pass that slide
     */
    public void setSlide(SlideFragmentBase fragment, boolean canMoveFurther)
    {
        // check if we have it
        int n = fragments.indexOf(fragment);
        if (n == -1) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Slide " + fragment + " not present");
            return;
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "Changing slide " + fragment +
                " canMoveFurther=" + canMoveFurther);
        canPass.set(n, canMoveFurther);

        // recalculate the number of accessible slides and notify data set change if needed
        if (recalculateAccessibleSlides()) notifyDataSetChanged();

        if (BuildConfig.DEBUG) Log.d(TAG, "Total slides=" + canPass.size() +
                " numAccessibleSlides=" + numAccessibleSlides);
    }

    /**
     * Recalculate the number of accessible slides
     * @return true if there was a change
     */
    private boolean recalculateAccessibleSlides()
    {
        int old = numAccessibleSlides;
        int n = canPass.indexOf(false);
        if (n == -1) numAccessibleSlides = fragments.size();
        else numAccessibleSlides = n + 1;

        return old != numAccessibleSlides;
    }

    /**
     * Checks if we canmove further the specified slide
     * @param position The position of the slide to be checked
     * @return true if we can move further it
     */
    public boolean canMoveFurther(int position)
    {
        return canPass.get(position);
    }



    public int getLastItemPosition() {
        return getCount() - 1;
    }

    public boolean isLastSlide(int position) {
        return position == getCount() - 1;
    }
}
