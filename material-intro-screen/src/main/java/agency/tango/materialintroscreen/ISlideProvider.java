package agency.tango.materialintroscreen;

import agency.tango.materialintroscreen.fragments.SlideFragmentBase;

/** Provides slides and their info */
public interface ISlideProvider {

    /**
     * Shall return a new SlideFragment for the specified position and type
     * @param position The slide's position
     * @param type The type of the slide
     * @return A new SlideFragment
     */
    SlideFragmentBase getNewSlide(int position, int type);
}
