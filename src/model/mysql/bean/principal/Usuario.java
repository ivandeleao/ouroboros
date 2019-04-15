/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.swing.JOptionPane;
import model.mysql.dao.principal.RecursoDAO;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.MAIN_VIEW;

/**
 *
 * @author ivand
 */
@Entity
public class Usuario implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    private String login;
    private String senha;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Diretiva> diretivas = new HashSet<>();

    
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }


    public LocalDateTime getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(LocalDateTime atualizacao) {
        this.atualizacao = atualizacao;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Diretiva> getDiretivas() {
        return diretivas;
    }

    public void setDiretivas(Set<Diretiva> diretivas) {
        this.diretivas = diretivas;
    }

    
    //--------------------------------------------------------------------------
    
    /**
     * Adiciona ou atualiza diretiva do usuário
     * @param diretiva
     */
    public void addDiretiva(Diretiva diretiva) {
        
        diretiva.setUsuario(this);
        
        for(Diretiva d : diretivas) {
            System.out.println("Diretiva antes: " + d.getUsuario().getId() + " - " + d.getRecurso().getNome() + " - " + d.getStatus());
        }
        
        //setDiretiva.removeIf((Diretiva d) -> d.equals(diretiva));
        Diretiva diretivaFind = findDiretiva(diretiva.getRecurso());
        if(diretivaFind != null) {
            diretivaFind.setStatus(diretiva.getStatus());
            diretivas.remove(diretivaFind);
            diretivas.add(diretivaFind);
        } else {
            diretivas.add(diretiva);
        }
        
        for(Diretiva d : diretivas) {
            System.out.println("Diretiva depois: " + d.getUsuario().getId() + " - " + d.getRecurso().getNome() + " - " + d.getStatus());
        }
        
        diretiva.setUsuario(this);
    }
    
    /**
     * Encontra diretiva pelo recurso ignorando o status
     * @param recurso
     * @return 
     */
    public Diretiva findDiretiva(Recurso recurso) {
        System.out.println("Encontrar - recurso: " + recurso.getId());
        for(Diretiva diretiva : this.diretivas) {
            if(diretiva.getRecurso().equals(recurso)) {
                System.out.println("findDiretiva id: " + diretiva.getId());
                return diretiva;
            }
        }
        return null;
    }
    
    public void normalizarDiretivas() {
        //Obter lista de recursos existentes e adicionar caso não exista
        for(Recurso recurso : new RecursoDAO().findAll()) {
            if(findDiretiva(recurso) == null) {
                Diretiva diretiva = new Diretiva(this, recurso, DiretivaStatus.BLOQUEADO);
                this.addDiretiva(diretiva);
            }
        }
    }
    
    public boolean autorizarAcesso(Recurso recurso) {
        //TODO: liberação por supervisor
        Diretiva d = findDiretiva(recurso);
        if(d.getStatus().equals(DiretivaStatus.LIBERADO)) {
            return true;
        } else {
            JOptionPane.showMessageDialog(MAIN_VIEW, "Acesso negado", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}
