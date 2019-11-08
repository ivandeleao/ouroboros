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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.Veiculo;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class VeiculoDAO {
    public Veiculo save(Veiculo veiculo){
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return veiculo;
    }

    public Veiculo findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Veiculo veiculo = null;
        
        try {
            veiculo = em.find(Veiculo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return veiculo;
    }
    
    public Veiculo findByPlaca(String placa){
        if(findByCriteria(placa, null, false).isEmpty()) {
            return null;
        }
        return findByCriteria(placa, null, false).get(0);
    }
    
    public List<Veiculo> findByPlacaOuModelo(String placaOuModelo){
        return findByCriteria(placaOuModelo, placaOuModelo, false);
    }
    
    public List<Veiculo> findAll(boolean exibirExcluidos) {
        return findByCriteria(null, null, false);
    }
    
    
    
    public List<Veiculo> findByCriteria(String placa, String modelo, boolean exibirExcluidos){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Veiculo> listVeiculo = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Veiculo> q = cb.createQuery(Veiculo.class);
            Root<Veiculo> rootVeiculo = q.from(Veiculo.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (placa != null) {
                //achar por partes diversas
                placa = placa.replaceAll(" ", "%").replaceAll("-", "%");
                predicates.add(
                        cb.or(
                                cb.like(rootVeiculo.get("placa"), "%"+placa+"%")
                        )
                );
            }
            if (modelo != null) {
                //achar por partes diversas
                modelo = modelo.replaceAll(" ", "%");
                predicates.add(
                        cb.or(
                                cb.like(rootVeiculo.get("modelo"), "%"+modelo+"%")
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
            q.select(rootVeiculo).where(cb.or(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Veiculo> query = em.createQuery(q);
            
            
            listVeiculo = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return listVeiculo;
    }
    
    
    public Veiculo delete(Veiculo veiculo) {
        veiculo.setExclusao(LocalDateTime.now());

        return save(veiculo);
    }
}
