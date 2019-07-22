/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe.bean;

import java.math.BigDecimal;
import model.mysql.bean.principal.catalogo.Produto;

/**
 *
 * @author ivand
 */
public class Prod {

    private String cProd;
    private String xProd;
    private String ncm;
    private String cest;
    private String uCom;
    private String qCom;
    private String vUnCom;
    private String vFrete;
    private String vSeg;
    private String vDesc;
    private String vOutro;
    private String indTot; //0=Valor do item (vProd) não compõe o valor total da NF-e 1=Valor do item (vProd) compõe o valor total da NF-e (vProd) (v2.0)

    private Produto produto; //se existir vinculação

    public String getcProd() {
        return cProd;
    }

    public void setcProd(String cProd) {
        this.cProd = cProd;
    }

    /**
     * 
     * @return Descrição do produto ou serviço
     */
    public String getxProd() {
        return xProd;
    }

    public void setxProd(String xProd) {
        this.xProd = xProd;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public String getuCom() {
        return uCom;
    }

    public void setuCom(String uCom) {
        this.uCom = uCom;
    }

    public String getqCom() {
        return qCom;
    }

    public void setqCom(String qCom) {
        this.qCom = qCom;
    }

    /**
     * 
     * @return Valor unitário de comercialização
     */
    public String getvUnCom() {
        return vUnCom;
    }

    public void setvUnCom(String vUnCom) {
        this.vUnCom = vUnCom;
    }

    public String getvFrete() {
        return vFrete;
    }

    public void setvFrete(String vFrete) {
        this.vFrete = vFrete;
    }

    public String getvSeg() {
        return vSeg;
    }

    public void setvSeg(String vSeg) {
        this.vSeg = vSeg;
    }

    public String getvDesc() {
        return vDesc;
    }

    public void setvDesc(String vDesc) {
        this.vDesc = vDesc;
    }

    public String getvOutro() {
        return vOutro;
    }

    public void setvOutro(String vOutro) {
        this.vOutro = vOutro;
    }

    /**
     * 
     * @return 0=Valor do item (vProd) não compõe o valor total da NF-e 1=Valor do item (vProd) compõe o valor total da NF-e (vProd) (v2.0)
     */
    public String getIndTot() {
        return indTot;
    }

    public void setIndTot(String indTot) {
        this.indTot = indTot;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    //--------------------------------------------------------------------------
    public boolean isVinculado() {
        return getProduto() != null;
    }
}
