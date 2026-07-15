create or replace PACKAGE BODY PCK_PREDIAL_COM8 AS

--1 
FUNCTION FC_ANULAR_PRESCRIPCION
/*
    NAME              : FC_ANULAR_PRESCRIPCION --> se trae del controlador
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 27/06/2017
    TIME              : 02:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de anular una prescripcion.

    MODIFICATIONS     : 
    @NAME:  anularPrescripcion
    @METHOD:  GET
  */ 
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO        IN IP_FACTURADOS.CODIGO%TYPE,
    UN_NUMERO_ORDEN  IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_FECHAANTERIOR IN DATE,
    UN_RESOLUCIONANT IN IP_FACTURADOS.OBSERVACIONES%TYPE,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
    UN_OBSERVACION   IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_RESOLUCION    IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_PRESCRIPCION  IN PCK_SUBTIPOS.TI_ENTERO
) RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
    MI_DESDE IP_FACTURADOS.PREANO%TYPE;
    MI_HASTA IP_FACTURADOS.PREANO%TYPE;

BEGIN
    BEGIN 
        SELECT  MIN(IP_FACTURADOS.PREANO) DESDE
               ,MAX(IP_FACTURADOS.PREANO) HASTA
          INTO MI_DESDE,
               MI_HASTA
          FROM IP_FACTURADOS 
         WHERE IP_FACTURADOS.COMPANIA          = UN_COMPANIA 
           AND IP_FACTURADOS.CODIGO            = UN_CODIGO  
           AND TRUNC(IP_FACTURADOS.FECHAPRESCRIPCION) = TRUNC(UN_FECHAANTERIOR)
           AND (IP_FACTURADOS.OBSERVACIONES LIKE 'RES. DE PRESCRIPCION: '||UN_RESOLUCIONANT  
	              OR  IP_FACTURADOS.OBSERVACIONES LIKE 'RES. DE PRESCRIPCIÓN: '||UN_RESOLUCIONANT)    
           AND IP_FACTURADOS.NOCOBRADO NOT IN(0)   
           AND IP_FACTURADOS.PAGADO NOT IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DESDE:=NULL;
        MI_HASTA:=NULL;
    END;
    IF MI_DESDE IS NULL AND MI_HASTA IS NULL THEN 
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                      UN_EXC_COD   => SQLCODE, 
                      UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ANULAPRESCRIPCION);
        END;
    END IF;
    IF PCK_PREDIAL_COM1.FC_ACT_FACTURADOS_PRESCRIPCION(
                     UN_COMPANIA     => UN_COMPANIA
                    ,UN_CODIGO       => UN_CODIGO
                    ,UN_NUMPREDIAL   => UN_NUMERO_ORDEN
                    ,UN_PRESCRIPCION => UN_PRESCRIPCION
                    ,UN_RESOLUCION   => UN_RESOLUCION 
                    ,UN_DESDE        => MI_DESDE
                    ,UN_HASTA        => MI_HASTA
                    ,UN_OBSERVACION  => UN_OBSERVACION
                    ,UN_USUARIO      => UN_USUARIO) > 0 THEN 
        RETURN -1;
    END IF;               
END FC_ANULAR_PRESCRIPCION;

--2
FUNCTION FC_VALIDARTIPOUNO

/*
        NAME              : FC_VALIDARTIPOUNO --> en Access validarTipoUno
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 27/06/2017
        TIME              : 02:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Valida si linea de código de archivo plano corresponde al departamento y de ciudad de la sesión actual.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_LINEATEXTO       => linea extraida de archivo plano
                            UN_DEPARTAMENTO     => Departamento de compañia con la que se ingresa
                            UN_MUNICIPIO        => Ciudad de compañia con la que se ingresa

        @NAME  :  validarTipoUno
        @METHOD:  GET    
      */ 
(
    UN_LINEATEXTO   IN VARCHAR2,
    UN_DEPARTAMENTO IN IP_IGAC_TIPO_UNO.DEPARTAMENTO%TYPE,
    UN_MUNICIPIO    IN IP_IGAC_TIPO_UNO.MUNICIPIO%TYPE 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_VALIDARESOLUCIONTIPOUNO VARCHAR2(50);
    MI_DEPARTAMENTO IP_IGAC_TIPO_UNO.DEPARTAMENTO%TYPE;
    MI_MUNICIPIO IP_IGAC_TIPO_UNO.MUNICIPIO%TYPE;
BEGIN
    MI_DEPARTAMENTO               := SUBSTR(UN_LINEATEXTO, 0, 2);
    MI_MUNICIPIO                  := SUBSTR(UN_LINEATEXTO, 3, 3);
    MI_VALIDARESOLUCIONTIPOUNO    := PCK_PREDIAL_COM6.FC_VALIDATIPORESOLUCION( 
                                   UN_PRIMERALINEA => UN_LINEATEXTO);
    IF MI_VALIDARESOLUCIONTIPOUNO <> 'Tipo Uno' THEN
        BEGIN
	          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
	      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG( 
                          UN_EXC_COD   => SQLCODE, 
                          UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_PLANONOCORRESPONDE);
        END;
    END IF;
    IF MI_DEPARTAMENTO <> UN_DEPARTAMENTO THEN
        BEGIN
	          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
	            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, 
                                        UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DEPPLANONOCORRESP);
  	  	END;
    END IF;
      IF MI_MUNICIPIO <> UN_MUNICIPIO THEN
	  	   BEGIN
		         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
		         EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
		         PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, 
                                       UN_ERROR_COD=>PCK_ERRORES
                                      .ERR_PREDIAL_MUNPLANONOCORRESP);
		   END;
	  END IF;
RETURN -1;
END FC_VALIDARTIPOUNO;

--3

FUNCTION FC_INSERTAREGISTROIGACUNO

/*
    NAME              : FC_INSERTAREGISTROIGACUNO --> en Access ingresarIgacTipoUno
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 27/06/2017
    TIME              : 02:00 PM
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Elimina campos de tablas, realiza consulta de codigos de tipos de documentos, 
    realiza llamado a funciones FC_INGRESARIGACTIPOUNO y FC_VALIDARTIPOUNO
    MODIFICATIONS     :
    PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                        UN_CADENA           => linea extraida de archivo plano
                        UN_ANOFECHA         => Año actual
                        UN_USUARIO        	=> Usuario que realiza cargue de archivo
                        UN_CODIGOPAIS	      => Codigo pais de compañia con la que se ingresa
                        UN_DEPARTAMENTO     => Departamento de compañia con la que se ingresa
                        UN_MUNICIPIO        => Ciudad de compañia con la que se ingresa


    @NAME  :  insertarRegistroIgacUno
    @METHOD:  GET    
  */ 
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA           IN CLOB,
    UN_ANOFECHA         IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CODIGOPAIS			  IN PAISES.PAIS%TYPE,
    UN_DEPARTAMENTO     IN DEPARTAMENTO.CODIGO%TYPE,
    UN_MUNICIPIO        IN CIUDAD.CODIGO%TYPE,
    UN_IND_TOTAL        IN PCK_SUBTIPOS.TI_LOGICO,
    UN_NOMBRECOMPANIA   IN COMPANIA.NOMBRE%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_REGDOCUMENTOS      VARCHAR2(200 CHAR);
    MI_LISTADOCUMENTOS    VARCHAR2(200 CHAR);
    MI_CADENA             CLOB;
    MI_REEMPLAZOS			    PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NUMEROREG          PCK_SUBTIPOS.TI_ENTERO;
    MI_ARCHIVOTEXTO       VARCHAR2(500 CHAR);
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_LINEAS 			      PCK_SUBTIPOS.TI_ENTERO;
    MI_RESPUESTA          PCK_SUBTIPOS.TI_LOGICO;
