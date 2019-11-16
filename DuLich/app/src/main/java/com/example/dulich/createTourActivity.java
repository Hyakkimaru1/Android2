package com.example.dulich;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class createTourActivity extends AppCompatActivity {
    Button create;
    ImageButton start;
    ImageButton end;


    EditText tourName;
    EditText startDay;
    EditText endDay;
    EditText adult;
    EditText children;
    EditText minC;
    EditText maxC;
    ImageView img;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_create_tour);
    create = findViewById(R.id.buttonCreate);
    tourName = findViewById(R.id.editTextTourname);
    startDay = findViewById(R.id.editTextStartday);
    endDay = findViewById(R.id.editTextEndday);
    adult = findViewById(R.id.editTextAdult);
    children = findViewById(R.id.editTextChildren);
    minC = findViewById(R.id.editTextMinC);
    maxC = findViewById(R.id.editTextMaxC);
    img = findViewById(R.id.imageView);
    start = findViewById(R.id.imageButtonStartTime);
    end = findViewById(R.id.imageButtonEndTime);





    }
}
