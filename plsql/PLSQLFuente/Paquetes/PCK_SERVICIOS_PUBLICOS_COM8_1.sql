create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM8 AS

--1
FUNCTION FC_REGISTRAR_ACTA_FINANCIABLE
    /*
       NAME              : PR_INSERTAERRORCALCULO --> Se separo de la funcion en access ManejarError
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
       DATE MIGRADOR     : 15/05/2017
       TIME              : 10:05 AM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Funcion que permite insertar y actualizar el acta de un financiable. Se pasa logica de Java a PlSQL.
       PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_IDACTA          => ID DEL ACTA A REGISTRAR
                           UN_CLASE           => CLASE DEL FINANCIABLE
                           UN_CICLO           => CICLO AL QUPERTENECE EL USUARIO
                           UN_CODIGORUTA      => Codigo de ruta del usuario.
                           UN_CONCEPTOINICIAL => CONCEPTO DEL FINANCIABLE INICIAL    
                           UN_CONCEPTOFINAL   => CONCEPTO DEL FINANCIABLE FINAL
                           UN_IMPRESO         => INDICADOR DE IMPERSO DEL ACTA
                           UN_PERIODO         => PERIODO DEL USUARIO
                           UN_USUARIO         => USUARIO QUE ACCEDE AL SISTEMA .
       MODIFICATIONS     :

       @NAME:    registrarActaFinanciable
       @METHOD:  GET
     */
