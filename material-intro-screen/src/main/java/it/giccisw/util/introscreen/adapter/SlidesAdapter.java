package it.giccisw.util.introscreen.adapter;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import it.giccisw.util.introscreen.BuildConfig;
import it.giccisw.util.introscreen.fragments.SlideFragmentBase;

/** Stores the fragments for the slides, organized in sections */
public class SlidesAdapter extends PagerAdapter {

    /** The log tag */
    private static final String TAG = "FragmentPagerAdapter";

    /** The fragment manager */
    private final FragmentManager fragmentManager;

    /** The current transaction, if any */
    private FragmentTransaction curTransaction = null;

    /** The current primary item, if any */
    private Fragment currentPrimaryItem = null;

    /** List of available slides, all of them */
    private final List<SlideFragmentBase> fragments = new ArrayList<>();

    /** If true, the user can pass past that slide */
    private final List<Boolean> canPass = new ArrayList<>();

    /** The number of accessible slide */
    private int numAccessibleSlides;

    /** Saved states of destroyed fragments */
    private ArrayList<Fragment.SavedState> savedStates = new ArrayList<>();

    public SlidesAdapter(FragmentManager fm) {
        fragmentManager = fm;
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @SuppressWarnings("ReferenceEquality")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        if (curTransaction == null) curTransaction = fragmentManager.beginTransaction();

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
            curTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            if (BuildConfig.DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
            curTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != currentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (curTransaction == null) {
            curTransaction = fragmentManager.beginTransaction();
        }
        if (BuildConfig.DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                + " v=" + ((Fragment)object).getView());
        curTransaction.detach((Fragment)object);
    }

    @SuppressWarnings("ReferenceEquality")
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                currentPrimaryItem.setUserVisibleHint(false);
            }
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            currentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (curTransaction != null) {
            curTransaction.commitNowAllowingStateLoss();
            curTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    /**
     * Return a unique identifier for the item at the given position.
     *
     * <p>The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.</p>
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    public long getItemId(int position) {
        return position;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }










    /**
     * Return the Fragment associated with a specified position.
     */
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
        savedStates.add(null);

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
