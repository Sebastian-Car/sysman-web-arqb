create or replace PACKAGE BODY "PCK_CGR" AS

  -- 2
FUNCTION FC_VERIFICARCONFIGURACION
/*
      NAME              : FC_VERIFICARCONFIGURACION -->EN ACCESS VerificarConfiguracion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA 
      DATE MIGRADOR     : 07/03/2017
      TIME              : 02:05 PM
      SOURCE MODULE     : SysmanRes5993_CGR2017.02.08
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION  
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO          => ANIO QUE SERA FILTRADO EN LAS CONSULTAS.
      MODIFICATIONS     : 
      @NAME:  generarConfiguracion
      @METHOD:  GET
    */ 
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO
)
RETURN CLOB
AS 
  MI_CONTEO   PCK_SUBTIPOS.TI_ENTERO;
  MI_MENSAJE  CLOB;
BEGIN
    BEGIN 
        SELECT COUNT(ID) 
          INTO MI_CONTEO
          FROM V_PLAN_PRESUPUESTAL
         WHERE COMPANIA = UN_COMPANIA 
           AND ANO      = UN_ANIO 
           AND MOVIMIENTO NOT IN(0) 
         ORDER BY ID;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CONTEO:=0;
    END;
    IF MI_CONTEO = 0 THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE
                       ,UN_ERROR_COD => PCK_ERRORES.ERRR_ENTES_CUENPRESUPUESTALES
                      );
        END;
    ELSE
        MI_MENSAJE:='Las siguientes inconsistencias se presentan en la Configuración de las cuentas presupuestales'|| CHR(10);
    END IF;
    <<VERIFICACUENTAS>>
    FOR MI_RS IN (
         SELECT  ID
                ,NOMBRE
                ,NATURALEZA
                ,CODIGOSCHIP SCHIP
                ,RECURSOSCHIP
                ,ORIGENESPECIFICOINGRESOS
                ,DESTINACIONDELOSRECURSOS
                ,VIGENCIAGASTO
                ,VIGENCIATESORERIASCHIP 
           FROM V_PLAN_PRESUPUESTAL
          WHERE COMPANIA = UN_COMPANIA
            AND ANO      = UN_ANIO
            AND MOVIMIENTO NOT IN(0) 
          ORDER BY ID)
    LOOP
        IF MI_RS.SCHIP IS NULL THEN
            MI_MENSAJE:= MI_MENSAJE 
                         ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                         ||' -> No tiene configurado el CODIGO SCHIP' 
                         || CHR(10);
        END IF;

        IF MI_RS.RECURSOSCHIP IS NULL THEN
            MI_MENSAJE:= MI_MENSAJE 
                         ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                         ||' -> No tiene configurado el Recurso '
                         || CHR(10);
        END IF;

        IF MI_RS.ORIGENESPECIFICOINGRESOS IS NULL THEN
            MI_MENSAJE:= MI_MENSAJE 
                         ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                         ||' -> No tiene configurado el Origen Especifico '
                         || CHR(10);
        END IF;

        IF MI_RS.DESTINACIONDELOSRECURSOS IS NULL THEN
            MI_MENSAJE:= MI_MENSAJE 
                         ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                         || ' -> No tiene configurada la destinación de los recursos '
                         || CHR(10);
        END IF;

        IF MI_RS.NATURALEZA = 'D' THEN
            IF MI_RS.VIGENCIATESORERIASCHIP IS NULL THEN
                MI_MENSAJE:=MI_MENSAJE 
                            ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                            || ' -> No tiene configurada la vigencia de tesoreria '|| CHR(10);
            END IF;
            IF MI_RS.VIGENCIAGASTO IS NULL THEN
              MI_MENSAJE:=MI_MENSAJE 
                          ||' La cuenta: '|| MI_RS.ID || '  ' || MI_RS.NOMBRE 
                          ||' -> No tiene configurada la vigencia de gasto '|| CHR(10);
            END IF;
        END IF;
    END LOOP VERIFICACUENTAS;
    RETURN MI_MENSAJE;
END FC_VERIFICARCONFIGURACION;

  -- 3
    FUNCTION FC_GENERALOGHACIENDA 
    /*
      NAME              : FC_GENERALOGHACIENDA
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 07/03/2017
      TIME              : 03:47 PM
      SOURCE MODULE     : SysmanRes5993_CGR2017.02.08
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Estructura el contenido del Archivo Log al generar el informe para la Secretaría de Hacienda de Bogotá
      MODIFICATIONS     : 
      PARAMETERS        : UN_TITULOPLANO   => Nombre con el que se genera el informa para Secretaría de Hacienda Bogotá
                          UN_FECHAINICIAL  => Fecha inicial en la que se genera el informe
                          UN_FECHAFINAL    => Fecha final en la que se genera el informe
                          UN_FUENTEINICIAL => Fuente inicial definida por el usuario para generar el informe
                          UN_FUENTEFINAL   => Fuente final definida por el usuario para generar el informe
                          UN_USUARIO       => Usuario que genera el reporte
                          UN_INICIOPROCESO => Fecha y hora de inicio del proceso de generación del informe
                          UN_FINPROCESO    => Fecha y hora en que finaliza del proceso de generación del informe
      @NAME  :  generarLogSecretariaHacienda
      @METHOD:  GET     
    */ 
    (
      UN_TITULOPLANO    IN  VARCHAR2,
      UN_FECHAINICIAL   IN  DATE,
      UN_FECHAFINAL     IN  DATE,
      UN_FUENTEINICIAL  IN  VARCHAR2,
      UN_FUENTEFINAL    IN  VARCHAR2,
      UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO,
      UN_INICIOPROCESO  IN  DATE,
      UN_FINPROCESO     IN  DATE
    )
    RETURN  CLOB
    AS 
      MI_STR      CLOB;
    BEGIN
      MI_STR := '1. EMPIEZO A GENERAR ARCHIVO'|| CHR(10) ||
                '   ARCHIVO:            ' || UN_TITULOPLANO || CHR(10) || 
                '   FECHA:              ' || TO_CHAR(SYSDATE, 'DD/MM/YYYY') || CHR(10) ||
                '   FECHA INICIAL:      ' || TO_CHAR(UN_FECHAINICIAL, 'DD/MM/YYYY') || CHR(10) ||
                '   FECHA FINAL:        ' || TO_CHAR(UN_FECHAFINAL, 'DD/MM/YYYY') || CHR(10) || 
                '   FUENTE INICIAL:     ' || UN_FUENTEINICIAL || CHR(10) || 
                '   FUENTE FINAL:       ' || UN_FUENTEFINAL || CHR(10) || 
                '   USUARIO:            ' || UN_USUARIO || CHR(10) || 
                '   INICIO DEL PROCESO: ' || TO_CHAR(UN_INICIOPROCESO, 'DD/MM/YYYY HH24:mi:ss') || CHR(10) || 
                '   FIN DEL PROCESO:    ' || TO_CHAR(UN_FINPROCESO, 'DD/MM/YYYY HH24:mi:ss');          
      RETURN MI_STR;
    END FC_GENERALOGHACIENDA;  


  -- 4

  FUNCTION FC_REVISARMOVSCHIP
  /*
  NAME              : FC_REVISARMOVSCHIP En Access --> Revisar_MovSchip
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 03/03/2017
  TIME              : 11:45 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : 
  DESCRIPTION       : Función que se llama en Usuarios Predial / Botón Financiar - Previo
                      Funcion que se encarga de insertar los acuerdos en la tabla IP_ACUERDOS ó TMP_IP_ACUERDOS
                      ,además de actualizar las tablas IP_FACTURADOS,IP_USUARIOS_PREDIAL,IP_RECIBOS_DE_PAGO.


  @NAME:  RevisarMovSchip
  @METHOD:  POST    
  */     
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TABLA             IN VARCHAR2
)
RETURN NUMBER 
AS
  MI_CON                PCK_SUBTIPOS.TI_ENTERO;
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSSQL            PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO             PCK_SUBTIPOS.TI_ENTERO;
  MI_ID                 VARCHAR2(60 CHAR);
  MI_CODIGO             VARCHAR2(60 CHAR);
  MI_RSSCHIP            SYS_REFCURSOR;  
  MI_RSID               SYS_REFCURSOR;  
  MI_RTA                PCK_SUBTIPOS.TI_ENTERO;    
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_TABLA              PCK_SUBTIPOS.TI_CAMPOS;
BEGIN 

  MI_RTA := 0;
  MI_STRSQL :='SELECT CODIGO
               FROM '||UN_TABLA||'
               WHERE ENTIDAD = '||UN_COMPANIA||'
               ORDER BY CODIGO';

  EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

      IF MI_CONTEO >=1 THEN           
        OPEN MI_RSSCHIP FOR MI_STRSQL;
          LOOP             

          FETCH MI_RSSCHIP INTO MI_CODIGO;

            EXIT WHEN MI_RSSCHIP%NOTFOUND;


          MI_STRSSQL := 'SELECT CODIGO 
                         FROM '||UN_TABLA||'
                         WHERE ENTIDAD = '||UN_COMPANIA||'
                          AND CODIGO LIKE '''||MI_CODIGO||'''';

              EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSSQL||')' INTO MI_CONTEO;                  

            IF MI_CONTEO >=1 THEN           
              OPEN MI_RSID FOR MI_STRSSQL;
                LOOP             

                FETCH MI_RSID INTO MI_ID; 
                EXIT WHEN MI_RSID%NOTFOUND;

                  IF MI_CONTEO = 1 THEN 
                    MI_CAMPOS:='MOVIMIENTO = -1';

                    MI_CONDICION := 'ENTIDAD = '||UN_COMPANIA||'
                                     AND CODIGO = '''||MI_ID||'''
                                     AND MOVIMIENTO = 0';


                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TABLA
                                                       ,UN_ACCION    => 'M'
                                                       ,UN_CAMPOS    => MI_CAMPOS
                                                       ,UN_CONDICION => MI_CONDICION);
                  ELSE 
                    MI_CAMPOS:='MOVIMIENTO = 0';

                    MI_CONDICION := 'ENTIDAD = '||UN_COMPANIA||'
                                     AND CODIGO = '''||MI_ID||'''
                                     AND MOVIMEINTO = -1';


                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TABLA
                                                       ,UN_ACCION    => 'M'
                                                       ,UN_CAMPOS    => MI_CAMPOS
                                                       ,UN_CONDICION => MI_CONDICION);
                  END IF;

                    IF PCK_DATOS.GL_RTA > 0 THEN
                        MI_RTA := MI_RTA +1 ;
                    END IF;
                END LOOP; 
            END IF;  
        END LOOP; 
      END IF;  

 RETURN MI_RTA;     

 MI_TABLA := 'PLAN_PRESUPUESTAL';
 MI_MERGEUSING :='SELECT  PLAN_PRESUPUESTAL.CODIGOSCHIP
                          ,PLAN_SCHIP.ENTIDAD
                  FROM PLAN_PRESUPUESTAL
                  INNER JOIN PLAN_SCHIP
                      ON PLAN_PRESUPUESTAL.CODIGOSCHIP = PLAN_SCHIP.CODIGO
                  WHERE PLAN_SCHIP.ENTIDAD =''1''
                  AND  PLAN_SCHIP.MOVIMIENTO = 0';

  MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                    AND TABLA.CODIGOSCHIP = VISTA.CODIGOSCHIP';                  

  MI_MERGEEXISTE := 'UPDATE SET TABLA.CODIGOSCHIP = Null';      

  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                  UN_ACCION      => 'MM', 
                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                  UN_MERGEEXISTE => MI_MERGEEXISTE); 
END FC_REVISARMOVSCHIP;


 -- 5
FUNCTION FC_GENERARPLANOEJEINGRESOS
/*
      NAME              : FC_GENERARPLANOEJEINGRESOS -->EN ACCESS GenerarPlanoEjecucionIngresos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
      DATE MIGRADOR     : 09/03/2017
      TIME              : 10:05 PM
      SOURCE MODULE     : SysmanRes5993_CGR2017.02.08
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : RETORNA UNA CADENA QUE CONTENDRA EL CONTENIDO DEL ARCHIVO DE EJECUCION DE INGRESOS EN EL PERIODO SELECCIONADO.
      PENDIENTE CONSULTA RESUMENPPTO_P_I
      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO           => ANIO QUE SERA FILTRADO EN LAS CONSULTAS.
                          UN_MESFINAL       => MES POR EL QUE SERA FILTRADO LA EJECUCION DE INGRESOS.
                          UN_CODIGODETALLE  => CODIGO DE LA CONTADURIA. 
                          UN_TIPOENTIDAD    => TIPO DE ENTIDAD A PARTIR DEL CUAL SE GENERA EL ARCHIVO.
                          UN_PERIODO        => PERIODO SELECCIONADO PARA GENERAR EL ARCHIVO.
      MODIFICATIONS     : 
      @NAME:  generarConsultaExcel
      @METHOD:  GET
    */ 
(
  UN_COMPANIA         PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO             SALDO_PLAN_PPTAL.ANO%TYPE,
  UN_MESFINAL         SALDO_PLAN_PPTAL.MES%TYPE,
  UN_CODIGODETALLE    VARCHAR2,
  UN_TIPOENTIDAD      PCK_SUBTIPOS.TI_ENTERO,
  UN_PERIODO          VARCHAR2
)
RETURN CLOB
AS 
  MI_CONTEO     PCK_SUBTIPOS.TI_ENTERO;
  MI_MENSAJE    CLOB;
BEGIN
  MI_CONTEO:=0;
  MI_MENSAJE:='Las siguientes inconsistencias se presentan en el archivo plano que acaba de generar:'|| CHR(10) || CHR(13);
  MI_MENSAJE:='S  '|| UN_CODIGODETALLE || '  1' || UN_PERIODO ||'  '|| UN_ANIO || '  EJECUCIONDEINGRESOS' ||'  '|| TO_CHAR(SYSDATE,'DD/MM/YYYY');
  /*
  FOR MI_RS IN (
                WITH RESUMENPPTO_P_I AS(
                  SELECT 
                        SALDO_PLAN_PPTAL.COMPANIA,
                        SALDO_PLAN_PPTAL.ANO,
                        PLAN_PRESUPUESTAL.CODIGOSCHIP  CODIGOEQUIVALENTE,
                        SUM( SALDO_PLAN_PPTAL.APROPIACION_CREDITO- SALDO_PLAN_PPTAL.APROPIACION_DEBITO) APROPIADO,
                        SUM( SALDO_PLAN_PPTAL.MODIF_PAC_CREDITO- SALDO_PLAN_PPTAL.MODIF_PAC_DEBITO ) MODIFPAC,
                        SUM( SALDO_PLAN_PPTAL.EJE_CNT_CREDITO- SALDO_PLAN_PPTAL.EJE_CNT_DEBITO ) EJECUCIONCNT,
                        SUM( SALDO_PLAN_PPTAL.TRASLADO_CREDITO- SALDO_PLAN_PPTAL.TRASLADO_DEBITO ) TRASLADO,
                        SUM( SALDO_PLAN_PPTAL.EJE_PPT_CREDITO- SALDO_PLAN_PPTAL.EJE_PPT_DEBITO ) EJECUCIONPPT,
                        SUM( SALDO_PLAN_PPTAL.APLAZAM_CREDITO-SALDO_PLAN_PPTAL.APLAZAM_DEBITO ) APLAZAMIENTO,
                        SUM(SALDO_PLAN_PPTAL.ADICION)           ADICION,
                        SUM(SALDO_PLAN_PPTAL.REDUCCION)         REDUCCION,
                        SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD)    DISPONIBILIDAD,
                        SUM(SALDO_PLAN_PPTAL.REG_CONTRACT)      REG_CONTRACT,
                        SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)   REG_NO_CONTRACT,
                        SUM(SALDO_PLAN_PPTAL.REG_REVERSION)     REG_REVERSION,
                        SUM(SALDO_PLAN_PPTAL.VIGENCIAANTERIOR)  VIGENCIAANTERIOR,
                        NVL(RECAUDO_VA,0)                       RECAUDO_VANTERIOR,
                        SUM(SALDO_PLAN_PPTAL.VIGENCIAFUTURA)    VIGENCIAFUTURA,
                        SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)     PAC_APROPIADO,
                        SUM(SALDO_PLAN_PPTAL.PAC_PROGRAMADO)    PAC_PROGRAMADO,
                        SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)    MODIF_REG_CONT,
                        SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)  MODIF_REG_NOCONT,
                        SUM(SALDO_PLAN_PPTAL.REINTEGRO)         REINTEGRO,
                        PLAN_PRESUPUESTAL.NATURALEZA,
                        (SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)) REGNOCONTRACT,
                        (SUM(SALDO_PLAN_PPTAL.REG_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)) REGCONT,
                        SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)+SUM( CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' THEN SALDO_PLAN_PPTAL.MODIF_PAC_DEBITO-SALDO_PLAN_PPTAL.MODIF_PAC_CREDITO ELSE SALDO_PLAN_PPTAL.MODIF_PAC_CREDITO-SALDO_PLAN_PPTAL.MODIF_PAC_DEBITO END) PACTOTAL,
                        SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION)       REO,
                        SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) MODIFREO,
                        SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION)+SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) TOTALREO,
                        SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS)            TOTALMODIFINGRESOS,
                        SUM( CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' THEN SALDO_PLAN_PPTAL.EJE_PPT_DEBITO- SALDO_PLAN_PPTAL.EJE_PPT_CREDITO ELSE SALDO_PLAN_PPTAL.EJE_PPT_CREDITO- SALDO_PLAN_PPTAL.EJE_PPT_DEBITO END)+SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS) TOTALINGRESOS,
                        SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)         INGRESOSCAUSADOS,
                        SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS)   MODIFICACIONICA,
                        SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)+SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS)  TOTALICA,
                        SUM(SALDO_PLAN_PPTAL.PAC_EJECUTADO)             PACEJECUTADO_P,
                        SUM(SALDO_PLAN_PPTAL.EJE_PPT_DEBITO)  CREDITOS,
                        SUM(SALDO_PLAN_PPTAL.EJE_PPT_CREDITO) CONTRACREDITOS,
                        PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS,
                        PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS,
                        PLAN_PRESUPUESTAL.RECURSOSCHIP,
                        (SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT))+(SUM(SALDO_PLAN_PPTAL.REG_CONTRACT) +SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT))   REGISTROSP,
                        SUM(SALDO_PLAN_PPTAL.TRASLADO_DEBITO)   MOD_DEBITO,
                        SUM(SALDO_PLAN_PPTAL.TRASLADO_CREDITO)  MOD_CREDITO,
                        SUM(SALDO_PLAN_PPTAL.APLAZAM_DEBITO)    APLAZADEB,
                        SUM(SALDO_PLAN_PPTAL.APLAZAM_CREDITO)   APLAZACRE,
                        SUM(SALDO_PLAN_PPTAL.RECONOCIMIENTOS)   RECON,
                        PLAN_PRESUPUESTAL.COD_RECIPROCA,
                        CASE WHEN PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP IN 0 THEN 'C' ELSE 'S' END COD_SIT
                  FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
                  LEFT JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL
                    ON PLAN_PRESUPUESTAL.ID        = SALDO_PLAN_PPTAL.ID
                    AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO
                    AND PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA
                  INNER JOIN SALDO_AUX_PPTAL
                    ON PLAN_PRESUPUESTAL.ID        = SALDO_AUX_PPTAL.ID
                    AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                    AND PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                  WHERE SALDO_PLAN_PPTAL.MES <= UN_MESFINAL
                    AND SALDO_PLAN_PPTAL.COMPANIA = UN_COMPANIA
                    AND SALDO_PLAN_PPTAL.ANO = UN_ANIO
                    AND PLAN_PRESUPUESTAL.CODIGOSCHIP IS NOT NULL
                    AND PLAN_PRESUPUESTAL.NATURALEZA ='C'
                  GROUP BY SALDO_PLAN_PPTAL.COMPANIA,
                    SALDO_PLAN_PPTAL.ANO,
                    PLAN_PRESUPUESTAL.CODIGOSCHIP,
                    NVL(RECAUDO_VA,0),
                    PLAN_PRESUPUESTAL.NATURALEZA,
                    PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS,
                    PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS,
                    PLAN_PRESUPUESTAL.RECURSOSCHIP,
                    PLAN_PRESUPUESTAL.COD_RECIPROCA,
                    CASE WHEN PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP IN 0 THEN 'C' ELSE 'S' END
                )
                SELECT  RESUMENPPTO_P_I.CODIGOEQUIVALENTE  CONCEPTO,
                                      RESUMENPPTO_P_I.RECURSOSCHIP COD_REC,
                                      NVL(RESUMENPPTO_P_I.ORIGENESPECIFICOINGRESOS,'')  COD_OEI,
                                      NVL(RESUMENPPTO_P_I.DESTINACIONDELOSRECURSOS,'')  COD_DEST_ESTT,
                                      0    NDA_DPC,
                                      0    NDA_REC,
                                      NVL(COD_RECIPROCA,'CODIGOENTIDAD')  ID_ETRA,
                                      0    ND_ACTO_ADTIVO,
                                      0    DPC_INGR,
                                      0    REV_DPC_INGR,
                                      COD_SIT,
                                      CASE WHEN RECAUDO_VANTERIOR <> 0  THEN 0 ELSE TRUNC(CASE WHEN RESUMENPPTO_P_I.TOTALINGRESOS>=0 THEN RESUMENPPTO_P_I.TOTALINGRESOS+0.501 ELSE RESUMENPPTO_P_I.TOTALINGRESOS-0.501 END) END  EE_RECAUDO_INGR,
                                      0  EE_DEVOLUC_INGR,
                                      0  EE_REVREC_INGR,
                                      0  E_OTRAS_INGR,
                                      0  REV_OTRAS_INGR,
                                      TRUNC( CASE WHEN RESUMENPPTO_P_I.RECON  >=0 THEN RESUMENPPTO_P_I.RECON + 0.501 ELSE RESUMENPPTO_P_I.RECON - 0.501 END) RECONOC_INGR,
                                      CASE WHEN RECAUDO_VANTERIOR <> 0 THEN TRUNC(CASE WHEN RESUMENPPTO_P_I.TOTALINGRESOS>=0 THEN RESUMENPPTO_P_I.TOTALINGRESOS+0.501 ELSE RESUMENPPTO_P_I.TOTALINGRESOS-0.501 END) ELSE 0 END  REC_VA_INGR,
                                      0  REV_RVA_INGR
                              FROM RESUMENPPTO_P_I 
                              WHERE RESUMENPPTO_P_I.NATURALEZA ='C'
                              AND RESUMENPPTO_P_I.ORIGENESPECIFICOINGRESOS IS NOT NULL
                              AND RESUMENPPTO_P_I.DESTINACIONDELOSRECURSOS IS NOT NULL
                              AND RESUMENPPTO_P_I.RECURSOSCHIP IS NOT NULL
                              AND (TRUNC(CASE WHEN RESUMENPPTO_P_I.TOTALINGRESOS >=0 THEN RESUMENPPTO_P_I.TOTALINGRESOS   +0.501 ELSE RESUMENPPTO_P_I.TOTALINGRESOS-0.501 END ) NOT IN(0)
                              OR TRUNC(CASE WHEN RESUMENPPTO_P_I.RECON >=0 THEN RESUMENPPTO_P_I.RECON+ 0.501 ELSE RESUMENPPTO_P_I.RECON - 0.501 END ) NOT IN (0)
                              OR TRUNC(CASE WHEN RESUMENPPTO_P_I.VIGENCIAANTERIOR >=0 THEN RESUMENPPTO_P_I.VIGENCIAANTERIOR+0.501 ELSE RESUMENPPTO_P_I.VIGENCIAANTERIOR-0.501 END ) NOT IN (0)))
  LOOP

    MI_CONTEO:=MI_CONTEO+1;
    IF UN_TIPOENTIDAD= 6 OR UN_TIPOENTIDAD= 7  THEN
       MI_MENSAJE:= MI_MENSAJE 
                    || 'D  ' 
                    || MI_RS.CONCEPTO        || '  '
                    || MI_RS.COD_REC          || '  '
                    || MI_RS.COD_OEI         || '  '
                    || MI_RS.COD_DEST_ESTT   || '  '
                    || MI_RS.COD_SIT         || '  '
                    || MI_RS.NDA_REC         || '  '
                    || MI_RS.ID_ETRA         || '  '
                    || MI_RS.ND_ACTO_ADTIVO  || '  '
                    || MI_RS.EE_RECAUDO_INGR || '  '
                    || MI_RS.EE_DEVOLUC_INGR || '  '
                    || MI_RS.EE_REVREC_INGR  || '  '
                    || MI_RS.REC_VA_INGR     || '  '
                    || MI_RS.REV_RVA_INGR    || CHR(10) || CHR(13);
    END IF;

    IF UN_TIPOENTIDAD = 1 OR UN_TIPOENTIDAD = 2 OR UN_TIPOENTIDAD = 4 OR UN_TIPOENTIDAD = 5 THEN
      MI_MENSAJE:=  MI_MENSAJE
                    || 'D  ' 
                    || MI_RS.CONCEPTO        || '  '
                    || MI_RS.COD_REC         || '  '
                    || MI_RS.COD_OEI         || '  '
                    || MI_RS.COD_DEST_ESTT   || '  '
                    || MI_RS.COD_SIT         || '  '
                    || MI_RS.NDA_REC         || '  '
                    || MI_RS.ID_ETRA         || '  '
                    || MI_RS.EE_RECAUDO_INGR || '  '
                    || MI_RS.EE_DEVOLUC_INGR || '  '
                    || MI_RS.EE_REVREC_INGR  || '  '
                    || MI_RS.E_OTRAS_INGR    || '  '
                    || MI_RS.REV_OTRAS_INGR  || '  '
                    || MI_RS.REC_VA_INGR     || '  '
                    || MI_RS.REV_RVA_INGR    || CHR(10) || CHR(13);
    END IF;

    IF UN_TIPOENTIDAD = 3 THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE
                         ,UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                          );
      END;
    END IF;
  END LOOP;
  */
  IF MI_CONTEO = 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
                PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE
                       ,UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOARCHIVOPLANO --No existe información para el achivo plano de Ejecución de Ingresos en el Periodo seleccionado.
                        );
    END;
  END IF;

  RETURN MI_MENSAJE;
END FC_GENERARPLANOEJEINGRESOS;

 -- 6
  FUNCTION FC_ACTUALIZACONFIGPPTAL
    /*
      NAME              : FC_ACTUALIZACONFIGPPTAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 08/03/2017
      TIME              : 10:26 AM
      SOURCE MODULE     : UTILIDADESCHIP
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE REALIZA LA ACTUALIZACION DE LAS CONFIGURACIONES DE RUBROS DEL 
                          PLAN PRESUPUESTAL DEL AÑO ANTERIOR. (EN SALDO_AUX_PPTAL).
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO          => ANIO QUE SE ESTA CONFIGURANDO.
      MODIFICATIONS     : 
      @NAME:  actualizarConfiguracionPptal
      @METHOD:  GET
    */
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANIO             IN  PCK_SUBTIPOS.TI_ANIO 
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
    RETURN NUMBER
  AS
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_ACTUALIZADOS           PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      MI_TABLA := 'PLAN_PPTAL_CONFIG';
      MI_MERGEUSING := 'SELECT PPTAL_ACT.COMPANIA
                              ,PPTAL_ACT.ANO 
                              ,PPTAL_ACT.CODIGO 
                              ,PPTAL_ACT.CENTRO_COSTO 
                              ,PPTAL_ACT.TERCERO 
                              ,PPTAL_ACT.SUCURSAL 
                              ,PPTAL_ACT.AUXILIAR 
                              ,PPTAL_ACT.REFERENCIA 
                              ,PPTAL_ACT.FUENTE_RECURSO
                              ,PPTAL_ANT.CODIGOSCHIP
                              ,PPTAL_ANT.ORIGENESPECIFICOINGRESOS
                              ,PPTAL_ANT.DESTINACIONDELOSRECURSOS
                              ,PPTAL_ANT.FINALIDADGASTO
                              ,PPTAL_ANT.VIGENCIAGASTO
                              ,PPTAL_ANT.DEPENDENCIASCHIP
                              ,PPTAL_ANT.VIGENCIATESORERIASCHIP
                              ,PPTAL_ANT.RECURSOSCHIP
                              ,PPTAL_ANT.CODIGOSIRECI

                        FROM   PLAN_PPTAL_CONFIG PPTAL_ANT
                          INNER JOIN PLAN_PPTAL_CONFIG PPTAL_ACT
                             ON      PPTAL_ANT.COMPANIA       = PPTAL_ACT.COMPANIA
                            AND      PPTAL_ANT.CODIGO         = PPTAL_ACT.CODIGO
                            AND      PPTAL_ANT.CENTRO_COSTO   = PPTAL_ACT.CENTRO_COSTO
                            AND      PPTAL_ANT.TERCERO        = PPTAL_ACT.TERCERO
                            AND      PPTAL_ANT.SUCURSAL       = PPTAL_ACT.SUCURSAL
                            AND      PPTAL_ANT.AUXILIAR       = PPTAL_ACT.AUXILIAR
                            AND      PPTAL_ANT.REFERENCIA     = PPTAL_ACT.REFERENCIA
                            AND      PPTAL_ANT.FUENTE_RECURSO = PPTAL_ACT.FUENTE_RECURSO
                        WHERE  PPTAL_ANT.COMPANIA = '''||UN_COMPANIA||'''
                          AND  PPTAL_ANT.ANO      = '||UN_ANIO||' - 1
                          AND  PPTAL_ACT.ANO      = '||UN_ANIO||'';             
      MI_MERGEENLACE := '     TABLA.COMPANIA       = VISTA.COMPANIA
                          AND TABLA.ANO            = VISTA.ANO 
                          AND TABLA.CODIGO         = VISTA.CODIGO
                          AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                          AND TABLA.TERCERO        = VISTA.TERCERO
                          AND TABLA.SUCURSAL       = VISTA.SUCURSAL
                          AND TABLA.AUXILIAR       = VISTA.AUXILIAR
                          AND TABLA.REFERENCIA     = VISTA.REFERENCIA
                          AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO';            
      MI_MERGEEXISTE := ' UPDATE
                            SET TABLA.CODIGOSCHIP              = VISTA.CODIGOSCHIP
                               ,TABLA.ORIGENESPECIFICOINGRESOS = VISTA.ORIGENESPECIFICOINGRESOS
                               ,TABLA.DESTINACIONDELOSRECURSOS = VISTA.DESTINACIONDELOSRECURSOS
                               ,TABLA.FINALIDADGASTO           = VISTA.FINALIDADGASTO
                               ,TABLA.VIGENCIAGASTO            = VISTA.VIGENCIAGASTO
                               ,TABLA.DEPENDENCIASCHIP         = VISTA.DEPENDENCIASCHIP
                               ,TABLA.VIGENCIATESORERIASCHIP   = VISTA.VIGENCIATESORERIASCHIP
                               ,TABLA.RECURSOSCHIP             = VISTA.RECURSOSCHIP
                               ,TABLA.CODIGOSIRECI             = VISTA.CODIGOSIRECI
                               ,TABLA.DATE_MODIFIED            = SYSDATE
                               ,TABLA.MODIFIED_BY              = '''||UN_USUARIO||''''; 

      MI_ACTUALIZADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                           UN_ACCION    => 'MM',
                                           UN_MERGEUSING => MI_MERGEUSING,
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGEEXISTE => MI_MERGEEXISTE);             
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
    END;
    RETURN MI_ACTUALIZADOS;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
      MI_MSGERROR(1).CLAVE := 'COMPANIA';
      MI_MSGERROR(1).VALOR := UN_COMPANIA;
      MI_MSGERROR(2).CLAVE := 'ANIO';
      MI_MSGERROR(2).VALOR := UN_ANIO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_CONFIGPPTALANT,
        UN_REEMPLAZOS => MI_MSGERROR
      );
  END FC_ACTUALIZACONFIGPPTAL;

 -- 7
