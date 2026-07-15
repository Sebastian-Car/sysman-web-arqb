create or replace PACKAGE BODY "PCK_BANCOS_PROY2" AS
/*
Cuerpo del paquete PCK_BANCOS_PROY2
*/
-- 1
-- Procedimiento Mantenimiento_Act_Proyecto

FUNCTION FC_MANTENIMIENTO_ACT_PROYECTO
/*
   NAME 			      : FC_MANTENIMIENTO_ACT_PROYECTO
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE MIGRADOR	  : 26/08/2015
   TIME				      : 04:00 PM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Proceso para actualizar proyectos desde novedades
   MODIFIER			    : JUAN PABLO ACEVEDO TORRES
   DATE MODIFIED	  : 24/09/2020
   TIME				      : 08:39 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
   @Name: actualizarProyectoMante
*/
(
    UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA, -- Código de la compañía
    UN_PROYECTO_INICIAL IN VARCHAR2,                 -- Proyecto inicial
    UN_PROYECTO_FINAL 	IN VARCHAR2,                  -- Proyecto final
    UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO

) RETURN VARCHAR2 AS
    MI_CONSULTA 		    PCK_SUBTIPOS.TI_CONSULTA;
    MI_VALORES			    PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
    MI_TIPOT            BPNOVEDADPROYECTO.TIPOT%TYPE;  
    MI_CLASET           BPNOVEDADPROYECTO.CLASET%TYPE;
    MI_CODIGO           BPNOVEDADPROYECTO.CODIGO%TYPE;
    MI_DEPENDENCIA      BPNOVEDADPROYECTO.DEPENDENCIA%TYPE;
    MI_VIGENCIA         BPNOVEDADPROYECTO.VIGENCIA%TYPE;
    MI_CLASENOVEDAD     BPTIPONOVEDAD.CLASENOVEDAD%TYPE;
    MI_PROYECTO         BP_D_NOVEDADPROYECTO.PROYECTO%TYPE;
    MI_VALORTOTAL       PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORPROGRAMADO  PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORSOLICITADO  PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_DISMINUIDO PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOREJECUTADO   PCK_SUBTIPOS.TI_DOBLE;
    RS                  SYS_REFCURSOR;


BEGIN
    GL_ANOMALIAS := '';
	-- Verifica que el detalle de la novedad tenga componente

        BEGIN
            BEGIN
                MI_TABLA      := 'BP_D_NOVEDADPROYECTO';
                MI_MERGEUSING :=' SELECT COMPONENTES.TIPOCOMPONENTE,
                                         BP_D_NOVEDADPROYECTO.COMPANIA,
                                         BP_D_NOVEDADPROYECTO.TIPOT,
                                         BP_D_NOVEDADPROYECTO.CLASET,
                                         BP_D_NOVEDADPROYECTO.NOVEDAD,
                                         BP_D_NOVEDADPROYECTO.DEPENDENCIA,
                                         BP_D_NOVEDADPROYECTO.CODIGO,
                                         BP_D_NOVEDADPROYECTO.PROYECTO
                                    FROM BP_D_NOVEDADPROYECTO
                                   INNER JOIN COMPONENTES
                                      ON BP_D_NOVEDADPROYECTO.COMPANIA       = COMPONENTES.COMPANIA
                                     AND BP_D_NOVEDADPROYECTO.PROYECTO       = COMPONENTES.CODIGOPROYECTO
                                     AND BP_D_NOVEDADPROYECTO.COMPONENTE     = COMPONENTES.CODIGO
                                     AND BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE = COMPONENTES.TIPOCOMPONENTE
                                   WHERE BP_D_NOVEDADPROYECTO.COMPANIA       = ''' || UN_COMPANIA || '''
                                     AND BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE IS NULL
                                     AND BP_D_NOVEDADPROYECTO.PROYECTO       BETWEEN  ''' || UN_PROYECTO_INICIAL || ''' 
                                     AND ''' || UN_PROYECTO_FINAL || '''';
                MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA    AND
                                    TABLA.TIPOT       = VISTA.TIPOT       AND
                                    TABLA.CLASET      = VISTA.CLASET      AND
                                    TABLA.NOVEDAD     = VISTA.NOVEDAD     AND
                                    TABLA.DEPENDENCIA = VISTA.DEPENDENCIA AND
                                    TABLA.CODIGO      = VISTA.CODIGO      AND
                                    TABLA.PROYECTO    = VISTA.PROYECTO';

                MI_MERGEEXISTE := 'UPDATE SET TABLA.TIPOCOMPONENTE = VISTA.TIPOCOMPONENTE, 
                                   TABLA.MODIFIED_BY = '''||UN_USUARIO||''', 
                                   TABLA.DATE_MODIFIED = SYSDATE ';

                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                            ,UN_ACCION 	  => 'MM'
                                            ,UN_MERGEUSING => MI_MERGEUSING
                                            ,UN_MERGEENLACE=> MI_MERGEENLACE
                                            ,UN_MERGEEXISTE=> MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;    
  -- Limpieza de saldos
            BEGIN
                MI_TABLA      := ' BP_D_NOVEDADPROYECTO';
                MI_MERGEUSING := ' SELECT BPNOVEDADPROYECTO.COMPANIA,
                                          BPNOVEDADPROYECTO.TIPOT,
                                          BPNOVEDADPROYECTO.CLASET,
                                          BPNOVEDADPROYECTO.DEPENDENCIA,
                                          BPNOVEDADPROYECTO.CODIGO
                                     FROM BPNOVEDADPROYECTO
                                    INNER JOIN BP_D_NOVEDADPROYECTO
                                       ON BPNOVEDADPROYECTO.COMPANIA    = BP_D_NOVEDADPROYECTO.COMPANIA
                                      AND BPNOVEDADPROYECTO.TIPOT 	    = BP_D_NOVEDADPROYECTO.TIPOT
                                      AND BPNOVEDADPROYECTO.CLASET 	    = BP_D_NOVEDADPROYECTO.CLASET
                                      AND BPNOVEDADPROYECTO.CODIGO      = BP_D_NOVEDADPROYECTO.NOVEDAD
                                      AND BPNOVEDADPROYECTO.DEPENDENCIA = BP_D_NOVEDADPROYECTO.DEPENDENCIA
                                    WHERE BPNOVEDADPROYECTO.COMPANIA    = ''' || UN_COMPANIA || '''
                                      AND BP_D_NOVEDADPROYECTO.PROYECTO BETWEEN ''' || UN_PROYECTO_INICIAL  || '''  
                                      AND ''' || UN_PROYECTO_FINAL  || '''
                                      AND NVL(BP_D_NOVEDADPROYECTO.CONPROGRAMACION, 0) NOT IN(0)';
                MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA    AND
                                    TABLA.TIPOT       = VISTA.TIPOT       AND
                                    TABLA.CLASET      = VISTA.CLASET      AND
                                    TABLA.DEPENDENCIA = VISTA.DEPENDENCIA AND
                                    TABLA.CODIGO      = VISTA.CODIGO ';
                MI_MERGEEXISTE := ' UPDATE SET TABLA.CONPROGRAMACION = 0, 
                                    TABLA.MODIFIED_BY = '''||UN_USUARIO||''', 
                                    TABLA.DATE_MODIFIED = SYSDATE ';

                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                            ,UN_ACCION 	  => 'MM'
                                            ,UN_MERGEUSING => MI_MERGEUSING
                                            ,UN_MERGEENLACE=> MI_MERGEENLACE
                                            ,UN_MERGEEXISTE=> MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_UPDATE_CONSULTA,
                                        UN_TABLAERROR => MI_TABLA);
        END;
