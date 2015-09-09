package scps.nyu.edu.nycrealestate.Activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import scps.nyu.edu.nycrealestate.BackEndClasses.ArticleAdapter;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ContextMenu;
import scps.nyu.edu.nycrealestate.FrontEndClasses.ErrorHandler;
import scps.nyu.edu.nycrealestate.R;

// This activity class will displays a listview containing sample real estate articles from patrse.com
// (eventually we'd like to replace this with articles from an API)
public class NewsActivity extends AppCompatActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener {

    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

        final ListView listView = (ListView)findViewById(R.id.listView);

        // draw markers for saved listings
        ParseQuery<ParseObject> query = ParseQuery.getQuery("News");

        // select listings based on map filter

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {

                    ArrayList<String> articleList = new ArrayList<>();
                    boolean addedArticle = false;


                    for (int i = 0; i < results.size(); i++) {
                        ParseObject listing = results.get(i);
                        String articleHeading = listing.getString("Heading");
                        String articleSummary = listing.getString("Summary");
                        addedArticle = true;

                        // ~ is an uncommon character to see in a news article
                        articleList.add(i, (articleHeading + "~" + articleSummary));
                    }

                    ArticleAdapter arrayAdapter = new ArticleAdapter(
                            NewsActivity.this,
                            articleList
                    );

                    listView.setAdapter(arrayAdapter);

                    // if no records found display no data image
                    if (!addedArticle) {
                        ImageView imageView = (ImageView)findViewById(R.id.empty);
                        listView.setEmptyView(imageView);   //Display this TextView when table contains no records.
                    }

                } else {
                    // something went wrong
                }
            }
        });
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
                startActivity(ContextMenu.getMenuActivityIntent(this, getResources().getString(R.string.news_menu), position));
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
        mToolBarTextView.setText(R.string.title_activity_news);
    }

    private List<MenuObject> getMenuObjects() {
        return ContextMenu.getMenuObjects(NewsActivity.this, getResources().getString(R.string.news_menu));
    }
}
