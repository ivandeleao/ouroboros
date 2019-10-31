/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import model.mysql.dao.fiscal.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.nfe.DocumentoReferenciado;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class DocumentoReferenciadoDAO {
    public DocumentoReferenciado save(DocumentoReferenciado documentoReferenciado){
        try {
            em.getTransaction().begin();
            if(documentoReferenciado.getId() == null){
                em.persist(documentoReferenciado);
            }else{
                em.merge(documentoReferenciado);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println( "Erro em DocumentoReferenciado.save " + e);
            em.getTransaction().rollback();
        }
        
        return documentoReferenciado;
    }

    public DocumentoReferenciado findById(Integer id){
        DocumentoReferenciado documentoReferenciado = null;
        
        try {
            documentoReferenciado = em.find(DocumentoReferenciado.class, id);
        } catch (Exception e) {
            System.err.println("Erro em DocumentoReferenciado.findById " + e);
        }
        
        return documentoReferenciado;
    }
    
    public DocumentoReferenciado findByChave(String chave){
        DocumentoReferenciado documentoReferenciado = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<DocumentoReferenciado> q = cb.createQuery(DocumentoReferenciado.class);
            Root<DocumentoReferenciado> rootDocumentoReferenciado = q.from(DocumentoReferenciado.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.like(rootDocumentoReferenciado.get("chave"), chave));
            
            q.select(rootDocumentoReferenciado).where(predicates.toArray(new Predicate[]{}));
            
            TypedQuery<DocumentoReferenciado> query = em.createQuery(q);
            
            documentoReferenciado = query.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
        }
        return documentoReferenciado;
    }
    
    
    public List<DocumentoReferenciado> findAll(){
        List<DocumentoReferenciado> satCupons = null;
        try {
            Query query = em.createQuery("from " + DocumentoReferenciado.class.getSimpleName() + " c");
            satCupons = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em documentoReferenciado.findAll " + e);
        }
        return satCupons;
    }
    
    
    
    
    
    
}
