package com.alberoneramos.workout.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapter.ExerciseListAdapter;
import com.alberoneramos.workout.adapter.ExerciseListSetsAdapter;
import com.alberoneramos.workout.controller.WorkoutPlanController;
import com.alberoneramos.workout.models.Exercise;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.alberoneramos.workout.utils.UrlBuilder;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.yashovardhan99.timeit.Stopwatch;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import cn.iwgang.countdownview.CountdownView;


public class WorkoutPlanFragment extends Fragment {

    private static final int EDIT_CODE = 31;


    @BindView(R.id.exerciseList)
    RecyclerView exerciseList;
    @BindView(R.id.exerciseListRepetition)
    RecyclerView exerciseListRepetition;
    @BindView(R.id.workoutTitle)
    TextView title;
    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.back)
    ImageButton backButton;
    @BindView(R.id.more)
    ImageButton moreButton;
    @BindView(R.id.toggleWorkout)
    ToggleButton workoutToggleButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.color_ellipse)
    ImageView colorEllipse;

    private Uri shortLink;
    private WorkoutPlan workoutPlan;
    private WorkoutPlanController wPController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            workoutPlan = bundle.getParcelable("WORKOUT_PLAN");
        }
        buildUrl();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workoutplan_list, container, false);
        ButterKnife.bind(this, view);
        recyclerViewSetup();
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.setTextView(timer);
        moreButton.setOnClickListener(this::showPopup);
        backButton.setOnClickListener(l -> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack());
        workoutToggleButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                toggleToolbarColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(android.R.color.white),
                        getResources().getColor(android.R.color.white));
                exerciseListRepetition.setVisibility(View.VISIBLE);
                exerciseListRepetition.invalidate();
                timer.setTextColor(getResources().getColor(R.color.colorPrimary));
                timer.setTranslationZ(30);
                timer.bringToFront();
                if(stopwatch.getElapsedTime()>0){
                    stopwatch.resume();
                } else {
                    stopwatch.start();
                }
                exerciseList.setVisibility(View.GONE);
                exerciseList.invalidate();
            } else {
                stopwatch.pause();
                timer.setTextColor(getResources().getColor(R.color.white));
//                toggleToolbarColors(getResources().getColor(android.R.color.white), getResources().getColor(R.color.colorPrimary),
//                        getResources().getColor(android.R.color.black));
//                exerciseListRepetition.setVisibility(View.GONE);
//                exerciseList.setVisibility(View.VISIBLE);
//                exerciseListRepetition.invalidate();
//                exerciseList.invalidate();
            }
        }));
        title.setText(workoutPlan.getName());
        colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK) {
            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts/" + workoutPlan.getWorkoutPlanId());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workoutPlan = dataSnapshot.getValue(WorkoutPlan.class);
                    recyclerViewSetup();
                    colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
                    title.setText(workoutPlan.getName());
                    Log.d("Montanha", String.valueOf(workoutPlan.getName()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ReadError::", "The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    private void recyclerViewSetup() {
        List<Exercise> exercises = workoutPlan.getExercises();
        exerciseList.setAdapter(new ExerciseListAdapter(getContext(), exercises));
        ExerciseListSetsAdapter adapter = new ExerciseListSetsAdapter(getContext(), exercises);
        exerciseListRepetition.setAdapter(adapter);
        exerciseListRepetition.setLayoutManager(new LinearLayoutManager(getContext()));
        StickyHeaderDecoration decor = new StickyHeaderDecoration(adapter);
        exerciseListRepetition.addItemDecoration(decor);
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions_workoutplan, popup.getMenu());
        wPController = new WorkoutPlanController(getActivity(), workoutPlan);
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.share:
                    wPController.shareDialogBuilder(shortLink.toString());
                    return true;
                case R.id.delete:
                    wPController.deleteDialogBuilder();
                    return true;
                case R.id.edit:
                    wPController.showEditActivity(EDIT_CODE);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void buildUrl() {
        UrlBuilder builder = new UrlBuilder();
        Uri longUri = builder.buildWorkoutUrl(this.workoutPlan.getWorkoutPlanId());
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(longUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        shortLink = Objects.requireNonNull(task.getResult()).getShortLink();

                });
    }

    private void toggleToolbarColors(int toolBarColor, int imageButtonColor, int titleColor) {
        this.toolbar.setBackgroundColor(toolBarColor);
        this.backButton.setColorFilter(imageButtonColor);
        this.title.setTextColor(titleColor);
        this.moreButton.setColorFilter(imageButtonColor);
    }

}
