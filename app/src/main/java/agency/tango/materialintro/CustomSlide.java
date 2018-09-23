package agency.tango.materialintro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import agency.tango.materialintroscreen.fragments.SlideFragmentBase;

public class CustomSlide extends SlideFragmentBase {

    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_slide, container, false);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCanMoveFurther(checkBox.isChecked());
            }
        });
        setCanMoveFurther(checkBox.isChecked());
        cantMoveFurtherErrorString = R.string.error_message;
        return view;
    }
}