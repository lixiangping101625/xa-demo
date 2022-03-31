package com.hlkj.xademo.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.io.IOException;

@Configuration
@MapperScan(value = "com.hlkj.xademo.db131.dao",sqlSessionFactoryRef = "sqlSessionFactoryBean131")
public class ConfigDb131 {

    @Bean("db131")
    public DataSource db131(){
        //注意是MysqlXADataSource数据源
        MysqlXADataSource xaDataSource = new MysqlXADataSource();
        xaDataSource.setUser("root");
        xaDataSource.setPassword("123456");
        xaDataSource.setUrl("jdbc:mysql://127.0.0.1:3307/xa_131");

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(xaDataSource);


        return atomikosDataSourceBean;
    }

    @Bean("sqlSessionFactoryBean131")
    @Primary
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("db131") DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resourceResolver.getResources("mybatis/db131/*.xml"));
        return sqlSessionFactoryBean;
    }

    /**
     * 自定事务管理器TM（只在一个数据源定义即可）
     * @return
     */
    @Bean("xaTransaction")
    public JtaTransactionManager jtaTransactionManager(){
        UserTransaction userTransaction = new UserTransactionImp();
        UserTransactionManager userTransactionManager = new UserTransactionManager();

        return new JtaTransactionManager(userTransaction,userTransactionManager);
    }

}
