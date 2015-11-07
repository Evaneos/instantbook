package com.evaneos.android.watchme.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.evaneos.android.watchme.R;

/**
 * Created by Guillaume 'DarzuL' Bourderye on 07/11/15.
 *
 * Dialog to display information
 */
public class InfoDialog extends DialogFragment {

    private static final String ARG_DIALOG_ID = "dialogId";
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_ACTIONS = "actions";
    private int mDialogId;

    public static InfoDialog newInstance(int dialogId, String title, String content, int... actionsRes) {
        InfoDialog fragment = new InfoDialog();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putIntArray(ARG_ACTIONS, actionsRes);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        mDialogId = getArguments().getInt(ARG_DIALOG_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        ((TextView) view.findViewById(R.id.dialog_title)).setText(args.getString(ARG_TITLE));
        ((TextView) view.findViewById(R.id.dialog_content)).setText(args.getString(ARG_CONTENT));
        setupBtn(args.getIntArray(ARG_ACTIONS), (ViewGroup) view.findViewById(R.id.dialog_btn_layout));
    }

    private void setupBtn(int[] actionsRes, ViewGroup layout) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (final int actionRes : actionsRes) {
            Button btn = (Button) inflater.inflate(R.layout.dialog_btn, null);
            btn.setText(actionRes);
            layout.addView(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Callback) getActivity()).onAction(mDialogId, actionRes);
                    dismiss();
                }
            });
        }
    }

    public interface Callback {
        void onAction(int dialogId, int actionRes);
    }
}
