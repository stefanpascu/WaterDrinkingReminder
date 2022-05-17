package com.example.waterdrinkingreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;

import java.util.ArrayList;

public class SwitchCup extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ArrayList<String> moviesList;
    ArrayList<Drawable> containerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_cup);

        moviesList = new ArrayList<>();
        containerList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(moviesList, containerList);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        moviesList.add("50 ml");
        moviesList.add("75 ml");
        moviesList.add("100 ml");
        moviesList.add("125 ml");
        moviesList.add("150 ml");
        moviesList.add("175 ml");
        moviesList.add("200 ml");
        moviesList.add("225 ml");
        moviesList.add("250 ml");
        moviesList.add("275 ml");
        moviesList.add("300 ml");
        moviesList.add("325 ml");
        moviesList.add("350 ml");
        moviesList.add("375 ml");
        moviesList.add("400 ml");
        moviesList.add("425 ml");
        moviesList.add("450 ml");
        moviesList.add("475 ml");
        moviesList.add("500 ml");
        moviesList.add("750 ml");
        moviesList.add("1000 ml");

        containerList.add(getDrawable(R.drawable.ic_cup1));
        containerList.add(getDrawable(R.drawable.ic_cup1));
        containerList.add(getDrawable(R.drawable.ic_cup1));
        containerList.add(getDrawable(R.drawable.ic_cup2));
        containerList.add(getDrawable(R.drawable.ic_cup2));
        containerList.add(getDrawable(R.drawable.ic_cup2));
        containerList.add(getDrawable(R.drawable.ic_cup3));
        containerList.add(getDrawable(R.drawable.ic_cup3));
        containerList.add(getDrawable(R.drawable.ic_cup3));
        containerList.add(getDrawable(R.drawable.ic_cup4));
        containerList.add(getDrawable(R.drawable.ic_cup4));
        containerList.add(getDrawable(R.drawable.ic_cup4));
        containerList.add(getDrawable(R.drawable.ic_cup5));
        containerList.add(getDrawable(R.drawable.ic_cup5));
        containerList.add(getDrawable(R.drawable.ic_cup5));
        containerList.add(getDrawable(R.drawable.ic_cup6));
        containerList.add(getDrawable(R.drawable.ic_cup6));
        containerList.add(getDrawable(R.drawable.ic_cup6));
        containerList.add(getDrawable(R.drawable.ic_cup7));
        containerList.add(getDrawable(R.drawable.ic_cup7));
        containerList.add(getDrawable(R.drawable.ic_cup7));

    }
}