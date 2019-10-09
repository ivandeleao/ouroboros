/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bootstrap.bean.nfe.CofinsBs;
import model.bootstrap.dao.nfe.CofinsBsDAO;
import model.mysql.bean.fiscal.Cofins;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CofinsDAO {

    public Cofins save(Cofins cofins) {
        try {
            em.getTransaction().begin();
            if (cofins.getCodigo() == null) {
                em.persist(cofins);
            } else {
                em.merge(cofins);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return cofins;
    }

    public Cofins findById(Integer id) {
        Cofins cofins = null;
        try {
            cofins = em.find(Cofins.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return cofins;
    }

    public List<Cofins> findAll() {
        List<Cofins> cofinsList = null;
        try {
            Query query = em.createQuery("from Cofins c order by codigo, id");

            cofinsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cofinsList;
    }
    
    public List<Cofins> listarSimplesNacional() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Cofins> q = cb.createQuery(Cofins.class);
            Root<Cofins> rootCofins = q.from(Cofins.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThan(rootCofins.get("codigo"), 100));

            q.select(rootCofins).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Cofins> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em cofins.findByCodigo " + e);
        }
        return null;
    }
    
    public List<Cofins> listarTributacaoNormal() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Cofins> q = cb.createQuery(Cofins.class);
            Root<Cofins> rootCofins = q.from(Cofins.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.greaterThan(rootCofins.get("codigo"), 100));

            q.select(rootCofins).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Cofins> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em cofins.findByCodigo " + e);
        }
        return null;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<CofinsBs> cofinsBsList = new CofinsBsDAO().findAll();
        

        em.getTransaction().begin();
        for (CofinsBs cofinsBs : cofinsBsList) {
            Cofins cofins = new Cofins(cofinsBs.getId(), cofinsBs.getCodigo(), cofinsBs.getDescricao());
            if (findById(cofinsBs.getId()) == null) {
                em.persist(cofins);
            } else {
                em.merge(cofins);
            }
        }
        
        
        em.getTransaction().commit();

    }
}
