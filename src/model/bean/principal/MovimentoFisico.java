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
import java.io.Serializable;
import model.bean.fiscal.UnidadeComercial;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;

/**
 * Representa item de venda, item de compra e movimento de estoque
 *
 * @author ivand
 */
@Entity
public class MovimentoFisico implements Serializable, Comparable<MovimentoFisico> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    @OneToMany(mappedBy = "movimentoFisicoOrigem", cascade = CascadeType.ALL) //, orphanRemoval = true)
    private List<MovimentoFisico> movimentosFisicosComponente = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "movimentoFisicoOrigemId")
    private MovimentoFisico movimentoFisicoOrigem;
    
    @ManyToOne
    @JoinColumn(name = "vendaId", nullable = true)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produtoId") //, nullable = true)
    private Produto produto;

    private String codigo; //se não possuir código no cadastro será usado o id

    private LocalDateTime dataAndamento;
    
    private LocalDateTime dataPronto;
    private LocalDateTime dataProntoPrevista;
    
    private LocalDateTime dataEntrada; //data efetiva para itens locados (devolução)
    private LocalDateTime dataSaida; //data efetiva para itens locados ou com entrega posterior (data entrega)

    private LocalDateTime dataEntradaPrevista;
    private LocalDateTime dataSaidaPrevista;

    private BigDecimal entrada;
    private BigDecimal saida;

    private BigDecimal valor;

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
    @JoinColumn(name = "unidadeComercialVendaId", nullable = true)
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

    public MovimentoFisico(Produto produto, String codigo, BigDecimal entrada, BigDecimal saida, BigDecimal valor, UnidadeComercial unidadeComercialVenda, MovimentoFisicoTipo movimentoFisicoTipo, String observacao) {
        this.produto = produto;
        this.codigo = codigo;
        this.entrada = entrada;
        this.saida = saida;
        this.valor = valor;
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
        //return venda;
        
        if(venda != null) {
            return venda;
        } else {
            if(getMovimentoFisicoOrigem() != null) {
                return getMovimentoFisicoOrigem().getVenda();
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

    
    

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataAndamento() {
        return dataAndamento;
    }

    public void setDataAndamento(LocalDateTime dataAndamento) {
        this.dataAndamento = dataAndamento;
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

    
    
    
    
    //--------------------------------------------------------------------------
    
    
    public BigDecimal getSaldoLinear() {
        return getEntrada().subtract(getSaida());
    }
    
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
    
    public BigDecimal getSubtotal() {
        return getValor().multiply(getSaldoLinearAbsoluto());
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

    /*
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MovimentoFisico)) {
            return false;
        }
        return Objects.equals(this.getId(), ((MovimentoFisico) obj).getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }*/
}
