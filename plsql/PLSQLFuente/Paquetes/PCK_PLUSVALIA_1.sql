create or replace PACKAGE BODY PCK_PLUSVALIA AS

PROCEDURE PR_PLUSVALIA_CARGAR_PLANTILLA 
/*
    NAME              : PR_PLUSVALIA_CARGAR_PLANTILLA
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 11/02/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CARGAR INFORMACION DE LA PLANTILLA
    @NAME:    plusvaliaCargarPlantilla
    @METHOD:  POST
    */
(
  UN_CARGAR_PLANTILLA IN CLOB,
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROYECTO         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
  MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
BEGIN
          MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => UN_CARGAR_PLANTILLA,
                                                       UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);

    <<CREAR_CARGAR_PLANTILLA>>
   FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
     LOOP

          MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => UN_CARGAR_PLANTILLA,
                                                           UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL);


                  MI_CAMPOS := 'ID_PROYECTO
                                ,COMPANIA
                                ,LIQUIDACION
                                ,FECHA
                                ,MATRICULA_INMOBILIARIA
                                ,CODIGO_PREDIAL
                                ,CODIGO_CATASTRAL
                                ,DIRECCION 
                                ,NOMBRE_PROPIETARIO
                                ,NIT_PROPIETARIO
                                ,NOMBRE_REPRESENTANTE
                                ,NIT_REPRESENTANTE
                                ,DIRECCION_NOTIFICACION
                                ,TELEFONO
                                ,HG1_CODIGO
                                ,HG1_ACUERDO
                                ,HG1_TRATAMIENTO
                                ,HG1_ACTIVIDAD
                                ,HG1_AREA_PREDIO
                                ,HG1_FRENTE_PREDIO
                                ,HG1_VOLADIZO
                                ,HG1_AREA_BRUTA
                                ,HG1_AFECTACIONES
                                ,HG1_CESION_PUBLICA
                                ,HG2_CODIGO
                                ,HG2_ACUERDO
                                ,HG2_TRATAMIENTO
                                ,HG2_ACTIVIDAD
                                ,HG2_AREA_PREDIO
                                ,HG2_FRENTE_PREDIO
                                ,HG2_VOLADIZO
                                ,HG2_AREA_BRUTA
                                ,HG2_AFECTACIONES
                                ,HG2_CESION_PUBLICA
                                ,VALOR_LIQUIDACION
                                ,FECHA_PAGO
                                ,CREATED_BY
                                ,DATE_CREATED';

                  MI_VALORES := '  '|| UN_PROYECTO          ||'
                                ,'''|| UN_COMPANIA          ||'''
                                ,  '|| MI_DATOS_COLUMNAS(1) ||'
                                ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                                ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                                ,  '|| MI_DATOS_COLUMNAS(4) ||'
                                ,  '|| MI_DATOS_COLUMNAS(5) ||'
                                ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                                ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                                ,  '|| MI_DATOS_COLUMNAS(8) ||'
                                ,'''|| MI_DATOS_COLUMNAS(9) ||'''
                                ,  '|| MI_DATOS_COLUMNAS(10) ||'
                                ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                                ,  '|| MI_DATOS_COLUMNAS(12) ||'
                                ,  '|| MI_DATOS_COLUMNAS(13) ||'
                                ,  '|| MI_DATOS_COLUMNAS(14) ||'
                                ,  '|| MI_DATOS_COLUMNAS(15) ||'
                                ,  '|| MI_DATOS_COLUMNAS(16) ||'
                                ,  '|| MI_DATOS_COLUMNAS(17) ||'
                                ,  '|| MI_DATOS_COLUMNAS(18) ||'
                                ,  '|| MI_DATOS_COLUMNAS(19) ||'
                                ,  '|| MI_DATOS_COLUMNAS(20) ||'
                                ,  '|| MI_DATOS_COLUMNAS(21) ||'
                                ,  '|| MI_DATOS_COLUMNAS(22) ||'
                                ,  '|| MI_DATOS_COLUMNAS(23) ||'
                                ,  '|| MI_DATOS_COLUMNAS(24) ||'
                                ,  '|| MI_DATOS_COLUMNAS(25) ||'
                                ,  '|| MI_DATOS_COLUMNAS(26) ||'
                                ,  '|| MI_DATOS_COLUMNAS(27) ||'
                                ,  '|| MI_DATOS_COLUMNAS(28) ||'
                                ,  '|| MI_DATOS_COLUMNAS(29) ||'
                                ,  '|| MI_DATOS_COLUMNAS(30) ||'
                                ,  '|| MI_DATOS_COLUMNAS(31) ||'
                                ,  '|| MI_DATOS_COLUMNAS(32) ||'
                                ,  '|| MI_DATOS_COLUMNAS(33) ||'
                                ,'''|| MI_DATOS_COLUMNAS(34) ||'''
                                ,'''|| UN_USUARIO ||'''
                                ,SYSDATE';

       BEGIN 
            BEGIN

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_PLUSVALIA',
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);


                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                   RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_CARGARPLANTILLA);
       END;

   END LOOP CREAR_CARGAR_PLANTILLA; 

END PR_PLUSVALIA_CARGAR_PLANTILLA;



FUNCTION FC_PROCESO_FACTURACION 
/*
    NAME              : FC_PROCESO_FACTURACION
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 27/02/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCESO DE FACTURACION DEL MODULO PLUSVALIA 
    @NAME:    procesoFacturacion
    @METHOD:  POST
    */
