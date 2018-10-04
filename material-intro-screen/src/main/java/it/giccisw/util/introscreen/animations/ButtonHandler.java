package it.giccisw.util.introscreen.animations;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;

import androidx.annotation.FloatRange;
import it.giccisw.util.introscreen.R;

public class ButtonHandler {

    /** The view of the button */
    protected View button;

    /** The default Y translation offset */
    protected int yOffset;

    protected Animator activateAnimator, deactivateAnimator;

    protected boolean active;


    public ButtonHandler(View button) {
        this.button = button;
        yOffset = button.getResources().getDimensionPixelOffset(R.dimen.mis_y_button_offset);
        activateAnimator = AnimatorInflater.loadAnimator(button.getContext(), R.animator.mis_enter);
        activateAnimator.setTarget(button);
        deactivateAnimator = AnimatorInflater.loadAnimator(button.getContext(), R.animator.mis_exit);
        deactivateAnimator.setTarget(button);
    }

    public void activate(@FloatRange(from = 0, to = 1.0) float percentage)
    {
        button.setTranslationY((1f - percentage) * yOffset);
        button.setAlpha(percentage);
        active = percentage == 1f;
        button.setActivated(active);
    }

    public void activate(boolean active)
    {
        if (this.active == active) return;
        this.active = active;
        (active ? activateAnimator : deactivateAnimator).start();
        button.setActivated(active);
    }
}
