package ru.vps.umorili_client.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.vps.umorili_client.R;

public class AboutFragment extends Fragment {
    private static final String ARG_APP_NAME = "ru.vps.umorili_client.fragment.about_fragment.app_name";
    private static final String ARG_APP_VERSION = "ru.vps.umorili_client.fragment.about_fragment.app_version";
    //
    private String name;
    private String version;

    public static AboutFragment newInstance(String appName, String appVersion) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, appName);
        args.putString(ARG_APP_VERSION, appVersion);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_APP_NAME);
            version = getArguments().getString(ARG_APP_VERSION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView aboutView = (TextView) view.findViewById(R.id.about);
        String aboutString = getString(R.string.about_text, name, version);
        aboutView.setText(Html.fromHtml(aboutString));

        return view;
    }
}