-- Limpieza de los valores de proyectos a cero
-- PROYECTOS

        BEGIN
            BEGIN
                MI_VALORES := ' VALOREJECUTADO	= 0, VALORSOLICITADO	= 0,
                                VALORPROGRAMADO	= 0, CONPROGRAMACION	= 0,
                                DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USUARIO || '''';

                MI_CONSULTA := '      COMPANIA = ''' || UN_COMPANIA || '''
                                AND 	CODIGO   BETWEEN ''' || UN_PROYECTO_INICIAL  || ''' 
                                AND    ''' || UN_PROYECTO_FINAL  || '''';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA 		=> 'PROYECTOS'
                                           ,UN_ACCION 	=> 'M'
                                           ,UN_CAMPOS 	=> MI_VALORES
                                           ,UN_CONDICION => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                       UN_ERROR_COD   =>PCK_ERRORES.ER_BANCOSP_UPDATE_PROYECTOS,
                                       UN_TABLAERROR => MI_TABLA);
        END;
/*
        BEGIN
            BEGIN
                -- COMPONENTES
                MI_VALORES := 'VALOREJECUTADO		    = 0,
                               VALORTOTALSOLICITADO = 0,
                               VALORPROGRAMADO		  = 0,
                               SALDOCOMPONENTE		  = 0,
                               CONPROGRAMACION 		  = 0,
                               DATE_MODIFIED = SYSDATE,
                               MODIFIED_BY = ''' || UN_USUARIO  || '''';
                MI_CONSULTA := ' COMPANIA = ''' || UN_COMPANIA || '''
                                AND CODIGOPROYECTO BETWEEN ''' || UN_PROYECTO_INICIAL  || ''' 
                                AND ''' || UN_PROYECTO_FINAL   || '''';
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA 	   => 'COMPONENTES'
                                           ,UN_ACCION 	 => 'M'
                                           ,UN_CAMPOS 	 => MI_VALORES
                                           ,UN_CONDICION => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                       UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPONENTES,
                                       UN_TABLAERROR => MI_TABLA);
        END;
        */
 -- ACTUALIZACION DE LA TABLA PROYECTOS se actulilzan los valores del proyecto desde la tabla componentes_activiades
        BEGIN
            BEGIN

            MI_CONSULTA := '      COMPANIA = ''' || UN_COMPANIA || '''
                                AND 	CODIGO   BETWEEN ''' || UN_PROYECTO_INICIAL  || ''' 
                                AND    ''' || UN_PROYECTO_FINAL  || '''';            

            /* SELECT SUM(CA.COSTOTOTAL) VALORTOTAL,
            SUM(CA.VALORPROGRAMADO) VALORPROGRAMADO,
            SUM(CA.VALOR_SOLICITADO_ACTIVIDAD) VALORSOLICITADO,
            SUM(CA.VALOR_DISMINUIDO) VALOR_DISMINUIDO,
            SUM(CA.VALOREJECUTADO ) VALOREJECUTADO
            INTO MI_VALORTOTAL,MI_VALORPROGRAMADO,MI_VALORSOLICITADO,MI_VALOR_DISMINUIDO,MI_VALOREJECUTADO
            FROM COMPONENTES_ACTIVIDADES CA
            WHERE CA.COMPANIA=UN_COMPANIA 
            AND CA.CODIGOPROYECTO = UN_PROYECTO_INICIAL;*/
            
            
            SELECT SUM(CA.VALORTOTAL) VALORTOTAL,
            SUM(CA.VALORPROGRAMADO) VALORPROGRAMADO,
            SUM(CA.VALORTOTALSOLICITADO) VALORSOLICITADO,
            SUM(CA.VALOR_DISMINUIDO) VALOR_DISMINUIDO,
            SUM(CA.VALOREJECUTADO ) VALOREJECUTADO
            INTO MI_VALORTOTAL,MI_VALORPROGRAMADO,MI_VALORSOLICITADO,MI_VALOR_DISMINUIDO,MI_VALOREJECUTADO
            FROM COMPONENTES CA
            WHERE CA.COMPANIA=UN_COMPANIA 
            AND CA.CODIGOPROYECTO = UN_PROYECTO_INICIAL;
            
            
             /* MI_VALORES := ' VALOREJECUTADO	='''||MI_VALOREJECUTADO||''',
                                VALORSOLICITADO	='''||MI_VALORSOLICITADO||''',
                                VALORPROGRAMADO	='''||MI_VALORPROGRAMADO||''',
                                VALOR_DISMINUIDO ='''||MI_VALOR_DISMINUIDO||''',
                                DATE_MODIFIED = SYSDATE,
                                MODIFIED_BY = ''' || UN_USUARIO || ''''; */
            

                MI_VALORES := ' VALOREJECUTADO	='''||MI_VALOREJECUTADO||''',
                                VALORSOLICITADO	='''||MI_VALORSOLICITADO||''',

                                VALOR_DISMINUIDO ='''||MI_VALOR_DISMINUIDO||''',
                                DATE_MODIFIED = SYSDATE,
                                MODIFIED_BY = ''' || UN_USUARIO || '''';                             
                          

               /* MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA 		=> 'PROYECTOS'
                                           ,UN_ACCION 	=> 'M'
                                           ,UN_CAMPOS 	=> MI_VALORES
                                           ,UN_CONDICION => MI_CONSULTA);*/
                                           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                       UN_ERROR_COD   =>PCK_ERRORES.ER_BANCOSP_UPDATE_PROYECTOS,
                                       UN_TABLAERROR => MI_TABLA);
        END;

        BEGIN
            BEGIN
              -- COMPONENTES_ACTIVIDADES

              MI_CONSULTA := '      COMPANIA = ''' || UN_COMPANIA || '''
                        AND 	CODIGOPROYECTO   BETWEEN ''' || UN_PROYECTO_INICIAL  || ''' 
                        AND    ''' || UN_PROYECTO_FINAL  || '''';

                MI_TABLA    := 'COMPONENTES_ACTIVIDADES';
                MI_VALORES  := 'VALOREJECUTADO		          = 0,
                                VALOR_SOLICITADO_ACTIVIDAD	= 0,
                                VALORPROGRAMADO	          = 0,
                                VALOR_DISMINUIDO        = 0,
                                CANTIDAD_EJE				        = 0,
                                DATE_MODIFIED = SYSDATE,
                                MODIFIED_BY = ''' || UN_USUARIO || '''';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA 	   => MI_TABLA
                                           ,UN_ACCION 	 => 'M'
                                           ,UN_CAMPOS 	 => MI_VALORES
                                           ,UN_CONDICION => MI_CONSULTA);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPSACTV
                                      ,UN_TABLAERROR => MI_TABLA);
        END;

        BEGIN

            BEGIN
                    BEGIN
                        MI_TABLA      := 'BP_INCONSISTENCIAS_NOVE';
                        MI_CONSULTA   := 'COMPANIA = ''' || UN_COMPANIA || '''';
                        MI_RTA        := PCK_DATOS.FC_ACME(UN_ACCION   => 'E',
                                                           UN_TABLA    => MI_TABLA,
                                                           UN_CONDICION=> MI_CONSULTA);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_DELET_TOT_PROY,
                                        UN_TABLAERROR => MI_TABLA); 
            END;    

           /*
            BEGIN
                -- Preparación de novedades

                MI_TABLA := 'TEMP_AUX_MANTENIMIENTO';
                MI_VALORES := 'COMPANIA
                              , TIPOT
                              , CLASET
                              , CODIGO
                              , DEPENDENCIA
                              , VIGENCIA
                              , CLASENOVEDAD
                              , PROYECTO';

                MI_CONSULTA := 'SELECT 
                                          A.COMPANIA
                                        , A.TIPOT
                                        , A.CLASET
                                        , A.CODIGO
                                        , A.DEPENDENCIA
                                        , A.VIGENCIA
                                        , A.CLASENOVEDAD
                                        , A.PROYECTO FROM(SELECT  N.COMPANIA
                                        , N.TIPOT
                                        , N.CLASET
                                        , N.CODIGO
                                        , N.DEPENDENCIA
                                        , N.VIGENCIA
                                        , T.CLASENOVEDAD
                                        , D.PROYECTO
                                  FROM BPNOVEDADPROYECTO N
                                 INNER JOIN BPTIPONOVEDAD T
                                    ON N.COMPANIA = T.COMPANIA
                                   AND N.TIPOT    = T.TIPOT
                                   AND N.CLASET   = T.CLASET
                                 INNER JOIN BP_D_NOVEDADPROYECTO D
                                    ON N.COMPANIA    = D.COMPANIA
                                   AND N.TIPOT    	  = D.TIPOT
                                   AND N.CLASET      = D.CLASET
                                   AND N.CODIGO      = D.NOVEDAD
                                   AND N.DEPENDENCIA = D.DEPENDENCIA
                                 WHERE N.COMPANIA = ''' || UN_COMPANIA || '''
                                   AND N.VOBOBP   NOT IN (0)
                                   AND N.CLASET   IN(''B'',''P'')
                                   AND N.ESTADO   IN(''V'',''AP'')
                                   AND TO_NUMBER(NVL(T.AFECTAPROGRAMACION,0)) NOT IN(0)
                                   AND D.PROYECTO BETWEEN ''' || UN_PROYECTO_INICIAL || ''' AND ''' ||UN_PROYECTO_FINAL || '''
                              GROUP BY N.COMPANIA,
                                       N.TIPOT,
                                       N.CLASET,
                                       N.CODIGO,
                                       N.DEPENDENCIA,
                                       N.VIGENCIA,
                                       T.CLASENOVEDAD,
                                       D.PROYECTO
                                 ORDER BY D.PROYECTO) A
                                 LEFT JOIN TEMP_AUX_MANTENIMIENTO
                                 ON    A.COMPANIA       = TEMP_AUX_MANTENIMIENTO.COMPANIA
                                 AND   A.TIPOT          = TEMP_AUX_MANTENIMIENTO.TIPOT
                                 AND   A.CLASET         = TEMP_AUX_MANTENIMIENTO.CLASET
                                 AND   A.CODIGO         = TEMP_AUX_MANTENIMIENTO.CODIGO
                                 AND   A.DEPENDENCIA    = TEMP_AUX_MANTENIMIENTO.DEPENDENCIA
                                 AND   A.PROYECTO       = TEMP_AUX_MANTENIMIENTO.PROYECTO
                                 WHERE TEMP_AUX_MANTENIMIENTO.COMPANIA IS NULL
                                 AND TEMP_AUX_MANTENIMIENTO.TIPOT IS NULL
                                 AND TEMP_AUX_MANTENIMIENTO.CLASET IS NULL
                                 AND TEMP_AUX_MANTENIMIENTO.CODIGO IS NULL
                                 AND TEMP_AUX_MANTENIMIENTO.DEPENDENCIA IS NULL
                                 AND TEMP_AUX_MANTENIMIENTO.PROYECTO IS NULL';

                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA
                                            ,UN_ACCION  => 'IS'
                                            ,UN_CAMPOS  => MI_VALORES
                                            ,UN_VALORES => MI_CONSULTA);


            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                       UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TAUXMANT,
                                       UN_TABLAERROR => MI_TABLA);
        END;

        COMMIT;
        */
  --
  END;

  MI_STRSQL := '';

        MI_STRSQL := 'SELECT DISTINCT
                      N.TIPOT, 
                      N.CLASET, 
                      N.CODIGO, 
                      N.DEPENDENCIA, 
                      N.VIGENCIA, 
                      T.CLASENOVEDAD,
                      D.PROYECTO                        
                      FROM BPNOVEDADPROYECTO N 
                      INNER JOIN BPTIPONOVEDAD  T 
                      ON N.COMPANIA = T.COMPANIA 
                      AND N.TIPOT = T.TIPOT 
                      AND N.CLASET = T.CLASET 
                      INNER JOIN BP_D_NOVEDADPROYECTO  D 
                      ON N.COMPANIA = D.COMPANIA 
                      AND N.TIPOT = D.TIPOT
                      AND N.CLASET = D.CLASET 
                      AND N.CODIGO = D.NOVEDAD 
                      AND N.DEPENDENCIA = D.DEPENDENCIA 
                      WHERE N.COMPANIA IN('''||UN_COMPANIA||''') 

                      AND N.CLASET IN(''B'',''P'') 
                      AND N.ESTADO IN(''V'',''AP'') 
                      AND T.AFECTAPROGRAMACION NOT IN(0)
                      AND N.VOBOBP NOT IN (0)
                      AND D.PROYECTO BETWEEN ''' || UN_PROYECTO_INICIAL || ''' AND ''' ||UN_PROYECTO_FINAL || '''
                      ORDER BY N.CODIGO';

                      --AND N.VOBOBP NOT IN (0)


        OPEN RS FOR MI_STRSQL;
        <<MANTAUXILIAR>>
        LOOP 
            FETCH RS INTO MI_TIPOT,
                          MI_CLASET,
                          MI_CODIGO,
                          MI_DEPENDENCIA,
                          MI_VIGENCIA,
                          MI_CLASENOVEDAD,
                          MI_PROYECTO;
                  EXIT WHEN RS%NOTFOUND;

          BEGIN
                MI_RTA := PCK_BANCOS_PROY2.FC_ACT_PROGRAMACION(UN_COMPANIA 	   => UN_COMPANIA
                                             ,UN_TIPO 		     => MI_TIPOT
                                             ,UN_CLASE 		   => MI_CLASET
                                             ,UN_NOVEDAD 	   => MI_CODIGO
                                             ,UN_DEPENDENCIA  => MI_DEPENDENCIA
                                             ,UN_VIGENCIA     => MI_VIGENCIA
                                             ,UN_PROYECTO     => MI_PROYECTO
                                             ,UN_ACT_PLANIND  => 0
                                             ,UN_TIPO_NOVEDAD => MI_CLASENOVEDAD
                                             ,UN_USUARIO      => UN_USUARIO);
            END;
        END LOOP MANTAUXILIAR;
        --
        MI_RTA := PCK_BANCOS_PROY2.FC_ACT_PROGRAMADO(UN_COMPANIA 		    => UN_COMPANIA
                                             ,UN_PROYECTO_INICIAL => UN_PROYECTO_INICIAL
                                             ,UN_PROYECTO_FINAL   => UN_PROYECTO_FINAL
                                             ,UN_USUARIO          => UN_USUARIO);
RETURN GL_ANOMALIAS;

END FC_MANTENIMIENTO_ACT_PROYECTO;


-- Función Cant_Actividad
FUNCTION FC_CANT_ACTIVIDAD
/*
   NAME 			      : FC_CANT_ACTIVIDAD
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE MIGRADOR	  : 28/08/2015
   TIME				      : 10:13 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Genera las cantidades iniciales de cada actividad.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 09:50 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación.
   @Name: getCantidadPorActividad
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, -- CÓdigo de la compañía
  UN_VIGENCIA         IN PCK_SUBTIPOS.TI_ENTERO,   -- Vigencia gubernamental
  UN_PROYECTO         IN VARCHAR2,                 -- Código del proyecto
  UN_COMPONENTE       IN VARCHAR2,                 -- Código del componente del proyecto
  UN_TIPO_COMPONENTE  IN VARCHAR2,                 -- Tipo de componente
  UN_ACTIVIDAD        IN VARCHAR2                  -- Actividad a la que se quiere identificar las cantidades
)
RETURN PCK_SUBTIPOS.TI_DOBLE
AS
  MI_CANTIDAD         PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  SELECT SUM(CANTIDAD)
  INTO   MI_CANTIDAD
  FROM  COMPONENTES_ACTIVIDADES
  WHERE COMPANIA = UN_COMPANIA
    AND VIGENCIA       = UN_VIGENCIA
    AND CODIGOPROYECTO = UN_PROYECTO
    AND COMPONENTE     = UN_COMPONENTE
    AND TIPOCOMPONENTE = UN_TIPO_COMPONENTE
    AND ACTIVIDAD      = UN_ACTIVIDAD;
  --
  RETURN MI_CANTIDAD;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
  MI_CANTIDAD := 0;
END FC_CANT_ACTIVIDAD;
--
-- 3
-- Función Cant_Disponible
FUNCTION FC_CANT_DISPONIBLE
/*
   NAME 			      : FC_CANT_DISPONIBLE
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE MIGRADOR	  : 28/08/2015
   TIME				      : 09:14 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Genera las cantidades disponibles de cada actividad.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 10:01 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación.
   @Name: getCantidadDisponible
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, -- Código de la compañía
  UN_ESTADO           IN VARCHAR2,                 -- E=>Ejecutado; P=>Programada; RP=>Reprogramada
  UN_VIGENCIA         IN PCK_SUBTIPOS.TI_ENTERO,   -- Vigencia gubernamental
  UN_PROYECTO         IN VARCHAR2,                 -- Código del proyecto
  UN_COMPONENTE       IN VARCHAR2,                 -- Código del componente del proyecto
  UN_TIPO_COMPONENTE  IN VARCHAR2,                 -- Tipo de componente
  UN_ACTIVIDAD        IN VARCHAR2,                 -- Actividad a la que se quiere identificar las cantidades
  UN_VAL_NUEVO        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_VAL_VIEJO        IN PCK_SUBTIPOS.TI_ENTERO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
AS
  MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO  :=  0;
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  RS                SYS_REFCURSOR;
  RSPROGRAMADO      PROGRAMACION.CANTIDAD%TYPE;
  MI_VALOR_INICIAL  PCK_SUBTIPOS.TI_ENTERO  :=  0;
BEGIN
  MI_STRSQL := 'SELECT  SUM(PROGRAMACION.CANTIDAD) AS PROGRAMADO
                FROM    PROGRAMACION
                WHERE   PROGRAMACION.COMPANIA         = ''' || UN_COMPANIA || '''
                  AND   PROGRAMACION.VIGENCIA         = ' || UN_VIGENCIA ||  '
                  AND   PROGRAMACION.CODIGOPROYECTO   = '''  || UN_PROYECTO || '''
                  AND   PROGRAMACION.CODIGOCOMPONENTE = ''' || UN_COMPONENTE || '''
                  AND   PROGRAMACION.TIPOCOMPONENTE   = ''' || UN_TIPO_COMPONENTE || '''
                  AND   PROGRAMACION.CODIGOACTIVIDAD  = ''' || UN_ACTIVIDAD || '''';
  --
  IF UN_ESTADO <> 'E' THEN
    MI_STRSQL := MI_STRSQL || ' AND   PROGRAMACION.Tipoestado <> ''E'' ';
  ELSE
    MI_STRSQL := MI_STRSQL || ' AND   PROGRAMACION.Tipoestado = ''E'' ';
  END IF;
  --
  OPEN RS FOR MI_STRSQL;
   FETCH RS INTO RSPROGRAMADO;
    IF RS%FOUND THEN
      MI_CANTIDAD := NVL(RSPROGRAMADO,0);
    END IF;
  CLOSE RS;
  --
  MI_VALOR_INICIAL := FC_CANT_ACTIVIDAD(UN_COMPANIA         => UN_COMPANIA
                                        ,UN_VIGENCIA        => UN_VIGENCIA
                                        ,UN_PROYECTO        => UN_PROYECTO
                                        ,UN_COMPONENTE      => UN_COMPONENTE
                                        ,UN_TIPO_COMPONENTE => UN_TIPO_COMPONENTE
                                        ,UN_ACTIVIDAD       =>UN_ACTIVIDAD);

  MI_CANTIDAD      := MI_VALOR_INICIAL - MI_CANTIDAD - NVL(UN_VAL_NUEVO,0) + NVL(UN_VAL_VIEJO,0);
  --
  RETURN MI_CANTIDAD;
  --
END FC_CANT_DISPONIBLE;
--
-- 4
-- Función Act_Programacion
FUNCTION FC_ACT_PROGRAMACION
/*
   NAME 			      : FC_ACT_PROGRAMACION
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE MIGRADOR	  : 27/08/2015
   TIME				      : 10:52 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Actualiza y revisa la programaciÃ³n dependiendo de la clase de novedad.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 10:39 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
   @Name: actualizarProgramacion
*/
(
  UN_COMPANIA 	   IN PCK_SUBTIPOS.TI_COMPANIA, -- Código de la compañía
  UN_TIPO          IN VARCHAR2,
  UN_CLASE         IN VARCHAR2,
  UN_NOVEDAD       IN PCK_SUBTIPOS.TI_LONG,   -- se realiza cambio de tipo TI_ENTERO a TI_LONG
  UN_DEPENDENCIA   IN VARCHAR2,
  UN_VIGENCIA      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PROYECTO      IN VARCHAR2,
  UN_ACT_PLANIND   IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPO_NOVEDAD  IN VARCHAR2,
  UN_USUARIO     	 IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA          		PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TIPO         		VARCHAR2(1 CHAR);
  MI_STRSQL       		PCK_SUBTIPOS.TI_STRSQL;
  RS              		SYS_REFCURSOR;
  RS2                 SYS_REFCURSOR;
  RSPROYECTO			    BP_D_NOVEDADPROYECTO.PROYECTO%TYPE;
  RSCOMPONENTE			  BP_D_NOVEDADPROYECTO.COMPONENTE%TYPE;
  RSTIPOCOMPONENTE		BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE%TYPE;
  RSACTIVIDAD			    BP_D_NOVEDADPROYECTO.ACTIVIDAD%TYPE;
  RSVIGENCIA			    BPNOVEDADPROYECTO.VIGENCIA%TYPE;
  RSFECHA				      BPNOVEDADPROYECTO.FECHA%TYPE;
  RSCONPROGRAMACION		BP_D_NOVEDADPROYECTO.CONPROGRAMACION%TYPE;
  RSCONPROGRAMACION_P	BP_D_NOVEDADPROYECTO.CONPROGRAMACION_P%TYPE;
  RSCONPROGRAMACION_C	BP_D_NOVEDADPROYECTO.CONPROGRAMACION_C%TYPE;
  RSPERIOCIDAD			  PROYECTOS.PERIOCIDAD%TYPE;
  RSVALORTOTAL			  PROYECTOS.VALORTOTAL%TYPE;
  RSPERIODO				    BP_D_NOVEDADPROYECTO.PERIODO%TYPE;
  RSVALORAPROBADO		  BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE;
  RSVALORSOLICITADO		BP_D_NOVEDADPROYECTO.VALORSOLICITADO%TYPE;
  RSCANTIDAD			    BP_D_NOVEDADPROYECTO.CANTIDAD%TYPE;
  RSCODIGO				    BP_D_NOVEDADPROYECTO.CODIGO%TYPE;
  RSDIFERENCIA			  BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE;
  RSVALORDISMINUIDO   BP_D_NOVEDADPROYECTO.VALORDISMINUIDO%TYPE;
  RSCONCOMPROBANTEPPTAL BPNOVEDADPROYECTO.CONCOMPROBANTEPPTAL%TYPE;
  MI_CAMPOS       		PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONSECUTIVO  		PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VALORES      		PCK_SUBTIPOS.TI_VALORES;
  MI_PORCENTAJE   		NUMBER(22,0);
  MI_CANTIDAD     		BP_D_NOVEDADPROYECTO.CANTIDAD%TYPE; 
  MI_VAL_ACTUA    		BP_D_NOVEDADPROYECTO.VALORSOLICITADO%TYPE;
  MI_VALAPROB         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CONDICION    		PCK_SUBTIPOS.TI_CONDICION;
  MI_COL_VALOR        VARCHAR2(30 CHAR);
  MI_COL_PCT          VARCHAR2(30 CHAR);
  RS2VALOR1           PROGRAMACION.VALOR1%TYPE;
  RS2VALOR2           PROGRAMACION.VALOR2%TYPE;
  RS2VALOR3           PROGRAMACION.VALOR3%TYPE;
  RS2VALOR4           PROGRAMACION.VALOR4%TYPE;
  RS2VALOR5           PROGRAMACION.VALOR5%TYPE;
  RS2VALOR6           PROGRAMACION.VALOR6%TYPE;
  RS2VALOR7           PROGRAMACION.VALOR7%TYPE;
  RS2VALOR8           PROGRAMACION.VALOR8%TYPE;
  RS2VALOR9           PROGRAMACION.VALOR9%TYPE;
  RS2VALOR10          PROGRAMACION.VALOR10%TYPE;
  RS2VALOR11          PROGRAMACION.VALOR11%TYPE;
  RS2VALOR12          PROGRAMACION.VALOR12%TYPE;
  MI_ETAPA            VARCHAR2(200 CHAR);
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_NUMERO           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_SALDOCOMPONENTE  PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
  MI_ETAPA := '01 Validando Novedad';
  -- 1. Validando novedad
  IF UN_TIPO_NOVEDAD IS NULL OR UN_TIPO_NOVEDAD = '' THEN
    BEGIN
      -- Valida que la novedad afecte programaciÃ³n
      SELECT  BPTIPONOVEDAD.CLASENOVEDAD
      INTO    MI_TIPO
      FROM    BPTIPONOVEDAD
      WHERE   BPTIPONOVEDAD.COMPANIA           = UN_COMPANIA
        AND   BPTIPONOVEDAD.TIPOT              = UN_TIPO
        AND   BPTIPONOVEDAD.CLASET             = UN_CLASE
        AND   BPTIPONOVEDAD.AFECTAPROGRAMACION <> 0;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
      MI_TIPO := '';
    END;
  ELSE
    MI_TIPO := UN_TIPO_NOVEDAD;
  END IF;

  MI_ETAPA := '02 Identificando Detalle de Novedad';
  -- 2. Identificando Detalle de Novedad
  MI_STRSQL := 'SELECT  BP_D_NOVEDADPROYECTO.PROYECTO
                        ,BP_D_NOVEDADPROYECTO.COMPONENTE
                        ,BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE
                        ,BP_D_NOVEDADPROYECTO.ACTIVIDAD
                        ,COMPONENTES_ACTIVIDADES.VIGENCIA
                        ,BPNOVEDADPROYECTO.FECHA
                        ,BP_D_NOVEDADPROYECTO.CONPROGRAMACION
                        ,BP_D_NOVEDADPROYECTO.CONPROGRAMACION_P
                        ,BP_D_NOVEDADPROYECTO.CONPROGRAMACION_C
                        ,PROYECTOS.PERIOCIDAD
                        ,PROYECTOS.VALORTOTAL
                        ,BP_D_NOVEDADPROYECTO.PERIODO
                        ,BP_D_NOVEDADPROYECTO.VALORAPROBADO
                        ,BP_D_NOVEDADPROYECTO.VALORSOLICITADO
                        ,BP_D_NOVEDADPROYECTO.CANTIDAD
                        ,BP_D_NOVEDADPROYECTO.CODIGO
                        ,NVL(BP_D_NOVEDADPROYECTO.valoraprobado,0) - NVL(BP_D_NOVEDADPROYECTO.VALORPROGRAMADO,0) AS DIFERENCIA
                        ,BP_D_NOVEDADPROYECTO.VALORDISMINUIDO
                        ,BPNOVEDADPROYECTO.CONCOMPROBANTEPPTAL 
                FROM  BPNOVEDADPROYECTO
                  INNER JOIN BP_D_NOVEDADPROYECTO
                          ON BPNOVEDADPROYECTO.CODIGO      = BP_D_NOVEDADPROYECTO.NOVEDAD
                         AND BPNOVEDADPROYECTO.CLASET      = BP_D_NOVEDADPROYECTO.CLASET
                         AND BPNOVEDADPROYECTO.TIPOT        = BP_D_NOVEDADPROYECTO.TIPOT
                         AND BPNOVEDADPROYECTO.COMPANIA    = BP_D_NOVEDADPROYECTO.COMPANIA
                         AND BPNOVEDADPROYECTO.DEPENDENCIA = BP_D_NOVEDADPROYECTO.DEPENDENCIA
                    INNER JOIN PROYECTOS
                            ON BP_D_NOVEDADPROYECTO.COMPANIA = PROYECTOS.COMPANIA
                           AND BP_D_NOVEDADPROYECTO.PROYECTO = PROYECTOS.CODIGO
                  INNER JOIN COMPONENTES_ACTIVIDADES
                          ON BP_D_NOVEDADPROYECTO.COMPANIA = COMPONENTES_ACTIVIDADES.COMPANIA
                          AND BP_D_NOVEDADPROYECTO.PROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                          AND BP_D_NOVEDADPROYECTO.COMPONENTE  = COMPONENTES_ACTIVIDADES.COMPONENTE
                          AND BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
                          AND BP_D_NOVEDADPROYECTO.ACTIVIDAD = COMPONENTES_ACTIVIDADES.ACTIVIDAD
                WHERE   BPNOVEDADPROYECTO.COMPANIA    IN(''' || UN_COMPANIA || ''')
                  AND   BPNOVEDADPROYECTO.TIPOT       IN(''' || UN_TIPO || ''')
                  AND   BPNOVEDADPROYECTO.CLASET      IN('''  || UN_CLASE || ''')
                  AND   BPNOVEDADPROYECTO.CODIGO      IN(''' || UN_NOVEDAD || ''')
                  AND   BPNOVEDADPROYECTO.DEPENDENCIA IN(''' || UN_DEPENDENCIA || ''')
                  AND   BPNOVEDADPROYECTO.ESTADO      IN(''V'',''AP'')';

                   --AND   BPNOVEDADPROYECTO.VIGENCIA    IN(''' || UN_VIGENCIA || ''')

  --
  IF UN_PROYECTO IS NOT NULL THEN
    MI_STRSQL :=  MI_STRSQL || ' AND BP_D_NOVEDADPROYECTO.PROYECTO IN(' || UN_PROYECTO|| ')';
  END IF;
  --
    MI_STRSQL :=  MI_STRSQL || ' AND NVL(BP_D_NOVEDADPROYECTO.CONPROGRAMACION,0) IN(0)';
  --
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,'''; --/!\
  --
  OPEN RS FOR MI_STRSQL;
  LOOP
  FETCH RS INTO RSPROYECTO, RSCOMPONENTE, RSTIPOCOMPONENTE, RSACTIVIDAD, RSVIGENCIA,
  RSFECHA, RSCONPROGRAMACION, RSCONPROGRAMACION_P, RSCONPROGRAMACION_C, RSPERIOCIDAD, RSVALORTOTAL,
  RSPERIODO, RSVALORAPROBADO, RSVALORSOLICITADO, RSCANTIDAD, RSCODIGO, RSDIFERENCIA, RSVALORDISMINUIDO, RSCONCOMPROBANTEPPTAL ;
  EXIT WHEN RS%NOTFOUND;
    IF MI_TIPO = 'E' OR MI_TIPO = 'M' THEN
      MI_ETAPA := '03 Revisando Programación';
      -- 3. Revisando programaciÃ³n
      IF (RSPROYECTO IS NULL) OR (RSCOMPONENTE IS NULL) OR (RSTIPOCOMPONENTE IS NULL) OR (RSACTIVIDAD IS NULL) THEN
        PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA => UN_COMPANIA
                                                        ,UN_TIPO => UN_TIPO
                                                        ,UN_CLASE => UN_CLASE
                                                        ,UN_NOVEDAD => UN_NOVEDAD
                                                        ,UN_DEPENDENCIA => UN_DEPENDENCIA
                                                        ,UN_RSCODIGO => RSCODIGO
                                                        ,UN_RSPROYECTO => RSPROYECTO
                                                        ,UN_RSCOMPONENTE => RSCOMPONENTE
                                                        ,UN_RSTIPOCOMPONENTE => RSTIPOCOMPONENTE
                                                        ,UN_RSACTIVIDAD => RSACTIVIDAD
                                                        ,UN_CANTIDAD => 0
                                                        ,UN_INCONSISTENCIA => 'CON NOVEDAD'
                                                        ,UN_RSVIGENCIA => RSVIGENCIA
                                                        ,UN_ESTADO => NULL
                                                        ,UN_USUARIO => UN_USUARIO
                                                        );

        MI_RTA := 1;
        CONTINUE;
      END IF;
      --
      IF TO_NUMBER(NVL(RSPERIODO,1)) = 0 THEN
        GL_ANOMALIAS := GL_ANOMALIAS || 'El periodo (' || NVL(RSPERIODO, 1) || ') registrado para la novedad ' || UN_TIPO || '-' || UN_NOVEDAD || CHR(13) || CHR(10);
        CONTINUE;
      END IF;
      -- Guarda el valor a donde se debe actualizar
      MI_COL_VALOR := 'VALOR' || NVL(RSPERIODO,1);
      MI_COL_PCT   := 'PORCENTAJE' || NVL(RSPERIODO,1);

      IF NVL(RSVALORTOTAL,0) = 0 THEN
        MI_PORCENTAJE := 0;
      ELSE
        MI_PORCENTAJE := NVL(RSVALORAPROBADO,0)/RSVALORTOTAL;
        MI_PORCENTAJE := ROUND(MI_PORCENTAJE, 4);
      END IF;
      --
      MI_ETAPA := '04 Controlando Cantidad';
      -- 4. Controlando cantidad, que no se pase de lo controlado
      MI_CANTIDAD := PCK_BANCOS_PROY2.FC_CANT_DISPONIBLE(UN_COMPANIA        => UN_COMPANIA
                                                        ,UN_ESTADO          => MI_TIPO
                                                        ,UN_VIGENCIA        => RSVIGENCIA
                                                        ,UN_PROYECTO        => RSPROYECTO
                                                        ,UN_COMPONENTE      => RSCOMPONENTE
                                                        ,UN_TIPO_COMPONENTE => RSTIPOCOMPONENTE
                                                        ,UN_ACTIVIDAD       => RSACTIVIDAD
                                                        ,UN_VAL_NUEVO       => NULL
                                                        ,UN_VAL_VIEJO       => NULL);



      IF NVL(RSCANTIDAD,0) - NVL(MI_CANTIDAD,0) > 0 THEN
        GL_ANOMALIAS := GL_ANOMALIAS || 'En la vigencia: ' || RSVIGENCIA || ' Proyecto: ' || RSPROYECTO ||
                        ' Componente: ' || RSCOMPONENTE || ' Tipo Componente: ' || RSTIPOCOMPONENTE || ' Actividad: ' || RSACTIVIDAD ||
                        '  Lo ejecutado sobrepasa a lo programado en ' || TO_CHAR(NVL(RSCANTIDAD,0) - NVL(MI_CANTIDAD,0)) ||  CHR(13) || CHR(10);

      END IF;

      -- Consulta si existe un ejecutado para actualizar
      OPEN RS2 FOR
        SELECT  VALOR1
                , VALOR2
                , VALOR3
                , VALOR4
                , VALOR5
                , VALOR6
                , VALOR7
                , VALOR8
                , VALOR9
                , VALOR10
                , VALOR11
                , VALOR12
        FROM    PROGRAMACION
        WHERE   COMPANIA               IN('' || UN_COMPANIA || '')
          AND   CODIGOPROYECTO         IN('' || RSPROYECTO || '')
          AND   CODIGOCOMPONENTE       IN('' || RSCOMPONENTE || '')
          AND   TIPOCOMPONENTE         IN('' || RSTIPOCOMPONENTE || '')
          AND   CODIGOACTIVIDAD        IN('' || RSACTIVIDAD || '')
          AND   VIGENCIA               IN(UN_VIGENCIA)
          AND   TIPOESTADO             IN('E')
          AND   TIPOT_QUEAPRUEBA       IN('' || UN_TIPO || '')
          AND   CLASET_QUEAPRUEBA      IN('' || UN_CLASE || '')
          AND   CODIGO_QUEAPRUEBA      IN('' || UN_NOVEDAD || '')
          AND   DEPENDENCIA_QUEAPRUEBA IN('' || UN_DEPENDENCIA || '')
          AND   CODIGOITEM_QUEAPRUEBA  IN('' || RSCODIGO || '');

      FETCH RS2 INTO RS2VALOR1, RS2VALOR2, RS2VALOR3, RS2VALOR4, RS2VALOR5,
      RS2VALOR6, RS2VALOR7, RS2VALOR8, RS2VALOR9, RS2VALOR10, RS2VALOR11, RS2VALOR12;

      --si existe ejecutado por hacer MF

      IF RS2%FOUND THEN
        MI_ETAPA := '05 Actualizando programación';
        -- 5. Actualizando programaciÃ³n
        --MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2.MI_COL_VALOR,0); -- Recordset.Fields /!\
        IF NVL(RSPERIODO,1) = 1 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR1,0);
        ELSIF NVL(RSPERIODO,1) = 2 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR2,0);
        ELSIF NVL(RSPERIODO,1) = 3 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR3,0);
        ELSIF NVL(RSPERIODO,1) = 4 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR4,0);
        ELSIF NVL(RSPERIODO,1) = 5 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR5,0);
        ELSIF NVL(RSPERIODO,1) = 6 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR6,0);
        ELSIF NVL(RSPERIODO,1) = 7 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR7,0);
        ELSIF NVL(RSPERIODO,1) = 8 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR8,0);
        ELSIF NVL(RSPERIODO,1) = 9 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR9,0);
        ELSIF NVL(RSPERIODO,1) = 10 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR10,0);
        ELSIF NVL(RSPERIODO,1) = 11 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR11,0);
        ELSIF NVL(RSPERIODO,1) = 12 THEN
          MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0) - NVL(RS2VALOR12,0);
        END IF;
        --
      BEGIN
       BEGIN


       MI_CAMPOS := MI_COL_VALOR ||' = ' || NVL(RSVALORAPROBADO,0) || '
                        ,' || MI_COL_PCT || '='|| MI_PORCENTAJE || '
                        ,VALORTOTAL = '|| NVL(RSVALORAPROBADO,0) || '
                        ,CANTIDAD = '|| NVL(RSCANTIDAD,0)||'
                        ,DATE_MODIFIED = SYSDATE,  
                        MODIFIED_BY = '''||UN_USUARIO||'''';
       /*
        MI_CAMPOS    := MI_COL_VALOR || ',' || MI_COL_PCT ||',VALORTOTAL,CANTIDAD,DATE_MIDIFIED,MODIFIED_BY';

        MI_VALORES   := NVL(RSVALORAPROBADO,0) || '
                        ,' || MI_PORCENTAJE || '
                        ,' || NVL(RSVALORAPROBADO,0) || '
                        ,' || NVL(RSCANTIDAD,0)||'
                        ,SYSDATE,
                        '''||UN_USUARIO||'''';
                        */

        MI_CONDICION := '( PROGRAMACION.COMPANIA             IN(''' || UN_COMPANIA ||''')
                     AND PROGRAMACION.CODIGOPROYECTO         IN(''' || RSPROYECTO || ''')
                     AND PROGRAMACION.CODIGOCOMPONENTE       IN(''' || RSCOMPONENTE || ''')
                     AND PROGRAMACION.TIPOCOMPONENTE         IN(''' || RSTIPOCOMPONENTE || ''')
                     AND PROGRAMACION.CODIGOACTIVIDAD        IN(''' || RSACTIVIDAD || ''')
                     AND PROGRAMACION.VIGENCIA               IN(' || UN_VIGENCIA || ')
                     AND PROGRAMACION.TIPOESTADO             IN(''E'')
                     AND PROGRAMACION.TIPOT_QUEAPRUEBA       IN(''' || UN_TIPO || ''')
                     AND PROGRAMACION.CLASET_QUEAPRUEBA      IN(''' || UN_CLASE || ''')
                     AND PROGRAMACION.CODIGO_QUEAPRUEBA      IN(''' || UN_NOVEDAD || ''')
                     AND PROGRAMACION.DEPENDENCIA_QUEAPRUEBA IN(''' || UN_DEPENDENCIA || ''')
                     AND PROGRAMACION.CODIGOITEM_QUEAPRUEBA  IN(''' || RSCODIGO || '''))';


        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROGRAMACION'
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

       END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_EPROGR
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
      END;

      ELSE
      --SI ENSERTA PROGRAMACION SE LLENA GL MF
        MI_ETAPA := '06 Insertando programación';
        -- 6. Insertando programaciÃ³n
        -- Se guardan los valores por periodo
        -- Genera consecutivo para cargar en la tabla programaciÃ³n
        MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0);

        MI_CONDICION := ' PROGRAMACION.COMPANIA         IN(''' || UN_COMPANIA || ''')' ||
                    ' AND PROGRAMACION.CODIGOPROYECTO   IN(''' || RSPROYECTO || ''')' ||
                    ' AND PROGRAMACION.CODIGOCOMPONENTE IN(''' || RSCOMPONENTE || ''')' ||
                    ' AND PROGRAMACION.TIPOCOMPONENTE   IN(''' || RSTIPOCOMPONENTE || ''')' ||
                    ' AND PROGRAMACION.CODIGOACTIVIDAD  IN(''' || RSACTIVIDAD || ''')' ||
                    ' AND PROGRAMACION.VIGENCIA         IN(' || RSVIGENCIA || ')' ||
                    ' AND PROGRAMACION.TIPOESTADO       IN(''E'')';

        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'PROGRAMACION'
                                                          ,UN_CRITERIO => MI_CONDICION
                                                          ,UN_CAMPO    => 'CODIGO');
        --

     BEGIN
       BEGIN
        MI_CAMPOS := 'COMPANIA
                    , CODIGOPROYECTO
                    , CODIGOCOMPONENTE
                    , TIPOCOMPONENTE
                    , CODIGOACTIVIDAD
                    , VIGENCIA
                    , TIPOESTADO
                    , CODIGO
                    , FECHA
                    , PERIOCIDAD
                    , VALORTOTAL
                    , CANTIDAD
                    , TIPOT_QUEAPRUEBA
                    , CLASET_QUEAPRUEBA
                    , CODIGO_QUEAPRUEBA
                    , DEPENDENCIA_QUEAPRUEBA
                    , CODIGOITEM_QUEAPRUEBA
                    ,' || MI_COL_VALOR || '
                    ,' || MI_COL_PCT||'
                    ,DATE_CREATED
                    ,CREATED_BY';

        MI_VALORES := '''' || UN_COMPANIA || '''
                      ,''' || RSPROYECTO || '''
                      ,''' || RSCOMPONENTE || '''
                      ,''' || RSTIPOCOMPONENTE || '''
                      ,''' || RSACTIVIDAD || '''
                      ,' || RSVIGENCIA || '
                      , ''E''
                      , ''' || MI_CONSECUTIVO || '''
                      , SYSDATE 
                      ,'''|| RSPERIOCIDAD || '''
                      ,' || NVL(RSVALORAPROBADO,0) || '
                      ,' || NVL(RSCANTIDAD,0) ||'
                      ,''' || UN_TIPO || '''
                      ,'''|| UN_CLASE || '''
                      ,' || UN_NOVEDAD || '
                      ,''' || UN_DEPENDENCIA || '''
                      ,' || RSCODIGO || '
                      ,' || NVL(RSVALORAPROBADO,0) || '
                      ,' || MI_PORCENTAJE || '
                      ,SYSDATE
                      ,'''||UN_USUARIO||'''';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'PROGRAMACION'
                                              ,UN_ACCION  =>  'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

       END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_EPROGR
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
     END;
      END IF;
      CLOSE RS2;
      --
      IF PCK_DATOS.GL_RTA > 0 THEN
       BEGIN
        BEGIN
        MI_VAL_ACTUA := NVL(RSVALORAPROBADO,0);
        -- Se actualiza el valor ejecutado en el proyecto, componente y la actividad
        MI_ETAPA := '07 Actualizando Proyecto';
        -- 7. Actualizando Proyecto


        -- PROYECTOS
        ----------------------------------------------
        BEGIN
        MI_STRSQL := 'SELECT  NVL(PROYECTOS.VALOREJECUTADO,0)VALOREJECUTADO
                                        FROM    PROYECTOS
                                          INNER JOIN COMPONENTES
                                                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                                                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
                                            INNER JOIN COMPONENTES_ACTIVIDADES
                                                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                                                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                                                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                                                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
                                        WHERE   PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
                                        AND     PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
                                        AND     COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
                                        AND     COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE  || '''
                                        AND     COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
                                        AND     COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA ;

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_NUMERO := 0;
        END;
        -------------------------------------------------------

        MI_CAMPOS := 'VALOREJECUTADO 	= '||MI_NUMERO||',
                      CONPROGRAMACION = -1,
                      DATE_MODIFIED   = SYSDATE,
                      MODIFIED_BY     ='''||UN_USUARIO||'''';

        MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                           AND CODIGO   = ''' || RSPROYECTO || '''';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROYECTOS'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_EPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
        END;




        -- COMPONENTES-----------------------------
        BEGIN 
          MI_STRSQL := 'SELECT  SUM(NVL(COMPONENTES_ACTIVIDADES.VALOREJECUTADO,0)) VALOREJECUTADO 
              FROM    PROYECTOS
                INNER JOIN COMPONENTES
                        ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                       AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
                  INNER JOIN COMPONENTES_ACTIVIDADES
                          ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                         AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                         AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                         AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
              WHERE   PROYECTOS.COMPANIA                  = ''' || UN_COMPANIA || '''
                AND     PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
                AND     COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
                AND     COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE  || '''
                AND     COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA;

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_NUMERO := 0;
        END;

        BEGIN   

         MI_STRSQL := 'SELECT  NVL(COMPONENTES.SALDOCOMPONENTE,0)
                  FROM    PROYECTOS
                    INNER JOIN COMPONENTES
                            ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                           AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
                      INNER JOIN COMPONENTES_ACTIVIDADES
                              ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                             AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                             AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                             AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
                  WHERE   PROYECTOS.COMPANIA                  = ''' || UN_COMPANIA || '''
                    AND     PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
                    AND     COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
                    AND     COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE  || '''
                    AND     COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
                    AND     COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA ;


        EXECUTE IMMEDIATE MI_STRSQL INTO MI_SALDOCOMPONENTE;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_SALDOCOMPONENTE := 0;
        END;

         MI_CAMPOS := 'VALOREJECUTADO = '||MI_NUMERO||',
                      SALDOCOMPONENTE =   '||MI_SALDOCOMPONENTE || ',
                      CONPROGRAMACION = -1,
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY   = '''||UN_USUARIO||'''';

        MI_CONDICION := ' COMPANIA       = ''' || UN_COMPANIA || '''
                           AND CODIGOPROYECTO = ''' || RSPROYECTO || '''
                           AND CODIGO         = ''' || RSCOMPONENTE || '''
                           AND TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE  || '''';
        BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                          ,UN_ACCION    => 'M'
                           ,UN_CAMPOS    => MI_CAMPOS
                           ,UN_CONDICION => MI_CONDICION);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
       END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMP
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
       END;

  ----------------------- COMPONENTES_ACTIVIDADES----------------------------

        BEGIN 
          MI_STRSQL := 'SELECT  NVL(COMPONENTES_ACTIVIDADES.VALOREJECUTADO,0)
          FROM    PROYECTOS
            INNER JOIN COMPONENTES
                    ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                   AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
               INNER JOIN COMPONENTES_ACTIVIDADES
                       ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                      AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                      AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                      AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
          WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
            AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
            AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
            AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE  || '''
            AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
            AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA ;

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_NUMERO := 0;
        END;

        BEGIN   

         MI_STRSQL := 'SELECT  COMPONENTES_ACTIVIDADES.CANTIDAD_EJE
          FROM    PROYECTOS
            INNER JOIN COMPONENTES
                    ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                   AND PROYECTOS.CODIGO = COMPONENTES.CODIGOPROYECTO
              INNER JOIN COMPONENTES_ACTIVIDADES
                      ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                     AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                     AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                     AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
          WHERE PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
            AND PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
            AND COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
            AND COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE  || '''
            AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
            AND COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA ;


        EXECUTE IMMEDIATE MI_STRSQL INTO MI_SALDOCOMPONENTE;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_SALDOCOMPONENTE := 0;
        END;

         MI_CAMPOS := 'VALOREJECUTADO = '||MI_NUMERO||',
                      CONPROGRAMACION = -1,
                      DATE_MODIFIED   = SYSDATE,
                      MODIFIED_BY     = '''||UN_USUARIO||'''';
