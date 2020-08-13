/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.dao.principal.ParcelaDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CaixaItemDAO {

    public CaixaItem save(CaixaItem caixaItem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (caixaItem.getId() == null) {

                if (caixaItem.getConta() != null && caixaItem.getConta().getData().compareTo(LocalDate.now()) != 0) {
                    caixaItem.setDataHora(LocalDateTime.of(caixaItem.getConta().getData(), LocalTime.MIN));
                }
                
                //2020-07-09 sempre alimentar a data de recebimento para facilitar nas consultas
                if (caixaItem.getDataHoraRecebimento() == null) {
                    caixaItem.setDataHoraRecebimento(caixaItem.getDataHora());
                }

                em.persist(caixaItem);
            } else {
                em.merge(caixaItem);
            }

            em.merge(calcularSaldo(caixaItem));

            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em CaixaItemDAO.save() " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        //alimentar campo de cache do saldo da conta
        if (caixaItem.getCaixa() != null) {
            //caixaItem.getCaixa().getConta().setSaldo();
            caixaItem.getCaixa().getConta().setSaldo(caixaItem.getSaldoAcumulado()); //2020-05-22
            new ContaDAO().save(caixaItem.getCaixa().getConta());
        } else {
            //if (caixaItem.getConta() != null) {
            //caixaItem.getConta().setSaldo();

            System.out.println("descricao: " + caixaItem.getDescricao());
            System.out.println("saldo acumulado: " + caixaItem.getSaldoAcumulado());

            caixaItem.getConta().setSaldo(caixaItem.getSaldoAcumulado()); //2020-05-22
            new ContaDAO().save(caixaItem.getConta());
        }

        return caixaItem;
    }

    private CaixaItem calcularSaldo(CaixaItem caixaItem) {
        BigDecimal saldoAnterior = getSaldoAnterior(caixaItem);
        caixaItem.setSaldoAcumulado(saldoAnterior.add(caixaItem.getCredito()).subtract(caixaItem.getDebito()));

        return caixaItem;
    }

    public CaixaItem findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        CaixaItem caixaItem = null;
        try {
            caixaItem = em.find(CaixaItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return caixaItem;
    }

    public List<CaixaItem> findByCaixa(Caixa caixa, CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> caixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(caixaItem.get("caixa"), caixa));

            if (caixaItemTipo != null && caixaItemTipo.getId() > 0) {
                predicates.add(cb.equal(caixaItem.get("caixaItemTipo"), caixaItemTipo));
            }

            if (meioDePagamento != null && meioDePagamento.getId() > 0) {
                predicates.add(cb.equal(caixaItem.get("meioDePagamento"), meioDePagamento));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(caixaItem.get("id")));

            q.select(caixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);
            //query.setMaxResults(50);

            caixaItens = query.getResultList();

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return caixaItens;
    }

    public List<CaixaItem> findByCriteria(LocalDate dataInicial, LocalDate dataFinal, Conta conta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> caixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(caixaItem.get("criacao"), (Comparable) dataInicial.atStartOfDay()));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(caixaItem.get("criacao"), (Comparable) dataFinal.atTime(23, 59, 59)));
            }

            if (conta != null) {
                predicates.add(cb.equal(caixaItem.get("conta"), conta));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(caixaItem.get("id")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(caixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            caixaItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return caixaItens;
    }

    /**
     * Busca pela data de recebimento se informado e data do registro
     * posteriormente
     *
     * @param dataInicial
     * @param dataFinal
     * @param conta
     * @return
     */
    public List<CaixaItem> findByRecebimento(LocalDate dataInicial, LocalDate dataFinal, Conta conta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> rootCaixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootCaixaItem.get("dataHoraRecebimento"), (Comparable) dataInicial.atTime(LocalTime.MIN)));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootCaixaItem.get("dataHoraRecebimento"), (Comparable) dataFinal.atTime(LocalTime.MAX)));
            }

            if (conta != null) {
                predicates.add(cb.equal(rootCaixaItem.get("conta"), conta));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootCaixaItem.get("id")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootCaixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            caixaItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return caixaItens;
    }

    public CaixaItem getUltimaData(Conta conta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaItem> caixaItens = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<CaixaItem> q = cb.createQuery(CaixaItem.class);
            Root<CaixaItem> caixaItem = q.from(CaixaItem.class);

            List<Predicate> predicates = new ArrayList<>();

            if (conta != null) {
                predicates.add(cb.equal(caixaItem.get("conta"), conta));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(caixaItem.get("criacao")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(caixaItem).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<CaixaItem> query = em.createQuery(q);

            query.setMaxResults(1);

            caixaItens = query.getResultList();

            if (!caixaItens.isEmpty()) {
                return caixaItens.get(0);
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return null;
    }

    public BigDecimal getSaldoAnterior(CaixaItem caixaItem) {
        /*BigDecimal saldoAnterior = BigDecimal.ZERO; 2020-04-25 tentativa de melhora no desempenho - aparentemente não há diferença
        List<CaixaItem> caixaItens;
        
        if(caixaItem.getCaixa() != null) {
            System.out.println("caixa..");
            caixaItens = caixaItem.getCaixa().getCaixaItens();
        } else {
            System.out.println("conta..");
            caixaItens = caixaItem.getConta().getCaixaItens();
        }
        
        if (!caixaItens.isEmpty()) {
            System.out.println("tem itens");
            saldoAnterior = caixaItens.get(caixaItens.size() - 2).getSaldoAcumulado();
        }
        
        return saldoAnterior;*/

        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            //2019-12-03 Nova entidade Conta
            Query q;
            if (caixaItem.getCaixa() != null) {
                q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where id < :id and caixaId = :caixaId");
                q.setParameter("caixaId", caixaItem.getCaixa().getId());

            } else {
                q = em.createNativeQuery("select sum(credito - debito) as saldo from caixaItem where id < :id and contaId = :contaId");
                q.setParameter("contaId", caixaItem.getConta().getId());

            }

            q.setParameter("id", caixaItem.getId());

            if (q.getSingleResult() != null) {
                return (BigDecimal) q.getSingleResult();
            } else {
                return BigDecimal.ZERO;
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return null;
    }

    public CaixaItem estornar(CaixaItem itemEstornar) {
        //EntityManager em = CONNECTION_FACTORY.getConnection();
        Parcela parcela = itemEstornar.getParcela();
        if (parcela != null) {
            parcela.setDescontoPercentual(BigDecimal.ZERO);
            parcela.setAcrescimoMonetario(BigDecimal.ZERO);
            new ParcelaDAO().save(parcela);
        }

        CaixaItem estorno = itemEstornar.deepClone();
        //estorno.setCaixa(new CaixaDAO().getLastCaixa()); //2019-12-11
        estorno.setConta(itemEstornar.getConta()); //2019-12-11
        estorno.setId(null);
        estorno.setCredito(itemEstornar.getDebito());
        estorno.setDebito(itemEstornar.getCredito());
        estorno.setCaixaItemTipo(CaixaItemTipo.ESTORNO);
        estorno.setEstornoOrigem(itemEstornar);
        estorno.setDataHora(LocalDateTime.now()); //acertar a data - aparentemente não posso usar a clonagem
        estorno.setTranferenciaOrigem(null);

        save(itemEstornar);

        estorno = save(estorno);

        //itemEstornar.setEstornoId(estorno.getId());
        ////em.refresh(itemEstornar); 2019-12-04
        /*if(parcela != null) {
            em.refresh(parcela);
        }*/
        //em.close();
        return estorno;
    }

    /*public CaixaItem estornarDeCaixa(CaixaItem itemEstornar) { 2020-02-28 - não usado
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Parcela parcela = itemEstornar.getParcela();
        parcela.setDescontoPercentual(BigDecimal.ZERO);
        parcela.setAcrescimoMonetario(BigDecimal.ZERO);
        new ParcelaDAO().save(parcela);
        
        CaixaItem estorno = itemEstornar.deepClone();
        estorno.setCaixa(new CaixaDAO().getLastCaixa());
        estorno.setId(null);
        estorno.setCredito(itemEstornar.getDebito());
        estorno.setDebito(itemEstornar.getCredito());
        estorno.setCaixaItemTipo(CaixaItemTipo.ESTORNO);
        estorno.setEstornoOrigem(itemEstornar);

        save(estorno);
        
        //itemEstornar.setEstornoId(estorno.getId());
        itemEstornar = save(itemEstornar);
        em.refresh(itemEstornar);
        
        if(itemEstornar.getParcela() != null) {
            em.refresh(itemEstornar.getParcela());
        }
        em.close();
        
        return estorno;
    }*/
}
