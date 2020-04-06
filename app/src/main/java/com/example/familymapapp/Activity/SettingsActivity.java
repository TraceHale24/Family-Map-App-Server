package com.example.familymapapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.familymap.R;
import com.example.familymapapp.Extras.DataCache;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout mLogout;
    private Switch mLifeStorySwitch;
    private Switch mFamilyTreeSwitch;
    private Switch mSpouseSwitch;
    private Switch mFathersSideSwitch;
    private Switch mMothersSideSwitch;
    private Switch mMalesSwitch;
    private Switch mFemalesSwitch;
    DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLogout = (LinearLayout) findViewById(R.id.logoutLayout);
        mLifeStorySwitch = (Switch) findViewById(R.id.lifeStoryLinesSwitch);
        mFamilyTreeSwitch = (Switch) findViewById(R.id.familyTreeLinesSwitch);
        mSpouseSwitch = (Switch) findViewById(R.id.spouseLinesSwitch);
        mFathersSideSwitch = (Switch) findViewById(R.id.fathersLineSwitch);
        mMothersSideSwitch = (Switch) findViewById(R.id.mothersLinesSwitch);
        mMalesSwitch = (Switch) findViewById(R.id.maleEventsSwitch);
        mFemalesSwitch = (Switch) findViewById(R.id.femaleEventsSwitch);


        mLifeStorySwitch.setChecked(dataCache.isLifeStoryLines());
        mFamilyTreeSwitch.setChecked(dataCache.isFamilyTreeLines());
        mSpouseSwitch.setChecked(dataCache.isSpouseLines());
        mFathersSideSwitch.setChecked(dataCache.isFatherSide());
        mMothersSideSwitch.setChecked(dataCache.isMotherSide());
        mMalesSwitch.setChecked(dataCache.isMaleEvents());
        mFemalesSwitch.setChecked(dataCache.isFemaleEvents());


        mLifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setLifeStoryLines(isChecked);
            }
        });

        mFamilyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFamilyTreeLines(isChecked);
            }
        });

        mSpouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setSpouseLines(isChecked);
            }
        });
        mFathersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFatherSide(isChecked);
            }
        });

        mMothersSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMotherSide(isChecked);
            }
        });
        mMalesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMaleEvents(isChecked);
            }
        });

        mFemalesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFemaleEvents(isChecked);
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                dataCache.clear();
                startActivity(i);
                MainActivity mainActivity = new MainActivity();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
