create or replace PACKAGE BODY                     "PCK_CONTABILIDAD" AS
/**@package:  Contabilidad **/

PROCEDURE PR_ACTCONTA 
 /*
    NAME              : ACTCONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 16/01/2015 
    TIME              : 3:45 PM     
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON/JOSE PASCUAL GOMEZ BLANCO/JOSE PASCUAL GOMEZ BLANCO/ADRIANA CACERES
    DATE MODIFIED     : 23/01/2015 / 26/01/2015 / 27/01/2015 / 17/11/2016
    DESCRIPTION       : VERIFICA SI LA CUENTA CONTABLE EXISTE Y QUE AUXILIARES REQUIERE PARA VALIDARLOS Y ACTUALIZAR LOS SALDOS
    MODIFICATIONS     : Incluir manejo de errores / Modificacion manejo de excepciones
    @NAME:  actualizarSaldosContables
    @METHOD:  POST

  */
  ( 
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA          IN DATE,
    UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO , 
    UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL ,
    UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR ,
    UN_CENTROCOSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO , 
    UN_REFERENCIA     IN PCK_SUBTIPOS.TI_REFERENCIA , 
    UN_FUENTERECURSO  IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS , 
    UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0, 
    UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_DEBITO_ANT     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0, 
    UN_CREDITO_ANT    IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_IND_CIE        IN PCK_SUBTIPOS.TI_LOGICO :=0 -- SI EL COMPROBANTE ES DE CIERRE ; 
  )  
  AS
  MI_ANIO                     PCK_SUBTIPOS.TI_ANIO ;
  MI_MES                      PCK_SUBTIPOS.TI_MES;
  MI_DIA                      PCK_SUBTIPOS.TI_DIA;
  MI_CUENTA                   TYP_CUENTA_AUX;
BEGIN

  IF NVL(UN_DEBITO,0)<>NVL(UN_DEBITO_ANT,0) OR NVL(UN_CREDITO,0)<>NVL(UN_CREDITO_ANT,0) THEN
    MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
    MI_MES  := EXTRACT (MONTH FROM UN_FECHA);
    MI_DIA  := EXTRACT (DAY FROM UN_FECHA);

    IF MI_MES = 12 AND MI_DIA = 31 AND UN_IND_CIE <> 0 THEN
      MI_MES := 13;   
    END IF;
    MI_CUENTA := FC_VERIFICAR_INDICADORES_CON(UN_COMPANIA   => UN_COMPANIA,  
                                              UN_ANIO       => MI_ANIO, 
                                              UN_CODIGO     => UN_CODIGO, 
                                              UN_CENTRO     => UN_CENTROCOSTO, 
                                              UN_TERCERO    => UN_TERCERO, 
                                              UN_SUCURSAL   => UN_SUCURSAL, 
                                              UN_AUXILIAR   => UN_AUXILIAR, 
                                              UN_REFERENCIA => UN_REFERENCIA, 
                                              UN_FUENTERECURSO=>UN_FUENTERECURSO);   
    PR_ACTCONTA0    (UN_COMPANIA       => MI_CUENTA.MI_COMPANIA,  
                     UN_ANIO           => MI_CUENTA.MI_ANIO, 
                     UN_CODIGO         => MI_CUENTA.MI_CODIGO, 
                     UN_MES            => MI_MES, 
                     UN_DEBITO         => NVL(UN_DEBITO,0),  
                     UN_CREDITO        => NVL(UN_CREDITO,0), 
                     UN_DEBITO_ANT     => NVL(UN_DEBITO_ANT,0), 
                     UN_CREDITO_ANT    => NVL(UN_CREDITO_ANT,0),
                     UN_NATURALEZA     => MI_CUENTA.MI_NATURALEZA); 
    PR_ACTCONTA0AUX (UN_COMPANIA       => MI_CUENTA.MI_COMPANIA,  
                     UN_ANIO           => MI_CUENTA.MI_ANIO, 
                     UN_CODIGO         => MI_CUENTA.MI_CODIGO,  
                     UN_CENTRO         => MI_CUENTA.MI_CENTROCOSTO, 
                     UN_TERCERO        => MI_CUENTA.MI_TERCERO, 
                     UN_SUCURSAL       => MI_CUENTA.MI_SUCURSAL, 
                     UN_AUXILIAR       => MI_CUENTA.MI_AUXILIAR, 
                     UN_REFERENCIA     => MI_CUENTA.MI_REFERENCIA, 
                     UN_FUENTERECURSO  => MI_CUENTA.MI_FUENTERECURSO, 
                     UN_MES            => MI_MES, 
                     UN_NATURALEZA     => MI_CUENTA.MI_NATURALEZA, 
                     UN_IND_DEPURADOS  => 1,  
                     UN_DEBITO         => NVL(UN_DEBITO,0),  
                     UN_CREDITO        => NVL(UN_CREDITO,0), 
                     UN_DEBITO_ANT     => NVL(UN_DEBITO_ANT,0), 
                     UN_CREDITO_ANT    => NVL(UN_CREDITO_ANT,0)); 
  END IF;


END PR_ACTCONTA;

