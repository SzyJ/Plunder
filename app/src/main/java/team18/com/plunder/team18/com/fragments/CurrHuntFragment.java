package team18.com.plunder.team18.com.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team18.com.plunder.R;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class CurrHuntFragment extends android.support.v4.app.Fragment implements MainActivityFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_hunts, container, false);
        getActivity().setTitle(getString(R.string.current_hunts_fragment_title));
        return rootView;
    }
}
