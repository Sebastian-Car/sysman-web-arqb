create or replace PACKAGE BODY PCK_CONTABILIZAR_ALMACEN AS

 --1
 FUNCTION FC_AJUSTESNIVELESNIIF
   /* 
        NAME              : En Access InterfaceAlmacenAjustesNivelesNIIF
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 27/07/2018
        TIME              : 08:37 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2018.07.05
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     : 

        @NAME:contabilizarAlmcnH
        @METHOD:
    */
   (
     UN_COMPANIAORIGEN  IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_COMPANIADESTINO IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_FECHINTERF      IN DATE,
     UN_TIPO            IN VARCHAR2,     
     UN_NUMERO          IN NUMBER,
     UN_TERCERO         IN PCK_SUBTIPOS.TI_TERCERO,
     UN_SUCURSAL        IN PCK_SUBTIPOS.TI_SUCURSAL,    
     UN_CENTROCOSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
     UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
   )RETURN CLOB
    AS
        MI_AGRUPACION    PCK_SUBTIPOS.TI_ENTERO;
        MI_DATOS         PCK_SUBTIPOS.TI_ENTERO;
        MI_RTAPLANO      CLOB;
        MI_CONSECUTIVO   PCK_SUBTIPOS.TI_ENTERO;
        MI_CUENTA        PCK_SUBTIPOS.TI_STRSQL; 
        MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
        MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
        MI_NATURALEZA    PCK_SUBTIPOS.TI_NATURALEZACONTA;
        MI_FILAS         PCK_SUBTIPOS.TI_ENTERO;
        MI_STRTEXTO      PCK_SUBTIPOS.TI_DESCRIPCION;
        MI_ANO           PCK_SUBTIPOS.TI_ANIO;
        MI_MES           PCK_SUBTIPOS.TI_MES;   
        MI_COMPROBANTE   NUMBER;
        MI_MANEJACENTRO  VARCHAR2(30);
        MI_TERCEROUNICO  VARCHAR2(30);
        MI_MENSAJECENTRO VARCHAR2(1000);
        MI_TERCERO       PCK_SUBTIPOS.TI_TERCERO;
        MI_SUCURSAL      PCK_SUBTIPOS.TI_SUCURSAL;   
        MI_CENTROCOSTO   PCK_SUBTIPOS.TI_CENTRO_COSTO;
        MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
        MI_ANO := TO_NUMBER(TO_CHAR(UN_FECHINTERF,'YYYY'));
        MI_MES := TO_NUMBER(TO_CHAR(UN_FECHINTERF,'MM'));
        MI_DATOS       :=0;
        MI_CONSECUTIVO :=0;
        MI_AGRUPACION:= TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIADESTINO, 
                                                            UN_NOMBRE    => 'DIGITOS AGRUPACION INVENTARIO' ,
                                                            UN_MODULO    => 10, 
                                                            UN_FECHA_PAR => SYSDATE ), '0'));
        MI_MANEJACENTRO:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIADESTINO, 
                                                    UN_NOMBRE    => 'MANEJA INTERFACE ALMACEN MENSUAL POR CENTRO COSTO' ,
                                                    UN_MODULO    => 10, 
                                                    UN_FECHA_PAR => SYSDATE ), 'NO');     

        MI_TERCEROUNICO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIADESTINO, 
                                                    UN_NOMBRE    => 'INTERFAZ ALMACEN CON UNICO TERCERO' ,
                                                    UN_MODULO    => 96, 
                                                    UN_FECHA_PAR => SYSDATE ), 'NO');     

        IF MI_TERCEROUNICO = 'SI' THEN
            MI_TERCERO     := UN_TERCERO;     
            MI_SUCURSAL    := UN_SUCURSAL;
            MI_CENTROCOSTO := UN_CENTROCOSTO;        
        ELSE
            MI_TERCERO     := PCK_DATOS.CONS_TERCERO;     
            MI_SUCURSAL    := PCK_DATOS.CONS_SUCURSAL;
            MI_CENTROCOSTO := PCK_DATOS.CONS_CENTRO;

        END IF;


        MI_RTAPLANO := TO_CLOB('Infome de advertencias interfase almacén a contabilidad'               || CHR(13) || CHR(10));   
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('             Fecha:' || TO_CHAR(SYSDATE, 'DD/MM/YYYY') || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('           Usuario:' || UN_USUARIO                     || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('          Compañia:' || UN_COMPANIAORIGEN              || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('               Año:' || MI_ANO                         || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('               Mes:' || MI_MES                         || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('  Tipo Comprobante:' || UN_TIPO                        || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('Número Comprobante:' || UN_NUMERO                      || CHR(13) || CHR(10));    
        MI_RTAPLANO := MI_RTAPLANO || CHR(13) || CHR(10);    

        MI_TABLA := 'TEMP_PLANA_AJUSTES';
        MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,TIPO_CPTE
                  ,COMPROBANTE
                  ,CONSECUTIVO
                  ,CUENTA
                  ,FECHA
                  ,NATURALEZA
                  ,VALOR_DEBITO
                  ,VALOR_CREDITO
                  ,EJECUCION_DEBITO
                  ,EJECUCION_CREDITO
                  ,TERCERO
                  ,SUCURSAL
                  ,CENTRO_COSTO
                  ,REFERENCIA';
        FOR RS IN(SELECT DEPRECIAR.COMPANIA,
                           CUENTADEBITO,
                           CUENTACREDITO,
                           PLAN_DEBITO.CODIGO      DEBITOPLAN,
                           PLAN_DEBITO.NATURALEZA  DEBITONATU,
                           PLAN_CREDITO.CODIGO     CREDITOPLAN,
                           PLAN_CREDITO.NATURALEZA CREDITONATU,
                           TO_CHAR(DEPRECIAR.PERIODO, 'YYYY') ANO,
                           TO_CHAR(DEPRECIAR.PERIODO, 'MM'  ) MES,
                           DEPRECIAR.REFERENCIA,
                           CASE WHEN MI_MANEJACENTRO = 'SI' THEN DEPRECIAR.CENTRO_COSTO ELSE '99999999999999999999' END CENTRO_COSTO,
                           SUBSTR(DEPRECIAR.ELEMENTO,1, MI_AGRUPACION) GRUPO,
                           SUM(DEPRECIAR.NIIF_VLRDEPRECIACION) AS VLRDEPRECIACION1
                    FROM DEPRECIAR LEFT JOIN NIIF_INVENTARIOCONTA NI
                      ON DEPRECIAR.COMPANIA                          = NI.COMPANIA
                     AND DEPRECIAR.ANO                               = NI.ANO 
                     AND SUBSTR(DEPRECIAR.ELEMENTO,1, MI_AGRUPACION) = NI.CODIGOELEMENTO 
                     AND DEPRECIAR.NIIF_TIPO_ACTIVO                  = NI.TIPOACTIVO   
                     AND DEPRECIAR.BODEGA                            = NI.BODEGA
                     AND CASE WHEN MI_MANEJACENTRO = 'SI' THEN DEPRECIAR.CENTRO_COSTO ELSE '99999999999999999999' END = NI.CENTRO_COSTO                    
                    LEFT JOIN (SELECT COMPANIA, ANO, CODIGO, NATURALEZA 
                               FROM PLAN_CONTABLE 
                               WHERE COMPANIA = UN_COMPANIADESTINO
                                 AND (MOVIMIENTO  NOT IN(0)
                                   OR MAN_CEN_CTO NOT IN(0)
                                   OR MAN_AUX_TER NOT IN(0)
                                   OR MAN_AUX_GEN NOT IN(0)
                                   OR MAN_AUX_REF NOT IN(0)
                                   OR MAN_AUX_FUE NOT IN(0)
                                      )
                              ) PLAN_DEBITO  
                     ON PLAN_DEBITO.COMPANIA = NI.COMPANIA
                    AND PLAN_DEBITO.ANO      = NI.ANO 
                    AND PLAN_DEBITO.CODIGO   = NI.CUENTADEBITO         
                    LEFT JOIN (SELECT COMPANIA, ANO, CODIGO, NATURALEZA 
                               FROM PLAN_CONTABLE 
                               WHERE COMPANIA = UN_COMPANIADESTINO
                                 AND (MOVIMIENTO  NOT IN(0)
                                   OR MAN_CEN_CTO NOT IN(0)
                                   OR MAN_AUX_TER NOT IN(0)
                                   OR MAN_AUX_GEN NOT IN(0)
                                   OR MAN_AUX_REF NOT IN(0)
                                   OR MAN_AUX_FUE NOT IN(0)
                                     )
                              ) PLAN_CREDITO  
                     ON PLAN_CREDITO.COMPANIA = NI.COMPANIA
                    AND PLAN_CREDITO.ANO      = NI.ANO 
                    AND PLAN_CREDITO.CODIGO   = NI.CUENTACREDITO   
                    WHERE DEPRECIAR.COMPANIA       = UN_COMPANIAORIGEN
                      AND DEPRECIAR.PERIODO        = UN_FECHINTERF 
                    GROUP BY DEPRECIAR.COMPANIA, 
                             CUENTADEBITO,
                             CUENTACREDITO,
                             PLAN_DEBITO.CODIGO,
                             PLAN_DEBITO.NATURALEZA,
                             PLAN_CREDITO.CODIGO, 
                             PLAN_CREDITO.NATURALEZA,
                             TO_CHAR(DEPRECIAR.PERIODO, 'YYYY'), 
                             TO_CHAR(DEPRECIAR.PERIODO, 'MM'  ),
                             DEPRECIAR.REFERENCIA,
                             CASE WHEN MI_MANEJACENTRO = 'SI' THEN DEPRECIAR.CENTRO_COSTO ELSE '99999999999999999999' END,
                             SUBSTR(DEPRECIAR.ELEMENTO,1, MI_AGRUPACION)
                    HAVING SUM(DEPRECIAR.NIIF_VLRDEPRECIACION )>0
                    ORDER BY TO_CHAR(DEPRECIAR.PERIODO, 'YYYY'), 
                             TO_CHAR(DEPRECIAR.PERIODO, 'MM') 
        ) LOOP
            MI_DATOS :=1;
            IF MI_MANEJACENTRO = 'SI' THEN
                MI_MENSAJECENTRO := ', para el Centro de costo ' || RS.CENTRO_COSTO;
            ELSE
                MI_MENSAJECENTRO := ' ';
            END IF;
            IF RS.CUENTADEBITO IS NULL THEN
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('El grupo ' || RS.GRUPO || MI_MENSAJECENTRO || '; no ha sido configurado, en el debito. El valor dejado de reportar es ' || RS.VLRDEPRECIACION1 ||  CHR(13) || CHR(10));
            ELSIF RS.DEBITOPLAN IS NULL THEN
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('El grupo ' || RS.GRUPO || MI_MENSAJECENTRO || '; la cuenta debito no es valida en el plan contable. El valor dejado de reportar es ' || RS.VLRDEPRECIACION1 ||  CHR(13) || CHR(10));
            END IF;
            IF RS.CUENTACREDITO IS NULL THEN
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('El grupo ' || RS.GRUPO || MI_MENSAJECENTRO || '; no ha sido configurado, en el credito. El valor dejado de reportar es ' || RS.VLRDEPRECIACION1 ||  CHR(13) || CHR(10));
            ELSIF RS.CREDITOPLAN IS NULL THEN
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('El grupo ' || RS.GRUPO || MI_MENSAJECENTRO || '; la cuenta credito no es valida en el plan contable. El valor dejado de reportar es ' || RS.VLRDEPRECIACION1 ||  CHR(13) || CHR(10));
            END IF;
            IF RS.DEBITOPLAN IS NOT NULL THEN
                MI_CUENTA      := RS.DEBITOPLAN;
                MI_NATURALEZA  := RS.DEBITONATU;
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                MI_VALORES :=' '''|| UN_COMPANIADESTINO ||'''
                                ,'|| MI_ANO ||'
                              ,'''|| UN_TIPO ||'''
                                ,'|| UN_NUMERO ||'
                                ,'|| MI_CONSECUTIVO ||'
                              ,'''|| MI_CUENTA || '''
                               ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                               ,'''|| MI_NATURALEZA ||'''
                               ,'''|| RS.VLRDEPRECIACION1 ||'''
                               ,'''|| 0 ||'''
                               ,'''|| RS.VLRDEPRECIACION1 ||'''
                               ,'''|| 0 ||'''
                               ,'''||MI_TERCERO||'''
                               ,'''||MI_SUCURSAL||'''
                               ,'''||CASE WHEN MI_TERCEROUNICO = 'SI' AND MI_CENTROCOSTO IS NOT NULL THEN MI_CENTROCOSTO ELSE RS.CENTRO_COSTO END||'''
                               ,'''|| RS.REFERENCIA || '''';

                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            END IF;
            IF RS.CREDITOPLAN IS NOT NULL THEN
                MI_CUENTA      := RS.CREDITOPLAN;
                MI_NATURALEZA  := RS.CREDITONATU;
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                MI_VALORES :=' '''|| UN_COMPANIADESTINO ||'''
                                ,'|| MI_ANO ||'
                              ,'''|| UN_TIPO ||'''
                                ,'|| UN_NUMERO ||'
                                ,'|| MI_CONSECUTIVO ||'
                              ,'''|| MI_CUENTA || '''
                               ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                               ,'''|| MI_NATURALEZA ||'''
                               ,'''|| 0 ||'''
                               ,'''|| RS.VLRDEPRECIACION1 ||'''
                               ,'''|| 0 ||'''
                               ,'''|| RS.VLRDEPRECIACION1 ||'''
                               ,'''||MI_TERCERO||'''
                               ,'''||MI_SUCURSAL||'''
                               ,'''||CASE WHEN MI_TERCEROUNICO = 'SI' AND MI_CENTROCOSTO IS NOT NULL THEN MI_CENTROCOSTO ELSE RS.CENTRO_COSTO END||'''
                               ,'''|| RS.REFERENCIA || '''';

                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            END IF;            
        END LOOP;        
        IF MI_DATOS = 0 THEN
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('No existen depreciación para el mes de ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES) || CHR(13) || CHR(10));
        ELSIF MI_CONSECUTIVO = 0 THEN
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('Existen depreciación en el mes de ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES) || ', pero no son validas las configuraciones.' || CHR(13) || CHR(10));
        ELSE        
            MI_STRTEXTO := 'INTERFACE DE ALMACEN A CONTABILIDAD DEL MES:'|| UN_FECHINTERF || '';
            MI_RTAPLANO:=  MI_RTAPLANO||TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR(  UN_COMPANIA         => UN_COMPANIADESTINO
                                                                                  ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                                                  ,UN_NUMERO           => UN_NUMERO 
                                                                                  ,UN_ANO              => MI_ANO
                                                                                  ,UN_FECHA            => UN_FECHINTERF
                                                                                  ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                                                  ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                                                  ,UN_DESCRIPCION      => MI_STRTEXTO
                                                                                  ,UN_USUARIO          => UN_USUARIO 
                                                                                  ,UN_SIMPLE           => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIADESTINO, 
                                                                                                                                              UN_NOMBRE    => 'SIMPLIFICAR INTERFACE ALMACEN AJUSTES' ,
                                                                                                                                              UN_MODULO    => 96, 
                                                                                                                                              UN_FECHA_PAR => SYSDATE ), 'SI') = 'SI' THEN -1 ELSE 0 END
                                                                                  ,UN_INDIMPRESION     => -1
                                                               )) ;

          --(INI_CC:3105 Registra el comprobante y el numero al realizar la contabilizacion)
          MI_TABLA := 'DEPRECIAR';

          MI_CAMPOS := 'TIPO_CPTE_CONTABLE = CASE 
                              WHEN TIPO_CPTE_CONTABLE IS NULL THEN '''|| UN_TIPO ||''' 
                              ELSE TIPO_CPTE_CONTABLE 
                          END
                         ,CPTE_CONTABLE = CASE 
                              WHEN CPTE_CONTABLE IS NULL THEN '|| UN_NUMERO ||' 
                              ELSE CPTE_CONTABLE 
                          END
                          ,CONSECUTIVO_PROCESO = CONSECUTIVO_PROCESO + 1';
            
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIAORIGEN || '''
                            AND PERIODO = TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')';
                
            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
                
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;--(FIN_CC:3105)

        END IF;        
        RETURN MI_RTAPLANO;
   END FC_AJUSTESNIVELESNIIF;   

 --2
 FUNCTION FC_CONTABILIZRALMCNRTRACTIVO 
/* 
        NAME              : En Access InterfaceAlmacenRetiroActivos
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE CACERES ALVAREZ
        DATE MIGRADOR     : 19/04/2018
        TIME              : 09:27 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :
  @NAME:
  @METHOD:
*/
  (
      UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
     ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
     ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
     ,UN_FECHINTERF    IN  DATE 
     ,UN_TIPO          IN  VARCHAR2     
     ,UN_NUMERO        IN  NUMBER  
     ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
  )

RETURN PCK_SUBTIPOS.TI_LOGICO

AS  
   MI_AGRUPACION                   NUMBER;  --??
   MI_ALMDEP                       PCK_SUBTIPOS.TI_LOGICO;
   MI_FECHAINTERF                  DATE;  -- PARA FECHA INTERFAZ 
   MI_NATURALEZA                   PCK_SUBTIPOS.TI_NATURALEZACONTA;
   MI_STRTEXTO                     COMPROBANTE_CNT.DESCRIPCION%TYPE;
   MI_PRUEBA                       PCK_SUBTIPOS.TI_LOGICO;
   MI_ENTRADA                      PCK_SUBTIPOS.TI_LOGICO;           -- variable que almacena un valor si la consulta retorna o no registros  

   MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
   MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
   MI_FILAS                        PCK_SUBTIPOS.TI_ENTERO;
   MI_CONDICIONACME                PCK_SUBTIPOS.TI_CONDICION;
   --
   MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
   MI_RESPUESTA                    PCK_SUBTIPOS.TI_LOGICO;
   MI_RETORNO                      CLOB;

--   MI_RS                           SYS_REFCURSOR;
--   MI_RS2                          SYS_REFCURSOR;
   MI_CONDICION                    BOOLEAN DEFAULT FALSE;
   MI_VALOR                        NUMBER(1);
   MI_INSERTAR                     BOOLEAN DEFAULT FALSE;


BEGIN
           MI_AGRUPACION    :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                        UN_NOMBRE    => 'DIGITOS AGRUPACION INVENTARIO' ,
                                                        UN_MODULO    => 10, 
                                                        UN_FECHA_PAR => SYSDATE ), 'NO');  
           MI_ALMDEP        := CASE
                               WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                              UN_NOMBRE    => 'INTERFAZ DE ALMACEN CON DEPENDENCIA' ,
                                                              UN_MODULO    => 96, 
                                                              UN_FECHA_PAR => SYSDATE ), 'NO')='NO'
                               THEN 0
                               ELSE -1
                               END;
           MI_CONSECUTIVO   := 1;
           MI_FECHAINTERF := TO_CHAR(UN_FECHINTERF,'DD/MM/YYYY'); 
           MI_VALOR:=0;

           BEGIN

           MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''
                                 AND ANO = '|| UN_ANO ||'
                           AND TIPO_CPTE = '''|| UN_TIPO ||'''
                         AND COMPROBANTE = '|| UN_NUMERO ||'  ';

           MI_FILAS := PCK_DATOS.FC_ACME
                       (UN_TABLA     => 'TEMP_PLANA_AJUSTES'
                       ,UN_ACCION    => 'E'
                       ,UN_CONDICION => MI_CONDICIONACME);
           END;


       BEGIN

                MI_TABLA := 'TEMP_PLANA_AJUSTES';
                MI_CAMPOS := 'COMPANIA
                              ,ANO
                              ,TIPO_CPTE
                              ,COMPROBANTE
                              ,CONSECUTIVO
                              ,CUENTA
                              ,FECHA
                              ,NATURALEZA
                              ,VALOR_DEBITO
                              ,VALOR_CREDITO
                              ,EJECUCION_DEBITO
                              ,EJECUCION_CREDITO
                              ,CENTRO_COSTO
                              ,TERCERO
                              ,SUCURSAL
                              ,AUXILIAR
                              '||CASE WHEN MI_ALMDEP <>0 THEN ',D_DEPENDENCIACNT' ELSE '' END||' '; 

           MI_ENTRADA:= 0;
           FOR MI_RS IN( SELECT  DISTINCT SUBSTR(ELEMENTO,1,MI_AGRUPACION) GRUPO
                           FROM  D_MOVIMIENTO  
			                         INNER JOIN TIPOMOVIMIENTO 
				                               ON D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                                      AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                          WHERE D_MOVIMIENTO .COMPANIA=UN_COMPANIA
                            AND TO_CHAR((FECHA),'YYYY')=UN_ANO
                            AND TO_CHAR((FECHA),'MM')=UN_MES  
                            AND (TIPOMOVIMIENTO.CONCEPTO IN ('CM','DS','L','DT','T') AND TIPOMOVIMIENTO.CLASE NOT IN ('E') 
                            OR ( TIPOMOVIMIENTO.CLASE NOT IN ( 'T','E' ) AND TIPOMOVIMIENTO.CONCEPTO NOT IN ( 'T','II','N' ) ))
                          )          
               LOOP 
               MI_ENTRADA:= -1;    
               IF MI_ENTRADA <>0  THEN 
                  MI_ENTRADA:= 0;
                      FOR MI_RS2 IN(SELECT D_MOVIMIENTO.COMPANIA,
                                          D_MOVIMIENTO.TIPOMOVIMIENTO, 
	                                       	SUBSTR(D_MOVIMIENTO.ELEMENTO,1,MI_AGRUPACION)  GRUPO, 
                                          TO_CHAR((FECHA),'MM') MES,
                                          TO_CHAR((FECHA),'YYYY') ANO,
                                          SUM(D_MOVIMIENTO.VALORTOTAL)  VALORTOTAL, 
	                                       	SUM(DEPRECIAR.DEPACUMULADA)  DEPACUMULADA, 
	                                        SUM(DEPRECIAR.VLRLIBROS)  VLRLIBROS, 
	                                        ALMACENCONTABILIDAD.CODIGOELEMENTO, 
	                                        ALMACENCONTABILIDAD.DEBITO_HISTORICO_BAJA, 
	                                        ALMACENCONTABILIDAD.CREDITO_HISTORICO_BAJA, 
	                                        ALMACENCONTABILIDAD.DEBITO_ACUMULADA_BAJA, 
	                                        ALMACENCONTABILIDAD.CREDITO_ACUMULADA_BAJA, 
	                                        ALMACENCONTABILIDAD.DEBITO_LIBROS_BAJA, 
	                                        ALMACENCONTABILIDAD.CREDITO_LIBROS_BAJA, 
	                                        ALMACENCONTABILIDAD.DEBITO_RETIRADOS_BAJA, 
	                                        ALMACENCONTABILIDAD.CREDITO_RETIRADOS_BAJA 

                                 FROM DEPRECIAR 
                                        INNER JOIN D_MOVIMIENTO 
                                		         ON DEPRECIAR.COMPANIA = D_MOVIMIENTO.COMPANIA 
                                            AND DEPRECIAR.ELEMENTO = D_MOVIMIENTO.ELEMENTO 
                                			    	AND DEPRECIAR.SERIE = D_MOVIMIENTO.SERIE 
                                				INNER JOIN TIPOMOVIMIENTO 
                                				        ON D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                                					     AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO 
                                					   INNER JOIN ALMACENCONTABILIDAD 
                                					           ON TIPOMOVIMIENTO.COMPANIA = ALMACENCONTABILIDAD.COMPANIA
                                						    	  AND TIPOMOVIMIENTO.CODIGO = ALMACENCONTABILIDAD.TIPOMOVIMIENTO 
                                  WHERE      ((TIPOMOVIMIENTO.CONCEPTO IN ('CM','DS','L','DT') AND TIPOMOVIMIENTO.CLASE NOT IN ('E') OR ( TIPOMOVIMIENTO.CLASE NOT IN ( 'T','E','II','N' ) AND TIPOMOVIMIENTO.CONCEPTO NOT IN ( 'T' ) )) AND DEPRECIAR.PERIODO =MI_FECHAINTERF AND ALMACENCONTABILIDAD.ANO =UN_ANO)

                                             OR (D_MOVIMIENTO.COMPANIA=UN_COMPANIA AND SUBSTR(D_MOVIMIENTO.ELEMENTO,1, MI_AGRUPACION)= MI_RS.GRUPO AND TO_CHAR((FECHA),'MM')=UN_MES AND TO_CHAR((FECHA),'YYYY')=UN_ANO AND ALMACENCONTABILIDAD.CODIGOELEMENTO =MI_RS.GRUPO 
                                                 AND (ALMACENCONTABILIDAD.DEBITO_HISTORICO_BAJA IS NOT NULL   OR ALMACENCONTABILIDAD.CREDITO_HISTORICO_BAJA IS NOT NULL     OR ALMACENCONTABILIDAD.DEBITO_ACUMULADA_BAJA IS NOT NULL
                                                  OR ALMACENCONTABILIDAD.CREDITO_ACUMULADA_BAJA IS NOT NULL   OR ALMACENCONTABILIDAD.DEBITO_LIBROS_BAJA IS NOT NULL        OR ALMACENCONTABILIDAD.CREDITO_LIBROS_BAJA IS NOT NULL 
                                                  OR ALMACENCONTABILIDAD.DEBITO_RETIRADOS_BAJA IS NOT NULL    OR ALMACENCONTABILIDAD.CREDITO_RETIRADOS_BAJA IS NOT NULL)) 

                               GROUP BY    D_MOVIMIENTO.COMPANIA, 
                                           D_MOVIMIENTO.TIPOMOVIMIENTO, 
	                     	                   SUBSTR(D_MOVIMIENTO.ELEMENTO,1,MI_AGRUPACION ), 
                                           TO_CHAR((FECHA),'MM'),
                                           TO_CHAR((FECHA),'YYYY'),
	                     	                   ALMACENCONTABILIDAD.CODIGOELEMENTO, 
                                           ALMACENCONTABILIDAD.DEBITO_HISTORICO_BAJA, 
	                                         ALMACENCONTABILIDAD.CREDITO_HISTORICO_BAJA, 
	                                         ALMACENCONTABILIDAD.DEBITO_ACUMULADA_BAJA, 
	                                         ALMACENCONTABILIDAD.CREDITO_ACUMULADA_BAJA, 
	                                         ALMACENCONTABILIDAD.DEBITO_LIBROS_BAJA, 
	                                         ALMACENCONTABILIDAD.CREDITO_LIBROS_BAJA, 
	                                         ALMACENCONTABILIDAD.DEBITO_RETIRADOS_BAJA, 
	                                         ALMACENCONTABILIDAD.CREDITO_RETIRADOS_BAJA )
                                LOOP 
            -- CONDICION teniendo en cuentra la condicion del CASE WHEN realice la insercion 
        --señor hace falta validar en un if si el campo de la insercion es vacio o no ?? 
               IF  MI_ENTRADA <>0  THEN 
                IF MI_RS2.DEBITO_HISTORICO_BAJA <> '' THEN 
                     MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEBITO_HISTORICO_BAJA);

                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.DEBITO_HISTORICO_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF; 
 --------primera parte, insercion1

                IF MI_RS2.CREDITO_HISTORICO_BAJA <> '' THEN   

                      MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.CREDITO_HISTORICO_BAJA);


                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.CREDITO_HISTORICO_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';


                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF ;    

 ------ segunda parte, insercion 2 

                IF MI_RS2.DEBITO_ACUMULADA_BAJA <> '' THEN 

                      MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEBITO_ACUMULADA_BAJA);


                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.DEBITO_ACUMULADA_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS2.DEPACUMULADA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.DEPACUMULADA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF ;
  ------ tercera parte, insercion 3  

                IF MI_RS2.CREDITO_ACUMULADA_BAJA <> '' THEN  

                      MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.CREDITO_ACUMULADA_BAJA);


                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.CREDITO_ACUMULADA_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.DEPACUMULADA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.DEPACUMULADA ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';


                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF;
  ------ cuarta parte, insercion 4                    

                IF MI_RS2.DEBITO_LIBROS_BAJA <> '' THEN     

                    MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEBITO_LIBROS_BAJA);

                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.DEBITO_LIBROS_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS2.VLRLIBROS ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VLRLIBROS ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF ;
  ------ quinta parte, insercion 5 

                IF MI_RS2.CREDITO_LIBROS_BAJA <> '' THEN  

                    MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.CREDITO_LIBROS_BAJA);


                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.CREDITO_LIBROS_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VLRLIBROS ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VLRLIBROS ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';


                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                END IF;
  ------ sexta parte, insercion 6  

                IF MI_RS2.DEBITO_RETIRADOS_BAJA <> '' THEN     

                    MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEBITO_RETIRADOS_BAJA);


                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.DEBITO_RETIRADOS_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                --    MI_INSERTAR := TRUE;
                END IF;
  ------ septima parte, insercion 7  

                IF MI_RS2.CREDITO_RETIRADOS_BAJA <> '' THEN     

                    MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.CREDITO_RETIRADOS_BAJA);

                     MI_VALORES :=   ' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.CREDITO_RETIRADOS_BAJA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS2.VALORTOTAL ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                  END IF ;
                END IF ;  
               END LOOP;

               END IF ;  

           END LOOP;

                    BEGIN

                    MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''
                                          AND ANO = '|| UN_ANO ||'
                                    AND TIPO_CPTE = '''|| UN_TIPO ||'''
                                  AND COMPROBANTE = '|| UN_NUMERO ||'  ';

                    MI_FILAS := PCK_DATOS.FC_ACME
                                (UN_TABLA     => 'TEMP_PLANA_AJUSTES'
                                ,UN_ACCION    => 'E'
                                ,UN_CONDICION => MI_CONDICIONACME);
                    END;

                     MI_STRTEXTO := 'INTERFACE DE ALMACEN A CONTABILIDAD DEL MES: ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES);

                 --se evalua el parametro y posteriormente se hace una consulta a la funcion FC_CONTABILIZAR
                IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                             UN_NOMBRE    => 'SIMPLIFICAR INTERFACE ALMACEN' ,
                                             UN_MODULO    => 96, 
                                             UN_FECHA_PAR => SYSDATE ), 'SI') = 'SI' THEN 
                   MI_RETORNO:= PCK_CONTABILIZAR.FC_CONTABILIZAR(  UN_COMPANIA         => UN_COMPANIA
                                                                    ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                                    ,UN_NUMERO           => UN_NUMERO 
                                                                    ,UN_ANO              => UN_ANO
                                                                    ,UN_FECHA            => UN_FECHINTERF
                                                                    ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                                    ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                                    ,UN_DESCRIPCION      => MI_STRTEXTO
                                                                   -- DB
                                                                    ,UN_USUARIO          => UN_USUARIO 
                                                                    ,UN_SIMPLE           => -1
                                                                    ,UN_INDIMPRESION     => -1
                                                                    ,UN_RESAUXGEN        => MI_ALMDEP
                                                                    ) ;
                   MI_RESPUESTA:=-1;
                ELSE                                                         
                    MI_RETORNO:= PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                                                    ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                                    ,UN_NUMERO           => UN_NUMERO 
                                                                    ,UN_ANO              => UN_ANO
                                                                    ,UN_FECHA            => UN_FECHINTERF
                                                                    ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                                    ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                                    ,UN_DESCRIPCION      => MI_STRTEXTO
                                                                   -- DB
                                                                    ,UN_USUARIO          => UN_USUARIO 
                                                                    ,UN_SIMPLE           => 0
                                                                    ,UN_INDIMPRESION     => -1
                                                                    ,UN_RESAUXGEN        => MI_ALMDEP
                                                                    ) ;
                   MI_RESPUESTA:=0;        

                END IF ;
        END;  

  RETURN  MI_RESPUESTA; 

