create or replace PACKAGE BODY "PCK_PLAN_DESARROLLO" AS


FUNCTION FC_CARGAR_NIVEL
/*
    NAME              : PR_CARGAR_NIVEL   --- SE TOMO COMO BASE CARGAR_NIVEL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 10/08/2016
    TIME              : 15:00 PM
    SOURCE MODULE     : SysmanDES2016.08.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : CARGA EN LA COLECCION LOS NIVELES DEL PLAN INDICATIVO. 
    @NAME: cargarNivel
  */
 (  
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_VIGENCIA     IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO       IN VARCHAR2 DEFAULT ''    
 )
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO 
AS
  MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 1;
  MI_SQL          PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CONSULTA     PCK_SUBTIPOS.TI_STRSQL;
  MI_REGISTROS    NUMBER;
BEGIN
   MI_SQL := ' SELECT   * ' ||
             ' FROM BP_NIVEL_PLAN_IND ' || CHR(10) || CHR(13) ;
  MI_CONDICION := '     COMPANIA             ='''         || UN_COMPANIA || '''' || CHR(10) || CHR(13) ||
                  ' AND VIGENCIA =  ' || UN_VIGENCIA   ;
  MI_CONSULTA := MI_SQL || ' WHERE ' || MI_CONDICION;
  EXECUTE IMMEDIATE MI_CONSULTA BULK  COLLECT INTO CNIVEL_PLANIND;

  IF CNIVEL_PLANIND.COUNT <=0 THEN
    MI_REGISTROS := 0;
  ELSE
    MI_REGISTROS := CNIVEL_PLANIND.COUNT;
  END IF;
  RETURN MI_REGISTROS;
END FC_CARGAR_NIVEL;


--2
FUNCTION FC_GETM_PRO
/*
    NAME              : FC_GETM_PRO   --- GETM_PRO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 10/08/2016
    TIME              : 16:08 PM
    SOURCE MODULE     : SysmanDES2016.08.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA LA CANTIDA DE DIGITOS DEL ID DEL PLAN INDICATIVO QUE TENGA INDICADOR DE META PRODUCCION
    @NAME: obtenerDigitosMetaProduccion
  */
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 2;
  MI_DIGITOS      NUMBER:=0;
BEGIN
  FOR I IN CNIVEL_PLANIND.FIRST .. CNIVEL_PLANIND.LAST
    LOOP
      IF CNIVEL_PLANIND(I).META_PRODUC <> 0 THEN
        MI_DIGITOS :=CNIVEL_PLANIND(I).DIGITOS;    
      END IF;
    END LOOP;
  RETURN MI_DIGITOS;
END FC_GETM_PRO; 


--3
FUNCTION FC_GETM_RES
/*
    NAME              : FC_GETM_RES   --- GETM_RES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 10/08/2016
    TIME              : 16:17 PM
    SOURCE MODULE     : SysmanDES2016.08.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA LA CANTIDA DE DIGITOS DEL ID DEL PLAN INDICATIVO QUE TENGA INDICADOR DE META RESULTADO
    @NAME: obtenerDigitosMetaResultado
  */
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 3;
  MI_DIGITOS      NUMBER:=0;
BEGIN
  FOR I IN CNIVEL_PLANIND.FIRST .. CNIVEL_PLANIND.LAST
    LOOP
      IF CNIVEL_PLANIND(I).META_RESUL <> 0 THEN
        MI_DIGITOS :=CNIVEL_PLANIND(I).DIGITOS;    
      END IF;
    END LOOP;
  RETURN MI_DIGITOS;
END FC_GETM_RES;


--4
FUNCTION FC_GET_ACCION
/*
    NAME              : FC_GET_ACCION   --- GET_ACCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 10/08/2016
    TIME              : 16:22 PM
    SOURCE MODULE     : SysmanDES2016.08.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA LA CANTIDA DE DIGITOS DEL ID DEL PLAN INDICATIVO QUE TENGA INDICADOR DE ACCION
    @NAME: obtenerDigitosAccion
  */
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 4;
  MI_DIGITOS      NUMBER:=0;
BEGIN
  FOR I IN CNIVEL_PLANIND.FIRST .. CNIVEL_PLANIND.LAST
    LOOP
      IF CNIVEL_PLANIND(I).ACCION <> 0 THEN
        MI_DIGITOS :=CNIVEL_PLANIND(I).DIGITOS;    
      END IF;
    END LOOP;
  RETURN MI_DIGITOS;
END FC_GET_ACCION;

--5

PROCEDURE PR_MANTENIMIENTOCUADRESALDOS
/*
  NAME              : PR_MANTENIMIENTOCUADRESALDOS -> En Access: mantenimientoCuadreSaldos
  AUTHORS           : STEFANINI SYSMAN
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 26/12/2018
  TIME              : 09:20 AM
  SOURCE MODULE     : SysmanDES2018.01.01
  DESCRIPTION       : Procedimiento que limpia las tablas oficiales BP para luego actualizar
                      con los totales las PI de las transacciones cerradas y mayoriza.
  MODIFIED BY       : Angie Carolina Corredor Cely 
  DESCRIPTION       : Se agregaron paréntesis de apertura y finalización en las líneas 465, 466, 470, 471, 480 y 481,
                      pues debido a éste error de sintaxis, no se ejecutaba la consulta correctamente.
  

  @NAME  : cuadrarSaldos
*/
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA        IN PCK_SUBTIPOS.TI_ANIO
)
AS 
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RS            SYS_REFCURSOR; 
  MI_RS1           SYS_REFCURSOR; 

BEGIN

  BEGIN
      MI_CAMPOS := 'PRESUPUESTO   = 0,            
                   COMPROMETIDO  = 0,            
                   PAGADO        = 0  ';

      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' ';

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO_FUENTES',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIF                                    
                                        );


  END;

  BEGIN
      MI_CAMPOS := 'PRESUPUESTO   = 0,            
                    COMPROMETIDO  = 0,            
                    PAGADO        = 0  ';

      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' ';

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO_METAS',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIM                                   
                                        );


  END;


  BEGIN
      MI_CAMPOS := 'PRESUPUESTO   = 0,            
                   COMPROMETIDO  = 0,            
                   PAGADO        = 0  ';

      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' ';

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIINDICATIVO                                     
                                        );


  END;

    BEGIN
      MI_CAMPOS := 'AVANCE   = 0,            
                    META     = 0 ';

      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' ';

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR_META',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDICADORMETAS                                     
                                        );


  END;


  --FUENTES
  --Consulta los totales de las fuentes, en las transacciones cerradas (que ya fueron aprobadas)

  <<RECORRER_FUENTES>>
  FOR MI_RS IN (SELECT PIF.COMPANIA,                                                     
                    PIF.ID_PLAN,                                                      
                    PIF.VIGENCIA_PLAN,                                                
                    PIF.VIGENCIA_META,                                                
                    PIF.FUENTE,                                                       
                    SUM(PIF.VALOR_PRESUPUESTO_FIN  - PIF.VALOR_PRESUPUESTO_INI)  PRESUPUESTO,   
                    SUM(PIF.VALOR_COMPROMETIDO_FIN - PIF.VALOR_COMPROMETIDO_INI) COMPROMETIDO,  
                    SUM(PIF.VALOR_PAGADO_FIN       - PIF.VALOR_PAGADO_INI)       PAGADO  
             FROM   PI_PLAN_INDICATIVO_FUENTES PIF                                    
             WHERE  PIF.COMPANIA = UN_COMPANIA                                   
               AND  PIF.NUMERO IN                                                     
                      (                                                               
                      SELECT NUMERO                                                   
                      FROM   PI_TRANSACCION                                           
                      WHERE  COMPANIA = UN_COMPANIA                              
                        AND  ESTADO = 'C'                                             
                      )                                                               
             GROUP  BY PIF.COMPANIA,                                                  
                    PIF.ID_PLAN,                                                      
                    PIF.VIGENCIA_PLAN,                                                
                    PIF.VIGENCIA_META,                                                
                    PIF.FUENTE )LOOP


      MI_CAMPOS := ' PRESUPUESTO   =  '||NVL(MI_RS.PRESUPUESTO, 0)||',
                     COMPROMETIDO  =  '|| NVL(MI_RS.COMPROMETIDO, 0)||' ,
                     PAGADO        =  '|| NVL(MI_RS.PAGADO, 0)||'  ';

      MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND ID_PLAN       = '''||MI_RS.ID_PLAN||'''
                   AND VIGENCIA_PLAN =   '||MI_RS.VIGENCIA_PLAN||'
                   AND VIGENCIA_META =   '||MI_RS.VIGENCIA_META||'
                   AND FUENTE        = '''||MI_RS.FUENTE||''' ';

    BEGIN               
     BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO_FUENTES',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
               MI_MSGERROR(1).CLAVE := 'PLAN';
               MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
               MI_MSGERROR(2).CLAVE := 'FUENTE';
               MI_MSGERROR(2).VALOR := MI_RS.FUENTE;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIFUENTES
                                        ,UN_REEMPLAZOS  => MI_MSGERROR                                       
                                        );


   END;                   

 END LOOP RECORRER_FUENTES;      

 --METAS
 --Consulta los totales de las metas, en las transacciones cerradas (que ya fueron aprobadas) 

   <<RECORRER_METAS>>
  FOR MI_RS IN (SELECT PIM.COMPANIA,
                PIM.ID_PLAN,
                PIM.VIGENCIA_PLAN,
                PIM.VIGENCIA_META,
                SUM(PIM.VALOR_PRESUPUESTO_FIN  - PIM.VALOR_PRESUPUESTO_INI)  PRESUPUESTO,
                SUM(PIM.VALOR_COMPROMETIDO_FIN - PIM.VALOR_COMPROMETIDO_INI) COMPROMETIDO,
                SUM(PIM.VALOR_PAGADO_FIN       - PIM.VALOR_PAGADO_INI)       PAGADO
              FROM PI_PLAN_INDICATIVO_METAS PIM
              WHERE PIM.COMPANIA = UN_COMPANIA
              AND PIM.NUMERO    IN
                ( SELECT NUMERO 
                  FROM PI_TRANSACCION 
                  WHERE COMPANIA = UN_COMPANIA
                    AND ESTADO   = 'C'
                )
              GROUP BY PIM.COMPANIA,
                PIM.ID_PLAN,
                PIM.VIGENCIA_PLAN,
                PIM.VIGENCIA_META )LOOP


      MI_CAMPOS := ' PRESUPUESTO   =  '||NVL(MI_RS.PRESUPUESTO, 0)||',
                     COMPROMETIDO  =  '|| NVL(MI_RS.COMPROMETIDO, 0)||' ,
                     PAGADO        =  '|| NVL(MI_RS.PAGADO, 0)||'  ';

      MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND ID_PLAN       = '''||MI_RS.ID_PLAN||'''
                   AND VIGENCIA_PLAN =   '||MI_RS.VIGENCIA_PLAN||'
                   AND VIGENCIA_META =   '||MI_RS.VIGENCIA_META||'  ';

    BEGIN               
     BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO_METAS',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
               MI_MSGERROR(1).CLAVE := 'PLAN';
               MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIFUENTES
                                        ,UN_REEMPLAZOS  => MI_MSGERROR                                       
                                        );


   END;                   


 END LOOP RECORRER_METAS;  


 --INDICADORES 

 <<RECORRER_INDICADORES>>
 FOR MI_RS1 IN (SELECT ID_PLAN,
                  INDICADOR,
                  SUM(META1_INI) TOTALMETA1_INI,
                  SUM(META1_FIN) TOTALMETA1_FIN,
                  SUM(AVANCE1_INI) TOTALAVANCE1_INI,
                  SUM(AVANCE1_FIN) TOTALAVANCE1_FIN,
                  SUM(META2_INI) TOTALMETA2_INI,
                  SUM(META2_FIN) TOTALMETA2_FIN,
                  SUM(AVANCE2_INI) TOTALAVANCE2_INI,
                  SUM(AVANCE2_FIN) TOTALAVANCE2_FIN,
                  SUM(META3_INI) TOTALMETA3_INI,
                  SUM(META3_FIN) TOTALMETA3_FIN,
                  SUM(AVANCE3_INI) TOTALAVANCE3_INI,
                  SUM(AVANCE3_FIN) TOTALAVANCE3_FIN,
                  SUM(META4_INI) TOTALMETA4_INI,
                  SUM(META4_FIN) TOTALMETA4_FIN,
                  SUM(AVANCE4_INI) TOTALAVANCE4_INI,
                  SUM(AVANCE4_FIN) TOTALAVANCE4_FIN
                FROM PI_INDICADOR_META_TRA
                WHERE COMPANIA       = UN_COMPANIA
                AND VIGENCIA_INICIAL = UN_VIGENCIA
                AND NUMERO          IN
                  (SELECT NUMERO
                  FROM PI_TRANSACCION
                  WHERE COMPANIA = UN_COMPANIA
                  AND ESTADO     = 'C'
                  )
                GROUP BY COMPANIA,
                  ID_PLAN,
                  INDICADOR )
 LOOP

    <<ACTUALIZAR_INDICADOR>>

      FOR MI_RS IN (SELECT COMPANIA, 
               VIGENCIA_INICIAL,                         
               ID_PLAN,                                  
               INDICADOR,                                
               VIGENCIA_META,                            
               META,                                     
               AVANCE                                    
        FROM   PI_INDICADOR_META                         
        WHERE  COMPANIA         = UN_COMPANIA       
         AND  VIGENCIA_INICIAL =  UN_VIGENCIA   
         AND  ID_PLAN          =  MI_RS1.ID_PLAN 
         AND  INDICADOR        =  MI_RS1.INDICADOR)
      LOOP

       IF UN_VIGENCIA = MI_RS.VIGENCIA_META THEN

          MI_CAMPOS := 'META    ='||(MI_RS.META + (MI_RS1.TOTALMETA1_FIN - MI_RS1.TOTALMETA1_INI))||'
                        ,AVANCE ='||(MI_RS.AVANCE + (MI_RS1.TOTALAVANCE1_FIN - MI_RS1.TOTALAVANCE1_INI));

       ELSIF  UN_VIGENCIA + 1 = MI_RS.VIGENCIA_META THEN

          MI_CAMPOS := 'META    ='||(MI_RS.META + (MI_RS1.TOTALMETA2_FIN - MI_RS1.TOTALMETA2_INI))||'
                        ,AVANCE ='||(MI_RS.AVANCE + (MI_RS1.TOTALAVANCE2_FIN - MI_RS1.TOTALAVANCE2_INI));

       ELSIF  UN_VIGENCIA + 2 = MI_RS.VIGENCIA_META THEN

          MI_CAMPOS := 'META    ='||(MI_RS.META + (MI_RS1.TOTALMETA3_FIN - MI_RS1.TOTALMETA3_INI))||'
                        ,AVANCE ='||(MI_RS.AVANCE + (MI_RS1.TOTALAVANCE3_FIN - MI_RS1.TOTALAVANCE3_INI));   

       ELSIF  UN_VIGENCIA + 3 = MI_RS.VIGENCIA_META THEN

          MI_CAMPOS := 'META    ='||(MI_RS.META + (MI_RS1.TOTALMETA4_FIN - MI_RS1.TOTALMETA4_INI))||'
                        ,AVANCE ='||(MI_RS.AVANCE + (MI_RS1.TOTALAVANCE4_FIN - MI_RS1.TOTALAVANCE4_INI));   

       END IF;       

            MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''
                        AND  VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                        AND  ID_PLAN          = '''||MI_RS.ID_PLAN||'''
                        AND  INDICADOR        = '''||MI_RS.INDICADOR||'''
                        AND  VIGENCIA_META    ='||MI_RS.VIGENCIA_META||' ';


          BEGIN               
           BEGIN
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR_META',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
               MI_MSGERROR(1).CLAVE := 'PLAN';
               MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDMETA
                                        ,UN_REEMPLAZOS  => MI_MSGERROR                                       
                                        );


   END;                          


      END LOOP ACTUALIZAR_INDICADOR;

 END LOOP RECORRER_INDICADORES;

END PR_MANTENIMIENTOCUADRESALDOS;


--6
FUNCTION FC_GENERARTRANSACCION
/*
    NAME              : FC_GENERARTRANSACCION => En Access GenerarTransaccion()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 27/02/2018
    TIME              : 12:50 PM
    SOURCE MODULE     : SysmanDES2018.01.01
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : ESTA FUNCION RETORNA LA CANTIDAD DE DIAS HABILES ENTRE DOS FECHAS DE VIATICOS

    @NAME:  generarTransaccion
    @METHOD:  GET                         
  */
  (
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_TIPO         IN PI_TRANSACCION.TIPO%TYPE, 
	UN_VIGENCIA     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_DEPENDENCIA  IN PI_TRANSACCION.DEPENDENCIA%TYPE,
  UN_RESPONSABLE  IN PI_TRANSACCION.RESPONSABLE%TYPE,
  UN_SUCURSAL     IN PI_TRANSACCION.SUCURSAL%TYPE,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN PCK_SUBTIPOS.TI_LONG
AS
  MI_CONS         PCK_SUBTIPOS.TI_ENTERO:=0;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;  
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES; 
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RS           SYS_REFCURSOR;
  MI_CONSECUTIVO  PCK_SUBTIPOS.TI_LONG;
BEGIN

--Generar Consecutivo transaccion
  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||''' AND ANO= '||UN_VIGENCIA||'  AND TIPO= '''||UN_TIPO||''' ';

  MI_CONS :=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                     UN_TABLA    => 'PI_TRANSACCION',
                                     UN_CRITERIO =>  MI_CONDICION,
                                     UN_CAMPO    => 'NUMERO',
                                     UN_INICIAL  => '1') ;

