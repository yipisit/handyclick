package com.firstapp.handyclickapp_v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialogWeb extends AppCompatDialogFragment {
    private EditText editTextwebsite;
    private ExampleDialogWebListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_web, null);

        builder.setView(view).setTitle("Please enter a web address").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String webURL = editTextwebsite.getText().toString();
                        listener.applyTexts(webURL);


                    }
                });
        editTextwebsite = view.findViewById(R.id.edit_website);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogWebListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogWebListener{
        void applyTexts(String webURL);

        void applyTexts(String phonenumber, String webURL);
    }
}