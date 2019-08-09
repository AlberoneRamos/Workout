package com.alberoneramos.workout.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPlan implements Parcelable{
    private List<com.alberoneramos.workout.models.Exercise> exercises = new ArrayList<com.alberoneramos.workout.models.Exercise>();
    private List<Integer> weekdays = new ArrayList<Integer>();
    private String workoutPlanId = "";
    private String name = "";
    private int colorId = 0;

    public WorkoutPlan(String name, List<Exercise> exercises, int colorId, List<Integer> weekdays, String WorkoutPlanId) {
        this.exercises = exercises;
        this.name = name;
        this.colorId = colorId;
        this.weekdays = weekdays;
        this.workoutPlanId = WorkoutPlanId;
    }

    private WorkoutPlan(Parcel workoutPlan){
        workoutPlan.readList(this.exercises, Exercise.class.getClassLoader());
        workoutPlan.readList(this.weekdays,Integer.class.getClassLoader());
        this.name = workoutPlan.readString();
        this.colorId = workoutPlan.readInt();
        this.workoutPlanId = workoutPlan.readString();
    }

    public WorkoutPlan(){}

    public String getWorkoutPlanId() {
        return workoutPlanId;
    }

    public void setWorkoutPlanId(String workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public WorkoutPlan(WorkoutPlan workoutPlan){
        this(workoutPlan.getName(),workoutPlan.getExercises(),workoutPlan.getColorId(),workoutPlan.getWeekdays(),workoutPlan.getWorkoutPlanId());
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorTag(int colorId) {
        this.colorId = colorId;
    }

    public static Creator<WorkoutPlan> getCREATOR() {
        return CREATOR;
    }

    public List<com.alberoneramos.workout.models.Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.exercises);
        dest.writeList(this.weekdays);
        dest.writeString(this.name);
        dest.writeInt(this.colorId);
        dest.writeString(this.workoutPlanId);
    }

    public void editExercise(int position, Exercise exercise){
        this.exercises.set(position,exercise);
    }

    public void addExercise(Exercise exercise){
        this.exercises.add(exercise);
    }

    public static final Parcelable.Creator<WorkoutPlan> CREATOR = new Parcelable.Creator<WorkoutPlan>(){
        public WorkoutPlan createFromParcel(Parcel workoutPlan){
            return new WorkoutPlan(workoutPlan);
        }

        @Override
        public WorkoutPlan[] newArray(int size) {
            return new WorkoutPlan[size];
        }
    };
}
