package com.zalexdev.stryker.fragments;

import static com.zalexdev.stryker.su.SuUtils.mountChroot;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.adapters.WifiAdapter;
import com.zalexdev.stryker.objects.WifiNetwork;

import com.zalexdev.stryker.utils.ExecutorBuilder;

import com.zalexdev.stryker.utils.Preferences;
import com.zalexdev.stryker.utils.Utils;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Collections;

public class ScannerWiFi extends Fragment {

    private RecyclerView recyclerView;
    private final Utils utils = new Utils();
    private LinearProgressIndicator progressBar;
    private boolean scanning = false;
    private LinearLayout errorLayout;
    private LottieAnimationView errorAnimation;
    private MaterialTextView errorText;
    private static ScannerWiFi instance;
    private ArrayList<WifiNetwork> networks = new ArrayList<>();
    private boolean wpsOnly = false;
    private FloatingActionButton wps;

    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new ScannerWiFi();
        }
    }

    public static ScannerWiFi getInstance(Context context) {
        if (instance == null) {
            initInstance(context);
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mountChroot(null, null);
        View root = inflater.inflate(R.layout.fragment_wifi, container, false);
        progressBar = root.findViewById(R.id.progress_bar);
        errorLayout = root.findViewById(R.id.layout_error);
        errorAnimation = root.findViewById(R.id.anim_error);
        errorText = root.findViewById(R.id.text_error);
        wps = root.findViewById(R.id.wps_button);
        setupRecyclerView(root);
        setupBottomSheet(root);
        setupBackButton(root);
        ImageView refresh = root.findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> scanNetworks());
        if (networks.isEmpty()){
            scanNetworks();
        }
        wpsOnly = Preferences.getInstance().getBoolean("wpsOnly");
        if (wpsOnly){
            wps.setImageResource(R.drawable.wps_icon);
        }else {
            wps.setImageResource(R.drawable.nowps);
        }
        wps.setOnClickListener(v -> {
            wpsOnly = !wpsOnly;
            Preferences.getInstance().setBoolean("wpsOnly", wpsOnly);
            setupRecyclerView(root);
            if (wpsOnly){
                wps.setImageResource(R.drawable.wps_icon);
            }else {
                wps.setImageResource(R.drawable.nowps);
            }
        });
        return root;
    }

    private void setupRecyclerView(View root) {
        recyclerView = root.findViewById(R.id.wifi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        WifiAdapter adapter = new WifiAdapter(networks);
        adapter.setWpsOnly(wpsOnly);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomSheet(View root) {
        FrameLayout bottomSheet = root.findViewById(R.id.standard_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);
        behavior.setSkipCollapsed(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        FloatingActionButton fab = root.findViewById(R.id.magic_button);
        fab.setOnClickListener(v -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            fab.hide();
            wps.hide();
        });


        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    fab.hide();
                    wps.hide();
                }else if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                    fab.show();
                    wps.show();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    private void setupBackButton(View root) {
        root.findViewById(R.id.back).setOnClickListener(v -> Preferences.getInstance().replaceFragment(new Main(), "main"));
    }

    private void scanNetworks(){
        wps.hide();
        if (!scanning) {
            scanning = true;
            errorAnimation.cancelAnimation();
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            ExecutorBuilder wifiScanner = new ExecutorBuilder()
                    .setCommand("iw dev wlan0 scan | jc --iw-scan")
                    .setChroot(true)
                    .setContext(getContext())
                    .setError(s -> {
                        progressBar.setVisibility(View.GONE);
                        Preferences.getInstance().toaster("Error scanning networks");
                        errorText.setText(getString(R.string.error)+"\n"+s);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorAnimation.playAnimation();
                        scanning = false;
                        ACRA.getErrorReporter().handleSilentException(new Exception(s));
                    })
                    .setOnFinished(strings -> {
                        networks = parseWifi(strings);
                        progressBar.setVisibility(View.GONE);
                        setupRecyclerView(recyclerView);
                        scanning = false;
                        wps.show();
                    });
            wifiScanner.execute();
        }else {
            Preferences.getInstance().toaster("Already scanning, please wait!");
        }
    }


    public ArrayList<WifiNetwork> parseWifi(ArrayList<String> output) {
        System.out.println(output);
        ArrayList<WifiNetwork> networks = new ArrayList<>();

        StringBuilder myJsonString = new StringBuilder();
        for (String line : output) {
            if (!line.contains("[!] ")) {
                myJsonString.append(line);
            }
        }
        ObjectMapper om = new ObjectMapper();
        try {
            WifiNetwork[] root = om.readValue(myJsonString.toString(), WifiNetwork[].class);
            Collections.addAll(networks, root);
            for (WifiNetwork network : networks) {
                network.getManufacturer();
                if (network.ssid == null || network.ssid.equals("null") || network.ssid.equals("") || network.ssid.equals("<length: 0>")) {
                    network.ssid = "Hidden SSID";
                }
                if (network.ssid.contains("\\x")) {
                    Log.e("SSID", network.ssid);
                    Log.e("SSID", decodeString(network.ssid));
                    Log.e("BSSID", network.bssid);
                    network.ssid = decodeString(network.ssid);
                }else if (network.ssid.contains("\\u")) {
                    network.ssid = decodeString(network.ssid);
                }
                if (network.manufacturer != null &&  network.manufacturer.length() < 3) {
                        network.manufacturer = "Unknown";
                }

            }
        } catch (Exception e) {
            ACRA.getErrorReporter().handleException(e);

        }


        ArrayList<WifiNetwork> uniqueNetworks = new ArrayList<>();
        ArrayList<String> uniqueSSIDs = new ArrayList<>();

        for (WifiNetwork network : networks) {
            if (!uniqueSSIDs.contains(network.ssid)) {
                uniqueSSIDs.add(network.ssid);

                uniqueNetworks.add(network);
            }
        }





        Log.d("Networks "+uniqueNetworks.size(), uniqueNetworks.toString());
        return networks;
    }

    public String decodeString(String encodedString) {
        return ExecutorBuilder.runCommand("echo -e \"SSID: " + encodedString + "\"").toString().replace("SSID: ", "").replace("[","").replace("]","").replace("\n","");
    }


}