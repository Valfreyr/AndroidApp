/**
 * Class Description    : Class for Changing to DarkTheme from LightTheme
 * Contributors         : Katarzyna Nozka
 */
package com.ncl.csc2022.team10;

import android.app.Activity;
import android.content.Intent;

public class ThemeChanger {
    private static int Theme;
    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;

    public static void changeToTheme(Activity activity, int theme)
    {
        Theme = theme;
        activity.finish();
        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra("ThemeChanger", theme);
        activity.startActivity(intent);
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity, int selectedTheme)
    {
        switch (selectedTheme)
        {
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme);
                break;

            case THEME_DARK:
                activity.setTheme(R.style.AppThemeDark);
                break;

            default:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }
}

