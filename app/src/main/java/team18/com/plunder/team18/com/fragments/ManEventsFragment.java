package team18.com.plunder.team18.com.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team18.com.plunder.R;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class ManEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_events, container, false);
        return rootView;
    }

}