BEGIN 
    MI_CADENA:=UN_CADENA;
    MI_CONDICION:= 'COMPANIA= ''' || UN_COMPANIA  || '''';
    BEGIN
        BEGIN
            MI_TABLA:='IP_IGAC_TIPO_UNO';
            MI_RTA := PCK_DATOS.FC_ACME(
                               UN_TABLA=> MI_TABLA,
                               UN_ACCION     => 'E',
                               UN_CONDICION  => MI_CONDICION); 
            MI_TABLA:='IP_CAMBIOSIGAC';                   
            MI_RTA := PCK_DATOS.FC_ACME(
                              UN_TABLA     => MI_TABLA,
                              UN_ACCION     => 'E',
                              UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
            MI_REEMPLAZOS(0).VALOR:=  MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;                     
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ELIMINARTABLA,
                     UN_TABLAERROR => MI_TABLA
                     );
    END;                         
    BEGIN                                                                                                                                                        
        SELECT LISTAGG(''''||DCTO_IDENTIDAD||'''',',')WITHIN GROUP (ORDER BY DCTO_IDENTIDAD) DCTO_IDENTIDAD
          INTO MI_REGDOCUMENTOS
          FROM TIPOS_DOCUMENTOS
         WHERE COMPANIA = UN_COMPANIA;   
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_REGDOCUMENTOS:='X';
    END;
    MI_REGDOCUMENTOS:=NVL(MI_REGDOCUMENTOS,'X');
    MI_NUMEROREG := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);--HASTA LA PRIMERA @
    MI_LINEAS := 1;
    WHILE MI_NUMEROREG <> 0
    LOOP 
        MI_ARCHIVOTEXTO := REGEXP_SUBSTR(MI_CADENA,'[^@]+', 1);
        MI_CADENA       := REPLACE(MI_CADENA,MI_ARCHIVOTEXTO||'@','');
        MI_ARCHIVOTEXTO := REPLACE(MI_ARCHIVOTEXTO,CHR(39),'_');
        MI_NUMEROREG    := REGEXP_COUNT(MI_CADENA,'[^@]+', 1);
        IF PCK_PREDIAL_COM8.FC_VALIDARTIPOUNO(UN_LINEATEXTO => MI_ARCHIVOTEXTO
                         ,UN_DEPARTAMENTO => UN_DEPARTAMENTO
                         ,UN_MUNICIPIO    => UN_MUNICIPIO ) NOT IN(0) THEN 
            IF PCK_PREDIAL_COM8.FC_INGRESARIGACTIPOUNO(  UN_COMPANIA        => UN_COMPANIA
                                       ,UN_ARCHIVOTEXTO    => MI_ARCHIVOTEXTO
                                       ,UN_ANOFECHA        => UN_ANOFECHA
                                       ,UN_LISTADOCUMENTOS => MI_REGDOCUMENTOS
                                       ,UN_LINEAS          => MI_LINEAS
                                       ,UN_USUARIO         => UN_USUARIO
                                       ,UN_CODIGOPAIS      => UN_CODIGOPAIS) NOT IN (0) THEN 
                MI_LINEAS := MI_LINEAS+1;
            END IF;
        END IF;
    END LOOP; 
    MI_RESPUESTA :=PCK_PREDIAL_COM6.FC_IMPORTAR_IGAC_TIPO_UNO(
                     UN_COMPANIA       => UN_COMPANIA
                    ,UN_USUARIO        => UN_USUARIO
                    ,UN_FECHACORTE     => SYSDATE
                    ,UN_IND_TOTAL      => UN_IND_TOTAL
                    ,UN_NOMBRECOMPANIA => UN_NOMBRECOMPANIA);
    RETURN MI_RESPUESTA;
END FC_INSERTAREGISTROIGACUNO;

--4
FUNCTION FC_INGRESARIGACTIPOUNO 
/*
      NAME              : FC_INGRESARIGACTIPOUNO --> en Access insertaRegistroIgacUno
      AUTHORS           : SYSMAN SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 27/06/2017
      TIME              : 02:00 PM
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Funcion realiza llamado a ACME para inserción de datos a tabla.
      MODIFICATIONS     :
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                          UN_ARCHIVOTEXTO     => linea extraida de archivo plano
                          UN_ANOFECHA         => Año actual
                          UN_LISTADOCUMENTOS  => Listado concatenado de codigo de documentos
                          UN_LINEAS	          => lineas a ingresar
                          UN_CODIGOPAIS       => codigo de país de compañia con la que se ingresa

      @NAME  :  ingresarIgacTipoUno
      @METHOD:  GET    
    */
(
    UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ARCHIVOTEXTO   	IN PCK_SUBTIPOS.TI_STRSQL,
    UN_ANOFECHA			    IN PCK_SUBTIPOS.TI_ANIO,
    UN_LISTADOCUMENTOS 	IN VARCHAR2,
    UN_LINEAS 			    IN PCK_SUBTIPOS.TI_LONG,
    UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CODIGOPAIS			  IN PAISES.PAIS%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_CAMPOS			        	PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA				        PCK_SUBTIPOS.TI_TABLA;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_DEPARTAMENTO 		    IP_IGAC_TIPO_UNO.DEPARTAMENTO%TYPE;
    MI_MUNICIPIO			      IP_IGAC_TIPO_UNO.MUNICIPIO%TYPE;
    MI_CODIGO				        IP_IGAC_TIPO_UNO.CODIGO%TYPE;
    MI_TIPO 				        IP_IGAC_TIPO_UNO.TIPO%TYPE;
    MI_NUMEROORDEN		    	IP_IGAC_TIPO_UNO.NUMERO_ORDEN%TYPE;
    MI_NUMEROREGISTRO	    	IP_IGAC_TIPO_UNO.NUMERO_REGISTROS%TYPE;
    MI_NOMBRE				        IP_IGAC_TIPO_UNO.NOMBRE%TYPE;
    MI_DIGITO				        IP_IGAC_TIPO_UNO.DIGITO%TYPE;
    MI_TIPO_NIT				      VARCHAR2(200);
    MI_NIT 					        IP_IGAC_TIPO_UNO.NIT%TYPE;
    MI_DIRECCION 			      IP_IGAC_TIPO_UNO.DIRECCION%TYPE;
    MI_DESTINOECONOMICO     IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO%TYPE;
    MI_AREA_HA  			      IP_IGAC_TIPO_UNO.AREA_HA%TYPE;
    MI_AREA_M2 				      IP_IGAC_TIPO_UNO.AREA_M2%TYPE;
    MI_AREA_CONST_ANT  		  IP_IGAC_TIPO_UNO.AREA_CONST_ANT%TYPE;
    MI_AREA_CONSTRUIDA 		  IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA%TYPE;
    MI_AVALUO_ANO 			    IP_IGAC_TIPO_UNO.AVALUO_ANO%TYPE;
    MI_CONDICION_TRIBUTARIA	IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA%TYPE;
    MI_RESOLUCION 			    IP_IGAC_TIPO_UNO.RESOLUCION%TYPE;
    MI_MUTACION 			      IP_IGAC_TIPO_UNO.MUTACION%TYPE;
    MI_FECHA_MOVIMIENTO 	  IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO%TYPE;
    MI_NOVEDAD 			      	IP_IGAC_TIPO_UNO.NOVEDAD%TYPE;
    MI_REEMPLAZOS			      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA 				        	PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN	
	  MI_DEPARTAMENTO:= SUBSTR(UN_ARCHIVOTEXTO, 1, 2);
    MI_MUNICIPIO:= SUBSTR(UN_ARCHIVOTEXTO, 3, 3);
    MI_CODIGO:= SUBSTR(UN_ARCHIVOTEXTO, 6, 15);
    MI_TIPO:= SUBSTR(UN_ARCHIVOTEXTO, 21, 1);
    MI_NUMEROORDEN:= SUBSTR(UN_ARCHIVOTEXTO, 22, 3);
    MI_NUMEROREGISTRO:= SUBSTR(UN_ARCHIVOTEXTO, 25, 3);
    MI_NOMBRE:= REPLACE(SUBSTR(UN_ARCHIVOTEXTO, 28, 33),CHR(39), '_');
    MI_DIGITO:= SUBSTR(UN_ARCHIVOTEXTO, 61, 1); 
    MI_TIPO_NIT:= SUBSTR(UN_ARCHIVOTEXTO, 62, 1);
    MI_NIT:= SUBSTR(UN_ARCHIVOTEXTO, 63, 12);
    MI_DIRECCION:= REPLACE(SUBSTR(UN_ARCHIVOTEXTO, 75, 34),CHR(39),'_');
    MI_DESTINOECONOMICO := SUBSTR(UN_ARCHIVOTEXTO, 109, 2); 
    MI_AREA_HA := SUBSTR(UN_ARCHIVOTEXTO, 111, 8);
    MI_AREA_M2 := SUBSTR(UN_ARCHIVOTEXTO, 119, 4);
    MI_AREA_CONST_ANT := 0;
    MI_AREA_CONSTRUIDA := SUBSTR(UN_ARCHIVOTEXTO, 123, 6);
    MI_AVALUO_ANO := SUBSTR(UN_ARCHIVOTEXTO, 129, 12);
    MI_CONDICION_TRIBUTARIA := SUBSTR(UN_ARCHIVOTEXTO, 141, 1);
    MI_NOVEDAD := SUBSTR(UN_ARCHIVOTEXTO, 142, 1);
    MI_RESOLUCION := SUBSTR(UN_ARCHIVOTEXTO, 143, 4);
    MI_MUTACION := SUBSTR(UN_ARCHIVOTEXTO, 147,LENGTH(UN_ARCHIVOTEXTO) - 146);
    MI_FECHA_MOVIMIENTO := NVL(SUBSTR(UN_ARCHIVOTEXTO, 142, 2) 
                               || '/' 
                               || SUBSTR(UN_ARCHIVOTEXTO, 144, 2) 
                               || '/' 
                               || SUBSTR(UN_ARCHIVOTEXTO, 146, 4)
                              ,'01/01/'||UN_ANOFECHA); 
    MI_TIPO_NIT := CASE WHEN NVL(MI_TIPO_NIT,' ') = ' '
                        THEN 'X'
                        ELSE MI_TIPO_NIT
                   END;	   

	  IF INSTR(UN_LISTADOCUMENTOS,MI_TIPO_NIT,1)<=1 THEN 
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:= 'TIPONIT';
            MI_REEMPLAZOS(0).VALOR:= MI_TIPO_NIT;
            MI_REEMPLAZOS(1).CLAVE:= 'LINEA';
            MI_REEMPLAZOS(1).VALOR:= UN_LINEAS;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VALIDATIPONIT
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
        END;                
    END IF;
    IF SUBSTR(UN_ARCHIVOTEXTO, 21 , 1) = '1' THEN 
        MI_TABLA:=   'IP_IGAC_TIPO_UNO';
        MI_CAMPOS:=  'COMPANIA ' ||
                      ',PAIS '||
                      ',DEPARTAMENTO '||
                      ',MUNICIPIO '||
                      ',CODIGO '||
                      ',TIPO '||
                      ',NUMERO_ORDEN '||
                      ',NUMERO_REGISTROS '||
                      ',NOMBRE '||
                      ',DIGITO '||
                      ',TIPO_NIT '||
                      ',NIT '||
                      ',DIRECCION '||
                      ',DESTINO_ECONOMICO '||
                      ',AREA_HA '||
                      ',AREA_M2 '||
                      ',AREA_CONST_ANT '||
                      ',AREA_CONSTRUIDA '||
                      ',AVALUO_ANO '||
                      ',CONDICION_TRIBUTARIA '||
                      ',RESOLUCION '||
                      ',MUTACION '||
                      ',FECHA_MOVIMIENTO '||
                      ',NOVEDAD '||
                      ',CREATED_BY '||
                      ',DATE_CREATED ';

        MI_VALORES:=   '  '''|| UN_COMPANIA ||''' 
                        , '''|| UN_CODIGOPAIS ||''' 
                , '''|| MI_DEPARTAMENTO ||''' 
                , '''|| MI_MUNICIPIO ||''' 
                , '''|| MI_CODIGO ||''' 
                , '''|| MI_TIPO ||''' 
                  , '''|| MI_NUMEROORDEN ||''' 
                  , '''|| MI_NUMEROREGISTRO ||''' 
                  , '''|| MI_NOMBRE ||''' 
                  , '''|| MI_DIGITO ||''' 
                  , '''|| MI_TIPO_NIT ||''' 
                  , '''|| MI_NIT ||''' 
                  , '''|| MI_DIRECCION ||''' 
                  , '''|| MI_DESTINOECONOMICO ||''' 
                  , '|| MI_AREA_HA ||'
                  , '|| MI_AREA_M2 ||'
                  , '|| MI_AREA_CONST_ANT ||' 
                  , '|| MI_AREA_CONSTRUIDA ||' 
                  , '|| MI_AVALUO_ANO ||'
                  , '''|| MI_CONDICION_TRIBUTARIA ||''' 
                  , '''|| MI_RESOLUCION ||''' 
                  , '''|| MI_MUTACION ||''' 
                  , '''|| MI_FECHA_MOVIMIENTO ||''' 
                  , '''|| MI_NOVEDAD ||''' 
                  , '''|| UN_USUARIO ||'''
                  , SYSDATE ';
        BEGIN 
            BEGIN 
                MI_RTA := PCK_DATOS.FC_ACME( 
                                    UN_TABLA   => MI_TABLA
                                   ,UN_ACCION  => 'I' 
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'RESOLUCION';
            MI_REEMPLAZOS(0).VALOR:= MI_RESOLUCION;
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_IGACTIPOUNO
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
        END; 		
    END IF;
    RETURN -1;
END FC_INGRESARIGACTIPOUNO;

--5
FUNCTION FC_ACTUALIZARAPORTERECIBO 
    /*
      NAME              : FC_ACTUALIZARAPORTERECIBO --> EN ACCESS 
      AUTHORS           : STEFANINI SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 29/06/2017
      TIME              : 04:32 PM
      SOURCE MODULE     : PredialP2016.05.06
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Realiza la actualización de la información de aportes en una factura específica
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                          UN_USUARIO       => Usuario que accede al sistema 
                          UN_CONCEPTO      => Concepto definido en el parámetro "CONCEPTO PARA APORTE VOLUNTARIO"
                          UN_VALORAPORTE   => Valor del aporte a registrar
                          UN_PREVAL        => Valor total de la factura 
                          UN_PROYECTO      => Proyecto asignado a la factura que también ingresa por parámetro
                          UN_NUMEROORDEN   => Número de Orden Predial 
                          UN_FACTURA       => Código de la factura 
                          UN_INDAPORTE     => Indicador de aporte en el proyecto 
	    @NAME  :  actualizarAporteRecibo
      @METHOD:  PUT    
    */ 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
  UN_CONCEPTO           IN VARCHAR2,
  UN_VALORAPORTE        IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PREVAL             IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PROYECTO           IN PCK_SUBTIPOS.TI_LONG,
  UN_NUMEROORDEN        IN PCK_SUBTIPOS.TI_NUMORDEN,
  UN_FACTURA            IN PCK_SUBTIPOS.TI_DOCNUM,
  UN_INDAPORTE          IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN PCK_SUBTIPOS.TI_ENTERO 
AS 
  MI_TABLA             PCK_SUBTIPOS.TI_TABLA;  
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA               PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TOTALPREVAL       PCK_SUBTIPOS.TI_DOBLE;
BEGIN

  BEGIN
    BEGIN
      MI_TABLA        := 'IP_RECIBOS_DE_PAGO';

      MI_TOTALPREVAL  := UN_PREVAL + UN_VALORAPORTE;

      IF UN_INDAPORTE IN (-1) THEN
        MI_CAMPOS     := 'INDAPORTE          = -1, ' ||
                          UN_CONCEPTO||'     = ' || UN_VALORAPORTE || ', ' ||
                         'PREVAL             = ' || MI_TOTALPREVAL || ', ' ||
                         'PROYECTO_APORTE    = ' || UN_PROYECTO || ', ' ||
                         'DATE_MODIFIED      = SYSDATE, '||
                         'MODIFIED_BY        = ''' || UN_USUARIO || ''' ';  
      ELSE
        MI_CAMPOS     := 'INDAPORTE          = 0, ' ||
                          UN_CONCEPTO||'     = 0, ' ||
                         'PREVAL             = ' || UN_PREVAL || ', ' ||
                         'PROYECTO_APORTE    = 0, ' ||
                         'DATE_MODIFIED      = SYSDATE, '||
                         'MODIFIED_BY        = ''' || UN_USUARIO || ''' ';              
      END IF;      

      MI_CONDICION    := '     COMPANIA      = ''' || UN_COMPANIA ||''''||
                         ' AND DOCNUM        = ''' || UN_FACTURA ||''''||
                         ' AND NUMERO_ORDEN  = ''' || UN_NUMEROORDEN || '''';

      MI_RTA          := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                        
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    =>  SQLCODE,
          UN_ERROR_COD  =>  PCK_ERRORES.ERRR_PREDIAL_ACTAPORTERECIBO,
          UN_TABLAERROR =>  MI_TABLA
        );
  END;

  RETURN MI_RTA;
END FC_ACTUALIZARAPORTERECIBO;

--6
FUNCTION FC_VALIDA_CFG_TARIFAS 
/*
    NAME              : FC_VALIDA_CFG_TARIFAS --> se trae del controlador
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 30/06/2017
    TIME              : 09:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de validar los datos antes de actualizar o insertar una configuracion de tarifa

    MODIFICATIONS     : 
    @NAME:  validarConfigTarifas
    @METHOD:  GET
  */ 
(
      UN_IND_TIPOESTRATO IN PCK_SUBTIPOS.TI_LOGICO
     ,UN_TIPO_INICIAL    IN IP_TIPO_PREDIO.CODIGO%TYPE
     ,UN_TIPO_FINAL      IN IP_TIPO_PREDIO.CODIGO%TYPE
     ,UN_IND_ESTRATO     IN PCK_SUBTIPOS.TI_LOGICO
     ,UN_ESTRATO_INICIAL IN IP_TABLA_ESTRATOS.CODIGO_ESTRATO%TYPE
     ,UN_ESTRATO_FINAL   IN IP_TABLA_ESTRATOS.CODIGO_ESTRATO%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO 
AS
    MI_EXISTE PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    IF UN_IND_TIPOESTRATO NOT IN (0) THEN 
        BEGIN  
            SELECT COUNT(1)  CONTEO
              INTO MI_EXISTE   
              FROM IP_TIPO_PREDIO 
             WHERE CODIGO = UN_TIPO_INICIAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:=0;
        END;     
        IF MI_EXISTE IN (0) THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN     
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFAS
                           ,UN_TABLAERROR => 'IP_TIPO_PREDIO'
                           ); 
            END;
        END IF;
        BEGIN 
            SELECT COUNT(*) CONTEO
              INTO MI_EXISTE
              FROM IP_TIPO_PREDIO 
             WHERE CODIGO = UN_TIPO_FINAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE:=0;
        END;    
        IF MI_EXISTE IN (0) THEN  
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN     
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA1
                           ,UN_TABLAERROR => 'IP_TIPO_PREDIO'
                           ); 
            END;
        END IF;
    END IF;
    IF UN_IND_ESTRATO NOT IN (0) THEN 
        BEGIN 
            SELECT COUNT(*) CONTEO
              INTO MI_EXISTE
              FROM IP_TABLA_ESTRATOS 
             WHERE CODIGO_ESTRATO = UN_ESTRATO_INICIAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:=0;
        END;
        IF MI_EXISTE = 0 THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN     
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA2
                           ,UN_TABLAERROR => 'IP_TABLA_ESTRATOS'
                           ); 
            END;
        END IF;
        BEGIN
            SELECT COUNT(*) CONTEO
              INTO MI_EXISTE
              FROM IP_TABLA_ESTRATOS 
             WHERE CODIGO_ESTRATO = UN_ESTRATO_FINAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:=0;
        END;
        IF MI_EXISTE IN (0) THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN     
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA3
                           ,UN_TABLAERROR => 'IP_TABLA_ESTRATOS'
                           ); 
            END;
        END IF;
    END IF;
    RETURN -1;
END FC_VALIDA_CFG_TARIFAS;


FUNCTION FC_REGISTRAR_CERTIFICADO
/*
    NAME              : FC_REGISTRAR_CERTIFICADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 30/06/2017
    TIME              : 09:00 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : FUNCION QUE INSERTA EL CERTIFICADO CATASTRAL PARA LOS USUARIOS NO REGISTRADOS
    MODIFICATIONS     : 
    PARAMETERS        : 
                      UN_COMPANIA      => COMPANIA DE INGRESO A LA APLICACION
                      UN_FORMULARIO    => TIPO DE LA TABLA IP_NUMEROSDEFACTURA DEL LA CUAL SE OBTENDRA EL NUMERO DE FACTURA .
                      UN_PREDIO        => CODIGO DEL PREDIO A REGISTRAR EL CERTIFICADO.
                      UN_DIRECCION     => DIRRECCION DEL PREDIO PARA EL CUAL SE VA REGISTRAR EL CERTIFICADO.
                      UN_NOMBRE        => NOMBRE DEL PREDIO PARA EL CUAL SE VA REGISTRAR EL CERTIFICADO.
                      UN_CEDULA        => NIT DEL PREDIO PARA EL CUAL SE VA REGISTRAR EL CERTIFICADO.
                      UN_NUMORDEN      => NUMERO DE ORDEN DEL PREDIO
                      UN_VALOR         => VALOR DEL PREDIO
                      UN_SUCURSAL      => SUCURSAL DEL PREDIO
                      UN_USUARIO       => USUARIO QUE REGISTRA EL CERTIFICADO.
                      UN_RECIBO        => NUMERO DE RECIBO SI EL PREDIO LO TIENE.
                      UN_DESTINO       => LUGAR PARA EL CUAL SE REGISTRA EL CERTIFICADO
                      UN_BANCO         => BANCO EN EL CUAL SE REALIZO EL PAGO DEL RECIBO
                      UN_FECHA         => FECHA DE PAGO DEL RECIBO
                      UN_ANIO          => AÑO DE PAGO DEL RECIBO 
    @NAME : registrarCertificado
    @METHOD : POST
*/
(
  UN_COMPANIA 		    IN    PCK_SUBTIPOS.TI_COMPANIA,
  UN_FORMULARIO       IN    VARCHAR2,
  UN_PREDIO           IN    IP_CERTIFICADOCATAS.CODIGOPREDIO%TYPE,
  UN_DIRECCION        IN    IP_CERTIFICADOCATAS.DIRECCION%TYPE,
  UN_NOMBRE           IN    IP_CERTIFICADOCATAS.NOMBRE%TYPE,
  UN_CEDULA           IN    IP_CERTIFICADOCATAS.NIT%TYPE,
  UN_NUMORDEN         IN    IP_CERTIFICADOCATAS.NUMERO_ORDEN%TYPE,
  UN_VALOR            IN    IP_CERTIFICADOCATAS.VALOR%TYPE,
  UN_SUCURSAL         IN    IP_CERTIFICADOCATAS.SUCURSAL%TYPE,
  UN_USUARIO          IN    PCK_SUBTIPOS.TI_USUARIO,
  UN_RECIBO           IN    IP_CERTIFICADOPS.REC_VALORIZACION%TYPE,
  UN_DESTINO          IN    IP_CERTIFICADOPS.DESTINO%TYPE,
  UN_BANCO            IN    IP_CERTIFICADOPS.PAG_BAN%TYPE,
  UN_FECHA            IN    IP_CERTIFICADOPS.PAG_FEC%TYPE,
  UN_ANIO             IN    IP_CERTIFICADOPS.PAGO_ANO%TYPE
)
RETURN VARCHAR2
AS
  MI_CONSECUTIVO      IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
  MI_DIGITOS          IP_TIPOSNUMERACION.DIGITOS%TYPE;
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION; 
  MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TIPO             VARCHAR2(1CHAR);
BEGIN

  BEGIN
    SELECT NF.CONSECUTIVOREAL, 
           T.DIGITOS
    INTO   MI_CONSECUTIVO,
           MI_DIGITOS
    FROM IP_NUMEROSDEFACTURA NF 
      INNER JOIN IP_TIPOSNUMERACION T 
      ON (NF.TIPO = T.CODIGO) 
    WHERE NF.COMPANIA = UN_COMPANIA
      AND NF.TIPO = CASE WHEN UN_FORMULARIO = '0' THEN 'C' ELSE 'P' END
      AND NF.ACTIVO <> 0;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_CONSECUTIVOCONF
              );
  END;

  IF  UN_FORMULARIO = '0' THEN 
      MI_TIPO :='C'; 
  ELSE 
      MI_TIPO :='P';      
  END IF;

  IF MI_CONSECUTIVO IS NOT NULL THEN
    MI_CONSECUTIVO:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => MI_CONSECUTIVO+1, UN_LONGITUD => MI_DIGITOS);
    MI_CAMPOS := 'CONSECUTIVOREAL =''' ||MI_CONSECUTIVO ||''',
                  DATE_MODIFIED = SYSDATE,
                  MODIFIED_BY = '''||UN_USUARIO ||''' ';
    MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA||'''
                    AND TIPO    = '''||MI_TIPO||'''
                    AND ACTIVO  <> 0';
    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_NUMEROSDEFACTURA', 
                                     UN_ACCION    =>  'M', 
                                     UN_CAMPOS    =>  MI_CAMPOS,
                                     UN_CONDICION =>  MI_CONDICION);                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTCONSECUTIVO
          );
    END;
  END IF;

  MI_CAMPOS :='COMPANIA, NUMCER, DIRECCION, UBICACION, 
               AREA_HA, AREA_M2, AVALUO,ANOAVALUO, NOMBRE, 
               NIT, NUMERO_ORDEN, VALOR, SUCURSAL, CREATED_BY, DATE_CREATED';
  MI_VALORES := ''''|| UN_COMPANIA    ||''',
                ''' || MI_CONSECUTIVO ||''',
                ''' || UN_DIRECCION   ||''',
                0,0,0,0,'||EXTRACT (YEAR FROM SYSDATE)||',
                '''|| UN_NOMBRE||''',
                '''|| UN_CEDULA||''',
                '''|| UN_NUMORDEN||''',
                '||   UN_VALOR||',
                '''|| UN_SUCURSAL||''',
                '''|| UN_USUARIO||''',
                SYSDATE
                ';

  IF UN_FORMULARIO = '0' THEN
    MI_TABLA := 'IP_CERTIFICADOCATAS';
    MI_CAMPOS := MI_CAMPOS || ',CODIGOPREDIO, FECHA_EXP';
    MI_VALORES := MI_VALORES || ','''||UN_PREDIO||''',SYSDATE';
  ELSIF UN_FORMULARIO = '1' THEN
    MI_TABLA := 'IP_CERTIFICADOPS';
    MI_CAMPOS := MI_CAMPOS || ',REC_VALORIZACION, DESTINO,CODIGOPREDIO,FECHA_EXP,PAG_BAN, PAG_FEC, PAG_VAL, PAGO_ANO';
    MI_VALORES := MI_VALORES ||',
                  '''|| UN_RECIBO  ||''',
                  '''|| UN_DESTINO ||''',
                  '''|| UN_PREDIO  ||''',
                  SYSDATE,
                  '''|| UN_BANCO   ||''',
                  '''|| UN_FECHA   ||''',
                  '||   UN_VALOR   ||',
                  '||   UN_ANIO    ||'';
  END IF;

  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                             UN_ACCION  =>  'I',
                                             UN_CAMPOS  =>  MI_CAMPOS, 
                                             UN_VALORES =>  MI_VALORES);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_INSCERTIFICADO
                    );
  END;
  RETURN MI_CONSECUTIVO;
END FC_REGISTRAR_CERTIFICADO;

FUNCTION FC_APLICARTODAS_CFGTARIFAS
/*
      NAME              : FC_APLICARTODAS_CFGTARIFAS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/06/2017
      TIME              : 12:00 M
      SOURCE MODULE     : 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

      PARAMETERS        : UN_COMPANIA => Compania que ingreso a la aplicación
                          UN_USUARIO  => Usuario que ingreso a la aplicación
    @NAME:   aplicarTodasCfgTarifas
    @METHOD: GET     
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS  
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_RTA       PCK_SUBTIPOS.TI_ENTERO :=0;
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    BEGIN
        SELECT COUNT(1) CONTEO
          INTO MI_RTA
          FROM IP_CONFIGURACIONTARIFAS
         WHERE COMPANIA = UN_COMPANIA;
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_RTA:=0;
    END;
    IF MI_RTA IN (0) THEN 
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA4
                           ,UN_TABLAERROR => 'IP_CONFIGURACIONTARIFAS'
                           ); 
        END;
    END IF;
    MI_TABLA:='IP_USUARIOS_PREDIAL';
    FOR RS IN (
        SELECT  CODTARIFA
               ,PREDIO_INICIAL
               ,PREDIO_FINAL
               ,AVALUO_INICIAL
               ,AVALUO_FINAL
               ,AREAM2_INICIAL
               ,AREAM2_FINAL
               ,AREACONST_INICIAL
               ,AREACONST_FINAL
               ,AREAHA_INICIAL
               ,AREAHA_FINAL
               ,IND_CLASEPREDIO
               ,CLASE_INICIAL
               ,CLASE_FINAL
               ,IND_TIPOESTRATO
               ,TIPO_INICIAL
               ,TIPO_FINAL
               ,IND_ESTRATO
               ,ESTRATO_INICIAL
               ,ESTRATO_FINAL
               ,PORCINICIAL
               ,PORCFINAL
          FROM IP_CONFIGURACIONTARIFAS 
         WHERE COMPANIA = UN_COMPANIA 
         ORDER BY ID)
    LOOP
        MI_CAMPOS:=' TRPCOD        = '''||RS.CODTARIFA||'''
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
        MI_CONDICION:= '    CODIGO     BETWEEN '''||RS.PREDIO_INICIAL||''' AND  '''||RS.PREDIO_FINAL||''' 
                        AND AVALUO_ANO BETWEEN '''||RS.AVALUO_INICIAL||'''  AND '''||RS.AVALUO_FINAL||'''
                        AND AREA_M2    BETWEEN '||RS.AREAM2_INICIAL||' AND '||RS.AREAM2_FINAL||' 
                        AND AREA_CONSTRUIDA BETWEEN '||RS.AREACONST_INICIAL||' AND '||RS.AREACONST_FINAL||'
                        AND AREA_HA         BETWEEN '||RS.AREAHA_INICIAL||'  AND '||RS.AREAHA_FINAL||' '
                        ||(CASE WHEN RS.IND_CLASEPREDIO NOT IN (0) 
                                THEN '  AND NVL(CLASE_PREDIO, '' '') BETWEEN '''||RS.CLASE_INICIAL||''' AND '''||RS.CLASE_FINAL||''' '
                           END)
                        ||(CASE WHEN RS.IND_TIPOESTRATO NOT IN (0)
                                THEN ' AND NVL(TIPO, '' '') BETWEEN '''||RS.TIPO_INICIAL||''' AND '''||RS.TIPO_FINAL||''' '
                           END)
                        ||(CASE WHEN RS.IND_ESTRATO NOT IN (0)
                                THEN ' AND NVL(ESTRATO_SOCIOECONOMICO, '' '') BETWEEN '''||RS.ESTRATO_INICIAL||''' AND '''||RS.ESTRATO_FINAL||''' '
                           END)
                        ||' AND PCK_SYSMAN_UTL.FC_ROUND((AREA_CONSTRUIDA*100)
                                                        /CASE WHEN AREA_M2 = 0 
                                                              THEN 1 
                                                              ELSE AREA_M2 
                                                         END,2) BETWEEN '||RS.PORCINICIAL||' AND '||RS.PORCFINAL
                        ||' AND CODIGO_NO_ACTIVO IN (0) 
                            AND INDBORRADO IN (0) ';
            BEGIN
                BEGIN
                    MI_RTA:=PCK_DATOS.FC_ACME(
                                      UN_TABLA     => MI_TABLA
                                     ,UN_ACCION    => 'M' 
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA1
                           ,UN_TABLAERROR => MI_TABLA
                           ); 
            END;
            --JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB944"));
    END LOOP;    
    RETURN -1;
END FC_APLICARTODAS_CFGTARIFAS;

FUNCTION FC_APLICAR_CFGTARIFA
/*
      NAME              : FC_APLICAR_CFGTARIFA 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/06/2017
      TIME              : 08:10 AM
      SOURCE MODULE     : 
      DESCRIPTION       : APLICA LA CONFIGURACION DE UNA TARIFA A LOS USUARIOS    
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

      PARAMETERS        : 
                          UN_USUARIO => Usuario que ingreso a la aplicación
    @NAME:   aplicarConfigTarifa
    @METHOD: GET     
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ID       IN IP_CONFIGURACIONTARIFAS.ID%TYPE
   ,UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS  
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_RTA       PCK_SUBTIPOS.TI_ENTERO :=0;
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    MI_TABLA:='IP_USUARIOS_PREDIAL';
    FOR RS IN (
        SELECT  CODTARIFA
               ,PREDIO_INICIAL
               ,PREDIO_FINAL
               ,AVALUO_INICIAL
               ,AVALUO_FINAL
               ,AREAM2_INICIAL
               ,AREAM2_FINAL
               ,AREACONST_INICIAL
               ,AREACONST_FINAL
               ,AREAHA_INICIAL
               ,AREAHA_FINAL
               ,IND_CLASEPREDIO
               ,CLASE_INICIAL
               ,CLASE_FINAL
               ,IND_TIPOESTRATO
               ,TIPO_INICIAL
               ,TIPO_FINAL
               ,IND_ESTRATO
               ,ESTRATO_INICIAL
               ,ESTRATO_FINAL
               ,PORCINICIAL
               ,PORCFINAL
          FROM IP_CONFIGURACIONTARIFAS 
         WHERE COMPANIA = UN_COMPANIA 
           AND ID       = UN_ID  
         ORDER BY ID)
    LOOP
        MI_CAMPOS:=' TRPCOD        = '''||RS.CODTARIFA||'''
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
        MI_CONDICION:= '    CODIGO     BETWEEN '''||RS.PREDIO_INICIAL||''' AND  '''||RS.PREDIO_FINAL||''' 
                        AND AVALUO_ANO BETWEEN '''||RS.AVALUO_INICIAL||'''  AND '''||RS.AVALUO_FINAL||'''
                        AND AREA_M2    BETWEEN '||RS.AREAM2_INICIAL||' AND '||RS.AREAM2_FINAL||' 
                        AND AREA_CONSTRUIDA BETWEEN '||RS.AREACONST_INICIAL||' AND '||RS.AREACONST_FINAL||'
                        AND AREA_HA         BETWEEN '||RS.AREAHA_INICIAL||'  AND '||RS.AREAHA_FINAL||' '
                        ||(CASE WHEN RS.IND_CLASEPREDIO NOT IN (0) 
                                THEN '  AND NVL(CLASE_PREDIO, '' '') BETWEEN '''||RS.CLASE_INICIAL||''' AND '''||RS.CLASE_FINAL||''' '
                                ELSE ''
                           END)
                        ||(CASE WHEN RS.IND_TIPOESTRATO NOT IN (0)
                                THEN ' AND NVL(TIPO, '' '') BETWEEN '''||RS.TIPO_INICIAL||''' AND '''||RS.TIPO_FINAL||''' '
                           END)
                        ||(CASE WHEN RS.IND_ESTRATO NOT IN (0)
                                THEN ' AND NVL(ESTRATO_SOCIOECONOMICO, '' '') BETWEEN '''||RS.ESTRATO_INICIAL||''' AND '''||RS.ESTRATO_FINAL||''' '
                           END)
                        ||' AND PCK_SYSMAN_UTL.FC_ROUND((AREA_CONSTRUIDA*100)
                                                        /CASE WHEN AREA_M2 = 0 
                                                              THEN 1 
                                                              ELSE AREA_M2 
                                                         END,2) BETWEEN '||RS.PORCINICIAL||' AND '||RS.PORCFINAL
                        ||' AND CODIGO_NO_ACTIVO IN (0) 
                            AND INDBORRADO IN (0) ';
            BEGIN
                BEGIN
                    MI_RTA:=PCK_DATOS.FC_ACME(
                                      UN_TABLA     => MI_TABLA
                                     ,UN_ACCION    => 'M' 
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGTARIFA1
                           ,UN_TABLAERROR => MI_TABLA
                           ); 
            END;
            --JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB944"));
    END LOOP;    
    RETURN -1;
END FC_APLICAR_CFGTARIFA;

--10

PROCEDURE PR_EXINTXDUPLICIDADCAT
  /*
  NAME              : PR_EXINTXDUPLICIDADCAT En Access --> cmdAceptar_Click()
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 30/06/2017
  TIME              : 10:40 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       :
  @NAME:  exencionDeInteresPorDuplicidad
  @METHOD:  POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMEROORDEN     IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_PREDIO          IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_ANOINICIAL      IN PCK_SUBTIPOS.TI_ENTERO,
    UN_ANOFINAL        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_RESOLUCION      IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_ELABORADOPOR    IN IP_USUARIOS_PREDIAL.OBSERVACIO%TYPE,
    UN_FIRMADOPOR      IN IP_USUARIOS_PREDIAL.OBSERVACIO%TYPE,
    UN_FECHARESOLUCION IN VARCHAR2,
    UN_TIPOEXENCION    IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO) 
AS
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_OBSERVACION        VARCHAR(3000 CHAR);

BEGIN
  IF UN_TIPOEXENCION <>0 THEN
    MI_CAMPOS       := 'C2=0, C4=0, IND_EXEINT_DUP=-1
                       ,DATE_MODIFIED = SYSDATE
                       ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
  ELSE
    MI_CAMPOS := 'IND_EXEINT_DUP=0
                  ,DATE_MODIFIED = SYSDATE
                  ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
  END IF;
  MI_CONDICION      := 'COMPANIA = '''||UN_COMPANIA||'''                  
                    AND NUMERO_ORDEN = '''||UN_NUMEROORDEN||'''                  
                    AND  CODIGO = '''||UN_PREDIO||'''                  
                    AND PREANO BETWEEN '||UN_ANOINICIAL||' AND '||UN_ANOFINAL;


  BEGIN
   BEGIN
        PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOS' 
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS 
                                               ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;   

   END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                      MI_MSGERROR(1).CLAVE := 'PREDIO';
                      MI_MSGERROR(1).VALOR := UN_PREDIO;
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE
                            ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_ACT_FACTURADO
                            ,UN_REEMPLAZOS => MI_MSGERROR
                            );

  END; 
  IF UN_TIPOEXENCION <> 0 THEN

    MI_OBSERVACION := 'Predio excento de intereses para las vigencias '||UN_ANOINICIAL||' y '||UN_ANOFINAL||' 
                       con Res. '||UN_RESOLUCION||' del '||UN_FECHARESOLUCION||' elaborada por: '||UN_ELABORADOPOR||' 
                       y firmada por: '||UN_FIRMADOPOR||'';

    MI_CAMPOS  := 'IND_EXEINT_DUP=-1
                  ,RESOLUCION_EXTINT_DUP='''||UN_RESOLUCION||'''
                  ,OBSERVACIO= '''||MI_OBSERVACION||'''
                  ,DATE_MODIFIED = SYSDATE
                  ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
  ELSE


   MI_OBSERVACION :=  'Cancela Predio excento de intereses para las vigencias '||UN_ANOINICIAL||' y '||UN_ANOFINAL||' 
                       con Res. '||UN_RESOLUCION||' del '||UN_FECHARESOLUCION||'  elaborada por: '||UN_ELABORADOPOR||'  
                       y firmada por: '||UN_FIRMADOPOR||'';

   MI_CAMPOS := 'IND_EXEINT_DUP=0,RESOLUCION_EXTINT_DUP='''||UN_RESOLUCION||'''
                ,OBSERVACIO= '''||MI_OBSERVACION||'''
                ,DATE_MODIFIED = SYSDATE
                ,MODIFIED_BY   = '''||UN_USUARIO||''' ';

  END IF;



  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||''' 
                AND NUMERO_ORDEN = '''||UN_NUMEROORDEN||'''                
                AND CODIGO = '''||UN_PREDIO||'''';

  BEGIN
   BEGIN
      PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_USUARIOS_PREDIAL' 
                                             ,UN_ACCION    => 'M' 
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);      

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                
  END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                      MI_MSGERROR(1).CLAVE := 'PREDIO';
                      MI_MSGERROR(1).VALOR := UN_PREDIO;
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE
                            ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_ACT_USPREDIAL
                            ,UN_REEMPLAZOS => MI_MSGERROR
                            );
 END; 
END PR_EXINTXDUPLICIDADCAT;

