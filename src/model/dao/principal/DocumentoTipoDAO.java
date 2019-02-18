/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.bean.principal.DocumentoTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class DocumentoTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<DocumentoTipo> documentoTipos = new ArrayList<>();
        documentoTipos.add(DocumentoTipo.ENTRADA);
        documentoTipos.add(DocumentoTipo.SAIDA);
        
        
        em.getTransaction().begin();
        for(DocumentoTipo documentoTipo : documentoTipos){
            if(findById(documentoTipo.getId()) == null){
                em.persist(documentoTipo);
            } else {
                em.merge(documentoTipo);
            }
        }
        em.getTransaction().commit();

    }
    
    public DocumentoTipo save(DocumentoTipo documentoTipo) {
        try {
            em.getTransaction().begin();
            if (documentoTipo.getId() == null) {
                em.persist(documentoTipo);
            } else {
                em.merge(documentoTipo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return documentoTipo;
    }

    public DocumentoTipo findById(Integer id) {
        DocumentoTipo documentoTipo = null;
        try {
            documentoTipo = em.find(DocumentoTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return documentoTipo;
    }
    
    public List<DocumentoTipo> findAll() {
        List<DocumentoTipo> documentoTipos = null;
        try {
            Query query = em.createQuery("from DocumentoTipo documentoTipo order by id");

            documentoTipos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return documentoTipos;
    }
    
    
}
