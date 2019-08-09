package com.alberoneramos.workout.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.alberoneramos.workout.R;
import com.alberoneramos.workout.dialogs.AddExerciseDialog;
import com.alberoneramos.workout.models.Exercise;

import java.util.List;

public class ExerciseListAddAdapter extends RecyclerSwipeAdapter<ExerciseListAddAdapter.SimpleViewHolder> {

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDescription;
        TextView targetMuscle;
        SwipeLayout swipeLayout;
        ImageButton buttonDelete;

        SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            buttonDelete = itemView.findViewById(R.id.delete_button);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
            targetMuscle = itemView.findViewById(R.id.targetMuscle);
        }

    }
    private final List<Exercise> items;
    private Context mContext;

    public ExerciseListAddAdapter(Context context, List<Exercise> items) {
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
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete_button));
            }
        });
        holder.buttonDelete.setOnClickListener(view -> {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        });
        holder.exerciseName.setText(item.getExerciseName());
        holder.exerciseDescription.setText(item.getSets() + " sets x " + item.getRepetitions() + " reps x " + item.getWeight() + " kg");
        holder.targetMuscle.setText(item.getStringTargetMuscle());
        holder.swipeLayout.getSurfaceView().setOnLongClickListener(pos -> openEditDialog(items.get(position),position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean openEditDialog(Exercise exercise, int position){
        AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("EDIT_MODE",true);
        bundle.putParcelable("EXERCISE",exercise);
        bundle.putInt("POSITION",position);
        addExerciseDialog.setArguments(bundle);
        addExerciseDialog.show(((Activity)mContext).getFragmentManager(),"AddExercise");
        return true;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise,parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public List<Exercise> getItems(){
        return this.items;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}