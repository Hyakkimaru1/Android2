package com.example.dulich;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class createTourActivity extends AppCompatActivity {
    Button create;
    Button next;
    EditText tourName;
    EditText startDay;
    EditText endDay;
    EditText adult;
    EditText children;
    EditText minC;
    EditText maxC;
    ImageView img;
    EditText departure;
    EditText destinate;

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
    next = findViewById(R.id.buttonNext);
    tourName = findViewById(R.id.editTextTourname);
    startDay = findViewById(R.id.editTextStartday);
    endDay = findViewById(R.id.editTextEndday);
    adult = findViewById(R.id.editTextAdult);
    children = findViewById(R.id.editTextChildren);
    minC = findViewById(R.id.editTextMinC);
    maxC = findViewById(R.id.editTextMaxC);
    img = findViewById(R.id.imageView);


    }
}
