package agency.tango.materialintro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import agency.tango.materialintroscreen.fragments.SlideFragmentBase;

public class CustomSlide extends SlideFragmentBase {

    private boolean accepted;
    private CheckBox checkBox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_slide, container, false);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepted = checkBox.isChecked();
                setCanMoveFurther(accepted);
            }
        });
        if (savedInstanceState != null) accepted = savedInstanceState.getBoolean("checkbox");
        cantMoveFurtherErrorString = R.string.error_message;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCanMoveFurther(accepted);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("checkbox", accepted);
    }
}