--Crear transaccion                                     

  MI_CAMPOS := 'COMPANIA
              , ANO
              , TIPO
              , NUMERO
              , FECHA
              , MES
              , DIA
              , DEPENDENCIA
              , RESPONSABLE
              , SUCURSAL
              , ESTADO
              , DESCRIPCION            
              , CREATED_BY
              , DATE_CREATED  ';

  MI_VALORES := ''''||UN_COMPANIA||'''
                , '||UN_VIGENCIA||'
                , '''||UN_TIPO||'''
                , '||MI_CONS||'
                , SYSDATE
                , TO_CHAR(SYSDATE,''MM'') 
                , TO_CHAR(SYSDATE,''DD'')
                , '''||UN_DEPENDENCIA||'''
                , '''||UN_RESPONSABLE||'''
                , '''||UN_SUCURSAL||'''
                , ''A''
                , '''||UN_TIPO||'''                   
                , '''||UN_USUARIO||'''
                ,   SYSDATE   ';      



  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_TRANSACCION',
                                              UN_ACCION      => 'I',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSTRANSACCION                                   
                                        );


  END;
--Crear en el plan indicativo  

    MI_CAMPOS := 'COMPANIA,     
                TIPO,         
                NUMERO,       
                ID_PLAN,      
                VIGENCIA_INICIAL,         
                VALOR_PRESUPUESTO_INI,    
                VALOR_PRESUPUESTO_FIN,    
                VALOR_COMPROMETIDO_INI,   
                VALOR_COMPROMETIDO_FIN,   
                VALOR_PAGADO_INI,         
                VALOR_PAGADO_FIN,
                VALOR_OBLIGACIONES_INI,   
                VALOR_OBLIGACIONES_FIN,
                VIGENCIA_FINAL,           
                DESCRIPCION,  
                TIPO_META_PLAN,           
                PREDECESOR,   
                RESPONSABLE,  
                SUCURSAL,     
                DEPENDENCIA_INI,          
                DEPENDENCIA_FIN,          
                CREATED_BY,                         
                DATE_CREATED  ';
          --    CODIGOBPIM,   
  MI_VALORES := 'SELECT COMPANIA,
                '''||UN_TIPO||''',
                '||MI_CONS||',
                ID,
                VIGENCIA_INICIAL,
                PRESUPUESTO,
                PRESUPUESTO,
                COMPROMETIDO,
                COMPROMETIDO,
                PAGADO,
                PAGADO,
                VALOR_OBLIGACIONES,
                VALOR_OBLIGACIONES,
                VIGENCIA_FINAL,
                NVL(DESCRIPCION,'' '')DESCRIPCION,
                TIPO_META_PLAN,
                PREDECESOR,
                RESPONSABLE,
                SUCURSAL,
                DEPENDENCIA,
                DEPENDENCIA,                   
                  '''||UN_USUARIO||''',                
                SYSDATE  
              FROM BP_PLAN_INDICATIVO
              WHERE COMPANIA='''||UN_COMPANIA||'''
              AND VIGENCIA_INICIAL = '||UN_VIGENCIA;      
             -- CODIGOBPIM,   
  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PLAN_INDICATIVO',
                                              UN_ACCION      => 'IS',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPITRANSACCION  
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;


