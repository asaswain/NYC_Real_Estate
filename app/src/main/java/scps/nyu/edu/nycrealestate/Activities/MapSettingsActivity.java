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
import java.util.List;

import scps.nyu.edu.nycrealestate.BackEndClasses.GoogleMapData;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ContextMenu;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ErrorHandler;
import scps.nyu.edu.nycrealestate.R;

// This activity class contains the settings for configuring the google map
public class MapSettingsActivity extends AppCompatActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener {

    boolean firstTime[] = {true};

    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_settings);

        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

        Spinner mapTypeView = (Spinner) findViewById(R.id.mapType);
        EditText cameraTiltView = (EditText) findViewById(R.id.cameraAngle);

        // Set up map type view spinner

        // listener for map type view
        AdapterView.OnItemSelectedListener cbxListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getId() == (R.id.mapType)) {
                    if (firstTime[0]) {
                        firstTime[0] = false;
                        return;
                    }
                    String type = (String) parent.getItemAtPosition(position);
                    try {
                        GoogleMapData.setMapType(type);
                    } catch (IllegalArgumentException exc) {
                        // do nothing
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // build a list of map types
        String[] typeList = {"Normal", "Hybrid", "Satellite", "Terrain"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeList
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeView.setAdapter(arrayAdapter);
        mapTypeView.setOnItemSelectedListener(cbxListener);


        // load views with current map data

        try {
            String stringMapName = GoogleMapData.getMapName();
            int tmpIndex = getIndex(mapTypeView, stringMapName);
            if (tmpIndex != 0) {
                mapTypeView.setSelection(tmpIndex);
            } else {
                mapTypeView.setSelection(0);
            }
        } catch (IllegalArgumentException e) {
            mapTypeView.setSelection(0);
        }

        if (GoogleMapData.getCameraTilt() != 0) {
            cameraTiltView.setText(Integer.toString(GoogleMapData.getCameraTilt()));
        } else {
            cameraTiltView.setText("");
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
        Intent intent = new Intent(MapSettingsActivity.this, GoogleMapActivity.class);

        // save editText fields (current values of spinners were already saved in spinner listener methods)
        EditText cameraTiltView = (EditText) findViewById(R.id.cameraAngle);

        String stringCameraTilt = cameraTiltView.getText().toString().trim();

        try {
            int cameraTilt;
            if (!(cameraTiltView.getText().toString().trim().equals("") )) {
                cameraTilt = Integer.valueOf(stringCameraTilt);
            } else {
                cameraTilt = 0;
            }
            GoogleMapData.setCameraTilt(cameraTilt);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // display any errors that occurred when starting activity
                ErrorHandler.displayException(MapSettingsActivity.this, e);
            }
        } catch (IllegalArgumentException e) {
            // display any errors that occurred when updating camera angle
            ErrorHandler.displayException(MapSettingsActivity.this, e);
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
                startActivity(ContextMenu.getMenuActivityIntent(this, getResources().getString(R.string.map_settings_menu), position));
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
        mToolBarTextView.setText(R.string.title_activity_settings);
    }

    private List<MenuObject> getMenuObjects() {
        return ContextMenu.getMenuObjects(MapSettingsActivity.this, getResources().getString(R.string.map_settings_menu));
    }
}
