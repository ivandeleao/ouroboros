/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

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
import model.mysql.bean.endereco.Cidade;
import model.mysql.bean.endereco.Estado;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.ParcelaStatus;
import model.mysql.bean.principal.pessoa.Perfil;
import model.mysql.bean.principal.documento.Venda;
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

    public List<Parcela> findByCriteria(Pessoa cliente, Timestamp dataInicial, Timestamp dataFinal, TipoOperacao tipoOperacao) {
        List<Parcela> parcelas = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Parcela> cq = cb.createQuery(Parcela.class);
            Root<Parcela> rootParcela = cq.from(Parcela.class);

            Join<Parcela, Venda> rootJoin = rootParcela.join("venda", JoinType.LEFT);
            cq.multiselect(rootParcela, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(
                    cb.isFalse(rootJoin.get("orcamento")),
                    cb.isNull(rootJoin.get("orcamento")))
            );

            predicates.add(cb.isNull(rootJoin.get("cancelamento")));

            if (cliente != null) {
                predicates.add(cb.equal(rootJoin.get("cliente"), cliente));
            } else {
                predicates.add(cb.isNotNull(rootJoin.get("cliente")));
            }

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataFinal));
            }

            //if (tipoOperacao != null) {
            predicates.add(cb.equal(rootJoin.get("tipoOperacao"), tipoOperacao));
            //}

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(rootParcela.get("vencimento")));

            cq.select(rootParcela).where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Parcela> query = em.createQuery(cq);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            //parcelas = query.getResultList();
            parcelas.addAll(query.getResultList());
        } catch (Exception e) {
            System.err.println(e);
        }
        return parcelas;
    }

    public List<Parcela> findPorData(Timestamp dataInicial, Timestamp dataFinal, TipoOperacao tipoOperacao) {
        List<Parcela> parcelas = findByCriteria(null, dataInicial, dataFinal, tipoOperacao);
        List<Parcela> parcelasEmAberto = new ArrayList<>();
        for (Parcela p : parcelas) {
            if (p.getVencimento() != null) {
                parcelasEmAberto.add(p);
            }
        }
        parcelasEmAberto.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelasEmAberto;
    }

    public List<Parcela> findPorStatus(Pessoa cliente, List<ParcelaStatus> listStatus, Timestamp dataInicial, Timestamp dataFinal, TipoOperacao tipoOperacao) {
        List<Parcela> parcelas = findByCriteria(cliente, dataInicial, dataFinal, tipoOperacao);
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

    public Parcela findUltimaPorPerfil(Perfil perfil) {
        Parcela parcela = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Parcela> cq = cb.createQuery(Parcela.class);
            Root<Parcela> rootParcela = cq.from(Parcela.class);

            Join<Parcela, Venda> rootJoinVenda = rootParcela.join("venda", JoinType.LEFT);
            Join<Venda, Pessoa> rootJoinPessoa = rootJoinVenda.join("cliente", JoinType.LEFT);
            Join<Pessoa, Perfil> rootJoinPerfil = rootJoinPessoa.join("perfis", JoinType.LEFT);

            cq.multiselect(rootParcela, rootJoinVenda);//, rootJoinPessoa);//, rootJoinPerfil);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(cb.isFalse(rootJoinVenda.get("orcamento")),
                    cb.isNull(rootJoinVenda.get("orcamento")))
            );

            predicates.add(cb.isNull(rootJoinVenda.get("cancelamento")));

            predicates.add(cb.equal(rootJoinVenda.get("cliente"), perfil.getPessoa()));

            predicates.add(cb.equal(rootJoinPerfil.get("grupo"), perfil.getGrupo()));

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(rootParcela.get("id"))); //2019-04-19 - era vencimento

            cq.select(rootParcela).where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Parcela> query = em.createQuery(cq);

            query.setMaxResults(1);
            
            try {
                parcela = query.getSingleResult();
            } catch(Exception e) {
                //nothing to do
            }

        } catch (Exception e) {
            System.err.println("Erro em findUltimaPorPerfil: " + e);
        }
        return parcela;
    }

}
