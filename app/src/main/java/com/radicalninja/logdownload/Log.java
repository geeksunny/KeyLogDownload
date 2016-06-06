package com.radicalninja.logdownload;

import com.sun.istack.internal.Nullable;
import org.apache.http.util.TextUtils;

/**
 * Basic console logger utility inspired by the Android SDK's Log class.
 */
public class Log {

    private static Log instance = new Log();
    // todo: static getters/setters
    private int tagWidth = 25;
    private boolean autoExpandWidth = false;
    private int autoExpandWidthPadding = 3;

    public static void setTagWidth(int tagWidth) {
        instance.tagWidth = tagWidth;
    }

    public static void setAutoExpandWidth(boolean autoExpandWidth) {
        instance.autoExpandWidth = autoExpandWidth;
    }

    public static void setAutoExpandWidthPadding(int autoExpandWidthPadding) {
        instance.autoExpandWidthPadding = autoExpandWidthPadding;
    }

    private void log(final String msg) {
        System.out.println(msg);
    }

    private void log(@Nullable final String tag, final String msg) {
        final String line = String.format("%s : %s", tag, msg);
        log(line);
    }

    private void log(final char level, @Nullable final String tag, final String msg) {
        final String line = String.format(" %s| %s%s", level, prepareTag(tag), msg);
        instance.log(line);
    }

    private String prepareTag(final String tag) {
        if (TextUtils.isEmpty(tag)) {
            return "";
        }
        final int size = tag.length();
        final int diff = tagWidth - size;
        if (autoExpandWidth && size > tagWidth) {
            tagWidth = size + autoExpandWidthPadding;
        }
        String newTag;
        if (size < tagWidth) {
            final StringBuilder padding = new StringBuilder(diff);
            for (int i = 0; i < diff; i++) {
                padding.append(" ", 0, diff);
            }
            padding.append(tag);
            newTag = padding.toString();
        } else if (size > tagWidth) {
            newTag = String.format("%s...", tag.substring(0, tagWidth-3));
        } else {
            newTag = tag;
        }
        return String.format("%s : ", newTag);
    }

    public static void v(@Nullable final String tag, final String msg) {
        instance.log('V', tag, msg);
    }

    public static void i(@Nullable final String tag, final String msg) {
        instance.log('I', tag, msg);
    }

    public static void d(@Nullable final String tag, final String msg) {
        instance.log('D', tag, msg);
    }

    public static void w(@Nullable final String tag, final String msg) {
        instance.log('W', tag, msg);
    }

    public static void e(@Nullable final String tag, final String msg) {
        instance.log('E', tag, msg);
    }

}