package sa.iff.minatentlocator.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import sa.iff.minatentlocator.Activities.MapsActivity;
import sa.iff.minatentlocator.MapUtils.Locations;
import sa.iff.minatentlocator.ProtoBufUtil.GetFilesWeb;
import sa.iff.minatentlocator.R;
import sa.iff.minatentlocator.SharedPrefArrayUtils;
import sa.iff.minatentlocator.Dialogs.SwitchNetworkDialog;
import sa.iff.minatentlocator.Utils.PermissionManager;

/**
 * Created by mukht on 9/6/2016.
 */
public class ScrollViewFragment extends Fragment {

    private static final String PLACE = "place";
    private AutoCompleteTextView editFrom, editTo;
    public RelativeLayout category_row, place_row, favourites_row;
    private static Locations locations;
    private Hashtable<String, String[]> locationList;
    private String place, favPlace;
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private NestedScrollView nestedScrollView;

    public Switch myLocSwitch;
    private Snackbar snackbarDownloadFinished;
    public boolean radioChecked, favSelected;
    private AtomicBoolean fileStillDownloading;
    private Spinner listCategoryTo, listCategoryFrom, listFavourites;
    private ArrayList<String> poleNumbersFrom, poleNumbersTo;
    private ArrayList<String> metaFiles;
    private ArrayAdapter<String> locationAdapter;

    private SwitchNetworkDialog switchNetworkDialog;
    private SharedPreferences sharedPreferences, sharedDefaultPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;
    private Handler handleDownloadFinished;
    private Message msg;


    public static Fragment newInstance(String place, SwitchNetworkDialog switchNetworkDialog) {
        Bundle args = new Bundle();
        args.putString(PLACE, place);
        ScrollViewFragment fragment = new ScrollViewFragment();
        fragment.switchNetworkDialog = switchNetworkDialog;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);

        MaterialViewPagerHelper.registerScrollView(getActivity(), nestedScrollView);
        //mScrollView.setScrollViewCallbacks(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = getArguments().getString(PLACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_scrollview, container, false);
        context = getActivity().getApplicationContext();
        sharedPreferences = getActivity().getSharedPreferences("Favourite_Management", Context.MODE_PRIVATE);
        sharedPrefArrayUtils = new SharedPrefArrayUtils(sharedPreferences, place);
        radioChecked = false;     favSelected = false;    favPlace = null;

        try {
            locations = new Locations(context);
            locationList = locations.parseLocations(place);
            poleNumbersTo = poleNumbersFrom = new ArrayList<>(locationList.keySet());

            listCategoryTo = (Spinner) rootView.findViewById(R.id.list_category_to);
            listCategoryFrom = (Spinner) rootView.findViewById(R.id.list_category_from);
            listFavourites = (Spinner) rootView.findViewById(R.id.list_favourite);
            final ArrayList<String> categoryList = new ArrayList<>(locations.returnCategories(place));
            Collections.sort(categoryList);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, categoryList);
            listCategoryTo.setAdapter(categoryAdapter);
            listCategoryFrom.setAdapter(categoryAdapter);

            sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            listCategoryFrom.setSelection(categoryList.indexOf
                    (sharedDefaultPreferences.getString(place.toLowerCase()+"_cat_from", categoryList.get(0))));
            listCategoryTo.setSelection(categoryList.indexOf
                    (sharedDefaultPreferences.getString(place.toLowerCase()+"_cat_to", categoryList.get(0))));

