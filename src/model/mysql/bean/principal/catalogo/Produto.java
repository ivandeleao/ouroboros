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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.swing.ImageIcon;
import model.mysql.bean.fiscal.Anp;
import model.nosql.TipoCalculoEnum;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Cofins;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ipi;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.Pis;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.documento.Venda;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {@Index(columnList = "nome"), 
    @Index(columnList = "codigo"),
    @Index(columnList = "exclusao"),
    @Index(columnList = "outrosCodigos"),
    @Index(columnList = "balanca")
})
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
    
    private boolean valorFixo; //ignorar tabelas de preço
    
    private String outrosCodigos;
    private String localizacao;

    @OneToMany(mappedBy = "produtoId", cascade = CascadeType.ALL)
    private List<ProdutoComponente> listProdutoComponente = new ArrayList<>();

    @OneToMany(mappedBy = "componenteId")
    private List<ProdutoComponente> listProdutoComponenteReverso = new ArrayList<>();

    @OneToMany(mappedBy = "produto")
    @OrderBy
    private List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<ProdutoTamanho> produtoTamanhos = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "categoriaId", nullable = true)
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "subcategoriaId", nullable = true)
    private Subcategoria subcategoria;
    
    @ManyToOne
    @JoinColumn(name = "marcaId", nullable = true)
    private Marca marca;
    
    @ManyToOne
    @JoinColumn(name = "produtoTipoId", columnDefinition = "int default 1")
    private ProdutoTipo produtoTipo;
    

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(columnDefinition = "boolean default false")
    private Boolean balanca;
    
    private Integer diasValidade;
    
    @Column(columnDefinition = "decimal(20,3) default 0")
    private BigDecimal conteudoQuantidade;
    
    private boolean montavel; //Ex: pizza meio a meio
    
    @ManyToOne
    @JoinColumn(name = "conteudoUnidadeId") //2019-08-28 , nullable = true)
    private UnidadeComercial conteudoUnidade;
    
    private LocalDateTime necessidadeCompra;
    
    private Integer diasGarantia;
    
    @Column(columnDefinition = "decimal(20,3) default 0")//, nullable = false)
    private BigDecimal estoqueMinimo;
    
    @Column(columnDefinition = "decimal(20,3) default 0")//, nullable = false)
    private BigDecimal estoqueAtual;
    
    @OneToMany(mappedBy = "produto")
    @OrderBy
    private List<ProdutoImagem> produtoImagens = new ArrayList<>();

    //dados fiscais ------------------------------------------------------------
    private String ean;
    private String eanTributavel;
    private String exTipi;
    private String genero; //rever - parece não ser usado

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
    @JoinColumn(name = "unidadeTributavelId", nullable = true)
    private UnidadeComercial unidadeTributavel;
    
    private BigDecimal valorTributavel;
    
    @ManyToOne
    @JoinColumn(name = "icmsId", nullable = true)
    private Icms icms;
    
    //NCM pode ser cadastrado com código genérico no produto
    //Caso não exista nesta tabela, deve ser adicionado via banco
    @ManyToOne
    @JoinColumn(name = "ncmCodigo", nullable = true)
    private Ncm ncm;

    //Apenas o código armazenado pois, não existe mais nenhum dado associado a esta entidade
    private String cest; //Código Especificador da Substituição Tributária.

    @ManyToOne
    @JoinColumn(name = "modalidadeBcIcmsId", nullable = true)
    private ModalidadeBcIcms modalidadeBcIcms; //N13 Modalidade de determinação da BC do Icms
    
    private BigDecimal percentualReducaoBcIcms; //N14 Percentual de Redução de BC
    //private BigDecimal valorBcIcms; //N15 Valor da BC do Icms
    private BigDecimal aliquotaIcms; //N16 Alíquota do Imposto
    
    @ManyToOne
    @JoinColumn(name = "motivoDesoneracaoId", nullable = true)
    private MotivoDesoneracao motivoDesoneracao; //N28 modDesICMS
    
    @ManyToOne
    @JoinColumn(name = "modalidadeBcIcmsStId", nullable = true)
    private ModalidadeBcIcmsSt modalidadeBcIcmsSt; //N18 Modalidade de determinação da BC do Icms ST
    
    private BigDecimal percentualMargemValorAdicionadoIcmsSt; //N19 Percentual da margem de valor Adicionado do ICMS ST
    private BigDecimal percentualReducaoBcIcmsSt; //N20 Percentual da Redução de BC do ICMS ST
    //private BigDecimal valorBcIcmsSt; //N21 Valor da BC do Icms ST
    private BigDecimal aliquotaIcmsSt; //N22 Alíquota do Imposto do Icms ST
    
    private BigDecimal percentualBcOperacaoPropria; //N25 Percentual da BC operação própria
    
    //IPI-----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "ipiId", nullable = true)
    private Ipi ipi; //O09
    private String ipiCodigoEnquadramento; //O06 cEnq
    private String ipiCnpjProdutor; //O03 CNPJProd
    
    private TipoCalculoEnum ipiTipoCalculo;
    private BigDecimal ipiAliquota; //O13 pIPI
    private BigDecimal ipiValorUnidadeTributavel; //O12 vUnid
    //Fim IPI-------------------------------------------------------------------
    
    //PIS-----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "pisId", nullable = true)
    private Pis pis; //Q06
    private TipoCalculoEnum pisTipoCalculo;
    private BigDecimal aliquotaPis; //Q08 pPIS
    private BigDecimal aliquotaPisReais; //Q11 vAliqProd
    //Fim PIS-------------------------------------------------------------------
    
    //PIS ST--------------------------------------------------------------------
    private TipoCalculoEnum pisStTipoCalculo;
    private BigDecimal aliquotaPisSt; //R03 pPIS
    private BigDecimal aliquotaPisStReais; //R05 vAliqProd
    //Fim PIS ST----------------------------------------------------------------
    
    
    //COFINS-----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "cofinsId", nullable = true)
    private Cofins cofins; //S06
    private TipoCalculoEnum cofinsTipoCalculo;
    private BigDecimal aliquotaCofins; //S08 pCOFINS
    private BigDecimal aliquotaCofinsReais; //S10 vAliqProd
    //Fim COFINS-------------------------------------------------------------------
    
    //COFINS ST--------------------------------------------------------------------
    private TipoCalculoEnum cofinsStTipoCalculo;
    private BigDecimal aliquotaCofinsSt; //T03 pCOFINS
    private BigDecimal aliquotaCofinsStReais; //T05 vAliqProd
    //Fim COFINS ST----------------------------------------------------------------
    
    //Combustível --------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "anpId", nullable = true)
    private Anp anp;
    private String codif;
    
    /*private BigDecimal combustivelBc;
    private BigDecimal combustivelAliquota;
    private BigDecimal combustivelValor;*/
            
    //Fim Combustível ----------------------------------------------------------
    
    
    //Fim dados fiscais --------------------------------------------------------
    
    
    
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
        return nome != null ? nome : "";
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

    public BigDecimal getValorVenda(TabelaPreco tabelaPreco) {
        if(isValorFixo() || tabelaPreco == null) {
            return getValorVenda();
        } else {
            
            List<TabelaPrecoVariacao> variacoes = tabelaPreco.getTabelaPrecoVariacoes();
            
            if(variacoes.isEmpty()) {
                return getValorVenda();
            }
            
            if(variacoes.size() == 1 && !variacoes.get(0).isComIntervalo()) {
                return variacoes.get(0).getValorVariacao(valorVenda);
            }
            
            for(TabelaPrecoVariacao v : tabelaPreco.getTabelaPrecoVariacoes()) {
                if(valorVenda.compareTo(v.getValorInicial()) >= 0 && valorVenda.compareTo(v.getValorFinal()) <= 0) {
                    return v.getValorVariacao(valorVenda);
                }
            }
            
        }
        
        return getValorVenda();
    }
    
    public BigDecimal getValorVenda() {
        return valorVenda != null ? valorVenda : BigDecimal.ZERO;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }
    
    public boolean isValorFixo() {
        return valorFixo;
    }

    public void setValorFixo(boolean valorFixo) {
        this.valorFixo = valorFixo;
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

    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public ProdutoTipo getProdutoTipo() {
        return produtoTipo;
    }

    public void setProdutoTipo(ProdutoTipo produtoTipo) {
        this.produtoTipo = produtoTipo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao.trim();
    }

    public String getEan() {
        return ean != null ? ean : "";
    }

    public void setEan(String ean) {
        this.ean = ean.trim();
    }

    public String getEanTributavel() {
        return eanTributavel != null ? eanTributavel : "";
    }

    public void setEanTributavel(String eanTributavel) {
        this.eanTributavel = eanTributavel.trim();
    }

    public String getExTipi() {
        return exTipi != null ? exTipi : "";
    }

    public void setExTipi(String exTipi) {
        this.exTipi = exTipi.trim();
    }

    public String getGenero() {
        return genero != null ? genero : "";
    }

    public void setGenero(String genero) {
        this.genero = genero.trim();
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

    public UnidadeComercial getUnidadeTributavel() {
        return unidadeTributavel;
    }

    public void setUnidadeTributavel(UnidadeComercial unidadeTributavel) {
        this.unidadeTributavel = unidadeTributavel;
    }

    public BigDecimal getValorTributavel() {
        return valorTributavel != null ? valorTributavel : BigDecimal.ZERO;
    }

    public void setValorTributavel(BigDecimal valorTributavel) {
        this.valorTributavel = valorTributavel;
    }

    public Icms getIcms() {
        return icms;
    }

    public void setIcms(Icms icms) {
        this.icms = icms;
    }

    public ModalidadeBcIcms getModalidadeBcIcms() {
        return modalidadeBcIcms;
    }

    public void setModalidadeBcIcms(ModalidadeBcIcms modalidadeBcIcms) {
        this.modalidadeBcIcms = modalidadeBcIcms;
    }

    public BigDecimal getPercentualReducaoBcIcms() {
        return percentualReducaoBcIcms;
    }

    public void setPercentualReducaoBcIcms(BigDecimal percentualReducaoBcIcms) {
        this.percentualReducaoBcIcms = percentualReducaoBcIcms;
    }

/*    public BigDecimal getValorBcIcms() {
        return valorBcIcms;
    }

    public void setValorBcIcms(BigDecimal valorBcIcms) {
        this.valorBcIcms = valorBcIcms;
    }*/

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms != null ? aliquotaIcms : BigDecimal.ZERO;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public MotivoDesoneracao getMotivoDesoneracao() {
        return motivoDesoneracao;
    }

    public void setMotivoDesoneracao(MotivoDesoneracao motivoDesoneracao) {
        this.motivoDesoneracao = motivoDesoneracao;
    }

    public ModalidadeBcIcmsSt getModalidadeBcIcmsSt() {
        return modalidadeBcIcmsSt;
    }

    public void setModalidadeBcIcmsSt(ModalidadeBcIcmsSt modalidadeBcIcmsSt) {
        this.modalidadeBcIcmsSt = modalidadeBcIcmsSt;
    }

    public BigDecimal getPercentualMargemValorAdicionadoIcmsSt() {
        return percentualMargemValorAdicionadoIcmsSt;
    }

    public void setPercentualMargemValorAdicionadoIcmsSt(BigDecimal percentualMargemValorAdicionadoIcmsSt) {
        this.percentualMargemValorAdicionadoIcmsSt = percentualMargemValorAdicionadoIcmsSt;
    }

    public BigDecimal getPercentualReducaoBcIcmsSt() {
        return percentualReducaoBcIcmsSt;
    }

    public void setPercentualReducaoBcIcmsSt(BigDecimal percentualReducaoBcIcmsSt) {
        this.percentualReducaoBcIcmsSt = percentualReducaoBcIcmsSt;
    }

/*    public BigDecimal getValorBcIcmsSt() {
        return valorBcIcmsSt;
    }

    public void setValorBcIcmsSt(BigDecimal valorBcIcmsSt) {
        this.valorBcIcmsSt = valorBcIcmsSt;
    }*/

    public BigDecimal getAliquotaIcmsSt() {
        return aliquotaIcmsSt;
    }

    public void setAliquotaIcmsSt(BigDecimal aliquotaIcmsSt) {
        this.aliquotaIcmsSt = aliquotaIcmsSt;
    }

    public BigDecimal getPercentualBcOperacaoPropria() {
        return percentualBcOperacaoPropria;
    }

    public void setPercentualBcOperacaoPropria(BigDecimal percentualBcOperacaoPropria) {
        this.percentualBcOperacaoPropria = percentualBcOperacaoPropria;
    }

    public Ncm getNcm() {
        return ncm;
    }

    public void setNcm(Ncm ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest != null ? cest : "";
    }

    public void setCest(String cest) {
        this.cest = cest.trim();
    }

    public Ipi getIpi() {
        return ipi;
    }

    public void setIpi(Ipi ipi) {
        this.ipi = ipi;
    }

    public String getIpiCodigoEnquadramento() {
        return ipiCodigoEnquadramento;
    }

    public void setIpiCodigoEnquadramento(String ipiCodigoEnquadramento) {
        this.ipiCodigoEnquadramento = ipiCodigoEnquadramento;
    }

    public String getIpiCnpjProdutor() {
        return ipiCnpjProdutor;
    }

    public void setIpiCnpjProdutor(String ipiCnpjProdutor) {
        this.ipiCnpjProdutor = ipiCnpjProdutor;
    }

    public TipoCalculoEnum getIpiTipoCalculo() {
        return ipiTipoCalculo;
    }

    public void setIpiTipoCalculo(TipoCalculoEnum ipiTipoCalculo) {
        this.ipiTipoCalculo = ipiTipoCalculo;
    }

    public BigDecimal getIpiAliquota() {
        return ipiAliquota;
    }

    public void setIpiAliquota(BigDecimal ipiAliquota) {
        this.ipiAliquota = ipiAliquota;
    }

    public BigDecimal getIpiValorUnidadeTributavel() {
        return ipiValorUnidadeTributavel;
    }

    public void setIpiValorUnidadeTributavel(BigDecimal ipiValorUnidadeTributavel) {
        this.ipiValorUnidadeTributavel = ipiValorUnidadeTributavel;
    }

    public Pis getPis() {
        return pis;
    }

    public void setPis(Pis pis) {
        this.pis = pis;
    }

    public TipoCalculoEnum getPisTipoCalculo() {
        return pisTipoCalculo;
    }

    public void setPisTipoCalculo(TipoCalculoEnum pisTipoCalculo) {
        this.pisTipoCalculo = pisTipoCalculo;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
    }

    public BigDecimal getAliquotaPisReais() {
        return aliquotaPisReais;
    }

    public void setAliquotaPisReais(BigDecimal aliquotaPisReais) {
        this.aliquotaPisReais = aliquotaPisReais;
    }

    public TipoCalculoEnum getPisStTipoCalculo() {
        return pisStTipoCalculo;
    }

    public void setPisStTipoCalculo(TipoCalculoEnum pisStTipoCalculo) {
        this.pisStTipoCalculo = pisStTipoCalculo;
    }

    public BigDecimal getAliquotaPisSt() {
        return aliquotaPisSt;
    }

    public void setAliquotaPisSt(BigDecimal aliquotaPisSt) {
        this.aliquotaPisSt = aliquotaPisSt;
    }

    public BigDecimal getAliquotaPisStReais() {
        return aliquotaPisStReais;
    }

    public void setAliquotaPisStReais(BigDecimal aliquotaPisStReais) {
        this.aliquotaPisStReais = aliquotaPisStReais;
    }

    public Cofins getCofins() {
        return cofins;
    }

    public void setCofins(Cofins cofins) {
        this.cofins = cofins;
    }

    public TipoCalculoEnum getCofinsTipoCalculo() {
        return cofinsTipoCalculo;
    }

    public void setCofinsTipoCalculo(TipoCalculoEnum cofinsTipoCalculo) {
        this.cofinsTipoCalculo = cofinsTipoCalculo;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
    }

    public BigDecimal getAliquotaCofinsReais() {
        return aliquotaCofinsReais;
    }

    public void setAliquotaCofinsReais(BigDecimal aliquotaCofinsReais) {
        this.aliquotaCofinsReais = aliquotaCofinsReais;
    }

    public TipoCalculoEnum getCofinsStTipoCalculo() {
        return cofinsStTipoCalculo;
    }

    public void setCofinsStTipoCalculo(TipoCalculoEnum cofinsStTipoCalculo) {
        this.cofinsStTipoCalculo = cofinsStTipoCalculo;
    }

    public BigDecimal getAliquotaCofinsSt() {
        return aliquotaCofinsSt;
    }

    public void setAliquotaCofinsSt(BigDecimal aliquotaCofinsSt) {
        this.aliquotaCofinsSt = aliquotaCofinsSt;
    }

    public BigDecimal getAliquotaCofinsStReais() {
        return aliquotaCofinsStReais;
    }

    public void setAliquotaCofinsStReais(BigDecimal aliquotaCofinsStReais) {
        this.aliquotaCofinsStReais = aliquotaCofinsStReais;
    }

    public Anp getAnp() {
        return anp;
    }

    public void setAnp(Anp anp) {
        this.anp = anp;
    }

    public String getCodif() {
        return codif != null ? codif : "";
    }

    public void setCodif(String codif) {
        this.codif = codif;
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

    public List<ProdutoTamanho> getProdutoTamanhos() {
        return produtoTamanhos;
    }

    public void setProdutoTamanhos(List<ProdutoTamanho> produtoTamanhos) {
        this.produtoTamanhos = produtoTamanhos;
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

    public Boolean isBalanca() {
        return balanca != null ? balanca : false;
    }

    public void setBalanca(Boolean balanca) {
        this.balanca = balanca;
    }

    public Integer getDiasValidade() {
        return diasValidade != null ? diasValidade : 0;
    }

    public void setDiasValidade(Integer diasValidade) {
        this.diasValidade = diasValidade;
    }
    
    public BigDecimal getConteudoQuantidade() {
        return conteudoQuantidade != null ? conteudoQuantidade : BigDecimal.ZERO;
    }

    public void setConteudoQuantidade(BigDecimal conteudoQuantidade) {
        this.conteudoQuantidade = conteudoQuantidade;
    }

    public boolean isMontavel() {
        return montavel;
    }

    public void setMontavel(boolean montavel) {
        this.montavel = montavel;
    }

    public UnidadeComercial getConteudoUnidade() {
        return conteudoUnidade;
    }

    public void setConteudoUnidade(UnidadeComercial conteudoUnidade) {
        this.conteudoUnidade = conteudoUnidade;
    }

    public LocalDateTime getNecessidadeCompra() {
        return necessidadeCompra;
    }

    public void setNecessidadeCompra(LocalDateTime necessidadeCompra) {
        this.necessidadeCompra = necessidadeCompra;
    }

    public Integer getDiasGarantia() {
        return diasGarantia != null ? diasGarantia : 0;
    }

    public void setDiasGarantia(Integer diasGarantia) {
        this.diasGarantia = diasGarantia;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo != null ? estoqueMinimo : BigDecimal.ZERO;
    }

    public void setEstoqueMinimo(BigDecimal estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public BigDecimal getEstoqueAtual() {
        return estoqueAtual != null ? estoqueAtual : BigDecimal.ZERO;
    }

    public void setEstoqueAtual() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        
        BigDecimal estoqueAtual = BigDecimal.ZERO;
        Query q = em.createNativeQuery("select sum(entrada - saida) as saldo from " 
                + MovimentoFisico.class.getSimpleName() 
                + " left join " + Venda.class.getSimpleName()
                + " on " + MovimentoFisico.class.getSimpleName() + ".vendaId = " 
                + Venda.class.getSimpleName() + ".id "
                + " where produtoId = :produtoId"
                + " and (" + Venda.class.getSimpleName() + ".orcamento is null"
                        + " or " + Venda.class.getSimpleName() + ".orcamento = false)"
                + " and " + Venda.class.getSimpleName() + ".cancelamento is null");


        q.setParameter("produtoId", getId());

        if (q.getSingleResult() != null) {
            estoqueAtual = (BigDecimal) q.getSingleResult();
        }

        
        this.estoqueAtual = estoqueAtual;
        
        //System.out.println("this.estoqueAtual: " + this.estoqueAtual);
    }

    public List<ProdutoImagem> getProdutoImagens() {
        return produtoImagens;
    }

    public void setProdutoImagens(List<ProdutoImagem> produtoImagens) {
        this.produtoImagens = produtoImagens;
    }

    
    
    //Facilitadores-------------------------------------------------------------
    
    public ImageIcon getIcone() {
        if (isExcluido()) {
            return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-unavailable-20.png"));
            
        } else {
            if (isBalanca()) {
                return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-industrial-scales-20.png"));
                
            } else if (getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-new-product-20.png"));
                
            } else { //Serviço
                return new javax.swing.ImageIcon(getClass().getResource("/res/img/icon/icons8-service-20.png"));
                
            }
        }
    }
    
    public boolean isExcluido() {
        return getExclusao() != null;
    }
    
    public String getValorVendaComTamanhos() {
        //System.out.println(this.getProdutoTamanhos().size());
        if(getProdutoTamanhos().isEmpty()) {
            return Decimal.toString(getValorVenda());
            
        } else {
            List<String> valores = new ArrayList<>();
            for(ProdutoTamanho pt :getProdutoTamanhos()) {
                valores.add(pt.getTamanho().getNome().substring(0, 1) + " " + Decimal.toString(pt.getValorVenda()));
            }
            return String.join("\r\n", valores);
        }
    }
    //Fim Facilitadores---------------------------------------------------------
    
    
    //Bags----------------------------------------------------------------------
    
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
    
    public void addProdutoTamanho(ProdutoTamanho produtoTamanho) {
        produtoTamanhos.remove(produtoTamanho);
        produtoTamanhos.add(produtoTamanho);
        produtoTamanho.setProduto(this);
    }

    public void removeProdutoTamanho(ProdutoTamanho produtoTamanho) {
        produtoTamanho.setProduto(null);
        produtoTamanhos.remove(produtoTamanho);
    }
    
    public void addProdutoImagem(ProdutoImagem produtoImagem) {
        produtoImagens.remove(produtoImagem);
        produtoImagens.add(produtoImagem);
        produtoImagem.setProduto(this);
    }

    public void removeProdutoImagem(ProdutoImagem produtoImagem) {
        produtoImagem.setProduto(null);
        produtoImagens.remove(produtoImagem);
    }

    //Fim Bags------------------------------------------------------------------
    
    public boolean hasConteudo() {
        return getConteudoQuantidade().compareTo(BigDecimal.ZERO) > 0 && getConteudoUnidade() != null;
    }
    
    /**
     * 
     * @return Conteúdo: Quantidade concatenada com Unidade. Ex: 10 kg
     */
    public String getConteudoComUnidade() {
        return Decimal.toString(getConteudoQuantidade(), 3) + " " + getConteudoUnidade().getNome();
    }
    
    
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

    /**
     * 
     * @return Quantidade com unidade e informação de conteúdo se houver
     * Ex: 100 unid (10 kg)
     */
    public String getEstoqueAtualComUnidade() {
        if(getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
            return "";
        }
        
        String estoqueAtual = Decimal.toStringDescarteDecimais(getEstoqueAtual());
        
        if(getUnidadeComercialVenda() != null) {
            estoqueAtual += " " + getUnidadeComercialVenda().getNome();
        }
        
        if(hasConteudo()) {
            estoqueAtual += " (" + getEstoquePorConteudoComUnidade() + ")";
        }
        
        return estoqueAtual;
    }
    
    public BigDecimal getEstoquePorConteudo() {
        return getEstoqueAtual().multiply(getConteudoQuantidade());
    }
    
    public String getEstoquePorConteudoComUnidade() {
        return Decimal.toString(getEstoqueAtual().multiply(getConteudoQuantidade()), 3) + " " + getConteudoUnidade().getNome();
    }
    
    
    
    
    
    public BigDecimal getEstoqueAtualBkp() {
        BigDecimal estoqueAtual = BigDecimal.ZERO;

        //Ignora serviços
        if(getProdutoTipo().equals(ProdutoTipo.PRODUTO) && !getMovimentosFisicos().isEmpty()) {
            estoqueAtual = getMovimentosFisicos().stream().map(MovimentoFisico::getSaldoLinear).reduce(BigDecimal::add).get();
        }

        return estoqueAtual;
    }
    
    
    public BigDecimal getEstoqueAtualCompra() {
        return getEstoqueAtual().multiply(getValorCompra());
    }
    
    public BigDecimal getEstoqueAtualVenda() {
        return getEstoqueAtual().multiply(getValorVenda());
    }
    
    public Integer getIdUltimaCompra() {
        if(!getMovimentosFisicos().isEmpty()) {
            
            Comparator<Venda> comparator = Comparator.comparing(Venda::getId);
            
            List<Venda> vendas = getMovimentosFisicos().stream().filter((mf) -> (mf.getMovimentoFisicoTipo().equals(MovimentoFisicoTipo.COMPRA))).map(MovimentoFisico::getVenda).collect(Collectors.toList());
            
            for(Venda v: vendas) {
                System.out.println("v: " + v);
            }
            
            
            if(!vendas.isEmpty() && vendas.stream().filter(venda -> venda != null).max(comparator).isPresent()) {
                return vendas.stream().filter(venda -> venda != null).max(comparator).get().getId();
            }
        }
        
        return 0;
    }
    
    
    public ProdutoTamanho getProdutoTamanho(Tamanho tamanho) {
        for(ProdutoTamanho pt : getProdutoTamanhos()) {
            if(pt.getTamanho().equals(tamanho)) {
                return pt;
            }
        }
        return null;
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
        clone.setBalanca(this.isBalanca());

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
    
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        return Objects.equals(this.getId(), ((Produto) obj).getId());
    }
}
