package code.support.demo.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import code.support.demo.R;
import code.support.demo.widget.PickerView;

public class TestActivity_8 extends AppCompatActivity{

    String[] years = new String[]{
            "1991年",
            "1992年",
            "1993年",
            "1994年",
            "1995年",
            "1996年",
            "1997年",
            "1998年",
            "1999年",
            "2000年",
            "2001年",
            "2002年",
            "2003年",
            "2004年",
            "2005年",
            "2006年",
            "2007年",
            "2008年",
            "2009年",
            "2010年",
            "2011年",
            "2012年",
            "2013年",
            "2014年",
            "2015年",
            "2016年"
    };
    String[] months = new String[]{
            "01月",
            "02月",
            "03月",
            "04月",
            "05月",
            "06月",
            "07月",
            "08月",
            "09月",
            "10月",
            "11月",
            "12月",
            "13月",
    };
    String[] days = new String[]{
            "01日",
            "02日",
            "03日",
            "04日",
            "05日",
            "06日",
            "07日",
            "08日",
            "09日",
            "10日",
            "11日",
            "12日",
            "13日",
            "14日",
            "15日",
            "16日",
            "17日",
            "18日",
            "19日",
            "20日",
            "21日",
            "22日",
            "23日",
            "24日",
            "25日",
            "26日",
            "27日",
            "28日",
            "29日",
            "30日",
            "31日",
    };

    private List<String> l1 = new ArrayList<>();
    private List<String> l2 = new ArrayList<>();
    private List<String> l3 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_08);

        PickerView year = (PickerView) findViewById(R.id.year_pv);
        PickerView month = (PickerView) findViewById(R.id.month_pv);
        PickerView day = (PickerView) findViewById(R.id.day_pv);

        Collections.addAll(l1, years);
        Collections.addAll(l2, months);
        Collections.addAll(l3, days);

        year.setData(l1);
        month.setData(l2);
        day.setData(l3);
    }
}
