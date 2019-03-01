/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import static ouroboros.Ouroboros.IMPRESSORA_DESATIVAR;
import static ouroboros.Ouroboros.MAIN_VIEW;
import view.Toast;
/**
 *
 * @author ivand
 */
public class PrintString {
    
    public static void print(String string, String printerName){
        if(IMPRESSORA_DESATIVAR) {
            new Toast("Impressão desativada. Habilite em configurações do sistema.");
        } else {
            try {
                //System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
                
                PrintService service = findPrintService(printerName);
                
                //InputStream is = new ByteArrayInputStream("hello world!\f".getBytes("UTF8"));
                InputStream is = new ByteArrayInputStream(string.getBytes("UTF8"));
                
                PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
                pras.add(new Copies(1));

                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                Doc doc = new SimpleDoc(is, flavor, null);
                DocPrintJob job = service.createPrintJob();
                
                job.print(doc, pras);
                is.close();
                
            } catch (Exception e) {
                System.err.println("Erro ao imprimir. " + e);
                JOptionPane.showMessageDialog(MAIN_VIEW, "Erro ao imprimir. "  + e, "Erro", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        System.out.println("Available printers: " + Arrays.asList(printServices));
        return null;
    }
}
