/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bootstrap.bean.nfe.IpiBs;
import model.bootstrap.dao.nfe.IpiBsDAO;
import model.mysql.bean.fiscal.Ipi;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class IpiDAO {

    public Ipi save(Ipi ipi) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (ipi.getCodigo() == null) {
                em.persist(ipi);
            } else {
                em.merge(ipi);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return ipi;
    }

    public Ipi findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Ipi ipi = null;
        try {
            ipi = em.find(Ipi.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ipi;
    }

    public List<Ipi> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Ipi> ipiList = null;
        try {
            Query query = em.createQuery("from Ipi p order by codigo, id");

            ipiList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ipiList;
    }
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<IpiBs> ipiBsList = new IpiBsDAO().findAll();
        

        em.getTransaction().begin();
        for (IpiBs ipiBs : ipiBsList) {
            Ipi ipi = new Ipi(ipiBs.getId(), ipiBs.getCodigo(), ipiBs.getDescricao());
            if (findById(ipiBs.getId()) == null) {
                em.persist(ipi);
            } else {
                em.merge(ipi);
            }
        }
        
        
        em.getTransaction().commit();

        em.close();
    }
}
