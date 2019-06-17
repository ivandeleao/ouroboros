/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.bean.principal.catalogo.ProdutoComponenteId;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ProdutoComponenteDAO {
    public ProdutoComponente save(ProdutoComponente produtoComponente){
        try {
            em.getTransaction().begin();
            ProdutoComponenteId id = new ProdutoComponenteId(produtoComponente.getProdutoId(), produtoComponente.getComponenteId());
            if(findById(id) == null){ //
                em.persist(produtoComponente);
            }else{
                em.merge(produtoComponente);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        
        return produtoComponente;
    }

    public ProdutoComponente findById(ProdutoComponenteId id){
        ProdutoComponente produtoComponente = null;
        
        try {
            produtoComponente = em.find(ProdutoComponente.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return produtoComponente;
    }
    
    public List<ProdutoComponente> findAll(){
        List<ProdutoComponente> listProdutoComponente = null;
        try {
            Query query = em.createQuery("from ProdutoComponente pc");
            query.setMaxResults(50);
            listProdutoComponente = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listProdutoComponente;
    }
    
    public ProdutoComponente remove(ProdutoComponente produtoComponente) {
        em.getTransaction().begin();
        Query query = em.createQuery("Delete from ProdutoComponente pc where pc.produtoId = :pId and pc.componenteId = :cId");
        query.setParameter("pId", produtoComponente.getProdutoId());
        query.setParameter("cId", produtoComponente.getComponenteId());
        query.executeUpdate();
        em.getTransaction().commit();
        
        return produtoComponente;
    }
}
