package com.zalexdev.stryker.appintro.slides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.zalexdev.stryker.R;

import com.zalexdev.stryker.utils.Preferences;

import java.util.ArrayList;


public class Slide1 extends Fragment {

    private static final String PRIVACY_POLICY_URL = "https://strykerdefence.com/static/docs/Privacy%20and%20Cookie%20Policy.pdf";
    private static final String TERMS_AND_CONDITIONS_URL = "https://strykerdefence.com/static/docs/Terms%20and%20Conditions.pdf";
    private static final String ETHICAL_HACKING_STANDARDS_URL = "https://strykerdefence.com/ethicalhacking";

    private Context context;
    private MaterialButton button;
    private final ArrayList<MaterialCheckBox> checkBoxes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide1, container, false);
        context = getContext();

        button = view.findViewById(R.id.login);
        checkBoxes.add(view.findViewById(R.id.slide_checkbox));
        checkBoxes.add(view.findViewById(R.id.slide_checkbox2));
        checkBoxes.add(view.findViewById(R.id.slide_checkbox3));

        setTextViewWithLink(view.findViewById(R.id.text1), "I agree with with <a href='" + PRIVACY_POLICY_URL + "'>Privacy Policy</a>", PRIVACY_POLICY_URL);
        setTextViewWithLink(view.findViewById(R.id.text2), "I agree with with <a href='" + TERMS_AND_CONDITIONS_URL + "'>Terms and Conditions</a>", TERMS_AND_CONDITIONS_URL);
        setTextViewWithLink(view.findViewById(R.id.text3), "I agree with with <a href='" + ETHICAL_HACKING_STANDARDS_URL + "'>Ethical Hacking Standards</a>", ETHICAL_HACKING_STANDARDS_URL);

        for (MaterialCheckBox checkBox : checkBoxes) {
            checkBox.setOnClickListener(view12 -> button.setEnabled(areAllCheckBoxesChecked()));
        }

        button.setOnClickListener(view1 -> {
            Preferences.getInstance().replaceFragment(new Slide2(), "Slide2");
            Preferences.getInstance().setString("wlan0_cmd_on", "echo 4 > /sys/module/wlan/parameters/con_mode;ip link set wlan0 down;ip link set wlan0 up");
            Preferences.getInstance().setString("wlan0_cmd_off", "echo 0 > /sys/module/wlan/parameters/con_mode;ip link set wlan0 down;ip link set wlan0 up");
            Preferences.getInstance().setString("wlan1_cmd_on", "airmon-ng start {wlan}");
            Preferences.getInstance().setString("wlan1_cmd_off", "airmon-ng stop {wlan}");
            Preferences.getInstance().setBoolean("iface_down", true);
            Preferences.getInstance().setBoolean("wlan0_onchroot", false);
            Preferences.getInstance().setBoolean("wlan1_onchroot", true);
        });
        return view;
    }

    private void setTextViewWithLink(TextView textView, String htmlText, String url) {
        textView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
        textView.setOnClickListener(v -> openUrl(url));
    }

    private boolean areAllCheckBoxesChecked() {
        for (MaterialCheckBox checkBox : checkBoxes) {
            if (!checkBox.isChecked()) {
                return false;
            }
        }
        return true;
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Error opening url", Toast.LENGTH_SHORT).show();
        }
    }
}