PROCEDURE PR_ACTCONTA0 
  /*
    NAME              : ACTCONTA0
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 19/01/2015
    TIME              : 8:45 AM 
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON/JOSE PASCUAL GOMEZ BLANCO / ADRIANA CACERES 
    DATE MODIFIED     : 23/01/2015 / 27/01/2015 / 17/11/2016
    DESCRIPTION       : ACTUALIZA LOS VALORES DE PLAN_CONTABLE
    MODIFICATIONS     : Incluir manejo de errores / Modificacion manejo de excepciones
    @NAME:  mayorizarSaldosContables
    @METHOD:  PUT
  */
  ( 
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO, 
    UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOCONTA,   
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE:=0,
    UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE:=0,
    UN_DEBITO_ANT     IN PCK_SUBTIPOS.TI_DOBLE:=0, 
    UN_CREDITO_ANT    IN PCK_SUBTIPOS.TI_DOBLE:=0,
    UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA
  )
  AS
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_NETO          PCK_SUBTIPOS.TI_DOBLE;
    MI_NETO_ANT      PCK_SUBTIPOS.TI_DOBLE;  
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  BEGIN    
    IF UN_DEBITO<>UN_DEBITO_ANT OR UN_CREDITO<>UN_CREDITO_ANT THEN
      IF UN_NATURALEZA='D' THEN
         MI_NETO:=UN_DEBITO-UN_CREDITO;
         MI_NETO_ANT:=UN_DEBITO_ANT-UN_CREDITO_ANT;
       ELSE         
         MI_NETO:=UN_CREDITO-UN_DEBITO;
         MI_NETO_ANT:=UN_CREDITO_ANT-UN_DEBITO_ANT;
      END IF;  
    BEGIN  
      MI_CAMPOS :='  DEBITO'  || UN_MES ||  '= DEBITO'  || UN_MES || ' + ' || UN_DEBITO  || ' - ' || UN_DEBITO_ANT ||
                  ', CREDITO' || UN_MES ||  '=CREDITO'  || UN_MES || ' + ' || UN_CREDITO || ' - ' || UN_CREDITO_ANT ||
                  ', NETO'    || UN_MES ||  '=NETO'     || UN_MES || ' + CASE WHEN NATURALEZA= ''' || UN_NATURALEZA || ''' THEN ' || MI_NETO || ' ELSE -1 *' || MI_NETO || ' END - CASE WHEN NATURALEZA= ''' || UN_NATURALEZA || ''' THEN ' || MI_NETO_ANT || ' ELSE -1 *' || MI_NETO_ANT || ' END '  ;                
      <<SALDOMES>>
      FOR MI_I IN UN_MES .. 13 LOOP
            MI_CAMPOS:= MI_CAMPOS ||
                  ', SALDO' || MI_I || '=SALDO' || MI_I || ' + CASE WHEN NATURALEZA= ''' || UN_NATURALEZA || ''' THEN ' || MI_NETO || ' ELSE -1 *' || MI_NETO || ' END - CASE WHEN NATURALEZA= ''' || UN_NATURALEZA || ''' THEN ' || MI_NETO_ANT || ' ELSE -1 *' || MI_NETO_ANT || ' END '  ;    
      END LOOP SALDOMES;
     --ACTUALIZAR PLAN_CONTABLE 
     MI_CONDICION := ' COMPANIA='''           || UN_COMPANIA || '''' ||
                      '   AND ANO='           || UN_ANIO ||                
                      '   AND CODIGO  BETWEEN ''' || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || UN_CODIGO || '''' ||
                      '   AND CODIGO = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO) )';        

    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE', 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);  
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
       RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 

  END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_NOACT,
        UN_TABLAERROR =>'PLAN_CONTABLE'
      );  
END PR_ACTCONTA0;

PROCEDURE PR_ACTCONTA0AUX
 /*
    NAME              : ACTCONTAAUX
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 19/01/2015
    TIME              : 2:00 PM 
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON /JOSE PASCUAL GOMEZ BLANCO / ADRIANA CACERES
    DATE MODIFIED     : 23/01/2015 / 27/01/2015 / 17/11/2016
    DESCRIPTION       : ACTUALIZA LOS VALORES DE SALDO_AUX_CONTABLE
    MODIFICATIONS     : Incluir manejo de errores / Modificacion manejo de excepciones  
    @NAME:  actualizarAuxiliaresContables
    @METHOD:  POST
  */
  ( 
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO, 
    UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOCONTA, 
    UN_CENTRO         IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
    UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO, 
    UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR,
    UN_REFERENCIA     IN PCK_SUBTIPOS.TI_REFERENCIA, 
    UN_FUENTERECURSO  IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS, 
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA,
    UN_IND_DEPURADOS  IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,
    UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_DEBITO_ANT     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0, 
    UN_CREDITO_ANT    IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
  )
   AS
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_NETO          PCK_SUBTIPOS.TI_DOBLE;
    MI_NETO_ANT      PCK_SUBTIPOS.TI_DOBLE;  
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_CUENTA        TYP_CUENTA_AUX;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_CREA_CUENTA   PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
  BEGIN          
    --si los indicadores no vienen depurados los depura
    IF UN_IND_DEPURADOS=0 THEN
       MI_CUENTA := FC_VERIFICAR_INDICADORES_CON(UN_COMPANIA   => UN_COMPANIA,  
                                                  UN_ANIO       => UN_ANIO, 
                                                  UN_CODIGO     => UN_CODIGO, 
                                                  UN_CENTRO     => UN_CENTRO, 
                                                  UN_TERCERO    => UN_TERCERO, 
                                                  UN_SUCURSAL   => UN_SUCURSAL, 
                                                  UN_AUXILIAR   => UN_AUXILIAR, 
                                                  UN_REFERENCIA => UN_REFERENCIA, 
                                                  UN_FUENTERECURSO=>UN_FUENTERECURSO); 
    ELSE
      MI_CUENTA.MI_COMPANIA     :=UN_COMPANIA;
      MI_CUENTA.MI_ANIO         :=UN_ANIO;
      MI_CUENTA.MI_CODIGO       :=UN_CODIGO;
      MI_CUENTA.MI_NATURALEZA   :=UN_NATURALEZA;
      MI_CUENTA.MI_CENTROCOSTO  :=UN_CENTRO;
      MI_CUENTA.MI_TERCERO      :=UN_TERCERO;
      MI_CUENTA.MI_SUCURSAL     :=UN_SUCURSAL;
      MI_CUENTA.MI_AUXILIAR     :=UN_AUXILIAR;
      MI_CUENTA.MI_REFERENCIA   :=UN_REFERENCIA;
      MI_CUENTA.MI_FUENTERECURSO:=UN_FUENTERECURSO;
    END IF;
    --BEGIN
      MI_CREA_CUENTA:=1;
      --SE DEBE VERIFICAR EN LA TABLA SALDO_AUX_CONTABLE SI LA CUENTA Y SUS AUXILIARES YA EXISTEN PARA ACTUALIZARLA O CREARLA
       SELECT COUNT(COMPANIA)
       INTO MI_CREA_CUENTA
       FROM  SALDO_AUX_CONTABLE 
        WHERE COMPANIA      = MI_CUENTA.MI_COMPANIA 
          AND ANO           = MI_CUENTA.MI_ANIO 
          AND CODIGO        = MI_CUENTA.MI_CODIGO
          AND CENTRO_COSTO  = MI_CUENTA.MI_CENTROCOSTO
          AND TERCERO       = MI_CUENTA.MI_TERCERO
          AND SUCURSAL      = MI_CUENTA.MI_SUCURSAL
          AND AUXILIAR      = MI_CUENTA.MI_AUXILIAR
          AND REFERENCIA    = MI_CUENTA.MI_REFERENCIA
          AND FUENTE_RECURSO= MI_CUENTA.MI_FUENTERECURSO;


    IF MI_CREA_CUENTA=0 THEN
       BEGIN
         MI_CAMPOS :='COMPANIA' || ', ANO' || ', CODIGO' || ', CENTRO_COSTO' || ', TERCERO' || ', SUCURSAL' || ', AUXILIAR' || ', REFERENCIA' || ', FUENTE_RECURSO' || ', NATURALEZA';
         MI_VALORES :='''' || MI_CUENTA.MI_COMPANIA || ''',' || MI_CUENTA.MI_ANIO || ',''' || MI_CUENTA.MI_CODIGO || ''',' || '''' || MI_CUENTA.MI_CENTROCOSTO || ''','  ||  '''' || MI_CUENTA.MI_TERCERO || ''',' || '''' || MI_CUENTA.MI_SUCURSAL || ''',' || '''' || MI_CUENTA.MI_AUXILIAR || ''',' || '''' || MI_CUENTA.MI_REFERENCIA || ''',' || '''' || MI_CUENTA.MI_FUENTERECURSO || ''',' || '''' || MI_CUENTA.MI_NATURALEZA || ''''; 
         BEGIN
         PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                             UN_ACCION    => 'I', 
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_VALORES   => MI_VALORES);

       EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
         END;

         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR := 'SALDO_AUX_CONTABLE';
                MI_ERROR(2).CLAVE := 'CAMPOS';
                MI_ERROR(2).VALOR :=  MI_CAMPOS;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'SALDO_AUX_CONTABLE',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                 UN_REEMPLAZOS => MI_ERROR);
     END;
    END IF;
    --no trae debitos o creditos no se actualizan saldos
    IF (UN_DEBITO<>0 OR UN_CREDITO<>0 OR UN_CREDITO_ANT<>0 OR UN_DEBITO_ANT<>0) THEN
      IF MI_CUENTA.MI_NATURALEZA='D' THEN
        MI_NETO:=UN_DEBITO-UN_CREDITO;
        MI_NETO_ANT:=UN_DEBITO_ANT-UN_CREDITO_ANT;
      ELSE         
        MI_NETO:=UN_CREDITO-UN_DEBITO;
        MI_NETO_ANT:=UN_CREDITO_ANT-UN_DEBITO_ANT;
      END IF;   

      BEGIN
      MI_CAMPOS :='DEBITO'  || UN_MES || '= DEBITO' || UN_MES || ' + ' || UN_DEBITO  || ' - ' || UN_DEBITO_ANT ||
                ', CREDITO' || UN_MES || '= CREDITO' || UN_MES || ' + ' || UN_CREDITO || ' - ' || UN_CREDITO_ANT ||
                ', NETO'    || UN_MES || '= NETO'    || UN_MES || ' + CASE WHEN NATURALEZA= ''' || MI_CUENTA.MI_NATURALEZA || ''' THEN ' || MI_NETO || ' ELSE -1 *' || MI_NETO || ' END - CASE WHEN NATURALEZA= ''' || MI_CUENTA.MI_NATURALEZA || ''' THEN ' || MI_NETO_ANT || ' ELSE -1 *' || MI_NETO_ANT || ' END '  ;                
      <<SALDOMES>>
      FOR MI_I IN UN_MES .. 13 LOOP
            MI_CAMPOS:= MI_CAMPOS ||
                       ', SALDO' || MI_I || '=SALDO' || MI_I || ' + CASE WHEN NATURALEZA= ''' || MI_CUENTA.MI_NATURALEZA || ''' THEN ' || MI_NETO || ' ELSE -1 *' || MI_NETO || ' END - CASE WHEN NATURALEZA= ''' || MI_CUENTA.MI_NATURALEZA || ''' THEN ' || MI_NETO_ANT || ' ELSE -1 *' || MI_NETO_ANT || ' END '  ;    
      END LOOP SALDOMES;       
     --ACTUALIZAR SALDO_AUX_CONTABLE 
     BEGIN
          MI_CONDICION := ' COMPANIA='''               || MI_CUENTA.MI_COMPANIA || '''' ||
                          '   AND ANO='                || MI_CUENTA.MI_ANIO ||                
                          '   AND CODIGO = '''         || MI_CUENTA.MI_CODIGO || '''' ||
                          '   AND TERCERO = '''        || MI_CUENTA.MI_TERCERO || '''' ||
                          '   AND SUCURSAL = '''       || MI_CUENTA.MI_SUCURSAL || '''' ||
                          '   AND CENTRO_COSTO = '''   || MI_CUENTA.MI_CENTROCOSTO || '''' ||
                          '   AND AUXILIAR = '''       || MI_CUENTA.MI_AUXILIAR || '''' ||
                          '   AND REFERENCIA = '''     || MI_CUENTA.MI_REFERENCIA || '''' ||
                          '   AND FUENTE_RECURSO = ''' || MI_CUENTA.MI_FUENTERECURSO || '''';     
           PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);

                     EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                       END;
          EXCEPTION
               WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                          MI_ERROR(1).CLAVE := 'TABLA';
                          MI_ERROR(1).VALOR := 'SALDO_AUX_CONTABLE';
                          MI_ERROR(2).CLAVE := 'CAMPOS';
                          MI_ERROR(2).VALOR :=  MI_CONDICION;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_TABLAERROR => 'SALDO_AUX_CONTABLE',
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                                     UN_REEMPLAZOS => MI_ERROR);
      END;
  END IF;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_SALDO,
        UN_TABLAERROR =>'SALDO_AUX_CONTABLE'
      ); 

END PR_ACTCONTA0AUX;


PROCEDURE PR_MAYORIZARCUENTAS
/*
    NAME              : CUADRECONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : HENRY PUERTO
    DATE MIGRADOR     : 11/09/2009
    TIME              : 05:00 PM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO/ SANDRA MILENA DAZA LEGUIZAMON/JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 21/01/2015 / 23/01/2015/ 26/05/2017
    TIME              : 9:41 AM
    DESCRIPTION       : Refleja los saldos desde las auxiliares a las cuentas hijas del plan contable mayoriza
    MODIFICATIONS     : / Incluir manejo de errores
                        /Se ajusta para que no tome los saldos desde los detalles sino que se realice desde las auxiliares           
    @NAME:  mayorizarContable
    @METHOD:  PUT
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES_INI    IN PCK_SUBTIPOS.TI_MES DEFAULT 0,
    UN_MES_FIN    IN PCK_SUBTIPOS.TI_MES DEFAULT 13

  ) 
  AS
  MI_MESINI    PCK_SUBTIPOS.TI_MES;
  MI_MESFIN    PCK_SUBTIPOS.TI_MES;
  MI_STRSQL    PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQLUP  PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQLSE  PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQL1   PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    MI_MESINI := UN_MES_INI;
    MI_MESFIN := UN_MES_FIN;

    MI_STRSQL1:='';
    MI_STRSQLUP:='';
    MI_STRSQLSE:='';
    <<CEROMES>>
    FOR MI_I IN MI_MESINI .. MI_MESFIN LOOP
      IF MI_I>MI_MESINI THEN
        MI_STRSQL:=MI_STRSQL ||',';
        MI_STRSQLUP:=MI_STRSQLUP||',';
        MI_STRSQLSE:=MI_STRSQLSE||',';
      END IF;

      MI_STRSQL:= MI_STRSQL ||' DEBITO'  || MI_I ||'=0, 
                                CREDITO' || MI_I ||'=0, 
                                NETO'    || MI_I ||'=0,
                                SALDO'   || MI_I ||'=0 ';
      MI_STRSQLUP:= MI_STRSQLUP ||' DEBITO'  || MI_I ||', 
                                    CREDITO' || MI_I ||', 
                                    NETO'    || MI_I ||',
                                    SALDO'   || MI_I ;
      MI_STRSQLSE:= MI_STRSQLSE ||' SUM(DEBITO'  || MI_I ||'), 
                                    SUM(CREDITO' || MI_I ||'), 
                                    SUM(NETO'    || MI_I ||'),
                                    SUM(SALDO'   || MI_I ||')';
    END LOOP CEROMES;
    MI_CONDICION:=' COMPANIA = '||CHR(39) || UN_COMPANIA || CHR(39) ||
                  ' AND ANO='   ||UN_ANO  || CHR(13)||CHR(10); --||
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     =>'PLAN_CONTABLE',
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_STRSQL, 
                                            UN_CONDICION => MI_CONDICION);  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
		  RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
	  END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
				  MI_MSGERROR(1).CLAVE := 'ANIO';
				  MI_MSGERROR(1).VALOR := UN_ANO;
					PCK_ERR_MSG.RAISE_WITH_MSG(
					  UN_EXC_COD    => SQLCODE,
					  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MAYORIZANDOENCERO,
					  UN_REEMPLAZOS => MI_MSGERROR
		 ); 
    END;

    MI_STRSQL:='(' || MI_STRSQLUP || ')=(SELECT' || MI_STRSQLSE ||'                                    
                                         FROM SALDO_AUX_CONTABLE S
                                         WHERE PLAN_CONTABLE.COMPANIA =  S.COMPANIA
                                           AND PLAN_CONTABLE.ANO      =  S.ANO
                                           AND PLAN_CONTABLE.CODIGO   =  SUBSTR(S.CODIGO,1, LENGTH(PLAN_CONTABLE.CODIGO))
                                         )';
    MI_CONDICION:=' COMPANIA = '||CHR(39) || UN_COMPANIA || CHR(39) ||
                  ' AND ANO='   ||UN_ANO  || CHR(13)||CHR(10) ||
                  '  AND (COMPANIA, ANO, CODIGO) IN(SELECT COMPANIA, ANO, SUBSTR(F.CODIGO,1, LENGTH(PLAN_CONTABLE.CODIGO)) 
                                                    FROM SALDO_AUX_CONTABLE F
                                                    WHERE PLAN_CONTABLE.COMPANIA  =  F.COMPANIA
                                                      AND PLAN_CONTABLE.ANO       =  F.ANO
                                                      AND PLAN_CONTABLE.CODIGO    =  SUBSTR(F.CODIGO,1, LENGTH(PLAN_CONTABLE.CODIGO)))'; --||                                     
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     =>'PLAN_CONTABLE',
                                              UN_ACCION    => 'M', 
                                              UN_CAMPOS    => MI_STRSQL, 
                                              UN_CONDICION => MI_CONDICION); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 	  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
				  MI_MSGERROR(1).CLAVE := 'ANIO';
				  MI_MSGERROR(1).VALOR := UN_ANO;
					PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MAYORIZANDO,
              UN_REEMPLAZOS => MI_MSGERROR
       ); 
    END;
END PR_MAYORIZARCUENTAS;

PROCEDURE PR_CUADRECONTA_AUX
/*
    NAME              : CUADRECONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 21/01/2015
    TIME              : 11:50 AM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO / SANDRA MILENA DAZA LEGUIZAMON
    DATE MODIFIED     : 21/01/2015 / 23/01/2015
    TIME              : 11:50 AM
    DESCRIPTION       : Arma la sentencia SQL según los parametros recibidos
    MODIFICATIONS     : / Incluir rutinas de errores
    @NAME:  corregirSaldosAuxiliaresContables
    @METHOD:  POST
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES_INI    IN PCK_SUBTIPOS.TI_MES,
    UN_MES_FIN    IN PCK_SUBTIPOS.TI_MES,
    UN_CODIGO_INI IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT '0',
    UN_CODIGO_FIN IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT PCK_DATOS.FC_CONS_MAX_ID --'99999999999999999999999999999999'
  ) 
  AS
  MI_MESINI       PCK_SUBTIPOS.TI_MES;
  MI_MESFIN       PCK_SUBTIPOS.TI_MES;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQL1      PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS_AUX   PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
  IF UN_MES_INI=0 THEN
    MI_MESINI := 1;
  ELSE 
    MI_MESINI := UN_MES_INI;
  END IF;
  IF UN_MES_FIN=0 THEN
    MI_MESFIN := 1;
  ELSE 
    MI_MESFIN := UN_MES_FIN;
  END IF;

  BEGIN
  -- se reestructura los id que se tienen en la detalle pero no en la plan
    
    <<EST_CUENTAS>>
     FOR RS IN 
     (
        SELECT DISTINCT 
                    D.COMPANIA,
                    D.ANO,
                    D.TIPO_CPTE,
                    D.COMPROBANTE,
                    D.CONSECUTIVO,
                    D.CUENTA,
                    D.ID IDDETALLE,
                    PCK_SYSMAN_UTL.FC_CODIGO_CNT(D.COMPANIA,D.ANO,D.CUENTA,D.CENTRO_COSTO,D.TERCERO,D.SUCURSAL,D.AUXILIAR,D.REFERENCIA,D.FUENTE_RECURSO) ID_CORRECTO,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.AUXILIAR,4) AUXILIARI,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.TERCERO,2) TERCEROI,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.SUCURSAL,3) SUCURSALI,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.REFERENCIA,5) REFERENCIAI,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.CENTRO_COSTO,1) CENTRO_COSTOI,
                    PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(D.COMPANIA,D.ANO,D.CUENTA,D.FUENTE_RECURSO,6) FUENTE_RECURSOI
        FROM DETALLE_COMPROBANTE_CNT D
                    LEFT JOIN V_PLAN_CONTABLE V ON V.CODIGO = D.CUENTA 
                        AND V.COMPANIA  = D.COMPANIA  
                        AND V.ANO       = D.ANO 
                        AND V.ID        = D.ID
         WHERE D.COMPANIA =  UN_COMPANIA   
                        AND D.ANO =  UN_ANIO 
                        AND TO_NUMBER(TO_CHAR(D.FECHA,'MM')) BETWEEN  UN_MES_INI  AND  UN_MES_FIN 
                        AND V.ID IS NULL
         ORDER BY D.CUENTA)
      LOOP

        IF RS.IDDETALLE <> RS.ID_CORRECTO THEN

         MI_CAMPOS := 'ID  = ''' || RS.ID_CORRECTO|| '''
                      , AUXILIARI        = ''' || RS.AUXILIARI|| '''
                      , TERCEROI         = ''' || RS.TERCEROI|| '''
                      , SUCURSALI        = ''' || RS.SUCURSALI|| '''
                      , REFERENCIAI      = ''' || RS.REFERENCIAI|| '''
                      , CENTRO_COSTOI    = ''' || RS.CENTRO_COSTOI|| '''
                      , FUENTE_RECURSOI  = ''' || RS.FUENTE_RECURSOI|| '''';
                      
         MI_CONDICION := 'COMPANIA        = ''' || RS.COMPANIA    ||''' 
                         AND ANO          = '   || RS.ANO         ||'
                         AND TIPO_CPTE    = ''' || RS.TIPO_CPTE   ||'''
                         AND COMPROBANTE  = '   || RS.COMPROBANTE ||'
                         AND CONSECUTIVO  = '   || RS.CONSECUTIVO; 
         BEGIN                 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);
    
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        END IF;
        END LOOP EST_CUENTAS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'ANIO';
          MI_MSGERROR(1).VALOR := UN_ANIO;
          MI_MSGERROR(2).CLAVE := 'INICIAL';
          MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
          MI_MSGERROR(3).CLAVE := 'FINAL';
          MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACTUALIZA,
            UN_REEMPLAZOS => MI_MSGERROR
     );
        END;
        
    BEGIN
    --EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
    MI_STRSQL1:='(';
    <<CEROMES>>
    FOR MI_I IN MI_MESINI .. MI_MESFIN LOOP
      IF MI_I>MI_MESINI THEN
        MI_STRSQL:=MI_STRSQL ||',';
        MI_STRSQL1:=  MI_STRSQL1 || ' OR ';
      END IF;
      MI_STRSQL:= MI_STRSQL ||' DEBITO'  || MI_I ||'=0,' || 
                              ' CREDITO' || MI_I ||'=0,' || 
                              ' NETO'    || MI_I ||'=0,' ||
                              ' SALDO'   || MI_I ||'=0 ';

      MI_STRSQL1:= MI_STRSQL1 || ' DEBITO'  || MI_I || ' NOT IN (0) OR ' ||
                                 ' CREDITO' || MI_I || ' NOT IN (0) OR ' || 
                                 ' NETO'    || MI_I || ' NOT IN (0) OR ' ||
                                 ' SALDO'   || MI_I || ' NOT IN (0) ';
    END LOOP CEROMES;
    MI_STRSQL1:=MI_STRSQL1 || ') ';
    MI_CONDICION:=' COMPANIA = '''         || UN_COMPANIA   || '''' ||
                  ' AND ANO='              || UN_ANIO       || CHR(13) || CHR(10) ||
                  ' AND CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' 
                  || ' AND '   || MI_STRSQL1 ;
    BEGIN
     PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_STRSQL,
                                         UN_CONDICION => MI_CONDICION);                   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;  
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'ANIO';
        MI_MSGERROR(1).VALOR := UN_ANIO;
        MI_MSGERROR(2).CLAVE := 'INICIAL';
        MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
        MI_MSGERROR(3).CLAVE := 'FINAL';
        MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACERO,
            UN_REEMPLAZOS => MI_MSGERROR
         );
  END;
  BEGIN  
    --Crea las no existentes
    MI_CAMPOS := ' COMPANIA, ' ||
                 ' ANO, ' ||
                 ' CODIGO , ' ||
                 ' NATURALEZA, ' || 
                 ' CENTRO_COSTO, ' ||
                 ' TERCERO, ' ||
                 ' SUCURSAL, ' ||
                 ' AUXILIAR, ' ||
                 ' REFERENCIA, ' ||
                 ' FUENTE_RECURSO';
    MI_VALORES := ' SELECT DISTINCT  ' ||
                  '        DET.COMPANIA,   ' ||
                  '        DET.ANO,  ' ||
                  '        DET.CUENTA,   ' ||
                  '        DET.NATURALEZA,  ' ||
                  '        DECODE(PLAN.MAN_CEN_CTO, 0,  ''' || PCK_DATOS.FC_CONS_CENTRO     || '''    ,DET.CENTRO_COSTO) ,    ' || 
                  '        DECODE(PLAN.MAN_AUX_TER, 0,  ''' || PCK_DATOS.FC_CONS_TERCERO    || '''    ,DET.TERCERO)      ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_TER, 0,  ''' || PCK_DATOS.FC_CONS_SUCURSAL   || '''    ,DET.SUCURSAL)     ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_GEN, 0,  ''' || PCK_DATOS.FC_CONS_AUXILIAR   || '''    ,DET.AUXILIAR)     ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_REF, 0,  ''' || PCK_DATOS.FC_CONS_REFERENCIA || '''    ,DET.REFERENCIA)   ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_FUE, 0,  ''' || PCK_DATOS.FC_CONS_FUENTE     || '''    ,DET.FUENTE_RECURSO)    ' ||
                  ' FROM  DETALLE_COMPROBANTE_CNT DET  ' ||
                  ' INNER JOIN PLAN_CONTABLE PLAN ' ||
                  '   ON DET.COMPANIA        = PLAN.COMPANIA   ' ||
                  '  AND DET.ANO             = PLAN.ANO       ' ||
                  '  AND DET.CUENTA          = PLAN.CODIGO     ' ||
                  ' LEFT JOIN SALDO_AUX_CONTABLE SA    ' ||
                  '   ON DET.COMPANIA        = SA.COMPANIA ' ||  
                  '  AND DET.ANO             = SA.ANO       ' ||
                  '  AND DET.CUENTA          = SA.CODIGO     ' ||
                  '  AND DECODE(PLAN.MAN_CEN_CTO, 0, ''' || PCK_DATOS.FC_CONS_CENTRO     || '''   ,DET.CENTRO_COSTO)    =SA.CENTRO_COSTO  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_TER, 0, ''' || PCK_DATOS.FC_CONS_TERCERO    || '''   ,DET.TERCERO)         =SA.TERCERO  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_TER, 0, ''' || PCK_DATOS.FC_CONS_SUCURSAL   || '''   ,DET.SUCURSAL)        =SA.SUCURSAL  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_GEN, 0, ''' || PCK_DATOS.FC_CONS_AUXILIAR   || '''   ,DET.AUXILIAR)        =SA.AUXILIAR  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_REF, 0, ''' || PCK_DATOS.FC_CONS_REFERENCIA || '''   ,DET.REFERENCIA)      =SA.REFERENCIA  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_FUE, 0, ''' || PCK_DATOS.FC_CONS_FUENTE     || '''   ,DET.FUENTE_RECURSO)  =SA.FUENTE_RECURSO  ' ||
                  ' WHERE DET.COMPANIA='''       || UN_COMPANIA   || '''' ||
                   ' AND DET.ANO='              || UN_ANIO       || 
                   ' AND DET.CUENTA BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' ||
                   ' AND TO_NUMBER(TO_CHAR(DET.FECHA,''MM'')) BETWEEN ' || UN_MES_INI || ' AND ' || UN_MES_FIN || 
                   ' AND SA.COMPANIA IS NULL';
    BEGIN
      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                            UN_ACCION  => 'IS', 
                                            UN_CAMPOS  => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'ANIO';
          MI_MSGERROR(1).VALOR := UN_ANIO;
          MI_MSGERROR(2).CLAVE := 'INICIAL';
          MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
          MI_MSGERROR(3).CLAVE := 'FINAL';
          MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACEROINSERTAR,
              UN_REEMPLAZOS => MI_MSGERROR
           ); 
  END;
  --actualiza los saldos 
  --MI_MESINI := 1;
  --MI_MESFIN := 12;

    IF MI_MESINI=13 THEN
      MI_MESINI := 12;
    END IF;
    IF MI_MESFIN=13 THEN
      MI_MESFIN := 12;
    END IF;

    BEGIN  
      MI_CAMPOS:='';
      <<SALDOS>>
      FOR RS IN 
      (
      SELECT P.COMPANIA, P.ANO, P.CODIGO,
              DECODE(P.MAN_CEN_CTO,0, PCK_DATOS.FC_CONS_CENTRO        ,DCT.CENTRO_COSTO)   CENTRO_COSTO, 
              DECODE(P.MAN_AUX_TER,0, PCK_DATOS.FC_CONS_TERCERO       ,DCT.TERCERO)        TERCERO,
              DECODE(P.MAN_AUX_TER,0, PCK_DATOS.FC_CONS_SUCURSAL      ,DCT.SUCURSAL)       SUCURSAL,
              DECODE(P.MAN_AUX_GEN,0, PCK_DATOS.FC_CONS_AUXILIAR      ,DCT.AUXILIAR)       AUXILIAR ,
              DECODE(P.MAN_AUX_REF,0, PCK_DATOS.FC_CONS_REFERENCIA    ,DCT.REFERENCIA)     REFERENCIA ,
              DECODE(P.MAN_AUX_FUE,0, PCK_DATOS.FC_CONS_FUENTE        ,DCT.FUENTE_RECURSO) FUENTE_RECURSO ,
              SUM(DECODE(DCT.MES,1,DCT.Valor_debito,0)) DEBITO1,
              SUM(DECODE(DCT.MES,2,DCT.Valor_debito,0)) DEBITO2,
              SUM(DECODE(DCT.MES,3,DCT.Valor_debito,0)) DEBITO3,
              SUM(DECODE(DCT.MES,4,DCT.Valor_debito,0)) DEBITO4,
              SUM(DECODE(DCT.MES,5,DCT.Valor_debito,0)) DEBITO5,
              SUM(DECODE(DCT.MES,6,DCT.Valor_debito,0)) DEBITO6,
              SUM(DECODE(DCT.MES,7,DCT.Valor_debito,0)) DEBITO7,
              SUM(DECODE(DCT.MES,8,DCT.Valor_debito,0)) DEBITO8,
              SUM(DECODE(DCT.MES,9,DCT.Valor_debito,0)) DEBITO9,
              SUM(DECODE(DCT.MES,10,DCT.Valor_debito,0)) DEBITO10,
              SUM(DECODE(DCT.MES,11,DCT.Valor_debito,0)) DEBITO11,
              SUM(DECODE(DCT.MES,12,DECODE(CIERRE,0,DCT.Valor_debito,0),0)) DEBITO12,
              SUM(DECODE(DCT.MES,12,DECODE(CIERRE,1,DCT.Valor_debito,-1,DCT.Valor_debito,0),0)) DEBITO13,
              SUM(DECODE(DCT.MES,1,DCT.Valor_credito,0)) CREDITO1,
              SUM(DECODE(DCT.MES,2,DCT.Valor_credito,0)) CREDITO2,
              SUM(DECODE(DCT.MES,3,DCT.Valor_credito,0)) CREDITO3,
              SUM(DECODE(DCT.MES,4,DCT.Valor_credito,0)) CREDITO4,
              SUM(DECODE(DCT.MES,5,DCT.Valor_credito,0)) CREDITO5,
              SUM(DECODE(DCT.MES,6,DCT.Valor_credito,0)) CREDITO6,
              SUM(DECODE(DCT.MES,7,DCT.Valor_credito,0)) CREDITO7,
              SUM(DECODE(DCT.MES,8,DCT.Valor_credito,0)) CREDITO8,
              SUM(DECODE(DCT.MES,9,DCT.Valor_credito,0)) CREDITO9,
              SUM(DECODE(DCT.MES,10,DCT.Valor_credito,0)) CREDITO10,
              SUM(DECODE(DCT.MES,11,DCT.Valor_credito,0)) CREDITO11,
              SUM(DECODE(DCT.MES,12,DECODE(CIERRE,0,DCT.Valor_CREDITO,0),0)) CREDITO12,
              SUM(DECODE(DCT.MES,12,DECODE(CIERRE,1,DCT.Valor_CREDITO,-1,DCT.Valor_CREDITO,0),0)) CREDITO13
        FROM PLAN_CONTABLE P INNER JOIN DETALLE_COMPROBANTE_CNT DCT
          ON P.COMPANIA=DCT.COMPANIA
         AND P.ANO     =DCT.ANO
         AND P.CODIGO  =DCT.CUENTA 
       WHERE P.COMPANIA=  UN_COMPANIA  
         AND P.ANO=       UN_ANIO
         AND P.CODIGO BETWEEN UN_CODIGO_INI AND UN_CODIGO_FIN 
         AND TO_NUMBER(TO_CHAR(DCT.FECHA,'MM'))  BETWEEN MI_MESINI AND MI_MESFIN 
       GROUP BY P.COMPANIA, P.ANO, P.CODIGO, 
       DECODE(P.MAN_CEN_CTO,0, PCK_DATOS.FC_CONS_CENTRO        ,DCT.CENTRO_COSTO), 
       DECODE(P.MAN_AUX_TER,0, PCK_DATOS.FC_CONS_TERCERO       ,DCT.TERCERO), 
       DECODE(P.MAN_AUX_TER,0, PCK_DATOS.FC_CONS_SUCURSAL      ,DCT.SUCURSAL), 
       DECODE(P.MAN_AUX_GEN,0, PCK_DATOS.FC_CONS_AUXILIAR      ,DCT.AUXILIAR), 
       DECODE(P.MAN_AUX_REF,0, PCK_DATOS.FC_CONS_REFERENCIA    ,DCT.REFERENCIA), 
       DECODE(P.MAN_AUX_FUE,0, PCK_DATOS.FC_CONS_FUENTE        ,DCT.FUENTE_RECURSO) 

      )
        LOOP
          MI_CAMPOS:='';
          <<CREARCAMPO>>
          FOR MI_I IN MI_MESINI .. MI_MESFIN LOOP
            IF MI_I <=12 THEN
              MI_CAMPOS := MI_CAMPOS || ' DEBITO'  || MI_I ||'='  || CASE MI_I WHEN 1 THEN RS.DEBITO1 
                                                                               WHEN 2 THEN RS.DEBITO2 
                                                                               WHEN 3 THEN RS.DEBITO3
                                                                               WHEN 4 THEN RS.DEBITO4
                                                                               WHEN 5 THEN RS.DEBITO5
                                                                               WHEN 6 THEN RS.DEBITO6
                                                                               WHEN 7 THEN RS.DEBITO7
                                                                               WHEN 8 THEN RS.DEBITO8
                                                                               WHEN 9 THEN RS.DEBITO9
                                                                               WHEN 10 THEN RS.DEBITO10
                                                                               WHEN 11 THEN RS.DEBITO11
                                                                               WHEN 12 THEN RS.DEBITO12
                                                                               ELSE 0 END ||
                                       ', CREDITO'  || MI_I ||'='  || CASE MI_I WHEN 1 THEN RS.CREDITO1 
                                                                               WHEN 2 THEN RS.CREDITO2 
                                                                               WHEN 3 THEN RS.CREDITO3
                                                                               WHEN 4 THEN RS.CREDITO4
                                                                               WHEN 5 THEN RS.CREDITO5
                                                                               WHEN 6 THEN RS.CREDITO6
                                                                               WHEN 7 THEN RS.CREDITO7
                                                                               WHEN 8 THEN RS.CREDITO8
                                                                               WHEN 9 THEN RS.CREDITO9
                                                                               WHEN 10 THEN RS.CREDITO10
                                                                               WHEN 11 THEN RS.CREDITO11
                                                                               WHEN 12 THEN RS.CREDITO12
                                                                               ELSE 0 END || ',';
            END IF;
            IF MI_I =12 AND UN_MES_FIN=13 THEN
              MI_CAMPOS := MI_CAMPOS || ' DEBITO13=' || RS.DEBITO13 || ', CREDITO13=' || RS.CREDITO13 || ',';
            END IF;
          END LOOP CREARCAMPO;
        MI_CAMPOS_AUX := MI_CAMPOS;

        --CONDICION CREADA PARA CORREGIR LA CADENA DE LA VARIABLE MI_CAMPOS        
          IF SUBSTR(MI_CAMPOS_AUX,-1)= ',' THEN
            MI_CAMPOS := SUBSTR(MI_CAMPOS,1,LENGTH(MI_CAMPOS)-1);
          END IF;

          MI_CONDICION:= ' COMPANIA='''       || UN_COMPANIA       || '''' || 
                     ' AND ANO='              || UN_ANIO           || 
                     ' AND CODIGO ='''        || RS.CODIGO         || '''' || 
                     ' AND CENTRO_COSTO='''   || RS.CENTRO_COSTO   || '''' || 
                     ' AND TERCERO='''        || RS.TERCERO        || '''' || 
                     ' AND SUCURSAL='''       || RS.SUCURSAL       || '''' || 
                     ' AND AUXILIAR='''       || RS.AUXILIAR       || '''' || 
                     ' AND REFERENCIA='''     || RS.REFERENCIA     || '''' || 
                     ' AND FUENTE_RECURSO=''' || RS.FUENTE_RECURSO || '''';
          BEGIN
		    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
				RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
			END;  

        END LOOP SALDOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
				  MI_MSGERROR(1).CLAVE := 'ANIO';
				  MI_MSGERROR(1).VALOR := UN_ANIO;
				  MI_MSGERROR(2).CLAVE := 'INICIAL';
				  MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
				  MI_MSGERROR(3).CLAVE := 'FINAL';
				  MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
					PCK_ERR_MSG.RAISE_WITH_MSG(
					  UN_EXC_COD    => SQLCODE,
					  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACTUALIZA,
					  UN_REEMPLAZOS => MI_MSGERROR
		 ); 				
    END;
   --elimina los valores en cero
   BEGIN
    MI_MESINI := 0;
    MI_MESFIN := 13;
    MI_STRSQL :='';
    MI_STRSQL1:='SALDOINICIAL=0 ';
    <<CEROMES>>
    FOR MI_I IN MI_MESINI .. MI_MESFIN LOOP
      MI_STRSQL1:= MI_STRSQL1 || ' AND ';
      MI_STRSQL1:= MI_STRSQL1 || ' DEBITO'  || MI_I || '=0 AND ' ||
                                 ' CREDITO' || MI_I || '=0 AND ' || 
                                 ' NETO'    || MI_I || '=0 AND ' ||
                                 ' SALDO'   || MI_I || '=0 ';
    END LOOP CEROMES;
    MI_CONDICION:=' COMPANIA = '''         || UN_COMPANIA   || '''' ||
                  ' AND ANO  ='            || UN_ANIO       || CHR(13) || CHR(10) ||
                  ' AND CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' ||
                  ' AND NOT(CENTRO_COSTO   = ''' ||PCK_DATOS.FC_CONS_CENTRO      || '''
                        AND TERCERO        = ''' ||PCK_DATOS.FC_CONS_TERCERO     || '''
                        AND SUCURSAL       = ''' ||PCK_DATOS.FC_CONS_SUCURSAL    || '''
                        AND AUXILIAR       = ''' ||PCK_DATOS.FC_CONS_AUXILIAR    || '''
                        AND REFERENCIA     = ''' ||PCK_DATOS.FC_CONS_REFERENCIA  || '''
                        AND FUENTE_RECURSO = ''' ||PCK_DATOS.FC_CONS_FUENTE      || '''
                           ) ' ||
                  ' AND '   || MI_STRSQL1 ;

    BEGIN
      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
		RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
	END;  

     PR_SALDOSYNETOS_AUX (UN_COMPANIA   => UN_COMPANIA,
                             UN_ANIO       => UN_ANIO, 
                             UN_MES_INI    => UN_MES_INI, 
                             UN_MES_FIN    => UN_MES_FIN, 
                             UN_CODIGO_INI => UN_CODIGO_INI, 
                             UN_CODIGO_FIN => UN_CODIGO_FIN);   
    PR_MAYORIZARCUENTAS(UN_COMPANIA => UN_COMPANIA, 
                         UN_ANO      => UN_ANIO,
                         UN_MES_INI  => UN_MES_INI,
                         UN_MES_FIN  => UN_MES_FIN );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
				  MI_MSGERROR(1).CLAVE := 'ANIO';
				  MI_MSGERROR(1).VALOR := UN_ANIO;
				  MI_MSGERROR(2).CLAVE := 'INICIAL';
				  MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
				  MI_MSGERROR(3).CLAVE := 'FINAL';
				  MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
					PCK_ERR_MSG.RAISE_WITH_MSG(
					  UN_EXC_COD    => SQLCODE,
					  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOELIMINA,
					  UN_REEMPLAZOS => MI_MSGERROR
		 ); 				
  END;
END PR_CUADRECONTA_AUX;

PROCEDURE PR_REVISAR_MOVIMIENTO_CONTABLE 
  /*
    NAME              : REVISAR_MOVIMIENTO_CONTABLE 
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : HENRY PUERTO
    DATE MIGRADOR     : 17/06/UN_ANO
    TIME              : 05:00 PM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 21/01/2015 - 23/01/2015 / 27/01/2015
    TIME              : 9:30 AM
    DESCRIPTION       : REVISA LOS INDICADORES DE MOVIMIENTO DE LAS CUENTAS DE CONTABILIDAD
    MODIFICATIONS     : - incluir rutina de errores
    @NAME:  actualizarIndicadorDeMovimientoContable
    @METHOD:  PUT
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO      IN PCK_SUBTIPOS.TI_ANIO
  )
AS
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM +6;
  GL_RTA          PCK_SUBTIPOS.TI_RTA_ACME;  
  CANT   NUMBER;   
BEGIN
  -- PONE EN -1S EL INDICADOR DE MOVIMIENTO DE TODAS
  MI_CAMPOS := ' VERIFICAR_MOV = 0';
  MI_CONDICION := ' COMPANIA=''' || UN_COMPANIA || '''' ||
                  '   AND ANO=' || UN_ANIO || '';                
  GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE',
                              UN_ACCION    => 'M', 
                              UN_CAMPOS    => MI_CAMPOS, 
                              UN_CONDICION => MI_CONDICION);                

  -- PONE EN CEROS EL INDICADOR DE MOVIMIENTO CUANDO TIENE HIJOS
  MI_CAMPOS := ' VERIFICAR_MOV=-1 ';
  MI_CONDICION := 'PC.COMPANIA =''' || UN_COMPANIA || '''' ||
                 ' AND PC.ANO ='    || UN_ANIO     ||
                 ' AND (PC.COMPANIA, PC.ANO, PC.CODIGO) IN ' ||
                   ' (SELECT I.COMPANIA, I.ANO, I.CODIGO ' ||
                   ' FROM PLAN_CONTABLE I  LEFT JOIN PLAN_CONTABLE F  ' ||
                   ' ON I.COMPANIA=F.COMPANIA ' ||
                   ' AND I.ANO=F.ANO  ' ||
                   ' AND F.CODIGO BETWEEN (I.CODIGO) AND I.CODIGO || ''' || PCK_DATOS.CONS_MAX_ID || '''' ||
                   ' WHERE I.COMPANIA=''' || UN_COMPANIA || '''' ||
                     ' AND I.ANO='        || UN_ANIO     ||
                   ' GROUP BY I.COMPANIA, I.ANO, I.CODIGO ' ||
                   ' HAVING COUNT(1)=1 ' ||
                   ' )';
  GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE PC',
                              UN_ACCION    => 'M', 
                              UN_CAMPOS    => MI_CAMPOS, 
                              UN_CONDICION => MI_CONDICION);   

  --ACTUALIZAR EL CAMPO MOVIMIENTO QUE EL RESULTADO DE VERIFICAR_MOV
  MI_CAMPOS := ' MOVIMIENTO=VERIFICAR_MOV ';
  MI_CONDICION := ' COMPANIA=''' || UN_COMPANIA || '''' ||
                  ' AND ANO='    || UN_ANIO     || '' ||
                  ' AND MOVIMIENTO<>VERIFICAR_MOV ' ||
                  ' AND LENGTH(CODIGO) > 6' ||
                  ' AND (MAN_CEN_CTO +  MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_FUE + MAN_AUX_REF) = 0 ';
  GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE',
                              UN_ACCION    => 'M', 
                              UN_CAMPOS    => MI_CAMPOS, 
                              UN_CONDICION => MI_CONDICION);        


   SELECT COUNT(COMPANIA) 
    INTO CANT
   FROM PLAN_CONTABLE
     WHERE COMPANIA = UN_COMPANIA
      AND ANO       = UN_ANIO
      AND MOVIMIENTO NOT IN(0)
      AND LENGTH(CODIGO)<6 
      AND (MAN_CEN_CTO +  MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_FUE + MAN_AUX_REF) = 0;
  IF CANT>0 THEN  
      MI_CAMPOS := ' MOVIMIENTO = 0 ';
      MI_CONDICION := '     COMPANIA=''' || UN_COMPANIA || '''' ||
                      ' AND ANO='        || UN_ANIO     || ''   ||
                      ' AND MOVIMIENTO NOT IN(0) ' ||
                      ' AND LENGTH(CODIGO)<6' || 
                      ' AND (MAN_CEN_CTO +  MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_FUE + MAN_AUX_REF) = 0 ';                
      GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE',
                                  UN_ACCION    => 'M', 
                                  UN_CAMPOS    => MI_CAMPOS, 
                                  UN_CONDICION => MI_CONDICION);
    END IF;

  EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Revisar Movimientos ';
    PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'PLAN_CONTABLE','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_RTA || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END PR_REVISAR_MOVIMIENTO_CONTABLE;


FUNCTION FC_VERIFICAR_INDICADORES_CON
  /*
    NAME              : VERIFICAR_INDICADORES 
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLNCO
    DATE MIGRADOR     : 23/01/2015
    TIME              : 2:15 PM
    MODIFIER          : JOSE PASCUAL GOMEZ BLNCO / ADRIANA CACERES
    DATE MODIFIED     : 23/01/2015 - 23/01/2015 - 27/01/2015 /17/11/2016
    TIME              : 2:15 PM
    DESCRIPTION       : REVISA LOS INDICADORES DE MOVIMIENTO Y AUXILIARES PARA DEVOLVER EN UNA TYPE LOS VALORES CORRECTOS DE LOS MISMO PARA LA CUENTA DADA
    MODIFICATIONS     : - incluir rutina de errores / Modificacion manejo de excepciones
    @NAME:  consultarIndicadoresMovimiento
    @METHOD:  GET
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOCONTA, 
    UN_CENTRO         IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
    UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO, 
    UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR,
    UN_REFERENCIA     IN PCK_SUBTIPOS.TI_REFERENCIA, 
    UN_FUENTERECURSO  IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
  ) 
  RETURN TYP_CUENTA_AUX
AS
  MI_CUENTA       TYP_CUENTA_AUX;
  MI_MAN_CEN_CTO  PCK_SUBTIPOS.TI_LOGICO;
  MI_MAN_AUX_TER  PCK_SUBTIPOS.TI_LOGICO;
  MI_MAN_AUX_GEN  PCK_SUBTIPOS.TI_LOGICO;
  MI_MAN_AUX_FUE  PCK_SUBTIPOS.TI_LOGICO;
  MI_MAN_AUX_REF  PCK_SUBTIPOS.TI_LOGICO;
  MI_MOVIMIENTO   PCK_SUBTIPOS.TI_LOGICO;
  MI_NATURALEZA   PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MI_CLASECUENTA  PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM +7;
  GL_RTA          PCK_SUBTIPOS.TI_RTA_ACME; 
  --PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN 

BEGIN
  BEGIN
  SELECT MAN_CEN_CTO,   MAN_AUX_TER,   MAN_AUX_GEN,   MAN_AUX_FUE,    MAN_AUX_REF,    MOVIMIENTO,    NATURALEZA,   CLASECUENTA          
  INTO   MI_MAN_CEN_CTO, MI_MAN_AUX_TER, MI_MAN_AUX_GEN, MI_MAN_AUX_FUE, MI_MAN_AUX_REF,
         MI_MOVIMIENTO, MI_NATURALEZA, MI_CLASECUENTA 
  FROM   PLAN_CONTABLE 
  WHERE COMPANIA = UN_COMPANIA AND ANO = UN_ANIO AND CODIGO = UN_CODIGO;

  EXCEPTION WHEN NO_DATA_FOUND THEN 
     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_IND,
        UN_TABLAERROR =>'PLAN_CONTABLE'
      );  
  END;
  MI_CUENTA.MI_COMPANIA := UN_COMPANIA;
  MI_CUENTA.MI_ANIO := UN_ANIO;
  MI_CUENTA.MI_CODIGO := UN_CODIGO;

  IF (MI_MAN_CEN_CTO +  MI_MAN_AUX_TER + MI_MAN_AUX_GEN + MI_MAN_AUX_FUE + MI_MAN_AUX_REF + MI_MOVIMIENTO) = 0 THEN
      BEGIN
         RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_SININD,
        UN_TABLAERROR =>'PLAN_CONTABLE'
      ); 
      END;
  END IF;  

  MI_CUENTA.MI_NATURALEZA     := MI_NATURALEZA;
  MI_CUENTA.MI_CLASECUENTA    := MI_CLASECUENTA;
  MI_CUENTA.MI_MOVIMIENTO     := MI_MOVIMIENTO;
  MI_CUENTA.MI_TERCERO        := UN_TERCERO;
  MI_CUENTA.MI_SUCURSAL       := UN_SUCURSAL;
  MI_CUENTA.MI_CENTROCOSTO    := UN_CENTRO;  
  MI_CUENTA.MI_AUXILIAR       := UN_AUXILIAR;
  MI_CUENTA.MI_REFERENCIA     := UN_REFERENCIA;
  MI_CUENTA.MI_FUENTERECURSO  := UN_FUENTERECURSO;

  IF MI_MAN_CEN_CTO = 0 OR UN_CENTRO IS NULL THEN 
    MI_CUENTA.MI_CENTROCOSTO := PCK_DATOS.CONS_CENTRO;
  END IF;
  IF MI_MAN_AUX_TER = 0 OR UN_TERCERO IS NULL THEN 
    MI_CUENTA.MI_TERCERO := PCK_DATOS.CONS_TERCERO;
    MI_CUENTA.MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
  END IF;
  IF MI_MAN_AUX_GEN = 0 OR UN_AUXILIAR IS NULL THEN 
    MI_CUENTA.MI_AUXILIAR := PCK_DATOS.CONS_AUXILIAR;       
  END IF;
  IF MI_MAN_AUX_REF = 0 OR UN_REFERENCIA IS NULL THEN 
    MI_CUENTA.MI_REFERENCIA := PCK_DATOS.CONS_REFERENCIA;      
  END IF;
  IF MI_MAN_AUX_FUE = 0 OR UN_FUENTERECURSO IS NULL THEN 
    MI_CUENTA.MI_FUENTERECURSO := PCK_DATOS.CONS_FUENTE;   
  END IF;

  RETURN MI_CUENTA;        
END FC_VERIFICAR_INDICADORES_CON ;

PROCEDURE PR_SALDOSYNETOS_AUX(
  /*
    NAME              : CUADRECONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 29/01/2015
    TIME              : 11:09 AM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 29/01/2015
    TIME              : 11:09 AM
    DESCRIPTION       : Actualiza los debitos credito, netos y saldos contables por cada mes
    @NAME:  CorregirCuentasAuxContables
    @METHOD:  PUT
  */
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES_INI        IN PCK_SUBTIPOS.TI_MES,
  UN_MES_FIN        IN PCK_SUBTIPOS.TI_MES,
  UN_CODIGO_INI     IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT '0',
  UN_CODIGO_FIN     IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT PCK_DATOS.FC_CONS_MAX_ID
  )
  AS
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME; 
  MI_MESINI       PCK_SUBTIPOS.TI_MES :=UN_MES_INI;
  MI_MESFIN       PCK_SUBTIPOS.TI_MES :=UN_MES_FIN;
  MI_I            PCK_SUBTIPOS.TI_ENTERO;   
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  -- Refleja  los saldos para todos los meses
  BEGIN
	MI_I:=0;
    IF MI_MESINI= 0 THEN
      MI_CAMPOS:= ' NETO0 =DECODE(NATURALEZA,''D'',DEBITO0-CREDITO0,CREDITO0-DEBITO0), ' ||
                  ' SALDO0=DECODE(NATURALEZA,''D'',DEBITO0-CREDITO0,CREDITO0-DEBITO0) ';
      MI_CONDICION :=     ' COMPANIA='''       || UN_COMPANIA   || '''' ||
                      ' AND ANO='              || UN_ANIO       || 
                      ' AND CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''';           
      BEGIN
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    END IF;   
    IF UN_MES_INI=0 THEN
      MI_MESINI := 1;
    ELSE 
      MI_MESINI := UN_MES_INI;
    END IF;
    IF UN_MES_FIN=0 THEN
      MI_MESFIN := 1;
    ELSE 
      MI_MESFIN := UN_MES_FIN;
    END IF; 
    IF UN_MES_FIN<>0 THEN
      <<SALDONETOMES>>
      FOR MI_I IN MI_MESINI .. MI_MESFIN LOOP
        MI_CAMPOS:= ' NETO'  || MI_I || '=' ||
                    ' DECODE(NATURALEZA,''D'',DEBITO' || MI_I || '-CREDITO' || MI_I || ',CREDITO' || MI_I || '-DEBITO' || MI_I || '), ' ||
                    ' SALDO' || MI_I || '= SALDO'     || (MI_I - 1) || 
                  ' + DECODE(NATURALEZA,''D'',DEBITO' || MI_I || '-CREDITO' || MI_I || ',CREDITO' || MI_I || '-DEBITO' || MI_I || ')';

        MI_CONDICION:= ' COMPANIA='''       || UN_COMPANIA   || '''' || 
                   ' AND ANO='              || UN_ANIO       || 
                   ' AND CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' ;

        BEGIN           
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                              UN_ACCION    => 'M', 
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;		  
      END LOOP SALDONETOMES;
    END IF;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
				  MI_MSGERROR(1).CLAVE := 'ANIO';
				  MI_MSGERROR(1).VALOR := UN_ANIO;
				  MI_MSGERROR(2).CLAVE := 'INICIAL';
				  MI_MSGERROR(2).VALOR := UN_CODIGO_INI;
				  MI_MSGERROR(3).CLAVE := 'FINAL';
				  MI_MSGERROR(3).VALOR := UN_CODIGO_FIN;
				  MI_MSGERROR(4).CLAVE := 'MES';
				  MI_MSGERROR(4).VALOR := MI_I;
					PCK_ERR_MSG.RAISE_WITH_MSG(
					  UN_EXC_COD    => SQLCODE,
					  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACTUALIZASALDO,
					  UN_REEMPLAZOS => MI_MSGERROR
		  );
END PR_SALDOSYNETOS_AUX;

PROCEDURE PR_SUBIR_SALDOS_INICIALES(
/*
    NAME              : CUADRECONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : HENRY PUERTO
    DATE MIGRADOR     : 03/09/2009
    TIME              : 05:00 PM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 26/01/2015
    TIME              : 4:40 PM
    DESCRIPTION       : Registra en los saldos 0 del plan contable lo registrado en el los saldos iniciales
    @NAME:  cargarSaldosIniciales
    @METHOD:  PUT
  */
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_MODIFICADOR    IN VARCHAR2,
  UN_DESDEPASARSAL  IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  )
AS
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_EXCEPCION    PCK_SUBTIPOS.TI_LOGICO := 0;
  GL_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
  MI_RTA          VARCHAR2(2 CHAR);
  BEGIN

    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA   => UN_COMPANIA,
                                                      UN_ANO        => UN_ANIO,
                                                      UN_MODULO     => 1,
                                                      UN_PROCESO    => 1); 
    IF MI_RTA <> 'A' THEN
      DECLARE
        MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          GL_PASASALDO:=0;  
          MI_MSGERROR(1).CLAVE := 'ANO';
          MI_MSGERROR(1).VALOR := UN_ANIO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD => SQLCODE,
                 UN_TABLAERROR=>'SALDOSINICIALES',
                 UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO,
                 UN_REEMPLAZOS => MI_MSGERROR
               );
      END;  
    END IF;


   BEGIN 
    DECLARE 
      X PCK_SUBTIPOS.TI_LOGICO;
    BEGIN
      SELECT DISTINCT 1
      INTO X
      FROM  SALDOSINICIALES 
      WHERE COMPANIA =UN_COMPANIA 
      AND ANO =UN_ANIO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      GL_PASASALDO:=0;
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_1
          );
  END;
  /*
  * SE CONDICIONA DE TAL FORMA QUE SI LA RUTINA ES LLAMADA DESDE PR_PASAR_SALDOS_ANO_SIGUIENTE
  * NO SE TENGA EN CUENTA QUE EL BALANCE DEBE ESTAR CUADRADO
  */
  IF UN_DESDEPASARSAL = 0 THEN  
      BEGIN 
      DECLARE
        SUMADEDEBITO  PCK_SUBTIPOS.TI_DOBLE;
        SUMADECREDITO PCK_SUBTIPOS.TI_DOBLE;
      BEGIN
        SELECT SUM(DEBITO) AS SUMADEDEBITO, SUM(CREDITO) AS SUMADECREDITO 
        INTO   SUMADEDEBITO, SUMADECREDITO
        FROM   SALDOSINICIALES 
        WHERE COMPANIA = UN_COMPANIA 
           AND ANO     = UN_ANIO;

        IF (TRUNC((SUMADEDEBITO*100)+0.5)/100) <> (TRUNC((SUMADECREDITO*100)+0.5)/100) THEN
            MI_EXCEPCION := -1;
            GL_PASASALDO:=0;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END IF;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        GL_PASASALDO:=0;
        MI_EXCEPCION := -1;
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_1
            );
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        GL_PASASALDO:=0;
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD
            );
      END;
      --
      DECLARE
       ACTIVO                PCK_SUBTIPOS.TI_DOBLE;
       PASIVO                PCK_SUBTIPOS.TI_DOBLE;
       PATRIMONIO            PCK_SUBTIPOS.TI_DOBLE;
       INGRESOS              PCK_SUBTIPOS.TI_DOBLE;
       GASTOS                PCK_SUBTIPOS.TI_DOBLE;
       COSTOVENTAS           PCK_SUBTIPOS.TI_DOBLE;
       COSTOPRODUCCION       PCK_SUBTIPOS.TI_DOBLE;
       CUENTASORDEN          PCK_SUBTIPOS.TI_DOBLE;
       CUENTASORDENPORCONTRA PCK_SUBTIPOS.TI_DOBLE;
      BEGIN


        SELECT SUM(CASE WHEN SUBSTR(CODIGO,0,1)='1'THEN DEBITO-CREDITO ELSE 0 END) AS ACTIVO, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='2'THEN CREDITO-DEBITO ELSE 0 END) AS PASIVO, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='3'THEN CREDITO-DEBITO ELSE 0 END) AS PATRIMONIO, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='4'THEN CREDITO-DEBITO ELSE 0 END) AS INGRESOS, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='5'THEN DEBITO-CREDITO ELSE 0 END) AS GASTOS, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='6'THEN DEBITO-CREDITO ELSE 0 END) AS COSTOVENTAS, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='7'THEN DEBITO-CREDITO ELSE 0 END) AS COSTOPRODUCCION, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='8'THEN DEBITO-CREDITO ELSE 0 END) AS CUENTASORDEN, 
               SUM(CASE WHEN SUBSTR(CODIGO,0,1)='9'THEN CREDITO-DEBITO ELSE 0 END) AS CUENTASORDENPORCONTRA
        INTO   ACTIVO, PASIVO, PATRIMONIO, INGRESOS, GASTOS, COSTOVENTAS, COSTOPRODUCCION, CUENTASORDEN, CUENTASORDENPORCONTRA
        FROM   SALDOSINICIALES  
        WHERE  COMPANIA =UN_COMPANIA 
        AND         ANO =UN_ANIO;
        --
        BEGIN 
          IF TRUNC(((NVL(ACTIVO, 0) + NVL(GASTOS, 0) + NVL(COSTOVENTAS, 0) + NVL(COSTOPRODUCCION, 0)) * 100) + 0.5) / 100 <> TRUNC(((NVL(PATRIMONIO, 0) + NVL(PASIVO, 0) + NVL(INGRESOS, 0)) * 100) + 0.5) / 100 THEN
               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;      
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          GL_PASASALDO:=0;
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_6
            );
       END;
        --
      BEGIN
        IF TRUNC((NVL(CUENTASORDEN, 0) * 100) + 0.5) / 100 <> TRUNC((NVL(CUENTASORDENPORCONTRA, 0) * 100) + 0.5) / 100 THEN
            MI_EXCEPCION := -1;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;     
        END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            GL_PASASALDO:=0;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_7
            ); 
        END;
      END;  
  END IF;
  PR_REVISAR_MOVIMIENTO_CONTABLE(UN_COMPANIA, UN_ANIO);

  MI_CAMPOS := ' SALDOINICIAL =0,' || 
               ' SALDO0       =0,' ||
               ' CREDITO0     =0,' ||     
               ' DEBITO0      =0,' ||
               ' NETO0        =0,' ||
               ' MODIFIED_BY  =''' || UN_MODIFICADOR || ''',' ||
               ' DATE_MODIFIED=SYSDATE';
  MI_CONDICION := ' COMPANIA=''' || UN_COMPANIA || '''' ||
                  ' AND ANO=' || UN_ANIO;   

  GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE', 
                              UN_ACCION    => 'M', 
                              UN_CAMPOS    => MI_CAMPOS,
                              UN_CONDICION => MI_CONDICION); 

  MI_CAMPOS := '(SALDOINICIAL, ' || 
               ' SALDO0, ' || 
               ' NETO0, ' || 
               ' DEBITO0, ' ||  
               ' CREDITO0 ' || 
               ' ) = ' || 
               ' (SELECT  ' || 
                ' SUM(DECODE(PC.NATURALEZA,''D'',DEBITO-CREDITO,CREDITO-DEBITO)), ' || 
                ' SUM(DECODE(PC.NATURALEZA,''D'',DEBITO-CREDITO,CREDITO-DEBITO)), ' || 
                ' SUM(DECODE(PC.NATURALEZA,''D'',DEBITO-CREDITO,CREDITO-DEBITO)), ' || 
                ' SUM(DEBITO), ' || 
                ' SUM(CREDITO) ' || 
               ' FROM  SALDOSINICIALES PCN  ' || 
               ' WHERE PCN.COMPANIA = PC.COMPANIA ' || 
               ' AND PCN.ANO        = PC.ANO ' || 
               ' AND PCN.CODIGO     = PC.CODIGO ' || 
               ')';
  MI_CONDICION := ' PC.COMPANIA=''' || UN_COMPANIA || '''' ||
                  ' AND PC.ANO='    || UN_ANIO || 
                  ' AND EXISTS(SELECT COMPANIA ' ||
                  '            FROM  SALDOSINICIALES PCN  ' ||
                  '            WHERE PCN.COMPANIA = PC.COMPANIA  ' ||
                  '              AND PCN.ANO      = PC.ANO  ' ||
                  '              AND PCN.CODIGO   = PC.CODIGO )';

  GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE PC', 
                              UN_ACCION    => 'M', 
                              UN_CAMPOS    => MI_CAMPOS,
                              UN_CONDICION => MI_CONDICION); 


  PR_SUBIR_SALDOS_INICIALES_AUX (UN_COMPANIA   => UN_COMPANIA, 
                                 UN_ANIO       => UN_ANIO);

  PR_MAYORIZARCUENTAS(UN_COMPANIA => UN_COMPANIA, 
                     UN_ANO      => UN_ANIO,
                     UN_MES_INI  => 0,
                     UN_MES_FIN  => 13 );

   MI_CAMPOS := 'CONTABILIZADO = -1, DATE_MODIFIED = TO_DATE(SYSDATE,''DD/MM/YYYY HH24:MI:SS''), MODIFIED_BY=''' || UN_MODIFICADOR || '''';
   MI_CONDICION := ' COMPANIA=''' || UN_COMPANIA || '''' ||
                   ' AND ANO='    || UN_ANIO ;


  GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDOSINICIALES PC', 
                            UN_ACCION    => 'M', 
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_CONDICION => MI_CONDICION);

END PR_SUBIR_SALDOS_INICIALES;

PROCEDURE PR_SUBIR_SALDOS_INICIALES_AUX(
/*
    NAME              : CUADRECONTA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 26/01/2015
    TIME              : 05:03 PM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 26/01/2015
    TIME              : 5:03 PM
    DESCRIPTION       : Registra en los saldos 0 del auxiliar de saldos del plan contable lo registrado en el los saldos iniciales
    @NAME:  cargarSaldosInicialesAuxiliares
    @METHOD:  POST
  */
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_CODIGO_INI     IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT '0',
  UN_CODIGO_FIN     IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT PCK_DATOS.FC_CONS_MAX_ID
  )
AS
  GL_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TABLA        PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM +11;

BEGIN
  DECLARE
    X PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    SELECT DISTINCT 1
    INTO X
    FROM  SALDOSINICIALES 
    WHERE COMPANIA = UN_COMPANIA 
      AND ANO      = UN_ANIO
      AND CODIGO BETWEEN UN_CODIGO_INI AND UN_CODIGO_FIN;
    EXCEPTION WHEN NO_DATA_FOUND THEN
     MI_CAMPOS := ' SALDOINICIAL =0,' || 
                  ' SALDO0       =0,' ||
                  ' CREDITO0     =0,' ||     
                  ' DEBITO0      =0,' ||
                  ' NETO0        =0';
      MI_CONDICION := ' COMPANIA=''' || UN_COMPANIA || '''' ||
                  ' AND ANO=' || UN_ANIO ||
                  ' AND CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''';    

      GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                UN_ACCION    => 'M', 
                                UN_CAMPOS    => MI_CAMPOS,
                                UN_CONDICION => MI_CONDICION);
      RETURN;
  END;

  --Ajustar la naturaleza de acuerdo al plan contable  
  MI_TABLA := '( ' ||
              '  SELECT PC.NATURALEZA INI, SA.NATURALEZA FIN ' ||
              '  FROM PLAN_CONTABLE PC INNER JOIN  SALDO_AUX_CONTABLE SA ' ||
              '   ON PC.COMPANIA=SA.COMPANIA ' ||
              '  AND PC.ANO     =SA.ANO '||
              '  AND PC.CODIGO  =SA.CODIGO ' ||
              '   WHERE PC.COMPANIA='''  || UN_COMPANIA || '''' ||
                  ' AND PC.ANO='         || UN_ANIO     || 
                  ' AND PC.CODIGO BETWEEN '''|| UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' ||
                  ' AND PC.NATURALEZA<>SA.NATURALEZA ' ||
               ' )';
  MI_CAMPOS := ' FIN=INI';

  GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                            UN_ACCION    => 'M', 
                            UN_CAMPOS    => MI_CAMPOS);
  --Crea las no existentes
  MI_CAMPOS := ' COMPANIA, '    ||
               ' ANO, '         ||
               ' CODIGO, '      ||
               ' NATURALEZA, '  || 
               ' CENTRO_COSTO, '||
               ' TERCERO, '     ||
               ' SUCURSAL, '    ||
               ' AUXILIAR, '    ||
               ' REFERENCIA, '  ||
               ' FUENTE_RECURSO';

  MI_VALORES := ' SELECT DISTINCT SALI.COMPANIA, ' || 
                       ' SALI.ANO, '      ||
                       ' SALI.CODIGO, '   ||
                       ' P.NATURALEZA, '  ||
                       ' DECODE(P.MAN_CEN_CTO,0,''' || PCK_DATOS.CONS_CENTRO     || ''',SALI.CENTRO_COSTO), ' ||
                       ' DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_TERCERO    || ''',SALI.TERCERO)     , ' ||
                       ' DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_SUCURSAL   || ''',SALI.SUCURSAL)    , ' ||
                       ' DECODE(P.MAN_AUX_GEN,0,''' || PCK_DATOS.CONS_AUXILIAR   || ''',SALI.AUXILIAR)    , ' ||
                       ' DECODE(P.MAN_AUX_REF,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.REFERENCIA)  , ' ||
                       ' DECODE(P.MAN_AUX_FUE,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.FUENTE_RECURSO)' ||
               ' FROM  SALDOSINICIALES SALI ' ||
               ' INNER JOIN PLAN_CONTABLE P ' ||
                  ' ON SALI.COMPANIA = P.COMPANIA '  ||
                 ' AND SALI.ANO      = P.ANO '       ||
                 ' AND SALI.CODIGO   = P.CODIGO '    || 
               ' LEFT JOIN SALDO_AUX_CONTABLE SA '   ||
                  ' ON SALI.COMPANIA  =SA.COMPANIA ' || 
                 ' AND SALI.ANO       =SA.ANO '      ||
                 ' AND SALI.CODIGO    =SA.CODIGO '   ||
                 ' AND DECODE(P.MAN_CEN_CTO,0,''' || PCK_DATOS.CONS_CENTRO     || ''',SALI.CENTRO_COSTO)   =SA.CENTRO_COSTO ' ||
                 ' AND DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_TERCERO    || ''',SALI.TERCERO)        =SA.TERCERO ' ||
                 ' AND DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_SUCURSAL   || ''',SALI.SUCURSAL)       =SA.SUCURSAL ' ||
                 ' AND DECODE(P.MAN_AUX_GEN,0,''' || PCK_DATOS.CONS_AUXILIAR   || ''',SALI.AUXILIAR)       =SA.AUXILIAR ' ||
                 ' AND DECODE(P.MAN_AUX_REF,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.REFERENCIA)     =SA.REFERENCIA ' ||
                 ' AND DECODE(P.MAN_AUX_FUE,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.FUENTE_RECURSO) =SA.FUENTE_RECURSO ' ||
               ' WHERE SALI.COMPANIA='''       || UN_COMPANIA   || '''' ||
                 ' AND SALI.ANO='              || UN_ANIO       || 
                 ' AND SALI.CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''' ||
                 ' AND SA.COMPANIA IS NULL';
  GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE SA', 
                            UN_ACCION    => 'IS', 
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_VALORES   => MI_VALORES);            

  --Actualiza las que existen
  MI_CAMPOS := '(SALDOINICIAL, ' || 
               ' SALDO0, ' || 
               ' NETO0, ' || 
               ' DEBITO0, ' ||  
               ' CREDITO0 ' || 
               ' ) = ' || 
               ' (SELECT ' || 
                ' NVL(SUM(DECODE(P.NATURALEZA,''D'',SALI.DEBITO-SALI.CREDITO,SALI.CREDITO-SALI.DEBITO)),0), ' || 
                ' NVL(SUM(DECODE(P.NATURALEZA,''D'',SALI.DEBITO-SALI.CREDITO,SALI.CREDITO-SALI.DEBITO)),0), ' || 
                ' NVL(SUM(DECODE(P.NATURALEZA,''D'',SALI.DEBITO-SALI.CREDITO,SALI.CREDITO-SALI.DEBITO)),0), ' || 
                ' NVL(SUM(SALI.DEBITO),0), ' || 
                ' NVL(SUM(SALI.CREDITO),0) ' || 
               ' FROM  SALDOSINICIALES SALI ' ||
               ' INNER JOIN PLAN_CONTABLE P ' ||
                  ' ON SALI.COMPANIA = P.COMPANIA '  ||
                 ' AND SALI.ANO      = P.ANO '       ||
                 ' AND SALI.CODIGO   = P.CODIGO '        || 
               ' WHERE SALI.COMPANIA =SA.COMPANIA '  || 
                 ' AND SALI.ANO      =SA.ANO ' || 
                 ' AND SALI.CODIGO       =SA.CODIGO ' || 
                 ' AND DECODE(P.MAN_CEN_CTO,0,''' || PCK_DATOS.CONS_CENTRO     || ''',SALI.CENTRO_COSTO)   =SA.CENTRO_COSTO ' ||
                 ' AND DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_TERCERO    || ''',SALI.TERCERO)        =SA.TERCERO ' ||
                 ' AND DECODE(P.MAN_AUX_TER,0,''' || PCK_DATOS.CONS_SUCURSAL   || ''',SALI.SUCURSAL)       =SA.SUCURSAL ' ||
                 ' AND DECODE(P.MAN_AUX_GEN,0,''' || PCK_DATOS.CONS_AUXILIAR   || ''',SALI.AUXILIAR)       =SA.AUXILIAR ' ||
                 ' AND DECODE(P.MAN_AUX_REF,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.REFERENCIA)     =SA.REFERENCIA ' ||
                 ' AND DECODE(P.MAN_AUX_FUE,0,''' || PCK_DATOS.CONS_REFERENCIA || ''',SALI.FUENTE_RECURSO) =SA.FUENTE_RECURSO ' || 
               ')';
  MI_CONDICION := ' SA.COMPANIA='''           || UN_COMPANIA   || '''' ||
                  ' AND SA.ANO='              || UN_ANIO       || 
                  ' AND SA.CODIGO BETWEEN ''' || UN_CODIGO_INI || ''' AND ''' || UN_CODIGO_FIN || '''';            

  GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE SA', 
                            UN_ACCION    => 'M', 
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_CONDICION => MI_CONDICION);
   PR_SALDOSYNETOS_AUX     (UN_COMPANIA   => UN_COMPANIA,
                            UN_ANIO       => UN_ANIO, 
                            UN_MES_INI    => 0, 
                            UN_MES_FIN    => 13, 
                            UN_CODIGO_INI => UN_CODIGO_INI, 
                            UN_CODIGO_FIN => UN_CODIGO_FIN);

  EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Subir saldos auxiliares';
    PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'SALDO_AUX_CONTABLE','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_RTA || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END PR_SUBIR_SALDOS_INICIALES_AUX;

PROCEDURE PR_PASAR_SALDOS_ANO_SIGUIENTE (
/*
    NAME              : PR_PASAR_SALDOS_ANO_SIGUIENTE
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 05/02/2015
    TIME              : 05:03 PM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO/YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 05/02/2015 - 01/06/2016 - 11/02/2019
    TIME              : 5:03 PM / 9:43 AM  / 05:20PM
    DESCRIPTION       : Pasa los saldos del mes 13 a los saldos iniciales del plan contable
    MODIFICATIONS     : Se incluye validación del año del Plan Contable 
                      : Se cambia el proceso para guarde los saldos en saldos iniciales y luego contabilice
                        con esto se unifica el proceso de los saldo0.
                        Se crea indicador (INICIA_CONTABILIDAD) en la tabla ano que permite identificar si en el año se inicia con saldos iniciales
                        para controlar que no permite pasar saldos
    @NAME:  prepararSaldosAnoSiguiente
    @METHOD:  POST
  */
        UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_ANO_DESTINO 	IN PCK_SUBTIPOS.TI_ANIO
				)  
AS
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_EXCLUIDOS    PCK_SUBTIPOS.TI_EXCLUIDOS;
  MI_ANIO         PCK_SUBTIPOS.TI_ANIO := 0;
  MI_ANO_INICIAL  PCK_SUBTIPOS.TI_ANIO;
  MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXISTE PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_INICIACONTA  PCK_SUBTIPOS.TI_LOGICO;
  MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ERROR        CLOB;

  BEGIN
    MI_ANO_INICIAL := UN_ANO_DESTINO-1;

    /**
    * SI EL AÑO ARRANCA CON PLAN CONTABLE Y SALDOS NUEVOS NO PERMITA PASAR SALDOS DE UN AÑOA A OTRO
    **/
    MI_INICIACONTA:=0;
    SELECT INICIA_CONTABILIDAD
    INTO MI_INICIACONTA
    FROM ANO
    WHERE COMPANIA = UN_COMPANIA
      AND NUMERO   = UN_ANO_DESTINO;
    IF  MI_INICIACONTA <> 0 THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => -20001,
                                   UN_TABLAERROR  => 'ANO',
                                   UN_ERROR_COD   => PCK_ERRORES.ERR_CONTA_PASARSALDONUEVO,
                                   UN_REEMPLAZOS  => MI_REEMPLAZOS);
    END IF;
    BEGIN
      SELECT COUNT(ANO)
      INTO MI_ANIO
      FROM PLAN_CONTABLE
      WHERE COMPANIA = UN_COMPANIA
        AND ANO   = UN_ANO_DESTINO;
      IF MI_ANIO=0 THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_3
      );
    END;  

    MI_ANIO :=0;
    BEGIN
      SELECT COUNT(ANO)
      INTO MI_ANIO
      FROM PLAN_CONTABLE
      WHERE COMPANIA = UN_COMPANIA
        AND ANO   = MI_ANO_INICIAL;
      IF MI_ANIO=0 THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_4
      );
    END;  

    MI_ERROR :='';
    <<ERRADAS>>
    FOR RS IN (
                WITH  BASE AS (
                          SELECT 'EXISTE' ESTADO, COMPANIA,ANO,CODIGO,NOMBRE,
                                  SALDO0      ,
                                  MOVIMIENTO +  MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_REF + MAN_AUX_FUE MOV
                          FROM PLAN_CONTABLE  
                          WHERE COMPANIA = UN_COMPANIA 
                          AND ANO       = UN_ANO_DESTINO
                          AND SUBSTR(CODIGO,1,1) IN (1,2,3,8,9) 
                          UNION ALL
                          SELECT 'COPIA' ESTADO, PP.COMPANIA,UN_ANO_DESTINO + 0 ANO,CODIGO,NOMBRE,
                                  PP.SALDO13,
                                  MOVIMIENTO +  MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_REF + MAN_AUX_FUE MOV
                          FROM PLAN_CONTABLE PP  
                          WHERE PP.COMPANIA = UN_COMPANIA 
                            AND PP.ANO      = MI_ANO_INICIAL 
                            AND SUBSTR(CODIGO,1,1) IN (1,2,3,8,9)  
                            AND PP.CODIGO NOT IN(SELECT CODIGO      
                                          FROM PLAN_CONTABLE  
                                          WHERE COMPANIA = UN_COMPANIA 
                                            AND ANO      = UN_ANO_DESTINO 
                                            AND CODIGO   = PP.CODIGO) 
                      )
                  SELECT *
                  FROM BASE INI
                  WHERE CODIGO IN( SELECT INI.CODIGO
                                  FROM BASE
                                  WHERE COMPANIA= INI.COMPANIA
                                    AND ANO     = INI.ANO
                                    AND CODIGO  = SUBSTR(INI.CODIGO, 1, LENGTH(CODIGO))
                                    AND CODIGO  <> INI.CODIGO
                                    AND MOV NOT IN(0)                  
                                    )
                )
    LOOP
      MI_ERROR := MI_ERROR || CHR(13) || CHR(10) || RS.CODIGO;    
    END LOOP ERRADAS;
     
    IF MI_ERROR IS NOT NULL THEN  
      DECLARE
          MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR; 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'CUENTAS';
          MI_MSGERROR(1).VALOR := MI_ERROR;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_PASANDO_CUENTAS,
            UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
    END IF;
    
    GL_PASASALDO:=-1;
    BEGIN 
      BEGIN
        MI_CAMPOS:= ' SALDO0  = 0 , ' ||
                    ' NETO0   = 0 , ' ||
                    ' DEBITO0 = 0 ,  ' ||
                    ' CREDITO0= 0';
        MI_CONDICION :=    ' COMPANIA =''' || UN_COMPANIA || '''' ||
                       ' AND ANO =' || UN_ANO_DESTINO ||
                       ' AND SUBSTR(CODIGO,1,1) IN (''1'',''2'',''3'',''8'',''9'')';

        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE', 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);  
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDOSINICIALES', 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => 'SALDOINICIAL=0',
                                            UN_CONDICION => MI_CONDICION); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      BEGIN
        --Actualiza los campos de saldo0, debito0, credito0, y Neto0; en base a saldos 13 del anterior año
        MI_MERGEUSING := 'SELECT 
                            SAL.COMPANIA,
                            ' || UN_ANO_DESTINO || ' ANO,
                            SAL.CODIGO,
                            DECODE(PLAN.MAN_CEN_CTO, 0,''' || PCK_DATOS.FC_CONS_CENTRO     || ''',NVL(cc.EQUIVALENTE_SIG_VIG, SAL.CENTRO_COSTO)) CENTRO_COSTO,
                            DECODE(PLAN.MAN_AUX_TER, 0,''' || PCK_DATOS.FC_CONS_TERCERO    || ''',SAL.TERCERO)      TERCERO,
                            DECODE(PLAN.MAN_AUX_TER, 0,''' || PCK_DATOS.FC_CONS_SUCURSAL   || ''',SAL.SUCURSAL)     SUCURSAL,
                            DECODE(PLAN.MAN_AUX_GEN, 0,''' || PCK_DATOS.FC_CONS_AUXILIAR   || ''',NVL(AX.EQUIVALENTE_SIG_VIG,SAL.AUXILIAR)) AUXILIAR,
                            DECODE(PLAN.MAN_AUX_REF, 0,''' || PCK_DATOS.FC_CONS_REFERENCIA || ''',NVL(REF.EQUIVALENTE_SIG_VIG,SAL.REFERENCIA))   REFERENCIA,
                            DECODE(PLAN.MAN_AUX_FUE, 0,''' || PCK_DATOS.FC_CONS_FUENTE     || ''',NVL(FR.EQUIVALENTE_SIG_VIG,SAL.FUENTE_RECURSO)) FUENTE_RECURSO,
                            SUM(SAL.SALDO13) SALDO0
                          FROM SALDO_AUX_CONTABLE SAL INNER JOIN PLAN_CONTABLE PLAN
                            ON SAL.COMPANIA =PLAN.COMPANIA
                           AND SAL.CODIGO   =PLAN.CODIGO
                           INNER JOIN CENTRO_COSTO CC ON SAL.COMPANIA = CC.COMPANIA 
                            AND SAL.ANO = CC.ANO
                            AND SAL.CENTRO_COSTO = cc.codigo
                            INNER JOIN FUENTE_RECURSOS FR ON SAL.COMPANIA = FR.COMPANIA 
                            AND SAL.ANO = FR.ANO
                            AND SAL.FUENTE_RECURSO = FR.codigo
                            INNER JOIN AUXILIAR AX ON SAL.COMPANIA = AX.COMPANIA 
                            AND SAL.ANO = AX.ANO
                            AND SAL.AUXILIAR = AX.codigo
                            INNER JOIN REFERENCIA REF ON SAL.COMPANIA = REF.COMPANIA 
                            AND SAL.ANO = REF.ANO
                            AND SAL.REFERENCIA = REF.codigo
                          WHERE SAL.COMPANIA       = '''||UN_COMPANIA||'''
                            AND SAL.ANO            = '  ||MI_ANO_INICIAL || '
                            AND PLAN.ANO           = '  ||UN_ANO_DESTINO ||'
                            AND SUBSTR(SAL.CODIGO,1,1) IN (''1'',''2'',''3'',''8'',''9'')
                            AND SAL.SALDO13     <>0
                          GROUP BY   SAL.COMPANIA,
                            ' || UN_ANO_DESTINO || ',
                            SAL.CODIGO,
                            DECODE(PLAN.MAN_CEN_CTO, 0,''' || PCK_DATOS.FC_CONS_CENTRO     || ''',NVL(cc.EQUIVALENTE_SIG_VIG, SAL.CENTRO_COSTO)) ,
                            DECODE(PLAN.MAN_AUX_TER, 0,''' || PCK_DATOS.FC_CONS_TERCERO    || ''',SAL.TERCERO)      ,
                            DECODE(PLAN.MAN_AUX_TER, 0,''' || PCK_DATOS.FC_CONS_SUCURSAL   || ''',SAL.SUCURSAL)     ,
                            DECODE(PLAN.MAN_AUX_GEN, 0,''' || PCK_DATOS.FC_CONS_AUXILIAR   || ''',NVL(AX.EQUIVALENTE_SIG_VIG,SAL.AUXILIAR))      ,
                            DECODE(PLAN.MAN_AUX_REF, 0,''' || PCK_DATOS.FC_CONS_REFERENCIA || ''',NVL(REF.EQUIVALENTE_SIG_VIG,SAL.REFERENCIA))    ,
                            DECODE(PLAN.MAN_AUX_FUE, 0,''' || PCK_DATOS.FC_CONS_FUENTE     || ''',NVL(FR.EQUIVALENTE_SIG_VIG,SAL.FUENTE_RECURSO))  ';

        MI_MERGEENLACE := ' 	TABLA.COMPANIA       = VISTA.COMPANIA
                            AND TABLA.ANO            = VISTA.ANO
                            AND TABLA.CODIGO         = VISTA.CODIGO
                            AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                            AND TABLA.TERCERO        = VISTA.TERCERO
                            AND TABLA.SUCURSAL       = VISTA.SUCURSAL
                            AND TABLA.AUXILIAR       = VISTA.AUXILIAR
                            AND TABLA.REFERENCIA     = VISTA.REFERENCIA
                            AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO';	

        MI_MERGEEXISTE := 'UPDATE SET  	TABLA.SALDOINICIAL = VISTA.SALDO0
                           WHERE  TABLA.COMPANIA          ='''||UN_COMPANIA||'''
                              AND TABLA.ANO                 = '||UN_ANO_DESTINO||
                            ' AND SUBSTR(TABLA.CODIGO,1,1) IN (''1'',''2'',''3'',''8'',''9'')';		
        MI_MERGENOEXISTE:= ' INSERT (COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO, SALDOINICIAL)
                             VALUES (VISTA.COMPANIA, VISTA.ANO, VISTA.CODIGO, VISTA.CENTRO_COSTO, VISTA.TERCERO, 
                                    VISTA.SUCURSAL, VISTA.AUXILIAR, VISTA.REFERENCIA, VISTA.FUENTE_RECURSO, VISTA.SALDO0)';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => 'SALDOSINICIALES' , 
                                                UN_ACCION => 'IM' , 
                                                UN_MERGEUSING => MI_MERGEUSING , 
                                                UN_MERGEENLACE => MI_MERGEENLACE , 
                                                UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                UN_MERGENOEXIS => MI_MERGENOEXISTE);	                                       

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        GL_PASASALDO:=0;
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_2
      );
    END;   
    --CC_2235_GROJAS: Actualiza el campo de ajuste0 en base a ajuste12 del anterior año
    BEGIN
        MI_MERGEUSING := 'SELECT COMPANIA, CODIGO, AJUSTE12
                            FROM PLAN_CONTABLE
                           WHERE COMPANIA = '''||UN_COMPANIA||'''
                             AND ANO = '||MI_ANO_INICIAL||'
                             AND CLASECUENTA = ''B''';

        MI_MERGEENLACE := 'TABLA.CODIGO = VISTA.CODIGO
                           AND TABLA.COMPANIA = '''||UN_COMPANIA||'''
                           AND TABLA.ANO = '||UN_ANO_DESTINO||'
                           AND TABLA.CLASECUENTA = ''B''';

        MI_MERGEEXISTE := 'UPDATE SET TABLA.AJUSTE0 = VISTA.AJUSTE12';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                UN_TABLA       => 'PLAN_CONTABLE',
                                UN_ACCION      => 'MM',
                                UN_MERGEUSING  => MI_MERGEUSING,
                                UN_MERGEENLACE => MI_MERGEENLACE,
                                UN_MERGEEXISTE => MI_MERGEEXISTE
                            );
    EXCEPTION 
        WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            GL_PASASALDO := 0;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;
    PCK_CONTABILIDAD.PR_SUBIR_SALDOS_INICIALES(UN_COMPANIA      => UN_COMPANIA, 
                                               UN_ANIO          => UN_ANO_DESTINO,
                                               UN_MODIFICADOR   => 'PASASALDO',
                                               UN_DESDEPASARSAL => -1);
    GL_PASASALDO:=0;
END PR_PASAR_SALDOS_ANO_SIGUIENTE;

FUNCTION FC_CORRECION_CHEQUERA 
/*    
    NAME              : FC_CORRECION_CHEQUERA --> EN ACCESS evento boton correcion de chequera
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    SOURCE MODULE     : SysmanCT2016.02.06
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se agregó validación del ultimo cheque del comprobante 
    @NAME:  corregirChequera
    @METHOD:  GET
*/    

  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA 
  ) RETURN VARCHAR2 AS 

  MI_TRANSACCION    PCK_SUBTIPOS.TI_RTA_ACME;
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM+13;
  MI_RESPUESTA      VARCHAR2(3200 CHAR):='OK';
  MI_TAMANO         PCK_SUBTIPOS.TI_ENTERO_LARGO :=0; 
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  MI_NUMCHEQUERAACTUAL  PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_NUMEROCHEQUE   PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_NUMEROACTUAL   PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_NUMEROINICIAL  PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_NUMEROFINAL    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_ANULADA        PCK_SUBTIPOS.TI_LOGICO;



  BEGIN
      BEGIN
      MI_STRSQL := 'SELECT MAX(NUMEROCHEQUE)NUMEROCHEQUE 
                FROM COMPROBANTE_CNTBANCOS
                WHERE COMPANIA = '''||UN_COMPANIA||'''
                  AND ANO = '||UN_ANO||'
                  AND CUENTA = '''||UN_CUENTA||'''
                  AND TIPO = ''EGR''';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMEROCHEQUE;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NUMEROCHEQUE := NULL;
      END;
      BEGIN
      MI_STRSQL := 'SELECT MIN(NUMCHEQUERA)NUMCHEQUERA,NUMACTUAL,NUMINICIAL,NUMFINAL ,ANULADA 
                FROM CHEQUERA 
                WHERE COMPANIA = '''||UN_COMPANIA||'''
                  AND ANO = '||UN_ANO||'
                  AND CUENTA = '''||UN_CUENTA||'''
                  AND ANULADA  IN(0)
                  AND   DISPONIBLES  > 0  
                  AND ROWNUM = 1
                GROUP BY NUMACTUAL,NUMINICIAL,NUMFINAL,ANULADA';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMCHEQUERAACTUAL , MI_NUMEROACTUAL,MI_NUMEROINICIAL,MI_NUMEROFINAL,MI_ANULADA;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NUMEROACTUAL := NULL;
        MI_NUMCHEQUERAACTUAL := NULL;
      END;
      BEGIN 
        BEGIN 
          IF MI_NUMEROCHEQUE IS NOT NULL 
          AND MI_NUMEROACTUAL IS NOT NULL 
          AND MI_NUMEROCHEQUE <> MI_NUMEROACTUAL
            THEN 
              IF MI_NUMEROCHEQUE   >= MI_NUMEROINICIAL 
              AND MI_NUMEROCHEQUE  <= MI_NUMEROFINAL
                THEN 
                MI_TRANSACCION := 
                  PCK_DATOS.FC_ACME(UN_TABLA => 'CHEQUERA' ,
                                    UN_ACCION => 'M',
                                    UN_CAMPOS => 'NUMACTUAL  = '||MI_NUMEROCHEQUE ,
                                    UN_CONDICION => ' COMPANIA      = '''|| UN_COMPANIA ||'''   
                                      AND ANO       =   '|| UN_ANO      ||'
                                      AND CUENTA    = '''|| UN_CUENTA   ||'''
                                      AND ANULADA IN(0)');
            END IF;                           
          END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
       PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_CHEQUERA
         ); 
      END;   
      MI_TRANSACCION := 
          PCK_DATOS.FC_ACME(UN_TABLA => 'CHEQUERA' ,
                            UN_ACCION => 'M',
                            UN_CAMPOS => 'NUMACTUAL  = NUMFINAL',
                            UN_CONDICION => 'COMPANIA      = '''|| UN_COMPANIA ||'''   
                                              AND ANO       =   '|| UN_ANO      ||'
                                              AND CUENTA    = '''|| UN_CUENTA   ||'''
                                              AND NUMACTUAL < 0');

      MI_TRANSACCION := 
          PCK_DATOS.FC_ACME(UN_TABLA => ' CHEQUERA',
                            UN_ACCION=> 'M',
                            UN_CAMPOS=>'DISPONIBLES  = (NUMFINAL - NUMACTUAL)',
                            UN_CONDICION=> ' COMPANIA      = '''|| UN_COMPANIA ||'''   
                                              AND ANO       =   '|| UN_ANO      ||'
                                              AND CUENTA    = '''|| UN_CUENTA   ||'''');

      MI_TRANSACCION := 
          PCK_DATOS.FC_ACME(UN_TABLA => 'CHEQUERA',
                            UN_ACCION => 'M',
                            UN_CAMPOS => 'ANULADA  = -1',
                            UN_CONDICION => 'COMPANIA       = '''|| UN_COMPANIA ||'''   
                                              AND ANO        =   '|| UN_ANO      ||'
                                              AND CUENTA     = '''|| UN_CUENTA   ||'''
                                              AND DISPONIBLES IN(0)');

         MI_TRANSACCION := 
                          PCK_DATOS.FC_ACME(UN_TABLA => 'CHEQUERA' ,
                                            UN_ACCION=> 'M',
                                            UN_CAMPOS=> ' ANULADA  = 0',
                                            UN_CONDICION => 'COMPANIA   = '''|| UN_COMPANIA ||'''   
                                                                AND ANO         =   '|| UN_ANO      ||'
                                                                AND CUENTA      = '''|| UN_CUENTA   ||'''
                                                                AND NUMCHEQUERA =   '|| MI_NUMCHEQUERAACTUAL);


        IF MI_NUMCHEQUERAACTUAL IS NOT NULL 
        THEN
               MI_TRANSACCION := 
                          PCK_DATOS.FC_ACME(UN_TABLA => 'CHEQUERA' ,
                                            UN_ACCION => 'M',
                                            UN_CAMPOS => 'NUMACTUAL  = NUMINICIAL,DISPONIBLES = (NUMFINAL - NUMINICIAL)',
                                            UN_CONDICION => ' COMPANIA = '''|| UN_COMPANIA ||'''   
                                                              AND ANO         =   '|| UN_ANO      ||'
                                                              AND CUENTA      = '''|| UN_CUENTA   ||'''
                                                              AND NVL(ANULADA,0) NOT IN(0)');



     END IF;



     RETURN MI_RESPUESTA;
     EXCEPTION  WHEN OTHERS THEN
              PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar la reversación.';
              MI_RESPUESTA := SQLERRM;
              PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_REVERSADOCUMENTOAS','',SQLERRM );
              RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
              RETURN MI_RESPUESTA;
END FC_CORRECION_CHEQUERA;

FUNCTION FC_VERIFICAPERIODOCONCILIA
    /*
    NAME              : FC_VERIFICAPERIODOCONCILIA  --> EN ACCESS VERIFICAPERIODOCONCILIA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 08/03/2016
    TIME              : 03:00 PM
    SOURCE MODULE     : SysmanCT2016.02.06.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Devuelve True si un determinado periodo está activo y False si está cerrado
    MODIFICATIONS     : 
    @NAME:  revisarConciliacionPeriodo
    @METHOD:  GET
    */
   (UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES
  )
RETURN NUMBER AS 
    MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM;
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
    MI_ESTADO       VARCHAR2(1);
BEGIN
   MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES( UN_COMPANIA => UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MES     => UN_MES, 
                                                       UN_MODULO  => 1, 
                                                       UN_PROCESO => 2);    
    IF NVL(MI_ESTADO,'A') = 'A' THEN
        RETURN -1;
    ELSE
        RETURN 0;
    END IF;

    EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG:= 'Error al verificar el periodo.';
        PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_VERIFICAPERIODOCONCILIA','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END;

FUNCTION FC_SALDOINICIALCAJA
/*
    NAME              : FC_SALDO_INICIAL_CAJA_ID 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
    DATE MIGRADOR     : 08/03/2015
    TIME              : 11:00 AM
    SOURCE MODULE     : CONTABILIDAD
    DESCRIPTION       : Halla el saldo de la cuenta dada.
    @NAME:  consultarSaldosCaja
    @METHOD:  GET
  */
  (
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHA      IN DATE,
  UN_CUENTA     IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_NATURALEZA IN PCK_SUBTIPOS.TI_NATURALEZACONTA
  )
  RETURN NUMBER
  AS
    MI_SALDO          PCK_SUBTIPOS.TI_DOBLE;
    MI_PRIMERA_FECHA  DATE;
    MI_MES_ANTERIOR   PCK_SUBTIPOS.TI_MES;
    MI_ANO            PCK_SUBTIPOS.TI_ANIO;
    MI_MES            PCK_SUBTIPOS.TI_MES;
    MI_DIA            PCK_SUBTIPOS.TI_DIA;
    MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + + 1;
    MI_SUMCREDITO     PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMDEBITO      PCK_SUBTIPOS.TI_DOBLE;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;

  BEGIN
    MI_SALDO           := 0;
    MI_SUMCREDITO      := 0;
    MI_SUMDEBITO       := 0;
    MI_ANO             := TO_CHAR(UN_FECHA,'YYYY');
    MI_MES             := TO_CHAR(UN_FECHA,'MM');
    MI_DIA             := TO_CHAR(UN_FECHA,'DD');
    MI_PRIMERA_FECHA   := TO_DATE('01/' || MI_MES || '/' || MI_ANO,'DD/MM/YYYY'); 
    MI_MES_ANTERIOR    := MI_MES - 1;

    BEGIN
      MI_STRSQL := 'SELECT SALDO' || MI_MES_ANTERIOR || ' SALDO 
      FROM   V_PLAN_CONTABLE
      WHERE  COMPANIA = ''' || UN_COMPANIA || ''' 
        AND  ANO      = ' || MI_ANO || '
        AND  ID       = ''' || UN_CUENTA || ''''; 
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_SALDO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_SALDO := 0;
    END;

    IF MI_DIA > 1 THEN
      BEGIN
        SELECT 
              SUM(VALOR_DEBITO) SUMADEBITO, 
              SUM(VALOR_CREDITO) SUMACREDITO
        INTO  MI_SUMDEBITO,
              MI_SUMCREDITO
        FROM  DETALLE_COMPROBANTE_CNT
        WHERE COMPANIA = UN_COMPANIA 
          AND ANO      = MI_ANO 
          AND CUENTA       = UN_CUENTA   
          AND FECHA BETWEEN MI_PRIMERA_FECHA AND (UN_FECHA - 1);    

        MI_SALDO      := NVL(MI_SALDO,0);  
        MI_SUMDEBITO  := NVL(MI_SUMDEBITO,0);  
        MI_SUMCREDITO := NVL(MI_SUMCREDITO,0);    

        IF UN_NATURALEZA = 'C' THEN
          MI_SALDO := MI_SALDO + (MI_SUMCREDITO - MI_SUMDEBITO);
        ELSE
          MI_SALDO := MI_SALDO + (MI_SUMDEBITO - MI_SUMCREDITO);
        END IF;
      END;
    END IF;

  RETURN MI_SALDO;


  EXCEPTION WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG := 'Error al calcular saldo';
  PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  '','',SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

END FC_SALDOINICIALCAJA;


FUNCTION FC_VERIFICAINCONSISTENCIAS
  /*
  NAME              : FC_VERIFICAINCONSISTENCIAS En Access --> VerificarInconsistencias
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 05/04/2017
  TIME              : 17:00 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : CONTABILIDAD
  DESCRIPTION       : Función que verifica las incosistencias y retorna un archivo plano
  @NAME:  verificarInconsistencias
  @METHOD:  POST
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ENTERO

  )
  RETURN CLOB AS
    MI_ENCABEZADO   CLOB;
    MI_SALIDA       CLOB;
    MI_CUENTAS      CLOB;
    MI_CENTROCOSTO  CLOB;
    MI_AUXILIAR     CLOB;
    MI_CONTADOR     NUMBER;
    MI_RS           SYS_REFCURSOR;
BEGIN    
  MI_ENCABEZADO := 'Relación de Inconsistencias Configuración de Saldos Iniciales con Indicadores de Movimiento. Año: '||UN_ANO||'';
  MI_CONTADOR := 0;

  --CUENTAS CONFIGURADAS A CUENTAS SIN INDICADOR DE MOVIMIENTO

  <<CUENTAS_SIN_MOVIMIENTO>>            
  FOR MI_RS IN(SELECT CODIGO, SALDOINICIAL, AUXILIAR, CENTRO_COSTO 
              FROM SALDOSINICIALES
              WHERE COMPANIA = UN_COMPANIA
                AND ANO      = UN_ANO
                AND CODIGO IN ( SELECT PLAN_CONTABLE.CODIGO 
                                FROM PLAN_CONTABLE
                                WHERE COMPANIA   = UN_COMPANIA
                                  AND ANO        = UN_ANO
                                  AND ABS(MOVIMIENTO) + 
                                      ABS(MAN_CEN_CTO) + 
                                      ABS(MAN_AUX_TER) + 
                                      ABS(MAN_AUX_GEN) + 
                                      ABS(MAN_AUX_REF) + 
                                      ABS(MAN_AUX_FUE) 
                                      IN (0) ))
  LOOP 
  MI_CONTADOR := MI_CONTADOR+1;
    IF MI_CONTADOR = 1 THEN
        MI_CUENTAS := '******  CUENTAS CONFIGURADAS EN PLAN CONTABLE SIN INDICADOR DE MOVIMIENTO  *******'|| CHR(10)
              || 'CONSECUTIVO' ||CHR(9)||'CUENTA'||CHR(9)||'VALOR'||CHR(9)||'AUXILIAR'||CHR(9)||'CENTRO_COSTO';
    END IF;
  MI_CUENTAS := MI_CUENTAS ||CHR(10)
                ||CHR(9)||MI_CONTADOR||CHR(9)||MI_RS.CODIGO||CHR(9)||MI_RS.SALDOINICIAL||CHR(9)||NVL(MI_RS.AUXILIAR,'No Presenta')||CHR(9)||NVL(MI_RS.CENTRO_COSTO,'No Presenta');

  END LOOP CUENTAS_SIN_MOVIMIENTO;

  MI_SALIDA := MI_ENCABEZADO||CHR(10)
              ||MI_CUENTAS||CHR(10)
              ||'---------------------------------------------------------------';

  --CUENTAS CONFIGURADAS A CENTRO COSTO SIN INDICADOR DE MOVIMIENTO       

  <<CUENTAS_CENTROCOSTO>>            
  FOR MI_RS IN(SELECT CODIGO, SALDOINICIAL, AUXILIAR, CENTRO_COSTO 
               FROM SALDOSINICIALES 
               WHERE COMPANIA     = UN_COMPANIA
                 AND ANO          = UN_ANO
                 AND CENTRO_COSTO IS NOT NULL
                 AND CENTRO_COSTO IN (SELECT CODIGO 
                                      FROM CENTRO_COSTO
                                      WHERE COMPANIA   = UN_COMPANIA
                                        AND ANO        = UN_ANO
                                        AND MOVIMIENTO IN (0)))
  LOOP 
  MI_CONTADOR := 0;
  MI_CONTADOR := MI_CONTADOR+1;

  IF MI_CONTADOR = 1 THEN
        MI_CUENTAS := '******  CUENTAS CONFIGURADAS A CENTRO COSTO SIN INDICADOR DE MOVIMIENTO  *******'|| CHR(10)
              || 'CONSECUTIVO' ||CHR(9)||'CUENTA'||CHR(9)||'VALOR'||CHR(9)||'AUXILIAR'||CHR(9)||'CENTRO_COSTO';
    END IF;

  MI_CENTROCOSTO := MI_CUENTAS ||CHR(10)
                ||CHR(9)||MI_CONTADOR||CHR(9)||MI_RS.CODIGO||CHR(9)||MI_RS.SALDOINICIAL||CHR(9)||NVL(MI_RS.AUXILIAR,'No Presenta')||CHR(9)||NVL(MI_RS.CENTRO_COSTO,'No Presenta');

  END LOOP CUENTAS_CENTROCOSTO;

  MI_SALIDA := MI_SALIDA||CHR(10)
               ||MI_CENTROCOSTO||CHR(10)
               ||'---------------------------------------------------------------';


  --CUENTAS CONFIGURADAS A AUXILIAR SIN INDICADOR DE MOVIMIENTO

  <<CUENTAS_AUXILIAR>>            
  FOR MI_RS IN(SELECT CODIGO, SALDOINICIAL, AUXILIAR, CENTRO_COSTO 
               FROM SALDOSINICIALES
               WHERE COMPANIA  = UN_COMPANIA
                 AND ANO = UN_ANO
                 AND AUXILIAR IS NOT NULL
                 AND AUXILIAR IN ( SELECT CODIGO 
                                   FROM AUXILIAR
                                   WHERE COMPANIA = UN_COMPANIA
                                     AND ANO = UN_ANO
                                     AND MOVIMIENTO IN (0)) )
  LOOP 
  MI_CONTADOR := 0;
  MI_CONTADOR := MI_CONTADOR+1;

  IF MI_CONTADOR = 1 THEN
        MI_CUENTAS := '******  CUENTAS CONFIGURADAS A AUXILIAR SIN INDICADOR DE MOVIMIENTO  *******'|| CHR(10)
              || 'CONSECUTIVO' ||CHR(9)||'CUENTA'||CHR(9)||'VALOR'||CHR(9)||'AUXILIAR'||CHR(9)||'CENTRO_COSTO';
    END IF;

  MI_AUXILIAR := MI_CUENTAS ||CHR(10)
                ||CHR(9)||MI_CONTADOR||CHR(9)||MI_RS.CODIGO||CHR(9)||MI_RS.SALDOINICIAL||CHR(9)||NVL(MI_RS.AUXILIAR,'No Presenta')||CHR(9)||NVL(MI_RS.CENTRO_COSTO,'No Presenta');

  END LOOP CUENTAS_AUXILIAR;

  MI_SALIDA := MI_SALIDA||CHR(10)
               ||MI_AUXILIAR||CHR(10)
               ||'********  Fin de Archivo*******'||CHR(10)
               ||'Fecha Elaborado: '||TO_CHAR(SYSDATE,'DD/MM/YYYY');


  IF MI_CONTADOR = 0 THEN
    MI_SALIDA := '0';
    RETURN MI_SALIDA;
  END IF;

  RETURN MI_SALIDA;
 END FC_VERIFICAINCONSISTENCIAS;

 FUNCTION FC_VALIDARCUENTAUTILIZAR
   /*
  NAME              : FC_VALIDARCUENTAUTILIZAR 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ
  DATE MIGRADOR     : 12/05/2017
  TIME              : 12:00 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : CONTABILIDAD
  DESCRIPTION       : Permite validad si la cuenta se puede mover de acuerdo a los indicadores de movimiento
                      con el parametro UN_VALIDABLOQUEADO => se valida si tambein se debe controlar el bloqueo de la cuenta
  @NAME:  validarCuentaaUtilizar
  */
( 
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
  UN_CUENTA          IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_VALIDABLOQUEADO IN PCK_SUBTIPOS.TI_LOGICO
  )
RETURN VARCHAR2
  AS
    MI_X             NUMBER:= 0;
    MI_NATURALEZA    VARCHAR2(1 CHAR);
    MI_BLOQUEACUENTA VARCHAR2(2 CHAR);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    BEGIN 
      BEGIN
        SELECT  MAN_CEN_CTO 
                + MAN_AUX_TER 
                + MAN_AUX_GEN 
                + MAN_AUX_FUE 
                + MAN_AUX_REF 
                + MOVIMIENTO
               ,NATURALEZA,
               BLOQUEACUENTA
          INTO  MI_X
               ,MI_NATURALEZA
               ,MI_BLOQUEACUENTA
          FROM PLAN_CONTABLE   
         WHERE COMPANIA = UN_COMPANIA
           AND ANO      = UN_ANO
           AND CODIGO   = UN_CUENTA; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
          RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          MI_MSGERROR(1).CLAVE := 'CUENTA';
          MI_MSGERROR(1).VALOR := UN_CUENTA;
          MI_MSGERROR(2).CLAVE := 'ANIO';
          MI_MSGERROR(2).VALOR := UN_ANO;
          MI_MSGERROR(3).CLAVE := 'COMPANIA';
          MI_MSGERROR(3).VALOR := UN_COMPANIA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD     => SQLCODE
                     ,UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_NO_EXISTECUENTA
                     ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'
                     ,UN_REEMPLAZOS  => MI_MSGERROR
          );
    END;
    --VALIDA QUE LA CUENTA SEA DE MOVIMIENTO O CON AUXILIAR
    BEGIN 
      IF MI_X=0 THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      MI_MSGERROR(1).CLAVE := 'CUENTA';
      MI_MSGERROR(1).VALOR := UN_CUENTA;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_9
                 ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'
                 ,UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    /*
    *VALIDA QUE LA CUENTA NO ESTE BLOQUEADA 
    *PARA EFECTOS DE PASAR SALDOS DE UN AÑO A OTRO TAMPOCO SE DEBE VALIDAR SI ESTA BLOQUEADA
    */
    IF UN_VALIDABLOQUEADO<>0 AND GL_PASASALDO=0 THEN
      BEGIN 
        IF MI_BLOQUEACUENTA='SI' THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
        END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'CUENTA';
        MI_MSGERROR(1).VALOR := UN_CUENTA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_CUENTABLOQUEADA
                   ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'
                   ,UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
  RETURN MI_NATURALEZA;
END FC_VALIDARCUENTAUTILIZAR;

PROCEDURE PR_VALIDARFECHAHEADERCNT
   /*
  NAME              : PR_VALIDARFECHAHEADERCNT 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ
  DATE MIGRADOR     : 12/05/2017
  TIME              : 12:00 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : CONTABILIDAD
  DESCRIPTION       : Permite validar que la fecha de un detalle sea igual a lal header del comprobante contable
  @NAME:  validarCuentaaUtilizar
  */
( 
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO            IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO          IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_FECHA           IN DATE
  )
  AS
    MI_FECHA      DATE;    
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    BEGIN 
      BEGIN
        SELECT  FECHA
          INTO  MI_FECHA
          FROM  COMPROBANTE_CNT   
         WHERE COMPANIA = UN_COMPANIA
           AND ANO      = UN_ANO
           AND TIPO     = UN_TIPO
           AND NUMERO   = UN_NUMERO
           AND FECHA    = UN_FECHA; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
          RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          MI_MSGERROR(1).CLAVE := 'TIPO';
          MI_MSGERROR(1).VALOR := UN_TIPO;
          MI_MSGERROR(2).CLAVE := 'NUMERO';
          MI_MSGERROR(2).VALOR := UN_NUMERO;          
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD     => SQLCODE
                     ,UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_FECHADIFERENTEHEAD
                     ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'
                     ,UN_REEMPLAZOS  => MI_MSGERROR
          );
    END;   
END PR_VALIDARFECHAHEADERCNT;

PROCEDURE PR_CAMBIOSDENITATERCERO_ANTERI 
 /*
    NAME              : PR_CAMBIOSDENITATERCERO
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODR�?GUEZ
    DATE MIGRADOR     : 27/07/2016
    TIME              : 08:00 AM     
    MODIFIER          : DIEGO ALFREDO SUESCA
    DATE MODIFIED     : 23/05/2017
    MODIFIER          : JUAN CAMILO RODRIGUEZ DIAZ
    DESCRIPTION       : ACTUALIZA LOS CAMPOS (COMPAÑIA,NIT,SUCURSAL) DE LA TABLA TERCERO Y DE LAS TABLAS QUE TENGAN RELACIÓN CON ESTA TABLA.
    MODIFICATIONS     : JCRODRIGUEZ => CORRECCIÓN SEGÚN EST�?NDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
    Las sentencias de este procedimiento se generaron con el procedimiento PCK_DATOS.PR_CAMBIALLAVE ubicado en SYSMANIRISNUEVO
    @NAME:  cambiarNitTerceros
    @METHOD:  POST
  */
  ( 
    UN_NUE_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUE_NIT        IN PCK_SUBTIPOS.TI_TERCERO , 
    UN_NUE_SUCURSAL   IN PCK_SUBTIPOS.TI_SUCURSAL ,
    UN_ANT_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_ANT_NIT        IN PCK_SUBTIPOS.TI_TERCERO , 
    UN_ANT_SUCURSAL   IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )  
  AS

  MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  MI_PCKDATOS     PCK_SUBTIPOS.TI_RTA_ACME;
  MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSULTA     PCK_SUBTIPOS.TI_CONSULTA;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_PARAMETROS   PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CAMPOS;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_EXISTE       VARCHAR2(10 CHAR);
  MI_TER          PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS1 VARCHAR2(32000):='';

BEGIN

  PCK_GENERALES.CAMBIONIT := 1;

 --SE AGREGA VALIDACION PARA CUADNO EL NIT YA EXISTA

  BEGIN
  MI_STRSQL := 'SELECT ''X'' EXISTE    
                FROM TERCERO
                WHERE COMPANIA = '''|| UN_NUE_COMPANIA || '''
                  AND NIT      = '''|| UN_NUE_NIT || ''' 
                  AND SUCURSAL = '''|| UN_NUE_SUCURSAL || '''';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;

     EXCEPTION WHEN NO_DATA_FOUND THEN 

MI_TABLA:='TERCERO';
        MI_CAMPOS:= 'COMPANIA,'||
                      'NIT,'||
                      'SUCURSAL,'||
                      'NIT_CEDULA,'||
                      'PROPIETARIO,'||
                      'PAIS,'||
                      'DEPARTAMENTO,'||
                      'CIUDAD,'||
                      'DIRECCION,'||
                      'REGIMEN,'||
                      'AUTORETENEDOR,'||
                      'AUTORET_IVA,'||
                      'AUTORET_ICA,'||
                      'AUTORET_TBRE,'||
                      'AUTORET_PRO,'||
                      'KCONSTRUCTOR,'||
                      'KCONSULTORING,'||
                      'KCONSULTOR,'||
                      'KPROVEEDOR,'||
                      'TELEFONOS,'||
                      'FAX,'||
                      'CONTACTO,'||
                      'CARGO_CONTACTO,'||
                      'CLASE,'||
                      'TIPO_ASOCIADO,'||
                      'ZONA,'||
                      'MAX_CREDITO,'||
                      'PRMD_CARTERA,'||
                      'DIAS_PRMD_CARTERA,'||
                      'VLRMAXCARTERA,'||
                      'VENDEDOR,'||
                      'SUCURSAL_VEND,'||
                      'FORMADEPAGO,'||
                      'FECHAULTIMAFACTURA,'||
                      'VALORULTIMAFACTURA,'||
                      'CODIGOEQUIVALENTE,'||
                      'PORCDESCUENTO,'||
                      'CLASEENTIDADOFICIAL,'||
                      'CODIGOICA,'||
                      'CALIFICACIONCC,'||
                      'FECHAINSCRIPCIONCC,'||
                      'NOMBREREPLEGAL,'||
                      'NOMBREVENDEDOR,'||
                      'DIRECCIONEMAIL,'||
                      'DIRECCIONWEB,'||
                      'NOCAMARACOMERCIO,'||
                      'FECHACAMARACOMERCIO,'||
                      'CEDULAREPRESENTANTE,'||
                      'EXPEDIDAENREPRESENTANTE,'||
                      'CARGOREPRESENTANTE,'||
                      'TARJETAPROFESIONAL,'||
                      'REGISTROMERCANTIL,'||
                      'CIUDADDEREGISTRO,'||
                      'ACTIVIDAD,'||
                      'NATURALEZA,'||
                      'TIPOID,'||
                      'TIPOSOCIEDAD,'||
                      'NITAPODERADO,'||
                      'SUCURSALAPODERADO,'||
                      'NOMBRE1,'||
                      'NOMBRE2,'||
                      'APELLIDO1,'||
                      'APELLIDO2,'||
                      'EXPEDIDACEDULA,'||
                      'ANTECEDENTES,'||
                      'TIPO_TERCERO,'||
                      'APLICADESCUENTO,'||
                      'CODIGOSCHIP,'||
                      'EMBARGO,'||
                      'PAGOELECTRONICO,'||
                      'LEY1450,'||
                      'IBC,'||
                      'APORTESVOLUNTARIOS,'||
                      'TIPO_CONTRATISTA,'||
                      'IND_INTERVENTOR,'||
                      'CODIGO_PROPONENTE,'||
                      'CAMARA_COMERCIO,'||
                      'ACTIVO,'||
                      'TIPO_EMBARGO,'||
                      'AFC,'||
                      'UNIDAD,'||
                      'GRADO,'||
                      'FUERZA,'||
                      'FECHA_EMBARGO,'||
                      'VALOR_EMBARGO,'||
                      'NRODOCUMENTO_EMBARGO,'||
                      'RETENEDORIVA,'||
                      'RETENEDORICA,'||
                      'CODIGORUP,'||
                      'DECLARARENTA,'||
                      'DEPENDIENTE,'||
                      'DEDUCIBLE_VIVIENDA,'||
                      'MEDPREPAGADA,'||
                      'CODIGOPOSTAL,'||
                      'INFORMACION_ADICIONAL,'||
                      'APORTESARL,'||
                      'NOAPORTASALUD,'||
                      'NOAPORTAPENSION,'||
                      'VINCULACION,'||
                      'ESTADO,'||
                      'CONDUCTOR,'||
                      'ORDEN,'||
                      'FECHANACIMIENTO,'||
                      'ASIGNACIOCOND,'||
                      'FECHAULTIMOEXAMEN,'||
                      'NROLICENCIA,'||
                      'CODIGO,'||
                      'TIPO_DE_SANGRE,'||
                      'CARGO,'||
                      'EXPERIENCIA,'||
                      'CATEGORIA_LICENCIA,'||
                      'DETERIORO,'||
                      'PROFESION,'||
                      'DATE_CREATED,'||
                      'CREATED_BY';

        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''','||
                     '''' || UN_NUE_NIT || ''','||
                     '''' || UN_NUE_SUCURSAL || ''','||
                    'NIT_CEDULA,'||
                    'PROPIETARIO,'||
                    'PAIS,'||
                    'DEPARTAMENTO,'||
                    'CIUDAD,'||
                    'DIRECCION,'||
                    'REGIMEN,'||
                    'AUTORETENEDOR,'||
                    'AUTORET_IVA,'||
                    'AUTORET_ICA,'||
                    'AUTORET_TBRE,'||
                    'AUTORET_PRO,'||
                    'KCONSTRUCTOR,'||
                    'KCONSULTORING,'||
                    'KCONSULTOR,'||
                    'KPROVEEDOR,'||
                    'TELEFONOS,'||
                    'FAX,'||
                    'CONTACTO,'||
                    'CARGO_CONTACTO,'||
                    'CLASE,'||
                    'TIPO_ASOCIADO,'||
                    'ZONA,'||
                    'MAX_CREDITO,'||
                    'PRMD_CARTERA,'||
                    'DIAS_PRMD_CARTERA,'||
                    'VLRMAXCARTERA,'||
                    'VENDEDOR,'||
                    'SUCURSAL_VEND,'||
                    'FORMADEPAGO,'||
                    'FECHAULTIMAFACTURA,'||
                    'VALORULTIMAFACTURA,'||
                    'CODIGOEQUIVALENTE,'||
                    'PORCDESCUENTO,'||
                    'CLASEENTIDADOFICIAL,'||
                    'CODIGOICA,'||
                    'CALIFICACIONCC,'||
                    'FECHAINSCRIPCIONCC,'||
                    'NOMBREREPLEGAL,'||
                    'NOMBREVENDEDOR,'||
                    'DIRECCIONEMAIL,'||
                    'DIRECCIONWEB,'||
                    'NOCAMARACOMERCIO,'||
                    'FECHACAMARACOMERCIO,'||
                    'CEDULAREPRESENTANTE,'||
                    'EXPEDIDAENREPRESENTANTE,'||
                    'CARGOREPRESENTANTE,'||
                    'TARJETAPROFESIONAL,'||
                    'REGISTROMERCANTIL,'||
                    'CIUDADDEREGISTRO,'||
                    'ACTIVIDAD,'||
                    'NATURALEZA,'||
                    'TIPOID,'||
                    'TIPOSOCIEDAD,'||
                    'NITAPODERADO,'||
                    'SUCURSALAPODERADO,'||
                    'NOMBRE1,'||
                    'NOMBRE2,'||
                    'APELLIDO1,'||
                    'APELLIDO2,'||
                    'EXPEDIDACEDULA,'||
                    'ANTECEDENTES,'||
                    'TIPO_TERCERO,'||
                    'APLICADESCUENTO,'||
                    'CODIGOSCHIP,'||
                    'EMBARGO,'||
                    'PAGOELECTRONICO,'||
                    'LEY1450,'||
                    'IBC,'||
                    'APORTESVOLUNTARIOS,'||
                    'TIPO_CONTRATISTA,'||
                    'IND_INTERVENTOR,'||
                    'CODIGO_PROPONENTE,'||
                    'CAMARA_COMERCIO,'||
                    'ACTIVO,'||
                    'TIPO_EMBARGO,'||
                    'AFC,'||
                    'UNIDAD,'||
                    'GRADO,'||
                    'FUERZA,'||
                    'FECHA_EMBARGO,'||
                    'VALOR_EMBARGO,'||
                    'NRODOCUMENTO_EMBARGO,'||
                    'RETENEDORIVA,'||
                    'RETENEDORICA,'||
                    'CODIGORUP,'||
                    'DECLARARENTA,'||
                    'DEPENDIENTE,'||
                    'DEDUCIBLE_VIVIENDA,'||
                    'MEDPREPAGADA,'||
                    'CODIGOPOSTAL,'||
                    'INFORMACION_ADICIONAL,'||
                    'APORTESARL,'||
                    'NOAPORTASALUD,'||
                    'NOAPORTAPENSION,'||
                    'VINCULACION,'||
                    'ESTADO,'||
                    'CONDUCTOR,'||
                    'ORDEN,'||
                    'FECHANACIMIENTO,'||
                    'ASIGNACIOCOND,'||
                    'FECHAULTIMOEXAMEN,'||
                    'NROLICENCIA,'||
                    'CODIGO,'||
                    'TIPO_DE_SANGRE,'||
                    'CARGO,'||
                    'EXPERIENCIA,'||
                    'CATEGORIA_LICENCIA,'||
                    'DETERIORO,'||
                    'PROFESION,'||
                    'SYSDATE,'||
                    ''''||UN_USUARIO||''''||
                    ' FROM TERCERO'||
                    ' WHERE COMPANIA  = ''' || UN_ANT_COMPANIA || ''''|| 
                    ' AND NIT         = ''' || UN_ANT_NIT || ''''|| 
                    ' AND SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
     BEGIN
       BEGIN
          MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                          UN_ACCION  => 'IS',
                                          UN_CAMPOS  => MI_CAMPOS,
                                          UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
       END; 

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
     END; 
 END; 

  BEGIN
      MI_TABLA:='ACCIDENTALIDAD';
      MI_VALORES:=' ACCIDENTALIDAD.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  ' ACCIDENTALIDAD.CONDUCTOR     = ''' || UN_NUE_NIT || ''','||
                  ' ACCIDENTALIDAD.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  ' ACCIDENTALIDAD.DATE_MODIFIED =SYSDATE,'||
                  ' ACCIDENTALIDAD.MODIFIED_BY   ='''||UN_USUARIO||'''';
      MI_CONDICION:=' ACCIDENTALIDAD.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ACCIDENTALIDAD.CONDUCTOR  = ''' || UN_ANT_NIT || ''''||
                    ' AND ACCIDENTALIDAD.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  
  --MI_PCKDATOS := PCK_DATOS.FC_ACME('ACTIVIDADESINSCRITOS','M','ACTIVIDADESINSCRITOS.COMPANIA = ''' || UN_NUE_COMPANIA || ''', ACTIVIDADESINSCRITOS.NUMERO_DCTO = ''' || UN_NUE_NIT || ''', ACTIVIDADESINSCRITOS.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'ACTIVIDADESINSCRITOS.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND ACTIVIDADESINSCRITOS.NUMERO_DCTO = ''' || UN_ANT_NIT || ''' AND ACTIVIDADESINSCRITOS.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');
  --MI_PCKDATOS := PCK_DATOS.FC_ACME('ACTIVIDADESPROGRAMADAS','M','ACTIVIDADESPROGRAMADAS.COMPANIA = ''' || UN_NUE_COMPANIA || ''', ACTIVIDADESPROGRAMADAS.NUMERODOCUMENTO = ''' || UN_NUE_NIT || ''', ACTIVIDADESPROGRAMADAS.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'ACTIVIDADESPROGRAMADAS.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND ACTIVIDADESPROGRAMADAS.NUMERODOCUMENTO = ''' || UN_ANT_NIT || ''' AND ACTIVIDADESPROGRAMADAS.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');
  --MI_PCKDATOS := PCK_DATOS.FC_ACME('APERTURA_INSCRITOS','M','APERTURA_INSCRITOS.COMPANIA = ''' || UN_NUE_COMPANIA || ''', APERTURA_INSCRITOS.NUMERO_DCTO = ''' || UN_NUE_NIT || ''', APERTURA_INSCRITOS.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'APERTURA_INSCRITOS.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND APERTURA_INSCRITOS.NUMERO_DCTO = ''' || UN_ANT_NIT || ''' AND APERTURA_INSCRITOS.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');

   BEGIN
      MI_TABLA:='APROPIACIONESINICIALES';
      MI_VALORES:=' APROPIACIONESINICIALES.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  ' APROPIACIONESINICIALES.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  ' APROPIACIONESINICIALES.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  ' APROPIACIONESINICIALES.DATE_MODIFIED  =SYSDATE,'||
                  ' APROPIACIONESINICIALES.MODIFIED_BY    ='''||UN_USUARIO||'''';

      MI_CONDICION:=' APROPIACIONESINICIALES.COMPANIA     = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND APROPIACIONESINICIALES.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND APROPIACIONESINICIALES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;   
  BEGIN
        MI_TABLA  :='ASEGURADORA';
        MI_CAMPOS:='COMPANIA,'||
                    'NITASEGURADORA,'||
                    'SUCURSAL,'||
                    'NOMBRE,'||
                    'SIRECI,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';

        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''' ,'||
                     '''' || UN_NUE_NIT || ''' ,'||
                     '''' || UN_NUE_SUCURSAL || ''','||
                    'NOMBRE,'||
                    'SIRECI,'||
                    'SYSDATE,'||
                    ''''||UN_USUARIO||''''|| 
                    ' FROM ASEGURADORA'||
                    ' WHERE ASEGURADORA.COMPANIA     = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ASEGURADORA.NITASEGURADORA = ''' || UN_ANT_NIT || ''''||
                    ' AND ASEGURADORA.SUCURSAL       = ''' || UN_ANT_SUCURSAL || '''';

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;


          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;   

     BEGIN
      MI_TABLA:='POLIZAS';
      MI_VALORES:='POLIZAS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'POLIZAS.ASEGURADORA   = ''' || UN_NUE_NIT || ''','||
                  'POLIZAS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'POLIZAS.DATE_MODIFIED =SYSDATE,'||
                  'POLIZAS.MODIFIED_BY   ='''||UN_USUARIO||'''';

      MI_CONDICION:='POLIZAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND POLIZAS.ASEGURADORA = ''' || UN_ANT_NIT || ''''||
                    ' AND POLIZAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;   




  BEGIN
        MI_TABLA  :='POLIZAS_ACTIVOS';
        MI_CAMPOS:='COMPANIA,'||
                    'ASEGURADORA,'||
                    'SUCURSAL,'||
                    'NUMERO_POLIZA,'||
                    'CONSECUTIVO,'||
                    'SEL_GRUPO,'||
                    'SEL_ELEMENTO,'||
                    'SEL_SERIE,'||
                    'FECHAI,'||
                    'FECHAF,'||
                    'FECHAADQUISICION,'||
                    'DESCRIPCION,'||
                    'VIGENTE,'||
                    'VALOR,'||
                    'ACTUALIZADA,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';

        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''' ,'||
                     '''' || UN_NUE_NIT || ''' ,'||
                     '''' || UN_NUE_SUCURSAL || ''','||
                     'NUMERO_POLIZA,'||
                     'CONSECUTIVO,'||
                     'SEL_GRUPO,'||
                     'SEL_ELEMENTO,'||
                     'SEL_SERIE,'||
                     'FECHAI,'||
                     'FECHAF,'||
                     'FECHAADQUISICION,'||
                     'DESCRIPCION,'||
                     'VIGENTE,'||
                     'VALOR,'||
                     'ACTUALIZADA,'||
                     'SYSDATE,'||
                     ''''||UN_USUARIO||''''|| 
                     ' FROM POLIZAS_ACTIVOS'||
                     ' WHERE POLIZAS_ACTIVOS.COMPANIA   = ''' || UN_ANT_COMPANIA || ''''||
                     ' AND POLIZAS_ACTIVOS.ASEGURADORA  = ''' || UN_ANT_NIT || ''''||
                     ' AND POLIZAS_ACTIVOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';
          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;


          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  

  BEGIN
      MI_TABLA:='D_POLIZAS_ACTIVOS';
      MI_VALORES:='D_POLIZAS_ACTIVOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'D_POLIZAS_ACTIVOS.ASEGURADORA   = ''' || UN_NUE_NIT || ''','||
                  'D_POLIZAS_ACTIVOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'D_POLIZAS_ACTIVOS.DATE_MODIFIED =SYSDATE,'||
                  'D_POLIZAS_ACTIVOS.MODIFIED_BY   ='''||UN_USUARIO||'''';


      MI_CONDICION:='D_POLIZAS_ACTIVOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    'AND D_POLIZAS_ACTIVOS.ASEGURADORA  = ''' || UN_ANT_NIT || ''''||
                    'AND D_POLIZAS_ACTIVOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;   

  BEGIN
      MI_TABLA:='AUDITORIA_CONCILIACION';
      MI_VALORES:='AUDITORIA_CONCILIACION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'AUDITORIA_CONCILIACION.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'AUDITORIA_CONCILIACION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'AUDITORIA_CONCILIACION.DATE_MODIFIED =SYSDATE,'||
                  'AUDITORIA_CONCILIACION.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='AUDITORIA_CONCILIACION.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND AUDITORIA_CONCILIACION.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND AUDITORIA_CONCILIACION.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;   




  BEGIN
      MI_TABLA:='BPPROYECTONOVEDADESTECNICAS';
      MI_VALORES:='BPPROYECTONOVEDADESTECNICAS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'BPPROYECTONOVEDADESTECNICAS.INTERVENTOR   = ''' || UN_NUE_NIT || ''','||
                  'BPPROYECTONOVEDADESTECNICAS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'BPPROYECTONOVEDADESTECNICAS.DATE_MODIFIED =SYSDATE,'||
                  'BPPROYECTONOVEDADESTECNICAS.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='BPPROYECTONOVEDADESTECNICAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND BPPROYECTONOVEDADESTECNICAS.INTERVENTOR = ''' || UN_ANT_NIT || ''''||
                    ' AND BPPROYECTONOVEDADESTECNICAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';  
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;     


  --MI_PCKDATOS := PCK_DATOS.FC_ACME('CALIFICACION_PRUEBAS','M','CALIFICACION_PRUEBAS.COMPANIA = ''' || UN_NUE_COMPANIA || ''', CALIFICACION_PRUEBAS.NUMERO_DCTO = ''' || UN_NUE_NIT || ''', CALIFICACION_PRUEBAS.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'CALIFICACION_PRUEBAS.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND CALIFICACION_PRUEBAS.NUMERO_DCTO = ''' || UN_ANT_NIT || ''' AND CALIFICACION_PRUEBAS.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');
  --MI_PCKDATOS := PCK_DATOS.FC_ACME('COMITE_CALIFICACION_EV','M','COMITE_CALIFICACION_EV.COMPANIA = ''' || UN_NUE_COMPANIA || ''', COMITE_CALIFICACION_EV.NUM_DCTO = ''' || UN_NUE_NIT || ''', COMITE_CALIFICACION_EV.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'COMITE_CALIFICACION_EV.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND COMITE_CALIFICACION_EV.NUM_DCTO = ''' || UN_ANT_NIT || ''' AND COMITE_CALIFICACION_EV.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');
  --MI_PCKDATOS := PCK_DATOS.FC_ACME('COMITE_SELECCION','M','COMITE_SELECCION.COMPANIA = ''' || UN_NUE_COMPANIA || ''', COMITE_SELECCION.NUMERO_DCTO = ''' || UN_NUE_NIT || ''', COMITE_SELECCION.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'COMITE_SELECCION.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND COMITE_SELECCION.NUMERO_DCTO = ''' || UN_ANT_NIT || ''' AND COMITE_SELECCION.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');


  BEGIN
      MI_TABLA:='COMPROBANTE_CNT';
      MI_VALORES:='COMPROBANTE_CNT.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'COMPROBANTE_CNT.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'COMPROBANTE_CNT.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','|| 
                  'COMPROBANTE_CNT.DATE_MODIFIED  =SYSDATE,'||
                  'COMPROBANTE_CNT.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='COMPROBANTE_CNT.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_CNT.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_CNT.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;     
 BEGIN
      MI_TABLA:='COMPROBANTE_CNT';
      MI_VALORES:= 'COMPROBANTE_CNT.COMPANIA         = ''' || UN_NUE_COMPANIA || ''','||
                   'COMPROBANTE_CNT.TERCERO_NSESION  = ''' || UN_NUE_NIT || ''','||
                   'COMPROBANTE_CNT.SUCURSAL_NSESION = ''' || UN_NUE_SUCURSAL || ''','||
                   'COMPROBANTE_CNT.DATE_MODIFIED    =SYSDATE,'||
                   'COMPROBANTE_CNT.MODIFIED_BY      ='''||UN_USUARIO||'''';  

      MI_CONDICION:='COMPROBANTE_CNT.COMPANIA              = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_CNT.TERCERO_NSESION  = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_CNT.SUCURSAL_NSESION = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;     

  BEGIN
      MI_TABLA:='COMPROBANTE_PPTAL';
      MI_VALORES:='COMPROBANTE_PPTAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'COMPROBANTE_PPTAL.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'COMPROBANTE_PPTAL.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'COMPROBANTE_PPTAL.DATE_MODIFIED  =SYSDATE,'||
                  'COMPROBANTE_PPTAL.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='COMPROBANTE_PPTAL.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_PPTAL.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_PPTAL.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;    


   BEGIN
      MI_TABLA:='CONSORCIADOS';
      MI_VALORES:='CONSORCIADOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'CONSORCIADOS.NIT           = ''' || UN_NUE_NIT || ''','||
                  'CONSORCIADOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'CONSORCIADOS.DATE_MODIFIED =SYSDATE,'||
                  'CONSORCIADOS.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='CONSORCIADOS.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND CONSORCIADOS.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND CONSORCIADOS.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  --MI_PCKDATOS := PCK_DATOS.FC_ACME('COTIZACIONESACTIVIDADES','M','COTIZACIONESACTIVIDADES.COMPANIA = ''' || UN_NUE_COMPANIA || ''', COTIZACIONESACTIVIDADES.NITESTABLECIMIENTO = ''' || UN_NUE_NIT || ''', COTIZACIONESACTIVIDADES.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'COTIZACIONESACTIVIDADES.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND COTIZACIONESACTIVIDADES.NITESTABLECIMIENTO = ''' || UN_ANT_NIT || ''' AND COTIZACIONESACTIVIDADES.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');

   BEGIN
        MI_TABLA  :='CUOTASPARTES';
        MI_CAMPOS:='COMPANIA,'||
                    'NIT,'||
                    'SUCURSAL,'||
                    'TIPOC,'||
                    'FECHAABONO,'||
                    'ABONO,'||
                    'SALDOINICIAL,'|| 
                    'DATE_CREATED,'||
                    'CREATED_BY';

        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''' ,'||
                      '''' || UN_NUE_NIT || ''' ,'||
                      '''' || UN_NUE_SUCURSAL || ''' ,'||
                      'TIPOC,'||
                      'FECHAABONO,'||
                      'ABONO,'||
                      'SALDOINICIAL,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||            
                      ' FROM CUOTASPARTES'||
                      ' WHERE CUOTASPARTES.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND CUOTASPARTES.NIT        = ''' || UN_ANT_NIT || ''''||
                      ' AND CUOTASPARTES.SUCURSAL   = ''' || UN_ANT_SUCURSAL || ''''; 

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


 BEGIN
      MI_TABLA:='CUOTASPARTES_DETALLE';
      MI_VALORES:='CUOTASPARTES_DETALLE.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'CUOTASPARTES_DETALLE.NIT            = ''' || UN_NUE_NIT || ''','||
                  'CUOTASPARTES_DETALLE.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'CUOTASPARTES_DETALLE.DATE_MODIFIED  =SYSDATE,'||
                  'CUOTASPARTES_DETALLE.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='CUOTASPARTES_DETALLE.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND CUOTASPARTES_DETALLE.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND CUOTASPARTES_DETALLE.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  --MI_PCKDATOS := PCK_DATOS.FC_ACME('CURSOS_CARRERAS','M','CURSOS_CARRERAS.COMPANIA = ''' || UN_NUE_COMPANIA || ''', CURSOS_CARRERAS.NITENTIDAD = ''' || UN_NUE_NIT || ''', CURSOS_CARRERAS.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'CURSOS_CARRERAS.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND CURSOS_CARRERAS.NITENTIDAD = ''' || UN_ANT_NIT || ''' AND CURSOS_CARRERAS.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');


   BEGIN
      MI_STRSQL :=' SELECT COUNT(TERCERO)TER 
                    FROM DETALLE_COMPROBANTE_CNT 
                    WHERE DETALLE_COMPROBANTE_CNT.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_TER;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TER:=0;
      END;               

      MI_TABLA:='DETALLE_COMPROBANTE_CNT';
      MI_VALORES:='DETALLE_COMPROBANTE_CNT.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'DETALLE_COMPROBANTE_CNT.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'DETALLE_COMPROBANTE_CNT.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'DETALLE_COMPROBANTE_CNT.DATE_MODIFIED   =SYSDATE,'||
                  'DETALLE_COMPROBANTE_CNT.MODIFIED_BY     ='''||UN_USUARIO||'''';  

      MI_CONDICION:='DETALLE_COMPROBANTE_CNT.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';  

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
      END;

      /*21/08/2018 YB Se agrega procedimiento. el cual se encarga de actualizar los saldos en la tabla SALDO_AUX_CONTABLE*/
      PCK_CONTABILIDAD6.PR_ACTCONTSALDOAUX(   UN_COMPANIA_ANT	  =>	UN_ANT_COMPANIA,
                                              UN_TERCERO_ANT    => 	UN_ANT_NIT,
                                              UN_SUCURSAL_ANT	  =>  UN_ANT_SUCURSAL,
                                              UN_COMPANIA_NUE		=>  UN_NUE_COMPANIA,
                                              UN_TERCERO_NUE		=>  UN_NUE_NIT,
                                              UN_SUCURSAL_NUE 	=>  UN_NUE_SUCURSAL,
                                              UN_USUARIO 			  =>  UN_USUARIO);
      BEGIN
      MI_TABLA:='DETALLE_COMPROBANTE_PPTAL';
      MI_VALORES:='DETALLE_COMPROBANTE_PPTAL.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'DETALLE_COMPROBANTE_PPTAL.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'DETALLE_COMPROBANTE_PPTAL.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'DETALLE_COMPROBANTE_PPTAL.DATE_MODIFIED =SYSDATE,'||
                  'DETALLE_COMPROBANTE_PPTAL.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='DETALLE_COMPROBANTE_PPTAL.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                      AND DETALLE_COMPROBANTE_PPTAL.TERCERO  = ''' || UN_ANT_NIT || '''
                      AND DETALLE_COMPROBANTE_PPTAL.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




 BEGIN
      MI_TABLA:='D_MOVIMIENTO';
      MI_VALORES:='D_MOVIMIENTO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'D_MOVIMIENTO.PROVEEDORCA          = ''' || UN_NUE_NIT || ''','||
                  'D_MOVIMIENTO.SUCURSALCA           = ''' || UN_NUE_SUCURSAL || ''','||      
                  'D_MOVIMIENTO.DATE_MODIFIED        =SYSDATE,'||
                  'D_MOVIMIENTO.MODIFIED_BY          ='''||UN_USUARIO||'''';  

      MI_CONDICION:='D_MOVIMIENTO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND D_MOVIMIENTO.PROVEEDORCA = ''' || UN_ANT_NIT || ''''||
                    ' AND D_MOVIMIENTO.SUCURSALCA  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




  BEGIN
      MI_TABLA:='D_SCANNER';
      MI_VALORES:='D_SCANNER.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'D_SCANNER.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'D_SCANNER.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'D_SCANNER.DATE_MODIFIED =SYSDATE,'||
                  'D_SCANNER.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='D_SCANNER.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND D_SCANNER.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND D_SCANNER.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='D_TRANSACCIONMODELO';
      MI_VALORES:='D_TRANSACCIONMODELO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'D_TRANSACCIONMODELO.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'D_TRANSACCIONMODELO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'D_TRANSACCIONMODELO.DATE_MODIFIED =SYSDATE,'||
                  'D_TRANSACCIONMODELO.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='D_TRANSACCIONMODELO.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND D_TRANSACCIONMODELO.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND D_TRANSACCIONMODELO.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';  
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  --MI_PCKDATOS := PCK_DATOS.FC_ACME('ELEGIBLES','M','ELEGIBLES.COMPANIA = ''' || UN_NUE_COMPANIA || ''', ELEGIBLES.NUMERO_DCTO = ''' || UN_NUE_NIT || ''', ELEGIBLES.SUCURSAL = ''' || UN_NUE_SUCURSAL || '''',NULL,NULL,'ELEGIBLES.COMPANIA = ''' || UN_ANT_COMPANIA || ''' AND ELEGIBLES.NUMERO_DCTO = ''' || UN_ANT_NIT || ''' AND ELEGIBLES.SUCURSAL = ''' || UN_ANT_SUCURSAL || ''' ');

     BEGIN
      MI_TABLA:='EMBARGOS';
      MI_VALORES:='EMBARGOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'EMBARGOS.CEDULA        = ''' || UN_NUE_NIT || ''','||
                  'EMBARGOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'EMBARGOS.DATE_MODIFIED =SYSDATE,'||
                  'EMBARGOS.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='EMBARGOS.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND EMBARGOS.CEDULA   = ''' || UN_ANT_NIT || ''''||
                    ' AND EMBARGOS.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

BEGIN
      MI_TABLA:='EMBARGOS_CT';
      MI_VALORES:='EMBARGOS_CT.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'EMBARGOS_CT.NIT_DEMANDADO      = ''' || UN_NUE_NIT || ''','||
                  'EMBARGOS_CT.SUCURSAL_DEMANDADO = ''' || UN_NUE_SUCURSAL || ''','||
                  'EMBARGOS_CT.DATE_MODIFIED      =SYSDATE,'||
                  'EMBARGOS_CT.MODIFIED_BY        ='''||UN_USUARIO||'''';  

      MI_CONDICION:='EMBARGOS_CT.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND EMBARGOS_CT.NIT_DEMANDADO       = ''' || UN_ANT_NIT || ''''||
                    ' AND EMBARGOS_CT.SUCURSAL_DEMANDADO  = ''' || UN_ANT_SUCURSAL || '''';   

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='EMBARGOS_CT';
      MI_VALORES:='EMBARGOS_CT.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'EMBARGOS_CT.NIT_DEMANDANTE      = ''' || UN_NUE_NIT || ''','||
                  'EMBARGOS_CT.SUCURSAL_DEMANDANTE = ''' || UN_NUE_SUCURSAL || ''','||  
                  'EMBARGOS_CT.DATE_MODIFIED       =SYSDATE,'||
                  'EMBARGOS_CT.MODIFIED_BY         ='''||UN_USUARIO||'''';  

      MI_CONDICION:='EMBARGOS_CT.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND EMBARGOS_CT.NIT_DEMANDANTE      = ''' || UN_ANT_NIT || ''''||
                    ' AND EMBARGOS_CT.SUCURSAL_DEMANDANTE = ''' || UN_ANT_SUCURSAL || ''''; 
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
        MI_TABLA  :='NAT_ENTIDADESCAPACITACION';
        MI_CAMPOS:='COMPANIA,'||
                    'NITESTABLECIMIENTO,'||
                    'SUCURSAL,'||
                    'NOMBREESTABLECIMIENTO,'||
                    'DIRECCIONESTABLECIMIENTO,'||
                    'TELEFONOESTABLECIMIENTO,'||
                    'DIRECCIONCONTACTO,'||
                    'TELEFONOCONTACTO,'||
                    'TIPO,'||
                    'PERSONACONTACTO,'||
                    'SUCURSALCONTACTO,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''' ,'||
                      '''' || UN_NUE_NIT || ''' ,'||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'NOMBREESTABLECIMIENTO,'||
                      'DIRECCIONESTABLECIMIENTO,'||
                      'TELEFONOESTABLECIMIENTO,'||
                      'DIRECCIONCONTACTO,'||
                      'TELEFONOCONTACTO,'||
                      'TIPO,'||
                      'PERSONACONTACTO,'||
                      'SUCURSALCONTACTO,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||   
                      ' FROM NAT_ENTIDADESCAPACITACION'||
                      ' WHERE NAT_ENTIDADESCAPACITACION.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND NAT_ENTIDADESCAPACITACION.NITESTABLECIMIENTO  = ''' || UN_ANT_NIT || ''''||
                      ' AND NAT_ENTIDADESCAPACITACION.SUCURSAL            = ''' || UN_ANT_SUCURSAL || '''';              

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_NUE_COMPANIA || ''','|| 
                                'NIT = ''' || UN_NUE_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_NUE_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


   BEGIN
        MI_TABLA  :='NAT_ENTIDADESCAPACITACION';
        MI_VALORES:='COMPANIA = ''' || UN_NUE_COMPANIA || '''
                    ,PERSONACONTACTO = ''' || UN_NUE_NIT || '''
                    ,SUCURSALCONTACTO = ''' || UN_NUE_SUCURSAL || '''
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY = '''||UN_USUARIO||'''';       

        MI_CONDICION :=' NAT_ENTIDADESCAPACITACION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND NAT_ENTIDADESCAPACITACION.PERSONACONTACTO    = ''' || UN_ANT_NIT || ''''||
                      ' AND NAT_ENTIDADESCAPACITACION.SUCURSALCONTACTO   = ''' || UN_ANT_SUCURSAL || '''';              

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                              UN_ACCION       => 'M',
                                              UN_CAMPOS       => MI_VALORES,
                                              UN_CONDICION    => MI_CONDICION);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


  BEGIN
        MI_TABLA  :='NAT_CURSOS_CARRERAS';
        MI_VALORES:='COMPANIA      = ''' || UN_NUE_COMPANIA || '''
                    ,NITENTIDAD    = ''' || UN_NUE_NIT || '''
                    ,SUCURSAL      = ''' || UN_NUE_SUCURSAL || '''
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY   = '''||UN_USUARIO||'''';       

        MI_CONDICION :=' COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND NITENTIDAD    = ''' || UN_ANT_NIT || ''''||
                      ' AND SUCURSAL      = ''' || UN_ANT_SUCURSAL || '''';              

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                              UN_ACCION       => 'M',
                                              UN_CAMPOS       => MI_VALORES,
                                              UN_CONDICION    => MI_CONDICION);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                    'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                    'NIT = ''' || UN_ANT_NIT || ''','|| 
                                    'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


      BEGIN
        MI_TABLA  :='NAT_CALIFICACION_PRUEBAS';
        MI_VALORES:='COMPANIA      = ''' || UN_NUE_COMPANIA || '''
                    ,NUMERO_DCTO    = ''' || UN_NUE_NIT || '''
                    ,SUCURSAL      = ''' || UN_NUE_SUCURSAL || '''
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY   = '''||UN_USUARIO||'''';       

        MI_CONDICION :=' COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND NUMERO_DCTO    = ''' || UN_ANT_NIT || ''''||
                      ' AND SUCURSAL      = ''' || UN_ANT_SUCURSAL || '''';              

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                              UN_ACCION       => 'M',
                                              UN_CAMPOS       => MI_VALORES,
                                              UN_CONDICION    => MI_CONDICION);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                    'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                    'NIT = ''' || UN_ANT_NIT || ''','|| 
                                    'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


   BEGIN
      MI_TABLA:='ESTABLECIMIENTOS_DOCENTES';
      MI_VALORES:='ESTABLECIMIENTOS_DOCENTES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'ESTABLECIMIENTOS_DOCENTES.NIT           = ''' || UN_NUE_NIT || ''','||
                  'ESTABLECIMIENTOS_DOCENTES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'ESTABLECIMIENTOS_DOCENTES.DATE_MODIFIED =SYSDATE,'||
                  'ESTABLECIMIENTOS_DOCENTES.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='ESTABLECIMIENTOS_DOCENTES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ESTABLECIMIENTOS_DOCENTES.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND ESTABLECIMIENTOS_DOCENTES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';  
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
   BEGIN
      MI_TABLA:='ES_ASP_ESTPR';
      MI_VALORES:='ES_ASP_ESTPR.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ASP_ESTPR.ENTIDAD       = ''' || UN_NUE_NIT || ''','||
                  'ES_ASP_ESTPR.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ASP_ESTPR.DATE_MODIFIED =SYSDATE,'||
                  'ES_ASP_ESTPR.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='ES_ASP_ESTPR.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ASP_ESTPR.ENTIDAD  = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ASP_ESTPR.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='ES_ESTPREVIO';
      MI_VALORES:='ES_ESTPREVIO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ESTPREVIO.SUPERVISION          = ''' || UN_NUE_NIT || ''','||
                  'ES_ESTPREVIO.SUCURSAL_SUPERVISION = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ESTPREVIO.DATE_MODIFIED        =SYSDATE,'||
                  'ES_ESTPREVIO.MODIFIED_BY          ='''||UN_USUARIO||'''';  

      MI_CONDICION:='ES_ESTPREVIO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ESTPREVIO.SUPERVISION          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ESTPREVIO.SUCURSAL_SUPERVISION = ''' || UN_ANT_SUCURSAL || ''''; 

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


     BEGIN
      MI_TABLA:='ES_SUPERVISORES';
      MI_VALORES:='ES_SUPERVISORES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_SUPERVISORES.CEDULA        = ''' || UN_NUE_NIT || ''','||
                  'ES_SUPERVISORES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_SUPERVISORES.DATE_MODIFIED =SYSDATE,'||
                  'ES_SUPERVISORES.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='ES_SUPERVISORES.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_SUPERVISORES.CEDULA    = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_SUPERVISORES.SUCURSAL  = ''' || UN_ANT_SUCURSAL || ''''; 

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



      BEGIN
      MI_TABLA:='NAT_EVALUACION';
      MI_VALORES:='NAT_EVALUACION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EVALUACION.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_EVALUACION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EVALUACION.DATE_MODIFIED =SYSDATE,'||
                  'NAT_EVALUACION.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_EVALUACION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EVALUACION.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EVALUACION.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='NAT_EVALUACION_DOCUMENTOS';
      MI_VALORES:='NAT_EVALUACION_DOCUMENTOS.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EVALUACION_DOCUMENTOS.CEDULA          = ''' || UN_NUE_NIT || ''','||
                  'NAT_EVALUACION_DOCUMENTOS.SUCURSAL_CEDULA = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EVALUACION_DOCUMENTOS.DATE_MODIFIED   =SYSDATE,'||
                  'NAT_EVALUACION_DOCUMENTOS.MODIFIED_BY     ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_EVALUACION_DOCUMENTOS.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EVALUACION_DOCUMENTOS.CEDULA          = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EVALUACION_DOCUMENTOS.SUCURSAL_CEDULA = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='NAT_ACTIVIDADESPROGRAMADAS';
      MI_VALORES:='NAT_ACTIVIDADESPROGRAMADAS.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_ACTIVIDADESPROGRAMADAS.COORDEVENTO          = ''' || UN_NUE_NIT || ''','||
                  'NAT_ACTIVIDADESPROGRAMADAS.SUCURSALCOOR = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_ACTIVIDADESPROGRAMADAS.DATE_MODIFIED   =SYSDATE,'||
                  'NAT_ACTIVIDADESPROGRAMADAS.MODIFIED_BY     ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_ACTIVIDADESPROGRAMADAS.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_ACTIVIDADESPROGRAMADAS.COORDEVENTO          = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_ACTIVIDADESPROGRAMADAS.SUCURSALCOOR = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TERCERO';
                     MI_REEMPLAZOS (0).VALOR :=  UN_ANT_NIT;    
                     MI_REEMPLAZOS (1).CLAVE := 'SUCURSAL';                    
                     MI_REEMPLAZOS (1).VALOR :=  UN_ANT_SUCURSAL;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTACPROGRA,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;





    BEGIN
      MI_TABLA:='FAMILIARES';
      MI_VALORES:='FAMILIARES.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'FAMILIARES.IDENTIFICACION  = ''' || UN_NUE_NIT || ''','||
                  'FAMILIARES.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||      
                  'FAMILIARES.DATE_MODIFIED   =SYSDATE,'||
                  'FAMILIARES.MODIFIED_BY     ='''||UN_USUARIO||'''';  

      MI_CONDICION:='FAMILIARES.COMPANIA            = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND FAMILIARES.IDENTIFICACION = ''' || UN_ANT_NIT || ''''||
                    ' AND FAMILIARES.SUCURSAL       = ''' || UN_ANT_SUCURSAL || ''''; 

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;





   BEGIN
      MI_TABLA:='FONDO';
      MI_VALORES:='FONDO.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'FONDO.NIT             = ''' || UN_NUE_NIT || ''','||
                  'FONDO.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'FONDO.DATE_MODIFIED   =SYSDATE,'||
                  'FONDO.MODIFIED_BY     ='''||UN_USUARIO||'''';  

      MI_CONDICION:='FONDO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND FONDO.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND FONDO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



BEGIN
      MI_TABLA:='FOTOSCONDUCTORES';
      MI_VALORES:='FOTOSCONDUCTORES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'FOTOSCONDUCTORES.REGISTRO      = ''' || UN_NUE_NIT || ''','||
                  'FOTOSCONDUCTORES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'FOTOSCONDUCTORES.DATE_MODIFIED =SYSDATE,'||
                  'FOTOSCONDUCTORES.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='FOTOSCONDUCTORES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND FOTOSCONDUCTORES.REGISTRO = ''' || UN_ANT_NIT || ''''||
                    ' AND FOTOSCONDUCTORES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='HISTORIAL_DE_CARGOS';
      MI_VALORES:='HISTORIAL_DE_CARGOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'HISTORIAL_DE_CARGOS.NUMERODOC     = ''' || UN_NUE_NIT || ''','||
                  'HISTORIAL_DE_CARGOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'HISTORIAL_DE_CARGOS.DATE_MODIFIED =SYSDATE,'||
                  'HISTORIAL_DE_CARGOS.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='HISTORIAL_DE_CARGOS.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND HISTORIAL_DE_CARGOS.NUMERODOC = ''' || UN_ANT_NIT || ''''||
                    ' AND HISTORIAL_DE_CARGOS.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
        MI_TABLA  :='ID_COMERCIALIZADORA';
        MI_CAMPOS:='COMPANIA,'||
                    'NIT,'||
                    'SUCURSAL,'||
                    'NOMBRE,'||
                    'DIRECCION,'||
                    'TELEFONO,'||
                    'EMAIL,'||
                    'PAIS,'||
                    'DEPARTAMENTO,'||
                    'CIUDAD,'|| 
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''' ,'||
                      '''' || UN_NUE_NIT || ''' ,'||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'NOMBRE,'||
                      'DIRECCION,'||
                      'TELEFONO,'||
                      'EMAIL,'||
                      'PAIS,'||
                      'DEPARTAMENTO,'||
                      'CIUDAD,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||   
                      ' FROM ID_COMERCIALIZADORA'||
                      ' WHERE ID_COMERCIALIZADORA.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND ID_COMERCIALIZADORA.NIT        = ''' || UN_ANT_NIT || ''''||
                      ' AND ID_COMERCIALIZADORA.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';                      

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 

  BEGIN
      MI_TABLA:='ID_HISTORICOS';
      MI_VALORES:='ID_HISTORICOS.COMPANIA                  = ''' || UN_NUE_COMPANIA || ''','||
                  'ID_HISTORICOS.COMERCIALIZADORA          = ''' || UN_NUE_NIT || ''','||
                  'ID_HISTORICOS.SUCURSAL_COMERCIALIZADORA = ''' || UN_NUE_SUCURSAL || ''','||
                  'ID_HISTORICOS.DATE_MODIFIED             =SYSDATE,'||
                  'ID_HISTORICOS.MODIFIED_BY               ='''||UN_USUARIO||'''';  

      MI_CONDICION:='ID_HISTORICOS.COMPANIA                       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ID_HISTORICOS.COMERCIALIZADORA          = ''' || UN_ANT_NIT || ''''||
                    ' AND ID_HISTORICOS.SUCURSAL_COMERCIALIZADORA = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 


   BEGIN
      MI_TABLA:='INVENTARIO';
      MI_VALORES:='INVENTARIO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'INVENTARIO.PROVULTCOMPRA  = ''' || UN_NUE_NIT || ''','||
                  'INVENTARIO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'INVENTARIO.DATE_MODIFIED  =SYSDATE,'||
                  'INVENTARIO.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='INVENTARIO.COMPANIA           = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND INVENTARIO.PROVULTCOMPRA = ''' || UN_ANT_NIT || ''''||
                    ' AND INVENTARIO.SUCURSAL      = ''' || UN_ANT_SUCURSAL || ''''; 
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 

     BEGIN
      MI_TABLA:='INVENTARIO_PARQUE_AUTOMOTOR';
      MI_VALORES:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.CONDUCTOR     = ''' || UN_NUE_NIT || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||  
                  'INVENTARIO_PARQUE_AUTOMOTOR.DATE_MODIFIED =SYSDATE,'||
                  'INVENTARIO_PARQUE_AUTOMOTOR.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.CONDUCTOR  = ''' || UN_ANT_NIT || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='INVENTARIO_PARQUE_AUTOMOTOR';
      MI_VALORES:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.ENTIDAD_COMODATARIA  = ''' || UN_NUE_NIT || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL_COMODATARIA = ''' || UN_NUE_SUCURSAL || ''','|| 
                  'INVENTARIO_PARQUE_AUTOMOTOR.DATE_MODIFIED        =SYSDATE,'||
                  'INVENTARIO_PARQUE_AUTOMOTOR.MODIFIED_BY          ='''||UN_USUARIO||'''';  

      MI_CONDICION:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.ENTIDAD_COMODATARIA  = ''' || UN_ANT_NIT || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL_COMODATARIA = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

   BEGIN
      MI_TABLA:='JUREXPERIENCIA';
      MI_VALORES:='JUREXPERIENCIA.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'JUREXPERIENCIA.NITSOCIEDAD   = ''' || UN_NUE_NIT || ''','||
                  'JUREXPERIENCIA.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'JUREXPERIENCIA.DATE_MODIFIED =SYSDATE,'||
                  'JUREXPERIENCIA.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='JUREXPERIENCIA.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND JUREXPERIENCIA.NITSOCIEDAD  = ''' || UN_ANT_NIT || ''''||
                    ' AND JUREXPERIENCIA.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='NAT_JURIDENTIFICACION';
      MI_VALORES:='NAT_JURIDENTIFICACION.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_JURIDENTIFICACION.NUMERO_IDREPRE = ''' || UN_NUE_NIT || ''','||
                  'NAT_JURIDENTIFICACION.SUCURSAL_REPRE = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_JURIDENTIFICACION.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_JURIDENTIFICACION.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_JURIDENTIFICACION.COMPANIA            = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_JURIDENTIFICACION.NUMERO_IDREPRE = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_JURIDENTIFICACION.SUCURSAL_REPRE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

     BEGIN
      MI_TABLA:='NAT_JURSERVICIOS';
      MI_VALORES:='NAT_JURSERVICIOS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_JURSERVICIOS.NITSOCIEDAD    = ''' || UN_NUE_NIT || ''','||
                  'NAT_JURSERVICIOS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_JURSERVICIOS.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_JURSERVICIOS.MODIFIED_BY    ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_JURSERVICIOS.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_JURSERVICIOS.NITSOCIEDAD  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_JURSERVICIOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


  BEGIN
        MI_TABLA  :='LUGAR_PARQUEO';
        MI_CAMPOS:='COMPANIA,'||
                    'CODIGO,'||
                    'SUCURSAL_CODIGO,'||
                    'NOMBRE,TELEFONO,'||
                    'DIRECCION,'||
                    'RESPONSABLE,'||
                    'SUCURSAL,'|| 
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''','||
                      '''' || UN_NUE_NIT || ''','||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'NOMBRE,'||
                      'TELEFONO,'||
                      'DIRECCION,'||
                      'RESPONSABLE,'||
                      'SUCURSAL,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||
                      ' FROM LUGAR_PARQUEO'||
                      ' WHERE LUGAR_PARQUEO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND LUGAR_PARQUEO.CODIGO          = ''' || UN_ANT_NIT || ''''||
                      ' AND LUGAR_PARQUEO.SUCURSAL_CODIGO = ''' || UN_ANT_SUCURSAL || '''';                  

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 
      BEGIN
      MI_TABLA:='INVENTARIO_PARQUE_AUTOMOTOR';
      MI_VALORES:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA         = ''' || UN_NUE_COMPANIA || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.LUGAR_PARQUEO    = ''' || UN_NUE_NIT || ''','||
                  'INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL_PARQUEO = ''' || UN_NUE_SUCURSAL || ''','|| 
                  'INVENTARIO_PARQUE_AUTOMOTOR.DATE_MODIFIED    =SYSDATE,'||
                  'INVENTARIO_PARQUE_AUTOMOTOR.MODIFIED_BY      ='''||UN_USUARIO||'''';  

      MI_CONDICION:='INVENTARIO_PARQUE_AUTOMOTOR.COMPANIA              = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.LUGAR_PARQUEO    = ''' || UN_ANT_NIT || ''''||
                    ' AND INVENTARIO_PARQUE_AUTOMOTOR.SUCURSAL_PARQUEO = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  



        BEGIN
      MI_TABLA:='LUGAR_PARQUEO';
      MI_VALORES:='LUGAR_PARQUEO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'LUGAR_PARQUEO.RESPONSABLE   = ''' || UN_NUE_NIT || ''','||
                  'LUGAR_PARQUEO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'LUGAR_PARQUEO.DATE_MODIFIED =SYSDATE,'||
                  'LUGAR_PARQUEO.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='LUGAR_PARQUEO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND LUGAR_PARQUEO.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND LUGAR_PARQUEO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 

 BEGIN
      MI_TABLA:='MOVIMIENTO';
      MI_VALORES:='MOVIMIENTO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'MOVIMIENTO.PROVEEDORCA   = ''' || UN_NUE_NIT || ''','||
                  'MOVIMIENTO.SUCURSALCA    = ''' || UN_NUE_SUCURSAL || ''','||
                  'MOVIMIENTO.DATE_MODIFIED =SYSDATE,'||
                  'MOVIMIENTO.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='MOVIMIENTO.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MOVIMIENTO.PROVEEDORCA  = ''' || UN_ANT_NIT || ''''||
                    ' AND MOVIMIENTO.SUCURSALCA   = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='MOVIMIENTO';
      MI_VALORES:='MOVIMIENTO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'MOVIMIENTO.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'MOVIMIENTO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'MOVIMIENTO.DATE_MODIFIED =SYSDATE,'||
                  'MOVIMIENTO.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='MOVIMIENTO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MOVIMIENTO.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND MOVIMIENTO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';                    
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




   BEGIN
      MI_TABLA:='NATTIEMPOEXPERIENCIAYSITUACION';
      MI_VALORES:='NATTIEMPOEXPERIENCIAYSITUACION.COMPANIA = ''' || UN_NUE_COMPANIA || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.NUMERO_DCTO = ''' || UN_NUE_NIT || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.SUCURSAL = ''' || UN_NUE_SUCURSAL || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.DATE_MODIFIED =SYSDATE,'||
                  'NATTIEMPOEXPERIENCIAYSITUACION.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NATTIEMPOEXPERIENCIAYSITUACION.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NATTIEMPOEXPERIENCIAYSITUACION.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NATTIEMPOEXPERIENCIAYSITUACION.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
        MI_TABLA  :='NAT_DATOS_PERSONALES';
        MI_CAMPOS:= 'COMPANIA,'||
                    'NUMERO_DCTO,'||
                    'SUCURSAL,'||
                    'CODIGO,'||
                    'APELLIDO1,'||
                    'APELLIDO2,'||
                    'NOMBRES,'||
                    'DCTO_IDENTIDAD,'||
                    'EXPEDIDA,'||
                    'PAISEXTRANJERO,'||
                    'SEXO,'||
                    'ANO,'||
                    'CLASELIBMILITAR,'||
                    'NUMLIBMILITAR,'||
                    'DISTRITOMILITAR,'||
                    'PAISNCTO,'||
                    'DEPTONCTO,'||
                    'MUNICIPIONCTO,'||
                    'FECHANCTO,'||
                    'PAISRESIDE,'||
                    'DEPTORESIDE,'||
                    'MUNICIPIORESIDE,'||
                    'DIRECCION,'||
                    'TELEFONOS,'||
                    'ESTADO_CIVIL,'||
                    'PERSONAS,'||
                    'PUBLICACIONES,'||
                    'INHABILIDADES,'||
                    'FOTO,'||
                    'OBSERVACIONES,'||
                    'JEFEPERSONAL,'||
                    'FIRMAJEFEPER,'||
                    'GRUPOSANGUINEO,'||
                    'RH,'||
                    'CERTIFICADOJUDICIAL,'||
                    'FORMAVINCULACION,'||
                    'LICENCIACONDUCCION,'||
                    'ALERGIAS,'||
                    'BARRIO,'||
                    'MEDICOTRATANTE,'||
                    'ESTRATO,'||
                    'FECHARETIRO,'||
                    'AC_ANOS,'||
                    'DIRECCIONRESIDENCIA,'||
                    'FECHAINGRESO,'||
                    'PROPIETARIO,'||
                    'EMAIL,'||
                    'TELEFONORESIDENCIA,'||
                    'DPTOEXPCEDULA,'||
                    'NUMEROCARPETA,'||
                    'CUENTA,'||
                    'BANCO,'||
                    'TIPOCUENTA,'||
                    'ANOPB,'||
                    'MESPB,'||
                    'DIASPB,'||
                    'ANOPV,'||
                    'MESPV,'||
                    'DIASPV,'||
                    'ANOTI,'||
                    'MESTI,'||
                    'DIASTI,'||
                    'ID_CENTROS_DE_COSTO,'||
                    'MEDICINA_PREPAGADA,'||
                    'FECHA_MEDICINA,'||
                    'FONDO_SINDICATO,'||
                    'SINDICATO,'||
                    'NIT_ESTABLECIMIENTO_DOCENTES,'||
                    'REGIMEN,'||
                    'GRUPO_SANGUINEO,'||
                    'FECHATERCONTRATO,'||
                    'CIUDAD_CUENTA,'||
                    'GRUPOCONTABLE,'||
                    'PROCESORETENCION,'||
                    'ID_DE_TIPO,'||
                    'TIPO_SALARIO,'||
                    'AREAMISOADM,'||
                    'TIPOACTIVIDAD,'||
                    'SEDE,'||
                    'NIVELSIIF,'||
                    'DEPENDIENTES384,'||
                    'FECHA_DEPENDIENTES384,'||
                    'DECLARANTES384,'||
                    'FECHA_DECLARANTES384,'||
                    'RETE_MINIMA,'||
                    'FECHA_RETE_MINIMA,'||
                    'ASALARIADO_NOEMPLEADOS,'||
                    'NUMEROPATRONAL,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:='SELECT '||
                      '''' || UN_NUE_COMPANIA || ''','||
                      '''' || UN_NUE_NIT || ''','||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'CODIGO,'||
                      'APELLIDO1,'||
                      'APELLIDO2,'||
                      'NOMBRES,'||
                      'DCTO_IDENTIDAD,'||
                      'EXPEDIDA,'||
                      'PAISEXTRANJERO,'||
                      'SEXO,'||
                      'ANO,'||
                      'CLASELIBMILITAR,'||
                      'NUMLIBMILITAR,'||
                      'DISTRITOMILITAR,'||
                      'PAISNCTO,'||
                      'DEPTONCTO,'||
                      'MUNICIPIONCTO,'||
                      'FECHANCTO,'||
                      'PAISRESIDE,'||
                      'DEPTORESIDE,'||
                      'MUNICIPIORESIDE,'||
                      'DIRECCION,'||
                      'TELEFONOS,'||
                      'ESTADO_CIVIL,'||
                      'PERSONAS,'||
                      'PUBLICACIONES,'||
                      'INHABILIDADES,'||
                      'FOTO,'||
                      'OBSERVACIONES,'||
                      'JEFEPERSONAL,'||
                      'FIRMAJEFEPER,'||
                      'GRUPOSANGUINEO,'||
                      'RH,'||
                      'CERTIFICADOJUDICIAL,'||
                      'FORMAVINCULACION,'||
                      'LICENCIACONDUCCION,'||
                      'ALERGIAS,'||
                      'BARRIO,'||
                      'MEDICOTRATANTE,'||
                      'ESTRATO,'||
                      'FECHARETIRO,'||
                      'AC_ANOS,'||
                      'DIRECCIONRESIDENCIA,'||
                      'FECHAINGRESO,'||
                      'PROPIETARIO,'||
                      'EMAIL,'||
                      'TELEFONORESIDENCIA,'||
                      'DPTOEXPCEDULA,'||
                      'NUMEROCARPETA,'||
                      'CUENTA,'||
                      'BANCO,'||
                      'TIPOCUENTA,'||
                      'ANOPB,'||
                      'MESPB,'||
                      'DIASPB,'||
                      'ANOPV,'||
                      'MESPV,'||
                      'DIASPV,'||
                      'ANOTI,'||
                      'MESTI,'||
                      'DIASTI,'||
                      'ID_CENTROS_DE_COSTO,'||
                      'MEDICINA_PREPAGADA,'||
                      'FECHA_MEDICINA,'||
                      'FONDO_SINDICATO,'||
                      'SINDICATO,'||
                      'NIT_ESTABLECIMIENTO_DOCENTES,'||
                      'REGIMEN,'||
                      'GRUPO_SANGUINEO,'||
                      'FECHATERCONTRATO,'||
                      'CIUDAD_CUENTA,'||
                      'GRUPOCONTABLE,'||
                      'PROCESORETENCION,'||
                      'ID_DE_TIPO,'||
                      'TIPO_SALARIO,'||
                      'AREAMISOADM,'||
                      'TIPOACTIVIDAD,'||
                      'SEDE,'||
                      'NIVELSIIF,'||
                      'DEPENDIENTES384,'||
                      'FECHA_DEPENDIENTES384,'||
                      'DECLARANTES384,'||
                      'FECHA_DECLARANTES384,'||
                      'RETE_MINIMA,'||
                      'FECHA_RETE_MINIMA,'||
                      'ASALARIADO_NOEMPLEADOS,'||
                      'NUMEROPATRONAL,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||
                      ' FROM NAT_DATOS_PERSONALES'||
                      ' WHERE NAT_DATOS_PERSONALES.COMPANIA  = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND NAT_DATOS_PERSONALES.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                      ' AND NAT_DATOS_PERSONALES.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';                          

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO ='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 

      BEGIN
      MI_TABLA:='FAMILIARES';
      MI_VALORES:='FAMILIARES.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'FAMILIARES.DCTO_EMPLEADO      = ''' || UN_NUE_NIT || ''','||
                  'FAMILIARES.SUCURSAL_EMPLEADO  = ''' || UN_NUE_SUCURSAL || ''','||      
                  'FAMILIARES.DATE_MODIFIED      =SYSDATE,'||
                  'FAMILIARES.MODIFIED_BY        ='''||UN_USUARIO||'''';  

      MI_CONDICION:='FAMILIARES.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND FAMILIARES.DCTO_EMPLEADO     = ''' || UN_ANT_NIT || ''''||
                    ' AND FAMILIARES.SUCURSAL_EMPLEADO = ''' || UN_ANT_SUCURSAL || ''''; 

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

     BEGIN
      MI_TABLA:='NAT_ACTIVIDADES';
      MI_VALORES:='NAT_ACTIVIDADES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_ACTIVIDADES.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_ACTIVIDADES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_ACTIVIDADES.DATE_MODIFIED =SYSDATE,'||
                  'NAT_ACTIVIDADES.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_ACTIVIDADES.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_ACTIVIDADES.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_ACTIVIDADES.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



     BEGIN
      MI_TABLA:='NAT_COMISION';
      MI_VALORES:='NAT_COMISION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_COMISION.CO_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_COMISION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_COMISION.DATE_MODIFIED =SYSDATE,'||
                  'NAT_COMISION.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_COMISION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_COMISION.CO_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_COMISION.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';                    
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='NAT_COMITE_SELECCION';
      MI_VALORES:='NAT_COMITE_SELECCION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_COMITE_SELECCION.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_COMITE_SELECCION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_COMITE_SELECCION.DATE_MODIFIED =SYSDATE,'||
                  'NAT_COMITE_SELECCION.MODIFIED_BY   ='''||UN_USUARIO||'''';  

      MI_CONDICION:='NAT_COMITE_SELECCION.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_COMITE_SELECCION.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_COMITE_SELECCION.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

 BEGIN
      MI_TABLA:='NAT_COMPENSATORIO';
      MI_VALORES:='NAT_COMPENSATORIO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_COMPENSATORIO.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_COMPENSATORIO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_COMPENSATORIO.DATE_MODIFIED =SYSDATE,'||
                  'NAT_COMPENSATORIO.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_COMPENSATORIO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_COMPENSATORIO.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_COMPENSATORIO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='NAT_INCENTIVOS';
      MI_VALORES:='NAT_INCENTIVOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_INCENTIVOS.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_INCENTIVOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_INCENTIVOS.DATE_MODIFIED =SYSDATE,'||
                  'NAT_INCENTIVOS.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_INCENTIVOS.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_INCENTIVOS.DP_NUMEDOCU  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_INCENTIVOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';                 
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



  BEGIN
      MI_TABLA:='NATTIEMPOEXPERIENCIAYSITUACION';
      MI_VALORES:='NATTIEMPOEXPERIENCIAYSITUACION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NATTIEMPOEXPERIENCIAYSITUACION.DATE_MODIFIED =SYSDATE,'||
                  'NATTIEMPOEXPERIENCIAYSITUACION.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NATTIEMPOEXPERIENCIAYSITUACION.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NATTIEMPOEXPERIENCIAYSITUACION.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NATTIEMPOEXPERIENCIAYSITUACION.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';        
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



    BEGIN
      MI_TABLA:='NAT_EDUCACION_BASICAYMEDIA';
      MI_VALORES:='NAT_EDUCACION_BASICAYMEDIA.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EDUCACION_BASICAYMEDIA.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_EDUCACION_BASICAYMEDIA.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EDUCACION_BASICAYMEDIA.DATE_MODIFIED =SYSDATE,'||
                  'NAT_EDUCACION_BASICAYMEDIA.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_EDUCACION_BASICAYMEDIA.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EDUCACION_BASICAYMEDIA.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EDUCACION_BASICAYMEDIA.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



     BEGIN
      MI_TABLA:='NAT_EDUCACION_SUPERIOR';
      MI_VALORES:='NAT_EDUCACION_SUPERIOR.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EDUCACION_SUPERIOR.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_EDUCACION_SUPERIOR.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EDUCACION_SUPERIOR.DATE_MODIFIED =SYSDATE,'||
                  'NAT_EDUCACION_SUPERIOR.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_EDUCACION_SUPERIOR.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EDUCACION_SUPERIOR.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EDUCACION_SUPERIOR.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

       BEGIN
      MI_TABLA:='NAT_EXPERIENCIA_LABORAL';
      MI_VALORES:='NAT_EXPERIENCIA_LABORAL.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EXPERIENCIA_LABORAL.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_EXPERIENCIA_LABORAL.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EXPERIENCIA_LABORAL.DATE_MODIFIED =SYSDATE,'||
                  'NAT_EXPERIENCIA_LABORAL.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_EXPERIENCIA_LABORAL.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EXPERIENCIA_LABORAL.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EXPERIENCIA_LABORAL.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


        BEGIN
      MI_TABLA:='NAT_FORMACION_ACADEMICA';
      MI_VALORES:='NAT_FORMACION_ACADEMICA.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_FORMACION_ACADEMICA.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_FORMACION_ACADEMICA.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_FORMACION_ACADEMICA.DATE_MODIFIED =SYSDATE,'||
                  'NAT_FORMACION_ACADEMICA.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_FORMACION_ACADEMICA.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_FORMACION_ACADEMICA.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_FORMACION_ACADEMICA.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




  BEGIN
      MI_TABLA:='NAT_IDIOMAS';
      MI_VALORES:='NAT_IDIOMAS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_IDIOMAS.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_IDIOMAS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_IDIOMAS.DATE_MODIFIED =SYSDATE,'||
                  'NAT_IDIOMAS.MODIFIED_BY   ='''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_IDIOMAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_IDIOMAS.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_IDIOMAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='NAT_OTROS_ESTUDIOS';
      MI_VALORES:='NAT_OTROS_ESTUDIOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_OTROS_ESTUDIOS.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_OTROS_ESTUDIOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_OTROS_ESTUDIOS.DATE_MODIFIED =SYSDATE,'||
                  'NAT_OTROS_ESTUDIOS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_OTROS_ESTUDIOS.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_OTROS_ESTUDIOS.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_OTROS_ESTUDIOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


     BEGIN
      MI_TABLA:='NAT_NOMBRAMIENTO';
      MI_VALORES:='NAT_NOMBRAMIENTO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_NOMBRAMIENTO.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_NOMBRAMIENTO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_NOMBRAMIENTO.DATE_MODIFIED =SYSDATE,'||
                  'NAT_NOMBRAMIENTO.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_NOMBRAMIENTO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_NOMBRAMIENTO.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_NOMBRAMIENTO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


       BEGIN
      MI_TABLA:='NAT_PRIMA_SERVICIOS';
      MI_VALORES:='NAT_PRIMA_SERVICIOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_PRIMA_SERVICIOS.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_PRIMA_SERVICIOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_PRIMA_SERVICIOS.DATE_MODIFIED =SYSDATE,'||
                  'NAT_PRIMA_SERVICIOS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_PRIMA_SERVICIOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_PRIMA_SERVICIOS.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_PRIMA_SERVICIOS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';



      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



         BEGIN
      MI_TABLA:='NAT_PRIMA_TECNICA';
      MI_VALORES:='NAT_PRIMA_TECNICA.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_PRIMA_TECNICA.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_PRIMA_TECNICA.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_PRIMA_TECNICA.DATE_MODIFIED =SYSDATE,'||
                  'NAT_PRIMA_TECNICA.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_PRIMA_TECNICA.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_PRIMA_TECNICA.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_PRIMA_TECNICA.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';    
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='NAT_PUBLICACIONES';
      MI_VALORES:='NAT_PUBLICACIONES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_PUBLICACIONES.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_PUBLICACIONES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','|| 
                  'NAT_PUBLICACIONES.DATE_MODIFIED =SYSDATE,'||
                  'NAT_PUBLICACIONES.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_PUBLICACIONES.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_PUBLICACIONES.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_PUBLICACIONES.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';   
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='NAT_QUINQUENIO';
      MI_VALORES:='NAT_QUINQUENIO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_QUINQUENIO.DP_NUMEDOCU    = ''' || UN_NUE_NIT || ''','||
                  'NAT_QUINQUENIO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_QUINQUENIO.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_QUINQUENIO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_QUINQUENIO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_QUINQUENIO.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_QUINQUENIO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='NAT_REQUISITOS_POSESION';
      MI_VALORES:='NAT_REQUISITOS_POSESION.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_REQUISITOS_POSESION.DP_NUMEDOCU    = ''' || UN_NUE_NIT || ''','||
                  'NAT_REQUISITOS_POSESION.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_REQUISITOS_POSESION.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_REQUISITOS_POSESION.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_REQUISITOS_POSESION.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_REQUISITOS_POSESION.DP_NUMEDOCU  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_REQUISITOS_POSESION.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



 BEGIN
      MI_TABLA:='NAT_SEGURIDAD_SOCIAL';
      MI_VALORES:='NAT_SEGURIDAD_SOCIAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_SEGURIDAD_SOCIAL.DP_NUMEDOCU    = ''' || UN_NUE_NIT || ''','||
                  'NAT_SEGURIDAD_SOCIAL.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_SEGURIDAD_SOCIAL.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_SEGURIDAD_SOCIAL.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_SEGURIDAD_SOCIAL.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_SEGURIDAD_SOCIAL.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_SEGURIDAD_SOCIAL.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




BEGIN
      MI_TABLA:='NAT_SUPRESION_CARGO';
      MI_VALORES:='NAT_SUPRESION_CARGO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_SUPRESION_CARGO.DP_NUMEDOCU    = ''' || UN_NUE_NIT || ''','||
                  'NAT_SUPRESION_CARGO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_SUPRESION_CARGO.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_SUPRESION_CARGO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_SUPRESION_CARGO.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_SUPRESION_CARGO.DP_NUMEDOCU  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_SUPRESION_CARGO.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='NAT_EXPERIENCIA_LABORAL';
      MI_VALORES:='NAT_EXPERIENCIA_LABORAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_EXPERIENCIA_LABORAL.NUMERO_DCTO    = ''' || UN_NUE_NIT || ''','||
                  'NAT_EXPERIENCIA_LABORAL.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_EXPERIENCIA_LABORAL.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_EXPERIENCIA_LABORAL.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_EXPERIENCIA_LABORAL.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_EXPERIENCIA_LABORAL.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_EXPERIENCIA_LABORAL.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



  BEGIN
      MI_TABLA:='NAT_FORMACION_ACADEMICA';
      MI_VALORES:='NAT_FORMACION_ACADEMICA.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_FORMACION_ACADEMICA.NUMERO_DCTO    = ''' || UN_NUE_NIT || ''','||
                  'NAT_FORMACION_ACADEMICA.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_FORMACION_ACADEMICA.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_FORMACION_ACADEMICA.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_FORMACION_ACADEMICA.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_FORMACION_ACADEMICA.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_FORMACION_ACADEMICA.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



  BEGIN
      MI_TABLA:='NAT_IDIOMAS';
      MI_VALORES:='NAT_IDIOMAS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_IDIOMAS.NUMERO_DCTO    = ''' || UN_NUE_NIT || ''','||
                  'NAT_IDIOMAS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_IDIOMAS.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_IDIOMAS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_IDIOMAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_IDIOMAS.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_IDIOMAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='NAT_OTROS_ESTUDIOS';
      MI_VALORES:='NAT_OTROS_ESTUDIOS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_OTROS_ESTUDIOS.NUMERO_DCTO    = ''' || UN_NUE_NIT || ''','||
                  'NAT_OTROS_ESTUDIOS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_OTROS_ESTUDIOS.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_OTROS_ESTUDIOS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_OTROS_ESTUDIOS.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_OTROS_ESTUDIOS.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_OTROS_ESTUDIOS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




   BEGIN
      MI_TABLA:='NAT_NOMBRAMIENTO';
      MI_VALORES:='NAT_NOMBRAMIENTO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_NOMBRAMIENTO.DP_NUMEDOCU    = ''' || UN_NUE_NIT || ''','||
                  'NAT_NOMBRAMIENTO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_NOMBRAMIENTO.DATE_MODIFIED  =SYSDATE,'||
                  'NAT_NOMBRAMIENTO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_NOMBRAMIENTO.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_NOMBRAMIENTO.DP_NUMEDOCU  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_NOMBRAMIENTO.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
        MI_TABLA  :='ORDENADOR';
        MI_CAMPOS:='COMPANIA,'||
                   'CEDULA,'||
                   'SUCURSAL,'||
                   'NOMBRE,'||
                   'NOMBRAMIENTO,'||
                   'CARGO,'||
                   'EXPEDIDACEDULAORDENADOR,'||
                   'IND_ACTIVO,'||
                   'USUARIO,'||
                   'DATE_CREATED,'||
                   'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''' ,'||
                      '''' || UN_NUE_NIT || ''' ,'||
                      '''' || UN_NUE_SUCURSAL || ''' ,'||
                      'NOMBRE,'||
                      'NOMBRAMIENTO,'||
                      'CARGO,'||
                      'EXPEDIDACEDULAORDENADOR,'||
                      'IND_ACTIVO,'||
                      'USUARIO,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||    
                      ' FROM ORDENADOR'||
                      ' WHERE ORDENADOR.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND ORDENADOR.CEDULA     = ''' || UN_ANT_NIT || ''''||
                      ' AND ORDENADOR.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';                                

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 



    BEGIN
      MI_TABLA:='COMPROBANTE_CNT';
      MI_VALORES:='COMPROBANTE_CNT.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'COMPROBANTE_CNT.ORDENADOR          = ''' || UN_NUE_NIT || ''','||
                  'COMPROBANTE_CNT.ORDENADORSUCURSAL  = ''' || UN_NUE_SUCURSAL || ''','||
                  'COMPROBANTE_CNT.DATE_MODIFIED      =SYSDATE,'||
                  'COMPROBANTE_CNT.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='COMPROBANTE_CNT.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_CNT.ORDENADOR         = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_CNT.ORDENADORSUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 


  BEGIN
      MI_TABLA:='COMPROBANTE_CNT';
      MI_VALORES:='COMPROBANTE_CNT.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'COMPROBANTE_CNT.ORDENADOR      = ''' || UN_NUE_NIT || ''','||
                  'COMPROBANTE_CNT.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'COMPROBANTE_CNT.DATE_MODIFIED  =SYSDATE,'||
                  'COMPROBANTE_CNT.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='COMPROBANTE_CNT.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_CNT.ORDENADOR = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_CNT.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='NOVEDADCONTRATO';
      MI_VALORES:='NOVEDADCONTRATO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'NOVEDADCONTRATO.ORDENADOR      = ''' || UN_NUE_NIT || ''','||
                  'NOVEDADCONTRATO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'NOVEDADCONTRATO.DATE_MODIFIED  =SYSDATE,'||
                  'NOVEDADCONTRATO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NOVEDADCONTRATO.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NOVEDADCONTRATO.ORDENADOR = ''' || UN_ANT_NIT || ''''||
                    ' AND NOVEDADCONTRATO.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.ORDENADOR          = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSAL_ORDENADOR = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED      =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.ORDENADOR           = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSAL_ORDENADOR  = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.CEDULACONTRATISTA   = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSALCONTRATISTA = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED       =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY         = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.CEDULACONTRATISTA   = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSALCONTRATISTA = ''' || UN_ANT_SUCURSAL || '''';



      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.CEDULAINTERVENTOR   = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSALINTERVENTOR = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED       =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY         = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.CEDULAINTERVENTOR   = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSALINTERVENTOR = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



   BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.NITCESION      = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSALCESION = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED  =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA            = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.NITCESION      = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSALCESION = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


     BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED  =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


       BEGIN
      MI_TABLA:='ORDENPDESERVICIOS';
      MI_VALORES:='ORDENPDESERVICIOS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENPDESERVICIOS.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'ORDENPDESERVICIOS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENPDESERVICIOS.DATE_MODIFIED  =SYSDATE,'||
                  'ORDENPDESERVICIOS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENPDESERVICIOS.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENPDESERVICIOS.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENPDESERVICIOS.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



        BEGIN
      MI_TABLA:='PERSONAL';
      MI_VALORES:='PERSONAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'PERSONAL.NUMERO_DCTO    = ''' || UN_NUE_NIT || ''','||
                  'PERSONAL.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'PERSONAL.DATE_MODIFIED  =SYSDATE,'||
                  'PERSONAL.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PERSONAL.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PERSONAL.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND PERSONAL.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

      BEGIN
      MI_TABLA:='DETALLE_PROFESIONES';
      MI_VALORES:='DETALLE_PROFESIONES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'DETALLE_PROFESIONES.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'DETALLE_PROFESIONES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'DETALLE_PROFESIONES.DATE_MODIFIED = SYSDATE,'||
                  'DETALLE_PROFESIONES.MODIFIED_BY   = '''||UN_USUARIO||'''';  

      MI_CONDICION:='DETALLE_PROFESIONES.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DETALLE_PROFESIONES.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND DETALLE_PROFESIONES.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';



  BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

        BEGIN
      MI_TABLA:='PLANACNTWEB';
      MI_VALORES:='PLANACNTWEB.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'PLANACNTWEB.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'PLANACNTWEB.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'PLANACNTWEB.DATE_MODIFIED   =SYSDATE,'||
                  'PLANACNTWEB.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PLANACNTWEB.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PLANACNTWEB.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND PLANACNTWEB.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


       BEGIN
      MI_TABLA:='PROCEDENCIA_TRAMITE';
      MI_VALORES:='PROCEDENCIA_TRAMITE.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'PROCEDENCIA_TRAMITE.NIT            = ''' || UN_NUE_NIT || ''','||
                  'PROCEDENCIA_TRAMITE.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROCEDENCIA_TRAMITE.DATE_MODIFIED  =SYSDATE,'||
                  'PROCEDENCIA_TRAMITE.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROCEDENCIA_TRAMITE.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROCEDENCIA_TRAMITE.NIT       = ''' || UN_ANT_NIT || ''''||
                    ' AND PROCEDENCIA_TRAMITE.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='PROCEDENCIA_TRAMITE';
      MI_VALORES:='PROCEDENCIA_TRAMITE.COMPANIA               = ''' || UN_NUE_COMPANIA || ''','||
                  'PROCEDENCIA_TRAMITE.REPRESENTANTELEGAL     = ''' || UN_NUE_NIT || ''','||
                  'PROCEDENCIA_TRAMITE.SUCURSAL_REPRESENTANTE = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROCEDENCIA_TRAMITE.DATE_MODIFIED          =SYSDATE,'||
                  'PROCEDENCIA_TRAMITE.MODIFIED_BY            = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROCEDENCIA_TRAMITE.COMPANIA                    = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROCEDENCIA_TRAMITE.REPRESENTANTELEGAL     = ''' || UN_ANT_NIT || ''''||
                    ' AND PROCEDENCIA_TRAMITE.SUCURSAL_REPRESENTANTE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



   BEGIN
        MI_TABLA  :='PROPONENTE';
        MI_CAMPOS:= 'COMPANIA,'||
                    'PROPONENTE,'||
                    'SUCURSAL,'||
                    'TIPOCONTRATO,'||
                    'TRANSACCION,'||
                    'CONSECUTIVODETALLE,'||
                    'FECHAINSCRIPCION,'||
                    'DOCUMENTOSOPORTE,'||
                    'ESTADO,'||
                    'VALORP,'||
                    'FOLIO,'||
                    'NOMBRECOMPLETO,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''' ,'||
                     '''' || UN_NUE_NIT || ''' ,'||
                     '''' || UN_NUE_SUCURSAL || ''','||
                     'TIPOCONTRATO,'||
                     'TRANSACCION,'||
                     'CONSECUTIVODETALLE,'||
                     'FECHAINSCRIPCION,'||
                     'DOCUMENTOSOPORTE,'||
                     'ESTADO,'||
                     'VALORP,'||
                     'FOLIO,'||
                     'NOMBRECOMPLETO,'||
                     'SYSDATE,'||
                     ''''||UN_USUARIO||''''||             
                     ' FROM PROPONENTE'||
                     ' WHERE PROPONENTE.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                     ' AND PROPONENTE.PROPONENTE = ''' || UN_ANT_NIT || ''''||
                     ' AND PROPONENTE.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';


          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 



BEGIN
      MI_TABLA:='OBSERVACIONES';
      MI_VALORES:='OBSERVACIONES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'OBSERVACIONES.PROPONENTE    = ''' || UN_NUE_NIT || ''','||
                  'OBSERVACIONES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'OBSERVACIONES.DATE_MODIFIED =SYSDATE,'||
                  'OBSERVACIONES.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='OBSERVACIONES.COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND OBSERVACIONES.PROPONENTE = ''' || UN_ANT_NIT || ''''||
                    ' AND OBSERVACIONES.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


BEGIN
      MI_TABLA:='PRERREQUISITOS_PROPONENTE';
      MI_VALORES:='PRERREQUISITOS_PROPONENTE.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'PRERREQUISITOS_PROPONENTE.PROPONENTE    = ''' || UN_NUE_NIT || ''','||
                  'PRERREQUISITOS_PROPONENTE.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'PRERREQUISITOS_PROPONENTE.DATE_MODIFIED =SYSDATE,'||
                  'PRERREQUISITOS_PROPONENTE.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PRERREQUISITOS_PROPONENTE.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PRERREQUISITOS_PROPONENTE.PROPONENTE  = ''' || UN_ANT_NIT || ''''||
                    ' AND PRERREQUISITOS_PROPONENTE.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



BEGIN
      MI_TABLA:='PROPONENTE_ITEMINVENTARIO';
      MI_VALORES:='PROPONENTE_ITEMINVENTARIO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'PROPONENTE_ITEMINVENTARIO.PROPONENTE    = ''' || UN_NUE_NIT || ''','||
                  'PROPONENTE_ITEMINVENTARIO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROPONENTE_ITEMINVENTARIO.DATE_MODIFIED =SYSDATE,'||
                  'PROPONENTE_ITEMINVENTARIO.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROPONENTE_ITEMINVENTARIO.COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROPONENTE_ITEMINVENTARIO.PROPONENTE = ''' || UN_ANT_NIT || ''''||
                    ' AND PROPONENTE_ITEMINVENTARIO.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;





BEGIN
      MI_TABLA:='VLR_VARIABLE_PROPONENTE';
      MI_VALORES:='VLR_VARIABLE_PROPONENTE.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'VLR_VARIABLE_PROPONENTE.PROPONENTE    = ''' || UN_NUE_NIT || ''','||
                  'VLR_VARIABLE_PROPONENTE.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'VLR_VARIABLE_PROPONENTE.DATE_MODIFIED =SYSDATE,'||
                  'VLR_VARIABLE_PROPONENTE.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='VLR_VARIABLE_PROPONENTE.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND VLR_VARIABLE_PROPONENTE.PROPONENTE  = ''' || UN_ANT_NIT || ''''||
                    ' AND VLR_VARIABLE_PROPONENTE.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


BEGIN
      MI_TABLA:='PROPUESTA';
      MI_VALORES:='PROPUESTA.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'PROPUESTA.NIT           = ''' || UN_NUE_NIT || ''','||
                  'PROPUESTA.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROPUESTA.DATE_MODIFIED =SYSDATE,'||
                  'PROPUESTA.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROPUESTA.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROPUESTA.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND PROPUESTA.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


BEGIN
      MI_TABLA:='PROVEEDOR';
      MI_VALORES:='PROVEEDOR.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'PROVEEDOR.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'PROVEEDOR.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROVEEDOR.DATE_MODIFIED =SYSDATE,'||
                  'PROVEEDOR.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROVEEDOR.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROVEEDOR.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND PROVEEDOR.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



BEGIN
      MI_TABLA:='NAT_PUBLICACIONES';
      MI_VALORES:='NAT_PUBLICACIONES.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_PUBLICACIONES.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_PUBLICACIONES.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_PUBLICACIONES.DATE_MODIFIED =SYSDATE,'||
                  'NAT_PUBLICACIONES.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_PUBLICACIONES.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_PUBLICACIONES.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_PUBLICACIONES.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



BEGIN
      MI_TABLA:='NAT_REPRESENTANTE';
      MI_VALORES:='NAT_REPRESENTANTE.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_REPRESENTANTE.NUMERO_DCTO   = ''' || UN_NUE_NIT || ''','||
                  'NAT_REPRESENTANTE.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_REPRESENTANTE.DATE_MODIFIED =SYSDATE,'||
                  'NAT_REPRESENTANTE.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_REPRESENTANTE.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_REPRESENTANTE.NUMERO_DCTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_REPRESENTANTE.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


BEGIN
      MI_TABLA:='NAT_REQUISITOS_POSESION';
      MI_VALORES:='NAT_REQUISITOS_POSESION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_REQUISITOS_POSESION.DP_NUMEDOCU   = ''' || UN_NUE_NIT || ''','||
                  'NAT_REQUISITOS_POSESION.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_REQUISITOS_POSESION.DATE_MODIFIED =SYSDATE,'||
                  'NAT_REQUISITOS_POSESION.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_REQUISITOS_POSESION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_REQUISITOS_POSESION.DP_NUMEDOCU = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_REQUISITOS_POSESION.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  --20180607_1400:@eamaya Se agrga validación para cuando el nuevo nit ya exista en la tabla RESPONSABLE

  BEGIN
    MI_STRSQL := 'SELECT ''X'' EXISTE    
                  FROM RESPONSABLE
                  WHERE RESPONSABLE.COMPANIA   = ''' || UN_NUE_COMPANIA || ''''||
                  ' AND RESPONSABLE.CEDULA     = ''' || UN_NUE_NIT || ''''||
                  ' AND RESPONSABLE.SUCURSAL   = ''' || UN_NUE_SUCURSAL || ''''; 

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;

       EXCEPTION WHEN NO_DATA_FOUND THEN 

             BEGIN
                  MI_TABLA  :='RESPONSABLE';
                  MI_CAMPOS:= 'COMPANIA,'||
                              'CEDULA,'||
                              'SUCURSAL,'||
                              'CARGO,'||
                              'ACTIVO,'||
                              'DATE_CREATED,'||
                              'CREATED_BY';       
                  MI_CONSULTA:='SELECT '||
                               '''' || UN_NUE_COMPANIA || ''','||
                               '''' || UN_NUE_NIT || ''','||
                               '''' || UN_NUE_SUCURSAL || ''','||
                               'CARGO,'||
                               'ACTIVO,'||
                               'SYSDATE,'||
                               ''''||UN_USUARIO||''''||                  
                               ' FROM RESPONSABLE'||
                               ' WHERE RESPONSABLE.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                               ' AND RESPONSABLE.CEDULA     = ''' || UN_ANT_NIT || ''''||
                               ' AND RESPONSABLE.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';                    


                    BEGIN
                        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'IS',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_CONSULTA);
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
                    END;         

                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                               MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                          'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                          'NIT = ''' || UN_ANT_NIT || ''','|| 
                                          'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                               MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                               MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                               MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                               MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                          UN_TABLAERROR  => MI_TABLA,
                                                          UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                          UN_REEMPLAZOS  => MI_REEMPLAZOS);
            END; 

   END;

 --20180607_1400:@eamaya FIN  
  BEGIN
      MI_TABLA:='ADICIONES';
      MI_VALORES:='ADICIONES.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'ADICIONES.RESPONSABLE_ORDENA = ''' || UN_NUE_NIT || ''','||
                  'ADICIONES.SUCURSAL           = ''' || UN_NUE_SUCURSAL || ''','||
                  'ADICIONES.DATE_MODIFIED      =SYSDATE,'||
                  'ADICIONES.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ADICIONES.COMPANIA                = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ADICIONES.RESPONSABLE_ORDENA = ''' || UN_ANT_NIT || ''''||
                    ' AND ADICIONES.SUCURSAL           = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  

     BEGIN
        MI_TABLA  :='DEPENDENCIA_RESPONSABLE';
        MI_CAMPOS:= 'COMPANIA,'||
                    'RESPONSABLE,'||
                    'SUCURSAL,'||
                    'DEPENDENCIA,'||
                    'CARGO,'||
                    'RESPONSABLEALMACEN,'||
                    'JEFEUNIDAD,'||
                    'ACTIVO_RECEP,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''' ,'||
                     '''' || UN_NUE_NIT || ''' ,'||
                     '''' || UN_NUE_SUCURSAL || ''' ,'||
                     'DEPENDENCIA,'||
                     'CARGO,'||
                     'RESPONSABLEALMACEN,'||
                     'JEFEUNIDAD,'||
                     'ACTIVO_RECEP,'||
                     'SYSDATE,'||
                     ''''||UN_USUARIO||''''||   
                     ' FROM DEPENDENCIA_RESPONSABLE'||
                     ' WHERE DEPENDENCIA_RESPONSABLE.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                     ' AND DEPENDENCIA_RESPONSABLE.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                     ' AND DEPENDENCIA_RESPONSABLE.SUCURSAL    = ''' || UN_ANT_SUCURSAL || ''' ';                             


          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 



    BEGIN
      MI_TABLA:='VI_VIATICOS';
      MI_VALORES:='VI_VIATICOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'VI_VIATICOS.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'VI_VIATICOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'VI_VIATICOS.DATE_MODIFIED =SYSDATE,'||
                  'VI_VIATICOS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='VI_VIATICOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND VI_VIATICOS.TERCERO     = ''' || UN_ANT_NIT || ''''||
                    ' AND VI_VIATICOS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  

   BEGIN
      MI_TABLA:='BPNOVEDADPROYECTO';
      MI_VALORES:='BPNOVEDADPROYECTO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'BPNOVEDADPROYECTO.RESPONSABLE   = ''' || UN_NUE_NIT || ''','||
                  'BPNOVEDADPROYECTO.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'BPNOVEDADPROYECTO.DATE_MODIFIED =SYSDATE,'||
                  'BPNOVEDADPROYECTO.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='BPNOVEDADPROYECTO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND BPNOVEDADPROYECTO.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND BPNOVEDADPROYECTO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  


    BEGIN
      MI_TABLA:='BPNOVEDADPROYECTO';
      MI_VALORES:='BPNOVEDADPROYECTO.COMPANIA          = ''' || UN_NUE_COMPANIA || ''','||
                  'BPNOVEDADPROYECTO.RESPONSABLE_REVIS = ''' || UN_NUE_NIT || ''','||
                  'BPNOVEDADPROYECTO.SUCURSAL_REVIS    = ''' || UN_NUE_SUCURSAL || ''','||
                  'BPNOVEDADPROYECTO.DATE_MODIFIED     =SYSDATE,'||
                  'BPNOVEDADPROYECTO.MODIFIED_BY       = '''||UN_USUARIO||''''; 

      MI_CONDICION:='BPNOVEDADPROYECTO.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND BPNOVEDADPROYECTO.RESPONSABLE_REVIS = ''' || UN_ANT_NIT || ''''||
                    ' AND BPNOVEDADPROYECTO.SUCURSAL_REVIS    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  

      BEGIN
      MI_TABLA:='BPRESPONSABLEPROYECTO';
      MI_VALORES:='BPRESPONSABLEPROYECTO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'BPRESPONSABLEPROYECTO.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'BPRESPONSABLEPROYECTO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'BPRESPONSABLEPROYECTO.DATE_MODIFIED  =SYSDATE,'||
                  'BPRESPONSABLEPROYECTO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='BPRESPONSABLEPROYECTO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND BPRESPONSABLEPROYECTO.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND BPRESPONSABLEPROYECTO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='BP_PLAN_INDICATIVO';
      MI_VALORES:='BP_PLAN_INDICATIVO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'BP_PLAN_INDICATIVO.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'BP_PLAN_INDICATIVO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'BP_PLAN_INDICATIVO.DATE_MODIFIED  =SYSDATE,'||
                  'BP_PLAN_INDICATIVO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='BP_PLAN_INDICATIVO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND BP_PLAN_INDICATIVO.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND BP_PLAN_INDICATIVO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='CERTIFICADO_PLAN_COMPRAS';
      MI_VALORES:='CERTIFICADO_PLAN_COMPRAS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'CERTIFICADO_PLAN_COMPRAS.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'CERTIFICADO_PLAN_COMPRAS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'CERTIFICADO_PLAN_COMPRAS.DATE_MODIFIED  =SYSDATE,'||
                  'CERTIFICADO_PLAN_COMPRAS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='CERTIFICADO_PLAN_COMPRAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND CERTIFICADO_PLAN_COMPRAS.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND CERTIFICADO_PLAN_COMPRAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;




   BEGIN
      MI_TABLA:='COMPROBANTE_PPTAL';
      MI_VALORES:='COMPROBANTE_PPTAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'COMPROBANTE_PPTAL.CODSOLICITANTE = ''' || UN_NUE_NIT || ''','||
                  'COMPROBANTE_PPTAL.SUCSOLICITANTE = ''' || UN_NUE_SUCURSAL || ''','||
                  'COMPROBANTE_PPTAL.DATE_MODIFIED  =SYSDATE,'||
                  'COMPROBANTE_PPTAL.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='COMPROBANTE_PPTAL.COMPANIA            = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND COMPROBANTE_PPTAL.CODSOLICITANTE = ''' || UN_ANT_NIT || ''''||
                    ' AND COMPROBANTE_PPTAL.SUCSOLICITANTE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

   BEGIN

      MI_TABLA:='DETALLE_COMPROBANTE_CNT';
      MI_VALORES:='DETALLE_COMPROBANTE_CNT.COMPANIA          = ''' || UN_NUE_COMPANIA || ''','||
                  'DETALLE_COMPROBANTE_CNT.D_RESPONSABLECNT  = ''' || UN_NUE_NIT || ''','||
                  'DETALLE_COMPROBANTE_CNT.D_RESPSUCURSALCNT = ''' || UN_NUE_SUCURSAL || ''','||
                  'DETALLE_COMPROBANTE_CNT.DATE_MODIFIED     =SYSDATE,'||
                  'DETALLE_COMPROBANTE_CNT.MODIFIED_BY       = '''||UN_USUARIO||''''; 

      MI_CONDICION:='DETALLE_COMPROBANTE_CNT.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.D_RESPONSABLECNT  = ''' || UN_ANT_NIT || ''''||
                    ' AND DETALLE_COMPROBANTE_CNT.D_RESPSUCURSALCNT = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



   BEGIN
      MI_TABLA:='DETALLE_PLAN_COMPRAS';
      MI_VALORES:='DETALLE_PLAN_COMPRAS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'DETALLE_PLAN_COMPRAS.RESPONSABLE   = ''' || UN_NUE_NIT || ''','||
                  'DETALLE_PLAN_COMPRAS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'DETALLE_PLAN_COMPRAS.DATE_MODIFIED =SYSDATE,'||
                  'DETALLE_PLAN_COMPRAS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='DETALLE_PLAN_COMPRAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''|| 
                    ' AND DETALLE_PLAN_COMPRAS.RESPONSABLE = ''' || UN_ANT_NIT || ''''||   
                    ' AND DETALLE_PLAN_COMPRAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='DEVOLUTIVO';
      MI_VALORES:='DEVOLUTIVO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'DEVOLUTIVO.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'DEVOLUTIVO.DATE_MODIFIED        =SYSDATE,'||
                  'DEVOLUTIVO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='DEVOLUTIVO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DEVOLUTIVO.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='ELEMENTOBODEGA';
      MI_VALORES:='ELEMENTOBODEGA.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ELEMENTOBODEGA.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ELEMENTOBODEGA.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ELEMENTOBODEGA.DATE_MODIFIED        =SYSDATE,'||
                  'ELEMENTOBODEGA.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ELEMENTOBODEGA.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ELEMENTOBODEGA.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ELEMENTOBODEGA.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='ES_ESTPREVIO';
      MI_VALORES:='ES_ESTPREVIO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ESTPREVIO.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ES_ESTPREVIO.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ESTPREVIO.DATE_MODIFIED        =SYSDATE,'||
                  'ES_ESTPREVIO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_ESTPREVIO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ESTPREVIO.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ESTPREVIO.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='ES_ITEMS_E';
      MI_VALORES:='ES_ITEMS_E.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ITEMS_E.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ES_ITEMS_E.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ITEMS_E.DATE_MODIFIED        =SYSDATE,'||
                  'ES_ITEMS_E.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_ITEMS_E.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ITEMS_E.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ITEMS_E.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='MANTENIMIENTO';
      MI_VALORES:='MANTENIMIENTO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'MANTENIMIENTO.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'MANTENIMIENTO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'MANTENIMIENTO.DATE_MODIFIED  =SYSDATE,'||
                  'MANTENIMIENTO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='MANTENIMIENTO.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MANTENIMIENTO.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND MANTENIMIENTO.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='ORDENDESUMINISTRO';
      MI_VALORES:='ORDENDESUMINISTRO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDESUMINISTRO.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'ORDENDESUMINISTRO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDESUMINISTRO.DATE_MODIFIED  =SYSDATE,'||
                  'ORDENDESUMINISTRO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDESUMINISTRO.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDESUMINISTRO.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDESUMINISTRO.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='PI_TRANSACCION';
      MI_VALORES:='PI_TRANSACCION.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'PI_TRANSACCION.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'PI_TRANSACCION.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'PI_TRANSACCION.DATE_MODIFIED  =SYSDATE,'||
                  'PI_TRANSACCION.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PI_TRANSACCION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PI_TRANSACCION.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND PI_TRANSACCION.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


     BEGIN
      MI_TABLA:='PLAN_DE_COMPRAS';
      MI_VALORES:='PLAN_DE_COMPRAS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'PLAN_DE_COMPRAS.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'PLAN_DE_COMPRAS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'PLAN_DE_COMPRAS.DATE_MODIFIED  =SYSDATE,'||
                  'PLAN_DE_COMPRAS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PLAN_DE_COMPRAS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PLAN_DE_COMPRAS.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND PLAN_DE_COMPRAS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

   BEGIN
      MI_TABLA:='PROYECTOS';
      MI_VALORES:='PROYECTOS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'PROYECTOS.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'PROYECTOS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROYECTOS.DATE_MODIFIED  =SYSDATE,'||
                  'PROYECTOS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROYECTOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROYECTOS.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND PROYECTOS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='PROYECTOS';
      MI_VALORES:='PROYECTOS.COMPANIA          = ''' || UN_NUE_COMPANIA || ''','||
                  'PROYECTOS.RESPONSABLE_RADIC = ''' || UN_NUE_NIT || ''','||
                  'PROYECTOS.SUCURSAL_RADIC    = ''' || UN_NUE_SUCURSAL || ''','||
                  'PROYECTOS.DATE_MODIFIED     =SYSDATE,'||
                  'PROYECTOS.MODIFIED_BY       = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PROYECTOS.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROYECTOS.RESPONSABLE_RADIC = ''' || UN_ANT_NIT || ''''||
                    ' AND PROYECTOS.SUCURSAL_RADIC    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='REQUISICION';
      MI_VALORES:='REQUISICION.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'REQUISICION.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'REQUISICION.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'REQUISICION.DATE_MODIFIED  =SYSDATE,'||
                  'REQUISICION.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='REQUISICION.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND REQUISICION.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND REQUISICION.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='RESPONSABLE_VISITA';
      MI_VALORES:='RESPONSABLE_VISITA.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'RESPONSABLE_VISITA.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'RESPONSABLE_VISITA.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'RESPONSABLE_VISITA.DATE_MODIFIED  =SYSDATE,'||
                  'RESPONSABLE_VISITA.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='RESPONSABLE_VISITA.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESPONSABLE_VISITA.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND RESPONSABLE_VISITA.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='DEVOLUTIVO';
      MI_VALORES:='DEVOLUTIVO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'DEVOLUTIVO.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'DEVOLUTIVO.DATE_MODIFIED        =SYSDATE,'||
                  'DEVOLUTIVO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='DEVOLUTIVO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DEVOLUTIVO.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='D_ORDENDECOMPRA';
      MI_VALORES:='D_ORDENDECOMPRA.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'D_ORDENDECOMPRA.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'D_ORDENDECOMPRA.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'D_ORDENDECOMPRA.DATE_MODIFIED        =SYSDATE,'||
                  'D_ORDENDECOMPRA.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='D_ORDENDECOMPRA.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND D_ORDENDECOMPRA.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND D_ORDENDECOMPRA.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



    BEGIN
      MI_TABLA:='ELEMENTOBODEGA';
      MI_VALORES:='ELEMENTOBODEGA.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ELEMENTOBODEGA.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ELEMENTOBODEGA.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ELEMENTOBODEGA.DATE_MODIFIED        =SYSDATE,'||
                  'ELEMENTOBODEGA.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ELEMENTOBODEGA.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ELEMENTOBODEGA.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ELEMENTOBODEGA.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

     BEGIN
      MI_TABLA:='ES_ASP_ESTPR';
      MI_VALORES:='ES_ASP_ESTPR.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ASP_ESTPR.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ES_ASP_ESTPR.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ASP_ESTPR.DATE_MODIFIED        =SYSDATE,'||
                  'ES_ASP_ESTPR.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_ASP_ESTPR.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ASP_ESTPR.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ASP_ESTPR.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='ES_DETA_ESTPR';
      MI_VALORES:='ES_DETA_ESTPR.COMPANIA         = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_DETA_ESTPR.RESP_ENTREGA     = ''' || UN_NUE_NIT || ''','||
                  'ES_DETA_ESTPR.SUCURSAL_ENTREGA = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_DETA_ESTPR.DATE_MODIFIED    =SYSDATE,'||
                  'ES_DETA_ESTPR.MODIFIED_BY      = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_DETA_ESTPR.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_DETA_ESTPR.RESP_ENTREGA      = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_DETA_ESTPR.SUCURSAL_ENTREGA  = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='ES_DETA_ESTPR';
      MI_VALORES:='ES_DETA_ESTPR.COMPANIA          = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_DETA_ESTPR.RESP_RECIBIDO     = ''' || UN_NUE_NIT || ''','||
                  'ES_DETA_ESTPR.SUCURSAL_RECIBIDO = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_DETA_ESTPR.DATE_MODIFIED     =SYSDATE,'||
                  'ES_DETA_ESTPR.MODIFIED_BY       = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_DETA_ESTPR.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_DETA_ESTPR.RESP_RECIBIDO       = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_DETA_ESTPR.SUCURSAL_RECIBIDO   = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



  BEGIN
      MI_TABLA:='ES_ESTPREVIO';
      MI_VALORES:='ES_ESTPREVIO.COMPANIA              = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_ESTPREVIO.SUPERVISADO           = ''' || UN_NUE_NIT || ''','||
                  'ES_ESTPREVIO.SUCURSAL_SUPERVISADO  = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_ESTPREVIO.DATE_MODIFIED         =SYSDATE,'||
                  'ES_ESTPREVIO.MODIFIED_BY           = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_ESTPREVIO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_ESTPREVIO.SUPERVISADO          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_ESTPREVIO.SUCURSAL_SUPERVISADO = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='ES_RIES_ESTPR';
      MI_VALORES:='ES_RIES_ESTPR.COMPANIA               = ''' || UN_NUE_COMPANIA || ''','||
                  'ES_RIES_ESTPR.RESP_RECIBIDO          = ''' || UN_NUE_NIT || ''','||
                  'ES_RIES_ESTPR.SUCURSAL_RESP_RECIBIDO = ''' || UN_NUE_SUCURSAL || ''','||
                  'ES_RIES_ESTPR.DATE_MODIFIED          =SYSDATE,'||
                  'ES_RIES_ESTPR.MODIFIED_BY            = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ES_RIES_ESTPR.COMPANIA                    = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ES_RIES_ESTPR.RESP_RECIBIDO          = ''' || UN_ANT_NIT || ''''||
                    ' AND ES_RIES_ESTPR.SUCURSAL_RESP_RECIBIDO = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

 BEGIN
      MI_TABLA:='NAT_JURIDENTIFICACION';
      MI_VALORES:='NAT_JURIDENTIFICACION.COMPANIA               = ''' || UN_NUE_COMPANIA || ''','||
                  'NAT_JURIDENTIFICACION.RESPONSABLE            = ''' || UN_NUE_NIT || ''','||
                  'NAT_JURIDENTIFICACION.SUCURSAL_RESPONSABLE   = ''' || UN_NUE_SUCURSAL || ''','||
                  'NAT_JURIDENTIFICACION.DATE_MODIFIED          =SYSDATE,'||
                  'NAT_JURIDENTIFICACION.MODIFIED_BY            = '''||UN_USUARIO||''''; 

      MI_CONDICION:='NAT_JURIDENTIFICACION.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_JURIDENTIFICACION.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_JURIDENTIFICACION.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='MOVIMIENTO';
      MI_VALORES:='MOVIMIENTO.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'MOVIMIENTO.RESPONSABLE_DESTINO = ''' || UN_NUE_NIT || ''','||
                  'MOVIMIENTO.SUCURSAL_RESDESTINO = ''' || UN_NUE_SUCURSAL || ''','||
                  'MOVIMIENTO.DATE_MODIFIED       =SYSDATE,'||
                  'MOVIMIENTO.MODIFIED_BY         = '''||UN_USUARIO||''''; 

      MI_CONDICION:='MOVIMIENTO.COMPANIA                 = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MOVIMIENTO.RESPONSABLE_DESTINO = ''' || UN_ANT_NIT || ''''||
                    ' AND MOVIMIENTO.SUCURSAL_RESDESTINO = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



 BEGIN
      MI_TABLA:='MOVIMIENTO';
      MI_VALORES:='MOVIMIENTO.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'MOVIMIENTO.RESPONSABLE_ORIGEN  = ''' || UN_NUE_NIT || ''','||
                  'MOVIMIENTO.SUCURSAL_RESORIGEN  = ''' || UN_NUE_SUCURSAL || ''','||
                  'MOVIMIENTO.DATE_MODIFIED       =SYSDATE,'||
                  'MOVIMIENTO.MODIFIED_BY         = '''||UN_USUARIO||''''; 

      MI_CONDICION:='MOVIMIENTO.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MOVIMIENTO.RESPONSABLE_ORIGEN = ''' || UN_ANT_NIT || ''''||
                    ' AND MOVIMIENTO.SUCURSAL_RESORIGEN = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='ORDENDECOMPRA';
      MI_VALORES:='ORDENDECOMPRA.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'ORDENDECOMPRA.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'ORDENDECOMPRA.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'ORDENDECOMPRA.DATE_MODIFIED        =SYSDATE,'||
                  'ORDENDECOMPRA.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='ORDENDECOMPRA.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENDECOMPRA.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENDECOMPRA.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='PLACAS_SINMOVIMIENTO';
      MI_VALORES:='PLACAS_SINMOVIMIENTO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'PLACAS_SINMOVIMIENTO.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'PLACAS_SINMOVIMIENTO.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'PLACAS_SINMOVIMIENTO.DATE_MODIFIED        =SYSDATE,'||
                  'PLACAS_SINMOVIMIENTO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PLACAS_SINMOVIMIENTO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PLACAS_SINMOVIMIENTO.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND PLACAS_SINMOVIMIENTO.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='PREDIOS';
      MI_VALORES:='PREDIOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'PREDIOS.RESPONSABLE   = ''' || UN_NUE_NIT || ''','||
                  'PREDIOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'PREDIOS.DATE_MODIFIED =SYSDATE,'||
                  'PREDIOS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PREDIOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PREDIOS.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND PREDIOS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='RESPONSABILIDADES';
      MI_VALORES:='RESPONSABILIDADES.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'RESPONSABILIDADES.RESPONSABLE          = ''' || UN_NUE_NIT || ''','||
                  'RESPONSABILIDADES.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'RESPONSABILIDADES.DATE_MODIFIED        =SYSDATE,'||
                  'RESPONSABILIDADES.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='RESPONSABILIDADES.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESPONSABILIDADES.RESPONSABLE          = ''' || UN_ANT_NIT || ''''||
                    ' AND RESPONSABILIDADES.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

   BEGIN
      MI_TABLA:='TRANSACCION_DEVOLUTIVO';
      MI_VALORES:='TRANSACCION_DEVOLUTIVO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'TRANSACCION_DEVOLUTIVO.CEDULA_RESPONSABLE   = ''' || UN_NUE_NIT || ''','||
                  'TRANSACCION_DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_NUE_SUCURSAL || ''','||
                  'TRANSACCION_DEVOLUTIVO.DATE_MODIFIED        =SYSDATE,'||
                  'TRANSACCION_DEVOLUTIVO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TRANSACCION_DEVOLUTIVO.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TRANSACCION_DEVOLUTIVO.CEDULA_RESPONSABLE   = ''' || UN_ANT_NIT || ''''||
                    ' AND TRANSACCION_DEVOLUTIVO.SUCURSAL_RESPONSABLE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='VIAS';
      MI_VALORES:='VIAS.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'VIAS.RESPONSABLE    = ''' || UN_NUE_NIT || ''','||
                  'VIAS.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'VIAS.DATE_MODIFIED  =SYSDATE,'||
                  'VIAS.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='VIAS.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND VIAS.RESPONSABLE  = ''' || UN_ANT_NIT || ''''||
                    ' AND VIAS.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
        MI_TABLA  :='RESPONSABLES_TALLERES';
        MI_CAMPOS:= 'COMPANIA,'||
                    'NIT,'||
                    'SUCURSAL,'||
                    'NOMBRE,'||
                    'TALLER,'||
                    'SUCURSAL_TALLER,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:='SELECT '||
                     '''' || UN_NUE_COMPANIA || ''','||
                     '''' || UN_NUE_NIT || ''','||   
                     '''' || UN_NUE_SUCURSAL || ''','||
                     'NOMBRE,'||
                     'TALLER,'||
                     'SUCURSAL_TALLER,'||
                     'SYSDATE,'||
                     ''''||UN_USUARIO||''''||                       
                     ' FROM RESPONSABLES_TALLERES'||
                     ' WHERE RESPONSABLES_TALLERES.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                     ' AND RESPONSABLES_TALLERES.NIT = ''' || UN_ANT_NIT || ''''||
                     ' AND RESPONSABLES_TALLERES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';                               

          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 
 -- 27/07/2018  @amonroy, Se elimina el envío de los campos NIT_TALLER y UN_NUE_SUCURSAL en la actualización para la tabla D_MANTENIMIENTO
     BEGIN
      MI_TABLA:='D_MANTENIMIENTO';
      MI_VALORES:='D_MANTENIMIENTO.COMPANIA               = ''' || UN_NUE_COMPANIA || ''','||
                  'D_MANTENIMIENTO.RESPONSABLE            = ''' || UN_NUE_NIT || ''','||                  
                  'D_MANTENIMIENTO.SUCURSAL_RESPONSABLE   = ''' || UN_NUE_SUCURSAL || ''','||
                  'D_MANTENIMIENTO.DATE_MODIFIED          =SYSDATE,'||
                  'D_MANTENIMIENTO.MODIFIED_BY            = '''||UN_USUARIO||''''; 

      MI_CONDICION:='D_MANTENIMIENTO.COMPANIA                   = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND D_MANTENIMIENTO.RESPONSABLE           = ''' || UN_ANT_NIT || ''''||                    
                    ' AND D_MANTENIMIENTO.SUCURSAL_RESPONSABLE  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;



  BEGIN
      MI_TABLA:='RESTRICCIONES';
      MI_VALORES:='RESTRICCIONES.COMPANIA            = ''' || UN_NUE_COMPANIA || ''','||
                  'RESTRICCIONES.DOCUMENTO_IDENTIDAD = ''' || UN_NUE_NIT || ''','||
                  'RESTRICCIONES.SUCURSAL            = ''' || UN_NUE_SUCURSAL || ''','||
                  'RESTRICCIONES.DATE_MODIFIED       =SYSDATE,'||
                  'RESTRICCIONES.MODIFIED_BY         = '''||UN_USUARIO||''''; 

      MI_CONDICION:='RESTRICCIONES.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESTRICCIONES.DOCUMENTO_IDENTIDAD = ''' || UN_ANT_NIT || ''''||
                    ' AND RESTRICCIONES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='RESUMEN_RENTAS';
      MI_VALORES:='RESUMEN_RENTAS.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'RESUMEN_RENTAS.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'RESUMEN_RENTAS.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'RESUMEN_RENTAS.DATE_MODIFIED   =SYSDATE,'||
                  'RESUMEN_RENTAS.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='RESUMEN_RENTAS.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESUMEN_RENTAS.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND RESUMEN_RENTAS.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';  
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

     BEGIN
      MI_TABLA:='SALDOSINICIALES';
      MI_VALORES:='SALDOSINICIALES.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SALDOSINICIALES.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'SALDOSINICIALES.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SALDOSINICIALES.DATE_MODIFIED   =SYSDATE,'||
                  'SALDOSINICIALES.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SALDOSINICIALES.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SALDOSINICIALES.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND SALDOSINICIALES.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
  /*
    BEGIN
      MI_TABLA:='SALDO_AUX_CONTABLE';
      MI_VALORES:='SALDO_AUX_CONTABLE.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'SALDO_AUX_CONTABLE.TERCERO       = ''' || UN_NUE_NIT || ''','||
                  'SALDO_AUX_CONTABLE.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'SALDO_AUX_CONTABLE.DATE_MODIFIED =SYSDATE,'||
                  'SALDO_AUX_CONTABLE.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SALDO_AUX_CONTABLE.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SALDO_AUX_CONTABLE.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND SALDO_AUX_CONTABLE.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
 */

    BEGIN
      MI_TABLA:='SALDO_AUX_PPTAL';
      MI_VALORES:='SALDO_AUX_PPTAL.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'SALDO_AUX_PPTAL.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'SALDO_AUX_PPTAL.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'SALDO_AUX_PPTAL.DATE_MODIFIED  =SYSDATE,'||
                  'SALDO_AUX_PPTAL.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SALDO_AUX_PPTAL.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SALDO_AUX_PPTAL.TERCERO   = ''' || UN_ANT_NIT || ''''||
                    ' AND SALDO_AUX_PPTAL.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='SECCIONALES';
      MI_VALORES:='SECCIONALES.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'SECCIONALES.DIRECTOR       = ''' || UN_NUE_NIT || ''','||
                  'SECCIONALES.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'SECCIONALES.DATE_MODIFIED  =SYSDATE,'||
                  'SECCIONALES.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SECCIONALES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SECCIONALES.DIRECTOR = ''' || UN_ANT_NIT || ''''||
                    ' AND SECCIONALES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='SOLICITUDDISPONIBILIDAD';
      MI_VALORES:='SOLICITUDDISPONIBILIDAD.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'SOLICITUDDISPONIBILIDAD.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'SOLICITUDDISPONIBILIDAD.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'SOLICITUDDISPONIBILIDAD.DATE_MODIFIED  =SYSDATE,'||
                  'SOLICITUDDISPONIBILIDAD.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SOLICITUDDISPONIBILIDAD.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SOLICITUDDISPONIBILIDAD.TERCERO = ''' || UN_ANT_NIT || ''''||
                    ' AND SOLICITUDDISPONIBILIDAD.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='SP_AFORADORES';
      MI_VALORES:='SP_AFORADORES.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'SP_AFORADORES.NIT            = ''' || UN_NUE_NIT || ''','||
                  'SP_AFORADORES.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'SP_AFORADORES.DATE_MODIFIED  =SYSDATE,'||
                  'SP_AFORADORES.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SP_AFORADORES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SP_AFORADORES.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND SP_AFORADORES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


    BEGIN
      MI_TABLA:='SP_EMPRESAS_CONVENIO';
      MI_VALORES:='SP_EMPRESAS_CONVENIO.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SP_EMPRESAS_CONVENIO.NIT             = ''' || UN_NUE_NIT || ''','||
                  'SP_EMPRESAS_CONVENIO.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SP_EMPRESAS_CONVENIO.DATE_MODIFIED   =SYSDATE,'||
                  'SP_EMPRESAS_CONVENIO.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SP_EMPRESAS_CONVENIO.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SP_EMPRESAS_CONVENIO.NIT       = ''' || UN_ANT_NIT || ''''||
                    ' AND SP_EMPRESAS_CONVENIO.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='SUPERVISORES';
      MI_VALORES:='SUPERVISORES.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SUPERVISORES.CEDULA          = ''' || UN_NUE_NIT || ''','||
                  'SUPERVISORES.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SUPERVISORES.DATE_MODIFIED   =SYSDATE,'||
                  'SUPERVISORES.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SUPERVISORES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SUPERVISORES.CEDULA   = ''' || UN_ANT_NIT || ''''||
                    ' AND SUPERVISORES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
        MI_TABLA  :='TALLER';
        MI_CAMPOS:= 'COMPANIA,'||
                    'NIT,'||
                    'SUCURSAL,'||
                    'NOMBRE,'||
                    'DIRECCION,'||
                    'TELEFONO,'||
                    'FAX,'||
                    'PROPIETARIO,'||
                    'SUCURSAL_PROPIETARIO,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''','||
                      '''' || UN_NUE_NIT || ''','||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'NOMBRE,'||
                      'DIRECCION,'||
                      'TELEFONO,'||
                      'FAX,'||
                      'PROPIETARIO,'||
                      'SUCURSAL_PROPIETARIO,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||                      
                      ' FROM TALLER '||
                      ' WHERE TALLER.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND TALLER.NIT        = ''' || UN_ANT_NIT || ''''||
                      ' AND TALLER.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';


          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 


  BEGIN
        MI_TABLA  :='RESPONSABLES_TALLERES';
        MI_CAMPOS:= 'COMPANIA,'||
                    'TALLER,'||
                    'SUCURSAL_TALLER,'||
                    'NIT,'||
                    'SUCURSAL,'||
                    'NOMBRE,'||
                    'DATE_CREATED,'||
                    'CREATED_BY';       
        MI_CONSULTA:= 'SELECT '||
                      '''' || UN_NUE_COMPANIA || ''','||
                      '''' || UN_NUE_NIT || ''','||
                      '''' || UN_NUE_SUCURSAL || ''','||
                      'NIT,'||
                      'SUCURSAL,'||
                      'NOMBRE,'||
                      'SYSDATE,'||
                      ''''||UN_USUARIO||''''||                      
                      ' FROM RESPONSABLES_TALLERES '||
                      ' WHERE RESPONSABLES_TALLERES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                      ' AND RESPONSABLES_TALLERES.TALLER          = ''' || UN_ANT_NIT || ''''||
                      ' AND RESPONSABLES_TALLERES.SUCURSAL_TALLER = ''' || UN_ANT_SUCURSAL || '''';/* ||
                      ' AND RESPONSABLES_TALLERES.NIT NOT IN (PCK_DATOS.FC_CONS_TERCERO)';  */              
          BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_CONSULTA);
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;         

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_PARAMETROS:='USUARIO='''||UN_USUARIO||''','||              
                                'COMPANIA = ''' || UN_ANT_COMPANIA || ''','|| 
                                'NIT = ''' || UN_ANT_NIT || ''','|| 
                                'SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END; 

  -- 27/07/2018  @amonroy, Se elimina el envío de los campos RESPONSABLE y SUCURSAL_RESPONSABLE en la actualización para la tabla D_MANTENIMIENTO
       BEGIN
          MI_TABLA:='D_MANTENIMIENTO';
          MI_VALORES:='D_MANTENIMIENTO.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                      'D_MANTENIMIENTO.NIT_TALLER           = ''' || UN_NUE_NIT || ''','||
                      'D_MANTENIMIENTO.SUCURSAL             = ''' || UN_NUE_SUCURSAL || ''','||
                      'D_MANTENIMIENTO.DATE_MODIFIED        = SYSDATE,'||
                      'D_MANTENIMIENTO.MODIFIED_BY          = '''||UN_USUARIO||''''; 

          MI_CONDICION:='D_MANTENIMIENTO.COMPANIA                   = ''' || UN_ANT_COMPANIA || ''''||
                        ' AND D_MANTENIMIENTO.NIT_TALLER            = ''' || UN_ANT_NIT || ''''||
                        ' AND D_MANTENIMIENTO.SUCURSAL              = ''' || UN_ANT_SUCURSAL || '''';

        BEGIN
            MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                            UN_ACCION       => 'M',
                                            UN_CAMPOS       => MI_VALORES,
                                            UN_CONDICION    => MI_CONDICION);                                    
              EXCEPTION
                   WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
      END; 


  BEGIN
      MI_TABLA:='TALLER';
      MI_VALORES:='TALLER.COMPANIA             = ''' || UN_NUE_COMPANIA || ''','||
                  'TALLER.PROPIETARIO          = ''' || UN_NUE_NIT || ''','||
                  'TALLER.SUCURSAL_PROPIETARIO = ''' || UN_NUE_SUCURSAL || ''','||
                  'TALLER.DATE_MODIFIED        =SYSDATE,'||
                  'TALLER.MODIFIED_BY          = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TALLER.COMPANIA                  = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TALLER.PROPIETARIO          = ''' || UN_ANT_NIT || ''''||
                    ' AND TALLER.SUCURSAL_PROPIETARIO = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='TERCEROPAGOS';
      MI_VALORES:='TERCEROPAGOS.COMPANIA      = ''' || UN_NUE_COMPANIA || ''','||
                  'TERCEROPAGOS.NIT           = ''' || UN_NUE_NIT || ''','||
                  'TERCEROPAGOS.SUCURSAL      = ''' || UN_NUE_SUCURSAL || ''','||
                  'TERCEROPAGOS.DATE_MODIFIED =SYSDATE,'||
                  'TERCEROPAGOS.MODIFIED_BY   = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TERCEROPAGOS.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TERCEROPAGOS.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND TERCEROPAGOS.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='TERCEROS_APORTANTES';
      MI_VALORES:='TERCEROS_APORTANTES.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'TERCEROS_APORTANTES.NIT            = ''' || UN_NUE_NIT || ''','||
                  'TERCEROS_APORTANTES.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'TERCEROS_APORTANTES.DATE_MODIFIED  =SYSDATE,'||
                  'TERCEROS_APORTANTES.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TERCEROS_APORTANTES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TERCEROS_APORTANTES.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND TERCEROS_APORTANTES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='TRANSACCIONES';
      MI_VALORES:='TRANSACCIONES.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'TRANSACCIONES.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'TRANSACCIONES.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'TRANSACCIONES.DATE_MODIFIED  =SYSDATE,'||
                  'TRANSACCIONES.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TRANSACCIONES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TRANSACCIONES.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND TRANSACCIONES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='TRANSACCIONMODELO';
      MI_VALORES:='TRANSACCIONMODELO.COMPANIA       = ''' || UN_NUE_COMPANIA || ''','||
                  'TRANSACCIONMODELO.TERCERO        = ''' || UN_NUE_NIT || ''','||
                  'TRANSACCIONMODELO.SUCURSAL       = ''' || UN_NUE_SUCURSAL || ''','||
                  'TRANSACCIONMODELO.DATE_MODIFIED  =SYSDATE,'||
                  'TRANSACCIONMODELO.MODIFIED_BY    = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TRANSACCIONMODELO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TRANSACCIONMODELO.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND TRANSACCIONMODELO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='TRANSACCION_DEVOLUTIVO';
      MI_VALORES:='TRANSACCION_DEVOLUTIVO.COMPANIA              = ''' || UN_NUE_COMPANIA || ''','||
                  'TRANSACCION_DEVOLUTIVO.DOCUMENTO_SOLICITANTE = ''' || UN_NUE_NIT || ''','||
                  'TRANSACCION_DEVOLUTIVO.SUCURSAL_SOLICITANTE  = ''' || UN_NUE_SUCURSAL || ''','||
                  'TRANSACCION_DEVOLUTIVO.DATE_MODIFIED         =SYSDATE,'||
                  'TRANSACCION_DEVOLUTIVO.MODIFIED_BY           = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TRANSACCION_DEVOLUTIVO.COMPANIA                    = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TRANSACCION_DEVOLUTIVO.DOCUMENTO_SOLICITANTE  = ''' || UN_ANT_NIT || ''''||
                    ' AND TRANSACCION_DEVOLUTIVO.SUCURSAL_SOLICITANTE   = ''' || UN_ANT_SUCURSAL || '''';


      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='TRANSACCION_DEVOLUTIVO';
      MI_VALORES:='TRANSACCION_DEVOLUTIVO.COMPANIA                    = ''' || UN_NUE_COMPANIA || ''','||
                  'TRANSACCION_DEVOLUTIVO.NUMDOCUMENTO_REPRESENTANTE  = ''' || UN_NUE_NIT || ''','||
                  'TRANSACCION_DEVOLUTIVO.SUCURSAL_REPRESENTANTE      = ''' || UN_NUE_SUCURSAL || ''','||
                  'TRANSACCION_DEVOLUTIVO.DATE_MODIFIED               =SYSDATE,'||
                  'TRANSACCION_DEVOLUTIVO.MODIFIED_BY                 = '''||UN_USUARIO||''''; 

      MI_CONDICION:='TRANSACCION_DEVOLUTIVO.COMPANIA = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TRANSACCION_DEVOLUTIVO.NUMDOCUMENTO_REPRESENTANTE = ''' || UN_ANT_NIT || ''''||  
                    ' AND TRANSACCION_DEVOLUTIVO.SUCURSAL_REPRESENTANTE = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='VEHICULOGASES';
      MI_VALORES:='VEHICULOGASES.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'VEHICULOGASES.NUMERODOCUMENTO = ''' || UN_NUE_NIT || ''','||
                  'VEHICULOGASES.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'VEHICULOGASES.DATE_MODIFIED   =SYSDATE,'||
                  'VEHICULOGASES.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='VEHICULOGASES.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND VEHICULOGASES.NUMERODOCUMENTO = ''' || UN_ANT_NIT || ''''||
                    ' AND VEHICULOGASES.SUCURSAL        = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='VEHICULOSEGURO';
      MI_VALORES:='VEHICULOSEGURO.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'VEHICULOSEGURO.NIT             = ''' || UN_NUE_NIT || ''','||
                  'VEHICULOSEGURO.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'VEHICULOSEGURO.DATE_MODIFIED   =SYSDATE,'||
                  'VEHICULOSEGURO.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='VEHICULOSEGURO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND VEHICULOSEGURO.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND VEHICULOSEGURO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='SF_CUENTAS_AUXTERCERO';
      MI_VALORES:='SF_CUENTAS_AUXTERCERO.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SF_CUENTAS_AUXTERCERO.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'SF_CUENTAS_AUXTERCERO.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SF_CUENTAS_AUXTERCERO.DATE_MODIFIED   = SYSDATE,'||
                  'SF_CUENTAS_AUXTERCERO.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SF_CUENTAS_AUXTERCERO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SF_CUENTAS_AUXTERCERO.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND SF_CUENTAS_AUXTERCERO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='SF_DETALLE_FACTURA';
      MI_VALORES:='SF_DETALLE_FACTURA.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SF_DETALLE_FACTURA.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'SF_DETALLE_FACTURA.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SF_DETALLE_FACTURA.DATE_MODIFIED   = SYSDATE,'||
                  'SF_DETALLE_FACTURA.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SF_DETALLE_FACTURA.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SF_DETALLE_FACTURA.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND SF_DETALLE_FACTURA.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='SF_OBJETO_COBRO';
      MI_VALORES:='SF_OBJETO_COBRO.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SF_OBJETO_COBRO.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'SF_OBJETO_COBRO.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SF_OBJETO_COBRO.DATE_MODIFIED   = SYSDATE,'||
                  'SF_OBJETO_COBRO.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SF_OBJETO_COBRO.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SF_OBJETO_COBRO.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND SF_OBJETO_COBRO.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='SF_FACTURA';
      MI_VALORES:='SF_FACTURA.COMPANIA        = ''' || UN_NUE_COMPANIA || ''','||
                  'SF_FACTURA.TERCERO         = ''' || UN_NUE_NIT || ''','||
                  'SF_FACTURA.SUCURSAL        = ''' || UN_NUE_SUCURSAL || ''','||
                  'SF_FACTURA.DATE_MODIFIED   = SYSDATE,'||
                  'SF_FACTURA.MODIFIED_BY     = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SF_FACTURA.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SF_FACTURA.TERCERO  = ''' || UN_ANT_NIT || ''''||
                    ' AND SF_FACTURA.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='MOVIMIENTO';
      MI_VALORES:='MOVIMIENTO.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'MOVIMIENTO.NIT_RECIBIDO       = ''' || UN_NUE_NIT || ''','||
                  'MOVIMIENTO.SUCURSAL_RECIBIDO  = ''' || UN_NUE_SUCURSAL || ''','||
                  'MOVIMIENTO.DATE_MODIFIED      = SYSDATE,'||
                  'MOVIMIENTO.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='MOVIMIENTO.COMPANIA               = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND MOVIMIENTO.NIT_RECIBIDO      = ''' || UN_ANT_NIT || ''''||
                    ' AND MOVIMIENTO.SUCURSAL_RECIBIDO = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'NIT';
                     MI_REEMPLAZOS (0).VALOR :=  UN_ANT_NIT;    
                     MI_REEMPLAZOS (1).CLAVE := 'SUSCURSAL';                    
                     MI_REEMPLAZOS (1).VALOR :=  UN_ANT_SUCURSAL;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTCAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='SP_EMPRESAS_TERCERIZA';
      MI_VALORES:='SP_EMPRESAS_TERCERIZA.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'SP_EMPRESAS_TERCERIZA.NIT       = ''' || UN_NUE_NIT || ''','||
                  'SP_EMPRESAS_TERCERIZA.SUCURSAL  = ''' || UN_NUE_SUCURSAL || ''','||
                  'SP_EMPRESAS_TERCERIZA.DATE_MODIFIED      = SYSDATE,'||
                  'SP_EMPRESAS_TERCERIZA.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='SP_EMPRESAS_TERCERIZA.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND SP_EMPRESAS_TERCERIZA.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND SP_EMPRESAS_TERCERIZA.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'NIT';
                     MI_REEMPLAZOS (0).VALOR :=  UN_ANT_NIT;    
                     MI_REEMPLAZOS (1).CLAVE := 'SUSCURSAL';                    
                     MI_REEMPLAZOS (1).VALOR :=  UN_ANT_SUCURSAL;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_ACTSPEMPRESTERCER,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
  BEGIN
      MI_TABLA:='PLAN_PPTAL_CONFIG';
      MI_VALORES:='PLAN_PPTAL_CONFIG.COMPANIA           = ''' || UN_NUE_COMPANIA || ''','||
                  'PLAN_PPTAL_CONFIG.TERCERO            = ''' || UN_NUE_NIT || ''','||
                  'PLAN_PPTAL_CONFIG.SUCURSAL           = ''' || UN_NUE_SUCURSAL || ''','||
                  'PLAN_PPTAL_CONFIG.DATE_MODIFIED      = SYSDATE,'||
                  'PLAN_PPTAL_CONFIG.MODIFIED_BY        = '''||UN_USUARIO||''''; 

      MI_CONDICION:='PLAN_PPTAL_CONFIG.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PLAN_PPTAL_CONFIG.TERCERO     = ''' || UN_ANT_NIT || ''''||
                    ' AND PLAN_PPTAL_CONFIG.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_VALORES,
                                        UN_CONDICION    => MI_CONDICION);                                    
          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'NIT';
                     MI_REEMPLAZOS (0).VALOR :=  UN_ANT_NIT;    
                     MI_REEMPLAZOS (1).CLAVE := 'SUSCURSAL';                    
                     MI_REEMPLAZOS (1).VALOR :=  UN_ANT_SUCURSAL;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONT_PLANPPTALCONFIG_TER,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

  BEGIN
      MI_TABLA:='RESPONSABLES_TALLERES';        
      MI_CONDICION:='RESPONSABLES_TALLERES.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESPONSABLES_TALLERES.TALLER          = ''' || UN_ANT_NIT || ''''||
                    ' AND RESPONSABLES_TALLERES.SUCURSAL_TALLER = ''' || UN_ANT_SUCURSAL || '''';                      

      BEGIN

           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='TALLER';            
      MI_CONDICION:='TALLER.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND TALLER.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND TALLER.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN

           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='RESPONSABLES_TALLERES';         
      MI_CONDICION:='RESPONSABLES_TALLERES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESPONSABLES_TALLERES.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND RESPONSABLES_TALLERES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN

           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='DEPENDENCIA_RESPONSABLE';         
      MI_CONDICION:='DEPENDENCIA_RESPONSABLE.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND DEPENDENCIA_RESPONSABLE.RESPONSABLE = ''' || UN_ANT_NIT || ''''||
                    ' AND DEPENDENCIA_RESPONSABLE.SUCURSAL    = ''' || UN_ANT_SUCURSAL || ''''; 
      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
 BEGIN
      MI_TABLA:='RESPONSABLE';         
      MI_CONDICION:='RESPONSABLE.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND RESPONSABLE.CEDULA    = ''' || UN_ANT_NIT || ''''||
                    ' AND RESPONSABLE.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


   BEGIN
      MI_TABLA:='PROPONENTE';            
      MI_CONDICION:='PROPONENTE.COMPANIA        = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND PROPONENTE.PROPONENTE = ''' || UN_ANT_NIT || ''''||
                    ' AND PROPONENTE.SUCURSAL   = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

   BEGIN
      MI_TABLA:='ORDENADOR';            
      MI_CONDICION:='ORDENADOR.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ORDENADOR.CEDULA    = ''' || UN_ANT_NIT || ''''||
                    ' AND ORDENADOR.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

    BEGIN
      MI_TABLA:='NAT_DATOS_PERSONALES';            
      MI_CONDICION:='NAT_DATOS_PERSONALES.COMPANIA          = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_DATOS_PERSONALES.NUMERO_DCTO  = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_DATOS_PERSONALES.SUCURSAL     = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

 BEGIN
      MI_TABLA:='LUGAR_PARQUEO';            
      MI_CONDICION:='LUGAR_PARQUEO.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND LUGAR_PARQUEO.CODIGO          = ''' || UN_ANT_NIT || ''''||
                    ' AND LUGAR_PARQUEO.SUCURSAL_CODIGO = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='ID_COMERCIALIZADORA';            
      MI_CONDICION:='ID_COMERCIALIZADORA.COMPANIA       = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ID_COMERCIALIZADORA.NIT       = ''' || UN_ANT_NIT || ''''||
                    ' AND ID_COMERCIALIZADORA.SUCURSAL  = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='NAT_ENTIDADESCAPACITACION';            
      MI_CONDICION:='NAT_ENTIDADESCAPACITACION.COMPANIA                = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_ENTIDADESCAPACITACION.NITESTABLECIMIENTO = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_ENTIDADESCAPACITACION.SUCURSAL           = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


  BEGIN
      MI_TABLA:='NAT_ENTIDADESCAPACITACION';            
      MI_CONDICION:='NAT_ENTIDADESCAPACITACION.COMPANIA                = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NAT_ENTIDADESCAPACITACION.PERSONACONTACTO    = ''' || UN_ANT_NIT || ''''||
                    ' AND NAT_ENTIDADESCAPACITACION.SUCURSALCONTACTO   = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='CUOTASPARTES';            
      MI_CONDICION:='CUOTASPARTES.COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND CUOTASPARTES.NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND CUOTASPARTES.SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;


 BEGIN
      MI_TABLA:='POLIZAS_ACTIVOS';            
      MI_CONDICION:='POLIZAS_ACTIVOS.COMPANIA         = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND POLIZAS_ACTIVOS.ASEGURADORA = ''' || UN_ANT_NIT || ''''||
                    ' AND POLIZAS_ACTIVOS.SUCURSAL    = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

 BEGIN
      MI_TABLA:='ASEGURADORA';            
      MI_CONDICION:='ASEGURADORA.COMPANIA             = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND ASEGURADORA.NITASEGURADORA  = ''' || UN_ANT_NIT || ''''||
                    ' AND ASEGURADORA.SUCURSAL        = ''' || UN_ANT_SUCURSAL || '''';

      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;

 BEGIN
      MI_TABLA:='TERCERO';            
      MI_CONDICION:='COMPANIA      = ''' || UN_ANT_COMPANIA || ''''||
                    ' AND NIT      = ''' || UN_ANT_NIT || ''''||
                    ' AND SUCURSAL = ''' || UN_ANT_SUCURSAL || '''';
      BEGIN    
           MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION);                                 
           EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
         EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='USUARIO=''' || UN_USUARIO || ''','||
                               'COMPANIA=''' || UN_ANT_COMPANIA || ''','||
                               'NIT= ''' || UN_ANT_NIT || ''','||
                               'SUCURSAL= ''' || UN_ANT_SUCURSAL || '''';
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
 /* 
    PCK_CONTABILIDAD.PR_CAMBIAR_NITH ( UN_COMPANIA          => UN_NUE_COMPANIA,
                                       UN_NIT_ANTERIOR      => UN_ANT_NIT,
                                       UN_SUCURSAL_ANTERIOR => UN_ANT_SUCURSAL,
                                       UN_NIT_NUEVO         => UN_NUE_NIT,
                                       UN_SUCURSAL_NUEVO    => UN_NUE_SUCURSAL);
 */                      
  PCK_GENERALES.CAMBIONIT := 0; 

END PR_CAMBIOSDENITATERCERO_ANTERI;


 FUNCTION FC_VALIDARCUENTAEXISTE
   /*
  NAME              : FC_VALIDARCUENTAEXISTE 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ
  DATE MIGRADOR     : 13/11/2017
  TIME              : 08:00 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : CONTABILIDAD
  DESCRIPTION       : Permite validad si la cuenta se puede mover de acuerdo a los indicadores de movimiento
                      con el parametro UN_VALIDABLOQUEADO => se valida si tambein se debe controlar el bloqueo de la cuenta
  @NAME:  validarCuentaaUtilizar
  */
( 
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
  UN_CUENTA          IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_VALIDABLOQUEADO IN PCK_SUBTIPOS.TI_LOGICO
  )
RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_X             NUMBER:= 0;
    MI_NATURALEZA    VARCHAR2(1 CHAR);
    MI_BLOQUEACUENTA VARCHAR2(2 CHAR);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    BEGIN 
      SELECT  MAN_CEN_CTO 
              + MAN_AUX_TER 
              + MAN_AUX_GEN 
              + MAN_AUX_FUE 
              + MAN_AUX_REF 
              + MOVIMIENTO,       
              BLOQUEACUENTA
          INTO  MI_X
               ,MI_BLOQUEACUENTA
        FROM PLAN_CONTABLE   
       WHERE COMPANIA = UN_COMPANIA
         AND ANO      = UN_ANO
         AND CODIGO   = UN_CUENTA; 
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RETURN 0; 
    END;
    --VALIDA QUE LA CUENTA SEA DE MOVIMIENTO O CON AUXILIA
    IF MI_X=0 THEN
      RETURN 0; 
    END IF;
    --VALIDA QUE LA CUENTA NO ESTE BLOQUEADA
    IF UN_VALIDABLOQUEADO<>0 THEN
      IF MI_BLOQUEACUENTA='SI' THEN
        RETURN 0;
      END IF;
    END IF;
    RETURN -1;
END FC_VALIDARCUENTAEXISTE;


PROCEDURE PR_CAMBIOSDENITATERCERO  
/*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 08/02/2019
        TIME              : 10:02 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite realizar el cambio de nit de cualquier compañia, 
                            se basa en una tabla donde se configuran las tablas sobre las cuales se realiza el cambio de nit 
                            Se debe tener en cuenta casos especiales como los de la tablas de saldos
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:  cambioNitTercero
        @METHOD:Post
*/  
(
    UN_NUE_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUE_NIT        IN PCK_SUBTIPOS.TI_TERCERO , 
    UN_NUE_SUCURSAL   IN PCK_SUBTIPOS.TI_SUCURSAL ,
    UN_ANT_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_ANT_NIT        IN PCK_SUBTIPOS.TI_TERCERO , 
    UN_ANT_SUCURSAL   IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
AS

BEGIN
    /**
    *SE INCLUYE PARA QUE EL PROCESO NO SE EJECUTE SI ENVIAN EL MISMO NIT PARA EL INICIAL Y EL FINAL
    */
    IF   UN_NUE_NIT = UN_ANT_NIT AND UN_NUE_SUCURSAL = UN_ANT_SUCURSAL THEN
        ROLLBACK;
        RETURN;
    END IF;
    /**
    * SE REALIZA EL CAMBIO PARA TODAS LAS COMPAÑIAS PUES EN LA UPC ENVIAN DESDE UNA PARA CAMBAIR A TODAS
    **/
    BEGIN
        PCK_GENERALES.GL_CAMBIONIT := 1;
        FOR RS IN ( SELECT COMPANIA
                    FROM TERCERO
                    WHERE SUCURSAL = UN_ANT_SUCURSAL
                      AND NIT      = UN_ANT_NIT
        ) LOOP   
            /*
            * INSERTA LAS TABLAS CONFIGURADAS COMO OPERACION 1
            */
            PCK_CAMBIONIT.PR_INSERTAR_TABLA(
                                            UN_COMPANIA_ANT  => RS.COMPANIA,
                                            UN_TERCERO_ANT   => UN_ANT_NIT,  
                                            UN_SUCURSAL_ANT  => UN_ANT_SUCURSAL,
                                            UN_COMPANIA_NUE  => RS.COMPANIA,
                                            UN_TERCERO_NUE   => UN_NUE_NIT,  
                                            UN_SUCURSAL_NUE  => UN_NUE_SUCURSAL,
                                            UN_USUARIO       => UN_USUARIO
            );
            /*
            * ACTUALIZA LAS TABLAS CONFIGURADAS COMO OPERACION 2
            */
            PCK_CAMBIONIT.PR_ACTUALIZAR_TABLA(
                                            UN_COMPANIA_ANT  => RS.COMPANIA,
                                            UN_TERCERO_ANT   => UN_ANT_NIT,  
                                            UN_SUCURSAL_ANT  => UN_ANT_SUCURSAL,
                                            UN_COMPANIA_NUE  => RS.COMPANIA,
                                            UN_TERCERO_NUE   => UN_NUE_NIT,  
                                            UN_SUCURSAL_NUE  => UN_NUE_SUCURSAL,
                                            UN_USUARIO       => UN_USUARIO
            );
            --Se agrega procedimiento. el cual se encarga de actualizar los saldos en la tabla SALDO_AUX_CONTABLE
            PCK_CONTABILIDAD6.PR_ACTCONTSALDOAUX(   UN_COMPANIA_ANT	=>	RS.COMPANIA,
                                                    UN_TERCERO_ANT  => 	UN_ANT_NIT,
                                                    UN_SUCURSAL_ANT	=>  UN_ANT_SUCURSAL,
                                                    UN_COMPANIA_NUE	=>  RS.COMPANIA,
                                                    UN_TERCERO_NUE	=>  UN_NUE_NIT,
                                                    UN_SUCURSAL_NUE	=>  UN_NUE_SUCURSAL,
                                                    UN_USUARIO 		=>  UN_USUARIO);

            /*
            * ELIMINA LAS TABLAS CONFIGURADAS COMO OPERACION 1 PERO EN ORDEN DESCENDENTE
            */
            PCK_CAMBIONIT.FC_ELIMINAR_TABLA(
                                            UN_COMPANIA_ANT  => RS.COMPANIA,
                                            UN_TERCERO_ANT   => UN_ANT_NIT,  
                                            UN_SUCURSAL_ANT  => UN_ANT_SUCURSAL,
                                            UN_USUARIO       => UN_USUARIO
            );          
        END LOOP;
        PCK_GENERALES.GL_CAMBIONIT := 0; 
        COMMIT;  
    END;
END PR_CAMBIOSDENITATERCERO;

FUNCTION FC_CODIGO_CONTAB 
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO         IN VARCHAR2,
    UN_CENTRO_COSTO   IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR,
    UN_USUARIO        IN VARCHAR2
  )RETURN VARCHAR2 
AS 
    MI_CODIGO         VARCHAR2(200 CHAR);
    MI_CENTRO_COSTO   PCK_SUBTIPOS.TI_CENTRO_COSTO := '';
    MI_TERCERO        PCK_SUBTIPOS.TI_TERCERO := '';
    MI_SUCURSAL       PCK_SUBTIPOS.TI_SUCURSAL := '';
    MI_AUXILIAR       PCK_SUBTIPOS.TI_AUXILIAR := '';
    MI_CODIGO_CONTAB  VARCHAR2(200 CHAR);
BEGIN
 
 
 FOR RS IN(SELECT COMPANIA,
                ANO,
                CODIGO,
                MAN_CEN_CTO,
                MAN_AUX_TER,
                MAN_AUX_GEN,
                MOVIMIENTO
                FROM PLAN_CONTABLE
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANO
                  AND CODIGO   = UN_CODIGO)
  LOOP
  
   MI_CENTRO_COSTO := CASE WHEN RS.MAN_CEN_CTO NOT IN (0) THEN UN_CENTRO_COSTO ELSE '' END;
   MI_TERCERO := CASE WHEN RS.MAN_AUX_TER NOT IN (0) THEN UN_TERCERO ELSE '' END;
   MI_SUCURSAL := CASE WHEN RS.MAN_AUX_TER NOT IN (0) THEN UN_SUCURSAL ELSE '' END;
   MI_AUXILIAR := CASE WHEN RS.MAN_AUX_GEN NOT IN (0) THEN UN_AUXILIAR ELSE '' END;
   
  END LOOP;
  
  BEGIN
      IF UN_CODIGO = '' THEN 
        RETURN MI_CODIGO_CONTAB;
      ELSE
    MI_CODIGO_CONTAB := SUBSTR(UN_CODIGO,0, 16);
    END IF;
    IF MI_CENTRO_COSTO IS NOT NULL THEN
       MI_CODIGO_CONTAB := MI_CODIGO_CONTAB || SUBSTR(MI_CENTRO_COSTO,0, 10);
    ELSE
       MI_CODIGO_CONTAB := RPAD(MI_CODIGO_CONTAB,10,' ');
    END IF;
    IF MI_TERCERO IS NOT NULL THEN
       MI_CODIGO_CONTAB := MI_CODIGO_CONTAB || SUBSTR(MI_TERCERO,0,11);
       MI_CODIGO_CONTAB := MI_CODIGO_CONTAB || SUBSTR(MI_SUCURSAL,0,3);
    ELSE
       MI_CODIGO_CONTAB := RPAD(MI_CODIGO_CONTAB,11,' ');
       MI_CODIGO_CONTAB := RPAD(MI_CODIGO_CONTAB,3,' ');
    END IF;
    IF MI_AUXILIAR IS NOT NULL THEN
       MI_CODIGO_CONTAB := MI_CODIGO_CONTAB || SUBSTR(MI_AUXILIAR,0,16);
    ELSE
       MI_CODIGO_CONTAB := RPAD(MI_CODIGO_CONTAB,16,' ');
    END IF;

    MI_CODIGO_CONTAB := TRIM(MI_CODIGO_CONTAB);
    END;  
  
  RETURN MI_CODIGO_CONTAB;
END FC_CODIGO_CONTAB;

FUNCTION FC_CAMBIARFECHACOMPROBANTE
   /*
   NAME 			   : PR_CAMBIARFECHACOMPROBANTE
   AUTHORS 			   : SYSMAN SAS
   AUTHOR MIGRACION	   : ALISSON CATALINA CELEITA GUTIERREZ - SANDRA MILENA DAZA LEGUIZAMON
   DATE MIGRADOR	   : 15/10/2021 - 07/02/2022
   MODULO ORIGEN	   : CONTABILIDAD
   DESCRIPTION		   : Proceso para cambiar la fecha de todos los comprobantes.
                            - Se adiciona control de fecha actual del comprobante, si está cerrado se cancela el proceso
                            - Se valida que si alguna de las condiciones para realizar el proceso se incumple se finalice la rutina 
  */ (
        UN_COMPANIA          IN   PCK_SUBTIPOS.TI_COMPANIA,       -- Código de la compania
        UN_ANIO              IN   PCK_SUBTIPOS.TI_ENTERO,         -- Año
        UN_TIPOCOMPROBANTE   IN   VARCHAR2,                       -- Tipo de comprobante
        UN_COMPROBANTE       IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,   -- Número de comprobante
        UN_FECHAACTUAL       IN   DATE,                           -- Nueva fecha que va a tomar el tipo de comprobante
        UN_USUARIO           IN   PCK_SUBTIPOS.TI_USUARIO
    )RETURN VARCHAR2 AS

        MI_TABLA                 VARCHAR2(200 CHAR);
        MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
        MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
        MI_CONDICION2            PCK_SUBTIPOS.TI_CONDICION;
        MI_FECHA                 DATE;
        MI_RTA                   VARCHAR2(3200 CHAR);
        MI_RETORNO               CLOB;
        MI_ANIOAFECT             PCK_SUBTIPOS.TI_ANIO;
        MI_TIPOCPTEAFECT         VARCHAR2(32 CHAR);
        MI_CMPTEAFECTADO         VARCHAR2(32 CHAR);
        MI_CONSECUTIVOAFECTADO   NUMBER(3, 0);
        MI_DATO                  VARCHAR2(32 CHAR);
        MI_IMPRESO               NUMBER(1, 0);
    BEGIN
   -- DBMS_OUTPUT.PUT_LINE(TO_CHAR(UN_FECHAACTUAL, 'DD/MM/YYYY'));
        
        SELECT FECHA
        INTO   MI_FECHA
        FROM   COMPROBANTE_CNT
        WHERE  COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
        AND     COMPROBANTE_CNT.ANO = UN_ANIO
        AND     COMPROBANTE_CNT.TIPO = UN_TIPOCOMPROBANTE
        AND     COMPROBANTE_CNT.NUMERO = UN_COMPROBANTE;
        
        BEGIN
            SELECT
                DIA_BLOQUEO.ESTADO
            INTO MI_DATO
            FROM
                DIA_BLOQUEO
            WHERE
                DIA_BLOQUEO.COMPANIA = UN_COMPANIA
                AND DIA_BLOQUEO.ANO = TO_NUMBER(TO_CHAR(MI_FECHA,'YYYY'))
                AND DIA_BLOQUEO.MES = TO_NUMBER(TO_CHAR(MI_FECHA,'MM'))
                AND DIA_BLOQUEO.DIA = TO_NUMBER(TO_CHAR(MI_FECHA,'DD'))
                AND APLICACION = PCK_DATOS.FC_MODULOCONTABILIDAD
                AND PROCESO  = 1
                AND DIA_BLOQUEO.ESTADO = 'A';
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
               RETURN 'El periodo definido por la Fecha del comprobante se encuentra cerrado, no se puede cambiar la fecha al movimiento';
        END;
        
        
        BEGIN
            SELECT
                DIA_BLOQUEO.ESTADO
            INTO MI_DATO
            FROM
                DIA_BLOQUEO
            WHERE
                DIA_BLOQUEO.COMPANIA = UN_COMPANIA
                AND DIA_BLOQUEO.ANO = TO_NUMBER(TO_CHAR(UN_FECHAACTUAL,'YYYY'))
                AND DIA_BLOQUEO.MES = TO_NUMBER(TO_CHAR(UN_FECHAACTUAL,'MM'))
                AND DIA_BLOQUEO.DIA = TO_NUMBER(TO_CHAR(UN_FECHAACTUAL,'DD'))
                AND APLICACION = PCK_DATOS.FC_MODULOCONTABILIDAD
                AND PROCESO  = 1
                AND DIA_BLOQUEO.ESTADO = 'A';
               
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                MI_RETORNO := MI_RETORNO
                              || 'El periodo definido por la Fecha Anterior se encuentra cerrado, no se puede cambiar la fecha al movimiento';
        END;

        IF TO_CHAR(MI_FECHA, 'YYYY')<> TO_CHAR(UN_FECHAACTUAL, 'YYYY') THEN
              RETURN 'Para continuar con el proceso el cambio de fecha debe estar dentro de la misma vigencia';
        END IF;
        
         BEGIN
            SELECT
                COMPROBANTE_CNT.IMPRESO
            INTO MI_IMPRESO
            FROM
                COMPROBANTE_CNT
            WHERE
                COMPROBANTE_CNT.NUMERO = UN_COMPROBANTE
				AND COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
				AND COMPROBANTE_CNT.TIPO = UN_TIPOCOMPROBANTE
				AND COMPROBANTE_CNT.ANO = UN_ANIO
                AND COMPROBANTE_CNT.IMPRESO = 0;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                MI_RETORNO := 'El comprobante ya fue impreso, no se puede cambiar la fecha';
                RETURN MI_RETORNO;
        END;
        
        --JM INI CC 4237
        BEGIN
            FOR DETALLE IN 
                ( SELECT
                     AFECT.FECHA, AFECT.COMPROBANTE, AFECT.TIPO_CPTE -- TIENE QUE SER MAYOR 
                FROM
                    DETALLE_COMPROBANTE_CNT DCCNT
                    JOIN DETALLE_COMPROBANTE_CNT AFECT
                    ON AFECT.COMPANIA = DCCNT.COMPANIA 
                    AND AFECT.ANO = DCCNT.ANO_AFECT
                    AND AFECT.TIPO_CPTE = DCCNT.TIPO_CPTE_AFECT
                    AND AFECT.COMPROBANTE = DCCNT.CMPTE_AFECTADO
                    AND AFECT.CONSECUTIVO = DCCNT.CONSECUTIVOAFECTADO
                WHERE
                    DCCNT.COMPANIA = UN_COMPANIA
                    AND DCCNT.ANO = UN_ANIO
                    AND DCCNT.TIPO_CPTE = UN_TIPOCOMPROBANTE
                    AND DCCNT.COMPROBANTE = UN_COMPROBANTE
                    )
            LOOP
                
                
                IF DETALLE.FECHA IS NOT NULL AND DETALLE.FECHA > UN_FECHAACTUAL THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_RETORNO := 'El comprobante '
                                          || UN_TIPOCOMPROBANTE
                                          || ' No '
                                          || UN_COMPROBANTE
                                          || ' no puede tener fecha inferior al '
                                          || DETALLE.TIPO_CPTE
                                          || ' No. '
                                          || DETALLE.COMPROBANTE;
                            RETURN MI_RETORNO;
                    END;
                END IF; 
            END LOOP;
        END;
        
        BEGIN
            FOR DETALLE2 IN 
                ( SELECT
                     DCCNT.FECHA , DCCNT.COMPROBANTE, DCCNT.TIPO_CPTE 
                FROM
                    DETALLE_COMPROBANTE_CNT DCCNT
                    JOIN DETALLE_COMPROBANTE_CNT AFECT
                    ON AFECT.COMPANIA = DCCNT.COMPANIA 
                    AND AFECT.ANO = DCCNT.ANO_AFECT
                    AND AFECT.TIPO_CPTE = DCCNT.TIPO_CPTE_AFECT
                    AND AFECT.COMPROBANTE = DCCNT.CMPTE_AFECTADO
                    AND AFECT.CONSECUTIVO = DCCNT.CONSECUTIVOAFECTADO
                WHERE
                    DCCNT.COMPANIA = UN_COMPANIA
                    AND DCCNT.ANO_AFECT = UN_ANIO
                    AND DCCNT.TIPO_CPTE_AFECT = UN_TIPOCOMPROBANTE
                    AND DCCNT.CMPTE_AFECTADO = UN_COMPROBANTE
                    )
            LOOP
                IF DETALLE2.FECHA IS NOT NULL AND DETALLE2.FECHA < UN_FECHAACTUAL THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_RETORNO := 'El comprobante '
                                          || UN_TIPOCOMPROBANTE
                                          || ' No '
                                          || UN_COMPROBANTE
                                          || ' no puede tener fecha superior al '
                                          || DETALLE2.COMPROBANTE
                                          || ' No. '
                                          || DETALLE2.TIPO_CPTE;
                            RETURN MI_RETORNO;
                    END;
                END IF; 
            END LOOP;
        END;
        --JM FIN CC 4237
        
        --TICKET 7710260
        BEGIN 
            FOR RSCPTE IN (
                        SELECT DISTINCT ANO, TIPO_CPTE, COMPROBANTE
                        FROM   DETALLE_COMPROBANTE_CNT
                        WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA         = UN_COMPANIA
                        AND    DETALLE_COMPROBANTE_CNT.ANO_AFECT        =  UN_ANIO
                        AND    DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT  = UN_TIPOCOMPROBANTE
                        AND    DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO   = UN_COMPROBANTE
                    )
                LOOP
                IF RSCPTE.ANO IS NOT NULL THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_RETORNO := 'El comprobante '
                                          || UN_TIPOCOMPROBANTE
                                          || ' No '
                                          || UN_COMPROBANTE
                                          || ' está relacionado en el comprobante contable  '
                                          || RSCPTE.TIPO_CPTE
                                          || ' No '
                                          || RSCPTE.COMPROBANTE;
                            RETURN MI_RETORNO;
                    END;
                END IF;    
            END LOOP;                    
        END;
        
        BEGIN 
            FOR RSCPTE IN(
                        SELECT DISTINCT ANO, TIPO_CPTE, COMPROBANTE
                        FROM   DETALLE_COMPROBANTE_PPTAL
                        WHERE  DETALLE_COMPROBANTE_PPTAL.COMPANIA         = UN_COMPANIA
                        AND    DETALLE_COMPROBANTE_PPTAL.ANO_AFECT        = UN_ANIO
                        AND    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT  = UN_TIPOCOMPROBANTE
                        AND    DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO   = UN_COMPROBANTE
                    )
                LOOP
                IF RSCPTE.ANO IS NOT NULL THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_RETORNO := 'El comprobante '
                                          || UN_TIPOCOMPROBANTE
                                          || ' No '
                                          || UN_COMPROBANTE
                                          || ' está relacionado en el comprobante presupuestal '
                                          || RSCPTE.TIPO_CPTE
                                          || ' No '
                                          || RSCPTE.COMPROBANTE;
                            RETURN MI_RETORNO;
                    END;
                END IF;    
            END LOOP;                    
        END;
        --TICKET 7710260
       

        BEGIN
            BEGIN
                MI_TABLA := 'COMPROBANTE_CNT';
                MI_CAMPOS := 'FECHA = '|| 'TO_DATE('''||TO_CHAR(UN_FECHAACTUAL,'DD/MM/YYYY')||''',''DD/MM/YYYY HH24:MI:SS'')'||
                ' ,DATE_MODIFIED = SYSDATE, MODIFIED_BY   = 
                ''' || UN_USUARIO || ''' ';
                
                MI_CONDICION := ' COMPROBANTE_CNT.COMPANIA       =''' || UN_COMPANIA || '''
                 AND COMPROBANTE_CNT.TIPO = '''|| UN_TIPOCOMPROBANTE || '''
                 AND COMPROBANTE_CNT.NUMERO = '|| UN_COMPROBANTE || '
                 AND COMPROBANTE_CNT.ANO         = '|| UN_ANIO||'';
                DBMS_OUTPUT.PUT_LINE(MI_CONDICION);
                MI_RETORNO :=  PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                                UN_ACCION => 'M',
                                                UN_CAMPOS => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_RETORNO :='Error'||SQLCODE||' al realizar la actualizacion, motivo: '||SQLERRM;
            END;
        RETURN MI_RETORNO;
    END FC_CAMBIARFECHACOMPROBANTE;

PROCEDURE PR_PERPARARDATOSAJUSTEFISCAL
/*
    NAME              : PERPARARDATOSAJUSTEFISCAL
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÑOZ
    DATE MIGRADOR     : 11/12/2023
    TIME              : 08:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : MIGRACION DEL METODO PREPARAR DATOS AJUSTE FISCAL 
    MODIFICATIONS     : 
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO_FISCAL      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES_FISCAL      IN PCK_SUBTIPOS.TI_MES
  ) 
  AS
  MI_MESINI    PCK_SUBTIPOS.TI_MES;
  MI_MESFIN    PCK_SUBTIPOS.TI_MES;
  MI_STRSQL    PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQLUP  PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQLSE  PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQL1   PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
  MI_SALDO     VARCHAR2(100);
BEGIN
    -- INICIO
    -- MIGRACION DE LA PRIMERA SECCION DEL PROCESO, CONSULTA DE LA TABLA DETALLE_BALANCE_FISCAL Y SE INSERTA EN LA TEMPORAL TMP_SALDOFISCAL 
    DELETE FROM TMP_SALDOFISCAL;
    COMMIT;
    INSERT INTO TMP_SALDOFISCAL (COMPANIA, ANO, CUENTA, CODIGO_CUENTA, SUM_SALDO_DEBITO, SUM_SALDO_CREDITO) (SELECT COMPANIA, ANO, CUENTA, CODIGO_CUENTA, SUM(SALDO_DEBITO), SUM(SALDO_CREDITO) FROM DETALLE_BALANCE_FISCAL GROUP BY COMPANIA,ANO,CUENTA, CODIGO_CUENTA);
    
    -- MIGRACION DE LA SEGUNDA SECCION DEL PROCESO, SE CONSULTA EL PLAN_CONTABLE RELACIONADO CON LA TABLA TMP_SALDOFISCAL, ESTA IONFO SE ALMACENA EN LA TABLA TEMPORAL TMPBALANCEDEDUCIBLE
    MI_STRSQL := 'INSERT INTO TMPBALANCEDEDUCIBLE (COMPANIA, 
                                                    ANO,
                                                    ID,
                                                    CODIGO,
                                                    NATURALEZA,
                                                    CLASE,
                                                    SALDO,
                                                    SALDO1,
                                                    NOMBRE,
                                                    DEBITO,
                                                    CREDITO,
                                                    SALDOANTDEBITO,                             
                                                    SALDOANTCREDITO,                            
                                                    SALDONUEDEBITO,                             
                                                    SALDONUECREDITO,                            
                                                    SALDONUEDEBITONODEDUCIBLE,                  
                                                    SALDONUECREDITONODEDUCIBLE,                 
                                                    SALDOFISCAL,                                
                                                    SALDODEBITOFISCAL,                          
                                                    SALDOCREDITOFISCAL,                         
                                                    DEDUCIBLE,                                     
                                                    TERCERO,                               
                                                    AUXILIAR,                              
                                                    CENTRO_COSTO,                          
                                                    REFERENCIA_CONTABLE) 
                        (SELECT DISTINCT  PLAN_CONTABLE.COMPANIA, 
                             PLAN_CONTABLE.ANO,
                             PLAN_CONTABLE.ID,
                             PLAN_CONTABLE.CODIGO,
                             PLAN_CONTABLE.NATURALEZA,
                             SUBSTR(PLAN_CONTABLE.CODIGO,1,1) AS CLASE,
                             PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' AS SALDO,
                             PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' AS SALDO_1,
                             PLAN_CONTABLE.NOMBRE,
                             PLAN_CONTABLE.DEBITO'||UN_MES_FISCAL ||'  AS DEBITO, 
                             PLAN_CONTABLE.CREDITO'||UN_MES_FISCAL ||'   AS CREDITO,
                             (CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' >=0) THEN 
                                PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||'
                             ELSE
                                0
                             END + 
                             CASE WHEN (PLAN_CONTABLE.NATURALEZA = ''C'' AND PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' <0) THEN
                                -PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||'  
                             ELSE 
                                0
                             END) AS SALDOANTDEBITO,
                             (CASE WHEN (PLAN_CONTABLE.NATURALEZA=''C'' AND PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' >=0) THEN
                                PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' 
                             ELSE
                                0
                             END + 
                             CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||' <0) THEN
                                -PLAN_CONTABLE.SALDO'||TO_CHAR(UN_MES_FISCAL - 1)||'
                             ELSE
                                0
                             END) AS SALDOANTCREDITO,
                             (CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' >=0) THEN
                                PLAN_CONTABLE.SALDO'||UN_MES_FISCAL ||'
                             ELSE
                                0
                             END +
                             CASE WHEN (PLAN_CONTABLE.NATURALEZA=''C'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' <0) THEN
                                -PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||'
                             ELSE
                                0
                             END) AS SALDONUEDEBITO,
                             (CASE WHEN (PLAN_CONTABLE.NATURALEZA=''C'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' >=0) THEN
                                PLAN_CONTABLE.SALDO'||UN_MES_FISCAL ||' 
                             ELSE
                                0
                             END +
                             CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' <0) THEN
                                -PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||'
                             ELSE
                                0
                             END) AS SALDONUECREDITO,
                             (CASE WHEN P.DEDUCIBLE NOT IN (0) THEN
                                CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' >=0) THEN 
                                PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' 
                                 ELSE
                                    0
                                 END+
                                 CASE WHEN (PLAN_CONTABLE.NATURALEZA=''C'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' <0) THEN
                                    -PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' 
                                 ELSE
                                    0
                                 END
                             ELSE
                                0    
                             END) AS SALDONUEDEBITONODEDUCIBLE,
                             (CASE WHEN P.DEDUCIBLE NOT IN (0) THEN
                                CASE WHEN (PLAN_CONTABLE.NATURALEZA=''C'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' >=0) THEN
                                    PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' 
                                ELSE
                                    0 
                                END +
                                CASE WHEN (PLAN_CONTABLE.NATURALEZA=''D'' AND PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' <0) THEN
                                    -PLAN_CONTABLE.SALDO'||UN_MES_FISCAL||' 
                                ELSE
                                    0
                                END
                             ELSE
                                0
                             END) AS SALDONUECREDITONODEDUCIBLE,
                             0 AS SALDOFISCAL, 
                             NVL(TSF.SUM_SALDO_DEBITO,0) AS SALDODEBITOFISCAL, 
                             NVL(TSF.SUM_SALDO_CREDITO,0) AS SALDOCREDITOFISCAL, 
                             P.DEDUCIBLE, 
                             PLAN_CONTABLE.TERCERO,  
                             PLAN_CONTABLE.AUXILIAR, 
                             PLAN_CONTABLE.CENTRO_COSTO, 
                             PLAN_CONTABLE.REFERENCIA 
                      FROM V_PLAN_CONTABLE PLAN_CONTABLE 
                      INNER JOIN PLAN_CONTABLE P ON PLAN_CONTABLE.COMPANIA =  P.COMPANIA
                        AND PLAN_CONTABLE.ANO = P.ANO 
                        AND PLAN_CONTABLE.CODIGO =  P. CODIGO
                        LEFT JOIN TMP_SALDOFISCAL TSF             
                         ON  (PLAN_CONTABLE.COMPANIA     = TSF.COMPANIA)        
                         AND (PLAN_CONTABLE.ANO          = TSF.ANO)             
                         AND (PLAN_CONTABLE.ID           = TSF.CODIGO_CUENTA)   
                      WHERE PLAN_CONTABLE.COMPANIA = '''||UN_COMPANIA||''' 
                        AND PLAN_CONTABLE.ANO = '||UN_ANO_FISCAL||'  
                        AND (PLAN_CONTABLE.TERCERO IS NULL 
                                OR  PLAN_CONTABLE.AUXILIAR IS NULL 
                                OR PLAN_CONTABLE.CENTRO_COSTO IS NULL  
                                OR PLAN_CONTABLE.REFERENCIA IS NULL))';
    
    EXECUTE IMMEDIATE(MI_STRSQL);
    --LVEGA 7733592 
    FOR R IN (SELECT DISTINCT SUBSTR(CODIGO, 1, 2) ID FROM PLAN_CONTABLE WHERE DEDUCIBLE NOT IN (0)) LOOP
        -- LLEVA LOS SALDOS A LA TMP DE LAS CUENTAS PADRE ASOCIADAS A LAS CUENTAS MARCADAS CON SALDO NO DEDUCIBLE
    
        FOR I IN (
        SELECT SALDO1,SALDO2,SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12, CODIGO FROM PLAN_CONTABLE
                              WHERE PLAN_CONTABLE.COMPANIA = UN_COMPANIA
                                AND PLAN_CONTABLE.ANO = UN_ANO_FISCAL
                                AND PLAN_CONTABLE.CODIGO LIKE ''||R.ID||'%')LOOP
        
        CASE
        WHEN UN_MES_FISCAL = '1' THEN  MI_SALDO := I.SALDO1;
        WHEN UN_MES_FISCAL = '2' THEN  MI_SALDO := I.SALDO2;
        WHEN UN_MES_FISCAL = '3' THEN  MI_SALDO := I.SALDO3;
        WHEN UN_MES_FISCAL = '4' THEN  MI_SALDO := I.SALDO4;
        WHEN UN_MES_FISCAL = '5' THEN  MI_SALDO := I.SALDO5;
        WHEN UN_MES_FISCAL = '6' THEN  MI_SALDO := I.SALDO6;
        WHEN UN_MES_FISCAL = '7' THEN  MI_SALDO := I.SALDO7;
        WHEN UN_MES_FISCAL = '8' THEN  MI_SALDO := I.SALDO8;
        WHEN UN_MES_FISCAL = '9' THEN  MI_SALDO := I.SALDO9;
        WHEN UN_MES_FISCAL = '10' THEN  MI_SALDO := I.SALDO10;
        WHEN UN_MES_FISCAL = '11' THEN  MI_SALDO := I.SALDO11;
        ELSE  MI_SALDO := I.SALDO12;
        END CASE;
        
        MI_CAMPOS := 'SALDONUEDEBITONODEDUCIBLE = '|| MI_SALDO;
    
        MI_VALORES := 'TMPBALANCEDEDUCIBLE.COMPANIA = '''||UN_COMPANIA||'''  
                                    AND TMPBALANCEDEDUCIBLE.ANO = '||UN_ANO_FISCAL||'
                                    AND TMPBALANCEDEDUCIBLE.ID = '||I.CODIGO||'';
        
        BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'TMPBALANCEDEDUCIBLE',
                                       UN_ACCION    =>  'M',
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_VALORES);
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
       
        END;
        COMMIT;
        END LOOP;
    END LOOP;
    --LVEGA 7733592
    
    FOR C1 IN (SELECT * FROM TMPBALANCEDEDUCIBLE ORDER BY ID) LOOP
        -- MAYORIZAR CUENTA SEGUN C.CODIGO , EN ESTE PASO SE REALIZA LA SUMA DE LOS VALORES PARA LA MAYORIZACION
        UPDATE TMPBALANCEDEDUCIBLE SET 
                           SaldoNueCreditonodeducible = SaldoNueCreditonodeducible + NVL(C1.SaldoNueCreditonodeducible, 0), 
                           SALDODEBITOFISCAL = SALDODEBITOFISCAL + NVL(C1.SALDODEBITOFISCAL, 0), 
                           SALDOCREDITOFISCAL = SALDOCREDITOFISCAL + NVL(C1.SALDOCREDITOFISCAL, 0)
                           WHERE Compania =UN_COMPANIA 
                                And Ano = UN_ANO_FISCAL 
                                And (LENGTH(ID) < LENGTH(C1.ID)) 
                                And (ID = SUBSTR(C1.ID, 1, LENGTH(ID))); 
        
    END LOOP;
    
    UPDATE TMPBALANCEDEDUCIBLE 
              SET   SaldoFiscal =  CASE WHEN NATURALEZA = 'C' THEN
                                            (SaldoNueCredito - SaldoNueCreditonodeducible + SALDOCREDITOFISCAL) - (SaldoNueDebito - SaldoNueDebitonodeducible + SALDODEBITOFISCAL)
                                    ELSE
                                            (SaldoNueDebito - SaldoNueDebitonodeducible + SALDODEBITOFISCAL) - (SaldoNueCredito - SaldoNueCreditonodeducible + SALDOCREDITOFISCAL)
                                    END;
END PR_PERPARARDATOSAJUSTEFISCAL;

PROCEDURE PR_ELIMINAR_COMP_ING
/*
    NAME              : PR_ELIMINAR_COMP_ING
    AUTHORS           : CRISTIAN SUESCUN
    DATE              : 23/09/2024 
    TICKET            : 7751939
    DESCRIPTION       : BORRA LA INFORMACION DEL COMPROBANTE DE INGRESO DE LAS TABLAS
                        COMPROBANTE_PPTAL Y DETALLE_COMPROBANTE_PPTAL, SIEMPRE Y CUANDO CONTENGA EL MISMO
                        TIPO_DE_CRUCE Y NUMERO O COMPROBANTE
    @NAME:  eliminarComprobantesIngreso
    @METHOD:  DELETE
     */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO            IN VARCHAR2,
  UN_NUMERO          IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_TERCERO         IN PCK_SUBTIPOS.TI_TERCERO
) AS

MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;

  MI_TIPOCRUCE     VARCHAR2(100);

BEGIN
        -- Realiza una busqueda en la tabla TIPO_COMPROBANTE, en caso de no encontrar data se dispara la excepcion
        BEGIN
        SELECT TIPO_CRUCECUENTAS
        INTO MI_TIPOCRUCE
        FROM TIPO_COMPROBANTE
        WHERE COMPANIA = UN_COMPANIA
        AND CODIGO = UN_TIPO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_TIPOCRUCE := ''; --Se asigna una cadena vacia en caso de encontrar informacion
        END;
      
      IF MI_TIPOCRUCE IS NOT NULL THEN 
      BEGIN
       MI_CONDICION:=' COMPANIA = ''' || UN_COMPANIA   || '''
                   AND ANO  = ' || UN_ANO       || '
                   AND TIPO_CPTE = ''' || MI_TIPOCRUCE || ''' 
                   AND COMPROBANTE = ''' ||UN_NUMERO || ''' ';

            BEGIN
            --Si se encontraron registros, se realiza el proceso de DELETE tomando como entrada los valores en MI_CONDICION
              PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL', 
                                                  UN_ACCION    => 'E', -- La letra E corresponde a Eliminar
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        END;    
        
        BEGIN
       MI_CONDICION:=' COMPANIA = ''' || UN_COMPANIA   || '''
                   AND ANO  = ' || UN_ANO       || '
                   AND TIPO = ''' || MI_TIPOCRUCE || ''' 
                   AND NUMERO = ''' ||UN_NUMERO || ''' ';

            BEGIN
             --Si se encontraron registros, se realiza el proceso de DELETE tomando como entrada los valores en MI_CONDICION
              PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_PPTAL', 
                                                  UN_ACCION    => 'E', -- La letra E corresponde a Eliminar
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        END;   
      END IF;
    
END PR_ELIMINAR_COMP_ING;

END PCK_CONTABILIDAD;