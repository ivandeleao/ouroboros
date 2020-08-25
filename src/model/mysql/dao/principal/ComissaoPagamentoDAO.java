/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.ComissaoPagamento;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ComissaoPagamentoDAO {
    public ComissaoPagamento save(ComissaoPagamento comissaoPagamento){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(comissaoPagamento.getId() == null){
                em.persist(comissaoPagamento);
            }else{
                em.merge(comissaoPagamento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return comissaoPagamento;
    }

    public ComissaoPagamento findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ComissaoPagamento comissaoPagamento = null;
        try {
            comissaoPagamento = em.find(ComissaoPagamento.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return comissaoPagamento;
    }
    
    
    public List<ComissaoPagamento> findAll(boolean exibirExcluidos) {
        return findByCriteria(false);
    }
    
    
    
    public List<ComissaoPagamento> findByCriteria(boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ComissaoPagamento> listComissaoPagamento = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<ComissaoPagamento> q = cb.createQuery(ComissaoPagamento.class);
            Root<ComissaoPagamento> rootComissaoPagamento = q.from(ComissaoPagamento.class);
            
            //List<Predicate> predicates = new ArrayList<>();
            
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootComissaoPagamento.get("exclusao")));
            }
            
            
            /*List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootComissaoPagamento.get("nome")));
            */
            
            //q.select(rootComissaoPagamento).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.select(rootComissaoPagamento).where(predicateExclusao);
            
            //q.orderBy(o);
            
            TypedQuery<ComissaoPagamento> query = em.createQuery(q);
            
            
            listComissaoPagamento = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return listComissaoPagamento;
    }
    
    
    public ComissaoPagamento delete(ComissaoPagamento comissaoPagamento) {
        comissaoPagamento.setExclusao(LocalDateTime.now());

        return save(comissaoPagamento);
    }
}
