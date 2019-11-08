/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import model.mysql.bean.principal.pessoa.Pessoa;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.Veiculo;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "criacao")
})
public class OSTransporte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    @ManyToOne
    @JoinColumn(name = "remetenteId")
    private Pessoa remetente;
    
    @ManyToOne
    @JoinColumn(name = "motoristaId")
    private Funcionario motorista;
    
    private String solicitanteNome;
    private String solicitanteSetor;

    @ManyToOne
    @JoinColumn(name = "veiculoId")
    private Veiculo veiculo;

    private LocalDateTime cancelamento;
    private String motivoCancelamento;

    private BigDecimal descontoMonetario;
    private BigDecimal descontoPercentual;
    
    private BigDecimal total; //cache


    @OneToMany(mappedBy = "osTransporte", cascade = CascadeType.ALL, orphanRemoval = true)
    //@OrderBy("id")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<OSTransporteItem> osTranporteItens = new ArrayList<>();


    @Column(length = 1000)
    private String observacao;
    
    public OSTransporte() {
        
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

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Timestamp atualizacao) {
        this.atualizacao = atualizacao;
    }

    public Pessoa getRemetente() {
        return remetente;
    }

    public void setRemetente(Pessoa remetente) {
        this.remetente = remetente;
    }

    public Funcionario getMotorista() {
        return motorista;
    }

    public void setMotorista(Funcionario motorista) {
        this.motorista = motorista;
    }

    public String getSolicitanteNome() {
        return solicitanteNome;
    }

    public void setSolicitanteNome(String solicitanteNome) {
        this.solicitanteNome = solicitanteNome;
    }

    public String getSolicitanteSetor() {
        return solicitanteSetor;
    }

    public void setSolicitanteSetor(String solicitanteSetor) {
        this.solicitanteSetor = solicitanteSetor;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public LocalDateTime getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(LocalDateTime cancelamento) {
        this.cancelamento = cancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public BigDecimal getDescontoMonetario() {
        return descontoMonetario != null ? descontoMonetario : BigDecimal.ZERO;
    }

    public void setDescontoMonetario(BigDecimal descontoMonetario) {
        this.descontoMonetario = descontoMonetario;
    }

    public BigDecimal getDescontoPercentual() {
        return descontoPercentual != null ? descontoPercentual : BigDecimal.ZERO;
    }

    public void setDescontoPercentual(BigDecimal descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal() {
        this.total = getTotalItens().subtract(getDesconto());
    }

    public List<OSTransporteItem> getOsTranporteItens() {
        //Collections.sort(osTranporteItens);
        return osTranporteItens;
    }

    public void setOsTranporteItens(List<OSTransporteItem> osTranporteItens) {
        this.osTranporteItens = osTranporteItens;
    }


    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    //--------------------------------------------------------------------------
    
    public BigDecimal getDesconto() {
        return getDescontoMonetario().add(getDescontoPercentualEmMonetario());
    }
    
    public BigDecimal getDescontoPercentualEmMonetario() {
        return getTotalItens().multiply(getDescontoPercentual().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
    }
    
    public BigDecimal getTotalItens() {
        if(getOsTranporteItens().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getOsTranporteItens().stream().map(OSTransporteItem::getSubtotal).reduce(BigDecimal::add).get();
    }
    
    public void addOSTransporteItem(OSTransporteItem ostItem) {
        osTranporteItens.remove(ostItem);
        osTranporteItens.add(ostItem);
        ostItem.setOsTransporte(this);
    }

    public void removeOSTransporteItem(OSTransporteItem ostItem) {
        ostItem.setOsTransporte(null);
        osTranporteItens.remove(ostItem);
    }
}