END FC_CONTABILIZRALMCNRTRACTIVO;

 --3
 FUNCTION FC_CONTABILIZRALMACAJUSTNIVELS  
/*
        NAME              : En Access InterfaceAlmacenAjustesNiveles
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE CACERES
        DATE MIGRADOR     : 02/05/2018
        TIME              : 09:07 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :
  @NAME:
  @METHOD:
*/
   ( 
      UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
     ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
     ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
     ,UN_FECHINTERF    IN  DATE
     ,UN_TIPO          IN  VARCHAR2     
     ,UN_NUMERO        IN  NUMBER
     ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
   )

RETURN PCK_SUBTIPOS.TI_LOGICO

AS 
     MI_ERR1                         CLOB;
     MI_ERR4                         CLOB;
     MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;

     MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
     MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
     MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
     MI_FILAS                        PCK_SUBTIPOS.TI_ENTERO;
     MI_CONDICIONACME                PCK_SUBTIPOS.TI_CONDICION;

     MI_RS                           SYS_REFCURSOR;
     MI_CONT_INTE                    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
     MI_RS2                          SYS_REFCURSOR;
     MI_FUNCION                      PCK_SUBTIPOS.TI_LOGICO;
     MI_ENTRADA                      PCK_SUBTIPOS.TI_LOGICO;           -- variable que almacena un valor si la consulta retorna o no registros  
     MI_REGISTROS                    NUMBER;                           -- variable utilizada en el INTO

     MI_VALOR                        NUMBER(1);
     MI_NATURALEZA                   PCK_SUBTIPOS.TI_NATURALEZACONTA;
     MI_ANOANTERIOR                  PCK_SUBTIPOS.TI_ANIO;
     MI_ANO                          PCK_SUBTIPOS.TI_ANIO;
     MI_MESANTERIOR                  PCK_SUBTIPOS.TI_MES;
     MI_MES                          PCK_SUBTIPOS.TI_MES;
     MI_STRBODEGA                    NUMBER;
     MI_STRINSERVIBLES               NUMBER;
     MI_STRRESPONSABILIDADES         NUMBER;


BEGIN
          -- FALTA DEFINIR MAS MENSAJES PARA LLENAR EL CLOB  
  MI_ERR4 := TO_CLOB('INFOME DE ADVERTENCIAS INTERFACE ALMACÉN A CONTABILIDAD'|| CHR(13) || CHR(10));
  MI_ERR4 := MI_ERR4 || TO_CLOB('FECHA: '||TO_CHAR(SYSDATE) || CHR(13) || CHR(10));   
  MI_ERR4 := MI_ERR4 || TO_CLOB ('USUARIO: '||UN_USUARIO||CHR(13) || CHR(10));


  MI_CONSECUTIVO           := 1;
  MI_VALOR                 := 0;  -- variable que se le asigna un valor para remplazar un campo en la insercion  

  MI_STRBODEGA             := 000000000000; --variable de bodega
  MI_STRINSERVIBLES        := 999999999999;  --variable de inservibles 
  MI_STRRESPONSABILIDADES  := 999999999902;  --variable de responsabilidades 


         BEGIN                                                      --  delete a TEMP_PLANA_AJUSTES
           MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''  
                                 AND ANO = '|| UN_ANO ||'
                           AND TIPO_CPTE = '''|| UN_TIPO ||'''
                         AND COMPROBANTE = '|| UN_NUMERO ||'  ';

           MI_FILAS := PCK_DATOS.FC_ACME
                       (UN_TABLA     => 'TEMP_PLANA_AJUSTES'    --- VERIFICAR TABLA PlanaInterfaceAlm
                       ,UN_ACCION    => 'E'
                       ,UN_CONDICION => MI_CONDICIONACME);
         END;

                MI_TABLA := 'TEMP_PLANA_AJUSTES'; --- VERIFICAR TABLA PlanaInterfaceAlm
                MI_CAMPOS := ' COMPANIA
                              ,ANO
                              ,TIPO_CPTE
                              ,COMPROBANTE
                              ,CONSECUTIVO
                              ,CUENTA
                              ,FECHA
                              ,NATURALEZA
                              ,VALOR_DEBITO
                              ,VALOR_CREDITO
                              ,EJECUCION_DEBITO
                              ,EJECUCION_CREDITO
                              ,CENTRO_COSTO
                              ,TERCERO
                              ,SUCURSAL
                              ,AUXILIAR '; 
        BEGIN
          MI_ENTRADA :=0 ;
          FOR MI_RS IN(SELECT ACUMULADO.COMPANIA, 
                              ACUMULADO.CODIGOELEMENTO,
                              ACUMULADO.ANO, 
                              ACUMULADO.MES,
                              ACUMULADO.COSTOSALIDA,
                              AJUSTEAPPMES,
                              AJUSINFLA,
                              AJUSDEPREC,
                              AJUSTEDEPRECIACION,
                              COSTOSALIDAAJ,
                              DEPREC,
                              DEPACUMULADADC,
                              INVENTARIO.TIPO,
                              VLRDEPRECIACIONAJUS,
                              VLRDEPRECIACION,
                              VLRDEPRECIACIONCOMODATO,
                              VLRCUANTIAMIN
                         FROM ACUMULADO 
                              LEFT JOIN INVENTARIO
                                     ON  ACUMULADO.COMPANIA = INVENTARIO.COMPANIA
                                    AND  ACUMULADO.CODIGOELEMENTO = INVENTARIO.CODIGOELEMENTO
                        WHERE   ACUMULADO.COMPANIA=UN_COMPANIA
                        	AND ACUMULADO.ANO=UN_ANO
                        	AND ACUMULADO.MES =UN_MES
                            AND INVENTARIO.TIPO<>'C' 
                         ORDER BY ACUMULADO.COMPANIA, 
                                  ACUMULADO.CODIGOELEMENTO, 
                        		      ACUMULADO.ANO, 
                        		      ACUMULADO.MES,
                                  ACUMULADO.COSTOSALIDA)

          LOOP
          MI_ENTRADA := -1 ;

          IF  MI_ENTRADA <>-1 THEN  
              MI_ERR4 := MI_ERR4 || TO_CLOB ('No existen movimientos acumulados en el mes de '||' '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES)|| CHR(13) || CHR(10));
              MI_CONT_INTE := MI_CONT_INTE + 1;

              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;  ---- VERIFICAR EL RISE PARA FINALIZAR EL FLUJO DE LA FUNCION 

              MI_FUNCION:=0;
          END IF; 
           ----VERIFICAR CAMPOS A USAR DE AQUI EN ADELANTE
          MI_ENTRADA := 0 ; 
          FOR MI_RS2 IN (SELECT AJUSINFLADEBITO,
                                AJUSINFLACREDITO,
                                AJUSDEPRECDEBITO,
                                AJUSDEPRECCREDITO,
                                AJUSINFLADEBITOS,
                                AJUSDEPRECDEBITOS,
                                AJUSTEDEPRECIACIONCR,
                                AJUSTEDEPRECIACIONDB,
                                AJUSINFLACREDITOS,
                                AJUSDEPRECCREDITOS,
                                CODIGOELEMENTO, 
                                COSTOSALDB,
                                COSTOSALCR,
                                COSTOSALAJDB,
                                COSTOSALAJCR,
                                DEPRECDEBITOS,
                                DEPRECCREDITOS,
                                DEPRECDEBITOSCOMODATO,
                                DEPCUANTIAMIN_DEB,
                                DEPRECCREDITOSCOMODATO,
                                DEPCUANTIAMIN_CRE,
                                DEPACUMULADADB,
                                DEPACUMULADACR
                           FROM INVENTARIOCONTABILIDAD 
                          WHERE COMPANIA=UN_COMPANIA
                            AND LENGTH(INVENTARIOCONTABILIDAD.CODIGOELEMENTO) <= LENGTH(MI_RS.CODIGOELEMENTO) 
                            AND INVENTARIOCONTABILIDAD.CODIGOELEMENTO = SUBSTR( MI_RS.CODIGOELEMENTO ,1,LENGTH(INVENTARIOCONTABILIDAD.CODIGOELEMENTO)) 
                            AND ANO=UN_ANO
                       ORDER BY COMPANIA,CODIGOELEMENTO DESC ,ANO) 

          LOOP
          MI_ENTRADA := -1; 
          MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.COSTOSALDB);

          IF MI_ENTRADA <> 0 THEN                         -- Aquí se toma el costo de salida   
                    IF NVL(MI_RS.COSTOSALIDA,0)> 0
                       AND NVL(MI_RS2.COSTOSALDB,'')<>''
                       AND NVL(MI_RS2.COSTOSALCR,'')<>'' THEN 
                                                           --aquí inserta los detalles del comprobante(cuentadebitar)
                       MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.COSTOSALDB || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS.COSTOSALIDA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS.COSTOSALIDA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                        BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;

                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

              --- insercion 2 primera parte
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.COSTOSALCR);
                                                                                   -- aquí inserta los detalles del comprobante(cuentaacreditar)
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.COSTOSALCR || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.COSTOSALIDA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.COSTOSALIDA ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                      BEGIN
                      MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                    UN_ACCION  => 'I',
                                                    UN_CAMPOS  => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORES);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                      END;

                      MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                   -- se almacenan inconsistencias 
                    ELSIF  NVL(MI_RS.COSTOSALIDA,0)> 0
                                 AND (NVL(MI_RS2.COSTOSALDB,'')=''
                                  OR NVL(MI_RS2.COSTOSALCR,'')='') THEN 
                                  MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en costo salida: '|| MI_RS.COSTOSALIDA ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.COSTOSALDB,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.COSTOSALCR,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE := MI_CONT_INTE + '1';
                    END IF;

                    MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.COSTOSALAJDB);
                    -- Aquí se toma el costo de Salida Ajustada
                    IF NVL(MI_RS.COSTOSALIDAAJ,0)> 0
                       AND NVL(MI_RS2.COSTOSALAJDB,'')<>''
                       AND NVL(MI_RS2.COSTOSALAJCR,'')<>'' THEN 
                       --aquí inserta los detalles del comprobante(cuentadebitar)
                       MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.COSTOSALAJDB || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS.COSTOSALIDAAJ ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS.COSTOSALIDAAJ ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                        BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;

                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

              --- insercion 2 SEGUNDA parte
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.COSTOSALAJCR);
                        --aquí inserta los detalles del comprobante(cuentaacreditar)
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.COSTOSALAJCR || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.COSTOSALIDAAJ ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.COSTOSALIDAAJ ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                      BEGIN
                      MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                    UN_ACCION  => 'I',
                                                    UN_CAMPOS  => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORES);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                      END;

                      MI_CONSECUTIVO:= MI_CONSECUTIVO + 1; 

                    ELSIF  NVL(MI_RS.COSTOSALIDAAJ,0)> 0
                                 AND (NVL(MI_RS2.COSTOSALAJDB,'')=''
                                  OR NVL(MI_RS2.COSTOSALAJCR,'')='') THEN 
                                  MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en costo salida: '|| MI_RS.COSTOSALIDAAJ ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.COSTOSALAJDB,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.COSTOSALAJCR,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                    END IF;        

              -- 3 Aquí se toma el Ajuste por inflación del Mes             
                    IF NVL(MI_RS.AJUSTEAPPMES,0)> 0
                         AND NVL(MI_RS2.AJUSINFLADEBITO,'')<>''
                         AND NVL(MI_RS2.AJUSINFLACREDITO,'')<>'' THEN 

                         MI_ERR1:=TO_CLOB('Tomando el ajuste por inflación del Mes, Insertando en PlanaInterfaceAlm - Cuenta Debitar, Elementos:  '||' '||MI_RS.CODIGOELEMENTO|| CHR(13) || CHR(10));
                         MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITO);
                         --aquí inserta los detalles del comprobante(cuentadebitar)
                         MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.AJUSINFLADEBITO || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_RS.AJUSTEAPPMES ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS.AJUSTEAPPMES ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                        BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;

                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;  

                        -- tercera parte insercion 2
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLACREDITO);
                        -- aquí inserta los detalles del comprobante(cuentaacreditar)
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                    ,'|| UN_ANO ||'
                                    ,'''|| UN_TIPO ||'''
                                    ,'|| UN_NUMERO ||'
                                    ,'|| MI_CONSECUTIVO ||'
                                    ,'''|| MI_RS2.AJUSINFLACREDITO || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                    ,'''|| MI_NATURALEZA ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS.AJUSTEAPPMES ||'''
                                    ,'''|| MI_VALOR ||'''
                                    ,'''|| MI_RS.AJUSTEAPPMES ||'''
                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                        BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;

                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                        ELSIF  NVL(MI_RS.AJUSTEAPPMES,0)> 0
                               AND (NVL(MI_RS2.AJUSINFLADEBITO,'')=''
                                OR NVL(MI_RS2.AJUSINFLACREDITO,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en ajuste por inflación: '|| MI_RS.AJUSTEAPPMES ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.AJUSINFLADEBITO,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.AJUSINFLACREDITO,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + 1;
                        END IF;         

                      --4. Aquí se toma el Valor de la depreciacion ajustada  

                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSDEPRECDEBITO);

                        IF NVL(MI_RS.VLRDEPRECIACIONAJUS,0)> 0
                           AND NVL(MI_RS2.AJUSDEPRECDEBITO,'')<>''
                           AND NVL(MI_RS2.AJUSDEPRECCREDITO,'')<>'' THEN 
                           -- aquí inserta los detalles del comprobante(cuentadebitar)
                           MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.AJUSDEPRECDEBITO || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONAJUS ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONAJUS ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 4 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSDEPRECCREDITO);

                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.AJUSDEPRECCREDITO || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONAJUS ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONAJUS ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.VLRDEPRECIACIONAJUS,0)> 0
                               AND (NVL(MI_RS2.AJUSDEPRECDEBITO,'')=''
                                OR NVL(MI_RS2.AJUSDEPRECCREDITO,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación ajustada: '|| MI_RS.VLRDEPRECIACIONAJUS ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.AJUSDEPRECDEBITO,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.AJUSDEPRECCREDITO,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                            -- 5 . Aquí se toma el Valor de la depreciacion del mes
                             MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECDEBITOS);

                            IF NVL(MI_RS.VLRDEPRECIACION,0)> 0
                               AND NVL(MI_RS2.DEPRECDEBITOS,'')<>''
                               AND NVL(MI_RS2.DEPRECCREDITOS,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.DEPRECDEBITOS || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.VLRDEPRECIACION ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.VLRDEPRECIACION ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 5 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECCREDITOS);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.DEPRECCREDITOS || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACION ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACION ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.VLRDEPRECIACION,0)> 0
                               AND (NVL(MI_RS2.DEPRECDEBITOS,'')=''
                                OR NVL(MI_RS2.DEPRECCREDITOS,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación mensual: '|| MI_RS.VLRDEPRECIACION ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.DEPRECDEBITOS,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.DEPRECCREDITOS,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                          --5-A Depreciacion de bienes dados en comodato   
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECDEBITOSCOMODATO);

                            IF NVL(MI_RS.VLRDEPRECIACIONCOMODATO,0)> 0
                               AND NVL(MI_RS2.DEPRECDEBITOSCOMODATO,'')<>''
                               AND NVL(MI_RS2.DEPRECCREDITOSCOMODATO,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.DEPRECDEBITOSCOMODATO || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.VLRDEPRECIACIONCOMODATO ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.VLRDEPRECIACIONCOMODATO ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 5-A insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECCREDITOSCOMODATO);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.DEPRECCREDITOSCOMODATO || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONCOMODATO ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRDEPRECIACIONCOMODATO ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.VLRDEPRECIACIONCOMODATO,0)> 0
                               AND (NVL(MI_RS2.DEPRECDEBITOSCOMODATO,'')=''
                                OR NVL(MI_RS2.DEPRECCREDITOSCOMODATO,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación de bienes en comodato: '|| MI_RS.VLRDEPRECIACIONCOMODATO ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.DEPRECDEBITOSCOMODATO,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.DEPRECCREDITOSCOMODATO,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                           -- 5.B Aquí se toma el Valor de la depreciacion cuando aplica cuantia minima
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPCUANTIAMIN_DEB);

                            IF NVL(MI_RS.VLRCUANTIAMIN,'0')> '0'
                               AND NVL(MI_RS2.DEPCUANTIAMIN_DEB,'')<>''
                               AND NVL(MI_RS2.DEPCUANTIAMIN_CRE,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.DEPCUANTIAMIN_DEB || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.VLRCUANTIAMIN ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.VLRCUANTIAMIN ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';
                                            --- verificar en la insercion el campo centro de costo ya que en access posiblemente no sirve 

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 5-B insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPCUANTIAMIN_CRE);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.DEPCUANTIAMIN_CRE || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRCUANTIAMIN ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.VLRCUANTIAMIN ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';
                                        --- verificar en la insercion el campo centro de costo ya que en access posiblemente no sirve 

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.VLRCUANTIAMIN,0)> 0
                               AND (NVL(MI_RS2.DEPCUANTIAMIN_DEB,'')=''
                                OR NVL(MI_RS2.DEPCUANTIAMIN_CRE,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación cuando aplica cuantía mínima: '|| MI_RS.VLRCUANTIAMIN ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.DEPCUANTIAMIN_DEB,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.DEPCUANTIAMIN_CRE,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                            --6. Aquí se toma valor-> AjusInfla y cuentas->AjusInflaDebitoS, AjusInflaCreditoS

                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITOS);

                            IF NVL(MI_RS.AJUSINFLA,0)> 0
                               AND NVL(MI_RS2.AJUSINFLADEBITOS,'')<>''
                               AND NVL(MI_RS2.AJUSINFLACREDITOS,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.AJUSINFLADEBITOS || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.AJUSINFLA ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.AJUSINFLA ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 6 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLACREDITOS);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.AJUSINFLACREDITOS || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSINFLA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSINFLA ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.AJUSINFLA,0)> 0
                               AND (NVL(MI_RS2.AJUSINFLADEBITOS,'')=''
                                OR NVL(MI_RS2.AJUSINFLACREDITOS,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en ajuste por inflación: '|| MI_RS.VLRCUANTIAMIN ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.AJUSINFLADEBITOS,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.AJUSINFLACREDITOS,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + 1;
                            END IF;  

                      --   '7. Aquí se toma valor-> Deprec y cuentas->DeprecDebitoS, DeprecCreditoS

                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECDEBITOS);

                            IF NVL(MI_RS.DEPREC,0)> 0
                               AND NVL(MI_RS2.DEPRECDEBITOS,'')<>''
                               AND NVL(MI_RS2.DEPRECCREDITOS,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.DEPRECDEBITOS || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.DEPREC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.DEPREC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 7 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPRECCREDITOS);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.DEPRECCREDITOS || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.DEPREC ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.DEPREC ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.DEPREC,0)> 0
                               AND (NVL(MI_RS2.DEPRECDEBITOS,'')=''
                                OR NVL(MI_RS2.DEPRECCREDITOS,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación mensual: '|| MI_RS.DEPREC ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.DEPRECDEBITOS,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.DEPRECCREDITOS,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;   


                           -- '8. Aquí se toma valor-> AjusDeprec y cuentas->AjusDeprecDebitoS, AjusDeprecCreditoS
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSDEPRECDEBITOS);

                            IF NVL(MI_RS.AJUSDEPREC,0)> 0
                               AND NVL(MI_RS2.AJUSDEPRECDEBITOS,'')<>''
                               AND NVL(MI_RS2.AJUSDEPRECCREDITOS,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.AJUSDEPRECDEBITOS || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.AJUSDEPREC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.AJUSDEPREC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 8 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSDEPRECCREDITOS);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.AJUSDEPRECCREDITOS || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSDEPREC ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSDEPREC ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.AJUSDEPREC,0)> 0
                               AND (NVL(MI_RS2.AJUSDEPRECDEBITOS,'')=''
                                OR NVL(MI_RS2.AJUSDEPRECCREDITOS,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación ajustada: '|| MI_RS.AJUSDEPREC ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.AJUSDEPRECDEBITOS,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.AJUSDEPRECCREDITOS,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;


                            --9. Aquí se toma valor-> AjusteDepreciacion y cuentas->AjusteDepreciacionDb, AjusteDepreciacionCr
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSTEDEPRECIACIONDB);

                            IF NVL(MI_RS.AJUSTEDEPRECIACION,0)>0
                               AND NVL(MI_RS2.AJUSTEDEPRECIACIONDB,'')<>''
                               AND NVL(MI_RS2.AJUSTEDEPRECIACIONCR,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.AJUSTEDEPRECIACIONDB || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.AJUSTEDEPRECIACION ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.AJUSTEDEPRECIACION ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            -- parte 9 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSTEDEPRECIACIONCR);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.AJUSTEDEPRECIACIONCR || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSTEDEPRECIACION ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.AJUSTEDEPRECIACION ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.AJUSTEDEPRECIACION,0)> 0
                               AND (NVL(MI_RS2.AJUSTEDEPRECIACIONDB,'')=''
                                OR NVL(MI_RS2.AJUSTEDEPRECIACIONCR,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación ajustada: '|| MI_RS.AJUSTEDEPRECIACION ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.AJUSTEDEPRECIACIONDB,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.AJUSTEDEPRECIACIONCR,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                            --  '10. Aquí se toma valor-> DepAcumuladaDC y cuentas->DepAcumuladaDb, DepAcumuladaCr
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPACUMULADADB);

                            IF NVL(MI_RS.DEPACUMULADADC,0)> 0
                               AND NVL(MI_RS2.DEPACUMULADADB,'')<>''
                               AND NVL(MI_RS2.DEPACUMULADACR,'')<>'' THEN 

                               MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                            ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                            ,'''|| MI_RS2.DEPACUMULADADB || '''
                                            ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                            ,'''|| MI_NATURALEZA ||'''
                                            ,'''|| MI_RS.DEPACUMULADADC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| MI_RS.DEPACUMULADADC ||'''
                                            ,'''|| MI_VALOR ||'''
                                            ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                            ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                            ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                            ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              


                            -- parte 10 insercion 2 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.DEPACUMULADACR);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS2.DEPACUMULADACR || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.DEPACUMULADADC ||'''
                                        ,'''|| MI_VALOR ||'''
                                        ,'''|| MI_RS.DEPACUMULADADC ||'''
                                        ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                        ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                        ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                            ELSIF  NVL(MI_RS.DEPACUMULADADC,0)> 0
                               AND (NVL(MI_RS2.DEPACUMULADADB,'')=''
                                OR NVL(MI_RS2.DEPACUMULADACR,'')='') THEN 
                                MI_ERR4:= MI_ERR4||TO_CLOB('El elemento:' || MI_RS.CODIGOELEMENTO ||' '||'con valor en depreciación: '|| MI_RS.DEPACUMULADADC ||' '|| 
                                                             CASE WHEN NVL(MI_RS2.DEPACUMULADADB,'')= '' THEN  'no tiene cuenta débito configurada' ELSE '' END || 
                                                             CASE WHEN NVL(MI_RS2.DEPACUMULADACR,'')= '' THEN 'no tiene cuenta crédito configurada' ELSE '' END || CHR(13) || CHR(10) );          
                                  MI_CONT_INTE:= MI_CONT_INTE + '1';
                            END IF;

                END IF;             
     END LOOP;-- MI_RS2
     END LOOP;-- MI_RS

   END;

   BEGIN 

       IF MI_MES = 1 THEN
          MI_ANOANTERIOR := MI_ANO - 1;
          MI_MESANTERIOR := 12;
       ELSE 
          MI_ANOANTERIOR := MI_ANO ;
          MI_MESANTERIOR := MI_MES - 1;
       END IF ; 

       MI_ENTRADA:= 0;
       IF  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'MANEJO DE AJUSTES ENTRE BODEGAS' ,
                                     UN_MODULO    => 10, 
                                     UN_FECHA_PAR => SYSDATE ), 'NO')= 'SI' THEN
           FOR MI_RS IN ( SELECT INVENTARIO.TIPO,
                                 DEPRECIAR.COMPANIA, 
                                 DEPRECIAR.ELEMENTO,
                                 DEPRECIAR.SERIE,
                                 DEPRECIAR.PERIODO,
                                 DEPRECIAR.DEPENDENCIA,
                                 DEPRECIAR.ACUMAJUSTESAJ 
                            FROM DEPRECIAR 
                                 LEFT JOIN INVENTARIO 
                                      ON DEPRECIAR.COMPANIA = INVENTARIO.COMPANIA  
                                     AND DEPRECIAR.ELEMENTO = INVENTARIO.CODIGOELEMENTO 
                           WHERE DEPRECIAR.COMPANIA=UN_COMPANIA  
                                 AND PCK_SYSMAN_UTL.FC_MES(DEPRECIAR.PERIODO)=UN_MES
                                 AND PCK_SYSMAN_UTL.FC_ANIO(DEPRECIAR.PERIODO)=UN_ANO
                                 AND (DEPRECIAR.DEPENDENCIA = MI_STRBODEGA 
                                    OR (DEPRECIAR.DEPENDENCIA <> MI_STRBODEGA  
                                    AND DEPRECIAR.DEPENDENCIA <> MI_STRINSERVIBLES 
                                    AND DEPRECIAR.DEPENDENCIA <> MI_STRRESPONSABILIDADES )) 
                                 AND INVENTARIO.TIPO<>'C' 
                          ORDER BY DEPRECIAR.COMPANIA, 
                                   DEPRECIAR.ELEMENTO, 
                                   DEPRECIAR.PERIODO )
           LOOP
           MI_ENTRADA:= -1; 
               IF MI_ENTRADA<>0 THEN 
                  FOR MI_RS2 IN ( SELECT CODIGOELEMENTO,   --VERIFICAR LOS CAMPOS A USAR 
                                         AJUSINFLADEBITO,
                                         AJUSINFLADEBITOS
                                    FROM INVENTARIOCONTABILIDAD 
                                   WHERE COMPANIA=UN_COMPANIA  
                                         AND LENGTH(INVENTARIOCONTABILIDAD.CODIGOELEMENTO) <=  LENGTH(MI_RS.ELEMENTO)  
                                         AND INVENTARIOCONTABILIDAD.CODIGOELEMENTO = SUBSTR(MI_RS.ELEMENTO,LENGTH(INVENTARIOCONTABILIDAD.CODIGOELEMENTO)) 
                                         AND ANO=UN_ANO  
                                ORDER BY COMPANIA,CODIGOELEMENTO DESC ,ANO )

                  LOOP

                      IF MI_RS2.CODIGOELEMENTO IS NOT NULL THEN 
                         IF NVL(MI_RS.ACUMAJUSTESAJ,0)> 0 AND NVL(MI_RS2.AJUSINFLADEBITO, '')<>'' AND NVL(MI_RS2.AJUSINFLADEBITOS, '')<>'' THEN 
                               -- de bodega a servicio
                               IF MI_RS.DEPENDENCIA = MI_STRBODEGA THEN   
                                      SELECT COUNT (0) REGISTROS
                                              INTO MI_REGISTROS 
                                              FROM DEPRECIAR 
                                             WHERE DEPRECIAR.COMPANIA=UN_COMPANIA 
                                                 AND DEPRECIAR.ELEMENTO=MI_RS.ELEMENTO 
                                                 AND DEPRECIAR.SERIE=MI_RS.SERIE 
                                                 AND PCK_SYSMAN_UTL.FC_ANIO(DEPRECIAR.PERIODO)=MI_ANOANTERIOR 
                                                 AND PCK_SYSMAN_UTL.FC_MES(DEPRECIAR.PERIODO)=MI_MESANTERIOR 
                                                 AND DEPRECIAR.DEPENDENCIA <> MI_STRBODEGA 
                                                 AND DEPRECIAR.DEPENDENCIA <> MI_STRINSERVIBLES 
                                                 AND DEPRECIAR.DEPENDENCIA <> MI_STRRESPONSABILIDADES 
                                          ORDER BY DEPRECIAR.COMPANIA, 
                                                   DEPRECIAR.ELEMENTO, 
                                                   DEPRECIAR.SERIE, 
                                                   DEPRECIAR.PERIODO;

                                    IF MI_REGISTROS > 0 THEN
                                       MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITOS);
                                       MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                    ,'|| UN_ANO ||'
                                                    ,'''|| UN_TIPO ||'''
                                                    ,'|| UN_NUMERO ||'
                                                    ,'|| MI_CONSECUTIVO ||'
                                                    ,'''|| MI_RS2.AJUSINFLADEBITOS || '''
                                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                    ,'''|| MI_NATURALEZA ||'''
                                                    ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                    ,'''|| MI_VALOR ||'''
                                                    ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                    ,'''|| MI_VALOR ||'''
                                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                                          BEGIN
                                          MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                        UN_ACCION  => 'I',
                                                                        UN_CAMPOS  => MI_CAMPOS,
                                                                        UN_VALORES => MI_VALORES);
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                          END;

                                          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                                         --insercion 2 parte 1
                                          MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITO);
                                          MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                      ,'|| UN_ANO ||'
                                                      ,'''|| UN_TIPO ||'''
                                                      ,'|| UN_NUMERO ||'
                                                      ,'|| MI_CONSECUTIVO ||'
                                                      ,'''|| MI_RS2.AJUSINFLADEBITO || '''
                                                      ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                      ,'''|| MI_NATURALEZA ||'''
                                                      ,'''|| MI_VALOR ||'''
                                                      ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                      ,'''|| MI_VALOR ||'''
                                                      ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                      ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                                      ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                                      ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                                      ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                                          BEGIN
                                          MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                        UN_ACCION  => 'I',
                                                                        UN_CAMPOS  => MI_CAMPOS,
                                                                        UN_VALORES => MI_VALORES);
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                          END;

                                          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                                    END IF;   

                               ELSE
                                   SELECT COUNT (0) REGISTROS
                                              INTO MI_REGISTROS 
                                              FROM DEPRECIAR 
                                             WHERE DEPRECIAR.COMPANIA=UN_COMPANIA 
                                                 AND DEPRECIAR.ELEMENTO=MI_RS.ELEMENTO 
                                                 AND DEPRECIAR.SERIE=MI_RS.SERIE 
                                                 AND PCK_SYSMAN_UTL.FC_ANIO(DEPRECIAR.PERIODO)=MI_ANOANTERIOR 
                                                 AND PCK_SYSMAN_UTL.FC_MES(DEPRECIAR.PERIODO)=MI_MESANTERIOR 
                                                 AND DEPRECIAR.DEPENDENCIA <> MI_STRBODEGA 
                                          ORDER BY DEPRECIAR.COMPANIA, 
                                                   DEPRECIAR.ELEMENTO, 
                                                   DEPRECIAR.SERIE, 
                                                   DEPRECIAR.PERIODO;

                                   IF MI_REGISTROS > 0 THEN
                                      MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITO);
                                      MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                    ,'|| UN_ANO ||'
                                                    ,'''|| UN_TIPO ||'''
                                                    ,'|| UN_NUMERO ||'
                                                    ,'|| MI_CONSECUTIVO ||'
                                                    ,'''|| MI_RS2.AJUSINFLADEBITO || '''
                                                    ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                    ,'''|| MI_NATURALEZA ||'''
                                                    ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                    ,'''|| MI_VALOR ||'''
                                                    ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                    ,'''|| MI_VALOR ||'''
                                                    ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                                    ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                                    ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                                    ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                                          BEGIN
                                          MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                        UN_ACCION  => 'I',
                                                                        UN_CAMPOS  => MI_CAMPOS,
                                                                        UN_VALORES => MI_VALORES);
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                          END;

                                          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              

                                         --insercion 2 parte 2
                                          MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS2.AJUSINFLADEBITOS);
                                          MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                      ,'|| UN_ANO ||'
                                                      ,'''|| UN_TIPO ||'''
                                                      ,'|| UN_NUMERO ||'
                                                      ,'|| MI_CONSECUTIVO ||'
                                                      ,'''|| MI_RS2.AJUSINFLADEBITOS || '''
                                                      ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                      ,'''|| MI_NATURALEZA ||'''
                                                      ,'''|| MI_VALOR ||'''
                                                      ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                      ,'''|| MI_VALOR ||'''
                                                      ,'''|| MI_RS.ACUMAJUSTESAJ ||'''
                                                      ,'''|| PCK_DATOS.CONS_CENTRO ||'''
                                                      ,'''|| PCK_DATOS.CONS_TERCERO ||'''
                                                      ,'''|| PCK_DATOS.CONS_SUCURSAL ||'''
                                                      ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                                          BEGIN
                                          MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                        UN_ACCION  => 'I',
                                                                        UN_CAMPOS  => MI_CAMPOS,
                                                                        UN_VALORES => MI_VALORES);
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                          END;

                                          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                                       END IF;  

                               END IF; 

                         END IF;
                      END IF;
                  END LOOP;
               END IF ;   
           END LOOP;

      -- ELSE -- VERIFICAR  el llamado  debido al GOTOen access InterfazContable 

       END IF;
   MI_FUNCION:=-1;
   END;      

  RETURN MI_FUNCION;

