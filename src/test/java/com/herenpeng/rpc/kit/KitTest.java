package com.herenpeng.rpc.kit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-06-18 23:00
 */
public class KitTest {

    @Test
    public void test01() {
        List<List<Object>> data = new ArrayList<>();

        List<Object> list1 = Arrays.asList("序号", "昵称", "性别（1:男0:女）", "手机号码", "地址");
        List<Object> list2 = Arrays.asList(1, "归零者", 1, "17854265423", "xx省xx市xx区xx东路x号");
        List<Object> list3 = Arrays.asList(2, "歌者", 1, "17854521114", "xx省xx市xx区xx路xxx-x号");
        List<Object> list4 = Arrays.asList(3, "叶文洁", 0, "18945411454", "xx省xx市xx区xx东路xx号");
        List<Object> list5 = Arrays.asList(4, "罗辑", 1, "18945414154", "xx省xx市xx区xx路xxx号xx花园x栋x单元xxx室");
        data.add(list1);
        data.add(list2);
        data.add(list3);
        data.add(list4);
        data.add(list5);
        TableUtils.print(data);
    }

}
