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

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editTextphonenr;
    private EditText editTextwebsite;
    private EditText editTextlocation;
    private ExampleDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);

        builder.setView(view).setTitle("Please enter your phone number, web address and destination").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String phonenumber = editTextphonenr.getText().toString();
                        String webURL = editTextwebsite.getText().toString();
                        String location = editTextlocation.getText().toString();
                        listener.applyTexts(phonenumber, webURL, location);



                    }
                });
        editTextphonenr = view.findViewById(R.id.edit_phonenr);
        editTextwebsite = view.findViewById(R.id.edit_website);
        editTextlocation = view.findViewById(R.id.edit_location);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener{
        void applyTexts(String phonenumber, String webURL, String location);
    }
}
