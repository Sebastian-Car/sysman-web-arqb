create or replace PACKAGE BODY "PCK_CHIPFUT" AS 


--2
PROCEDURE PR_TRAER_FUENTES_PRESUPUESTO 
 /*
      NAME              : PR_TRAER_FUENTES_PRESUPUESTO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
      DATE MIGRADOR     : 24/02/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : CHIP-FUT
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE INSERTA LOS AUXILIARES A FUENTE_RECURSOS
      MODIFICATIONS     : 

      @NAME:    traerFuentesPresupuesto
      @METHOD:  PUT
    */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO 
 )
AS 
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  BEGIN
      BEGIN 
          MI_CAMPOS  := 'COMPANIA,CODIGO,ANO,NOMBRE';
          MI_VALORES := ' SELECT 
                          COMPANIA,
                          CODIGO,
                          ANO,
                          NOMBRE
                    FROM AUXILIAR
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO = '||UN_ANIO||'
                     AND MOVIMIENTO NOT IN (0)
                     AND CODIGO NOT IN (SELECT 
                                               CODIGO
                                               FROM FUENTE_RECURSOS
                                               WHERE COMPANIA = '''||UN_COMPANIA||'''
                                               AND ANO ='||UN_ANIO||'
                                               AND MOVIMIENTO NOT IN (0))';          

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'FUENTE_RECURSOS',
                                                     UN_ACCION  => 'IS',
                                                     UN_CAMPOS  => MI_CAMPOS,
                                                     UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
    END; 
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
    MI_MSGERROR(1).CLAVE := 'ANO';
    MI_MSGERROR(1).VALOR := UN_ANIO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_FUEN_PRESUPUES,
                               UN_REEMPLAZOS  => MI_MSGERROR,
                               UN_TABLAERROR => 'SP_D_ABONOS');
  END;
END PR_TRAER_FUENTES_PRESUPUESTO;
--3
PROCEDURE PR_CREAR_CODIGOS_FUT 
 /*
      NAME              : PR_CREAR_CODIGOS_FUT
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
      DATE MIGRADOR     : 28/02/2017
      TIME              : 08:46 PM
      SOURCE MODULE     : CHIP-FUT
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE INSERTA LOS CODIGOS FUT DE UN AÑO A OTRO
      MODIFICATIONS     : 

      @NAME:    traerCrearCodigosFut
      @METHOD:  PUT
    */
 ( 
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO
 )
AS
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ANIO_ANTERIOR    PCK_SUBTIPOS.TI_ANIO;
BEGIN

  BEGIN
    BEGIN
        MI_ANIO_ANTERIOR := UN_ANIO-1;
        MI_CAMPOS  :='COMPANIA,ANO,CODIGOFUT,NOMBRE,DESCRIPCION,NATURALEZA,MOVIMIENTO';
        MI_VALORES :='SELECT COMPANIA,
                             '||UN_ANIO||' AS ANO,
                             CODIGOFUT,
                             NOMBRE,
                             DESCRIPCION,
                             NATURALEZA,
                             MOVIMIENTO
                        FROM CODIGOSFUT
                       WHERE COMPANIA = '''||UN_COMPANIA||'''
                         AND ANO      = '||MI_ANIO_ANTERIOR;

         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'CODIGOSFUT',
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
       END; 
       EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
       MI_MSGERROR(1).CLAVE := 'ANO';
       MI_MSGERROR(1).VALOR := UN_ANIO;

       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_CREAR_CODIGOS_FUT,
                                  UN_REEMPLAZOS  => MI_MSGERROR,
                                  UN_TABLAERROR => 'SP_D_ABONOS');
     END;                                       


END PR_CREAR_CODIGOS_FUT;


FUNCTION FC_NOMBREFUENTE
 /*
      NAME              : En access NombreFuente
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 28/03/2017
      TIME              : 10:00 AM
      SOURCE MODULE     : CHIP-FUT
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE RETORNA EL NOMBRE DE UNA FUENTE FUT
      MODIFICATIONS     : 

      @NAME:    obtenerNombreFuente
      @METHOD:  GET
    */
(
   UN_CODFUENTE    IN FUENTESFUT.CODIGOFU_FUT%TYPE
)
RETURN VARCHAR2
AS
  MI_RETORNO       FUENTESFUT.NOMBREFU_FUT%TYPE;
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
  BEGIN
    BEGIN
      SELECT NOMBREFU_FUT 
      INTO MI_RETORNO
      FROM FUENTESFUT 
      WHERE CODIGOFU_FUT= UN_CODFUENTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
                RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN    
       MI_MSGERROR(1).CLAVE := 'CODIGO';
       MI_MSGERROR(1).VALOR := UN_CODFUENTE;
       PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_NOMFUENTES,
          UN_REEMPLAZOS =>  MI_MSGERROR
        );
  END;  
  RETURN MI_RETORNO;
END;

  -- 6
FUNCTION FC_PREPARAANIOCBANCOS 
      /*
        NAME              : FC_PREPARAANIOCBANCOS --> EN ACCESS preparaano_Click
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
        DATE MIGRADOR     : 24/03/2017
        TIME              : 02:25 PM
        SOURCE MODULE     : SysmanChip2017.02.03
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Realiza la actualización de las cuentas bancarias pertenecientes a un año especifico, haciendo la copia de las mismas a un año destino.
                             Adicionalmente trae la información de las cuentas tipo 'B' que se encuentran en el plan contable para el Año Destino y las almacena en CUENTASBANCOS
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación
                            UN_ANIO        => Año en el cual se está trabajando las cuentas bancarias
                            UN_ANIODESTINO => Año destino es el año que se desea preparar basándose en la informacion del año que se está trabajando
        @NAME  :  prepararAnio
        @METHOD:  GET     
      */ 
