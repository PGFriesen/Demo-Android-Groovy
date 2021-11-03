package com.dynatrace.sampleAndroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.dynatrace.android.agent.DTXAction;
import com.dynatrace.android.agent.Dynatrace;
import com.dynatrace.android.agent.WebRequestTiming;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManualInstrumentationFragment extends Fragment {

    private View view;
    private DTXAction parentAction; // Currently open Parent User Action
    private ArrayList<DTXAction> childrenActions; // Array of any child actions
    private int numberOfChildren; // Counter for total number of children actions added to a parent
    private Random random = new Random();
    private OkHttpClient client;
    private TooltipHelper helper;
    private HashMap<String, View> viewMap;
    private Toaster t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_instrumentation_manual, container, false);

        // Initialize variables for fragment
        initializeFragment();

        return view;
    }

    /** 'Enter Action' button is pressed
    Manually Create a User Action with provided name */
    private boolean onEnterAction(View v){
        String userActionName = ((EditText) view.findViewById(R.id.editTextUserActionName)).getText().toString();
        boolean isSuccessful = false;
        String toastMessage = "Give the user action a name";

        if(userActionName.length() > 0){
            // TODO (Manually created User-Action)
            parentAction = Dynatrace.enterAction(userActionName);

            isSuccessful = true;
            toastMessage = "User Action created with name: " + userActionName;
        }

        t.toast(getActivity(), toastMessage, Toast.LENGTH_SHORT);
        return isSuccessful;
    }


    /** 'Leave Action' button is pressed
    Manually leave the currently open action and clear the reference and array of child actions */
    private void onLeaveAction(){
        // TODO (Manually Leave User-Action)
        parentAction.leaveAction();

        /* Clear the reference to the parentAction and the list of children actions
        because they are automatically closed when parent is closed */
        parentAction = null;
        childrenActions.clear();
        numberOfChildren = 0;

        t.toast(getActivity(), "Parent Action closed", Toast.LENGTH_SHORT);
    }


    /** 'Create Child Action' button is pressed
    Add a child action to the open parent action */
    private void onCreateChildAction(){
        // TODO (Manually created Child Action)
        DTXAction childAction = Dynatrace.enterAction("Child Action #" + String.valueOf(numberOfChildren), parentAction);

        /* Add the child action to the list and increment the counter
        (The total number of children can only increase, while the size of the list can decrease) */
        childrenActions.add(childAction);
        numberOfChildren ++;

        t.toast(getActivity(), "Added Child Action #" + String.valueOf(numberOfChildren) , Toast.LENGTH_SHORT);
    }


    /** 'Close Child Action' button is pressed
    Leave the most recently added child action, then remove it from the list */
    private void onLeaveChild(){
        childrenActions.get(childrenActions.size() - 1).leaveAction();
        childrenActions.remove(childrenActions.size() - 1);

        t.toast(getActivity(), "Closed most recently added child action" , Toast.LENGTH_SHORT);
    }


    /** 'Web Request' button is pressed
    Manually tag a web request to be associated with the currently open action and time it */
    private void onWebRequest() {
        String url = ((EditText) view.findViewById(R.id.editTextURL)).getText().toString();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO (Manually detected web request)
                // Create the unique request tag value for the 'x-dynatrace' header, then create the timing object
                String uniqueRequestTag = parentAction.getRequestTag();
                WebRequestTiming timing = Dynatrace.getWebRequestTiming(uniqueRequestTag);

                try {
                    // Create the Request object with the URL with headers
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader(Dynatrace.getRequestTagHeader(), uniqueRequestTag) // Add the 'x-dynatrace' header with the uniqueRequestTag as the value
                            .build();

                    // Start the web request timer
                    timing.startWebRequestTiming();

                    // Send the request
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String body = response.body().string();
                    }

                    // Stop the timer
                    timing.stopWebRequestTiming(url, response.code(), response.message());

                    // send a toast to the UI to display the response code to the user
                    getActivity().runOnUiThread(() -> {
                        t.toast(getActivity(), "Web request returned " + response.code() + " for " + url, Toast.LENGTH_SHORT);
                    });

                } catch (Exception e) {
                    // Stop the timing in case of request exception, and if we get a MalformedURLException, report it for the action
                    try {
                        timing.stopWebRequestTiming(url, -1, e.toString());
                    } catch(MalformedURLException m){
                        parentAction.reportError("Exception during web request", m);
                    }
                }
            }
        });
        thread.start();
    }


    /** 'Event' button is pressed
    Report an event for the parent action */
    private void onReportEvent(){
        parentAction.reportEvent("Manually reported Event");

        t.toast(getActivity(), "Reported event: 'Manually reported Event'", Toast.LENGTH_SHORT);
    }


    /** 'Value' button is pressed
    Report a value for the parent action */
    private void onReportValue(){
        int k = random.nextInt();
        parentAction.reportValue("Manually reported value (random integer)", random.nextInt());

        t.toast(getActivity(), "Reported Value: " + String.valueOf(k) + " for key 'Manually reported value (random integer)'" , Toast.LENGTH_SHORT);
    }


    /** 'Error' button is pressed
    Report a handled error for the parent action */
    private void onReportError(){
        try {
            System.out.println(1/0);
        } catch (ArithmeticException e) {
            parentAction.reportError("Manually reported error", e);

            t.toast(getActivity(), "Reported Error: " + e.toString() + " with key 'Manually reported error'" , Toast.LENGTH_SHORT);
        }
    }


    /**
     * Since the button clicks for the fragment are reported to the Activity, we have to pass the
     * view for the activity to the fragment to determine the correct button functionality
     *
     * @param v the parent view for the activity
     */
    public void onFragmentButton(View v){
        switch(v.getId()){
            case R.id.buttonEnterAction:
                if(onEnterAction(v)){
                    handleButtonDisplays(v);
                };
                break;
            case R.id.buttonLeaveAction:
                onLeaveAction();
                handleButtonDisplays(v);
                break;
            case R.id.buttonManualWebRequest:
                onWebRequest();
                break;
            case R.id.buttonChildAction:
                onCreateChildAction();
                handleButtonDisplays(v);
                break;
            case R.id.buttonCloseChild:
                onLeaveChild();
                handleButtonDisplays(v);
                break;
            case R.id.buttonReportEvent:
                onReportEvent();
                break;
            case R.id.buttonReportValue:
                onReportValue();
                break;
            case R.id.buttonReportError:
                onReportError();
                break;
            default:
                break;
        }
    }


    /**
     * Helper function to help handle enabling and disabling of buttons (and color indicators)
     * There are only 4 buttons which could enable or disable other buttons
     * parent/child - enter/leave actions
     *
     * @param v The view object for the buttons
     */
    private void handleButtonDisplays(View v){
        switch(v.getId()){
            case R.id.buttonEnterAction:
                for (String key : viewMap.keySet()) {
                    if (key != "enterAction" && key != "closeChild"){
                        viewMap.get(key).setEnabled(true);
                    } else {
                        viewMap.get(key).setEnabled(false);
                    }
                }
                break;
            case R.id.buttonLeaveAction:
                for (String key : viewMap.keySet()){
                    if (key == "enterAction"){
                        viewMap.get(key).setEnabled(true);
                    } else {
                        viewMap.get(key).setEnabled(false);
                    }
                }
                break;
            case R.id.buttonChildAction:
                viewMap.get("closeChild").setEnabled(true);
                break;
            case R.id.buttonCloseChild:
                if (childrenActions.size() == 0){
                    viewMap.get("closeChild").setEnabled(false);
                }
                break;
        }
    }

    /**
     * Helper function to set click listeners for all tooltip buttons
     */
    private void setTooltips(){
        this.helper = new TooltipHelper();

        // User Action Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonActionHelp), "Manual User Action Dialog");
        // Child Action Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonChildActionHelp), "Child Action Dialog");
        // Web Request Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonManualWebRequestHelp), "Manual Web Request Dialog");
        // Reporting Tooltip Button
        setActionName((Button) view.findViewById(R.id.buttonReportingHelp), "Reporting Dialog");
    }

    /**
     * Helper function that sets the click listener for the given Dialog
     *
     * @param button The button for which we're setting the click listener
     * @param dialogTag Tag used to determine the corresponding dialog
     */
    private void setActionName(Button button, String dialogTag){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (dialogTag.length() > 0) {
                    Dynatrace.modifyUserAction(userAction -> {
                        userAction.reportValue("Original Action Name", userAction.getActionName());
                        userAction.setActionName("Touch on " + dialogTag);
                    });
                    helper.showDialog(getParentFragmentManager(), dialogTag);
                }
            }
        });
    }

    /**
     * Create the tooltip helper, httpClient, toaster, and array for children actions along with a viewMap for
     * simplified access of buttons on fragment
     */
    private void initializeFragment(){
        // Create the httpClient and tooltip helper
        this.client = new OkHttpClient();
        this.t = new Toaster();
        setTooltips();

        // Initialize array for children actions
        this.childrenActions = new ArrayList<DTXAction>();
        this.numberOfChildren = 0;

        // Map buttons for easier access
        this.viewMap = new HashMap<String, View>();
        viewMap.put("enterAction", view.findViewById(R.id.buttonEnterAction));
        viewMap.put("leaveAction", view.findViewById(R.id.buttonLeaveAction));
        viewMap.put("createChild", view.findViewById(R.id.buttonChildAction));
        viewMap.put("closeChild", view.findViewById(R.id.buttonCloseChild));
        viewMap.put("webRequest", view.findViewById(R.id.buttonManualWebRequest));
        viewMap.put("reportValue", view.findViewById(R.id.buttonReportValue));
        viewMap.put("reportEvent", view.findViewById(R.id.buttonReportEvent));
        viewMap.put("reportError", view.findViewById(R.id.buttonReportError));
    }

}

