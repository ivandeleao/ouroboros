/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.Caixa;
import model.bean.fiscal.MeioDePagamento;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CaixaDAO {
    public Caixa save(Caixa caixa) {
        try {
            em.getTransaction().begin();
            if (caixa.getId() == null) {
                em.persist(caixa);
            } else {
                em.merge(caixa);
            }
            //em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return caixa;
    }

    public Caixa findById(Integer id) {
        Caixa caixa = null;
        try {
            caixa = em.find(Caixa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixa;
    }

    public List<Caixa> findAll() {
        List<Caixa> caixas = null;
        try {
            Query query = em.createQuery("from Caixa c order by criacao desc");

            caixas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixas;
    }
    
    public Caixa getLastCaixa() {
        Caixa caixa = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Caixa> q = cb.createQuery(Caixa.class);
            Root<Caixa> rootCaixa = q.from(Caixa.class);

            List<Predicate> predicates = new ArrayList<>();

            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.desc(rootCaixa.get("criacao")));

            q.select(rootCaixa).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<Caixa> query = em.createQuery(q);
            query.setMaxResults(1);

            System.out.println("query result: " + query.getSingleResult());

            if (query.getSingleResult() != null) {

                return (Caixa) query.getSingleResult();
            }

        } catch (NoResultException e) {
            //that's ok!
        } catch (Exception e) {
            System.err.println(e);
        }

        return caixa;
    }
    
    public BigDecimal getSaldo(Caixa caixa){
        try {
            Query q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where caixaId = :caixaId");
            q.setParameter("caixaId", caixa.getId());
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            }
        } catch(Exception e){
            System.err.println(e);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getSaldoPorMeioDePagamento(Caixa caixa, MeioDePagamento meioDePagamento){
        try {
            Query q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where caixaId = :caixaId and meioDePagamentoId = :meioDePagamento");
            q.setParameter("caixaId", caixa.getId());
            q.setParameter("meioDePagamento", meioDePagamento);
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            }
        } catch(Exception e){
            System.err.println(e);
        }
        return BigDecimal.ZERO;
    }
}