(
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,  
      UN_ANIO               IN PCK_SUBTIPOS.TI_ANIO,
      UN_ANIODESTINO        IN PCK_SUBTIPOS.TI_ANIO
)
RETURN NUMBER 
  AS 
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;  
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;  
      MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  BEGIN
    BEGIN
      BEGIN
        MI_TABLA   := 'CUENTABANCOS';
        MI_CAMPOS  := ' COMPANIA, '||
                      ' IDCONTABLE, '||
                      ' BANCO, '||
                      ' SUCURSALBANCO, '||
                      ' FECHAAPERTURA, '||
                      ' FECHACANCELACION, '||
                      ' TIPOCUENTA, '||
                      ' DIRECCION, '||
                      ' CUENTANUMERO, '||
                      ' CLASECUENTA, '||
                      ' RECURSOS, '||
                      ' ESTADO, '||
                      ' GERENTE, '||
                      ' ANO ';
        MI_VALORES := 'SELECT CUENTABANCOS.COMPANIA, '||
                      '  CUENTABANCOS.IDCONTABLE, '||
                      '  CUENTABANCOS.BANCO, '||
                      '  CUENTABANCOS.SUCURSALBANCO, '||
                      '  CUENTABANCOS.FECHAAPERTURA, '||
                      '  CUENTABANCOS.FECHACANCELACION, '||
                      '  CUENTABANCOS.TIPOCUENTA, '||
                      '  CUENTABANCOS.DIRECCION, '||
                      '  CUENTABANCOS.CUENTANUMERO, '||
                      '  CUENTABANCOS.CLASECUENTA, '||
                      '  CUENTABANCOS.RECURSOS, '||
                      '  CUENTABANCOS.ESTADO, '||
                      '  CUENTABANCOS.GERENTE, '||
                         UN_ANIODESTINO || ' ' ||
                      'FROM CUENTABANCOS '||
                      '  INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE '||
                      '     ON CUENTABANCOS.COMPANIA   = PLAN_CONTABLE.COMPANIA '||
                      '    AND CUENTABANCOS.IDCONTABLE = PLAN_CONTABLE.ID '||
                      '    AND CUENTABANCOS.ANO        = PLAN_CONTABLE.ANO '||
                      'WHERE CUENTABANCOS.COMPANIA     = ''' || UN_COMPANIA ||''' '||
                      '  AND CUENTABANCOS.ANO          = '|| UN_ANIO ;

        MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                      UN_ACCION    => 'IS', 
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_VALORES   => MI_VALORES);       

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (
          UN_EXC_COD 		  =>	SQLCODE,
          UN_ERROR_COD	  =>	PCK_ERRORES.ERR_ENTES_INSCUENTABANCOS,
          UN_TABLAERROR 	=>	MI_TABLA
        );
    END;

    IF MI_RTA IN(0) THEN

      BEGIN
        BEGIN  
          MI_TABLA      := 'CUENTABANCOS';
          MI_MERGEUSING := 'SELECT CUENTABANCOS.COMPANIA, '||
                           '       CUENTABANCOS.ANO, '||
                           '       CUENTABANCOS.IDCONTABLE, '||
                           '       CUENTABANCOS.BANCO, '||
                           '   '|| UN_ANIODESTINO ||' ANIODESTINO '||
                           'FROM CUENTABANCOS '||
                           'INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE '||
                           '   ON CUENTABANCOS.COMPANIA   = PLAN_CONTABLE.COMPANIA '||
                           '  AND CUENTABANCOS.IDCONTABLE = PLAN_CONTABLE.ID '||
                           '  AND CUENTABANCOS.ANO        = PLAN_CONTABLE.ANO '||
                           'WHERE PLAN_CONTABLE.COMPANIA  = ''' || UN_COMPANIA ||''' '||
                           '  AND PLAN_CONTABLE.ANO       = ' || UN_ANIO;
          MI_MERGEENLACE := '     TABLA.COMPANIA      = VISTA.COMPANIA   '||
                            ' AND TABLA.ANO           = VISTA.ANO        '||   
                            ' AND TABLA.IDCONTABLE    = VISTA.IDCONTABLE '||   
                            ' AND TABLA.BANCO         = VISTA.BANCO';   
          MI_MERGEEXISTE := ' UPDATE SET TABLA.ANO    = VISTA.ANIODESTINO';                    

          MI_RTA         := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                              UN_ACCION      => 'MM', 
                                              UN_MERGEUSING  => MI_MERGEUSING, 
                                              UN_MERGEENLACE => MI_MERGEENLACE , 
                                              UN_MERGEEXISTE => MI_MERGEEXISTE);                  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_ACTCUENTABANCOS
          );
      END;
    END IF;

    BEGIN
      BEGIN
        MI_TABLA   := 'CUENTABANCOS';
        MI_CAMPOS  := ' COMPANIA, '||
                      ' ANO, ' ||
                      ' IDCONTABLE, '||
                      ' BANCO, '||
                      ' ESOFICIAL ';
        MI_VALORES := 'SELECT PLAN_CONTABLE.COMPANIA, '||
                      '  PLAN_CONTABLE.ANO, '||
                      '  PLAN_CONTABLE.ID, '||
                      '  PLAN_CONTABLE.BANCO, '||
                      '  PLAN_CONTABLE.ESOFICIAL '||
                      'FROM V_PLAN_CONTABLE PLAN_CONTABLE '||
                      '  LEFT JOIN CUENTABANCOS '||
                      '    ON PLAN_CONTABLE.ANO        = CUENTABANCOS.ANO '||
                      '   AND PLAN_CONTABLE.ID         = CUENTABANCOS.IDCONTABLE '||
                      '   AND PLAN_CONTABLE.COMPANIA   = CUENTABANCOS.COMPANIA '||
                      'WHERE PLAN_CONTABLE.COMPANIA    = ''' || UN_COMPANIA || ''' '||
                      '  AND PLAN_CONTABLE.ANO         = '|| UN_ANIODESTINO ||' '||
                      '  AND PLAN_CONTABLE.CLASECUENTA IN (''B'') '||
                      '  AND CUENTABANCOS.IDCONTABLE   IS NULL '||
                      '  AND PLAN_CONTABLE.MOVIMIENTO  IN (-1) ';

        MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                      UN_ACCION    => 'IS', 
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_VALORES   => MI_VALORES);       

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD 		  =>	SQLCODE,
          UN_ERROR_COD	  =>	PCK_ERRORES.ERR_ENTES_INSCBANCOSPLAN,
          UN_TABLAERROR 	=>	MI_TABLA
        );
    END;
  RETURN MI_RTA;
END FC_PREPARAANIOCBANCOS; 

FUNCTION FC_TESORERIASALUD
  (
  --7
  /*
        NAME              : FC_TESORERIASALUD --> EN ACCESS se depura para facilidad de mantenimiento de creartabla y generarplanoformularios
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
        DATE MIGRADOR     : 29/03/2017
        TIME              : 10:45 AM
        SOURCE MODULE     : SysmanChip2017.02.03
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Elabora la consulta de elaboracion del plano y retorna 
                            la cadena de caracteres necesaria para generar el plano.
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                            UN_ANIO          => Año seleccionado en el formulario.
                            UN_PESOS         => Indicador seleccionado en el formulario.
                            UN_TRIMESTRE     => Trimestre seleccionado en el formulario.
                            UN_ANIO          => Año seleccionado en el formulario.
                            UN_CODIGOENTIDAD => Codigo de Entidad ingresado en el formulario.
                            UN_NOMBREARCH    => Nombre de archivo generado en el formulario.
        @NAME  :  generarPlanoTesoreriaSalud
        @METHOD:  GET     
      */ 
      UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
     ,UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO
     ,UN_PESOS         IN PCK_SUBTIPOS.TI_LOGICO 
     ,UN_TRIMESTRE     IN PCK_SUBTIPOS.TI_ENTERO
     ,UN_CODIGOENTIDAD IN VARCHAR2
     ,UN_NOMBREARCH    IN VARCHAR2
  )
  RETURN CLOB 
  AS
      MI_RTA         CLOB;
      MI_REDONDEO    PCK_SUBTIPOS.TI_ENTERO;
      MI_MESINICIAL  PCK_SUBTIPOS.TI_ENTERO; 
      MI_MESFINAL    PCK_SUBTIPOS.TI_ENTERO; 
      MI_MES         PCK_SUBTIPOS.TI_ENTERO; 
  BEGIN
      IF UN_TRIMESTRE = 1 THEN
          MI_MESINICIAL:= 1;
          MI_MESFINAL:= 3;
      END IF;
      IF UN_TRIMESTRE = 2 THEN 
         MI_MESINICIAL:= 4;
         MI_MESFINAL:= 6;
      END IF;
      IF UN_TRIMESTRE = 3 THEN 
          MI_MESINICIAL:= 7;
          MI_MESFINAL:= 9;
      END IF;
      IF UN_TRIMESTRE = 4 THEN 
          MI_MESINICIAL:= 10;
          MI_MESFINAL:= 12;
      END IF;
      MI_MES:= CASE MI_MESFINAL WHEN 1 THEN 3
                                WHEN 2 THEN 6
                                WHEN 3 THEN 9
                                ELSE 12
               END;
      MI_MESINICIAL:= CASE MI_MESFINAL WHEN 1 THEN 1
                                       WHEN 2 THEN 3
                                       WHEN 3 THEN 6
                                       ELSE 9
                      END;
      MI_REDONDEO:=PCK_SYSMAN_UTL.FC_PAR(
                                  UN_COMPANIA  => UN_COMPANIA
                                 ,UN_NOMBRE    => 'DIGITO REDONDEO DE INFORMES FUT' 
                                 ,UN_MODULO    => '99'
                                 ,UN_FECHA_PAR => SYSDATE);
      MI_RTA:='S'||CHR(9)||
              UN_CODIGOENTIDAD||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
                   END||CHR(9)||
              UN_ANIO||CHR(9)||
              UN_NOMBREARCH||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY');

      <<RECORREGATEGORIASSALUD>>
      FOR RS IN (
          WITH CATEGORIASFIN AS
          (
          SELECT  TMP_FUT_CATEGORIAS.FUT
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(CASE WHEN TIPO_FUTSALUD=1
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO)
                                     ELSE 0
                                END)
                       ELSE SUM(CASE WHEN TIPO_FUTSALUD=1
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO)
                                     ELSE 0
                                END)
                  END REGIMEN_SUBSIDIADO
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(CASE WHEN TIPO_FUTSALUD=2
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO)
                                     ELSE 0
                                END)
                       ELSE SUM(CASE WHEN TIPO_FUTSALUD=2
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO)
                                     ELSE 0
                                END)
                  END SALUD_PUBLICA
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(CASE WHEN TIPO_FUTSALUD=3
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO)
                                     ELSE 0
                                END)
                       ELSE SUM(CASE WHEN TIPO_FUTSALUD=3
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO)
                                     ELSE 0
                                END)
                       END PRESTACION_DEL_SERVICIO_OFERTA
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(CASE WHEN TIPO_FUTSALUD=4
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO)
                                     ELSE 0
                                END)
                       ELSE SUM(CASE WHEN TIPO_FUTSALUD=4
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO)
                                     ELSE 0
                                END)
                  END OTROS_GASTOS_SALUD_INVERSION
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(CASE WHEN TIPO_FUTSALUD=5
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO)
                                     ELSE 0
                                END)
                       ELSE SUM(CASE WHEN TIPO_FUTSALUD=5
                                     THEN PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO)
                                     ELSE 0
                                END)
                  END OTROS_GASTOS_SALUD_FUNCTO
                 ,CASE WHEN UN_PESOS = 0
                       THEN SUM(PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO))
                       ELSE SUM(PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO))
                  END TOTAL
                 ,0 SALDO_PENDIENTE_POR_TRANSFERIR
          FROM (
                SELECT   FUT_SALUD_TESORERIA FUT 
                        , PLAN_PRESUPUESTAL.TIPO_FUTSALUD
                        , SALDO_PLAN_PPTAL.COMPANIA
                        , SALDO_PLAN_PPTAL.ANO
                        , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                   THEN APROPIACION_DEBITO-APROPIACION_CREDITO
                                   ELSE APROPIACION_CREDITO-APROPIACION_DEBITO
                              END) APRINI
                        , SUM(SALDO_PLAN_PPTAL.ADICION) ADI
                        , SUM(SALDO_PLAN_PPTAL.REDUCCION) RED
                        , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                   THEN TRASLADO_DEBITO-TRASLADO_CREDITO
                                   ELSE TRASLADO_CREDITO-TRASLADO_DEBITO
                               END) TRASLADO
                        , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                   THEN APLAZAM_DEBITO-APLAZAM_CREDITO
                                   ELSE APLAZAM_CREDITO-APLAZAM_DEBITO
                               END) APLAZAMIENTO
                        , SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD) DISPONIBILIDAD
                        , PLAN_PRESUPUESTAL.NATURALEZA
                        , SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION) REO
                        , SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) MODIFREO
                        , SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS) TOTALMODIFINGRESOS
                        , SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS) INGRESOSCAUSADOS
                        , PLAN_PRESUPUESTAL.FUENTE_FLS 
                        , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                   THEN APROPIACION_DEBITO-APROPIACION_CREDITO
                                   ELSE APROPIACION_CREDITO-APROPIACION_DEBITO
                              END)
                          +SUM(SALDO_PLAN_PPTAL.ADICION)-SUM(SALDO_PLAN_PPTAL.REDUCCION)
                          +SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                    THEN TRASLADO_DEBITO-TRASLADO_CREDITO
                                    ELSE TRASLADO_CREDITO-TRASLADO_DEBITO
                               END) APRDEF 
                        , SUM(CASE WHEN SALDO_PLAN_PPTAL.MES < MI_MES
                                   THEN EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                   ELSE 0
                              END) EJECANT
                        , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                   THEN (CASE WHEN SALDO_PLAN_PPTAL.MES BETWEEN MI_MESINICIAL AND MI_MESFINAL
                                              THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                              ELSE 0
                                         END)
                                   ELSE (CASE WHEN SALDO_PLAN_PPTAL.MES BETWEEN MI_MESINICIAL AND MI_MESFINAL
                                              THEN EJE_PPT_CREDITO- EJE_PPT_DEBITO
                                              ELSE 0
                                         END)
                              END) EJECUCIONPPT
                   FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                       LEFT JOIN SALDO_PLAN_PPTAL 
                           ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
                           AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
                           AND PLAN_PRESUPUESTAL.ID       = SALDO_PLAN_PPTAL.CODIGO
                  WHERE SALDO_PLAN_PPTAL.COMPANIA = UN_COMPANIA
                    AND SALDO_PLAN_PPTAL.ANO      = UN_ANIO
                    AND SALDO_PLAN_PPTAL.MES      BETWEEN MI_MESINICIAL AND MI_MESFINAL
                    AND PLAN_PRESUPUESTAL.FUT_SALUD_TESORERIA IS NOT NULL
                  GROUP BY   SALDO_PLAN_PPTAL.COMPANIA
                           , SALDO_PLAN_PPTAL.ANO
                           , PLAN_PRESUPUESTAL.FUT_SALUD_TESORERIA
                           , PLAN_PRESUPUESTAL.NATURALEZA
                           , PLAN_PRESUPUESTAL.FUENTE_FLS
                           , PLAN_PRESUPUESTAL.TIPO_FUTSALUD) TMP_FUT_CATEGORIAS
            GROUP BY TMP_FUT_CATEGORIAS.FUT
            HAVING CASE WHEN UN_PESOS = 0
                        THEN SUM(PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT,MI_REDONDEO))
                        ELSE SUM(PCK_SYSMAN_UTL.FC_ROUND(EJECUCIONPPT/1000,MI_REDONDEO))
                   END NOT IN (0)
            UNION ALL
            SELECT  TMP_FUT_CATEGORIASINI.FUT
                   , SUM(TMP_FUT_CATEGORIASINI.REGIMEN_SUBSIDIADO) REGIMEN_SUBSIDIADO
                   , SUM(TMP_FUT_CATEGORIASINI.SALUD_PUBLICA) SALUD_PUBLICA
                   , SUM(TMP_FUT_CATEGORIASINI.PRESTACION_DEL_SERVICIO_OFERTA) PRESTACION_DEL_SERVICIO_OFERTA
                   , SUM(TMP_FUT_CATEGORIASINI.OTROS_GASTOS_SALUD_INVERSION) OTROS_GASTOS_SALUD_INVERSION
                   , SUM(TMP_FUT_CATEGORIASINI.OTROS_GASTOS_SALUD_FUNCTO) OTROS_GASTOS_SALUD_FUNCTO
                   , SUM(TMP_FUT_CATEGORIASINI.REGIMEN_SUBSIDIADO)
                     +SUM(TMP_FUT_CATEGORIASINI.SALUD_PUBLICA)
                     +SUM(TMP_FUT_CATEGORIASINI.PRESTACION_DEL_SERVICIO_OFERTA)
                     +SUM(TMP_FUT_CATEGORIASINI.OTROS_GASTOS_SALUD_INVERSION)
                     +SUM(TMP_FUT_CATEGORIASINI.OTROS_GASTOS_SALUD_FUNCTO) TOTAL
                    , SUM(TMP_FUT_CATEGORIASINI.SALDO_PENDIENTE_POR_TRANSFERIR) SALDO_PENDIENTE_POR_TRANSFERIR
               FROM ( 
                   SELECT  PLAN_PRESUPUESTAL.TIPO_FUTSALUD
                          ,CASE WHEN UN_PESOS = 0
                                THEN CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=1
                                         THEN PCK_SYSMAN_UTL.FC_ROUND(
                                              SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                       THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                       ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                  END)+SUM(MODIF_INGRESOS),MI_REDONDEO)
                                         ELSE 0
                                    END
                                ELSE CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=1
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS))/1000,MI_REDONDEO)
                                          ELSE 0
                                     END
                           END REGIMEN_SUBSIDIADO
                          ,CASE WHEN UN_PESOS = 0
                                THEN CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=2
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS)),MI_REDONDEO)
                                          ELSE 0
                                     END
                                ELSE CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=2
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS))/1000,MI_REDONDEO)
                                          ELSE 0
                                     END
                           END SALUD_PUBLICA
                          ,CASE WHEN UN_PESOS = 0
                                THEN CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=3
                                           THEN PCK_SYSMAN_UTL.FC_ROUND(
                                                (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                          THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                          ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                     END)+SUM(MODIF_INGRESOS)),MI_REDONDEO)
                                           ELSE 0
                                      END
                                ELSE CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=3
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS))/1000,MI_REDONDEO)
                                           ELSE 0
                                      END
                           END PRESTACION_DEL_SERVICIO_OFERTA
                          ,CASE WHEN UN_PESOS = 0
                                THEN CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=4
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS)),MI_REDONDEO)
                                          ELSE 0
                                      END
                                ELSE CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=4
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS))/1000,MI_REDONDEO)
                                          ELSE 0
                                      END
                           END OTROS_GASTOS_SALUD_INVERSION
                          ,CASE WHEN UN_PESOS = 0
                                THEN CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=5
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                     END)+SUM(MODIF_INGRESOS)),MI_REDONDEO)
                                          ELSE 0
                                     END
                                ELSE CASE WHEN PLAN_PRESUPUESTAL.TIPO_FUTSALUD=5
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                               (SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                                         THEN EJE_PPT_DEBITO-EJE_PPT_CREDITO
                                                         ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
                                                    END)+SUM(MODIF_INGRESOS))/1000,MI_REDONDEO)
                                          ELSE 0
                                      END
                           END OTROS_GASTOS_SALUD_FUNCTO
                          ,0 SALDO_PENDIENTE_POR_TRANSFERIR
                          ,'ITFS' FUT
                          ,SALDO_PLAN_PPTAL.COMPANIA
                          ,SALDO_PLAN_PPTAL.ANO
                          ,PLAN_PRESUPUESTAL.NATURALEZA 
                     FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                         LEFT JOIN SALDO_PLAN_PPTAL 
                             ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
                             AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
                             AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO
                     WHERE SALDO_PLAN_PPTAL.COMPANIA    =  UN_COMPANIA
                       AND SALDO_PLAN_PPTAL.ANO         =  (UN_ANIO-1) 
                       AND SALDO_PLAN_PPTAL.MES         <= CASE WHEN MI_MESINICIAL =1
                                                                THEN 12
                                                                ELSE MI_MESINICIAL-1
                                                           END 
                       AND PLAN_PRESUPUESTAL.NATURALEZA = 'C'
                       AND PLAN_PRESUPUESTAL.FUT_SALUD_TESORERIA IS NOT NULL
                     GROUP BY   PLAN_PRESUPUESTAL.TIPO_FUTSALUD
                              , 'ITFS'
                              , SALDO_PLAN_PPTAL.COMPANIA
                              , SALDO_PLAN_PPTAL.ANO
                              , PLAN_PRESUPUESTAL.NATURALEZA) TMP_FUT_CATEGORIASINI
                     GROUP BY TMP_FUT_CATEGORIASINI.FUT  
          )     SELECT 
                         FUT
                       , REGIMEN_SUBSIDIADO
                       , SALUD_PUBLICA
                       , PRESTACION_DEL_SERVICIO_OFERTA
                       , OTROS_GASTOS_SALUD_INVERSION
                       , OTROS_GASTOS_SALUD_FUNCTO
                       , TOTAL
                       , SALDO_PENDIENTE_POR_TRANSFERIR
                  FROM CATEGORIASFIN
                UNION ALL          
                SELECT   'FTFS' FUT
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(REGIMEN_SUBSIDIADO,0)
                                  ELSE REGIMEN_SUBSIDIADO 
                             END) REGIMEN_SUBSIDIADO
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(SALUD_PUBLICA,0)
                                  ELSE SALUD_PUBLICA 
                             END) SALUD_PUBLICA
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(PRESTACION_DEL_SERVICIO_OFERTA,0)
                                  ELSE PRESTACION_DEL_SERVICIO_OFERTA 
                             END) PRESTACION_DEL_SERVICIO_OFERTA
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(OTROS_GASTOS_SALUD_INVERSION,0)
                                  ELSE OTROS_GASTOS_SALUD_INVERSION 
                             END)  OTROS_GASTOS_SALUD_INVERSION
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(OTROS_GASTOS_SALUD_FUNCTO,0)
                                  ELSE OTROS_GASTOS_SALUD_FUNCTO 
                             END)  OTROS_GASTOS_SALUD_FUNCTO
                       , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                  THEN -NVL(SALDO_PENDIENTE_POR_TRANSFERIR,0)
                                  ELSE SALDO_PENDIENTE_POR_TRANSFERIR 
                             END) SALDO_PENDIENTE_POR_TRANSFERIR
                      , SUM(CASE WHEN SUBSTR(FUT,1,4) = 'PTFS' 
                                 THEN -NVL(TOTAL,0)
                                 ELSE TOTAL  
                            END) TOTAL  
                 FROM CATEGORIASFIN 
                 GROUP BY 'FTFS'
                 ORDER BY 1)
      LOOP 
          MI_RTA:=MI_RTA
                  ||'D'||CHR(9)
                  ||RS.FUT||CHR(9)
                  ||RS.REGIMEN_SUBSIDIADO
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00' 
                         ELSE '' 
                    END ||CHR(9)
                  ||RS.SALUD_PUBLICA
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00' 
                         ELSE '' 
                    END ||CHR(9)
                  ||RS.PRESTACION_DEL_SERVICIO_OFERTA
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00' 
                         ELSE '' 
                    END ||CHR(9)
                  ||RS.OTROS_GASTOS_SALUD_INVERSION
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00' 
                         ELSE '' 
                    END ||CHR(9)
                  ||RS.OTROS_GASTOS_SALUD_FUNCTO
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00' 
                         ELSE '' 
                    END ||CHR(9)
                  ||0||'.00'||CHR(9)
                  ||RS.TOTAL
                  ||CASE WHEN UN_PESOS = 0 
                         THEN '.00'  
                         ELSE '' 
                    END||CHR(10)||CHR(13);
      END LOOP RECORREGATEGORIASSALUD;
    RETURN MI_RTA;
END FC_TESORERIASALUD;


--8 
FUNCTION FC_INVERSIONES_TEMPORALES(
  /*
        NAME              : FC_INVERSIONES_TEMPORALES
        AUTHORS           : STEFANINI SYSMAN  
        AUTHOR MIGRACION  : JUAN SEBASTIAN FORERO NOGUERA
        DATE MIGRADOR     : 29/03/2017
        TIME              : 03:25 PM
        SOURCE MODULE     : SysmanChip2017.02.03
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Elabora la consulta de elaboracion del plano y retorna 
                            la cadena de caracteres necesaria para generar el plano.
        MODIFICATIONS     : 
        PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                            UN_PERIODO     => Trimestre seleccionado en el formulario.
                            UN_ANO          => Año seleccionado en el formulario.
                            UN_CODIGOENTIDAD => Codigo de Entidad ingresado en el formulario.
                            UN_STRSQL => Consulta para el cuerpo del reporte
        @NAME  :  generarInversion
        @METHOD:  GET     
      */ 

  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_STRSQL             IN PCK_SUBTIPOS.TI_STRSQL,
  UN_CODIGOENTIDAD      IN VARCHAR2,
  UN_PERIODO            IN VARCHAR2,
  UN_ANO                IN PCK_SUBTIPOS.TI_ANIO


)RETURN CLOB
AS
  MI_MENSAJE                 CLOB;
  MI_CONSULTA                PCK_SUBTIPOS.TI_STRSQL;
  MI_G                       NUMBER;
  MI_I                       NUMBER;
  MI_K                       NUMBER;
  MI_M                       NUMBER;
  MI_N                       NUMBER;
  MI_RS		                   SYS_REFCURSOR;
  MI_CONSULTASUP             PCK_SUBTIPOS.TI_STRSQL;
  MI_V_CONCEPTO              VARCHAR2(50 CHAR);
  MI_V_NUMERO                VARCHAR2(50 CHAR);
  MI_V_TIPO_INVERSION        VARCHAR2(50 CHAR);
  MI_V_MONEDA                VARCHAR2(50 CHAR);
  MI_V_ENTIDAD               VARCHAR2(50 CHAR);
  MI_V_ENTIDADINTER          VARCHAR2(50 CHAR);
  MI_V_PLAZO                 VARCHAR2(50 CHAR);
  MI_V_TASA_CUPO             VARCHAR2(50 CHAR);
  MI_V_FECHA_EMISION         VARCHAR2(15 CHAR);
  MI_V_FECHA_COMP            VARCHAR2(15 CHAR);
  MI_V_VALOR_NOMINAL         VARCHAR2(50 CHAR);
  MI_V_TASA_COMPRA           VARCHAR2(50 CHAR);
  MI_V_VALOR_PAGA            VARCHAR2(50 CHAR);  
  MI_V_FECHA_VENCIMIENTO     VARCHAR2(15 CHAR);
  MI_V_INVERSION_VENDID      VARCHAR2(50 CHAR);
  MI_V_VALOR_VNT             VARCHAR2(50 CHAR);
  MI_V_FECHA_VNT             VARCHAR2(15 CHAR);
  MI_V_TASA_VENTA            VARCHAR2(50 CHAR);  
  MI_V_TASA_RENDIMIENTO      VARCHAR2(50 CHAR); 
  MI_V_RENDIMIENTOS_DET      VARCHAR2(50 CHAR);  
    BEGIN 
      MI_MENSAJE := 'S'||CHR(9)
                  ||UN_CODIGOENTIDAD||CHR(9)
                  ||UN_PERIODO||CHR(9)
                  ||UN_ANO||CHR(9)
                  ||'INVERSIONES_TEMPORALES'||CHR(9)
                  ||TO_CHAR(SYSDATE,'DD-MM-YYYY')||CHR(9)
                  ||CHR(10);
      MI_CONSULTASUP:= 'SELECT Sum(VALOR_NOMINAL) G, 
            Sum(VALOR_PAGA) I, 
            Sum(VALOR_VNT) K, 
            Sum(TASA_RENDIMIENTO_VENCIMIENTO) M, 
            Sum(INVERSIONES_TMP_FUT.RENDIMIENTOS_DETALLE) N 
      FROM ('||UN_STRSQL||')  INVERSIONES_TMP_FUT';

      EXECUTE IMMEDIATE MI_CONSULTASUP INTO MI_G, MI_I, MI_K, MI_M, MI_N;


      MI_MENSAJE := MI_MENSAJE 
                    ||'D'||CHR(9)
                    ||'VAL'||CHR(9)
                    ||'1'||CHR(9)
                    ||'1'||CHR(9)
                    ||'1'||CHR(9)
                    ||'ND'||CHR(9)
                    ||'0'||CHR(9)
                    ||'0'||CHR(9)
                    ||TO_CHAR(SYSDATE,'DD-MM-YYYY')||CHR(9)
                    ||TO_CHAR(SYSDATE,'DD-MM-YYYY')||CHR(9)
                    ||MI_G||CHR(9)
                    ||'0'||CHR(9)
                    ||MI_I||CHR(9)
                    ||TO_CHAR(SYSDATE,'DD-MM-YYYY')||CHR(9)
                    ||'0'||CHR(9)
                    ||MI_K||CHR(9)
                    ||TO_CHAR(SYSDATE,'DD-MM-YYYY')||CHR(9)
                    ||'0'||CHR(9)
                    ||MI_M||CHR(9)
                    ||MI_N||CHR(9)
                    ||CHR(10);

      OPEN MI_RS FOR UN_STRSQL; 
          LOOP
      FETCH MI_RS
      INTO  MI_V_CONCEPTO, 
            MI_V_NUMERO, 
            MI_V_TIPO_INVERSION, 
            MI_V_MONEDA, 
            MI_V_ENTIDAD,
            MI_V_ENTIDADINTER,
            MI_V_PLAZO,         
            MI_V_TASA_CUPO,
            MI_V_FECHA_EMISION,
            MI_V_FECHA_COMP,
            MI_V_VALOR_NOMINAL,
            MI_V_TASA_COMPRA,
            MI_V_VALOR_PAGA,
            MI_V_FECHA_VENCIMIENTO,
            MI_V_INVERSION_VENDID,
            MI_V_VALOR_VNT,
            MI_V_FECHA_VNT,  
            MI_V_TASA_VENTA,
            MI_V_TASA_RENDIMIENTO,
            MI_V_RENDIMIENTOS_DET;
      EXIT WHEN MI_RS%NOTFOUND;

          MI_MENSAJE := MI_MENSAJE 
                        ||'D'||CHR(9)
                        ||MI_V_CONCEPTO||CHR(9)
                        ||MI_V_NUMERO||CHR(9)
                        ||MI_V_TIPO_INVERSION||CHR(9)
                        ||MI_V_MONEDA||CHR(9)
                        ||MI_V_ENTIDAD||CHR(9)
                        ||MI_V_ENTIDADINTER||CHR(9)
                        ||MI_V_PLAZO||CHR(9)
                        ||MI_V_TASA_CUPO||CHR(9)
                        ||MI_V_FECHA_EMISION||CHR(9)
                        ||MI_V_FECHA_COMP||CHR(9)
                        ||MI_V_VALOR_NOMINAL||CHR(9)
                        ||MI_V_TASA_COMPRA||CHR(9)
                        ||MI_V_VALOR_PAGA||CHR(9)
                        ||MI_V_FECHA_VENCIMIENTO||CHR(9)
                        ||MI_V_INVERSION_VENDID||CHR(9)
                        ||MI_V_VALOR_VNT||CHR(9)
                        ||MI_V_FECHA_VNT||CHR(9)
                        ||MI_V_TASA_VENTA||CHR(9)
                        ||MI_V_TASA_RENDIMIENTO||CHR(9)
                        ||MI_V_RENDIMIENTOS_DET||CHR(9)
                        ||CHR(10);
      END LOOP;
      CLOSE MI_RS;

      RETURN MI_MENSAJE;
END FC_INVERSIONES_TEMPORALES;

  --9
  FUNCTION FC_PREPARARFUENTE 
    /*
      NAME              : FC_PREPARARFUENTE --> EN ACCESS preparar_Click
      AUTHORS           : STEFANINI SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 29/03/2017
      TIME              : 02:45 PM
      SOURCE MODULE     : SysmanChip2017.02.03
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Evalúa las fuentes configuradas en el Año Origen y Verifica si ya existen en el Año Destino. 
                          Si en el año destino aún no se encuentra configurada ninguna fuente, se pasa la información existente en el año origen al año destino.
                          Si ya existen cuentas en el año destino, se evalúa si se debe realizar una actualización o una inserción de las fuentes.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación
                          UN_ANIO        => Año en el cual se está trabajando la configuración de Fuentes FUT
                          UN_ANIODESTINO => Año destino es el año que se desea preparar basándose en la informacion del año que se está trabajando
      @NAME  :  prepararFuente
      @METHOD:  GET     
    */ 
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO             IN  PCK_SUBTIPOS.TI_ANIO,
    UN_ANIODESTINO      IN  PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER 
  AS 
      MI_AUX            VARCHAR2(1 CHAR);
      MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;  
      MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
      MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
      MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGEEXISTE;    
      MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
      MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
      BEGIN
         SELECT   
               DISTINCT 'X' AUX 
          INTO 
               MI_AUX 
          FROM  
               FUENTE_RECURSOS  
         WHERE  
               COMPANIA =  UN_COMPANIA    
           AND ANO      =  UN_ANIODESTINO;    

      EXCEPTION WHEN NO_DATA_FOUND THEN 
          BEGIN 
              BEGIN 
                  MI_TABLA   := 'FUENTE_RECURSOS';
                  MI_CAMPOS  := ' COMPANIA, ' ||
                                ' CODIGO, ' ||
                                ' ANO, ' ||
                                ' NOMBRE, ' ||
                                ' TIPO, ' ||
                                ' CODIGO_FUT, ' ||
                                ' CODIGO_FUTCF ';
                  MI_VALORES := ' SELECT ' ||
                                '       COMPANIA, ' ||
                                '       CODIGO, ' ||
                                '  ' || UN_ANIODESTINO ||' ANO, ' ||
                                '       NOMBRE, ' ||
                                '       TIPO, ' ||
                                '       CODIGO_FUT, ' ||
                                '       CODIGO_FUTCF ' ||
                                '  FROM ' ||
                                '       FUENTE_RECURSOS ' ||
                                ' WHERE ' ||
                                '       COMPANIA = ''' || UN_COMPANIA ||''' ' ||
                                '   AND ANO      = ' || UN_ANIO;           
                  MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                UN_ACCION    => 'IS', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);       

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
              MI_MSGERROR(1).CLAVE := 'ANIO';
              MI_MSGERROR(1).VALOR :=  UN_ANIO;
              MI_MSGERROR(2).CLAVE := 'ANIODES';
              MI_MSGERROR(2).VALOR :=  UN_ANIODESTINO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_INSFUENTES,
                                UN_TABLAERROR => MI_TABLA,
                                UN_REEMPLAZOS => MI_MSGERROR
                                );  
          END;  
      END;

      IF MI_AUX IS NOT NULL THEN
        BEGIN
          BEGIN
            MI_TABLA        := 'FUENTE_RECURSOS';
            MI_MERGEUSING   :=  'SELECT  ' ||
                                '      COMPANIA,  ' ||
                                       UN_ANIODESTINO ||' ANO, ' ||
                                '      CODIGO,  ' ||
                                '      CODIGO_FUT,  ' ||
                                '      CODIGO_FUTCF,     ' ||
                                '      NOMBRE,  ' ||
                                '      TIPO  ' ||
                                ' FROM  ' ||
                                '      FUENTE_RECURSOS ' ||
                                'WHERE  ' ||
                                '      COMPANIA = ''' || UN_COMPANIA || ''' ' ||
                                '  AND ANO      = ' || UN_ANIO || ' ' ||
                                'ORDER BY CODIGO';
            MI_MERGEENLACE  :=  '     TABLA.COMPANIA = VISTA.COMPANIA ' ||
                                ' AND TABLA.ANO      = VISTA.ANO      ' ||
                                ' AND TABLA.CODIGO   = VISTA.CODIGO   ';
            MI_MERGEEXISTE  :=  'UPDATE ' ||
                                '   SET TABLA.CODIGO_FUT   = VISTA.CODIGO_FUT,  ' ||
                                '       TABLA.CODIGO_FUTCF = VISTA.CODIGO_FUTCF ';
            MI_MERGENOEXIS  :=  ' INSERT ' ||
                                '   ( ' ||
                                '     COMPANIA, ' ||
                                '     CODIGO, ' ||
                                '     ANO, ' ||
                                '     NOMBRE, ' ||
                                '     TIPO, ' ||
                                '     CODIGO_FUT, ' ||
                                '     CODIGO_FUTCF ' ||
                                '   ) ' ||
                                '   VALUES ' ||
                                '   ( ' ||
                                '     VISTA.COMPANIA, ' ||
                                '     VISTA.CODIGO, ' ||
                                '     VISTA.ANO, ' ||
                                '     VISTA.NOMBRE, ' ||
                                '     VISTA.TIPO, ' ||
                                '     VISTA.CODIGO_FUT, ' ||
                                '     VISTA.CODIGO_FUTCF ' ||
                                '   ) ';
            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                        UN_ACCION      => 'IM',
                                        UN_MERGEUSING  => MI_MERGEUSING,
                                        UN_MERGEENLACE => MI_MERGEENLACE,
                                        UN_MERGEEXISTE => MI_MERGEEXISTE,
                                        UN_MERGENOEXIS => MI_MERGENOEXIS);  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                     RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
          MI_MSGERROR(1).CLAVE := 'ANIO';
          MI_MSGERROR(1).VALOR :=  UN_ANIO;
          MI_MSGERROR(2).CLAVE := 'ANIODES';
          MI_MSGERROR(2).VALOR :=  UN_ANIODESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_PREPFUENTES,
                            UN_TABLAERROR => MI_TABLA,
                            UN_REEMPLAZOS => MI_MSGERROR
                            );   
        END;
      END IF;

    RETURN MI_RTA;
  END FC_PREPARARFUENTE;


FUNCTION FC_TRASLADAR_CONF_SIG_ANO 
 (
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO              IN PCK_SUBTIPOS.TI_ANIO 
 )
RETURN VARCHAR2 
AS 
    MI_TABLAMERGE   PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_ANO_DESTINO  PCK_SUBTIPOS.TI_ANIO;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_REGISTROS_CODIGOS_FUT   PCK_SUBTIPOS.TI_ENTERO;
    MI_REGISTROS_FUENTES_FUT   PCK_SUBTIPOS.TI_ENTERO;
BEGIN
MI_ANO_DESTINO:=UN_ANIO+1;
BEGIN
SELECT COUNT(*)
  INTO MI_REGISTROS_CODIGOS_FUT
  FROM CODIGOSFUT 
 WHERE COMPANIA = UN_COMPANIA 
   AND      ANO = MI_ANO_DESTINO;
EXCEPTION WHEN NO_DATA_FOUND THEN 
MI_REGISTROS_CODIGOS_FUT:=0;
END ;

IF MI_REGISTROS_CODIGOS_FUT = 0 THEN 
    RETURN 'TB_TB3039' ;
END IF;

BEGIN 
SELECT COUNT(*)
  INTO MI_REGISTROS_FUENTES_FUT
  FROM FUENTESFUT
 WHERE COMPANIA = UN_COMPANIA 
   AND      ANO = MI_ANO_DESTINO;
EXCEPTION WHEN NO_DATA_FOUND THEN 
MI_REGISTROS_FUENTES_FUT:=0;
END;

IF MI_REGISTROS_FUENTES_FUT = 0 THEN 
    RETURN 'TB_TB3040' ;
END IF;

BEGIN 
    BEGIN

        MI_TABLAMERGE := 'PLAN_PPTAL_CONFIG';
        MI_MERGEUSING := 'SELECT 
                                 PLAN_PPTAL_CONFIG.COMPANIA,
                                 '||MI_ANO_DESTINO||' ANO,
                                 PLAN_PPTAL_CONFIG.CENTRO_COSTO,
                                 PLAN_PPTAL_CONFIG.TERCERO,
                                 PLAN_PPTAL_CONFIG.SUCURSAL,
                                 PLAN_PPTAL_CONFIG.AUXILIAR,
                                 PLAN_PPTAL_CONFIG.REFERENCIA,
                                 PLAN_PPTAL_CONFIG.FUENTE_RECURSO,
                                 PLAN_PPTAL_CONFIG.CODIGO,
                                 PLAN_PPTAL_CONFIG.DESTINO,
                                 PLAN_PPTAL_CONFIG.CODIGOFUT_H,
                                 PLAN_PPTAL_CONFIG.FUENTE_FUT,
                                 PLAN_PPTAL_CONFIG.UNIDADEJECUTORA_FUT
                            FROM PLAN_PPTAL_CONFIG 
                           WHERE PLAN_PPTAL_CONFIG.COMPANIA='''||UN_COMPANIA||'''
                             AND PLAN_PPTAL_CONFIG.ANO   = '||UN_ANIO;

            MI_MERGEENLACE := '     TABLA.COMPANIA       = VISTA.COMPANIA
                                AND TABLA.ANO            = VISTA.ANO
                                AND TABLA.CODIGO         = VISTA.CODIGO
                                AND TABLA.DESTINO        = VISTA.DESTINO
                                AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                                AND TABLA.TERCERO        = VISTA.TERCERO
                                AND TABLA.SUCURSAL       = VISTA.SUCURSAL
                                AND TABLA.AUXILIAR       = VISTA.AUXILIAR
                                AND TABLA.REFERENCIA     = VISTA.REFERENCIA
                                AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO';

          MI_MERGEEXISTE :=' UPDATE SET TABLA.CODIGOFUT_H         = VISTA.CODIGOFUT_H, 
                                        TABLA.FUENTE_FUT          = VISTA.FUENTE_FUT,
                                        TABLA.UNIDADEJECUTORA_FUT = VISTA.UNIDADEJECUTORA_FUT 
                                  WHERE TABLA.COMPANIA = '''||UN_COMPANIA||'''' ;

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLAMERGE,
                                               UN_ACCION      => 'MM',
                                               UN_MERGEUSING  => MI_MERGEUSING,
                                               UN_MERGEENLACE => MI_MERGEENLACE,
                                               UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END; 
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
      MI_MSGERROR(1).CLAVE := 'ANO';
      MI_MSGERROR(1).VALOR := UN_ANIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_ENTES_TRASLADAR_SIG_ANO,
                                 UN_REEMPLAZOS  => MI_MSGERROR,
                                 UN_TABLAERROR => 'SP_D_ABONOS');
    END;  



  RETURN  PCK_DATOS.GL_RTA;
END FC_TRASLADAR_CONF_SIG_ANO;


--11
FUNCTION FC_PREPARA_RECURSOS_TERCEROS
  /*
    OBJETIVO              : Ajustar las retenciones en los egresos de los detalles de los comprobantes.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_USUARIO : Nombre del usuario que esta interactuando en el formulario.
                            UN_FECHAINI: Cadena con la fecha inicial dd/mm/yyyy.
                            UN_FECHAFIN: Cadena con la fecha final + un dia.
    PARÁMETROS DE SALIDA  : Consulta equivalente a la tabla temporal TMPLisDescuentosPorPagar en Access. 
    MODULO                : SysmanChip2017.02.03
    NOMBRE EN ACCESS      : prepara_recursos_terceros

    AUTOR                 : STEFANINI SYSMAN
    AUTOR MIGRACIÓN       : PABLO ANDRES ESPITIA CUCA
    FECHA MIGRACIÓN       : 29/03/2017 
    HORA MIGRACIÓN        : 14:35

    AUTOR MODIFICACIÓN    :
    FECHA MODIFICACIÓN    :
    OBJETIVO MODIFICACIÓN :

    @NAME: prepararRecursosTerceros
    @METHOD: PUT
  */
(
   UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  ,UN_FECHAINI IN DATE
  ,UN_FECHAFIN IN DATE
)
RETURN CLOB
AS 
  MI_FECHAINI    DATE;
  MI_FECHAFIN    DATE;
  MI_PARMANEJA   PCK_SUBTIPOS.TI_PARAMETRO;
  MI_TABLA       PCK_SUBTIPOS.TI_CONSULTA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  MI_MERGEUSING  PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ANIO        PCK_SUBTIPOS.TI_ANIO;
  MI_RTA         PCK_SUBTIPOS.TI_RTA_ACME;

BEGIN
  MI_FECHAINI := TO_DATE(UN_FECHAINI);
  MI_FECHAFIN := TO_DATE(UN_FECHAFIN,'DD/MM/YYYY');
  MI_ANIO     := EXTRACT(YEAR FROM MI_FECHAINI);
  MI_TABLA := 'DETALLE_COMPROBANTE_CNT';  

  MI_PARMANEJA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                           ,UN_NOMBRE   => 'MANEJA RETENCIONES EN EGRESO'
                                           ,UN_MODULO   => PCK_DATOS.FC_MODULOENTESDECONTROL
                                           ,UN_FECHA_PAR=> SYSDATE)
                     ,'NO');

  IF MI_PARMANEJA IN ('SI') THEN
    BEGIN
      BEGIN   
        MI_MERGEUSING := 'SELECT DISTINCT
                             DETALLE_COMPROBANTE_CNT.COMPANIA
                            ,DETALLE_COMPROBANTE_CNT.ANO
                            ,DETALLE_COMPROBANTE_CNT.TIPO_CPTE
                            ,DETALLE_COMPROBANTE_CNT.COMPROBANTE
                            ,DETALLE_COMPROBANTE_CNT.CONSECUTIVO
                            ,DETALLE_COMPROBANTE_CNT.CUENTA
                            ,DETALLE_COMPROBANTE_CNT.FECHA
                          FROM DETALLE_COMPROBANTE_CNT
                            INNER JOIN PLAN_CONTABLE 
                               ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                              AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                              AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                            INNER JOIN TIPO_COMPROBANTE 
                               ON DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                            INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE 
                               ON DETALLE_COMPROBANTE_CNT.COMPANIA    = DETALLE.COMPANIA
                              AND DETALLE_COMPROBANTE_CNT.ANO         = DETALLE.ANO
                              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = DETALLE.TIPO_CPTE
                              AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = DETALLE.COMPROBANTE
                            INNER JOIN PLAN_CONTABLE PLAN 
                               ON DETALLE.COMPANIA = PLAN.COMPANIA
                              AND DETALLE.ANO      = PLAN.ANO
                              AND DETALLE.CUENTA   = PLAN.CODIGO 
                          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                            AND DETALLE_COMPROBANTE_CNT.FECHA BETWEEN TO_DATE('''||MI_FECHAINI||''',''DD/MM/YYYY'') 
                                                                  AND TO_DATE('''||MI_FECHAFIN||''',''DD/MM/YYYY'')
                            AND TIPO_COMPROBANTE.CLASE_CONTABLE IN (''E'')
                            AND PLAN_CONTABLE.CLASECUENTA       IN (''B'')
                            AND PLAN.CLASECUENTA                IN (''I'')
                            AND DETALLE.BANCO_RETENCION IS NULL ';

        MI_MERGEENLACE := ' VISTA.COMPANIA    = TABLA.COMPANIA
                        AND VISTA.ANO         = TABLA.ANO
                        AND VISTA.TIPO_CPTE   = TABLA.TIPO_CPTE
                        AND VISTA.COMPROBANTE = TABLA.COMPROBANTE
                        AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO ';

        MI_MERGEEXISTE := ' UPDATE SET BANCO_RETENCION      = VISTA.CUENTA
                                      ,PAGO_RETENCION       = -1
                                      ,FECHA_PAGO_RETENCION = VISTA.FECHA ';

        /*RETENCION EN EL EGRESO*/
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA
                                             ,UN_ACCION      => 'MM'
                                             ,UN_MERGEUSING  => MI_MERGEUSING
                                             ,UN_MERGEENLACE => MI_MERGEENLACE
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE
                THEN RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
      MI_MSGERROR(1).CLAVE := 'FECHAINI';
      MI_MSGERROR(1).VALOR := MI_FECHAINI;
      MI_MSGERROR(2).CLAVE := 'FECHAFIN';
      MI_MSGERROR(2).VALOR := MI_FECHAFIN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_M_PRT_RETENCEGRESO
                                ,UN_TABLAERROR=> MI_TABLA
                                ,UN_REEMPLAZOS=> MI_MSGERROR);
    END;

    MI_RTA := ' SELECT DISTINCT 
                   LISDESCUENTOSPORPAGAR.COMPANIA
                  ,LISDESCUENTOSPORPAGAR.ANO
                  ,LISDESCUENTOSPORPAGAR.CUENTA
                  ,SUM(LISDESCUENTOSPORPAGAR.VALOR_CREDITO) AS VALOR_CREDITO
                FROM (
                SELECT 
                   '''||UN_USUARIO||'''  AS USUARIO
                  ,DC.COMPANIA
                  ,DC.BANCO_RETENCION    AS CUENTA
                  ,DC.ANO
                  ,DC.FECHA
                  ,DC.TIPO_CPTE
                  ,DC.COMPROBANTE
                  ,DC.TERCERO
                  ,DC.SUCURSAL
                  ,DC.CUENTA             AS CUENTARETE
                  ,DC.FECHA              AS FECHA_PAGO_RETENCION
                  ,SUM(DC.VALOR_CREDITO) AS VALOR_CREDITO
                FROM COMPROBANTE_CNT CC 
                  INNER JOIN DETALLE_COMPROBANTE_CNT DC 
                     ON CC.COMPANIA = DC.COMPANIA
                    AND CC.ANO      = DC.ANO
                    AND CC.TIPO     = DC.TIPO_CPTE
                    AND CC.NUMERO   = DC.COMPROBANTE
                  INNER JOIN PLAN_CONTABLE PC
                     ON DC.COMPANIA = PC.COMPANIA
                    AND DC.ANO      = PC.ANO
                    AND DC.CUENTA   = PC.CODIGO
                WHERE DC.COMPANIA ='''||UN_COMPANIA||'''
                  AND DC.FECHA BETWEEN TO_DATE('''||MI_FECHAINI||''',''DD/MM/YYYY'') AND TO_DATE('''||MI_FECHAFIN||''',''DD/MM/YYYY'')
                  AND PC.CLASECUENTA IN (''I'')
                GROUP BY 
                   '''||UN_USUARIO||'''
                  ,DC.COMPANIA
                  ,DC.BANCO_RETENCION
                  ,DC.ANO
                  ,DC.FECHA
                  ,DC.TIPO_CPTE
                  ,DC.COMPROBANTE
                  ,DC.TERCERO
                  ,DC.SUCURSAL
                  ,DC.CUENTA
                  ,DC.FECHA
                )LISDESCUENTOSPORPAGAR 
                  INNER JOIN PLAN_CONTABLE PLAN1
                     ON LISDESCUENTOSPORPAGAR.COMPANIA = PLAN1.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.ANO      = PLAN1.ANO
                    AND LISDESCUENTOSPORPAGAR.CUENTA   = PLAN1.CODIGO
                  INNER JOIN TERCERO 
                     ON LISDESCUENTOSPORPAGAR.COMPANIA = TERCERO.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.TERCERO  = TERCERO.NIT
                    AND LISDESCUENTOSPORPAGAR.SUCURSAL = TERCERO.SUCURSAL
                  INNER JOIN PLAN_CONTABLE PLAN2
                     ON LISDESCUENTOSPORPAGAR.COMPANIA   = PLAN2.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.ANO        = PLAN2.ANO
                    AND LISDESCUENTOSPORPAGAR.CUENTARETE = PLAN2.CODIGO
                WHERE LISDESCUENTOSPORPAGAR.COMPANIA='''||UN_COMPANIA||'''
                  AND LISDESCUENTOSPORPAGAR.USUARIO ='''||UN_USUARIO||'''
                GROUP BY 
                   LISDESCUENTOSPORPAGAR.COMPANIA
                  ,LISDESCUENTOSPORPAGAR.ANO
                  ,LISDESCUENTOSPORPAGAR.CUENTA ';
  ELSE
    BEGIN
      BEGIN
        MI_MERGEUSING := 'SELECT
                             COMPROBANTE.COMPANIA
                            ,COMPROBANTE.ANO 
                            ,COMPROBANTE.TIPO
                            ,COMPROBANTE.NUMERO
                            ,DCC.CONSECUTIVO
                          FROM DETALLE_COMPROBANTE_CNT DCC
                            INNER JOIN COMPROBANTE_CNT COMPROBANTE 
                               ON DCC.COMPANIA        = COMPROBANTE.COMPANIA
                              AND DCC.TIPO_CPTE_AFECT = COMPROBANTE.TIPO
                              AND DCC.CMPTE_AFECTADO  = COMPROBANTE.NUMERO 
                          WHERE DCC.COMPANIA ='''||UN_COMPANIA||''' 
                            AND DDC.ANO      =  '||MI_ANIO||'
                            AND DCC.ANO_AFECT IS NULL ';

        MI_MERGEENLACE := ' VISTA.COMPANIA    = TABLA.COMPANIA
                        AND VISTA.ANO         = TABLA.ANO
                        AND VISTA.TIPO        = TABLA.TIPO_CPTE
                        AND VISTA.NUMERO      = TABLA.COMPROBANTE
                        AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO ';

        MI_MERGEEXISTE := ' UPDATE SET ANO_AFECT = VISTA.ANO ';                           

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA
                                             ,UN_ACCION      => 'MM'
                                             ,UN_MERGEUSING  => MI_MERGEUSING
                                             ,UN_MERGEENLACE => MI_MERGEENLACE
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE 
                THEN RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
      MI_MSGERROR(1).CLAVE := 'ANIO';
      MI_MSGERROR(1).VALOR := MI_ANIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_M_PRT_ANOAFE
                                ,UN_TABLAERROR=> MI_TABLA
                                ,UN_REEMPLAZOS=> MI_MSGERROR);
    END;

    <<DETCOMPROBANTES>>
    FOR MI_DET IN (
      SELECT 
         DETALLE_COMPROBANTE_CNT.ANO
        ,DETALLE_COMPROBANTE_CNT.TIPO_CPTE
        ,DETALLE_COMPROBANTE_CNT.COMPROBANTE
      FROM DETALLE_COMPROBANTE_CNT 
        INNER JOIN PLAN_CONTABLE 
           ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
          AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
        INNER JOIN TIPO_COMPROBANTE 
           ON DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
        INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE 
           ON PLAN_CONTABLE.COMPANIA                  = DETALLE.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO_AFECT       = DETALLE.ANO
          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT = DETALLE.TIPO_CPTE
          AND DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO  = DETALLE.COMPROBANTE
        INNER JOIN PLAN_CONTABLE PLAN 
           ON DETALLE.COMPANIA = PLAN.COMPANIA
          AND DETALLE.ANO      = PLAN.ANO
          AND DETALLE.CUENTA   = PLAN.CODIGO
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
        AND DETALLE_COMPROBANTE_CNT.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN
        AND DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO NOT IN (0) 
        AND PLAN_CONTABLE.CLASECUENTA                  IN ('B')
        AND PLAN.CLASECUENTA                           IN ('I')
        AND TIPO_COMPROBANTE.CLASE_CONTABLE            IN ('E')
        AND DETALLE.BANCO_RETENCION IS NULL
      GROUP BY 
         DETALLE_COMPROBANTE_CNT.ANO
        ,DETALLE_COMPROBANTE_CNT.TIPO_CPTE
        ,DETALLE_COMPROBANTE_CNT.COMPROBANTE
    ) LOOP
      BEGIN
        BEGIN
          MI_MERGEUSING := 'SELECT 
                               DETALLE1.COMPANIA
                              ,DETALLE1.ANO
                              ,DETALLE1.TIPO_CPTE
                              ,DETALLE1.COMPROBANTE
                              ,DETALLE1.CONSECUTIVO
                              ,DETALLE1.CUENTA
                              ,DETALLE1.FECHA
                            FROM DETALLE_COMPROBANTE_CNT DETALLE1
                              INNER JOIN PLAN_CONTABLE PLAN1
                                 ON DETALLE1.COMPANIA = PLAN1.COMPANIA
                                AND DETALLE1.ANO      = PLAN1.ANO 
                                AND DETALLE1.CUENTA   = PLAN1.CODIGO
                              INNER JOIN TIPO_COMPROBANTE 
                                 ON DETALLE1.COMPANIA = TIPO_COMPROBANTE.COMPANIA
                                AND DETALLE1.TIPO_CPTE= TIPO_COMPROBANTE.CODIGO
                              INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE2 
                                 ON DETALLE1.COMPANIA        = DETALLE2.COMPANIA
                                AND DETALLE1.ANO             = DETALLE2.ANO
                                AND DETALLE1.TIPO_CPTE_AFECT = DETALLE2.TIPO_CPTE
                                AND DETALLE1.CMPTE_AFECTADO  = DETALLE2.COMPROBANTE
                              INNER JOIN PLAN_CONTABLE PLAN2 
                                 ON DETALLE2.COMPANIA = PLAN2.COMPANIA
                                AND DETALLE2.ANO      = PLAN2.ANO
                                AND DETALLE2.CUENTA   = PLAN2.CODIGO 
                            WHERE DETALLE1.COMPANIA   ='''||UN_COMPANIA||'''
                              AND DETALLE1.ANO        =  '||MI_ANIO||'
                              AND DETALLE1.TIPO_CPTE                IN ('''||MI_DET.TIPO_CPTE||''')
                              AND DETALLE1.COMPROBANTE=  '||MI_DET.COMPROBANTE||'
                              AND DETALLE1.FECHA BETWEEN TO_DATE('''||MI_FECHAINI||''',''DD/MM/YYYY'') AND TO_DATE('''||MI_FECHAFIN||''',''DD/MM/YYYY'') 
                              AND DETALLE2.BANCO_RETENCION IS NULL
                              AND PLAN1.CLASECUENTA                 IN (''B'')
                              AND PLAN2.CLASECUENTA                 IN (''I'')
                              AND TIPO_COMPROBANTE.CLASE_CONTABLE   IN (''E'')      ';

          MI_MERGEENLACE := ' VISTA.COMPANIA    = TABLA.COMPANIA
                          AND VISTA.ANO         = TABLA.ANO
                          AND VISTA.TIPO_CPTE   = TABLA.TIPO_CPTE
                          AND VISTA.COMPROBANTE = TABLA.COMPROBANTE
                          AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO ';

          MI_MERGEEXISTE := ' UPDATE SET BANCO_RETENCION      = VISTA.CUENTA
                                        ,PAGO_RETENCION       = -1
                                        ,FECHA_PAGO_RETENCION = VISTA.FECHA ';      

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA
                                               ,UN_ACCION      => 'MM'
                                               ,UN_MERGEUSING  => MI_MERGEUSING
                                               ,UN_MERGEENLACE => MI_MERGEENLACE
                                               ,UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;

        BEGIN
          MI_MERGEUSING := 'SELECT 
                              DETALLE2.COMPANIA
                             ,DETALLE2.ANO 
                             ,DETALLE2.TIPO_CPTE
                             ,DETALLE2.COMPROBANTE
                             ,DETALLE3.CUENTA
                             ,DETALLE1.FECHA
                            FROM DETALLE_COMPROBANTE_CNT DETALLE1
                              INNER JOIN PLAN_CONTABLE PLAN1
                                 ON DETALLE1.COMPANIA = PLAN1.COMPANIA
                                AND DETALLE1.ANO      = PLAN1.ANO 
                                AND DETALLE1.CUENTA   = PLAN1.CODIGO
                              INNER JOIN TIPO_COMPROBANTE 
                                 ON DETALLE1.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                AND DETALLE1.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                              INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE2 
                                 ON DETALLE1.COMPANIA        = DETALLE2.COMPANIA
                                AND DETALLE1.TIPO_CPTE_AFECT = DETALLE2.TIPO_CPTE
                                AND DETALLE1.CMPTE_AFECTADO  = DETALLE2.COMPROBANTE
                              INNER JOIN PLAN_CONTABLE PLAN2 
                                 ON DETALLE2.COMPANIA = PLAN2.COMPANIA
                                AND DETALLE2.ANO      = PLAN2.ANO
                                AND DETALLE2.CUENTA   = PLAN2.CODIGO
                              INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE3 
                                 ON DETALLE1.COMPANIA    = DETALLE3.COMPANIA
                                AND DETALLE1.ANO         = DETALLE3.ANO
                                AND DETALLE1.TIPO_CPTE   = DETALLE3.TIPO_CPTE
                                AND DETALLE1.COMPROBANTE = DETALLE3.COMPROBANTE
                              INNER JOIN PLAN_CONTABLE PLAN3 
                                 ON DETALLE3.COMPANIA = PLAN3.COMPANIA
                                AND DETALLE3.ANO      = PLAN3.ANO
                                AND DETALLE3.CUENTA   = PLAN3.CODIGO 
                            WHERE DETALLE1.COMPANIA   ='''||UN_COMPANIA||'''
                              AND DETALLE1.ANO        =  '||MI_ANIO||'
                              AND DETALLE1.TIPO_CPTE              IN ('''||MI_DET.TIPO_CPTE||''')
                              AND DETALLE1.COMPROBANTE=  '||MI_DET.COMPROBANTE||'
                              AND DETALLE1.CMPTE_AFECTADO NOT IN(0)
                              AND DETALLE1.FECHA BETWEEN TO_DATE('''||MI_FECHAINI||''',''DD/MM/YYYY'') 
                                                     AND TO_DATE('''||MI_FECHAFIN||''',''DD/MM/YYYY'')
                              AND DETALLE2.BANCO_RETENCION IS NULL
                              AND PLAN2.CLASECUENTA               IN (''I'')
                              AND PLAN3.CLASECUENTA               IN (''B'')
                              AND TIPO_COMPROBANTE.CLASE_CONTABLE IN (''E'') ';

          MI_MERGEENLACE := 'VISTA.COMPANIA    = TABLA.COMPANIA
                         AND VISTA.ANO         = TABLA.ANO
                         AND VISTA.TIPO_CPTE   = TABLA.TIPO_CPTE
                         AND VISTA.COMPROBANTE = TABLA.COMPROBANTE ';

          MI_MERGEEXISTE := ' UPDATE SET BANCO_RETENCION      = VISTA.CUENTA
                                        ,PAGO_RETENCION       = -1
                                        ,FECHA_PAGO_RETENCION = VISTA.FECHA ';        

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'MM'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
        MI_MSGERROR(1).CLAVE := 'ANIO';
        MI_MSGERROR(1).VALOR := MI_ANIO;
        MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
        MI_MSGERROR(2).VALOR := MI_DET.COMPROBANTE;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_ENTES_M_PRT_BANPAGFEC
                                  ,UN_TABLAERROR=> MI_TABLA
                                  ,UN_REEMPLAZOS=> MI_MSGERROR);
      END;      
    END LOOP DETCOMPROBANTES;

    MI_RTA := ' SELECT DISTINCT 
                   LISDESCUENTOSPORPAGAR.COMPANIA
                  ,LISDESCUENTOSPORPAGAR.ANO
                  ,LISDESCUENTOSPORPAGAR.CUENTA
                  ,SUM(LISDESCUENTOSPORPAGAR.VALOR_CREDITO) AS VALOR_CREDITO
                FROM (
                SELECT DISTINCT 
                   '''||UN_USUARIO||''' AS USUARIO
                  ,DETALLE1.COMPANIA
                  ,DETALLE1.ANO
                  ,DETALLE2.BANCO_RETENCION
                  ,DETALLE1.FECHA
                  ,DETALLE1.TIPO_CPTE_AFECT
                  ,DETALLE1.CMPTE_AFECTADO
                  ,DETALLE2.TERCERO
                  ,DETALLE2.SUCURSAL
                  ,DETALLE2.VALOR_CREDITO
                  ,DETALLE2.CUENTA
                FROM DETALLE_COMPROBANTE_CNT DETALLE1
                  INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE2 
                     ON DETALLE1.COMPANIA        = DETALLE2.COMPANIA
                    AND DETALLE1.TIPO_CPTE_AFECT = DETALLE2.TIPO_CPTE
                    AND DETALLE1.CMPTE_AFECTADO  = DETALLE2.COMPROBANTE
                  INNER JOIN PLAN_CONTABLE PC
                     ON DETALLE2.COMPANIA = PC.COMPANIA
                    AND DETALLE2.ANO      = PC.ANO
                    AND DETALLE2.CUENTA   = PC.ID
                  INNER JOIN TIPO_COMPROBANTE TC
                     ON DETALLE1.COMPANIA  = TC.COMPANIA
                    AND DETALLE1.TIPO_CPTE = TC.CODIGO
                WHERE DETALLE1.COMPANIA='''||UN_COMPANIA||'''
                  AND DETALLE1.FECHA BETWEEN TO_DATE('''||UN_FECHAINI||''',''DD/MM/YYYY'') AND TO_DATE('''||UN_FECHAFIN||''',''DD/MM/YYYY'')
                  AND DETALLE2.FECHA_PAGO_RETENCION BETWEEN TO_DATE('''||UN_FECHAINI||''',''DD/MM/YYYY'') 
                                                        AND TO_DATE('''||UN_FECHAFIN||''',''DD/MM/YYYY'')
                  AND TC.CLASE_CONTABLE IN (''E'')
                  AND PC.CLASECUENTA    IN (''I'')
                )LISDESCUENTOSPORPAGAR 
                  INNER JOIN PLAN_CONTABLE PLAN1
                     ON LISDESCUENTOSPORPAGAR.COMPANIA = PLAN1.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.ANO      = PLAN1.ANO
                    AND LISDESCUENTOSPORPAGAR.CUENTA   = PLAN1.CODIGO
                  INNER JOIN TERCERO 
                     ON LISDESCUENTOSPORPAGAR.COMPANIA = TERCERO.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.TERCERO  = TERCERO.NIT
                    AND LISDESCUENTOSPORPAGAR.SUCURSAL = TERCERO.SUCURSAL
                  INNER JOIN PLAN_CONTABLE PLAN2
                     ON LISDESCUENTOSPORPAGAR.COMPANIA   = PLAN2.COMPANIA
                    AND LISDESCUENTOSPORPAGAR.ANO        = PLAN2.ANO
                    AND LISDESCUENTOSPORPAGAR.CUENTARETE = PLAN2.CODIGO
                WHERE LISDESCUENTOSPORPAGAR.COMPANIA='''||UN_COMPANIA||'''
                  AND LISDESCUENTOSPORPAGAR.USUARIO ='''||UN_USUARIO||'''
                GROUP BY 
                   LISDESCUENTOSPORPAGAR.COMPANIA
                  ,LISDESCUENTOSPORPAGAR.ANO
                  ,LISDESCUENTOSPORPAGAR.CUENTA';
  END IF;

  RETURN MI_RTA;
END FC_PREPARA_RECURSOS_TERCEROS;


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
      @NAME  :  generarPlanoFutSinMovimientos
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
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 	   => 'PLAN_PPTAL_CONFIG',
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
							ON V_PLAN_PRESUPUESTAL.COMPANIA     = CODIGOSFUT.COMPANIA
							AND V_PLAN_PRESUPUESTAL.ANO 	      = CODIGOSFUT.ANO 
							AND V_PLAN_PRESUPUESTAL.CODIGOFUT_H = CODIGOSFUT.CODIGOFUT
					WHERE V_PLAN_PRESUPUESTAL.COMPANIA  = UN_COMPANIA
						AND V_PLAN_PRESUPUESTAL.ANO       = UN_ANIO
						AND CODIGOSFUT.MOVIMIENTO IN(0) 
						AND V_PLAN_PRESUPUESTAL.CONSECUTIVO_FUT IS NOT NULL 
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

--13
PROCEDURE PR_ELIMINARDATOSCHIP
    /*
      NAME              : FC_ELIMINARDATOSCHIP En Access InsertaDesdeArchivo() Condicion Eliminar
      AUTHOR            : ELKIN GEOVANNY AMAYA SILVA
      DATE              : 22/03/2017
      TIME              : 12:53 PM
      SOURCE MODULE     : SysmanChip2018.03.01
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Elimina los datos de una tabla enviada desde el controlador.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA => Compañia de ingreso a la aplicación
                          UN_ANIO     => Año actual
                          UN_TABLA    => Nombre de la tabla de la cual se va a borrar la información

      @NAME  :  eliminarDatosChip
      @METHOD:  GET 
    */ 
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
      UN_TABLA          IN PCK_SUBTIPOS.TI_TABLA
    )
    AS
		MI_STR           CLOB;
   	MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION		 PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;

	BEGIN 

    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' ';


    FOR MI_RS IN (SELECT *
                  FROM
                    (SELECT CNAME FROM COL WHERE TNAME LIKE UN_TABLA
                    )
                  WHERE CNAME LIKE 'ANO')LOOP

        MI_CONDICION := MI_CONDICION ||'AND ANO = '||UN_ANIO;

    END LOOP;

     BEGIN
       BEGIN
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TABLA
                                             ,UN_ACCION    => 'E'
                                             ,UN_CONDICION => MI_CONDICION);                                                           

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

         END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESCONTROL THEN
               MI_MSGERROR(1).CLAVE := 'TABLA';
               MI_MSGERROR(1).VALOR := UN_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_ENTESELIMICHIPANIO
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;   
END PR_ELIMINARDATOSCHIP;
--14
FUNCTION FC_GENERARARCHIVOSALDOMOV
(
 /*
      NAME              : FC_GENERARARCHIVOSALDOMOV 
      AUTHOR            : YESIKA PAOLA BECERRA CASTRO
      DATE              : 23/05/2018
      TIME              : 02:22 PM
      SOURCE MODULE     : SysmanChip2018.04.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de los saldos y movimientos
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_ANIO           =>Año contable del que se desea obtener la información.
                          UN_TRIMESTRE      => Trimestre para el cual se va a rendir la información del plano.
                          UN_CODIGOENTIDAD  =>  Codigo de la entidad reciproca. 
                          UN_DIGITOS        => Número de digitos para filtrar id del plan contable

      @NAME  :  generarPlanoSaldoMovimiento
      @METHOD:  GET 
    */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CODIGOENTIDAD    IN VARCHAR2,
    UN_DIGITOS          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CENTAVOS         IN PCK_SUBTIPOS.TI_LOGICO,
	UN_COVID			IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS 
    MI_RTA                  CLOB;
    MI_CADENA_UNO           CLOB;
    MI_CADENA_DOS           CLOB;
    MI_CONCEPTO             VARCHAR2(20 CHAR);
    MI_SALDOINICIAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_DEBITO               PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTALDEBITO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTAUNOINI         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTADOSINI         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTATRESINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACUATROINI      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACINCOINI       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASEISINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASIETEINI       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTAUNOFIN         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTADOSFIN         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTATRESFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACUATROFIN      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACINCOFIN       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASEISFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASIETEFIN       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CREDITO              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TOTALCREDITO         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDOFIN             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDONOCOR           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDOCOR             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIA           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIAINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIAFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TOTDEB               VARCHAR2(25 CHAR);
    MI_TOTCRE               VARCHAR2(25 CHAR);
    MI_TOTDIS               VARCHAR2(25 CHAR);
    MI_TOTCUENTAUNOINI      VARCHAR2(25 CHAR);
    MI_TOTCUENTADOSINI      VARCHAR2(25 CHAR);
    MI_TOTCUENTATRESINI     VARCHAR2(25 CHAR);
    MI_TOTCUENTACUATROINI   VARCHAR2(25 CHAR);
    MI_TOTCUENTACINCOINI    VARCHAR2(25 CHAR);
    MI_TOTCUENTASEISINI     VARCHAR2(25 CHAR);
    MI_TOTCUENTASIETEINI    VARCHAR2(25 CHAR);
    MI_TOTCUENTAUNOFIN      VARCHAR2(25 CHAR);
    MI_TOTCUENTADOSFIN      VARCHAR2(25 CHAR);
    MI_TOTCUENTATRESFIN     VARCHAR2(25 CHAR);
    MI_TOTCUENTACUATROFIN   VARCHAR2(25 CHAR);
    MI_TOTCUENTACINCOFIN    VARCHAR2(25 CHAR);
    MI_TOTCUENTASEISFIN     VARCHAR2(25 CHAR);
    MI_TOTCUENTASIETEFIN    VARCHAR2(25 CHAR);
    MI_DIFERENCIAINICIAL    VARCHAR2(25 CHAR);
    MI_DIFERENCIAFINAL      VARCHAR2(25 CHAR);
    MI_NOMCUENTAUNO         VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTADOS         VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTATRES        VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTACUATRO      VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTACINCO       VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTASEIS        VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTASIETE       VARCHAR2(50 CHAR) DEFAULT ' ';
BEGIN 

    IF UN_EXCEL = 0 THEN 
      MI_CADENA_UNO :=   'S' ||  CHR(9)|| UN_CODIGOENTIDAD ||CHR(9)||
                  '1' ||  CASE UN_TRIMESTRE   WHEN 1 THEN '0103'
                                              WHEN 2 THEN '0406'
                                              WHEN 3 THEN '0709'
                                              WHEN 4 THEN '1012'
                          END||CHR(9)||
                  UN_ANO||CHR(9)||'CGN2015_001_SALDOS_Y_MOVIMIENTOS_CONVERGENCIA'||CHR(9)||
                  TO_CHAR(SYSDATE, 'DD-MM-YYYY') ||CHR(13)||CHR(10);
    ELSE
      MI_CADENA_UNO := 'CODIGO'                  ||CHR(9)|| 
                       'CONCEPTO'                ||CHR(9);
                
          IF(UN_COVID <> 0) THEN 
            MI_CADENA_UNO :=  MI_CADENA_UNO || 'DESCRIPCION'  ||CHR(9);
         END IF;
         MI_CADENA_UNO := MI_CADENA_UNO ||
                'SALDO INICIAL'           ||CHR(9)||
                'MOVIMIENTO DEBITO'       ||CHR(9)||
                'MOVIMIENTO CREDITO'      ||CHR(9)||
                'SALDO FINAL'             ||CHR(9)||
                'SALDO FINAL CORRIENTE'   ||CHR(9)||
                'SALDO FINAL NO CORRIENTE'        
                ||CHR(13)||CHR(10);
    END IF;  
    MI_CADENA_DOS :='Validación del formato CGN2015_001_SALDOS_Y_MOVIMIENTOS_CONVERGENCIA' ||CHR(13)||CHR(10);
    --<AUTOR:CP FECHA:2701/2023 TICKET:Ticket#7723657 — Solicitud revision y ajuste Saldos y movimientos >
    --Se agrega proceso de mayorización y se agrega columnas SALDO_FINAL_CORRIENTE y SALDO_FINAL_NO_CORRIENTE

    /*FECHA:03/03/2023 TICKET:Ticket#7728108  — Solicitud revision y ajuste Saldos y movimientos >
   Se ajusta y se agrega proceso de distribucion del saldo final a las columnas SALDO_FINAL_CORRIENTE y SALDO_FINAL_NO_CORRIENTE 
    respectivamente cuando los auxiliares tienen porcion corriente y no corriente en el mismo capitulo de la cuenta*/
    <<SALDOSMOVIMIENTOS>>  
    FOR RS IN ( 
      WITH SALDOCORRIENTE AS  (
        SELECT 
           D.ID,D.NOMBRE,D.NATURALEZA,D.CORRIENTE,D.SALDOINICIAL,D.DEBITO,D.CREDITO,D.SALDOFINAL
           ,SUM(CASE  WHEN LENGTH(D.ID) < UN_DIGITOS THEN 0 ELSE CASE WHEN SSCS.CORRIENTE NOT IN(0) THEN SSCS.SALDOFINAL ELSE 0 END END )   SALDO_FINAL_CORRIENTE 
           ,SUM(CASE  WHEN LENGTH(D.ID) < UN_DIGITOS THEN 0 ELSE CASE WHEN SSCS.CORRIENTE IN(0) THEN SSCS.SALDOFINAL ELSE 0 END END ) SALDO_FINAL_NO_CORRIENTE
           , D.DESCRIPCION
        FROM (
                SELECT 
                    ID,
                    NOMBRE,
                    NATURALEZA,
                    CORRIENTE,
                    TO_NUMBER(SUM(CASE UN_TRIMESTRE 
                        WHEN 1 THEN SALDO0
                        WHEN 2 THEN SALDO3
                        WHEN 3 THEN SALDO6
                        WHEN 4 THEN SALDO9
                    END)) SALDOINICIAL,
                    TO_NUMBER(SUM(CASE UN_TRIMESTRE
                        WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                        WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                        WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                        WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                    END)) DEBITO,
                   TO_NUMBER( SUM(CASE UN_TRIMESTRE 
                        WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                        WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                        WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                        WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                    END)) CREDITO,
                      TO_NUMBER(SUM(CASE WHEN NATURALEZA IN('D')
                            THEN 
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN SALDO0  
                                WHEN 2 THEN SALDO3 
                                WHEN 3 THEN SALDO6 
                                WHEN 4 THEN SALDO9 
                              END +
                              CASE UN_TRIMESTRE 
                                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                              END -
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                              END
                          ELSE 
                             CASE UN_TRIMESTRE  
                                WHEN 1 THEN SALDO0  
                                WHEN 2 THEN SALDO3 
                                WHEN 3 THEN SALDO6 
                                WHEN 4 THEN SALDO9 
                              END +
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                              END
                               -
                              CASE UN_TRIMESTRE 
                                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                              END
                            END)) SALDOFINAL, ' ' DESCRIPCION
                FROM V_PLAN_CONTABLE
                WHERE COMPANIA    = UN_COMPANIA
                    AND ANO       = UN_ANO
                    AND (
                          (1 = CASE WHEN UN_EXCEL <> 0 THEN 1 ELSE 0 END
                                  AND LENGTH(ID)<= UN_DIGITOS)
                      OR  ( 1 = CASE WHEN UN_EXCEL = 0 THEN 1 ELSE 0 END
                                AND LENGTH(ID)= UN_DIGITOS)
                        )
                    AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )
                GROUP BY  ID,
                          NOMBRE,
                          NATURALEZA,
                          CORRIENTE    
                UNION ALL
                
                SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,6) ID,
                    PLAN_CONTABLE.NOMBRE,
                    PLAN_CONTABLE.NATURALEZA,
                    PLAN_CONTABLE.CORRIENTE,
                    CASE WHEN UN_TRIMESTRE = 4 THEN SALDO10 ELSE 0 END SALDOINICIAL,
                    CASE WHEN UN_TRIMESTRE = 4 THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END DEBITO,
                    CASE WHEN UN_TRIMESTRE = 4 THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END CREDITO,
                    CASE WHEN UN_TRIMESTRE = 4 THEN PLAN_CONTABLE.SALDO13 ELSE 0 END SALDOFINAL,
					DETALLE_COMPROBANTE_CNT.DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN HEADERCIERRE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA  = HEADERCIERRE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO       = HEADERCIERRE.ANO
                 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = HEADERCIERRE.TIPOCOMPROBANTE
                 AND DETALLE_COMPROBANTE_CNT.CUENTA    = HEADERCIERRE.CONTRACUENTA                 
                INNER JOIN PLAN_CONTABLE  
                    ON PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                   AND PLAN_CONTABLE.ANO      = DETALLE_COMPROBANTE_CNT.ANO
                   AND PLAN_CONTABLE.CODIGO   = DETALLE_COMPROBANTE_CNT.CUENTA
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
                AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='CIE'
                AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,1)='3'
				AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )
                UNION ALL
                
               SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS) ID,
                    PLAN_CONTABLE.NOMBRE,
                    PLAN_CONTABLE.NATURALEZA,
                    PLAN_CONTABLE.CORRIENTE,
                    CASE WHEN UN_TRIMESTRE = 4 THEN SALDO10 ELSE 0 END  SALDOINICIAL,                    
                    CASE WHEN UN_TRIMESTRE = 4 THEN           DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END DEBITO,
                    CASE WHEN UN_TRIMESTRE = 4 THEN           VALOR_DEBITO  ELSE 0 END CREDITO,
                    CASE WHEN UN_TRIMESTRE = 4 THEN  DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - VALOR_DEBITO ELSE 0 END  SALDOFINAL,
					DETALLE_COMPROBANTE_CNT.DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN HEADERCIERRE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA  = HEADERCIERRE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO       = HEADERCIERRE.ANO
                 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = HEADERCIERRE.TIPOCOMPROBANTE
                 AND DETALLE_COMPROBANTE_CNT.CUENTA    = HEADERCIERRE.CONTRACUENTA                 
                INNER JOIN PLAN_CONTABLE  
                    ON PLAN_CONTABLE.COMPANIA = HEADERCIERRE.COMPANIA
                   AND PLAN_CONTABLE.ANO      = HEADERCIERRE.ANO
                   AND PLAN_CONTABLE.CODIGO   = HEADERCIERRE.CUENTAACERRAR
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
                AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='CIE'
                AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,1) ='3'
				AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )
                        
                UNION ALL
                
               SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS) ID,
                    PLAN_CONTABLE.NOMBRE,  PLAN_CONTABLE.NATURALEZA , PLAN_CONTABLE.CORRIENTE,
                    0  SALDOINICIAL,                    
                    SUM (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) DEBITO,
                    SUM (VALOR_DEBITO) CREDITO,
                    0  SALDOFINAL,
					MAX(DETALLE_COMPROBANTE_CNT.DESCRIPCION) DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT               
                INNER JOIN PLAN_CONTABLE  
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                   AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,UN_DIGITOS)   = PLAN_CONTABLE.CODIGO
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
				AND (UN_COVID <> 0 AND DETALLE_COMPROBANTE_CNT.INFORMECOVID <> 0 )
                GROUP BY SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS),
                         PLAN_CONTABLE.NOMBRE,
                         PLAN_CONTABLE.NATURALEZA , 
                         PLAN_CONTABLE.CORRIENTE
                          
                ORDER BY  ID ) D INNER JOIN 
                (

                SELECT 
                          SUBSTR(V_PLAN_CONTABLE.ID,1,UN_DIGITOS) ID,
                          NATURALEZA,
                          CORRIENTE ,
                      TO_NUMBER(SUM(CASE WHEN MOVIMIENTO IN(0)  THEN 0 ELSE 
                        CASE WHEN NATURALEZA IN('D')
                            THEN 
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN SALDO0  
                                WHEN 2 THEN SALDO3 
                                WHEN 3 THEN SALDO6 
                                WHEN 4 THEN SALDO9 
                              END +
                              CASE UN_TRIMESTRE 
                                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                              END -
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                              END
                          ELSE 
                             CASE UN_TRIMESTRE  
                                WHEN 1 THEN SALDO0  
                                WHEN 2 THEN SALDO3 
                                WHEN 3 THEN SALDO6 
                                WHEN 4 THEN SALDO9 
                              END +
                              CASE UN_TRIMESTRE  
                                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                              END
                               -
                              CASE UN_TRIMESTRE 
                                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                              END
                            END END)) SALDOFINAL
                FROM V_PLAN_CONTABLE
                WHERE COMPANIA    = UN_COMPANIA
                    AND ANO       = UN_ANO
                GROUP BY SUBSTR(V_PLAN_CONTABLE.ID,1,UN_DIGITOS),
                          NATURALEZA,
                          CORRIENTE 
                ) SSCS
        ON  D.ID =  SSCS.ID
        GROUP BY  D.ID,D.NOMBRE,D.NATURALEZA,D.CORRIENTE,D.SALDOINICIAL,D.DEBITO,D.CREDITO,D.SALDOFINAL, D.DESCRIPCION
        )
          SELECT
                  SALDOCORRIENTE.ID
                , SALDOCORRIENTE.NOMBRE
                , SALDOCORRIENTE.NATURALEZA 
                , SALDOCORRIENTE.CORRIENTE 
                , SALDOCORRIENTE.SALDOINICIAL
                , SALDOCORRIENTE.DEBITO
                , SALDOCORRIENTE.CREDITO
                , SALDOCORRIENTE.SALDOFINAL
                , DATOSCORRIENTE.SALDO_CORRIENTE SALDO_FINAL_CORRIENTE 
                , DATOSCORRIENTE.SALDO_NO_CORRIENTE SALDO_FINAL_NO_CORRIENTE
                , SALDOCORRIENTE.DESCRIPCION
          FROM (
                          SELECT  SIN_MOVIMIENTO.ID
                                , SUM(MOVIMIENTO.SALDO_FINAL_CORRIENTE) AS SALDO_CORRIENTE
                                , SUM(MOVIMIENTO.SALDO_FINAL_NO_CORRIENTE) AS SALDO_NO_CORRIENTE
                          FROM (
                                SELECT  SCO.ID
                                      , SCO.SALDO_FINAL_CORRIENTE 
                                      , SCO.SALDO_FINAL_NO_CORRIENTE 
                                FROM SALDOCORRIENTE SCO 
                                WHERE  LENGTH(SCO.ID)= UN_DIGITOS 
                          )   MOVIMIENTO 
                          INNER JOIN (
                                SELECT  SCO.ID
                                       , SCO.SALDO_FINAL_CORRIENTE 
                                       , SCO.SALDO_FINAL_NO_CORRIENTE  
                                FROM SALDOCORRIENTE SCO 
                                WHERE  LENGTH(SCO.ID) <= UN_DIGITOS 
                          )  SIN_MOVIMIENTO
                          ON  SUBSTR(MOVIMIENTO.ID,0,LENGTH(SIN_MOVIMIENTO.ID))    =  SIN_MOVIMIENTO.ID
                          GROUP BY SIN_MOVIMIENTO.ID
          ) DATOSCORRIENTE
          INNER JOIN SALDOCORRIENTE 
             ON DATOSCORRIENTE.ID =  SALDOCORRIENTE.ID
          ORDER BY DATOSCORRIENTE.ID

    )  
    LOOP

      IF RS.SALDOINICIAL <> 0 OR RS.DEBITO <> 0 OR RS.CREDITO <> 0 OR RS.SALDOFINAL <> 0 THEN
        IF UN_EXCEL = 0 THEN 
          IF LENGTH(RS.ID) >0 AND LENGTH(RS.ID) < 2 THEN 
            MI_CONCEPTO := RS.ID;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 3 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1);
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 5 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) ;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 7 THEN
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) || '.' || SUBSTR(RS.ID,5,2);
          END IF; 
        ELSE 
          MI_CONCEPTO := RS.ID;
        END IF;  

      MI_SALDOFIN := RS.SALDOFINAL;

       IF UN_MILES <> 0 THEN
          MI_SALDOINICIAL := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDOINICIAL / 1000);
          MI_DEBITO       := PCK_SYSMAN_UTL.FC_ROUND(RS.DEBITO / 1000);
          MI_CREDITO      := PCK_SYSMAN_UTL.FC_ROUND(RS.CREDITO / 1000);
          MI_SALDOFIN     := PCK_SYSMAN_UTL.FC_ROUND(MI_SALDOFIN / 1000); 
       ELSIF UN_CENTAVOS <> 0 THEN
          MI_SALDOINICIAL := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDOINICIAL,0);
          MI_DEBITO       := PCK_SYSMAN_UTL.FC_ROUND(RS.DEBITO,0);
          MI_CREDITO      := PCK_SYSMAN_UTL.FC_ROUND(RS.CREDITO,0);
          MI_SALDOFIN     := PCK_SYSMAN_UTL.FC_ROUND(MI_SALDOFIN,0);
          MI_SALDOCOR     := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO_FINAL_CORRIENTE,0);
          MI_SALDONOCOR   := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO_FINAL_NO_CORRIENTE,0);
          
        ELSE
          MI_SALDOINICIAL := RS.SALDOINICIAL;
          MI_DEBITO := RS.DEBITO;
          MI_CREDITO := RS.CREDITO;
              --IF RS.CORRIENTE NOT IN(0) THEN 
          MI_SALDOCOR := RS.SALDO_FINAL_CORRIENTE;
       --   MI_SALDONOCOR := 0;
       -- ELSE 
       --    MI_SALDOCOR  := 0;
          MI_SALDONOCOR := RS.SALDO_FINAL_NO_CORRIENTE;
       -- END IF;
        
        END IF;


        MI_CADENA_UNO := TO_CLOB(MI_CADENA_UNO ||
                      CASE WHEN UN_EXCEL IN(0) THEN
                       'D'                  || CHR(9)
                      END
                      ||  MI_CONCEPTO         || CHR(9));
        IF(UN_COVID <> 0 ) THEN
            MI_CADENA_UNO := MI_CADENA_UNO ||  TO_CLOB(RS.DESCRIPCION)     || CHR(9);
        END IF;
        MI_CADENA_UNO := TO_CLOB(MI_CADENA_UNO || CASE WHEN UN_EXCEL NOT IN(0)THEN
                           SUBSTR(RS.NOMBRE, 1, 30)  || CHR(9)   
                         END                       
                      ||  TO_CLOB(MI_SALDOINICIAL)     || CHR(9)
                      ||  TO_CLOB(MI_DEBITO)           || CHR(9)
                      ||  TO_CLOB(MI_CREDITO)          || CHR(9)
                      ||  TO_CLOB(MI_SALDOFIN)         || CHR(9)
                      ||  TO_CLOB(MI_SALDOCOR)         || CHR(9)
                      ||  TO_CLOB(MI_SALDONOCOR)       || 
                       CHR(13)||CHR(10)                      
                      );

        MI_TOTALDEBITO :=  MI_TOTALDEBITO   +     MI_DEBITO;
        MI_TOTALCREDITO := MI_TOTALCREDITO  +     MI_CREDITO;
        IF RS.ID = '1' THEN 
          MI_NOMCUENTAUNO := RS.NOMBRE;
          MI_CUENTAUNOINI := MI_SALDOINICIAL;
          MI_CUENTAUNOFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '2' THEN 
          MI_NOMCUENTADOS := RS.NOMBRE;
          MI_CUENTADOSINI := MI_SALDOINICIAL;
          MI_CUENTADOSFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '3' THEN 
          MI_NOMCUENTATRES := RS.NOMBRE;
          MI_CUENTATRESINI := MI_SALDOINICIAL;
          MI_CUENTATRESFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '4' THEN 
          MI_NOMCUENTACUATRO := RS.NOMBRE;
          MI_CUENTACUATROINI := MI_SALDOINICIAL;
          MI_CUENTACUATROFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '5' THEN 
          MI_NOMCUENTACINCO := RS.NOMBRE;
          MI_CUENTACINCOINI := MI_SALDOINICIAL;
          MI_CUENTACINCOFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '6' THEN 
          MI_NOMCUENTASEIS := RS.NOMBRE;
          MI_CUENTASEISINI := MI_SALDOINICIAL;
          MI_CUENTASEISFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '7' THEN 
          MI_NOMCUENTASIETE := RS.NOMBRE;
          MI_CUENTASIETEINI := MI_SALDOINICIAL;
          MI_CUENTASIETEFIN := MI_SALDOFIN;
        END IF; 
      END IF;                    
      END LOOP SALDOSMOVIMIENTOS;
      MI_TOTDEB				      := TO_CHAR(MI_TOTALDEBITO,'FM999,999,999,999,999.999');
      MI_TOTCRE		          := TO_CHAR(MI_TOTALCREDITO,'FM999,999,999,999,999.999');
      MI_DIFERENCIA         := MI_TOTALDEBITO - MI_TOTALCREDITO;
      MI_TOTDIS 		        := TO_CHAR(MI_DIFERENCIA,'FM999,999,999,999,999.999');
      MI_TOTCUENTAUNOINI	  := TO_CHAR(MI_CUENTAUNOINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTADOSINI	  := TO_CHAR(MI_CUENTADOSINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTATRESINI	  := TO_CHAR(MI_CUENTATRESINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTACUATROINI	:= TO_CHAR(MI_CUENTACUATROINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTACINCOINI	:= TO_CHAR(MI_CUENTACINCOINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTASEISINI	  := TO_CHAR(MI_CUENTASEISINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTASIETEINI	:= TO_CHAR(MI_CUENTASIETEINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTAUNOFIN	  := TO_CHAR(MI_CUENTAUNOFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTADOSFIN	  := TO_CHAR(MI_CUENTADOSFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTATRESFIN	  := TO_CHAR(MI_CUENTATRESFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTACUATROFIN	:= TO_CHAR(MI_CUENTACUATROFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTACINCOFIN	:= TO_CHAR(MI_CUENTACINCOFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTASEISFIN	  := TO_CHAR(MI_CUENTASEISFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTASIETEFIN	:= TO_CHAR(MI_CUENTASIETEFIN,'FM999,999,999,999,999.999');
      MI_DIFERENCIAINI      := MI_CUENTAUNOINI - ((MI_CUENTADOSINI + MI_CUENTATRESINI)+(MI_CUENTACUATROINI-MI_CUENTACINCOINI-MI_CUENTASEISINI-MI_CUENTASIETEINI));
      MI_DIFERENCIAINICIAL	:= TO_CHAR(MI_DIFERENCIAINI,'FM999,999,999,999,999.999');
      MI_DIFERENCIAFIN      := MI_CUENTAUNOFIN - ((MI_CUENTADOSFIN + MI_CUENTATRESFIN)+(MI_CUENTACUATROFIN-MI_CUENTACINCOFIN-MI_CUENTASEISFIN-MI_CUENTASIETEFIN));
      MI_DIFERENCIAFINAL	:= TO_CHAR(MI_DIFERENCIAFIN,'FM999,999,999,999,999.999');
      MI_CADENA_DOS   := TO_CLOB(MI_CADENA_DOS  
                          || 'Total Movimiento Débito: '    || CHR(9)
                          || TO_CLOB(MI_TOTDEB)             || CHR(13)||CHR(10) 
                          || 'Total Movimiento Crédito: '   || CHR(9)
                          || TO_CLOB(MI_TOTCRE)             || CHR(13)||CHR(10) 
                          || 'Diferencia: '                 || CHR(9)
                          || TO_CLOB(MI_TOTDIS) || CHR(13)||CHR(10) 
                          || 'Valores Cuentas Mayores'    || CHR(13)||CHR(10)
                          || '1 - ' || RPAD(MI_NOMCUENTAUNO,10)    || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTAUNOINI,20)    || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTAUNOFIN,20)    || CHR(13)||CHR(10)
                          || '2 - ' || RPAD(MI_NOMCUENTADOS,10)    || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTADOSINI,20)    || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTADOSFIN,20)    || CHR(13)||CHR(10)
                          || '3 - ' || RPAD(MI_NOMCUENTATRES,10)   || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTATRESINI,20)   || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTATRESFIN,20)   || CHR(13)||CHR(10)
                          || '4 - ' || RPAD(MI_NOMCUENTACUATRO,10) || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTACUATROINI,20) || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTACUATROFIN,20) || CHR(13)||CHR(10)
                          || '5 - ' || RPAD(MI_NOMCUENTACINCO,10)  || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTACINCOINI,20)  || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTACINCOFIN,20)  || CHR(13)||CHR(10)
                          || '6 - ' || RPAD(MI_NOMCUENTASEIS,10)   || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTASEISINI,20)   || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTASEISFIN,20)   || CHR(13)||CHR(10)
                          || '7 - ' || RPAD(MI_NOMCUENTASIETE,10)  || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTASIETEINI,20)  || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTASIETEFIN,20)  || CHR(13)||CHR(10)
                          || RPAD('Diferencias',30) || RPAD(MI_DIFERENCIAINICIAL,33)|| CHR(9) || MI_DIFERENCIAFINAL) ;
      MI_RTA := MI_CADENA_UNO || '&' || MI_CADENA_DOS; 
    RETURN MI_RTA;
END  FC_GENERARARCHIVOSALDOMOV; 
--14
FUNCTION FC_GENERARARCHIVORECIPROCAS
(
 /*
      NAME              : FC_GENERARARCHIVORECIPROCAS 
      AUTHOR            : YESIKA PAOLA BECERRA CASTRO
      DATE              : 25/06/2018
      TIME              : 11:06 AM
      SOURCE MODULE     : SysmanChip2018.04.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de los saldos de operaciones reciprocas
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       => CompaÃ±ia de ingreso a la aplicaciÃ³n
                          UN_ANIO           =>AÃ±o contable del que se desea obtener la informaciÃ³n.
                          UN_TRIMESTRE      => Trimestre para el cual se va a rendir la informaciÃ³n del plano.
                          UN_CODIGOENTIDAD  =>  Codigo de la entidad reciproca. 
                          UN_DIGITOS        => NÃºmero de digitos para filtrar id del plan contable
                          UN_EXCEL          => Permite validar que encabezado se generara 
                          UN_MILES          => Permtite validar si los valores se dividen en 1000 o no 
                          UN_CENTAVOS       => Permite validar si los valores se visulizan con decimales o no 

      @NAME  :  generarPlanoReciproco
      @METHOD:  GET 
    */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CODIGOENTIDAD    IN VARCHAR2,
    UN_DIGITOS          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CENTAVOS         IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CGN              IN PCK_SUBTIPOS.TI_LOGICO,
    UN_COD_EQUIV        IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS 
    MI_RTA            CLOB;
    MI_CONCEPTO       VARCHAR2(20 CHAR);
    MI_SALDOFIN       PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDONOCOR     PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOCOR       PCK_SUBTIPOS.TI_DOBLE;
    MI_ENCABEZADO     VARCHAR2(50 CHAR);
BEGIN 

    IF UN_EXCEL = 0 THEN 
      IF UN_CGN NOT IN (0) THEN 
        MI_ENCABEZADO := 'CGN2015_002_OPERACIONES_RECIPROCAS_CONVERGENCIA';
      ELSE 
        MI_ENCABEZADO := 'CGN2015_002_OPERACIONES_RECIPROCAS_CONVERGENCIA';
      END IF; 
      MI_RTA :=   'S' ||  CHR(9)|| UN_CODIGOENTIDAD ||CHR(9)||
                  '1' ||  CASE UN_TRIMESTRE   WHEN 1 THEN '0103'
                                              WHEN 2 THEN '0406'
                                              WHEN 3 THEN '0709'
                                              WHEN 4 THEN '1012'
                          END||CHR(9)||
                  UN_ANO||CHR(9)||MI_ENCABEZADO||CHR(9)||
                  TO_CHAR(SYSDATE, 'DD-MM-YYYY') ||CHR(13)||CHR(10);
    ELSE
      MI_RTA := 'CODIGO CONTABLE SUBCUENTA' ||CHR(9)|| 
                'NOMBRE DE LA SUBCUENTA'    ||CHR(9)||
                'CODIGO INSTITUCIONAL'      ||CHR(9)||
                'NOMBRE DE LA ENTIDAD'      ||CHR(9)||
                'VALOR  CORRIENTE'          ||CHR(9)||
                'VALOR  NO CORRIENTE'       
                ||CHR(13)||CHR(10);
    END IF;  
    <<SALDOSMOVIMIENTOS>>  
    FOR RS IN (
      SELECT ID,NOMBRE ,NATURALEZA , TERCERO , NOMBRETERCERO,
            SUM(CASE WHEN CORRIENTE  =  0 THEN TO_NUMBER(SALDOFINAL) ELSE 0 END  )AS SALDOFINALNOCORRIENTE,
            SUM(CASE WHEN NOT CORRIENTE  =  0 THEN TO_NUMBER(SALDOFINAL) ELSE 0 END )AS SALDOFINALCORRIENTE
      FROM (
             SELECT 
                 RECIPROCA.ID,
                 PLAN_CONTABLE.NOMBRE,
                 RECIPROCA.NATURALEZA,
                 RECIPROCA.CORRIENTE,
                 RECIPROCA.TERCERO ,
                 CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL NOMBRETERCERO,
                 TO_NUMBER(RECIPROCA.SALDOFINAL) SALDOFINAL
              FROM(
                        SELECT PLAN.COMPANIA,PLAN.ANO,
                                  CASE WHEN UN_COD_EQUIV   IN (0) 
                                THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                ELSE 
                                  CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                    THEN PLAN.COD_EQUIV 
                                    ELSE  SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                  END
                              END ID,
                              PLAN.NATURALEZA,
                              PLAN.CORRIENTE,
                              CASE WHEN PLAN.TERCERO_RECIPROCAS IS NULL
                                   THEN CODIGO_UNICO_INSTITUCIONAL.CODIGO
                                   ELSE 
                                   PLAN.TERCERO_RECIPROCAS 
                                   END TERCERO ,
                                   CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL, 
                                  TO_NUMBER(SUM( CASE UN_TRIMESTRE
                                      WHEN 1 THEN PLAN.SALDO3  
                                      WHEN 2 THEN PLAN.SALDO6 
                                      WHEN 3 THEN PLAN.SALDO9
                                      WHEN 4 THEN PLAN.SALDO12 
                                ELSE 0 END)) SALDOFINAL

                          FROM V_PLAN_CONTABLE PLAN
                              INNER JOIN TERCERO 
                                ON PLAN.COMPANIA  = TERCERO.COMPANIA
                                AND PLAN.TERCERO  = TERCERO.NIT
                                AND PLAN.SUCURSAL = TERCERO.SUCURSAL  
                             INNER JOIN PLAN_CONTABLE
                                 ON PLAN.COMPANIA                    = PLAN_CONTABLE.COMPANIA
                                AND PLAN.ANO                         = PLAN_CONTABLE.ANO
                                AND SUBSTR(PLAN.CODIGO,1,UN_DIGITOS) = PLAN_CONTABLE.CODIGO   
                            INNER JOIN CODIGO_UNICO_INSTITUCIONAL
                            ON TERCERO.NIT_CEDULA= CODIGO_UNICO_INSTITUCIONAL.CODIGO
                          WHERE PLAN.COMPANIA    = UN_COMPANIA
                              AND PLAN.ANO       = UN_ANO
                              AND PLAN.NOREPORTARRECIPROCAS NOT IN(0)
                              AND ((TERCERO.CLASEENTIDADOFICIAL NOT IN(0) AND CODIGO_UNICO_INSTITUCIONAL.CODIGO IS NOT NULL) OR (TERCERO.CLASEENTIDADOFICIAL IN(0) AND  PLAN.TERCERO_RECIPROCAS IS NOT NULL))
                              AND PLAN.MAN_AUX_TER_PADRE NOT IN(0)
                           GROUP BY PLAN.COMPANIA,PLAN.ANO,
                                  CASE WHEN UN_COD_EQUIV  IN (0) 
                                    THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                    ELSE 
                                      CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                        THEN PLAN.COD_EQUIV 
                                        ELSE SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                      END
                                    END ,
                                    PLAN.NATURALEZA,
                                    PLAN.CORRIENTE,
                                    CASE WHEN PLAN.TERCERO_RECIPROCAS IS NULL
                                   THEN CODIGO_UNICO_INSTITUCIONAL.CODIGO
                                   ELSE 
                                   PLAN.TERCERO_RECIPROCAS 
                                   END,
                                   PLAN.COD_EQUIV,
                                   CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL

                UNION ALL
                    SELECT PLAN.COMPANIA,PLAN.ANO,
                                  CASE WHEN UN_COD_EQUIV   IN (0) 
                                    THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                    ELSE 
                                      CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                        THEN PLAN.COD_EQUIV 
                                        ELSE  SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                      END
                                  END ID,
                                  PLAN.NATURALEZA,
                                  PLAN.CORRIENTE,
                                  CASE WHEN PLAN.TERCERO_RECIPROCAS IS NULL
                                   THEN CODIGO_UNICO_INSTITUCIONAL.CODIGO
                                   ELSE 
                                   PLAN.TERCERO_RECIPROCAS 
                                   END TERCERO ,
                                   CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL,
                                TO_NUMBER( SUM(CASE UN_TRIMESTRE
                                 WHEN 1 THEN                                  
                                 CASE WHEN MES BETWEEN 1 AND 3 
                                   THEN  CASE WHEN PLAN.NATURALEZA IN('D')
                                                      THEN VALOR_DEBITO - VALOR_CREDITO
                                                      ELSE VALOR_CREDITO - VALOR_DEBITO
                                                    END
                                              END 

                                      WHEN 2 THEN 
                                            CASE WHEN MES BETWEEN 1 AND 6 
                                               THEN  CASE WHEN PLAN.NATURALEZA IN('D')
                                                        THEN VALOR_DEBITO - VALOR_CREDITO
                                                        ELSE VALOR_CREDITO - VALOR_DEBITO
                                                      END
                                                  END

                                      WHEN 3 THEN 
                                                CASE WHEN MES BETWEEN 1 AND 9 
                                                   THEN  
                                                   CASE WHEN PLAN.NATURALEZA IN('D')
                                                            THEN VALOR_DEBITO - VALOR_CREDITO
                                                            ELSE VALOR_CREDITO - VALOR_DEBITO
                                                          END
                                                  END                                               
                                      WHEN 4 THEN                                       
                                    CASE WHEN MES BETWEEN 1 AND 12 
                                              THEN CASE WHEN PLAN.NATURALEZA IN('D')
                                                          THEN VALOR_DEBITO - VALOR_CREDITO
                                                          ELSE VALOR_CREDITO - VALOR_DEBITO
                                                        END
                                                  END

                                      END ))   SALDOFINAL
                          FROM PLAN_CONTABLE PLAN
                               INNER JOIN DETALLE_COMPROBANTE_CNT DET
                                 ON PLAN.COMPANIA  = DET.COMPANIA
                                 AND PLAN.ANO      = DET.ANO
                                 AND PLAN.CODIGO   = DET.CUENTA
                                           INNER JOIN TERCERO 
                                ON  DET.COMPANIA  = TERCERO.COMPANIA
                                AND DET.TERCERO  = TERCERO.NIT
                                AND DET.SUCURSAL = TERCERO.SUCURSAL
                                INNER JOIN CODIGO_UNICO_INSTITUCIONAL
                                ON TERCERO.NIT_CEDULA= CODIGO_UNICO_INSTITUCIONAL.CODIGO
                           WHERE PLAN.COMPANIA    = UN_COMPANIA
                              AND PLAN.ANO       = UN_ANO
                              --AND PLAN.CLASECUENTA NOT IN('I','B')
                              AND REPORTAR_100 IN(0)
                              AND PLAN.NOREPORTARRECIPROCAS NOT IN(0)
                              AND ((TERCERO.CLASEENTIDADOFICIAL NOT IN(0) AND CODIGO_UNICO_INSTITUCIONAL.CODIGO IS NOT NULL) OR (TERCERO.CLASEENTIDADOFICIAL IN(0) AND  PLAN.TERCERO_RECIPROCAS IS NOT NULL))
                              AND PLAN.MAN_AUX_TER IN(0)
                          GROUP BY PLAN.COMPANIA,PLAN.ANO,
                                   CASE WHEN UN_COD_EQUIV  IN (0) 
                                    THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                    ELSE 
                                      CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                        THEN PLAN.COD_EQUIV 
                                        ELSE SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                      END
                                    END ,
                                    PLAN.NATURALEZA,
                                    PLAN.CORRIENTE,
                                    CASE WHEN PLAN.TERCERO_RECIPROCAS IS NULL
                                   THEN CODIGO_UNICO_INSTITUCIONAL.CODIGO
                                   ELSE 
                                   PLAN.TERCERO_RECIPROCAS 
                                   END ,
                                  PLAN.COD_EQUIV,
                                  CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL

                  UNION ALL
                     SELECT PLAN.COMPANIA,PLAN.ANO,
                                  CASE WHEN UN_COD_EQUIV   IN (0) 
                                    THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                    ELSE 
                                      CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                        THEN PLAN.COD_EQUIV 
                                        ELSE  SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                      END
                                  END ID,
                                  PLAN.NATURALEZA,
                                  PLAN.CORRIENTE,
                                  PLAN.TERCERO_RECIPROCAS TERCERO,
                                  CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL,
                                   TO_NUMBER(SUM( CASE UN_TRIMESTRE
                                      WHEN 1 THEN PLAN.SALDO3  
                                      WHEN 2 THEN PLAN.SALDO6 
                                      WHEN 3 THEN PLAN.SALDO9
                                      WHEN 4 THEN PLAN.SALDO12 
                                ELSE 0 END)) SALDOFINAL
                          FROM PLAN_CONTABLE PLAN
                          LEFT JOIN  CODIGO_UNICO_INSTITUCIONAL ON
                          PLAN.TERCERO_RECIPROCAS =CODIGO_UNICO_INSTITUCIONAL.CODIGO
                             WHERE PLAN.COMPANIA    = UN_COMPANIA
                              AND PLAN.ANO       = UN_ANO
                              --AND PLAN.CLASECUENTA IN('I','B')
                              AND PLAN.NOREPORTARRECIPROCAS NOT IN(0)
                              AND REPORTAR_100 NOT IN(0)
                             -- AND PLAN.TERCERO_RECIPROCAS IS NOT NULL
                              AND PLAN.MAN_AUX_TER IN(0)
                          GROUP BY PLAN.COMPANIA,PLAN.ANO,
                                   CASE WHEN UN_COD_EQUIV  IN (0) 
                                    THEN SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                    ELSE 
                                      CASE WHEN PLAN.COD_EQUIV IS NOT NULL 
                                        THEN PLAN.COD_EQUIV 
                                        ELSE SUBSTR(PLAN.CODIGO,1,UN_DIGITOS)
                                      END
                                    END ,
                                    PLAN.NATURALEZA,
                                    PLAN.CORRIENTE,
                                    PLAN.TERCERO_RECIPROCAS,
                                  PLAN.COD_EQUIV,
                                  CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL 
                ) RECIPROCA
                  INNER JOIN PLAN_CONTABLE
                     ON   RECIPROCA.COMPANIA=PLAN_CONTABLE.COMPANIA
                     AND  RECIPROCA.ANO     =PLAN_CONTABLE.ANO
                     AND RECIPROCA.ID       =PLAN_CONTABLE.CODIGO
                LEFT JOIN   TERCERO
                    ON   RECIPROCA.COMPANIA    =TERCERO.COMPANIA
                     AND  RECIPROCA.TERCERO    =TERCERO.NIT_CEDULA
                INNER JOIN CODIGO_UNICO_INSTITUCIONAL
                                ON RECIPROCA.TERCERO= CODIGO_UNICO_INSTITUCIONAL.CODIGO
        WHERE RECIPROCA.SALDOFINAL<>0                 
               GROUP BY RECIPROCA.ID, 
        RECIPROCA.CORRIENTE, 
        PLAN_CONTABLE.NOMBRE, 
        CODIGO_UNICO_INSTITUCIONAL.RAZON_SOCIAL, 
        RECIPROCA.NATURALEZA, 
        RECIPROCA.TERCERO, 
        TO_NUMBER(RECIPROCA.SALDOFINAL)
      ) D
      GROUP BY ID,NOMBRE ,NATURALEZA , TERCERO , NOMBRETERCERO
      ORDER BY ID)  
    LOOP

      IF RS.SALDOFINALCORRIENTE + RS.SALDOFINALNOCORRIENTE  <> 0 THEN
        IF UN_EXCEL = 0 THEN 
          IF LENGTH(RS.ID) >0 AND LENGTH(RS.ID) < 2 THEN 
            MI_CONCEPTO := RS.ID;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 3 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1);
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 5 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) ;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 7 THEN
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) || '.' || SUBSTR(RS.ID,5,2);
          END IF; 
        ELSE 
          MI_CONCEPTO := RS.ID;
        END IF;  

       MI_RTA := TO_CLOB(MI_RTA || TO_CLOB(
                      CASE WHEN UN_EXCEL IN(0) THEN
                       'D'                  || CHR(9)
                      END
                      ||  MI_CONCEPTO         || CHR(9)
                      || CASE WHEN UN_EXCEL NOT IN(0)THEN
                          RS.NOMBRE || CHR(9) 
                         END                           
                      ||  RS.TERCERO                   || CHR(9)
                      || CASE WHEN UN_EXCEL NOT IN(0)THEN
                            RS.NOMBRETERCERO  || CHR(9)     
                          END                    
                      ||  TO_CLOB(RS.SALDOFINALCORRIENTE)         || CHR(9)
                      ||  TO_CLOB(RS.SALDOFINALNOCORRIENTE)       || CHR(13)||CHR(10)));
        
        
       
        
      END IF;   
      END LOOP SALDOSMOVIMIENTOS;
    RETURN MI_RTA;
END  FC_GENERARARCHIVORECIPROCAS;     
--16
FUNCTION FC_GENERARARCHIVOBALANCE
(
 /*
      NAME              : FC_GENERARARCHIVOSALDOMOV 
      AUTHOR            : YESIKA PAOLA BECERRA CASTRO
      DATE              : 27/06/2018
      TIME              : 02:22 PM
      SOURCE MODULE     : SysmanChip2018.04.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de convergencia de Nit
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_ANIO           =>Año contable del que se desea obtener la información.
                          UN_CODIGOENTIDAD  =>  Codigo de la entidad reciproca. 
                          UN_CODIGOINICIAL  => Código inicial contable 
                          UN_CODIGOFINAL    => Código final contable 
                          UN_DIGITOS        => Número de digitos para filtrar id del plan contable
                          UN_EXCEL          => Indicador que permite identificar que tipo de encabezado debe retornar
                          UN_MILES          => Indicador que permite identicar si los valores son divisibles por 1000
                          UN_CENTAVOS       => Indicador que permite identificar si los valores se retornar con decimales

      @NAME  :  generarPlanoBalance
      @METHOD:  GET 
    */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGOENTIDAD    IN VARCHAR2,
    UN_CODIGOINICIAL    IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_CODIGOFINAL      IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_DIGITOS          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CENTAVOS         IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS 
    MI_RTA                CLOB;
    MI_CONCEPTO           VARCHAR2(20 CHAR);
    MI_SALDOINICIAL       VARCHAR2(20 CHAR);
    MI_AJUSTE_ERR_DEB     VARCHAR2(20 CHAR);
    MI_AJUSTE_ERR_CRE     VARCHAR2(20 CHAR);
    MI_AJUSTE_CONV_DEB    VARCHAR2(20 CHAR);
    MI_AJUSTE_CONV_CRE    VARCHAR2(20 CHAR);
    MI_CONVERGENCIA_DEB   VARCHAR2(20 CHAR);
    MI_CONVERGENCIA_CRE   VARCHAR2(20 CHAR);
    MI_SALDOAJUSTE        VARCHAR2(20 CHAR);
    MI_SALDOCOR           VARCHAR2(20 CHAR);
    MI_SALDONOCOR         VARCHAR2(20 CHAR);
BEGIN 

    IF UN_EXCEL = 0 THEN 
      MI_RTA :=   'S' ||  CHR(9)|| UN_CODIGOENTIDAD ||CHR(9)||
                  '1' ||  '10112' ||CHR(9)||
                  UN_ANO||CHR(9)||'CGN'||UN_ANO||'_001_SI_CONVERGENCIA'||CHR(9)||
                  '01-01-'||UN_ANO ||CHR(13)||CHR(10);
    ELSE
      MI_RTA := 'CÓDIGO DE LA SUBCUENTA'                    ||CHR(9)|| 
                'NOMBRE DE LA SUBCUENTA'                    ||CHR(9)||
                'SALDO INICIAL 01-01-'||UN_ANO              ||CHR(9)||
                'AJUSTE POR ERRORES DÉBITO'                 ||CHR(9)||
                'AJUSTE POR ERRORES CRÉDITO'                ||CHR(9)||
                'AJUSTE  POR CONVERGENCIA DÉBITO'           ||CHR(9)||
                'AJUSTE POR CONVERGENCIA CRÉDITO'           ||CHR(9)||
                'RECLASIFICACION POR CONVERGENCIA DÉBITO'   ||CHR(9)||
                'RECLASIFICACION POR CONVERGENCIA CRÉDITO'  ||CHR(9)||
                'SALDO A AJUSTAR 01-01-'||UN_ANO            ||CHR(9)||
                'SALDO CORRIENTE'                           ||CHR(9)||
                'SALDO NO CORRIENTE'
                ||CHR(13)||CHR(10);
    END IF;  
    <<SALDOSMOVIMIENTOS>>  
    FOR RS IN ( SELECT  
                  PLAN_CONTABLE.COMPANIA, 
                  PLAN_CONTABLE.ANO, 
                  PLAN_CONTABLE.CODIGO,
                  PLAN_CONTABLE.NOMBRE ,
                  PLAN_CONTABLE.NATURALEZA,
                  PLAN_CONTABLE.SALDO0,  
                  PLAN_CONTABLE.CORRIENTE,
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN ('AEN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END) AS AJUSTE_ERR_DEB,
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('AEN') THEN  DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END ) AS AJUSTE_ERR_CRE,  
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END) AS AJUSTE_CONV_DEB, 
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END) AS AJUSTE_CONV_CRE, 
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END) AS RECLAS_CONVERGENCIA_DEB,
                  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END) AS RECLAS_CONVERGENCIA_CRE,
                  SUM (CASE WHEN DETALLE_COMPROBANTE_CNT.NATURALEZA IN('D') 
                        THEN 
                         (CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('AEN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END  
                         ) -
                         (CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('AEN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END + 
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END
                         )
                      ELSE 
                        (CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('AEN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO ELSE 0 END  
                         ) -
                         (CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END +
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('AEN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END + 
                          CASE WHEN DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('RCN') THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO ELSE 0 END
                         )
                      END) SALDO_AJUSTAR
          FROM V_PLAN_CONTABLE PLAN_CONTABLE 
            INNER JOIN DETALLE_COMPROBANTE_CNT   
              ON  PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
              AND PLAN_CONTABLE.ANO = DETALLE_COMPROBANTE_CNT.ANO
              AND PLAN_CONTABLE.ID = DETALLE_COMPROBANTE_CNT.CUENTA
          WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
            AND TO_NUMBER(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,'YYYY')) = UN_ANO
            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN('ACN','AEN','RCN') 
            AND SUBSTR(NVL(DETALLE_COMPROBANTE_CNT.CUENTA,''),1,1) IN(1,2,3,8,9) 
            AND DETALLE_COMPROBANTE_CNT.ID BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
            AND LENGTH(PLAN_CONTABLE.CODIGO) = UN_DIGITOS 
            AND PLAN_CONTABLE.SALDO0 NOT IN(0)
          GROUP BY 
            PLAN_CONTABLE.COMPANIA, 
            PLAN_CONTABLE.ANO,
            PLAN_CONTABLE.CODIGO, 
            PLAN_CONTABLE.ID, 
            PLAN_CONTABLE.NOMBRE,  
            PLAN_CONTABLE.SALDO0, 
            PLAN_CONTABLE.CORRIENTE, 
            DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
            PLAN_CONTABLE.NATURALEZA,
            PLAN_CONTABLE.CORRIENTE)  
    LOOP

        IF UN_EXCEL = 0 THEN 
          IF LENGTH(RS.CODIGO) >0 AND LENGTH(RS.CODIGO) < 2 THEN 
            MI_CONCEPTO := RS.CODIGO;
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 3 THEN 
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1);
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 5 THEN 
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1) || '.' || SUBSTR(RS.CODIGO,3,2) ;
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 7 THEN
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1) || '.' || SUBSTR(RS.CODIGO,3,2) || '.' || SUBSTR(RS.CODIGO,5,2);
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 9 THEN
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1) || '.' || SUBSTR(RS.CODIGO,3,2) || '.' || SUBSTR(RS.CODIGO,5,2) || '.' || SUBSTR(RS.CODIGO,7,2);
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 11 THEN
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1) || '.' || SUBSTR(RS.CODIGO,3,2) || '.' || SUBSTR(RS.CODIGO,5,2) || '.' || SUBSTR(RS.CODIGO,7,2) || '.' || SUBSTR(RS.CODIGO,9,2);  
          ELSIF LENGTH(RS.CODIGO)>0  AND LENGTH(RS.CODIGO) < 13 THEN
            MI_CONCEPTO := SUBSTR(RS.CODIGO,1,1) || '.' || SUBSTR(RS.CODIGO,2,1) || '.' || SUBSTR(RS.CODIGO,3,2) || '.' || SUBSTR(RS.CODIGO,5,2) || '.' || SUBSTR(RS.CODIGO,7,2) || '.' || SUBSTR(RS.CODIGO,9,2) || '.' || SUBSTR(RS.CODIGO,11,2);          
          ELSE
            MI_CONCEPTO := RS.CODIGO;
          END IF; 
        ELSE 
          MI_CONCEPTO := RS.CODIGO;
        END IF;  

        MI_SALDOINICIAL := RS.SALDO0;

       IF UN_MILES <> 0 THEN
          MI_SALDOINICIAL     := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO0 / 1000));
          MI_AJUSTE_ERR_DEB   := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_ERR_DEB / 1000));
          MI_AJUSTE_ERR_CRE   := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_ERR_CRE / 1000));
          MI_AJUSTE_CONV_DEB  := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_CONV_DEB / 1000));
          MI_AJUSTE_CONV_CRE  := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_CONV_CRE / 1000));
          MI_CONVERGENCIA_DEB := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.RECLAS_CONVERGENCIA_DEB / 1000));
          MI_CONVERGENCIA_CRE := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.RECLAS_CONVERGENCIA_CRE / 1000));
       ELSIF UN_CENTAVOS <> 0 THEN
          MI_SALDOINICIAL     := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO0));
          MI_AJUSTE_ERR_DEB   := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_ERR_DEB));
          MI_AJUSTE_ERR_CRE   := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_ERR_CRE));
          MI_AJUSTE_CONV_DEB  := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_CONV_DEB));
          MI_AJUSTE_CONV_CRE  := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.AJUSTE_CONV_CRE));
          MI_CONVERGENCIA_DEB := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.RECLAS_CONVERGENCIA_DEB));
          MI_CONVERGENCIA_CRE := TO_NUMBER(PCK_SYSMAN_UTL.FC_ROUND(RS.RECLAS_CONVERGENCIA_CRE));
        ELSE
          MI_SALDOINICIAL     := TO_NUMBER(RS.SALDO0);
          MI_AJUSTE_ERR_DEB   := TO_NUMBER(RS.AJUSTE_ERR_DEB);
          MI_AJUSTE_ERR_CRE   := TO_NUMBER(RS.AJUSTE_ERR_CRE);
          MI_AJUSTE_CONV_DEB  := TO_NUMBER(RS.AJUSTE_CONV_DEB);
          MI_AJUSTE_CONV_CRE  := TO_NUMBER(RS.AJUSTE_CONV_CRE);
          MI_CONVERGENCIA_DEB := TO_NUMBER(RS.RECLAS_CONVERGENCIA_DEB);
          MI_CONVERGENCIA_CRE := TO_NUMBER(RS.RECLAS_CONVERGENCIA_CRE);
        END IF;
        IF RS.NATURALEZA IN ('D') THEN 
          MI_SALDOAJUSTE :=TO_CHAR( MI_SALDOINICIAL + (MI_AJUSTE_ERR_DEB + MI_AJUSTE_CONV_DEB + MI_CONVERGENCIA_DEB) - (MI_AJUSTE_ERR_CRE + MI_AJUSTE_CONV_CRE +MI_CONVERGENCIA_CRE ));
        ELSE 
          MI_SALDOAJUSTE := TO_CHAR(MI_SALDOINICIAL + (MI_AJUSTE_ERR_CRE+ MI_AJUSTE_CONV_CRE+MI_CONVERGENCIA_CRE)-(MI_AJUSTE_ERR_DEB + MI_AJUSTE_CONV_DEB + MI_CONVERGENCIA_DEB));
        END IF;  

        IF RS.CORRIENTE IN(0) THEN 
          MI_SALDOCOR := TO_CHAR(MI_SALDOAJUSTE);
          MI_SALDONOCOR := 0;
        ELSE 
          MI_SALDOCOR  := 0;
          MI_SALDONOCOR :=TO_CHAR( MI_SALDOAJUSTE);
        END IF;


       MI_RTA := TO_CLOB(MI_RTA ||
                      CASE WHEN UN_EXCEL IN(0) THEN
                       'D'                  || CHR(9)
                      END
                      ||  MI_CONCEPTO         || CHR(9)
                      || CASE WHEN UN_EXCEL NOT IN(0)THEN
                          RS.NOMBRE || CHR(9)
                         END                            
                      ||  TO_CLOB(MI_SALDOINICIAL)      || CHR(9)
                      ||  TO_CLOB(MI_AJUSTE_ERR_DEB)    || CHR(9)
                      ||  TO_CLOB(MI_AJUSTE_ERR_CRE)    || CHR(9)
                      ||  TO_CLOB(MI_AJUSTE_CONV_DEB)   || CHR(9)
                      ||  TO_CLOB(MI_AJUSTE_CONV_CRE)   || CHR(9)
                      ||  TO_CLOB(MI_CONVERGENCIA_DEB)  || CHR(9)
                      ||  TO_CLOB(MI_CONVERGENCIA_CRE)  || CHR(9)
                      ||  TO_CLOB(MI_SALDOAJUSTE)       || CHR(9)
                      ||  TO_CLOB(MI_SALDOCOR)          || CHR(9)
                      ||  TO_CLOB(MI_SALDONOCOR)      
                      || CHR(13)||CHR(10));

      END LOOP SALDOSMOVIMIENTOS;
    RETURN MI_RTA;
END  FC_GENERARARCHIVOBALANCE;   

FUNCTION FC_GENERARINCONSGASTOSINV(
    /*
      NAME              : FC_GENERARINCONSGASTOSINV
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 18/07/2018
      TIME              : 10:00 AM
      SOURCE MODULE     : CHIP-FUT
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE OBTIENE LOS DATOS DE ARCHIVO PLANO DE GASTOS DE INVERSION
      MODIFICATIONS     : 

      @NAME:    generarPlanoGastosInv
      @METHOD:  GET
    */

    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ENTERO,
    UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MESINICIAL       IN PCK_SUBTIPOS.TI_ENTERO,
    UN_MESFINAL         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CODIGOENTIDAD    IN VARCHAR2,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_SICEP            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_TIPO             IN VARCHAR2
    )
  RETURN CLOB AS
    MI_CADENA           CLOB;
    MI_INCLUIRAPLAZ     PARAMETRO.VALOR%TYPE;
    MI_CODIGOS          PARAMETRO.VALOR%TYPE;
    MI_DIGITOREDOND     PARAMETRO.VALOR%TYPE;
    MI_CIFRACTRFUR      PARAMETRO.VALOR%TYPE;
    MI_ENCABEZADO       CLOB;
    MI_MES              PCK_SUBTIPOS.TI_MES;
BEGIN

    MI_CODIGOS := '' || NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
              , UN_NOMBRE    => 'VIGENCIAS INCLUIDAS EN GASTOS DE INVERSION FUT'
              , UN_MODULO    => 99
              , UN_FECHA_PAR => SYSDATE),'VA') || '';
    MI_INCLUIRAPLAZ := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
              , UN_NOMBRE    => 'INCLUIR APLAZAMIENTOS EN DEFINITIVA DEL FUT'
              , UN_MODULO    => 99
              , UN_FECHA_PAR => SYSDATE),'NO') ;
    MI_DIGITOREDOND := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
              , UN_NOMBRE    => 'DIGITO REDONDEO DE INFORMES FUT'
              , UN_MODULO    => 99
              , UN_FECHA_PAR => SYSDATE),'0')) ; 
    MI_CIFRACTRFUR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
              , UN_NOMBRE    => 'FUENTE FUT PARA CIFRA DE CONTROL VAL'
              , UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD()
              , UN_FECHA_PAR => SYSDATE),'700') ; 

    IF UN_EXCEL NOT IN (0) THEN
          MI_CADENA :='S'                         ||CHR(9) || 
                        'CODIGO'                      ||CHR(9) || 
                        'FUENTE'                      ||CHR(9) ||
                        CASE WHEN UN_SICEP NOT IN (0) 
                            THEN 'NOMBRE FUENTE'      ||CHR(9) 
                            ELSE '' 
                            END || 
                        'PRESUPUESTO_INICIAL'         ||CHR(9) ||
                        'PRESUPUESTO_DEFINITIVO'      ||CHR(9) ||
                        'COMPROMISOS'                 ||CHR(9) ||
                        'OBLIGACIONES'                ||CHR(9) ||
                        'PAGOS'                       ||CHR(9) ||
                        'TOTAL PRESUPUESTO_INICIAL'   ||CHR(9) ||
                        'TOTAL PRESUPUESTO_DEFINITIVO'||CHR(9) ||
                        'TOTAL COMPROMISOS'           ||CHR(9) ||
                        'TOTAL OBLIGACIONES'          ||CHR(9) ||
                        'TOTAL PAGOS'                 ||CHR(13)||CHR(10); 
    ELSE

        MI_CADENA :=  'S' ||  CHR(9)|| UN_CODIGOENTIDAD ||CHR(9)||
                          '1' ||  CASE UN_TRIMESTRE   WHEN 1 THEN '0103'
                                                      WHEN 2 THEN '0406'
                                                      WHEN 3 THEN '0709'
                                                      WHEN 4 THEN '1012'
                                  END||CHR(9)||
                          UN_ANO||CHR(9)||CASE WHEN UN_TIPO = 'I' 
                                            THEN  'GASTOS_DE_INVERSION' 
                                            ELSE 'GASTOS_DE_FUNCIONAMIENTO' 
                                          END||CHR(9)||
                          TO_CHAR(SYSDATE, 'DD-MM-YYYY') ||CHR(13)||CHR(10);
    END IF; 

   MI_MES := CASE UN_TRIMESTRE   
      WHEN 1 THEN 3
      WHEN 2 THEN 6
      WHEN 3 THEN 9
      WHEN 4 THEN 12
     END; 

    IF UN_TIPO = 'I' 
    THEN 
      BEGIN
        <<ENCABEZADO>>
        FOR MI_RS_TOTALES IN (SELECT 
                                MI_CIFRACTRFUR FUENTE,
                                SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,
                                SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE) PRESUPUESTO_DEFINITIVO,
                                SUM(V_RESUMENPPTO_BASE.COMPROMISOSACUM) COMPROMISOS,
                                SUM(V_RESUMENPPTO_BASE.OBLIGACIONESACUM) OBLIGACIONES,
                                SUM(V_RESUMENPPTO_BASE.EJECUCIONPPT) PAGOS
                              FROM V_PLAN_PRESUPUESTAL
                                INNER JOIN  V_RESUMENPPTO_BASE 
                                  ON V_PLAN_PRESUPUESTAL.COMPANIA       =  V_RESUMENPPTO_BASE.COMPANIA 
                                  AND V_PLAN_PRESUPUESTAL.ANO            =  V_RESUMENPPTO_BASE.ANO
                                  AND V_PLAN_PRESUPUESTAL.ID             =  V_RESUMENPPTO_BASE.ID
                              WHERE V_PLAN_PRESUPUESTAL.COMPANIA  = UN_COMPANIA
                                AND V_RESUMENPPTO_BASE.ANO = UN_ANO
                                AND V_RESUMENPPTO_BASE.MES <= MI_MES
                                AND V_PLAN_PRESUPUESTAL.DESTINO   = UN_TIPO
                                AND V_RESUMENPPTO_BASE.NATURALEZA  ='D'
                                AND V_PLAN_PRESUPUESTAL.FUENTE_FUT          IS NOT NULL
                                AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA           ='VA'  
                            HAVING SUM(V_RESUMENPPTO_BASE.APROPIADO +
                                        V_RESUMENPPTO_BASE.APROPIACIONVIGENTE +
                                        V_RESUMENPPTO_BASE.COMPROMISOSACUM +
                                        V_RESUMENPPTO_BASE.OBLIGACIONESACUM +
                                        V_RESUMENPPTO_BASE.EJECUCIONPPT) <> 0
                            GROUP BY  MI_CIFRACTRFUR)
        LOOP 
            MI_CADENA := MI_CADENA ||
                             'D'   ||  CHR(9)||
                             'VAL' ||  CHR(9)||
                              MI_CIFRACTRFUR                ||CHR(9)||
                              MI_RS_TOTALES.PRESUPUESTO_INICIAL     ||CHR(9)||
                              MI_RS_TOTALES.PRESUPUESTO_DEFINITIVO  ||CHR(9)||
                              MI_RS_TOTALES.COMPROMISOS             ||CHR(9)||
                              MI_RS_TOTALES.OBLIGACIONES            ||CHR(9)||
                              MI_RS_TOTALES.PAGOS                   ||CHR(13)||CHR(10);                  
        END LOOP ENCABEZADO;
        <<DETALLE>>
        FOR MI_RS_DETALLE IN (SELECT 
                                V_PLAN_PRESUPUESTAL.CODIGOFUT_H AS CODIGO,
                                V_PLAN_PRESUPUESTAL.FUENTE_FUT AS FUENTE,
                                SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,
                                SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE) PRESUPUESTO_DEFINITIVO,
                                SUM(V_RESUMENPPTO_BASE.COMPROMISOSACUM) COMPROMISOS,
                                SUM(V_RESUMENPPTO_BASE.OBLIGACIONESACUM) OBLIGACIONES,
                                SUM(V_RESUMENPPTO_BASE.EJECUCIONPPT) PAGOS
                              FROM V_PLAN_PRESUPUESTAL
                              INNER JOIN  V_RESUMENPPTO_BASE 
                                    ON V_PLAN_PRESUPUESTAL.COMPANIA       =  V_RESUMENPPTO_BASE.COMPANIA 
                                   AND V_PLAN_PRESUPUESTAL.ANO            =  V_RESUMENPPTO_BASE.ANO
                                   AND V_PLAN_PRESUPUESTAL.ID             =  V_RESUMENPPTO_BASE.ID
                              WHERE V_PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                                AND V_PLAN_PRESUPUESTAL.DESTINO     = UN_TIPO
                                AND V_RESUMENPPTO_BASE.NATURALEZA   ='D'
                                AND V_PLAN_PRESUPUESTAL.FUENTE_FUT   IS NOT NULL
                                AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA  ='VA'
                                AND V_RESUMENPPTO_BASE.ANO = UN_ANO
                                AND V_RESUMENPPTO_BASE.MES <= MI_MES
                              HAVING SUM( V_RESUMENPPTO_BASE.APROPIADO +
                                          V_RESUMENPPTO_BASE.APROPIACIONVIGENTE +
                                          V_RESUMENPPTO_BASE.COMPROMISOSACUM +
                                          V_RESUMENPPTO_BASE.OBLIGACIONESACUM +
                                          V_RESUMENPPTO_BASE.EJECUCIONPPT) <> 0
                              GROUP BY  V_PLAN_PRESUPUESTAL.CODIGOFUT_H ,
                                        V_PLAN_PRESUPUESTAL.FUENTE_FUT 
                              ORDER BY V_PLAN_PRESUPUESTAL.CODIGOFUT_H)
          LOOP 
            MI_CADENA := MI_CADENA ||
                          'D'   ||  CHR(9)||
                          MI_RS_DETALLE.CODIGO ||  CHR(9)||
                          MI_RS_DETALLE.FUENTE ||CHR(9)||
                          MI_RS_DETALLE.PRESUPUESTO_INICIAL ||CHR(9)||
                          MI_RS_DETALLE.PRESUPUESTO_DEFINITIVO ||CHR(9)||
                          MI_RS_DETALLE.COMPROMISOS ||CHR(9)||
                          MI_RS_DETALLE.OBLIGACIONES ||CHR(9)||
                          MI_RS_DETALLE.PAGOS ||CHR(13)||CHR(10);                  

          END LOOP DETALLE;
      END;
   ELSE 
     BEGIN
        <<ENCABEZADO>>
        FOR MI_RS_TOTALES IN (SELECT 
                                V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT ,
                                V_PLAN_PRESUPUESTAL.FUENTE_FUT AS FUENTE,
                                SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,
                                SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE) PRESUPUESTO_DEFINITIVO,
                                SUM(V_RESUMENPPTO_BASE.COMPROMISOSACUM) COMPROMISOS,
                                SUM(V_RESUMENPPTO_BASE.OBLIGACIONESACUM) OBLIGACIONES,
                                SUM(V_RESUMENPPTO_BASE.EJECUCIONPPT) PAGOS
                              FROM V_PLAN_PRESUPUESTAL
                              INNER JOIN  V_RESUMENPPTO_BASE 
                                    ON V_PLAN_PRESUPUESTAL.COMPANIA       =  V_RESUMENPPTO_BASE.COMPANIA 
                                   AND V_PLAN_PRESUPUESTAL.ANO            =  V_RESUMENPPTO_BASE.ANO
                                   AND V_PLAN_PRESUPUESTAL.ID             =  V_RESUMENPPTO_BASE.ID
                              WHERE V_PLAN_PRESUPUESTAL.COMPANIA    = UN_COMPANIA
                                AND V_RESUMENPPTO_BASE.ANO = UN_ANO
                                AND V_RESUMENPPTO_BASE.MES <= MI_MES
                                AND V_PLAN_PRESUPUESTAL.DESTINO     = UN_TIPO
                                AND V_RESUMENPPTO_BASE.NATURALEZA   ='D'
                                AND V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT IS NOT NULL
                                AND V_PLAN_PRESUPUESTAL.FUENTE_FUT          IS NOT NULL
                                AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA           ='VA'  
                              HAVING SUM(V_RESUMENPPTO_BASE.APROPIADO +
                                V_RESUMENPPTO_BASE.APROPIACIONVIGENTE +
                                V_RESUMENPPTO_BASE.COMPROMISOSACUM +
                                V_RESUMENPPTO_BASE.OBLIGACIONESACUM +
                                V_RESUMENPPTO_BASE.EJECUCIONPPT) <> 0
                              GROUP BY  
                                V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT ,
                                V_PLAN_PRESUPUESTAL.FUENTE_FUT)
        LOOP 
            MI_CADENA := MI_CADENA ||
                             'D'   ||  CHR(9)||
                             'VAL' ||  CHR(9)||
                              MI_RS_TOTALES.UNIDADEJECUTORA_FUT     ||CHR(9)||
                              MI_RS_TOTALES.FUENTE                  ||CHR(9)||
                              MI_RS_TOTALES.PRESUPUESTO_INICIAL     ||CHR(9)||
                              MI_RS_TOTALES.PRESUPUESTO_DEFINITIVO  ||CHR(9)||
                              MI_RS_TOTALES.COMPROMISOS             ||CHR(9)||
                              MI_RS_TOTALES.OBLIGACIONES            ||CHR(9)||
                              MI_RS_TOTALES.PAGOS                   ||CHR(13)||CHR(10);                  
        END LOOP ENCABEZADO;
        <<DETALLE>>
        FOR MI_RS_DETALLE IN (SELECT 
                                    V_PLAN_PRESUPUESTAL.CODIGOFUT_H CODIGO, 
                                    V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT ,
                                    V_PLAN_PRESUPUESTAL.FUENTE_FUT AS FUENTE,
                                    SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,
                                    SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE) PRESUPUESTO_DEFINITIVO,
                                    SUM(V_RESUMENPPTO_BASE.COMPROMISOSACUM) COMPROMISOS,
                                    SUM(V_RESUMENPPTO_BASE.OBLIGACIONESACUM) OBLIGACIONES,
                                    SUM(V_RESUMENPPTO_BASE.EJECUCIONPPT) PAGOS
                                FROM V_PLAN_PRESUPUESTAL
                                INNER JOIN  V_RESUMENPPTO_BASE 
                                      ON V_PLAN_PRESUPUESTAL.COMPANIA       =  V_RESUMENPPTO_BASE.COMPANIA 
                                     AND V_PLAN_PRESUPUESTAL.ANO            =  V_RESUMENPPTO_BASE.ANO
                                     AND V_PLAN_PRESUPUESTAL.ID             =  V_RESUMENPPTO_BASE.ID
                                WHERE V_PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
                                  AND V_RESUMENPPTO_BASE.ANO = UN_ANO
                                  AND V_RESUMENPPTO_BASE.MES <= MI_MES
                                  AND V_PLAN_PRESUPUESTAL.DESTINO ='F'
                                  AND V_RESUMENPPTO_BASE.NATURALEZA  ='D'
                                  AND V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT IS NOT NULL
                                  AND V_PLAN_PRESUPUESTAL.FUENTE_FUT          IS NOT NULL
                                  AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA           ='VA'  
                               HAVING SUM(V_RESUMENPPTO_BASE.APROPIADO +
                                  V_RESUMENPPTO_BASE.APROPIACIONVIGENTE +
                                  V_RESUMENPPTO_BASE.COMPROMISOSACUM +
                                  V_RESUMENPPTO_BASE.OBLIGACIONESACUM +
                                  V_RESUMENPPTO_BASE.EJECUCIONPPT) <> 0
                                GROUP BY  
                                  V_PLAN_PRESUPUESTAL.CODIGOFUT_H , 
                                    V_PLAN_PRESUPUESTAL.UNIDADEJECUTORA_FUT ,
                                    V_PLAN_PRESUPUESTAL.FUENTE_FUT 
                                ORDER BY  V_PLAN_PRESUPUESTAL.CODIGOFUT_H )
          LOOP 
            MI_CADENA := MI_CADENA ||
                          'D'   ||  CHR(9)||
                          MI_RS_DETALLE.CODIGO ||  CHR(9)||
                          MI_RS_DETALLE.UNIDADEJECUTORA_FUT ||  CHR(9)||
                          MI_RS_DETALLE.FUENTE ||CHR(9)||
                          MI_RS_DETALLE.PRESUPUESTO_INICIAL ||CHR(9)||
                          MI_RS_DETALLE.PRESUPUESTO_DEFINITIVO ||CHR(9)||
                          MI_RS_DETALLE.COMPROMISOS ||CHR(9)||
                          MI_RS_DETALLE.OBLIGACIONES ||CHR(9)||
                          MI_RS_DETALLE.PAGOS ||CHR(13)||CHR(10);                  

          END LOOP DETALLE;
      END;
    END IF;  
RETURN MI_CADENA;
END FC_GENERARINCONSGASTOSINV;


PROCEDURE PR_TRASLADARCONFFUT
(
      /*
      NAME              : PR_TRASLADARCONFFUT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 18/07/2018
      TIME              : 16:00 PM
      SOURCE MODULE     : CHIP-FUT
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Procedimiento que traslada la configuracion fut al año siguiente
      MODIFICATIONS     : 

      @NAME:    trasladarConfSiguienteAnio
      @METHOD:  GET
    */

    UN_COMPANIA       PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO        PCK_SUBTIPOS.TI_USUARIO  
)
AS
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIOSIG        PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 

 MI_ANIOSIG := UN_ANIO + 1;
 --Insertar configuracion fut al siguiente año
 <<RECORRER_CONFFUT>>
 FOR MI_RS IN(SELECT COMPANIA ,
                      ANO ,
                      CODIGO ,
                      FORMULARIO ,
                      NOMBRE ,
                      MOVIMIENTO ,
                      CUENTA_PPTAL ,
                      NATURALEZA ,
                      CREATED_BY ,
                      MODIFIED_BY ,
                      DATE_CREATED ,
                      DATE_MODIFIED
              FROM CODIGOS_FUT_FORMULARIO
              WHERE COMPANIA = UN_COMPANIA
                AND ANO      = UN_ANIO)                    
 LOOP

        MI_STRSQL := 'SELECT ''X'' EXISTE
                      FROM CODIGOS_FUT_FORMULARIO
                      WHERE COMPANIA = '''||UN_COMPANIA||'''
                        AND ANO        = '||MI_ANIOSIG||'
                        AND CODIGO     = '''||MI_RS.CODIGO||''' ';

        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;           

        IF MI_CONTEO = 0 THEN

            MI_CAMPOS := 'COMPANIA
                         , ANO
                         , CODIGO
                         , FORMULARIO
                         , NOMBRE
                         , MOVIMIENTO
                         , CUENTA_PPTAL
                         , NATURALEZA
                         , CREATED_BY
                         , MODIFIED_BY';

            MI_VALORES := 'SELECT COMPANIA ,
                            ANO + 1,
                            CODIGO ,
                            FORMULARIO ,
                            NOMBRE ,
                            MOVIMIENTO ,
                            CUENTA_PPTAL ,
                            NATURALEZA ,
                            '''||UN_USUARIO||''',
                            SYSDATE
                          FROM CODIGOS_FUT_FORMULARIO
                          WHERE COMPANIA = '''||MI_RS.COMPANIA||'''
                            AND ANO      = '||MI_RS.ANO||'
                            AND CODIGO   = '''||MI_RS.CODIGO||''' ';    

        BEGIN                  
           BEGIN   
               PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME (UN_TABLA   => 'CODIGOS_FUT_FORMULARIO',
                                                    UN_ACCION  => 'IS',
                                                    UN_CAMPOS  => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORES); 



               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
          END; 

              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
              MI_MSGERROR(1).CLAVE := 'ANIO';
              MI_MSGERROR(1).VALOR := MI_ANIOSIG;
              MI_MSGERROR(2).CLAVE := 'CODIGO';
              MI_MSGERROR(2).VALOR := MI_RS.CODIGO;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_INS_CODIGOFUTFOR,
                                         UN_REEMPLAZOS  => MI_MSGERROR);

    END;                           

        END IF;
 END LOOP RECORRER_CONFFUT; 

  --Insertar fuentes fut al siguiente año

  <<RECORRER_FUENTEFUT>>
 FOR MI_RS IN(SELECT COMPANIA
                     , ANO
                     , CODIGO_FUT
                     , NOMBRE_FUT
                     , CODIGO_FORMULARIO
                     , CREATED_BY
                     , MODIFIED_BY
                     , DATE_MODIFIED
                     , DATE_CREATED
               FROM FUENTESFUTCATEGORIAS
               WHERE COMPANIA = UN_COMPANIA
                 AND ANO        = UN_ANIO)                    
 LOOP

        MI_STRSQL := 'SELECT ''X'' EXISTE
                        FROM FUENTESFUTCATEGORIAS
                        WHERE COMPANIA = '''||UN_COMPANIA||'''
                        AND ANO        = '||MI_ANIOSIG||'
                        AND CODIGO_FUT = '''||MI_RS.CODIGO_FUT||''' ';

        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;           

        IF MI_CONTEO = 0 THEN

            MI_CAMPOS := 'COMPANIA
                          ,ANO
                          ,CODIGO_FUT
                          ,NOMBRE_FUT
                          ,CODIGO_FORMULARIO
                          ,CREATED_BY
                          ,MODIFIED_BY';

            MI_VALORES := 'SELECT COMPANIA
                          , ANO + 1 
                          , CODIGO_FUT
                          , NOMBRE_FUT
                          , CODIGO_FORMULARIO
                          , '''||UN_USUARIO||'''
                          , SYSDATE
                          FROM FUENTESFUTCATEGORIAS
                          WHERE COMPANIA = '''||MI_RS.COMPANIA||'''
                            AND ANO      = '||MI_RS.ANO||'
                            AND CODIGO_FUT   = '''||MI_RS.CODIGO_FUT||''' ';    

         BEGIN
              BEGIN
               PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME (UN_TABLA   => 'FUENTESFUTCATEGORIAS',
                                                    UN_ACCION  => 'IS',
                                                    UN_CAMPOS  => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORES);             



                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
              END; 

              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
              MI_MSGERROR(1).CLAVE := 'ANIO';
              MI_MSGERROR(1).VALOR := MI_ANIOSIG;
              MI_MSGERROR(2).CLAVE := 'CODIGO';
              MI_MSGERROR(2).VALOR := MI_RS.CODIGO_FUT;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_INS_FUENTEFUTCATE,
                                         UN_REEMPLAZOS  => MI_MSGERROR);                                                    

         END;

        END IF;
 END LOOP RECORRER_FUENTEFUT; 


 --Actualizar configuracion de un año al siguiente año

 <<ACT_CONFFUT>> 
 FOR MI_RS IN (SELECT PLAN_PPTAL_CONFIG.COMPANIA ,
                      PLAN_PPTAL_CONFIG.ANO ,
                      PLAN_PPTAL_CONFIG.CENTRO_COSTO ,
                      PLAN_PPTAL_CONFIG.TERCERO ,
                      PLAN_PPTAL_CONFIG.SUCURSAL ,
                      PLAN_PPTAL_CONFIG.AUXILIAR ,
                      PLAN_PPTAL_CONFIG.REFERENCIA ,
                      PLAN_PPTAL_CONFIG.FUENTE_RECURSO ,
                      PLAN_PPTAL_CONFIG.DESTINO,
                      PLAN_PPTAL_CONFIG.CODIGO ,
                      PLAN_PPTAL_CONFIG.FUT_DESPLAZADOS1,
                      PLAN_PPTAL_CONFIG.FUT_CUENTAXPAGAR,
                      PLAN_PPTAL_CONFIG.FUT_EJECUCIONSALUD,
                      PLAN_PPTAL_CONFIG.FUT_SALUD_TESORERIA,
                      PLAN_PPTAL_CONFIG.FUT_VIGENCIA_FUTURA,
                      PLAN_PPTAL_CONFIG.FUT_INGRESOS_RESERVAS,
                      PLAN_PPTAL_CONFIG.FUT_VICTIMAS1,
                      PLAN_PPTAL_CONFIG.FUENTE_FLS,
                      PLAN_PPTAL_CONFIG.TIPOVIGENCIA
               FROM PLAN_PPTAL_CONFIG
               WHERE PLAN_PPTAL_CONFIG.COMPANIA = UN_COMPANIA
                 AND PLAN_PPTAL_CONFIG.ANO      = UN_ANIO)LOOP

 MI_VALORES := 'FUT_DESPLAZADOS1      = '''||MI_RS.FUT_DESPLAZADOS1||'''
               ,FUT_CUENTAXPAGAR      = '''||MI_RS.FUT_CUENTAXPAGAR||''' 
               ,FUT_EJECUCIONSALUD    = '''||MI_RS.FUT_EJECUCIONSALUD||'''  
               ,FUT_SALUD_TESORERIA   = '''||MI_RS.FUT_SALUD_TESORERIA||'''
               ,FUT_VIGENCIA_FUTURA   = '''||MI_RS.FUT_VIGENCIA_FUTURA||'''
               ,FUT_INGRESOS_RESERVAS = '''||MI_RS.FUT_INGRESOS_RESERVAS||'''
               ,FUT_VICTIMAS1         = '''||MI_RS.FUT_VICTIMAS1||'''
               ,FUENTE_FLS            = '''||MI_RS.FUENTE_FLS||'''
               ,TIPOVIGENCIA          = '''||MI_RS.TIPOVIGENCIA||''' ';


  MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA||'''
                   AND ANO     = '||MI_ANIOSIG||'
                   AND CODIGO  = '''||MI_RS.CODIGO|| '''';   

        BEGIN
         BEGIN
           PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PPTAL_CONFIG',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_VALORES,
                                                UN_CONDICION => MI_CONDICION);             

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
         END; 

              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN 
              MI_MSGERROR(1).CLAVE := 'CUENTA';
              MI_MSGERROR(1).VALOR := MI_RS.CODIGO;
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR := MI_ANIOSIG;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPPTALCONFIG,
                                         UN_REEMPLAZOS  => MI_MSGERROR);                                                    

         END;              

 END LOOP ACT_CONFFUT;


