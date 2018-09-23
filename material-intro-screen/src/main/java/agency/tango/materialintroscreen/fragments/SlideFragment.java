package agency.tango.materialintroscreen.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import agency.tango.materialintroscreen.R;

public class SlideFragment extends SlideFragmentBase {

    public static final String BACKGROUND_COLOR = "background_color";
    public static final String BUTTONS_COLOR = "buttons_color";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String NEEDED_PERMISSIONS = "needed_permission";
    public static final String POSSIBLE_PERMISSIONS = "possible_permission";
    public static final String IMAGE = "image";
    public static final String GRANT_PERMISSION_MESSAGE = "grant_permission_message";
    public static final String GRANT_PERMISSION_ERROR = "grant_permission_error";

    @StringRes
    private int grantPermissionStringRes;

    @StringRes
    private int grantPermissionErrorStringRes;

    private String[] neededPermissions;
    private String[] possiblePermissions;

    public static SlideFragment createInstance(Bundle bundle) {
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mis_fragment_slide, container, false);
        TextView titleTextView = view.findViewById(R.id.txt_title_slide);
        TextView descriptionTextView = view.findViewById(R.id.txt_description_slide);
        ImageView imageView = view.findViewById(R.id.image_slide);

        Bundle bundle = getArguments();
        assert bundle != null;
        backgroundColor = bundle.getInt(BACKGROUND_COLOR);
        buttonsColor = bundle.getInt(BUTTONS_COLOR);
        int image = bundle.getInt(IMAGE, 0);
        String title = bundle.getString(TITLE);
        String description = bundle.getString(DESCRIPTION);
        neededPermissions = bundle.getStringArray(NEEDED_PERMISSIONS);
        possiblePermissions = bundle.getStringArray(POSSIBLE_PERMISSIONS);
        grantPermissionStringRes = bundle.getInt(GRANT_PERMISSION_MESSAGE);
        grantPermissionErrorStringRes = bundle.getInt(GRANT_PERMISSION_ERROR);

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        if (image != 0) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), image));
            imageView.setVisibility(View.VISIBLE);
        }

        if (grantPermissionStringRes == 0)
            grantPermissionStringRes = R.string.mis_grant_permissions;

        if (grantPermissionErrorStringRes == 0)
            grantPermissionErrorStringRes = R.string.mis_please_grant_permissions;

        return view;
    }

    @Override
    public String[] possiblePermissions() {
        return possiblePermissions;
    }

    @Override
    public String[] neededPermissions() {
        return neededPermissions;
    }

    @Override
    public int grantPermissionStringRes() {
        return grantPermissionStringRes;
    }

    @Override
    public int grantPermissionErrorStringRes() {
        return grantPermissionErrorStringRes;
    }
}