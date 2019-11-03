/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.documento.OSTransporte;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class OSTransporteDAO {

    public OSTransporte save(OSTransporte osTransporte) {
        try {
            em.getTransaction().begin();
            
            if (osTransporte.getId() == null) {
                em.persist(osTransporte);
            } else {
                em.merge(osTransporte);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em OSTransporte.save" + e);
            em.getTransaction().rollback();
        }

        return osTransporte;
    }

    public OSTransporte findById(Integer id) {
        OSTransporte osTransporte = null;
        try {
            osTransporte = em.find(OSTransporte.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return osTransporte;
    }

    public List<OSTransporte> findAll() {
        List<OSTransporte> osTransportes = null;
        try {
            Query query = em.createQuery("from OSTransporte ost order by criacao desc");

            osTransportes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return osTransportes;
    }

    public List<OSTransporte> getComandasAbertas() {
        List<OSTransporte> osTransportes = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<OSTransporte> q = cb.createQuery(OSTransporte.class);
            Root<OSTransporte> osTransporte = q.from(OSTransporte.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(osTransporte.get("cancelamento")));

            q.select(osTransporte).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<OSTransporte> query = em.createQuery(q);

            osTransportes = query.getResultList();

        } catch (Exception e) {
            System.err.println(e);
            //do nothing
        }

        return osTransportes;
    }

}
