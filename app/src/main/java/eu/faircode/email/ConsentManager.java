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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Central gate for every outbound transfer of personal data to a third party.
 *
 * Rules:
 *  - The master "deny all" switch (default ON) blocks every third-party transfer outright.
 *  - Otherwise consent is asked for each transfer by default; there is no permanently
 *    remembered consent unless the user explicitly chooses one.
 *  - For a given recipient the user may choose "allow all" or "deny all" for 5 minutes,
 *    which is remembered in memory only and automatically expires.
 */
public class ConsentManager {
    private static final String TAG = "Consent";
    private static final int CONSENT_TIMEOUT = 5 * 60; // seconds to answer a prompt
    static final long BATCH_WINDOW = 5 * 60 * 1000L; // 5 minutes

    // In-memory only, cleared on process death. Deliberately never persisted.
    private static volatile ActivityBase currentActivity = null;
    private static final Map<String, BatchState> batches = new HashMap<>();

    static void register(ActivityBase activity) {
        currentActivity = activity;
    }

    static void unregister(ActivityBase activity) {
        if (currentActivity == activity)
            currentActivity = null;
    }

    /**
     * The master switch. When enabled (default), no data is ever sent to a third party.
     */
    static boolean isDenyAll(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("deny_thirdparty", true);
    }

    @NonNull
    static ConsentRequest buildRequest(Context context, String recipient, List<String> dataItems) {
        ConsentRequest request = new ConsentRequest();
        request.recipient = recipient;
        request.items = (dataItems == null ? Collections.emptyList() : dataItems);
        return request;
    }

    /**
     * Convenience gate for callers that have a recipient and a single description.
     * Returns false when sharing is not explicitly allowed (including the master switch).
     */
    static boolean check(Context context, String recipient, String dataItem) {
        return requestConsent(context, buildRequest(context, recipient,
                Collections.singletonList(dataItem)));
    }

    /**
     * Ask the user, on the UI thread, whether the pending transfer may proceed.
     *
     * This method may be called from a background thread; it blocks until the user
     * has answered. It always returns {@code false} if no UI is available.
     */
    static boolean requestConsent(Context context, ConsentRequest request) {
        // Master switch: deny all third-party data requests outright
        if (isDenyAll(context)) {
            Log.i(TAG, "Denied by master switch: " + request);
            return false;
        }

        // A previously chosen batch decision for this recipient is still valid?
        BatchState batch = getBatch(request.recipient);
        if (batch != null) {
            Log.i(TAG, "Batch " + (batch.allow ? "allow" : "deny") + " for " + request.recipient);
            ConsentLog.append(context, request.recipient,
                    request.describe() + " (batch)", batch.allow);
            return batch.allow;
        }

        // No UI to ask => refuse (fail closed)
        ActivityBase activity = currentActivity;
        if (activity == null) {
            Log.w(TAG, "No activity to request consent; denying " + request);
            ConsentLog.append(context, request.recipient, request.describe(), false);
            return false;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean allowed = new AtomicBoolean(false);
        final int[] scope = new int[]{FragmentDialogConsent.SCOPE_ONCE};

        activity.runOnUiThread(() -> {
            try {
                FragmentDialogConsent fragment = FragmentDialogConsent.newInstance(request);
                fragment.setListener((answer, selectedScope) -> {
                    allowed.set(answer);
                    scope[0] = selectedScope;
                    latch.countDown();
                });
                fragment.show(activity.getSupportFragmentManager(), "consent");
            } catch (Throwable ex) {
                Log.e(TAG, ex.toString());
                latch.countDown();
            }
        });

        try {
            // Do not block a background thread forever if the dialog is never answered
            if (!latch.await(CONSENT_TIMEOUT, TimeUnit.SECONDS)) {
                Log.w(TAG, "Consent timed out for " + request);
                allowed.set(false);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            allowed.set(false);
        }

        // Remember a batch choice (per recipient, in memory, expires automatically)
        if (scope[0] != FragmentDialogConsent.SCOPE_ONCE) {
            BatchState state = new BatchState();
            state.allow = allowed.get();
            state.until = System.currentTimeMillis() + BATCH_WINDOW;
            synchronized (batches) {
                batches.put(request.recipient, state);
            }
        }

        // Log the decision (this is what creates the day file, only on a real prompt)
        ConsentLog.append(context, request.recipient, request.describe(), allowed.get());

        return allowed.get();
    }

    private static BatchState getBatch(String recipient) {
        if (recipient == null)
            return null;
        synchronized (batches) {
            BatchState state = batches.get(recipient);
            if (state == null)
                return null;
            if (state.until < System.currentTimeMillis()) {
                batches.remove(recipient);
                return null;
            }
            return state;
        }
    }

    private static class BatchState {
        boolean allow;
        long until;
    }

    /**
     * Description of a single pending third-party transfer.
     */
    static class ConsentRequest {
        String recipient;
        List<String> items;

        String describe() {
            StringBuilder sb = new StringBuilder();
            if (items != null)
                for (String item : items) {
                    if (sb.length() > 0)
                        sb.append(", ");
                    sb.append(item);
                }
            return sb.toString();
        }

        @NonNull
        @Override
        public String toString() {
            return recipient + " [" + describe() + "]";
        }
    }
}
