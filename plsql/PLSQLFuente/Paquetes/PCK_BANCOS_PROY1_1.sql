create or replace PACKAGE BODY "PCK_BANCOS_PROY1" AS

FUNCTION FC_VALIDARDIGITOS
/*
    NAME              : VALIDARDIGITOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Diego Maldonado
    DATE MIGRADOR     : 19/08/2015
    TIME              : 2:42 PM
    SOURCE MODULE     : BancoProyectos
    MODIFIER          : JUAN CAMILO
    DATE MODIFIED     : 27/01/2017
    TIME              : 15:00
    DESCRIPTION       : PERMITE VALIDAR LA NO REPETICION DE LOS DIGITOS POR ANO.
    MODIFICATIONS     : Depuracion de la funcion validarDigitos
    PARAMETROS DE ENTRADA:
      UN_COMPANIA:   --Código de la compañía
      UN_ANIO:       --Ano en el cual se van a evaluar los digitos
      UN_DIGITO:     --Número de digitos
    @NAME: existeNivelPlanIndicativoporDigito
  */
  (
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO,
      UN_DIGITO   IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_RT             PCK_SUBTIPOS.TI_LOGICO;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
       BEGIN
            SELECT COUNT(DIGITOS) 
              INTO MI_CONTEO
              FROM BP_NIVEL_PLAN_IND
             WHERE COMPANIA  IN(UN_COMPANIA)
               AND VIGENCIA  IN(UN_ANIO)
               AND DIGITOS   IN(UN_DIGITO);
          IF MI_CONTEO=0 THEN
             RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END IF;
          EXCEPTION
               WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                    MI_REEMPLAZOS(1).CLAVE := 'DIGITO';
                    MI_REEMPLAZOS(1).VALOR := UN_DIGITO;
                    MI_REEMPLAZOS(2).CLAVE := 'ANO';
                    MI_REEMPLAZOS(2).VALOR := UN_ANIO;
                    MI_REEMPLAZOS(3).CLAVE := 'COMPANIA';
                    MI_REEMPLAZOS(3).VALOR := UN_COMPANIA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD => SQLCODE,
                          UN_TABLAERROR=> 'COMPANIA',
                          UN_ERROR_COD => PCK_ERRORES.ER_BANCO_VALDIGITO_NO_DATA,
                          UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
       END;
       RETURN -1;
       EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ER_BANCO_VALDIGITO
                 );
END FC_VALIDARDIGITOS;

FUNCTION FC_BTNCOPIARACTIVIDADES
  /*
     NAME              : BTNCOPIARACTIVIDADES
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : Diego Maldonado
     DATE MIGRADOR     : 14/10/2015
     TIME              : 14:50 PM
     SOURCE MODULE     : BancoProyectos
     MODIFIER          : JUAN CAMILO
     DATE MODIFIED     : 30/01/2017
     TIME              : 16:40
     DESCRIPTION       : Proceso para copiar actividades llamado desde el formulario ComponentesActividades.
     MODIFICATIONS     : Depuracion de la función y adicion de excepción
     PARAMETROS DE ENTRADA:
     	 UN_COMPANIA         --Código de la compañía,
     	 UN_PROYECTO         --Código del proyecto
     	 UN_TIPOCOMPONENTE   --Tipo de componente
     	 UN_COMPONENTE       --Componente
     	 UN_NOMBRECOMPONENTE --Nombre del componente
       UN_VIGENCIA         --Vigencia gubernamental
    @NAME: copiarActividadesBancoProyecto
    */
    (
        UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_PROYECTO               IN COMPONENTES.CODIGOPROYECTO%TYPE,
        UN_TIPOCOMPONENTE         IN COMPONENTES.TIPOCOMPONENTE%TYPE,
        UN_COMPONENTE             IN COMPONENTES.CODIGO%TYPE,
        UN_NOMBRECOMPONENTE       IN COMPONENTES.NOMBRECOMPONENTE%TYPE,
        UN_VIGENCIA               IN PCK_SUBTIPOS.TI_ANIO,
        UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO
    )
       RETURN NUMBER
    AS
        MI_COMPANIA               PCK_SUBTIPOS.TI_COMPANIA;
        MI_CODIGOPROYECTO         VARCHAR2(30  CHAR);
        MI_CODIGOCOMPONENTE       VARCHAR2(30  CHAR);
        MI_TIPOCOMPONENTE         VARCHAR2(30  CHAR);
        MI_NOMBRECOMPONENTE       VARCHAR2(300 CHAR);
        MI_PVIGENCIA              VARCHAR2(50  CHAR);
        MI_ACTIVIDAD              VARCHAR2(30  CHAR);
        MI_PDESCRIPCION           VARCHAR2(300 CHAR);
        MI_NOMBREACTIVIDAD        BP_ACTIVIDADES.NOMBRE%TYPE;
        MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS:= 'COMPANIA,
                                                            CODIGOPROYECTO,
                                                            COMPONENTE,
                                                            TIPOCOMPONENTE,
                                                            ACTIVIDAD,
                                                            NOMBREACTIVIDAD,
                                                            DESCRIPCION,
                                                            VIGENCIA,
                                                            DATE_CREATED,
                                                            CREATED_BY';
        MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
        MI_ACTIVIDADESINSERTADAS  PCK_SUBTIPOS.TI_ENTERO:=0;
        MI_REEMPLAZOS   	    	  PCK_SUBTIPOS.TI_CLAVEVALOR;

        CURSOR rs IS SELECT DISTINCT
                COMPONENTES.COMPANIA,
                COMPONENTES.CODIGOPROYECTO,
                MIN(COMPONENTES.CODIGO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS CODIGOCOMPONENTE,
                COMPONENTES.TIPOCOMPONENTE,
                COMPONENTES.NOMBRECOMPONENTE,
                MIN(COMPONENTES.VIGENCIA) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS PVIGENCIA,
                COMPONENTES_ACTIVIDADES.ACTIVIDAD,
                MIN(COMPONENTES_ACTIVIDADES.DESCRIPCION) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS PDESCRIPCION,
                BP_ACTIVIDADES.NOMBRE AS NOMBREACTIVIDAD
            FROM(COMPONENTES INNER JOIN COMPONENTES_ACTIVIDADES ON
                  (COMPONENTES.COMPANIA = COMPONENTES_ACTIVIDADES.COMPANIA)
                  AND (COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO)
                  AND (COMPONENTES.CODIGO = COMPONENTES_ACTIVIDADES.COMPONENTE)
                  AND (COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE)
                ) INNER JOIN BP_ACTIVIDADES ON
                  (COMPONENTES_ACTIVIDADES.COMPANIA = BP_ACTIVIDADES.COMPANIA)
                  AND (COMPONENTES_ACTIVIDADES.ACTIVIDAD = BP_ACTIVIDADES.CODIGO)
            GROUP BY COMPONENTES.COMPANIA, COMPONENTES.CODIGOPROYECTO, COMPONENTES.TIPOCOMPONENTE, COMPONENTES.NOMBRECOMPONENTE, COMPONENTES_ACTIVIDADES.ACTIVIDAD, BP_ACTIVIDADES.NOMBRE
                HAVING COMPONENTES.COMPANIA= UN_COMPANIA
                  AND COMPONENTES.CODIGOPROYECTO =   UN_PROYECTO
                  AND COMPONENTES.TIPOCOMPONENTE =   UN_TIPOCOMPONENTE
                  AND COMPONENTES.NOMBRECOMPONENTE = UN_NOMBRECOMPONENTE
            ORDER BY COMPONENTES_ACTIVIDADES.ACTIVIDAD;
    BEGIN
        OPEN rs;
            LOOP
                FETCH rs INTO MI_COMPANIA,
                MI_CODIGOPROYECTO,
                MI_CODIGOCOMPONENTE,
                MI_TIPOCOMPONENTE,
                MI_NOMBRECOMPONENTE,
                MI_PVIGENCIA,
                MI_ACTIVIDAD,
                MI_PDESCRIPCION,
                MI_NOMBREACTIVIDAD;
                EXIT WHEN rs%NOTFOUND;
                BEGIN
                    MI_VALORES := ''''||UN_COMPANIA||''',
                                  '''||UN_PROYECTO||''',
                                  '''||UN_COMPONENTE||''',
                                  '''||UN_TIPOCOMPONENTE||''',
                                  '''||MI_ACTIVIDAD||''',
                                  '''||MI_NOMBREACTIVIDAD||''',
                                  '''||MI_PDESCRIPCION||''',
                                  '''||UN_VIGENCIA||''' ,
                                  SYSDATE,
                                  '''||UN_USUARIO||'''';
                    BEGIN
                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'COMPONENTES_ACTIVIDADES',
                                                        UN_ACCION      => 'I',
                                                        UN_CAMPOS      => MI_CAMPOS,
                                                        UN_VALORES     => MI_VALORES
                                                        );

                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                    END;
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                               MI_REEMPLAZOS (1).CLAVE := 'INSERCION';
                               MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE,
                                            UN_TABLAERROR  => 'COMPONENTES_ACTIVIDADES',
                                            UN_ERROR_COD   => PCK_ERRORES.ER_BANCO_INSERT_COPIARACTIVI,
                                            UN_REEMPLAZOS  => MI_REEMPLAZOS
                               );
                END;

                IF PCK_DATOS.GL_RTA <> 0 THEN
                   MI_ACTIVIDADESINSERTADAS := MI_ACTIVIDADESINSERTADAS+1;
                END IF;

            END LOOP;
        CLOSE rs;

        RETURN MI_ACTIVIDADESINSERTADAS;

       EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ER_BANCO_OTHER_COPIARACTIVIDAD
                 );
END FC_BTNCOPIARACTIVIDADES;