FUNCTION FC_PREPARAR_PERIODO
/*
    NAME              : FC_ANULAR_PRESCRIPCION --> se trae del controlador
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
    DATE MIGRADOR     : 04/07/2017
    TIME              : 12:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : proceso para la preparación del periodo    
    MODIFICATIONS     : 
    PARAMETERS        :   UN_COMPANIA      => Compañia de ingreso a la aplicación
                          UN_USUARIO       => Usuario que accede al sistema 
                          UN_ANO           => año en el cual se prepara el periodo
                          UN_MES           => mes en el cual se prepara el perido
                          UN_PERIODO_BASE  => perido base
                          UN_NUMERO_ORDEN  => constante numero de orden de predial       
    @NAME:  prepararPeriodo
    @METHOD:  GET
  */ 
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
    UN_ANO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES           IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO_BASE  IN PCK_SUBTIPOS.TI_CAMPOS,
    UN_NUMERO_ORDEN  IN PCK_SUBTIPOS.TI_CAMPOS

) RETURN PCK_SUBTIPOS.TI_ENTERO
AS 
    MI_RPTA        PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPO       PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_CAMPO:='';
  MI_RPTA:=0;
  BEGIN
      BEGIN
        SELECT 'X' INTO MI_CAMPO
          FROM IP_DESCUENTOS_ESPECIALES
        WHERE ANO   = UN_ANO
          AND MES   = UN_MES;  
      EXCEPTION
           WHEN NO_DATA_FOUND THEN
           MI_CAMPO:=NULL;
    END; 
    IF MI_CAMPO = '' OR MI_CAMPO IS NULL  THEN   

        MI_TABLA:='IP_DESCUENTOS_ESPECIALES';      
        MI_CAMPOS:='COMPANIA,'||
                    'NUMERO_ORDENI,'||
                    'NUMERO_ORDENF,'||
                    'ANO,'||
                    'MES,'||
                    'CONSECUTIVO,'||
                    'CODIGO_INICIAL,'||
                    'CODIGO_FINAL,'||
                    'INCREM_INICIAL,'||
                    'INCREM_FINAL,'||
                    'AREA_CONST_ANT,'||
                    'AREA_CONST_ACTUAL,'||
                    'IND_PRIORIDAD,'||
                    'PORC_DSCTO,'||
                    'FECHA_LIMITE,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';
        MI_VALORES:='SELECT '||
                    'COMPANIA,'||
                    ''''||UN_NUMERO_ORDEN||''','||
                    ''''||UN_NUMERO_ORDEN||''','||
                    UN_ANO||','||              
                    UN_MES||','||            
                    'CONSECUTIVO,'||
                    'CODIGO_INICIAL,'||
                    'CODIGO_FINAL,'||
                    'INCREM_INICIAL,'||
                    'INCREM_FINAL,'||
                    'AREA_CONST_ANT,'||
                    'AREA_CONST_ACTUAL,'||
                    'IND_PRIORIDAD,'||
                    'PORC_DSCTO,'||
                    'LAST_DAY(''01/'||UN_MES||'/'||UN_ANO||'''),'||
                    'SYSDATE,'||
                    ''''||UN_USUARIO||''''||
                    ' FROM   IP_DESCUENTOS_ESPECIALES '||
                    ' WHERE  ANO = SUBSTR('''||UN_PERIODO_BASE||''', 0, 4) '||
                    ' AND MES = SUBSTR('''||UN_PERIODO_BASE ||''', 6, 2)';        

        MI_RPTA:=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLA,
                                   UN_ACCION   =>'IS',
                                   UN_CAMPOS   =>MI_CAMPOS,
                                   UN_VALORES  =>MI_VALORES);               
     END IF;

           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;               

   END;
RETURN  MI_RPTA;         
        EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                  MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (2).VALOR :=  MI_TABLA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                             UN_TABLAERROR  => MI_TABLA,
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS,
                                             UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_INSER_PREPERIODO); 
            WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN             
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,                                                                      
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_NO_DATA_PREPERIODO); 


END FC_PREPARAR_PERIODO;

--12
PROCEDURE PR_REGISTRARPAGOSWEB
(
	/*
       NAME              : PR_REGISTRARPAGOSWEB
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
       DATE MIGRADOR     : 04/07/2017
       TIME              : 12:55 PM
       SOURCE MODULE     : IMPUESTO PREDIAL
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Procedimiento que permite insertar y actualizar la tabla IP_PAGOS_WEB
       PARAMETERS        : 	UN_COMPANIA     Compania en la que se esta trabajando.
                            UN_REFERENCIA   referencia del Pago. 
                            UN_PREDIO       Codigo del predio seleccinado. 
                            UN_NUMEROORDEN  Numero de orden del predio seleccionado. 
                            UN_PYS     		valor a facturar.
                            UN_INSCRITO     Nombre del usuario. 
                            UN_NITINSCRITO  nit del usuario. 
                            UN_USUARIO      usuario por la cual se identifica para entrar a la aplicacion. 
                            UN_ELIMINAR     si la referencia es nula enviara vacio.
       MODIFICATIONS     :

       @NAME:    registrarPagosWeb
       @METHOD:  POST
     */


	UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_REFERENCIA   IN VARCHAR2,
	UN_PREDIO       IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_NUMEROORDEN  IN PCK_SUBTIPOS.TI_NUMORDEN,
	UN_PYS     		  IN VARCHAR2,
	UN_INSCRITO     IN VARCHAR2,
	UN_NITINSCRITO  IN VARCHAR2,
	UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
	UN_ELIMINAR     IN VARCHAR2
)

	AS 
		MI_CAMPOS  		PCK_SUBTIPOS.TI_CAMPOS;
		MI_VALORES 		PCK_SUBTIPOS.TI_VALORES;
		MI_CONDICION 	PCK_SUBTIPOS.TI_CONDICION;
		MI_TABLA 		PCK_SUBTIPOS.TI_TABLA;
		MI_REEMPLAZOS 	PCK_SUBTIPOS.TI_CLAVEVALOR;
	BEGIN 
		MI_TABLA := 'IP_PAGOS_WEB';
		MI_CAMPOS := 'COMPANIA,TIPO,REFERENCIA, CODIGO_PREDIO,NUMERO_ORDEN, FECHA_EXPEDICION, VALOR,FECHA_LIMITE_PAG, PAGO,ANULADO,
						NOMBRE_NO_INSCRITRO, NIT_NO_INSCRITRO, ANO_GENERADO, DATE_CREATED, CREATED_BY';
		MI_VALORES := ''''||UN_COMPANIA||''',''PS'','''||UN_REFERENCIA||''','''||UN_PREDIO||''','''||UN_NUMEROORDEN||''',SYSDATE,'||UN_PYS||'
						,SYSDATE,0,0,'''||UN_INSCRITO||''','''||UN_NITINSCRITO||''',EXTRACT(YEAR FROM SYSDATE),SYSDATE,'''||UN_USUARIO||'''';

		BEGIN 
			BEGIN 
				PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
	            	              UN_TABLA   => MI_TABLA
	            	             ,UN_ACCION  => 'I'
	            	             ,UN_CAMPOS  => MI_CAMPOS
	            	             ,UN_VALORES => MI_VALORES);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
					MI_REEMPLAZOS(0).CLAVE:='REFERENCIA';
					MI_REEMPLAZOS(0).VALOR:=UN_REFERENCIA;
					MI_REEMPLAZOS(1).CLAVE:='PREDIO';
					MI_REEMPLAZOS(1).VALOR:=UN_PREDIO;
				RAISE PCK_EXCEPCIONES.EXC_RECAUDOPREDIAL;
	        END;	
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
						PCK_ERR_MSG.RAISE_WITH_MSG(
							UN_EXC_COD    => SQLCODE,
							UN_TABLAERROR => MI_TABLA,
							UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PAGOSWEB,
							UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;	

		IF UN_ELIMINAR <> '' THEN 
			MI_CAMPOS := 'ANULADO = -1 , ANULADO_POR = '''||UN_USUARIO||''', FECHA_ANULADO = SYSDATE , 
							DATE_MODIFIED = SYSDATE, MODIFIED_BY = '''||UN_USUARIO||'''';
			MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND TIPO = ''PS'' AND CODIGO_PREDIO = '''||UN_PREDIO||''' AND NUMERO_ORDEN = '''||UN_NUMEROORDEN||'''
							AND PAGO IN(0) AND ANULADO IN(0) AND REFERENCIA NOT IN('''||UN_REFERENCIA||''')';

			BEGIN
				BEGIN 
					PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
									UN_TABLA   => MI_TABLA,
									UN_ACCION  => 'M',
									UN_CAMPOS  => MI_CAMPOS	,
									UN_CONDICION => MI_CONDICION);
					EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
						MI_REEMPLAZOS(0).CLAVE:='PREDIO';
						MI_REEMPLAZOS(0).VALOR:= UN_PREDIO;
					RAISE PCK_EXCEPCIONES.EXC_RECAUDOPREDIAL;
				END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
							PCK_ERR_MSG.RAISE_WITH_MSG(
							UN_EXC_COD    => SQLCODE,
							UN_TABLAERROR => MI_TABLA,
							UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PAGOSWEBACT,
							UN_REEMPLAZOS => MI_REEMPLAZOS);
			END;
		END IF;	
END PR_REGISTRARPAGOSWEB;		

--13
FUNCTION FC_ACTUALIZAR_INCR_AUTOAVALUO
/*
        NAME              : ACTUALIZAR_INCR_AUTOAVALUO -->
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 07/07/2017
        TIME              : 08:00 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Carga datos de autoavaluo en tabla temporal.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_NUMERO_ORDEN     => Numero de orden enviada desde controlador

        @NAME  :  actualizarIncrAutoavaluo
        @METHOD:  GET    
      */ 
(
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMEROORDEN IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
) RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
  MI_RTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
  MI_REGISTROS  PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    MI_TABLA      := 'IP_TMP_INCR_AUTOAVALUO';
    MI_REGISTROS  := 0;
      BEGIN
          BEGIN
              MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
              MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                                UN_ACCION => 'E', 
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
          MI_REEMPLAZOS(0).VALOR:= MI_TABLA;
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ELIMINARTABLA,
                                      UN_TABLAERROR => MI_TABLA);
      END;
          BEGIN
              FOR RS IN
                 (SELECT MAX(FACTURADOS.PREANO) KEEP (DENSE_RANK LAST 
                  ORDER BY ROWNUM) ANO,
                  MAX(AUTOAVALUO.AUTOAVALUO) KEEP (DENSE_RANK LAST
                  ORDER BY ROWNUM) AVALUO,
                  MAX(AUTOAVALUO.AVALUO_ANT) KEEP (DENSE_RANK LAST
                  ORDER BY ROWNUM) AVALUO_ANT,
                  FACTURADOS.CODIGO,
                  FACTURADOS.COMPANIA,
                  NVL(FACTURADOS.INCR_AUTOAVALUO,0) INCRE_AUTOAVALUO,
                  FACTURADOS.NUMERO_ORDEN,
                  CASE
                    WHEN FACTURADOS.ES_AUTOAVALUO NOT IN (0)
                    THEN 'SI'
                    ELSE 'NO'
                  END AUTOAVAL,
                  MAX(AUTOAVALUO.NUMERO_RADICACION) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NUMERO_RADICACION  
                  FROM IP_FACTURADOS FACTURADOS INNER JOIN IP_AUTOAVALUO AUTOAVALUO
                  ON FACTURADOS.COMPANIA            = AUTOAVALUO.COMPANIA
                  AND FACTURADOS.PREANO             = AUTOAVALUO.PAGO_ANO
                  AND FACTURADOS.NUMERO_ORDEN       = AUTOAVALUO.NUMERO_ORDEN
                  AND FACTURADOS.CODIGO             = AUTOAVALUO.CODPRE
                  WHERE FACTURADOS.COMPANIA         = UN_COMPANIA
                  AND FACTURADOS.NUMERO_ORDEN       = UN_NUMEROORDEN
                  AND FACTURADOS.PAGADO NOT        IN(0)
                  AND FACTURADOS.ES_AUTOAVALUO NOT IN (0)
                  GROUP BY FACTURADOS.CODIGO,
                    FACTURADOS.ES_AUTOAVALUO,
                    FACTURADOS.NUMERO_ORDEN,
                    FACTURADOS.INCR_AUTOAVALUO,
                    FACTURADOS.COMPANIA,
                    NVL(FACTURADOS.INCR_AUTOAVALUO,0))
  LOOP
    MI_CAMPOS  := 'ANO,AVALUO,AVALUO_ANT,CODIGO,COMPANIA,INCRE_AUTOAVALUO,NUMERO_ORDEN,AUTOAVAL,NUM_RADICACION,DATE_CREATED,CREATED_BY';
    MI_VALORES := ''''|| RS.ANO ||''', 
                  '''|| RS.AVALUO ||''', 
                  '''|| RS.AVALUO_ANT ||''',                        
                  '''|| RS.CODIGO ||''',
                  '''|| RS.COMPANIA ||''', 
                  '''|| RS.INCRE_AUTOAVALUO ||''',                        
                  '''|| RS.NUMERO_ORDEN ||''',
                  '''|| RS.AUTOAVAL ||''',
                  '''|| RS.NUMERO_RADICACION||''',
                  SYSDATE,
                  '''|| UN_USUARIO ||'''';
    BEGIN
        BEGIN
            MI_RTA := 0;
            MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA => MI_TABLA ,
                                          UN_ACCION => 'I' ,
                                          UN_CAMPOS => MI_CAMPOS ,
                                          UN_VALORES => MI_VALORES);
            MI_REGISTROS := MI_REGISTROS + MI_RTA ;                              

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
        MI_REEMPLAZOS(0).VALOR:= MI_TABLA;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSER_TMPINCAUTAV
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;
  END LOOP;
END;
RETURN MI_REGISTROS;
END FC_ACTUALIZAR_INCR_AUTOAVALUO;

--14
FUNCTION FC_GENERAR_AUTOAVALUO

/*
        NAME              : FC_GENERAR_AUTOAVALUO --> en Access oprimirbtn_generar
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 07/07/2017
        TIME              : 08:00 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Realiza proceso de actualización de valores de autoavaluo.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_NUMERO_ORDEN     => Numero de orden enviada desde controlador
                            UN_USUARIO          => Usuario que realiza proceso de autoavaluo

        @NAME  :  generarAutoavaluo
        @METHOD:  GET    
      */ 
