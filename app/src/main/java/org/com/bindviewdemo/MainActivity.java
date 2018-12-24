package org.com.bindviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.com.bindviewannotion.BindView;
import org.com.bindviwe.BindingView;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv1)
    TextView tv;

    @BindView(R.id.tv2)
    TextView tv2;

    @butterknife.BindView(R.id.tv1)
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BindingView.bindView(this);
        tv.setText("TTTTTTTTTTTTTTTT");
    }
}