--PENDIENTE CANTIDAD_EJE
        MI_CONDICION := '  COMPANIA           = ''' || UN_COMPANIA || '''
                           AND CODIGOPROYECTO = ''' || RSPROYECTO || '''
                           AND COMPONENTE         = ''' || RSCOMPONENTE || '''
                           AND TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE  || '''';
        BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES_ACTIVIDADES'
                          ,UN_ACCION    => 'M'
                           ,UN_CAMPOS    => MI_CAMPOS
                           ,UN_CONDICION => MI_CONDICION);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
       END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMP
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
       END;







        --
        IF PCK_DATOS.GL_RTA < 1 THEN
          PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA          => UN_COMPANIA
                                                          ,UN_TIPO             => UN_TIPO
                                                          ,UN_CLASE            => UN_CLASE
                                                          ,UN_NOVEDAD          => UN_NOVEDAD
                                                          ,UN_DEPENDENCIA      => UN_DEPENDENCIA
                                                          ,UN_RSCODIGO         => RSCODIGO
                                                          ,UN_RSPROYECTO       => RSPROYECTO
                                                          ,UN_RSCOMPONENTE     => RSCOMPONENTE
                                                          ,UN_RSTIPOCOMPONENTE => RSTIPOCOMPONENTE
                                                          ,UN_RSACTIVIDAD      => RSACTIVIDAD
                                                          ,UN_CANTIDAD         => 0
                                                          ,UN_INCONSISTENCIA   => 'NOVEDAD ACTUALIZADA'
                                                          ,UN_RSVIGENCIA       => RSVIGENCIA
                                                          ,UN_ESTADO           => NULL
                                                          ,UN_USUARIO          => UN_USUARIO);
        ELSE
          -- Se coloca el indicador de con programaciÃ³n en el detalle de la novedad
          IF UN_ACT_PLANIND <> 0 THEN
            PCK_DATOS.GL_RTA := PCK_BANCOS_PROY5.FC_ACT_PLAN_INDICADOR(UN_COMPANIA         => UN_COMPANIA
                                                                       ,UN_MODULO          => PCK_DATOS.MODULOBANCOPROY
                                                                       ,UN_NOVEDAD_INICIAL => UN_NOVEDAD
                                                                       ,UN_NOVEDAD_FINAL   => UN_NOVEDAD
                                                                       ,UN_TIPO            => UN_TIPO
                                                                       ,UN_USUARIO         => UN_USUARIO);
          END IF;
          --
          PCK_DATOS.GL_RTA := FC_ACT_INDICADOR_NOVEDAD (UN_COMPANIA     => UN_COMPANIA
                                                        ,UN_TIPO        => UN_TIPO
                                                        ,UN_CLASE       => UN_CLASE
                                                        ,UN_NOVEDAD     => UN_NOVEDAD
                                                        ,UN_DEPENDENCIA => UN_DEPENDENCIA
                                                        ,UN_RSCODIGO    => RSCODIGO
                                                        ,UN_RSPROYECTO  => RSPROYECTO
                                                        ,UN_USUARIO     => UN_USUARIO);

        END IF;
      END IF;




    ELSIF MI_TIPO = 'S' THEN
      -- Para actualizar Ãºnicamente el valor solicitado. Se actualiza el valor ejecutado en el proyecto, componente y la actividad.
      MI_ETAPA := '08 Actualizando Proyecto';
      -- 8. Actualizando Proyecto
      IF (RSPROYECTO IS NULL) OR (RSCOMPONENTE IS NULL) OR (RSTIPOCOMPONENTE IS NULL) OR (RSACTIVIDAD IS NULL) THEN
        PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA          => UN_COMPANIA
                                                        ,UN_TIPO             => UN_TIPO
                                                        ,UN_CLASE            => UN_CLASE
                                                        ,UN_NOVEDAD          => UN_NOVEDAD
                                                        ,UN_DEPENDENCIA      => UN_DEPENDENCIA
                                                        ,UN_RSCODIGO         => RSCODIGO
                                                        ,UN_RSPROYECTO       => RSPROYECTO
                                                        ,UN_RSCOMPONENTE     => RSCOMPONENTE
                                                        ,UN_RSTIPOCOMPONENTE => RSTIPOCOMPONENTE
                                                        ,UN_RSACTIVIDAD      => RSACTIVIDAD
                                                        ,UN_CANTIDAD         => 0
                                                        ,UN_INCONSISTENCIA   =>'CON NOVEDAD'
                                                        ,UN_RSVIGENCIA       => RSVIGENCIA
                                                        ,UN_ESTADO           => NULL
                                                        ,UN_USUARIO          => UN_USUARIO);
        MI_RTA := 1;
        CONTINUE;
      END IF;
      --
      MI_VAL_ACTUA := NVL(RSVALORSOLICITADO,0);
      MI_VALAPROB  := NVL(RSVALORAPROBADO,0);

      --
      BEGIN
        SELECT  NVL(PROYECTOS.VALORSOLICITADO,0)
        INTO    MI_CANTIDAD
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE   PROYECTOS.COMPANIA                = UN_COMPANIA
        AND     PROYECTOS.CODIGO                  = RSPROYECTO
        AND     COMPONENTES.CODIGO                = RSCOMPONENTE
        AND     COMPONENTES.TIPOCOMPONENTE        = RSTIPOCOMPONENTE
        AND     COMPONENTES_ACTIVIDADES.ACTIVIDAD = RSACTIVIDAD
        AND     COMPONENTES_ACTIVIDADES.VIGENCIA  = RSVIGENCIA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA          => UN_COMPANIA
                                                        ,UN_TIPO             => UN_TIPO
                                                        ,UN_CLASE            => UN_CLASE
                                                        ,UN_NOVEDAD          => UN_NOVEDAD
                                                        ,UN_DEPENDENCIA      => UN_DEPENDENCIA
                                                        ,UN_RSCODIGO         => RSCODIGO
                                                        ,UN_RSPROYECTO       => RSPROYECTO
                                                        ,UN_RSCOMPONENTE     => RSCOMPONENTE
                                                        ,UN_RSTIPOCOMPONENTE => RSTIPOCOMPONENTE
                                                        ,UN_RSACTIVIDAD      => RSACTIVIDAD
                                                        ,UN_CANTIDAD         => 0
                                                        ,UN_INCONSISTENCIA   => 'NOVEDAD NO ACTUALIZADA'
                                                        ,UN_RSVIGENCIA       => RSVIGENCIA
                                                        ,UN_ESTADO           => NULL
                                                        ,UN_USUARIO          => UN_USUARIO);
        MI_RTA := 1;
        CONTINUE;

      END;

      ------ACTUALIZACION VALOR EJECUTADO PROYECTOS----------

      IF NVL(RSCONCOMPROBANTEPPTAL, 0) <> 0 THEN


      BEGIN 
      BEGIN 

      MI_STRSQL := 'VALOREJECUTADO = ( 
      SELECT  NVL(PROYECTOS.VALOREJECUTADO,0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || ' 
          ) + ' || MI_VALAPROB || ',
          DATE_MODIFIED   = SYSDATE,
          MODIFIED_BY     = '''||UN_USUARIO||'''';


      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                   AND CODIGO   = ''' || RSPROYECTO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROYECTOS'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    =>  MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_EPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;


    COMMIT; 

       -- ACTUALIZACION VALOR EJECUTADO COMPONENTES ----- 
    BEGIN
     BEGIN

      MI_STRSQL := 'VALOREJECUTADO =
      (
       SELECT  SUM(NVL(COMPONENTES_ACTIVIDADES.VALOREJECUTADO,0))
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || ' 
          ) + ' || MI_VALAPROB || ',
          DATE_MODIFIED   = SYSDATE,
          MODIFIED_BY     = '''||UN_USUARIO||'''';

      MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                  AND  CODIGOPROYECTO = ''' || RSPROYECTO || '''
                  AND  CODIGO         = ''' || RSCOMPONENTE || '''
                  AND  TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMP
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;
COMMIT; 

       -- ACTUALIZACION VALOR EJECUTADO COMPONENTES_ACTIVIDADES ----- 


       BEGIN
     BEGIN
      MI_STRSQL := 'VALOREJECUTADO =
      (
        SELECT  NVL(COMPONENTES_ACTIVIDADES.VALOREJECUTADO,0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || ' 
          ) + ' || MI_VALAPROB || ',
          DATE_MODIFIED   = SYSDATE,
          MODIFIED_BY     = '''||UN_USUARIO||'''';

      MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA || '''
                  AND CODIGOPROYECTO = ''' || RSPROYECTO || '''
                  AND COMPONENTE     = ''' || RSCOMPONENTE || '''
                  AND TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE || '''
                  AND ACTIVIDAD      = ''' || RSACTIVIDAD || '''
                  AND VIGENCIA       = ' || RSVIGENCIA;

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES_ACTIVIDADES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMPA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;
    COMMIT; 

    END IF;


      -- PROYECTOS

    BEGIN
     BEGIN
      MI_STRSQL := 'VALORSOLICITADO =
      (
        SELECT  NVL(PROYECTOS.VALORSOLICITADO,0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      ) + ' || MI_VAL_ACTUA || ',
      VALOR_DISMINUIDO = (
        SELECT  NVL(PROYECTOS.VALOR_DISMINUIDO, 0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE  PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND  PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND  COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND  COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      )  + '||RSVALORDISMINUIDO||',
      CONPROGRAMACION = -1,
      DATE_MODIFIED   = SYSDATE,
      MODIFIED_BY     = '''||UN_USUARIO||'''';

      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                   AND CODIGO   = ''' || RSPROYECTO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROYECTOS'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    =>  MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_EPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;


    COMMIT; 

      -- COMPONENTES
    BEGIN
     BEGIN

      MI_STRSQL := 'VALORTOTALSOLICITADO =
      (
        SELECT  SUM(NVL(COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD,0))
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      ) + ' || MI_VAL_ACTUA || ',
      VALOR_DISMINUIDO = (
        SELECT  SUM(NVL(COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO,0))
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      )  + NVL('||RSVALORDISMINUIDO||',0),
      CONPROGRAMACION = -1,
      DATE_MODIFIED   = SYSDATE,
      MODIFIED_BY     = '''||UN_USUARIO||'''';

      MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                  AND  CODIGOPROYECTO = ''' || RSPROYECTO || '''
                  AND  CODIGO         = ''' || RSCOMPONENTE || '''
                  AND  TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMP
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;
COMMIT; 
      -- COMPONENTES_ACTIVIDADES

    BEGIN
     BEGIN
      MI_STRSQL := 'VALOR_SOLICITADO_ACTIVIDAD =
      (
        SELECT  NVL(COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD,0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      ) + ' || MI_VAL_ACTUA || ',
      VALOR_DISMINUIDO = (
        SELECT  NVL(COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO,0)
        FROM    PROYECTOS
          INNER JOIN COMPONENTES
                  ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                 AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
            INNER JOIN COMPONENTES_ACTIVIDADES
                    ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                   AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                   AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                   AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
        WHERE PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
          AND PROYECTOS.CODIGO                  = ''' || RSPROYECTO || '''
          AND COMPONENTES.CODIGO                = ''' || RSCOMPONENTE || '''
          AND COMPONENTES.TIPOCOMPONENTE        = ''' || RSTIPOCOMPONENTE || '''
          AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RSACTIVIDAD || '''
          AND COMPONENTES_ACTIVIDADES.VIGENCIA  = ' || RSVIGENCIA || '
      )  + NVL('||RSVALORDISMINUIDO||',0),
      DATE_MODIFIED = SYSDATE,
      MODIFIED_BY   = '''||UN_USUARIO||'''';

      MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA || '''
                  AND CODIGOPROYECTO = ''' || RSPROYECTO || '''
                  AND COMPONENTE     = ''' || RSCOMPONENTE || '''
                  AND TIPOCOMPONENTE = ''' || RSTIPOCOMPONENTE || '''
                  AND ACTIVIDAD      = ''' || RSACTIVIDAD || '''
                  AND VIGENCIA       = ' || RSVIGENCIA;

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES_ACTIVIDADES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_STRSQL
                                            ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'MI_ETAPA';
                       MI_MSGERROR(1).VALOR := MI_ETAPA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_ECOMPA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );
    END;
    COMMIT; 
      --
      IF PCK_DATOS.GL_RTA < 1 THEN
        PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA          => UN_COMPANIA
                                                        ,UN_TIPO             => UN_TIPO
                                                        ,UN_CLASE            =>UN_CLASE
                                                        ,UN_NOVEDAD          => UN_NOVEDAD
                                                        ,UN_DEPENDENCIA      => UN_DEPENDENCIA
                                                        ,UN_RSCODIGO         => RSCODIGO
                                                        ,UN_RSPROYECTO       => RSPROYECTO
                                                        ,UN_RSCOMPONENTE     => RSCOMPONENTE
                                                        ,UN_RSTIPOCOMPONENTE => RSTIPOCOMPONENTE
                                                        ,UN_RSACTIVIDAD      => RSACTIVIDAD
                                                        ,UN_CANTIDAD         => 0
                                                        ,UN_INCONSISTENCIA   =>'NOVEDAD NO ACTUALIZADA'
                                                        ,UN_RSVIGENCIA       => RSVIGENCIA
                                                        ,UN_ESTADO           => NULL
                                                        ,UN_USUARIO          => UN_USUARIO);
      ELSE
        IF UN_ACT_PLANIND <> 0 THEN
          PCK_DATOS.GL_RTA := PCK_BANCOS_PROY5.FC_ACT_PLAN_INDICADOR (UN_COMPANIA         => UN_COMPANIA
                                                                      ,UN_MODULO          => PCK_DATOS.MODULOBANCOPROY
                                                                      ,UN_NOVEDAD_INICIAL => UN_NOVEDAD
                                                                      ,UN_NOVEDAD_FINAL   => UN_NOVEDAD
                                                                      ,UN_TIPO            => UN_TIPO
                                                                      ,UN_USUARIO         => UN_USUARIO);
        END IF;
        --
        /*
        PCK_DATOS.GL_RTA := FC_ACT_INDICADOR_NOVEDAD (UN_COMPANIA     => UN_COMPANIA
                                                      ,UN_TIPO        => UN_TIPO
                                                      ,UN_CLASE       => UN_CLASE
                                                      ,UN_NOVEDAD     => UN_NOVEDAD
                                                      ,UN_DEPENDENCIA => UN_DEPENDENCIA
                                                      ,UN_RSCODIGO    => RSCODIGO
                                                      ,UN_RSPROYECTO  => RSPROYECTO
                                                      ,UN_USUARIO     => UN_USUARIO);
                               */                       
       COMMIT;                                               
      END IF;
    END IF;
  END LOOP; -- RS
  CLOSE RS;
  --
  MI_RTA := 1;
  RETURN MI_RTA;
