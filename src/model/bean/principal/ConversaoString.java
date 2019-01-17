package model.bean.principal;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author Joe
 */

public class ConversaoString {
    
    private String quantidade;
    private String descricao;
    private String valor;
    private String subTotal;
    private String dataEntrega;
    private String dataRetirada;
    private String valorTotal;
    
    private String nome;
    private String cnpj;
    private String cep;
    private String telefone1;
    private String telefone2;
    private String ie;
    private String im;
    private String email;

    public String getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = Decimal.toString(quantidade);
    }

    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
            return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = Decimal.toString(valor);
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = Decimal.toString(subTotal);
    }
    
    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = DateTime.toStringDate(dataEntrega);
    }

    public String getDataRetirada() {
        return dataRetirada;
    }


    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = DateTime.toStringDate(dataRetirada);
    }
    
    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = Decimal.toString(valorTotal);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getIe() {
        return ie;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