FUNCTION FC_PROGRACTIAFTERDELETE
/*
    NAME              : Form_AfterDelConfirm (Access en el formulario de ProgramaciÃ³n de Actividades (Programacion))
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Diego Maldonado
    DATE MIGRADOR     : 26/10/2015
    TIME              : 08:47 AM
    SOURCE MODULE     : BancoProyectos
    MODIFIER          : JUAN CAMILO
    DATE MODIFIED     : 31/01/2017
    TIME              : 10:00
    DESCRIPTION       : Proceso para cambiar el estado de registro del componente y del proyecto despues de eliminar todas las actividades programadas.
    MODIFICATIONS     : Depuracion de la funcion FC_PROGRACTIAFTERDELETE
    PARAMETROS DE ENTRADA:
      UN_COMPANIA          --Código de la compañia
      UN_PROYECTO          --Código de Proyecto
      UN_TIPOCOMPONENTE    --Tipo de componente
      UN_CODIGOCOMPONENTE  --Código de componente
      UN_CODIGOACTIVIDAD   --Código de actividad
      UN_VIGENCIA          --Numero de vigencia gubernamental
  @NAME: eliminarProgramacionActividad
  */
  (
      UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_PROYECTO              IN PROGRAMACION.CODIGOPROYECTO%TYPE,
      UN_TIPOCOMPONENTE        IN PROGRAMACION.TIPOCOMPONENTE%TYPE,
      UN_CODIGOCOMPONENTE      IN PROGRAMACION.CODIGOCOMPONENTE%TYPE,
      UN_CODIGOACTIVIDAD       IN PROGRAMACION.CODIGOACTIVIDAD%TYPE,
      UN_VIGENCIA              IN PCK_SUBTIPOS.TI_ANIO,
      UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER
  AS
        MI_STRSQL              PCK_SUBTIPOS.TI_STRSQL;
        MI_CONTEO              PCK_SUBTIPOS.TI_ENTERO:= 0;
        MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
        MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
        MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
        MI_PARAMETROS          PCK_SUBTIPOS.TI_CAMPOS;
        MI_REEMPLAZOS          PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    SELECT COUNT(COMPANIA) 
    INTO MI_CONTEO
    FROM PROGRAMACION 
    WHERE PROGRAMACION.COMPANIA         = UN_COMPANIA
      AND PROGRAMACION.CODIGOPROYECTO   = UN_PROYECTO
      AND PROGRAMACION.CODIGOCOMPONENTE = UN_CODIGOCOMPONENTE
      AND PROGRAMACION.TIPOCOMPONENTE   = UN_TIPOCOMPONENTE
      AND PROGRAMACION.CODIGOACTIVIDAD  = UN_CODIGOACTIVIDAD
     AND PROGRAMACION.VIGENCIA          = UN_VIGENCIA;

  IF MI_CONTEO=0 THEN
        MI_TABLA     := ' PROYECTOS ';
        MI_CAMPOS    := ' CONPROGRAMACION = 0,
                          MODIFIED_BY     =''' || UN_USUARIO|| ''',
                          DATE_MODIFIED   = SYSDATE';
        MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||''''||
                        ' AND CODIGO = '''||UN_PROYECTO||'''';
        MI_PARAMETROS:=UN_COMPANIA||' '||UN_PROYECTO;

        BEGIN
            BEGIN
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA        =>  MI_TABLA,
                                                UN_ACCION       =>  'M',
                                                UN_CAMPOS       =>  MI_CAMPOS,
                                                UN_CONDICION    =>  MI_CONDICION);
            EXCEPTION
                 WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

            EXCEPTION
                 WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       MI_REEMPLAZOS(1).CLAVE:='PARAMETROS';
                       MI_REEMPLAZOS(1).VALOR:=MI_PARAMETROS;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD => SQLCODE,
                                 UN_TABLAERROR => 'PROYECTOS',
                                 UN_ERROR_COD => PCK_ERRORES.ER_BANCO_UPDATE_PROYECOMPONENT,
                                 UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
        END;
        BEGIN
        MI_PARAMETROS:= '';
        MI_TABLA     := ' COMPONENTES ';
        MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||''''||
                        ' AND CODIGOPROYECTO = '''||UN_PROYECTO||'''' ||
                        ' AND CODIGO = '''||UN_CODIGOCOMPONENTE||'''' ||
                        ' AND TIPOCOMPONENTE = '''||UN_TIPOCOMPONENTE||'''';
        MI_PARAMETROS:=UN_COMPANIA||' '||UN_PROYECTO ||' '||UN_CODIGOCOMPONENTE||' '||UN_TIPOCOMPONENTE;

            BEGIN
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_TABLA      => MI_TABLA,
                                                     UN_ACCION        => 'M',
                                                     UN_CAMPOS        => MI_CAMPOS,
                                                     UN_CONDICION     => MI_CONDICION);
            EXCEPTION
                 WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
           EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     MI_REEMPLAZOS(1).CLAVE:='PARAMETROS';
                     MI_REEMPLAZOS(1).VALOR:=MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD => SQLCODE,
                         UN_TABLAERROR => 'COMPONENTES',
                         UN_ERROR_COD => PCK_ERRORES.ER_BANCO_UPDATE_PROYECOMPONENT,
                         UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
       END;
  END IF;
  RETURN PCK_DATOS.GL_RTA;
   EXCEPTION
          	WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ER_BANCO_OTHER_PROGRAFTERDEL
                    );
END FC_PROGRACTIAFTERDELETE;

  FUNCTION FC_CALCULARPORCENTAJE
  /*
    NAME              : FC_CALCULARPORCENTAJE -> CalcularPorcentaje (Access en el formulario de Programacion de Actividades (Programacion))
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : Diego Maldonado
    DATE MIGRADOR     : 27/10/2015
    TIME              : 08:05 AM
    DESCRIPTION       : Proceso que permite calcular el porcentaje respecto al valor de la progrmacion de una actividad.

    MODIFIED BY       : (25/10/2017) PABLO ANDRES ESPITIA CUCA
    MODIFICATIONS     : (25/10/2017) Indentacion de PLSQL.
                                     Manejo de excepciones.
                                     Adicion de campos de auditoria.

    @NAME  : calcularPorcentaje
    @METHOD: GET
  */
  (
    UN_COMPANIA                    IN PCK_SUBTIPOS.TI_COMPANIA,                 --Codigo de la compania.
    UN_TIPOCOMPONENTE              IN PROGRAMACION.TIPOCOMPONENTE%TYPE,         --Tipo de componente programacion financiera.
    UN_CODIGOCOMPONENTE            IN PROGRAMACION.CODIGOCOMPONENTE%TYPE,       --Codigo componente programacion financiera.
    UN_PROYECTO                    IN PROYECTOS.CODIGO%TYPE,                    --Codigo del proyecto programacion financiera.
    UN_VIGENCIA                    IN PCK_SUBTIPOS.TI_ANIO,                     --Vigencia de la programacion financiera.
    UN_VALORAPROBADO               IN BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE,  --Valor aprobado de programacion financiera.
    UN_VALORPROGRAMADO             IN BP_D_NOVEDADPROYECTO.VALORPROGRAMADO%TYPE,--Valor programado de programacion financiera.
    UN_TIPOESTADO                  IN VARCHAR2,                                 --Codigo tipo de estado de programacion financiera.
    UN_VALORTOTAL                  IN PCK_SUBTIPOS.TI_DOBLE,                    --Valor total programacion financiera.
    UN_VALOR                       IN PROGRAMACION.VALOR1%TYPE,                 --Nuevo valor asignado en el campo valor.
    UN_VALOR_ANT                   IN PROGRAMACION.VALOR1%TYPE,                 --Valor del campo valor antes de ser modificado.
    UN_PERIODOPROYECTO             IN NUMBER DEFAULT 0,                         --Periodo de programacion financiera.
    UN_TIPOT_QUEAPRUEBA            IN PROGRAMACION.TIPOT_QUEAPRUEBA%TYPE,
    UN_TIPOT_QUEAPRUEBA_PROG       IN PROGRAMACION.TIPOT_QUEAPRUEBA%TYPE,
    UN_CLASET_QUEAPRUEBA           IN PROGRAMACION.CLASET_QUEAPRUEBA%TYPE,
    UN_CLASET_QUEAPRUEBA_PROG      IN PROGRAMACION.CLASET_QUEAPRUEBA%TYPE,
    UN_CODIGO_QUEAPRUEBA_PROG      IN PROGRAMACION.CODIGO_QUEAPRUEBA%TYPE,
    UN_CODIGO_QUEAPRUEBA           IN PROGRAMACION.CODIGO_QUEAPRUEBA%TYPE,
    UN_CODIGOITEM_QUEAPRUEBA_PROG  IN PROGRAMACION.CODIGOITEM_QUEAPRUEBA%TYPE,
    UN_CODIGOITEM_QUEAPRUEBA       IN PROGRAMACION.CODIGOITEM_QUEAPRUEBA%TYPE,
    UN_DEPENDENCIA_QUEAPRUEBA_PROG IN PROGRAMACION.DEPENDENCIA_QUEAPRUEBA%TYPE,
    UN_DEPENDENCIA_QUEAPRUEBA      IN PROGRAMACION.DEPENDENCIA_QUEAPRUEBA%TYPE,
    UN_CODIGOACTIVIDAD             IN PROGRAMACION.CODIGOACTIVIDAD%TYPE,        --Codigo de la actividad.
    UN_NOMACTIVIDAD                IN BP_ACTIVIDADES.NOMBRE%TYPE,               --Nombre de la actividad.
    UN_CODIGO_PROG                 IN PROGRAMACION.CODIGO%TYPE,                 --Codigo programacion de actividades.
    UN_CANTIDAD_PROG               IN PROGRAMACION.CANTIDAD%TYPE,               --Cantidad programacion de actividades.
    UN_VALORTOTAL_PROG             IN PROGRAMACION.VALORTOTAL%TYPE,             --Valor total programacion de actividades.
    UN_USUARIO                     IN PCK_SUBTIPOS.TI_USUARIO                   --Codigo del usuario que desencadena el proceso.
  )
  RETURN PCK_SUBTIPOS.TI_PORCENTAJE
  AS 
    MI_PAR_MANEJA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_FACTOR_MILES    NUMBER(4,0);
    MI_VALOR           PROGRAMACION.VALOR1%TYPE; --En access: dblvalor
    MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CANT_PROG       PCK_SUBTIPOS.TI_DOBLE; --En access: can_pro
    MI_VALOR_MAX       PCK_SUBTIPOS.TI_DOBLE; --En access: ValorMax
    MI_AUX_SALDO       PCK_SUBTIPOS.TI_DOBLE; --En access: dblSaldo
    MI_ERROR_COD       PLS_INTEGER;
    MI_VALORAPROBADO   BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE; --En access: valorAprobadosolicitud
    MI_VALORPROGRAMADO BP_D_NOVEDADPROYECTO.VALORPROGRAMADO%TYPE;
    MI_RTA_ACME        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_BDN       PCK_SUBTIPOS.TI_TABLA DEFAULT 'BP_D_NOVEDADPROYECTO';
    MI_TABLA_C         PCK_SUBTIPOS.TI_TABLA DEFAULT 'COMPONENTES';  
    MI_TABLA_CA        PCK_SUBTIPOS.TI_TABLA DEFAULT 'COMPONENTES_ACTIVIDADES';
    MI_TABLA_P         PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROYECTOS';
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS_AUX      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_PORCENTAJE      PCK_SUBTIPOS.TI_PORCENTAJE := 0;
  BEGIN
    --Comparando cuando es ejecutada
    IF UN_TIPOESTADO IN('E') THEN
      IF UN_TIPOT_QUEAPRUEBA_PROG       NOT IN(UN_TIPOT_QUEAPRUEBA      ) OR
         UN_CLASET_QUEAPRUEBA_PROG      NOT IN(UN_CLASET_QUEAPRUEBA     ) OR
         UN_CODIGO_QUEAPRUEBA_PROG      NOT IN(UN_CODIGO_QUEAPRUEBA     ) OR
         UN_CODIGOITEM_QUEAPRUEBA_PROG  NOT IN(UN_CODIGOITEM_QUEAPRUEBA ) OR
         UN_DEPENDENCIA_QUEAPRUEBA_PROG NOT IN(UN_DEPENDENCIA_QUEAPRUEBA)
      THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          MI_REEMPLAZOS(1).CLAVE := 'ACTIVIDAD';
          MI_REEMPLAZOS(1).VALOR := UN_CODIGOACTIVIDAD;
          MI_REEMPLAZOS(2).CLAVE := 'NOMACTIVIDAD';
          MI_REEMPLAZOS(2).VALOR := UN_NOMACTIVIDAD;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_MSG_PR_CALPOR_QUEAPRUE
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
      END IF;
    END IF;

    MI_PAR_MANEJA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                              ,UN_NOMBRE   => 'MANEJA VALORES DE PROYECTOS EN MILES'
                                              ,UN_MODULO   => PCK_DATOS.FC_MODULOBANCOPROY
                                              ,UN_FECHA_PAR => SYSDATE)
                        ,'NO');

    MI_FACTOR_MILES := CASE WHEN MI_PAR_MANEJA IN('SI')
                            THEN 1000
                            ELSE 1
                       END;



    --Revisando saldo                      
    IF UN_VALOR IS NOT NULL THEN    
      --Comparar el nuevo valor con el anterior.
      MI_VALOR := CASE WHEN UN_VALOR_ANT > 0
                       THEN UN_VALOR - UN_VALOR_ANT
                       ELSE UN_VALOR
                  END;

      FOR RS IN (SELECT 
                   PROYECTOS.VALOREJECUTADO    PVALOREJECUTADO
                  ,PROYECTOS.VALORPROGRAMADO   PVALORPROGRAMADO
                  ,COMPONENTES.VALOREJECUTADO  CVALOREJECUTADO
                  ,COMPONENTES.VALORPROGRAMADO CVALORPROGRAMADO
                  ,PROYECTOS.VALORTOTAL        PVALORTOTAL
                  ,COMPONENTES.VALORTOTAL      CVALORTOTAL
                  ,PROYECTOS.VALORANNO1
                  ,PROYECTOS.VALORANNO2
                  ,PROYECTOS.VALORANNO3
                  ,PROYECTOS.VALORANNO4
                  ,PROYECTOS.CONPROGRAMACION   CONPROGRAMACION_P
                  ,COMPONENTES.CONPROGRAMACION CONPROGRAMACION_C
                  ,COMPONENTES.SALDOCOMPONENTE CSALDO
                  ,CA.VALORPROGRAMADO          AVALORPROGRAMADO
                  ,CA.VALOREJECUTADO           AVALOREJECUTADO
                  ,CA.COSTOTOTAL               AVALORTOTAL
                  ,CA.COSTOUNITARIO            AVALORUNI
                  ,CA.CANTIDAD                 ACANTIDAD
                FROM PROYECTOS
                  INNER JOIN COMPONENTES
                     ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                    AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
                  INNER JOIN COMPONENTES_ACTIVIDADES CA
                     ON COMPONENTES.COMPANIA       = CA.COMPANIA
                    AND COMPONENTES.CODIGOPROYECTO = CA.CODIGOPROYECTO
                    AND COMPONENTES.CODIGO         = CA.COMPONENTE
                    AND COMPONENTES.TIPOCOMPONENTE = CA.TIPOCOMPONENTE
                  WHERE PROYECTOS.COMPANIA         = UN_COMPANIA
                    AND PROYECTOS.CODIGO           = UN_PROYECTO
                    AND COMPONENTES.CODIGO         = UN_CODIGOCOMPONENTE
                    AND COMPONENTES.TIPOCOMPONENTE = UN_TIPOCOMPONENTE
                    AND CA.ACTIVIDAD               = UN_CODIGOACTIVIDAD
                    AND CA.VIGENCIA                = UN_VIGENCIA)
      LOOP
        IF UN_TIPOESTADO NOT IN('E') THEN
          BEGIN
            SELECT SUM(CANTIDAD)
            INTO MI_CANT_PROG
            FROM PROGRAMACION 
            WHERE PROGRAMACION.COMPANIA         = UN_COMPANIA
              AND PROGRAMACION.CODIGOPROYECTO   = UN_PROYECTO
              AND PROGRAMACION.CODIGOCOMPONENTE = UN_CODIGOCOMPONENTE
              AND PROGRAMACION.TIPOCOMPONENTE   = UN_TIPOCOMPONENTE 
              AND PROGRAMACION.CODIGOACTIVIDAD  = UN_CODIGOACTIVIDAD
              AND PROGRAMACION.VIGENCIA         = UN_VIGENCIA
              AND (   TIPOESTADO NOT IN (UN_TIPOESTADO)
                   OR (    TIPOESTADO = UN_TIPOESTADO 
                       AND CODIGO     = UN_CODIGO_PROG));        

          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CANT_PROG := 0;
          END;

          MI_VALOR_MAX := UN_CANTIDAD_PROG * RS.AVALORUNI;

          BEGIN
            IF (MI_VALOR + UN_VALORTOTAL_PROG) <= MI_VALOR_MAX THEN
              MI_AUX_SALDO := MI_VALOR_MAX - (RS.AVALORPROGRAMADO - (MI_CANT_PROG * RS.AVALORUNI));

              --Verifica que el nuevo saldo a programar sea menor o igual al saldo total por programar.
              IF MI_AUX_SALDO < MI_VALOR THEN
                MI_REEMPLAZOS(1).CLAVE := 'VALORPROG';
                MI_REEMPLAZOS(1).VALOR := MI_AUX_SALDO;       

                MI_ERROR_COD := PCK_ERRORES.ERR_BDP_MSG_PR_CALPOR_SALDOINS;

                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
              END IF;

            ELSE
              MI_REEMPLAZOS(1).CLAVE := 'CANTIDAD';
              MI_REEMPLAZOS(1).VALOR := UN_CANTIDAD_PROG; 
              MI_REEMPLAZOS(2).CLAVE := 'VALORUNITARIO';
              MI_REEMPLAZOS(2).VALOR := RS.AVALORUNI; 
              MI_REEMPLAZOS(3).CLAVE := 'VALOREJECUTADO';
              MI_REEMPLAZOS(3).VALOR := UN_VALORTOTAL_PROG; 
              MI_REEMPLAZOS(4).CLAVE := 'MAXIVALOR';
              MI_REEMPLAZOS(4).VALOR := (MI_VALOR_MAX - UN_VALORTOTAL_PROG);             

              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_MSG_PR_CALPOR_EXCVALOR;

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END IF;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => MI_ERROR_COD
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);
          END;

          --Actualizando saldos - Programacion
          MI_CAMPOS := 'VALORPROGRAMADO =   '||(RS.CVALORPROGRAMADO + MI_VALOR)||'
                       ,SALDOCOMPONENTE =   '||(RS.CSALDO           + MI_VALOR)||'
                       ,CONPROGRAMACION = -1
                       ,DATE_MODIFIED   = SYSDATE
                       ,MODIFIED_BY     = '''||UN_USUARIO                      ||'''';

          MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA        ||'''
                       AND CODIGOPROYECTO = '''||UN_PROYECTO        ||'''
                       AND CODIGO         = '''||UN_CODIGOCOMPONENTE||'''
                       AND TIPOCOMPONENTE = '''||UN_TIPOCOMPONENTE  ||'''';

          BEGIN
            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_C
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VPROGRAMAD;

              MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
              MI_REEMPLAZOS(1).VALOR := UN_CODIGOCOMPONENTE;
              MI_REEMPLAZOS(2).CLAVE := 'TIPO';
              MI_REEMPLAZOS(2).VALOR := UN_TIPOCOMPONENTE;            

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

            MI_CAMPOS_AUX := CASE UN_PERIODOPROYECTO WHEN 1 
                                                     THEN ',VALORANNO1 = '||(RS.VALORANNO1 + MI_VALOR)
                                                     WHEN 2
                                                     THEN ',VALORANNO2 = '||(RS.VALORANNO2 + MI_VALOR)
                                                     WHEN 3
                                                     THEN ',VALORANNO3 = '||(RS.VALORANNO3 + MI_VALOR)
                                                     WHEN 4
                                                     THEN ',VALORANNO4 = '||(RS.VALORANNO4 + MI_VALOR)
                             END;

            MI_CAMPOS := 'VALORPROGRAMADO =   '||(RS.PVALORPROGRAMADO + MI_VALOR)||'
                         ,CONPROGRAMACION = -1'                                  ||
                         MI_CAMPOS_AUX                                           ||'
                         ,DATE_MODIFIED   = SYSDATE
                         ,MODIFIED_BY     = '''||UN_USUARIO                      ||'''';          

            MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                         AND CODIGO   = '''||UN_PROYECTO||'''';          

            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_P --PROYECTOS
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VPROGRPROY;

              MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
              MI_REEMPLAZOS(1).VALOR := UN_PROYECTO;

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;          

            MI_CAMPOS := 'VALORPROGRAMADO =   '||(RS.AVALORPROGRAMADO + MI_VALOR)||'
                         ,DATE_MODIFIED   = SYSDATE
                         ,MODIFIED_BY     = '''||UN_USUARIO                      ||'''';   

            MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA        ||'''
                         AND CODIGOPROYECTO = '''||UN_PROYECTO        ||'''
                         AND COMPONENTE     = '''||UN_CODIGOCOMPONENTE||'''
                         AND TIPOCOMPONENTE = '''||UN_TIPOCOMPONENTE  ||'''
                         AND ACTIVIDAD      = '''||UN_CODIGOACTIVIDAD ||'''';          

            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_CA
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VPROGCOACT;

              MI_REEMPLAZOS(1).CLAVE := 'ACTIVIDAD';
              MI_REEMPLAZOS(1).VALOR := UN_CODIGOACTIVIDAD;
              MI_REEMPLAZOS(2).CLAVE := 'COMPONENTE';
              MI_REEMPLAZOS(2).VALOR := UN_CODIGOCOMPONENTE;
              MI_REEMPLAZOS(3).CLAVE := 'TIPO';
              MI_REEMPLAZOS(3).VALOR := UN_TIPOCOMPONENTE;            

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;           

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => MI_ERROR_COD
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);         
          END;

          --Calculando porcentaje
          IF RS.PVALORTOTAL IN(0) THEN
            MI_PORCENTAJE := 0;
          ELSE
            MI_PORCENTAJE :=  ROUND(UN_VALOR / RS.PVALORTOTAL ,4); 
          END IF;

        ELSE --Asignando programacion
          MI_VALORAPROBADO   := UN_VALORAPROBADO   / MI_FACTOR_MILES;
          MI_VALORPROGRAMADO := UN_VALORPROGRAMADO / MI_FACTOR_MILES;

          BEGIN
            IF MI_VALOR > MI_VALORAPROBADO THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_MSG_PR_CALPOR_VAPROBAD;

              MI_REEMPLAZOS(1).CLAVE := 'VALOR';
              MI_REEMPLAZOS(1).VALOR := MI_VALORAPROBADO;

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END IF;

            IF MI_VALOR > (MI_VALORAPROBADO - MI_VALORPROGRAMADO) THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_MSG_PR_CALPOR_SALDOPRG;

              MI_REEMPLAZOS(1).CLAVE := 'VALOR';
              MI_REEMPLAZOS(1).VALOR := (MI_VALORAPROBADO - MI_VALORPROGRAMADO);

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END IF;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => MI_ERROR_COD
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
          END;

          MI_CAMPOS := 'VALORPROGRAMADO   = VALORPROGRAMADO + ('||MI_VALOR * MI_FACTOR_MILES||')
                       ,CONPROGRAMACION_P = -1
                       ,CONPROGRAMACION_C = -1
                       ,DATE_MODIFIED     = SYSDATE
                       ,MODIFIED_BY       = '''||UN_USUARIO||'''';

          MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA              ||'''
                       AND TIPOT       = '''||UN_TIPOT_QUEAPRUEBA      ||'''
                       AND CLASET      = '''||UN_CLASET_QUEAPRUEBA     ||'''
                       AND NOVEDAD     =   '||UN_CODIGO_QUEAPRUEBA     ||' 
                       AND DEPENDENCIA = '''||UN_DEPENDENCIA_QUEAPRUEBA||'''
                       AND CODIGO      =   '||UN_CODIGOITEM_QUEAPRUEBA ||' 
                       AND PROYECTO    = '''||UN_PROYECTO              ||'''
                       AND COMPONENTE  = '''||UN_CODIGOCOMPONENTE      ||'''';

          BEGIN
            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_BDN
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            MI_REEMPLAZOS(1).CLAVE := 'PROYECTO';
            MI_REEMPLAZOS(1).VALOR := UN_PROYECTO;
            MI_REEMPLAZOS(2).CLAVE := 'COMPONENTE';
            MI_REEMPLAZOS(2).VALOR := UN_CODIGOCOMPONENTE;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VALPROGRAM
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                      ,UN_TABLAERROR => MI_TABLA_BDN);         
          END;

          --Actualizando saldos - Programacion ejecutada
          MI_VALOR := MI_VALOR * MI_FACTOR_MILES;

          MI_CAMPOS := 'VALOREJECUTADO  =   '||(RS.CVALOREJECUTADO + MI_VALOR)||'
                       ,CONPROGRAMACION = -1
                       ,SALDOCOMPONENTE =   '||(RS.CSALDO          - MI_VALOR)||'
                       ,DATE_MODIFIED   = SYSDATE
                       ,MODIFIED_BY     = '''||UN_USUARIO                     ||'''';

          MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA        ||'''
                       AND CODIGOPROYECTO = '''||UN_PROYECTO        ||'''
                       AND CODIGO         = '''||UN_CODIGOCOMPONENTE||'''
                       AND TIPOCOMPONENTE = '''||UN_TIPOCOMPONENTE  ||'''';

          BEGIN
            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_C
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VEJECOMPON;

              MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
              MI_REEMPLAZOS(1).VALOR := UN_CODIGOCOMPONENTE;
              MI_REEMPLAZOS(2).CLAVE := 'TIPO';
              MI_REEMPLAZOS(2).VALOR := UN_TIPOCOMPONENTE;            

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

            MI_CAMPOS := 'VALOREJECUTADO =    '||(RS.PVALOREJECUTADO + MI_VALOR)||'
                         ,CONPROGRAMACION = -1'                                 ||'
                         ,DATE_MODIFIED   = SYSDATE
                         ,MODIFIED_BY     = '''||UN_USUARIO                     ||'''';          

            MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                         AND CODIGO   = '''||UN_PROYECTO||'''';          

            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_P --PROYECTOS
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VEJEPROYEC;

              MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
              MI_REEMPLAZOS(1).VALOR := UN_PROYECTO;

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;          

            MI_CAMPOS := 'VALOREJECUTADO =   '||(RS.AVALOREJECUTADO + MI_VALOR)||'
                         ,DATE_MODIFIED  = SYSDATE
                         ,MODIFIED_BY    = '''||UN_USUARIO                     ||'''';   

            MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA        ||'''
                         AND CODIGOPROYECTO = '''||UN_PROYECTO        ||'''
                         AND COMPONENTE     = '''||UN_CODIGOCOMPONENTE||'''
                         AND TIPOCOMPONENTE = '''||UN_TIPOCOMPONENTE  ||'''
                         AND ACTIVIDAD      = '''||UN_CODIGOACTIVIDAD ||'''';          

            BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                              ,UN_TABLA     => MI_TABLA_CA
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              MI_ERROR_COD := PCK_ERRORES.ERR_BDP_M_PR_CALPOR_VEJECOMACT;

              MI_REEMPLAZOS(1).CLAVE := 'ACTIVIDAD';
              MI_REEMPLAZOS(1).VALOR := UN_CODIGOACTIVIDAD;
              MI_REEMPLAZOS(2).CLAVE := 'COMPONENTE';
              MI_REEMPLAZOS(2).VALOR := UN_CODIGOCOMPONENTE;
              MI_REEMPLAZOS(3).CLAVE := 'TIPO';
              MI_REEMPLAZOS(3).VALOR := UN_TIPOCOMPONENTE;            

              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;           

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => MI_ERROR_COD
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);         
          END;        

          --Calculando porcentaje ejecutado
          MI_PORCENTAJE := CASE WHEN UN_VALORTOTAL IN(0) 
                                THEN 0
                                ELSE ROUND(UN_VALOR / UN_VALORTOTAL,4)
                           END;

        END IF;
      END LOOP;
    END IF;   

    RETURN MI_PORCENTAJE;
  END FC_CALCULARPORCENTAJE;

    FUNCTION FC_CARGAR_RUBROS_PROYECTO
