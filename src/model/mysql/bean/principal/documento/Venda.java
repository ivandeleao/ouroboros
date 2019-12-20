/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.fiscal.MeioDePagamento;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.fiscal.SatCupom;
import model.mysql.bean.fiscal.SatCupomTipo;
import model.mysql.bean.fiscal.nfe.ConsumidorFinal;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import model.mysql.bean.fiscal.nfe.DocumentoReferenciado;
import model.mysql.bean.fiscal.nfe.FinalidadeEmissao;
import model.mysql.bean.fiscal.nfe.ModalidadeFrete;
import model.mysql.bean.fiscal.nfe.NaturezaOperacao;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
import model.mysql.bean.fiscal.nfe.TipoAtendimento;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoStatus;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.dao.endereco.CidadeDAO;
import model.nosql.NfeStatusEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;
import util.Decimal;
import util.Texto;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "criacao"),
    @Index(columnList = "encerramento"),
    @Index(columnList = "comanda")
})
public class Venda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    @CreationTimestamp
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "tipoOperacaoId", columnDefinition = "integer default 1")
    private TipoOperacao tipoOperacao;

    @ManyToOne
    @JoinColumn(name = "vendaTipoId")
    private VendaTipo vendaTipo;

    @ManyToOne
    @JoinColumn(name = "funcionarioId")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "clienteId")
    private Pessoa cliente;

    @ManyToOne
    @JoinColumn(name = "veiculoId")
    private Veiculo veiculo;

    private LocalDateTime cancelamento; //desliga financeiro e estoque relacionados
    private String motivoCancelamento;

    @Column(columnDefinition = "boolean default false")
    private Boolean orcamento; //desliga financeiro e estoque relacionados

    private Timestamp encerramento;
    private Integer comanda;
    private String comandaNome;

    private BigDecimal acrescimoMonetarioProdutos;
    private BigDecimal acrescimoPercentualProdutos;
    private BigDecimal descontoMonetarioProdutos;
    private BigDecimal descontoPercentualProdutos;

    private BigDecimal acrescimoMonetarioServicos;
    private BigDecimal acrescimoPercentualServicos;
    private BigDecimal descontoMonetarioServicos;
    private BigDecimal descontoPercentualServicos;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @OrderBy
    private List<MovimentoFisico> movimentosFisicos = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true) //2019-09-03 adicionado orphanRemoval
    @OrderBy
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Parcela> parcelas = new ArrayList<>();

    @Column(length = 1000)
    private String relato; //Descrição do que deve ser feito no serviço

    @Column(length = 1000)
    private String observacao;
    
    //------------------- relacionamento circular
    @OneToMany(mappedBy = "documentoPai", cascade = CascadeType.ALL)
    private List<Venda> documentosFilho = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "documentoPaiId")
    private Venda documentoPai;
    //-------------------

    //Delivery------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "meioDePagamentoId")
    private MeioDePagamento meioDePagamento;

    private BigDecimal valorReceber;

    private String enderecoEntrega;
    //Fim Delivery--------------------------------------------------------------

    //fiscal
    private LocalDateTime nfseDataHora;

    private String chaveAcessoNfe;
    private String protocoloNfe;
    private Integer serieNfe;
    private Integer numeroNfe;
    private LocalDateTime dataHoraEmissaoNfe;
    private LocalDateTime dataHoraSaidaEntradaNfe;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @OrderBy
    private List<DocumentoReferenciado> documentosReferenciados = new ArrayList<>();

    private LocalDateTime cartaCorrecaoDataHora;
    private String cartaCorrecaoMotivo;
    private Integer cartaCorrecaoSequencia;

    private LocalDateTime cancelamentoNfe;
    private String motivoCancelamentoNfe;

    @ManyToOne
    @JoinColumn(name = "regimeTributarioId")
    private RegimeTributario regimeTributario;

    @ManyToOne
    @JoinColumn(name = "naturezaOperacaoId")
    private NaturezaOperacao naturezaOperacao;

    @ManyToOne
    @JoinColumn(name = "tipoAtendimentoId")
    private TipoAtendimento tipoAtendimento;

    @ManyToOne
    @JoinColumn(name = "consumidorFinalId")
    private ConsumidorFinal consumidorFinal;

    @ManyToOne
    @JoinColumn(name = "destinoOperacaoId")
    private DestinoOperacao destinoOperacao;

    @ManyToOne
    @JoinColumn(name = "finalidadeEmissaoId")
    private FinalidadeEmissao finalidadeEmissao;

    //Entrega-------------------------------------------------------------------
    private boolean entregaDiferente;

    private String entregaCnpj;
    private String entregaCpf;
    private String entregaIe;
    private String entregaNome;

    private String entregaCep;
    private String entregaEndereco;
    private String entregaNumero;
    private String entregaComplemento;
    private String entregaBairro;
    private String entregaCodigoMunicipio; //Traz município e UF

    private String entregaTelefone;
    private String entregaEmail;
    //Fim Entrega---------------------------------------------------------------

    //Transporte----------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "modalidadeFreteId")
    private ModalidadeFrete modalidadeFrete;
    /*
    private String transportadorCnpj;
    private String transportadorCpf;
    private String transportadorIe;
    private String transportadorNome;
    
    private String transportadorCep;
    private String transportadorEndereco;
    private String transportadorNumero;
    private String transportadorComplemento;
    private String transportadorBairro;
    private String transportadorCodigoMunicipio; //Traz município e UF
     */
    //Fim Transporte------------------------------------------------------------

    @Column(length = 2000)
    private String informacoesAdicionaisFisco;

    @Column(length = 5000)
    private String informacoesComplementaresContribuinte;

    private String destCpfCnpj;

    @OneToMany(mappedBy = "venda") //, cascade = CascadeType.ALL)
    @OrderBy
    private List<SatCupom> satCupons = new ArrayList<>();

    //Cache---------------------------------------------------------------------
    @Enumerated(EnumType.ORDINAL)
    private VendaStatus vendaStatus;
    private BigDecimal totalProdutos;
    private BigDecimal totalServicos;
    //Fim Cache-----------------------------------------------------------------
    
    private LocalDateTime ultimaImpressaoCupom;
    
    protected Venda() {
        //this.vendaTipo = VendaTipo.VENDA;
    }

    public Venda(VendaTipo vendaTipo) {
        this(vendaTipo, false);
    }

    public Venda(VendaTipo vendaTipo, boolean orcamento) {
        this.vendaTipo = vendaTipo;
        if (vendaTipo.equals(VendaTipo.COMPRA)) {
            this.tipoOperacao = TipoOperacao.ENTRADA;
        } else {
            this.tipoOperacao = TipoOperacao.SAIDA;
        }

        this.vendaTipo = vendaTipo;
        this.orcamento = orcamento;
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Timestamp atualizacao) {
        this.atualizacao = atualizacao;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public VendaTipo getVendaTipo() {
        return vendaTipo;
    }

    public void setVendaTipo(VendaTipo vendaTipo) {
        this.vendaTipo = vendaTipo;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Pessoa getPessoa() {
        return cliente;
    }

    public void setPessoa(Pessoa cliente) {
        this.cliente = cliente;
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

    public Boolean isOrcamento() {
        return orcamento == null ? false : orcamento;
    }

    public void setOrcamento(Boolean orcamento) {
        this.orcamento = orcamento;
    }

    public Timestamp getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Timestamp encerramento) {
        this.encerramento = encerramento;
    }

    public Integer getComanda() {
        return comanda;
    }

    public void setComanda(Integer comanda) {
        this.comanda = comanda;
    }

    public String getComandaNome() {
        return comandaNome != null ? comandaNome : "";
    }

    public void setComandaNome(String comandaNome) {
        this.comandaNome = comandaNome;
    }

    public BigDecimal getAcrescimoMonetarioProdutos() {
        return acrescimoMonetarioProdutos != null ? acrescimoMonetarioProdutos : BigDecimal.ZERO;
    }

    public void setAcrescimoMonetarioProdutos(BigDecimal acrescimoMonetarioProdutos) {
        this.acrescimoMonetarioProdutos = acrescimoMonetarioProdutos;
    }

    public BigDecimal getAcrescimoPercentualProdutos() {
        return acrescimoPercentualProdutos != null ? acrescimoPercentualProdutos : BigDecimal.ZERO;
    }

    public void setAcrescimoPercentualProdutos(BigDecimal acrescimoPercentualProdutos) {
        this.acrescimoPercentualProdutos = acrescimoPercentualProdutos;
    }

    public BigDecimal getDescontoMonetarioProdutos() {
        return descontoMonetarioProdutos != null ? descontoMonetarioProdutos : BigDecimal.ZERO;
    }

    public void setDescontoMonetarioProdutos(BigDecimal descontoMonetarioProdutos) {
        this.descontoMonetarioProdutos = descontoMonetarioProdutos;
    }

    public BigDecimal getDescontoPercentualProdutos() {
        return descontoPercentualProdutos != null ? descontoPercentualProdutos : BigDecimal.ZERO;
    }

    public void setDescontoPercentualProdutos(BigDecimal descontoPercentualProdutos) {
        this.descontoPercentualProdutos = descontoPercentualProdutos;
    }

    public BigDecimal getAcrescimoMonetarioServicos() {
        return acrescimoMonetarioServicos != null ? acrescimoMonetarioServicos : BigDecimal.ZERO;
    }

    public void setAcrescimoMonetarioServicos(BigDecimal acrescimoMonetarioServicos) {
        this.acrescimoMonetarioServicos = acrescimoMonetarioServicos;
    }

    public BigDecimal getAcrescimoPercentualServicos() {
        return acrescimoPercentualServicos != null ? acrescimoPercentualServicos : BigDecimal.ZERO;
    }

    public void setAcrescimoPercentualServicos(BigDecimal acrescimoPercentualServicos) {
        this.acrescimoPercentualServicos = acrescimoPercentualServicos;
    }

    public BigDecimal getDescontoMonetarioServicos() {
        return descontoMonetarioServicos != null ? descontoMonetarioServicos : BigDecimal.ZERO;
    }

    public void setDescontoMonetarioServicos(BigDecimal descontoMonetarioServicos) {
        this.descontoMonetarioServicos = descontoMonetarioServicos;
    }

    public BigDecimal getDescontoPercentualServicos() {
        return descontoPercentualServicos != null ? descontoPercentualServicos : BigDecimal.ZERO;
    }

    public void setDescontoPercentualServicos(BigDecimal descontoPercentualServicos) {
        this.descontoPercentualServicos = descontoPercentualServicos;
    }

    public LocalDateTime getNfseDataHora() {
        return nfseDataHora;
    }

    public void setNfseDataHora(LocalDateTime nfseDataHora) {
        this.nfseDataHora = nfseDataHora;
    }

    public String getChaveAcessoNfe() {
        return chaveAcessoNfe != null ? chaveAcessoNfe : "";
    }

    public void setChaveAcessoNfe(String chaveAcessoNfe) {
        this.chaveAcessoNfe = chaveAcessoNfe;
    }

    public String getProtocoloNfe() {
        return protocoloNfe;
    }

    public void setProtocoloNfe(String protocoloNfe) {
        this.protocoloNfe = protocoloNfe;
    }

    public Integer getSerieNfe() {
        return serieNfe;
    }

    public void setSerieNfe(Integer serieNfe) {
        this.serieNfe = serieNfe;
    }

    public Integer getNumeroNfe() {
        return numeroNfe;
    }

    public void setNumeroNfe(Integer numeroNfe) {
        this.numeroNfe = numeroNfe;
    }

    public LocalDateTime getDataHoraEmissaoNfe() {
        return dataHoraEmissaoNfe;
    }

    public void setDataHoraEmissaoNfe(LocalDateTime dataHoraEmissaoNfe) {
        this.dataHoraEmissaoNfe = dataHoraEmissaoNfe;
    }

    public LocalDateTime getDataHoraSaidaEntradaNfe() {
        return dataHoraSaidaEntradaNfe;
    }

    public void setDataHoraSaidaEntradaNfe(LocalDateTime dataHoraSaidaEntradaNfe) {
        this.dataHoraSaidaEntradaNfe = dataHoraSaidaEntradaNfe;
    }

    public List<DocumentoReferenciado> getDocumentosReferenciados() {
        return documentosReferenciados;
    }

    public void setDocumentosReferenciados(List<DocumentoReferenciado> documentosReferenciados) {
        this.documentosReferenciados = documentosReferenciados;
    }

    public LocalDateTime getCartaCorrecaoDataHora() {
        return cartaCorrecaoDataHora;
    }

    public void setCartaCorrecaoDataHora(LocalDateTime cartaCorrecaoDataHora) {
        this.cartaCorrecaoDataHora = cartaCorrecaoDataHora;
    }

    public String getCartaCorrecaoMotivo() {
        return cartaCorrecaoMotivo;
    }

    public void setCartaCorrecaoMotivo(String cartaCorrecaoMotivo) {
        this.cartaCorrecaoMotivo = cartaCorrecaoMotivo;
    }

    public Integer getCartaCorrecaoSequencia() {
        return cartaCorrecaoSequencia != null ? cartaCorrecaoSequencia : 0;
    }

    public void setCartaCorrecaoSequencia(Integer cartaCorrecaoSequencia) {
        this.cartaCorrecaoSequencia = cartaCorrecaoSequencia;
    }

    public LocalDateTime getCancelamentoNfe() {
        return cancelamentoNfe;
    }

    public void setCancelamentoNfe(LocalDateTime cancelamentoNfe) {
        this.cancelamentoNfe = cancelamentoNfe;
    }

    public String getMotivoCancelamentoNfe() {
        return motivoCancelamentoNfe;
    }

    public void setMotivoCancelamentoNfe(String motivoCancelamentoNfe) {
        this.motivoCancelamentoNfe = motivoCancelamentoNfe;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public NaturezaOperacao getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(NaturezaOperacao naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public ConsumidorFinal getConsumidorFinal() {
        return consumidorFinal;
    }

    public void setConsumidorFinal(ConsumidorFinal consumidorFinal) {
        this.consumidorFinal = consumidorFinal;
    }

    public DestinoOperacao getDestinoOperacao() {
        return destinoOperacao;
    }

    public void setDestinoOperacao(DestinoOperacao destinoOperacao) {
        this.destinoOperacao = destinoOperacao;
    }

    public FinalidadeEmissao getFinalidadeEmissao() {
        return finalidadeEmissao;
    }

    public void setFinalidadeEmissao(FinalidadeEmissao finalidadeEmissao) {
        this.finalidadeEmissao = finalidadeEmissao;
    }

    public boolean isEntregaDiferente() {
        return entregaDiferente;
    }

    public void setEntregaDiferente(boolean entregaDiferente) {
        this.entregaDiferente = entregaDiferente;
    }

    public String getEntregaCnpj() {
        return Texto.parse(entregaCnpj);
    }

    public void setEntregaCnpj(String entregaCnpj) {
        this.entregaCnpj = entregaCnpj;
    }

    public String getEntregaCpf() {
        return Texto.parse(entregaCpf);
    }

    public void setEntregaCpf(String entregaCpf) {
        this.entregaCpf = entregaCpf;
    }

    public String getEntregaIe() {
        return entregaIe;
    }

    public void setEntregaIe(String entregaIe) {
        this.entregaIe = entregaIe;
    }

    public String getEntregaNome() {
        return entregaNome;
    }

    public void setEntregaNome(String entregaNome) {
        this.entregaNome = entregaNome;
    }

    public String getEntregaCep() {
        return entregaCep;
    }

    public void setEntregaCep(String entregaCep) {
        this.entregaCep = entregaCep;
    }

    public String getEntregaEndereco() {
        return entregaEndereco;
    }

    public void setEntregaEndereco(String entregaEndereco) {
        this.entregaEndereco = entregaEndereco;
    }

    public String getEntregaNumero() {
        return entregaNumero;
    }

    public void setEntregaNumero(String entregaNumero) {
        this.entregaNumero = entregaNumero;
    }

    public String getEntregaComplemento() {
        return entregaComplemento;
    }

    public void setEntregaComplemento(String entregaComplemento) {
        this.entregaComplemento = entregaComplemento;
    }

    public String getEntregaBairro() {
        return entregaBairro;
    }

    public void setEntregaBairro(String entregaBairro) {
        this.entregaBairro = entregaBairro;
    }

    public String getEntregaCodigoMunicipio() {
        return entregaCodigoMunicipio;
    }

    public void setEntregaCodigoMunicipio(String entregaCodigoMunicipio) {
        this.entregaCodigoMunicipio = entregaCodigoMunicipio;
    }

    public String getEntregaTelefone() {
        return entregaTelefone;
    }

    public void setEntregaTelefone(String entregaTelefone) {
        this.entregaTelefone = entregaTelefone;
    }

    public String getEntregaEmail() {
        return entregaEmail;
    }

    public void setEntregaEmail(String entregaEmail) {
        this.entregaEmail = entregaEmail;
    }

    public String getInformacoesAdicionaisFisco() {
        return informacoesAdicionaisFisco;
    }

    public ModalidadeFrete getModalidadeFrete() {
        return modalidadeFrete;
    }

    public void setModalidadeFrete(ModalidadeFrete modalidadeFrete) {
        this.modalidadeFrete = modalidadeFrete;
    }

    public void setInformacoesAdicionaisFisco(String informacoesAdicionaisFisco) {
        this.informacoesAdicionaisFisco = informacoesAdicionaisFisco;
    }

    public String getInformacoesComplementaresContribuinte() {
        return informacoesComplementaresContribuinte;
    }

    public void setInformacoesComplementaresContribuinte(String informacoesComplementaresContribuinte) {
        this.informacoesComplementaresContribuinte = informacoesComplementaresContribuinte;
    }

    public String getDestCpfCnpj() {
        return destCpfCnpj;
    }

    public void setDestCpfCnpj(String destCpfCnpj) {
        this.destCpfCnpj = destCpfCnpj;
    }

    public String getRelato() {
        return relato != null ? relato : "";
    }

    public void setRelato(String relato) {
        this.relato = relato;
    }

    public String getObservacao() {
        return observacao != null ? observacao : "";
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public BigDecimal getValorReceber() {
        return valorReceber != null ? valorReceber : BigDecimal.ZERO;
    }

    public void setValorReceber(BigDecimal valorReceber) {
        this.valorReceber = valorReceber;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<SatCupom> getSatCupons() {
        return satCupons;
    }

    public void setSatCupons(List<SatCupom> satCupons) {
        this.satCupons = satCupons;
    }

    public LocalDateTime getUltimaImpressaoCupom() {
        return ultimaImpressaoCupom;
    }

    public void setUltimaImpressaoCupom(LocalDateTime ultimaImpressaoCupom) {
        this.ultimaImpressaoCupom = ultimaImpressaoCupom;
    }

    public List<Venda> getDocumentosFilho() {
        //ignorar itens removidos
        return documentosFilho.stream().filter(doc -> doc.getCancelamento() == null).collect(Collectors.toList());
    }

    public void setDocumentosFilho(List<Venda> documentosFilho) {
        this.documentosFilho = documentosFilho;
    }

    public Venda getDocumentoPai() {
        return documentoPai;
    }

    public void setDocumentoPai(Venda documentoPai) {
        this.documentoPai = documentoPai;
    }
    
    
    
    //--------------------------------------------------------------------------
    
    public void addDocumentoFilho(Venda documentoFilho) {
        documentosFilho.remove(documentoFilho);
        documentosFilho.add(documentoFilho);
        documentoFilho.setDocumentoPai(this);
    }
    
    public void removeDocumentoFilho(Venda documentoFilho) {
        documentoFilho.setDocumentoPai(null);
        this.documentosFilho.remove(documentoFilho);
    }

    //--------------------------------------------------------------------------
    public BigDecimal getValorTroco() {
        if (getValorReceber().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return getValorReceber().subtract(getTotal());
    }

    public boolean hasNfse() {
        return getNfseDataHora() != null;
    }

    public boolean hasCupomSat() {
        return !getSatCupons().isEmpty();
    }
    
    public boolean hasUltimoCupomSatCancelado() {
        if(getSatCupons().isEmpty()) {
            return false;
        }
        return getSatCupons().get(getSatCupons().size() - 1).getSatCupomTipo().equals(SatCupomTipo.CANCELAMENTO);
    }

    public boolean hasNfe() {
        return !getChaveAcessoNfe().isEmpty();
    }
    
    public boolean hasDocumentosFilho() {
        return !getDocumentosFilho().isEmpty();
    }
    
    public boolean hasDocumentoPai() {
        return getDocumentoPai() != null;
    }
    
    public NfeStatusEnum getStatusNfe() {
        if(getCancelamentoNfe() != null) {
            return NfeStatusEnum.CANCELADA;
            
        } else if(getDataHoraEmissaoNfe() != null) {
            return NfeStatusEnum.EMITIDA;
            
        } else {
            return NfeStatusEnum.DIGITAÇÃO;
        }
    }

    /**
     *
     * @return movimentos não estornados entrada/saída de acordo com o
     * TipoOperacao do documento
     */
    public List<MovimentoFisico> getMovimentosFisicos() {
        if (getTipoOperacao().equals(TipoOperacao.ENTRADA)) {
            return getMovimentosFisicosEntrada();

        } else {
            return getMovimentosFisicosSaida();

        }
    }

    /**
     *
     * @return movimentosFisicos de entrada não estornados
     */
    public List<MovimentoFisico> getMovimentosFisicosEntrada() {
        List<MovimentoFisico> itensAtivos = new ArrayList<>();
        for (MovimentoFisico item : movimentosFisicos) {
            if (item.getEstorno() == null && item.getEstornoOrigem() == null && item.getEntrada().compareTo(BigDecimal.ZERO) > 0) {
                itensAtivos.add(item);
            }
        }
        return itensAtivos;
    }

    public List<MovimentoFisico> getMovimentosFisicosEntradaProdutos() {
        List<MovimentoFisico> itensProdutos = new ArrayList<>();
        getMovimentosFisicosEntrada().stream().filter((itemProduto) -> (itemProduto.getProdutoTipo().equals(ProdutoTipo.PRODUTO))).forEachOrdered((itemProduto) -> {
            itensProdutos.add(itemProduto);
        });
        return itensProdutos;
    }

    public List<MovimentoFisico> getMovimentosFisicosEntradaServicos() {
        List<MovimentoFisico> itensProdutos = new ArrayList<>();
        getMovimentosFisicosEntrada().stream().filter((itemProduto) -> (itemProduto.getProdutoTipo().equals(ProdutoTipo.SERVICO))).forEachOrdered((itemProduto) -> {
            itensProdutos.add(itemProduto);
        });
        return itensProdutos;
    }

    /**
     *
     * @return movimentosFisicos de saída não estornados
     */
    public List<MovimentoFisico> getMovimentosFisicosSaida() {
        List<MovimentoFisico> itensAtivos = new ArrayList<>();
        //itens de vendas filho
        for(Venda docFilho : getDocumentosFilho()) {
            itensAtivos.addAll(docFilho.getMovimentosFisicosSaida());
        }
        //
        for (MovimentoFisico item : movimentosFisicos) {
            if (item.getEstorno() == null
                    && item.getEstornoOrigem() == null
                    && item.getSaida().compareTo(BigDecimal.ZERO) > 0
                    && item.getMontagemOrigem() == null) {
                itensAtivos.add(item);
            }
        }
        return itensAtivos;
    }

    /**
     *
     * @return movimentos não estornados entrada/saída de acordo com o
     * TipoOperacao do documento
     */
    public List<MovimentoFisico> getMovimentosFisicosProdutos() {
        List<MovimentoFisico> itensProdutos = new ArrayList<>();
        getMovimentosFisicos().stream().filter((itemProduto) -> (itemProduto.getProdutoTipo().equals(ProdutoTipo.PRODUTO))).forEachOrdered((itemProduto) -> {
            itensProdutos.add(itemProduto);
        });
        return itensProdutos;
    }

    /**
     *
     * @return movimentos não estornados entrada/saída de acordo com o
     * TipoOperacao do documento
     */
    public List<MovimentoFisico> getMovimentosFisicosServicos() {
        List<MovimentoFisico> itensProdutos = new ArrayList<>();
        getMovimentosFisicos().stream().filter((itemProduto) -> (itemProduto.getProdutoTipo().equals(ProdutoTipo.SERVICO))).forEachOrdered((itemProduto) -> {
            itensProdutos.add(itemProduto);
        });
        return itensProdutos;
    }

    /**
     *
     * @return movimentosFisicos de devolucao não estornados
     */
    public List<MovimentoFisico> getMovimentosFisicosDevolucao() {
        List<MovimentoFisico> mfDevolucoes = new ArrayList<>();
        for (MovimentoFisico mf : movimentosFisicos) {
            if (mf.getEstorno() == null && mf.getEstornoOrigem() == null && mf.getDevolucao() != null) {
                mfDevolucoes.add(mf.getDevolucao());
            }
        }
        return mfDevolucoes;

    }

    public void setMovimentosFisicos(List<MovimentoFisico> movimentosFisicos) {
        this.movimentosFisicos = movimentosFisicos;
    }

    public List<Parcela> getParcelasAPrazo() {
        List<Parcela> parcelasAPrazo = new ArrayList<>();
        for (Parcela parcela : getParcelas()) {
            if (parcela.getVencimento() != null) {
                parcelasAPrazo.add(parcela);
            }
        }
        return parcelasAPrazo;
    }

    public List<Parcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<Parcela> parcelas) {
        this.parcelas = parcelas;
    }

    //--------------------------------------------------------------------------
    /**
     *
     * @return Tipo do documento seguido do literal orçamento, cancelado, etc
     */
    public String getTitulo() {
        String titulo = getVendaTipo().getNome();
        titulo += isOrcamento() ? " - ORÇAMENTO" : "";
        titulo += getCancelamento() != null ? " - CANCELADO" : "";
        return titulo;
    }

    public VendaStatus setVendaStatus() {
        VendaStatus status = VendaStatus.AGUARDANDO;
        MovimentoFisicoStatus tempMfStatus = MovimentoFisicoStatus.AGUARDANDO;

        if (getCancelamento() != null) {
            status = VendaStatus.CANCELADO;
            
        } else if (isOrcamento()) {
            status = VendaStatus.ORÇAMENTO;
            
        } else {

            /*if (getVendaTipo().equals(VendaTipo.VENDA)) {
                return VendaStatus.ENTREGA_CONCLUÍDA;
            }*/
            //Compara todos os vendaStatus...
            Map<MovimentoFisicoStatus, VendaStatus> mapStatus = new LinkedHashMap<>();
            mapStatus.put(MovimentoFisicoStatus.RECEBIMENTO_CONCLUÍDO, VendaStatus.RECEBIMENTO_CONCLUÍDO);
            mapStatus.put(MovimentoFisicoStatus.RECEBIMENTO_ATRASADO, VendaStatus.RECEBIMENTO_ATRASADO);
            mapStatus.put(MovimentoFisicoStatus.RECEBIMENTO_PREVISTO, VendaStatus.RECEBIMENTO_PREVISTO);
            mapStatus.put(MovimentoFisicoStatus.ENTREGA_CONCLUÍDA, VendaStatus.ENTREGA_CONCLUÍDA);
            mapStatus.put(MovimentoFisicoStatus.ENTREGA_ATRASADA, VendaStatus.ENTREGA_ATRASADA);
            mapStatus.put(MovimentoFisicoStatus.ENTREGA_PREVISTA, VendaStatus.ENTREGA_PREVISTA);
            mapStatus.put(MovimentoFisicoStatus.PREPARAÇÃO_CONCLUÍDA, VendaStatus.PREPARAÇÃO_CONCLUÍDA);
            mapStatus.put(MovimentoFisicoStatus.PREPARAÇÃO_ATRASADA, VendaStatus.PREPARAÇÃO_ATRASADA);
            mapStatus.put(MovimentoFisicoStatus.PREPARAÇÃO_PREVISTA, VendaStatus.PREPARAÇÃO_PREVISTA);
            mapStatus.put(MovimentoFisicoStatus.ANDAMENTO, VendaStatus.ANDAMENTO);
            mapStatus.put(MovimentoFisicoStatus.AGUARDANDO, VendaStatus.AGUARDANDO);

            //System.out.println("getStatus vendaId: " + this.getId());
            outerLoop:
            for (Map.Entry<MovimentoFisicoStatus, VendaStatus> entryStatus : mapStatus.entrySet()) {
                //System.out.println("for map: " + entryStatus.getKey());

                for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                    //System.out.println("\t for mf.getId: " + mf.getId());

                    //Se já foi entregue, verificar vendaStatus da devolução
                    if (mf.getStatus() == MovimentoFisicoStatus.ENTREGA_CONCLUÍDA && mf.getDevolucao() != null) {
                        //se for igual memorizar para o caso de não estarem todos no mesmo vendaStatus
                        if (mf.getDevolucao().getStatus() == entryStatus.getKey()) {
                            //System.out.println("\t\t mf.getId: " + mf.getId() + " mf.getDevolucao().getStatus: " + mf.getDevolucao().getStatus());
                            tempMfStatus = entryStatus.getKey();
                        } else if (getMovimentosFisicosSaida().indexOf(mf) == getMovimentosFisicosSaida().size() - 1) {
                            //return entryStatus.getValue();
                            status = entryStatus.getValue();
                            break outerLoop;
                        }
                    } else {
                        //System.out.println("\t\t else: mf.getStatus: " + mf.getStatus());
                        //se for igual memorizar para o caso de não estarem todos no mesmo vendaStatus
                        if (mf.getStatus() == entryStatus.getKey()) {
                            //System.out.println("\t\t mf.getId: " + mf.getId() + " mf.getStatus: " + mf.getStatus());
                            tempMfStatus = entryStatus.getKey();
                        } else if (getMovimentosFisicosSaida().indexOf(mf) == getMovimentosFisicosSaida().size() - 1) {
                            //return mapStatus.get(mf.getStatus());
                            status = mapStatus.get(mf.getStatus());
                            break outerLoop;
                        }
                    }
                }
            }
        }
        
        //System.out.println("fim status: " + status.toString());
        
        vendaStatus = status;

        return status;

    }
    
    
    public VendaStatus getVendaStatus() {
        //System.out.println("status: " + vendaStatus);
        //System.out.println("getStatus: " + setVendaStatus());
        return vendaStatus != null ? vendaStatus : setVendaStatus();
    }

    public MovimentoFisico findMovimentoFisico(MovimentoFisico movimentoFisico) {
        //System.out.println("Encontrar - movimentoFisico: " + movimentoFisico.getId());
        for (MovimentoFisico mf : this.movimentosFisicos) {
            if (mf.getId().equals(movimentoFisico.getId())) {
                System.out.println("findMovimentoFisico id: " + mf.getId());
                return mf;
            }
        }
        return null;
    }

    public void addMovimentoFisico(MovimentoFisico movimentoFisico) {
        movimentosFisicos.remove(movimentoFisico);
        movimentosFisicos.add(movimentoFisico);
        movimentoFisico.setVenda(this);
    }

    public void removeMovimentoFisico(MovimentoFisico movimentoFisico) {
        //movimentoFisico = findMovimentoFisico(movimentoFisico);
        movimentoFisico.setVenda(null);
        movimentosFisicos.remove(movimentoFisico);
    }

    public void addParcela(Parcela parcela) {
        parcelas.remove(parcela);
        parcelas.add(parcela);
        parcela.setVenda(this);
    }

    public void removeParcela(Parcela parcela) {
        parcela.setVenda(null);
        parcelas.remove(parcela);
    }

    public void addSatCupom(SatCupom satCupom) {
        satCupons.remove(satCupom);
        satCupons.add(satCupom);
        satCupom.setVenda(this);
    }

    public void removeSatCupom(SatCupom satCupom) {
        satCupom.setVenda(null);
        satCupons.remove(satCupom);
    }

    public void addDocumentoReferenciado(DocumentoReferenciado documentoReferenciado) {
        documentosReferenciados.remove(documentoReferenciado);
        documentosReferenciados.add(documentoReferenciado);
        documentoReferenciado.setVenda(this);
    }

    public void removeDocumentoReferenciado(DocumentoReferenciado documentoReferenciado) {
        documentoReferenciado.setVenda(null);
        documentosReferenciados.remove(documentoReferenciado);
    }

    public List<MovimentoFisico> getListMovimentoFisicoEager() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<MovimentoFisico> listMovimentoFisico = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MovimentoFisico> cq = cb.createQuery(MovimentoFisico.class);
            Root<MovimentoFisico> rootMovimentoFisico = cq.from(MovimentoFisico.class);

            rootMovimentoFisico.fetch("listMovimentoFisicoComponente", JoinType.LEFT);
            rootMovimentoFisico.fetch("produto", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootMovimentoFisico.get("venda"), this));

            cq.select(rootMovimentoFisico).where(predicates.toArray(new Predicate[]{}));

            //cq.orderBy(cb.asc(coalesce));
            TypedQuery<MovimentoFisico> query = em.createQuery(cq);

            listMovimentoFisico = (List<MovimentoFisico>) query.getResultList();

            return listMovimentoFisico;

        } catch (Exception e) {
            System.err.println("Erro em findProdutoCompostoPorPeriodo " + e);
        } finally {
            em.close();
        }
        return listMovimentoFisico;

    }

    public LocalDateTime getPrimeiraDataSaidaPrevista() {
        LocalDateTime primeiraData = null;

        List<MovimentoFisico> listMf = new ArrayList<>();
        for (MovimentoFisico mf : movimentosFisicos) {
            if (mf.getDataSaidaPrevista() != null) {
                listMf.add(mf);
            }
        }
        if (!listMf.isEmpty()) {
            listMf.sort(Comparator.comparing(MovimentoFisico::getDataSaidaPrevista));

            primeiraData = listMf.get(0).getDataSaidaPrevista();
        }

        return primeiraData;
    }

    public BigDecimal getTotalItens() {
        if (!getMovimentosFisicosSaida().isEmpty()) {
            return getMovimentosFisicosSaida().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();

        } else if (!getMovimentosFisicosEntrada().isEmpty()) {
            return getMovimentosFisicosEntrada().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();

        }
        return BigDecimal.ZERO;
    }

    /**
     *
     * @return total de valor unitário * quantidade
     */
    public BigDecimal getTotalItensProdutos() {
        BigDecimal total = BigDecimal.ZERO;

        if (getTipoOperacao().equals(TipoOperacao.SAIDA)) {
            for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                    total = total.add(mf.getSubtotalItem());
                }
            }

        } else {
            for (MovimentoFisico mf : getMovimentosFisicosEntrada()) {
                if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                    total = total.add(mf.getSubtotalItem());
                }
            }

        }

        return total;
    }

    public BigDecimal getTotalItensServicos() {
        BigDecimal total = BigDecimal.ZERO;

        if (getTipoOperacao().equals(TipoOperacao.SAIDA)) {
            for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                if (mf.getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                    total = total.add(mf.getSubtotalItem());
                }
            }
        } else {
            for (MovimentoFisico mf : getMovimentosFisicosEntrada()) {
                if (mf.getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                    total = total.add(mf.getSubtotalItem());
                }
            }
        }

        return total;
    }

    /**
     *
     * @return soma de acrescimoMonetario e acrescimoPercentualEmMonetario ou o
     * valor percentual aplicado em todos os itens
     */
    public BigDecimal getTotalAcrescimoProdutos() {
        if (!getMovimentosFisicosProdutos().isEmpty() && getTotalAcrescimoProdutosTipo().equals("%")) {
            return getMovimentosFisicosProdutos().get(0).getAcrescimoPercentual();

        } else {
            return getTotalAcrescimo(ProdutoTipo.PRODUTO);
        }
    }

    /**
     *
     * @return soma de acrescimoMonetario e acrescimoPercentualEmMonetario ou o
     * valor percentual aplicado em todos os itens
     */
    public BigDecimal getTotalAcrescimoServicos() {
        if (!getMovimentosFisicosServicos().isEmpty() && getTotalAcrescimoServicosTipo().equals("%")) {
            return getMovimentosFisicosServicos().get(0).getAcrescimoPercentual();

        } else {
            return getTotalAcrescimo(ProdutoTipo.SERVICO);
        }

    }

    private BigDecimal getTotalAcrescimo(ProdutoTipo produtoTipo) {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicos()) {
            if (mf.getProdutoTipo().equals(produtoTipo)) {
                total = total.add(mf.getAcrescimo());
            }
        }

        return total;
    }

    /**
     *
     * @return soma de descontoMonetario e descontoPercentualEmMonetario ou o
     * valor percentual aplicado em todos os itens
     */
    public BigDecimal getTotalDescontoProdutos() {
        if (!getMovimentosFisicosProdutos().isEmpty() && getTotalDescontoProdutosTipo().equals("%")) {
            return getMovimentosFisicosProdutos().get(0).getDescontoPercentual();

        } else {
            return getTotalDesconto(ProdutoTipo.PRODUTO);
        }
    }
    
    public BigDecimal getTotalDescontoProdutosEmMonetario() {
        return getTotalDesconto(ProdutoTipo.PRODUTO);
    }

    /**
     *
     * @return soma de descontoMonetario e descontoPercentualEmMonetario ou o
     * valor percentual aplicado em todos os itens
     */
    public BigDecimal getTotalDescontoServicos() {
        if (!getMovimentosFisicosServicos().isEmpty() && getTotalDescontoServicosTipo().equals("%")) {
            return getMovimentosFisicosServicos().get(0).getDescontoPercentual();

        } else {
            return getTotalDesconto(ProdutoTipo.SERVICO);
        }

    }

    private BigDecimal getTotalDesconto(ProdutoTipo produtoTipo) {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicos()) {
            if (mf.getProdutoTipo().equals(produtoTipo)) {
                total = total.add(mf.getDesconto());
            }
        }

        return total;
    }

    public BigDecimal getTotalFreteProdutos() {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            //if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                total = total.add(mf.getValorFrete());
            //}
        }

        return total;
    }

    public BigDecimal getTotalFreteServicos() {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicos()) {
            if (mf.getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                total = total.add(mf.getValorFrete());
            }
        }

        return total;
    }

    public BigDecimal getTotalSeguroProdutos() {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            if (mf.getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                total = total.add(mf.getValorSeguro());
            }
        }

        return total;
    }

    public BigDecimal getTotalSeguroServicos() {
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicos()) {
            if (mf.getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                total = total.add(mf.getValorSeguro());
            }
        }

        return total;
    }

    public BigDecimal getTotalValorAproximadoTributosFederais() {
        BigDecimal totalTributos = BigDecimal.ZERO;

        if (!getMovimentosFisicos().isEmpty()) {
            totalTributos = getMovimentosFisicos().stream().map(MovimentoFisico::getValorAproximadoTributosFederais).reduce(BigDecimal::add).get();
        }

        return totalTributos;
    }

    public BigDecimal getTotalValorAproximadoTributosEstaduais() {
        BigDecimal totalTributos = BigDecimal.ZERO;

        if (!getMovimentosFisicos().isEmpty()) {
            totalTributos = getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorAproximadoTributosEstaduais).reduce(BigDecimal::add).get();
        }

        return totalTributos;
    }

    /**
     *
     * @return soma dos subtotais dos movimentos físicos
     */
    public BigDecimal getTotal() {
        //return getTotalProdutosCache().add(getTotalServicosCache()).setScale(2, RoundingMode.HALF_UP);
        return getTotalProdutos().add(getTotalServicos()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     *
     * @return soma dos produtos mais acréscimos e menos descontos
     */
    public BigDecimal getTotalProdutos() {
        /*BigDecimal total = BigDecimal.ZERO;
        
        if (!getMovimentosFisicosProdutos().isEmpty()) {
            total = getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();
        }
        
        totalProdutos = total;

        return total;*/
        
        return totalProdutos != null ? totalProdutos : BigDecimal.ZERO;
    }
    
    public void setTotalProdutos() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (!getMovimentosFisicosProdutos().isEmpty()) {
            total = getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();
        }
        
        totalProdutos = total;
    }
    
    /*public BigDecimal getTotalProdutosCache() {
        return totalProdutos != null ? totalProdutos : getTotalProdutos();
    }*/
    
    /**
     *
     * @return soma dos serviços mais acréscimos e menos descontos
     */
    public BigDecimal getTotalServicos() {
        /*BigDecimal total = BigDecimal.ZERO;
        
        if (!getMovimentosFisicosServicos().isEmpty()) {
            total = getMovimentosFisicosServicos().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();
        }
        
        totalServicos = total;

        return total;*/
        
        return totalServicos != null ? totalServicos : BigDecimal.ZERO;
    }
    
    public void setTotalServicos() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (!getMovimentosFisicosServicos().isEmpty()) {
            total = getMovimentosFisicosServicos().stream().map(MovimentoFisico::getSubtotal).reduce(BigDecimal::add).get();
        }
        
        totalServicos = total;

    }
    
    /*public BigDecimal getTotalServicosCache() {
        return totalServicos != null ? totalServicos : getTotalServicos();
    }*/

    public BigDecimal getTotalRecebidoAVista() {
        BigDecimal totalRecebido = BigDecimal.ZERO;
        for (Parcela parcela : parcelas) {
            if (parcela.getVencimento() == null) {
                totalRecebido = totalRecebido.add(parcela.getValorQuitado());
            }
        }

        return totalRecebido.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalRecebidoAVistaProdutos() {

        return getTotalRecebidoAVista().multiply(getRateioProduto()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     *
     * @return proporção do valor de produtos no documento em relação aos
     * serviços representado por um valor entre 0 e 1
     */
    public BigDecimal getRateioProduto() {
        return getTotalItens().equals(BigDecimal.ZERO) ? BigDecimal.ZERO : getTotalItensProdutos().divide(getTotalItens(), 10, RoundingMode.HALF_UP);
    }

    /**
     *
     * @return proporção do valor de serviços no documento em relação aos
     * produtos representado por um valor entre 0 e 1
     */
    public BigDecimal getRateioServico() {
        return (BigDecimal.ONE).subtract(getRateioProduto()).setScale(10);
    }

    public BigDecimal getTotalRecebidoAPrazo() {
        BigDecimal totalRecebidoAPrazo = BigDecimal.ZERO;
        if (!getParcelasAPrazo().isEmpty()) {
            totalRecebidoAPrazo = getParcelasAPrazo().stream().map(Parcela::getValorQuitado).reduce(BigDecimal::add).get();
        }
        return totalRecebidoAPrazo;
    }

    public BigDecimal getTotalReceber() {
        return getTotal().subtract(getTotalRecebidoAVista());
    }

    public BigDecimal getTotalReceberProdutos() {
        return getTotalProdutos().subtract(getTotalRecebidoAVistaProdutos()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     *
     * @return Soma das parcelas a prazo
     */
    public BigDecimal getTotalAPrazo() {
        BigDecimal total = BigDecimal.ZERO;
        if (!getParcelasAPrazo().isEmpty()) {
            total = getParcelasAPrazo().stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        }
        return total;
    }

    /**
     *
     * @return Valor não recebido e sem parcelamento definido
     */
    public BigDecimal getTotalEmAberto() {
        //return getTotal().subtract(getTotalRecebidoAVista()).subtract(getTotalAPrazo());
        //System.out.println("getTotal().subtract(getTotalRecebidoAVista()).subtract(getTotalAPrazo()).setScale(2, RoundingMode.HALF_UP)");
        //System.out.println("getTotal(): " + getTotal());
        //System.out.println("getTotalRecebidoAVista(): " + getTotalRecebidoAVista());
        //System.out.println("getTotalAPrazo(): " + getTotalAPrazo());
        return getTotal().subtract(getTotalRecebidoAVista()).subtract(getTotalAPrazo()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTroco() {
        BigDecimal troco = BigDecimal.ZERO;
        for (Parcela parcela : parcelas) {
            troco = troco.add(parcela.getTroco());
        }
        return troco;
    }

    public List<CaixaItem> getRecebimentos() {
        List<CaixaItem> recebimentos = new ArrayList<>();
        for (Parcela parcela : getParcelas()) {
            recebimentos.addAll(parcela.getRecebimentos());
        }
        return recebimentos;
    }

    public Map<MeioDePagamento, BigDecimal> getRecebimentosAgrupadosPorMeioDePagamento() {
        Map<MeioDePagamento, BigDecimal> recebimentos = new HashMap<>();
        for (Parcela parcela : getParcelas()) {
            for (CaixaItem caixaItem : parcela.getRecebimentos()) {
                if (caixaItem.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                    recebimentos.merge(caixaItem.getMeioDePagamento(), caixaItem.getCredito(), BigDecimal::add);
                }
            }
        }

        return recebimentos;
    }

    public Map<MeioDePagamento, BigDecimal> getRecebimentosAgrupadosPorMeioDePagamentoProdutos() {
        Map<MeioDePagamento, BigDecimal> recebimentosProdutos = new HashMap<>();

        for (Map.Entry<MeioDePagamento, BigDecimal> entry : getRecebimentosAgrupadosPorMeioDePagamento().entrySet()) {

            recebimentosProdutos.put(entry.getKey(), entry.getValue().multiply(getRateioProduto()));
        }

        return recebimentosProdutos;
    }

    /**
     *
     * @return % se todos os itens tiverem % no mesmo valor, $ para todas as
     * outras situações
     */
    public String getTotalAcrescimoProdutosTipo() {
        if (!getMovimentosFisicosProdutos().isEmpty()) {

            BigDecimal primeiroValor = getMovimentosFisicosProdutos().get(0).getAcrescimoPercentual();
            if (primeiroValor.compareTo(BigDecimal.ZERO) == 0) {
                return "$";
            }

            for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
                if (mf.getAcrescimoPercentual().compareTo(primeiroValor) != 0) {
                    return "$";
                }
            }
        }
        return "%";
    }

    /**
     *
     * @return % se todos os itens tiverem % no mesmo valor, $ para todas as
     * outras situações
     */
    public String getTotalAcrescimoServicosTipo() {
        if (!getMovimentosFisicosServicos().isEmpty()) {

            BigDecimal primeiroValor = getMovimentosFisicosServicos().get(0).getAcrescimoPercentual();
            if (primeiroValor.compareTo(BigDecimal.ZERO) == 0) {
                return "$";
            }

            for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
                if (mf.getAcrescimoPercentual().compareTo(primeiroValor) != 0) {
                    return "$";
                }
            }
        }
        return "%";
    }

    /**
     *
     * @return % se todos os itens tiverem % no mesmo valor, $ para todas as
     * outras situações
     */
    public String getTotalDescontoProdutosTipo() {
        if (!getMovimentosFisicosProdutos().isEmpty()) {

            BigDecimal primeiroValor = getMovimentosFisicosProdutos().get(0).getDescontoPercentual();
            if (primeiroValor.compareTo(BigDecimal.ZERO) == 0) {
                return "$";
            }

            for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
                if (mf.getDescontoPercentual().compareTo(primeiroValor) != 0) {
                    return "$";
                }
            }
        }
        return "%";
    }

    /**
     *
     * @return % se todos os itens tiverem % no mesmo valor, $ para todas as
     * outras situações
     */
    public String getTotalDescontoServicosTipo() {
        if (!getMovimentosFisicosServicos().isEmpty()) {

            BigDecimal primeiroValor = getMovimentosFisicosServicos().get(0).getDescontoPercentual();
            if (primeiroValor.compareTo(BigDecimal.ZERO) == 0) {
                return "$";
            }

            for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
                if (mf.getDescontoPercentual().compareTo(primeiroValor) != 0) {
                    return "$";
                }
            }
        }
        return "%";
    }

    /**
     *
     * @return soma de acréscimo monetário e percentual
     */
    public BigDecimal getAcrescimoConsolidado() {
        return getAcrescimoConsolidadoProdutos().add(getAcrescimoConsolidadoServicos());
    }

    public BigDecimal getAcrescimoConsolidadoProdutos() {
        return getAcrescimoMonetarioProdutos().add(getAcrescimoPercentualEmMonetarioProdutos());
    }

    public BigDecimal getAcrescimoConsolidadoServicos() {
        return getAcrescimoMonetarioServicos().add(getAcrescimoPercentualEmMonetarioServicos());
    }

    /**
     *
     * @return acréscimo monetário ou percentual com símbolo de porcentagem
     */
    public String getTotalAcrescimoFormatado() {
        String total = Decimal.toString(getTotalAcrescimoProdutos());

        if (getTotalAcrescimoProdutosTipo().equals("%")) {
            total += "%";
        }
        return total;
    }

    public String getTotalAcrescimoFormatadoProdutos() {
        String total = Decimal.toString(getTotalAcrescimoProdutos());

        if (getTotalAcrescimoProdutosTipo().equals("%")) {
            total += "%";
        }
        return total;
    }

    public String getTotalAcrescimoFormatadoServicos() {
        String total = Decimal.toString(getTotalAcrescimoServicos());

        if (getTotalAcrescimoServicosTipo().equals("%")) {
            total += "%";
        }
        return total;
    }

    /**
     *
     * @return desconto monetário ou percentual com símbolo de porcentagem
     */
    public String getTotalDescontoFormatado() {
        if (getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoMonetarioProdutos());
        } else {
            return Decimal.toString(getDescontoPercentualProdutos()) + "%";
        }
    }

    public String getTotalDescontoFormatadoProdutos() {
        String total = Decimal.toString(getTotalDescontoProdutos());

        if (getTotalDescontoProdutosTipo().equals("%")) {
            total += "%";
        }
        return total;
    }

    public String getTotalDescontoFormatadoServicos() {
        String total = Decimal.toString(getTotalDescontoServicos());

        if (getTotalDescontoServicosTipo().equals("%")) {
            total += "%";
        }
        return total;
    }

    /**
     *
     * @return soma dos acréscimos de produtos e serviços em monetário
     */
    public BigDecimal getAcrescimoPercentualEmMonetario() {
        return getAcrescimoPercentualEmMonetarioProdutos().add(getAcrescimoPercentualEmMonetarioServicos());
    }

    public BigDecimal getAcrescimoPercentualEmMonetarioProdutos() {
        return getTotalItensProdutos().multiply(getAcrescimoPercentualProdutos().divide(new BigDecimal(100)));
    }

    public BigDecimal getAcrescimoPercentualEmMonetarioServicos() {
        return getTotalItensServicos().multiply(getAcrescimoPercentualServicos().divide(new BigDecimal(100)));
    }

    /**
     *
     * @return soma dos descontos de produtos e serviços em monetário
     */
    public BigDecimal getDescontoPercentualEmMonetario() {
        return getDescontoPercentualEmMonetarioProdutos().add(getDescontoPercentualEmMonetarioServicos());
    }

    public BigDecimal getDescontoPercentualEmMonetarioProdutos() {
        //System.out.println("getDescontoPercentualEmMonetarioProdutos::::");
        //System.out.println("getTotalItensProdutos(): " + getTotalItensProdutos());
        //System.out.println("getDescontoPercentualProdutos(): " + getDescontoPercentualProdutos());
        return getTotalItensProdutos().multiply(getDescontoPercentualProdutos().divide(new BigDecimal(100)));
    }

    public BigDecimal getDescontoPercentualEmMonetarioServicos() {
        return getTotalItensServicos().multiply(getDescontoPercentualServicos().divide(new BigDecimal(100)));
    }

    /**
     *
     * @return soma de desconto monetário e percentual
     */
    public BigDecimal getDescontoConsolidado() {
        return getDescontoConsolidadoProdutos().add(getDescontoConsolidadoServicos());
    }

    public BigDecimal getDescontoConsolidadoProdutos() {
        return getDescontoMonetarioProdutos().add(getDescontoPercentualEmMonetarioProdutos());
    }

    public BigDecimal getDescontoConsolidadoServicos() {
        return getDescontoMonetarioServicos().add(getDescontoPercentualEmMonetarioServicos());
    }

    public void distribuirAcrescimoMonetarioProdutos(BigDecimal totalAcrescimo) {
        BigDecimal total = getTotalItensProdutos();
        BigDecimal totalReverso = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP).multiply(mf.getSubtotalItem());

            BigDecimal valorAcrescimo = (totalAcrescimo).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            totalReverso = totalReverso.add(valorAcrescimo);

            if (getMovimentosFisicosProdutos().lastIndexOf(mf) == getMovimentosFisicosProdutos().size() - 1) {
                valorAcrescimo = valorAcrescimo.add(totalAcrescimo).subtract(totalReverso);
            }
            mf.setAcrescimoPercentual(BigDecimal.ZERO);
            mf.setAcrescimoMonetario(valorAcrescimo);
        }
    }

    public void distribuirAcrescimoPercentualProdutos(BigDecimal totalAcrescimo) {
        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            mf.setAcrescimoMonetario(BigDecimal.ZERO);
            mf.setAcrescimoPercentual(totalAcrescimo);
        }
    }

    public void distribuirDescontoMonetarioProdutos(BigDecimal totalDesconto) {
        BigDecimal total = getTotalItensProdutos();
        BigDecimal totalReverso = BigDecimal.ZERO;
        System.out.println("---------------------------------------------------");
        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 2, RoundingMode.HALF_UP).multiply(mf.getSubtotalItem()).setScale(2, RoundingMode.HALF_UP);

            System.out.println("valorRateio: " + valorRateio);
            BigDecimal valorDesconto = (totalDesconto).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            System.out.println("valorDesconto: " + valorDesconto);
            totalReverso = totalReverso.add(valorDesconto);

            if (getMovimentosFisicosProdutos().lastIndexOf(mf) == getMovimentosFisicosProdutos().size() - 1) {
                valorDesconto = valorDesconto.add(totalDesconto).subtract(totalReverso);

                //2019-09-09 - Problema de gerar desconto negativo no último item
                if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
                    for (MovimentoFisico mfCorrigir : getMovimentosFisicosProdutos()) {
                        mfCorrigir.setDescontoMonetario(mfCorrigir.getDescontoMonetario().add(valorDesconto));
                        valorDesconto = BigDecimal.ZERO;
                        break;
                    }
                }

            }
            mf.setDescontoPercentual(BigDecimal.ZERO);
            mf.setDescontoMonetario(valorDesconto);
        }
    }

    public void distribuirDescontoPercentualProdutos(BigDecimal totalDesconto) {
        for (MovimentoFisico mf : getMovimentosFisicosProdutos()) {
            mf.setDescontoMonetario(BigDecimal.ZERO);
            mf.setDescontoPercentual(totalDesconto);
        }
    }

    public void distribuirAcrescimoMonetarioServicos(BigDecimal totalAcrescimo) {
        BigDecimal total = getTotalItensServicos();
        BigDecimal totalReverso = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
            BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 10, RoundingMode.HALF_UP).multiply(mf.getSubtotalItem());

            BigDecimal valorAcrescimo = (totalAcrescimo).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            totalReverso = totalReverso.add(valorAcrescimo);

            if (getMovimentosFisicosServicos().lastIndexOf(mf) == getMovimentosFisicosServicos().size() - 1) {
                valorAcrescimo = valorAcrescimo.add(totalAcrescimo).subtract(totalReverso);
            }
            mf.setAcrescimoPercentual(BigDecimal.ZERO);
            mf.setAcrescimoMonetario(valorAcrescimo);
        }
    }

    public void distribuirAcrescimoPercentualServicos(BigDecimal totalAcrescimo) {
        for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
            mf.setAcrescimoMonetario(BigDecimal.ZERO);
            mf.setAcrescimoPercentual(totalAcrescimo);
        }
    }

    public void distribuirDescontoMonetarioServicos(BigDecimal totalDesconto) {
        BigDecimal total = getTotalItensServicos();
        BigDecimal totalReverso = BigDecimal.ZERO;

        for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
            BigDecimal valorRateio = (new BigDecimal(100)).divide(total, 10, RoundingMode.HALF_UP).multiply(mf.getSubtotalItem());

            BigDecimal valorDesconto = (totalDesconto).multiply(valorRateio).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            totalReverso = totalReverso.add(valorDesconto);

            if (getMovimentosFisicosServicos().lastIndexOf(mf) == getMovimentosFisicosServicos().size() - 1) {
                valorDesconto = valorDesconto.add(totalDesconto).subtract(totalReverso);
            }
            mf.setDescontoPercentual(BigDecimal.ZERO);
            mf.setDescontoMonetario(valorDesconto);
        }
    }

    public void distribuirDescontoPercentualServicos(BigDecimal totalDesconto) {
        for (MovimentoFisico mf : getMovimentosFisicosServicos()) {
            mf.setDescontoMonetario(BigDecimal.ZERO);
            mf.setDescontoPercentual(totalDesconto);
        }
    }

    //Totais Nfe----------------------------------------------------------------
    public BigDecimal getTotalBcIcms() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorBcIcms).reduce(BigDecimal::add).get();
    }

    public BigDecimal getTotalIcms() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorIcms).reduce(BigDecimal::add).get();
    }

    public BigDecimal getTotalIcmsDesonerado() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorIcmsDesonerado).reduce(BigDecimal::add).get();
    }

    //VFCP
    //VFCPST
    //VFCPSTRet
    public BigDecimal getTotalBcIcmsSt() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorBcIcmsSt).reduce(BigDecimal::add).get();
    }

    public BigDecimal getTotalIcmsSt() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorIcmsSt).reduce(BigDecimal::add).get();
    }

    //VII;
    //VIPI;
    //VIPIDevol;
    public BigDecimal getTotalPis() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorPis).reduce(BigDecimal::add).get();
    }

    public BigDecimal getTotalCofins() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getValorCofins).reduce(BigDecimal::add).get();
    }

    public BigDecimal getTotalOutrosProdutos() {
        if (getMovimentosFisicosProdutos().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return getMovimentosFisicosProdutos().stream().map(MovimentoFisico::getAcrescimo).reduce(BigDecimal::add).get();
    }

    //Fim Totais Nfe------------------------------------------------------------
    /**
     *
     * @return CPF ou CNPJ ou String vazia
     */
    public String getEntregaCpfOuCnpj() {
        if (getEntregaCpf().length() > 0) {
            return getEntregaCpf();
        } else if (getEntregaCnpj().length() > 0) {
            return getEntregaCnpj();
        } else {
            return "";
        }
    }

    public void setEntregaCpfOuCnpj(String cpfCnpj) {
        cpfCnpj = cpfCnpj.trim();
        if (cpfCnpj.length() > 14) {
            setEntregaCnpj(cpfCnpj);
            setEntregaCpf(null);
        } else {
            setEntregaCnpj(null);
            setEntregaCpf(cpfCnpj);
        }
    }

    public Cidade getEntregaMunicipio() {
        if (getEntregaCodigoMunicipio().isEmpty()) {
            return null;
        }

        CidadeDAO cidadeDAO = new CidadeDAO();
        Cidade cidade = cidadeDAO.findByCodigoIbge(getEntregaCodigoMunicipio());

        return cidade != null ? cidade : null;
    }

    public String getDescricaoConcatenada() {
        List<String> valores = new ArrayList<>();
        getMovimentosFisicos().forEach((mf) -> {
            valores.add(mf.getDescricao());
        });

        return String.join("\r\n", valores);
    }

}
