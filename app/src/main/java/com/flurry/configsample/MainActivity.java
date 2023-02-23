/*
 * Copyright 2018, Oath Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flurry.configsample;

import android.os.Bundle;
import android.view.View;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    ViewPager2 pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Need to call setTheme before setContentView; hence use the cached Config value here.
        mFlurryConfig = FlurryConfig.getInstance();
        if ("AppThemeActionBar".equals(mFlurryConfig.getString("ui_theme",
                getResources().getString(R.string.ui_theme)))) {
            setTheme(R.style.AppThemeActionBar);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.pager_main);

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager2) findViewById(R.id.pager);
        final SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(this);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.pager_tab);
        new TabLayoutMediator(tabLayout, pager,
                (tab, position) -> tab.setText(pagerAdapter.fragments.get(position).first)
        ).attach();

        // Setup Flurry Config
        mFlurryConfigListener = new FlurryConfigListener() {
            @Override
            public void onFetchSuccess() {
                mFlurryConfig.activateConfig();
            }

            @Override
            public void onFetchNoChange() {
                // Use the Config cached data if available
            }

            @Override
            public void onFetchError(boolean isRetrying) {
                // Use the Config cached data if available
            }

            @Override
            public void onActivateComplete(boolean isCache) {
                FlurryAgent.logEvent("Config Update Pager");
                if (mFlurryConfig.getBoolean("pager_tab",
                        getResources().getBoolean(R.bool.pager_tab))) {
                    findViewById(R.id.pager_tab).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.pager_tab).setVisibility(View.GONE);
                }

                pagerAdapter.updateViews(mFlurryConfig.getString("pager_views",
                        getResources().getString(R.string.pager_views)));
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mFlurryConfig.unregisterListener(mFlurryConfigListener);
    }

    public void nextPage() {
        pager.setCurrentItem(pager.getCurrentItem() + 1);
    }

    private static final Map<String, Pair<Integer, Class<? extends Fragment>>> fragmentsMap = new HashMap<>();
    static {
        // Initialize available fragments
        fragmentsMap.put("Welcome",
                new Pair<Integer, Class<? extends Fragment>>(R.string.welcome_title,  WelcomeFragment.class));
        fragmentsMap.put("Account",
                new Pair<Integer, Class<? extends Fragment>>(R.string.login_title,    LoginFragment.class));
        fragmentsMap.put("Credit",
                new Pair<Integer, Class<? extends Fragment>>(R.string.credit_title,   CreditFragment.class));
        fragmentsMap.put("Contents",
                new Pair<Integer, Class<? extends Fragment>>(R.string.contents_title, ContentsFragment.class));
    }

    private class SamplePagerAdapter extends FragmentStateAdapter {
        private final List<Pair<Integer, Class<? extends Fragment>>> fragments = new ArrayList<>();

        public SamplePagerAdapter(FragmentActivity fa) {
            super(fa);

            findViewById(R.id.pager_tab).setVisibility(View.GONE);
            updateViews(getResources().getString(R.string.pager_views));
        }

        public void updateViews(String pager_views) {
            fragments.clear();

            String[] views = pager_views.split(",");
            for (String view : views) {
                if (fragmentsMap.containsKey(view)) {
                    fragments.add(fragmentsMap.get(view));
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment createFragment(int position) {
            try {
                return fragments.get(position).second.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

    }

}
