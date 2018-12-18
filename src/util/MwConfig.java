/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ivand
 */
public class MwConfig {
    
    public static String read(String parametro){
        try {
            String valor = "";
            ArrayList lines =  MwIOFile.read("config.txt");

            for(Object line : lines) {
                System.out.println("config: " + line.toString());
                String[] tokens = line.toString().split("=");
                if(parametro.equals(tokens[0])){
                    valor = tokens[1];
                }
            }
            
            return valor;
            
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de configuração. " + e);
        }
        return null;
        
    }
    
    public static void writeFile(List<String> lines){
        MwIOFile.writeFile(lines, "config.txt");
    }
}
