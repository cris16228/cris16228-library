package com.github.cris16228.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class RoundedImageView extends ImageView {

    Bitmap bitmap;
    Drawable drawable;

    public RoundedImageView(Context context) {
        super(context);
        drawable = getDrawable();
        if (drawable == null)
            return;
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawable = getDrawable();
        if (drawable == null)
            return;
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawable = getDrawable();
        if (drawable == null)
            return;
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getWidth() == 0 || getHeight() == 0)
            return;
        Bitmap bitmap_copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int w = getWidth(), h = getHeight();
        Bitmap roundBitmap = getRoundBitmap(bitmap_copy, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    private Bitmap getRoundBitmap(Bitmap bitmap_copy, int radius) {
        Bitmap bitmap;

        if (bitmap_copy.getWidth() != radius || bitmap_copy.getHeight() != radius) {
            float smallest = Math.min(bitmap_copy.getWidth(), bitmap_copy.getHeight());
            float factor = smallest / radius;
            bitmap = Bitmap.createScaledBitmap(bitmap_copy, (int) (bitmap_copy.getWidth() / factor), (int) (bitmap_copy.getHeight() / factor), false);
        } else
            bitmap = bitmap_copy;
        Bitmap out = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(out);
        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2 + .7f, radius / 2 + .7f, radius / 2 + .1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return out;
    }
}
