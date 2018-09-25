package it.giccisw.util.introscreen.animations.wrappers;

import android.view.View;

import it.giccisw.util.introscreen.animations.ViewTranslationWrapper;
import it.giccisw.util.introscreen.animations.translations.DefaultPositionTranslation;
import it.giccisw.util.introscreen.animations.translations.EnterDefaultTranslation;
import it.giccisw.util.introscreen.animations.translations.ExitDefaultTranslation;

public class SkipButtonTranslationWrapper extends ViewTranslationWrapper {
    public SkipButtonTranslationWrapper(View view) {
        super(view);

        setEnterTranslation(new EnterDefaultTranslation())
                .setDefaultTranslation(new DefaultPositionTranslation())
                .setExitTranslation(new ExitDefaultTranslation());
    }
}