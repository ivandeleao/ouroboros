/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.Constante;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ConstanteDAO {
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<Constante> constantes = new ArrayList<>();
        constantes.add(new Constante("SISTEMA_ID", ""));
        constantes.add(new Constante("SISTEMA_VALIDADE", ""));
        constantes.add(new Constante("SISTEMA_REVALIDAR_ADMINISTRADOR", "true"));
        
        
        constantes.add(new Constante("EMPRESA_NOME_FANTASIA", "NOME FANTASIA - NÃO CADASTRADO"));
        constantes.add(new Constante("EMPRESA_RAZAO_SOCIAL", "RAZÃO SOCIAL - NÃO CADASTRADO"));
        constantes.add(new Constante("EMPRESA_CNPJ", "11111111111111"));
        constantes.add(new Constante("EMPRESA_IE", "111111111111"));
        constantes.add(new Constante("EMPRESA_IM", ""));
        constantes.add(new Constante("EMPRESA_TELEFONE", ""));
        constantes.add(new Constante("EMPRESA_TELEFONE2", ""));
        constantes.add(new Constante("EMPRESA_EMAIL", ""));
        
        constantes.add(new Constante("EMPRESA_ENDERECO_CEP", ""));
        constantes.add(new Constante("EMPRESA_ENDERECO", "ENDEREÇO - NÃO CADASTRADO"));
        constantes.add(new Constante("EMPRESA_ENDERECO_NUMERO", ""));
        constantes.add(new Constante("EMPRESA_ENDERECO_COMPLEMENTO", ""));
        constantes.add(new Constante("EMPRESA_ENDERECO_BAIRRO", ""));
        constantes.add(new Constante("EMPRESA_ENDERECO_CODIGO_MUNICIPIO", ""));
        
        constantes.add(new Constante("IMPRESSORA_CUPOM", "NÃO DEFINIDA"));
        constantes.add(new Constante("IMPRESSORA_A4", "NÃO DEFINIDA"));
        constantes.add(new Constante("IMPRESSORA_FORMATO_PADRAO", "CUPOM"));
        constantes.add(new Constante("IMPRESSORA_DESATIVAR", "false"));
        
        constantes.add(new Constante("SOFTWARE_HOUSE_CNPJ", "04615918000104"));
        constantes.add(new Constante("TO_SAT_PATH", "toSat/"));
        constantes.add(new Constante("FROM_SAT_PATH", "fromSat/"));
        constantes.add(new Constante("SAT_HABILITAR", "false"));
        constantes.add(new Constante("SAT_DLL", "emulador.dll"));
        constantes.add(new Constante("SAT_CODIGO_ATIVACAO", "123456789"));
        constantes.add(new Constante("SAT_SIGN_AC", "fDX1FF9/+2m31Y8BNIA7CR7Y1Db5a2BMQLegIMl41w1Cve6Q6jJ/HDGO817qYZhV2vgMk4aNY/eDF11GAlsseUTPU0tYOfLzWXGuUvEU2no/+lDkinrbbhHuUu4B8SsPyxGpNU0jxYAy6S+JutJUUkMRGZ0IteCudgNRww1zmcw3PBovWQoVOZnuJv4lDgZyUza8NRHBlDGNUygUmN9xLUTvcATmM1levaFFYCnycV7bhLIY1Is16OQEWQSMFyckRz6MUGXr1r3XyezWXm/XJZh4VZ0GwGu8Y2vU0beyMh9ZSGmGpXKTRdaK8/yG8gdNN+0TLr3qfOHVMXhEpzuRvw=="));
        constantes.add(new Constante("SAT_PRINTER", "HP6DC7CE (HP DeskJet 5820 series)"));
        
        constantes.add(new Constante("SAT_MARGEM_ESQUERDA", "5"));
        constantes.add(new Constante("SAT_MARGEM_DIREITA", "30"));
        constantes.add(new Constante("SAT_MARGEM_SUPERIOR", "10"));
        constantes.add(new Constante("SAT_MARGEM_INFERIOR", "0"));
        
        constantes.add(new Constante("TO_PRINTER_PATH", "toPrinter/"));
        constantes.add(new Constante("BACKUP_PATH", "backup/"));
        
        constantes.add(new Constante("VENDA_INSERCAO_DIRETA", "true"));
        constantes.add(new Constante("VENDA_NUMERO_COMANDAS", "50"));
        
        constantes.add(new Constante("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO", "true"));
        constantes.add(new Constante("VENDA_BLOQUEAR_CREDITO_EXCEDIDO", "true"));
        
        constantes.add(new Constante("PARCELA_MULTA", "0.00"));
        constantes.add(new Constante("PARCELA_JUROS_MONETARIO_MENSAL", "0.00"));
        constantes.add(new Constante("PARCELA_JUROS_PERCENTUAL_MENSAL", "0.00"));
        
        
        
        
        em.getTransaction().begin();
        for(Constante constante : constantes){
            if(findByNome(constante.getNome()) == null){
                em.persist(constante);
            }
        }
        em.getTransaction().commit();

    }
    
    public Constante save(Constante constante) {
        try {
            em.getTransaction().begin();
            if (constante.getNome() == null) {
                em.persist(constante);
            } else {
                em.merge(constante);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return constante;
    }

    public Constante findByNome(String nome) {
        Constante constante = null;
        try {
            constante = em.find(Constante.class, nome);
        } catch (Exception e) {
            System.err.println(e);
        }
        return constante;
    }
    
    public List<Constante> findAll() {
        List<Constante> constantes = null;
        try {
            Query query = em.createQuery("from Constante c order by nome");

            constantes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return constantes;
    }
    
    public static String getValor(String constante){
        if(new ConstanteDAO().findByNome(constante) == null) {
            return null;
        }
        return new ConstanteDAO().findByNome(constante).getValor();
    }
    
    
    public static void alterarNome(String oldName, String newName) {
        em.getTransaction().begin();
        Query query = em.createQuery("UPDATE Constante SET nome = :newName where nome = :oldName");
        query.setParameter("oldName", oldName);
        query.setParameter("newName", newName);
        query.executeUpdate();
        em.getTransaction().commit();
    }
}
