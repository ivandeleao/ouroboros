/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author ivand
 */
public class MwConfig {
    static String filePath = "config.txt";
    
    public static String getValue(String key) {
        Properties props = new Properties();
        try {
            FileInputStream file = new FileInputStream(filePath);
            props.load(file);
            file.close();
            
        } catch(IOException e) {
            System.out.println("Erro em MwConfig.getValue " + e);
        }
        return props.getProperty(key);
    }

    public static void setValue(String key, String value) {
        Properties props = new Properties();
        try (FileInputStream file = new FileInputStream(filePath)) {
            props.load(file);
            props.setProperty(key, value);
            
            OutputStream output = new FileOutputStream(filePath);
            props.store(output, null);
        } catch (IOException e) {
            System.err.println("Erro em MwConfig.setValue " + e);
        }
    }
}
