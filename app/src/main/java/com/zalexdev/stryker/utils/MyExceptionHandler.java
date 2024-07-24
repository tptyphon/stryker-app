package com.zalexdev.stryker.utils;

import static com.zalexdev.stryker.su.SuUtils.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
            // Handle uncaught exceptions here
            Log.e(TAG, "Uncaught exception occurred", throwable);
        }
    }