END FC_CONTABILIZRALMACAJUSTNIVELS;

 --4
 FUNCTION FC_CONTABILIZARALMCNH     
/* 
        NAME              : En Access InterfaceAlmacenh
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : 
        DATE MIGRADOR     : 21/05/2018
        TIME              : 04:37 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     : 

        @NAME:contabilizarAlmcnH
        @METHOD:
*/
  ( 
    UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA  
   ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
   ,UN_FECHINTERF    IN  DATE                    -- la fecha de consulta para los comprobantes
   ,UN_TIPO          IN  VARCHAR2                -- tipo de comprobante Ejm: ALM    
   ,UN_NUMERO        IN  NUMBER 
   ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO -- usuario de la sesion
   )

 RETURN CLOB

  AS 
     MI_ENTRADA                      PCK_SUBTIPOS.TI_LOGICO;     -- variable de verificacion de registros en una consulta 
     MI_RTA                          PCK_SUBTIPOS.TI_LOGICO;    -- variable de retorno
     MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
     MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
     MI_RTAPLANO                     CLOB;

     MI_STRTEXTO                     PCK_SUBTIPOS.TI_DESCRIPCION;
    -- MI_RETORNO                      CLOB;

     MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
     MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
     MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
     MI_FILAS                        PCK_SUBTIPOS.TI_ENTERO;
     MI_CONDICIONACME                PCK_SUBTIPOS.TI_CONDICION;

     MI_STRELEMENTOANT               PCK_SUBTIPOS.TI_ELEMENTO;
     MI_STRCODIGOANT                 PCK_SUBTIPOS.TI_ELEMENTO;
     MI_STRCONTROL                   PCK_SUBTIPOS.TI_ELEMENTO;
     MI_CURVALOR                     PCK_SUBTIPOS.TI_DOBLE;

     MI_CODTERCERO                   PCK_SUBTIPOS.TI_TERCERO;
     MI_CODSUCURSAL                  PCK_SUBTIPOS.TI_SUCURSAL;
     MI_NATURALEZA                   PCK_SUBTIPOS.TI_NATURALEZACONTA;
     MI_CENTRODECOSTO                PCK_SUBTIPOS.TI_CENTRO_COSTO;
     MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
     MI_REGISTROS_YA_CONTAB          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;--(CC:3108 CFBARRERA: Almacena la cantidad de registros contabilizados)
     MI_LOG_OMITIDOS                 CLOB := '';--(CC:3108 CFBARRERA: Guarda el mensaje de los registros ya contabilizados) 


