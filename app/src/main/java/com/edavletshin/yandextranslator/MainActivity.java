package com.edavletshin.yandextranslator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.edavletshin.yandextranslator.Fragments.HistoryFragment;
import com.edavletshin.yandextranslator.Fragments.TranslatorFragment;
import com.edavletshin.yandextranslator.adapters.MainTabsAdapter;
import com.edavletshin.yandextranslator.adapters.TabsAdapter;

public class MainActivity extends AppCompatActivity {

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
        final MainTabsAdapter adapter = new MainTabsAdapter(getSupportFragmentManager());
        layout.setAdapter(adapter);
        tabLayout.setupWithViewPager(layout);
        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.mipmap.ic_translate));
        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.mipmap.ic_bookmark));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==1)
                adapter.refresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


}
