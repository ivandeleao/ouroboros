/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.Produto;
import model.bean.fiscal.Cfop;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CfopDAO {

    public Cfop save(Cfop cfop) {
        try {
            em.getTransaction().begin();
            if (cfop.getCodigo() == null) {
                em.persist(cfop);
            } else {
                em.merge(cfop);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return cfop;
    }

    public Cfop findByCodigo(Integer codigo) {
        Cfop cfop = null;
        try {
            cfop = em.find(Cfop.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        }
        return cfop;
    }

    public List<Cfop> findAll() {
        List<Cfop> cfopList = null;
        try {
            Query query = em.createQuery("from CFOP c order by codigo");

            cfopList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cfopList;
    }
    
    public List<Cfop> findAllEntradaDentroDoEstado(){
        return findAllByIntervaloDeCodigo(1000, 1999);
    }
    
    public List<Cfop> findAllEntradaForaDoEstado(){
        return findAllByIntervaloDeCodigo(2000, 2999);
    }
    
    public List<Cfop> findAllEntradaDoExterior(){
        return findAllByIntervaloDeCodigo(3000, 3999);
    }
    
    public List<Cfop> findAllSaidaDentroDoEstado(){
        return findAllByIntervaloDeCodigo(5000, 5999);
    }
    
    public List<Cfop> findAllSaidaForaDoEstado(){
        return findAllByIntervaloDeCodigo(6000, 6999);
    }
    
    public List<Cfop> findAllSaidaParaExterior(){
        return findAllByIntervaloDeCodigo(7000, 7999);
    }
    
    /**
     * Filtra por intervalo de códigos - apenas utilizáveis por nota fiscal
     * @param codigoInicial valor inclusivo (>=)
     * @param codigoFinal valor inclusivo (<=)
     * @return
     */
    public List<Cfop> findAllByIntervaloDeCodigo(int codigoInicial, int codigoFinal){
        List<Cfop> cfopList = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Cfop> q = cb.createQuery(Cfop.class);
            Root<Cfop> cfopRoot = q.from(Cfop.class);
            
            List<Predicate> predicates = new ArrayList<>();
            //intervalo de códigos - Ex: 5000 - 5999 -> saídas (vendas) dentro do estado
            predicates.add(cb.greaterThanOrEqualTo(cfopRoot.get("codigo"), (Comparable) codigoInicial));
            predicates.add(cb.lessThanOrEqualTo(cfopRoot.get("codigo"), (Comparable) codigoFinal));
            //apenas utilizáveis por nota fiscal
            predicates.add(cb.equal(cfopRoot.get("indNFe"), true));
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(cfopRoot.get("codigo")));
            
            q.select(cfopRoot).where(predicates.toArray(new Predicate[]{}));
            q.orderBy(o);
            
            TypedQuery<Cfop> query = em.createQuery(q);
            
            cfopList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cfopList;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<Cfop> cfopList = new ArrayList<>();
        //Tabela de Cfop relacionada a versão 140 da NT2015/002
        //http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=Iy/5Qol1YbE=

        cfopList.add(new Cfop(1101, "	Compra p/ industrialização ou produção rural	", true, false, false, false));
        cfopList.add(new Cfop(1102, "	Compra p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(1111, "	Compra p/ industrialização de mercadoria recebida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(1113, "	Compra p/ comercialização, de mercadoria recebida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(1116, "	Compra p/ industrialização ou produção rural originada de encomenda p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(1117, "	Compra p/ comercialização originada de encomenda p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(1118, "	Compra de mercadoria p/ comercialização pelo adquirente originário, entregue pelo vendedor remetente ao destinatário, em venda à ordem.	", true, false, false, false));
        cfopList.add(new Cfop(1120, "	Compra p/ industrialização, em venda à ordem, já recebida do vendedor remetente	", true, false, false, false));
        cfopList.add(new Cfop(1121, "	Compra p/ comercialização, em venda à ordem, já recebida do vendedor remetente	", true, false, false, false));
        cfopList.add(new Cfop(1122, "	Compra p/ industrialização em que a mercadoria foi remetida pelo fornecedor ao industrializador sem transitar pelo estabelecimento adquirente	", true, false, false, false));
        cfopList.add(new Cfop(1124, "	Industrialização efetuada por outra empresa	", true, false, false, false));
        cfopList.add(new Cfop(1125, "	Industrialização efetuada por outra empresa quando a mercadoria remetida p/ utilização no processo de industrialização não transitou pelo estabelecimento adquirente da mercadoria	", true, false, false, false));
        cfopList.add(new Cfop(1126, "	Compra p/ utilização na prestação de serviço sujeita ao ICMS	", true, false, false, false));
        cfopList.add(new Cfop(1128, "	Compra p/ utilização na prestação de serviço sujeita ao ISSQN	", true, false, false, false));
        cfopList.add(new Cfop(1151, "	Transferência p/ industrialização ou produção rural	", true, false, false, false));
        cfopList.add(new Cfop(1152, "	Transferência p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(1153, "	Transferência de energia elétrica p/ distribuição	", true, false, false, false));
        cfopList.add(new Cfop(1154, "	Transferência p/ utilização na prestação de serviço	", true, false, false, false));
        cfopList.add(new Cfop(1201, "	Devolução de venda de produção do estabelecimento 	", true, false, false, true));
        cfopList.add(new Cfop(1202, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, true));
        cfopList.add(new Cfop(1203, "	Devolução de venda de produção do estabelecimento, destinada à ZFM ou ALC	", true, false, false, true));
        cfopList.add(new Cfop(1204, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros, destinada à ZFM ou ALC	", true, false, false, true));
        cfopList.add(new Cfop(1205, "	Anulação de valor relativo à prestação de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(1206, "	Anulação de valor relativo à prestação de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(1207, "	Anulação de valor relativo à venda de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(1208, "	Devolução de produção do estabelecimento, remetida em transferência	", true, false, false, true));
        cfopList.add(new Cfop(1209, "	Devolução de mercadoria adquirida ou recebida de terceiros, remetida em transferência	", true, false, false, true));
        cfopList.add(new Cfop(1212, "	Devolução de venda no mercado interno de mercadoria industrializada e insumo importado sob o Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, true));
        cfopList.add(new Cfop(1251, "	Compra de energia elétrica p/ distribuição ou comercialização	", true, false, false, false));
        cfopList.add(new Cfop(1252, "	Compra de energia elétrica por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(1253, "	Compra de energia elétrica por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(1254, "	Compra de energia elétrica por estabelecimento prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(1255, "	Compra de energia elétrica por estabelecimento prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(1256, "	Compra de energia elétrica por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(1257, "	Compra de energia elétrica p/ consumo por demanda contratada	", true, false, false, false));
        cfopList.add(new Cfop(1301, "	Aquisição de serviço de comunicação p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(1302, "	Aquisição de serviço de comunicação por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(1303, "	Aquisição de serviço de comunicação por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(1304, "	Aquisição de serviço de comunicação por estabelecimento de prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(1305, "	Aquisição de serviço de comunicação por estabelecimento de geradora ou de distribuidora de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(1306, "	Aquisição de serviço de comunicação por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(1351, "	Aquisição de serviço de transporte p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(1352, "	Aquisição de serviço de transporte por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(1353, "	Aquisição de serviço de transporte por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(1354, "	Aquisição de serviço de transporte por estabelecimento de prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(1355, "	Aquisição de serviço de transporte por estabelecimento de geradora ou de distribuidora de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(1356, "	Aquisição de serviço de transporte por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(1360, "	Aquisição de serviço de transporte por contribuinte-substituto em relação ao serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(1401, "	Compra p/ industrialização ou produção rural de mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1403, "	Compra p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1406, "	Compra de bem p/ o ativo imobilizado cuja mercadoria está sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1407, "	Compra de mercadoria p/ uso ou consumo cuja mercadoria está sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1408, "	Transferência p/ industrialização ou produção rural de mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1409, "	Transferência p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1410, "	Devolução de venda de mercadoria, de produção do estabelecimento, sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(1411, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(1414, "	Retorno de mercadoria de produção do estabelecimento, remetida p/ venda fora do estabelecimento, sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1415, "	Retorno de mercadoria adquirida ou recebida de terceiros, remetida p/ venda fora do estabelecimento em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(1451, "	Retorno de animal do estabelecimento produtor	", true, false, false, false));
        cfopList.add(new Cfop(1452, "	Retorno de insumo não utilizado na produção	", true, false, false, false));
        cfopList.add(new Cfop(1501, "	Entrada de mercadoria recebida com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(1503, "	Entrada decorrente de devolução de produto, de fabricação do estabelecimento, remetido com fim específico de exportação	", true, false, false, true));
        cfopList.add(new Cfop(1504, "	Entrada decorrente de devolução de mercadoria remetida com fim específico de exportação, adquirida ou recebida de terceiros	", true, false, false, true));
        cfopList.add(new Cfop(1505, "	Entrada decorrente de devolução simbólica de mercadoria remetida p/ formação de lote de exportação, de produto industrializado ou produzido pelo próprio estabelecimento.	", true, false, false, true));
        cfopList.add(new Cfop(1506, "	Entrada decorrente de devolução simbólica de mercadoria, adquirida ou recebida de terceiros, remetida p/ formação de lote de exportação.	", true, false, false, true));
        cfopList.add(new Cfop(1551, "	Compra de bem p/ o ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(1552, "	Transferência de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(1553, "	Devolução de venda de bem do ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(1554, "	Retorno de bem do ativo imobilizado remetido p/ uso fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(1555, "	Entrada de bem do ativo imobilizado de terceiro, remetido p/ uso no estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(1556, "	Compra de material p/ uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(1557, "	Transferência de material p/ uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(1601, "	Recebimento, por transferência, de crédito de ICMS	", true, false, false, false));
        cfopList.add(new Cfop(1602, "	Recebimento, por transferência, de saldo credor do ICMS, de outro estabelecimento da mesma empresa, p/ compensação de saldo devedor do imposto. 	", true, false, false, false));
        cfopList.add(new Cfop(1603, "	Ressarcimento de ICMS retido por substituição tributária	", true, false, false, false));
        cfopList.add(new Cfop(1604, "	Lançamento do crédito relativo à compra de bem p/ o ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(1605, "	Recebimento, por transferência, de saldo devedor do ICMS de outro estabelecimento da mesma empresa	", true, false, false, false));
        cfopList.add(new Cfop(1651, "	Compra de combustível ou lubrificante p/ industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(1652, "	Compra de combustível ou lubrificante p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(1653, "	Compra de combustível ou lubrificante por consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(1658, "	Transferência de combustível ou lubrificante p/ industrialização	", true, false, false, false));
        cfopList.add(new Cfop(1659, "	Transferência de combustível ou lubrificante p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(1660, "	Devolução de venda de combustível ou lubrificante destinados à industrialização subseqüente	", true, false, false, true));
        cfopList.add(new Cfop(1661, "	Devolução de venda de combustível ou lubrificante destinados à comercialização	", true, false, false, true));
        cfopList.add(new Cfop(1662, "	Devolução de venda de combustível ou lubrificante destinados a consumidor ou usuário final	", true, false, false, true));
        cfopList.add(new Cfop(1663, "	Entrada de combustível ou lubrificante p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(1664, "	Retorno de combustível ou lubrificante remetidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(1901, "	Entrada p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(1902, "	Retorno de mercadoria remetida p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(1903, "	Entrada de mercadoria remetida p/ industrialização e não aplicada no referido processo	", true, false, false, false));
        cfopList.add(new Cfop(1904, "	Retorno de remessa p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(1905, "	Entrada de mercadoria recebida p/ depósito em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(1906, "	Retorno de mercadoria remetida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(1907, "	Retorno simbólico de mercadoria remetida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(1908, "	Entrada de bem por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(1909, "	Retorno de bem remetido por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(1910, "	Entrada de bonificação, doação ou brinde	", true, false, false, false));
        cfopList.add(new Cfop(1911, "	Entrada de amostra grátis	", true, false, false, false));
        cfopList.add(new Cfop(1912, "	Entrada de mercadoria ou bem recebido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(1913, "	Retorno de mercadoria ou bem remetido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(1914, "	Retorno de mercadoria ou bem remetido p/ exposição ou feira	", true, false, false, false));
        cfopList.add(new Cfop(1915, "	Entrada de mercadoria ou bem recebido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(1916, "	Retorno de mercadoria ou bem remetido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(1917, "	Entrada de mercadoria recebida em consignação mercantil ou industrial	", true, false, false, false));
        cfopList.add(new Cfop(1918, "	Devolução de mercadoria remetida em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(1919, "	Devolução simbólica de mercadoria vendida ou utilizada em processo industrial, remetida anteriormente em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(1920, "	Entrada de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(1921, "	Retorno de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(1922, "	Lançamento efetuado a título de simples faturamento decorrente de compra p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(1923, "	Entrada de mercadoria recebida do vendedor remetente, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(1924, "	Entrada p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(1925, "	Retorno de mercadoria remetida p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(1926, "	Lançamento efetuado a título de reclassificação de mercadoria decorrente de formação de kit ou de sua desagregação	", true, false, false, false));
        cfopList.add(new Cfop(1931, "	Lançamento efetuado pelo tomador do serviço de transporte, quando a responsabilidade de retenção do imposto for atribuída ao remetente ou alienante da mercadoria, pelo serviço de transporte realizado por transportador autônomo ou por transportador não-inscrito na UF onde se tenha iniciado o serviço.	", true, false, false, false));
        cfopList.add(new Cfop(1932, "	Aquisição de serviço de transporte iniciado em UF diversa daquela onde esteja inscrito o prestador	", true, false, false, false));
        cfopList.add(new Cfop(1933, "	Aquisição de serviço tributado pelo Imposto sobre Serviços de Qualquer Natureza	", true, false, false, false));
        cfopList.add(new Cfop(1934, "	Entrada simbólica de mercadoria recebida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(1949, "	Outra entrada de mercadoria ou prestação de serviço não especificada	", true, false, false, false));
        cfopList.add(new Cfop(2101, "	Compra p/ industrialização ou produção rural	", true, false, false, false));
        cfopList.add(new Cfop(2102, "	Compra p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(2111, "	Compra p/ industrialização de mercadoria recebida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(2113, "	Compra p/ comercialização, de mercadoria recebida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(2116, "	Compra p/ industrialização ou produção rural originada de encomenda p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(2117, "	Compra p/ comercialização originada de encomenda p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(2118, "	Compra de mercadoria p/ comercialização pelo adquirente originário, entregue pelo vendedor remetente ao destinatário, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(2120, "	Compra p/ industrialização, em venda à ordem, já recebida do vendedor remetente	", true, false, false, false));
        cfopList.add(new Cfop(2121, "	Compra p/ comercialização, em venda à ordem, já recebida do vendedor remetente	", true, false, false, false));
        cfopList.add(new Cfop(2122, "	Compra p/ industrialização em que a mercadoria foi remetida pelo fornecedor ao industrializador sem transitar pelo estabelecimento adquirente	", true, false, false, false));
        cfopList.add(new Cfop(2124, "	Industrialização efetuada por outra empresa	", true, false, false, false));
        cfopList.add(new Cfop(2125, "	Industrialização efetuada por outra empresa quando a mercadoria remetida p/ utilização no processo de industrialização não transitou pelo estabelecimento adquirente da mercadoria	", true, false, false, false));
        cfopList.add(new Cfop(2126, "	Compra p/ utilização na prestação de serviço sujeita ao ICMS	", true, false, false, false));
        cfopList.add(new Cfop(2128, "	Compra p/ utilização na prestação de serviço sujeita ao ISSQN	", true, false, false, false));
        cfopList.add(new Cfop(2151, "	Transferência p/ industrialização ou produção rural	", true, false, false, false));
        cfopList.add(new Cfop(2152, "	Transferência p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(2153, "	Transferência de energia elétrica p/ distribuição	", true, false, false, false));
        cfopList.add(new Cfop(2154, "	Transferência p/ utilização na prestação de serviço	", true, false, false, false));
        cfopList.add(new Cfop(2201, "	Devolução de venda de produção do estabelecimento	", true, false, false, true));
        cfopList.add(new Cfop(2202, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, true));
        cfopList.add(new Cfop(2203, "	Devolução de venda de produção do estabelecimento destinada à ZFM ou ALC	", true, false, false, true));
        cfopList.add(new Cfop(2204, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros, destinada à ZFM ou ALC	", true, false, false, true));
        cfopList.add(new Cfop(2205, "	Anulação de valor relativo à prestação de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(2206, "	Anulação de valor relativo à prestação de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(2207, "	Anulação de valor relativo à venda de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(2208, "	Devolução de produção do estabelecimento, remetida em transferência.	", true, false, false, true));
        cfopList.add(new Cfop(2209, "	Devolução de mercadoria adquirida ou recebida de terceiros e remetida em transferência	", true, false, false, true));
        cfopList.add(new Cfop(2212, "	Devolução de venda no mercado interno de mercadoria industrializada e insumo importado sob o Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, true));
        cfopList.add(new Cfop(2251, "	Compra de energia elétrica p/ distribuição ou comercialização	", true, false, false, false));
        cfopList.add(new Cfop(2252, "	Compra de energia elétrica por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(2253, "	Compra de energia elétrica por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(2254, "	Compra de energia elétrica por estabelecimento prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(2255, "	Compra de energia elétrica por estabelecimento prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(2256, "	Compra de energia elétrica por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(2257, "	Compra de energia elétrica p/ consumo por demanda contratada	", true, false, false, false));
        cfopList.add(new Cfop(2301, "	Aquisição de serviço de comunicação p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(2302, "	Aquisição de serviço de comunicação por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(2303, "	Aquisição de serviço de comunicação por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(2304, "	Aquisição de serviço de comunicação por estabelecimento de prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(2305, "	Aquisição de serviço de comunicação por estabelecimento de geradora ou de distribuidora de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(2306, "	Aquisição de serviço de comunicação por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(2351, "	Aquisição de serviço de transporte p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(2352, "	Aquisição de serviço de transporte por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(2353, "	Aquisição de serviço de transporte por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(2354, "	Aquisição de serviço de transporte por estabelecimento de prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(2355, "	Aquisição de serviço de transporte por estabelecimento de geradora ou de distribuidora de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(2356, "	Aquisição de serviço de transporte por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(2401, "	Compra p/ industrialização ou produção rural de mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2403, "	Compra p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2406, "	Compra de bem p/ o ativo imobilizado cuja mercadoria está sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2407, "	Compra de mercadoria p/ uso ou consumo cuja mercadoria está sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2408, "	Transferência p/ industrialização ou produção rural de mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2409, "	Transferência p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2410, "	Devolução de venda de produção do estabelecimento, quando o produto sujeito a ST	", true, false, false, true));
        cfopList.add(new Cfop(2411, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(2414, "	Retorno de produção do estabelecimento, remetida p/ venda fora do estabelecimento, quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(2415, "	Retorno de mercadoria adquirida ou recebida de terceiros, remetida p/ venda fora do estabelecimento em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(2501, "	Entrada de mercadoria recebida com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(2503, "	Entrada decorrente de devolução de produto industrializado pelo estabelecimento, remetido com fim específico de exportação	", true, false, false, true));
        cfopList.add(new Cfop(2504, "	Entrada decorrente de devolução de mercadoria remetida com fim específico de exportação, adquirida ou recebida de terceiros	", true, false, false, true));
        cfopList.add(new Cfop(2505, "	Entrada decorrente de devolução simbólica de mercadoria remetida p/ formação de lote de exportação, de produto industrializado ou produzido pelo próprio estabelecimento.	", true, false, false, true));
        cfopList.add(new Cfop(2506, "	Entrada decorrente de devolução simbólica de mercadoria, adquirida ou recebida de terceiros, remetida p/ formação de lote de exportação.	", true, false, false, true));
        cfopList.add(new Cfop(2551, "	Compra de bem p/ o ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(2552, "	Transferência de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(2553, "	Devolução de venda de bem do ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(2554, "	Retorno de bem do ativo imobilizado remetido p/ uso fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(2555, "	Entrada de bem do ativo imobilizado de terceiro, remetido p/ uso no estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(2556, "	Compra de material p/ uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(2557, "	Transferência de material p/ uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(2603, "	Ressarcimento de ICMS retido por substituição tributária	", true, false, false, false));
        cfopList.add(new Cfop(2651, "	Compra de combustível ou lubrificante p/ industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(2652, "	Compra de combustível ou lubrificante p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(2652, "	Compra de combustível ou lubrificante p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(2653, "	Compra de combustível ou lubrificante por consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(2658, "	Transferência de combustível ou lubrificante p/ industrialização	", true, false, false, false));
        cfopList.add(new Cfop(2659, "	Transferência de combustível ou lubrificante p/ comercialização 	", true, false, false, false));
        cfopList.add(new Cfop(2660, "	Devolução de venda de combustível ou lubrificante destinados à industrialização subseqüente	", true, false, false, true));
        cfopList.add(new Cfop(2661, "	Devolução de venda de combustível ou lubrificante destinados à comercialização	", true, false, false, true));
        cfopList.add(new Cfop(2662, "	Devolução de venda de combustível ou lubrificante destinados a consumidor ou usuário final	", true, false, false, true));
        cfopList.add(new Cfop(2663, "	Entrada de combustível ou lubrificante p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(2664, "	Retorno de combustível ou lubrificante remetidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(2901, "	Entrada p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(2902, "	Retorno de mercadoria remetida p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(2903, "	Entrada de mercadoria remetida p/ industrialização e não aplicada no referido processo	", true, false, false, false));
        cfopList.add(new Cfop(2904, "	Retorno de remessa p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(2905, "	Entrada de mercadoria recebida p/ depósito em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(2906, "	Retorno de mercadoria remetida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(2907, "	Retorno simbólico de mercadoria remetida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(2908, "	Entrada de bem por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(2909, "	Retorno de bem remetido por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(2910, "	Entrada de bonificação, doação ou brinde	", true, false, false, false));
        cfopList.add(new Cfop(2911, "	Entrada de amostra grátis	", true, false, false, false));
        cfopList.add(new Cfop(2912, "	Entrada de mercadoria ou bem recebido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(2913, "	Retorno de mercadoria ou bem remetido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(2914, "	Retorno de mercadoria ou bem remetido p/ exposição ou feira	", true, false, false, false));
        cfopList.add(new Cfop(2915, "	Entrada de mercadoria ou bem recebido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(2916, "	Retorno de mercadoria ou bem remetido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(2917, "	Entrada de mercadoria recebida em consignação mercantil ou industrial	", true, false, false, false));
        cfopList.add(new Cfop(2918, "	Devolução de mercadoria remetida em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(2919, "	Devolução simbólica de mercadoria vendida ou utilizada em processo industrial, remetida anteriormente em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(2920, "	Entrada de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(2921, "	Retorno de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(2922, "	Lançamento efetuado a título de simples faturamento decorrente de compra p/ recebimento futuro	", true, false, false, false));
        cfopList.add(new Cfop(2923, "	Entrada de mercadoria recebida do vendedor remetente, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(2924, "	Entrada p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(2925, "	Retorno de mercadoria remetida p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(2931, "	Lançamento efetuado pelo tomador do serviço de transporte, quando a responsabilidade de retenção do imposto for atribuída ao remetente ou alienante da mercadoria, pelo serviço de transporte realizado por transportador autônomo ou por transportador não-inscrito na UF onde se tenha iniciado o serviço  	", true, false, false, false));
        cfopList.add(new Cfop(2932, "	Aquisição de serviço de transporte iniciado em UF diversa daquela onde esteja inscrito o prestador 	", true, false, false, false));
        cfopList.add(new Cfop(2933, "	Aquisição de serviço tributado pelo Imposto Sobre Serviços de Qualquer Natureza	", true, false, false, false));
        cfopList.add(new Cfop(2934, "	Entrada simbólica de mercadoria recebida p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(2949, "	Outra entrada de mercadoria ou prestação de serviço não especificado	", true, false, false, false));
        cfopList.add(new Cfop(3101, "	Compra p/ industrialização ou produção rural	", true, false, false, false));
        cfopList.add(new Cfop(3102, "	Compra p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(3126, "	Compra p/ utilização na prestação de serviço sujeita ao ICMS	", true, false, false, false));
        cfopList.add(new Cfop(3127, "	Compra p/ industrialização sob o regime de drawback 	", true, false, false, false));
        cfopList.add(new Cfop(3128, "	Compra p/ utilização na prestação de serviço sujeita ao ISSQN	", true, false, false, false));
        cfopList.add(new Cfop(3129, "	Compra para industrialização sob o Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, false));
        cfopList.add(new Cfop(3201, "	Devolução de venda de produção do estabelecimento	", true, false, false, true));
        cfopList.add(new Cfop(3202, "	Devolução de venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, true));
        cfopList.add(new Cfop(3205, "	Anulação de valor relativo à prestação de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(3206, "	Anulação de valor relativo à prestação de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(3207, "	Anulação de valor relativo à venda de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(3211, "	Devolução de venda de produção do estabelecimento sob o regime de drawback 	", true, false, false, true));
        cfopList.add(new Cfop(3212, "	Devolução de venda no mercado externo de mercadoria industrializada sob o Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, true));
        cfopList.add(new Cfop(3251, "	Compra de energia elétrica p/ distribuição ou comercialização	", true, false, false, false));
        cfopList.add(new Cfop(3301, "	Aquisição de serviço de comunicação p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(3351, "	Aquisição de serviço de transporte p/ execução de serviço da mesma natureza	", true, false, false, false));
        cfopList.add(new Cfop(3352, "	Aquisição de serviço de transporte por estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(3353, "	Aquisição de serviço de transporte por estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(3354, "	Aquisição de serviço de transporte por estabelecimento de prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(3355, "	Aquisição de serviço de transporte por estabelecimento de geradora ou de distribuidora de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(3356, "	Aquisição de serviço de transporte por estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(3503, "	Devolução de mercadoria exportada que tenha sido recebida com fim específico de exportação	", true, false, false, true));
        cfopList.add(new Cfop(3551, "	Compra de bem p/ o ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(3553, "	Devolução de venda de bem do ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(3556, "	Compra de material p/ uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(3651, "	Compra de combustível ou lubrificante p/ industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(3652, "	Compra de combustível ou lubrificante p/ comercialização	", true, false, false, false));
        cfopList.add(new Cfop(3653, "	Compra de combustível ou lubrificante por consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(3930, "	Lançamento efetuado a título de entrada de bem sob amparo de regime especial aduaneiro de admissão temporária	", true, false, false, false));
        cfopList.add(new Cfop(3949, "	Outra entrada de mercadoria ou prestação de serviço não especificado	", true, false, false, false));
        cfopList.add(new Cfop(5101, "	Venda de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5102, "	Venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(5103, "	Venda de produção do estabelecimento efetuada fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5104, "	Venda de mercadoria adquirida ou recebida de terceiros, efetuada fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5105, "	Venda de produção do estabelecimento que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(5106, "	Venda de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar 	", true, false, false, false));
        cfopList.add(new Cfop(5109, "	Venda de produção do estabelecimento destinada à ZFM ou ALC	", true, false, false, false));
        cfopList.add(new Cfop(5110, "	Venda de mercadoria, adquirida ou recebida de terceiros, destinada à ZFM ou ALC	", true, false, false, false));
        cfopList.add(new Cfop(5111, "	Venda de produção do estabelecimento remetida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(5112, "	Venda de mercadoria adquirida ou recebida de terceiros remetida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(5113, "	Venda de produção do estabelecimento remetida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(5114, "	Venda de mercadoria adquirida ou recebida de terceiros remetida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(5115, "	Venda de mercadoria adquirida ou recebida de terceiros, recebida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(5116, "	Venda de produção do estabelecimento originada de encomenda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(5117, "	Venda de mercadoria adquirida ou recebida de terceiros, originada de encomenda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(5118, "	Venda de produção do estabelecimento entregue ao destinatário por conta e ordem do adquirente originário, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(5119, "	Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário por conta e ordem do adquirente originário, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(5120, "	Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário pelo vendedor remetente, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(5122, "	Venda de produção do estabelecimento remetida p/ industrialização, por conta e ordem do adquirente, sem transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(5123, "	Venda de mercadoria adquirida ou recebida de terceiros remetida p/ industrialização, por conta e ordem do adquirente, sem transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(5124, "	Industrialização efetuada p/ outra empresa	", true, false, false, false));
        cfopList.add(new Cfop(5125, "	Industrialização efetuada p/ outra empresa quando a mercadoria recebida p/ utilização no processo de industrialização não transitar pelo estabelecimento adquirente da mercadoria	", true, false, false, false));
        cfopList.add(new Cfop(5129, "	Venda de insumo importado e de mercadoria industrializada sob o amparo do Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, false));
        cfopList.add(new Cfop(5151, "	Venda de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5152, "	Transferência de mercadoria adquirida ou recebida de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(5153, "	Transferência de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(5155, "	Transferência de produção do estabelecimento, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(5156, "	Transferência de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(5201, "	Devolução de compra p/ industrialização ou produção rural	", true, false, false, true));
        cfopList.add(new Cfop(5202, "	Devolução de compra p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(5205, "	Anulação de valor relativo a aquisição de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(5206, "	Anulação de valor relativo a aquisição de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(5207, "	Anulação de valor relativo à compra de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(5208, "	Devolução de mercadoria recebida em transferência p/ industrialização ou produção rural 	", true, false, false, true));
        cfopList.add(new Cfop(5209, "	Devolução de mercadoria recebida em transferência p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(5210, "	Devolução de compra p/ utilização na prestação de serviço	", true, false, false, true));
        cfopList.add(new Cfop(5251, "	Venda de energia elétrica p/ distribuição ou comercialização	", true, false, false, false));
        cfopList.add(new Cfop(5252, "	Venda de energia elétrica p/ estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(5253, "	Venda de energia elétrica p/ estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(5254, "	Venda de energia elétrica p/ estabelecimento prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(5255, "	Venda de energia elétrica p/ estabelecimento prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(5256, "	Venda de energia elétrica p/ estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(5257, "	Venda de energia elétrica p/ consumo por demanda contratada	", true, false, false, false));
        cfopList.add(new Cfop(5258, "	Venda de energia elétrica a não contribuinte	", true, false, false, false));
        cfopList.add(new Cfop(5301, "	Prestação de serviço de comunicação p/ execução de serviço da mesma natureza	", false, true, false, false));
        cfopList.add(new Cfop(5302, "	Prestação de serviço de comunicação a estabelecimento industrial	", false, true, false, false));
        cfopList.add(new Cfop(5303, "	Prestação de serviço de comunicação a estabelecimento comercial	", false, true, false, false));
        cfopList.add(new Cfop(5304, "	Prestação de serviço de comunicação a estabelecimento de prestador de serviço de transporte	", false, true, false, false));
        cfopList.add(new Cfop(5305, "	Prestação de serviço de comunicação a estabelecimento de geradora ou de distribuidora de energia elétrica	", false, true, false, false));
        cfopList.add(new Cfop(5306, "	Prestação de serviço de comunicação a estabelecimento de produtor rural	", false, true, false, false));
        cfopList.add(new Cfop(5307, "	Prestação de serviço de comunicação a não contribuinte	", false, true, false, false));
        cfopList.add(new Cfop(5351, "	Prestação de serviço de transporte p/ execução de serviço da mesma natureza	", false, false, true, false));
        cfopList.add(new Cfop(5352, "	Prestação de serviço de transporte a estabelecimento industrial	", false, false, true, false));
        cfopList.add(new Cfop(5353, "	Prestação de serviço de transporte a estabelecimento comercial	", false, false, true, false));
        cfopList.add(new Cfop(5354, "	Prestação de serviço de transporte a estabelecimento de prestador de serviço de comunicação	", false, false, true, false));
        cfopList.add(new Cfop(5355, "	Prestação de serviço de transporte a estabelecimento de geradora ou de distribuidora de energia elétrica	", false, false, true, false));
        cfopList.add(new Cfop(5356, "	Prestação de serviço de transporte a estabelecimento de produtor rural	", false, false, true, false));
        cfopList.add(new Cfop(5357, "	Prestação de serviço de transporte a não contribuinte	", false, false, true, false));
        cfopList.add(new Cfop(5359, "	Prestação de serviço de transporte a contribuinte ou a não-contribuinte, quando a mercadoria transportada esteja dispensada de emissão de Nota Fiscal  	", false, false, true, false));
        cfopList.add(new Cfop(5360, "	Prestação de serviço de transporte a contribuinte-substituto em relação ao serviço de transporte	", false, false, true, false));
        cfopList.add(new Cfop(5401, "	Venda de produção do estabelecimento quando o produto esteja sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(5402, "	Venda de produção do estabelecimento de produto sujeito a ST, em operação entre contribuintes substitutos do mesmo produto	", true, false, false, false));
        cfopList.add(new Cfop(5403, "	Venda de mercadoria, adquirida ou recebida de terceiros, sujeita a ST, na condição de contribuinte-substituto	", true, false, false, false));
        cfopList.add(new Cfop(5405, "	Venda de mercadoria, adquirida ou recebida de terceiros, sujeita a ST, na condição de contribuinte-substituído	", true, false, false, false));
        cfopList.add(new Cfop(5408, "	Transferência de produção do estabelecimento quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(5409, "	Transferência de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(5410, "	Devolução de compra p/ industrialização de mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(5411, "	Devolução de compra p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(5412, "	Devolução de bem do ativo imobilizado, em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(5413, "	Devolução de mercadoria destinada ao uso ou consumo, em operação com mercadoria sujeita a ST.	", true, false, false, true));
        cfopList.add(new Cfop(5414, "	Remessa de produção do estabelecimento p/ venda fora do estabelecimento, quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(5415, "	Remessa de mercadoria adquirida ou recebida de terceiros p/ venda fora do estabelecimento, em operação com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(5451, "	Remessa de animal e de insumo p/ estabelecimento produtor	", true, false, false, false));
        cfopList.add(new Cfop(5501, "	Remessa de produção do estabelecimento, com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(5502, "	Remessa de mercadoria adquirida ou recebida de terceiros, com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(5503, "	Devolução de mercadoria recebida com fim específico de exportação	", true, false, false, true));
        cfopList.add(new Cfop(5504, "	Remessa de mercadoria p/ formação de lote de exportação, de produto industrializado ou produzido pelo próprio estabelecimento.	", true, false, false, false));
        cfopList.add(new Cfop(5505, "	Remessa de mercadoria, adquirida ou recebida de terceiros, p/ formação de lote de exportação.	", true, false, false, false));
        cfopList.add(new Cfop(5551, "	Venda de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(5552, "	Transferência de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(5553, "	Devolução de compra de bem p/ o ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(5554, "	Remessa de bem do ativo imobilizado p/ uso fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5555, "	Devolução de bem do ativo imobilizado de terceiro, recebido p/ uso no estabelecimento	", true, false, false, true));
        cfopList.add(new Cfop(5556, "	Devolução de compra de material de uso ou consumo	", true, false, false, true));
        cfopList.add(new Cfop(5557, "	Transferência de material de uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(5601, "	Transferência de crédito de ICMS acumulado	", true, false, false, false));
        cfopList.add(new Cfop(5602, "	Transferência de saldo credor do ICMS, p/ outro estabelecimento da mesma empresa, destinado à compensação de saldo devedor do ICMS	", true, false, false, false));
        cfopList.add(new Cfop(5603, "	Ressarcimento de ICMS retido por substituição tributária	", true, false, false, false));
        cfopList.add(new Cfop(5605, "	Transferência de saldo devedor do ICMS de outro estabelecimento da mesma empresa  	", true, false, false, false));
        cfopList.add(new Cfop(5606, "	Utilização de saldo credor do ICMS p/ extinção por compensação de débitos fiscais	", true, false, false, false));
        cfopList.add(new Cfop(5651, "	Venda de combustível ou lubrificante de produção do estabelecimento destinados à industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(5652, "	Venda de combustível ou lubrificante, de produção do estabelecimento, destinados à comercialização	", true, false, false, false));
        cfopList.add(new Cfop(5653, "	Venda de combustível ou lubrificante, de produção do estabelecimento, destinados a consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(5654, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados à industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(5655, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados à comercialização	", true, false, false, false));
        cfopList.add(new Cfop(5656, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados a consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(5657, "	Remessa de combustível ou lubrificante, adquiridos ou recebidos de terceiros, p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5658, "	Transferência de combustível ou lubrificante de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5659, "	Transferência de combustível ou lubrificante adquiridos ou recebidos de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(5660, "	Devolução de compra de combustível ou lubrificante adquiridos p/ industrialização subseqüente	", true, false, false, true));
        cfopList.add(new Cfop(5661, "	Devolução de compra de combustível ou lubrificante adquiridos p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(5662, "	Devolução de compra de combustível ou lubrificante adquiridos por consumidor ou usuário final	", true, false, false, true));
        cfopList.add(new Cfop(5663, "	Remessa p/ armazenagem de combustível ou lubrificante	", true, false, false, false));
        cfopList.add(new Cfop(5664, "	Retorno de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(5665, "	Retorno simbólico de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(5666, "	Remessa, por conta e ordem de terceiros, de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(5667, "	Venda de combustível ou lubrificante a consumidor ou usuário final estabelecido em outra UF	", true, false, false, false));
        cfopList.add(new Cfop(5901, "	Remessa p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(5902, "	Retorno de mercadoria utilizada na industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(5903, "	Retorno de mercadoria recebida p/ industrialização e não aplicada no referido processo	", true, false, false, false));
        cfopList.add(new Cfop(5904, "	Remessa p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(5905, "	Remessa p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(5906, "	Retorno de mercadoria depositada em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(5907, "	Retorno simbólico de mercadoria depositada em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(5908, "	Remessa de bem por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(5909, "	Retorno de bem recebido por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(5910, "	Remessa em bonificação, doação ou brinde	", true, false, false, false));
        cfopList.add(new Cfop(5911, "	Remessa de amostra grátis	", true, false, false, false));
        cfopList.add(new Cfop(5912, "	Remessa de mercadoria ou bem p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(5913, "	Retorno de mercadoria ou bem recebido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(5914, "	Remessa de mercadoria ou bem p/ exposição ou feira	", true, false, false, false));
        cfopList.add(new Cfop(5915, "	Remessa de mercadoria ou bem p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(5916, "	Retorno de mercadoria ou bem recebido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(5917, "	Remessa de mercadoria em consignação mercantil ou industrial	", true, false, false, false));
        cfopList.add(new Cfop(5918, "	Devolução de mercadoria recebida em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(5919, "	Devolução simbólica de mercadoria vendida ou utilizada em processo industrial, recebida anteriormente em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(5920, "	Remessa de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(5921, "	Devolução de vasilhame ou sacaria	", true, false, false, true));
        cfopList.add(new Cfop(5922, "	Lançamento efetuado a título de simples faturamento decorrente de venda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(5923, "	Remessa de mercadoria por conta e ordem de terceiros, em venda à ordem ou em operações com armazém geral ou depósito fechado.	", true, false, false, false));
        cfopList.add(new Cfop(5924, "	Remessa p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(5925, "	Retorno de mercadoria recebida p/ industrialização por conta e ordem do adquirente da mercadoria, quando aquela não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(5926, "	Lançamento efetuado a título de reclassificação de mercadoria decorrente de formação de kit ou de sua desagregação	", true, false, false, false));
        cfopList.add(new Cfop(5927, "	Lançamento efetuado a título de baixa de estoque decorrente de perda, roubo ou deterioração	", true, false, false, false));
        cfopList.add(new Cfop(5928, "	Lançamento efetuado a título de baixa de estoque decorrente do encerramento da atividade da empresa	", true, false, false, false));
        cfopList.add(new Cfop(5929, "	Lançamento efetuado em decorrência de emissão de documento fiscal relativo a operação ou prestação também registrada em equipamento Emissor de Cupom Fiscal - ECF	", true, false, false, false));
        cfopList.add(new Cfop(5931, "	Lançamento efetuado em decorrência da responsabilidade de retenção do imposto por substituição tributária, atribuída ao remetente ou alienante da mercadoria, pelo serviço de transporte realizado por transportador autônomo ou por transportador não inscrito na UF onde iniciado o serviço	", true, false, true, false));
        cfopList.add(new Cfop(5932, "	Prestação de serviço de transporte iniciada em UF diversa daquela onde inscrito o prestador	", true, false, true, false));
        cfopList.add(new Cfop(5933, "	Prestação de serviço tributado pelo Imposto Sobre Serviços de Qualquer Natureza	", true, false, false, false));
        cfopList.add(new Cfop(5934, "	Remessa simbólica de mercadoria depositada em armazém geral ou depósito fechado.	", true, false, false, false));
        cfopList.add(new Cfop(5949, "	Outra saída de mercadoria ou prestação de serviço não especificado	", true, false, false, false));
        cfopList.add(new Cfop(6101, "	Venda de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6102, "	Venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(6103, "	Venda de produção do estabelecimento, efetuada fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6104, "	Venda de mercadoria adquirida ou recebida de terceiros, efetuada fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6105, "	Venda de produção do estabelecimento que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(6106, "	Venda de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(6107, "	Venda de produção do estabelecimento, destinada a não contribuinte	", true, false, false, false));
        cfopList.add(new Cfop(6108, "	Venda de mercadoria adquirida ou recebida de terceiros, destinada a não contribuinte	", true, false, false, false));
        cfopList.add(new Cfop(6109, "	Venda de produção do estabelecimento destinada à ZFM ou ALC	", true, false, false, false));
        cfopList.add(new Cfop(6110, "	Venda de mercadoria, adquirida ou recebida de terceiros, destinada à ZFM ou ALC	", true, false, false, false));
        cfopList.add(new Cfop(6111, "	Venda de produção do estabelecimento remetida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(6112, "	Venda de mercadoria adquirida ou recebida de Terceiros remetida anteriormente em consignação industrial	", true, false, false, false));
        cfopList.add(new Cfop(6113, "	Venda de produção do estabelecimento remetida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(6114, "	Venda de mercadoria adquirida ou recebida de terceiros remetida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(6115, "	Venda de mercadoria adquirida ou recebida de terceiros, recebida anteriormente em consignação mercantil	", true, false, false, false));
        cfopList.add(new Cfop(6116, "	Venda de produção do estabelecimento originada de encomenda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(6117, "	Venda de mercadoria adquirida ou recebida de terceiros, originada de encomenda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(6118, "	Venda de produção do estabelecimento entregue ao destinatário por conta e ordem do adquirente originário, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(6119, "	Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário por conta e ordem do adquirente originário, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(6120, "	Venda de mercadoria adquirida ou recebida de terceiros entregue ao destinatário pelo vendedor remetente, em venda à ordem	", true, false, false, false));
        cfopList.add(new Cfop(6122, "	Venda de produção do estabelecimento remetida p/ industrialização, por conta e ordem do adquirente, sem transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(6123, "	Venda de mercadoria adquirida ou recebida de terceiros remetida p/ industrialização, por conta e ordem do adquirente, sem transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(6124, "	Industrialização efetuada p/ outra empresa	", true, false, false, false));
        cfopList.add(new Cfop(6125, "	Industrialização efetuada p/ outra empresa quando a mercadoria recebida p/ utilização no processo de industrialização não transitar pelo estabelecimento adquirente da mercadoria	", true, false, false, false));
        cfopList.add(new Cfop(6129, "	Venda de insumo importado e de mercadoria industrializada sob o amparo do Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, false));
        cfopList.add(new Cfop(6151, "	Transferência de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6152, "	Transferência de mercadoria adquirida ou recebida de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(6153, "	Transferência de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(6155, "	Transferência de produção do estabelecimento, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(6156, "	Transferência de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(6201, "	Devolução de compra p/ industrialização ou produção rural	", true, false, false, true));
        cfopList.add(new Cfop(6202, "	Devolução de compra p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(6205, "	Anulação de valor relativo a aquisição de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(6206, "	Anulação de valor relativo a aquisição de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(6207, "	Anulação de valor relativo à compra de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(6208, "	Devolução de mercadoria recebida em transferência p/ industrialização ou produção rural 	", true, false, false, true));
        cfopList.add(new Cfop(6209, "	Devolução de mercadoria recebida em transferência p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(6210, "	Devolução de compra p/ utilização na prestação de serviço	", true, false, false, true));
        cfopList.add(new Cfop(6251, "	Venda de energia elétrica p/ distribuição ou comercialização	", true, false, false, false));
        cfopList.add(new Cfop(6252, "	Venda de energia elétrica p/ estabelecimento industrial	", true, false, false, false));
        cfopList.add(new Cfop(6253, "	Venda de energia elétrica p/ estabelecimento comercial	", true, false, false, false));
        cfopList.add(new Cfop(6254, "	Venda de energia elétrica p/ estabelecimento prestador de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(6255, "	Venda de energia elétrica p/ estabelecimento prestador de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(6256, "	Venda de energia elétrica p/ estabelecimento de produtor rural	", true, false, false, false));
        cfopList.add(new Cfop(6257, "	Venda de energia elétrica p/ consumo por demanda contratada	", true, false, false, false));
        cfopList.add(new Cfop(6258, "	Venda de energia elétrica a não contribuinte	", true, false, false, false));
        cfopList.add(new Cfop(6301, "	Prestação de serviço de comunicação p/ execução de serviço da mesma natureza	", false, true, false, false));
        cfopList.add(new Cfop(6302, "	Prestação de serviço de comunicação a estabelecimento industrial	", false, true, false, false));
        cfopList.add(new Cfop(6303, "	Prestação de serviço de comunicação a estabelecimento comercial	", false, true, false, false));
        cfopList.add(new Cfop(6304, "	Prestação de serviço de comunicação a estabelecimento de prestador de serviço de transporte	", false, true, false, false));
        cfopList.add(new Cfop(6305, "	Prestação de serviço de comunicação a estabelecimento de geradora ou de distribuidora de energia elétrica	", false, true, false, false));
        cfopList.add(new Cfop(6306, "	Prestação de serviço de comunicação a estabelecimento de produtor rural	", false, true, false, false));
        cfopList.add(new Cfop(6307, "	Prestação de serviço de comunicação a não contribuinte	", false, true, false, false));
        cfopList.add(new Cfop(6351, "	Prestação de serviço de transporte p/ execução de serviço da mesma natureza	", false, false, true, false));
        cfopList.add(new Cfop(6352, "	Prestação de serviço de transporte a estabelecimento industrial	", false, false, true, false));
        cfopList.add(new Cfop(6353, "	Prestação de serviço de transporte a estabelecimento comercial	", false, false, true, false));
        cfopList.add(new Cfop(6354, "	Prestação de serviço de transporte a estabelecimento de prestador de serviço de comunicação	", false, false, true, false));
        cfopList.add(new Cfop(6355, "	Prestação de serviço de transporte a estabelecimento de geradora ou de distribuidora de energia elétrica	", false, false, true, false));
        cfopList.add(new Cfop(6356, "	Prestação de serviço de transporte a estabelecimento de produtor rural	", false, false, true, false));
        cfopList.add(new Cfop(6357, "	Prestação de serviço de transporte a não contribuinte	", false, false, true, false));
        cfopList.add(new Cfop(6359, "	Prestação de serviço de transporte a contribuinte ou a não-contribuinte, quando a mercadoria transportada esteja dispensada de emissão de Nota Fiscal  	", false, false, true, false));
        cfopList.add(new Cfop(6360, "	Prestação de serviço de transporte a contribuinte substituto em relação ao serviço de transporte  	", false, false, true, false));
        cfopList.add(new Cfop(6401, "	Venda de produção do estabelecimento quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(6402, "	Venda de produção do estabelecimento de produto sujeito a ST, em operação entre contribuintes substitutos do mesmo produto	", true, false, false, false));
        cfopList.add(new Cfop(6403, "	Venda de mercadoria adquirida ou recebida de terceiros em operação com mercadoria sujeita a ST, na condição de contribuinte substituto	", true, false, false, false));
        cfopList.add(new Cfop(6404, "	Venda de mercadoria sujeita a ST, cujo imposto já tenha sido retido anteriormente	", true, false, false, false));
        cfopList.add(new Cfop(6408, "	Transferência de produção do estabelecimento quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(6409, "	Transferência de mercadoria adquirida ou recebida de terceiros, sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(6410, "	Devolução de compra p/ industrialização ou ptrodução rural quando a mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(6411, "	Devolução de compra p/ comercialização em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(6412, "	Devolução de bem do ativo imobilizado, em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(6413, "	Devolução de mercadoria destinada ao uso ou consumo, em operação com mercadoria sujeita a ST	", true, false, false, true));
        cfopList.add(new Cfop(6414, "	Remessa de produção do estabelecimento p/ venda fora do estabelecimento, quando o produto sujeito a ST	", true, false, false, false));
        cfopList.add(new Cfop(6415, "	Remessa de mercadoria adquirida ou recebida de terceiros p/ venda fora do estabelecimento, quando a referida ração com mercadoria sujeita a ST	", true, false, false, false));
        cfopList.add(new Cfop(6501, "	Remessa de produção do estabelecimento, com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(6502, "	Remessa de mercadoria adquirida ou recebida de terceiros, com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(6503, "	Devolução de mercadoria recebida com fim específico de exportação	", true, false, false, true));
        cfopList.add(new Cfop(6504, "	Remessa de mercadoria p/ formação de lote de exportação, de produto industrializado ou produzido pelo próprio estabelecimento.	", true, false, false, false));
        cfopList.add(new Cfop(6505, "	Remessa de mercadoria, adquirida ou recebida de terceiros, p/ formação de lote de exportação.	", true, false, false, false));
        cfopList.add(new Cfop(6551, "	Venda de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(6552, "	Transferência de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(6553, "	Devolução de compra de bem p/ o ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(6554, "	Remessa de bem do ativo imobilizado p/ uso fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6555, "	Devolução de bem do ativo imobilizado de terceiro, recebido p/ uso no estabelecimento	", true, false, false, true));
        cfopList.add(new Cfop(6556, "	Devolução de compra de material de uso ou consumo	", true, false, false, true));
        cfopList.add(new Cfop(6557, "	Transferência de material de uso ou consumo	", true, false, false, false));
        cfopList.add(new Cfop(6603, "	Ressarcimento de ICMS retido por substituição tributária	", true, false, false, false));
        cfopList.add(new Cfop(6651, "	Venda de combustível ou lubrificante, de produção do estabelecimento, destinados à industrialização subseqüente	", true, false, false, false));
        cfopList.add(new Cfop(6652, "	Venda de combustível ou lubrificante, de produção do estabelecimento, destinados à comercialização	", true, false, false, false));
        cfopList.add(new Cfop(6653, "	Venda de combustível ou lubrificante, de produção do estabelecimento, destinados a consumidor ou usuário final 	", true, false, false, false));
        cfopList.add(new Cfop(6654, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados à industrialização subseqüente 	", true, false, false, false));
        cfopList.add(new Cfop(6655, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados à comercialização	", true, false, false, false));
        cfopList.add(new Cfop(6656, "	Venda de combustível ou lubrificante, adquiridos ou recebidos de terceiros, destinados a consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(6657, "	Remessa de combustível ou lubrificante, adquiridos ou recebidos de terceiros, p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6658, "	Transferência de combustível ou lubrificante de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6659, "	Transferência de combustível ou lubrificante adquiridos ou recebidos de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(6660, "	Devolução de compra de combustível ou lubrificante adquiridos p/ industrialização subseqüente	", true, false, false, true));
        cfopList.add(new Cfop(6661, "	Devolução de compra de combustível ou lubrificante adquiridos p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(6662, "	Devolução de compra de combustível ou lubrificante adquiridos por consumidor ou usuário final	", true, false, false, true));
        cfopList.add(new Cfop(6663, "	Remessa p/ armazenagem de combustível ou lubrificante	", true, false, false, false));
        cfopList.add(new Cfop(6664, "	Retorno de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(6665, "	Retorno simbólico de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(6666, "	Remessa, por conta e ordem de terceiros, de combustível ou lubrificante recebidos p/ armazenagem	", true, false, false, false));
        cfopList.add(new Cfop(6667, "	Venda de combustível ou lubrificante a consumidor ou usuário final estabelecido em outra UF diferente da que ocorrer o consumo	", true, false, false, false));
        cfopList.add(new Cfop(6901, "	Remessa p/ industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(6902, "	Retorno de mercadoria utilizada na industrialização por encomenda	", true, false, false, false));
        cfopList.add(new Cfop(6903, "	Retorno de mercadoria recebida p/ industrialização e não aplicada no referido processo	", true, false, false, false));
        cfopList.add(new Cfop(6904, "	Remessa p/ venda fora do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(6905, "	Remessa p/ depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(6906, "	Retorno de mercadoria depositada em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(6907, "	Retorno simbólico de mercadoria depositada em depósito fechado ou armazém geral	", true, false, false, false));
        cfopList.add(new Cfop(6908, "	Remessa de bem por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(6909, "	Retorno de bem recebido por conta de contrato de comodato	", true, false, false, false));
        cfopList.add(new Cfop(6910, "	Remessa em bonificação, doação ou brinde	", true, false, false, false));
        cfopList.add(new Cfop(6911, "	Remessa de amostra grátis	", true, false, false, false));
        cfopList.add(new Cfop(6912, "	Remessa de mercadoria ou bem p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(6913, "	Retorno de mercadoria ou bem recebido p/ demonstração	", true, false, false, false));
        cfopList.add(new Cfop(6914, "	Remessa de mercadoria ou bem p/ exposição ou feira	", true, false, false, false));
        cfopList.add(new Cfop(6915, "	Remessa de mercadoria ou bem p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(6916, "	Retorno de mercadoria ou bem recebido p/ conserto ou reparo	", true, false, false, false));
        cfopList.add(new Cfop(6917, "	Remessa de mercadoria em consignação mercantil ou industrial	", true, false, false, false));
        cfopList.add(new Cfop(6918, "	Devolução de mercadoria recebida em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(6919, "	Devolução simbólica de mercadoria vendida ou utilizada em processo industrial, recebida anteriormente em consignação mercantil ou industrial	", true, false, false, true));
        cfopList.add(new Cfop(6920, "	Remessa de vasilhame ou sacaria	", true, false, false, false));
        cfopList.add(new Cfop(6921, "	Devolução de vasilhame ou sacaria	", true, false, false, true));
        cfopList.add(new Cfop(6922, "	Lançamento efetuado a título de simples faturamento decorrente de venda p/ entrega futura	", true, false, false, false));
        cfopList.add(new Cfop(6923, "	Remessa de mercadoria por conta e ordem de terceiros, em venda à ordem ou em operações com armazém geral ou depósito fechado	", true, false, false, false));
        cfopList.add(new Cfop(6924, "	Remessa p/ industrialização por conta e ordem do adquirente da mercadoria, quando esta não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(6925, "	Retorno de mercadoria recebida p/ industrialização por conta e ordem do adquirente da mercadoria, quando aquela não transitar pelo estabelecimento do adquirente	", true, false, false, false));
        cfopList.add(new Cfop(6929, "	Lançamento efetuado em decorrência de emissão de documento fiscal relativo a operação ou prestação também registrada em equipamento Emissor de Cupom Fiscal - ECF	", true, false, false, false));
        cfopList.add(new Cfop(6931, "	Lançamento efetuado em decorrência da responsabilidade de retenção do imposto por substituição tributária, atribuída ao remetente ou alienante da mercadoria, pelo serviço de transporte realizado por transportador autônomo ou por transportador não inscrito na UF onde iniciado o serviço	", true, false, true, false));
        cfopList.add(new Cfop(6932, "	Prestação de serviço de transporte iniciada em UF diversa daquela onde inscrito o prestador	", true, false, true, false));
        cfopList.add(new Cfop(6933, "	Prestação de serviço tributado pelo Imposto Sobre Serviços de Qualquer Natureza 	", true, false, false, false));
        cfopList.add(new Cfop(6934, "	Remessa simbólica de mercadoria depositada em armazém geral ou depósito fechado	", true, false, false, false));
        cfopList.add(new Cfop(6949, "	Outra saída de mercadoria ou prestação de serviço não especificado	", true, false, false, false));
        cfopList.add(new Cfop(7101, "	Venda de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(7102, "	Venda de mercadoria adquirida ou recebida de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(7105, "	Venda de produção do estabelecimento, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(7106, "	Venda de mercadoria adquirida ou recebida de terceiros, que não deva por ele transitar	", true, false, false, false));
        cfopList.add(new Cfop(7127, "	Venda de produção do estabelecimento sob o regime de drawback 	", true, false, false, false));
        cfopList.add(new Cfop(7129, "	Venda de produção do estabelecimento ao mercado externo de mercadoria industrializada sob o amparo do Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, false));
        cfopList.add(new Cfop(7201, "	Devolução de compra p/ industrialização ou produção rural	", true, false, false, true));
        cfopList.add(new Cfop(7202, "	Devolução de compra p/ comercialização	", true, false, false, true));
        cfopList.add(new Cfop(7205, "	Anulação de valor relativo à aquisição de serviço de comunicação	", true, false, false, false));
        cfopList.add(new Cfop(7206, "	Anulação de valor relativo a aquisição de serviço de transporte	", true, false, false, false));
        cfopList.add(new Cfop(7207, "	Anulação de valor relativo à compra de energia elétrica	", true, false, false, false));
        cfopList.add(new Cfop(7210, "	Devolução de compra p/ utilização na prestação de serviço	", true, false, false, true));
        cfopList.add(new Cfop(7211, "	Devolução de compras p/ industrialização sob o regime de drawback 	", true, false, false, true));
        cfopList.add(new Cfop(7212, "	Devolução de compras para industrialização sob o regime de Regime Aduaneiro Especial de Entreposto Industrial (Recof-Sped)	", true, false, false, true));
        cfopList.add(new Cfop(7251, "	Venda de energia elétrica p/ o exterior	", true, false, false, false));
        cfopList.add(new Cfop(7301, "	Prestação de serviço de comunicação p/ execução de serviço da mesma natureza	", false, true, false, false));
        cfopList.add(new Cfop(7358, "	Prestação de serviço de transporte	", false, false, true, false));
        cfopList.add(new Cfop(7501, "	Exportação de mercadorias recebidas com fim específico de exportação	", true, false, false, false));
        cfopList.add(new Cfop(7551, "	Venda de bem do ativo imobilizado	", true, false, false, false));
        cfopList.add(new Cfop(7553, "	Devolução de compra de bem p/ o ativo imobilizado	", true, false, false, true));
        cfopList.add(new Cfop(7556, "	Devolução de compra de material de uso ou consumo	", true, false, false, true));
        cfopList.add(new Cfop(7651, "	Venda de combustível ou lubrificante de produção do estabelecimento	", true, false, false, false));
        cfopList.add(new Cfop(7654, "	Venda de combustível ou lubrificante adquiridos ou recebidos de terceiros	", true, false, false, false));
        cfopList.add(new Cfop(7667, "	Venda de combustível ou lubrificante a consumidor ou usuário final	", true, false, false, false));
        cfopList.add(new Cfop(7930, "	Lançamento efetuado a título de devolução de bem cuja entrada tenha ocorrido sob amparo de regime especial aduaneiro de admissão temporária	", true, false, false, false));
        cfopList.add(new Cfop(7949, "	Outra saída de mercadoria ou prestação de serviço não especificado	", true, false, false, false));

        em.getTransaction().begin();
        for (Cfop cfop : cfopList) {
            if (findByCodigo(cfop.getCodigo()) == null) {
                cfop.setDescricao(cfop.getDescricao().trim());
                em.persist(cfop);
            }
        }
        em.getTransaction().commit();

    }
}