(
     UN_COMPANIA         IN    PCK_SUBTIPOS.TI_COMPANIA,
     UN_NUMERO_ORDEN     IN    IP_AUTOAVALUO.NUMERO_ORDEN%TYPE,
     UN_USUARIO        	 IN    PCK_SUBTIPOS.TI_USUARIO
) RETURN PCK_SUBTIPOS.TI_LOGICO
AS
     MI_TABLA                      PCK_SUBTIPOS.TI_TABLA;
     MI_INCR_AUTOAVALUO            IP_TMP_INCR_AUTOAVALUO.INCRE_AUTOAVALUO%TYPE;
     MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
     MI_RTA                        PCK_SUBTIPOS.TI_RTA_ACME;
     MI_REEMPLAZOS                 PCK_SUBTIPOS.TI_CLAVEVALOR;
     MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
     MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
     MI_CONSECUTIVOAVALUO          PCK_SUBTIPOS.TI_LONG;
     MI_CONSECUTIVORAD             PCK_SUBTIPOS.TI_LONG;
     MI_CONSECTF                   IP_AUTOAVALUO.NUMERO_RADICACION%TYPE;
     MI_ULTIMODEFECHA_CREA_REGISTRO  IP_AUTOAVALUO.DATE_CREATED%TYPE;
     MI_CONSECUTIVO                IP_AUTOAVALUO.CONSECT%TYPE;
     MI_GENERACONSECUTIVORADAUTO   VARCHAR2(5 CHAR);
     MI_CONSECUTIVOINICIAL         PARAMETRO.VALOR%TYPE;
     MI_REG_ACT                    PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN
    MI_TABLA   :='IP_FACTURADOS';
    FOR RS IN(SELECT  TMP_INCR_AUTOAVALUO.CODIGO,
                      TMP_INCR_AUTOAVALUO.ANO,
                      TMP_INCR_AUTOAVALUO.AUTOAVAL,
                      TMP_INCR_AUTOAVALUO.INCRE_AUTOAVALUO,
                      TMP_INCR_AUTOAVALUO.NUMERO_ORDEN
                FROM IP_TMP_INCR_AUTOAVALUO TMP_INCR_AUTOAVALUO
                WHERE COMPANIA = UN_COMPANIA)
    LOOP    
        MI_CAMPOS   := 'INCR_AUTOAVALUO      = ''' || CASE WHEN RS.INCRE_AUTOAVALUO NOT IN (0)
                                                           THEN -1 
                                                           ELSE 0 
                                                      END|| ''',
                       DATE_MODIFIED         = SYSDATE,
                       MODIFIED_BY           = ''' || UN_USUARIO || ''' ';
        MI_CONDICION:=   '       CODIGO      = ''' || RS.CODIGO           || '''' || 
                         ' AND PREANO       >= ''' || RS.ANO              || '''' ||
                         ' AND COMPANIA      = ''' || UN_COMPANIA         || '''';
        BEGIN
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
                MI_REEMPLAZOS(0).VALOR:=  MI_TABLA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUA_AUINCREMENTO
                                                ,UN_TABLAERROR => MI_TABLA
                                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
    END LOOP; 
    MI_REG_ACT := 0;
    FOR RS IN(SELECT  FACTURADOS.CODIGO,
                      FACTURADOS.NUMERO_ORDEN,
                      FACTURADOS.PREANO,
                      FACTURADOS.TRPCOD TRPCODF,
                      FACTURADOS.AVALUO,
                      MAX(AUTOAVALUO.AVALUO_ANT) KEEP (DENSE_RANK LAST  ORDER BY ROWNUM) AVALUO_ANT,
                      MAX(AUTOAVALUO.AUTOAVALUO) KEEP (DENSE_RANK LAST  ORDER BY ROWNUM) AUTOAVALUO,
                      MAX(AUTOAVALUO.TRPCOD) KEEP (DENSE_RANK LAST
                      ORDER BY ROWNUM) TRPCOD,
                      MAX(AUTOAVALUO.TRPCOD_ANT) KEEP (DENSE_RANK LAST
                      ORDER BY ROWNUM) TRPCOD_ANT,
                      MAX(AUTOAVALUO.NUMERO_RADICACION) KEEP (DENSE_RANK LAST
                      ORDER BY ROWNUM) NUMERO_RADICACION
                 FROM IP_FACTURADOS FACTURADOS
                     INNER JOIN IP_AUTOAVALUO AUTOAVALUO
                        ON FACTURADOS.COMPANIA              = AUTOAVALUO.COMPANIA
                        AND FACTURADOS.NUMERO_ORDEN         = AUTOAVALUO.NUMERO_ORDEN
                        AND FACTURADOS.CODIGO               = AUTOAVALUO.CODPRE
                WHERE FACTURADOS.COMPANIA           = UN_COMPANIA
                  AND FACTURADOS.INCR_AUTOAVALUO NOT IN (0)
                GROUP BY  FACTURADOS.CODIGO,
                          FACTURADOS.NUMERO_ORDEN,
                          FACTURADOS.PREANO,
                          FACTURADOS.TRPCOD,
                          FACTURADOS.AVALUO,
                          FACTURADOS.INCR_AUTOAVALUO)
    LOOP
        MI_CAMPOS := ' AVALUOANT 	= '''||RS.AVALUO||'''
                     , AVALUO    	= '''||RS.AUTOAVALUO ||''' 
                     , TARIFAANT 	= '''||FC_ASIGNA_TARIFA(
                                         UN_COMPANIA  => UN_COMPANIA,
                                         UN_TRPCOD    => RS.TRPCODF, 
                                         UN_TRPANO    => RS.PREANO, 
                                         UN_AVALUO    => RS.AVALUO) ||''' 
                     , TRPCOD    	= '''||FC_ASIGNA_TARIFA(
                                          UN_COMPANIA  => UN_COMPANIA,
                                          UN_TRPCOD    => RS.TRPCODF, 
                                          UN_TRPANO    => RS.PREANO, 
                                          UN_AVALUO    => RS.AUTOAVALUO) ||''' 
                     , PREIND     	=  ''M''  
                     , ES_AUTOAVALUO    = -1
                     , DATE_MODIFIED    =  SYSDATE  
                     , MODIFIED_BY     = '''|| UN_USUARIO ||'''' ;
         MI_CONDICION :=  '    PREANO = '''||RS.PREANO||''' 
            		           AND CODIGO   ='''|| RS.CODIGO ||'''
                           AND COMPANIA ='''|| UN_COMPANIA ||'''';
         BEGIN
             BEGIN
                 MI_TABLA := 'IP_FACTURADOS';
                 MI_RTA   := PCK_DATOS.FC_ACME (
                                       UN_TABLA     => MI_TABLA, 
                                       UN_ACCION    => 'M', 
                                       UN_CAMPOS    => MI_CAMPOS, 
                                       UN_CONDICION => MI_CONDICION);  
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
                 MI_REEMPLAZOS(0).VALOR:=  MI_TABLA;
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
             END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
             PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUA_AUINCREMENTO
                        ,UN_TABLAERROR => MI_TABLA
                        ,UN_REEMPLAZOS => MI_REEMPLAZOS);
         END;
         BEGIN 
             SELECT NVL(MAX(AUTOAVALUO.CONSECT),1) 
               INTO MI_CONSECTF
               FROM IP_AUTOAVALUO AUTOAVALUO
              WHERE COMPANIA = UN_COMPANIA;
         EXCEPTION WHEN NO_DATA_FOUND THEN 
             MI_CONSECTF:=NULL;
         END; 
         IF MI_CONSECUTIVOAVALUO <> '1' THEN
         MI_CONSECUTIVOAVALUO := MI_CONSECTF+1;
         ELSE
         MI_CONSECUTIVOAVALUO := MI_CONSECTF;
         END IF;
         MI_CONSECUTIVORAD    := RS.NUMERO_RADICACION;

         BEGIN
             SELECT AUTOAVALUO.CONSECT,
                    MAX(AUTOAVALUO.DATE_CREATED) 
                    KEEP (DENSE_RANK LAST ORDER BY ROWNUM) 
               INTO MI_CONSECUTIVO,
                    MI_ULTIMODEFECHA_CREA_REGISTRO
               FROM IP_AUTOAVALUO AUTOAVALUO
              WHERE AUTOAVALUO.CODPRE         = RS.CODIGO 
                    AND AUTOAVALUO.PAGO_ANO         = RS.PREANO
                    AND AUTOAVALUO.NUMERO_RADICACION= MI_CONSECUTIVORAD 
              GROUP BY AUTOAVALUO.CONSECT;
         EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_CONSECUTIVO := NULL;
         END;

             IF  MI_CONSECUTIVO IS NULL THEN
                 MI_GENERACONSECUTIVORADAUTO := PCK_SYSMAN_UTL.FC_PAR(
                                                               UN_COMPANIA  => UN_COMPANIA,
                                                               UN_NOMBRE 	 => 'GENERA CONSECUTIVO DE RADICADO AUTOAVALUO',
                                                               UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                               UN_FECHA_PAR => SYSDATE);
                 IF MI_GENERACONSECUTIVORADAUTO = 'NO' THEN
                     MI_CONSECUTIVOINICIAL     := PCK_SYSMAN_UTL.FC_PAR(
                                                                 UN_COMPANIA  => UN_COMPANIA,
                                                                 UN_NOMBRE 	  => 'CONSECUTIVO INICIAL DE RADICADO AUTOAVALUO',
                                                                 UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                                 UN_FECHA_PAR => SYSDATE);
                     MI_CONSECUTIVORAD         := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                                 UN_TABLA    =>  'IP_AUTOAVALUO',
                                                                 UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                                 UN_CAMPO    =>  'NUMERO_RADICACION',
                                                                 UN_INICIAL  =>  MI_CONSECUTIVOINICIAL);
                 ELSE
                    MI_CONSECUTIVORAD := MI_CONSECUTIVOAVALUO;
                 END IF;
                 MI_TABLA    :=  'IP_AUTOAVALUO';
                 MI_CAMPOS   :=  'COMPANIA,
                                  CODPRE, 
                                  NUMERO_ORDEN, 
                                  PAGO_ANO, 
                                  NUMERO_RADICACION,
                                  AVALUO_ANT, 
                                  AUTOAVALUO, 
                                  CREATED_BY,
                                  DATE_CREATED, 
                                  TIPO_DECLA,
                                  CONSECT,
                                  TRPCOD,
                                  TRPCOD_ANT';
                 MI_VALORES  :=  ''''||  UN_COMPANIA ||''', 
                                  '''||  RS.CODIGO   ||''',
                                  '''||  UN_NUMERO_ORDEN   ||''',
                                  '''||  RS.PREANO   ||''',
                                  '''||  MI_CONSECUTIVORAD   ||''',
                                  '''||  RS.AVALUO   ||''',
                                  '''||  RS.AUTOAVALUO   ||''',
                                  '''||  UN_USUARIO   ||''',
                                  SYSDATE,
                                  '''||  'I'   ||''',
                                  '''||  MI_CONSECUTIVOAVALUO ||''',
                                  '''||  FC_ASIGNA_TARIFA(
                                                       UN_COMPANIA  => UN_COMPANIA,
                                                       UN_TRPCOD    => RS.TRPCODF, 
                                                       UN_TRPANO    => RS.PREANO, 
                                                       UN_AVALUO    => RS.AUTOAVALUO) || ''',
                                  '''||  FC_ASIGNA_TARIFA(
                                                       UN_COMPANIA  => UN_COMPANIA,
                                                       UN_TRPCOD    => RS.TRPCODF, 
                                                       UN_TRPANO    => RS.PREANO, 
                                                       UN_AVALUO    => RS.AVALUO) ||'''';
                 BEGIN
                     BEGIN
                         MI_REG_ACT := PCK_DATOS.FC_ACME(  UN_TABLA => MI_TABLA,
                                                           UN_ACCION => 'I',
                                                           UN_CAMPOS => MI_CAMPOS,
                                                           UN_VALORES => MI_VALORES);
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                     MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
                     MI_REEMPLAZOS(0).VALOR:=  MI_TABLA;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUA_AUINCREMENTO
                                               ,UN_TABLAERROR => MI_TABLA
                                               ,UN_REEMPLAZOS => MI_REEMPLAZOS);
                 END;                                    
                     IF MI_REG_ACT <> 0 AND MI_GENERACONSECUTIVORADAUTO <> 'NO' THEN
                         BEGIN
                             BEGIN
                                 MI_CAMPOS    :=  ' VALOR        = '''||MI_CONSECUTIVORAD||'''
                                                    DATE_MODIFIED= SYSDATE,
                                                    MODIFIED_BY  = '''||UN_USUARIO||'''';
                                 MI_CONDICION :=  ' NOMBRE       = '''|| 'CONSECUTIVO INICIAL DE RADICADO AUTOAVALUO' ||'''
                                                    AND COMPANIA = '''|| UN_COMPANIA ||'''';
                                 MI_RTA := PCK_DATOS.FC_ACME(
                                                     UN_TABLA     =>  'PARAMETRO', 
                                                     UN_ACCION    =>  'M', 
                                                     UN_CAMPOS    =>  MI_CAMPOS, 
                                                     UN_CONDICION =>  MI_CONDICION);
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                 MI_REEMPLAZOS(0).CLAVE:= 'TABLA';
                                 MI_REEMPLAZOS(0).VALOR:=  MI_TABLA;
                                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END;
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUA_AUINCREMENTO
                                                       ,UN_TABLAERROR => MI_TABLA
                                                       ,UN_REEMPLAZOS => MI_REEMPLAZOS);
                         END;
                     END IF;
             END IF;
    END LOOP;

    RETURN -1;
END FC_GENERAR_AUTOAVALUO;

--15
FUNCTION FC_ASIGNA_TARIFA 

/*
      NAME              : FC_ASIGNA_TARIFA=>controlador: FrmincrementosautoavaluosControlador.java
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 10/07/2017
      TIME              : 08:00 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : 
      PARAMETERS        : UN_COMPANIA	    => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_TRPCOD       => CODIGO DE TARIFA DE PREDIO
                          UN_TRPANO       => AÑO AUTOAVALUO.
                          UN_AVALUO	      => VALOR DE AVALUO PREDIO.


      @NAME:  asignarTarifa 
      @METHOD:  GET
    */
(
    UN_COMPANIA IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_TRPCOD   IN  IP_TARIFAS.TRPCOD%TYPE,
    UN_TRPANO   IN  IP_TARIFAS.TRPANO%TYPE,
    UN_AVALUO   IN  IP_TARIFAS.TRPVAL%TYPE 

)RETURN VARCHAR2 
AS 
   MI_TPRCOD     VARCHAR2(10);
BEGIN
  SELECT MIN(TARIFAS.TRPCOD) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) TRPCOD INTO MI_TPRCOD
    FROM IP_TARIFAS TARIFAS 
   WHERE TARIFAS.TRPCOD LIKE SUBSTR( UN_TRPCOD ,0,1)||'%'
     AND TARIFAS.TRPANO = UN_TRPANO  
     AND UN_AVALUO      BETWEEN TARIFAS.TRPRAN1 AND TARIFAS.TRPRAN2;
  RETURN MI_TPRCOD;
END FC_ASIGNA_TARIFA;

--16
FUNCTION FC_REGISTRAR_PAGOSDOBLES
    /*
      NAME              : FC_REGISTRAR_PAGOSDOBLES=>controlador:PagosdoblesdosControlador.java
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 10/07/2017
      TIME              : 11:31 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : 
      PARAMETERS        : UN_USUARIO      => USUARIO ACTIVO EN LA SESSION.
                          UN_COMPANIA     => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_NUMERO_ORDEN => NUMERO DE ORDEN DE PREDIAL
                          UN_CODIGO       =>

      @NAME:  registrarPagosDobles 
      @METHOD:  GET
    */
   (
      UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_NUMERO_ORDEN   IN PCK_SUBTIPOS.TI_PARAMETRO,
      UN_CODIGO         IN PCK_SUBTIPOS.TI_PARAMETRO,
      UN_FACTURA        IN PCK_SUBTIPOS.TI_PARAMETRO,
      UN_PAGOD_BANCO    IN PCK_SUBTIPOS.TI_PARAMETRO,
      UN_PAGOD_PAQUETE  IN PCK_SUBTIPOS.TI_PARAMETRO,
      UN_PAGOD_ANO      IN PCK_SUBTIPOS.TI_ANIO,
      UN_PAGOD_FECHA    IN DATE     
    )
      RETURN PCK_SUBTIPOS.TI_ENTERO
    AS
      MI_RPTA             PCK_SUBTIPOS.TI_ENTERO;
      MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES          PCK_SUBTIPOS.TI_VALORES;   
      MI_CONDICION        PCK_SUBTIPOS.TI_CAMPOS;
      MI_PARAMETROS       PCK_SUBTIPOS.TI_CAMPOS;
      MI_PAGO_DOBLE       PCK_SUBTIPOS.TI_ENTERO;    
      MI_RECIBOS_PAGO     PCK_SUBTIPOS.TI_ENTERO;
      MI_BANCOS_CAB       PCK_SUBTIPOS.TI_ENTERO; 
      MI_PAGO             PCK_SUBTIPOS.TI_LOGICO;
      MI_PREVAL_RECIBOS   PCK_SUBTIPOS.TI_DOBLE;  
      MI_NROCUPONES       PCK_SUBTIPOS.TI_DOBLE;  
      MI_VLRREPORTADO     PCK_SUBTIPOS.TI_DOBLE;     
      MI_NROCUPONESACU    PCK_SUBTIPOS.TI_DOBLE;
      MI_ACUMULADO        PCK_SUBTIPOS.TI_DOBLE;
      MI_CUENTA           PCK_SUBTIPOS.TI_ENTERO;
      MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
      MI_CONTAR           PCK_SUBTIPOS.TI_ENTERO;
      MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN     
        MI_RPTA:=0;
        BEGIN  
          BEGIN
              SELECT PAGO,PREVAL INTO MI_PAGO,MI_PREVAL_RECIBOS
                FROM IP_RECIBOS_DE_PAGO  
               WHERE COMPANIA   = UN_COMPANIA   
                 AND PRECOD     = UN_CODIGO    
                 AND DOCNUM     = UN_FACTURA;

                 EXCEPTION
                      WHEN NO_DATA_FOUND THEN
                           MI_RECIBOS_PAGO:=NULL;   

                IF MI_RECIBOS_PAGO IS NULL  THEN
                  RETURN -11;
                END IF;
          END;

          IF MI_PAGO=0 THEN 
             BEGIN

                  SELECT NROCUPONES,
                         VLRREPORTADO,                    
                         NROCUPONESACU,
                         ACUMULADO INTO
                         MI_NROCUPONES,
                         MI_VLRREPORTADO,                     
                         MI_NROCUPONESACU,
                         MI_ACUMULADO
                   FROM IP_PAGO_BANCOSCAB 
                  WHERE COMPANIA      =  UN_COMPANIA  
                    AND TRUNC(PREFEC) =  TRUNC(TO_DATE(UN_PAGOD_FECHA,'DD/MM/YYYY HH24:MI:SS'))  
                    AND PAQUETE       =  UN_PAGOD_PAQUETE                                     
                    AND PAG_BAN       =  UN_PAGOD_BANCO;

                     EXCEPTION
                          WHEN NO_DATA_FOUND THEN
                           MI_BANCOS_CAB:=NULL;  
                   IF MI_BANCOS_CAB IS NULL THEN 
                      BEGIN
                          MI_TABLA:='IP_PAGO_BANCOSCAB';

                          MI_CAMPOS:='COMPANIA,'||
                                     'PREFEC,'||
                                     'PAQUETE,'||
                                     'PAG_BAN,'||
                                     'NROCUPONES,'||
                                     'VLRREPORTADO,'||
                                     'CREATED_BY,'||
                                     'DATE_CREATED';

                            MI_VALORES:=''''||UN_COMPANIA ||''','||
                                        'TO_DATE('''||UN_PAGOD_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')'||','||
                                        ''''||UN_PAGOD_PAQUETE||''','||
                                        ''''||UN_PAGOD_BANCO||''','||
                                        '1,'||  
                                        MI_PREVAL_RECIBOS||','||    
                                        ''''||UN_USUARIO||''','||
                                        'SYSDATE'; 

                             MI_RPTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I', 
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES)+MI_RPTA;
                           EXCEPTION
                               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;                               
                       END;                                   
                   ELSE
                      BEGIN

                         MI_TABLA:='IP_PAGO_BANCOSCAB';

                         MI_CAMPOS:= 'NROCUPONES    ='||MI_NROCUPONES+1||','||
                                     'VLRREPORTADO  ='||MI_VLRREPORTADO+MI_PREVAL_RECIBOS||','||
                                     'NROCUPONESACU ='||MI_NROCUPONESACU+1||','||
                                     'ACUMULADO     ='||MI_ACUMULADO+MI_PREVAL_RECIBOS||','||
                                     'MODIFIED_BY   ='''||UN_USUARIO||''','||
                                     'DATE_MODIFIED =SYSDATE';

                          MI_CONDICION:='COMPANIA           = '''||UN_COMPANIA||''''||
                                        ' AND TRUNC(PREFEC) = TRUNC(TO_DATE('''||UN_PAGOD_FECHA||''',''DD/MM/YYYY HH24:MI:SS''))'||
                                        ' AND PAQUETE       = '''||UN_PAGOD_PAQUETE||''''||
                                        ' AND PAG_BAN       = '''||UN_PAGOD_BANCO||'''';

                          MI_RPTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                      UN_ACCION    => 'M', 
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICION)+MI_RPTA;    

                          EXCEPTION
                               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                   END IF;
             END;

             BEGIN 
                 SELECT COUNT(*) CUENTA INTO MI_CUENTA
                  FROM IP_PAGO_BANCOSDET 
                 WHERE COMPANIA      =   UN_COMPANIA   
                    AND PRECOD       =   UN_CODIGO   
                    AND NUMERO_ORDEN =   UN_NUMERO_ORDEN                                     
                    AND PAG_BAN      =   UN_PAGOD_BANCO                                     
                    AND TRUNC(PREFEC)=   TRUNC(TO_DATE(UN_PAGOD_FECHA,'DD/MM/YYYY HH24:MI:SS'))   
                    AND PAQUETE      =   UN_PAGOD_PAQUETE                                     
                    AND ANO          =   UN_PAGOD_ANO   
                    AND NUMFACTURA   =   UN_FACTURA;  

                  IF MI_CUENTA = 0 THEN 
                    BEGIN

                         MI_TABLA:='IP_PAGO_BANCOSDET';

                         MI_CAMPOS :='COMPANIA,'||
                                    'NUMERO_ORDEN,'||
                                    'PREFEC,'||
                                    'PAQUETE,'||
                                    'PAG_BAN,'||
                                    'PRECOD,'||
                                    'ANO,'||
                                    'PREVAL,'||
                                    'NUMFACTURA,'||
                                    'BARRAS,'||
                                    'USUARIO,'||
                                    'CREATED_BY,'||
                                    'DATE_CREATED';

                          MI_VALORES :=''''||UN_COMPANIA||''','||
                                       ''''||UN_NUMERO_ORDEN||''','||
                                       'TO_DATE('''||UN_PAGOD_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')'||','||
                                       ''''||UN_PAGOD_PAQUETE||''','||
                                       ''''||UN_PAGOD_BANCO||''','||
                                       ''''||UN_CODIGO||''','||
                                       UN_PAGOD_ANO||','|| 
                                       MI_PREVAL_RECIBOS||','||
                                       ''''||UN_FACTURA||''','||
                                       ''''||UN_CODIGO||''','||
                                       ''''||UN_USUARIO||''','||
                                       ''''||UN_USUARIO||''','||
                                       'SYSDATE';

                          MI_RPTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                       UN_ACCION  => 'I', 
                                                       UN_CAMPOS  => MI_CAMPOS,
                                                       UN_VALORES => MI_VALORES)+MI_RPTA;               
                          EXCEPTION
                               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;   
                      END;
                  END IF;

             END;
              BEGIN

                  SELECT COUNT(*) INTO MI_CONTAR
                    FROM IP_PAGOSDOBLES  
                    WHERE  COMPANIA    =   UN_COMPANIA    
                      AND NUMERO_ORDEN =   UN_NUMERO_ORDEN     
                      AND PRECOD       =   UN_CODIGO    
                      AND DOCNUM       =   UN_FACTURA   
                      AND PREANO       =   UN_PAGOD_ANO;

                 -- EXCEPTION
                   --    WHEN NO_DATA_FOUND THEN
                     --       MI_PAGO_DOBLE:=NULL;

                  IF  MI_CONTAR=0 THEN 
                    BEGIN

                       MI_TABLA:='IP_PAGOSDOBLES';

                       MI_CAMPOS :='COMPANIA,'||
                                   'NUMERO_ORDEN,'||
                                   'DOCNUM,'||
                                   'PRECOD,'||
                                   'PREANO,'||
                                   'VALOR,'||
                                   'FECHA,'||                              
                                   'TIPO,'||
                                   'BANCO,'||
                                   'GPAGODOBLE,'||
                                   'CREATED_BY,'||
                                   'DATE_CREATED';

                        MI_VALORES:=''''||UN_COMPANIA||''','||
                                    ''''||UN_NUMERO_ORDEN||''','||
                                    ''''||UN_FACTURA||''','||
                                    ''''||UN_CODIGO||''','||
                                    UN_PAGOD_ANO||','|| 
                                    MI_PREVAL_RECIBOS||','||
                                    'TO_DATE('''||UN_PAGOD_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')'||','||                              
                                    '''S'','||
                                    ''''||UN_PAGOD_BANCO||''','||
                                    '-1,'||
                                    ''''||UN_USUARIO||''','||
                                    'SYSDATE';

                         MI_RPTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES)+MI_RPTA; 

                         EXCEPTION
                              WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;   
                      END;                                  

                  END IF;
              END;   

             BEGIN
                    MI_TABLA:='IP_RECIBOS_DE_PAGO';

                    MI_CAMPOS :='ANULADO            = 0,'||
                                 'PAGO              = -1,'||
                                 'PAG_BANPAG        = '''||UN_PAGOD_BANCO||''','||
                                 'PREFECPAG         =  TO_DATE('''||UN_PAGOD_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')'||','||
                                 'PAQUETEPAG        ='''||UN_PAGOD_PAQUETE||''','||
                                 'FECHA_REGRECAUDO  =SYSDATE,'||
                                 'USUARIO_REGRECAUDO= '''||UN_USUARIO||''','||
                                 'MODIFIED_BY       ='''||UN_USUARIO||''','||
                                 'DATE_MODIFIED     =SYSDATE';

                    MI_CONDICION:='COMPANIA    ='''||UN_COMPANIA||''''||
                                  ' AND DOCNUM = '''||UN_FACTURA||''''||
                                  ' AND PRECOD = '''||UN_CODIGO||'''';

                    MI_RPTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M', 
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION)+MI_RPTA; 
                   EXCEPTION
                        WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;           

             END;

             BEGIN 
                MI_TABLA:='IP_PAGOSDOBLES';

                MI_CAMPOS:='OBSERVACIONES=''Pago doble para el año '||UN_PAGOD_ANO||'''';

                MI_CONDICION:='COMPANIA          ='''||UN_COMPANIA||''''||
                              ' AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN||''''||
                              ' AND PRECOD       ='''||UN_CODIGO||'''';

                MI_RPTA :=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION)+MI_RPTA;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;
             END;
          ELSE 
            RETURN -10;            
          END IF;

        END;       

 RETURN MI_RPTA;    

      EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN     

                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_UPDATE,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);

               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN     

                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_INSERT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);

END FC_REGISTRAR_PAGOSDOBLES;

--17
FUNCTION FC_ACTUALIZAINDACTIVO
/*
    NAME              : FC_ACTUALIZAINDACTIVO --> se trae del controlador
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 10/07/2017
    TIME              : 12:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Desactiva los demas consecutivos para ese tipo de numeracion

    MODIFICATIONS     : 
    @NAME:  actualizarIndicadorActivo
    @METHOD:  GET
  */ 
(
     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_TIPO       IN IP_NUMEROSDEFACTURA.TIPO%TYPE
    ,UN_IND_ACTIVO IN PCK_SUBTIPOS.TI_LOGICO 
    ,UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_EXISTE     PCK_SUBTIPOS.TI_LOGICO;
    MI_RTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION; 
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    BEGIN 
        SELECT CASE WHEN COUNT(*)>0
                    THEN -1
                    ELSE 0
               END ACTI
          INTO MI_EXISTE
          FROM IP_NUMEROSDEFACTURA
         WHERE COMPANIA = UN_COMPANIA 
           AND TIPO     = UN_TIPO
           AND ACTIVO   NOT IN (0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE:=0;
    END;
    IF UN_IND_ACTIVO NOT IN (0) AND MI_EXISTE NOT IN (0) THEN 
        MI_TABLA:= 'IP_NUMEROSDEFACTURA';
        MI_CAMPOS:= ' ACTIVO        = 0 
                     ,DATE_MODIFIED = SYSDATE
                     ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
        MI_CONDICION:= '    COMPANIA = '''||UN_COMPANIA||''' 
                        AND TIPO     = '''||UN_TIPO||'''
                        AND ACTIVO   NOT IN (0)';
        BEGIN 
            BEGIN
                MI_RTA:=PCK_DATOS.FC_ACME(
                	              UN_TABLA     => MI_TABLA
                	             ,UN_ACCION    => 'M'
                	             ,UN_CAMPOS    => MI_CAMPOS
                	             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='TIPO';
                MI_REEMPLAZOS(0).VALOR:=UN_TIPO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE
                       ,UN_TABLAERROR  => 'IP_NUMEROSDEFACTURA'
                       ,UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_ACTIVARCONSECUTIV
                       ,UN_REEMPLAZOS  => MI_REEMPLAZOS);
        END;
    END IF;
    RETURN -1;
END FC_ACTUALIZAINDACTIVO;

--18
FUNCTION FC_VALIDARCONSECUTIVO 
/*
    NAME              : FC_ANULAR_PRESCRIPCION --> se trae del controlador
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 10/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Valida si se genera un consecutivo valido para ese registro, 
                        si es verdadero el consecutivo es incorrecto.

    MODIFICATIONS     : 
    @NAME:  validarConsecutivo
    @METHOD:  GET
  */ 
(
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_SECUENCIA   IN IP_NUMEROSDEFACTURA.SECUENCIA%TYPE
   ,UN_CONSECUTIVO IN IP_NUMEROSDEFACTURA.CONSECUTIVO%TYPE 
   ,UN_TIPO        IN IP_NUMEROSDEFACTURA.TIPO%TYPE 	 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_DIGITOS   IP_TIPOSNUMERACION.DIGITOS%TYPE;
    MI_ULTIMOCON IP_NUMEROSDEFACTURA.CONSECUTIVO%TYPE; 
BEGIN
    BEGIN 
        SELECT NVL(DIGITOS,10) DIGITOS 
          INTO MI_DIGITOS
          FROM IP_TIPOSNUMERACION 
         WHERE CODIGO = UN_TIPO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIGITOS:=0;
    END;    
    IF UN_CONSECUTIVO NOT IN (0) THEN 
        BEGIN 
            SELECT COUNT(1) ULTIMOCON 
              INTO MI_ULTIMOCON 
              FROM IP_NUMEROSDEFACTURA 
             WHERE COMPANIA                          =  UN_COMPANIA
               AND SECUENCIA                         <> UN_SECUENCIA
               AND LPAD(CONSECUTIVO, MI_DIGITOS,'0') >= UN_CONSECUTIVO
               AND TIPO                              =  UN_TIPO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_ULTIMOCON:=0;
        END;
        IF MI_ULTIMOCON NOT IN (0) THEN
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                           ,UN_TABLAERROR  => 'IP_NUMEROSDEFACTURA'
                           ,UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_VALIDARCONSECUTIV);
            END;
        END IF;        
    END IF;
    RETURN -1;
END FC_VALIDARCONSECUTIVO;

--19
FUNCTION FC_ENCABEZADO_COLUMNA
/*
    NAME              : FC_ENCABEZADO_COLUMNA --> EN ACCES Encabezado_Columna
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 11/07/2017
    TIME              : 02:00 PM
    SOURCE MODULE     : PredialP2017.01.06VB
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : RETORNA EL ENCABEZADO DE LA TABLA IP_CONCEPTOS DE ACUERDO AL CODIGO ENVIADO  
    PARAMETERS        : UN_COMPANIA  => COMPANIA DE INGRESO A LA APLICACION
                        UN_NIT       => NIT DE LA COMPANIA DE INGRESO A LA APLICACION 
                        UN_CODIGO    => CODIGO DEL CONCEPTO DEL CUAL SE REQUIERE EL ENCABEZADO
                        UN_ANIO      => ANO PARA FILTRAR EL CONCEPTO
    @NAME:  obtenerEncabezadoColumna
    @METHOD:  GET
  */ 
(
 UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,
 UN_NIT           IN  COMPANIA.NITCOMPANIA%TYPE,
 UN_CODIGO        IN  PCK_SUBTIPOS.TI_ENTERO, 
 UN_ANIO          IN  PCK_SUBTIPOS.TI_ANIO   DEFAULT NULL
)
RETURN VARCHAR2
AS
    MI_ANIO         PCK_SUBTIPOS.TI_ANIO;
    MI_NOMBRE       IP_CONCEPTOS.NOMBRE%TYPE;
    MI_ENCABEZADO   IP_CONCEPTOS.ENCABEZADO%TYPE;
    MI_CODIGO       IP_CONCEPTOS.CODIGO%TYPE;
BEGIN 

   BEGIN 
       SELECT ENCABEZADO,
              NOMBRE,
              CODIGO
         INTO MI_ENCABEZADO,
              MI_NOMBRE,
              MI_CODIGO
         FROM IP_CONCEPTOS 
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = UN_ANIO
          AND CODIGO   = UN_CODIGO;
   EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CODIGO := NULL;
   END;

   IF MI_CODIGO IS NOT NULL THEN
     IF UN_NIT = '800095728-2' THEN 
        MI_ENCABEZADO:=MI_NOMBRE;
     END IF;
   ELSE
      MI_ENCABEZADO:=' ';
   END IF;

   MI_ENCABEZADO:=REPLACE(REPLACE(MI_ENCABEZADO,'.', ' '),'.', ' ');
   RETURN MI_ENCABEZADO;
END FC_ENCABEZADO_COLUMNA;

--20
FUNCTION FC_PREPARARLISTADOGRAL 
    /*
      NAME              : FC_PREPARARLISTADOGRAL --> EN ACCESS 
      AUTHORS           : STEFANINI SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 12/07/2017
      TIME              : 3:49 PM
      SOURCE MODULE     : PredialP2016.05.06
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Construye el codigo SQL para enviar como reemplazo a la consulta los reportes 000850PREDIALLISGRAL y 000852LISTADOGENERAL 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA             => Compañia de ingreso a la aplicación
                          UN_CLAVE                => Indicador para incluir el filtro del nombre por palabra clave
                          UN_PALABRACLAVE         => Palabra con la cual se desea filtrar el nombre
                          UN_NOMBRES              => Indicador para incluir el filtro del nombre entre dos palabras
                          UN_NOMBREINICIAL        => Primer nombre por el que se desea filtrar el resultado de la consulta
                          UN_NOMBREFINAL          => Segundo nombre por el que se desea filtrar el resultado de la consulta
                          UN_DIRECCIONINICIAL     => Primer valor por el que se desea filtrar la dirección del predio
                          UN_DIRECCIONFINAL       => Segundo valor por el que se desea filtrar la dirección del predio
                          UN_INDICADOR            => Valor del indicador "Presentar Indicador de Excento" al generar el informe
                          UN_SOLOPREDIOS          => Valor del indicador "Mostrar Solo Predios Excentos" al generar el informe
                          UN_ORDENADO             => Inidica el tipo de ordenamiento con el que se desea generar el informe
      @NAME  :  prepararListadoGeneral
      @METHOD:  GET     
    */ 
(
  UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLAVE                IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_PALABRACLAVE         IN  PCK_SUBTIPOS.TI_USUARIO,
  UN_NOMBRES              IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_NOMBREINICIAL        IN  IP_USUARIOS_PREDIAL.NOMBRE%TYPE,
  UN_NOMBREFINAL          IN  IP_USUARIOS_PREDIAL.NOMBRE%TYPE,
  UN_DIRECCIONINICIAL     IN  IP_USUARIOS_PREDIAL.DIRECCION%TYPE,
  UN_DIRECCIONFINAL       IN  IP_USUARIOS_PREDIAL.DIRECCION%TYPE,
  UN_INDICADOR            IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_SOLOPREDIOS          IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_ORDENADO             IN  PCK_SUBTIPOS.TI_LOGICO -- es number de uno, no boolean
)
RETURN CLOB 
AS 
  MI_CONDICION            CLOB;
  MI_PARFORMATO           PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN

  MI_PARFORMATO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'FORMATO LISTADO GENERAL DE PREDIOS',
                                         UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                         UN_FECHA_PAR => SYSDATE);

  IF UN_CLAVE NOT IN (0) THEN
     IF UN_PALABRACLAVE IS NOT NULL THEN 
          MI_CONDICION := ' AND IP_USUARIOS_PREDIAL.NOMBRE LIKE ''%' || UPPER(UN_PALABRACLAVE) ||'%'' ';
     ELSE
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_LGPALABRACLAVE);
      END;                        
     END IF;

  -- Si el indicador "Entre Nombres" esta seleccionado   
  ELSIF UN_NOMBRES NOT IN (0) THEN
    IF UN_NOMBREINICIAL IS NOT NULL AND UN_NOMBREFINAL IS NOT NULL THEN
      IF SUBSTR(UPPER(UN_NOMBREINICIAL),1,3) NOT LIKE 'TOD' AND SUBSTR(UPPER(UN_NOMBREFINAL),1,3) NOT LIKE 'TOD' THEN 
        MI_CONDICION := MI_CONDICION ||  ' AND (   IP_USUARIOS_PREDIAL.NOMBRE LIKE ''%' || UPPER(UN_NOMBREINICIAL) || '%'' '||
                                              ' OR IP_USUARIOS_PREDIAL.NOMBRE LIKE ''%' || UPPER(UN_NOMBREFINAL) || '%'') ';        
      END IF;    
    ELSE
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_LGNOMBRES);
      END; 
    END IF;
  END IF;

  -- Validacion de las direcciones 
  IF UN_DIRECCIONINICIAL IS NOT NULL AND UN_DIRECCIONFINAL IS NOT NULL THEN
      IF SUBSTR(UPPER(UN_DIRECCIONINICIAL),1,3) NOT LIKE 'TOD' AND SUBSTR(UPPER(UN_DIRECCIONFINAL),1,3) NOT LIKE 'TOD' THEN 
        MI_CONDICION := MI_CONDICION ||  ' AND (   IP_USUARIOS_PREDIAL.DIRECCION LIKE ''%' || UPPER(UN_DIRECCIONINICIAL) || '%'' '||
                                              ' OR IP_USUARIOS_PREDIAL.DIRECCION LIKE ''%' || UPPER(UN_DIRECCIONFINAL) || '%'') ';        
      END IF;    
    ELSE
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_LGDIRECCION);
      END; 
    END IF;

    -- Si el parametro es nulo o diferente de PREDIAL_LISGRAL
    IF MI_PARFORMATO IS NULL OR MI_PARFORMATO NOT LIKE 'PREDIAL_LISGRAL' THEN
      IF UN_SOLOPREDIOS NOT IN (0) THEN
        MI_CONDICION := MI_CONDICION || 'AND IP_USUARIOS_PREDIAL.INDCAR NOT IN (0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.INDEXE NOT IN (0) ';
      END IF;
    ELSE
      IF UN_INDICADOR NOT IN (0) THEN 
        MI_CONDICION := MI_CONDICION || 'AND IP_USUARIOS_PREDIAL.INDBORRADO IN(0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN(0) '; 

      ELSIF UN_SOLOPREDIOS NOT IN (0) THEN
        MI_CONDICION := MI_CONDICION || 'AND IP_USUARIOS_PREDIAL.INDBORRADO IN(0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN(0) '||
                                        'AND (   IP_USUARIOS_PREDIAL.INDEXE NOT IN(0) '||
                                        '     OR IP_USUARIOS_PREDIAL.INDCAR NOT IN(0)) ';          
      ELSE 
        MI_CONDICION := MI_CONDICION || 'AND IP_USUARIOS_PREDIAL.INDBORRADO IN (0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN(0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.INDEXE IN(0) ' ||
                                        'AND IP_USUARIOS_PREDIAL.INDCAR IN(0) ';
      END IF;     
    END IF;

    -- Ordenamiento del reporte, se selecciona en el combo "Ordenar reporte por"
    MI_CONDICION := MI_CONDICION || 
                    CASE UN_ORDENADO   
                      WHEN 1 THEN ' ORDER BY IP_USUARIOS_PREDIAL.CODIGO '
                      WHEN 2 THEN ' ORDER BY IP_USUARIOS_PREDIAL.AVALUO_ANO '
                      WHEN 3 THEN ' ORDER BY IP_USUARIOS_PREDIAL.NOMBRE '
                      WHEN 4 THEN ' ORDER BY IP_USUARIOS_PREDIAL.DIRECCION '
                    END;  

  RETURN MI_CONDICION;
END FC_PREPARARLISTADOGRAL;

--21
FUNCTION FC_VALORDESCUENTOESPECIAL
/*
    NAME              : FC_VALORDESCUENTOESPECIAL --> en Access ValorDescuentoEspecial PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : retorna el valor de los descuentos especiales que se le realizan a un predio

    PARAMETERS        :

    MODIFICATIONS     : UN_COMPANIA     => Compañia de ingreso a la aplicación
                        UN_PREDIO       => Codigo del predio 
                        UN_NUMERO_ORDEN => Numero de Orden del predio (Propietario)

    @NAME:  obtenerValorDescuentoEspecial
    @METHOD:  GET
  */ 
(  
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_PREDIO       IN PCK_SUBTIPOS.TI_CODPREDIO
   ,UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN
) RETURN PCK_SUBTIPOS.TI_DOBLE
AS
    MI_VALORDESCESPECIAL PCK_SUBTIPOS.TI_DOBLE;
    MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
    MI_APLICADESCESP     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONCAFECDESCESP   PCK_SUBTIPOS.TI_PARAMETRO;
    MI_I                 PCK_SUBTIPOS.TI_ENTERO;
    MI_STRIN             PCK_SUBTIPOS.TI_CONDICION; 
BEGIN
    MI_APLICADESCESP:=NVL(PCK_SYSMAN_UTL.FC_PAR(
    	                                   UN_COMPANIA  => UN_COMPANIA
    	                                  ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                                  ,UN_NOMBRE    => 'MANEJA OPCION DESCUENTOS ESPECIALES'
    	                                  ,UN_FECHA_PAR => SYSDATE
    	                                  ), 'NO');
    MI_CONCAFECDESCESP:=NVL(PCK_SYSMAN_UTL.FC_PAR(
  	                                       UN_COMPANIA  => UN_COMPANIA
  	                                      ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
  	                                      ,UN_NOMBRE    => 'CONCEPTOS AFECTADOS EN DESCUENTO ESPECIAL'
  	                                      ,UN_FECHA_PAR => SYSDATE
  	                                      )
                           ,'2,4');
    IF MI_APLICADESCESP = 'SI' THEN
        FOR MI_I IN 2..20
        LOOP
          IF MI_I < 3 OR MI_I > 12 THEN
            IF INSTR(1, MI_CONCAFECDESCESP, MI_I) > 0 THEN
              MI_STRIN:= MI_STRIN||'DESESPC'||MI_I||'+ ';
            END IF;
          END IF;
        END LOOP;
        MI_STRIN:= SUBSTR(MI_STRIN, 1, LENGTH(MI_STRIN) - 2);


        MI_STRSQL:= 'SELECT SUM('||MI_STRIN||') TOTAL_DESCESP 
                       FROM IP_FACTURADOS 
                      WHERE COMPANIA     = '''||UN_COMPANIA||'''
                        AND CODIGO       = '''||UN_PREDIO||'''
                        AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||''' 
                        AND PAGADO        IN (0)
                        AND NOCOBRADO     IN (0)
                        AND INDPAGO_ACPAG IN (0)';

        BEGIN 
        	  EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALORDESCESPECIAL;  
        EXCEPTION WHEN NO_DATA_FOUND THEN
        	  MI_VALORDESCESPECIAL:=0;
        END;         
    ELSE
      MI_VALORDESCESPECIAL:= 0;
    END IF;
    RETURN MI_VALORDESCESPECIAL;
