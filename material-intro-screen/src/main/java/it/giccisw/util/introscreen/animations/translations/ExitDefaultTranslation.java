package it.giccisw.util.introscreen.animations.translations;

import android.view.View;

import it.giccisw.util.introscreen.R;
import it.giccisw.util.introscreen.animations.IViewTranslation;
import androidx.annotation.FloatRange;

public class ExitDefaultTranslation implements IViewTranslation {
    @Override
    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
        view.setTranslationY(percentage * view.getResources().getDimensionPixelOffset(R.dimen.mis_y_offset));
    }
}
