package com.yellowsoft.worldtechhackathon.adapters;

/**
 * Created by subhankar on 11/22/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yellowsoft.worldtechhackathon.R;
import com.yellowsoft.worldtechhackathon.SessionManager;
import com.yellowsoft.worldtechhackathon.models.Post2;
import com.yellowsoft.worldtechhackathon.models.Result;
import com.yellowsoft.worldtechhackathon.network.ApiClient;
import com.yellowsoft.worldtechhackathon.network.ApiInterface;
import com.yellowsoft.worldtechhackathon.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private Context context;
    ArrayList<Post2> ArrayListPosts;

    public SessionManager session;
    public String userid;

    private final int cellSize;


    public GridAdapter(Context context, ArrayList<Post2> objects) {
        this.context = context;
        ArrayListPosts = objects;
        this.cellSize = Utils.getScreenWidth(context) / 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageFeedItem;

        public MyViewHolder(View view) {
            super(view);
            imageFeedItem = (ImageView) view.findViewById(R.id.ivPhoto);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Post2 post = ArrayListPosts.get(position);
//        Picasso.with(context).load(post.getImageUrl()).into(holder.imageFeedItem);
        if(post.getImageUrl() != "") {
//            Glide.with(context).load(post.getImageUrl())
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.imageFeedItem);
            Picasso.with(context)
                    .load(post.getImageUrl())
                    .resize(cellSize, cellSize)
                    .centerCrop()
                    .into(holder.imageFeedItem);
        } else {
            holder.imageFeedItem.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ArrayListPosts.size();
    }
}



