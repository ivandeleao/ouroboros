/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class MovimentoFisicoTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<MovimentoFisicoTipo> mfts = new ArrayList<>();
        mfts.add(MovimentoFisicoTipo.LANCAMENTO_MANUAL);
        mfts.add(MovimentoFisicoTipo.VENDA);
        mfts.add(MovimentoFisicoTipo.EXCLUSAO_VENDA);
        mfts.add(MovimentoFisicoTipo.COMPRA);
        mfts.add(MovimentoFisicoTipo.EXCLUSAO_COMPRA);
        mfts.add(MovimentoFisicoTipo.ALUGUEL);
        mfts.add(MovimentoFisicoTipo.DEVOLUCAO_ALUGUEL);
        
        
        em.getTransaction().begin();
        for(MovimentoFisicoTipo mft : mfts){
            if(findById(mft.getId()) == null){
                em.persist(mft);
            } else {
                em.merge(mft);
            }
        }
        em.getTransaction().commit();

    }
    
    public MovimentoFisicoTipo save(MovimentoFisicoTipo movimentoFisicoTipo) {
        try {
            em.getTransaction().begin();
            if (movimentoFisicoTipo.getId() == null) {
                em.persist(movimentoFisicoTipo);
            } else {
                em.merge(movimentoFisicoTipo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return movimentoFisicoTipo;
    }

    public MovimentoFisicoTipo findById(Integer id) {
        MovimentoFisicoTipo movimentoFisicoTipo = null;
        try {
            movimentoFisicoTipo = em.find(MovimentoFisicoTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return movimentoFisicoTipo;
    }
    
    public List<MovimentoFisicoTipo> findAll() {
        List<MovimentoFisicoTipo> mfts = null;
        try {
            Query query = em.createQuery("from MovimentoFisicoTipo mft order by id");

            mfts = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return mfts;
    }
    
    public List<MovimentoFisicoTipo> findAllEnabled() {
        List<MovimentoFisicoTipo> mfts = null;
        try {
            Query query = em.createQuery("from MovimentoFisicoTipo mft where habilitado = true order by id");

            mfts = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return mfts;
    }
}
