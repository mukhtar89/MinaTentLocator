package sa.iff.minatentlocator.Activities;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import sa.iff.minatentlocator.AboutInfo;
import sa.iff.minatentlocator.NavFragment;
import sa.iff.minatentlocator.NavPageAdapter;
import sa.iff.minatentlocator.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavFragment.OnFragmentInteractionListener {

    private Context context;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private final Handler drawerActionHandler = new Handler();
    private DrawerLayout drawerLayout;
    private NavPageAdapter navPageAdapter;

    private int navItemId;
    private AboutInfo aboutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        aboutInfo = new AboutInfo(this);
        // load saved navigation state if present
        if (null == savedInstanceState) {
            navItemId = 0;
        } else {
            navItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navDrawer);
        navigationView.setNavigationItemSelectedListener(this);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        navPageAdapter = new NavPageAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(navPageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    /*public void onFABClick(View view) {
        NavFragment Mina, Makkah, Aziziyah;
        Mina = (NavFragment) navPageAdapter.getItem(0);
        Aziziyah = (NavFragment) navPageAdapter.getItem(2);
        FragmentManager fragmentManager= getSupportFragmentManager();
        if (fragmentManager.getFragments().get(viewPager.getCurrentItem()) instanceof navPageAdapter.getItem(0)) {
            //if (Mina!= null) {
                Mina.fabOnClick();
            //}
        } else if (fragmentManager.getFragments().get(viewPager.getCurrentItem()).equals(Aziziyah)) {
            if (Aziziyah!= null) {
                Aziziyah.fabOnClick();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.about) {
            try {
                aboutInfo.showDialog();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, navItemId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        navItemId = item.getItemId();

        drawerLayout.closeDrawer(GravityCompat.START);
        drawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (navItemId) {
                    case R.id.nav_mina: viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_makkah: viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_aziziyah: viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_manage: viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_share: viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_send: viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_about: try {
                                    aboutInfo.showDialog();
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                        break;
                    default: break;

                }
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onRadioButtonClicked (View view) {
        boolean stateChecked = NavFragment.radioChecked;
        NavFragment.radioChecked = ((RadioButton) view).isChecked();
        if (!stateChecked && NavFragment.category_row.getVisibility() == View.VISIBLE) {
            NavFragment.category_row.setVisibility(View.GONE);
            NavFragment.place_row.setVisibility(View.GONE);
        }
        else if (stateChecked && NavFragment.category_row.getVisibility() == View.GONE) {
            NavFragment.myLocButton.setChecked(false);
            NavFragment.radioChecked = false;
            NavFragment.category_row.setVisibility(View.VISIBLE);
            NavFragment.place_row.setVisibility(View.VISIBLE);
        }
    }
}
