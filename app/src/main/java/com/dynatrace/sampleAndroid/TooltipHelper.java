package com.dynatrace.sampleAndroid;

import android.util.Pair;

import androidx.fragment.app.FragmentManager;

/**
 * Helper class used throughout the application to create and display a dialog popup window
 */
public class TooltipHelper {

    /**
     * Create the dialog object and get the corresponding Title and Message for it based on the
     * provided tag
     */
    public void showDialog(FragmentManager fragmentManager, String tag){
        Pair<String, String> tooltip = getTooltip(tag);
        TooltipDialog dialog = new TooltipDialog(tooltip.first, tooltip.second);
        dialog.show(fragmentManager, tag);
    }

    /**
     * Helper function to determine if an Item in the ListView should have a clickable button
     *
     * @param s String value passed from ListView Adapter for a list item
     * @return true if the Item should have a button
     */
    public Boolean hasClickable(String s){
        switch(s){
            case "Adjust DataCollectionLevel with SDK":
            case "User Actions":
            case "Web Requests":
            case "Mobile SDK":
            case "Dynatrace Android Gradle Plugin":
            case "200 User Action limit reached":
            case "Application is terminated":
            case "Cluster Activegate":
            case "Environment Activegate":
            case "Instrumented OneAgent Webserver":
            case "Back-end Webserver process must be instrumented with a OneAgent":
            case"Try disabling instrumentation of sensors depending on issue":
                return true;
            default:
                return false;
        }
    }

    /**
     * Getter for the immutable pair of strings with the dialog window title and message
     */
    public Pair<String, String> getTooltip(String tag){
        Pair<String, String> tooltip;
        switch(tag){
            case "Web Request Dialog":
                tooltip = new Pair<String, String>(T_WEB_REQUEST, M_WEB_REQUEST);
                break;
            case "User Action Dialog":
                tooltip = new Pair<String, String>(T_USER_ACTION, M_USER_ACTION);
                break;
            case "Modify User Action Dialog":
                tooltip = new Pair<String, String>(T_USER_ACTION2, M_USER_ACTION2);
                break;
            case "Exception Handling Dialog":
                tooltip = new Pair<String, String>(T_EXCEPTION_HANDLING,M_EXCEPTION_HANDLING);
                break;
            case "Crash Reporting Dialog":
                tooltip = new Pair<String, String>(T_CRASH_REPORTING, M_CRASH_REPORTING);
                break;
            case "Reporting Dialog":
                tooltip = new Pair<String, String>(T_REPORTING, M_REPORTING);
                break;
            case "Manual Web Request Dialog":
                tooltip = new Pair<String, String>(T_MANUAL_WEB_REQUEST, M_MANUAL_WEB_REQUEST);
                break;
            case "Child Action Dialog":
                tooltip = new Pair<String, String>(T_CHILD_ACTION, M_CHILD_ACTION);
                break;
            case "Manual User Action Dialog":
                tooltip = new Pair<String, String>(T_MANUAL_ACTION, M_MANUAL_ACTION);
                break;
            case "Adjust DataCollectionLevel with SDK":
                tooltip = new Pair<String, String>(T_DATA_COLLECTION_LEVEL, M_DATA_COLLECTION_LEVEL);
                break;
            case "User Actions":
                tooltip = new Pair<String, String>(T_AUTO_USER_ACTION_CONCEPT, M_AUTO_USER_ACTION_CONCEPT);
                break;
            case "Web Requests":
                tooltip = new Pair<String, String>(T_AUTO_WEB_REQUEST_CONCEPT, M_AUTO_WEB_REQUEST_CONCEPT);
                break;
            case "Manual User Actions":
                tooltip = new Pair<String, String>(T_MANUAL_ACTION, M_MANUAL_USER_ACTION_CONCEPT);
                break;
            case "Manual Web Requests":
                tooltip = new Pair<String, String>(T_MANUAL_WEB_REQUEST, M_MANUAL_WEB_REQUEST_CONCEPT);
                break;
            case "Mobile SDK":
                tooltip = new Pair<String, String>(T_SDK_CONCEPT, M_SDK_CONCEPT);
                break;
            case "Dynatrace Android Gradle Plugin":
                tooltip = new Pair<String, String>(T_PLUGIN_CONCEPT, M_PLUGIN_CONCEPT);
                break;
            case "Application is terminated":
                tooltip = new Pair<String, String>(T_SPLITTING_CONCEPT, M_SPLITTING_CONCEPT);
                break;
            case "Cluster Activegate":
                tooltip = new Pair<String, String>(T_CAG_CONCEPT, M_CAG_CONCEPT);
                break;
            case "Environment Activegate":
                tooltip = new Pair<String, String>(T_EAG_CONCEPT, M_EAG_CONCEPT);
                break;
            case "Instrumented OneAgent Webserver":
                tooltip = new Pair<String, String>(T_OA_CONCEPT, M_OA_CONCEPT);
                break;
            case "about":
                tooltip = new Pair<String, String>(T_ABOUT,M_ABOUT);
                break;
            case "Back-end Webserver process must be instrumented with a OneAgent":
                tooltip = new Pair<String, String>(T_PUREPATH, M_PUREPATH);
                break;
            case "Try disabling instrumentation of sensors depending on issue":
                tooltip = new Pair<String, String>(T_DISABLE_SENSORS, M_DISABLE_SENSORS);
                break;
            default:
                tooltip = new Pair<String, String>("","");
        }

        return tooltip;
    }

