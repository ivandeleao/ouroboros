/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe.bean;

/**
 *
 * @author ivand
 */
public class Emit {

    private String cnpj;
    private String xNome;
    private String xFant;

    private EnderEmit enderEmit;

    private String ie;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getxNome() {
        return xNome;
    }

    public void setxNome(String xNome) {
        this.xNome = xNome;
    }

    public String getxFant() {
        return xFant;
    }

    public void setxFant(String xFant) {
        this.xFant = xFant;
    }

    public EnderEmit getEnderEmit() {
        return enderEmit;
    }

    public void setEnderEmit(EnderEmit enderEmit) {
        this.enderEmit = enderEmit;
    }

    public String getIe() {
        return ie;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

}
