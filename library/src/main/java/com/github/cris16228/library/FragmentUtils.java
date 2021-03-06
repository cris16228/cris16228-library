package com.github.cris16228.library;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils {

    FragmentActivity fragmentActivity;

    public static FragmentUtils with(FragmentActivity _fragmentActivity) {
        FragmentUtils fragmentUtils = new FragmentUtils();
        fragmentUtils.fragmentActivity = _fragmentActivity;
        return fragmentUtils;
    }

    public void replace(@IdRes int containerViewId, @NonNull Fragment fragment) {
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        manager.beginTransaction().replace(containerViewId,
                fragment, fragment.getClass().getSimpleName()).addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    public void refreshFragment(FragmentActivity fragmentActivity, Fragment fragment) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }
}
