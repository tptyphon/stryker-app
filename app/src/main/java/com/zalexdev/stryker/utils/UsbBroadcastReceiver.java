package com.zalexdev.stryker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbBroadcastReceiver extends BroadcastReceiver {

    private UsbActionListener listener;

    public UsbBroadcastReceiver(UsbActionListener listener) {
        this.listener = listener;
    }

    public UsbBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null && listener != null) {
                listener.onUsbAttached(device);
            }
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null && listener != null) {
                listener.onUsbDetached(device);
            }
        }
    }
}

