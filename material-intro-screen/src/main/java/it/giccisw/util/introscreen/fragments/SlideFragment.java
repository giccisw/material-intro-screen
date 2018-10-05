package it.giccisw.util.introscreen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.giccisw.util.introscreen.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SlideFragment extends SlideFragmentBase {

    /** The log tag */
    private final static String TAG = "SlideFragment";

    static final String BACKGROUND_COLOR = "background_color";
    static final String BUTTONS_COLOR = "buttons_color";
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String MANDATORY_PERMISSIONS = "mandatory_permission";
    static final String OPTIONAL_PERMISSIONS = "optional_permission";
    static final String IMAGE = "image";
    static final String GRANT_PERMISSION_MESSAGE = "grant_permission_message";
    static final String GRANT_PERMISSION_ERROR = "grant_permission_error";

    public static SlideFragment createInstance(Bundle bundle) {
        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    /** The message button */
    protected Button messageButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

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
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), image));
            imageView.setVisibility(View.VISIBLE);
        }

        messageButton.setTextColor(ContextCompat.getColor(getContext(), backgroundColor));

        fixForPermissions(hasMandatoryPermissionsToGrant(), hasOptionalPermissionsToGrant());

        return view;
    }

    @Override
    public void onDPadCenter() {
        messageButton.performClick();
    }

    @Override
    protected void fixForPermissions(boolean hasMandatoryPermissionsToGrant, boolean hasOptionalPermissionsToGrant)
    {
        // show the button if there are any permission which shall be granted
        boolean hasAnyPermissionToGrant = hasMandatoryPermissionsToGrant || hasOptionalPermissionsToGrant;
        messageButton.setText(grantPermissionStringRes);
        messageButton.setVisibility(hasAnyPermissionToGrant ? View.VISIBLE : View.INVISIBLE);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermissions();
            }
        });
    }
}