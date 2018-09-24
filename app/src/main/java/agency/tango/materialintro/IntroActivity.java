package agency.tango.materialintro;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.fragments.SlideFragmentBuilder;
import androidx.annotation.Nullable;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSkipButtonVisible();

//        getBackButtonTranslationWrapper()
//                .setEnterTranslation(new IViewTranslation() {
//                    @Override
//                    public void translate(View view,
//                                          @FloatRange(from = 0, to = 1.0) float percentage) {
//                        view.setAlpha(percentage);
//                    }
//                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.first_slide_background)
                        .buttonsColor(R.color.first_slide_buttons)
                        .image(R.drawable.img_office)
                        .title("Organize your time with us")
                        .description("Would you try?")
                        .build()
//                new MessageButtonBehaviour("Work with love",
//                        new MessageButtonBehaviour.MessageButtonClickListener() {
//                    @Override
//                    public void onClick(Button messageButton) {
//                        messageButton.setText("Click me once again!");
//                        showMessage("We provide solutions to make you love your work");
//                    }
//                })
        );

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .title("Want more?")
                .description("Go on")
                .build());

        addSlide(new CustomSlide());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.third_slide_background)
                        .buttonsColor(R.color.third_slide_buttons)
                        .optionalPermissions(
                                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS})
                        .mandatoryPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION})
                        .image(R.drawable.img_equipment)
                        .grantPermissionMessage(R.string.txt_pls_grant_permission)
                        .grantPermissionError(R.string.txt_grant_permission_error)
                        .title("We provide best tools")
                        .description("ever")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.fourth_slide_background)
                .buttonsColor(R.color.fourth_slide_buttons)
                .title("That's it")
                .description("Would you join us?")
                .build());

        // other fragment
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.second_slide_background)
                .buttonsColor(R.color.second_slide_buttons)
                .title("Want more?")
                .description("Go on")
                .build());
    }

    @Override
    public void onLastSlidePassed() {
        Toast.makeText(this, "Try this library in your project! :)", Toast.LENGTH_SHORT).show();
    }
}