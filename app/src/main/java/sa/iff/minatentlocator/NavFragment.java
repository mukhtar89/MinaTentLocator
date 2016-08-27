package sa.iff.minatentlocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import sa.iff.minatentlocator.Activities.MapsActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavFragment extends Fragment {

    private static final String PLACE = "place";
    private AutoCompleteTextView editFrom, editTo;
    public TableRow category_row, place_row, favourites_row;
    private static Locations locations;
    private Hashtable<String, String[]> locationList;
    private String place, favPlace;
    private Context context;

    public RadioButton myLocButton;
    public boolean radioChecked, favSelected;
    private Spinner listCategoryTo, listCategoryFrom, listFavourites;
    private ArrayList<String> poleNumbersFrom, poleNumbersTo;
    private ArrayAdapter<String> locationAdapter;

    private OnFragmentInteractionListener mListener;
    private LocationPermission locationPermission;
    private SharedPreferences sharedPreferences;
    private SharedPrefArrayUtils sharedPrefArrayUtils;

    public NavFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param place Place.
     * @return A new instance of fragment NavFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavFragment newInstance(String place) {
        NavFragment fragment = new NavFragment();
        Bundle args = new Bundle();
        args.putString(PLACE, place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = getArguments().getString(PLACE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_nav, container, false);
        context = getActivity().getApplicationContext();
        locationPermission = new LocationPermission(context, getActivity());
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
            if (place.equals("Mina")) {
                listCategoryFrom.setSelection(6);
                listCategoryTo.setSelection(6);
            }
            editFrom = (AutoCompleteTextView) rootView.findViewById(R.id.auto_from);
            editTo = (AutoCompleteTextView) rootView.findViewById(R.id.auto_to);
            editFrom.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    editFrom.showDropDown();
                    return false;
                }
            });
            editTo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    editTo.showDropDown();
                    return false;
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
                    locationAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, poleNumbersTo);
                    editTo.setAdapter(locationAdapter);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editTo.showDropDown();
                }
            });

            favourites_row = (TableRow) rootView.findViewById(R.id.favourites_row);
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

            category_row = (TableRow) rootView.findViewById(R.id.category_row);
            place_row = (TableRow) rootView.findViewById(R.id.place_row);
            myLocButton = (RadioButton) rootView.findViewById(R.id.my_loc_button);
            myLocButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean stateChecked = radioChecked;
                    radioChecked = ((RadioButton) v).isChecked();
                    if (!stateChecked && category_row.getVisibility() == View.VISIBLE) {
                        favourites_row.setVisibility(View.GONE);
                        category_row.setVisibility(View.GONE);
                        place_row.setVisibility(View.GONE);
                    }
                    else if (stateChecked && category_row.getVisibility() == View.GONE) {
                        myLocButton.setChecked(false);
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
        context.startActivity(intent);
    }

    public void fabOnClick() {
        if (!radioChecked && !locationList.containsKey(editFrom.getText().toString()) && !favSelected)
            Toast.makeText(context, "Enter the starting point", Toast.LENGTH_SHORT).show();
        else if (!locationList.containsKey(editTo.getText().toString()))
            Toast.makeText(context, "Enter the destination point", Toast.LENGTH_SHORT).show();
        else if (editFrom.getText().toString().equals(editTo.getText().toString()))
            Toast.makeText(context, "Origin and Destination cannot be same", Toast.LENGTH_SHORT).show();
        else if (radioChecked && !locationPermission.checkLocationPermission()) {
            final Handler handlerLocation = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        if (locationPermission.checkLocationPermission())
                            getMaps();
                    }
                    else Toast.makeText(context, "Cannot detect your location because Access to your Location is Denied"
                            , Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            locationPermission.getLocationPermission(handlerLocation);
                        /*if (locationPermission.checkLocationPermission())
                            getMaps();
                        else Toast.makeText(context, "Cannot detect your location because Access to your Location is Denied"
                                , Toast.LENGTH_SHORT).show();*/
        }
        else getMaps();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
