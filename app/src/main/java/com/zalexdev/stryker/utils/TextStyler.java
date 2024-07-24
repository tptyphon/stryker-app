package com.zalexdev.stryker.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class TextStyler {

    public static String bold(String text) {
        return "<b>" + text + "</b>";
    }

    public static String italic(String text) {
        return "<i>" + text + "</i>";
    }

    public static String underline(String text) {
        return "<u>" + text + "</u>";
    }

    public static String color(String text, String color) {
        return "<font color=\"" + color + "\">" + text + "</font>";
    }

    public static String size(String text, int size) {
        return "<font size=\"" + size + "\">" + text + "</font>";
    }

    public static String yellow(String text) {
        return color(text, "yellow");
    }

    public static String red(String text) {
        return color(text, "red");
    }

    public static String green(String text) {
        return color(text, "green");
    }

    public static String blue(String text) {
        return color(text, "blue");
    }

    public static String orange(String text) {
        return color(text, "orange");
    }

    public static String purple(String text) {
        return color(text, "purple");
    }

    public static String cyan(String text) {
        return color(text, "cyan");
    }

    public static String white(String text) {
        return color(text, "white");
    }

    public static String black(String text) {
        return color(text, "black");
    }

    public static String gray(String text) {
        return color(text, "gray");
    }

    public static String maroon(String text) {
        return color(text, "maroon");
    }

    public static String olive(String text) {
        return color(text, "olive");
    }

    public static String lime(String text) {
        return color(text, "lime");
    }

    public static String teal(String text) {
        return color(text, "teal");
    }

    public static String navy(String text) {
        return color(text, "navy");
    }

    public static String fuchsia(String text) {
        return color(text, "fuchsia");
    }

    public static String aqua(String text) {
        return color(text, "aqua");
    }

    public static String info(String text) {
        return cyan("[◯] "+text);
    }

    public static String success(String text) {
        return green("[✓] "+text);
    }

    public static String warning(String text) {
        return yellow("[!] "+text);
    }

    public static String danger(String text) {
        return red("[⚠] "+text);
    }

    public static Spanned convert(String text) {
        return android.text.Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
    }

    public static void makeScrollable(TextView textView) {
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


}
