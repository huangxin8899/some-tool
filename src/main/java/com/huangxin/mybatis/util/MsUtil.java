package com.huangxin.mybatis.util;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Map;

/**
 * MsUtil
 *
 * @author huangxin
 */
public class MsUtil {

    private final Configuration configuration;
    private final LanguageDriver languageDriver;

    public MsUtil(Configuration configuration) {
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
    }

    private String newMsId(String sql, SqlCommandType sqlCommandType) {
        return sqlCommandType.toString() + "." + sql.hashCode();
    }

    private boolean hasMappedStatement(String msId) {
        return this.configuration.hasStatement(msId, false);
    }

    private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
        MappedStatement ms = (new MappedStatement.Builder(this.configuration, msId, sqlSource, SqlCommandType.SELECT)).resultMaps(new ArrayList<ResultMap>() {
            {
                this.add((new ResultMap.Builder(MsUtil.this.configuration, "defaultResultMap", resultType, new ArrayList<>())).build());
            }
        }).build();
        this.configuration.addMappedStatement(ms);
    }

    private void newInsertMappedStatement(String msId, SqlSource sqlSource, Class<?> parameterType) {
        this.configuration.setUseGeneratedKeys(true);
        MappedStatement ms = (new MappedStatement.Builder(this.configuration, msId, sqlSource, SqlCommandType.INSERT)).resultMaps(new ArrayList<ResultMap>() {
            {
                this.add((new ResultMap.Builder(MsUtil.this.configuration, "defaultResultMap", Integer.TYPE, new ArrayList<>())).build());
            }
        }).keyProperty(AnnoUtil.getPrimaryName(parameterType)).keyColumn(AnnoUtil.getPrimaryColumn(parameterType)).build();
        this.configuration.addMappedStatement(ms);
    }

    private void newUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
        MappedStatement ms = (new MappedStatement.Builder(this.configuration, msId, sqlSource, sqlCommandType)).resultMaps(new ArrayList<ResultMap>() {
            {
                this.add((new ResultMap.Builder(MsUtil.this.configuration, "defaultResultMap", Integer.TYPE, new ArrayList<>())).build());
            }
        }).build();
        this.configuration.addMappedStatement(ms);
    }

    public String select(String sql) {
        return select(sql, Map.class);
    }

    public String selectDynamic(String sql, Class<?> parameterType) {
        return selectDynamic(sql, parameterType, Map.class);
    }

    public String select(String sql, Class<?> resultType) {
        String msId = this.newMsId(resultType + sql, SqlCommandType.SELECT);
        if (!this.hasMappedStatement(msId)) {
            StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
            this.newSelectMappedStatement(msId, sqlSource, resultType);
        }
        return msId;
    }

    public String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
        String msId = this.newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
        if (!this.hasMappedStatement(msId)) {
            SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
            this.newSelectMappedStatement(msId, sqlSource, resultType);
        }
        return msId;
    }

    public String insertDynamic(String sql, Class<?> parameterType) {
        String msId = this.newMsId(sql + parameterType, SqlCommandType.INSERT);
        if (!this.hasMappedStatement(msId)) {
            SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
            this.newInsertMappedStatement(msId, sqlSource, parameterType);
        }
        return msId;
    }

    public String update(String sql) {
        String msId = this.newMsId(sql, SqlCommandType.UPDATE);
        if (!this.hasMappedStatement(msId)) {
            StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
            this.newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
        }
        return msId;
    }

    public String updateDynamic(String sql, Class<?> parameterType) {
        String msId = this.newMsId(sql + parameterType, SqlCommandType.UPDATE);
        if (!this.hasMappedStatement(msId)) {
            SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
            this.newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
        }
        return msId;
    }

    public String delete(String sql) {
        String msId = this.newMsId(sql, SqlCommandType.DELETE);
        if (!this.hasMappedStatement(msId)) {
            StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
            this.newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
        }
        return msId;
    }

    public String deleteDynamic(String sql, Class<?> parameterType) {
        String msId = this.newMsId(sql + parameterType, SqlCommandType.DELETE);
        if (!this.hasMappedStatement(msId)) {
            SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
            this.newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
        }
        return msId;
    }
}
