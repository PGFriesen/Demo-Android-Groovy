package com.dynatrace.sampleAndroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dynatrace.android.agent.DTXAction;
import com.dynatrace.android.agent.Dynatrace;
import com.dynatrace.android.agent.conf.DataCollectionLevel;
import com.dynatrace.android.agent.conf.DynatraceConfigurationBuilder;
import com.dynatrace.android.agent.conf.UserPrivacyOptions;

public class MainActivity extends AppCompatActivity {

    TooltipHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.helper = new TooltipHelper();

        ActionBar bar = getSupportActionBar();
        bar.setTitle("MainActivity");

        /** MANUALLY START THE ONEAGENT WHEN AUTOSTART IS DISABLED OR WHEN USING STANDALONE MANUAL INSTRUMENTATION
         * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#start-oneagent */
//        Dynatrace.startup(this, new DynatraceConfigurationBuilder("<ApplicationID Placeholder>","<BeaconURL Placeholder>")
//                .withUserOptIn(false)
//                .withStartupLoadBalancing(true)
//                .withCrashReporting(true)
//                .withDebugLogging(true)
//                .buildConfiguration()
//        );

        /** ADJUST DataCollectionLevel TO ENABLE DATA COLLECTION WHEN "userOptIn = true"
         * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#data-privacy */
//        Dynatrace.applyUserPrivacyOptions(UserPrivacyOptions.builder()
//                .withCrashReportingOptedIn(true)
//                .withDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
//                .build()
//        );

        // Set click listener to start Instrumentation Activity
        RelativeLayout instrumentation = (RelativeLayout) findViewById(R.id.relativeLayoutInstrumentation);
        instrumentation.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                Intent intent = new Intent(view.getContext(), InstrumentationActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener to start Concepts Activity
        RelativeLayout concepts = (RelativeLayout) findViewById(R.id.relativeLayoutConcepts);
        concepts.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                Intent intent = new Intent(view.getContext(), ConceptsActivity.class);
                startActivity(intent);
            }
        });

        // "About" button
        Button about = (Button) findViewById(R.id.buttonAbout);
        about.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Pair tooltip = helper.getTooltip("about");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle((String)tooltip.first)
                        .setMessage((String)tooltip.second)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    /** TAGGING USERS
     * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#tag-specific-users
     */
    public void onTagSession(View view) {
        // Helper Toaster to prepare breakfast, err... display a toast to the user
        Toaster t = new Toaster();

        String userTag = ((EditText) findViewById(R.id.textUserTag)).getText().toString();
        if (userTag.length() > 0) {
            Dynatrace.identifyUser(userTag); // Tag the user with the text entered into the box
            t.toast(MainActivity.this,"Session successfully tagged as: " + userTag, Toast.LENGTH_LONG);
        } else {
            t.toast( MainActivity.this,"Please enter in a tag", Toast.LENGTH_SHORT);
        }
    }
}