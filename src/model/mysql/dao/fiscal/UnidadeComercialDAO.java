/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.List;
import javax.persistence.EntityManager;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.bootstrap.bean.UnidadeComercialBs;
import model.bootstrap.dao.UnidadeComercialBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class UnidadeComercialDAO {
    
    public UnidadeComercial findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        UnidadeComercial unidadeComercial = null;
        try {
            unidadeComercial = em.find(UnidadeComercial.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return unidadeComercial;
    }
    
    public List<UnidadeComercial> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<UnidadeComercial> unidades = null;
        try {
            unidades = em.createQuery("from UnidadeComercial uC").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return unidades;
    }
    
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<UnidadeComercialBs> unidadeComercialBsList = new UnidadeComercialBsDAO().findAll();

        em.getTransaction().begin();
        for (UnidadeComercialBs unidadeComercialBs : unidadeComercialBsList) {
            System.out.println("unidadeComercialBs: " + unidadeComercialBs.getDescricao());
            UnidadeComercial unidadeComercial = new UnidadeComercial(unidadeComercialBs.getId(), unidadeComercialBs.getNome(), unidadeComercialBs.getDescricao());
            if (findById(unidadeComercialBs.getId()) == null) {
                em.persist(unidadeComercial);
            }else{
                em.merge(unidadeComercial);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }
}
