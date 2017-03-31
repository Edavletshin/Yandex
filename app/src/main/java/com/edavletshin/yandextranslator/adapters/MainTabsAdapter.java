package com.edavletshin.yandextranslator.adapters;

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

    private void refresh(){
        history.refresh();
    }

    public MainTabsAdapter(FragmentManager fm) {
        super(fm);


        translator = new TranslatorFragment();
        //слушатель для обновления истории, если было добавлено слово в бд
        translator.setOnRefreshListener(new TranslatorFragment.Refresh() {
            @Override
            public void refreshHistory() {
                refresh();
            }
        });

        history = new HistoryFragment();
        //слушатель, для того, чтобы иконка добавления в избранное менялась во фргаменте TranslatorFragment,
        // если слово в истории было добавлено/удалено из избранных и являлось тем же самым, что и во фрагменте TranslatorFragment
        history.setOnTurnElectedListener(new HistoryFragment.Elected() {
            @Override
            public void turnElect(boolean elected) {
                translator.turnElect(elected);
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