(
    UN_COMPANIA              IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_PROYECTO           IN  VP_PROYECTOS.ID%TYPE,
    UN_BENEFICIARIO_INICIAL  IN  VP_BENEFICIARIOS.ID%TYPE DEFAULT 0,
    UN_BENEFICIARIO_FINAL    IN  VP_BENEFICIARIOS.ID%TYPE DEFAULT 0,
    UN_ETAPA                 IN  VP_PROCESO_FACTURACION.CODIGOETAPA%TYPE,
    UN_RECLASIFICAR          IN  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
    UN_PROCESO               IN  VP_PROCESO_FACTURACION.ID%TYPE DEFAULT 0,
    UN_USUARIO               IN  PCK_SUBTIPOS.TI_USUARIO
)RETURN VARCHAR2
AS 
    MI_CODIGO_PROYECTO  VP_PROYECTOS.CODIGO%TYPE;
    MI_CLASE_PROYECTO   VP_PROYECTOS.CLASE%TYPE;
    MI_ID_PROCESO       VP_PROCESO_FACTURACION.ID%TYPE;
    MI_NUMERO_PROCESO   VP_PROCESO_FACTURACION.NUMERO%TYPE;
    MI_ID_BENEFICIARIO  VP_BENEFICIARIOS.ID%TYPE;
    MI_IP_CODIGO        VP_BENEFICIARIOS.IP_CODIGO%TYPE;
    MI_NUMERO_ORDEN     VP_BENEFICIARIOS.IP_NUMERO_ORDEN%TYPE;
    MI_ID_FACTURA       VP_FACTURA.ID%TYPE;
    MI_NUMERO_FACTURA   VP_FACTURA.NUMERO_FACTURA%TYPE;
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_LONG;  
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_RTA              VARCHAR2(32000 CHAR);
    MI_RS               SYS_REFCURSOR;
    MI_RSS              SYS_REFCURSOR;
    MI_VALOR_CN         PCK_SUBTIPOS.TI_DOBLE; 
    MI_RES_DIV         PCK_SUBTIPOS.TI_DOBLE;
    MI_RES_TOTAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
    RSAREA_FISICA                     VP_BENEFICIARIOS.AREA_FISICA%TYPE;
    RSDESTINACION_ECONOMICA    VP_BENEFICIARIOS.DESTINACION_ECONOMICA%TYPE;
    RSGRADO_BENEFICIO          VP_BENEFICIARIOS.GRADO_BENEFICIO%TYPE;
    RSAREA_TOTAL                      VP_PROYECTOS.AREA_TOTAL%TYPE;
    RSVALOR_DISTRIBUIBLE              VP_PROYECTOS.VALOR_DISTRIBUIBLE%TYPE;

    MI_GRADO_BENEFICIO   NUMBER;
    MI_DESTINACION_ECONOMICA   NUMBER;

