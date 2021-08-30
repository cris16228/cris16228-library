package com.github.cris16228.library;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabAnimator {

    private Animation rotate_open_anim;
    private Animation rotate_close_anim;
    private Animation from_bottom_anim;
    private Animation to_bottom_anim;
    private Context context;
    private boolean clicked = false;

    public static FabAnimator with(Context _context) {
        FabAnimator fabAnimator = new FabAnimator();
        fabAnimator.context = _context;
        return fabAnimator;
    }

    public FabAnimator load() {
        rotate_open_anim = AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim);
        rotate_close_anim = AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim);
        from_bottom_anim = AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim);
        to_bottom_anim = AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim);
        return this;
    }

    public View.OnClickListener  onClick(@NonNull FloatingActionButton fab) {
        return v -> {
            setVisibility(fab, clicked);
            setAnimation(fab, clicked);
            setClickable(fab, clicked);
            clicked = !clicked;
        };
    }

    public View.OnClickListener onClick(@NonNull FloatingActionButton main_fab, @NonNull FloatingActionButton fab) {
        return v -> {
            setVisibility(fab, clicked);
            setAnimation(main_fab, fab, clicked);
            setClickable(fab, clicked);
            clicked = !clicked;
        };
    }

    public View.OnClickListener onClicks(@NonNull FloatingActionButton main_fab, @NonNull FloatingActionButton... fabs) {

        clicked = !clicked;
        return v -> {
            for (FloatingActionButton fab : fabs) {
                setVisibility(fab, clicked);
                setAnimation(main_fab, fab, clicked);
                setClickable(fab, clicked);
            }
        };
    }

    private void setAnimation(FloatingActionButton main_fab, FloatingActionButton fab, boolean clicked) {
        if (main_fab == null) return;
        if (fab == null) return;
        if (!clicked) {
            fab.startAnimation(from_bottom_anim);
            main_fab.startAnimation(rotate_open_anim);
        } else {
            fab.startAnimation(to_bottom_anim);
            main_fab.startAnimation(rotate_close_anim);
        }
    }

    private void setVisibility(FloatingActionButton fab, boolean clicked) {
        if (fab == null) return;
        if (!clicked)
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.INVISIBLE);
    }

    private void setAnimation(FloatingActionButton fab, boolean clicked) {
        if (fab == null) return;
        if (!clicked)
            fab.startAnimation(from_bottom_anim);
        else
            fab.startAnimation(to_bottom_anim);
    }

    private void setClickable(FloatingActionButton fab, boolean clicked) {
        if (fab == null) return;
        fab.setClickable(!clicked);
    }
}
