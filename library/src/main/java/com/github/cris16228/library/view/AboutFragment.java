package com.github.cris16228.library.view;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.github.cris16228.library.R;
import com.github.cris16228.library.deviceutils.PackageUtils;

public class AboutFragment extends Fragment {

    private final PackageInfo packageInfo;
    private ImageView aboutImage;
    private TextView aboutAppTitle, aboutAppDescription, aboutAppVersion;
    private final @StringRes int appDescription;
    private final String appVersion;

    public AboutFragment(PackageInfo packageInfo, int appDescription, String appVersion) {
        this.packageInfo = packageInfo;
        this.appDescription = appDescription;
        this.appVersion = appVersion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        aboutImage = view.findViewById(R.id.aboutImage);
        aboutAppTitle = view.findViewById(R.id.aboutAppTitle);
        aboutAppDescription = view.findViewById(R.id.aboutAppDescription);
        aboutAppVersion = view.findViewById(R.id.aboutAppVersion);
        aboutAppTitle.setText(PackageUtils.with(requireContext()).getAppName(packageInfo.packageName));
        aboutAppDescription.setText(appDescription);
        aboutAppVersion.setText(requireContext().getResources().getString(R.string.about_version, packageInfo.versionName, String.valueOf(packageInfo.getLongVersionCode())));
        return view;
    }
}