END PR_TRASLADARCONFFUT;

FUNCTION FC_GENERARINGRESOSFUT(

      /*
      NAME              : FC_GENERARARCHIVOPLANOINGRESOSFUT 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
      DATE MIGRADOR     : 18/07/2018
      TIME              : 08:30 AM
      SOURCE MODULE     : SysmanChip2018.04.04
      DESCRIPTION       : Archivo plano de Ingresos fut. 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     :                                 
      PARAMETERS        :   UN_COMPANIA 	      => Compania que ingreso a la aplicación
                            UN_ANO              => Año que selecciono en la aplicación
                            UN_CODIGOENTIDAD    => Codigo de la entidad que se selecciona en la aplicación,
                            UN_MES              => Mes seleccionado en la aplicación,
                            UN_TRIMESTRE        => Trimestre seleccionado en la aplicación,
                            UN_TRANSBANCOS      => Check para saber si se genera el informe con transbancos o no,
                            UN_MILES            => Check para saber si se genera el informe con miles o no,



    @NAME:   generarArchivoPlanoIngresosFut
    @METHOD: GET   
    */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
      UN_CODIGOENTIDAD    IN VARCHAR2,
      UN_MES              IN PCK_SUBTIPOS.TI_ENTERO,
      UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
      UN_TRANSBANCOS      IN PCK_SUBTIPOS.TI_LOGICO,
      UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
      UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS
      MI_RTA            CLOB;
      MI_MESFINAL       PCK_SUBTIPOS.TI_ENTERO;
      MI_MESINICIAL     PCK_SUBTIPOS.TI_ENTERO ;
      MI_TIPOENTIDAD    COMPANIA.TIPOENTIDAD%TYPE;
      MI_PARDIGITO      PCK_SUBTIPOS.TI_ENTERO;
BEGIN
      MI_PARDIGITO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(
                                    UN_COMPANIA  => UN_COMPANIA	  ,
                                    UN_NOMBRE    => 'DIGITO REDONDEO DE INFORMES FUT' ,
                                    UN_MODULO 	 => PCK_DATOS.FC_MODULOENTESDECONTROL ,
                                    UN_FECHA_PAR => SYSDATE        
                                ),2));
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
      UN_EXC_COD      =>SQLCODE,
      UN_ERROR_COD    =>PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD,
      UN_REEMPLAZOS   =>MI_REEMPLAZOS);
