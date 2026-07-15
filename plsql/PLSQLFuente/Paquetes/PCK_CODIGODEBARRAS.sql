create or replace PACKAGE "PCK_CODIGODEBARRAS" AS
--***********************************************************************
--****************                                    *******************
--****************      Modulo de codigo de barras    *******************
--****************                                    *******************
--**************** Autor: Fernando Camargo Sarmiento  *******************
--****************                                    *******************
--***********************************************************************
--
-- Rutinas de impresion, Rutina tipo 39, EAN 13 y 128.
--
-- Descripcion:  Este modulo muestra el codigo de barras equivalente
--               a un campo
-- 39, 128 clase C y Ean 13 probadas ok. (las otras no se han probado)
--
-- Uso:
-- En el evento alDarFormato del reporte, se debe pegar este codigo:
--
-- ImprimirCodigoDeBarras(Objeto, CampoDato, NombreReporte, TipoDeBarras,IncluirDigitoDeChequeo,Comprimir)
-- revisar la descripcion de dicha funcion
--
-- Donde Objeto es el objeto Ole Dependiente donde se colocara el codigo de barras,
--       CampoDato es el campo que tiene el dato por convertir, y
--       NombreReporte es el nombre del reporte en el que se imprimira.
  /*
  AUTOR MIGRACION : JESUS ALBEIRO AVENDA?O BECERRA JAAB.
  FECHA           : 18-JUN-10
  */
  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
  type VECTORCODE128 is table of varchar2(106) index by pls_integer;
type VECTORCODEBAR is table of varchar2(200) index by pls_integer;
type MATRIZCODEBAR is table of VECTORCODEBAR index by pls_integer;

  FUNCTION FC_IMPRIMIRCODIGODEBARRAS (UN_TEXTOPARACONVERTIR  VARCHAR2, UN_TIPODEBARRAS         VARCHAR2 DEFAULT 'CODE128', UN_INCLUIRDIGITODECHEQUEO BOOLEAN DEFAULT TRUE, UN_COMPRIMIR BOOLEAN DEFAULT FALSE ) RETURN VARCHAR2;
  FUNCTION FC_CODE128                (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_CODEBAR                (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_CODE25                 (UN_DATOACODIFICAR      VARCHAR2, UN_BLNADDCHECKDIGIT     BOOLEAN DEFAULT FALSE)  RETURN VARCHAR2;
  FUNCTION FC_CODE39                 (UN_DATOACODIFICAR      VARCHAR2, UN_BLNADDCHECKDIGIT     BOOLEAN DEFAULT FALSE)  RETURN VARCHAR2;
  FUNCTION FC_CODIFICAR_SUPLEMENTO   (UN_SUMPLEMENTO         VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_EAN13                  (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_EAN8                   (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_INTERLACE              (UN_MESSAGE             VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_ITF                    (UN_DATOACODIFICAR      VARCHAR2, UN_BLNADDCHECKDIGIT     BOOLEAN DEFAULT TRUE)   RETURN VARCHAR2;
  FUNCTION FC_MSI                    (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_POSTNET                (UN_DATOACODIFICAR      VARCHAR2, UN_BLNINCLUDECHECKDIGIT BOOLEAN DEFAULT TRUE)   RETURN VARCHAR2;
  FUNCTION FC_UPCE                   (UN_DATOACODIFICAR      VARCHAR2) RETURN VARCHAR2;
  FUNCTION FC_UPCA                   (UN_DATOACODIFICAR      VARCHAR2, UN_BLNCOMPRESS          BOOLEAN DEFAULT FALSE)  RETURN VARCHAR2;
END PCK_CODIGODEBARRAS;