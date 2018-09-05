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

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;

public class WelcomeFragment extends Fragment {

    // Flurry Config instance
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    // Welcome message text view
    private TextView welcomeMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.welcome, container, false);
        welcomeMessage = (TextView) rootView.findViewById(R.id.welcome);

        // Setup Flurry Config
        mFlurryConfig = FlurryConfig.getInstance();
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
                FlurryAgent.logEvent("Config Update Welcome");
                String text = mFlurryConfig.getString("welcome_message",
                        getResources().getString(R.string.welcome_message));
                welcomeMessage.setText(text.replaceAll("\\\\n", "\n"));

                int fontSize = mFlurryConfig.getInt("welcome_font_size",
                        getResources().getInteger(R.integer.welcome_font_size));
                welcomeMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

                String fontColor = mFlurryConfig.getString("welcome_font_color",
                        getResources().getString(R.string.welcome_font_color));
                welcomeMessage.setTextColor(Color.parseColor(fontColor));
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);

        // Reset Config Data button
        ((Button) rootView.findViewById(R.id.reset_config)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlurryConfig.resetState();

                getActivity().recreate();
            }
        });

        // App Version
        try {
            ((TextView) rootView.findViewById(R.id.version)).setText("version: " +
                    getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFlurryConfig.unregisterListener(mFlurryConfigListener);
    }

}