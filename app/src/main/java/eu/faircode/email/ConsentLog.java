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
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Day-based, append-only log of third-party data-sharing consent requests.
 *
 * A log file is only created when a consent prompt is actually shown to the user.
 * Logs are stored as local files, one per day: consent-yyyyMMdd.log in the app files dir.
 */
public class ConsentLog {
    private static final String PREFIX = "consent-";
    private static final String SUFFIX = ".log";
    private static final String TAG = "ConsentLog";

    static File getFile(Context context) {
        return getFile(context, Calendar.getInstance().getTime());
    }

    static File getFile(Context context, java.util.Date date) {
        String day = new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(date);
        return new File(context.getFilesDir(), PREFIX + day + SUFFIX);
    }

    static List<File> getFiles(Context context) {
        List<File> files = new ArrayList<>();
        File[] list = context.getFilesDir().listFiles();
        if (list != null)
            for (File file : list)
                if (file.getName().startsWith(PREFIX) && file.getName().endsWith(SUFFIX))
                    files.add(file);
        Collections.sort(files, (a, b) -> b.getName().compareTo(a.getName()));
        return files;
    }

    static void append(Context context, String recipient, String data, boolean allowed) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                .format(Calendar.getInstance().getTime());
        // Keep the data description single-line so one entry == one line
        String safeData = (data == null ? "" : data.replace("\r", " ").replace("\n", " "));
        String line = timestamp + "\t" +
                (allowed ? "ALLOW" : "DENY") + "\t" +
                (recipient == null ? "" : recipient) + "\t" +
                safeData + "\n";

        File file = getFile(context);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            bw.write(line);
        } catch (IOException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    static String read(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append('\n');
        }
        return sb.toString();
    }

    static void deleteAll(Context context) {
        for (File file : getFiles(context))
            if (!file.delete())
                Log.w(TAG, "Delete failed " + file);
    }
}
