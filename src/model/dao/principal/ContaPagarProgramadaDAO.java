/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.ContaPagarProgramada;
import model.bean.fiscal.MeioDePagamento;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ContaPagarProgramadaDAO {
    public ContaPagarProgramada save(ContaPagarProgramada contaPagarProgramada) {
        try {
            em.getTransaction().begin();
            if (contaPagarProgramada.getId() == null) {
                em.persist(contaPagarProgramada);
            } else {
                em.merge(contaPagarProgramada);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro ContaPagarProgramada.save " + e);
            em.getTransaction().rollback();
        }
        return contaPagarProgramada;
    }

    public ContaPagarProgramada findById(Integer id) {
        ContaPagarProgramada contaPagarProgramada = null;
        try {
            contaPagarProgramada = em.find(ContaPagarProgramada.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaPagarProgramada;
    }

    public List<ContaPagarProgramada> findAll() {
        List<ContaPagarProgramada> contaPagarProgramadas = null;
        try {
            Query query = em.createQuery("from ContaPagarProgramada c order by nome");

            contaPagarProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaPagarProgramadas;
    }
    
    public List<ContaPagarProgramada> findPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        List<ContaPagarProgramada> contasPagarProgramadas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<ContaPagarProgramada> q = cb.createQuery(ContaPagarProgramada.class);
            Root<ContaPagarProgramada> contaPagarProgramada = q.from(ContaPagarProgramada.class);

            
            Predicate inicioIsLessThanInicial = cb.lessThan(contaPagarProgramada.get("inicio"), (Comparable) dataInicial);
            Predicate terminoIsLessThanInicial = cb.lessThan(contaPagarProgramada.get("termino"), (Comparable) dataInicial);
            
            Predicate inicioIsGreaterThanFinal = cb.greaterThan(contaPagarProgramada.get("inicio"), (Comparable) dataFinal);
            Predicate terminoIsGreaterThanFinal = cb.greaterThan(contaPagarProgramada.get("termino"), (Comparable) dataFinal);
            
            Predicate limites = cb.and(
                    cb.not(cb.and(inicioIsLessThanInicial, terminoIsLessThanInicial)),
                    cb.not(cb.and(inicioIsGreaterThanFinal, terminoIsGreaterThanFinal))
                    );

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(contaPagarProgramada.get("inicio")));

            q.select(contaPagarProgramada).where(limites);
            q.orderBy(o);

            TypedQuery<ContaPagarProgramada> query = em.createQuery(q);

            contasPagarProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return contasPagarProgramadas;
    }
    
}
