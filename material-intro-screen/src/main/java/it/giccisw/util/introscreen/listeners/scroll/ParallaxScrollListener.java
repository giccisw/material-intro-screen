package it.giccisw.util.introscreen.listeners.scroll;

import it.giccisw.util.introscreen.adapter.SlidesAdapter;
import it.giccisw.util.introscreen.fragments.SlideFragmentBase;
import it.giccisw.util.introscreen.listeners.IPageScrolledListener;
import it.giccisw.util.introscreen.parallax.Parallaxable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ParallaxScrollListener implements IPageScrolledListener {

    private SlidesAdapter adapter;

    public ParallaxScrollListener(SlidesAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void pageScrolled(int position, float offset) {
        if (position != adapter.getCount()) {
            Fragment fragment = adapter.getItem(position);
            Fragment fragmentNext = getNextFragment(position);

            if (fragment != null && fragment instanceof Parallaxable) {
                ((Parallaxable) fragment).setOffset(offset);
            }

            if (fragmentNext != null && fragment instanceof Parallaxable) {
                ((Parallaxable) fragmentNext).setOffset(offset - 1);
            }
        }
    }

    @Nullable
    private SlideFragmentBase getNextFragment(int position) {
        if (position < adapter.getLastItemPosition()) {
            return adapter.getItem(position + 1);
        }
        return null;
    }
}