/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import connection.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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
import model.bean.principal.Venda;
import model.bean.principal.Produto;
import model.bean.principal.Categoria;
import model.bean.principal.VendaCategoriaConsolidado;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.VendaItemConsolidado;
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

            query.setMaxResults(50);
            vendas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        //em.close();
        return vendas;
    }

    public Timestamp getLastTimestamp() {
        Timestamp lastCriacao = null; //getLastCriacao2();
        Timestamp lastEncerramento = null; //getLastEncerramento2();

        System.out.println("lastCriacao: " + lastCriacao);
        System.out.println("lastEncerramento: " + lastEncerramento);

        if (lastCriacao != null && lastEncerramento != null && lastCriacao.compareTo(lastEncerramento) > 0) {
            return lastCriacao;
        } else if (lastEncerramento != null) {
            return lastEncerramento;
        } else {
            return DateTime.getNow();
        }
    }

    public LocalDateTime getLastCriacao2() {
        List<Venda> vendas = null;
        try {
            Query query = em.createQuery("from Venda v encerramento is not null order by criacao desc");

            query.setMaxResults(1);

            List<Venda> listVenda = query.getResultList();

            LocalDateTime lastCriacao = listVenda.get(0).getCriacao();

            return lastCriacao;

        } catch (Exception e) {
            System.err.println("getLastCriacao2: " + e);
        }
        return null;
    }

    public LocalDateTime getLastCriacao() {
        List<Venda> vendas = null;
        try {
            LocalDateTime lastCriacao;
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);
            Root<Venda> venda = q.from(Venda.class);

            List<Predicate> predicates = new ArrayList<>();

            List<Order> o = new ArrayList<>();
            o.add(cb.desc(venda.get("criacao")));

            q.select(venda);//.where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            TypedQuery<Venda> query = em.createQuery(q);
            query.setMaxResults(1);

            List<Venda> listVenda = query.getResultList();

            lastCriacao = listVenda.get(0).getCriacao();

            return lastCriacao;

        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    public Timestamp getLastEncerramento2() {
        List<Venda> vendas = null;
        try {
            Query query = em.createQuery("from Venda v where encerramento is not null order by encerramento desc");

            query.setMaxResults(1);

            List<Venda> listVenda = query.getResultList();

            Timestamp lastEncerramento = listVenda.get(0).getEncerramento();

            return lastEncerramento;

        } catch (Exception e) {
            System.err.println("getLastEncerramento2: " + e);
        }
        return null;
    }

    public Timestamp getLastEncerramento() {
        List<Venda> vendas = null;
        try {
            Timestamp lastEncerramento;
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);
            Root<Venda> venda = q.from(Venda.class);

            TypedQuery<Venda> query = em.createQuery(q);

            List<Predicate> predicates = new ArrayList<>();

            List<Order> o = new ArrayList<>();

            predicates = new ArrayList<>();

            predicates.add(cb.isNotNull(venda.get("encerramento")));

            o = new ArrayList<>();
            o.add(cb.desc(venda.get("encerramento")));

            q.select(venda).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);

            query = em.createQuery(q);
            query.setMaxResults(1);

            List<Venda> listVenda = query.getResultList();

            lastEncerramento = listVenda.get(0).getEncerramento();

            return lastEncerramento;

        } catch (Exception e) {
            System.err.println(e);
            //do nothing
        }
        return null;
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

            q.select(venda).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Venda> query = em.createQuery(q);

            vendas = query.getResultList();

        } catch (Exception e) {
            System.err.println(e);
            //do nothing
        }

        return vendas;
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

            q.select(venda).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Venda> query = em.createQuery(q);
            //talvez tenha que limitar o resultado para 1

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

    public List<Venda> findByCriteria(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<Venda> vendas = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Venda> q = cb.createQuery(Venda.class);
            
            Root<Venda> venda = q.from(Venda.class);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(venda.get("criacao"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(venda.get("criacao"), (Comparable) dataFinal));
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
    
    
    public List<Venda> findPorPeriodoEntrega(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<Venda> listVenda = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
            Root<Venda> rootVenda = cq.from(Venda.class);

            Join<Venda, MovimentoFisico> rootJoin = rootVenda.join("movimentosFisicos", JoinType.INNER);
            cq.multiselect(rootVenda, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootJoin.get("dataSaidaPrevista"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootJoin.get("dataSaidaPrevista"), (Comparable) dataFinal));
            }
            
            rootJoin.on(predicates.toArray(new Predicate[]{}));

            List<Order> o = new ArrayList<>();
            
            o.add(cb.desc(rootJoin.get("dataSaidaPrevista")));
            
            o.add(cb.desc(rootVenda.get("criacao")));

            cq.select(rootVenda);//.where(predicates.toArray(new Predicate[]{}));

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
    
    
    public List<Venda> findPorPeriodoDevolucao(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<Venda> listVenda = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
            Root<Venda> rootVenda = cq.from(Venda.class);

            Join<Venda, MovimentoFisico> rootJoin = rootVenda.join("movimentosFisicos", JoinType.INNER);
            cq.multiselect(rootVenda, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootJoin.get("dataEntradaPrevista"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootJoin.get("dataEntradaPrevista"), (Comparable) dataFinal));
            }
            
            rootJoin.on(predicates.toArray(new Predicate[]{}));

            List<Order> o = new ArrayList<>();
            
            o.add(cb.desc(rootJoin.get("dataEntradaPrevista")));
            
            o.add(cb.desc(rootVenda.get("criacao")));

            cq.select(rootVenda);//.where(predicates.toArray(new Predicate[]{}));

            cq.orderBy(o);

            TypedQuery<Venda> query = em.createQuery(cq);

            listVenda = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listVenda;
    }
    
    
    
    
    
    

    public List<MovimentoFisico> findItens(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();

        List<Venda> listVenda = findByCriteria(dataInicial, dataFinal);
        for (Venda v : listVenda) {
            if (!v.getMovimentosFisicos().isEmpty()) {
                listMovimentoFisico.addAll(v.getMovimentosFisicos());
            }
        }

        return listMovimentoFisico;
    }
    
    public List<VendaItemConsolidado> findItensConsolidado(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //código, nome, quantidade, valor médio, subtotal
        
        List<MovimentoFisico> listMovimentoFisico = findItens(dataInicial, dataFinal);
        
        Map<Produto, BigDecimal> sumQuantidade = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto, 
                Collectors.mapping(MovimentoFisico::getSaida, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))
        );
        
        Map<Produto, BigDecimal> sumValor = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto, 
                Collectors.mapping(MovimentoFisico::getSubtotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))
        );
        
        
        List<VendaItemConsolidado> listConsolidado = new ArrayList<>();
        for(Map.Entry<Produto, BigDecimal> entrySumQuantidade : sumQuantidade.entrySet()) {
            
            VendaItemConsolidado c = new VendaItemConsolidado();
            
            Produto produto = entrySumQuantidade.getKey();
            c.setProduto(produto);
            c.setQuantidade(entrySumQuantidade.getValue());
            c.setTotal(sumValor.get(produto));
            
            listConsolidado.add(c);
        }
        
        
        return listConsolidado;
    }
    
    public List<VendaCategoriaConsolidado> findVendasConsolidadasPorCategoria(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //categoria, total bruto e total líquido
        
        List<MovimentoFisico> listMovimentoFisico = findItens(dataInicial, dataFinal);
        
        List<VendaCategoriaConsolidado> listConsolidado = new ArrayList<>();
        
        List<Categoria> listCategoria = new CategoriaDAO().findAll();
        
        //categoria e total
        Map<Categoria, BigDecimal> sumTotal = new HashMap<>();
        
        Categoria semCategoria = new Categoria();
        semCategoria.setId(0);
        semCategoria.setNome("Sem Categoria");
        //listCategoria.add(semCategoria);
        for(MovimentoFisico movimentoFisico : listMovimentoFisico) {
            for(Categoria categoria : listCategoria) {
                if(movimentoFisico.getProduto().getCategoria() == categoria) {
                    sumTotal.merge(categoria, movimentoFisico.getSubtotal(), BigDecimal::add);
                }
            }
            if (movimentoFisico.getProduto().getCategoria() == null) {
                sumTotal.merge(semCategoria, movimentoFisico.getSubtotal(), BigDecimal::add);
            }
        }
        
        for(Map.Entry<Categoria, BigDecimal> entry : sumTotal.entrySet()) {
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
    public Map<Produto, Map<BigDecimal, List<MovimentoFisico>>> findItensConsolidadoSeparandoValor(LocalDateTime dataInicial, LocalDateTime dataFinal) {
        //código, nome, quantidade, valor médio, subtotal
        
        List<MovimentoFisico> listMovimentoFisico = findItens(dataInicial, dataFinal);
        
        Map<Produto, Map<BigDecimal, List<MovimentoFisico>>> sumQuantidade = listMovimentoFisico.stream().collect(Collectors.groupingBy(MovimentoFisico::getProduto, 
                        Collectors.groupingBy(MovimentoFisico::getValor)
                )
        );
        
        
        return sumQuantidade;
    }

}
