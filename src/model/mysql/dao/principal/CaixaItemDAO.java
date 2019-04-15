/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Caixa;
import model.mysql.bean.principal.CaixaItem;
import model.mysql.bean.principal.CaixaItemTipo;
import model.mysql.bean.principal.Venda;
import model.mysql.bean.principal.MovimentoFisico;
import static ouroboros.Ouroboros.em;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class CaixaItemDAO {

    public CaixaItem save(CaixaItem caixaItem) {
        try {
            em.getTransaction().begin();
            if (caixaItem.getId() == null) {
                em.persist(caixaItem);
            } else {
                em.merge(caixaItem);
            }
            
            em.merge(calcularSaldo(caixaItem));
            
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return caixaItem;
    }
    
    private CaixaItem calcularSaldo(CaixaItem caixaItem){
        System.out.println("caixaItem id: " + caixaItem.getId());
        BigDecimal saldoAnterior = getSaldoAnterior(caixaItem);
        System.out.println("saldoAnterior: " + saldoAnterior);
        caixaItem.setSaldoAcumulado(saldoAnterior.add(caixaItem.getCredito()).subtract(caixaItem.getDebito()));
        
        return caixaItem;
    }

    public CaixaItem findById(Integer id) {
        CaixaItem caixaItem = null;
        try {
            caixaItem = em.find(CaixaItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaItem;
    }

    public List<CaixaItem> findByCaixa(Caixa caixa, CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento) {
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> caixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.equal(caixaItem.get("caixa"), caixa));
            
            if(caixaItemTipo != null && caixaItemTipo.getId() > 0) {
                predicates.add(cb.equal(caixaItem.get("caixaItemTipo"), caixaItemTipo));
            }
            
            if(meioDePagamento != null && meioDePagamento.getId() > 0) {
                predicates.add(cb.equal(caixaItem.get("meioDePagamento"), meioDePagamento));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(caixaItem.get("id")));

            q.select(caixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);

            caixaItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaItens;
    }
    
    public List<CaixaItem> findByCriteria(LocalDate dataInicial, LocalDate dataFinal) {
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> caixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(caixaItem.get("criacao"), (Comparable) dataInicial.atStartOfDay() ));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(caixaItem.get("criacao"), (Comparable) dataFinal.atTime(23, 59, 59)));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(caixaItem.get("id")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(caixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            caixaItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaItens;
    }
    
    public BigDecimal getSaldoAnterior(CaixaItem caixaItem) {
        try {
            Query q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where id < :id and caixaId = :caixaId");
            q.setParameter("id", caixaItem.getId());
            q.setParameter("caixaId", caixaItem.getCaixa().getId());
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            } else {
                return BigDecimal.ZERO;
            }

        } catch(Exception e){
            System.err.println(e);
        }
        return null;
    }
    
    /**
     * 
     * @param itemEstornar
     * @return objeto estorno gerado
     */
    public CaixaItem estornar(CaixaItem itemEstornar) {
        CaixaItem estorno = itemEstornar.deepClone();
        estorno.setId(null);
        estorno.setCredito(itemEstornar.getDebito());
        estorno.setDebito(itemEstornar.getCredito());
        estorno.setCaixaItemTipo(CaixaItemTipo.ESTORNO);
        estorno.setEstornoOrigem(itemEstornar);

        save(estorno);
        
        //itemEstornar.setEstornoId(estorno.getId());
        itemEstornar = save(itemEstornar);
        em.refresh(itemEstornar);
        
        if(itemEstornar.getParcela() != null) {
            em.refresh(itemEstornar.getParcela());
        }
        return estorno;
    }
}
