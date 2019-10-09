/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.fiscal;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import model.mysql.dao.fiscal.IbptDAO;

/**
 *
 * @author ivand
 */
@Entity
public class Ibpt implements Serializable {
    @Id
    private Integer codigo;
    private Integer ex;
    private BigDecimal aliqNac;
    private BigDecimal aliqImp;
    private BigDecimal aliqEst;
    
    
    public Ibpt(){}
    
    public Ibpt(Integer codigo, Integer ex, BigDecimal aliqNac, BigDecimal aliqImp, BigDecimal aliqEst){
        this.codigo = codigo;
        this.ex = ex;
        this.aliqNac = aliqNac;
        this.aliqImp = aliqImp;
        this.aliqEst = aliqEst;
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

    public BigDecimal getAliqEst() {
        return aliqEst;
    }

    public void setAliqEst(BigDecimal aliqEst) {
        this.aliqEst = aliqEst;
    }
    
}
