/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.fiscal;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ivand
 */
@Entity
public class Ibpt implements Serializable {
    @Id
    private Integer codigo;
    private Integer ex;
    private Integer tabela;
    private BigDecimal aliqNac;
    private BigDecimal aliqImp;
    
    
    public Ibpt(){}
    
    public Ibpt(Integer codigo, Integer ex, Integer tabela, BigDecimal aliqNac, BigDecimal aliqImp){
        this.codigo = codigo;
        this.ex = ex;
        this.tabela = tabela;
        this.aliqNac = aliqNac;
        this.aliqImp = aliqImp;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getEx() {
        return ex;
    }

    public void setEx(Integer ex) {
        this.ex = ex;
    }

    public Integer getTabela() {
        return tabela;
    }

    public void setTabela(Integer tabela) {
        this.tabela = tabela;
    }

    public BigDecimal getAliqNac() {
        return aliqNac;
    }

    public void setAliqNac(BigDecimal aliqNac) {
        this.aliqNac = aliqNac;
    }

    public BigDecimal getAliqImp() {
        return aliqImp;
    }

    public void setAliqImp(BigDecimal aliqImp) {
        this.aliqImp = aliqImp;
    }
    
    
}
