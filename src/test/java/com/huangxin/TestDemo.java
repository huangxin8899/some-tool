package com.huangxin;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Test
 *
 * @author 黄鑫
 */
public class TestDemo {
    @Test
    public void test() {
        String[][] result = parseStringToArray("[[A1,B1,10],[A1,B1,10]]");
        // 打印结果
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static String[][] parseStringToArray(String input) {
        // 去除首尾的方括号，并按逗号分割
        String[] outerTokens = input.substring(1, input.length() - 1).split("],");

        // 初始化结果数组
        String[][] result = new String[outerTokens.length][];

        for (int i = 0; i < outerTokens.length; i++) {
            // 去除当前子数组的方括号，并按逗号分割
            String[] innerTokens = outerTokens[i].replaceAll("\\[|\\]", "").split(",");

            // 将字符串转换为整数并存入结果数组
            result[i] = new String[innerTokens.length];
            System.arraycopy(innerTokens, 0, result[i], 0, innerTokens.length);
        }

        return result;
    }
}
