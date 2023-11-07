package com.huangxin.sql;

import com.huangxin.domain.Order;
import com.huangxin.domain.User;
import com.huangxin.sql.builder.DeleteBuilder;
import com.huangxin.sql.builder.InsertBuilder;
import com.huangxin.sql.builder.SelectBuilder;
import com.huangxin.sql.builder.UpdateBuilder;
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
        String string = new SelectBuilder().select(User::getId, "id1")
                .from(User.class, "user1")
                .eq(User::getName, "aa")
                .and(and -> and.like(User::getName, "1").ge(User::getId, 1).or(or -> or.isNotNull(User::getId).eq(User::getId, 1)))
                .or(or -> or.eq(User::getId, 1).eq(User::getId, 2))
                .leftJoin(Order.class, "o", join -> join.eq(User::getId, Order::getId).notIn(User::getName, "bb","ccc").or(e -> e.eq(User::getId, 1).between(User::getId, 2, 3)))
                .rightJoin(User.class, "user2", join -> join.eq(User::getName, "bb").or(e -> e.eq(User::getId, 1)))
                .having(having -> having.ge(User::getAge, 12).or(e -> e.eq(User::getId, 1).eq(User::getId, 2)))
                .groupBy(User::getId)
                .orderByAsc(User::getId,User::getAge)
                .build();
        System.out.println(string);
    }

    @Test
    public void testInsert() {
        User user = new User(1, "2", 9);
        User user2 = new User(1, "2", 19);
        List<User> list = Arrays.asList(user, user, user2);
        System.out.println(new InsertBuilder().insertBatch(list).build());
    }

    @Test
    public void testUpdate() {
        String sql = new UpdateBuilder()
                .updateTable("111")
                .set(User::getId, 2)
                .eq(User::getName, "aa")
                .and(e -> e.eq(User::getId, 1).eq(User::getId, 1).or(c -> c.eq(User::getId, 2).eq(User::getId, 1)))
                .or(e -> e.eq(User::getId, 1).eq(User::getId, 2))
                .build();
        System.out.println(sql);
    }

    @Test
    public void testDelete() {
        String sql = new DeleteBuilder()
                .deleteTable("111")
                .eq(Order::getId, 3).or(e -> e.eq(Order::getAge, 22)).build();
        System.out.println(sql);
    }

    @Test
    public void testSql() {
        String string = new SQL().SELECT("11").FROM("22")
                .WHERE("333", "333", "333")
                .AND()
                .WHERE("44")
                .OR()
                .WHERE("54")
                .toString();
        System.out.println(string);
    }
}
