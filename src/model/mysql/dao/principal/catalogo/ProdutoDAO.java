/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.fiscal.UnidadeComercial;
import model.mysql.bean.principal.catalogo.Categoria;
import model.mysql.bean.principal.catalogo.Marca;
import model.mysql.bean.principal.catalogo.ProdutoTipo;
import model.mysql.bean.principal.catalogo.Subcategoria;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ProdutoDAO {

    public Produto save(Produto produto) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
            em.close();
        }

        return produto;
    }

    public Produto findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Produto produto = null;

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Produto> q = cb.createQuery(Produto.class);
            Root<Produto> rootProduto = q.from(Produto.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootProduto.get("id"), id));
            predicates.add(cb.isNull(rootProduto.get("exclusao")));

            q.select(rootProduto).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Produto> query = em.createQuery(q);

            produto = query.getSingleResult();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em produto.findByCodigo " + e);
        } finally {
            em.close();
        }
        
        return produto;
    }

    public List<Produto> findByCodigo(String codigo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return produtos;
    }

    public List<Produto> findByNome(String nome) {
        return findByCriteria(nome, null, null, null, null, null, false, null, null, Optional.of(false));
    }

    public List<Produto> findItensDeBalanca() {
        return findByCriteria(null, null, null, null, null, null, true, null, null, Optional.of(false));
    }
    
    public List<Produto> findPorCategoria(Categoria categoria) {
        return findByCriteria(null, categoria, null, null, null, null, false, null, null, Optional.of(false));
    }

    public List<Produto> findByCriteria(String buscaRapida, Categoria categoria, Subcategoria subcategoria, Marca marca, UnidadeComercial unidadeVenda, ProdutoTipo produtoTipo, boolean apenasItemBalanca, Optional<Boolean> necessidadeCompra, Optional<Boolean> estoqueMinimo, Optional<Boolean> exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Produto> produtos = null;
        try {
            //em = Ouroboros.CONNECTION_FACTORY.getConnection();
            //em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Produto> q = cb.createQuery(Produto.class);
            Root<Produto> produto = q.from(Produto.class);

            List<Predicate> predicates = new ArrayList<>();

            if (buscaRapida != null) {
                predicates.add(
                        cb.or(
                            cb.like(produto.get("codigo"), "%" + buscaRapida + "%"),
                            cb.like(produto.get("nome"), "%" + buscaRapida + "%"),
                            cb.like(produto.get("outrosCodigos"), "%" + buscaRapida + "%"),
                            cb.like(produto.get("descricao"), "%" + buscaRapida + "%") // aplicacao
                        )
                );
            }
            
            if(categoria != null && categoria.getId() > 0) {
                predicates.add(cb.equal(produto.get("categoria"), categoria));
            }
            
            if(subcategoria != null && subcategoria.getId() > 0) {
                predicates.add(cb.equal(produto.get("subcategoria"), subcategoria));
            }
            
            if(marca != null && marca.getId() > 0) {
                predicates.add(cb.equal(produto.get("marca"), marca));
            }
            
            if (unidadeVenda != null && unidadeVenda.getId() > 0) {
                predicates.add(cb.equal(produto.get("unidadeComercialVenda"), unidadeVenda));
            }
            
            if (produtoTipo != null && produtoTipo.getId() > 0) {
                predicates.add(cb.equal(produto.get("produtoTipo"), produtoTipo));
            }
            
            if (apenasItemBalanca) {
                predicates.add(cb.isTrue(produto.get("balanca")));
            }
            
            Predicate predicateNecessidadeCompra = null;
            if (necessidadeCompra != null && necessidadeCompra.isPresent()) {
                if (necessidadeCompra.get()) {
                    predicateNecessidadeCompra = cb.isNotNull(produto.get("necessidadeCompra"));

                } else {
                    predicateNecessidadeCompra = cb.isNull(produto.get("necessidadeCompra"));

                }
            }
            
            Predicate predicateEstoqueMinimo = null;
            if (estoqueMinimo != null && estoqueMinimo.isPresent()) {
                if (estoqueMinimo.get()) {
                    predicateEstoqueMinimo = cb.and(
                            cb.greaterThan(produto.get("estoqueMinimo"), BigDecimal.ZERO),
                            cb.lessThanOrEqualTo(produto.get("estoqueAtual"), produto.get("estoqueMinimo"))
                    );

                } else { //Não
                    predicateEstoqueMinimo = cb.or(
                            cb.equal(produto.get("estoqueMinimo"), BigDecimal.ZERO),
                            cb.greaterThan(produto.get("estoqueAtual"), produto.get("estoqueMinimo"))
                    );

                }
            }
            
            if(predicateNecessidadeCompra != null && predicateEstoqueMinimo != null) {
                predicates.add(cb.or(predicateNecessidadeCompra, predicateEstoqueMinimo));
                
            } else if(predicateNecessidadeCompra != null) {
                predicates.add(predicateNecessidadeCompra);
                
            } else if(predicateEstoqueMinimo != null) {
                predicates.add(predicateEstoqueMinimo);
                
            }
            
            
            

            /*Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(produto.get("exclusao")));
            }*/
            
            Predicate predicateExclusao = null;
            if (exibirExcluidos != null && exibirExcluidos.isPresent()) {
                if (exibirExcluidos.get()) {
                    predicateExclusao = cb.isNotNull(produto.get("exclusao"));

                } else {
                    predicateExclusao = cb.isNull(produto.get("exclusao"));

                }
                predicates.add(predicateExclusao);
            }
            
            //predicateExclusao = (cb.isNull(produto.get("exclusao")));
            
            
            

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(produto.get("nome")));

            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            if (predicates.isEmpty()) {
                q.select(produto).where(predicateExclusao);
            } else {
                //q.select(produto).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
                q.select(produto).where(cb.and(predicates.toArray(new Predicate[]{})));
            }

            q.orderBy(o);

            TypedQuery<Produto> query = em.createQuery(q);

            produtos = query.getResultList();
            //em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em produto.findByCriteria " + e);
        } finally {
            em.close();
        }
        
        return produtos;
    }
    
    

    public List<Produto> findAll() {
        return findByCriteria(null, null, null, null, null, null, false, null, null, Optional.of(false));
    }

    public Produto delete(Produto produto) {
        produto.setExclusao(LocalDateTime.now());

        return save(produto);
    }
    
    public Produto desfazerExclusao(Produto produto) {
        produto.setExclusao(null);

        return save(produto);
    }
}
