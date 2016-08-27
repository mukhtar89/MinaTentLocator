package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sa.iff.minatentlocator.ExpandableListAdapter;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.SharedPrefArrayUtils;

public class FavouriteActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private SharedPreferences sharedPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;
    private ImageView itemDeleteButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        sharedPreferences = getSharedPreferences("Favourite_Management", Context.MODE_PRIVATE);

        expListView = (ExpandableListView) findViewById(R.id.fav_exp_list_view);
        itemDeleteButton = (ImageView) findViewById(R.id.fav_list_item_delete);

        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        for (int i=0; i<listDataHeader.size(); i++)
           expListView.expandGroup(i, true);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                itemDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String place = null;
                        switch(groupPosition) {
                            case 0: place = "Mina";
                                break;
                            case 1: place = "Makkah";
                                break;
                            case 2: place = "Aziziyah";
                                    break;
                        }
                        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, place);
                        ArrayList<String> favPlaces = sharedPrefArrayUtils.loadArray();
                        favPlaces.remove(childPosition);
                        sharedPrefArrayUtils.saveArray(favPlaces);
                        prepareListData();
                        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);
                        expListView.refreshDrawableState();
                        Snackbar.make(v, "Undo Delete Favourite item", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    }
                });
                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Mina");
        listDataHeader.add("Makkah");
        listDataHeader.add("Aziziyah");

        // Adding child data
        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, "Mina");
        List<String> Mina = sharedPrefArrayUtils.loadArray();

        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, "Makkah");
        List<String> Makkah = sharedPrefArrayUtils.loadArray();

        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, "Aziziyah");
        List<String> Aziziyah = sharedPrefArrayUtils.loadArray();

        listDataChild.put(listDataHeader.get(0), Mina); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Makkah);
        listDataChild.put(listDataHeader.get(2), Aziziyah);
    }
}
