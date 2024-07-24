package com.zalexdev.stryker.objects;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zalexdev.stryker.utils.Utils;

import java.util.ArrayList;
import java.util.Base64;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WifiNetwork{
    // Setter and getter for bssid
    @Getter
    @Setter
    public String bssid;
    // Setter and getter for freq
    @Getter
    @Setter
    public int freq = 0;
    // Setter and getter for capability
    @Getter
    @Setter
    public String capability;
    // Setter and getter for ssid
    @Getter
    @Setter
    public String ssid;
    public ArrayList<Double> supported_rates;
    public int primary_channel = 0;
    public String network_options;
    public String network_type;
    public String venue_group;
    public int venue_type = 0;
    public Integer station_count;
    // Setter and getter for channels
    @Getter
    @Setter
    public String channels;
    // Setter and getter for country
    @Getter
    @Setter
    public String country;
    // Setter and getter for wps
    @Getter
    @Setter
    public String wps;
    public String wi_fi_protected_setup_state;
    public String ap_setup_locked;
    public String response_type;
    // Setter and getter for uuid
    @Getter
    @Setter
    public String uuid;
    // Setter and getter for manufacturer
    @Setter
    public String manufacturer;
    // Setter and getter for model
    @Setter
    public String model;
    public Object model_number;
    public Object serial_number;
    public String primary_device_type;
    public String device_name;
    public String config_methods;
    // Setter and getter for version2
    @Getter
    @Setter
    public double version2 = 0;
    // Setter and getter for environment
    @Getter
    @Setter
    public String environment;
    public double signal_dbm;
    public int last_seen_ms = 0;
    public ArrayList<Double> selected_rates;
    public String supported_channel_width;
    public String channel_width;
    // Setter and getter for wpa
    @Getter
    @Setter
    public String wpa;
    public boolean hasStationInfo = false;
    @Getter
    @Setter
    public String password = "";
    @Getter
    @Setter
    public String pin = "";



    // Setter and getter for supported_rates
    public void setSupportedRates(ArrayList<Double> supported_rates) {
        this.supported_rates = supported_rates;
    }


    public ArrayList<Double> getSupportedRates() {
        return this.supported_rates;
    }

    // Setter and getter for primary_channel
    public void setPrimaryChannel(int primary_channel) {
        this.primary_channel = primary_channel;
    }

    public int getPrimaryChannel() {
        return this.primary_channel;
    }

    // Setter and getter for network_options
    public void setNetworkOptions(String network_options) {
        this.network_options = network_options;
    }

    public String getNetworkOptions() {
        return this.network_options;
    }

    // Setter and getter for network_type
    public void setNetworkType(String network_type) {
        this.network_type = network_type;
    }

    public String getNetworkType() {
        return this.network_type;
    }

    // Setter and getter for venue_group
    public void setVenueGroup(String venue_group) {
        this.venue_group = venue_group;
    }

    public String getVenueGroup() {
        return this.venue_group;
    }

    // Setter and getter for venue_type
    public void setVenueType(int venue_type) {
        this.venue_type = venue_type;
    }

    public int getVenueType() {
        return this.venue_type;
    }

    // Setter and getter for station_count
    public void setStationCount(int station_count) {
        this.station_count = station_count;
    }

    public int getStationCount() {
        return this.station_count;
    }

    // Setter and getter for wi_fi_protected_setup_state
    public void setWiFiProtectedSetupState(String wi_fi_protected_setup_state) {
        this.wi_fi_protected_setup_state = wi_fi_protected_setup_state;
    }

    public String getWiFiProtectedSetupState() {
        return this.wi_fi_protected_setup_state;
    }

    // Setter and getter for ap_setup_locked
    public void setApSetupLocked(String ap_setup_locked) {
        this.ap_setup_locked = ap_setup_locked;
    }

    public String getApSetupLocked() {
        return this.ap_setup_locked;
    }

    // Setter and getter for response_type
    public void setResponseType(String response_type) {
        this.response_type = response_type;
    }

    public String getResponseType() {
        return this.response_type;
    }

    public String getModel() {
        if (device_name != null && !device_name.isEmpty()) {
            return this.device_name;
        } else if (model !=null && !model.isEmpty()) {
            return this.model;
        } else{
            return this.manufacturer;
        }
    }

    // Setter and getter for model_number
    public void setModelNumber(Object model_number) {
        this.model_number = model_number;
    }

    public Object getModelNumber() {
        return this.model_number;
    }

    // Setter and getter for serial_number
    public void setSerialNumber(Object serial_number) {
        this.serial_number = serial_number;
    }

    public Object getSerialNumber() {
        return this.serial_number;
    }

    // Setter and getter for primary_device_type
    public void setPrimaryDeviceType(String primary_device_type) {
        this.primary_device_type = primary_device_type;
    }

    public String getPrimaryDeviceType() {
        return this.primary_device_type;
    }

    // Setter and getter for device_name
    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public String getDeviceName() {
        return this.device_name;
    }

    // Setter and getter for config_methods
    public void setConfigMethods(String config_methods) {
        this.config_methods = config_methods;
    }

    public String getConfigMethods() {
        return this.config_methods;
    }

    // Setter and getter for signal_dbm
    public void setSignalDbm(double signal_dbm) {
        this.signal_dbm = signal_dbm;
    }

    public int getSignalDbm() {
        return (int) this.signal_dbm;
    }

    // Setter and getter for last_seen_ms
    public void setLastSeenMs(int last_seen_ms) {
        this.last_seen_ms = last_seen_ms;
    }

    public int getLastSeenMs() {
        return this.last_seen_ms;
    }

    // Setter and getter for selected_rates
    public void setSelectedRates(ArrayList<Double> selected_rates) {
        this.selected_rates = selected_rates;
    }

    public ArrayList<Double> getSelectedRates() {
        return this.selected_rates;
    }

    // Setter and getter for supported_channel_width
    public void setSupportedChannelWidth(String supported_channel_width) {
        this.supported_channel_width = supported_channel_width;
    }

    public String getSupportedChannelWidth() {
        return this.supported_channel_width;
    }

    // Setter and getter for channel_width
    public void setChannelWidth(String channel_width) {
        this.channel_width = channel_width;
    }

    public String getChannelWidth() {
        return this.channel_width;
    }

    public boolean isBssidSet() {
        return this.bssid != null && !this.bssid.isEmpty();
    }

    public boolean isFreqSet() {
        return this.freq != 0;
    }

    public boolean isCapabilitySet() {
        return this.capability != null && !this.capability.isEmpty();
    }

    public boolean isSsidSet() {
        return this.ssid != null && !this.ssid.isEmpty();
    }

    public boolean isSupportedRatesSet() {
        return this.supported_rates != null && !this.supported_rates.isEmpty();
    }

    public boolean isPrimaryChannelSet() {
        return this.primary_channel != 0;
    }

    public boolean isNetworkOptionsSet() {
        return this.network_options != null && !this.network_options.isEmpty();
    }

    public boolean isNetworkTypeSet() {
        return this.network_type != null && !this.network_type.isEmpty();
    }

    public boolean isVenueGroupSet() {
        return this.venue_group != null && !this.venue_group.isEmpty();
    }

    public boolean isVenueTypeSet() {
        return this.venue_type != 0;
    }

    public boolean isStationCountSet() {
        return this.station_count != null;
    }

    public boolean isChannelsSet() {
        return this.channels != null && !this.channels.isEmpty();
    }

    public boolean isCountrySet() {
        return this.country != null && !this.country.isEmpty();
    }

    public boolean isWpsSet() {
        return this.wps != null && !this.wps.isEmpty();
    }

    public boolean isWiFiProtectedSetupStateSet() {
        return this.wi_fi_protected_setup_state != null && !this.wi_fi_protected_setup_state.isEmpty();
    }

    public boolean isApSetupLockedSet() {
        return this.ap_setup_locked != null && !this.ap_setup_locked.isEmpty();
    }

    public boolean isResponseTypeSet() {
        return this.response_type != null && !this.response_type.isEmpty();
    }

    public boolean isUuidSet() {
        return this.uuid != null && !this.uuid.isEmpty();
    }

    public boolean isManufacturerSet() {
        return this.manufacturer != null && !this.manufacturer.isEmpty();
    }

    public boolean isModelSet() {
        return this.model != null && !this.model.isEmpty();
    }

    public boolean isModelNumberSet() {
        return this.model_number != null;
    }

    public boolean isSerialNumberSet() {
        return this.serial_number != null;
    }

    public boolean isPrimaryDeviceTypeSet() {
        return this.primary_device_type != null && !this.primary_device_type.isEmpty();
    }

    public boolean isDeviceNameSet() {
        return this.device_name != null && !this.device_name.isEmpty();
    }

    public boolean isConfigMethodsSet() {
        return this.config_methods != null && !this.config_methods.isEmpty();
    }

    public boolean isVersion2Set() {
        return this.version2 != 0;
    }

    public boolean isEnvironmentSet() {
        return this.environment != null && !this.environment.isEmpty();
    }

    public boolean isSignalDbmSet() {
        return this.signal_dbm != 0;
    }

    public boolean isLastSeenMsSet() {
        return this.last_seen_ms != 0;
    }

    public boolean isSelectedRatesSet() {
        return this.selected_rates != null && !this.selected_rates.isEmpty();
    }

    public boolean isSupportedChannelWidthSet() {
        return this.supported_channel_width != null && !this.supported_channel_width.isEmpty();
    }

    public boolean checkWPS() {
        return this.wps != null && !this.wps.isEmpty();
    }

    public boolean checkPixie() {
        Utils utils = new Utils();
        return utils.checkPixie(getModel());
    }

    public boolean checkLocked() {
        return this.ap_setup_locked != null && !this.ap_setup_locked.isEmpty();
    }

    public boolean checkFiveGhz() {
        return this.freq > 4900;
    }

    public boolean isChannelWidthSet() {
        return this.channel_width != null && !this.channel_width.isEmpty();
    }
    public String getManufacturer() {
        Utils utils = new Utils();
        this.manufacturer = utils.getVendorByMacFromDB(this.bssid.toUpperCase());
        if (this.manufacturer != null && this.manufacturer.length() < 3) {
            this.manufacturer = "Unknown";
        }
        return this.manufacturer;
    }

    public boolean isWpaSet() {
        return this.wpa != null && !this.wpa.isEmpty();
    }

    public void setStationInfo(){
        this.hasStationInfo = true;
    }

    public boolean hasStationInfo(){
        return this.hasStationInfo;
    }


    public String generateDialogText() {
        WifiNetwork wifiNetwork = this;

        String dialogText =
                "Password: " + (wifiNetwork.getPassword().isEmpty() ? "No password saved" : wifiNetwork.getPassword())+
                "\nWPS Pin: " + (wifiNetwork.getPin().isEmpty() ? "No pin saved" : wifiNetwork.getPin())+
                "\n\uD83D\uDD0D BSSID: " + (wifiNetwork.isBssidSet() ? wifiNetwork.getBssid() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0E Frequency: " + (wifiNetwork.isFreqSet() ? wifiNetwork.getFreq() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBB Capability: " + (wifiNetwork.isCapabilitySet() ? wifiNetwork.getCapability() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF1 SSID: " + (wifiNetwork.isSsidSet() ? wifiNetwork.getSsid() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF6 Supported Rates: " + (wifiNetwork.isSupportedRatesSet() ? wifiNetwork.getSupportedRates() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0C Primary Channel: " + (wifiNetwork.isPrimaryChannelSet() ? wifiNetwork.getPrimaryChannel() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDDFA Network Options: " + (wifiNetwork.isNetworkOptionsSet() ? wifiNetwork.getNetworkOptions() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD12 Network Type: " + (wifiNetwork.isNetworkTypeSet() ? wifiNetwork.getNetworkType() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC Venue Group: " + (wifiNetwork.isVenueGroupSet() ? wifiNetwork.getVenueGroup() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 Venue Type: " + (wifiNetwork.isVenueTypeSet() ? wifiNetwork.getVenueType() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF1 Station Count: " + (wifiNetwork.isStationCountSet() ? wifiNetwork.getStationCount() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0D Channels: " + (wifiNetwork.isChannelsSet() ? wifiNetwork.getChannels() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDDFA Country: " + (wifiNetwork.isCountrySet() ? wifiNetwork.getCountry() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD12 WPS: " + (wifiNetwork.isWpsSet() ? wifiNetwork.getWps() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC WiFi Protected Setup State: " + (wifiNetwork.isWiFiProtectedSetupStateSet() ? wifiNetwork.getWiFiProtectedSetupState() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 AP Setup Locked: " + (wifiNetwork.isApSetupLockedSet() ? wifiNetwork.getApSetupLocked() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0D UUID: " + (wifiNetwork.isUuidSet() ? wifiNetwork.getUuid() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC Manufacturer: " + (wifiNetwork.isManufacturerSet() ? wifiNetwork.getManufacturer() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 Model: " + (wifiNetwork.isModelSet() ? wifiNetwork.getModel() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF1 Model Number: " + (wifiNetwork.isModelNumberSet() ? wifiNetwork.getModelNumber() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0D Serial Number: " + (wifiNetwork.isSerialNumberSet() ? wifiNetwork.getSerialNumber() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC Primary Device Type: " + (wifiNetwork.isPrimaryDeviceTypeSet() ? wifiNetwork.getPrimaryDeviceType() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 Device Name: " + (wifiNetwork.isDeviceNameSet() ? wifiNetwork.getDeviceName() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF1 Config Methods: " + (wifiNetwork.isConfigMethodsSet() ? wifiNetwork.getConfigMethods() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC Environment: " + (wifiNetwork.isEnvironmentSet() ? wifiNetwork.getEnvironment() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 Signal Dbm: " + (wifiNetwork.isSignalDbmSet() ? wifiNetwork.getSignalDbm() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCF1 Last Seen Ms: " + (wifiNetwork.isLastSeenMsSet() ? wifiNetwork.getLastSeenMs() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDD0D Selected Rates: " + (wifiNetwork.isSelectedRatesSet() ? wifiNetwork.getSelectedRates() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCBC Supported Channel Width: " + (wifiNetwork.isSupportedChannelWidthSet() ? wifiNetwork.getSupportedChannelWidth() : "\uD83D\uDD34") +
                "\n" +
                "\uD83D\uDCD7 Channel Width: " + (wifiNetwork.isChannelWidthSet() ? wifiNetwork.getChannelWidth() : "\uD83D\uDD34") +
                "\n";
        return dialogText;
}

    @NonNull
    public String toString() {
        return "WifiNetwork{" +
                "bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                '}';
    }
    public String toBase64String() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(this);
            return Base64.getEncoder().encodeToString(jsonString.getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WifiNetwork fromBase64String(String base64String) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = new String(Base64.getDecoder().decode(base64String));
            return objectMapper.readValue(jsonString, WifiNetwork.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

