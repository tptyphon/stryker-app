package com.zalexdev.stryker.fragments;

import static com.zalexdev.stryker.su.SuUtils.TAG;
import static com.zalexdev.stryker.su.SuUtils.mountChroot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.adapters.NucleiAdapter;
import com.zalexdev.stryker.adapters.WifiAdapter;

import com.zalexdev.stryker.objects.NucleiVuln;
import com.zalexdev.stryker.objects.WifiNetwork;
import com.zalexdev.stryker.su.MyForegroundService;
import com.zalexdev.stryker.su.SuUtils;
import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.FileUtils;
import com.zalexdev.stryker.utils.Preferences;
import com.zalexdev.stryker.utils.Utils;

import org.acra.ACRA;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class NucleiHostFragment extends Fragment {


    private Preferences preferences;
    public static NucleiHostFragment newInstance() {
        return new NucleiHostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mountChroot(null, null);
        View root = inflater.inflate(R.layout.fragment_nucleihost, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.vuln_list);
        ImageView favicon = root.findViewById(R.id.img);
        CircularProgressIndicator progress = root.findViewById(R.id.progress);
        preferences = Preferences.getInstance();
        getFavicon(preferences.getContext(), "https://strykerdefence.com", drawable -> {
            if (drawable != null) {
                favicon.setImageDrawable(drawable);
            }
        });

        MyForegroundService.startService(preferences.getContext());
        final MyForegroundService[] s = {null};
        MyForegroundService.setOnServiceStartedListener(service -> {
            service.startScan(preferences.getContext(), "http://honey.scanme.sh/");
            s[0] = service;
        });
        ExecutorBuilder executorBuilder = new ExecutorBuilder();
        executorBuilder.setChroot(true);
        executorBuilder.setCommand("env");
        executorBuilder.setActivity(getActivity());
        executorBuilder.execute();
        NucleiAdapter adapter = new NucleiAdapter(preferences, preferences.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(preferences.getContext()));
        recyclerView.setAdapter(adapter);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                if (s[0] != null && s[0].nucleiHost != null){
                    List<NucleiVuln> list = preferences.getNucleiHostById(s[0].nucleiHost.getRandom_id()).getVulnerabilities();
                    Collections.reverse(list);
                    new Handler(Looper.getMainLooper()).post(() -> adapter.updateList(list));
                    Log.d("adapter", "run: "+list.size());
                }}
                catch (Exception e) {
                    Log.e("adapter", "run: ", e);
                }
            }
        },0,1000);

        return root;
    }


    private void setupBackButton(View root) {
        root.findViewById(R.id.back).setOnClickListener(v -> Preferences.getInstance().replaceFragment(new Main(), "main"));
    }


    public static void getFavicon(Context context, String websiteUrl, Consumer<Drawable> onComplete) {
        new Thread(() -> {
            Drawable favicon = downloadFavicon(context, websiteUrl);
            new Handler(Looper.getMainLooper()).post(() -> onComplete.accept(favicon));
        }).start();
    }

    private static Drawable downloadFavicon(Context context, String websiteUrl) {
        try {
            // Connect to the website and get the HTML
            Document doc = Jsoup.connect(websiteUrl).get();

            // Look for favicon in HTML
            String faviconUrl = findFaviconUrl(doc, websiteUrl);

            // Download the favicon
            URL url = new URL(faviconUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(input);

            // Convert Bitmap to Drawable
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error downloading favicon", e);
            return null;
        }
    }

    private static String findFaviconUrl(Document doc, String websiteUrl) {
        // Check for preload icon
        Elements preloadIcons = doc.select("link[rel~=(?i)^(preload )?icon]");
        if (!preloadIcons.isEmpty()) {
            Element preloadIcon = preloadIcons.first();
            String href = preloadIcon.attr("href");
            if (!href.isEmpty()) {
                return makeAbsoluteUrl(websiteUrl, href);
            }
        }

        // Check for standard favicon
        Elements standardIcons = doc.select("link[rel~=(?i)^(shortcut )?icon]");
        if (!standardIcons.isEmpty()) {
            Element standardIcon = standardIcons.first();
            String href = standardIcon.attr("href");
            if (!href.isEmpty()) {
                return makeAbsoluteUrl(websiteUrl, href);
            }
        }

        // Fallback to default favicon.ico
        return websiteUrl + "/favicon.ico";
    }

    private static String makeAbsoluteUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        } else {
            try {
                URL base = new URL(baseUrl);
                URL absolute = new URL(base, relativeUrl);
                return absolute.toString();
            } catch (Exception e) {
                Log.e(TAG, "Error creating absolute URL", e);
                return relativeUrl;
            }
        }
    }
}