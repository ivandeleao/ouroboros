/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.ContaProgramada;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ContaProgramadaDAO {
    public ContaProgramada save(ContaProgramada contaProgramada) {
        try {
            em.getTransaction().begin();
            if (contaProgramada.getId() == null) {
                em.persist(contaProgramada);
            } else {
                em.merge(contaProgramada);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro ContaProgramada.save " + e);
            em.getTransaction().rollback();
        }
        return contaProgramada;
    }

    public ContaProgramada findById(Integer id) {
        ContaProgramada contaProgramada = null;
        try {
            contaProgramada = em.find(ContaProgramada.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaProgramada;
    }

    public List<ContaProgramada> findAll() {
        List<ContaProgramada> contaProgramadas = null;
        try {
            Query query = em.createQuery("from ContaProgramada c order by nome");

            contaProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaProgramadas;
    }
    
    public List<ContaProgramada> findPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<ContaProgramada> contasPagarProgramadas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<ContaProgramada> cq = cb.createQuery(ContaProgramada.class);
            Root<ContaProgramada> rootContaProgramada = cq.from(ContaProgramada.class);
            
            //Join<ContaProgramada, ContaProgramadaBaixa> rootJoin = rootContaProgramada.join("contaProgramadaBaixa", JoinType.LEFT);
            //cq.multiselect(rootContaProgramada, rootJoin);

            
            Predicate inicioIsLessThanInicial = cb.lessThan(rootContaProgramada.get("inicio"), (Comparable) dataInicial);
            Predicate terminoIsLessThanInicial = cb.lessThan(rootContaProgramada.get("termino"), (Comparable) dataInicial);
            
            Predicate inicioIsGreaterThanFinal = cb.greaterThan(rootContaProgramada.get("inicio"), (Comparable) dataFinal);
            Predicate terminoIsGreaterThanFinal = cb.greaterThan(rootContaProgramada.get("termino"), (Comparable) dataFinal);
            
            Predicate limites = cb.and(
                    cb.not(cb.and(inicioIsLessThanInicial, terminoIsLessThanInicial)),
                    cb.not(cb.and(inicioIsGreaterThanFinal, terminoIsGreaterThanFinal))
                    );

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootContaProgramada.get("inicio")));

            cq.select(rootContaProgramada).where(limites);
            cq.orderBy(o);

            TypedQuery<ContaProgramada> query = em.createQuery(cq);

            contasPagarProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return contasPagarProgramadas;
    }
    
}
