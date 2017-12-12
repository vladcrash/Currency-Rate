package ru.tinkoff.school.currencyrate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.tinkoff.school.currencyrate.fragments.AnalysisFragment;
import ru.tinkoff.school.currencyrate.fragments.CurrencyListFragment;
import ru.tinkoff.school.currencyrate.fragments.HistoryFragment;
import ru.tinkoff.school.currencyrate.R;

public class MainActivity extends AppCompatActivity {

    private CurrencyListFragment mCurrencyListFragment;
    private HistoryFragment mHistoryFragment;
    private AnalysisFragment mAnalysisFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.exchange:
                    launchFragment(mCurrencyListFragment);
                    return true;
                case R.id.history:
                    launchFragment(mHistoryFragment);
                    return true;
                case R.id.analysis:
                    launchFragment(mAnalysisFragment);
                    return true;
            }

            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mCurrencyListFragment = new CurrencyListFragment();
        mHistoryFragment = new HistoryFragment();
        mAnalysisFragment = new AnalysisFragment();

        launchFragment(mCurrencyListFragment);
    }

    private void launchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
