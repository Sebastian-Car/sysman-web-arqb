create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS AS

  --1
  FUNCTION FC_NOMBREPERIODO 
  /*
    OBJETIVO              : Retornar el nombre del mes concatenado con el año.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_ANO: Valor numérico del año.
                            UN_PERIODO: Código del mes.
                            UN_FRECUENCIA: Indicador de la frecuencia de los periodos de facturación.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : YESIKA PAOLA BECERRA CASTRO
    FECHA                 : 22/08/2016
    REALIZADO POR:        : SYSMAN  SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :
    @NAME:    asignarNombrePeriodo
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_FRECUENCIA   IN VARCHAR2
  )
  RETURN VARCHAR2  
  AS 
    MI_ERROR_FUN     PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_PERIODO_ANT   PCK_SUBTIPOS.TI_ENTERO;
    MI_FRECUENCIA    VARCHAR2 (3 CHAR);
    MI_NOMBREPERIODO VARCHAR2 (300 CHAR);
    MI_MES           PCK_SUBTIPOS.TI_ENTERO;
    MI_MESUNO        PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;  
  BEGIN 
    IF UN_FRECUENCIA IS NULL OR UN_FRECUENCIA = '' THEN 
      MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION'
                                             ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                             ,UN_FECHA_PAR => SYSDATE);
    ELSE
      MI_FRECUENCIA := UN_FRECUENCIA;
    END IF;

    CASE 
      WHEN MI_FRECUENCIA = 'M' THEN 
        MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO) || '/' || TO_CHAR(UN_ANO);
      WHEN MI_FRECUENCIA = 'B' THEN 
        MI_MES := UN_PERIODO * 2 - 1;
        MI_MESUNO := UN_PERIODO * 2;
        MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) || '/' || TO_CHAR(UN_ANO);
      WHEN MI_FRECUENCIA = 'C' THEN 
        MI_PERIODO_ANT := CASE WHEN UN_PERIODO * 2 - 2 = 0 
                               THEN 12 
                               ELSE UN_PERIODO * 2 - 2 
                          END;
        MI_MES := UN_PERIODO * 2 - 1;
        MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODO_ANT) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || '/' || TO_CHAR(UN_ANO);
      WHEN MI_FRECUENCIA = 'T' THEN 
        MI_MES := UN_PERIODO * 3 - 2;
        MI_MESUNO := UN_PERIODO *  3;
        MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) || '/' || TO_CHAR(UN_ANO);
    END CASE ;

    RETURN MI_NOMBREPERIODO;
	END FC_NOMBREPERIODO;


  --2
  FUNCTION FC_CAMBIARFECHAPAGO 
  /*
    OBJETIVO              : Cambiar los datos de un pago.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_BANCOANT: Código del banco anterior.
                            UN_FECHAANT: Fecha del banco anterior.
                            UN_PAQUETEANT: Código del paquete anterior.
                            UN_BANCONUE: Código del nuevo banco.
                            UN_FECHANUE: Fecha del nuevo banco.
                            UN_PAQUETENUE: Código del nuevo paquete.
                            UN_USUARIO: Nombre de usuario que ejecuta la función
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : JAVIER ANDRES RODRIGUEZ RIOS
    FECHA                 : 22/08/2016 4:00 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    : 23/05/2017
    LIDER MODIFICACIÓN    : AURA LILIANA MONROY GARCÍA
    REALIZADO POR         : SYSMAN SAS
    OBJETIVO MODIFICACIÓN : Se agregan los campos de auditoría en las operaciones de inserción y modificación

    NAME                  : FC_CAMBIARFECHAPAGO En Access --> cambiarfechapago
    SOURCE MODULE         : SERVICIOS PUBLICOS
    @NAME:    cambiarFechaPago
    @METHOD:  GET
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_BANCOANT   IN PCK_SUBTIPOS.TI_BANCO,
    UN_FECHAANT   IN DATE,
    UN_PAQUETEANT IN SP_RECAUDOS.NUMEROPAQUETE%TYPE,
    UN_BANCONUE   IN PCK_SUBTIPOS.TI_BANCO,
    UN_FECHANUE   IN DATE,
    UN_PAQUETENUE IN SP_RECAUDOS.NUMEROPAQUETE%TYPE,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2
  AS
    MI_ERROR_FUN           PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_CONSECUTIVO         SP_PAGO.CONSECUTIVO%TYPE;
    MI_RTA                 PCK_SUBTIPOS.TI_RTA_ACME;
    MI_BARRAS1             SP_RECAUDOS.BARRAS%TYPE;
    MI_TIPO1               SP_RECAUDOS.TIPO%TYPE;
    MI_VALORREPORTADO1     SP_RECAUDOS.VALORREPORTADO%TYPE;
    MI_CUPONESREPORTADOS1  SP_RECAUDOS.CUPONESREPORTADOS%TYPE;
    MI_VALORREGISTRADO1    SP_RECAUDOS.VALORREGISTRADO%TYPE;
    MI_CUPONESREGISTRADOS1 SP_RECAUDOS.CUPONESREGISTRADOS%TYPE;
    MI_DIFERENCIACUPONES1  SP_RECAUDOS.DIFERENCIACUPONES%TYPE;
    MI_DIFERENCIAVALORES1  SP_RECAUDOS.DIFERENCIAVALORES%TYPE;
    MI_USUARIO1            SP_RECAUDOS.USUARIO%TYPE;
    MI_COMENTARIOS1        SP_RECAUDOS.COMENTARIOS%TYPE;
    MI_FECHA2              DATE;
    MI_BANCO2              PCK_SUBTIPOS.TI_BANCO;
    MI_NUMEROPAQUETE2      SP_RECAUDOS.NUMEROPAQUETE%TYPE;
    MI_TABLAINSERT         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSINSERT        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORESINSERT       PCK_SUBTIPOS.TI_VALORES;
    MI_TABLAUPDATE         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE        PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE     PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLADELETE         PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE     PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLAMERGE          PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING          PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE         PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE         PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE       PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_INDICADOR           SP_PAGO.INDCONTROL%TYPE;
  BEGIN
    IF UN_BANCOANT||UN_FECHAANT||UN_PAQUETEANT=UN_BANCONUE||UN_FECHANUE||UN_PAQUETENUE THEN
      BEGIN 
       -- MI_RTA :='No se puede realizar la transacción porque los datos iniciales son iguales a los datos finales';
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO1);
      END;
    ELSE
      BEGIN
        BEGIN 
          SELECT 
            BARRAS,
            TIPO,
            VALORREPORTADO,
            CUPONESREPORTADOS,
            VALORREGISTRADO,
            CUPONESREGISTRADOS,
            DIFERENCIACUPONES,
            DIFERENCIAVALORES,
            USUARIO,
            COMENTARIOS
          INTO 
            MI_BARRAS1,
            MI_TIPO1,
            MI_VALORREPORTADO1,
            MI_CUPONESREPORTADOS1,
            MI_VALORREGISTRADO1,
            MI_CUPONESREGISTRADOS1,
            MI_DIFERENCIACUPONES1,
            MI_DIFERENCIAVALORES1,
            MI_USUARIO1,
            MI_COMENTARIOS1
          FROM SP_RECAUDOS
          WHERE COMPANIA      = UN_COMPANIA
            AND FECHA         = UN_FECHAANT
            AND BANCO         = UN_BANCOANT
            AND NUMEROPAQUETE = UN_PAQUETEANT;

          EXCEPTION WHEN NO_DATA_FOUND 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;          
        END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR (1).CLAVE := 'FECHA';
          MI_MSGERROR (1).VALOR := UN_FECHAANT;
          MI_MSGERROR (2).CLAVE := 'BANCO';
          MI_MSGERROR (2).VALOR := UN_BANCOANT;
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO2
                                     ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                     ,UN_REEMPLAZOS => MI_MSGERROR);        
      END;

      IF MI_BARRAS1 IS NOT NULL THEN
        -- LA TABLA NO EXISTE.  
        BEGIN
          BEGIN
            SELECT 
              FECHA
             ,BANCO
             ,NUMEROPAQUETE
            INTO 
              MI_FECHA2
             ,MI_BANCO2
             ,MI_NUMEROPAQUETE2
            FROM SP_RECAUDOS
            WHERE COMPANIA     =UN_COMPANIA
              AND FECHA        =UN_FECHANUE
              AND BANCO        =UN_BANCONUE
              AND NUMEROPAQUETE=UN_PAQUETENUE;

            EXCEPTION WHEN NO_DATA_FOUND 
                      THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR (1).CLAVE := 'FECHA';
            MI_MSGERROR (1).VALOR := UN_FECHANUE;
            MI_MSGERROR (2).CLAVE := 'BANCO';
            MI_MSGERROR (2).VALOR := UN_BANCONUE;
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO2
                                       ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                       ,UN_REEMPLAZOS => MI_MSGERROR);
        END;

        IF MI_FECHA2 IS NOT NULL THEN
          FOR RS IN (
            SELECT *
            FROM SP_PAGO
            WHERE COMPANIA     =UN_COMPANIA
              AND FECHA        =UN_FECHAANT
              AND BANCO        =UN_BANCOANT
              AND NUMEROPAQUETE=UN_PAQUETEANT)
          LOOP
            MI_CONSECUTIVO := 0;
            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (
                                UN_TABLA    => 'SP_PAGO'
                               ,UN_CRITERIO => '   COMPANIA     = '''||UN_COMPANIA||''' 
                                               AND FECHA        =TO_DATE('''||UN_FECHANUE||''',''DD/MM/YYYY'')'||' 
                                               AND BANCO        ='''||UN_BANCONUE||''' 
                                               AND NUMEROPAQUETE='''||UN_PAQUETENUE||''''
                               ,UN_CAMPO => 'CONSECUTIVO');

            MI_TABLAINSERT  := 'SP_PAGO';

            MI_CAMPOSINSERT :='COMPANIA     , FECHA,      BANCO,      NUMEROPAQUETE, CONSECUTIVO
                              ,CODIGOINTERNO, OPERACION,  VALORPAGO,  USUARIO,       CODIGOBARRAS
                              ,CICLO,         CODIGORUTA, PERATRASO,  HORA,          ANO
                              ,PERIODO,       INDCONTROL, CREATED_BY, DATE_CREATED';

            MI_INDICADOR    := CASE WHEN RS.INDCONTROL IS NULL THEN 0 ELSE RS.INDCONTROL END;                              

            MI_VALORESINSERT:=''''||UN_COMPANIA||'''
                              ,TO_DATE('''|| UN_FECHANUE||''',''DD/MM/YYYY'')'||'
                              ,'''|| UN_BANCONUE||'''
                              ,'''|| UN_PAQUETENUE||'''
                              ,'|| MI_CONSECUTIVO||'
                              ,'''|| RS.CODIGOINTERNO||'''
                              ,'''|| RS.OPERACION||'''
                              ,'|| RS.VALORPAGO||'
                              ,'''|| RS.USUARIO||'''
                              ,'''|| RS.CODIGOBARRAS||'''
                              ,'|| RS.CICLO||'
                              ,'|| RS.CODIGORUTA||'
                              ,'|| NVL(RS.PERATRASO, 0)||'
                              ,SYSDATE'||'
                              ,'|| RS.ANO||'
                              ,'''|| RS.PERIODO||'''
                              ,'|| MI_INDICADOR ||' 
                              , ''' || UN_USUARIO || '''
                              , SYSDATE ';

            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT, 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOSINSERT, 
                                                     UN_VALORES => MI_VALORESINSERT);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                     
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE := 'BANCO';
                MI_MSGERROR (1).VALOR := UN_BANCONUE;
                MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
                MI_MSGERROR (2).VALOR := UN_PAQUETENUE;
                MI_MSGERROR (3).CLAVE := 'FECHA';
                MI_MSGERROR (3).VALOR := TO_CHAR (UN_FECHANUE, 'DD/MM/YYYY');
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_I
                                           ,UN_TABLAERROR => MI_TABLAINSERT
                                           ,UN_REEMPLAZOS => MI_MSGERROR);              
            END;
          END LOOP;

          MI_TABLADELETE    := 'SP_PAGO';
          MI_CONDICIONDELETE:='COMPANIA='''||UN_COMPANIA||
                              ''' AND FECHA=TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'||
                              ' AND BANCO='''||UN_BANCOANT||
                              ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICIONDELETE);
          <<ACTUALIZADRECAUDOS>>
          FOR RS IN (
            SELECT 
              COMPANIA,
              FECHA,
              BANCO,
              NUMEROPAQUETE,
              CONCEPTO,
              VALORDEUDA,
              VALORPAGOPERIODO,
              CREATED_BY,
              DATE_CREATED,
              MODIFIED_BY,
              DATE_MODIFIED,
              VALORFINACT,
              VALORABONOACT,
              VALORABONOANT,
              VALORFINANT,
              CREDITOABONADO
            FROM SP_D_RECAUDO
            WHERE COMPANIA     =UN_COMPANIA
              AND FECHA        =UN_FECHAANT
              AND BANCO        =UN_BANCOANT
              AND NUMEROPAQUETE=UN_PAQUETEANT
            ORDER BY CONCEPTO)
          LOOP
            MI_TABLAMERGE   :='SP_D_RECAUDO';

            MI_MERGEUSING   :='SELECT *'|| CHR(13) || CHR(10) ||
                              ' FROM SP_D_RECAUDO '|| CHR(13) || CHR(10) ||
                              'WHERE COMPANIA   ='''||UN_COMPANIA||''''|| CHR(13) || CHR(10) ||
                              'AND FECHA        =TO_DATE('''||UN_FECHANUE||''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                              'AND BANCO        ='''||UN_BANCONUE||''''|| CHR(13) || CHR(10) ||
                              'AND NUMEROPAQUETE='''||UN_PAQUETENUE||''''|| CHR(13) || CHR(10) ||
                              'AND CONCEPTO     ='''||RS.CONCEPTO||''''|| CHR(13) || CHR(10) ||
                              'ORDER BY CONCEPTO';

            MI_MERGEENLACE  := '    TABLA.COMPANIA      = VISTA.COMPANIA '|| CHR(13) || CHR(10) ||       
                               'AND TABLA.FECHA         = VISTA.FECHA '|| CHR(13) || CHR(10) ||
                               'AND TABLA.BANCO         = VISTA.BANCO '|| CHR(13) || CHR(10) ||
                               'AND TABLA.NUMEROPAQUETE = VISTA.NUMEROPAQUETE '|| CHR(13) || CHR(10) ||      
                               'AND TABLA.CONCEPTO      = VISTA.CONCEPTO ';

            MI_MERGEEXISTE  :=' UPDATE SET VALORDEUDA                                 = VALORDEUDA + '||RS.VALORDEUDA||' 
                                          ,'|| CHR(13) || CHR(10) ||'VALORPAGOPERIODO = VALORPAGOPERIODO + '||RS.VALORPAGOPERIODO||'
                                          ,'|| CHR(13) || CHR(10) ||'VALORFINACT      = VALORFINACT + '||RS.VALORFINACT||'
                                          ,'|| CHR(13) || CHR(10) ||'VALORABONOACT    = VALORABONOACT+'||RS.VALORABONOACT||'
                                          ,'|| CHR(13) || CHR(10) ||'VALORABONOANT    = VALORABONOANT+'||RS.VALORABONOANT||'
                                          ,'|| CHR(13) || CHR(10) ||'VALORFINANT      = VALORFINANT + '||RS.VALORFINANT||'
                                          ,'|| CHR(13) || CHR(10) ||'CREDITOABONADO   = CREDITOABONADO + '||RS.CREDITOABONADO|| CHR(13) || CHR(10) ||'
                                          ,MODIFIED_BY   = ''' || UN_USUARIO ||'''
                                          ,DATE_MODIFIED = SYSDATE ' ||
                                     ' WHERE COMPANIA    ='''||UN_COMPANIA ||''''|| CHR(13) || CHR(10) ||   
                                     '  AND FECHA        =TO_DATE('''||UN_FECHANUE||''',''DD/MM/YYYY'')' || CHR(13) || CHR(10) ||   
                                     '  AND BANCO        ='''||UN_BANCONUE||'''' || CHR(13) || CHR(10) ||   
                                     '  AND NUMEROPAQUETE='''||UN_PAQUETENUE||'''' || CHR(13) || CHR(10) ||   
                                     '  AND CONCEPTO     ='''||RS.CONCEPTO||'''';

            MI_MERGENOEXISTE:=' INSERT (COMPANIA, 
                                        FECHA, 
                                        BANCO, 
                                        NUMEROPAQUETE, 
                                        CONCEPTO, 
                                        VALORDEUDA, 
                                        VALORPAGOPERIODO, 
                                        VALORFINACT, 
                                        VALORABONOACT, 
                                        VALORABONOANT, 
                                        VALORFINANT, 
                                        CREDITOABONADO,
                                        CREATED_BY,
                                        DATE_CREATED)
                                VALUES ('''||RS.COMPANIA||''',TO_DATE('''|| 
                                        UN_FECHANUE||''',''DD/MM/YYYY''),'''|| 
                                        RS.BANCO||''','''|| 
                                        RS.NUMEROPAQUETE||''','''|| 
                                        RS.CONCEPTO||''','|| 
                                        NVL(RS.VALORDEUDA, 0)||','|| 
                                        NVL(RS.VALORPAGOPERIODO, 0)||','|| 
                                        NVL(RS.VALORFINACT, 0)||','|| 
                                        NVL(RS.VALORABONOACT, 0)||','|| 
                                        NVL(RS.VALORABONOANT, 0)||','|| 
                                        NVL(RS.VALORFINANT, 0)||','|| 
                                        NVL(RS.CREDITOABONADO, 0)||','||'
                                        ''' || UN_USUARIO ||''','|| 
                                        'SYSDATE )';

            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLAMERGE, 
                                                     UN_ACCION      => 'IM', 
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                     UN_MERGENOEXIS => MI_MERGENOEXISTE);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                     
              END; 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE := 'BANCO';
                MI_MSGERROR (1).VALOR := UN_BANCONUE;
                MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
                MI_MSGERROR (2).VALOR := UN_PAQUETENUE;
                MI_MSGERROR (3).CLAVE := 'CONCEPTO';
                MI_MSGERROR (3).VALOR := RS.CONCEPTO;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_IM
                                           ,UN_TABLAERROR => MI_TABLAMERGE
                                           ,UN_REEMPLAZOS => MI_MSGERROR);              
            END;
          END LOOP ACTUALIZADRECAUDOS;

          MI_TABLADELETE := 'SP_D_RECAUDO';

          MI_CONDICIONDELETE:=' COMPANIA           ='''||UN_COMPANIA|| 
                              ''' AND FECHA        =TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'|| 
                              ' AND BANCO          ='''||UN_BANCOANT|| 
                              ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                   UN_ACCION    => 'E',
                                                   UN_CONDICION => MI_CONDICIONDELETE);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                       
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := UN_BANCOANT;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := UN_PAQUETEANT;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (UN_FECHAANT, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_E
                                         ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;
        /*  
          MI_TABLADELETE    :='SP_RECAUDOS';
          MI_CONDICIONDELETE:=' COMPANIA='''||UN_COMPANIA||
                              ''' AND FECHA =TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'|| 
                              ' AND BANCO='''||UN_BANCOANT||
                              ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';

          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICIONDELETE);   */                 
        ELSE
          MI_TABLAINSERT    :='SP_RECAUDOS';
          MI_CAMPOSINSERT   :=' COMPANIA, 
                                FECHA, 
                                BANCO, 
                                NUMEROPAQUETE, 
                                BARRAS, 
                                TIPO, 
                                VALORREPORTADO, 
                                CUPONESREPORTADOS, 
                                VALORREGISTRADO, 
                                CUPONESREGISTRADOS, 
                                DIFERENCIACUPONES, 
                                DIFERENCIAVALORES, 
                                USUARIO, 
                                COMENTARIOS,
                                CREATED_BY,
                                DATE_CREATED ';

          MI_VALORESINSERT  :=''''||UN_COMPANIA||''', TO_DATE('''|| 
                                    UN_FECHANUE||''',''DD/MM/YYYY''), '''|| 
                                    UN_BANCONUE||''', '''|| 
                                    UN_PAQUETENUE||''', '''|| 
                                    MI_BARRAS1||''', '''|| 
                                    MI_TIPO1||''', '|| 
                                    MI_VALORREPORTADO1||', '|| 
                                    MI_CUPONESREPORTADOS1||', '|| 
                                    MI_VALORREGISTRADO1||', '||
                                    MI_CUPONESREGISTRADOS1||', '||
                                    MI_DIFERENCIACUPONES1||', '||
                                    MI_DIFERENCIAVALORES1||', '''||
                                    MI_USUARIO1||''', '''||
                                    MI_COMENTARIOS1||' Cambio de fecha'',
                                    ''' || UN_USUARIO || ''', '||
                                    ' SYSDATE';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT, 
                                                    UN_ACCION  => 'I', 
                                                    UN_CAMPOS  => MI_CAMPOSINSERT, 
                                                    UN_VALORES => MI_VALORESINSERT);     

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;              
            END;   

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := UN_BANCONUE;
              MI_MSGERROR (2).CLAVE := 'FECHA';
              MI_MSGERROR (2).VALOR := TO_CHAR (UN_FECHANUE, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_RECA_I
                                         ,UN_TABLAERROR => MI_TABLAINSERT
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

          MI_TABLAUPDATE :='SP_D_RECAUDO';
          MI_CAMPOSUPDATE :='FECHA = TO_DATE('''||UN_FECHANUE|| ''',''DD/MM/YYYY''),
                             BANCO = '''||UN_BANCONUE||''', 
                             NUMEROPAQUETE = '''||UN_PAQUETENUE||''', 
                             MODIFIED_BY = ''' || UN_USUARIO || ''', 
                             DATE_MODIFIED = SYSDATE';
          MI_CONDICIONUPDATE:=' COMPANIA    = '''||UN_COMPANIA||
                              ''' AND FECHA =TO_DATE('''||UN_FECHAANT|| 
                              ''',''DD/MM/YYYY'') AND BANCO ='''||UN_BANCOANT||
                              ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE, 
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                    UN_CONDICION => MI_CONDICIONUPDATE);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                    
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := UN_BANCOANT;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := UN_PAQUETEANT;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (UN_FECHAANT, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_RECA_M
                                         ,UN_TABLAERROR => MI_TABLAUPDATE
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

          MI_TABLADELETE :='SP_D_RECAUDO';

          MI_CONDICIONDELETE :=' COMPANIA='''||UN_COMPANIA||
                               ''' AND FECHA = TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'|| 
                               ''' AND BANCO = '''||UN_BANCOANT||
                               ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                   UN_ACCION    => 'E',
                                                   UN_CONDICION => MI_CONDICIONDELETE);  

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                   
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := UN_BANCOANT;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := UN_PAQUETEANT;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (UN_FECHAANT, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_E
                                         ,UN_TABLAERROR => MI_TABLADELETE
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

          <<ACTUALIZAPAGOS>>
          FOR RS IN (
            SELECT *
            FROM SP_PAGO
            WHERE COMPANIA     = UN_COMPANIA
              AND FECHA        = UN_FECHAANT
              AND BANCO        = UN_BANCOANT
              AND NUMEROPAQUETE= UN_PAQUETEANT)
          LOOP
            MI_TABLAUPDATE    :='SP_PAGO';

            MI_CAMPOSUPDATE   :='    FECHA = TO_DATE('''||UN_FECHANUE||''',''DD/MM/YYYY'')'|| 
                                ''', BANCO = '''||UN_BANCONUE||
                                ''', NUMEROPAQUETE = '''||UN_PAQUETENUE||
                                ''', MODIFIED_BY   = ''' || UN_USUARIO || 
                                ''', DATE_MODIFIED = SYSDATE';

            MI_CONDICIONUPDATE:=' COMPANIA='''||UN_COMPANIA||
                                ''' AND CODIGOINTERNO='''||RS.CODIGOINTERNO||
                                ''' AND FECHA = TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'|| 
                                ''' AND BANCO='''||UN_BANCOANT||
                                ''' AND NUMEROPAQUETE='''||UN_PAQUETEANT||'''';

            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE, 
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                      UN_CONDICION => MI_CONDICIONUPDATE);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                
              END; 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE := 'CODIGOINTERNO';
                MI_MSGERROR (1).VALOR := RS.CODIGOINTERNO;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_PAGO_M
                                           ,UN_TABLAERROR => MI_TABLAUPDATE
                                           ,UN_REEMPLAZOS => MI_MSGERROR);              
            END;

            MI_TABLAUPDATE    :='SP_USUARIO';

            MI_CAMPOSUPDATE   :='''  FECHAPAGOPERPROCESO =TO_DATE('''||UN_FECHANUE||''',''DD/MM/YYYY'')'||
                                ''', BANCOPERPROCESO = '''||UN_BANCONUE|| 
                                ''', PAQUETEPAGOPERPROCESO = '''||UN_PAQUETENUE||
                                ''', MODIFIED_BY  = ''' || UN_USUARIO || 
                                ''', DATE_MODIFIED = SYSDATE ';

            MI_CONDICIONUPDATE:=' COMPANIA='''||UN_COMPANIA|| 
                                ''' AND CODIGOINTERNO='''||RS.CODIGOINTERNO||
                                ''' AND FECHAPAGOPERPROCESO =TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'||
                                ''' AND BANCOPERPROCESO='''||UN_BANCOANT|| 
                                ''' AND PAQUETEPAGOPERPROCESO='''||UN_PAQUETEANT||'''';

            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE, 
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                      UN_CONDICION => MI_CONDICIONUPDATE);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                
              END; 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE := 'CODIGOINTERNO';
                MI_MSGERROR (1).VALOR := RS.CODIGOINTERNO;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_USUA_M
                                           ,UN_TABLAERROR => MI_TABLAUPDATE
                                           ,UN_REEMPLAZOS => MI_MSGERROR); 
            END;
          END LOOP ACTUALIZAPAGOS;

          MI_TABLADELETE :='SP_RECAUDOS';
          MI_CONDICIONDELETE:=' COMPANIA    = '''||UN_COMPANIA|| 
                              ''' AND FECHA = TO_DATE('''||UN_FECHAANT||''',''DD/MM/YYYY'')'|| 
                              ''' AND BANCO   = '''||UN_BANCOANT|| 
                              ''' AND NUMEROPAQUETE = '''||UN_PAQUETEANT||'''';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                   UN_ACCION    => 'E',
                                                   UN_CONDICION => MI_CONDICIONDELETE);                    

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;               
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := UN_BANCOANT;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := UN_PAQUETEANT;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (UN_FECHAANT, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CAMBIARFECHAPAGO_E
                                         ,UN_TABLAERROR => MI_TABLADELETE
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

        END IF;
        ELSE
          MI_RTA:='No existe información para el banco '||UN_BANCOANT||', paquete '||UN_PAQUETEANT||', fecha '||UN_FECHAANT;
        END IF;

        MI_RTA:='Proceso terminado correctamente.';
      END IF;

        RETURN MI_RTA;

/*EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Error al realizar el proceso de cambio de datos de pago.';
    PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'SP_USUARIO','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );      */

  END FC_CAMBIARFECHAPAGO;

  -- 3
  FUNCTION FC_AUDITORIASINLECTURA
  /*
    OBJETIVO              : Realizar la inserción de los campos ingresados por parámetro a la tabla SP_AUDSINLECTURA
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_USUARIO: Nombre del usuario administrador.
                            UN_PROCESO: 
                            UN_CICLO: Número del ciclo del usuario.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : AURA LILIANA MONROY GARCÍA
    FECHA                 : 22/08/2016 05:50 PM
    REALIZADO POR:        : SYSMAN  SAS
    FECHA MODIFICACIÓN    : 30/08/2016 11:26 PM
    LIDER MODIFICACIÓN    : AURA LILIANA MONROY GARCÍA
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : Incluir dentro del procedimiento la consulta de los usuarios con PERIODOLECTURA > 0,
                            también la inserción en la tabla SP_AUDSINLECTURA y la actualización en la tabla SP_USUARIO

    NAME                  : PR_AUDITORIASINLECTURA  --> EN ACCESS AuditoriaSinLectura
    SOURCE MODULE         : SysmanSp2016.05.04            
    @NAME:    insertarAuditoriaSinLectura
    @METHOD:  GET
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO     IN SP_AUDSINLECTURA.USUARIO%TYPE,
    UN_PROCESO     IN SP_AUDSINLECTURA.PROCESO%TYPE,
    UN_CICLO       IN VARCHAR2
  )
  RETURN VARCHAR2
  AS
    MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 2;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_PCKDATOS           NUMBER;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RESULTADO          NUMBER;
    MI_ANO                PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO            PCK_SUBTIPOS.TI_PERIODO;
    MI_PERIODOSINLECTURA  SP_USUARIO.PERIODOSINLECTURA%TYPE;
    MI_CODIGOINTERNO      SP_USUARIO.CODIGOINTERNO%TYPE;
    MI_ACUMULADO          PCK_SUBTIPOS.TI_DOBLE;
    MI_CODIGORUTA_1       PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR; 

    CURSOR RS1 IS 
      SELECT 
        ANO,
        PERIODO,
        PERIODOSINLECTURA,
        CODIGOINTERNO,
        ACUMULADO,
        CODIGORUTA
      FROM SP_USUARIO
      WHERE CICLO             = UN_CICLO
        AND PERIODOSINLECTURA > 0
        AND COMPANIA          = UN_COMPANIA
      ORDER BY CODIGORUTA;

  BEGIN
    OPEN RS1;
    FETCH RS1
    INTO MI_ANO,
      MI_PERIODO,
      MI_PERIODOSINLECTURA,
      MI_CODIGOINTERNO,
      MI_ACUMULADO,
      MI_CODIGORUTA_1;

    IF MI_CODIGORUTA_1 IS NULL THEN
      CLOSE RS1;
      RETURN '0';
    END IF;
    CLOSE RS1;

    OPEN RS1;
    LOOP
      FETCH RS1
      INTO MI_ANO,
        MI_PERIODO,
        MI_PERIODOSINLECTURA,
        MI_CODIGOINTERNO,
        MI_ACUMULADO,
        MI_CODIGORUTA_1;
      EXIT WHEN RS1%NOTFOUND;

      -- EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT = ''DD/MM/YYYY HH24:mi:ss''';
      MI_CAMPOS      := 'SP_AUDSINLECTURA.COMPANIA, 
                        SP_AUDSINLECTURA.USUARIO, 
                        SP_AUDSINLECTURA.PROCESO,
                        SP_AUDSINLECTURA.FECHA, 
                        SP_AUDSINLECTURA.HORA, 
                        SP_AUDSINLECTURA.ANO, 
                        SP_AUDSINLECTURA.PERIODO, 
                        SP_AUDSINLECTURA.CICLO, 
                        SP_AUDSINLECTURA.CODINTERNO,
                        SP_AUDSINLECTURA.CREATED_BY,
                        SP_AUDSINLECTURA.DATE_CREATED';

      MI_VALORES     := ''''||UN_COMPANIA||''', 
                        '''||UN_USUARIO||''', 
                        '''||UN_PROCESO||''', 
                        SYSDATE, 
                        SYSDATE, 
                        '||MI_ANO||' , 
                        '''||MI_PERIODO||''', 
                        '||UN_CICLO||',
                        '''||MI_CODIGOINTERNO||''',
                        '''||UN_USUARIO||''',
                        SYSDATE';                 
      BEGIN
        BEGIN 
          MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_AUDSINLECTURA', 
                                            UN_ACCION  => 'I', 
                                            UN_CAMPOS  => MI_CAMPOS, 
                                            UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
            THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_TABLAERROR => 'SP_AUDSINLECTURA');
      END;

      IF MI_RESULTADO > 0 THEN
         MI_CAMPOS    := ' ACUMULADO = 0, PERIODOSINLECTURA = 0, MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
         MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                           AND CICLO = ' || UN_CICLO|| '
                           AND CODIGORUTA = ''' || MI_CODIGORUTA_1 || '''';       
         BEGIN
           BEGIN
             MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_USUARIO',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                  
           END; 

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
             MI_MSGERROR(1).CLAVE := 'CICLO';
             MI_MSGERROR(1).VALOR := UN_CICLO;
             MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
             MI_MSGERROR(2).VALOR := MI_CODIGORUTA_1;
             CLOSE RS1;
             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_AIL_USU_M,
                                        UN_TABLAERROR => 'SP_USUARIO',
                                        UN_REEMPLAZOS => MI_MSGERROR);
         END;
      END IF;
    END LOOP;   

    CLOSE RS1;

    RETURN '1';
  END FC_AUDITORIASINLECTURA;

  --4
  PROCEDURE PR_RECONSTRUIR_12

   /*
      NAME              : PR_RECONSTRUIR_12 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 24/08/2016
      TIME              : 10:56 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.
      MODIFIER          : YESIKA PAOLA BECERRA CASTRO
      DATE MODIFIED     : 19/07/2017
      TIME MODIFIED     : 09:05 AM
      DESCRIPTION:      : Se cambio nombre de los parametros UN_ESTADO , ESTADO_ANT, por UN_CAMPOACTUAL, UN_CAMPOANTERIOR,
                          debido a que dependiendo de donde se llame la funcion estos campos cambian.                    


      @NAME   :    reasignarConceptoDiferenteDeDoce
      @METHOD :  PUT

    */ 


  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL       IN DATE,
    UN_FECHAFINAL         IN DATE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    MI_ANO                PCK_SUBTIPOS.TI_ANIO;
    MI_PCKDATOS           NUMBER;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    <<ACTUALIZAFACTURADO>>
    FOR RS IN (
      SELECT SP_USUARIO.CODIGORUTA,
        SP_USUARIO.BANCOPERPROCESO,
        SP_USUARIO.FECHAPAGOPERPROCESO,
        SP_FACTURADO.DEUDA,
        SP_FACTURADO.CONCEPTO
      FROM SP_USUARIO
        INNER JOIN SP_FACTURADO
          ON SP_USUARIO.COMPANIA         = SP_FACTURADO.COMPANIA
          AND SP_USUARIO.CICLO           = SP_FACTURADO.CICLO
          AND SP_USUARIO.CODIGORUTA      = SP_FACTURADO.CODIGORUTA
          AND SP_USUARIO.PERIODO         = SP_FACTURADO.PERIODO
          AND SP_USUARIO.ANO             = SP_FACTURADO.ANO          
      WHERE SP_USUARIO.COMPANIA = UN_COMPANIA
        AND SP_USUARIO.BANCOPERPROCESO IS NOT NULL
        AND SP_USUARIO.FECHAPAGOPERPROCESO BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        AND SP_FACTURADO.DEUDA <>0
        AND SP_FACTURADO.CONCEPTO=12)
    LOOP
      FOR RSF IN (
        SELECT SUM(CASE WHEN ((SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48) OR (SP_FACTURADO.CONCEPTO IN(201,202,202,204,205,206,207,246,247,248,249)) AND (SP_FACTURADO.CONCEPTO NOT IN(12,17)))
                        THEN DEUDA
                   END) AS SUM_DEUDA,
               SP_FACTURADO.CODIGORUTA
        FROM SP_FACTURADO
        WHERE COMPANIA  = UN_COMPANIA
          AND CODIGORUTA= RS.CODIGORUTA
        GROUP BY SP_FACTURADO.CODIGORUTA)
      LOOP
        MI_MSGERROR(1).CLAVE := 'CODIGORUTA';
        MI_MSGERROR(1).VALOR := RSF.CODIGORUTA;

        MI_CAMPOS := ' DEUDA          = (CASE WHEN (DEUDA>0 AND CONCEPTO<>12)
                                        THEN (DEUDA+(DEUDA* '|| RS.DEUDA ||')/ '|| RSF.SUM_DEUDA ||')
                                        ELSE 0 END),
                        MODIFIED_BY    = '''||UN_USUARIO||''',
                        DATE_MODIFIED  = SYSDATE' ;         

        MI_CONDICION := ' COMPANIA     = ''' || UN_COMPANIA || '''             
                      AND CODIGORUTA   = ''' || RSF.CODIGORUTA || '''';

        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_FACTURADO'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_RECONSTRUIR_M
                                           ,UN_TABLAERROR => 'SP_FACTURADO'
                                           ,UN_REEMPLAZOS => MI_MSGERROR);          
        END;
      END LOOP ACTUALIZAFACTURADO;
    END LOOP;
  END PR_RECONSTRUIR_12; 

  --5
  PROCEDURE PR_CHARLESPESO

    /*
      NAME              : PR_CHARLESPESO
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 24/08/2016 
      TIME              : 10:56 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Actualizar los valores de SP_D_ABONOS entre dos fechas.
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.



    @NAME:    actualizarDetalladeDeAbonos
    @METHOD:  PUT

    */ 

  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL IN DATE,
    UN_FECHAFINAL   IN DATE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_PCKDATOS           NUMBER;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_MERGEUSING := 'SELECT 
                        SP_D_ABONOS.COMPANIA
                       ,SP_D_ABONOS.CICLO
                       ,SP_D_ABONOS.CODIGORUTA
                       ,SP_D_ABONOS.ANO
                       ,SP_D_ABONOS.PERIODO
                       ,SP_D_ABONOS.CONSECUTIVO
                       ,SP_D_ABONOS.CONCEPTO
                     FROM SP_D_ABONOS
                       INNER JOIN SP_ABONOS
                         ON SP_D_ABONOS.COMPANIA     = SP_ABONOS.COMPANIA
                         AND SP_D_ABONOS.CICLO       = SP_ABONOS.CICLO
                         AND SP_D_ABONOS.CODIGORUTA  = SP_ABONOS.CODIGORUTA
                         AND SP_D_ABONOS.ANO         = SP_ABONOS.ANO
                         AND SP_D_ABONOS.PERIODO     = SP_ABONOS.PERIODO
                         AND SP_D_ABONOS.CONSECUTIVO = SP_ABONOS.CONSECUTIVO
                     WHERE SP_ABONOS.COMPANIA     =''' || UN_COMPANIA ||'''
                       AND TO_DATE(TO_CHAR(SP_ABONOS.FECHA,''DD/MM/YYYY'')) BETWEEN TO_DATE('''|| TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                                                                                AND TO_DATE('''|| TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')';
    MI_MERGEENLACE:='TABLA.COMPANIA    =  VISTA.COMPANIA AND
                     TABLA.CICLO       =  VISTA.CICLO AND
                     TABLA.CODIGORUTA  =  VISTA.CODIGORUTA AND
                     TABLA.ANO         =  VISTA.ANO AND
                     TABLA.PERIODO     =  VISTA.PERIODO AND
                     TABLA.CONSECUTIVO =  VISTA.CONSECUTIVO AND
                     TABLA.CONCEPTO    =  VISTA.CONCEPTO';

    MI_MERGEEXISTE := 'UPDATE SET VALOR          = ROUND(VALORACT,0)+ROUND(VALORANT,0),
                                  VALORACT       = ROUND(VALORACT,0),
                                  VALORANT       = ROUND(VALORANT,0),
                                  MODIFIED_BY    = '''||UN_USUARIO||''',
                                  DATE_MODIFIED  = SYSDATE' ;   
    BEGIN                              
      BEGIN
        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_D_ABONOS', 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                            
      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'FECHAINICIAL';
          MI_MSGERROR(1).VALOR := UN_FECHAINICIAL;
          MI_MSGERROR(2).CLAVE := 'FECHAFINAL';
          MI_MSGERROR(2).CLAVE := UN_FECHAFINAL;          
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_MM_D_ABONOS_CHARLESPESO,
                                     UN_TABLAERROR => 'SP_D_ABONOS',
                                     UN_REEMPLAZOS => MI_MSGERROR);
    END;

    <<ACTUALIZA_D_ABONOS>>
    FOR RS IN (
      SELECT 
        SP_ABONOS.COMPANIA,
        SP_ABONOS.CONSECUTIVO,
        SP_ABONOS.FECHA,
        SP_ABONOS.VALOR,
        SUM(SP_D_ABONOS.VALORANT+SP_D_ABONOS.VALORACT) AS SUMADA,
        SP_ABONOS.VALOR - SUM(SP_D_ABONOS.VALORANT+SP_D_ABONOS.VALORACT) AS DIFSUMA
      FROM SP_ABONOS
        INNER JOIN SP_D_ABONOS
          ON  SP_ABONOS.COMPANIA   = SP_D_ABONOS.COMPANIA
          AND SP_ABONOS.CICLO      = SP_D_ABONOS.CICLO
          AND SP_ABONOS.CODIGORUTA = SP_D_ABONOS.CODIGORUTA
          AND SP_ABONOS.ANO        = SP_D_ABONOS.ANO
          AND SP_ABONOS.PERIODO    = SP_D_ABONOS.PERIODO
          AND SP_ABONOS.CONSECUTIVO= SP_D_ABONOS.CONSECUTIVO
      WHERE SP_ABONOS.COMPANIA  = UN_COMPANIA
        AND TO_DATE(TO_CHAR(SP_ABONOS.FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
      GROUP BY 
        SP_ABONOS.COMPANIA,
        SP_ABONOS.CONSECUTIVO,
        SP_ABONOS.FECHA,
        SP_ABONOS.VALOR)
    LOOP 
      MI_CAMPOS := ' VALORANT = (CASE WHEN VALORANT <> 0
                                      THEN (VALORANT + ' || RS.DIFSUMA || ')
                                      ELSE VALORANT 
                                 END),
                   VALORACT      = (CASE WHEN VALORANT <> 0
                                    THEN VALORACT
                                    ELSE (VALORACT + ' || RS.DIFSUMA || ' )
                                    END),
                   MODIFIED_BY   = '''||UN_USUARIO||''',
                   DATE_MODIFIED = SYSDATE'; 
      MI_CONDICION := ' COMPANIA   = ''' || UN_COMPANIA || '''             
                      AND CONCEPTO = (SELECT MIN(CONCEPTO)
                                      FROM SP_D_ABONOS
                                      WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                                        AND CONSECUTIVO = ' || RS.CONSECUTIVO || ')';
      BEGIN
        BEGIN
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_ABONOS',
                                           UN_ACCION    => 'M', 
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_CONDICION => MI_CONDICION);  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                            
        END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONCEPTO';
          MI_MSGERROR(1).VALOR := RS.CONSECUTIVO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CHARLESPESO_D_ABONOS_I,
                                     UN_TABLAERROR => 'SP_D_ABONOS',
                                     UN_REEMPLAZOS => MI_MSGERROR);
      END;
    END LOOP ACTUALIZA_D_ABONOS;
  END PR_CHARLESPESO;

  --6
  FUNCTION FC_ACTUALIZAAUDI

     /*
      NAME              : FC_ACTUALIZAAUDI  --> EN ACCESS ActualizaAcudi
      AUTHORS           : SYSMAN SAS
      AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
      DATE MIGRADO      : 25/08/2016 
      TIME              : 12:36 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Inserta en la tabla SP_AUDITORIA_USUARIO
      PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                              UN_CICLO: Número del ciclo.
                              UN_CODIGO: Código de ruta del usuario.
                              UN_VALFINAL: Indicador de chapeta.
                              UN_VALINICIAL: Indicador de chapeta anterior.
                              UN_CAMPOACTUAL: Campo afectado, para insecion en la tabla
                              UN_CAMPOANTERIOR: Campo antes de ser afectado, para insercion en la tabla 
                              UN_PERIODO: Código del mes.
                              UN_USUARIO : Usuario por el cual se ingresa en la aplicacion
     MODIFIER          : YESIKA PAOLA BECERRA CASTRO
     DATE MODIFIED     : 19/07/2017
     TIME MODIFIED     : 09:05 AM
     DESCRIPTION:      : Se cambio nombre de los parametros UN_ESTADO , ESTADO_ANT, por UN_CAMPOACTUAL, UN_CAMPOANTERIOR,
                          debido a que dependiendo de donde se llame la funcion estos campos cambian.                                


    @NAME:    insertarAuditoriaDelUsuario
    @METHOD:  GET

    */ 
   (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO, 
    UN_CODIGO           IN PCK_SUBTIPOS.TI_CODIGORUTA, 
    UN_VALFINAL         IN VARCHAR2, 
    UN_VALINICIAL       IN VARCHAR2, 
    UN_CAMPOACTUAL      IN VARCHAR2,
    UN_CAMPOANTERIOR    IN VARCHAR2,
    UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER
  AS 
    MI_ACTUALIZAAUDI   PCK_SUBTIPOS.TI_LOGICO;
    MI_STRSQL          PCK_SUBTIPOS.TI_STRSQL; 
    MI_CONS            SP_AUDITORIA_USUARIO.CONSECUTIVO%TYPE;
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS; 
    MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN 
    IF UN_VALFINAL = UN_VALINICIAL THEN 
      RETURN MI_ACTUALIZAAUDI; 
    END IF; 

    MI_CONS := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA      => 'SP_AUDITORIA_USUARIO'
                                                ,UN_CRITERIO  => ' COMPANIA       = '''||UN_COMPANIA||''' 
                                                                  AND CICLO       =   '||UN_CICLO||'
                                                                  AND CODIGORUTA  = '''||UN_CODIGO||''''
                                                ,UN_CAMPO     => 'CONSECUTIVO'
                                                ,UN_INICIAL   => '1');


    MI_CAMPOS:='COMPANIA,CICLO,CODIGORUTA,CONSECUTIVO,'||UN_CAMPOACTUAL||','||UN_CAMPOANTERIOR||',DATE_CREATED,ANO,PERIODO,CREATED_BY';
    MI_VALORES:=''''||UN_COMPANIA||''',
                '''||UN_CICLO ||''',
                '''||UN_CODIGO||''',
                '||MI_CONS||',
                '''||UN_VALFINAL||''',
                '''||UN_VALINICIAL||''', 
                SYSDATE, 
                TO_CHAR(SYSDATE, ''YYYY''), 
                '''||UN_PERIODO||''',
                '''||UN_USUARIO||'''';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_AUDITORIA_USUARIO',
                                              UN_ACCION  => 'I',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
      END; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CICLO';
        MI_MSGERROR(1).VALOR := UN_CICLO;
        MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
        MI_MSGERROR(2).VALOR := UN_CODIGO;
        MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
        MI_MSGERROR(3).VALOR := MI_CONS;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SP_ACTUALIZAAUDI_AUD_USU_I,
                                   UN_TABLAERROR => 'SP_AUDITORIA_USUARIO',
                                   UN_REEMPLAZOS => MI_MSGERROR);
    END;
    MI_ACTUALIZAAUDI:=-1;      

    RETURN MI_ACTUALIZAAUDI;
  END;  

  --7
  PROCEDURE PR_CHARLESPRESORECAUDOS
   /*
      NAME              : PR_CHARLESPRESORECAUDOS 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 25/08/2016
      TIME              : 09:53 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.

      @NAME   :  actualizarDetalleDeRecaudo
      @METHOD :  PUT

    */ 

  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL IN DATE,
    UN_FECHAFINAL   IN DATE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_PCKDATOS           NUMBER;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_CAMPOS := ' VALORDEUDA        = ROUND(VALORDEUDA), 
                   VALORPAGOPERIODO  = ROUND(VALORPAGOPERIODO), 
                   VALORFINACT       = ROUND(VALORFINACT), 
                   VALORFINANT       = ROUND(VALORFINANT), 
                   VALORABONOACT     = ROUND(VALORABONOACT), 
                   VALORABONOANT     = ROUND(VALORABONOANT), 
                   CREDITOABONADO    = ROUND(CREDITOABONADO),
                   MODIFIED_BY       = '''||UN_USUARIO||''',
                   DATE_MODIFIED     = SYSDATE  ';              

    MI_CONDICION := '   COMPANIA     = ''' || UN_COMPANIA || '''             
                    AND TO_DATE(TO_CHAR(FECHA,''DD/MM/YYYY'')) BETWEEN TO_DATE('''|| TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                                                                   AND TO_DATE('''|| TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')';                                  
    BEGIN
      BEGIN
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO', 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION);   

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'FECHAINICIAL';
        MI_MSGERROR(1).VALOR := UN_FECHAINICIAL;
        MI_MSGERROR(2).CLAVE := 'FECHAFINAL';
        MI_MSGERROR(2).VALOR := UN_FECHAFINAL;
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CHARLES_PRE_REC_D_REC_I
                                   ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                   ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    <<ACTUALIZADRECAUDOS>>
    FOR RS IN (
      SELECT 
        SP_D_RECAUDO.COMPANIA,
        SP_D_RECAUDO.FECHA,
        SP_D_RECAUDO.BANCO,
        SP_D_RECAUDO.NUMEROPAQUETE,
        QRYSUMAPAGOS.SUMADEVALORPAGO,
        SUM(VALORDEUDA + VALORPAGOPERIODO) AS RECTOT,
        SUMADEVALORPAGO - SUM(VALORDEUDA +VALORPAGOPERIODO) AS DIFSUMA
      FROM SP_D_RECAUDO
        INNER JOIN (SELECT 
                      COMPANIA
                     ,FECHA
                     ,BANCO
                     ,NUMEROPAQUETE
                     ,SUM(VALORPAGO) AS SUMADEVALORPAGO
                    FROM SP_PAGO
                    GROUP BY 
                      COMPANIA,
                      FECHA,
                      BANCO,
                      NUMEROPAQUETE) QRYSUMAPAGOS
          ON SP_D_RECAUDO.COMPANIA       = QRYSUMAPAGOS.COMPANIA
          AND SP_D_RECAUDO.FECHA         = QRYSUMAPAGOS.FECHA
          AND SP_D_RECAUDO.BANCO         = QRYSUMAPAGOS.BANCO
          AND SP_D_RECAUDO.NUMEROPAQUETE = QRYSUMAPAGOS.NUMEROPAQUETE
      WHERE SP_D_RECAUDO.COMPANIA     = UN_COMPANIA
        AND TO_DATE(TO_CHAR(SP_D_RECAUDO.FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        AND (SP_D_RECAUDO.CONCEPTO BETWEEN 1 AND 49 
          OR SP_D_RECAUDO.CONCEPTO IN (247, 249))
      GROUP BY 
        SP_D_RECAUDO.COMPANIA,
        SP_D_RECAUDO.FECHA,
        SP_D_RECAUDO.BANCO,
        SP_D_RECAUDO.NUMEROPAQUETE,
        QRYSUMAPAGOS.SUMADEVALORPAGO)
    LOOP
      IF RS.DIFSUMA <> 0 AND RS.DIFSUMA > -500 AND RS.DIFSUMA < 500 THEN
        MI_CAMPOS := ' VALORDEUDA       = (CASE WHEN VALORDEUDA <> 0
                                           THEN (VALORDEUDA + ' || RS.DIFSUMA || ')
                                           ELSE VALORDEUDA 
                                           END) ,
                       VALORPAGOPERIODO  = (CASE WHEN VALORDEUDA <> 0
                                           THEN VALORPAGOPERIODO
                                           ELSE (VALORPAGOPERIODO + ' || RS.DIFSUMA || ' )
                                           END),
                       MODIFIED_BY       = '''||UN_USUARIO||''',
                       DATE_MODIFIED     = SYSDATE  '; 
        MI_CONDICION := '   COMPANIA        = ''' || UN_COMPANIA || '''
                        AND FECHA         = TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                        AND BANCO         =''' || RS.BANCO || ''' 
                        AND NUMEROPAQUETE =''' || RS.NUMEROPAQUETE || '''
                        AND CONCEPTO = (SELECT MIN(CONCEPTO)
                                        FROM SP_D_RECAUDO 
                                        WHERE COMPANIA      =''' || UN_COMPANIA || '''
                                          AND FECHA         = TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                                          AND BANCO         ='''|| RS.BANCO || ''' 
                                          AND NUMEROPAQUETE ='''|| RS.NUMEROPAQUETE ||''')';               

        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                      THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                    
          END; 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                         MI_MSGERROR (1).CLAVE := 'FECHA';
                         MI_MSGERROR (1).VALOR := RS.FECHA;
                         MI_MSGERROR (2).CLAVE := 'BANCO';
                         MI_MSGERROR (2).VALOR := RS.BANCO;
                         MI_MSGERROR (3).CLAVE := 'NUMEROPAQUETE';
                         MI_MSGERROR (3).VALOR := RS.NUMEROPAQUETE;
                         PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CHARLES_PRE_REC_D_REC_M
                                                    ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END IF;
    END LOOP ACTUALIZADRECAUDOS;
  END PR_CHARLESPRESORECAUDOS;

  --8  
  PROCEDURE PR_RECONSTRUIRD_RECAUDO
  /*
      NAME              : PR_RECONSTRUIRD_RECAUDO --> EN ACCESS ReconstruirD_Recaudo
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 29/08/2016
      TIME              : 08:00 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.

      @NAME:    reasignarDetalleDelRecaudo
      @METHOD:  GET

    */ 
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL       IN DATE,
    UN_FECHAFINAL         IN DATE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS                 NUMBER;
    MI_SIGUIENTE                BOOLEAN;
    MI_SUMAAUX                  PCK_SUBTIPOS.TI_DOBLE;
    MI_DEUDAAUX                 PCK_SUBTIPOS.TI_DOBLE;
    MI_BL247                    BOOLEAN;
    MI_INTNUMCONCEPTOS          PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_BL247 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE' 
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS 
                                         ,UN_FECHA_PAR => SYSDATE)
                   ,'NO'
                ) = 'SI';

    MI_INTNUMCONCEPTOS := NVL (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'NUMERO MAXIMO CONCEPTOS' 
                                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS 
                                                    ,UN_FECHA_PAR => SYSDATE),0);

    IF MI_INTNUMCONCEPTOS = 0 THEN
      MI_INTNUMCONCEPTOS := 250;
    END IF;

    MI_CONDICION:='   COMPANIA='''||UN_COMPANIA|| ''' 
                  AND TO_DATE(TO_CHAR(FECHA,''DD/MM/YYYY'')) BETWEEN TO_DATE('''||TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY')||''',''DD/MM/YYYY'') 
                                                             AND TO_DATE('''||TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY')||''',''DD/MM/YYYY'') ';

    BEGIN
      BEGIN
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                         UN_ACCION    => 'E',
                                         UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                  THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR (1).CLAVE:= 'FECHAINICIAL';
        MI_MSGERROR (1).VALOR:= UN_FECHAINICIAL;
        MI_MSGERROR (2).CLAVE:= 'FECHAFINAL';
        MI_MSGERROR (2).VALOR:= UN_FECHAFINAL;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUDO_E,
                                   UN_TABLAERROR => 'SP_D_RECAUDO',
                                   UN_REEMPLAZOS => MI_MSGERROR);
    END;

    <<INSERTADRECAUDOS>>
    FOR RS IN (
      SELECT 
        SP_PAGO.COMPANIA,
        SP_PAGO.FECHA,
        SP_PAGO.BANCO,
        SP_PAGO.NUMEROPAQUETE,
        SP_FACTURADO.CONCEPTO,
        SUM (SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT)  SUMADEVALOR_FACTURADO,
        SUM (SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT)  SUMADEDEUDA,
        SUM (SP_FACTURADO.VALOR_FACTURADOANT) SUMADEVALOR_FACTURADOANT,
        SUM (SP_FACTURADO.DEUDAANT) SUMADEDEUDAANT,
        SUM (SP_FACTURADO.CREDITOABONADO) SUMADECREDITOABONADO,
        SUM (SP_FACTURADO.VALORFINANT) SUMAVALORFINANT,
        SUM (SP_FACTURADO.VALORFINACT) SUMAVALORFINACT,
        SUM (SP_FACTURADO.VALORABONOANT) SUMAVALORABONOANT,
        SUM (SP_FACTURADO.VALORABONOACT) SUMAVALORABONOACT,
        SP_PAGO.ANO PANO,
        SP_PAGO.PERIODO PPERIODO,
        SP_PAGO.CICLO,
        SP_CICLO.ANO CANO,
        SP_CICLO.PERIODO CPERIODO,
        SP_PAGO.OPERACION
      FROM SP_PAGO
        INNER JOIN SP_FACTURADO
          ON SP_PAGO.COMPANIA    = SP_FACTURADO.COMPANIA
          AND SP_PAGO.CICLO      = SP_FACTURADO.CICLO
          AND SP_PAGO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
        INNER JOIN SP_CICLO
          ON SP_PAGO.CICLO     = SP_CICLO.NUMERO
          AND SP_PAGO.COMPANIA = SP_CICLO.COMPANIA
        INNER JOIN SP_USUARIO
          ON SP_FACTURADO.COMPANIA   = SP_USUARIO.COMPANIA
          AND SP_FACTURADO.CICLO     = SP_USUARIO.CICLO
          AND SP_FACTURADO.CODIGORUTA= SP_USUARIO.CODIGORUTA
          AND SP_FACTURADO.PERIODO   = SP_USUARIO.PERIODO
          AND SP_FACTURADO.ANO       = SP_USUARIO.ANO
      WHERE SP_PAGO.COMPANIA         = UN_COMPANIA
        AND SP_FACTURADO.CONCEPTO BETWEEN 1 AND 249
        AND TO_DATE (TO_CHAR (SP_PAGO.FECHA, 'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        AND SP_USUARIO.ANO IS NOT NULL
        AND SP_PAGO.OPERACION<>'D'
      GROUP BY 
        SP_PAGO.COMPANIA,
        SP_PAGO.FECHA,
        SP_PAGO.BANCO,
        SP_PAGO.NUMEROPAQUETE,
        SP_FACTURADO.CONCEPTO,
        SP_PAGO.ANO,
        SP_PAGO.PERIODO,
        SP_PAGO.CICLO,
        SP_CICLO.ANO,
        SP_CICLO.PERIODO,
        SP_PAGO.OPERACION)
    LOOP
      MI_SIGUIENTE := FALSE;

      IF RS.CONCEPTO = 247 THEN
        IF (RS.OPERACION = '2' AND NOT MI_BL247) OR MI_BL247 THEN
          MI_CAMPOS :='COMPANIA
                      , FECHA
                      , BANCO
                      , NUMEROPAQUETE
                      , CONCEPTO
                      , VALORPAGOPERIODO
                      , VALORDEUDA
                      , CREDITOABONADO
                      , VALORFINANT
                      , VALORFINACT
                      , VALORABONOANT
                      , VALORABONOACT
                      , CREATED_BY
                      , DATE_CREATED ';

          MI_VALORES:=''''||UN_COMPANIA||'''
                      ,TO_DATE ('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')'||'
                      ,'''|| RS.BANCO||'''
                      ,'''|| RS.NUMEROPAQUETE||'''
                      ,'|| RS.CONCEPTO||'
                      ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                THEN NVL (RS.SUMADEVALOR_FACTURADO,0) 
                                ELSE NVL (RS.SUMADEVALOR_FACTURADOANT,0) 
                           END||'
                      ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                THEN NVL (RS.SUMADEDEUDA,0) 
                                ELSE NVL (RS.SUMADEDEUDAANT,0) 
                           END||'
                      ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                THEN NVL (RS.SUMADECREDITOABONADO,0) 
                                ELSE 0 
                           END||'
                      ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL (RS.PPERIODO,0) 
                                THEN NVL (RS.SUMAVALORFINANT,0) 
                                ELSE 0 
                           END||'
                      ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL (RS.PPERIODO,0) 
                                THEN NVL (RS.SUMAVALORFINACT,0) 
                                ELSE 0 
                           END||'
                      ,0
                      ,0,'''||UN_USUARIO||''',SYSDATE';
          BEGIN
            BEGIN
              MI_PCKDATOS:= PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_D_RECAUDO', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                             
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUDO_I,
                                         UN_TABLAERROR => 'SP_D_RECAUDO');
          END;

          IF MI_PCKDATOS > 0 THEN
            MI_CAMPOS := 'VALORDEUDA       = VALORDEUDA + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                                                    THEN NVL(RS.SUMADEDEUDA,0) 
                                                                    ELSE NVL(RS.SUMADEDEUDAANT,0) 
                                                               END || ', 
                         VALORPAGOPERIODO  = VALORPAGOPERIODO + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                                                          THEN NVL(RS.SUMADEVALOR_FACTURADO,0) 
                                                                          ELSE NVL(RS.SUMADEVALOR_FACTURADOANT,0) 
                                                                     END || ' ,
                         CREDITOABONADO    = CREDITOABONADO + ' || NVL(RS.SUMADECREDITOABONADO,0) || ',
                         VALORFINANT       = VALORFINANT + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL(RS.PPERIODO,0) 
                                                                     THEN NVL(RS.SUMAVALORFINANT,0) 
                                                                     ELSE 0 
                                                                END || ',
                         VALORFINACT       = VALORFINACT + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL(RS.PPERIODO,0) 
                                                                     THEN NVL(RS.SUMAVALORFINACT,0) 
                                                                     ELSE 0 
                                                                END||',
                         MODIFIED_BY       = '''||UN_USUARIO||''',
                         DATE_MODIFIED     = SYSDATE   ' ; 

            MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                            AND FECHA         = TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO         =''' || RS.BANCO || ''' 
                            AND NUMEROPAQUETE =''' || RS.NUMEROPAQUETE || '''
                            AND CONCEPTO      = '|| RS.CONCEPTO ;               
            BEGIN
              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);  

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;     
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                MI_MSGERROR(1).CLAVE:='BANCO';
                MI_MSGERROR(1).VALOR:=RS.BANCO;
                MI_MSGERROR(2).CLAVE:='NUMEROPAQUETE';
                MI_MSGERROR(2).VALOR:=RS.NUMEROPAQUETE;
                MI_MSGERROR(3).CLAVE:='CONCEPTO';
                MI_MSGERROR(3).VALOR:=RS.CONCEPTO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUDO_M,
                                           UN_TABLAERROR => 'SP_D_RECAUDO',
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;
          END IF;
        END IF;
      ELSE
        IF RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO THEN
          IF    NVL (RS.SUMADEVALOR_FACTURADO,0) = 0 
            AND NVL (RS.SUMADEDEUDA,0) = 0 
            AND NVL (RS.SUMADECREDITOABONADO,0) = 0 
            AND NVL (RS.SUMAVALORFINANT,0) = 0 
            AND NVL (RS.SUMAVALORFINACT,0) = 0 
          THEN
            MI_SIGUIENTE := TRUE;
          END IF;

        ELSE
          IF NVL(RS.SUMADEVALOR_FACTURADOANT,0) = 0 AND NVL(RS.SUMADEDEUDAANT,0) = 0 THEN
            MI_SIGUIENTE := TRUE;
          END IF;
        END IF;  

        IF NOT MI_SIGUIENTE THEN
          MI_CAMPOS :='COMPANIA, 
                      FECHA, 
                      BANCO, 
                      NUMEROPAQUETE, 
                      CONCEPTO, 
                      VALORPAGOPERIODO, 
                      VALORDEUDA,
                      CREDITOABONADO,
                      VALORFINANT,
                      VALORFINACT,
                      VALORABONOANT,
                      VALORABONOACT, 
                      CREATED_BY,
                      DATE_CREATED';

          MI_VALORES:= ''''||UN_COMPANIA||'''
                       ,TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'')'||'
                       ,'''|| RS.BANCO||'''
                       ,'''|| RS.NUMEROPAQUETE||'''
                       ,'|| RS.CONCEPTO||'
                       ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                 THEN NVL (RS.SUMADEVALOR_FACTURADO,0) 
                                 ELSE NVL (RS.SUMADEVALOR_FACTURADOANT,0) 
                            END||'
                       ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                 THEN NVL (RS.SUMADEDEUDA,0) 
                                 ELSE NVL (RS.SUMADEDEUDAANT,0) 
                            END||'
                       ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                 THEN NVL (RS.SUMADECREDITOABONADO,0) 
                                 ELSE 0 
                            END||'
                       ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL (RS.PPERIODO,0) 
                                 THEN NVL (RS.SUMAVALORFINANT,0) 
                                 ELSE 0 
                            END||'
                       ,'|| CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL (RS.PPERIODO,0) 
                                 THEN NVL (RS.SUMAVALORFINACT,0) 
                                 ELSE 0 
                            END||'
                       ,0
                       ,0,
                       '''||UN_USUARIO||''',
                       SYSDATE';
          BEGIN
            BEGIN            
              MI_PCKDATOS:= PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_D_RECAUDO', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                 
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUDO_I,
                                         UN_TABLAERROR => 'SP_D_RECAUDO');
          END;

          IF MI_PCKDATOS > 0 THEN
            MI_CAMPOS := 'VALORDEUDA       = VALORDEUDA + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                                                    THEN NVL(RS.SUMADEDEUDA,0) 
                                                                    ELSE NVL(RS.SUMADEDEUDAANT,0) 
                                                                END || ', 
                         VALORPAGOPERIODO  = VALORPAGOPERIODO + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = RS.PPERIODO 
                                                                          THEN NVL(RS.SUMADEVALOR_FACTURADO,0) 
                                                                          ELSE NVL(RS.SUMADEVALOR_FACTURADOANT,0) 
                                                                     END || ' ,
                         CREDITOABONADO    = CREDITOABONADO + ' || NVL(RS.SUMADECREDITOABONADO,0) || ',
                         VALORFINANT       = VALORFINANT + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL(RS.PPERIODO,0) 
                                                                     THEN NVL(RS.SUMAVALORFINANT,0) 
                                                                     ELSE 0 
                                                                END || ',
                         VALORFINACT       = VALORFINACT + ' || CASE WHEN RS.CANO = RS.PANO AND RS.CPERIODO = NVL(RS.PPERIODO,0) 
                                                                     THEN NVL(RS.SUMAVALORFINACT,0) 
                                                                     ELSE 0 
                                                                END||',
                         MODIFIED_BY       = '''||UN_USUARIO||''',
                         DATE_MODIFIED     = SYSDATE' ; 


            MI_CONDICION := ' COMPANIA        = ''' || UN_COMPANIA || '''
                            AND FECHA         = TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO         =''' || RS.BANCO || ''' 
                            AND NUMEROPAQUETE =''' || RS.NUMEROPAQUETE || '''
                            AND CONCEPTO      = '|| RS.CONCEPTO ;               

            BEGIN
              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION); 

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                 
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE:='BANCO';
                MI_MSGERROR (1).VALOR:=RS.BANCO;
                MI_MSGERROR (2).CLAVE:='NUMEROPAQUETE';
                MI_MSGERROR (2).VALOR:=RS.NUMEROPAQUETE;
                MI_MSGERROR (3).CLAVE:='CONCEPTO';
                MI_MSGERROR (3).VALOR:=RS.CONCEPTO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUDO_M,
                                           UN_TABLAERROR => 'SP_D_RECAUDO',
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;
          END IF;
        END IF;
      END IF;
    END LOOP INSERTADRECAUDOS;

    IF NVL(PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA  => UN_COMPANIA
                                 ,UN_NOMBRE    => 'CIERRE DE PERIODO DISCRIMINADO'
                                 ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                 ,UN_FECHA_PAR => SYSDATE)
          ,'NO'
       ) = 'NO' 
    THEN
      IF NVL (PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA => UN_COMPANIA
                                    ,UN_NOMBRE => 'REVISA RECAUDO 49'
                                    ,UN_MODULO => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                    ,UN_FECHA_PAR => SYSDATE)
             ,'NO'
         ) = 'SI' 
      THEN
        <<ACTUALIZADRECAUDOS>>
        FOR RSCNSUSUARIO IN (
          SELECT 
            COMPANIA,
            FECHA,
            BANCO,
            NUMEROPAQUETE,
            VALORPAGOPERIODO
          FROM SP_D_RECAUDO 
          WHERE COMPANIA = UN_COMPANIA
            AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
            AND CONCEPTO = 49
            AND VALORPAGOPERIODO<>0)
        LOOP
          SELECT SUM (VALORPAGOPERIODO)
          INTO MI_SUMAAUX
          FROM SP_D_RECAUDO
          WHERE COMPANIA     = UN_COMPANIA
            AND FECHA        = RSCNSUSUARIO.FECHA
            AND BANCO        = RSCNSUSUARIO.BANCO
            AND NUMEROPAQUETE= RSCNSUSUARIO.NUMEROPAQUETE
            AND ((CONCEPTO BETWEEN 1 AND 48
              OR CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
              AND CONCEPTO NOT IN (49))
            AND VALORPAGOPERIODO<>0
          GROUP BY 
            COMPANIA,
            FECHA,
            BANCO,
            NUMEROPAQUETE;

          MI_CAMPOS := 'VALORPAGOPERIODO = VALORPAGOPERIODO +
                                           ROUND((VALORPAGOPERIODO * ' || RSCNSUSUARIO.VALORPAGOPERIODO || ')/' || MI_SUMAAUX || ',0),
                        MODIFIED_BY      = '''||UN_USUARIO||''',
                        DATE_MODIFIED    = SYSDATE  ';

          MI_CONDICION := ' COMPANIA        = ''' || UN_COMPANIA || '''
                          AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                          AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                          AND NUMEROPAQUETE =''' || RSCNSUSUARIO.NUMEROPAQUETE || '''';
          BEGIN
            BEGIN                
              MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION); 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;               
            END;

            MI_CAMPOS := 'VALORPAGOPERIODO = 0 ,
                          MODIFIED_BY      = '''||UN_USUARIO||''',
                          DATE_MODIFIED    = SYSDATE  ';

            MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                            AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                            AND NUMEROPAQUETE = ''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                            AND CONCEPTO      = 49 ';

            BEGIN                
              MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);   

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;               
            END;                               

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'BANCO';
              MI_MSGERROR(1).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR(2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR(2).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              MI_MSGERROR(3).CLAVE := 'FECHA';
              MI_MSGERROR(3).VALOR := RSCNSUSUARIO.FECHA;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_M,
                                         UN_TABLAERROR => 'SP_D_RECAUDO',
                                         UN_REEMPLAZOS => MI_MSGERROR);
          END;
        END LOOP ACTUALIZADRECAUDOS;
      END IF;

      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                   ,UN_NOMBRE    => 'REVISA RECAUDO 12' 
                                   ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS 
                                   ,UN_FECHA_PAR => SYSDATE)
             ,'NO'
            ) = 'SI' 
      THEN
        <<ACTUALIZADRECAUDOS>>
        FOR RSCNSUSUARIO IN (
            SELECT 
              COMPANIA,
              FECHA,
              BANCO,
              NUMEROPAQUETE,
              VALORPAGOPERIODO
            FROM SP_D_RECAUDO
            WHERE COMPANIA = UN_COMPANIA
              AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
              AND CONCEPTO =12
              AND VALORPAGOPERIODO<>0)
        LOOP
          BEGIN
            BEGIN
              SELECT SUM(VALORPAGOPERIODO)
              INTO MI_SUMAAUX
              FROM SP_D_RECAUDO
              WHERE COMPANIA     = UN_COMPANIA
                AND FECHA        = RSCNSUSUARIO.FECHA
                AND BANCO        = RSCNSUSUARIO.BANCO
                AND NUMEROPAQUETE= RSCNSUSUARIO.NUMEROPAQUETE
                AND (CONCEPTO BETWEEN 1 AND 48
                  OR CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                AND CONCEPTO NOT IN (12)
                AND VALORPAGOPERIODO<>0
              GROUP BY 
                COMPANIA,
                FECHA,
                BANCO,
                NUMEROPAQUETE;

              EXCEPTION WHEN NO_DATA_FOUND THEN
                --MI_SUMAAUX := NULL;
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (RSCNSUSUARIO.FECHA, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUD_NDF
                                         ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

          IF MI_SUMAAUX IS NOT NULL THEN
            MI_CAMPOS := 'VALORPAGOPERIODO = VALORPAGOPERIODO +
                                             ROUND((VALORPAGOPERIODO * ' || RSCNSUSUARIO.VALORPAGOPERIODO || ')/' || MI_SUMAAUX || ',0),
                          MODIFIED_BY      = '''||UN_USUARIO||''',
                          DATE_MODIFIED    = SYSDATE  ';

            MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                            AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                            AND NUMEROPAQUETE =''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                            AND ((CONCEPTO BETWEEN 1 AND 48
                              OR  CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
                              AND CONCEPTO NOT IN (12))';

            BEGIN
              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                MI_MSGERROR(1).CLAVE := 'BANCO';
                MI_MSGERROR(1).VALOR := RSCNSUSUARIO.BANCO;
                MI_MSGERROR(2).CLAVE := 'NUMEROPAQUETE';
                MI_MSGERROR(2).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
                MI_MSGERROR(3).CLAVE := 'FECHA';
                MI_MSGERROR(3).VALOR := RSCNSUSUARIO.FECHA;                
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_M,
                                           UN_TABLAERROR => 'SP_D_RECAUDO',
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;
          ELSE
            MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA || '''
                            AND FECHA            = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO            =''' || RSCNSUSUARIO.BANCO || ''' 
                            AND NUMEROPAQUETE    =''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                            AND VALORPAGOPERIODO = 0
                            AND CONCEPTO NOT IN (12)';

            BEGIN                
              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'SP_D_RECAUDO',UN_ACCION => 'E',UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                MI_MSGERROR(1).CLAVE := 'FECHA';
                MI_MSGERROR(1).VALOR := RSCNSUSUARIO.FECHA;
                MI_MSGERROR(2).CLAVE := 'BANCO';
                MI_MSGERROR(2).VALOR := RSCNSUSUARIO.BANCO;
                MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
                MI_MSGERROR(3).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_E,
                                           UN_TABLAERROR => 'SP_D_RECAUDO',
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;

            BEGIN
              BEGIN
                SELECT SUM(SP_FACTURADO.VALOR_FACTURADOIN)
                INTO MI_SUMAAUX
                FROM SP_USUARIO
                  INNER JOIN SP_FACTURADO
                    ON SP_USUARIO.PERIODO               = SP_FACTURADO.PERIODO
                    AND SP_USUARIO.ANO                  = SP_FACTURADO.ANO
                    AND SP_USUARIO.CODIGORUTA           = SP_FACTURADO.CODIGORUTA
                    AND SP_USUARIO.CICLO                = SP_FACTURADO.CICLO
                    AND SP_USUARIO.COMPANIA             = SP_FACTURADO.COMPANIA
                  WHERE (SP_USUARIO.COMPANIA             = UN_COMPANIA
                    AND SP_USUARIO.FECHAPAGOPERANTERIOR  = RSCNSUSUARIO.FECHA
                    AND SP_USUARIO.BANCOPERANTERIOR      =  RSCNSUSUARIO.BANCO
                    AND SP_USUARIO.PAQUETEPAGOPERANTERIOR=  RSCNSUSUARIO.NUMEROPAQUETE
                    AND SP_FACTURADO.VALOR_FACTURADOIN  <>0
                    AND ((SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48
                      OR SP_FACTURADO.CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                      AND SP_FACTURADO.CONCEPTO NOT IN (12,17)))
                    OR (SP_USUARIO.FECHAPAGOPERPROCESO     = RSCNSUSUARIO.FECHA
                      AND SP_USUARIO.BANCOPERPROCESO       = RSCNSUSUARIO.BANCO
                      AND SP_USUARIO.PAQUETEPAGOPERPROCESO = RSCNSUSUARIO.NUMEROPAQUETE
                      AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48
                        OR SP_FACTURADO.CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                      AND SP_FACTURADO.CONCEPTO NOT IN (12,17));

                EXCEPTION WHEN NO_DATA_FOUND THEN
              --    MI_SUMAAUX := 0;
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (RSCNSUSUARIO.FECHA, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUD_NDF
                                         ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                         ,UN_REEMPLAZOS => MI_MSGERROR);              
            END;

            IF MI_SUMAAUX > 0 THEN
              MI_CAMPOS :='COMPANIA,CONCEPTO,
                          VALORPAGOPERIODO,
                          FECHA,BANCO,
                          NUMEROPAQUETE,VALORDEUDA,
                          VALORFINACT,VALORFINANT,
                          VALORABONOACT,VALORABONOANT,
                          CREDITOABONADO,
                          CREATED_BY,
                          DATE_CREATED';

              MI_VALORES:='SELECT 
                            USUARIO.COMPANIA
                           ,FACTURADO.CONCEPTO
                           ,ROUND((FACTURADO.VALOR_FACTURADOIN * ' || RSCNSUSUARIO.VALORPAGOPERIODO || ')/' || MI_SUMAAUX || ')
                           ,TO_DATE(''' || TO_CHAR(RSCNSUSUARIO.FECHA,'MM/DD/YYYY') || ''',''DD/MM/YYYY'')
                           ,''' || RSCNSUSUARIO.BANCO || '''
                           ,''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                           ,0
                           ,0
                           ,0
                           ,0
                           ,0
                           ,0  
                           ,'''||UN_USUARIO||'''
                           , SYSDATE
                          FROM SP_USUARIO
                            INNER JOIN SP_FACTURADO
                              ON SP_USUARIO.PERIODO               = SP_FACTURADO.PERIODO
                              AND SP_USUARIO.ANO                  = SP_FACTURADO.ANO
                              AND SP_USUARIO.CODIGORUTA           = SP_FACTURADO.CODIGORUTA
                              AND SP_USUARIO.CICLO                = SP_FACTURADO.CICLO
                              AND SP_USUARIO.COMPANIA             = SP_FACTURADO.COMPANIA
                          WHERE (SP_USUARIO.COMPANIA              = UN_COMPANIA
                            AND SP_USUARIO.FECHAPAGOPERANTERIOR  = '''||RSCNSUSUARIO.FECHA||'''
                            AND SP_USUARIO.BANCOPERANTERIOR      = '''||RSCNSUSUARIO.BANCO||'''
                            AND SP_USUARIO.PAQUETEPAGOPERANTERIOR= '''||RSCNSUSUARIO.NUMEROPAQUETE||'''
                            AND SP_FACTURADO.VALOR_FACTURADOIN  <>0
                            AND ((SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48
                              OR SP_FACTURADO.CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                              AND SP_FACTURADO.CONCEPTO NOT IN (12,17)))
                            OR (SP_USUARIO.FECHAPAGOPERPROCESO     = '''||RSCNSUSUARIO.FECHA||'''
                              AND SP_USUARIO.BANCOPERPROCESO       = '''||RSCNSUSUARIO.BANCO||'''
                              AND SP_USUARIO.PAQUETEPAGOPERPROCESO = '''||RSCNSUSUARIO.NUMEROPAQUETE||'''
                              AND ((SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48
                                OR SP_FACTURADO.CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                                AND SP_FACTURADO.CONCEPTO NOT IN (12,17)))';    
              BEGIN
                BEGIN
                  MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_D_RECAUDO', 
                                                   UN_ACCION  => 'IS', 
                                                   UN_CAMPOS  => MI_CAMPOS, 
                                                   UN_VALORES => MI_VALORES);  

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                            THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                    
                END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                  MI_MSGERROR(1).CLAVE := 'FECHA';
                  MI_MSGERROR(1).VALOR := RSCNSUSUARIO.FECHA;
                  MI_MSGERROR(2).CLAVE := 'BANCO';
                  MI_MSGERROR(2).VALOR := RSCNSUSUARIO.BANCO;
                  MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
                  MI_MSGERROR(3).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_IS,
                                             UN_TABLAERROR => 'SP_D_RECAUDO',
                                             UN_REEMPLAZOS => MI_MSGERROR);
              END;
            END IF;
          END IF;

          MI_CAMPOS := 'VALORPAGOPERIODO = 0,
                        MODIFIED_BY      = '''||UN_USUARIO||''',
                        DATE_MODIFIED    = SYSDATE ';

          MI_CONDICION := ' COMPANIA        = ''' || UN_COMPANIA || '''
                          AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                          AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                          AND NUMEROPAQUETE = ''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                          AND CONCEPTO      = 12 ';

          BEGIN
            BEGIN
              MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);  

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                        
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'FECHA';
              MI_MSGERROR(1).VALOR := TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY');
              MI_MSGERROR(2).CLAVE := 'BANCO';
              MI_MSGERROR(2).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR(3).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_M,
                                         UN_TABLAERROR => 'SP_D_RECAUDO',
                                         UN_REEMPLAZOS => MI_MSGERROR);            
          END;
        END LOOP ACTUALIZADRECAUDOS;
      END IF;

      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'REVISA RECAUDO 17', 
                                   UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS, 
                                   UN_FECHA_PAR => SYSDATE),
            'NO') = 'SI' 
      THEN
        <<ACTUALIZADRECAUDO>>    
        FOR RSCNSUSUARIO IN (
          SELECT 
            COMPANIA,
            FECHA,
            BANCO,
            NUMEROPAQUETE,
            VALORPAGOPERIODO,
            VALORDEUDA
          FROM SP_D_RECAUDO
          WHERE COMPANIA = '001'
            AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
            AND CONCEPTO =17
            AND (VALORPAGOPERIODO<>0
              OR VALORDEUDA        <>0))
        LOOP
          BEGIN
            BEGIN
              SELECT 
                SUM (SP_D_RECAUDO.VALORPAGOPERIODO) AS SUMADEVALORPAGOPERIODO,
                SUM (SP_D_RECAUDO.VALORDEUDA) AS SUMADEVALORDEUDA
              INTO MI_SUMAAUX,MI_DEUDAAUX
              FROM SP_D_RECAUDO
              WHERE COMPANIA     = UN_COMPANIA
                AND FECHA        = RSCNSUSUARIO.FECHA
                AND BANCO        = RSCNSUSUARIO.BANCO
                AND NUMEROPAQUETE= RSCNSUSUARIO.NUMEROPAQUETE
                AND (CONCEPTO BETWEEN 1 AND 48
                  OR CONCEPTO  IN (201,202,203,204,205,206,207,246,247,248,249))
                AND CONCEPTO NOT IN (17)
                AND VALORPAGOPERIODO<>0
              GROUP BY COMPANIA,
                FECHA,
                BANCO,
                NUMEROPAQUETE;

              EXCEPTION WHEN NO_DATA_FOUND 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                --MI_SUMAAUX := NULL;
                --MI_DEUDAAUX := NULL; 
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR (1).CLAVE := 'BANCO';
              MI_MSGERROR (1).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR (2).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR (2).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              MI_MSGERROR (3).CLAVE := 'FECHA';
              MI_MSGERROR (3).VALOR := TO_CHAR (RSCNSUSUARIO.FECHA, 'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECONSTRUIRD_RECAUD_NDF
                                         ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                         ,UN_REEMPLAZOS => MI_MSGERROR);            
          END;

          BEGIN
            IF MI_SUMAAUX < 0 THEN
              MI_CAMPOS := 'VALORPAGOPERIODO = VALORPAGOPERIODO - 
                                               ROUND((VALORPAGOPERIODO * ' || ABS(RSCNSUSUARIO.VALORPAGOPERIODO) || ')/' || MI_SUMAAUX || ',0),
                            MODIFIED_BY      = '''||UN_USUARIO||''',
                            DATE_MODIFIED    = SYSDATE ';

              MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                              AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                              AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                              AND NUMEROPAQUETE =''' || RSCNSUSUARIO.NUMEROPAQUETE || '''';

              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                 
              END;
            END IF;

            IF MI_DEUDAAUX < 0 THEN
              MI_CAMPOS := 'VALORDEUDA     = VALORDEUDA - 
                                              ROUND((VALORDEUDA * ' || ABS(RSCNSUSUARIO.VALORDEUDA) || ')/' || MI_DEUDAAUX || ',0),
                            MODIFIED_BY    = '''||UN_USUARIO||''',
                            DATE_MODIFIED  = SYSDATE ';
              MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                              AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                              AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                              AND NUMEROPAQUETE = ''' || RSCNSUSUARIO.NUMEROPAQUETE || '''';

              BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);   

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                               
              END;         
            END IF;  

            MI_CAMPOS := 'VALORPAGOPERIODO = 0 ,
                          VALORDEUDA       = 0,
                          MODIFIED_BY      = '''||UN_USUARIO||''',
                          DATE_MODIFIED    = SYSDATE ';

            MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                            AND FECHA         = TO_DATE('''|| TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                            AND BANCO         =''' || RSCNSUSUARIO.BANCO || ''' 
                            AND NUMEROPAQUETE = ''' || RSCNSUSUARIO.NUMEROPAQUETE || '''
                            AND CONCEPTO      = 17 ';

            BEGIN                
              MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO',
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                             
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'FECHA';
              MI_MSGERROR(1).VALOR := TO_CHAR(RSCNSUSUARIO.FECHA,'DD/MM/YYYY');
              MI_MSGERROR(2).CLAVE := 'BANCO';
              MI_MSGERROR(2).VALOR := RSCNSUSUARIO.BANCO;
              MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR(3).VALOR := RSCNSUSUARIO.NUMEROPAQUETE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_REC_REC_D_REC_M,
                                         UN_TABLAERROR => 'SP_D_RECAUDO',
                                         UN_REEMPLAZOS => MI_MSGERROR);
          END;
        END LOOP ACTUALIZADRECAUDO;
      END IF;
    END IF;
  END PR_RECONSTRUIRD_RECAUDO;

  --9     
  PROCEDURE PR_RECRECAUDOS

  /*
      NAME              : PR_RECONSTRUCCIONRECAUCONCEPTO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 29/08/2016
      TIME              : 12:00   PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Actualizar los recaudos entre dos fechas.
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.
      @NAME   :  actualizarDetalleDelRecuadoEntreFechas
      @METHOD :  PUT

    */ 

  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL   IN DATE,
    UN_FECHAFINAL     IN DATE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS           NUMBER;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_CAMPOS := ' VALORREGISTRADO    = 0, 
                   CUPONESREGISTRADOS = 0,
                   MODIFIED_BY    = '''||UN_USUARIO||''',
                   DATE_MODIFIED  = SYSDATE';

    MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                    AND TO_DATE(TO_CHAR(FECHA,''DD/MM/YYYY'')) BETWEEN TO_DATE('''|| TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                                                                 AND TO_DATE('''|| TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ';

    BEGIN
      BEGIN             
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_RECAUDOS',
                                         UN_ACCION    =>'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION);  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                  THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;     
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR(1).CLAVE := 'FECHAINICIAL';
        MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY');
        MI_MSGERROR(2).CLAVE := 'FECHAFINAL';
        MI_MSGERROR(2).VALOR := TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY');
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECRECAUDOS_M,
                                   UN_TABLAERROR => 'SP_RECAUDOS',
                                   UN_REEMPLAZOS => MI_MSGERROR);
    END;

    <<ACTUALIZARECAUDOS>>
    FOR RS IN (
      SELECT 
        COMPANIA,
        FECHA,
        BANCO,
        NUMEROPAQUETE,
        SUM(VALORPAGO)     AS SUMAPAGO,
        COUNT(CONSECUTIVO) AS CUENTAC
      FROM SP_PAGO
      WHERE COMPANIA = UN_COMPANIA
        AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
      GROUP BY 
        COMPANIA,
        FECHA,
        BANCO,
        NUMEROPAQUETE)
    LOOP
      MI_CAMPOS := 'VALORREGISTRADO    = '|| RS.SUMAPAGO || ',
                    CUPONESREGISTRADOS = ' || RS.CUENTAC||',
                    MODIFIED_BY        = '''||UN_USUARIO||''',
                    DATE_MODIFIED      = SYSDATE' ;

      MI_CONDICION := '   COMPANIA      = ''' || UN_COMPANIA || '''
                      AND FECHA         = TO_DATE('''|| TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                      AND BANCO         = '''|| RS.BANCO || ''' 
                      AND NUMEROPAQUETE = ''' || RS.NUMEROPAQUETE || '''';
      BEGIN
        BEGIN           
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_RECAUDOS',
                                           UN_ACCION    => 'M', 
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;      
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := TO_CHAR(RS.FECHA,'DD/MM/YYYY');
          MI_MSGERROR(2).CLAVE := 'BANCO';
          MI_MSGERROR(2).VALOR := RS.BANCO;
          MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
          MI_MSGERROR(3).VALOR := RS.NUMEROPAQUETE;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_RECRECAUDOS_M01,
                                     UN_TABLAERROR => 'SP_RECAUDOS',
                                     UN_REEMPLAZOS => MI_MSGERROR);        
      END;
    END LOOP ACTUALIZARECAUDOS;    
  END PR_RECRECAUDOS; 

  --10
  PROCEDURE PR_PASAABONOSRECAUDOS
  /*
      NAME              : PR_RECONSTRUCCIONRECAUCONCEPTO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADO      : 29/08/2016
      TIME              : 03:30 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que actualiza la tabla recaudos teniendo encuenta los abonos
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.



      @NAME:    pasarAbonosDeRecuados
      @METHOD:  GET

    */ 

  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA            IN DATE,
    UN_BANCO            IN PCK_SUBTIPOS.TI_BANCO,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS                 NUMBER;
    MI_COUNT                    PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_CONDICION:='   COMPANIA='''||UN_COMPANIA|| ''' 
                  AND TO_DATE(TO_CHAR(FECHA,''DD/MM/YYYY'')) = TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') 
                  AND BANCO                                  = ''' || UN_BANCO || ''' 
                  AND NUMEROPAQUETE                          = ''888''';

    BEGIN
      BEGIN
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'SP_D_RECAUDO',
                                         UN_ACCION=> 'E',
                                         UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
            THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := UN_BANCO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAUDOS_E,
                                   UN_TABLAERROR => 'SP_D_RECAUDO',
                                   UN_REEMPLAZOS => MI_MSGERROR);      
    END;

    BEGIN
      SELECT COUNT(*)
      INTO MI_COUNT
      FROM SP_ABONOS
        INNER JOIN SP_D_ABONOS
          ON SP_ABONOS.COMPANIA      = SP_D_ABONOS.COMPANIA
          AND SP_ABONOS.CICLO        = SP_D_ABONOS.CICLO
          AND SP_ABONOS.CODIGORUTA   = SP_D_ABONOS.CODIGORUTA
          AND SP_ABONOS.PERIODO      = SP_D_ABONOS.PERIODO
          AND SP_ABONOS.ANO          = SP_D_ABONOS.ANO
          AND  SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO  
      WHERE SP_ABONOS.COMPANIA                                          = UN_COMPANIA
        AND TO_DATE(TO_CHAR(SP_ABONOS.FECHA,'DD/MM/YYYY'),'DD/MM/YYYY') = UN_FECHA
        AND SP_ABONOS.BANCO                                             = UN_BANCO;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := UN_BANCO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAUDOS_NDF
                                   ,UN_TABLAERROR => 'SP_D_ABONOS'
                                   ,UN_REEMPLAZOS => MI_MSGERROR);
    END;  

    IF MI_COUNT > 0 THEN
      BEGIN
        SELECT COUNT(*) 
        INTO MI_COUNT
        FROM  SP_RECAUDOS 
        WHERE COMPANIA = UN_COMPANIA
          AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY'),'DD/MM/YYYY') = UN_FECHA
          AND BANCO                                             = UN_BANCO
          AND NUMEROPAQUETE                                     = '888';

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := UN_BANCO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAU_REC_NDF
                                   ,UN_TABLAERROR => 'SP_RECAUDOS'
                                   ,UN_REEMPLAZOS => MI_MSGERROR);
      END;

      IF MI_COUNT = 0 THEN
        MI_CAMPOS :='COMPANIA,
                     FECHA,
                     BANCO, 
                     NUMEROPAQUETE, 
                     BARRAS, 
                     TIPO, 
                     VALORREPORTADO, 
                     CUPONESREPORTADOS, 
                     VALORREGISTRADO,
                     CUPONESREGISTRADOS, 
                     DIFERENCIACUPONES, 
                     DIFERENCIAVALORES, 
                     USUARIO, 
                     COMENTARIOS,
                     CREATED_BY,
                     DATE_CREATED';

        MI_VALORES:=''''||UN_COMPANIA||''',
                    TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')'||',
                    '''|| UN_BANCO||''',
                    ''888'',
                    0,
                    1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    '''|| UN_USUARIO ||''',
                    ''PAGOS REALIZADOS POR ABONOS'',
                    '''||UN_USUARIO||''',  
                   SYSDATE';

        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_RECAUDOS', 
                                             UN_ACCION  => 'I', 
                                             UN_CAMPOS  => MI_CAMPOS,
                                             UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                      THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR(1).CLAVE := 'FECHA';
            MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
            MI_MSGERROR(2).CLAVE := 'BANCO';
            MI_MSGERROR(2).VALOR := UN_BANCO;
            MI_MSGERROR(3).CLAVE := 'USUARIO';
            MI_MSGERROR(3).VALOR := UN_USUARIO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAUDOS_I,
                                       UN_TABLAERROR => 'SP_RECAUDOS',
                                       UN_REEMPLAZOS => MI_MSGERROR);          
        END;
      END IF;

      <<INSERTAABONOS>>
      FOR RS IN(
        SELECT 
          SP_ABONOS.FECHA,
          SP_ABONOS.BANCO,
          SP_D_ABONOS.CONCEPTO,
          SUM(SP_D_ABONOS.VALORACT) AS SUMADEVALORACT,
          SUM(SP_D_ABONOS.VALORANT) AS SUMADEVALORANT
         FROM SP_ABONOS
         INNER JOIN SP_D_ABONOS
           ON SP_ABONOS.COMPANIA      = SP_D_ABONOS.COMPANIA
           AND SP_ABONOS.CICLO        = SP_D_ABONOS.CICLO
           AND SP_ABONOS.CODIGORUTA   = SP_D_ABONOS.CODIGORUTA
           AND SP_ABONOS.ANO          = SP_D_ABONOS.ANO
           AND SP_ABONOS.PERIODO      = SP_D_ABONOS.PERIODO
           AND  SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO      
         WHERE SP_ABONOS.COMPANIA                                         = UN_COMPANIA
           AND TO_DATE(TO_CHAR(SP_ABONOS.FECHA,'DD/MM/YYYY'),'DD/MM/YYYY')= UN_FECHA
           AND SP_ABONOS.BANCO                                            = UN_BANCO
         GROUP BY SP_ABONOS.FECHA,
           SP_ABONOS.BANCO,
           SP_D_ABONOS.CONCEPTO)
      LOOP
        MI_CAMPOS :='COMPANIA,
                     FECHA,
                     BANCO,
                     NUMEROPAQUETE,
                     CONCEPTO, 
                     VALORDEUDA,
                     VALORPAGOPERIODO,
                     VALORABONOACT,
                     VALORABONOANT,
                     VALORFINACT, 
                     VALORFINANT,
                     CREDITOABONADO ,
                     CREATED_BY,
                     DATE_CREATED';

        MI_VALORES:=''''||UN_COMPANIA||''',
                    TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')'||',
                    '''|| UN_BANCO||''',
                    ''888'',
                    '|| RS.CONCEPTO ||',
                    0,
                    0,
                    ' || RS.SUMADEVALORACT ||',
                    ' || RS.SUMADEVALORANT ||',
                    0,
                    0,
                    0,
                    '''||UN_USUARIO||''',  
                    SYSDATE';
        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA    => 'SP_D_RECAUDO'
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                      THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                 
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'FECHA';
            MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
            MI_MSGERROR(2).CLAVE := 'BANCO';
            MI_MSGERROR(2).VALOR := UN_BANCO;
            MI_MSGERROR(3).CLAVE := 'CONCEPTO';
            MI_MSGERROR(3).VALOR := RS.CONCEPTO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAUDO_ABO_I,
                                       UN_TABLAERROR => 'SP_D_RECAUDO',
                                       UN_REEMPLAZOS => MI_MSGERROR);          
        END;
      END LOOP INSERTAABONOS;
    ELSE
      MI_CONDICION:='COMPANIA='''||UN_COMPANIA|| ''' 
                    AND TO_DATE(TO_CHAR(FECHA,''DD/MM/YYYY'')) = TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') 
                    AND BANCO                                  = ''' || UN_BANCO || ''' 
                    AND NUMEROPAQUETE                          = ''888''';

      BEGIN
        BEGIN
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA  => 'SP_D_RECAUDO',
                                           UN_ACCION => 'E', 
                                           UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := UN_BANCO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PASAABONOSRECAUDO_REC_E,
                                   UN_TABLAERROR => 'SP_D_RECAUDO',
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;
    END IF;
  END PR_PASAABONOSRECAUDOS;

  --11
  PROCEDURE PR_INTERFAZABONOS

   /*
      NAME              : PR_RECONSTRUCCIONRECAUCONCEPTO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : 
      DATE MIGRADO      : 
      TIME              : 
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.



      @NAME:    validarBancoAbonos
      @METHOD:  GET

    */ 

  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL     IN DATE,
    UN_FECHAFINAL       IN DATE,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
  BEGIN
    <<VALIDARBANCOABONOS>>
    FOR RS IN (
      SELECT 
        BANCO, 
        FECHA 
      FROM SP_ABONOS 
      WHERE COMPANIA = UN_COMPANIA 
        AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
      GROUP BY 
        BANCO, 
        FECHA 
      ORDER BY FECHA)
    LOOP
      IF RS.FECHA IS NOT NULL AND RS.BANCO IS NOT NULL THEN
        PCK_SERVICIOS_PUBLICOS.PR_PASAABONOSRECAUDOS(UN_COMPANIA=> UN_COMPANIA, 
                                                     UN_FECHA   => RS.FECHA, 
                                                     UN_BANCO   => RS.BANCO, 
                                                     UN_USUARIO => UN_USUARIO);
      END IF;
    END LOOP VALIDARBANCOABONOS;
  END PR_INTERFAZABONOS;

  --12
  PROCEDURE PR_AJUSTEPESORECAUDO
    /*
      NAME              : PR_AJUSTEPESORECAUDO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : 
      DATE MIGRADO      : 
      TIME              : 
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     : Código de la compañia.
                          UN_FECHAINICIAL : Fecha en la que se inicia el proceso.
                          UN_FECHAFINAL   : Fecha en la que se termina el proceso. 
                          UN_USUARIO      : Nombre del usuario administrador.

      @NAME:    ajustarPesoDeRecaudo
      @METHOD:  PUT

    */ 
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL         IN DATE,
    UN_FECHAFINAL           IN DATE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_FECHA          DATE;
    MI_BANCO          PCK_SUBTIPOS.TI_BANCO;
    MI_NUMEROPAQUETE  SP_PAGO.NUMEROPAQUETE%TYPE;
    MI_SUMAABONO      PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMAPAGO       PCK_SUBTIPOS.TI_DOBLE;
    MI_DIF            PCK_SUBTIPOS.TI_DOBLE;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS       NUMBER;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    <<ACTUALIZARECAUDOS>>
    FOR RSPAGO IN (
      SELECT 
        COMPANIA,
        FECHA,
        BANCO,
        NUMEROPAQUETE,
        SUM(VALORPAGO) AS SUMADEVALORPAGO
      FROM SP_PAGO
      WHERE COMPANIA = UN_COMPANIA
        AND TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY')) BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
      GROUP BY 
        COMPANIA,
        FECHA,
        BANCO,
        NUMEROPAQUETE)
    LOOP
      BEGIN
        BEGIN
          SELECT 
            FECHA
           ,BANCO
           ,NUMEROPAQUETE
           ,SUM (VALORABONOACT+VALORABONOANT)    AS SUMAABONO
           ,SUM (VALORDEUDA   +VALORPAGOPERIODO) AS SUMAPAGO
          INTO 
             MI_FECHA
            ,MI_BANCO
            ,MI_NUMEROPAQUETE
            ,MI_SUMAABONO
            ,MI_SUMAPAGO
          FROM SP_D_RECAUDO
          WHERE COMPANIA     = UN_COMPANIA
            AND FECHA        = RSPAGO.FECHA
            AND BANCO        = RSPAGO.BANCO
            AND NUMEROPAQUETE= RSPAGO.NUMEROPAQUETE
            AND (CONCEPTO BETWEEN 1 AND 48
              OR  CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
            AND CONCEPTO NOT IN (12,17)
          GROUP BY 
            COMPANIA,
            FECHA,
            BANCO,
            NUMEROPAQUETE;

          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := TO_CHAR(RSPAGO.FECHA,'DD/MM/YYYY');
          MI_MSGERROR(2).CLAVE := 'BANCO';
          MI_MSGERROR(2).VALOR := RSPAGO.BANCO;
          MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
          MI_MSGERROR(3).VALOR := RSPAGO.NUMEROPAQUETE;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_AJUSTEPESORECA_DREC_NDF
                                     ,UN_TABLAERROR => 'SP_D_RECAUDO'
                                     ,UN_REEMPLAZOS => MI_MSGERROR);         
      END;

      IF MI_BANCO IS NOT NULL THEN
        MI_DIF := RSPAGO.SUMADEVALORPAGO - MI_SUMAPAGO;

        IF MI_DIF >= -10 AND MI_DIF <= 10 AND MI_DIF <> 0 THEN

          MI_CAMPOS := 'VALORPAGOPERIODO = VALORPAGOPERIODO + '|| MI_DIF||',
                        MODIFIED_BY      = '''||UN_USUARIO||''',
                        DATE_MODIFIED    = SYSDATE';

          MI_CONDICION := '   COMPANIA                               = ''' || UN_COMPANIA || '''
                          AND TO_DATE (TO_CHAR(FECHA,''DD/MM/YYYY''))= TO_DATE('''|| TO_CHAR(RSPAGO.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') 
                          AND BANCO                                  = '''|| RSPAGO.BANCO || ''' 
                          AND NUMEROPAQUETE                          = ''' || RSPAGO.NUMEROPAQUETE || ''' 
                          AND CONCEPTO                               = 249 ';            

          BEGIN
            BEGIN
              MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_D_RECAUDO'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);  

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                              
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'FECHA';
              MI_MSGERROR(1).VALOR := TO_CHAR(RSPAGO.FECHA,'DD/MM/YYYY');
              MI_MSGERROR(2).CLAVE := 'BANCO';
              MI_MSGERROR(2).VALOR := RSPAGO.BANCO;
              MI_MSGERROR(3).CLAVE := 'NUMEROPAQUETE';
              MI_MSGERROR(3).VALOR := RSPAGO.NUMEROPAQUETE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_AJUSTEPESORECA_DREC_I,
                                         UN_TABLAERROR => 'SP_D_RECAUDO',
                                         UN_REEMPLAZOS => MI_MSGERROR);            
          END;
        END IF;
      END IF;
    END LOOP ACTUALIZARECAUDOS;
  END PR_AJUSTEPESORECAUDO;

  --13
  FUNCTION FC_ESCRIBIRPERIODO
  /*
    OBJETIVO              : EscribirPeriodo
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_PERIODO: Código del mes.
                            UN_ANO: Valor numérico del año.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : NICOLÁS GÓMEZ BARBOSA
    FECHA                 : 05/09/2016 16:00
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :
    @NAME:    escribirPeriodo
    @METHOD:  GET
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PERIODO    IN NUMBER,
    UN_ANO        IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN VARCHAR2
  AS 
    MI_ERROR_FUN     PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_FRECUENCIA    VARCHAR2(3 CHAR);
    MI_PERIODO       PCK_SUBTIPOS.TI_ENTERO;
    MI_ANO           PCK_SUBTIPOS.TI_ANIO;
    MI_FREC          PCK_SUBTIPOS.TI_LOGICO;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 
    MI_FRECUENCIA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION', 
                                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS, 
                                               UN_FECHA_PAR => SYSDATE)
                        ,'M');

    CASE WHEN MI_FRECUENCIA = 'M' 
         THEN MI_FREC := 1;
         WHEN MI_FRECUENCIA = 'B' 
         THEN MI_FREC := 2;
         WHEN MI_FRECUENCIA = 'C' 
         THEN MI_FREC := 2;
         WHEN MI_FRECUENCIA = 'T'
         THEN MI_FREC := 3;
    END CASE;

    IF UN_PERIODO <= 0 THEN 
      MI_PERIODO:=UN_PERIODO+(12/MI_FREC);
      MI_ANO:=UN_ANO-1;
    ELSIF UN_PERIODO >12 THEN 
      MI_PERIODO:=UN_PERIODO+(12/MI_FREC);
      MI_ANO:=UN_ANO+1;
    ELSE
      MI_PERIODO:=UN_PERIODO;
      MI_ANO:=UN_ANO;
    END IF;

    RETURN PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA   => UN_COMPANIA,
                                                   UN_ANO        => MI_ANO,
                                                   UN_PERIODO    => TO_CHAR(MI_PERIODO),
                                                   UN_FRECUENCIA => MI_FRECUENCIA);                                                
  END FC_ESCRIBIRPERIODO;

  --14
  PROCEDURE PR_CONTROLCOPIA
   /*
    OBJETIVO              : ControlaCopia
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_CICLO: Número del ciclo del usuario.
                            UN_CODIGO: Código de ruta del usuario.
                            UN_TIPO: Indicador 
                            UN_APLICA: Indicador para ejecutar el procedimiento de la función
                            UN_TIMPRESION: 
                            UN_USER: Nombre del administrador del registro.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : NICOLÁS GÓMEZ BARBOSA
    FECHA                 : 06/09/2016 08:30
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    NAME                  : PR_CONTROLCOPIA En Access --> ControlaCopia
    SOURCE MODULE         : SERVICIOS PUBLICOS
    @NAME:    controlarCopia
    @METHOD:  POST
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO      IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGO     IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_TIPO       IN SP_HISTORIA_COPIAS.TIPO%TYPE,
    UN_APLICA     IN VARCHAR2,
    UN_TIMPRESION IN SP_HISTORIA_COPIAS.TIMPRESION%TYPE,
    UN_USER       IN SP_HISTORIA_COPIAS.CREADOR%TYPE
  ) 
  AS 
    MI_ERROR_FUN     PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_CONSABO       SP_HISTORIA_COPIAS.CONSECUTIVO%TYPE;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 
    IF UN_APLICA = 'SI' THEN 
      <<INSERTAUSUARIOS>>
      FOR RS IN (
        SELECT 
          ANO
         ,PERIODO
         ,FACTURA
         ,TOTFACTURAPERACTUAL
         ,USO
         ,ESTRATO
         ,ABONOACT
         ,INDRECAUDADO
         ,PERIODOSATRASO
        FROM SP_USUARIO 
        WHERE SP_USUARIO.COMPANIA   = UN_COMPANIA
          AND SP_USUARIO.CICLO      = UN_CICLO
          AND SP_USUARIO.CODIGORUTA = UN_CODIGO)
      LOOP
        IF (UN_TIPO='F' AND RS.INDRECAUDADO>1) OR UN_TIPO<>'F' THEN
          MI_CONSABO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_HISTORIA_COPIAS',
                                                       UN_CRITERIO => 'COMPANIA='''||UN_COMPANIA||''' AND CICLO='||UN_CICLO||' AND CODIGORUTA='''||UN_CODIGO||''' AND TIPO='''||UN_TIPO||'''',
                                                       UN_CAMPO    => 'CONSECUTIVO');

          MI_CAMPOS:='COMPANIA, CICLO, CODIGORUTA, CONSECUTIVO, TIPO, ANO, PERIODO, FACTURA, VALORFACTURA, VALORABONO, FECHA, USO,
                     ESTRATO, CREADOR, PERIODOSATRASO,TIMPRESION';

          MI_VALORES:=''''||UN_COMPANIA||''',
                      '||UN_CICLO||',
                      '''||UN_CODIGO||''',
                      '||MI_CONSABO||',
                      '''||UN_TIPO||''',
                      '||RS.ANO||',
                      '''||RS.PERIODO||''',
                      '''||RS.FACTURA||''',
                      '||RS.TOTFACTURAPERACTUAL||',
                      '||CASE WHEN UN_TIPO='A' THEN RS.ABONOACT ELSE 0 END||',
                      SYSDATE,
                      '''||RS.USO||''',
                      '''||RS.ESTRATO||''',
                      '''||UN_USER||''',
                      '||NVL(RS.PERIODOSATRASO,0)||',
                      '||NVL(UN_TIMPRESION,0);

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_HISTORIA_COPIAS',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
              MI_MSGERROR(1).CLAVE := 'CICLO';
              MI_MSGERROR(1).VALOR := UN_CICLO;
              MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
              MI_MSGERROR(2).VALOR := UN_CODIGO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SP_CONTROLCOPIA_HISTCOPI_I,
                                         UN_TABLAERROR => 'SP_HISTORIA_COPIAS',
                                         UN_REEMPLAZOS => MI_MSGERROR);
          END;
        END IF;
      END LOOP INSERTAUSUARIOS;
    END IF;
  END PR_CONTROLCOPIA;

  --15
  FUNCTION FC_AUTORIZACION_MICROMEDICION 
  /*
    OBJETIVO              : Definir si la entidad está autorizada para manejar proceso de micromedición, 
                            según su Nit y la configuración del parámetro MANEJA PROCESO DE MICROMEDICION.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_NIT: Nit de la entidad.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 22/09/2016 11:15 AM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :  

    NAME              : FC_AUTORIZACION_MICROMEDICION (En Access AUTORIZACION_MICROMEDICION del módulo MOD_AUTORIZACIONES)
    SOURCE MODULE     : SysmanSp2016.05.04
    @NAME:    autorizarMicromedicion
    @METHOD:  GET
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NIT        IN VARCHAR2
  )
  RETURN NUMBER
  AS
    MI_RTA          PCK_SUBTIPOS.TI_LOGICO := 0;
    MI_NIT_COMPANIA COMPANIA.NITCOMPANIA%TYPE;
    MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
  BEGIN
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                             UN_NOMBRE    => 'MANEJA PROCESO DE MICROMEDICION',
                             UN_FECHA_PAR => SYSDATE) <> 'SI' 
    THEN 
      RETURN MI_RTA;
    END IF;

    MI_NIT_COMPANIA := PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA, 
                                                                 UN_NIT      => UN_NIT);
    IF MI_NIT_COMPANIA IN('844000755' --YOPAL
                          ,'832000776'--FUNZA
                          ,'832001512'--MADRID
                          ,'899999714'--CHIA
                          ,'890680053'--FUSAGASUGA
                          ,'899999717') --PRUEBA
    THEN
      MI_RTA := -1;
    END IF;


    RETURN MI_RTA;  
    /*EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Error al realizar autorización de la Micromedición.';
        PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'PCK_SERVICIOS_PUBLICOS.FC_AUTORIZACION_MICROMEDICION','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );  */
  END FC_AUTORIZACION_MICROMEDICION;

  --16
  FUNCTION FC_GENERA_CONSUMOS_MICRO
  /*
    OBJETIVO              : Permite actualizar la última lectura de los microconsumos con la lectura aforo.
                            Además permite registrar el consumo de micromedición cálculados por los mismos.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_CICLO: Número del ciclo del usuario.
                            UN_STRPERIODO: Código del mes.
                            UN_INTANO: Valor numérico del año.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 22/09/2016 12:30 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    NAME                  : FC_GENERA_CONSUMOS_MICRO (En Access GeneraConsumosMicro del módulo MOD_CONSUMOMANUAL)
    SOURCE MODULE         : SysmanSp2016.05.04
    @NAME:    generarMicroconsumos
    @METHOD:  GET
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_CICLO      IN PCK_SUBTIPOS.TI_CICLO, 
    UN_STRPERIODO IN PCK_SUBTIPOS.TI_PERIODO, 
    UN_INTANO     IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER AS 
    MI_GENERA_CONSUMOS_MICRO NUMBER;
    MI_RS                    SYS_REFCURSOR; 
    MI_STRSQL                PCK_SUBTIPOS.TI_STRSQL;
    MI_LECTURAAFORO          SP_USUARIO.LECTURAAFORO%TYPE;
    MI_RTA                   NUMBER;
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_GENERA_CONSUMOS_MICRO := 0;

    --ACTUALIZANDO ÚLTIMA LECTURA DE MICROMEDICIÓN SOLO SE REALIZA UNA VEZ;
    <<ACTUALIZAMICMEDICION>>
    FOR MI_RS IN (
      SELECT CODIGORUTA 
      FROM SP_MICROMEDICION_CAMBIO
      WHERE COMPANIA = UN_COMPANIA
        AND CICLO    = UN_CICLO
        AND CONTROLLECTURA NOT IN (0)) 
    LOOP
      BEGIN
        BEGIN
          MI_STRSQL := 'SELECT LECTURAAFORO
                       FROM SP_USUARIO
                       WHERE COMPANIA   = ''' || UN_COMPANIA || '''
                         AND CICLO      = ' || UN_CICLO || '
                         AND CODIGORUTA = ''' || MI_RS.CODIGORUTA || '''';
          EXECUTE IMMEDIATE MI_STRSQL INTO MI_LECTURAAFORO;

          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_LECTURAAFORO := 0;
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          MI_MSGERROR(1).CLAVE := 'CICLO';
          MI_MSGERROR(1).VALOR := UN_CICLO;
          MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
          MI_MSGERROR(2).VALOR := MI_RS.CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_GENERA_CONSUMOS_MIC_NDF
                                     ,UN_TABLAERROR => 'SP_USUARIO'
                                     ,UN_REEMPLAZOS => MI_MSGERROR);
      END;

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_MICROMEDICION_CAMBIO', 
                                      UN_ACCION    => 'M', 
                                      UN_CAMPOS    => 'LECTURAFINAL = ' || MI_LECTURAAFORO, 
                                      UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CICLO      = '|| UN_CICLO || '
                                                      AND CODIGORUTA = ''' || MI_RS.CODIGORUTA ||'''');

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                      
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CICLO';
          MI_MSGERROR(1).VALOR := UN_CICLO;
          MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
          MI_MSGERROR(2).VALOR := MI_RS.CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_GENERA_CONSUMOS_MIC_M,
                                     UN_TABLAERROR => 'SP_MICROMEDICION_CAMBIO',
                                     UN_REEMPLAZOS => MI_MSGERROR);        
      END;
    END LOOP ACTUALIZAMICMEDICION;

    --ACTUALIZO CONSUMOS MANUALES CON LA SUMATORIA DE LAS DIFERENCIAS DE LOS CONSUMOS DE CADA MEDIDOR
    <<ACTUALIZAUSUARIOS>>
    FOR MI_RS IN (
      SELECT 
        COMPANIA
       ,CICLO
       ,CODIGORUTA
       ,SUM (LECTURAFINAL - LECTURAINICIAL) CONSUMO
      FROM SP_MICROMEDICION_CAMBIO
      WHERE COMPANIA = UN_COMPANIA
        AND CICLO    = UN_CICLO
        AND PERIODO  = UN_STRPERIODO
        AND ANO      = UN_INTANO 
      GROUP BY 
        COMPANIA
       ,CICLO
       ,CODIGORUTA) 
    LOOP
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_USUARIO', 
                                      UN_ACCION    => 'M', 
                                      UN_CAMPOS    => 'CONSAFOROMICRO = ' || MI_RS.CONSUMO, 
                                      UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CICLO      = '|| UN_CICLO || '
                                                      AND CODIGORUTA = ''' || MI_RS.CODIGORUTA ||'''');

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                                                        
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CICLO';
          MI_MSGERROR(1).VALOR := UN_CICLO;
          MI_MSGERROR(2).CLAVE := 'CODIGORUTA';
          MI_MSGERROR(2).VALOR := MI_RS.CODIGORUTA;          
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_GENERA_CONSUMOS_MIC_M,
                                     UN_TABLAERROR => 'SP_USUARIO',
                                     UN_REEMPLAZOS => MI_MSGERROR);

      END;
    END LOOP ACTUALIZAUSUARIOS;

    MI_GENERA_CONSUMOS_MICRO := -1;
    RETURN MI_GENERA_CONSUMOS_MICRO;
  END FC_GENERA_CONSUMOS_MICRO;

  --17
  FUNCTION FC_ANO_PERIODO_SIGUIENTE
  /*
    OBJETIVO              : Retornar el ano y/o el periodo siguiente.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_ANO: Ano actual.
                            UN_PERIODO: Periodo actual.
                            UN_TIPO_RETORNO: Puede tomar los siguientes valores
                                              0: En caso de que se quiera retornar unicamente el ano.
                                              1: En caso de que se quiera retornar unicamente el periodo.
                                             Cualquier otro valor que tome este parámetro, se tomará como separador, 
                                             así: ANO || UN_TIPO_RETORNO || PERIODO. Ejemplo: 2016/01
                            UN_FRECUENCIA: Frecuencia de periodos de facturación. Si es nula se toma desde el parametro 
                                           FRECUENCIA PERIODOS DE FACTURACION
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 22/09/2016 04:00 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    : 28/09/2016
    LIDER MODIFICACIÓN    : DIEGO FERNANDO MALDONADO MORALES
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : En cuanto a la versión de access, se agregó el parámetro UN_MODULO 
                            (para el llamado al parámetro de frecuencia) y el parámetro UN_TIPO_RETORNO, 
                            el cual puede tomar los valores que se detallan en la documentación de parámetros.

    NAME              : FC_ANO_PERIODO_SIGUIENTE (En Access AnoSte, PeriodoSte, PerSte, PeriodoSiguiente)
    SOURCE MODULE     : SysmanSp2016.05.04
    @NAME:    prepararAnoPeriodoSiguiente
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_TIPO_RETORNO IN VARCHAR2 DEFAULT '',
    UN_FRECUENCIA   IN VARCHAR2 DEFAULT NULL
  )
  RETURN VARCHAR2
  AS
    MI_ANO        PCK_SUBTIPOS.TI_ANIO := UN_ANO;
    MI_FRECUENCIA VARCHAR2(1 CHAR);
    MI_PERIODO    NUMBER(2) := TO_NUMBER(UN_PERIODO) + 1;    
    MI_ERROR_FUN  PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
  BEGIN
    IF UN_FRECUENCIA IS NULL THEN 
      MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                             UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                             UN_FECHA_PAR => SYSDATE);
    ELSE MI_FRECUENCIA := UN_FRECUENCIA;
    END IF;

    IF   (MI_FRECUENCIA = 'M' AND MI_PERIODO > 12)
      OR (MI_FRECUENCIA = 'B' AND MI_PERIODO > 6)
      OR (MI_FRECUENCIA = 'C' AND MI_PERIODO > 6)
      OR (MI_FRECUENCIA = 'T' AND MI_PERIODO > 4)
    THEN 
      MI_ANO := MI_ANO + 1;
      MI_PERIODO := 1;
    END IF;

    IF UN_TIPO_RETORNO = '0' THEN 
      RETURN MI_ANO;
    ELSIF UN_TIPO_RETORNO = '1' THEN
      RETURN PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => MI_PERIODO, UN_LONGITUD => 2);
    ELSE 
      RETURN MI_ANO || UN_TIPO_RETORNO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => MI_PERIODO, UN_LONGITUD => 2);
    END IF;

   /* EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Error al calcular el año y el periodo siguiente.';
        PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );  */
  END FC_ANO_PERIODO_SIGUIENTE;

  --18
  FUNCTION FC_ANO_PERIODO_ANTERIOR
  /*
    OBJETIVO              : Retornar el ano y/o el periodo anterior.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Compania en la que se está trabajando
                            UN_ANO: Ano actual.
                            UN_PERIODO: Periodo actual.
                            UN_TIPO_RETORNO: Puede tomar los siguientes valores 
                                              0: En caso de que se quiera retornar unicamente el ano.
                                              1: En caso de que se quiera retornar unicamente el periodo.
                                             Cualquier otro valor que tome este parámetro, se tomará como separador, 
                                             así: ANO || UN_TIPO_RETORNO || PERIODO. Ejemplo: 2016/01
                            UN_FRECUENCIA: La frecuencia de periodos de facturación. 
                                           Si es nula se toma desde el parametro FRECUENCIA PERIODOS DE FACTURACION
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : VICTOR JULIO MOLANO BOLIVAR
    FECHA                 : 30/09/2016 10:00 AM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : En cuanto a la versión de access, se agregó el parámetro UN_TIPO_RETORNO, el cual puede tomar los valores 
                            que se detallan en la documentación de parámetros.

    NAME                  : FC_ANO_PERIODO_ANTERIOR (En Access AnoAnt, PeriodoAnt, PerAnt, PerAnt2)
    SOURCE MODULE         : SysmanSp2016.05.04
    @NAME:    prepararAnoPeriodoAnterior
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_TIPO_RETORNO IN VARCHAR2 DEFAULT '',
    UN_FRECUENCIA   IN VARCHAR2 DEFAULT NULL
  )
  RETURN VARCHAR2
  AS
    MI_ANO        PCK_SUBTIPOS.TI_ANIO := UN_ANO;
    MI_FRECUENCIA VARCHAR2(1 CHAR);
    MI_PERIODO    NUMBER(2) := TO_NUMBER(UN_PERIODO) - 1;    
    MI_ERROR_FUN  PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
  BEGIN
    IF UN_FRECUENCIA IS NULL THEN  MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                          UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                          UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                                                          UN_FECHA_PAR => SYSDATE);
    ELSE  MI_FRECUENCIA := UN_FRECUENCIA;
    END IF;

    IF MI_PERIODO <= 0 THEN
    	MI_ANO := MI_ANO - 1;

    	IF MI_FRECUENCIA = 'M' THEN
        MI_PERIODO := 12;
    	ELSIF MI_FRECUENCIA = 'B' OR MI_FRECUENCIA = 'C' THEN
    		MI_PERIODO := 6;
    	ELSIF MI_FRECUENCIA = 'T' THEN
    		MI_PERIODO := 4;
    	END IF;
    END IF;

    IF UN_TIPO_RETORNO = '0' THEN 
      RETURN MI_ANO;
    ELSIF UN_TIPO_RETORNO = '1' THEN
      RETURN PCK_SYSMAN_UTL.FC_STRZERO(MI_PERIODO,2);
    ELSE 
      RETURN MI_ANO || UN_TIPO_RETORNO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => MI_PERIODO, UN_LONGITUD => 2);
    END IF;

   /* EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Error al calcular el año y el periodo anterior.';
        PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_ANTERIOR','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );  */
  END FC_ANO_PERIODO_ANTERIOR;

  --19
  FUNCTION FC_PERN
  /*
    OBJETIVO              : Retornar el periodo (solamente el mes,bimestre o trimestre ) siguiente
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Compania en la que se está trabajando
                            UN_INTANO: (No esta en uso)
                            UN_STRPERIODO: Código del mes.
                            UN_NUMPERIODOS: Cantidad de periodos.
                            UN_FRECUENCIA: Frecuencia periodos de facturación.
                            UN_MODULO: Código del módulo.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 26/09/2016 12:30 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : En cuanto a la versión de access, se agregó el parámetro UN_MODULO 
                            (para el llamado al parámetro de frecuencia)

    NAME                  : FC_PERN (En Access PerN del módulo Facturacion)
    SOURCE MODULE         : SysmanSp2016.05.04
    @NAME:    prepararPeriodoSiguiente
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_INTANO       IN PCK_SUBTIPOS.TI_ANIO, 
    UN_STRPERIODO   IN PCK_SUBTIPOS.TI_PERIODO, 
    UN_NUMPERIODOS  IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_FRECUENCIA   IN VARCHAR2 DEFAULT NULL,
    UN_MODULO       IN PCK_SUBTIPOS.TI_MODULO
  )
  RETURN VARCHAR2 
  AS 
    MI_PERN          VARCHAR2(2 CHAR);
    MI_INTPERIODO1   NUMBER(2); 
    MI_STRFRECUENCIA VARCHAR2(4000 CHAR); 
    MI_ERROR_FUN     PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
  BEGIN
    --24/07/2014;
    -- RETORNA EL PERIODO (SOLAMENTE EL MES,BIMESTRE O TRIMESTRE ) SIGUIENTE;
    IF UN_FRECUENCIA = '' OR UN_FRECUENCIA IS NULL THEN
      MI_STRFRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(
                            UN_COMPANIA  => UN_COMPANIA,
                            UN_MODULO    => UN_MODULO,
                            UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                            UN_FECHA_PAR => SYSDATE);
    ELSE
      MI_STRFRECUENCIA := UN_FRECUENCIA;
    END IF;

    --STRFRECUENCIA = PARFACTURACION(COMPANIA, 'FRECUENCIA PERIODOS DE FACTURACION');
    --INTPERIODO1 = VAL(STRPERIODO) + 1;
    MI_INTPERIODO1 := TO_NUMBER(UN_STRPERIODO);

    IF UN_NUMPERIODOS < 0 THEN
      FOR I IN 1..ABS(UN_NUMPERIODOS) LOOP
        MI_INTPERIODO1 := MI_INTPERIODO1 - 1;

        IF MI_INTPERIODO1 <= 0 THEN
          MI_INTPERIODO1 := CASE MI_STRFRECUENCIA WHEN 'M'
                                                  THEN 12 
                                                  WHEN 'B'
                                                  THEN 6
                                                  WHEN 'C'
                                                  THEN 6
                                                  WHEN 'T'
                                                  THEN 4
                            END;
        END IF;
      END LOOP;
    ELSE
      FOR I IN 1..UN_NUMPERIODOS LOOP
        MI_INTPERIODO1 := MI_INTPERIODO1 + 1;
        IF MI_STRFRECUENCIA = 'M' AND MI_INTPERIODO1 > 12 THEN
          MI_INTPERIODO1 := 1;
        ELSIF MI_STRFRECUENCIA = 'B' AND MI_INTPERIODO1 > 6 THEN
          MI_INTPERIODO1 := 1;
        ELSIF MI_STRFRECUENCIA = 'C' AND MI_INTPERIODO1 > 6 THEN
          MI_INTPERIODO1 := 1;
        ELSIF MI_STRFRECUENCIA = 'T' AND MI_INTPERIODO1 > 4 THEN
          MI_INTPERIODO1 := 1;
        END IF;
      END LOOP;
    END IF;

    MI_PERN := PCK_SYSMAN_UTL.FC_STRZERO(MI_INTPERIODO1, 2);
    RETURN MI_PERN;

   /* EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Error en la funcion FC_PERN.';
        PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'PCK_SERVICIOS_PUBLICOS.FC_PERN','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); */
  END FC_PERN;

  --20
  FUNCTION FC_ANON
  /*
    OBJETIVO              : Retornar el ANO (solamente el mes,bimestre o trimestre ) siguiente
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Compania en la que se está trabajando
                            UN_INTANO: Valor numérico del año.
                            UN_STRPERIODO: Código del mes.
                            UN_NUMPERIODOS: Cantidad de periodos.
                            UN_FRECUENCIA: Frecuencia periodos de facturación.
                            UN_MODULO: Código del módulo.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 26/09/2016 02:30 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : En cuanto a la versión de access, se agregó el parámetro UN_MODULO 
                            (para el llamado al parámetro de frecuencia)

    NAME                  : FC_ANON (En Access AnoN del módulo Facturacion)
    SOURCE MODULE         : SysmanSp2016.05.04
    @NAME:    prepararAnoSiguiente
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_INTANO       IN PCK_SUBTIPOS.TI_ANIO, 
    UN_STRPERIODO   IN PCK_SUBTIPOS.TI_PERIODO, 
    UN_NUMPERIODOS  IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_FRECUENCIA   IN VARCHAR2 DEFAULT NULL,
    UN_MODULO       IN PCK_SUBTIPOS.TI_MODULO
  )
  RETURN VARCHAR2 
  AS 
    MI_INTPERIODO1    NUMBER(2); 
    MI_STRFRECUENCIA  PCK_SUBTIPOS.TI_RTA_ACME; 
    MI_INTANO1        PCK_SUBTIPOS.TI_ANIO;
    MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
  BEGIN
    --24/07/2014;
    -- RETORNA EL PERIODO (SOLAMENTE EL MES,BIMESTRE O TRIMESTRE ) SIGUIENTE;
    IF UN_FRECUENCIA = '' OR UN_FRECUENCIA IS NULL THEN
      MI_STRFRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(
                            UN_COMPANIA  => UN_COMPANIA,
                            UN_MODULO    => UN_MODULO,
                            UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                            UN_FECHA_PAR => SYSDATE);
    ELSE
      MI_STRFRECUENCIA := UN_FRECUENCIA;
    END IF;

    MI_INTANO1 := UN_INTANO;
    MI_INTPERIODO1 := TO_NUMBER(UN_STRPERIODO);

    IF UN_NUMPERIODOS < 0 THEN
      <<ITERAPERIODO>>
      FOR I  IN 1..ABS(UN_NUMPERIODOS) LOOP
        MI_INTPERIODO1 := MI_INTPERIODO1 - 1;

        IF MI_INTPERIODO1 <= 0 THEN
          MI_INTANO1 := MI_INTANO1 - 1;
          MI_INTPERIODO1 := CASE MI_STRFRECUENCIA WHEN 'M'
                                                  THEN 12 
                                                  WHEN 'B'
                                                  THEN 6
                                                  WHEN 'C'
                                                  THEN 6
                                                  WHEN 'T'
                                                  THEN 4
                            END;
        END IF;
      END LOOP ITERAPERIODO;
    ELSE
      <<ITERAPERIODO>>
      FOR I IN 1..UN_NUMPERIODOS LOOP
        MI_INTPERIODO1 := MI_INTPERIODO1 + 1;
        IF MI_STRFRECUENCIA = 'M' AND MI_INTPERIODO1 > 12 THEN
          MI_INTANO1 := MI_INTANO1 + 1;
        ELSIF MI_STRFRECUENCIA = 'B' AND MI_INTPERIODO1 > 6 THEN
          MI_INTANO1 := MI_INTANO1 + 1;
        ELSIF MI_STRFRECUENCIA = 'C' AND MI_INTPERIODO1 > 6 THEN
          MI_INTANO1 := MI_INTANO1 + 1;
        ELSIF MI_STRFRECUENCIA = 'T' AND MI_INTPERIODO1 > 4 THEN
          MI_INTANO1 := MI_INTANO1 + 1;
        END IF;

        IF MI_INTPERIODO1 = 13 THEN 
          MI_INTPERIODO1 := 1;
        END IF;        
      END LOOP ITERAPERIODO;
    END IF;

    RETURN MI_INTANO1;

    /*EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Error en la funcion FC_ANON.';
        PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'PCK_SERVICIOS_PUBLICOS.FC_ANON','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); */
  END FC_ANON;

  --21
