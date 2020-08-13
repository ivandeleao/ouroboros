/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.agenda;

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
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.agenda.Tarefa;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class TarefaDAO {
    public Tarefa save(Tarefa tarefa){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(tarefa.getId() == null){
                em.persist(tarefa);
            }else{
                em.merge(tarefa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return tarefa;
    }

    public Tarefa findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Tarefa tarefa = null;
        try {
            tarefa = em.find(Tarefa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tarefa;
    }
    
    public List<Tarefa> findAll(boolean exibirExcluidos) {
        return findByCriteria(null, null, null, false);
    }
    
    
    
    public List<Tarefa> findByCriteria(String descricao, Optional<Boolean> tarefaConcluida, Funcionario funcionario, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Tarefa> listTarefa = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Tarefa> q = cb.createQuery(Tarefa.class);
            Root<Tarefa> rootTarefa = q.from(Tarefa.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
           
            if(descricao != null){
                //achar por partes diversas
                descricao = descricao.replaceAll(" ", "%");
                predicates.add(
                                cb.like(rootTarefa.get("descricao"), "%"+descricao+"%")
                );
            }
            
            if (tarefaConcluida != null && tarefaConcluida.isPresent()) {
                if (tarefaConcluida.get()) {
                    predicates.add(cb.isNotNull(rootTarefa.get("conclusao")));

                } else {
                    predicates.add(cb.isNull(rootTarefa.get("conclusao")));

                }
            }
            
            if (funcionario != null) {
                if (funcionario.getId() > 0) { //0 = todos
                    predicates.add(cb.equal(rootTarefa.get("funcionario"), funcionario));

                } else if (funcionario.getId() == -1) { //sem funcionário
                    predicates.add(cb.isNull(rootTarefa.get("funcionario")));

                }
            }
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootTarefa.get("exclusao")));
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootTarefa.get("data")));
            o.add(cb.asc(rootTarefa.get("hora")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootTarefa).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Tarefa> query = em.createQuery(q);
            
            
            listTarefa = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return listTarefa;
    }
    
    
    public Tarefa delete(Tarefa tarefa) {
        tarefa.setExclusao(LocalDateTime.now());

        return save(tarefa);
    }
}
