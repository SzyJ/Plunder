package team18.com.plunder.team18.com.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team18.com.plunder.R;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class ManHuntFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_hunts, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "\"Create Hunt\" button!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }

}
