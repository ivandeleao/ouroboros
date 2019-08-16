/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import model.mysql.dao.principal.catalogo.CategoriaDAO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.Funcionario;
import model.mysql.bean.principal.documento.VendaCategoriaConsolidado;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.Veiculo;
import model.mysql.bean.principal.documento.ComandaSnapshot;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.VendaItemConsolidado;
import model.mysql.bean.principal.pessoa.Pessoa;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;
import static ouroboros.Ouroboros.em;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class VendaDAO {

    public Venda save(Venda venda) {
        try {
            em.getTransaction().begin();
            if (venda.getId() == null) {
                em.persist(venda);
            } else {
                em.merge(venda);
            }
            //em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em venda.save" + e);
            em.getTransaction().rollback();
        }

        return venda;
    }

    public Venda findById(Integer id) {
        Venda venda = null;
        try {
            venda = em.find(Venda.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return venda;
    }

    public List<Venda> findAll() {
        //em = CONNECTION_FACTORY.getConnection();
        List<Venda> vendas = null;
        try {
            Query query = em.createQuery("from Venda v order by criacao desc");

            //query.setMaxResults(50);
            vendas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        //em.close();
        return vendas;
    }

    public List<Venda> getComandasAbertas() {
        List<Venda> vendas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);
            Root<Venda> venda = q.from(Venda.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNotNull(venda.get("comanda")));
            predicates.add(cb.isNull(venda.get("encerramento")));

            predicates.add(cb.isNull(venda.get("cancelamento")));

            q.select(venda).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Venda> query = em.createQuery(q);

            vendas = query.getResultList();

        } catch (Exception e) {
            System.err.println(e);
            //do nothing
        }

        return vendas;
    }

    public List<ComandaSnapshot> getComandasAbertasSnapshotBkp() {
        List<ComandaSnapshot> comandas = new ArrayList<>();
        for (Venda v : getComandasAbertas()) {
            ComandaSnapshot c = new ComandaSnapshot();
            c.setId(v.getId());
            c.setNumero(v.getComanda());
            c.setInicio(v.getCriacao());
            c.setItens(v.getMovimentosFisicosSaida().size());
            c.setValor(v.getTotal());

            comandas.add(c);
        }

        return comandas;
    }

    public List<ComandaSnapshot> getComandasAbertasSnapshot() {
        List<ComandaSnapshot> comandas = new ArrayList<>();
        System.out.println("teste 123...");
        //try {
        String sql = "select venda.id, venda.comanda as numero, venda.criacao as inicio, "
                + "count( if( movimentofisico.saida > 0, 1, null)) - count( if(movimentofisico.entrada > 0, 1, null)) as itens, "
                + "sum((movimentofisico.saida - movimentofisico.entrada) * movimentofisico.valor) as valor "
                + "from venda left join movimentofisico on venda.id = movimentofisico.vendaId "
                + "where comanda is not null and encerramento is null and cancelamento is null "
                + "group by venda.id";

        
        Query q = em.createNativeQuery(sql);

        List<Object[]> rows = q.getResultList();

        for (Object[] row : rows) {

            System.out.println("row: " + row[0]);
            ComandaSnapshot c = new ComandaSnapshot();
            c.setId(Integer.valueOf(row[0].toString()));
            c.setNumero(Integer.valueOf(row[1].toString()));
            c.setInicio(((Timestamp) row[2]).toLocalDateTime());
            c.setItens(Integer.valueOf(row[3].toString()));
            c.setValor((BigDecimal) row[4]);

            comandas.add(c);
        }

        return comandas;

        //} catch (Exception e) {
        //    System.err.println(e);
        //}
        //return null;
    }

    public Venda getComandaAberta(int comanda) {
        List<Venda> vendas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);
            Root<Venda> venda = q.from(Venda.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(venda.get("comanda"), (Comparable) comanda));
            predicates.add(cb.isNull(venda.get("encerramento")));
            predicates.add(cb.isNull(venda.get("cancelamento")));

            q.select(venda).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Venda> query = em.createQuery(q);
            //talvez tenha que limitar o resultado para 1
            query.setMaxResults(1);

            System.out.println("query result: " + query.getSingleResult());

            if (query.getSingleResult() != null) {

                return query.getSingleResult();
            }

        } catch (NoResultException e) {
            //that's ok!
        } catch (Exception e) {
            System.err.println(e);
        }

        return null;
    }

    public List<Venda> findByCriteria(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCanceladas, Optional<Boolean> satEmitido) {
        List<Venda> vendas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);

            Root<Venda> venda = q.from(Venda.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(venda.get("tipoOperacao"), tipoOperacao));

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(venda.get("criacao"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(venda.get("criacao"), (Comparable) dataFinal));
            }

            if (funcionario != null && funcionario.getId() > 0) {
                predicates.add(cb.equal(venda.get("funcionario"), funcionario));
            }

            if (pessoa != null && pessoa.getId() > 0) {
                predicates.add(cb.equal(venda.get("cliente"), pessoa));
            }

            if (veiculo != null && veiculo.getId() > 0) {
                predicates.add(cb.equal(venda.get("veiculo"), veiculo));
            }

            if (!exibirCanceladas) {
                predicates.add(cb.isNull(venda.get("cancelamento")));
            }

            if (satEmitido.isPresent()) {
                if (satEmitido.get()) {
                    predicates.add(cb.isNotEmpty(venda.get("satCupons")));

                } else {
                    predicates.add(cb.isEmpty(venda.get("satCupons")));

                }
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(venda.get("criacao")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(venda).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<Venda> query = em.createQuery(q);

            //query.setMaxResults(50);
            //query.setParameter(parNome, nome);
            vendas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return vendas;
    }

    public List<Venda> findPorPeriodoEntrega(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCanceladas) {
        List<Venda> listVenda = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
            Root<Venda> rootDocumento = cq.from(Venda.class);

            Join<Venda, MovimentoFisico> rootJoin = rootDocumento.join("movimentosFisicos", JoinType.INNER);
            cq.multiselect(rootDocumento, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootDocumento.get("tipoOperacao"), tipoOperacao));

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootJoin.get("dataSaidaPrevista"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootJoin.get("dataSaidaPrevista"), (Comparable) dataFinal));
            }

            if (funcionario != null && funcionario.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("funcionario"), funcionario));
            }

            if (pessoa != null && pessoa.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("cliente"), pessoa));
            }

            if (veiculo != null && veiculo.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("veiculo"), veiculo));
            }

            if (!exibirCanceladas) {
                predicates.add(cb.isNull(rootDocumento.get("cancelamento")));
            }

            rootJoin.on(predicates.toArray(new Predicate[]{}));

            List<Order> o = new ArrayList<>();

            o.add(cb.desc(rootJoin.get("dataSaidaPrevista")));

            o.add(cb.desc(rootDocumento.get("criacao")));

            cq.select(rootDocumento);//.where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Venda> query = em.createQuery(cq);

            listVenda = query.getResultList();

            //remover duplicatas
            /*
            List<Person> personListFiltered = personList.stream() 
            .filter(distinctByKey(p -> p.getName())) 
            .collect(Collectors.toList());
             */
        } catch (Exception e) {
            System.err.println(e);
        }
        return listVenda;
    }

    public List<Venda> findPorPeriodoDevolucao(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCanceladas) {
        List<Venda> listVenda = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
            Root<Venda> rootDocumento = cq.from(Venda.class);

            Join<Venda, MovimentoFisico> rootJoin = rootDocumento.join("movimentosFisicos", JoinType.INNER);
            cq.multiselect(rootDocumento, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootDocumento.get("tipoOperacao"), tipoOperacao));

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootJoin.get("dataEntradaPrevista"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootJoin.get("dataEntradaPrevista"), (Comparable) dataFinal));
            }

            if (funcionario != null && funcionario.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("funcionario"), funcionario));
            }

            if (pessoa != null && pessoa.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("cliente"), pessoa));
            }

            if (veiculo != null && veiculo.getId() > 0) {
                predicates.add(cb.equal(rootDocumento.get("veiculo"), veiculo));
            }

            if (!exibirCanceladas) {
                predicates.add(cb.isNull(rootDocumento.get("cancelamento")));
            }

            rootJoin.on(predicates.toArray(new Predicate[]{}));

            List<Order> o = new ArrayList<>();

            o.add(cb.desc(rootJoin.get("dataEntradaPrevista")));

            o.add(cb.desc(rootDocumento.get("criacao")));

            cq.select(rootDocumento);//.where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Venda> query = em.createQuery(cq);

            listVenda = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listVenda;
    }

    public List<MovimentoFisico> findItens(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();

        List<Venda> listVenda = findByCriteria(tipoOperacao, dataInicial, dataFinal, null, null, null, false, Optional.empty());
        for (Venda v : listVenda) {
            if (!v.getMovimentosFisicosSaida().isEmpty()) {
                listMovimentoFisico.addAll(v.getMovimentosFisicosSaida());
            }
        }

        return listMovimentoFisico;
    }

    public List<VendaItemConsolidado> findItensConsolidado(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //código, nome, quantidade, valor médio, subtotal

        List<MovimentoFisico> listMovimentoFisico = findItens(tipoOperacao, dataInicial, dataFinal);

        Map<Produto, BigDecimal> sumQuantidade = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto,
                Collectors.mapping(MovimentoFisico::getSaida, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))
        );

        Map<Produto, BigDecimal> sumValor = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto,
                Collectors.mapping(MovimentoFisico::getSubtotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))
        );

        List<VendaItemConsolidado> listConsolidado = new ArrayList<>();
        for (Map.Entry<Produto, BigDecimal> entrySumQuantidade : sumQuantidade.entrySet()) {

            VendaItemConsolidado c = new VendaItemConsolidado();

            Produto produto = entrySumQuantidade.getKey();
            c.setProduto(produto);
            c.setQuantidade(entrySumQuantidade.getValue());
            c.setTotal(sumValor.get(produto));

            listConsolidado.add(c);
        }

        return listConsolidado;
    }

    public List<VendaCategoriaConsolidado> findVendasConsolidadasPorCategoria(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //categoria, total bruto e total líquido

        List<MovimentoFisico> listMovimentoFisico = findItens(tipoOperacao, dataInicial, dataFinal);

        List<VendaCategoriaConsolidado> listConsolidado = new ArrayList<>();

        List<Categoria> listCategoria = new CategoriaDAO().findAll();

        //categoria e total
        Map<Categoria, BigDecimal> sumTotal = new HashMap<>();

        Categoria semCategoria = new Categoria();
        semCategoria.setId(0);
        semCategoria.setNome("Sem Categoria");
        //listCategoria.add(semCategoria);
        for (MovimentoFisico movimentoFisico : listMovimentoFisico) {
            for (Categoria categoria : listCategoria) {
                if (movimentoFisico.getProduto().getCategoria() == categoria) {
                    sumTotal.merge(categoria, movimentoFisico.getSubtotal(), BigDecimal::add);
                }
            }
            if (movimentoFisico.getProduto().getCategoria() == null) {
                sumTotal.merge(semCategoria, movimentoFisico.getSubtotal(), BigDecimal::add);
            }
        }

        for (Map.Entry<Categoria, BigDecimal> entry : sumTotal.entrySet()) {
            Categoria categoria = entry.getKey();
            BigDecimal totalBruto = entry.getValue();
            VendaCategoriaConsolidado c = new VendaCategoriaConsolidado();
            c.setCategoria(categoria);
            c.setTotalBruto(totalBruto);
            c.setTotalLiquido(BigDecimal.ZERO);

            listConsolidado.add(c);
        }

        return listConsolidado;
    }

    //TO DO
    public Map<Produto, Map<BigDecimal, List<MovimentoFisico>>> findItensConsolidadoSeparandoValor(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //código, nome, quantidade, valor médio, subtotal

        List<MovimentoFisico> listMovimentoFisico = findItens(tipoOperacao, dataInicial, dataFinal);

        Map<Produto, Map<BigDecimal, List<MovimentoFisico>>> sumQuantidade = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto,
                Collectors.groupingBy(MovimentoFisico::getValor)
        )
        );

        return sumQuantidade;
    }

}
