/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

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
import model.mysql.bean.fiscal.Ncm;
import model.bootstrap.bean.NcmBs;
import model.bootstrap.dao.NcmBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class NcmDAO {

    public Ncm save(Ncm ncm) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (ncm.getCodigo() == null) {
                em.persist(ncm);
            } else {
                em.merge(ncm);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return ncm;
    }

    
    public Ncm findByCodigo(String codigo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Ncm ncm = null;
        try {
            ncm = em.find(Ncm.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ncm;
    }

    public List<Ncm> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();List<Ncm> ncmList = null;
        try {
            Query query = em.createQuery("from Ncm n order by codigo");

            ncmList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ncmList;
    }
    
    public List<Ncm> findAllByCodigo(String descricao){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Ncm> listNcm = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Ncm> q = cb.createQuery(Ncm.class);
            Root<Ncm> rootNcm = q.from(Ncm.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(descricao != null){
                predicates.add(cb.like(rootNcm.get("descricao"), "%"+descricao+"%"));
            }
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootNcm.get("descricao")));
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootNcm).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);
            
            TypedQuery<Ncm> query = em.createQuery(q);
            
            listNcm = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return listNcm;
    }
    
    public Ncm remove(Ncm ncm) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            ncm = em.find(Ncm.class, ncm.getCodigo());
            em.getTransaction().begin();
            em.remove(ncm);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return ncm;
    }
    
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<NcmBs> ncmBsList = new NcmBsDAO().findAll();

  
        em.getTransaction().begin();
        for (NcmBs ncmBs : ncmBsList) {
            Ncm ncm = new Ncm(ncmBs.getCodigo(), ncmBs.getDescricao(), ncmBs.getIpi(), null, null);
            if (findByCodigo(ncmBs.getCodigo()) == null) {
                em.persist(ncm);
            }else{
                em.merge(ncm);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }
}
