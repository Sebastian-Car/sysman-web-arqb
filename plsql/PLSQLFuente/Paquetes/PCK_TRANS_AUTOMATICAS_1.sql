create or replace PACKAGE BODY PCK_TRANS_AUTOMATICAS AS

PROCEDURE PR_VALIDARORDEN(
      /*
      NAME              : FC_VALIDARORDEN
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 25/09/2018
      TIME              : 06:00 PM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      TIME              : 
      DATE MODIFIED     : 
      DESCRIPTION       : Valida el consecutivo de la tabla, por orden (Jf) no se realiza en un trigger
      MODIFICATIONS     : 

      @NAME:validarOrden
      @METHOD:GET
    */

    UN_COMPANIA   IN    D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANIO       IN    D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO       IN    D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO     IN    D_TRANSACCIONMODELO.NUMERO%TYPE, 
    UN_ORDEN      IN    D_TRANSACCIONMODELO.ORDEN%TYPE
)
AS
    MI_LETRAMAYOR       VARCHAR2(2 CHAR);
BEGIN       
    BEGIN
        BEGIN
            SELECT MAX(ORDEN) 
            INTO MI_LETRAMAYOR 
            FROM D_TRANSACCIONMODELO 
            WHERE COMPANIA = UN_COMPANIA 
            AND ANO = UN_ANIO
            AND TIPO = UN_TIPO
            AND NUMERO = UN_NUMERO;
        /*EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_LETRAMAYOR := UN_ORDEN;*/
        END;

        IF MI_LETRAMAYOR IS NULL THEN 
            MI_LETRAMAYOR := UN_ORDEN;
        END IF;

        IF UN_ORDEN <> MI_LETRAMAYOR THEN 
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'D_TRANSACCIONMODELO',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ORDENDETALLETRANSACC);
    END;    

END PR_VALIDARORDEN;

FUNCTION FC_CONSECUTIVOORDEN(
      /*
      NAME              : FC_CONSECUTIVOORDEN
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 18/07/2018
      TIME              : 10:00 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Valida el consecutivo de la tabla, por orden (JF) no se realiza en un trigger
      MODIFICATIONS     : 

      @NAME:   generarConsecutivoOrden
      @METHOD: GET
    */

    UN_COMPANIA     IN  D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANIO         IN  D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO         IN  D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO       IN  D_TRANSACCIONMODELO.NUMERO%TYPE,
    UN_TABLA        IN  PCK_SUBTIPOS.TI_TABLA

) RETURN VARCHAR2
AS
    MI_VALORLETRA       NUMBER(2,0);
    MI_RETORNO          VARCHAR2(2 CHAR);
    MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
    MI_ORDEN            NUMBER(2,0);
    MI_RS               SYS_REFCURSOR;

BEGIN  
      MI_CONSULTA:= ' SELECT ASCII(MAX(ORDEN))
                    FROM ' || UN_TABLA || '
                    WHERE COMPANIA = ''' ||UN_COMPANIA || '''
                    AND ANO = ' || UN_ANIO || '
                    AND TIPO = ''' || UN_TIPO || '''
                    AND NUMERO = ''' || UN_NUMERO || '''' ;

    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
      FETCH MI_RS INTO MI_VALORLETRA;
      EXIT WHEN MI_RS%NOTFOUND;
    END LOOP;
       IF MI_VALORLETRA IS NULL THEN 
          MI_RETORNO := 'A';
          RETURN MI_RETORNO ;
          END IF;

      BEGIN

          MI_VALORLETRA := MI_VALORLETRA + 1;

          IF (MI_VALORLETRA <= 90) THEN
              MI_RETORNO := CHR(MI_VALORLETRA);
          ELSE
             BEGIN
                  BEGIN
                      RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                  END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                          ,UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANTA_MAXIMOORDEN);
             END;     
          END IF;
      END;
RETURN MI_RETORNO;
END FC_CONSECUTIVOORDEN;

PROCEDURE PR_COPIARTRANSACCION(

 /*
      NAME              : PR_COPIARTRANSACCION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
      DATE MIGRADOR     : 25/09/2018
      TIME              : 06:00 PM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : permite copiar un transaccion modelo y sus detalles
      MODIFICATIONS     : 

      @NAME:copiarTransaccionModelo
      @METHOD:POST
    */
      UN_COMPANIA IN  PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO      IN  TRANSACCIONMODELO.ANO%TYPE,
      UN_TIPO     IN  TRANSACCIONMODELO.TIPO%TYPE,
      UN_NUMERO   IN  TRANSACCIONMODELO.NUMERO%TYPE,
      UN_USUARIO  IN  PCK_SUBTIPOS.TI_USUARIO
    )
AS 
      MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
      MI_RTA        NUMBER(2,0);
      MI_CONSECUTIVO  NUMBER(20,0);
