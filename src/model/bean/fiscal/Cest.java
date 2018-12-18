
package model.bean.fiscal;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Site oficial: https://www.confaz.fazenda.gov.br/legislacao/convenios/2017/CV052_17#wrapper
 * @author ivand
 */
@Entity
public class Cest implements Serializable{
    @Id
    private Integer id; //campo irrelevante - adicionado apenas por compatibilidade com o JPA
    private String codigo; //codigo e ncm possuem valores repetidos. Relação N-N
    private String ncm;
    @Column(length = 500)
    private String descricao;

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((Cest) obj).getId());
    }
}
