/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.Recurso;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class RecursoDAO {
    public Recurso save(Recurso recurso){
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
        }
        
        return recurso;
    }

    public Recurso findById(Integer id){
        Recurso recurso = null;
        
        try {
            recurso = em.find(Recurso.class, id);
        } catch (Exception e) {
            System.err.println("Erro em recurso.findById " + e);
        }
        
        return recurso;
    }
    
    
    public List<Recurso> findAll(){
        List<Recurso> listRecurso = null;
        try {
            Query query = em.createQuery("from " + Recurso.class.getSimpleName() + " r");
            listRecurso = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em recurso.findAll " + e);
        }
        return listRecurso;
    }
    
    public void bootstrap() {
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

    }
    
}
