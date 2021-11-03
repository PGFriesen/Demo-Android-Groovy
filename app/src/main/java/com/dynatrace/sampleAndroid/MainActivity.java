package com.dynatrace.sampleAndroid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dynatrace.android.agent.Dynatrace;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("MainActivity");
        setClickListeners();


        /** MANUALLY START THE ONEAGENT
         * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#start-oneagent
         *
         * When AutoStart is disabled inside the build.gradle file, the agent must be manually started with
         * the call to Dynatrace.startup() which uses the DynatraceConfigurationBuilder to configure
         * the OneAgent with the ApplicationId and BeaconUrl from the UI in addition to any other
         * OneAgent configs
         *
         * When AutoStart is enabled (default), the OneAgent is started automatically by the plugin
         * during the "onCreate" method for the MainActivity and uses the OneAgent configurations
         * from the build.gradle file
         *
         * NOTE: Whether or not the Agent is Manually Started has no affect on instrumentation done
         * by the plugin during build time, however, the Agent must be started in order for data
         * to be reported back to Dynatrace
         */
        // TODO (Manually Starting the OneAgent)
//        Dynatrace.startup(this, new DynatraceConfigurationBuilder("<ApplicationID Placeholder>","<BeaconURL Placeholder>")
//                .withUserOptIn(false)
//                .withStartupLoadBalancing(true)
//                .withCrashReporting(true)
//                .withDebugLogging(true)
//                .buildConfiguration()
//        );



        /** ADJUST PRIVACY LEVELS FOR SENDING DATA
         * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#data-privacy
         *
         * When "userOptIn true" is set inside the build.gradle file, DataCollectionLevel is set to
         * "OFF" and only a single user action is sent until the API is called to enable data sending
         *
         * The "UserPrivacyOptions.builder()" chains two calls to set the boolean value for crash reporting
         * and the DataCollectionLevel which can be one of three values - OFF, PERFORMANCE, or USER_BEHAVIOR
         */
        // TODO (Adjusting User Privacy Options)
//        Dynatrace.applyUserPrivacyOptions(UserPrivacyOptions.builder()
//                .withCrashReportingOptedIn(true)
//                .withDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
//                .build()
//        );

    }

    /**
     * Helper method to set the click listeners for the other two activities
     * (InstrumentationActivity and ConceptsActivity), and the popup for the "about" button
     */
    private void setClickListeners() {
        // Set click listener to start Instrumentation Activity
        RelativeLayout instrumentation = (RelativeLayout) findViewById(R.id.relativeLayoutInstrumentation);
        instrumentation.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                Intent intent = new Intent(view.getContext(), InstrumentationActivity.class);
                startActivity(intent);
            }
        });

        // TODO (TASK) see 'Renaming AUto-Actions and reporting values / events'
        /* The above and below onClick listeners are for a relativeLayout, not a button,
         so they currently result in user actions with the same name and are indistinguishable.
         Rename these actions using the SDK to have more meaningful and distinguishable names */

        // Set click listener to start Concepts Activity
        RelativeLayout concepts = (RelativeLayout) findViewById(R.id.relativeLayoutConcepts);
        concepts.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                Intent intent = new Intent(view.getContext(), ConceptsActivity.class);
                startActivity(intent);
            }
        });

        TooltipHelper helper = new TooltipHelper();

        // "About" button
        Button about = (Button) findViewById(R.id.buttonAbout);
        about.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                helper.showDialog(getSupportFragmentManager(), "about");
            }
        });
    }


    /** TAGGING USERS
     * https://www.dynatrace.com/support/help/shortlink/oneagent-sdk-for-android#tag-specific-users
     *
     * Inside of the layout file "res/layout/activity_main.xml" the button with id="@+id/buttonTagUser"
     * has the attribute 'onClick="onTagSession"' which directly corresponds to this method when
     * the button is pressed and uses the text entered by the user to tag the session
     *
     */
    public void onTagSession(View view) {
        // Helper Toaster to prepare breakfast, err... display a toast to the user
        Toaster t = new Toaster();
        String userTag = ((EditText) findViewById(R.id.textUserTag)).getText().toString();

        if (userTag.length() > 0) {

            // TODO (Tagging Users)
            Dynatrace.identifyUser(userTag); // Tag the session with the input provided by user

            t.toast(MainActivity.this,"Session successfully tagged as: " + userTag, Toast.LENGTH_LONG);
            return;
        }

        t.toast( MainActivity.this,"Please enter in a tag", Toast.LENGTH_SHORT);

    }
}