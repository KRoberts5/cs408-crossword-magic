package mcis.jsu.edu.crosswordmagic;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.*;
import android.support.v4.view.*;

import java.util.ArrayList;
import static android.provider.BaseColumns._ID;

public class MainActivityView extends AppCompatActivity {



    private CrosswordMagicViewModel model;
    private PuzzleDatabase puzzleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        model = ViewModelProviders.of(this).get(CrosswordMagicViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configureTabLayout();

        /* Save Context Reference and Default Puzzle ID in Model */

        model.setContext(this);
        model.setPuzzleID(R.raw.puzzle);

        /* Get Display Properties; Save to Model */

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        model.setWindowHeightDp(dm.heightPixels);
        model.setWindowWidthDp(dm.widthPixels);
        model.setWindowOverheadDp(getOverheadHeight());

        puzzleDB = new PuzzleDatabase(this);



        model.setDatabase(puzzleDB);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main_activity_view, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void configureTabLayout() {

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Main Menu"));
        tabLayout.addTab(tabLayout.newTab().setText("Puzzle"));
        tabLayout.addTab(tabLayout.newTab().setText("Clues"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });

    }

    private int getOverheadHeight() {

        /* Compute Current Height of Action Bar, Status Bar, and View Pager */

        int height = 0;

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            height += TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = height + height + getResources().getDimensionPixelSize(resourceId);
        }

        return height;

    }



}
