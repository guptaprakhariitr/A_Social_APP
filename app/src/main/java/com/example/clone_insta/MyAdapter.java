package com.example.clone_insta;

import android.media.Image;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public ArrayList<String> mItemList;
  public List<String> mnameList;
    public ArrayList<String> mImageUrlList;

    public MyAdapter(ArrayList<String> itemList, ArrayList<String> imageUrlList,ArrayList<String> names) {
        mItemList = itemList;
        mnameList=names;
        mImageUrlList = imageUrlList;
        Log.i("Tag_in","here1"+itemList.size());
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem, namesC;
        Button like_it;
        ImageView post_image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i("Tag_in","here2");
            namesC=itemView.findViewById(R.id.post_uploader);
            tvItem = itemView.findViewById(R.id.post_caption);
           // likes_count = itemView.findViewById(R.id.like_count);
            like_it = itemView.findViewById(R.id.like_button);
            post_image = itemView.findViewById(R.id.post_pic);

        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        Log.i("Tag_in","here3");
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }
    @NonNull
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        Log.i("Tag_in","here4");
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        Log.i("Tag_in","hello7");
        Log.i("Tag_in"," "+mItemList.size());
        return mnameList.size();

    }
    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        Log.i("Tag_in","here5");
        String item = mItemList.get(position);
        Log.i("Tag_in",item);
        viewHolder.tvItem.setText(item);
        String urll = mImageUrlList.get(position);
        Log.i("Tag_in",urll);
        if (!urll.equals("")) {
            Glide.with(viewHolder.tvItem.getContext())
                    .load(urll)
                    .into(viewHolder.post_image);
        } else {
            viewHolder.post_image.setImageResource(R.drawable.noimage);
        }
        String name;
        try {
             name=mnameList.get(position);
        if(name!=null) {
            viewHolder.namesC.setText("Uploaded by "+name);

        }
        }
        catch (ArrayIndexOutOfBoundsException r){
            name="------";
        }
    }
}