package it.giccisw.util.introscreen.animations.wrappers;

import android.view.View;

import it.giccisw.util.introscreen.animations.ViewTranslationWrapper;
import it.giccisw.util.introscreen.animations.translations.AlphaTranslation;
import it.giccisw.util.introscreen.animations.translations.DefaultAlphaTranslation;

public class ViewPagerTranslationWrapper extends ViewTranslationWrapper {
    public ViewPagerTranslationWrapper(View view) {
        super(view);

        setDefaultTranslation(new DefaultAlphaTranslation())
                .setExitTranslation(new AlphaTranslation());
    }
}