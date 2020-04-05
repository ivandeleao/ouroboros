/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.Diretiva;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class DiretivaDAO {
    public Diretiva save(Diretiva diretiva){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(diretiva.getId() == null){
                em.persist(diretiva);
            }else{
                em.merge(diretiva);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println( "Erro em diretiva.save " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return diretiva;
    }

    public Diretiva findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Diretiva diretiva = null;
        
        try {
            diretiva = em.find(Diretiva.class, id);
        } catch (Exception e) {
            System.err.println("Erro em diretiva.findById " + e);
        } finally {
            em.close();
        }
        
        return diretiva;
    }
    
    
    public List<Diretiva> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Diretiva> listDiretiva = null;
        try {
            Query query = em.createQuery("from " + Diretiva.class.getSimpleName() + " d");
            listDiretiva = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em diretiva.findAll " + e);
        } finally {
            em.close();
        }
        
        return listDiretiva;
    }
    
    public Diretiva remove(Diretiva diretiva) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        
        try {
            diretiva = em.find(Diretiva.class, diretiva.getId());
            em.getTransaction().begin();
            em.remove(diretiva);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
             em.close();
        }
        
        return diretiva;
    }
    
}
