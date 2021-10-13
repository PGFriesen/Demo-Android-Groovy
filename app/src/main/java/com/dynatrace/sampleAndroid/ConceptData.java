package com.dynatrace.sampleAndroid;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ConceptData {

    private HashMap<String, HashMap<String, ArrayList<String>>> concepts;
    private HashMap<String, HashMap<String, ArrayList<String>>> troubleshooting;
    private Resources resources;

    private static final String CONCEPTS = "concepts";
    private static final String TROUBLESHOOTING = "troubleshooting";

    /**
     * Class to help create the HashMap objects for the ExpandableListView Data to populate depending on
     * whether we are viewing CONCEPTS or TROUBLESHOOTING
     * @param r
     */
    public ConceptData(Resources r){
        this.resources = r;
        this.concepts = new HashMap<String, HashMap<String, ArrayList<String>>>();
        this.troubleshooting = new HashMap<String, HashMap<String, ArrayList<String>>>();

        setConceptData();
        setTroubleshootingData();
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getConceptData() { return concepts; }
    public HashMap<String, HashMap<String, ArrayList<String>>> getTroubleshootingData() { return troubleshooting; }

    private void setConceptData(){
        HashMap<String, ArrayList<String>> instrumentationConcepts = new HashMap<String, ArrayList<String>>();
            ArrayList<String> automaticInstrumentation = new ArrayList<String>();
            Collections.addAll(automaticInstrumentation, resources.getStringArray(R.array.automatic_instrumentation));
            instrumentationConcepts.put("Automatic Instrumentation",automaticInstrumentation);

            ArrayList<String> manualInstrumentation = new ArrayList<String>();
            Collections.addAll(manualInstrumentation, resources.getStringArray(R.array.manual_instrumentation));
            instrumentationConcepts.put("Manual Instrumentation",manualInstrumentation);

            ArrayList<String> standaloneInstrumentation = new ArrayList<String>();
            Collections.addAll(standaloneInstrumentation, resources.getStringArray(R.array.standalone_instrumentation));
            instrumentationConcepts.put("Standalone Manual Instrumentation",standaloneInstrumentation);
        concepts.put("Instrumentation Types", instrumentationConcepts);


        HashMap<String, ArrayList<String>> dataPrivacyConcepts = new HashMap<String, ArrayList<String>>();
            ArrayList<String> dataCollectionLevels = new ArrayList<String>();
            Collections.addAll(dataCollectionLevels, resources.getStringArray(R.array.data_collection));
            dataPrivacyConcepts.put("Data Collection Levels",dataCollectionLevels);

            ArrayList<String> userOptIn = new ArrayList<String>();
            Collections.addAll(userOptIn, resources.getStringArray(R.array.user_opt_in));
            dataPrivacyConcepts.put("userOptIn configurations",userOptIn);
        concepts.put("Data Privacy and UserOptIn", dataPrivacyConcepts);


        HashMap<String, ArrayList<String>> sessionConcepts = new HashMap<String, ArrayList<String>>();
            ArrayList<String> splittingConditions = new ArrayList<String>();
            Collections.addAll(splittingConditions, resources.getStringArray(R.array.session_splitting_conditions));
            sessionConcepts.put("Sessions end (split) when...",splittingConditions);
        concepts.put("Sessions", sessionConcepts);


        HashMap<String, ArrayList<String>> communicationConcepts = new HashMap<String, ArrayList<String>>();
            ArrayList<String> beaconURL = new ArrayList<String>();
            Collections.addAll(beaconURL, resources.getStringArray(R.array.beacon_url));
            communicationConcepts.put("BeaconURL", beaconURL);

            ArrayList<String> sendingIntervals = new ArrayList<String>();
            Collections.addAll(sendingIntervals, resources.getStringArray(R.array.sending_intervals));
            communicationConcepts.put("Reporting intervals", sendingIntervals);
        concepts.put("Communication", communicationConcepts);


        HashMap<String, ArrayList<String>> correlationConcepts = new HashMap<String, ArrayList<String>>();
            ArrayList<String> requirements = new ArrayList<String>();
            Collections.addAll(requirements, resources.getStringArray(R.array.purepath_requirements));
            correlationConcepts.put("Requirements", requirements);
        concepts.put("Purepath Correlation", correlationConcepts);
    }

    private void setTroubleshootingData(){
        HashMap<String, ArrayList<String>> missingDataIssue = new HashMap<String, ArrayList<String>>();
            ArrayList<String> noData = new ArrayList<String>();
            Collections.addAll(noData, resources.getStringArray(R.array.no_data));
            missingDataIssue.put("No data in Dynatrace",noData);

            ArrayList<String> singleAction = new ArrayList<String>();
            Collections.addAll(singleAction, resources.getStringArray(R.array.single_action));
            missingDataIssue.put("Only seeing 1 User Action per Session",singleAction);

            ArrayList<String> missingRequests = new ArrayList<String>();
            Collections.addAll(missingRequests, resources.getStringArray(R.array.missing_requests));
            missingDataIssue.put("Missing Web Requests in Waterfall Analysis",missingRequests);
        troubleshooting.put("Missing data", missingDataIssue);

        HashMap<String, ArrayList<String>> logging = new HashMap<String, ArrayList<String>>();
            ArrayList<String> enableLogging = new ArrayList<String>();
            Collections.addAll(enableLogging, resources.getStringArray(R.array.enable_logs));
            logging.put("Getting Logs",enableLogging);

            ArrayList<String> analyzeLogs = new ArrayList<String>();
            Collections.addAll(analyzeLogs, resources.getStringArray(R.array.analyze_logs));
            logging.put("Analyzing Logs",analyzeLogs);
        troubleshooting.put("Logging", logging);

        HashMap<String, ArrayList<String>> problems = new HashMap<String, ArrayList<String>>();
            ArrayList<String> unexpectedBehavior = new ArrayList<String>();
            Collections.addAll(unexpectedBehavior, resources.getStringArray(R.array.crashes_or_unexpected_behavior));
            problems.put("Crashes or Unexpected Behavior",unexpectedBehavior);
        troubleshooting.put("Problems with Mobile App when Instrumented", problems);

        HashMap<String, ArrayList<String>> failedBuild = new HashMap<String, ArrayList<String>>();
            ArrayList<String> buildLogs = new ArrayList<String>();
            Collections.addAll(buildLogs, resources.getStringArray(R.array.build_logs));
            failedBuild.put("Retrieve Verbose Build Logs",buildLogs);

            ArrayList<String> supported = new ArrayList<String>();
            Collections.addAll(supported, resources.getStringArray(R.array.is_app_supported));
            failedBuild.put("Ensure the app is supported",supported);
        troubleshooting.put("Build Fails when Instrumented", failedBuild);


    }


}
