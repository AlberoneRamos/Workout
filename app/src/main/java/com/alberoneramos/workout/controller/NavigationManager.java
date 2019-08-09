package com.alberoneramos.workout.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alberoneramos.workout.R;

public class NavigationManager {

    private static Fragment getVisibleFragment(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public static void openFragment(FragmentManager fragmentManager, Fragment fragment, String tag, int placeholder) {
        if (compareFragment(getVisibleFragment(fragmentManager), fragment)) {
            if (fragment.getTag() == null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(placeholder, fragment, tag)
                        .addToBackStack(tag)
                        .commit();
            } else {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(placeholder, fragment, fragment.getTag())
                        .addToBackStack(fragment.getTag())
                        .commit();
            }
        }
    }

    public static void openActivity(Activity activity, Class<?> clazz) {
        Intent intent = new Intent(activity, clazz);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        activity.startActivity(intent);
    }

    public static void openActivity(Activity activity, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(activity, clazz);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private static boolean compareFragment(Fragment visibleFragment, Fragment nextFragment) {
        return (visibleFragment == null || (nextFragment != null && (visibleFragment.getClass() != nextFragment.getClass())));
    }

}
