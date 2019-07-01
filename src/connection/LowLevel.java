/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import ouroboros.Ouroboros;

/**
 *
 * @author ivand
 */
public class LowLevel {
    
    private static Connection con;
    
    public LowLevel() {
        try {
            String url = "jdbc:mysql://" + Ouroboros.SERVER + "/ouroboros?useTimezone=true&serverTimezone=America/Sao_Paulo";
            String user = "root";
            String password = "";
            Connection con = DriverManager.getConnection(url, user, password);
            
        } catch(Exception e) {
            System.err.println("Erro em LowLevel construtor " + e);
        }
    }
    
    public static void main(String[] args) {
        //run();
        
        removerForeignKey("produto", "icms");
        
    }
    
    public static void run() {
        try {
            String url = "jdbc:mysql://" + Ouroboros.SERVER + "/ouroboros?useTimezone=true&serverTimezone=America/Sao_Paulo";
            String user = "root";
            String password = "";
            Connection con = DriverManager.getConnection(url, user, password);
            
            String sql = "select * from constante";
            
            PreparedStatement stmt = con.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                System.out.println(rs.getString("nome") + " = " + rs.getString("valor"));
            }
            
            rs.close();
            stmt.close();
            con.close();
            
        } catch(Exception e) {
            System.err.println("Erro LowLevel.run " + e);
        }
        
    }
    
    public static void removerForeignKey(String tabelaEstrangeira, String tabelaPrimaria) {
        try {
            //adicionei parâmetro de multiQuery
            String url = "jdbc:mysql://" + Ouroboros.SERVER + "/ouroboros?useTimezone=true&serverTimezone=America/Sao_Paulo&allowMultiQueries=true";
            String user = "root";
            String password = "";
            Connection con = DriverManager.getConnection(url, user, password);
            //Localiza o nome da FK e cria a instrução para remover
            String sql = "set @s:='';" +
                "select @s:=concat(@s, 'alter table ', '" + tabelaEstrangeira + "', ' drop foreign key ',CONSTRAINT_NAME, ';')" +
                " from information_schema.key_column_usage" +
                " where CONSTRAINT_SCHEMA = '" + Ouroboros.DATABASE_NAME + "'" +
                "  and TABLE_NAME ='" + tabelaEstrangeira + "'" +
                "  and REFERENCED_TABLE_NAME = '" + tabelaPrimaria + "';" +
                "prepare stmt from @s;" +
                "execute stmt;" +
                "" +
                "deallocate prepare stmt;";
            
            sql = "set @s:='';select @s:=concat(@s, 'alter table ', 'produto', ' drop foreign key ',CONSTRAINT_NAME, ';') from information_schema.key_column_usage where CONSTRAINT_SCHEMA = 'ouroboros'  and TABLE_NAME ='produto'  and REFERENCED_TABLE_NAME = 'icms';prepare stmt from @s;execute stmt;deallocate prepare stmt;";

            System.out.println("sql: " + sql);
            
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.execute();
            
        } catch(Exception e) {
            System.err.println("Erro em LowLevel.removerForeignKey " + e);
        }
        
    }
}
