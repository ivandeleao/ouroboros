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
import model.mysql.bean.principal.catalogo.TabelaPrecoVariacao;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class TabelaPrecoVariacaoDAO {
    public TabelaPrecoVariacao save(TabelaPrecoVariacao tabelaPreco) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (tabelaPreco.getId() == null) {
                em.persist(tabelaPreco);
            } else {
                em.merge(tabelaPreco);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return tabelaPreco;
    }

    public TabelaPrecoVariacao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        TabelaPrecoVariacao tabelaPreco = null;
        try {
            tabelaPreco = em.find(TabelaPrecoVariacao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return tabelaPreco;
    }
    
    public List<TabelaPrecoVariacao> findAll(){
        return findByCriteria(false);
    }
    
    public List<TabelaPrecoVariacao> findByCriteria(boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<TabelaPrecoVariacao> tabelaPrecoVariacoes = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<TabelaPrecoVariacao> q = cb.createQuery(TabelaPrecoVariacao.class);
            Root<TabelaPrecoVariacao> rootTabelaPreco = q.from(TabelaPrecoVariacao.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootTabelaPreco.get("exclusao")));
            }
            
            //List<Order> o = new ArrayList<>();
            //o.add(cb.asc(rootTabelaPreco.get("nome")));
            
            q.select(rootTabelaPreco).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            
            q.select(rootTabelaPreco).where(cb.or(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            
            //q.orderBy(o);
            
            TypedQuery<TabelaPrecoVariacao> query = em.createQuery(q);
            
            tabelaPrecoVariacoes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return tabelaPrecoVariacoes;
    }
    
    public TabelaPrecoVariacao delete(TabelaPrecoVariacao tabelaPreco) {
        tabelaPreco.setExclusao(LocalDateTime.now());

        return save(tabelaPreco);
    }
}
