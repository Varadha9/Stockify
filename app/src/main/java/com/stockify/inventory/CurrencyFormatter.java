package com.stockify.inventory;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public final class CurrencyFormatter {

    public static final String PREFS          = "UserPrefs";
    public static final String KEY_SYMBOL     = "currencySymbol";
    public static final String DEFAULT_SYMBOL = "\u20B9";

    private CurrencyFormatter() {}

    public static String getSymbol(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SYMBOL, DEFAULT_SYMBOL);
    }

    public static void setSymbol(Context context, String symbol) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SYMBOL, symbol)
                .apply();
    }

    /** For display — uses device locale so numbers look natural to the user. */
    public static String format(Context context, double amount) {
        return getSymbol(context) + String.format(Locale.getDefault(), "%.2f", amount);
    }

    public static String formatRounded(Context context, double amount) {
        return getSymbol(context) + String.format(Locale.getDefault(), "%.0f", amount);
    }
}