END FC_VALORDESCUENTOESPECIAL;

--22
FUNCTION FC_PORC_DESC_LOTES
/*
    NAME              : FC_PORC_DESC_LOTES --> en Access PORC_DESC_LOTES PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el valor del porcentaje de descuento que se le realiza a un predio.

    PARAMETERS        :

    MODIFICATIONS     : UN_COMPANIA     => Compañia de ingreso a la aplicación
                        UN_PREDIO       => Codigo del predio 
                        UN_FECHA_EV     => Fecha en la que se va a calcular el porcentaje

    @NAME:  obtenerValorPorcDctoEspecial
    @METHOD:  GET
  */ 
(
	 UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA 
	,UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO
	,UN_FECHA_EV IN DATE
) RETURN PCK_SUBTIPOS.TI_DOBLE
AS
    MI_INCRAVALUO      PCK_SUBTIPOS.TI_DOBLE;
    MI_PORC            PCK_SUBTIPOS.TI_DOBLE;
    MI_PORC_DESC_LOTES PCK_SUBTIPOS.TI_DOBLE;
BEGIN 
    MI_INCRAVALUO:= PCK_PREDIAL_COM3.FC_INCREMENTOAVALUO(
                                     UN_COMPANIA  => UN_COMPANIA
                                    ,UN_CODPREDIO => UN_PREDIO);
    BEGIN 
    	  SELECT PORC_DSCTO * 100 PORC 
          INTO MI_PORC
          FROM IP_DESCUENTOS_ESPECIALES 
         WHERE COMPANIA       =  UN_COMPANIA
           AND ANO            =  EXTRACT (YEAR  FROM UN_FECHA_EV) 
           AND MES            =  EXTRACT (MONTH FROM UN_FECHA_EV) 
           AND CODIGO_INICIAL <= UN_PREDIO     
           AND CODIGO_FINAL   >= UN_PREDIO 
           AND INCREM_INICIAL <= MI_INCRAVALUO 
           AND INCREM_FINAL   >= MI_INCRAVALUO 
         ORDER BY MES, IND_PRIORIDAD;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        BEGIN 
        	SELECT PORCENTAJE*100 PORC 
        	  INTO MI_PORC
        	  FROM IP_DESCUENTOS_ANO 
        	 WHERE COMPANIA = UN_COMPANIA
        	   AND ANO = EXTRACT (YEAR FROM SYSDATE) 
        	   AND MES = EXTRACT (MONTH FROM UN_FECHA_EV) 
             ORDER BY MES;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_PORC:=0;
        END;
    END;
    MI_PORC_DESC_LOTES:= NVL(MI_PORC, 0);
    RETURN MI_PORC_DESC_LOTES;
END FC_PORC_DESC_LOTES;

--23
FUNCTION FC_MJSDSCTOESP
/*
    NAME              : FC_MJSDSCTOESP --> en Access MJSDSCTOESP PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el valor del titulo del rango de descuento que se le realiza a un predio.

    PARAMETERS        :

    MODIFICATIONS     : UN_COMPANIA     => Compañia de ingreso a la aplicación
                        UN_PREDIO       => Codigo del predio 
                        UN_FECHA_EV     => Fecha en la que se va a calcular el porcentaje

    @NAME:  obtenerMsjeDesctoEspecial
    @METHOD:  GET
  */ 
( 
	 UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA 
	,UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO
	,UN_FECHA_EV IN DATE
) RETURN IP_DESCUENTOS_ESPECIALES.TITULO_RANGO%TYPE
AS
    MI_INCRAVALUO PCK_SUBTIPOS.TI_DOBLE;
    MI_MJSDSCTOESP IP_DESCUENTOS_ESPECIALES.TITULO_RANGO%TYPE;
BEGIN 
    MI_INCRAVALUO:= PCK_PREDIAL_COM3.FC_INCREMENTOAVALUO(
                                     UN_COMPANIA  => UN_COMPANIA
                                    ,UN_CODPREDIO => UN_PREDIO);
    BEGIN  
        SELECT TITULO_RANGO 
          INTO MI_MJSDSCTOESP
          FROM IP_DESCUENTOS_ESPECIALES 
         WHERE COMPANIA       =  UN_COMPANIA
           AND ANO            =  EXTRACT(YEAR FROM UN_FECHA_EV)  
           AND MES            =  EXTRACT(MONTH FROM UN_FECHA_EV)
           AND CODIGO_INICIAL <= UN_PREDIO 
           AND CODIGO_FINAL   >= UN_PREDIO 
           AND INCREM_INICIAL <= MI_INCRAVALUO 
           AND INCREM_FINAL   >= MI_INCRAVALUO 
         ORDER BY  MES, IND_PRIORIDAD;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_MJSDSCTOESP:= '';
    END; 
    RETURN MI_MJSDSCTOESP; 
END FC_MJSDSCTOESP;

--24
FUNCTION FC_GENERAPLANOFACTURACION
/*
    NAME              : FC_GENERAPLANOFACTURACION --> en Access GENERAPLANOFACTURACION PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Genera el archivo plano de la facturacion en lotes. Aplica para el Nit 8912800003

    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_CODIGOINICIAL => Codigo de predio Inicial para el filtro del plano
                         UN_CODIGOFINAL   => Codigo de predio final para el filtro del plano
                         UN_NOMBREINICIAL => Nombre del propietario del predio inicial para el filtro del plano
                         UN_NOMBREFINAL   => Nombre del propietario del predio final para el filtro del plano
                         UN_ANIOINICIAL   => Anio inicial para el filtro del plano
                         UN_ANIOFINAL     => Anio final para el filtro del plano
                         UN_HASTAANIO     => Anio hasta el cual se genera el plano
                         UN_NITINICIAL    => Nit inicial para el filtro del plano
                         UN_NITFINAL      => Nit final para el filtro del plano
                         UN_NUMERO_ORDEN  => Numero de Orden del predio (Propietario)
                         UN_ORDEN         => Orden en el cual se generara el plano
                         UN_VALORINFERIOR => Valor inferior del predio para el filtro del plano
                         UN_VALORSUPERIOR => Valor superior del predio para el filtro del plano

    @NAME:  generaPlanoFacturacion
    @METHOD:  GET
  */ 
(
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODPREDIO
    ,UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODPREDIO
    ,UN_NOMBREINICIAL IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    ,UN_NOMBREFINAL   IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    ,UN_ANIOINICIAL   IN PCK_SUBTIPOS.TI_ANIO
    ,UN_ANIOFINAL     IN PCK_SUBTIPOS.TI_ANIO
    ,UN_HASTAANIO     IN PCK_SUBTIPOS.TI_ANIO
    ,UN_NITINICIAL    IN IP_USUARIOS_PREDIAL.NIT%TYPE
    ,UN_NITFINAL      IN IP_USUARIOS_PREDIAL.NIT%TYPE
    ,UN_NUMERO_ORDEN  IN PCK_SUBTIPOS.TI_NUMORDEN 
    ,UN_ORDEN         IN VARCHAR2
    ,UN_VALORINFERIOR IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_VALORSUPERIOR IN PCK_SUBTIPOS.TI_DOBLE
)
 RETURN CLOB 
AS 
	  MI_FECHA           DATE;
    MI_FECHA1          DATE;
    MI_FECHA2          DATE;
    MI_TOTAL           NUMBER:=0;
    MI_TOTAL1          NUMBER:=0;
    MI_TOTAL2          NUMBER:=0;
    MI_DESC            NUMBER:=0;
    MI_DESC1           NUMBER:=0;
    MI_DESC2           NUMBER:=0;
    MI_I               NUMBER:=0;
    MI_STRSQL          VARCHAR2(1000 CHAR):='';
    MI_RS              SYS_REFCURSOR;
    MI_ORDENINF        PCK_SUBTIPOS.TI_CONDICION;
    MI_CODIGO          IP_USUARIOS_PREDIAL.CODIGO%TYPE;
    MI_NOMBRE          IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
    MI_NIT             IP_USUARIOS_PREDIAL.NIT%TYPE; 
    MI_DIRECCION       IP_USUARIOS_PREDIAL.DIRECCION%TYPE;
    MI_DIR_CORRESP     IP_USUARIOS_PREDIAL.DIRECCION_CORRESPONDENCIA%TYPE;
    MI_AREA_HA         IP_USUARIOS_PREDIAL.AREA_HA%TYPE;
    MI_AREA_M2         IP_USUARIOS_PREDIAL.AREA_M2%TYPE;
    MI_AREA_CONSTRUIDA IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA%TYPE;
    MI_PAGO_ANO        IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE;
    MI_PAG_FEC         IP_USUARIOS_PREDIAL.PAG_FEC%TYPE;
    MI_PAG_VAL         IP_USUARIOS_PREDIAL.PAG_VAL%TYPE;
    MI_NUM_COM         IP_USUARIOS_PREDIAL.NUM_COM%TYPE;
    MI_NUMERO_FACTURA  IP_USUARIOS_PREDIAL.NUMERO_FACTURA%TYPE;
    MI_TRPCOD          IP_USUARIOS_PREDIAL.TRPCOD%TYPE;
    MI_PREFEC          IP_RECIBOS_DE_PAGO.PREFEC%TYPE;
    MI_DESC_INTERES    IP_RECIBOS_DE_PAGO.DESC_INTERES%TYPE;
    MI_PREFECLIM       IP_RECIBOS_DE_PAGO.PREFECLIM%TYPE;
    MI_PREVAL          IP_RECIBOS_DE_PAGO.PREVAL%TYPE;
    MI_C13             IP_RECIBOS_DE_PAGO.C13%TYPE;
    MI_TARDESC         VARCHAR2(255 CHAR);
    MI_TAR             VARCHAR2(20 CHAR);  
    MI_SUMADEC1        PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC2        PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC3        PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC4        PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC14       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC15       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC16       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC17       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC18       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC19       PCK_SUBTIPOS.TI_DOBLE;
    MI_OTROS           PCK_SUBTIPOS.TI_DOBLE;
    MI_SC18            PCK_SUBTIPOS.TI_DOBLE;
    MI_SC19            PCK_SUBTIPOS.TI_DOBLE;
    MI_SC20            PCK_SUBTIPOS.TI_DOBLE;
    MI_CADENA          PCK_SUBTIPOS.TI_STRSQL;
    MI_QUERY           PCK_SUBTIPOS.TI_STRSQL;
    MI_RTACADENA       CLOB;
