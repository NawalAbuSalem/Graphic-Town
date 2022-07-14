package com.nns.graphictown.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nns.graphictown.R;

import java.util.List;

public class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedAdapter.SelectedImagesHolder>{


    private List<String> imageList;
    private Context context;
    public ImageSelectedAdapter(List<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectedImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new SelectedImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesHolder holder, final int position) {
        Glide.with(context).load(imageList.get(position)).centerCrop().into(holder.selectedImageView);
        holder.checkBox.setChecked(true);
        holder.checkBox.setEnabled(false);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    class SelectedImagesHolder extends RecyclerView.ViewHolder {
        private ImageView selectedImageView;
        private CheckBox checkBox;
        public SelectedImagesHolder(@NonNull View itemView) {
            super(itemView);
            selectedImageView=itemView.findViewById(R.id.selected_image);
            checkBox=itemView.findViewById(R.id.selected_image_check_box);
        }
    }

}
