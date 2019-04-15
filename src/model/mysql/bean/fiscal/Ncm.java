package model.mysql.bean.fiscal;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ivand
 * Fonte: http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=Iy/5Qol1YbE=
 * NCM pode ser cadastrado com código genérico no produto
 * Caso não exista nesta tabela, deve ser adicionado via banco
 */
@Entity
public class Ncm implements Serializable {
    @Id
    private String codigo;
    @Column(length = 500)
    private String descricao;
    private String ipi;
    private Date inicioVigencia;
    private Date fimVigencia;
    //private UnidadeComercial uTrib; ??
    
    
    private Ncm(){}
    
    public Ncm(String codigo, String descricao, String ipi, Date inicioVigencia, Date fimVigencia){
        this.codigo = codigo;
        this.descricao = descricao;
        this.ipi = ipi;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
    }
    
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIpi() {
        return ipi;
    }

    public void setIpi(String ipi) {
        this.ipi = ipi;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getCodigo(), ((Ncm) obj).getCodigo());
    }
}