(
  /*
    NAME              : FC_CARGAR_RUBROS_PROYECTO
    AUTHORS           : PAOLA  VEGA
    DATE              : 08/01/2026
    TIME              : 02:00 PM
    DESCRIPTION       : Proceso que permite insertar de manera masiva registros que vienen desde plantilla excel de rubros por proyecto.

    @NAME  : cargarRubrosProyecto
    @METHOD: POST
  */
      UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CADENA     IN CLOB,
      UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_FILAS           PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS           PCK_SYSMAN_UTL.T_SPLIT;
    MI_CAMPOS    CLOB;
    MI_VALORES   CLOB;
    MI_SECTOR        VARCHAR2(50);
    MI_PROGRAMA      VARCHAR2(50);
    MI_SUBPROGRAMA   VARCHAR2(50);
    MI_EXISTE NUMBER;
    MI_VALOR  VARCHAR2(50);
    NUM_FILA   NUMBER := 0;
    HAY_ERROR  BOOLEAN := FALSE;
    CARGA_VALIDA  BOOLEAN := TRUE;
    LOG_RTA    CLOB := '';

BEGIN
    MI_FILAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                    UN_LISTA       => UN_CADENA,
                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                );

    FOR I IN MI_FILAS.FIRST .. MI_FILAS.LAST LOOP
        NUM_FILA := NUM_FILA + 1;
        HAY_ERROR :=FALSE;
        MI_DATOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                        UN_LISTA       => MI_FILAS(I),
                        UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL
                    );

        IF    MI_DATOS(1) = 'NoDato'
           OR MI_DATOS(2) = 'NoDato'
           OR MI_DATOS(3) = 'NoDato'
           OR MI_DATOS(4) = 'NoDato'
           OR MI_DATOS(5) = 'NoDato'
           OR MI_DATOS(6) = 'NoDato'
           OR MI_DATOS(7) = 'NoDato'
        THEN
            LOG_RTA := LOG_RTA ||
                'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                'RUBRO='         || MI_DATOS(3) || ' | ' ||
                'FUENTE='        || MI_DATOS(4) || ' | ' ||
                'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                'OBSERVACIONES= El registro tiene campos vacíos'
                || CHR(10);
        
            HAY_ERROR := TRUE;
            
        ELSE
            
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM PROYECTOS
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_DATOS(1);
    
            IF MI_EXISTE = 0 THEN
                 LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=EL PROYECTO NO EXISTE PARA LA VIGENCIA' || CHR(10);
                HAY_ERROR := TRUE;
            END IF;
    
            
            BEGIN
                SELECT  SECTOR, PROGRAMA, SUBPROGRAMA
                  INTO  MI_SECTOR, MI_PROGRAMA, MI_SUBPROGRAMA
                  FROM PLAN_PRESUPUESTAL
                 WHERE COMPANIA = UN_COMPANIA
                   AND CODIGO   = MI_DATOS(3)
                   AND VIGENCIA = MI_DATOS(2);
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=RUBRO NO EXISTE PARA LA VIGENCIA' || CHR(10);
                    HAY_ERROR := TRUE;
            END;
    
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM FUENTE_RECURSOS
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_DATOS(4)
               AND ANO = MI_DATOS(2);
    
            IF MI_EXISTE = 0 THEN
                 LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=FUENTE NO EXISTE' || CHR(10);
                HAY_ERROR := TRUE;
            END IF;
    
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM CENTRO_COSTO
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_DATOS(5)
               AND ANO = MI_DATOS(2);
    
            IF MI_EXISTE = 0 THEN
                LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=CENTRO DE COSTO NO EXISTE' || CHR(10);
                HAY_ERROR := TRUE;
            END IF;
    
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM REFERENCIA
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_DATOS(6)
               AND ANO = MI_DATOS(2);
    
            IF MI_EXISTE = 0 THEN
                 LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=REFERENCIA NO EXISTE' || CHR(10);
                HAY_ERROR := TRUE;
            END IF;
    
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM AUXILIAR
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO   = MI_DATOS(7)
               AND ANO = MI_DATOS(2);
    
            IF MI_EXISTE = 0 THEN
                 LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=AUXILIAR NO EXISTE' || CHR(10);
                HAY_ERROR := TRUE;
            END IF;
    
    
            SELECT COUNT(1)
              INTO MI_EXISTE
              FROM BP_PROYECTOSRUBROS
             WHERE COMPANIA      = UN_COMPANIA
               AND PROYECTO      = MI_DATOS(1)
               AND VIGENCIA      = MI_DATOS(2)
               AND RUBROPPTALES         = MI_DATOS(3)
               AND FUENTERECURSOSRUBRO   = MI_DATOS(4)
               AND CENTRO_COSTO  = MI_DATOS(5)
               AND REFERENCIA    = MI_DATOS(6)
               AND AUXILIAR      = MI_DATOS(7);
        
    
            IF MI_EXISTE > 0 THEN
               LOG_RTA := LOG_RTA ||
                           'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                           'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                           'RUBRO='         || MI_DATOS(3) || ' | ' ||
                           'FUENTE='        || MI_DATOS(4) || ' | ' ||
                           'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                           'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                           'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                           'OBSERVACIONES=EL REGISTRO YA EXISTE' || CHR(10);
               
               
                HAY_ERROR := TRUE;
            ELSE
                 IF NOT HAY_ERROR THEN
                    MI_CAMPOS :=
                        'COMPANIA
                        ,PROYECTO
                        ,RUBROPRESUPUESTAL
                        ,VIGENCIA
                        ,VALOR
                        ,DIMENSION
                        ,SECTOR
                        ,PROGRAMA
                        ,SUBPROGRAMA
                        ,RUBROPPTALES
                        ,FUENTERECURSOSRUBRO
                        ,CREATED_BY
                        ,DATE_CREATED
                        ,CENTRO_COSTO
                        ,REFERENCIA
                        ,AUXILIAR';
        
                    MI_VALORES :=
                        ''''||UN_COMPANIA||'''
                        ,'''||MI_DATOS(1)||'''
                        ,NULL
                        ,'''||MI_DATOS(2)||'''
                        ,0
                        ,'''||MI_DATOS(3)||'''
                        ,'''||MI_DATOS(3)||'''
                        ,'''||MI_DATOS(3)||'''
                        ,'''||MI_DATOS(3)||'''
                        ,'''||MI_DATOS(3)||'''
                        ,'''||MI_DATOS(4)||'''
                        ,'''||UN_USUARIO||'''
                        ,SYSDATE
                        ,'''||MI_DATOS(5)||'''
                        ,'''||MI_DATOS(6)||'''
                        ,'''||MI_DATOS(7)||'''';
        
                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA    => 'BP_PROYECTOSRUBROS',
                                              UN_ACCION   => 'I',
                                              UN_CAMPOS   => MI_CAMPOS,
                                              UN_VALORES  => MI_VALORES
                                          );
                                   
                        LOG_RTA := LOG_RTA ||
                                   'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                                   'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                                   'RUBRO='         || MI_DATOS(3) || ' | ' ||
                                   'FUENTE='        || MI_DATOS(4) || ' | ' ||
                                   'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                                   'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                                   'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                                   'OBSERVACIONES=CARGADO' || CHR(10);
        
                   EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    
                           LOG_RTA := LOG_RTA ||
                                   'PROYECTO='      || MI_DATOS(1) || ' | ' ||
                                   'VIGENCIA='      || MI_DATOS(2) || ' | ' ||
                                   'RUBRO='         || MI_DATOS(3) || ' | ' ||
                                   'FUENTE='        || MI_DATOS(4) || ' | ' ||
                                   'CENTRO_COSTO='  || MI_DATOS(5) || ' | ' ||
                                   'REFERENCIA='    || MI_DATOS(6) || ' | ' ||
                                   'AUXILIAR='      || MI_DATOS(7) || ' | ' ||
                                   'OBSERVACIONES=' || SQLERRM || CHR(10);
                    
                            HAY_ERROR := TRUE;
                    
                            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                            END;
                        END IF;
                    END IF;  
            IF HAY_ERROR THEN
                CARGA_VALIDA := FALSE;
            END IF;
                END IF;
    END LOOP;


    IF NOT CARGA_VALIDA THEN
        ROLLBACK;
        RETURN LOG_RTA || CHR(10) || 'PROCESO CANCELADO. SE PRESENTARON INCONSISTENCIAS.';
    ELSE
        COMMIT;
        RETURN LOG_RTA || CHR(10) || 'PROCESO EJECUTADO, REGISTROS INSERTADOS CORRECTAMENTE.';
    END IF;

