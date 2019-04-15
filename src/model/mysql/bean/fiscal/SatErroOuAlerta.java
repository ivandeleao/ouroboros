/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.fiscal;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ivand
 */
@Entity
public class SatErroOuAlerta implements Serializable {
    @Id
    @Column(length = 4)
    private String codigo;
    @Column(length = 20)
    private String tipo;
    @Column(length = 2048)
    private String descricao;

    public SatErroOuAlerta() {}
    
    public SatErroOuAlerta(String codigo, String tipo, String descricao) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
