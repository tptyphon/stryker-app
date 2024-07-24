package com.zalexdev.stryker.objects;

import android.annotation.SuppressLint;
import android.util.Log;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class NucleiHost {
    private String url;
    private String random_id = "";
    private List<NucleiVuln> vulnerabilities;

    public NucleiHost(String url) {
        this.url = url;
        this.vulnerabilities = new ArrayList<>();
        this.random_id = generateRandomId();
        Log.d("NucleiHost", "Created new NucleiHost with URL: " + url + " and random_id: " + random_id);
    }

    public void addVulnerability(NucleiVuln vuln) {
        vulnerabilities.add(vuln);
    }

    public int getTotalVulnerabilities() {
        return vulnerabilities.size();
    }

    public int getCriticalVulnerabilities() {
        return countVulnerabilitiesBySeverity("critical");
    }

    public int getHighVulnerabilities() {
        return countVulnerabilitiesBySeverity("high");
    }

    public int getMediumVulnerabilities() {
        return countVulnerabilitiesBySeverity("medium");
    }

    public int getLowVulnerabilities() {
        return countVulnerabilitiesBySeverity("low");
    }

    public int getInfoVulnerabilities() {
        return countVulnerabilitiesBySeverity("info");
    }

    private int countVulnerabilitiesBySeverity(String severity) {
        return (int) vulnerabilities.stream()
                .filter(vuln -> vuln.getSeverity().equalsIgnoreCase(severity))
                .count();
    }

    @SuppressLint("DefaultLocale")
    public String getSummary() {
        return String.format(
                "Host: %s\n" +
                        "Total vulnerabilities: %d\n" +
                        "Critical: %d\n" +
                        "High: %d\n" +
                        "Medium: %d\n" +
                        "Low: %d\n" +
                        "Info: %d",
                url,
                getTotalVulnerabilities(),
                getCriticalVulnerabilities(),
                getHighVulnerabilities(),
                getMediumVulnerabilities(),
                getLowVulnerabilities(),
                getInfoVulnerabilities()
        );
    }

    public String generateRandomId() {
        random_id = String.valueOf(System.currentTimeMillis());
        return random_id;
    }
}