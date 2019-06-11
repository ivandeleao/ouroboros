/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.fiscal.ProdutoOrigem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.principal.MovimentoFisico;
import org.hibernate.annotations.CreationTimestamp;
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
    private String outrosCodigos;
    private String localizacao;

    @OneToMany(mappedBy = "produtoId", cascade = CascadeType.ALL) //, cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)//, cascade = CascadeType.ALL)
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

    //NCM pode ser cadastrado com código genérico no produto
    //Caso não exista nesta tabela, deve ser adicionado via banco
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

    public String getOutrosCodigos() {
        return outrosCodigos;
    }

    public void setOutrosCodigos(String outrosCodigos) {
        this.outrosCodigos = outrosCodigos;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
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
        List<MovimentoFisico> listMovimentoFisicoNaoOrcamento = new ArrayList<>();

        for (MovimentoFisico mf : listMovimentoFisico) {
            if (mf.getVenda() == null || !mf.getVenda().isOrcamento()) {
                //if(!mf.getVenda().isOrcamento() && mf.getVenda().getCancelamento() != null) {
                listMovimentoFisicoNaoOrcamento.add(mf);
            }
        }
        return listMovimentoFisicoNaoOrcamento;
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
    public void addMovimentoFisico(MovimentoFisico movimentoFisico) {
        listMovimentoFisico.remove(movimentoFisico);
        listMovimentoFisico.add(movimentoFisico);
        movimentoFisico.setProduto(this);
    }

    public void removeMovimentoFisico(MovimentoFisico movimentoFisico) {
        movimentoFisico.setProduto(null);
        listMovimentoFisico.remove(movimentoFisico);
    }

    public void addComponente(ProdutoComponente produtoComponente) {
        listProdutoComponente.remove(produtoComponente);
        listProdutoComponente.add(produtoComponente);
        produtoComponente.setProduto(this);
        //produtoComponente.setProdutoId(this.id);
    }

    public void removeComponente(ProdutoComponente produtoComponente) {
        //produtoComponente.setProdutoId(null);
        produtoComponente.setProduto(null);
        listProdutoComponente.remove(produtoComponente);
    }

    //--------------------------------------------------------------------------
    /**
     *
     * @return lista de produtos que contêm este componente
     */
    public List<Produto> getListProdutoComposto() {
        List<Produto> listProdutoComposto = new ArrayList<>();
        for (ProdutoComponente pc : getListProdutoComponenteReverso()) {
            listProdutoComposto.add(pc.getProduto());
        }
        return listProdutoComposto;
    }

    public BigDecimal getEstoqueAtual() {
        BigDecimal estoqueAtual = BigDecimal.ZERO;

        /*if (!getMovimentosFisicos().isEmpty()) {
            estoqueAtual = getMovimentosFisicos().get(getMovimentosFisicos().size() - 1).getSaldoAcumulado();
        }*/
        if (!getMovimentosFisicos().isEmpty()) {
            //recebido = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
            estoqueAtual = getMovimentosFisicos().stream().map(MovimentoFisico::getSaldoLinear).reduce(BigDecimal::add).get();
        }

        return estoqueAtual;
    }

    /**
     * Cria uma cópia rasa do objeto, exceto id e código
     * @return 
     */
    public Produto copiar() {
        Produto clone = new Produto();
        clone.setNome(this.getNome());
        clone.setDescricao(this.getDescricao());
        clone.setValorCompra(this.getValorCompra());
        clone.setMargemLucro(this.getMargemLucro());
        clone.setValorVenda(this.getValorVenda());
        clone.setOutrosCodigos(this.getOutrosCodigos());
        clone.setLocalizacao(this.getLocalizacao());
        clone.setCategoria(this.getCategoria());
        clone.setObservacao(this.getObservacao());
        clone.setBalanca(this.getBalanca());

        //fiscal    
        clone.setUnidadeComercialVenda(this.getUnidadeComercialVenda());
        clone.setOrigem(this.getOrigem());
        clone.setCfopSaidaDentroDoEstado(this.getCfopSaidaDentroDoEstado());
        clone.setCfopSaidaForaDoEstado(this.getCfopSaidaForaDoEstado());
        clone.setIcms(this.getIcms());
        clone.setNcm(this.getNcm());
        clone.setCest(this.getCest()); //Código Especificador da Substituição Tributária.
        clone.setAliquotaIcms(this.getAliquotaIcms());
        
        return clone;
    }

    /*
    2019-03-01 - Aparentemente não funciona bem por conta da gestão de id do JPA
    public Produto copiar() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Produto) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro em copiar " + e);
            return null;
        }
    }*/
}
