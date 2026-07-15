create or replace PACKAGE BODY PCK_PROGVISITAS AS

FUNCTION FC_REPROGRAMAR_VISITA
  /*
    NAME              : FC_REPROGRAMAR_VISITA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE MIGRADOR     : 22/04/2016
    TIME              : 9:13 AM 
    SOURCE MODULE     : 
    DESCRIPTION       : FUNCION QUE TOMA LOS DATOS DE UNA VISITA EXISTENTE PARA REPROGRAMARLA. SE CONSERVA LOS DATOS PRINCIPALES GENERANDO UN NUEVO CONSECUTIVO. 
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 12:34 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones

  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASEVISITA    IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_CODVISITA      IN PCK_SUBTIPOS.TI_ENTERO,
    UN_NROVISITA      IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_FECHAHORA_INI  IN VARCHAR2,
    UN_FECHAHORA_FIN  IN VARCHAR2,
    UN_ESTADOVISITA   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO        IN VARCHAR2 
  )
RETURN VARCHAR2
AS
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_RESP_NASIG     PCK_SUBTIPOS.TI_ENTERO;
  MI_RESP_ASIG      PCK_SUBTIPOS.TI_ENTERO;
  MI_ROWID_NUEVO    VARCHAR2(3000 CHAR) := 'X';
  MI_CRUZAHORARIO   PCK_SUBTIPOS.TI_ENTERO:= 0;
BEGIN

 <<EXTRAER_VISITAS>>
  FOR RS_VISITA IN (
              SELECT  BARRIO
                      ,DIRECCION
                      ,MATRICULA_MERCANTIL
                      ,EXPEDIENTE
                      ,DESCIPCION
                      ,ACTA
                      ,ZONA_CARDINAL
                      ,TIPO_ESTABLECIMIENTO
                      ,VEHICULO
                      ,CODIGO_PREDIO
              FROM    VISITA   
              WHERE   COMPANIA    = UN_COMPANIA
                AND   CLASE       = UN_CLASEVISITA 
                AND   CODIGO      = UN_CODVISITA
                AND   NUMERO      = UN_NROVISITA
            ) LOOP

   BEGIN
    BEGIN
    MI_CAMPOS := 'COMPANIA
                  ,CLASE
                  ,CODIGO
                  ,NUMERO
                  ,FECHA_INICIO
                  ,FECHA_FIN
                  ,ESTADO
                  ,ACTA
                  ,BARRIO
                  ,DIRECCION
                  ,MATRICULA_MERCANTIL
                  ,EXPEDIENTE
                  ,DESCIPCION
                  ,ZONA_CARDINAL
                  ,TIPO_ESTABLECIMIENTO
                  ,VEHICULO
                  ,CODIGO_PREDIO
                  ,CREATED_BY, DATE_CREATED';

    MI_VALORES := '''' || UN_COMPANIA || '''
                  , ' || UN_CLASEVISITA || '
                  , ' || UN_CODVISITA ||'
                  , ' || UN_NROVISITA || ' + 1 
                  , TO_DATE(''' || UN_FECHAHORA_INI ||''', ''DD/MM/YYYY HH24:MI:SS'')
                  , TO_DATE(''' || UN_FECHAHORA_FIN || ''', ''DD/MM/YYYY HH24:MI:SS'')
                  , ' || UN_ESTADOVISITA ||'
                  , ''' || RS_VISITA.ACTA || '''
                  , ''' || RS_VISITA.BARRIO || '''
                  , ''' || RS_VISITA.DIRECCION || '''
                  , ''' || RS_VISITA.MATRICULA_MERCANTIL ||'''
                  , ''' || RS_VISITA.EXPEDIENTE || '''
                  , ''' || RS_VISITA.DESCIPCION || '''
                  , ' ||   CASE WHEN RS_VISITA.ZONA_CARDINAL IS NULL THEN 'NULL' ELSE '' || RS_VISITA.ZONA_CARDINAL END  ||'
                  , ' ||  CASE WHEN RS_VISITA.TIPO_ESTABLECIMIENTO IS NULL THEN 'NULL' ELSE '' || RS_VISITA.TIPO_ESTABLECIMIENTO END  || '
                  ,''' || RS_VISITA.VEHICULO || '''
                  , ''' || RS_VISITA.CODIGO_PREDIO ||'''
                  , ''' || UN_USUARIO || '''
                  , SYSDATE ';  

    MI_ROWID_NUEVO := PCK_DATOS.FC_ACME (UN_TABLA    => 'VISITA'
                                         ,UN_ACCION  => 'I'
                                         ,UN_CAMPOS  =>  MI_CAMPOS
                                         ,UN_VALORES => MI_VALORES
                                         ,UN_ROWID   =>'1'); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_PROGRAMACION_VISITAS;
    END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PROGRAMACION_VISITAS THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
            ,UN_ERROR_COD => PCK_ERRORES.ERR_PROGVISITAS_INSERT_VISITA
         );    
   END; 
    --PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME ('VISITA', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL ); 
    IF LENGTH(MI_ROWID_NUEVO) > 0 THEN  

      -- INSERTAR LOS RESPONSABLES VALIDANDO QUE NO TENGAN VISITAS EN LAS FECHAS Y HORAS PROGRAMADAS. 
      <<EXTRAER_RESPONSABLES>>
      FOR RS_RESP IN  (
                        SELECT  DEPENDENCIA, RESPONSABLE, SUCURSAL, EMAIL , VISITADOR_PRINCIPAL
                        FROM    RESPONSABLE_VISITA
                        WHERE   COMPANIA      = UN_COMPANIA
                          AND   CLASE_VISITA  = UN_CLASEVISITA
                          AND   CODIGO_VISITA = UN_CODVISITA  
                          AND   NUMERO_VISITA = UN_NROVISITA  
                      )LOOP
        MI_CRUZAHORARIO :=0;              
        <<EXTRAER_HORARIO_CRUZADO>>
        FOR RS_HORRESP IN  (
                            WITH ULTIMA_VISITA AS 
                            (
                            SELECT 
                              VISITA.COMPANIA, 
                              VISITA.CLASE,
                              VISITA.CODIGO,
                              MAX(VISITA.NUMERO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTNROVISITA
                            FROM VISITA 
                            GROUP BY VISITA.COMPANIA, VISITA.CLASE, VISITA.CODIGO
                            ), ULTVISITA AS 
                            (SELECT 
                              U.COMPANIA, 
                              U.CLASE, 
                              U.CODIGO, 
                              U.ULTNROVISITA,
                              V.ESTADO,   
                              V.FECHA_INICIO,
                              V.FECHA_FIN 
                            FROM ULTIMA_VISITA U
                              INNER JOIN VISITA V
                                ON U.COMPANIA = V.COMPANIA
                                AND U.CLASE = V.CLASE
                                AND U.CODIGO = V.CODIGO
                                AND U.ULTNROVISITA = V.NUMERO)
                            SELECT 
                              R.CODIGO_VISITA, 
                              V.ULTNROVISITA   NUMERO_VISITA
                            FROM   RESPONSABLE_VISITA R 
                               INNER JOIN ULTVISITA  V
                               ON  R.COMPANIA = V.COMPANIA 
                               AND  R.CLASE_VISITA = V.CLASE 
                               AND  R.CODIGO_VISITA = V.CODIGO 
                               AND  R.NUMERO_VISITA = V.ULTNROVISITA  
                                INNER JOIN ESTADO_VISITA E
                                ON V.COMPANIA = E.COMPANIA
                                AND V.ESTADO = E.CODIGO 
                            WHERE  R.COMPANIA           = UN_COMPANIA
                              AND  R.CODIGO_VISITA      <> UN_CODVISITA 
                              AND  R.RESPONSABLE        = RS_RESP.RESPONSABLE
                              AND  R.SUCURSAL           = RS_RESP.SUCURSAL
                              AND  (TO_DATE(V.FECHA_INICIO  , 'DD/MM/YYYY HH24:MI:SS')     < TO_DATE(UN_FECHAHORA_INI , 'DD/MM/YYYY HH24:MI:SS')
                              AND   TO_DATE(V.FECHA_FIN  , 'DD/MM/YYYY HH24:MI:SS')         > TO_DATE(UN_FECHAHORA_INI , 'DD/MM/YYYY HH24:MI:SS')) 
                               OR  (TO_DATE(V.FECHA_INICIO  , 'DD/MM/YYYY HH24:MI:SS')     < TO_DATE(UN_FECHAHORA_FIN , 'DD/MM/YYYY HH24:MI:SS') 
                              AND   TO_DATE(V.FECHA_FIN  , 'DD/MM/YYYY HH24:MI:SS')         > TO_DATE(UN_FECHAHORA_FIN , 'DD/MM/YYYY HH24:MI:SS'))

                           )LOOP
            MI_CRUZAHORARIO  := 1;
        END LOOP EXTRAER_HORARIO_CRUZADO;
        --SI NO SE CRUZA HORARIO INSERTA EL RESPONSABLE
        IF MI_CRUZAHORARIO = 0 THEN
         BEGIN
          BEGIN

          MI_CAMPOS := 'COMPANIA
                        ,CLASE_VISITA
                        ,CODIGO_VISITA
                        ,NUMERO_VISITA
                        ,DEPENDENCIA
                        ,RESPONSABLE
                        ,SUCURSAL
                        ,CREATED_BY
                        ,DATE_CREATED
                        ,EMAIL
                        ,VISITADOR_PRINCIPAL';

          MI_VALORES := '''' || UN_COMPANIA || '''
                        , ' || UN_CLASEVISITA || '
                        , ' || UN_CODVISITA ||'
                        , ' || UN_NROVISITA || ' + 1
                        , ''' || RS_RESP.DEPENDENCIA || '''
                        , ''' || RS_RESP.RESPONSABLE || '''
                        , ''' || RS_RESP.SUCURSAL || '''
                        , ''' || UN_USUARIO || '''
                        , SYSDATE
                        , ''' || RS_RESP.EMAIL || '''
                        , '|| RS_RESP.VISITADOR_PRINCIPAL ||' ';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RESPONSABLE_VISITA'
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES);  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PROGRAMACION_VISITAS;

           END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PROGRAMACION_VISITAS THEN    
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                  ,UN_ERROR_COD => PCK_ERRORES.ERR_PROGVISITAS_INSERT_RVISITA
               );
         END; 

        END IF;
      END LOOP EXTRAER_RESPONSABLES;
    END IF; 
  END LOOP EXTRAER_VISITAS;  

RETURN MI_ROWID_NUEVO;

END FC_REPROGRAMAR_VISITA;

END PCK_PROGVISITAS;