    static final String T_USER_ACTION = "User Action monitoring";
    static final String M_USER_ACTION =  "User Actions are created when a button is clicked\n\n" +
            "The action is automatically closed once the \"timeout\" is reached if there are no open events for the user action\n\n" +
            "If the action is still open when the \"maxDuration\" is reached, the user action will close regardless and any remaining open events will be discarded.\n\n\n" +
            "The timeout and maxDuration can be configured in the build.gradle\n\n" +
            "Default values are:\ntimeout=500ms (0.5 seconds)\nmaxDuration=60,000ms (1 minute)\n\n" +
            "Possible values that can be configured:\ntimeout: 100ms - 5,000ms\nmaxDuration: 100ms - 540,000ms (9 minutes)";

    static final String T_USER_ACTION2 = "Modify Automatically Detected User Actions";
    static final String M_USER_ACTION2 = "Automatic User Actions use the text on the button to create the action name, however, you can also extend and modify the user action by calling \"Dynatrace.modifyUserAction\" inside the click listener for the button\n\n" +
            "With this method, you can change the automatically detected name for the user action, report values, events and errors for the user action.\n\n" +
            "For this button, the action name is changed to be \"Dynatrace.modifyUserAction() example\" instead of \"Touch on Modify and Extend User Actions\"\n\n" +
            "Look for the \"onModifiableUserAction\" method inside of the AutoInstrumentationFragment.java class" ;

    static final String T_WEB_REQUEST = "Web Request";
    static final String M_WEB_REQUEST = "Clicking the web request button creates the user action, and the slider configures the added delay before the request is fired off to the specified URL.\n\n" +
            "In order for the Web Request to show up in the Waterfall Analysis, it must be SENT while there is an open User Action\n\n" +
            "If there is no open user action when a request is made, it will still show up in the Web Request section for the Mobile Application overview in Dynatrace\n\n" +
            "Try changing the default 'userAction.timeout' value in the build.gradle configs and see how that behaves with different delays for web requests with the slider";

