/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import model.bean.fiscal.MeioDePagamento;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "criacao")
    , @Index(columnList = "encerramento")
    , @Index(columnList = "comanda")
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
    @JoinColumn(name = "clienteId")
    private Pessoa cliente;
    
    private LocalDateTime cancelamento; //desliga financeiro e estoque relacionados
    
    @Column(columnDefinition = "boolean default false")
    private Boolean orcamento; //desliga financeiro e estoque relacionados
    
    private Timestamp encerramento;
    private Integer comanda;
    private BigDecimal acrescimoMonetario;
    private BigDecimal acrescimoPercentual;
    private BigDecimal descontoMonetario;
    private BigDecimal descontoPercentual;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL) //, orphanRemoval = true) //, cascade = CascadeType.REFRESH) //, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy
    private List<MovimentoFisico> movimentosFisicos = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)//, cascade = CascadeType.REFRESH) //, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy
    private List<Parcela> parcelas = new ArrayList<>();

    //fiscal
    private String destCpfCnpj;
    
    @Column(length = 1000)
    private String observacao;
    
    
    
    
    
    

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

    public Pessoa getCliente() {
        return cliente;
    }

    public void setCliente(Pessoa cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(LocalDateTime cancelamento) {
        this.cancelamento = cancelamento;
    }

    public Boolean getOrcamento() {
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
    
    /**
     * 
     * @return número da comanda
     */
    public Integer getComanda() {
        return comanda;
    }

    public void setComanda(Integer comanda) {
        this.comanda = comanda;
    }

    /**
     *
     * @return soma de acréscimo monetário e percentual
     */
    public BigDecimal getAcrescimoConsolidado() {
        return getAcrescimoMonetario().add(getAcrescimoPercentualEmMonetario());
    }

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

    public BigDecimal getAcrescimoPercentualEmMonetario() {
        return getTotalItens().multiply(getAcrescimoPercentual().divide(new BigDecimal(100)));
    }

    /**
     *
     * @return soma de desconto monetário e percentual
     */
    public BigDecimal getDescontoConsolidado() {
        return getDescontoMonetario().add(getDescontoPercentualEmMonetario());
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

    public BigDecimal getDescontoPercentualEmMonetario() {
        return getTotalItens().multiply(getDescontoPercentual().divide(new BigDecimal(100)));
    }

    public List<MovimentoFisico> getMovimentosFisicos() {
        //retorna apenas movimentosFisicos não excluídos
        List<MovimentoFisico> itensAtivos = new ArrayList<>();
        for (MovimentoFisico item : movimentosFisicos) {
            //if (!item.isExcluido() && item.getSaida().compareTo(BigDecimal.ZERO) > 0) {
            if (item.getEstorno() == null && item.getEstornoOrigem() == null && item.getSaida().compareTo(BigDecimal.ZERO) > 0) {
                itensAtivos.add(item);
            }
        }
        return itensAtivos;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    
    
    
    //--------------------------------------------------------------------------
    
    public VendaStatus getStatus() {
        if(getCancelamento() != null) {
            return VendaStatus.CANCELADO;
        }
        
        if(getOrcamento()) {
            return VendaStatus.ORÇAMENTO;
        }
        
        
        for(MovimentoFisico mf : getMovimentosFisicos()) {
            if(mf.getDataEntrada() == null) {
                break;
            } else if (getMovimentosFisicos().indexOf(mf) == getMovimentosFisicos().size() -1) {
                return VendaStatus.RECEBIDO;
            }
        }
        
        for(MovimentoFisico mf : getMovimentosFisicos()) {
            if(mf.getDataSaida() == null) {
                break;
            } else if (getMovimentosFisicos().indexOf(mf) == getMovimentosFisicos().size() -1) {
                return VendaStatus.ENTREGUE;
            }
        }
        
        for(MovimentoFisico mf : getMovimentosFisicos()) {
            if(mf.getDataPronto() == null) {
                break;
            } else if (getMovimentosFisicos().indexOf(mf) == getMovimentosFisicos().size() -1) {
                return VendaStatus.PRONTO;
            }
        }
        
        return VendaStatus.ANDAMENTO;
        
    }
    
    
    public MovimentoFisico findMovimentoFisico(MovimentoFisico movimentoFisico) {
        System.out.println("Encontrar - movimentoFisico: " + movimentoFisico.getId());
        for(MovimentoFisico mf : this.movimentosFisicos) {
            if(mf.getId().equals(movimentoFisico.getId())) {
                System.out.println("findMovimentoFisico id: " + mf.getId());
                return mf;
            }
        }
        return null;
    }
    
    
    public void addMovimentoFisico(MovimentoFisico movimentoFisico) {
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
        for(MovimentoFisico mf : movimentosFisicos) {
            if(mf.getDataSaidaPrevista() != null) {
                listMf.add(mf);
            }
        }
        if(!listMf.isEmpty()) {
            listMf.sort(Comparator.comparing(MovimentoFisico::getDataSaidaPrevista));
            
            primeiraData = listMf.get(0).getDataSaidaPrevista();
        }
        
        
        return primeiraData;
    }
    
    
    
    
    
    
    

    public BigDecimal getTotalItens() {
        BigDecimal totalItens = new BigDecimal(0);
        for (MovimentoFisico item : getMovimentosFisicos()) {
            totalItens = totalItens.add(item.getSubtotal());
        }
        return totalItens;
    }

    public BigDecimal getTotal() {
        //return getTotalItens().add(getAcrescimoTotal()).subtract(getDescontoTotal());
        return getTotalItens().add(getAcrescimoMonetario()).add(getAcrescimoPercentualEmMonetario()).subtract(getDescontoMonetario()).subtract(getDescontoPercentualEmMonetario());
    }

    public BigDecimal getTotalRecebido() {
        BigDecimal totalRecebido = BigDecimal.ZERO;
        for (Parcela parcela : parcelas) {
            if (parcela.getVencimento() == null) {
                totalRecebido = totalRecebido.add(parcela.getRecebido());
            }
        }
        /*
        if(!parcelas.isEmpty()) {
            totalRecebido = parcelas.stream().map(Parcela::getRecebido).reduce(BigDecimal::add).get();
        }*/
        return totalRecebido;
    }
    
    public BigDecimal getTotalRecebidoAPrazo() {
        BigDecimal totalRecebidoAPrazo = BigDecimal.ZERO;
            if(!getParcelasAPrazo().isEmpty()) {
                totalRecebidoAPrazo = getParcelasAPrazo().stream().map(Parcela::getRecebido).reduce(BigDecimal::add).get();
            }
        return totalRecebidoAPrazo;
    }

    public BigDecimal getTotalReceber() {
        return getTotal().subtract(getTotalRecebido());
    }
    
    /**
     * 
     * @return Soma das parcelas a prazo
     */
    public BigDecimal getTotalAPrazo() {
        BigDecimal total = BigDecimal.ZERO;
        if(!getParcelasAPrazo().isEmpty()) {
            total = getParcelasAPrazo().stream().map(Parcela::getValor).reduce(BigDecimal::add).get();
        }
        return total;
    }
    
    /**
     * 
     * @return Valor não recebido e sem parcelamento definido
     */
    public BigDecimal getTotalEmAberto() {
        //return getTotal().subtract(getTotalRecebido()).subtract(getTotalAPrazo());
        return getTotal().subtract(getTotalRecebido()).subtract(getTotalAPrazo());
    }

    /*
    public BigDecimal getAcrescimoTotal(){
        BigDecimal acrescimoTotal = BigDecimal.ZERO;
        for(Parcela parcela : parcelas){
            acrescimoTotal = acrescimoTotal.add(parcela.getAcrescimo());
        }
        return acrescimoTotal;
    }
    
    public BigDecimal getDescontoTotal(){
        BigDecimal descontoTotal = BigDecimal.ZERO;
        for(Parcela parcela : parcelas){
            descontoTotal = descontoTotal.add(parcela.getDesconto());
            BigDecimal descontoPercentual = parcela.getDescontoPercentual();
            BigDecimal descontoPercentualConvertido = descontoPercentual.divide(new BigDecimal(100)).multiply(parcela.getValor());
            descontoTotal = descontoTotal.add(parcela.getDescontoPercentual());
        }
        return descontoTotal;
    }*/
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

    
}
