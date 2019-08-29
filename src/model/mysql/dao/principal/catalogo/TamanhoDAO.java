/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.List;
import model.mysql.bean.principal.catalogo.Tamanho;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class TamanhoDAO {
    
    public Tamanho save(Tamanho tamanho) {
        try {
            em.getTransaction().begin();
            if (tamanho.getId() == null) {
                em.persist(tamanho);
            } else {
                em.merge(tamanho);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return tamanho;
    }

    public Tamanho findById(Integer id) {
        Tamanho tamanho = null;
        try {
            tamanho = em.find(Tamanho.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return tamanho;
    }
    
    public List<Tamanho> findAll(){
        List<Tamanho> tamanhos = null;
        try {
            tamanhos = em.createQuery("from Tamanho t").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tamanhos;
    }
    

}
