package com.dynatrace.sampleAndroid;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dynatrace.android.agent.Dynatrace;
import com.dynatrace.android.agent.conf.DataCollectionLevel;
import com.dynatrace.android.agent.conf.UserPrivacyOptions;

public class InstrumentationActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private boolean whichFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrumentation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onReplaceFragment(findViewById(R.id.buttonFragmentAuto));
    }

    /**
     * Add a menu to the actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    /**
     * Options Listener for the menu items
     * Switch statement should provide cases for each item id in res/menu/menu_items.xml
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionEndVisit:
                Dynatrace.endVisit(); // Split your current session: Ends previous session and any new data will show up in a new session
                break;
            case R.id.optionStringProperty:
                // Report a String value for the user action created by this button
                // Create a Session Property in the UI
                break;
            case R.id.optionNumericProperty:
                // Report a Numeric value for the user action created by this button
                // Create a Numeric Property in the UI that sums the values
                break;
            case R.id.optionActionProperty:
                // Report a value for the user action created by this button
                // Create a User Action Property in the UI
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom click listener for buttons clicked in a fragment. Depending on the active fragment,
     * calls the custom "onFragmentButton" method for the active fragment to handle and passes the
     * view object
     *
     * @param view The view object of the button that was pressed
     */
    public void onFragmentButton(View view) {
        if (whichFragment) {
            ((AutoInstrumentationFragment) currentFragment).onFragmentButton(view);
            return;
        }
        ((ManualInstrumentationFragment) currentFragment).onFragmentButton(view);
    }

    /**
     * Custom click listener for the fragment-picking buttons
     *
     * @param view the view object for the button that was clicked
     */
    public void onReplaceFragment(View view){
        View otherView;
        if (view.getId() == R.id.buttonFragmentAuto){
            otherView = findViewById(R.id.buttonFragmentManual);
            this.currentFragment = new AutoInstrumentationFragment();
            whichFragment = true;
        } else {
            otherView = findViewById(R.id.buttonFragmentAuto);
            this.currentFragment = new ManualInstrumentationFragment();
            whichFragment = false;
        }

        // Programatically set button colors
        view.setBackgroundResource(R.drawable.button_teal_pressed);
        ((Button)view).setTextColor(0xFFFFFFFF);
        otherView.setBackgroundResource(R.drawable.button_white_unfocused);
        ((Button)otherView).setTextColor(0xFF000000);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutFragment, currentFragment).commit();
    }
}