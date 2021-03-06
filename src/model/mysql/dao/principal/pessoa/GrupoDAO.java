/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.pessoa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.pessoa.Grupo;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class GrupoDAO {

    public Grupo save(Grupo grupo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (grupo.getId() == null) {
                em.persist(grupo);
            } else {
                em.merge(grupo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return grupo;
    }

    public Grupo findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Grupo grupo = null;

        try {
            grupo = em.find(Grupo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }

        return grupo;
    }

    public List<Grupo> findAll() {
        return findByCriteria(null, false);
    }

    public List<Grupo> findByNome(String nome, boolean exibirExcluidos) {
        return findByCriteria(nome, exibirExcluidos);
    }

    public List<Grupo> findByCriteria(String nome, boolean exibirExcluidos) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Grupo> grupos = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Grupo> q = cb.createQuery(Grupo.class);
            Root<Grupo> rootGrupo = q.from(Grupo.class);

            List<Predicate> predicates = new ArrayList<>();

            if (nome != null) {
                //achar por partes diversas do nome
                nome = nome.replaceAll(" ", "%");
                predicates.add(
                        cb.like(rootGrupo.get("nome"), "%" + nome + "%")
                );
            }

            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootGrupo.get("exclusao")));
            }

            //predicates.add(cb.greaterThanOrEqualTo(grupo.get("valor"), (Comparable) menorValor));
            //predicates.add(cb.lessThanOrEqualTo(grupo.get("valor"), (Comparable) maiorValor));
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootGrupo.get("nome")));

            q.select(rootGrupo).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);

            q.orderBy(o);

            TypedQuery<Grupo> query = em.createQuery(q);

            grupos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return grupos;
    }

    public Grupo delete(Grupo grupo) {
        grupo.setExclusao(LocalDateTime.now());

        return save(grupo);
    }
}
