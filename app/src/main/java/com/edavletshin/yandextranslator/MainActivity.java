package com.edavletshin.yandextranslator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.edavletshin.yandextranslator.Fragments.HistoryFragment;
import com.edavletshin.yandextranslator.Fragments.TranslatorFragment;
import com.edavletshin.yandextranslator.adapters.MainTabsAdapter;
import com.edavletshin.yandextranslator.adapters.TabsAdapter;

public class MainActivity extends AppCompatActivity {

    //главная xml разметка
    private final int LAYOUT = R.layout.main_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initTabLayoutView();

    }

    private void initTabLayoutView() {
        ViewPager layout = (ViewPager) findViewById(R.id.main_screen);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.maintabs);
        //кастомный адаптер для табов
        final MainTabsAdapter adapter = new MainTabsAdapter(getSupportFragmentManager());
        layout.setAdapter(adapter);
        tabLayout.setupWithViewPager(layout);
        //иконки для tablayout
        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.mipmap.ic_translate));
        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.mipmap.ic_bookmark));
    }


}
