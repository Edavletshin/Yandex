package com.edavletshin.yandextranslator.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.edavletshin.yandextranslator.Fragments.TabsFragments.FirstTabFragmentHistory;
import com.edavletshin.yandextranslator.Fragments.TabsFragments.SecondTabFragmentElected;

/**
 * Created by edgar on 16.03.2017.
 */

public class TabsAdapter extends FragmentStatePagerAdapter {

    private FirstTabFragmentHistory history;
    private SecondTabFragmentElected elected;

    public Elect delegate;
    //интерфейс для изменения иконки добавления в избранное в TranslatorFragment
    public interface Elect{
        void turnElect(/*проверка откуда идет метод, из истории или из избранных*/boolean elected);
    }

    public void onTurnElectListener(Elect delegate){
        this.delegate = delegate;
    }

    private String[] tabs = new String[]{
            "История",
            "Избранное"
    };

    //обновление истории
    public void refreshLists(){
        history.displayListView();
        elected.displayListView();
    }

    //очистка истории
    public void clearList(){
        history.clearList();
        elected.clearList();
    }

    public TabsAdapter(FragmentManager fm, final Context context) {
        super(fm);


        history = FirstTabFragmentHistory.getInstance(new HistoryArrayAdapter.onElectButtonClickListener() {

            @Override
            public void addElected(HistoryArrayAdapter.Info info, boolean isLast) {
                //добавление слова в избранное
                elected.addItemToElected(info);
                if (isLast){
                    //если слово было тоже самое, что и во TranslatorFragment, то там же меняет цвет иконки
                    delegate.turnElect(false);
                }
            }

            @Override
            public void deleteElected(HistoryArrayAdapter.Info info, boolean isLast) {
                //удаление слова из избранных
                elected.deleteItemFromElected(info);
                if (isLast){
                    //если слово было тоже самое, что и во TranslatorFragment, то там же меняет цвет иконки
                    delegate.turnElect(false);
                }
            }
        });

        elected = SecondTabFragmentElected.getInstance(new HistoryArrayAdapter.onElectButtonClickListener() {
            @Override
            public void addElected(HistoryArrayAdapter.Info info, boolean isLast) {

            }

            @Override
            public void deleteElected(HistoryArrayAdapter.Info info, boolean isLast) {
                //удаление слова из избранных
                elected.deleteItemFromElected(info);
                if (/*убирает из избранных*/history.setUnelected(info)){
                    //если слово было тоже самое, что и во TranslatorFragment, то там же меняет цвет иконки
                    delegate.turnElect(true);
                }
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
