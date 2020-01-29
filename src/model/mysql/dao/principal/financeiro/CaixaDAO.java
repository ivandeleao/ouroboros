/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.temp.CaixaResumoPorMeioDePagamento;
import model.mysql.dao.fiscal.MeioDePagamentoDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CaixaDAO {
    public Caixa save(Caixa caixa) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return caixa;
    }

    public Caixa findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Caixa caixa = null;
        try {
            caixa = em.find(Caixa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return caixa;
    }

    public List<Caixa> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Caixa> caixas = null;
        try {
            Query query = em.createQuery("from Caixa c order by criacao desc");

            caixas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return caixas;
    }
    
    //--------------------------------------------------------------------------
    
    public Caixa getLastCaixa() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }

        return caixa;
    }
    
    public BigDecimal getSaldo(Caixa caixa){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            Query q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where caixaId = :caixaId");
            q.setParameter("caixaId", caixa.getId());
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            }
        } catch(Exception e){
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getSaldoPorMeioDePagamento(Caixa caixa, MeioDePagamento meioDePagamento){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            Query q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where caixaId = :caixaId and meioDePagamentoId = :meioDePagamento");
            q.setParameter("caixaId", caixa.getId());
            q.setParameter("meioDePagamento", meioDePagamento);
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            }
        } catch(Exception e){
            System.err.println(e);
        }finally {
            em.close();
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalCreditoPorMeioDePagamento(Caixa caixa, MeioDePagamento meioDePagamento) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            Query q = em.createNativeQuery("select sum(credito) as credito from caixaItem where caixaId = :caixaId and meioDePagamentoId = :meioDePagamento");
            q.setParameter("caixaId", caixa.getId());
            q.setParameter("meioDePagamento", meioDePagamento);
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            }
        } catch(Exception e){
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return BigDecimal.ZERO;
    }
    
    public List<CaixaResumoPorMeioDePagamento> getResumoPorMeioDePagamento(List<CaixaItem> caixaItens) {
        //Total de Crédito e Débito
        List<CaixaResumoPorMeioDePagamento> resumo = new ArrayList<>();
        
        MeioDePagamentoDAO mpDAO = new MeioDePagamentoDAO();
        
        for(MeioDePagamento mp : mpDAO.findAll()) {
            BigDecimal creditoTotal = BigDecimal.ZERO;
            BigDecimal debitoTotal = BigDecimal.ZERO;
            BigDecimal saldoCreditoDebito = BigDecimal.ZERO;
            BigDecimal suprimentoTotal = BigDecimal.ZERO;
            BigDecimal sangriaTotal = BigDecimal.ZERO;
            BigDecimal saldoSuprimentoSangria = BigDecimal.ZERO;
            BigDecimal saldoFinal = BigDecimal.ZERO;

            //System.out.println("dao resumo-----------------------------------------");
            for(CaixaItem caixaItem : caixaItens) {
                /*System.out.println("ci: " + caixaItem.getId() + " credito: " + caixaItem.getCredito()
                        + " mp: " + caixaItem.getMeioDePagamento());*/
                if(caixaItem.getMeioDePagamento().equals(mp)) {
                    //System.out.println(" - - - igual");
                    if(!caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.SUPRIMENTO) &&
                            !caixaItem.getCaixaItemTipo().equals(CaixaItemTipo.SANGRIA)) {
                        
                        creditoTotal = creditoTotal.add(caixaItem.getCredito());
                        debitoTotal = debitoTotal.add(caixaItem.getDebito());
                        saldoCreditoDebito = creditoTotal.subtract(debitoTotal);
                        
                    } else {
                            
                        suprimentoTotal = suprimentoTotal.add(caixaItem.getCredito());
                        sangriaTotal = sangriaTotal.add(caixaItem.getDebito());
                        saldoSuprimentoSangria = suprimentoTotal.subtract(sangriaTotal);
                    }
                }
                
                saldoFinal = saldoCreditoDebito.add(saldoSuprimentoSangria);
            }
            
            CaixaResumoPorMeioDePagamento r = new CaixaResumoPorMeioDePagamento(mp, creditoTotal, debitoTotal, saldoCreditoDebito, suprimentoTotal, sangriaTotal, saldoSuprimentoSangria, saldoFinal);
            
            resumo.add(r);
            
        }
        
        return resumo;
        
    }
    
}
