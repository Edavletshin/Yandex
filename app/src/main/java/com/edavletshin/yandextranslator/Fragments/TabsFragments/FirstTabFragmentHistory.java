package com.edavletshin.yandextranslator.Fragments.TabsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

public class FirstTabFragmentHistory extends Fragment {

    private View view;
    public EditText searchText;//текст для поиска
    private DataBaseHelper dbHelper;
    private Cursor cursor;
    private ListView listView;
    public HistoryArrayAdapter.onElectButtonClickListener delegate;
    public HistoryArrayAdapter ownAdapter;

    //очситка
    public void clearList(){
        if (ownAdapter!=null){
            ownAdapter.list.clear();
            String[] emptyText = new String[]{"Пусто"};
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
            listView.setAdapter(arrayAdapter);
            searchText.setEnabled(false);
        }
    }

    //убирает из избранных
    public boolean setUnelected(HistoryArrayAdapter.Info info){
        boolean isLast = false;
        for (int i = 0; i < ownAdapter.list.size(); i++){
            if (ownAdapter.list.get(i).id.equals(info.id)){
                ownAdapter.list.get(i).isElected = "0";
                if (ownAdapter.list.size()==i+1){
                    isLast = true;
                }
            }
        }
        ownAdapter.notifyDataSetChanged();
        return isLast;
    }

    //кастомный конструктор
    public static FirstTabFragmentHistory getInstance(HistoryArrayAdapter.onElectButtonClickListener delegate){
        FirstTabFragmentHistory fragment = new FirstTabFragmentHistory();
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
        searchText.setHint("Найти в истории");
        displayListView();

        return view;
    }

    public void displayListView() {
        listView = (ListView) view.findViewById(R.id.langListView);
        dbHelper = new DataBaseHelper(getActivity(), null);
        cursor = dbHelper.fetchHistory(false);

        if (cursor.getCount() == 0) {
            String[] emptyText = new String[]{"Пусто"};
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.text_item,emptyText);
            listView.setAdapter(arrayAdapter);
            searchText.setEnabled(false);
        } else {

            searchText.setEnabled(true);

            List<HistoryArrayAdapter.Info> infoList = new ArrayList<>();

            //инициалиция списка истории
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

            //слушатель для поиска слов по ключу
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
