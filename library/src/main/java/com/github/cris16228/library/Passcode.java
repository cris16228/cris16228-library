package com.github.cris16228.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Passcode extends FrameLayout implements View.OnClickListener {

    public String _pass = "";
    onPasswordListener onPasswordListener;
    View dot_1, dot_2, dot_3, dot_4;
    MaterialButton btn_number_1, btn_number_2, btn_number_3, btn_number_4, btn_number_5, btn_number_6, btn_number_7, btn_number_8, btn_number_9, btn_number_0,
            btn_clear;
    ArrayList<String> numbers_list = new ArrayList<>();
    String pass;

    public Passcode(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public Passcode(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Passcode(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Passcode);
        try {
            _pass = typedArray.getString(R.styleable.Passcode_pass);
        } finally {
            typedArray.recycle();
        }
        initView(context);
    }

    public Passcode(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.layout_passcode, this);
        dot_1 = view.findViewById(R.id.dot_1);
        dot_2 = view.findViewById(R.id.dot_2);
        dot_3 = view.findViewById(R.id.dot_3);
        dot_4 = view.findViewById(R.id.dot_4);
        btn_number_0 = view.findViewById(R.id.btn_number_0);
        btn_number_1 = view.findViewById(R.id.btn_number_1);
        btn_number_2 = view.findViewById(R.id.btn_number_2);
        btn_number_3 = view.findViewById(R.id.btn_number_3);
        btn_number_4 = view.findViewById(R.id.btn_number_4);
        btn_number_5 = view.findViewById(R.id.btn_number_5);
        btn_number_6 = view.findViewById(R.id.btn_number_6);
        btn_number_7 = view.findViewById(R.id.btn_number_7);
        btn_number_8 = view.findViewById(R.id.btn_number_8);
        btn_number_9 = view.findViewById(R.id.btn_number_9);
        btn_clear = view.findViewById(R.id.btn_clear);

        btn_number_0.setOnClickListener(this);
        btn_number_1.setOnClickListener(this);
        btn_number_2.setOnClickListener(this);
        btn_number_3.setOnClickListener(this);
        btn_number_4.setOnClickListener(this);
        btn_number_5.setOnClickListener(this);
        btn_number_6.setOnClickListener(this);
        btn_number_7.setOnClickListener(this);
        btn_number_8.setOnClickListener(this);
        btn_number_9.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_number_0) {
            numbers_list.add("0");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_1) {
            numbers_list.add("1");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_2) {
            numbers_list.add("2");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_3) {
            numbers_list.add("3");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_4) {
            numbers_list.add("4");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_5) {
            numbers_list.add("5");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_6) {
            numbers_list.add("6");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_7) {
            numbers_list.add("7");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_8) {
            numbers_list.add("8");
            passNumber(numbers_list);
        } else if (id == R.id.btn_number_9) {
            numbers_list.add("9");
            passNumber(numbers_list);
        } else if (id == R.id.btn_clear) {
            numbers_list.clear();
            passNumber(numbers_list);
        }
    }

    public void onPasswordListener(onPasswordListener _onPasswordListener) {
        onPasswordListener = _onPasswordListener;

    }

    private void passNumber(ArrayList<String> numbers_list) {
        if (numbers_list == null) {
            dot_1.setBackgroundResource(R.drawable.passcode_dot_background);
            dot_2.setBackgroundResource(R.drawable.passcode_dot_background);
            dot_3.setBackgroundResource(R.drawable.passcode_dot_background);
            dot_4.setBackgroundResource(R.drawable.passcode_dot_background);
        } else
            switch (numbers_list.size()) {
                case 1:
                    pass += numbers_list.get(0);
                    dot_1.setBackgroundResource(R.drawable.passcode_dot_overlay);
                    break;
                case 2:
                    pass += numbers_list.get(1);
                    dot_2.setBackgroundResource(R.drawable.passcode_dot_overlay);
                    break;
                case 3:
                    pass += numbers_list.get(2);
                    dot_3.setBackgroundResource(R.drawable.passcode_dot_overlay);
                    break;
                case 4:
                    pass += numbers_list.get(3);
                    dot_4.setBackgroundResource(R.drawable.passcode_dot_overlay);
                    Base64Utils.Base64Decoder decoder = new Base64Utils.Base64Decoder();
                    if (!TextUtils.isEmpty(_pass)) {
                        if (pass.equals(decoder.decrypt(_pass)))
                            onPasswordListener.onPasswordMatch();
                        else
                            onPasswordListener.onPasswordNotMatch();
                    }
                    break;
            }
    }

    public interface onPasswordListener {

        void onPasswordMatch();

        void onPasswordNotMatch();
    }

}
