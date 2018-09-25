package it.giccisw.util.introscreen.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.giccisw.util.introscreen.MaterialIntroActivity;
import it.giccisw.util.introscreen.R;
import it.giccisw.util.introscreen.parallax.ParallaxFragment;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SlideFragmentBase extends ParallaxFragment {

    /** Request code which shall be used for permission requests */
    private static final int PERMISSIONS_REQUEST_CODE = 15621;

    /** Colors for background and buttons */
    @ColorRes
    protected int backgroundColor = R.color.mis_default_background_color,
                  buttonsColor = R.color.mis_default_buttons_color;

    /** Message which shall be used for impassable slide */
    @StringRes
    protected int cantMoveFurtherErrorString = R.string.mis_impassable_slide;

    /** Requested permissions */
    protected String[] mandatoryPermissions, optionalPermissions;

    /** Strings which shall be used for permission requests */
    @StringRes
    protected int grantPermissionStringRes = R.string.mis_grant_permissions,
            grantPermissionErrorStringRes = R.string.mis_please_grant_permissions;

    @ColorRes
    public int backgroundColor() {
        return backgroundColor;
    }

    @ColorRes
    public int buttonsColor() {
        return buttonsColor;
    }

    @StringRes
    public int cantMoveFurtherErrorString() {
        return cantMoveFurtherErrorString;
    }

    @StringRes
    public int grantPermissionStringRes() {
        return grantPermissionStringRes;
    }

    @StringRes
    public int grantPermissionErrorStringRes() {
        return grantPermissionErrorStringRes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean hasNeededPermissionsToGrant = fixForPermissions();
            if (hasNeededPermissionsToGrant) showError(getString(grantPermissionErrorStringRes));
        }
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** Called when the DPAD_CENTER key is pressed */
    public void onDPadCenter() {}

    /**
     * Changes the slide behaviour depending on the permission state
     * @return true if there are needed permissions to be granted
     */
    protected boolean fixForPermissions() {
        boolean hasNeededPermissionsToGrant = hasNeededPermissionsToGrant();
        setCanMoveFurther(!hasNeededPermissionsToGrant);
        return hasNeededPermissionsToGrant;
    }

    /**
     * Changes the possibility to pass over this slide
     * @param canMoveFurther If true the user can pass over this slide
     */
    protected void setCanMoveFurther(boolean canMoveFurther) {
        ((MaterialIntroActivity)getActivity()).setCanMoveFurther(this, canMoveFurther);
    }

    /**
     * Shows a snackbar with the specified error string
     * @param error The error string which shall be shown
     */
    protected void showError(String error) {
        final Activity activity = getActivity();
        assert activity != null;
        Snackbar.make(activity.findViewById(R.id.coordinator_layout_slide), error, Snackbar.LENGTH_SHORT)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        activity.findViewById(R.id.navigation_view).setTranslationY(0f);
                        super.onDismissed(snackbar, event);
                    }
                }).show();
    }

    /**
     * Checks if there are any permission to be granted
     * @return True if there is at least a permission which shall be granted
     */
    protected boolean hasAnyPermissionsToGrant()
    {
        boolean hasPermissionToGrant = hasPermissionsToGrant(mandatoryPermissions);
        if (!hasPermissionToGrant) hasPermissionToGrant = hasPermissionsToGrant(optionalPermissions);
        return hasPermissionToGrant;
    }

    /**
     * Checks if there are any mandatory permission to be granted
     * @return True if there is at least a mandatory permission which shall be granted
     */
    protected boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(mandatoryPermissions);
    }

    /** Starts the permissions ask process */
    protected void askForPermissions()
    {
        // build the list of permissions not yet granted
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        if (mandatoryPermissions != null) for (String permission : mandatoryPermissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED)
                notGrantedPermissions.add(permission);
        }
        if (optionalPermissions != null) for (String permission : optionalPermissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED)
                notGrantedPermissions.add(permission);
        }

        // ask for the missing permissions
        ActivityCompat.requestPermissions(getActivity(),
                notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]),
                PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Checks if the passed permissions are available or not
     * @param permissions The permissions which shall be checked
     * @return true if at least one of the passed permission is not granted
     */
    private boolean hasPermissionsToGrant(String[] permissions)
    {
        // permission requests was introduced in Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        if (permissions != null)
            for (String permission : permissions)
                if (!TextUtils.isEmpty(permission) &&
                    ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                        return true;

        return false;
    }
}