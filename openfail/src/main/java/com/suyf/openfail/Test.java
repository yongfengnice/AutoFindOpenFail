package com.suyf.openfail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Created by suyongfeng on 2022/3/16
 */
public class Test {

    private void test(){
        List<String> list = new ArrayList<>();
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        });
    }
}
