/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Produto;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CategoriaDAO {
    public Categoria save(Categoria categoria) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        return categoria;
    }

    public Categoria findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Categoria categoria = null;
        try {
            categoria = em.find(Categoria.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return categoria;
    }
    
    public List<Categoria> findAll(){
        return findByCriteria("", false);
    }
    
    public List<Categoria> findByCriteria(String buscaRapida, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Categoria> categorias = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Categoria> q = cb.createQuery(Categoria.class);
            Root<Categoria> rootCategoria = q.from(Categoria.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(buscaRapida != null){
                predicates.add(cb.like(rootCategoria.get("nome"), "%"+buscaRapida+"%"));
            }
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootCategoria.get("exclusao")));
            }
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootCategoria.get("nome")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootCategoria).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            
            q.select(rootCategoria).where(cb.or(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            
            q.orderBy(o);
            
            TypedQuery<Categoria> query = em.createQuery(q);
            
            categorias = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return categorias;
    }
    
    public Categoria delete(Categoria categoria) {
        categoria.setExclusao(LocalDateTime.now());

        return save(categoria);
    }
}