(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_IDACTA          IN SP_CERTIFICADOSESTRATIFICACION.IDACTA%TYPE
    ,UN_CLASE           IN SP_CERTIFICADOSESTRATIFICACION.CLASE%TYPE
    ,UN_CICLO           IN SP_CERTIFICADOSESTRATIFICACION.CICLO%TYPE
    ,UN_CODIGORUTA      IN SP_CERTIFICADOSESTRATIFICACION.CODIGORUTA%TYPE
    ,UN_CONCEPTOINICIAL IN SP_FINANCIABLES.CONCEPTO%TYPE
    ,UN_CONCEPTOFINAL   IN SP_FINANCIABLES.CONCEPTO%TYPE
    ,UN_IMPRESO         IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_PERIODO         IN SP_FINANCIABLES.PERIODO%TYPE
    ,UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO 
AS 
    MI_RTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_EXISTE     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA; 
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    BEGIN 
        SELECT COUNT(IDACTA) IDACTA 
          INTO MI_EXISTE 
          FROM SP_CERTIFICADOSESTRATIFICACION 
         WHERE COMPANIA   = UN_COMPANIA 
           AND IDACTA     = UN_IDACTA
           AND CLASE      = UN_CLASE
           AND CICLO      = UN_CICLO
           AND CODIGORUTA = UN_CODIGORUTA;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE:=0;
    END;
    IF MI_EXISTE IN (0) THEN 
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => 'SP_CERTIFICADOSESTRATIFICACION'
                       ,UN_ERROR_COD  => PCK_ERRORES.ERROR_FACSERP_ACTA_FINANCIABLE
                       );
        END;
    END IF;
    IF UN_IMPRESO IN (0) THEN 
        MI_TABLA:='SP_FINANCIABLE_ACTA';
        MI_CAMPOS:= ' COMPANIA
                     ,IDACTA
                     ,CLASE
                     ,CONCEPTO
                     ,MONTOFINANCIAR
                     ,NUMEROCUOTAS
                     ,SALDOFINANCIABLE
                     ,VALORCUOTA
                     ,INTERES
                     ,NROCUOTA
                     ,ANOINICIAL
                     ,PERIODOINICIAL
                     ,FECHACREACION
                     ,USUARIO
                     ,BLOQUEADOHASTAANO
                     ,BLOQUEADOHASTAPERIODO
                     ,CREATED_BY
                     ,DATE_CREATED';
        MI_VALORES:= ' SELECT   COMPANIA
                              , '''||UN_IDACTA||'''         
                              , '''||UN_CLASE||'''
                              , CONCEPTO
                              , MONTOFINANCIAR
                              , NUMEROCUOTAS
                              , SALDOFINANCIABLE
                              , VALORCUOTA
                              , INTERES
                              , NROCUOTA
                              , ANOINICIAL
                              , PERIODOINICIAL
                              , FECHACREACION
                              , USUARIO
                              , BLOQUEADOHASTAANO
                              , BLOQUEADOHASTAPERIODO
                              , '''||UN_USUARIO||'''
                              ,SYSDATE
                         FROM SP_FINANCIABLES   
                        WHERE COMPANIA   = '''||UN_COMPANIA||'''
                          AND CICLO      = '''||UN_CICLO||'''
                          AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                          AND PERIODO    = '''||UN_PERIODO||'''
                          AND CONCEPTO   BETWEEN '||UN_CONCEPTOINICIAL||' AND '||UN_CONCEPTOFINAL;
        BEGIN
	        BEGIN 
	            MI_RTA:=PCK_DATOS.FC_ACME(
	            	              UN_TABLA   => MI_TABLA
	            	             ,UN_ACCION  => 'IS'
	            	             ,UN_CAMPOS  => MI_CAMPOS
	            	             ,UN_VALORES => MI_VALORES);
	        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
	            MI_REEMPLAZOS(0).CLAVE:='UN_CICLO';
              MI_REEMPLAZOS(0).VALOR:=UN_CICLO;
              MI_REEMPLAZOS(1).CLAVE:='UN_CODIGORUTA';
              MI_REEMPLAZOS(1).VALOR:=UN_CODIGORUTA;
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
	         UN_EXC_COD    => SQLCODE
            ,UN_TABLAERROR => MI_TABLA
	        ,UN_ERROR_COD  => PCK_ERRORES.ERROR_FACSERP_ACTA_FINANCIABL3
            ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
        MI_TABLA:='SP_CERTIFICADOSESTRATIFICACION';
        MI_CAMPOS:=' IMPRESO       = -1
                    ,DATE_MODIFIED = SYSDATE
                    ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
        MI_CONDICION:= '     COMPANIA = '''||UN_COMPANIA||''' 
                         AND IDACTA   = '''||UN_IDACTA||''' 
                         AND CLASE    = '''||UN_CLASE||''' ';
        BEGIN
	        BEGIN 
	            MI_RTA:=PCK_DATOS.FC_ACME(
	            	              UN_TABLA     => MI_TABLA
	            	             ,UN_ACCION    => 'M'
	            	             ,UN_CAMPOS    => MI_CAMPOS
	            	             ,UN_CONDICION => MI_CONDICION);
	        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
	            MI_REEMPLAZOS(0).CLAVE:='UN_IDACTA';
              MI_REEMPLAZOS(0).VALOR:=UN_IDACTA;
              MI_REEMPLAZOS(1).CLAVE:='UN_CLASE';
              MI_REEMPLAZOS(1).VALOR:=UN_CLASE;
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
	                      UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_ERROR_COD  => PCK_ERRORES.ERROR_FACSERP_ACTA_FINANCIABL2
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
    END IF;
    RETURN -1;
END FC_REGISTRAR_ACTA_FINANCIABLE;

PROCEDURE PR_AJUSTARESTADOUSUARIO
 /*
       NAME              : oprimirAceptar metodo AjustarestadousuarioControlador 
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
       DATE MIGRADOR     : 15/05/2017
       TIME              : 03:28 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Procedimiento que permite ajustar el estado de ls usuarios. Se pasa logica de Java a PlSQL.
       PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_USUARIO    => USUARIO QUE ACCEDE AL SISTEMA .
       MODIFICATIONS     :

       @NAME:    ajustarEstadoUsuario
       @METHOD:  GET
     */
(
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
AS
MI_COND         VARCHAR2(50 CHAR);
MI_PERIODO      VARCHAR2(50 CHAR);
MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS; 
MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
BEGIN
  <<CODRUTA>>
  FOR RS IN ( SELECT DISTINCT CODIGORUTA,
                              ESTADO,
                              NVL(FECHACAMBIOEST,SYSDATE) FECHACAMBIOEST,
                              SUBSIDIO,
                              SOBREPRECIO,
                              PERIODO,
                              CICLO,
                              SUBACUEDUCTO,
                              SUBALCANTARILLADO,
                              SUBASEO,
                              SUBFIJO,
                              SUBFIJOALC,
                              SUBCONSUMOAC,
                              SUBCONSUMOAL,
                              SOBREACUEDUCTO,
                              SOBREALCANTARILLADO,
                              SOBREASEO,
                              SOBREFIJO,
                              SOBREFIJOALC,
                              SOBRECONSUMOAC,
                              SOBRECONSUMOAL,
                              SUBSINMEDICION,
                              SOBRESINMEDICION,
                              SOBREALCSINMEDICION,
                              SUBALCSINMEDICION,
                              ANO,
                              COMPANIA,
                              NVL(TOTFACTURAPERACTUAL,0)
              FROM SP_USUARIO WHERE ESTADO='S'
              AND COMPANIA = UN_COMPANIA
              AND NVL(TOTFACTURAPERACTUAL,0)=0) 
  LOOP
    MI_COND:=EXTRACT(MONTH FROM RS.FECHACAMBIOEST) || EXTRACT(YEAR FROM RS.FECHACAMBIOEST);
    MI_PERIODO:=RS.PERIODO || RS.ANO;

    IF MI_COND != MI_PERIODO THEN
       MI_CAMPOS := 'SUBSIDIO = 0,SOBREPRECIO = 0,SUBACUEDUCTO=0,
                     SUBALCANTARILLADO = 0,SUBASEO =0,SUBFIJO =0,
                     SUBFIJOALC=0,SUBCONSUMOAC=0,SUBCONSUMOAL=0,
                     SOBREACUEDUCTO=0,SOBREALCANTARILLADO=0,SOBREASEO=0,
                     SOBREFIJO=0,SOBREFIJOALC=0,SOBRECONSUMOAC=0,
                     SOBRECONSUMOAL=0,SUBSINMEDICION=0,SOBRESINMEDICION=0,
                     SOBREALCSINMEDICION=0,SUBALCSINMEDICION=0,CARGOFIJO = 0, 
                     VALORBASICO = 0,VALORCOMPLEMENTARIO=0,VALORSUNTUARIO=0,
                     ACSINMEDICION=0,CARGOFIJOAL = 0,VALORALCBASICO =0,
                     VALORALCCOMPLEMENTARIO=0,VALORALCSUNTUARIO =0,ALSINMEDICION = 0,
                     VASEOUNICO=0,VASEODOMICILIARIO=0,VASEOBARRIDO=0,
                     VASEOCONSUMO=0,CONSUMOBASICO=0,CONSUMOCOMPLEMENTARIO=0,
                     CONSUMOSUNTUARIO=0,CONSUMO=0, DATE_MODIFIED =  SYSDATE,
                     MODIFIED_BY = ''' || UN_USUARIO ||'''';

       MI_CONDICION := 'COMPANIA            = '''||UN_COMPANIA||'''
                        AND CODIGORUTA      = '''||RS.CODIGORUTA||'''';
        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SP_USUARIO', 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERROR_FACSERP_ACT_VALORES
              );
        END;
    END IF;   
  END LOOP CODRUTA;


  <<USOHIST>>
  FOR RS IN ( SELECT DISTINCT SP_USUARIO.COMPANIA,
                              SP_USUARIO.CICLO,
                              SP_USUARIO.CODIGORUTA
              FROM SP_USUARIO
              LEFT JOIN SP_USUARIO_HISTORICOS
              ON SP_USUARIO.COMPANIA    = SP_USUARIO_HISTORICOS.COMPANIA
              AND SP_USUARIO.CICLO      = SP_USUARIO_HISTORICOS.CICLO
              AND SP_USUARIO.CODIGORUTA = SP_USUARIO_HISTORICOS.CODIGORUTA
              WHERE SP_USUARIO_HISTORICOS.COMPANIA  IS NULL) 
  LOOP
    MI_CAMPOS  := 'COMPANIA,CICLO,CODIGORUTA,CREATED_BY, DATE_CREATED ';
    MI_VALORES := ''''|| RS.COMPANIA    ||''',
                  ''' || RS.CICLO       ||''',
                  ''' || RS.CODIGORUTA  ||'''
                  ''' || UN_USUARIO     ||''', 
                  SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  'SP_USUARIO_HISTORICOS',
                                                UN_ACCION   =>  'I', 
                                                UN_CAMPOS   =>  MI_CAMPOS, 
                                                UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERROR_FACSERP_INS_DATOSHIST
                      );
    END;
  END LOOP USOHIST;


  <<USOINS>>
  FOR RS IN (SELECT DISTINCT  SP_USUARIO.COMPANIA,
                              CICLO,
                              CODIGORUTA
             FROM SP_USUARIO
             WHERE COMPANIA IS NULL) 
  LOOP
    MI_CAMPOS  := 'COMPANIA,CICLO,CODIGORUTA,CREATED_BY, DATE_CREATED ';
    MI_VALORES := ''''|| RS.COMPANIA   ||''',
                  ''' || RS.CICLO      ||''',
                  ''' || RS.CODIGORUTA ||'''
                  ''' || UN_USUARIO    ||''', 
                  SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  'SP_USUARIO',
                                                UN_ACCION   =>  'I', 
                                                UN_CAMPOS   =>  MI_CAMPOS, 
                                                UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERROR_FACSERP_INS_DATOS
                      );
    END;
  END LOOP USOINS;

  <<PAGUSOHIST>>
  FOR RS IN ( SELECT SP_USUARIO_HISTORICOS.COMPANIA,
                     SP_USUARIO_HISTORICOS.CICLO,
                     SP_USUARIO_HISTORICOS.CODIGORUTA,
                     SP_USUARIO_HISTORICOS.FECHAPAGO1,
                     SP_USUARIO_HISTORICOS.VALORPAGO1,
                     SP_USUARIO_HISTORICOS.BANCOPAGO1,
                     SP_USUARIO_HISTORICOS.ANO1,
                     SP_USUARIO_HISTORICOS.PERIODO1,
                     SP_PAGO.VALORPAGO,
                     SP_PAGO.BANCO
              FROM SP_USUARIO_HISTORICOS
              INNER JOIN SP_PAGO
              ON SP_USUARIO_HISTORICOS.COMPANIA    = SP_PAGO.COMPANIA
              AND SP_USUARIO_HISTORICOS.CODIGORUTA = SP_PAGO.CODIGORUTA
              AND SP_USUARIO_HISTORICOS.CICLO      = SP_PAGO.CICLO
              AND SP_USUARIO_HISTORICOS.FECHAPAGO1 = SP_PAGO.FECHA 
              WHERE SP_USUARIO_HISTORICOS.VALORPAGO1=0)
  LOOP
    MI_CAMPOS := 'VALORPAGO1      =  '''|| RS.VALORPAGO ||''', 
                  DATE_MODIFIED   =  SYSDATE,
                  MODIFIED_BY     =  '''|| UN_USUARIO   ||'''' ;

    MI_CONDICION := 'COMPANIA        = '''|| RS.COMPANIA    ||'''
                     AND CICLO       = '''|| RS.CICLO       ||'''
                     AND CODIGORUTA  = '''|| RS.CODIGORUTA  ||'''';
    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SP_USUARIO_HISTORICOS', 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_CONDICION);                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERROR_FACSERP_ACT_VALORPAGO
            );
    END;
  END LOOP PAGUSOHIST;
END PR_AJUSTARESTADOUSUARIO;


--


 FUNCTION FC_BORRAR_PAQUETE_PAGO 
    /*
    NAME              : FC_BORRAR_PAQUETE_PAGO
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 16/05/2017
    TIME              : 02:53
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Funcion encargada de borrar un paquete de pagos
    PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación
                        UN_BANCO       => 
                        UN_FECHA       => 
                        UN_PAQUETE     =>


    @NAME:  borrarPaquetePago
    @METHOD:  GET 
    */
(
  UN_COMPANIA       PCK_SUBTIPOS.TI_COMPANIA,
  UN_BANCO          VARCHAR2,
  UN_FECHA          DATE,
  UN_PAQUETE        SP_D_RECAUDO.NUMEROPAQUETE%TYPE

)

RETURN PCK_SUBTIPOS.TI_LOGICO AS 

 MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;         
 MI_CONSULTACANT                   PCK_SUBTIPOS.TI_STRSQL;
 MI_CANTIDAD                       PCK_SUBTIPOS.TI_ENTERO;
 MI_EXISTE                         PCK_SUBTIPOS.TI_ENTERO;
 MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
 MI_RTA                            PCK_SUBTIPOS.TI_RTA_ACME;
 MI_RESPUESTA                      PCK_SUBTIPOS.TI_ENTERO;
 MI_MSGERROR                       PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
 MI_CONSULTACANT :=' SELECT COUNT(*) CANTIDAD
                       FROM SP_USUARIO
                      WHERE COMPANIA              = '''||UN_COMPANIA||'''
                        AND BANCOPERPROCESO       = '''||UN_BANCO||'''
                        AND FECHAPAGOPERPROCESO   = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                        AND PAQUETEPAGOPERPROCESO = '''||UN_PAQUETE||'''';

 EXECUTE IMMEDIATE MI_CONSULTACANT INTO MI_CANTIDAD;

 IF MI_CANTIDAD > 0 THEN 

    MI_RESPUESTA := PCK_SERVICIOS_PUBLICOS_COM1.FC_HAYPERIODOSCERRADOS(UN_COMPANIA => UN_COMPANIA,
                                                                       UN_FECHA    => UN_FECHA,
                                                                       UN_BANCO    => UN_BANCO,
                                                                       UN_PAQUETE  => UN_PAQUETE);
    BEGIN                                                                        

        IF  MI_RESPUESTA = 0 THEN 
             RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;          
        END IF; 

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                UN_ERROR_COD  =>PCK_ERRORES.ERROR_CICLOS_CERRARDOS);

    END;

    RETURN 0;

 ELSE    
     MI_CONSULTA :=' SELECT COUNT(*) EXISTE
                    FROM SP_PAGO
                   WHERE COMPANIA      = '''||UN_COMPANIA||'''
                     AND BANCO         = '''||UN_BANCO||'''
                     AND FECHA         = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                     AND NUMEROPAQUETE = '''||UN_PAQUETE||'''';


     EXECUTE IMMEDIATE MI_CONSULTA INTO MI_EXISTE;

     IF MI_EXISTE NOT IN (0) THEN
       BEGIN
        BEGIN 

           MI_CONDICION := '     COMPANIA     = '''||UN_COMPANIA||'''
                            AND BANCO         = '''||UN_BANCO||'''
                            AND FECHA         =TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                            AND NUMEROPAQUETE = '''||UN_PAQUETE||'''';

           MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_PAGO',
                                       UN_ACCION    => 'E',
                                       UN_CONDICION => MI_CONDICION); 

            MI_MSGERROR(1).CLAVE := 'TABLA';
            MI_MSGERROR(1).VALOR := 'SP_PAGO';        
            MI_MSGERROR(2).CLAVE := 'BANCO';
            MI_MSGERROR(2).VALOR := UN_BANCO;
            MI_MSGERROR(2).CLAVE := 'FECHA';
            MI_MSGERROR(2).VALOR := UN_FECHA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                    UN_ERROR_COD=>PCK_ERRORES.ERROR_DELETE_PAGO,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                 
         END;

     END IF;

    BEGIN
      BEGIN
        MI_CONDICION := '    COMPANIA      = '''||UN_COMPANIA||'''
                         AND BANCO         = '''||UN_BANCO||'''
                         AND FECHA         = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                         AND NUMEROPAQUETE = '''||UN_PAQUETE||'''';

        MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_RECAUDO',
                                    UN_ACCION    => 'E',
                                    UN_CONDICION => MI_CONDICION); 

            MI_MSGERROR(1).CLAVE := 'TABLA';
            MI_MSGERROR(1).VALOR := 'SP_D_RECAUDO';        
            MI_MSGERROR(2).CLAVE := 'BANCO';
            MI_MSGERROR(2).VALOR := UN_BANCO;
            MI_MSGERROR(2).CLAVE := 'FECHA';
            MI_MSGERROR(2).VALOR := UN_FECHA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                    UN_ERROR_COD=>PCK_ERRORES.ERROR_DELETE_PAGO,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                 
         END;

    BEGIN   
      BEGIN
          MI_CONDICION := '     COMPANIA       = '''||UN_COMPANIA||'''
                            AND BANCO          = '''||UN_BANCO||'''
                            AND FECHA          = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                            AND NUMEROPAQUETE = '''||UN_PAQUETE||'''';

          MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_RECAUDOS',
                                      UN_ACCION    => 'E',
                                      UN_CONDICION => MI_CONDICION); 

          MI_MSGERROR(1).CLAVE := 'TABLA';
          MI_MSGERROR(1).VALOR := 'SP_RECAUDOS';        
          MI_MSGERROR(2).CLAVE := 'BANCO';
          MI_MSGERROR(2).VALOR := UN_BANCO;
          MI_MSGERROR(2).CLAVE := 'FECHA';
          MI_MSGERROR(2).VALOR := UN_FECHA;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                  UN_ERROR_COD=>PCK_ERRORES.ERROR_DELETE_PAGO,
                                   UN_REEMPLAZOS => MI_MSGERROR);                                 
       END;
 END IF;


  RETURN -1;
END FC_BORRAR_PAQUETE_PAGO;	

PROCEDURE PR_ACTUALIZAMETROSDESVIACION
  /*
        NAME              : PR_ACTUALIZAMETROSDESVIACION --> En Access evento actualizar Después de ESTADO formulario Desviación
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
        DATE MIGRADOR     : 17/05/2017
        TIME              : 11:07 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Actualiza los metros y desvíos en usuario e inserta la auditoría de desviación

        PARAMETERS        : UN_COMPANIA          => Compañia en la que se esta trabajando
                            UN_CICLO             => Ciclo seleccionado por el usuario 
                            UN_CODIGORUTA        => Código de Ruta seleccionado por el usuario.
                            UN_METROS            => Metros a cobrar, digitados en el diálogo que abre del formulario Desviaciones..
                            UN_PERIODOCOBRO      => Si tiene periodo de cobro el usuario o no 
                            UN_ANO               => Ano de la desviacion
                            UN_PERIODO           => Periodo de la desviacion
                            UN_USUARIO           => Usuario con el que se ingresa a la aplicación

        MODIFICATIONS     :

        @NAME:    actualizarMetrosDesviacion
        @METHOD:  POST
  */
   (
    UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO    	  IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA 	IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_METROS   	  IN PCK_SUBTIPOS.TI_ENTERO,
    UN_PERIODOCOBRO IN PCK_SUBTIPOS.TI_LOGICO,
    UN_ANO 			    IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_USUARIO 		  IN VARCHAR2
   )
    AS 
      MI_CAMPOS 		  PCK_SUBTIPOS.TI_CAMPOS;
      MI_CONDICION 	  PCK_SUBTIPOS.TI_CONDICION;
      MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
      MI_RTA 			    PCK_SUBTIPOS.TI_RTA_ACME;
      MI_CONSECUTIVO  PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_CALCULO      VARCHAR2(50 CHAR);
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;


    BEGIN
      MI_CAMPOS := 'SP_USUARIO.DESVIACIONAFORO = 0,';

      IF UN_PERIODOCOBRO <> 0 
      THEN MI_CAMPOS := MI_CAMPOS || 'SP_USUARIO.METROSDESVIACIONAFORO = 0,
                      SP_USUARIO.DESVIOSIGNIFICATIVO = -1,
                      SP_USUARIO.METROSDESVIACION = '||UN_METROS ;
      ELSE MI_CAMPOS := MI_CAMPOS || 'SP_USUARIO.METROSDESVIACIONAFORO = ' || UN_METROS;
      END IF;

      MI_CAMPOS := MI_CAMPOS || ', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
      MI_CONDICION := 'SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                AND SP_USUARIO.CICLO = '''||UN_CICLO||'''
                AND SP_USUARIO.CODIGORUTA = '''||UN_CODIGORUTA||'''';

      IF UN_PERIODOCOBRO = 0 
      THEN MI_CONDICION := MI_CONDICION || 'AND SP_USUARIO.DESVIACIONAFORO NOT IN(0)';
      END IF;

      BEGIN 
        BEGIN 
          MI_RTA := PCK_DATOS.FC_ACME (	UN_TABLA => 'SP_USUARIO',
                          UN_ACCION => 'M',
                          UN_CAMPOS => MI_CAMPOS,
                          UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_DESVIACIONUSUARIO
                                         ,UN_TABLAERROR => 'SP_USUARIO');          
          END;		

      IF MI_RTA > 0 
      THEN
        MI_CONDICION := 'SP_AUDITORIAMNUMOD.COMPANIA = '''||UN_COMPANIA||'''
                  AND SP_AUDITORIAMNUMOD.CICLO = '''||UN_CICLO||'''
                  AND SP_AUDITORIAMNUMOD.CODIGORUTA = '''||UN_CODIGORUTA||'''';
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(	UN_TABLA => 'SP_AUDITORIAMNUMOD',
                                  UN_CRITERIO => MI_CONDICION,
                                  UN_CAMPO => 'CONSECUTIVO',
                                  UN_INICIAL => '1');
        MI_CAMPOS := 'COMPANIA,CICLO,CODIGORUTA,CONSECUTIVO,CODIGO,CUENTA,HORA,FORMULARIO,CAMPO,VALORINICIAL,VALORFINAL,ANO,PERIODO,CREATED_BY,DATE_CREATED';
        MI_VALORES := ''''||UN_COMPANIA||''', '''||UN_CICLO||''','''||UN_CODIGORUTA||''','||MI_CONSECUTIVO||','''|| UN_COMPANIA || ' ^ '|| UN_CICLO || ' ^ ' || UN_CODIGORUTA ||''',
                '''||UN_USUARIO||''',SYSDATE,''DESVIACIONES'',''DESVIACIONAFORO'',-1,0,'||UN_ANO||','||UN_PERIODO||','''||UN_USUARIO||''',SYSDATE';
        BEGIN 
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME (	UN_TABLA => 'SP_AUDITORIAMNUMOD',
                            UN_ACCION => 'I',
                            UN_CAMPOS => MI_CAMPOS,
                            UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;                 
          END;									
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_AUDITORIADESVIACION
                                         ,UN_TABLAERROR => 'SP_AUDITORIAMNUMOD');          
        END;
      END IF;
      IF UN_PERIODOCOBRO <> 0 
      THEN MI_CALCULO := PCK_SERVICIOS_PUBLICOS_COM7.FC_CALCULOFACTURACION(	UN_COMPANIA => UN_COMPANIA,
                                          UN_INTCICLO => UN_CICLO,
                                          UN_STRCODIGOINICIAL => UN_CODIGORUTA,
                                          UN_STRCODIGOFINAL => UN_CODIGORUTA,
                                          UN_ENSERIE => 0,
                                          UN_USUARIO => UN_USUARIO);
        BEGIN
          IF MI_CALCULO <> 'Proceso ejecutado exitosamente'
          THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;   
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'CALCULAR';
              MI_MSGERROR(1).VALOR := MI_CALCULO;
              PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_CALFACTDESVIACION,
                            UN_TABLAERROR => 'SP_FACTURADO',
                            UN_REEMPLAZOS => MI_MSGERROR);    
        END;	
      END IF;
	END PR_ACTUALIZAMETROSDESVIACION;	

  FUNCTION FC_ABONARCUOTASFINANCIABLE
    /*
      NAME              : FC_ABONARCUOTASFINANCIABLE 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 23/05/2017
      TIME              : 10:32 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite registrar los abonos de la deuda de un financiable.
                          Ruta: Panel Principal\Facturación Servicios Públicos\Novedades\Abono a financiable de deuda.
      PARAMETERS        : UN_COMPANIA     => Compañia de ingreso a la aplicación.  
                          UN_CUOTAS       => Número de cuotas por abonar.
                          UN_CICLO        => Ciclo seleccionado por el usuario 
                          UN_CODIGORUTA   => Código de Ruta seleccionado por el usuario.
                          UN_PERIODO      => Periodo del financiable.
                          UN_ANIO         => Anio al que está registrado el financiable.
                          UN_USUARIO      => Usuario que realiza el registro del abono.
                          UN_CONSECUTIVO  => Consecutivo para registrar el financiable de la deuda.

      @NAME:  realizarAbonoCuotasFinanciable
      @METHOD:  GET
      */ 
    (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CUOTAS             IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA         IN PCK_SUBTIPOS.TI_CODIGORUTA, 
    UN_PERIODO           	IN PCK_SUBTIPOS.TI_PERIODO,
    UN_ANIO					      IN PCK_SUBTIPOS.TI_ANIO, 
    UN_USUARIO            IN VARCHAR2,
    UN_CONSECUTIVO        IN VARCHAR2
    )
    RETURN VARCHAR2 AS 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;  
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROREXC    PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
    BEGIN
      BEGIN
        MI_CAMPOS    := 'SALDOFINANCIABLE = CASE WHEN SALDOFINANCIABLE <= (VALORCUOTA*' || UN_CUOTAS || ') 
                                                 THEN 0 
                                                 ELSE SALDOFINANCIABLE - (VALORCUOTA*' || UN_CUOTAS ||  ') END, 
                         NROCUOTA         = NROCUOTA + ' || UN_CUOTAS || 
                      ', DATE_MODIFIED    = SYSDATE, 
                         MODIFIED_BY      = ''' || UN_USUARIO || '''';
        MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA || 
                     ''' AND CICLO      = '   || UN_CICLO ||
                       ' AND CODIGORUTA = ''' || UN_CODIGORUTA || 
                     ''' AND ANO        = '   || UN_ANIO ||
                       ' AND PERIODO    = ''' || UN_PERIODO || '''';            
        MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_FINANCIABLES',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
         MI_MSGERROREXC(1).CLAVE := 'OPERACION';
         MI_MSGERROREXC(1).VALOR := 'actualizaron';
         MI_MSGERROREXC(2).CLAVE := 'CODIGORUTA';
         MI_MSGERROREXC(2).VALOR := UN_CODIGORUTA;
         MI_MSGERROREXC(3).CLAVE := 'PERIODO';
         MI_MSGERROREXC(3).VALOR := UN_PERIODO;
         MI_MSGERROREXC(4).CLAVE := 'ANIO';
         MI_MSGERROREXC(4).VALOR := UN_ANIO;
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGABONOCUOTAS,
           UN_REEMPLAZOS => MI_MSGERROREXC,
           UN_TABLAERROR => 'SP_FINANCIABLES'
         );                      
      END;

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_FINANCIABLESDEUDAABONOS', 
                                                       UN_CRITERIO => ' COMPANIA = ''' || UN_COMPANIA ||
                                                                      ''' AND FINANCIABLE = ' || UN_CONSECUTIVO,
                                                       UN_CAMPO    => 'NUMERO',
                                                       UN_INICIAL  => '1');

    BEGIN
      BEGIN
        MI_CAMPOS   := 'COMPANIA,
                        FINANCIABLE,
                        NUMERO,
                        FECHA,
                        CUOTAS,
                        USUARIO,
                        DATE_CREATED,
                        CREATED_BY';
        MI_VALORES  := '''' || UN_COMPANIA || ''', ' ||
                       UN_CONSECUTIVO || ', ' || 
                       MI_CONSECUTIVO ||
                       ', SYSDATE, ' || 
                       UN_CUOTAS || ', ''' ||
                       UN_USUARIO || ''', 
                       SYSDATE, ''' || 
                       UN_USUARIO || '''';            
        MI_RTAACME  := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_FINANCIABLESDEUDAABONOS',
                                          UN_ACCION  => 'I',
                                          UN_CAMPOS  => MI_CAMPOS,
                                          UN_VALORES => MI_VALORES);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
         MI_MSGERROREXC(1).CLAVE := 'OPERACION';
         MI_MSGERROREXC(1).VALOR := 'insertaron';
         MI_MSGERROREXC(2).CLAVE := 'CODIGORUTA';
         MI_MSGERROREXC(2).VALOR := UN_CODIGORUTA;
         MI_MSGERROREXC(3).CLAVE := 'PERIODO';
         MI_MSGERROREXC(3).VALOR := UN_PERIODO;
         MI_MSGERROREXC(4).CLAVE := 'ANIO';
         MI_MSGERROREXC(4).VALOR := UN_ANIO;
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGABONOCUOTAS,
           UN_REEMPLAZOS => MI_MSGERROREXC,
           UN_TABLAERROR => 'SP_FINANCIABLESDEUDAABONOS'
         );                      
      END;
    RETURN MI_RTAACME;
    END FC_ABONARCUOTASFINANCIABLE;

PROCEDURE PR_RECONSTRUCCIONRECAUCONCEPTO 
  /*
      NAME              : PR_RECONSTRUCCIONRECAUCONCEPTO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
      DATE MIGRADO      : 30/05/2017
      TIME              : 03:00 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite la recontruccion del recaudos por concepto
                          Ruta: FACTURACION\PROCESOS\MANTENIMIENTO\RECONSTRUCCIÓN DE RECAUDOS POR CONCEPTO
      PARAMETERS        : UN_COMPANIA     => Compañia de ingreso a la aplicación.  
                          UN_FECHA        => Fecha de ejecucion del proceso.
                          UN_USUARIO      => Usuario que realiza el proceso.


      @NAME:    reconstruccionDeRecaudosPorConcepto
      @METHOD:  GET
   */ 


(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHA             IN DATE,
  UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
)

AS 
BEGIN
  PCK_SERVICIOS_PUBLICOS.PR_RECONSTRUIR_12(UN_COMPANIA     => UN_COMPANIA,
                                           UN_FECHAINICIAL => UN_FECHA,
                                           UN_FECHAFINAL   => UN_FECHA,
                                           UN_USUARIO      => UN_USUARIO);

  PCK_SERVICIOS_PUBLICOS.PR_CHARLESPESO(UN_COMPANIA     => UN_COMPANIA,
                                        UN_FECHAINICIAL => UN_FECHA,
                                        UN_FECHAFINAL   => UN_FECHA,
                                        UN_USUARIO      => UN_USUARIO);


  PCK_SERVICIOS_PUBLICOS.PR_CHARLESPRESORECAUDOS(UN_COMPANIA     => UN_COMPANIA,
                                                 UN_FECHAINICIAL => UN_FECHA,
                                                 UN_FECHAFINAL   => UN_FECHA,
                                                 UN_USUARIO      => UN_USUARIO);

  PCK_SERVICIOS_PUBLICOS.PR_RECONSTRUIRD_RECAUDO(UN_COMPANIA     => UN_COMPANIA,
                                                 UN_FECHAINICIAL => UN_FECHA,
                                                 UN_FECHAFINAL   => UN_FECHA,
                                                 UN_USUARIO      => UN_USUARIO);

  PCK_SERVICIOS_PUBLICOS.PR_RECRECAUDOS(UN_COMPANIA     => UN_COMPANIA,
                                        UN_FECHAINICIAL => UN_FECHA,
                                        UN_FECHAFINAL   => UN_FECHA,
                                        UN_USUARIO      => UN_USUARIO);    

  PCK_SERVICIOS_PUBLICOS.PR_INTERFAZABONOS(UN_COMPANIA     => UN_COMPANIA,
                                           UN_FECHAINICIAL => UN_FECHA,
                                           UN_FECHAFINAL   => UN_FECHA,
                                           UN_USUARIO      => UN_USUARIO);        

  PCK_SERVICIOS_PUBLICOS.PR_AJUSTEPESORECAUDO(UN_COMPANIA     => UN_COMPANIA,
                                              UN_FECHAINICIAL => UN_FECHA,
                                              UN_FECHAFINAL   => UN_FECHA,
                                              UN_USUARIO      => UN_USUARIO);


END PR_RECONSTRUCCIONRECAUCONCEPTO;

--7
PROCEDURE PR_ACTUALIZACONSECUTIVOS
/*
   NAME              : FC_ACTUALIZACONSECUTIVOS 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 30/05/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador 
                       ImpresionfacturasControlador metodo generarfactura,
                       Funcion que permite actualizar los consecutivos de las facturas generadas en el formulario.
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO AL QUE PERTENECE EL USUARIO
                       UN_CODIGOINICIAL   => CODIGO DE RUTA DEL USUARIO INICIAL    
                       UN_CODIGOFINAL     => CODIGO DE RUTA DEL USUARIO FINAL
                       UN_FECHALIMITE1    => FECHA LIMITE DE PAGO 1
                       UN_FECHALIMITE2    => FECHA LIMITE DE PAGO 2
                       UN_USUARIO         => USUARIO QUE ACCEDE AL SISTEMA .
   MODIFICATIONS     :

   @NAME:    actualizaConsecutivos
   @METHOD:  POST
 */
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO
   ,UN_MARCA         IN VARCHAR2
   ,UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_FECHALIMITE1  IN DATE
   ,UN_FECHALIMITE2  IN DATE
   ,UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
	MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
	MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
	MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
	MI_CONSECUTIVO     SP_NUMEROSDEFACTURA.CONSECUTIVO%TYPE; 
	MI_CONSECUTIVOREAL SP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
	MI_DEUDAMAYORA     PARAMETRO.VALOR%TYPE;
	MI_MANEJATERCERO   PARAMETRO.VALOR%TYPE;
	MI_FACTSUSP        PARAMETRO.VALOR%TYPE;
	MI_FACTCORT        PARAMETRO.VALOR%TYPE;
	MI_FACT            PCK_SUBTIPOS.TI_ENTERO_LARGO;
	MI_FACTAUX         VARCHAR2(30 CHAR);
	MI_FECHAUSUARIO    PARAMETRO.VALOR%TYPE;
	MI_SELECT          PCK_SUBTIPOS.TI_STRSQL;
	MI_FROM            PCK_SUBTIPOS.TI_CAMPOS;
	MI_WHERE           PCK_SUBTIPOS.TI_CONDICION;
	MI_COPIAS          PARAMETRO.VALOR%TYPE;
	MI_RTA             PCK_SUBTIPOS.TI_ENTERO_LARGO;
	MI_SECUENCIA       SP_NUMEROSDEFACTURA.SECUENCIA%TYPE;    
	MI_EXISTE          PCK_SUBTIPOS.TI_ENTERO_LARGO;
	MI_STRSQL          PCK_SUBTIPOS.TI_STRSQL;
  MI_RS              SYS_REFCURSOR;
  MI_CODIGORUTA      PCK_SUBTIPOS.TI_CODIGORUTA;   
  MI_ANO             PCK_SUBTIPOS.TI_ANIO;
  MI_PERIODO         PCK_SUBTIPOS.TI_PERIODO;
  MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_MANEJATERCERO:= NVL(PCK_SYSMAN_UTL.FC_PAR(
                                          UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO'
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                         ,UN_FECHA_PAR => SYSDATE)
                           ,'NO');
    MI_DEUDAMAYORA:= NVL(PCK_SYSMAN_UTL.FC_PAR(
    	                                  UN_COMPANIA  => UN_COMPANIA
                                       ,UN_NOMBRE    => 'PERMITE EMITIR FACTURAS EN CEROS'
                                       ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                       ,UN_FECHA_PAR => SYSDATE)
                           ,'NO');
    MI_FACTSUSP:= NVL(PCK_SYSMAN_UTL.FC_PAR(
                                     UN_COMPANIA  => UN_COMPANIA
                                    ,UN_NOMBRE    => 'IMPRIMIR FACTURA USUARIO SUSPENDIDO'
                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                    ,UN_FECHA_PAR => SYSDATE)
                     ,'NO');
    MI_FACTCORT:= NVL(PCK_SYSMAN_UTL.FC_PAR(
                                     UN_COMPANIA  => UN_COMPANIA
                                    ,UN_NOMBRE    => 'IMPRIMIR FACTURA USUARIO CORTADO'
                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                    ,UN_FECHA_PAR => SYSDATE)
                     ,'NO');   
    MI_FECHAUSUARIO:= NVL(PCK_SYSMAN_UTL.FC_PAR(
                                         UN_COMPANIA  => UN_COMPANIA
                                        ,UN_NOMBRE    => 'PERMITE DISTINTAS FECHAS LIMITE PARA UN CICLO'
                                        ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                        ,UN_FECHA_PAR => SYSDATE)
                         ,'NO');    
    MI_COPIAS:= NVL(PCK_SYSMAN_UTL.FC_PAR(
                                   UN_COMPANIA  => UN_COMPANIA
                                  ,UN_NOMBRE    => 'MANEJA HISTORIA DE COPIAS'
                                  ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                  ,UN_FECHA_PAR => SYSDATE)
                   ,'NO');

    IF UN_MARCA = '1' THEN 
        MI_SELECT:= 'SELECT   SP_USUARIO.CODIGORUTA
                            , SP_USUARIO.ANO
                            , SP_USUARIO.PERIODO';
        MI_FROM:= CASE WHEN 'SI' = MI_MANEJATERCERO 
                       THEN ' FROM SP_USUARIO 
                                  LEFT JOIN SP_HISTORIA_EXTERNA 
                                      ON  SP_USUARIO.COMPANIA   = SP_HISTORIA_EXTERNA.COMPANIA
                                      AND SP_USUARIO.CICLO      = SP_HISTORIA_EXTERNA.CICLO 
                                      AND SP_USUARIO.CODIGORUTA = SP_HISTORIA_EXTERNA.CODIGORUTA
                                      AND SP_USUARIO.ANO        = SP_HISTORIA_EXTERNA.ANO
                                      AND SP_USUARIO.PERIODO    = SP_HISTORIA_EXTERNA.PERIODO '
                       ELSE ' FROM SP_USUARIO '
                  END;
        MI_WHERE:=' WHERE SP_USUARIO.COMPANIA        = '''||UN_COMPANIA||'''
                      AND SP_USUARIO.CICLO           = '||UN_CICLO||'
                      AND SP_USUARIO.CODIGORUTA      BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                      AND SP_USUARIO.BANCOPERPROCESO IS NULL ';


        IF MI_DEUDAMAYORA='NO' AND MI_MANEJATERCERO='SI' THEN 
            MI_WHERE:=MI_WHERE||' AND (TOTFACTURAPERACTUAL>0 OR NVL(SP_HISTORIA_EXTERNA.VALORASEO,0)<>0) ';
        ELSIF MI_DEUDAMAYORA='NO' THEN 
            MI_WHERE:=MI_WHERE||' AND TOTFACTURAPERACTUAL>0 ';
        ELSIF MI_DEUDAMAYORA = 'SI' AND MI_MANEJATERCERO = 'SI' THEN 
            MI_WHERE:=MI_WHERE||' AND (TOTFACTURAPERACTUAL>=0 OR NVL(SP_HISTORIA_EXTERNA.VALORASEO,0)<>0) ';
        ELSE 
            MI_WHERE:=MI_WHERE||' AND TOTFACTURAPERACTUAL>=0 ';
        END IF;

        IF 'SI'= MI_FACTSUSP THEN 
            IF 'SI'= MI_FACTCORT THEN 
                MI_WHERE:=MI_WHERE||' AND SP_USUARIO.ESTADO<>''R'' ';
            ELSE 
                MI_WHERE:=MI_WHERE||' AND SP_USUARIO.ESTADO NOT IN (''C'',''R'') ';
            END IF;
        ELSE 
            IF 'SI'=MI_FACTCORT THEN 
                MI_WHERE:=MI_WHERE||' AND SP_USUARIO.ESTADO NOT IN (''S'',''R'') ';
            ELSE 
              MI_WHERE:=MI_WHERE||' AND SP_USUARIO.ESTADO NOT IN (''C'',''R'',''S'') ';
            END IF;
        END IF;
        MI_STRSQL:= MI_SELECT||MI_FROM||MI_WHERE;

        BEGIN
          EXECUTE IMMEDIATE 'SELECT COUNT(1) EXISTE FROM ('||MI_STRSQL||')' INTO MI_EXISTE; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:=0; 
        END;

        IF MI_FACTSUSP = 'NO' 
           AND MI_FACTCORT = 'NO' 
           AND MI_EXISTE = 0 THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'SP_USUARIO' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFAC
                          );
            END;
        END IF;
        IF MI_EXISTE = 0 THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'SP_USUARIO' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA1
                          );
            END;
        END IF;
        BEGIN 
          SELECT  SECUENCIA
                 ,CONSECUTIVO
                 ,CONSECUTIVOREAL
            INTO  MI_SECUENCIA
                 ,MI_CONSECUTIVO
                 ,MI_CONSECUTIVOREAL 
            FROM SP_NUMEROSDEFACTURA 
           WHERE UN_COMPANIA = UN_COMPANIA
               AND ROWNUM=1
             ORDER BY SECUENCIA DESC;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_SECUENCIA:=NULL;
            MI_CONSECUTIVO:=NULL;
            MI_CONSECUTIVOREAL :=NULL;
        END;
        IF MI_CONSECUTIVO IS NULL THEN
            MI_FACT:= 1;
            MI_CONSECUTIVO:= 1; 
        ELSE
            MI_FACT:= MI_CONSECUTIVOREAL;
            MI_CONSECUTIVO:= MI_SECUENCIA; 
        END IF;

        OPEN MI_RS FOR MI_STRSQL;
        LOOP    
            FETCH MI_RS INTO  MI_CODIGORUTA
                             ,MI_ANO
                             ,MI_PERIODO;
            EXIT WHEN MI_RS%NOTFOUND;
            IF 'SI'=MI_MANEJATERCERO THEN 
                MI_CAMPOS:= ' FACTURA         = '||MI_FACT||'
                             ,FECHALIMITE     = TO_DATE('''||UN_FECHALIMITE1||''',''DD/MM/YYYY'') 
                             ,FECHAEXPFACTURA = SYSDATE'
                             ||CASE WHEN 'SI'=MI_FECHAUSUARIO
                                  THEN ',FECHALIMITE2 = TO_DATE('''||UN_FECHALIMITE2||''',''DD/MM/YYYY'') '
                                  ELSE '' 
                             END||'
                             ,INDRECAUDADO  = INDRECAUDADO+1
                             ,MODIFIED_BY   = '''||UN_USUARIO||'''
                             ,DATE_MODIFIED = SYSDATE';
                MI_CONDICION:= '    COMPANIA   = '''||UN_COMPANIA||'''
                                AND CICLO      = '''||UN_CICLO||''' 
                                AND CODIGORUTA = '''||MI_CODIGORUTA||''' 
                                AND ANO        =   '||MI_ANO||' 
                                AND PERIODO    = '''||MI_PERIODO||''' ';
                BEGIN
                    BEGIN
                        MI_TABLA:='SP_USUARIO';
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_CICLO';
                        MI_REEMPLAZOS(0).VALOR:=UN_CICLO;
                        MI_REEMPLAZOS(1).CLAVE:='MI_CODIGORUTA';
                        MI_REEMPLAZOS(1).VALOR:=MI_CODIGORUTA;
                        MI_REEMPLAZOS(2).CLAVE:='MI_ANO';
                        MI_REEMPLAZOS(2).VALOR:=MI_ANO;
                        MI_REEMPLAZOS(3).CLAVE:='MI_PERIODO';
                        MI_REEMPLAZOS(3).VALOR:=MI_PERIODO;
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA2 
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
                END;
                MI_CAMPOS:= 'FACTURA       = '||MI_FACT||'
                            ,MODIFIED_BY   = '''||UN_USUARIO||'''
                            ,DATE_MODIFIED = SYSDATE'      ;
                MI_CONDICION:= '    COMPANIA   = '''||UN_COMPANIA||''' 
                                AND CICLO      = '||UN_CICLO||'
                                AND CODIGORUTA = '''||MI_CODIGORUTA||''' 
                                AND ANO        = '||MI_ANO||'
                                AND PERIODO    = '''||MI_PERIODO||''' ';
                BEGIN
                    BEGIN
                        MI_TABLA:='SP_HISTORIA_EXTERNA';
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_CICLO';
                        MI_REEMPLAZOS(0).VALOR:=UN_CICLO;
                        MI_REEMPLAZOS(1).CLAVE:='MI_CODIGORUTA';
                        MI_REEMPLAZOS(1).VALOR:=MI_CODIGORUTA;
                        MI_REEMPLAZOS(2).CLAVE:='MI_ANO';
                        MI_REEMPLAZOS(2).VALOR:=MI_ANO;
                        MI_REEMPLAZOS(3).CLAVE:='MI_PERIODO';
                        MI_REEMPLAZOS(3).VALOR:=MI_PERIODO;
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA3
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
                END;
            ELSE 
                MI_CAMPOS:= ' FACTURA = '||MI_FACT||
                              CASE WHEN 'SI' = MI_FECHAUSUARIO
                                  THEN ',FECHALIMITE2 = TO_DATE('''||UN_FECHALIMITE2||''',''DD/MM/YYYY'') '
                                  ELSE ''
                               END||'
                             ,MODIFIED_BY   = '''||UN_USUARIO||'''
                             ,DATE_MODIFIED = SYSDATE';
                MI_CONDICION:= '    COMPANIA   = '''||UN_COMPANIA||'''
                                AND CICLO      = '||UN_CICLO||'
                                AND CODIGORUTA = '''||MI_CODIGORUTA||''' 
                                AND ANO        = '||MI_ANO||'
                                AND PERIODO    = '''||MI_PERIODO||''' ';
                BEGIN
                    BEGIN
                        MI_TABLA:='SP_USUARIO';
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_CICLO';
                        MI_REEMPLAZOS(0).VALOR:=UN_CICLO;
                        MI_REEMPLAZOS(1).CLAVE:='MI_CODIGORUTA';
                        MI_REEMPLAZOS(1).VALOR:=MI_CODIGORUTA;
                        MI_REEMPLAZOS(2).CLAVE:='MI_ANO';
                        MI_REEMPLAZOS(2).VALOR:=MI_ANO;
                        MI_REEMPLAZOS(3).CLAVE:='MI_PERIODO';
                        MI_REEMPLAZOS(3).VALOR:=MI_PERIODO;
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA2 
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
                END;
                PCK_SERVICIOS_PUBLICOS.PR_CONTROLCOPIA(
                                     UN_COMPANIA   => UN_COMPANIA
                                    ,UN_CICLO      => UN_CICLO
                                    ,UN_CODIGO     => UN_CODIGOINICIAL
                                    ,UN_TIPO       => 'A'
                                    ,UN_APLICA     => MI_COPIAS
                                    ,UN_TIMPRESION => UN_MARCA
                                    ,UN_USER       => UN_USUARIO);
            END IF;
            MI_FACT:=MI_FACT+1;    
        END LOOP; 
        CLOSE MI_RS;

        MI_FACTAUX:= PCK_SYSMAN_UTL.FC_STRZERO(MI_FACT,10);
        MI_CAMPOS:= ' CONSECUTIVOREAL = '''||MI_FACTAUX||''' 
                     ,MODIFIED_BY     = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED   = SYSDATE';
        MI_CONDICION:= '    COMPANIA  = '''||UN_COMPANIA||''' 
                        AND SECUENCIA = '||MI_CONSECUTIVO;
        BEGIN
            BEGIN
                MI_TABLA:='SP_NUMEROSDEFACTURA';
                MI_RTA:=PCK_DATOS.FC_ACME(
                                UN_TABLA     => MI_TABLA
                               ,UN_ACCION    => 'M'
                               ,UN_CAMPOS    => MI_CAMPOS
                               ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='MI_FACTAUX';
                MI_REEMPLAZOS(0).VALOR:=MI_FACTAUX;
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA4
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
        END;

        MI_TABLA:='SP_FACTURAS';
        MI_CAMPOS:= 'CONSECUTIVO   = '||MI_CONSECUTIVO||'
                    ,MODIFIED_BY   = '''||UN_USUARIO||'''
                    ,DATE_MODIFIED = SYSDATE';
        MI_CONDICION:= '    COMPANIA = '''||UN_COMPANIA||''' 
                        AND CICLO    = '||UN_CICLO;
        BEGIN
            BEGIN
                MI_RTA:=PCK_DATOS.FC_ACME(
                                UN_TABLA     => MI_TABLA
                               ,UN_ACCION    => 'M'
                               ,UN_CAMPOS    => MI_CAMPOS
                               ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='MI_FACTAUX';
                MI_REEMPLAZOS(0).VALOR:=MI_CONSECUTIVO;
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZACONSECFA4
                          );
        END;
     ELSIF UN_MARCA IN ('2','4') THEN 
        PCK_SERVICIOS_PUBLICOS.PR_CONTROLCOPIA(
                               UN_COMPANIA   => UN_COMPANIA
                              ,UN_CICLO      => UN_CICLO
                              ,UN_CODIGO     => UN_CODIGOINICIAL
                              ,UN_TIPO       => 'A'
                              ,UN_APLICA     => MI_COPIAS
                              ,UN_TIMPRESION => UN_MARCA
                              ,UN_USER       => UN_USUARIO);
    ELSIF UN_MARCA='3' THEN 
        PCK_SERVICIOS_PUBLICOS.PR_CONTROLCOPIA(
                               UN_COMPANIA   => UN_COMPANIA
                              ,UN_CICLO      => UN_CICLO
                              ,UN_CODIGO     => UN_CODIGOINICIAL
                              ,UN_TIPO       => 'A'
                              ,UN_APLICA     => MI_COPIAS
                              ,UN_TIMPRESION => UN_MARCA
                              ,UN_USER       => UN_USUARIO);
    END IF;
END PR_ACTUALIZACONSECUTIVOS;

--8  
FUNCTION FC_VALIDAR_FINANCIABLE
(
  /*
   NAME              : FC_VALIDAR_FINANCIABLE -- en Access de la pestaña financiables del formulario factura Form_BeforeUpdate 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : YESIKA PAOLA 
   DATE MIGRADOR     : 31/05/2017
   TIME              : 12:50 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador.
   PARAMETERS        : 	UN_COMPANIA   			    => Compañia por la que se ingresa en la aplicacion 
                        UN_CICLO                => Ciclo seleccionado 
                        UN_ANO        			    => año del usuario seleccionado 
                        UN_PERIODO    			    => periodo del usuario seleccionado 
                        UN_EXTRA      			    => Si el proceso se ejecuta desde un boton validar las validacion dependidiendo de la respuesta del usuario
                        UN_FIMM       			    => Estado de facturacion en planos del usuario seleccionado
                        UN_LECTURA    			    => Lectura del usuario seleccionado 
                        UN_BANCOPERPROCESO 		  => codigo del banco del usuario seleccionado 
                        UN_PERIODOSNOCOBROFAC 	=> periodos no cobrados del usuario seleccionado
                        UN_FECHA 				        => fecha de creacion del registro
                        UN_FECHA_PREPARACION    => fecha de preparacion del ciclo 
                        UN_INDCALCULADO         => esta calculado 
   MODIFICATIONS     :

   @NAME:    validarFinanciable
   @METHOD:  GET
 */

	UN_COMPANIA   			    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CICLO                IN PCK_SUBTIPOS.TI_CICLO,
	UN_ANO        			    IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO    			    IN PCK_SUBTIPOS.TI_PERIODO,
	UN_EXTRA      			    IN PCK_SUBTIPOS.TI_LOGICO,
	UN_FIMM       			    IN VARCHAR2,
	UN_LECTURA    			    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_BANCOPERPROCESO 		  IN VARCHAR2,
	UN_PERIODOSNOCOBROFAC 	IN VARCHAR2,
	UN_FECHA 				        IN DATE

)
RETURN PCK_SUBTIPOS.TI_LOGICO 
	AS 
		MI_EXISTEPERIODO      PCK_SUBTIPOS.TI_LOGICO;
		MI_ANOSIG             PCK_SUBTIPOS.TI_ANIO;
		MI_PERSIG             PCK_SUBTIPOS.TI_PERIODO;
		MI_DIFERENCIA         PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_FECHA_PREPARACION  SP_CICLO.FECHA_PREPARACION%TYPE;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CALCULADO          SP_CICLO.INDCALCULADO%TYPE;

	BEGIN 

		MI_ANOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA => UN_COMPANIA,UN_ANO => UN_ANO, UN_PERIODO => UN_PERIODO,UN_TIPO_RETORNO => '0', UN_FRECUENCIA => NULL);
		MI_PERSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA => UN_COMPANIA,UN_ANO => UN_ANO, UN_PERIODO => UN_PERIODO,UN_TIPO_RETORNO => '1', UN_FRECUENCIA => NULL);
		MI_EXISTEPERIODO := PCK_SERVICIOS_PUBLICOS_COM3.FC_EXISTEPERIODO(UN_COMPANIA => UN_COMPANIA, UN_ANO => MI_ANOSIG , UN_PERIODO =>  MI_PERSIG);

		IF UN_EXTRA IN(0) 
		THEN RETURN 0;
		ELSE 
			BEGIN 
				IF MI_EXISTEPERIODO = 0 
				THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
				END IF;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_EXISTEPERIODO,
                                      UN_TABLAERROR => 'SP_PERIODO');

			END;
			BEGIN 
        IF UN_FIMM = 'F' AND UN_LECTURA = 0 AND UN_BANCOPERPROCESO IS NULL AND UN_PERIODOSNOCOBROFAC = 0
        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
        END IF;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
								UN_ERROR_COD  => PCK_ERRORES.ERR_PLANOSFINANCIABLES,
								UN_TABLAERROR => 'SP_USUARIO');
			END;
      BEGIN
        IF UN_FIMM <> 'F' AND UN_LECTURA = 0 AND UN_BANCOPERPROCESO IS NULL AND UN_PERIODOSNOCOBROFAC = 0
        THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
        END IF;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
								UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOSFINANCIABLES,
								UN_TABLAERROR => 'SP_USUARIO');
			END;

      SELECT FECHA_PREPARACION , INDCALCULADO
      INTO MI_FECHA_PREPARACION , MI_CALCULADO
      FROM SP_CICLO 
      WHERE COMPANIA = UN_COMPANIA
        AND NUMERO = UN_CICLO;

      BEGIN 
      IF MI_FECHA_PREPARACION IS NULL 
      THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
         MI_MSGERROR(1).CLAVE := 'CICLO';
         MI_MSGERROR(1).VALOR := UN_CICLO;
              PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_FECHACICLO,
                                          UN_TABLAERROR => 'SP_CICLO',
                                          UN_REEMPLAZOS => MI_MSGERROR);  
      END;
     	SELECT  CASE WHEN UN_FECHA >= TO_DATE(TO_CHAR(MI_FECHA_PREPARACION,'DD/MM/YYYY')) 
              THEN 1 ELSE 0 END
			INTO MI_DIFERENCIA
			FROM DUAL;

			IF MI_DIFERENCIA = 0 
      THEN 
        BEGIN 
          BEGIN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
          END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
						PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
									UN_ERROR_COD  => PCK_ERRORES.ERR_FECHANOVALIDA,
									UN_TABLAERROR => 'SP_USUARIO');
        END;
    	ELSIF MI_CALCULADO IN(0) 
			THEN 
        BEGIN 
          BEGIN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
          END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
						PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
									UN_ERROR_COD  => PCK_ERRORES.ERR_CICLONOCALCULADO,
									UN_TABLAERROR => 'SP_USUARIO');
        END;
			END IF;
		RETURN -1;	
		END IF;
END FC_VALIDAR_FINANCIABLE;	

--9
  FUNCTION FC_ASIGNARCONCGRUPOUSUARIOS 
    /*
      NAME              : FC_ASIGNARCONCGRUPOUSUARIOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 26,30,31/05/2017
      TIME              : 09:17 AM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite asignar conceptos a un grupo de usuarios.
                          Ruta: Panel Principal\Facturación de Servicios Públicos\Novedades\Adicionar Concepto General.
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación.  
                          UN_CICLO          => Número del ciclo seleccionado.
                          UN_CODIGOINICIAL  => Código de ruta incial seleccionado.
                          UN_CODIGOFINAL    => Código de ruta final seleccionado.
                          UN_PERIODO        => Periodo en el que se encuentra el ciclo.
                          UN_ANIO           => Año en el que se encuentra el ciclo.
                          UN_CONCEPTO       => Concepto seleccionado que se asignará a los usuarios.
                          UN_NUEVOVALORFACT => Valor asignado para el concepto.
                          UN_COLVALINGRESO  => Número de la columna seleccionada para el valor ingreso. 
                          UN_NUEVOVALORFACT => Número de la columna seleccionada para el valor egreso.
                          UN_CONDICIONUSO   => Condición que se recibe si el uso seleccionado es diferente de "TODOS".
                          UN_CONDICIONEST   => Condición que se recibe si el estrato seleccionado es diferente de "TODOS".
                          UN_USUARIO        => Código del usuario que realiza la modificación. 


      @NAME:  asignarConceptosGrupoUsuarios
      @METHOD:  GET
      */  
    (
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO,
      UN_CODIGOINICIAL      IN PCK_SUBTIPOS.TI_CODIGORUTA, 
      UN_CODIGOFINAL        IN PCK_SUBTIPOS.TI_CODIGORUTA,
      UN_PERIODO           	IN PCK_SUBTIPOS.TI_PERIODO,
      UN_ANIO					      IN PCK_SUBTIPOS.TI_ANIO, 
      UN_CONCEPTO           IN VARCHAR2,
      UN_NUEVOVALORFACT     IN PCK_SUBTIPOS.TI_DOBLE, 
      UN_CONDICIONUSO       IN VARCHAR2,
      UN_CONDICIONEST       IN VARCHAR2,
      UN_USUARIO            IN VARCHAR2
    )RETURN PCK_SUBTIPOS.TI_ENTERO
  AS 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;  
    MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_RPTA           PCK_SUBTIPOS.TI_ENTERO;
    MI_RPTA_CALCULO   PCK_SUBTIPOS.TI_CAMPOS;

    MI_MSGERROREXC    PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    BEGIN
      BEGIN     
        MI_MERGEUSING := 'SELECT COMPANIA,  
                                 CICLO,  
                                 CODIGORUTA,  
                                 ANO,    
                                 PERIODO,
                                 ESTADO      
                            FROM SP_USUARIO 
                           WHERE COMPANIA = ''' || UN_COMPANIA || 
                       '''   AND CICLO    = '   || UN_CICLO ||  
                         '   AND CODIGORUTA BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || 
                       '''   AND ANO      = '   || UN_ANIO  || 
                         '   AND PERIODO  = ''' || UN_PERIODO || 
                       '''   AND ESTADO   = ''A''    ' ||
                             UN_CONDICIONEST || UN_CONDICIONUSO ;
        MI_MERGEENLACE := '     TABLA.COMPANIA   = VISTA.COMPANIA   
                            AND TABLA.CICLO      = VISTA.CICLO  
                            AND TABLA.CODIGORUTA = VISTA.CODIGORUTA   
                            AND TABLA.ANO        = VISTA.ANO      
                            AND TABLA.PERIODO    = VISTA.PERIODO   
                            AND TABLA.CONCEPTO = ''' || UN_CONCEPTO || '''';
        MI_MERGEEXISTE := 'UPDATE SET TABLA.VALOR_FACTURADO = ''' || UN_NUEVOVALORFACT || ''',
                                      TABLA.MODIFIED_BY     = ''' || UN_USUARIO || ''',
                                      TABLA.DATE_MODIFIED   = SYSDATE';
        MI_MERGENOEXIS := 'INSERT (COMPANIA,   
                                   CICLO,   
                                   CODIGORUTA,  
                                   ANO,    
                                   PERIODO,   
                                   CONCEPTO,  
                                   VALOR_FACTURADO, 
                                   CREATED_BY, 
                                   DATE_CREATED)  
                           VALUES (VISTA.COMPANIA,   
                                   VISTA.CICLO,   
                                   VISTA.CODIGORUTA,  
                                   VISTA.ANO,    
                                   VISTA.PERIODO, ''' ||    
                                   UN_CONCEPTO || ''', ''' || 
                                   UN_NUEVOVALORFACT || ''', ''' || 
                                   UN_USUARIO || ''', 
                                   SYSDATE)';
        MI_RPTA :=  PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO', 
                                       UN_ACCION      => 'IM', 
                                       UN_MERGEUSING  => MI_MERGEUSING, 
                                       UN_MERGEENLACE => MI_MERGEENLACE,
                                       UN_MERGEEXISTE => MI_MERGEEXISTE,
                                       UN_MERGENOEXIS => MI_MERGENOEXIS);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;

      MI_RPTA_CALCULO:=PCK_SERVICIOS_PUBLICOS_COM7.FC_CALCULOFACTURACION(UN_COMPANIA         =>UN_COMPANIA,
                                                                         UN_INTCICLO         =>UN_CICLO,
                                                                         UN_STRCODIGOINICIAL =>UN_CODIGOINICIAL,
                                                                         UN_STRCODIGOFINAL   =>UN_CODIGOFINAL,
                                                                         UN_ENSERIE          =>0,
                                                                         UN_FINAL            =>0,
                                                                         UN_USUARIO          =>UN_USUARIO);

    RETURN MI_RPTA;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN   
         MI_MSGERROREXC(1).CLAVE := 'ANIO';  
         MI_MSGERROREXC(1).VALOR := UN_ANIO;  
         MI_MSGERROREXC(2).CLAVE := 'PERIODO';  
         MI_MSGERROREXC(2).VALOR := UN_PERIODO;  
         PCK_ERR_MSG.RAISE_WITH_MSG(  
           UN_EXC_COD    => SQLCODE,  
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ASIGNARCONCGRUPO,  
           UN_REEMPLAZOS => MI_MSGERROREXC,  
           UN_TABLAERROR => 'SP_FACTURADO'  
         );                      
      END; 

  END FC_ASIGNARCONCGRUPOUSUARIOS;
  --10
  FUNCTION FC_VALIDACODIGORUTA 
  /*
   NAME              : FC_ACTUALIZACONSECUTIVOS 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 02/06/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador 
                       ImpresionfacturasControlador metodo generarfactura,
                       Funcion que permite actualizar los consecutivos de las facturas generadas en el formulario.
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO AL QUE PERTENECE EL USUARIO
                       UN_CODIGOINICIAL   => CODIGO DE RUTA DEL USUARIO INICIAL    
                       UN_CODIGOFINAL     => CODIGO DE RUTA DEL USUARIO FINAL
                       UN_FECHALIMITE1    => FECHA LIMITE DE PAGO 1
                       UN_FECHALIMITE2    => FECHA LIMITE DE PAGO 2
                       UN_USUARIO         => USUARIO QUE ACCEDE AL SISTEMA .
   MODIFICATIONS     :

   @NAME:    validarCodigoRuta
   @METHOD:  GET
 */
  (
      UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CICLO      IN PCK_SUBTIPOS.TI_CICLO,
      UN_CODIGORUTA IN PCK_SUBTIPOS.TI_CODIGORUTA
  )RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS
      MI_EXISTE PCK_SUBTIPOS.TI_CODIGORUTA;
      MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
      BEGIN 
          BEGIN 
              SELECT CODIGORUTA 
                INTO MI_EXISTE 
                FROM SP_USUARIO 
               WHERE COMPANIA   = UN_COMPANIA
                 AND CICLO      = UN_CICLO 
                 AND CODIGORUTA = UN_CODIGORUTA;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
              MI_REEMPLAZOS(0).CLAVE:= 'CODIGO';
              MI_REEMPLAZOS(0).VALOR:= UN_CODIGORUTA;
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;    
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN  
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_TABLAERROR => 'SP_USUARIO' 
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_VALIDA_CODIGO_RUTA
                     ,UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
      END;
      RETURN -1;
  END FC_VALIDACODIGORUTA;

--11
FUNCTION FC_CREARPARAMETROFACTURA
  /*
   NAME              : FC_ACTUALIZACONSECUTIVOS 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 02/06/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador 
                       ImpresionfacturasControlador metodo generarfactura,
                       Funcion que permite actualizar los consecutivos de las facturas generadas en el formulario.
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO AL QUE PERTENECE EL USUARIO
                       UN_ANIO            => ANIO DEL REGISTRO DE FACTURA
                       UN_PERIODO         => PERIODO DEL REGISTRO DE FACTURA
                       UN_MARCA           => PARAMETRO QUE DEFINE DESDE QUE OPCION DE MENU SE ABRIO EL FORMULARIO
                       UN_USUARIO         => USUARIO QUE ACCEDE AL SISTEMA .
   MODIFICATIONS     :

   @NAME:    crearParametroFacturacion
   @METHOD:  GET
 */
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO,
    UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO       IN PCK_SUBTIPOS.TI_PERIODO,
    UN_MARCA         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 
AS
   MI_EXISTE              PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_CONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_NORECIBOINICIAL     SP_PARAMETROFACTURACION.NORECIBOINICIAL%TYPE;
   MI_NUMERORECIBOINICIAL PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_RTA                 PCK_SUBTIPOS.TI_ENTERO_LARGO; 
   MI_FACTURAINICIAL      SP_PARAMETROFACTURACION.FACTURAINICIAL%TYPE;
   MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
   MI_REEMPLAZOS          PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_CONTROLAFECHLIMITE  PARAMETRO.VALOR%TYPE;
   MI_FRECUENCIA          PARAMETRO.VALOR%TYPE;  
   MI_TIPOFACTURA         PARAMETRO.VALOR%TYPE;  
   MI_APLICA720           PARAMETRO.VALOR%TYPE;  
   MI_INICIO720ASEO       PARAMETRO.VALOR%TYPE;  
   MI_NOMBREFACTURAACTUAL PARAMETRO.VALOR%TYPE;  
   MI_NOMBREFACTURAANTERI PARAMETRO.VALOR%TYPE; 
   MI_NOMBREFACTURA       PARAMETRO.VALOR%TYPE; 
   MI_FECHALIMITE1        DATE;
   MI_FECHALIMITE2        DATE;
   MI_FECHALIMITE3        DATE;
   MI_PERIODO             PCK_SUBTIPOS.TI_PERIODO;
   MI_ANO                 PCK_SUBTIPOS.TI_ANIO;
   MI_CODIGOINICIAL       PCK_SUBTIPOS.TI_CODIGORUTA;
   MI_CODIGOFINAL         PCK_SUBTIPOS.TI_CODIGORUTA;     
   MI_PERIODOLARGO        SP_PARAMETROFACTURACION.PERIODOLARGO%TYPE;
   MI_PERIODOCORTO        SP_PARAMETROFACTURACION.PERIODOCORTO%TYPE;
   MI_PERIODOANT1         SP_PARAMETROFACTURACION.PERIODOANT1%TYPE;
   MI_PERIODOANT2         SP_PARAMETROFACTURACION.PERIODOANT2%TYPE;
   MI_PERIODOANT3         SP_PARAMETROFACTURACION.PERIODOANT3%TYPE;
   MI_PERIODOANT4         SP_PARAMETROFACTURACION.PERIODOANT4%TYPE;
   MI_PERIODOANT5         SP_PARAMETROFACTURACION.PERIODOANT5%TYPE;
   MI_PERIODOANT6         SP_PARAMETROFACTURACION.PERIODOANT6%TYPE;
   MI_FECHACORTEC1        SP_PARAMETROFACTURACION.FECHACORTEC1%TYPE;
   MI_FECHACORTEC2        SP_PARAMETROFACTURACION.FECHACORTEC2%TYPE;
   MI_FECHACORTEC3        SP_PARAMETROFACTURACION.FECHACORTEC3%TYPE;
   MI_FECHACORTEL1        SP_PARAMETROFACTURACION.FECHACORTEL1%TYPE;
   MI_FECHACORTEL2        SP_PARAMETROFACTURACION.FECHACORTEL2%TYPE;
   MI_FECHACORTEL3        SP_PARAMETROFACTURACION.FECHACORTEL3%TYPE;
BEGIN
    /*VALIDA SI EXISTE EL REGISTRO, SE DEBE ASEGURAR QUE EL FORMULARIO LO GUARDE */
    BEGIN 
        BEGIN  
            SELECT  SP_PARAMETROFACTURACION.FECHALIMITE1
                   ,SP_PARAMETROFACTURACION.FECHALIMITE2
                   ,SP_PARAMETROFACTURACION.CODIGOINICIAL
                   ,SP_PARAMETROFACTURACION.CODIGOFINAL
              INTO  MI_FECHALIMITE1
                   ,MI_FECHALIMITE2
                   ,MI_CODIGOINICIAL
                   ,MI_CODIGOFINAL
              FROM SP_PARAMETROFACTURACION
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = UN_ANIO
               AND CICLO    = UN_CICLO
               AND PERIODO  = UN_PERIODO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;        
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'SP_PARAMETROFACTURACION' 
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURA1
                   );
    END;

    /*CARGA DE PARAMETROS DEL PROCESO*/
    MI_NOMBREFACTURAACTUAL:= PCK_SYSMAN_UTL.FC_PAR(
                                            UN_COMPANIA  => UN_COMPANIA
                                           ,UN_NOMBRE    => 'NOMBRE REPORTE FACTURA'
                                           ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                           ,UN_FECHA_PAR => SYSDATE
                                           ,UN_IND_MAYUS => 0);
    MI_NOMBREFACTURAANTERI:= PCK_SYSMAN_UTL.FC_PAR(
                                            UN_COMPANIA  => UN_COMPANIA
                                           ,UN_NOMBRE    => 'NOMBRE REPORTE FACTURA ANTERIOR'
                                           ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                           ,UN_FECHA_PAR => SYSDATE
                                           ,UN_IND_MAYUS => 0);
    -- '(MZANGUNA 11/05/2016) Parametro para imprimir facturas antes y despues de Res 720
    IF MI_APLICA720 = 'SI' AND UN_ANIO||','||UN_PERIODO >= MI_INICIO720ASEO THEN
        MI_NOMBREFACTURA:= MI_NOMBREFACTURAACTUAL; 
    ELSE 
        MI_NOMBREFACTURA:= MI_NOMBREFACTURAANTERI;
    END IF;
    IF MI_NOMBREFACTURA IS NULL THEN  
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='PARAM';
            MI_REEMPLAZOS(0).VALOR:='NOMBRE REPORTE FACTURA'; 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'PARAMETRO' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURAS
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
        END;
    END IF;
    MI_CONTROLAFECHLIMITE:=PCK_SYSMAN_UTL.FC_PAR(
                                          UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'CONTROLAR FECHA LIMITE'
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                         ,UN_FECHA_PAR => SYSDATE);
    MI_FRECUENCIA:= PCK_SYSMAN_UTL.FC_PAR(
                                   UN_COMPANIA  => UN_COMPANIA
                                  ,UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION'
                                  ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                  ,UN_FECHA_PAR => SYSDATE);

    MI_APLICA720:= PCK_SYSMAN_UTL.FC_PAR(
                                  UN_COMPANIA  => UN_COMPANIA
                                 ,UN_NOMBRE    => 'APLICA RESOLUCION CRA 720'
                                 ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                 ,UN_FECHA_PAR => SYSDATE);
    MI_INICIO720ASEO:= PCK_SYSMAN_UTL.FC_PAR(
                                      UN_COMPANIA  => UN_COMPANIA
                                     ,UN_NOMBRE    => 'PERIODO INICIO RES 720 ASEO'
                                     ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                     ,UN_FECHA_PAR => SYSDATE);


    BEGIN 
        SELECT CONSECUTIVO
          INTO MI_CONSECUTIVO
          FROM SP_FACTURAS 
         WHERE COMPANIA = UN_COMPANIA
           AND CICLO    = UN_CICLO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CONSECUTIVO:=NULL;
    END; 
    IF MI_CONSECUTIVO IS NOT NULL THEN 
        MI_NORECIBOINICIAL:= MI_CONSECUTIVO; 
        MI_NUMERORECIBOINICIAL:= MI_CONSECUTIVO; 
    ELSE
        MI_NORECIBOINICIAL:= 0;
        MI_NUMERORECIBOINICIAL:= 0; 
        MI_FACTURAINICIAL:=0;
        BEGIN 
            MI_TABLA:='SP_FACTURAS';
            MI_CAMPOS:=' COMPANIA
                        ,CICLO
                        ,CONSECUTIVO
                        ,DATE_CREATED
                        ,CREATED_BY';
            MI_VALORES:=' '''||UN_COMPANIA||'''
                         ,'||UN_CICLO||'
                         ,0
                         ,SYSDATE
                         ,'''||UN_USUARIO||''' ';
            BEGIN 
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA   => MI_TABLA
                                 ,UN_ACCION  => 'I'
                                 ,UN_CAMPOS  => MI_CAMPOS
                                 ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => MI_TABLA 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURA2
                          );
        END;
    END IF;


    IF MI_CONTROLAFECHLIMITE = 'SI' THEN
        IF MI_FECHALIMITE1 < TRUNC(SYSDATE) THEN 
            BEGIN  
            	RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                MI_REEMPLAZOS(0).CLAVE:='NUM';
                MI_REEMPLAZOS(0).VALOR:='1'; 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'NA' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURA3
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
        END IF;
    END IF;

    IF MI_CONTROLAFECHLIMITE = 'SI' THEN
        IF MI_FECHALIMITE1 < TRUNC(SYSDATE) THEN 
            BEGIN  
            	RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                MI_REEMPLAZOS(0).CLAVE:='NUM';
                MI_REEMPLAZOS(0).VALOR:='2'; 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'NA' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURA3
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
        END IF;
    END IF;
    BEGIN 
        SELECT MIN(PERIODO),
               MIN(ANO)
          INTO MI_PERIODO,
               MI_ANO
          FROM SP_USUARIO
         WHERE COMPANIA = UN_COMPANIA
           AND CICLO    = UN_CICLO;
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_PERIODO:=NULL;
        MI_ANO:=NULL;
    END; 
    IF MI_PERIODO IS NOT NULL THEN 
    	  MI_PERIODOCORTO:= PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA, MI_ANO, MI_PERIODO, MI_FRECUENCIA);
        MI_PERIODOLARGO:= MI_PERIODOCORTO; --'NombrePeriodo(StrCia, rs!ANO, rs!PERIODO, MI_FRECUENCIA)
        MI_PERIODOANT1:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,TO_NUMBER(MI_PERIODO)-1, MI_ANO); 
        MI_PERIODOANT2:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,MI_PERIODO-2, MI_ANO);
        MI_PERIODOANT3:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,MI_PERIODO-3, MI_ANO);
        MI_PERIODOANT4:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,MI_PERIODO-4, MI_ANO);
        MI_PERIODOANT5:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,MI_PERIODO-5, MI_ANO);
        MI_PERIODOANT6:= PCK_SERVICIOS_PUBLICOS.FC_ESCRIBIRPERIODO(UN_COMPANIA,MI_PERIODO-6, MI_ANO);
        MI_FECHACORTEC1:= EXTRACT(DAY FROM MI_FECHALIMITE1)||'/'||SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT(MONTH FROM MI_FECHALIMITE1)), 1, 3)||'/'||EXTRACT(YEAR FROM MI_FECHALIMITE1);
        MI_FECHACORTEC2:= EXTRACT(DAY FROM MI_FECHALIMITE2)||'/'||SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT(MONTH FROM MI_FECHALIMITE2)), 1, 3)||'/'||EXTRACT(YEAR FROM MI_FECHALIMITE2);
        MI_FECHACORTEC3:= EXTRACT(DAY FROM NVL(MI_FECHALIMITE3, SYSDATE))||'/'||SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT (MONTH FROM NVL(MI_FECHALIMITE3, SYSDATE))), 1, 3)||'/'||EXTRACT(YEAR FROM NVL(MI_FECHALIMITE3, SYSDATE));
        MI_FECHACORTEL1:= EXTRACT(DAY FROM MI_FECHALIMITE1)||' de '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT (MONTH FROM MI_FECHALIMITE1))||' de '||EXTRACT(YEAR FROM MI_FECHALIMITE1);
        MI_FECHACORTEL2:= EXTRACT(DAY FROM MI_FECHALIMITE2)||' de '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT (MONTH FROM MI_FECHALIMITE2))||' de '||EXTRACT(YEAR FROM MI_FECHALIMITE2);
        MI_FECHACORTEL3:= EXTRACT(DAY FROM NVL(MI_FECHALIMITE3, SYSDATE))||' de '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT (MONTH FROM NVL(MI_FECHALIMITE3, SYSDATE)))||' de '||EXTRACT(YEAR FROM NVL(MI_FECHALIMITE3, SYSDATE));

        MI_TABLA:='SP_PARAMETROFACTURACION';
        MI_CAMPOS:='PERIODOCORTO  = '''||MI_PERIODOCORTO||'''
                   ,PERIODOLARGO  = '''||MI_PERIODOLARGO||'''
                   ,PERIODOANT1   = '''||MI_PERIODOANT1||'''
                   ,PERIODOANT2   = '''||MI_PERIODOANT2||'''
                   ,PERIODOANT3   = '''||MI_PERIODOANT3||'''
                   ,PERIODOANT4   = '''||MI_PERIODOANT4||'''
                   ,PERIODOANT5   = '''||MI_PERIODOANT5||'''
                   ,PERIODOANT6   = '''||MI_PERIODOANT6||'''
                   ,FECHACORTEC1  = '''||MI_FECHACORTEC1||'''
                   ,FECHACORTEC2  = '''||MI_FECHACORTEC2||'''
                   ,FECHACORTEC3  = '''||MI_FECHACORTEC3||'''
                   ,FECHACORTEL1  = '''||MI_FECHACORTEL1||'''
                   ,FECHACORTEL2  = '''||MI_FECHACORTEL2||'''
                   ,FECHACORTEL3  = '''||MI_FECHACORTEL3||'''
                   ,MODIFIED_BY   = '''||UN_USUARIO||'''
                   ,DATE_MODIFIED = SYSDATE ';
        MI_CONDICION:=    '     COMPANIA = '''||UN_COMPANIA||''' 
                            AND CICLO    = '||UN_CICLO||' 
                            AND ANO      = '||UN_ANIO||' 
                            AND PERIODO  = '''||UN_PERIODO||''' ';
        BEGIN 
            BEGIN 
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_ANIO';
                MI_REEMPLAZOS(0).VALOR:=UN_ANIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_CICLO';
                MI_REEMPLAZOS(1).VALOR:=UN_CICLO;
                MI_REEMPLAZOS(2).CLAVE:='UN_PERIODO';
                MI_REEMPLAZOS(2).VALOR:=UN_PERIODO;
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'SP_USUARIO' 
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREAR_PAR_FACTURA4
                          );
        END;
    END IF;
    /*MI_TIPOFACTURA no aplica el parametro*/
    --IF MI_TIPOFACTURA = 'SI' THEN --' si la factura es un reporte
        PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZACONSECUTIVOS(
                                    UN_COMPANIA      => UN_COMPANIA
                                   ,UN_CICLO         => UN_CICLO
                                   ,UN_MARCA         => UN_MARCA
                                   ,UN_CODIGOINICIAL => MI_CODIGOINICIAL
                                   ,UN_CODIGOFINAL   => MI_CODIGOFINAL
                                   ,UN_FECHALIMITE1  => MI_FECHALIMITE1
                                   ,UN_FECHALIMITE2  => MI_FECHALIMITE2
                                   ,UN_USUARIO       => UN_USUARIO);
    --END IF;
  RETURN MI_NOMBREFACTURA; 
END FC_CREARPARAMETROFACTURA;	

  --12
FUNCTION FC_ELIMINARFINANCIBLE 
(
  /*
   NAME              : FC_ELIMINARFINANCIBLE ->Access->  Form_Delete Formulario Financiables
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
   DATE MIGRADOR     : 05/06/2017
   TIME              : 11:27 AM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador 
                       financiablesFacturas 
   PARAMETERS        :  UN_COMPANIA 				        => Compania por la que se ingresa a la aplicacion
                        UN_CICLO                    => Ciclo seleccionado al ingresar por consulta de facturacion
                        UN_CODIGORUTA               => Codigo de ruta seleccionado,
                        UN_ANO      				        => Ano en el que se encuentra el usuario seleccinado 
                        UN_PERIODO  				        => Periodo en el que se encutra el usuario seleccionado
                        UN_BANCOPERPROCESO          => Banco en el cual pago el usuario 
                        UN_CONCEPTO 				        => Concepto a eliminar del subformulario financiables
                        UN_USUARIO 					        => Usuario que ingreso a la aplicacion 
                        UN_CODIGOINTERNO            => Codigo interno del usuario 
  MODIFICATIONS     :

   @NAME:    eliminarFinanciable
   @METHOD:  GET
 */
	UN_COMPANIA 				        IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CICLO                    IN PCK_SUBTIPOS.TI_CICLO,
	UN_CODIGORUTA               IN PCK_SUBTIPOS.TI_CODIGORUTA,
	UN_ANO      				        IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO  				        IN PCK_SUBTIPOS.TI_PERIODO,
	UN_BANCOPERPROCESO          IN VARCHAR2,
	UN_CONCEPTO 				        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO 					        IN VARCHAR2,
	UN_CODIGOINTERNO            IN VARCHAR2
)
RETURN PCK_SUBTIPOS.TI_ENTERO

AS 
	MI_ELIMINARFINANCIABLE 	PCK_SUBTIPOS.TI_PARAMETRO;
  MI_ANOSIG               PCK_SUBTIPOS.TI_ANIO;
  MI_PERIODOSIG           PCK_SUBTIPOS.TI_PERIODO;
	MI_TIENEFACTURADO       VARCHAR2(2 CHAR);
	MI_TIENEABONOS          PCK_SUBTIPOS.TI_ENTERO;
	MI_PERMITEELIMINARFIN   PCK_SUBTIPOS.TI_PARAMETRO;
	MI_TABLA     			      PCK_SUBTIPOS.TI_TABLA;           
	MI_CAMPOS 				      PCK_SUBTIPOS.TI_CAMPOS;
	MI_CONDICION 			      PCK_SUBTIPOS.TI_CONDICION;  
	MI_RTA            		  VARCHAR2(2 CHAR);
	MI_EXISTE               PCK_SUBTIPOS.TI_ENTERO;
  MI_BLOQUEADO            PCK_SUBTIPOS.TI_LOGICO;
  MI_NROCUOTA             PCK_SUBTIPOS.TI_ENTERO;
  MI_SALDOFINANCIABLE     PCK_SUBTIPOS.TI_DOBLE;
  MI_NUMEROCUOTAS         PCK_SUBTIPOS.TI_ENTERO;
  MI_VALORCUOTA           PCK_SUBTIPOS.TI_DOBLE;
  MI_MONTOFINANCIAR       PCK_SUBTIPOS.TI_DOBLE;
  MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
BEGIN 
  SELECT SP_FINANCIABLES.BLOQUEADO , SP_FINANCIABLES.NROCUOTA,SP_FINANCIABLES.NUMEROCUOTAS, 
         SP_FINANCIABLES.SALDOFINANCIABLE,SP_FINANCIABLES.VALORCUOTA,SP_FINANCIABLES.MONTOFINANCIAR
  INTO  MI_BLOQUEADO,MI_NROCUOTA,MI_NUMEROCUOTAS,MI_SALDOFINANCIABLE,MI_VALORCUOTA,MI_MONTOFINANCIAR
  FROM SP_FINANCIABLES
  WHERE SP_FINANCIABLES.COMPANIA    = UN_COMPANIA
    AND SP_FINANCIABLES.CICLO       = UN_CICLO
    AND SP_FINANCIABLES.CODIGORUTA  = UN_CODIGORUTA
    AND SP_FINANCIABLES.ANO         = UN_ANO
    AND SP_FINANCIABLES.PERIODO     = UN_PERIODO
    AND SP_FINANCIABLES.CONCEPTO    = UN_CONCEPTO;

	MI_ELIMINARFINANCIABLE := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA , UN_NOMBRE => 'ELIMINAR FINANCIABLES BLOQUEADOS PERIODO SIGUIENTE',
													UN_MODULO => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS, UN_FECHA_PAR => SYSDATE , UN_IND_MAYUS => -1	),'NO');
	MI_ANOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE (UN_COMPANIA => UN_COMPANIA, UN_ANO => UN_ANO, UN_PERIODO => UN_PERIODO, UN_TIPO_RETORNO => '0', UN_FRECUENCIA => NULL);
  MI_PERIODOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE (UN_COMPANIA => UN_COMPANIA, UN_ANO => UN_ANO, UN_PERIODO => UN_PERIODO, UN_TIPO_RETORNO => '1', UN_FRECUENCIA => NULL);
	IF 	MI_ELIMINARFINANCIABLE  = 'SI' AND 	UN_BANCOPERPROCESO IS NOT NULL AND MI_BLOQUEADO <> 0 
	THEN 
		RETURN 0;
	ELSE 
    BEGIN
		MI_STRSQL := '  SELECT DISTINCT ''X'' 
                    FROM   SP_FACTURADO 
                    WHERE  COMPANIA 	= '''||UN_COMPANIA||'''
                      AND  CICLO 		  = '||UN_CICLO||'
                      AND  CODIGORUTA = '''||UN_CODIGORUTA||'''
                      AND  ANO 			  = '||UN_ANO||'
                      AND  PERIODO 		= '''||UN_PERIODO||'''
                      AND  CONCEPTO 	= '||UN_CONCEPTO||'';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_TIENEFACTURADO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_TIENEFACTURADO:=NULL; 
    END;
		BEGIN
			IF MI_TIENEFACTURADO IS NOT NULL AND UN_BANCOPERPROCESO IS NOT NULL 
			THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
			END IF;
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOPAGO,
                                      UN_TABLAERROR => 'SP_FACTURADO');
		END;
	END IF;
	SELECT COUNT(VALOR) VALOR
	INTO MI_TIENEABONOS
	FROM SP_ABONOS 
	WHERE COMPANIA 		= UN_COMPANIA 
		AND CICLO 		= UN_CICLO
		AND CODIGORUTA 	= UN_CODIGORUTA 
		AND ANO 		= UN_ANO
		AND PERIODO 	= UN_PERIODO
		AND VALOR NOT IN(0);

	BEGIN 
		IF MI_TIENEABONOS > 0 
		THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
		END IF;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_TIENEABONOS,
                                      UN_TABLAERROR => 'SP_ABONOS');
	END;

	IF MI_NROCUOTA > 0
	THEN 
		IF UN_CONCEPTO <> 12 
		THEN 	
			MI_PERMITEELIMINARFIN := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA , UN_NOMBRE => 'PERMITE ELIMINAR FINANCIABLES',
													UN_MODULO => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS, UN_FECHA_PAR => SYSDATE , UN_IND_MAYUS => -1	),'NO');
			IF 	MI_PERMITEELIMINARFIN = 'NO'
			THEN 
				MI_TABLA := 'SP_FINANCIABLES';
				MI_CAMPOS := 'SP_FINANCIABLES.VALORCUOTA = ' || MI_SALDOFINANCIABLE || ' ,SP_FINANCIABLES.NROCUOTA = '||MI_NUMEROCUOTAS||', SP_FINANCIABLES.DATE_MODIFIED = SYSDATE, SP_FINANCIABLES.MODIFIED_BY = '''||UN_USUARIO||''' ';
				MI_CONDICION := 'SP_FINANCIABLES.COMPANIA = '''||UN_COMPANIA||''' AND SP_FINANCIABLES.CICLO = '''||UN_CICLO||''' AND SP_FINANCIABLES.CONCEPTO = '||UN_CONCEPTO||'
								AND SP_FINANCIABLES.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_FINANCIABLES.ANO = '||UN_ANO||' AND SP_FINANCIABLES.PERIODO = '''||UN_PERIODO||'''';
				BEGIN 
					BEGIN 
						PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => MI_TABLA,
																UN_ACCION => 'M',
																UN_CAMPOS => MI_CAMPOS,
																UN_CONDICION => MI_CONDICION);
					EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
						RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
					END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUALIZAFINANCIABLE,
                                      UN_TABLAERROR => 'SP_FINANCIABLES');
				END;	

			MI_RTA := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(UN_COMPANIA 	=> UN_COMPANIA , 
															UN_USUARIO 		  => UN_USUARIO , 
															UN_MACROPROCESO => 'IncrementaTarifas',
															UN_SUBPROCESO	  => 'Creación',
															UN_ANIO 		    => MI_ANOSIG,
															UN_PERIODO 		  => MI_PERIODOSIG,
															UN_CODINTERNO 	=> 0,
															UN_DESCRIPCION 	=> 'Incrementa Tarifas desde el Período ' || UN_ANO || ' - ' || UN_PERIODO|| '');		
			--Se va a pasar todo el saldo del financiable a una sola cuota, debe volver a calcular.
			RETURN 2;
			END IF;
		END IF;	
	END IF;

	IF UN_CONCEPTO = 12
	THEN 
		BEGIN 
			IF (MI_SALDOFINANCIABLE - MI_VALORCUOTA) < 5 AND (MI_SALDOFINANCIABLE - MI_VALORCUOTA) > -5 
			THEN 
				MI_TABLA := 'SP_FACTURADO';
				MI_CAMPOS := 'SP_FACTURADO.DEUDA = SP_FACTURADO.DEUDA + SP_FACTURADO.VALORFINANT + SP_FACTURADO.VALORFINACT, SP_FACTURADO.DATE_MODIFIED = SYSDATE , SP_FACTURADO.MODIFIED_BY = '''||UN_USUARIO||'''';
				MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||'''
					AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||'''';
				BEGIN 
					BEGIN 
						PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(  UN_TABLA      => MI_TABLA,
                                                    UN_ACCION     => 'M',
                                                    UN_CAMPOS     => MI_CAMPOS,
                                                    UN_CONDICION  => MI_CONDICION);
						EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
							RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
					END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
						PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_DEUDAFACTURADO,
                                        UN_TABLAERROR => 'SP_FACTURADO');	
				END;	
			ELSIF 	(MI_SALDOFINANCIABLE - MI_VALORCUOTA) > 0 
			THEN 
				MI_TABLA := 'SP_FACTURADO';
				MI_CAMPOS := 'SP_FACTURADO.DEUDA = SP_FACTURADO.DEUDA + SP_FACTURADO.VALORFINANT + SP_FACTURADO.VALORFINACT +
          ((('||MI_SALDOFINANCIABLE||' - '||MI_VALORCUOTA||') * (SP_FACTURADO.VALORFINANT + SP_FACTURADO.VALORFINACT)) / '||MI_VALORCUOTA||')
					,SP_FACTURADO.DATE_MODIFIED = SYSDATE , SP_FACTURADO.MODIFIED_BY = '''||UN_USUARIO||'''';
				MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||'''
					AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||'''';
				BEGIN 
					BEGIN 
						PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                    UN_ACCION     => 'M',
                                                    UN_CAMPOS     => MI_CAMPOS,
                                                    UN_CONDICION  => MI_CONDICION);
						EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
							RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
					END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
						PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_DEUDAFACTURADO,
                                        UN_TABLAERROR => 'SP_FACTURADO');	
				END;	
				MI_TABLA := 'SP_USUARIO';
				MI_CAMPOS := 'SP_USUARIO.TOTFACTURAPERACTUAL = SP_USUARIO.TOTFACTURAPERACTUAL + ('||MI_SALDOFINANCIABLE||' - '|| MI_VALORCUOTA||'), SP_USUARIO.DATE_MODIFIED = SYSDATE , 
					SP_USUARIO.MODIFIED_BY = '''||UN_USUARIO||'''';
				MI_CONDICION := 'SP_USUARIO.COMPANIA = '''||UN_COMPANIA||''' AND SP_USUARIO.CICLO = '''||UN_CICLO||''' AND SP_USUARIO.CODIGORUTA = '''||UN_CODIGORUTA||'''
					AND SP_USUARIO.ANO = '||UN_ANO||' AND SP_USUARIO.PERIODO = '''||UN_PERIODO||'''';
				BEGIN 
					BEGIN 
						PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                    UN_ACCION     => 'M',
                                                    UN_CAMPOS     => MI_CAMPOS,
                                                    UN_CONDICION  => MI_CONDICION);
						EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
							RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
					END;
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
						PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_FACTURAUSUARIO,
                                        UN_TABLAERROR => 'SP_USUARIO');	
				END;	
			ELSE
				 RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;  
			END IF;	 
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_ELIFINANCIBLE,
                                      UN_TABLAERROR => 'SP_FINANCIABLES');
		END;	
		MI_TABLA := 'SP_FACTURADO';
		MI_CAMPOS := 'SP_FACTURADO.VALORFINANT = 0 , SP_FACTURADO.VALORFINACT = 0 , SP_FACTURADO.DATE_MODIFIED = SYSDATE , SP_FACTURADO.MODIFIED_BY = '''||UN_USUARIO||'''';
		MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' 
			AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||'''';
		BEGIN 
			BEGIN 
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_VALORESFACTURADO,
                                          UN_TABLAERROR => 'SP_FACTURADO');	
		END;	
		MI_CAMPOS := 'SP_FACTURADO.VALOR_FACTURADO = 0 , SP_FACTURADO.DATE_MODIFIED = SYSDATE , SP_FACTURADO.MODIFIED_BY = '''||UN_USUARIO||'''';
		MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' 
			AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||''' AND SP_FACTURADO.CONCEPTO = 12';
		BEGIN 
			BEGIN 
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_VALORFACTURADO,
                                          UN_TABLAERROR => 'SP_FACTURADO');	
		END;	
		MI_CAMPOS := 'SP_FACTURADO.VALOR_FACTURADO = SP_FACTURADO.VALOR_FACTURADO + '||MI_SALDOFINANCIABLE||',SP_FACTURADO.DATE_MODIFIED = SYSDATE , SP_FACTURADO.MODIFIED_BY = '''||UN_USUARIO||'''';
		MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' 
			AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||''' AND SP_FACTURADO.CONCEPTO = 250';
		BEGIN 
			BEGIN 
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_VALFACTURADO,
                                          UN_TABLAERROR => 'SP_FACTURADO');	
		END;	
		BEGIN
    MI_STRSQL := 'SELECT  COUNT(''X'') EXISTE
                  FROM SP_D_DEUDAFACTURADAFINANCIADA 
                  WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA    = '''||UN_COMPANIA||''' 
                    AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO       = ' || UN_CICLO || '
                    AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA  = '''||UN_CODIGORUTA||''' 
                    AND SP_D_DEUDAFACTURADAFINANCIADA.ANO         = '||UN_ANO||'
                    AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO     = '''||UN_PERIODO||''' 
                    AND (SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO BETWEEN 1 AND 48 
                      OR SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO IN (201,202,203,204,205,206,207,247,248,249))
                  GROUP BY  	SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CICLO, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA, 
                              SP_D_DEUDAFACTURADAFINANCIADA.ANO, 
                              SP_D_DEUDAFACTURADAFINANCIADA.PERIODO';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:=0; 
    END;
		IF MI_EXISTE <> 0 
		THEN 
			MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
			MI_CONDICION := 'SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA = '''||UN_COMPANIA||''' AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO = '''||UN_CICLO||'''
				AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_D_DEUDAFACTURADAFINANCIADA.ANO = '||UN_ANO||'
				AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO = '''||UN_PERIODO||'''';

			BEGIN 
				BEGIN 
					PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                  UN_ACCION     => 'E',
                                                  UN_CONDICION  => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_ELIDEUDAFACTURADA,
                                          UN_TABLAERROR => 'SP_D_DEUDAFACTURADAFINANCIADA');	
			END;
		END IF;
		MI_RTA := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(	
                                            UN_COMPANIA 	  => UN_COMPANIA , 
                                            UN_USUARIO 		  => UN_USUARIO , 
                                            UN_MACROPROCESO => 'FINANCIABLES',
                                            UN_SUBPROCESO	  => 'Eliminación',
                                            UN_ANIO 		    => UN_ANO,
                                            UN_PERIODO 		  => UN_PERIODO,
                                            UN_CODINTERNO 	=> UN_CODIGOINTERNO,
                                            UN_DESCRIPCION 	=> 'Monto: '||MI_MONTOFINANCIAR||'; Saldo: '||MI_SALDOFINANCIABLE||';Cuotas: '||MI_NUMEROCUOTAS||',
                                              Cuota: '||MI_NROCUOTA||'; Valor Cuota: '||MI_VALORCUOTA||'; Concepto: '||UN_CONCEPTO||'');	
		RETURN 4;
  END IF;   
  RETURN 1;
END FC_ELIMINARFINANCIBLE ;	
--13
FUNCTION FC_ELIMINARFACTURADO
(
    /*
   NAME              : FC_ELIMINARFACTURADO 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
   DATE MIGRADOR     : 05/06/2017
   TIME              : 12:00 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Se paso a PLSQL logica que se encontraba en el controlador 
                       financiablesFacturas 
   PARAMETERS        :  UN_COMPANIA 				        => Compania por la que se ingresa a la aplicacion
                        UN_CICLO                    => Ciclo seleccionado al ingresar por consulta de facturacion
                        UN_CODIGORUTA               => Codigo de ruta seleccionado,
                        UN_ANO      				        => Ano en el que se encuentra el usuario seleccinado 
                        UN_PERIODO  				        => Periodo en el que se encutra el usuario seleccionado
                        UN_BANCOPERPROCESO          => Banco en el cual pago el usuario 
                        UN_USUARIO 					        => Usuario que ingreso a la aplicacion 
                        UN_CODIGOINTERNO            => Codigo interno del usuario 
  MODIFICATIONS     :

   @NAME:    eliminarFacturado
   @METHOD:  DELETE
 */

	UN_COMPANIA 				        IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CICLO                    IN PCK_SUBTIPOS.TI_CICLO,
	UN_CODIGORUTA               IN PCK_SUBTIPOS.TI_CODIGORUTA,
	UN_ANO      				        IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO  				        IN PCK_SUBTIPOS.TI_PERIODO,
	UN_BANCOPERPROCESO          IN VARCHAR2,
	UN_CONCEPTO 				        IN PCK_SUBTIPOS.TI_ENTERO,
	UN_USUARIO 					        IN VARCHAR2,
	UN_CODIGOINTERNO            IN VARCHAR2
)
RETURN PCK_SUBTIPOS.TI_ENTERO
	AS 
		MI_TABLA     		        PCK_SUBTIPOS.TI_TABLA;           
		MI_CONDICION 		        PCK_SUBTIPOS.TI_CONDICION; 
		MI_RTAAUD               VARCHAR2(2 CHAR);		
		MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
    MI_NROCUOTA             PCK_SUBTIPOS.TI_ENTERO;
    MI_SALDOFINANCIABLE     PCK_SUBTIPOS.TI_DOBLE;
    MI_NUMEROCUOTAS         PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORCUOTA           PCK_SUBTIPOS.TI_DOBLE;
    MI_MONTOFINANCIAR       PCK_SUBTIPOS.TI_DOBLE;
BEGIN 
  SELECT SP_FINANCIABLES.NROCUOTA,SP_FINANCIABLES.NUMEROCUOTAS, 
         SP_FINANCIABLES.SALDOFINANCIABLE,SP_FINANCIABLES.VALORCUOTA,SP_FINANCIABLES.MONTOFINANCIAR
  INTO  MI_NROCUOTA,MI_NUMEROCUOTAS,MI_SALDOFINANCIABLE,MI_VALORCUOTA,MI_MONTOFINANCIAR
  FROM SP_FINANCIABLES
  WHERE SP_FINANCIABLES.COMPANIA    = UN_COMPANIA
    AND SP_FINANCIABLES.CICLO       = UN_CICLO
    AND SP_FINANCIABLES.CODIGORUTA  = UN_CODIGORUTA
    AND SP_FINANCIABLES.ANO         = UN_ANO
    AND SP_FINANCIABLES.PERIODO     = UN_PERIODO
    AND SP_FINANCIABLES.CONCEPTO    = UN_CONCEPTO;

	MI_RTA := PCK_SERVICIOS_PUBLICOS_COM8.FC_ELIMINARFINANCIBLE(
                                          UN_COMPANIA 				        => UN_COMPANIA,
                                          UN_CICLO                    => UN_CICLO,
                                          UN_CODIGORUTA               => UN_CODIGORUTA,
                                          UN_ANO      				        => UN_ANO,
                                          UN_PERIODO  				        => UN_PERIODO,
                                          UN_BANCOPERPROCESO          => UN_BANCOPERPROCESO,
                                          UN_CONCEPTO 				        => UN_CONCEPTO,
                                          UN_USUARIO 					        => UN_USUARIO,
                                          UN_CODIGOINTERNO            => UN_CODIGOINTERNO	);
	IF MI_RTA = 0 
	THEN RETURN MI_RTA;
	ELSIF MI_RTA = 2
	THEN RETURN MI_RTA;
	END IF;
	BEGIN 
		BEGIN 
			MI_TABLA := 'SP_FACTURADO';
			MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||'''
				AND SP_FACTURADO.ANO = '||UN_ANO||' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||''' AND SP_FACTURADO.CONCEPTO = '||UN_CONCEPTO;

			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                              UN_ACCION     => 'E',
                                              UN_CONDICION  => MI_CONDICION);	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
				RAISE PCK_EXCEPCIONES.EXC_FACTURACION;								
			END;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
				PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_ELIFINENFAC,
                                    UN_TABLAERROR => 'SP_FACTURADO');	
	END;
	MI_RTAAUD := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(	
                                        UN_COMPANIA 	  => UN_COMPANIA , 
                                        UN_USUARIO 		  => UN_USUARIO , 
                                        UN_MACROPROCESO => 'FACTURADO',
                                        UN_SUBPROCESO	  => 'Eliminación',
                                        UN_ANIO 		    => UN_ANO,
                                        UN_PERIODO 		  => UN_PERIODO,
                                        UN_CODINTERNO 	=> UN_CODIGOINTERNO,
                                        UN_DESCRIPCION 	=> 'Valor Facturado: '||MI_MONTOFINANCIAR||';Concepto: '||UN_CONCEPTO);	
	MI_RTAAUD := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(	
                                        UN_COMPANIA 	  => UN_COMPANIA , 
                                        UN_USUARIO 		  => UN_USUARIO , 
                                        UN_MACROPROCESO => 'FINANCIABLES',
                                        UN_SUBPROCESO	  => 'Eliminación',
                                        UN_ANIO 		    => UN_ANO,
                                        UN_PERIODO 		  => UN_PERIODO,
                                        UN_CODINTERNO 	=> UN_CODIGOINTERNO,
                                        UN_DESCRIPCION 	=> 'Monto: '||MI_MONTOFINANCIAR||'; Saldo: '||MI_SALDOFINANCIABLE||';Cuotas: '||MI_NUMEROCUOTAS||',
                                          Cuota: '||MI_NROCUOTA||'; Valor Cuota: '||MI_VALORCUOTA||'; Concepto: '||UN_CONCEPTO||'');			


  RETURN MI_RTA;
END FC_ELIMINARFACTURADO;	

--14
FUNCTION FC_ULTIMOPAGO
  /*
   NAME              : FC_ULTIMOPAGO 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 07/06/2017
   TIME              : 12:05 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : OBTIENE EL VALOR DEL ULTIMO PAGO REALIZADO ANTERIOR A UNA FECHA POR UN USUARIO 
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CODIGORUTA      => CICLO AL QUE PERTENECE EL USUARIO
                       UN_ANIO            => ANIO DEL REGISTRO DE FACTURA
   MODIFICATIONS     :

   @NAME:    obtenerUltimoPago
   @METHOD:  GET
 */
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CODIGORUTA IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_FECHA      IN DATE
)RETURN PCK_SUBTIPOS.TI_DOBLE
AS
    MI_ULTIMO PCK_SUBTIPOS.TI_DOBLE;
BEGIN 
    BEGIN 
        SELECT NVL(VALORPAGO,0) VALORPAGO   
          INTO MI_ULTIMO
          FROM SP_PAGO 
          WHERE COMPANIA   = UN_COMPANIA
            AND CODIGORUTA = UN_CODIGORUTA
            AND FECHA  = (SELECT MAX(FECHA) 
                            FROM SP_PAGO 
                           WHERE COMPANIA   = UN_COMPANIA
                             AND CODIGORUTA = UN_CODIGORUTA);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_ULTIMO:=0;
    END;
  RETURN MI_ULTIMO;
END;

--15
FUNCTION FC_CARGAR_CONSUMOS
/*
   NAME              : FC_CARGAR_CONSUMOS 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
   DATE MIGRADOR     : 08/06/2017
   TIME              : 09:53 AM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : PERMITE CARGAR CONSUMOS MANUAL Y PROMEDIO DE UN ARCHIVO DE EXCEL SEGUN EL PARAMETRO UN_TIPOCONSUMO
   MODIFICATIONS     :
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO AL QUE PERTENECE EL USUARIO
                       UN_DATOSEXCEL      => ES CADA REGISTRO QUE SE VA LEYENDO DEL ARCHIVO
                       UN_ANO             => ANO DEL REGISTRO DE FACTURA
                       UN_PERIODO         => PERIODO RELACIONADO AL CICLO QUE SE ESTÁ TRABAJANDO 
                       UN_USUARIO         => USUARIO QUE ACCEDE AL SISTEMA
                       UN_STRSQL          => TEXTO USADO PARA CREAR LA CONSULTA Y ACTUALIZAR LOS REGISTROS
                       UN_STRLOG          => TEXTO QUE ACUMULA LOS ERRORES GENERADOS AL CARGAR LOS REGISTROS
                       UN_NUM_USU_ERROR   => CANTIDAD DE ERRORES GENERADOS AL INSERTAR REGISTROS PARA EL LOG
                       UN_NUM_USU_OK      => CANTIDAD DE REGISTRO INSERTADOR CORRECTAMENTE PARA EL LOG
                       UN_TIPOCONSUMO     => TIPO DE CONSUMO A CARGAR 0 -> MANUAL, -1 -> PROMEDIO

   @NAME:    cargarConsumosManYProm
   @METHOD:  GET
 */
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_CICLO           IN PCK_SUBTIPOS.TI_CICLO,
    UN_DATOSEXCEL      IN VARCHAR2,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,
    UN_STRSQL          IN  CLOB,
    UN_STRLOG          IN  CLOB,
    UN_NUM_USU_ERROR   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_NUM_USU_OK      IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TIPOCONSUMO     IN PCK_SUBTIPOS.TI_LOGICO)
  RETURN CLOB
AS
  MI_DATOS              PCK_SYSMAN_UTL.T_SPLIT;
  MI_RTA                PCK_SUBTIPOS.TI_LOGICO;
  MI_RTA1               PCK_SUBTIPOS.TI_LOGICO;
  MI_CODIGOINTERNO      VARCHAR2(100);
  MI_CODIGORUTA         PCK_SUBTIPOS.TI_CODIGORUTA;
  MI_LECTURAAFOROACT    VARCHAR2(100);
  MI_CONSUMOACU         VARCHAR2(100);
  MI_CONSUMOALC         VARCHAR2(100);
  MI_PROBLEMA           VARCHAR2(100);
  MI_STRSQL             CLOB;
  MI_STRLOG             CLOB   := UN_STRLOG;
  MI_NUM_USU_ERROR      NUMBER := UN_NUM_USU_ERROR;
  MI_NUM_USU_OK         NUMBER := UN_NUM_USU_OK;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CREARPROBLEMA      PCK_SUBTIPOS.TI_LOGICO;
  MI_RETORNO            PCK_SUBTIPOS.TI_LOGICO := -1;
  MI_REGAFECT           PCK_SUBTIPOS.TI_LOGICO := -1;
BEGIN
  BEGIN
    MI_DATOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA => '' || UN_DATOSEXCEL || '', UN_DELIMITADOR => ',');
    MI_CODIGOINTERNO   := MI_DATOS(1);
    MI_CODIGORUTA      := MI_DATOS(2);
    MI_LECTURAAFOROACT := MI_DATOS(3);
    IF UN_TIPOCONSUMO = 0  THEN  -- 0 -> MANUAL, -1 -> PROMEDIO
        MI_CONSUMOACU      := MI_DATOS(4);
        MI_CONSUMOALC      := MI_DATOS(5);
        MI_PROBLEMA        := MI_DATOS(6);
    ELSE
        MI_PROBLEMA        := MI_DATOS(4);
    END IF;


    IF MI_LECTURAAFOROACT IS NOT NULL THEN
      MI_STRSQL           := MI_STRSQL || 'LECTURAAFORO = ' || MI_LECTURAAFOROACT;
    ELSE
      MI_STRLOG        := MI_STRLOG || ' El usuario ' || MI_CODIGOINTERNO || ' reporta lectura aforo vacia ' || CHR(10)||CHR(13);
      MI_NUM_USU_ERROR := MI_NUM_USU_ERROR + 1;
    END IF;

    IF UN_TIPOCONSUMO = 0 THEN 
      IF MI_CONSUMOACU IS NOT NULL THEN
        MI_STRSQL      := MI_STRSQL || ', CONSUMOACUMANUAL= ' || MI_CONSUMOACU;
      ELSE
        MI_STRLOG        :=  MI_STRLOG || ' El usuario ' || MI_CODIGOINTERNO || ' reporta consumo de acueducto vacio ' || CHR(10)||CHR(13);
        MI_NUM_USU_ERROR := MI_NUM_USU_ERROR + 1;
      END IF;
      IF MI_CONSUMOALC IS NOT NULL THEN
        MI_STRSQL      := MI_STRSQL || ', CONSUMOALCMANUAL= ' || MI_CONSUMOALC;
      ELSE
        MI_STRLOG        := MI_STRLOG || ' El usuario ' || MI_CODIGOINTERNO || ' reporta consumo de alcantarillado vacio ' || CHR(10)||CHR(13);
        MI_NUM_USU_ERROR := MI_NUM_USU_ERROR + 1;
      END IF;    
      MI_STRSQL := MI_STRSQL || ' , AFOROCONSMANUAL = -1 ';
    END IF;    

    MI_STRSQL := MI_STRSQL || ', DATE_MODIFIED = SYSDATE, MODIFIED_BY = '''|| UN_USUARIO ||''' ' ;
    MI_CONDICION     := 'COMPANIA    = '''||UN_COMPANIA
                        || ''' AND CICLO = '||UN_CICLO
                        || '  AND CODIGORUTA = '''|| MI_CODIGORUTA|| ''' ';
    BEGIN
      BEGIN
        MI_RTA1 := PCK_DATOS.FC_ACME(UN_TABLA => 'SP_USUARIO', 
                                              UN_ACCION => 'M', 
                                              UN_CAMPOS => MI_STRSQL, 
                                              UN_CONDICION => MI_CONDICION);
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTCONSUMO);
    END;

    IF MI_RTA1 = 0 THEN 
      MI_STRLOG := MI_STRLOG || ' El usuario '|| MI_CODIGOINTERNO || ' no está registrado ' || CHR(10)||CHR(13);
      MI_NUM_USU_ERROR := MI_NUM_USU_ERROR + 1;
    ELSE
      MI_CREARPROBLEMA := -1;
      IF MI_PROBLEMA IS NULL THEN  
        SELECT COUNT(*)
        INTO MI_RTA
        FROM SP_PROBLEMA
        WHERE COMPANIA    = UN_COMPANIA
        AND CLASEPROBLEMA = 'AFR'
        AND CODIGO        = '0';
        IF MI_RTA        <> 0 THEN
          SELECT COUNT(*)
          INTO MI_RTA
          FROM SP_USUARIO_PROBLEMA
          WHERE COMPANIA = UN_COMPANIA
          AND CLASE      = 'AFR'
          AND CODIGORUTA = MI_CODIGORUTA
          AND PROBLEMA   = '0';
          IF MI_RTA     = 0 THEN
            MI_CREARPROBLEMA := -1;
            MI_CAMPOS   := 'COMPANIA, ANO, PERIODO, CICLO, CODIGORUTA, PROBLEMA, CLASE, LECTURA, CREATED_BY, DATE_CREATED';
            MI_VALORES  := ' '''||UN_COMPANIA||''' , 
                           '||UN_ANO||' , 
                           ''' || PCK_SERVICIOS_PUBLICOS_COM2.FC_PERIODOSIGUIENTE(UN_COMPANIA, UN_ANO, UN_PERIODO, NULL) || ''' ,                                           
                           ''' || UN_CICLO ||''', 
                           '''||MI_CODIGORUTA||''' , 
                           '''||MI_PROBLEMA||''', 
                           ''AFR'', 
                           '||MI_LECTURAAFOROACT||' ,
                           '''||UN_USUARIO ||''', SYSDATE
                           ';
            BEGIN
              BEGIN
                MI_REGAFECT := PCK_DATOS.FC_ACME (UN_TABLA => 'SP_USUARIO_PROBLEMA', 
                                                       UN_ACCION => 'I', 
                                                       UN_CAMPOS => MI_CAMPOS, 
                                                       UN_VALORES => MI_VALORES);                                              
              EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE, 
                                          UN_ERROR_COD => PCK_ERRORES.ERROR_FACSERP_INS_DATOS );
            END;
            IF MI_REGAFECT = 0 THEN
              MI_STRLOG := MI_STRLOG || 'Al usuario ' || MI_CODIGOINTERNO || ' se le actualizaron lectura y consumos, pero no se ha podido crear el problema de aforo ' || MI_PROBLEMA || '' || CHR(10);
              MI_CREARPROBLEMA := 0;
            END IF;          

          END IF; -- IS NULL
        END IF; -- IS NOT NULL
      ELSE
        MI_STRLOG := MI_STRLOG || 'El usuario '|| MI_CODIGOINTERNO || ' reporta un problema de aforo que no existe (codigo = ' || MI_PROBLEMA || ') ' || CHR(10);
        MI_CREARPROBLEMA := 0;
      END IF; --  IF UN_PROBLEMA = '' 

      MI_NUM_USU_OK := MI_NUM_USU_OK + 1;
      IF MI_CREARPROBLEMA = 0 THEN
        MI_NUM_USU_OK := MI_NUM_USU_OK -1;
        MI_NUM_USU_ERROR := MI_NUM_USU_ERROR + 1;
      END IF;

    END IF;   -- verificarNumUsuarios
  END;

  IF MI_NUM_USU_ERROR = 0 THEN
    RETURN '*' || '#' ||MI_NUM_USU_OK||'';
  ELSE
    RETURN MI_STRLOG || '#' || MI_NUM_USU_OK || '#' || MI_NUM_USU_ERROR ;
  END IF;
END FC_CARGAR_CONSUMOS;

--16
PROCEDURE PR_ACTUALIZARCONCEPTO
    /*
      NAME              : PR_ACTUALIZARCONCEPTO 
      AUTHORS           : STEFANINI SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 08/06/2017
      TIME              : 02:54 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Este procedimiento se ejecuta al realizar actualizaciones en el valor de la deuda o en el 
                          valor facturado de un concepto en la factura. Registra los valores anteriores y los actuales 
                          en la tabla SP_MODIFICACIONESDEUDA y adicionalmente realiza el registro de estos cambios en 
                          la tabla SP_AUDITORIAMNUMOD.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación
                          UN_CICLO              => Ciclo seleccionado al ingresar por consulta de facturacion
                          UN_ANIO               => Año relacionado al ciclo que se está trabajando 
                          UN_PERIODO            => Periodo relacionado al ciclo que se está trabajando 
                          UN_CODIGORUTA         => Código de Ruta del usuario
                          UN_CODIGOINTERNO      => Código Interno del usuario
                          UN_CONCEPTO           => Número de Concepto  que se está modificando
                          UN_USUARIO            => Usuario que accede al sistema
                          UN_DEUDAANTERIOR      => Valor inicial de la deuda en el concepto que se está modificando
                          UN_DEUDANUEVA         => Valor actual asignado a la deuda del concepto que se está modificando
                          UN_FACTURADOANTERIOR  => Valor facturado inicialmente al concepto 
                          UN_FACTURADONUEVO     => Valor facturado actualizado del concepto 
      @NAME  :  actualizarConcepto
      @METHOD:  POST     
    */ 
 (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO,
    UN_ANIO               IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO            IN PCK_SUBTIPOS.TI_PERIODO,     
    UN_CODIGORUTA         IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CODIGOINTERNO      IN SP_USUARIO.CODIGOINTERNO%TYPE,
    UN_CONCEPTO           IN SP_CONCEPTOS.CODIGO%TYPE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
    UN_DEUDAANTERIOR      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_DEUDANUEVA         IN PCK_SUBTIPOS.TI_DOBLE,
    UN_FACTURADOANTERIOR  IN PCK_SUBTIPOS.TI_DOBLE,
    UN_FACTURADONUEVO     IN PCK_SUBTIPOS.TI_DOBLE	
 )
 AS 
    MI_CONSECUTIVO        SP_AUDITORIAMNUMOD.CONSECUTIVO%TYPE; 
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  IF UN_DEUDANUEVA <> UN_DEUDAANTERIOR THEN 

    MI_TABLA   := 'SP_MODIFICACIONESDEUDA';
    MI_CAMPOS  := ' COMPANIA '||
                  ', CICLO '||
                  ', CODIGORUTA '||
                  ', CONCEPTO '||
                  ', DATE_CREATED '||
                  ', CREATED_BY '||
                  ', VRANT '||
                  ', VRNUE '||
                  ', TIPOMODIFICACION '||
                  ', CAUSAMODIFICACION '||
                  ', ANO '||
                  ', PERIODO '||
                  ', FECHA'||
                  ', HORA';
    MI_VALORES :=  ' '''||UN_COMPANIA||''' '||
                   ', '''||UN_CICLO||''' '|| 
                   ', '''||UN_CODIGORUTA||''' '||
                   ', '||UN_CONCEPTO||' '||
                   ', SYSDATE '||
                   ', '''||UN_USUARIO||''' '||
                   ', '||UN_DEUDAANTERIOR||' '||
                   ', '||UN_DEUDANUEVA||' '||
                   ', 1 '||
                   ', 1 '||
                   ', '|| UN_ANIO ||' '||
                   ', '''||UN_PERIODO||''' '||
                   ', TRUNC(SYSDATE) '||
                   ', SYSDATE';

    BEGIN
      BEGIN 
        MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I' 
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
    	  MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'CICLO';
        MI_MSGERROR(2).VALOR := UN_CICLO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSMODIFICACIONES
                 ,UN_TABLAERROR => MI_TABLA
                 ,UN_REEMPLAZOS => MI_MSGERROR
                );  
    END;  


    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_AUDITORIAMNUMOD',
                                                       UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||'''',
                                                       UN_CAMPO    => 'CONSECUTIVO',
                                                       UN_INICIAL  => '1');

    MI_TABLA   := 'SP_AUDITORIAMNUMOD';
    MI_CAMPOS  := '  CONSECUTIVO '||
                  ', CODIGO '||
                  ', CUENTA '||
                  ', HORA '||
                  ', FORMULARIO '||
                  ', CAMPO '||
                  ', VALORINICIAL '||
                  ', VALORFINAL '||
                  ', COMPANIA '||
                  ', CICLO '||
                  ', CODIGORUTA '||
                  ', ANO '||
                  ', PERIODO '||
                  ', DATE_CREATED '||
                  ', CREATED_BY ';
    MI_VALORES  := '  '|| MI_CONSECUTIVO ||' '||
                   ', ''RUTA'|| UN_CODIGORUTA ||'^INTERNO'|| UN_CODIGOINTERNO ||''' '||
                   ', '''|| UN_USUARIO ||''' '||
                   ', SYSDATE'||
                   ', ''FACTURA'' '||
                   ', ''CONCEPTO '|| UN_CONCEPTO ||' DEUDA '' '||
                   ', '|| UN_DEUDAANTERIOR ||' '||
                   ', '|| UN_DEUDANUEVA ||' '||
                   ', '''|| UN_COMPANIA ||''' '||
                   ', '|| UN_CICLO ||' '|| 
                   ', '''|| UN_CODIGORUTA ||''' '||
                   ', '|| UN_ANIO ||' '||
                   ', '''||UN_PERIODO||''' '||
                   ', SYSDATE '||
                   ', '''||UN_USUARIO||''' ';

    BEGIN
      BEGIN 
        MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I' 
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
    	  MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'CICLO';
        MI_MSGERROR(2).VALOR := UN_CICLO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSAUDITORIAMNUMOD
                 ,UN_TABLAERROR => MI_TABLA
                 ,UN_REEMPLAZOS => MI_MSGERROR
                );  
    END;  
  END IF; 

  IF UN_FACTURADONUEVO <> UN_FACTURADOANTERIOR THEN 

    MI_TABLA   := 'SP_MODIFICACIONESDEUDA';
    MI_CAMPOS  := ' COMPANIA '||
                  ', CICLO '||
                  ', CODIGORUTA '||
                  ', CONCEPTO '||
                  ', DATE_CREATED '||
                  ', CREATED_BY '||
                  ', VRANT '||
                  ', VRNUE '||
                  ', TIPOMODIFICACION '||
                  ', CAUSAMODIFICACION '||
                  ', ANO '||
                  ', PERIODO '||
                  ', FECHA'||
                  ', HORA';
    MI_VALORES :=  ' '''|| UN_COMPANIA ||''' '||
                   ', '''|| UN_CICLO ||''' '|| 
                   ', '''|| UN_CODIGORUTA ||''' '||
                   ', '|| UN_CONCEPTO ||' '||
                   ', SYSDATE '||
                   ', '''|| UN_USUARIO ||''' '||
                   ', '|| UN_FACTURADOANTERIOR ||' '||
                   ', '|| UN_FACTURADONUEVO ||' '||
                   ', 2 '||
                   ', 1 '||
                   ', '|| UN_ANIO ||' '||
                   ', '''|| UN_PERIODO ||''' '||
                   ', TRUNC(SYSDATE) '||
                   ', SYSDATE + 1/86400';

    BEGIN
      BEGIN 
        MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I' 
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'CICLO';
        MI_MSGERROR(2).VALOR := UN_CICLO;
        MI_MSGERROR(3).CLAVE := 'CONCEPTO';
        MI_MSGERROR(3).VALOR := UN_CONCEPTO;        
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSMODIFIVALOR
                 ,UN_TABLAERROR => MI_TABLA
                 ,UN_REEMPLAZOS => MI_MSGERROR
                );  
    END;  

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_AUDITORIAMNUMOD',
                                                       UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||'''',
                                                       UN_CAMPO    => 'CONSECUTIVO',
                                                       UN_INICIAL  => '1');


    MI_TABLA   := 'SP_AUDITORIAMNUMOD';
    MI_CAMPOS  := '  CONSECUTIVO '||
                  ', CODIGO '||
                  ', CUENTA '||
                  ', HORA '||
                  ', FORMULARIO '||
                  ', CAMPO '||
                  ', VALORINICIAL '||
                  ', VALORFINAL '||
                  ', COMPANIA '||
                  ', CICLO '||
                  ', CODIGORUTA '||
                  ', ANO '||
                  ', PERIODO '||
                  ', DATE_CREATED '||
                  ', CREATED_BY ';
    MI_VALORES :=  '  '|| MI_CONSECUTIVO ||' '||
                   ', ''RUTA'|| UN_CODIGORUTA ||'^INTERNO'|| UN_CODIGOINTERNO ||''' '||
                   ', '''|| UN_USUARIO ||''' '||
                   ', SYSDATE'||
                   ', ''FACTURA'' '||
                   ', ''CONCEPTO '|| UN_CONCEPTO ||' FACTURADO '' '||
                   ', '|| UN_FACTURADOANTERIOR ||' '||
                   ', '|| UN_FACTURADONUEVO ||' '||
                   ', '''|| UN_COMPANIA ||''' '||
                   ', '|| UN_CICLO ||' '|| 
                   ', '''|| UN_CODIGORUTA ||''' '||
                   ', '|| UN_ANIO ||' '||
                   ', '''||UN_PERIODO||''' '||
                   ', SYSDATE '||
                   ', '''||UN_USUARIO||''' ';

    BEGIN
      BEGIN 
        MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I' 
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
      	MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'CICLO';
        MI_MSGERROR(2).VALOR := UN_CICLO;
        MI_MSGERROR(3).CLAVE := 'CONCEPTO';
        MI_MSGERROR(3).VALOR := UN_CONCEPTO; 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSAUDITMNUMODVAL
                 ,UN_TABLAERROR => MI_TABLA
                 ,UN_REEMPLAZOS => MI_MSGERROR
                );  
    END; 

  END IF;

END PR_ACTUALIZARCONCEPTO;

--17
FUNCTION FC_ESCRIBIRPERIODOCORTO
  /*
   NAME              : FC_ESCRIBIRPERIODOCORTO 
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 09/06/2017
   TIME              : 08:09 AM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : OBTIENE EL VALOR DEL ULTIMO PAGO REALIZADO ANTERIOR A UNA FECHA POR UN USUARIO 
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CODIGORUTA      => CICLO EN EL QUE SE ESTA TRABAJANDO.
                       UN_ANIO            => ANIO EN EL QUE SE ESTA TRABAJANDO.
                       UN_FRECUENCIA      => FRECUENCIA EN LA QUE SE ESTA TRABAJANDO.
   MODIFICATIONS     :

   @NAME:    escribirperiodoCorto
   @METHOD:  GET
 */
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PERIODO    IN NUMBER,
    UN_ANIO       IN NUMBER, 
    UN_FRECUENCIA IN VARCHAR2 DEFAULT NULL
) RETURN VARCHAR2
AS
    MI_FRECUENCIA    NUMBER:=0;
    MI_FRECUENCIAPAR PARAMETRO.VALOR%TYPE;
    MI_PERIODO       NUMBER;
    MI_ANO           NUMBER;
BEGIN 
   IF UN_FRECUENCIA IS NULL THEN
       MI_FRECUENCIAPAR:= PCK_SYSMAN_UTL.FC_PAR(
                                      UN_COMPANIA  => UN_COMPANIA
                                     ,UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION'
                                     ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                     ,UN_FECHA_PAR => SYSDATE
                                     );
   ELSE
       MI_FRECUENCIAPAR:=UN_FRECUENCIA;
   END IF;
   MI_FRECUENCIA:= CASE MI_FRECUENCIAPAR WHEN 'M' THEN 1
                                         WHEN 'B' THEN 2
                                         WHEN 'C' THEN 2
                                         WHEN 'T' THEN 3
                   END;
    MI_PERIODO:=TO_NUMBER(UN_PERIODO);
    MI_ANO:=UN_ANIO;
    IF TO_NUMBER(UN_PERIODO) <= 0 THEN
       MI_PERIODO:= TO_NUMBER(UN_PERIODO) + (12 / MI_FRECUENCIA);
       MI_ANO:= MI_ANO - 1;
    END IF;    
    IF MI_PERIODO > (12 / MI_FRECUENCIA) THEN
       MI_PERIODO:= MI_PERIODO - (12 / MI_FRECUENCIA);
       MI_ANO:= MI_ANO + 1;
    END IF;
    RETURN PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODOCORTO(
                                      UN_COMPANIA   => UN_COMPANIA
                                     ,UN_ANO        => MI_ANO
                                     ,UN_PERIODO    => MI_PERIODO
                                     ,UN_FRECUENCIA => MI_FRECUENCIAPAR);
END FC_ESCRIBIRPERIODOCORTO;

-- 18
FUNCTION FC_PREPARARINFORMESUSPENSIONES 
    /*
      NAME              : FC_PREPARARINFORMESUSPENSIONES --> EN ACCESS PreparaInformeSuspensiones
      AUTHORS           : STEFANINI SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 14/06/2017
      TIME              : 12:45 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Construye el codigo SQL para enviar como reemplazo a la consulta del reporte 001270LCortesSuspensiones
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA          => Compañia de ingreso a la aplicación
                          UN_CICLOINICIAL      => Ciclo inicial en el que se desea generar el reporte
                          UN_CICLOFINAL        => Ciclo final en el que se desea generar el reporte
                          UN_ABONOS            => Indicador para incluir usuarios con abonos
                          UN_CHAPETAS          => Indicador para incluir usuarios con cahpetas
                          UN_PQR               => Indicador para incluir usuarios con PQR (Excluir cartera)
                          UN_PERIODOATRASOINI  => Periodo de Atraso Inicial en el que se desea generar el reporte
                          UN_PERIODOATRASOFIN  => Periodo de Atraso Final en el que se desea generar el reporte
                          UN_CONDICION         => Indica si se incluye la condición de Valor Superior a... de forma obligatoria u opcional
                          UN_VALORSUPERIOR     => Número ingresado por el usuario como Valor Superior para generar el reporte
                          UN_ORDENADOPOR       => Indica la opcion seleccionada por el usuario en el controlador para definir la opción de ordenamiento en el reporte
      @NAME  :  prepararInformeSuspensiones
      @METHOD:  GET
    */ 
  (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_CICLOINICIAL         IN  PCK_SUBTIPOS.TI_CICLO,
     UN_CICLOFINAL           IN  PCK_SUBTIPOS.TI_CICLO,
     UN_ABONOS               IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_CHAPETAS             IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_PQR                  IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_PERIODOATRASOINI     IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_PERIODOATRASOFIN     IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_CONDICION            IN  PCK_SUBTIPOS.TI_ENTERO,
     UN_VALORSUPERIOR        IN  PCK_SUBTIPOS.TI_DOBLE,
     UN_ORDENADOPOR          IN  PCK_SUBTIPOS.TI_ENTERO
  )
RETURN CLOB
AS 
    MI_PARAMETROFILTRAR      VARCHAR2(2 CHAR);
    MI_PARAMETROEXCLUIR      VARCHAR2(2 CHAR);
    MI_PARAMETROFECHA        DATE;
    MI_EXCLUIRADICIONAL      VARCHAR2(300 CHAR);
    MI_USUARIOSSINABONOS     CLOB;
    MI_NITENEXCLUIRMIN       VARCHAR2(50 CHAR);
    MI_FILTROABONOS          VARCHAR2(150 CHAR);
    MI_EXCLUIRMINUS          CLOB;

BEGIN

    MI_PARAMETROFILTRAR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'FILTRAR CHAPETAS ACTAS SUSPENSION',
                                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE);

    MI_PARAMETROEXCLUIR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS',
                                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE); 

     IF MI_PARAMETROEXCLUIR = 'SI' THEN 
        MI_EXCLUIRADICIONAL :=  '   AND USUARIO.COMPANIA   =  ''' || UN_COMPANIA || ''' ' ||
                                '   AND USUARIO.CICLO BETWEEN  '  || UN_CICLOINICIAL || ' AND  '  || UN_CICLOFINAL;     
                           END IF;

    -- 1. Agrega el INNER JOIN o no dependiendo el valor del parametro
    IF MI_PARAMETROFILTRAR = 'SI' THEN
        IF UN_ABONOS = 3 THEN
           MI_USUARIOSSINABONOS :=  'INNER JOIN ( ' ||
                                    '       SELECT USUARIO.COMPANIA,' ||
                                    '              USUARIO.CICLO,' ||
                                    '              USUARIO.CODIGORUTA,' ||
                                    '              USUARIO.ANO,' ||
                                    '              USUARIO.PERIODO,' ||
                                    '              USUARIO.CODIGOINTERNO,' ||
                                    '              SUM(USUARIO.NOTACREDITO) AS SUMADENOTACREDITO'||
                                    '              FROM SP_USUARIO USUARIO' ||
                                    '                LEFT JOIN SP_ABONOS ABONOS' ||
                                    '                  ON USUARIO.COMPANIA   = ABONOS.COMPANIA'||
                                    '                 AND USUARIO.CICLO      = ABONOS.CICLO'||
                                    '                 AND USUARIO.CODIGORUTA = ABONOS.CODIGORUTA'||
                                    '                 AND USUARIO.ANO        = ABONOS.ANO' ||
                                    '                 AND USUARIO.PERIODO    = ABONOS.PERIODO'||
                                    '                WHERE ABONOS.COMPANIA IS NULL'||
                                    '               '|| MI_EXCLUIRADICIONAL ||
                                    '                GROUP BY USUARIO.COMPANIA,' ||
                                    '                         USUARIO.CICLO,' ||
                                    '                         USUARIO.CODIGORUTA,' ||
                                    '                         USUARIO.ANO,' ||
                                    '                         USUARIO.PERIODO,' ||
                                    '                         USUARIO.CODIGOINTERNO' ||
                                    '                         HAVING SUM(USUARIO.NOTACREDITO) IN(0) '||
                                    ') USUARIOSSINABONOS ' ||
                                    '  ON USUARIO.COMPANIA   = USUARIOSSINABONOS.COMPANIA ' ||
                                    ' AND USUARIO.CICLO      = USUARIOSSINABONOS.CICLO ' ||
                                    ' AND USUARIO.CODIGORUTA = USUARIOSSINABONOS.CODIGORUTA '||
                                    ' AND USUARIO.ANO        = USUARIOSSINABONOS.ANO ' ||
                                    ' AND USUARIO.PERIODO    = USUARIOSSINABONOS.PERIODO ';

        END IF;

    ELSE
        MI_NITENEXCLUIRMIN := ',USUARIO.NIT '; 
    END IF;                                                                                            


    -- 2. Dependiendo de la selección de abonos, se define el tipo de Join y el filtro de abonos
    IF UN_ABONOS = -1 OR UN_ABONOS = 0 THEN
        MI_USUARIOSSINABONOS :=  MI_USUARIOSSINABONOS || CASE WHEN UN_ABONOS = -1 THEN 'LEFT ' ELSE 'INNER ' END ||
                                ' JOIN SP_ABONOS ABONOS ' ||
                                '  ON USUARIO.COMPANIA    = ABONOS.COMPANIA ' ||
                                ' AND USUARIO.CODIGORUTA  = ABONOS.CODIGORUTA ' ||
                                ' AND USUARIO.CICLO       = ABONOS.CICLO ' ||
                                ' AND USUARIO.ANO         = ABONOS.ANO ' ||
                                ' AND USUARIO.PERIODO     = ABONOS.PERIODO';

        IF MI_PARAMETROFILTRAR = 'SI' THEN
            MI_FILTROABONOS := CASE WHEN UN_ABONOS = -1 
                                    THEN ' AND ABONOS.COMPANIA IS NULL AND USUARIO.NOTACREDITO IN(0) ' 
                                    ELSE ' AND USUARIO.NOTACREDITO NOT IN(0) ' 
                               END; 
        ELSE 
            MI_FILTROABONOS := CASE WHEN UN_ABONOS = -1 
                                    THEN ' AND ABONOS.COMPANIA IS NULL ' 
                                    ELSE ' ' 
                               END;                                    
        END IF;                                
    END IF;

    -- 3. Agrega los filtros de la consulta
    MI_USUARIOSSINABONOS :=  MI_USUARIOSSINABONOS || 
                            ' WHERE USUARIO.COMPANIA  = ''' || UN_COMPANIA || ''' ' ||
                            '  AND USUARIO.CICLO BETWEEN '  || UN_CICLOINICIAL || ' AND ' || UN_CICLOFINAL || ' ' ||
                            '  AND USUARIO.BANCOPERPROCESO IS NULL' ||
                            '  AND USUARIO.ESTADO NOT IN(''R'',''S'') ' || MI_FILTROABONOS;

    -- 4. Filtros Adicionales de acuerdo a las opciones seleccionadas en Chapetas, PQR e Incluir Valor Superior
    IF UN_CHAPETAS = 0 OR UN_CHAPETAS = -1 THEN
        MI_USUARIOSSINABONOS :=  MI_USUARIOSSINABONOS || ' AND CHAPETAS = ' || UN_CHAPETAS || ' ';
    END IF;

    IF UN_PQR = 0 OR UN_PQR = -1 THEN 
        MI_USUARIOSSINABONOS :=  MI_USUARIOSSINABONOS ||
                                 CASE WHEN UN_PQR = 0 
                                      THEN ' AND EXCLUIRCARTERA = -1 '
                                      ELSE ' AND EXCLUIRCARTERA = 0 '
                                 END;
    END IF;

    MI_USUARIOSSINABONOS :=  MI_USUARIOSSINABONOS || 
                             ' AND ' || CASE WHEN UN_CONDICION = 1 THEN ' ' ELSE ' (' END ||  
                             ' NVL(USUARIO.PERIODOSATRASO,0) BETWEEN '|| UN_PERIODOATRASOINI || ' AND ' || UN_PERIODOATRASOFIN || ' '||
                             CASE WHEN UN_CONDICION = 1 THEN ' AND 'ELSE ' OR' END ||
                             ' USUARIO.TOTFACTURAPERACTUAL >= ' || UN_VALORSUPERIOR || ' ' || 
                             CASE WHEN UN_CONDICION = 1 THEN ' ' ELSE ' )' END  ;                                 

    -- 5. Continua armando la consulta, adiciona un MINUS para eliminar
    --    algunos registros del resultado final dependiendo el valor del
    --    parametro "EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS" y adiciona los ordenamientos                                 

    IF MI_PARAMETROEXCLUIR = 'SI' AND UN_PQR = 0 THEN 
        MI_PARAMETROFECHA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'FECHA CORTE DE PQR PARA SUI',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE);

        MI_EXCLUIRMINUS := ' MINUS ' ||
                            'SELECT DISTINCT ' ||
                                  'USUARIO.CODIGORUTA, '||
                                  'USUARIO.CODIGOINTERNO, ' ||
                                  'USUARIO.PRIMERAPELLIDO  ||'' ''|| USUARIO.SEGUNDOAPELLIDO  ||'' ''|| USUARIO.NOMBRES NOMBRECOMPLETO, ' ||
                                  'USUARIO.DIRTECNICA, ' ||
                                  'USUARIO.DIRGUIA, ' ||
                                  'USUARIO.LECTURA, ' ||
                                  'USUARIO.PERIODOSATRASO, ' ||
                                  'USUARIO.TOTFACTURAPERACTUAL, ' ||
                                  'CASE WHEN SP_MEDIDOR.CODIGO IS NOT NULL THEN SP_MEDIDOR.CODIGO ELSE '' '' END MEDIDOR,' ||
                                  'USUARIO.CODIGOCATASTRAL, ' ||
                                  'CASE WHEN USUARIO.CHAPETAS NOT IN (0) THEN ''x'' ELSE '' '' END CHAPETAS, ' ||
                                  'USUARIO.EXCLUIRCARTERA, ' ||
                                  'USUARIO.BANCOPERPROCESO, ' ||
                                  'USUARIO.ESTADO ' ||
                                  ',USUARIO.NIT ' ||
                            ' FROM (SP_USUARIO USUARIO ' ||
                            '       INNER JOIN SP_ORDENTRABAJO ORDENTRABAJO ' ||
                            '          ON USUARIO.COMPANIA        = ORDENTRABAJO.COMPANIA '||
                            '         AND USUARIO.CODIGORUTA      = ORDENTRABAJO.CODIGORUTA '||
                            '         AND USUARIO.CICLO           = ORDENTRABAJO.CICLO) ' || 
                            '       INNER JOIN SP_D_ORDENTRABAJO D_ORDENTRABAJO ' ||
                            '          ON ORDENTRABAJO.COMPANIA   = D_ORDENTRABAJO.COMPANIA '||
                            '         AND ORDENTRABAJO.CLASEDOC   = D_ORDENTRABAJO.CLASEDOC '||
                            '         AND ORDENTRABAJO.NUMORDEN   = D_ORDENTRABAJO.ORDENTRABAJO '||
                            '       INNER JOIN SP_MEDIDOR '|| 
                            '          ON USUARIO.COMPANIA        = SP_MEDIDOR.COMPANIA '||
                            '         AND USUARIO.MEDIDOR         = SP_MEDIDOR.CONSECUTIVO '||
                            ' WHERE (D_ORDENTRABAJO.TIPORESPUESTA       IS NULL ' ||
                            '       OR D_ORDENTRABAJO.FECHASOLUCION     IS NULL ' ||
                            '       OR D_ORDENTRABAJO.FECHANOTIFICACION IS NULL ' ||
                            '       OR D_ORDENTRABAJO.TIPONOTIFICACION  IS NULL )' ||
                            '       AND USUARIO.COMPANIA                 = ''' || UN_COMPANIA || ''' ' ||
                            '       AND USUARIO.CICLO BETWEEN ' || UN_CICLOINICIAL || ' AND ' || UN_CICLOFINAL || ' ' ||
                            ' AND ORDENTRABAJO.FECHASOLICITUD >= TO_DATE(''' || MI_PARAMETROFECHA || ''',''DD/MM/YYYY'') ' ||
                            ' AND ORDENTRABAJO.CLASEDOC        = ''PQR'' ' ||
                            ' AND ORDENTRABAJO.NUMORDEN       IS NOT NULL ' ||
                            ' AND USUARIO.TOTFACTURAPERACTUAL >= 0 ' ||
                            ' AND USUARIO.ESTADO NOT          IN (''R'',''S'') ';        
    END IF;

    IF UN_ORDENADOPOR = 1 THEN
        MI_USUARIOSSINABONOS := MI_USUARIOSSINABONOS ||
                                CASE WHEN MI_PARAMETROEXCLUIR = 'SI' AND UN_PQR = 0 
                                     THEN MI_EXCLUIRMINUS || ' ORDER BY CODIGORUTA' 
                                     ELSE ' ORDER BY USUARIO.CICLO, USUARIO.CODIGORUTA '
                                END;         

    ELSIF UN_ORDENADOPOR = 2 THEN
        MI_USUARIOSSINABONOS := MI_USUARIOSSINABONOS ||
                                CASE WHEN MI_PARAMETROEXCLUIR = 'SI' AND UN_PQR = 0 
                                     THEN MI_EXCLUIRMINUS || ' ORDER BY PERIODOSATRASO, CODIGORUTA ' 
                                     ELSE ' ORDER BY USUARIO.PERIODOSATRASO, USUARIO.CODIGORUTA '
                                END;            

    ELSE
        MI_USUARIOSSINABONOS := MI_USUARIOSSINABONOS ||
                                CASE WHEN MI_PARAMETROEXCLUIR = 'SI' AND UN_PQR = 0 
                                     THEN MI_EXCLUIRMINUS || ' ORDER BY TOTFACTURAPERACTUAL, CODIGORUTA ' 
                                     ELSE ' ORDER BY USUARIO.TOTFACTURAPERACTUAL, USUARIO.CODIGORUTA '
                                END;         
    END IF;        

  RETURN MI_USUARIOSSINABONOS;
END FC_PREPARARINFORMESUSPENSIONES;

--19
  PROCEDURE PR_ACTUALIZARDATOSMULTIUSU 
    /*
      NAME              : PR_ACTUALIZARDATOSMULTIUSU 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 10,12,14/06/2017
      TIME              : 12:02 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite actualizar los valores del porcentaje y cantidad total a los usuarios correpondientes
                          al ciclo y código de ruta seleccionados después de insertar, actualizar o eliminar información de los usos en el formulario 
                          Multiusuarios.
                          Ruta: Panel Principal\Facturación de Servicios Públicos\Suscriptores\Información Suscriptores\Botón Multiusuarios
      PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación.  
                          UN_CICLO       => Número del ciclo seleccionado.
                          UN_CODIGORUTA  => Código de ruta seleccionado.
                          UN_USUARIO     => Código del usuario que realiza la modificación. 


      @NAME:  actualizarDatosMultiusuarios
      @METHOD:  PUT
      */  
     ( 
       UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
       UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO,
       UN_CODIGORUTA     IN PCK_SUBTIPOS.TI_CODIGORUTA,
       UN_USUARIO        IN VARCHAR2
     )
  AS 
      MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS; 
      MI_VALORES        PCK_SUBTIPOS.TI_VALORES; 
      MI_RTAACME        PCK_SUBTIPOS.TI_RTA_ACME; 
      MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION; 
      MI_CANTIDADTOTAL  PCK_SUBTIPOS.TI_ENTERO; 
      MI_PORCENTAJE     PCK_SUBTIPOS.TI_PORCENTAJE;
      MI_AJUSTEPORC     PCK_SUBTIPOS.TI_PORCENTAJE := 0;
      MI_CAMPOMSG       VARCHAR2(30 CHAR);
      MI_MSGERROREXC    PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN  
    BEGIN
      <<ACT_PORCENTAJE>>
      FOR RS_CANTIDAD IN (SELECT ROUND(SP_MULTIUSUARIOS.CANTIDAD,2) CANTIDAD ,             
                                       SP_MULTIUSUARIOS.PORCENTAJE,                 
                                       CANTIDADTOTAL.CANTIDADTOTAL,  
                                       SP_MULTIUSUARIOS.USO,   
                                       SP_MULTIUSUARIOS.ESTRATO,   
                                       SP_MULTIUSUARIOS.ESTRATOASEO          
                                  FROM SP_MULTIUSUARIOS           
                                       INNER JOIN (SELECT SP_MULTIUSUARIOS.COMPANIA,                     
                                                          SP_MULTIUSUARIOS.CICLO,                       
                                                          SP_MULTIUSUARIOS.CODIGORUTA,                       
                                                          SUM(SP_MULTIUSUARIOS.CANTIDAD)CANTIDADTOTAL                     
                                                     FROM SP_MULTIUSUARIOS                      
                                                    WHERE SP_MULTIUSUARIOS.COMPANIA   = UN_COMPANIA 
                                                      AND SP_MULTIUSUARIOS.CICLO      = UN_CICLO                     
                                                      AND SP_MULTIUSUARIOS.CODIGORUTA = UN_CODIGORUTA                      
                                                    GROUP BY SP_MULTIUSUARIOS.COMPANIA,                               
                                                             SP_MULTIUSUARIOS.CICLO,                              
                                                             SP_MULTIUSUARIOS.CODIGORUTA ) CANTIDADTOTAL          
                                               ON CANTIDADTOTAL.COMPANIA   = SP_MULTIUSUARIOS.COMPANIA         
                                              AND CANTIDADTOTAL.CICLO      = SP_MULTIUSUARIOS.CICLO         
                                              AND CANTIDADTOTAL.CODIGORUTA = SP_MULTIUSUARIOS.CODIGORUTA)
      LOOP
        BEGIN
          IF   RS_CANTIDAD.CANTIDAD = 0 
          AND  RS_CANTIDAD.CANTIDADTOTAL = 0
          THEN
            MI_PORCENTAJE := 0;
          ELSE
            MI_PORCENTAJE := ROUND(RS_CANTIDAD.CANTIDAD/RS_CANTIDAD.CANTIDADTOTAL,2);
          END IF;
          MI_AJUSTEPORC := MI_AJUSTEPORC + MI_PORCENTAJE;

          MI_CAMPOS    := '  PORCENTAJE    = '   || MI_PORCENTAJE || 
                          ', MODIFIED_BY   = ''' || UN_USUARIO || ''',  
                             DATE_MODIFIED = SYSDATE';
          MI_CONDICION :=   ' COMPANIA    = ''' || UN_COMPANIA ||
                      ''' AND CICLO       = '   || UN_CICLO || 
                        ' AND CODIGORUTA  = ''' || UN_CODIGORUTA || 
                      ''' AND USO         = ''' || RS_CANTIDAD.USO || 
                      ''' AND ESTRATO     = ''' || RS_CANTIDAD.ESTRATO || 
                      ''' AND ESTRATOASEO = ''' || RS_CANTIDAD.ESTRATOASEO || '''';
          MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_MULTIUSUARIOS',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
          MI_CANTIDADTOTAL := RS_CANTIDAD.CANTIDADTOTAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          MI_CAMPOMSG := 'el porcentaje';
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
      END LOOP ACT_PORCENTAJE;

      IF MI_AJUSTEPORC < 1
      THEN
        MI_PORCENTAJE := MI_PORCENTAJE +(1 - MI_AJUSTEPORC);
      END IF;

      BEGIN
        MI_CAMPOS    := '  CANTIDADTOTAL = '   || MI_CANTIDADTOTAL|| 
                        ', MODIFIED_BY   = ''' || UN_USUARIO || ''',  
                           DATE_MODIFIED = SYSDATE';
        MI_CONDICION :=   ' COMPANIA   = ''' || UN_COMPANIA ||
                    ''' AND CICLO      = '   || UN_CICLO || 
                      ' AND CODIGORUTA = ''' || UN_CODIGORUTA || '''';
        MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_MULTIUSUARIOS',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);               
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        MI_CAMPOMSG := 'la cantidad total';
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN   
         MI_MSGERROREXC(1).CLAVE := 'CAMPO';  
         MI_MSGERROREXC(1).VALOR := MI_CAMPOMSG;  
         MI_MSGERROREXC(2).CLAVE := 'CODIGORUTA';  
         MI_MSGERROREXC(2).VALOR := UN_CODIGORUTA;  
         PCK_ERR_MSG.RAISE_WITH_MSG(  
           UN_EXC_COD    => SQLCODE,  
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTMULTIUSUARIO,  
           UN_REEMPLAZOS => MI_MSGERROREXC,  
           UN_TABLAERROR => 'SP_MULTIUSUARIOS'  
         );                      
      END; 
  END PR_ACTUALIZARDATOSMULTIUSU;

--20  
PROCEDURE PR_GENERARORDENTRABAJO
(
   /*
      NAME              : PR_GENERARORDENTRABAJO -- Access -> Evento del Botón Generar Orden de Trabajo 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADO      : 15/06/2017
      TIME              : 12:48 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Procedimiento que permite insertar una orden de trabajo a las tablas SP_ORDENTRABAJO y SP_D_ORDENTRABAJO
                          Ruta: Panel Principal\Facturación de Servicios Públicos\P.Q.R\Registro de Peticiones, quejas y reclamos\Botón Generar Orden de Trabajo
      PARAMETERS        : UN_COMPANIA     => Compañia de ingreso a la aplicación.  
                          UN_CONSECUTIVO  => Consecutivo de la orden de trabajo -> CLASEDOC = ORD.
                          UN_NUMORDEN     => Número seleccionado por el usuario 
                          UN_USUARIO      => Código del usuario que realiza la inserción. 


      @NAME:  generarOrdenDeTrabajo
      @METHOD:  POST
      */  
	UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CONSECUTIVO IN PCK_SUBTIPOS.TI_LONG,
	UN_NUMORDEN    IN PCK_SUBTIPOS.TI_LONG,
	UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO	

)
	AS
		MI_TABLA   PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS  PCK_SUBTIPOS.TI_CAMPOS;
		MI_VALORES PCK_SUBTIPOS.TI_VALORES;

	BEGIN
		BEGIN 
			BEGIN 
				MI_TABLA := 'SP_ORDENTRABAJO';
				MI_CAMPOS := 'COMPANIA, CLASEDOC, NUMORDEN, TIPOREQUERIMIENTO, CICLO, CODIGORUTA, PRESENTACION, ENVIOOFICINA, 
							NECESITAVISITA, DEPENDENCIAENV, FECHAENVIO, RECIBIDOPOR, HORASOLICITUD, FECHASOLICITUD, 
							OPERADOR, AFORADOR, PRIMERAPELLIDO, SEGUNDOAPELLIDO, NOMBRES, DIRTECNICA, DIRGUIA, TELEFONO, 
							REMISION, RADICADO, SOLICITANTE, NUMORDENEXT, OFPRESENTACION,NUMORDENT,CREATED_BY,DATE_CREATED';
				MI_VALORES := '	SELECT 	COMPANIA,''ORD'','||UN_CONSECUTIVO||',TIPOREQUERIMIENTO,CICLO,CODIGORUTA,PRESENTACION,ENVIOOFICINA,
										NECESITAVISITA,DEPENDENCIAENV,FECHAENVIO,RECIBIDOPOR,HORASOLICITUD,FECHASOLICITUD,OPERADOR,AFORADOR,PRIMERAPELLIDO, 
										SEGUNDOAPELLIDO,NOMBRES,DIRTECNICA,DIRGUIA,TELEFONO,NUMORDEN,RADICADO,SOLICITANTE,NUMORDENEXT,OFPRESENTACION,'||UN_NUMORDEN||', '''||UN_USUARIO||''', SYSDATE
								FROM SP_ORDENTRABAJO
								WHERE COMPANIA   = '''||UN_COMPANIA||'''
									AND CLASEDOC =''PQR'' 
									AND NUMORDEN = '||UN_NUMORDEN;			

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
														UN_ACCION   =>  'IS', 
														UN_CAMPOS   =>  MI_CAMPOS, 
														UN_VALORES  =>  MI_VALORES);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;	
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_INSE_ORDENTRABAJO
                      );	
		END;

		BEGIN 
			BEGIN 
				MI_TABLA := 'SP_D_ORDENTRABAJO';
				MI_CAMPOS := 'COMPANIA, CLASEDOC, ORDENTRABAJO, NUMERO, CLASEPROBLEMA, PROBLEMA, SOLUCION, OBSERVACIONES, 
								FECHASOLUCION, INDFAVOREMPRESA, VRFACTURADOANT, NUEVOVALOR, TOTALFACTURAANT, TOTFACTURADO, CONCEPTO,CREATED_BY,DATE_CREATED';
				MI_VALORES := '	SELECT 	COMPANIA,''ORD'','||UN_CONSECUTIVO||',NUMERO,CLASEPROBLEMA,PROBLEMA,SOLUCION,OBSERVACIONES,FECHASOLUCION,INDFAVOREMPRESA, 
										VRFACTURADOANT,NUEVOVALOR,TOTALFACTURAANT,TOTFACTURADO,CONCEPTO,'''||UN_USUARIO||''',SYSDATE
								FROM SP_D_ORDENTRABAJO
								WHERE COMPANIA= '''||UN_COMPANIA||'''
									AND CLASEDOC= ''PQR''
									AND ORDENTRABAJO = '||UN_NUMORDEN;	
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
														UN_ACCION   =>  'IS', 
														UN_CAMPOS   =>  MI_CAMPOS, 
														UN_VALORES  =>  MI_VALORES);          
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;	
			END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_INSE_ORDENTRABAJO
                      );	
		END;	
END;		

--21
FUNCTION FC_ACT_MEDIDOR(
      /*
      NAME              : FC_ACT_MEDIDOR 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIAN ESTEVEN GUERRERO GUERRERO
      DATE MIGRADO      : 21/06/2017
      TIME              : 11:04 PM
      SOURCE MODULE     : SERVICIOS PÚBLICOS 
      DESCRIPTION       : Función que permite actualizar el número de medidor a los suscriptores.
                          Ruta: Panel Principal\Facturación de Servicios Públicos\
      PARAMETERS        : UN_COMPANIA     => Compañia de ingreso a la aplicación.  
                          UN_CONSECUTIVO  => Consecutivo del medidor.
                          UN_CODIGORUTA   => Código de ruta del suscriptor.
                          UN_CICLO        => Ciclo de facturación.
                          UN_USUARIO      => Usuario que ingresa al sistema. 

      @NAME: ACT_MEDIDOR
      @METHOD:  POST
      */  
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONSECUTIVO IN SP_MEDIDOR.CONSECUTIVO%TYPE,
    UN_CODIGORUTA  IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CICLO       IN PCK_SUBTIPOS.TI_CICLO, 
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO )
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
MI_EXISTE SP_MEDIDOR.CODIGO%TYPE;
MI_TABLA PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
MI_RTA PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    BEGIN     
      SELECT CODIGO
        INTO MI_EXISTE
        FROM SP_MEDIDOR
      WHERE COMPANIA    = UN_COMPANIA
        AND CONSECUTIVO = UN_CONSECUTIVO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN
    MI_TABLA:= 'SP_MEDIDOR';
    MI_CAMPOS:= ' CICLO         = '||UN_CICLO||'
                , CODIGORUTA    = '''||UN_CODIGORUTA ||''' 
                , DATE_MODIFIED = SYSDATE
                , MODIFIED_BY   = '''||UN_USUARIO||''' ';
    MI_CONDICION:=  ' COMPANIA = '''||UN_COMPANIA||''' 
                      AND CONSECUTIVO ='|| UN_CONSECUTIVO;
    DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      BEGIN
          MI_RTA:=PCK_DATOS.FC_ACME(
                            UN_TABLA => MI_TABLA, 
                            UN_CAMPOS => MI_CAMPOS,
                            UN_CONDICION => MI_CONDICION,
                            UN_ACCION => 'M');
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_REEMPLAZOS(0).CLAVE:='MEDIDOR';
          MI_REEMPLAZOS(0).VALOR:=UN_CONSECUTIVO;
          MI_REEMPLAZOS(1).CLAVE:='CODIGORUTA';
          MI_REEMPLAZOS(1).VALOR:=UN_CODIGORUTA;
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION;          
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                  UN_EXC_COD    => SQLCODE
                                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_FACTURACION_ACTUALMEDIDOR
                                 ,UN_TABLAERROR => MI_TABLA
                                 ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                 ); 
    END;
    END IF;
    RETURN -1;
  END FC_ACT_MEDIDOR;

--22
FUNCTION FC_AGREGARNUEVOCODIGORUTA
    /*
    NAME              : FC_AGREGARNUEVOCODIGORUTA
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRADOR     : 27/06/2017
    TIME              : 12:45
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Funcion de hacer copia de los datos de una solicitud dada
    MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
    DESCRIPTION 
    MODIFIED          : se agrega el parametro UN_USUARIO, se identa según estandar y se 
                        agregan campos de auditoría
    PARAMETERS        : UN_COMPANIA        => COMPAÑIA DE INGRESO AL SISTEMA
                        UN_NUMERO          => NUMERO SOLICITUD DE SERVICIO DEL REGISTRO A COPIAR
                        UN_CLASESOLICITUD  => CLASE SOLICITUD DE SERVICIO DEL REGISTRO A COPIAR
                        UN_NUMERONUEVO     => EL NUMERO PARA EL NUEVO REGISTRO
                        UN_CODIGORUTANUEVO => EL CODIGO PARA EL NUEVO REGISTRO
                        UN_USUARIO         => USUARIO QUE REALIZA EL PROCESO
    @NAME:  agregarNuevoCodigoRuta
    @METHOD:  GET 
    */
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO          IN SP_SOLICITUDSERVICIO.NUMERO%TYPE,
    UN_CLASESOLICITUD  IN SP_SOLICITUDSERVICIO.CLASESOLICITUD%TYPE,
    UN_NUMERONUEVO     IN SP_SOLICITUDSERVICIO.NUMERO%TYPE,
    UN_CODIGORUTANUEVO IN SP_SOLICITUDSERVICIO.CODIGORUTA%TYPE,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
BEGIN
  -- Crea copia del registro con un consecutivo
  MI_CAMPOS  := 'COMPANIA ,CLASESOLICITUD ,NUMERO ,NOMBRE ,DIRTECNICA ,DIRGUIA ,TELEFONO 
        ,TIPODOCUMENTO ,NIT ,SUCURSAL ,OBSERVACIONES ,NECESITAVISITA ,VERIFICAOFICINA 
        ,CODIGOCATASTRAL ,NUMAPARTAMENTO ,NUMFAMAPART,NUMPERSONASFAM ,NUMPISOSVIVIENDA 
        ,TIPODEPREDIO ,USO ,ESTRATO ,AREACONSTRUIDA ,AREALIBRE ,AREATOTAL ,DIAMACUEDUCTO 
        ,DIAMALCANTARILLADO ,EXISTERED ,SERVICIOGRAVEDAD ,EXISTEACOMETIDA ,EXISTEALCANTARILLADO 
        ,ALCANTARILLADOGRAVE ,DISTANREDMEDIDOR ,LONGFRENTEPREDIO ,LONGFONDOPREDIO ,AREACONSTRUIDARED 
        ,AREAROTURACONCRETO ,AREAROTURAASFALTO ,AREAAFIRMACIONTIERRA ,DEPENDENCIA ,RECIBIDOPOR 
        , OPERADOR ,VALORTOTAL ,TIPOVIVIENDA ,PAIS ,DEPARTAMENTO ,CIUDAD ,ACTIVADO ,SOLICITANTE 
        ,DIAMETROMATRIZ ,PERNOCOBRO ,ESTRATOALUMBRADO ,DIGITOS ,INDAC ,INDALC ,INDAS ,NUMMEDIDOR 
        ,EXISTEREDALCANTARILLADO ,PRIMERAPELLIDO ,SEGUNDOAPELLIDO ,NOMBRES ,USUARIO ,ESTADOAPROBADO 
        ,SECTORHIDRAULICO ,CODIGORUTA ,SECTHIDRAULICO ,SUBSECTOR ,REDHIDRAULICA ,SECCION ,MANZANA 
        ,OBS_PRESUPUESTP ,CODIGODANE ,LADO ,SECTOR ,TIPONOTIFICACION ,CODIGO_RESPUESTA ,TIPORESPUESTA 
        ,SOLICITANTE_NIT ,SOLICITANTE_SUCURSAL ,SOLICITANTE_TEL ,SOLICITANTE_CEL ,SOLICITANTE_EMAIL 
        ,SOLICITANTE_DIR ,BARRIO ,PRESENTACION ,ANULADA ,USUARIO_ANUL ,CREATED_BY  
        ,FECHADEPENDENCIA ,HORASOLICITUD ,FECHASOLICITUD ,FECHAEXPEDICION ,FECHAVENCIMIENTO 
        ,FECHANOTIFICACION ,FECHA_ANUL ,DATE_CREATED';

  MI_VALORES := 'SELECT COMPANIA ,CLASESOLICITUD ,'||UN_NUMERONUEVO||' ,NOMBRE ,DIRTECNICA ,DIRGUIA ,TELEFONO 
        ,TIPODOCUMENTO ,NIT ,SUCURSAL ,OBSERVACIONES ,NECESITAVISITA ,VERIFICAOFICINA 
        ,CODIGOCATASTRAL ,NUMAPARTAMENTO ,NUMFAMAPART ,NUMPERSONASFAM ,NUMPISOSVIVIENDA 
        ,TIPODEPREDIO ,USO ,ESTRATO ,AREACONSTRUIDA ,AREALIBRE ,AREATOTAL ,DIAMACUEDUCTO 
        ,DIAMALCANTARILLADO ,EXISTERED ,SERVICIOGRAVEDAD ,EXISTEACOMETIDA ,EXISTEALCANTARILLADO 
        ,ALCANTARILLADOGRAVE ,DISTANREDMEDIDOR ,LONGFRENTEPREDIO ,LONGFONDOPREDIO ,AREACONSTRUIDARED 
        ,AREAROTURACONCRETO ,AREAROTURAASFALTO ,AREAAFIRMACIONTIERRA ,DEPENDENCIA ,RECIBIDOPOR 
        ,OPERADOR ,VALORTOTAL ,TIPOVIVIENDA ,PAIS ,DEPARTAMENTO ,CIUDAD ,ACTIVADO ,SOLICITANTE 
        ,DIAMETROMATRIZ ,PERNOCOBRO ,ESTRATOALUMBRADO ,DIGITOS ,INDAC ,INDALC ,INDAS ,NUMMEDIDOR 
        ,EXISTEREDALCANTARILLADO ,PRIMERAPELLIDO ,SEGUNDOAPELLIDO ,NOMBRES ,USUARIO ,ESTADOAPROBADO 
        ,SECTORHIDRAULICO , '''||UN_CODIGORUTANUEVO||''' ,SECTHIDRAULICO ,SUBSECTOR ,REDHIDRAULICA ,SECCION ,MANZANA 
        ,OBS_PRESUPUESTP ,CODIGODANE ,LADO ,SECTOR ,TIPONOTIFICACION ,CODIGO_RESPUESTA ,TIPORESPUESTA 
        ,SOLICITANTE_NIT ,SOLICITANTE_SUCURSAL ,SOLICITANTE_TEL ,SOLICITANTE_CEL ,SOLICITANTE_EMAIL 
        ,SOLICITANTE_DIR , BARRIO ,PRESENTACION ,ANULADA ,USUARIO_ANUL , '''||UN_USUARIO||''' 
        ,FECHADEPENDENCIA ,HORASOLICITUD ,FECHASOLICITUD ,FECHAEXPEDICION ,FECHAVENCIMIENTO 
        ,FECHANOTIFICACION ,FECHA_ANUL , SYSDATE
      FROM SP_SOLICITUDSERVICIO
      WHERE COMPANIA     = '''||UN_COMPANIA||'''
      AND NUMERO         = '||UN_NUMERO||'
      AND CLASESOLICITUD = '''||UN_CLASESOLICITUD||''' ';
  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES, 
                                             UN_TABLA => 'SP_SOLICITUDSERVICIO', 
                                             UN_ACCION => 'IS');
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
    END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, 
                                  UN_ERROR_COD=>PCK_ERRORES.ERRR_FACTURACION_COPIARSOLIC );
  END;

  -- Inserta los documentos
  MI_CAMPOS := 'SOLICITUDSERVICIO, 
                COMPANIA, 
                CLASESOLICITUD, 
                DOCUMENTO,
                DATE_CREATED,
                CREATED_BY';
  MI_VALORES := 'SELECT '||UN_NUMERONUEVO||' ,
                        COMPANIA,
                        CLASESOLICITUD,
                        DOCUMENTO,
                        SYSDATE,
                        '''||UN_USUARIO||'''
                  FROM SP_SOLICITUDDOCPRESENTADO
                  WHERE COMPANIA        = '''||UN_COMPANIA||'''
                  AND CLASESOLICITUD    = '''||UN_CLASESOLICITUD||'''
                  AND SOLICITUDSERVICIO = '||UN_NUMERO ||' ';
  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES, 
                                             UN_TABLA   => 'SP_SOLICITUDDOCPRESENTADO', 
                                             UN_ACCION  => 'IS'); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                 UN_ERROR_COD=>PCK_ERRORES.ERRR_FACTURACION_INSERTDOCS);          
  END;

  -- Inserta los elementos
  MI_CAMPOS := 'SOLICITUD,
                COMPANIA,
                CLASESOLICITUD,
                CODIGO,
                CANTIDAD,
                VALORUNITARIO,
                PORCIVA,
                DATE_CREATED,
                CREATED_BY
                ';
  MI_VALORES := 'SELECT '||UN_NUMERONUEVO||',
                  COMPANIA,
                  CLASESOLICITUD,
                  CODIGO,
                  CANTIDAD,
                  VALORUNITARIO,
                  PORCIVA,
                  SYSDATE,
                  '''||UN_USUARIO||'''
                FROM SP_ELEMENTOSSOLICITUD
                WHERE COMPANIA     = '''||UN_COMPANIA||'''
                AND CLASESOLICITUD = '''||UN_CLASESOLICITUD||'''
                AND SOLICITUD      = '||UN_NUMERO||' ';
  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES, 
                                             UN_TABLA   => 'SP_ELEMENTOSSOLICITUD', 
                                             UN_ACCION  => 'IS'); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD =>SQLCODE,
                              UN_ERROR_COD=>PCK_ERRORES.ERRR_FACTURACION_INSERTELEM);          
  END;

  -- Inserta financiables
  MI_CAMPOS := 'SOLICITUD,
                COMPANIA,
                CLASESOLICITUD,
                CONCEPTO,
                VALOR,
                INICIAL,
                CUOTAS,
                ANOINICIAL,
                PERIODOINICIAL,
                DATE_CREATED,
                CREATED_BY';
  MI_VALORES := 'SELECT '||UN_NUMERONUEVO||',
                  COMPANIA,
                  CLASESOLICITUD,
                  CONCEPTO,
                  VALOR,
                  INICIAL,
                  CUOTAS,
                  ANOINICIAL,
                  PERIODOINICIAL,
                  SYSDATE,
                  '''||UN_USUARIO||'''
                FROM SP_SOLICITUDFINANCIABLES
                WHERE COMPANIA     = '''||UN_COMPANIA||'''
                AND CLASESOLICITUD = '''||UN_CLASESOLICITUD||'''
                AND SOLICITUD      = '||UN_NUMERO||' ';
  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES, 
                                             UN_TABLA   => 'SP_SOLICITUDFINANCIABLES', 
                                             UN_ACCION  => 'IS'); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD =>SQLCODE,
                              UN_ERROR_COD=>PCK_ERRORES.ERRR_FACTURACION_INSERTFINAN);          
  END;  
  RETURN -1;
END FC_AGREGARNUEVOCODIGORUTA;

--23
FUNCTION FC_ACTSUBTOTELEMENTOS
    /*
    NAME              : FC_ACTSUBTOTELEMENTOS
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRADOR     : 27/06/2017
    TIME              : 12:45
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Funcion de actualizar los totales de los elementos de la solicitud
    PARAMETERS        : UN_COMPANIA       => COMPAÑÍA DE INGRESO A LA APLICACIÓN
                        UN_MODULO         => CÓDIGO DEL MÓDULO
                        UN_USUARIO        => USUARIO QUE ACCEDE AL SISTEMA
                        UN_CLASESOLICITUD => NUMERO CLASE SOLICITUD A ACTUALIZAR
                        UN_SOLICITUD      => CODIGO SOLICITUD A ACTUALIZAR
    @NAME:  actualizarSubTotalElementos
    @METHOD:  GET 
    */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CLASESOLICITUD IN SP_SOLICITUDSERVICIO.CLASESOLICITUD%TYPE,
    UN_SOLICITUD      IN SP_SOLICITUDSERVICIO.NUMERO%TYPE )
  RETURN VARCHAR2
AS
    MI_VALORTOTAL     NUMBER := 0;
    MI_TXTVALORTOTAL  PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RTA            NUMBER := 0;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
BEGIN
  -- ACTUALIZA EL VALOR DEL CONCEPTO 7 SI NO EXISTE LO CREA
  SELECT  NVL(SUM((CANTIDAD*VALORUNITARIO)+((CANTIDAD*VALORUNITARIO)*(NVL(PORCIVA,0)/100))),0) VALORTOTAL
  INTO    MI_VALORTOTAL
  FROM    SP_ELEMENTOSSOLICITUD
  WHERE   COMPANIA        =UN_COMPANIA
  AND     CLASESOLICITUD  =UN_CLASESOLICITUD
  AND     SOLICITUD       =UN_SOLICITUD;

  SELECT  COUNT(*)
  INTO    MI_RTA
  FROM    SP_SOLICITUDFINANCIABLES
  WHERE   COMPANIA        =UN_COMPANIA
  AND     CLASESOLICITUD  =UN_CLASESOLICITUD
  AND     SOLICITUD       =UN_SOLICITUD
  AND     CONCEPTO        =7;

  IF MI_RTA         > 0 THEN
    MI_CAMPOS     :=    'VALOR= '         || MI_VALORTOTAL 
                    || ',INICIAL= '       || MI_VALORTOTAL 
                    || ',CUOTAS=    1'
                    || ',MODIFIED_BY= ''' || UN_USUARIO
                    || ''' ,DATE_MODIFIED = SYSDATE';
    MI_CONDICION  :='COMPANIA= '''                || UN_COMPANIA 
                    || ''' AND CLASESOLICITUD= '  || UN_CLASESOLICITUD 
                    || ' AND SOLICITUD= '         || UN_SOLICITUD 
                    || ' AND CONCEPTO=7';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   =>  'SP_SOLICITUDFINANCIABLES',
                                         UN_ACCION  =>  'M',
                                         UN_CAMPOS  =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_FACTURACION_ACTCONCEPTO7);
    END;
  ELSE
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                 'COBRAR FINANCIABLE DE RECONEXION EN LA MATRICULA', 
                                 UN_MODULO, 
                                 SYSDATE), 
                                 'NO') = 'SI' THEN
      MI_CAMPOS  := 'COMPANIA,
                    CLASESOLICITUD,
                    SOLICITUD,
                    CONCEPTO,
                    VALOR,
                    INICIAL,
                    CUOTAS,
                    CREATED_BY,
                    DATE_CREATED';
      MI_VALORES := ' '''||UN_COMPANIA||''' ,
                    ''' || UN_CLASESOLICITUD || ''',
                    ' || UN_SOLICITUD || ',
                    7,
                    ' || MI_VALORTOTAL || ',
                    0,
                    1, 
                    '''|| UN_USUARIO ||'''
                    , SYSDATE';
      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES, 
                                                 UN_TABLA   => 'SP_SOLICITUDFINANCIABLES', 
                                                 UN_ACCION  => 'I'); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION; 
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERRR_FACTURACION_INSCONCEPTO7);
      END;
    END IF;
  END IF;

  SELECT TO_CHAR(NVL(SUM((CANTIDAD*VALORUNITARIO)
            +((CANTIDAD*VALORUNITARIO)
            *(NVL(PORCIVA,0)/100))),0), '99,999,999,999,999,990.90') 
            VALORTOTAL
  INTO   MI_TXTVALORTOTAL
  FROM   SP_ELEMENTOSSOLICITUD
  WHERE  COMPANIA       = UN_COMPANIA
  AND    CLASESOLICITUD = UN_CLASESOLICITUD
  AND    SOLICITUD      = UN_SOLICITUD;
  RETURN MI_TXTVALORTOTAL;
END FC_ACTSUBTOTELEMENTOS;

--24
FUNCTION FC_ACTUALIZAR_MEDIDOR_ESTADO
/*
    NAME              : FC_ACTUALIZAR_MEDIDOR_ESTADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 05/07/2017
    TIME              : 02:00 PM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : ACTUALIZA O INSERTA EL MEDIDOR PARA LOS USUARIOS QUE NO POSEEN MEDIDOR 
    PARAMETERS        : 
        UN_COMPANIA     => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
        UN_ESTADO       => PARAMETRO QUE DETERMINA EL ESTADO AL CUAL SE VA A ACTUALIZAR EL MEDIDOR
        UN_MARCA        => PARAMETRO QUE DETERMINA LA MARCA A LA CUAL SE VA A ACTUALIZAR EL MEDIDOR
        UN_CODIGO       => PARAMETRO QUE INDICA EL CODIGO EL MEDIDOR
        UN_CLASE        => PARAMETRO QUE INDICA LA CLASE DEL MEDIDOR
        UN_LOCALIZACION => PARAMETRO QUE INDICA LA LOCALIZACION DEL MEDIDOR
        UN_CICLO        => PARAMETRO QUE INDICA EL CICLO AL CUAL PERTENECE EL MEDIDOR 
        UN_CODIGORUTA   => PARAMETRO QUE INDICA EL CODIGORUTA DEL MEDIDOR 
        UN_DIGITOS      => PARAMETRO QUE INDICA LOS DIGITOS DEL MEDIDOR 
        UN_USUARIO      => PARAMETRO QUE INDICA EL USUARIO QUE ESTA REALIZANDO LA OPERACION

    @NAME:    actualizarMedidorEstado 
    @METHOD:  GET
*/
(
  UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_ESTADO          IN  SP_MEDIDOR.ESTADO%TYPE,
  UN_MARCA           IN  SP_MEDIDOR.MARCA%TYPE,
  UN_CODIGO          IN  SP_MEDIDOR.CODIGO%TYPE,
  UN_USUARIO         IN  SP_MEDIDOR.MODIFIED_BY%TYPE,
  UN_CLASE           IN  SP_MEDIDOR.CLASE%TYPE,
  UN_LOCALIZACION    IN  SP_MEDIDOR.LOCALIZACION%TYPE,
  UN_CICLO           IN  SP_MEDIDOR.CICLO%TYPE,
  UN_CODIGORUTA      IN  SP_MEDIDOR.CODIGORUTA%TYPE,
  UN_DIGITOS         IN  SP_MEDIDOR.DIGITOS%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_CONSECUTIVO        SP_MEDIDOR.CONSECUTIVO%TYPE;
  MI_CODIGO             SP_MEDIDOR.CODIGO%TYPE;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN

  SELECT  MAX(CONSECUTIVO) 
  INTO MI_CONSECUTIVO   
  FROM SP_MEDIDOR   
  WHERE COMPANIA =UN_COMPANIA;

  IF MI_CONSECUTIVO IS NOT NULL THEN
    MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
  ELSE
    MI_CONSECUTIVO:=1;
  END IF;

 BEGIN
   SELECT DISTINCT CODIGO
   INTO   MI_CODIGO
   FROM SP_MEDIDOR  
   WHERE COMPANIA    = UN_COMPANIA 
    AND  CONSECUTIVO = UN_CODIGO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_CODIGO:=NULL;
  END;

  MI_TABLA := 'SP_MEDIDOR';
  IF MI_CODIGO IS NOT NULL THEN
    MI_CODIGO := UN_CODIGO;
    MI_CAMPOS := 'ESTADO        = '''|| UN_ESTADO     ||''',
                  MARCA         = '''|| UN_MARCA      ||''',
                  CODIGORUTA    = '''|| UN_CODIGORUTA ||''',
                  CICLO         = '  || UN_CICLO      ||',
                  MODIFIED_BY   = '''|| UN_USUARIO    ||''',
                  DATE_MODIFIED = SYSDATE';                 
    MI_CONDICION := 'CONSECUTIVO    = '   || UN_CODIGO    ||'
                    AND COMPANIA    = ''' || UN_COMPANIA  || '''';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_CONDICION);                 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_FACTURACION_ACTESTADOMED
            );
      END;
  ELSE 
    MI_CODIGO:= MI_CONSECUTIVO;
    MI_CAMPOS := 'COMPANIA,
                  CLASE,
                  LOCALIZACION,
                  CODIGO,
                  MARCA,
                  ESTADO,
                  CICLO,
                  CODIGORUTA,
                  CONSECUTIVO,
                  DIGITOS,
                  CREATED_BY,
                  DATE_CREATED';

    MI_VALORES :=''''  ||   UN_COMPANIA     ||''',
                    '  ||   UN_CLASE        ||',
                    '  ||   UN_LOCALIZACION ||',
                    '''||   UN_CODIGO       ||''',
                    '''||   UN_MARCA        ||''',
                    '''||   UN_ESTADO       ||''',
                    '  ||   UN_CICLO        ||',
                    '''||   UN_CODIGORUTA   ||''',
                    '  ||   MI_CONSECUTIVO  ||',
                    '  ||   UN_DIGITOS      ||',
                    '''||   UN_USUARIO      ||''',
                    SYSDATE';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                UN_ACCION   =>  'I', 
                                                UN_CAMPOS   =>  MI_CAMPOS, 
                                                UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERRR_FACTURACION_INSESTADOMED
                        );
    END;
  END IF;

  MI_TABLA := 'SP_USUARIO';
  MI_CAMPOS := ' MEDIDOR       = '|| MI_CODIGO   ||',
                 MODIFIED_BY   = '''|| UN_USUARIO  ||''',
                 DATE_MODIFIED = SYSDATE';                 
  MI_CONDICION := ' COMPANIA       = '''|| UN_COMPANIA   ||'''
                    AND CICLO      = '  || UN_CICLO      ||'
                    AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''';
  BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_CONDICION);                 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_SP_ACTMEDIDORUSUARIO
            );
      END;

  RETURN 1;
