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

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryConfig;
import com.flurry.android.FlurryConfigListener;

public class MainApplication extends Application {
    public static final String FLURRY_APIKEY = "QYX7NC49H7PD97YV64WJ";

    @Override
    public void onCreate() {
        super.onCreate();

        // Init Flurry
        new FlurryAgent.Builder()
                .withLogLevel(Log.VERBOSE)
                .withLogEnabled(true)
                .build(this, FLURRY_APIKEY);

        // Setup Flurry Config and initiate fetch
        final FlurryConfig flurryConfig = FlurryConfig.getInstance();
        flurryConfig.registerListener(new FlurryConfigListener() {
            @Override
            public void onFetchSuccess() {
                FlurryAgent.logEvent("Config Fetched");
                Toast.makeText(MainApplication.this, "Fetch - Success", Toast.LENGTH_SHORT).show();

                flurryConfig.activateConfig();
            }

            @Override
            public void onFetchNoChange() {
                FlurryAgent.logEvent("Config Fetch - No Change");
                Toast.makeText(MainApplication.this, "Fetch - No Change", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(boolean isRetrying) {
                FlurryAgent.logEvent("Config Fetch - Failed");
                Toast.makeText(MainApplication.this, "Fetch - Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActivateComplete(boolean isCache) {
                String message = "Config Activated: " + (isCache ? "Cache" : "Fetch");
                FlurryAgent.logEvent(message);
                Toast.makeText(MainApplication.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        flurryConfig.fetchConfig();
    }

}
