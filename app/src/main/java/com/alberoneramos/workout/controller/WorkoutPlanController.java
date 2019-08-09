package com.alberoneramos.workout.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.alberoneramos.workout.utils.UrlBuilder;
import com.alberoneramos.workout.view.activity.AddWorkoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class WorkoutPlanController {
    private Activity activity;
    private WorkoutPlan workoutPlan;

    public WorkoutPlanController(Activity activity, WorkoutPlan workoutPlan) {
        this.activity = activity;
        this.workoutPlan = workoutPlan;
    }

    public void deleteDialogBuilder() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(R.string.delete_workout_dialog_title);
        alertDialog.setMessage(activity.getResources().getString(R.string.delete_workout_dialog_message));
        alertDialog.setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts").child(workoutPlan.getWorkoutPlanId()).removeValue();
            goBack(activity.getFragmentManager());
        });
        alertDialog.setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.cancel());
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    public void shareDialogBuilder(String uri) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(R.string.share_workout_title);
        alertDialog.setMessage(activity.getResources().getString(R.string.share_workout_message));
        LinearLayout layout = new LinearLayout(activity);
        final EditText input = setupDialogInput(uri);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        ImageView qrCode = new ImageView(activity);
        qrCode.setImageBitmap(UrlBuilder.generateQRCode(uri, 800));
        layout.addView(qrCode);
        alertDialog.setPositiveButton(R.string.clipboard_copy, (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(activity).getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URI", uri);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
        });
        alertDialog.setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.cancel());
        alertDialog.setView(layout);
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    private EditText setupDialogInput(String uri) {
        EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(uri);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        input.setLayoutParams(params);
        input.setInputType(InputType.TYPE_NULL);
        input.setSelectAllOnFocus(true);
        input.setMaxLines(1);
        input.setHorizontallyScrolling(true);
        input.setFilters(new InputFilter[]{
                (src, start, end, dst, dstart, dend) -> src.length() < 1 ? dst.subSequence(dstart, dend) : ""
        });
        return input;
    }

    public void showEditActivity(int code) {
        Intent intent = new Intent(activity, AddWorkoutActivity.class);
        intent.putExtra("WORKOUT_DATA", workoutPlan);
        Objects.requireNonNull(activity).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        intent.putExtra("EDIT_MODE", true);
        activity.startActivityForResult(intent, code);
    }

    private void goBack(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
        }
    }


}