BEGIN 

         BEGIN
               MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''
                                     AND ANO = '|| UN_ANO ||'
                               AND TIPO_CPTE = '''|| UN_TIPO ||'''
                             AND COMPROBANTE = '|| UN_NUMERO ||'  ';

               MI_FILAS := PCK_DATOS.FC_ACME
                           (UN_TABLA     => 'TEMP_PLANA_AJUSTES'     --- VERIFICAR TABLA PlanaInterfaceAlm
                           ,UN_ACCION    => 'E'
                           ,UN_CONDICION => MI_CONDICIONACME);
         END; 

         MI_STRELEMENTOANT := '*';
         MI_STRCODIGOANT := '*';
         MI_STRCONTROL := '*';
         MI_CURVALOR := 0;

         BEGIN
               MI_ENTRADA:= 0;  
               FOR MI_RS IN( SELECT D_MOVIMIENTO.TIPOMOVIMIENTO, 
                                    D_MOVIMIENTO.ELEMENTO, 
                                    ALMACENCONTABILIDAD.CODIGOELEMENTO, 
                                    SUM(D_MOVIMIENTO.VALORTOTAL) AS VALOR, 
                                    SUM(D_MOVIMIENTO.VALORBASE) AS BASE,
                                    SUM(NVL(D_MOVIMIENTO.VALORIVA,0))AS SUMVALORIVA, 
                                    D_MOVIMIENTO.TERCERO, 
                                    D_MOVIMIENTO.SUCURSAL, 
                                    D_MOVIMIENTO.CENTRODECOSTO, 
                                    ALMACENCONTABILIDAD.CUENTADEBITO, 
                                    ALMACENCONTABILIDAD.CUENTACREDITO, 
                                    ALMACENCONTABILIDAD.DEBITO_BASE, 
                                    ALMACENCONTABILIDAD.CREDITO_BASE,
                                    ALMACENCONTABILIDAD.CREDITO_IVA, 
                                    ALMACENCONTABILIDAD.DEBITO_IVA,
                                    TIPOMOVIMIENTO.MANEJA_VENTAS,
                                    TIPOMOVIMIENTO.CLASE 
                               FROM ALMACENCONTABILIDAD
                                    LEFT JOIN  TIPOMOVIMIENTO 
                                        LEFT JOIN  D_MOVIMIENTO
                                               ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA 
                                              AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO 
                                        ON  D_MOVIMIENTO.COMPANIA= ALMACENCONTABILIDAD.COMPANIA 
                                        AND D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDAD.TIPOMOVIMIENTO          
                            WHERE SUBSTR(D_MOVIMIENTO.ELEMENTO,1,LENGTH(ALMACENCONTABILIDAD.CODIGOELEMENTO))=ALMACENCONTABILIDAD.CODIGOELEMENTO 
                                 AND D_MOVIMIENTO.COMPANIA =UN_COMPANIA
                                 AND TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY')=TO_CHAR(UN_FECHINTERF,'DD/MM/YYYY')
                                 AND D_MOVIMIENTO.IND_REG IN (-1) -- asumiendo que -1 es equivalente a true 
                                 AND ALMACENCONTABILIDAD.ANO= PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA =>UN_FECHINTERF)       
                            GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO, 
                                     D_MOVIMIENTO.ELEMENTO, 
                                     ALMACENCONTABILIDAD.CODIGOELEMENTO, 
                                     D_MOVIMIENTO.TERCERO, 
                                     D_MOVIMIENTO.SUCURSAL, 
                                     D_MOVIMIENTO.CENTRODECOSTO, 
                                     ALMACENCONTABILIDAD.CUENTADEBITO, 
                                     ALMACENCONTABILIDAD.CUENTACREDITO, 
                                     ALMACENCONTABILIDAD.DEBITO_BASE, 
                                     ALMACENCONTABILIDAD.CREDITO_BASE, 
                                     ALMACENCONTABILIDAD.CREDITO_IVA, 
                                     ALMACENCONTABILIDAD.DEBITO_IVA,
                                     TIPOMOVIMIENTO.MANEJA_VENTAS,
                                     TIPOMOVIMIENTO.CLASE)

               LOOP
                    MI_ENTRADA:= -1;


                    IF MI_RS.CLASE ='E' THEN
                       BEGIN
                             IF MI_RS.DEBITO_IVA IS NULL OR MI_RS.CREDITO_IVA IS NULL THEN 
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                             END IF; 

                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN 


                             --Por favor configure primero las cuentas CREDITO y DEBITO del IVA para generar el comprobante   
                             PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_CONFCNTCREYDEBPRGNRACOMPB,
                                                         UN_TABLAERROR => MI_TABLA );

                        --MI_RTA:= 0;
                       END;
                    END IF;

                    MI_RTAPLANO := TO_CLOB('POSIBLES INCONSISTENCIAS EN LA CONFIGURACION DE LA INTERFACE DE ALMACEN'|| CHR(13) || CHR(10));   
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('No existe configuración de interface para los elementos: '|| CHR(13) || CHR(10));    

                    IF NVL(MI_RS.CODIGOELEMENTO, '')='' THEN 
                       MI_RTAPLANO := MI_RTAPLANO || TO_CLOB (MI_RS.ELEMENTO|| CHR(13) || CHR(10));    
                    END IF;   
                    IF MI_RS.ELEMENTO = MI_STRELEMENTOANT AND MI_RS.CODIGOELEMENTO <> MI_STRCODIGOANT THEN
                       MI_STRCONTROL:= MI_RS.ELEMENTO||MI_RS.CODIGOELEMENTO;
                    ELSE
                       MI_STRCONTROL:='*';
                    END IF;
                IF MI_STRCONTROL <> MI_RS.ELEMENTO||MI_RS.CODIGOELEMENTO THEN  -- Goto
               --       GOTO CONTINUAR  REALIZAR EL GOTO 
               --   
                    IF MI_RS.ELEMENTO <> MI_STRELEMENTOANT THEN
                       MI_STRELEMENTOANT:= MI_RS.ELEMENTO;
                    END IF;
                    IF MI_RS.CODIGOELEMENTO <> MI_STRCODIGOANT THEN
                       MI_STRCODIGOANT:= MI_RS.CODIGOELEMENTO;
                    END IF;
                    IF MI_RS.MANEJA_VENTAS <>0 THEN -- -1 VERDADERO; 0 FALSO; CAPO MI_RS<>0
                       MI_CURVALOR:= TO_NUMBER((NVL(MI_RS.BASE, 0) * 100 + 0.001) / 100);
                    ELSE
                       MI_CURVALOR:= TO_NUMBER((NVL(MI_RS.VALOR, 0) * 100 + 0.001) / 100);
                    END IF;
                    --24/05/2018



                    MI_CENTRODECOSTO:= CASE 
                                         WHEN NVL(TRIM(MI_RS.CENTRODECOSTO),'') <>''
                                         THEN MI_RS.CENTRODECOSTO
                                         ELSE PCK_DATOS.CONS_CENTRO
                                         END ;

                    MI_CODTERCERO:= CASE 
                                       WHEN NVL(MI_RS.TERCERO,'') <> '' AND NVL(MI_RS.SUCURSAL,'') <> ''
                                       THEN MI_RS.TERCERO
                                       ELSE PCK_DATOS.FC_CONS_TERCERO
                                       END;

                    MI_CODSUCURSAL:=  CASE 
                                        WHEN NVL(MI_RS.TERCERO,'') <> '' AND NVL(MI_RS.SUCURSAL,'') <> ''
                                        THEN MI_RS.SUCURSAL
                                        ELSE PCK_DATOS.FC_CONS_SUCURSAL
                                        END;                                   

                    MI_TABLA := 'TEMP_PLANA_AJUSTES';
                    MI_CAMPOS := 'COMPANIA
                                  ,ANO
                                  ,TIPO_CPTE
                                  ,COMPROBANTE
                                  ,CONSECUTIVO
                                  ,CUENTA
                                  ,FECHA
                                  ,NATURALEZA
                                  ,VALOR_DEBITO
                                  ,VALOR_CREDITO
                                  ,EJECUCION_DEBITO
                                  ,EJECUCION_CREDITO
                                  ,CENTRO_COSTO
                                  ,TERCERO
                                  ,SUCURSAL
                                  ,AUXILIAR  ';


                    --Aquí empieza a llenar la Tabla Plana para luego pasarla a interfaz

                    IF MI_CURVALOR <> 0 THEN

                       IF MI_RS.CUENTADEBITO IS NOT NULL THEN
                       --aquí inserta los detalles del comprobante(cuentadebitar)
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS.CUENTADEBITO);  

                          MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS.CUENTADEBITO || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| MI_CURVALOR ||'''
                                        ,'''|| 0 ||'''
                                        ,'''|| MI_CURVALOR ||'''
                                        ,'''|| 0 ||'''
                                        ,'''|| MI_CENTRODECOSTO ||'''
                                        ,'''|| MI_CODTERCERO ||'''
                                        ,'''|| MI_CODSUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                       END IF;

                       IF MI_RS.CUENTACREDITO  IS NOT NULL THEN 
                          --aquí inserta los detalles del comprobante(cuentaacreditar)
                          MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS.CUENTACREDITO);

                          MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                        ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                        ,'''|| MI_RS.CUENTACREDITO || '''
                                        ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                        ,'''|| MI_NATURALEZA ||'''
                                        ,'''|| 0 ||'''
                                        ,'''|| MI_CURVALOR ||'''
                                        ,'''|| 0 ||'''  
                                        ,'''|| MI_CURVALOR ||'''
                                        ,'''|| MI_CENTRODECOSTO ||'''
                                        ,'''|| MI_CODTERCERO ||'''
                                        ,'''|| MI_CODSUCURSAL ||'''
                                        ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';
                        -- verificar insercion anterior 

                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                       END IF;

                     END IF;   

                     --Aqui llenamos la tablas plana pero con los valores de iva, para luego pasarla a interfaz
                     IF MI_RS.MANEJA_VENTAS <> 0 THEN
                        MI_CURVALOR:= TO_NUMBER((NVL(MI_RS.SUMVALORIVA,0)*100 + 0.001)/100);   
                         IF MI_CURVALOR <> 0 THEN 
                               IF NVL(MI_RS.CUENTADEBITO,'')<>''THEN
                               --aquí inserta los detalles del comprobante(cuentadebitar)
                                  MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS.DEBITO_IVA);

                                  MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                ,'|| UN_ANO ||'
                                                ,'''|| UN_TIPO ||'''
                                                ,'|| UN_NUMERO ||'
                                                ,'|| MI_CONSECUTIVO ||'
                                                ,'''|| MI_RS.DEBITO_IVA || '''
                                                ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                ,'''|| MI_NATURALEZA ||'''
                                                ,'''|| MI_CURVALOR ||'''
                                                ,'''|| 0 ||'''
                                                ,'''|| MI_CURVALOR ||'''
                                                ,'''|| 0 ||'''
                                                ,'''|| MI_CENTRODECOSTO ||'''
                                                ,'''|| MI_CODTERCERO ||'''
                                                ,'''|| MI_CODSUCURSAL ||'''
                                                ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';

                                    BEGIN
                                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                      UN_ACCION  => 'I',
                                                                      UN_CAMPOS  => MI_CAMPOS,
                                                                      UN_VALORES => MI_VALORES);
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                    END;

                                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                               END IF;

                               IF MI_RS.CUENTACREDITO  IS NOT NULL THEN
                                  --aquí inserta los detalles del comprobante(cuentaacreditar)
                                  MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RS.CREDITO_IVA);

                                  MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                ,'|| UN_ANO ||'
                                                ,'''|| UN_TIPO ||'''
                                                ,'|| UN_NUMERO ||'
                                                ,'|| MI_CONSECUTIVO ||'
                                                ,'''|| MI_RS.CREDITO_IVA || '''
                                                ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                                ,'''|| MI_NATURALEZA ||'''
                                                ,'''|| 0 ||'''
                                                ,'''|| MI_CURVALOR ||'''
                                                ,'''|| 0 ||'''  
                                                ,'''|| MI_CURVALOR ||'''
                                                ,'''|| MI_CENTRODECOSTO ||'''
                                                ,'''|| MI_CODTERCERO ||'''
                                                ,'''|| MI_CODSUCURSAL ||'''
                                                ,'''|| PCK_DATOS.CONS_AUXILIAR ||''' ';
                                -- verificar insercion anterior 

                                    BEGIN
                                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                      UN_ACCION  => 'I',
                                                                      UN_CAMPOS  => MI_CAMPOS,
                                                                      UN_VALORES => MI_VALORES);
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                                    END;

                                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                               END IF;
                         END IF ; 

                     END IF;   




               END IF;
              END LOOP;

               BEGIN
                     IF MI_ENTRADA <> -1  THEN 
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                     END IF;  

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN   
                    --No existen movimientos de almacén en la fecha   
                     MI_REEMPLAZOS (1).CLAVE :='FECHA_INTERFACE';
                     MI_REEMPLAZOS (1).VALOR :=UN_FECHINTERF;

                     PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_NOEXTMOVDEALMCNAFCHA,
                                                 UN_TABLAERROR => MI_TABLA,
                                                 UN_REEMPLAZOS => MI_REEMPLAZOS); 
                    -- MI_RTA:= 0;                                                
               END;

         END;

         MI_STRTEXTO := ' INTERFACE DE ALMACEN A CONTABILIDAD DE LA FECHA:'|| UN_FECHINTERF ||' ';
         --se evalua el parametro y posteriormente se hace una consulta a la funcion FC_CONTABILIZAR
         IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                                      UN_NOMBRE => 'SIMPLIFICAR INTERFACE ALMACEN' ,
                                      UN_MODULO => -1, 
                                      UN_FECHA_PAR => SYSDATE ), 'SI') = 'SI' THEN 
             MI_RTAPLANO:= MI_RTAPLANO || TO_CLOB (PCK_CONTABILIZAR.FC_CONTABILIZAR(  UN_COMPANIA         => UN_COMPANIA
                                                              ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                              ,UN_NUMERO           => UN_NUMERO 
                                                              ,UN_ANO              => UN_ANO
                                                              ,UN_FECHA            => UN_FECHINTERF
                                                              ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                              ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                              ,UN_DESCRIPCION      => MI_STRTEXTO
                                                             -- DB
                                                              ,UN_USUARIO          => UN_USUARIO 
                                                              ,UN_SIMPLE           => -1
                                                              ,UN_INDIMPRESION     => -1
                                                               )) ;

             MI_RTA:= -1;
             /*BEGIN
                     IF MI_RTA <> 0  THEN 
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                     END IF;  

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN   
                    --La interface se realizó con éxito   
                     PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_NOEXTMOVDEALMCNAFCHA,
                                                 UN_TABLAERROR => MI_TABLA );

              END;*/

         ELSE                                                         
              MI_RTAPLANO:= MI_RTAPLANO || TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                                              ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                              ,UN_NUMERO           => UN_NUMERO 
                                                              ,UN_ANO              => UN_ANO
                                                              ,UN_FECHA            => UN_FECHINTERF
                                                              ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                              ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                              ,UN_DESCRIPCION      => MI_STRTEXTO
                                                             -- DB
                                                              ,UN_USUARIO          => UN_USUARIO 
                                                              ,UN_SIMPLE           => 0
                                                              ,UN_INDIMPRESION     => -1
                                                               )) ;
              MI_RTA:= 0;
          /*    BEGIN
                     IF MI_RTA <> -1  THEN 
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                     END IF;  

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN   
                    --La interface NO se realizó con éxito   
                     PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_NOEXTMOVDEALMCNAFCHA,
                                                 UN_TABLAERROR => MI_TABLA );

              END;
            */   
          END IF ;

        --(INI_CC:3108_Genera alerta si el proceso de contabilizacion se ejecuto por segunda vez en el mismo periodo)
        BEGIN
            FOR RS_OMITIDOS IN (
                 SELECT M.COMPANIA,
                       M.TIPOMOVIMIENTO,
                       M.NUMERO,
                       M.TIPO_CPTE_CONTABLE,
                       M.CPTE_CONTABLE,
                       TO_CHAR(M.FECHA, 'DD/MM/YYYY') AS FECHA_STR
                FROM MOVIMIENTO M
               WHERE M.COMPANIA = UN_COMPANIA
                  AND M.ANO = UN_ANO  
                  AND TRUNC(M.FECHA) = UN_FECHINTERF
                  AND M.TIPO_CPTE_CONTABLE IS NOT NULL  
                  AND M.CPTE_CONTABLE IS NOT NULL        
            ) LOOP
                MI_REGISTROS_YA_CONTAB := MI_REGISTROS_YA_CONTAB + 1;
                
                -- Agregar cada registro en una línea
                 MI_LOG_OMITIDOS := MI_LOG_OMITIDOS || 
                ' Compañía: ' || RS_OMITIDOS.COMPANIA || 
                ', Tipo Movimiento: ' || RS_OMITIDOS.TIPOMOVIMIENTO ||
                ', Número Movimiento: ' || RS_OMITIDOS.NUMERO ||
                ', Ya contabilizado con tipo comprobante: ' || RS_OMITIDOS.TIPO_CPTE_CONTABLE ||
                ', Comprobante: ' || RS_OMITIDOS.CPTE_CONTABLE ||
                ', Fecha: ' || RS_OMITIDOS.FECHA_STR ||
                CHR(10);
            END LOOP;
            
            IF MI_REGISTROS_YA_CONTAB > 0 THEN
                -- Agregar separación antes del mensaje
                MI_RTAPLANO := MI_RTAPLANO || CHR(10) || CHR(10);
                
                -- Construir el mensaje línea por línea
                MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
                MI_RTAPLANO := MI_RTAPLANO || 'REGISTROS YA CONTABILIZADOS (OMITIDOS)' || CHR(10);
                MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
                MI_RTAPLANO := MI_RTAPLANO || 'Total registros omitidos: ' || MI_REGISTROS_YA_CONTAB || CHR(10);
                MI_RTAPLANO := MI_RTAPLANO || CHR(10);
                MI_RTAPLANO := MI_RTAPLANO || MI_LOG_OMITIDOS;
                MI_RTAPLANO := MI_RTAPLANO || CHR(10);
            END IF;
        END; --(FIN_CC:3108_FIN_CFBARRERA)
       
        --(INI_CC:3108 Registra el comprobante y el numero al realizar la contabilizacion)
            MI_TABLA := 'MOVIMIENTO';

            MI_CAMPOS := 'TIPO_CPTE_CONTABLE = CASE 
                              WHEN TIPO_CPTE_CONTABLE IS NULL THEN ''' || UN_TIPO || ''' 
                              ELSE TIPO_CPTE_CONTABLE 
                          END
                         ,CPTE_CONTABLE = CASE 
                              WHEN CPTE_CONTABLE IS NULL THEN ' || UN_NUMERO || ' 
                              ELSE CPTE_CONTABLE 
                          END
                         ,REGISTRADO = CASE 
                              WHEN REGISTRADO = 0 THEN -1 
                              ELSE REGISTRADO 
                          END';

            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                            AND ANO = ' || UN_ANO || '
                            AND FECHA = TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')';
                
            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
            
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;--(FIN_CC:3108)


  RETURN MI_RTAPLANO; 

END FC_CONTABILIZARALMCNH;

 --5
 FUNCTION FC_CONTABILIZARHNIVELES 
/*  
        NAME              : En Access InterfaceAlmacenHNiveles
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 27605/2018
        TIME              : 05:07 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:contabilizarHNiveles
        @METHOD:Post
*/   
     (   
        UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
       ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
       ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
       ,UN_FECHINTERF    IN  DATE 
       ,UN_TIPO          IN  VARCHAR2     
       ,UN_NUMERO        IN  NUMBER
       ,UN_NIIF          IN  PCK_SUBTIPOS.TI_LOGICO
       ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
     ) 

RETURN CLOB

AS
     MI_PROVEEDORSALIDA              PCK_SUBTIPOS.TI_LOGICO :=0;              -- variable que define el INTO a usar
     MI_CODTERCERO                   PCK_SUBTIPOS.TI_TERCERO;
     MI_CODSUCURSAL                  PCK_SUBTIPOS.TI_SUCURSAL;
     MI_IVA_DISCRI                   PCK_SUBTIPOS.TI_STRSQL; 
     MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;    
     MI_STRTEXTO                     PCK_SUBTIPOS.TI_DESCRIPCION;
     MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
     MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
     MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
     MI_FILAS                        PCK_SUBTIPOS.TI_ENTERO;
     MI_CONDICIONACME                PCK_SUBTIPOS.TI_CONDICION;
     MI_NATURALEZA                   PCK_SUBTIPOS.TI_NATURALEZACONTA;
     MI_STRELEMENTOANT               PCK_SUBTIPOS.TI_ELEMENTO;
     MI_STRCODIGOANT                 PCK_SUBTIPOS.TI_ELEMENTO;
     MI_STRCONTROL                   VARCHAR2(60);
     MI_CURVALOR                     PCK_SUBTIPOS.TI_DOBLE;
     MI_RTAPLANO                     CLOB;
     MI_FECHAINTERFAZ                VARCHAR2(12);
     MI_CUENTA                       PCK_SUBTIPOS.TI_STRSQL; 
     MI_CONSULTA                     CLOB;
     MI_CONTADOR                     PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
     MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
     MI_REGISTROS_YA_CONTAB          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;--(CC:3108 CFBARRERA: Almacena la cantidad de registros contabilizados)
     MI_LOG_OMITIDOS                 CLOB := '';--(CC:3108 CFBARRERA: Guarda el mensaje de los registros ya contabilizados) 
     TYPE REG IS RECORD 
     (
        TIPOMOVIMIENTO     D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
        ELEMENTO           D_MOVIMIENTO.ELEMENTO%TYPE,
        CODIGOELEMENTO     ALMACENCONTABILIDAD.CODIGOELEMENTO%TYPE,
        TERCERO            MOVIMIENTO.TERCERO%TYPE,
        SUCURSAL           MOVIMIENTO.SUCURSAL%TYPE,
        PROVEEDORCA        MOVIMIENTO.PROVEEDORCA%TYPE,
        SUCURSALCA         MOVIMIENTO.SUCURSALCA%TYPE,
        CENTRODECOSTO      D_MOVIMIENTO.CENTRODECOSTO%TYPE,
        REFERENCIA         MOVIMIENTO.REFERENCIA%TYPE,
        FUENTE_RECURSO     MOVIMIENTO.FUENTEDERECURSO%TYPE,
        SUMVALORBASE       NUMBER,
        SUMVALORIVA        NUMBER,
        VALOR              NUMBER,
        CUENTADEBITO       ALMACENCONTABILIDAD.CUENTADEBITO%TYPE,
        CUENTACREDITO      ALMACENCONTABILIDAD.CUENTACREDITO%TYPE,
        DEBITO_BASE        ALMACENCONTABILIDAD.DEBITO_BASE%TYPE,
        CREDITO_BASE       ALMACENCONTABILIDAD.CREDITO_BASE%TYPE,
        DEBITO_IVA         ALMACENCONTABILIDAD.DEBITO_IVA%TYPE,
        CREDITO_IVA        ALMACENCONTABILIDAD.CREDITO_IVA%TYPE,
        DISCRIMINA_IVA     VARCHAR2(100)
     ); 
     TYPE REGISTROS IS TABLE OF REG INDEX BY BINARY_INTEGER ;
     T_RS REGISTROS;
     MI_RS    SYS_REFCURSOR;
     MI_I     INTEGER;

BEGIN
    BEGIN
        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''
                             AND ANO = '|| UN_ANO ||'
                       AND TIPO_CPTE = '''|| UN_TIPO ||'''
                     AND COMPROBANTE = '|| UN_NUMERO ||'  ';

        MI_FILAS := PCK_DATOS.FC_ACME
                   (UN_TABLA     => 'TEMP_PLANA_AJUSTES'     
                   ,UN_ACCION    => 'E'
                   ,UN_CONDICION => MI_CONDICIONACME);
    END; 
    MI_IVA_DISCRI:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN' ,
                                              UN_MODULO    => 96, 
                                              UN_FECHA_PAR => SYSDATE ), 'NO');

    --si cumple el parametro se consolida la consulta segun corresponda        
    IF   NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'MOSTRAR PROVEEDOR EN SALIDAS DE ALMACEN' ,
                                   UN_MODULO    => 96, 
                                   UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI'  THEN 
        MI_PROVEEDORSALIDA:= -1 ;            
    ELSE
        MI_PROVEEDORSALIDA:= 0 ;
    END IF;  
    MI_FECHAINTERFAZ:= TO_CHAR (UN_FECHINTERF,'DD/MM/YYYY');                                                                          
    MI_TABLA := 'TEMP_PLANA_AJUSTES';
    MI_CAMPOS := 'COMPANIA
              ,ANO
              ,TIPO_CPTE
              ,COMPROBANTE
              ,CONSECUTIVO
              ,CUENTA
              ,FECHA
              ,NATURALEZA
              ,VALOR_DEBITO
              ,VALOR_CREDITO
              ,EJECUCION_DEBITO
              ,EJECUCION_CREDITO
              ,CENTRO_COSTO
              ,TERCERO
              ,SUCURSAL
              ,REFERENCIA
              ,FUENTE_RECURSOS';

    MI_STRELEMENTOANT := '*';
    MI_STRCODIGOANT := '*';
    MI_STRCONTROL := '*';
    MI_CURVALOR := 0;
    MI_RTAPLANO := '';
    MI_CONSULTA:= PCK_CONTABILIZAR_ALMACEN.FC_CONTBLIZARARMCONSLTHNVLES(UN_COMPANIA    => UN_COMPANIA
                                                                       ,UN_FECHINTERF  => UN_FECHINTERF
                                                                       ,UN_NIIF        => UN_NIIF);

    OPEN MI_RS FOR MI_CONSULTA;
    LOOP 
    FETCH MI_RS BULK COLLECT
    INTO T_RS;
        FOR MI_I IN 1 .. T_RS.COUNT
        LOOP
            IF T_RS(MI_I).CUENTADEBITO IS NULL OR T_RS(MI_I).CUENTACREDITO IS NULL THEN
                IF MI_CONTADOR = 1 THEN 
                    MI_RTAPLANO := TO_CLOB('POSIBLES INCONSISTENCIAS EN LA CONFIGURACION DE LA INTERFACE DE ALMACEN '|| CHR(13) || CHR(10));
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('No existe configuración de interface para los elementos: '|| CHR(13) || CHR(10));
                    MI_CONTADOR := MI_CONTADOR + 1;
                END IF;
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ( T_RS(MI_I).ELEMENTO|| CHR(13) || CHR(10) || ',');
            END IF;
            IF T_RS(MI_I).ELEMENTO = MI_STRELEMENTOANT AND T_RS(MI_I).CODIGOELEMENTO <> MI_STRCODIGOANT THEN
                MI_STRCONTROL:= T_RS(MI_I).ELEMENTO || T_RS(MI_I).CODIGOELEMENTO;
            ELSE
                MI_STRCONTROL:='*';
            END IF;
            IF T_RS(MI_I).ELEMENTO <> MI_STRELEMENTOANT THEN
                MI_STRELEMENTOANT:= T_RS(MI_I).ELEMENTO;
            END IF;
            IF T_RS(MI_I).CODIGOELEMENTO <> MI_STRCODIGOANT THEN
                MI_STRCODIGOANT:= T_RS(MI_I).CODIGOELEMENTO;
            END IF;
            IF  MI_STRCONTROL <> T_RS(MI_I).ELEMENTO||T_RS(MI_I).CODIGOELEMENTO THEN 
                --Aquí empieza a llenar la Tabla Plana para luego pasarla a interfaz
                MI_CURVALOR:= TO_NUMBER((NVL(T_RS(MI_I).VALOR, 0) * 100 + 0.001) / 100); 
                IF MI_CURVALOR <> 0 THEN
                    MI_CODTERCERO := T_RS(MI_I).TERCERO;
                    MI_CODSUCURSAL:= T_RS(MI_I).SUCURSAL;
                    IF MI_PROVEEDORSALIDA =-1 THEN
                        MI_CODTERCERO:= CASE WHEN T_RS(MI_I).PROVEEDORCA <>'999999999999999999' AND T_RS(MI_I).SUCURSALCA<>'999'
                                             THEN T_RS(MI_I).PROVEEDORCA ELSE T_RS(MI_I).TERCERO END;
                        MI_CODSUCURSAL:= CASE WHEN T_RS(MI_I).PROVEEDORCA <>'999999999999999999' AND T_RS(MI_I).SUCURSALCA<>'999'
                                              THEN T_RS(MI_I).SUCURSALCA ELSE T_RS(MI_I).SUCURSAL END;
                    END IF;
                    IF MI_IVA_DISCRI='SI' THEN
                        MI_CURVALOR:= TO_NUMBER((T_RS(MI_I).SUMVALORBASE* 100 + 0.001) / 100); 
                    END IF;
                    MI_CUENTA:= CASE WHEN  MI_IVA_DISCRI='SI' THEN T_RS(MI_I).DEBITO_BASE ELSE T_RS(MI_I).CUENTADEBITO END;
                    IF  NVL(MI_CUENTA,' ')  <> ' '  THEN
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CUENTA);
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                      ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                      ,'''|| MI_CUENTA || '''
                                       ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                       ,'''|| MI_NATURALEZA ||'''
                                       ,'|| MI_CURVALOR ||'
                                       ,'|| 0 ||'
                                       ,'|| MI_CURVALOR ||'
                                       ,'''|| 0 ||'''
                                       ,'''|| T_RS(MI_I).CENTRODECOSTO ||'''
                                       ,'''|| MI_CODTERCERO ||'''
                                       ,'''|| MI_CODSUCURSAL ||'''
                                       ,'''|| T_RS(MI_I).REFERENCIA ||'''
                                       ,'''|| T_RS(MI_I).FUENTE_RECURSO ||'''';

                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                            UN_ACCION  => 'I',
                                                            UN_CAMPOS  => MI_CAMPOS,
                                                            UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;     
                    END IF;  
                    MI_CUENTA:= CASE WHEN  MI_IVA_DISCRI='SI' THEN T_RS(MI_I).CREDITO_BASE ELSE T_RS(MI_I).CUENTACREDITO END;
                    IF  NVL(MI_CUENTA,' ')  <> ' '  THEN
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CUENTA);
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                        ,'|| UN_ANO ||'
                                      ,'''|| UN_TIPO ||'''
                                        ,'|| UN_NUMERO ||'
                                        ,'|| MI_CONSECUTIVO ||'
                                      ,'''|| MI_CUENTA || '''
                                       ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                       ,'''|| MI_NATURALEZA ||'''
                                       ,'''|| 0 ||'''
                                       ,'''|| MI_CURVALOR ||'''
                                       ,'''|| 0 ||'''
                                       ,'''|| MI_CURVALOR ||'''
                                       ,'''|| T_RS(MI_I).CENTRODECOSTO ||'''
                                       ,'''|| MI_CODTERCERO ||'''
                                       ,'''|| MI_CODSUCURSAL ||'''
                                       ,'''|| T_RS(MI_I).REFERENCIA ||'''
                                       ,'''|| T_RS(MI_I).FUENTE_RECURSO ||'''';

                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                            UN_ACCION  => 'I',
                                                            UN_CAMPOS  => MI_CAMPOS,
                                                            UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;     
                    END IF;

                    IF MI_IVA_DISCRI='SI' THEN
                        IF  NVL(T_RS(MI_I).DEBITO_IVA,' ')  <> ' '  THEN
                            MI_CURVALOR:= TO_NUMBER((T_RS(MI_I).SUMVALORIVA* 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => T_RS(MI_I).DEBITO_IVA);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                          ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                          ,'''|| T_RS(MI_I).DEBITO_IVA || '''
                                           ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                           ,'''|| MI_NATURALEZA ||'''
                                           ,'''|| MI_CURVALOR ||'''
                                           ,'''|| 0 ||'''
                                           ,'''|| MI_CURVALOR ||'''
                                           ,'''|| 0 ||'''
                                           ,'''|| T_RS(MI_I).CENTRODECOSTO ||'''
                                           ,'''|| MI_CODTERCERO ||'''
                                           ,'''|| MI_CODSUCURSAL ||'''
                                           ,'''|| T_RS(MI_I).REFERENCIA ||'''
                                           ,'''|| T_RS(MI_I).FUENTE_RECURSO ||'''';

                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                UN_ACCION  => 'I',
                                                                UN_CAMPOS  => MI_CAMPOS,
                                                                UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;     
                        END IF;
                        IF  NVL(T_RS(MI_I).CREDITO_IVA,' ')  <> ' '  THEN
                            MI_CURVALOR:= TO_NUMBER((T_RS(MI_I).SUMVALORIVA* 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => T_RS(MI_I).CREDITO_IVA);
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                            ,'|| UN_ANO ||'
                                          ,'''|| UN_TIPO ||'''
                                            ,'|| UN_NUMERO ||'
                                            ,'|| MI_CONSECUTIVO ||'
                                          ,'''|| T_RS(MI_I).CREDITO_IVA || '''
                                           ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                           ,'''|| MI_NATURALEZA ||'''
                                           ,'''|| 0 ||'''
                                           ,'''|| MI_CURVALOR ||'''
                                           ,'''|| 0 ||'''
                                           ,'''|| MI_CURVALOR ||'''
                                           ,'''|| T_RS(MI_I).CENTRODECOSTO ||'''
                                           ,'''|| MI_CODTERCERO ||'''
                                           ,'''|| MI_CODSUCURSAL ||'''
                                           ,'''|| T_RS(MI_I).REFERENCIA ||'''
                                           ,'''|| T_RS(MI_I).FUENTE_RECURSO ||'''';

                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                UN_ACCION  => 'I',
                                                                UN_CAMPOS  => MI_CAMPOS,
                                                                UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;     
                        END IF;                         
                    END IF;
                END IF;
            END IF; 
        END LOOP;
        EXIT WHEN MI_RS%NOTFOUND;
    END LOOP;
    CLOSE MI_RS;

    MI_STRTEXTO := ' INTERFACE DE ALMACEN A CONTABILIDAD DE LA FECHA:'|| UN_FECHINTERF || '';
    IF MI_CONTADOR = 1 THEN 
        MI_RTAPLANO := TO_CLOB('INTERFACE DE ALMACEN A CONTABILIDAD EJECUTADA CORRECTAMENTE: '|| UN_FECHINTERF || CHR(13) || CHR(10));
    END IF;
    MI_RTAPLANO:=  MI_RTAPLANO||TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR(  UN_COMPANIA         => UN_COMPANIA
                                                      ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                      ,UN_NUMERO           => UN_NUMERO 
                                                      ,UN_ANO              => UN_ANO
                                                      ,UN_FECHA            => UN_FECHINTERF
                                                      ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                      ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                      ,UN_DESCRIPCION      => MI_STRTEXTO
                                                      ,UN_USUARIO          => UN_USUARIO 
                                                      ,UN_SIMPLE           => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                                                  UN_NOMBRE    => 'SIMPLIFICAR INTERFACE ALMACEN' ,
                                                                                                                  UN_MODULO    => 96, 
                                                                                                                  UN_FECHA_PAR => SYSDATE ), 'SI') = 'SI' THEN -1 ELSE 0 END
                                                      ,UN_INDIMPRESION     => -1
                                                      ,UN_INTOMITIRPPTAL   => -1
                                                       )) ;
    --(INI_CC:3108_Genera alerta si el proceso de contabilizacion se ejecuto por segunda vez en el mismo periodo)
    BEGIN
        FOR RS_OMITIDOS IN (
             SELECT M.COMPANIA,
                   M.TIPOMOVIMIENTO,
                   M.NUMERO,
                   M.TIPO_CPTE_CONTABLE,
                   M.CPTE_CONTABLE,
                   TO_CHAR(M.FECHA, 'DD/MM/YYYY') AS FECHA_STR
            FROM MOVIMIENTO M
           WHERE M.COMPANIA = UN_COMPANIA
              AND M.ANO = UN_ANO  
              AND TRUNC(M.FECHA) = UN_FECHINTERF
              AND M.TIPO_CPTE_CONTABLE IS NOT NULL  
              AND M.CPTE_CONTABLE IS NOT NULL        
        ) LOOP
            MI_REGISTROS_YA_CONTAB := MI_REGISTROS_YA_CONTAB + 1;
            
            -- Agregar cada registro en una línea
            MI_LOG_OMITIDOS := MI_LOG_OMITIDOS || 
                ' Compañía: ' || RS_OMITIDOS.COMPANIA || 
                ', Tipo Movimiento: ' || RS_OMITIDOS.TIPOMOVIMIENTO ||
                ', Número Movimiento: ' || RS_OMITIDOS.NUMERO ||
                ', Ya contabilizado con tipo comprobante: ' || RS_OMITIDOS.TIPO_CPTE_CONTABLE ||
                ', Comprobante: ' || RS_OMITIDOS.CPTE_CONTABLE ||
                ', Fecha: ' || RS_OMITIDOS.FECHA_STR ||
                CHR(10);
        END LOOP;
        
        IF MI_REGISTROS_YA_CONTAB > 0 THEN
            -- Agregar separación antes del mensaje
            MI_RTAPLANO := MI_RTAPLANO || CHR(10) || CHR(10);
            
            -- Construir el mensaje línea por línea
            MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || 'REGISTROS YA CONTABILIZADOS (OMITIDOS)' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || 'Total registros omitidos: ' || MI_REGISTROS_YA_CONTAB || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || MI_LOG_OMITIDOS;
            MI_RTAPLANO := MI_RTAPLANO || CHR(10);
        END IF;
    END; --(FIN_CC:3108_FIN_CFBARRERA)
   

    --(INI_CC:3108 Registra el comprobante y el numero al realizar la contabilizacion)
        MI_TABLA := 'MOVIMIENTO';

        MI_CAMPOS := 'TIPO_CPTE_CONTABLE = CASE 
                          WHEN TIPO_CPTE_CONTABLE IS NULL THEN ''' || UN_TIPO || ''' 
                          ELSE TIPO_CPTE_CONTABLE 
                      END
                     ,CPTE_CONTABLE = CASE 
                          WHEN CPTE_CONTABLE IS NULL THEN ' || UN_NUMERO || ' 
                          ELSE CPTE_CONTABLE 
                      END
                     ,REGISTRADO = CASE 
                          WHEN REGISTRADO = 0 THEN -1 
                          ELSE REGISTRADO 
                      END';

        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                        AND ANO = ' || UN_ANO || '
                        AND FECHA = TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')';
            
        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_CONDICION => MI_CONDICION);
        
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
    END;--(FIN_CC:3108)

  RETURN MI_RTAPLANO;

 END FC_CONTABILIZARHNIVELES;

 --6
  FUNCTION FC_CONTABILIZARHNIVELESCC   
