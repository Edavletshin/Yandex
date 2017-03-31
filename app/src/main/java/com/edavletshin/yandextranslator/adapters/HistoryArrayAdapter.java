package com.edavletshin.yandextranslator.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edavletshin.yandextranslator.DataBase.DataBaseHelper;
import com.edavletshin.yandextranslator.R;

import java.util.List;

/**
 * Created by edgar on 17.03.2017.
 */

public class HistoryArrayAdapter extends ArrayAdapter<HistoryArrayAdapter.Info> {


    public List<Info> list;
    private DataBaseHelper dbHelper;

    public onElectButtonClickListener delegate;

    //интерфейс для трекинга нажимания на кнопку, которая делает слово избранным или удаляет из избранных
    public interface onElectButtonClickListener {
        void addElected(HistoryArrayAdapter.Info info, boolean isLast);
        void deleteElected(HistoryArrayAdapter.Info info, boolean isLast);
    }

    //конструктор
    public HistoryArrayAdapter(Context context, int resource, List<Info> objects, onElectButtonClickListener delegate) {
        super(context, resource, objects);
        this.delegate = delegate;
        this.list = objects;
        dbHelper = new DataBaseHelper(getContext(),null);
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
        }

        //инициализация вида слов в истории
        ((TextView) convertView.findViewById(R.id.translate)).setText(list.get(position).translate);
        ((TextView) convertView.findViewById(R.id.word)).setText(list.get(position).word);
        ((TextView) convertView.findViewById(R.id.langPair)).setText(list.get(position).langPair);
        final ImageView elect = (ImageView) convertView.findViewById(R.id.elect);

        //слушатель для удаления/добавления избранных слов
        elect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.update(list.get(position).id, list.get(position).isElected);
                if (list.get(position).isElected.equals("1")) {
                    list.get(position).isElected = "0";
                    Toast.makeText(getContext(), "Удалено из избранных", Toast.LENGTH_SHORT).show();
                    elect.setColorFilter(null);
                    if (list.size()==position+1) {
                        delegate.deleteElected(list.get(position),true);
                    } else {
                        delegate.deleteElected(list.get(position),false);
                    }

                } else {
                    if (list.size()==position+1) {
                        delegate.addElected(list.get(position),true);
                    } else {
                        delegate.addElected(list.get(position),false);
                    }
                    list.get(position).isElected = "1";
                    elect.setColorFilter(view.getResources().getColor(R.color.yandexColor));
                    Toast.makeText(getContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (list.get(position).isElected.equals("1")) {
            elect.setColorFilter(getContext().getResources().getColor(R.color.yandexColor));
        } else {
            elect.setColorFilter(null);
        }

        return convertView;
    }

    //класс для слов в истории
    public static class Info {

        public Info(String id, String translate, String word, String langPair, String isElected) {
            this.id = id;
            this.translate = translate;
            this.word = word;
            this.langPair = langPair;
            this.isElected = isElected;
        }

        public String id;
        public String translate;
        public String word;
        public String langPair;
        public String isElected;
    }

}