END FC_CARGAR_RUBROS_PROYECTO;

FUNCTION FC_ACTUALIZAR_ACTIVXPROYECTO
(
  /*
    NAME              : FC_ACTUALIZAR_ACTIVXPROYECTO
    AUTHORS           : PAOLA  VEGA
    DATE              : 08/01/2026
    TIME              : 02:00 PM
    DESCRIPTION       : Proceso que permite actualizar de manera masiva registros que vienen desde plantilla excel hacia componentes actividades.

    @NAME  : actualizarActivXProyecto
    @METHOD: UPDATE
  */
      UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CADENA     IN CLOB,
      UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO,
      UN_VIGENCIA   IN NUMBER
)
RETURN CLOB
AS
    MI_FILAS               PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS               PCK_SYSMAN_UTL.T_SPLIT;
    MI_PROYECTO            COMPONENTES_ACTIVIDADES.CODIGOPROYECTO%TYPE;
    MI_COMPONENTE          COMPONENTES_ACTIVIDADES.COMPONENTE%TYPE;
    MI_ACTIVIDAD           COMPONENTES_ACTIVIDADES.ACTIVIDAD%TYPE;
    MI_CANTIDAD_ARCHIVO       NUMBER;
    MI_COSTO_ARCHIVO          NUMBER;
    MI_CANTIDAD_BD         NUMBER;
    MI_COSTO_BD            NUMBER;
    MI_COSTOTOTAL         NUMBER;
    MI_EXISTE              NUMBER;
    MI_CONT_ACTUALIZADAS   NUMBER := 0;
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    
    MI_LOG_ACTUALIZADAS    CLOB := 'ACTIVIDADES ACTUALIZADAS' || CHR(10) ||
                                   '--------------------------------------------------' || CHR(10);
                                   
    MI_LOG_NO_ACTUALIZADAS CLOB := 'ACTIVIDADES NO ACTUALIZADAS' || CHR(10) ||
                                   '--------------------------------------------------' || CHR(10);