BEGIN
         SELECT CODIGO,
                CLASE
            INTO MI_CODIGO_PROYECTO,
                 MI_CLASE_PROYECTO
          FROM VP_PROYECTOS
          WHERE COMPANIA = UN_COMPANIA
          AND ID = UN_ID_PROYECTO;


          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_PROCESO_FACTURACION',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' ',
                                                     UN_CAMPO    => 'NUMERO',
                                                     UN_INICIAL  => 1);

          MI_CAMPOS := 'COMPANIA,
                        ID_PROYECTO,
                        CODIGO_PROYECTO,
                        CLASE_PROYECTO,
                        NUMERO,
                        FECHA,
                        CODIGOETAPA,
                        FECHA_LIMITE,
                        VALOR_TOTAL,
                        CREATED_BY,
                        DATE_CREATED';


          MI_VALORES := ' '''|| UN_COMPANIA        ||''',
                            '|| UN_ID_PROYECTO     ||' ,
                          '''|| MI_CODIGO_PROYECTO ||''',
                          '''|| MI_CLASE_PROYECTO  ||''',
                            '|| MI_CONSECUTIVO     ||',
                          '''|| SYSDATE            ||''',
                            '|| UN_ETAPA           ||' ,
                          '''|| (SYSDATE + 3)      ||''',
                            '|| 0                  ||',
                          '''|| UN_USUARIO         ||''',
                          '''|| SYSDATE            ||''' ' ;    



        BEGIN                           
            BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_PROCESO_FACTURACION',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                              MI_MSGERROR(1).CLAVE := 'PROYECTO';
                              MI_MSGERROR(1).VALOR := MI_CODIGO_PROYECTO;
                              MI_MSGERROR(2).CLAVE := 'CLASE';
                              MI_MSGERROR(2).VALOR := MI_CLASE_PROYECTO;


                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
        END;   

              BEGIN    
                      BEGIN

                              SELECT ID,
                                     NUMERO
                                INTO MI_ID_PROCESO,
                                     MI_NUMERO_PROCESO
                              FROM VP_PROCESO_FACTURACION
                              WHERE COMPANIA = UN_COMPANIA
                              AND NUMERO = MI_CONSECUTIVO;

                           EXCEPTION WHEN NO_DATA_FOUND THEN
                  RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

              END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERR_PLUSVALIA_NODATA
                );
            END;    

             IF UN_RECLASIFICAR NOT IN (0) THEN 
                  MI_CONSULTA := 'SELECT VB.ID,
                              VB.IP_CODIGO,
                              VB.IP_NUMERO_ORDEN,
                              VB.AREA_GRAVADA,
                              VB.DESTINACION_ECONOMICA,
                              VB.GRADO_BENEFICIO,
                              VP.AREA_TOTAL,
                              VP.VALOR_DISTRIBUIBLE
                            FROM VP_BENEFICIARIOS VB
                            INNER JOIN VP_PROYECTOS VP
                            ON VB.ID_PROYECTO = VP.ID
                            INNER JOIN VP_FACTURA VF
                            ON VB.ID = VF.ID_BENEFICIARIOS
                            WHERE VB.COMPANIA       = ''' || UN_COMPANIA || '''
                            AND VF.NUMERO_PROCESO   =   ' || UN_PROCESO  || '
                            ORDER BY VB.ID';

        ELSE 

         MI_CONSULTA := 'SELECT VB.ID,
                              VB.IP_CODIGO,
                              VB.IP_NUMERO_ORDEN,
                              VB.AREA_GRAVADA,
                              VB.DESTINACION_ECONOMICA,
                              VB.GRADO_BENEFICIO,
                              VP.AREA_TOTAL,
                              VP.VALOR_DISTRIBUIBLE
                            FROM VP_BENEFICIARIOS VB
                            INNER JOIN VP_PROYECTOS VP
                            ON VB.COMPANIA = VP.COMPANIA
                            AND VB.CODIGO_PROYECTO = VP.CODIGO
                            AND VB.CLASE = VP.CLASE
                            WHERE VB.COMPANIA  = ''' || UN_COMPANIA    || '''
                            AND VB.ID_PROYECTO =   ' || UN_ID_PROYECTO || '
                            AND VB.ID  BETWEEN '|| UN_BENEFICIARIO_INICIAL ||' AND ' || UN_BENEFICIARIO_FINAL ||'
                            ORDER BY VB.ID ';

        END IF;

     /* <<INSERT_FACTURA>>
    FOR MI_RS IN (SELECT VB.ID,
                          VB.IP_CODIGO,
                          VB.IP_NUMERO_ORDEN,
                          VB.AREA_FISICA,
                          VB.FACTOR_DESTINACION_ECONOMICA,
                          VB.FACTOR_GRADO_BENEFICIO,
                          VP.AREA_TOTAL,
                          VP.VALOR_DISTRIBUIBLE
                  FROM VP_BENEFICIARIOS VB
                  INNER JOIN VP_PROYECTOS VP
                  ON VB.COMPANIA = VP.COMPANIA
                  AND VB.CODIGO_PROYECTO = VP.CODIGO
                  AND VB.CLASE = VP.CLASE
                  WHERE VB.COMPANIA = UN_COMPANIA
                  AND VB.ID_PROYECTO = UN_ID_PROYECTO
                  AND VB.ID BETWEEN UN_BENEFICIARIO_INICIAL AND UN_BENEFICIARIO_FINAL
                  ORDER BY VB.ID)
     LOOP*/

        <<INSERT_FACTURA>>
     OPEN MI_RS FOR MI_CONSULTA;    
       LOOP
         FETCH MI_RS INTO MI_ID_BENEFICIARIO,
                          MI_IP_CODIGO,
                          MI_NUMERO_ORDEN,
                          RSAREA_FISICA,
                          RSDESTINACION_ECONOMICA,    
                          RSGRADO_BENEFICIO,          
                          RSAREA_TOTAL,                      
                          RSVALOR_DISTRIBUIBLE;

         EXIT WHEN MI_RS%NOTFOUND;

     BEGIN


       MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_FACTURA',
                                                          UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||'''
                                                                          AND CLASE_PROYECTO = '''|| MI_CLASE_PROYECTO ||''' ' ,
                                                          UN_CAMPO    => 'NUMERO_FACTURA',
                                                          UN_INICIAL  => EXTRACT(YEAR FROM SYSDATE)||'000001'); 


      MI_CAMPOS := 'COMPANIA,
                    ID_PROCESO_FACTURACION,
                    NUMERO_PROCESO,
                    CODIGO_PROYECTO,
                    CLASE_PROYECTO,
                    NUMERO_FACTURA,
                    ID_BENEFICIARIOS,
                    IP_CODIGO,
                    IP_NUMERO_ORDEN,
                    ESTADO,
                    CREATED_BY,
                    DATE_CREATED';

    MI_VALORES :=  ' '''|| UN_COMPANIA           ||''',
                       '|| MI_ID_PROCESO         ||' ,
                       '|| MI_NUMERO_PROCESO     ||',
                     '''|| MI_CODIGO_PROYECTO    ||''',
                     '''|| MI_CLASE_PROYECTO     ||''',
                       '|| MI_CONSECUTIVO        ||',
                       '|| MI_ID_BENEFICIARIO    ||',
                     '''|| MI_IP_CODIGO          ||''',
                     '''|| MI_NUMERO_ORDEN       ||''' ,
                     '''|| 'A'                   ||''',
                     '''|| UN_USUARIO            ||''',
                     '''|| SYSDATE               ||''' ' ;


          BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_FACTURA',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
          END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                              MI_MSGERROR(1).CLAVE := 'PROYECTO';
                              MI_MSGERROR(1).VALOR := MI_CODIGO_PROYECTO;
                              MI_MSGERROR(2).CLAVE := 'CLASE';
                              MI_MSGERROR(2).VALOR := MI_CLASE_PROYECTO;


                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );    


     END;

      --UPDATE REFACTURAR
     IF UN_RECLASIFICAR = -1 THEN

           SELECT VF.ID,VF.ID_PROCESO_FACTURACION
            INTO MI_ID_FACTURA,
                 MI_ID_PROCESO
          FROM VP_FACTURA VF
          INNER JOIN VP_PROCESO_FACTURACION VPF
          ON VF.ID_PROCESO_FACTURACION  = VPF.ID
          WHERE VF.COMPANIA       = UN_COMPANIA
          AND VF.NUMERO_PROCESO   = UN_PROCESO
          AND VF.IP_CODIGO        = MI_IP_CODIGO
          AND VF.IP_NUMERO_ORDEN  = MI_NUMERO_ORDEN;


        BEGIN
            BEGIN
              MI_CAMPOS:= 'ID_PROCESO_AFECT = ' || MI_ID_PROCESO ||',
                           ID_FACTURA_AFECT = ' || MI_ID_FACTURA ||' ';

              MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA || '''
                              AND CODIGO_PROYECTO = ''' || MI_CODIGO_PROYECTO ||'''
                              AND CLASE_PROYECTO  = ''' || MI_CLASE_PROYECTO  ||'''
                              AND NUMERO_FACTURA  =   ' || MI_CONSECUTIVO     ||' ';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'VP_FACTURA'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_UPDATE_VALOR);
          END;
              BEGIN
            BEGIN
              MI_CAMPOS:= 'ESTADO = ''I''';

              MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                               AND ID      =   ' || MI_ID_FACTURA     ||' ';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'VP_FACTURA'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_UPDATE_VALOR);
          END;

     END IF;
    -- 
      <<INSERT_DETALLE_FACTURA>>
     FOR MI_RSS IN(SELECT ID,
                          CODIGO 
                          FROM VP_CONCEPTOS
                          WHERE COMPANIA = UN_COMPANIA
                          AND CODIGO_PROYECTO = MI_CODIGO_PROYECTO
                          AND CLASE = MI_CLASE_PROYECTO)
    LOOP
     BEGIN


                   SELECT ID,
              NUMERO_FACTURA 
              INTO MI_ID_FACTURA,
              MI_NUMERO_FACTURA
              FROM VP_FACTURA 
              WHERE COMPANIA = UN_COMPANIA
              AND CODIGO_PROYECTO = MI_CODIGO_PROYECTO
              AND CLASE_PROYECTO = MI_CLASE_PROYECTO
              AND NUMERO_FACTURA = MI_CONSECUTIVO;

      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_DETALLE_FACTURA',
                                                          UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' ' ,
                                                          UN_CAMPO    => 'CONSECUTIVO',
                                                          UN_INICIAL  => 1);

      BEGIN
        SELECT SUM(FACTOR)
        INTO MI_DESTINACION_ECONOMICA
        FROM VP_FACTORES
        WHERE ID = RSDESTINACION_ECONOMICA;
        SELECT SUM(FACTOR)
        INTO MI_GRADO_BENEFICIO
        FROM VP_FACTORES
        WHERE ID = RSGRADO_BENEFICIO;

          MI_VALOR_CN := (RSAREA_FISICA * MI_DESTINACION_ECONOMICA * MI_GRADO_BENEFICIO);

          MI_RES_DIV := (MI_VALOR_CN / RSAREA_TOTAL)*1000;

          MI_RES_TOTAL :=  MI_RES_DIV * RSVALOR_DISTRIBUIBLE;
      END;



    MI_CAMPOS :=    'COMPANIA,
                      CODIGO_PROYECTO,
                      CLASE,
                      ID_FACTURA,
                      NUMERO,
                      ID_CONCEPTO,
                      CONCEPTO,
                      CONSECUTIVO,
                      IP_CODIGO,
                      IP_NUMERO_ORDEN,
                      FECHA,
                      FECHA_LIMITE_PAGO,
                      VALOR,
                      CREATED_BY,
                      DATE_CREATED';


   MI_VALORES :=   ' '''|| UN_COMPANIA           ||''',
                     '''|| MI_CODIGO_PROYECTO    ||''' ,
                     '''|| MI_CLASE_PROYECTO     ||''',
                       '|| MI_ID_FACTURA         ||',
                       '|| MI_NUMERO_PROCESO     ||',
                       '|| MI_RSS.ID             ||',
                     '''|| MI_RSS.CODIGO         ||''',
                       '|| MI_NUMERO_FACTURA     ||',
                     '''|| MI_IP_CODIGO       ||''' ,
                     '''|| MI_NUMERO_ORDEN ||''',
                     '''|| SYSDATE               ||''',
                     '''|| (SYSDATE + 30)         ||''',
                       '|| MI_VALOR_CN           ||',
                     '''|| UN_USUARIO            ||''',
                     '''|| SYSDATE               ||''' ' ;



          BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_DETALLE_FACTURA',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
          END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_NODATA_CONCEPTOS
                                                    );   




     END; 

     END LOOP INSERT_DETALLE_FACTURA;

      BEGIN
            BEGIN
              MI_CAMPOS:= 'VALOR = ' || MI_VALOR_CN || '';

              MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA || '''
                              AND CODIGO_PROYECTO = ''' || MI_CODIGO_PROYECTO ||'''
                              AND CLASE_PROYECTO  = ''' || MI_CLASE_PROYECTO  ||'''
                              AND NUMERO_FACTURA  =   ' || MI_CONSECUTIVO     ||' ';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'VP_FACTURA'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_UPDATE_VALOR);
          END;
     END LOOP INSERT_FACTURA;
     CLOSE MI_RS;

 RETURN MI_RTA;
