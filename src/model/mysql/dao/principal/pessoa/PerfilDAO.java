/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.pessoa;

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
import model.mysql.bean.principal.pessoa.Perfil;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class PerfilDAO {
    public Perfil save(Perfil perfil){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(perfil.getId() == null){
                em.persist(perfil);
            }else{
                em.merge(perfil);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return perfil;
    }

    public Perfil findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Perfil perfil = null;
        
        try {
            perfil = em.find(Perfil.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return perfil;
    }
    
    public Perfil findByChaveComposta(Perfil perfil) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Perfil> q = cb.createQuery(Perfil.class);
            Root<Perfil> rootPerfil = q.from(Perfil.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootPerfil.get("pessoa"), perfil.getPessoa()));
            predicates.add(cb.equal(rootPerfil.get("grupo"), perfil.getGrupo()));
            
            q.select(rootPerfil).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Perfil> query = em.createQuery(q);
            //talvez tenha que limitar o resultado para 1
            query.setMaxResults(1);

            //System.out.println("query result: " + query.getSingleResult());

            if (query.getSingleResult() != null) {

                return query.getSingleResult();
            }

        } catch (NoResultException e) {
            //that's ok!
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return null;
    }
    
    
    
    public List<Perfil> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Perfil> listPerfil = null;
        try {
            Query query = em.createQuery("from " + Perfil.class.getSimpleName() + " p");
            query.setMaxResults(50);
            listPerfil = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return listPerfil;
    }
    
    
    public Perfil remove(Perfil perfil) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            perfil = em.find(Perfil.class, perfil.getId());
            System.out.println("remove find: " + perfil.getId());
            em.getTransaction().begin();
            em.remove(perfil);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return perfil;
    }
}