FUNCTION FC_PREPARAR_CRITICA
  /*
    OBJETIVO              : Retornar el ANO (solamente el mes,bimestre o trimestre ) siguiente
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Compania en la que se está trabajando
                            UN_MODULO: Código del módulo.
                            UN_CICLO: Número del ciclo del usuario.
                            UN_STRCODIGOINI: Código de ruta inicial del usuario.
                            UN_STRCODIGOFIN: Código de ruta final del usuario.
                            UN_ANO: año actual seleccionado en el formulario modal con los datos para la crítica
                            UN_PERIODO: periodo actual seleccionado en el formulario modal con los datos para la crítica
                            UN_CONSUMO_MENOR: Consumo del usuario.
                            UN_PORC_MENOR: Porcentaje.
                            UN_PORC_MAYOR: Porcentaje.
                            UN_NORMALES: Indicador para negar condición.
                            UN_MANUAL: Indicador para condicionar por AFOROCONSMANUAL.
                            UN_IGUALES: Indicador par filtrar por LECTURAAFORO y LECTURA.
                            UN_DESVIACION: Indicador para filtrar por DESVIOSIGNIFICATIVO.
                            UN_USUARIO: usuario de la sesion en el sistema
                            UN_REPORTE: indicador - en 0 retorna la consulta para la grilla del subformulario correccion
                            critica de consumos, en -1 retorna los filtros para la consulta que genera el reporte de excel
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : DIEGO FERNANDO MALDONADO MORALES
    FECHA                 : 26/09/2016 02:30 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    : 27/07/2017
    MODIFICADOR POR       : SERGIO ESTEBAN PIÑA VARGAS 
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN : En cuanto a la versión de access, se agregó el parámetro UN_MODULO 
                            (para el llamado al parámetro de frecuencia) - SE AGREGA VALIDACIÓN
                            DE EL TIPO DE RETORNO PARA RETORNAR LA CONSULTA DE LA GRILLA DEL 
                            SUBFORMULARIO CORRECCION DE CRITICA DE CONSUMOS Y LOS FILTROS PARA EL REPORTE DE EXCEL

    NAME              : FC_PREPARAR_CRITICA (En Access se encuentra dentro del evento Aceptar_Click 
                        en el formulario FRM_PEDIRCICLOCRITICA)
    SOURCE MODULE     : SysmanSp2016.05.04   
    @NAME:    prepararCritica
    @METHOD:  GET
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
    UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO,
    UN_STRCODIGOINI   IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_STRCODIGOFIN   IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO,
    UN_CONSUMO_MENOR  IN SP_USUARIO.CONSUMOPROM%TYPE,
    UN_PORC_MENOR     IN PCK_SUBTIPOS.TI_PORCENTAJE,
    UN_PORC_MAYOR     IN PCK_SUBTIPOS.TI_PORCENTAJE,
    UN_NORMALES       IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MANUAL         IN PCK_SUBTIPOS.TI_LOGICO,
    UN_IGUALES        IN PCK_SUBTIPOS.TI_LOGICO,
    UN_DESVIACION     IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_REPORTE        IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN VARCHAR2
  AS
    MI_RTA          NUMBER;
    MI_PARAMETRO    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_STRCOND      PCK_SUBTIPOS.TI_CONDICION;
    MI_STRCRITICA   PCK_SUBTIPOS.TI_CONDICION;
    MI_STRCONSUMOP  VARCHAR2(200 CHAR);
    MI_STRCONSUMO   VARCHAR2(200 CHAR);
    MI_FRECUENCIA   VARCHAR2(20 CHAR);
    MI_INTPERIODO1  PCK_SUBTIPOS.TI_LOGICO;
    MI_WHEREMANUAL  VARCHAR2(4000 CHAR);
    MI_WHEREIGUALES VARCHAR2(4000 CHAR);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  BEGIN
    MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
                      UN_COMPANIA  => UN_COMPANIA,
                      UN_MODULO    => UN_MODULO,
                      UN_NOMBRE    => 'FACTURACION EN SITIO',
                      UN_FECHA_PAR => SYSDATE);

    IF MI_PARAMETRO = 'SI' THEN 
      MI_STRCOND :='CASE WHEN SP_USUARIO.CONSUMOPROM < ' || UN_CONSUMO_MENOR || ' 
                         THEN (CASE WHEN SP_USUARIO.CONSUMO < SP_USUARIO.CONSUMOPROM * (1 - ' || (UN_PORC_MENOR / 100) ||')
                                    THEN ''INFERIOR''
                                    ELSE (CASE WHEN SP_USUARIO.CONSUMO > SP_USUARIO.CONSUMOPROM * (1 + ' || (UN_PORC_MENOR / 100) ||')
                                               THEN ''SUPERIOR''
                                               ELSE ''NORMAL''
                                          END)
                               END)
                         ELSE (CASE WHEN SP_USUARIO.CONSUMO < SP_USUARIO.CONSUMOPROM * (1 - ' || (UN_PORC_MAYOR / 100) ||')
                                    THEN ''INFERIOR''
                                    ELSE (CASE WHEN SP_USUARIO.CONSUMO > SP_USUARIO.CONSUMOPROM * (1 + ' || (UN_PORC_MAYOR / 100) ||')
                                               THEN ''SUPERIOR''
                                               ELSE ''NORMAL''
                                          END)
                               END)
                    END';
      MI_STRCRITICA := MI_STRCOND;

      IF UN_NORMALES NOT IN (0) THEN 
        MI_STRCOND := ' NOT ' || MI_STRCOND;
      END IF;

      MI_STRCONSUMOP := ' SP_USUARIO.CONSUMOPROM ';  
      MI_STRCONSUMO := ' SP_USUARIO.CONSUMO ';
    ELSE
      MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(
                         UN_COMPANIA  => UN_COMPANIA,
                         UN_MODULO    => UN_MODULO,
                         UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                         UN_FECHA_PAR => SYSDATE);
      MI_INTPERIODO1 := 6;

      --IF MI_FRECUENCIA = 'M'
      --THEN MI_INTPERIODO1 := 6;
      IF MI_FRECUENCIA = 'B' OR MI_FRECUENCIA = 'C' THEN 
        MI_INTPERIODO1 := 3;
      ELSIF MI_FRECUENCIA = 'T' THEN 
        MI_INTPERIODO1 := 2;
      END IF;

      IF UN_MANUAL NOT IN (0) 
        THEN MI_WHEREMANUAL := ' AND SP_USUARIO.AFOROCONSMANUAL IN (0) ';
      END IF;

      IF UN_IGUALES NOT IN (0) THEN 
        MI_WHEREIGUALES := ' AND (SP_USUARIO.LECTURAAFORO - SP_USUARIO.LECTURA) NOT IN (0) ';
      END IF;

      MI_STRCONSUMOP := ' SP_USUARIO.CONSUMO';

      <<SUMACONSUMO>>
      FOR MI_I IN 1..(MI_INTPERIODO1 - 1) LOOP
        MI_STRCONSUMOP := MI_STRCONSUMOP || ' + SP_USUARIO.CONSUMO' || MI_I;
      END LOOP SUMACONSUMO;

      MI_STRCONSUMOP := 'ROUND((('|| MI_STRCONSUMOP || ') /' || MI_INTPERIODO1 || '),0)';

      MI_CAMPOS := ' SP_USUARIO.TEMP_CONSUMOPROMCAL = ' || MI_STRCONSUMOP||'
                    ,SP_USUARIO.MODIFIED_BY         = '''||UN_USUARIO||'''';

      MI_CONDICION := ' SP_USUARIO.COMPANIA         = ''' || UN_COMPANIA || ''' 
                      AND SP_USUARIO.CICLO          = ' || UN_CICLO || ' 
                      AND SP_USUARIO.CODIGORUTA BETWEEN ''' || UN_STRCODIGOINI || ''' AND ''' || UN_STRCODIGOFIN || '''' 
                      || MI_WHEREIGUALES || 
                      MI_WHEREMANUAL || 
                      ' AND SP_USUARIO.TEMP_CONSUMOPROMCAL - ' || MI_STRCONSUMOP || 'NOT IN (0) ';    

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'SP_USUARIO', 
                                      UN_ACCION => 'M', 
                                      UN_CAMPOS => MI_CAMPOS, 
                                      UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;          
        END;

        MI_CAMPOS :=' SP_USUARIO.TEMP_LIMITEINF = SP_USUARIO.TEMP_CONSUMOPROMCAL * (CASE WHEN TEMP_CONSUMOPROMCAL < ' || UN_CONSUMO_MENOR || ' 
                                                                                         THEN 1 - (' || UN_PORC_MENOR / 100 || ')
                                                                                         ELSE 1 - (' || UN_PORC_MAYOR / 100 || ') 
                                                                                    END) ' ||
                    ',SP_USUARIO.TEMP_LIMITESUP = SP_USUARIO.TEMP_CONSUMOPROMCAL * (CASE WHEN TEMP_CONSUMOPROMCAL < ' || UN_CONSUMO_MENOR || ' 
                                                                                         THEN 1 + (' || UN_PORC_MENOR / 100 || ')
                                                                                         ELSE 1 + (' || UN_PORC_MAYOR / 100 || ') 
                                                                                    END)'
                                                                                    ||'
                    ,SP_USUARIO.MODIFIED_BY         = '''||UN_USUARIO||'''';

        MI_CONDICION := ' SP_USUARIO.COMPANIA  = ''' || UN_COMPANIA || '''
                          AND SP_USUARIO.CICLO = ' || UN_CICLO || '
                          AND SP_USUARIO.CODIGORUTA BETWEEN ''' || UN_STRCODIGOINI || ''' AND ''' || UN_STRCODIGOFIN || ''''
                          || MI_WHEREMANUAL ;

        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'SP_USUARIO', UN_ACCION => 'M', UN_CAMPOS => MI_CAMPOS, UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;            
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          MI_MSGERROR(1).CLAVE := 'CICLO';
          MI_MSGERROR(1).VALOR := UN_CICLO;
          MI_MSGERROR(2).CLAVE := 'CODIGOINICIAL';
          MI_MSGERROR(2).VALOR := UN_STRCODIGOINI;
          MI_MSGERROR(3).CLAVE := 'CODIGOFINAL';
          MI_MSGERROR(3).VALOR := UN_STRCODIGOFIN;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_PREPARAR_CRITICA_M,
                                     UN_TABLAERROR => 'SP_USUARIO',
                                     UN_REEMPLAZOS => MI_MSGERROR);        
      END;

      MI_STRCONSUMOP := ' SP_USUARIO.TEMP_CONSUMOPROMCAL';
      MI_STRCONSUMO := ' (SP_USUARIO.LECTURAAFORO - SP_USUARIO.LECTURA)';

      IF UN_NORMALES NOT IN (0) THEN 
        MI_STRCOND := MI_STRCONSUMO || ' BETWEEN SP_USUARIO.TEMP_LIMITEINF AND SP_USUARIO.TEMP_LIMITESUP';
      ELSE 
        MI_STRCOND := MI_STRCONSUMO || ' NOT BETWEEN SP_USUARIO.TEMP_LIMITEINF AND SP_USUARIO.TEMP_LIMITESUP';
      END IF;

      MI_STRCRITICA := ' CASE WHEN ' || MI_STRCONSUMO || ' < SP_USUARIO.TEMP_LIMITEINF
                              THEN ''INFERIOR''
                              ELSE (CASE WHEN ' || MI_STRCONSUMO || ' > SP_USUARIO.TEMP_LIMITESUP
                                         THEN ''SUPERIOR''
                                         ELSE ''NORMAL''
                                    END)
                         END';
    END IF;

    IF UN_DESVIACION NOT IN (0) THEN
      MI_STRCOND := MI_WHEREIGUALES || MI_WHEREMANUAL|| ' AND SP_USUARIO.DESVIOSIGNIFICATIVO = -1';
    ELSE 
      MI_STRCOND := MI_WHEREIGUALES || MI_WHEREMANUAL|| ' AND SP_USUARIO.DESVIOSIGNIFICATIVO   IN (0)'|| ' AND ' || MI_STRCOND;
    END IF;

    IF UN_REPORTE = 0 THEN
      --RETURN MI_STRCONSUMOP || '#' || MI_STRCONSUMO || '#' || MI_STRCOND || '#' || MI_STRCRITICA;    
      MI_STRSQL :=  'SELECT SP_USUARIO.COMPANIA,
                      SP_USUARIO.CICLO,
                      SP_USUARIO.CODIGORUTA,
                      SP_USUARIO.CODIGOINTERNO,
                      SP_USUARIO.LECTURA,
                      SP_USUARIO.LECTURAAFORO,
                      TO_CHAR(SP_USUARIO.FECHALECTURAAFORO, ''DD/MM/YYYY'') FECHALECTURAAFORO,
                      SP_USUARIO.MEDIDOR,
                      SP_USUARIO.DIRTECNICA AS DIRGUIA,
                      SP_USUARIO.PRIMERAPELLIDO
                      || '''|| ' ' ||'''
                      || SP_USUARIO.SEGUNDOAPELLIDO
                      || '''|| ' ' ||'''
                      || SP_USUARIO.NOMBRES AS NOMBRE,
                      SP_USUARIO.CONSUMO1,
                      SP_USUARIO.CONSUMO2,
                      SP_USUARIO.CONSUMO3,
                      SP_USUARIO.CONSUMO4,
                      SP_USUARIO.CONSUMO5,
                      SP_USUARIO.CONSUMO6,
                      SP_USUARIO.LECTURA1,
                      SP_USUARIO.LECTURA2,
                      SP_USUARIO.LECTURA3,
                      SP_USUARIO.LECTURA4,
                      SP_USUARIO.LECTURA5,
                      SP_USUARIO.LECTURA6,
                      SP_USUARIO.NUMERODIGITOS,
                      SP_USUARIO.USO,
                      SP_USOS.NOMBRE             AS NOMBREUSO,
                      SP_ESTRATOS.NOMBRE         AS ESTRATO ,
                      SP_USUARIO.ESTADO          AS ESTADO,
                      '|| MI_STRCONSUMOP ||'     AS CONSUMOP,
                      '|| MI_STRCONSUMO ||'      AS CONS,
                      SP_USUARIO.DESVIACIONAFORO AS DESVIACIONAFORO,
                      SP_USUARIO.AFOROCONSMANUAL AS AFOROCONSMANUAL,
                      SP_USUARIO.CONSAFOROMICRO  AS CONSMICRO,
                      '|| MI_STRCRITICA ||'      AS CRITICA,
                      SP_USUARIO.AFORADOR,
                      TERCERO.NOMBRE AS AFORADORNOMBRE,
                      SP_USUARIO.CHAPETAS,
                      SP_USUARIO.INDDESHABITADO,
                      SP_USUARIO.CONSUMO,
                      SP_USUARIO.METROSDESVIACIONAFORO AS METROSDESVIACION
                    FROM SP_USUARIO
                    INNER JOIN SP_USOS
                    ON SP_USUARIO.COMPANIA = SP_USOS.COMPANIA
                    AND SP_USUARIO.USO     = SP_USOS.CODIGO
                    INNER JOIN SP_ESTRATOS
                    ON (SP_USUARIO.COMPANIA = SP_ESTRATOS.COMPANIA
                    AND SP_USUARIO.USO      = SP_ESTRATOS.USO
                    AND SP_USUARIO.ESTRATO  = SP_ESTRATOS.CODIGO)
                    LEFT JOIN SP_AFORADORES
                    ON (SP_USUARIO.COMPANIA = SP_AFORADORES.COMPANIA
                    AND SP_USUARIO.AFORADOR = SP_AFORADORES.CODIGO)
                    LEFT JOIN TERCERO
                    ON (SP_AFORADORES.COMPANIA = TERCERO.COMPANIA
                    AND SP_AFORADORES.NIT      = TERCERO.NIT
                    AND SP_AFORADORES.SUCURSAL = TERCERO.SUCURSAL)
                    WHERE SP_USUARIO.COMPANIA  = '''|| UN_COMPANIA ||'''
                    AND SP_USUARIO.CICLO       = '|| UN_CICLO ||'
                    AND SP_USUARIO.CODIGORUTA BETWEEN '''|| UN_STRCODIGOINI ||''' AND '''|| UN_STRCODIGOFIN ||'''
                    AND SP_USUARIO.ANO     = '|| UN_ANO ||'
                    AND SP_USUARIO.PERIODO = '|| UN_PERIODO ||'
                    ' || MI_STRCOND ||'
                    ';
      RETURN MI_STRSQL;
    ELSE
      RETURN MI_STRCOND;    
    END IF;

  END FC_PREPARAR_CRITICA;				 

  --22
  FUNCTION FC_NOMBREUSO 
  /*
    OBJETIVO              : Retornar el Nombre registrado en la tabla SP_USOS
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Compania en la que se está trabajando

    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : YESIKA PAOLA BECERRA CASTRO
    FECHA                 : 31/10/2016 14:45 PM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    NAME              : FC_NOMBREUSO En Access --> Nombreuso
    SOURCE MODULE     : SERVICIOS PUBLICOS
    @NAME:    asignarNombreUso
    @METHOD:  GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO   IN SP_USOS.CODIGO%TYPE
  )
  RETURN VARCHAR2
  AS 
    MI_ERROR_FUN PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 22;
    MI_RTA       VARCHAR2(20 CHAR);
    MI_STRSQL    PCK_SUBTIPOS.TI_STRSQL;
    MI_NOMBRE    SP_USOS.NOMBRE%TYPE;
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
      BEGIN
        BEGIN
          MI_STRSQL := 'SELECT SP_USOS.NOMBRE
                       FROM SP_USOS
                       WHERE SP_USOS.COMPANIA = '''||UN_COMPANIA||'''
                         AND SP_USOS.CODIGO   = '''||UN_CODIGO||'''';

          EXECUTE IMMEDIATE MI_STRSQL INTO MI_NOMBRE;

          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := UN_CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NOMBREUSO_NDF,
                                     UN_TABLAERROR => 'SP_USOS',
                                     UN_REEMPLAZOS => MI_MSGERROR);
      END;

      MI_RTA := MI_NOMBRE;
      RETURN MI_RTA;     
  END FC_NOMBREUSO;   

  --23
  PROCEDURE PR_ACTUALIZAFRECUENCIAS
    /*
      OBJETIVO              : Actualizar la frecuencia de barrido y recoleccion.
      PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                              UN_CICLO: Codigo del ciclo.
                              UN_CODINI: Código de ruta inicial.
                              UN_CODFIN: Código de ruta final.
                              UN_ACTIVIDAD: Código de la actividad.
                              UN_FRECUENCIA: Valor de la frecuencia de recoleccion y barrido.

      PARÁMETROS DE SALIDA  : 

      AUTOR                 : STEFANINI SYSMAN
      AUTOR MIGRACIÓN       : PABLO ANDRES ESPITIA CUCA
      FECHA MIGRACIÓN       : 15/05/2017
      HORA MIGRACIÓN        : 10:35 AM


      AUTOR MODIFICACIÓN    :
      FECHA MODIFICACIÓN    :
      OBJETIVO MODIFICACIÓN :

      @NAME: actualizarFrecuencia
      @METHOD: PUT
    */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO      IN SP_USUARIO.CICLO%TYPE,
    UN_CODINI     IN SP_USUARIO.CODIGORUTA%TYPE,
    UN_CODFIN     IN SP_USUARIO.CODIGORUTA%TYPE,
    UN_ACTIVIDAD  IN VARCHAR2,
    UN_FRECUENCIA IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
    MI_ACTIVIDAD VARCHAR2(20 CHAR);
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA       PCK_SUBTIPOS.TI_RTA_ACME;
  BEGIN
    MI_TABLA := 'SP_USUARIO';

    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
                 AND CICLO    =   '||UN_CICLO||' 
                 AND CODIGORUTA BETWEEN '''||UN_CODINI||''' AND '''||UN_CODFIN||'''';

    IF UN_ACTIVIDAD IN ('B') THEN
      MI_CAMPOS    := 'FREC_BARRI = '||UN_FRECUENCIA;
      MI_ACTIVIDAD := 'BARRIDO';
    ELSE
      MI_CAMPOS    := 'FREC_RECO = '||UN_FRECUENCIA; 
      MI_ACTIVIDAD := 'RECOLECCION';
    END IF;

    MI_CAMPOS := MI_CAMPOS || 
              ' ,DATE_MODIFIED = SYSDATE
                ,MODIFIED_BY   = '''||UN_USUARIO||'''';

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                 THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
      MI_MSGERROR(1).VALOR := MI_ACTIVIDAD;
      MI_MSGERROR(2).CLAVE := 'CODINI';
      MI_MSGERROR(2).VALOR := UN_CODINI;
      MI_MSGERROR(3).CLAVE := 'CODFIN';
      MI_MSGERROR(3).VALOR := UN_CODFIN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_M_ACTUALIZAFRECUENCIA
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
    END;

  END PR_ACTUALIZAFRECUENCIAS;

  --24
 PROCEDURE PR_DESACTIVAR_SERV_ASEOURB
    /*
      OBJETIVO              : Desactiva el servicio de aseo urbano.
      PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                              UN_CICLO: Código del ciclo.
                              UN_CODIGORUTA: Código de ruta. 
                              UN_ANIO: Codigo del año.
                              UN_PERIODO: Numero del periodo.
                              UN_USUARIO: Código del usuario que inicio sesión.

      PARÁMETROS DE SALIDA  : 

      AUTOR                 : STEFANINI SYSMAN
      AUTOR MIGRACIÓN       : PABLO ANDRES ESPITIA CUCA
      FECHA MIGRACIÓN       : 16/05/2017
      HORA MIGRACIÓN        : 09:36 AM
      BEAN ASOCIADO         : AseoUrbanoControlador.java


      AUTOR MODIFICACIÓN    :
      FECHA MODIFICACIÓN    :
      OBJETIVO MODIFICACIÓN :

      @NAME: desactivarServicioAseoUrbano
      @METHOD: POST
    */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO      IN SP_USUARIO.CICLO%TYPE,
    UN_CODIGORUTA IN SP_USUARIO.CODIGORUTA%TYPE,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO    IN SP_HISTORIA_EXTERNA.PERIODO%TYPE,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA       PCK_SUBTIPOS.TI_RTA_ACME;
    MI_SQL       PCK_SUBTIPOS.TI_STRSQL;
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_BANCO     SP_HISTORIA_EXTERNA.BANCO_PAGO%TYPE;
  BEGIN 
    MI_TABLA := 'SP_HISTORIA_EXTERNA';

    BEGIN
      BEGIN
        SELECT BANCO_PAGO
        INTO MI_BANCO
        FROM SP_HISTORIA_EXTERNA
        WHERE COMPANIA = UN_COMPANIA
        AND CICLO      = UN_CICLO
        AND CODIGORUTA = UN_CODIGORUTA
        AND ANO        = UN_ANIO
        AND PERIODO    = UN_PERIODO;

      EXCEPTION WHEN NO_DATA_FOUND
                THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;   

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CODRUTA';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_VERIF_HISTO_EXTERNA
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
    END; 

    IF MI_BANCO IS NOT NULL THEN 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODRUTA';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'ANIO';
          MI_MSGERROR(2).VALOR := UN_ANIO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_IS_DSAU_HIS_EXT_DESACTI
                                    ,UN_TABLAERROR => MI_TABLA
                                    ,UN_REEMPLAZOS => MI_MSGERROR
                                    );         
      END;
    END IF;

    MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(MI_TABLA);

    MI_SQL := 'SELECT '||MI_CAMPOS||',SYSDATE DATE_CREATED,'''||UN_USUARIO||''' CREATED_BY
               FROM SP_HISTORIA_EXTERNA
               WHERE COMPANIA   = '''||UN_COMPANIA  ||'''
                 AND CICLO      =   '||UN_CICLO     ||'
                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                 AND ANO        =   '||UN_ANIO      ||'
                 AND PERIODO    = '''||UN_PERIODO   ||'''';

    MI_TABLA := 'SP_HISTORIA_EXTERNA_DESACTIVA';    

    BEGIN
      BEGIN    
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'IS'
                                   ,UN_CAMPOS    => MI_CAMPOS ||',DATE_CREATED, CREATED_BY'
                                   ,UN_VALORES   => MI_SQL);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

      MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''
                   AND CICLO      =   '||UN_CICLO     ||'
                   AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                   AND ANO        =   '||UN_ANIO      ||'
                   AND PERIODO    = '''||UN_PERIODO   ||'''';

      BEGIN
        MI_TABLA := 'SP_HISTORIA_EXTERNA';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                  THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;      

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CODRUTA';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
      MI_MSGERROR(2).CLAVE := 'PERIODO';
      MI_MSGERROR(2).VALOR := UN_PERIODO;
      MI_MSGERROR(3).CLAVE := 'ANIO';
      MI_MSGERROR(3).VALOR := UN_ANIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_IS_DSAU_HIS_EXT_DESACTI
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
    END;
  END PR_DESACTIVAR_SERV_ASEOURB;

  --25
  PROCEDURE PR_ACTIVAR_SERV_ASEOURB 
    /* 
      OBJETIVO              : Activa el servicio de aseo urbano.
      PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                              UN_CICLO: Código del ciclo.
                              UN_CODIGORUTA: Código de ruta. 
                              UN_ANIO: Codigo del año.
                              UN_PERIODO: Numero del periodo.
                              UN_USUARIO: Código del usuario que inicio sesión.

      PARÁMETROS DE SALIDA  : 

      AUTOR                 : STEFANINI SYSMAN
      AUTOR MIGRACIÓN       : PABLO ANDRES ESPITIA CUCA
      FECHA MIGRACIÓN       : 16/05/2017
      HORA MIGRACIÓN        : 15:20 PM
      BEAN ASOCIADO         : AseoUrbanoControlador.java


      AUTOR MODIFICACIÓN    :
      FECHA MODIFICACIÓN    :
      OBJETIVO MODIFICACIÓN :

      @NAME: activarServicioAseoUrbano
      @METHOD: POST
    */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO      IN SP_USUARIO.CICLO%TYPE,
    UN_CODIGORUTA IN SP_USUARIO.CODIGORUTA%TYPE,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO    IN SP_HISTORIA_EXTERNA.PERIODO%TYPE,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA_HED PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_HE  PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA       PCK_SUBTIPOS.TI_RTA_ACME;
    MI_SQL       PCK_SUBTIPOS.TI_STRSQL;
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_COMPANIA  SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA%TYPE;
  BEGIN 
    MI_TABLA_HED := 'SP_HISTORIA_EXTERNA_DESACTIVA';
    MI_TABLA_HE  := 'SP_HISTORIA_EXTERNA';

    BEGIN
      BEGIN
        SELECT COMPANIA
        INTO MI_COMPANIA
        FROM SP_HISTORIA_EXTERNA_DESACTIVA
        WHERE COMPANIA   = UN_COMPANIA
          AND CICLO      = UN_CICLO
          AND CODIGORUTA = UN_CODIGORUTA
          AND ANO        = UN_ANIO
          AND PERIODO    = UN_PERIODO;

      EXCEPTION WHEN NO_DATA_FOUND
                THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_ASAU_SHED_VALIDARCP
                                ,UN_TABLAERROR => MI_TABLA_HED
                                );
    END;

    MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(MI_TABLA_HED);

    MI_SQL := 'SELECT '||MI_CAMPOS||',SYSDATE DATE_CREATED,'''||UN_USUARIO||''' CREATED_BY
               FROM SP_HISTORIA_EXTERNA_DESACTIVA
               WHERE COMPANIA   = '''||UN_COMPANIA  ||'''
                 AND CICLO      =   '||UN_CICLO     ||'
                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                 AND ANO        =   '||UN_ANIO      ||'
                 AND PERIODO    = '''||UN_PERIODO   ||'''';

    BEGIN
      BEGIN    
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_HE
                                   ,UN_ACCION    => 'IS'
                                   ,UN_CAMPOS    => MI_CAMPOS ||',DATE_CREATED, CREATED_BY'
                                   ,UN_VALORES   => MI_SQL);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

      MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''
                   AND CICLO      =   '||UN_CICLO     ||'
                   AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                   AND ANO        =   '||UN_ANIO      ||'
                   AND PERIODO    = '''||UN_PERIODO   ||'''';

      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_HED
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR 
                  THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;      

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CODRUTA';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
      MI_MSGERROR(2).CLAVE := 'PERIODO';
      MI_MSGERROR(2).VALOR := UN_PERIODO;
      MI_MSGERROR(3).CLAVE := 'ANIO';
      MI_MSGERROR(3).VALOR := UN_ANIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_IS_E_ASAU_HISEXTDE_HISE
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
    END;    
  END PR_ACTIVAR_SERV_ASEOURB; 

  --26

  FUNCTION FC_ACTUALIZACHAPETA
  /*
  NAME              : FC_ACTUALIZACHAPETA En Access --> Aceptar_Click() FrmActualizarChapetas
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 24/05/2017
  TIME              : 09:10 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : SERVICIOS PUBLICOS
  DESCRIPTION       : Función que se llama en el formulario actualizar chapetas y se encarga de actualizar la
  tabla SP_USARIO editando el campo de CHAPETAS en 0.
  @NAME:  actualizarChapetas
  @METHOD:  GET
  */
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO    
)
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO            PCK_SUBTIPOS.TI_ENTERO;
  MI_RSUSUARIOS        SYS_REFCURSOR;
  MI_CODIGORUTA        PCK_SUBTIPOS.TI_CODIGORUTA;
  MI_ESTADO            SP_AUDITORIA_USUARIO.ESTADO%TYPE;
  MI_PERIODO           PCK_SUBTIPOS.TI_PERIODO;
  MI_CHAPETAS          SP_USUARIO.CHAPETAS%TYPE;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ACTUALIZAAUDI     PCK_SUBTIPOS.TI_ENTERO; 
  MI_RPTA              PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  MI_RPTA:=0;
  MI_STRSQL := 'SELECT  CODIGORUTA
                        ,ESTADO
                        ,PERIODO
                        ,CHAPETAS
                FROM SP_USUARIO
                WHERE SP_USUARIO.COMPANIA ='''||UN_COMPANIA||'''
                  AND SP_USUARIO.CICLO    = '||UN_CICLO||'
                  AND SP_USUARIO.CODIGORUTA BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                  AND SP_USUARIO.BANCOPERPROCESO IS NOT NULL
                  AND SP_USUARIO.CHAPETAS NOT    IN (0)';

  EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;      

          IF MI_CONTEO >=1 THEN           
            OPEN MI_RSUSUARIOS FOR MI_STRSQL;
              LOOP

                 FETCH MI_RSUSUARIOS INTO MI_CODIGORUTA
                                          ,MI_ESTADO
                                          ,MI_PERIODO
                                          ,MI_CHAPETAS; 



                  MI_CAMPOS := 'CHAPETAS       = 0 ,'||
                               ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                               ' DATE_MODIFIED = SYSDATE';

                  MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || ''' '||
                              ' AND CICLO      = '   || UN_CICLO || ' '||
                              ' AND CODIGORUTA = ''' || MI_CODIGORUTA || '''';

                  BEGIN
                   BEGIN

                     MI_RPTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION)+MI_RPTA;


                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                   RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END ;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                              MI_MSGERROR(1).CLAVE := 'RUTA';
                              MI_MSGERROR(1).VALOR := MI_CODIGORUTA;
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                            UN_EXC_COD =>SQLCODE
                                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTUCHAPETAS
                                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END; 


             MI_ACTUALIZAAUDI := PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI(UN_COMPANIA        => UN_COMPANIA, 
                                                                        UN_CICLO            => UN_CICLO, 
                                                                        UN_CODIGO           => MI_CODIGORUTA, 
                                                                        UN_VALFINAL         => 0, 
                                                                        UN_VALINICIAL       => -1, 
                                                                        UN_CAMPOACTUAL      => 'CHAPETAS',
                                                                        UN_CAMPOANTERIOR    => 'CHAPETAS_ANT',
                                                                        UN_PERIODO          => MI_PERIODO,
                                                                        UN_USUARIO          => UN_USUARIO);


                EXIT WHEN MI_RSUSUARIOS%NOTFOUND;

            END LOOP;
    END IF;
    RETURN MI_RPTA;
END FC_ACTUALIZACHAPETA;



END PCK_SERVICIOS_PUBLICOS;