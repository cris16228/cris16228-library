package com.github.cris16228.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class PieChart extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    RectF rectf = new RectF(10, 10, 200, 200);
    int temp = 0;
    private float[] value_degree;
    private String[] COLORS;

    public PieChart(Context context, float[] values, String[] colors) {
        super(context);
        if (colors.length < values.length || values.length < colors.length) {
            Log.e(getClass().getSimpleName(), "Error");
            return;
        }
        value_degree = new float[values.length];
        COLORS = new String[]{};
        for (int i = 0; i < values.length; i++) {
            value_degree[i] = values[i];
            COLORS[i] = colors[i];
        }
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private float[] calculateData(float[] data) {
        float total = 0;
        for (float datum : data) {
            total += datum;
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = 360 * (data[i] / total);
        }
        return data;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
            if (i == 0) {
                paint.setColor(Color.parseColor(COLORS[i]));
                canvas.drawArc(rectf, 0, value_degree[i], true, paint);
            } else {
                temp += (int) value_degree[i - 1];
                paint.setColor(Color.parseColor(COLORS[i]));
                canvas.drawArc(rectf, temp, value_degree[i], true, paint);
            }
        }
    }

}