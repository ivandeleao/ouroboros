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
import model.mysql.bean.fiscal.MeioDePagamento;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class MeioDePagamentoDAO {

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<MeioDePagamento> mps = new ArrayList<>();
        //Especificacao_SAT_v_ER_2_24_04.pdf - página 99
        mps.add(MeioDePagamento.DINHEIRO);
        mps.add(MeioDePagamento.CHEQUE);
        mps.add(MeioDePagamento.CARTAO_DE_CREDITO);
        mps.add(MeioDePagamento.CARTAO_DE_DEBITO);
        mps.add(MeioDePagamento.CREDITO_LOJA);
        mps.add(MeioDePagamento.VALE_ALIMENTACAO);
        mps.add(MeioDePagamento.VALE_REFEICAO);
        mps.add(MeioDePagamento.VALE_PRESENTE);
        mps.add(MeioDePagamento.VALE_COMBUSTIVEL);
        mps.add(MeioDePagamento.BOLETO_BANCARIO);
        mps.add(MeioDePagamento.SEM_PAGAMENTO);
        mps.add(MeioDePagamento.OUTROS);
        
        em.getTransaction().begin();
        for(MeioDePagamento mp : mps){
            if(findByCodigoSAT(mp.getCodigoSAT()) == null){
                em.persist(mp);
            } else {
                em.merge(mp);
            }
        }
        em.getTransaction().commit();

    }

    public MeioDePagamento save(MeioDePagamento meioDePagamento) {
        try {
            em.getTransaction().begin();
            if (meioDePagamento.getId() == null) {
                em.persist(meioDePagamento);
            } else {
                em.merge(meioDePagamento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return meioDePagamento;
    }

    public MeioDePagamento findById(Integer id) {
        MeioDePagamento meioDePagamento = null;
        try {
            meioDePagamento = em.find(MeioDePagamento.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return meioDePagamento;
    }

    public List<MeioDePagamento> findAll() {
        List<MeioDePagamento> mps = null;
        try {
            Query query = em.createQuery("from MeioDePagamento mp order by ordem");

            mps = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return mps;
    }
    
    public List<MeioDePagamento> findAllEnabled() {
        List<MeioDePagamento> mps = null;
        try {
            Query query = em.createQuery("from MeioDePagamento mp where habilitado = true order by ordem");

            mps = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return mps;
    }

    public MeioDePagamento findByCodigoSAT(String codigoSAT) {
        MeioDePagamento mp = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MeioDePagamento> q = cb.createQuery(MeioDePagamento.class);
            Root<MeioDePagamento> mpRoot = q.from(MeioDePagamento.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(mpRoot.get("codigoSAT"), codigoSAT));

            q.select(mpRoot).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<MeioDePagamento> query = em.createQuery(q);

            mp = query.getSingleResult();
            return mp;
        } catch (NoResultException e) {
            //do nothing!
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return mp;

    }

}
