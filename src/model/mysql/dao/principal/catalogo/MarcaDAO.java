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
import model.mysql.bean.principal.catalogo.Marca;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class MarcaDAO {
    public Marca save(Marca marca) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (marca.getId() == null) {
                em.persist(marca);
            } else {
                em.merge(marca);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return marca;
    }

    public Marca findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Marca marca = null;
        try {
            marca = em.find(Marca.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return marca;
    }
    
    public List<Marca> findAll(){
        return findByCriteria("", false);
    }
    
    public List<Marca> findByCriteria(String buscaRapida, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Marca> marcas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Marca> q = cb.createQuery(Marca.class);
            Root<Marca> rootMarca = q.from(Marca.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(buscaRapida != null){
                predicates.add(cb.like(rootMarca.get("nome"), "%"+buscaRapida+"%"));
            }
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootMarca.get("exclusao")));
            }
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootMarca.get("nome")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootMarca).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            q.select(rootMarca).where(cb.or(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Marca> query = em.createQuery(q);
            
            marcas = query.getResultList();
            
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return marcas;
    }
    
    public Marca delete(Marca marca) {
        marca.setExclusao(LocalDateTime.now());

        return save(marca);
    }
}