--Crear en el plan indicativo metas                    

  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                                 UN_TABLA    => 'PI_PLAN_INDICATIVO_METAS',
                                                 UN_CRITERIO =>  ' COMPANIA = '||UN_COMPANIA||'',
                                                 UN_CAMPO    => 'CONSECUTIVO');
  FOR MI_RS IN(SELECT   BP_PLAN_INDICATIVO_METAS.COMPANIA,                  
                       UN_TIPO TIPO,
                       MI_CONS NUMERO,
                       BP_PLAN_INDICATIVO_METAS.ID_PLAN,
                       BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN,
                       BP_PLAN_INDICATIVO_METAS.VIGENCIA_META,
                       BP_PLAN_INDICATIVO_METAS.DEPENDENCIA,
                       BP_PLAN_INDICATIVO_METAS.DEPENDENCIA DEPENDENCIAFIN,
                       BP_PLAN_INDICATIVO_METAS.PRESUPUESTO,
                       BP_PLAN_INDICATIVO_METAS.PRESUPUESTO PRESUPUESTOFIN,
                       BP_PLAN_INDICATIVO_METAS.COMPROMETIDO,
                       BP_PLAN_INDICATIVO_METAS.COMPROMETIDO COMPROMETIDOFIN,
                       BP_PLAN_INDICATIVO_METAS.PAGADO,
                       BP_PLAN_INDICATIVO_METAS.PAGADO PAGADOFIN,
                       BP_PLAN_INDICATIVO_METAS.VALOR_OBLIGACIONES,
                       BP_PLAN_INDICATIVO_METAS.VALOR_OBLIGACIONES VALOR_OBLIGACIONESFIN,
                       BP_PLAN_INDICATIVO_METAS.VALOR_DISPONIBLE,
                       BP_PLAN_INDICATIVO_METAS.VALOR_DISPONIBLE VALOR_DISPONIBLEFIN                
                FROM BP_PLAN_INDICATIVO_METAS
               WHERE BP_PLAN_INDICATIVO_METAS.COMPANIA      = UN_COMPANIA
                 AND BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN = UN_VIGENCIA)
   LOOP  


    MI_CAMPOS := 'COMPANIA,
                  TIPO,
                  NUMERO,
                  CONSECUTIVO,
                  ID_PLAN,
                  VIGENCIA_PLAN,
                  VIGENCIA_META,
                  DEPENDENCIA_INI,
                  DEPENDENCIA_FIN,
                  VALOR_PRESUPUESTO_INI,
                  VALOR_PRESUPUESTO_FIN,
                  VALOR_COMPROMETIDO_INI,
                  VALOR_COMPROMETIDO_FIN,
                  VALOR_PAGADO_INI,
                  VALOR_PAGADO_FIN,
                  VALOR_OBLIGACIONES_INI,    
                  VALOR_OBLIGACIONES_FIN,    
                  VALOR_DISPONIBLE_INI,      
                  VALOR_DISPONIBLE_FIN,                 
                  CREATED_BY,                         
                  DATE_CREATED  ';

  MI_VALORES := ' '''||MI_RS.COMPANIA|| ''',                  
                  '''||MI_RS.TIPO||''',
                  '||MI_RS.NUMERO||',
                  '||MI_CONSECUTIVO||', 
                  '''||MI_RS.ID_PLAN||''',
                  '||MI_RS.VIGENCIA_PLAN||',
                  '||MI_RS.VIGENCIA_META||',
                  '''||MI_RS.DEPENDENCIA||''',
                  '''||MI_RS.DEPENDENCIAFIN||''',
                  '||MI_RS.PRESUPUESTO||',
                  '||MI_RS.PRESUPUESTOFIN||',
                  '||MI_RS.COMPROMETIDO||',
                  '||MI_RS.COMPROMETIDOFIN||',
                  '||MI_RS.PAGADO||',
                  '||MI_RS.PAGADOFIN||',
                  '||MI_RS.VALOR_OBLIGACIONES||',
                  '||MI_RS.VALOR_OBLIGACIONESFIN||',
                  '||MI_RS.VALOR_DISPONIBLE||',
                  '||MI_RS.VALOR_DISPONIBLEFIN||',
                  '''||UN_USUARIO||''',                
                   SYSDATE ';      

         MI_CONSECUTIVO := MI_CONSECUTIVO + 1;         
  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PLAN_INDICATIVO_METAS',
                                              UN_ACCION      => 'I',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIMETASTRA  
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;  

END LOOP;  

--Crear en el plan indicativo fuentes                    
  MI_CONSECUTIVO :=    PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( 
                                UN_TABLA => 'PI_PLAN_INDICATIVO_FUENTES', 
                                UN_CRITERIO => ' COMPANIA = '||UN_COMPANIA||'', 
                                UN_CAMPO => 'CONSECUTIVO') ;
   FOR MI_RS IN(SELECT BP_PLAN_INDICATIVO_FUENTES.COMPANIA,
                  UN_TIPO TIPO,
                  MI_CONS NUMERO,
                  BP_PLAN_INDICATIVO_FUENTES.ID_PLAN,
                  BP_PLAN_INDICATIVO_FUENTES.VIGENCIA_PLAN,
                  BP_PLAN_INDICATIVO_FUENTES.VIGENCIA_META,
                  BP_PLAN_INDICATIVO_FUENTES.FUENTE,
                  BP_PLAN_INDICATIVO_FUENTES.PRESUPUESTO,
                  BP_PLAN_INDICATIVO_FUENTES.PRESUPUESTO PRESUPUESTOFIN,
                  BP_PLAN_INDICATIVO_FUENTES.COMPROMETIDO,
                  BP_PLAN_INDICATIVO_FUENTES.COMPROMETIDO COMPROMETIDOFIN,
                  BP_PLAN_INDICATIVO_FUENTES.PAGADO,
                  BP_PLAN_INDICATIVO_FUENTES.PAGADO PAGADOFIN,
                  BP_PLAN_INDICATIVO_FUENTES.VALOR_OBLIGACIONES,
                  BP_PLAN_INDICATIVO_FUENTES.VALOR_OBLIGACIONES VALOR_OBLIGACIONESFIN,
                  BP_PLAN_INDICATIVO_FUENTES.VALOR_DISPONIBLE,
                  BP_PLAN_INDICATIVO_FUENTES.VALOR_DISPONIBLE VALOR_DISPONIBLEFIN           
                FROM BP_PLAN_INDICATIVO_FUENTES 
               WHERE BP_PLAN_INDICATIVO_FUENTES.COMPANIA    = UN_COMPANIA
                 AND BP_PLAN_INDICATIVO_FUENTES.VIGENCIA_PLAN = UN_VIGENCIA)
    LOOP 

    MI_CAMPOS := 'COMPANIA,                   
                  TIPO,                       
                  NUMERO,                     
                  CONSECUTIVO,                
                  ID_PLAN,                    
                  VIGENCIA_PLAN,              
                  VIGENCIA_META,              
                  FUENTE,                     
                  VALOR_PRESUPUESTO_INI,      
                  VALOR_PRESUPUESTO_FIN,      
                  VALOR_COMPROMETIDO_INI,     
                  VALOR_COMPROMETIDO_FIN,     
                  VALOR_PAGADO_INI,           
                  VALOR_PAGADO_FIN,
                  VALOR_OBLIGACIONES_INI,     
                  VALOR_OBLIGACIONES_FIN,     
                  VALOR_DISPONIBLE_INI,       
                  VALOR_DISPONIBLE_FIN,                  
                  CREATED_BY,                         
                  DATE_CREATED  ';

  MI_VALORES := ' '''||MI_RS.COMPANIA||''',                  
                  '''||MI_RS.TIPO||''',
                  '||MI_RS.NUMERO||',
                  '||MI_CONSECUTIVO||', 
                  '''|| MI_RS.ID_PLAN||''',                   
                  '|| MI_RS.VIGENCIA_PLAN||',             
                  '|| MI_RS.VIGENCIA_META||',             
                  '''|| MI_RS.FUENTE||''',               
                  '|| MI_RS.PRESUPUESTO||',               
                  '|| MI_RS.PRESUPUESTOFIN||',               
                  '|| MI_RS.COMPROMETIDO||',              
                  '|| MI_RS.COMPROMETIDOFIN||',              
                  '|| MI_RS.PAGADO||',                    
                  '|| MI_RS.PAGADOFIN||',
                  '|| MI_RS.VALOR_OBLIGACIONES||', 
                  '|| MI_RS.VALOR_OBLIGACIONESFIN||',
                  '|| MI_RS.VALOR_DISPONIBLE||',
                  '|| MI_RS.VALOR_DISPONIBLEFIN||',                  
                  '''||UN_USUARIO||''',                
                  SYSDATE ' ;      

               MI_CONSECUTIVO := MI_CONSECUTIVO + 1;        
  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PLAN_INDICATIVO_FUENTES',
                                              UN_ACCION      => 'I',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIFUENTESTRA  
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;    
 END LOOP ;

--Crear en el plan indicativo adquisiciones                    

    MI_CAMPOS := 'COMPANIA,                 
                TIPO,                     
                NUMERO,                   
                VIGENCIA_INICIAL,         
                ID_PLAN,                  
                CODIGO,                   
                DESCRIPCION_INI,          
                DESCRIPCION_FIN,          
                F_ESTUDIO_PLAN_INI,       
                F_ESTUDIO_PLAN_FIN,       
                F_CONTRATO_PLAN_INI,      
                F_CONTRATO_PLAN_FIN,      
                FECHA_INI_ANT,            
                FECHA_INI_ACT,            
                FECHA_FIN_ANT,            
                FECHA_FIN_ACT,            
                CONTRATO_INI,             
                CONTRATO_FIN,             
                EST_PREVIO_INI,           
                EST_PREVIO_FIN,           
                F_ESTUDIO_EJEC_INI,       
                F_ESTUDIO_EJEC_FIN,       
                F_CONTRATO_EJEC_INI,                                           
                F_CONTRATO_EJEC_FIN,
                F_INICIO_EJECUTADA_INI,   
                F_INICIO_EJECUTADA_FIN,   
                F_FIN_EJECUTADA_INI,      
                F_FIN_EJECUTADA_FIN,	
                CODIGO_UNSPSC_INI,        
                CODIGO_UNSPSC_FIN,        
                MODALIDAD_INI,            
                MODALIDAD_FIN,            
                VALOR_ESTIMADO_INI,       
                VALOR_ESTIMADO_FIN,       
                VALOR_ESTIMADO_VIG_INI,   
                VALOR_ESTIMADO_VIG_FIN,   
                VIGENCIAS_FUTURAS_INI,    
                VIGENCIAS_FUTURAS_FIN,    
                ESTADO_SOLICITUD_INI,     
                ESTADO_SOLICITUD_FIN,               
                CREATED_BY,                         
                DATE_CREATED    ';

  MI_VALORES := ' SELECT COMPANIA,                  
                '''||UN_TIPO||''',
                  '||MI_CONS||',                
                  VIGENCIA_INICIAL,         
                  ID_PLAN,                  
                  CODIGO,                   
                  DESCRIPCION,              
                  DESCRIPCION,              
                  F_ESTUDIO_PLAN,           
                  F_ESTUDIO_PLAN,           
                  F_CONTRATO_PLAN,          
                  F_CONTRATO_PLAN,          
                  FECHA_INI,                
                  FECHA_INI,                
                  FECHA_FIN,                
                  FECHA_FIN,                
                  CONTRATO,                 
                  CONTRATO,                 
                  EST_PREVIO,               
                  EST_PREVIO,               
                  F_ESTUDIO_EJEC,           
                  F_ESTUDIO_EJEC,           
                  F_CONTRATO_EJEC,          
                  F_CONTRATO_EJEC,
                  F_INICIO_EJECUTADA,
                  F_INICIO_EJECUTADA,       
                  F_FIN_EJECUTADA,
                  F_FIN_EJECUTADA,
                  CODIGO_UNSPSC,            
                  CODIGO_UNSPSC,            
                  MODALIDAD,                
                  MODALIDAD,                
                  NVL(VALOR_ESTIMADO,0),     
                  NVL(VALOR_ESTIMADO,0),     
                  NVL(VALOR_ESTIMADO_VIG,0), 
                  NVL(VALOR_ESTIMADO_VIG,0), 
                  VIGENCIAS_FUTURAS,        
                  VIGENCIAS_FUTURAS,        
                  ESTADO_SOLICITUD,         
                  ESTADO_SOLICITUD,                   
                  '''||UN_USUARIO||''',                
                   SYSDATE      
              FROM BP_PLAN_ADQUISICIONES
              WHERE COMPANIA='''||UN_COMPANIA||'''
              AND VIGENCIA_INICIAL = '||UN_VIGENCIA;      


  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PLAN_ADQUISICIONES',
                                              UN_ACCION      => 'IS',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIADQUISITRA  
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END; 

--Crear en el plan indicativo adquisiciones vigencia                     

    MI_CAMPOS := 'COMPANIA,                           
                  TIPO,                               
                  NUMERO,                             
                  VIGENCIA_INICIAL,                   
                  ID_PLAN,                            
                  CODIGO,                             
                  VIGENCIA,                           
                  VALOR_ESTIMADO_INI,                 
                  VALOR_ESTIMADO_FIN,
                  VALOR_EJECUTADO_INI,           
                  VALOR_EJECUTADO_FIN, 
                  CREATED_BY,                         
                  DATE_CREATED  ';

  MI_VALORES := ' SELECT COMPANIA,                  
                  '''||UN_TIPO||''',
                  '||MI_CONS||',                
                    VIGENCIA_INICIAL,                   
                    ID_PLAN,                            
                    CODIGO,                             
                    VIGENCIA,                           
                    VALOR_ESTIMADO,                     
                    VALOR_ESTIMADO,
                    VALOR_EJECUTADO,
                    VALOR_EJECUTADO,
                  '''||UN_USUARIO||''',                
                   SYSDATE   
                FROM  BP_PLAN_ADQUISICIONES_VIGENCIA   
                WHERE COMPANIA='''||UN_COMPANIA||'''
                AND VIGENCIA_INICIAL = '||UN_VIGENCIA;      


  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PLAN_ADQUISICIONES_VIGENCIA',
                                              UN_ACCION      => 'IS',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIADQUIVITRA  
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;   


--Crear en el plan indicativo progranacion fisica

    MI_CAMPOS := 'COMPANIA,
                  TIPO,
                  NUMERO,
                  ID_PLAN,
                  VIGENCIA_INICIAL,
                  VIGENCIA,
                  FECHA_INICIAL_INI,
                  FECHA_INICIAL_FIN,
                  FECHA_FINAL_INI,
                  FECHA_FINAL_FIN,
                  PROGRAMADO_TR1_INI,
                  PROGRAMADO_TR1_FIN,
                  PROGRAMADO_TR2_INI,
                  PROGRAMADO_TR2_FIN,
                  PROGRAMADO_TR3_INI,
                  PROGRAMADO_TR3_FIN,
                  PROGRAMADO_TR4_INI,
                  PROGRAMADO_TR4_FIN,
                  EJECUTADO_TR1_INI,
                  EJECUTADO_TR1_FIN,
                  EJECUTADO_TR2_INI,
                  EJECUTADO_TR2_FIN,
                  EJECUTADO_TR3_INI,
                  EJECUTADO_TR3_FIN,
                  EJECUTADO_TR4_INI,
                  EJECUTADO_TR4_FIN,
                  POBLACION_OBJETIVO,
                  UBICACION_GEOGRAFICA,
                  IMPACTO_SOCIAL, 
                  CREATED_BY,                         
                  DATE_CREATED  ';

  MI_VALORES := ' SELECT COMPANIA,                  
                  '''||UN_TIPO||''',
                  '||MI_CONS||',                
                    ID_PLAN,
                    VIGENCIA_INICIAL,
                    VIGENCIA,
                    FECHA_INICIAL,
                    FECHA_INICIAL,
                    FECHA_FINAL,
                    FECHA_FINAL,
                    PROGRAMADO_TR1,
                    PROGRAMADO_TR1,
                    PROGRAMADO_TR2,
                    PROGRAMADO_TR2,
                    PROGRAMADO_TR3,
                    PROGRAMADO_TR3,
                    PROGRAMADO_TR4,
                    PROGRAMADO_TR4,
                    EJECUTADO_TR1,
                    EJECUTADO_TR1,
                    EJECUTADO_TR2,
                    EJECUTADO_TR2,
                    EJECUTADO_TR3,
                    EJECUTADO_TR3,
                    EJECUTADO_TR4,
                    EJECUTADO_TR4,
                    POBLACION_OBJETIVO,
                    UBICACION_GEOGRAFICA,
                    IMPACTO_SOCIAL,
                  '''||UN_USUARIO||''',                
                    SYSDATE   
                FROM  BP_PROGRAMACION_FISICA   
                WHERE COMPANIA='''||UN_COMPANIA||'''
                AND VIGENCIA_INICIAL = '||UN_VIGENCIA;      


  BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_PROGRAMACION_FISICA',
                                              UN_ACCION      => 'IS',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIPROGFISITRA
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;    
  <<RECORER_INDICADORES>>
  FOR MI_RS IN(SELECT PI_INDICADOR.CODIGO AS INDICADOR,
                  PI_INDICADOR.NOMBRE,
                  PI_INDICADOR.TIPO_META_INDICADOR,
                  PI_INDICADOR_META.VIGENCIA_INICIAL,
                  PI_INDICADOR_META.ID_PLAN,
                  PI_INDICADOR.LB,
                  PI_INDICADOR.REFERENTE,
                  PI_INDICADOR.META_CUATRENIO,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL THEN NVL(PI_INDICADOR_META.META,0)  ELSE 0
                  END) VALOR1A,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL THEN NVL(PI_INDICADOR_META.AVANCE,0) ELSE 0
                  END) VALOR1B,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+1 THEN NVL(PI_INDICADOR_META.META,0) ELSE 0
                  END) VALOR2A,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+1 THEN NVL(PI_INDICADOR_META.AVANCE,0)ELSE 0
                  END) VALOR2B,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+2 THEN NVL(PI_INDICADOR_META.META,0)ELSE 0
                  END) VALOR3A,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+2 THEN NVL(PI_INDICADOR_META.AVANCE,0)ELSE 0
                  END) VALOR3B,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+3 THEN NVL(PI_INDICADOR_META.META,0) ELSE 0
                  END) VALOR4A,
                  SUM(CASE
                    WHEN PI_INDICADOR_META.VIGENCIA_META = PI_INDICADOR_META.VIGENCIA_INICIAL+3 THEN NVL(PI_INDICADOR_META.AVANCE,0)ELSE 0
                  END) VALOR4B
                FROM PI_INDICADOR
                INNER JOIN PI_INDICADOR_META
                ON (PI_INDICADOR.COMPANIA          = PI_INDICADOR_META.COMPANIA)
                AND (PI_INDICADOR.VIGENCIA_INICIAL = PI_INDICADOR_META.VIGENCIA_INICIAL)
                AND (PI_INDICADOR.ID_PLAN          = PI_INDICADOR_META.ID_PLAN)
                AND (PI_INDICADOR.CODIGO           = PI_INDICADOR_META.INDICADOR)
                WHERE PI_INDICADOR.COMPANIA        = UN_COMPANIA
                AND PI_INDICADOR.VIGENCIA_INICIAL  = UN_VIGENCIA
                GROUP BY PI_INDICADOR.CODIGO,
                  PI_INDICADOR.NOMBRE,
                  PI_INDICADOR.TIPO_META_INDICADOR,
                  PI_INDICADOR_META.VIGENCIA_INICIAL,
                  PI_INDICADOR_META.ID_PLAN,
                  PI_INDICADOR.LB,
                  PI_INDICADOR.REFERENTE,
                  PI_INDICADOR.META_CUATRENIO)LOOP

        MI_CAMPOS := 'COMPANIA,                     
                      TIPO,                         
                      NUMERO,                       
                      VIGENCIA_INICIAL,             
                      ID_PLAN,                      
                      INDICADOR,                    
                      NOMBRE,                       
                      LB_INI,                       
                      LB_FIN,                       
                      REFERENTE_INI,
                      REFERENTE_FIN, 
                      META_CUATRENIO_INI,           
                      META_CUATRENIO_FIN,           
                      META1_INI,                    
                      META1_FIN,                    
                      AVANCE1_INI,                  
                      AVANCE1_FIN,                  
                      META2_INI,                    
                      META2_FIN,                    
                      AVANCE2_INI,                  
                      AVANCE2_FIN,                  
                      META3_INI,                    
                      META3_FIN,                    
                      AVANCE3_INI,                  
                      AVANCE3_FIN,                                       
                      META4_INI,                    
                      META4_FIN,                   
                      AVANCE4_INI,                  
                      AVANCE4_FIN,
                      TIPO_INDICADOR';


    MI_VALORES := ''''||UN_COMPANIA||'''                    
                    , '''||UN_TIPO||'''
                    , '||MI_CONS||'
                    , '||MI_RS.VIGENCIA_INICIAL||'
                    , '''||MI_RS.ID_PLAN||'''
                    , '''||MI_RS.INDICADOR||'''
                    , '''||MI_RS.NOMBRE||''' 
                    , '||MI_RS.LB||'
                    , '||MI_RS.LB||'
                    , '||MI_RS.REFERENTE||'
                    , '||MI_RS.REFERENTE||'
                    , '||MI_RS.META_CUATRENIO||'
                    , '||MI_RS.META_CUATRENIO||'
                    , '||MI_RS.VALOR1A||'
                    , '||MI_RS.VALOR1A||'
                    , '||MI_RS.VALOR1B||'
                    , '||MI_RS.VALOR1B||'
                    , '||MI_RS.VALOR2A||'
                    , '||MI_RS.VALOR2A||'
                    , '||MI_RS.VALOR2B||'
                    , '||MI_RS.VALOR2B||'
                    , '||MI_RS.VALOR3A||'
                    , '||MI_RS.VALOR3A||'
                    , '||MI_RS.VALOR3B||'
                    , '||MI_RS.VALOR3B||'
                    , '||MI_RS.VALOR4A||'
                    , '||MI_RS.VALOR4A||'
                    , '||MI_RS.VALOR4B||'
                    , '||MI_RS.VALOR4B||'
                    , '''||MI_RS.TIPO_META_INDICADOR||''' ';                      

    BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR_META_TRA',
                                              UN_ACCION      => 'I',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSINDMETATRA
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;   


  END LOOP RECORER_INDICADORES;


    MI_CAMPOS := 'COMPANIA,
                  TIPO,
                  NUMERO,
                  VIGENCIA_INICIAL,
                  VIGENCIA_META,
                  ID_PLAN,
                  INDICADOR,
                  CODIGO,
                  RUTA_INI,
                  RUTA_FIN,
                  ES_NUEVO,
                  DEBE_ELIMINAR,
                  CREATED_BY,
                  DATE_CREATED';

    MI_VALORES := 'SELECT COMPANIA,
                    '''||UN_TIPO||''',
                    '||MI_CONS||',
                    VIGENCIA_INICIAL,
                    VIGENCIA_META,
                    ID_PLAN,
                    INDICADOR,
                    CODIGO,
                    RUTA,
                    RUTA,
                    0,
                    0,
                    CREATED_BY,
                    DATE_CREATED
                  FROM PI_INDICADOR_META_ANEXO
                  WHERE COMPANIA='''||UN_COMPANIA||'''
                  AND VIGENCIA_INICIAL = '||UN_VIGENCIA;  

    BEGIN

      BEGIN
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR_META_TRA_ANEXO',
                                              UN_ACCION      => 'IS',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_VALORES     => MI_VALORES
                                              );

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                   MI_MSGERROR(1).CLAVE := 'TIPO';
                   MI_MSGERROR(1).VALOR := UN_TIPO;
                   MI_MSGERROR(2).CLAVE := 'NUMERO';
                   MI_MSGERROR(2).VALOR := MI_CONS;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSMETAANEXTRA
                                         ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );


  END;                  



  RETURN MI_CONS;


END FC_GENERARTRANSACCION;

--7
FUNCTION FC_CARGARINFOPRESUPUESTAL
(
 /*
      NAME              : FC_CARGARINFOPRESUPUESTAL --> EN ACCESS Aceptar_Click()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 01/02/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : FUNCION QUE CARGA LA INFORMACION A PARTIR DE UNOS DATOS Y REALIZA LAS OPERACIONES DE ACTUALIZACION E INSERCION
                          A LAS TABLAS BP Y PI
      PARAMETERS        : 


      @NAME:  cargarInformacionPresupuestal
      @METHOD:  POST
    */

  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONSTANTE           IN PI_PLAN_INDICATIVO.NUMERO%TYPE,
  UN_VIGENCIAGUBER       IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE,
  UN_VIGENCIAPRES        IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE,
  UN_NOMBREDEPENDENCIA   IN PI_PLAN_INDICATIVO.DESCRIPCION%TYPE,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO,
  UN_DATOSEXCEL          IN CLOB
)RETURN CLOB

AS 
  MI_RDS                    SYS_REFCURSOR;
  MI_RSCONCEPTOSSINCONF     SYS_REFCURSOR;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_PARAMETRO              VARCHAR2 (15 CHAR) ;
  MI_FILAAUX                PCK_SYSMAN_UTL.T_SPLIT;
  MI_COLAUX                 PCK_SYSMAN_UTL.T_SPLIT;
  MI_SEPARADOR_COL          VARCHAR2(10);
  MI_SEPARADOR_REG          VARCHAR2(10);
  MI_VIGENCIAACT            NUMBER;
  MI_RUBROACT               VARCHAR2(255 CHAR);
  MI_FUENTEACT              VARCHAR2(255 CHAR); 
  MI_PRESUPUESTOACT         NUMBER (20,2);
  MI_COMPROMETIDOACT        NUMBER (20,2);
  MI_PAGADOACT              NUMBER (20,2);
  MI_DISPONIBLEACT          NUMBER (20,2);
  MI_OBLIGACIONESACT        NUMBER (20,2);
  MI_RS                     SYS_REFCURSOR;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RETORNO                CLOB;

