package com.edavletshin.yandextranslator.Fragments.TabsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.edavletshin.yandextranslator.DataBase.DataBaseHelper;
import com.edavletshin.yandextranslator.DataBase.DataEntry;
import com.edavletshin.yandextranslator.R;
import com.edavletshin.yandextranslator.adapters.HistoryArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edgar on 16.03.2017.
 */

public class SecondTabFragmentElected extends Fragment {

    private View view;
    public EditText searchText;
    private DataBaseHelper dbHelper;
    private Cursor cursor;
    private HistoryArrayAdapter.onElectButtonClickListener delegate;
    private ListView listView;
    public HistoryArrayAdapter ownAdapter;

    //функция для добавления слова в избранное
    public void addItemToElected(HistoryArrayAdapter.Info info){
        if (ownAdapter != null) {
            if (ownAdapter.getCount()!=0) {
                ownAdapter.list.add(info);
                ownAdapter.notifyDataSetChanged();
            } else {
                List<HistoryArrayAdapter.Info> infoList = new ArrayList<>();
                infoList.add(info);
                ownAdapter = new HistoryArrayAdapter(getActivity(),R.layout.listview,infoList,delegate);
                searchText.addTextChangedListener(textWatcher);
                listView.setAdapter(ownAdapter);
                cursor = dbHelper.fetchHistory(true);
                searchText.setEnabled(true);
            }
        } else {
            List<HistoryArrayAdapter.Info> infoList = new ArrayList<>();
            infoList.add(info);
            ownAdapter = new HistoryArrayAdapter(getActivity(),R.layout.listview,infoList,delegate);
            searchText.addTextChangedListener(textWatcher);
            listView.setAdapter(ownAdapter);
            cursor = dbHelper.fetchHistory(true);
            searchText.setEnabled(true);
        }
    }

    //функция для удаления слова из избранного
    public void deleteItemFromElected(HistoryArrayAdapter.Info info){
        for (int i = 0; i < ownAdapter.list.size(); i++){
            if (ownAdapter.list.get(i).id.equals(info.id)){
                ownAdapter.list.remove(i);
            }
        }
        ownAdapter.notifyDataSetChanged();
        if (ownAdapter.getCount()==0){
            String[] emptyText = new String[]{"Пусто"};
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
            listView.setAdapter(arrayAdapter);
            searchText.setEnabled(false);
            searchText.setHint("Найти в избранном");
        }
    }

    //очистка
    public void clearList(){
        if (ownAdapter!=null){
            ownAdapter.list.clear();
            String[] emptyText = new String[]{"Пусто"};
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
            listView.setAdapter(arrayAdapter);
            searchText.setEnabled(false);
        }
    }


    //кастомный конструктор
    public static SecondTabFragmentElected getInstance(HistoryArrayAdapter.onElectButtonClickListener delegate){
        SecondTabFragmentElected fragment = new SecondTabFragmentElected();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.delegate = delegate;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.listview,container,false);
        searchText = (EditText) view.findViewById(R.id.searchText);
        searchText.setHint("Найти в избранном");

        displayListView();

        return view;
    }

    public void displayListView() {
        listView = (ListView) view.findViewById(R.id.langListView);
        dbHelper = new DataBaseHelper(getActivity(), null);
        cursor = dbHelper.fetchHistory(true);

        if (cursor.getCount() == 0) {
            String[] emptyText = new String[]{"Пусто"};
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
            listView.setAdapter(arrayAdapter);
            searchText.setEnabled(false);
        } else {

            List<HistoryArrayAdapter.Info> infoList = new ArrayList<>();

            //добавление в список всех избранных слов
            for (int count = 0; count < cursor.getCount(); count++){
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry._ID));
                String translate = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_TRANSLATE));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_WORD));
                String langPair = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_LANG_PAIR));
                String isElect = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_IS_ELECTED));
                HistoryArrayAdapter.Info info = new HistoryArrayAdapter.Info(id,translate,word,langPair,isElect);
                infoList.add(info);
                cursor.moveToNext();
            }

            HistoryArrayAdapter adapter = new HistoryArrayAdapter(getActivity(),R.layout.listview,infoList,delegate);

            ownAdapter = adapter;

            listView.setAdapter(adapter);

            //слушатель на поиск слов по ключу
            searchText.addTextChangedListener(textWatcher);

        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            cursor.moveToFirst();

            List<HistoryArrayAdapter.Info> infoList2 = new ArrayList<>();

            for (int count = 0; count < cursor.getCount(); count++){
                if ((cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_WORD))
                        +cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_TRANSLATE))).contains(charSequence)){

                    String id = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry._ID));
                    String translate = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_TRANSLATE));
                    String word = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_WORD));
                    String langPair = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_LANG_PAIR));
                    String isElect = cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_IS_ELECTED));
                    HistoryArrayAdapter.Info info = new HistoryArrayAdapter.Info(id,translate,word,langPair,isElect);
                    infoList2.add(info);
                }
                cursor.moveToNext();
            }
            if (infoList2.size()!=0) {
                HistoryArrayAdapter adapter = new HistoryArrayAdapter(getActivity(), R.layout.listview, infoList2,delegate);
                ownAdapter = adapter;
                listView.setAdapter(adapter);
            } else {
                String[] emptyText = new String[]{"Не найдено"};
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
                listView.setAdapter(arrayAdapter);
            }


        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
