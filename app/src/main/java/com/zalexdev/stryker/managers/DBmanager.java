package com.zalexdev.stryker.managers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zalexdev.stryker.utils.Utils;

public class DBmanager {
    public SQLiteDatabase dbMacs = null;
    public SQLiteDatabase dbCodenames = null;
    public SQLiteDatabase dbAdapters = null;

    public String getVendorByMacFromDB(String mac) {
        String vendor = "";
        try {
            if (dbMacs == null || !dbMacs.isOpen()) {
                dbMacs = SQLiteDatabase.openDatabase(
                        Utils.fileDir + "vendors.db",
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
            }
            Cursor cursor = dbMacs.rawQuery(
                    "select MacPrefix,VendorName from macvendor where MacPrefix LIKE '%" + mac.substring(0, 8).toUpperCase() + "%' COLLATE NOCASE",
                    null
            );
            if (cursor.moveToFirst()) {
                vendor = cursor.getString(1);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toTitleCase(
                vendor.replace("Technologies", "").replace("Technology", "").replace("Co.,ltd", "")
                        .replace("International", "").replace("Communications Corporation", "")
        );
    }

    public String getDeviceByCodeNameFromDB(String codename) {
        String model = "";
        try {
            if (dbCodenames == null || !dbCodenames.isOpen()) {
                dbCodenames = SQLiteDatabase.openDatabase(
                        Utils.fileDir + "codenames.db",
                        null,
                        SQLiteDatabase.OPEN_READONLY
                );
            }
            Cursor cursor = dbCodenames.rawQuery(
                    "SELECT manufacture,model FROM codename WHERE codename = '" + codename + "';",
                    null
            );
            if (cursor.moveToFirst()) {
                model = cursor.getString(0) + " " + cursor.getString(1).replace(cursor.getString(0), "");
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return toTitleCase(model);
        } catch (NullPointerException ignored) {
            return model;
        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            if (s.length() > 1) {
                sb.append(Character.toUpperCase(s.charAt(0)))
                        .append(s.substring(1)).append(" ");
            }
        }
        return sb.toString();
    }
}
