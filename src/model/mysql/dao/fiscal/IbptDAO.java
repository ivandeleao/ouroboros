/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.Ibpt;
import model.bootstrap.bean.IbptBs;
import model.bootstrap.dao.IbptBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class IbptDAO {

    public Ibpt save(Ibpt ibpt) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (ibpt.getCodigo() == null) {
                em.persist(ibpt);
            } else {
                em.merge(ibpt);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return ibpt;
    }

    
    public Ibpt findByCodigo(String strCodigo) {
        Integer codigo = Integer.parseInt(strCodigo);
        return findByCodigo(codigo);
    }
    
    public Ibpt findByCodigo(Integer codigo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Ibpt ibpt = null;
        try {
            ibpt = em.find(Ibpt.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ibpt;
    }

    public List<Ibpt> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Ibpt> ibptList = null;
        try {
            Query query = em.createQuery("from Ibpt i order by codigo");

            ibptList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return ibptList;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<IbptBs> ibptBsList = new IbptBsDAO().findAll();

        //try{
            em.getTransaction().begin();
            for (IbptBs ibptBs : ibptBsList.stream().distinct().collect(Collectors.toList())) { //2019-12-06 estava reclamando de entidade duplicada
                Ibpt ibpt = new Ibpt(ibptBs.getCodigo(), ibptBs.getEx(), ibptBs.getAliqNac(), ibptBs.getAliqImp(), ibptBs.getAliqEst());
                if (findByCodigo(ibptBs.getCodigo()) == null) {
                    em.persist(ibpt);
                } else {
                    em.merge(ibpt);
                }
            }
            em.getTransaction().commit();
            
        /*} catch (Exception e) {
            //do nothing
        } finally {
            em.close();
        }*/
    }

}
