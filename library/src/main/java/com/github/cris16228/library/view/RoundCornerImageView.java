package com.github.cris16228.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.cris16228.library.R;

public class RoundCornerImageView extends ImageView {

    public float roundCorners;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView);
        try {
            roundCorners = typedArray.getFloat(R.styleable.RoundCornerImageView_roundCorners, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public float getRoundCorners() {
        return roundCorners;
    }

    public void setRoundCorners(float roundCorners) {
        this.roundCorners = roundCorners;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;
        if (getWidth() == 0 || getHeight() == 0)
            return;
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        Bitmap bitmap_copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int w = getWidth(), h = getHeight();
        Bitmap roundBitmap = getRoundedCornerBitmap(bitmap_copy, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap_copy, int radius) {
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
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawRoundRect(rectF, roundCorners, roundCorners, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return out;
    }
}
