/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.endereco.Cidade;
import model.bean.endereco.Estado;
import model.bean.principal.CaixaItem;
import model.bean.principal.Pessoa;
import model.bean.principal.Parcela;
import model.bean.principal.ParcelaStatus;
import model.bean.principal.Venda;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ParcelaDAO {

    public Parcela save(Parcela parcela) {
        try {
            em.getTransaction().begin();
            if (parcela.getId() == null) {
                em.persist(parcela);
            } else {
                em.merge(parcela);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return parcela;
    }

    public Parcela findById(Integer id) {
        Parcela parcela = null;
        try {
            parcela = em.find(Parcela.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return parcela;
    }

    public Parcela remove(Parcela parcela) {
        System.out.println("remover parcela: " + parcela.getId());
        try {
            parcela = em.find(Parcela.class, parcela.getId());
            em.getTransaction().begin();
            em.remove(parcela);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return parcela;
    }

    public List<Parcela> findByCriteria(Pessoa cliente, Timestamp dataInicial, Timestamp dataFinal) {
        List<Parcela> parcelas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Parcela> cq = cb.createQuery(Parcela.class);
            Root<Parcela> rootParcela = cq.from(Parcela.class);

            Join<Parcela, Venda> rootVenda = rootParcela.join("venda", JoinType.INNER);
            cq.multiselect(rootParcela, rootVenda);

            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.or(
                    cb.isFalse(rootVenda.get("orcamento")), 
                    cb.isNull(rootVenda.get("orcamento"))));

            if (cliente != null) {
                predicates.add(cb.equal(rootVenda.get("cliente"), cliente));
            } else {
                predicates.add(cb.isNotNull(rootVenda.get("cliente")));
            }

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataFinal));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(rootParcela.get("vencimento")));

            cq.select(rootParcela).where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Parcela> query = em.createQuery(cq);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            parcelas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return parcelas;
    }

    public List<Parcela> findPorData(Timestamp dataInicial, Timestamp dataFinal) {
        List<Parcela> parcelas = findByCriteria(null, dataInicial, dataFinal);
        List<Parcela> parcelasEmAberto = new ArrayList<>();
        for (Parcela p : parcelas) {
            if (p.getVencimento() != null) {
                parcelasEmAberto.add(p);
            }
        }
        parcelasEmAberto.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelasEmAberto;
    }

    public List<Parcela> findPorStatus(Pessoa cliente, List<ParcelaStatus> listStatus, Timestamp dataInicial, Timestamp dataFinal) {
        List<Parcela> parcelas = findByCriteria(cliente, dataInicial, dataFinal);
        List<Parcela> parcelasEmAberto = new ArrayList<>();
        for (Parcela p : parcelas) {
            for (ParcelaStatus status : listStatus) {
                if (p.getVencimento() != null && p.getStatus() == status) {
                    parcelasEmAberto.add(p);
                }
            }
        }
        parcelasEmAberto.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelasEmAberto;
    }

}