END;

IF UN_MES = 0 THEN

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

ELSE 
      IF UN_MES = 1 THEN
        MI_MESFINAL := UN_TRIMESTRE;
      ELSE 
        MI_MESFINAL   := UN_TRIMESTRE;
      END IF;
END IF;

IF UN_EXCEL = 0 THEN  
      MI_RTA:='S'||CHR(9)||
              UN_CODIGOENTIDAD||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
              END||CHR(9)||
              UN_ANO||CHR(9)||
              'INGRESOS FUT'||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY')
              ||CHR(13)||CHR(10);
ELSE
           MI_RTA:='CODIGO'                 ||CHR(9)||
                   'NOMBRE'                 ||CHR(9)||
                   'DESCRIPCION'            ||CHR(9)||
                   'PRESUPUESTO_INICIAL'    ||CHR(9)||
                   'PRESUPUESTO_DEFINITIVO' ||CHR(9)||
                   'SINFONDOS'              ||CHR(9)||
                   'DOCUMENTOSOPORTE'       ||CHR(9)||
                   'NUMERO_DOCUMENTO'       ||CHR(9)||
                   'PORCENTAJE_DESTINACION' ||CHR(9)||
                   'VALOR_DESTINACION'   
                   ||CHR(13)||CHR(10); 
