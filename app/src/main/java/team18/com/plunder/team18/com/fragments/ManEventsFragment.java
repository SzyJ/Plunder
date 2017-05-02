package team18.com.plunder.team18.com.fragments;

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
import android.widget.EditText;

import team18.com.plunder.CreateEventActivity;
import team18.com.plunder.R;
import team18.com.plunder.utils.Event;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class ManEventsFragment extends android.support.v4.app.Fragment implements MainActivityFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_events, container, false);
        getActivity().setTitle(getString(R.string.man_events_fragment_title));

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater layInf = LayoutInflater.from(getActivity());

                final View dialogView = layInf.inflate(R.layout.dialog_create_event, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create New Plunder");
                builder.setMessage("Please give your new plunder a name and proceed to make it it");
                builder.setView(dialogView);

                final EditText eventNameInput = (EditText) dialogView.findViewById(R.id.event_name_input);

                builder.setPositiveButton("Create Plunder",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String eventName = eventNameInput.getText().toString();
                                if (!eventName.isEmpty()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                                    intent.putExtra("new_event_obj", new Event(eventName));
                                    startActivity(intent);
                                } else {
                                    eventNameInput.setError("Event name cannot be empty!");
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
