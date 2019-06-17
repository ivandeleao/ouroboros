/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ProdutoTipoDAO {
    
    public void bootstrap() {
        List<ProdutoTipo> cits = new ArrayList<>();
        cits.add(ProdutoTipo.PRODUTO);
        cits.add(ProdutoTipo.SERVICO);
        
        em.getTransaction().begin();
        for(ProdutoTipo cit : cits){
            if(findById(cit.getId()) == null){
                em.persist(cit);
            } else {
                em.merge(cit);
            }
        }
        em.getTransaction().commit();

    }
    
    public ProdutoTipo save(ProdutoTipo produtoTipo) {
        try {
            em.getTransaction().begin();
            if (produtoTipo.getId() == null) {
                em.persist(produtoTipo);
            } else {
                em.merge(produtoTipo);
            }
            //em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return produtoTipo;
    }

    public ProdutoTipo findById(Integer id) {
        ProdutoTipo produtoTipo = null;
        try {
            produtoTipo = em.find(ProdutoTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return produtoTipo;
    }
    
    public List<ProdutoTipo> findAll(){
        List<ProdutoTipo> produtoTipos = null;
        try {
            produtoTipos = em.createQuery("from ProdutoTipo t").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return produtoTipos;
    }
    
    public List<ProdutoTipo> findByCriteria(String buscaRapida){
        List<ProdutoTipo> produtoTipos = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<ProdutoTipo> q = cb.createQuery(ProdutoTipo.class);
            Root<ProdutoTipo> rootProdutoTipo = q.from(ProdutoTipo.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(buscaRapida != null){
                predicates.add(cb.like(rootProdutoTipo.get("nome"), "%"+buscaRapida+"%"));
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootProdutoTipo.get("nome")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootProdutoTipo).where(cb.or(predicates.toArray(new Predicate[]{})));
            q.orderBy(o);
            
            TypedQuery<ProdutoTipo> query = em.createQuery(q);
            
            produtoTipos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return produtoTipos;
    }
}
