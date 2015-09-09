package scps.nyu.edu.nycrealestate.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.List;

import scps.nyu.edu.nycrealestate.BackEndClasses.GoogleMapData;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ContextMenu;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ErrorHandler;
import scps.nyu.edu.nycrealestate.R;

// This activity class contains the filters for choosing which listings to display on the google map
public class ListingFiltersActivity extends AppCompatActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener {

    boolean firstTime[] = {true};

    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_filters);

        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

        EditText maxPriceView = (EditText) findViewById(R.id.targetMaxPrice);
        Spinner nbrBedroomsView = (Spinner) findViewById(R.id.targetNbrBedrooms);

        // Set up number of bedrooms spinner

        // listener for number bedrooms view
        AdapterView.OnItemSelectedListener cbxListener2 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getId() == (R.id.targetNbrBedrooms)) {
                    if (firstTime[0]) {
                        firstTime[0] = false;
                        return;
                    }
                    String type = (String) parent.getItemAtPosition(position);
                    try {
                        if (!type.equals("All")) {
                            GoogleMapData.setTargetNbrBedrooms(Integer.valueOf(type));
                        } else {
                            GoogleMapData.setTargetNbrBedrooms(null);
                        }
                    } catch (IllegalArgumentException exc) {
                        // do nothing
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // build a list of bedrooms
        String[] nbrList = {"All","0","1","2","3","4","5"};

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nbrList
        );

        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nbrBedroomsView.setAdapter(arrayAdapter2);
        nbrBedroomsView.setOnItemSelectedListener(cbxListener2);

        // load views with current filter data

        if (GoogleMapData.getTargetMaxPrice() != null) {
            if (!(GoogleMapData.getTargetMaxPrice() == null)) {
                DecimalFormat formatter = new DecimalFormat("############.00");
                String formattedPrice = formatter.format(GoogleMapData.getTargetMaxPrice());
                maxPriceView.setText("" + formattedPrice);
            } else {
                maxPriceView.setText("All");
            }
        } else {
            maxPriceView.setText("All");
        }

        if (GoogleMapData.getTargetNbrBedrooms() != null) {
            String stringNbrBedrooms = Integer.toString(GoogleMapData.getTargetNbrBedrooms());
            int tmpIndex = getIndex(nbrBedroomsView, stringNbrBedrooms);
            if (tmpIndex != 0) {
                nbrBedroomsView.setSelection(tmpIndex);
            } else {
                nbrBedroomsView.setSelection(0);
            }
        } else {
            nbrBedroomsView.setSelection(0);
        }
    }

    // get index of myString in spinner view
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    // go to Google Maps page
    public void viewMap(View v) {
        Intent intent = new Intent(ListingFiltersActivity.this, GoogleMapActivity.class);

        // save editText fields (current values of spinners were already saved in spinner listener methods)
        EditText maxPriceView = (EditText) findViewById(R.id.targetMaxPrice);
        String stringMaxPrice = maxPriceView.getText().toString().trim();

        if (!(maxPriceView.getText().toString().trim().equals(""))) {

            try {
                if (!(stringMaxPrice.equals("All"))) {
                    double maxPrice = Float.valueOf(stringMaxPrice);
                    GoogleMapData.setTargetMaxPrice(maxPrice);
                } else {
                    GoogleMapData.setTargetMaxPrice(null);
                }

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ErrorHandler.displayException(ListingFiltersActivity.this, e);
                }
            } catch (IllegalArgumentException e) {
                ErrorHandler.displayException(ListingFiltersActivity.this, e);
            }
        } else {
            ErrorHandler.displayError(ListingFiltersActivity.this, R.string.filters_save_error);
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        parseMenuClick(position);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        parseMenuClick(position);
    }

    private void parseMenuClick(int position) {
        if (position > 0) {
            try {
                startActivity(ContextMenu.getMenuActivityIntent(this, getResources().getString(R.string.listing_filters_menu), position));
            } catch (InvalidParameterException e) {
                ErrorHandler.displayException(this, e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                mMenuDialogFragment.show(fragmentManager, "ContextMenuDialogFragment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e) {
            // do nothing
        }
        mToolBarTextView.setText(R.string.title_activity_filters);
    }

    private List<MenuObject> getMenuObjects() {
        return ContextMenu.getMenuObjects(ListingFiltersActivity.this, getResources().getString(R.string.listing_filters_menu));
    }
}
