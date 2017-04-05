package team18.com.plunder.team18.com.fragments;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import team18.com.plunder.CreateHunt;
import team18.com.plunder.R;

import static com.google.android.gms.location.LocationRequest.create;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class ManHuntFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_manage_hunts, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater layInf = LayoutInflater.from(getActivity());

                final View dialogView = layInf.inflate(R.layout.dialog_create_hunt, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create New Hunt");
                builder.setMessage("Some Text describing what a hunt is. Please give your new hunt a name and proceed to choose waypoints for it");
                builder.setView(dialogView);

                final EditText huntNameInput = (EditText) dialogView.findViewById(R.id.hunt_name_input);

                builder.setPositiveButton("Choose Hunt Waypoints!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String huntName = huntNameInput.getText().toString();
                                if (!huntName.isEmpty()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), CreateHunt.class);
                                    intent.putExtra("hunt_name", huntName);
                                    startActivity(intent);
                                } else {
                                    huntNameInput.setError("Hunt name cannot be empty!");
                                }
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        );



                builder.show();
            }
        });

        return rootView;
    }

}