END FC_ACTUALIZAR_MEDIDOR_ESTADO;

FUNCTION FC_ANOPERACTUAL
 /*
   NAME              : FC_ANOPERACTUAL --> En Access  AnoPerActual
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
   DATE MIGRADOR     : 25/07/2017
   TIME              : 04:05 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Trae el año y periodo actual del ciclo seleccionado 
   PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_CICLO           => CICLO A CONSULTAR

   MODIFICATIONS     :

   @NAME:    obtenerAnioPeriodoActual
   @METHOD:  GET
 */
(
     UN_COMPANIA IN  PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO    IN  PCK_SUBTIPOS.TI_CICLO
) RETURN VARCHAR2 
AS
    MI_ANO          PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO      SP_CICLO.PERIODO%TYPE;
    MI_ANOPERACTUAL VARCHAR2(300 CHAR);
BEGIN 
    BEGIN 
	    --DEVUELVE AÑO Y PERIODO UNIDOS POR COMA
        SELECT  ANO
               ,PERIODO 
          INTO  MI_ANO
               ,MI_PERIODO 
          FROM SP_CICLO 
         WHERE COMPANIA = UN_COMPANIA
           AND NUMERO   = UN_CICLO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_ANO:= NULL;
        MI_PERIODO:=NULL;
    END;
    IF MI_ANO IS NULL THEN 
    	  MI_ANOPERACTUAL:='0';
    ELSE
        MI_ANOPERACTUAL:= MI_ANO||','||MI_PERIODO;
    END IF;
    RETURN MI_ANOPERACTUAL;
END FC_ANOPERACTUAL; 

END PCK_SERVICIOS_PUBLICOS_COM8;