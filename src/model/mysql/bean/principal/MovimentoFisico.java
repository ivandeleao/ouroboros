/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.catalogo.Produto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import model.mysql.bean.fiscal.UnidadeComercial;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.mysql.bean.fiscal.Anp;
import model.nosql.TipoCalculoEnum;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Cofins;
import model.mysql.bean.fiscal.Icms;
import model.mysql.bean.fiscal.Ncm;
import model.mysql.bean.fiscal.Pis;
import model.mysql.bean.fiscal.ProdutoOrigem;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.catalogo.Tamanho;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;
import util.Decimal;
import util.FiscalUtil;

/**
 * Representa item de venda, item de compra e movimento de estoque
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "criacao"),
    @Index(columnList = "atualizacao"),
    @Index(columnList = "descricao"), 
    @Index(columnList = "codigo"), 
    @Index(columnList = "dataAndamento"),
    @Index(columnList = "dataAndamentoPrevista"),
    @Index(columnList = "dataPronto"),
    @Index(columnList = "dataProntoPrevista"),
    @Index(columnList = "dataEntrada"),
    @Index(columnList = "dataEntradaPrevista"),
    @Index(columnList = "dataSaida"),
    @Index(columnList = "dataSaidaPrevista")
    
})
public class MovimentoFisico implements Serializable, Comparable<MovimentoFisico> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    //--------------------------------------------------------------------------
    @OneToMany(mappedBy = "movimentoFisicoOrigem", cascade = CascadeType.ALL)
    private List<MovimentoFisico> movimentosFisicosComponente = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "movimentoFisicoOrigemId")
    private MovimentoFisico movimentoFisicoOrigem;
    //--------------------------------------------------------------------------
    
    //--------------------------------------------------------------------------
    //Produtos montados na hora da venda - Ex: Pizza meio a meio
    @OneToMany(mappedBy = "montagemOrigem") //, cascade = CascadeType.ALL) 2019-11-29
    private List<MovimentoFisico> montagemItens = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "montagemOrigemId")
    private MovimentoFisico montagemOrigem;
    //--------------------------------------------------------------------------
    
    @ManyToOne
    @JoinColumn(name = "vendaId", nullable = true)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produtoId", nullable = true)
    private Produto produto;
    
    @ManyToOne
    @JoinColumn(name = "produtoTipoId")
    private ProdutoTipo produtoTipo;
    
    @ManyToOne
    @JoinColumn(name = "tamanhoId")
    private Tamanho tamanho;
    
    private String descricao;

    private String codigo; //se não possuir código no cadastro será usado o id
    
    private LocalDateTime dataAndamento;
    private LocalDateTime dataAndamentoPrevista;
    
    private LocalDateTime dataPronto;
    private LocalDateTime dataProntoPrevista;
    
    private LocalDateTime dataEntrada; //data efetiva para itens locados (devolução) ou compra
    private LocalDateTime dataSaida; //data efetiva para itens locados ou com entrega posterior (data entrega)

    private LocalDateTime dataEntradaPrevista;
    private LocalDateTime dataSaidaPrevista;

    @Column(columnDefinition = "decimal(20,3) default 0", nullable = false)
    private BigDecimal entrada;
    
    @Column(columnDefinition = "decimal(20,3) default 0", nullable = false)
    private BigDecimal saida;
    
    private BigDecimal valor;
    
    @Column(columnDefinition = "decimal(13,2) default 0")
    private BigDecimal valorFrete; //2019-07-17 NFe
    
    @Column(columnDefinition = "decimal(13,2) default 0")
    private BigDecimal valorSeguro; //2019-07-26 NFe
    
    @Column(columnDefinition = "decimal(13,2) default 0")
    private BigDecimal acrescimoMonetario; //2019-07-26 - NFe vOutro - outras despesas acessórias
    
    @Column(columnDefinition = "decimal(13,2) default 0")
    private BigDecimal acrescimoPercentual;
    
    @Column(columnDefinition = "decimal(13,2) default 0")
    private BigDecimal descontoMonetario; //2019-07-26 NFe
    
    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal descontoPercentual; //2019-04-01
    

    private BigDecimal saldoAcumulado;

    @Column(nullable = true)
    private String observacao;
    
    @ManyToOne
    @JoinColumn(name = "funcionarioId", nullable = true)
    private Funcionario funcionario;
    
    //------------------- relacionamento circular
    @OneToOne(mappedBy = "estornoOrigem", cascade = CascadeType.ALL)
    private MovimentoFisico estorno;
    
    @OneToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "estornoOrigemId")
    private MovimentoFisico estornoOrigem;
    //-------------------

    @ManyToOne
    @JoinColumn(name = "movimentoFisicoTipoId", nullable = true)
    private MovimentoFisicoTipo movimentoFisicoTipo;

    @Column(columnDefinition = "boolean default false")
    private Boolean excluido;

    //------------------- relacionamento circular
    @OneToOne(mappedBy = "devolucaoOrigem", cascade = CascadeType.ALL)
    private MovimentoFisico devolucao;

    @OneToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucaoOrigemId")
    private MovimentoFisico devolucaoOrigem;
    //-------------------
    
    //Fiscal--------------------------------------------------------------------
    private String ean;
    private String eanTributavel; //
    private String exTipi;
    
    @ManyToOne
    @JoinColumn(name = "unidadeComercialVendaId") //2019-08-28 , nullable = true)
    private UnidadeComercial unidadeComercialVenda;
    
    @ManyToOne
    @JoinColumn(name = "unidadeTributavelId", nullable = true)
    private UnidadeComercial unidadeTributavel;
    
    @ManyToOne
    @JoinColumn(name = "origemId", nullable = true)
    private ProdutoOrigem origem;

    @ManyToOne
    @JoinColumn(name = "cfopCodigo", nullable = true)
    private Cfop cfop;
    
    @ManyToOne
    @JoinColumn(name = "ncmCodigo", nullable = true)
    private Ncm ncm;

    private String cest; //Código Especificador da Substituição Tributária.
    
    private BigDecimal quantidadeTributavel; //I14 qTrib
    
    private BigDecimal valorTributavel; //I14a vUnTrib
    
    private boolean valorCompoeTotal; //I17b IndTot 0-não; 1-sim
    
    private String pedidoCompra; //I60 xPed
    private Integer itemPedidoCompra; //I61 nItemPed
    
    //private BigDecimal totalTributos; //M02 vTotTrib Valor aproximado total de tributos federais, estaduais e municipais
    
    //ICMS
    @ManyToOne
    @JoinColumn(name = "icmsId", nullable = true)
    private Icms icms; //N01
    
    private BigDecimal aliquotaAplicavelCalculoCreditoIcms;
    private BigDecimal valorCreditoIcms;
    
    @ManyToOne
    @JoinColumn(name = "modalidadeBcIcmsId", nullable = true)
    private ModalidadeBcIcms modalidadeBcIcms; //N13 Modalidade de determinação da BC do Icms
    
    private BigDecimal percentualReducaoBcIcms; //N14 Percentual de Redução de BC
    private BigDecimal valorBcIcms; //N15 Valor da BC do Icms
    private BigDecimal aliquotaIcms; //N16 Alíquota do Imposto
    private BigDecimal valorIcms; //N17 Valor do Icms
    
    private BigDecimal valorIcmsDesonerado; //N27a
    
    @ManyToOne
    @JoinColumn(name = "motivoDesoneracaoId", nullable = true)
    private MotivoDesoneracao motivoDesoneracao; //N28 modDesICMS
    
    
    
    @ManyToOne
    @JoinColumn(name = "modalidadeBcIcmsStId", nullable = true)
    private ModalidadeBcIcmsSt modalidadeBcIcmsSt; //N18 Modalidade de determinação da BC do Icms ST
    
    private BigDecimal percentualMargemValorAdicionadoIcmsSt; //N19 Percentual da margem de valor Adicionado do ICMS ST
    private BigDecimal percentualReducaoBcIcmsSt; //N20 Percentual da Redução de BC do ICMS ST
    private BigDecimal valorBcIcmsSt; //N21 Valor da BC do Icms ST
    private BigDecimal aliquotaIcmsSt; //N22 Alíquota do Imposto do Icms ST
    private BigDecimal valorIcmsSt; //N17 Valor do Icms St
    
    private BigDecimal percentualBcOperacaoPropria; //N25 Percentual da BC operação própria
    
    private BigDecimal valorBcIcmsStRetido; //N26 vBCSTRet Valor da BC do ICMS ST retido
    private BigDecimal aliquotaSuportadaConsumidorFinal; //N26a pST Alíquota suportada pelo Consumidor Final
    private BigDecimal valorIcmsProprioSubstituto; //N26b vICMSSubstituto Valor do ICMS prórprio do substituto
    private BigDecimal valorIcmsStRetido; //N27 vICMSSTRet Valor do ICMS ST retido
    
    //PIS-----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "pisId", nullable = true)
    private Pis pis; //Q06
    
    private TipoCalculoEnum pisTipoCalculo;
    
    private BigDecimal valorBcPis; //Q07 vBc
    private BigDecimal aliquotaPis; //Q08 pPIS
    
    private BigDecimal quantidadeVendidaPis; //Q10 qBCProd
    private BigDecimal aliquotaPisReais; //Q11 vAliqProd
    
    private BigDecimal valorPis; //Q09 vPIS
    //Fim PIS-------------------------------------------------------------------
    
    //PIS ST--------------------------------------------------------------------
    private TipoCalculoEnum pisStTipoCalculo;
    
    private BigDecimal valorBcPisSt; //R02 vBc
    private BigDecimal aliquotaPisSt; //R03 pPIS
    
    private BigDecimal quantidadeVendidaPisSt; //R04 qBCProd
    private BigDecimal aliquotaPisStReais; //R05 vAliqProd
    
    private BigDecimal valorPisSt; //R06 vPIS
    //Fim PIS ST----------------------------------------------------------------
    
    
    //COFINS-----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "cofinsId", nullable = true)
    private Cofins cofins; //S06
    
    private TipoCalculoEnum cofinsTipoCalculo;
    
    private BigDecimal valorBcCofins; //S07 vBc
    private BigDecimal aliquotaCofins; //S08 pCOFINS
    
    private BigDecimal quantidadeVendidaCofins; //S09 qBCProd
    private BigDecimal aliquotaCofinsReais; //S10 vAliqProd
    
    private BigDecimal valorCofins; //S11 vCOFINS
    //Fim COFINS----------------------------------------------------------------
    
    //COFINS ST-----------------------------------------------------------------
    private TipoCalculoEnum cofinsStTipoCalculo;
    
    private BigDecimal valorBcCofinsSt; //T02 vBc
    private BigDecimal aliquotaCofinsSt; //T03 pCOFINS
    
    private BigDecimal quantidadeVendidaCofinsSt; //T04 qBCProd
    private BigDecimal aliquotaCofinsStReais; //T05 vAliqProd
    
    private BigDecimal valorCofinsSt; //T06 vCOFINS
    //Fim COFINS ST-------------------------------------------------------------
    
    
    //Combustível --------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "anpId", nullable = true)
    private Anp anp;
    private String codif;
    @Column(columnDefinition = "decimal(12,4) default 0")
    private BigDecimal combustivelQuantidade;
    private String combustivelUf;
    
    private BigDecimal combustivelBc;
    private BigDecimal combustivelAliquota;
    private BigDecimal combustivelValor;
            
    //Fim Combustível ----------------------------------------------------------
    
    
    //Fim Fiscal----------------------------------------------------------------

    public MovimentoFisico() {
    }

    public MovimentoFisico(Produto produto, String codigo, String descricao, ProdutoTipo produtoTipo, BigDecimal entrada, BigDecimal saida, BigDecimal valor, BigDecimal descontoPercentual, UnidadeComercial unidadeComercialVenda, MovimentoFisicoTipo movimentoFisicoTipo, String observacao) {
        this.produto = produto;
        this.codigo = codigo;
        this.descricao = descricao;
        this.produtoTipo = produtoTipo;
        this.entrada = entrada;
        this.saida = saida;
        this.valor = valor;
        this.descontoPercentual = descontoPercentual;
        this.unidadeComercialVenda = unidadeComercialVenda;
        this.movimentoFisicoTipo = movimentoFisicoTipo;
        this.observacao = observacao;
    }
    
    public MovimentoFisico(Produto produto, String codigo, String descricao, ProdutoTipo produtoTipo, 
            BigDecimal entrada, BigDecimal saida, BigDecimal valor, BigDecimal descontoPercentual, 
            UnidadeComercial unidadeComercialVenda, MovimentoFisicoTipo movimentoFisicoTipo, String observacao, 
            String exTipi, Ncm ncm, String cest, Cfop cfop, String ean,
            UnidadeComercial unidadeTributavel, BigDecimal quantidadeTributavel, BigDecimal valorTributavel, String eanTributavel,
            BigDecimal acrescimoMonetario, BigDecimal descontoMonetario, BigDecimal valorFrete, BigDecimal valorSeguro,
            boolean valorCompoeTotal,
            Icms icms, ProdutoOrigem origem, ModalidadeBcIcms modalidadeBcIcms, BigDecimal percentualReducaoBcIcms,
            Pis pis, TipoCalculoEnum pisTipoCalculo, BigDecimal aliquotaPis, BigDecimal aliquotaPisReais,
            TipoCalculoEnum pisStTipoCalculo, BigDecimal aliquotaPisSt, BigDecimal aliquotaPisStReais, 
            Cofins cofins, TipoCalculoEnum cofinsTipoCalculo, BigDecimal aliquotaCofins, BigDecimal aliquotaCofinsReais, 
            TipoCalculoEnum cofinsStTipoCalculo, BigDecimal aliquotaCofinsSt, BigDecimal aliquotaCofinsStReais
    ) {
        
        this.produto = produto;
        this.codigo = codigo;
        this.descricao = descricao;
        this.produtoTipo = produtoTipo;
        this.entrada = entrada;
        this.saida = saida;
        this.valor = valor;
        this.descontoPercentual = descontoPercentual;
        this.unidadeComercialVenda = unidadeComercialVenda;
        this.movimentoFisicoTipo = movimentoFisicoTipo;
        this.observacao = observacao;
        this.exTipi = exTipi;
        this.ncm = ncm;
        this.cest = cest;
        this.cfop = cfop;
        this.ean = ean;
        this.unidadeTributavel = unidadeTributavel;
        this.quantidadeTributavel = quantidadeTributavel;
        this.valorTributavel = valorTributavel;
        this.eanTributavel = eanTributavel;
        this.acrescimoMonetario = acrescimoMonetario;
        this.descontoMonetario = descontoMonetario;
        this.valorFrete = valorFrete;
        this.valorSeguro = valorSeguro;
        this.valorCompoeTotal = valorCompoeTotal;
        this.icms = icms;
        this.origem = origem;
        this.modalidadeBcIcms = modalidadeBcIcms;
        this.percentualReducaoBcIcms = percentualReducaoBcIcms;
        //...
        this.pis = pis;
        this.pisTipoCalculo = pisTipoCalculo;
        this.aliquotaPis = aliquotaPis;
        this.aliquotaPisReais = aliquotaPisReais;
        this.pisStTipoCalculo = pisStTipoCalculo;
        this.aliquotaPisSt = aliquotaPisSt;
        this.aliquotaPisStReais = aliquotaPisStReais;
        
        this.cofins = cofins;
        this.cofinsTipoCalculo = cofinsTipoCalculo;
        this.aliquotaCofins = aliquotaCofins;
        this.aliquotaCofinsReais = aliquotaCofinsReais;
        this.cofinsStTipoCalculo = cofinsStTipoCalculo;
        this.aliquotaCofinsSt = aliquotaCofinsSt;
        this.aliquotaCofinsStReais = aliquotaCofinsStReais;
        
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

    public Venda getVenda() {
        if(venda != null) {
            return venda;
        } else {
            if(getMovimentoFisicoOrigem() != null) {
                return getMovimentoFisicoOrigem().getVenda();
                
            } else if(getDevolucaoOrigem() != null) {
                return getDevolucaoOrigem().getVenda();
                
            }
        }
        return null;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public List<MovimentoFisico> getMovimentosFisicosComponente() {
        //remover duplicatas
        //if(movimentosFisicosComponente != null) {
        //    movimentosFisicosComponente = movimentosFisicosComponente.stream().distinct().collect(Collectors.toSet());
        //}
        return movimentosFisicosComponente;
    }

    public void setMovimentosFisicosComponente(List<MovimentoFisico> movimentosFisicosComponente) {
        this.movimentosFisicosComponente = movimentosFisicosComponente;
    }

    public MovimentoFisico getMovimentoFisicoOrigem() {
        return movimentoFisicoOrigem;
    }

    public void setMovimentoFisicoOrigem(MovimentoFisico movimentoFisicoOrigem) {
        this.movimentoFisicoOrigem = movimentoFisicoOrigem;
    }

    public List<MovimentoFisico> getMontagemItens() {
        return montagemItens;
    }

    public void setMontagemItens(List<MovimentoFisico> montagemItens) {
        this.montagemItens = montagemItens;
    }

    public MovimentoFisico getMontagemOrigem() {
        return montagemOrigem;
    }

    public void setMontagemOrigem(MovimentoFisico montagemOrigem) {
        this.montagemOrigem = montagemOrigem;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao != null ? descricao : "";
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ProdutoTipo getProdutoTipo() {
        return produtoTipo;
    }

    public void setProdutoTipo(ProdutoTipo produtoTipo) {
        this.produtoTipo = produtoTipo;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public void setTamanho(Tamanho tamanho) {
        this.tamanho = tamanho;
    }

    public LocalDateTime getDataAndamento() {
        return dataAndamento;
    }

    public void setDataAndamento(LocalDateTime dataAndamento) {
        this.dataAndamento = dataAndamento;
    }

    public LocalDateTime getDataAndamentoPrevista() {
        return dataAndamentoPrevista;
    }

    public void setDataAndamentoPrevista(LocalDateTime dataAndamentoPrevista) {
        this.dataAndamentoPrevista = dataAndamentoPrevista;
    }

    public LocalDateTime getDataPronto() {
        return dataPronto;
    }

    public void setDataPronto(LocalDateTime dataPronto) {
        this.dataPronto = dataPronto;
    }

    public LocalDateTime getDataProntoPrevista() {
        return dataProntoPrevista;
    }

    public void setDataProntoPrevista(LocalDateTime dataProntoPrevista) {
        this.dataProntoPrevista = dataProntoPrevista;
    }
    
    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataEntradaPrevista() {
        return dataEntradaPrevista;
    }

    public void setDataEntradaPrevista(LocalDateTime dataEntradaPrevista) {
        this.dataEntradaPrevista = dataEntradaPrevista;
    }

    public LocalDateTime getDataSaidaPrevista() {
        return dataSaidaPrevista;
    }

    public void setDataSaidaPrevista(LocalDateTime dataSaidaPrevista) {
        this.dataSaidaPrevista = dataSaidaPrevista;
    }

    public BigDecimal getEntrada() {
        return entrada != null ? entrada : BigDecimal.ZERO;
    }

    public void setEntrada(BigDecimal entrada) {
        this.entrada = entrada;
    }

    public BigDecimal getSaida() {
        return saida != null ? saida : BigDecimal.ZERO;
    }

    public void setSaida(BigDecimal saida) {
        this.saida = saida;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getEanTributavel() {
        return eanTributavel;
    }

    public void setEanTributavel(String eanTributavel) {
        this.eanTributavel = eanTributavel;
    }

    public String getExTipi() {
        return exTipi;
    }

    public void setExTipi(String exTipi) {
        this.exTipi = exTipi;
    }

    public UnidadeComercial getUnidadeTributavel() {
        return unidadeTributavel;
    }

    public void setUnidadeTributavel(UnidadeComercial unidadeTributavel) {
        this.unidadeTributavel = unidadeTributavel;
    }

    public ProdutoOrigem getOrigem() {
        return origem;
    }

    public void setOrigem(ProdutoOrigem origem) {
        this.origem = origem;
    }

    public Cfop getCfop() {
        return cfop;
    }

    public void setCfop(Cfop cfop) {
        this.cfop = cfop;
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

    public BigDecimal getQuantidadeTributavel() {
        return quantidadeTributavel;
    }

    public void setQuantidadeTributavel(BigDecimal quantidadeTributavel) {
        this.quantidadeTributavel = quantidadeTributavel;
    }

    public BigDecimal getValorTributavel() {
        return valorTributavel;
    }

    public void setValorTributavel(BigDecimal valorTributavel) {
        this.valorTributavel = valorTributavel;
    }

    public boolean isValorCompoeTotal() {
        return valorCompoeTotal;
    }

    public void setValorCompoeTotal(boolean valorCompoeTotal) {
        this.valorCompoeTotal = valorCompoeTotal;
    }

    public String getPedidoCompra() {
        return pedidoCompra;
    }

    public void setPedidoCompra(String pedidoCompra) {
        this.pedidoCompra = pedidoCompra;
    }

    public Integer getItemPedidoCompra() {
        return itemPedidoCompra != null ? itemPedidoCompra : 0;
    }

    public void setItemPedidoCompra(Integer itemPedidoCompra) {
        this.itemPedidoCompra = itemPedidoCompra;
    }

    public Icms getIcms() {
        return icms;
    }

    public void setIcms(Icms icms) {
        this.icms = icms;
    }

    public BigDecimal getAliquotaAplicavelCalculoCreditoIcms() {
        return aliquotaAplicavelCalculoCreditoIcms;
    }

    public void setAliquotaAplicavelCalculoCreditoIcms(BigDecimal aliquotaAplicavelCalculoCreditoIcms) {
        this.aliquotaAplicavelCalculoCreditoIcms = aliquotaAplicavelCalculoCreditoIcms;
    }

    public BigDecimal getValorCreditoIcms() {
        return valorCreditoIcms;
    }

    public void setValorCreditoIcms(BigDecimal valorCreditoIcms) {
        this.valorCreditoIcms = valorCreditoIcms;
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

    public BigDecimal getValorBcIcms() {
        return valorBcIcms != null ? valorBcIcms : BigDecimal.ZERO;
    }

    public void setValorBcIcms(BigDecimal valorBcIcms) {
        this.valorBcIcms = valorBcIcms;
    }

    public BigDecimal getAliquotaIcms() {
        return aliquotaIcms != null ? aliquotaIcms : BigDecimal.ZERO;
    }

    public void setAliquotaIcms(BigDecimal aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public BigDecimal getValorIcms() {
        return valorIcms != null ? valorIcms : BigDecimal.ZERO;
    }

    public void setValorIcms(BigDecimal valorIcms) {
        this.valorIcms = valorIcms;
    }

    public BigDecimal getValorIcmsDesonerado() {
        return valorIcmsDesonerado != null ? valorIcmsDesonerado : BigDecimal.ZERO;
    }

    public void setValorIcmsDesonerado(BigDecimal valorIcmsDesonerado) {
        this.valorIcmsDesonerado = valorIcmsDesonerado;
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
        return percentualMargemValorAdicionadoIcmsSt != null ? percentualMargemValorAdicionadoIcmsSt : BigDecimal.ZERO;
    }

    public void setPercentualMargemValorAdicionadoIcmsSt(BigDecimal percentualMargemValorAdicionadoIcmsSt) {
        this.percentualMargemValorAdicionadoIcmsSt = percentualMargemValorAdicionadoIcmsSt;
    }

    public BigDecimal getPercentualReducaoBcIcmsSt() {
        return percentualReducaoBcIcmsSt != null ? percentualReducaoBcIcmsSt : BigDecimal.ZERO;
    }

    public void setPercentualReducaoBcIcmsSt(BigDecimal percentualReducaoBcIcmsSt) {
        this.percentualReducaoBcIcmsSt = percentualReducaoBcIcmsSt;
    }

    public BigDecimal getValorBcIcmsSt() {
        return valorBcIcmsSt != null ? valorBcIcmsSt : BigDecimal.ZERO;
    }

    public void setValorBcIcmsSt(BigDecimal valorBcIcmsSt) {
        this.valorBcIcmsSt = valorBcIcmsSt;
    }

    public BigDecimal getAliquotaIcmsSt() {
        return aliquotaIcmsSt != null ? aliquotaIcmsSt : BigDecimal.ZERO;
    }

    public void setAliquotaIcmsSt(BigDecimal aliquotaIcmsSt) {
        this.aliquotaIcmsSt = aliquotaIcmsSt;
    }

    public BigDecimal getValorIcmsSt() {
        return valorIcmsSt != null ? valorIcmsSt : BigDecimal.ZERO;
    }

    public void setValorIcmsSt(BigDecimal valorIcmsSt) {
        this.valorIcmsSt = valorIcmsSt;
    }

    public BigDecimal getPercentualBcOperacaoPropria() {
        return percentualBcOperacaoPropria;
    }

    public void setPercentualBcOperacaoPropria(BigDecimal percentualBcOperacaoPropria) {
        this.percentualBcOperacaoPropria = percentualBcOperacaoPropria;
    }

    public BigDecimal getValorBcIcmsStRetido() {
        return valorBcIcmsStRetido;
    }

    public void setValorBcIcmsStRetido(BigDecimal valorBcIcmsStRetido) {
        this.valorBcIcmsStRetido = valorBcIcmsStRetido;
    }

    public BigDecimal getAliquotaSuportadaConsumidorFinal() {
        return aliquotaSuportadaConsumidorFinal;
    }

    public void setAliquotaSuportadaConsumidorFinal(BigDecimal aliquotaSuportadaConsumidorFinal) {
        this.aliquotaSuportadaConsumidorFinal = aliquotaSuportadaConsumidorFinal;
    }

    public BigDecimal getValorIcmsProprioSubstituto() {
        return valorIcmsProprioSubstituto;
    }

    public void setValorIcmsProprioSubstituto(BigDecimal valorIcmsProprioSubstituto) {
        this.valorIcmsProprioSubstituto = valorIcmsProprioSubstituto;
    }

    public BigDecimal getValorIcmsStRetido() {
        return valorIcmsStRetido;
    }

    public void setValorIcmsStRetido(BigDecimal valorIcmsStRetido) {
        this.valorIcmsStRetido = valorIcmsStRetido;
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

    public BigDecimal getValorBcPis() {
        return valorBcPis;
    }

    public void setValorBcPis(BigDecimal valorBcPis) {
        this.valorBcPis = valorBcPis;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
    }

    public BigDecimal getQuantidadeVendidaPis() {
        return quantidadeVendidaPis != null ? quantidadeVendidaPis : BigDecimal.ZERO;
    }

    public void setQuantidadeVendidaPis(BigDecimal quantidadeVendidaPis) {
        this.quantidadeVendidaPis = quantidadeVendidaPis;
    }

    public BigDecimal getAliquotaPisReais() {
        return aliquotaPisReais != null ? aliquotaPisReais : BigDecimal.ZERO;
    }

    public void setAliquotaPisReais(BigDecimal aliquotaPisReais) {
        this.aliquotaPisReais = aliquotaPisReais;
    }

    public BigDecimal getValorPis() {
        return valorPis != null ? valorPis : BigDecimal.ZERO;
    }

    public void setValorPis(BigDecimal valorPis) {
        this.valorPis = valorPis;
    }

    public TipoCalculoEnum getPisStTipoCalculo() {
        return pisStTipoCalculo;
    }

    public void setPisStTipoCalculo(TipoCalculoEnum pisStTipoCalculo) {
        this.pisStTipoCalculo = pisStTipoCalculo;
    }

    public BigDecimal getValorBcPisSt() {
        return valorBcPisSt != null ? valorBcPisSt : BigDecimal.ZERO;
    }

    public void setValorBcPisSt(BigDecimal valorBcPisSt) {
        this.valorBcPisSt = valorBcPisSt;
    }

    public BigDecimal getAliquotaPisSt() {
        return aliquotaPisSt != null ? aliquotaPisSt : BigDecimal.ZERO;
    }

    public void setAliquotaPisSt(BigDecimal aliquotaPisSt) {
        this.aliquotaPisSt = aliquotaPisSt;
    }

    public BigDecimal getQuantidadeVendidaPisSt() {
        return quantidadeVendidaPisSt != null ? quantidadeVendidaPisSt : BigDecimal.ZERO;
    }

    public void setQuantidadeVendidaPisSt(BigDecimal quantidadeVendidaPisSt) {
        this.quantidadeVendidaPisSt = quantidadeVendidaPisSt;
    }

    public BigDecimal getAliquotaPisStReais() {
        return aliquotaPisStReais != null ? aliquotaPisStReais : BigDecimal.ZERO;
    }

    public void setAliquotaPisStReais(BigDecimal aliquotaPisStReais) {
        this.aliquotaPisStReais = aliquotaPisStReais;
    }

    public BigDecimal getValorPisSt() {
        return valorPisSt != null ? valorPisSt : BigDecimal.ZERO;
    }

    public void setValorPisSt(BigDecimal valorPisSt) {
        this.valorPisSt = valorPisSt;
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

    public BigDecimal getValorBcCofins() {
        return valorBcCofins != null ? valorBcCofins : BigDecimal.ZERO;
    }

    public void setValorBcCofins(BigDecimal valorBcCofins) {
        this.valorBcCofins = valorBcCofins;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins != null ? aliquotaCofins : BigDecimal.ZERO;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
    }

    public BigDecimal getQuantidadeVendidaCofins() {
        return quantidadeVendidaCofins != null ? quantidadeVendidaCofins : BigDecimal.ZERO;
    }

    public void setQuantidadeVendidaCofins(BigDecimal quantidadeVendidaCofins) {
        this.quantidadeVendidaCofins = quantidadeVendidaCofins;
    }

    public BigDecimal getAliquotaCofinsReais() {
        return aliquotaCofinsReais != null ? aliquotaCofinsReais : BigDecimal.ZERO;
    }

    public void setAliquotaCofinsReais(BigDecimal aliquotaCofinsReais) {
        this.aliquotaCofinsReais = aliquotaCofinsReais;
    }

    public BigDecimal getValorCofins() {
        return Decimal.parse(valorCofins);
    }

    public void setValorCofins(BigDecimal valorCofins) {
        this.valorCofins = valorCofins;
    }

    public TipoCalculoEnum getCofinsStTipoCalculo() {
        return cofinsStTipoCalculo;
    }

    public void setCofinsStTipoCalculo(TipoCalculoEnum cofinsStTipoCalculo) {
        this.cofinsStTipoCalculo = cofinsStTipoCalculo;
    }

    public BigDecimal getValorBcCofinsSt() {
        return valorBcCofinsSt;
    }

    public void setValorBcCofinsSt(BigDecimal valorBcCofinsSt) {
        this.valorBcCofinsSt = valorBcCofinsSt;
    }

    public BigDecimal getAliquotaCofinsSt() {
        return aliquotaCofinsSt;
    }

    public void setAliquotaCofinsSt(BigDecimal aliquotaCofinsSt) {
        this.aliquotaCofinsSt = aliquotaCofinsSt;
    }

    public BigDecimal getQuantidadeVendidaCofinsSt() {
        return quantidadeVendidaCofinsSt;
    }

    public void setQuantidadeVendidaCofinsSt(BigDecimal quantidadeVendidaCofinsSt) {
        this.quantidadeVendidaCofinsSt = quantidadeVendidaCofinsSt;
    }

    public BigDecimal getAliquotaCofinsStReais() {
        return aliquotaCofinsStReais;
    }

    public void setAliquotaCofinsStReais(BigDecimal aliquotaCofinsStReais) {
        this.aliquotaCofinsStReais = aliquotaCofinsStReais;
    }

    public BigDecimal getValorCofinsSt() {
        return valorCofinsSt;
    }

    public void setValorCofinsSt(BigDecimal valorCofinsSt) {
        this.valorCofinsSt = valorCofinsSt;
    }

    public Anp getAnp() {
        return anp;
    }

    public void setAnp(Anp anp) {
        this.anp = anp;
    }

    public String getCodif() {
        return codif;
    }

    public void setCodif(String codif) {
        this.codif = codif.trim();
    }

    public BigDecimal getCombustivelQuantidade() {
        return combustivelQuantidade != null ? combustivelQuantidade : BigDecimal.ZERO;
    }

    public void setCombustivelQuantidade(BigDecimal combustivelQuantidade) {
        this.combustivelQuantidade = combustivelQuantidade;
    }

    public String getCombustivelUf() {
        return combustivelUf != null ? combustivelUf : "";
    }

    public void setCombustivelUf(String combustivelUf) {
        this.combustivelUf = combustivelUf;
    }

    public BigDecimal getCombustivelBc() {
        return combustivelBc;
    }

    public void setCombustivelBc(BigDecimal combustivelBc) {
        this.combustivelBc = combustivelBc;
    }

    public BigDecimal getCombustivelAliquota() {
        return combustivelAliquota;
    }

    public void setCombustivelAliquota(BigDecimal combustivelAliquota) {
        this.combustivelAliquota = combustivelAliquota;
    }

    public BigDecimal getCombustivelValor() {
        return combustivelValor;
    }

    public void setCombustivelValor(BigDecimal combustivelValor) {
        this.combustivelValor = combustivelValor;
    }

    

    
    
    //--------------------------------------------------------------------------
    
    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado != null ? saldoAcumulado : BigDecimal.ZERO;
    }

    public void setSaldoAcumulado(BigDecimal saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public String getObservacao() {
        return observacao != null ? observacao : "";
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    
    //--------------------------------------------------------------------------
    
    public boolean isEstornado() {
        return getEstorno() != null;
    }
    
    public boolean isEstorno() {
        return getEstornoOrigem()!= null;
    }
    
    
    public boolean isAgrupado() {
        return getVenda().hasDocumentoPai();
    }
    
    
    /**
     * 
     * @return Descritivo com tipo do documento e id do mesmo.
     * Exs: Venda 10, Locação 13, Compra 15.
     * Procura recursivamente se necessário.
     */
    public String getDocumentoOrigem() {
        if(getVenda() != null) {
            return getVenda().getVendaTipo().getNome() + " " + getVenda().getId();
            
        } else if(getDevolucaoOrigem() != null) {
            Venda v = getDevolucaoOrigem().getVenda();
            return v.getVendaTipo().getNome() + " " + v.getId();
            
        } else if(getMovimentoFisicoOrigem() != null) {
            return getMovimentoFisicoOrigem().getDocumentoOrigem();
            
        } else if(getEstornoOrigem() != null) {
            return getEstornoOrigem().getDocumentoOrigem();
            
        } else {
            return "LANÇAMENTO MANUAL";
        }
    }

    public MovimentoFisico getEstorno() {
        //return estorno;
        
        if(estorno != null) {
            return estorno;
        } else {
            if(getMovimentoFisicoOrigem() != null) {
                    return getMovimentoFisicoOrigem().getEstorno();
            }
        }
        return null;
    }
    
    private void setEstorno(MovimentoFisico estorno) { //************ private
        this.estorno = estorno;
    }

    public MovimentoFisico getEstornoOrigem() {
        //return estornoOrigem;
        
        if(estornoOrigem != null) {
            return estornoOrigem;
        } else {
            if(getMovimentoFisicoOrigem() != null) {
                return getMovimentoFisicoOrigem().getEstornoOrigem();
            }
        }
        return null;
    }

    public void setEstornoOrigem(MovimentoFisico estornoOrigem) {
        this.estornoOrigem = estornoOrigem;
    }
    
    
    

    public MovimentoFisico getDevolucao() {
        return devolucao;
    }

    public void setDevolucao(MovimentoFisico devolucao) {
        this.devolucao = devolucao;
    }

    public MovimentoFisico getDevolucaoOrigem() {
        return devolucaoOrigem;
    }

    public void setDevolucaoOrigem(MovimentoFisico devolucaoOrigem) {
        this.devolucaoOrigem = devolucaoOrigem;
    }

    

    public LocalDateTime getPrevisaoEntrega() {
        return null; //pegar da venda
    }

    public LocalDateTime getPrevisaoDevolucao() {
        return null; //pegar da venda
    }

    public MovimentoFisicoTipo getMovimentoFisicoTipo() {
        return movimentoFisicoTipo;
    }

    public void setMovimentoFisicoTipo(MovimentoFisicoTipo movimentoFisicoTipo) {
        this.movimentoFisicoTipo = movimentoFisicoTipo;
    }

    public UnidadeComercial getUnidadeComercialVenda() {
        return unidadeComercialVenda;
    }

    public void setUnidadeComercialVenda(UnidadeComercial unidadeComercialVenda) {
        this.unidadeComercialVenda = unidadeComercialVenda;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorFrete() {
        return valorFrete != null ? valorFrete : BigDecimal.ZERO;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getValorSeguro() {
        return valorSeguro != null ? valorSeguro : BigDecimal.ZERO;
    }

    public void setValorSeguro(BigDecimal valorSeguro) {
        this.valorSeguro = valorSeguro;
    }

    /**
     * 
     * @return equivalente NFe vOutro (Outras despesas acessórias)
     */
    public BigDecimal getAcrescimoMonetario() {
        return acrescimoMonetario != null ? acrescimoMonetario : BigDecimal.ZERO;
    }

    public void setAcrescimoMonetario(BigDecimal acrescimoMonetario) {
        this.acrescimoMonetario = acrescimoMonetario;
    }

    public BigDecimal getAcrescimoPercentual() {
        return acrescimoPercentual != null ? acrescimoPercentual : BigDecimal.ZERO;
    }

    public void setAcrescimoPercentual(BigDecimal acrescimoPercentual) {
        this.acrescimoPercentual = acrescimoPercentual;
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
    
    //--------------------------------------------------------------------------
    
    /**
     * 
     * @return getEntrada().subtract(getSaida())
     */
    public BigDecimal getSaldoLinear() {
        return getEntrada().subtract(getSaida());
    }
    
    /**
     * 
     * @return getEntrada().subtract(getSaida()).abs()
     */
    public BigDecimal getSaldoLinearAbsoluto() {
        return getEntrada().subtract(getSaida()).abs();
    }
    
    
    public void addMovimentoFisicoComponente(MovimentoFisico movimentoFisico) {
        movimentosFisicosComponente.remove(movimentoFisico);
        movimentosFisicosComponente.add(movimentoFisico);
        movimentoFisico.setMovimentoFisicoOrigem(this);
    }
    
    public void removeMovimentoFisicoComponente(MovimentoFisico movimentoFisico) {
        movimentoFisico.setMovimentoFisicoOrigem(null);
        this.movimentosFisicosComponente.remove(movimentoFisico);
    }
    
    public void addMontagemItem(MovimentoFisico movimentoFisico) {
        montagemItens.remove(movimentoFisico);
        montagemItens.add(movimentoFisico);
        movimentoFisico.setMontagemOrigem(this);
    }
    
    public void removeMontagemItem(MovimentoFisico movimentoFisico) {
        movimentoFisico.setMontagemOrigem(null);
        this.montagemItens.remove(movimentoFisico);
    }

    
    public void addEstorno(MovimentoFisico mfEstorno) {
        this.estorno = mfEstorno;
        mfEstorno.setEstornoOrigem(this);
    }
    
    public void removeEstorno(MovimentoFisico mfEstorno) {
        if(mfEstorno != null) {
            mfEstorno.setEstornoOrigem(null);
        }
        this.estorno = null;
    }
    
    
    public void addDevolucao(MovimentoFisico mfDevolucao) {
        devolucao = mfDevolucao;
        mfDevolucao.setDevolucaoOrigem(this);
    }
    
    public void removeDevolucao(MovimentoFisico mfDevolucao) {
        if(mfDevolucao != null) {
            mfDevolucao.setDevolucaoOrigem(null);
        }
        this.devolucao = null;
    }
    
    
    public MovimentoFisicoStatus getStatus() {
        
        if(getEstornoOrigem()!= null) {
            return MovimentoFisicoStatus.ESTORNO;
            
        } else if(getEstorno() != null) {
            return MovimentoFisicoStatus.ESTORNADO;
            
        } else if (getDataEntrada() != null) {
            return MovimentoFisicoStatus.RECEBIMENTO_CONCLUÍDO;
            
        } else if (getDataEntradaPrevista() != null && getDataEntradaPrevista().compareTo(DateTime.getNow().toLocalDateTime()) < 0) {
            return MovimentoFisicoStatus.RECEBIMENTO_ATRASADO;
            
        } else if (getDataEntradaPrevista() != null) {
            return MovimentoFisicoStatus.RECEBIMENTO_PREVISTO;
        
        } else if (getDataSaida() != null) {
            return MovimentoFisicoStatus.ENTREGA_CONCLUÍDA;
            
        } else if (getDataSaidaPrevista() != null && getDataSaidaPrevista().compareTo(DateTime.getNow().toLocalDateTime()) < 0) {
            return MovimentoFisicoStatus.ENTREGA_ATRASADA;
            
        } else if (getDataSaidaPrevista() != null) {
            return MovimentoFisicoStatus.ENTREGA_PREVISTA;
            
        } else if (getDataPronto() != null) {
            return MovimentoFisicoStatus.PREPARAÇÃO_CONCLUÍDA;
            
        } else if (getDataProntoPrevista() != null && getDataProntoPrevista().compareTo(DateTime.getNow().toLocalDateTime()) < 0) {
            return MovimentoFisicoStatus.PREPARAÇÃO_ATRASADA;
            
        } else if (getDataProntoPrevista() != null) {
            return MovimentoFisicoStatus.PREPARAÇÃO_PREVISTA;
            
        } else if(getDataAndamento() != null) {
            return MovimentoFisicoStatus.ANDAMENTO;
            
        } else {
            return MovimentoFisicoStatus.AGUARDANDO;
            
        }

    }

    public LocalDateTime getDataRelevante() {
        if (getDataEntrada() != null) {
            return getDataEntrada();
        } else if (getDataSaida() != null) {
            return getDataSaida();
        } else if (getDataEntradaPrevista() != null) {
            return getDataEntradaPrevista();
        } else if (getDataSaidaPrevista() != null) {
            return getDataSaidaPrevista();
        } else {
            return getCriacao().toLocalDateTime();
        }
    }
    
    public BigDecimal getAcrescimoPercentualEmMonetario() {
        BigDecimal desconto = getSubtotalItem().multiply(getAcrescimoPercentual().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
        return desconto;
    }
    
    public BigDecimal getDescontoPercentualEmMonetario() {
        BigDecimal desconto = getSubtotalItem().multiply(getDescontoPercentual().divide(new BigDecimal(100), 10, RoundingMode.HALF_UP));
        return desconto;
    }
    
    public String getAcrescimoFormatado() {
        if(getAcrescimoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getAcrescimoPercentual()) + "%";
        } else {
            return Decimal.toString(getAcrescimoMonetario());
        }
    }
    
    public String getDescontoFormatado() {
        if(getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoPercentual()) + "%";
        } else {
            return Decimal.toString(getDescontoMonetario());
        }
    }
    
    /**
     * 
     * @return soma de acrescimoMonetario e acrescimoPercentualEmMonetario
     */
    public BigDecimal getAcrescimo() {
        return getAcrescimoMonetario().add(getAcrescimoPercentualEmMonetario());
    }
    
    /**
     * 
     * @return soma de descontoMonetario e descontoPercentualEmMonetario
     */
    public BigDecimal getDesconto() {
        return getDescontoMonetario().add(getDescontoPercentualEmMonetario());
    }
    
    /**
     * 
     * @return valor * quantidade
     */
    public BigDecimal getSubtotalItem() {
        return getValor().multiply(getSaldoLinearAbsoluto());
    }
    
    /**
     * 
     * @return (valor * quantidade) + frete + seguro + acréscimo - desconto
     */
    public BigDecimal getSubtotal() {
        //arredondando aqui está sumindo o item ao reabrir a venda
        //return getValor().multiply(getSaldoLinearAbsoluto());//.setScale(2, RoundingMode.HALF_UP);
        //2019-04-01
        return (getSubtotalItem() //vProd
                .subtract(getDescontoMonetario()).subtract(getDescontoPercentualEmMonetario()) //vDesc
                .subtract(getValorIcmsDesonerado()) //vICMSDeson
                .add(getValorIcmsSt()) //vST
                .add(getValorFrete()) //vFrete
                .add(getValorSeguro()) //vSeg
                .add(getAcrescimoMonetario()).add(getAcrescimoPercentualEmMonetario()) //vOutro
                //.add(...) //vII
                //.add(...) //vIPI
                //.add(...) //vServ
                );
    }
    
    public String getDescricaoItemMontado() {
        String descricao = getDescricao();
        
        if(getTamanho() != null && getMontagemItens().isEmpty()) {
            descricao += " " + getTamanho().getNome();
        }
        
        for(MovimentoFisico montagemItem : getMontagemItens()) {
            descricao += "\r\n 1/" + getMontagemItens().size() + " " + montagemItem.getDescricao();
        }
        
        return descricao;
    }
    
    public BigDecimal getTotalTributos() {
        return getValorAproximadoTributosFederais().add(getValorAproximadoTributosEstaduais());
    }
    
    public BigDecimal getValorAproximadoTributosFederais() {
        if(getNcm() == null) {
            return BigDecimal.ZERO;
        }
        return FiscalUtil.calcularValorAproximadoTributosFederais(getNcm(), getSubtotalItem());
    }
    
    public BigDecimal getValorAproximadoTributosEstaduais() {
        if(getNcm() == null) {
            return BigDecimal.ZERO;
        }
        return FiscalUtil.calcularValorAproximadoTributosEstaduais(getNcm(), getSubtotalItem());
    }
    
    public MovimentoFisico deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            MovimentoFisico clone = (MovimentoFisico) ois.readObject();
            clone.setId(null);
            return clone;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro em deepClone " + e);
            return null;
        }
    }

    @Override
    public String toString() {
        return getDescricao();
    }

    
    
    @Override
    public int compareTo(MovimentoFisico o) {
        return id.compareTo(o.getId());
    }
}
