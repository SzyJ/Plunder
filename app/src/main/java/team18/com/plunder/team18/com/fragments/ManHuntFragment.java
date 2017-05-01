package team18.com.plunder.team18.com.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import team18.com.plunder.CreateHunt;
import team18.com.plunder.R;
import team18.com.plunder.utils.CustomAdapter;
import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.HuntCardData;
import team18.com.plunder.utils.VariableBank;

import static com.google.android.gms.location.LocationRequest.create;

/**
 * Created by Szymon Jackiewicz on 2/6/2017.
 */

public class ManHuntFragment extends android.support.v4.app.Fragment implements MainActivityFragment {

    private String GET_HUNT_ARRAY_URL = "http://homepages.cs.ncl.ac.uk/2016-17/csc2022_team18/PHP/hunt_array.php";

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CustomAdapter adapter;
    private List<HuntCardData> dataList;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_manage_hunts, container, false);
        getActivity().setTitle(getString(R.string.man_hunts_fragment_title));

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        dataList = new ArrayList<>();

        loadData(VariableBank.USER_ID);

        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new CustomAdapter(getContext(), dataList);

        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LayoutInflater layInf = LayoutInflater.from(getActivity());

                final View dialogView = layInf.inflate(R.layout.dialog_create_hunt, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create New Hunt");
                builder.setMessage("Please give your new hunt a name and proceed to choose waypoints for it");
                builder.setView(dialogView);

                final EditText huntNameInput = (EditText) dialogView.findViewById(R.id.hunt_name_input);

                builder.setPositiveButton("Choose Hunt Waypoints!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String huntName = huntNameInput.getText().toString();
                                if (!huntName.isEmpty()) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), CreateHunt.class);
                                    intent.putExtra("new_hunt_obj", new Hunt(huntName, false));
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

    private void loadData(final String userId) {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                    "Fetching your hunts...", true);
            @Override
            protected Void doInBackground(Integer... params) {

                dialog.show();

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("user_id", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(GET_HUNT_ARRAY_URL)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        HuntCardData data = new HuntCardData(object.getString("hunt_id"), object.getString("hunt_name"), (long) Integer.parseInt(object.getString("date_created")));

                        dataList.add(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // End of content reached
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                adapter.notifyDataSetChanged();

                dialog.hide();
            }
        };

        task.execute();
    }

}
