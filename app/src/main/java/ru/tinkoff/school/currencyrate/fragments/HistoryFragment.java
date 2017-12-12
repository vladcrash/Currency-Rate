package ru.tinkoff.school.currencyrate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.FilterActivity;
import ru.tinkoff.school.currencyrate.adapters.HistoryAdapter;
import ru.tinkoff.school.currencyrate.asynchronous.GettingFilteredCurrencyTask;


public class HistoryFragment extends Fragment {

    private RecyclerView mHistoryRecyclerView;
    private HistoryAdapter mAdapter;
    private boolean isCall;

    @Override
    public void onResume() {
        super.onResume();
        if (isCall) {
            new GettingFilteredCurrencyTask(getActivity(), mAdapter).execute();
        }
        isCall = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HistoryAdapter(getActivity());
        new GettingFilteredCurrencyTask(getActivity(), mAdapter).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);
        mHistoryRecyclerView = view.findViewById(R.id.history_recycler_view);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHistoryRecyclerView.setAdapter(mAdapter);

        getActivity().setTitle(R.string.story);
        isCall = false;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate((R.menu.fragment_history), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                FilterActivity.start(getActivity());
                return true;
            default:
                return false;
        }
    }

}
