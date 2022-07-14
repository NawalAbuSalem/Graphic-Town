package com.nns.graphictown.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.nns.graphictown.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryImagesAdapter extends RecyclerView.Adapter<GalleryImagesAdapter.GalleryImagesHolder>{
    private Map<String, Boolean> checkBoxStates = new HashMap<>();
    public interface OnGalleryImageClickListener {
        void addNewImage(String imageUri,int position);
        void removeImage(String imageUri,int position);
    }

    private OnGalleryImageClickListener onGalleryImageClickListener;
    private List<String>imageList;
    private Context context;
    public GalleryImagesAdapter(List<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;

    }

    @NonNull
    @Override
    public GalleryImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        GalleryImagesHolder holder=new GalleryImagesHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryImagesHolder holder, final int position) {
        Boolean checkedState = checkBoxStates.get(imageList.get(position));
        holder.imageCheckBox.setChecked(checkedState == null ? false : checkedState);
        Glide.with(context).load(imageList.get(position)).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.spin_kit.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.galleryImageView);
        holder.imageCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked=holder.imageCheckBox.isChecked();
                onCheckChanged(position);
                if (isChecked){
                    onGalleryImageClickListener.addNewImage(imageList.get(position),position);
                }else {
                onGalleryImageClickListener.removeImage(imageList.get(position),position);
                }
            }
        });

    }
    // call this when the checked state is changed
    private void onCheckChanged(int position) {
         String item = imageList.get(position);
        if (item == null) {
            return;
        }
        Boolean lastCheckedState = checkBoxStates.get(imageList.get(position));
        boolean checkedState = (null == lastCheckedState) ? false : lastCheckedState;
        checkBoxStates.put(item, !checkedState);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setOnGalleryImageClickListener(OnGalleryImageClickListener onGalleryImageClickListener) {
        this.onGalleryImageClickListener = onGalleryImageClickListener;
    }

    class GalleryImagesHolder extends RecyclerView.ViewHolder {
        private ImageView galleryImageView;
        private CheckBox imageCheckBox;
        private SpinKitView spin_kit;
        public GalleryImagesHolder(@NonNull View itemView) {
            super(itemView);
            galleryImageView=itemView.findViewById(R.id.gallery_image);
            imageCheckBox=itemView.findViewById(R.id.image_check_box);
            spin_kit=itemView.findViewById(R.id.spin_kit);
        }
    }
    /*
    Glide.with(context)
                    .load(data.getImagePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            myViewHolder.spin_kit.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(myViewHolder.image_pro);
     */

}
