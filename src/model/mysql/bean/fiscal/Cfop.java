/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.fiscal;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ivand
 *
 * sobre Cfop:
 * https://www.oobj.com.br/bc/article/o-que-%C3%A9-cfop-c%C3%B3digo-fiscal-de-opera%C3%A7%C3%B5es-e-presta%C3%A7%C3%B5es-51.html
 *
 */
@Entity
public class Cfop implements Serializable {

    @Id
    private Integer codigo;
    @Column(length = 500)
    private String descricao;
    private boolean indNFe;
    private boolean indComunica;
    private boolean indTransp;
    private boolean indDevol;

    public Cfop() {
    }

    public Cfop(Integer codigo, String descricao, boolean indNFe, boolean indComunica, boolean indTransp, boolean indDevol) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.indNFe = indNFe;
        this.indComunica = indComunica;
        this.indTransp = indTransp;
        this.indDevol = indDevol;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isIndNFe() {
        return indNFe;
    }

    public void setIndNFe(boolean indNFe) {
        this.indNFe = indNFe;
    }

    public boolean isIndComunica() {
        return indComunica;
    }

    public void setIndComunica(boolean indComunica) {
        this.indComunica = indComunica;
    }

    public boolean isIndTransp() {
        return indTransp;
    }

    public void setIndTransp(boolean indTransp) {
        this.indTransp = indTransp;
    }

    public boolean isIndDevol() {
        return indDevol;
    }

    public void setIndDevol(boolean indDevol) {
        this.indDevol = indDevol;
    }

    @Override
    public String toString() {
        return getCodigo() + " " + getDescricao();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getCodigo(), ((Cfop) obj).getCodigo());
    }
}
