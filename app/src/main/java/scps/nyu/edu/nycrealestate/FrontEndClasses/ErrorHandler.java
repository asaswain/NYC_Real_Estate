package scps.nyu.edu.nycrealestate.FrontEndClasses;

import android.content.Context;
import android.widget.Toast;

// This class display errors and exceptions as toast messages
public class ErrorHandler {

    /**
     * Display any error messages
     * @param context - context from activity
     * @param stringID - String.XML text id
     */
    public static void displayError(Context context, int stringID) {
        String errorText = context.getResources().getString(stringID);
        Toast toast = Toast.makeText(context, errorText, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Display any error messages
     * @param context - context from activity
     * @param errorText - text of error message
     */
    public static void displayError(Context context, String errorText) {
        Toast toast = Toast.makeText(context, errorText, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Display error text of any java exceptions that occurred
     * @param context - context from activity
     * @param e - exception object
     */
    public static void displayException(Context context, Exception e) {
        String[] exceptionText = e.toString().split(":");
        String errorText = exceptionText[1];
        Toast toast = Toast.makeText(context, errorText, Toast.LENGTH_LONG);
        toast.show();
    }

}
