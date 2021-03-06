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
import javax.swing.JOptionPane;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.SERVER;

/**
 * SQLiteDialect: https://gist.github.com/virasak/54436
 * baixei e adicionei no Hibernate a classe para SQLite
 * @author ivand
 */
public class ConnectionFactory {
    EntityManagerFactory emf;
    EntityManagerFactory emfBootstrap = Persistence.createEntityManagerFactory("bootstrapPU");
    private final static String DATABASE = Ouroboros.DATABASE_NAME;
    
    public ConnectionFactory() {
        
        if (!DATABASE.equals("ouroboros")) {
            JOptionPane.showMessageDialog(Ouroboros.MAIN_VIEW, "DB: " + DATABASE);
        }
        
        Map properties = new HashMap();
        //Propriedades JDBC
        //properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        //ao trocar a versão para mysql-connector-java-8.0.12.jar
        properties.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        
        properties.put("javax.persistence.jdbc.url", "jdbc:mysql://" + SERVER + "/" + DATABASE + "?useTimezone=true&serverTimezone=GMT-3&useSSL=false"); //2019-12-13 desativado ssl - erro no MacOS
        properties.put("javax.persistence.jdbc.user", Ouroboros.DATABASE_USER);
        properties.put("javax.persistence.jdbc.password", Ouroboros.DATABASE_PASSWORD);
        /*When it is set to USE, data is retrieved from the second-level cache, 
        if available. If the data is not in the cache, the persistence provider will read it from the database. 
        When it is set to BYPASS, the second-level cache is bypassed and a call 
        to the database is made to retrieve the data.
        */
        properties.put("javax.persistence.CacheRetrieveMode", "BYPASS"); // pega do banco sempre
        //properties.put("javax.persistence.CacheStoreMode", "USE");
        //Configurações específicas do Hibernate
        
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "false");
        properties.put("hibernate.enable_lazy_load_no_trans", "true");
        
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
