package com.example.appointmaps;

import java.security.PublicKey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TitleFragment extends DialogFragment {
	public String titleString;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View textEntryView = inflater.inflate(R.layout.edit_title, null);
		final EditText editTitleEditText = (EditText) textEntryView
				.findViewById(R.id.edit_title1);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(textEntryView)
				// Add action buttons
				.setTitle("What is your event?")
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// sign in the user ...
								titleString = editTitleEditText.getText()
										.toString();
								
							}
						});
		return builder.create();
	}

	public String getTitleString() {
		return titleString;
	}
}
