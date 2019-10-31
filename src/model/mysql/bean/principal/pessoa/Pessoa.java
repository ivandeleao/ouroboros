/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.pessoa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.FinanceiroStatus;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.endereco.CidadeDAO;
import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.pessoa.PerfilDAO;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.Texto;

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
    
    private LocalDateTime exclusao;
    
    private boolean cliente;
    private boolean fornecedor;
    
    private String nome; //ou razão social
    private String cpf;
    private String rg;
    private Date nascimento;
    
    private String nomeFantasia;
    private String cnpj;
    private String ie;
    private boolean ieIsento;
    private String im;
    private String suframa;
    
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
    
    private BigDecimal limiteCredito;
    
    
    @OneToMany(mappedBy = "cliente") //, fetch = FetchType.LAZY)
    @OrderBy
    private List<Venda> vendaList = new ArrayList<>();
    

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
    private List<Perfil> perfis = new ArrayList<>();
    
    
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

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
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
        this.nome = nome.trim();
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

    public String getIm() {
        return im != null ? im : "";
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getSuframa() {
        return suframa;
    }

    public void setSuframa(String suframa) {
        this.suframa = suframa;
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
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email.trim();
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
        this.endereco = endereco.trim();
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
        return bairro != null ? bairro.trim() : "";
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

    public BigDecimal getLimiteCredito() {
        return limiteCredito != null ? limiteCredito : BigDecimal.ZERO;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    
    public List<Venda> getVendaList() {
        return vendaList;
    }

    public void setVendaList(List<Venda> vendaList) {
        this.vendaList = vendaList;
    }

    public List<Perfil> getPerfis() {
        perfis.sort(Comparator.comparing((perfil) -> perfil.getGrupo().getNome()));
        return perfis;
    }

    public void setPerfis(List<Perfil> perfis) {
        this.perfis = perfis;
    }
    
    
    
    //--------------------------------------------------------------------------
    
    public void addPerfil(Perfil perfil) {
        perfis.remove(perfil);
        perfis.add(perfil);
        perfil.setPessoa(this);
    }
    
    public void removePerfil(Perfil perfil) {
        perfil.setPessoa(null);
        perfis.remove(perfil);
    }
    
    public String getCepSoNumeros() {
        return Texto.soNumeros(getCep());
    }
    
    /**
     * 
     * @return CPF ou CNPJ ou String vazia
     */
    public String getCpfOuCnpj() {
        if(getCpf().length() > 0) {
            return getCpf();
        } else if (getCnpj().length() > 0) {
            return getCnpj();
        } else {
            return "";
        }
    }
    
    public String getCpfOuCnpjSoNumeros() {
        return Texto.soNumeros(getCpfOuCnpj());
    }
    
    public String getMunicipio() {
        if(getCodigoMunicipio().isEmpty()) {
            return "";
        }
        
        CidadeDAO cidadeDAO = new CidadeDAO();
        Cidade cidade = cidadeDAO.findByCodigoIbge(getCodigoMunicipio());
        
        return cidade != null ? cidade.getNome() : "";
    }
    
    public String getUf() {
        if(getCodigoMunicipio().isEmpty()) {
            return "";
        }
        
        CidadeDAO cidadeDAO = new CidadeDAO();
        Cidade cidade = cidadeDAO.findByCodigoIbge(getCodigoMunicipio());
        
        return cidade != null ? cidade.getEstado().getSigla() : "";
    }
    
    public String getEnderecoCompleto() {
        String enderecoCompleto = getEndereco();
        
        if(enderecoCompleto.isEmpty()) {
            return "";
        }
        
        if(!getNumero().isEmpty()) {
            enderecoCompleto += ", " + getNumero();
        }
        if(!getBairro().isEmpty()) {
            enderecoCompleto += ", " + getBairro();
        }
        
        return enderecoCompleto;
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
    
    public BigDecimal getTotalEmAtraso() {
        List<FinanceiroStatus> listStatus = new ArrayList<>();
        listStatus.add(FinanceiroStatus.VENCIDO);
        
        List<Parcela> parcelas = new ParcelaDAO().findPorStatus(this, listStatus, null, null, TipoOperacao.SAIDA);
        
        if(!parcelas.isEmpty()) {
            return parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * 
     * @return soma das parcelas em aberto e vencidas
     */
    public BigDecimal getTotalComprometido() {
        List<FinanceiroStatus> listStatus = new ArrayList<>();
        listStatus.add(FinanceiroStatus.VENCIDO);
        listStatus.add(FinanceiroStatus.ABERTO);
        
        List<Parcela> parcelas = new ParcelaDAO().findPorStatus(this, listStatus, null, null, TipoOperacao.SAIDA);
        
        if(!parcelas.isEmpty()) {
            return parcelas.stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        }
        
        return BigDecimal.ZERO;
    }
    
}
