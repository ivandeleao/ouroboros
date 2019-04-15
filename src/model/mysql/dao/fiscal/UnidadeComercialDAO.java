/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import connection.ConnectionFactory;
import java.util.List;
import javax.persistence.EntityManager;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.bootstrap.bean.UnidadeComercialBs;
import model.bootstrap.dao.UnidadeComercialBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class UnidadeComercialDAO {
    
    public UnidadeComercial findById(Integer id) {
        UnidadeComercial unidadeComercial = null;
        try {
            unidadeComercial = em.find(UnidadeComercial.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return unidadeComercial;
    }
    
    public List<UnidadeComercial> findAll(){
        List<UnidadeComercial> unidades = null;
        try {
            unidades = em.createQuery("from UnidadeComercial uC").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return unidades;
    }
    
    public void bootstrap() {
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
    }
}
