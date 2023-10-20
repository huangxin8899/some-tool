package com.huangxin.mybatis;

import com.huangxin.domain.User;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * SqlTest
 *
 * @author 黄鑫
 */
public class SqlTest {

    @Test
    public void testSelect() {
        /*SQL sql = SqlFactory.query(User.class).select(User::getId).eq(User::getAge, 1).getSql();
        System.out.println(sql.toString());*/
        String string = new SqlBuilder().select(User::getId, "id1")
                .from(User.class, "user1")
                .eq(User::getName, "aa")
                .or(e -> e.eq(User::getId, 1).eq(User::getId, 2))
                .leftJoin(User::getId, User::getAge, join -> join.eq(User::getName, "bb").or(e -> e.eq(User::getId, 1).eq(User::getId, 2)))
                .rightJoin(User::getId, User::getAge, join -> join.eq(User::getName, "bb").or(e -> e.eq(User::getId, 1)))
                .having(sql -> sql.ge(User::getAge, 12).or(e -> e.eq(User::getId, 1).eq(User::getId, 2)))
                .groupBy(User::getId)
                .build();
        System.out.println(string);
    }

    @Test
    public void testInsert() {
        User user = new User();
        List<User> list = Arrays.asList(user, user, user);
        System.out.println(new SqlBuilder().insertBatch(list));
    }
}
