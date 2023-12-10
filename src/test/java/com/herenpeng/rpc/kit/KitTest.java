package com.herenpeng.rpc.kit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.kit.serialize.JsonSerializer;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author herenpeng
 * @since 2023-06-18 23:00
 */
public class KitTest {


    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    public void test02() throws Exception {
        // 1KB
        byte[] bytes = generateBytes(1);
        System.out.println(Arrays.toString(bytes));
        testCompress(bytes);
        System.out.println("=================");
        // 10KB
        testCompress(generateBytes( 10));
        System.out.println("=================");
        // 1MB
        testCompress(generateBytes(1024));
        System.out.println("=================");
        // 100MB
        testCompress(generateBytes(1024 * 100));
    }


    private void testCompress(byte[] bytes) {
        long start = System.currentTimeMillis();
        byte[] compress = RpcKit.compress(bytes);
        long compressTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        byte[] decompress = RpcKit.decompress(compress);
        long decompressTime = System.currentTimeMillis() - start;

        System.out.println("压缩前字节长度：" + bytes.length);
        System.out.println("压缩后字节长度：" + compress.length + "，压缩耗时：" + compressTime + "ms，压缩率：" + pre(compress.length, bytes.length) + "%");
        System.out.println("解压缩后字节长度：" + decompress.length + "，解压缩耗时：" + decompressTime + "ms，解压缩后数据和源数据" + (compareBytes(bytes, decompress) ? "一致" : "不一致"));
    }


    private String pre(int num1, int num2) {
        BigDecimal divide = new BigDecimal(num1).multiply(new BigDecimal(100)).divide(new BigDecimal(num2), RoundingMode.HALF_UP);
        return divide.toPlainString();
    }


    private byte[] generateBytes(int length) throws JsonProcessingException {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            // 一个循环内的数据大约1KB
            list.add(new User(15, "郑明", true, 18, new Date(), new Date(), new Date()));
            list.add(new User(16, "李红", false, 21, new Date(), new Date(), new Date()));
            list.add(new User(17, "吴雷", true, 25, new Date(), new Date(), new Date()));
            list.add(new User(18, "张刚", true, 29, new Date(), new Date(), new Date()));
            list.add(new User(19, "小李", true, 42, new Date(), new Date(), new Date()));
            list.add(new User(20, "赵王", false, 28, new Date(), new Date(), new Date()));
            list.add(new User(21, "孙周", false, 35, new Date(), new Date(), new Date()));
            list.add(new User(22, "大王", false, 28, new Date(), new Date(), new Date()));
        }
        return objectMapper.writeValueAsBytes(list);
    }


    private boolean compareBytes(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length != bytes2.length) {
            return false;
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }


    @Test
    public void test03() {
        List<User> list = new ArrayList<>();
    }

}
