/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.Recurso;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class RecursoDAO {
    public Recurso save(Recurso recurso){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(recurso.getId() == null){
                em.persist(recurso);
            }else{
                em.merge(recurso);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println( "Erro em recurso.save " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return recurso;
    }

    public Recurso findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Recurso recurso = null;
        
        try {
            recurso = em.find(Recurso.class, id);
        } catch (Exception e) {
            System.err.println("Erro em recurso.findById " + e);
        } finally {
            em.close();
        }
        
        return recurso;
    }
    
    
    public List<Recurso> findAll(){
        return findByCriteria(false);
    }
    
    public List<Recurso> findByCriteria(boolean exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Recurso> grupos = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Recurso> q = cb.createQuery(Recurso.class);
            Root<Recurso> rootRecurso = q.from(Recurso.class);

            //List<Predicate> predicates = new ArrayList<>();


            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootRecurso.get("exclusao")));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootRecurso.get("nome")));

            //q.select(rootRecurso).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            q.select(rootRecurso).where(predicateExclusao);

            q.orderBy(o);

            TypedQuery<Recurso> query = em.createQuery(q);

            grupos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return grupos;
    }
    
    
    public Recurso delete(Recurso recurso) {
        if(recurso.getExclusao() == null) {
            recurso.setExclusao(LocalDateTime.now());
        }
        return save(recurso);
    }
    
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Set<Recurso> recursos = new HashSet<>();
        recursos.add(Recurso.SISTEMA);
        recursos.add(Recurso.USUARIOS);
        recursos.add(Recurso.PRODUTOS);
        recursos.add(Recurso.FATURAMENTO);
        recursos.add(Recurso.COMANDAS);
        recursos.add(Recurso.FINANCEIRO);
        recursos.add(Recurso.PESSOAS);
        recursos.add(Recurso.BACKUP);

        recursos.add(Recurso.ORCAMENTO);
        recursos.add(Recurso.VENDA);
        recursos.add(Recurso.PEDIDO);
        recursos.add(Recurso.ORDEM_DE_SERVICO);
        recursos.add(Recurso.LOCACAO);
        recursos.add(Recurso.COMPRA);
        
        
        
        em.getTransaction().begin();
        for(Recurso recurso : recursos){
            if(findById(recurso.getId()) == null){
                em.persist(recurso);
            } else {
                em.merge(recurso);
            }
        }
        em.getTransaction().commit();

        em.close();
    }
    
}
