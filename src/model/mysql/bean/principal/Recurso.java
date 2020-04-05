/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Recursos do sistema: telas, ações e opções diversas para liberar/negar ao usuário
 * @author ivand
 */
@Entity
public class Recurso implements Serializable{
    
    @Id
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private LocalDateTime exclusao;
    
    private String nome;
    
    @OneToMany(mappedBy = "recurso")
    private List<Diretiva> usuarioRecursos = new ArrayList<>();
    
    
    //public static final Recurso SISTEMA = new Recurso(1, "SISTEMA"); // removido
    //public static final Recurso USUARIOS = new Recurso(2, "USUÁRIOS"); //removido
    public static final Recurso PRODUTOS = new Recurso(3, "PRODUTOS");
    public static final Recurso DOCUMENTOS_DE_SAIDA = new Recurso(4, "DOCUMENTOS DE SAÍDA");
    public static final Recurso COMANDAS = new Recurso(5, "COMANDAS");
    public static final Recurso FINANCEIRO = new Recurso(6, "FINANCEIRO");
    public static final Recurso CLIENTES_E_FORNECEDORES = new Recurso(7, "CLIENTES E FORNECEDORES");
    public static final Recurso BACKUP = new Recurso(8, "BACKUP");
    
    public static final Recurso ORCAMENTO = new Recurso(9, "ORÇAMENTO");
    public static final Recurso VENDA = new Recurso(10, "VENDA");
    public static final Recurso PEDIDO = new Recurso(11, "PEDIDO");
    public static final Recurso ORDEM_DE_SERVICO = new Recurso(12, "ORDEM DE SERVIÇO");
    public static final Recurso LOCACAO = new Recurso(13, "LOCAÇÃO");
    public static final Recurso DOCUMENTOS_DE_ENTRADA = new Recurso(14, "DOCUMENTOS DE ENTRADA");
    
    public static final Recurso DELIVERY = new Recurso(15, "DELIVERY");
    public static final Recurso VENDA_POR_FICHA = new Recurso(16, "VENDA POR FICHA");
    public static final Recurso FUNCIONARIO = new Recurso(17, "FUNCIONÁRIO");
    public static final Recurso VEICULOS = new Recurso(18, "VEÍCULOS");

    
    public Recurso() {
    }
    
    public Recurso(int id, String nome) {
        this.id = id;
        this.nome = nome;
        
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

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Diretiva> getUsuarioRecurso() {
        return usuarioRecursos;
    }

    public void setUsuarioRecurso(List<Diretiva> setUsuarioRecurso) {
        this.usuarioRecursos = setUsuarioRecurso;
    }
    
    //Facilitadores-------------------------------------------------------------
    public boolean isExcluido() {
        return getExclusao() != null;
    }
    //Fim Facilitadores---------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Recurso)) {
            return false;
        }
        return Objects.equals(this.getId(), ((Recurso) obj).getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    
}
