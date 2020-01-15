/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.Constante;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ConstanteDAO {
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Constante> constantes = new ArrayList<>();
        constantes.add(new Constante("SISTEMA_ID", ""));
        constantes.add(new Constante("SISTEMA_VERSAO", "2018-01-01")); //dummy date to start
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
        
        constantes.add(new Constante("IMPRESSAO_RODAPE", ""));
        
        constantes.add(new Constante("NFSE_ALIQUOTA", "0.00"));
        constantes.add(new Constante("NFSE_CODIGO_SERVICO", ""));
        
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
        
        constantes.add(new Constante("NFE_HABILITAR", "false"));
        constantes.add(new Constante("NFE_PATH", "custom/nfe/"));
        constantes.add(new Constante("NFE_SERIE", "1"));
        constantes.add(new Constante("NFE_PROXIMO_NUMERO", "1"));
        constantes.add(new Constante("NFE_TIPO_AMBIENTE", "1"));
        constantes.add(new Constante("NFE_REGIME_TRIBUTARIO", "1"));
        constantes.add(new Constante("NFE_NATUREZA_OPERACAO", "1"));
        constantes.add(new Constante("NFE_TIPO_ATENDIMENTO", "1"));
        constantes.add(new Constante("NFE_CONSUMIDOR_FINAL", "1"));
        constantes.add(new Constante("NFE_DESTINO_OPERACAO", "1"));
        constantes.add(new Constante("NFE_INFORMACOES_ADICIONAIS_FISCO", ""));
        constantes.add(new Constante("NFE_INFORMACOES_COMPLEMENTARES_CONTRIBUINTE", ""));
        
        constantes.add(new Constante("OST_HABILITAR", "false"));
        
        constantes.add(new Constante("TO_PRINTER_PATH", "toPrinter/"));
        constantes.add(new Constante("BACKUP_PATH", "backup/"));
        
        constantes.add(new Constante("CLIENTE_LIMITE_CREDITO", "0.00"));
        
        constantes.add(new Constante("VENDA_FUNCIONARIO_POR_ITEM", "true"));
        constantes.add(new Constante("VENDA_FUNCIONARIO_POR_ITEM_PRODUTO", "true"));
        constantes.add(new Constante("VENDA_FUNCIONARIO_POR_ITEM_SERVICO", "true"));
        
        constantes.add(new Constante("VENDA_INSERCAO_DIRETA", "true"));
        constantes.add(new Constante("VENDA_NUMERO_COMANDAS", "50"));
        
        constantes.add(new Constante("VENDA_BLOQUEAR_PARCELAS_EM_ATRASO", "true"));
        constantes.add(new Constante("VENDA_BLOQUEAR_CREDITO_EXCEDIDO", "true"));
        constantes.add(new Constante("VENDA_VALIDAR_ESTOQUE", "true"));
        constantes.add(new Constante("VENDA_ALERTAR_GARANTIA_POR_VEICULO", "true"));
        
        constantes.add(new Constante("VENDA_EXIBIR_VEICULO", "true"));
        
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

        em.close();
    }
    
    public static Constante save(Constante constante) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }

        return constante;
    }
    
    public static Constante saveByNome(String nome, String valor) {
        return save(new Constante(nome, valor.trim()));
    }

    public Constante findByNome(String nome) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Constante constante = null;
        try {
            constante = em.find(Constante.class, nome);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return constante;
    }
    
    public List<Constante> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Constante> constantes = null;
        try {
            Query query = em.createQuery("from Constante c order by nome");

            constantes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
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
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try{
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Constante SET nome = :newName where nome = :oldName");
            query.setParameter("oldName", oldName);
            query.setParameter("newName", newName);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch(Exception e) {
            System.err.println("Erro em ConstanteDAO.alterarNome() " + e);
            //do nothing
        } finally {
            em.close();
        }
    }
}
