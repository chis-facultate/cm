package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Map.Entry<String, String>> itemList;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, String> item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<Map.Entry<String, String>> getItemList() {
        return itemList;
    }

    public void setItemList(List<Map.Entry<String, String>> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCheie;
        private final TextView textViewVal;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewCheie = itemView.findViewById(R.id.id_textView_cheie_item_layout);
            textViewVal = itemView.findViewById(R.id.id_textView_val_item_layout);

            // Listener click
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                listener.onItemClick(position);
            });
        }

        public void bind(Map.Entry<String, String> item) {
            textViewCheie.setText(item.getKey());
            textViewVal.setText(item.getValue());
        }
    }
}
