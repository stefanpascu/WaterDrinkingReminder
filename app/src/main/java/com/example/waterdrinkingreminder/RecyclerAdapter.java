package com.example.waterdrinkingreminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    String userId;

    private static final String TAG = "RecyclerAdapter";
    List<String> cupSizesList;
    List<String> cupSizesListAll;
    List<Drawable> containerList;

    public RecyclerAdapter(List<String> cupSizesList, ArrayList<Drawable> containerList) {
        this.cupSizesList = cupSizesList;
        this.containerList = containerList;
        cupSizesListAll = new ArrayList<>();
        cupSizesListAll.addAll(cupSizesList);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(cupSizesList.get(position));
        holder.imageView.setBackground(containerList.get(position));
    }

    @Override
    public int getItemCount() {
        return cupSizesList.size();
    }

    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<String> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(cupSizesListAll);
            } else {
                for (String movie: cupSizesListAll) {
                    if (movie.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            cupSizesList.clear();
            cupSizesList.addAll((Collection<? extends String>) filterResults.values);
            notifyDataSetChanged();
        }
    };



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            DocumentReference documentReference = firebaseStore.collection("users").document(userId);
            documentReference.update("cupVolume", cupSizesList.get(getAdapterPosition()).split(" ")[0]);
            Toast.makeText(view.getContext(), cupSizesList.get(getAdapterPosition()) + " container selected", Toast.LENGTH_SHORT).show();
        }

    }

}