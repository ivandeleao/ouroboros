/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.swing.JOptionPane;
import model.mysql.dao.principal.DiretivaDAO;
import model.mysql.dao.principal.RecursoDAO;
import model.mysql.dao.principal.UsuarioDAO;
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
    private boolean administrador;
    
    @OneToMany(mappedBy = "usuario")
    private List<Diretiva> diretivas = new ArrayList<>();

    
    
    
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

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public List<Diretiva> getDiretivas() {
        diretivas.sort(Comparator.comparing((diretiva -> diretiva.getRecurso().getNome())));
        return diretivas;
    }

    public void setDiretivas(List<Diretiva> diretivas) {
        this.diretivas = diretivas;
    }

    
    //--------------------------------------------------------------------------
    
    public void addDiretiva(Diretiva diretiva) {
        diretivas.remove(diretiva);
        diretivas.add(diretiva);
        diretiva.setUsuario(this);
    }
    
    public void removeDiretiva(Diretiva diretiva) {
        diretiva.setUsuario(null);
        diretivas.remove(diretiva);
    }
    
    
    /**
     * Encontra diretiva pelo recurso ignorando o status
     * @param recurso
     * @return 
     */
    public Diretiva findDiretiva(Recurso recurso) {
        //System.out.println("Encontrar - recurso: " + recurso.getId());
        for(Diretiva diretiva : this.diretivas) {
            if(diretiva.getRecurso().equals(recurso)) {
                //System.out.println("findDiretiva id: " + diretiva.getId());
                return diretiva;
            }
        }
        return null;
    }
    
    public void normalizarDiretivas() {
        //Obter lista de recursos existentes e adicionar caso não exista
        for(Recurso recurso : new RecursoDAO().findByCriteria(true)) {
            //System.out.println("recurso: " + recurso.getNome());
            if (recurso.isExcluido()) {
                //System.out.println("excluido");
                Diretiva diretiva = findDiretiva(recurso);
                if (diretiva != null) {
                    this.removeDiretiva(diretiva);
                    new DiretivaDAO().remove(diretiva);
                }
                
            } else if(findDiretiva(recurso) == null) {
                Diretiva diretiva = new Diretiva(recurso, DiretivaStatusEnum.LIBERADO);
                this.addDiretiva(diretiva);
                new DiretivaDAO().save(diretiva);
            }
        }
        
    }
    
    public boolean autorizarAcesso(Recurso recurso) {
        Diretiva d = findDiretiva(recurso);
        if(d.getStatus().equals(DiretivaStatusEnum.LIBERADO)) {
            System.out.println("liberado");
            return true;
            
        } else {
            System.out.println("testar");
            if (UsuarioDAO.validarAdministradorComLogin()) {
                System.out.println("admin valido");
                return true;
                
            } else {
                JOptionPane.showMessageDialog(MAIN_VIEW, "Acesso negado", "Atenção", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
    }
}
