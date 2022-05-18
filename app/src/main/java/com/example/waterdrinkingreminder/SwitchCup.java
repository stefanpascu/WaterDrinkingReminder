package com.example.waterdrinkingreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class SwitchCup extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ArrayList<String> cupSizesList;
    ArrayList<Drawable> containerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Filter your results");

        setContentView(R.layout.activity_switch_cup);

        cupSizesList = new ArrayList<>();
        containerList = new ArrayList<>();


        cupSizesList.add("50 ml");
        cupSizesList.add("75 ml");
        cupSizesList.add("100 ml");
        cupSizesList.add("125 ml");
        cupSizesList.add("150 ml");
        cupSizesList.add("175 ml");
        cupSizesList.add("200 ml");
        cupSizesList.add("225 ml");
        cupSizesList.add("250 ml");
        cupSizesList.add("275 ml");
        cupSizesList.add("300 ml");
        cupSizesList.add("325 ml");
        cupSizesList.add("350 ml");
        cupSizesList.add("375 ml");
        cupSizesList.add("400 ml");
        cupSizesList.add("425 ml");
        cupSizesList.add("450 ml");
        cupSizesList.add("475 ml");
        cupSizesList.add("500 ml");
        cupSizesList.add("750 ml");
        cupSizesList.add("1000 ml");

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(cupSizesList, containerList);
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}