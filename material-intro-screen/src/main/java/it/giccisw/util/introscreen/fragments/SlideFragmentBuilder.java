package it.giccisw.util.introscreen.fragments;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import static it.giccisw.util.introscreen.fragments.SlideFragment.BACKGROUND_COLOR;
import static it.giccisw.util.introscreen.fragments.SlideFragment.BUTTONS_COLOR;
import static it.giccisw.util.introscreen.fragments.SlideFragment.DESCRIPTION;
import static it.giccisw.util.introscreen.fragments.SlideFragment.GRANT_PERMISSION_ERROR;
import static it.giccisw.util.introscreen.fragments.SlideFragment.GRANT_PERMISSION_MESSAGE;
import static it.giccisw.util.introscreen.fragments.SlideFragment.IMAGE;
import static it.giccisw.util.introscreen.fragments.SlideFragment.MANDATORY_PERMISSIONS;
import static it.giccisw.util.introscreen.fragments.SlideFragment.OPTIONAL_PERMISSIONS;
import static it.giccisw.util.introscreen.fragments.SlideFragment.TITLE;

@SuppressWarnings("unused")
public class SlideFragmentBuilder {

    @ColorRes
    private int backgroundColor;

    @ColorRes
    private int buttonsColor;

    @DrawableRes
    private int image;

    @StringRes
    private int grantPermissionMessage;

    @StringRes
    private int grantPermissionError;

    private String title;
    private String description;
    private String[] mandatoryPermissions;
    private String[] optionalPermissions;

    public SlideFragmentBuilder backgroundColor(@ColorRes int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public SlideFragmentBuilder buttonsColor(@ColorRes int buttonsColor) {
        this.buttonsColor = buttonsColor;
        return this;
    }

    public SlideFragmentBuilder title(String title) {
        this.title = title;
        return this;
    }

    public SlideFragmentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SlideFragmentBuilder mandatoryPermissions(String[] mandatoryPermissions) {
        this.mandatoryPermissions = mandatoryPermissions;
        return this;
    }

    public SlideFragmentBuilder optionalPermissions(String[] optionalPermissions) {
        this.optionalPermissions = optionalPermissions;
        return this;
    }

    public SlideFragmentBuilder image(@DrawableRes int image) {
        this.image = image;
        return this;
    }

    public SlideFragmentBuilder grantPermissionMessage(@StringRes int grantPermissionMessage) {
        this.grantPermissionMessage = grantPermissionMessage;
        return this;
    }

    public SlideFragmentBuilder grantPermissionError(int grantPermissionError) {
        this.grantPermissionError = grantPermissionError;
        return this;
    }


    public SlideFragment build() {
        String missing = "";
        if (backgroundColor == 0) {
            missing += " backgroundColor";
        }
        if (buttonsColor == 0) {
            missing += " buttonsColor";
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Missing required properties in SlideFragmentBuilder:" + missing);
        }

        Bundle bundle = new Bundle();
        if (backgroundColor != 0) bundle.putInt(BACKGROUND_COLOR, backgroundColor);
        if (buttonsColor != 0) bundle.putInt(BUTTONS_COLOR, buttonsColor);
        bundle.putInt(IMAGE, image);
        bundle.putString(TITLE, title);
        bundle.putString(DESCRIPTION, description);
        if (mandatoryPermissions != null) bundle.putStringArray(MANDATORY_PERMISSIONS, mandatoryPermissions);
        if (optionalPermissions != null) bundle.putStringArray(OPTIONAL_PERMISSIONS, optionalPermissions);
        if (grantPermissionMessage != 0) bundle.putInt(GRANT_PERMISSION_MESSAGE, grantPermissionMessage);
        if (grantPermissionError != 0) bundle.putInt(GRANT_PERMISSION_ERROR, grantPermissionError);

        return SlideFragment.createInstance(bundle);
    }
}
