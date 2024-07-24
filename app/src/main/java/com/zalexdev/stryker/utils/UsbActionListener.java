package com.zalexdev.stryker.utils;

import android.hardware.usb.UsbDevice;

public interface UsbActionListener {
    void onUsbAttached(UsbDevice device);
    void onUsbDetached(UsbDevice device);
}