END FC_PROCESO_FACTURACION;



FUNCTION FC_CALCULO_PLUSVALIA 

/*
    NAME              : FC_CALCULO_PLUSVALIA
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 20/03/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CALCULO DEL MODULO PLUSVALIA 
    @NAME:    calculoPlusvalia
    @METHOD:  POST
    */
(
    UN_COMPANIA              IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_PROYECTO           IN  VP_PROYECTOS.ID%TYPE,
    UN_BENEFICIARIO          IN  VP_BENEFICIARIOS.ID%TYPE DEFAULT 0,
    UN_RECLASIFICAR          IN  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
    UN_PROCESO               IN  VP_PROCESO_FACTURACION.ID%TYPE DEFAULT 0,
    UN_USUARIO               IN  PCK_SUBTIPOS.TI_USUARIO
)RETURN VARCHAR2
AS 
    MI_CODIGO_PROYECTO  VP_PROYECTOS.CODIGO%TYPE;
    MI_CLASE_PROYECTO   VP_PROYECTOS.CLASE%TYPE;
    MI_ID_PROCESO       VP_PROCESO_FACTURACION.ID%TYPE;
    MI_NUMERO_PROCESO   VP_PROCESO_FACTURACION.NUMERO%TYPE;
    MI_ID_BENEFICIARIO  VP_BENEFICIARIOS.ID%TYPE;
    MI_IP_CODIGO        VP_BENEFICIARIOS.IP_CODIGO%TYPE;
    MI_NUMERO_ORDEN     VP_BENEFICIARIOS.IP_NUMERO_ORDEN%TYPE;
    MI_ID_FACTURA       VP_FACTURA.ID%TYPE;
    MI_NUMERO_FACTURA   VP_FACTURA.NUMERO_FACTURA%TYPE;
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_LONG;  
    MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_RTA              VARCHAR2(32000 CHAR);
    MI_RS               SYS_REFCURSOR;
    RS                  SYS_REFCURSOR;
    MI_RSS              SYS_REFCURSOR;
    MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMA_BASE        PCK_SUBTIPOS.TI_DOBLE;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
    MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
    MI_MES              PCK_SUBTIPOS.TI_MES;
    RSAREA_BRUTA        VP_BENEFICIARIOS.AREA_BRUTA%TYPE;
    RSAREA_NETA         VP_BENEFICIARIOS.AREA_NETA%TYPE;
    RSAFECTACIONES      VP_BENEFICIARIOS.AFECTACIONES%TYPE;
    MI_IPC_HECHO_1      PCK_SUBTIPOS.TI_DOBLE;
    MI_IPC_HECHO_2      PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL_HECHO_1    PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL_HECHO_2    PCK_SUBTIPOS.TI_DOBLE;
    MI_CONTADOR         PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
    MI_CONFIG_IPC       PCK_SUBTIPOS.TI_DOBLE;