END IF;
  <<TOTALES>>
  FOR MI_RS_TOTALES IN (SELECT 
                            SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,  
                            SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE)PRESUPUESTO_DEFINITIVO,
                            SUM(CASE  WHEN V_PLAN_PRESUPUESTAL.CONSITUACIONFONDOS NOT IN(0)
                                  THEN EJECUCIONPPT
                                  ELSE 0   
                                  END) AS RECUADO   , 
                            SUM(CASE  WHEN V_PLAN_PRESUPUESTAL.CONSITUACIONFONDOS IN(0)
                                THEN EJECUCIONPPT
                                ELSE 0   
                                END) AS SINFONDOS,
                            SUM(EJECUCIONPPT)RECUADOFONDO,
                             'No' AS DOCUMENTOSOPORTE,
                             'NA' AS NUMERO_DOCUMENTO,
                             '0' AS PORCENTAJE_DESTINACION,
                              0 AS VALOR_DESTINACION
                           FROM V_PLAN_PRESUPUESTAL
                              INNER JOIN V_RESUMENPPTO_BASE
                                 ON V_PLAN_PRESUPUESTAL.COMPANIA = V_RESUMENPPTO_BASE.COMPANIA
                                 AND V_PLAN_PRESUPUESTAL.ANO = V_RESUMENPPTO_BASE.ANO
                                 AND V_PLAN_PRESUPUESTAL.ID = V_RESUMENPPTO_BASE.ID
                            WHERE V_PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
                            AND V_PLAN_PRESUPUESTAL.ANO = UN_ANO
                            AND V_RESUMENPPTO_BASE.MES <= MI_MESFINAL
                            AND V_PLAN_PRESUPUESTAL.CODIGOFUT_H IS NOT NULL
                            AND V_PLAN_PRESUPUESTAL.REGALIAS IN (0)
                            AND V_PLAN_PRESUPUESTAL.NATURALEZA IN ('C'))   
  LOOP 
        MI_RTA := MI_RTA
           ||'D'                          ||CHR(9)
           ||'VAL'                        ||CHR(9)
           ||MI_RS_TOTALES.PRESUPUESTO_INICIAL       ||CHR(9)
           ||MI_RS_TOTALES.PRESUPUESTO_DEFINITIVO    ||CHR(9)
           ||MI_RS_TOTALES.RECUADO                   ||CHR(9)
           ||MI_RS_TOTALES.SINFONDOS                 ||CHR(9)
           ||MI_RS_TOTALES.RECUADOFONDO              ||CHR(9)
           ||MI_RS_TOTALES.DOCUMENTOSOPORTE          ||CHR(9)
           ||MI_RS_TOTALES.NUMERO_DOCUMENTO          ||CHR(9)
           ||MI_RS_TOTALES.PORCENTAJE_DESTINACION    ||CHR(9)
           ||MI_RS_TOTALES.VALOR_DESTINACION 
           ||CHR(13)||CHR(10);
  END LOOP TOTALES;
