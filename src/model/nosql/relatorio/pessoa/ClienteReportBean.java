/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.nosql.relatorio.pessoa;

import java.math.BigDecimal;
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.pessoa.Pessoa;

/**
 *
 * @author ivand
 */
public class ClienteReportBean {
    
    private String vendedor;
    private String cidade;
    private String clienteId;
    private String clienteNome;
    private BigDecimal saldo;

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    
}
