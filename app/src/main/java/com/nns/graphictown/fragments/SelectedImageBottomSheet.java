package com.nns.graphictown.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nns.graphictown.Adapters.ImageSelectedAdapter;
import com.nns.graphictown.R;

import java.util.ArrayList;

public class SelectedImageBottomSheet extends BottomSheetDialogFragment {
    private ArrayList<String> selectedImagesList;
    private RecyclerView selectedImageRecyclerView;
    ImageSelectedAdapter imageSelectedAdapter;

    public SelectedImageBottomSheet(Context context) {
        selectedImagesList=new ArrayList<>();
        imageSelectedAdapter=new ImageSelectedAdapter(selectedImagesList,context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_images,container,false);
        selectedImageRecyclerView=view.findViewById(R.id.selected_image_recycler_view);
        selectedImageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        selectedImageRecyclerView.setAdapter(imageSelectedAdapter);
        return view;
    }
    public void InsetNewImage(String imageUri){
        selectedImagesList.add(imageUri);
        imageSelectedAdapter.notifyItemInserted(selectedImagesList.size()-1);
    }
}