    static final String T_EXCEPTION_HANDLING = "Exception Handling";
    static final String M_EXCEPTION_HANDLING = "Caught exceptions are not automatically recorded and need to be reported manually, however, they can be manually reported for User Actions in the same manner that we report values or events\n\n" +
            "With the Dynatrace.modifyUserAction method, we can use a try-catch block and in the case of an exception, report the error for the user action to indicate the exception\n\n" +
            "To see an example, check the \"onCaughtException\" method in the AutoInstrumentationFragment.java class where we handle a malformed URL exception";

    static final String T_CRASH_REPORTING = "Crash Reporting";
    static final String M_CRASH_REPORTING = "The Mobile OneAgent captures all unhandled exceptions and errors and sends the crash report immediatly back to Dynatrace\n\n" +
            "The crash report includes the time of occurrence as well as the full stack trace of the exception\n\n" +
            "In the \"onCrashApplication\" method within the AutoInstrumentationFragment.java class, we simply attempt to print the result of dividing by zero with no try-catch blocks";

    static final String T_MANUAL_ACTION = "Manual User Actions";
    static final String M_MANUAL_ACTION = "Custom actions are created with the SDK by calling \"DTXAction act = Dynatrace.enterAction('ActionName')\", and closed with \"act.leaveAction()\"\n\n" +
            "If you do not call leaveAction, then the action won't be reported by the Mobile Agent.\n\n" +
            "Inside the class ManualInstrumentationFragment.java...\n\n" +
            "Dynatrace.enterAction() gets called in the 'onEnterAction' method when the button is pressed\n\n" +
            "leaveAction() gets called in the 'onLeaveAction' method when the leave button is pressed and all associated events will be included in the waterfall";

    static final String T_CHILD_ACTION = "Child Actions";
    static final String M_CHILD_ACTION = "You can add children actions to open user actions by calling \"Dynatrace.enterAction()\" with two parameters\n\n" +
            "The first parameter will still be the String name of the child action\n\n" +
            "The second parameter expects a DTXAction object which will be the parent action and will show up in the Waterfall Analysis for said parent action\n\n\n" +
            "When a parent action is closed, all open children associated with the action are automatically closed as well\n\n" +
            "Check out the methods 'onCreateChildAction' and 'onLeaveChild' inside the ManualInstrumentationFragment.java class";

    static final String T_MANUAL_WEB_REQUEST = "Manual Web Requests";
    static final String M_MANUAL_WEB_REQUEST = "IMPORTANT NOTE: IN ORDER TO PREVENT AUTOMATIC TAGGING OF WEB REQUESTS SET 'webRequests.enabled false' IN THE BUILD.GRADLE FILE AND REBUILD THE APP!\n\n" +
            "There are 3 things that are being done to manually monitor the request inside the 'onWebRequest' method\n\n" +
            "1. A uniqueRequestTag is created for the web request that explicitly ties it to a user action\n\n" +
            "2. Create the timing object with the unique tag to time the request for Waterfall Analysis\n\n" +
            "3. Add the unique tag as a header to the request for purepath correlation to OneAgent webservers";


    static final String T_REPORTING = "Reporting";
    static final String M_REPORTING = "The main difference with reporting for manual user actions is that instead of calling Dynatrace.modifyUserAction" +
            " and reporting the events or values, you can simply call the respective methods directly on the action since it is manually created\n\n" +
            "Check out the onReportEvent/Value/Error methods in the manual instrumentation fragment class for examples";

    static final String T_DATA_COLLECTION_LEVEL = "DataCollectionLevel";
    static final String M_DATA_COLLECTION_LEVEL = "The SDK method \"Dynatrace.applyUserPrivacyOptions\" can be called at any time when \"userOptIn\" is set to \"true\" while the app is running to adjust the dataCollectionLevel\n\n" +
            "It is important to note that this method does NOT prompt the user, and it is up to the development team to notify the user or not of data collection and request permissions\n\n" +
            "The SDK method simply uses the \"UserPrivacyOptions.builder()\" to set the new data collection level for the current session\n\n" +
            "NOTE: Any time the DataCollectionLevel is changed, the session will 'split' and a new session will be created for the new level of data collection";