BEGIN

    MI_SEPARADOR_COL := PCK_DATOS.GL_SEPARADOR_COL;
    MI_SEPARADOR_REG := PCK_DATOS.GL_SEPARADOR_REG;


    --Antes de cargar la informacion, se deja en cero los valores para que las fuentes queden con el valor que se sube
    MI_CAMPOS := 'VALOR_COMPROMETIDO_FIN = 0, 
                  VALOR_PAGADO_FIN       = 0, 
                  VALOR_DISPONIBLE_FIN   = 0,  
                  VALOR_OBLIGACIONES_FIN = 0 ';


     MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                            UN_NOMBRE    => 'PERMITE CARGA DE VALOR PRESUPUESTADO EN PLAN DE ACCION',
                                            UN_MODULO 	 => PCK_DATOS.MODULOPLANDESARROLLO,
                                            UN_FECHA_PAR => SYSDATE,
                                            UN_IND_MAYUS =>  0)         ;

      IF MI_PARAMETRO = 'SI' THEN

      MI_CAMPOS := MI_CAMPOS|| ', VALOR_PRESUPUESTO_FIN = 0 '; 

      END IF;


    MI_CONDICION := 'COMPANIA           = '''||UN_COMPANIA||'''     
                     AND  TIPO          = ''FIN''                      
                     AND  NUMERO        = '||UN_CONSTANTE||'               
                     AND  VIGENCIA_PLAN = '||UN_VIGENCIAGUBER||'
                     AND  VIGENCIA_META = '||UN_VIGENCIAPRES||' ';


    BEGIN
      BEGIN

              PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_FUENTES',
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );                                 

              EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PLANDES;
      END;
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDFUECARGA                                  
                                        );        

   END;



  MI_FILAAUX :=   PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => UN_DATOSEXCEL
                                             ,UN_DELIMITADOR => MI_SEPARADOR_REG);


  <<REGISTROS>>
  FOR RS IN MI_FILAAUX.FIRST..MI_FILAAUX.LAST 
  LOOP
    MI_COLAUX := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_FILAAUX(RS),
                                                UN_DELIMITADOR  =>  MI_SEPARADOR_COL);     

        MI_VIGENCIAACT      :=  CASE WHEN MI_COLAUX(1) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(1)) END;
        MI_RUBROACT         :=  CASE WHEN MI_COLAUX(2) =  'null' THEN '' ELSE MI_COLAUX(2) END;
        MI_FUENTEACT        :=  CASE WHEN MI_COLAUX(3) =  'null' THEN '' ELSE MI_COLAUX(3) END;        
        MI_PRESUPUESTOACT   :=  CASE WHEN MI_COLAUX(4) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(4)) END;
        MI_COMPROMETIDOACT  :=  CASE WHEN MI_COLAUX(5) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(5)) END;
        MI_PAGADOACT        :=  CASE WHEN MI_COLAUX(6) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(6)) END;
        MI_DISPONIBLEACT    :=  CASE WHEN MI_COLAUX(7) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(7)) END;
        MI_OBLIGACIONESACT  :=  CASE WHEN MI_COLAUX(8) =  'null' THEN 0 ELSE TO_NUMBER(MI_COLAUX(8)) END;

    <<RECORRER_EQUIVALENCIA>>                                                    
    FOR MI_RS IN(SELECT SUBPROYECTO,        
                        PORCENTAJE_PART,    
                        PORCENTAJE_PART_OBL 
                 FROM   BP_EQUIVALENCIAS    
                 WHERE  COMPANIA         = UN_COMPANIA 
                   AND  VIGENCIA_INICIAL = UN_VIGENCIAGUBER
                   AND  VIGENCIA_META    = MI_VIGENCIAACT 
                   AND  RUBRO            = MI_RUBROACT )LOOP

       MI_CONDICION := 'COMPANIA      ='''||UN_COMPANIA||'''  
                    AND TIPO          = ''FIN''
                    AND NUMERO        = '||UN_CONSTANTE||'            
                    AND FUENTE        = '''||MI_FUENTEACT||'''      
                    AND ID_PLAN       = '''||MI_RS.SUBPROYECTO||''' 
                    AND VIGENCIA_PLAN = '||UN_VIGENCIAGUBER||'  
                    AND VIGENCIA_META = '||MI_VIGENCIAACT;                    

      MI_CAMPOS := 'VALOR_COMPROMETIDO_FIN = 0, 
                    VALOR_PAGADO_FIN       = 0, 
                    VALOR_DISPONIBLE_FIN   = 0, 
                    VALOR_OBLIGACIONES_FIN = 0,
                    MODIFIED_BY    = '''||UN_USUARIO||''',
                    DATE_MODIFIED  = SYSDATE ';  


        IF MI_PARAMETRO = 'SI' THEN      
            MI_CAMPOS := MI_CAMPOS|| ', VALOR_PRESUPUESTO_FIN = 0 '; 
        END IF;  
          BEGIN
            BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDFUECARGA                                  
                                              );        

         END;

    MI_CAMPOS := 'VALOR_COMPROMETIDO_FIN = VALOR_COMPROMETIDO_FIN + ('||MI_COMPROMETIDOACT ||' * '||MI_RS.PORCENTAJE_PART||'), 
                  VALOR_PAGADO_FIN       = VALOR_PAGADO_FIN + ('||MI_PAGADOACT ||' * '||MI_RS.PORCENTAJE_PART||'), 
                  VALOR_OBLIGACIONES_FIN = VALOR_OBLIGACIONES_FIN + ('||MI_OBLIGACIONESACT ||' * '||MI_RS.PORCENTAJE_PART_OBL||'),
                  MODIFIED_BY    = '''||UN_USUARIO||''',
                  DATE_MODIFIED  = SYSDATE ';     


     IF MI_PARAMETRO = 'SI' THEN
       MI_CAMPOS := ',VALOR_PRESUPUESTO_FIN = VALOR_PRESUPUESTO_FIN + ('||MI_PRESUPUESTOACT ||' * '||MI_RS.PORCENTAJE_PART||')';
     END IF;

       BEGIN
            BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'FUENTE';
                        MI_MSGERROR(1).VALOR := MI_FUENTEACT;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.SUBPROYECTO;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDFUECARGAEXC 
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );        

       END;


       MI_CAMPOS := 'VALOR_DISPONIBLE_FIN = (VALOR_PRESUPUESTO_FIN - VALOR_COMPROMETIDO_FIN),
                     MODIFIED_BY    = '''||UN_USUARIO||''',
                     DATE_MODIFIED  = SYSDATE ';

        BEGIN
            BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'FUENTE';
                        MI_MSGERROR(1).VALOR := MI_FUENTEACT;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.SUBPROYECTO;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTINDFUECARGAEXC 
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );        

       END; 


    END LOOP RECORRER_EQUIVALENCIA;


  END LOOP REGISTROS;

     PCK_DATOS.GL_RTA:= PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL(UN_COMPANIA   => UN_COMPANIA, 
                                        UN_VIGENCIA   => UN_VIGENCIAGUBER,
                                        UN_CODIGO     => ''  );     

    PCK_PLAN_DESARROLLO.PR_MAYORIZA_COMP_PAG_METAS (UN_COMPANIA  => UN_COMPANIA,
                                                    UN_VIGENCIA  => UN_VIGENCIAGUBER,
                                                    UN_DIGNIVEL  => PCK_PLAN_DESARROLLO.FC_GETM_PRO,
                                                    UN_TIPOT     => 'FIN', 
                                                    UN_NUMEROT   => UN_CONSTANTE,
                                                    UN_USUARIO   => UN_USUARIO);

    PCK_PLAN_DESARROLLO.PR_MAYORIZA_COMP_PAG_PLAN  (UN_COMPANIA  => UN_COMPANIA,
                                                    UN_VIGENCIA  => UN_VIGENCIAGUBER,
                                                    UN_DIGNIVEL  => PCK_PLAN_DESARROLLO.FC_GETM_PRO,
                                                    UN_TIPOT     => 'FIN', 
                                                    UN_NUMEROT   => UN_CONSTANTE,
                                                    UN_USUARIO   => UN_USUARIO);

  MI_RETORNO :=  PCK_PLAN_DESARROLLO1.FC_ACTUALIZARPLANINDICATIVO(UN_COMPANIA           => UN_COMPANIA,
                                                                  UN_VIGENCIA          => UN_VIGENCIAGUBER ,
                                                                  UN_TIPOT             => 'FIN', 
                                                                  UN_NUMEROT           => UN_CONSTANTE,
                                                                  UN_NOMBREDEPENDENCIA => UN_NOMBREDEPENDENCIA,
                                                                  UN_ACCION            => PCK_PLAN_DESARROLLO.FC_GET_ACCION, 
                                                                  UN_USUARIO           => UN_USUARIO);


  RETURN MI_RETORNO;
  END FC_CARGARINFOPRESUPUESTAL;

--8

FUNCTION FC_CODIGODNP
	(

	/*
      NAME              : PR_CARGARINFOPRESUPUESTAL --> EN ACCESS codigoDNP()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : FUNCION QUE DEVUELVE EL CODGIO_DNP SEGUN SU NOMBRE
      PARAMETERS        : UN_NOMBRE      => NOMBRE DE LA FUENTE D LA QUE SE QUIERE OBTENER EL CODIGO


    */
		UN_NOMBRE IN VARCHAR2
	) RETURN PI_FUENTEEQUIV_DNP.CODIGO%TYPE

  IS 
    MI_CODIGO  PI_FUENTEEQUIV_DNP.CODIGO%TYPE;
BEGIN 
    BEGIN
      SELECT CODIGO INTO MI_CODIGO 
      FROM PI_FUENTEEQUIV_DNP 
      WHERE NOMBRE_CODIGO = UN_NOMBRE;		
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
      MI_CODIGO := '';
    END; 

    RETURN MI_CODIGO;

END FC_CODIGODNP;


PROCEDURE PR_TOTALIZAR_FUENTE_PLANEACION

(

	/*
      NAME              : PR_TOTALIZAR_FUENTE_PLANEACION --> EN ACCESS totalizarFuentesPlaneacion()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO QUE SE ENCARGA DE INSERTAR EN LA TABLA TEMPORAL PI_TOTAL_FUENTES_PLANEACION

    */
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA_INICIAL IN PI_INF_PLANACCION.VIGENCIA%TYPE 
)

AS
	MI_TABLA		PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS		PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES		PCK_SUBTIPOS.TI_STRSQL;
	MI_RTA			PCK_SUBTIPOS.TI_RTA_ACME;

BEGIN	

	IF PCK_SYSMAN_UTL.FC_EXISTETABLA(UPPER('PI_TOTAL_FUENTES_PLANEACION')) <> 0 THEN
		DELETE FROM PI_TOTAL_FUENTES_PLANEACION; 		
	END IF;	
	BEGIN
		BEGIN

			MI_TABLA	:= 'PI_TOTAL_FUENTES_PLANEACION';

			MI_CAMPOS	:= 'COMPANIA,
							ID_PLAN,
							VIGENCIA_PLAN,
							VIGENCIA_META,
							CODIGO_DNP,
							VALOR_PRESUPUESTO,
							VALOR_PAGADO';
        MI_VALORES	:= 'SELECT DISTINCT FUENTESBP.COMPANIA,
                      FUENTESBP.ID_PLAN,
                      FUENTESBP.VIGENCIA_PLAN,
                      FUENTESBP.VIGENCIA_META,
                      FUENTE.CODIGO_DNP,
                      FUENTESBP.PRESUPUESTO AS VALOR_PRESUPUESTO,
                      FUENTESBP.PAGADO      AS VALOR_PAGADO
                    FROM BP_PLAN_INDICATIVO_FUENTES FUENTESBP
                    INNER JOIN FUENTE_RECURSOS FUENTE
                    ON FUENTESBP.COMPANIA       = FUENTE.COMPANIA
                    AND FUENTESBP.FUENTE        = FUENTE.CODIGO
                    AND FUENTESBP.VIGENCIA_META = FUENTE.ANO
                  WHERE FUENTESBP.COMPANIA = '''||UN_COMPANIA||'''
                    AND FUENTESBP.VIGENCIA_META = '''||UN_VIGENCIA_INICIAL||'''
                    AND FUENTE.CODIGO_DNP IS NOT NULL';

			MI_RTA	:= PCK_DATOS.FC_ACME(
											UN_TABLA	=>	MI_TABLA,
											UN_ACCION	=>	'IS',
											UN_CAMPOS	=>	MI_CAMPOS,
											UN_VALORES	=>	MI_VALORES
										);

			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
		          RAISE PCK_EXCEPCIONES.EXC_PLANDES;
	    END;

	    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
									UN_EXC_COD    	=>	SQLCODE,
									UN_ERROR_COD 	=>	PCK_ERRORES.ER_PLANDES_INSERT_FUENTES
								  );
	END;	    

END PR_TOTALIZAR_FUENTE_PLANEACION;


PROCEDURE PR_MAYORIZA_COMP_PAG_METAS
(
 /*
      NAME              : PR_MAYORIZA_COMP_PAG_METAS --> EN ACCESS Mayoriza_Comp_Pag_Metas
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 02/03/2018
      TIME              : 08:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO QUE MAYORIZA LOS INDICADORES DE METAS


      @NAME:  mayorizarCompPagMetas
      @METHOD:  POST
    */

  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA            IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE ,
  UN_DIGNIVEL            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOT               IN PI_PLAN_INDICATIVO.TIPO%TYPE, 
  UN_NUMEROT             IN PI_PLAN_INDICATIVO.NUMERO%TYPE,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO

)

AS 
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_RS                     SYS_REFCURSOR;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
  <<RECORRER_FUENTES>>
  FOR MI_RS IN (SELECT PLAN.COMPANIA,
                    PLAN.TIPO,
                    PLAN.NUMERO,
                    PLAN.ID_PLAN,
                    PLAN.VIGENCIA_INICIAL,
                    FUENTE.VIGENCIA_META,
                    SUM(FUENTE.VALOR_PRESUPUESTO_FIN)   PRESUPUESTO,
                    SUM(FUENTE.VALOR_COMPROMETIDO_FIN)  COMPROMETIDO,
                    SUM(FUENTE.VALOR_PAGADO_FIN)        PAGADO,
                    SUM(FUENTE.VALOR_OBLIGACIONES_FIN)  OBLIGACIONES,
                    SUM(FUENTE.VALOR_DISPONIBLE_FIN)    DISPONIBLE
                  FROM PI_PLAN_INDICATIVO PLAN
                  INNER JOIN PI_PLAN_INDICATIVO_FUENTES FUENTE
                    ON  PLAN.COMPANIA          = FUENTE.COMPANIA
                    AND PLAN.VIGENCIA_INICIAL  = FUENTE.VIGENCIA_PLAN
                    AND PLAN.TIPO              = FUENTE.TIPO
                    AND PLAN.NUMERO            = FUENTE.NUMERO
                    AND PLAN.ID_PLAN           = FUENTE.ID_PLAN
                  WHERE  PLAN.COMPANIA                 = UN_COMPANIA
                    AND  PLAN.VIGENCIA_INICIAL         = UN_VIGENCIA
                    AND  LENGTH(FUENTE.ID_PLAN)        = UN_DIGNIVEL
                    AND  PLAN.TIPO                     = UN_TIPOT
                    AND  PLAN.NUMERO                   = UN_NUMEROT     
                  GROUP BY PLAN.COMPANIA,
                    PLAN.TIPO,
                    PLAN.NUMERO,
                    PLAN.ID_PLAN,
                    PLAN.VIGENCIA_INICIAL,
                    FUENTE.VIGENCIA_META)LOOP

        MI_CAMPOS := 'VALOR_PRESUPUESTO_FIN  = '||MI_RS.PRESUPUESTO||',   
                      VALOR_COMPROMETIDO_FIN = '||MI_RS.COMPROMETIDO||',  
                      VALOR_PAGADO_FIN       = '||MI_RS.PAGADO||',        
                      VALOR_OBLIGACIONES_FIN = '||MI_RS.OBLIGACIONES||',  
                      VALOR_DISPONIBLE_FIN   = '||MI_RS.DISPONIBLE||',
                      MODIFIED_BY            = '''||UN_USUARIO||''',
                      DATE_MODIFIED          = SYSDATE ';

        MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA||'''                          
                      AND  TIPO          = '''||UN_TIPOT||'''                     
                      AND  NUMERO        = '''||UN_NUMEROT||'''                      
                      AND  ID_PLAN       = '''||MI_RS.ID_PLAN||'''                                    
                      AND  VIGENCIA_PLAN = '||UN_VIGENCIA||'                
                      AND  VIGENCIA_PLAN = '||MI_RS.VIGENCIA_INICIAL||'
                      AND  VIGENCIA_META = '||MI_RS.VIGENCIA_META||' ';

      BEGIN
            BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_METAS',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIMETASMAYORIZA 
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );        

       END;        


  END LOOP RECORRER_FUENTES;


  <<RECORRER_FUENTES>>
  FOR MI_RS IN (SELECT PLAN.COMPANIA,
                    PLAN.TIPO,
                    PLAN.NUMERO,
                    PLAN.ID_PLAN,
                    PLAN.VIGENCIA_INICIAL,
                    FUENTE.VIGENCIA_META,
                    FUENTE.FUENTE,
                    SUM(FUENTE.VALOR_PRESUPUESTO_FIN)   PRESUPUESTO,
                    SUM(FUENTE.VALOR_COMPROMETIDO_FIN)  COMPROMETIDO,
                    SUM(FUENTE.VALOR_PAGADO_FIN)        PAGADO,
                    SUM(FUENTE.VALOR_OBLIGACIONES_FIN)  OBLIGACIONES,
                    SUM(FUENTE.VALOR_DISPONIBLE_FIN)    DISPONIBLE
                  FROM PI_PLAN_INDICATIVO PLAN
                  INNER JOIN PI_PLAN_INDICATIVO_FUENTES FUENTE
                    ON  PLAN.COMPANIA          = FUENTE.COMPANIA
                    AND PLAN.VIGENCIA_INICIAL  = FUENTE.VIGENCIA_PLAN
                    AND PLAN.TIPO              = FUENTE.TIPO
                    AND PLAN.NUMERO            = FUENTE.NUMERO
                    AND PLAN.ID_PLAN           = FUENTE.ID_PLAN
                  WHERE  PLAN.COMPANIA                 = UN_COMPANIA
                    AND  PLAN.VIGENCIA_INICIAL         = UN_VIGENCIA
                    AND  LENGTH(FUENTE.ID_PLAN)        = UN_DIGNIVEL
                    AND  PLAN.TIPO                     = UN_TIPOT
                    AND  PLAN.NUMERO                   = UN_NUMEROT     
                  GROUP BY PLAN.COMPANIA,
                    PLAN.TIPO,
                    PLAN.NUMERO,
                    PLAN.ID_PLAN,
                    PLAN.VIGENCIA_INICIAL,
                    FUENTE.VIGENCIA_META,
                    FUENTE.FUENTE)LOOP

        MI_CAMPOS := 'VALOR_PRESUPUESTO_FIN  = '||MI_RS.PRESUPUESTO||',   
                      VALOR_COMPROMETIDO_FIN = '||MI_RS.COMPROMETIDO||',  
                      VALOR_PAGADO_FIN       = '||MI_RS.PAGADO||',        
                      VALOR_OBLIGACIONES_FIN = '||MI_RS.OBLIGACIONES||',  
                      VALOR_DISPONIBLE_FIN   = '||MI_RS.DISPONIBLE||',
                      MODIFIED_BY            = '''||UN_USUARIO||''',
                      DATE_MODIFIED          = SYSDATE ';

        MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA||'''                          
                      AND  TIPO          = '''||UN_TIPOT||'''                     
                      AND  NUMERO        = '''||UN_NUMEROT||'''                      
                      AND  ID_PLAN       = '''||MI_RS.ID_PLAN||'''                                    
                      AND  VIGENCIA_PLAN = '||MI_RS.VIGENCIA_INICIAL||'                
                      AND  VIGENCIA_META = '||MI_RS.VIGENCIA_META||'
                      AND  FUENTE        = '''||MI_RS.FUENTE||''' ';

                 BEGIN
            BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIFUENTEMAYORIZA 
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );        

       END;        


  END LOOP RECORRER_FUENTES;

END PR_MAYORIZA_COMP_PAG_METAS;


PROCEDURE PR_MAYORIZA_COMP_PAG_PLAN
(
 /*
      NAME              : PR_MAYORIZA_COMP_PAG_PLAN --> EN ACCESS Mayoriza_Comp_Pag_Plan
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 02/03/2018
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO QUE MAYORIZA LOS INDICADORES DEL PLAN


      @NAME:  mayorizarCompPagPlan
      @METHOD:  POST
    */

  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA            IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE ,
  UN_DIGNIVEL            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOT               IN PI_PLAN_INDICATIVO.TIPO%TYPE, 
  UN_NUMEROT             IN PI_PLAN_INDICATIVO.NUMERO%TYPE,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO

)

AS 
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_PARAMETRO              VARCHAR2 (15 CHAR) ;
  MI_FILAAUX                PCK_SYSMAN_UTL.T_SPLIT;
  MI_COLAUX                 PCK_SYSMAN_UTL.T_SPLIT;
  MI_SEPARADOR_COL          VARCHAR2(10);
  MI_SEPARADOR_REG          VARCHAR2(10);
  MI_VIGENCIAACT            NUMBER;
  MI_RUBROACT               VARCHAR2(255 CHAR);
  MI_FUENTEACT              VARCHAR2(255 CHAR); 
  MI_PRESUPUESTOACT         NUMBER (20,2);
  MI_COMPROMETIDOACT        NUMBER (20,2);
  MI_PAGADOACT              NUMBER (20,2);
  MI_DISPONIBLEACT          NUMBER (20,2);
  MI_OBLIGACIONESACT        NUMBER (20,2);
  MI_RS                     SYS_REFCURSOR;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  <<RECORRER_FUENTES>>

  FOR MI_RS IN (SELECT PLAN.COMPANIA,
                  PLAN.ID_PLAN,
                  PLAN.VIGENCIA_INICIAL,
                  SUM(FUENTE.VALOR_PRESUPUESTO_FIN) PRESUPUESTO,
                  SUM(FUENTE.VALOR_COMPROMETIDO_FIN) COMPROMETIDO,
                  SUM(FUENTE.VALOR_OBLIGACIONES_FIN) OBLIGACIONES,
                  SUM(FUENTE.VALOR_PAGADO_FIN) PAGADO
                FROM PI_PLAN_INDICATIVO PLAN
                  INNER JOIN PI_PLAN_INDICATIVO_FUENTES FUENTE
                    ON PLAN.COMPANIA            = FUENTE.COMPANIA
                    AND PLAN.VIGENCIA_INICIAL   = FUENTE.VIGENCIA_PLAN
                    AND PLAN.ID_PLAN            = FUENTE.ID_PLAN
                    AND PLAN.TIPO               = FUENTE.TIPO
                    AND PLAN.NUMERO             = FUENTE.NUMERO
                WHERE PLAN.COMPANIA           = UN_COMPANIA
                  AND PLAN.VIGENCIA_INICIAL   = UN_VIGENCIA
                  AND PLAN.TIPO               = UN_TIPOT
                  AND PLAN.NUMERO             = UN_NUMEROT
                  AND LENGTH(FUENTE.ID_PLAN) <= UN_DIGNIVEL
                GROUP BY PLAN.COMPANIA,
                  PLAN.ID_PLAN,
                  PLAN.VIGENCIA_INICIAL )LOOP

    MI_CAMPOS := 'VALOR_PRESUPUESTO_FIN  = '||MI_RS.PRESUPUESTO||',
                  VALOR_COMPROMETIDO_FIN = '||MI_RS.COMPROMETIDO||',
                  VALOR_OBLIGACIONES_FIN = '||MI_RS.OBLIGACIONES||',
                  VALOR_PAGADO_FIN       = '||MI_RS.PAGADO||',
                  MODIFIED_BY            = '''||UN_USUARIO||''',
                  DATE_MODIFIED          = SYSDATE ';        


    MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''                          
                AND  ID_PLAN          = '''||MI_RS.ID_PLAN||'''                                    
                AND  VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                AND  TIPO             = '''||UN_TIPOT||'''                     
                AND  NUMERO           = '''||UN_NUMEROT||''' ';              

    BEGIN
       BEGIN

                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => 'PI_PLAN_INDICATIVO',
                                                    UN_ACCION      => 'M',
                                                    UN_CAMPOS      => MI_CAMPOS,
                                                    UN_CONDICION   => MI_CONDICION
                                                    );                                 

                    EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANDES;
       END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPINDICAOMAYZA 
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );        

    END;                  

  END LOOP RECORRER_FUENTES;

END PR_MAYORIZA_COMP_PAG_PLAN;

PROCEDURE PR_ACTUALIZAFUENTETIPO2 
(	
	/*
      NAME              : PR_ACTUALIZAFUENTETIPO2 --> EN ACCESS actualizaFuenteTipo2()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO QUE SE ENCARGA DE ACTUALIZAR CAMPOS EN LA TABLA TEMPORAL INF_PLANACCION

    */
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA_META IN PI_TOTAL_FUENTES_PLANEACION.VIGENCIA_META%TYPE,
	UN_CAMPO IN  PCK_SUBTIPOS.TI_CAMPOS,
	UN_STREQUIV IN PI_TOTAL_FUENTES_PLANEACION.CODIGO_DNP%TYPE,
	UN_DESTINO  IN  PCK_SUBTIPOS.TI_CAMPOS
)

AS 

	MI_TABLA        PCK_SUBTIPOS.TI_TABLA; 
	MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;  
	MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
	MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
	MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;

BEGIN
	BEGIN

		MI_TABLA := 'PI_INF_PLANACCION';

		MI_MERGEUSING :=  'SELECT COMPANIA,VIGENCIA_META,ID_PLAN,VALOR_'|| UN_DESTINO || '
		FROM PI_TOTAL_FUENTES_PLANEACION 
		WHERE VIGENCIA_META =  '''|| UN_VIGENCIA_META || '''       
		AND CODIGO_DNP    = ''' || UN_STREQUIV || '''
		AND COMPANIA  = ''' || UN_COMPANIA || ''' ';

		MI_MERGEENLACE := ' VISTA.COMPANIA = TABLA.COMPANIA
		AND VISTA.VIGENCIA_META = TABLA.VIGENCIA
		AND VISTA.ID_PLAN = TABLA.PROYECTO ';

		MI_MERGEEXISTE := 'UPDATE SET TABLA.'|| UN_CAMPO ||' = VISTA.VALOR_'|| UN_DESTINO || ' ';

		MI_RTA := PCK_DATOS.FC_ACME(
										UN_TABLA => MI_TABLA,
										UN_ACCION => 'MM' ,
										UN_MERGEUSING => MI_MERGEUSING, 
										UN_MERGEENLACE => MI_MERGEENLACE , 
										UN_MERGEEXISTE => MI_MERGEEXISTE
									);

		 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
        	THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;	
    END;	

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   =>SQLCODE,
        UN_ERROR_COD =>PCK_ERRORES.ERRR_CONTABILIDAD_COPIANIIF
      );								                  

END PR_ACTUALIZAFUENTETIPO2;


--13
PROCEDURE PR_ACTCAMPOSRECURSO_PLANACCION
(
	/*
      NAME              : PR_ACTCAMPOSRECURSO_PLANACCION --> EN ACCESS actualizarCamposRecurso_PlanAccion()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO REALIZA LA ACTUALIZACIÓN DE LOS DIFERENTES CAMPOS DE PRESUPUESTO TENIENDO
      					 EN CUENTA EL CODIGO DNP DE LA FUENTE DE RECURSO.	                        
    */
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA IN PCK_SUBTIPOS.TI_ENTERO,
	UN_DESTINO  IN  PCK_SUBTIPOS.TI_CAMPOS
)

AS
	MI_CODIGO_EQUIV		PCK_SUBTIPOS.TI_CAMPOS;

	MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
	MI_RTA                   PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN 

	-- 'actualizar el campo de presupuesto Cofinanciación Departamento
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Cofinanciación Departamento');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_COFDEPARMENTAL',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto Cofinanciación Nacional
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Cofinanciación Nacion');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_COFNACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto Credito
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Credito');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_CREDITO',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto Otros
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Otros');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_OTROS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto Regalías
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Regalías');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_REGALIAS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto Recursos Propios
	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Recursos Propios');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_RECURSOSPROPIOS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto SGP Alimentacion Escolar

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Alimentacion Escolar');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_ALIMESCOLAR',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP APSB

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP APSB');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_APSB',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Cultura

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Cultura');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_CULTURA',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Deporte 

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Deporte');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_DEPORTE',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Educacion

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Educacion');
	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_EDUCACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Libre Destinación 42% Mpios 4, 5 y 6 Cat

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Libre Destinación 42% Mpios 4, 5 y 6 Cat');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_LIBREDESTINACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto SGP Destinación especifica


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Destinación especifica');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_DESTINACIONESPEC',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto SGP Libre Inversion

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Libre Inversion');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_LIBREINVERSION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Municipios Rio Magdalena

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Municipios Rio Magdalena');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_MCPIOS_RIOMAG',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;


	--'actualizar el campo de presupuesto SGP Salud


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Salud');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'RECURSO_SGP_SALUD',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	---'actualizar el total con la sumatoria de los anteriores campos

	BEGIN
		BEGIN

			MI_TABLA 	:= 'PI_INF_PLANACCION';

			MI_CAMPOS 	:= 'RECURSO_TOTAL = RECURSO_COFDEPARMENTAL + RECURSO_COFNACION + RECURSO_CREDITO  
						    + RECURSO_OTROS + RECURSO_RECURSOSPROPIOS + RECURSO_SGP_ALIMESCOLAR  
						    + RECURSO_SGP_APSB + RECURSO_SGP_CULTURA + RECURSO_SGP_DEPORTE 
						    + RECURSO_SGP_EDUCACION + RECURSO_SGP_LIBREDESTINACION + RECURSO_SGP_DESTINACIONESPEC  
						    + RECURSO_SGP_LIBREINVERSION + RECURSO_SGP_MCPIOS_RIOMAG + RECURSO_SGP_SALUD  
						    + RECURSO_REGALIAS';		

			MI_RTA 		:= PCK_DATOS.FC_ACME 	(
													UN_TABLA  => MI_TABLA,
													UN_ACCION => 'M',
													UN_CAMPOS => MI_CAMPOS
												);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
			RAISE PCK_EXCEPCIONES.EXC_PLANDES;

		END;

		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN
			PCK_ERR_MSG.RAISE_WITH_MSG(
				UN_EXC_COD =>SQLCODE
				,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACT_INFPLAN_ACCION
				);														    	
	END;	


END PR_ACTCAMPOSRECURSO_PLANACCION; 

PROCEDURE PR_ACT_EJECUTADO_PLANACCION
(
	/*
      NAME              : PR_ACT_EJECUTADO_PLANACCION --> EN ACCESS actualizarCamposEjecutado_PlanAccion()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO REALIZA LA ACTUALIZACIÓN DE LOS DIFERENTES CAMPOS DE EJECUTADO
      						TENIENDO EN CUENTA EL CODIGO DNP DE LA FUENTE DE RECURSO	                        
    */	
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA IN PCK_SUBTIPOS.TI_ENTERO,
	UN_DESTINO  IN  PCK_SUBTIPOS.TI_CAMPOS
)

AS
	MI_CODIGO_EQUIV		PCK_SUBTIPOS.TI_CAMPOS;

	MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
	MI_RTA                   PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN 

	--  'actualizar el campo de presupuesto Cofinanciación Departamento

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Cofinanciación Departamento');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_COFDEPARMENTAL',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto Cofinanciación Nacional

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Cofinanciación Nacion');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_COFNACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	-- 'actualizar el campo de presupuesto Credito

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Credito');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_CREDITO',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	--actualizar el campo de presupuesto Otros

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Otros');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_OTROS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	


	-- 'actualizar el campo de presupuesto Regalías


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Regalías');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_REGALIAS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	-- 'actualizar el campo de presupuesto Recursos Propios

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('Recursos Propios');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_RECURSOSPROPIOS',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	-- 'actualizar el campo de presupuesto SGP Alimentacion Escolar

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Alimentacion Escolar');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_ALIMESCOLAR',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;		

	-- 'actualizar el campo de presupuesto SGP APSB

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP APSB');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_APSB',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;		

	-- 'actualizar el campo de presupuesto SGP Cultura


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Cultura');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_CULTURA',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;			

	--'actualizar el campo de presupuesto SGP Deporte

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Deporte');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_DEPORTE',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;			

	--'actualizar el campo de presupuesto SGP Educacion

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Educacion');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_EDUCACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	-- 'actualizar el campo de presupuesto SGP Libre Destinación 42% Mpios 4, 5 y 6 Cat

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Libre Destinación 42% Mpios 4, 5 y 6 Cat');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_LIBREDESTINACION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;	

	-- 'actualizar el campo de presupuesto SGP Destinación especifica

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Destinación especifica');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_DESTINACIONESPEC',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	--'actualizar el campo de presupuesto SGP Libre Inversion

	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Libre Inversion');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_LIBREINVERSION',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Municipios Rio Magdalena


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Municipios Rio Magdalena');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_MCPIOS_RIOMAG',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;

	-- 'actualizar el campo de presupuesto SGP Salud


	MI_CODIGO_EQUIV := PCK_PLAN_DESARROLLO.FC_CODIGODNP('SGP Salud');

	IF (MI_CODIGO_EQUIV IS NOT NULL) OR (MI_CODIGO_EQUIV <> '') THEN
		PCK_PLAN_DESARROLLO.PR_ACTUALIZAFUENTETIPO2
			(
				UN_COMPANIA => UN_COMPANIA,
				UN_VIGENCIA_META => UN_VIGENCIA , 
				UN_CAMPO => 'EJECUTADO_SGP_SALUD',
				UN_STREQUIV => MI_CODIGO_EQUIV,
				UN_DESTINO => UN_DESTINO  
			);
	END IF;


    --'actualizar el total con la sumatoria de los anteriores campos

	BEGIN
	  BEGIN

            MI_TABLA    := 'PI_INF_PLANACCION';

            MI_CAMPOS	:= 'EJECUTADO_TOTAL = EJECUTADO_COFDEPARMENTAL + EJECUTADO_COFNACION + EJECUTADO_CREDITO  
						   + EJECUTADO_OTROS + EJECUTADO_RECURSOSPROPIOS + EJECUTADO_SGP_ALIMESCOLAR  
						   + EJECUTADO_SGP_APSB + EJECUTADO_SGP_CULTURA + EJECUTADO_SGP_DEPORTE 
						   + EJECUTADO_SGP_EDUCACION + EJECUTADO_SGP_LIBREDESTINACION + EJECUTADO_SGP_DESTINACIONESPEC  
						   + EJECUTADO_SGP_LIBREINVERSION + EJECUTADO_SGP_MCPIOS_RIOMAG + EJECUTADO_SGP_SALUD  
						   + EJECUTADO_REGALIAS';

			MI_RTA      := PCK_DATOS.FC_ACME    (
			                                        UN_TABLA  => MI_TABLA,
			                                        UN_ACCION => 'M',
			                                        UN_CAMPOS => MI_CAMPOS
			                                    );

	  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
	      RAISE PCK_EXCEPCIONES.EXC_PLANDES;

	  END;

	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN
	            PCK_ERR_MSG.RAISE_WITH_MSG
	            (
	                UN_EXC_COD 		=>	SQLCODE,
	                UN_ERROR_COD	=>	PCK_ERRORES.ER_PLANDES_ACT_INFPLAN_ACCION
	            );                                                              
    END;

END PR_ACT_EJECUTADO_PLANACCION;

FUNCTION FC_VALIDARSALDOFUENTES
(
 /*
      NAME              : FC_VALIDARSALDOFUENTES --> EN ACCESS validarSaldoFuentes()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 02/03/2018
      TIME              : 14:00 
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : FUNCION QUE VALIDA LOS SALDOS DE LAS FUENTES PARA RETORNAR INCONSISTENCIAS                                

      @NAME:  validarSaldoFuentes
      @METHOD:  GET
    */

  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA            IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE ,
  UN_TIPOT               IN PI_PLAN_INDICATIVO.TIPO%TYPE, 
  UN_NUMEROT             IN PI_PLAN_INDICATIVO.NUMERO%TYPE

)RETURN CLOB

AS 

  MI_RS                     SYS_REFCURSOR;
  MI_RSGASTADO              SYS_REFCURSOR;
  MI_RSPROYECTADO           SYS_REFCURSOR;
  MI_RS1                    SYS_REFCURSOR;
  MI_SALDODISPONIBLE        NUMBER(20,2);
  MI_PROYECTADO             NUMBER(20,2);
  MI_NOMSUBPROGRAMA         VARCHAR(1000);
  MI_RETORNO                CLOB;

BEGIN

  MI_RETORNO := '';

<<RECORRER_FUENTES>>
  FOR MI_RS IN (SELECT PI.COMPANIA,                          
                      PI.ID_PLAN,                           
                      PI.VIGENCIA_PLAN,                     
                      PI.VIGENCIA_META,                     
                      PI.FUENTE,                            
                      FR.NOMBRE AS NOM_FUENTE,              
                      NVL(BP.PRESUPUESTO,0) + (PI.VALOR_PRESUPUESTO_FIN - PI.VALOR_PRESUPUESTO_INI)  PRESUPUESTO 
                FROM PI_PLAN_INDICATIVO_FUENTES PI        
                  INNER JOIN FUENTE_RECURSOS FR               
                      ON PI.COMPANIA      = FR.COMPANIA        
                     AND PI.VIGENCIA_META = FR.ANO             
                     AND PI.FUENTE        = FR.CODIGO
                  LEFT JOIN BP_PLAN_INDICATIVO_FUENTES BP    
                     ON PI.COMPANIA      = BP.COMPANIA        
                    AND PI.ID_PLAN       = BP.ID_PLAN         
                    AND PI.VIGENCIA_PLAN = BP.VIGENCIA_PLAN   
                    AND PI.VIGENCIA_META = BP.VIGENCIA_META   
                    AND PI.FUENTE        = BP.FUENTE          
                  WHERE  PI.COMPANIA = UN_COMPANIA           
                    AND  PI.TIPO     = UN_TIPOT         
                    AND  PI.NUMERO   = UN_NUMEROT
                     )LOOP

      <<CONSULTAR_GASTADO>>                  
        FOR MI_RSGASTADO IN (SELECT SUM(BP.PRESUPUESTO)  GASTADO 
                             FROM   BP_PLAN_INDICATIVO_FUENTES BP                         
                             WHERE  BP.COMPANIA      = UN_COMPANIA             
                               AND  BP.VIGENCIA_PLAN = MI_RS.VIGENCIA_PLAN
                               AND  BP.VIGENCIA_META = MI_RS.VIGENCIA_META
                               AND  BP.FUENTE        = MI_RS.FUENTE             
                               AND  BP.ID_PLAN IN (                                       
                                      SELECT ID                                           
                                      FROM   BP_PLAN_INDICATIVO                           
                                      WHERE  COMPANIA         = UN_COMPANIA    
                                        AND  VIGENCIA_INICIAL = MI_RS.VIGENCIA_PLAN
                                        AND  VIGENCIA_META    = MI_RS.VIGENCIA_META
                                        AND  PREDECESOR       = MI_RS.ID_PLAN)                                                   
                             GROUP  BY BP.FUENTE )LOOP

          <<CONSULTAR_PROYECTADO>>      
             FOR MI_RSPROYECTADO IN (SELECT SUM(PI.VALOR_PRESUPUESTO_FIN - PI.VALOR_PRESUPUESTO_INI)  PROYECTADO 
                                     FROM   PI_PLAN_INDICATIVO_FUENTES PI                         
                                     WHERE  PI.COMPANIA      = UN_COMPANIA              
                                       AND  PI.VIGENCIA_PLAN = MI_RS.VIGENCIA_PLAN
                                       AND  PI.VIGENCIA_META = MI_RS.VIGENCIA_META           
                                       AND  PI.FUENTE        = MI_RS.FUENTE                
                                       AND  PI.TIPO          = UN_TIPOT
                                       AND  PI.NUMERO        = UN_NUMEROT
                                       AND  PI.ID_PLAN IN (                                       
                                              SELECT ID_PLAN                                      
                                              FROM   PI_PLAN_INDICATIVO                           
                                              WHERE  COMPANIA         = UN_COMPANIA     
                                                AND  VIGENCIA_INICIAL = MI_RS.VIGENCIA_PLAN 
                                                AND  PREDECESOR       = MI_RS.ID_PLAN
                                                AND  TIPO             = UN_TIPOT           
                                                AND  NUMERO           = UN_NUMEROT
                                              )                                                   
                                     GROUP  BY PI.FUENTE)LOOP

                    MI_PROYECTADO := NVL(MI_RSPROYECTADO.PROYECTADO,0);

                    MI_SALDODISPONIBLE := MI_RS.PRESUPUESTO - NVL(MI_RSGASTADO.GASTADO,0);


                        IF MI_PROYECTADO > MI_SALDODISPONIBLE THEN

                        <<DESCRIPCION_PINDICATIVO>>
                          FOR MI_RS1 IN(SELECT DESCRIPCION 
                                         FROM   PI_PLAN_INDICATIVO 
                                         WHERE  COMPANIA = UN_COMPANIA 
                                           AND  TIPO     = UN_TIPOT
                                           AND  NUMERO   = UN_NUMEROT
                                           AND  ID_PLAN  = MI_RS.ID_PLAN 
                                           AND  VIGENCIA_INICIAL = MI_RS.VIGENCIA_PLAN)LOOP


                            MI_NOMSUBPROGRAMA :=  ' : ' ||MI_RS1.DESCRIPCION;               

                          END LOOP DESCRIPCION_PINDICATIVO;

                            MI_RETORNO := MI_RETORNO ||'('||MI_RS.ID_PLAN||')'||MI_NOMSUBPROGRAMA||' - '||MI_RS.NOM_FUENTE||' ('||MI_RS.VIGENCIA_META||')'
                             ||(CHR(10))||(CHR(13))
                             ||'  - Total Presupuestado = $'||LTRIM(TO_CHAR(MI_RS.PRESUPUESTO,'999,999,999,999,999,999.99')) ||(CHR(10))||(CHR(13))
                             ||'  - Saldo Disponible    = $'||LTRIM(TO_CHAR(MI_SALDODISPONIBLE,'999,999,999,999,999,999.99')) ||(CHR(10))||(CHR(13)) 
                             ||'  - Valor a Registrar   = $'||LTRIM(TO_CHAR(MI_PROYECTADO,'999,999,999,999,999,999.99')) ||(CHR(10))||(CHR(13)) ||(CHR(10))||(CHR(13));               

                        END IF;

             END LOOP CONSULTAR_PROYECTADO;

        END LOOP CONSULTAR_GASTADO;

  END LOOP RECORRER_FUENTES;

  RETURN MI_RETORNO;

END FC_VALIDARSALDOFUENTES;

--15
FUNCTION FC_LISTAFUENTESIDPLAN
  /*
    NAME              : Funcion -> En Access: listaFuentesIdPlan
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JHON FREDY HERNANDEZ CASTRO
    DATE MIGRATION    : 02/03/2018
    TIME              : 05:15 PM
    SOURCE MODULE     : Plan de Desarrollo Menu/Informes/Plan de Adquisiciones (SysmanDES2018.01.01)
    DESCRIPTION       : Retorna concatenado los nombres de las fuentes que se relacionan entre BP_PLAN_INDICATIVO_FUENTES y FUENTE_RECURSOS
                        segun una compania, una vigencia, y un id plan.
    MODIFIED BY       :
    PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_VIGENCIA => UN AÑO DE VIGENCIA INICIAL DEL PLAN
                        UN_IDPLAN   => IDENTIFICADOR DEL PLAN

    @NAME  : listarFuentesIdPlan
    @METHOD: GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VIGENCIA IN PCK_SUBTIPOS.TI_ANIO,
    UN_IDPLAN   IN BP_PLAN_INDICATIVO_FUENTES.ID_PLAN%TYPE 
  )
  RETURN PCK_SUBTIPOS.TI_RTA_ACME 
  AS
    NOMBRE_CONCAT PCK_SUBTIPOS.TI_RTA_ACME ;
  BEGIN
    FOR MI_RS IN
    (
      SELECT DISTINCT FUENTE_RECURSOS.NOMBRE
        FROM BP_PLAN_INDICATIVO_FUENTES
        INNER JOIN FUENTE_RECURSOS
           ON BP_PLAN_INDICATIVO_FUENTES.COMPANIA      = FUENTE_RECURSOS.COMPANIA
          AND BP_PLAN_INDICATIVO_FUENTES.VIGENCIA_PLAN = FUENTE_RECURSOS.ANO
          AND BP_PLAN_INDICATIVO_FUENTES.FUENTE        = FUENTE_RECURSOS.CODIGO
      WHERE BP_PLAN_INDICATIVO_FUENTES.COMPANIA      = UN_COMPANIA
        AND BP_PLAN_INDICATIVO_FUENTES.VIGENCIA_PLAN = UN_VIGENCIA
        AND BP_PLAN_INDICATIVO_FUENTES.ID_PLAN       = UN_IDPLAN
    )
    LOOP
      NOMBRE_CONCAT := NOMBRE_CONCAT || MI_RS.NOMBRE||',';
    END LOOP;
    NOMBRE_CONCAT := SUBSTR(NOMBRE_CONCAT,1,LENGTH(NOMBRE_CONCAT)-1);
  RETURN NOMBRE_CONCAT;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN '';
END FC_LISTAFUENTESIDPLAN;

--16 

FUNCTION FC_PREPARARINFPLANACCION
(

/*
      NAME              : PR_PREPARARINFPLANACCION --> EN ACCESS prepararInfPlanAccion()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
      DATE MIGRADOR     : 02/03/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : FUNCION QUE REALIZA LA ACTUALIZACIÓN DE LAS DIFERENTES TABLAS TEMPORALES PARA PERPARAR EL ARCHIVO EXCEL. 

      @NAME  : prepararInfPlanAccion
    */


	UN_COMPANIA				      IN		PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA_CONSULTA_I	IN   	PI_INF_PLANACCION.VIGENCIA%TYPE,
	UN_VIGENCIA_CONSULTA_F	IN  	PI_INF_PLANACCION.VIGENCIA%TYPE

)RETURN CLOB

IS

	MI_PARDIGITOSEJE			PARAMETRO.VALOR%TYPE;
	MI_PARDIGITOSPOLITICA		PARAMETRO.VALOR%TYPE;
	MI_CARGANIVEL				NUMBER;

	MI_TABLA			PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS			PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES			PCK_SUBTIPOS.TI_STRSQL;
	MI_RTA				PCK_SUBTIPOS.TI_RTA_ACME;

  	MI_CONTADOR 		NUMBER := 0;
  	MI_SALIDA_H1  	  	CLOB;
  	MI_SALIDA_H2  	  	CLOB;
    MI_RET              CLOB;

BEGIN




	MI_PARDIGITOSEJE	:= 	PCK_SYSMAN_UTL.FC_PAR
			                    (UN_COMPANIA    => UN_COMPANIA
			                    ,UN_NOMBRE      => 'NUMERO DE DIGITOS EJE'
			                    ,UN_MODULO      => PCK_DATOS.MODULOPLANDESARROLLO
			                    ,UN_FECHA_PAR   => SYSDATE );

	MI_PARDIGITOSPOLITICA	:=		PCK_SYSMAN_UTL.FC_PAR
			                    (UN_COMPANIA    => UN_COMPANIA
			                    ,UN_NOMBRE      => 'NUMERO DE DIGITOS POLITICA'
			                    ,UN_MODULO      => PCK_DATOS.MODULOPLANDESARROLLO
			                    ,UN_FECHA_PAR   => SYSDATE );

	IF MI_PARDIGITOSEJE IS NULL OR MI_PARDIGITOSEJE = '' OR PCK_SYSMAN_UTL.FC_ES_NUMERO(MI_PARDIGITOSEJE) <> -1
	THEN
		RETURN NULL;
	END IF;

	IF MI_PARDIGITOSPOLITICA IS NULL OR MI_PARDIGITOSPOLITICA = '' OR PCK_SYSMAN_UTL.FC_ES_NUMERO(MI_PARDIGITOSPOLITICA) <> -1
	THEN
		RETURN NULL;
	END IF;

	MI_CARGANIVEL := PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL(UN_COMPANIA => UN_COMPANIA, UN_VIGENCIA => UN_VIGENCIA_CONSULTA_I);


	IF MI_CARGANIVEL IS NOT NULL AND MI_CARGANIVEL <> 0 THEN

		IF PCK_SYSMAN_UTL.FC_EXISTETABLA(UPPER('PI_INF_PLANACCION')) <> 0 THEN
			BEGIN


				    BEGIN
				      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PI_INF_PLANACCION', 
				                                          UN_ACCION    => 'E', 
				                                          UN_CONDICION => '1=1');
				    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
						RAISE PCK_EXCEPCIONES.EXC_PLANDES;
					END;  
			END;	

		END IF;	

		-- 'consultar los id de plan indicativo que cumple con la caracteristica de ser un indicador de producto.
		BEGIN
			BEGIN

				MI_TABLA	:= 'PI_INF_PLANACCION';

				MI_CAMPOS	:= 'COMPANIA,
                      VIGENCIA,
                      EJE,
                      POLITICA,
                      PROGRAMA,
                      SUBPROGRAMA,
                      PROYECTO,
                      BPIM,
                      RESPONSABLE';

				MI_VALORES	:='	SELECT	BPPLAN.COMPANIA, 
										BPPLAN.VIGENCIA_INICIAL AS VIGENCIA,
							           	SUBSTR(BPPLAN.ID,1, ' || MI_PARDIGITOSEJE || ') AS EJE, 
							           	SUBSTR(BPPLAN.ID,1, ' || MI_PARDIGITOSPOLITICA  || ' ) AS POLITICA,  
							           	SUBSTR(BPPLAN.ID,1, ' || PCK_PLAN_DESARROLLO.FC_GETM_RES || ' ) AS PROGRAMA,
							           	SUBSTR(BPPLAN.ID,1, ' || PCK_PLAN_DESARROLLO.FC_GETM_PRO || ' ) AS SUBPROGRAMA,  
							           	SUBSTR(BPPLAN.ID,1, ' ||PCK_PLAN_DESARROLLO.FC_GET_ACCION || ') AS PROYECTO,
							           	BPPLAN.CODIGOBPIM AS BPIM,
                        	TER.NOMBRE AS RESPONSABLE
								FROM	BP_PLAN_INDICATIVO BPPLAN   
								INNER JOIN  DEPENDENCIA_RESPONSABLE DEPRE ON BPPLAN.COMPANIA = DEPRE.COMPANIA  
									AND BPPLAN.DEPENDENCIA = DEPRE.DEPENDENCIA
								  AND BPPLAN.RESPONSABLE = DEPRE.RESPONSABLE
								  AND BPPLAN.SUCURSAL    = DEPRE.SUCURSAL 
								INNER JOIN RESPONSABLE RE ON RE.COMPANIA = DEPRE.COMPANIA
								  AND RE.CEDULA = DEPRE.RESPONSABLE
								  AND RE.SUCURSAL = DEPRE.SUCURSAL
								INNER JOIN TERCERO TER ON TER.COMPANIA = RE.COMPANIA
									AND TER.NIT = RE.CEDULA
									AND TER.SUCURSAL = RE.SUCURSAL	  
								WHERE   BPPLAN.COMPANIA = '''|| UN_COMPANIA || '''  
									AND BPPLAN.VIGENCIA_INICIAL = ''' || UN_VIGENCIA_CONSULTA_I || '''
									AND LENGTH(BPPLAN.ID) <=  ''' || PCK_PLAN_DESARROLLO.FC_GET_ACCION || '''';	

				MI_RTA	:= PCK_DATOS.FC_ACME(
												UN_TABLA	=>	MI_TABLA,
												UN_ACCION	=>	'IS',
												UN_CAMPOS	=>	MI_CAMPOS,
												UN_VALORES	=>	MI_VALORES
											);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
		          RAISE PCK_EXCEPCIONES.EXC_PLANDES;																			
			END;

		    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN
	        PCK_ERR_MSG.RAISE_WITH_MSG(
										UN_EXC_COD    	=>	SQLCODE,
										UN_ERROR_COD 	=>	PCK_ERRORES.ER_PLANDES_INSERT_PLAN_ACCION
									  );	
		END;	




	PCK_PLAN_DESARROLLO.PR_TOTALIZAR_FUENTE_PLANEACION(
														UN_COMPANIA => UN_COMPANIA,
														UN_VIGENCIA_INICIAL => UN_VIGENCIA_CONSULTA_I
													  );




	PCK_PLAN_DESARROLLO.PR_ACTCAMPOSRECURSO_PLANACCION
	(
		UN_COMPANIA => UN_COMPANIA,
		UN_VIGENCIA => UN_VIGENCIA_CONSULTA_I,
		UN_DESTINO => 'PRESUPUESTO'
	);

	PCK_PLAN_DESARROLLO.PR_ACT_EJECUTADO_PLANACCION
	(
		UN_COMPANIA => UN_COMPANIA,
		UN_VIGENCIA => UN_VIGENCIA_CONSULTA_I,
		UN_DESTINO => 'PAGADO'
	);


	END IF;
  FOR CURSOR_H1 IN (
  			SELECT	    BP_PLAN_INDICATIVO.DESCRIPCION AS DESC_EJE, 
						BP_PLAN_INDICATIVO_1.DESCRIPCION AS DESC_POLITICA,  
				        BP_PLAN_INDICATIVO_2.DESCRIPCION AS DESC_PROGRAMA,
				        BP_PLAN_INDICATIVO_3.DESCRIPCION AS DESC_SUBPROGRAMA,  
				        BP_PLAN_INDICATIVO_4.DESCRIPCION AS DESC_PROYECTO,
				        INF.BPIM,
				        INF.RESPONSABLE,  
				        INF.RECURSO_COFDEPARMENTAL,
				        INF.RECURSO_COFNACION,
				        INF.RECURSO_CREDITO,  
				        INF.RECURSO_OTROS,
				        INF.RECURSO_REGALIAS,
				        INF.RECURSO_RECURSOSPROPIOS,  
				        INF.RECURSO_SGP_ALIMESCOLAR,
				        INF.RECURSO_SGP_APSB,
				        INF.RECURSO_SGP_CULTURA,  
				        INF.RECURSO_SGP_DEPORTE ,
				        INF.RECURSO_SGP_EDUCACION,
				        INF.RECURSO_SGP_LIBREDESTINACION,  
				        INF.RECURSO_SGP_DESTINACIONESPEC ,
				        INF.RECURSO_SGP_LIBREINVERSION,
				        INF.RECURSO_SGP_MCPIOS_RIOMAG,  
				        INF.RECURSO_SGP_SALUD ,
				        INF.RECURSO_TOTAL, 
				        INF.EJECUTADO_COFDEPARMENTAL,  
				        INF.EJECUTADO_COFNACION, 
				        INF.EJECUTADO_CREDITO , 
				        INF.EJECUTADO_OTROS,  
				        INF.EJECUTADO_REGALIAS,
				        INF.EJECUTADO_RECURSOSPROPIOS ,
				        INF.EJECUTADO_SGP_ALIMESCOLAR,  
				        INF.EJECUTADO_SGP_APSB,
				        INF.EJECUTADO_SGP_CULTURA ,
				        INF.EJECUTADO_SGP_DEPORTE,   
				        INF.EJECUTADO_SGP_EDUCACION,
				        INF.EJECUTADO_SGP_LIBREDESTINACION ,
				        INF.EJECUTADO_SGP_DESTINACIONESPEC,  
				        INF.EJECUTADO_SGP_LIBREINVERSION ,
				        INF.EJECUTADO_SGP_MCPIOS_RIOMAG,
				        INF.EJECUTADO_SGP_SALUD,  
				        INF.EJECUTADO_TOTAL 
			FROM	PI_INF_PLANACCION  INF
			INNER JOIN   BP_PLAN_INDICATIVO  
				ON           INF.COMPANIA = BP_PLAN_INDICATIVO.COMPANIA 
				AND          INF.EJE = BP_PLAN_INDICATIVO.ID  
			INNER JOIN   BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_1  
				ON           INF.COMPANIA = BP_PLAN_INDICATIVO_1.COMPANIA  
				AND          INF.POLITICA = BP_PLAN_INDICATIVO_1.ID  
			INNER JOIN   BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_2  
				ON           INF.COMPANIA = BP_PLAN_INDICATIVO_2.COMPANIA  
				AND          INF.PROGRAMA = BP_PLAN_INDICATIVO_2.ID  
			INNER JOIN   BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_3  
				ON           INF.COMPANIA = BP_PLAN_INDICATIVO_3.COMPANIA  
				AND          INF.SUBPROGRAMA = BP_PLAN_INDICATIVO_3.ID  
			INNER JOIN   BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_4  
				ON           INF.COMPANIA = BP_PLAN_INDICATIVO_4.COMPANIA  
				AND          INF.PROYECTO = BP_PLAN_INDICATIVO_4.ID  
			WHERE        INF.COMPANIA = UN_COMPANIA 
				AND          INF.VIGENCIA <=  UN_VIGENCIA_CONSULTA_F
				AND          BP_PLAN_INDICATIVO.VIGENCIA_INICIAL =  UN_VIGENCIA_CONSULTA_I   
				AND          BP_PLAN_INDICATIVO_1.VIGENCIA_INICIAL =  UN_VIGENCIA_CONSULTA_I   
				AND          BP_PLAN_INDICATIVO_2.VIGENCIA_INICIAL =  UN_VIGENCIA_CONSULTA_I   
				AND          BP_PLAN_INDICATIVO_3.VIGENCIA_INICIAL =  UN_VIGENCIA_CONSULTA_I   
				AND          BP_PLAN_INDICATIVO_4.VIGENCIA_INICIAL =  UN_VIGENCIA_CONSULTA_I
			)
  LOOP
    MI_SALIDA_H1 := CURSOR_H1.DESC_EJE 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.DESC_POLITICA 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.DESC_PROGRAMA
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.DESC_SUBPROGRAMA
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.DESC_PROYECTO
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.BPIM
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RESPONSABLE
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_COFDEPARMENTAL
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_COFNACION
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_CREDITO
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_OTROS
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_REGALIAS
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_RECURSOSPROPIOS
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_APSB
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_CULTURA           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_DEPORTE           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_EDUCACION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_LIBREDESTINACION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_LIBREINVERSION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_MCPIOS_RIOMAG           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_SGP_SALUD           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.RECURSO_TOTAL           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_COFDEPARMENTAL           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_COFNACION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_CREDITO           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_OTROS           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_REGALIAS           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_RECURSOSPROPIOS           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_ALIMESCOLAR           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_APSB           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_CULTURA           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_DEPORTE           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_EDUCACION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_LIBREDESTINACION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_DESTINACIONESPEC           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_LIBREINVERSION           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_MCPIOS_RIOMAG           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_SGP_SALUD           
    				|| PCK_DATOS.GL_SEPARADOR_COL  || CURSOR_H1.EJECUTADO_TOTAL           
    				|| PCK_DATOS.GL_SEPARADOR_REG ;
    MI_CONTADOR := MI_CONTADOR + 1;
  END LOOP;

  FOR MI_CURSOR_H2 IN 
  	(
  		WITH QRY_SUBPROGRAMA AS (
  		                            SELECT  COMPANIA,
  		                                    VIGENCIA_FINAL,
  		                                    SUBSTR(ID,1,1) AS EJE,
  		                                    SUBSTR(ID,1,3) AS POLITICA,
  		                                    SUBSTR(ID,1,5) AS PROGRAMA,
  		                                    SUBSTR(ID,1,7) AS SUBPROGRAMA
  		                            FROM BP_PLAN_INDICATIVO
  		                            WHERE COMPANIA = UN_COMPANIA
  		                                AND VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		                                AND LENGTH(ID) <= 7
  		                        ) 


  		SELECT 
  		BP_PLAN_INDICATIVO.DESCRIPCION AS DESC_EJE,
  		       BP_PLAN_INDICATIVO_1.DESCRIPCION AS DESC_POLITICA,
  		       BP_PLAN_INDICATIVO_2.DESCRIPCION AS DESC_PROGRAMA,
  		       BP_PLAN_INDICATIVO_3.DESCRIPCION AS DESC_SUBPROGRAMA,
  		       PI_INDICADOR.DESCRIPCION_META,
  		       PI_INDICADOR.NOMBRE,
  		       PI_INDICADOR_META.META,
  		       PI_INDICADOR_META.AVANCE
  		FROM QRY_SUBPROGRAMA QRY
  		INNER JOIN BP_PLAN_INDICATIVO ON QRY.COMPANIA = BP_PLAN_INDICATIVO.COMPANIA
  		    AND QRY.EJE = BP_PLAN_INDICATIVO.ID
  		INNER JOIN BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_1 ON QRY.COMPANIA = BP_PLAN_INDICATIVO_1.COMPANIA
  		    AND QRY.POLITICA = BP_PLAN_INDICATIVO_1.ID
  		INNER JOIN BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_2 ON QRY.COMPANIA = BP_PLAN_INDICATIVO_2.COMPANIA
  		    AND QRY.PROGRAMA = BP_PLAN_INDICATIVO_2.ID
  		INNER JOIN BP_PLAN_INDICATIVO  BP_PLAN_INDICATIVO_3 ON QRY.COMPANIA = BP_PLAN_INDICATIVO_3.COMPANIA
  		    AND QRY.SUBPROGRAMA = BP_PLAN_INDICATIVO_3.ID
  		INNER JOIN PI_INDICADOR ON QRY.COMPANIA = PI_INDICADOR.COMPANIA
  		    AND QRY.SUBPROGRAMA = PI_INDICADOR.ID_PLAN
  		INNER JOIN PI_INDICADOR_META ON PI_INDICADOR.COMPANIA = PI_INDICADOR_META.COMPANIA
  		    AND PI_INDICADOR.VIGENCIA_INICIAL = PI_INDICADOR_META.VIGENCIA_INICIAL
  		    AND PI_INDICADOR.ID_PLAN = PI_INDICADOR_META.ID_PLAN
  		    AND PI_INDICADOR.CODIGO = PI_INDICADOR_META.INDICADOR
  		WHERE QRY.COMPANIA = UN_COMPANIA
  		  AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		  AND BP_PLAN_INDICATIVO_1.VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		  AND BP_PLAN_INDICATIVO_2.VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		  AND BP_PLAN_INDICATIVO_3.VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		  AND PI_INDICADOR.VIGENCIA_INICIAL = UN_VIGENCIA_CONSULTA_I
  		  AND PI_INDICADOR_META.VIGENCIA_META <= UN_VIGENCIA_CONSULTA_F
  	)
  LOOP

  	MI_SALIDA_H2 := MI_CURSOR_H2.DESC_EJE 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.DESC_POLITICA 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.DESC_PROGRAMA 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.DESC_SUBPROGRAMA 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.DESCRIPCION_META 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.NOMBRE 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.META 
    				|| PCK_DATOS.GL_SEPARADOR_COL  || MI_CURSOR_H2.AVANCE
    				|| PCK_DATOS.GL_SEPARADOR_REG ; 

  END LOOP;


  IF MI_SALIDA_H1 IS NOT NULL OR MI_SALIDA_H2 IS NOT NULL THEN
    MI_RET := TO_CLOB(MI_SALIDA_H1 || ',.HOJ.,' || MI_SALIDA_H2 );
  END IF;

  RETURN MI_RET;
END FC_PREPARARINFPLANACCION; 



--17

FUNCTION FC_GENERA_PREDECESOR
(
  /*
      NAME              : FC_GENERA_PREDECESOR --> EN ACCESS Genera_predecesor()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 08/03/2018
      TIME              : 08:30 AM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : Función que Genera los predecesores de acuerdo a la configuración de los niveles
      PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                          UN_VIGENCIA    => VIGENCIA GUBERNAMENTAL SELECCIONADA
                          UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
     @NAME: generarPredecesor
    */
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA IN PCK_SUBTIPOS.TI_ANIO,
	UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
	AS
		MI_RTA 			  PCK_SUBTIPOS.TI_RTA_ACME;
		MI_CONT 		  PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
    MI_CONT1 		  PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
		MI_EXISTE 		PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
		MI_CAMPOS   	PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION 	PCK_SUBTIPOS.TI_CONDICION;
		MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;

	BEGIN 
		MI_EXISTE := PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL(UN_COMPANIA => UN_COMPANIA, UN_VIGENCIA => UN_VIGENCIA);
		IF MI_EXISTE = 0 THEN 
			RETURN 0;
		END IF;

		BEGIN 
			FOR i IN REVERSE 2 .. MI_EXISTE LOOP
        MI_CONT := i;
				MI_CAMPOS := '	PREDECESOR 		= SUBSTR(ID,1,'||CNIVEL_PLANIND(MI_CONT-1).DIGITOS||'),
                        MODIFIED_BY	 	= '''||UN_USUARIO||''',
                        DATE_MODIFIED 	= SYSDATE';
				MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA||'''
								AND VIGENCIA_INICIAL  = '||UN_VIGENCIA||'
                AND LENGTH(ID)  = '||CNIVEL_PLANIND(MI_CONT).DIGITOS||'';

				BEGIN 
					BEGIN
						MI_RTA := PCK_DATOS.FC_ACME(	UN_TABLA       => 'BP_PLAN_INDICATIVO',
                                          UN_ACCION      => 'M',
                                          UN_CAMPOS      => MI_CAMPOS,
                                          UN_CONDICION   => MI_CONDICION
													);
					EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
						RAISE PCK_EXCEPCIONES.EXC_PLANDES;
					END;
				EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
					MI_MSGERROR(1).CLAVE := 'VIGENCIA';
					MI_MSGERROR(1).VALOR := UN_VIGENCIA;
					PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD     =>  SQLCODE
                              ,UN_ERROR_COD   =>  PCK_ERRORES.ER_PLANDES_PREDECESOR  
                              ,UN_REEMPLAZOS  =>  MI_MSGERROR );


				END;	
			END LOOP ;
		RETURN -1;	
		END;
	END FC_GENERA_PREDECESOR;


