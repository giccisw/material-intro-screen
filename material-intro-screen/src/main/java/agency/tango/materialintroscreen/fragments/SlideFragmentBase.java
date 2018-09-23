package agency.tango.materialintroscreen.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public String[] possiblePermissions() {
        return new String[0];
    }

    public String[] neededPermissions() {
        return new String[0];
    }

    @StringRes
    public int grantPermissionStringRes() {
        return R.string.mis_grant_permissions;
    }

    @StringRes
    public int grantPermissionErrorStringRes() {
        return R.string.mis_please_grant_permissions;
    }

    public boolean hasAnyPermissionsToGrant()
    {
        boolean hasPermissionToGrant = hasPermissionsToGrant(neededPermissions());
        if (!hasPermissionToGrant) hasPermissionToGrant = hasPermissionsToGrant(possiblePermissions());
        return hasPermissionToGrant;
    }

    public boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(neededPermissions());
    }

    public void askForPermissions()
    {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();

        if (neededPermissions() != null) {
            for (String permission : neededPermissions()) {
                if (!TextUtils.isEmpty(permission)) {
                    if (ContextCompat.checkSelfPermission(getContext(), permission)
                            != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permission);
                    }
                }
            }
        }
        if (possiblePermissions() != null) {
            for (String permission : possiblePermissions()) {
                if (!TextUtils.isEmpty(permission)) {
                    if (ContextCompat.checkSelfPermission(getContext(), permission)
                            != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permission);
                    }
                }
            }
        }

        String[] permissionsToGrant = removeEmptyAndNullStrings(notGrantedPermissions);
        ActivityCompat
                .requestPermissions(getActivity(), permissionsToGrant, PERMISSIONS_REQUEST_CODE);
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

    private String[] removeEmptyAndNullStrings(final ArrayList<String> permissions) {
        List<String> list = new ArrayList<>(permissions);
        list.removeAll(Collections.singleton(null));
        return list.toArray(new String[list.size()]);
    }

    private boolean isAndroidVersionNotSupportingPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}