END FC_ACT_PROGRAMACION;




--
-- 5
-- Función Act_Programado
FUNCTION FC_ACT_PROGRAMADO
/*
   NAME 			      : FC_ACT_PROGRAMADO
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE MIGRADOR	  : 05/09/2015
   TIME				      : 11:00 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Actualiza programado y saldo del proyecto
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 12:50 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
   @Name: actualizarProgramado
*/
(
	UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA, -- CÓdigo de la compañía
	UN_PROYECTO_INICIAL IN VARCHAR2,                 -- Proyecto inicial
	UN_PROYECTO_FINAL 	IN VARCHAR2,
  UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA                PCK_SUBTIPOS.TI_ENTERO :=  0;
  MI_STRSQL       		  PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO;
BEGIN

  --
  FOR RS IN (
    SELECT COMPANIA
           , CODIGO
           , CODIGOPROYECTO
           , CODIGOCOMPONENTE
           , TIPOCOMPONENTE
           , CODIGOACTIVIDAD
           , SUM(VALORTOTAL) AS TOTAL
           , TIPOT_QUEAPRUEBA
           , CLASET_QUEAPRUEBA
           , CODIGO_QUEAPRUEBA
           , DEPENDENCIA_QUEAPRUEBA
           ,VIGENCIA
           , TIPOESTADO
    FROM   PROGRAMACION
    WHERE COMPANIA       = UN_COMPANIA
      AND TIPOESTADO     IN('P','RP')
      AND CODIGOPROYECTO BETWEEN UN_PROYECTO_INICIAL AND UN_PROYECTO_FINAL
    GROUP BY COMPANIA
             , CODIGO
             , CODIGOPROYECTO
             , CODIGOCOMPONENTE
             , TIPOCOMPONENTE
             , CODIGOACTIVIDAD
             , TIPOT_QUEAPRUEBA
             , CLASET_QUEAPRUEBA
             , CODIGO_QUEAPRUEBA
             , DEPENDENCIA_QUEAPRUEBA
             , VIGENCIA
             , TIPOESTADO
    ORDER BY COMPANIA
             , CODIGOPROYECTO
             , CODIGOCOMPONENTE
             , TIPOCOMPONENTE
  )
  LOOP
   BEGIN
    BEGIN

    -- PROYECTOS
    MI_STRSQL := 'VALORPROGRAMADO =
    (
      SELECT  PROYECTOS.VALORPROGRAMADO
      FROM 	  PROYECTOS
        INNER JOIN COMPONENTES
                ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
               AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
           INNER JOIN COMPONENTES_ACTIVIDADES
                   ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                  AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                  AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                  AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
      WHERE	 PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
        AND  PROYECTOS.CODIGO                  = ''' || RS.CODIGOPROYECTO || '''
        AND  COMPONENTES.CODIGO                = ''' || RS.CODIGOCOMPONENTE || '''
        AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RS.TIPOCOMPONENTE || '''
        AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RS.CODIGOACTIVIDAD || '''
    ) + ' || RS.TOTAL;

    MI_CONDICION := 'PROYECTOS.COMPANIA = ''' || UN_COMPANIA || '''
                 AND PROYECTOS.CODIGO   = ''' || RS.CODIGOPROYECTO || '''';

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROYECTOS'
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_STRSQL
                                           ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_PROYECTOS
                                        );
   END;

   COMMIT;
    -- COMPONENTES

   BEGIN
    BEGIN
       MI_STRSQL := 'VALORPROGRAMADO = (
        (
          SELECT SUM(NVL(COMPONENTES_ACTIVIDADES.VALORPROGRAMADO,0)) VALORPROGRAMADO
          FROM 	 PROYECTOS
           INNER JOIN COMPONENTES
                   ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                  AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
             INNER JOIN COMPONENTES_ACTIVIDADES
                     ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                    AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                    AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                    AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
          WHERE	 PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
            AND  PROYECTOS.CODIGO                  = ''' || RS.CODIGOPROYECTO || '''
            AND  COMPONENTES.CODIGO                = ''' || RS.CODIGOCOMPONENTE || '''
            AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RS.TIPOCOMPONENTE || '''
        ) + ' || RS.TOTAL || ') ,
        SALDOCOMPONENTE = (
        (
          (
            SELECT COMPONENTES.VALORPROGRAMADO
            FROM 	 PROYECTOS
              INNER JOIN COMPONENTES
                      ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                     AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
                INNER JOIN COMPONENTES_ACTIVIDADES
                        ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                       AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                       AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                       AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
            WHERE	 PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
              AND  PROYECTOS.CODIGO                  = ''' || RS.CODIGOPROYECTO || '''
              AND  COMPONENTES.CODIGO                = ''' || RS.CODIGOCOMPONENTE || '''
              AND  COMPONENTES.TIPOCOMPONENTE        = ''' || RS.TIPOCOMPONENTE || '''
              AND  COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RS.CODIGOACTIVIDAD || '''
          ) + ' || RS.TOTAL || '
        )
        -
        (
          SELECT 	COMPONENTES.VALOREJECUTADO
          FROM 	PROYECTOS
            INNER JOIN COMPONENTES
                    ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
                   AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
              INNER JOIN COMPONENTES_ACTIVIDADES
                      ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                     AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                     AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                     AND COMPONENTES.TIPOCOMPONENTE = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
          WHERE	PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
            AND PROYECTOS.CODIGO                  = ''' || RS.CODIGOPROYECTO || '''
            AND COMPONENTES.CODIGO                = ''' || RS.CODIGOCOMPONENTE || '''
            AND COMPONENTES.TIPOCOMPONENTE        = ''' || RS.TIPOCOMPONENTE || '''
            AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RS.CODIGOACTIVIDAD || '''
        ))';

        MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                     AND CODIGOPROYECTO = ''' || RS.CODIGOPROYECTO || '''
                     AND CODIGO         = ''' || RS.CODIGOCOMPONENTE || '''
                     AND TIPOCOMPONENTE = ''' || RS.TIPOCOMPONENTE || '''';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                                              ,UN_ACCION    =>  'M'
                                              ,UN_CAMPOS    => MI_STRSQL
                                              ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPONENTES
                                        );
   END;
   COMMIT;

   BEGIN

    BEGIN
    -- COMPONENTES_ACTIVIDADES
    MI_STRSQL := 'VALORPROGRAMADO = (
    (
      SELECT COMPONENTES_ACTIVIDADES.VALORPROGRAMADO
      FROM   PROYECTOS
        INNER JOIN COMPONENTES
                ON PROYECTOS.COMPANIA = COMPONENTES.COMPANIA
               AND PROYECTOS.CODIGO   = COMPONENTES.CODIGOPROYECTO
          INNER JOIN COMPONENTES_ACTIVIDADES
                  ON COMPONENTES.COMPANIA       = COMPONENTES_ACTIVIDADES.COMPANIA
                 AND COMPONENTES.CODIGOPROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                 AND COMPONENTES.CODIGO         = COMPONENTES_ACTIVIDADES.COMPONENTE
                AND COMPONENTES.TIPOCOMPONENTE  = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
      WHERE	PROYECTOS.COMPANIA                = ''' || UN_COMPANIA || '''
        AND PROYECTOS.CODIGO                  = ''' || RS.CODIGOPROYECTO || '''
        AND COMPONENTES.CODIGO                = ''' || RS.CODIGOCOMPONENTE || '''
        AND COMPONENTES.TIPOCOMPONENTE        = ''' || RS.TIPOCOMPONENTE || '''
        AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = ''' || RS.CODIGOACTIVIDAD || '''
    ) + ' || RS.TOTAL || ') ';

    MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                 AND CODIGOPROYECTO = ''' || RS.CODIGOPROYECTO || '''
                 AND COMPONENTE     = ''' || RS.CODIGOCOMPONENTE || '''
                 AND TIPOCOMPONENTE = ''' || RS.TIPOCOMPONENTE || '''
                 AND ACTIVIDAD      = ''' || RS.CODIGOACTIVIDAD || '''';

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'COMPONENTES_ACTIVIDADES'
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_STRSQL
                                           ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPSACTV
                                        );
   END;

   COMMIT;

    --
    IF PCK_DATOS.GL_RTA < 1 THEN
      PCK_DATOS.GL_RTA := FC_REG_INCONSISTENCIA_NOVE (UN_COMPANIA          => UN_COMPANIA
                                                      ,UN_TIPO             => RS.TIPOT_QUEAPRUEBA
                                                      ,UN_CLASE            => RS.CLASET_QUEAPRUEBA
                                                      ,UN_NOVEDAD          => NVL(RS.CODIGO_QUEAPRUEBA,0)
                                                      ,UN_DEPENDENCIA      => RS.DEPENDENCIA_QUEAPRUEBA
                                                      ,UN_RSCODIGO         => RS.CODIGO
                                                      ,UN_RSPROYECTO       => NVL(RS.CODIGOPROYECTO,'NO EXISTE')
                                                      ,UN_RSCOMPONENTE     => NVL(RS.CODIGOCOMPONENTE,'NO EXISTE')
                                                      ,UN_RSTIPOCOMPONENTE => NVL(RS.TIPOCOMPONENTE,'NO EXISTE')
                                                      ,UN_RSACTIVIDAD      => NVL(RS.CODIGOACTIVIDAD,'NO EXISTE')
                                                      ,UN_CANTIDAD         => RS.TOTAL
                                                      ,UN_INCONSISTENCIA   => 'CON PROGRAMACION'
                                                      ,UN_RSVIGENCIA       => RS.VIGENCIA
                                                      ,UN_ESTADO           =>  CASE WHEN RS.TIPOESTADO = 'P' THEN 'PROGRAMADO' ELSE 'REPROGRAMADO' END
                                                      ,UN_USUARIO          => UN_USUARIO);
      MI_RTA := 1;
    END IF;
  END LOOP;
  --
  RETURN MI_RTA;
  --