FUNCTION FC_ACTRESUMENPPTO
  /*
  NAME              : FC_ACTRESUMENPPTO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MIGRADOR     : 14/03/2017
  TIME              : 12:39 PM
  SOURCE MODULE     : 
  DESCRIPTION       : Actualizacino del plan presupuestal
  PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                      UN_TABLA          => TALBA PARA ACTUALIZAR LOS DATOS.
                      UN_ANO            => ANO ACTUAL.
  @NAME: actResumenPpto
  @METHOD: POST
  */
  (
   -- Parametro que recibe el numero de la entidad que se calcula
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TABLA    IN PCK_SUBTIPOS.TI_TABLA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS
    MI_RPTA        PCK_SUBTIPOS.TI_LOGICO;    
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES     PCK_SUBTIPOS.TI_CAMPOS;
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  BEGIN
     IF UN_COMPANIA IS NOT NULL OR
        UN_TABLA    IS NOT NULL OR
        UN_ANO      IS NOT NULL THEN 

        MI_CAMPOS:='COMPAÑIA: '||UN_COMPANIA||','||
                     'AÑO:    '||UN_ANO||','||
                     'TABLA:  '||UN_TABLA;
       BEGIN          
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN  
          MI_REEMPLAZOS(0).CLAVE:='CAMPOS';
          MI_REEMPLAZOS(0).VALOR:=MI_CAMPOS;  
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD     => SQLCODE,
            UN_ERROR_COD   => PCK_ERRORES.ERRR_ENTES_ACTRESU_DATANULL,
            UN_REEMPLAZOS => MI_REEMPLAZOS 
            );
      END;
    END IF;
    BEGIN  

        MI_VALORES:='  CODIGOSCHIP 			        = NULL,
                       ORIGENESPECIFICOINGRESOS = NULL,
                       DESTINACIONDELOSRECURSOS = NULL,
                       FINALIDADGASTO 		     	= NULL,
                       VIGENCIAGASTO 				    = NULL,
                       DEPENDENCIASCHIP 			  = NULL,
                       VIGENCIATESORERIASCHIP 	= NULL,
                       RECURSOSCHIP 				    = NULL';
        MI_CONDICION:='  COMPANIA	 ='''||UN_COMPANIA||''''||
                      '  AND ANO	 ='''||UN_ANO||''''||
                      '  AND MOVIMIENTO				       <>-1 
                         AND(CODIGOSCHIP 			       IS NOT NULL 
                         OR ORIGENESPECIFICOINGRESOS IS NOT NULL 
                         OR DESTINACIONDELOSRECURSOS IS NOT NULL 
                         OR FINALIDADGASTO 			     IS NOT NULL 
                         OR VIGENCIAGASTO 			     IS NOT NULL 
                         OR DEPENDENCIASCHIP 		     IS NOT NULL 
                         OR VIGENCIATESORERIASCHIP   IS NOT NULL 
                         OR RECURSOSCHIP 			       IS NOT NULL)';
        BEGIN                 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA =>UN_TABLA, 
                                               UN_ACCION    =>'M',
                                               UN_CAMPOS    =>MI_VALORES, 
                                               UN_CONDICION =>MI_CONDICION 
                                               );  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;                      
        END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
                 MI_REEMPLAZOS(0).CLAVE:='CAMPOS';
                 MI_REEMPLAZOS(0).VALOR:='COMPANIA:'||UN_COMPANIA||', AÑO:'||UN_ANO;   
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_ACTRESUMENPPTO_COMPACAMP,
                   UN_TABLAERROR  => UN_TABLA,
                   UN_REEMPLAZOS  => MI_REEMPLAZOS  
                  );
    END;
  RETURN MI_RPTA;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD =>SQLCODE,
                   UN_ERROR_COD=>PCK_ERRORES.ERRR_ACTRESUMENPPTO_NOTFOUND);
END FC_ACTRESUMENPPTO;

--9
FUNCTION FC_GENERAPLANOFUTSINMOV
    /*
      NAME              : FC_GENERAPLANOFUTSINMOV
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 30/03/2017
      TIME              : 10:26 AM
      SOURCE MODULE     : SysmanChip2017.02.03
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Estructura el contenido del archivo txt que permite conocer las inconsistencias al configurar un código fut sin movimiento presupuestal.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA => Compañia de ingreso a la aplicación
                          UN_ANIO     => Año presupuestal del que se desea obtener la información
                          UN_MESFINAL => Mes final de consulta para obtener la información de Programación de Ingresos
      @NAME  :  generarPlanoFutsSinMovimiento
      @METHOD:  GET     
    */ 
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
    )
    RETURN  CLOB
	AS
		MI_STR     CLOB;
   	MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION			      PCK_SUBTIPOS.TI_CONDICION;

	BEGIN 
    BEGIN 
      MI_CAMPOS := 'CONSECUTIVO_FUT  = NULL';
      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND ANO = '||UN_ANIO||' AND CONSECUTIVO_FUT IN(0)';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 	   => 'PLAN_PRESUPUESTAL',
                                                  UN_ACCION 	 => 'M',
                                                  UN_CAMPOS 	 => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;											
    END;
		MI_STR := 'Las siguientes inconsistencias se presentan al configurar un Código FUT Sin Movimiento a la cuenta Presupuestal' ||CHR(10);
		FOR MI_RS IN (SELECT  
						V_PLAN_PRESUPUESTAL.ID,
						V_PLAN_PRESUPUESTAL.NOMBRE, 
						CASE V_PLAN_PRESUPUESTAL.DESTINO
							WHEN 'F'
							THEN 'Funcionamiento'
							WHEN 'I'
							THEN 'Inversión'
							WHEN 'S'
							THEN 'Servicio de la Deuda'
							ELSE 'Libre destinación' 
						END DESTINOF, 
						CODIGOSFUT.CODIGOFUT 
					FROM V_PLAN_PRESUPUESTAL 
						LEFT JOIN CODIGOSFUT 
							ON V_PLAN_PRESUPUESTAL.COMPANIA = CODIGOSFUT.COMPANIA
							AND V_PLAN_PRESUPUESTAL.ANO 	= CODIGOSFUT.ANO 
							AND V_PLAN_PRESUPUESTAL.CODIGOFUT_H = CODIGOSFUT.CODIGOFUT
					WHERE V_PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA 
						AND V_PLAN_PRESUPUESTAL.ANO = UN_ANIO
						AND CODIGOSFUT.MOVIMIENTO IN(0) 
						AND CONSECUTIVO_FUT IS NOT NULL 
					ORDER BY V_PLAN_PRESUPUESTAL.ID)
			LOOP
				MI_STR := 	MI_STR 			||
							RPAD('Cuenta Presupuestal: ' || MI_RS.ID , 40 , ' ') || 
							RPAD(MI_RS.NOMBRE,100,' ') ||
							RPAD('Destino: ' || MI_RS.DESTINOF,25,' ')||
							'Código FUT Sin Movimiento ' || MI_RS.CODIGOFUT || CHR(10);
			END LOOP;
		RETURN MI_STR;	
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_ENTES_FUTSINMOV,
                 UN_TABLAERROR =>'V_PLAN_PRESUPUESTAL');      

  END FC_GENERAPLANOFUTSINMOV;

--10
FUNCTION FC_EJECUCION_GASTOS 
    /*
      NAME              : FC_EJECUCION_GASTOS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 13/08/2017
      TIME              : 10:26 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 15/08/2017
      TIME              : 04:30 PM   
      DESCRIPTION       : Archivo plano de ejecucion de gastos presupuestal para todas las entidades. 
      MODIFICATIONS     : Se realizan ajustes de indentacion y se elimina el parametro UN_TIPOENTIDAD y se realiza la consulta a la tabla COMPANIA  
                          para traer el valor del tipo entidad
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOENTIDAD    => Codigo de la entidad recgenerarPlanoEjecucionGastosiproca. 
                          UN_CODIGOCONTADURIA =>Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_ANTICIPOS        =>para saber si se genera el informe con anticipos o no.   
                          --> Se elimino UN_TIPOENTIDAD      =>Tipo de entidad que rinde el informe al ente de control.
      @NAME  :  generarPlanoEjecucionGastos
      @METHOD:  GET     
    */ 
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOENTIDAD       IN VARCHAR2
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_ANTICIPOS           IN PCK_SUBTIPOS.TI_LOGICO
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE;
BEGIN
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;
 IF UN_EXCEL=0 THEN   
    MI_RTA:='S'||CHR(9)||
            UN_CODIGOCONTADURIA||CHR(9)||
            '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                   WHEN 2 THEN '0406'
                                   WHEN 3 THEN '0709'
                                   WHEN 4 THEN '1012'
                 END||CHR(9)||
            UN_ANO||CHR(9)||
            'EJECUCIONDEGASTOS'||CHR(9)||
            TO_CHAR(SYSDATE, 'DD-MM-YYYY')
            ||CHR(13)||CHR(10);
 ELSE
          IF MI_TIPOENTIDAD IN(5,6,7) THEN  
             MI_RTA:='CONTROL'       ||CHR(9)||
                     'CONCEPTO'      ||CHR(9)||
                     'VIG_GAST'      ||CHR(9)||
                     'DEPENDENCIA'   ||CHR(9)||
                     'COD_REC'       ||CHR(9)||
                     'COD_OEI'       ||CHR(9)||
                     'COD_DEST_ESTT' ||CHR(9)||
                     'COD_FIN'       ||CHR(9)||
                     'COD_SIT'       ||CHR(9)||
                     'NDA_COM'       ||CHR(9)||
                     'NDA_OBL'       ||CHR(9)||
                     'NDA_PAG'       ||CHR(9)||
                     'ID_ETRA'       ||CHR(9)||
                     'EJE_GCCA_GAST' ||CHR(9)||
                     'EJE_GCSA_GAST' ||CHR(9)||
                     'REV_GC_GAST'   ||CHR(9)||
                     'EJE_OC_GAST'   ||CHR(9)||
                     'REV_OC_GAST'   ||CHR(9)||
                     'EJE_PAGOS_GAST'||CHR(9)||
                     'ANU_PAGOS_GAST'||CHR(9)||
                     'RES_PRE_GAST'  ||CHR(9)||
                     'CXP_GAST_ACEP' ||CHR(9)||
                     'OBL_PE_GAST'
                     ||CHR(13)||CHR(10);
          ELSIF MI_TIPOENTIDAD IN(1,4) THEN 
                MI_RTA:='CONTROL'       ||CHR(9)||
                        'CONCEPTO'      ||CHR(9)||
                        'VIG_GAST'      ||CHR(9)||
                        'COD_REC'       ||CHR(9)||
                        'COD_OEI'       ||CHR(9)||
                        'COD_DEST_ESTT' ||CHR(9)||
                        'COD_FIN'       ||CHR(9)||
                        'COD_SIT'       ||CHR(9)||
                        'NDA_COM'       ||CHR(9)||
                        'NDA_OBL'       ||CHR(9)||
                        'NDA_PAG'       ||CHR(9)||
                        'ID_ETRA'       ||CHR(9)||
                        'EJE_GCCA_GAST' ||CHR(9)||
                        'EJE_GCSA_GAST' ||CHR(9)||
                        'REV_GC_GAST'   ||CHR(9)||
                        'EJE_OC_GAST'   ||CHR(9)||
                        'REV_OC_GAST'   ||CHR(9)||
                        'EJE_PAGOS_GAST'||CHR(9)||
                        'ANU_PAGOS_GAST'||CHR(9)||
                        'RES_PRE_GAST'  ||CHR(9)||
                        'CXP_GAST_ACEP' ||CHR(9)||
                        'OBL_PE_GAST'   
                        ||CHR(13)||CHR(10);
          ELSIF MI_TIPOENTIDAD IN(2) THEN
                MI_RTA:='CONTROL'       ||CHR(9)||
                        'CONCEPTO'      ||CHR(9)||
                        'VIG_GAST'      ||CHR(9)||
                        'COD_REC'       ||CHR(9)||
                        'COD_OEI'       ||CHR(9)||
                        'COD_DEST_ESTT' ||CHR(9)||
                        'COD_FIN'       ||CHR(9)||
                        'COD_SIT'       ||CHR(9)||
                        'NDA_COM'       ||CHR(9)||
                        'NDA_PAG'       ||CHR(9)||
                        'ID_ETRA'       ||CHR(9)||
                        'EJE_GCCA_GAST' ||CHR(9)||
                        'EJE_GCSA_GAST' ||CHR(9)||
                        'REV_GC_GAST'   ||CHR(9)||
                        'EJE_PAGOS_GAST'||CHR(9)||
                        'ANU_PAGOS_GAST'||CHR(9)||
                        'CXP_GAST_ACEP' 
                        ||CHR(13)||CHR(10);
          END IF;
 END IF;

    IF MI_TIPOENTIDAD IN(5,6,7) THEN  
        <<RECORRERESUMENPPTO_E>>
        FOR RS IN (
            WITH V_RESUMENPPTO_E AS(
                  SELECT  SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,PLAN_PRESUPUESTAL.CODIGOSCHIP CODIGOEQUIVALENTE
                       ,PLAN_PRESUPUESTAL.RECURSOSCHIP
                       ,PLAN_PRESUPUESTAL.VIGENCIAGASTO
                       ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS
                       ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS
                       ,PLAN_PRESUPUESTAL.FINALIDADGASTO
                       ,PLAN_PRESUPUESTAL.DEPENDENCIASCHIP
                       ,PLAN_PRESUPUESTAL.COD_RECIPROCA
                       ,PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,CASE WHEN VIGENCIAGASTO IN('1','4') 
                             THEN SUM((REG_CONTRACT+REG_NO_CONTRACT)+(MODIF_REG_CONTADR+MODIF_REG_NOCONTADR))
                             ELSE 0 
                        END AS EJE_GCSA_GAST
                       ,CASE WHEN VIGENCIAGASTO NOT IN ('3') 
                             THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)
                                              ELSE 0 
                                         END  AS EJE_OC_GAST
                       ,SUM(EJE_PPT_DEBITO)               AS EJE_PAGOS_GAST
                       ,SUM(EJE_PPT_CREDITO) AS ANU_PAGOS_GAST
                       ,ABS(SUM(MODIF_REG_CONTDMR+MODIF_REG_NOCONTDMR)) AS DISMINUCION
                       ,SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACIONARO) AS REO
                       ,SUM(MODIF_REGISTRO_OBLIGACIONDRO) AS DISMINUCIONDRO
                       ,SUM(EJE_PPT_CREDITO) AS DISMINUCIONDEG
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) AS APLAZADEB
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) AS APLAZACRE
                  FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                      INNER JOIN SALDO_AUX_PPTAL 
                          ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                          AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                          AND PLAN_PRESUPUESTAL.ID   = SALDO_AUX_PPTAL.ID
                 WHERE PLAN_PRESUPUESTAL.COMPANIA   = UN_COMPANIA 
                   AND PLAN_PRESUPUESTAL.ANO        = UN_ANO 
                   AND PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                   AND MES<=MI_MESFINAL 
                   AND PLAN_PRESUPUESTAL.REGALIAS IN(0) 
                   AND CODIGOSCHIP IS NOT NULL 
                   AND DEPENDENCIASCHIP IS NOT NULL 
                   AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
                   AND DESTINACIONDELOSRECURSOS IS NOT NULL 
                   AND FINALIDADGASTO IS NOT NULL 
                   AND RECURSOSCHIP IS NOT NULL 
                   AND VIGENCIAGASTO IS NOT NULL 

                 GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                          , SALDO_AUX_PPTAL.ANO
                          , CODIGOSCHIP,VIGENCIAGASTO
                          , FINALIDADGASTO,DEPENDENCIASCHIP
                          , COD_RECIPROCA
                          , SITUACIONFONDOSSCHIP
                          , PLAN_PRESUPUESTAL.NATURALEZA
                          , ORIGENESPECIFICOINGRESOS
                          , DESTINACIONDELOSRECURSOS
                          , RECURSOSCHIP
                  HAVING SUM( (CASE WHEN VIGENCIAGASTO IN ('1','4') 
                            THEN ((REG_CONTRACT+REG_NO_CONTRACT)+(MODIF_REG_CONTADR+MODIF_REG_NOCONTADR))
                            ELSE 0 
                       END)) NOT IN (0)
                    OR    SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION) NOT IN (0)
                    OR  SUM(MODIF_INGRESOS+CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                                           ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                                    END
                            ) NOT IN (0)
                 )
            SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                   ,VIGENCIAGASTO AS VIG_GAST
                   ,DEPENDENCIASCHIP AS DEPENDENCIA
                   ,RECURSOSCHIP AS COD_REC
                   ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                   ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                   ,FINALIDADGASTO AS COD_FIN
                   ,CASE WHEN SITUACIONFONDOSSCHIP =-1 
                         THEN 'S' 
                         ELSE 'C' 
                    END AS COD_SIT
                   ,SUM(0) AS NDA_COM
                   ,SUM(0) AS NDA_OBL
                   ,SUM(0) AS NDA_PAG
                   ,NVL(COD_RECIPROCA,UN_CODIGOENTIDAD) AS ID_ETRA
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN SUM(CASE WHEN VIGENCIAGASTO IN ('1','4') 
                                       THEN EJE_GCSA_GAST 
                                       ELSE 0 
                                  END) 
                         ELSE 0 
                    END AS EJE_GCCA_GAST
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN 0 
                         ELSE SUM(CASE WHEN VIGENCIAGASTO IN ('1','4') 
                                       THEN EJE_GCSA_GAST 
                                       ELSE 0 
                                  END) END AS EJE_GCSA_GAST
                   ,ABS(SUM(CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                             THEN 0 
                             ELSE DISMINUCION 
                        END)) AS REV_GC_GAST
                   ,SUM(CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                             THEN (REO) 
                             ELSE 0 
                        END) AS EJE_OC_GAST
                   ,ABS(SUM(DISMINUCIONDRO))AS REV_OC_GAST
                   ,SUM(EJE_PAGOS_GAST) AS EJE_PAGOS_GAST
                   ,SUM(DISMINUCIONDEG) AS ANU_PAGOS_GAST
                   ,SUM(0) AS RES_PRE_GAST
                   ,SUM(0) AS CXP_GAST_ACEP
                   ,SUM(0) AS OBL_PE_GAST 
              FROM V_RESUMENPPTO_E
             WHERE (CASE WHEN UN_ANTICIPOS=-1 
                         THEN CASE WHEN VIGENCIAGASTO IN ('1','4') 
                                   THEN EJE_GCSA_GAST 
                                   ELSE 0 
                                   END 
                              ELSE 0 
                         END
                    +CASE WHEN UN_ANTICIPOS=-1 
                          THEN 0 
                          ELSE CASE WHEN VIGENCIAGASTO IN ('1','4') 
                                    THEN EJE_GCSA_GAST 
                                    ELSE 0 
                               END 
                          END
                    +CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                          THEN 0 
                          ELSE DISMINUCION 
                     END
                    +CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                          THEN (REO) 
                          ELSE 0 
                     END
                    +DISMINUCIONDRO
                    +EJE_PAGOS_GAST
                    +DISMINUCIONDEG)>0
             GROUP BY   CODIGOEQUIVALENTE
                      , VIGENCIAGASTO
                      , DEPENDENCIASCHIP
                      , RECURSOSCHIP
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , FINALIDADGASTO
                      , SITUACIONFONDOSSCHIP
                      , COD_RECIPROCA
                ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                           ||CHR(9)
                    ||RS.CONCEPTO                   ||CHR(9)
                    ||TO_CHAR(RS.VIG_GAST)          ||CHR(9)
                    ||RS.DEPENDENCIA                ||CHR(9)
                    ||TO_CHAR(RS.COD_REC)           ||CHR(9)
                    ||RS.COD_OEI                    ||CHR(9)
                    ||RS.COD_DEST_ESTT              ||CHR(9)
                    ||RS.COD_FIN                    ||CHR(9)
                    ||RS.COD_SIT                    ||CHR(9)
                    ||TO_CHAR(RS.NDA_COM)           ||CHR(9)
                    ||TO_CHAR(RS.NDA_OBL)           ||CHR(9)
                    ||TO_CHAR(RS.NDA_PAG)           ||CHR(9)
                    ||RS.ID_ETRA                    ||CHR(9)
                    ||TO_CHAR(RS.EJE_GCCA_GAST)     ||CHR(9)
                    ||TO_CHAR(RS.EJE_GCSA_GAST)     ||CHR(9)
                    ||TO_CHAR(RS.REV_GC_GAST)       ||CHR(9)
                    ||TO_CHAR(RS.EJE_OC_GAST)       ||CHR(9)
                    ||TO_CHAR(RS.REV_OC_GAST)       ||CHR(9)
                    ||TO_CHAR(RS.EJE_PAGOS_GAST)    ||CHR(9)
                    ||TO_CHAR(RS.ANU_PAGOS_GAST)    ||CHR(9)
                    ||TO_CHAR(RS.RES_PRE_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.CXP_GAST_ACEP)     ||CHR(9)
                    ||TO_CHAR(RS.OBL_PE_GAST)       ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_E;
    ELSIF MI_TIPOENTIDAD IN(1,4) THEN 
        --NO SE TIENE EN CUENTA LA DEPENDENCIA
        <<RECORRERESUMENPPTO_E>>
        FOR RS IN (
            WITH V_RESUMENPPTO_E AS(
                SELECT  SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,PLAN_PRESUPUESTAL.CODIGOSCHIP CODIGOEQUIVALENTE
                       ,PLAN_PRESUPUESTAL.RECURSOSCHIP
                       ,PLAN_PRESUPUESTAL.VIGENCIAGASTO
                       ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS
                       ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS
                       ,PLAN_PRESUPUESTAL.FINALIDADGASTO
                       ,PLAN_PRESUPUESTAL.COD_RECIPROCA
                       ,PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,CASE WHEN VIGENCIAGASTO IN('1') 
                             THEN SUM((REG_CONTRACT+REG_NO_CONTRACT)+(MODIF_REG_CONTADR+MODIF_REG_NOCONTADR))
                             ELSE 0 
                        END AS EJE_GCSA_GAST
                       ,CASE WHEN VIGENCIAGASTO NOT IN ('3','4') 
                             THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)
                             ELSE 0 
                        END  AS EJE_OC_GAST
                       ,SUM(EJE_PPT_DEBITO) AS EJE_PAGOS_GAST
                       ,SUM(EJE_PPT_CREDITO) AS ANU_PAGOS_GAST
                       ,CASE WHEN VIGENCIAGASTO IN('1')  THEN ABS(SUM(MODIF_REG_CONTDMR+MODIF_REG_NOCONTDMR)) ELSE 0 END AS DISMINUCION
                       ,SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACIONARO) AS REO
                       ,CASE WHEN VIGENCIAGASTO NOT IN ('3','4') THEN SUM(MODIF_REGISTRO_OBLIGACIONDRO) ELSE 0 END AS DISMINUCIONDRO
                       ,SUM(EJE_PPT_CREDITO) AS DISMINUCIONDEG
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) AS APLAZADEB
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) AS APLAZACRE
                  FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                      INNER JOIN SALDO_AUX_PPTAL 
                         ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                         AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                         AND PLAN_PRESUPUESTAL.ID   = SALDO_AUX_PPTAL.ID
                 WHERE PLAN_PRESUPUESTAL.COMPANIA   =  UN_COMPANIA 
                   AND PLAN_PRESUPUESTAL.ANO        =  UN_ANO 
                   AND PLAN_PRESUPUESTAL.NATURALEZA =  'D' 
                   AND MES                          <= MI_MESFINAL 
                   AND PLAN_PRESUPUESTAL.REGALIAS   IN (0) 
                   AND CODIGOSCHIP                  IS NOT NULL 
                   AND ORIGENESPECIFICOINGRESOS     IS NOT NULL 
                   AND DESTINACIONDELOSRECURSOS     IS NOT NULL 
                   AND FINALIDADGASTO               IS NOT NULL 
                   AND RECURSOSCHIP                 IS NOT NULL 
                   AND VIGENCIAGASTO                IS NOT NULL                    
             GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                      , SALDO_AUX_PPTAL.ANO
                      , CODIGOSCHIP
                      , VIGENCIAGASTO
                      , FINALIDADGASTO
                      , COD_RECIPROCA
                      , SITUACIONFONDOSSCHIP
                      , PLAN_PRESUPUESTAL.NATURALEZA
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , RECURSOSCHIP)
            SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                   ,VIGENCIAGASTO AS VIG_GAST
                   ,RECURSOSCHIP AS COD_REC
                   ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                   ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                   ,FINALIDADGASTO AS COD_FIN
                   ,CASE WHEN SITUACIONFONDOSSCHIP =-1 
                         THEN 'S' 
                         ELSE 'C' 
                    END AS COD_SIT
                   ,SUM(0) AS NDA_COM
                   ,SUM(0) AS NDA_OBL
                   ,SUM(0) AS NDA_PAG
                   ,NVL(COD_RECIPROCA,UN_CODIGOENTIDAD) AS ID_ETRA
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN SUM(EJE_GCSA_GAST) 
                         ELSE 0 
                    END AS EJE_GCCA_GAST
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN 0 
                         ELSE SUM(EJE_GCSA_GAST) 
                    END AS EJE_GCSA_GAST
                   ,ABS(SUM(DISMINUCION)) AS REV_GC_GAST
                   ,SUM(CASE WHEN VIGENCIAGASTO NOT IN ('3','4') 
                             THEN (REO) 
                             ELSE 0 
                        END) AS EJE_OC_GAST
                   ,ABS(SUM(DISMINUCIONDRO)) AS REV_OC_GAST
                   ,SUM(EJE_PAGOS_GAST) AS EJE_PAGOS_GAST
                   ,SUM(DISMINUCIONDEG) AS ANU_PAGOS_GAST
                   ,SUM(0) AS RES_PRE_GAST
                   ,SUM(0) AS CXP_GAST_ACEP
                   ,SUM(0) AS OBL_PE_GAST 
              FROM V_RESUMENPPTO_E
              GROUP BY   CODIGOEQUIVALENTE
                      , VIGENCIAGASTO
                      , RECURSOSCHIP
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , FINALIDADGASTO
                      , SITUACIONFONDOSSCHIP
                      , COD_RECIPROCA
              HAVING SUM( EJE_GCSA_GAST +
                          DISMINUCION +
                          CASE WHEN VIGENCIAGASTO NOT IN ('3','4')THEN (REO) ELSE 0  END+
                          DISMINUCIONDRO +
                          EJE_PAGOS_GAST +
                          DISMINUCIONDEG)>0
             ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                       ||CHR(9)
                    ||RS.CONCEPTO               ||CHR(9)
                    ||TO_CHAR(RS.VIG_GAST)      ||CHR(9)
                    ||RS.COD_REC                ||CHR(9)
                    ||RS.COD_OEI                ||CHR(9)
                    ||RS.COD_DEST_ESTT          ||CHR(9)
                    ||RS.COD_FIN                ||CHR(9)
                    ||RS.COD_SIT                ||CHR(9)
                    ||TO_CHAR(RS.NDA_COM)       ||CHR(9)
                    ||TO_CHAR(RS.NDA_OBL)       ||CHR(9)
                    ||TO_CHAR(RS.NDA_PAG)       ||CHR(9)
                    ||RS.ID_ETRA                ||CHR(9)
                    ||TO_CHAR(RS.EJE_GCCA_GAST) ||CHR(9)
                    ||TO_CHAR(RS.EJE_GCSA_GAST) ||CHR(9)
                    ||TO_CHAR(RS.REV_GC_GAST)   ||CHR(9)
                    ||TO_CHAR(RS.EJE_OC_GAST)   ||CHR(9)
                    ||TO_CHAR(RS.REV_OC_GAST)   ||CHR(9)
                    ||TO_CHAR(RS.EJE_PAGOS_GAST)||CHR(9)
                    ||TO_CHAR(RS.ANU_PAGOS_GAST)||CHR(9)
                    ||TO_CHAR(RS.RES_PRE_GAST)  ||CHR(9)
                    ||TO_CHAR(RS.RES_PRE_GAST)  ||CHR(9)
                    ||TO_CHAR(RS.OBL_PE_GAST)   ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_E;         
    ELSE
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD); --No existe información para este tipo de entidad.);
        END;
    END IF;
    RETURN MI_RTA;    
END FC_EJECUCION_GASTOS;

--11
FUNCTION FC_EJECUCION_GASTOS_REGALIAS 
    /*
      NAME              : FC_EJECUCION_GASTOS_REGALIAS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 13/08/2017
      TIME              : 2:26 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 15/08/2017
      TIME              : 05:50 PM  
      DESCRIPTION       : Archivo plano de ejecucion de gastos presupuestales para todas las entidades que manejan regalías. 
      MODIFICATIONS     : Se realizan ajustes de indentacion y se elimina el parametro UN_TIPOENTIDAD y se realiza la consulta a la tabla COMPANIA  
                          para traer el valor del tipo entidad
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOCONTADURIA =>Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_ANTICIPOS        =>para saber si se genera el informe con anticipos o no.   
                          UN_CODIGOSGR        =>codigo del sistema General de Regalias, este codigo es dado por cada entidad.
                          UN_TIPOENTIDAD      =>Tipo de entidad que rinde el informe al ente de control.
      @NAME  :  generarPlanoEjecucionGastosRegalias
      @METHOD:  GET     
    */ 
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_ANTICIPOS           IN PCK_SUBTIPOS.TI_LOGICO
   ,UN_CODIGOSGR           IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE;
BEGIN
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;

 IF UN_EXCEL=0 THEN  
      MI_RTA:='S'||CHR(9)||
              UN_CODIGOCONTADURIA||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
                   END||CHR(9)||
              UN_ANO||CHR(9)||
              'SGR_EJECUCION_DE_GASTOS'||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY')
              ||CHR(13)||CHR(10);
  ELSE
           MI_RTA:='CONTROL'       ||CHR(9)||
                   'CONCEPTO'      ||CHR(9)||
                   'CODIGO_BPIN_SGR' ||CHR(9)||
                   'COD_REC'       ||CHR(9)||
                   'COD_OEI'       ||CHR(9)||
                   'COD_DEST_ESTT' ||CHR(9)||
                   'COD_SIT'       ||CHR(9)||
                   'TERCERO'       ||CHR(9)||
                   'EJE_GCSA_GAST' ||CHR(9)||
                   'EJE_OC_GAST'   ||CHR(9)||
                   'EJE_PAGOS_GAST'||CHR(9)||
                   'CXP_GAST_ACEP' ||CHR(9)||
                   'OBL_PE_GAST'   
                   ||CHR(13)||CHR(10); 
   END IF;     
    IF MI_TIPOENTIDAD IN(6,7) THEN
        <<V_RESUMENPPTO_E_R>>
        FOR RS IN (
             WITH V_RESUMENPPTO_E_R AS(
                SELECT  
                        SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,CODIGOSCHIP CODIGOEQUIVALENTE 
                       ,CODIGO_BPIN_SGR
                       ,RECURSOSCHIP                       
                       ,VIGENCIAGASTO
                       ,ORIGENESPECIFICOINGRESOS
                       ,DESTINACIONDELOSRECURSOS
                       ,FINALIDADGASTO
                       ,DEPENDENCIASCHIP
                       ,SITUACIONFONDOSSCHIP
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,UN_CODIGOSGR AS TERCERO
                       ,CASE WHEN VIGENCIAGASTO IN('1') 
                             THEN SUM(REG_CONTRACT+REG_NO_CONTRACT)
                             ELSE 0 
                        END AS EJE_GCSA_GAST
                       ,CASE WHEN VIGENCIAGASTO NOT IN ('3','4') 
                             THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION) 
                             ELSE 0 
                        END AS EJE_OC_GAST
                       ,SUM(EJE_PPT_DEBITO) AS EJE_PAGOS_GAST
                       ,SUM(EJE_PPT_CREDITO) AS ANU_PAGOS_GAST
                       ,SUM(ABS(MODIF_REG_CONTDMR+MODIF_REG_NOCONTDMR)) AS DISMINUCION
                       ,SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACIONARO) AS REO
                       ,SUM(ABS(MODIF_REGISTRO_OBLIGACIONDRO)) AS DISMINUCIONDRO
                       ,SUM(ABS(EJE_PPT_CREDITO)) AS DISMINUCIONDEG
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) AS APLAZADEB,
                        SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) AS APLAZACRE
                   FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                       INNER JOIN SALDO_AUX_PPTAL 
                           ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                           AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                           AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                   WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA 
                    AND PLAN_PRESUPUESTAL.ANO    =UN_ANO 
                    AND PLAN_PRESUPUESTAL.NATURALEZA='D' 
                    AND MES                      <=MI_MESFINAL 
                    AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0) 
                    AND CODIGOSCHIP IS NOT NULL 
                    AND DEPENDENCIASCHIP IS NOT NULL 
                    AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
                    AND DESTINACIONDELOSRECURSOS IS NOT NULL 
                    AND FINALIDADGASTO IS NOT NULL 
                    AND RECURSOSCHIP IS NOT NULL 
                    AND VIGENCIAGASTO IS NOT NULL 
                    AND (CASE WHEN VIGENCIAGASTO In ('1') 
                             THEN REG_CONTRACT+REG_NO_CONTRACT-(MODIF_REG_CONTDMR+MODIF_REG_NOCONTDMR)
                             ELSE 0 
                        END NOT IN (0)  
                        OR REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION NOT IN (0) 
                        OR MODIF_INGRESOS+CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                            THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                            ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                       END NOT IN (0)
                         )    GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                           , SALDO_AUX_PPTAL.ANO
                           , CODIGOSCHIP
                           , CODIGO_BPIN_SGR
                           , VIGENCIAGASTO
                           , FINALIDADGASTO
                           , DEPENDENCIASCHIP
                           , SITUACIONFONDOSSCHIP
                           , PLAN_PRESUPUESTAL.NATURALEZA
                           , UN_CODIGOSGR
                           , ORIGENESPECIFICOINGRESOS
                           , DESTINACIONDELOSRECURSOS
                           , RECURSOSCHIP
                           )
            SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                   ,CODIGO_BPIN_SGR 
                   ,VIGENCIAGASTO AS VIG_GAST
                   ,DEPENDENCIASCHIP AS DEPENDENCIA
                   ,RECURSOSCHIP AS COD_REC                   
                   ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                   ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                   ,FINALIDADGASTO AS COD_FIN
                   ,CASE WHEN SITUACIONFONDOSSCHIP =-1 
                         THEN 'S' 
                         ELSE 'C' 
                    END AS COD_SIT
                   ,TERCERO
                   ,SUM(0) AS NDA_COM
                   ,SUM(0) AS NDA_OBL
                   ,SUM(0) AS NDA_PAG
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN SUM(CASE WHEN VIGENCIAGASTO IN ('1') 
                                       THEN EJE_GCSA_GAST 
                                       ELSE 0 
                                  END) 
                         ELSE 0 
                    END AS EJE_GCCA_GAST
                   ,CASE WHEN UN_ANTICIPOS=-1 
                         THEN 0 
                         ELSE SUM(CASE WHEN VIGENCIAGASTO IN ('1') 
                                       THEN EJE_GCSA_GAST 
                                       ELSE 0 
                                  END)- SUM(CASE WHEN VIGENCIAGASTO IN ('2','3') 
                                                 THEN 0 
                                                 ELSE DISMINUCION 
                                            END) 
                    END AS EJE_GCSA_GAST
                   ,SUM(CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                             THEN 0 
                             ELSE DISMINUCION 
                        END) AS REV_GC_GAST
                   ,SUM(CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                             THEN (REO) 
                             ELSE 0 
                        END)-SUM(DISMINUCIONDRO) AS EJE_OC_GAST
                   ,SUM(EJE_PAGOS_GAST)-SUM(DISMINUCIONDEG) AS EJE_PAGOS_GAST
                   ,SUM(0) AS CXP_GAST_ACEP
                   ,SUM(0) AS OBL_PE_GAST 
              FROM V_RESUMENPPTO_E_R
             WHERE (CASE WHEN UN_ANTICIPOS=-1 
                         THEN CASE WHEN VIGENCIAGASTO IN ('1') 
                                   THEN EJE_GCSA_GAST 
                                   ELSE 0 
                              END 
                         ELSE 0 
                    END
                   +CASE WHEN UN_ANTICIPOS=-1 
                         THEN 0 
                         ELSE CASE WHEN VIGENCIAGASTO IN ('1') 
                                   THEN EJE_GCSA_GAST 
                                   ELSE 0 
                              END 
                    END
                   +CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                         THEN 0 
                         ELSE DISMINUCION 
                    END
                   +CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                         THEN (REO) 
                         ELSE 0 
                    END
                   +DISMINUCIONDRO
                   +EJE_PAGOS_GAST
                   +DISMINUCIONDEG)>0
             GROUP BY   CODIGOEQUIVALENTE
                      , VIGENCIAGASTO
                      , CODIGO_BPIN_SGR
                      , DEPENDENCIASCHIP
                      , RECURSOSCHIP
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , FINALIDADGASTO
                      , SITUACIONFONDOSSCHIP
                      , TERCERO
             ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'              ||CHR(9)
                    ||RS.CONCEPTO      ||CHR(9)
                    ||RS.CODIGO_BPIN_SGR ||CHR(9)
                    ||RS.COD_REC       ||CHR(9)
                    ||RS.COD_OEI       ||CHR(9)
                    ||RS.COD_DEST_ESTT ||CHR(9)
                    ||RS.COD_SIT       ||CHR(9)
                    ||RS.TERCERO       ||CHR(9)
                    ||RS.EJE_GCSA_GAST ||CHR(9)
                    ||RS.EJE_OC_GAST   ||CHR(9)
                    ||RS.EJE_PAGOS_GAST||CHR(9)
                    ||RS.CXP_GAST_ACEP ||CHR(9)
                    ||RS.OBL_PE_GAST   ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP V_RESUMENPPTO_E_R;
    ELSIF MI_TIPOENTIDAD IN(1,2,4,5) THEN  
    	  FOR RS IN (
            WITH V_RESUMENPPTO_E_R AS(
                SELECT   SALDO_AUX_PPTAL.COMPANIA
                        ,SALDO_AUX_PPTAL.ANO
                        ,CODIGOSCHIP CODIGOEQUIVALENTE
                            ,RECURSOSCHIP
                            ,VIGENCIAGASTO
                            ,CODIGO_BPIN_SGR
                            ,ORIGENESPECIFICOINGRESOS
                            ,DESTINACIONDELOSRECURSOS
                            ,FINALIDADGASTO
                            ,SITUACIONFONDOSSCHIP
                            ,PLAN_PRESUPUESTAL.NATURALEZA
                            ,UN_CODIGOSGR AS TERCERO
                            ,CASE WHEN VIGENCIAGASTO IN('1') 
                                  THEN TRUNC(CASE WHEN SUM(REG_CONTRACT+REG_NO_CONTRACT)>0 
                                                  THEN SUM(REG_CONTRACT+REG_NO_CONTRACT)+0.501 
                                                  ELSE SUM(REG_CONTRACT+REG_NO_CONTRACT)-0.501 
                                             END )
                                  ELSE 0 
                             END AS EJE_GCSA_GAST
                            ,CASE WHEN VIGENCIAGASTO NOT IN ('3','4') 
                                  THEN TRUNC(CASE WHEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)>0 
                                                  THEN SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)+0.501 
                                                  ELSE SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION)-0.501 
                                             END) 
                                  ELSE 0 
                             END AS EJE_OC_GAST
                            ,TRUNC(CASE WHEN SUM(EJE_PPT_DEBITO)>0 
                                        THEN SUM(EJE_PPT_DEBITO)+0.501 
                                        ELSE SUM(EJE_PPT_DEBITO)-0.501 
                                   END) AS EJE_PAGOS_GAST
                            ,SUM(EJE_PPT_CREDITO) AS ANU_PAGOS_GAST
                            ,SUM(MODIF_REG_CONTDMR+MODIF_REG_NOCONTDMR) AS DISMINUCION
                            ,SUM(REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACIONARO) AS REO
                            ,SUM(MODIF_REGISTRO_OBLIGACIONDRO) AS DISMINUCIONDRO
                            ,SUM(EJE_PPT_CREDITO) AS DISMINUCIONDEG
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) AS APLAZADEB
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) AS APLAZACRE
                       FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                           INNER JOIN SALDO_AUX_PPTAL 
                               ON  PLAN_PRESUPUESTAL.COMPANIA   = SALDO_AUX_PPTAL.COMPANIA
                               AND PLAN_PRESUPUESTAL.ANO        = SALDO_AUX_PPTAL.ANO
                               AND PLAN_PRESUPUESTAL.ID         = SALDO_AUX_PPTAL.ID
                      WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA 
                        AND PLAN_PRESUPUESTAL.ANO=UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA='D' 
                        AND MES<=MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0) 
                        AND CODIGOSCHIP IS NOT NULL 
                        AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND FINALIDADGASTO IS NOT NULL 
                        AND RECURSOSCHIP IS NOT NULL 
                        AND VIGENCIAGASTO IS NOT NULL 
                        AND CASE WHEN VIGENCIAGASTO In ('1') 
                                 THEN TRUNC(CASE WHEN REG_CONTRACT+REG_NO_CONTRACT>0 
                                                 THEN REG_CONTRACT+REG_NO_CONTRACT+0.501 
                                                 ELSE REG_CONTRACT+REG_NO_CONTRACT-0.501 
                                            END) 
                                 ELSE 0 
                            END NOT IN (0)  
                         OR PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA 
                        AND PLAN_PRESUPUESTAL.ANO=UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA='D' 
                        AND MES<=MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0) 
                        AND CODIGOSCHIP IS NOT NULL 
                        AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND FINALIDADGASTO IS NOT NULL 
                        AND RECURSOSCHIP IS NOT NULL 
                        AND VIGENCIAGASTO IS NOT NULL 
                        AND TRUNC(CASE WHEN REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION>0 
                                       THEN REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION+0.501 
                                       ELSE REGISTRO_OBLIGACION+MODIF_REGISTRO_OBLIGACION-0.501 
                                  END) NOT IN (0) 
                         OR PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA 
                        AND PLAN_PRESUPUESTAL.ANO=UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA='D' 
                        AND MES<=MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0) 
                        AND CODIGOSCHIP IS NOT NULL 
                        AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND FINALIDADGASTO IS NOT NULL 
                        AND RECURSOSCHIP IS NOT NULL 
                        AND VIGENCIAGASTO IS NOT NULL 
                        AND TRUNC(CASE WHEN MODIF_INGRESOS+CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                                THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                                                ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                                           END>0 
                                       THEN MODIF_INGRESOS+CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                                THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                                                ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                                           END+0.501 
                                       ELSE MODIF_INGRESOS+CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                                THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                                                ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                                                                END-0.501 
                                  END) NOT IN (0)
                      GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                               , SALDO_AUX_PPTAL.ANO
                               , CODIGOSCHIP
                               , CODIGO_BPIN_SGR
                               , VIGENCIAGASTO
                               , FINALIDADGASTO
                               , SITUACIONFONDOSSCHIP
                               , PLAN_PRESUPUESTAL.NATURALEZA
                               , UN_CODIGOSGR
                               , ORIGENESPECIFICOINGRESOS
                               , DESTINACIONDELOSRECURSOS
                               , RECURSOSCHIP)
                SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                       ,CODIGO_BPIN_SGR 
                       ,VIGENCIAGASTO AS VIG_GAST
                       ,RECURSOSCHIP AS COD_REC
                       ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                       ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                       ,FINALIDADGASTO AS COD_FIN
                       ,CASE WHEN SITUACIONFONDOSSCHIP =-1 
                             THEN 'S' 
                             ELSE 'C' 
                        END AS COD_SIT
                       ,TERCERO
                       ,SUM(0) AS NDA_COM
                       ,SUM(0) AS NDA_OBL
                       ,SUM(0) AS NDA_PAG
                       ,CASE WHEN UN_ANTICIPOS=-1 
                             THEN SUM(CASE WHEN VIGENCIAGASTO IN ('1') 
                                           THEN EJE_GCSA_GAST 
                                           ELSE 0 
                                      END) 
                             ELSE 0 
                        END AS EJE_GCCA_GAST
                       ,CASE WHEN UN_ANTICIPOS=-1 
                             THEN 0 
                             ELSE SUM(CASE WHEN VIGENCIAGASTO IN ('1') 
                                           THEN EJE_GCSA_GAST 
                                           ELSE 0 
                                      END)- SUM(CASE WHEN VIGENCIAGASTO IN ('2','3') 
                                                     THEN 0 
                                                     ELSE DISMINUCION 
                                                END) 
                        END AS EJE_GCSA_GAST
                       ,SUM(CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                                THEN 0 
                                ELSE DISMINUCION 
                            END) AS REV_GC_GAST
                       ,SUM(CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                                THEN (REO) 
                                ELSE 0 
                            END)-SUM(DISMINUCION) AS EJE_OC_GAST
                       ,SUM(EJE_PAGOS_GAST)-SUM(DISMINUCION) AS EJE_PAGOS_GAST
                       ,SUM(0) AS CXP_GAST_ACEP
                       ,SUM(0) AS OBL_PE_GAST 
                  FROM V_RESUMENPPTO_E_R
                 WHERE (CASE WHEN UN_ANTICIPOS=-1 
                             THEN CASE WHEN VIGENCIAGASTO IN ('1') 
                                       THEN EJE_GCSA_GAST 
                                       ELSE 0 
                                  END 
                             ELSE 0 
                        END
                        +CASE WHEN UN_ANTICIPOS=-1 
                              THEN 0 
                              ELSE CASE WHEN VIGENCIAGASTO IN ('1') 
                                        THEN EJE_GCSA_GAST 
                                        ELSE 0 
                                   END 
                         END
                        +CASE WHEN VIGENCIAGASTO = 2 OR VIGENCIAGASTO =3 
                              THEN 0 
                              ELSE DISMINUCION 
                         END
                        +CASE WHEN VIGENCIAGASTO IN ('1','2','4') 
                              THEN (REO) 
                              ELSE 0 
                         END
                        +DISMINUCIONDRO
                        +EJE_PAGOS_GAST
                        +DISMINUCIONDEG)>0
                   GROUP BY   CODIGOEQUIVALENTE
                            , VIGENCIAGASTO
                            , CODIGO_BPIN_SGR
                            , RECURSOSCHIP
                            , ORIGENESPECIFICOINGRESOS
                            , DESTINACIONDELOSRECURSOS
                            , FINALIDADGASTO
                            , SITUACIONFONDOSSCHIP
                            , TERCERO
                   ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'              ||CHR(9)
                    ||RS.CONCEPTO      ||CHR(9)
                    ||RS.CODIGO_BPIN_SGR ||CHR(9)
                    ||RS.COD_REC       ||CHR(9)
                    ||RS.COD_OEI       ||CHR(9)
                    ||RS.COD_DEST_ESTT ||CHR(9)
                    ||RS.COD_SIT       ||CHR(9)
                    ||RS.TERCERO       ||CHR(9)
                    ||RS.EJE_GCSA_GAST ||CHR(9)
                    ||RS.EJE_OC_GAST   ||CHR(9)
                    ||RS.EJE_PAGOS_GAST||CHR(9)
                    ||RS.CXP_GAST_ACEP ||CHR(9)
                    ||RS.OBL_PE_GAST   ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP V_RESUMENPPTO_E_R;
    ELSE
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                        );
        END;
    END IF;
    RETURN MI_RTA;             
END FC_EJECUCION_GASTOS_REGALIAS;

--12
FUNCTION FC_EJECUCION_INGRESOS
    /*
      NAME              : FC_EJECUCION_INGRESOS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 13/08/2017
      TIME              : 2:26 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de ejecución de ingresos presupuestales para todas las entidades. 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOENTIDAD    => Codigo de la entidad reciproca.
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          --> Se elimino se pasa a consulta UN_TIPOENTIDAD      => Tipo de entidad que rinde el informe al ente de control.
      @NAME  :  generarPlanoEjecucionIngresos
      @METHOD:  GET     
    */ 
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOENTIDAD       IN VARCHAR2
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE;
BEGIN 
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_TIPOENTIDAD:=NULL;
        END; 
        IF MI_TIPOENTIDAD IS NULL THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;
  IF UN_EXCEL=0 THEN  
      MI_RTA:='S'||CHR(9)||
              UN_CODIGOCONTADURIA||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
                   END||CHR(9)||
              UN_ANO||CHR(9)||
              'EJECUCIONDEINGRESOS'||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY')
              ||CHR(13)||CHR(10);
  ELSE
        IF MI_TIPOENTIDAD IN(1,2,4,5) THEN  
           MI_RTA:='CONTROL'         ||CHR(9)||
                   'CONCEPTO'        ||CHR(9)||
                   'COD_REC'         ||CHR(9)||
                   'COD_OEI'         ||CHR(9)||
                   'COD_DEST_ESTT'   ||CHR(9)||
                   'COD_SIT'         ||CHR(9)||
                   'NDA_REC'         ||CHR(9)||
                   'ID_ETRA'         ||CHR(9)||
                   'EE_RECAUDO_INGR' ||CHR(9)||
                   'EE_DEVOLUC_INGR' ||CHR(9)||
                   'EE_REVREC_INGR'  ||CHR(9)||
                   'E_OTRAS_INGR'    ||CHR(9)||
                   'REV_OTRAS_INGR'  ||CHR(9)||
                   'REC_VA_INGR'     ||CHR(9)||
                   'REV_RVA_INGR'    
                    ||CHR(13)||CHR(10);
        ELSIF MI_TIPOENTIDAD IN(6,7) THEN        
               MI_RTA:='CONTROL'         ||CHR(9)||
                       'CONCEPTO'        ||CHR(9)||
                       'COD_REC'         ||CHR(9)||
                       'COD_OEI'         ||CHR(9)||
                       'COD_DEST_ESTT'   ||CHR(9)||
                       'COD_SIT'         ||CHR(9)||
                       'NDA_REC'         ||CHR(9)||
                       'ID_ETRA'         ||CHR(9)||
                       'ND_ACTO_ADTIVO'  ||CHR(9)||
                       'EE_RECAUDO_INGR' ||CHR(9)||
                       'EE_DEVOLUC_INGR' ||CHR(9)||
                       'EE_REVREC_INGR'  ||CHR(9)||
                       'REC_VA_INGR'     ||CHR(9)||
                       'REV_RVA_INGR'    
                        ||CHR(13)||CHR(10);     
        END IF;
  END IF;

   IF MI_TIPOENTIDAD IN(1,2,4,5) THEN  
       <<V_RESUMENPPTO_P_I>>
       FOR RS IN (
           WITH V_RESUMENPPTO_P_I AS(
               SELECT  SALDO_AUX_PPTAL.COMPANIA
                      ,SALDO_AUX_PPTAL.ANO
                      ,CODIGOSCHIP CODIGOEQUIVALENTE
                      ,RECURSOSCHIP
                      ,ORIGENESPECIFICOINGRESOS
                      ,DESTINACIONDELOSRECURSOS
                      ,CASE WHEN SITUACIONFONDOSSCHIP = '0' 
                            THEN 'C' 
                            ELSE 'S' 
                       END COD_SIT
                      ,PLAN_PRESUPUESTAL.NATURALEZA
                      ,COD_RECIPROCA COD_RECIPROCA
                      ,RECAUDO_VA RECAUDO_VANTERIOR
                      ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                           END) APROPIADO
                      ,SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                      ,SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                      ,SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                      ,SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                      ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                      ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                      ,SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                      ,SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                      ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                           END) AS TOTALINGRESOS
                 FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                     INNER JOIN SALDO_AUX_PPTAL 
                         ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                         AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                         AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA--COMPANIA
                  AND PLAN_PRESUPUESTAL.ANO=UN_ANO --ANOTRABAJO
                  AND PLAN_PRESUPUESTAL.NATURALEZA='C'
                  AND SALDO_AUX_PPTAL.MES<=MI_MESFINAL --MESFINAL DEL TRIMESTRE
                  AND PLAN_PRESUPUESTAL.REGALIAS IN(0)
                  AND CODIGOSCHIP IS NOT NULL
                GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                         , SALDO_AUX_PPTAL.ANO
                         , CODIGOSCHIP 
                         , PLAN_PRESUPUESTAL.NATURALEZA
                         , COD_RECIPROCA
                         , RECAUDO_VA
                         , ORIGENESPECIFICOINGRESOS
                         , DESTINACIONDELOSRECURSOS
                         , RECURSOSCHIP
                         , CASE WHEN SITUACIONFONDOSSCHIP='0' 
                                THEN 'C' 
                                ELSE 'S' 
                           END)
           SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                  ,RECURSOSCHIP AS COD_REC
                  ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                  ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                  ,COD_SIT
                  ,'0' AS NDA_REC
                  ,NVL(COD_RECIPROCA,UN_CODIGOENTIDAD) AS ID_ETRA
                  ,CASE WHEN RECAUDO_VANTERIOR<>0 
                        THEN 0 
                        ELSE TRUNC(CASE WHEN TOTALINGRESOS>=0 
                                        THEN TOTALINGRESOS+0.501 
                                        ELSE TOTALINGRESOS-0.501 
                                   END)
                   END AS EE_RECAUDO_INGR
                  ,0 AS EE_DEVOLUC_INGR
                  ,0 AS EE_REVREC_INGR
                  ,0 AS E_OTRAS_INGR
                  ,0 AS REV_OTRAS_INGR
                  ,CASE WHEN RECAUDO_VANTERIOR<>0 
                        THEN TRUNC(CASE WHEN TOTALINGRESOS>=0 
                                        THEN TOTALINGRESOS+0.501 
                                        ELSE TOTALINGRESOS-0.501 
                                   END) 
                        ELSE 0 
                   END AS REC_VA_INGR
                  ,0 AS REV_RVA_INGR
             FROM V_RESUMENPPTO_P_I
            WHERE ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL
              AND RECURSOSCHIP IS NOT NULL
              AND TRUNC(CASE WHEN TOTALINGRESOS>=0 
                             THEN TOTALINGRESOS+0.501 
                             ELSE TOTALINGRESOS-0.501 
                        END) NOT IN (0)
               OR ORIGENESPECIFICOINGRESOS IS NOT NULL
              AND DESTINACIONDELOSRECURSOS IS NOT NULL
              AND RECURSOSCHIP IS NOT NULL
              AND TRUNC(CASE WHEN RECON>=0 
                             THEN RECON+0.501 
                             ELSE RECON-0.501 
                        END) NOT IN (0) 
               OR ORIGENESPECIFICOINGRESOS IS NOT NULL
              AND DESTINACIONDELOSRECURSOS IS NOT NULL
              AND RECURSOSCHIP IS NOT NULL
              AND TRUNC(CASE WHEN VIGENCIAANTERIOR>=0 
                             THEN VIGENCIAANTERIOR+0.501 
                             ELSE VIGENCIAANTERIOR-0.501 
                        END) NOT IN (0)
            ORDER BY CODIGOEQUIVALENTE)
       LOOP
           MI_RTA:= MI_RTA
                    ||'D'                ||CHR(9)
                    ||RS.CONCEPTO        ||CHR(9)
                    ||RS.COD_REC         ||CHR(9)
                    ||RS.COD_OEI         ||CHR(9)
                    ||RS.COD_DEST_ESTT   ||CHR(9)
                    ||RS.COD_SIT         ||CHR(9)
                    ||RS.NDA_REC         ||CHR(9)
                    ||RS.ID_ETRA         ||CHR(9)
                    ||RS.EE_RECAUDO_INGR ||CHR(9)
                    ||RS.EE_DEVOLUC_INGR ||CHR(9)
                    ||RS.EE_REVREC_INGR  ||CHR(9)
                    ||RS.E_OTRAS_INGR    ||CHR(9)
                    ||RS.REV_OTRAS_INGR  ||CHR(9)
                    ||RS.REC_VA_INGR     ||CHR(9)
                    ||RS.REV_RVA_INGR    ||CHR(9)
                    ||CHR(13)||CHR(10);
       END LOOP V_RESUMENPPTO_P_I;
    ELSIF MI_TIPOENTIDAD IN(6,7) THEN
        <<V_RESUMENPPTO_P_I>>
        FOR RS IN (
            WITH V_RESUMENPPTO_P_I AS( 
                SELECT  SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,CODIGOSCHIP CODIGOEQUIVALENTE
                       ,RECURSOSCHIP
                       ,ORIGENESPECIFICOINGRESOS
                       ,DESTINACIONDELOSRECURSOS
                       ,CASE WHEN SITUACIONFONDOSSCHIP = '0' 
                             THEN 'C' 
                             ELSE 'S' 
                        END COD_SIT
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,COD_RECIPROCA COD_RECIPROCA
                       ,RECAUDO_VA RECAUDO_VANTERIOR
                       ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                 ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                            END) APROPIADO
                       ,SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                       ,SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                       ,SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                       ,SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                       ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                 ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                            END )+SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS) AS TOTALINGRESOS
                  FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                      INNER JOIN SALDO_AUX_PPTAL 
                          ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                          AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                          AND PLAN_PRESUPUESTAL.ID   = SALDO_AUX_PPTAL.ID
                 WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA--COMPANIA
                   AND PLAN_PRESUPUESTAL.ANO=UN_ANO --ANOTRABAJO
                   AND PLAN_PRESUPUESTAL.NATURALEZA='C'
                   AND SALDO_AUX_PPTAL.MES<=MI_MESFINAL --MESFINAL DEL TRIMESTRE
                   AND PLAN_PRESUPUESTAL.REGALIAS IN(0)
                   AND CODIGOSCHIP IS NOT NULL
                 GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                          , SALDO_AUX_PPTAL.ANO
                          , CODIGOSCHIP 
                          , PLAN_PRESUPUESTAL.NATURALEZA
                          , COD_RECIPROCA
                          , RECAUDO_VA
                          , ORIGENESPECIFICOINGRESOS
                          , DESTINACIONDELOSRECURSOS
                          , RECURSOSCHIP
                          , CASE WHEN SITUACIONFONDOSSCHIP='0' 
                                 THEN 'C' 
                                 ELSE 'S' 
                            END)
            SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                   ,RECURSOSCHIP AS COD_REC
                   ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                   ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                   ,COD_SIT
                   ,'0' AS NDA_REC
                   ,NVL(COD_RECIPROCA,UN_CODIGOENTIDAD) AS ID_ETRA
                   ,'0' AS ND_ACTO_ADTIVO
                   ,CASE WHEN RECAUDO_VANTERIOR<>0 
                         THEN 0 
                         ELSE TRUNC(CASE WHEN TOTALINGRESOS>=0 
                                         THEN TOTALINGRESOS+0.501 
                                         ELSE TOTALINGRESOS-0.501 
                                    END)
                    END AS EE_RECAUDO_INGR
                   ,0 AS EE_DEVOLUC_INGR
                   ,0 AS EE_REVREC_INGR
                   ,CASE WHEN RECAUDO_VANTERIOR<>0 
                         THEN TRUNC(CASE WHEN TOTALINGRESOS>=0 
                                         THEN TOTALINGRESOS+0.501 
                                         ELSE TOTALINGRESOS-0.501 
                                    END) 
                         ELSE 0 
                    END AS REC_VA_INGR
                   ,0 AS REV_RVA_INGR
              FROM V_RESUMENPPTO_P_I
             WHERE ORIGENESPECIFICOINGRESOS IS NOT NULL 
               AND DESTINACIONDELOSRECURSOS IS NOT NULL
               AND RECURSOSCHIP IS NOT NULL
               AND TRUNC(CASE WHEN TOTALINGRESOS>=0 
                              THEN TOTALINGRESOS+0.501 
                              ELSE TOTALINGRESOS-0.501 
                         END) NOT IN (0) 
                OR ORIGENESPECIFICOINGRESOS IS NOT NULL
               AND DESTINACIONDELOSRECURSOS IS NOT NULL
               AND RECURSOSCHIP IS NOT NULL
               AND TRUNC(CASE WHEN RECON>=0 
                              THEN RECON+0.501 
                              ELSE RECON-0.501 
                         END) NOT IN (0) 
                OR ORIGENESPECIFICOINGRESOS IS NOT NULL
               AND DESTINACIONDELOSRECURSOS IS NOT NULL
               AND RECURSOSCHIP IS NOT NULL
               AND TRUNC(CASE WHEN VIGENCIAANTERIOR>=0 
                              THEN VIGENCIAANTERIOR+0.501 
                              ELSE VIGENCIAANTERIOR-0.501 
                         END) NOT IN (0)
             ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                ||CHR(9)
                    ||RS.CONCEPTO        ||CHR(9)
                    ||RS.COD_REC         ||CHR(9)
                    ||RS.COD_OEI         ||CHR(9)
                    ||RS.COD_DEST_ESTT   ||CHR(9)
                    ||RS.COD_SIT         ||CHR(9)
                    ||RS.NDA_REC         ||CHR(9)
                    ||RS.ID_ETRA         ||CHR(9)
                    ||RS.ND_ACTO_ADTIVO  ||CHR(9)
                    ||RS.EE_RECAUDO_INGR ||CHR(9)
                    ||RS.EE_DEVOLUC_INGR ||CHR(9)
                    ||RS.EE_REVREC_INGR  ||CHR(9)
                    ||RS.REC_VA_INGR     ||CHR(9)
                    ||RS.REV_RVA_INGR    ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP V_RESUMENPPTO_P_I;     
    ELSE
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                        );
        END;  
    END IF;
    RETURN MI_RTA;       
END FC_EJECUCION_INGRESOS;

--13
FUNCTION FC_EJECUCION_INGRESOS_REGALIAS
/*
      NAME              : FC_EJECUCION_INGRESOS_REGAL??AS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 11/08/2017
      TIME              : 8:00 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de ejecución de ingresos presupuestales para todas las entidades que manejan regalías. 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_CODIGOSGR        =>codigo del sistema General de Regalias, este codigo es dado por cada entidad.
      @NAME  :  generarPlanoEjecucionIngresosRegalias
      @METHOD:  GET     
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_CODIGOSGR           IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
BEGIN
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;

    IF UN_EXCEL=0 THEN
        MI_RTA:='S'||CHR(9)||
                UN_CODIGOCONTADURIA||CHR(9)||
                '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                       WHEN 2 THEN '0406'
                                       WHEN 3 THEN '0709'
                                       WHEN 4 THEN '1012'
                     END||CHR(9)||
                UN_ANO||CHR(9)||
                'SGR_EJECUCION_DE_INGRESOS'||CHR(9)||
                TO_CHAR(SYSDATE, 'DD-MM-YYYY')
                ||CHR(13)||CHR(10);
    ELSE
         MI_RTA:='CONTROL'            ||CHR(9)||
                 'CONCEPTO'           ||CHR(9)||
                 'CODIGO_BPIN_SGR'    ||CHR(9)||
                 'COD_REC'            ||CHR(9)||
                 'COD_OEI'            ||CHR(9)||
                 'COD_DEST_ESTT'      ||CHR(9)||
                 'COD_SIT'            ||CHR(9)||
                 'TERCERO'            ||CHR(9)||
                 'EE_RECAUDO_INGR'    ||CHR(13)||CHR(10);
    END IF;

    <<V_RESUMENPPTO_P_I_R>>
    FOR RS IN (
        WITH V_RESUMENPPTO_P_I_R AS(
            SELECT  SALDO_AUX_PPTAL.COMPANIA
                   ,SALDO_AUX_PPTAL.ANO
                   ,CODIGOSCHIP CODIGOEQUIVALENTE
                   ,CODIGO_BPIN_SGR
                   ,RECURSOSCHIP
                   ,ORIGENESPECIFICOINGRESOS
                   ,DESTINACIONDELOSRECURSOS
                   ,CASE WHEN SITUACIONFONDOSSCHIP = '0' 
                         THEN 'C' 
                         ELSE 'S' 
                    END COD_SIT
                   ,UN_CODIGOSGR AS TERCERO
                   ,PLAN_PRESUPUESTAL.NATURALEZA
                   ,COD_RECIPROCA COD_RECIPROCA
                   ,RECAUDO_VA RECAUDO_VANTERIOR
                   ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                             THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                             ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                        END) APROPIADO
                   ,SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                   ,SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                   ,SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                   ,SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                   ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                   ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                   ,SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                   ,SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                   ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                             THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                             ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                        END )+SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS) AS TOTALINGRESOS
              FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                  INNER JOIN SALDO_AUX_PPTAL 
                      ON  PLAN_PRESUPUESTAL.COMPANIA  = SALDO_AUX_PPTAL.COMPANIA
                      AND PLAN_PRESUPUESTAL.ANO       = SALDO_AUX_PPTAL.ANO
                      AND PLAN_PRESUPUESTAL.ID        = SALDO_AUX_PPTAL.ID
             WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA--COMPANIA
               AND PLAN_PRESUPUESTAL.ANO=UN_ANO --ANOTRABAJO
               AND PLAN_PRESUPUESTAL.NATURALEZA='C'
               AND SALDO_AUX_PPTAL.MES<=MI_MESFINAL --MESFINAL DEL TRIMESTRE
               AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0)
               AND CODIGOSCHIP IS NOT NULL
             GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                      , SALDO_AUX_PPTAL.ANO
                      , CODIGOSCHIP 
                      , CODIGO_BPIN_SGR
                      , PLAN_PRESUPUESTAL.NATURALEZA
                      , COD_RECIPROCA
                      , RECAUDO_VA
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , RECURSOSCHIP
                      , CASE WHEN SITUACIONFONDOSSCHIP='0' 
                             THEN 'C' 
                             ELSE 'S' 
                        END)
        SELECT  CODIGOEQUIVALENTE AS CONCEPTO
               ,CODIGO_BPIN_SGR 
               ,RECURSOSCHIP AS COD_REC
               ,ORIGENESPECIFICOINGRESOS AS COD_OEI
               ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
               ,COD_SIT
               ,TERCERO
               ,CASE WHEN RECAUDO_VANTERIOR<>0 
                     THEN 0 
                     ELSE TRUNC(CASE WHEN TOTALINGRESOS>=0 
                                     THEN TOTALINGRESOS+0.501 
                                     ELSE TOTALINGRESOS-0.501 
                                END)
                END AS EE_RECAUDO_INGR 
          FROM V_RESUMENPPTO_P_I_R
         WHERE ORIGENESPECIFICOINGRESOS IS NOT NULL 
           AND DESTINACIONDELOSRECURSOS IS NOT NULL
           AND RECURSOSCHIP IS NOT NULL
           AND TRUNC(CASE WHEN TOTALINGRESOS>=0 
                          THEN TOTALINGRESOS+0.501 
                          ELSE TOTALINGRESOS-0.501 
                     END) NOT IN (0) 
            OR ORIGENESPECIFICOINGRESOS IS NOT NULL
           AND DESTINACIONDELOSRECURSOS IS NOT NULL
           AND RECURSOSCHIP IS NOT NULL
           AND TRUNC(CASE WHEN RECON>=0 
                          THEN RECON+0.501 
                          ELSE RECON-0.501 
                     END) NOT IN (0) 
            OR ORIGENESPECIFICOINGRESOS IS NOT NULL
           AND DESTINACIONDELOSRECURSOS IS NOT NULL
           AND RECURSOSCHIP IS NOT NULL
           AND TRUNC(CASE WHEN VIGENCIAANTERIOR>=0 
                          THEN VIGENCIAANTERIOR+0.501 
                          ELSE VIGENCIAANTERIOR-0.501 
                     END) NOT IN (0)
         ORDER BY CODIGOEQUIVALENTE)
    LOOP       
        MI_RTA:=MI_RTA
                ||'D'                   ||CHR(9)
                ||RS.CONCEPTO           ||CHR(9)
                ||RS.CODIGO_BPIN_SGR    ||CHR(9)
                ||RS.COD_REC            ||CHR(9)
                ||RS.COD_OEI            ||CHR(9)
                ||RS.COD_DEST_ESTT      ||CHR(9)
                ||RS.COD_SIT            ||CHR(9)
                ||RS.TERCERO            ||CHR(9)
                ||RS.EE_RECAUDO_INGR    ||CHR(13)||CHR(10);
    END LOOP V_RESUMENPPTO_P_I_R;
    RETURN MI_RTA;            
END FC_EJECUCION_INGRESOS_REGALIAS;

--14
FUNCTION FC_PROGRAMA_GASTOS_REGALIAS
/*
      NAME              : FC_PROGRAMA_GASTOS_REGAL??AS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 11/08/2017
      TIME              : 12:05 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de programación de gastos presupuestales para todas las entidades que manejan regalías. 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_CODIGOSGR        =>codigo del sistema General de Regalias, este codigo es dado por cada entidad.
                          -->se elimino UN_TIPOENTIDAD      => Tipo de entidad que rinde el informe al ente de control.
      @NAME  :  generarPlanoProgramacionGastosRegalias
      @METHOD:  GET     
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_CODIGOSGR           IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE; 
BEGIN
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_TIPOENTIDAD:=NULL;
        END; 
        IF MI_TIPOENTIDAD IS NULL THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;

    IF UN_EXCEL=0 THEN    
        MI_RTA:='S'||CHR(9)||
                UN_CODIGOCONTADURIA||CHR(9)||
                '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                       WHEN 2 THEN '0406'
                                       WHEN 3 THEN '0709'
                                       WHEN 4 THEN '1012'
                     END||CHR(9)||
                UN_ANO||CHR(9)||
                'SGR_PROGRAMACION_DE_GASTOS'||CHR(9)||
                TO_CHAR(SYSDATE, 'DD-MM-YYYY')
                ||CHR(13)||CHR(10);
    ELSE
             MI_RTA:='CONTROL'          ||CHR(9)||
                     'CONCEPTO'         ||CHR(9)||
                     'CODIGO_BPIN_SGR'  ||CHR(9)||
                     'COD_REC'          ||CHR(9)||                     
                     'COD_OEI'          ||CHR(9)||
                     'COD_DEST_ESTT'    ||CHR(9)||
                     'COD_SIT'          ||CHR(9)||
                     'TERCERO'          ||CHR(9)||
                     'APR_INI_DIS_GAST' ||CHR(9)||
                     'MOD_ADI_GAST'     ||CHR(9)||
                     'MOD_RED_GAST'     ||CHR(9)||
                     'MT_CRE_GAST'      ||CHR(9)||
                     'MT_CCRE_GAST'     ||CHR(9)||
                     'APLAZAMIENTO'     ||CHR(9)||
                     'DESAPLAZAMIENTO'  ||CHR(9)||
                     'APR_DEF_GAST'     ||CHR(9)||
                     'CDP_GAST'         
                     ||CHR(13)||CHR(10);
    END IF;
    --6 Y 7 LLEVAN DEPENDENCIA LAS DEMAS NO
    IF MI_TIPOENTIDAD IN(6,7) OR MI_TIPOENTIDAD IN(1,2,4,5) THEN  
    -- SE COMENTA EL IF DE TIPOENTIDAD 6 Y 7 PUES TIENE LA MISMA CONSULTA QUE 1, 2, 3, 4, 5    
    --ELSIF MI_TIPOENTIDAD IN(1,2,4,5) THEN
        FOR RS IN (SELECT PLAN_PRESUPUESTAL.CODIGOSCHIP CONCEPTO
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO VIG_GAST
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP COD_REC
                            ,PLAN_PRESUPUESTAL.CODIGO_BPIN_SGR
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS COD_OEI
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS COD_DEST_ESTT
                            ,CASE WHEN PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP='0'
                                  THEN 'C' 
                                  ELSE 'S' 
                             END COD_SIT
                            ,UN_CODIGOSGR AS TERCERO
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO COD_FIN    
                            ,SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                      THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                                THEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) 
                                                ELSE ADICION
                                                END 
                                      ELSE (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                 THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                 ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                  END)  
                                      END) AS APR_INI_DIS_GAST
                            , SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                       THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                             THEN SALDO_AUX_PPTAL.ADICION
                                             ELSE 0 
                                        END 
                                   ELSE SALDO_AUX_PPTAL.ADICION
                                   END)  AS MOD_ADI_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.REDUCCION         )) MOD_RED_GAST
                            ,SUM(0) AS CANCELAC_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO   )) MT_CRE_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO  )) MT_CCRE_GAST
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO        ) APLAZAMIENTO
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO         ) DESAPLAZAMIENTO
                            ,SUM(0) AS APR_DEF_GAST
                            ,SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 
                                      ELSE SALDO_AUX_PPTAL.DISPONIBILIDAD --SALDO_AUX_PPTAL.DISPONIBILIDAD - SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END) CDP_GAST
                            ,ABS(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 ELSE SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END)) REV_CDP_GAST
                        FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                           INNER JOIN SALDO_AUX_PPTAL 
                               ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                               AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                               AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                        WHERE PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                        AND PLAN_PRESUPUESTAL.ANO           = UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA    = 'D'
                        AND SALDO_AUX_PPTAL.MES            <= MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS                    NOT IN (0)
                        AND PLAN_PRESUPUESTAL.CODIGOSCHIP              IS NOT NULL
                        AND PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.FINALIDADGASTO           IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.VIGENCIAGASTO            IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.RECURSOSCHIP             IS NOT NULL
                        AND (APROPIACION_DEBITO  NOT IN (0)
                          OR APROPIACION_CREDITO NOT IN (0)
                          OR ADICION          NOT IN (0)
                          OR REDUCCION        NOT IN (0) 
                          OR TRASLADO_DEBITO  NOT IN (0) 
                          OR TRASLADO_CREDITO NOT IN (0)
                          OR APLAZAM_DEBITO   NOT IN (0)
                          OR APLAZAM_CREDITO  NOT IN (0)
                          OR DISPONIBILIDAD    NOT IN (0)
                          OR DISPONIBILIDADADD NOT IN (0)
                          OR DISPONIBILIDADDMD NOT IN (0)
                        )
                        GROUP BY   PLAN_PRESUPUESTAL.CODIGOSCHIP 
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO 
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP 
                            ,PLAN_PRESUPUESTAL.CODIGO_BPIN_SGR
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS 
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS 
                            ,CASE WHEN PLAN_PRESUPUESTAL.SITUACIONFONDOSSCHIP='0'
                                  THEN 'C' 
                                  ELSE 'S' 
                             END 
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO 
                            ,UN_CODIGOSGR
                        ORDER BY PLAN_PRESUPUESTAL.CODIGOSCHIP)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                 ||CHR(9)
                    ||RS.CONCEPTO         ||CHR(9)
                    ||RS.CODIGO_BPIN_SGR  ||CHR(9)
                    ||RS.COD_REC          ||CHR(9)
                    ||RS.COD_OEI          ||CHR(9)                    
                    ||RS.COD_DEST_ESTT    ||CHR(9)
                    ||RS.COD_SIT          ||CHR(9)
                    ||RS.TERCERO          ||CHR(9)
                    ||RS.APR_INI_DIS_GAST ||CHR(9)
                    ||RS.MOD_ADI_GAST     ||CHR(9)
                    ||RS.MOD_RED_GAST     ||CHR(9)
                    ||RS.MT_CRE_GAST      ||CHR(9)
                    ||RS.MT_CCRE_GAST     ||CHR(9)
                    ||RS.APLAZAMIENTO     ||CHR(9) 
                    ||RS.DESAPLAZAMIENTO  ||CHR(9)
                    ||RS.APR_DEF_GAST     ||CHR(9)
                    ||RS.CDP_GAST         ||CHR(9)          
                    ||CHR(13)||CHR(10);
        END LOOP V_RESUMENPPTO_PG_R;
    ELSE 
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                        );
        END;
    END IF;
    RETURN MI_RTA;            
END FC_PROGRAMA_GASTOS_REGALIAS;

--15
FUNCTION FC_PROGRAMA_INGRESOS_REGALIAS
/*
      NAME              : FC_PROGRAMA_INGRESOS_REGAL??AS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 11/08/2017
      TIME              : 3:17 PM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de programación de ingresos presupuestales para todas las entidades que manejan regalías. 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_CODIGOSGR        =>codigo del sistema General de Regalias, este codigo es dado por cada entidad.
      @NAME  :  generarPlanoProgramacionIngresosRegalias
      @METHOD:  GET     
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_CODIGOSGR           IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
BEGIN
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;
  IF UN_EXCEL=0 THEN  
        MI_RTA:='S'||CHR(9)||
                UN_CODIGOCONTADURIA||CHR(9)||
                '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                       WHEN 2 THEN '0406'
                                       WHEN 3 THEN '0709'
                                       WHEN 4 THEN '1012'
                     END||CHR(9)||
                UN_ANO||CHR(9)||
                'SGR_PROGRAMACION_DE_INGRESOS'||CHR(9)||
                TO_CHAR(SYSDATE, 'DD-MM-YYYY')
                ||CHR(13)||CHR(10);
  ELSE
          MI_RTA:='CONTROL'            ||CHR(9)||
                  'CONCEPTO'           ||CHR(9)||
                  'CODIGO_BPIN_SGR'    ||CHR(9)||
                  'COD_REC'            ||CHR(9)||
                  'COD_OEI'            ||CHR(9)||
                  'COD_DEST_ESTT'      ||CHR(9)||
                  'COD_SIT'            ||CHR(9)||
                  'TERCERO'            ||CHR(9)||
                  'PRE_INI_INGR'       ||CHR(9)||
                  'MOD_ADI_INGR'       ||CHR(9)||
                  'MOD_RED_INGR'       ||CHR(9)||
                  'MT_CRE_INGR'        ||CHR(9)||
                  'MT_CCRE_INGR'       ||CHR(9)||
                  'MOD_APLAZA_INGR'    ||CHR(9)||
                  'MOD_DESAPLAZA_INGR' ||CHR(9)||
                  'DEFINITIVO'         ||CHR(13)||CHR(10);  
  END IF;

     <<V_RESUMENPPTO_P_I_R>>
    FOR RS IN (
        WITH V_RESUMENPPTO_P_I_R AS(
            SELECT   SALDO_AUX_PPTAL.COMPANIA
                   , SALDO_AUX_PPTAL.ANO
                   , CODIGOSCHIP CODIGOEQUIVALENTE
                   , CODIGO_BPIN_SGR
                   , RECURSOSCHIP
                   , ORIGENESPECIFICOINGRESOS
                   , DESTINACIONDELOSRECURSOS
                   , CASE WHEN SITUACIONFONDOSSCHIP='0'
                          THEN 'C' 
                          ELSE 'S' 
                     END COD_SIT
                   , PLAN_PRESUPUESTAL.NATURALEZA
                   , UN_CODIGOSGR AS TERCERO
                   , COD_RECIPROCA COD_RECIPROCA
                   , RECAUDO_VA RECAUDO_VANTERIOR
                   , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                              THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                              ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                         END) APROPIADO
                   , SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                   , SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                   , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                              THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                              ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                         END) AS TRASLADO
                   , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                              THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                              ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                         END)
                     +SUM(SALDO_AUX_PPTAL.ADICION)
                     +SUM(SALDO_AUX_PPTAL.REDUCCION) 
                     +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                               THEN TRASLADO_DEBITO-TRASLADO_CREDITO 
                               ELSE TRASLADO_CREDITO-TRASLADO_DEBITO 
                          END) AS DEFINITIVO
                   , SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                   , SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                   , SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                   , SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                   , SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                   , SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                   , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                              THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                              ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                         END)+SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS) AS TOTALINGRESOS
              FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                  INNER JOIN SALDO_AUX_PPTAL 
                      ON  PLAN_PRESUPUESTAL.COMPANIA  = SALDO_AUX_PPTAL.COMPANIA
                      AND PLAN_PRESUPUESTAL.ANO       = SALDO_AUX_PPTAL.ANO
                      AND PLAN_PRESUPUESTAL.ID        = SALDO_AUX_PPTAL.ID
             WHERE PLAN_PRESUPUESTAL.COMPANIA=UN_COMPANIA--COMPANIA
               AND PLAN_PRESUPUESTAL.ANO=UN_ANO --ANOTRABAJO
               AND PLAN_PRESUPUESTAL.NATURALEZA='C'
               AND SALDO_AUX_PPTAL.MES<=MI_MESFINAL --MESFINAL
               AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0)
               AND CODIGOSCHIP IS NOT NULL
             GROUP BY   SALDO_AUX_PPTAL.COMPANIA
                      , SALDO_AUX_PPTAL.ANO
                      , CODIGOSCHIP 
                      , CODIGO_BPIN_SGR
                      , PLAN_PRESUPUESTAL.NATURALEZA
                      , COD_RECIPROCA
                      , RECAUDO_VA
                      , ORIGENESPECIFICOINGRESOS
                      , DESTINACIONDELOSRECURSOS
                      , RECURSOSCHIP
                      , CASE WHEN SITUACIONFONDOSSCHIP='0' 
                             THEN 'C' 
                             ELSE 'S' 
                        END)
        SELECT   CODIGOEQUIVALENTE AS CONCEPTO
               , CODIGO_BPIN_SGR 
               , RECURSOSCHIP AS COD_REC
               , ORIGENESPECIFICOINGRESOS AS COD_OEI
               , DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
               , COD_SIT
               , TERCERO
               , TRUNC(APROPIADO+0.501) AS PRE_INI_INGR
               , TRUNC(ADICION+0.501) AS MOD_ADI_INGR
               , TRUNC(ABS(CASE WHEN REDUCCION>=0 
                                THEN REDUCCION+0.501 
                                ELSE REDUCCION-0.501 
                           END))   AS MOD_RED_INGR
               , TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                                THEN MOD_CREDITO+0.501 
                                ELSE MOD_CREDITO-0.501 
                           END)) AS MT_CRE_INGR
               ,TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                               THEN MOD_DEBITO+0.501 
                               ELSE MOD_DEBITO-0.501 
                          END)) AS MT_CCRE_INGR
               ,TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                               THEN APLAZADEB+0.501 
                               ELSE APLAZADEB-0.501 
                          END)) AS MOD_APLAZA_INGR
               ,TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                               THEN APLAZACRE+0.501 
                               ELSE APLAZACRE-0.501 
                          END)) AS MOD_DESAPLAZA_INGR
               ,DEFINITIVO 
          FROM V_RESUMENPPTO_P_I_R
         WHERE RECURSOSCHIP IS NOT NULL 
           AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
           AND DESTINACIONDELOSRECURSOS IS NOT NULL 
           AND TRUNC(APROPIADO+0.501) NOT IN (0) 
            OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ADICION+0.501) NOT IN (0)
            OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ABS(CASE WHEN REDUCCION>=0 
                             THEN REDUCCION+0.501 
                             ELSE REDUCCION-0.501 
                        END)) NOT IN (0) 
           OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                             THEN MOD_CREDITO+0.501 
                             ELSE MOD_CREDITO-0.501 
                        END)) NOT IN (0) 
           OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                             THEN MOD_DEBITO+0.501 
                             ELSE MOD_DEBITO-0.501 
                        END)) NOT IN (0) 
           OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                             THEN APLAZADEB+0.501 
                             ELSE APLAZADEB-0.501 
                        END)) NOT IN (0) 
           OR RECURSOSCHIP IS NOT NULL 
          AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
          AND DESTINACIONDELOSRECURSOS IS NOT NULL 
          AND TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                             THEN APLAZACRE+0.501 
                             ELSE APLAZACRE-0.501 
                        END)) NOT IN (0)
        ORDER BY CODIGOEQUIVALENTE)
    LOOP       
        MI_RTA:=MI_RTA
                ||'D'                   ||CHR(9)
                ||RS.CONCEPTO           ||CHR(9)
                ||RS.CODIGO_BPIN_SGR    ||CHR(9)
                ||RS.COD_REC            ||CHR(9)
                ||RS.COD_OEI            ||CHR(9)
                ||RS.COD_DEST_ESTT      ||CHR(9)
                ||RS.COD_SIT            ||CHR(9)
                ||RS.TERCERO            ||CHR(9)
                ||RS.PRE_INI_INGR       ||CHR(9)
                ||RS.MOD_ADI_INGR       ||CHR(9)
                ||RS.MOD_RED_INGR       ||CHR(9)
                ||RS.MT_CRE_INGR        ||CHR(9)
                ||RS.MT_CCRE_INGR       ||CHR(9) 
                ||RS.MOD_APLAZA_INGR    ||CHR(9)
                ||RS.MOD_DESAPLAZA_INGR ||CHR(9)
                ||RS.DEFINITIVO         ||CHR(13)||CHR(10);
    END LOOP V_RESUMENPPTO_P_I_R;
    RETURN MI_RTA;               
END FC_PROGRAMA_INGRESOS_REGALIAS;

--16

FUNCTION FC_PROGRAMACION_GASTOS 
/*
      NAME              : FC_PROGRAMACION_GASTOS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 11/08/2017
      TIME              : 5:53 PM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de programación de gastos presupuestales. 
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de Gastos.
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                          UN_TIPOENTIDAD      => Tipo de entidad que rinde el informe al ente de control.
      @NAME  :  generarPlanoProgramacionGastos
      @METHOD:  GET     
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE; 
BEGIN
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_TIPOENTIDAD:=NULL;
        END; 
        IF MI_TIPOENTIDAD IS NULL THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;

    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;

    IF UN_EXCEL=0 THEN
        MI_RTA:='S'||CHR(9)||
                UN_CODIGOCONTADURIA||CHR(9)||
                '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                       WHEN 2 THEN '0406'
                                       WHEN 3 THEN '0709'
                                       WHEN 4 THEN '1012'
                     END||CHR(9)||
                UN_ANO||CHR(9)||
                'PROGRAMACIONDEGASTOS'||CHR(9)||
                TO_CHAR(SYSDATE, 'DD-MM-YYYY')
                ||CHR(13)||CHR(10);
    ELSE
     IF MI_TIPOENTIDAD IN(1,4,5,6,7) THEN  
        MI_RTA:='CONTROL'          ||CHR(9)||             
                'CONCEPTO'         ||CHR(9)||
                'VIG_GAST'         ||CHR(9)||
                'COD_REC'          ||CHR(9)||
                'COD_OEI'          ||CHR(9)||
                'COD_DEST_ESTT'    ||CHR(9)||
                'COD_FIN'          ||CHR(9)||
                'APR_INI_DIS_GAST' ||CHR(9)||
                'MOD_ADI_GAST'     ||CHR(9)||
                'MOD_RED_GAST'     ||CHR(9)||
                'CANCELAC_GAST'    ||CHR(9)||
                'MT_CRE_GAST'      ||CHR(9)||
                'MT_CCRE_GAST'     ||CHR(9)||
                'APLAZAMIENTO'     ||CHR(9)||
                'DESAPLAZAMIENTO'  ||CHR(9)||
                'APR_DEF_GAST'     ||CHR(9)||
                'CDP_GAST'         ||CHR(9)||
                'REV_CDP_GAST'     
                ||CHR(13)||CHR(10);
       ELSIF MI_TIPOENTIDAD IN(2) THEN  
             MI_RTA:='CONTROL'          ||CHR(9)||             
                     'CONCEPTO'         ||CHR(9)||
                     'VIG_GAST'         ||CHR(9)||
                     'COD_REC'          ||CHR(9)||
                     'COD_OEI'          ||CHR(9)||
                     'COD_DEST_ESTT'    ||CHR(9)||
                     'COD_FIN'          ||CHR(9)||
                     'APR_INI_DIS_GAST' ||CHR(9)||
                     'MOD_ADI_GAST'     ||CHR(9)||
                     'MOD_RED_GAST'     ||CHR(9)||
                     'MT_CRE_GAST'      ||CHR(9)||
                     'MT_CCRE_GAST'     ||CHR(9)||
                     'APLAZAMIENTO'     ||CHR(9)||
                     'DESAPLAZAMIENTO'  ||CHR(9)||
                     'APR_DEF_GAST'     
                     ||CHR(13)||CHR(10);       
       END IF;             
    END IF;

    IF MI_TIPOENTIDAD IN(5,6,7,1) THEN  
     --Se identifica que la consulta para la entidad 5, 6, 7, 1 son iguales
     --ELSIF MI_TIPOENTIDAD IN(1) THEN  

        FOR RS IN (SELECT PLAN_PRESUPUESTAL.CODIGOSCHIP CONCEPTO
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO VIG_GAST
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP COD_REC
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS COD_OEI
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS COD_DEST_ESTT
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO COD_FIN    
                            ,ROUND(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                      THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                                THEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) 
                                                ELSE ADICION
                                                END 
                                      ELSE (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                 THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                 ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                  END)  
                                      END),0) AS APR_INI_DIS_GAST
                            , ROUND(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                       THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                             THEN SALDO_AUX_PPTAL.ADICION 
                                             ELSE 0 
                                        END 
                                   ELSE SALDO_AUX_PPTAL.ADICION  
                                   END),0)  AS MOD_ADI_GAST
                            ,ROUND(ABS(SUM (CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                    THEN 0
                                    ELSE (SALDO_AUX_PPTAL.REDUCCION)END)),0) MOD_RED_GAST
                            ,ROUND(ABS(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                  THEN SALDO_AUX_PPTAL.REDUCCION
                                  ELSE 0 END )),0) AS CANCELAC_GAST
                            ,ROUND(ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO   )),0) MT_CRE_GAST
                            ,ROUND(ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO  )),0) MT_CCRE_GAST
                            ,ROUND(SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO        ),0) APLAZAMIENTO
                            ,ROUND(SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO         ),0) DESAPLAZAMIENTO
                            ,SUM(0) AS APR_DEF_GAST
                            ,ROUND(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 
                                      ELSE SALDO_AUX_PPTAL.DISPONIBILIDAD - SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END),0) CDP_GAST
                            ,ROUND(ABS(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 ELSE SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END)),0) REV_CDP_GAST
                        FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                           INNER JOIN SALDO_AUX_PPTAL 
                               ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                               AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                               AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID 
                        WHERE PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                        AND PLAN_PRESUPUESTAL.ANO           = UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA    = 'D'
                        AND SALDO_AUX_PPTAL.MES            <= MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS                 IN (0)                        
                        AND PLAN_PRESUPUESTAL.CODIGOSCHIP              IS NOT NULL                        
                        AND PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.FINALIDADGASTO           IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.VIGENCIAGASTO            IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.RECURSOSCHIP             IS NOT NULL
                        AND (APROPIACION_DEBITO  NOT IN (0)
                          OR APROPIACION_CREDITO NOT IN (0)
                          OR ADICION          NOT IN (0)
                          OR REDUCCION        NOT IN (0) 
                          OR TRASLADO_DEBITO  NOT IN (0) 
                          OR TRASLADO_CREDITO NOT IN (0)
                          OR APLAZAM_DEBITO   NOT IN (0)
                          OR APLAZAM_CREDITO  NOT IN (0)
                          OR DISPONIBILIDAD    NOT IN (0)
                          OR DISPONIBILIDADADD NOT IN (0)
                          OR DISPONIBILIDADDMD NOT IN (0)
                        )
                        GROUP BY   PLAN_PRESUPUESTAL.CODIGOSCHIP 
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO 
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP 
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS 
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS 
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO                             
                        ORDER BY PLAN_PRESUPUESTAL.CODIGOSCHIP)
        LOOP       

            MI_RTA:=MI_RTA
                    ||'D'                           ||CHR(9)
                    ||RS.CONCEPTO                   ||CHR(9)
                    ||TO_CHAR(RS.VIG_GAST)          ||CHR(9)
                    ||TO_CHAR(RS.COD_REC)           ||CHR(9)
                    ||RS.COD_OEI                    ||CHR(9)
                    ||RS.COD_DEST_ESTT              ||CHR(9)
                    ||RS.COD_FIN                    ||CHR(9)
                    ||TO_CHAR(RS.APR_INI_DIS_GAST)  ||CHR(9)
                    ||TO_CHAR(RS.MOD_ADI_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.MOD_RED_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.CANCELAC_GAST)     ||CHR(9)
                    ||TO_CHAR(RS.MT_CRE_GAST )      ||CHR(9)
                    ||TO_CHAR(RS.MT_CCRE_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.APLAZAMIENTO)      ||CHR(9) 
                    ||TO_CHAR(RS.DESAPLAZAMIENTO)   ||CHR(9)
                    ||TO_CHAR(RS.APR_DEF_GAST)      ||CHR(9)   
                    ||TO_CHAR(RS.CDP_GAST)          ||CHR(9)     
                    ||TO_CHAR(RS.REV_CDP_GAST)      ||CHR(9)   
                    ||CHR(13)||CHR(10);
               
        END LOOP RECORRERESUMENPPTO_PG;
    ELSIF MI_TIPOENTIDAD IN(2) THEN 
        FOR RS IN (SELECT PLAN_PRESUPUESTAL.CODIGOSCHIP CONCEPTO
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO VIG_GAST
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP COD_REC
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS COD_OEI
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS COD_DEST_ESTT
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO COD_FIN    
                            ,SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                      THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                                THEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) 
                                                ELSE ADICION
                                                END 
                                      ELSE (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                 THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                 ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                  END)  
                                      END) AS APR_INI_DIS_GAST
                            , TRUNC(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                       THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                             THEN SALDO_AUX_PPTAL.ADICION 
                                             ELSE 0 
                                        END 
                                   ELSE SALDO_AUX_PPTAL.ADICION  
                                   END))  AS MOD_ADI_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.REDUCCION         )) MOD_RED_GAST
                            ,SUM(0) AS CANCELAC_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO   )) MT_CRE_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO  )) MT_CCRE_GAST
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO        ) APLAZAMIENTO
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO         ) DESAPLAZAMIENTO
                            ,SUM(0) AS APR_DEF_GAST
                        FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                           INNER JOIN SALDO_AUX_PPTAL 
                               ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                               AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                               AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                        WHERE PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                        AND PLAN_PRESUPUESTAL.ANO           = UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA    = 'D'
                        AND SALDO_AUX_PPTAL.MES            <= MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS                 IN (0)                        
                        AND PLAN_PRESUPUESTAL.CODIGOSCHIP              IS NOT NULL                        
                        AND PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.FINALIDADGASTO           IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.VIGENCIAGASTO            IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.RECURSOSCHIP             IS NOT NULL
                        AND (APROPIACION_DEBITO  NOT IN (0)
                          OR APROPIACION_CREDITO NOT IN (0)
                          OR ADICION          NOT IN (0)
                          OR REDUCCION        NOT IN (0) 
                          OR TRASLADO_DEBITO  NOT IN (0) 
                          OR TRASLADO_CREDITO NOT IN (0)
                          OR APLAZAM_DEBITO   NOT IN (0)
                          OR APLAZAM_CREDITO  NOT IN (0)
                          OR DISPONIBILIDAD    NOT IN (0)
                          OR DISPONIBILIDADADD NOT IN (0)
                          OR DISPONIBILIDADDMD NOT IN (0)
                        )
                        GROUP BY   PLAN_PRESUPUESTAL.CODIGOSCHIP 
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO 
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP 
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS 
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS 
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO                             
                        ORDER BY PLAN_PRESUPUESTAL.CODIGOSCHIP)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                           ||CHR(9)
                    ||RS.CONCEPTO                   ||CHR(9)
                    ||TO_CHAR(RS.VIG_GAST)          ||CHR(9)
                    ||TO_CHAR(RS.COD_REC)           ||CHR(9)
                    ||RS.COD_OEI                    ||CHR(9)
                    ||RS.COD_DEST_ESTT              ||CHR(9)
                    ||RS.COD_FIN                    ||CHR(9)
                    ||TO_CHAR(RS.APR_INI_DIS_GAST)  ||CHR(9)
                    ||TO_CHAR(RS.MOD_ADI_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.MOD_RED_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.MT_CRE_GAST)       ||CHR(9)
                    ||TO_CHAR(RS.MT_CCRE_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.APLAZAMIENTO)      ||CHR(9) 
                    ||TO_CHAR(RS.DESAPLAZAMIENTO)   ||CHR(9)
                    ||TO_CHAR(RS.APR_DEF_GAST)      ||CHR(9)         
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_PG;
    ELSIF MI_TIPOENTIDAD IN(4) THEN 
        FOR RS IN (SELECT PLAN_PRESUPUESTAL.CODIGOSCHIP CONCEPTO
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO VIG_GAST
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP COD_REC
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS COD_OEI
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS COD_DEST_ESTT
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO COD_FIN    
                            ,SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                      THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                                THEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) 
                                                ELSE ADICION
                                                END 
                                      ELSE (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                 THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                 ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                  END)  
                                      END) AS APR_INI_DIS_GAST
                            , TRUNC(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN('2') 
                                       THEN CASE WHEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                                           THEN APROPIACION_DEBITO  - APROPIACION_CREDITO 
                                                           ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                                                           END) <> 0 
                                             THEN SALDO_AUX_PPTAL.ADICION 
                                             ELSE 0 
                                        END 
                                   ELSE SALDO_AUX_PPTAL.ADICION 
                                   END))  AS MOD_ADI_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.REDUCCION         )) MOD_RED_GAST
                            ,SUM(0) AS CANCELAC_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO   )) MT_CRE_GAST
                            ,ABS(SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO  )) MT_CCRE_GAST
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO        ) APLAZAMIENTO
                            ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO         ) DESAPLAZAMIENTO
                            ,SUM(0) AS APR_DEF_GAST
                            ,SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 
                                      ELSE SALDO_AUX_PPTAL.DISPONIBILIDAD - SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END) CDP_GAST
                            ,ABS(SUM(CASE WHEN PLAN_PRESUPUESTAL.VIGENCIAGASTO  IN ('2','3') 
                                      THEN 0 ELSE SALDO_AUX_PPTAL.DISPONIBILIDADDMD
                                      END)) REV_CDP_GAST
                        FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                           INNER JOIN SALDO_AUX_PPTAL 
                               ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                               AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                               AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                        WHERE PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                        AND PLAN_PRESUPUESTAL.ANO           = UN_ANO 
                        AND PLAN_PRESUPUESTAL.NATURALEZA    = 'D'
                        AND SALDO_AUX_PPTAL.MES            <= MI_MESFINAL 
                        AND PLAN_PRESUPUESTAL.REGALIAS                 IN (0)                        
                        AND PLAN_PRESUPUESTAL.CODIGOSCHIP              IS NOT NULL                        
                        AND PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.FINALIDADGASTO           IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.VIGENCIAGASTO            IS NOT NULL 
                        AND PLAN_PRESUPUESTAL.RECURSOSCHIP             IS NOT NULL
                        AND (APROPIACION_DEBITO  NOT IN (0)
                          OR APROPIACION_CREDITO NOT IN (0)
                          OR ADICION          NOT IN (0)
                          OR REDUCCION        NOT IN (0) 
                          OR TRASLADO_DEBITO  NOT IN (0) 
                          OR TRASLADO_CREDITO NOT IN (0)
                          OR APLAZAM_DEBITO   NOT IN (0)
                          OR APLAZAM_CREDITO  NOT IN (0)
                          OR DISPONIBILIDAD    NOT IN (0)
                          OR DISPONIBILIDADADD NOT IN (0)
                          OR DISPONIBILIDADDMD NOT IN (0)
                        )
                        GROUP BY   PLAN_PRESUPUESTAL.CODIGOSCHIP 
                            ,PLAN_PRESUPUESTAL.VIGENCIAGASTO 
                            ,PLAN_PRESUPUESTAL.RECURSOSCHIP 
                            ,PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS 
                            ,PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS 
                            ,PLAN_PRESUPUESTAL.FINALIDADGASTO                             
                        ORDER BY PLAN_PRESUPUESTAL.CODIGOSCHIP)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                           ||CHR(9)
                    ||RS.CONCEPTO                   ||CHR(9)
                    ||TO_CHAR(RS.VIG_GAST)          ||CHR(9)
                    ||TO_CHAR(RS.COD_REC)           ||CHR(9)
                    ||RS.COD_OEI                    ||CHR(9)
                    ||RS.COD_DEST_ESTT              ||CHR(9)
                    ||RS.COD_FIN                    ||CHR(9)
                    ||TO_CHAR(RS.APR_INI_DIS_GAST)  ||CHR(9)
                    ||TO_CHAR(RS.MOD_ADI_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.MOD_RED_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.CANCELAC_GAST)     ||CHR(9)
                    ||TO_CHAR(RS.MT_CRE_GAST)       ||CHR(9)
                    ||TO_CHAR(RS.MT_CCRE_GAST)      ||CHR(9)
                    ||TO_CHAR(RS.APLAZAMIENTO)      ||CHR(9) 
                    ||TO_CHAR(RS.DESAPLAZAMIENTO)   ||CHR(9)
                    ||TO_CHAR(RS.APR_DEF_GAST)      ||CHR(9)   
                    ||TO_CHAR(RS.CDP_GAST)          ||CHR(9)     
                    ||TO_CHAR(RS.REV_CDP_GAST)      ||CHR(9)   
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_PG;   
    ELSE
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                        );
        END;    
    END IF;
    RETURN MI_RTA;      
END FC_PROGRAMACION_GASTOS;

--17
FUNCTION FC_PROGRAMACION_INGRESOS
/*
      NAME              : FC_PROGRAMACION_INGRESOS
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE MERCHAN
      DATE MIGRADOR     : 14/08/2017
      TIME              : 2:00 PM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 15/08/2017
      TIME              : 02:20 PM
      DESCRIPTION       : Archivo plano de programación de ingresos presupuestales. 
      MODIFICATIONS     : Se realizan ajustes de indentacion y se elimina el parametro UN_TIPOENTIDAD y se realiza la consulta a la tabla COMPANIA  
                          para traer el valor del tipo entidad
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
                          UN_ANO              => Año presupuestal del que se desea obtener la información.
                          UN_TRIMESTRE        => Trimestre para el cual se va a rendir la información del plano de Programación de ingresos.--->numero 1 2 3 4
                          UN_CODIGOCONTADURIA => Codigo de la contaduria asignado a cada entidad que rinde el informe. 
                         --> Se elimino  UN_TIPOENTIDAD      => Tipo de entidad que rinde el informe al ente de control.--numero 1,2 viene de la tabla comnpania del campo tipoentidad
      @NAME  :  generarPlanoProgramacionIngresos
      @METHOD:  GET     
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOCONTADURIA    IN VARCHAR2
   ,UN_EXCEL               IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB 
AS
    MI_RTA         CLOB;
    MI_REDONDEO    PCK_SUBTIPOS.TI_ENTERO; 
    MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
    MI_MES         PCK_SUBTIPOS.TI_ENTERO; 
    MI_ACTOAD      PCK_SUBTIPOS.TI_ENTERO; 
    MI_TIPOENTIDAD COMPANIA.TIPOENTIDAD%TYPE; 
BEGIN
    IF UN_TRIMESTRE = 1 THEN
        MI_MESFINAL:= 3;
    END IF;
    IF UN_TRIMESTRE = 2 THEN 
       MI_MESFINAL:= 6;
    END IF;
    IF UN_TRIMESTRE = 3 THEN 
        MI_MESFINAL:= 9;
    END IF;
    IF UN_TRIMESTRE = 4 THEN 
        MI_MESFINAL:= 12;
    END IF;
    /*consultar tipo entidad de la tabla compania --> Lanzar excepcion debe configurar tipo entidad*/
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
        BEGIN 
            SELECT TIPOENTIDAD 
              INTO MI_TIPOENTIDAD
              FROM COMPANIA
             WHERE CODIGO = UN_COMPANIA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
            MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD 
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;

    MI_ACTOAD:=PCK_SYSMAN_UTL.FC_PAR(
                              UN_COMPANIA  => UN_COMPANIA
                             ,UN_NOMBRE    =>'ACTO ADMINISTRATIVO CGR'
                             ,UN_MODULO    => 99
                             ,UN_FECHA_PAR => SYSDATE);
    IF UN_EXCEL=0 THEN
        MI_RTA:='S'||CHR(9)
                ||UN_CODIGOCONTADURIA||CHR(9)
                ||'1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                         WHEN 2 THEN '0406'
                                         WHEN 3 THEN '0709'
                                         WHEN 4 THEN '1012'
                       END||CHR(9)
                 ||UN_ANO||CHR(9)||
                 'PROGRAMACIONDEINGRESOS'||CHR(9)||
                 TO_CHAR(SYSDATE, 'DD-MM-YYYY')
                 ||CHR(13)||CHR(10);
    ELSE
         IF MI_TIPOENTIDAD IN(6,7) THEN  
            MI_RTA:='CONTROL'            ||CHR(9)||
                    'CONCEPTO'           ||CHR(9)||
                    'COD_REC'            ||CHR(9)||
                    'COD_OEI'            ||CHR(9)||
                    'COD_DEST_ESTT'      ||CHR(9)||
                    'COD_SIT'            ||CHR(9)||
                    'ND_ACTO_ADTIVO'     ||CHR(9)||
                    'PRE_INI_INGR'       ||CHR(9)||
                    'MOD_ADI_INGR'       ||CHR(9)||
                    'MOD_RED_INGR'       ||CHR(9)||
                    'MT_CRE_INGR'        ||CHR(9)||
                    'MT_CCRE_INGR'       ||CHR(9)||
                    'MOD_APLAZA_INGR'    ||CHR(9)||
                    'MOD_DESAPLAZA_INGR' ||CHR(9)||
                    'PRE_DEF_ING'        
                    ||CHR(13)||CHR(10);
         ELSIF MI_TIPOENTIDAD IN(1,2,4,5) THEN
           MI_RTA:='CONTROL'            ||CHR(9)||
                   'CONCEPTO'           ||CHR(9)||
                   'COD_REC'            ||CHR(9)||
                   'COD_OEI'            ||CHR(9)||
                   'COD_DEST_ESTT'      ||CHR(9)||
                   'COD_SIT'            ||CHR(9)||
                   'PRE_INI_INGR'       ||CHR(9)||
                   'MOD_ADI_INGR'       ||CHR(9)||
                   'MOD_RED_INGR'       ||CHR(9)||
                   'MT_CRE_INGR'        ||CHR(9)||
                   'MT_CCRE_INGR'       ||CHR(9)||
                   'MOD_APLAZA_INGR'    ||CHR(9)||
                   'MOD_DESAPLAZA_INGR' ||CHR(9)||
                   'PRE_DEF_ING'        
                   ||CHR(13)||CHR(10);
         END IF;
    END IF;

    IF MI_TIPOENTIDAD IN(6,7) THEN  
        <<RECORRERESUMENPPTO_PG>>
        FOR RS IN (
            WITH V_RESUMENPPTO_P_I AS(
                SELECT  
                        SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,CODIGOSCHIP CODIGOEQUIVALENTE
                       ,RECURSOSCHIP
                       ,ORIGENESPECIFICOINGRESOS
                       ,DESTINACIONDELOSRECURSOS
                       ,CASE WHEN SITUACIONFONDOSSCHIP = '0'
                             THEN 'C' 
                             ELSE 'S' 
                        END COD_SIT
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,COD_RECIPROCA COD_RECIPROCA
                       ,RECAUDO_VA RECAUDO_VANTERIOR
                       ,SUM( CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                  ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                             END) APROPIADO
                       ,SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                       ,SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                       ,SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                       ,SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                       ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                 ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                            END )
                        +SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS) AS TOTALINGRESOS
                   FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                       INNER JOIN SALDO_AUX_PPTAL 
                           ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                           AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                           AND PLAN_PRESUPUESTAL.ID   = SALDO_AUX_PPTAL.ID
                  WHERE PLAN_PRESUPUESTAL.COMPANIA   = UN_COMPANIA
                    AND PLAN_PRESUPUESTAL.ANO        = UN_ANO 
                    AND PLAN_PRESUPUESTAL.NATURALEZA = 'C'
                    AND SALDO_AUX_PPTAL.MES          <=MI_MESFINAL 
                    AND PLAN_PRESUPUESTAL.REGALIAS   IN (0)
                    AND CODIGOSCHIP                  IS NOT NULL
                  GROUP BY SALDO_AUX_PPTAL.COMPANIA
                         , SALDO_AUX_PPTAL.ANO
                         , CODIGOSCHIP 
                         , PLAN_PRESUPUESTAL.NATURALEZA
                         , COD_RECIPROCA
                         , RECAUDO_VA
                         , ORIGENESPECIFICOINGRESOS
                         , DESTINACIONDELOSRECURSOS
                         , RECURSOSCHIP
                         , CASE WHEN SITUACIONFONDOSSCHIP='0' 
                                THEN 'C' 
                                ELSE 'S' 
                           END)
          SELECT  CODIGOEQUIVALENTE AS CONCEPTO
                 ,RECURSOSCHIP AS COD_REC
                 ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                 ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                 ,COD_SIT
                 ,NVL(COD_RECIPROCA,MI_ACTOAD) AS ND_ACTO_ADTIVO
                 ,TRUNC(APROPIADO+0.501) AS PRE_INI_INGR
                 ,TRUNC(ADICION+0.501) AS MOD_ADI_INGR
                 ,TRUNC(ABS(CASE WHEN REDUCCION>=0 
                                 THEN REDUCCION+0.501 
                                 ELSE REDUCCION-0.501 
                            END)) AS MOD_RED_INGR
                 ,TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                                 THEN MOD_CREDITO+0.501 
                                 ELSE MOD_CREDITO-0.501 
                            END)) AS MT_CRE_INGR
                 ,TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                                 THEN MOD_DEBITO+0.501 
                                 ELSE MOD_DEBITO-0.501 
                            END)) AS MT_CCRE_INGR
                 ,TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                                 THEN APLAZADEB+0.501 
                                 ELSE APLAZADEB-0.501 
                            END)) AS MOD_APLAZA_INGR
                 ,TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                                 THEN APLAZACRE+0.501 
                                 ELSE APLAZACRE-0.501 
                            END)) AS MOD_DESAPLAZA_INGR
                 ,0 AS PRE_DEF_ING
            FROM V_RESUMENPPTO_P_I
           WHERE RECURSOSCHIP IS NOT NULL 
             AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
             AND DESTINACIONDELOSRECURSOS IS NOT NULL 
             AND (TRUNC(APROPIADO+0.501) NOT IN (0) 
              OR TRUNC(ADICION+0.501) NOT IN (0)
              OR TRUNC(ABS(CASE WHEN REDUCCION>=0 
                                THEN REDUCCION+0.501 
                                ELSE REDUCCION-0.501 
                           END)) NOT IN (0) 
              OR TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                                THEN MOD_CREDITO+0.501 
                                ELSE MOD_CREDITO-0.501 
                           END)) NOT IN (0) 
              OR TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                                THEN MOD_DEBITO+0.501 
                                ELSE MOD_DEBITO-0.501 
                           END)) NOT IN (0) 
              OR TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                                THEN APLAZADEB+0.501 
                                ELSE APLAZADEB-0.501 
                           END)) NOT IN (0) 
              OR TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                                THEN APLAZACRE+0.501 
                                ELSE APLAZACRE-0.501 
                           END)) NOT IN (0))
           ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                   ||CHR(9)
                    ||RS.CONCEPTO           ||CHR(9)
                    ||RS.COD_REC            ||CHR(9)
                    ||RS.COD_OEI            ||CHR(9)
                    ||RS.COD_DEST_ESTT      ||CHR(9)
                    ||RS.COD_SIT            ||CHR(9)
                    ||RS.ND_ACTO_ADTIVO     ||CHR(9)
                    ||RS.PRE_INI_INGR       ||CHR(9)
                    ||RS.MOD_ADI_INGR       ||CHR(9)
                    ||RS.MOD_RED_INGR       ||CHR(9)
                    ||RS.MT_CRE_INGR        ||CHR(9)
                    ||RS.MT_CCRE_INGR       ||CHR(9) 
                    ||RS.MOD_APLAZA_INGR    ||CHR(9)
                    ||RS.MOD_DESAPLAZA_INGR ||CHR(9)
                    ||RS.PRE_DEF_ING        ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_P_I;
    ELSIF MI_TIPOENTIDAD IN(1,2,4,5) THEN
        FOR RS IN (
            WITH V_RESUMENPPTO_P_I AS(
                SELECT  SALDO_AUX_PPTAL.COMPANIA
                       ,SALDO_AUX_PPTAL.ANO
                       ,CODIGOSCHIP CODIGOEQUIVALENTE
                       ,RECURSOSCHIP
                       ,ORIGENESPECIFICOINGRESOS
                       ,DESTINACIONDELOSRECURSOS
                       ,CASE WHEN SITUACIONFONDOSSCHIP='0'
                             THEN 'C' 
                             ELSE 'S' 
                        END COD_SIT
                       ,PLAN_PRESUPUESTAL.NATURALEZA
                       ,COD_RECIPROCA COD_RECIPROCA
                       ,RECAUDO_VA RECAUDO_VANTERIOR
                       ,SUM( CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                  THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                                  ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                             END) APROPIADO
                       ,SUM(SALDO_AUX_PPTAL.ADICION) ADICION
                       ,SUM(SALDO_AUX_PPTAL.REDUCCION) REDUCCION
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO) MOD_CREDITO
                       ,SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO) MOD_DEBITO
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO) APLAZADEB
                       ,SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO) APLAZACRE
                       ,SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS) RECON
                       ,SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR) VIGENCIAANTERIOR
                       ,SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                 THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO 
                                 ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO 
                            END )
                        +SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS) AS TOTALINGRESOS
                  FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                      INNER JOIN SALDO_AUX_PPTAL
                          ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                          AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                          AND PLAN_PRESUPUESTAL.ID   = SALDO_AUX_PPTAL.ID
                 WHERE PLAN_PRESUPUESTAL.COMPANIA   =  UN_COMPANIA
                   AND PLAN_PRESUPUESTAL.ANO        =  UN_ANO --ANOTRABAJO
                   AND PLAN_PRESUPUESTAL.NATURALEZA =  'C'
                   AND SALDO_AUX_PPTAL.MES          <= MI_MESFINAL --MESFINAL
                   AND PLAN_PRESUPUESTAL.REGALIAS   IN(0)
                   AND CODIGOSCHIP                  IS NOT NULL
                 GROUP BY  SALDO_AUX_PPTAL.COMPANIA
                          ,SALDO_AUX_PPTAL.ANO
                          ,CODIGOSCHIP 
                          ,PLAN_PRESUPUESTAL.NATURALEZA
                          ,COD_RECIPROCA
                          ,RECAUDO_VA
                          ,ORIGENESPECIFICOINGRESOS
                          ,DESTINACIONDELOSRECURSOS
                          ,RECURSOSCHIP
                          ,CASE WHEN SITUACIONFONDOSSCHIP='0' 
                                THEN 'C' 
                                ELSE 'S' 
                           END)
            SELECT CODIGOEQUIVALENTE AS CONCEPTO
                  ,RECURSOSCHIP AS COD_REC
                  ,ORIGENESPECIFICOINGRESOS AS COD_OEI
                  ,DESTINACIONDELOSRECURSOS AS COD_DEST_ESTT
                  ,COD_SIT
                  ,TRUNC(APROPIADO+0.501) AS PRE_INI_INGR
                  ,TRUNC(ADICION+0.501) AS MOD_ADI_INGR
                  ,TRUNC(ABS(CASE WHEN REDUCCION>=0 
                                  THEN REDUCCION+0.501 
                                  ELSE REDUCCION-0.501 
                             END)) AS MOD_RED_INGR
                  ,TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                                  THEN MOD_CREDITO+0.501 
                                  ELSE MOD_CREDITO-0.501 
                             END)) AS MT_CRE_INGR
                  ,TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                                  THEN MOD_DEBITO+0.501 
                                  ELSE MOD_DEBITO-0.501 
                             END)) AS MT_CCRE_INGR
                  ,TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                                  THEN APLAZADEB+0.501 
                                  ELSE APLAZADEB-0.501 
                             END)) AS MOD_APLAZA_INGR
                  ,TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                                  THEN APLAZACRE+0.501 
                                  ELSE APLAZACRE-0.501 
                             END)) AS MOD_DESAPLAZA_INGR
                  ,0 AS PRE_DEF_ING
             FROM V_RESUMENPPTO_P_I
            WHERE RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(APROPIADO+0.501)   NOT IN (0) 
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ADICION+0.501) NOT IN (0)
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ABS(CASE WHEN REDUCCION>=0 
                                 THEN REDUCCION+0.501 
                                 ELSE REDUCCION-0.501 
                            END)) NOT IN (0)
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ABS(CASE WHEN MOD_CREDITO>=0 
                                 THEN MOD_CREDITO+0.501 
                                 ELSE MOD_CREDITO-0.501 
                            END)) NOT IN (0)
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ABS(CASE WHEN MOD_DEBITO>=0 
                                 THEN MOD_DEBITO+0.501 
                                 ELSE MOD_DEBITO-0.501 
                            END)) NOT IN (0) 
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ABS(CASE WHEN APLAZADEB>=0 
                                 THEN APLAZADEB+0.501 
                                 ELSE APLAZADEB-0.501 
                            END)) NOT IN (0) 
               OR RECURSOSCHIP IS NOT NULL 
              AND ORIGENESPECIFICOINGRESOS IS NOT NULL 
              AND DESTINACIONDELOSRECURSOS IS NOT NULL 
              AND TRUNC(ABS(CASE WHEN APLAZACRE>=0 
                                 THEN APLAZACRE+0.501 
                                 ELSE APLAZACRE-0.501 
                            END)) NOT IN (0)
            ORDER BY CODIGOEQUIVALENTE)
        LOOP       
            MI_RTA:=MI_RTA
                    ||'D'                   ||CHR(9)
                    ||RS.CONCEPTO           ||CHR(9)
                    ||RS.COD_REC            ||CHR(9)
                    ||RS.COD_OEI            ||CHR(9)
                    ||RS.COD_DEST_ESTT      ||CHR(9)
                    ||RS.COD_SIT            ||CHR(9)
                    ||RS.PRE_INI_INGR       ||CHR(9)
                    ||RS.MOD_ADI_INGR       ||CHR(9)
                    ||RS.MOD_RED_INGR       ||CHR(9)
                    ||RS.MT_CRE_INGR        ||CHR(9)
                    ||RS.MT_CCRE_INGR       ||CHR(9) 
                    ||RS.MOD_APLAZA_INGR    ||CHR(9)
                    ||RS.MOD_DESAPLAZA_INGR ||CHR(9)
                    ||RS.PRE_DEF_ING        ||CHR(9)
                    ||CHR(13)||CHR(10);
        END LOOP RECORRERESUMENPPTO_P_I; 
    ELSE
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD --No existe información para este tipo de entidad.
                        );
        END;    
    END IF;
    RETURN MI_RTA;            
END FC_PROGRAMACION_INGRESOS;
--18
FUNCTION FC_CODIGOSCHIP
/*
      NAME              : FC_CODIGOSCHIP
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 24/08/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanCGR2017.07.02
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Trae el valor por defecto para la generacion de los planos CGR
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación.
      @NAME  :  consultarCodigoSChip
      @METHOD:  GET     
    */
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
)RETURN COMPANIA.CODIGOSCHIP%TYPE
AS
    MI_CODIGOSCHIP COMPANIA.CODIGOSCHIP%TYPE;
BEGIN
    BEGIN 
        SELECT CODIGOSCHIP
          INTO MI_CODIGOSCHIP  
          FROM COMPANIA 
         WHERE CODIGO = UN_COMPANIA; 
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CODIGOSCHIP:=NULL;
    END;
    If MI_CODIGOSCHIP IS NULL THEN 
    	DECLARE 
    	    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    	BEGIN
    	    MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
    	    MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
    	    RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
        	PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_INFOTIPOENTIDAD
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       ,UN_TABLAERROR => 'COMPANIA' 
                        );
    	END;
    END IF;
    RETURN MI_CODIGOSCHIP;
END FC_CODIGOSCHIP;

--19

FUNCTION FC_VALIDACONFPPTAL (

/*      NAME              : FC_VALIDACONFPPTAL -->
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 28/03/2018
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Carga datos de configuracion de cuentas pptal desde CGR
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_CADENA           => Cadena que trae datos de excell
                            UN_ANIO             => Anio de formulario
                            UN_ENTIDAD          => Entidad desde actualizacion de datos
                            UN_REGALIAS         => Regalías desde actualizacion de datos
                            UN_NATURALEZA       => Entidad desde actualizacion de datos
                            UN_USUARIO          => Usuario que realizar el procedimiento

        @NAME  :  validaConfPptal
        @METHOD:  PUT
    */
 UN_CADENA      IN CLOB,
 UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_ANIO        IN PLAN_PPTAL_CONFIG.ANO%TYPE,
 UN_ENTIDAD     IN PLAN_SCHIP.ENTIDAD%TYPE,
 UN_REGALIAS    IN VARCHAR2,
 UN_NATURALEZA  IN VARCHAR2,
 UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS 
    MI_LINEA      PCK_SYSMAN_UTL.T_SPLIT;
    MI_CADENA     PCK_SYSMAN_UTL.T_SPLIT;
    UN_NUMERO     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_NUMREG     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_NUMCOL     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CODIGO     PLAN_SCHIP.CODIGO%TYPE;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_MOVIMIENTO PCK_SUBTIPOS.TI_LOGICO;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_ERRORES    CLOB;
    MI_RTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RECURSOSCHIP         PLAN_PPTAL_CONFIG.RECURSOSCHIP%TYPE;
    MI_ORIGENESPINGRESOS	  PLAN_PPTAL_CONFIG.ORIGENESPECIFICOINGRESOS%TYPE;
    MI_DESTINACIONRECURSOS	PLAN_PPTAL_CONFIG.DESTINACIONDELOSRECURSOS%TYPE;
    MI_DEPENDENCIASCHIP		  PLAN_PPTAL_CONFIG.DEPENDENCIASCHIP%TYPE;
    MI_FINALIDADGASTO		    PLAN_PPTAL_CONFIG.FINALIDADGASTO%TYPE;
    MI_VIGENCIAGASTO		    PLAN_PPTAL_CONFIG.VIGENCIAGASTO%TYPE;
    MI_VIGENCIATESORERIA	  PLAN_PPTAL_CONFIG.VIGENCIATESORERIASCHIP%TYPE;
    MI_RESGUARDOCHIP		    PLAN_PPTAL_CONFIG.CR1_RESGUARDOSCHIP%TYPE;
    MI_CODIGOSIRECI			    PLAN_PPTAL_CONFIG.CODIGOSIRECI%TYPE;
    MI_RECAUDOVA			      PLAN_PPTAL_CONFIG.RECAUDO_VA%TYPE;
    MI_CODRECIPROCA			    PLAN_PPTAL_CONFIG.COD_RECIPROCA%TYPE;
    MI_LINEACAMPO           CLOB;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                VARCHAR2(50);

BEGIN
    MI_TABLA := 'PLAN_PPTAL_CONFIG';    
    MI_CADENA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                            UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    FOR RS IN MI_CADENA.FIRST..MI_CADENA.LAST 
    LOOP
        MI_LINEA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA      => MI_CADENA(RS),
                                                UN_DELIMITADOR=> PCK_DATOS.GL_SEPARADOR_COL);

            MI_VALOR := MI_LINEA(3);
                IF MI_LINEA(3) IS NOT NULL OR MI_LINEA(3) NOT IN ('') THEN 
                    BEGIN
                        SELECT MOVIMIENTO 
                          INTO MI_MOVIMIENTO 
                          FROM PLAN_SCHIP 
                         WHERE ENTIDAD = UN_ENTIDAD
                           AND CODIGO = MI_LINEA(3);                      
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_ERRORES := MI_ERRORES || 'EL CODIGO NO EXISTE EN LOS CODIGOS CHIP ' || MI_LINEA(3) || PCK_DATOS.GL_SEPARADOR_COL;
                    END;
                END IF;

                    BEGIN            
                        BEGIN
                            MI_VALORES    :=  '     CODIGOSCHIP               ='''  || MI_LINEA(3)  || 
                                              ''',  RECURSOSCHIP              ='    || MI_LINEA(4)  || 
                                              ',    ORIGENESPECIFICOINGRESOS  ='''  || MI_LINEA(5)  || 
                                              ''',  DESTINACIONDELOSRECURSOS  ='''  || MI_LINEA(6)  ||
                                              ''',  DEPENDENCIASCHIP          ='''  || MI_LINEA(7)  ||
                                              ''',  FINALIDADGASTO            ='''  || MI_LINEA(8)  ||
                                              ',    VIGENCIAGASTO             ='    || MI_LINEA(10) ||
                                              ',    VIGENCIATESORERIASCHIP    ='    || MI_LINEA(11) ||
                                              ',    CR1_RESGUARDOSCHIP        ='''  || MI_LINEA(12) ||
                                              ''',  CODIGOSIRECI              ='''  || MI_LINEA(13) ||
                                              ''',  RECAUDO_VA                ='    || MI_LINEA(14) ||
                                              ',    COD_RECIPROCA             ='''  || MI_LINEA(15) || ''''; 

                            MI_CONDICION  :=  'PLAN_PPTAL_CONFIG.COMPANIA           = '''||UN_COMPANIA  ||'''                 
                                               AND PLAN_PPTAL_CONFIG.ANO            = '''||UN_ANIO      ||'''                  
                                               AND PLAN_PPTAL_CONFIG.CODIGO         = '''||MI_LINEA(1)  ||'''
                                               AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = '''||MI_LINEA(16) ||'''
                                               AND PLAN_PPTAL_CONFIG.TERCERO        = '''||MI_LINEA(17) ||'''
                                               AND PLAN_PPTAL_CONFIG.SUCURSAL       = '''||MI_LINEA(18) ||'''
                                               AND PLAN_PPTAL_CONFIG.AUXILIAR       = '''||MI_LINEA(19) ||'''
                                               AND PLAN_PPTAL_CONFIG.REFERENCIA     = '''||MI_LINEA(20) ||'''
                                               AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = '''||MI_LINEA(21) || '''';                  

                            MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                         ,UN_ACCION    => 'M' 
                                                         ,UN_CAMPOS    => MI_VALORES 
                                                         ,UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ENTESCONTROL; 
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESCONTROL THEN 
                        MI_ERRORES := MI_ERRORES || 'ERROR AL ACTUALIZAR LA CUENTA ' || MI_LINEA(1) || PCK_DATOS.GL_SEPARADOR_COL;
                    END;                


    END LOOP;  


  RETURN MI_ERRORES;
END FC_VALIDACONFPPTAL;

PROCEDURE PR_ACTUALIZAR_CODIGOCCEPT
/*
  NAME              : PR_ACTUALIZAR_CODIGOCCEPT  
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 22/06/2022
  TIME              : 04:00 PM
  SOURCE MODULE     : 
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Procedimiento que se encarga de actualizar los codigos CCEPT en PLAN_PPTAL_CONFIG
  PARAMETERS        : 
  @Name             :actualizarCodigoCCEPT
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,  
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO          
)
AS
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;  
  MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;  
  MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;  
 BEGIN
 
    MI_MERGEUSING := 'SELECT PLAN_PRESUPUESTAL.CODIGO,
                             PLAN_PRESUPUESTAL.ANO,
                             PLAN_PRESUPUESTAL.COMPANIA
                      FROM PLAN_PRESUPUESTAL
                        INNER JOIN CUIPO_CODIGO_CCEPT ON PLAN_PRESUPUESTAL.COMPANIA = CUIPO_CODIGO_CCEPT.COMPANIA
                                 AND PLAN_PRESUPUESTAL.ANO = CUIPO_CODIGO_CCEPT.ANO
                                 AND PLAN_PRESUPUESTAL.CODIGO = CUIPO_CODIGO_CCEPT.CODIGO
                       WHERE CUIPO_CODIGO_CCEPT.COMPANIA = '''||UN_COMPANIA||'''   ';                   
                                 
     MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANO
                     AND TABLA.CODIGO = VISTA.CODIGO';
                             
     MI_MERGEEXISTE := ' UPDATE SET TABLA.CODIGO_CCEPT = VISTA.CODIGO,
                              TABLA.DATE_MODIFIED         = SYSDATE,
                              TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';          
                              
     BEGIN
      BEGIN
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'PLAN_PPTAL_CONFIG'
                                   ,UN_ACCION      => 'MM' 
                                   ,UN_MERGEUSING  => MI_MERGEUSING 
                                   ,UN_MERGEENLACE => MI_MERGEENLACE 
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);  
                                   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO);      
   END;        
   
END  PR_ACTUALIZAR_CODIGOCCEPT;

PROCEDURE PR_ACT_CAMPOS_CUIPO
 /*
    NAME              : PR_ACT_CAMPOS_CUIPO
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : 
    PARAMETERS        : 

    @NAME: actCamposCuipo  
    @METHOD: PUT      
  */ 
(
   UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANIO                  IN PCK_SUBTIPOS.TI_ANIO,
   UN_CAMPO                 IN VARCHAR2,
   UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO       
)
AS 

   MI_VALORES        PCK_SUBTIPOS.TI_VALORES;   
   MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;   
   MI_RS             SYS_REFCURSOR;
   MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_COMPANIA       PLAN_PPTAL_CONFIG.COMPANIA%TYPE;
   MI_ANIO              PLAN_PPTAL_CONFIG.ANO%TYPE;
   MI_RUBRO             PLAN_PPTAL_CONFIG.CODIGO%TYPE;   
   MI_FUENTE            PLAN_PPTAL_CONFIG.FUENTE_CUIPO%TYPE;
   MI_CODIGO_CPC        PLAN_PPTAL_CONFIG.CODIGO_CPC_CUIPO%TYPE;
   MI_CODIGO_PRODUCTO   PLAN_PPTAL_CONFIG.CODIGO_PRODUCTO_CUIPO%TYPE;
   MI_CODIGO_BPIN       PLAN_PPTAL_CONFIG.CODIGO_BPIN_SGR%TYPE;
   MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR; 
   MI_CONDICIONES       VARCHAR2(100 CHAR);
   MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
   MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;

BEGIN      
    
    MI_CONDICIONES := CASE UN_CAMPO WHEN 'CPC' THEN 'AND CODIGO_CPC_CUIPO IS NOT NULL '
                                    WHEN 'FUENTE' THEN 'AND FUENTE_CUIPO IS NOT NULL'
                                    WHEN 'PRODUCTO' THEN 'AND CODIGO_PRODUCTO_CUIPO IS NOT NULL' 
                                    WHEN 'CODIGO_BPIN' THEN 'AND CODIGO_BPIN_SGR IS NOT NULL' 
                                    END;
                            
    
            MI_STRSQL := 'SELECT
                            COMPANIA,
                            ANO,
                            CODIGO,
                            FUENTE_CUIPO,
                            CODIGO_CPC_CUIPO,
                            CODIGO_PRODUCTO_CUIPO,
                            CODIGO_BPIN_SGR
                        FROM PLAN_PPTAL_CONFIG
                        WHERE COMPANIA = '''|| UN_COMPANIA || '''
                        AND ANO = '|| UN_ANIO
                        || MI_CONDICIONES;

       <<PLAN_PPTAL_CUIPO>>
       OPEN MI_RS FOR MI_STRSQL;
       LOOP 
       FETCH MI_RS INTO  MI_COMPANIA, MI_ANIO,MI_RUBRO, MI_FUENTE, MI_CODIGO_CPC, MI_CODIGO_PRODUCTO,MI_CODIGO_BPIN;
           EXIT WHEN MI_RS%NOTFOUND; 


                MI_TABLA  := 'DETALLE_COMPROBANTE_PPTAL';
                
                
                IF UN_CAMPO = 'CPC' THEN
                
                MI_CAMPOS :=  'CODIGO_CPC = ''' || MI_CODIGO_CPC || ''', 
                               MODIFIED_BY = ''' || UN_USUARIO || ''',
                               DATE_MODIFIED = SYSDATE' ;
                               
                ELSIF UN_CAMPO = 'FUENTE' THEN
                
                MI_CAMPOS :=  'FUENTE_CUIPO = ''' || MI_FUENTE || ''', 
                               MODIFIED_BY = ''' || UN_USUARIO || ''',
                               DATE_MODIFIED = SYSDATE' ;
                               
                ELSIF UN_CAMPO = 'CODIGO_BPIN' THEN
                
                MI_CAMPOS :=  'CODIGO_BPIN = ''' || MI_CODIGO_BPIN || ''', 
                               MODIFIED_BY = ''' || UN_USUARIO || ''',
                               DATE_MODIFIED = SYSDATE' ;                                   
                               
                ELSIF UN_CAMPO = 'PRODUCTO' THEN
                
                MI_CAMPOS :=  'COD_PROD_CUIPO = ''' || MI_CODIGO_PRODUCTO || ''', 
                               MODIFIED_BY = ''' || UN_USUARIO || ''',
                               DATE_MODIFIED = SYSDATE' ;
                END IF;    

                               
                MI_CONDICION := 'COMPANIA   =   '''|| UN_COMPANIA ||'''
                                 AND ANO    =   '''|| MI_ANIO || '''
                                 AND CUENTA =   '''|| MI_RUBRO ||'''';
                 BEGIN
                    BEGIN                 
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                             UN_ACCION     => 'M', 
                                             UN_CAMPOS     => MI_CAMPOS, 
                                             UN_CONDICION  => MI_CONDICION);
                                    

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    END;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN  
                                MI_MSGERROR(1).CLAVE := 'TABLA';
                                MI_MSGERROR(1).VALOR := 'D_MOVIMIENTO';
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD   => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA,
                                        UN_REEMPLAZOS  => MI_MSGERROR 
                                      );
              END;            

       END LOOP PLAN_PPTAL_CUIPO;     
    
END PR_ACT_CAMPOS_CUIPO;


PROCEDURE PR_ACTUALIZAR_CAMPOSCUIPOAFECT
/*
  NAME              : PR_ACTUALIZAR_CAMPOSCUIPOAFECT  
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 22/06/2022
  TIME              : 05:500 PM
  SOURCE MODULE     : 
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Procedimiento que se encarga de actualizar los codigos CCEPT en PLAN_PPTAL_CONFIG
  PARAMETERS        : 
  @Name             :actualizarCamposCuipoAfect
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,  
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO          
)
AS
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_MERGEUSING        PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEUSINGWHERE   PCK_SUBTIPOS.TI_MERGEUSING;  
  MI_MERGEENLACE       PCK_SUBTIPOS.TI_MERGEENLACE;  
  MI_MERGEEXISTE       PCK_SUBTIPOS.TI_MERGEEXISTE;  
 BEGIN
 
--Nota el año queda quemado hay que crear el parámetro ano y enviarlo en la función se espera  ticket para realizar el cambio 


    MI_MERGEUSING := ' SELECT DISTINCT DETALLE_COMPROBANTE_PPTAL.COMPANIA,
                               DETALLE_COMPROBANTE_PPTAL.ANO,
                               DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                               DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
                               DETALLE_COMPROBANTE_PPTAL.CUENTA,
                               DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
                               LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC )CODIGO_BPIN
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.SECTOR) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) SECTOR
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.PROGRAMA) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) PROGRAMA
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) SUBPROGRAMA
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) COD_PROD_CUIPO
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) CODIGO_CCPET
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) CODIGO_CPC
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) CODIGOUNIDADEJE
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) FUENTE_CUIPO
                              ,LAST_VALUE(DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA) IGNORE NULLS OVER (PARTITION BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO)  ORDER BY (DETALLE_COMPROBANTE_PPTAL.COMPANIA || DETALLE_COMPROBANTE_PPTAL.ANO || DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE || DETALLE_COMPROBANTE_PPTAL.COMPROBANTE||DETALLE_COMPROBANTE_PPTAL.CUENTA || DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO) ASC ) CODIGOCCPETREGA
                FROM DETALLE_COMPROBANTE_PPTAL INNER JOIN TIPO_COMPROBPP
                 ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA
                 AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO ';

MI_MERGEUSINGWHERE := MI_MERGEUSING || '
                WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                 AND CLASE= ''DIS''
                 AND ANO  =  2022
                 AND( DETALLE_COMPROBANTE_PPTAL.SECTOR IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.PROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA IS NOT NULL)  ';                   
                                 
     MI_MERGEENLACE := ' TABLA.COMPANIA         = VISTA.COMPANIA
                     AND TABLA.ANO_AFECT         = VISTA.ANO
                     AND TABLA.TIPO_CPTE_AFECT   = VISTA.TIPO_CPTE
                     AND TABLA.CMPTE_AFECTADO    = VISTA.COMPROBANTE
                     AND TABLA.CONSECUTIVOPPTO   = VISTA.CONSECUTIVO';
                             
     MI_MERGEEXISTE := ' UPDATE SET TABLA.CODIGO_BPIN      = VISTA.CODIGO_BPIN,
                              TABLA.SECTOR           = VISTA.SECTOR,
                              TABLA.PROGRAMA         = VISTA.PROGRAMA,
                              TABLA.SUBPROGRAMA      = VISTA.SUBPROGRAMA,
                              TABLA.COD_PROD_CUIPO   = VISTA.COD_PROD_CUIPO,
                              TABLA.CODIGO_CCPET      = VISTA.CODIGO_CCPET,
                              TABLA.CODIGO_CPC    = VISTA.CODIGO_CPC,
                              TABLA.CODIGOUNIDADEJE  = VISTA.CODIGOUNIDADEJE,
                              TABLA.FUENTE_CUIPO     = VISTA.FUENTE_CUIPO,
                              TABLA.CODIGOCCPETREGA  = VISTA.CODIGOCCPETREGA,
                              TABLA.DATE_MODIFIED    = SYSDATE,
                              TABLA.MODIFIED_BY      = '''||UN_USUARIO||''' ';          
                              
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                   ,UN_ACCION      => 'MM' 
                                   ,UN_MERGEUSING  => MI_MERGEUSINGWHERE 
                                   ,UN_MERGEENLACE => MI_MERGEENLACE 
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);  
                                   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO);      
    END;  

    MI_MERGEUSINGWHERE := MI_MERGEUSING || '
                WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                 AND CLASE= ''RES''
                 AND ANO  =  2022
                 AND(DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SECTOR IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.PROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA IS NOT NULL)  ';       
       BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                   ,UN_ACCION      => 'MM' 
                                   ,UN_MERGEUSING  => MI_MERGEUSINGWHERE 
                                   ,UN_MERGEENLACE => MI_MERGEENLACE 
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);  
                                   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO);      
    END;  
    MI_MERGEUSINGWHERE := MI_MERGEUSING || '
                WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                 AND CLASE= ''REO''
                 AND ANO  =  2022
                 AND( DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SECTOR IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.PROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA IS NOT NULL)  ';       
       BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                   ,UN_ACCION      => 'MM' 
                                   ,UN_MERGEUSING  => MI_MERGEUSINGWHERE 
                                   ,UN_MERGEENLACE => MI_MERGEENLACE 
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);  
                                   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO);      
    END; 
     MI_MERGEUSINGWHERE := MI_MERGEUSING || '
                WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                 AND CLASE= ''EGR''
                 AND ANO  =  2022
                 AND( DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SECTOR IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.PROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO IS NOT NULL
                  OR DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA IS NOT NULL)  ';       
       BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                   ,UN_ACCION      => 'MM' 
                                   ,UN_MERGEUSING  => MI_MERGEUSINGWHERE 
                                   ,UN_MERGEENLACE => MI_MERGEENLACE 
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);  
                                   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO);      
    END;
     
END  PR_ACTUALIZAR_CAMPOSCUIPOAFECT;

PROCEDURE PR_CARGAR_TIPO_RECURSO 
/*
    NAME              : PR_CARGAR_TIPO_RECURSO
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     :                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : 

    @NAME: cargarTipoRecurso
    @METHOD:  PUT
    */
( 
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA       IN CLOB,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
 MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGAR_PMR_FUENTE>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                 UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
   BEGIN   
   BEGIN
                  
                MI_TABLA  := 'TIPO_RECURSO_SGR';
                            
              MI_CAMPOS := 'COMPANIA
                              ,ANO
                              ,CODIGO
                              ,NOMBRE
                              ,CREATED_BY
                              ,DATE_CREATED';
            
                  MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                              ,'''|| UN_USUARIO ||'''
                              ,SYSDATE';
                                 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);
  
      

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ENTESCONTROL;
          END;
          
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESCONTROL THEN
             MI_MSGERROR(1).CLAVE := 'CODIGO';
             MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
             MI_MSGERROR(2).CLAVE := 'ANIO';
             MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(1);
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_REEMPLAZOS => MI_MSGERROR,
                  UN_ERROR_COD => PCK_ERRORES.ERR_CARGAR_TIPO_RECURSOS
                );
        END;

  END LOOP CARGAR_PMR_FUENTE;
END PR_CARGAR_TIPO_RECURSO; 

FUNCTION FC_ACTCLASIFICADORESPPTALCUIPO
     /*  
        NAME              : FC_ACTCLASIFICADORESPPTALCUIPO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
        DATE MIGRADOR     : 13/04/2021
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite actualizar los clasificares en la detalle presupuestal  desde la  PLAN_PPTAL_CONFIG
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:FC_ACTCLASIFICADORESPPTALCUIPO

    */    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER AS
    MI_SALIDA VARCHAR2(100);
    MI_EGR               PCK_SUBTIPOS.TI_DOBLE             := 0 ;
    MI_SECTOR            PCK_SUBTIPOS.TI_SECTOR            := '';
    MI_PROGRAMA          PCK_SUBTIPOS.TI_PROGRAMA          := '';
    MI_SUBPROGRAMA       PCK_SUBTIPOS.TI_SUBPROGRAMA       := '';
    MI_COD_PROD_CUIPO    PCK_SUBTIPOS.TI_COD_PROD_CUIPO    := '';
    MI_CODIGO_BPIN        PCK_SUBTIPOS.TI_CODIGO_BPIN      := '';
    MI_CODIGO_CCPET       PCK_SUBTIPOS.TI_CODIGO_CCPET       := '';
    MI_CODIGO_CPC        PCK_SUBTIPOS.TI_CODIGO_CPC     := '';
    MI_CODIGOUNIDADEJE   PCK_SUBTIPOS.TI_CODIGOUNIDADEJE   := '';
    MI_FUENTE_CUIPO      PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
    MI_FUENTE_CUIPOCUIPO PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
    MI_CODIGOCCPETREGA   PCK_SUBTIPOS.TI_CODIGOCCPETREGA   := '';

    MI_REGALIAS               PCK_SUBTIPOS.TI_DOBLE        := 0;

    MI_PARAMETRO        VARCHAR2(4000 CHAR);
    MI_DATOS_FILA       PCK_SYSMAN_UTL.T_SPLIT;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CONDICIONCNULL   PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    MI_PARAMETRO     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA
                                                , UN_NOMBRE =>'HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO'
                                                , UN_MODULO =>PCK_DATOS.MODULOPRESUPUESTO
                                                , UN_FECHA_PAR=>SYSDATE ), 'NO');


    --CLASECLASIFICADOR -> APLICACION 0;No Aplica;1;Rubro;2;Detall
    
    <<VERIFICACUENTA>>
    FOR RS_VERIF_CUENTAS IN 
    (
        SELECT CODIGO
              ,CODIGO_CCEPT
              ,FUENTE_CUIPO
              ,SECCION_PPTAL_CUIPO
              ,SECTOR_CUIPO
              ,PROGRAMA_CUIPO
              ,CODIGO_PRODUCTO_CUIPO
              ,CODIGO_CPC_CUIPO
              ,CODIGO_BPIN_SGR
        FROM PLAN_PPTAL_CONFIG PP 
        WHERE PP.COMPANIA = UN_COMPANIA
          AND PP.ANO  = UN_ANIO
          --AND PP.TIPOVIGENCIA IN('VA')
          --and codigo  =  '2.1.2.02.02.008.23'
        ORDER BY CODIGO
    )
    LOOP
        MI_SECTOR          := '';
        MI_PROGRAMA        := '';
        MI_COD_PROD_CUIPO  := '';
        MI_CODIGO_BPIN      := ''; 
        MI_CODIGO_CCPET     := ''; --0  REGALIAS
        MI_CODIGO_CPC   := '';
        MI_CODIGOUNIDADEJE := '';
        MI_FUENTE_CUIPO    := '';
        MI_CODIGOCCPETREGA := '';  --NOT IN 0
        BEGIN
           MI_REGALIAS := 0;
           SELECT REGALIAS 
           INTO MI_REGALIAS
           FROM PLAN_PRESUPUESTAL  PP
           WHERE PP.COMPANIA = UN_COMPANIA
             AND PP.ANO      = UN_ANIO
             AND PP.CODIGO   = RS_VERIF_CUENTAS.CODIGO;
       EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_REGALIAS := 0;                
       END;
        
        MI_SECTOR            := RS_VERIF_CUENTAS.SECTOR_CUIPO; 
        MI_PROGRAMA          := RS_VERIF_CUENTAS.PROGRAMA_CUIPO;
        MI_COD_PROD_CUIPO    := RS_VERIF_CUENTAS.CODIGO_PRODUCTO_CUIPO;
        MI_CODIGO_CCPET       := RS_VERIF_CUENTAS.CODIGO_CCEPT;
        MI_CODIGO_CPC     := RS_VERIF_CUENTAS.CODIGO_CPC_CUIPO;
        MI_CODIGOUNIDADEJE   := RS_VERIF_CUENTAS.SECCION_PPTAL_CUIPO;
        MI_FUENTE_CUIPO      := RS_VERIF_CUENTAS.FUENTE_CUIPO;
        MI_CODIGO_BPIN       := RS_VERIF_CUENTAS.CODIGO_BPIN_SGR;
        MI_CODIGOCCPETREGA   := RS_VERIF_CUENTAS.CODIGO_CCEPT;
        
        MI_CAMPOS :=  '';
        MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA             || '''
                     AND ANO         =   ' || UN_ANIO                 || '
                     AND CUENTA      = ''' || RS_VERIF_CUENTAS.CODIGO || '''
                     ';
        IF  MI_SECTOR IS NOT NULL THEN
          MI_CAMPOS := MI_CAMPOS || ' SECTOR           = ''' || MI_SECTOR          ||''' ' ;
          MI_CONDICIONCNULL := MI_CONDICION || ' AND SECTOR IS NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);      
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_PROGRAMA IS NOT NULL THEN 
          MI_CAMPOS := MI_CAMPOS || ' PROGRAMA         = ''' || MI_PROGRAMA        ||''' ' ;                 
          MI_CONDICIONCNULL := MI_CONDICION || ' AND PROGRAMA IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_COD_PROD_CUIPO IS NOT NULL THEN 
          MI_CAMPOS := MI_CAMPOS || ' COD_PROD_CUIPO   = ''' || MI_COD_PROD_CUIPO  ||''' ' ;                     
          MI_CONDICIONCNULL := MI_CONDICION || ' AND COD_PROD_CUIPO IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_CODIGO_BPIN IS NOT NULL THEN 
          MI_CAMPOS := MI_CAMPOS || ' CODIGO_BPIN       = ''' || MI_CODIGO_BPIN      ||''' ' ;                    
          MI_CONDICIONCNULL := MI_CONDICION || ' AND CODIGO_BPIN IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_CODIGO_CCPET IS NOT NULL  AND MI_REGALIAS  =  0 THEN 
          MI_CAMPOS := MI_CAMPOS || ' CODIGO_CCPET      = ''' || MI_CODIGO_CCPET     ||''' ' ;                    
          MI_CONDICIONCNULL := MI_CONDICION || ' AND CODIGO_CCPET IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_CODIGO_CPC IS NOT NULL THEN 
          MI_CAMPOS := MI_CAMPOS || ' CODIGO_CPC    = ''' || MI_CODIGO_CPC   ||''' ' ;                      
          MI_CONDICIONCNULL := MI_CONDICION || ' AND CODIGO_CPC IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_CODIGOUNIDADEJE IS NOT NULL  THEN 
          MI_CAMPOS := MI_CAMPOS || ' CODIGOUNIDADEJE  = ''' || MI_CODIGOUNIDADEJE ||''' ' ;                      
          MI_CONDICIONCNULL := MI_CONDICION || ' AND CODIGOUNIDADEJE IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF  MI_FUENTE_CUIPO IS NOT NULL THEN 
          MI_CAMPOS := MI_CAMPOS || ' FUENTE_CUIPO     = ''' || MI_FUENTE_CUIPO    ||''' ' ;                
          MI_CONDICIONCNULL := MI_CONDICION || ' AND FUENTE_CUIPO IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        MI_CAMPOS :=  '';
        IF   MI_CODIGOCCPETREGA IS NOT NULL AND MI_REGALIAS NOT IN(0) THEN 
          MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPETREGA  = ''' || MI_CODIGOCCPETREGA ||''' ' ;  
          MI_CONDICIONCNULL := MI_CONDICION || ' AND CODIGOCCPETREGA IS  NULL ' ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICIONCNULL);
        END IF;
        
    END LOOP VERIFICACUENTA;
    RETURN -1;
END FC_ACTCLASIFICADORESPPTALCUIPO ;

PROCEDURE PR_CARGAR_TIPO_CLASIFI_DETALLE
/*
    NAME              : PR_CARGAR_TIPO_CLASIFI_DETALLE
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 26/06/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA TIPOS CLASIFICADORES DETALLE DE UN EXCEL

    @NAME:    PR_CARGAR_TIPO_CLASIFI_DETALLE
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE             NUMBER := 0;
MI_EXISTENDATOS       NUMBER := 0;

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_TIPOCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
       --INSERTA TIPO_CLASIFICADORES                                              

      MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,CLASECLASIFICADOR
                  ,CODIGO
                  ,NOMBRE
                  ,CREATED_BY
                  ,DATE_CREATED';

      MI_VALORES := ''''|| UN_COMPANIA ||'''
                    ,'|| MI_DATOS_COLUMNAS(1) ||'
                  ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
                  
    BEGIN
        SELECT COUNT(0) CLASE 
          INTO MI_EXISTENDATOS
          FROM CLASECLASIFICADOR 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = MI_DATOS_COLUMNAS(1) 
           AND CODIGO = MI_DATOS_COLUMNAS(5) 
           AND (APLICACION = 2 OR APLICACIONINGRESOS = 2);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTENDATOS := 0;
    END;

    BEGIN
        IF MI_EXISTENDATOS = 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END IF;
    END;

        BEGIN
           SELECT COUNT('X') EXISTE
           INTO MI_EXISTE 
           FROM TIPOCLASIFICADOR
           WHERE COMPANIA = UN_COMPANIA
              AND ANO  =  MI_DATOS_COLUMNAS(1)
              AND CLASECLASIFICADOR = MI_DATOS_COLUMNAS(5)
              AND CODIGO = MI_DATOS_COLUMNAS(2);
        EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_EXISTE :=0 ;
        END;   
        
       IF   MI_EXISTE = 0 THEN 
         BEGIN
             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TIPOCLASIFICADOR'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
         END;
      END IF;

  END LOOP CREAR_TIPOCLASIFICADORES;
