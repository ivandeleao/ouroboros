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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.SatCupom;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoStatus;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.em;
import util.Decimal;

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
    private BigDecimal acrescimoMonetarioProdutos;
    private BigDecimal acrescimoPercentualProdutos;
    private BigDecimal descontoMonetarioProdutos;
    private BigDecimal descontoPercentualProdutos;
    
    private BigDecimal acrescimoMonetarioServicos;
    private BigDecimal acrescimoPercentualServicos;
    private BigDecimal descontoMonetarioServicos;
    private BigDecimal descontoPercentualServicos;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL) //, orphanRemoval = true) //, cascade = CascadeType.REFRESH) //, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy
    private List<MovimentoFisico> movimentosFisicos = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)//, cascade = CascadeType.REFRESH) //, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy
    private List<Parcela> parcelas = new ArrayList<>();

    @Column(length = 1000)
    private String relato; //Descrição do que deve ser feito no serviço

    @Column(length = 1000)
    private String observacao;

    //fiscal
    private String destCpfCnpj;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @OrderBy
    private List<SatCupom> satCupons = new ArrayList<>();

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
    
    //--------------------------------------------------------------------------
    public boolean hasCupomSat() {
        return !getSatCupons().isEmpty();
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

    /**
     *
     * @return movimentosFisicos de saída não estornados
     */
    public List<MovimentoFisico> getMovimentosFisicosSaida() {
        List<MovimentoFisico> itensAtivos = new ArrayList<>();
        for (MovimentoFisico item : movimentosFisicos) {
            if (item.getEstorno() == null && item.getEstornoOrigem() == null && item.getSaida().compareTo(BigDecimal.ZERO) > 0) {
                itensAtivos.add(item);
            }
        }
        return itensAtivos;
    }
    
    public List<MovimentoFisico> getMovimentosFisicosSaidaProdutos() {
        List<MovimentoFisico> itensProdutos = new ArrayList<>();
        getMovimentosFisicosSaida().stream().filter((itemProduto) -> (itemProduto.getProduto().getProdutoTipo().equals(ProdutoTipo.PRODUTO))).forEachOrdered((itemProduto) -> {
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

    public List<SatCupom> getSatCupons() {
        return satCupons;
    }

    public void setSatCupons(List<SatCupom> satCupons) {
        this.satCupons = satCupons;
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

    public VendaStatus getStatus() {
        MovimentoFisicoStatus tempMfStatus = MovimentoFisicoStatus.AGUARDANDO;

        if (getCancelamento() != null) {
            return VendaStatus.CANCELADO;
        }

        if (isOrcamento()) {
            return VendaStatus.ORÇAMENTO;
        }

        /*if (getVendaTipo().equals(VendaTipo.VENDA)) {
            return VendaStatus.ENTREGA_CONCLUÍDA;
        }*/

        //Compara todos os status...
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
        for (Map.Entry<MovimentoFisicoStatus, VendaStatus> entryStatus : mapStatus.entrySet()) {
            //System.out.println("for map: " + entryStatus.getKey());

            for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                //System.out.println("\t for mf.getId: " + mf.getId());

                //Se já foi entregue, verificar status da devolução
                if (mf.getStatus() == MovimentoFisicoStatus.ENTREGA_CONCLUÍDA && mf.getDevolucao() != null) {
                    //se for igual memorizar para o caso de não estarem todos no mesmo status
                    if (mf.getDevolucao().getStatus() == entryStatus.getKey()) {
                        //System.out.println("\t\t mf.getId: " + mf.getId() + " mf.getDevolucao().getStatus: " + mf.getDevolucao().getStatus());
                        tempMfStatus = entryStatus.getKey();
                    } else if (getMovimentosFisicosSaida().indexOf(mf) == getMovimentosFisicosSaida().size() - 1) {
                        return entryStatus.getValue();
                    }
                } else {
                    //System.out.println("\t\t else: mf.getStatus: " + mf.getStatus());
                    //se for igual memorizar para o caso de não estarem todos no mesmo status
                    if (mf.getStatus() == entryStatus.getKey()) {
                        //System.out.println("\t\t mf.getId: " + mf.getId() + " mf.getStatus: " + mf.getStatus());
                        tempMfStatus = entryStatus.getKey();
                    } else if (getMovimentosFisicosSaida().indexOf(mf) == getMovimentosFisicosSaida().size() - 1) {
                        return mapStatus.get(mf.getStatus()); //entryStatus.getValue();
                    }
                }
            }
        }

        return mapStatus.get(tempMfStatus);

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

    public List<MovimentoFisico> getListMovimentoFisicoEager() {
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

    public BigDecimal getTotalItensProdutos() {
        BigDecimal total = BigDecimal.ZERO;

        if (getTipoOperacao().equals(TipoOperacao.SAIDA)) {
            for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                if (mf.getProduto().getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                    total = total.add(mf.getSubtotal());
                }
            }

        } else {
            for (MovimentoFisico mf : getMovimentosFisicosEntrada()) {
                if (mf.getProduto().getProdutoTipo().equals(ProdutoTipo.PRODUTO)) {
                    total = total.add(mf.getSubtotal());
                }
            }

        }

        return total;
    }

    public BigDecimal getTotalItensServicos() {
        BigDecimal total = BigDecimal.ZERO;

        if (getTipoOperacao().equals(TipoOperacao.SAIDA)) {
            for (MovimentoFisico mf : getMovimentosFisicosSaida()) {
                if (mf.getProduto().getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                    total = total.add(mf.getSubtotal());
                }
            }
        } else {
            for (MovimentoFisico mf : getMovimentosFisicosEntrada()) {
                if (mf.getProduto().getProdutoTipo().equals(ProdutoTipo.SERVICO)) {
                    total = total.add(mf.getSubtotal());
                }
            }
        }

        return total;
    }

    /**
     *
     * @return soma dos movimentos físicos mais acréscimos e menos descontos
     */
    public BigDecimal getTotal() {
        return getTotalProdutos().add(getTotalServicos());
    }
    
    public BigDecimal getTotalProdutos() {
        return getTotalItensProdutos().add(getAcrescimoMonetarioProdutos()).add(getAcrescimoPercentualEmMonetarioProdutos()).subtract(getDescontoMonetarioProdutos()).subtract(getDescontoPercentualEmMonetarioProdutos()).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTotalServicos() {
        return getTotalItensServicos().add(getAcrescimoMonetarioServicos()).add(getAcrescimoPercentualEmMonetarioServicos()).subtract(getDescontoMonetarioServicos()).subtract(getDescontoPercentualEmMonetarioServicos()).setScale(2, RoundingMode.HALF_UP);
    }

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
     * @return proporção do valor de produtos no documento em relação aos serviços representado por um valor entre 0 e 1
     */
    public BigDecimal getRateioProduto() {
        return getTotalItens().equals(BigDecimal.ZERO) ? BigDecimal.ZERO : getTotalItensProdutos().divide(getTotalItens(), 10, RoundingMode.HALF_UP);
    }
    
    /**
     * 
     * @return proporção do valor de serviços no documento em relação aos produtos representado por um valor entre 0 e 1
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
        return getTotalProdutos().subtract(getTotalRecebidoAVistaProdutos());
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
     * @return soma de acréscimo monetário e percentual
     */
    public BigDecimal getAcrescimoConsolidado() {
        return getAcrescimoMonetarioProdutos().add(getAcrescimoConsolidadoServicos());
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
    public String getAcrescimoAplicado() {
        if (getAcrescimoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getAcrescimoMonetarioProdutos());
        } else {
            return Decimal.toString(getAcrescimoPercentualProdutos()) + "%";
        }
    }
    
    public String getAcrescimoAplicadoProdutos() {
        if (getAcrescimoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getAcrescimoMonetarioProdutos());
        } else {
            return Decimal.toString(getAcrescimoPercentualProdutos()) + "%";
        }
    }
    
    public String getAcrescimoAplicadoServicos() {
        if (getAcrescimoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getAcrescimoMonetarioServicos());
        } else {
            return Decimal.toString(getAcrescimoPercentualServicos()) + "%";
        }
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
        return getDescontoMonetarioProdutos().add(getDescontoConsolidadoServicos());
    }
    
    public BigDecimal getDescontoConsolidadoProdutos() {
        return getDescontoMonetarioProdutos().add(getDescontoPercentualEmMonetarioProdutos());
    }
    
    public BigDecimal getDescontoConsolidadoServicos() {
        return getDescontoMonetarioServicos().add(getDescontoPercentualEmMonetarioServicos());
    }

    /**
     *
     * @return desconto monetário ou percentual com símbolo de porcentagem
     */
    public String getDescontoAplicado() {
        if (getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoMonetarioProdutos());
        } else {
            return Decimal.toString(getDescontoPercentualProdutos()) + "%";
        }
    }
    
    public String getDescontoAplicadoProdutos() {
        if (getDescontoMonetarioProdutos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoMonetarioProdutos());
        } else {
            return Decimal.toString(getDescontoPercentualProdutos()) + "%";
        }
    }
    
    public String getDescontoAplicadoServicos() {
        if (getDescontoMonetarioServicos().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoMonetarioServicos());
        } else {
            return Decimal.toString(getDescontoPercentualServicos()) + "%";
        }
    }
}