END FC_ACT_PROGRAMADO;
--
-- 6
-- Función ANOMALIAS
FUNCTION FC_ANOMALIAS
/*
   NAME 			      : FC_ANOMALIAS
   AUTHORS 			    : SYSMAN  SAS / JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE         	  : 10/09/2015
   TIME				      : 12:03 AM
   DESCRIPTION		  : Devuelve una cadena con las inconsistencias encontradas
                      durante el proceso de mantenimiento.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 02:30 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación.
   @Name: No se migra Pues es una variable global que toca ver como la manejamos
*/
RETURN VARCHAR2
AS

BEGIN
  RETURN GL_ANOMALIAS;
END FC_ANOMALIAS;
--
-- 7
-- Función REG_INCONSISTENCIA_NOVE
FUNCTION FC_REG_INCONSISTENCIA_NOVE
/*
   NAME 			      : FC_REG_INCONSISTENCIA_NOVE
   AUTHORS 			    : SYSMAN  SAS / JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE         	  : 06/10/2015
   TIME				      : 05:03 AM
   DESCRIPTION		  : Registra una inconsistencia en las novedades encontradas
                      al actualizar la programaciÃ³n.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 02:40 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
   @Name: insertaInconsistenciaNovedad
*/
(
	UN_COMPANIA 	      IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_TIPO             IN VARCHAR2,
	UN_CLASE            IN VARCHAR2,
	UN_NOVEDAD          IN PCK_SUBTIPOS.TI_LONG,    -- Se cambia el tipo de TI_ENTERO a TI_LONG
	UN_DEPENDENCIA      IN VARCHAR2,
  UN_RSCODIGO				  IN BP_D_NOVEDADPROYECTO.CODIGO%TYPE,
  UN_RSPROYECTO			  IN BP_D_NOVEDADPROYECTO.PROYECTO%TYPE,
  UN_RSCOMPONENTE			IN BP_D_NOVEDADPROYECTO.COMPONENTE%TYPE,
  UN_RSTIPOCOMPONENTE	IN BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE%TYPE,
  UN_RSACTIVIDAD			IN BP_D_NOVEDADPROYECTO.ACTIVIDAD%TYPE,
  UN_CANTIDAD         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_INCONSISTENCIA	  IN VARCHAR2,
  UN_RSVIGENCIA			  IN BPNOVEDADPROYECTO.VIGENCIA%TYPE,
  UN_ESTADO           IN VARCHAR2,
  UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_CAMPOS				    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONSECUTIVO 		  PCK_SUBTIPOS.TI_ENTERO;
  MI_VALORES 			    PCK_SUBTIPOS.TI_VALORES;
BEGIN
 BEGIN
  BEGIN
  MI_CAMPOS := 'COMPANIA
                ,ID_CODIGO
                ,TIPOT
                ,CLASET
                ,NOVEDAD
                ,DEPENDENCIA
                ,CODIGO
                ,PROYECTO
                ,COMPONENTE
                ,TIPO_COMPONENTE
                ,ACTIVIDAD
                ,CANTIDAD
                ,PROBLEMA
                ,VIGENCIA
                ,TESTADO
                ,DATE_CREATED
                ,CREATED_BY';

  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'BP_INCONSISTENCIAS_NOVE'
                                                    ,UN_CRITERIO => NULL
                                                    ,UN_CAMPO    => 'ID_CODIGO');
  MI_VALORES := '''' || UN_COMPANIA || '''
                ,' || MI_CONSECUTIVO || '
                ,''' || UN_TIPO || '''
                ,''' || UN_CLASE || '''
                ,' || UN_NOVEDAD 	|| '
                ,'''|| UN_DEPENDENCIA || '''
                ,' || UN_RSCODIGO || '
                ,''' || NVL(UN_RSPROYECTO, 'NO EXISTE') || '''
                ,''' || NVL(UN_RSCOMPONENTE, 'NO EXISTE') 	|| '''
                ,'''|| NVL(UN_RSTIPOCOMPONENTE, 'NO EXISTE') || '''
                ,''' || NVL(UN_RSACTIVIDAD, 'NO EXISTE') || '''
                ,' || UN_CANTIDAD || '
                , ''' || UN_INCONSISTENCIA || '''
                ,' 	|| UN_RSVIGENCIA || '
                ,''' || UN_ESTADO || '''
                ,SYSDATE
                ,'''||UN_USUARIO||'''';

  RETURN PCK_DATOS.FC_ACME (UN_TABLA    => 'BP_INCONSISTENCIAS_NOVE'
                            ,UN_ACCION  => 'I'
                            ,UN_CAMPOS  => MI_CAMPOS
                            ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

  END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_BPINCONOVE
                                        );
 END;