BEGIN

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => 'TRANSACCIONMODELO',
                                                                   UN_CRITERIO => ' COMPANIA = ''' || UN_COMPANIA || 
                                                                                   ''' AND ANO = ' || UN_ANO || 
                                                                                   ' AND TIPO = ''' || UN_TIPO || '''' , 
                                                                   UN_CAMPO => 'NUMERO'
                                                                    );
    BEGIN



        FOR MI_RS IN (SELECT AUXILIAR,CENTRO_COSTO,CLASE,DESCRIPCION,FECHA
                         ,FUENTE_RECURSO,MOVIMIENTO,NUMERO,NUMEROGENERAR
                         ,PIDECTABANCOS,PIDEDOCASOCIADO,PIDEDOLARES,REFERENCIA
                         ,SUCURSAL,TERCERO,TEXTO,TIPOGENERAR,TODOSLOSCENTROS
                         ,VLRAGIRAR,VLRBASEGRAVABLE,VLRCOMPROBANTE
                         ,VLRIVAFACTURADO, TIPO_GASTO, MEDIO_PAGO, CONCEPTO, TIPO_OPERACION
                    FROM TRANSACCIONMODELO
                   WHERE COMPANIA = UN_COMPANIA
                     AND ANO = UN_ANO
                     AND TIPO = UN_TIPO
                     AND NUMERO = UN_NUMERO
                  ) 
        LOOP
            BEGIN
                BEGIN
                    MI_CAMPOS := ' ANO
                                  ,AUXILIAR
                                  ,CENTRO_COSTO
                                  ,CLASE
                                  ,COMPANIA
                                  ,DESCRIPCION
                                  ,FECHA
                                  ,FUENTE_RECURSO
                                  ,MOVIMIENTO
                                  ,NUMERO
                                  ,NUMEROGENERAR
                                  ,PIDECTABANCOS
                                  ,PIDEDOCASOCIADO
                                  ,PIDEDOLARES
                                  ,REFERENCIA
                                  ,SUCURSAL
                                  ,TERCERO
                                  ,TEXTO
                                  ,TIPO
                                  ,TIPOGENERAR
                                  ,TODOSLOSCENTROS
                                  ,VLRAGIRAR
                                  ,VLRBASEGRAVABLE
                                  ,VLRCOMPROBANTE
                                  ,VLRIVAFACTURADO
                                  ,TIPO_GASTO
                                  ,MEDIO_PAGO
                                  ,CONCEPTO
                                  .TIPO_OPERACION
                                  ,CREATED_BY,
                                  DATE_CREATED';
                    MI_VALORES :=  '' || UN_ANO        || '  ,''' ||
                                  MI_RS.AUXILIAR         || ''',''' ||
                                  MI_RS.CENTRO_COSTO     || ''',''' ||
                                  MI_RS.CLASE            || ''',''' || 
                                  UN_COMPANIA         || ''',''' || 
                                  MI_RS.DESCRIPCION      || ''',''' || 
                                  MI_RS.FECHA            || ''',''' || 
                                  MI_RS.FUENTE_RECURSO   || ''',' || 
                                  MI_RS.MOVIMIENTO       || ',' || 
                                  MI_CONSECUTIVO      || ',''' ||  
                                  MI_RS.NUMEROGENERAR    || ''',' ||  
                                  MI_RS.PIDECTABANCOS    || ',' ||  
                                  MI_RS.PIDEDOCASOCIADO  || ',' ||  
                                  MI_RS.PIDEDOLARES      || ',''' ||  
                                  MI_RS.REFERENCIA       || ''',''' ||  
                                  MI_RS.SUCURSAL         || ''',''' ||  
                                  MI_RS.TERCERO          || ''',''' ||  
                                  MI_RS.TEXTO            || ''',''' ||  
                                  UN_TIPO             || ''',''' ||  
                                  MI_RS.TIPOGENERAR      || ''',' ||  
                                  MI_RS.TODOSLOSCENTROS  || ',''' ||  
                                  MI_RS.VLRAGIRAR        || ''',''' ||  
                                  MI_RS.VLRBASEGRAVABLE  || ''',''' ||  
                                  MI_RS.VLRCOMPROBANTE   || ''',''' ||  
                                  MI_RS.VLRIVAFACTURADO || ''', ''' || 
                                  MI_RS.TIPO_GASTO || ''', ''' || 
                                  MI_RS.MEDIO_PAGO || ''', ''' || 
                                  MI_RS.CONCEPTO || ''', ''' || 
                                  MI_RS.TIPO_OPERACION || ''', ''' || 
                                  UN_USUARIO || ''',SYSDATE '; 
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TRANSACCIONMODELO', 
                                                 UN_ACCION    => 'I', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;  
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANTA_COPIARCOMPRON,
                  UN_TABLAERROR =>  'TRANSACCIONMODELO'
                  );              
                END;
        END LOOP;
    END;

  IF MI_RTA = 1 THEN
        BEGIN
            FOR MI_RS IN (SELECT  RETENCION
                              ,VALORDEBITO
                              ,VALORCREDITO
                              ,NATURALEZA
                              ,CLASEAPEDIR
                              ,CLASECUENTA
                              ,PEDIRMOSTRAR
                              ,MENSAJE
                              ,ORDEN
                              ,TERCERO
                              ,FUENTE_RECURSO
                              ,AUXILIAR
                              ,CENTRO_COSTO
                              ,REFERENCIA
                              ,FORMULA_BASE
                              ,FORMULA
                              ,MENSAJE_VALIDACION
                              ,COND_VALIDACION
                              ,COND_ALERTA
                              ,MENSAJE_ALERTA
                              ,FORMULAINTERNA
                              ,APLICACION
                              ,SUCURSAL
                              ,CODIGO_CUENTA
                              ,TIPORETENCION
                              ,CONCEPTORETENCION
                        FROM D_TRANSACCIONMODELO
                       WHERE COMPANIA = UN_COMPANIA
                         AND ANO = UN_ANO
                         AND TIPO = UN_TIPO
                         AND NUMERO = UN_NUMERO
                      ) 
            LOOP
                BEGIN
                    BEGIN
                        MI_CAMPOS := ' RETENCION
                                      ,NUMERO
                                      ,VALORDEBITO
                                      ,VALORCREDITO
                                      ,ANO
                                      ,NATURALEZA
                                      ,CLASEAPEDIR
                                      ,CLASECUENTA
                                      ,PEDIRMOSTRAR
                                      ,ORDEN
                                      ,MENSAJE
                                      ,COMPANIA
                                      ,TERCERO
                                      ,FUENTE_RECURSO
                                      ,AUXILIAR
                                      ,CENTRO_COSTO
                                      ,REFERENCIA
                                      ,FORMULA_BASE
                                      ,FORMULA
                                      ,MENSAJE_VALIDACION
                                      ,COND_VALIDACION
                                      ,COND_ALERTA
                                      ,MENSAJE_ALERTA
                                      ,FORMULAINTERNA
                                      ,APLICACION
                                      ,TIPO
                                      ,SUCURSAL
                                      ,CODIGO_CUENTA
                                      ,TIPORETENCION
                                      ,CONCEPTORETENCION
                                      ,DATE_CREATED
                                      ,CREATED_BY';
                        MI_VALORES :=   '' || MI_RS.RETENCION 
                                      || ',' || MI_CONSECUTIVO
                                      || ',' || MI_RS.VALORDEBITO
                                      || ',' || MI_RS.VALORCREDITO
                                      || ',' || UN_ANO
                                      || '  ,''' || MI_RS.NATURALEZA
                                      || ''',''' || MI_RS.CLASEAPEDIR
                                      || ''',''' || MI_RS.CLASECUENTA
                                      || ''',''' || MI_RS.PEDIRMOSTRAR
                                      || ''',''' || MI_RS.ORDEN
                                      || ''',''' || MI_RS.MENSAJE
                                      || ''',''' || UN_COMPANIA
                                      || ''',''' || MI_RS.TERCERO
                                      || ''',''' || MI_RS.FUENTE_RECURSO
                                      || ''',''' || MI_RS.AUXILIAR
                                      || ''',''' || MI_RS.CENTRO_COSTO
                                      || ''',''' || MI_RS.REFERENCIA
                                      || ''',''' || MI_RS.FORMULA_BASE
                                      || ''',''' || MI_RS.FORMULA
                                      || ''',''' || MI_RS.MENSAJE_VALIDACION
                                      || ''',''' || MI_RS.COND_VALIDACION
                                      || ''',''' || MI_RS.COND_ALERTA
                                      || ''',''' || MI_RS.MENSAJE_ALERTA
                                      || ''',''' || MI_RS.FORMULAINTERNA
                                      || ''',''' || MI_RS.APLICACION
                                      || ''',''' || UN_TIPO
                                      || ''',''' || MI_RS.SUCURSAL
                                      || ''',''' || MI_RS.CODIGO_CUENTA
                                      || ''',''' || MI_RS.TIPORETENCION
                                      || ''',''' || MI_RS.CONCEPTORETENCION
                                      || ''',SYSDATE,''' || UN_USUARIO || '''';
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'D_TRANSACCIONMODELO', 
                                                 UN_ACCION    => 'I', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;  
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    =>  SQLCODE,
                      UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANTA_COPIARDETCOMPROB,
                      UN_TABLAERROR =>  'D_TRANSACCIONMODELO'
                      );              
                    END;
            END LOOP;
        END;


        --Copiando centros de costos

        BEGIN
            FOR MI_RS IN (SELECT  CENTRO_COSTO
                        FROM TRANSACCIONMODELO_CC
                       WHERE COMPANIA = UN_COMPANIA
                         AND TIPO = UN_TIPO
                         AND NUMERO = UN_NUMERO
                      ) 
            LOOP
                BEGIN
                    BEGIN
                        MI_CAMPOS := ' NUMERO
                                      ,ANO
                                      ,COMPANIA
                                      ,TIPO
                                      ,CENTRO_COSTO
                                      ,DATE_CREATED
                                      ,CREATED_BY';
                        MI_VALORES :=   '' || MI_CONSECUTIVO
                                      || ',' || UN_ANO
                                      || ',''' || UN_COMPANIA
                                      || ''',''' || UN_TIPO
                                      || ''',''' || MI_RS.CENTRO_COSTO
                                      || ''',SYSDATE,''' || UN_USUARIO || '''';
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TRANSACCIONMODELO_CC', 
                                                 UN_ACCION    => 'I', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_VALORES   => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;  
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    =>  SQLCODE,
                      UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANTA_COPIARDETCOMPROB,
                      UN_TABLAERROR =>  'TRANSACCIONMODELO_CC'
                      );              
                    END;
            END LOOP;
        END;

    END IF;


END PR_COPIARTRANSACCION;

FUNCTION FC_CALCULARTRANSACCION 
( /*
      NAME              : FC_CALCULARTRANSACCION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : HENRY PUERTO
      DATE MIGRADOR     : 11/10/2018
      TIME              : 09:00 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : Yessica Sana
      TIME              : 03:00 PM
      DATE MODIFIED     : 06/11/2018
      DESCRIPTION       : Valida el consecutivo de la tabla
      MODIFICATIONS     : 
      1. Se validan los terceros de los detalles se configuren en los detalles del comprobante contable dado que se estaba heredando en del header.
      2. Cuando no se encontraba ultimo comprobante del tipo configurado se generaba error al aumentar el consecutivo dado que este venia null,
      se realiza modificacion para que inicie en año * 10000 si no se encuentra comprobante anterior
      3. Cuando se consultaba en la tabla SALDO_AUX_CONTABLE, Se filtra por tercero dado que se mostraban mas de un registro
      4. Se agrega variable UN_NUMEROMODELO para manejar restriccion en la tabla TRANSACCIONES

      @NAME:calcularTransaccion
      @METHOD:POST
    */
    UN_COMPANIA       IN  D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANO            IN  D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO           IN  D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO_MODELO  IN  TRANSACCIONES.NUMERO_MODELO%TYPE,
    UN_NUMERO         IN  TRANSACCIONES.NUMERO%TYPE,
    UN_USUARIO        IN  D_TRANSACCIONMODELO.CREATED_BY%TYPE
) RETURN CLOB
AS
    MI_X              VARCHAR2(1);
    MI_MENSAJE        VARCHAR2(32000);
    MI_MENSAJESALIDA  VARCHAR2(32000);
    MI_ALERTA         VARCHAR2(32000);
    MI_NUMERO         NUMBER:=0;
    MI_CUENTA         VARCHAR2(32):= '';
    MI_TERCERO        VARCHAR2(32);
    MI_SUCURSAL       VARCHAR2(3);
    MI_CENTRO_COSTO   VARCHAR2(32);
    MI_AUXILIAR       VARCHAR2(32);
    MI_REFERENCIA     VARCHAR2(32);
    MI_FUENTE         VARCHAR2(32);
    MI_CUENTARETENCION VARCHAR2(32);
    MI_CUENTA_PRESUPUESTAL   VARCHAR2(32);
    MI_SALDOA         NUMBER(20,2);
    MI_NETOA          NUMBER(20,2); 
    MI_SALDO          NUMBER(20,2);
    MI_NETO           NUMBER(20,2);
    MI_FORMULAINTERNA VARCHAR2(3200);
    MI_FORMULABASE    VARCHAR(32000);
    MI_I              INTEGER:= 0;
    MI_J              INTEGER:= 0;
    MI_VALOR          NUMBER(20,4):= 0;
    MI_COND           BOOLEAN:= FALSE;
    MI_NATURALEZA     VARCHAR2(1);
    MI_NATURALEZAB    VARCHAR2(1);
    MI_NATURALEZAP    VARCHAR2(1);
    MI_FVLRCOMPROBANTE VARCHAR2(254);
    MI_FVALORAGIRAR    VARCHAR2(254);
    MI_VALORBASE      NUMBER(25,4);
    MI_FECHA          DATE;
    TYPE TYPE_VALORES IS TABLE OF NUMERIC(20,2) INDEX BY PLS_INTEGER;
    MI_VALORES TYPE_VALORES;
    MI_CONDVALIDACION VARCHAR2(254);
    MI_CONDALERTA VARCHAR2(254);
    MI_CONDAPLICACION VARCHAR2(254);
    MI_ANO             NUMBER(4);
    MI_MES             NUMBER(4);
    MI_DESCRIPCION     VARCHAR2(254);
    UN_RETORNO         VARCHAR2(300 CHAR);
    MI_RETE           VARCHAR2(32000);
    MI_CIIU           VARCHAR2(10);
    MI_COMPANIARETE   VARCHAR2(3);
    MI_CODIGORETE     VARCHAR2(5);
    MI_CUENTADEBRETE  VARCHAR2(32);
    MI_CUENTACRERETE  VARCHAR2(32);
    MI_PCT_BASERETE   NUMBER(20,2);
    MI_LIMITE_INF     NUMBER(20,2);
    MI_PCT_APLICAR    NUMBER(20,2);
    MI_FACTORREDONDEO NUMBER(5 ,0);
    MI_CONTROL        NUMBER(5,0);
BEGIN
    MI_MENSAJE:=' ';
    --SI YA SE TIENEN DETALLES ESTOS NO SE PERMITEN MODIFICAR
    PR_CONTROLAR_TRANSACCION( UN_COMPANIA       => UN_COMPANIA,
                              UN_ANO            => UN_ANO,
                              UN_TIPO           => UN_TIPO,
                              UN_NUMERO_MODELO  => UN_NUMERO_MODELO,
                              UN_NUMERO         => UN_NUMERO); 

    DELETE TEMP_PLANA_AJUSTES
   	WHERE  COMPANIA    = UN_COMPANIA 
	  AND  TIPO_CPTE   = UN_TIPO 
	  AND  COMPROBANTE = 1 ;
--   ' Borra la tabla de ,mensajes de error
    DELETE MENSAJETRANSACCION 
	WHERE  COMPANIA=UN_COMPANIA  
	  AND  TIPO=UN_TIPO  
	  AND  NUMERO= 1;
--   'Valida el tipo de transacciÃƒÂ³n
    FOR MI_RS IN (
	         SELECT T.*,
                  TM.PIDECTABANCOS,
                  TP.TIPO TIPOGENERAR 
             FROM TRANSACCIONES T
            INNER JOIN TRANSACCIONMODELO TM
               ON T.COMPANIA      = TM.COMPANIA
              AND T.TIPO          = TM.TIPO
              AND T.ANO           = TM.ANO
              AND T.NUMERO_MODELO = TM.NUMERO
            INNER JOIN TIPOTRANSACCION TP
               ON T.COMPANIA = TP.COMPANIA
              AND T.TIPO     = TP.CODIGO              
            WHERE T.COMPANIA = UN_COMPANIA
			        AND T.TIPO          = UN_TIPO
			        AND T.NUMERO_MODELO = UN_NUMERO_MODELO
                    AND T.NUMERO        = UN_NUMERO
			 ) 
    LOOP
        MI_DESCRIPCION:= MI_RS.DESCRIPCION;
        MI_FECHA      := MI_RS.FECHA;
        MI_ANO        := EXTRACT(YEAR FROM MI_FECHA);
        MI_MES        := EXTRACT(MONTH FROM MI_FECHA);
        IF NVL(MI_RS.NUMERO_AFECTAR, '') <> ''  THEN
            --valida el comprobante contable afectado y su respectiva fecha
            BEGIN
                SELECT FECHA
                INTO MI_FECHA				
                FROM COMPROBANTE_CNT 
                WHERE COMPANIA = UN_COMPANIA 
                  AND ANO      = MI_ANO
                  AND TIPO     = MI_RS.TIPO_AFECTAR
                  AND NUMERO   = MI_RS.NUMERO_AFECTAR
                ORDER BY COMPANIA,ANO,TIPO,NUMERO;
                IF MI_FECHA > MI_RS.FECHA THEN
                    MI_MENSAJE:=MI_MENSAJE||'El Comprobante No '||MI_RS.TIPO_AFECTAR||' ' ||MI_RS.NUMERO_AFECTAR ||' fue realizado en una fecha posterior a la del comprobante que se esta realizado.';
                END IF;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_MENSAJE:=MI_MENSAJE+CHR(13)+CHR(10)+'El comprobante No '||MI_RS.TIPO_AFECTAR||' ' || MI_RS.NUMERO_AFECTAR ||' no existe';
            END;
            --valida el comprobante presupuestal afectado 
            BEGIN 
                SELECT NUMERO,    FECHA
                 INTO  MI_NUMERO, MI_FECHA		  
                 FROM  COMPROBANTE_PPTAL 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND  ANO      = MI_ANO
                  AND  TIPO     = MI_RS.TIPO_AFECTAR_PPTO 
                  AND  NUMERO   = MI_RS.NUMERO_AFECTAR_PPTO 
               ORDER BY COMPANIA,ANO,TIPO,NUMERO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_MENSAJE:=MI_MENSAJE||'El comprobante afectado Nro '||MI_RS.TIPO_AFECTAR||' ' ||MI_RS.NUMERO_AFECTAR_PPTO ||', no existe'||CHR(13)||CHR(10); 
            END;
        END IF;
		IF MI_RS.PIDECTABANCOS<>0 THEN
            IF MI_RS.CUENTADEBANCOS = '' THEN
                MI_MENSAJE:=MI_MENSAJE||'Se requiere la cuenta de bancos'||CHR(13)||CHR(10); 
            END IF;    
            BEGIN
                SELECT CODIGO,   NATURALEZA
                  INTO MI_CUENTA,MI_NATURALEZAB
                FROM   PLAN_CONTABLE 
                WHERE COMPANIA    = UN_COMPANIA 
                  AND ANO         = MI_ANO  
                  AND CODIGO      = MI_RS.CUENTADEBANCOS 
                  AND CLASECUENTA = 'B' 
                  AND (MOVIMIENTO + MAN_CEN_CTO+ MAN_AUX_TER + MAN_AUX_GEN+MAN_AUX_REF+MAN_AUX_FUE)<>0;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_MENSAJE:=MI_MENSAJE||'La cuenta :' || MI_RS.CUENTADEBANCOS ||' no es una cuenta de bancos o no tiene movimiento o no maneja auxiliar de terceros o no maneja auxiliar general '||CHR(13)||CHR(10);             
            END;
        END IF;

        PR_GETIONARERROR(UN_MENSAJE => MI_MENSAJE);
        FOR I IN 1.. 28 LOOP
            MI_VALORES(I) :=0;
        END LOOP;

        MI_I:= 0;
        FOR MI_RS1 IN
		       (SELECT DT.*,
   	 		   	       DM.PEDIRMOSTRAR,
	  		      	   DM.CLASEAPEDIR,
                       DM.NATURALEZA,
                       DM.CUENTA_PPTAL CUENTA_PRESUPUESTAL,
                       DM.CUENTA_RETENCION,
                       DM.FORMULA_BASE,
                       DM.COND_VALIDACION,
                       DM.MENSAJE_VALIDACION,
                       DM.COND_ALERTA,
                       DM.MENSAJE_ALERTA,
                       DM.APLICACION,
                       DM.FORMULA,
                       DM.TERCERO TERCERODETALLE,
                       DM.ORDEN ORDENMODELO,
                       DM.MENSAJE,
                       T.TERCERO TERCEROTRA,
                       T.SUCURSAL SUCURSALTRA
		        FROM D_TRANSACCIONMODELO DM LEFT JOIN (SELECT * 
                                                       FROM  D_TRANSACCIONES DTT
                                                       WHERE DTT.COMPANIA      = UN_COMPANIA 
                                                         AND DTT.TIPO          = UN_TIPO
                                                         AND DTT.ANO           = MI_ANO   
                                                         AND DTT.NUMERO_MODELO = UN_NUMERO_MODELO  
                                                         AND DTT.NUMERO        = UN_NUMERO  
                                                        ) DT
                  ON DT.COMPANIA      = DM.COMPANIA 
                 AND DT.ANO           = DM.ANO 
                 AND DT.TIPO          = DM.TIPO 
                 AND DT.NUMERO_MODELO = DM.NUMERO 
                 AND DT.ORDEN         = DM.ORDEN
                LEFT JOIN TRANSACCIONES T 
                   ON T.COMPANIA      = DT.COMPANIA  
                  AND T.ANO           = DT.ANO
                  AND T.TIPO          = DT.TIPO
                  AND T.NUMERO_MODELO = DT.NUMERO_MODELO
                  AND T.NUMERO        = DT.NUMERO                
	  		    WHERE DM.COMPANIA    = UN_COMPANIA 
                AND DM.TIPO          = UN_TIPO
                AND DM.ANO           = MI_ANO   
                AND DM.NUMERO        = UN_NUMERO_MODELO 
                AND (DT.NUMERO       = UN_NUMERO OR DT.NUMERO IS NULL)

           ORDER BY DM.ORDEN 
        )
		LOOP
            MI_I          := MI_I+1;
            MI_CODIGORETE :='';
            MI_CIIU       :='';
            IF MI_RS1.NUMERO_MODELO IS NULL THEN
                MI_VALOR:=0;
                MI_MENSAJESALIDA        := MI_MENSAJESALIDA|| 'No se Calcula la variable ' || MI_RS1.ORDENMODELO || ' - ' || MI_RS1.MENSAJE || CHR(10) || CHR(13); 
                --IF MI_RS1.PEDIRMOSTRAR = 'R' THEN
                --    MI_MENSAJESALIDA := MI_MENSAJESALIDA|| 'Variable ' || MI_RS1.ORDENMODELO || ', ' || SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 10) + 1, INSTR(MI_RETE,';',1, 11) - INSTR(MI_RETE,';',1, 10) -1) || CHR(10) || CHR(13);                     
                --END IF;
            ELSE

                MI_VALOR      := NVL(MI_RS1.VALOR,0);
                MI_VALORBASE  := 0;
                MI_NATURALEZA := MI_RS1.NATURALEZA;
                --VALIDA QUE EL VALOR NO SE CERO O MENOR QUE CERO CUANDO DEBE DIGITAR Y ES VALOR
                IF MI_RS1.PEDIRMOSTRAR = 'P' AND MI_RS1.CLASEAPEDIR = 'V' THEN
                    IF MI_RS1.VALOR <= 0 THEN
                        MI_MENSAJESALIDA:=MI_MENSAJESALIDA || 'Por favor Digite los valores de los detalles de clase Digitar.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                    END IF;
                END IF;
                --VALIDA QUE EL VALOR NO SE CERO O MENOR QUE CERO CUANDO DEBE DIGITAR Y SE PIDE LA CUENTA CONTABLE
                IF MI_RS1.PEDIRMOSTRAR = 'P' AND MI_RS1.CLASEAPEDIR = 'C' THEN
                    BEGIN
                        SELECT CODIGO,    NATURALEZA
                          INTO MI_CUENTA, MI_NATURALEZA
                        FROM   PLAN_CONTABLE 
                        WHERE COMPANIA = UN_COMPANIA
                          AND ANO      = MI_ANO 
                          AND CODIGO   = MI_RS1.CUENTA_CONTABLE
                          AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN+MAN_AUX_REF+MAN_AUX_FUE)<>0;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CUENTA:='';
                        MI_MENSAJE:=MI_MENSAJE||'No se encuentra la cuenta '||MI_RS1.CUENTA_CONTABLE||' o no tiene movimiento o no maneja ninguno de los auxiliares.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);

                    END;
                ELSIF MI_RS1.PEDIRMOSTRAR = 'P' AND MI_RS1.CLASEAPEDIR = 'P' THEN
                    --VALIDA QUE EL VALOR NO SE CERO O MENOR QUE CERO CUANDO DEBE DIGITAR Y SE PIDE LA CUENTA PRESUPUESTAL
                    BEGIN
                        SELECT CODIGO,NATURALEZA
                          INTO MI_CUENTA_PRESUPUESTAL,MI_NATURALEZAP
                          FROM PLAN_PRESUPUESTAL
                         WHERE COMPANIA = UN_COMPANIA
                           AND   ANO    = MI_ANO
                           AND   CODIGO = MI_RS1.CUENTA_PPTAL
                           AND  (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN+MAN_AUX_REF+MAN_AUX_FUE)<>0;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CUENTA_PRESUPUESTAL:='';
                        MI_MENSAJE:=MI_MENSAJE||'No se encuentra la cuenta '||MI_RS1.CUENTA_PRESUPUESTAL||' o no tiene movimiento o no maneja ninguno de los auxiliares.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);

                    END;
                ELSIF MI_RS1.PEDIRMOSTRAR = 'P' AND MI_RS1.CLASEAPEDIR = 'V' THEN
                    -- EL DATO DEBE VENIR EN VALOR
                    NULL;
                -- Adicionado para manejar la retencion de Comcaja marzo 14 de 2006  por JCVC
                ELSIF MI_RS1.PEDIRMOSTRAR = 'P' AND MI_RS1.CLASEAPEDIR = 'T' THEN
                    BEGIN
                        SELECT CODIGO,NATURALEZA
                          INTO MI_CUENTARETENCION,MI_NATURALEZA
                          FROM PLAN_CONTABLE 
                         WHERE COMPANIA = UN_COMPANIA
                           AND ANO    = MI_ANO
                           AND CODIGO = MI_RS1.CUENTA_RETENCION
                           AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN+MAN_AUX_REF+MAN_AUX_FUE)<>0;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CUENTARETENCION:='';
                        MI_MENSAJE:=MI_MENSAJE||'No se encuentra la cuenta de retenciÃ³n '||MI_RS1.CUENTA_RETENCION||' o no tiene movimiento o no maneja ninguno de los auxiliares' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);

                    END;
                END IF;
                --      ' Verifica que el tercero se encuentre en la transaccion modelo se encuentre
                -- @asana, 06/11/2018, Se validan los terceros de los detalles se configuren en los detalles del comprobante contable dado que se estaba heredando en del header.
                BEGIN
                    SELECT NIT,SUCURSAL
                      INTO MI_TERCERO,MI_SUCURSAL
                    FROM  TERCERO
                    WHERE COMPANIA =UN_COMPANIA
                      AND NIT      = MI_RS1.TERCERODETALLE
                      AND SUCURSAL = MI_RS1.SUCURSAL;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TERCERO :=PCK_DATOS.CONS_TERCERO ;
                    MI_SUCURSAL:=PCK_DATOS.CONS_SUCURSAL ;
                    MI_MENSAJE :=MI_MENSAJE||'El tercero '||MI_RS1.TERCERO||' no se encuentra registrado.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;
                BEGIN
                   SELECT CODIGO
                   INTO   MI_CENTRO_COSTO
                   FROM   CENTRO_COSTO
                   WHERE COMPANIA = UN_COMPANIA
                    AND  ANO      = MI_RS.ANO
                    AND  CODIGO   = MI_RS.CENTRO_COSTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CENTRO_COSTO:=PCK_DATOS.CONS_CENTRO;
                    MI_MENSAJE:=MI_MENSAJE||'El centro de costo  '||MI_RS.CENTRO_COSTO||' no se encuentra registrado.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;
                BEGIN
                   SELECT CODIGO
                   INTO   MI_AUXILIAR
                   FROM   AUXILIAR
                   WHERE COMPANIA = UN_COMPANIA
                    AND  ANO      = MI_RS.ANO
                    AND  CODIGO   = MI_RS.AUXILIAR;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_AUXILIAR:=PCK_DATOS.CONS_AUXILIAR;
                    MI_MENSAJE:=MI_MENSAJE||'El auxiliar general '||MI_RS.AUXILIAR||' no se encuentra registrado.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;
                BEGIN
                   SELECT CODIGO
                   INTO   MI_REFERENCIA
                   FROM   REFERENCIA
                   WHERE COMPANIA = UN_COMPANIA
                    AND  ANO      = MI_RS.ANO
                    AND  CODIGO   = MI_RS.REFERENCIA;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_REFERENCIA:=PCK_DATOS.CONS_REFERENCIA;
                     MI_MENSAJE:=MI_MENSAJE||'La referencia '||MI_RS.REFERENCIA||' no se encuentra registrada.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;

                BEGIN
                   SELECT CODIGO
                   INTO   MI_FUENTE
                   FROM   FUENTE_RECURSOS
                   WHERE COMPANIA = UN_COMPANIA
                    AND  ANO      = MI_RS.ANO
                    AND  CODIGO   = MI_RS.FUENTE_RECURSO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_FUENTE:=PCK_DATOS.CONS_FUENTE;
                    MI_MENSAJE:=MI_MENSAJE||'La fuente de recursos  '||MI_RS.FUENTE_RECURSO||' no se encuentra registrada.' || CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;
                PR_GETIONARERROR(UN_MENSAJE => MI_MENSAJE);
                --Evalua la formula general
                MI_FORMULABASE    := NVL(MI_RS1.FORMULA_BASE, '');
                MI_CONDVALIDACION := NVL(MI_RS1.COND_VALIDACION, '');
                MI_CONDALERTA     := NVL(MI_RS1.COND_ALERTA, '');
                MI_CONDAPLICACION := NVL(MI_RS1.APLICACION, '');
                IF MI_RS1.PEDIRMOSTRAR = 'P' THEN
                    IF MI_RS1.CLASEAPEDIR = 'C' THEN
                        MI_CUENTA := MI_RS1.CUENTA_CONTABLE;
                    ELSIF MI_RS1.CLASEAPEDIR = 'P' THEN
                        --' FALTA LA CLASE DE AFECTACION DE PRESUPUESTO
                        MI_CUENTA_PRESUPUESTAL := MI_RS1.CUENTA_PRESUPUESTAL;
                    ELSIF MI_RS1.CLASEAPEDIR = 'V' AND MI_RS1.CUENTA_CONTABLE <> '' THEN
                        MI_CUENTA := MI_RS1.CUENTA_CONTABLE;
                        --'PARA LA RETENCION   COMCAJA MARZO 15 2006 POR JCVC
                    ELSIF MI_RS1.CLASEAPEDIR = 'T' THEN
                        MI_CUENTA := MI_RS1.CUENTA_RETENCION;
                    ELSE
                        MI_CUENTA := MI_RS1.CUENTA_CONTABLE;
                    END IF;
                ELSE
                    MI_CUENTA := MI_RS1.CUENTA_CONTABLE;
                END IF;
                BEGIN
                    SELECT CASE MI_MES 
                          WHEN 1 THEN SALDO0
                          WHEN 2 THEN SALDO1
                          WHEN 3 THEN SALDO2
                          WHEN 4 THEN SALDO3
                          WHEN 5 THEN SALDO4
                          WHEN 6 THEN SALDO5
                          WHEN 7 THEN SALDO6
                          WHEN 8 THEN SALDO7
                          WHEN 9 THEN SALDO8
                          WHEN 10 THEN SALDO9
                          WHEN 11 THEN SALDO10
                          WHEN 12 THEN SALDO11
                          ELSE 0
                          END SALDOANT,
                         CASE MI_MES
                          WHEN 1 THEN SALDO1
                          WHEN 2 THEN SALDO2
                          WHEN 3 THEN SALDO3
                          WHEN 4 THEN SALDO4
                          WHEN 5 THEN SALDO5
                          WHEN 6 THEN SALDO6
                          WHEN 7 THEN SALDO7
                          WHEN 8 THEN SALDO8
                          WHEN 9 THEN SALDO9
                          WHEN 10 THEN SALDO10
                          WHEN 11 THEN SALDO11
                          ELSE  SALDO12
                          END SALDO,
                         CASE MI_MES
                          WHEN 1 THEN NETO0
                          WHEN 2 THEN NETO1
                          WHEN 3 THEN NETO2
                          WHEN 4 THEN NETO3
                          WHEN 5 THEN NETO4
                          WHEN 6 THEN NETO5
                          WHEN 7 THEN NETO6
                          WHEN 8 THEN NETO7
                          WHEN 9 THEN NETO8
                          WHEN 10 THEN NETO9
                          WHEN 11 THEN NETO10
                          WHEN 12 THEN NETO11
                          ELSE 0
                          END NETOANT,
                         CASE MI_MES
                          WHEN 1 THEN NETO1
                          WHEN 2 THEN NETO2
                          WHEN 3 THEN NETO3
                          WHEN 4 THEN NETO4
                          WHEN 5 THEN NETO5
                          WHEN 6 THEN NETO6
                          WHEN 7 THEN NETO7
                          WHEN 8 THEN NETO8
                          WHEN 9 THEN NETO9
                          WHEN 10 THEN NETO10
                          WHEN 11 THEN NETO11
                          ELSE NETO12
                          END NETO
                     INTO   MI_SALDOA,
                        MI_NETOA,
                        MI_SALDO,
                        MI_NETO
                     FROM   SALDO_AUX_CONTABLE
                     WHERE COMPANIA       = UN_COMPANIA
                       AND ANO            = MI_ANO
                       AND CODIGO         = MI_CUENTA
                       AND CENTRO_COSTO   = MI_CENTRO_COSTO
                       AND AUXILIAR       = MI_AUXILIAR
                       AND REFERENCIA     = MI_REFERENCIA
                       AND FUENTE_RECURSO = MI_FUENTE
                       --@asana, 06/11/2018, Se filtra por tercero dado que se mostraban mas de un registro y no permite seguir el proceso
                       AND TERCERO        = MI_TERCERO
                       AND SUCURSAL       = MI_SUCURSAL;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_SALDOA:=0;
                    MI_NETOA:=0;
                    MI_SALDO:=0;
                    MI_NETO:=0;
                END;
                IF MI_RS1.PEDIRMOSTRAR <> 'P' OR (MI_RS1.PEDIRMOSTRAR = 'P' AND NVL(MI_RS1.CUENTA_CONTABLE, '') = '') THEN
                    IF MI_RS1.PEDIRMOSTRAR = 'P' AND NVL(MI_RS1.FORMULA, '') <> '' AND MI_RS1.CLASEAPEDIR = 'T' THEN
                        MI_FORMULAINTERNA := MI_RS1.FORMULA; 
                    ELSIF MI_RS1.PEDIRMOSTRAR = 'P' AND NVL(MI_RS1.FORMULA, '') = '' THEN
                        MI_FORMULAINTERNA := MI_RS1.VALOR;
                    ELSE
                        MI_FORMULAINTERNA := NVL(MI_RS1.FORMULA, '');

                        MI_FORMULAINTERNA := REPLACE(MI_FORMULAINTERNA, 'NETOA',  TO_CHAR(MI_NETOA));
                        MI_FORMULAINTERNA := REPLACE(MI_FORMULAINTERNA, 'SALDOA', TO_CHAR(MI_SALDOA));
                        MI_FORMULAINTERNA := REPLACE(MI_FORMULAINTERNA, 'NETO',   TO_CHAR(MI_NETO));
                        MI_FORMULAINTERNA := REPLACE(MI_FORMULAINTERNA, 'SALDO',  TO_CHAR(MI_SALDO));
                        MI_FORMULABASE    := REPLACE(MI_FORMULABASE,    'SALDOA', TO_CHAR(MI_SALDOA ));
                        MI_FORMULABASE    := REPLACE(MI_FORMULABASE,    'SALDO',  TO_CHAR(MI_SALDO ));
                        MI_FORMULABASE    := REPLACE(MI_FORMULABASE,    'NETOA',  TO_CHAR(MI_NETOA ));
                        MI_FORMULABASE    := REPLACE(MI_FORMULABASE,    'NETO',   TO_CHAR(MI_NETO ));
                        MI_CONDVALIDACION := REPLACE(MI_CONDVALIDACION, 'SALDOA', TO_CHAR(MI_SALDOA ));
                        MI_CONDVALIDACION := REPLACE(MI_CONDVALIDACION, 'SALDO',  TO_CHAR(MI_SALDO ));
                        MI_CONDVALIDACION := REPLACE(MI_CONDVALIDACION, 'NETOA',  TO_CHAR(MI_NETOA ));
                        MI_CONDVALIDACION := REPLACE(MI_CONDVALIDACION, 'NETO',   TO_CHAR(MI_NETO ));
                        MI_CONDALERTA     := REPLACE(MI_CONDALERTA,     'SALDOA', TO_CHAR(MI_SALDOA ));
                        MI_CONDALERTA     := REPLACE(MI_CONDALERTA,     'SALDO',  TO_CHAR(MI_SALDO ));
                        MI_CONDALERTA     := REPLACE(MI_CONDALERTA,     'NETOA',  TO_CHAR(MI_NETOA ));
                        MI_CONDALERTA     := REPLACE(MI_CONDALERTA,     'NETO',   TO_CHAR(MI_NETO ));
                        MI_CONDAPLICACION := REPLACE(MI_CONDAPLICACION, 'SALDOA', TO_CHAR(MI_SALDOA ));
                        MI_CONDAPLICACION := REPLACE(MI_CONDAPLICACION, 'SALDO',  TO_CHAR(MI_SALDO));
                        MI_CONDAPLICACION := REPLACE(MI_CONDAPLICACION, 'NETOA',  TO_CHAR(MI_NETOA ));
                        MI_CONDAPLICACION := REPLACE(MI_CONDAPLICACION, 'NETO',   TO_CHAR(MI_NETO ));
                        MI_J:=1;
                        WHILE MI_J<MI_I LOOP
                            MI_FORMULAINTERNA := REPLACE(MI_FORMULAINTERNA,  CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_FORMULABASE    := REPLACE(MI_FORMULABASE,     CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_CONDVALIDACION := REPLACE(MI_CONDVALIDACION,  CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_CONDALERTA     := REPLACE(MI_CONDALERTA,      CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_CONDAPLICACION := REPLACE(MI_CONDAPLICACION,  CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_FVALORAGIRAR   := REPLACE(MI_FVALORAGIRAR,    CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_FVLRCOMPROBANTE:= REPLACE(MI_FVLRCOMPROBANTE, CHR(MI_J+64), TO_CHAR(MI_VALORES(MI_J)));
                            MI_J:=MI_J+1;
                        END LOOP;

                        IF MI_RS1.PEDIRMOSTRAR = 'R' THEN
                            MI_RETE:= PCK_CONTABILIDAD7.FC_CODIGORETENCIONTERCERO (
                                                      UN_COMPANIA   => UN_COMPANIA
                                                    , UN_ANIO       => MI_RS1.ANO
                                                    , UN_TERCERO    => MI_RS1.TERCEROTRA
                                                    , UN_SUCURSAL   => MI_RS1.SUCURSALTRA
                                                    , UN_TIPOFUENTE => MI_RS1.TIPORETENCION
                                                    , UN_CONCEPTO   => MI_RS1.CONCEPTORETENCION
                                                    );

                            MI_COMPANIARETE  := SUBSTR(MI_RETE, 1, INSTR(MI_RETE,';',1, 1) -1);
                            MI_CODIGORETE    := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 3) + 1, INSTR(MI_RETE,';',1, 4) - INSTR(MI_RETE,';',1, 3) -1);
                            MI_CUENTACRERETE := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 5) + 1, INSTR(MI_RETE,';',1, 6) - INSTR(MI_RETE,';',1, 5) -1);
                            MI_CUENTADEBRETE := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 4) + 1, INSTR(MI_RETE,';',1, 5) - INSTR(MI_RETE,';',1, 4) -1);        
                            MI_PCT_BASERETE  := TO_NUMBER(SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 6) + 1, INSTR(MI_RETE,';',1, 7) - INSTR(MI_RETE,';',1, 6) -1));
                            MI_LIMITE_INF    := TO_NUMBER(SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 7) + 1, INSTR(MI_RETE,';',1, 8) - INSTR(MI_RETE,';',1, 7) -1));
                            MI_PCT_APLICAR   := TO_NUMBER(SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 8) + 1, INSTR(MI_RETE,';',1, 9) - INSTR(MI_RETE,';',1, 8) -1));
                            MI_FACTORREDONDEO:= TO_NUMBER(SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 9) + 1, INSTR(MI_RETE,';',1, 10) - INSTR(MI_RETE,';',1, 9) -1));                         
                            MI_CIIU          := TO_NUMBER(SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 10) + 1, INSTR(MI_RETE,';',1, 11) - INSTR(MI_RETE,';',1, 10) -1));                         
                            IF NVL(MI_COMPANIARETE,' ') =' ' OR (NVL(MI_COMPANIARETE,' ') <> ' ' AND MI_CUENTACRERETE ='') THEN
                                MI_MENSAJESALIDA        := MI_MENSAJESALIDA|| 'Variable ' || MI_RS1.ORDEN || ', ' || SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 11) + 1, INSTR(MI_RETE,';',1, 12) - INSTR(MI_RETE,';',1, 11) -1) || CHR(10) || CHR(13); 
                                MI_FORMULAINTERNA := 0;
                                MI_FORMULABASE    := 0;
                            ELSE
                                --NUEVO PROCESO PARA CALCULAR LA RETENCION POR TERCERO
                                -- @ASANA, 26/03/2019 SE AGREGA VALIDACION PARA EJECUTAR LA OPERACION Y LUEGO REALIZAR VAIDACION CON MI_FORMULABASE

                                IF LENGTH(TRIM(MI_FORMULABASE)) >0 THEN
                                  BEGIN
                                      EXECUTE IMMEDIATE 'SELECT '||MI_FORMULABASE||' FROM DUAL ' INTO MI_VALORBASE;
                                  EXCEPTION WHEN OTHERS THEN
                                      MI_MENSAJE:=MI_MENSAJE||'Error en la formula de base gravable en el item  '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                                      MI_VALORBASE:=0;
                                  END;	 
                                MI_FORMULABASE := NVL(MI_VALORBASE,0);
                                MI_FORMULAINTERNA := 'CASE WHEN ' || MI_FORMULABASE || '<= ' || MI_LIMITE_INF || '
                                                          THEN 0 
                                                          ELSE ROUND(' || MI_FORMULABASE
                                                              || ' * ' || CASE WHEN MI_PCT_BASERETE >0 THEN MI_PCT_BASERETE/100 ELSE 0 END 
                                                              || ' * ' || CASE WHEN MI_PCT_APLICAR >0  THEN MI_PCT_APLICAR /100 ELSE 0 END 
                                                              || ' , ' || MI_FACTORREDONDEO
                                                              || ') END'; 

                               END IF;
                            END IF;
                        END IF;
                    END IF; 
                ELSE
                    MI_FORMULAINTERNA := TO_CHAR(MI_RS1.VALOR);
                END IF;
                IF LENGTH(TRIM(MI_FORMULAINTERNA)) <=0 THEN
                    MI_FORMULAINTERNA := '0';
                END IF;
                BEGIN
                    EXECUTE IMMEDIATE 'SELECT '||MI_FORMULAINTERNA||' FROM DUAL ' INTO MI_VALOR;
                EXCEPTION WHEN OTHERS THEN
                    MI_VALOR   := 0;
                    MI_MENSAJE := MI_MENSAJE||'Error en la formula en el item  '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);                    
                END;	 
            END IF;
            MI_VALORES(MI_I) := MI_VALOR;
            MI_VALORBASE     := 0;
            IF MI_RS1.NUMERO_MODELO IS NOT NULL THEN
                IF LENGTH(TRIM(MI_FORMULABASE)) >0 THEN
                    BEGIN

                        EXECUTE IMMEDIATE 'SELECT '||MI_FORMULABASE||' FROM DUAL ' INTO MI_VALORBASE;
                    EXCEPTION WHEN OTHERS THEN
                        MI_MENSAJE:=MI_MENSAJE||'Error en la formula de base gravable en el item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                        MI_VALORBASE:=0;
                    END;	 
                END IF;
                IF LENGTH(TRIM(MI_CONDAPLICACION)) >0 THEN
                    BEGIN
                        EXECUTE IMMEDIATE 'SELECT '||MI_CONDAPLICACION||' FROM DUAL ' INTO MI_COND;
                    EXCEPTION WHEN OTHERS THEN
                        MI_COND:=FALSE;
                        MI_MENSAJE:=MI_MENSAJE||'Error en la condicion de aplicacion en el item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                    END;	 
                    IF NOT MI_COND THEN
                        MI_VALOR:=0;
                    END IF;
                END IF;
                IF LENGTH(TRIM(MI_CONDVALIDACION)) >0 THEN
                    BEGIN
                        EXECUTE IMMEDIATE 'SELECT '||MI_CONDVALIDACION||' FROM DUAL ' INTO MI_COND;
                    EXCEPTION WHEN OTHERS THEN
                        MI_COND:=FALSE;
                        MI_MENSAJE:=MI_MENSAJE||'Error en la condicion de validacion en el item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                    END;	 
                    IF NOT MI_COND THEN
                        MI_VALOR:=0;
                    END IF;
                END IF;
                BEGIN
                    UPDATE D_TRANSACCIONES
                    SET    VALOR              = NVL(MI_VALOR,0),
                           VALOR_BASEGRAVABLE = NVL(MI_VALORBASE,0),
                           CODIGORETENCION    = MI_CODIGORETE,
                           CIIU               = MI_CIIU
                    WHERE  COMPANIA = UN_COMPANIA
                      AND  TIPO          = UN_TIPO
                      AND  NUMERO_MODELO = UN_NUMERO_MODELO
                      AND  NUMERO        = UN_NUMERO
                      AND  ORDEN         = CHR(64+MI_I);
                EXCEPTION WHEN OTHERS THEN
                    MI_MENSAJE:=MI_MENSAJE||'Error en la actualizacion del item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                END;			  
                IF LENGTH(TRIM(MI_CONDALERTA)) > 0 THEN
                    BEGIN
                        EXECUTE IMMEDIATE 'SELECT '||MI_CONDALERTA||' FROM DUAL ' INTO MI_COND;
                    EXCEPTION WHEN OTHERS THEN
                        MI_COND:=FALSE;
                        MI_MENSAJE:=MI_MENSAJE||'Error en la condicion de alerta en el item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                    END;
                    IF NOT MI_COND THEN
                        MI_ALERTA:=MI_ALERTA||'Alerta '||MI_RS1.MENSAJE_ALERTA||' en el item '||MI_RS1.ORDEN|| CHR(13)||CHR(10) || 'Variable ' || MI_RS1.DESCRIPCION || CHR(13)||CHR(10);
                    END IF;
                END IF;                
            END IF;
            PR_GETIONARERROR(UN_MENSAJE => MI_MENSAJE);            
        END LOOP;
    END LOOP;

    RETURN MI_MENSAJESALIDA;
END FC_CALCULARTRANSACCION;

FUNCTION FC_UPDATETRANSACCIONES
(  /*
      NAME              : PR_COPIARTRANSACCION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 26/11/2018
      TIME              : 08:00 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : permite copiar y modificar los detalles transaccion
      MODIFICATIONS     : 

      @NAME:modificarTransaccionesRete
      @METHOD:POST
    */
    UN_COMPANIA       IN  D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANO            IN  D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO           IN  D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO_MODELO  IN  TRANSACCIONES.NUMERO_MODELO%TYPE,
    UN_NUMERO         IN  TRANSACCIONES.NUMERO%TYPE,
    UN_USUARIO        IN  D_TRANSACCIONMODELO.CREATED_BY%TYPE
    ) RETURN CLOB
    AS 

    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA_ACME       NUMBER(20,0);
    MI_CONTROL        NUMBER(5,0);
    MI_RETE           VARCHAR2(32000);
    MI_SALIDA         CLOB;
    MI_TERCERO        VARCHAR2(32);
    MI_SUCURSAL       VARCHAR2(3);
    MI_COMPANIARETE   VARCHAR2(3);
    MI_CODIGORETE     VARCHAR2(5);
    MI_CODIGOCIIU     VARCHAR2(10);
    MI_CUENTACRERETE  VARCHAR2(32);
 BEGIN  
    MI_SALIDA :=' ';
    --SI YA SE TIENEN DETALLES ESTOS NO SE PERMITEN MODIFICAR
    PR_CONTROLAR_TRANSACCION( UN_COMPANIA       => UN_COMPANIA,
                              UN_ANO            => UN_ANO,
                              UN_TIPO           => UN_TIPO,
                              UN_NUMERO_MODELO  => UN_NUMERO_MODELO,
                              UN_NUMERO         => UN_NUMERO);
    MI_TABLA := 'D_TRANSACCIONES';
    BEGIN
        BEGIN
            MI_MERGEUSING := 'SELECT  COMPANIA,
                                    ANO,
                                    AUXILIAR,
                                    CENTRO_COSTO,
                                    CASE WHEN PEDIRMOSTRAR NOT IN(''R'') 
                                         THEN CODIGO_CUENTA 
                                         ELSE NVL(CODIGO_CUENTA,''2'') END CODIGO_CUENTA,
                                    CUENTA_PPTAL,
                                    MENSAJE,
                                    FUENTE_RECURSO,
                                    NUMERO,
                                    ORDEN,
                                    REFERENCIA,
                                    SUCURSAL,
                                    TERCERO,
                                    TIPO,
                                    CASE WHEN PEDIRMOSTRAR NOT IN(''R'') THEN NULL ELSE TIPORETENCION END TIPORETENCION,
                                    CASE WHEN PEDIRMOSTRAR NOT IN(''R'') THEN NULL ELSE CONCEPTORETENCION END CONCEPTORETENCION
                                   FROM D_TRANSACCIONMODELO        
                                   WHERE COMPANIA = ''' || UN_COMPANIA      || '''
                                     AND ANO      = '   || UN_ANO           || '
                                     AND TIPO     = ''' || UN_TIPO          || '''
                                     AND NUMERO   = ''' || UN_NUMERO_MODELO || '''';
                                     --AND PEDIRMOSTRAR NOT IN(''R'')' ;

            MI_MERGEENLACE := ' VISTA.COMPANIA = TABLA.COMPANIA
                            AND VISTA.ANO      = TABLA.ANO
                            AND VISTA.TIPO     = TABLA.TIPO 
                            AND ''' || UN_NUMERO || '''  = TABLA.NUMERO 
                            AND VISTA.NUMERO   = TABLA.NUMERO_MODELO
                            AND VISTA.ORDEN    = TABLA.ORDEN ';
            MI_MERGEEXISTE := ' UPDATE SET TABLA.AUXILIAR          = VISTA.AUXILIAR,
                                           TABLA.CENTRO_COSTO      = VISTA.CENTRO_COSTO,
                                           TABLA.CUENTA_PPTAL      = VISTA.CUENTA_PPTAL,
                                           TABLA.DESCRIPCION       = VISTA.MENSAJE,
                                           TABLA.FUENTE_RECURSO    = VISTA.FUENTE_RECURSO,
                                           TABLA.REFERENCIA        = VISTA.REFERENCIA,
                                           TABLA.SUCURSAL          = VISTA.SUCURSAL,
                                           TABLA.TERCERO           = VISTA.TERCERO,
                                           TABLA.CUENTA_CONTABLE   = VISTA.CODIGO_CUENTA,
                                           TABLA.TIPORETENCION     = VISTA.TIPORETENCION,
                                           TABLA.CONCEPTORETENCION = VISTA.CONCEPTORETENCION,
                                           TABLA.MODIFIED_BY       = ''' || UN_USUARIO || ''',
                                           TABLA.DATE_MODIFIED     = SYSDATE ';
            MI_MERGENOEXISTE := 'INSERT(  COMPANIA,
                                        ANO,
                                        AUXILIAR,
                                        CENTRO_COSTO,
                                        CUENTA_PPTAL,
                                        DESCRIPCION,
                                        FUENTE_RECURSO,
                                        NUMERO_MODELO,
                                        NUMERO,
                                        ORDEN,
                                        REFERENCIA,
                                        SUCURSAL,
                                        TERCERO,
                                        TIPO,
                                        CUENTA_CONTABLE,
                                        TIPORETENCION,
                                        CONCEPTORETENCION,
                                        CREATED_BY, 
                                        DATE_CREATED )
                                 VALUES( VISTA.COMPANIA,
                                        VISTA.ANO,
                                        VISTA.AUXILIAR,
                                        VISTA.CENTRO_COSTO,
                                        VISTA.CUENTA_PPTAL,
                                        VISTA.MENSAJE,
                                        VISTA.FUENTE_RECURSO,
                                        VISTA.NUMERO, ''' || 
                                        UN_NUMERO || ''',
                                        VISTA.ORDEN,
                                        VISTA.REFERENCIA,
                                        VISTA.SUCURSAL,
                                        VISTA.TERCERO,
                                        VISTA.TIPO,
                                        VISTA.CODIGO_CUENTA,
                                        VISTA.TIPORETENCION,
                                        VISTA.CONCEPTORETENCION,
                                        ''' || UN_USUARIO ||''',
                                        SYSDATE )';                            

            MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                             ,UN_ACCION      => 'IM'
                                             ,UN_MERGEUSING  => MI_MERGEUSING
                                             ,UN_MERGEENLACE => MI_MERGEENLACE
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                             ,UN_MERGENOEXIS => MI_MERGENOEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_INSERTADETALLES
                                ,UN_TABLAERROR => MI_TABLA);
    END;
    --CONSULTA EL TERCERO
    SELECT TERCERO, SUCURSAL
    INTO MI_TERCERO, MI_SUCURSAL
    FROM TRANSACCIONES
    WHERE COMPANIA      = UN_COMPANIA
      AND ANO           = UN_ANO
      AND TIPO          = UN_TIPO
      AND NUMERO_MODELO = UN_NUMERO_MODELO
      AND NUMERO        = UN_NUMERO;      

    <<RETENCIONES>>
    FOR RS IN(SELECT  COMPANIA,
                    ANO,
                    TIPO,
                    NUMERO,
                    ORDEN,
                    TIPORETENCION,
                    CONCEPTORETENCION
                   FROM D_TRANSACCIONMODELO        
                   WHERE COMPANIA =  UN_COMPANIA     
                     AND ANO      =  UN_ANO          
                     AND TIPO     =  UN_TIPO         
                     AND NUMERO   =  UN_NUMERO_MODELO 
                     AND PEDIRMOSTRAR IN('R'))
    LOOP
        MI_CODIGOCIIU :='';
        MI_RETE:= PCK_CONTABILIDAD7.FC_CODIGORETENCIONTERCERO (
                  UN_COMPANIA   => UN_COMPANIA
                , UN_ANIO       => UN_ANO
                , UN_TERCERO    => MI_TERCERO
                , UN_SUCURSAL   => MI_SUCURSAL
                , UN_TIPOFUENTE => RS.TIPORETENCION
                , UN_CONCEPTO   => RS.CONCEPTORETENCION
                );
        MI_COMPANIARETE  := SUBSTR(MI_RETE, 1, INSTR(MI_RETE,';',1, 1) -1);
        MI_CODIGORETE    := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 3) + 1, INSTR(MI_RETE,';',1, 4) - INSTR(MI_RETE,';',1, 3) -1);
        MI_CUENTACRERETE := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 5) + 1, INSTR(MI_RETE,';',1, 6) - INSTR(MI_RETE,';',1, 5) -1);
        MI_CODIGOCIIU    := SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 10) + 1, INSTR(MI_RETE,';',1, 11) - INSTR(MI_RETE,';',1, 10) -1); 
        MI_CONDICION := ' COMPANIA      =  ''' || UN_COMPANIA      || '''
                      AND ANO           =  '   || UN_ANO           || '
                      AND TIPO          =  ''' || UN_TIPO          || '''    
                      AND NUMERO_MODELO =  ''' || UN_NUMERO_MODELO || ''' 
                      AND NUMERO        =  '   || UN_NUMERO        || ' 
                      AND ORDEN         =  ''' || RS.ORDEN         || '''';       
        --ELIMINA LAS CUENTAS DE TIPO RETENCION QUE NO ESTAN CONFIGURADAS CORRECTAMENTE
        IF NVL(MI_COMPANIARETE,' ') =' ' OR (NVL(MI_COMPANIARETE,' ') <> ' ' AND MI_CUENTACRERETE ='') THEN
            MI_SALIDA := MI_SALIDA || 'Variable ' || RS.ORDEN || ', ' || SUBSTR(MI_RETE,    INSTR(MI_RETE,';',1, 11) + 1, INSTR(MI_RETE,';',1, 12) - INSTR(MI_RETE,';',1, 11) -1) || CHR(10) || CHR(13);     
            BEGIN
                BEGIN
                    MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA, 
                                                     UN_ACCION     => 'E', 
                                                     UN_CONDICION  => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                END;   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN     
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA);
            END;            
            IF MI_CUENTACRERETE ='' THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
                    MI_REEMPLAZOS (1).CLAVE := 'CODIGO';
                    MI_REEMPLAZOS (1).VALOR := MI_CODIGORETE;
                    MI_REEMPLAZOS (2).CLAVE := 'TIPO';
                    MI_REEMPLAZOS (2).VALOR := RS.TIPORETENCION;            
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_RETESINCUENTA,
                                               UN_TABLAERROR => 'RETENCIONES',
                                               UN_REEMPLAZOS => MI_REEMPLAZOS );
                END;
            END IF;
        ELSE
            IF NVL(MI_CUENTACRERETE,' ')=' ' THEN
                BEGIN 
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
                     MI_REEMPLAZOS (1).CLAVE := 'CODIGO';
                     MI_REEMPLAZOS (1).VALOR := MI_CODIGORETE;
                     MI_REEMPLAZOS (2).CLAVE := 'TIPO';
                     MI_REEMPLAZOS (2).VALOR := RS.TIPORETENCION;  
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_RETENCIONSINCUEN,
                                           UN_TABLAERROR => 'RETENCIONES',
                                           UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;
            END IF;
            BEGIN 
                 MI_CAMPOS    := 'CUENTA_CONTABLE  = ''' || MI_CUENTACRERETE || ''',
                                  CODIGORETENCION  = ''' || MI_CODIGORETE    || ''', 
                                  CIIU             = ''' || MI_CODIGOCIIU    || ''', 
                                  DATE_MODIFIED    = SYSDATE, 
                                  MODIFIED_BY      = ''' || UN_USUARIO       || ''' ';
                BEGIN
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                      UN_ACCION    => 'M', 
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE
                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_INSERTADETALLES
                      ,UN_TABLAERROR => MI_TABLA
                    );
            END;  
        END IF;
    END LOOP RETENCIONES;
    RETURN MI_SALIDA;
END FC_UPDATETRANSACCIONES;

PROCEDURE PR_GETIONARERROR
(
/*
      NAME              : PR_GETIONARERROR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 27/11/2018
      TIME              : 08:00 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Permite controlar los errores para evitar realizar muchos llamados y simplificar el codigo
      MODIFICATIONS     : 

      @NAME:modificarTransacciones
      @METHOD:POST
    */
    UN_MENSAJE    IN CLOB
) AS
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    IF NVL(UN_MENSAJE,' ')<>' ' THEN
        RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
    END IF;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
    MI_REEMPLAZOS (1).CLAVE := 'MENSAJE';
    MI_REEMPLAZOS (1).VALOR := UN_MENSAJE;
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                               UN_TABLAERROR => 'RETENCIONES',
                               UN_REEMPLAZOS => MI_REEMPLAZOS);
END PR_GETIONARERROR;

FUNCTION FC_VALIDAR_TRANSACCION
(  /*
      NAME              : FC_VALIDAR_TRANSACCION;
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 27/11/2018
      TIME              : 15:00
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Permite calcular la transaccion en base a los detalles del modelo de la transaccion
      MODIFICATIONS     : 

      @NAME:validarTransaccion
      @METHOD:POST
    */
    UN_COMPANIA       IN  D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANO            IN  D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO           IN  D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO_MODELO  IN  TRANSACCIONES.NUMERO_MODELO%TYPE,
    UN_NUMERO         IN  TRANSACCIONES.NUMERO%TYPE,
    UN_USUARIO        IN  D_TRANSACCIONMODELO.CREATED_BY%TYPE
    ) RETURN CLOB
    AS 
        MI_VALORDB        NUMBER(25,4); 
        MI_VALORCR        NUMBER(25,4);
        MI_I_CONTABLE     INTEGER:= 0;
        MI_FECHA          DATE; 
        MI_TIPOGENERAR    VARCHAR2(32);
        MI_CANTIDAD       NUMBER(5,0);
        MI_ANIOFINAL      NUMBER(5,0);
        MI_CONSECUTIVO    NUMBER(15);
        MI_MENSAJESALIDA  CLOB;
        MI_RTAPLANO       VARCHAR2(32000);
        MI_TERCEROHEADER  VARCHAR2(32);
        MI_SUCURSALHEADER VARCHAR2(3);
        MI_DESCRIPCION    VARCHAR2(32000);
        MI_I              INTEGER:= 0;
        MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
        MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
        MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
	  MI_CPTEINGRESOS   TIPO_COMPROBPP.CODIGO%TYPE;
        MI_EXISTE         NUMBER(5,0);    
        MI_LIST_EQPPTALES CLOB;
        MI_CANTREG        NUMBER(5,0);

        TYPE REG_RETE IS RECORD 
         (
            TIPORETENCION   COMPROBANTE_CNTRETENCION.TIPORETENCION%TYPE,
            CODIGORETENCION COMPROBANTE_CNTRETENCION.CODIGORETENCION%TYPE,
            VALOR           COMPROBANTE_CNTRETENCION.VALOR%TYPE,
            VALORBASE       COMPROBANTE_CNTRETENCION.VALOR%TYPE            
         ); 

        TYPE MI_COMRETETABLA IS TABLE OF REG_RETE INDEX BY BINARY_INTEGER;
        MI_COMRETE        MI_COMRETETABLA;
        MI_I_COMRETE      NUMBER(5,0) :=0;
        MI_CONTROL        NUMBER(5,0);      
        MI_AFECTAPPTO        NUMBER(5,0):=0;   
        MI_GASTO           NUMBER(5,0):=0;   
BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
    MI_MENSAJESALIDA := ' ';    
    PR_CONTROLAR_TRANSACCION( UN_COMPANIA       => UN_COMPANIA,
                              UN_ANO            => UN_ANO,
                              UN_TIPO           => UN_TIPO,
                              UN_NUMERO_MODELO  => UN_NUMERO_MODELO,
                              UN_NUMERO         => UN_NUMERO);

    MI_MENSAJESALIDA := MI_MENSAJESALIDA ||  NVL(FC_UPDATETRANSACCIONES(UN_COMPANIA      => UN_COMPANIA,
                                                       UN_ANO           => UN_ANO,
                                                       UN_TIPO          => UN_TIPO,
                                                       UN_NUMERO_MODELO => UN_NUMERO_MODELO,
                                                       UN_NUMERO        => UN_NUMERO,
                                                       UN_USUARIO       => UN_USUARIO),' ') || CHR(10) || CHR(13);
    MI_MENSAJESALIDA := MI_MENSAJESALIDA ||  NVL(FC_CALCULARTRANSACCION(UN_COMPANIA      => UN_COMPANIA,
                                                       UN_ANO           => UN_ANO,
                                                       UN_TIPO          => UN_TIPO,
                                                       UN_NUMERO_MODELO => UN_NUMERO_MODELO,
                                                       UN_NUMERO        => UN_NUMERO,
                                                       UN_USUARIO       => UN_USUARIO),' ') || CHR(10) || CHR(13);  

    MI_FECHA     := SYSDATE;
    MI_ANIOFINAL := TO_NUMBER(TO_CHAR(MI_FECHA,'YYYY'));

    SELECT TP.TIPO, T.TERCERO, T.SUCURSAL, NVL(T.DESCRIPCION ,' '), M.AFECTAPPTO
    INTO MI_TIPOGENERAR, MI_TERCEROHEADER, MI_SUCURSALHEADER, MI_DESCRIPCION, MI_AFECTAPPTO
    FROM TRANSACCIONES T INNER JOIN TIPOTRANSACCION TP
       ON T.COMPANIA = TP.COMPANIA
      AND T.TIPO     = TP.CODIGO  
    INNER JOIN TRANSACCIONMODELO M
       ON T.COMPANIA = M.COMPANIA
      AND T.ANO      = M.ANO
      AND T.TIPO     = M.TIPO  
      AND T.NUMERO_MODELO  = M.NUMERO
    WHERE T.COMPANIA      = UN_COMPANIA
      AND T.ANO           = UN_ANO
      AND T.TIPO          = UN_TIPO
      AND T.NUMERO_MODELO = UN_NUMERO_MODELO
      AND T.NUMERO        = UN_NUMERO
      AND T.TIPO_CPTE   IS NULL 
      AND T.COMPROBANTE IS NULL;

    FOR RS IN(SELECT DT.*, 
                     DM.PEDIRMOSTRAR,  
                     P.NATURALEZA NAT_CONTABLE, 
                     DM.NATURALEZA,
                     DM.CLASEAPEDIR
              FROM D_TRANSACCIONES DT INNER JOIN D_TRANSACCIONMODELO DM  
                  ON DT.COMPANIA      = DM.COMPANIA 
                 AND DT.ANO           = DM.ANO
                 AND DT.TIPO          = DM.TIPO 
                 AND DT.NUMERO_MODELO = DM.NUMERO 
                 AND DT.ORDEN         = DM.ORDEN    
              INNER JOIN PLAN_CONTABLE P
                  ON DT.COMPANIA        = P.COMPANIA
                 AND MI_ANIOFINAL       = P.ANO
                 AND DT.CUENTA_CONTABLE = P.CODIGO                 
              WHERE DT.COMPANIA      = UN_COMPANIA
                AND DT.ANO           = UN_ANO
                AND DT.TIPO          = UN_TIPO
                AND DT.NUMERO_MODELO = UN_NUMERO_MODELO
                AND DT.NUMERO        = UN_NUMERO
                AND DM.CLASEAPEDIR <> 'I'
                AND DT.VALOR       <> 0 )
    LOOP
        IF RS.PEDIRMOSTRAR = 'R' THEN
            MI_VALORDB := CASE WHEN RS.VALOR < 0 THEN -RS.VALOR ELSE 0 END ;
            MI_VALORCR := CASE WHEN RS.VALOR >= 0 THEN RS.VALOR ELSE 0 END ;
        ELSE           
            MI_VALORDB := CASE WHEN RS.NATURALEZA = 'D' THEN CASE WHEN RS.VALOR >= 0 THEN RS.VALOR ELSE 0 END ELSE CASE WHEN RS.VALOR < 0 THEN -RS.VALOR ELSE 0 END END;
            MI_VALORCR := CASE WHEN RS.NATURALEZA = 'C' THEN CASE WHEN RS.VALOR >= 0 THEN RS.VALOR ELSE 0 END ELSE CASE WHEN RS.VALOR < 0 THEN -RS.VALOR ELSE 0 END END;
        END IF;
        MI_I_CONTABLE := MI_I_CONTABLE + 1;
        MI_TABLA := 'TEMP_PLANA_AJUSTES';
         MI_CAMPOS:='COMPANIA,
                      ANO,
                      TIPO_CPTE,
                      COMPROBANTE,
                      CONSECUTIVO,
                      CUENTA,
                      FECHA,
                      DESCRIPCION,
                      VALOR_DEBITO,
                      VALOR_CREDITO,
                      BASE_GRAVABLE,
                      TERCERO,
                      SUCURSAL,
                      CENTRO_COSTO,
                      AUXILIAR,
                      REFERENCIA,
                      FUENTE_RECURSOS,
                      NATURALEZA';
        MI_VALORES:= '''' || UN_COMPANIA      || ''',' ||
                             MI_ANIOFINAL     ||   ',' ||
                     '''' || MI_TIPOGENERAR   || ''', ||
                             1, ' ||
                     '''' || MI_I_CONTABLE || ''',' ||  
                     '''' || RS.CUENTA_CONTABLE || ''',' ||
                     '''' || MI_FECHA || ''',' ||
                     '''' || RS.DESCRIPCION || ''',' ||                     
                             MI_VALORDB        ||   ',' ||
                             MI_VALORCR        ||   ',' ||
                             RS.VALOR_BASEGRAVABLE  ||   ',' ||
                     '''' || RS.TERCERO        ||   ''',' ||
                     '''' || RS.SUCURSAL       ||   ''',' ||
                     '''' || RS.CENTRO_COSTO       ||   ''',' ||
                     '''' || RS.AUXILIAR       ||   ''',' ||
                     '''' || RS.REFERENCIA       ||   ''',' ||
                     '''' || RS.FUENTE_RECURSO       ||   ''',' ||
                     '''' || RS.NAT_CONTABLE       ||   ''''; 
      /*  BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CREARDETALLESAFEC,
                          UN_TABLAERROR => MI_TABLA);
            END;   */              

        INSERT INTO TEMP_PLANA_AJUSTES
                         (COMPANIA,
                          ANO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          CUENTA,
                          FECHA,
                          DESCRIPCION,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          BASE_GRAVABLE,
                          TERCERO,
                          SUCURSAL,
                          CENTRO_COSTO,
                          AUXILIAR,
                          REFERENCIA,
                          FUENTE_RECURSOS,
                          NATURALEZA
                          )
        VALUES     (
                  UN_COMPANIA,
                  MI_ANIOFINAL,
                  MI_TIPOGENERAR,
                  1,
                  MI_I_CONTABLE,
                  RS.CUENTA_CONTABLE,
                  MI_FECHA,
                  RS.DESCRIPCION,
                  MI_VALORDB,
                  MI_VALORCR,
                  RS.VALOR_BASEGRAVABLE,
                  RS.TERCERO,
                  RS.SUCURSAL,
                  RS.CENTRO_COSTO,
                  RS.AUXILIAR,
                  RS.REFERENCIA,
                  RS.FUENTE_RECURSO,
                  RS.NAT_CONTABLE); 
        IF NVL(RS.TIPORETENCION,' ')<>' ' AND NVL(RS.CODIGORETENCION,' ')<>' ' THEN
            MI_I_COMRETE := MI_I_COMRETE + 1;
            MI_COMRETE(MI_I_COMRETE).TIPORETENCION     := RS.TIPORETENCION;
            MI_COMRETE(MI_I_COMRETE).CODIGORETENCION   := RS.CODIGORETENCION;
            MI_COMRETE(MI_I_COMRETE).VALOR             := MI_VALORCR;
            MI_COMRETE(MI_I_COMRETE).VALORBASE         := RS.VALOR_BASEGRAVABLE;
        END IF;
    END LOOP;

    SELECT SUM(VALOR_DEBITO) VALOR_DEBITO,SUM(VALOR_CREDITO) VALOR_CREDITO
    INTO  MI_VALORDB,MI_VALORCR
    FROM TEMP_PLANA_AJUSTES;

    IF MI_VALORDB<>MI_VALORCR THEN
        PR_GETIONARERROR(UN_MENSAJE => ' COMPROBANTE DESCUADRADO DEBITOS='||TO_CHAR(MI_VALORDB,'999,999,999,999,999.99')||' CREDITOS '||TO_CHAR(MI_VALORCR,'999,999,999,999,999.99'));
    END IF;  
    --CONSULTA EL CONSECUTIVO PARA CREAR EL COMPROBANTE CONTABLE
    BEGIN
        SELECT MAX(NUMERO)
        INTO   MI_CONSECUTIVO
        FROM   COMPROBANTE_CNT
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO      = MI_ANIOFINAL
          AND  TIPO     = MI_TIPOGENERAR;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CONSECUTIVO:=MI_ANIOFINAL*10000;
    END; 
    --@asana, 06/11/2018 Cuando no se encontraba ultimo comprobante del tipo configurado se generaba error al aumentar el consecutivo dado que este veni­a null
    IF MI_CONSECUTIVO IS NOT NULL THEN
        MI_CONSECUTIVO := MI_CONSECUTIVO+1;  
    ELSE 
        MI_CONSECUTIVO := MI_ANIOFINAL*10000;
    END IF;        
    UPDATE TEMP_PLANA_AJUSTES
       SET COMPROBANTE = MI_CONSECUTIVO
    WHERE COMPROBANTE  = 1;            
    BEGIN
        SELECT COUNT(1) CANTIDAD
        INTO MI_CANTIDAD
        FROM TEMP_PLANA_AJUSTES
        WHERE COMPANIA = UN_COMPANIA;
    END;
    IF MI_CANTIDAD NOT IN (0) THEN
        IF MI_AFECTAPPTO<>0 THEN
            SELECT COUNT(CLASECNTPRES.CLASEAFECTAR)
              INTO MI_GASTO  
              FROM TIPOTRANSACCION INNER JOIN TIPO_COMPROBPP   
                ON TIPOTRANSACCION.COMPANIA = TIPO_COMPROBPP.COMPANIA  
               AND TIPOTRANSACCION.TIPO     = TIPO_COMPROBPP.CODIGO 
             INNER JOIN CLASECNTPRES    
                ON TIPO_COMPROBPP.CLASE     = CLASECNTPRES.CODIGO 
             WHERE TIPOTRANSACCION.COMPANIA = UN_COMPANIA
               AND TIPOTRANSACCION.CODIGO   = UN_TIPO;
        END IF;
        -- @asana, 06/11/2018, Se validan los terceros de los detalles se configuren en los detalles del comprobante contable dado que se estaba heredando en del header.
        MI_RTAPLANO := PCK_CONTABILIZAR.FC_CONTABILIZAR
                                            (UN_COMPANIA         => UN_COMPANIA
                                            ,UN_TIPOCOMPROBANTE  => MI_TIPOGENERAR
                                            ,UN_NUMERO           => MI_CONSECUTIVO
                                            ,UN_ANO              => MI_ANIOFINAL
                                            ,UN_FECHA            => MI_FECHA
                                            ,UN_TERCERO          => MI_TERCEROHEADER
                                            ,UN_SUCURSAL         => MI_SUCURSALHEADER
                                            ,UN_DESCRIPCION      => MI_DESCRIPCION
                                            ,UN_SIMPLE           => 0
                                            ,UN_INDIMPRESION     => 0
                                            ,UN_INTOMITIRPPTAL   => MI_GASTO
                                            ,UN_USUARIO          => UN_USUARIO );        

        --Se actualiza los comprobantes y las fechas dentro de las transacciones
        UPDATE D_TRANSACCIONES
        SET FECHA       = MI_FECHA
        WHERE COMPANIA  = UN_COMPANIA
          AND TIPO      = UN_TIPO
          AND ANO       = UN_ANO
          AND NUMERO_MODELO = UN_NUMERO_MODELO
          AND NUMERO        = UN_NUMERO;

        UPDATE TRANSACCIONES
        SET TIPO_CPTE   = MI_TIPOGENERAR,
            COMPROBANTE = MI_CONSECUTIVO,
            FECHA       = MI_FECHA
        WHERE COMPANIA  = UN_COMPANIA
          AND TIPO      = UN_TIPO
          AND ANO       = UN_ANO
          AND NUMERO_MODELO = UN_NUMERO_MODELO
          AND NUMERO        = UN_NUMERO;

        MI_I := MI_COMRETE.COUNT;
        IF MI_COMRETE.COUNT >0 THEN
            FOR MI_I IN MI_COMRETE.FIRST .. MI_COMRETE.LAST LOOP
             INSERT INTO COMPROBANTE_CNTRETENCION(COMPANIA, ANO, TIPO, NUMERO, 
                                                  TIPORETENCION, CODIGORETENCION,
                                                  VALOR, VALORBASE, CALCULADO)
             VALUES (UN_COMPANIA, UN_ANO, MI_TIPOGENERAR, MI_CONSECUTIVO,
                     MI_COMRETE(MI_I).TIPORETENCION, MI_COMRETE(MI_I).CODIGORETENCION ,
                     MI_COMRETE(MI_I).VALOR,         MI_COMRETE(MI_I).VALORBASE ,-1);
            END LOOP;  
        END IF;
        IF MI_GASTO<>0 THEN
            PR_CREARCOMPRESUPUESTAL(UN_COMPANIA      => UN_COMPANIA,
                             UN_ANIO          => UN_ANO,
                             UN_TIPO          => UN_TIPO,  
                             UN_NUMERO_MODELO => UN_NUMERO_MODELO,
                             UN_NUMERO        => UN_NUMERO);
        END IF;

	  -- SE ADICIONA PARA VALIDAR SI PARA EL COMPROBANTE PRESUPUESTAL SE DEBE CREAR COMPROBANTE PRESUPUESTAL DE INGRESO 
        BEGIN
            SELECT TIPO_CRUCECUENTAS
            INTO    MI_CPTEINGRESOS
            FROM    TIPO_COMPROBANTE
            WHERE   COMPANIA    = UN_COMPANIA
            AND     CODIGO      = UN_TIPO;
            
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_CPTEINGRESOS := 'NA';
        END;        
        
        IF MI_CPTEINGRESOS <> 'NA' THEN
            -- VERIFICAR SI EL COMPROBANTE CONFIGURADO EXISTE EN LA CONFIGURACION DE COMPROBANTES PRESUPUESTALES
            BEGIN
                SELECT  COUNT(CODIGO)
                INTO    MI_EXISTE
                FROM    TIPO_COMPROBPP
                WHERE   COMPANIA = UN_COMPANIA
                AND     CODIGO = MI_CPTEINGRESOS;
                
                EXCEPTION WHEN NO_DATA_FOUND THEN 
                    MI_EXISTE := 0;
            END;
            
            IF MI_EXISTE > 0 THEN
                MI_CANTREG := 0;
                -- VERIFICAR SI EL COMPROBANTE CONTABLE TIENE CUENTAS PARA CREAR EL COMPROBANTE DE INGRESO
                FOR  RS IN (
                        SELECT D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO CUENTA, ABS(CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END) VALOR,        COUNT(D.COMPANIA) CONTADOR,        MIN(C.RUBRO) RUBRO_PPTAL 
                        FROM DETALLE_COMPROBANTE_CNT D 
                        INNER JOIN PLAN_CONTABLE P    ON D.COMPANIA = P.COMPANIA   AND D.ANO      = P.ANO    AND D.CUENTA   = P.CODIGO   
                        INNER JOIN PLAN_PPTAL_CUENTACNT C   ON P.COMPANIA    = C.COMPANIA  AND P.ANO         = C.ANO  AND P.CODIGO      = C.CUENTA_CONTABLE 
                        WHERE D.COMPANIA  = UN_COMPANIA   
                        AND D.ANO         = UN_ANO   
                        AND D.TIPO_CPTE   = UN_TIPO   
                        AND D.COMPROBANTE = MI_CONSECUTIVO
                        GROUP BY D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO,        CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END        ORDER BY CONSECUTIVO
                    )
                    LOOP
                    IF MI_LIST_EQPPTALES IS NULL THEN
                        MI_LIST_EQPPTALES := RS.CONSECUTIVO || ',' || RS.CUENTA || ',' || RS.VALOR  || ',' || RS.RUBRO_PPTAL;
                    ELSE
                        MI_LIST_EQPPTALES := MI_LIST_EQPPTALES || ',' || RS.CONSECUTIVO || ',' || RS.CUENTA || ',' || RS.VALOR  || ',' || RS.RUBRO_PPTAL;
                    END IF;
                    MI_CANTREG := MI_CANTREG + 1;
                END LOOP;
            END IF;
            IF LENGTH(MI_LIST_EQPPTALES) > 1 THEN
                PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL(
                                    UN_COMPANIA 		=> UN_COMPANIA,
                                    UN_ANO 				=> UN_ANO, 
                                    UN_TIPO 			=> UN_TIPO, 
                                    UN_NUMERO 			=> MI_CONSECUTIVO,
                                    UN_FECHA 			=> MI_FECHA	,
                                    UN_TERCERO 			=> MI_TERCEROHEADER,
                                    UN_SUCURSAL 		=> MI_SUCURSALHEADER,
                                    UN_DESCRIPCION 		=> MI_DESCRIPCION,
                                    UN_NUMERODOC 		=> '',
                                    UN_VALORDOC 		=>	0,
                                    UN_TIPOPPTAL 		=>	MI_CPTEINGRESOS,
                                    UN_CADENAINSERTAR	=>  MI_LIST_EQPPTALES,
                                    UN_CANTIDAD     	=>  MI_CANTREG,
                                    UN_USUARIO         	=>  UN_USUARIO);
            END IF;
        END IF;
	
        MI_MENSAJESALIDA := 'Comprobante creado correctamente';
    END IF;
    RETURN NVL(MI_MENSAJESALIDA,' ');
