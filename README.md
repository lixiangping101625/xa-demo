### jta-atomikos 实现分布式锁
#### 1、加依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jta-atomikos</artifactId>
    </dependency>

#### 2、设置XADataSource和sqlSessionFactory
    第一个资源管理器配置：
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

    第一个资源管理器配置：
        import com.mysql.cj.jdbc.MysqlXADataSource;
        import org.mybatis.spring.SqlSessionFactoryBean;
        import org.mybatis.spring.annotation.MapperScan;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
        import org.springframework.core.io.support.ResourcePatternResolver;

        import javax.sql.DataSource;
        import java.io.IOException;

        @Configuration
        @MapperScan(value = "com.hlkj.xademo.db132.dao",sqlSessionFactoryRef = "sqlSessionFactoryBean132")
        public class ConfigDb132 {

            @Bean("db132")
            public DataSource db132(){
                MysqlXADataSource xaDataSource = new MysqlXADataSource();
                xaDataSource.setUser("root");
                xaDataSource.setPassword("123456");
                xaDataSource.setUrl("jdbc:mysql://127.0.0.1:3307/xa_132");

                AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
                atomikosDataSourceBean.setXaDataSource(xaDataSource);


                return atomikosDataSourceBean;
            }

            @Bean("sqlSessionFactoryBean132")
            public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("db132") DataSource dataSource) throws IOException {
                SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                sqlSessionFactoryBean.setDataSource(dataSource);
                ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
                sqlSessionFactoryBean.setMapperLocations(resourceResolver.getResources("mybatis/db132/*.xml"));
                return sqlSessionFactoryBean;
            }

        }

     测试：
            @Resource
            private XA131Mapper xa131Mapper;
            @Resource
            private XA132Mapper xa132Mapper;

            @Transactional(transactionManager = "xaTransaction")//指定事务管理器
            public void testXA() {
                XA131 xa131 = new XA131();
                xa131.setId(1);
                xa131.setName("xa_131");
                xa131Mapper.insert(xa131);

                XA132 xa132 = new XA132();
                xa132.setId(1);
                xa132.setName("xa_132");
                xa132Mapper.insert(xa132);

            }
     效果：正常（同时成功，同时失败）
