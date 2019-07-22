/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static ouroboros.Ouroboros.SERVER;

/**
 * SQLiteDialect: https://gist.github.com/virasak/54436
 * baixei e adicionei no Hibernate a classe para SQLite
 * @author ivand
 */
public class ConnectionFactory {
    EntityManagerFactory emf;
    EntityManagerFactory emfBootstrap = Persistence.createEntityManagerFactory("bootstrapPU");
    
    public ConnectionFactory() {
        Map properties = new HashMap();
        //Propriedades JDBC
        //properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        //ao trocar a versão para mysql-connector-java-8.0.12.jar
        properties.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        
        properties.put("javax.persistence.jdbc.url", "jdbc:mysql://" + SERVER + "/ouroboros?useTimezone=true&serverTimezone=America/Sao_Paulo");
        properties.put("javax.persistence.jdbc.user", "root");
        properties.put("javax.persistence.jdbc.password", "");
        /*When it is set to USE, data is retrieved from the second-level cache, 
        if available. If the data is not in the cache, the persistence provider will read it from the database. 
        When it is set to BYPASS, the second-level cache is bypassed and a call 
        to the database is made to retrieve the data.
        */
        //properties.put("javax.persistence.CacheRetrieveMode", "BYPASS"); // pega do banco sempre
        properties.put("javax.persistence.CacheStoreMode", "USE");
        //Configurações específicas do Hibernate
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "false");
        //Important
        properties.put("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        //Configuring Connection Pool
        properties.put("hibernate.c3p0.min_size", "5");
        properties.put("hibernate.c3p0.max_size", "100");
        properties.put("hibernate.c3p0.acquire_increment", "1");
        properties.put("hibernate.c3p0.timeout", "5");
        properties.put("hibernate.c3p0.max_statements", "0");
        properties.put("hibernate.c3p0.idle_test_period", "10");
        
        
        
        emf = Persistence.createEntityManagerFactory("mwPU", properties);
        
    }
    
    public EntityManager getConnection(){
        return emf.createEntityManager();
    }
    
    public EntityManager getConnectionBootstrap(){
        return emfBootstrap.createEntityManager();
    }
    
}
