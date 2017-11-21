package ru.vps.retrofit2test.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.vps.retrofit2test.R;
import ru.vps.retrofit2test.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MainFragment())
                    .commit();
        }
    }
}
