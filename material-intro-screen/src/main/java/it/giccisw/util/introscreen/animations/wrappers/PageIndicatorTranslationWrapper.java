package it.giccisw.util.introscreen.animations.wrappers;

import android.view.View;

import it.giccisw.util.introscreen.animations.ViewTranslationWrapper;
import it.giccisw.util.introscreen.animations.translations.DefaultPositionTranslation;
import it.giccisw.util.introscreen.animations.translations.ExitDefaultTranslation;

public class PageIndicatorTranslationWrapper extends ViewTranslationWrapper {
    public PageIndicatorTranslationWrapper(View view) {
        super(view);

        setDefaultTranslation(new DefaultPositionTranslation())
                .setExitTranslation(new ExitDefaultTranslation());
    }
}