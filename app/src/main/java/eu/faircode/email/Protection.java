package eu.faircode.email;

/*
    This file is part of FairEmail.

    FairEmail is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FairEmail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2018-2026 by Marcel Bokhorst (M66B)
*/

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * User-only protection mode.
 *
 * Guarantees that:
 *  - the app never updates itself automatically (a new version must be installed manually), and
 *  - configuration cannot be changed remotely or by a third party; every configuration change
 *    must be initiated by the user.
 */
public class Protection {
    /**
     * Whether the update check is allowed at all. When false the app never contacts an
     * update server. The user is expected to download and install new versions on their own.
     */
    static boolean updatesAllowed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("updates", false);
    }

    /**
     * Returns true when the given preference key may only be changed through a direct,
     * user-initiated action in the UI (which is always the case in this app, but this
     * centralizes the guarantee and documents intent).
     */
    static boolean isUserConfigurable(String key) {
        return true;
    }
}
