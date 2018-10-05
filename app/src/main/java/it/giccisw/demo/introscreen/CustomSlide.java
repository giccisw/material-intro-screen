package it.giccisw.demo.introscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import it.giccisw.util.introscreen.fragments.SlideFragmentBase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomSlide extends SlideFragmentBase {

    private boolean accepted;
    private CheckBox checkBox;

    @Override
    public boolean onSlideAttached() {
        return false;
    }

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("checkbox", accepted);
    }
}