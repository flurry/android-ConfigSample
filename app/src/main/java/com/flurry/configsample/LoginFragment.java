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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;

public class LoginFragment extends Fragment {

    // Flurry Config instance
    private FlurryConfig mFlurryConfig;
    private FlurryConfigListener mFlurryConfigListener;

    // Login text input layout & submit button
    private TextInputLayout loginId;
    private TextInputLayout loginPassword;
    private Button loginSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.login, container, false);
        loginId = (TextInputLayout) rootView.findViewById(R.id.login_id_layout);
        loginPassword = (TextInputLayout) rootView.findViewById(R.id.login_password_layout);
        loginSubmit = (Button) rootView.findViewById(R.id.login_submit);

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
                FlurryAgent.logEvent("Config Update Login");
                loginId.setHint(mFlurryConfig.getString("login_id",
                        getResources().getString(R.string.login_id)));

                loginPassword.setHint(mFlurryConfig.getString("login_password",
                        getResources().getString(R.string.login_password)));

                loginSubmit.setText(mFlurryConfig.getString("login_submit",
                        getResources().getString(R.string.login_submit)));
            }
        };
        mFlurryConfig.registerListener(mFlurryConfigListener);

        // Submit button
        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).nextPage();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFlurryConfig.unregisterListener(mFlurryConfigListener);
    }

}