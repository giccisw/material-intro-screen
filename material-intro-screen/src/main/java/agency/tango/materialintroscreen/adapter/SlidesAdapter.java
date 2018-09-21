package agency.tango.materialintroscreen.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import agency.tango.materialintroscreen.fragments.SlideFragmentBase;

/** Stores the fragments for the slides */
public class SlidesAdapter extends FragmentPagerAdapter {

    /** List of available slides */
    private ArrayList<SlideFragmentBase> fragments = new ArrayList<>();

    public SlidesAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public SlideFragmentBase getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addItem(SlideFragmentBase fragment) {
        fragments.add(getCount(), fragment);
        notifyDataSetChanged();
    }

    public int getLastItemPosition() {
        return getCount() - 1;
    }

    public boolean isLastSlide(int position) {
        return position == getCount() - 1;
    }

    public boolean shouldFinish(int position) {
        return position == getCount() && getItem(getCount() - 1).canMoveFurther();
    }

    public boolean shouldLockSlide(int position) {
        SlideFragmentBase fragment = getItem(position);
        return !fragment.canMoveFurther() || fragment.hasNeededPermissionsToGrant();
    }
}
