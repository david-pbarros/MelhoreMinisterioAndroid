package br.com.dbcorp.melhoreministerio;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * Created by david.barros on 16/11/2015.
 */
public class ScreenTest {

    public String test(Activity context) {
        String density = null;

        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                density = "small ";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                density = "normal ";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                density = "large ";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                density = "xlarge ";
                break;
            default:
                density = "";
        }

        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_280:
                density += "280";
                break;
            case DisplayMetrics.DENSITY_360:
                density += "360";
                break;
            case DisplayMetrics.DENSITY_400:
                density += "400";
                break;
            case DisplayMetrics.DENSITY_420:
                density += "420";
                break;
            case DisplayMetrics.DENSITY_560:
                density += "560";
                break;
            case DisplayMetrics.DENSITY_LOW:
                density += "ldpi";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                density += "mdpi";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                density += "hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                density += "xhdpi";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                density += "xxhdpi";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                density += "xxxhdpi";
                break;
        }

        return density;
    }
}
