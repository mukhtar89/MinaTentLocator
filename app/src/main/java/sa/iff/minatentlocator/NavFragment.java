package sa.iff.minatentlocator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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

import sa.iff.minatentlocator.Activities.MainActivity;
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
    public static TableRow category_row, place_row;
    private Button btnGo;
    private static Locations locations;
    private Hashtable<String, String[]> locationList;
    private String place;
    private Context context;
    private Integer initialToastTo, initialToastFrom;

    public static RadioButton myLocButton;
    public static boolean radioChecked;
    private Spinner listCategoryTo, listCategoryFrom;
    private ArrayList<String> poleNumbersFrom, poleNumbersTo;
    private ArrayAdapter<String> locationAdapter;

    private OnFragmentInteractionListener mListener;
    private LocationPermission locationPermission;

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
        initialToastTo = 0;
        initialToastFrom = 0;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_nav, container, false);
        context = getActivity().getApplicationContext();
        locationPermission = new LocationPermission(context, getActivity());
        radioChecked = false;

        try {
            locations = new Locations(context);
            locationList = locations.parseLocations(place);
            poleNumbersTo = poleNumbersFrom = new ArrayList<>(locationList.keySet());

            listCategoryTo = (Spinner) rootView.findViewById(R.id.list_category_to);
            listCategoryFrom = (Spinner) rootView.findViewById(R.id.list_category_from);
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
                    if (initialToastFrom != 0)
                        Toast.makeText(context, "Origin Category: " + categoryList.get(position), Toast.LENGTH_SHORT).show();
                    poleNumbersFrom = new ArrayList<>();
                    Iterator<Map.Entry<String, String[]>> iter = locationList.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String[]> entrySet = iter.next();
                        if (entrySet.getValue()[1].equals(categoryList.get(position)))
                            poleNumbersFrom.add(entrySet.getKey());
                    }
                    locationAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, poleNumbersFrom);
                    editFrom.setAdapter(locationAdapter);
                    initialToastFrom++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editFrom.showDropDown();
                }
            });
            listCategoryTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (initialToastTo != 0)
                        Toast.makeText(context, "Destination Category: " + categoryList.get(position), Toast.LENGTH_SHORT).show();
                    poleNumbersTo = new ArrayList<>();
                    Iterator<Map.Entry<String, String[]>> iter = locationList.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String[]> entrySet = iter.next();
                        if (entrySet.getValue()[1].equals(categoryList.get(position)))
                            poleNumbersTo.add(entrySet.getKey());
                    }
                    locationAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, poleNumbersTo);
                    editTo.setAdapter(locationAdapter);
                    initialToastTo++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editTo.showDropDown();
                }
            });

            category_row = (TableRow) rootView.findViewById(R.id.category_row);
            place_row = (TableRow) rootView.findViewById(R.id.place_row);
            myLocButton = (RadioButton) rootView.findViewById(R.id.my_loc_button);

            btnGo = (Button) rootView.findViewById(R.id.btnGo);
            btnGo.setOnClickListener(new View.OnClickListener() {
                /** * {@inheritDoc} */
                @Override
                public void onClick(final View v) {
                    if (!radioChecked && !locationList.containsKey(editFrom.getText().toString()))
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
                    else {
                        getMaps();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void getMaps() {
        final Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("FROM", radioChecked? "myloc" : locationList.get(editFrom.getText().toString())[0]);
        intent.putExtra("TO", locationList.get(editTo.getText().toString())[0]);
        intent.putExtra("FROM_LABEL", radioChecked? "My Location" : editFrom.getText().toString());
        intent.putExtra("TO_LABEL", editTo.getText().toString());
        intent.putExtra("PLACE", place);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*public void fabOnClick() {
        if (!radioChecked && !locationList.containsKey(editFrom.getText().toString())) {
            Toast.makeText(context, "Enter the starting point", Toast.LENGTH_SHORT).show();
        } else if (!locationList.containsKey(editTo.getText().toString())) {
            Toast.makeText(context, "Enter the destination point", Toast.LENGTH_SHORT).show();
        } else {
            final Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("FROM", radioChecked? "myloc" : locationList.get(editFrom.getText().toString())[0]);
            intent.putExtra("TO", locationList.get(editTo.getText().toString())[0]);
            intent.putExtra("PLACE", place);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }*/

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
