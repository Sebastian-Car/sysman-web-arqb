CREATE OR REPLACE PACKAGE                "PCK_DATOS" AS 

GL_ERROR_NUM  NUMBER:=1000;                       -- 11/02/2015 - Variable que se usa para almacenar el c¿o del error presentando, par¿tro de la funci¿VALUAR_ERROR
GL_ERROR_MSG  VARCHAR2(32000);                    -- 23/01/2015 - Variable que se usa para almacenar el mensaje del error presentando, par¿tro de la funci¿VALUAR_ERROR
GL_ERROR_RTA  VARCHAR2(32000);                    -- 23/01/2015 - Variable que se usa para almacenar el mensaje del error presentando, par¿tro de la funci¿VALUAR_ERROR
GL_RTA        NUMBER;                             -- 23/01/2015 - Variable que se usa para almacenar el resulatado de la ejecuci¿e funciones
MI_ERROR_NUM  NUMBER;             -- 23/01/2015 - Variable que se usa para almacenar el c¿o del error presentando, par¿tro de la funci¿VALUAR_ERROR
MI_ERROR_MSG  VARCHAR2(32000);    -- 23/01/2015 - Variable que se usa para almacenar el mensaje del error presentando, par¿tro de la funci¿VALUAR_ERROR
CONS_DEPENDENCIA       CONSTANT VARCHAR(20) :=LPAD('9',20,'9'); -- 29/09/2015 - Constante que identifica el valor por defecto para las dependencias.
CONS_MAX_ID            CONSTANT VARCHAR(32) :=LPAD('9',32,'9');  -- 27/01/2015 - Constante que identifica el m¿mo valor de una cuenta contable
CONS_CENTRO            CONSTANT VARCHAR(20) :=LPAD('9',20,'9');  -- 27/01/2015 - Constante que identifica el centro de costo varios
CONS_TERCERO           CONSTANT VARCHAR(18) :=LPAD('9',18,'9');  -- 27/01/2015 - Constante que identifica el tercero varios
CONS_SUCURSAL          CONSTANT VARCHAR(3)  :=LPAD('9',3,'9');   -- 27/01/2015 - Constante que identifica la sucursal varios
CONS_AUXILIAR          CONSTANT VARCHAR(20) :=LPAD('9',20,'9');  -- 27/01/2015 - Constante que identifica el auxiliar varios
CONS_REFERENCIA        CONSTANT VARCHAR(20) :=LPAD('9',20,'9');  -- 27/01/2015 - Constante que identifica el referencia varios
CONS_FUENTE            CONSTANT VARCHAR(20) :=LPAD('9',20,'9');  -- 27/01/2015 - Constante que identifica el fuente varios
CONS_NUMERO_ORDEN_PREDIAL CONSTANT VARCHAR(3) := '001';          --02/08/2016 - Constante que identifica el número de orden de PREDIAL
MODULONOMINA           CONSTANT  NUMBER:= 6;                     -- 19/08/2015 - CODIGO DEL MODULO DE NOMINA
MODULOBANCOPROY        CONSTANT  NUMBER:= 52;                    -- 19/08/2015 - CODIGO DEL MODULO DE BANCO DE PROYECTOS
MODULOCONTRATO         CONSTANT  NUMBER:= 9;                     -- 10/10/2015 - CODIGO DEL MODULO DE CONTRATOS
MODULOALMACEN          CONSTANT  NUMBER:= 10;                    -- 09/11/2015 - CODIGO DEL MODULO DE ALMACEN
MODULOPRECONTRACTUAL   CONSTANT  NUMBER:= 19;                    -- 09/11/2015 - CODIGO DEL MODULO DE PRECONTRACTUAL
MODULOCONTRATOS        CONSTANT  NUMBER:= 9;                     -- 29/12/2015 - CODIGO DEL MODULO DE CONTRATOS
MODULOCONTABILIDAD     CONSTANT NUMBER:=1;                        --28/03/2016  - CÓDIGO DEL MÓDULO DE CONTABILIDAD
MODULOPRESUPUESTO      CONSTANT NUMBER:=3;                        --28/03/2016  - CÓDIGO DEL MÓDULO DE PRESUPUESTO
MODULOPREDIAL          CONSTANT NUMBER:=60;   
MODULOSERVICIOSPUBLICOS  CONSTANT NUMBER:=74;   

FUNCTION FC_ACME
  (
  UN_TABLA     VARCHAR2,
  UN_ACCION    VARCHAR2:='',      --  'M' MODIFICAR 'I' INSERTAR 'E' ELIMINAR
  UN_CAMPOS    VARCHAR2:='',      --  CODIGO,NOMBRE
  UN_VALORES   VARCHAR2:='',      --  '01','OBJETO'
  UN_ROWID     VARCHAR2:='0',      --  'AAAM1lAABAAAS5PAAa'
  UN_CONDICION VARCHAR2:='',
  UN_MERGEUSING  VARCHAR2 :='', 
  UN_MERGEENLACE VARCHAR2 :='',
  UN_MERGEEXISTE VARCHAR2 :='', 
  UN_MERGENOEXIS VARCHAR2 :='',
  UN_LLAVE VARCHAR2 :=''
  )
RETURN VARCHAR2;

--02
FUNCTION FC_MODULONOMINA
RETURN NUMBER;

--03
FUNCTION FC_MODULOBANCOPROY
RETURN NUMBER;

--04
FUNCTION FC_CONS_AUXILIAR
RETURN VARCHAR2;
--05
FUNCTION FC_CONS_FUENTE
RETURN VARCHAR2;

FUNCTION FC_MODULOALMACEN
RETURN NUMBER;

FUNCTION FC_MODULOPRECONTRACTUAL
RETURN NUMBER;

FUNCTION FC_MODULOCONTRATOS
RETURN NUMBER; 

--04
FUNCTION FC_CONS_CENTRO
RETURN VARCHAR2;

FUNCTION FC_CONS_TERCERO
RETURN VARCHAR2;

FUNCTION FC_CONS_SUCURSAL
RETURN VARCHAR2;

FUNCTION FC_CONS_REFERENCIA
RETURN VARCHAR2;

FUNCTION FC_MODULOCONTABILIDAD
RETURN NUMBER;

FUNCTION FC_MODULOPREDIAL
RETURN NUMBER;

FUNCTION FC_CONS_MAX_ID
RETURN VARCHAR2;

FUNCTION FC_MODULOSERVICIOSPUBLICOS
RETURN NUMBER;

END PCK_DATOS;