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
import model.mysql.bean.principal.financeiro.Cartao;
import model.nosql.CartaoTipoEnum;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CartaoDAO {
    public Cartao save(Cartao cartao) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (cartao.getId() == null) {
                em.persist(cartao);
            } else {
                em.merge(cartao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return cartao;
    }

    public Cartao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Cartao cartao = null;
        try {
            cartao = em.find(Cartao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return cartao;
    }

    public List<Cartao> findAll() {
        return findByCriteria(null, false);
    }
    
    public List<Cartao> findByTipo(CartaoTipoEnum cartaoTipo) {
        return findByCriteria(cartaoTipo, false);
    }
    
    public List<Cartao> findByCriteria(CartaoTipoEnum cartaoTipo, boolean exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Cartao> cartoes = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Cartao> q = cb.createQuery(Cartao.class);
            Root<Cartao> rootCartao = q.from(Cartao.class);

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

            TypedQuery<Cartao> query = em.createQuery(q);

            cartoes = query.getResultList();
            //em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em cartao.findByCriteria " + e);
        } finally {
            em.close();
        }
        
        return cartoes;
    }
    
    
    public Cartao delete(Cartao cartao) {
        cartao.setExclusao(LocalDateTime.now());

        return save(cartao);
    }
    
    
    //--------------------------------------------------------------------------
    
    
    
    
}
