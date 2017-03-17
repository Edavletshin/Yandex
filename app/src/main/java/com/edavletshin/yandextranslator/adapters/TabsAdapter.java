package com.edavletshin.yandextranslator.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.edavletshin.yandextranslator.Fragments.TabsFragments.SecondTabFragmentElected;
import com.edavletshin.yandextranslator.Fragments.TabsFragments.FirstTabFragmentHistory;

/**
 * Created by edgar on 16.03.2017.
 */

public class TabsAdapter extends FragmentStatePagerAdapter {

    private FirstTabFragmentHistory history;
    private SecondTabFragmentElected elected;

    private String[] tabs = new String[]{
            "История",
            "Избранное"
    };

    public void refreshLists(){
        history.displayListView();
        elected.displayListView();
    }

    public void clearList(){
        history.clearList();
        elected.clearList();
    }

    public TabsAdapter(FragmentManager fm, final Context context) {
        super(fm);


        history = FirstTabFragmentHistory.getInstance(new HistoryArrayAdapter.onElectButtonClickListener() {
            @Override
            public void addElected(HistoryArrayAdapter.Info info) {
                elected.addItemToElected(info);
            }

            @Override
            public void deleteElected(HistoryArrayAdapter.Info info) {
                elected.deleteItemFromElected(info);
            }
        });

        elected = SecondTabFragmentElected.getInstance(new HistoryArrayAdapter.onElectButtonClickListener() {
            @Override
            public void addElected(HistoryArrayAdapter.Info info) {

            }

            @Override
            public void deleteElected(HistoryArrayAdapter.Info info) {
                elected.deleteItemFromElected(info);
                history.setUnelected(info);
            }
        });

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return history;
            case 1:
                return elected;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
