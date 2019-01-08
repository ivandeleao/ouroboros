/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import connection.ConnectionFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.Produto;
import model.bean.fiscal.UnidadeComercial;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ProdutoDAO {

    public Produto save(Produto produto) {
        //em = new ConnectionFactory().getConnection();
        try {
            em.getTransaction().begin();
            if (produto.getId() == null) {
                em.persist(produto);
            } else {
                em.merge(produto);
            }
            //------------------------------------------------------------------
            produto.setCodigo(produto.getCodigo());
            em.merge(produto);
            //------------------------------------------------------------------
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em produto save " + e);
            em.getTransaction().rollback();
        } finally {
            //em.close();
        }

        return produto;
    }

    public Produto findById(Integer id) {
        Produto produto = null;

        try {
            produto = em.find(Produto.class, id);
        } catch (Exception e) {
            System.err.println("Erro em produto findById " + e);
        }

        return produto;
    }

    public List<Produto> findByCodigo(String codigo) {
        List<Produto> produtos = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Produto> q = cb.createQuery(Produto.class);
            Root<Produto> produto = q.from(Produto.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(produto.get("codigo"), codigo));
            predicates.add(cb.isNull(produto.get("exclusao")));

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(produto.get("nome")));

            q.select(produto).where(cb.and(predicates.toArray(new Predicate[]{})));
            q.orderBy(o);

            TypedQuery<Produto> query = em.createQuery(q);

            produtos = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em produto.findByCodigo " + e);
        }
        return produtos;
    }

    public List<Produto> findByNome(String nome) {
        return findByCriteria(nome, null, false, false);
    }

    public List<Produto> findItensDeBalanca() {
        return findByCriteria(null, null, true, false);
    }

    public List<Produto> findByCriteria(String buscaRapida, UnidadeComercial unidadeVenda, boolean apenasItemBalanca, boolean exibirExcluidos) {
        List<Produto> produtos = null;
        try {
            em = Ouroboros.CONNECTION_FACTORY.getConnection();
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Produto> q = cb.createQuery(Produto.class);
            Root<Produto> produto = q.from(Produto.class);

            List<Predicate> predicates = new ArrayList<>();

            if (buscaRapida != null) {
                predicates.add(cb.like(produto.get("codigo"), "%" + buscaRapida + "%"));
                predicates.add(cb.like(produto.get("nome"), "%" + buscaRapida + "%"));
                predicates.add(cb.like(produto.get("outrosCodigos"), "%" + buscaRapida + "%"));
            }
            if (unidadeVenda != null && unidadeVenda.getId() > 0) {
                predicates.add(cb.equal(produto.get("unidadeComercialVenda"), unidadeVenda));
            }
            if (apenasItemBalanca) {
                predicates.add(cb.isTrue(produto.get("balanca")));
            }

            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(produto.get("exclusao")));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(produto.get("nome")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            if (predicates.isEmpty()) {
                q.select(produto).where(predicateExclusao);
            } else {
                q.select(produto).where(cb.or(predicates.toArray(new Predicate[]{})), predicateExclusao);
            }

            q.orderBy(o);

            TypedQuery<Produto> query = em.createQuery(q);

            produtos = query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em produto.findByCriteria " + e);
        }
        return produtos;
    }
    
    

    public List<Produto> findAll() {
        return findByCriteria(null, null, false, false);
    }

    public Produto delete(Produto produto) {
        produto.setExclusao(LocalDateTime.now());

        return save(produto);
    }
}
