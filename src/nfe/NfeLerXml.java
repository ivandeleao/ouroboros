/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.catalogo.ProdutoFornecedorDAO;
import model.mysql.dao.principal.pessoa.PessoaDAO;
import nfe.bean.Cobr;
import nfe.bean.Det;
import nfe.bean.Dup;
import nfe.bean.Emit;
import nfe.bean.EnderEmit;
import nfe.bean.Ide;
import nfe.bean.InfNFe;
import nfe.bean.NFe;
import nfe.bean.Prod;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import util.MwXML;

/**
 *
 * @author ivand
 */
public class NfeLerXml {

    public static NFe importarNFe(String xmlFilePath) {
        
        NFe nfe = new NFe();
        
        InfNFe infNFe = new InfNFe();
        
        Ide ide = importarIde(xmlFilePath);
        infNFe.setIde(ide);
        
        Emit emit = importarEmit(xmlFilePath);
        infNFe.setEmit(emit);
        
        List<Det> dets = importarDets(xmlFilePath, emit);
        infNFe.setDets(dets);
        
        Cobr cobr = importarCobr(xmlFilePath);
        infNFe.setCobr(cobr);
        
        nfe.setInfNFe(infNFe);
        
        return nfe;
    }

    private static Ide importarIde(String xmlFilePath) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);

            Ide ide = new Ide();

            ide.setcUF(MwXML.getValue(doc, "ide", "cUF"));
            ide.setcNF(MwXML.getValue(doc, "ide", "cNF"));
            ide.setNatOp(MwXML.getValue(doc, "ide", "natOp"));
            ide.setMod(MwXML.getValue(doc, "ide", "mod"));
            ide.setSerie(MwXML.getValue(doc, "ide", "serie"));
            ide.setnNF(MwXML.getValue(doc, "ide", "nNF"));
            ide.setDhEmi(MwXML.getValue(doc, "ide", "dhEmi"));
            ide.setDhSaiEnt(MwXML.getValue(doc, "ide", "dhSaiEnt"));

            return ide;

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Erro ao importarIde. " + e);
        }

        return null;
    }
    
    private static Emit importarEmit(String xmlFilePath) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);
            
            Emit emit = new Emit();
            
            emit.setCnpj(MwXML.getValue(doc, "emit", "CNPJ"));
            emit.setxFant(MwXML.getValue(doc, "emit", "xFant"));
            emit.setxNome(MwXML.getValue(doc, "emit", "xNome"));
            emit.setIe(MwXML.getValue(doc, "emit", "IE"));
            
            EnderEmit enderEmit = new EnderEmit();
            
            enderEmit.setxLgr(MwXML.getValue(doc, "enderEmit", "xLgr"));
            enderEmit.setNro(MwXML.getValue(doc, "enderEmit", "nro"));
            enderEmit.setxBairro(MwXML.getValue(doc, "enderEmit", "xBairro"));
            enderEmit.setcMun(MwXML.getValue(doc, "enderEmit", "cMun"));
            enderEmit.setxMun(MwXML.getValue(doc, "enderEmit", "xMun"));
            enderEmit.setUf(MwXML.getValue(doc, "enderEmit", "UF"));
            enderEmit.setCep(MwXML.getValue(doc, "enderEmit", "CEP"));
            enderEmit.setcPais(MwXML.getValue(doc, "enderEmit", "cPais"));
            enderEmit.setxPais(MwXML.getValue(doc, "enderEmit", "xPais"));
            enderEmit.setFone(MwXML.getValue(doc, "enderEmit", "fone"));

            emit.setEnderEmit(enderEmit);
            
            return emit;

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Erro ao importarEmitente. " + e);
        }

        return null;
    }

    private static List<Det> importarDets(String xmlFilePath, Emit emit) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);

            List<Map<String, String>> xmlProds = MwXML.getPairs(doc, "prod");
            List<Det> dets = new ArrayList<>();

            for (int x = 0; x < xmlProds.size(); x++) {

                Map<String, String> xmlProd = xmlProds.get(x);

                Prod prod = new Prod();

                prod.setcProd(xmlProd.get("cProd"));
                prod.setxProd(xmlProd.get("xProd"));
                prod.setNcm(xmlProd.get("NCM"));
                prod.setCest(xmlProd.get("CEST"));
                prod.setuCom(xmlProd.get("uCom"));
                prod.setqCom(xmlProd.get("qCom"));
                prod.setvUnCom(xmlProd.get("vUnCom"));
                prod.setvFrete(xmlProd.get("vFrete"));
                prod.setvSeg(xmlProd.get("vSeg"));
                prod.setvDesc(xmlProd.get("vDesc"));
                prod.setvOutro(xmlProd.get("vOutro"));
                prod.setIndTot(xmlProd.get("vIndTot"));
                
                System.out.println("ncm: " + xmlProd.get("NCM"));
                
                Pessoa fornecedor = new PessoaDAO().findByCpfCnpj(emit.getCnpj());
                if(ProdutoFornecedorDAO.getByFornecedor(fornecedor, prod.getcProd()) != null) {
                    prod.setProduto(ProdutoFornecedorDAO.getByFornecedor(fornecedor, prod.getcProd()).getProduto());
                }
                
                Det det = new Det();
                det.setProd(prod);

                System.out.println("xProd: " + xmlProd.get("xProd"));
                
                dets.add(det);

            }

            return dets;

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Erro ao importarDets. " + e);
        }

        return null;

    }
    
    private static Cobr importarCobr(String xmlFilePath) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = dbf.newDocumentBuilder();
            File f = new File(xmlFilePath);
            Document doc = builder.parse(f);

            List<Map<String, String>> xmlDups = MwXML.getPairs(doc, "dup");
            List<Dup> dups = new ArrayList<>();

            for (int x = 0; x < xmlDups.size(); x++) {

                Map<String, String> xmlDup = xmlDups.get(x);

                Dup dup = new Dup();

                dup.setnDup(xmlDup.get("nDup"));
                dup.setdVenc(xmlDup.get("dVenc"));
                dup.setvDup(xmlDup.get("vDup"));
                
                
                dups.add(dup);

            }
            
            Cobr cobr = new Cobr();
            cobr.setDups(dups);

            return cobr;

        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("Erro ao importarCobr. " + e);
        }

        return null;

    }
}
