/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.time.LocalDate;
import model.mysql.dao.principal.catalogo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.Cheque;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ChequeDAO {
    public Cheque save(Cheque cheque) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (cheque.getId() == null) {
                em.persist(cheque);
            } else {
                em.merge(cheque);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return cheque;
    }

    public Cheque findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Cheque cheque = null;
        try {
            cheque = em.find(Cheque.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return cheque;
    }
    
    public List<Cheque> findAll(){
        return findByCriteria(null, null, null, null, null, null, false);
    }
    
    public List<Cheque> findByCriteria(String conta, String numero, String correntista, LocalDate dataInicial, LocalDate dataFinal, Optional<Boolean> utilizado, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Cheque> cheques = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Cheque> q = cb.createQuery(Cheque.class);
            Root<Cheque> rootCheque = q.from(Cheque.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            /*if(buscaRapida != null){
                predicates.add(cb.like(rootCheque.get("correntista"), "%"+buscaRapida+"%"));
            }*/
            
            if(conta != null){
                predicates.add(cb.like(rootCheque.get("conta"), "%" + conta + "%"));
            }
            
            if(numero != null){
                predicates.add(cb.like(rootCheque.get("numero"), "%" + numero + "%"));
            }
            
            if(correntista != null){
                predicates.add(cb.like(rootCheque.get("correntista"), "%" + correntista + "%"));
            }
            
            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootCheque.get("vencimento"), (Comparable) dataInicial));
            }
            
            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootCheque.get("vencimento"), (Comparable) dataFinal));
            }
            
            if (utilizado != null && utilizado.isPresent()) {
                if (utilizado.get()) {
                    predicates.add(cb.isNotNull(rootCheque.get("utilizado")));
                } else {
                    predicates.add(cb.isNull(rootCheque.get("utilizado")));
                }
            }
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootCheque.get("exclusao")));
            }
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootCheque.get("vencimento")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            ////q.select(rootCheque).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            q.select(rootCheque).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Cheque> query = em.createQuery(q);
            
            cheques = query.getResultList();
            
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return cheques;
    }
    
    public Cheque delete(Cheque cheque) {
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
        for (CaixaItem ci : cheque.getCaixaItens()) {
            ci.setCheque(null);
            caixaItemDAO.save(ci);
        }
        //cheque.setCaixaItens(new ArrayList<>());
        cheque.setExclusao(LocalDateTime.now());

        return save(cheque);
    }
}
