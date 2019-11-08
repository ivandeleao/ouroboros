/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.List;
import javax.persistence.EntityManager;
import model.mysql.bean.principal.catalogo.ProdutoTamanho;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ProdutoTamanhoDAO {
    
    public ProdutoTamanho save(ProdutoTamanho produtoTamanho) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (produtoTamanho.getId() == null) {
                em.persist(produtoTamanho);
            } else {
                em.merge(produtoTamanho);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return produtoTamanho;
    }

    public ProdutoTamanho findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ProdutoTamanho produtoTamanho = null;
        try {
            produtoTamanho = em.find(ProdutoTamanho.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return produtoTamanho;
    }
    
    public List<ProdutoTamanho> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ProdutoTamanho> produtoTamanhos = null;
        try {
            produtoTamanhos = em.createQuery("from ProdutoTamanho pt").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return produtoTamanhos;
    }
    

}
