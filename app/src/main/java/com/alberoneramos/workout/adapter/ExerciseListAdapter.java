package com.alberoneramos.workout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.alberoneramos.workout.R;
import com.alberoneramos.workout.models.Exercise;

import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.SimpleViewHolder> {

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDescription;
        TextView targetMuscle;
        SwipeLayout swipeLayout;

        SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
            targetMuscle = itemView.findViewById(R.id.targetMuscle);
        }

        public TextView getExerciseName() {
            return exerciseName;
        }

        public void setExerciseName(TextView exerciseName) {
            this.exerciseName = exerciseName;
        }

        public TextView getExerciseDescription() {
            return exerciseDescription;
        }

        public void setExerciseDescription(TextView exerciseDescription) {
            this.exerciseDescription = exerciseDescription;
        }

        public TextView getTargetMuscle() {
            return targetMuscle;
        }

        public void setTargetMuscle(TextView targetMuscle) {
            this.targetMuscle = targetMuscle;
        }

        public SwipeLayout getSwipeLayout() {
            return swipeLayout;
        }

        public void setSwipeLayout(SwipeLayout swipeLayout) {
            this.swipeLayout = swipeLayout;
        }
    }
    private final List<Exercise> items;
    private Context mContext;

    public ExerciseListAdapter(Context context,List<Exercise> items) {
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        Exercise item = items.get(position);
        holder.exerciseName.setText(item.getExerciseName());
        holder.swipeLayout.setRightSwipeEnabled(false);
        holder.exerciseDescription.setText(item.getSets() + " sets x " + item.getRepetitions() + " reps x " + item.getWeight() + " kg");
        holder.targetMuscle.setText(item.getStringTargetMuscle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise,parent, false);
        return new SimpleViewHolder(v);
    }


    public List<Exercise> getItems(){
        return this.items;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}