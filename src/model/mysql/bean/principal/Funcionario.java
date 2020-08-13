/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import model.nosql.TipoCalculoEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "nome")})
public class Funcionario implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private LocalDateTime exclusao;
    
    
    private String nome;
    private String cpf;
    private String rg;
    private Date nascimento;
    
    private String telefone1;
    private String telefone2;
    private String telefoneRecado;
    private String contato;
    
    private String email;
    
    private String cep;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String codigoMunicipio;
    
    private String observacao;
    
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoDocumentoProdutoMonetario;
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoDocumentoProdutoPercentual;
    
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoDocumentoServicoMonetario;
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoDocumentoServicoPercentual;
    
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoItemProdutoMonetario;
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoItemProdutoPercentual;
    
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoItemServicoMonetario;
    @Column(columnDefinition = "decimal(5,3) default 0", nullable = false)
    private BigDecimal comissaoItemServicoPercentual;
    
    
    public Funcionario() {}

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

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getTelefoneRecado() {
        return telefoneRecado;
    }

    public void setTelefoneRecado(String telefoneRecado) {
        this.telefoneRecado = telefoneRecado;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getComissaoDocumentoProdutoMonetario() {
        return comissaoDocumentoProdutoMonetario;
    }

    public void setComissaoDocumentoProdutoMonetario(BigDecimal comissaoDocumentoProdutoMonetario) {
        this.comissaoDocumentoProdutoMonetario = comissaoDocumentoProdutoMonetario;
    }

    public BigDecimal getComissaoDocumentoProdutoPercentual() {
        return comissaoDocumentoProdutoPercentual;
    }

    public void setComissaoDocumentoProdutoPercentual(BigDecimal comissaoDocumentoProdutoPercentual) {
        this.comissaoDocumentoProdutoPercentual = comissaoDocumentoProdutoPercentual;
    }

    public BigDecimal getComissaoDocumentoServicoMonetario() {
        return comissaoDocumentoServicoMonetario;
    }

    public void setComissaoDocumentoServicoMonetario(BigDecimal comissaoDocumentoServicoMonetario) {
        this.comissaoDocumentoServicoMonetario = comissaoDocumentoServicoMonetario;
    }

    public BigDecimal getComissaoDocumentoServicoPercentual() {
        return comissaoDocumentoServicoPercentual;
    }

    public void setComissaoDocumentoServicoPercentual(BigDecimal comissaoDocumentoServicoPercentual) {
        this.comissaoDocumentoServicoPercentual = comissaoDocumentoServicoPercentual;
    }

    public BigDecimal getComissaoItemProdutoMonetario() {
        return comissaoItemProdutoMonetario;
    }

    public void setComissaoItemProdutoMonetario(BigDecimal comissaoItemProdutoMonetario) {
        this.comissaoItemProdutoMonetario = comissaoItemProdutoMonetario;
    }

    public BigDecimal getComissaoItemProdutoPercentual() {
        return comissaoItemProdutoPercentual;
    }

    public void setComissaoItemProdutoPercentual(BigDecimal comissaoItemProdutoPercentual) {
        this.comissaoItemProdutoPercentual = comissaoItemProdutoPercentual;
    }

    public BigDecimal getComissaoItemServicoMonetario() {
        return comissaoItemServicoMonetario;
    }

    public void setComissaoItemServicoMonetario(BigDecimal comissaoItemServicoMonetario) {
        this.comissaoItemServicoMonetario = comissaoItemServicoMonetario;
    }

    public BigDecimal getComissaoItemServicoPercentual() {
        return comissaoItemServicoPercentual;
    }

    public void setComissaoItemServicoPercentual(BigDecimal comissaoItemServicoPercentual) {
        this.comissaoItemServicoPercentual = comissaoItemServicoPercentual;
    }
    
    
    //Facilitadores ------------------------------------------------------------
    
    public BigDecimal getComissaoDocumentoProduto() {
        return getComissaoDocumentoProdutoTipo().equals(TipoCalculoEnum.VALOR) ? 
                getComissaoDocumentoProdutoMonetario() : getComissaoDocumentoProdutoPercentual();
    }
    
    public BigDecimal getComissaoDocumentoServico() {
        return getComissaoDocumentoServicoTipo().equals(TipoCalculoEnum.VALOR) ? 
                getComissaoDocumentoServicoMonetario() : getComissaoDocumentoServicoPercentual();
    }
    
    public BigDecimal getComissaoItemProduto() {
        return getComissaoItemProdutoTipo().equals(TipoCalculoEnum.VALOR) ? 
                getComissaoItemProdutoMonetario() : getComissaoItemProdutoPercentual();
    }
    
    public BigDecimal getComissaoItemServico() {
        return getComissaoItemServicoTipo().equals(TipoCalculoEnum.VALOR) ? 
                getComissaoItemServicoMonetario() : getComissaoItemServicoPercentual();
    }
    
    
    public void setComissaoDocumentoProduto(BigDecimal valor, TipoCalculoEnum tipo) {
        if (tipo.equals(TipoCalculoEnum.VALOR)) {
            setComissaoDocumentoProdutoMonetario(valor);
            setComissaoDocumentoProdutoPercentual(BigDecimal.ZERO);
            
        } else {
            setComissaoDocumentoProdutoMonetario(BigDecimal.ZERO);
            setComissaoDocumentoProdutoPercentual(valor);
        }
    }
    
    public void setComissaoDocumentoServico(BigDecimal valor, TipoCalculoEnum tipo) {
        if (tipo.equals(TipoCalculoEnum.VALOR)) {
            setComissaoDocumentoServicoMonetario(valor);
            setComissaoDocumentoServicoPercentual(BigDecimal.ZERO);
            
        } else {
            setComissaoDocumentoServicoMonetario(BigDecimal.ZERO);
            setComissaoDocumentoServicoPercentual(valor);
        }
    }
    
    public void setComissaoItemProduto(BigDecimal valor, TipoCalculoEnum tipo) {
        if (tipo.equals(TipoCalculoEnum.VALOR)) {
            setComissaoItemProdutoMonetario(valor);
            setComissaoItemProdutoPercentual(BigDecimal.ZERO);
            
        } else {
            setComissaoItemProdutoMonetario(BigDecimal.ZERO);
            setComissaoItemProdutoPercentual(valor);
        }
    }
    
    public void setComissaoItemServico(BigDecimal valor, TipoCalculoEnum tipo) {
        if (tipo.equals(TipoCalculoEnum.VALOR)) {
            setComissaoItemServicoMonetario(valor);
            setComissaoItemServicoPercentual(BigDecimal.ZERO);
            
        } else {
            setComissaoItemServicoMonetario(BigDecimal.ZERO);
            setComissaoItemServicoPercentual(valor);
        }
    }
    
    
    public TipoCalculoEnum getComissaoDocumentoProdutoTipo() {
        return getComissaoDocumentoProdutoMonetario().compareTo(BigDecimal.ZERO) > 0 ? TipoCalculoEnum.VALOR : TipoCalculoEnum.PERCENTUAL;
    }
    
    public TipoCalculoEnum getComissaoDocumentoServicoTipo() {
        return getComissaoDocumentoServicoMonetario().compareTo(BigDecimal.ZERO) > 0 ? TipoCalculoEnum.VALOR : TipoCalculoEnum.PERCENTUAL;
    }
    
    public TipoCalculoEnum getComissaoItemProdutoTipo() {
        return getComissaoItemProdutoMonetario().compareTo(BigDecimal.ZERO) > 0 ? TipoCalculoEnum.VALOR : TipoCalculoEnum.PERCENTUAL;
    }
    
    public TipoCalculoEnum getComissaoItemServicoTipo() {
        return getComissaoItemServicoMonetario().compareTo(BigDecimal.ZERO) > 0 ? TipoCalculoEnum.VALOR : TipoCalculoEnum.PERCENTUAL;
    }
    
    
    public String getEnderecoCompleto() {
        return getEndereco() + ", " + getNumero() + " - " + getBairro();
    }
    
    //Fim Facilitadores --------------------------------------------------------
    
    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Funcionario other = (Funcionario) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}