FOR RS IN (
           SELECT 
            V_PLAN_PRESUPUESTAL.CODIGOFUT_H AS CODIGO,
            SUM(V_RESUMENPPTO_BASE.APROPIADO) PRESUPUESTO_INICIAL,  
            SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE)PRESUPUESTO_DEFINITIVO,
            SUM(CASE  WHEN V_PLAN_PRESUPUESTAL.CONSITUACIONFONDOS NOT IN(0)
                  THEN EJECUCIONPPT
                  ELSE 0   
                  END) AS RECUADO   , 
            SUM(CASE  WHEN V_PLAN_PRESUPUESTAL.CONSITUACIONFONDOS IN(0)
                THEN EJECUCIONPPT
                ELSE 0   
                END) AS SINFONDOS,
             SUM(EJECUCIONPPT) RECAUDOFONDO,    
             'No' AS DOCUMENTOSOPORTE,
             'NA' AS NUMERO_DOCUMENTO,
             '0' AS PORCENTAJE_DESTINACION,
              0 AS VALOR_DESTINACION
           FROM V_PLAN_PRESUPUESTAL
              INNER JOIN V_RESUMENPPTO_BASE
                 ON V_PLAN_PRESUPUESTAL.COMPANIA = V_RESUMENPPTO_BASE.COMPANIA
                 AND V_PLAN_PRESUPUESTAL.ANO = V_RESUMENPPTO_BASE.ANO
                 AND V_PLAN_PRESUPUESTAL.ID = V_RESUMENPPTO_BASE.ID
            WHERE V_PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
            AND V_PLAN_PRESUPUESTAL.ANO = UN_ANO
            AND V_RESUMENPPTO_BASE.MES <= MI_MESFINAL 
            AND V_PLAN_PRESUPUESTAL.CODIGOFUT_H IS NOT NULL
            AND V_PLAN_PRESUPUESTAL.REGALIAS IN (0)
            AND V_PLAN_PRESUPUESTAL.NATURALEZA IN ('C')
            GROUP BY 
              V_PLAN_PRESUPUESTAL.CODIGOFUT_H)                                                                              
