package sa.iff.minatentlocator.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sa.iff.minatentlocator.R;

public class MainActivity extends AppCompatActivity {

    private EditText editFrom;
    private EditText editTo;
    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main);
        editFrom = (EditText) findViewById(R.id.editFrom);
        editTo = (EditText) findViewById(R.id.editTo);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            /** * {@inheritDoc} */
            @Override
            public void onClick(final View v) {
                if ("".equals(editFrom.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Enter the starting point", Toast.LENGTH_SHORT).show();
                } else if ("".equals(editTo.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Enter the destination point", Toast.LENGTH_SHORT).show();
                } else {
                    final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("FROM", editFrom.getText().toString().trim());
                    intent.putExtra("TO", editTo.getText().toString().trim());
                    MainActivity.this.startActivity(intent);
                }
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