END FC_VALIDAR_TRANSACCION;

PROCEDURE PR_CONTROLAR_TRANSACCION
/*
      NAME              : FC_VALIDAR_TRANSACCION;
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 04/12/2018
      TIME              : 10:00
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Permite validar si la transaccion ya fue enviada a contabilidad
      MODIFICATIONS     : 

      @NAME:controlarTransaccion
      @METHOD:POST
    */
( 
    UN_COMPANIA       IN  D_TRANSACCIONMODELO.COMPANIA%TYPE,
    UN_ANO            IN  D_TRANSACCIONMODELO.ANO%TYPE,
    UN_TIPO           IN  D_TRANSACCIONMODELO.TIPO%TYPE,
    UN_NUMERO_MODELO  IN  TRANSACCIONES.NUMERO_MODELO%TYPE,
    UN_NUMERO         IN  TRANSACCIONES.NUMERO%TYPE
) AS
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TIPO_CPTE   TRANSACCIONES.TIPO_CPTE%TYPE;
    MI_COMPROBANTE TRANSACCIONES.COMPROBANTE%TYPE;
BEGIN
    BEGIN
        SELECT TIPO_CPTE, COMPROBANTE
        INTO MI_TIPO_CPTE, MI_COMPROBANTE
        FROM TRANSACCIONES
        WHERE COMPANIA      = UN_COMPANIA
          AND ANO           = UN_ANO
          AND TIPO          = UN_TIPO
          AND NUMERO_MODELO = UN_NUMERO_MODELO
          AND NUMERO        = UN_NUMERO;
        IF MI_TIPO_CPTE IS NOT NULL OR MI_COMPROBANTE IS NOT NULL  THEN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;    
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        NULL;
    END;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN  
    MI_REEMPLAZOS(0).CLAVE := 'TIPO';
    MI_REEMPLAZOS(0).VALOR := UN_TIPO;
    MI_REEMPLAZOS(1).CLAVE := 'MODELO';
    MI_REEMPLAZOS(1).VALOR := UN_NUMERO_MODELO;
    MI_REEMPLAZOS(2).CLAVE := 'NUMERO';
    MI_REEMPLAZOS(2).VALOR := UN_NUMERO;
    MI_REEMPLAZOS(3).CLAVE := 'TIPO_COM';
    MI_REEMPLAZOS(3).VALOR := MI_TIPO_CPTE;
    MI_REEMPLAZOS(4).CLAVE := 'COMPROBANTE';
    MI_REEMPLAZOS(4).VALOR := MI_COMPROBANTE;
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'TRANSACCIONES',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_TRANSACCIONCONCOM,
                 UN_REEMPLAZOS => MI_REEMPLAZOS
               );