LOOP
      MI_RTA := MI_RTA
           ||'D'                          ||CHR(9)
           ||RS.CODIGO                    ||CHR(9)
           ||RS.PRESUPUESTO_INICIAL       ||CHR(9)
           ||RS.PRESUPUESTO_DEFINITIVO    ||CHR(9)
           ||RS.RECUADO                   ||CHR(9)
           ||RS.SINFONDOS                 ||CHR(9)
           ||RS.RECAUDOFONDO              ||CHR(9)
           ||RS.DOCUMENTOSOPORTE          ||CHR(9)
           ||RS.NUMERO_DOCUMENTO          ||CHR(9)
           ||RS.PORCENTAJE_DESTINACION    ||CHR(9)
           ||RS.VALOR_DESTINACION 
           ||CHR(13)||CHR(10);
END LOOP;

RETURN MI_RTA;

END FC_GENERARINGRESOSFUT;

FUNCTION FC_GENERAR2009BDMESEMESTRAL(

      /*
      NAME              : FC_GENERAR2009BDMESEMESTRAL 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 31/05/2021
      TIME              : 08:30 AM
      SOURCE MODULE     : 
      DESCRIPTION       : Archivo CGN2009_BDME_REPORTE_SEMESTRAL. 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     :                                 
      PARAMETERS        : 

    @NAME:   generarArchivoPlano2009BDME
    @METHOD: GET   
    */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
      UN_CODIGOENTIDAD    IN VARCHAR2,
      UN_MES              IN PCK_SUBTIPOS.TI_ENTERO,
      UN_FECHA            IN VARCHAR2
      
)
RETURN CLOB
AS
      MI_RTA            CLOB := '';
      MI_MESFINAL       PCK_SUBTIPOS.TI_ENTERO;
      MI_MESINICIAL     PCK_SUBTIPOS.TI_ENTERO ;
      MI_TIPOENTIDAD    COMPANIA.TIPOENTIDAD%TYPE;
      MI_PARDIGITO      PCK_SUBTIPOS.TI_ENTERO;
BEGIN
      
      MI_RTA := 'S' ||CHR(9)
                ||UN_CODIGOENTIDAD||CHR(9)
                ||1||LPAD(UN_MES,2,0)||LPAD(UN_MES,2,0)||CHR(9)
                ||UN_ANIO||CHR(9)
                ||'CGN2009_BDME_REPORTE_SEMESTRAL'||CHR(9)
                ||TO_CHAR(CURRENT_DATE,'DD-MM-YYYY') ||CHR(13)||CHR(10);

    FOR RS IN (WITH CONSULTA AS (SELECT  CASE WHEN DEUDORES.DEUDORES >0 THEN '2' ELSE '1' END CONCEPTO,
                    CASE WHEN  TERCERO.NATURALEZA = 'J' THEN '2' ELSE '1' END TIPODEUDOR,
                   DETALLE_COMPROBANTE_CNT.COMPROBANTE NUMERO ,
                   DETALLE_COMPROBANTE_CNT.TERCERO  NIT,       
                  CASE WHEN  TERCERO.TIPOID = 'C' THEN 1    
                       WHEN  TERCERO.TIPOID = 'N' THEN 2 
                       WHEN  TERCERO.TIPOID = 'T' THEN 3  
                       WHEN  TERCERO.TIPOID = 'E' THEN 4 END TIPOID  ,
                   TERCERO.NOMBRE  ,
                   SUM((DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)
                -CASE WHEN DETALLE_COMPROBANTE_CNT.FECHA_ABONOINICIAL<= TO_DATE(UN_FECHA) THEN DETALLE_COMPROBANTE_CNT.ABONOINICIAL ELSE 0 END)
              -NVL(AFEC.VALOR_AFECTACION,0)  VALOR,
                   'N/A' ESTADO     ,
                    ANO.SALARIOMINIMO
            FROM COMPROBANTE_CNT
            INNER JOIN TERCERO ON COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA
            AND COMPROBANTE_CNT.TERCERO = TERCERO.NIT  
            AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
            INNER JOIN TIPO_COMPROBANTE ON COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA
                                           AND COMPROBANTE_CNT.TIPO = TIPO_COMPROBANTE.CODIGO
            INNER JOIN DETALLE_COMPROBANTE_CNT ON COMPROBANTE_CNT.COMPANIA =  DETALLE_COMPROBANTE_CNT.COMPANIA
            AND COMPROBANTE_CNT.ANO = DETALLE_COMPROBANTE_CNT.ANO
            AND COMPROBANTE_CNT.TIPO = DETALLE_COMPROBANTE_CNT.TIPO_CPTE
            AND COMPROBANTE_CNT.NUMERO = DETALLE_COMPROBANTE_CNT.COMPROBANTE	
            INNER JOIN PLAN_CONTABLE ON PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                                        AND   PLAN_CONTABLE.CODIGO = DETALLE_COMPROBANTE_CNT.CUENTA
                                        AND   PLAN_CONTABLE.ANO = DETALLE_COMPROBANTE_CNT.ANO
              LEFT JOIN (SELECT COUNT(*)DEUDORES,
                   DEUDOR_SOLIDARIO.COMPANIA,
                   DEUDOR_SOLIDARIO.NIT_DEUDORPRINCIPAL,
                   DEUDOR_SOLIDARIO.SUCURSAL_DEUDORPRINCIPAL
            FROM DEUDOR_SOLIDARIO
            INNER JOIN TERCERO ON DEUDOR_SOLIDARIO.COMPANIA = TERCERO.COMPANIA
                                  AND NIT_DEUDORPRINCIPAL = TERCERO.NIT
                                  AND SUCURSAL_DEUDORPRINCIPAL = TERCERO.SUCURSAL
            GROUP BY DEUDOR_SOLIDARIO.COMPANIA,
                     DEUDOR_SOLIDARIO.NIT_DEUDORPRINCIPAL,
                     DEUDOR_SOLIDARIO.SUCURSAL_DEUDORPRINCIPAL) DEUDORES ON TERCERO.COMPANIA = DEUDORES.COMPANIA
                        AND  DEUDORES.NIT_DEUDORPRINCIPAL = TERCERO.NIT
                        AND DEUDORES.SUCURSAL_DEUDORPRINCIPAL = TERCERO.SUCURSAL
            INNER JOIN ANO ON ANO.COMPANIA = COMPROBANTE_CNT.COMPANIA
            AND ANO.NUMERO = UN_ANIO
                  LEFT JOIN (SELECT DET.COMPANIA,
                      DET.TERCERO,
                      DET.SUCURSAL,
                      DET.CMPTE_AFECTADO AS FACTURA,
                      DET.TIPO_CPTE_AFECT AS TIPO_FACTURA,
                      DET.CUENTA,
                      SUM((NVL(DET.VALOR_CREDITO,0)-NVL(DET.VALOR_DEBITO,0))) VALOR_AFECTACION,
                      SUM((NVL(DET.VALOR_DEBITO,0))) DEBITO_AFECTADO,
                      SUM((NVL(DET.VALOR_CREDITO,0))) CREDITO_AFECTADO
               FROM  TIPO_COMPROBANTE TC INNER JOIN COMPROBANTE_CNT CN
                  ON CN.COMPANIA = TC.COMPANIA
                 AND CN.TIPO     = TC.CODIGO
               INNER JOIN  DETALLE_COMPROBANTE_CNT DET
                  ON CN.NUMERO = DET.COMPROBANTE
                 AND CN.TIPO = DET.TIPO_CPTE
                 AND CN.ANO = DET.ANO
                 AND CN.COMPANIA = DET.COMPANIA
              INNER JOIN PLAN_CONTABLE PLAN
                ON PLAN.COMPANIA = DET.COMPANIA
                AND PLAN.ANO      = DET.ANO
                AND PLAN.CODIGO  = DET.CUENTA               
              WHERE DET.COMPANIA = UN_COMPANIA
                 AND DET.ANO <=UN_ANIO
                 --PILAS CON FECHA
                 AND DET.FECHA <= TO_DATE(UN_FECHA)
                 AND TC.CLASE_CONTABLE In ('I','B','N','C')
                 AND DET.CMPTE_AFECTADO<>0
                 AND PLAN.CLASECUENTA In ('A','C')
             GROUP BY DET.COMPANIA,
                      DET.TERCERO,
                      DET.SUCURSAL,
                      DET.CMPTE_AFECTADO,
                      DET.TIPO_CPTE_AFECT,
                      DET.CUENTA   
        ) AFEC
        ON DETALLE_COMPROBANTE_CNT.CUENTA      = AFEC.CUENTA
       AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = AFEC.FACTURA
       AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = AFEC.TIPO_FACTURA
       AND DETALLE_COMPROBANTE_CNT.COMPANIA    = AFEC.COMPANIA
       AND DETALLE_COMPROBANTE_CNT.TERCERO     = AFEC.TERCERO
       AND DETALLE_COMPROBANTE_CNT.SUCURSAL    = AFEC.SUCURSAL    
            WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                  AND TIPO_COMPROBANTE.CLASE_CONTABLE IN('V')
                  AND PLAN_CONTABLE.CLASECUENTA IN ('C')
                  GROUP BY  
                   CASE WHEN DEUDORES.DEUDORES >0 THEN '2' ELSE '1' END ,
                    CASE WHEN  TERCERO.NATURALEZA = 'J' THEN '2' ELSE '1' END,
                     DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE ,
                   DETALLE_COMPROBANTE_CNT.TERCERO,                
                   DETALLE_COMPROBANTE_CNT.SUCURSAL, 
                   COMPROBANTE_CNT.FECHA_VCN_DOC,
                   ANO.SALARIOMINIMO,
                   CASE WHEN  TERCERO.TIPOID = 'C' THEN 1    
                       WHEN  TERCERO.TIPOID = 'N' THEN 2 
                       WHEN  TERCERO.TIPOID = 'T' THEN 3  
                       WHEN  TERCERO.TIPOID = 'E' THEN 4 END,
                   TERCERO.NOMBRE,
                   'N/A',
                    TERCERO.NATURALEZA,
                    '',
                    CASE WHEN  TERCERO.NATURALEZA = 'J' THEN 'Juridica' ELSE 'Natural' END,
                    CASE WHEN DETALLE_COMPROBANTE_CNT.FECHA_ABONOINICIAL<= TO_DATE(UN_FECHA) THEN DETALLE_COMPROBANTE_CNT.ABONOINICIAL ELSE 0 END,
                      NVL(AFEC.VALOR_AFECTACION,0) 
                   HAVING SUM((DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)
                -CASE WHEN DETALLE_COMPROBANTE_CNT.FECHA_ABONOINICIAL<= TO_DATE(UN_FECHA) THEN DETALLE_COMPROBANTE_CNT.ABONOINICIAL ELSE 0 END)
              -NVL(AFEC.VALOR_AFECTACION,0) > 0
                     AND MONTHS_BETWEEN( TO_DATE(UN_FECHA),COMPROBANTE_CNT.FECHA_VCN_DOC) > 6
                   ORDER BY DETALLE_COMPROBANTE_CNT.TERCERO,  DETALLE_COMPROBANTE_CNT.COMPROBANTE DESC)
                    SELECT CONSULTA.CONCEPTO,
                    CONSULTA.TIPODEUDOR,
                    CONSULTA.NUMERO ,
                    CONSULTA.NIT,       
                   CONSULTA.TIPOID  ,
                  CONSULTA.NOMBRE  ,                   
                    CONSULTA.VALOR,
                   CONSULTA.ESTADO    FROM CONSULTA
                 INNER JOIN (SELECT SUM(VALOR), CONCEPTO,
                    TIPODEUDOR,                    
                    NIT,       
                   TIPOID  ,
                  NOMBRE  ,                                       
                   ESTADO   FROM CONSULTA
                   GROUP BY CONCEPTO,
                    TIPODEUDOR,                    
                    NIT,       
                   TIPOID  ,
                  NOMBRE  ,                                       
                   ESTADO ,
                   SALARIOMINIMO
                   HAVING SUM(VALOR)> SALARIOMINIMO* 5  )SC ON  CONSULTA.CONCEPTO = SC.CONCEPTO
                   AND CONSULTA.TIPODEUDOR = SC.TIPODEUDOR
                   AND CONSULTA.NIT = SC.NIT
                   AND CONSULTA.TIPOID = SC.TIPOID
                   AND CONSULTA.NOMBRE = SC.NOMBRE
                   AND CONSULTA.ESTADO = SC.ESTADO)LOOP
                   

                   --<TICKET:7715693 FECHA:02/062022 AUTOR:CP>Se revisa la consulta y se evidencia que se esta ejecutando una diferencia entre fechas para determinar la cantidad de meses  esta condición  esta en el where cuando se esta ejecutando  una consulta de agrupamiento por esta razón no debe ir en el where sino  en el  having 
                    --AND MONTHS_BETWEEN( TO_DATE(UN_FECHA),COMPROBANTE_CNT.FECHA_VCN_DOC) > 6
     MI_RTA := TO_CLOB(MI_RTA
           ||'D'                           ||CHR(9)
           ||TO_CLOB(RS.CONCEPTO)                   ||CHR(9)
           ||TO_CLOB(RS.TIPODEUDOR)                 ||CHR(9)
           ||TO_CLOB(LPAD(RS.NUMERO,20,0))          ||CHR(9)
           ||TO_CLOB(RS.NIT)                        ||CHR(9)
           ||TO_CLOB(RS.TIPOID)                     ||CHR(9)          
           ||TO_CLOB(SUBSTR(RS.NOMBRE,0,100))        ||CHR(9)
           ||TO_CLOB(RS.VALOR)                      ||CHR(9)
           ||TO_CLOB(RS.ESTADO)
           ||CHR(13)||CHR(10) );  

    END LOOP;
      
RETURN TO_CLOB(MI_RTA);

END FC_GENERAR2009BDMESEMESTRAL;

