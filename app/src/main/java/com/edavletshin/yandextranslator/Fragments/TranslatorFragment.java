package com.edavletshin.yandextranslator.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edavletshin.yandextranslator.DataBase.DataBaseHelper;
import com.edavletshin.yandextranslator.DataBase.DataEntry;
import com.edavletshin.yandextranslator.DataBase.LangPairs;
import com.edavletshin.yandextranslator.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by edgar on 15.03.2017.
 */

public class TranslatorFragment extends Fragment {

    private static final int RESULT_OK = -1;//для голосового ввода
    private static final int REQUEST_VOICE = 9999;//для голосового ввода

    private ArrayAdapter<String> adapterForList;//лист для окна popup
    private boolean isElected = false;//был ли translatedText добавлен в избранное
    private PopupWindow popup;//окно для смены языка в тулбаре
    private View view;//сам фрагмент
    private TextView translatedText;//переведенный текст
    private EditText translatingText;//поле для ввода перевода
    private Button fromLang;//с какого языка переводится
    private Button toLang;//на какой язык переводится
    private DataBaseHelper dbHelper;//объект бд
    private String writtenWord;//введеное пользователем слово
    private MyTimerTask mMyTimerTask;//таск для таймера
    private Timer mTimer;//таймер, для отсчета времени перед переводом
    private HashMap<String,String> languages;//все языки
    private final Long TIME_FOR_WAIT = 750l;//длина, перед переводом слова

    private refresh delegate;
    public interface refresh{
        void refreshHistory();
    }

    public void setOnRefreshListener(refresh delegate){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_translator,container,false);
        //добавление языков
        languages = LangPairs.pairsNames;
        //инициализация бд
        dbHelper = new DataBaseHelper(getActivity(), null);

