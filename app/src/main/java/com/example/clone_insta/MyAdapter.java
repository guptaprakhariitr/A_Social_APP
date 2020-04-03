package com.example.clone_insta;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<String> mItemList;
    public List<String> mLikeList;
    public List<String> mImageUrlList;

    public MyAdapter(List<String> itemList, List<String> likeList, List<String> imageUrlList) {
        mItemList = itemList;
        mLikeList = likeList;
        mImageUrlList = imageUrlList;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem, likes_count;
        Button like_it;
        ImageView post_image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.caption);
            likes_count = itemView.findViewById(R.id.like_count);
            like_it = itemView.findViewById(R.id.like_button);
            post_image = itemView.findViewById(R.id.post_pic);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        String item = mItemList.get(position);
        viewHolder.tvItem.setText(item);
        String urll = mImageUrlList.get(position);
        if (!urll.equals("")) {
            Glide.with(viewHolder.itemView.getContext())
                    .load(urll)
                    .into(viewHolder.post_image);
        } else {
            viewHolder.post_image.setImageResource(R.drawable.noimage);
        }
    }
}