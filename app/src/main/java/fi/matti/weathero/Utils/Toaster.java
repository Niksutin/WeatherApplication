package fi.matti.weathero.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class is for showing toasts.
 *
 * Created by matti on 16.3.2018.
 */

public class Toaster {
    public static void show(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