            editFrom = (AutoCompleteTextView) rootView.findViewById(R.id.auto_from);
            editTo = (AutoCompleteTextView) rootView.findViewById(R.id.auto_to);
            editFrom.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    editFrom.showDropDown();
                    return false;
                }
            });
            editFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hideKeyboard(context, view);
                }
            });
            editTo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    editTo.showDropDown();
                    return false;
                }
            });
            editTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hideKeyboard(context, view);
                }
            });
            listCategoryFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    poleNumbersFrom = new ArrayList<>();
                    Iterator<Map.Entry<String, String[]>> iter = locationList.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String[]> entrySet = iter.next();
                        if (entrySet.getValue()[1].equals(categoryList.get(position)))
                            poleNumbersFrom.add(entrySet.getKey());
                    }
                    Collections.sort(poleNumbersFrom);
                    locationAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, poleNumbersFrom);
                    editFrom.setAdapter(locationAdapter);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editFrom.showDropDown();
                }
            });
            listCategoryTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    poleNumbersTo = new ArrayList<>();
                    Iterator<Map.Entry<String, String[]>> iter = locationList.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String[]> entrySet = iter.next();
                        if (entrySet.getValue()[1].equals(categoryList.get(position)))
                            poleNumbersTo.add(entrySet.getKey());
                    }
                    Collections.sort(poleNumbersTo);
                    locationAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, poleNumbersTo);
                    editTo.setAdapter(locationAdapter);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editTo.showDropDown();
                }
            });

            favourites_row = (RelativeLayout) rootView.findViewById(R.id.favourites_row);
            final ArrayList<String> favPlaces = sharedPrefArrayUtils.loadArray();
            if (favPlaces.size() == 0)
                favourites_row.setVisibility(View.GONE);
            else {
                Collections.sort(favPlaces);
                favPlaces.add("Select One...");
                ArrayAdapter<String> favAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_fav, favPlaces);
                listFavourites.setAdapter(favAdapter);
                listFavourites.setSelection(favPlaces.size()-1);
                listFavourites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != (favPlaces.size()-1)) {
                            favSelected = true;
                            favPlace = favPlaces.get(position);
                            category_row.setVisibility(View.GONE);
                            place_row.setVisibility(View.GONE);
                        }
                        else {
                            favSelected = false;
                            category_row.setVisibility(View.VISIBLE);
                            place_row.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            category_row = (RelativeLayout) rootView.findViewById(R.id.category_row);
            place_row = (RelativeLayout) rootView.findViewById(R.id.place_row);
            myLocSwitch = (Switch) rootView.findViewById(R.id.my_loc_switch);
            myLocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        radioChecked = true;
                        favourites_row.setVisibility(View.GONE);
                        category_row.setVisibility(View.GONE);
                        place_row.setVisibility(View.GONE);
                    } else {
                        radioChecked = false;
                        if (favPlaces.size() != 0)
                            favourites_row.setVisibility(View.VISIBLE);
                        category_row.setVisibility(View.VISIBLE);
                        place_row.setVisibility(View.VISIBLE);
                    }
                }
            });

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabOnClick();
                }
            });

            handleDownloadFinished = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1)
                        fileStillDownloading.set(false);
                    if (msg.arg1 == 0 && !fileStillDownloading.get())
                        getMaps();
                    return false;
                }
            });
            fileStillDownloading = new AtomicBoolean(false);
            metaFiles = new ArrayList<>(Arrays.asList(context.getExternalFilesDir(null).list()));
            coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout_fragment);
            snackbarDownloadFinished = Snackbar.make(coordinatorLayout, place + " Map metadata files download has finished!", Snackbar.LENGTH_SHORT);
            if (!metaFiles.contains("vertexes_" + place + ".ser")) {
                /*dialogWebConnect = new DialogWebConnect(this.context, getActivity());
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null)
                    dialogWebConnect.show();
                if (activeNetworkInfo != null) {
                    while (!activeNetworkInfo.isConnected())
                        dialogWebConnect.show();
                    new GetFilesWeb(context, place, snackbarDownloadFinished, handleDownloadFinished).execute(locations.returnUrls(place)[0], locations.returnUrls(place)[1]);
                    fileStillDownloading.set(true);
                }*/
                BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (switchNetworkDialog.isOnline(context)) {
                            new GetFilesWeb(context, place, snackbarDownloadFinished, handleDownloadFinished).execute(locations.returnUrls(place)[0], locations.returnUrls(place)[1]);
                            fileStillDownloading.set(true);
                            metaFiles.add("vertexes_" + place + ".ser");
                        }
                    }
                };
                IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                context.registerReceiver(networkStateReceiver, networkFilter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView;

    }

    private void getMaps() {
        final Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("FROM", radioChecked ? "myloc" : locationList.get(favSelected ? favPlace : editFrom.getText().toString())[0]);
        intent.putExtra("TO", locationList.get(editTo.getText().toString())[0]);
        intent.putExtra("FROM_LABEL", radioChecked ? "My Location" : (favSelected ? favPlace : editFrom.getText().toString()));
        intent.putExtra("TO_LABEL", editTo.getText().toString());
        intent.putExtra("PLACE", place);
        intent.putExtra("FAV", favSelected);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void fabOnClick() {
        if (!metaFiles.contains("vertexes_" + place + ".ser"))
            Toast.makeText(context, "Please connect to the Internet first to fetch required Map Files!", Toast.LENGTH_SHORT).show();
        else if (!radioChecked && !locationList.containsKey(editFrom.getText().toString()) && !favSelected)
            Toast.makeText(context, "Enter the starting point", Toast.LENGTH_SHORT).show();
        else if (!locationList.containsKey(editTo.getText().toString()))
            Toast.makeText(context, "Enter the destination point", Toast.LENGTH_SHORT).show();
        else if (editFrom.getText().toString().equals(editTo.getText().toString()) || (favSelected && favPlace.equals(editTo.getText().toString())))
            Toast.makeText(context, "Origin and Destination cannot be same", Toast.LENGTH_SHORT).show();
        else {
            if (!fileStillDownloading.get())
                getMaps();
            else {
                Snackbar snackbarWaitDownload = Snackbar.make(coordinatorLayout, "Please wait until the Map MetaData has been downloaded.", Snackbar.LENGTH_LONG);
                snackbarWaitDownload.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (fileStillDownloading.get()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        msg = new Message();
                        msg.arg1 = 0;
                        handleDownloadFinished.sendMessage(msg);
                    }
                }).start();
            }
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
