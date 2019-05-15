/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.Veiculo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class VeiculoDAO {
    public Veiculo save(Veiculo veiculo){
        try {
            em.getTransaction().begin();
            if(veiculo.getId() == null){
                em.persist(veiculo);
            }else{
                em.merge(veiculo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        
        return veiculo;
    }

    public Veiculo findById(Integer id){
        Veiculo veiculo = null;
        
        try {
            veiculo = em.find(Veiculo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return veiculo;
    }
    
    public List<Veiculo> findByPlacaOuModelo(String placaOuModelo){
        return findByCriteria(placaOuModelo, false);
    }
    
    public List<Veiculo> findAll(boolean exibirExcluidos) {
        return findByCriteria(null, false);
    }
    
    
    
    public List<Veiculo> findByCriteria(String termo, boolean exibirExcluidos){
        
        List<Veiculo> listVeiculo = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Veiculo> q = cb.createQuery(Veiculo.class);
            Root<Veiculo> rootVeiculo = q.from(Veiculo.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (termo != null) {
                //achar por partes diversas do nome
                termo = termo.replaceAll(" ", "%");
                predicates.add(
                        cb.or(
                                cb.like(rootVeiculo.get("placa"), "%"+termo+"%"),
                                cb.like(rootVeiculo.get("modelo"), "%"+termo+"%")
                        )
                );
            }
            
            
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootVeiculo.get("exclusao")));
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootVeiculo.get("placa")));
            
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootVeiculo).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Veiculo> query = em.createQuery(q);
            
            
            listVeiculo = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listVeiculo;
    }
    
    
    public Veiculo delete(Veiculo veiculo) {
        veiculo.setExclusao(LocalDateTime.now());

        return save(veiculo);
    }
}
