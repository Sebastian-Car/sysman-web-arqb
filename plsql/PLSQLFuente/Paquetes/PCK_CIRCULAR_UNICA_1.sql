create or replace PACKAGE BODY "PCK_CIRCULAR_UNICA" AS

PROCEDURE PR_PREPARAR_CODIGOS 
/*
    NAME              : PR_PREPARAR_CODIGOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN LEONARDO MALAVER JIMÉNEZ
    DATE MIGRADOR     : 05/07/2018
    TIME              : 04:34 PM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite preparar los códigos de un año a otro

      @NAME:    prepararCodigos
      @METHOD:  POST
  */
    (
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO_INI  IN PCK_SUBTIPOS.TI_ANIO,
      UN_ANIO_FIN  IN PCK_SUBTIPOS.TI_ANIO,
      UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO,
      UN_OPCION IN PCK_SUBTIPOS.TI_ENTERO
    )     
  AS 
      MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
      MI_EXISTE                     PCK_SUBTIPOS.TI_ENTERO;
      
      
    BEGIN

IF UN_OPCION = 1 THEN

    FOR RS IN 
    (
        SELECT  COMPANIA,
                CODIGO,
                ANO,
                NOMBRE,
                TIPOCUENTA,
                MOVIMIENTO
        FROM PLAN_CIRCULAR_UNICA
        WHERE COMPANIA  = UN_COMPANIA
        AND   ANO       = UN_ANIO_INI   
    )
    LOOP
      SELECT COUNT (1)
      INTO MI_EXISTE
      FROM PLAN_CIRCULAR_UNICA
      WHERE COMPANIA  = UN_COMPANIA
      AND CODIGO      = RS.CODIGO
      AND ANO         = UN_ANIO_FIN;
      
      IF MI_EXISTE       = 0 THEN
                            
        MI_CAMPOS      :=   'COMPANIA,                                          
                            CODIGO,                                          
                            ANO,                                          
                            NOMBRE,                                          
                            TIPOCUENTA,                                          
                            MOVIMIENTO,                                                                                
                            DATE_CREATED,                                           
                            CREATED_BY';
                            
        MI_VALORES     :=''''||UN_COMPANIA||''',                                          
                            '''||RS.CODIGO||''',                                          
                            '||UN_ANIO_FIN||',                                          
                            '''||RS.NOMBRE||''',                                          
                            '''||RS.TIPOCUENTA||''',                                          
                            '||RS.MOVIMIENTO||',                    
                            SYSDATE,                                          
                            ''' || UN_USUARIO ||'''' ;  

      BEGIN     
       BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'PLAN_CIRCULAR_UNICA', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CIRUNICA;
       END;
       
       
       
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_CIRUNICA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE, 
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CIRUNICA_INSPLANCIRUNI
                                        );
       END;    
    END IF;
    END LOOP; 
    
    ELSIF UN_OPCION = 2 THEN
    
        FOR RS IN 
    (
        SELECT  COMPANIA, 
                ANO,
                CODIGO,                
                NOMBRECUENTA,
                CLASE
        FROM TIPOCAMPO_SIA
        WHERE COMPANIA  = UN_COMPANIA
        AND   ANO       = UN_ANIO_INI   
    )
    LOOP
      SELECT COUNT (1)
      INTO MI_EXISTE
      FROM TIPOCAMPO_SIA
      WHERE COMPANIA  = UN_COMPANIA
      AND CODIGO      = RS.CODIGO
      AND ANO         = UN_ANIO_FIN;
      
      IF MI_EXISTE       = 0 THEN
                            
        MI_CAMPOS      :=   'COMPANIA,
                             ANO,
                            CODIGO,
                            NOMBRECUENTA,                                          
                            CLASE,                                                                                
                            DATE_CREATED,                                           
                            CREATED_BY';
                            
        MI_VALORES     :=''''||UN_COMPANIA||''',                      
                            '||UN_ANIO_FIN||',
                            '''||RS.CODIGO||''',
                            '''||RS.NOMBRECUENTA||''',                                          
                            '''||RS.CLASE||''',                    
                            SYSDATE,                                          
                            ''' || UN_USUARIO ||'''' ;  

      BEGIN     
       BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'TIPOCAMPO_SIA', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSIA;
       END;
       
       
       
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_SYSMANSIA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE, 
                                        UN_ERROR_COD  => PCK_ERRORES.EXC_SYSMANSIA_INSTCAMPOSIA
                                        );
       END;    
    END IF;
    END LOOP; 
    
    END IF;

