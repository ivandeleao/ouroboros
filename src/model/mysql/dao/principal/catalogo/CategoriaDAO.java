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
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Categoria;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CategoriaDAO {
    public Categoria save(Categoria categoria) {
        try {
            em.getTransaction().begin();
            if (categoria.getId() == null) {
                em.persist(categoria);
            } else {
                em.merge(categoria);
            }
            //em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return categoria;
    }

    public Categoria findById(Integer id) {
        Categoria categoria = null;
        try {
            categoria = em.find(Categoria.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return categoria;
    }
    
    public List<Categoria> findAll(){
        return findByCriteria("");
    }
    
    public List<Categoria> findByCriteria(String buscaRapida){
        List<Categoria> categorias = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Categoria> q = cb.createQuery(Categoria.class);
            Root<Categoria> rootCategoria = q.from(Categoria.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(buscaRapida != null){
                predicates.add(cb.like(rootCategoria.get("nome"), "%"+buscaRapida+"%"));
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootCategoria.get("nome")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootCategoria).where(cb.or(predicates.toArray(new Predicate[]{})));
            q.orderBy(o);
            
            TypedQuery<Categoria> query = em.createQuery(q);
            
            categorias = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return categorias;
    }
}
