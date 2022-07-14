package com.nns.graphictown.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Model.subscategory.SubCategory;
import com.nns.graphictown.Model.subscategory.SubCategoryData;
import com.nns.graphictown.R;

import java.util.List;

public class SubCategoryFilterAdapter extends RecyclerView.Adapter<SubCategoryFilterAdapter.SubCategoryHolder> {
    private List<SubCategoryData> subCategoryFilterList;
    RecyclerView recyclerView;
    private int idChecked = 0;
    private Context context;
    private OnSubCategoryFilterClickListener onSubCategoryFilterClickListener;

    public interface OnSubCategoryFilterClickListener {
        void onClick(int subCategoryId);
    }

    public SubCategoryFilterAdapter(List<SubCategoryData> subCategoryFilterList, Context context) {
        this.subCategoryFilterList = subCategoryFilterList;
        this.context = context;

    }

    public void setOnSubCategoryFilterClickListener(OnSubCategoryFilterClickListener onSubCategoryFilterClickListener) {
        this.onSubCategoryFilterClickListener = onSubCategoryFilterClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public SubCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategroy_filter, parent, false);
        return new SubCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryHolder holder, final int position) {
        if (idChecked == position) {
            holder.filterIndicator.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.filterText.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.filterIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.filterText.setTextColor(context.getResources().getColor(android.R.color.black));
        }
        holder.filterText.setText(subCategoryFilterList.get(position).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 idChecked = position;
                 notifyDataSetChanged();
                 recyclerView.scrollToPosition(position);
                 onSubCategoryFilterClickListener.onClick(subCategoryFilterList.get(position).getId());
             }
        });

    }

    @Override
    public int getItemCount() {
        return subCategoryFilterList.size();
    }

    class SubCategoryHolder extends RecyclerView.ViewHolder {
        private View filterIndicator;
        private TextView filterText;
        SubCategoryHolder(@NonNull View itemView) {
            super(itemView);
            filterIndicator = itemView.findViewById(R.id.subcategory_filter_indicator);
            filterText = itemView.findViewById(R.id.subcategory_filter_text);
        }
    }
}
