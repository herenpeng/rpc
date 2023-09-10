package com.herenpeng.rpc.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-06-18 21:38
 */
public class TableUtils {

    private static final String NODE = "+";
    private static final String EDGE = "-";
    private static final String HIGH = "|";
    private static final String SPACE = " ";

    private static int getWidth(Object object) {
        if (object == null) {
            return "null".length();
        }
        int width = 0;
        String content = object.toString();
        for (int i = 0; i < content.length(); i++) {
            width += content.charAt(i) < 128 ? 1 : 2;
        }
        return width;
    }

    private static void printEdge(List<Integer> widths) {
        for (int i = 0; i < widths.size(); i++) {
            int width = widths.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(NODE);
            sb.append(EDGE.repeat(Math.max(0, width + 2)));
            if (i == widths.size() - 1) {
                sb.append(NODE + "\n");
            }
            System.out.print(sb);
        }
    }

    private static void printData(List<Integer> maxWidths, List<Object> data) {
        for (int i = 0; i < maxWidths.size(); i++) {
            Integer maxWidth = maxWidths.get(i);
            Object object = i < data.size() ? data.get(i) : null;
            int width = getWidth(object);
            StringBuilder sb = new StringBuilder();
            sb.append(HIGH);
            String repeat = SPACE.repeat(Math.max(0, maxWidth - width + 1));
            if (object != null && object.getClass() == Integer.class) {
                sb.append(repeat);
                sb.append(object);
                sb.append(SPACE);
            } else {
                sb.append(SPACE);
                sb.append(object);
                sb.append(repeat);
            }
            if (i == maxWidths.size() - 1) {
                sb.append(HIGH + "\n");
            }
            System.out.print(sb);
        }
    }

    private static List<Integer> getMaxWidths(List<List<Object>> data) {
        List<Integer> maxWidths = new ArrayList<>();
        for (List<Object> datum : data) {
            for (int i = 0; i < datum.size(); i++) {
                int width = getWidth(datum.get(i).toString());
                if (i < maxWidths.size()) {
                    maxWidths.set(i, Math.max(width, maxWidths.get(i)));
                } else {
                    maxWidths.add(width);
                }
            }
        }
        return maxWidths;
    }


    public static void print(List<List<Object>> data) {
        List<Integer> maxWidths = getMaxWidths(data);
        for (int i = 0; i < data.size(); i++) {
            if (i <= 1) {
                printEdge(maxWidths);
            }
            printData(maxWidths, data.get(i));
            if (i == data.size() - 1) {
                printEdge(maxWidths);
            }
        }
    }

}
