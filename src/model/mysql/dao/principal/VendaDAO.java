/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import connection.ConnectionFactory;
import model.mysql.dao.principal.catalogo.CategoriaDAO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
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
import model.mysql.bean.principal.documento.VendaStatus;
import model.mysql.bean.principal.pessoa.Pessoa;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class VendaDAO {

    public Venda save(Venda venda) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();

            //carregar campo de cache
            venda.setTotalProdutos();
            venda.setTotalServicos();
            venda.setVendaStatus();

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
        } finally {
            em.close();
        }

        return venda;
    }

    public Venda findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Venda venda = null;
        try {
            venda = em.find(Venda.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return venda;
    }

    public List<Venda> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Venda> vendas = null;
        try {
            Query query = em.createQuery("from Venda v order by criacao desc");

            //query.setMaxResults(50);
            vendas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return vendas;
    }

    public List<Venda> getComandasAbertas() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
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
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ComandaSnapshot> comandas = new ArrayList<>();
        //try {
        /*String sql = "select venda.id, venda.comanda as numero, coalesce(venda.comandaNome, '') as nome, venda.criacao as inicio, "
                + "count( if( movimentoFisico.saida > 0, 1, null)) - count( if(movimentoFisico.entrada > 0, 1, null)) as itens, "
                + "sum((movimentoFisico.saida - movimentoFisico.entrada) * movimentoFisico.valor"
                + "- (movimentoFisico.valor * coalesce(movimentoFisico.descontoPercentual, 0) / 100)"
                + "- coalesce(movimentoFisico.descontoMonetario, 0)"
                + "+ (movimentoFisico.valor * coalesce(movimentoFisico.acrescimoPercentual, 0) / 100)"
                + "+ coalesce(movimentoFisico.acrescimoMonetario, 0)) as valor "
                + "from venda left join movimentoFisico on venda.id = movimentoFisico.vendaId "
                + "where comanda is not null and encerramento is null and cancelamento is null "
                + "group by venda.id";*/

        //2019-11-13 pegando o campo direto, pois agora uso o conceito de cache
        String sql = "select venda.id, venda.comanda as numero, coalesce(venda.comandaNome, '') as nome, venda.criacao as inicio, "
                + "count( if( movimentoFisico.saida > 0, 1, null)) - count( if(movimentoFisico.entrada > 0, 1, null)) as itens, "
                + "(venda.totalProdutos + venda.totalServicos) as valor "
                + "from venda left join movimentoFisico on venda.id = movimentoFisico.vendaId "
                + "where comanda is not null and encerramento is null and cancelamento is null "
                + "group by venda.id";

        Query q = em.createNativeQuery(sql);

        List<Object[]> rows = q.getResultList();

        for (Object[] row : rows) {

            System.out.println("row: " + row[0]);
            ComandaSnapshot c = new ComandaSnapshot();
            c.setId(Integer.valueOf(row[0].toString()));
            c.setNumero(Integer.valueOf(row[1].toString()));
            c.setNome(row[2].toString());
            c.setInicio(((Timestamp) row[3]).toLocalDateTime());
            c.setItens(Integer.valueOf(row[4].toString()));
            c.setValor((BigDecimal) row[5]);

            comandas.add(c);
        }

        em.close();

        return comandas;

        //} catch (Exception e) {
        //    System.err.println(e);
        //}
        //return null;
    }

    public Venda getComandaAberta(int comanda) {
        EntityManager em = CONNECTION_FACTORY.getConnection();

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
        } finally {
            em.close();
        }

        return null;
    }
    
    public List<Venda> findByVeiculo(Veiculo veiculo) {
        return findByCriteria(TipoOperacao.SAIDA, null, null, null, null, veiculo, false, null, null, null, null, false, null);
    }

    public List<Venda> findByCriteria(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCancelados, Optional<Boolean> nfseEmitido, Optional<Boolean> satEmitido, Optional<Boolean> nfeEmitido, Optional<Boolean> hasDocumentosFilhos, boolean exibirAgrupados, VendaStatus vendaStatus) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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

            if (!exibirCancelados) {
                predicates.add(cb.isNull(venda.get("cancelamento")));
            }

            if (nfseEmitido != null && nfseEmitido.isPresent()) {
                if (nfseEmitido.get()) {
                    predicates.add(cb.isNotNull(venda.get("nfseDataHora")));

                } else {
                    predicates.add(cb.isNull(venda.get("nfseDataHora")));

                }
            }

            if (satEmitido != null && satEmitido.isPresent()) {
                if (satEmitido.get()) {
                    predicates.add(cb.isNotEmpty(venda.get("satCupons")));

                } else {
                    predicates.add(cb.isEmpty(venda.get("satCupons")));

                }
            }

            if (nfeEmitido != null && nfeEmitido.isPresent()) {
                if (nfeEmitido.get()) {
                    predicates.add(cb.isNotNull(venda.get("chaveAcessoNfe")));

                } else {
                    predicates.add(cb.isNull(venda.get("chaveAcessoNfe")));

                }
            }

            if (hasDocumentosFilhos != null && hasDocumentosFilhos.isPresent()) {
                if (hasDocumentosFilhos.get()) {
                    predicates.add(cb.isNotEmpty(venda.get("documentosFilho")));

                } else {
                    predicates.add(cb.isEmpty(venda.get("documentosFilho")));

                }
            }

            if (!exibirCancelados) {
                predicates.add(cb.isNull(venda.get("cancelamento")));
            }

            if (!exibirAgrupados) {
                predicates.add(cb.isNull(venda.get("documentoPai")));
            }

            if (vendaStatus != null && vendaStatus.getId() > 0) {
                predicates.add(cb.equal(venda.get("vendaStatus"), vendaStatus));
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
            System.err.println("Erro em VendaDAO.findByCriteria() " + e);
        } finally {
            em.close();
        }
        return vendas;
    }

    public List<Venda> findPorPeriodoEntrega(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCanceladas) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        return listVenda;
    }

    public List<Venda> findPorPeriodoDevolucao(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal, Funcionario funcionario, Pessoa pessoa, Veiculo veiculo, boolean exibirCanceladas) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        return listVenda;
    }

    public List<MovimentoFisico> findItens(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();

        List<Venda> listVenda = findByCriteria(tipoOperacao, dataInicial, dataFinal, null, null, null, false, null, null, null, null, false, null);
        for (Venda v : listVenda) {
            if (!v.getMovimentosFisicosSaida().isEmpty()) {
                listMovimentoFisico.addAll(v.getMovimentosFisicosSaida());
            }
        }

        return listMovimentoFisico;
    }

    public List<VendaItemConsolidado> findItensConsolidado(TipoOperacao tipoOperacao, LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //código, nome, quantidade, valor médio, subtotal

        List<Produto> produtos = new ArrayList<>();
        
        List<MovimentoFisico> itens = findItens(tipoOperacao, dataInicial, dataFinal);
        
        itens = itens.stream().filter(mf -> mf.getProduto() != null).collect(Collectors.toList());
        
        for (MovimentoFisico mf : itens) {
            if(!produtos.contains(mf.getProduto()) && mf.getProduto() != null) {
                produtos.add(mf.getProduto());
            }
        }
        
        System.out.println("produtos size:" + produtos.size());
        
        produtos.sort(Comparator.comparing(Produto::getNome));
        
        List<VendaItemConsolidado> listConsolidado = new ArrayList<>();
        
        for(Produto p : produtos) {
            System.out.println("p: " + p .getNome());
            
            VendaItemConsolidado c = new VendaItemConsolidado();
            
            c.setProduto(p);

            for (MovimentoFisico mf : itens) {
                
                if(mf.getProduto().equals(p)) {
                    
                    c.setQuantidade(c.getQuantidade().add(mf.getSaida()));
                    c.setTotal(c.getTotal().add(mf.getSubtotal()));
                    
                    //itens.remove(mf);
                }
            }
            
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
                if (movimentoFisico.getProduto() != null && movimentoFisico.getProduto().getCategoria() == categoria) {
                    sumTotal.merge(categoria, movimentoFisico.getSubtotal(), BigDecimal::add);
                }
            }
            if (movimentoFisico.getProduto() != null && movimentoFisico.getProduto().getCategoria() == null) {
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

    public List<Venda> findGarantiaPorVeiculo(Produto produto, Venda documento) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<MovimentoFisico> mfs = new ArrayList<>();
        List<Venda> vendas = new ArrayList<>();
        
        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MovimentoFisico> cq = cb.createQuery(MovimentoFisico.class);

            Root<MovimentoFisico> rootMovimentoFisico = cq.from(MovimentoFisico.class);
            
            Join<MovimentoFisico, Venda> rootJoin = rootMovimentoFisico.join("venda", JoinType.LEFT);
            cq.multiselect(rootMovimentoFisico, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootMovimentoFisico.get("produto"), produto));
            
            LocalDateTime dataInicial = LocalDate.now().atStartOfDay().minusDays(Long.valueOf(produto.getDiasGarantia()));
            
            System.out.println("dias garantia: " + produto.getDiasGarantia());
            System.out.println("dataInicial: " + dataInicial);

            predicates.add(cb.greaterThanOrEqualTo(rootJoin.get("criacao"), (Comparable) dataInicial));


            /*if (pessoa != null && pessoa.getId() > 0) {
            predicates.add(cb.equal(venda.get("cliente"), pessoa));
            }*/
            
            Veiculo veiculo = documento.getVeiculo();
            
            if (veiculo != null && veiculo.getId() > 0) {
                predicates.add(cb.equal(rootJoin.get("veiculo"), veiculo));
            }
            
            predicates.add(cb.notEqual(rootJoin.get("id"), documento.getId()));
            
            predicates.add(cb.equal(rootJoin.get("tipoOperacao"), TipoOperacao.SAIDA));

            ////predicates.add(cb.isNull(rootMovimentoFisico.get("estorno")));
            predicates.add(cb.isNull(rootJoin.get("cancelamento")));

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(rootJoin.get("criacao")));

            cq.select(rootMovimentoFisico).where(predicates.toArray(new Predicate[]{}));
            cq.orderBy(o);

            TypedQuery<MovimentoFisico> query = em.createQuery(cq);

            mfs = query.getResultList();
            
            for(MovimentoFisico mf : mfs) {
                if(!vendas.contains(mf.getVenda()) && !mf.isEstornado() && !mf.isEstorno()) {
                    vendas.add(mf.getVenda());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro em VendaDAO.findGarantia() " + e);
        } finally {
            em.close();
        }
        return vendas;
    }
    
}
