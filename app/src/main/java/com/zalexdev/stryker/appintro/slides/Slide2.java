package com.zalexdev.stryker.appintro.slides;

import static com.zalexdev.stryker.su.SuUtils.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.zalexdev.stryker.R;

import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.Preferences;
import com.zalexdev.stryker.utils.Utils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class Slide2 extends Fragment {

    private Activity activity;
    private Context context;



    @SuppressLint({"SdCardPath", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide2, container, false);
        activity = getActivity();
        assert activity != null;
        context = getContext();
        TextView title = view.findViewById(R.id.slide_title);
        TextView description = view.findViewById(R.id.slide_description);
        MaterialButton grant = view.findViewById(R.id.grant);
        MaterialButton wiki = view.findViewById(R.id.wiki);
        grant.setOnClickListener(v -> {
            grant.setEnabled(false);
            ExecutorBuilder executorBuilder = new ExecutorBuilder()
                    .setActivity(activity)
                    .setCommand("id");

            executorBuilder.setOnFinished(strings -> {
                if (ExecutorBuilder.contains(strings,"uid=0")) {
                    copyAssets();

                    Preferences.getInstance().replaceFragment(new Slide3(), "Slide3");
                } else {
                    grant.setEnabled(true);
                    title.setText("Root not granted!");
                    description.setText("You need to grant root access to continue. Not sure why? Check the Wiki.");
                    wiki.setWidth(wiki.getWidth() * 2);
                    wiki.setHeight(wiki.getHeight() * 2);
                    wiki.setText("Read Wiki");
                    Log.d(Slide2.class.getSimpleName(), "accept: Root not granted");
                }
            });
            executorBuilder.execute();
        });






        return view;
    }
    private void copyAssets() {
        new Thread(() -> {

            String[] files = null;
            try {
                files = context.getAssets().list("");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }
            new File(Utils.fileDir).mkdirs();
            assert files != null;
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = context.getAssets().open(filename);
                    File outFile = new File(Utils.fileDir, filename);
                    out = Files.newOutputStream(outFile.toPath());
                    copyFile(in, out);
                    in.close();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
            }
            ExecutorBuilder executorBuilder = new ExecutorBuilder();
            executorBuilder.setCommand("chmod -R 777 " + Utils.fileDir);
            executorBuilder.execute();
            Log.d(TAG, "copyAssets: Files copied");
        }).start();
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}