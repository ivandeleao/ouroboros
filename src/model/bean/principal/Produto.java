/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.bean.fiscal.UnidadeComercial;
import model.bean.fiscal.ProdutoOrigem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import model.bean.fiscal.Cfop;
import model.bean.fiscal.Icms;
import model.bean.fiscal.Ncm;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "nome")})
public class Produto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    private LocalDateTime exclusao;
    
    private String codigo; //código de barras ou definido pelo usuário
    private String nome;
    private String descricao;
    private BigDecimal valorCompra;
    private BigDecimal margemLucro;
    private BigDecimal valorVenda;

    @OneToMany(mappedBy = "produtoId") //, cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)//, cascade = CascadeType.ALL)
    //@Fetch(FetchMode.SUBSELECT)
    private List<ProdutoComponente> listProdutoComponente = new ArrayList<>();

    @OneToMany(mappedBy = "componenteId") //, cascade = CascadeType.REFRESH) //, cascade = CascadeType.ALL, fetch = FetchType.EAGER)//orphanRemoval = true, fetch = FetchType.EAGER)
    //@Fetch(FetchMode.SUBSELECT)
    private List<ProdutoComponente> listProdutoComponenteReverso = new ArrayList<>();

    @OneToMany(mappedBy = "produto") //, fetch = FetchType.EAGER)
    //@Fetch(FetchMode.SUBSELECT)
    @OrderBy
    private List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "categoriaId", nullable = true)
    private Categoria categoria;

    @Column(columnDefinition = "TEXT")
    private String observacao;
    
    @Column(columnDefinition = "boolean default false")
    private Boolean balanca;
    
    

    //dados fiscais ------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "unidadeComercialVendaId", nullable = true)
    private UnidadeComercial unidadeComercialVenda;

    @ManyToOne
    @JoinColumn(name = "origemId", nullable = true)
    private ProdutoOrigem origem;

    @ManyToOne
    @JoinColumn(name = "cfopSaidaDentroDoEstadoCodigo", nullable = true)
    private Cfop cfopSaidaDentroDoEstado;

    @ManyToOne
    @JoinColumn(name = "cfopSaidaForaDoEstadoCodigo", nullable = true)
    private Cfop cfopSaidaForaDoEstado;

    @ManyToOne
    @JoinColumn(name = "icmsCodigo", nullable = true)
    private Icms icms;

    @ManyToOne
    @JoinColumn(name = "ncmCodigo", nullable = true)
    private Ncm ncm;

    //Apenas o código armazenado pois, não existe mais nenhum dado associado a esta entidade
    private String cest; //Código Especificador da Substituição Tributária.

    private BigDecimal aliquotaIcms;

    
    
    
    
    
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return (codigo != null && !codigo.isEmpty()) ? codigo : (getId() != null) ? getId().toString() : "";
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo != null ? codigo.trim() : "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao != null ? descricao.trim() : "";
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.trim();
    }

    public BigDecimal getValorCompra() {
        return valorCompra != null ? valorCompra : BigDecimal.ZERO;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getMargemLucro() {
        return margemLucro != null ? margemLucro : BigDecimal.ZERO;
    }

    public void setMargemLucro(BigDecimal margemLucro) {
        this.margemLucro = margemLucro;
    }

    public BigDecimal getValorVenda() {
        return valorVenda != null ? valorVenda : BigDecimal.ZERO;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao.trim();
    }

    public UnidadeComercial getUnidadeComercialVenda() {
        return unidadeComercialVenda;
    }

    public void setUnidadeComercialVenda(UnidadeComercial unidadeComercialVenda) {
        this.unidadeComercialVenda = unidadeComercialVenda;
    }

    public ProdutoOrigem getOrigem() {
        return origem;
    }

    public void setOrigem(ProdutoOrigem origem) {
        this.origem = origem;
    }

    public Cfop getCfopSaidaDentroDoEstado() {
        return cfopSaidaDentroDoEstado;
    }

    public void setCfopSaidaDentroDoEstado(Cfop cfopSaidaDentroDoEstado) {
        this.cfopSaidaDentroDoEstado = cfopSaidaDentroDoEstado;
    }

    public Cfop getCfopSaidaForaDoEstado() {
        return cfopSaidaForaDoEstado;
    }

    public void setCfopSaidaForaDoEstado(Cfop cfopSaidaForaDoEstado) {
        this.cfopSaidaForaDoEstado = cfopSaidaForaDoEstado;
    }

    public Icms getIcms() {
        return icms;
    }

    public void setIcms(Icms icms) {
        this.icms = icms;
    }

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms != null ? aliquotaIcms : BigDecimal.ZERO;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public Ncm getNcm() {
        return ncm;
    }

    public void setNcm(Ncm ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public List<MovimentoFisico> getMovimentosFisicos() {
        return listMovimentoFisico;
    }

    public void setMovimentosFisicos(List<MovimentoFisico> listMovimentoFisico) {
        this.listMovimentoFisico = listMovimentoFisico;
    }

    public List<ProdutoComponente> getListProdutoComponente() {
        return listProdutoComponente;
    }

    public void setListProdutoComponente(List<ProdutoComponente> listProdutoComponente) {
        this.listProdutoComponente = listProdutoComponente;
    }

    public List<ProdutoComponente> getListProdutoComponenteReverso() {
        return listProdutoComponenteReverso;
    }

    public void setListProdutoComponenteReverso(List<ProdutoComponente> listProdutoComponenteReverso) {
        this.listProdutoComponenteReverso = listProdutoComponenteReverso;
    }

    public List<MovimentoFisico> getListMovimentoFisico() {
        return listMovimentoFisico;
    }

    public void setListMovimentoFisico(List<MovimentoFisico> listMovimentoFisico) {
        this.listMovimentoFisico = listMovimentoFisico;
    }

    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(Timestamp criacao) {
        this.criacao = criacao;
    }

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public Boolean getBalanca() {
        return balanca;
    }

    public void setBalanca(Boolean balanca) {
        this.balanca = balanca;
    }
    
    
    
    
    
    
    

    //--------------------------------------------------------------------------
    /**
     * 
     * @return lista de produtos que contêm este componente
     */
    public List<Produto> getListProdutoComposto() {
        List<Produto> listProdutoComposto = new ArrayList<>();
        for(ProdutoComponente pc : getListProdutoComponenteReverso()) {
            listProdutoComposto.add(pc.getProduto());
        }
        return listProdutoComposto;
    }
    
    
    public BigDecimal getEstoqueAtual() {
        BigDecimal estoqueAtual = BigDecimal.ZERO;

        /*if (!getMovimentosFisicos().isEmpty()) {
            estoqueAtual = getMovimentosFisicos().get(getMovimentosFisicos().size() - 1).getSaldoAcumulado();
        }*/
        
        if(!getMovimentosFisicos().isEmpty()) {
            //recebido = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
            estoqueAtual = getMovimentosFisicos().stream().map(MovimentoFisico::getSaldoLinear).reduce(BigDecimal::add).get();
        }

        return estoqueAtual;
    }

    public Produto deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Produto) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro em deepClone " + e);
            return null;
        }
    }

}