    static final String T_AUTO_USER_ACTION_CONCEPT = "Automatic User Actions";
    static final String M_AUTO_USER_ACTION_CONCEPT = "During the app build, the Dynatrace Android Gradle Plugin instruments the following sensors\n\n" +
            "Base Android Sensors\n" +
            "- .app.Activity.onOptionsItemSelected\n" +
            "- .view.MenuItem$OnMenuItemClickListener\n" +
            "- .view.View$OnClickListener (Most Common Button Listener)\n" +
            "- .widget.AdapterView$OnItemClickListener\n" +
            "- .widget.AdapterView$OnItemSelectedListener\n\n" +
            "AndroidX Sensors\n" +
            "- .viewpager.widget.ViewPager$OnPageChangeListener\n" +
            "- .swiperefreshlayout.widget.SwipeRefreshLayout$OnRefreshListener\n\n" +
            "The plugin can also instrument custom 'onClick' listeners if the activity extends from 'AppCompatActivity'\n\n" +
            "An example of this can be found in MainActivity with the 'onTagSession' method, and because the activity extends from 'AppCompatActivity', the button gets auto-instrumented";

    static final String T_AUTO_WEB_REQUEST_CONCEPT = "Automatic Web Requests";
    static final String M_AUTO_WEB_REQUEST_CONCEPT = "Web Requests made with the following HTTP Frameworks will be automatically Instrumented\n\n" +
            "- HttpURLConnection\n- OkHttp (versions 3 and 4 only)\n- Apache HttpClient (Android-internal version only)\n\n" +
            "If the request is sent while a user action is open, then it will be correlated with the user action and show up in the waterfall analysis.\n\n" +
            "If there is no user action open when the request is made, it will only show up in the list of web requests for the application overview\n\n" +
            "Requests are automatically tagged with the 'x-dynatrace' HTTP header but will only be correlated to purepaths if associated with a user action and all other conditions are met";

    static final String M_MANUAL_USER_ACTION_CONCEPT = "Custom Actions can be created with the SDK and can be a great way to add custom data for both Automatic and Manual Instrumentation\n\n" +
            "Custom actions are created by calling \"Dynatrace.enterAction('actionname')\" and are closed with the \"leaveAction()\" method\n\n" +
            "Unlike auto actions that automatically close after a given duration, manually created actions must be manually closed as well, otherwise they won't be recorded";

    static final String M_MANUAL_WEB_REQUEST_CONCEPT = "For Manual Web Request monitoring, web requests must be manually tagged and timed with the SDK\n\n" +
            "There are a few important things to note about Manual Web Request Monitoring:\n\n" +
            "- Manually tagging web requests that are automatically detected will cause the data to be inaccurate and is not supported\n\n" +
            "- It is not possible to associate AUTOMATICALLY detected web requests with MANUALLY created user actions in Android\n\n" +
            "When correlating web requests to Custom User Actions, it must be ensured that the request is not automatically instrumented\n\n" +
            "Automatic Instrumentation of web requests can be disabled by adding \"webRequests.enabled false\" to the build.gradle file";

    static final String T_SDK_CONCEPT = "Dynatrace Android SDK";
    static final String M_SDK_CONCEPT = "The Android OneAgent SDK is made available when using the Dynatrace Android Gradle Plugin to instrument your app\n\n" +
            "Standalone manual instrumentation does not have the plugin and ONLY provides the SDK to be used for manual instrumentation\n\n" +
            "If autoStart is enabled, the OneAgent uses the configurations provided in the build.gradle, otherwise, any OneAgent configurations will need to be passed to start method for the OneAgent";

