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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * Shown before every outbound transfer of personal data to a third party.
 *
 * Consent is asked each time by default. For the same recipient the user may
 * choose to allow or deny all transfers for the next 5 minutes.
 */
public class FragmentDialogConsent extends FragmentDialogBase {
    static final int SCOPE_ONCE = 0;
    static final int SCOPE_ALLOW_5MIN = 1;
    static final int SCOPE_DENY_5MIN = 2;

    private Listener listener;
    private boolean answered = false;

    interface Listener {
        void onConsent(boolean allowed, int scope);
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    private void answer(boolean allowed, int scope) {
        if (answered)
            return;
        answered = true;
        if (listener != null)
            listener.onConsent(allowed, scope);
    }

    static FragmentDialogConsent newInstance(ConsentManager.ConsentRequest request) {
        FragmentDialogConsent fragment = new FragmentDialogConsent();
        Bundle args = new Bundle();
        args.putString("recipient", request.recipient);
        args.putStringArray("items", request.items.toArray(new String[0]));
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String recipient = (args == null ? null : args.getString("recipient"));
        String[] items = (args == null ? null : args.getStringArray("items"));

        final Context context = getContext();

        View dview = LayoutInflater.from(context).inflate(R.layout.dialog_consent, null);
        TextView tvRecipient = dview.findViewById(R.id.tvConsentRecipient);
        TextView tvData = dview.findViewById(R.id.tvConsentData);
        CheckBox cbAllowAll = dview.findViewById(R.id.cbConsentAllowAll);
        CheckBox cbDenyAll = dview.findViewById(R.id.cbConsentDenyAll);

        tvRecipient.setText(getString(R.string.title_consent_recipient, recipient));

        StringBuilder sb = new StringBuilder();
        if (items != null)
            for (String item : items)
                sb.append("\u2022 ").append(item).append('\n');
        tvData.setText(sb.toString().trim());

        // These two options are mutually exclusive
        cbAllowAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbDenyAll.isChecked())
                    cbDenyAll.setChecked(false);
            }
        });
        cbDenyAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbAllowAll.isChecked())
                    cbAllowAll.setChecked(false);
            }
        });

        return new AlertDialog.Builder(context)
                .setIcon(R.drawable.twotone_security_24)
                .setTitle(R.string.title_consent)
                .setView(dview)
                .setPositiveButton(R.string.title_consent_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        answer(true, cbAllowAll.isChecked()
                                ? SCOPE_ALLOW_5MIN : SCOPE_ONCE);
                    }
                })
                .setNegativeButton(R.string.title_consent_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        answer(false, cbDenyAll.isChecked()
                                ? SCOPE_DENY_5MIN : SCOPE_ONCE);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        answer(false, SCOPE_ONCE);
                    }
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
