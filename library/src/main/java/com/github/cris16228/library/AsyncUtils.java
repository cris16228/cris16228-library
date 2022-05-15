package com.github.cris16228.library;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncUtils {

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    private onExecuteListener onExecuteListener;

    public static AsyncUtils get() {
        return new AsyncUtils();
    }

    public AsyncUtils(ExecutorService executor, Handler handler) {
        this.executor = executor;
        this.handler = handler;
    }

    public AsyncUtils() {
    }

    public void onExecuteListener(onExecuteListener _onExecuteListener) {
        onExecuteListener = _onExecuteListener;
    }

    public void execute() {
        onExecuteListener.preExecute();
        executor.execute(() -> {
            onExecuteListener.doInBackground();
            handler.post(() -> onExecuteListener.postDelayed());
        });
    }

    public interface onExecuteListener {

        void preExecute();

        void doInBackground();

        void postDelayed();
    }
}
