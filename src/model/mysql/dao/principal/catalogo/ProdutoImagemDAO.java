/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import model.mysql.bean.principal.catalogo.ProdutoImagem;
import ouroboros.Ouroboros;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ProdutoImagemDAO {
    
    
    public ProdutoImagem save(ProdutoImagem produtoImagem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (produtoImagem.getId() == null) {
                em.persist(produtoImagem);
            } else {
                em.merge(produtoImagem);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }finally {
            em.close();
        }
        
        return produtoImagem;
    }

    public ProdutoImagem findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ProdutoImagem produtoImagem = null;
        try {
            produtoImagem = em.find(ProdutoImagem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return produtoImagem;
    }
    
    public List<ProdutoImagem> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ProdutoImagem> produtoImagens = null;
        try {
            produtoImagens = em.createQuery("from ProdutoImagem pI").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return produtoImagens;
    }
    
    public ProdutoImagem remove(ProdutoImagem produtoImagem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        
        try {
            produtoImagem = em.find(ProdutoImagem.class, produtoImagem.getId());
            em.getTransaction().begin();
            em.remove(produtoImagem);
            em.getTransaction().commit();
            
            removerArquivo(produtoImagem);
            
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
             em.close();
        }
        
        return produtoImagem;
    }
    
    private void removerArquivo(ProdutoImagem produtoImagem) {
        try {
            File file = new File(Ouroboros.APP_PATH 
                + "custom/catalogo/" 
                + produtoImagem.getProduto().getId() 
                + "/" +  produtoImagem.getArquivo());
            
            Files.deleteIfExists(file.toPath());
            
        } catch (IOException e) {
            System.out.println("Erro ao remover Arquivo de Imagem" + e);
        }
        
    }
    
}