END FC_REG_INCONSISTENCIA_NOVE;
--
-- 8
-- Función FC_ACT_INDICADOR_NOVEDAD
FUNCTION FC_ACT_INDICADOR_NOVEDAD
/*
   NAME 			      : FC_ACT_INDICADOR_NOVEDAD
   AUTHORS 			    : SYSMAN  SAS / JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE         	  : 07/10/2015
   TIME				      : 08:35 AM
   DESCRIPTION		  : Actualiza el indicador de CONPROGRAMACION en el detalle de la novedad.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 30/01/2017
   TIME				      : 03:00 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
   @Name: actualizaIndicadoresNovedad
*/
(
	UN_COMPANIA 	      IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_TIPO             IN VARCHAR2,
	UN_CLASE            IN VARCHAR2,
	UN_NOVEDAD          IN PCK_SUBTIPOS.TI_ENTERO,
	UN_DEPENDENCIA      IN VARCHAR2,
  UN_RSCODIGO				  IN BP_D_NOVEDADPROYECTO.CODIGO%TYPE,
  UN_RSPROYECTO			  IN BP_D_NOVEDADPROYECTO.PROYECTO%TYPE,
  UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_VALORES			    PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION 			  PCK_SUBTIPOS.TI_CONDICION;

BEGIN
 BEGIN
  BEGIN
  MI_VALORES := 'CONPROGRAMACION   = -1
                ,CONPROGRAMACION_C = -1
                ,CONPROGRAMACION_P = -1
                ,DATE_MODIFIED     = SYSDATE
                ,MODIFIED_BY       = '''||UN_USUARIO||'''';

  MI_CONDICION := 'COMPANIA   IN(''' || UN_COMPANIA || ''')
               AND TIPOT      IN(''' || UN_TIPO || ''')
               AND CLASET     IN(''' || UN_CLASE || ''')
               AND NOVEDAD    IN(' || UN_NOVEDAD || ')
              AND DEPENDENCIA IN(''' || UN_DEPENDENCIA || ''')
              AND CODIGO      IN(' || UN_RSCODIGO || ')
              AND PROYECTO    IN(''' || UN_RSPROYECTO || ''')';

  RETURN PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO'
                           ,UN_ACCION    =>'M'
                           ,UN_CAMPOS    => MI_VALORES
                           ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

  END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_BPDNOVPROY
                                        );
 END;

END FC_ACT_INDICADOR_NOVEDAD;

-- 9

FUNCTION FC_REVISAR_TOTALES
/*
   NAME 			      : FC_REVISAR_TOTALES
   AUTHORS 			    : SYSMAN  SAS / JUAN CARLOS RODRÃ¿GUEZ AMÃ?ZQUITA
   DATE         	  : 10/10/2015
   TIME				      : 08:30 AM
   DESCRIPTION		  : Revisa el valor de los componentes y actividades, y almacena
                      la informaciÃ³n con las alertas encontradas en tablas temporales.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA, ANA YESSICA SANA ROJAS
   DATE MODIFIED	  : 30/01/2017, 14/09/2017
   TIME				      : 03:20 PM, 08:00 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación y llamados por referencia de las funciones.
                      Se agregó manejo de excepciones se incluye validacion de método revisarTotalesProyecto() de controlador.
   @Name: actulizaTotalesProyecto


*/
(
	UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_PROYECTO_INICIAL IN VARCHAR2,
	UN_PROYECTO_FINAL 	IN VARCHAR2,
  UN_ACT_TOTAL        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO

)RETURN CLOB AS

    MI_CADENA           VARCHAR2(3200 CHAR);
    MI_ACTUALIZAR       BOOLEAN;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;    
    MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CADENA_COMP  CLOB;
    MI_CADENA_PROY  CLOB;
    MI_ARCCADENA    CLOB;
    MI_CODPROY      TEMP_TOTALES_COMPONENTES.CODIGOPROYECTO%TYPE;
    MI_CODIGO       TEMP_TOTALES_COMPONENTES.CODIGO%TYPE;
    MI_NOMCOMPON    TEMP_TOTALES_COMPONENTES.NOMBRECOMPONENTE%TYPE;
    MI_VIGENCIA     TEMP_TOTALES_COMPONENTES.VIGENCIA%TYPE;
    MI_VLRTOTAL     TEMP_TOTALES_COMPONENTES.VALORTOTAL%TYPE;
    MI_VLRACTIV     TEMP_TOTALES_COMPONENTES.VALOR_ACTIVIDADES%TYPE;
    MI_ALERTA       TEMP_TOTALES_COMPONENTES.ALERTA%TYPE;
    MI_SEPARADORFIL VARCHAR2(10);
    MI_SEPARADORCOL VARCHAR2(10);
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
    RS              SYS_REFCURSOR;
    MI_PCODIGO      TEMP_TOTALES_PROYECTOS.CODIGO%TYPE;
    MI_PVLRTOT      TEMP_TOTALES_PROYECTOS.VALORTOTAL%TYPE;
    MI_PVLRCOMP     TEMP_TOTALES_PROYECTOS.VALOR_COMPONENTES%TYPE;
    MI_PALERT       TEMP_TOTALES_PROYECTOS.ALERTA%TYPE;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;

BEGIN

        BEGIN
            BEGIN
                MI_TABLA    := 'TEMP_TOTALES_PROYECTOS';
                MI_CONDICION:= 'COMPANIA = ''' || UN_COMPANIA || '''';
                MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA =>  MI_TABLA,
                                              UN_ACCION => 'E',
                                              UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_DELET_TOT_PROY,
                                        UN_TABLAERROR => MI_TABLA);
        END;  
        BEGIN
            BEGIN
                MI_TABLA    := 'TEMP_TOTALES_COMPONENTES';
                MI_CONDICION:= 'COMPANIA = ''' || UN_COMPANIA || '''';
                MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA =>  MI_TABLA,
                                              UN_ACCION=> 'E',
                                              UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_DELET_TOT_COMP,
                                        UN_TABLAERROR => MI_TABLA);
        END;  
  -- Totales de proyectos
  BEGIN
   BEGIN
      MI_CAMPOS := 'COMPANIA,CODIGO
                   ,VALORTOTAL,VALOR_COMPONENTES
                   ,CODIGOBPIM';
      MI_STRSQL := 'SELECT   COMPANIA,
                CODIGO,
                VALORTOTAL,
                (
                  SELECT NVL(SUM(VALORTOTAL),0)
                  FROM  COMPONENTES
                  WHERE COMPANIA       = PROYECTOS.COMPANIA
                    AND CODIGOPROYECTO = PROYECTOS.CODIGO
                ) AS VALOR_COMPONENTES,
                CODIGOBPIM
      FROM      PROYECTOS
      WHERE     COMPANIA = ''' || UN_COMPANIA || '''
        AND     CODIGO   BETWEEN ' || UN_PROYECTO_INICIAL || ' AND ' || UN_PROYECTO_FINAL || ' ORDER BY  CODIGO';

        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_TOTALES_PROYECTOS'
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_STRSQL);
        COMMIT;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

   END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TEMTOTPROY
                                        );

  END;

  FOR RS IN (
    SELECT  CODIGO
            , VALORTOTAL
            , VALOR_COMPONENTES
    FROM    TEMP_TOTALES_PROYECTOS
    WHERE	  COMPANIA = UN_COMPANIA
  )
  LOOP
    IF RS.VALORTOTAL = 0 THEN
      MI_CADENA := 'No tenia valor Total.';
      MI_ACTUALIZAR := TRUE;
    ELSIF RS.VALORTOTAL < RS.VALOR_COMPONENTES THEN
      MI_CADENA := 'El valor de los componentes es superior al valor total del proyecto.';
      MI_ACTUALIZAR := TRUE;
     ELSIF RS.VALORTOTAL = RS.VALOR_COMPONENTES THEN
      MI_CADENA := 'El valor de los componentes es igual al valor total del proyecto.';
      MI_ACTUALIZAR := TRUE;
    END IF;
    --
    IF MI_ACTUALIZAR THEN

       BEGIN
        BEGIN
          MI_CAMPOS := 'ALERTA = ''' || MI_CADENA || '''';

          MI_STRSQL := 'COMPANIA = ''' || UN_COMPANIA || '''
                    AND CODIGO   = ''' || RS.CODIGO || '''';

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_TOTALES_PROYECTOS'
                                                ,UN_ACCION    =>  'M'
                                                ,UN_CAMPOS    => MI_CAMPOS
                                                ,UN_CONDICION => MI_STRSQL);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

        END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE
                                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TEMTOTPROY
                                            );
       END;

    END IF;
    --
    IF UN_ACT_TOTAL <> 0 THEN
     BEGIN
      BEGIN
      MI_CAMPOS := 'VALORTOTAL = ''' || RS.VALOR_COMPONENTES || ''',MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';

      MI_STRSQL := 'COMPANIA = ''' || UN_COMPANIA || '''
                AND CODIGO   = ''' || RS.CODIGO || '''';

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROYECTOS'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    =>  MI_CAMPOS
                                            ,UN_CONDICION =>  MI_STRSQL);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE
                                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_PROYECTOS
                                            );

     END;

    END IF;
    --
    MI_ACTUALIZAR := FALSE;
  END LOOP;

  BEGIN

   BEGIN
  -- Totales de componentes
        MI_CAMPOS := 'COMPANIA
                     ,CODIGOPROYECTO
                     ,CODIGO
                     ,TIPOCOMPONENTE
                     ,VIGENCIA
                     ,VALORTOTAL
                     ,NOMBRECOMPONENTE
                     ,VALOR_ACTIVIDADES';
        MI_STRSQL :=
        'SELECT   C.COMPANIA,
                  C.CODIGOPROYECTO,
                  C.CODIGO,
                  C.TIPOCOMPONENTE,
                  C.VIGENCIA,
                  C.VALORTOTAL,
                  C.NOMBRECOMPONENTE,
                  NVL(SUM(A.COSTOTOTAL),0) AS VALOR_ACTIVIDADES
        FROM      COMPONENTES C
                  LEFT JOIN COMPONENTES_ACTIVIDADES A
                  ON (C.COMPANIA=A.COMPANIA)
                  AND (C.CODIGOPROYECTO=A.CODIGOPROYECTO)
                  AND (C.CODIGO=A.COMPONENTE)
                  AND (C.TIPOCOMPONENTE=A.TIPOCOMPONENTE)
                  AND (C.VIGENCIA=A.VIGENCIA)
        WHERE     C.COMPANIA       = ' || UN_COMPANIA  || '
          AND     C.CODIGOPROYECTO BETWEEN ' || UN_PROYECTO_INICIAL ||' AND ' || UN_PROYECTO_FINAL || '
        GROUP BY  C.COMPANIA
                  , C.CODIGOPROYECTO
                  , C.CODIGO
                  , C.TIPOCOMPONENTE
                  , C.VIGENCIA
                  , C.VALORTOTAL
                  , C.NOMBRECOMPONENTE';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_TOTALES_COMPONENTES'
                                              ,UN_ACCION  => 'IS'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_STRSQL);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

   END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE
                                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TEMTOTCOMP
                                            );
  END;
  --
  FOR RS IN (
      SELECT  CODIGOPROYECTO
              ,CODIGO
              ,TIPOCOMPONENTE
              ,VIGENCIA
              ,VALORTOTAL
              ,NOMBRECOMPONENTE
              ,VALOR_ACTIVIDADES
      FROM    TEMP_TOTALES_COMPONENTES
      WHERE	  COMPANIA = UN_COMPANIA
  )
  LOOP
    IF RS.VALORTOTAL = 0 THEN
      MI_CADENA := 'No tenia valor total.';
      MI_ACTUALIZAR := TRUE;
    ELSIF RS.VALORTOTAL < RS.VALOR_ACTIVIDADES THEN
      MI_CADENA := 'El valor de las actividades es superior al valor total del componente.';
      MI_ACTUALIZAR := TRUE;
      ELSIF RS.VALORTOTAL = RS.VALOR_ACTIVIDADES THEN
      MI_CADENA := 'El valor de las actividades es i al valor total del componente.';
      MI_ACTUALIZAR := TRUE;

    END IF;
    --
    IF MI_ACTUALIZAR THEN
     BEGIN
      BEGIN
      MI_CAMPOS := 'ALERTA = ''' || MI_CADENA || '''';

      MI_STRSQL := 'COMPANIA       = ''' || UN_COMPANIA|| '''
                AND CODIGOPROYECTO = ''' || RS.CODIGOPROYECTO || '''
                AND CODIGO         = ''' || RS.CODIGO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_TOTALES_COMPONENTES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_STRSQL);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE
                                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TEMTOTCOMP
                                            );
     END;
    END IF;
    --
    IF UN_ACT_TOTAL <> 0 THEN
     BEGIN
      BEGIN
      MI_CAMPOS := 'VALORTOTAL = ''' || RS.VALOR_ACTIVIDADES || ''', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';

      MI_STRSQL := 'COMPANIA       = ''' || UN_COMPANIA|| '''
                AND CODIGOPROYECTO = ''' || RS.CODIGOPROYECTO || '''
                AND CODIGO         = ''' || RS.CODIGO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_STRSQL);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD     => SQLCODE
                                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_COMPONENTES
                                            );

     END;
    END IF;
    --
    MI_ACTUALIZAR := FALSE;
  END LOOP;
    MI_SEPARADORFIL := PCK_DATOS.GL_SEPARADOR_REG;
    MI_SEPARADORCOL := PCK_DATOS.GL_SEPARADOR_COL;

    BEGIN
        MI_STRSQL := 'SELECT CODIGOPROYECTO,
                             CODIGO,
                             NOMBRECOMPONENTE,
                             VIGENCIA,
                             VALORTOTAL,
                             VALOR_ACTIVIDADES,
                             ALERTA
                        FROM TEMP_TOTALES_COMPONENTES
                       WHERE COMPANIA = ''' || UN_COMPANIA || ''' 
                       AND ALERTA IS NOT NULL';
            OPEN RS FOR MI_STRSQL;
              <<COMPONENTES>>
              LOOP
                  FETCH RS INTO MI_CODPROY,
                                MI_CODIGO,
                                MI_NOMCOMPON,
                                MI_VIGENCIA,
                                MI_VLRTOTAL,
                                MI_VLRACTIV,
                                MI_ALERTA;
                  EXIT WHEN RS%NOTFOUND;
                  BEGIN 
                      MI_CADENA_COMP := MI_CADENA_COMP ||
                                        MI_SEPARADORCOL || MI_CODPROY   || 
                                        MI_SEPARADORFIL || MI_CODIGO    || 
                                        MI_SEPARADORFIL || MI_NOMCOMPON || 
                                        MI_SEPARADORFIL || MI_VIGENCIA  || 
                                        MI_SEPARADORFIL || MI_VLRTOTAL  || 
                                        MI_SEPARADORFIL || MI_VLRACTIV  || 
                                        MI_SEPARADORFIL || MI_ALERTA;
                  END;                             
            END LOOP COMPONENTES;

    END;
    BEGIN
        MI_STRSQL := 'SELECT CODIGO,
                             VALORTOTAL,
                             VALOR_COMPONENTES,
                             ALERTA
                        FROM TEMP_TOTALES_PROYECTOS
                       WHERE ALERTA IS NOT NULL
                         AND COMPANIA  = ''' || UN_COMPANIA || '''';
            OPEN RS FOR MI_STRSQL;
              <<PROYECTOS>>
              LOOP
                  FETCH RS INTO MI_PCODIGO,
                                MI_PVLRTOT,
                                MI_PVLRCOMP,
                                MI_PALERT;
                  EXIT WHEN RS%NOTFOUND;
                  BEGIN 
                      MI_CADENA_PROY := MI_CADENA_PROY ||
                                        MI_SEPARADORCOL || MI_PCODIGO  || 
                                        MI_SEPARADORFIL || MI_PVLRTOT  || 
                                        MI_SEPARADORFIL || MI_PVLRCOMP || 
                                        MI_SEPARADORFIL || MI_PALERT;
                  END;                             
            END LOOP PROYECTOS;

    END;
    IF MI_CADENA_COMP IS NULL AND MI_CADENA_PROY IS NULL THEN
        MI_ARCCADENA := NULL;
    ELSE
        MI_ARCCADENA := MI_CADENA_COMP || ',.PLA.,' || MI_CADENA_PROY ;
    END IF;
  RETURN MI_ARCCADENA;
  --
