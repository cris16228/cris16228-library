package com.github.cris16228.library;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;


public class Core {

    public String convertToFirstUpper(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toUpperCase();
    }

    public void refreshFragment(FragmentActivity fragmentActivity, Fragment fragment) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }
}