/*   
        NAME              : En Access InterfaceAlmacenHNivelesCC
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : 
        DATE MIGRADOR     : 19/06/2018
        TIME              : 12:37 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:contabilizarHNivelesCC
        @METHOD:
*/   
    (   
       UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
      ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
      ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
      ,UN_FECHINTERF    IN  DATE 
      ,UN_TIPO          IN  VARCHAR2                  -- tipo de comprobante Ejm: ALM
      ,UN_NUMERO        IN  NUMBER
      ,UN_NIIF          IN  PCK_SUBTIPOS.TI_LOGICO   -- 'check habilita la consulta para NIIF'
      ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO  --sesion del usuario
     ) 

RETURN CLOB

AS
  MI_ALMDEP                       PCK_SUBTIPOS.TI_LOGICO; -- variable de almacenamiento si corresponde el parametro parametro 
  MI_ALMPRO                       PCK_SUBTIPOS.TI_LOGICO; -- variable de almacenamiento si corresponde el parametro parametro 
  MI_SIMPLIFICA_ALM               PCK_SUBTIPOS.TI_LOGICO;
  MI_DISCRIMINA_IVA               PCK_SUBTIPOS.TI_LOGICO;
  MI_CODTERCERO                   PCK_SUBTIPOS.TI_TERCERO;
  MI_CODSUCURSAL                  PCK_SUBTIPOS.TI_SUCURSAL;
  MI_BOLIND                       PCK_SUBTIPOS.TI_LOGICO;  -- variable de referencia para validar la insercion para cuenta debito y cuenta credito 

  MI_STRELEMENTOANT               PCK_SUBTIPOS.TI_ELEMENTO;
  MI_STRCODIGOANT                 PCK_SUBTIPOS.TI_ELEMENTO;
  MI_STRCONTROL                   PCK_SUBTIPOS.TI_ELEMENTO;
  MI_CURVALOR                     PCK_SUBTIPOS.TI_DOBLE;

  MI_RTAPLANO                     CLOB;
 -- MI_RETORNO                      CLOB;
  MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_STRAUXILIAR                  PCK_SUBTIPOS.TI_AUXILIAR; 

  MI_CENTRODECOSTO                PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
  MI_NATURALEZA                   PCK_SUBTIPOS.TI_NATURALEZACONTA;


  MI_STRTEXTO                     PCK_SUBTIPOS.TI_DESCRIPCION;
  MI_RTA                          VARCHAR2(3200 CHAR);
  MI_RESPUESTA                    PCK_SUBTIPOS.TI_LOGICO;
  MI_FECHAINTERFAZ                VARCHAR2(12);

  MI_RS                           SYS_REFCURSOR;
  MI_CONDICION                    VARCHAR2(3200 CHAR);
  MI_VERIFI                       PCK_SUBTIPOS.TI_LOGICO;-- variable que define el INTO a usar

  MI_CONSULTA                     VARCHAR2(32000);      -- variable que contiene la consulta compuesta
  MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
  MI_FILAS                        PCK_SUBTIPOS.TI_ENTERO;
  MI_CONDICIONACME                PCK_SUBTIPOS.TI_CONDICION;

  TIPOMOVIMIENTO                D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE; -- variables definidas para el INTO-- 
  ELEMENTO                      D_MOVIMIENTO.ELEMENTO%TYPE;
  CODIGOELEMENTO                ALMACENCONTABILIDAD.CODIGOELEMENTO%TYPE;
  VALOR                         D_MOVIMIENTO.VALORTOTAL%TYPE;
  TERCERO                       D_MOVIMIENTO.TERCERO%TYPE;
  SUCURSAL                      D_MOVIMIENTO.SUCURSAL%TYPE;
  CENTRODECOSTO                 D_MOVIMIENTO.CENTRODECOSTO%TYPE;
  CUENTADEBITO                  ALMACENCONTABILIDAD.CUENTADEBITO%TYPE;
  CUENTACREDITO                 ALMACENCONTABILIDAD.CUENTACREDITO%TYPE;
  DEBITO_BASE                   ALMACENCONTABILIDAD.DEBITO_BASE%TYPE;
  CREDITO_IVA                   ALMACENCONTABILIDAD.DEBITO_IVA%TYPE;
  DEBITO_IVA                    ALMACENCONTABILIDAD.DEBITO_IVA%TYPE;
  CREDITO_BASE                  ALMACENCONTABILIDAD.CREDITO_BASE%TYPE;
  SUMVALORBASE                  D_MOVIMIENTO.VALORBASE%TYPE;
  SUMVALORIVA                   D_MOVIMIENTO.VALORIVA%TYPE;
  DEPENDENCIA_ORIGEN            MOVIMIENTO.DEPENDENCIA_ORIGEN%TYPE;
  PROVEEDORCA                   D_MOVIMIENTO.PROVEEDORCA%TYPE ;
  SUCURSALCA                    D_MOVIMIENTO.PROVEEDORCA%TYPE ;
  AUXILIAR                      INVENTARIO.AUXILIAR%TYPE;
  DEPENDENCIAS                  MOVIMIENTO.DEPENDENCIA_ORIGEN%TYPE;
  FUENTEDERECURSO               D_MOVIMIENTO.FUENTEDERECURSO%TYPE;
  MI_FUENTERECURSO              PCK_SUBTIPOS.TI_FUENTE_RECURSO;
  MI_REGISTROS_YA_CONTAB        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;--(CC:3108 CFBARRERA: Almacena la cantidad de registros contabilizados)
  MI_LOG_OMITIDOS               CLOB := '';--(CC:3108 CFBARRERA: Guarda el mensaje de los registros ya contabilizados) 

