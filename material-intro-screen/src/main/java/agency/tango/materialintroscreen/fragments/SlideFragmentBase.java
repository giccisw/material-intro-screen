package agency.tango.materialintroscreen.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.R;
import agency.tango.materialintroscreen.parallax.ParallaxFragment;

public class SlideFragmentBase extends ParallaxFragment {

    private static final int PERMISSIONS_REQUEST_CODE = 15621;

    /** Colors for background and buttons */
    @ColorRes protected int backgroundColor = R.color.mis_default_background_color,
                  buttonsColor = R.color.mis_default_buttons_color;

    /** Message which shall be used for impassable slide */
    @StringRes protected int cantMoveFurtherErrorString = R.string.mis_impassable_slide;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCanMoveFurther(!hasNeededPermissionsToGrant());
    }

    public void setCanMoveFurther(boolean canMoveFurther) {
        ((MaterialIntroActivity)getActivity()).setCanMoveFurther(this, canMoveFurther);
    }

    public void showError(String error) {
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

    protected boolean hasAnyPermissionsToGrant()
    {
        boolean hasPermissionToGrant = hasPermissionsToGrant(mandatoryPermissions);
        if (!hasPermissionToGrant) hasPermissionToGrant = hasPermissionsToGrant(optionalPermissions);
        return hasPermissionToGrant;
    }

    protected boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(mandatoryPermissions);
    }

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

    private boolean hasPermissionsToGrant(String[] permissions)
    {
        if (isAndroidVersionNotSupportingPermissions()) return false;

        if (permissions != null)
            for (String permission : permissions)
                if (!TextUtils.isEmpty(permission) &&
                    ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                        return true;

        return false;
    }

    private boolean isAndroidVersionNotSupportingPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}