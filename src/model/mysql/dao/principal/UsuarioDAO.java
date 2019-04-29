/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.Diretiva;
import model.mysql.bean.principal.DiretivaStatus;
import model.mysql.bean.principal.Usuario;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.em;
import view.LoginView;

/**
 *
 * @author ivand
 */
public class UsuarioDAO {

    public Usuario save(Usuario usuario) {
        try {
            em.getTransaction().begin();
            if (usuario.getId() == null) {
                em.persist(usuario);
            } else {
                em.merge(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em usuario.save " + e);
            em.getTransaction().rollback();
        }

        return usuario;
    }

    public Usuario findById(Integer id) {
        Usuario usuario = null;

        try {
            usuario = em.find(Usuario.class, id);
        } catch (Exception e) {
            System.err.println("Erro em usuario.findById " + e);
        }

        return usuario;
    }

    public List<Usuario> findAll() {
        List<Usuario> listUsuario = null;
        try {
            Query query = em.createQuery("from " + Usuario.class.getSimpleName() + " u");
            listUsuario = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em usuario.findAll " + e);
        }
        return listUsuario;
    }

    //--------------------------------------------------------------------------
    public static boolean validarAdministrador() {
        return Ouroboros.USUARIO.isAdministrador() && !Ouroboros.SISTEMA_REVALIDAR_ADMINISTRADOR;
    }
    
    public static boolean validarAdministradorComLogin() {
        if (validarAdministrador()) {
            return true;
            
        } else {
            LoginView loginView = new LoginView();

            Usuario usuario = loginView.getUsuario();

            return usuario != null && usuario.isAdministrador();
            
        }
    }

    public Usuario logar(String login, String senha) {

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Usuario> q = cb.createQuery(Usuario.class);
            Root<Usuario> rootUsuario = q.from(Usuario.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootUsuario.get("login"), login));
            predicates.add(cb.equal(rootUsuario.get("senha"), senha));

            q.select(rootUsuario).where(predicates.toArray(new Predicate[]{}));

            TypedQuery<Usuario> query = em.createQuery(q);

            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("Erro em usuario.findByCriteria " + e);
        }
        return null;
    }

    public List<Usuario> findByLogin(String login) {

        List<Usuario> listUsuario = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Usuario> q = cb.createQuery(Usuario.class);
            Root<Usuario> rootUsuario = q.from(Usuario.class);

            List<Predicate> predicates = new ArrayList<>();

            if (login != null) {
                //achar por partes diversas do login
                login = login.replaceAll(" ", "%");
                predicates.add(cb.like(rootUsuario.get("login"), "%" + login + "%"));
            }

            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootUsuario.get("login")));

            q.select(rootUsuario).where(cb.or(predicates.toArray(new Predicate[]{})));
            q.orderBy(o);

            TypedQuery<Usuario> query = em.createQuery(q);

            listUsuario = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listUsuario;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        Usuario usuario = new Usuario();

        for (Usuario u : findAll()) {
            if (u.getLogin().equals("mindware")) {
                usuario = u;
                break;
            }
        }

        usuario.setLogin("mindware");
        usuario.setSenha("753951");
        usuario.setAdministrador(true);
        usuario.normalizarDiretivas();
        usuario.getDiretivas().forEach((diretiva) -> {
            diretiva.setStatus(DiretivaStatus.LIBERADO);
        });

        save(usuario);

        if (findById(usuario.getId()) == null) {
            em.persist(usuario);
        } else {
            em.merge(usuario);
        }

    }

}
