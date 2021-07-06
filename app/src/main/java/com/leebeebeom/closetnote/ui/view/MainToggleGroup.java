package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.FragmentMainButtonBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class MainToggleGroup extends MaterialButtonToggleGroup {
    private static final Integer BTN_TOP_ID = 101492;
    private static final Integer BTN_BOTTOM_ID = 101493;
    private static final Integer BTN_OUTER_ID = 101494;
    private static final Integer BTN_ETC_ID = 101495;
    @Getter
    private final List<MaterialButton> mButtons = new ArrayList<>();
    private final List<Integer> btnIds = Arrays.asList(BTN_TOP_ID, BTN_BOTTOM_ID, BTN_OUTER_ID, BTN_ETC_ID);


    public MainToggleGroup(@NonNull @NotNull Context context) {
        super(context);
        init();
    }

    public MainToggleGroup(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainToggleGroup(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        List<String> names = Arrays.asList(context.getString(R.string.all_caps_top), context.getString(R.string.all_caps_bottom),
                context.getString(R.string.all_caps_outer), context.getString(R.string.all_caps_etc));
        for (int i = 0; i < 4; i++) {
            FragmentMainButtonBinding binding = FragmentMainButtonBinding.inflate(LayoutInflater.from(getContext()), this, false);
            MaterialButton button = binding.btn;
            button.setText(names.get(i));
            button.setId(btnIds.get(i));
            mButtons.add(button);
            addView(button);
        }

        addOnButtonCheckedListener();
    }

    private void addOnButtonCheckedListener() {
        int colorPrimary = getColorPrimary();
        int colorControl = getColorControl();
        ColorStateList buttonTextOriginColor = mButtons.get(3).getTextColors();

        addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            int childCount = group.getChildCount();
            for (int i = 0; i < 4; i++) {
                mButtons.get(i).setBackgroundColor(Color.TRANSPARENT);
                mButtons.get(i).setTextColor(buttonTextOriginColor);
            }
            if (isChecked) {
                if (checkedId == BTN_TOP_ID)
                    setButtonCheckedColor(0, colorControl, colorPrimary);
                else if (checkedId == BTN_BOTTOM_ID)
                    setButtonCheckedColor(1, colorControl, colorPrimary);
                else if (checkedId == BTN_OUTER_ID)
                    setButtonCheckedColor(2, colorControl, colorPrimary);
                else if (checkedId == BTN_ETC_ID)
                    setButtonCheckedColor(3, colorControl, colorPrimary);
            }
        });
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private int getColorControl() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.myColorControl, typedValue, true);
        return typedValue.data;
    }

    private void setButtonCheckedColor(int i, int colorControl, int colorPrimary) {
        mButtons.get(i).setBackgroundColor(colorControl);
        mButtons.get(i).setTextColor(colorPrimary);
    }

}
