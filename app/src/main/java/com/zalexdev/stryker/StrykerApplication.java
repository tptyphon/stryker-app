package com.zalexdev.stryker;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;

public class StrykerApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this, new CoreConfigurationBuilder()
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON)
                .withLogcatArguments("-t", "100000", "-v", "time")
                .withPluginConfigurations(
                    new ToastConfigurationBuilder()
                        .withText(getString(R.string.crash))
                        .build(),
                        new HttpSenderConfigurationBuilder()
                                .withUri("https://collector.tracepot.com/FyfIQpcBA-FIlZ2C4APg")
                                .build()
                )
            );
    }
}