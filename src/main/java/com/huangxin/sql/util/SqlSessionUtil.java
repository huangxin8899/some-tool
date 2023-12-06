package com.huangxin.sql.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * SqlSessionUtil
 *
 * @author huangxin
 */
@Component
public class SqlSessionUtil implements ApplicationContextAware {

    private static SqlSessionFactory sqlSessionFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanName = applicationContext.getBeanNamesForType(SqlSessionFactory.class);
        sqlSessionFactory = applicationContext.getBean(beanName[0], SqlSessionFactory.class);
    }

    public static <T> T queryOne(String sql, Object param, Class<T> resultType) {
        List<T> list = queryList(sql, param, resultType);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    public static <T> List<T> queryList(String sql, Object param, Class<T> resultType) {
        if (StrUtil.isEmpty(sql)) {
            return new ArrayList<>();
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.selectDynamic(sql, parameterType, resultType);
            return sqlSession.selectList(msId, param);
        }
    }

    public static int insert(String sql, Object param) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.insertDynamic(sql, parameterType);
            return sqlSession.insert(msId, param);
        }
    }

    public static int update(String sql, Object param) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.updateDynamic(sql, parameterType);
            return sqlSession.update(msId, param);
        }
    }

    public static int delete(String sql, Object param) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.deleteDynamic(sql, parameterType);
            return sqlSession.delete(msId, param);
        }
    }

    public static <T> List<T> execQuery(String sql, Object param, Class<T> resultType, BiFunction<SqlSession, String, List<T>> function) {
        if (StrUtil.isEmpty(sql)) {
            return null;
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.selectDynamic(sql, parameterType, resultType);
            return function.apply(sqlSession, msId);
        }
    }

}