END PR_CARGAR_TIPO_CLASIFI_DETALLE;


FUNCTION PR_ACT_CLA_ARBOL
/*
    NAME              : PR_ACT_CLA_ARBOL
    AUTHOR MIGRACION  : CAMILO ANDRÉS PEREZ DUEÑAS
    DATE MIGRADOR     : 15/07/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : LUIS JACOBO DIAZ M
    DATE MODIFIED     : 06/09/
    TIME              :
    MODIFICATIONS     : SE CRRIGE LA LOGICA DE LA ACTUALIZACIO DE LA INFORMACION.
    DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS  TIPOS CLASIFICADORES DE UN EXCEL

    @NAME:    PR_ACTUALIZAR_CLASIFICADORES_ARBOL
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE             NUMBER := 0;
MI_APLICACION         NUMBER := 0;
MI_APLICACIONINGRESOS NUMBER := 0;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_RETORNO                   CLOB := '';
MI_RTA                      VARCHAR2(10); 
MI_NUMERO_COD               VARCHAR(1000);

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_TIPOCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
        
       --ACTUALIZA TIPO_CLASIFICADORES  EN PLAN_PRESUPUESTAL 
       BEGIN
            IF(MI_DATOS_COLUMNAS(5) IS NOT NULL AND MI_DATOS_COLUMNAS(6) IS NOT NULL) THEN
               -- BUSCAMOS EL CLASIFICADOR
               BEGIN
                   SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE 
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  MI_DATOS_COLUMNAS(2)
                      AND CLASECLASIFICADOR = MI_DATOS_COLUMNAS(5)
                      AND CODIGO = MI_DATOS_COLUMNAS(6);
               EXCEPTION WHEN NO_DATA_FOUND THEN
                     MI_EXISTE :=0 ;
               END;
               IF (MI_EXISTE IN (0)) THEN
                    BEGIN
                         SELECT TRIM(TO_CHAR(MI_DATOS_COLUMNAS(6),99999999999999999999)) INTO MI_NUMERO_COD FROM DUAL;
                     EXCEPTION WHEN INVALID_NUMBER THEN
                         MI_NUMERO_COD := MI_DATOS_COLUMNAS(6) ;
                     END; 
                     BEGIN
                               SELECT COUNT('X') EXISTE
                               INTO MI_EXISTE 
                               FROM TIPOCLASIFICADOR
                               WHERE COMPANIA = UN_COMPANIA
                                  AND ANO  =  MI_DATOS_COLUMNAS(2)
                                  AND CLASECLASIFICADOR = MI_DATOS_COLUMNAS(5)
                                  AND TRIM(TO_CHAR(CODIGO,99999999999999999999)) = MI_NUMERO_COD;
                           EXCEPTION WHEN NO_DATA_FOUND OR INVALID_NUMBER THEN
                                 SELECT COUNT('X') EXISTE
                               INTO MI_EXISTE 
                               FROM TIPOCLASIFICADOR
                               WHERE COMPANIA = UN_COMPANIA
                                  AND ANO  =  MI_DATOS_COLUMNAS(2)
                                  AND CLASECLASIFICADOR = MI_DATOS_COLUMNAS(5)
                                  AND CODIGO = MI_NUMERO_COD;
                      END;
                ELSE
                    MI_NUMERO_COD := MI_DATOS_COLUMNAS(6);
               END IF;
               
               IF (MI_EXISTE NOT IN (0)) THEN
                    MI_CAMPOS :='CLASECLASIFICADOR  =  ''' || TRIM(MI_DATOS_COLUMNAS(5)) || '''
                                ,TIPOCLASIFICADOR   =  ''' || MI_NUMERO_COD || '''
                                ,MODIFIED_BY        =  ''' || UN_USUARIO           || '''
                                ,DATE_MODIFIED      =          SYSDATE'; 
               ELSE
                   MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' Clasificador tipo '||MI_DATOS_COLUMNAS(5)||' relacionado al codigo '||MI_DATOS_COLUMNAS(6)||' No existe. '||CHR(13); 
               END IF;
            END IF;
       EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CAMPOS := '';      
       END;
             
       IF(MI_CAMPOS IS NOT NULL) THEN
           MI_APLICACION := PCK_SYSMAN_UTL.FC_APLICACIONCUENTA(MI_DATOS_COLUMNAS(1), MI_DATOS_COLUMNAS(2), MI_DATOS_COLUMNAS(5), MI_DATOS_COLUMNAS(3));
           IF (MI_APLICACION = 1) THEN --) OR (NOT MI_APLICACIONINGRESOS = 1) THEN 
             BEGIN
                   MI_CONDICION := '   COMPANIA      =  ''' || MI_DATOS_COLUMNAS(1) || '''
                           AND ANO           =    ' || MI_DATOS_COLUMNAS(2) || '
                           AND CODIGO        =  ''' || MI_DATOS_COLUMNAS(3) || '''';
    
                           --AND (CASE WHEN NATURALEZA = ''D'' THEN ' || MI_APLICACION || ' ELSE ' || MI_APLICACIONINGRESOS || ' END) = 2';

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PRESUPUESTAL'
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);
                    COMMIT;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    ROLLBACK;
                    RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
             END;
          ELSE
            MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El clasificador '||MI_DATOS_COLUMNAS(5)||' no se inserto, no se encuentra configurado como rubo. '||CHR(13);
          END IF;
             MI_CAMPOS := '';
        END IF;
  END LOOP CREAR_TIPOCLASIFICADORES;
RETURN MI_RETORNO;
END PR_ACT_CLA_ARBOL;

FUNCTION FC_ACT_CLASIFICADOR_DETALLE
/*
    NAME              : FC_ACT_CLASIFICADOR_DETALLE
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
    DATE MIGRADOR     : 06/09/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 14/09/2023
    TIME              : 8:00 AM
    MODIFICATIONS     : Se incluye el clasificador RECURSO_SGR
    DESCRIPTION       : FUNCION QUE ACTUALIZA LOS CLASIFICADORES DETALLE DE UN EXCEL

    @NAME:    FC_ACT_CLASIFICADOR_DETALLE
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB 
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_EXISTE             NUMBER := 0;
MI_EXISTENDATOS       NUMBER := 0;

MI_EGR               PCK_SUBTIPOS.TI_DOBLE             := 0 ;
MI_SECTOR            PCK_SUBTIPOS.TI_SECTOR            := '';
MI_PROGRAMA          PCK_SUBTIPOS.TI_PROGRAMA          := '';
MI_SUBPROGRAMA       PCK_SUBTIPOS.TI_SUBPROGRAMA       := '';
MI_COD_PROD_CUIPO    PCK_SUBTIPOS.TI_COD_PROD_CUIPO    := '';
MI_CODIGO_BPIN       PCK_SUBTIPOS.TI_CODIGO_BPIN        := '';
MI_CODIGO_CCPET      PCK_SUBTIPOS.TI_CODIGO_CCPET       := '';
MI_CODIGO_CPC        PCK_SUBTIPOS.TI_CODIGO_CPC     := '';
MI_CODIGOUNIDADEJE   PCK_SUBTIPOS.TI_CODIGOUNIDADEJE   := '';
MI_FUENTE_CUIPO      PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
MI_FUENTE_CUIPOCUIPO PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
MI_CODIGOCCPETREGA   PCK_SUBTIPOS.TI_CODIGOCCPETREGA   := '';
MI_POLITICA_PUB      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;
MI_DETALLE_SEC       DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;
MI_NATURALEZA        PLAN_PRESUPUESTAL.NATURALEZA%TYPE;
MI_ANO               PLAN_PRESUPUESTAL.ANO%TYPE;
MI_CUENTA            DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
MI_COD_SECTOR                DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE; 
MI_COD_SECTORCOMP            DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE;         
MI_COD_PROGRAMA              DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;   
MI_COD_PROGRAMACOMP          DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;         
MI_COD_SUBPROGRAMA           DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;
MI_COD_SUBPROGRAMACOMP       DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;         
MI_COD_CODIGOUNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
MI_COD_CODIGOUNIDADEJECOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;         
MI_COD_CODIGOCCPETREGA       DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGOCCPETREGACOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGO_CCPET          DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_CODIGO_CCPETCOMP      DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_SITUACION_FONDOS      DETALLE_COMPROBANTE_PPTAL.SITUACION_FONDOS%TYPE;         
MI_COD_POLITICA_PUBLICA      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_POLITICA_PUBLICACOMP  DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_DETALLE_SECTORIAL     DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_DETALLE_SECTORIALCOMP DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_RECURSO_SGR           DETALLE_COMPROBANTE_PPTAL.RECURSO_SGR%TYPE;   --7735611 MPEREZ  
MI_COD_RECURSO_SGRCOMP       DETALLE_COMPROBANTE_PPTAL.RECURSO_SGR%TYPE;   --7735611 MPEREZ 
MI_COD_COD_PROD_CUIPO        DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO%TYPE;         
MI_COD_CODIGO_CPC            DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_CPCCOMP        DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_BPIN           DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE;
MI_COD_CODIGO_BPINCOMP       DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE; 
MI_COD_FUENTE_CUIPO          DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
MI_COD_FUENTE_CUIPOCOMP      DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;  
MI_COD_TIPO_CPTE             DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE;  
MI_COD_COMPROBANTE           DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;  
MI_COD_CONSECUTIVO           DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO%TYPE;
MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;  
MI_COD_FECHA                 DATE;
MI_RETORNO                   CLOB := '';
MI_RTA                      VARCHAR2(10); 
 
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
  <<ACTUALIZARCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
    MI_COD_FECHA := TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY');
    MI_ANO                      := EXTRACT(YEAR FROM MI_COD_FECHA);
    MI_CUENTA                   := MI_DATOS_COLUMNAS(8);
    MI_COD_TIPO_CPTE            := MI_DATOS_COLUMNAS(1);
    MI_COD_COMPROBANTE          := MI_DATOS_COLUMNAS(2);
    MI_COD_CONSECUTIVO          := MI_DATOS_COLUMNAS(3);
    MI_COD_SECTOR               := MI_DATOS_COLUMNAS(16);
    MI_COD_PROGRAMA             := MI_DATOS_COLUMNAS(17);
    MI_COD_SUBPROGRAMA          := MI_DATOS_COLUMNAS(18);
    MI_COD_COD_PROD_CUIPO       := MI_DATOS_COLUMNAS(19);
    MI_COD_CODIGO_BPIN          := MI_DATOS_COLUMNAS(20);
    MI_COD_CODIGO_CCPET         := MI_DATOS_COLUMNAS(21);
    MI_COD_CODIGO_CPC           := MI_DATOS_COLUMNAS(22);
    MI_COD_CODIGOUNIDADEJE      := MI_DATOS_COLUMNAS(23);
    MI_COD_FUENTE_CUIPO         := MI_DATOS_COLUMNAS(24);
    MI_COD_CODIGOCCPETREGA      := MI_DATOS_COLUMNAS(25);
    MI_COD_POLITICA_PUBLICA     := MI_DATOS_COLUMNAS(26);
    MI_COD_DETALLE_SECTORIAL    := MI_DATOS_COLUMNAS(27);
	MI_COD_RECURSO_SGR          := MI_DATOS_COLUMNAS(28);
	

    SELECT NATURALEZA
    INTO MI_NATURALEZA
    FROM PLAN_PRESUPUESTAL P
    WHERE  P.COMPANIA = UN_COMPANIA
       AND P.ANO      = MI_ANO
       AND P.CODIGO   = MI_CUENTA;

    BEGIN
        SELECT   H.IDPADRE 
        INTO     MI_COD_PROGRAMACOMP 
        FROM     TIPOCLASIFICADOR T     INNER JOIN TIPOCLASIFICADORESHIJO H 
          ON T.COMPANIA = H.COMPANIA                                            
          AND T.ANO = H.ANO                                            
          AND T.ID = H.IDPADRE 
        INNER JOIN TIPOCLASIFICADOR TCH                                         
          ON H.COMPANIA =  TCH.COMPANIA                                        
          AND H.ANO =  TCH.ANO                                         
          AND H.IDHIJO =  TCH.ID  
        WHERE     T.COMPANIA = UN_COMPANIA     AND T.ANO = MI_ANO     
          AND INSTR(          CONCAT(CONCAT(',',(
              SELECT     CLASEPADRE FROM     CLASECLASIFICADOR WHERE     COMPANIA = UN_COMPANIA     AND ANO = MI_ANO     AND CODIGO = '004'     AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
          AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0    
          AND T.CODIGO IS NOT NULL
          AND TCH.CODIGO =  MI_COD_COD_PROD_CUIPO
        GROUP BY H.IDPADRE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_COD_PROGRAMACOMP := '';                
    END;


    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_SUBPROGRAMACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '003'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_SUBPROGRAMA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_SUBPROGRAMACOMP := '';                
    END;
 


    /*
    BEGIN
        SELECT     H.IDPADRE 
        INTO      MI_COD_SECTORCOMP
        FROM     TIPOCLASIFICADOR T     
        INNER JOIN TIPOCLASIFICADORESHIJO H 
            ON T.COMPANIA = H.COMPANIA                                            
            AND T.ANO = H.ANO                                            
            AND T.ID = H.IDPADRE 
        INNER JOIN TIPOCLASIFICADOR TCH                                         
            ON H.COMPANIA =  TCH.COMPANIA                                        
            AND H.ANO =  TCH.ANO                                         
            AND H.IDHIJO =  TCH.ID  
        WHERE     T.COMPANIA = UN_COMPANIA     
            AND T.ANO = MI_ANO     
            AND INSTR(          CONCAT(CONCAT(',',(
               SELECT     CLASEPADRE FROM     CLASECLASIFICADOR 
               WHERE     COMPANIA = UN_COMPANIA   
                AND ANO = MI_ANO     
                AND CODIGO = '002'     
                AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
                AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0      
            AND H.IDHIJO =  MI_COD_PROGRAMACOMP     
            AND T.CODIGO IS NOT NULL
        GROUP BY H.IDPADRE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_SECTORCOMP := '';                
    END;   
    SE COMENTA EL CODIGO ANTERIOR PARA POBRAR UNA OPCION NUEVA */
    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_SECTORCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '001'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_SECTOR
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_SECTORCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_BPINCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '005'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_BPIN
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_CODIGO_BPINCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_CCPETCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '006'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_CCPET
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_CODIGO_CCPETCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_CPCCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '007'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_CPC
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_CODIGO_CPCCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGOUNIDADEJECOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '008'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGOUNIDADEJE
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_CODIGOUNIDADEJECOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_FUENTE_CUIPOCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '009'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_FUENTE_CUIPO
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_FUENTE_CUIPOCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGOCCPETREGACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '010'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGOCCPETREGA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_CODIGOCCPETREGACOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_POLITICA_PUBLICACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '011'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_POLITICA_PUBLICA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_POLITICA_PUBLICACOMP := '';                
    END;
     
    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_DETALLE_SECTORIALCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '012'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_DETALLE_SECTORIAL
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_DETALLE_SECTORIALCOMP := '';                
    END;
	
    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_RECURSO_SGRCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANO     
            AND CC.CODIGO = '013'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_RECURSO_SGR
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_COD_RECURSO_SGRCOMP := '';                
    END;
 --CLASECLASIFICADOR -> APLICACION 0;No Aplica;1;Rubro;2;Detall

    MI_CAMPOS :=  '';
    --IF '001' || MI_COD_SECTOR = MI_COD_SECTORCOMP  AND  MI_COD_SECTOR IS NOT NULL THEN -- SE COMENTA PARA NUEVA SOLUCION
    IF MI_COD_SECTOR = MI_COD_SECTORCOMP  AND  MI_COD_SECTOR IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' SECTOR             = ''' || MI_COD_SECTOR          ||''',' ;
    ELSIF MI_COD_SECTOR = 'NoDato' OR MI_COD_SECTOR IS NULL OR MI_COD_SECTOR = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Sector para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 001 No esta configurado como detalle  o el codigo ' || MI_COD_SECTOR || ' No existe  en TIPOCLASIFICADOR '||CHR(13);
    END IF;
    IF '002' || MI_COD_PROGRAMA =  MI_COD_PROGRAMACOMP  AND  MI_COD_PROGRAMA IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' PROGRAMA           = ''' || MI_COD_PROGRAMA        ||''',' ;  
    ELSIF MI_COD_PROGRAMA = 'NoDato' OR MI_COD_PROGRAMA IS NULL OR MI_COD_PROGRAMA = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Programa para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 002 No esta configurado como detalle  o el codigo ' || MI_COD_PROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_COD_SUBPROGRAMA =  MI_COD_SUBPROGRAMACOMP AND  MI_COD_SUBPROGRAMA IS NOT NULL THEN         
      MI_CAMPOS := MI_CAMPOS || ' SUBPROGRAMA        = ''' || MI_COD_SUBPROGRAMA     ||''',' ;       
    ELSIF MI_COD_SUBPROGRAMA = 'NoDato' OR MI_COD_SUBPROGRAMA IS NULL OR MI_COD_SUBPROGRAMA = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador SubPrograma para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 003 No esta configurado como detalle  o el codigo ' || MI_COD_SUBPROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
    END IF;
    IF  MI_COD_PROGRAMACOMP  IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' COD_PROD_CUIPO     = ''' || MI_COD_COD_PROD_CUIPO  ||''',' ;   
    ELSIF MI_COD_COD_PROD_CUIPO = 'NoDato' OR MI_COD_COD_PROD_CUIPO IS NULL OR MI_COD_COD_PROD_CUIPO = 'null' THEN
       MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Producto para la cuenta: ' || MI_CUENTA||CHR(13);  
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 004 No esta configurado como detalle  o el codigo ' || MI_COD_COD_PROD_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                 
    END IF;
    IF MI_COD_CODIGO_BPIN =  MI_COD_CODIGO_BPINCOMP  AND  MI_COD_CODIGO_BPIN IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' CODIGO_BPIN        = ''' || MI_COD_CODIGO_BPIN      ||''',' ; 
    ELSIF MI_COD_CODIGO_BPIN = 'NoDato' OR MI_COD_CODIGO_BPIN IS NULL OR MI_COD_CODIGO_BPIN = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Bpin para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 005 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_BPIN || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                  
    END IF;
    IF MI_COD_CODIGO_CCPET =  MI_COD_CODIGO_CCPETCOMP  AND  MI_COD_CODIGO_CCPET IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' CODIGO_CCPET       = ''' || MI_COD_CODIGO_CCPET     ||''',' ;   
    ELSIF MI_COD_CODIGO_CCPET = 'NoDato' OR MI_COD_CODIGO_CCPET IS NULL OR MI_COD_CODIGO_CCPET = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Codigo CCPET para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 006 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CCPET || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                
    END IF;
    IF MI_COD_CODIGO_CPC =  MI_COD_CODIGO_CPCCOMP  AND  MI_COD_CODIGO_CPC IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' CODIGO_CPC         = ''' || MI_COD_CODIGO_CPC   ||''',' ;       
    ELSIF MI_COD_CODIGO_CPC = 'NoDato' OR MI_COD_CODIGO_CPC IS NULL OR MI_COD_CODIGO_CPC = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Codigo CPC para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 007 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CPC || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
    END IF;
    IF MI_COD_CODIGOUNIDADEJE =  MI_COD_CODIGOUNIDADEJECOMP  AND  MI_COD_CODIGOUNIDADEJE IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' CODIGOUNIDADEJE    = ''' || MI_COD_CODIGOUNIDADEJE ||''',' ; 
    ELSIF MI_COD_CODIGOUNIDADEJE = 'NoDato' OR MI_COD_CODIGOUNIDADEJE IS NULL OR MI_COD_CODIGOUNIDADEJE = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Unidad Ejecutora para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 008 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOUNIDADEJE || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                    
    END IF;
    IF MI_COD_FUENTE_CUIPO =  MI_COD_FUENTE_CUIPOCOMP  AND  MI_COD_FUENTE_CUIPO IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' FUENTE_CUIPO       = ''' || MI_COD_FUENTE_CUIPO    ||''',' ;  
    ELSIF MI_COD_FUENTE_CUIPO = 'NoDato' OR MI_COD_FUENTE_CUIPO IS NULL OR MI_COD_FUENTE_CUIPO = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Fuente para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 009 No esta configurado como detalle  o el codigo ' || MI_COD_FUENTE_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                             
    END IF;
    IF  MI_COD_CODIGOCCPETREGA =  MI_COD_CODIGOCCPETREGACOMP  AND  MI_COD_CODIGOCCPETREGA IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPETREGA    = ''' || MI_COD_CODIGOCCPETREGA ||''',' ;  
    ELSIF MI_COD_CODIGOCCPETREGA = 'NoDato' OR MI_COD_CODIGOCCPETREGA IS NULL OR MI_COD_CODIGOCCPETREGA = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Codigo CCPET Regalias para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE 
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 010 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOCCPETREGA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_COD_POLITICA_PUBLICA =  MI_COD_POLITICA_PUBLICACOMP AND  MI_COD_POLITICA_PUBLICA IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' POLITICA_PUBLICA   = ''' || MI_COD_POLITICA_PUBLICA    ||''',' ;
    ELSIF MI_COD_POLITICA_PUBLICA = 'NoDato' OR MI_COD_POLITICA_PUBLICA IS NULL OR MI_COD_POLITICA_PUBLICA = 'null'THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador Politica Publica para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 011 No esta configurado como detalle  o el codigo ' || MI_COD_POLITICA_PUBLICA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                               
    END IF;
    IF  MI_COD_DETALLE_SECTORIAL =  MI_COD_DETALLE_SECTORIALCOMP AND  MI_COD_DETALLE_SECTORIAL IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' DETALLE_SECTORIAL  = ''' || MI_COD_DETALLE_SECTORIAL ||''',' ;  
    ELSIF MI_COD_DETALLE_SECTORIAL = 'NoDato' OR MI_COD_DETALLE_SECTORIAL IS NULL OR MI_COD_DETALLE_SECTORIAL = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador detalle Sectorial para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 012 No esta configurado como detalle  o el codigo ' || MI_COD_DETALLE_SECTORIAL || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
	IF  MI_COD_RECURSO_SGR =  MI_COD_RECURSO_SGRCOMP AND  MI_COD_RECURSO_SGR IS NOT NULL THEN 
      MI_CAMPOS := MI_CAMPOS || ' RECURSO_SGR  = ''' || MI_COD_RECURSO_SGR ||''',' ;  
    ELSIF MI_COD_RECURSO_SGR = 'NoDato' OR MI_COD_RECURSO_SGR IS NULL OR MI_COD_RECURSO_SGR = 'null' THEN
      MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador tipo de recurso SGR para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El tipo' || ' 013 No esta configurado como detalle  o el codigo ' || MI_COD_RECURSO_SGR || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_RETORNO IS NOT NULL THEN 
     MI_RETORNO :=  MI_RETORNO || CHR(10) || CHR(13);
    END IF;
    MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA             || '''
                 AND ANO               =   ' || MI_ANO                  || '
                 AND TIPO_CPTE         = ''' || MI_COD_TIPO_CPTE        || '''
                 AND COMPROBANTE       = ''' || MI_COD_COMPROBANTE      || '''
                 AND CONSECUTIVO       =   ' || MI_COD_CONSECUTIVO      || '
                 AND CUENTA            = ''' || MI_CUENTA               || ''' ';
    IF  MI_CAMPOS  IS NOT NULL THEN 
        MI_CAMPOS := SUBSTR(MI_CAMPOS,1,LENGTH(MI_CAMPOS) -1 ) ;   
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);      
    END IF;

  END LOOP ACTUALIZARCLASIFICADORES;
 