BEGIN
   -- propio para almacen de yopal -- comentario proveniente de migracion  
   MI_ALMDEP:= CASE 
               WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'INTERFAZ DE ALMACEN CON DEPENDENCIA' ,
                                              UN_MODULO    => 96, 
                                              UN_FECHA_PAR => SYSDATE ), 'SI') = 'NO' 
               THEN 0 
               ELSE -1
               END; 

   MI_ALMPRO:= CASE 
               WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'MOSTRAR PROVEEDOR EN SALIDAS DE ALMACEN' ,
                                              UN_MODULO    => 96, 
                                              UN_FECHA_PAR => SYSDATE ), 'NO') = 'NO' 
               THEN 0 
               ELSE -1
               END;            
    MI_SIMPLIFICA_ALM := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'SIMPLIFICAR INTERFACE ALMACEN' ,
                                   UN_MODULO    => 96, 
                                   UN_FECHA_PAR => SYSDATE ), 'SI') ='SI'
                         THEN -1
                         ELSE 0
                         END;
    
    MI_DISCRIMINA_IVA := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                           UN_NOMBRE    => 'VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN' ,
                                           UN_MODULO    => 96, 
                                           UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI'
                         THEN -1
                         ELSE 0
                         END; 
    -- se realiza un delete a la tabla plan TEMP_PLANA_AJUSTES
    BEGIN
       MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || '''
                             AND ANO = '|| UN_ANO ||'
                       AND TIPO_CPTE = '''|| UN_TIPO ||'''
                     AND COMPROBANTE = '|| UN_NUMERO ||'  ';
    
       MI_FILAS := PCK_DATOS.FC_ACME
                   (UN_TABLA     => 'TEMP_PLANA_AJUSTES'     
                   ,UN_ACCION    => 'E'
                   ,UN_CONDICION => MI_CONDICIONACME);
    END; 

    -- se formatea fecha de interfaz 
    MI_FECHAINTERFAZ:= TO_CHAR (UN_FECHINTERF,'DD/MM/YYYY');

    -- se valida el parametro MI_ALMDEP si corresponde se le agrega campos a los select y grup by                         
    IF MI_ALMDEP <> -1 THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                    UN_NOMBRE    => 'MANEJA INTERFACE DE ALMACEN INCLUYENDO AUXILIAR' ,
                                    UN_MODULO    => 96, 
                                    UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' 
          AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                        UN_NOMBRE    => 'MANEJA INTERFACE MENSUAL NIIF POR CENTRO DE COSTO' ,
                                        UN_MODULO    => 96, 
                                        UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' THEN 
            MI_VERIFI:=-1;    
        ELSE 
            MI_VERIFI:=0;    
        END IF;
    END IF; 
    -- me trae la consulta elaborada que corresponda  
    MI_CONSULTA:= PCK_CONTABILIZAR_ALMACEN.FC_CONTBLIZARARMCONSLTHNVLESCC (UN_COMPANIA    => UN_COMPANIA
                                                                       ,UN_FECHINTERF  => UN_FECHINTERF
                                                                       ,UN_NIIF        => UN_NIIF);
    
    MI_STRELEMENTOANT := '*';
    MI_STRCODIGOANT := '*';
    MI_STRCONTROL := '*';
    MI_CURVALOR := 0;
             
    --se recorre la consulta armada segun corresponda y se selecciona el INTO que se ajuste
    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
        IF MI_ALMDEP <>0 AND MI_ALMPRO <>0 THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,  DEPENDENCIA_ORIGEN,            
                PROVEEDORCA,  SUCURSALCA ;
        ELSIF MI_ALMDEP <>0 THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                  VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                  FUENTEDERECURSO,
                  CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                  CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                  SUMVALORBASE,  SUMVALORIVA,  DEPENDENCIA_ORIGEN  ;            
        ELSIF MI_ALMPRO <>0 THEN
           FETCH MI_RS
           INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,  PROVEEDORCA,  SUCURSALCA ;           
        ELSIF MI_VERIFI <>0 AND MI_ALMPRO <>0 THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,  DEPENDENCIAS,            
                PROVEEDORCA,  SUCURSALCA ;
        ELSIF MI_VERIFI <>-1 AND MI_ALMPRO <>0 THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,  AUXILIAR,            
                PROVEEDORCA,  SUCURSALCA ;     
        ELSIF MI_VERIFI <>0  THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,   DEPENDENCIAS; 
        ELSIF MI_VERIFI <>-1 AND MI_ALMPRO <>0 THEN
            FETCH MI_RS
            INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,  AUXILIAR  ;                    
        ELSIF MI_ALMDEP =0 AND MI_ALMPRO =0  THEN
           FETCH MI_RS
           INTO TIPOMOVIMIENTO, ELEMENTO,  CODIGOELEMENTO,            
                VALOR, TERCERO, SUCURSAL,  CENTRODECOSTO,
                FUENTEDERECURSO,
                CUENTADEBITO,  CUENTACREDITO,  DEBITO_BASE,                
                CREDITO_IVA,   DEBITO_IVA,   CREDITO_BASE,                 
                SUMVALORBASE,  SUMVALORIVA,   DEPENDENCIAS;
        END IF ;
        EXIT WHEN MI_RS%NOTFOUND;
        MI_RTAPLANO := TO_CLOB('POSIBLES INCONSISTENCIAS EN LA CONFIGURACION DE LA INTERFACE DE ALMACEN'|| CHR(13) || CHR(10));   
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('No existe configuración de interface para los elementos: '|| CHR(13) || CHR(10));

        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'MANEJA INTERFACE DE ALMACEN INCLUYENDO AUXILIAR' ,
                                   UN_MODULO    => 96, 
                                   UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' 
          AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                        UN_NOMBRE    => 'MANEJA INTERFACE MENSUAL NIIF POR CENTRO DE COSTO' ,
                                        UN_MODULO    => 96, 
                                        UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' THEN 
            MI_STRAUXILIAR := NVL(AUXILIAR, PCK_DATOS.CONS_AUXILIAR); 
        ELSE 
            MI_STRAUXILIAR := PCK_DATOS.CONS_AUXILIAR ;  
        END IF;

        IF CODIGOELEMENTO IS NULL  THEN 
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB (ELEMENTO|| CHR(13) || CHR(10));
            --VARIABLE CONTEO DE LOG  FALTA
            RETURN MI_RTAPLANO;
        END IF;
        IF ELEMENTO = MI_STRELEMENTOANT AND CODIGOELEMENTO <> MI_STRCODIGOANT THEN
            MI_STRCONTROL:= ELEMENTO||CODIGOELEMENTO;
        ELSE
            MI_STRCONTROL:='*';
        END IF;

        IF ELEMENTO <> MI_STRELEMENTOANT THEN
            MI_STRELEMENTOANT:= ELEMENTO;
        END IF;
        IF CODIGOELEMENTO <> MI_STRCODIGOANT THEN
            MI_STRCODIGOANT:= CODIGOELEMENTO;
        END IF;

        IF MI_STRCONTROL <> ELEMENTO||CODIGOELEMENTO THEN 
            -- GOTO CONTINUAR  REALIZAR EL GOTO 
            --Aquí empieza a llenar la Tabla Plana para luego pasarla a interfaz   
            MI_CURVALOR:= TO_NUMBER((NVL(VALOR, 0) * 100 + 0.001) / 100);  -- VERIFICAR EL CAMPO --VALOR-- EN LA CONSULTA 
            IF MI_CURVALOR <> 0 THEN 
                IF MI_ALMPRO = 0 THEN 
                    MI_CODTERCERO:= CASE 
                                 WHEN TERCERO IS NOT NULL AND SUCURSAL IS NOT NULL
                                 THEN TERCERO
                                 ELSE PCK_DATOS.FC_CONS_TERCERO
                                 END;
                    MI_CODSUCURSAL:=  CASE 
                               WHEN TERCERO IS NOT NULL AND SUCURSAL IS NOT NULL
                               THEN SUCURSAL
                               ELSE PCK_DATOS.FC_CONS_SUCURSAL
                               END;   
                ELSE 
                    MI_CODTERCERO:= CASE 
                                   WHEN PROVEEDORCA  NOT IN('999999999999999999') AND SUCURSALCA NOT IN('999')
                                   THEN PROVEEDORCA
                                   ELSE TERCERO
                                   END;
                    
                    MI_CODSUCURSAL:=  CASE 
                                     WHEN  PROVEEDORCA  NOT IN('999999999999999999') AND SUCURSALCA NOT IN('999')
                                     THEN SUCURSALCA
                                     ELSE  CASE 
                                           WHEN SUCURSAL IS NOT NULL
                                           THEN SUCURSAL
                                           ELSE '001'
                                           END 
                                     END; 
                END IF;
                MI_CENTRODECOSTO:= CASE 
                                   WHEN TRIM(CENTRODECOSTO) IS NOT NULL
                                   THEN CENTRODECOSTO
                                   ELSE PCK_DATOS.CONS_CENTRO
                                 END ;

                MI_FUENTERECURSO:= CASE 
                                   WHEN TRIM(FUENTEDERECURSO) IS NOT NULL
                                   THEN FUENTEDERECURSO
                                   ELSE PCK_DATOS.CONS_FUENTE
                                 END ;
                -- se define la tabla y los campós para la insercion                     
                MI_TABLA := 'TEMP_PLANA_AJUSTES';
                MI_CAMPOS := 'COMPANIA
                          ,ANO
                          ,TIPO_CPTE
                          ,COMPROBANTE
                          ,CONSECUTIVO
                          ,CUENTA
                          ,FECHA
                          ,NATURALEZA
                          ,VALOR_DEBITO
                          ,VALOR_CREDITO
                          ,EJECUCION_DEBITO
                          ,EJECUCION_CREDITO
                          ,CENTRO_COSTO
                          ,TERCERO
                          ,SUCURSAL
                          ,AUXILIAR
                          ,FUENTE_RECURSOS
                          ,D_DEPENDENCIACNT ';
                MI_BOLIND:= -1 ;
                IF MI_DISCRIMINA_IVA = -1 THEN 
                    IF DEBITO_BASE IS NOT NULL OR CREDITO_BASE IS NOT NULL THEN
                        MI_BOLIND := 0;
                        IF DEBITO_BASE IS NOT NULL  THEN
                            MI_CURVALOR:= TO_NUMBER((NVL(SUMVALORBASE, 0) * 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => DEBITO_BASE);
                            -- insercion para debito base                                                  
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                         ,'|| UN_ANO ||'
                                         ,'''|| UN_TIPO ||'''
                                         ,'|| UN_NUMERO ||'
                                         ,'|| MI_CONSECUTIVO ||'
                                         ,'''|| DEBITO_BASE || '''
                                         ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                         ,'''|| MI_NATURALEZA ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CENTRODECOSTO ||'''
                                         ,'''|| MI_CODTERCERO ||'''
                                         ,'''|| MI_CODSUCURSAL ||'''
                                         ,'''|| MI_STRAUXILIAR ||'''
                                         ,'''|| MI_FUENTERECURSO ||'''
                                         ,'''|| DEPENDENCIA_ORIGEN ||''' '; -- VERIFICAR EL CAMPO EN  EL BULK COLLEC Y CONSULTA  
                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;     
                        END IF;               
                        IF CREDITO_BASE IS NOT NULL THEN
                            MI_CURVALOR:= TO_NUMBER((NVL(SUMVALORBASE, 0) * 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => CREDITO_BASE);
                            -- insercion para credito base                                                  
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                         ,'|| UN_ANO ||'
                                         ,'''|| UN_TIPO ||'''
                                         ,'|| UN_NUMERO ||'
                                         ,'|| MI_CONSECUTIVO ||'
                                         ,'''|| CREDITO_BASE || '''
                                         ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                         ,'''|| MI_NATURALEZA ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| MI_CENTRODECOSTO ||'''
                                         ,'''|| MI_CODTERCERO ||'''
                                         ,'''|| MI_CODSUCURSAL ||'''
                                         ,'''|| MI_STRAUXILIAR ||'''
                                         ,'''|| MI_FUENTERECURSO ||'''
                                         ,'''|| DEPENDENCIA_ORIGEN ||''' ';  
                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                        END IF;
                        IF DEBITO_IVA IS NOT NULL THEN
                            MI_CURVALOR:= TO_NUMBER((NVL(SUMVALORIVA, 0) * 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => DEBITO_BASE);
                            -- insercion para debito iva                                                  
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                         ,'|| UN_ANO ||'
                                         ,'''|| UN_TIPO ||'''
                                         ,'|| UN_NUMERO ||'
                                         ,'|| MI_CONSECUTIVO ||'
                                         ,'''|| DEBITO_IVA || '''
                                         ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                         ,'''|| MI_NATURALEZA ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CENTRODECOSTO ||'''
                                         ,'''|| MI_CODTERCERO ||'''
                                         ,'''|| MI_CODSUCURSAL ||'''
                                         ,'''|| MI_STRAUXILIAR ||'''
                                         ,'''|| MI_FUENTERECURSO ||'''
                                         ,'''|| DEPENDENCIA_ORIGEN ||''' ';   
                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                        END IF;
                        IF CREDITO_IVA IS NOT NULL THEN
                            MI_CURVALOR:= TO_NUMBER((NVL(SUMVALORIVA, 0) * 100 + 0.001) / 100); 
                            MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => CREDITO_IVA);
                            -- insercion para credito iva                                                  
                            MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                         ,'|| UN_ANO ||'
                                         ,'''|| UN_TIPO ||'''
                                         ,'|| UN_NUMERO ||'
                                         ,'|| MI_CONSECUTIVO ||'
                                         ,'''|| CREDITO_IVA || '''
                                         ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                         ,'''|| MI_NATURALEZA ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| 0 ||'''
                                         ,'''|| MI_CURVALOR ||'''
                                         ,'''|| MI_CENTRODECOSTO ||'''
                                         ,'''|| MI_CODTERCERO ||'''
                                         ,'''|| MI_CODSUCURSAL ||'''
                                         ,'''|| MI_STRAUXILIAR ||'''
                                         ,'''|| MI_FUENTERECURSO ||'''
                                         ,'''|| DEPENDENCIA_ORIGEN ||''' ';
                            BEGIN
                                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                              UN_ACCION  => 'I',
                                                              UN_CAMPOS  => MI_CAMPOS,
                                                              UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                        END IF; 
                    END IF;
                END IF;
                IF  MI_BOLIND <>0 THEN 
                    IF CUENTADEBITO IS NOT NULL THEN
                        --aquí inserta los detalles del comprobante(cuentadebitar)                    
                        MI_CURVALOR:= TO_NUMBER((NVL(VALOR, 0) * 100 + 0.001) / 100); 
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => CUENTADEBITO);
                        -- insercion para cuenta debito                                                  
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                   ,'|| UN_ANO ||'
                                   ,'''|| UN_TIPO ||'''
                                   ,'|| UN_NUMERO ||'
                                   ,'|| MI_CONSECUTIVO ||'
                                   ,'''|| CUENTADEBITO || '''
                                   ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                   ,'''|| MI_NATURALEZA ||'''
                                   ,'''|| MI_CURVALOR ||'''
                                   ,'''|| 0 ||'''
                                   ,'''|| MI_CURVALOR ||'''
                                   ,'''|| 0 ||'''
                                   ,'''|| MI_CENTRODECOSTO ||'''
                                   ,'''|| MI_CODTERCERO ||'''
                                   ,'''|| MI_CODSUCURSAL ||'''
                                   ,'''|| MI_STRAUXILIAR ||'''
                                   ,'''|| MI_FUENTERECURSO ||'''
                                   ,'''|| DEPENDENCIA_ORIGEN ||''' ';   
                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        END;
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                    END IF ;
                    IF CUENTACREDITO IS NOT NULL THEN
                        --aquí inserta los detalles del comprobante(cuentaacreditar)                    
                        MI_CURVALOR:= TO_NUMBER((NVL(VALOR, 0) * 100 + 0.001) / 100); 
                        MI_NATURALEZA:= PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => CUENTACREDITO);
                        -- insercion para cuenta credito                                                 
                        MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                   ,'|| UN_ANO ||'
                                   ,'''|| UN_TIPO ||'''
                                   ,'|| UN_NUMERO ||'
                                   ,'|| MI_CONSECUTIVO ||'
                                   ,'''|| CUENTACREDITO || '''
                                   ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                   ,'''|| MI_NATURALEZA ||'''
                                   ,'''|| 0 ||'''
                                   ,'''|| MI_CURVALOR ||'''
                                   ,'''|| 0 ||'''
                                   ,'''|| MI_CURVALOR ||'''
                                   ,'''|| MI_CENTRODECOSTO ||'''
                                   ,'''|| MI_CODTERCERO ||'''
                                   ,'''|| MI_CODSUCURSAL ||'''
                                   ,'''|| MI_STRAUXILIAR ||'''
                                   ,'''|| MI_FUENTERECURSO ||'''
                                   ,'''|| DEPENDENCIA_ORIGEN ||''' '; --   
                        BEGIN
                            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;
                            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                        END IF ;
                    END IF; 
                END IF ;
            END IF;
    END LOOP ; 
    MI_STRTEXTO := ' INTERFACE DE ALMACEN A CONTABILIDAD DE LA FECHA:'|| UN_FECHINTERF || '';
    BEGIN 
        --se evalua el parametro y posteriormente se hace una consulta a la funcion FC_CONTABILIZAR        
        MI_RTAPLANO:=  TO_CLOB (PCK_CONTABILIZAR.FC_CONTABILIZAR(  
                                                       UN_COMPANIA         => UN_COMPANIA
                                                      ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                      ,UN_NUMERO           => UN_NUMERO 
                                                      ,UN_ANO              => UN_ANO
                                                      ,UN_FECHA            => UN_FECHINTERF
                                                      ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                      ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                      ,UN_DESCRIPCION      => MI_STRTEXTO
                                                      ,UN_USUARIO          => UN_USUARIO 
                                                      ,UN_SIMPLE           => MI_SIMPLIFICA_ALM
                                                      ,UN_INDIMPRESION     => -1
                                                      ,UN_RESAUXGEN        => MI_ALMDEP
                                                      )) ;        
    END ;

    --(INI_CC:3108_Genera alerta si el proceso de contabilizacion se ejecuto por segunda vez en el mismo periodo)
    BEGIN
        DBMS_LOB.CREATETEMPORARY(MI_LOG_OMITIDOS, TRUE);
        FOR RS_OMITIDOS IN (
             SELECT M.COMPANIA,
                   M.TIPOMOVIMIENTO,
                   M.NUMERO,
                   M.TIPO_CPTE_CONTABLE,
                   M.CPTE_CONTABLE,
                   TO_CHAR(M.FECHA, 'DD/MM/YYYY') AS FECHA_STR
            FROM MOVIMIENTO M
           WHERE M.COMPANIA = UN_COMPANIA
              AND M.ANO = UN_ANO  
              AND TRUNC(M.FECHA) = UN_FECHINTERF
              AND M.TIPO_CPTE_CONTABLE IS NOT NULL  
              AND M.CPTE_CONTABLE IS NOT NULL        
        ) LOOP
            MI_REGISTROS_YA_CONTAB := MI_REGISTROS_YA_CONTAB + 1;
            
            -- Agregar cada registro en una línea
            DBMS_LOB.APPEND(
                MI_LOG_OMITIDOS,
                'Compañía: ' || RS_OMITIDOS.COMPANIA
            || ', Tipo Movimiento: ' || RS_OMITIDOS.TIPOMOVIMIENTO
            || ', Número Movimiento: ' || RS_OMITIDOS.NUMERO
            || ', Ya contabilizado con tipo comprobante: ' || RS_OMITIDOS.TIPO_CPTE_CONTABLE
            || ', Comprobante: ' || RS_OMITIDOS.CPTE_CONTABLE
            || ', Fecha: ' || RS_OMITIDOS.FECHA_STR
            || CHR(10)
            );
        END LOOP;
        
        IF MI_REGISTROS_YA_CONTAB > 0 THEN
            -- Agregar separación antes del mensaje
            MI_RTAPLANO := MI_RTAPLANO || CHR(10) || CHR(10);
            
            -- Construir el mensaje línea por línea
            MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || 'REGISTROS YA CONTABILIZADOS (OMITIDOS)' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || '========================================' || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || 'Total registros omitidos: ' || MI_REGISTROS_YA_CONTAB || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || CHR(10);
            MI_RTAPLANO := MI_RTAPLANO || MI_LOG_OMITIDOS;
            MI_RTAPLANO := MI_RTAPLANO || CHR(10);
        END IF;
    END; --(FIN_CC:3108_FIN_CFBARRERA)
    DBMS_LOB.FREETEMPORARY(MI_LOG_OMITIDOS);

    --(INI_CC:3108 Registra el comprobante y el numero al realizar la contabilizacion)
        MI_TABLA := 'MOVIMIENTO';

        MI_CAMPOS := 'TIPO_CPTE_CONTABLE = CASE 
                          WHEN TIPO_CPTE_CONTABLE IS NULL THEN ''' || UN_TIPO || ''' 
                          ELSE TIPO_CPTE_CONTABLE 
                      END
                     ,CPTE_CONTABLE = CASE 
                          WHEN CPTE_CONTABLE IS NULL THEN ' || UN_NUMERO || ' 
                          ELSE CPTE_CONTABLE 
                      END
                     ,REGISTRADO = CASE 
                          WHEN REGISTRADO = 0 THEN -1 
                          ELSE REGISTRADO 
                      END';

        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                        AND ANO = ' || UN_ANO || '
                        AND FECHA = TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')';
            
        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_CONDICION => MI_CONDICION);
        
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
    END;--(FIN_CC:3108)
    RETURN MI_RTAPLANO;

 END FC_CONTABILIZARHNIVELESCC; 

 --7
 FUNCTION FC_CONTBLIZARARMCONSLTHNVLES 
 /*  
        NAME              : no aplica
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : 
        DATE MIGRADOR     : 13/07/2018
        TIME              : 04:37 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Esta funcion retorna la consulta elaborada para usarla en la funcion contabilizarHNiveles 
                            y la misma consulta final es usada para generar un excel plano
        PARAMETERS        :
        MODIFICATIONS     :

      @NAME:contabilizarArmConsltHNvles
      @METHOD:
*/  
        (
           UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
          ,UN_FECHINTERF    IN  DATE 
          ,UN_NIIF          IN  PCK_SUBTIPOS.TI_LOGICO
        ) 

RETURN CLOB 

AS 
     MI_CONSULTA                     CLOB;                  --variable que se le asignan la consulta elaborada
     MI_FECHAINTERFAZ                VARCHAR2(12);
     MI_IVA_DISCRI                   PCK_SUBTIPOS.TI_STRSQL;
     MI_MANEJA_TER                   PCK_SUBTIPOS.TI_STRSQL; 
BEGIN
    MI_IVA_DISCRI:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN' ,
                                              UN_MODULO    => 96, 
                                              UN_FECHA_PAR => SYSDATE ), 'NO');
                                              
    MI_MANEJA_TER:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'MANEJA TERCERO VARIOS EN INTERFAZ DIARIA DE ALMACEN' ,
                                              UN_MODULO    => 96, 
                                              UN_FECHA_PAR => SYSDATE ), 'NO');
                                              
    MI_FECHAINTERFAZ:= TO_CHAR (UN_FECHINTERF,'DD/MM/YYYY'); 

 -- AL EJECUTAR LA INTERFAZ NO ESTABA TENIENDO EN CUENTA LAS DONACIONES (TRASLADOS ENTRE CLASE DE BODEGAS DISTINTAS Y CONCEPTO T)
 -- SE DEBE CONDICIONAR TAMBIEN MOVIMIENTOS ENTRE CLASES DE BODEGAS DISTINTAS Y CONCEPTO DS. CAMBIO ASESORADO POR JUAN CARLOS FORERO - 06/02/2019 - SDAZA
 -- SE ELIMINA CASE WHEN  WHEN (MOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40,50) AND MOVIMIENTO.CLASE_BODEGA_DESTINO IN(60))  OR TIPOMOVIMIENTO.CONCEPTO IN(''T'',''DS'',''L'',''LS'') 
 -- POR QUE NO ESTA SUBIENDO A LA INTERFACE LOS COMPROBANTES DE 'RDI','TDR','DES Y 'DER' 03/04/2018 RMEDINA SOLICITUD JUNA CARLOS FORERO PARA IDSN 03/04/2019.
 -- AL ELIMINAR NO SE DEBE PONER EN FUNCIONAMIENTO LA INTERFACE DE RETIRO DE ACTIVOS, SI SE PONE EN FUNCIONAMIENTO ES NECESARIO ELIMINAR
 -- DE LA CONFIGURACION DE TRANSACCIONES LOS COMPROBANTES CON CONCEPTO L (FALTANTES NO JUSTIFICADOS) DS (SALIDA A INSERVIBLES) CM(COMODATO)
 -- Y LOS COMPROBANTES DE SALIDA CON CLASE S Y CONCEPTO T.
    MI_CONSULTA := ' SELECT D_MOVIMIENTO.TIPOMOVIMIENTO,
                           D_MOVIMIENTO.ELEMENTO,
                           ALMACENCONTABILIDAD.CODIGOELEMENTO,                                 
                           CASE WHEN (MOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40,50) AND MOVIMIENTO.CLASE_BODEGA_DESTINO IN(60)) 
                                   OR TIPOMOVIMIENTO.CONCEPTO IN(''T'',''DS'',''L'',''LS'')    
                                THEN ''999999999999999999''
                                ELSE CASE WHEN (TIPOMOVIMIENTO.CLASE= ''S'' AND ''' || MI_MANEJA_TER || ''' = ''SI'') THEN MOVIMIENTO.TERCERO 
                                ELSE CASE WHEN TIPOMOVIMIENTO.CLASE NOT IN(''S'') THEN MOVIMIENTO.TERCERO ELSE MOVIMIENTO.RESPONSABLE_DESTINO END END END TERCERO,
                           CASE WHEN (MOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40,50) AND MOVIMIENTO.CLASE_BODEGA_DESTINO IN(60))
                                   OR TIPOMOVIMIENTO.CONCEPTO IN(''T'',''DS'',''L'',''LS'')  
                                THEN ''999''                
                                ELSE CASE WHEN (TIPOMOVIMIENTO.CLASE= ''S'' AND ''' || MI_MANEJA_TER || ''' = ''SI'') THEN MOVIMIENTO.SUCURSAL 
                                ELSE CASE WHEN TIPOMOVIMIENTO.CLASE NOT IN(''S'') THEN MOVIMIENTO.SUCURSAL ELSE MOVIMIENTO.SUCURSAL_RESDESTINO END END END SUCURSAL,
                           MOVIMIENTO.PROVEEDORCA, 
                           MOVIMIENTO.SUCURSALCA,     
                           D_MOVIMIENTO.CENTRODECOSTO,
                           MOVIMIENTO.REFERENCIA,
                           MOVIMIENTO.FUENTEDERECURSO FUENTE_RECURSO,
                           SUM(' ||CASE WHEN  UN_NIIF  NOT IN(0) THEN ' NIIF_VALOR_BASE ' ELSE ' VALORBASE '  END || ')                   SUMVALORBASE,
                           SUM(' ||CASE WHEN  UN_NIIF  NOT IN(0) THEN ' 0 ' ELSE ' D_MOVIMIENTO.VALORIVA '  END || ')                     SUMVALORIVA, 
                           SUM(' ||CASE WHEN  UN_NIIF  NOT IN(0) THEN ' NIIF_VALOR_TOTAL ' ELSE ' D_MOVIMIENTO.VALORTOTAL '  END || ')    VALOR,
                               ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CUENTADEBITO '  ELSE ' ALMACENCONTABILIDAD.CUENTADEBITO '  END || ' CUENTADEBITO, 
                               ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CUENTACREDITO ' ELSE ' ALMACENCONTABILIDAD.CUENTACREDITO ' END || ' CUENTACREDITO,
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_DEBITO_BASE  ' ELSE ' DEBITO_BASE '  END || ' END  DEBITO_BASE, 
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CREDITO_BASE ' ELSE ' CREDITO_BASE ' END || ' END  CREDITO_BASE,
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_DEBITO_IVA '   ELSE ' DEBITO_IVA '   END || ' END  DEBITO_IVA, 
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CREDITO_IVA '  ELSE ' CREDITO_IVA '  END || ' END  CREDITO_IVA,
                           ''' || MI_IVA_DISCRI || ''' DISCRIMINA_IVA
                    FROM   MOVIMIENTO INNER JOIN D_MOVIMIENTO
                        ON MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                       AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                       AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
                    INNER JOIN TIPOMOVIMIENTO 
                      ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                     AND TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                    INNER JOIN ALMACENCONTABILIDAD 
                      ON D_MOVIMIENTO.COMPANIA       = ALMACENCONTABILIDAD.COMPANIA
                     AND D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDAD.TIPOMOVIMIENTO
                     AND SUBSTR(D_MOVIMIENTO.ELEMENTO,1,LENGTH(ALMACENCONTABILIDAD.CODIGOELEMENTO)) = ALMACENCONTABILIDAD.CODIGOELEMENTO
                    WHERE D_MOVIMIENTO.COMPANIA  = ''' || UN_COMPANIA || ''' 
                      AND TRUNC(D_MOVIMIENTO.FECHA)=TO_DATE(''' || MI_FECHAINTERFAZ || ''',''DD/MM/YYYY'') 
                      AND IND_REG   NOT IN(0)
                      AND ALMACENCONTABILIDAD.ANO = '|| TO_NUMBER(TO_CHAR(UN_FECHINTERF,'YYYY')) || '
                      AND NOT (TIPOMOVIMIENTO.CONCEPTO IN (''T'') AND TIPOMOVIMIENTO.CLASE IN(''T''))
                    GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO,
                             D_MOVIMIENTO.ELEMENTO,
                             ALMACENCONTABILIDAD.CODIGOELEMENTO,
                             CASE WHEN (MOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40,50) AND MOVIMIENTO.CLASE_BODEGA_DESTINO IN(60)) 
                                     OR TIPOMOVIMIENTO.CONCEPTO IN(''T'',''DS'',''L'',''LS'')    
                                THEN ''999999999999999999''
                                ELSE CASE WHEN (TIPOMOVIMIENTO.CLASE= ''S'' AND ''' || MI_MANEJA_TER || ''' = ''SI'') THEN MOVIMIENTO.TERCERO 
                                ELSE CASE WHEN TIPOMOVIMIENTO.CLASE NOT IN(''S'') THEN MOVIMIENTO.TERCERO ELSE MOVIMIENTO.RESPONSABLE_DESTINO END END END,
                             CASE WHEN (MOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40,50) AND MOVIMIENTO.CLASE_BODEGA_DESTINO IN(60))
                                   OR TIPOMOVIMIENTO.CONCEPTO IN(''T'',''DS'',''L'',''LS'')  
                                THEN ''999''                
                                ELSE CASE WHEN (TIPOMOVIMIENTO.CLASE= ''S'' AND ''' || MI_MANEJA_TER || ''' = ''SI'') THEN MOVIMIENTO.SUCURSAL 
                                ELSE CASE WHEN TIPOMOVIMIENTO.CLASE NOT IN(''S'') THEN MOVIMIENTO.SUCURSAL ELSE MOVIMIENTO.SUCURSAL_RESDESTINO END END END,
                             D_MOVIMIENTO.CENTRODECOSTO,
                             MOVIMIENTO.REFERENCIA,
                             MOVIMIENTO.FUENTEDERECURSO,
                             MOVIMIENTO.PROVEEDORCA, 
                             MOVIMIENTO.SUCURSALCA,
                             ' || CASE WHEN UN_NIIF NOT IN(0) THEN '  NIIF_CUENTADEBITO  ' ELSE ' ALMACENCONTABILIDAD.CUENTADEBITO  ' END || ', 
                             ' || CASE WHEN UN_NIIF NOT IN(0) THEN '  NIIF_CUENTACREDITO ' ELSE ' ALMACENCONTABILIDAD.CUENTACREDITO ' END || ' ,
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_DEBITO_BASE  ' ELSE ' DEBITO_BASE '  END || ' END , 
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CREDITO_BASE ' ELSE ' CREDITO_BASE ' END || ' END ,
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_DEBITO_IVA '   ELSE ' DEBITO_IVA '   END || ' END , 
                           CASE WHEN TIPOMOVIMIENTO.CONCEPTO IN(''L'',''LS'') OR (MOVIMIENTO.CLASE_BODEGA_ORIGEN = MOVIMIENTO.CLASE_BODEGA_DESTINO AND TIPOMOVIMIENTO.CONCEPTO IN(''T'', ''DS'')) THEN '' '' ELSE ' || CASE WHEN UN_NIIF NOT IN(0) THEN ' NIIF_CREDITO_IVA '  ELSE ' CREDITO_IVA '  END || ' END ,
                           ''' || MI_IVA_DISCRI || '''';   
  RETURN MI_CONSULTA;

 END FC_CONTBLIZARARMCONSLTHNVLES;

 --8
  FUNCTION FC_CONTBLIZARARMCONSLTHNVLESCC 
/*   
        NAME              : No aplica  
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : 
        DATE MIGRADOR     : 19/06/2018
        TIME              : 12:37 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Esta funcion retorna la consulta elaborada para usarla en la funcion InterfaceAlmacenHNivelesCC
                            y la misma consulta final es usada para generar un excel plano
        PARAMETERS        :
        MODIFICATIONS     :

      @NAME:contabilizarArmConsltHNvlesCC
      @METHOD:
*/        
      (
        UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
       ,UN_FECHINTERF    IN  DATE 
       ,UN_NIIF          IN  PCK_SUBTIPOS.TI_LOGICO   -- 'check habilita la consulta para NIIF'
      )

RETURN CLOB 

AS 
        MI_ALMDEP                       PCK_SUBTIPOS.TI_LOGICO; -- variable de almacenamiento si corresponde el parametro parametro 
        MI_ALMPRO                       PCK_SUBTIPOS.TI_LOGICO; -- variable de almacenamiento si corresponde el parametro parametro 
        MI_CONSULTA                     CLOB;      -- variable que contiene la consulta compuesta

        MI_FECHAINTERFAZ                VARCHAR2(12);

/*
        MI_SELECTMOV                    VARCHAR2(32000);         --variable que se le asigna el SELECT compuesto
        MI_GRUPMOV                      VARCHAR2(32000);         --variable que se le asigna el GROUP BY compuesto
        MI_FILTROMOV                    VARCHAR2(32000);         --variable que se le asigna el WHERE compuesto
        MI_FROMMOV                      VARCHAR2(32000);         --variable que se le asigna el FROM compuesto
  */      
        MI_SELECTMOV                    CLOB;         --variable que se le asigna el SELECT compuesto
        MI_GRUPMOV                      CLOB;         --variable que se le asigna el GROUP BY compuesto
        MI_FILTROMOV                    CLOB;         --variable que se le asigna el WHERE compuesto
        MI_FROMMOV                      CLOB;         --variable que se le asigna el FROM compuesto

BEGIN
       MI_ALMDEP:= CASE 
                   WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                  UN_NOMBRE    => 'INTERFAZ DE ALMACEN CON DEPENDENCIA' ,
                                                  UN_MODULO    => 96, 
                                                  UN_FECHA_PAR => SYSDATE ), 'NO') = 'NO' 
                   THEN 0 
                   ELSE -1
                   END; 


       MI_ALMPRO:= CASE 
                   WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                  UN_NOMBRE    => 'MOSTRAR PROVEEDOR EN SALIDAS DE ALMACEN' ,
                                                  UN_MODULO    => 96, 
                                                  UN_FECHA_PAR => SYSDATE ), 'NO') = 'NO' 
                   THEN 0 
                   ELSE -1
                   END;  


          -- se verica si el check de NIIF esta habilitado y se arma la correspondiente consulta 
          IF UN_NIIF <>0 THEN 

             MI_SELECTMOV:= 'SELECT D_MOVIMIENTO.TIPOMOVIMIENTO,
                              D_MOVIMIENTO.ELEMENTO,
                              ALMACENCONTABILIDADCC.CODIGOELEMENTO,
                              SUM(D_MOVIMIENTO.VALORTOTAL) AS VALOR,
                              CASE 
                                  WHEN D_MOVIMIENTO.PROVEEDORCA =''999999999999999999''
                                  THEN MOVIMIENTO.TERCERO
                                  ELSE D_MOVIMIENTO.PROVEEDORCA
                              END TERCERO,
                              CASE 
                                  WHEN D_MOVIMIENTO.SUCURSALCA = ''999''
                                  THEN MOVIMIENTO.SUCURSAL
                                  ELSE D_MOVIMIENTO.SUCURSALCA
                              END SUCURSAL, 
                              D_MOVIMIENTO.CENTRODECOSTO,
                              D_MOVIMIENTO.FUENTEDERECURSO,
                              ALMACENCONTABILIDADCC.NIIF_CUENTADEBITO AS CUENTADEBITO,
                              ALMACENCONTABILIDADCC.NIIF_CUENTACREDITO AS CUENTACREDITO, 
                              ALMACENCONTABILIDADCC.NIIF_DEBITO_BASE AS DEBITO_BASE,
                              ALMACENCONTABILIDADCC.NIIF_CREDITO_IVA AS CREDITO_IVA,
                              ALMACENCONTABILIDADCC.NIIF_DEBITO_IVA AS DEBITO_IVA,
                              ALMACENCONTABILIDADCC.NIIF_CREDITO_BASE AS CREDITO_BASE,
                              SUM(NVL(D_MOVIMIENTO.VALORBASE,0))AS SUMVALORBASE,
                              SUM(NVL(D_MOVIMIENTO.VALORIVA,0))AS SUMVALORIVA'; 

             MI_GRUPMOV:= ' GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO, 
                           D_MOVIMIENTO.ELEMENTO,
                           ALMACENCONTABILIDADCC.CODIGOELEMENTO,
                           MOVIMIENTO.TERCERO,
                           MOVIMIENTO.SUCURSAL,
                           D_MOVIMIENTO.CENTRODECOSTO,
                           D_MOVIMIENTO.FUENTEDERECURSO,
                           ALMACENCONTABILIDADCC.NIIF_CUENTADEBITO,
                           ALMACENCONTABILIDADCC.NIIF_CUENTACREDITO,
                           ALMACENCONTABILIDADCC.NIIF_DEBITO_BASE,
                           ALMACENCONTABILIDADCC.NIIF_CREDITO_IVA,
                           ALMACENCONTABILIDADCC.NIIF_DEBITO_IVA,
                           CASE
                            WHEN D_MOVIMIENTO.PROVEEDORCA =''999999999999999999''
                            THEN MOVIMIENTO.TERCERO
                            ELSE D_MOVIMIENTO.PROVEEDORCA
                           END,
                           CASE
                            WHEN D_MOVIMIENTO.SUCURSALCA = ''999''
                            THEN MOVIMIENTO.SUCURSAL
                            ELSE D_MOVIMIENTO.SUCURSALCA
                           END,
                           ALMACENCONTABILIDADCC.NIIF_CREDITO_BASE' ;

          ELSE 

               MI_SELECTMOV:= 'SELECT D_MOVIMIENTO.TIPOMOVIMIENTO, 
                                      D_MOVIMIENTO.ELEMENTO,
                                      ALMACENCONTABILIDADCC.CODIGOELEMENTO,  
                                      SUM(D_MOVIMIENTO.VALORTOTAL) AS VALOR,   
                                      CASE 
                                          WHEN D_MOVIMIENTO.PROVEEDORCA =''999999999999999999''
                                          THEN MOVIMIENTO.TERCERO
                                          ELSE D_MOVIMIENTO.PROVEEDORCA
                                      END TERCERO,
                                      CASE 
                                          WHEN D_MOVIMIENTO.SUCURSALCA = ''999''
                                          THEN MOVIMIENTO.SUCURSAL
                                          ELSE D_MOVIMIENTO.SUCURSALCA
                                      END SUCURSAL,
                                      D_MOVIMIENTO.CENTRODECOSTO, 
                                      D_MOVIMIENTO.FUENTEDERECURSO,
                                      ALMACENCONTABILIDADCC.CUENTADEBITO,
                                      ALMACENCONTABILIDADCC.CUENTACREDITO, 
                                      ALMACENCONTABILIDADCC.DEBITO_BASE,
                                      ALMACENCONTABILIDADCC.CREDITO_IVA,
                                      ALMACENCONTABILIDADCC.DEBITO_IVA,  
                                      ALMACENCONTABILIDADCC.CREDITO_BASE,
                                      SUM(NVL(D_MOVIMIENTO.VALORBASE,0))AS SUMVALORBASE,
                                      SUM(NVL(D_MOVIMIENTO.VALORIVA,0))AS SUMVALORIVA'; 

               MI_GRUPMOV:=  ' GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO, 
                                       D_MOVIMIENTO.ELEMENTO,
                                       ALMACENCONTABILIDADCC.CODIGOELEMENTO,
                                       MOVIMIENTO.TERCERO, 
                                       MOVIMIENTO.SUCURSAL,  
                                       D_MOVIMIENTO.CENTRODECOSTO,
                                       D_MOVIMIENTO.FUENTEDERECURSO,
                                       ALMACENCONTABILIDADCC.CUENTADEBITO, 
                                       ALMACENCONTABILIDADCC.CUENTACREDITO, 
                                       ALMACENCONTABILIDADCC.DEBITO_BASE, 
                                       ALMACENCONTABILIDADCC.CREDITO_IVA, 
                                       ALMACENCONTABILIDADCC.DEBITO_IVA, 
                                       CASE
                                          WHEN D_MOVIMIENTO.PROVEEDORCA =''999999999999999999''
                                          THEN MOVIMIENTO.TERCERO
                                          ELSE D_MOVIMIENTO.PROVEEDORCA
                                        END,
                                        CASE
                                          WHEN D_MOVIMIENTO.SUCURSALCA = ''999''
                                          THEN MOVIMIENTO.SUCURSAL
                                          ELSE D_MOVIMIENTO.SUCURSALCA
                                        END,
                                       ALMACENCONTABILIDADCC.CREDITO_BASE ';

          END IF ; 
                 -- se formatea fecha de interfaz 
                 MI_FECHAINTERFAZ:= TO_CHAR(UN_FECHINTERF,'DD/MM/YYYY');
                 -- se asigna el WHERE de la consulta
                 MI_FILTROMOV := ' WHERE D_MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''   
                                       AND SUBSTR(D_MOVIMIENTO.ELEMENTO,1,LENGTH(ALMACENCONTABILIDADCC.CODIGOELEMENTO))=ALMACENCONTABILIDADCC.CODIGOELEMENTO 
                                       AND ALMACENCONTABILIDADCC.ANO = TO_CHAR((D_MOVIMIENTO.FECHA),''YYYY'')
                                       AND D_MOVIMIENTO.FECHA = TO_DATE(''' || MI_FECHAINTERFAZ ||''', ''DD/MM/YYYY'')
                                       AND D_MOVIMIENTO.IND_REG NOT IN (0)'; 

              -- se valida el parametro MI_ALMDEP si corresponde se le agrega campos a los select y grup by                         
              IF MI_ALMDEP <> 0 THEN

                 MI_SELECTMOV := MI_SELECTMOV ||', MOVIMIENTO.DEPENDENCIA_ORIGEN ' ||'';

                 MI_GRUPMOV := MI_GRUPMOV ||', MOVIMIENTO.DEPENDENCIA_ORIGEN ' ||'' ; 

                 -- VERIFICAR EL FROM QUE CUMPLA PARA TODOS 

                 MI_FROMMOV:= ' FROM (D_MOVIMIENTO 
                                            LEFT JOIN ALMACENCONTABILIDADCC 
                                                   ON (D_MOVIMIENTO.COMPANIA = ALMACENCONTABILIDADCC.COMPANIA) 
                                                  AND (D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDADCC.TIPOMOVIMIENTO) 
                                                  AND (D_MOVIMIENTO.CENTRODECOSTO = ALMACENCONTABILIDADCC.CENTRO_COSTO)
                                                  AND (D_MOVIMIENTO.FUENTEDERECURSO = ALMACENCONTABILIDADCC.FUENTEDERECURSO))  
                                                INNER JOIN MOVIMIENTO 
                                                        ON (D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA) 
                                                       AND (D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO) 
                                                       AND (D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO) ' ;

              ELSE       
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                     UN_NOMBRE    => 'MANEJA INTERFACE DE ALMACEN INCLUYENDO AUXILIAR' ,
                                                     UN_MODULO    => 96, 
                                                     UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' 
                              AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                            UN_NOMBRE    => 'MANEJA INTERFACE MENSUAL NIIF POR CENTRO DE COSTO' ,
                                                            UN_MODULO    => 96, 
                                                            UN_FECHA_PAR => SYSDATE ), 'NO') = 'SI' THEN 

                              MI_SELECTMOV := MI_SELECTMOV ||', INVENTARIO.AUXILIAR ' ||'';

                              MI_GRUPMOV := MI_GRUPMOV ||', INVENTARIO.AUXILIAR ' ||'' ; 

                              -- VERIFICAR EL FROM QUE CUMPLA PARA TODOS 

                              MI_FROMMOV:= ' FROM (D_MOVIMIENTO 
                                                LEFT JOIN ALMACENCONTABILIDADCC 
                                                       ON (D_MOVIMIENTO.COMPANIA = ALMACENCONTABILIDADCC.COMPANIA) 
                                                      AND (D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDADCC.TIPOMOVIMIENTO) 
                                                      AND (D_MOVIMIENTO.CENTRODECOSTO = ALMACENCONTABILIDADCC.CENTRO_COSTO)
                                                      AND (D_MOVIMIENTO.FUENTEDERECURSO = ALMACENCONTABILIDADCC.FUENTEDERECURSO))  
                                                     LEFT JOIN INVENTARIO  
                                                            ON (D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA) 
                                                            AND (D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO)
                                                             INNER JOIN MOVIMIENTO 
                                                        ON (D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA) 
                                                       AND (D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO) 
                                                       AND (D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO)';                  
                        ELSE 

                              MI_SELECTMOV := MI_SELECTMOV ||', '' '' AS DEPENDENCIAS  ' ||'';


                              MI_FROMMOV:= ' FROM D_MOVIMIENTO 
                                               LEFT JOIN ALMACENCONTABILIDADCC 
                                                      ON (D_MOVIMIENTO.COMPANIA = ALMACENCONTABILIDADCC.COMPANIA) 
                                                     AND (D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDADCC.TIPOMOVIMIENTO) 
                                                     AND (D_MOVIMIENTO.CENTRODECOSTO= ALMACENCONTABILIDADCC.CENTRO_COSTO)
                                                     AND (D_MOVIMIENTO.FUENTEDERECURSO = ALMACENCONTABILIDADCC.FUENTEDERECURSO)
                                                      INNER JOIN MOVIMIENTO 
                                                        ON (D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA) 
                                                       AND (D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO) 
                                                       AND (D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO)'; 

                        END IF;

              END IF; 
                  IF MI_ALMPRO <> 0 THEN 
                     MI_SELECTMOV := MI_SELECTMOV ||', D_MOVIMIENTO.PROVEEDORCA, D_MOVIMIENTO.SUCURSALCA ' ||'';

                     MI_GRUPMOV := MI_GRUPMOV ||', D_MOVIMIENTO.PROVEEDORCA, D_MOVIMIENTO.SUCURSALCA ' ||'' ; 

                  END IF ; 
              -- se integra la consulta armada 
              MI_CONSULTA:= MI_SELECTMOV || MI_FROMMOV  || MI_FILTROMOV || MI_GRUPMOV ||'' ;

  RETURN MI_CONSULTA;

 END FC_CONTBLIZARARMCONSLTHNVLESCC;

  FUNCTION FC_CONTABILIZARRETIROACTIVOS 
/*  
        NAME              : En Access InterfaceAlmacenRetitoActivos
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 05/11/2018
        TIME              : 08:17 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2018.10.03
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:InterfaceAlmacenRetitoActivos
        @METHOD:Post
*/   
     (   
        UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
       ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
       ,UN_MES           IN  PCK_SUBTIPOS.TI_MES
       ,UN_FECHINTERF    IN  DATE 
       ,UN_TIPO          IN  VARCHAR2     
       ,UN_NUMERO        IN  NUMBER
       ,UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
     ) 
RETURN CLOB
    AS
    MI_NIIF            VARCHAR2(32);
    MI_COMPANIADESTINO VARCHAR2(32);
    MI_DIGITOS         NUMBER;
    MI_PARAMETRO       VARCHAR2(150);
    MI_DATOS           PCK_SUBTIPOS.TI_ENTERO;
    MI_RTAPLANO        CLOB; 
    MI_RTATEM          CLOB; 
    MI_CONSECUTIVO     PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_REGISTROS_YA_CONTAB     NUMBER := 0;  --(CC:3105 CFBARRERA: Almacena la cantidad de registros contabilizados)
    MI_LOG_OMITIDOS            CLOB := '';   --(CC:3105 CFBARRERA: Guarda el mensaje de los registros ya contabilizados)    
BEGIN 
    MI_DATOS :=0;
    MI_RTAPLANO := '';
    MI_CONSECUTIVO:=0;
    MI_COMPANIADESTINO := UN_COMPANIA;
    BEGIN
        MI_PARAMETRO:= 'MANEJA NIIF EN ALMACEN';
        MI_NIIF := NVL(PCK_SYSMAN_UTL.FC_PAR
                       (UN_COMPANIA    => UN_COMPANIA
                       ,UN_NOMBRE      => MI_PARAMETRO
                       ,UN_MODULO      => PCK_DATOS.MODULOALMACEN
                       ,UN_FECHA_PAR   => UN_FECHINTERF ), 'NO');

        MI_PARAMETRO:= 'DIGITOS AGRUPACION INVENTARIO';
        MI_DIGITOS := NVL(PCK_SYSMAN_UTL.FC_PAR
                       (UN_COMPANIA    => UN_COMPANIA
                       ,UN_NOMBRE      => MI_PARAMETRO
                       ,UN_MODULO      => PCK_DATOS.MODULOALMACEN
                       ,UN_FECHA_PAR   => UN_FECHINTERF ), '0');

        IF MI_NIIF ='SI'  THEN
            MI_PARAMETRO:= 'COMPANIA PARA INSERTAR COMPROBANTE ALMACEN';
            MI_COMPANIADESTINO := NVL(PCK_SYSMAN_UTL.FC_PAR
                       (UN_COMPANIA    => UN_COMPANIA
                       ,UN_NOMBRE      => MI_PARAMETRO
                       ,UN_MODULO      => PCK_DATOS.MODULOALMACEN
                       ,UN_FECHA_PAR   => UN_FECHINTERF ), 'ERRADO');  
            IF MI_COMPANIADESTINO = 'ERRADO' THEN
                MI_COMPANIADESTINO:= UN_COMPANIA;
            END IF;
        END IF;

    EXCEPTION WHEN OTHERS THEN
      MI_MSGERROR(1).CLAVE := 'PARAMETRO';
      MI_MSGERROR(1).VALOR := MI_PARAMETRO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>-20000,
            UN_ERROR_COD=>PCK_ERRORES.ERR_INICIARPARAMETROSOI,
            UN_TABLAERROR =>'PR_INICIARPARAMETROSPLANOSOI',
            UN_REEMPLAZOS => MI_MSGERROR
          ); 
    END;
    --(INI_CC:3105_Genera alerta si el proceso de contabilizacion se ejecuto por segunda vez en el mismo periodo)
     BEGIN
        FOR RS_OMITIDOS IN (
             SELECT D.ELEMENTO,
               D.SERIE,
               D.TIPO_CPTE_CONTABLE,
               D.CPTE_CONTABLE,
               TO_CHAR(D.PERIODO, 'DD/MM/YYYY') AS PERIODO_STR
        FROM DEPRECIAR D
       WHERE D.COMPANIA = UN_COMPANIA
          AND TO_NUMBER(TO_CHAR(D.PERIODO,'YYYY')) = UN_ANO  
          AND TO_NUMBER(TO_CHAR(D.PERIODO,'MM')) = UN_MES    
          AND D.PERIODO = UN_FECHINTERF
          AND D.CONSECUTIVO_PROCESO >= 2
          AND D.TIPO_CPTE_CONTABLE IS NOT NULL  
          AND D.CPTE_CONTABLE IS NOT NULL       
        ) LOOP
            MI_REGISTROS_YA_CONTAB := MI_REGISTROS_YA_CONTAB + 1;
            
            MI_LOG_OMITIDOS := MI_LOG_OMITIDOS || TO_CLOB(
                '  - Elemento: ' || RS_OMITIDOS.ELEMENTO || 
                ', Serie: ' || RS_OMITIDOS.SERIE ||
                ', Ya contabilizado con Tipo: ' || RS_OMITIDOS.TIPO_CPTE_CONTABLE ||
                ', Número: ' || RS_OMITIDOS.CPTE_CONTABLE ||
                ', Período: ' || RS_OMITIDOS.PERIODO_STR ||
                CHR(13) || CHR(10)
            );
        END LOOP;
        
        IF MI_REGISTROS_YA_CONTAB > 0 THEN
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(
                '========================================' || CHR(13) || CHR(10) ||
                'REGISTROS YA CONTABILIZADOS (OMITIDOS)' || CHR(13) || CHR(10) ||
                '========================================' || CHR(13) || CHR(10) ||
                'Total registros omitidos: ' || MI_REGISTROS_YA_CONTAB || CHR(13) || CHR(10) ||
                CHR(13) || CHR(10) ||
                MI_LOG_OMITIDOS ||
                CHR(13) || CHR(10)
            );
        END IF;
    END;    --(INI_CC:3105_FIN_CFBARRERA)

    <<MOVIMIENTOSACONTABILIZAR>>
    FOR RS IN (
                SELECT DM.COMPANIA, 
                       DM.TIPOMOVIMIENTO, 
                       SUBSTR(DM.ELEMENTO,1, MI_DIGITOS) GRUPO, 
                       TO_NUMBER(TO_CHAR(FECHA,'MM'))  MES, 
                       TO_NUMBER(TO_CHAR(FECHA,'YYYY')) ANO,
                       SUM(CASE WHEN MI_NIIF ='SI' THEN DM.NIIF_VALOR_TOTAL ELSE DM.VALORTOTAL  END) VALOR_TOTAL,
                       SUM(CASE WHEN MI_NIIF ='SI' THEN D.NIIF_DEPACUMULADA ELSE D.DEPACUMULADA END) DEPACUMULADA, 
                       SUM(CASE WHEN MI_NIIF ='SI' THEN D.NIIF_VLRLIBROS    ELSE D.VLRLIBROS    END) VLRLIBROS, 
                       AC.CODIGOELEMENTO, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_HISTORICO_BAJA  ELSE AC.DEBITO_HISTORICO_BAJA  END DEBITO_HISTORICO_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_HISTORICO_BAJA ELSE AC.CREDITO_HISTORICO_BAJA END CREDITO_HISTORICO_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_ACUMULADA_BAJA  ELSE AC.DEBITO_ACUMULADA_BAJA  END DEBITO_ACUMULADA_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_ACUMULADA_BAJA ELSE AC.CREDITO_ACUMULADA_BAJA END CREDITO_ACUMULADA_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_LIBROS_BAJA     ELSE AC.DEBITO_LIBROS_BAJA     END DEBITO_LIBROS_BAJA,
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_LIBROS_BAJA    ELSE AC.CREDITO_LIBROS_BAJA    END CREDITO_LIBROS_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_RETIRADOS_BAJA  ELSE AC.DEBITO_RETIRADOS_BAJA  END DEBITO_RETIRADOS_BAJA, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_RETIRADOS_BAJA ELSE AC.CREDITO_RETIRADOS_BAJA END CREDITO_RETIRADOS_BAJA
                FROM DEPRECIAR D INNER JOIN D_MOVIMIENTO DM 
                  ON D.COMPANIA = DM.COMPANIA 
                 AND D.ELEMENTO = DM.ELEMENTO 
                 AND D.SERIE    = DM.SERIE
                INNER JOIN TIPOMOVIMIENTO T
                  ON DM.COMPANIA       = T.COMPANIA 
                 AND DM.TIPOMOVIMIENTO = T.CODIGO
                INNER JOIN ALMACENCONTABILIDAD AC  
                  ON T.CODIGO   = AC.TIPOMOVIMIENTO 
                 AND T.COMPANIA = AC.COMPANIA 
                WHERE D.COMPANIA   = UN_COMPANIA
                  AND AC.ANO       = UN_ANO
                  AND TO_NUMBER(TO_CHAR(FECHA,'YYYY')) = UN_ANO
                  AND TO_NUMBER(TO_CHAR(FECHA,'MM'))   = UN_MES
                  AND D.PERIODO                        = UN_FECHINTERF
                  AND (T.CONCEPTO IN ('CM','DS','L','DT','T') AND T.CLASE NOT IN ('E') 
                  OR ( T.CLASE NOT IN ( 'T','E' ) AND T.CONCEPTO NOT IN ( 'T' ,'II','N') ))
                  AND AC.CODIGOELEMENTO = SUBSTR(DM.ELEMENTO,1, MI_DIGITOS)
                GROUP BY DM.COMPANIA, 
                       DM.TIPOMOVIMIENTO, 
                       SUBSTR(DM.ELEMENTO,1, MI_DIGITOS), 
                       TO_NUMBER(TO_CHAR(FECHA,'MM'))  , 
                       TO_NUMBER(TO_CHAR(FECHA,'YYYY')), 
                       AC.CODIGOELEMENTO, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_HISTORICO_BAJA  ELSE AC.DEBITO_HISTORICO_BAJA  END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_HISTORICO_BAJA ELSE AC.CREDITO_HISTORICO_BAJA END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_ACUMULADA_BAJA  ELSE AC.DEBITO_ACUMULADA_BAJA  END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_ACUMULADA_BAJA ELSE AC.CREDITO_ACUMULADA_BAJA END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_LIBROS_BAJA     ELSE AC.DEBITO_LIBROS_BAJA     END,
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_LIBROS_BAJA    ELSE AC.CREDITO_LIBROS_BAJA    END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_DEBITO_RETIRADOS_BAJA  ELSE AC.DEBITO_RETIRADOS_BAJA  END, 
                       CASE WHEN MI_NIIF ='SI' THEN AC.NIIF_CREDITO_RETIRADOS_BAJA ELSE AC.CREDITO_RETIRADOS_BAJA END  
    ) LOOP
        MI_DATOS :=1;

        /* se comenta el bloque de codigo ya que estas cuentas no se toman en cuenta para la configuracion de 
        INTERFAZ ALMACEN A CONTABILIDAD POR RETIRO DE ACTIVOS NIIF  ticket #7735241  JM 23/08/2023 
        
        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.DEBITO_HISTORICO_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => RS.VALOR_TOTAL
                                         ,UN_CREDITO     => 0
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF; 




        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.CREDITO_ACUMULADA_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => 0
                                         ,UN_CREDITO     => RS.DEPACUMULADA
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;



        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.CREDITO_LIBROS_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => 0
                                         ,UN_CREDITO     => RS.VLRLIBROS
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;



        */

        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.CREDITO_HISTORICO_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => 0
                                         ,UN_CREDITO     => RS.VALOR_TOTAL
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;

        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.DEBITO_ACUMULADA_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => RS.DEPACUMULADA
                                         ,UN_CREDITO     => 0
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;



        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.DEBITO_LIBROS_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => RS.VLRLIBROS
                                         ,UN_CREDITO     => 0
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;


        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.DEBITO_RETIRADOS_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => RS.VALOR_TOTAL
                                         ,UN_CREDITO     => 0
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;

        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        MI_RTATEM := FC_VALIDAINSERTAPLANA(UN_COMPANIA   => MI_COMPANIADESTINO
                                         ,UN_ANO         => UN_ANO
                                         ,UN_TIPO        => UN_TIPO
                                         ,UN_GRUPOE      => RS.GRUPO
                                         ,UN_NUMERO      => UN_NUMERO
                                         ,UN_CONSECUTIVO => MI_CONSECUTIVO
                                         ,UN_CUENTA      => RS.CREDITO_RETIRADOS_BAJA
                                         ,UN_FECHINTERF  => UN_FECHINTERF
                                         ,UN_DEBITO      => 0
                                         ,UN_CREDITO     => RS.VALOR_TOTAL
                                         ,UN_CENTRO      => PCK_DATOS.FC_CONS_CENTRO()
                                         ,UN_TERCERO     => PCK_DATOS.FC_CONS_TERCERO()
                                         ,UN_SUCURSAL    => PCK_DATOS.FC_CONS_SUCURSAL()
                                         ,UN_AXILIAR     => PCK_DATOS.FC_CONS_AUXILIAR()
                                         ,UN_FUENTE      => PCK_DATOS.FC_CONS_FUENTE()
                                         ,UN_REFERENCIA  => PCK_DATOS.FC_CONS_REFERENCIA());        
        IF MI_RTATEM <> 'OK' THEN
            MI_CONSECUTIVO:=MI_CONSECUTIVO-1;
            MI_RTAPLANO := MI_RTAPLANO  || MI_RTATEM;    
        END IF;

    END LOOP MOVIMIENTOSACONTABILIZAR;

    IF MI_DATOS = 0 THEN
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('No existe información de retiro de activo para el mes de ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || CHR(13) || CHR(10));
    ELSIF MI_CONSECUTIVO = 0 THEN
        MI_RTAPLANO := MI_RTAPLANO || TO_CLOB ('Existe información en el mes de ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ', pero no son validas las configuraciones.' || CHR(13) || CHR(10));
    ELSE        
        MI_RTAPLANO:=  MI_RTAPLANO||TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR(  UN_COMPANIA         => MI_COMPANIADESTINO
                                                                              ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                                                              ,UN_NUMERO           => UN_NUMERO 
                                                                              ,UN_ANO              => UN_ANO
                                                                              ,UN_FECHA            => UN_FECHINTERF
                                                                              ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                                              ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                                              ,UN_DESCRIPCION      => 'INTERFACE DE ALMACEN A CONTABILIDAD DEL MES:'|| UN_FECHINTERF || ''
                                                                              ,UN_USUARIO          => UN_USUARIO 
                                                                              ,UN_SIMPLE           => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => MI_COMPANIADESTINO, 
                                                                                                                                          UN_NOMBRE    => 'SIMPLIFICAR INTERFACE ALMACEN AJUSTES' ,
                                                                                                                                          UN_MODULO    => 96, 
                                                                                                                                          UN_FECHA_PAR => UN_FECHINTERF ), 'SI') = 'SI' THEN -1 ELSE 0 END
                                                                              ,UN_INDIMPRESION     => -1
                                                           )) ;
    END IF;        
    RETURN MI_RTAPLANO;

END FC_CONTABILIZARRETIROACTIVOS;

FUNCTION FC_VALIDAINSERTAPLANA
/*  
        NAME              : En Access InterfaceAlmacenRetitoActivos
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 05/11/2018
        TIME              : 08:17 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2018.10.03
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Para realizar la validación de las cuentas y el insert a la plana ajustes
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:InterfaceAlmacenRetitoActivos
        @METHOD:Post
*/   
 (UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA
 ,UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO
 ,UN_TIPO          IN  VARCHAR2
 ,UN_GRUPOE        IN  VARCHAR2
 ,UN_NUMERO        IN  NUMBER
 ,UN_CONSECUTIVO   IN  NUMBER
 ,UN_CUENTA        IN  VARCHAR2
 ,UN_FECHINTERF    IN  DATE 
 ,UN_DEBITO        IN  NUMBER
 ,UN_CREDITO       IN  NUMBER
 ,UN_CENTRO        IN  VARCHAR2
 ,UN_TERCERO       IN  VARCHAR2
 ,UN_SUCURSAL      IN  VARCHAR2
 ,UN_AXILIAR       IN  VARCHAR2
 ,UN_FUENTE        IN  VARCHAR2
 ,UN_REFERENCIA    IN  VARCHAR2
 )
 RETURN CLOB
 AS
    MI_NATU VARCHAR2(1);
    MI_TABLA    PCK_SUBTIPOS.TI_TABLA ;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_FILAS     PCK_SUBTIPOS.TI_ENTERO;
 BEGIN
    IF ABS(UN_DEBITO - UN_CREDITO) =0 THEN 
        RETURN 'NO';
    END IF;
    BEGIN
    SELECT NATURALEZA
    INTO MI_NATU
    FROM PLAN_CONTABLE
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANO
      AND CODIGO   = UN_CUENTA
      AND (MOVIMIENTO  NOT IN(0)
        OR MAN_CEN_CTO NOT IN(0)
        OR MAN_AUX_TER NOT IN(0)
        OR MAN_AUX_GEN NOT IN(0)
        OR MAN_AUX_REF NOT IN(0)
        OR MAN_AUX_FUE NOT IN(0)
        );
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RETURN TO_CLOB ('El grupo ' || UN_GRUPOE || '; no ha sido configurado. El valor dejado de reportar es ' || ABS(UN_DEBITO - UN_CREDITO) ||  CHR(13) || CHR(10));
    END;
    IF MI_NATU NOT IN ('D','C') THEN
        RETURN TO_CLOB ('El grupo ' || UN_GRUPOE || '; no ha sido configurado. El valor dejado de reportar es ' || ABS(UN_DEBITO - UN_CREDITO) ||  CHR(13) || CHR(10));
    END IF;
    MI_TABLA := 'TEMP_PLANA_AJUSTES';
        MI_CAMPOS := 'COMPANIA
                      ,ANO
                      ,TIPO_CPTE
                      ,COMPROBANTE
                      ,CONSECUTIVO
                      ,CUENTA
                      ,FECHA
                      ,NATURALEZA
                      ,VALOR_DEBITO
                      ,VALOR_CREDITO
                      ,EJECUCION_DEBITO
                      ,EJECUCION_CREDITO
                      ,CENTRO_COSTO
                      ,TERCERO
                      ,SUCURSAL 
                      ,AUXILIAR
                      ,FUENTE_RECURSOS
                      ,REFERENCIA';
    MI_VALORES :=' '''|| UN_COMPANIA    ||'''
                    ,'|| UN_ANO         ||'
                  ,'''|| UN_TIPO        ||'''
                    ,'|| UN_NUMERO      ||'
                    ,'|| UN_CONSECUTIVO ||'
                  ,'''|| UN_CUENTA      ||'''
                   ,TO_DATE(''' || TO_CHAR(UN_FECHINTERF, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                   ,'''|| MI_NATU     ||'''
                   ,'''|| UN_DEBITO   ||'''
                   ,'''|| UN_CREDITO  ||'''
                   ,'''|| UN_DEBITO   ||'''
                   ,'''|| UN_CREDITO  ||'''
                   ,'''|| UN_CENTRO   ||'''
                   ,'''|| UN_TERCERO  ||'''
                   ,'''|| UN_SUCURSAL ||'''
                   ,'''|| UN_AXILIAR  ||'''
                   ,'''|| UN_FUENTE   ||'''
                   ,'''|| UN_REFERENCIA ||'''';

    BEGIN
        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                      UN_ACCION  => 'I',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
    END;
    RETURN 'OK';
 END FC_VALIDAINSERTAPLANA;

PROCEDURE PR_ACT_BODEGATIPOACTIVO

/* 
        NAME              : PR_ACT_BODEGATIPOACTIVO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : YESSICA SANA
        DATE MIGRADOR     : 27/11/2018
        TIME              : 03:00 PM
        SOURCE MODULE     : INTERFACES ALMACEN CONTABILIDAD
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     : 

        @NAME:actualizarBodegaTipoActivo
        @METHOD: PUT
    */
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO     IN  NIIF_INVENTARIOCONTA.CODIGOELEMENTO%TYPE,
    UN_ANO          IN  NIIF_INVENTARIOCONTA.ANO%TYPE,
    UN_CENTROCOSTO  IN  NIIF_INVENTARIOCONTA.CENTRO_COSTO%TYPE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO
)
AS 
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_RTA_ACME       NUMBER(20,0);
    MI_CANTIDAD       NUMBER;

 BEGIN  


          MI_TABLA := 'NIIF_INVENTARIOCONTA';

          BEGIN
              SELECT COUNT(1) CANTIDAD
                INTO MI_CANTIDAD
                FROM NIIF_INVENTARIOCONTA
               WHERE COMPANIA = UN_COMPANIA ;
          END;

          FOR MI_RS IN (SELECT CODIGOELEMENTO ELEMENTO
                          FROM INVENTARIO
                         WHERE INVENTARIO.COMPANIA = UN_COMPANIA
                         AND INVENTARIO.CODIGOELEMENTO = ( CASE WHEN MI_CANTIDAD = 0 
                                 THEN INVENTARIO.CODIGOELEMENTO 
                                 ELSE UN_ELEMENTO 
                             END )
                         ORDER BY INVENTARIO.CODIGOELEMENTO)
          LOOP 
              BEGIN
                  BEGIN
                      MI_MERGEUSING := ' SELECT DISTINCT BODEGA.COMPANIA, BODEGA.CODIGO BODEGA,
                                            TIPO_ACTIVO.CODIGO_TIPOACTIVO TIPOACTIVO
                                       FROM TIPO_ACTIVO,BODEGA
                                      WHERE TIPO_ACTIVO.COMPANIA = BODEGA.COMPANIA
                                      AND TIPO_ACTIVO.COMPANIA = ''' || UN_COMPANIA || '''
                                      ORDER BY 1,2';

                      MI_MERGEENLACE := ' VISTA.COMPANIA = TABLA.COMPANIA
                                               AND ''' || UN_ELEMENTO || ''' = TABLA.CODIGOELEMENTO
                                               AND ' || UN_ANO || '  = TABLA.ANO
                                               AND VISTA.TIPOACTIVO = TABLA.TIPOACTIVO
                                               AND VISTA.BODEGA  = TABLA.BODEGA
                                               AND ''' || UN_CENTROCOSTO || ''' = TABLA.CENTRO_COSTO ';

                      MI_MERGEEXISTE := ' UPDATE SET TABLA.COMPANIA = VISTA.COMPANIA WHERE COMPANIA = ''' || UN_COMPANIA || '''';

                      MI_MERGENOEXISTE := 'INSERT(  COMPANIA,
                                                    CODIGOELEMENTO,
                                                    ANO,
                                                    TIPOACTIVO,
                                                    BODEGA,
                                                    CENTRO_COSTO,
                                                    CREATED_BY, 
                                                    DATE_CREATED )
                                             VALUES( VISTA.COMPANIA, ''' || UN_ELEMENTO || ''', ' ||
                                                    UN_ANO || ',
                                                    VISTA.TIPOACTIVO,
                                                    VISTA.BODEGA, ''' || 
                                                    UN_CENTROCOSTO || ''',
                                                    ''' || UN_USUARIO ||''',
                                                    SYSDATE )';  
                      MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                                       ,UN_ACCION      => 'IN'
                                                       ,UN_MERGEUSING  => MI_MERGEUSING
                                                       ,UN_MERGEENLACE => MI_MERGEENLACE
                                                       ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                       ,UN_MERGENOEXIS => MI_MERGENOEXISTE);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                  END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN 

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUALIZANDOBODEGATIPOACT
                                        ,UN_TABLAERROR => MI_TABLA);
              END;
          END LOOP;

END PR_ACT_BODEGATIPOACTIVO;

FUNCTION FC_INTERFAZ_TRANSICION
(
/* 
        NAME              : FC_INTERFAZ_TRANSICION
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 28/11/2018
        TIME              : 06:00 PM
        SOURCE MODULE     : INTERFACES Almacen Contabilidad
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Interface
        PARAMETERS        :
        MODIFICATIONS     :
  @NAME: insertarComprobTransicion
  @METHOD: POST
*/
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_TIPO         IN  VARCHAR2,
    UN_FECHA        IN  DATE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS 
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQL1                PCK_SUBTIPOS.TI_STRSQL;
    MI_ELEMENTO               DEPRECIAR.ELEMENTO%TYPE;
    MI_SERIE                  DEPRECIAR.SERIE%TYPE;
    MI_CUENTACREDITOINICIAL   NIIF_INVENTARIOCONTA.CUENTACREDITO%TYPE;
    MI_CUENTACREDITOFINAL     NIIF_INVENTARIOCONTA.CUENTACREDITO%TYPE;
    MI_RS                     SYS_REFCURSOR;
    MI_BODEGAINICIAL          NIIF_INVENTARIOCONTA.BODEGA%TYPE;
    MI_BODEGAFINAL            NIIF_INVENTARIOCONTA.BODEGA%TYPE;
    MI_TIPOACTIVO             NIIF_INVENTARIOCONTA.TIPOACTIVO%TYPE;
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_VLRACUMULADOINICIAL    DEPRECIAR.VLRACUMULADO%TYPE;
    MI_VLRACUMULADOFINAL      DEPRECIAR.VLRACUMULADO%TYPE;
    MI_VLRACUMULADONIIFINIC   DEPRECIAR.NIIF_VLRACUMULADO%TYPE;
    MI_VLRACUMULADONIIFFINA   DEPRECIAR.NIIF_VLRACUMULADO%TYPE;
    MI_MANEJANIIFALMACEN      PARAMETRO.VALOR%TYPE;
    MI_CONTADOR               NUMBER := 0;
    MI_CONTADORBODEGA         NUMBER := 0;
    MI_RTA                    NUMBER := 0;
    MI_CONSECDETALLES         NUMBER := 0;
    MI_RTAPLANO               CLOB;
    MI_CONSECUTIVO            NUMBER;
    MI_RSBODEGAS              SYS_REFCURSOR;
    MI_MES                    PCK_SUBTIPOS.TI_MES;
    MI_MENSAJE                VARCHAR(800 CHAR);
    MI_COMPROBANTE            NUMBER := 0;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
BEGIN

    MI_MES := MI_MES - 1;

    MI_MANEJANIIFALMACEN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                  UN_NOMBRE   => 'MANEJA NIIF EN ALMACEN',
                                                  UN_MODULO   => 10,
                                                  UN_FECHA_PAR=> SYSDATE,
                                                  UN_IND_MAYUS=> -1
                                                  );
    --Se realiza for para garantizar se realice recorrido para las combinaciones 30-20 y 20-30
    FOR MI_RSBODEGAS IN 1..2 LOOP
        MI_CONTADORBODEGA := MI_CONTADORBODEGA + 1;
        MI_STRSQL := 'WITH BODEGASERVICIO AS (
                        SELECT COMPANIA, ELEMENTO,
                               SERIE, BODEGA, 
                               DEPRECIAR.VLRACUMULADO,  
                               DEPRECIAR.NIIF_VLRACUMULADO, DEPRECIAR.ANO
                          FROM DEPRECIAR WHERE COMPANIA = ''' || UN_COMPANIA || '''
                           AND ANO = ' || UN_ANO || '
                           AND BODEGA IN (''' || CASE WHEN MI_CONTADORBODEGA = 1 THEN 30 ELSE 20 END || ''') 
                           AND PERIODO = LAST_DAY(TO_DATE(''28/' || TO_CHAR(UN_MES) || 
                               '/' || TO_CHAR(UN_ANO) || ''',''DD/MM/YYYY''))
                           AND IND_INTRANSICION IN (0))
                        SELECT DEPRECIAR.ELEMENTO,  DEPRECIAR.SERIE,
                               DEPRECIAR.BODEGA,  DEPRECIAR.VLRACUMULADO,
                               DEPRECIAR.NIIF_VLRACUMULADO, 
                               BODEGASERVICIO.BODEGA, BODEGASERVICIO.VLRACUMULADO,
                               BODEGASERVICIO.NIIF_VLRACUMULADO, DEPRECIAR.NIIF_TIPO_ACTIVO
                          FROM DEPRECIAR INNER JOIN BODEGASERVICIO
                            ON DEPRECIAR.COMPANIA = BODEGASERVICIO.COMPANIA
                           AND DEPRECIAR.ELEMENTO = BODEGASERVICIO.ELEMENTO
                           AND DEPRECIAR.SERIE = BODEGASERVICIO.SERIE
                           AND DEPRECIAR.ANO = BODEGASERVICIO.ANO
                         WHERE DEPRECIAR.COMPANIA = ''' || UN_COMPANIA || '''
                           AND DEPRECIAR.ANO = ' || CASE WHEN MI_MES = 0 THEN UN_ANO - 1  ELSE UN_ANO END || '
                           AND DEPRECIAR.BODEGA IN (''' || CASE WHEN MI_CONTADORBODEGA = 1 THEN 20 ELSE 30 END || ''') 
                           AND DEPRECIAR.PERIODO = LAST_DAY(TO_DATE(''28/' || TO_CHAR(CASE WHEN MI_MES = 0 THEN 12 ELSE UN_MES-1 END ) || 
                               '/' || TO_CHAR(UN_ANO) || ''',''DD/MM/YYYY''))';

        OPEN MI_RS FOR MI_STRSQL;

        LOOP
            FETCH MI_RS INTO MI_ELEMENTO, MI_SERIE, MI_BODEGAINICIAL, 
                              MI_VLRACUMULADOINICIAL,  MI_VLRACUMULADONIIFINIC,
                              MI_BODEGAFINAL, MI_VLRACUMULADOFINAL,
                              MI_VLRACUMULADONIIFFINA,MI_TIPOACTIVO;
            EXIT WHEN MI_RS%NOTFOUND;             
                BEGIN
                    SELECT CUENTACREDITO,
                           CTACREDITOVLRACTIVO
                      INTO MI_CUENTACREDITOINICIAL,
                           MI_CUENTACREDITOFINAL
                      FROM NIIF_INVENTARIOCONTA
                     WHERE COMPANIA = UN_COMPANIA
                       AND CODIGOELEMENTO = MI_ELEMENTO
                       AND ANO = UN_ANO
                       AND TIPOACTIVO = MI_TIPOACTIVO
                       AND BODEGA = CASE WHEN MI_CONTADORBODEGA = 1 THEN 30 ELSE 20 END
                       AND CENTRO_COSTO = '99999999999999999999';
                END;
                MI_TABLA := 'TEMP_PLANA_AJUSTES';
                IF MI_CUENTACREDITOINICIAL IS NOT NULL AND MI_CUENTACREDITOFINAL IS NOT NULL THEN
                    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA => 'COMPROBANTE_CNT',
                                                                        UN_CRITERIO => '    COMPANIA = ''' || UN_COMPANIA || 
                                                                                    ''' AND ANO  = ' || UN_ANO  || 
                                                                                    '   AND TIPO = ''' || UN_TIPO || '''',
                                                                        UN_CAMPO    => 'NUMERO',
                                                                        UN_INICIAL => UN_ANO * 1000
                                                                        );
                    MI_CONTADOR := 0;   
                    -- Se realiza for para asegurar que se cuente con los dos detalles (debito, credito)
                    FOR MI_I IN 1.. 2 

                    LOOP
                        MI_CONTADOR := MI_CONTADOR + 1;
                        BEGIN
                            BEGIN
                                MI_CAMPOS := 'COMPANIA
                                          ,ANO
                                          ,TIPO_CPTE
                                          ,COMPROBANTE
                                          ,CONSECUTIVO
                                          ,CUENTA
                                          ,FECHA
                                          ,NATURALEZA
                                          ,VALOR_DEBITO
                                          ,VALOR_CREDITO
                                          ,EJECUCION_DEBITO
                                          ,EJECUCION_CREDITO
                                          ,CENTRO_COSTO
                                          ,TERCERO
                                          ,SUCURSAL ';
                                MI_VALORES :=' '''|| UN_COMPANIA ||'''
                                                ,'|| UN_ANO ||'
                                              ,'''|| UN_TIPO ||'''
                                                ,'|| MI_CONSECUTIVO ||'
                                                ,'|| MI_CONTADOR ||'
                                              ,'''|| CASE WHEN MI_CONTADOR = 1 THEN MI_CUENTACREDITOFINAL ELSE MI_CUENTACREDITOINICIAL END || '''
                                              , LAST_DAY(TO_DATE(''28/' || UN_MES || '/' || UN_ANO || ''',''DD/MM/YYYY''))
                                               ,' ||  CASE WHEN MI_CONTADOR = 1 THEN '''D''' ELSE '''C''' END || '
                                               ,'''|| CASE WHEN MI_CONTADOR = 1 THEN 
                                                          CASE WHEN TRIM(MI_MANEJANIIFALMACEN) = 'SI' 
                                                          THEN MI_VLRACUMULADONIIFFINA
                                                          ELSE MI_VLRACUMULADOFINAL 
                                                          END 
                                                      ELSE 0 
                                                      END
                                                      ||'''
                                               ,'''|| CASE WHEN MI_CONTADOR = 1 THEN 
                                                          0
                                                      ELSE CASE WHEN TRIM(MI_MANEJANIIFALMACEN) = 'SI' 
                                                          THEN MI_VLRACUMULADONIIFFINA 
                                                          ELSE MI_VLRACUMULADOFINAL 
                                                          END  
                                                      END ||'''
                                               ,'''|| CASE WHEN MI_CONTADOR = 1 THEN 
                                                          CASE WHEN TRIM(MI_MANEJANIIFALMACEN) = 'SI' 
                                                          THEN MI_VLRACUMULADONIIFFINA 
                                                          ELSE MI_VLRACUMULADOFINAL 
                                                          END 
                                                      ELSE 0 
                                                      END ||'''
                                               ,'''|| CASE WHEN MI_CONTADOR = 1 THEN 
                                                          0
                                                      ELSE CASE WHEN TRIM(MI_MANEJANIIFALMACEN) = 'SI' 
                                                          THEN MI_VLRACUMULADONIIFFINA 
                                                          ELSE MI_VLRACUMULADOFINAL 
                                                          END  
                                                      END ||'''
                                               ,''99999999999999999999''
                                               ,''999999999999999999''
                                               ,''999''';

                                MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                             UN_ACCION  => 'I',
                                                             UN_CAMPOS  => MI_CAMPOS,
                                                             UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN 

                             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_INSERCIONTEMPAJUSTES
                                                ,UN_TABLAERROR => MI_TABLA);
                        END;

                    END LOOP;   
                    MI_MENSAJE := 'Comprobante de Interfaz de Transición del año: '     || 
                                  UN_ANO || ' del mes: ' || UN_MES || ' del elemento: ' || 
                                  MI_ELEMENTO || ', Serie: ' || MI_SERIE;

                    MI_RTAPLANO:=  PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                            ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                            ,UN_NUMERO           => MI_CONSECUTIVO 
                                            ,UN_ANO              => UN_ANO
                                            ,UN_FECHA            => UN_FECHA
                                            ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                            ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                            ,UN_DESCRIPCION      => MI_MENSAJE
                                            ,UN_SIMPLE           => 0
                                            ,UN_INDIMPRESION     => 0
                                            ,UN_USUARIO          => UN_USUARIO) ;

                    BEGIN
                        SELECT COUNT(1)
                          INTO  MI_COMPROBANTE
                          FROM COMPROBANTE_CNT
                         WHERE COMPANIA = UN_COMPANIA
                           AND ANO      = UN_ANO
                           AND TIPO     = UN_TIPO
                           AND NUMERO   = MI_CONSECUTIVO;
                    END;

                    IF MI_COMPROBANTE > 0 THEN
                        BEGIN
                            BEGIN
                                MI_CAMPOS := ' IND_INTRANSICION = -1 , DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USUARIO || '''';
                                MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                                                ''' AND ELEMENTO = ''' || MI_ELEMENTO || 
                                                ''' AND SERIE = ' || MI_SERIE || 
                                                ' AND PERIODO = 
                                                LAST_DAY(TO_DATE(''28/' || CASE WHEN MI_MES = 0 THEN 12 ELSE UN_MES END
                                                || '/' || CASE WHEN MI_MES = 0 THEN UN_ANO-1 ELSE UN_ANO END || ''',''DD/MM/YYYY''))';

                                MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA     =>  'DEPRECIAR',
                                                             UN_ACCION    =>  'M',
                                                             UN_CAMPOS    =>  MI_CAMPOS,
                                                             UN_CONDICION =>  MI_CONDICION);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                              END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUINDINTERFAZTRANSICION,
                                    UN_TABLAERROR => 'DEPRECIAR');
                        END;
                    END IF;

                END IF;
            END LOOP;
        END LOOP;
 RETURN NULL;
END FC_INTERFAZ_TRANSICION;

PROCEDURE PR_INSERTA_ALMCONTABILIDADCC 
/* 
        NAME              : PR_INSERTA_ALMCONTABILIDADCC
        AUTHORS           : SYSMAN  SAS
        AUTHOR            : DANIEL RANGEL
        DATE              : 29/03/2022
        TIME              : 03:00 PM
        SOURCE MODULE     : INTERFAZ CONTABILIZAR
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Realiza merge sobre la tabla ALMACENCONTABILIDADCC
        PARAMETERS        :
        MODIFICATIONS     : 

        @NAME: insertaAlmacenContabilidadCC
        @METHOD: PUT
    */
(
    UN_COMPANIA         IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOELEMENTO   IN   VARCHAR2,
    UN_TIPO             IN   VARCHAR2,
    UN_CENTROCOSTO      IN   VARCHAR2,
    UN_FUENTERECURSO    IN   VARCHAR2,
    UN_ANO              IN   PCK_SUBTIPOS.TI_ANIO
) AS 
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             CLOB;
    MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;

BEGIN 

BEGIN
    MI_CONDICION:='SELECT
              TM.COMPANIA,
              I.CODIGOELEMENTO,
              TM.CODIGO   TIPOMOVIMIENTO,
              CC.ANO,
              CC.CODIGO   CENTRO_COSTO,
              FR.CODIGO   FUENTE_RECURSO
          FROM
              INVENTARIO       I,
              TIPOMOVIMIENTO   TM,
              CENTRO_COSTO     CC,
              FUENTE_RECURSOS  FR
          WHERE
              I.TIPO = TM.TIPOELEMENTO
              AND I.COMPANIA = TM.COMPANIA
              AND I.COMPANIA = CC.COMPANIA
              AND I.COMPANIA = ''' ||UN_COMPANIA||'''
              AND CC.CODIGO = ''' ||UN_CENTROCOSTO||'''
              AND CC.ANO = '||UN_ANO||'
              AND FR.CODIGO = ''' ||UN_FUENTERECURSO||'''
              AND FR.ANO = '||UN_ANO||'
              AND I.CODIGOELEMENTO = '''||UN_CODIGOELEMENTO||'''
              AND I.TIPO = '''||UN_TIPO||'''
              AND LENGTH(I.CODIGOELEMENTO) = (
                  SELECT
                      VALOR
                  FROM
                      PARAMETRO
                  WHERE
                      COMPANIA = ''' ||UN_COMPANIA||''' 
                      AND MODULO = -1
                      AND NOMBRE LIKE ''%DIGITOS AGRUPACION INVENTARIO%''
              ) GROUP BY TM.COMPANIA, I.CODIGOELEMENTO,
              TM.CODIGO,  CC.ANO, CC.CODIGO, FR.CODIGO';
    MI_CAMPOS    := 'TABLA.COMPANIA = VISTA.COMPANIA
           AND TABLA.CODIGOELEMENTO = VISTA.CODIGOELEMENTO
           AND TABLA.TIPOMOVIMIENTO = VISTA.TIPOMOVIMIENTO
           AND TABLA.CENTRO_COSTO = VISTA.CENTRO_COSTO
           AND TABLA.FUENTEDERECURSO = VISTA.FUENTE_RECURSO
           AND TABLA.ANO = '||UN_ANO;

    MI_VALORES   :='INSERT (
                    COMPANIA,
                    CODIGOELEMENTO,
                    TIPOMOVIMIENTO,
                    ANO,
                    CENTRO_COSTO,
                    FUENTEDERECURSO)
                    VALUES
                    ( VISTA.COMPANIA,
                    VISTA.CODIGOELEMENTO,
                    VISTA.TIPOMOVIMIENTO,
                    '||UN_ANO||',
                    VISTA.CENTRO_COSTO,
                    VISTA.FUENTE_RECURSO)';
                    
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'ALMACENCONTABILIDADCC'
                                     ,UN_ACCION      => 'IN'
                                     ,UN_MERGEUSING  =>  MI_CONDICION
                                     ,UN_MERGEENLACE =>  MI_CAMPOS
                                     ,UN_MERGENOEXIS =>  MI_VALORES);
                                  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
END PR_INSERTA_ALMCONTABILIDADCC;

PROCEDURE PR_INSERTA_ALMACENCONTABILIDAD 
/* 
        NAME              : PR_INSERTA_ALMACENCONTABILIDAD
        AUTHORS           : SYSMAN  SAS
        AUTHOR            : DANIEL RANGEL
        DATE              : 30/03/2022
        TIME              : 10:00 AM
        SOURCE MODULE     : INTERFAZ CONTABILIZAR
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Realiza merge sobre la tabla ALMACENCONTABILIDAD
        PARAMETERS        :
        MODIFICATIONS     : 

        @NAME: insertaAlmacenContabilidad
        @METHOD: PUT
    */
(
    UN_COMPANIA         IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOELEMENTO   IN   VARCHAR2,
    UN_TIPO             IN   VARCHAR2,
    UN_ANO              IN   PCK_SUBTIPOS.TI_ANIO
) AS 
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             CLOB;
    MI_RTA                 PCK_SUBTIPOS.TI_ENTERO;

BEGIN 

BEGIN
    MI_CONDICION:='SELECT
              TM.COMPANIA,
              I.CODIGOELEMENTO,
              TM.CODIGO   TIPOMOVIMIENTO
          FROM
              INVENTARIO       I,
              TIPOMOVIMIENTO   TM
          WHERE
              I.TIPO = TM.TIPOELEMENTO
              AND I.COMPANIA = TM.COMPANIA
              AND I.COMPANIA = ''' ||UN_COMPANIA||'''
              AND I.CODIGOELEMENTO = '''||UN_CODIGOELEMENTO||'''
              AND I.TIPO = '''||UN_TIPO||'''
              AND LENGTH(I.CODIGOELEMENTO) = (
                  SELECT
                      VALOR
                  FROM
                      PARAMETRO
                  WHERE
                      COMPANIA = ''' ||UN_COMPANIA||''' 
					  AND MODULO = -1                      
                      AND NOMBRE LIKE ''%DIGITOS AGRUPACION INVENTARIO%''
              )';
              

    MI_CAMPOS    := 'TABLA.COMPANIA = VISTA.COMPANIA
           AND TABLA.CODIGOELEMENTO = VISTA.CODIGOELEMENTO
           AND TABLA.TIPOMOVIMIENTO = VISTA.TIPOMOVIMIENTO
           AND TABLA.ANO = '||UN_ANO;

    MI_VALORES   :='INSERT (
                    COMPANIA,
                    CODIGOELEMENTO,
                    TIPOMOVIMIENTO,
                    ANO)
                    VALUES
                    ( VISTA.COMPANIA,
                    VISTA.CODIGOELEMENTO,
                    VISTA.TIPOMOVIMIENTO,
                    '||UN_ANO||')';
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'ALMACENCONTABILIDAD'
                                     ,UN_ACCION      => 'IN'
                                     ,UN_MERGEUSING  =>  MI_CONDICION
                                     ,UN_MERGEENLACE =>  MI_CAMPOS
                                     ,UN_MERGENOEXIS =>  MI_VALORES);
                              
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
END PR_INSERTA_ALMACENCONTABILIDAD;

END PCK_CONTABILIZAR_ALMACEN;