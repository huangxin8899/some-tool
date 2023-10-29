package com.huangxin.mybatis;

import com.huangxin.domain.User;
import com.huangxin.mybatis.builder.SqlBuilder;
import org.junit.Test;

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
    public void name() {
        String str = null + "123";
        System.out.println(str);
    }
}
