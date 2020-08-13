/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.financeiro.Conta;
import model.nosql.ContaTipoEnum;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ContaDAO {
    public Conta save(Conta conta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (conta.getId() == null) {
                em.persist(conta);
            } else {
                em.merge(conta);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return conta;
    }

    public Conta findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Conta conta = null;
        try {
            conta = em.find(Conta.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return conta;
    }

    public List<Conta> findAll() {
        return findByCriteria(null, false);
    }
    
    public List<Conta> findByTipo(ContaTipoEnum contaTipo) {
        return findByCriteria(contaTipo, false);
    }
    
    public List<Conta> findAllBoleto() {
        return findAll().stream().filter(c -> c.isBoleto()).collect(Collectors.toList());
    }
    
    public List<Conta> findByCriteria(ContaTipoEnum contaTipo, boolean exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Conta> contas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Conta> q = cb.createQuery(Conta.class);
            Root<Conta> rootConta = q.from(Conta.class);

            List<Predicate> predicates = new ArrayList<>();
            
            if (contaTipo != null) {
                predicates.add(cb.equal(rootConta.get("contaTipo"), contaTipo));
            }

            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootConta.get("exclusao")));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootConta.get("nome")));

            if (predicates.isEmpty()) {
                q.select(rootConta).where(predicateExclusao);
            } else {
                q.select(rootConta).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            }

            q.orderBy(o);

            TypedQuery<Conta> query = em.createQuery(q);

            contas = query.getResultList();
            //em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em conta.findByCriteria " + e);
        } finally {
            em.close();
        }
        
        return contas;
    }
    
    
    public Conta delete(Conta conta) {
        conta.setExclusao(LocalDateTime.now());

        return save(conta);
    }
    
    public void bootstrap() {
        Conta conta = new Conta();
        
        conta.setId(1);
        conta.setNome("Principal");
        
        save(conta);
    }
    
    //--------------------------------------------------------------------------
    
    
    
    
}
