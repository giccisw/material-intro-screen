package agency.tango.materialintroscreen.behaviours;

import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.fragments.SlideFragmentBase;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** Handler for the message button handling permissions */
public class MessageButtonBehaviourPermission extends MessageButtonBehaviour {

    /** Arbitrary code used for permission requests */
    private static final int PERMISSIONS_REQUEST_CODE = 15621;

    /** Our activity */
    private final MaterialIntroActivity activity;

    /** Permissions */
    private final String[] mandatoryPermissions, optionalPermissions;

    /**
     * Creates a new permission button behaviour
     * @param activity The activity to be used for the permission request operations
     * @param messageButtonText The message for the button
     * @param mandatoryPermissions The mandatory permissions, could be null
     * @param optionalPermissions The optional permissions, could be null
     */
    public MessageButtonBehaviourPermission(final MaterialIntroActivity activity, String messageButtonText,
                                            final String[] mandatoryPermissions, final String[] optionalPermissions)
    {
        super(messageButtonText, new MessageButtonClickListener() {
            @Override
            public void onClick(Button messageButton) {

                // build the list of permissions not yet granted
                ArrayList<String> notGrantedPermissions = new ArrayList<>();
                if (mandatoryPermissions != null) for (String permission : mandatoryPermissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission)
                                != PackageManager.PERMISSION_GRANTED)
                            notGrantedPermissions.add(permission);
                }
                if (optionalPermissions != null) for (String permission : optionalPermissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission)
                                != PackageManager.PERMISSION_GRANTED)
                            notGrantedPermissions.add(permission);
                }

                String[] permissionsToGrant = removeEmptyAndNullStrings(notGrantedPermissions);
                ActivityCompat.requestPermissions(activity,
                        notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]),
                        PERMISSIONS_REQUEST_CODE);
            }
        });

        // save the parameters
        this.activity = activity;
        this.mandatoryPermissions = mandatoryPermissions;
        this.optionalPermissions = optionalPermissions;
    }

    public boolean hasAnyPermissionsToGrant()
    {
        boolean hasPermissionToGrant = hasPermissionsToGrant(mandatoryPermissions);
        if (!hasPermissionToGrant) hasPermissionToGrant = hasPermissionsToGrant(optionalPermissions);
        return hasPermissionToGrant;
    }

    public boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(mandatoryPermissions);
    }

    @Override
    public String getMessageButtonText(SlideFragmentBase slide)
    {
        boolean hasPermissionToGrant = hasPermissionsToGrant(mandatoryPermissions);
        activity.setCanMoveFurther(slide, !hasPermissionToGrant);
        if (!hasPermissionToGrant) hasPermissionToGrant = hasPermissionsToGrant(optionalPermissions);
        return hasPermissionToGrant ? super.getMessageButtonText(slide) : null;
    }

    private boolean hasPermissionsToGrant(String[] permissions)
    {
        if (isAndroidVersionNotSupportingPermissions()) return false;

        if (permissions != null)
            for (String permission : permissions)
                if (!TextUtils.isEmpty(permission) &&
                        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                    return true;

        return false;
    }

    private static String[] removeEmptyAndNullStrings(final ArrayList<String> permissions) {
        List<String> list = new ArrayList<>(permissions);
        list.removeAll(Collections.singleton(null));
        return list.toArray(new String[list.size()]);
    }

    private static boolean isAndroidVersionNotSupportingPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}
