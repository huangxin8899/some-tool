package com.huangxin.mybatis.util;

import cn.hutool.core.util.ObjectUtil;
import com.huangxin.mybatis.util.MsUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DefaultSqlExecutor
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
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MsUtil msUtil = new MsUtil(sqlSession.getConfiguration());
            Class<?> parameterType = ObjectUtil.isNotEmpty(param) ? param.getClass() : null;
            String msId = msUtil.selectDynamic(sql, parameterType, resultType);
            return sqlSession.selectOne(msId, param);
        }
    }

    public static <T> List<T> queryList(String sql, Object param, Class<T> resultType) {
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

}
