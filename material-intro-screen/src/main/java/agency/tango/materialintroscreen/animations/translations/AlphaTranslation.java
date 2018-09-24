package agency.tango.materialintroscreen.animations.translations;

import android.view.View;

import agency.tango.materialintroscreen.animations.IViewTranslation;
import androidx.annotation.FloatRange;

public class AlphaTranslation implements IViewTranslation {
    @Override
    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
        view.setAlpha(1.0f - percentage);
    }
}
