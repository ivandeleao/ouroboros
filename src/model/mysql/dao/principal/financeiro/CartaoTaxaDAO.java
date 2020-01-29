/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

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
import model.mysql.bean.principal.financeiro.CartaoTaxa;
import model.nosql.CartaoTipoEnum;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CartaoTaxaDAO {
    public CartaoTaxa save(CartaoTaxa cartaoTaxa) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (cartaoTaxa.getId() == null) {
                em.persist(cartaoTaxa);
            } else {
                em.merge(cartaoTaxa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return cartaoTaxa;
    }

    public CartaoTaxa findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        CartaoTaxa cartaoTaxa = null;
        try {
            cartaoTaxa = em.find(CartaoTaxa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return cartaoTaxa;
    }

    public List<CartaoTaxa> findAll() {
        return findByCriteria(null, false);
    }
    
    public List<CartaoTaxa> findByTipo(CartaoTipoEnum cartaoTipo) {
        return findByCriteria(cartaoTipo, false);
    }
    
    public List<CartaoTaxa> findByCriteria(CartaoTipoEnum cartaoTipo, boolean exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CartaoTaxa> cartaoTaxas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CartaoTaxa> q = cb.createQuery(CartaoTaxa.class);
            Root<CartaoTaxa> rootCartao = q.from(CartaoTaxa.class);

            List<Predicate> predicates = new ArrayList<>();
            
            if (cartaoTipo != null) {
                predicates.add(cb.equal(rootCartao.get("cartaoTipo"), cartaoTipo));
            }

            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootCartao.get("exclusao")));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootCartao.get("nome")));

            if (predicates.isEmpty()) {
                q.select(rootCartao).where(predicateExclusao);
            } else {
                q.select(rootCartao).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            }

            q.orderBy(o);

            TypedQuery<CartaoTaxa> query = em.createQuery(q);

            cartaoTaxas = query.getResultList();
            //em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em cartaoTaxa.findByCriteria " + e);
        } finally {
            em.close();
        }
        
        return cartaoTaxas;
    }
    
    
    public CartaoTaxa delete(CartaoTaxa cartaoTaxa) {
        cartaoTaxa.setExclusao(LocalDateTime.now());

        return save(cartaoTaxa);
    }
    
    //--------------------------------------------------------------------------
    
    
    
    
}
