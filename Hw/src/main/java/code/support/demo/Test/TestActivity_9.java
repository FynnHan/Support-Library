package code.support.demo.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import code.support.demo.R;
import code.support.demo.widget.PickerView;

public class TestActivity_9 extends AppCompatActivity {

    Button btn_scrollBy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_09);

        btn_scrollBy = (Button) findViewById(R.id.btn_scrollBy);
        btn_scrollBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 测试scrollBy的特点：调用者本身发生位移，其content的坐标不变
//                ((View) btn_scrollBy.getParent()).scrollBy(100, 100);
//                btn_scrollBy.scrollBy(20, 20);
            }
        });
    }
}