        //инициализация тулбара с языками
        initBar();
        //инициализация переводчика
        init();
        //инициализация кнопок: для голосового ввода, для удаления текста, для добавления в избранное
        initButtons();
        return view;
    }

    private void initButtons() {
        //кнопка для очистки translatingText
        view.findViewById(R.id.deleteText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatingText.setText("");
                translatedText.setText("");
            }
        });
        //кнопка для вызова намерения голосового ввода
        view.findViewById(R.id.searchView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                startActivityForResult(intent, REQUEST_VOICE);
            }
        });
        //добавление переведенного текста в избранное
        view.findViewById(R.id.elect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!translatedText.getText().equals("")) {
                    Cursor cursor = dbHelper.fetchHistory(false);
                    cursor.moveToLast();
                    dbHelper.update(
                            cursor.getString(cursor.getColumnIndexOrThrow(DataEntry._ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.COLUMN_IS_ELECTED)));
                    Toast.makeText(getActivity(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                    if (!isElected) {
                        isElected = true;
                        ((ImageView) view).setColorFilter(getResources().getColor(R.color.yandexColor));
                    } else {
                        Toast.makeText(getActivity(), "Удалено из избранных", Toast.LENGTH_SHORT).show();
                        isElected = false;
                        ((ImageView) view).setColorFilter(null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Сначало введите слово", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBar() {
        fromLang = (Button)view.findViewById(R.id.fromLang);
        toLang = (Button)view.findViewById(R.id.toLang);

        fromLang.setText(languages.get("ru"));
        toLang.setText(languages.get("en"));

        //открытие окна с предложенными языками для перевода
        View.OnClickListener changeLang = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!popup.isShowing())
                    showSpinnerList(view);
            }
        };

        fromLang.setOnClickListener(changeLang);
        toLang.setOnClickListener(changeLang);

        //кнопка для смена языка, последующей анимацией и переводом
        view.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstLang = fromLang.getText().toString();
                final String secondLang = toLang.getText().toString();
                TranslateAnimation animation = new TranslateAnimation(0, v.getWidth()+fromLang.getWidth(), 0, 0);
                animation.setDuration(50);
                animation.setFillAfter(false);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fromLang.setText(secondLang);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                TranslateAnimation animation1 = new TranslateAnimation(0, -v.getWidth()-toLang.getWidth(), 0, 0);
                animation1.setDuration(50);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        toLang.setText(firstLang);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animation1.setFillAfter(false);

                fromLang.startAnimation(animation);
                toLang.startAnimation(animation1);
                //если было введено какое-либо слово - то оно меняется местами с переводом
                if(!translatingText.getText().toString().equals("")) {
                    translatingText.setText(translatedText.getText().toString());
                }
            }
        });

    }

    private void init() {

        //инициализация окна для смены языка в тулбаре
        popup = new PopupWindow(getActivity());

        translatedText = (TextView)view.findViewById(R.id.textView);
        translatingText = (EditText) view.findViewById(R.id.editText);
        //слушатель для старта отсчета времени для перевода, это сделано для того, чтобы каждая буква не переводилась.
        //дает время на ввод пользователю слова для перевода
        translatingText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                writtenWord = charSequence.toString();

                //проверка, если уже был стартован таймер
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mTimer = new Timer();
                mMyTimerTask = new MyTimerTask();
                //запуск таймертаска
                mTimer.schedule(mMyTimerTask, TIME_FOR_WAIT);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //очистка поля для translatedText
                    translatedText.setText("");
                    //переводит кнопку для добавления в избранное в дефолтное состояние
                    ((ImageView) view.findViewById(R.id.elect)).setColorFilter(null);
                    //вводит прогресс бар в активное состояние
                    view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                }
            });
            //старт асинктаска для перевода слова с помощью яндекс апи
            new TranslatedTextGetter(
                    LangPairs.getKeyFromValue(languages,fromLang.getText().toString()),
                    LangPairs.getKeyFromValue(languages,toLang.getText().toString()),
                    writtenWord)
                    .execute();
        }
    }

    private class TranslatedTextGetter extends AsyncTask<String, Void, String> {

        private final String fromLang;//с какого языка перевод
        private final String toLang;//на какой язык перевод
        private final String word;//слово для перевода

        public TranslatedTextGetter(String fromLang, String toLang, String word) {
            this.fromLang = fromLang;
            this.toLang = toLang;
            this.word = word;
        }

        @Override
        protected String doInBackground(String ... params) {
            //ссылка на яндекс апи
            String urlTranslate = getResources().getString(R.string.linkOnYandex)+getResources().getString(R.string.apiKey);
            String translatedText = null;

            try {
                String encodedText = URLEncoder.encode(word, "UTF-8");
                String requestURL = urlTranslate + "&lang=" + fromLang + "-" + toLang + "&text=" + encodedText;

                //получение ответа от яндекса
                JSONObject json = getResponseByUrl(requestURL);
                Integer status = json.getInt("code");
                if (status != 200) {
                }
                else {
                    translatedText = json.getString("text");
                    translatedText = translatedText.substring(2, translatedText.length() - 2);
                }
            } catch (Exception e) {
                Log.e("asyncTask",e.toString());
            }
            return translatedText;
        }

        @Override
        protected void onPostExecute(String result) {
            //отключение прогресс бара
            view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            if (result != null) {
                if (! word.equals("") &&  ! result.equals("")) {
                    //добавление слова в историю
                    dbHelper.insertTranslate(word,result,fromLang.toUpperCase()+" - "+toLang.toUpperCase());
                }
                //настраивание ответа
                result = result.replaceAll("\\\\n", "\\\n");
                translatedText.setText(result);
            }
            else {
                Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject getResponseByUrl(String url) {
        JSONObject json = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            in.close();
            json = new JSONObject(sb.toString());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        return json;
    }

    private void showSpinnerList(final View anchorView) {

        adapterForList = new ArrayAdapter<>(
                getActivity(),
                R.layout.text_item,
                LangPairs.getSortedArray(languages));

        final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                ((Button)anchorView).setText(adapterForList.getItem(position));
                //если было введено какое-либо слово - то оно сразу переводится
                if(!translatingText.getText().toString().equals("")) {
                    view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    new TranslatedTextGetter(
                            LangPairs.getKeyFromValue(languages, fromLang.getText().toString()),
                            LangPairs.getKeyFromValue(languages, toLang.getText().toString()),
                            writtenWord)
                            .execute();
                }
                popup.dismiss();
            }
        };

        View layout = getActivity().getLayoutInflater().inflate(R.layout.listview, null);

        final ListView listView = (ListView)layout.findViewById(R.id.langListView);
        EditText editText = (EditText)layout.findViewById(R.id.searchText);
        editText.setTextColor(getResources().getColor(android.R.color.white));
        //поиск языка в окне
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> temp = new ArrayList<String>();
                for (String s : LangPairs.getSortedArray(languages)){
                    if (s.contains(charSequence)){
                        temp.add(s);
                    }
                }
                if (temp.size()==0){
                    temp.add("Не найдено");
                    listView.setOnItemClickListener(null);
                } else {
                    listView.setOnItemClickListener(onItemClickListener);
                }
                adapterForList = new ArrayAdapter<>(
                        getActivity(),
                        R.layout.text_item,
                        temp);
                listView.setAdapter(adapterForList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(400);
        popup.setFocusable(true);

        popup.showAsDropDown(anchorView);
        listView.setAdapter(adapterForList);
        listView.setOnItemClickListener(onItemClickListener);



    }

    //ответ от интента с голосовым вводом
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    translatingText.setText(searchWrd);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