END FC_REVISAR_TOTALES;

--10

FUNCTION FC_VALIDARPROCESOS

/*
   NAME 			      : FC_VALIDARPROCESOS
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ANA YESSICA SANA ROJAS
   DATE MIGRADOR	  : 15/09/2017
   TIME				      : 10:00 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : Funcion que verifica segun el proceso que se selecciona en formulario controlador
   MODIFIER			    : 
   DATE MODIFIED	  : 
   TIME				      : 
   MODIFICATIONS	  : 
   @Name: validarProcesos
*/
( 
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO            IN  PCK_SUBTIPOS.TI_ENTERO,
    UN_PROYECTO_INICIAL   IN  PROYECTOS.CODIGO%TYPE,
    UN_PROYECTO_FINAL     IN  PROYECTOS.CODIGO%TYPE,
    UN_ACT_TOTAL          IN  PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_MODULO             IN  PCK_SUBTIPOS.TI_MODULO

) RETURN CLOB AS
    MI_PARAMETRO          PARAMETRO.VALOR%TYPE;
    MI_CADENA             CLOB;
    MI_CANTIDAD           PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;   
    MI_CONDICION          PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA                PCK_SUBTIPOS.TI_ENTERO;
    MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN

    IF UN_PROCESO = '1' THEN

        MI_CADENA := PCK_BANCOS_PROY2.FC_REVISAR_TOTALES(UN_COMPANIA 		    => UN_COMPANIA,
                                                         UN_PROYECTO_INICIAL=> UN_PROYECTO_INICIAL,
                                                         UN_PROYECTO_FINAL 	=> UN_PROYECTO_FINAL,
                                                         UN_ACT_TOTAL       => UN_ACT_TOTAL,
                                                         UN_USUARIO         => UN_USUARIO)   ;      
    ELSIF UN_PROCESO = '2' THEN
        MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA =>  UN_COMPANIA,
                                                UN_NOMBRE   =>  'PROGRAMAR DESDE LAS NOVEDADES',
                                                UN_MODULO   =>  UN_MODULO,
                                                UN_FECHA_PAR=>  SYSDATE);
        IF MI_PARAMETRO = 'SI' THEN
            MI_CADENA := PCK_BANCOS_PROY2.FC_MANTENIMIENTO_ACT_PROYECTO (UN_COMPANIA 		    =>  UN_COMPANIA,
                                                                         UN_PROYECTO_INICIAL => UN_PROYECTO_INICIAL,
                                                                         UN_PROYECTO_FINAL 	=>  UN_PROYECTO_FINAL,
                                                                         UN_USUARIO        	=>  UN_USUARIO);
        ELSE
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_PARAM_NOPROGRNOVE);
            END;
        END IF;    

    ELSIF UN_PROCESO = '3' THEN 
        BEGIN
            SELECT COUNT(1) INTO MI_CANTIDAD FROM BP_INCONSISTENCIAS_NOVE;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CANTIDAD := 0;
        END;  
        IF MI_CANTIDAD <> 0 THEN
            MI_CADENA := '1';
        ELSE
            MI_CADENA := '0';
        END IF;
    ELSIF UN_PROCESO = '4' THEN
        MI_RTA := 0;
        BEGIN
            BEGIN
                MI_TABLA      := 'BPNOVEDADPROYECTO';
                MI_CAMPOS     := 'CONCOMPROBANTEPPTAL = 0, MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE'; 
                MI_CONDICION  := 'COMPANIA = ''' || UN_COMPANIA || ''' AND CONCOMPROBANTEPPTAL IS NULL';
                MI_RTA        := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_ACTIND_COMPRPTAL,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        BEGIN
            BEGIN
                MI_TABLA      := 'BPNOVEDADPROYECTO';
                MI_MERGEUSING := 'SELECT DISTINCT BPNOVEDADPROYECTO.COMPANIA,
                                         BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR,
                                         BPNOVEDADPROYECTO.DEPENDENCIA_AFECTAR,
                                         BPNOVEDADPROYECTO.TIPOT_AFECTAR,
                                         BPNOVEDADPROYECTO.CLASET_AFECTAR
                                    FROM BPNOVEDADPROYECTO
                                   INNER JOIN COMPROBANTE_PPTAL
                                      ON BPNOVEDADPROYECTO.COMPANIA      = COMPROBANTE_PPTAL.COMPANIA
                                     AND BPNOVEDADPROYECTO.TIPOT         = COMPROBANTE_PPTAL.TIPO
                                     AND BPNOVEDADPROYECTO.CODIGO        = COMPROBANTE_PPTAL.NUMERO
                                     AND BPNOVEDADPROYECTO.VIGENCIA      = COMPROBANTE_PPTAL.ANO
                                   WHERE BPNOVEDADPROYECTO.CLASET IN (''P'')
                                        AND BPNOVEDADPROYECTO.ESTADO NOT IN (''A'',''N'')';
                MI_MERGEENLACE  := ' TABLA.COMPANIA       = VISTA.COMPANIA 
                                    AND TABLA.TIPOT       = VISTA.TIPOT_AFECTAR 
                                    AND TABLA.CLASET      = VISTA.CLASET_AFECTAR 
                                    AND TABLA.CODIGO      = VISTA.DOCUMENTO_AFECTAR 
                                    AND TABLA.DEPENDENCIA = VISTA.DEPENDENCIA_AFECTAR ';
                MI_MERGEEXISTE  := 'UPDATE SET CONCOMPROBANTEPPTAL = -1, MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                MI_RTA          := PCK_DATOS.FC_ACME( UN_TABLA      => MI_TABLA
                                                     ,UN_ACCION 	  => 'MM'
                                                     ,UN_MERGEUSING => MI_MERGEUSING
                                                     ,UN_MERGEENLACE=> MI_MERGEENLACE
                                                     ,UN_MERGEEXISTE=> MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_ACTIND_COMPRPTAL,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        MI_CADENA := '' || MI_RTA || '';
    END IF;
    RETURN MI_CADENA;
END FC_VALIDARPROCESOS;

  --11
  PROCEDURE PR_VERIFICARSALDOPROYECTO 
    /*
      NAME              : PR_VERIFICARSALDOPROYECTO
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRATION  : PABLO ANDRÉS ESPITIA CUCA
      DATE MIGRATION    : 29/09/2017
      TIME              : 11:39 AM
      SOURCE MODULE     : BANCO DE PROYECTOS (52)
      PARAMETERS        : UN_COMPANIA      => Codigo de la compania desde la que se inicio sesion.
                          UN_PROYECTO      => Codigo del proyecto.
                          UN_VALOR         => Valor que se va a financiar en el proyecto, al insertar.
                          UN_VALOR_ANT     => Valor financiado, antes de editar.
                          UN_VALORPROYECTO => Valor total del proyecto.
      DESCRIPTION       : Verifica que la suma de los valores financiados más el valor ingresado o editado no supere el valor 
                          del proyecto.
      MODIFIED BY       : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :

      @NAME  : verificarSaldoProyecto
    */
    (
      UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_PROYECTO          IN BP_PROYFUENTESFINANCIACION.PROYECTO%TYPE,
      UN_VALOR             IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
      UN_VALOR_ANT         IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
      UN_VALORPROYECTO     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0 
    )
  AS 
    MI_SUMVALOR PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
    --Valor total de fuentes de financiacion / Valor total financiado
    BEGIN
      SELECT SUM(VALOR)
      INTO MI_SUMVALOR
      FROM BP_PROYFUENTESFINANCIACION
      WHERE COMPANIA = UN_COMPANIA
        AND PROYECTO = UN_PROYECTO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_SUMVALOR := 0;
    END;

    IF UN_VALORPROYECTO < (MI_SUMVALOR + UN_VALOR - UN_VALOR_ANT) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_MSG_PRVSP_VALVALORPROY);
      END;
    END IF;
  END PR_VERIFICARSALDOPROYECTO;
--12
PROCEDURE PR_VOBO_AFTER 
/*
   NAME 			      : PR_VOBO_AFTER
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: LAURA MELIZA BOTIA PEREZ 
   DATE MIGRADOR	  : 05/06/2018
   TIME				      : 11:57 AM
   MODULO ORIGEN	  : BANCO_PROYECTOS_PROCESOS
   DESCRIPTION		  : 
   MODIFIER			    : 
   DATE MODIFIED	  :
   TIME				      : 
   MODIFICATIONS	  : 
   PARAMETERS       : UN_COMPANIA         =>Compania con la que ingreso a la compania
                      UN_TIPOT            =>Tipo t que se selecciona en el modal
                      UN_CLASET           =>Clase t que se selecciona en el modal
                      UN_NOVEDAD          =>Novedad 
                      UN_DEPENDENCIA      =>Dependencia
                      UN_USUARIO          =>Usuario


   @NAME: validarVOBO
   @METHOD: GET

*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,                          -- Código de la compañía
  UN_TIPOT            IN VARCHAR2,                                          -- Tipot
  UN_CLASET           IN VARCHAR2,                                          -- Claset
  UN_NOVEDAD          IN VARCHAR2,                             -- Código de la novedad
  UN_DEPENDENCIA      IN BP_D_NOVEDADPROYECTO.DEPENDENCIA%TYPE,                            -- Código de la depedencia
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO                            -- Código del usuario por el cual se ingresa a la aplicación
 )
AS
      MI_TABLA                     PCK_SUBTIPOS.TI_TABLA;
      MI_MERGEUSING		             PCK_SUBTIPOS.TI_MERGEUSING;
      MI_MERGEENLACE		           PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE		           PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_RS                        VARCHAR2(200);
      MI_SOLUCION                  VARCHAR(200);              


 BEGIN

  FOR MI_RS IN (  SELECT DISTINCT PROYECTO
                  FROM BP_D_NOVEDADPROYECTO 
                  WHERE COMPANIA    = UN_COMPANIA 
                    AND TIPOT       = UN_TIPOT 
                    AND CLASET      = UN_CLASET 
                    AND NOVEDAD     = UN_NOVEDAD
                    AND DEPENDENCIA = UN_DEPENDENCIA
                )

  LOOP 
    MI_TABLA      := 'BP_D_NOVEDADPROYECTO';
    MI_MERGEUSING :=' SELECT DISTINCT
                        COMPONENTES.COMPANIA,
                        COMPONENTES.CODIGOPROYECTO,
                        COMPONENTES.CODIGO,
                        COMPONENTES.TIPOCOMPONENTE
                      FROM BP_D_NOVEDADPROYECTO
                       INNER JOIN COMPONENTES
                         ON BP_D_NOVEDADPROYECTO.COMPANIA         = COMPONENTES.COMPANIA
                         AND BP_D_NOVEDADPROYECTO.PROYECTO       = COMPONENTES.CODIGOPROYECTO
                         AND BP_D_NOVEDADPROYECTO.COMPONENTE     = COMPONENTES.CODIGO
                     WHERE BP_D_NOVEDADPROYECTO.COMPANIA       = ''' || UN_COMPANIA || '''
                        AND BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE IS NULL
                        AND BP_D_NOVEDADPROYECTO.PROYECTO  = '''||MI_RS.PROYECTO||'''';
    MI_MERGEENLACE := ' TABLA.COMPANIA                  = VISTA.COMPANIA    
                    AND TABLA.PROYECTO                  = VISTA.CODIGOPROYECTO      
                    AND TABLA.COMPONENTE                = VISTA.CODIGO      ';

    MI_MERGEEXISTE := 'UPDATE SET TIPOCOMPONENTE  = VISTA.TIPOCOMPONENTE, 
                                  MODIFIED_BY     = '''||UN_USUARIO||''', 
                                  DATE_MODIFIED   = SYSDATE ';
    BEGIN 
      BEGIN 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (   UN_TABLA      => MI_TABLA,
                                                UN_ACCION 	  => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD =>SQLCODE,
                                      UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPINDICATIVOVAL
                                    );   
    END;    




  --"Limpiando Saldos..."

                MI_TABLA      := ' BP_D_NOVEDADPROYECTO';
                MI_MERGEUSING := ' SELECT BPNOVEDADPROYECTO.COMPANIA,
                                          BPNOVEDADPROYECTO.TIPOT,
                                          BPNOVEDADPROYECTO.CLASET,
                                          BPNOVEDADPROYECTO.DEPENDENCIA,
                                          BPNOVEDADPROYECTO.CODIGO
                                     FROM BPNOVEDADPROYECTO
                                    INNER JOIN BP_D_NOVEDADPROYECTO
                                       ON BPNOVEDADPROYECTO.COMPANIA    = BP_D_NOVEDADPROYECTO.COMPANIA
                                      AND BPNOVEDADPROYECTO.TIPOT 	    = BP_D_NOVEDADPROYECTO.TIPOT
                                      AND BPNOVEDADPROYECTO.CLASET 	    = BP_D_NOVEDADPROYECTO.CLASET
                                      AND BPNOVEDADPROYECTO.CODIGO      = BP_D_NOVEDADPROYECTO.NOVEDAD
                                      AND BPNOVEDADPROYECTO.DEPENDENCIA = BP_D_NOVEDADPROYECTO.DEPENDENCIA
                                    WHERE BPNOVEDADPROYECTO.COMPANIA    = ''' || UN_COMPANIA || '''
                                      AND BP_D_NOVEDADPROYECTO.PROYECTO = ''' ||MI_RS.PROYECTO|| '''
                                      AND NVL(BP_D_NOVEDADPROYECTO.CONPROGRAMACION, 0) NOT IN(0)';
                MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA    
                                    AND TABLA.TIPOT       = VISTA.TIPOT       
                                    AND TABLA.CLASET      = VISTA.CLASET      
                                    AND TABLA.DEPENDENCIA = VISTA.DEPENDENCIA 
                                    AND TABLA.CODIGO      = VISTA.CODIGO ';
                MI_MERGEEXISTE := ' UPDATE  SET TABLA.CONPROGRAMACION = 0, 
                                    TABLA.MODIFIED_BY = '''||UN_USUARIO||''', 
                                    TABLA.DATE_MODIFIED = SYSDATE ';

  BEGIN 
      BEGIN 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (   UN_TABLA      => MI_TABLA,
                                                UN_ACCION 	  => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD =>SQLCODE,
                                      UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPINDICATIVOVAL
                                    );   
    END;    

--Limpia los valores del  proyecto a cero
    MI_MERGEUSING := 'SELECT DISTINCT PROYECTOS.COMPANIA,
                              PROYECTOS.CODIGO
                      FROM PROYECTOS        
                        INNER JOIN COMPONENTES
                          ON PROYECTOS.COMPANIA                 = COMPONENTES.COMPANIA
                          AND PROYECTOS.CODIGO                  = COMPONENTES.CODIGOPROYECTO
                        INNER JOIN COMPONENTES_ACTIVIDADES
                          ON COMPONENTES.COMPANIA               = COMPONENTES_ACTIVIDADES.COMPANIA
                          AND COMPONENTES.CODIGOPROYECTO        = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                          AND COMPONENTES.CODIGO               = COMPONENTES_ACTIVIDADES.COMPONENTE
                          AND COMPONENTES.TIPOCOMPONENTE        = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
                      WHERE PROYECTOS.COMPANIA                  = ''' || UN_COMPANIA || '''
                        AND PROYECTOS.CODIGO                    = ''' ||MI_RS.PROYECTO|| '''';

    MI_TABLA := 'PROYECTOS';

    MI_MERGEENLACE := 'TABLA.COMPANIA                            = VISTA.COMPANIA
                       AND TABLA.CODIGO                           = VISTA.CODIGO ';

    MI_MERGEEXISTE := 'UPDATE SET 
                              VALOREJECUTADO            = 0,
                              VALORSOLICITADO           = 0,
                              VALORPROGRAMADO           = 0,
                              CONPROGRAMACION           = 0,
                              MODIFIED_BY               = '''||UN_USUARIO||''', 
                              DATE_MODIFIED             = SYSDATE';

   BEGIN 
      BEGIN 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (   UN_TABLA      => MI_TABLA,
                                                UN_ACCION 	  => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD =>SQLCODE,
                                      UN_ERROR_COD=>PCK_ERRORES.ERR_BANCO_PROYECTOS
                                    );   
    END;    
    MI_TABLA := 'COMPONENTES';                           
    MI_MERGEENLACE := ' TABLA.COMPANIA                         =VISTA.COMPANIA
                        AND TABLA.CODIGOPROYECTO                    =VISTA.CODIGO';

    MI_MERGEEXISTE := 'UPDATE SET 
                                VALOREJECUTADO        =0,
                                VALORTOTALSOLICITADO  =0,
                                VALORPROGRAMADO       =0,
                                SALDOCOMPONENTE       =0,
                                CONPROGRAMACION       =0,
                                VALOR_DISMINUIDO      =0,
                                MODIFIED_BY                       = '''||UN_USUARIO||''', 
                                DATE_MODIFIED                     = SYSDATE' ;

     BEGIN 
        BEGIN 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (   UN_TABLA      => MI_TABLA,
                                                UN_ACCION 	  => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        --ERROR COMPONENTES
         PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD =>SQLCODE,
                                      UN_ERROR_COD=>PCK_ERRORES.ERR_BANCO_COMPONENTES
                                    );   
    END;    

    MI_TABLA := 'COMPONENTES_ACTIVIDADES';
    MI_MERGEENLACE :='TABLA.COMPANIA                            = VISTA.COMPANIA

                      AND TABLA.COMPONENTE                      = VISTA.CODIGO
                     ';

    MI_MERGEEXISTE := 'UPDATE SET 
                        VALOREJECUTADO              = 0,
                        VALORPROGRAMADO             = 0,
                        VALOR_SOLICITADO_ACTIVIDAD  = 0,
                        CANTIDAD_EJE                = 0,
                        VALOR_DISMINUIDO            = 0,
                        MODIFIED_BY                       = '''||UN_USUARIO||''', 
                        DATE_MODIFIED                     = SYSDATE' ;

    BEGIN 
        BEGIN 

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (   UN_TABLA      => MI_TABLA,
                                                UN_ACCION 	  => 'MM', 
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        --ERROR COMPONENTES_ACTIVIDADES
         PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD =>SQLCODE,
                                      UN_ERROR_COD=>PCK_ERRORES.ERR_BANCO_COMPONENTESACTI
                                    );   
    END;                  

  MI_SOLUCION := PCK_BANCOS_PROY2.FC_MANTENIMIENTO_ACT_PROYECTO(UN_COMPANIA => UN_COMPANIA,UN_PROYECTO_INICIAL => MI_RS.PROYECTO, UN_PROYECTO_FINAL=>MI_RS.PROYECTO,UN_USUARIO => UN_USUARIO);
END LOOP;
END PR_VOBO_AFTER;


END PCK_BANCOS_PROY2;