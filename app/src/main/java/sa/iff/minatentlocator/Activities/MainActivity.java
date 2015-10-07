package sa.iff.minatentlocator.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import sa.iff.minatentlocator.AboutInfo;
import sa.iff.minatentlocator.Locations;
import sa.iff.minatentlocator.R;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView editFrom;
    private AutoCompleteTextView editTo;
    private Button btnGo, btnMyLoc;
    private Locations locations;
    private Hashtable<String, String> locationList;
    private Toolbar toolbar;
    private AboutInfo aboutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aboutInfo = new AboutInfo(this);
        try {
            locations = new Locations(this);
            locationList = locations.parseLocations();
            ArrayList<String> poleNumbers = new ArrayList<>(locationList.keySet());
            editFrom = (AutoCompleteTextView) findViewById(R.id.autoFrom);
            editTo = (AutoCompleteTextView) findViewById(R.id.autoTo);
            ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, poleNumbers);
            editFrom.setAdapter(locationAdapter);
            editTo.setAdapter(locationAdapter);
            btnGo = (Button) findViewById(R.id.btnGo);
            btnGo.setOnClickListener(new View.OnClickListener() {
                /** * {@inheritDoc} */
                @Override
                public void onClick(final View v) {
                    if (!locationList.containsKey(editFrom.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Enter the starting point", Toast.LENGTH_SHORT).show();
                    } else if (!locationList.containsKey(editTo.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Enter the destination point", Toast.LENGTH_SHORT).show();
                    } else {
                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra("FROM", locationList.get(editFrom.getText().toString()));
                        intent.putExtra("TO", locationList.get(editTo.getText().toString()));
                        MainActivity.this.startActivity(intent);
                    }
                }
            });
            btnMyLoc = (Button) findViewById(R.id.btnMyLoc);
            btnMyLoc.setOnClickListener(new View.OnClickListener() {
                /** * {@inheritDoc} */
                @Override
                public void onClick(final View v) {
                    if (!locationList.containsKey(editTo.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Enter the destination point", Toast.LENGTH_SHORT).show();
                    } else {
                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra("FROM", "myloc");
                        intent.putExtra("TO", locationList.get(editTo.getText().toString()));
                        MainActivity.this.startActivity(intent);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
}
