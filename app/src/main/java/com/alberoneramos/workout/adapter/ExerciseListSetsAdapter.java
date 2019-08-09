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

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.alberoneramos.workout.R;
import com.alberoneramos.workout.models.Exercise;

import java.util.ArrayList;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class ExerciseListSetsAdapter extends RecyclerSwipeAdapter<ExerciseListSetsAdapter.SimpleViewHolder>implements
        StickyHeaderAdapter<ExerciseListSetsAdapter.HeaderHolder> {

    private LayoutInflater inflater;
    private List<String> headerText;
    private List<Integer> headerPosition;

    void setData(List<Exercise> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDescription;

        SimpleViewHolder(View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView exerciseName;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            exerciseName = (TextView) itemView;
        }
    }
    private final List<Exercise> items;

    public ExerciseListSetsAdapter(Context context, List<Exercise> items) {
        inflater = LayoutInflater.from(context);
        this.headerPosition = new ArrayList<>();
        this.headerText = new ArrayList<>();
        this.items = setupSingleSets(items);
    }

    private List<Exercise> setupSingleSets(List<Exercise> exercises){
        List<Exercise> singleSets = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        int totalSets = 0;
        for(Exercise exercise : exercises){
            for(int i = 0 ; i < exercise.getSets() ; i++ ) {
                Exercise set = new Exercise(exercise);
                headerPosition.add(totalSets);
                headerText.add(exercise.getExerciseName());
                set.setExerciseName("Set #"+ (i + 1));
                singleSets.add(set);

            }
            totalSets+=exercise.getSets();
        }
        return singleSets;
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
        holder.exerciseDescription.setText( item.getRepetitions() +" reps x " + item.getWeight() + " kg");
    }

    @Override
    public long getHeaderId(int position) {
        if(position == 0)
            return 0;
        else
            return headerPosition.get(position);
    }

    @NonNull
    @Override
    public HeaderHolder onCreateHeaderViewHolder(@NonNull ViewGroup parent) {
        final View view = inflater.inflate(R.layout.header_recyclerview_workout, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(@NonNull HeaderHolder viewHolder, int position) {
            viewHolder.header.setText(headerText.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView header;

        HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView;
        }
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_repetition,parent, false);
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