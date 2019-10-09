package com.disastermate.mapbox.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.disastermate.mapbox.R;

import java.util.ArrayList;

public class Flood2doList extends BaseDrawerActivity {
    private int index;
    ArrayList<Question> questions = new ArrayList<Question>() {{
        add(new Question(R.drawable.low_flood1, "Creating a Plan", false));
        add(new Question(R.drawable.low_flood2, "Preparing an Emergency Box for Evacuation", false));
        add(new Question(R.drawable.low_flood3, "Readying Your Home and Documents in Advance", false));
    }};
    ArrayList<Question> checked_questions = new ArrayList<>();
    ArrayList<Question> unchecked_questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_flooddo_list, frameLayout);

        BaseDrawerActivity.toolbar.setTitle("Flood Todo List");
        index = 0;
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.check_list_popup
                , null));
        settingsDialog.show();
        ImageView image = settingsDialog.findViewById(R.id.check_image);
        image.setImageResource(questions.get(index).getImageId());
        TextView factor_des = settingsDialog.findViewById(R.id.check_description);
        factor_des.setText(questions.get(index).description);
        ImageButton no_button = settingsDialog.findViewById(R.id.chelist_no);
        ImageButton yes_button = settingsDialog.findViewById(R.id.chelist_yes);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unchecked_questions.add(questions.get(index));
                if (index < questions.size() - 1) {
                    index++;
                    Log.i("123","nnnnn");
                    image.setImageResource(questions.get(index).imageId);
                    factor_des.setText(questions.get(index).description);
                } else
                {
                    updateUI();
                    settingsDialog.cancel();}
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questions.get(index).setQuestion_id(true);
                if (index < questions.size() - 1) {
                    index++;
                    Log.i("123","yyyy");
                    image.setImageResource(questions.get(index).imageId);
                    factor_des.setText(questions.get(index).description);
                } else {
                    updateUI();
                    settingsDialog.cancel();
                }

            }
        });

        findViewById(R.id.btn_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 0;
                for (Question q:questions
                ) {
                    q.setQuestion_id(false);
                }
                image.setImageResource(questions.get(index).getImageId());
                factor_des.setText(questions.get(index).description);
                settingsDialog.show();
            }
        });

    }

    private void updateUI(){
        if (questions.get(0).isChecked) {

            findViewById(R.id.check_image_1).setVisibility(View.GONE);
            findViewById(R.id.uncheck_text_1).setVisibility(View.GONE);
            findViewById(R.id.check_text_1).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.check_image_1).setVisibility(View.VISIBLE);
            findViewById(R.id.uncheck_text_1).setVisibility(View.VISIBLE);
            findViewById(R.id.check_text_1).setVisibility(View.GONE);
        }

        if (questions.get(1).isChecked) {

            findViewById(R.id.check_image_2).setVisibility(View.GONE);
            findViewById(R.id.uncheck_text_2).setVisibility(View.GONE);
            findViewById(R.id.check_text_2).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.check_image_2).setVisibility(View.VISIBLE);
            findViewById(R.id.uncheck_text_2).setVisibility(View.VISIBLE);
            findViewById(R.id.check_text_2).setVisibility(View.GONE);
        }
        if (questions.get(2).isChecked) {

            findViewById(R.id.check_image_3).setVisibility(View.GONE);
            findViewById(R.id.uncheck_text_3).setVisibility(View.GONE);
            findViewById(R.id.check_text_3).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.check_image_3).setVisibility(View.VISIBLE);
            findViewById(R.id.uncheck_text_3).setVisibility(View.VISIBLE);
            findViewById(R.id.check_text_3).setVisibility(View.GONE);
        }
    }
}
