package com.example.mapbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ActionActivity extends AppCompatActivity implements View.OnClickListener {


    private Button before;
    private Button during;
    private Button after;

    private TextView before_text;
    private TextView during_text;
    private TextView after_text;


    private TextView temporary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Causes");
        setContentView(R.layout.actions);

        before = findViewById(R.id.before);
        during = findViewById(R.id.during);
        after = findViewById(R.id.after);

        before_text = findViewById(R.id.before_text);
        during_text= findViewById(R.id.during_text);
        after_text = findViewById(R.id.after_text);


        before.setOnClickListener(this);
        during.setOnClickListener(this);
        after.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.before:
                before_text.setVisibility(View.VISIBLE);
                if (temporary != null) {
                    temporary.setVisibility(View.GONE);
                }
                temporary = before_text;
                break;
            case R.id.during:
                during_text.setVisibility(View.VISIBLE);
                if (temporary != null) {
                    temporary.setVisibility(View.GONE);
                }
                temporary = during_text;
                break;
            case R.id.after:
                after_text.setVisibility(View.VISIBLE);
                if (temporary != null) {
                    temporary.setVisibility(View.GONE);
                }
                temporary = after_text;
                break;
        }
    }
}