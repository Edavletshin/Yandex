package com.edavletshin.yandextranslator.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.edavletshin.yandextranslator.Fragments.HistoryFragment;
import com.edavletshin.yandextranslator.Fragments.TranslatorFragment;

/**
 * Created by edgar on 17.03.2017.
 */

public class MainTabsAdapter extends FragmentStatePagerAdapter {

    private TranslatorFragment translator;
    private HistoryFragment history;

    private String[] tabs = new String[]{
            "",
            ""
    };

    public void refresh(){
        history.refresh();
    }

    public MainTabsAdapter(FragmentManager fm) {
        super(fm);


        history = new HistoryFragment();

        translator = new TranslatorFragment();

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return translator;
            case 1:
                return history;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
