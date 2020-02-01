package com.allvens.allworkouts.assets;

import android.content.Context;

public class Helper {

    private Context context;

    /**
     * Consist of helper methods that aren't specific to classes but can be re-used
     * @param context
     */
    public Helper(Context context){
        this.context = context;
    }

    public int get_dpFromPixels(int dpPixels){
        final float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dpPixels * scale + 0.5f));
    }
}
