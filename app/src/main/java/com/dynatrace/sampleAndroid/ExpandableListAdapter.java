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
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private HashMap<String, HashMap<String, ArrayList<String>>> map;
    private ArrayList<String> topics;
    private Context context;

    public ExpandableListAdapter(HashMap<String, HashMap<String, ArrayList<String>>> m, Context c){
        this.map = m;
        this.topics = new ArrayList<String>(map.keySet());
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

        TextView topic = (TextView) convertView.findViewById(R.id.textViewTopic);
        topic.setText(topics.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ConceptExpandableListView conceptView = new ConceptExpandableListView(context);

        ArrayList<String> concepts = new ArrayList<String>(map.get(topics.get(groupPosition)).keySet());
        String concept = concepts.get(childPosition);

        conceptView.setAdapter(new ConceptListAdapter(concept, map.get(topics.get(groupPosition)).get(concept), context));

        return conceptView;
    }

    public void updateView(HashMap<String, HashMap<String, ArrayList<String>>> newData){
        this.map = newData;
        this.topics = new ArrayList<String>(newData.keySet());
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
     *
     */
    public class ConceptListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private String concept;
        private ArrayList<String> points;
        private TooltipHelper helper;

        public ConceptListAdapter(String s, ArrayList<String> a, Context c){
            this.context = c;
            this.concept = s;
            this.points = a;
            this.helper = new TooltipHelper();
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return points.size();
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
            return points;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_concept, null);
            }

            TextView info = (TextView) convertView.findViewById(R.id.textViewConcept);
            info.setText(concept);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_child_pressable, null);
            }

            // Set the message
            String message = points.get(childPosition);
            Button note = (Button) convertView.findViewById(R.id.buttonInfoDialog);
            TextView info = (TextView) convertView.findViewById(R.id.textViewInfo);
            info.setText(message);

            // Should the child row have a clickable button?
            if(helper.hasClickable(message)){
                note.setVisibility(View.VISIBLE);
                note.setEnabled(true);
                setTooltip(note, message, concept);
            } else {
                note.setVisibility(View.INVISIBLE);
                note.setEnabled(false);
            }

            return convertView;
        }

        /**
         * Helper function to set the button for appropriate sections
         */
        private void setTooltip(Button b, String tag, String concept){

            // "200 User Action limit reached" button is unique and not a dialog
            if (tag.contains("200 User Action limit reached")){
                b.setText("Try it out");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i =0; i<200; i++){
                            DTXAction splitSessionAction = Dynatrace.enterAction("Action #" + String.valueOf(i));
                            splitSessionAction.leaveAction();
                        }
                    }
                });
            } else {
                // Differentiate between Manual and Automatic User Action / Web Request Dialogue
                if(concept.contains("Manual Instrumentation") && (tag.contains("User Actions") || tag.contains("Web Requests"))){
                    tag = "Manual " + tag;
                }

                Pair tooltip = helper.getTooltip(tag);

                b.setText("Note");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle((String) tooltip.first)
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
        }
    }
}
