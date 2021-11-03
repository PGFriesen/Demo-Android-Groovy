package com.dynatrace.sampleAndroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.dynatrace.android.agent.DTXAction;
import com.dynatrace.android.agent.Dynatrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private HashMap<String, HashMap<String, ArrayList<String>>> map;
    private ArrayList<String> topics;
    private Context context;

    /**
     * The Custom List Adapter for the expandable listview. This is a multi-tiered expandable listview
     * where you expand one item from the top-level and it's children are their own expandable listviews
     *
     * @param m HashMap<Topic, HashMap<Concept, ArrayList<Notes>>>
     *
     * The hierarchy for the Expandable List Views is the following
     *
     *   Topic                           Sessions
     *       Concept                         Session Splitting
     *           Note                            Condition 1
     *                                           Condition 2
     */
    public ExpandableListAdapter(HashMap<String, HashMap<String, ArrayList<String>>> m, Context c){
        this.map = m;
        this.topics = new ArrayList<String>(map.keySet());
        Collections.sort(topics);
        this.context = c;
    }

    @Override
    public int getGroupCount() {
        return topics.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return map.get(topics.get(groupPosition)).keySet().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return topics.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return map.get(topics.get(groupPosition)).get(childPosition);
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_topic, null);
        }

        // Set the text for the Parent as the topic
        ((TextView) convertView.findViewById(R.id.textViewTopic)).setText(topics.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Create the child ListView
        ConceptExpandableListView conceptView = new ConceptExpandableListView(context);

        // Get the list of Concepts for the current topic
        ArrayList<String> concepts = new ArrayList<String>(map.get(topics.get(groupPosition)).keySet());
        String concept = concepts.get(childPosition);

        // Set the adapter for the Concept ListView and pass in the HashMap mapped to the Topic String
        conceptView.setAdapter(new ConceptListAdapter(concept, map.get(topics.get(groupPosition)).get(concept), context));

        return conceptView;
    }

    /**
     * When the dataset is changed (depending on viewing Concepts or Troubleshooting),
     * the mapping  changes and we have to update the view
     *
     * @param newData the new dataset HashMap
     */
    public void updateView(HashMap<String, HashMap<String, ArrayList<String>>> newData){
        this.map = newData;
        this.topics = new ArrayList<String>(newData.keySet());
        Collections.sort(topics);
        notifyDataSetChanged();
    }

    /**
     * Custom ExpandableListView class is used to set the height as ExpandableListViews require maximum height
     */
    public class ConceptExpandableListView extends ExpandableListView {
        public ConceptExpandableListView(Context context){
            super(context);
        }

        protected void onMeasure(int width, int height){
            height = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
            super.onMeasure(width, height);
        }
    }

    /**
     * Child ListView adapter that maps String values to String arrays (Concepts -> Notes)
     */
    public class ConceptListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private String concept;
        private ArrayList<String> notes;
        private TooltipHelper helper;

        /**
         * The ConceptListAdapter is for the "child" ListView within the "parent" ListView
         *
         * However, for this adapter, "Concepts" are the "parent" and "Notes" and the "children"
         *
         * @param s The Concept String
         * @param a List of notes mapped to the given Concept
         */
        public ConceptListAdapter(String s, ArrayList<String> a, Context c){
            this.context = c;
            this.concept = s;
            this.notes = a;
            this.helper = new TooltipHelper();
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return notes.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return concept;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return notes;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_concept, null);
            }

            // Set the text for the "parent" as the Concept
            ((TextView) convertView.findViewById(R.id.textViewConcept)).setText(concept);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_child_pressable, null);
            }

            // Set the text for the note
            String message = notes.get(childPosition);
            ((TextView) convertView.findViewById(R.id.textViewInfo)).setText(message);

            // Hide the button by default...
            Button button = (Button) convertView.findViewById(R.id.buttonInfoDialog);
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);

            // ...then enable it if it needs to be
            if(helper.hasClickable(message)){
                enableButton(button, message, concept);
            }

            return convertView;
        }

        /**
         * For Notes that should enable the button, set the button text and click listener to
         * handle the functionality (All but one show a dialog)
         */
        private void enableButton(Button b, String tag, String concept){
            b.setVisibility(View.VISIBLE);
            b.setEnabled(true);
            b.setText("Try it out");

            // "200 User Action limit reached" button is unique and not a dialog
            if (tag.contains("200 User Action limit reached")){
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create 200 custom user actions to split the session
                        for (int i =0; i<200; i++){
                            DTXAction splitSessionAction = Dynatrace.enterAction("Action #" + String.valueOf(i));
                            splitSessionAction.leaveAction();
                        }
                    }
                });
                return;
            }

            b.setText("Note");

            // Differentiate between Manual and Automatic User Action / Web Request Dialogue
            if(concept.contains("Manual Instrumentation") && (tag.contains("User Actions") || tag.contains("Web Requests"))){
                tag = "Manual " + tag;
            }

            // Get title-message pair for note button dialog
            Pair<String, String> tooltip = helper.getTooltip(tag);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(tooltip.first)
                            .setMessage(tooltip.second)
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
    }
}
