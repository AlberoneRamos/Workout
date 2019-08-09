package com.alberoneramos.workout.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.alberoneramos.workout.view.fragment.WorkoutPlanFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WorkoutPlanAdapter extends RecyclerSwipeAdapter<WorkoutPlanAdapter.SimpleViewHolder> {

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName;
        TextView numberOfExercises;
        SwipeLayout swipeLayout;
        ImageView colorEllipse;
        ImageButton buttonDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            colorEllipse = itemView.findViewById(R.id.color_ellipse);
            workoutName = itemView.findViewById(R.id.workoutName);
            numberOfExercises = itemView.findViewById(R.id.numberOfExercises);
            buttonDelete = itemView.findViewById(R.id.delete_button);
        }

    }

    private List<WorkoutPlan> items;
    private Context mContext;

    public WorkoutPlanAdapter(Context context, List<WorkoutPlan> items) {
        this.mContext = context;
        this.items = items;
    }

    private void removeRow(final View row, final int position) {
        final int initialHeight = row.getHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int newHeight = (int) (initialHeight * (1 - interpolatedTime));
                if (newHeight > 0) {
                    row.getLayoutParams().height = newHeight;
                    row.requestLayout();
                }
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                row.getLayoutParams().height = initialHeight;
                row.requestLayout();
                items.remove(position);
                notifyDataSetChanged();
            }
        });
        animation.setDuration(300);
        row.startAnimation(animation);
    }


    public void updateList(List<WorkoutPlan> list) {
        this.items = list;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        WorkoutPlan item = items.get(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete_button));
            }
        });
        holder.buttonDelete.setOnClickListener(view -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/workouts").child(item.getWorkoutPlanId()).removeValue();
        });
        String hexColor = String.format("#%06X", (0xFFFFFF & item.getColorId()));
        holder.colorEllipse.setColorFilter(Color.parseColor(hexColor));
        holder.swipeLayout.getSurfaceView().setOnClickListener(pos -> openDetailActivity(items.get(position)));
        holder.workoutName.setText(item.getName());
        holder.numberOfExercises.setText(String.valueOf(String.valueOf(item.getExercises().size())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openDetailActivity(WorkoutPlan workoutPlan) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        bundle.putParcelable("WORKOUT_PLAN", workoutPlan);
        Fragment masterDetailFragment = new WorkoutPlanFragment();
        masterDetailFragment.setArguments(bundle);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
        mFragmentTransaction.replace(R.id.fragment_container, masterDetailFragment).commit();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}