package scps.nyu.edu.nycrealestate.FrontEndClasses;

import android.content.Context;
import android.content.Intent;

import com.yalantis.contextmenu.lib.MenuObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import scps.nyu.edu.nycrealestate.Activities.GoogleMapActivity;
import scps.nyu.edu.nycrealestate.Activities.ListingFiltersActivity;
import scps.nyu.edu.nycrealestate.Activities.MapSettingsActivity;
import scps.nyu.edu.nycrealestate.Activities.NewsActivity;
import scps.nyu.edu.nycrealestate.R;

// This class gets context menu objects list to display menu on the screen and also get classes for each menu icon
public class ContextMenu {

    private static Context context;

    /**
     * get a list of MenuObjects for context menu
     * @param newContext context from activity
     * @param menuTypes a string of names indicating which menu icons to display
     *
     * @return a list of menuObjects
     */
    public static List<MenuObject> getMenuObjects(Context newContext, String menuTypes) {
        context = newContext;

        List<MenuObject> menuObjects = new ArrayList<>();

        String[] parts = menuTypes.split("-");
        for (String type : parts) {
            try {
                menuObjects.add(parseMenuType(type));
            } catch (InvalidParameterException e) {
                // just ignore invalid menu type strings
            }
        }

        return menuObjects;
    }

    /**
     * parse menu type abbreviations and return a MenuObject for that type
     * @param type type of Menu to return
     *
     * @exception InvalidParameterException if a type is not found in the list of valid menus
     *
     * @return a menuObject containing the menu icon and message
     */
    private static MenuObject parseMenuType (String type) {
        MenuObject menu;

        switch (type)
        {
            case "CloseMenu":
                menu = new MenuObject(context.getResources().getString(R.string.close_menu));
                menu.setResource(R.drawable.close);
                break;
            case "SaveListing":
                menu = new MenuObject(context.getResources().getString(R.string.save_listings));
                menu.setResource(R.drawable.save);
                break;
            case "News" :
                menu = new MenuObject(context.getResources().getString(R.string.news));
                menu.setResource(R.drawable.news);
                break;
            case "GoogleMap" :
                menu = new MenuObject(context.getResources().getString(R.string.google_map));
                menu.setResource(R.drawable.map);
                break;
            case "MapSettings" :
                menu = new MenuObject(context.getResources().getString(R.string.map_settings));
                menu.setResource(R.drawable.search);
                break;
            case "ListingSettings" :
                menu = new MenuObject(context.getResources().getString(R.string.listing_filters));
                menu.setResource(R.drawable.marker);
                break;
            default :
                throw new InvalidParameterException("Trying to display an unknown menu type");
        }

        return menu;
    }

    /**
     * Get Intent for the item in the menu clicked by the user
     *
     * @param newContext context from activity
     * @param menuTypes a string of names indicating which activities are available from the menu
     * @param position the index of the menu item clicked
     *
     * @return an intent for the menu item clicked
     */
    public static Intent getMenuActivityIntent(Context newContext, String menuTypes, int position) {
        context = newContext;

        String[] parts = menuTypes.split("-");
        String type = parts[position];
        return new Intent(context, parseMenuClass(type));
    }


    /**
     * parse menu type abbreviations and return the Activityclass for the menu type
     * @param type type of Menu class to return
     *
     * @exception InvalidParameterException if a type is not found in the list of valid menus
     *
     * @return a Class object containing the class to open for the menu icon
     */
    private static Class parseMenuClass (String type) {
        switch (type)
        {
            case "CloseMenu":
                throw new InvalidParameterException("Cannot open a screen for CloseMenu");

            case "SaveListing":
                throw new InvalidParameterException("Cannot open a screen for SaveListing");

            case "News" :
                return NewsActivity.class;

            case "GoogleMap" :
                return GoogleMapActivity.class;

            case "MapSettings" :
                return MapSettingsActivity.class;

            case "ListingSettings" :
                return ListingFiltersActivity.class;

            default :
                throw new InvalidParameterException("Trying to open an unknown menu type");
        }
    }
}
