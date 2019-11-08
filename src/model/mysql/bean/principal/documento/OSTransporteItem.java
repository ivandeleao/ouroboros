/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import model.mysql.bean.principal.pessoa.Pessoa;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(indexes = {
    @Index(columnList = "criacao"),
    @Index(columnList = "atualizacao")

})
public class OSTransporteItem implements Serializable, Comparable<OSTransporteItem> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    @ManyToOne
    @JoinColumn(name = "osTransporteId")
    private OSTransporte osTransporte;

    private String descricao;

    private BigDecimal valor;

    private BigDecimal motoristaValor;

    private BigDecimal motoristaPercentual;

    private BigDecimal pedagioValor;

    private BigDecimal pedagioPercentual; //do motorista

    private BigDecimal adicionalValor;

    private BigDecimal adicionalPercentual; //do motorista

    @ManyToOne
    @JoinColumn(name = "destinatarioId")
    private Pessoa destinatario;

    private String endereco;

    private String cidade;

    private String uf;

    public OSTransporteItem() {
    }

    public OSTransporteItem(String descricao, BigDecimal valor, BigDecimal motoristaValor, BigDecimal motoristaPercentual,
            BigDecimal pedagioValor, BigDecimal pedagioPercentual, BigDecimal adicionalValor, BigDecimal adicionalPercentual,
            Pessoa destinatario) {

        this.descricao = descricao;
        this.valor = valor;
        this.motoristaValor = motoristaValor;
        this.motoristaPercentual = motoristaPercentual;
        this.pedagioValor = pedagioValor;
        this.pedagioPercentual = pedagioPercentual;
        this.adicionalValor = adicionalValor;
        this.adicionalPercentual = adicionalPercentual;

        //this.total = valor.add(pedagioValor).add(adicionalValor);
        this.destinatario = destinatario;
        this.endereco = destinatario.getEnderecoCompleto();
        this.cidade = destinatario.getMunicipio();
        this.uf = destinatario.getUf();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(Timestamp criacao) {
        this.criacao = criacao;
    }

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Timestamp atualizacao) {
        this.atualizacao = atualizacao;
    }

    public OSTransporte getOsTransporte() {
        return osTransporte;
    }

    public void setOsTransporte(OSTransporte osTransporte) {
        this.osTransporte = osTransporte;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMotoristaValor() {
        return motoristaValor;
    }

    public void setMotoristaValor(BigDecimal motoristaValor) {
        this.motoristaValor = motoristaValor;
    }

    public BigDecimal getMotoristaPercentual() {
        return motoristaPercentual;
    }

    public void setMotoristaPercentual(BigDecimal motoristaPercentual) {
        this.motoristaPercentual = motoristaPercentual;
    }

    public BigDecimal getPedagioValor() {
        return pedagioValor != null ? pedagioValor : BigDecimal.ZERO;
    }

    public void setPedagioValor(BigDecimal pedagioValor) {
        this.pedagioValor = pedagioValor;
    }

    public BigDecimal getPedagioPercentual() {
        return pedagioPercentual;
    }

    public void setPedagioPercentual(BigDecimal pedagioPercentual) {
        this.pedagioPercentual = pedagioPercentual;
    }

    public BigDecimal getAdicionalValor() {
        return adicionalValor != null ? adicionalValor : BigDecimal.ZERO;
    }

    public void setAdicionalValor(BigDecimal adicionalValor) {
        this.adicionalValor = adicionalValor;
    }

    public BigDecimal getAdicionalPercentual() {
        return adicionalPercentual;
    }

    public void setAdicionalPercentual(BigDecimal adicionalPercentual) {
        this.adicionalPercentual = adicionalPercentual;
    }

    public Pessoa getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Pessoa destinatario) {
        this.destinatario = destinatario;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    //--------------------------------------------------------------------------
    public BigDecimal getSubtotal() {
        return getValor().add(getPedagioValor()).add(getAdicionalValor());
    }

    @Override
    public int compareTo(OSTransporteItem ostItem) {
        return id.compareTo(ostItem.getId());
    }
    
    
}
