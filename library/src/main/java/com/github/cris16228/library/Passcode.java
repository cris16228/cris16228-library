package com.github.cris16228.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Passcode extends FrameLayout implements View.OnClickListener {

    private final ArrayList<String> numbers_list = new ArrayList<>();
    private final int buttonColor = 0x00000000;
    public String passcode = "";
    private final ArrayList<String> set_passcode_list = new ArrayList<>();
    private String secondInput = "";
    private String firstInput = "";
    private onPasswordListener onPasswordListener;
    private View dot_1, dot_2, dot_3, dot_4;
    private MaterialButton btn_number_1, btn_number_2, btn_number_3, btn_number_4, btn_number_5, btn_number_6, btn_number_7, btn_number_8, btn_number_9, btn_number_0,
            btn_clear;
    private ImageView lock;
    private char[] code;
    private int background = 0xFFAAAAAA;
    private int overlay = 0xFF448AFF;
    private int error = 0xFFF24055;
    private String firstInputTip = "Enter a passcode of 4 digits";
    private String secondInputTip = "Re-enter new passcode";
    private String wrongLengthTip = "Enter a passcode of 4 digits";
    private String wrongInputTip = "Passcode do not match";
    private String correctInputTip = "Passcode is correct";
    private Drawable code_background;
    private Drawable code_overlay;
    private Drawable code_error;
    private TextView message;

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
            passcode = typedArray.getString(R.styleable.Passcode_pass);
            background = typedArray.getColor(R.styleable.Passcode_passcodeBackground, background);
            overlay = typedArray.getColor(R.styleable.Passcode_passcodeOverlay, overlay);
            error = typedArray.getColor(R.styleable.Passcode_passcodeError, error);
            firstInputTip = typedArray.getString(R.styleable.Passcode_firstInputTip);
            secondInputTip = typedArray.getString(R.styleable.Passcode_secondInputTip);
            wrongLengthTip = typedArray.getString(R.styleable.Passcode_wrongLengthTip);
            wrongInputTip = typedArray.getString(R.styleable.Passcode_wrongInputTip);
            correctInputTip = typedArray.getString(R.styleable.Passcode_correctInputTip);
        } finally {
            typedArray.recycle();
        }
        firstInputTip = firstInputTip == null ? "Enter a passcode of 4 digits" : firstInputTip;
        secondInputTip = secondInputTip == null ? "Re-enter new passcode" : secondInputTip;
        wrongLengthTip = wrongLengthTip == null ? firstInputTip : wrongLengthTip;
        wrongInputTip = wrongInputTip == null ? "Passcode do not match" : wrongInputTip;
        correctInputTip = correctInputTip == null ? "Passcode is correct" : correctInputTip;
        initView(context);
    }

    public Passcode(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        code = new char[4];
        View view = inflate(context, R.layout.layout_passcode, this);

        code_background = ResourcesCompat.getDrawable(getResources(), R.drawable.passcode_background, null);
        if (code_background != null) {
            DrawableCompat.setTint(code_background, Color.parseColor(String.format("#%06X", (0xFFFFFF & background))));
        }
        code_overlay = ResourcesCompat.getDrawable(getResources(), R.drawable.passcode_overlay, null);
        if (code_overlay != null) {
            DrawableCompat.setTint(code_overlay, Color.parseColor(String.format("#%06X", (0xFFFFFF & overlay))));
        }
        code_error = ResourcesCompat.getDrawable(getResources(), R.drawable.passcode_error, null);
        if (code_error != null) {
            DrawableCompat.setTint(code_error, Color.parseColor(String.format("#%06X", (0xFFFFFF & error))));
        }

        message = view.findViewById(R.id.message);
        lock = view.findViewById(R.id.lock);
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
        btn_number_0.setBackgroundColor(buttonColor);
        btn_number_1.setBackgroundColor(buttonColor);
        btn_number_2.setBackgroundColor(buttonColor);
        btn_number_3.setBackgroundColor(buttonColor);
        btn_number_4.setBackgroundColor(buttonColor);
        btn_number_5.setBackgroundColor(buttonColor);
        btn_number_6.setBackgroundColor(buttonColor);
        btn_number_7.setBackgroundColor(buttonColor);
        btn_number_8.setBackgroundColor(buttonColor);
        btn_number_9.setBackgroundColor(buttonColor);
        btn_clear.setBackgroundColor(buttonColor);
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

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    private void passNumber(ArrayList<String> numbers_list) {
        Base64Utils.Base64Decoder decoder = new Base64Utils.Base64Decoder();
        Base64Utils.Base64Encoder encoder = new Base64Utils.Base64Encoder();
        if (numbers_list.size() <= 0) {
            dot_1.setBackgroundResource(R.drawable.passcode_background);
            dot_2.setBackgroundResource(R.drawable.passcode_background);
            dot_3.setBackgroundResource(R.drawable.passcode_background);
            dot_4.setBackgroundResource(R.drawable.passcode_background);
            message.setText(firstInputTip);
        } else {
            if (TextUtils.isEmpty(passcode)) {
                if (TextUtils.isEmpty(firstInput))
                    message.setText(firstInputTip);
                else
                    message.setText(secondInputTip);
                switch (numbers_list.size()) {
                    case 1:
                        dot_1.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 2:
                        dot_2.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 3:
                        dot_3.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 4:
                        dot_1.setBackgroundResource(R.drawable.passcode_overlay);
                        if (TextUtils.isEmpty(firstInput))
                            firstInput = encoder.encrypt(String.valueOf(numbers_list.stream().collect(Collectors.joining()).toCharArray()), Base64.DEFAULT, "");
                        if (TextUtils.isEmpty(secondInput)) {
                            secondInput = encoder.encrypt(String.valueOf(numbers_list.stream().collect(Collectors.joining()).toCharArray()), Base64.DEFAULT, "");
                            if (secondInput.equals(firstInput))
                                onPasswordListener.onPasswordCreated(secondInput);
                            break;
                        }
                        numbers_list.clear();
                        passNumber(numbers_list);
                        break;
                }
            } else
                switch (numbers_list.size()) {
                    case 1:
                        dot_1.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 2:
                        dot_2.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 3:
                        dot_3.setBackgroundResource(R.drawable.passcode_overlay);
                        break;
                    case 4:
                        dot_1.setBackgroundResource(R.drawable.passcode_overlay);
                        code = numbers_list.stream().collect(Collectors.joining()).toCharArray();

                        if (!TextUtils.isEmpty(passcode)) {
                            if (String.valueOf(code).equals(decoder.decrypt(passcode, Base64.DEFAULT))) {
                                lock.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.lock_open, null));
                                message.setText(correctInputTip);
                                new Handler().postDelayed(() -> {
                                    numbers_list.clear();
                                    onPasswordListener.onPasswordMatch();
                                }, 500);
                            } else {
                                dot_1.setBackgroundResource(R.drawable.passcode_error);
                                dot_2.setBackgroundResource(R.drawable.passcode_error);
                                dot_3.setBackgroundResource(R.drawable.passcode_error);
                                dot_4.setBackgroundResource(R.drawable.passcode_error);
                                message.setText(wrongInputTip);
                                new Handler().postDelayed(() -> {
                                    numbers_list.clear();
                                    passNumber(numbers_list);
                                }, 1500);
                                onPasswordListener.onPasswordNotMatch();
                            }
                        }
                        break;
                }
        }
    }

    public interface onPasswordListener {

        void onPasswordMatch();

        void onPasswordNotMatch();

        void onPasswordCreated(String password);
    }

}
