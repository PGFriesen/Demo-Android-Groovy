package com.dynatrace.sampleAndroid;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

public class ConceptsActivity extends AppCompatActivity {

    private static final String CONCEPTS = "concepts";
    private static final String TROUBLESHOOTING = "troubleshooting";
    private ExpandableListAdapter adapter;
    private ExpandableListView expandableListView;
    private ConceptData data;
    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concepts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize Expandable ListView with data
        this.data = new ConceptData(getResources());
        this.expandableListView = findViewById(R.id.expandableListViewConcepts);
        this.adapter = new ExpandableListAdapter(data.getConceptData(), this);

        expandableListView.setAdapter(adapter);

        // Set the ExpandableListView to collapse other groups to avoid screen clutter
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
            @Override
            public void onGroupExpand(int groupPosition){
                if (lastPosition != -1 && groupPosition != lastPosition){
                    expandableListView.collapseGroup(lastPosition);
                }
                lastPosition = groupPosition;
            }
        });

        onSwitchList(findViewById(R.id.buttonConcepts));
    }


    public void onSwitchList(View view){
        View otherView;
        if (view.getId() == R.id.buttonConcepts){
            adapter.updateView(data.getConceptData());
            otherView = findViewById(R.id.buttonTroubleshooting);
        } else {
            otherView = findViewById(R.id.buttonConcepts);
            adapter.updateView(data.getTroubleshootingData());
        }

        // Programatically set button colors
        view.setBackgroundResource(R.drawable.button_teal_pressed);
        ((Button)view).setTextColor(0xFFFFFFFF);
        otherView.setBackgroundResource(R.drawable.button_white_unfocused);
        ((Button)otherView).setTextColor(0xFF000000);
    }

}