RETURN MI_RETORNO;
END FC_ACT_CLASIFICADOR_DETALLE;

PROCEDURE PR_CARGARTABLATEMP_ACT
/*
    NAME              : PR_CARGARTABLATEMP_ACT
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÑOZ
    DATE MIGRADOR     : 26/01/2023                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS CONSULTADOS DE LOS TIPOS DEL CLASIFICADOR SELECIONDO EN UNA TABLA TEMPORAL PARA PODER HACER LUEGO ALS ACTUALIZACIONES

    @NAME:    PR_CARGARTABLATEMP_ACT
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_CODIGO     IN VARCHAR2
)AS
BEGIN
DELETE FROM TMPCLASIFICADORESAACTUALIZAR;
INSERT INTO TMPCLASIFICADORESAACTUALIZAR (CODIGO, NOMBRE) SELECT
    CODIGO,
    NOMBRE
FROM
    TIPOCLASIFICADOR
WHERE
    COMPANIA = UN_COMPANIA
    AND ANO = UN_ANO
    AND CLASECLASIFICADOR = UN_CODIGO;
END PR_CARGARTABLATEMP_ACT;

PROCEDURE PR_ACT_TIPOCLASIFICADORES
/*
    NAME              : PR_ACT_TIPOCLASIFICADORES
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÑOZ
    DATE MIGRADOR     : 26/01/2023                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE ACTUALIZA LOS CLASIFICADORES DETALLE DESDE LA PESTAÑA DE ACTUALIZAR CLASIFICADORES

    @NAME:    PR_ACT_TIPOCLASIFICADORES
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_CODIGO      IN VARCHAR2
)AS
MI_CODIGO VARCHAR2(255);
MI_CODIGO_NUEVO VARCHAR2(255);
MI_VALIDA_DETALLE VARCHAR2(1);
MI_APLICACION VARCHAR2(1);
MI_APLICACION_INGRESOS VARCHAR2(1);
MI_TABLA                 VARCHAR2(200);
MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
MI_RTA                   VARCHAR2(32000);
BEGIN
    SELECT APLICACION INTO MI_VALIDA_DETALLE FROM CLASECLASIFICADOR WHERE COMPANIA = UN_COMPANIA AND ANO = UN_ANO AND CODIGO = UN_CODIGO;
    FOR REG IN (SELECT CODIGO, CODIGONUEVO INTO MI_CODIGO,MI_CODIGO_NUEVO FROM TMPCLASIFICADORESAACTUALIZAR WHERE CODIGONUEVO IS NOT NULL) LOOP
        IF(UN_CODIGO = '001' AND MI_VALIDA_DETALLE = '2') THEN -- SECTOR
            UPDATE DETALLE_COMPROBANTE_PPTAL SET SECTOR = REG.CODIGONUEVO WHERE SECTOR = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '002' AND MI_VALIDA_DETALLE = '2') THEN -- PROGRAMA
            UPDATE DETALLE_COMPROBANTE_PPTAL SET PROGRAMA = REG.CODIGONUEVO WHERE PROGRAMA = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '003' AND MI_VALIDA_DETALLE = '2') THEN -- SUB PROGRAMA
            UPDATE DETALLE_COMPROBANTE_PPTAL SET SUBPROGRAMA = REG.CODIGONUEVO WHERE SUBPROGRAMA = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '004' AND MI_VALIDA_DETALLE = '2') THEN -- PRODUCTO
            UPDATE DETALLE_COMPROBANTE_PPTAL SET COD_PROD_CUIPO = REG.CODIGONUEVO WHERE COD_PROD_CUIPO = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '005' AND MI_VALIDA_DETALLE = '2') THEN -- BPIN
            UPDATE DETALLE_COMPROBANTE_PPTAL SET CODIGO_BPIN = REG.CODIGONUEVO WHERE CODIGO_BPIN = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '006' AND MI_VALIDA_DETALLE = '2') THEN -- CCPET
            UPDATE DETALLE_COMPROBANTE_PPTAL SET CODIGO_CCPET = REG.CODIGONUEVO WHERE CODIGO_CCPET = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '007' AND MI_VALIDA_DETALLE = '2') THEN -- CPC DANE
            UPDATE DETALLE_COMPROBANTE_PPTAL SET CODIGO_CPC = REG.CODIGONUEVO WHERE CODIGO_CPC = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '008' AND MI_VALIDA_DETALLE = '2') THEN -- UNIDAD EJECUTORA
            UPDATE DETALLE_COMPROBANTE_PPTAL SET CODIGOUNIDADEJE = REG.CODIGONUEVO WHERE CODIGOUNIDADEJE = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '009' AND MI_VALIDA_DETALLE = '2') THEN -- FUENTE
            UPDATE DETALLE_COMPROBANTE_PPTAL SET FUENTE_CUIPO = REG.CODIGONUEVO WHERE FUENTE_CUIPO = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '010' AND MI_VALIDA_DETALLE = '2') THEN --CCPET REGLALIAS
            UPDATE DETALLE_COMPROBANTE_PPTAL SET CODIGOCCPETREGA = REG.CODIGONUEVO WHERE CODIGOCCPETREGA = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '011' AND MI_VALIDA_DETALLE = '2') THEN -- POLITICA PUBLICA
            UPDATE DETALLE_COMPROBANTE_PPTAL SET POLITICA_PUBLICA = REG.CODIGONUEVO WHERE POLITICA_PUBLICA = REG.CODIGO;
        END IF;
        IF(UN_CODIGO = '012' AND MI_VALIDA_DETALLE = '2') THEN -- DETALLE SECTORIAL
            UPDATE DETALLE_COMPROBANTE_PPTAL SET DETALLE_SECTORIAL = REG.CODIGONUEVO WHERE DETALLE_SECTORIAL = REG.CODIGO;
        END IF;
        
        SELECT APLICACION, APLICACIONINGRESOS INTO MI_APLICACION, MI_APLICACION_INGRESOS FROM CLASECLASIFICADOR WHERE COMPANIA = UN_COMPANIA AND ANO = UN_ANO AND CODIGO = UN_CODIGO;

        IF(MI_APLICACION ='1' OR MI_APLICACION_INGRESOS = '1') THEN 
            MI_TABLA     := 'PLAN_PRESUPUESTAL';
    
            MI_CAMPOS    := 'PLAN_PRESUPUESTAL.TIPOCLASIFICADOR = '''||REG.CODIGONUEVO||'''';
            
            MI_CONDICION := 'PLAN_PRESUPUESTAL.COMPANIA = ''' || UN_COMPANIA || '''
                         AND PLAN_PRESUPUESTAL.ANO = ' || UN_ANO ||'
                         AND PLAN_PRESUPUESTAL.TIPOCLASIFICADOR =''' || REG.CODIGO || '''' ;
            
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION);
                                         
            IF(UN_CODIGO = '001') THEN -- SECTOR
                UPDATE PLAN_PRESUPUESTAL SET SECTOR = REG.CODIGONUEVO WHERE SECTOR = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '002') THEN -- PROGRAMA
                UPDATE PLAN_PRESUPUESTAL SET PROGRAMA = REG.CODIGONUEVO WHERE PROGRAMA = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '003') THEN -- SUB PROGRAMA
                UPDATE PLAN_PRESUPUESTAL SET SUBPROGRAMA = REG.CODIGONUEVO WHERE SUBPROGRAMA = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '004') THEN -- PRODUCTO
                UPDATE PLAN_PRESUPUESTAL SET CODIGOPRODUCTO = REG.CODIGONUEVO WHERE CODIGOPRODUCTO = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '005') THEN -- BPIN
                UPDATE PLAN_PRESUPUESTAL SET CODIGOBPIN = REG.CODIGONUEVO WHERE CODIGOBPIN = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '006') THEN -- CCPET
                UPDATE PLAN_PRESUPUESTAL SET CODIGO_CCPET = REG.CODIGONUEVO WHERE CODIGO_CCPET = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '007') THEN -- CPC DANE
                UPDATE PLAN_PRESUPUESTAL SET CODIGOCPCDANE = REG.CODIGONUEVO WHERE CODIGOCPCDANE = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '008') THEN -- UNIDAD EJECUTORA
                UPDATE PLAN_PRESUPUESTAL SET CODIGOUNIDADEJE = REG.CODIGONUEVO WHERE CODIGOUNIDADEJE = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '009') THEN -- FUENTE
                UPDATE PLAN_PRESUPUESTAL SET CODIGOFUENTE = REG.CODIGONUEVO WHERE CODIGOFUENTE = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '010') THEN --CCPET REGLALIAS
                UPDATE PLAN_PRESUPUESTAL SET CODIGOCCPETREGA = REG.CODIGONUEVO WHERE CODIGOCCPETREGA = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '011') THEN -- POLITICA PUBLICA
                UPDATE PLAN_PRESUPUESTAL SET POLITCA_PUBLICA_CUIPO = REG.CODIGONUEVO WHERE POLITCA_PUBLICA_CUIPO = REG.CODIGO;
            END IF;
            IF(UN_CODIGO = '012') THEN -- DETALLE SECTORIAL
                UPDATE PLAN_PRESUPUESTAL SET DETALLE_SECTORIAL = REG.CODIGONUEVO WHERE DETALLE_SECTORIAL = REG.CODIGO;
            END IF;
        END IF;
    END LOOP;
END PR_ACT_TIPOCLASIFICADORES;

FUNCTION FC_ACT_CLASIFICADOR_RUBRO
/* 
    NAME              : FC_ACT_CLASIFICADOR_7RUBRO
    AUTHOR MIGRACION  : MARIA CAMILA ROSERO PAZOS
    DATE MIGRADOR     : 08/08/2023                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE ACTUALIZA LOS CLASIFICADORES RUBRO DE UN EXCEL
    
    @NAME:    FC_ACT_CLASIFICADOR_RUBRO
    @METHOD:  POST 
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_NATURALEZA  IN PCK_SUBTIPOS.TI_NATURALEZA,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB 
AS 
MI_DATOS_FILA                PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS            PCK_SYSMAN_UTL.T_SPLIT;
MI_CAMPOS                    PCK_SUBTIPOS.TI_CAMPOS;
MI_NATURALEZA                PLAN_PRESUPUESTAL.NATURALEZA%TYPE;
MI_CUENTA                    PLAN_PRESUPUESTAL.CODIGO%TYPE;
MI_COD_SECTOR                PLAN_PRESUPUESTAL.SECTOR%TYPE; 
MI_COD_SECTORCOMP            PLAN_PRESUPUESTAL.SECTOR%TYPE; 
MI_COD_SECTORRUB             PLAN_PRESUPUESTAL.SECTOR%TYPE;
MI_COD_PROGRAMA              PLAN_PRESUPUESTAL.PROGRAMA%TYPE;   
MI_COD_PROGRAMACOMP          PLAN_PRESUPUESTAL.PROGRAMA%TYPE;
MI_COD_PROGRAMARUB           PLAN_PRESUPUESTAL.PROGRAMA%TYPE;
MI_COD_SUBPROGRAMA           PLAN_PRESUPUESTAL.SUBPROGRAMA%TYPE;
MI_COD_SUBPROGRAMACOMP       PLAN_PRESUPUESTAL.SUBPROGRAMA%TYPE;  
MI_COD_SUBPROGRAMARUB        PLAN_PRESUPUESTAL.SUBPROGRAMA%TYPE; 
MI_COD_CODIGOUNIDADEJE       PLAN_PRESUPUESTAL.CODIGOUNIDADEJE%TYPE;
MI_COD_CODIGOUNIDADEJECOMP   PLAN_PRESUPUESTAL.CODIGOUNIDADEJE%TYPE; 
MI_COD_CODIGOUNIDADEJERUB    PLAN_PRESUPUESTAL.CODIGOUNIDADEJE%TYPE; 
MI_COD_CODIGOCCPETREGA       PLAN_PRESUPUESTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGOCCPETREGACOMP   PLAN_PRESUPUESTAL.CODIGOCCPETREGA%TYPE; 
MI_COD_CODIGOCCPETREGARUB    PLAN_PRESUPUESTAL.CODIGOCCPETREGA%TYPE;
MI_COD_CODIGO_CCPET          PLAN_PRESUPUESTAL.CODIGO_CCPET%TYPE;         
MI_COD_CODIGO_CCPETCOMP      PLAN_PRESUPUESTAL.CODIGO_CCPET%TYPE;
MI_COD_CODIGO_CCPETRUB       PLAN_PRESUPUESTAL.CODIGO_CCPET%TYPE;
MI_COD_POLITICA_PUBLICA      PLAN_PRESUPUESTAL.POLITCA_PUBLICA_CUIPO%TYPE;         
MI_COD_POLITICA_PUBLICACOMP  PLAN_PRESUPUESTAL.POLITCA_PUBLICA_CUIPO%TYPE;   
MI_COD_POLITICA_PUBLICARUB   PLAN_PRESUPUESTAL.POLITCA_PUBLICA_CUIPO%TYPE;
MI_COD_DETALLE_SECTORIAL     PLAN_PRESUPUESTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_DETALLE_SECTORIALCOMP PLAN_PRESUPUESTAL.DETALLE_SECTORIAL%TYPE;
MI_COD_DETALLE_SECTORIALRUB  PLAN_PRESUPUESTAL.DETALLE_SECTORIAL%TYPE;
MI_COD_COD_PROD_CUIPO        PLAN_PRESUPUESTAL.CODIGOPRODUCTO%TYPE;   
MI_COD_COD_PROD_CUIPOCOMP    PLAN_PRESUPUESTAL.CODIGOPRODUCTO%TYPE;  
MI_COD_COD_PROD_CUIPORUB     PLAN_PRESUPUESTAL.CODIGOPRODUCTO%TYPE; 
MI_COD_CODIGO_CPC            PLAN_PRESUPUESTAL.CODIGOCPCDANE%TYPE;         
MI_COD_CODIGO_CPCCOMP        PLAN_PRESUPUESTAL.CODIGOCPCDANE%TYPE; 
MI_COD_CODIGO_CPCRUB         PLAN_PRESUPUESTAL.CODIGOCPCDANE%TYPE; 
MI_COD_CODIGO_BPIN           PLAN_PRESUPUESTAL.CODIGOBPIN%TYPE;
MI_COD_CODIGO_BPINCOMP       PLAN_PRESUPUESTAL.CODIGOBPIN%TYPE;
MI_COD_CODIGO_BPINRUB        PLAN_PRESUPUESTAL.CODIGOBPIN%TYPE; 
MI_COD_FUENTE_CUIPO          PLAN_PRESUPUESTAL.CODIGOFUENTE%TYPE;
MI_COD_FUENTE_CUIPOCOMP      PLAN_PRESUPUESTAL.CODIGOFUENTE%TYPE; 
MI_COD_FUENTE_CUIPORUB       PLAN_PRESUPUESTAL.CODIGOFUENTE%TYPE;
MI_COD_TIPO_RECURSO          PLAN_PRESUPUESTAL.TIPO_RECURSO%TYPE; 
MI_VIGENCIA_ANTERIOR         PLAN_PRESUPUESTAL.RECAUDO_VA%TYPE; 
MI_SITUACION_FONDOS          PLAN_PRESUPUESTAL.CONSITUACIONFONDOS%TYPE; 
MI_TRANSFERENCIA             PLAN_PRESUPUESTAL.TRANSFERENCIA%TYPE; 
MI_TERCERO_CHIP              PLAN_PRESUPUESTAL.TERCERO_CHIP%TYPE; 
MI_DESTINACION_ESPECIFICA    PLAN_PRESUPUESTAL.APLICA_DEST_ESPECIFICA%TYPE; 
MI_TIPO_NORMA                PLAN_PRESUPUESTAL.TIPO_NORMA%TYPE; 
MI_NUMERO_NORMA              PLAN_PRESUPUESTAL.NUMERO_NORMA%TYPE; 
MI_FECHA_NORMA               PLAN_PRESUPUESTAL.FECHA_NORMA%TYPE; 
MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;  
MI_RETORNO                   CLOB := '';
MI_RTA                       VARCHAR2(10); 

BEGIN


SELECT COUNT('X') EXISTE
    INTO MI_COD_SECTORRUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
         AND CC.ANO = UN_ANO     
         AND CC.CODIGO = '001'     
         AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;
         
      IF MI_COD_SECTORRUB  = 0 THEN 
         MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador SECTOR no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13); 
      END IF;
        
SELECT COUNT('X') EXISTE
    INTO MI_COD_PROGRAMARUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '002'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;  
        
      IF MI_COD_PROGRAMARUB  = 0 THEN 
         MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador PROGRAMA no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
      END IF;
      
SELECT COUNT('X') EXISTE
    INTO MI_COD_SUBPROGRAMARUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '003'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;
        
      IF MI_COD_SUBPROGRAMARUB  = 0 THEN 
         MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador SUBPROGRAMA no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
      END IF;  
      
SELECT COUNT('X') EXISTE
    INTO MI_COD_COD_PROD_CUIPORUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '004'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;
        
      IF MI_COD_COD_PROD_CUIPORUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador COD_PROD_CUIPO no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
        END IF;
        
SELECT COUNT('X') EXISTE
    INTO MI_COD_CODIGO_BPINRUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '005'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1; 
        
    IF MI_COD_CODIGO_BPINRUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador CODIGO_BPIN no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);   
    END IF;
          

SELECT COUNT('X') EXISTE
    INTO MI_COD_CODIGO_CCPETRUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '006'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1; 
        
    IF MI_COD_CODIGO_CCPETRUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador CODIGO CCPET no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13); 
    END IF; 
              
SELECT COUNT('X') EXISTE
    INTO MI_COD_CODIGO_CPCRUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '007'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1; 
        
     IF MI_COD_CODIGO_CPCRUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) ||'El clasificador CODIGO CPC DANE no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' || MI_CUENTA||CHR(13);
    END IF;
        
SELECT COUNT('X') EXISTE
    INTO MI_COD_CODIGOUNIDADEJERUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '008'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;
        
    IF MI_COD_CODIGOUNIDADEJERUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador CODIGO UNIDAD EJECUTORA no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
    END IF; 

SELECT COUNT('X') EXISTE
    INTO MI_COD_FUENTE_CUIPORUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '009'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1; 
        
    IF MI_COD_FUENTE_CUIPORUB  = 0 THEN 
       MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador FUENTE FINANCIACION no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13); 
    END IF;
 
       
SELECT COUNT('X') EXISTE
    INTO MI_COD_CODIGOCCPETREGARUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '010'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;
        
    IF MI_COD_CODIGOCCPETREGARUB  = 0 THEN 
       MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador CODIGO CCPET REGALIAS no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13); 
    END IF; 
       
SELECT COUNT('X') EXISTE
    INTO MI_COD_POLITICA_PUBLICARUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '011'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;  
        
    IF MI_COD_POLITICA_PUBLICARUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador POLITICA PUBLICA no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
    END IF;
           
SELECT COUNT('X') EXISTE
    INTO MI_COD_DETALLE_SECTORIALRUB
    FROM CLASECLASIFICADOR CC 
    WHERE CC.COMPANIA = UN_COMPANIA     
        AND CC.ANO = UN_ANO     
        AND CC.CODIGO = '012'     
        AND (CASE WHEN UN_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1;   
        
    IF MI_COD_DETALLE_SECTORIALRUB  = 0 THEN 
        MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador DETALLE_SECTORIAL no se encuentra configurada con aplicacion rubro, se debe verificar la informacion.' ||CHR(13);
    END IF; 
        
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
                                               
  <<ACTUALIZARCLASIFICADORESRUBRO>>
  
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST  
  
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

    MI_CUENTA                   := MI_DATOS_COLUMNAS(1);
    MI_COD_SECTOR               := MI_DATOS_COLUMNAS(4);
    MI_COD_PROGRAMA             := MI_DATOS_COLUMNAS(5);
    MI_COD_SUBPROGRAMA          := MI_DATOS_COLUMNAS(6);
    MI_COD_COD_PROD_CUIPO       := MI_DATOS_COLUMNAS(7);
    MI_COD_CODIGO_BPIN          := MI_DATOS_COLUMNAS(8);
    MI_COD_CODIGO_CCPET         := MI_DATOS_COLUMNAS(9);
    MI_COD_CODIGO_CPC           := MI_DATOS_COLUMNAS(10);
    MI_COD_CODIGOUNIDADEJE      := MI_DATOS_COLUMNAS(11);
    MI_COD_FUENTE_CUIPO         := MI_DATOS_COLUMNAS(12);
    MI_COD_CODIGOCCPETREGA      := MI_DATOS_COLUMNAS(13);
    MI_COD_POLITICA_PUBLICA     := MI_DATOS_COLUMNAS(14);
    MI_COD_DETALLE_SECTORIAL    := MI_DATOS_COLUMNAS(15);
    MI_COD_TIPO_RECURSO         := MI_DATOS_COLUMNAS(16);
    MI_VIGENCIA_ANTERIOR        := CASE WHEN MI_DATOS_COLUMNAS(17) = 'SI' THEN '-1' ELSE '0' END;
    MI_SITUACION_FONDOS         := CASE WHEN MI_DATOS_COLUMNAS(18) = 'SI' THEN '-1' ELSE '0' END; 
    MI_TRANSFERENCIA            := CASE WHEN MI_DATOS_COLUMNAS(19) = 'SI' THEN '-1' ELSE '0' END;
    MI_TERCERO_CHIP             := MI_DATOS_COLUMNAS(20);
    MI_DESTINACION_ESPECIFICA   := CASE WHEN MI_DATOS_COLUMNAS(21) = 'SI' THEN '-1' ELSE '0' END;
    MI_TIPO_NORMA               := MI_DATOS_COLUMNAS(22);
    MI_NUMERO_NORMA             := MI_DATOS_COLUMNAS(23);
    MI_CAMPOS                   :=  '';     
    
 BEGIN
        IF MI_DATOS_COLUMNAS(24) IS NULL OR MI_DATOS_COLUMNAS(24) = '' THEN
            MI_FECHA_NORMA := NULL;
        ELSE
            MI_FECHA_NORMA := TO_DATE(MI_DATOS_COLUMNAS(24), 'DD/MM/YYYY');
        END IF;      
    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN MI_FECHA_NORMA := NULL;
        WHEN OTHERS THEN MI_FECHA_NORMA := NULL;
    END;
    
-- NATURALEZA = D para gastos NATURALEZA = C para ingresos                     
-- CLASECLASIFICADOR -> APLICACION 0;No Aplica;1;Rubro;2;Detall

    SELECT NATURALEZA
    INTO MI_NATURALEZA
    FROM PLAN_PRESUPUESTAL P
    WHERE  P.COMPANIA = UN_COMPANIA
       AND P.ANO      = UN_ANO
       AND P.CODIGO   = MI_CUENTA;

BEGIN
    IF MI_COD_SECTORRUB != 0 THEN
        IF MI_COD_SECTOR IS NOT NULL THEN
        
            BEGIN
                SELECT TC.CODIGO 
                INTO MI_COD_SECTORCOMP
                FROM CLASECLASIFICADOR CC INNER JOIN TIPOCLASIFICADOR TC 
                        ON CC.COMPANIA = TC.COMPANIA
                        AND CC.ANO = TC.ANO
                        AND CC.CODIGO = TC.CLASECLASIFICADOR
                WHERE CC.COMPANIA = UN_COMPANIA
                        AND CC.ANO = UN_ANO
                        AND CC.CODIGO = '001'
                        AND (CASE WHEN MI_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END ) = 1
                        AND TC.CODIGO = MI_COD_SECTOR
                GROUP BY TC.CODIGO;
                
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        MI_COD_SECTORCOMP := '';
            END;

            IF MI_COD_SECTOR = MI_COD_SECTORCOMP AND MI_COD_SECTOR IS NOT NULL THEN
                MI_CAMPOS := MI_CAMPOS || ' SECTOR = ''' || MI_COD_SECTOR || ''',';
            ELSE
                MI_RETORNO := MI_RETORNO || CHR(9) || 'Para la cuenta ' || MI_CUENTA || ' en el clasificador SECTOR el codigo ' || MI_COD_SECTOR || ' No existe en TIPOCLASIFICADOR ' || CHR(13);
            END IF;

        ELSE
            MI_RETORNO := MI_RETORNO || CHR(9) || 'No se diligencio el clasificador SECTOR para la cuenta: ' || MI_CUENTA || CHR(13);
        END IF;
    END IF;
END;     

BEGIN
    IF MI_COD_PROGRAMARUB  != 0 THEN 
        IF MI_COD_PROGRAMA IS NOT NULL THEN
              
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_PROGRAMACOMP
                FROM CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                        ON  CC.COMPANIA =  TC.COMPANIA
                        AND CC.ANO      =  TC.ANO
                        AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE CC.COMPANIA = UN_COMPANIA     
                        AND CC.ANO = UN_ANO     
                        AND CC.CODIGO = '002'     
                        AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                        AND TC.CODIGO =  MI_COD_PROGRAMA
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_PROGRAMACOMP := '';                
            END;
                
            IF MI_COD_PROGRAMA = MI_COD_PROGRAMACOMP  AND  MI_COD_PROGRAMA IS NOT NULL THEN 
                 MI_CAMPOS := MI_CAMPOS || ' PROGRAMA           = ''' || MI_COD_PROGRAMA        ||''',' ;  
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador PROGRAMA el codigo ' || MI_COD_PROGRAMA || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        ELSE 
        MI_RETORNO :=  MI_RETORNO || CHR(9) ||'No se diligencio el clasificador PROGRAMA para la cuenta: ' || MI_CUENTA||CHR(13);  
        END IF; 
    END IF;
END;  

BEGIN
    IF MI_COD_SUBPROGRAMARUB  != 0 THEN 
        IF MI_COD_SUBPROGRAMA IS NOT NULL THEN
        
            BEGIN
                SELECT TC.CODIGO 
                INTO MI_COD_SUBPROGRAMACOMP
                FROM CLASECLASIFICADOR CC 
                INNER JOIN TIPOCLASIFICADOR TC ON CC.COMPANIA = TC.COMPANIA
                    AND CC.ANO = TC.ANO
                    AND CC.CODIGO = TC.CLASECLASIFICADOR
                WHERE CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '003'     
                    AND (CASE WHEN MI_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1     
                    AND TC.CODIGO = MI_COD_SUBPROGRAMA
                GROUP BY TC.CODIGO;
                EXCEPTION 
                WHEN NO_DATA_FOUND THEN
                    MI_COD_SUBPROGRAMACOMP := '';                
            END;
            IF MI_COD_SUBPROGRAMA = MI_COD_SUBPROGRAMACOMP  AND  MI_COD_SUBPROGRAMA IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' SUBPROGRAMA        = ''' || MI_COD_SUBPROGRAMA     ||''',' ; 
            ELSE
                MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador SUBPROGRAMA el codigo ' || MI_COD_SUBPROGRAMA || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;   
   END IF;
END; 

BEGIN
    IF MI_COD_COD_PROD_CUIPORUB  != 0 THEN 
        IF MI_COD_COD_PROD_CUIPO IS NOT NULL THEN         
        
            BEGIN
                SELECT TC.CODIGO 
                INTO MI_COD_COD_PROD_CUIPOCOMP
                FROM CLASECLASIFICADOR CC 
                INNER JOIN TIPOCLASIFICADOR TC ON CC.COMPANIA = TC.COMPANIA
                    AND CC.ANO = TC.ANO
                    AND CC.CODIGO = TC.CLASECLASIFICADOR
                WHERE CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '004'     
                    AND (CASE WHEN MI_NATURALEZA = 'D' THEN CC.APLICACION ELSE CC.APLICACIONINGRESOS END) = 1     
                    AND TC.CODIGO = MI_COD_COD_PROD_CUIPO
                GROUP BY TC.CODIGO;
                EXCEPTION 
                WHEN NO_DATA_FOUND THEN
                    MI_COD_COD_PROD_CUIPOCOMP := '';                
            END;
            
            IF MI_COD_COD_PROD_CUIPO = MI_COD_COD_PROD_CUIPOCOMP  AND  MI_COD_COD_PROD_CUIPO IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' CODIGOPRODUCTO     = ''' || MI_COD_COD_PROD_CUIPO  ||''',' ;
             ELSE
                MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador CODIGO PRODUCTO el codigo ' || MI_COD_COD_PROD_CUIPO || ' No existe en TIPOCLASIFICADOR '||CHR(13);
             END IF;     
        ELSE 
            MI_RETORNO :=  MI_RETORNO || CHR(9) ||'No se diligencio el clasificador CODIGO PRODUCTO para la cuenta: ' || MI_CUENTA||CHR(13);  
        END IF;
    END IF;   
END;

 BEGIN
    IF MI_COD_CODIGO_BPINRUB  != 0 THEN
        IF MI_COD_CODIGO_BPIN IS NOT NULL THEN
        
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_BPINCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '005'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_CODIGO_BPIN
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_CODIGO_BPINCOMP := '';                
            END;
            IF MI_COD_CODIGO_BPIN = MI_COD_CODIGO_BPINCOMP  AND  MI_COD_CODIGO_BPIN IS NOT NULL THEN 
                 MI_CAMPOS := MI_CAMPOS || ' CODIGOBPIN        = ''' || MI_COD_CODIGO_BPIN      ||''',' ; 
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador CODIGO BPIN el codigo ' || MI_COD_CODIGO_BPIN || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;   
    END IF;
END; 

 BEGIN
    IF MI_COD_CODIGO_CCPETRUB  != 0 THEN 
        IF MI_COD_CODIGO_CCPET IS NOT NULL THEN
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_CCPETCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '006'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_CODIGO_CCPET
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_CODIGO_CCPETCOMP := '';                
            END;
            IF MI_COD_CODIGO_CCPET = MI_COD_CODIGO_CCPETCOMP  AND  MI_COD_CODIGO_CCPET IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPET       = ''' || MI_COD_CODIGO_CCPET     ||''',' ;  
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador CODIGO CCPET el codigo ' || MI_COD_CODIGO_CCPET || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;   
    END IF;
END; 

 BEGIN
    IF MI_COD_CODIGO_CPCRUB  != 0 THEN 
        IF MI_COD_CODIGO_CPC IS NOT NULL THEN
        
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_CPCCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '007'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_CODIGO_CPC
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_CODIGO_CPCCOMP := '';                
            END;
            IF MI_COD_CODIGO_CPC = MI_COD_CODIGO_CPCCOMP  AND  MI_COD_CODIGO_CPC IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' CODIGOCPCDANE         = ''' || MI_COD_CODIGO_CPC   ||''',' ;   
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador CODIGO CPC DANE el codigo ' || MI_COD_CODIGO_CPC || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        ELSE 
           MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El clasificador CODIGO CPC DANE No esta configurado como RUBRO' ||CHR(13);
        END IF;
    END IF;
END; 

 BEGIN
    IF MI_COD_CODIGOUNIDADEJERUB  != 0 THEN 
        IF MI_COD_CODIGOUNIDADEJE IS NOT NULL THEN
        
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGOUNIDADEJECOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '008'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_CODIGOUNIDADEJE
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_CODIGOUNIDADEJECOMP := '';                
            END;
            IF MI_COD_CODIGOUNIDADEJE = MI_COD_CODIGOUNIDADEJECOMP  AND  MI_COD_CODIGOUNIDADEJE IS NOT NULL THEN 
            MI_CAMPOS := MI_CAMPOS || ' CODIGOUNIDADEJE    = ''' || MI_COD_CODIGOUNIDADEJE ||''',' ;
            ELSE
             MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador UNIDAD EJECUTORA el codigo ' || MI_COD_CODIGOUNIDADEJE || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;   
    END IF;
END; 

 BEGIN
    IF MI_COD_FUENTE_CUIPORUB  != 0 THEN
        IF MI_COD_FUENTE_CUIPO IS NOT NULL THEN
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_FUENTE_CUIPOCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '009'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_FUENTE_CUIPO
                GROUP BY TC.CODIGO  ;
                 EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_FUENTE_CUIPOCOMP := '';                
            END;
            IF MI_COD_FUENTE_CUIPO = MI_COD_FUENTE_CUIPOCOMP  AND  MI_COD_FUENTE_CUIPO IS NOT NULL THEN 
            MI_CAMPOS := MI_CAMPOS || ' CODIGOFUENTE       = ''' || MI_COD_FUENTE_CUIPO    ||''',' ; 
            ELSE
            MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador FUENTE FINANCIACION el codigo ' || MI_COD_FUENTE_CUIPO || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;   
    END IF;
END; 

BEGIN
    IF MI_COD_CODIGOCCPETREGARUB != 0 THEN
        IF MI_COD_CODIGOCCPETREGA IS NOT NULL THEN
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGOCCPETREGACOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '010'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_CODIGOCCPETREGA
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_CODIGOCCPETREGACOMP := '';  
            END;
            IF MI_COD_CODIGOCCPETREGA = MI_COD_CODIGOCCPETREGACOMP  AND  MI_COD_CODIGOCCPETREGA IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPETREGA       = ''' || MI_COD_CODIGOCCPETREGA    ||''',' ; 
            ELSE
                MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador CODIGO CCPET REGALIAS el codigo ' || MI_COD_CODIGOCCPETREGA || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        END IF;
    END IF;  
END; 


BEGIN
    IF MI_COD_POLITICA_PUBLICARUB != 0 THEN 
        IF MI_COD_POLITICA_PUBLICA IS NOT NULL THEN
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_POLITICA_PUBLICACOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '011'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1    
                    AND TC.CODIGO =  MI_COD_POLITICA_PUBLICA
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_POLITICA_PUBLICACOMP := '';                
            END;
            IF MI_COD_POLITICA_PUBLICA = MI_COD_POLITICA_PUBLICACOMP  AND  MI_COD_POLITICA_PUBLICA IS NOT NULL THEN 
                 MI_CAMPOS := MI_CAMPOS || ' POLITCA_PUBLICA_CUIPO   = ''' || MI_COD_POLITICA_PUBLICA    ||''',' ;
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador POLITICA PUBLICA el codigo ' || MI_COD_POLITICA_PUBLICA || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        ELSE 
            MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador POLITICA PUBLICA para la cuenta: ' || MI_CUENTA||CHR(13);  
        END IF;
    END IF;
END; 


BEGIN
    IF MI_COD_DETALLE_SECTORIALRUB != 0 THEN
        IF MI_COD_DETALLE_SECTORIAL IS NOT NULL THEN
            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_DETALLE_SECTORIALCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = UN_ANO     
                    AND CC.CODIGO = '012'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 1     
                    AND TC.CODIGO =  MI_COD_DETALLE_SECTORIAL
                GROUP BY TC.CODIGO  ;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_COD_DETALLE_SECTORIALCOMP := '';                
            END;
            IF MI_COD_DETALLE_SECTORIAL = MI_COD_DETALLE_SECTORIALCOMP  AND  MI_COD_DETALLE_SECTORIAL IS NOT NULL THEN 
                MI_CAMPOS := MI_CAMPOS || ' DETALLE_SECTORIAL  = ''' || MI_COD_DETALLE_SECTORIAL ||''',' ;
            ELSE
                 MI_RETORNO :=  MI_RETORNO || CHR(9) || 'Para la cuenta '|| MI_CUENTA ||' en el clasificador DETALLE SECTORIAL el codigo ' || MI_COD_DETALLE_SECTORIAL || ' No existe en TIPOCLASIFICADOR '||CHR(13);
            END IF;
        ELSE 
            MI_RETORNO :=  MI_RETORNO || CHR(9) ||' No se diligencio el clasificador DETALLE SECTORIAL para la cuenta: ' || MI_CUENTA||CHR(13);  
        END IF;
    END IF;
END; 

    IF  MI_COD_TIPO_RECURSO =  '1' OR MI_COD_TIPO_RECURSO =  '2' OR MI_COD_TIPO_RECURSO =  '3' OR MI_COD_TIPO_RECURSO =  '4' 
     OR MI_COD_TIPO_RECURSO IS NULL OR MI_COD_TIPO_RECURSO = '' THEN 
      MI_CAMPOS := MI_CAMPOS || ' TIPO_RECURSO  = ''' || MI_COD_TIPO_RECURSO ||''',' ;  
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El valor Tipo Recurso '|| MI_COD_TIPO_RECURSO || ' No es valido debe ser un numero entre 1 y 4 '||CHR(13);               
    END IF;
    
    MI_CAMPOS := MI_CAMPOS || ' RECAUDO_VA  = ''' || MI_VIGENCIA_ANTERIOR ||''',';
    MI_CAMPOS := MI_CAMPOS || ' CONSITUACIONFONDOS  = ''' || MI_SITUACION_FONDOS ||''',' ;  
    MI_CAMPOS := MI_CAMPOS || ' TRANSFERENCIA  = ''' || MI_TRANSFERENCIA ||''',' ;  
    MI_CAMPOS := MI_CAMPOS || ' TERCERO_CHIP  = ''' || MI_TERCERO_CHIP ||''',' ;  
    MI_CAMPOS := MI_CAMPOS || ' APLICA_DEST_ESPECIFICA  = ''' || MI_DESTINACION_ESPECIFICA ||''',' ; 
    
    IF  MI_TIPO_NORMA =  '1' OR MI_TIPO_NORMA =  '2' OR MI_TIPO_NORMA =  '3' OR MI_TIPO_NORMA =  '4' OR MI_TIPO_NORMA =  '5' OR MI_TIPO_NORMA =  '6'
         OR MI_TIPO_NORMA IS NULL OR MI_TIPO_NORMA = '' THEN 
      MI_CAMPOS := MI_CAMPOS || ' TIPO_NORMA  = ''' || MI_TIPO_NORMA ||''',' ;  
    ELSE
      MI_RETORNO :=  MI_RETORNO || CHR(9) || 'El valor Tipo Norma '|| MI_TIPO_NORMA || ' No es valido, debe ser un numero entre 1 y 6 '||CHR(13);               
    END IF;
    
    MI_CAMPOS := MI_CAMPOS || ' NUMERO_NORMA  = ''' || MI_NUMERO_NORMA ||''',' ;
    MI_CAMPOS := MI_CAMPOS || ' FECHA_NORMA  = ''' || MI_FECHA_NORMA ||''',' ;  
    
    
    IF MI_RETORNO IS NOT NULL THEN 
     MI_RETORNO :=  MI_RETORNO || CHR(10) || CHR(13);
    END IF;
    MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA             || '''
                 AND ANO               =   ' || UN_ANO                  || '        
                 AND CODIGO            = ''' || MI_CUENTA               || ''' ';
    IF  MI_CAMPOS  IS NOT NULL THEN 
        MI_CAMPOS := SUBSTR(MI_CAMPOS,1,LENGTH(MI_CAMPOS) -1 ) ;   
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PRESUPUESTAL'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);      
    END IF;

  END LOOP ACTUALIZARCLASIFICADORESRUBRO;
  
MI_RETORNO := MI_RETORNO|| CHR(10) || CHR(13)||'PROCESO TERMINADO';
RETURN MI_RETORNO;

END FC_ACT_CLASIFICADOR_RUBRO;

FUNCTION FC_ACT_CLA_APROPIACIONES
/*
    NAME              : FC_ACT_CLA_APROPIACIONES
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÑOZ
    DATE MIGRADOR     : 15/07/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : LUIS JACOBO DIAZ M
    DATE MODIFIED     : 06/09/
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS  TIPOS CLASIFICADORES DESDE UN EXCEL A LAS APROPIACIONES INICIALES

    @NAME:    FC_ACT_CLA_APROPIACIONES
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE             NUMBER := 0;
MI_EXISTE_ERROR       NUMBER := 0;
MI_APLICACION         NUMBER := 0;
MI_APLICACIONINGRESOS NUMBER := 0;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_RETORNO            CLOB := '';
MI_RTA                VARCHAR2(10); 
MI_NUMERO_COD         VARCHAR2(1000);
MI_EXISTE_CCPET       VARCHAR2(100);
MI_EXISTE_PROGRAMA    VARCHAR2(100);
MI_EXISTE_UNIEJE      VARCHAR2(100);
MI_EXISTE_BPIN        VARCHAR2(100);

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_TIPOCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
       
        --ACTUALIZA TIPO_CLASIFICADORES  EN APROPIACIONESINICIALES
       MI_EXISTE := '';
       MI_EXISTE_ERROR := '';
       MI_CAMPOS := '';
            IF(MI_DATOS_COLUMNAS(13) IS NOT NULL) THEN 
               -- BUSCAMOS EL TIPO PARA EL CLASIFICADOR CCPET 006
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE 
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  UN_ANO
                      AND CLASECLASIFICADOR = '006'
                      AND CODIGO = MI_DATOS_COLUMNAS(13);
               IF (MI_EXISTE NOT IN (0)) THEN
                    MI_CAMPOS := MI_CAMPOS||'CODIGO_CCPET      =  ''' || TRIM(MI_DATOS_COLUMNAS(13)) || '''';
               ELSIF MI_EXISTE IN (0) THEN
                     MI_EXISTE_ERROR := MI_EXISTE_ERROR||'1' ;
               END IF;
            END IF;
            IF (MI_DATOS_COLUMNAS(14) IS NOT NULL) THEN
               -- BUSCAMOS EL TIPO PARA EL CLASIFICADOR PROGRAMA 002
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  UN_ANO
                      AND CLASECLASIFICADOR = '002'
                      AND CODIGO = MI_DATOS_COLUMNAS(14);
                   IF (MI_EXISTE NOT IN (0)) THEN
                        IF(MI_CAMPOS IS NOT NULL) THEN
                            MI_CAMPOS := MI_CAMPOS||',PROGRAMA      =  ''' || TRIM(MI_DATOS_COLUMNAS(14)) || '''';
                        ELSE
                            MI_CAMPOS := MI_CAMPOS||'PROGRAMA      =  ''' || TRIM(MI_DATOS_COLUMNAS(14)) || '''';
                        END IF;
                   ELSIF MI_EXISTE IN (0) THEN
                     MI_EXISTE_ERROR := MI_EXISTE_ERROR||'2' ;
                   END IF;
            END IF;
            IF( MI_DATOS_COLUMNAS(15) IS NOT NULL) THEN
               -- BUSCAMOS EL TIPO PARA EL CLASIFICADOR BPIN 005
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  UN_ANO
                      AND CLASECLASIFICADOR = '005'
                      AND CODIGO = MI_DATOS_COLUMNAS(15);
                   IF (MI_EXISTE NOT IN (0)) THEN
                        IF(MI_CAMPOS IS NOT NULL) THEN
                            MI_CAMPOS := MI_CAMPOS||',CODIGOBPIN      =  ''' || TRIM(MI_DATOS_COLUMNAS(12)) || '''';
                        ELSE
                            MI_CAMPOS := MI_CAMPOS||'CODIGOBPIN      =  ''' || TRIM(MI_DATOS_COLUMNAS(12)) || '''';
                        END IF;
                   ELSIF MI_EXISTE IN (0) THEN
                     MI_EXISTE_ERROR := MI_EXISTE_ERROR||'3' ;
                   END IF;
            END IF;
            IF( MI_DATOS_COLUMNAS(16) IS NOT NULL) THEN
               -- BUSCAMOS EL TIPO PARA EL CLASIFICADOR UNIDAD EJECUTORA 008
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  UN_ANO
                      AND CLASECLASIFICADOR = '008'
                      AND CODIGO = MI_DATOS_COLUMNAS(16);
                   IF (MI_EXISTE NOT IN (0)) THEN
                        IF(MI_CAMPOS IS NOT NULL) THEN
                            MI_CAMPOS := MI_CAMPOS||',CODIGOUNIDADEJE      =  ''' || TRIM(MI_DATOS_COLUMNAS(16)) || '''';
                        ELSE
                            MI_CAMPOS := MI_CAMPOS||'CODIGOUNIDADEJE      =  ''' || TRIM(MI_DATOS_COLUMNAS(16)) || '''';
                        END IF;
                   ELSIF MI_EXISTE IN (0) THEN
                     MI_EXISTE_ERROR := MI_EXISTE_ERROR||'4' ;
                   END IF;
            END IF;
            
            IF( MI_DATOS_COLUMNAS(17) IS NOT NULL) THEN
               
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE
                   FROM TIPOCLASIFICADOR
                   WHERE COMPANIA = UN_COMPANIA
                      AND ANO  =  UN_ANO
                      AND CLASECLASIFICADOR = '012'
                      AND CODIGO = MI_DATOS_COLUMNAS(17);
                   IF (MI_EXISTE NOT IN (0)) THEN
                        IF(MI_CAMPOS IS NOT NULL) THEN
                            MI_CAMPOS := MI_CAMPOS||',DETALLESECTORIAL      =  ''' || TRIM(MI_DATOS_COLUMNAS(17)) || '''';
                        ELSE
                            MI_CAMPOS := MI_CAMPOS||'DETALLESECTORIAL      =  ''' || TRIM(MI_DATOS_COLUMNAS(17)) || '''';
                        END IF;
                   ELSIF MI_EXISTE IN (0) THEN
                     MI_EXISTE_ERROR := MI_EXISTE_ERROR||'5' ;
                   END IF;
            END IF;

            IF(MI_CAMPOS IS NOT NULL) THEN
                MI_CAMPOS := MI_CAMPOS||',MODIFIED_BY       =  ''' || UN_USUARIO           || '''
                                ,DATE_MODIFIED     =          SYSDATE';
            END IF;
            -- evalua en caso tal de haberse encontrado alguno de los tipos de clasificadores enviados
               IF(MI_EXISTE_ERROR IS NOT NULL)THEN
                    -- SE HACE UN ANALISIS DE LOS TIPOS DE CLASIFICADORES QUE NO EXISTEN EN EL SISTEMA PARA PODER INFORMARLOS
                    MI_NUMERO_COD := NULL;
                    SELECT INSTR(MI_EXISTE_ERROR, '1') INTO MI_NUMERO_COD FROM DUAL;
                    IF(MI_NUMERO_COD <> 0 AND MI_NUMERO_COD IS NOT NULL)THEN
                        MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El tipo de clasificador '||MI_DATOS_COLUMNAS(13)||' relacionado al clasificador CCPET (006) no existe. '||CHR(13);
                    END IF;
                    MI_NUMERO_COD := NULL;
                    SELECT INSTR(MI_EXISTE_ERROR, '2') INTO MI_NUMERO_COD FROM DUAL;
                    IF(MI_NUMERO_COD <> 0 AND MI_NUMERO_COD IS NOT NULL)THEN
                       MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El tipo de clasificador '||MI_DATOS_COLUMNAS(14)||' relacionado al clasificador PROGRAMA (002) no existe. '||CHR(13);
                    END IF;
                    MI_NUMERO_COD := NULL;
                    SELECT INSTR(MI_EXISTE_ERROR, '3') INTO MI_NUMERO_COD FROM DUAL;
                    IF(MI_NUMERO_COD <> 0 AND MI_NUMERO_COD IS NOT NULL)THEN
                       MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El tipo de clasificador '||MI_DATOS_COLUMNAS(15)||' relacionado al clasificador BPIN (005) no existe. '||CHR(13);
                    END IF;
                    MI_NUMERO_COD := NULL;
                    SELECT INSTR(MI_EXISTE_ERROR, '4') INTO MI_NUMERO_COD FROM DUAL;
                    IF(MI_NUMERO_COD <> 0 AND MI_NUMERO_COD IS NOT NULL)THEN
                        MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El tipo de clasificador '||MI_DATOS_COLUMNAS(16)||' relacionado al clasificador UNIDAD EJECUTORA (008) no existe. '||CHR(13);
                    END IF;
                    SELECT INSTR(MI_EXISTE_ERROR, '5') INTO MI_NUMERO_COD FROM DUAL;
                    IF(MI_NUMERO_COD <> 0 AND MI_NUMERO_COD IS NOT NULL)THEN
                        MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||' El tipo de clasificador '||MI_DATOS_COLUMNAS(17)||' relacionado al clasificador DETALLE SECTORIAL (012) no existe. '||CHR(13);
                    END IF;
               END IF;

       IF(MI_CAMPOS IS NOT NULL) THEN
             BEGIN
                   MI_CONDICION := '   COMPANIA      =  ''' || UN_COMPANIA || '''
                           AND ANO            =    ' || UN_ANO || '
                           AND CODIGO         =  ''' || MI_DATOS_COLUMNAS(1) || ''' 
                           AND TERCERO        = ''' || MI_DATOS_COLUMNAS(3) || '''
                           AND SUCURSAL       = ''' || MI_DATOS_COLUMNAS(4) || '''
                           AND AUXILIAR       = ''' || MI_DATOS_COLUMNAS(7) || '''
                           AND CENTRO_COSTO   = ''' || MI_DATOS_COLUMNAS(5) || '''
                           AND REFERENCIA     = ''' || MI_DATOS_COLUMNAS(9) || '''
                           AND FUENTE_RECURSO = ''' || MI_DATOS_COLUMNAS(11) || ''' ';

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'APROPIACIONESINICIALES'
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);
                    COMMIT;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    ROLLBACK;
                    RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
             END;
          MI_CAMPOS := '';
        END IF;
  END LOOP CREAR_TIPOCLASIFICADORES;
RETURN MI_RETORNO;
END FC_ACT_CLA_APROPIACIONES;
END PCK_CGR;