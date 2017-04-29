package team18.com.plunder.team18.com.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import team18.com.plunder.CreateHunt;
import team18.com.plunder.R;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import team18.com.plunder.utils.Hunt;
import team18.com.plunder.utils.HuntsAdapter;

public class manageHunts extends Fragment {

    private RecyclerView recyclerView;
    private HuntsAdapter adapter;
    private List<Hunt> huntList;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_manage_hunts, container, false);
        getActivity().setTitle(getString(R.string.man_hunts_fragment_title));

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
                                    intent.putExtra("new_hunt_obj", new Hunt(huntName));
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_manage_hunts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        huntList = new ArrayList<>();
        adapter = new HuntsAdapter(this, huntList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareHunts();

        try {
            Glide.with(this).load(R.drawable.compass_cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few hunts for testing
     */
    private void prepareHunts() {

        int image = R.drawable.compass_redesign;

        /*Hunt a = new Hunt("testHunt", "description", image);
        huntList.add(a);

        a = new Hunt("testHunt1", "description1", image);
        huntList.add(a);

        a = new Hunt("testHunt2", "description2", image);
        huntList.add(a);

        a = new Hunt("testHunt3", "description3", image);
        huntList.add(a);

        a = new Hunt("testHunt4", "description4", image);
        huntList.add(a);

        a = new Hunt("testHunt5", "description5", image);
        huntList.add(a);

        a = new Hunt("testHunt6", "description6", image);
        huntList.add(a);

        a = new Hunt("testHunt7", "description7", image);
        huntList.add(a);

        a = new Hunt("testHunt8", "description8", image);
        huntList.add(a);

        a = new Hunt("testHunt9", "description9", image);
        huntList.add(a);*/

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
