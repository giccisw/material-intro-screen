package it.giccisw.util.introscreen.animations.translations;

import android.view.View;

import it.giccisw.util.introscreen.animations.IViewTranslation;
import androidx.annotation.FloatRange;

public class DefaultAlphaTranslation implements IViewTranslation {
    @Override
    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
        view.setAlpha(1f);
    }
}
