/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.financeiro.Conta;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CaixaItemDAO {

    public CaixaItem save(CaixaItem caixaItem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return caixaItem;
    }
    
    private CaixaItem calcularSaldo(CaixaItem caixaItem){
        BigDecimal saldoAnterior = getSaldoAnterior(caixaItem);
        caixaItem.setSaldoAcumulado(saldoAnterior.add(caixaItem.getCredito()).subtract(caixaItem.getDebito()));
        
        return caixaItem;
    }

    public CaixaItem findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        CaixaItem caixaItem = null;
        try {
            caixaItem = em.find(CaixaItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return caixaItem;
    }

    public List<CaixaItem> findByCaixa(Caixa caixa, CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return caixaItens;
    }
    
    public List<CaixaItem> findByCriteria(LocalDate dataInicial, LocalDate dataFinal, Conta conta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
            
            if (conta != null) {
                predicates.add(cb.equal(caixaItem.get("conta"), conta));
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
        } finally {
            em.close();
        }
        
        return caixaItens;
    }
    
    public BigDecimal getSaldoAnterior(CaixaItem caixaItem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            //2019-12-03 Nova entidade Conta
            Query q;
            if(caixaItem.getCaixa() != null) {
                q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where id < :id and caixaId = :caixaId");
                q.setParameter("caixaId", caixaItem.getCaixa().getId());
                
            } else {
                q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where id < :id and contaId = :contaId");
                q.setParameter("contaId", caixaItem.getConta().getId());
                
            }
            
            q.setParameter("id", caixaItem.getId());
            
            if(q.getSingleResult() != null){
                return (BigDecimal) q.getSingleResult();
            } else {
                return BigDecimal.ZERO;
            }

        } catch(Exception e){
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return null;
    }
    
    public CaixaItem estornar(CaixaItem itemEstornar) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Parcela parcela = itemEstornar.getParcela();
        if(parcela != null) {
            parcela.setDescontoPercentual(BigDecimal.ZERO);
            parcela.setAcrescimoMonetario(BigDecimal.ZERO);
            new ParcelaDAO().save(parcela);
        }
        
        CaixaItem estorno = itemEstornar.deepClone();
        //estorno.setCaixa(new CaixaDAO().getLastCaixa()); //2019-12-11
        estorno.setConta(itemEstornar.getConta()); //2019-12-11
        estorno.setId(null);
        estorno.setCredito(itemEstornar.getDebito());
        estorno.setDebito(itemEstornar.getCredito());
        estorno.setCaixaItemTipo(CaixaItemTipo.ESTORNO);
        estorno.setEstornoOrigem(itemEstornar);

        save(estorno);
        
        //itemEstornar.setEstornoId(estorno.getId());
        itemEstornar = save(itemEstornar);
        ////em.refresh(itemEstornar); 2019-12-04
        
        /*if(parcela != null) {
            em.refresh(parcela);
        }*/
        em.close();
        
        return estorno;
    }
    
    public CaixaItem estornarDeCaixa(CaixaItem itemEstornar) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Parcela parcela = itemEstornar.getParcela();
        parcela.setDescontoPercentual(BigDecimal.ZERO);
        parcela.setAcrescimoMonetario(BigDecimal.ZERO);
        new ParcelaDAO().save(parcela);
        
        CaixaItem estorno = itemEstornar.deepClone();
        estorno.setCaixa(new CaixaDAO().getLastCaixa());
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
        em.close();
        
        return estorno;
    }
}