END PR_CONTROLAR_TRANSACCION;

PROCEDURE PR_GUARDARAFECTACION(
      /*
      NAME              : PR_GUARDARAFECTACION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 14/11/2019
      TIME              : 11:10 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      TIME              : 
      DATE MODIFIED     : 
      DESCRIPTION       : Guarda los detalles de los comprobantes presupuestales que se afectaran con la transaccion
      MODIFICATIONS     : 

      @NAME: guardarAfectacion
      @METHOD: PUT
    */
    UN_COMPANIA      IN TRANSACCIONES_AFECTAPPTO.COMPANIA%TYPE,
    UN_ANIO          IN TRANSACCIONES_AFECTAPPTO.ANO%TYPE,
    UN_TIPO          IN TRANSACCIONES_AFECTAPPTO.TIPO%TYPE,
    UN_NUMERO_MODELO IN TRANSACCIONES_AFECTAPPTO.NUMERO_MODELO%TYPE, 
    UN_NUMERO        IN TRANSACCIONES_AFECTAPPTO.NUMERO%TYPE, 
    UN_DETALLES      IN PCK_SUBTIPOS.TI_STRSQL,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
    )
AS
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_DATOS_FILA     PCK_SYSMAN_UTL.T_SPLIT;
BEGIN       
    MI_TABLA := 'TRANSACCIONES_AFECTAPPTO';
    MI_CONDICION := 'COMPANIA      =''' || UN_COMPANIA      || '''' ||
               ' AND TIPO          =''' || UN_TIPO          || '''' ||
               ' AND ANO           ='   || UN_ANIO          ||
               ' AND NUMERO_MODELO =''' || UN_NUMERO_MODELO || '''' ||
               ' AND NUMERO        ='   || UN_NUMERO;
    --ELIMINAR LOS REGISTROS EXISTENTES
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA, 
                                                  UN_ACCION     => 'E', 
                                                  UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        END;   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN     
        PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_ELIMINADETALLEAFE,
                      UN_TABLAERROR => MI_TABLA);        
    END;
    IF NVL(UN_DETALLES,' ') <> ' ' THEN
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => UN_DETALLES,
                                                     UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG);
        /*
        RECORRER PARA INCORPORAR LOS NUEVOS REGISTROS
        */
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
            MI_CAMPOS:='COMPANIA,
                          TIPO,
                          ANO,
                          NUMERO_MODELO,
                          NUMERO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          VALOR,
                          CREATED_BY,
                          DATE_CREATED';
            MI_VALORES:= '''' || UN_COMPANIA      || ''',' ||
                         '''' || UN_TIPO          || ''',' ||
                                 UN_ANIO          ||   ',' ||
                         '''' || UN_NUMERO_MODELO || ''',' ||   
                                 UN_NUMERO        ||   ',' ||   
                         MI_DATOS_FILA(RS) || ',''' || 
                         UN_USUARIO || ''',SYSDATE'; 
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CREARDETALLESAFEC,
                          UN_TABLAERROR => MI_TABLA);
            END;     
        END LOOP;
    END IF;
END PR_GUARDARAFECTACION;

PROCEDURE PR_VALIDARAFECTACION
       /*
      NAME              : PR_VALIDARAFECTACION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 14/11/2019
      TIME              : 11:10 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      TIME              : 
      DATE MODIFIED     : 
      DESCRIPTION       : Permite validar que el valor y el tercero de la transaccion sea igual 
                          a los terceros de los detalles presupuestales a crear
      MODIFICATIONS     : 

      @NAME: guardarAfectacion
      @METHOD: PUT
    */
 (
    UN_COMPANIA      IN TRANSACCIONES_AFECTAPPTO.COMPANIA%TYPE,
    UN_ANIO          IN TRANSACCIONES_AFECTAPPTO.ANO%TYPE,
    UN_TIPO          IN TRANSACCIONES_AFECTAPPTO.TIPO%TYPE,
    UN_NUMERO_MODELO IN TRANSACCIONES_AFECTAPPTO.NUMERO_MODELO%TYPE, 
    UN_NUMERO        IN TRANSACCIONES_AFECTAPPTO.NUMERO%TYPE    
 )
 AS
    MI_TOTAL   NUMBER;
    MI_TOTALDETALLE NUMBER;
    MI_CANTERCERO   NUMBER :=0;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   

BEGIN 
    SELECT TRANSACCIONES.VALOR, SUM(NVL(TRANSACCIONES_AFECTAPPTO.VALOR,0)) TOTAL
    INTO MI_TOTAL, MI_TOTALDETALLE
    FROM TRANSACCIONES LEFT JOIN TRANSACCIONES_AFECTAPPTO
      ON TRANSACCIONES.COMPANIA  = TRANSACCIONES_AFECTAPPTO.COMPANIA
     AND TRANSACCIONES.TIPO  = TRANSACCIONES_AFECTAPPTO.TIPO
     AND TRANSACCIONES.ANO  = TRANSACCIONES_AFECTAPPTO.ANO
     AND TRANSACCIONES.NUMERO_MODELO  = TRANSACCIONES_AFECTAPPTO.NUMERO_MODELO
     AND TRANSACCIONES.NUMERO  = TRANSACCIONES_AFECTAPPTO.NUMERO     
    WHERE TRANSACCIONES.COMPANIA      = UN_COMPANIA
      AND TRANSACCIONES.TIPO          = UN_TIPO
      AND TRANSACCIONES.ANO           = UN_ANIO
      AND TRANSACCIONES.NUMERO_MODELO = UN_NUMERO_MODELO 
      AND TRANSACCIONES.NUMERO        = UN_NUMERO
    GROUP BY TRANSACCIONES.VALOR;

    IF MI_TOTAL<>MI_TOTALDETALLE THEN
        MI_MSGERROR(1).CLAVE := 'VALOR';
        MI_MSGERROR(1).VALOR := MI_TOTAL;
        MI_MSGERROR(2).CLAVE := 'DETALLE';
        MI_MSGERROR(2).VALOR := MI_TOTALDETALLE;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => -20001,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CONTROLAFECTADIF,
                      UN_REEMPLAZOS  => MI_MSGERROR  );
    END IF;

    SELECT COUNT(TRANSACCIONES.COMPANIA)
    INTO MI_CANTERCERO
    FROM TRANSACCIONES INNER JOIN TRANSACCIONES_AFECTAPPTO
      ON TRANSACCIONES.COMPANIA      = TRANSACCIONES_AFECTAPPTO.COMPANIA
     AND TRANSACCIONES.TIPO          = TRANSACCIONES_AFECTAPPTO.TIPO
     AND TRANSACCIONES.ANO           = TRANSACCIONES_AFECTAPPTO.ANO
     AND TRANSACCIONES.NUMERO_MODELO = TRANSACCIONES_AFECTAPPTO.NUMERO_MODELO
     AND TRANSACCIONES.NUMERO        = TRANSACCIONES_AFECTAPPTO.NUMERO    
    LEFT JOIN DETALLE_COMPROBANTE_PPTAL
      ON DETALLE_COMPROBANTE_PPTAL.COMPANIA      = TRANSACCIONES_AFECTAPPTO.COMPANIA
     AND DETALLE_COMPROBANTE_PPTAL.ANO           = TRANSACCIONES_AFECTAPPTO.ANO
     AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE     = TRANSACCIONES_AFECTAPPTO.TIPO_CPTE
     AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE   = TRANSACCIONES_AFECTAPPTO.COMPROBANTE
     AND DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO   = TRANSACCIONES_AFECTAPPTO.CONSECUTIVO   
    WHERE TRANSACCIONES.COMPANIA      = UN_COMPANIA
      AND TRANSACCIONES.TIPO          = UN_TIPO
      AND TRANSACCIONES.ANO           = UN_ANIO
      AND TRANSACCIONES.NUMERO_MODELO = UN_NUMERO_MODELO 
      AND TRANSACCIONES.NUMERO        = UN_NUMERO
      AND (TRANSACCIONES.TERCERO      <> DETALLE_COMPROBANTE_PPTAL.TERCERO
        OR TRANSACCIONES.SUCURSAL     <> DETALLE_COMPROBANTE_PPTAL.SUCURSAL);        

    IF MI_CANTERCERO>0 THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => -20001,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CONTROLAFECTATER);
    END IF;
EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_TOTAL:=0;
END PR_VALIDARAFECTACION;

PROCEDURE PR_CREARCOMPRESUPUESTAL

        /*
      NAME              : PR_CREARCOMPRESUPUESTAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 14/11/2019
      TIME              : 11:10 AM
      SOURCE MODULE     : TRANSACCIONES AUTOMATICAS
      MODIFIER          : 
      TIME              : 
      DATE MODIFIED     : 
      DESCRIPTION       : Genera el comprobante presupuestal
      MODIFICATIONS     : 

      @NAME: crearPresupuesto
      @METHOD: PUT
    */(
    UN_COMPANIA      IN TRANSACCIONES_AFECTAPPTO.COMPANIA%TYPE,
    UN_ANIO          IN TRANSACCIONES_AFECTAPPTO.ANO%TYPE,
    UN_TIPO          IN TRANSACCIONES_AFECTAPPTO.TIPO%TYPE,
    UN_NUMERO_MODELO IN TRANSACCIONES_AFECTAPPTO.NUMERO_MODELO%TYPE, 
    UN_NUMERO        IN TRANSACCIONES_AFECTAPPTO.NUMERO%TYPE    
 ) AS
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
 BEGIN
    PR_VALIDARAFECTACION(UN_COMPANIA      => UN_COMPANIA,
                         UN_ANIO          => UN_ANIO,
                         UN_TIPO          => UN_TIPO,  
                         UN_NUMERO_MODELO => UN_NUMERO_MODELO,
                         UN_NUMERO        => UN_NUMERO);

    BEGIN  
        --Crear el header
        MI_CAMPOS := ' COMPANIA, ' ||
                     ' ANO, ' ||
                     ' TIPO , ' ||
                     ' NUMERO, ' || 
                     ' TERCERO, ' ||
                     ' SUCURSAL, ' ||
                     ' CENTRO_COSTO, ' ||
                     ' AUXILIAR, ' ||
                     ' REFERENCIA, ' ||
                     ' FUENTE_RECURSO, ' ||
                     ' DESCRIPCION, ' ||
                     ' TEXTO, ' ||
                     ' VLR_DOCUMENTO, ' ||
                     ' FECHA, ' ||
                     ' FECHA_VCN_DOC, ' || 
                     ' NRO_DOCUMENTO';
        MI_VALORES := ' SELECT T.COMPANIA, T.ANO, T.TIPO_CPTE TIPO, T.COMPROBANTE, T.TERCERO, T.SUCURSAL, ' ||
                      '        T.CENTRO_COSTO, T.AUXILIAR, T.REFERENCIA, T.FUENTE_RECURSO,   ' ||
                      '        NVL(T.DESCRIPCION ,'' '') DESCRIPCION, NVL(T.DESCRIPCION ,'' '') TEXTO, ' ||
                      '        T.VALOR VLRDOCUMENTO, T.FECHA, T.FECHA, ' ||
                      '        T.TIPO || ''-'' || T.NUMERO_MODELO || ''-'' || NUMERO ' ||
                      ' FROM TRANSACCIONES T  ' ||
                      ' WHERE T.COMPANIA      =''' || UN_COMPANIA      || '''' ||
                        ' AND T.ANO           ='   || UN_ANIO          || 
                        ' AND T.TIPO          =''' || UN_TIPO          || '''' ||
                        ' AND T.NUMERO_MODELO =''' || UN_NUMERO_MODELO || '''' ||
                        ' AND T.NUMERO        ='   || UN_NUMERO;
        BEGIN
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_PPTAL', 
                                                UN_ACCION  => 'IS', 
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        END;  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CREARPPTAL,
              UN_TABLAERROR => 'COMPROBANTE_PPTAL'
           ); 
    END;

    --CREAR DETALLES PRESUPUESTALES
    BEGIN  
        --Crear el header
        MI_CAMPOS := ' COMPANIA, ' ||
                     ' ANO, ' ||
                     ' TIPO_CPTE , ' ||
                     ' COMPROBANTE, ' || 
                     ' CONSECUTIVO, ' || 
                     ' CUENTA, ' || 
                     ' DESCRIPCION, ' ||
                     ' VALOR_DEBITO, ' ||
                     ' FECHA, ' ||
                     ' CENTRO_COSTO, ' ||
                     ' TERCERO, ' ||
                     ' SUCURSAL, ' ||
                     ' AUXILIAR, ' ||
                     ' REFERENCIA, ' ||
                     ' FUENTE_RECURSO, ' ||
                     ' ANO_AFECT, ' ||
                     ' TIPO_CPTE_AFECT, ' ||
                     ' CMPTE_AFECTADO, ' ||
                     ' CONSECUTIVOPPTO, ' ||
                     ' NATURALEZA';
        MI_VALORES := ' SELECT T.COMPANIA, T.ANO, T.TIPO_CPTE, T.COMPROBANTE, ROWNUM CONSECUTIVO, ' ||
                      '        DET.CUENTA, NVL(T.DESCRIPCION ,'' '') DESCRIPCION, TA.VALOR, T.FECHA, ' ||
                      '        DET.CENTRO_COSTO, DET.TERCERO, DET.SUCURSAL, DET.AUXILIAR, DET.REFERENCIA, DET.FUENTE_RECURSO, ' ||
                      '        TA.ANO, TA.TIPO_CPTE, TA.COMPROBANTE, TA.CONSECUTIVO, DET.NATURALEZA ' ||
                      ' FROM TRANSACCIONES T INNER JOIN TRANSACCIONES_AFECTAPPTO TA ' ||
                      '   ON T.COMPANIA      = TA.COMPANIA
                         AND T.TIPO          = TA.TIPO
                         AND T.ANO           = TA.ANO
                         AND T.NUMERO_MODELO = TA.NUMERO_MODELO
                         AND T.NUMERO        = TA.NUMERO    
                        INNER JOIN DETALLE_COMPROBANTE_PPTAL DET
                          ON DET.COMPANIA      = TA.COMPANIA
                         AND DET.ANO           = TA.ANO
                         AND DET.TIPO_CPTE     = TA.TIPO_CPTE
                         AND DET.COMPROBANTE   = TA.COMPROBANTE
                         AND DET.CONSECUTIVO   = TA.CONSECUTIVO   ' ||
                      ' WHERE T.COMPANIA      =''' || UN_COMPANIA      || '''' ||
                        ' AND T.ANO           ='   || UN_ANIO          || 
                        ' AND T.TIPO          =''' || UN_TIPO          || '''' ||
                        ' AND T.NUMERO_MODELO =''' || UN_NUMERO_MODELO || '''' ||
                        ' AND T.NUMERO        ='   || UN_NUMERO;
        BEGIN
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL', 
                                                UN_ACCION  => 'IS', 
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        END;  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN

        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANTA_CREARPPTAL
           ); 
    END;
 END PR_CREARCOMPRESUPUESTAL;

END PCK_TRANS_AUTOMATICAS;