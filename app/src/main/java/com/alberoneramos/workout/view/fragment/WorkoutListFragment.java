package com.alberoneramos.workout.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapter.WorkoutPlanAdapter;
import com.alberoneramos.workout.models.EmptyRecyclerView;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WorkoutListFragment extends Fragment {

    public WorkoutListFragment() {
    }

    @BindView(R.id.list)
    EmptyRecyclerView workoutList;
    @BindView(android.R.id.empty)
    TextView emptyText;
    @BindView(R.id.editText)
    EditText recyclerFilter;
    private List<WorkoutPlan> workouts;
    private WorkoutPlanAdapter workoutPlanAdapter;
    ChildEventListener dataListener;
    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);
        ButterKnife.bind(this,view);
        recyclerFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        workouts = new ArrayList<>();
        workoutPlanAdapter = new WorkoutPlanAdapter(getContext(), workouts);
        workoutList.setAdapter(workoutPlanAdapter);
        workoutList.setEmptyView(emptyText);
        workoutList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseServer fs = new FirebaseServer();
        fs.fetchDataFromFirebase();
    }

    private void filter(String text) {
        List<WorkoutPlan> temp = new ArrayList<>();
        for (WorkoutPlan d : workouts)
            if (d.getName().toLowerCase().contains(text.toLowerCase())) temp.add(d);
        workoutPlanAdapter.updateList(temp);
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.removeEventListener(dataListener);
    }

    private class FirebaseServer {
         void fetchDataFromFirebase() {
             ref = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts");
             dataListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = 0;
                    if (previousChildKey != null)
                        index = getIndexForKey(previousChildKey, workouts) + 1;
                    workouts.add(index, snapshot.getValue(WorkoutPlan.class));
                    workoutPlanAdapter.notifyItemInserted(workouts.size());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int index = getIndexForKey(snapshot.getKey(), workouts);
                    workouts.set(index, snapshot.getValue(WorkoutPlan.class));
                    workoutPlanAdapter.notifyItemChanged(index);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    int index = getIndexForKey(snapshot.getKey(), workouts);
                    workouts.remove(index);
                    workoutPlanAdapter.notifyItemRemoved(index);
                    workoutPlanAdapter.notifyItemRangeChanged(0, workouts.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildKey) {
                    int oldIndex = getIndexForKey(snapshot.getKey(), workouts);
                    workouts.remove(oldIndex);
                    int newIndex = previousChildKey == null ? 0 : getIndexForKey(previousChildKey, workouts) + 1;
                    workouts.add(newIndex, snapshot.getValue(WorkoutPlan.class));
                    workoutPlanAdapter.notifyItemMoved(oldIndex, newIndex);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseReadError", "The read failed: " + error.getCode());
                }
            };
            ref.addChildEventListener(dataListener);
        }

        private int getIndexForKey(String key, List<WorkoutPlan> workouts) {
            int index = 0;
            for (WorkoutPlan workout : workouts) {
                if (workout.getWorkoutPlanId().equals(key)) {
                    return index;
                } else {
                    index++;
                }
            }
            throw new IllegalArgumentException("Key not found");
        }

    }

}