    static final String T_PLUGIN_CONCEPT = "Dynatrace Android Gradle Plugin";
    static final String M_PLUGIN_CONCEPT = "The Plugin serves two main purposes\n\n" +
            "1. By default, the plugin automatically instruments your app during build-time\n\n" +
            "This means that any changes to the instrumentation configurations will not take effect until the app is re-built and plugin configurations for instrumentation can ONLY be configured in the build.gradle\n\n\n" +
            "2. If autoStart is enabled, the plugin will automatically start the Android OneAgent during the Application startup in the onCreate method within the MainActivity.\n\n" +
            "autoStart can be disabled and the app will still get instrumented, but data won't get recorded until the OneAgent is manually started with the Agent configurations";

    static final String T_SPLITTING_CONCEPT = "App Termination";
    static final String M_SPLITTING_CONCEPT = "There are two main ways the app can be terminated to 'end' the session\n\n" +
            "1. The user explicitly closes the application\n\n" +
            "2. If the user sends the app to the background and continues to use other apps on the device, it is possible that the OS will purge the app instance from memory\n\n" +
            "In this case, even if the user navigates back to the monitored app before the inactivity timeout is reached and it will 'appear' to be loading the app from the background, a full reload occurs and a new session is created";
    static final String T_CAG_CONCEPT = "Cluster Activegate for BeaconURL";
    static final String M_CAG_CONCEPT = "The default BeaconURL that is configured for the Mobile Agent is the Cluster Activegate\n\n" +
            "For SaaS environments, this is a publicly reachable endpoint for the cluster the tenant is located in and no additional steps should be needed\n\n" +
            "For Managed environments, this uses the \"Public Endpoint\" configured within the CMC Settings\n\n" +
            "If the public endpoint is updated in the CMC, you will need to re-instrument the app with the new BeaconURL downloaded from the Instrumentation Wizard configurations ";

    static final String T_EAG_CONCEPT = "Environment Activegate as BeaconURL";
    static final String M_EAG_CONCEPT = "It is possible to specify an Environment Activegate as the BeaconURL in the Mobile Application \"Instrumentation settings\" for the Mobile App in the UI\n\n" +
            "In order for this to work, the specified Environment Activegate must have beacon forwarding enabled in the Activegate config";

    static final String T_OA_CONCEPT = "Instrumented OneAgent Server for BeaconURL";
    static final String M_OA_CONCEPT = "Similar to the Environment Activegate as a BeaconURL, an Instrumented OneAgent webserver can also be used as the BeaconURL by choosing this option in the \"Instrumentation settings\" within the UI for the Mobile App\n\n" +
            "It is important that the server is publicly reachable otherwise data may not make it back to Dynatrace as expected, and it is also a good idea to ensure that the OneAgent on the webserver is up-to-date";

    static final String T_ABOUT = "About this app";
    static final String M_ABOUT = "This Application was created as a learning tool to help teach Dynatrace Mobile Monitoring concepts and provide interactive examples\n\n" +
            "Tooltips are located throughout the app along with the concepts and troubleshooting section which include info that can help you to better utilize Mobile Application Monitoring\n\n" +
            "While the app is designed to teach through use, it is also provided with the source code to see how it works programmatically, and in Android studio, you can view the TODO list to use as a guide through the code\n\n" +
            "To get started, you will need to configure this for the Mobile Application set up in your Dynatrace tenant (TODO item 1)\n\n" +
            "Cheers";

    static final String T_PUREPATH = "Supported Webserver Processes for RUM correlation";
    static final String M_PUREPATH = "The supported webserver processes for RUM correlation are Java, Apache HTTP Server, IIS, NGINX, and Node.js";

    static final String T_DISABLE_SENSORS="Disable Instrumentation";
    static final String M_DISABLE_SENSORS="In the build.gradle file configurations for the plugin, disable functionality to see if the issue still occurs\n\n" +
            "Lifecycle events:\n" +
            "lifecycle.enabled false\n\n" +
            "User Actions\n" +
            "userActions.enabled false\n\n" +
            "Web Requests\n" +
            "webRequests.enabled false";
}


