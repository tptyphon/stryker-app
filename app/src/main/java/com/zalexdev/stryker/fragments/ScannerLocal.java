package com.zalexdev.stryker.fragments;

import static com.zalexdev.stryker.su.SuUtils.mountChroot;
import static com.zalexdev.stryker.utils.Utils.toast;

import android.content.Context;
import android.os.Bundle;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.zalexdev.stryker.R;
import com.zalexdev.stryker.adapters.LocalAdapter;

import com.zalexdev.stryker.objects.Device;
import com.zalexdev.stryker.utils.LocalScanEngine;
import com.zalexdev.stryker.utils.ExecutorBuilder;
import com.zalexdev.stryker.utils.Preferences;
import com.zalexdev.stryker.utils.Utils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ScannerLocal extends Fragment {

    private RecyclerView recyclerView;
    private final Utils utils = new Utils();
    private LinearProgressIndicator progressBar;
    private boolean scanning = false;
    private LinearLayout errorLayout;
    private LottieAnimationView errorAnimation;
    private MaterialTextView errorText;
    private static ScannerLocal instance;
    private ArrayList<Device> local = new ArrayList<>();
    private LocalAdapter adapter;
    private LocalScanEngine scanner;


    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new ScannerLocal();
        }
    }

    public static ScannerLocal getInstance(Context context) {
        if (instance == null) {
            initInstance(context);
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mountChroot(null, null);
        View root = inflater.inflate(R.layout.fragment_local, container, false);
        progressBar = root.findViewById(R.id.progress_bar);
        errorLayout = root.findViewById(R.id.layout_error);
        errorAnimation = root.findViewById(R.id.anim_error);
        errorText = root.findViewById(R.id.text_error);

        setupRecyclerView(root);
        setupBackButton(root);
        ImageView refresh = root.findViewById(R.id.refresh);
        progressBar.setVisibility(View.VISIBLE);
        scanner = new LocalScanEngine("wlan0");
        scanner.setOnFinished(integer -> {
            if (integer == -3) {
                errorLayout.setVisibility(View.VISIBLE);
                errorAnimation.setAnimation(R.raw.error);
                errorAnimation.playAnimation();
                errorText.setText("Cannot get gateway IP | Maybe you are not connected to a network?");
                scanning = false;
                progressBar.setVisibility(View.GONE);
            }
        });
        scanner.setOnProgressUpdate(progress -> {
            progressBar.setProgress(progress);
            if (progress == 100) {
                scanning = false;
                progressBar.setVisibility(View.GONE);

                errorLayout.setVisibility(View.GONE);
            }
        });
        scanner.setOnDeviceScanned(device -> adapter.updateItemByIp(device));
        scanner.setOnNetworkScanned(devices -> {
            adapter.updateList(devices);
            scanning = false;
            progressBar.setVisibility(View.GONE);
        });
        scanner.setOnNetworkPreScanned(devices -> {
            local = devices;
            adapter.updateList(devices);
        });
        refresh.setOnClickListener(v -> scan());
        if (adapter.getItemCount() == 0) {
            scan();
        } else {
            progressBar.setVisibility(View.GONE);
        }

        return root;
    }

    private void scan(){
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        adapter.updateList(new ArrayList<>());
        if (!scanning) {
            scanning = true;
            scanner.scanNetwork();
        }
    }

    private void setupRecyclerView(View root) {
        recyclerView = root.findViewById(R.id.wifi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LocalAdapter(local);
        recyclerView.setAdapter(adapter);
    }



    private void setupBackButton(View root) {
        root.findViewById(R.id.back).setOnClickListener(v -> Preferences.getInstance().replaceFragment(new Main(), "main"));
    }


    public String decodeString(String encodedString) {
        return ExecutorBuilder.runCommand("echo -e \"SSID: " + encodedString + "\"").toString().replace("SSID: ", "").replace("[","").replace("]","").replace("\n","");
    }


}