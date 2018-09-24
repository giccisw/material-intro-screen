package agency.tango.materialintroscreen.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import agency.tango.materialintroscreen.R;

public class SlideFragment extends SlideFragmentBase {

    public static final String BACKGROUND_COLOR = "background_color";
    public static final String BUTTONS_COLOR = "buttons_color";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String MANDATORY_PERMISSIONS = "mandatory_permission";
    public static final String OPTIONAL_PERMISSIONS = "optional_permission";
    public static final String IMAGE = "image";
    public static final String GRANT_PERMISSION_MESSAGE = "grant_permission_message";
    public static final String GRANT_PERMISSION_ERROR = "grant_permission_error";

    public static SlideFragment createInstance(Bundle bundle) {
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    protected Button messageButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mis_fragment_slide, container, false);
        TextView titleTextView = view.findViewById(R.id.mis_txt_title_slide);
        TextView descriptionTextView = view.findViewById(R.id.mis_txt_description_slide);
        ImageView imageView = view.findViewById(R.id.mis_image_slide);
        messageButton = view.findViewById(R.id.mis_button_message);

        Bundle bundle = getArguments();
        assert bundle != null;
        backgroundColor = bundle.getInt(BACKGROUND_COLOR, backgroundColor);
        buttonsColor = bundle.getInt(BUTTONS_COLOR, buttonsColor);
        int image = bundle.getInt(IMAGE, 0);
        String title = bundle.getString(TITLE);
        String description = bundle.getString(DESCRIPTION);
        mandatoryPermissions = bundle.getStringArray(MANDATORY_PERMISSIONS);
        optionalPermissions = bundle.getStringArray(OPTIONAL_PERMISSIONS);
        grantPermissionStringRes = bundle.getInt(GRANT_PERMISSION_MESSAGE, grantPermissionStringRes);
        grantPermissionErrorStringRes = bundle.getInt(GRANT_PERMISSION_ERROR, grantPermissionErrorStringRes);

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        if (image != 0) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), image));
            imageView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean hasNeededPermissionsToGrant = hasNeededPermissionsToGrant();
        setCanMoveFurther(hasNeededPermissionsToGrant);
        messageButton.setText(0);
        messageButton.setVisibility(hasNeededPermissionsToGrant ? View.VISIBLE : View.INVISIBLE);
    }
}