BEGIN
         SELECT CODIGO,
                CLASE,
                ANO_BASE,
                MES_BASE
            INTO MI_CODIGO_PROYECTO,
                 MI_CLASE_PROYECTO,
                 MI_ANIO,
                 MI_MES
          FROM VP_PROYECTOS
          WHERE COMPANIA = UN_COMPANIA
          AND ID = UN_ID_PROYECTO;


          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_PROCESO_FACTURACION',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' ' ,
                                                     UN_CAMPO    => 'NUMERO',
                                                     UN_INICIAL  => 1);

          MI_CAMPOS := 'COMPANIA,
                        ID_PROYECTO,
                        CODIGO_PROYECTO,
                        CLASE_PROYECTO,
                        NUMERO,
                        FECHA,
                        CODIGOETAPA,
                        FECHA_LIMITE,
                        VALOR_TOTAL,
                        CREATED_BY,
                        DATE_CREATED';


          MI_VALORES := ' '''|| UN_COMPANIA        ||''',
                            '|| UN_ID_PROYECTO     ||' ,
                          '''|| MI_CODIGO_PROYECTO ||''',
                          '''|| MI_CLASE_PROYECTO  ||''',
                            '|| MI_CONSECUTIVO     ||',
                          '''|| SYSDATE            ||''',
                            '|| 1                  ||' ,
                          '''|| (SYSDATE + 3)      ||''',
                            '|| 0                  ||',
                          '''|| UN_USUARIO         ||''',
                          '''|| SYSDATE            ||''' ' ;    



        BEGIN                           
            BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_PROCESO_FACTURACION',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                              MI_MSGERROR(1).CLAVE := 'PROYECTO';
                              MI_MSGERROR(1).VALOR := MI_CODIGO_PROYECTO;
                              MI_MSGERROR(2).CLAVE := 'CLASE';
                              MI_MSGERROR(2).VALOR := MI_CLASE_PROYECTO;


                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
        END;   

              BEGIN    
                      BEGIN

                              SELECT ID,
                                     NUMERO
                                INTO MI_ID_PROCESO,
                                     MI_NUMERO_PROCESO
                              FROM VP_PROCESO_FACTURACION
                              WHERE COMPANIA = UN_COMPANIA
                              AND NUMERO = MI_CONSECUTIVO;

                           EXCEPTION WHEN NO_DATA_FOUND THEN
                  RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

              END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERR_PLUSVALIA_NODATA
                );
            END;  


             IF UN_RECLASIFICAR NOT IN (0) THEN 


               MI_CONSULTA := 'SELECT VB.ID,
                              VB.IP_CODIGO,
                              VB.IP_NUMERO_ORDEN,
                              VB.AREA_BRUTA,
                              VB.AREA_NETA,
                              VB.AFECTACIONES
                            FROM VP_BENEFICIARIOS VB
                            INNER JOIN VP_PROYECTOS VP
                            ON VB.ID_PROYECTO = VP.ID
                            INNER JOIN VP_FACTURA VF
                            ON VB.ID = VF.ID_BENEFICIARIOS
                            WHERE VB.COMPANIA             = ''' || UN_COMPANIA || '''
                            AND VF.NUMERO_PROCESO   =   ' || UN_PROCESO  || '
                            ORDER BY VB.ID';


        ELSE  
               MI_CONSULTA := 'SELECT VB.ID,
                              VB.IP_CODIGO,
                              VB.IP_NUMERO_ORDEN,
                              VB.AREA_BRUTA,
                              VB.AREA_NETA,
                              VB.AFECTACIONES
                            FROM VP_BENEFICIARIOS VB
                            INNER JOIN VP_PROYECTOS VP
                            ON VB.COMPANIA = VP.COMPANIA
                            AND VB.CODIGO_PROYECTO = VP.CODIGO
                            AND VB.CLASE = VP.CLASE
                            WHERE VB.COMPANIA  = ''' || UN_COMPANIA    || '''
                            AND VB.ID_PROYECTO =   ' || UN_ID_PROYECTO || '
                            AND VB.ID          =  '|| UN_BENEFICIARIO  ||' 
                            ORDER BY VB.ID ';
        END IF;

      <<INSERT_FACTURA>>
     OPEN MI_RS FOR MI_CONSULTA;    
       LOOP
         FETCH MI_RS INTO MI_ID_BENEFICIARIO,
                          MI_IP_CODIGO,
                          MI_NUMERO_ORDEN,
                          RSAREA_BRUTA,
                          RSAREA_NETA,    
                          RSAFECTACIONES;

         EXIT WHEN MI_RS%NOTFOUND;


        /*    

    FOR MI_RS IN (MI_CONSULTA)
     LOOP*/
     BEGIN


       MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_FACTURA',
                                                          UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||'''
                                                                          AND CLASE_PROYECTO = '''|| MI_CLASE_PROYECTO ||''' ' ,
                                                          UN_CAMPO    => 'NUMERO_FACTURA',
                                                          UN_INICIAL  => EXTRACT(YEAR FROM SYSDATE)||'000001');





      MI_CAMPOS := 'COMPANIA,
                    ID_PROCESO_FACTURACION,
                    NUMERO_PROCESO,
                    CODIGO_PROYECTO,
                    CLASE_PROYECTO,
                    NUMERO_FACTURA,
                    ID_BENEFICIARIOS,
                    IP_CODIGO,
                    IP_NUMERO_ORDEN,
                    ESTADO,
                    CREATED_BY,
                    DATE_CREATED';

    MI_VALORES :=  ' '''|| UN_COMPANIA           ||''',
                       '|| MI_ID_PROCESO         ||' ,
                       '|| MI_NUMERO_PROCESO     ||',
                     '''|| MI_CODIGO_PROYECTO    ||''',
                     '''|| MI_CLASE_PROYECTO     ||''',
                       '|| MI_CONSECUTIVO        ||',
                       '|| MI_ID_BENEFICIARIO    ||',
                     '''|| MI_IP_CODIGO          ||''',
                     '''|| MI_NUMERO_ORDEN       ||''',
                     '''|| 'A'                   ||''',
                     '''|| UN_USUARIO            ||''',
                     '''|| SYSDATE               ||''' ' ;


          BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_FACTURA',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
          END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                              MI_MSGERROR(1).CLAVE := 'PROYECTO';
                              MI_MSGERROR(1).VALOR := MI_CODIGO_PROYECTO;
                              MI_MSGERROR(2).CLAVE := 'CLASE';
                              MI_MSGERROR(2).VALOR := MI_CLASE_PROYECTO;


                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );    


     END;

     --UPDATE REFACTURAR
     IF UN_RECLASIFICAR = -1 THEN

         SELECT VF.ID,
          VF.ID_PROCESO_FACTURACION
            INTO MI_ID_FACTURA,
                 MI_ID_PROCESO
          FROM VP_FACTURA VF
          INNER JOIN VP_PROCESO_FACTURACION VPF
          ON VF.ID_PROCESO_FACTURACION  = VPF.ID
          WHERE VF.COMPANIA             = UN_COMPANIA
          AND VF.NUMERO_PROCESO         = UN_PROCESO
          AND VF.IP_CODIGO              = MI_IP_CODIGO
          AND VF.IP_NUMERO_ORDEN        = MI_NUMERO_ORDEN;


        BEGIN
            BEGIN
              MI_CAMPOS:= 'ID_PROCESO_AFECT = ' || MI_ID_PROCESO ||',
                           ID_FACTURA_AFECT = ' || MI_ID_FACTURA ||' ';

              MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA || '''
                              AND CODIGO_PROYECTO = ''' || MI_CODIGO_PROYECTO ||'''
                              AND CLASE_PROYECTO  = ''' || MI_CLASE_PROYECTO  ||'''
                              AND NUMERO_FACTURA  =   ' || MI_CONSECUTIVO     ||' ';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'VP_FACTURA'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_UPDATE_VALOR);
          END;


           BEGIN
            BEGIN
              MI_CAMPOS:= 'ESTADO = ''I''';

              MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                               AND ID      =   ' || MI_ID_FACTURA     ||' ';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'VP_FACTURA'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_UPDATE_VALOR);
          END;


     END IF;

     --
      <<INSERT_DETALLE_FACTURA>>
     FOR MI_RSS IN(SELECT ID,
                          CODIGO 
                          FROM VP_CONCEPTOS
                          WHERE COMPANIA = UN_COMPANIA
                          AND CODIGO_PROYECTO = MI_CODIGO_PROYECTO
                          AND CLASE = MI_CLASE_PROYECTO)
    LOOP
     BEGIN




                   SELECT ID,
              NUMERO_FACTURA 
              INTO MI_ID_FACTURA,
              MI_NUMERO_FACTURA
              FROM VP_FACTURA 
              WHERE COMPANIA = UN_COMPANIA
              AND CODIGO_PROYECTO = MI_CODIGO_PROYECTO
              AND CLASE_PROYECTO = MI_CLASE_PROYECTO
              AND NUMERO_FACTURA = MI_CONSECUTIVO;

      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'VP_DETALLE_FACTURA',
                                                          UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||'''
                                                                          AND CLASE = '''|| MI_CLASE_PROYECTO ||''' ' ,
                                                          UN_CAMPO    => 'CONSECUTIVO',
                                                          UN_INICIAL  => 1);





    BEGIN

        FOR RS IN (SELECT 
                      VP_HECHOS_PROYECTOS.CODIGO,
                      VP_BENEFICIARIOS.AREA_BRUTA,
                      VP_BENEFICIARIOS.AFECTACIONES,
                      VP_BENEFICIARIOS.AREA_NETA,
                      VP_HECHOS_PROYECTOS.BASE_PLUSVALIA,
                      VP_HECHOS_PROYECTOS.TARIFA,
                      VP_HECHOS_BENEFICIARIOS.BASE_CONTRIBUCION,
                      VP_PROYECTOS.ANO_BASE,
                      VP_PROYECTOS.MES_BASE,
                      VP_HECHOS_PROYECTOS.DIVIDE_ACUERDO,
                      VP_HECHOS_BENEFICIARIOS.CAMPO1,
                      VP_HECHOS_BENEFICIARIOS.CAMPO2
                      FROM VP_PROYECTOS
                      INNER JOIN VP_HECHOS_PROYECTOS
                      ON VP_PROYECTOS.ID = VP_HECHOS_PROYECTOS.ID_PROYECTO
                      INNER JOIN VP_HECHOS_BENEFICIARIOS
                      ON VP_HECHOS_PROYECTOS.ID = VP_HECHOS_BENEFICIARIOS.ID_HECHOS_PROYECTOS
                      INNER JOIN VP_BENEFICIARIOS
                      ON VP_HECHOS_BENEFICIARIOS.ID_BENEFICIARIOS = VP_BENEFICIARIOS.ID
                      WHERE VP_PROYECTOS.COMPANIA = UN_COMPANIA
                      AND VP_PROYECTOS.CLASE = '44'
                      AND VP_PROYECTOS.ID = UN_ID_PROYECTO
                      AND VP_BENEFICIARIOS.ID = CASE WHEN  UN_RECLASIFICAR NOT IN (0) THEN MI_ID_BENEFICIARIO ELSE UN_BENEFICIARIO END 
                      ORDER BY VP_HECHOS_PROYECTOS.CODIGO)
            LOOP            
                BEGIN      
                   MI_CONTADOR := MI_CONTADOR + 1;   

                    IF UN_RECLASIFICAR NOT IN (0) THEN



                    MI_CONFIG_IPC := PCK_PLUSVALIA.FC_CONFIGURAR_IPC_HECHO(  UN_COMPANIA      => UN_COMPANIA
                                                            ,UN_ID_PROYECTO   => UN_ID_PROYECTO
                                                            ,UN_BENEFICIARIO  => MI_ID_BENEFICIARIO
                                                            ,UN_HECHO         => RS.CODIGO
                                                            ,UN_ANIO          => RS.ANO_BASE
                                                            ,UN_MES           => RS.MES_BASE);  


                    ELSE

                    MI_CONFIG_IPC := PCK_PLUSVALIA.FC_CONFIGURAR_IPC_HECHO(  UN_COMPANIA      => UN_COMPANIA
                                                              ,UN_ID_PROYECTO   => UN_ID_PROYECTO
                                                              ,UN_BENEFICIARIO  => UN_BENEFICIARIO
                                                              ,UN_HECHO         => RS.CODIGO
                                                              ,UN_ANIO          => RS.ANO_BASE
                                                              ,UN_MES           => RS.MES_BASE);    

                    END IF;


                    IF RS.DIVIDE_ACUERDO NOT IN (0) THEN

                    MI_TOTAL := RS.CAMPO2 - RS.CAMPO1; 

                    MI_IPC_HECHO_2 := MI_CONFIG_IPC;

                    MI_TOTAL_HECHO_2 := MI_CONFIG_IPC * MI_TOTAL;

                    MI_TOTAL := MI_TOTAL_HECHO_2;

                    ELSE 
                       MI_IPC_HECHO_1    :=   MI_CONFIG_IPC;
                       MI_TOTAL_HECHO_1  :=  MI_CONFIG_IPC * RS.AREA_NETA;
                       MI_TOTAL := MI_TOTAL_HECHO_1;
                    END IF;

                    END;   

                     IF MI_CONTADOR = 2 THEN 

                     MI_TOTAL := MI_TOTAL_HECHO_1 + MI_TOTAL_HECHO_2;

                     END IF; 



           END LOOP;

    END;



    MI_CAMPOS :=    'COMPANIA,
                      CODIGO_PROYECTO,
                      CLASE,
                      ID_FACTURA,
                      NUMERO,
                      ID_CONCEPTO,
                      CONCEPTO,
                      CONSECUTIVO,
                      IP_CODIGO,
                      IP_NUMERO_ORDEN,
                      VALOR,
                      VALOR_ABONADO,
                      VALOR_CONCEPTO,
                      IPC_1,
                      VALOR_HECHO_1,
                      IPC_2,
                      VALOR_HECHO_2,
                      CREATED_BY,
                      DATE_CREATED';


   MI_VALORES :=   ' '''|| UN_COMPANIA           ||''',
                     '''|| MI_CODIGO_PROYECTO    ||''' ,
                     '''|| MI_CLASE_PROYECTO     ||''',
                       '|| MI_ID_FACTURA         ||',
                       '|| MI_NUMERO_PROCESO     ||',
                       '|| MI_RSS.ID             ||',
                     '''|| MI_RSS.CODIGO         ||''',
                       '|| MI_NUMERO_FACTURA     ||',
                     '''|| MI_IP_CODIGO       ||''' ,
                     '''|| MI_NUMERO_ORDEN ||''',
                     '''|| MI_TOTAL              ||''',
                       '|| 0                     ||',
                     '''|| MI_TOTAL              ||''',
                     '''|| MI_IPC_HECHO_1        ||''', 
                     '''|| MI_TOTAL_HECHO_1      ||''',
                     '''|| MI_IPC_HECHO_2        ||''',
                     '''|| MI_TOTAL_HECHO_2      ||''',
                     '''|| UN_USUARIO            ||''',
                     '''|| SYSDATE               ||''' ' ;



          BEGIN
              MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => 'VP_DETALLE_FACTURA',
                                                          UN_ACCION    => 'I', 
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_VALORES   => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
          END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                              MI_MSGERROR(1).CLAVE := 'PROYECTO';
                              MI_MSGERROR(1).VALOR := MI_CODIGO_PROYECTO;
                              MI_MSGERROR(2).CLAVE := 'CLASE';
                              MI_MSGERROR(2).VALOR := MI_CLASE_PROYECTO;


                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD      => SQLCODE
                                                    ,UN_ERROR_COD   => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );    


     END;
     END LOOP INSERT_DETALLE_FACTURA;

     END LOOP INSERT_FACTURA;
     CLOSE MI_RS;

       MI_RTA := MI_NUMERO_PROCESO;

  RETURN MI_RTA;    
END FC_CALCULO_PLUSVALIA;

FUNCTION FC_CONFIGURAR_IPC 

/*
    NAME              : FC_CONFIGURAR_IPC
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 20/03/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CALCULAR EL IPC POR MES Y AÑO 
    @NAME:    configurarIPC
    @METHOD:  GET
    */
(
    UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_PROYECTO     IN  VP_PROYECTOS.ID%TYPE,
    UN_BENEFICIARIO    IN  VP_BENEFICIARIOS.ID%TYPE,
    UN_ANIO            IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES             IN  PCK_SUBTIPOS.TI_MES          

)RETURN NUMBER 

AS
   MI_RS               SYS_REFCURSOR;
   MI_RSS              SYS_REFCURSOR;
   MI_RTA              PCK_SUBTIPOS.TI_DOBLE;
   MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
   MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
   INDICADOR_INGRESO   BOOLEAN := TRUE;
   INDIGUALES          PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_CONTADOR         PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;

   BEGIN

       FOR MI_RS IN(SELECT BASE_PLUSVALIA,
                          VP_HECHOS_BENEFICIARIOS.BASE_CONTRIBUCION FROM VP_PROYECTOS
                          INNER JOIN VP_HECHOS_PROYECTOS
                          ON VP_PROYECTOS.ID = VP_HECHOS_PROYECTOS.ID_PROYECTO
                          INNER JOIN VP_HECHOS_BENEFICIARIOS
                          ON VP_HECHOS_PROYECTOS.ID = VP_HECHOS_BENEFICIARIOS.ID_HECHOS_PROYECTOS
                          WHERE VP_PROYECTOS.COMPANIA = UN_COMPANIA
                          AND VP_PROYECTOS.ID = UN_ID_PROYECTO
                          AND VP_HECHOS_BENEFICIARIOS.ID_BENEFICIARIOS = UN_BENEFICIARIO)
          LOOP
           BEGIN
            MI_CONTADOR := MI_CONTADOR + 1;
            INDICADOR_INGRESO := TRUE;

            FOR MI_RSS IN(SELECT ANO,
                                NUMERO,
                                IPC
                                FROM MES
                                WHERE COMPANIA = UN_COMPANIA
                                AND TO_DATE(ANO, 'YYYY')|| TO_DATE(NUMERO, 'MM') BETWEEN TO_DATE(UN_ANIO, 'YYYY')||TO_DATE(UN_MES, 'MM') AND TO_DATE(EXTRACT(YEAR FROM SYSDATE), 'YYYY')|| TO_DATE(EXTRACT(MONTH FROM SYSDATE),'MM')
                                AND NUMERO NOT IN (0,13)
                                AND ANO NOT IN (0)
                                ORDER BY TO_DATE(ANO||NUMERO, 'YYYYMM'))
          LOOP

           BEGIN

              INDIGUALES := 0;
           IF INDICADOR_INGRESO THEN
             MI_RTA :=   MI_RS.BASE_CONTRIBUCION + (MI_RS.BASE_CONTRIBUCION * MI_RSS.IPC);
             INDICADOR_INGRESO := FALSE;
             INDIGUALES := 1;

           END IF;


           IF INDIGUALES IN (0) THEN

           MI_RTA := MI_RTA + (MI_RTA * MI_RSS.IPC);

           END IF;
           END;



           END LOOP;

           IF MI_CONTADOR = 2 THEN
           MI_RTA := MI_RTA + MI_TOTAL;
           ELSE

              MI_TOTAL := MI_RTA;


           END IF;
           END;

          END LOOP;




  RETURN MI_RTA;
END FC_CONFIGURAR_IPC;

FUNCTION FC_CALCULO_ACUERDO 
/*
    NAME              : FC_CALCULO_ACUERDO
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 04/04/2019                                                                                         
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CALCULA EL ACUERDO N°11 DE 2005 Y ACUERDO N°9 DE 2010
    @NAME:    calcularAcuerdos
    @METHOD:  GET
    */
(
    UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_PROYECTO     IN  VP_PROYECTOS.ID%TYPE,
    UN_BENEFICIARIO    IN  VP_BENEFICIARIOS.ID%TYPE,
    UN_ACUERDO         IN  VP_NORMA_URBANISTICA.ID%TYPE

)RETURN NUMBER

AS
   MI_RTA              PCK_SUBTIPOS.TI_DOBLE;
   MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
   MI_AREA_NETA        PCK_SUBTIPOS.TI_DOBLE;
   MI_FRENTE_PREDIO    PCK_SUBTIPOS.TI_DOBLE;
   MI_INDICE_OCUPACION PCK_SUBTIPOS.TI_DOBLE;
   MI_VOLADIZO         PCK_SUBTIPOS.TI_DOBLE;
   MI_NUMERO_PISOS     PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_ALTILLO          PCK_SUBTIPOS.TI_DOBLE;
   MI_VALOR_1        PCK_SUBTIPOS.TI_DOBLE;
   MI_VALOR_2        PCK_SUBTIPOS.TI_DOBLE;
   MI_VALOR_3        PCK_SUBTIPOS.TI_DOBLE;


   BEGIN

                  SELECT AREA_NETA,
                        FRENTE_PREDIO
                        INTO MI_AREA_NETA,
                        MI_FRENTE_PREDIO
                        FROM VP_BENEFICIARIOS
                        WHERE COMPANIA  = UN_COMPANIA
                        AND ID_PROYECTO = UN_ID_PROYECTO
                        AND ID = UN_BENEFICIARIO;


                SELECT VNU.INDICE_OCUPACION,
                        VNU.VOLADIZO,
                        VNU.NUMERO_PISOS,
                        VNU.ALTILLO
                        INTO MI_INDICE_OCUPACION,
                        MI_VOLADIZO,
                        MI_NUMERO_PISOS,
                        MI_ALTILLO
                        FROM  VP_NORMA_URBANISTICA VNU
                        WHERE VNU.COMPANIA = UN_COMPANIA
                        AND VNU.ID         = UN_ACUERDO;


                       MI_VALOR_1 := MI_INDICE_OCUPACION * MI_AREA_NETA;

                       MI_RTA     := MI_VOLADIZO * MI_FRENTE_PREDIO;

                       MI_VALOR_2 := (MI_VALOR_1 + MI_RTA)*(MI_NUMERO_PISOS - 1);

                       MI_VALOR_3 :=  MI_ALTILLO * MI_VALOR_2;

                       MI_TOTAL   := MI_VALOR_1 + MI_VALOR_2 + MI_VALOR_3;

  RETURN MI_TOTAL;
END FC_CALCULO_ACUERDO;



FUNCTION FC_CONFIGURAR_IPC_HECHO 

/*
    NAME              : FC_CONFIGURAR_IPC
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 20/03/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CALCULAR EL IPC POR MES Y AÑO 
    @NAME:    configurarIPCHecho
    @METHOD:  GET
    */
(
    UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_PROYECTO     IN  VP_PROYECTOS.ID%TYPE,
    UN_BENEFICIARIO    IN  VP_BENEFICIARIOS.ID%TYPE,
    UN_HECHO           IN  VP_HECHOS_PROYECTOS.ID%TYPE,
    UN_ANIO            IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES             IN  PCK_SUBTIPOS.TI_MES          

)RETURN NUMBER 

AS

   MI_RS               SYS_REFCURSOR;
   MI_RSS              SYS_REFCURSOR;
   MI_RTA              PCK_SUBTIPOS.TI_DOBLE;
   MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
   MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
   INDICADOR_INGRESO   BOOLEAN := TRUE;
   INDIGUALES          PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_CONTADOR         PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
   MI_BASE_PLUSVALIA   PCK_SUBTIPOS.TI_DOBLE;
   MI_BASE_CONTRIBUCION PCK_SUBTIPOS.TI_DOBLE;

   BEGIN

                SELECT DISTINCT VP_HECHOS_PROYECTOS.BASE_PLUSVALIA,
                          VP_HECHOS_BENEFICIARIOS.BASE_CONTRIBUCION      
                          INTO MI_BASE_PLUSVALIA,
                          MI_BASE_CONTRIBUCION
                          FROM VP_PROYECTOS
                          INNER JOIN VP_HECHOS_PROYECTOS
                          ON VP_PROYECTOS.ID = VP_HECHOS_PROYECTOS.ID_PROYECTO
                          INNER JOIN VP_HECHOS_BENEFICIARIOS
                          ON VP_PROYECTOS.ID = VP_HECHOS_BENEFICIARIOS.ID_PROYECTOS
                          WHERE VP_PROYECTOS.COMPANIA = UN_COMPANIA
                          AND VP_PROYECTOS.ID = UN_ID_PROYECTO
                          AND VP_HECHOS_PROYECTOS.CODIGO = UN_HECHO
                          AND VP_HECHOS_BENEFICIARIOS.CODIGO_HECHO = UN_HECHO;


        BEGIN

            INDICADOR_INGRESO := TRUE;

            FOR MI_RSS IN(SELECT ANO,
                                NUMERO,
                                IPC
                                FROM MES
                                WHERE COMPANIA = UN_COMPANIA
                                AND TO_DATE(ANO, 'YYYY')|| TO_DATE(NUMERO, 'MM') BETWEEN TO_DATE(UN_ANIO, 'YYYY')||TO_DATE(UN_MES, 'MM') AND TO_DATE(EXTRACT(YEAR FROM SYSDATE), 'YYYY')|| TO_DATE(EXTRACT(MONTH FROM SYSDATE),'MM')
                                AND NUMERO NOT IN (0,13)
                                AND ANO NOT IN (0)
                                ORDER BY TO_DATE(ANO||NUMERO, 'YYYYMM'))
          LOOP

           BEGIN

              INDIGUALES := 0;
           IF INDICADOR_INGRESO THEN
             MI_RTA :=   MI_BASE_CONTRIBUCION + (MI_BASE_CONTRIBUCION * MI_RSS.IPC);
             INDICADOR_INGRESO := FALSE;
             INDIGUALES := 1;

           END IF;


           IF INDIGUALES IN (0) THEN

           MI_RTA := MI_RTA + (MI_RTA * MI_RSS.IPC);

           END IF;
           END;



           END LOOP;


          END;


  RETURN MI_RTA;
END FC_CONFIGURAR_IPC_HECHO;

END PCK_PLUSVALIA;