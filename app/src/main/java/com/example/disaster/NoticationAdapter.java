package com.example.disaster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NoticationAdapter extends RecyclerView.Adapter<NoticationAdapter.NotificationViewHolder> {
    private Context context;
    private ArrayList<String> products;

    public NoticationAdapter(Context context, ArrayList<String> strings){
        this.context = context;
        this.products = strings;
    }

    @NonNull
    @Override
    public NoticationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.notification_list_item, parent, false);
        return new NotificationViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull NoticationAdapter.NotificationViewHolder holder, int position) {
        String content = products.get(position);
        holder.nameTxt.setText(content);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "MyHolder";

      TextView nameTxt;


        private NotificationViewHolder(View itemView) {
            super(itemView);

            nameTxt= itemView.findViewById(R.id.notification_content);
        }
    }
}