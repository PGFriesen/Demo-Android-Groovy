package com.dynatrace.sampleAndroid;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dynatrace.android.agent.Dynatrace;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoInstrumentationFragment extends Fragment {

    private View view;
    private TextView textRequestDelay;
    private TextView textNumberOfRequests;

    private OkHttpClient client;
    private TooltipHelper helper;
    private Toaster t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        this.view = inflater.inflate(R.layout.fragment_instrumentation_auto, container, false);

        // Set the view for the request delay and number of requests
        this.textRequestDelay = (TextView) view.findViewById(R.id.textViewDelayIndicator);
        this.textNumberOfRequests = (TextView) view.findViewById(R.id.textViewNumberOfRequests);

        // Create the httpClient and tooltip helper
        this.client = new OkHttpClient();
        this.helper = new TooltipHelper();
        this.t = new Toaster();
        setSeekerBar();
        setTooltips();

        return view;
    }

    /** 'User Action' button is pressed
    If entered text is not empty, it will replace the button text */
    private void onUserAction(View v){
        EditText textNewButtonText = (EditText) view.findViewById(R.id.editTextNewButtonText);
        String newText = textNewButtonText.getText().toString();
        if (newText.length() > 0) {
            ((Button)v).setText(newText);
        }
        else {
            t.toast(getActivity(), "Enter some text to replace " + ((Button)v).getText().toString(), Toast.LENGTH_SHORT);
        }
    }


    /** 'Extend and Modify User Actions' button is pressed
    Rename the generated User Action and report several values and an event for it */
    private void onModifiableUserAction(){
        Dynatrace.modifyUserAction(userAction -> {
            userAction.reportValue("Original Action Name",userAction.getActionName());

            userAction.setActionName("Dynatrace.modifyUserAction() example");

            userAction.reportValue("What is the answer to life?", 42);
            userAction.reportValue("Reported value types","int, long, double, string");

            userAction.reportEvent("Reported Values represent Key:Value pairs, whereas Reported Events are a single string event");
            userAction.reportEvent("When creating Session or User Action properties with reported values, make sure to set the 'Name'");
        });
        t.toast(getActivity(), "User Action name changed from 'Touch on Extend and Modify User Actions' to 'Dynatrace.modifyUserAction() example'", Toast.LENGTH_LONG);
    }


    /** 'Caught Exception (Malformed URL)' button is pressed
    Cause an exception to be thrown (and handled) */
    private void onCaughtException(){
        try {
            URL url = new URL("httpSUPERSECRET::::::://////");
        } catch (MalformedURLException m) {
            Dynatrace.modifyUserAction(userAction -> {
                userAction.reportError("Malformed URL Error", m);
            });
            t.toast(getActivity(), "Caught Error: " + m.toString(), Toast.LENGTH_SHORT);
        }
    }


    /** 'Crash Application (Divide by 0)' button is pressed
    Attempts to log the result of dividing by zero and causes app to crash. */
    private void onCrashApplication(){
        System.out.println(2/0);
    }


    /** 'Web request' button is pressed
    The User Action is created when the click occurs and the request sends after the configured delay */
    private void onWebRequest(String url, int requestDelay, int numberOfRequests) {
        for (int i = 1; i <= numberOfRequests; i++){
            final int n = i;
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        // Sleeep this thread for (rD x n) ms where...
                        // rD = request delay
                        // n = nth number request between 1 and 20
                        Thread.sleep(requestDelay * n);

                        // Create the Request object with the URL
                        Request request = new Request.Builder()
                                .url(url)
                                .build();

                        // Send the request
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            Dynatrace.modifyUserAction(userAction -> {
                                userAction.reportEvent("Request Number " + String.valueOf(n) + " of " + numberOfRequests + " returned successfully");
                            });
                        }

                    } catch (Exception e) {
                        Dynatrace.modifyUserAction(userAction -> {
                            userAction.reportError("Automatic Web Request Error", e);
                        });
                    }

                    if (n == numberOfRequests){
                        getActivity().runOnUiThread(() -> {
                            t.toast(getActivity(), "Finished sending " + String.valueOf(numberOfRequests) + " request(s)", Toast.LENGTH_SHORT);
                        });
                    }
                }
            });
            thread.start();
        }

    }


    /**
     * This method handles the button clicks and receives the button view object from the parent activity
     *
     * @param v the view object for the button that was pressed
     */
    public void onFragmentButton(View v){
        switch(v.getId()){
            case R.id.buttonUserAction:
                onUserAction(v);
                break;
            case R.id.buttonModifyUserActionName:
                onModifiableUserAction();
                break;
            case R.id.buttonWebRequest:
                onWebRequest(((EditText) view.findViewById(R.id.editTextURL)).getText().toString(),
                        Integer.parseInt(textRequestDelay.getText().toString().substring(0, textRequestDelay.getText().toString().indexOf(' '))),
                        Integer.parseInt(textNumberOfRequests.getText().toString().substring(0, textNumberOfRequests.getText().toString().indexOf(' ')))
                );
                break;
            case R.id.buttonCaughtException:
                onCaughtException();
                break;
            case R.id.buttonCrash:
                onCrashApplication();
                break;
            default:
                break;
        }
    }


    /**
     * Helper function to set click listeners for all tooltip buttons
     */
    private void setTooltips(){
        // User Action Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonUserActionHelp), "Touch on Basic User Action tooltip", "User Action Dialog");

        // Modified User Action Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonModifyUserActionNameHelp), "Touch on Modify User Action tooltip", "Modify User Action Dialog");

        // Web Request Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonWebRequestHelp), "Touch on Web request tooltip", "Web Request Dialog");

        // Caught Error Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonCaughtExceptionHelp), "Touch on Caught Exception tooltip", "Exception Handling Dialog");

        // Crash Reporting Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonCrashHelp), "Touch on Crash Reporting tooltip", "Crash Reporting Dialog");
    }

    /**
     * Helper function that sets the click listener for the given button as well as rename the
     * action with Dynatrace.modifyUserAction(), mostly used for tooltip buttons with "?" text
     *
     * @param button The button for which we're setting the click listener
     * @param newActionName The new name for the automatically detected user action
     * @param dialogTag Tag used to determine the corresponding dialog
     */
    private void setActionName(Button button, String newActionName, String dialogTag){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Dynatrace.modifyUserAction(userAction -> {
                    userAction.reportValue("Original Button Text",userAction.getActionName());
                    userAction.setActionName(newActionName);
                });

                if (dialogTag.length() > 0) {
                    Pair tooltip = helper.getTooltip(dialogTag);
                    TooltipDialog dialog = new TooltipDialog((String) tooltip.first, (String) tooltip.second);
                    dialog.show(getParentFragmentManager(), dialogTag);
                }
            }
        });
    }

    /**
     * Helper function to set the Listener on the Seeker bars
     */
    private void setSeekerBar(){
        // Configure listener for request delay between 0 - 5,000 ms increments by 250
        SeekBar requestDelaySeeker = (SeekBar) view.findViewById(R.id.seekbarRequestDelay);
        requestDelaySeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int stepSize = 250;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress/stepSize)*stepSize;
                seekBar.setProgress(progress);
                textRequestDelay.setText(String.valueOf(progress) + " ms");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method sub, nothing to implement, but required to have
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Auto-generated method sub, nothing to implement, but required to have
            }
        });

        // Configure listener for number of requests between 1 - 20 increments by 1
        SeekBar numberOfRequests = (SeekBar) view.findViewById(R.id.seekbarNumberOfRequests);
        numberOfRequests.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int stepSize = 1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress/stepSize)*stepSize;
                seekBar.setProgress(progress);
                textNumberOfRequests.setText(String.valueOf(progress) + " call(s)");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Auto-generated method sub, nothing to implement, but required to have
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Auto-generated method sub, nothing to implement, but required to have
            }
        });
    }

}