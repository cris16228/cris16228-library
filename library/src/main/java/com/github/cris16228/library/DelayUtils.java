package com.github.cris16228.library;

import android.os.Handler;

public class DelayUtils {


    private onDelay onDelay;

    public DelayUtils() {
    }

    public static DelayUtils get() {
        return new DelayUtils();
    }

    public void onDelay(onDelay _onDelay) {
        onDelay = _onDelay;
    }

    private void postDelayed(long delay) {
        new Handler().postDelayed(() -> {
            onDelay.onDelayExecute();
        }, delay);
    }

    private void postRepeat(long delay, long repeatingTime) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            onDelay.onDelayExecute();
        }, delay);
    }

    public interface onDelay {

        void onDelayExecute();

        void onRepeatExecute();
    }
}
