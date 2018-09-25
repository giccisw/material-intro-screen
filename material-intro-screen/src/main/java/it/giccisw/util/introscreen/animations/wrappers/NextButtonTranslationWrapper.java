package it.giccisw.util.introscreen.animations.wrappers;

import android.view.View;

import it.giccisw.util.introscreen.R;
import it.giccisw.util.introscreen.animations.ViewTranslationWrapper;
import it.giccisw.util.introscreen.animations.translations.DefaultPositionTranslation;
import it.giccisw.util.introscreen.animations.translations.ExitDefaultTranslation;

public class NextButtonTranslationWrapper extends ViewTranslationWrapper {
    public NextButtonTranslationWrapper(View view) {
        super(view);

        setExitTranslation(new ExitDefaultTranslation())
                .setDefaultTranslation(new DefaultPositionTranslation())
                .setErrorAnimation(R.anim.mis_shake_it);
    }
}