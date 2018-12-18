/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author ivand
 */
public class MwIOFile {
    
    private static final Charset utf8 = StandardCharsets.UTF_8;
    
    public static void writeLine(String dados, String caminho){
        Path path = Paths.get(caminho);

        try(BufferedWriter w = Files.newBufferedWriter(path, utf8)){
            w.write(dados);
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public static void writeFile(List<String> lines, String caminho){
        Path path = Paths.get(caminho);

        try(BufferedWriter w = Files.newBufferedWriter(path, utf8)){
            for(String line : lines){
                w.write(line + "\r\n");
            }
            w.flush();
            w.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public static ArrayList<String> read(String caminho) throws IOException{
        Path path = Paths.get(caminho);
        
        ArrayList<String> lines = new ArrayList();
        try(BufferedReader reader = Files.newBufferedReader(path, utf8)) {
            String line = null;
            while((line = reader.readLine()) != null) {
                //System.out.println(line);
                lines.add(line);
            }
        } catch(IOException e) {
            System.out.println(e);
        }
        
        return lines;
    }
    
    public static String readFullContent(String caminho){
        try {
            File file = new File(caminho);
            FileInputStream fis;
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return new String(data, "UTF-8");
        } catch (IOException ex) {
            System.err.println("Erro ao ler arquivo: " + ex);
        }
        
        return null;
    }
    
    
    public static Optional<File> findAFile(String path){
        File file = new File(path);
        File[] arquivos = file.listFiles();

        Optional<File> optFile = Optional.empty();

        if(arquivos != null){
            for (File arquivo : arquivos) {
                if(arquivo.isFile()){
                    optFile = Optional.of(arquivo);
                    break;
                }

            }
        }
        return optFile;
    }
}