BEGIN
    MI_ORDENINF:= 'ORDER BY '||CASE UN_ORDEN WHEN 1 THEN 'CODIGO'
                                             WHEN 2 THEN 'NOMBRE||CODIGO'
                                             ELSE 'DIRECCION||CODIGO'
                               END;
    MI_QUERY:='SELECT DISTINCT 
                        CODIGO
                      , NOMBRE
                      , NIT
                      , DIRECCION
                      , DIRECCION_CORRESPONDENCIA
                      , AREA_HA
                      , AREA_M2
                      , AREA_CONSTRUIDA
                      , PAGO_ANO
                      , PAG_FEC
                      , PAG_VAL
                      , NUM_COM
                      , NUMERO_FACTURA
                      , TRPCOD 
                 FROM IP_USUARIOS_PREDIAL  
                WHERE COMPANIA     = '''||UN_COMPANIA||'''
                  AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||''' 
                  AND CODIGO       BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                  AND NOMBRE       BETWEEN '''||UN_NOMBREINICIAL||''' AND '''||UN_NOMBREFINAL||'''  
                  AND NIT          BETWEEN '''||UN_NITINICIAL||'''    AND '''||UN_NITFINAL||''' 
                  AND PAGO_ANO     BETWEEN '||UN_ANIOINICIAL||'   AND '||UN_ANIOFINAL||'
                  AND TOTAL        BETWEEN '||UN_VALORINFERIOR||' AND '||UN_VALORSUPERIOR||'
                  AND INDBORRADO       IN (0) 
                  AND CODIGO_NO_ACTIVO IN (0) 
                  AND PAGO_ANO         < '||UN_HASTAANIO||' 
                  AND PAGO_ACUERDO     IN (0) 
                  '||MI_ORDENINF;
    OPEN MI_RS FOR MI_QUERY;
    LOOP
        FETCH MI_RS INTO  MI_CODIGO
                         ,MI_NOMBRE
                         ,MI_NIT
                         ,MI_DIRECCION
                         ,MI_DIR_CORRESP
                         ,MI_AREA_HA
                         ,MI_AREA_M2
                         ,MI_AREA_CONSTRUIDA
                         ,MI_PAGO_ANO
                         ,MI_PAG_FEC
                         ,MI_PAG_VAL
                         ,MI_NUM_COM
                         ,MI_NUMERO_FACTURA
                         ,MI_TRPCOD
                         ;
        EXIT WHEN MI_RS%NOTFOUND;     
        --'Buscar descripcion de la tarifa actualmente asignada al predio para enviar los datos al plano
        BEGIN 
            SELECT (TRPCOD||'-'||TRPDES) TARIFA 
              INTO MI_TARDESC
              FROM IP_TARIFAS 
             WHERE COMPANIA = UN_COMPANIA
               AND TRPCOD   = MI_TRPCOD
               AND TRPANO   = EXTRACT(YEAR FROM SYSDATE)
             ;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TARDESC:='-'; 
        END;
        --RECORRE FECHAS POR PREDIO
        FOR MI_I IN 1..12 
        LOOP
            MI_STRSQL:= ' SELECT  FECHA'||MI_I||' FECHA
                                 ,TOTAL'||MI_I||' TOTAL
                                 ,DESC'||MI_I||' DESCU 
                                 ,PREFEC
                                 ,DESC_INTERES
                                 ,PREFECLIM
                                 ,PREVAL
                                 ,C13
                            FROM IP_RECIBOS_DE_PAGO 
                           WHERE COMPANIA = '''||UN_COMPANIA||'''
                             AND DOCNUM   = '''||MI_NUMERO_FACTURA||'''  
                             AND PRECOD   = '''||MI_CODIGO||'''';
            BEGIN 
                EXECUTE IMMEDIATE MI_STRSQL 
                             INTO  MI_FECHA
                                  ,MI_TOTAL
                                  ,MI_DESC
                                  ,MI_PREFEC
                                  ,MI_DESC_INTERES
                                  ,MI_PREFECLIM
                                  ,MI_PREVAL
                                  ,MI_C13;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_FECHA:=NULL;
                MI_TOTAL:=NULL;
                MI_DESC:=NULL;
            END;

            IF MI_FECHA1 IS NULL 
               AND MI_FECHA <> TO_DATE('01/01/1900','DD/MM/YYYY') THEN
                MI_FECHA1:= MI_FECHA;
                MI_TOTAL1:= MI_TOTAL;
                MI_DESC1:= MI_DESC;
            ELSIF MI_FECHA1 IS NOT NULL
                  AND MI_FECHA <> TO_DATE('01/01/1900','DD/MM/YYYY') 
                  AND MI_TOTAL1 = MI_TOTAL THEN
                MI_FECHA1:= MI_FECHA;
                MI_TOTAL1:= MI_TOTAL;
                MI_DESC1:= MI_DESC;
            END IF;
            IF MI_FECHA2 IS NULL 
               AND MI_FECHA <> TO_DATE('01/01/1900','DD/MM/YYYY') 
               AND MI_FECHA1 <> MI_FECHA THEN
                MI_FECHA2:= MI_FECHA;
                MI_TOTAL2:= MI_TOTAL;
                MI_DESC2:= MI_DESC;
            ELSIF MI_FECHA2 IS NOT NULL 
                  AND MI_FECHA <> TO_DATE('01/01/1900','DD/MM/YYYY') 
                  AND MI_TOTAL2 = MI_TOTAL THEN
                MI_FECHA2:= MI_FECHA;
                MI_TOTAL2:= MI_TOTAL;
                MI_DESC2:= MI_DESC;
            END IF;
        END LOOP;
        IF MI_FECHA1 IS NOT NULL 
           AND MI_FECHA2 IS NOT NULL THEN
           MI_CADENA:= '1|'||LPAD(MI_NUMERO_FACTURA, 10, ' ')||'|'
                       ||LPAD(TO_CHAR(MI_PREFEC, 'DD/MM/YYYY'), 11, ' ')||'|'
                       ||LPAD(TO_CHAR(MI_FECHA1, 'DD/MM/YYYY'), 11, ' ')||'|'
                       ||LPAD(MI_TOTAL1, 13, ' ')||'|'
                       ||LPAD(MI_DESC1, 13, ' ')||'|'
                       ||LPAD(TO_CHAR(MI_FECHA2, 'DD/MM/YYYY'), 11, ' ')||'|'
                       ||LPAD(MI_TOTAL2, 13, ' ')||'|'
                       ||LPAD(MI_DESC2, 13, ' ')||'|'
                       ||LPAD(MI_DESC_INTERES, 13, ' ')
                       ||LPAD(' ', 64, ' ');
        ELSE
            MI_CADENA:= '1|'||LPAD(MI_NUMERO_FACTURA, 10, ' ')||'|'
                        ||LPAD(TO_CHAR(MI_PREFEC, 'DD/MM/YYYY'), 11, ' ')||'|'
                        ||LPAD(TO_CHAR(MI_PREFECLIM, 'DD/MM/YYYY'), 11, ' ')||'|'
                        ||LPAD(MI_PREVAL, 13, ' ')||'|'
                        ||LPAD(MI_C13, 13, ' ')||'|'
                        ||LPAD(' ', 11, ' ')||'|'
                        ||LPAD(MI_TOTAL2, 13, ' ')||'|'
                        ||LPAD(MI_DESC2, 13, ' ')||'|'
                        ||LPAD(MI_DESC_INTERES, 13, ' ')
                        ||LPAD(' ', 64, ' ');
        END IF;
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

        MI_CADENA:= '2|'
                    ||RPAD(MI_CODIGO, 15, ' ')||'|'
                    ||LPAD(TO_CHAR(MI_NIT, '999,999,999,999,999'), 14, ' ')||'|'
                    ||RPAD(MI_NOMBRE, 40, ' ')
                    ||LPAD(' ', 64, ' ');

        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

        MI_CADENA:= '3|'
                    ||RPAD(MI_DIRECCION, 35, ' ')||'|'
                    ||RPAD(NVL(MI_DIR_CORRESP, '-'), 35, ' ')||'|'
                    ||LPAD(TO_CHAR(MI_AREA_HA, '999,999,999,999,999'), 5, ' ')||'|'
                    ||LPAD(TO_CHAR(MI_AREA_M2, '999,999,999,999,999'), 7, ' ')||'|'
                    ||LPAD(TO_CHAR(MI_AREA_CONSTRUIDA, '999,999,999,999,999'), 6, ' ')
                    ||LPAD(' ', 43, ' ');
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

        MI_CADENA:= '4|'
                    ||LPAD(NVL(MI_PAGO_ANO, 0), 4, ' ')||'|'
                    ||LPAD(NVL(TO_CHAR(MI_PAG_FEC, 'DD.MM.YYYY'), '-'), 10, ' ')||'|'
                    ||LPAD(MI_PAG_VAL, 13, ' ')||'|'
                    ||LPAD(MI_NUM_COM, 10, ' ')||'|'
                    ||RPAD(MI_TARDESC, 35, ' ')||'|'
                    ||LPAD(MI_PAGO_ANO + 1, 4, ' ')||'|'
                    ||LPAD(EXTRACT(YEAR FROM SYSDATE), 4, ' ')
                    ||LPAD(' ', 56, ' ');
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);
        --'PAO 10022011 se modifica por que los conceptos 18 19 y 20 van en otro marcador
        <<RECORREFACTURAS>>
        FOR MI_RSFAC IN( 
            SELECT   PREANO
                   , TRPPOR
                   , AVALUO
                   , C1
                   , C3
                   , C14
                   , C16
                   , C2
                   , C4
                   , C15
                   , C17
                   , C18
                   , C19
                   , C20
                   , (C18+C19+C20) OTROS
                   , (C1+C2+C3+C4+C14+C15+C16+C17+C18+C19) TOTAL 
              FROM IP_FACTURADOS 
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_CODIGO
               AND PAGADO   IN (0) 
             ORDER BY PREANO)
        LOOP
            MI_TAR:= TRIM(TO_CHAR(NVL(MI_RSFAC.TRPPOR, 0) * 1000));
            MI_CADENA:= '5|'
                        ||LPAD(NVL(MI_RSFAC.PREANO, 0), 4, ' ')||'|'
                        ||LPAD(NVL(MI_TAR, 0), 4, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.AVALUO, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C1, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C3, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C14 + MI_RSFAC.C18, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C16, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C2, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C4, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C15 + MI_RSFAC.C19, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.C17, 0), 13, ' ')||'|'
                        ||LPAD(NVL(MI_RSFAC.TOTAL, 0), 13, ' ')||'|';
            MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);
        END LOOP RECORREFACTURAS;

        MI_CADENA:= '6|'||RPAD(' ', 134, ' ');
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

        BEGIN   
            SELECT   SUM(F.C1)  SUMADEC1
                   , SUM(F.C2)  SUMADEC2
                   , SUM(F.C3)  SUMADEC3
                   , SUM(F.C4)  SUMADEC4
                   , SUM(F.C14) SUMADEC14
                   , SUM(F.C15) SUMADEC15
                   , SUM(F.C16) SUMADEC16
                   , SUM(F.C17) SUMADEC17
                   , SUM(F.C18) SUMADEC18
                   , SUM(F.C19) SUMADEC19
                   , SUM(C18+C19+C20) OTROS
                   , SUM(C1+C2+C3+C4+C14+C15+C16+C17+C18+C19) TOTAL 
              INTO  MI_SUMADEC1
                   ,MI_SUMADEC2
                   ,MI_SUMADEC3
                   ,MI_SUMADEC4
                   ,MI_SUMADEC14
                   ,MI_SUMADEC15
                   ,MI_SUMADEC16
                   ,MI_SUMADEC17
                   ,MI_SUMADEC18
                   ,MI_SUMADEC19
                   ,MI_OTROS
                   ,MI_TOTAL
              FROM IP_FACTURADOS F 
             WHERE F.COMPANIA = UN_COMPANIA 
               AND F.CODIGO   = MI_CODIGO 
               AND F.PAGADO   IN (0);
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_SUMADEC1:=NULL;
            MI_SUMADEC2:=NULL;
            MI_SUMADEC3:=NULL;
            MI_SUMADEC4:=NULL;
            MI_SUMADEC14:=NULL;
            MI_SUMADEC15:=NULL;
            MI_SUMADEC16:=NULL;
            MI_SUMADEC17:=NULL;
            MI_SUMADEC18:=NULL;
            MI_SUMADEC19:=NULL;
            MI_OTROS:=NULL;
            MI_TOTAL:=NULL;
        END;    
        MI_CADENA:= '7|'
                    ||RPAD(MI_CODIGO, 15, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC1, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC3, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC14 + MI_SUMADEC18, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC16, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC2, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC4, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC15 + MI_SUMADEC19, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SUMADEC17, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_TOTAL, 0), 13, ' ')||'|'
                    ||LPAD(' ', 7);
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);
        -- 'PAO 10/02/2011 Se agrega este marcador para mostrar solo el saldo en contra (concepto 20) y los conceptos 18 y 19.
        BEGIN 
        	SELECT   SUM(C18) SC18
                   , SUM(C19) SC19 
                   , SUM(C20) SC20 
              INTO   MI_SC18
                   , MI_SC19 
                   , MI_SC20      
              FROM IP_FACTURADOS 
             WHERE COMPANIA = UN_COMPANIA 
               AND CODIGO   = MI_CODIGO 
               AND PAGADO   IN (0);
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_SC18:=NULL;
            MI_SC19:=NULL;
            MI_SC20:=NULL;
        END;
        MI_CADENA:= '8|'
                    ||LPAD(NVL(MI_SC19, 0), 13, ' ')||'|'
                    ||LPAD(NVL(MI_SC18, 0), 13, ' ')||'|'
                    ||LPAD(NVL(FC_VALORDESCUENTOESPECIAL( 
                               UN_COMPANIA     => UN_COMPANIA
                              ,UN_PREDIO       => MI_CODIGO
                              ,UN_NUMERO_ORDEN => UN_NUMERO_ORDEN), 0), 13, ' ')||'|';
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13); 

        MI_CADENA:= '9|'||LPAD(NVL(MI_SC20, 0), 13, ' ')||'|';
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

        IF MI_FECHA1 IS NOT NULL 
           AND MI_FECHA2 IS NOT NULL THEN
            MI_CADENA:= '10|'
                        ||LPAD(FC_PORC_DESC_LOTES(
                               UN_COMPANIA => UN_COMPANIA 
                              ,UN_PREDIO   => MI_CODIGO
                              ,UN_FECHA_EV => MI_FECHA1), 2, ' ')||'|'
                        ||LPAD(FC_PORC_DESC_LOTES(
                                UN_COMPANIA => UN_COMPANIA 
                               ,UN_PREDIO   => MI_CODIGO
                               ,UN_FECHA_EV => MI_FECHA2  ), 2, ' ')||'|'
                        ||RPAD(FC_MJSDSCTOESP( 
                                UN_COMPANIA => UN_COMPANIA 
                               ,UN_PREDIO   => MI_CODIGO
                               ,UN_FECHA_EV => MI_PREFECLIM), 250, ' ');
        ELSE 
            MI_CADENA:= '10|'
                        ||LPAD(FC_PORC_DESC_LOTES(
                                UN_COMPANIA => UN_COMPANIA 
                               ,UN_PREDIO   => MI_CODIGO
                               ,UN_FECHA_EV => MI_PREFECLIM  ), 2, ' ')||'|';
        END IF; 
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

    END LOOP;
    CLOSE MI_RS;

    RETURN MI_RTACADENA;

END FC_GENERAPLANOFACTURACION;

--25
FUNCTION FC_GENERAPLANOFACTURACIONASO98
/*
    NAME              : FC_GENERAPLANOFACTURACIONASO98 --> en Access GENERAPLANOFACTURACIONASO98 PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Genera el plano de Facturación de asobancaria 98.    
    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_CODIGOINICIAL => Codigo de predio Inicial para el filtro del plano
                         UN_CODIGOFINAL   => Codigo de predio final para el filtro del plano


    @NAME:  generarPlanoFacturacionAsoBanNoventaOcho
    @METHOD:  GET
  */
(
	 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA  
	,UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODPREDIO
	,UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODPREDIO
) RETURN CLOB 
  --'JAAB 26MAR10
AS
    MI_RTACADENA      CLOB;
    MI_CODIGO_EAN     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ZONA           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CADENA         PCK_SUBTIPOS.TI_STRSQL;
    MI_PERIODOS       PCK_SUBTIPOS.TI_CONDICION;
    MI_TOTALREGISTROS PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TOTALSERVICIO  PCK_SUBTIPOS.TI_DOBLE;
BEGIN 
    --Ruta = "C:\SYSMAN\Plano_Facturacion.txt"
    MI_CODIGO_EAN:=PCK_SYSMAN_UTL.FC_PAR(
    	                          UN_COMPANIA  => UN_COMPANIA
    	                         ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                         ,UN_FECHA_PAR => SYSDATE
    	                         ,UN_NOMBRE    => 'CODIGO EAN');
    MI_ZONA:=PCK_SYSMAN_UTL.FC_PAR(
    	                          UN_COMPANIA  => UN_COMPANIA
    	                         ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                         ,UN_FECHA_PAR => SYSDATE
    	                         ,UN_NOMBRE    => 'ZONA CIUDAD');
    IF NVL(MI_CODIGO_EAN,'-') = '-' THEN
        BEGIN 
        	  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_VALIDARCFGPARAMET
                       ,UN_TABLAERROR => 'PARAMETRO');
        END;
    END IF;

    MI_CADENA:= '01'
                ||MI_CODIGO_EAN
                ||TO_CHAR(SYSDATE, 'YYYYMMDD')
                ||LPAD('0',8,'0')
                ||TO_CHAR(SYSDATE, 'YYYYMMDD')
                ||MI_ZONA
                ||LPAD(' ',42,' '); --'42 ESPACIOS
    MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

    <<RECORRERECIBOS>>
    FOR MI_RS IN (
    	 SELECT DISTINCT   DOCNUM
    	                 , PRECOD
    	                 , PREVAL
    	                 , PREANOI
    	                 , PREANOF 
    	   FROM IP_RECIBOS_DE_PAGO 
    	  WHERE COMPANIA      = UN_COMPANIA 
    	    AND PRECOD        BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
    	    AND TRUNC(PREFEC) = TRUNC(SYSDATE) 
    	    AND PREANO        = EXTRACT(YEAR FROM SYSDATE))
    LOOP
        MI_CADENA:= '02'
                    ||LPAD( MI_RS.DOCNUM||MI_RS.PRECOD
                    	  , 25
                    	  , '0') 
                    ||PCK_SYSMAN_UTL.FC_STRZERO(MI_RS.DOCNUM, 12);
        MI_PERIODOS:= MI_RS.PREANOF - (MI_RS.PREANOI - 1);
        MI_CADENA:= MI_CADENA
                    ||PCK_SYSMAN_UTL.FC_STRZERO(MI_PERIODOS, 2)
                    ||PCK_SYSMAN_UTL.FC_STRZERO(MI_RS.PREVAL, 13)
                    ||MI_CODIGO_EAN
                    ||LPAD('0',14,'0')
                    ||LPAD(' ',4,' ');
        MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);
        MI_TOTALREGISTROS:= MI_TOTALREGISTROS + 1;
        MI_TOTALSERVICIO:= MI_TOTALSERVICIO + MI_RS.PREVAL;
    END LOOP RECORRERECIBOS;

    MI_CADENA:= '03'
                ||PCK_SYSMAN_UTL.FC_STRZERO(MI_TOTALREGISTROS, 9)
                ||PCK_SYSMAN_UTL.FC_STRZERO(MI_TOTALSERVICIO, 18)
                ||LPAD('0',18,'0')
                ||LPAD('0',37,'0');

    MI_RTACADENA:=MI_RTACADENA||MI_CADENA||CHR(10)||CHR(13);

    RETURN MI_RTACADENA;
END FC_GENERAPLANOFACTURACIONASO98;

--26 
FUNCTION FC_GENERAPLANOPUNTOS
/*
    NAME              : FC_GENERAPLANOPUNTOS --> en Access GENERAPLANOPUNTOS PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Genera el plano de Puntos un .bat 
    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_NUMERO_ORDEN  => Numero de Orden del propietario del Predio
                         UN_CODIGOINICIAL => Codigo de predio Inicial para el filtro del plano
                         UN_CODIGOFINAL   => Codigo de predio final para el filtro del plano


    @NAME:  generarPlanoPuntos
    @METHOD:  GET
  */
(
      UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    , UN_NUMERO_ORDEN  IN PCK_SUBTIPOS.TI_NUMORDEN  
	  , UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODPREDIO
	  , UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODPREDIO
)RETURN CLOB
AS
     MI_REC_PREFECLIM IP_RECIBOS_DE_PAGO.PREFECLIM%TYPE;
     MI_REC_C1        IP_RECIBOS_DE_PAGO.C1%TYPE;
     MI_REC_C2        IP_RECIBOS_DE_PAGO.C2%TYPE;
     MI_REC_C3        IP_RECIBOS_DE_PAGO.C3%TYPE;
     MI_REC_C4        IP_RECIBOS_DE_PAGO.C4%TYPE;
     MI_REC_C5        IP_RECIBOS_DE_PAGO.C5%TYPE;
     MI_REC_C6        IP_RECIBOS_DE_PAGO.C6%TYPE;
     MI_REC_C7        IP_RECIBOS_DE_PAGO.C7%TYPE;
     MI_REC_C8        IP_RECIBOS_DE_PAGO.C8%TYPE;
     MI_REC_C9        IP_RECIBOS_DE_PAGO.C9%TYPE;
     MI_REC_C10       IP_RECIBOS_DE_PAGO.C10%TYPE;
     MI_REC_C11       IP_RECIBOS_DE_PAGO.C11%TYPE;
     MI_REC_C12       IP_RECIBOS_DE_PAGO.C12%TYPE;
     MI_REC_C13       IP_RECIBOS_DE_PAGO.C13%TYPE;
     MI_REC_C14       IP_RECIBOS_DE_PAGO.C14%TYPE;
     MI_REC_C15       IP_RECIBOS_DE_PAGO.C15%TYPE;
     MI_REC_C16       IP_RECIBOS_DE_PAGO.C16%TYPE;
     MI_REC_C17       IP_RECIBOS_DE_PAGO.C17%TYPE;
     MI_REC_C18       IP_RECIBOS_DE_PAGO.C18%TYPE;
     MI_REC_C19       IP_RECIBOS_DE_PAGO.C19%TYPE;
     MI_REC_C20       IP_RECIBOS_DE_PAGO.C20%TYPE;
     MI_REC_PREVAL    IP_RECIBOS_DE_PAGO.PREVAL%TYPE;
     MI_REC_PREFEC    IP_RECIBOS_DE_PAGO.PREFEC%TYPE;
     MI_REC_TOTAL2    IP_RECIBOS_DE_PAGO.TOTAL2%TYPE;
     MI_REC_FECHA2    IP_RECIBOS_DE_PAGO.FECHA2%TYPE;
     MI_REC_TOTAL3    IP_RECIBOS_DE_PAGO.TOTAL3%TYPE;
     MI_REC_DOCNUM    IP_RECIBOS_DE_PAGO.DOCNUM%TYPE;
     MI_REC_FECHA3    IP_RECIBOS_DE_PAGO.FECHA3%TYPE;
     MI_CADENA        PCK_SUBTIPOS.TI_CONDICION;
     MI_TAR           PCK_SUBTIPOS.TI_DOBLE;
     MI_DBLINTERES    PCK_SUBTIPOS.TI_DOBLE;
     MI_DBLTOTAL      PCK_SUBTIPOS.TI_DOBLE;
     MI_OBS_FACTURA   PARAMETRO.VALOR%TYPE;
     MI_RTACADENA     CLOB;
BEGIN
    MI_OBS_FACTURA:=PCK_SYSMAN_UTL.FC_PAR(
                                   UN_COMPANIA  => UN_COMPANIA 
                                  ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
                                  ,UN_NOMBRE    => 'OBSERVACIONES FACTURACION'
                                  ,UN_FECHA_PAR => SYSDATE);
    FOR MI_RS IN(
        SELECT   U.CODIGO
               , U.NOMBRE
               , U.NIT
               , U.DIRECCION
               , U.DIRECCION_CORRESPONDENCIA
               , U.AREA_HA
               , U.AREA_M2
               , U.AREA_CONSTRUIDA
               , U.PAGO_ANO
               , U.PAG_FEC
               , U.PAG_VAL
               , U.NUM_COM
               , U.NUMERO_FACTURA
               , U.MATRICULA_INMOBILIARIA
               , U.DESTINO_ECONOMICO
               , T.TRPCOD||' - '||TRPDES TARIFA 
          FROM IP_USUARIOS_PREDIAL U 
              LEFT JOIN IP_TARIFAS T 
                  ON  U.COMPANIA = T.COMPANIA
                  AND U.PAGO_ANO = T.TRPANO 
                  AND U.TRPCOD   = T.TRPCOD 
            WHERE U.COMPANIA     =  UN_COMPANIA 
              AND U.PAGO_ANO     <> EXTRACT(YEAR FROM SYSDATE) 
              AND U.NUMERO_ORDEN =  UN_NUMERO_ORDEN)
    LOOP    
        MI_CADENA:= MI_CADENA
                    ||'@p01 '||MI_RS.NUMERO_FACTURA||CHR(13)
                    ||'@p02 '||MI_RS.CODIGO||CHR(13)
                    ||'@p03 '||FC_NOMBRECOPROPIETARIO(
                                         UN_COMPANIA     => UN_COMPANIA
                                        ,UN_CODIGOPRED   => MI_RS.CODIGO
                                        ,UN_NUMERO_ORDEN => '001')||CHR(13)
                    ||'@p04 '||MI_RS.MATRICULA_INMOBILIARIA||CHR(13)
                    ||'@p05 '||MI_RS.NOMBRE||CHR(13)
                    ||'@p06 '||MI_RS.DIRECCION_CORRESPONDENCIA||CHR(13) 
                    ||'@p07 '||MI_RS.NIT||CHR(13)
                    ||'@p08 '||FC_ANIOS_PAGOS(
                                   UN_COMPANIA     => UN_COMPANIA
                                  ,UN_CODIGO       => MI_RS.CODIGO
                                  ,UN_NUMERO_ORDEN => UN_NUMERO_ORDEN);
        BEGIN 
        	SELECT 
        	         PREFECLIM
                   , C1
                   , C2
                   , C3
                   , C4
                   , C5
                   , C6
                   , C7
                   , C8
                   , C9
                   , C10
                   , C11
                   , C12
                   , C13
                   , C14
                   , C15
                   , C16
                   , C17
                   , C18
                   , C19
                   , C20
                   , PREVAL
                   , PREFEC
                   , TOTAL2
                   , FECHA2
                   , TOTAL3
                   , DOCNUM
                   , FECHA3
              INTO   MI_REC_PREFECLIM
                   , MI_REC_C1
                   , MI_REC_C2
                   , MI_REC_C3
                   , MI_REC_C4
                   , MI_REC_C5
                   , MI_REC_C6
                   , MI_REC_C7
                   , MI_REC_C8
                   , MI_REC_C9
                   , MI_REC_C10
                   , MI_REC_C11
                   , MI_REC_C12
                   , MI_REC_C13
                   , MI_REC_C14
                   , MI_REC_C15
                   , MI_REC_C16
                   , MI_REC_C17
                   , MI_REC_C18
                   , MI_REC_C19
                   , MI_REC_C20
                   , MI_REC_PREVAL
                   , MI_REC_PREFEC
                   , MI_REC_TOTAL2
                   , MI_REC_FECHA2
                   , MI_REC_TOTAL3
                   , MI_REC_DOCNUM
                   , MI_REC_FECHA3 
              FROM IP_RECIBOS_DE_PAGO 
             WHERE COMPANIA = UN_COMPANIA
               AND DOCNUM   = MI_RS.NUMERO_FACTURA 
               AND PRECOD   = MI_RS.CODIGO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_REC_DOCNUM:=NULL;
        END;
        IF MI_REC_DOCNUM IS NOT NULL THEN
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@p09 '||TO_CHAR(MI_REC_PREFECLIM, 'DD/MM/YYYY')||CHR(13)
                        ||'@p10 '||MI_RS.NUM_COM||CHR(13)
                        ||'@p11 '||MI_RS.AREA_HA||CHR(13)
                        ||'@p12 '||MI_RS.AREA_M2||CHR(13)
                        ||'@p13 '||MI_RS.AREA_CONSTRUIDA;
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@p14 '||MI_RS.PAGO_ANO||CHR(13)
                        ||'@p15 '||MI_RS.PAG_FEC||CHR(13)
                        ||'@p16 '||TO_CHAR(MI_RS.PAG_VAL, '999,999,999,999,999,999')||CHR(13)
                        ||'@p17 '||MI_RS.DIRECCION||CHR(13)
                        ||'@p18 '||MI_RS.DESTINO_ECONOMICO;
            FOR MI_RSFAC IN(
                SELECT   PREANO
                       , TRPPOR
                       , AVALUO
                       , C1
                       , C2
                       , C3
                       , C4
                       , C13
                       , (C14+C15+C16+C17+C18+C19+C20) OTROS
                       , (C1+C2+C3+C4+C13+C14+C15+C16+C17+C18+C19+C20) TOTAL 
                 FROM IP_FACTURADOS 
                WHERE COMPANIA = UN_COMPANIA
                  AND CODIGO=MI_RS.CODIGO 
                  AND PAGADO=0 
                ORDER BY PREANO)
            LOOP
                MI_TAR:= NVL(MI_RSFAC.TRPPOR, 0) * 1000;
                MI_CADENA:= MI_CADENA||CHR(13)||'@d15 '
                            ||LPAD(MI_RSFAC.PREANO, 14, ' ')
                            ||LPAD(MI_TAR, 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.AVALUO, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.C1, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.C2, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.C3, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.C4, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.C13, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.OTROS, '999,999,999,999,999,999'), 14, ' ')
                            ||LPAD(TO_CHAR(MI_RSFAC.TOTAL, '999,999,999,999,999,999'), 14, ' ');
            END LOOP;

            MI_DBLINTERES:= MI_REC_C2 + MI_REC_C6 + MI_REC_C10;
            MI_DBLTOTAL:= MI_REC_C1 + MI_REC_C5 + MI_REC_C9 + MI_REC_C2 + MI_REC_C6 + MI_REC_C10;
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@d20 '
                        ||LPAD('Predial', 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C1, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C5, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C9, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_DBLINTERES, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_DBLTOTAL, '999,999,999,999,999,999'), 17, ' ');
            MI_DBLINTERES:= MI_REC_C4 + MI_REC_C8 + MI_REC_C12;
            MI_DBLTOTAL:= MI_REC_C3 + MI_REC_C7 + MI_REC_C11 + MI_REC_C4 + MI_REC_C8 + MI_REC_C12;
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@d20 '
                        ||LPAD('CAR', 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C3, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C7, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C11, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_DBLINTERES, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(TO_CHAR(MI_DBLTOTAL, '999,999,999,999,999,999'), 17, ' ');
            MI_CADENA:= MI_CADENA||CHR(13)||'@d20 '
                        ||LPAD('Descuento', 17, ' ')
                        ||LPAD(TO_CHAR(MI_REC_C13, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(0, 17, ' ')
                        ||LPAD(0, 17, ' ')
                        ||LPAD(0, 17, ' ')
                        ||LPAD(0, 17, ' ');
            MI_DBLTOTAL:= MI_REC_C14 + MI_REC_C15 + MI_REC_C16+ MI_REC_C17 + MI_REC_C18 + MI_REC_C19 + MI_REC_C20;
            MI_CADENA:= MI_CADENA||CHR(13)||'@d20 '
                        ||LPAD('Otros', 17, ' ')
                        ||LPAD(TO_CHAR(MI_DBLTOTAL, '999,999,999,999,999,999'), 17, ' ')
                        ||LPAD(0, 17, ' ') 
                        ||LPAD(0, 17, ' ')
                        ||LPAD(0, 17, ' ')
                        ||LPAD(0, 17, ' ');
            MI_CADENA:= MI_CADENA||CHR(13)||'@d28 '
                        ||MI_OBS_FACTURA
                        ||'/www.sysman.com.co'||CHR(13)
                        ||'@p25 '||TO_CHAR(MI_REC_PREVAL, '999,999,999,999,999,999');
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@p100 '||MI_RS.CODIGO||MI_REC_DOCNUM||CHR(13)
                        ||'@p101 '||MI_REC_PREVAL||CHR(13)
                        ||'@p102 '||NVL(MI_REC_PREFEC, '');
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@p103 '||CASE WHEN MI_REC_TOTAL2 > 0
                                         THEN MI_RS.CODIGO||MI_REC_DOCNUM
                                         ELSE ''
                                    END||CHR(13)
                        ||'@p104 '||CASE WHEN MI_REC_TOTAL2 > 0
                                         THEN MI_REC_TOTAL2
                                         ELSE ''
                                    END||CHR(13)
                        ||'@p105 '||CASE WHEN MI_REC_FECHA2 = '12:00:00 AM'
                                         THEN ''
                                         ELSE MI_REC_FECHA2
                                    END;
            MI_CADENA:= MI_CADENA||CHR(13)
                        ||'@p106 '||CASE WHEN MI_REC_TOTAL3 > 0
                                         THEN MI_RS.CODIGO||MI_REC_DOCNUM
                                         ELSE ''
                                    END||CHR(13)
                        ||'@p107 '||CASE WHEN MI_REC_TOTAL3 > 0
                                         THEN MI_REC_TOTAL3
                                         ELSE ''
                                    END||CHR(13)
                        ||'@p108 '||CASE WHEN MI_REC_FECHA3 = '12:00:00 AM'
                                         THEN ''
                                         ELSE MI_REC_FECHA3
                                    END;
            MI_CADENA:= MI_CADENA||CHR(13)||'FinDeDocumento';

            MI_RTACADENA:=MI_RTACADENA||MI_CADENA;
        END IF;
    END LOOP;

    RETURN MI_RTACADENA;
END FC_GENERAPLANOPUNTOS;


--27
FUNCTION FC_NOMBRECOPROPIETARIO 
/*
    NAME              : FC_NOMBRECOPROPIETARIO --> en Access CoPropietario002 PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el nombre de un copropietario
    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_NUMERO_ORDEN  => Numero de Orden del propietario del Predio
                         UN_CODIGOPRED    => Codigo de predio 

    @NAME:  consultarNombreCopropietario
    @METHOD:  GET
  */
( 
	   UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CODIGOPRED   IN PCK_SUBTIPOS.TI_CODPREDIO
    ,UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN
) RETURN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
AS
    MI_NOMBRE IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
BEGIN 
    BEGIN 
    	SELECT NOMBRE 
    	  INTO MI_NOMBRE
    	  FROM IP_USUARIOS_PREDIAL 
    	 WHERE COMPANIA     = UN_COMPANIA 
    	   AND CODIGO       = UN_CODIGOPRED
    	   AND NUMERO_ORDEN = UN_NUMERO_ORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NOMBRE:='';
    END;  
    RETURN MI_NOMBRE;
END FC_NOMBRECOPROPIETARIO;

--28
FUNCTION FC_ANIOS_PAGOS
/*
    NAME              : FC_ANIOS_PAGOS --> en Access ANIOS_PAGOS PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el nombre de un copropietario
    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_NUMERO_ORDEN  => Numero de Orden del propietario del Predio
                         UN_CODIGO        => Codigo de predio 

    @NAME:  consultarAniosPagos
    @METHOD:  GET
  */
(
	 UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
	,UN_CODIGO       IN PCK_SUBTIPOS.TI_CODPREDIO
  ,UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN
) RETURN VARCHAR2
AS 
    MI_ANIOS_PAGOS VARCHAR2(20 CHAR);
BEGIN                                   
    BEGIN 
    	SELECT PAGO_ANO 
        INTO MI_ANIOS_PAGOS
    	  FROM IP_USUARIOS_PREDIAL 
    	 WHERE COMPANIA     = UN_COMPANIA
    	   AND CODIGO       = UN_CODIGO
    	   AND NUMERO_ORDEN = UN_NUMERO_ORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    	MI_ANIOS_PAGOS:=NULL; 
    END;
    IF MI_ANIOS_PAGOS IS NOT NULL THEN
         MI_ANIOS_PAGOS:= (MI_ANIOS_PAGOS + 1)||' A '||EXTRACT(YEAR FROM SYSDATE);
    ELSE
      MI_ANIOS_PAGOS:= '_';
    END IF;
    RETURN MI_ANIOS_PAGOS;
END FC_ANIOS_PAGOS;


--29
FUNCTION FC_CALCULA_USUARIOSAFACTURAR
/*
    NAME              : FC_FACTURARLOTEPREDIAL --> en Access parte de Pantalla_Click PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 17/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el valor del numero de usuarios a facturar.

    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_CODIGOINICIAL => Codigo de predio Inicial para el filtro del proceso
                         UN_CODIGOFINAL   => Codigo de predio final para el filtro del proceso
                         UN_NOMBREINICIAL => Nombre del propietario del predio inicial para el filtro del proceso
                         UN_NOMBREFINAL   => Nombre del propietario del predio final para el filtro del proceso
                         UN_ANIOINICIAL   => Anio inicial para el filtro del proceso
                         UN_ANIOFINAL     => Anio final para el filtro del proceso
                         UN_HASTAANIO     => Anio hasta el cual se genera el proceso
                         UN_NITINICIAL    => Nit inicial para el filtro del proceso
                         UN_NITFINAL      => Nit final para el filtro del proceso
                         UN_NUMERO_ORDEN  => Numero de Orden del predio (Propietario)
                         UN_ORDEN         => Orden en el cual se generara el proceso
                         UN_VALORINFERIOR => Valor inferior del predio para el filtro del proceso
                         UN_VALORSUPERIOR => Valor superior del predio para el filtro del proceso
                         UN_TIPOPREDIO    => Tipo de predio que se selecciona en el formulario

    @NAME:  calcularUsuariosFacturaEnLote
    @METHOD:  GET
  */ 
(
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    , UN_NUMERO_ORDEN     IN PCK_SUBTIPOS.TI_NUMORDEN
    , UN_CODIGOINICIAL    IN PCK_SUBTIPOS.TI_CODPREDIO
    , UN_CODIGOFINAL      IN PCK_SUBTIPOS.TI_CODPREDIO
    , UN_DIRECCIONINICIAL IN IP_USUARIOS_PREDIAL.DIRECCION%TYPE
    , UN_DIRECCIONFINAL   IN IP_USUARIOS_PREDIAL.DIRECCION%TYPE
    , UN_NOMBREINICIAL    IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    , UN_NOMBREFINAL      IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    , UN_NITINICIAL       IN IP_USUARIOS_PREDIAL.NIT%TYPE
    , UN_NITFINAL         IN IP_USUARIOS_PREDIAL.NIT%TYPE 
    , UN_ANIOINICIAL      IN PCK_SUBTIPOS.TI_ANIO
    , UN_ANIOFINAL        IN PCK_SUBTIPOS.TI_ANIO
    , UN_VALORINFERIOR    IN PCK_SUBTIPOS.TI_DOBLE
    , UN_VALORSUPERIOR    IN PCK_SUBTIPOS.TI_DOBLE
    , UN_HASTAANO         IN PCK_SUBTIPOS.TI_ANIO
    , UN_TIPOPREDIO       IN VARCHAR2
)RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS
    MI_STRSQL PCK_SUBTIPOS.TI_STRSQL;
    MI_EXISTE PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
	  MI_STRSQL:=' SELECT COUNT (1)
                   FROM (SELECT U.CODIGO,
                                U.PAGO_ANO,
                                U.NOMBRE,
                                U.NUMERO_ORDEN,
                                U.NIT,
                                AVALUO_ANO
                           FROM IP_USUARIOS_PREDIAL U 
                              INNER JOIN IP_FACTURADOS FAC
                                  ON  U.COMPANIA     = FAC.COMPANIA
                                  AND U.CODIGO       = FAC.CODIGO
                                  AND U.NUMERO_ORDEN = FAC.NUMERO_ORDEN
                           WHERE U.COMPANIA               = '''||UN_COMPANIA||'''
                             AND U.NUMERO_ORDEN           = '''||UN_NUMERO_ORDEN||''' 
                             AND U.CODIGO                 BETWEEN '''||UN_CODIGOINICIAL||'''    AND '''||UN_CODIGOFINAL||''' 
                             AND U.DIRECCION              BETWEEN '''||UN_DIRECCIONINICIAL||''' AND '''||UN_DIRECCIONFINAL||'''
                             AND U.NOMBRE                 BETWEEN '''||UN_NOMBREINICIAL||'''    AND '''||UN_NOMBREFINAL||''' 
                             AND U.NIT                    BETWEEN '''||UN_NITINICIAL||'''       AND '''||UN_NITFINAL||''' 
                             AND U.PAGO_ANO               BETWEEN '''||UN_ANIOINICIAL||'''      AND '''||UN_ANIOFINAL||''' 
                             AND U.TOTAL                  BETWEEN   '||UN_VALORINFERIOR||'      AND '||UN_VALORSUPERIOR||'
                             AND U.INDBORRADO             IN (0) 
                             AND U.CODIGO_NO_ACTIVO       IN (0) 
                             AND U.IND_PROCESOJUD         IN (0)
                             AND FAC.PREANO               <= '||UN_HASTAANO||'
                             AND FAC.PAGADO               IN (0)
                             AND FAC.NOCOBRADO            IN (0)
                             AND NVL(FAC.INDPAGO_ACPAG,0) IN (0)';

    IF UN_TIPOPREDIO = 2 THEN
        MI_STRSQL:= MI_STRSQL||' AND AREA_CONSTRUIDA IN (0)'; 
    ELSIF UN_TIPOPREDIO = 3 THEN
        MI_STRSQL:= MI_STRSQL||' AND AREA_CONSTRUIDA NOT IN (0)';
    END IF;
    MI_STRSQL:=MI_STRSQL||' GROUP BY U.CODIGO,
                                     U.PAGO_ANO,
                                     U.NOMBRE,
                                     U.NUMERO_ORDEN,
                                     U.NIT,
                                     AVALUO_ANO) ';
    BEGIN 
      	EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      	MI_EXISTE:=0;
    END;
    RETURN MI_EXISTE;
END FC_CALCULA_USUARIOSAFACTURAR;

--30
FUNCTION FC_FACTURARLOTEPREDIAL
/*
    NAME              : FC_FACTURARLOTEPREDIAL --> en Access Pantalla_Click PredialP2016.05.06
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/07/2017
    TIME              : 10:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el valor del titulo del rango de descuento que se le realiza a un predio.

    PARAMETERS        :

    MODIFICATIONS     :  UN_COMPANIA      => Compañia de ingreso a la aplicación
                         UN_CODIGOINICIAL => Codigo de predio Inicial para el filtro del plano
                         UN_CODIGOFINAL   => Codigo de predio final para el filtro del plano
                         UN_NOMBREINICIAL => Nombre del propietario del predio inicial para el filtro del plano
                         UN_NOMBREFINAL   => Nombre del propietario del predio final para el filtro del plano
                         UN_ANIOINICIAL   => Anio inicial para el filtro del plano
                         UN_ANIOFINAL     => Anio final para el filtro del plano
                         UN_HASTAANIO     => Anio hasta el cual se genera el plano
                         UN_NITINICIAL    => Nit inicial para el filtro del plano
                         UN_NITFINAL      => Nit final para el filtro del plano
                         UN_NUMERO_ORDEN  => Numero de Orden del predio (Propietario)
                         UN_ORDEN         => Orden en el cual se generara el plano
                         UN_VALORINFERIOR => Valor inferior del predio para el filtro del plano
                         UN_VALORSUPERIOR => Valor superior del predio para el filtro del plano
                         UN_TIPOPREDIO    => Tipo de predio que se selecciona en el formulario

    @NAME:  facturarEnLote
    @METHOD:  GET
  */ 
(
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    , UN_NITCOMPANIA      IN COMPANIA.NITCOMPANIA%TYPE 
    , UN_SIGLACOMPANIA    IN COMPANIA.SIGLACOMPANIA%TYPE
    , UN_NUMERO_ORDEN     IN PCK_SUBTIPOS.TI_NUMORDEN
    , UN_CODIGOINICIAL    IN PCK_SUBTIPOS.TI_CODPREDIO
    , UN_CODIGOFINAL      IN PCK_SUBTIPOS.TI_CODPREDIO
    , UN_DIRECCIONINICIAL IN IP_USUARIOS_PREDIAL.DIRECCION%TYPE
    , UN_DIRECCIONFINAL   IN IP_USUARIOS_PREDIAL.DIRECCION%TYPE
    , UN_NOMBREINICIAL    IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    , UN_NOMBREFINAL      IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
    , UN_NITINICIAL       IN IP_USUARIOS_PREDIAL.NIT%TYPE
    , UN_NITFINAL         IN IP_USUARIOS_PREDIAL.NIT%TYPE 
    , UN_ANIOINICIAL      IN PCK_SUBTIPOS.TI_ANIO
    , UN_ANIOFINAL        IN PCK_SUBTIPOS.TI_ANIO
    , UN_VALORINFERIOR    IN PCK_SUBTIPOS.TI_DOBLE
    , UN_VALORSUPERIOR    IN PCK_SUBTIPOS.TI_DOBLE
    , UN_HASTAANO         IN PCK_SUBTIPOS.TI_ANIO
    , UN_FECHALIMITE      IN DATE
    , UN_TIPOPREDIO       IN VARCHAR2 
    , UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO

)  RETURN VARCHAR2
AS    
    MI_MANEJADIFPORC         PARAMETRO.VALOR%TYPE;
    MI_ANOFACLEY             PARAMETRO.VALOR%TYPE;
    MI_ANOMINAPAGAR          PARAMETRO.VALOR%TYPE;
    MI_MANEJADESCESP         PARAMETRO.VALOR%TYPE;
    MI_MANMODLEYDESC         PARAMETRO.VALOR%TYPE;
    MI_PLANOASOBAN           PARAMETRO.VALOR%TYPE;
    MI_INICIO                PARAMETRO.VALOR%TYPE;
    MI_EXISTE                PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_I                     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_PREDREPORTE           PCK_SUBTIPOS.TI_CONDICION;
    MI_DOCNUM                PCK_SUBTIPOS.TI_DOCNUM;
    MI_DOCNUMINICIO          PCK_SUBTIPOS.TI_DOCNUM;
    MI_STRSQL                PCK_SUBTIPOS.TI_STRSQL;
    MI_CODIGO                IP_USUARIOS_PREDIAL.CODIGO%TYPE;
    MI_PAGO_ANO              IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE;
    MI_NOMBRE                IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
    MI_NUMERO_ORDEN          IP_USUARIOS_PREDIAL.NUMERO_ORDEN%TYPE;
    MI_NIT                   IP_USUARIOS_PREDIAL.NIT%TYPE;
    MI_AVALUO_ANO            IP_USUARIOS_PREDIAL.AVALUO_ANO%TYPE;
    MI_RS                    SYS_REFCURSOR;
    MI_MINANIO               IP_FACTURADOS.PREANO%TYPE;
    MI_MAXANIO               IP_FACTURADOS.PREANO%TYPE;
BEGIN 
    MI_MANEJADIFPORC:=PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'MANEJA DIF. PORCENTAJES EN LEY DESCUENTOS ESPECIALES'
    	                       ,UN_FECHA_PAR => SYSDATE );

    MI_ANOFACLEY    :=PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'VIGENCIA MAX PERMITIDA OPCION DESC ESP'
    	                       ,UN_FECHA_PAR => SYSDATE );

    MI_ANOMINAPAGAR :=PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'VIGENCIA MIN POR PAGAR PARA DESC ESP'
    	                       ,UN_FECHA_PAR => SYSDATE );

    MI_MANEJADESCESP:=PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'MANEJA OPCION DESCUENTOS ESPECIALES'
    	                       ,UN_FECHA_PAR => SYSDATE );

    MI_MANMODLEYDESC:=PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'PERMITE MODIFICACIONES LEY DESC. ESPECIALES'
    	                       ,UN_FECHA_PAR => SYSDATE );

    MI_PLANOASOBAN:= PCK_SYSMAN_UTL.FC_PAR(
    	                        UN_COMPANIA  => UN_COMPANIA
    	                       ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
    	                       ,UN_NOMBRE    => 'GENERAR PLANO FACTURACION ASOBANCARIA'
    	                       ,UN_FECHA_PAR => SYSDATE );


    MI_INICIO:=PCK_SYSMAN_UTL.FC_PAR(
                         UN_COMPANIA  => UN_COMPANIA
                        ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
                        ,UN_NOMBRE    => 'INICIO FACTURACION'
                        ,UN_FECHA_PAR => SYSDATE );

    IF MI_MANEJADIFPORC = 'SI' THEN
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE1
                       ,UN_TABLAERROR => 'PARAMETRO'
                       );
        END;
    END IF;
    --' Con el parametro se controla la aplicación de la ley de descuento vigente
    IF MI_MANEJADESCESP = 'SI' THEN
        BEGIN  
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE2
                       ,UN_TABLAERROR => 'PARAMETRO'
                       );
        END;
    END IF;
        --'Valida que el año hasta el cual se va a calcular corresponda al año valido en la ley de descuento
    IF UN_HASTAANO <> EXTRACT(YEAR FROM SYSDATE) THEN
        IF NVL(MI_MANMODLEYDESC,0) = '1' THEN
            IF UN_HASTAANO <> MI_ANOFACLEY THEN
                DECLARE 
                    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
                BEGIN 
                    MI_REEMPLAZOS(0).CLAVE:='MI_ANOFACLEY';
                    MI_REEMPLAZOS(0).VALOR:=MI_ANOFACLEY;
		                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
		            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE3
                               ,UN_TABLAERROR => 'PARAMETRO'
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
            END IF;
        ELSE
            IF UN_HASTAANO <> MI_ANOMINAPAGAR THEN
                BEGIN   
		                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
		            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE4
                               ,UN_TABLAERROR => 'PARAMETRO');
                END;
            END IF;
        END IF;
    END IF; 
    MI_EXISTE:=PCK_PREDIAL_COM8.FC_CALCULA_USUARIOSAFACTURAR(
                                UN_COMPANIA         => UN_COMPANIA
                               ,UN_NUMERO_ORDEN     => UN_NUMERO_ORDEN
                               ,UN_CODIGOINICIAL    => UN_CODIGOINICIAL
                               ,UN_CODIGOFINAL      => UN_CODIGOFINAL
                               ,UN_DIRECCIONINICIAL => UN_DIRECCIONINICIAL
                               ,UN_DIRECCIONFINAL   => UN_DIRECCIONFINAL
                               ,UN_NOMBREINICIAL    => UN_NOMBREINICIAL
                               ,UN_NOMBREFINAL      => UN_NOMBREFINAL
                               ,UN_NITINICIAL       => UN_NITINICIAL
                               ,UN_NITFINAL         => UN_NITFINAL
                               ,UN_ANIOINICIAL      => UN_ANIOINICIAL
                               ,UN_ANIOFINAL        => UN_ANIOFINAL
                               ,UN_VALORINFERIOR    => UN_VALORINFERIOR
                               ,UN_VALORSUPERIOR    => UN_VALORSUPERIOR
                               ,UN_HASTAANO         => UN_HASTAANO
                               ,UN_TIPOPREDIO       =>UN_TIPOPREDIO);

    IF MI_EXISTE IN (0) THEN
        BEGIN 
		        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
		    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
			      PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE5
                               ,UN_TABLAERROR => 'IP_USUARIOS_PREDIAL');
		    END;
    END IF;

    MI_STRSQL:='SELECT 
                         U.CODIGO 
                       , U.PAGO_ANO 
                       , U.NOMBRE
                       , U.NUMERO_ORDEN
                       , U.NIT
                       , AVALUO_ANO
                  FROM IP_USUARIOS_PREDIAL U 
                      INNER JOIN IP_FACTURADOS FAC
                         ON  U.COMPANIA     = FAC.COMPANIA
                         AND U.CODIGO       = FAC.CODIGO
                         AND U.NUMERO_ORDEN = FAC.NUMERO_ORDEN
                 WHERE U.COMPANIA               = '''||UN_COMPANIA||'''
                   AND U.NUMERO_ORDEN           = '''||UN_NUMERO_ORDEN||''' 
                   AND U.CODIGO                 BETWEEN '''||UN_CODIGOINICIAL||'''    AND '''||UN_CODIGOFINAL||''' 
                   AND U.DIRECCION              BETWEEN '''||UN_DIRECCIONINICIAL||''' AND '''||UN_DIRECCIONFINAL||'''
                   AND U.NOMBRE                 BETWEEN '''||UN_NOMBREINICIAL||'''    AND '''||UN_NOMBREFINAL||''' 
                   AND U.NIT                    BETWEEN '''||UN_NITINICIAL||'''       AND '''||UN_NITFINAL||''' 
                   AND U.PAGO_ANO               BETWEEN '''||UN_ANIOINICIAL||'''      AND '''||UN_ANIOFINAL||''' 
                   AND U.TOTAL                  BETWEEN   '||UN_VALORINFERIOR||'      AND '||UN_VALORSUPERIOR||'
                   AND U.INDBORRADO             IN (0) 
                   AND U.CODIGO_NO_ACTIVO       IN (0) 
                   AND U.IND_PROCESOJUD         IN (0)
                   AND FAC.PREANO                 <= '||UN_HASTAANO||'
                   AND FAC.PAGADO               IN (0)
                   AND FAC.NOCOBRADO            IN (0)
                   AND NVL(FAC.INDPAGO_ACPAG,0) IN (0)
                 ';
    IF UN_TIPOPREDIO = 2 THEN
        MI_STRSQL:= MI_STRSQL||' AND U.AREA_CONSTRUIDA IN (0)';   
    ELSIF UN_TIPOPREDIO = 3 THEN
        MI_STRSQL:= MI_STRSQL||' AND U.AREA_CONSTRUIDA NOT IN (0)';
    END IF;
    MI_STRSQL:=MI_STRSQL||' GROUP BY U.CODIGO,
                                     U.PAGO_ANO,
                                     U.NOMBRE,
                                     U.NUMERO_ORDEN,
                                     U.NIT,
                                     AVALUO_ANO ';
    IF MI_INICIO = 'NO' THEN
       MI_I:=0;
       OPEN MI_RS FOR MI_STRSQL;
       <<FACTURANDO>>
       LOOP
           FETCH MI_RS INTO   MI_CODIGO
                            , MI_PAGO_ANO
                            , MI_NOMBRE
                            , MI_NUMERO_ORDEN
                            , MI_NIT
                            , MI_AVALUO_ANO;
           EXIT WHEN MI_RS%NOTFOUND;
           BEGIN 
               SELECT MIN(IP_FACTURADOS.PREANO) MINIMO,
                      MAX(IP_FACTURADOS.PREANO) MAXIMO
                 INTO MI_MINANIO,
                      MI_MAXANIO
                 FROM IP_FACTURADOS
                WHERE IP_FACTURADOS.COMPANIA  = UN_COMPANIA
                  AND IP_FACTURADOS.CODIGO    = MI_CODIGO
                  AND IP_FACTURADOS.PREANO    > MI_PAGO_ANO
                  AND IP_FACTURADOS.PAGADO    IN(0)
                  AND IP_FACTURADOS.NOCOBRADO IN(0);
           EXCEPTION WHEN NO_DATA_FOUND THEN 
               MI_MINANIO:=MI_PAGO_ANO;
               MI_MAXANIO:=MI_PAGO_ANO;
           END;
           MI_DOCNUM:=PCK_PREDIAL_COM3.FC_FACTURAR(
                                        UN_COMPANIA                => UN_COMPANIA
                                      , UN_NITCOMPANIA             => UN_NITCOMPANIA
                                      , UN_SIGLACOMPANIA           => UN_SIGLACOMPANIA
                                      , UN_CODIGOPREDIO            => MI_CODIGO
                                      , UN_PAGO_ANO                => MI_PAGO_ANO
                                      , UN_UNICO_ANO               => 0
                                      , UN_FECHACORTE              => SYSDATE 
                                      , UN_FECHALIMITE             => UN_FECHALIMITE
                                      , UN_NOMBRE_PROPIETARIO      => MI_NOMBRE
                                      , UN_NUMEROORDEN_PROPIETARIO => MI_NUMERO_ORDEN
                                      , UN_NIT_PROPIETARIO         => MI_NIT
                                      , UN_FACTURA_CERO            => -1
                                      , UN_USUARIO                 => UN_USUARIO 
                                      , UN_ANIOINICIAL             => MI_MINANIO
                                      , UN_ANIOFIN                 => UN_HASTAANO 
                                      , UN_AVALUO_ANO              => MI_AVALUO_ANO
                                      , UN_APLICADESC              => 0);
            IF MI_I = 0 THEN 
                MI_DOCNUMINICIO:=MI_DOCNUM;
            END IF;
            MI_I:=MI_I+1;
        END LOOP FACTURANDO;
    ELSE
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURAR_EN_LOTE6
                       ,UN_TABLAERROR => 'IP_USUARIOS_PREDIAL');
        END;
    END IF;
    RETURN MI_DOCNUMINICIO||','||MI_DOCNUM;
END FC_FACTURARLOTEPREDIAL;

--31

FUNCTION FC_VALIDARFACTURAMF
/*
        NAME              : FC_VALIDARFACTURAMF --> en Access validarFacturaMF
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 24/07/2017
        TIME              : 08:00 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Se verifica si el usuarios tiene en activos recibo multifechas, en caso que si actualiza recibos de pagos.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_NUMERO_ORDEN     => Número de Orden Predial 
                            UN_FECHAPAGO        => Fecha con la que esta registrada el paquete y el banco del registro
                            UN_NUMFACTURA       => código de fáctura a insertar
                            UN_USUARIO          => Usuario que accede al sistema 

        @NAME  :  validarFacturaMF
        @METHOD:  POST    
      */ 
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_FECHAPAGO    IN DATE,
    UN_NUMFACTURA   IN IP_RECIBOS_DE_PAGO.DOCNUM%TYPE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO 
    )RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_CUENTA       PCK_SUBTIPOS.TI_ENTERO;
    MI_STRMES       PCK_SUBTIPOS.TI_ENTERO;
    MI_CONCDES      PARAMETRO.VALOR%TYPE;
    MI_CONCDESCAR   PARAMETRO.VALOR%TYPE;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA          PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_I            PCK_SUBTIPOS.TI_ENTERO;
    MI_RESPUESTA    PCK_SUBTIPOS.TI_ENTERO;
    MI_MES          PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHA        IP_RECIBOS_MULTIFECHA.FECHA1%TYPE;
    MI_DESC         IP_RECIBOS_MULTIFECHA.DESC1%TYPE;
    MI_TOTAL        IP_RECIBOS_MULTIFECHA.TOTAL1%TYPE;
    MI_IMP_ACT      IP_RECIBOS_MULTIFECHA.IMP_ACT1%TYPE;
    MI_INT_ACT      IP_RECIBOS_MULTIFECHA.INT_ACT1%TYPE;
    MI_IMP_ANT      IP_RECIBOS_MULTIFECHA.IMP_ANT1%TYPE;
    MI_INT_ANT      IP_RECIBOS_MULTIFECHA.INT_ANT1%TYPE;
    MI_IMP_DR       IP_RECIBOS_MULTIFECHA.IMP_DR1%TYPE;
    MI_INT_DR       IP_RECIBOS_MULTIFECHA.INT_DR1%TYPE;
    MI_INT          IP_RECIBOS_DE_PAGO.INT1%TYPE;
    MI_INTCAR       IP_RECIBOS_DE_PAGO.INTCAR1%TYPE;
    MI_DESC_CAR     IP_RECIBOS_DE_PAGO.DESC_CAR1%TYPE;
    MI_MESCONSULTA  DATE;
    MI_INDAPORTE    IP_RECIBOS_DE_PAGO.INDAPORTE%TYPE;
    MI_VALOR_APORTE IP_RECIBOS_DE_PAGO.VALOR_APORTE%TYPE;
    MI_VARIABLE     PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
BEGIN
      MI_VARIABLE   := 0;
      MI_TABLA      := 'IP_RECIBOS_DE_PAGO';
      MI_STRMES     := TO_CHAR(EXTRACT(MONTH FROM UN_FECHAPAGO));
      MI_CONCDES    := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,
                                             UN_NOMBRE => 'CONCEPTO DE DESCUENTO' ,
                                             UN_MODULO => PCK_DATOS.MODULOPREDIAL ,
                                             UN_FECHA_PAR => SYSDATE);
      MI_CONCDESCAR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,
                                             UN_NOMBRE => 'CONCEPTO PARA DESCUENTO CAR' ,
                                             UN_MODULO => PCK_DATOS.MODULOPREDIAL ,
                                             UN_FECHA_PAR => SYSDATE);
      BEGIN
          MI_STRSQL  := 'SELECT FECHA' || MI_STRMES || ',       DESC' || MI_STRMES || 
                        ',      TOTAL' || MI_STRMES || ',    IMP_ACT' || MI_STRMES || 
                        ',    INT_ACT' || MI_STRMES || ',     IMP_ANT'|| MI_STRMES || 
                        ',    INT_ANT' || MI_STRMES || ',     IMP_DR' || MI_STRMES || 
                        ',     INT_DR' || MI_STRMES || ' FROM IP_RECIBOS_MULTIFECHA                  
                        WHERE COMPANIA   = ''' || UN_COMPANIA || '''                  
                        AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN || '''                  
                        AND DOCNUM       = ''' || UN_NUMFACTURA || '''                  
                        AND PAGO        IN (0)                  
                        AND ANULADO     IN (0)' ;
          EXECUTE IMMEDIATE MI_STRSQL INTO MI_FECHA,MI_DESC, MI_TOTAL,MI_IMP_ACT,
                                           MI_INT_ACT, MI_IMP_ANT,
                                           MI_INT_ANT, MI_IMP_DR,
                                           MI_INT_DR;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_FECHA := NULL;
      END;      
      IF MI_FECHA IS NOT NULL AND TRUNC(MI_FECHA) <> TO_DATE('01/01/1900','DD/MM/YYYY') THEN
          MI_CAMPOS    := 'PREFECLIM          = ''' || MI_FECHA || ''',' || 
                          'C13                = '   || MI_DESC || ',' || 
                          'PREVAL             = '   || MI_TOTAL || ',' || 
                          'C1                 = ' || MI_IMP_ACT|| ',' || 
                          'C2                 = ' || MI_INT_ACT|| ',' || 
                          'C5                 = ' || MI_IMP_ANT|| ',' || 
                          'C6                 = ' || MI_INT_ANT|| ',' || 
                          'C9                 = ' || MI_IMP_DR || ',' || 
                          'C10                = ' || MI_INT_DR || ',' || 
                          'MODIFIED_BY        = ''' || UN_USUARIO|| ''',' || 
                          'DATE_MODIFIED      = SYSDATE';
          MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA ||''''|| 
                          ' AND DOCNUM    = ''' || UN_NUMFACTURA || '''';
          BEGIN
              BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, 
                                               UN_ACCION => 'M', 
                                               UN_CAMPOS => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_REEMPLAZOS (0).CLAVE := 'TABLA';
              MI_REEMPLAZOS (0).VALOR := MI_TABLA;
              MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';
              MI_REEMPLAZOS (1).VALOR := MI_CAMPOS;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                                         UN_TABLAERROR => MI_TABLA, 
                                         UN_ERROR_COD => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_UPDATE, 
                                         UN_REEMPLAZOS => MI_REEMPLAZOS);
          END;
          IF MI_RTA IN (0) THEN
              BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTMULTIFECHAS);
              END;
          ELSE
              RETURN 1; 
          END IF;
      ELSE
          FOR MI_I IN 1..12 LOOP
              BEGIN
                  MI_STRSQL := 'SELECT FECHA' || MI_I || ',       DESC' || MI_I || 
                                    ',      TOTAL' || MI_I || ',    IMP_ACT' || MI_I || 
                                    ',    INT_ACT' || MI_I || ',     IMP_ANT'|| MI_I || 
                                    ',    INT_ANT' || MI_I || ',     IMP_DR' || MI_I || 
                                    ',     INT_DR' || MI_I || ' FROM IP_RECIBOS_MULTIFECHA                  
                                      WHERE COMPANIA   = ''' || UN_COMPANIA || '''                  
                                        AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN || '''                  
                                        AND DOCNUM       = ''' || UN_NUMFACTURA || '''                  
                                        AND PAGO        IN (0)                  
                                        AND ANULADO     IN (0)';
                  EXECUTE IMMEDIATE MI_STRSQL INTO MI_FECHA,MI_DESC,MI_TOTAL,MI_IMP_ACT,
                                                                 MI_INT_ACT, MI_IMP_ANT,
                                                                 MI_INT_ANT, MI_IMP_DR,
                                                                 MI_INT_DR;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_FECHA := NULL;
              END;
              IF MI_FECHA IS NOT NULL AND MONTHS_BETWEEN (TRUNC(UN_FECHAPAGO), TRUNC(MI_FECHA)) <= 0 THEN
                  MI_CAMPOS :=  'PREFECLIM  = ''' || MI_FECHA || ''',' || 
                                'C13                = '   || MI_DESC || ',' || 
                                'PREVAL             = '   || MI_TOTAL || ',' || 
                                'C1                 = ' || MI_IMP_ACT|| ',' || 
                                'C2                 = ' || MI_INT_ACT|| ',' || 
                                'C5                 = ' || MI_IMP_ANT|| ',' || 
                                'C6                 = ' || MI_INT_ANT|| ',' || 
                                'C9                 = ' || MI_IMP_DR || ',' || 
                                'C10                = ' || MI_INT_DR || ',' || 
                                'MODIFIED_BY        = ''' || UN_USUARIO|| ''',' || 
                                'DATE_MODIFIED      = SYSDATE';
                  MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA ||''''|| 
                                  ' AND DOCNUM    = ''' || UN_NUMFACTURA || '''';
                  BEGIN
                      BEGIN
                          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, 
                                                       UN_ACCION => 'M', 
                                                       UN_CAMPOS => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                      MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                      MI_REEMPLAZOS (0).VALOR := MI_TABLA;
                      MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';
                      MI_REEMPLAZOS (1).VALOR := MI_CAMPOS;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                                                 UN_TABLAERROR => MI_TABLA, 
                                                 UN_ERROR_COD => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_UPDATE, 
                                                 UN_REEMPLAZOS => MI_REEMPLAZOS);
                  END;
                  IF MI_RTA IN (0) THEN
                      BEGIN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTMULTIFECHAS);
                      END;
                  ELSE 
                      RETURN 1;
                  END IF;
                   END IF;
          END LOOP;


      FOR MI_I IN 1..12 LOOP
          IF NVL(TRIM(MI_CONCDES),'') IS NULL THEN
              MI_CONCDES := 13;
          END IF;
          BEGIN 
              MI_STRSQL:='SELECT INDAPORTE, VALOR_APORTE
                                        ,FECHA' || MI_STRMES ||'
                                        ,FECHA' || MI_I   ||
                                        ',TOTAL'|| MI_I || 
                                        ',DESC' ||   MI_I   || 
                                        ',INT'  || MI_I || 
                                        ',INTCAR' ||   MI_I   ||
                                        ',DESC_CAR' || MI_I ||  
                                   ' FROM IP_RECIBOS_DE_PAGO
                                  WHERE COMPANIA     =''' || UN_COMPANIA   ||''' 
                                     AND DOCNUM      =''' || UN_NUMFACTURA ||'''
                                     AND PAGO        IN (0)
                                     AND ANULADO     IN (0)';
              EXECUTE IMMEDIATE MI_STRSQL INTO MI_INDAPORTE,  
                                               MI_VALOR_APORTE,
                                               MI_MESCONSULTA,
                                               MI_FECHA,
                                               MI_TOTAL,      
                                               MI_DESC,
                                               MI_INT,        
                                               MI_INTCAR,
                                               MI_DESC_CAR;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_FACTURANOREGISTRA);
              END;
          END;
          IF MI_MESCONSULTA IS NOT NULL AND TRUNC(MI_FECHA) <> TO_DATE('01/01/1900','DD/MM/YYYY') THEN
              MI_VARIABLE := MI_VARIABLE +1;
              IF MONTHS_BETWEEN (UN_FECHAPAGO, MI_FECHA) <= 0 AND  MI_TOTAL IS NOT NULL THEN
                  IF TRIM(MI_CONCDESCAR) IS NULL OR TRIM(MI_CONCDESCAR) IN (0) OR TRIM(MI_CONCDESCAR) IN ('_') THEN
                          MI_CAMPOS   := 'PREFECLIM          = ''' || MI_FECHA || ''',' || 
                                         'C' ||MI_CONCDES||' = '   || MI_DESC || ',' || 
                                         'PREVAL             = '   || (CASE WHEN MI_INDAPORTE NOT IN (0) THEN
                                                                      MI_TOTAL
                                                                    ELSE
                                                                      MI_TOTAL + MI_VALOR_APORTE
                                                                    END) || 
                                         'C2                 = '   || MI_INT || ',' || 
                                         'C4                 = '   || MI_INTCAR || ',' || 
                                         'MODIFIED_BY        = ''' || UN_USUARIO || ''',' || 
                                         'DATE_MODIFIED      = SYSDATE';
                          MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA ||'''
                                          AND DOCNUM    = ''' || UN_NUMFACTURA || '''';
                          BEGIN
                              BEGIN
                                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                             UN_ACCION    => 'M', 
                                                             UN_CAMPOS    => MI_CAMPOS, 
                                                             UN_CONDICION => MI_CONDICION);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                              MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                              MI_REEMPLAZOS (0).VALOR := MI_TABLA;
                              MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';
                              MI_REEMPLAZOS (1).VALOR := MI_CAMPOS;
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                                                         UN_TABLAERROR => MI_TABLA, 
                                                         UN_ERROR_COD => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_UPDATE, 
                                                         UN_REEMPLAZOS => MI_REEMPLAZOS);
                          END;
                      ELSE
                          MI_CAMPOS   := 'PREFECLIM           = '''   || MI_FECHA ||
                                         ',C' ||MI_CONCDES|| '= '     || MI_DESC ||
                                         ',PREVAL             = '     || CASE WHEN MI_INDAPORTE NOT IN (0) THEN
                                                                              MI_TOTAL
                                                                          ELSE
                                                                              MI_TOTAL + MI_VALOR_APORTE
                                                                          END || 
                                         ',C2                 = '     || MI_INT || 
                                         ',C4                  = '    || MI_INTCAR || ',' || 
                                         ',C'||MI_CONCDESCAR||'= '    || MI_DESC_CAR || ',' || 
                                         ',MODIFIED_BY         = ''' || UN_USUARIO || ''',' || 
                                         ',DATE_MODIFIED       = SYSDATE';
                          MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA ||''''|| 
                                          ' AND DOCNUM    = ''' || UN_NUMFACTURA || '''';
                          BEGIN
                              BEGIN
                                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA, 
                                                               UN_ACCION => 'M', 
                                                               UN_CAMPOS => MI_CAMPOS, 
                                                               UN_CONDICION => MI_CONDICION);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                              MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                              MI_REEMPLAZOS (0).VALOR := MI_TABLA;
                              MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';
                              MI_REEMPLAZOS (1).VALOR := MI_CAMPOS;
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                                                         UN_TABLAERROR => MI_TABLA, 
                                                         UN_ERROR_COD => PCK_ERRORES.ERR_IP_REGISTRAR_PAGO_UPDATE, 
                                                         UN_REEMPLAZOS => MI_REEMPLAZOS);
                          END;
                    END IF;
                    IF MI_RTA IN (0) THEN
                        BEGIN
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTMULTIFECHAS);
                        END;
                    ELSE 
                        RETURN 1;    
                    END IF;
                END IF;
            END IF;
        END LOOP;  
    END IF;
RETURN 1;
END FC_VALIDARFACTURAMF;

--32

FUNCTION FC_VALIDAPAGO 
/*
        NAME              : FC_VALIDAPAGO --> en Access antesEditarSub
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 24/07/2017
        TIME              : 08:00 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Se valida fecha es valida, si datos de factura coinciden, si se tienen abonos en factura.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_NUMERO_ORDEN     => Número de Orden Predial 
                            UN_PAQUETE          => Paquete del registro al que se ingresa a registrar el pago
                            UN_PAGOBANCO        => Banco del registro al que se ingresa a registrar el pago
                            UN_CODIGO           => Código del usuario al que corresponde factura a registrar 
                            UN_PREFECHAVAR      => Fecha con la que esta registrada el paquete y el banco del registro
                            UN_NUMFACTURA       => código de fáctura a insertar
                            UN_USUARIO          => Usuario que accede al sistema 

        @NAME  :  ValidaPago
        @METHOD:  POST    
      */ 
(
    UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO_ORDEN     IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_PAQUETE          IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE,
    UN_PAGOBANCO        IN IP_RECIBOS_DE_PAGO.PAG_BANPAG%TYPE,
    UN_PREFECHAVAR      IN DATE,
    UN_CODIGO           IN IP_RECIBOS_DE_PAGO.PRECOD%TYPE,
    UN_NUMFACTURA       IN IP_RECIBOS_DE_PAGO.DOCNUM%TYPE,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO AS
    MI_CUENTA     PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;  
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RESPUESTA  PCK_SUBTIPOS.TI_LOGICO;
    MI_PREFEC     IP_RECIBOS_DE_PAGO.PREFEC%TYPE;
    MI_ESACUERDO  IP_RECIBOS_DE_PAGO.ESACUERDO%TYPE;
    MI_ACUERDO    IP_RECIBOS_DE_PAGO.ACUERDO%TYPE;
    MI_ESABONO    IP_RECIBOS_DE_PAGO.ESABONO%TYPE;
    MI_VALOR      PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHAFACTURADO DATE;
    MI_TOTALABONO PCK_SUBTIPOS.TI_DOBLE;
    MI_CUENTAABONOS PCK_SUBTIPOS.TI_ENTERO;
    MI_PREVAL       PCK_SUBTIPOS.TI_ENTERO;
    MI_RECIBOACTUAL IP_USUARIOS_PREDIAL.RECIBO_ACTUAL%TYPE;
    MI_PAGOANO      IP_USUARIOS_PREDIAL.PAGO_ANO%TYPE;
    MI_CUENTAANIO   PCK_SUBTIPOS.TI_ENTERO;
    MI_PARAMCONFREGIS PARAMETRO.VALOR%TYPE;
    STRNUMFACTURA   IP_USUARIOS_PREDIAL.RECIBO_ACTUAL%TYPE;
    MI_VARIABLE     PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    MI_VARIABLE := 0;
    BEGIN
        SELECT PREFEC, ESACUERDO, ACUERDO, ESABONO, PREVAL
          INTO  MI_PREFEC, MI_ESACUERDO, MI_ACUERDO, MI_ESABONO, MI_PREVAL
          FROM IP_RECIBOS_DE_PAGO
         WHERE COMPANIA   = UN_COMPANIA
           AND PRECOD     = UN_CODIGO
           AND DOCNUM     = UN_NUMFACTURA
           AND ANULADO   IN (0)
           AND PAGO      IN (0);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_SINRECPENDIENTES);
        END;
    END;
    MI_RESPUESTA := FC_VALIDARFACTURAMF( UN_NUMERO_ORDEN => UN_NUMERO_ORDEN,
                                         UN_FECHAPAGO    => UN_PREFECHAVAR,
                                         UN_NUMFACTURA   => UN_NUMFACTURA,
                                         UN_USUARIO      => UN_USUARIO,
                                         UN_COMPANIA     => UN_COMPANIA );

        IF MI_RESPUESTA NOT IN (1) THEN
           RETURN 0;  
        END IF;
        IF MONTHS_BETWEEN (TRUNC(UN_PREFECHAVAR),TRUNC(MI_PREFEC)) < 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_MENORFECHAFACTURA);
            END;
        ELSE IF MI_ESACUERDO IN (0) AND MI_ACUERDO IS NOT NULL THEN
            BEGIN
                MI_REEMPLAZOS(0).CLAVE:= 'NACUERDO';
                MI_REEMPLAZOS(0).VALOR:= MI_ACUERDO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACTURASINRECA);
            END;
        ELSE IF MI_ESABONO NOT IN (0) THEN 
            BEGIN
                SELECT FECHAFACTURADO,SUM(TOTAL), COUNT(*) 
                  INTO MI_FECHAFACTURADO,MI_TOTALABONO,MI_CUENTAABONOS
                  FROM IP_FACTURADOSABONOS
                 WHERE COMPANIA   = UN_COMPANIA
                   AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                   AND CODIGO       = UN_CODIGO
                   AND PAGADO      IN 0
                 GROUP BY FECHAFACTURADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CUENTAABONOS := NULL;
                MI_VARIABLE := 1;
            END;
            IF MI_VARIABLE = 0 THEN
                IF MI_CUENTAABONOS IN (1) THEN
                    IF MI_PREVAL <> MI_TOTALABONO OR TRUNC(MI_PREFEC) <> TRUNC(MI_FECHAFACTURADO) THEN
                        BEGIN
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DATOSFACNOCOINC);
                        END;
                    END IF;
                ELSE
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ABONOSNOAPLICAN);
                    END;
                END IF;
            END IF;
            END IF;
        END IF;
        END IF;

        BEGIN 
          SELECT IP_USUARIOS_PREDIAL.RECIBO_ACTUAL,
                 IP_USUARIOS_PREDIAL.PAGO_ANO
            INTO MI_RECIBOACTUAL,
                 MI_PAGOANO
            FROM IP_USUARIOS_PREDIAL
           WHERE IP_USUARIOS_PREDIAL.COMPANIA     =  UN_COMPANIA
             AND IP_USUARIOS_PREDIAL.CODIGO       =  UN_CODIGO
             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN =  UN_NUMERO_ORDEN;

            EXCEPTION WHEN NO_DATA_FOUND THEN 
              MI_RECIBOACTUAL := NULL;
        END ;           
       IF (MI_PAGOANO - EXTRACT(YEAR FROM SYSDATE)) = 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PREDIOALDIAPAGOS);
            END;
        END IF;
        IF NVL(TRIM(UN_NUMFACTURA),'') IS NULL THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INGRESARNFACTURA);
            END;
        END IF;
        MI_PARAMCONFREGIS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,
                                                   UN_NOMBRE => 'CONFIRMAR NUMERO DE RECIBO AL REGISTRAR PAGO' ,
                                                   UN_MODULO => PCK_DATOS.MODULOPREDIAL ,
                                                   UN_FECHA_PAR => SYSDATE);
        IF (NVL(MI_PARAMCONFREGIS, 'NO')) = 'SI' THEN
            STRNUMFACTURA := NVL(MI_RECIBOACTUAL, '');
            IF UN_NUMFACTURA <> STRNUMFACTURA THEN 
                BEGIN
                    MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
                    MI_REEMPLAZOS(0).VALOR:= STRNUMFACTURA;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                EXCEPTION WHEN 	PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                             ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_NUMFACTNOCORRESP);
                END;
            END IF;
        END IF;
RETURN 1;
END FC_VALIDAPAGO;

--33
PROCEDURE PR_ACTUALIZAR_ANTESPAGOSDOBLES 
  /*
    NAME              : PR_ACTUALIZAR_ANTESPAGOSDOBLES - Access: PAGOSDOBLES.Form_BeforeUpdate
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 28/07/2017
    TIME              : 02:17 PM
    SOURCE MODULE     : IMPUESTO PREDIAL (60)
    DESCRIPTION       : Proceso que se ejecuta antes de realizar un pago doble.
    PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                        UN_VALOR            => Valor asociado al pago doble.
                        UN_CODIGO           => Codigo del usuario predial.
                        UN_NUMERO_ORDEN     => Numero de orden predial.
                        UN_ANIOEXC          => Anio al que se le va a aplicar el excedente.

    @NAME:   actualizarAntesPagosDobles
    @METHOD: GET                     
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VALOR          IN IP_PAGOSDOBLES.VALOR%TYPE,
    UN_CODIGO         IN IP_FACTURADOS.CODIGO%TYPE,
    UN_NUMERO_ORDEN   IN IP_FACTURADOS.NUMERO_ORDEN%TYPE,
    UN_ANIOEXC        IN PCK_SUBTIPOS.TI_ANIO
  )
AS
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_INDPAGADO PCK_SUBTIPOS.TI_LOGICO;
  MI_KEY       PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
BEGIN
  IF UN_VALOR IS NULL OR UN_VALOR <= 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_FCAAPD_MSG_VALIDVALORPD
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;
  END IF;

  BEGIN
    SELECT PAGADO 
    INTO MI_INDPAGADO
    FROM IP_FACTURADOS
    WHERE COMPANIA     = UN_COMPANIA
      AND CODIGO       = UN_CODIGO
      AND NUMERO_ORDEN = UN_NUMERO_ORDEN
      AND PREANO       = UN_ANIOEXC;

  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_KEY := -1;
  END;

  IF MI_KEY NOT IN(0) AND UN_ANIOEXC > EXTRACT(YEAR FROM SYSDATE) + 1 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_FCAAPD_MSG_VALIDAFECHAE
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;
  END IF;
END PR_ACTUALIZAR_ANTESPAGOSDOBLES;

END PCK_PREDIAL_COM8;