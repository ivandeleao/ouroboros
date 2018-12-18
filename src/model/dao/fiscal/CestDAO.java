/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.fiscal.Cest;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class CestDAO {
    
    public List<Cest> findByCodigoNcm(String codigoNcm){
        return findByCriteria(null, codigoNcm, null);
    }
    
    public List<Cest> findByCriteria(String codigoCest, String codigoNcm, String descricao){
        List<Cest> listCest = null;
        try {
            CriteriaBuilder cb = emBs.getCriteriaBuilder();
            
            CriteriaQuery<Cest> q = cb.createQuery(Cest.class);
            Root<Cest> rootCest = q.from(Cest.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(codigoCest != null) {
                predicates.add(cb.like(rootCest.get("codigo"), "%"+codigoCest+"%"));
            }
            
            if(codigoNcm != null) {
                predicates.add(cb.like(rootCest.get("ncm"), "%"+codigoNcm+"%"));
            }
            
            if(descricao != null) {
                predicates.add(cb.like(rootCest.get("descricao"), "%"+descricao+"%"));
            }
            
            q.select(rootCest).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            TypedQuery<Cest> query = emBs.createQuery(q);
            
            listCest = query.getResultList();
            
        } catch (Exception e) {
            System.err.println(e);
        }
        return listCest;
    }
    
    
}
