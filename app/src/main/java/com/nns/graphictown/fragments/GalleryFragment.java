package com.nns.graphictown.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nns.graphictown.Adapters.GalleryImagesAdapter;
import com.nns.graphictown.R;
import java.util.List;
public class GalleryFragment extends Fragment {


    public GalleryFragment() {
    }

    private List<String>imagesList;
    private RecyclerView galleryRecyclerView;
    private OnImageClickListener onImageClickListener;
    public GalleryFragment(List<String> imagesList) {
        this.imagesList = imagesList;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageClickListener){
            onImageClickListener= (OnImageClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.gallery_fragment,container,false);
        galleryRecyclerView = view.findViewById(R.id.gallery_recycler_view);
        addGalleryRecyclerAdapter();
        return view;
    }
    private void addGalleryRecyclerAdapter() {
        galleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        final GalleryImagesAdapter galleryImagesAdapter=new GalleryImagesAdapter(imagesList,getActivity());
        galleryRecyclerView.setAdapter(galleryImagesAdapter);
        galleryImagesAdapter.setOnGalleryImageClickListener(new GalleryImagesAdapter.OnGalleryImageClickListener() {
            @Override
            public void addNewImage(String imageUri,int position) {
                onImageClickListener.onInsertNewImageListener(imageUri);

            }
            @Override
            public void removeImage(String imageUri,int position) {
             onImageClickListener.onDeleteImageListener(imageUri);
            }
        });


    }






}
