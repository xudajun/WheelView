package com.ivan.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ivan.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试
 *
 * @author Xuzj
 */
public class MainActivity extends AppCompatActivity {

    private WheelView wheelView;
    private List<String> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wheelView = (WheelView) findViewById(R.id.wheelView);
        init();
    }

    void init() {
        data = new ArrayList<>();
        data.add("河南省");
        data.add("山东省");
        data.add("山西省");
        data.add("河北省");
        data.add("湖北省");
        data.add("湖南省");
        data.add("浙江省");
        data.add("西藏");
        data.add("福建省");
        data.add("广西省");
        data.add("广东省");
        data.add("贵州省");
        data.add("兰州省");
        data.add("甘肃省");
        wheelView.setData(data);
        wheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onItemSelect(int position) {
                Toast.makeText(MainActivity.this, "选中了" + data.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
