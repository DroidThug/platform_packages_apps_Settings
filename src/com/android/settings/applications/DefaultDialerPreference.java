/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.android.settings.AppListPreference;

import java.util.List;
import java.util.Objects;

public class DefaultDialerPreference extends AppListPreference {

    public DefaultDialerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isAvailable(context)) {
            loadDialerApps();
        }
    }

    @Override
    protected boolean persistString(String value) {
        if (!TextUtils.isEmpty(value) && !Objects.equals(value, getDefaultPackage())) {
            DefaultDialerManager.setDefaultDialerApplication(getContext(), value);
        }
        setSummary(getEntry());
        return true;
    }

    private void loadDialerApps() {
        List<ComponentName> dialerComponents =
                DefaultDialerManager.getInstalledDialerApplications(getContext());

        final String[] dialers = new String[dialerComponents.size()];
        for (int i = 0; i < dialerComponents.size(); i++) {
            dialers[i] = dialerComponents.get(i).getPackageName();
        }
        setPackageNames(dialers, getDefaultPackage());
    }

    private String getDefaultPackage() {
        ComponentName appName = DefaultDialerManager.getDefaultDialerApplication(getContext());
        if (appName != null) {
            return appName.getPackageName();
        }
        return null;
    }

    public static boolean isAvailable(Context context) {
        final TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (!tm.isVoiceCapable()) {
            return false;
        }

        final UserManager um =
                (UserManager) context.getSystemService(Context.USER_SERVICE);
        return !um.hasUserRestriction(UserManager.DISALLOW_OUTGOING_CALLS);
    }
}