FUNCTION FC_GETM_DEPENDENCIA
/*
    NAME              : FC_GETM_DEPENDENCIA   --- GetMAN_DEPENDENCIA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 26/07/2018
    TIME              : 02:00 PM
    SOURCE MODULE     : SysmanDES2018.01.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA EL INDICADOR DE MANEJA DEPENDENCIA DEL PLAN INDICATIVO.
    @NAME: obtenerManejaDependencia
  */
(
  UN_DIGITOS       NUMBER
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_DEPEN      NUMBER:=0;
BEGIN
  FOR I IN CNIVEL_PLANIND.FIRST .. CNIVEL_PLANIND.LAST
    LOOP
      IF CNIVEL_PLANIND(I).DIGITOS = UN_DIGITOS THEN
        MI_DEPEN :=CNIVEL_PLANIND(I).MANEJA_DEPEN;    
      END IF;
    END LOOP;
  RETURN MI_DEPEN;
END FC_GETM_DEPENDENCIA;


 -- 9 guardar pipoblacionbeneficiada
  PROCEDURE PR_GUARDAR_POBLACION_BENEF
/*
    NAME              : PR_GUARDAR_POBLACION_BENEF
    AUTHORS           : PAOLA VEGA AVELLA 
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : REGISTRA Y ACTUALIZA LOS VALORES DE LA TABLA PB_POBLACION_BENEFICIADA

    @NAME:    guardarPoblacionBenef 
    @METHOD:  PUT OR POST
  */
(
    UN_COMPANIA                    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO                        IN VARCHAR2, 
    UN_NUMERO                      IN PB_POBLACION_BENEFICIADA.NUMERO%TYPE,
    UN_ID_PLAN                     IN PB_POBLACION_BENEFICIADA.ID_PLAN%TYPE,
    UN_VIGENCIA_INICIAL            IN PB_POBLACION_BENEFICIADA.VIGENCIA_INICIAL%TYPE, 
    UN_VALOR_PRIMINFANCIA          IN PB_POBLACION_BENEFICIADA.VALOR_PRIMINFANCIA%TYPE,
    UN_VALOR_ADOLESCENCIA          IN PB_POBLACION_BENEFICIADA.VALOR_ADOLESCENCIA%TYPE,
    UN_VALOR_JUVENTUD              IN PB_POBLACION_BENEFICIADA.VALOR_JUVENTUD%TYPE,
    UN_VALOR_ADULTO                IN PB_POBLACION_BENEFICIADA.VALOR_ADULTO%TYPE, 
    UN_VALOR_ADULTO_MAYOR          IN PB_POBLACION_BENEFICIADA.VALOR_ADULTO_MAYOR%TYPE,
    UN_VALOR_TOTAL                 IN PB_POBLACION_BENEFICIADA.VALOR_TOTAL%TYPE,
    UN_VALOR_MUJERES               IN PB_POBLACION_BENEFICIADA.VALOR_MUJERES%TYPE,
    UN_VALOR_HOMBRES               IN PB_POBLACION_BENEFICIADA.VALOR_HOMBRES%TYPE,
    UN_VALOR_TOTGENERO             IN PB_POBLACION_BENEFICIADA.VALOR_TOTGENERO%TYPE,
    UN_VALOR_VCA                   IN PB_POBLACION_BENEFICIADA.VALOR_VCA%TYPE,
    UN_VALOR_LGTB                  IN PB_POBLACION_BENEFICIADA.VALOR_LGTB%TYPE,
    UN_VALOR_DISCAP                IN PB_POBLACION_BENEFICIADA.VALOR_DISCAP%TYPE,
    UN_VALOR_AFRO                  IN PB_POBLACION_BENEFICIADA.VALOR_AFRO%TYPE,
    UN_VALOR_INDIG                 IN PB_POBLACION_BENEFICIADA.VALOR_INDIG%TYPE,
    UN_VALOR_INFANCIA              IN PB_POBLACION_BENEFICIADA.VALOR_INFANCIA%TYPE,    
    UN_VALOR_TOTAL_COND            IN PB_POBLACION_BENEFICIADA.VALOR_TOTAL_COND%TYPE,
    UN_USUARIO                     IN PCK_SUBTIPOS.TI_USUARIO
)
AS
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
      MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
      MI_MERGENOEXIS        PCK_SUBTIPOS.TI_MERGENOEXISTE;
      MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
  BEGIN
    MI_TABLA := 'PB_POBLACION_BENEFICIADA';
    MI_MERGEUSING :=   'SELECT
                       '''|| UN_COMPANIA ||''' COMPANIA,
                            '''||UN_TIPO||''' TIPO,
                            '||UN_NUMERO||' NUMERO,
                            '''||UN_ID_PLAN||''' ID_PLAN,
                            '''||UN_VIGENCIA_INICIAL||''' VIGENCIA_INICIAL,
                            '||UN_VALOR_PRIMINFANCIA||' VALOR_PRIMINFANCIA,
                            '||UN_VALOR_ADOLESCENCIA||' VALOR_ADOLESCENCIA,
                            '||UN_VALOR_JUVENTUD||' VALOR_JUVENTUD,
                            '||UN_VALOR_ADULTO||' VALOR_ADULTO,
                            '||UN_VALOR_ADULTO_MAYOR||' VALOR_ADULTO_MAYOR,
                            '||UN_VALOR_TOTAL||' VALOR_TOTAL,
                            '||UN_VALOR_MUJERES||' VALOR_MUJERES,
                            '||UN_VALOR_HOMBRES||' VALOR_HOMBRES,
                            '||UN_VALOR_TOTGENERO||' VALOR_TOTGENERO,
                            '||UN_VALOR_VCA||' VALOR_VCA,
                            '||UN_VALOR_LGTB||' VALOR_LGTB,
                            '||UN_VALOR_DISCAP||' VALOR_DISCAP,
                            '||UN_VALOR_AFRO||' VALOR_AFRO, 
                            '||UN_VALOR_INDIG||' VALOR_INDIG, 
                            '||UN_VALOR_INFANCIA||' VALOR_INFANCIA,
                            '||UN_VALOR_TOTAL_COND||' VALOR_TOTAL_COND
                    FROM
                        DUAL';
                        
    MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                   AND VISTA.TIPO             = TABLA.TIPO 
                   AND VISTA.NUMERO          = TABLA.NUMERO 
                   AND VISTA.ID_PLAN             = TABLA.ID_PLAN
                   AND VISTA.VIGENCIA_INICIAL    = TABLA.VIGENCIA_INICIAL';


        MI_MERGENOEXIS := 'INSERT (
                            COMPANIA,
                            TIPO,
                            NUMERO,
                            ID_PLAN,
                            VIGENCIA_INICIAL,
                            VALOR_PRIMINFANCIA,
                            VALOR_ADOLESCENCIA,
                            VALOR_JUVENTUD,
                            VALOR_ADULTO,
                            VALOR_ADULTO_MAYOR,
                            VALOR_TOTAL,
                            VALOR_MUJERES,
                            VALOR_HOMBRES,
                            VALOR_TOTGENERO,
                            VALOR_VCA,
                            VALOR_LGTB,
                            VALOR_DISCAP,
                            VALOR_AFRO,
                            VALOR_INDIG,
                            VALOR_INFANCIA,
                            VALOR_TOTAL_COND,
                            DATE_CREATED,
                            CREATED_BY
                            )
                            VALUES 
                            (
                            VISTA.COMPANIA,
                            VISTA.TIPO,
                            VISTA.NUMERO,
                            VISTA.ID_PLAN,
                            VISTA.VIGENCIA_INICIAL,
                            VISTA.VALOR_PRIMINFANCIA,
                            VISTA.VALOR_ADOLESCENCIA,
                            VISTA.VALOR_JUVENTUD,
                            VISTA.VALOR_ADULTO,
                            VISTA.VALOR_ADULTO_MAYOR,
                            VISTA.VALOR_TOTAL,
                            VISTA.VALOR_MUJERES,
                            VISTA.VALOR_HOMBRES,
                            VISTA.VALOR_TOTGENERO,
                            VISTA.VALOR_VCA,
                            VISTA.VALOR_LGTB,
                            VISTA.VALOR_DISCAP,
                            VISTA.VALOR_AFRO,
                            VISTA.VALOR_INDIG,
                            VISTA.VALOR_INFANCIA,
                            VISTA.VALOR_TOTAL_COND,
                            SYSDATE,
                            '''||UN_USUARIO||'''
                            )';

        MI_MERGEEXISTE :='  UPDATE SET 
                            TABLA.VALOR_PRIMINFANCIA='||UN_VALOR_PRIMINFANCIA||',
                            TABLA.VALOR_ADOLESCENCIA='||UN_VALOR_ADOLESCENCIA||',
                            TABLA.VALOR_JUVENTUD='||UN_VALOR_JUVENTUD||',
                            TABLA.VALOR_ADULTO='||UN_VALOR_ADULTO||',
                            TABLA.VALOR_ADULTO_MAYOR= '||UN_VALOR_ADULTO_MAYOR||',
                            TABLA.VALOR_TOTAL='||UN_VALOR_TOTAL||',
                            TABLA.VALOR_MUJERES='||UN_VALOR_MUJERES||',
                            TABLA.VALOR_HOMBRES= '||UN_VALOR_HOMBRES||',
                            TABLA.VALOR_TOTGENERO= '||UN_VALOR_TOTGENERO||',
                            TABLA.VALOR_VCA='||UN_VALOR_VCA||',
                            TABLA.VALOR_LGTB='||UN_VALOR_LGTB||',
                            TABLA.VALOR_DISCAP='||UN_VALOR_DISCAP||',
                            TABLA.VALOR_AFRO='||UN_VALOR_AFRO||', 
                            TABLA.VALOR_INDIG='||UN_VALOR_INDIG||',
                            TABLA.VALOR_INFANCIA='||UN_VALOR_INFANCIA||',
                            TABLA.VALOR_TOTAL_COND='||UN_VALOR_TOTAL_COND||',
                            TABLA.MODIFIED_BY = '''||UN_USUARIO ||''',
                            TABLA.DATE_MODIFIED = SYSDATE';
 BEGIN
      BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA 
                                                , UN_ACCION      => 'IM'
                                                , UN_MERGEUSING  => MI_MERGEUSING
                                                , UN_MERGEENLACE => MI_MERGEENLACE
                                                , UN_MERGEEXISTE => MI_MERGEEXISTE
                                                , UN_MERGENOEXIS => MI_MERGENOEXIS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
            
       EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
					MI_MSGERROR(1).CLAVE := 'VIGENCIA';
					MI_MSGERROR(1).VALOR := UN_VIGENCIA_INICIAL;
                    MI_MSGERROR(2).CLAVE := 'IDPLAN';
					MI_MSGERROR(2).VALOR := UN_ID_PLAN;
                    MI_MSGERROR(3).CLAVE := 'TIPO';
					MI_MSGERROR(3).VALOR := UN_TIPO;
                    MI_MSGERROR(4).CLAVE := 'NUMERO';
					MI_MSGERROR(4).VALOR := UN_NUMERO;
					PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD     =>  SQLCODE
                              ,UN_ERROR_COD   =>  PCK_ERRORES.ER_PLANDES_PREDECESOR  
                              ,UN_REEMPLAZOS  =>  MI_MSGERROR );
		END;	
 END;
END PR_GUARDAR_POBLACION_BENEF;



END PCK_PLAN_DESARROLLO;