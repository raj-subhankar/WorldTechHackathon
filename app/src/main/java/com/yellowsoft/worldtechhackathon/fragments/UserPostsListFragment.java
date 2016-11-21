package com.yellowsoft.worldtechhackathon.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yellowsoft.worldtechhackathon.R;
import com.yellowsoft.worldtechhackathon.SessionManager;
import com.yellowsoft.worldtechhackathon.adapters.FeedAdapter;
import com.yellowsoft.worldtechhackathon.models.Post2;
import com.yellowsoft.worldtechhackathon.network.ApiClient;
import com.yellowsoft.worldtechhackathon.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostsListFragment extends Fragment {
    private SessionManager session;
    ArrayList<Post2> posts = new ArrayList<Post2>();
    private RecyclerView recyclerView;
    private FeedAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String userId;

    private static final String TAG = UserPostsListFragment.class.getSimpleName();

    public UserPostsListFragment() {
    }

    //public FeedActivity feedActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getContext());

        //feedActivity = (FeedActivity) getActivity();
        userId = session.getId();
        loadDataFromApi(userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                loadDataFromApi(userId);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);


        return view;
    }

    public void loadDataFromApi(String userid) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Post2>> call = apiService.getUserPosts(userid);
        call.enqueue(new Callback<List<Post2>>() {
            @Override
            public void onResponse(Call<List<Post2>> call, Response<List<Post2>> response) {
                Log.d("response","not received");
                if (!response.body().isEmpty()) {
                    Log.d("response","received");
                    posts.removeAll(posts);
                    posts.addAll(response.body());
                    mAdapter = new FeedAdapter(getContext(), posts);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);

                }
            }

            @Override
            public void onFailure(Call<List<Post2>> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }
}

