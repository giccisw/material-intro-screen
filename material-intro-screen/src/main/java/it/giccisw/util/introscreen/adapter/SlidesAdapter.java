package it.giccisw.util.introscreen.adapter;

import android.util.Log;

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
    private final static String TAG = "SlidesAdapter";

    /** List of available slides, all of them */
    private final List<SlideFragmentBase> fragments = new ArrayList<>();

    /** If true, the user can pass past that slide */
    private final List<Boolean> canPass = new ArrayList<>();

    /** The number of accessible slide */
    private int numAccessibleSlides;

    public SlidesAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public SlideFragmentBase getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return numAccessibleSlides;
    }

    @Override
    public int getItemPosition(@NonNull Object object)
    {
        // we do not support remove, so all fragments are unchanged, except those no longer visible
        @SuppressWarnings("SuspiciousMethodCalls")
        int n = fragments.indexOf(object);
        Log.d(TAG, "getItemPosition " + n + " of " + numAccessibleSlides);
        return n < numAccessibleSlides ? POSITION_UNCHANGED : POSITION_NONE;
    }

    /**
     * Returns the total number of slides in all sections
     * @return The total number of slides in all sections
     */
    public int getTotalCount() {
        return fragments.size();
    }

    /**
     * Adds a slide to current section if not present, or updates it if already in list
     * @param fragment The slide to be added
     * @param canMoveFurther If true the user can move pass that slide
     */
    public void setSlide(SlideFragmentBase fragment, boolean canMoveFurther)
    {
        // check if we have it
        int n = fragments.indexOf(fragment);
        if (n == -1) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Adding new slide " + fragment +
                    " canMoveFurther=" + canMoveFurther);
            fragments.add(fragment);
            canPass.add(canMoveFurther);
        }
        else {
            if (BuildConfig.DEBUG) Log.d(TAG, "Changing slide " + fragment +
                    " canMoveFurther=" + canMoveFurther);
            canPass.set(n, canMoveFurther);
        }

        // recalculate the number of accessible slides
        n = canPass.indexOf(false);
        if (n == -1) numAccessibleSlides = fragments.size();
        else numAccessibleSlides = n + 1;

        if (BuildConfig.DEBUG) Log.d(TAG, "Total slides=" + canPass.size() +
                " numAccessibleSlides=" + numAccessibleSlides);

        // notify data set change
        notifyDataSetChanged();
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
