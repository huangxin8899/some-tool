package com.huangxin.mybatis;

import com.huangxin.domain.User;
import com.huangxin.mybatis.builder.SqlBuilder;
import org.junit.Test;

import java.util.Arrays;

/**
 * BuilderTest
 *
 * @author huangxin
 */
public class BuilderTest {
    @Test
    public void testQuery() {
        System.out.println(SqlBuilder.query(User.getOne()).build());
    }

    @Test
    public void testDelete() {
        String string = SqlBuilder.deleteByIds(User.class, Arrays.asList("1", "2")).build();
        System.out.println(string);
    }
}