END PR_PREPARAR_CODIGOS;

FUNCTION FC_ACTUALIZAR_VIG_PLANPPTAL 
(
 /*
      NAME              : FC_ACTUALIZAR_VIG_PLANPPTAL 
      AUTHOR            : JONATHAN LEONARDO MALAVER JIMÉNEZ
      DATE              : 13/07/2018
      TIME              : 10:00 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Permite actualizar la vigencia de un año a otro y genera un archivo plano con 
                          las inconsistencias encontradas durante el proceso.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_ANIO_INI       => Año a actualizar.
                          UN_ANIO_FIN       => Año destino.
                          UN_USUARIO        => Usuario de ingreso a la aplicación.
      
      @NAME  :  actualizarVigenciaPlanPptal
      @METHOD:  GET 
    */ 
    
   UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANIO_INI           IN PCK_SUBTIPOS.TI_ANIO,
   UN_ANIO_FIN           IN PCK_SUBTIPOS.TI_ANIO,
   UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
)
   RETURN  CLOB
	
AS  
  	MI_RTA                  CLOB;
  	MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTE               PCK_SUBTIPOS.TI_ENTERO;

BEGIN 
  
		FOR MI_RS IN (
                    SELECT COMPANIA,
                            ANO,
                            CODIGO, 
                            CENTRO_COSTO,
                            TERCERO,
                            SUCURSAL,
                            APROPIACIONINICIAL, 
                            REFERENCIA,
                            FUENTE_RECURSO,
                            NATURALEZA,
                            RECAUDO_VA,
                            FUT_TRANSFERENCIA,
                            DISPONIBILIDAD_INICIAL_FUT,
                            AUXILIAR,
                            CODIGO_SUPERSALUD,
                            CODIGO_SIA
                    FROM PLAN_PPTAL_CONFIG
                    WHERE COMPANIA = UN_COMPANIA  
                      AND ANO = UN_ANIO_INI
                      AND CODIGO  IN ( 
                                       SELECT CODIGO 
                                       FROM PLAN_PRESUPUESTAL 
                                       WHERE COMPANIA = UN_COMPANIA  
                                       AND ANO = UN_ANIO_FIN
                                      )
                     AND CENTRO_COSTO IN ( 
                                            SELECT CODIGO 
                                            FROM CENTRO_COSTO 
                                            WHERE COMPANIA = UN_COMPANIA
                                              AND ANO = UN_ANIO_FIN
                                          )
                  ) 
			LOOP
        SELECT COUNT (1)
        INTO MI_EXISTE
        FROM PLAN_PPTAL_CONFIG
        WHERE COMPANIA    = UN_COMPANIA
        AND ANO           = UN_ANIO_FIN
        AND CODIGO        = MI_RS.CODIGO
        AND CENTRO_COSTO  = MI_RS.CENTRO_COSTO
        AND TERCERO       = MI_RS.TERCERO
        AND SUCURSAL      = MI_RS.SUCURSAL
        AND AUXILIAR      = MI_RS.AUXILIAR 
        AND REFERENCIA    = MI_RS.REFERENCIA 
        AND FUENTE_RECURSO = MI_RS.FUENTE_RECURSO;

      IF MI_EXISTE       = 0 THEN
      
        MI_CAMPOS := '
          COMPANIA,
          ANO,
          CODIGO, 
          CENTRO_COSTO,
          TERCERO,
          SUCURSAL,
          APROPIACIONINICIAL, 
          REFERENCIA,
          FUENTE_RECURSO,
          NATURALEZA,
          RECAUDO_VA,
          FUT_TRANSFERENCIA,
          DISPONIBILIDAD_INICIAL_FUT,
          AUXILIAR,
          CODIGO_SUPERSALUD,
          CODIGO_SIA,
          CREATED_BY,
          DATE_CREATED';
        MI_VALORES := 
          ' '''||MI_RS.COMPANIA||''',
            '||UN_ANIO_FIN||',
            '''||MI_RS.CODIGO||''', 
            '''||MI_RS.CENTRO_COSTO||''',
            '''||MI_RS.TERCERO||''',
            '''||MI_RS.SUCURSAL||''',
            '||MI_RS.APROPIACIONINICIAL||', 
            '''||MI_RS.REFERENCIA||''',
            '''||MI_RS.FUENTE_RECURSO||''',
            '''||MI_RS.NATURALEZA||''',
            '||MI_RS.RECAUDO_VA||',
            '||MI_RS.FUT_TRANSFERENCIA||',
            '||MI_RS.DISPONIBILIDAD_INICIAL_FUT||',
            '''||MI_RS.AUXILIAR||''',
            '''||MI_RS.CODIGO_SUPERSALUD||''',
            '''||MI_RS.CODIGO_SIA||''',
            '''||UN_USUARIO||''',
            SYSDATE';  
      BEGIN
        BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA => 'PLAN_PPTAL_CONFIG', 
                                                      UN_ACCION => 'I', 
                                                      UN_CAMPOS => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);
                                                      
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CIRUNICA;
      END;
      EXCEPTION
       WHEN PCK_EXCEPCIONES.EXC_CIRUNICA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE, 
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CIRUNICA_INSPLANPPTALCONF );
      END;                                
    END IF; 
    END LOOP;
    MI_RTA := 'Las siguientes inconsistencias se presentan al actualizar la vigencia del año '||UN_ANIO_INI||' al año '||UN_ANIO_FIN||CHR(13)||CHR(10);
    MI_RTA := MI_RTA || 'Verificar que se encuentran creados en el año '||UN_ANIO_FIN||CHR(13)||CHR(10);
       
       MI_RTA := MI_RTA || 'AÑO'                  ||CHR(9)|| 
                'CODIGO'                ||CHR(9)||
                'CENTRO COSTO'          ||CHR(9)||
                'TERCERO'               ||CHR(9)||
                'SUCURSAL'              ||CHR(9)||
                'AUXILIAR'              ||CHR(9)||
                'REFERENCIA'            ||CHR(9)||
                'FUENTE_RECURSO'        
                ||CHR(13)||CHR(10); 
    
    FOR MI_RS IN (
                  SELECT COMPANIA,
                         ANO,
                         CODIGO, 
                         CENTRO_COSTO,
                         TERCERO,
                         SUCURSAL,
                         APROPIACIONINICIAL, 
                         REFERENCIA,
                         FUENTE_RECURSO,
                         NATURALEZA,
                         RECAUDO_VA,
                         FUT_TRANSFERENCIA,
                         DISPONIBILIDAD_INICIAL_FUT,
                         AUXILIAR
                   FROM PLAN_PPTAL_CONFIG
                    WHERE COMPANIA = UN_COMPANIA 
                    AND ANO = UN_ANIO_INI
                    AND (CODIGO NOT  IN ( 
                                        SELECT CODIGO 
                                        FROM PLAN_PRESUPUESTAL 
                                         WHERE COMPANIA = UN_COMPANIA  
                                         AND ANO = UN_ANIO_FIN
                                       )
                    OR CENTRO_COSTO NOT IN ( 
                                             SELECT CODIGO 
                                             FROM CENTRO_COSTO 
                                              WHERE COMPANIA = UN_COMPANIA
                                              AND ANO = UN_ANIO_FIN
                                            ))
                )
    
    LOOP
        MI_RTA := MI_RTA || UN_ANIO_FIN            ||CHR(9)|| 
                            MI_RS.CODIGO          ||CHR(9)||
                            MI_RS.CENTRO_COSTO    ||CHR(9)||
                            MI_RS.TERCERO         ||CHR(9)||
                            MI_RS.SUCURSAL        ||CHR(9)||
                            MI_RS.AUXILIAR        ||CHR(9)||
                            MI_RS.REFERENCIA      ||CHR(9)||
                            MI_RS.FUENTE_RECURSO  ||CHR(13)||CHR(10); 
    END LOOP;
		RETURN MI_RTA;		
END FC_ACTUALIZAR_VIG_PLANPPTAL;

FUNCTION FC_CARGAR_CONFIG_PRESUPUESTAL 
(
/*
    NAME              :cargarInterfazporXls
    AUTHORS           : NCARDENAS
    AUTHOR MIGRACION  : NCARDENAS
    DATE MIGRADOR     :24/06/2026
    TIME              :5:21
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : VALIDACIÓN Y CARGUE DE  INFORMACIÓN DE CODIGO DE SUPERSALUD
    PARAMETERS        :
      --NAME:    cargarInterfazporXls
      --METHOD:  GET
  */
    UN_TABLA        IN VARCHAR2,
    UN_CADENA       IN CLOB,
    UN_USUARIO      IN VARCHAR2,
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN ANO.NUMERO%TYPE
  )  RETURN CLOB
AS
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_MENSAJE              CLOB;
MI_ERRORES              PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;
MI_TOTERRORES           PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;
MI_EXISTE               NUMBER(4);

MI_FUENTE              VARCHAR2(20);
MI_CENTRO              VARCHAR2(20);
MI_AUXILIAR_GEN           VARCHAR2(20);
MI_REFERENCIA          VARCHAR2(20);

MI_CONDICION            CLOB;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              CLOB;
MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
MI_EXISTEMSG            BOOLEAN:= FALSE;
MI_CONSECUTIVO          NUMBER(5) := 0;
BEGIN
       MI_MENSAJE := MI_MENSAJE
        || '*** INFORME DE ERRORES EN EL CARGUE DE LA PLANTILLA DE CONFIGURACION DE PLAN PRESUPUESTAL '|| ' ***'
        || CHR(10) || CHR(13)
        || CHR(10) || CHR(13);
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                       UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
        LOOP
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                         UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
        FOR I IN MI_DATOS_COLUMNAS.FIRST .. MI_DATOS_COLUMNAS.LAST LOOP
            IF TRIM(UPPER(MI_DATOS_COLUMNAS(I))) = 'NODATO' THEN
                MI_DATOS_COLUMNAS(I) := NULL;
            END IF;
    END LOOP;
    MI_ERRORES          := 0;
    
    
   MI_FUENTE := NVL(TRIM(MI_DATOS_COLUMNAS(5)),'99999999999999999999');
   MI_CENTRO := NVL(TRIM(MI_DATOS_COLUMNAS(7)),'99999999999999999999');
   MI_AUXILIAR_GEN := NVL(TRIM(MI_DATOS_COLUMNAS(8)),'99999999999999999999');
   MI_REFERENCIA := NVL(TRIM(MI_DATOS_COLUMNAS(9)),'99999999999999999999');
    ------ Validacion de Nulos ------
    
     IF TRIM(MI_DATOS_COLUMNAS(1)) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La CUENTA es NULA, este campo es obligatorio y necesaria para realizar insertar la información.'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF TRIM(MI_DATOS_COLUMNAS(6)) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' El CODIGO SUPERSALUD, es NULO, este campo es obligatorio. Ya que es la información a actualizar'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    ----- Validacion de existencia de codigo ----
    IF TRIM(MI_DATOS_COLUMNAS(1)) IS NOT NULL THEN
         BEGIN
            SELECT COUNT(*)
             INTO MI_EXISTE    
            FROM PLAN_PRESUPUESTAL
            WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANO
                AND CODIGO = MI_DATOS_COLUMNAS(1);
             IF MI_EXISTE = 0 THEN
                MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El RUBRO Numero: ' || MI_DATOS_COLUMNAS(1) || ' NO existe en el PLAN PRESUPUESTAL, Por favor revise configuraciones. ' ||  CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES:= MI_TOTERRORES + 1;
            END IF;
        END;
    END IF;
    
    ------ Validación Fuente recursos ------
    BEGIN 
        SELECT COUNT(*)
        INTO MI_EXISTE
        FROM FUENTE_RECURSOS
        WHERE COMPANIA = UN_COMPANIA
        AND ANO = UN_ANO
        AND CODIGO = MI_FUENTE;
         IF MI_EXISTE = 0 THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El FUENTE DE RECURSO: ' || MI_FUENTE || ' NO existe, Por favor revise configuraciones. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END;
    
    IF LENGTH(MI_FUENTE) > 20 THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La FUENTE RECURSOS: '|| MI_FUENTE ||' no puede SUPERAR 20 CARACTERES'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF NOT REGEXP_LIKE(MI_FUENTE, '^[A-Za-z0-9 ]+$') THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La FUENTE RECURSOS: '|| MI_FUENTE ||'  tiene caracteres inválidos'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;

    END IF;
    
    ------ Validación Centro de Costos ------
    BEGIN 
        SELECT COUNT(*)
        INTO MI_EXISTE
        FROM CENTRO_COSTO
        WHERE COMPANIA = UN_COMPANIA
        AND ANO = UN_ANO
        AND CODIGO = MI_CENTRO;     
        IF MI_EXISTE = 0 THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El CENTRO DE COSTOS: ' || MI_CENTRO || ' NO existe, Por favor revise configuraciones. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END;

    IF LENGTH( MI_CENTRO) > 20 THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La CENTRO DE COSTOS: '|| MI_CENTRO ||' no puede SUPERAR 20 CARACTERES'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF NOT REGEXP_LIKE( MI_CENTRO, '^[A-Za-z0-9 ]+$') THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La CENTRO DE COSTOS: '|| MI_CENTRO ||' tiene caracteres inválidos'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;

    END IF;
    
    ------ Validación Auxiliar ------
    
    BEGIN 
        SELECT COUNT(*)
        INTO MI_EXISTE
        FROM AUXILIAR
        WHERE COMPANIA = UN_COMPANIA
        AND ANO = UN_ANO
        AND CODIGO = MI_AUXILIAR_GEN;     
        IF MI_EXISTE = 0 THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El AUXILIAR: ' || MI_AUXILIAR_GEN || ' NO existe, Por favor revise configuraciones. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END;
    
    IF LENGTH( MI_AUXILIAR_GEN) > 20 THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La Auxiliar General: '|| MI_AUXILIAR_GEN ||' no puede SUPERAR 20 CARACTERES'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF NOT REGEXP_LIKE( MI_AUXILIAR_GEN, '^[A-Za-z0-9 ]+$') THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La Auxiliar General: '|| MI_AUXILIAR_GEN ||' tiene caracteres inválidos'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;

    END IF;
    
     ------ Validación Referencia ------
    
    BEGIN 
        SELECT COUNT(*)
        INTO MI_EXISTE
        FROM REFERENCIA
        WHERE COMPANIA = UN_COMPANIA
        AND ANO = UN_ANO
        AND CODIGO = MI_REFERENCIA;
        IF MI_EXISTE = 0 THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El REFERENCIA: ' || MI_REFERENCIA || ' NO existe, Por favor revise configuraciones. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END;
    
    IF LENGTH( MI_REFERENCIA) > 20 THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La REFERENCIA: '|| MI_REFERENCIA ||' no puede SUPERAR 20 CARACTERES'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF NOT REGEXP_LIKE( MI_REFERENCIA, '^[A-Za-z0-9 ]+$') THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La REFERENCIA: '|| MI_REFERENCIA ||' tiene caracteres inválidos'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;

    END IF;
   IF MI_ERRORES > 0 THEN
        CONTINUE;
    END IF;
    -----       Cargue de Información       ------
    
    MI_VALORES := 'UPDATE SET TABLA.CODIGO_SUPERSALUD =  VISTA.CODIGO_SUPERSALUD';
     
    MI_CONDICION:='SELECT  '''|| UN_COMPANIA|| ''' COMPANIA, ' || UN_ANO|| ' ANO,
                           '''|| MI_DATOS_COLUMNAS(1)|| ''' CODIGO, '''|| MI_FUENTE || ''' FUENTE_RECURSO,
                           '''|| MI_DATOS_COLUMNAS(6)|| ''' CODIGO_SUPERSALUD,
                           '''|| MI_CENTRO || ''' CENTRO_COSTO,
                           '''|| MI_AUXILIAR_GEN || ''' AUXILIAR,
                           '''|| MI_REFERENCIA|| ''' REFERENCIA  FROM DUAL';
    BEGIN
     MI_CAMPOS   := 'TABLA.COMPANIA = VISTA.COMPANIA
                        AND TABLA.ANO = VISTA.ANO
                        AND TABLA.CODIGO = VISTA.CODIGO
                        AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO
                        AND TABLA.CENTRO_COSTO = VISTA.CENTRO_COSTO
                        AND TABLA.AUXILIAR = VISTA.AUXILIAR
                        AND TABLA.REFERENCIA = VISTA.REFERENCIA';
                            
           
            
            MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA       => UN_TABLA
                                         ,UN_ACCION      => 'MM'
                                         ,UN_MERGEUSING  =>  MI_CONDICION
                                         ,UN_MERGEENLACE =>  MI_CAMPOS
                                             ,UN_MERGEEXISTE =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    END LOOP;
     MI_MENSAJE := MI_MENSAJE ||
                  'TOTAL DE ERRORES: ' || MI_TOTERRORES || CHR(10);
    RETURN MI_MENSAJE;
    
END FC_CARGAR_CONFIG_PRESUPUESTAL;

END PCK_CIRCULAR_UNICA;