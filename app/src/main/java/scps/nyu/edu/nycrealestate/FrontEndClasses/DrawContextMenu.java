package scps.nyu.edu.nycrealestate.FrontEndClasses;

import android.content.Context;

import com.yalantis.contextmenu.lib.MenuObject;

import java.util.ArrayList;
import java.util.List;

import scps.nyu.edu.nycrealestate.R;

// Draw context menu on screen
public class DrawContextMenu {

   private static Context context;

    /**
     * get a list of MenuObjects for context menu
     * @param newContext - context from activity
     * @param menuTypes - a string of characters indicating what menu icons to display
     * @return - a list of menuObjects
     */
    public static List<MenuObject> getMenuObjects(Context newContext, String menuTypes) {
        context = newContext;

        List<MenuObject> menuObjects = new ArrayList<>();

        for (int i = 0; i < menuTypes.length(); i++ ) {
            String type = menuTypes.substring(i,i+1);
            menuObjects.add(parseMenuType(type));
        }

        return menuObjects;
    }

    /**
     * parse menu type abbreviations and return a MenuObject for that type
     * @param type - type of Menu to return
     * @return - a menuObject containing the menu icon and message
     */
    private static MenuObject parseMenuType (String type) {
        MenuObject menu = null;

        switch (type)
        {
            case "C":
                menu = new MenuObject(context.getResources().getString(R.string.close_menu));
                menu.setResource(R.drawable.close);
                break;
            case "S":
                menu = new MenuObject(context.getResources().getString(R.string.save_listings));
                menu.setResource(R.drawable.save);
                break;
            case "N" :
                menu = new MenuObject(context.getResources().getString(R.string.news));
                menu.setResource(R.drawable.news);
                break;
            case "G" :
                menu = new MenuObject(context.getResources().getString(R.string.google_map));
                menu.setResource(R.drawable.map);
                break;
            case "M" :
                menu = new MenuObject(context.getResources().getString(R.string.map_settings));
                menu.setResource(R.drawable.search);
                break;
            case "L" :
                menu = new MenuObject(context.getResources().getString(R.string.listing_filters));
                menu.setResource(R.drawable.marker);
                break;
            case "V" :
                menu = new MenuObject(context.getResources().getString(R.string.google_voice));
                menu.setResource(R.drawable.voicesearch);
        }

        return menu;
    }

}
