package com.example.android.bookstudyplanner.uis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.BookEntity;
import com.example.android.bookstudyplanner.database.PlanningEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanessa on 08/07/2019.
 */

public class TabTodayFragment extends Fragment implements TodayRecyclerViewAdapter.ItemClickListener {

    TodayRecyclerViewAdapter todayRecyclerViewAdapter;
    ArrayList<PlanningEntity> planningEntities = new ArrayList<>();
    public void setPlanningEntities(ArrayList<PlanningEntity> planningEntities) {
        this.planningEntities = planningEntities;
    }

    public void setPlanningsToAdapter(List<PlanningEntity> plannings) {
        if (todayRecyclerViewAdapter != null) {
            todayRecyclerViewAdapter.setPlannings(plannings);
        } else {
            planningEntities.addAll(plannings);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvToday);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        todayRecyclerViewAdapter = new TodayRecyclerViewAdapter(getActivity(), planningEntities);
        todayRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(todayRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        PlanningEntity item = todayRecyclerViewAdapter.getItem(position);

        Utils.tostS(getActivity(), "you clicked on" + position + " title=" + item.getTitle());

        /*
        Intent myIntent = new Intent(getActivity(), BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK, item);

        getActivity().startActivity(myIntent);*/

    }
}
