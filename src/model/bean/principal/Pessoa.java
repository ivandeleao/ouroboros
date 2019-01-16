/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "nome")})
public class Pessoa implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    private boolean cliente;
    private boolean fornecedor;
    
    public String nome; //ou razão social
    private String cpf;
    private String rg;
    private Date nascimento;
    
    private String nomeFantasia;
    private String cnpj;
    private String ie;
    private boolean ieIsento;
    
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
    
    private String responsavelNome;
    private String responsavelCpf;
    private String responsavelRg;
    private LocalDate responsavelNascimento;
    private String responsavelEmail;
    private String responsavelParentesco;
    
    
    
    @OneToMany(mappedBy = "cliente") //, fetch = FetchType.LAZY)
    @OrderBy
    private List<Venda> vendaList = new ArrayList<>();
    
    
    public Pessoa() {}
    
    public Pessoa(String nome, String cpf, String rg, Date nascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.nascimento = nascimento;
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

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public boolean isCliente() {
        return cliente;
    }

    public void setCliente(boolean cliente) {
        this.cliente = cliente;
    }

    public boolean isFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(boolean fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf != null ? cpf : "";
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg != null ? rg : "";
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

    public String getNomeFantasia() {
        return nomeFantasia != null ? nomeFantasia : "";
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj != null ? cnpj : "";
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getIe() {
        return ie != null ? ie : "";
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public boolean isIeIsento() {
        return ieIsento;
    }

    public void setIeIsento(boolean ieIsento) {
        this.ieIsento = ieIsento;
    }

    public String getTelefone1() {
        return telefone1 != null ? telefone1 : "";
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2 != null ? telefone2 : "";
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
        return cep != null ? cep : "";
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco != null ? endereco : "";
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero != null ? numero : "";
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento != null ? complemento : "";
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro != null ? bairro : "";
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

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getResponsavelCpf() {
        return responsavelCpf;
    }

    public void setResponsavelCpf(String responsavelCpf) {
        this.responsavelCpf = responsavelCpf;
    }

    public String getResponsavelRg() {
        return responsavelRg;
    }

    public void setResponsavelRg(String responsavelRg) {
        this.responsavelRg = responsavelRg;
    }

    public LocalDate getResponsavelNascimento() {
        return responsavelNascimento;
    }

    public void setResponsavelNascimento(LocalDate responsavelNascimento) {
        this.responsavelNascimento = responsavelNascimento;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public String getResponsavelParentesco() {
        return responsavelParentesco;
    }

    public void setResponsavelParentesco(String responsavelParentesco) {
        this.responsavelParentesco = responsavelParentesco;
    }

    public List<Venda> getVendaList() {
        return vendaList;
    }

    public void setVendaList(List<Venda> vendaList) {
        this.vendaList = vendaList;
    }
    
    //--------------------------------------------------------------------------
    
    public String getCpfOuCnpj() {
        if(getCpf().length() > 0) {
            return getCpf();
        } else if (getCnpj().length() > 0) {
            return getCnpj();
        } else {
            return "";
        }
    }
    
    public String getEnderecoCompleto() {
        return getEndereco() + ", " + getNumero() + " - " + getBairro();
    }
    
    public List<Parcela> getParcelaList() {
        List<Parcela> parcelas = new ArrayList<>();
        
        for (Venda venda : getVendaList()) {
            parcelas.addAll(venda.getParcelas());
        }
        return parcelas;
    }
    
    public List<Parcela> getParcelaListAPrazo() {
        List<Parcela> parcelas = new ArrayList<>();
        
        for (Venda venda : getVendaList()) {
            parcelas.addAll(venda.getParcelasAPrazo());
        }
        parcelas.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelas;
    }
    
}
