/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
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
public class PrintPDFBox {
    
    public static void print(String filePath, String printerName){
        if(IMPRESSORA_DESATIVAR) {
            new Toast("Impressão desativada. Habilite em configurações do sistema.");
        } else {
            try {
                new Toast("Imprimindo...");
                System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
                PDDocument document = PDDocument.load(new File(filePath));

                PrintService myPrintService = findPrintService(printerName);
                
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPageable(new PDFPageable(document));
                job.setPrintService(myPrintService);
                
                PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
                attrs.add(javax.print.attribute.standard.PrintQuality.HIGH);
                
                job.print();
                document.close();
            } catch (PrinterException | IOException | NullPointerException e) {
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
