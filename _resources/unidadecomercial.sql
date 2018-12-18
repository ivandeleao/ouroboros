/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50612
Source Host           : localhost:3306
Source Database       : ouroboros

Target Server Type    : MYSQL
Target Server Version : 50612
File Encoding         : 65001

Date: 2018-07-06 11:55:27
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `unidadecomercial`
-- ----------------------------
DROP TABLE IF EXISTS `unidadecomercial`;
CREATE TABLE `unidadecomercial` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(20) NOT NULL,
  `descricao` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of unidadecomercial
-- ----------------------------
INSERT INTO `unidadecomercial` VALUES ('1', 'AMPOLA', 'AMPOLA');
INSERT INTO `unidadecomercial` VALUES ('2', 'BALDE', 'BALDE');
INSERT INTO `unidadecomercial` VALUES ('3', 'BANDEJ', 'BANDEJA');
INSERT INTO `unidadecomercial` VALUES ('4', 'BARRA', 'BARRA');
INSERT INTO `unidadecomercial` VALUES ('5', 'BISNAG', 'BISNAGA');
INSERT INTO `unidadecomercial` VALUES ('6', 'BLOCO', 'BLOCO');
INSERT INTO `unidadecomercial` VALUES ('7', 'BOBINA', 'BOBINA');
INSERT INTO `unidadecomercial` VALUES ('8', 'BOMB', 'BOMBONA');
INSERT INTO `unidadecomercial` VALUES ('9', 'CAPS', 'CAPSULA');
INSERT INTO `unidadecomercial` VALUES ('10', 'CART', 'CARTELA');
INSERT INTO `unidadecomercial` VALUES ('11', 'CENTO', 'CENTO');
INSERT INTO `unidadecomercial` VALUES ('12', 'CJ', 'CONJUNTO');
INSERT INTO `unidadecomercial` VALUES ('13', 'CM', 'CENTIMETRO');
INSERT INTO `unidadecomercial` VALUES ('14', 'CM2', 'CENTIMETRO QUADRADO');
INSERT INTO `unidadecomercial` VALUES ('15', 'CX', 'CAIXA');
INSERT INTO `unidadecomercial` VALUES ('16', 'CX2', 'CAIXA COM 2 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('17', 'CX3', 'CAIXA COM 3 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('18', 'CX5', 'CAIXA COM 5 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('19', 'CX10', 'CAIXA COM 10 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('20', 'CX15', 'CAIXA COM 15 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('21', 'CX20', 'CAIXA COM 20 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('22', 'CX25', 'CAIXA COM 25 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('23', 'CX50', 'CAIXA COM 50 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('24', 'CX100', 'CAIXA COM 100 UNIDADES');
INSERT INTO `unidadecomercial` VALUES ('25', 'DISP', 'DISPLAY');
INSERT INTO `unidadecomercial` VALUES ('26', 'DUZIA', 'DUZIA');
INSERT INTO `unidadecomercial` VALUES ('27', 'EMBAL', 'EMBALAGEM');
INSERT INTO `unidadecomercial` VALUES ('28', 'FARDO', 'FARDO');
INSERT INTO `unidadecomercial` VALUES ('29', 'FOLHA', 'FOLHA');
INSERT INTO `unidadecomercial` VALUES ('30', 'FRASCO', 'FRASCO');
INSERT INTO `unidadecomercial` VALUES ('31', 'GALAO', 'GALÃO');
INSERT INTO `unidadecomercial` VALUES ('32', 'GF', 'GARRAFA');
INSERT INTO `unidadecomercial` VALUES ('33', 'GRAMAS', 'GRAMAS');
INSERT INTO `unidadecomercial` VALUES ('34', 'JOGO', 'JOGO');
INSERT INTO `unidadecomercial` VALUES ('35', 'KG', 'QUILOGRAMA');
INSERT INTO `unidadecomercial` VALUES ('36', 'KIT', 'KIT');
INSERT INTO `unidadecomercial` VALUES ('37', 'LATA', 'LATA');
INSERT INTO `unidadecomercial` VALUES ('38', 'LITRO', 'LITRO');
INSERT INTO `unidadecomercial` VALUES ('39', 'M', 'METRO');
INSERT INTO `unidadecomercial` VALUES ('40', 'M2', 'METRO QUADRADO');
INSERT INTO `unidadecomercial` VALUES ('41', 'M3', 'METRO CÚBICO');
INSERT INTO `unidadecomercial` VALUES ('42', 'MILHEI', 'MILHEIRO');
INSERT INTO `unidadecomercial` VALUES ('43', 'ML', 'MILILITRO');
INSERT INTO `unidadecomercial` VALUES ('44', 'MWH', 'MEGAWATT HORA');
INSERT INTO `unidadecomercial` VALUES ('45', 'PACOTE', 'PACOTE');
INSERT INTO `unidadecomercial` VALUES ('46', 'PALETE', 'PALETE');
INSERT INTO `unidadecomercial` VALUES ('47', 'PARES', 'PARES');
INSERT INTO `unidadecomercial` VALUES ('48', 'PC', 'PEÇA');
INSERT INTO `unidadecomercial` VALUES ('49', 'POTE', 'POTE');
INSERT INTO `unidadecomercial` VALUES ('50', 'K', 'QUILATE');
INSERT INTO `unidadecomercial` VALUES ('51', 'RESMA', 'RESMA');
INSERT INTO `unidadecomercial` VALUES ('52', 'ROLO', 'ROLO');
INSERT INTO `unidadecomercial` VALUES ('53', 'SACO', 'SACO');
INSERT INTO `unidadecomercial` VALUES ('54', 'SACOLA', 'SACOLA');
INSERT INTO `unidadecomercial` VALUES ('55', 'TAMBOR', 'TAMBOR');
INSERT INTO `unidadecomercial` VALUES ('56', 'TANQUE', 'TANQUE');
INSERT INTO `unidadecomercial` VALUES ('57', 'TON', 'TONELADA');
INSERT INTO `unidadecomercial` VALUES ('58', 'TUBO', 'TUBO');
INSERT INTO `unidadecomercial` VALUES ('59', 'UNID', 'UNIDADE');
INSERT INTO `unidadecomercial` VALUES ('60', 'VASIL', 'VASILHAME');
INSERT INTO `unidadecomercial` VALUES ('61', 'VIDRO', 'VIDRO');
