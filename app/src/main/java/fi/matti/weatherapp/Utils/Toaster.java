package fi.matti.weatherapp.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This is a class to simplify showing toasts.
 *
 * Created by Niko on 16.3.2018.
 * Last updated by Niko on 23.4.2018
 */

public class Toaster {
    public static void show(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
