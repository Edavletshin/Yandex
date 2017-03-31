package com.edavletshin.yandextranslator.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edavletshin.yandextranslator.DataBase.DataBaseHelper;
import com.edavletshin.yandextranslator.R;
import com.edavletshin.yandextranslator.adapters.TabsAdapter;

/**
 * Created by edgar on 15.03.2017.
 */

public class HistoryFragment extends Fragment {

    private View view;
    private TabsAdapter adapter;

    private Elected delegate;
    public interface Elected{
        //интерфейс для того, чтобы если пользователь делает слово избранным в истории, то, при условии что это слово
        //еще находится во фрагменте перевода, менялась иконка в желтый цвет
        void turnElect(boolean elected);
    }
    public void setOnTurnElectedListener(Elected delegate){
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history,container,false);
        //инициализация табов
        ViewPager layout = (ViewPager) view.findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        adapter = new TabsAdapter(getActivity().getSupportFragmentManager(),getActivity());
        adapter.onTurnElectListener(new TabsAdapter.Elect() {
            @Override
            public void turnElect(boolean elected) {
                delegate.turnElect(elected);
            }
        });
        layout.setAdapter(adapter);
        tabLayout.setupWithViewPager(layout);

        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //очистка бд
                Toast.makeText(getActivity(), "Очищено", Toast.LENGTH_SHORT).show();
                adapter.clearList();
                new DataBaseHelper(getActivity(), null).clearDB();
            }
        });
        return view;
    }

    //функция для обновления истории
    public void refresh(){
        adapter.refreshLists();
    }

}
