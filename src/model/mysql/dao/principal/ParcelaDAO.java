/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import model.nosql.FinanceiroStatusEnum;
import model.mysql.bean.principal.pessoa.Perfil;
import model.mysql.bean.principal.documento.Venda;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ParcelaDAO {

    public Parcela save(Parcela parcela) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }

        return parcela;
    }

    public Parcela findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Parcela parcela = null;
        try {
            parcela = em.find(Parcela.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return parcela;
    }

    public Parcela remove(Parcela parcela) {
        EntityManager em = CONNECTION_FACTORY.getConnection();

        try {
            parcela = em.find(Parcela.class, parcela.getId());
            em.getTransaction().begin();
            em.remove(parcela);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return parcela;
    }

    public List<Parcela> findByCriteria(Pessoa cliente, LocalDate dataInicial, LocalDate dataFinal, TipoOperacao tipoOperacao, Optional<Boolean> isCartao, MeioDePagamento mp, Optional<Boolean> boletoImpressao, Optional<Boolean> boletoRemessa) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
                if (!(isCartao != null && isCartao.isPresent())) {
                    predicates.add(cb.isNotNull(rootJoin.get("cliente")));
                }
            }

            //2020-06-17 exibir as parcelas à vista
            //predicates.add(cb.isNotNull(rootParcela.get("vencimento"))); //2020-05-28
            
            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootParcela.get("vencimento"), (Comparable) dataFinal));
            }

            //if (tipoOperacao != null) {
            predicates.add(cb.equal(rootJoin.get("tipoOperacao"), tipoOperacao));
            //}

            if (isCartao != null && isCartao.isPresent()) {
                if (isCartao.get()) {
                    predicates.add(cb.isNotNull(rootParcela.get("cartaoTaxa")));
                } else {
                    predicates.add(cb.isNull(rootParcela.get("cartaoTaxa")));
                }
            }

            if (mp != null) {
                predicates.add(cb.equal(rootParcela.get("meioDePagamento"), mp));
            }

            if (boletoImpressao != null && boletoImpressao.isPresent()) {
                if (boletoImpressao.get()) {
                    predicates.add(cb.isNotNull(rootParcela.get("boletoImpressao")));
                } else {
                    predicates.add(cb.isNull(rootParcela.get("boletoImpressao")));
                }
            }

            if (boletoRemessa != null && boletoRemessa.isPresent()) {
                predicates.add(boletoRemessa.get()
                        ? cb.isNotNull(rootParcela.get("boletoRemessa"))
                        : cb.isNull(rootParcela.get("boletoRemessa")));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootParcela.get("vencimento")));

            cq.select(rootParcela).where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Parcela> query = em.createQuery(cq);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            //parcelas = query.getResultList();
            parcelas.addAll(query.getResultList());
        } catch (Exception e) {
            System.err.println("Erro em ParcelaDAO.findByCriteria: " + e);
        } finally {
            em.close();
        }

        return parcelas;
    }

    public List<Parcela> findPorData(LocalDate dataInicial, LocalDate dataFinal, TipoOperacao tipoOperacao, Optional<Boolean> isCartao) {
        List<Parcela> parcelas = findByCriteria(null, dataInicial, dataFinal, tipoOperacao, isCartao, null, null, null);
        /*
        2020-05-28 coloquei o filtro no findByCriteria
        List<Parcela> parcelasEmAberto = new ArrayList<>();
        for (Parcela p : parcelas) {
            if (p.getVencimento() != null) {
                parcelasEmAberto.add(p);
            }
        }
        parcelasEmAberto.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelasEmAberto;*/
        
        parcelas.sort(Comparator.comparing(Parcela::getVencimento));
        return parcelas;
    }

    public List<Parcela> findPorStatus(Pessoa cliente, List<FinanceiroStatusEnum> listStatus, LocalDate dataInicial, LocalDate dataFinal, TipoOperacao tipoOperacao, Optional<Boolean> isCartao) {
        List<Parcela> parcelas = findByCriteria(cliente, dataInicial, dataFinal, tipoOperacao, isCartao, null, null, null);
        List<Parcela> parcelasEmAberto = new ArrayList<>();
        parcelas.forEach((p) -> {
            //listStatus.stream().filter((status) -> (p.getVencimento() != null && p.getStatus() == status)).forEachOrdered((_item) -> {
            //2020-06-17 exibir as parcelas à vista
            listStatus.stream().filter((status) -> (p.getStatus() == status)).forEachOrdered((_item) -> {
                parcelasEmAberto.add(p);
            });
        });
        
        //2020-05-22 - reescrevi, mas parece ter o mesmo desempenho que o trecho acima
        /*parcelasEmAberto = parcelas.stream()
                .filter(p -> p.getVencimento() != null)
                .filter(p -> listStatus.stream().anyMatch( s -> s.equals(p.getStatus()) )).collect(Collectors.toList());
        */
        
        
        
        return parcelasEmAberto;
    }

    public Parcela findUltimaPorPerfil(Perfil perfil) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
            } catch (Exception e) {
                //nothing to do
            }

        } catch (Exception e) {
            System.err.println("Erro em findUltimaPorPerfil: " + e);
        } finally {
            em.close();
        }

        return parcela;
    }

}
