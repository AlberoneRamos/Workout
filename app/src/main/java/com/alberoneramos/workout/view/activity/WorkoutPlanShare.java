package com.alberoneramos.workout.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapter.ExerciseListAdapter;
import com.alberoneramos.workout.models.EmptyRecyclerView;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutPlanShare extends AppCompatActivity {

    @BindView(R.id.workoutTitle)
    TextView title;
    @BindView(R.id.back)
    ImageButton backButton;
    @BindView(R.id.empty)
    TextView emptyText;
    @BindView(R.id.add_workout)
    ImageButton addWorkoutButton;
    @BindView(R.id.exerciseList)
    EmptyRecyclerView exerciseList;
    WorkoutPlan workoutPlan;
    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_share);
        ButterKnife.bind(this);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnFailureListener(e -> Log.e("LinkError", e.getMessage())).addOnSuccessListener(e -> {
            Uri link = e.getLink();
            ref = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(link.getQueryParameter("USER_ID")))
                    .child("/workouts")
                    .child(Objects.requireNonNull(link.getQueryParameter("WORKOUT_ID")));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workoutPlan = dataSnapshot.getValue(WorkoutPlan.class);
                    if (workoutPlan == null) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.workout_not_found), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        title.setText(workoutPlan.getName());
                        recyclerViewSetup();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ReadError", databaseError.getMessage());
                }
            });
        });
        backButton.setOnClickListener((l) -> WorkoutPlanShareController.openMainActivity(this));
        addWorkoutButton.setOnClickListener((l) -> WorkoutPlanShareController.addWorkout(workoutPlan, this));
    }

    public void recyclerViewSetup() {
        ExerciseListAdapter adapter = new ExerciseListAdapter(this, workoutPlan.getExercises());
        exerciseList.setAdapter(adapter);
        exerciseList.setEmptyView(emptyText);
        exerciseList.setLayoutManager(new LinearLayoutManager(this));
    }

    static class WorkoutPlanShareController {
        static void openMainActivity(Activity activity) {
            Intent intent = new Intent(activity, HomescreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            activity.startActivity(intent);
        }

        static void addWorkout(WorkoutPlan workoutPlan, Activity activity) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String key = database.getReference("workouts").push().getKey();
            workoutPlan.setWorkoutPlanId(key);
            if (key != null) {
                database
                        .getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts")
                        .child(key)
                        .setValue(workoutPlan);
            }
            openMainActivity(activity);
        }
    }
}
