package com.jetlee.simpleedittext;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

/**
 * @Author：Jet啟思
 * @Date:2018/7/24 15:07
 */
public class Utils {

    public static float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static Bitmap getBitmap(Resources res, int size, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        options.inDensity = Math.min(options.outWidth, options.outHeight);
        options.inTargetDensity = size;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
