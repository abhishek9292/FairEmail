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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Viewable, day-based log of third-party data-sharing consent prompts.
 * A log file exists only for days on which a consent prompt was actually shown.
 */
public class ActivityConsentLog extends ActivityBase {
    private Spinner spDay;
    private TextView tvLog;

    private final List<File> files = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consent_log);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.title_consent_log);

        spDay = findViewById(R.id.spConsentDay);
        tvLog = findViewById(R.id.tvConsentLog);

        spDay.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                load(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        loadFiles();
    }

    private void loadFiles() {
        files.clear();
        files.addAll(ConsentLog.getFiles(this));

        List<String> labels = new ArrayList<>();
        SimpleDateFormat in = new SimpleDateFormat("yyyyMMdd", Locale.ROOT);
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        for (File file : files) {
            String name = file.getName().replace("consent-", "").replace(".log", "");
            try {
                labels.add(out.format(in.parse(name)));
            } catch (ParseException ex) {
                labels.add(name);
            }
        }

        if (labels.isEmpty()) {
            tvLog.setText(R.string.title_consent_log_empty);
            spDay.setEnabled(false);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adapter);
        load(0);
    }

    private void load(int position) {
        if (position < 0 || position >= files.size())
            return;
        try {
            String content = ConsentLog.read(files.get(position));
            tvLog.setText(TextUtils.isEmpty(content)
                    ? getString(R.string.title_consent_log_empty) : content);
        } catch (IOException ex) {
            tvLog.setText(Log.formatThrowable(ex, false));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.consent_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.menu_consent_delete) {
            onDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDelete() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.twotone_warning_24)
                .setTitle(R.string.title_consent_log_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConsentLog.deleteAll(ActivityConsentLog.this);
                        files.clear();
                        tvLog.setText(R.string.title_consent_log_empty);
                        spDay.setEnabled(false);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    static Intent getIntent(Context context) {
        return new Intent(context, ActivityConsentLog.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
