/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDateTime;
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
import model.mysql.bean.principal.Funcionario;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class FuncionarioDAO {
    public Funcionario save(Funcionario funcionario){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(funcionario.getId() == null){
                em.persist(funcionario);
            }else{
                em.merge(funcionario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return funcionario;
    }

    public Funcionario findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Funcionario funcionario = null;
        try {
            funcionario = em.find(Funcionario.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return funcionario;
    }
    
    public List<Funcionario> findByNome(String nome){
        return findByCriteria(nome, false);
    }
    
    public List<Funcionario> findAll(boolean exibirExcluidos) {
        return findByCriteria(null, false);
    }
    
    
    
    public List<Funcionario> findByCriteria(String nome, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Funcionario> listFuncionario = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Funcionario> q = cb.createQuery(Funcionario.class);
            Root<Funcionario> rootFuncionario = q.from(Funcionario.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
           
            if(nome != null){
                //achar por partes diversas do nome
                nome = nome.replaceAll(" ", "%");
                predicates.add(
                                cb.like(rootFuncionario.get("nome"), "%"+nome+"%")
                );
            }
            
            
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootFuncionario.get("exclusao")));
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootFuncionario.get("nome")));
            
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootFuncionario).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Funcionario> query = em.createQuery(q);
            
            
            listFuncionario = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return listFuncionario;
    }
    
    
    public Funcionario delete(Funcionario funcionario) {
        funcionario.setExclusao(LocalDateTime.now());

        return save(funcionario);
    }
}
