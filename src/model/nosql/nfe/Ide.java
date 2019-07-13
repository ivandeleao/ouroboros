/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql.nfe;

import java.math.BigDecimal;
import java.util.List;
import model.mysql.bean.principal.catalogo.Produto;

/**
 *
 * @author ivand
 */
public class Ide {
    private String cUF;
    private String cNF;
    private String natOp;
    private String mod;
    private String serie;
    private String nNF;
    private String dhEmi;
    private String dhSaiEnt;
    

    public String getcUF() {
        return cUF;
    }

    public void setcUF(String cUF) {
        this.cUF = cUF;
    }

    public String getcNF() {
        return cNF;
    }

    public void setcNF(String cNF) {
        this.cNF = cNF;
    }

    public String getNatOp() {
        return natOp;
    }

    public void setNatOp(String natOp) {
        this.natOp = natOp;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getnNF() {
        return nNF;
    }

    public void setnNF(String nNF) {
        this.nNF = nNF;
    }

    public String getDhEmi() {
        return dhEmi;
    }

    public void setDhEmi(String dhEmi) {
        this.dhEmi = dhEmi;
    }

    public String getDhSaiEnt() {
        return dhSaiEnt;
    }

    public void setDhSaiEnt(String dhSaiEnt) {
        this.dhSaiEnt = dhSaiEnt;
    }

    
    

}