BEGIN
        
    MI_FILAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                    UN_LISTA       => UN_CADENA,
                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                );

    FOR I IN MI_FILAS.FIRST .. MI_FILAS.LAST LOOP

        MI_DATOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                        UN_LISTA       => MI_FILAS(I),
                        UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL
                    );

        -- VALIDACION DE CAMPOS OBLIGATORIOS
        IF MI_DATOS(2) = 'NoDato'
        OR MI_DATOS(3) = 'NoDato'
        OR MI_DATOS(4) = 'NoDato'
        OR MI_DATOS(5) = 'NoDato'
        OR MI_DATOS(6) = 'NoDato'
        THEN
            MI_LOG_NO_ACTUALIZADAS := MI_LOG_NO_ACTUALIZADAS ||
                'VIGENCIA=' || UN_VIGENCIA || ' | ' ||
                'PROYECTO=' || MI_DATOS(2) || ' | ' ||
                'COMPONENTE=' || MI_DATOS(3) || ' | ' ||
                'ACTIVIDAD=' || MI_DATOS(4) || ' | ' ||
                'COSTOUNITARIO=' || MI_DATOS(6) || ' | ' ||
                'OBSERVACION=Datos obligatorios incompletos' || CHR(10);
            CONTINUE;
        END IF;

        MI_PROYECTO   := MI_DATOS(2);
        MI_COMPONENTE := MI_DATOS(3);
        MI_ACTIVIDAD  := MI_DATOS(4);

        MI_CANTIDAD_ARCHIVO := TO_NUMBER(MI_DATOS(5));
        MI_COSTO_ARCHIVO    := TO_NUMBER(MI_DATOS(6));

        -- VALIDACIONES NUMERICAS
        IF MI_CANTIDAD_ARCHIVO < 0 OR MI_COSTO_ARCHIVO < 0 THEN
            MI_LOG_NO_ACTUALIZADAS := MI_LOG_NO_ACTUALIZADAS ||
                'VIGENCIA=' || UN_VIGENCIA || ' | ' ||
                'PROYECTO=' || MI_PROYECTO || ' | ' ||
                'COMPONENTE=' || MI_COMPONENTE || ' | ' ||
                'ACTIVIDAD=' || MI_ACTIVIDAD || ' | ' ||
                'COSTOUNITARIO=' || MI_COSTO_ARCHIVO || ' | ' ||
                'OBSERVACION=Valores negativos no permitidos' || CHR(10);
            CONTINUE;
        END IF;

        -- VERIFICAR EXISTENCIA
        SELECT COUNT(*)
          INTO MI_EXISTE
          FROM COMPONENTES_ACTIVIDADES
         WHERE COMPANIA        = UN_COMPANIA
           AND VIGENCIA        = UN_VIGENCIA
           AND CODIGOPROYECTO = MI_PROYECTO
           AND COMPONENTE      = MI_COMPONENTE
           AND ACTIVIDAD       = MI_ACTIVIDAD;

        IF MI_EXISTE = 0 THEN
            MI_LOG_NO_ACTUALIZADAS := MI_LOG_NO_ACTUALIZADAS ||
                'VIGENCIA=' || UN_VIGENCIA || ' | ' ||
                'PROYECTO=' || MI_PROYECTO || ' | ' ||
                'COMPONENTE=' || MI_COMPONENTE || ' | ' ||
                'ACTIVIDAD=' || MI_ACTIVIDAD || ' | ' ||
                'COSTOUNITARIO=' || MI_COSTO_ARCHIVO || ' | ' ||
                'OBSERVACION=Actividad no existe para la vigencia' || CHR(10);
            CONTINUE;
        END IF;

        -- OBTENER VALORES ACTUALES
        SELECT CANTIDAD, COSTOUNITARIO
          INTO MI_CANTIDAD_BD, MI_COSTO_BD
          FROM COMPONENTES_ACTIVIDADES
         WHERE COMPANIA        = UN_COMPANIA
           AND VIGENCIA        = UN_VIGENCIA
           AND CODIGOPROYECTO = MI_PROYECTO
           AND COMPONENTE      = MI_COMPONENTE
           AND ACTIVIDAD       = MI_ACTIVIDAD;

        -- COMPARACION
        IF MI_CANTIDAD_BD = MI_CANTIDAD_ARCHIVO
        AND MI_COSTO_BD   = MI_COSTO_ARCHIVO
        THEN
           MI_LOG_NO_ACTUALIZADAS :=  MI_LOG_NO_ACTUALIZADAS ||
              'VIGENCIA=' || TO_CHAR(UN_VIGENCIA) || ' | ' ||
              'PROYECTO=' || TO_CHAR(MI_PROYECTO) || ' | ' ||
              'COMPONENTE=' || NVL(TO_CHAR(MI_COMPONENTE), '0') || ' | ' ||
              'ACTIVIDAD=' || NVL(TO_CHAR(MI_ACTIVIDAD), '0') || ' | ' ||
              'COSTOUNITARIO=' ||
                  NVL(TO_CHAR(MI_COSTO_ARCHIVO, 'FM9999999999999999990D00'), '0') || ' | ' ||
              'OBSERVACION=Valores iguales, no se actualiza' || CHR(10);
          CONTINUE;
        END IF;

        -- ACTUALIZACION
        MI_COSTOTOTAL := MI_CANTIDAD_ARCHIVO * MI_COSTO_ARCHIVO;

        BEGIN
            BEGIN
                MI_TABLA := ' COMPONENTES_ACTIVIDADES ';
                MI_CAMPOS :=
                      ' CANTIDAD = ' || MI_CANTIDAD_ARCHIVO || ', '
                   || ' COSTOUNITARIO = ' || MI_COSTO_ARCHIVO || ', '
                   || ' COSTOTOTAL = ' || MI_COSTOTOTAL || ', '
                   || ' MODIFIED_BY = ''' || UN_USUARIO || ''', '
                   || ' DATE_MODIFIED = SYSDATE ';
        
                MI_CONDICION :=
                      ' COMPANIA = ''' || UN_COMPANIA || ''''
                   || ' AND VIGENCIA = ' || UN_VIGENCIA
                   || ' AND CODIGOPROYECTO = ''' || MI_PROYECTO || ''''
                   || ' AND COMPONENTE = ''' || MI_COMPONENTE || ''''
                   || ' AND ACTIVIDAD = ''' || MI_ACTIVIDAD || '''';
        
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION
                                    );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD     => SQLCODE
                                ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPSACTV
                                );
        END;

        MI_CONT_ACTUALIZADAS := MI_CONT_ACTUALIZADAS + 1;

        MI_LOG_ACTUALIZADAS := MI_LOG_ACTUALIZADAS ||
            'VIGENCIA=' || UN_VIGENCIA || ' | ' ||
            'PROYECTO=' || MI_PROYECTO || ' | ' ||
            'COMPONENTE=' || MI_COMPONENTE || ' | ' ||
            'ACTIVIDAD=' || MI_ACTIVIDAD || ' | ' ||
            'COSTOUNITARIO=' || MI_COSTO_ARCHIVO || ' | ' ||
            'OBSERVACION=Valor Actividad y Programación Actualizado' || CHR(10);

    END LOOP;

    IF MI_CONT_ACTUALIZADAS > 0 THEN
        COMMIT;
    END IF;

    PCK_DATOS.GL_RTA := PCK_BANCOS_PROY5.FC_CREAR_MANTENIMIENTO (
                    UN_COMPANIA          =>UN_COMPANIA, 
                    UN_VIGENCIA          =>'TODAS', 
                    UN_CODIGO            =>'TODOS', 
                    UN_USUARIO           =>UN_USUARIO);

    RETURN MI_LOG_ACTUALIZADAS || CHR(10) || MI_LOG_NO_ACTUALIZADAS;

END FC_ACTUALIZAR_ACTIVXPROYECTO;

FUNCTION FC_CARGA_ARMONIZ_PPTAL
(
  /*
    NAME               : FC_CARGA_ARMONIZ_PPTAL
    AUTHORS            : CFBARRERA
    DATE               : 24/02/2026
    CONTROL DE CAMBIO  : CC_3630
    DESCRIPTION : Proceso que permite insertar de manera masiva registros que vienen
                  desde plantilla excel armonización plan de desarrollo presupuesto.

    --NAME  : cargarRubrosProyecto
    --METHOD: POST
  */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA     IN CLOB,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_FILAS         PCK_SYSMAN_UTL.T_SPLITCL;

    -- Variables que mapean las columnas del excel (entrada)
    MI_VIGENCIA_PLAN   VARCHAR2(10);
    MI_VIGENCIA_META   VARCHAR2(10);
    MI_ID_META         VARCHAR2(100);
    MI_VIGENCIA_RUBRO  VARCHAR2(10);
    MI_ID_RUBRO        VARCHAR2(100);
    MI_FUENTE          VARCHAR2(20);
    MI_CENTROCOSTO     VARCHAR2(20);
    MI_REFERENCIA      VARCHAR2(20);
    MI_AUXILIAR        VARCHAR2(32);
    MI_DESC_META       VARCHAR2(500);

    -- Variables con valores confirmados desde BD (los que se insertan)
    MI_VIGENCIA_PLAN_BD   NUMBER;
    MI_VIGENCIA_META_BD   NUMBER;
    MI_ID_META_BD         VARCHAR2(100);
    MI_VIGENCIA_RUBRO_BD  NUMBER;
    MI_ID_RUBRO_BD        VARCHAR2(100);
    MI_FUENTE_BD          VARCHAR2(20);
    MI_CENTROCOSTO_BD     VARCHAR2(20);
    MI_REFERENCIA_BD      VARCHAR2(20);
    MI_AUXILIAR_BD        VARCHAR2(32);

    -- FIX ORA-06502: VARCHAR2(32767) es el límite  PL/SQL
    MI_FILA_STR        VARCHAR2(32767);

    -- Variables para FC_ACME
    MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES         CLOB;  

    -- Control de flujo y contadores
    NUM_FILA           NUMBER  := 0;
    NUM_INSERTADOS     NUMBER  := 0;
    NUM_OMITIDOS       NUMBER  := 0;
    NUM_ERRORES        NUMBER  := 0;

    -- LOGs separados en formato compacto
    MI_LOG_INSERTADOS  CLOB := '';
    MI_LOG_OMITIDOS    CLOB := '';
    MI_LOG_ERRORES     CLOB := '';

    -- ================================================================
    -- FUNCION LOCAL: Extrae la columna N de un string delimitado
    -- ================================================================
    FUNCTION GET_COL(
        P_LINEA IN VARCHAR2,
        P_POS   IN NUMBER,
        P_SEP   IN VARCHAR2,
        P_MAX   IN NUMBER DEFAULT 500   -- límite máximo del valor retornado
    )
    RETURN VARCHAR2
    IS
        V_START  NUMBER;
        V_END    NUMBER;
        V_VAL    VARCHAR2(32767);
    BEGIN
        IF P_LINEA IS NULL THEN RETURN NULL; END IF;

        IF P_POS = 1 THEN
            V_END := INSTR(P_LINEA, P_SEP, 1, 1);
            IF V_END = 0 THEN
                V_VAL := TRIM(P_LINEA);
            ELSE
                V_VAL := TRIM(SUBSTR(P_LINEA, 1, V_END - 1));
            END IF;
        ELSE
            V_START := INSTR(P_LINEA, P_SEP, 1, P_POS - 1);
            IF V_START = 0 THEN RETURN NULL; END IF;
            V_START := V_START + LENGTH(P_SEP);
            V_END   := INSTR(P_LINEA, P_SEP, V_START, 1);
            IF V_END = 0 THEN
                V_VAL := TRIM(SUBSTR(P_LINEA, V_START));
            ELSE
                V_VAL := TRIM(SUBSTR(P_LINEA, V_START, V_END - V_START));
            END IF;
        END IF;

        -- SUBSTR defensivo para no exceder la variable destino
        RETURN SUBSTR(V_VAL, 1, P_MAX);

    EXCEPTION
        WHEN OTHERS THEN RETURN NULL;
    END GET_COL;

BEGIN

    -- ================================================================
    -- SEPARAR FILAS DEL CLOB
    -- ================================================================
    MI_FILAS := PCK_SYSMAN_UTL.FC_SPLIT_CL(
                    UN_LISTA       => UN_CADENA,
                    UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                );

    FOR I IN MI_FILAS.FIRST .. MI_FILAS.LAST LOOP

        NUM_FILA := NUM_FILA + 1;

        -- Limpiar variables en cada iteración
        MI_VIGENCIA_PLAN_BD  := NULL;
        MI_VIGENCIA_META_BD  := NULL;
        MI_ID_META_BD        := NULL;
        MI_VIGENCIA_RUBRO_BD := NULL;
        MI_ID_RUBRO_BD       := NULL;
        MI_FUENTE_BD         := NULL;
        MI_CENTROCOSTO_BD    := NULL;
        MI_REFERENCIA_BD     := NULL;
        MI_AUXILIAR_BD       := NULL;

        -- ============================================================
        -- Convertir fila CLOB ¿ VARCHAR2(32767) y parsear columnas
        -- Columnas: 1:VIGENCIA_INICIAL_PLAN | 2:VIGENCIA_META  | 3:CODIGO_META
        --           4:VIGENCIA_RUBRO        | 5:CODIGO_RUBRO   | 6:FUENTE
        --           7:CENTROCOSTO           | 8:REFERENCIA     | 9:AUXILIAR
        --           10:DESCRIPCION_META
        -- ============================================================
        BEGIN
            MI_FILA_STR := DBMS_LOB.SUBSTR(MI_FILAS(I), 32767, 1);
        EXCEPTION
            WHEN OTHERS THEN
                NUM_ERRORES := NUM_ERRORES + 1;
                MI_LOG_ERRORES := MI_LOG_ERRORES
                    || CHR(10) || 'FILA ' || NUM_FILA
                    || ' - Error al leer la fila: ' || SQLERRM;
                CONTINUE;
        END;

        MI_VIGENCIA_PLAN  := GET_COL(MI_FILA_STR, 1,  PCK_DATOS.GL_SEPARADOR_COL, 10);
        MI_VIGENCIA_META  := GET_COL(MI_FILA_STR, 2,  PCK_DATOS.GL_SEPARADOR_COL, 10);
        MI_ID_META        := GET_COL(MI_FILA_STR, 3,  PCK_DATOS.GL_SEPARADOR_COL, 100);
        MI_VIGENCIA_RUBRO := GET_COL(MI_FILA_STR, 4,  PCK_DATOS.GL_SEPARADOR_COL, 10);
        MI_ID_RUBRO       := GET_COL(MI_FILA_STR, 5,  PCK_DATOS.GL_SEPARADOR_COL, 100);
        MI_FUENTE         := GET_COL(MI_FILA_STR, 6,  PCK_DATOS.GL_SEPARADOR_COL, 20);
        MI_CENTROCOSTO    := GET_COL(MI_FILA_STR, 7,  PCK_DATOS.GL_SEPARADOR_COL, 20);
        MI_REFERENCIA     := GET_COL(MI_FILA_STR, 8,  PCK_DATOS.GL_SEPARADOR_COL, 20);
        MI_AUXILIAR       := GET_COL(MI_FILA_STR, 9,  PCK_DATOS.GL_SEPARADOR_COL, 32);
        MI_DESC_META      := GET_COL(MI_FILA_STR, 10, PCK_DATOS.GL_SEPARADOR_COL, 500);

        -- ============================================================
        -- VALIDACIÓN 1: Campos obligatorios no vacíos / NoDato
        -- ============================================================
        IF    MI_VIGENCIA_PLAN  IS NULL OR MI_VIGENCIA_PLAN  = 'NoDato'
           OR MI_VIGENCIA_META  IS NULL OR MI_VIGENCIA_META  = 'NoDato'
           OR MI_ID_META        IS NULL OR MI_ID_META        = 'NoDato'
           OR MI_VIGENCIA_RUBRO IS NULL OR MI_VIGENCIA_RUBRO = 'NoDato'
           OR MI_ID_RUBRO       IS NULL OR MI_ID_RUBRO       = 'NoDato'
           OR MI_FUENTE         IS NULL OR MI_FUENTE         = 'NoDato'
           OR MI_CENTROCOSTO    IS NULL OR MI_CENTROCOSTO    = 'NoDato'
           OR MI_REFERENCIA     IS NULL OR MI_REFERENCIA     = 'NoDato'
           OR MI_AUXILIAR       IS NULL OR MI_AUXILIAR       = 'NoDato'
        THEN
            NUM_ERRORES := NUM_ERRORES + 1;
            MI_LOG_ERRORES := MI_LOG_ERRORES
                || TO_CLOB(CHR(10) || 'FILA ' || NUM_FILA
                || ': META='   || NVL(MI_ID_META,  'N/A')
                || ' | RUBRO=' || NVL(MI_ID_RUBRO, 'N/A')
                || ' - Datos obligatorios incompletos o sin diligenciar.');
            CONTINUE;
        END IF;

        -- ============================================================
        -- VALIDACIÓN 2: Existencia META en BP_PLAN_INDICATIVO_METAS
        -- ============================================================
        BEGIN
            SELECT
                BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN,
                BP_PLAN_INDICATIVO_METAS.VIGENCIA_META,
                BP_PLAN_INDICATIVO_METAS.ID_PLAN
            INTO
                MI_VIGENCIA_PLAN_BD,
                MI_VIGENCIA_META_BD,
                MI_ID_META_BD
            FROM BP_NIVEL_PLAN_IND
            INNER JOIN BP_PLAN_INDICATIVO_METAS
                    ON BP_NIVEL_PLAN_IND.COMPANIA = BP_PLAN_INDICATIVO_METAS.COMPANIA
                   AND BP_NIVEL_PLAN_IND.VIGENCIA = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META
            WHERE BP_NIVEL_PLAN_IND.COMPANIA                = UN_COMPANIA
              AND BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN    = TO_NUMBER(MI_VIGENCIA_PLAN)
              AND BP_PLAN_INDICATIVO_METAS.VIGENCIA_META    = TO_NUMBER(MI_VIGENCIA_META)
              AND BP_PLAN_INDICATIVO_METAS.ID_PLAN          = MI_ID_META
              AND BP_NIVEL_PLAN_IND.META_PRODUC            IN ('-1')
              AND LENGTH(BP_PLAN_INDICATIVO_METAS.ID_PLAN) IN BP_NIVEL_PLAN_IND.DIGITOS
              AND ROWNUM = 1;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NUM_ERRORES := NUM_ERRORES + 1;
                MI_LOG_ERRORES := MI_LOG_ERRORES
                    || CHR(10) || 'FILA ' || NUM_FILA
                    || ': META=' || MI_ID_META
                    || ' (VIG_PLAN=' || MI_VIGENCIA_PLAN
                    || ', VIG_META=' || MI_VIGENCIA_META || ')'
                    || ' - Meta no existe en el plan indicativo o campos modificados.';
                CONTINUE;
               WHEN OTHERS THEN
                NUM_ERRORES := NUM_ERRORES + 1;
                MI_LOG_ERRORES := MI_LOG_ERRORES
                    || TO_CLOB(CHR(10) || 'FILA ' || NUM_FILA
                    || ': META=' || MI_ID_META
                    || ' - Error al validar la meta: ' || SQLERRM);
                CONTINUE;
            END;
        -- ============================================================
        -- VALIDACIÓN 3: Existencia RUBRO en el plan presupuestal
        -- ============================================================
        BEGIN
            SELECT
                BP_RUBRO_INVERSION_DET.RUBRO,
                PLAN_PRESUPUESTAL.FUENTE_RECURSO,
                PLAN_PRESUPUESTAL.CENTRO_COSTO,
                PLAN_PRESUPUESTAL.REFERENCIA,
                PLAN_PRESUPUESTAL.AUXILIAR,
                BP_RUBRO_INVERSION_DET.VIGENCIA
            INTO
                MI_ID_RUBRO_BD,
                MI_FUENTE_BD,
                MI_CENTROCOSTO_BD,
                MI_REFERENCIA_BD,
                MI_AUXILIAR_BD,
                MI_VIGENCIA_RUBRO_BD
            FROM BP_RUBRO_INVERSION_DET
            INNER JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
                    ON BP_RUBRO_INVERSION_DET.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                   AND BP_RUBRO_INVERSION_DET.VIGENCIA = PLAN_PRESUPUESTAL.ANO
                   AND BP_RUBRO_INVERSION_DET.RUBRO    = PLAN_PRESUPUESTAL.CODIGO
            WHERE BP_RUBRO_INVERSION_DET.COMPANIA      = UN_COMPANIA
              AND BP_RUBRO_INVERSION_DET.VIGENCIA       = TO_NUMBER(MI_VIGENCIA_RUBRO)
              AND BP_RUBRO_INVERSION_DET.RUBRO          = MI_ID_RUBRO
              AND PLAN_PRESUPUESTAL.FUENTE_RECURSO      = MI_FUENTE
              AND PLAN_PRESUPUESTAL.CENTRO_COSTO        = MI_CENTROCOSTO
              AND PLAN_PRESUPUESTAL.REFERENCIA          = MI_REFERENCIA
              AND PLAN_PRESUPUESTAL.AUXILIAR            = MI_AUXILIAR
              AND PLAN_PRESUPUESTAL.MOVIMIENTO         NOT IN (0)
              AND ROWNUM = 1;

        EXCEPTION
           WHEN NO_DATA_FOUND THEN
            NUM_ERRORES := NUM_ERRORES + 1;
            MI_LOG_ERRORES := MI_LOG_ERRORES
                || TO_CLOB( CHR(10) || 'META: '  || MI_ID_META_BD
                || ' (Fila ' || NUM_FILA || ')'
                || ' | RUBRO=' || MI_ID_RUBRO
                || ' - Rubro no encontrado en Plan Presupuestal vigencia '
                || MI_VIGENCIA_RUBRO
                || ' con la configuracion: FUENTE=' || MI_FUENTE
                || ' | CC='  || MI_CENTROCOSTO
                || ' | REF=' || MI_REFERENCIA
                || ' | AUX=' || MI_AUXILIAR || '.');
            CONTINUE;
        END;

        -- ============================================================
        -- DATOS A INSERTAR
        -- ============================================================
        BEGIN
            BEGIN
                MI_TABLA  := ' BP_ARMONIZACIONPD ';

                MI_CAMPOS :=
                    ' COMPANIA, VIGENCIA_PLAN, VIGENCIA_META, ID_META, '
                 || ' VIGENCIA_RUBRO, ID_RUBRO, FUENTE, CENTROCOSTO, '
                 || ' REFERENCIA, AUXILIARGENERAL, CREATED_BY, DATE_CREATED ';

                MI_VALORES :=
                    TO_CLOB(' SELECT ')
                 || '    ''' || UN_COMPANIA                             || ''', '
                 || '    '   || TO_CHAR(NVL(MI_VIGENCIA_PLAN_BD,  0))  || ', '
                 || '    '   || TO_CHAR(NVL(MI_VIGENCIA_META_BD,  0))  || ', '
                 || '    ''' || MI_ID_META_BD                           || ''', '
                 || '    '   || TO_CHAR(NVL(MI_VIGENCIA_RUBRO_BD, 0))  || ', '
                 || '    ''' || MI_ID_RUBRO_BD                          || ''', '
                 || '    ''' || MI_FUENTE_BD                            || ''', '
                 || '    ''' || MI_CENTROCOSTO_BD                       || ''', '
                 || '    ''' || MI_REFERENCIA_BD                        || ''', '
                 || '    ''' || MI_AUXILIAR_BD                          || ''', '
                 || '    ''' || UN_USUARIO                              || ''', '
                 || '    SYSTIMESTAMP '
                 || ' FROM DUAL '
                 || ' WHERE NOT EXISTS ( '
                 || '    SELECT 1 FROM BP_ARMONIZACIONPD '
                 || '    WHERE COMPANIA      = ''' || UN_COMPANIA                            || ''''
                 || '    AND VIGENCIA_PLAN   = '   || TO_CHAR(NVL(MI_VIGENCIA_PLAN_BD,  0))
                 || '    AND VIGENCIA_META   = '   || TO_CHAR(NVL(MI_VIGENCIA_META_BD,  0))
                 || '    AND ID_META         = ''' || MI_ID_META_BD                          || ''''
                 || '    AND VIGENCIA_RUBRO  = '   || TO_CHAR(NVL(MI_VIGENCIA_RUBRO_BD, 0))
                 || '    AND ID_RUBRO        = ''' || MI_ID_RUBRO_BD                         || ''''
                 || '    AND FUENTE          = ''' || MI_FUENTE_BD                           || ''''
                 || '    AND CENTROCOSTO     = ''' || MI_CENTROCOSTO_BD                      || ''''
                 || '    AND REFERENCIA      = ''' || MI_REFERENCIA_BD                       || ''''
                 || '    AND AUXILIARGENERAL = ''' || MI_AUXILIAR_BD                         || ''''
                 || ' ) ';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA   => MI_TABLA,
                                        UN_ACCION  => 'IS',
                                        UN_CAMPOS  => MI_CAMPOS,
                                        UN_VALORES => MI_VALORES
                                    );

            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                NUM_ERRORES := NUM_ERRORES + 1;
                MI_LOG_ERRORES := MI_LOG_ERRORES
                    || TO_CLOB(CHR(10) || 'FILA ' || NUM_FILA
                    || ': META=' || NVL(MI_ID_META_BD,  'N/A')
                    || ' | RUBRO=' || NVL(MI_ID_RUBRO_BD, 'N/A')
                    || ' - Error al insertar: ' || SQLERRM);
                CONTINUE;
        END;

        -- ============================================================
        -- RETORNO DEL RESULTADO
        -- ============================================================
        IF TO_NUMBER(PCK_DATOS.GL_RTA) > 0 THEN
            NUM_INSERTADOS := NUM_INSERTADOS + 1;
            MI_LOG_INSERTADOS := MI_LOG_INSERTADOS
                || CHR(10) || MI_ID_META_BD
                || ' | RUBRO=' || MI_ID_RUBRO_BD
                || ' | VIG='   || TO_CHAR(MI_VIGENCIA_RUBRO_BD)
                || ' - Cargado.';
        ELSE
            NUM_OMITIDOS := NUM_OMITIDOS + 1;
            MI_LOG_OMITIDOS := MI_LOG_OMITIDOS
                || CHR(10) || MI_ID_META_BD
                || ' | RUBRO=' || MI_ID_RUBRO_BD
                || ' | VIG='   || TO_CHAR(MI_VIGENCIA_RUBRO_BD)
                || ' - Ya existe en BP_ARMONIZACIONPD.';
        END IF;

    END LOOP;

    -- ================================================================
    -- COMMIT solo si hubo al menos un registro insertado
    -- ================================================================
    IF NUM_INSERTADOS > 0 THEN
        COMMIT;
    END IF;

    -- ================================================================
    -- RETORNO DEL LOG FINAL
    -- ================================================================
    RETURN
        TO_CLOB('============================================================') || CHR(10) ||
        'CARGA ARMONIZACIÓN PLAN DESARROLLO - PRESUPUESTO'                      || CHR(10) ||
        'Fecha    : ' || TO_CHAR(SYSDATE, 'DD/MM/YYYY')                || CHR(10) ||
        'Compañía : ' || UN_COMPANIA                                            || CHR(10) ||
        '============================================================'          || CHR(10) ||
        'Filas procesadas : ' || TO_CHAR(NUM_FILA)       || CHR(10) ||
        'Insertados       : ' || TO_CHAR(NUM_INSERTADOS) || CHR(10) ||
        'Omitidos         : ' || TO_CHAR(NUM_OMITIDOS)   || CHR(10) ||
        'Con error        : ' || TO_CHAR(NUM_ERRORES)    || CHR(10) ||
        '============================================================' || CHR(10) ||

        CASE WHEN NUM_ERRORES > 0 THEN
            CHR(10) || 'DETALLE DE ERRORES:'                               || CHR(10) ||
            '------------------------------------------------------------' ||
            MI_LOG_ERRORES                                                  || CHR(10) ||
            '------------------------------------------------------------'  || CHR(10)
        ELSE '' END ||
        -- Insertados
        CHR(10) || 'REGISTROS INSERTADOS:'                                || CHR(10) ||
        '------------------------------------------------------------'     ||
        CASE WHEN NUM_INSERTADOS > 0
             THEN MI_LOG_INSERTADOS
             ELSE TO_CLOB(CHR(10) || '(Ninguno)')
        END                                                                || CHR(10) ||
        '------------------------------------------------------------'     || CHR(10) ||
        -- Omitidos
        CHR(10) || 'REGISTROS OMITIDOS (YA EXISTEN):'                     || CHR(10) ||
        '------------------------------------------------------------'     ||
        CASE WHEN NUM_OMITIDOS > 0
             THEN MI_LOG_OMITIDOS
             ELSE TO_CLOB(CHR(10) || '(Ninguno)')
        END                                                                || CHR(10) ||
        '------------------------------------------------------------'     || CHR(10) ||
        '============================================================'     || CHR(10) ||
        CASE
            WHEN NUM_ERRORES    = 0 AND NUM_OMITIDOS  = 0
            THEN 'RESULTADO: PROCESO EJECUTADO CORRECTAMENTE. TODOS LOS REGISTROS FUERON INSERTADOS.'
            WHEN NUM_INSERTADOS = 0 AND NUM_ERRORES   > 0
            THEN 'RESULTADO: PROCESO FINALIZADO CON ERRORES. NINGÚN REGISTRO FUE INSERTADO. REVISE EL LOG.'
            WHEN NUM_INSERTADOS = 0 AND NUM_OMITIDOS  > 0
            THEN 'RESULTADO: TODOS LOS REGISTROS YA EXISTEN EN BP_ARMONIZACIONPD.'
            ELSE 'RESULTADO: PROCESO FINALIZADO PARCIALMENTE. REVISE EL LOG PARA VER DETALLE.'
        END;

END FC_CARGA_ARMONIZ_PPTAL;

END PCK_BANCOS_PROY1;