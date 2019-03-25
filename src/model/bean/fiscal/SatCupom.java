/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.fiscal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import model.bean.principal.Venda;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class SatCupom implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private String chave;
    
    @ManyToOne
    @JoinColumn(name = "vendaId")
    private Venda venda;
    
    @ManyToOne
    @JoinColumn(name = "satCupomTipoId")
    private SatCupomTipo satCupomTipo;

    protected SatCupom(){}
    
    public SatCupom(String chave, Venda venda, SatCupomTipo satCupomTipo) {
        this.chave = chave;
        this.venda = venda;
        this.satCupomTipo = satCupomTipo;
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public LocalDateTime getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(LocalDateTime atualizacao) {
        this.atualizacao = atualizacao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public SatCupomTipo getSatCupomTipo() {
        return satCupomTipo;
    }

    public void setSatCupomTipo(SatCupomTipo satCupomTipo) {
        this.satCupomTipo = satCupomTipo;
    }
    
    
    
    
    //--------------------------------------------------------------------------
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((SatCupom) obj).getId());
    }
}