FUNCTION FC_GENERASALDOMOVMES
(
 /*
      NAME              : FC_GENERASALDOMOVMES 
      AUTHOR            : MARIA CAMILA ROSERO PAZOS
      DATE              : 08/02/2023
      TIME              : 11:01 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Archivo plano de los saldos y movimientos POR MES
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       => Compañiaa de ingreso a la aplicaciÃ³n
                          UN_ANIO           =>Año contable del que se desea obtener la informacionn.
                          UN_MES            => Mes para el cual se va a rendir la informacionn del plano.
                          UN_CODIGOENTIDAD  =>  Codigo de la entidad reciproca. 
                          UN_DIGITOS        => Numero de digitos para filtrar id del plan contable

      @NAME  :  generarPlanoSaldoMovimientoMes
      @METHOD:  GET 
    */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES              IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CODIGOENTIDAD    IN VARCHAR2,
    UN_DIGITOS          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CENTAVOS         IN PCK_SUBTIPOS.TI_LOGICO,
	UN_COVID			IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS 
    MI_RTA                  CLOB;
    MI_CADENA_UNO           CLOB;
    MI_CADENA_DOS           CLOB;
    MI_CONCEPTO             VARCHAR2(20 CHAR);
    MI_SALDOINICIAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_DEBITO               PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTALDEBITO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTAUNOINI         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTADOSINI         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTATRESINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACUATROINI      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACINCOINI       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASEISINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASIETEINI       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTAUNOFIN         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTADOSFIN         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTATRESFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACUATROFIN      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTACINCOFIN       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASEISFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTASIETEFIN       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CREDITO              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TOTALCREDITO         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDOFIN             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDONOCOR           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALDOCOR             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIA           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIAINI        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIFERENCIAFIN        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TOTDEB               VARCHAR2(25 CHAR);
    MI_TOTCRE               VARCHAR2(25 CHAR);
    MI_TOTDIS               VARCHAR2(25 CHAR);
    MI_TOTCUENTAUNOINI      VARCHAR2(25 CHAR);
    MI_TOTCUENTADOSINI      VARCHAR2(25 CHAR);
    MI_TOTCUENTATRESINI     VARCHAR2(25 CHAR);
    MI_TOTCUENTACUATROINI   VARCHAR2(25 CHAR);
    MI_TOTCUENTACINCOINI    VARCHAR2(25 CHAR);
    MI_TOTCUENTASEISINI     VARCHAR2(25 CHAR);
    MI_TOTCUENTASIETEINI    VARCHAR2(25 CHAR);
    MI_TOTCUENTAUNOFIN      VARCHAR2(25 CHAR);
    MI_TOTCUENTADOSFIN      VARCHAR2(25 CHAR);
    MI_TOTCUENTATRESFIN     VARCHAR2(25 CHAR);
    MI_TOTCUENTACUATROFIN   VARCHAR2(25 CHAR);
    MI_TOTCUENTACINCOFIN    VARCHAR2(25 CHAR);
    MI_TOTCUENTASEISFIN     VARCHAR2(25 CHAR);
    MI_TOTCUENTASIETEFIN    VARCHAR2(25 CHAR);
    MI_DIFERENCIAINICIAL    VARCHAR2(25 CHAR);
    MI_DIFERENCIAFINAL      VARCHAR2(25 CHAR);
    MI_NOMCUENTAUNO         VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTADOS         VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTATRES        VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTACUATRO      VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTACINCO       VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTASEIS        VARCHAR2(50 CHAR) DEFAULT ' ';
    MI_NOMCUENTASIETE       VARCHAR2(50 CHAR) DEFAULT ' ';
BEGIN 

    IF UN_EXCEL = 0 THEN 
      MI_CADENA_UNO :=   'S' ||  CHR(9)|| UN_CODIGOENTIDAD ||CHR(9)||
                  '0' ||  UN_MES ||CHR(9)||
                  UN_ANO||CHR(9)||'CGN2015_001_SALDOS_Y_MOVIMIENTOS_CONVERGENCIA'||CHR(9)||
                  TO_CHAR(SYSDATE, 'DD-MM-YYYY') ||CHR(13)||CHR(10);
    ELSE
      MI_CADENA_UNO := 'CODIGO'                  ||CHR(9)|| 
                       'CONCEPTO'                ||CHR(9);

          IF(UN_COVID <> 0) THEN 
            MI_CADENA_UNO :=  MI_CADENA_UNO || 'DESCRIPCION'  ||CHR(9);
         END IF;
         MI_CADENA_UNO := MI_CADENA_UNO ||
                'SALDO INICIAL'           ||CHR(9)||
                'MOVIMIENTO DEBITO'       ||CHR(9)||
                'MOVIMIENTO CREDITO'      ||CHR(9)||
                'SALDO FINAL'             ||CHR(9)||
                'SALDO FINAL CORRIENTE'   ||CHR(9)||
                'SALDO FINAL NO CORRIENTE'        
                ||CHR(13)||CHR(10);
    END IF;  
    MI_CADENA_DOS :='Validacion del formato CGN2015_001_SALDOS_Y_MOVIMIENTOS_CONVERGENCIA' ||CHR(13)||CHR(10);
    --<AUTOR:CP FECHA:2701/2023 TICKET:Ticket#7723657 â€” Solicitud revision y ajuste Saldos y movimientos >
    --Se agrega proceso de mayorizaciÃ³n y se agrega columnas SALDO_FINAL_CORRIENTE y SALDO_FINAL_NO_CORRIENTE

    /*FECHA:03/03/2023 TICKET:Ticket#7728108  â€” Solicitud revision y ajuste Saldos y movimientos >
   Se ajusta y se agrega proceso de distribucion del saldo final a las columnas SALDO_FINAL_CORRIENTE y SALDO_FINAL_NO_CORRIENTE 
    respectivamente cuando los auxiliares tienen porcion corriente y no corriente en el mismo capitulo de la cuenta*/
    <<SALDOSMOVIMIENTOS>>  
    FOR RS IN ( 
      WITH SALDOCORRIENTE AS  (
        SELECT 
           D.ID,D.NOMBRE,D.NATURALEZA,D.CORRIENTE,D.SALDOINICIAL,D.DEBITO,D.CREDITO,D.SALDOFINAL
           ,SUM(CASE  WHEN LENGTH(D.ID) < UN_DIGITOS THEN 0 ELSE CASE WHEN SSCS.CORRIENTE NOT IN(0) THEN SSCS.SALDOFINAL ELSE 0 END END )   SALDO_FINAL_CORRIENTE 
           ,SUM(CASE  WHEN LENGTH(D.ID) < UN_DIGITOS THEN 0 ELSE CASE WHEN SSCS.CORRIENTE IN(0) THEN SSCS.SALDOFINAL ELSE 0 END END ) SALDO_FINAL_NO_CORRIENTE
           , D.DESCRIPCION
        FROM (
                SELECT 
                    ID,
                    NOMBRE,
                    NATURALEZA,
                    CORRIENTE,
                    TO_NUMBER(SUM(CASE UN_MES
                        WHEN 1  THEN SALDO0
                        WHEN 2  THEN SALDO1
                        WHEN 3  THEN SALDO2
                        WHEN 4  THEN SALDO3
                        WHEN 5  THEN SALDO4
                        WHEN 6  THEN SALDO5
                        WHEN 7  THEN SALDO6
                        WHEN 8  THEN SALDO7
                        WHEN 9  THEN SALDO8
                        WHEN 10 THEN SALDO9
                        WHEN 11 THEN SALDO10
                        WHEN 12 THEN SALDO11
                        WHEN 13 THEN SALDO12
                    END)) SALDOINICIAL,
                    TO_NUMBER(SUM(CASE UN_MES
                        WHEN 1 THEN DEBITO1
                        WHEN 2 THEN DEBITO2
                        WHEN 3 THEN DEBITO3
                        WHEN 4 THEN DEBITO4
                        WHEN 5 THEN DEBITO5
                        WHEN 6 THEN DEBITO6
                        WHEN 7 THEN DEBITO7
                        WHEN 8 THEN DEBITO8
                        WHEN 9 THEN DEBITO9
                        WHEN 10 THEN DEBITO10
                        WHEN 11 THEN DEBITO11
                        WHEN 12 THEN DEBITO12
                        
                    END)) DEBITO,
                   TO_NUMBER( SUM(CASE UN_MES 
                        WHEN 1 THEN CREDITO1
                        WHEN 2 THEN CREDITO2
                        WHEN 3 THEN CREDITO3
                        WHEN 4 THEN CREDITO4
                        WHEN 5 THEN CREDITO5
                        WHEN 6 THEN CREDITO6
                        WHEN 7 THEN CREDITO7
                        WHEN 8 THEN CREDITO8
                        WHEN 9 THEN CREDITO9
                        WHEN 10 THEN CREDITO10
                        WHEN 11 THEN CREDITO11
                        WHEN 12 THEN CREDITO12
                    END)) CREDITO,
                      TO_NUMBER(SUM(CASE WHEN NATURALEZA IN('D')
                            THEN CASE UN_MES  
                        WHEN 1  THEN SALDO0
                        WHEN 2  THEN SALDO1
                        WHEN 3  THEN SALDO2
                        WHEN 4  THEN SALDO3
                        WHEN 5  THEN SALDO4
                        WHEN 6  THEN SALDO5
                        WHEN 7  THEN SALDO6
                        WHEN 8  THEN SALDO7
                        WHEN 9  THEN SALDO8
                        WHEN 10 THEN SALDO9
                        WHEN 11 THEN SALDO10
                        WHEN 12 THEN SALDO11
                        WHEN 13 THEN SALDO12
                              END +
                              CASE UN_MES 
                                WHEN 1 THEN DEBITO1
                                WHEN 2 THEN DEBITO2
                                WHEN 3 THEN DEBITO3
                                WHEN 4 THEN DEBITO4
                                WHEN 5 THEN DEBITO5
                                WHEN 6 THEN DEBITO6
                                WHEN 7 THEN DEBITO7
                                WHEN 8 THEN DEBITO8
                                WHEN 9 THEN DEBITO9
                                WHEN 10 THEN DEBITO10
                                WHEN 11 THEN DEBITO11
                                WHEN 12 THEN DEBITO12
                              END -
                              CASE UN_MES  
                                WHEN 1 THEN CREDITO1
                                WHEN 2 THEN CREDITO2
                                WHEN 3 THEN CREDITO3
                                WHEN 4 THEN CREDITO4
                                WHEN 5 THEN CREDITO5
                                WHEN 6 THEN CREDITO6
                                WHEN 7 THEN CREDITO7
                                WHEN 8 THEN CREDITO8
                                WHEN 9 THEN CREDITO9
                                WHEN 10 THEN CREDITO10
                                WHEN 11 THEN CREDITO11
                                WHEN 12 THEN CREDITO12
                              END
                          ELSE 
                             CASE UN_MES  
                        WHEN 1  THEN SALDO0
                        WHEN 2  THEN SALDO1
                        WHEN 3  THEN SALDO2
                        WHEN 4  THEN SALDO3
                        WHEN 5  THEN SALDO4
                        WHEN 6  THEN SALDO5
                        WHEN 7  THEN SALDO6
                        WHEN 8  THEN SALDO7
                        WHEN 9  THEN SALDO8
                        WHEN 10 THEN SALDO9
                        WHEN 11 THEN SALDO10
                        WHEN 12 THEN SALDO11
                        WHEN 13 THEN SALDO12 
                              END +
                              CASE UN_MES  
                                WHEN 1 THEN CREDITO1
                                WHEN 2 THEN CREDITO2
                                WHEN 3 THEN CREDITO3
                                WHEN 4 THEN CREDITO4
                                WHEN 5 THEN CREDITO5
                                WHEN 6 THEN CREDITO6
                                WHEN 7 THEN CREDITO7
                                WHEN 8 THEN CREDITO8
                                WHEN 9 THEN CREDITO9
                                WHEN 10 THEN CREDITO10
                                WHEN 11 THEN CREDITO11
                                WHEN 12 THEN CREDITO12
                              END
                               -
                              CASE UN_MES 
                                WHEN 1 THEN DEBITO1
                                WHEN 2 THEN DEBITO2
                                WHEN 3 THEN DEBITO3
                                WHEN 4 THEN DEBITO4
                                WHEN 5 THEN DEBITO5
                                WHEN 6 THEN DEBITO6
                                WHEN 7 THEN DEBITO7
                                WHEN 8 THEN DEBITO8
                                WHEN 9 THEN DEBITO9
                                WHEN 10 THEN DEBITO10
                                WHEN 11 THEN DEBITO11
                                WHEN 12 THEN DEBITO12
                              END
                            END)) SALDOFINAL, ' ' DESCRIPCION
                FROM V_PLAN_CONTABLE
                WHERE COMPANIA    = UN_COMPANIA
                    AND ANO       = UN_ANO
                    AND (
                          (1 = CASE WHEN UN_EXCEL <> 0 THEN 1 ELSE 0 END
                                  AND LENGTH(ID)<= UN_DIGITOS)
                      OR  ( 1 = CASE WHEN UN_EXCEL = 0 THEN 1 ELSE 0 END
                                AND LENGTH(ID)= UN_DIGITOS)
                        )
                    AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )
                GROUP BY  ID,
                          NOMBRE,
                          NATURALEZA,
                          CORRIENTE    
                UNION ALL

                SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,6) ID,
                    PLAN_CONTABLE.NOMBRE,
                    PLAN_CONTABLE.NATURALEZA,
                    PLAN_CONTABLE.CORRIENTE,
                    (CASE WHEN 1  = UN_MES  THEN SALDO0  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN SALDO1  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN SALDO2  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN SALDO3  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN SALDO4  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN SALDO5  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN SALDO6  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN SALDO7  ELSE 0 END
                   + CASE WHEN 9  = UN_MES  THEN SALDO8  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN SALDO9  ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN SALDO10 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN SALDO11 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN SALDO12 ELSE 0 END
                   ) SALDOINICIAL,
                    (CASE WHEN 0  = UN_MES  THEN PLAN_CONTABLE.DEBITO0  ELSE 0 END
                   + CASE WHEN 1  = UN_MES  THEN PLAN_CONTABLE.DEBITO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN PLAN_CONTABLE.DEBITO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN PLAN_CONTABLE.DEBITO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN PLAN_CONTABLE.DEBITO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN PLAN_CONTABLE.DEBITO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN PLAN_CONTABLE.DEBITO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN PLAN_CONTABLE.DEBITO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN PLAN_CONTABLE.DEBITO8  ELSE 0 END
                   + CASE WHEN 9 =  UN_MES  THEN PLAN_CONTABLE.DEBITO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN PLAN_CONTABLE.DEBITO10 ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN PLAN_CONTABLE.DEBITO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN PLAN_CONTABLE.DEBITO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN PLAN_CONTABLE.DEBITO13 ELSE 0 END
                   ) DEBITO,
                    (CASE WHEN 0  = UN_MES  THEN PLAN_CONTABLE.CREDITO0  ELSE 0 END
                   + CASE WHEN 1  = UN_MES  THEN PLAN_CONTABLE.CREDITO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN PLAN_CONTABLE.CREDITO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN PLAN_CONTABLE.CREDITO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN PLAN_CONTABLE.CREDITO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN PLAN_CONTABLE.CREDITO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN PLAN_CONTABLE.CREDITO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN PLAN_CONTABLE.CREDITO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN PLAN_CONTABLE.CREDITO8  ELSE 0 END
                   + CASE WHEN 9 =  UN_MES  THEN PLAN_CONTABLE.CREDITO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN PLAN_CONTABLE.CREDITO10 ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN PLAN_CONTABLE.CREDITO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN PLAN_CONTABLE.CREDITO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN PLAN_CONTABLE.CREDITO13 ELSE 0 END
                   ) CREDITO,
                    (CASE WHEN 1  = UN_MES  THEN SALDO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN SALDO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN SALDO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN SALDO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN SALDO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN SALDO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN SALDO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN SALDO8  ELSE 0 END
                   + CASE WHEN 9  = UN_MES  THEN SALDO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN SALDO10  ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN SALDO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN SALDO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN SALDO13 ELSE 0 END 
                   ) SALDOFINAL,
					DETALLE_COMPROBANTE_CNT.DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN HEADERCIERRE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA  = HEADERCIERRE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO       = HEADERCIERRE.ANO
                 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = HEADERCIERRE.TIPOCOMPROBANTE
                 AND DETALLE_COMPROBANTE_CNT.CUENTA    = HEADERCIERRE.CONTRACUENTA                 
                INNER JOIN PLAN_CONTABLE  
                    ON PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                   AND PLAN_CONTABLE.ANO      = DETALLE_COMPROBANTE_CNT.ANO
                   AND PLAN_CONTABLE.CODIGO   = DETALLE_COMPROBANTE_CNT.CUENTA
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
                AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='CIE'
                AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,1)='3'
				AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )
                UNION ALL

               SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS) ID,
                    PLAN_CONTABLE.NOMBRE,
                    PLAN_CONTABLE.NATURALEZA,
                    PLAN_CONTABLE.CORRIENTE,
                                        (CASE WHEN 1  = UN_MES  THEN SALDO0  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN SALDO1  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN SALDO2  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN SALDO3  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN SALDO4  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN SALDO5  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN SALDO6  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN SALDO7  ELSE 0 END
                   + CASE WHEN 9  = UN_MES  THEN SALDO8  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN SALDO9  ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN SALDO10 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN SALDO11 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN SALDO12 ELSE 0 END
                   ) SALDOINICIAL,
                    (CASE WHEN 0  = UN_MES  THEN PLAN_CONTABLE.DEBITO0  ELSE 0 END
                   + CASE WHEN 1  = UN_MES  THEN PLAN_CONTABLE.DEBITO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN PLAN_CONTABLE.DEBITO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN PLAN_CONTABLE.DEBITO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN PLAN_CONTABLE.DEBITO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN PLAN_CONTABLE.DEBITO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN PLAN_CONTABLE.DEBITO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN PLAN_CONTABLE.DEBITO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN PLAN_CONTABLE.DEBITO8  ELSE 0 END
                   + CASE WHEN 9 =  UN_MES  THEN PLAN_CONTABLE.DEBITO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN PLAN_CONTABLE.DEBITO10 ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN PLAN_CONTABLE.DEBITO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN PLAN_CONTABLE.DEBITO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN PLAN_CONTABLE.DEBITO13 ELSE 0 END
                   ) CREDITO,
                    (CASE WHEN 0  = UN_MES  THEN PLAN_CONTABLE.CREDITO0  ELSE 0 END
                   + CASE WHEN 1  = UN_MES  THEN PLAN_CONTABLE.CREDITO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN PLAN_CONTABLE.CREDITO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN PLAN_CONTABLE.CREDITO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN PLAN_CONTABLE.CREDITO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN PLAN_CONTABLE.CREDITO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN PLAN_CONTABLE.CREDITO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN PLAN_CONTABLE.CREDITO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN PLAN_CONTABLE.CREDITO8  ELSE 0 END
                   + CASE WHEN 9 =  UN_MES  THEN PLAN_CONTABLE.CREDITO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN PLAN_CONTABLE.CREDITO10 ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN PLAN_CONTABLE.CREDITO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN PLAN_CONTABLE.CREDITO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN PLAN_CONTABLE.CREDITO13 ELSE 0 END
                   ) DEBITO,
                    (CASE WHEN 1  = UN_MES  THEN SALDO1  ELSE 0 END
                   + CASE WHEN 2  = UN_MES  THEN SALDO2  ELSE 0 END
                   + CASE WHEN 3  = UN_MES  THEN SALDO3  ELSE 0 END
                   + CASE WHEN 4  = UN_MES  THEN SALDO4  ELSE 0 END
                   + CASE WHEN 5  = UN_MES  THEN SALDO5  ELSE 0 END
                   + CASE WHEN 6  = UN_MES  THEN SALDO6  ELSE 0 END
                   + CASE WHEN 7  = UN_MES  THEN SALDO7  ELSE 0 END
                   + CASE WHEN 8  = UN_MES  THEN SALDO8  ELSE 0 END
                   + CASE WHEN 9  = UN_MES  THEN SALDO9  ELSE 0 END
                   + CASE WHEN 10 = UN_MES  THEN SALDO10  ELSE 0 END
                   + CASE WHEN 11 = UN_MES  THEN SALDO11 ELSE 0 END
                   + CASE WHEN 12 = UN_MES  THEN SALDO12 ELSE 0 END
                   + CASE WHEN 13 = UN_MES  THEN SALDO13 ELSE 0 END 
                   ) SALDOFINAL,
					DETALLE_COMPROBANTE_CNT.DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN HEADERCIERRE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA  = HEADERCIERRE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO       = HEADERCIERRE.ANO
                 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = HEADERCIERRE.TIPOCOMPROBANTE
                 AND DETALLE_COMPROBANTE_CNT.CUENTA    = HEADERCIERRE.CONTRACUENTA                 
                INNER JOIN PLAN_CONTABLE  
                    ON PLAN_CONTABLE.COMPANIA = HEADERCIERRE.COMPANIA
                   AND PLAN_CONTABLE.ANO      = HEADERCIERRE.ANO
                   AND PLAN_CONTABLE.CODIGO   = HEADERCIERRE.CUENTAACERRAR
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
                AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='CIE'
                AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,1) ='3'
				AND ( (UN_COVID <> 0 AND 1 = 0 )
						OR (UN_COVID = 0)
                        )

                UNION ALL

               SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS) ID,
                    PLAN_CONTABLE.NOMBRE,  PLAN_CONTABLE.NATURALEZA , PLAN_CONTABLE.CORRIENTE,
                    0  SALDOINICIAL,                    
                    SUM (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) DEBITO,
                    SUM (VALOR_DEBITO) CREDITO,
                    0  SALDOFINAL,
					MAX(DETALLE_COMPROBANTE_CNT.DESCRIPCION) DESCRIPCION
                FROM DETALLE_COMPROBANTE_CNT               
                INNER JOIN PLAN_CONTABLE  
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                   AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,UN_DIGITOS)   = PLAN_CONTABLE.CODIGO
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANO
				AND (UN_COVID <> 0 AND DETALLE_COMPROBANTE_CNT.INFORMECOVID <> 0 )
                GROUP BY SUBSTR(PLAN_CONTABLE.CODIGO,1,UN_DIGITOS),
                         PLAN_CONTABLE.NOMBRE,
                         PLAN_CONTABLE.NATURALEZA , 
                         PLAN_CONTABLE.CORRIENTE

                ORDER BY  ID ) D INNER JOIN 
                (

                SELECT 
                          SUBSTR(V_PLAN_CONTABLE.ID,1,UN_DIGITOS) ID,
                          NATURALEZA,
                          CORRIENTE ,
                      TO_NUMBER(SUM(CASE WHEN MOVIMIENTO IN(0)  THEN 0 ELSE 
                        CASE WHEN NATURALEZA IN('D')
                            THEN CASE UN_MES 
                        WHEN 0  THEN SALDO0
                        WHEN 1  THEN SALDO1
                        WHEN 2  THEN SALDO2
                        WHEN 3  THEN SALDO3
                        WHEN 4  THEN SALDO4
                        WHEN 5  THEN SALDO5
                        WHEN 6  THEN SALDO6
                        WHEN 7  THEN SALDO7
                        WHEN 8  THEN SALDO8
                        WHEN 9  THEN SALDO9
                        WHEN 10 THEN SALDO10
                        WHEN 11 THEN SALDO11
                        WHEN 12 THEN SALDO12
                        WHEN 13 THEN SALDO13
                              END +
                              CASE UN_MES 
                                WHEN 1 THEN DEBITO1
                                WHEN 2 THEN DEBITO2
                                WHEN 3 THEN DEBITO3
                                WHEN 4 THEN DEBITO4
                                WHEN 5 THEN DEBITO5
                                WHEN 6 THEN DEBITO6
                                WHEN 7 THEN DEBITO7
                                WHEN 8 THEN DEBITO8
                                WHEN 9 THEN DEBITO9
                                WHEN 10 THEN DEBITO10
                                WHEN 11 THEN DEBITO11
                                WHEN 12 THEN DEBITO12
                              END -
                              CASE UN_MES  
                                WHEN 1 THEN CREDITO1
                                WHEN 2 THEN CREDITO2
                                WHEN 3 THEN CREDITO3
                                WHEN 4 THEN CREDITO4
                                WHEN 5 THEN CREDITO5
                                WHEN 6 THEN CREDITO6
                                WHEN 7 THEN CREDITO7
                                WHEN 8 THEN CREDITO8
                                WHEN 9 THEN CREDITO9
                                WHEN 10 THEN CREDITO10
                                WHEN 11 THEN CREDITO11
                                WHEN 12 THEN CREDITO12
                              END
                          ELSE 
                     CASE UN_MES  
                        WHEN 0  THEN SALDO0
                        WHEN 1  THEN SALDO1
                        WHEN 2  THEN SALDO2
                        WHEN 3  THEN SALDO3
                        WHEN 4  THEN SALDO4
                        WHEN 5  THEN SALDO5
                        WHEN 6  THEN SALDO6
                        WHEN 7  THEN SALDO7
                        WHEN 8  THEN SALDO8
                        WHEN 9  THEN SALDO9
                        WHEN 10 THEN SALDO10
                        WHEN 11 THEN SALDO11
                        WHEN 12 THEN SALDO12
                        WHEN 13 THEN SALDO13 
                              END +
                              CASE UN_MES  
                                WHEN 1 THEN CREDITO1
                                WHEN 2 THEN CREDITO2
                                WHEN 3 THEN CREDITO3
                                WHEN 4 THEN CREDITO4
                                WHEN 5 THEN CREDITO5
                                WHEN 6 THEN CREDITO6
                                WHEN 7 THEN CREDITO7
                                WHEN 8 THEN CREDITO8
                                WHEN 9 THEN CREDITO9
                                WHEN 10 THEN CREDITO10
                                WHEN 11 THEN CREDITO11
                                WHEN 12 THEN CREDITO12
                              END
                               -
                              CASE UN_MES 
                                WHEN 1 THEN DEBITO1
                                WHEN 2 THEN DEBITO2
                                WHEN 3 THEN DEBITO3
                                WHEN 4 THEN DEBITO4
                                WHEN 5 THEN DEBITO5
                                WHEN 6 THEN DEBITO6
                                WHEN 7 THEN DEBITO7
                                WHEN 8 THEN DEBITO8
                                WHEN 9 THEN DEBITO9
                                WHEN 10 THEN DEBITO10
                                WHEN 11 THEN DEBITO11
                                WHEN 12 THEN DEBITO12
                          END
                            END END)) SALDOFINAL
                FROM V_PLAN_CONTABLE
                WHERE COMPANIA    = UN_COMPANIA
                    AND ANO       = UN_ANO
                GROUP BY SUBSTR(V_PLAN_CONTABLE.ID,1,UN_DIGITOS),
                          NATURALEZA,
                          CORRIENTE 
                ) SSCS
        ON  D.ID =  SSCS.ID
        GROUP BY  D.ID,D.NOMBRE,D.NATURALEZA,D.CORRIENTE,D.SALDOINICIAL,D.DEBITO,D.CREDITO,D.SALDOFINAL, D.DESCRIPCION
        )
          SELECT
                  SALDOCORRIENTE.ID
                , SALDOCORRIENTE.NOMBRE
                , SALDOCORRIENTE.NATURALEZA 
                , SALDOCORRIENTE.CORRIENTE 
                , SALDOCORRIENTE.SALDOINICIAL
                , SALDOCORRIENTE.DEBITO
                , SALDOCORRIENTE.CREDITO
                , SALDOCORRIENTE.SALDOFINAL
                , DATOSCORRIENTE.SALDO_CORRIENTE SALDO_FINAL_CORRIENTE 
                , DATOSCORRIENTE.SALDO_NO_CORRIENTE SALDO_FINAL_NO_CORRIENTE
                , SALDOCORRIENTE.DESCRIPCION
          FROM (
                          SELECT  SIN_MOVIMIENTO.ID
                                , SUM(MOVIMIENTO.SALDO_FINAL_CORRIENTE) AS SALDO_CORRIENTE
                                , SUM(MOVIMIENTO.SALDO_FINAL_NO_CORRIENTE) AS SALDO_NO_CORRIENTE
                          FROM (
                                SELECT  SCO.ID
                                      , SCO.SALDO_FINAL_CORRIENTE 
                                      , SCO.SALDO_FINAL_NO_CORRIENTE 
                                FROM SALDOCORRIENTE SCO 
                                WHERE  LENGTH(SCO.ID)= UN_DIGITOS 
                          )   MOVIMIENTO 
                          INNER JOIN (
                                SELECT  SCO.ID
                                       , SCO.SALDO_FINAL_CORRIENTE 
                                       , SCO.SALDO_FINAL_NO_CORRIENTE  
                                FROM SALDOCORRIENTE SCO 
                                WHERE  LENGTH(SCO.ID) <= UN_DIGITOS 
                          )  SIN_MOVIMIENTO
                          ON  SUBSTR(MOVIMIENTO.ID,0,LENGTH(SIN_MOVIMIENTO.ID))    =  SIN_MOVIMIENTO.ID
                          GROUP BY SIN_MOVIMIENTO.ID
          ) DATOSCORRIENTE
          INNER JOIN SALDOCORRIENTE 
             ON DATOSCORRIENTE.ID =  SALDOCORRIENTE.ID
          ORDER BY DATOSCORRIENTE.ID

    )  
    LOOP

      IF RS.SALDOINICIAL <> 0 OR RS.DEBITO <> 0 OR RS.CREDITO <> 0 OR RS.SALDOFINAL <> 0 THEN
        IF UN_EXCEL = 0 THEN 
          IF LENGTH(RS.ID) >0 AND LENGTH(RS.ID) < 2 THEN 
            MI_CONCEPTO := RS.ID;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 3 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1);
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 5 THEN 
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) ;
          ELSIF LENGTH(RS.ID)>0  AND LENGTH(RS.ID) < 7 THEN
            MI_CONCEPTO := SUBSTR(RS.ID,1,1) || '.' || SUBSTR(RS.ID,2,1) || '.' || SUBSTR(RS.ID,3,2) || '.' || SUBSTR(RS.ID,5,2);
          END IF; 
        ELSE 
          MI_CONCEPTO := RS.ID;
        END IF;  

      MI_SALDOFIN := RS.SALDOFINAL;

       IF UN_MILES <> 0 THEN
          MI_SALDOINICIAL := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDOINICIAL / 1000);
          MI_DEBITO       := PCK_SYSMAN_UTL.FC_ROUND(RS.DEBITO / 1000);
          MI_CREDITO      := PCK_SYSMAN_UTL.FC_ROUND(RS.CREDITO / 1000);
          MI_SALDOFIN     := PCK_SYSMAN_UTL.FC_ROUND(MI_SALDOFIN / 1000); 
       ELSIF UN_CENTAVOS <> 0 THEN
          MI_SALDOINICIAL := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDOINICIAL,0);
          MI_DEBITO       := PCK_SYSMAN_UTL.FC_ROUND(RS.DEBITO,0);
          MI_CREDITO      := PCK_SYSMAN_UTL.FC_ROUND(RS.CREDITO,0);
          MI_SALDOFIN     := PCK_SYSMAN_UTL.FC_ROUND(MI_SALDOFIN,0);
          MI_SALDOCOR     := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO_FINAL_CORRIENTE,0);
          MI_SALDONOCOR   := PCK_SYSMAN_UTL.FC_ROUND(RS.SALDO_FINAL_NO_CORRIENTE,0);

        ELSE
          MI_SALDOINICIAL := RS.SALDOINICIAL;
          MI_DEBITO := RS.DEBITO;
          MI_CREDITO := RS.CREDITO;
              --IF RS.CORRIENTE NOT IN(0) THEN 
          MI_SALDOCOR := RS.SALDO_FINAL_CORRIENTE;
       --   MI_SALDONOCOR := 0;
       -- ELSE 
       --    MI_SALDOCOR  := 0;
          MI_SALDONOCOR := RS.SALDO_FINAL_NO_CORRIENTE;
       -- END IF;

        END IF;


        MI_CADENA_UNO := TO_CLOB(MI_CADENA_UNO ||
                      CASE WHEN UN_EXCEL IN(0) THEN
                       'D'                  || CHR(9)
                      END
                      ||  MI_CONCEPTO         || CHR(9));
        IF(UN_COVID <> 0 ) THEN
            MI_CADENA_UNO := MI_CADENA_UNO ||  TO_CLOB(RS.DESCRIPCION)     || CHR(9);
        END IF;
        MI_CADENA_UNO := TO_CLOB(MI_CADENA_UNO || CASE WHEN UN_EXCEL NOT IN(0)THEN
                         SUBSTR(RS.NOMBRE, 1, 30)  || CHR(9)   
                         END                       
                      ||  TO_CLOB(MI_SALDOINICIAL)     || CHR(9)
                      ||  TO_CLOB(MI_DEBITO)           || CHR(9)
                      ||  TO_CLOB(MI_CREDITO)          || CHR(9)
                      ||  TO_CLOB(MI_SALDOFIN)         || CHR(9)
                      ||  TO_CLOB(MI_SALDOCOR)         || CHR(9)
                      ||  TO_CLOB(MI_SALDONOCOR)       || 
                       CHR(13)||CHR(10)                      
                      );

        MI_TOTALDEBITO :=  MI_TOTALDEBITO   +     MI_DEBITO;
        MI_TOTALCREDITO := MI_TOTALCREDITO  +     MI_CREDITO;
        IF RS.ID = '1' THEN 
          MI_NOMCUENTAUNO := RS.NOMBRE;
          MI_CUENTAUNOINI := MI_SALDOINICIAL;
          MI_CUENTAUNOFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '2' THEN 
          MI_NOMCUENTADOS := RS.NOMBRE;
          MI_CUENTADOSINI := MI_SALDOINICIAL;
          MI_CUENTADOSFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '3' THEN 
          MI_NOMCUENTATRES := RS.NOMBRE;
          MI_CUENTATRESINI := MI_SALDOINICIAL;
          MI_CUENTATRESFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '4' THEN 
          MI_NOMCUENTACUATRO := RS.NOMBRE;
          MI_CUENTACUATROINI := MI_SALDOINICIAL;
          MI_CUENTACUATROFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '5' THEN 
          MI_NOMCUENTACINCO := RS.NOMBRE;
          MI_CUENTACINCOINI := MI_SALDOINICIAL;
          MI_CUENTACINCOFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '6' THEN 
          MI_NOMCUENTASEIS := RS.NOMBRE;
          MI_CUENTASEISINI := MI_SALDOINICIAL;
          MI_CUENTASEISFIN := MI_SALDOFIN;
        END IF; 
        IF RS.ID = '7' THEN 
          MI_NOMCUENTASIETE := RS.NOMBRE;
          MI_CUENTASIETEINI := MI_SALDOINICIAL;
          MI_CUENTASIETEFIN := MI_SALDOFIN;
        END IF; 
      END IF;                    
      END LOOP SALDOSMOVIMIENTOS;
      MI_TOTDEB				      := TO_CHAR(MI_TOTALDEBITO,'FM999,999,999,999,999.999');
      MI_TOTCRE		          := TO_CHAR(MI_TOTALCREDITO,'FM999,999,999,999,999.999');
      MI_DIFERENCIA         := MI_TOTALDEBITO - MI_TOTALCREDITO;
      MI_TOTDIS 		        := TO_CHAR(MI_DIFERENCIA,'FM999,999,999,999,999.999');
      MI_TOTCUENTAUNOINI	  := TO_CHAR(MI_CUENTAUNOINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTADOSINI	  := TO_CHAR(MI_CUENTADOSINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTATRESINI	  := TO_CHAR(MI_CUENTATRESINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTACUATROINI	:= TO_CHAR(MI_CUENTACUATROINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTACINCOINI	:= TO_CHAR(MI_CUENTACINCOINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTASEISINI	  := TO_CHAR(MI_CUENTASEISINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTASIETEINI	:= TO_CHAR(MI_CUENTASIETEINI,'FM999,999,999,999,999.999');
      MI_TOTCUENTAUNOFIN	  := TO_CHAR(MI_CUENTAUNOFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTADOSFIN	  := TO_CHAR(MI_CUENTADOSFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTATRESFIN	  := TO_CHAR(MI_CUENTATRESFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTACUATROFIN	:= TO_CHAR(MI_CUENTACUATROFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTACINCOFIN	:= TO_CHAR(MI_CUENTACINCOFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTASEISFIN	  := TO_CHAR(MI_CUENTASEISFIN,'FM999,999,999,999,999.999');
      MI_TOTCUENTASIETEFIN	:= TO_CHAR(MI_CUENTASIETEFIN,'FM999,999,999,999,999.999');
      MI_DIFERENCIAINI      := MI_CUENTAUNOINI - ((MI_CUENTADOSINI + MI_CUENTATRESINI)+(MI_CUENTACUATROINI-MI_CUENTACINCOINI-MI_CUENTASEISINI-MI_CUENTASIETEINI));
      MI_DIFERENCIAINICIAL	:= TO_CHAR(MI_DIFERENCIAINI,'FM999,999,999,999,999.999');
      MI_DIFERENCIAFIN      := MI_CUENTAUNOFIN - ((MI_CUENTADOSFIN + MI_CUENTATRESFIN)+(MI_CUENTACUATROFIN-MI_CUENTACINCOFIN-MI_CUENTASEISFIN-MI_CUENTASIETEFIN));
      MI_DIFERENCIAFINAL	:= TO_CHAR(MI_DIFERENCIAFIN,'FM999,999,999,999,999.999');
      MI_CADENA_DOS   := TO_CLOB(MI_CADENA_DOS  
                          || 'Total Movimiento Debito: '    || CHR(9)
                          || TO_CLOB(MI_TOTDEB)             || CHR(13)||CHR(10) 
                          || 'Total Movimiento Credito: '   || CHR(9)
                          || TO_CLOB(MI_TOTCRE)             || CHR(13)||CHR(10) 
                          || 'Diferencia: '                 || CHR(9)
                          || TO_CLOB(MI_TOTDIS) || CHR(13)||CHR(10) 
                          || 'Valores Cuentas Mayores'    || CHR(13)||CHR(10)
                          || '1 - ' || RPAD(MI_NOMCUENTAUNO,10)    || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTAUNOINI,20)    || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTAUNOFIN,20)    || CHR(13)||CHR(10)
                          || '2 - ' || RPAD(MI_NOMCUENTADOS,10)    || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTADOSINI,20)    || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTADOSFIN,20)    || CHR(13)||CHR(10)
                          || '3 - ' || RPAD(MI_NOMCUENTATRES,10)   || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTATRESINI,20)   || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTATRESFIN,20)   || CHR(13)||CHR(10)
                          || '4 - ' || RPAD(MI_NOMCUENTACUATRO,10) || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTACUATROINI,20) || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTACUATROFIN,20) || CHR(13)||CHR(10)
                          || '5 - ' || RPAD(MI_NOMCUENTACINCO,10)  || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTACINCOINI,20)  || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTACINCOFIN,20)  || CHR(13)||CHR(10)
                          || '6 - ' || RPAD(MI_NOMCUENTASEIS,10)   || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTASEISINI,20)   || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTASEISFIN,20)   || CHR(13)||CHR(10)
                          || '7 - ' || RPAD(MI_NOMCUENTASIETE,10)  || CHR(9) ||  'Saldo Inicial: '|| RPAD(MI_TOTCUENTASIETEINI,20)  || CHR(9) || 'Saldo Final: ' || RPAD(MI_TOTCUENTASIETEFIN,20)  || CHR(13)||CHR(10)
                          || RPAD('Diferencias',30) || RPAD(MI_DIFERENCIAINICIAL,33)|| CHR(9) || MI_DIFERENCIAFINAL) ;
      MI_RTA := MI_CADENA_UNO || '&' || MI_CADENA_DOS; 
    RETURN MI_RTA;
END  FC_GENERASALDOMOVMES; 
END PCK_CHIPFUT;