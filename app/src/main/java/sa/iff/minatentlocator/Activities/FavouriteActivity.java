package sa.iff.minatentlocator.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sa.iff.minatentlocator.Utils.ExpandableListAdapter;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.SharedPrefArrayUtils;

public class FavouriteActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private LinearLayout favListItem;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private SharedPreferences sharedPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;
    private ImageView itemDeleteButton;
    private Context context;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        sharedPreferences = getSharedPreferences("Favourite_Management", Context.MODE_PRIVATE);
        path = context.getExternalFilesDir(null);

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
            public boolean onChildClick(ExpandableListView parent, final View listView, final int groupPosition, final int childPosition, long id) {
                /*itemDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {*/
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
                final ArrayList<String> favPlaces = sharedPrefArrayUtils.loadArray();
                //final SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
                //sharedPrefEditor.remove("graph_" + place + "_" + favPlaces.get(childPosition));
                final File readNodes = new File(path, "graph_" + place + "_" + favPlaces.get(childPosition) + "_nodes.csv");
                final File readMap = new File(path, "graph_" + place + "_" + favPlaces.get(childPosition) + "_map.csv");
                listView.setVisibility(View.GONE);
                expListView.refreshDrawableState();

                final Handler deleteHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.arg1 != 0) {
                            favPlaces.remove(childPosition);
                            sharedPrefArrayUtils.saveArray(favPlaces);
                            //sharedPrefEditor.apply();
                            readNodes.delete();   readMap.delete();

                            prepareListData();
                            listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                            expListView.setAdapter(listAdapter);
                            expListView.refreshDrawableState();
                            for (int i=0; i<listDataHeader.size(); i++)
                                expListView.expandGroup(i, true);
                        }
                        else listView.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                Snackbar undoDelete = Snackbar.make(listView, "Undo Delete \"" + favPlaces.get(childPosition) + "\"", Snackbar.LENGTH_LONG);
                undoDelete.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Message msg = new Message();
                                msg.arg1 = 0;
                                deleteHandler.sendMessage(msg);
                            }
                        }).show();
                undoDelete.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != DISMISS_EVENT_ACTION) {
                            Message msg = new Message();
                            msg.arg1 = 1;
                            deleteHandler.sendMessage(msg);
                        }
                    }
                });
            /*}
        });*/
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
