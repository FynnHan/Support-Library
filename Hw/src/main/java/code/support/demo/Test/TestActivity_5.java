package code.support.demo.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import code.support.demo.R;
import code.support.demo.widget.indexbar.IndexBar;

/**
 * Created by zpj on 2016/7/20.
 */
public class TestActivity_5 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_05);

        IndexBar indexBar = (IndexBar) findViewById(R.id.indexbar);
        TextView textView = (TextView) findViewById(R.id.textView);

        indexBar.setOnTouchingLetterChangedListener(new IndexBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                textView.setText("select " + s);
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("OnClick ");
            }
        });
    }
}
