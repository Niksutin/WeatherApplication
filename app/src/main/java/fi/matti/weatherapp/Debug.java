package fi.matti.weatherapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class is for simple debug prints and toasts.
 *
 * Created by matti on 8.1.2018.
 */

class Debug {

    private static String methodTag = new Exception().getStackTrace()[1].getMethodName();
    private static String classTag = getCallerClassName();

    /**
     * Get the class that called this function.
     *
     * @return name of the class where this method was called in.
     */
    private static String getCallerClassName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            if (!element.getClassName().equals(Debug.class.getName()) &&
                    element.getClassName().indexOf("java.lang.Thread") != 0) {
                return element.getClassName();
            }
        }
        return null;
    }

    /**
     * Print information for called class name, called method and displays
     * user chosen message.
     *
     * @param message message which will be printed.
     */
    static void print(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(classTag, methodTag + ": " + message);
        }
    }

    /**
     * Show a toaster with given message.
     *
     * @param context Context for the toast.
     * @param message message to be shown in the toast.
     */
    static void toast(Context context, String message) {
        Toast toast = Toast.makeText(context,
                classTag + ": " + methodTag +": " + message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 20);
        toast.show();
    }
}
