package com.alberoneramos.workout.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.interfaces.IAddExercise;
import com.alberoneramos.workout.models.Exercise;

public class AddExerciseDialog extends DialogFragment {
    Spinner bodyPartSpinner;
    AutoCompleteTextView exerciseName;
    private IAddExercise mCallback;
    EditText setsText;
    EditText repsText;
    EditText weightText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IAddExercise) activity;
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_add_exercise, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initViews(view);
        }
        builder.setView(view)
                .setPositiveButton((getArguments() != null && getArguments().getBoolean("EDIT_MODE")) ? R.string.exercise_dialog_edit : R.string.exercise_dialog_confirm, (dialog, id) -> {
                    boolean editMode;
                    Bundle arguments = getArguments();
                    int position;
                    if(arguments != null && arguments.containsKey("EDIT_MODE")) {
                        editMode = arguments.getBoolean("EDIT_MODE");
                        position = arguments.getInt("POSITION");
                    } else{
                        editMode = false;
                        position = 0;
                    }
                    mCallback.onExerciseAdd( new Exercise(bodyPartSpinner.getSelectedItemPosition()+1,
                            exerciseName.getText().toString(),
                            Integer.parseInt(setsText.getText().toString()),
                            Integer.parseInt(repsText.getText().toString()),
                            Integer.parseInt(weightText.getText().toString())),editMode,position);
                    AddExerciseDialog.this.getDialog().cancel();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> AddExerciseDialog.this.getDialog().cancel());
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initViews(View view){
        bodyPartSpinner = view.findViewById(R.id.body_part_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.body_parts));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyPartSpinner.setAdapter(spinnerArrayAdapter);
        exerciseName = view.findViewById(R.id.exercise_name);
        setsText = view.findViewById(R.id.sets);
        setsText.setText("0");
        repsText = view.findViewById(R.id.reps);
        repsText.setText("0");
        weightText = view.findViewById(R.id.weight);
        weightText.setText("0");
        if(getArguments() != null && getArguments().getBoolean("EDIT_MODE")){
            Exercise exercise = getArguments().getParcelable("EXERCISE");
            if (exercise != null) {
                exerciseName.setText(exercise.getExerciseName());
                setsText.setText(String.valueOf(exercise.getSets()));
                repsText.setText(String.valueOf(exercise.getRepetitions()));
                weightText.setText(String.valueOf(exercise.getWeight()));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
