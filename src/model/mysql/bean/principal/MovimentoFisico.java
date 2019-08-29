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
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.catalogo.Tamanho;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;
import util.Decimal;

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
    @OneToMany(mappedBy = "montagemOrigem", cascade = CascadeType.ALL)
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

    @ManyToOne
    @JoinColumn(name = "unidadeComercialVendaId") //2019-08-28 , nullable = true)
    private UnidadeComercial unidadeComercialVenda;

    @Column(columnDefinition = "boolean default false")
    private Boolean excluido;

    //------------------- relacionamento circular
    @OneToOne(mappedBy = "devolucaoOrigem", cascade = CascadeType.ALL)
    private MovimentoFisico devolucao;

    @OneToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucaoOrigemId")
    private MovimentoFisico devolucaoOrigem;
    //-------------------
    
    

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

    public Boolean isExcluido() {
        return excluido != null ? excluido : false;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }
    
    /*
    public int compareTo(MovimentoFisico movimentoFisico) {
        return id.compareTo(movimentoFisico.getId());
    }*/
    
    //--------------------------------------------------------------------------
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
        return (getSubtotalItem()
                .add(getAcrescimoMonetario()).add(getAcrescimoPercentualEmMonetario()) //2019-07-27
                .subtract(getDescontoMonetario()).subtract(getDescontoPercentualEmMonetario())
                .add(getValorFrete())
                .add(getValorSeguro())
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
    public int compareTo(MovimentoFisico o) {
        return id.compareTo(o.getId());
    }
}
