package com.example.toddlerjukebox;

import android.app.Activity;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import java.util.Optional;

public class UiLocker {
    private final Activity activity;

    public UiLocker(Activity activity) {
        this.activity = activity;
    }

    public void lock() {
        activity.setShowWhenLocked(true);
        activity.setTurnScreenOn(true);

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity.setShowWhenLocked(true);

        View decorView = activity.getWindow().getDecorView();
        Optional.ofNullable(decorView.getWindowInsetsController())
                .ifPresent(controller -> {
                    controller.hide(android.view.WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                });
    }

    public void unlock() {
        activity.setShowWhenLocked(false);
        activity.setTurnScreenOn(false);

        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity.setShowWhenLocked(false);

        View decorView = activity.getWindow().getDecorView();
        Optional.ofNullable(decorView.getWindowInsetsController())
                .ifPresent(controller ->
                        controller.show(android.view.WindowInsets.Type.systemBars()));
    }
}
