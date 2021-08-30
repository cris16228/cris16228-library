package com.github.cris16228.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout;

public class PieChart {

    public void createChart(Activity activity, int pie_chart,float[] values,Context context) {
        LinearLayout linear = activity.findViewById(pie_chart);
        values = calculateData(values);
        linear.addView(new ChartGraphview(context, values));
    }

    private float[] calculateData(float[] data) {
        // TODO Auto-generated method stub
        float total = 0;
        for (int i = 0; i < data.length; i++) {
            total += data[i];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = 360 * (data[i] / total);
        }
        return data;

    }

    public static class ChartGraphview extends View {
        RectF rectf = new RectF(10, 10, 200, 200);
        int temp = 0;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float[] value_degree;
        private final int[] COLORS = {Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN, Color.RED, Color.MAGENTA, Color.WHITE, Color.YELLOW};

        public ChartGraphview(Context context, float[] values) {

            super(context);
            value_degree = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                value_degree[i] = values[i];
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);

            for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } else {
                    temp += (int) value_degree[i - 1];
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                }
            }
        }
    }
}
