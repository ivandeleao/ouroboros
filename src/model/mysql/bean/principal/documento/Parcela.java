/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import model.nosql.FinanceiroStatusEnum;
import model.mysql.bean.principal.pessoa.Pessoa;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import model.mysql.bean.principal.financeiro.Cheque;
import model.nosql.TipoCalculoEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
public class Parcela implements Serializable, Comparable<Parcela> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "vendaId")
    private Venda venda;

    @OrderBy
    private Integer numero;

    //estou mantendo vencimento nulo para parcelas de recebimento a vista
    //só coloco data quando é a prazo
    private LocalDate vencimento;

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal valor;

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal multa; //valor percentual cobrado uma única vez quando vencido

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal jurosMonetario; //valor mensal - calculado como diário de mês comercial: 30 dias

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal jurosPercentual; //valor mensal - calculado como diário de mês comercial: 30 dias

    //não usados - rever
    //private BigDecimal acrescimo;
    //private BigDecimal desconto;
    //private BigDecimal descontoPercentual;
    //----------
    private BigDecimal acrescimoMonetario;
    private BigDecimal acrescimoPercentual;
    private BigDecimal descontoMonetario;
    private BigDecimal descontoPercentual;
    
    @ManyToOne
    @JoinColumn(name = "cartaoTaxaId")
    private CartaoTaxa cartaoTaxa;
    private BigDecimal cartaoTaxaValor;
    
    @Column(columnDefinition = "boolean default false")
    private boolean cartaoTaxaInclusa; //se a taxa entra no total da venda (cobrar do cliente)

    @ManyToOne
    @JoinColumn(name = "meioDePagamentoId")
    private MeioDePagamento meioDePagamento;

    @OneToMany(mappedBy = "parcela")//, cascade = CascadeType.ALL) 2019-11-13 lento ao salvar a venda
    private List<CaixaItem> recebimentos = new ArrayList<>();
    
    //Boleto
    private String boletoSequencial;
    private String boletoAno;
    private String boletoByte;
    private String boletoDv;
    private String boletoNossoNumero;
    private String boletoCodigoBarras;
    private LocalDateTime boletoImpressao;
    private LocalDateTime boletoRemessa;
    
    @ManyToOne
    @JoinColumn(name = "chequeId", nullable = true)
    private Cheque cheque;

    public Parcela() {
    }

    public Parcela(LocalDate vencimento, BigDecimal valor, BigDecimal multa, BigDecimal jurosMonetario, BigDecimal jurosPercentual, MeioDePagamento meioDePagamento) {
        this.vencimento = vencimento;
        this.valor = valor;

        this.multa = multa;
        this.jurosMonetario = jurosMonetario;
        this.jurosPercentual = jurosPercentual;

        this.meioDePagamento = meioDePagamento;
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

    public LocalDateTime getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(LocalDateTime atualizacao) {
        this.atualizacao = atualizacao;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorAtual() {
        BigDecimal valor = getValor()
                .add(getMultaCalculada())
                .add(getJurosCalculado())
//                .setScale(2, RoundingMode.HALF_UP)
                .subtract(getValorQuitado())
                .add(getAcrescimoMonetario())
                .add(getAcrescimoPercentualEmMonetario())
                .subtract(getDescontoMonetario())
                .subtract(getDescontoPercentualEmMonetario())
                .subtract(getCartaoTaxaValor()); //2020-02-26 para exibir corretamente o status de parcela de cartão
        
        return valor.setScale(2, RoundingMode.HALF_UP); //2019-10-07 arredondar para comparar corretamente com o valor da parcela
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMultaCalculada() {
        BigDecimal multaCalculada = BigDecimal.ZERO;
        if (getDiasEmAtraso().compareTo(0l) > 0) {
            multaCalculada = getValor().multiply(getMulta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        return multaCalculada.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    //Facilitadores-------------------------------------------------------------
    
    public boolean isAVista() {
        return getVencimento() == null;
    }
    
    public BigDecimal getAcrescimoPercentualEmMonetario() {
        return getValor().multiply(getAcrescimoPercentual()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getDescontoPercentualEmMonetario() {
        return getValor().multiply(getDescontoPercentual()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     *
     * @return representação do juros em monetário ou percentual
     */
    public String getJurosFormatado() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getJurosMonetario());
        } else {
            return Decimal.toString(getJurosPercentual()) + "%";
        }
    }

    /**
     *
     * @return o valor do juros utilizado: monetário ou percentual
     */
    public BigDecimal getJuros() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return getJurosMonetario();
        } else {
            return getJurosPercentual();
        }
    }
    
    public TipoCalculoEnum getJurosTipo() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return TipoCalculoEnum.VALOR;
        } else {
            return TipoCalculoEnum.PERCENTUAL;
        }
        
    }
    
    public BigDecimal getJurosPercentualEmMonetario() {
        return getValor().multiply(getJurosPercentual()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 
     * @return juros monetário ou juros percentual em monetário
     */
    public BigDecimal getJurosEmMonetario() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return getJurosMonetario();
        } else {
            return getJurosPercentualEmMonetario();
        }
    }
    

    public Long getDiasEmAtraso() {
        Long dias = 0l;
        LocalDate hoje;
        /*Deveria ser getValorAtual ao invés de getValor, mas entra em recursividade infinita! :<
        pois getValorAtual usa este método getDiasEmAtraso para calcular
         */
        /*System.out.println("getValorQuitado(): " + getValorQuitado());
        System.out.println("getValor(): " + getValor());
        System.out.println("getRecebimentos().isEmpty(): " + getRecebimentos().isEmpty());
        
        System.out.println("---------------");
        */
        //2020-04-07
        BigDecimal valorQuaseAtual = (getValor() //to do - criar um forma de armazenar o status quando quitado e não processar mais o status
                //.add(getMultaCalculada()) usa o getDiasEmAtraso() - recursivo
                //.add(getJurosCalculado()) usa o getDiasEmAtraso() - recursivo
                //.subtract(getValorQuitado())
                .add(getAcrescimoMonetario())
                .add(getAcrescimoPercentualEmMonetario())
                .subtract(getDescontoMonetario())
                .subtract(getDescontoPercentualEmMonetario())
                .subtract(getCartaoTaxaValor())).setScale(2, RoundingMode.HALF_UP);
        
        
        
        if (getValorQuitado().compareTo(valorQuaseAtual) >= 0 && !getRecebimentos().isEmpty()) { //se quitado
            //usar a data em que foi pago como limite de dias em atraso
            //System.out.println("getRecebimentos(): " + getRecebimentos());
            //System.out.println("getRecebimentos().get(0): " + getRecebimentos().get(0));
            //System.out.println("getRecebimentos().get(0).getDataHora(): " + getRecebimentos().get(0).getDataHora());

            /*
            for (CaixaItem r : getRecebimentos()) {
                System.out.println("recebimento: " + r.getId());
            }*/

            hoje = getRecebimentos().get(0).getCriacao().toLocalDate();
        } else {
            hoje = LocalDate.now();
        }

        if (getVencimento() != null && hoje.compareTo(getVencimento()) > 0) {
            dias = Math.abs(DateTime.diasEntreDatas(hoje, getVencimento()));
        }
        return dias;
    }

    public BigDecimal getJurosCalculado() {
        BigDecimal jurosAcumulado = BigDecimal.ZERO;
        BigDecimal jurosDiarioMonetario = BigDecimal.ZERO;

        if (getDiasEmAtraso().compareTo(0l) > 0) {
            if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) { //monetário
                jurosDiarioMonetario = getJurosMonetario();
            } else { //percentual
                BigDecimal jurosMensal = getValor().multiply(getJurosPercentual()).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
                //System.out.println("--jurosMensal: " + jurosMensal);
                jurosDiarioMonetario = jurosMensal.divide(new BigDecimal(30), 10, RoundingMode.HALF_UP); //mês comercial = 30 dias
                //System.out.println("--jurosDiarioMonetario: " + jurosDiarioMonetario);
            }
            jurosAcumulado = jurosDiarioMonetario.multiply(new BigDecimal(getDiasEmAtraso()));
        }

        return jurosAcumulado.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getJurosMonetario() {
        return jurosMonetario;
    }

    public void setJurosMonetario(BigDecimal jurosMonetario) {
        this.jurosMonetario = jurosMonetario;
    }

    public BigDecimal getJurosPercentual() {
        return jurosPercentual;
    }

    public void setJurosPercentual(BigDecimal jurosPercentual) {
        this.jurosPercentual = jurosPercentual;
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

    public CartaoTaxa getCartaoTaxa() {
        return cartaoTaxa;
    }

    public void setCartaoTaxa(CartaoTaxa cartaoTaxa) {
        this.cartaoTaxa = cartaoTaxa;
    }

    public BigDecimal getCartaoTaxaValor() {
        return cartaoTaxaValor != null ? cartaoTaxaValor : BigDecimal.ZERO;
    }

    public void setCartaoTaxaValor(BigDecimal cartaoTaxaValor) {
        this.cartaoTaxaValor = cartaoTaxaValor;
    }

  

    public boolean isCartaoTaxaInclusa() {
        return cartaoTaxaInclusa;
    }

    public void setCartaoTaxaInclusa(boolean cartaoTaxaInclusa) {
        this.cartaoTaxaInclusa = cartaoTaxaInclusa;
    }

    
    
    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public List<CaixaItem> getRecebimentos() {
        recebimentos.sort(Comparator.comparing(CaixaItem::getId));
        return recebimentos;
    }

    public void setRecebimentos(List<CaixaItem> recebimentos) {
        this.recebimentos = recebimentos;
    }

    public String getBoletoSequencial() {
        return boletoSequencial;
    }

    public void setBoletoSequencial(String boletoSequencial) {
        this.boletoSequencial = boletoSequencial;
    }

    public String getBoletoAno() {
        return boletoAno;
    }

    public void setBoletoAno(String boletoAno) {
        this.boletoAno = boletoAno;
    }

    public String getBoletoByte() {
        return boletoByte;
    }

    public void setBoletoByte(String boletoByte) {
        this.boletoByte = boletoByte;
    }

    public String getBoletoDv() {
        return boletoDv;
    }

    public void setBoletoDv(String boletoDv) {
        this.boletoDv = boletoDv;
    }

    public String getBoletoNossoNumero() {
        return boletoNossoNumero;
    }

    public void setBoletoNossoNumero(String boletoNossoNumero) {
        this.boletoNossoNumero = boletoNossoNumero;
    }

    public String getBoletoCodigoBarras() {
        return boletoCodigoBarras != null ? boletoCodigoBarras : "";
    }

    public void setBoletoCodigoBarras(String boletoCodigoBarras) {
        this.boletoCodigoBarras = boletoCodigoBarras;
    }

    public LocalDateTime getBoletoImpressao() {
        return boletoImpressao;
    }

    public void setBoletoImpressao(LocalDateTime boletoImpressao) {
        this.boletoImpressao = boletoImpressao;
    }

    public LocalDateTime getBoletoRemessa() {
        return boletoRemessa;
    }

    public void setBoletoRemessa(LocalDateTime boletoRemessa) {
        this.boletoRemessa = boletoRemessa;
    }

    public Cheque getCheque() {
        return cheque;
    }

    public void setCheque(Cheque cheque) {
        this.cheque = cheque;
    }

    //Métodos facilitadores ----------------------------------------------------
    
    public BigDecimal getJurosMonetarioDiario() {
        return getJurosMonetario().divide(new BigDecimal(30), 10, RoundingMode.HALF_UP);
    }
    
    public String getAcrescimoOuDescontoFormatado() {
        if(getAcrescimoSemTipo().compareTo(getDescontoSemTipo()) > 0) {
            return "+ " + getAcrescimoFormatado();
        } else {
            return "- " + getDescontoFormatado();
        }
    }
    
    public BigDecimal getAcrescimoSemTipo() {
        if(getAcrescimoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getAcrescimoPercentual();
        } else {
            return getAcrescimoMonetario();
        }
    }
    
    public BigDecimal getDescontoSemTipo() {
        if(getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getDescontoPercentual();
        } else {
            return getDescontoMonetario();
        }
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
    
    public String getAcrescimoTipo() {
        return getAcrescimoMonetario().compareTo(BigDecimal.ZERO) > 0 ? "$" : "%";
    }
    
    public String getDescontoTipo() {
        return getDescontoMonetario().compareTo(BigDecimal.ZERO) > 0 ? "$" : "%";
    }
    
    
    public String getDescricao() {
        String descricao = getVenda().getVendaTipo()
                + " " + getVenda().getId()
                + " - Parcela " + getNumeroDeTotal()
                + " - " + getVenda().getPessoa().getNome();
        
        
        return descricao;
    }
    
    
    public void addRecebimento(CaixaItem caixaItem) {
        recebimentos.remove(caixaItem);
        recebimentos.add(caixaItem);
        caixaItem.setParcela(this);
    }

    public void removeRecebimento(CaixaItem caixaItem) {
        caixaItem.setParcela(null);
        recebimentos.remove(caixaItem);
    }

    /**
     * 
     * @return Número da Parcela e Quantidade: 9/99
     */
    public String getNumeroDeTotal() {
        if (getNumero() == null) {
            return "À VISTA";
        } else {
            return String.format("%02d", getNumero()) + "/" + String.format("%02d", getVenda().getParcelas().size());
        }
    }
    
    /**
     * 
     * @return numero formatado com 2 dígitos
     */
    public String getNumeroFormatado() {
        if (getNumero() == null) {
            return "À VISTA";
        } else {
            return String.format("%02d", getNumero());
        }
    }

    /**
     *
     * @return soma dos valores pagos/recebidos desta parcela
     */
    public BigDecimal getValorQuitado() {
        BigDecimal valorQuitado = BigDecimal.ZERO;
        /*for (CaixaItem recebimento : recebimentos) {
            valorQuitado = valorQuitado.add(recebimento.getSaldoLinear());
        }*/
        if (!getRecebimentos().isEmpty()) {
            valorQuitado = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get().abs();
        }
        return valorQuitado;
    }

    /**
     *
     * @return se está quitado
     */
    public FinanceiroStatusEnum getStatus() {

        //if (getValorQuitado().compareTo(getValorAtual()) < 0 && getDiasEmAtraso() > 0) {
        
        //System.out.println("parcela id: " + getId());
        //System.out.println("getValorAtual(): " + getValorAtual());
        //System.out.println("getDiasEmAtraso(): " + getDiasEmAtraso());
        
        if (getValorAtual().compareTo(BigDecimal.ZERO) > 0 && getDiasEmAtraso() > 0) {
            return FinanceiroStatusEnum.VENCIDO;

        } else if ((getValorAtual().subtract(getCartaoTaxaValor())).compareTo(BigDecimal.ZERO) > 0) {
            return FinanceiroStatusEnum.ABERTO;
            
        } else if (getValorQuitado().compareTo(getValorAtual()) >= 0 // O valor atual pode ficar menor que o valorQuitado, quando tem recebimento parcial
                && getValorQuitado().compareTo(getValor()) >= 0) {
            return FinanceiroStatusEnum.QUITADO;
            
        } else {
            return FinanceiroStatusEnum.QUITADO;
        }

        //return null;
    }

    public LocalDateTime getUltimoRecebimento() {
        if (getStatus() == FinanceiroStatusEnum.QUITADO) {
            return getRecebimentos().get(getRecebimentos().size() - 1).getDataHoraRecebimento();
        }
        return null;
    }

    public BigDecimal getTroco() {
        BigDecimal troco = BigDecimal.ZERO;
        for (CaixaItem recebimento : recebimentos) {
            if (recebimento.getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
                troco = troco.add(recebimento.getDebito());
            }
        }
        return troco;
    }

    public Pessoa getCliente() {
        return getVenda().getPessoa();
    }
    
    public BigDecimal getCartaoValorLiquido() {
        return getValor().subtract(getCartaoTaxaValor());
    }
    
    public BigDecimal getCartaoValorRecebido() {
        return getValorQuitado().compareTo(BigDecimal.ZERO) > 0 ? getValorQuitado().add(getCartaoTaxaValor()) : BigDecimal.ZERO;
    }
    
    

    @Override
    public int compareTo(Parcela o) {
        return id.compareTo(o.getId());
    }
    

}
