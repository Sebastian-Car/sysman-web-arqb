create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM2 AS


FUNCTION FC_REGISTRARPERSUASIVO
  (
   /*
    NAME              : FC_REGISTRARPERSUASIVO --> EN ACCESS RegistrarPersuasivo
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 29/08/2016
    TIME              : 12:10 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS, JUAN CARLOS RODRIGUEZ AMEZQUITA
    DATE MODIFIED     : 30/05/2017
    TIME              : 08:22 AM
    MODIFICATIONS     : Envío de campos de auditoria en la inserción y actualización.
    DESCRIPTION       : Función que retorna -1(Verdadero) o 0(Falso), realiza un insert a la tabla SP_COBROSPERSUASIVOS
    PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                        UN_NIT	  		      => Nit de tercero.
                        UN_CONSECUTIVO	    => Número que identifica el valor con el que se registrará el campo IDCOBRO
                                               en la tabla SP_COBROSPERSUASIVOS.
                        UN_CICLO            => Ciclo al que pertenece el cobro.
                        UN_CODIGOINICIAL    => Código de Ruta Inicial seleccionado.
                        UN_CODIGOFINAL      => Código de Ruta Final seleccionado.
                        UN_PERATRASOINI    	=> Periodo atrasado inicial
                        UN_PERATRASOFIN     => Periodo atrasado final
                        UN_DEUDAINI			    => Valor de la deuda inicial
                        UN_DEUDAFIN			    => Valor de la deuda final
                        UN_USUARIO          => Código del usuario identificado en la aplicación.
    @NAME:  registrarPersuasivo
    @METHOD:  POST                    
   */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NIT              IN TERCERO.NIT%TYPE,
    UN_CONSECUTIVO      IN SP_COBROSPERSUASIVOS.IDCOBRO%TYPE,
    UN_CICLO            IN VARCHAR2,
    UN_CODIGOINICIAL    IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CODIGOFINAL      IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_PERATRASOINI     IN PCK_SUBTIPOS.TI_PERIODO,
    UN_PERATRASOFIN     IN VARCHAR2,
    UN_DEDUDAINI        IN PCK_SUBTIPOS.TI_DOBLE,
    UN_DEUDAFIN         IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2
AS 
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_IDCOBRO              SP_COBROSPERSUASIVOS.IDCOBRO%TYPE;
    MI_CODIGOCOBROP         SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_CODIGOCOBROE         SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_CODIGOCOBRON         SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_SSQL                 PCK_SUBTIPOS.TI_STRSQL;
    MI_CONSECUTIVO          SP_COBROSPERSUASIVOS.IDCOBRO%TYPE;
    MI_STRINICIAL           VARCHAR2(15 CHAR);
    MI_STRFINAL             VARCHAR2(15 CHAR);
    MI_REGISTRARPERSUASIVO  PCK_SUBTIPOS.TI_CONDICION;
    MI_STRACTESTADO         PCK_SUBTIPOS.TI_LOGICO;
    MI_STRCODPERSUASIVO     SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_STRCODCOACTIVO       SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_STRCODDEFAULT        SP_ESTADOSCOBRO.CODIGO%TYPE;
    MI_CICLO                PCK_SUBTIPOS.TI_CONDICION;
    MI_ESTADO               VARCHAR2(100 CHAR);
    MI_RS                   SYS_REFCURSOR;
    MI_CICLOI               PCK_SUBTIPOS.TI_CICLO;
    MI_CODIGORUTA           PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_TIPOCOBRO            SP_USUARIO.TIPOCOBRO%TYPE;
    MI_ANO                  PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO              PCK_SUBTIPOS.TI_PERIODO;
    MI_PERIODOSATRASO       SP_USUARIO.PERIODOSATRASO%TYPE;
    MI_TOTFACTURAPERACTUAL  SP_USUARIO.TOTFACTURAPERACTUAL%TYPE;
    MI_LNGCONMAN            PCK_SUBTIPOS.TI_ENTERO;
    MI_RTAC                 PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN 
    MI_REGISTRARPERSUASIVO := -1;
    BEGIN
        SELECT MAX(IDCOBRO) KEEP (DENSE_RANK LAST ORDER BY IDCOBRO) IDCOBRO 
          INTO MI_IDCOBRO
          FROM SP_COBROSPERSUASIVOS 
         WHERE COMPANIA = UN_COMPANIA
         ORDER BY IDCOBRO DESC;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_IDCOBRO:=1; 
    END;  
    IF MI_IDCOBRO IS NULL THEN 
        MI_CONSECUTIVO := 1;
    ELSE 
        MI_CONSECUTIVO := MI_IDCOBRO + 1;
    END IF;
    IF UN_CONSECUTIVO IS NOT NULL THEN 
        IF TO_NUMBER(UN_CONSECUTIVO) > TO_NUMBER(MI_CONSECUTIVO) THEN 
            MI_STRINICIAL := UN_CONSECUTIVO;
            MI_CONSECUTIVO := UN_CONSECUTIVO;
        ELSE 
            DECLARE 
                MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
            BEGIN 
              MI_REEMPLAZOS(0).CLAVE:='CONSECUTIVO';
              MI_REEMPLAZOS(0).VALOR:=MI_CONSECUTIVO;
            	RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            	PCK_ERR_MSG.RAISE_WITH_MSG(
	                        UN_EXC_COD    => SQLCODE
	                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGPERSUASIV2
	                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
	                       );
            END;
        END IF;
    ELSE 
        MI_STRINICIAL := MI_CONSECUTIVO;
    END IF;

    MI_STRACTESTADO := PCK_SERVICIOS_PUBLICOS_COM1.FC_AUTORIZACION_PERSUASIVO(
                                                   UN_COMPANIA => UN_COMPANIA
                                                  ,UN_NIT      => UN_NIT);

    IF MI_STRACTESTADO NOT IN (0) THEN 
        BEGIN 
           SELECT SP_ESTADOSCOBRO.CODIGO 
             INTO MI_CODIGOCOBROP 
             FROM SP_ESTADOSCOBRO 
            WHERE SP_ESTADOSCOBRO.COMPANIA = UN_COMPANIA
              AND SP_ESTADOSCOBRO.PERTENECE_A = 'P';
        EXCEPTION WHEN NO_DATA_FOUND THEN 
             MI_CODIGOCOBROP:=NULL;
        END;
        IF MI_CODIGOCOBROP IS NOT NULL THEN 
            MI_STRCODPERSUASIVO := MI_CODIGOCOBROP;
        END IF;
        BEGIN 
            SELECT SP_ESTADOSCOBRO.CODIGO 
              INTO MI_CODIGOCOBROE
              FROM SP_ESTADOSCOBRO 
             WHERE SP_ESTADOSCOBRO.COMPANIA    = UN_COMPANIA
               AND SP_ESTADOSCOBRO.PERTENECE_A = 'E';
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CODIGOCOBROE:=NULL;
        END;  
        IF MI_CODIGOCOBROE IS NOT NULL THEN 
            MI_STRCODCOACTIVO := MI_CODIGOCOBROE;
        END IF;     
        BEGIN 
            SELECT SP_ESTADOSCOBRO.CODIGO 
              INTO MI_CODIGOCOBRON 
              FROM SP_ESTADOSCOBRO 
             WHERE SP_ESTADOSCOBRO.COMPANIA    = UN_COMPANIA
               AND SP_ESTADOSCOBRO.PERTENECE_A = 'N';
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CODIGOCOBRON:=NULL;
        END; 
        IF MI_CODIGOCOBRON IS NOT NULL THEN 
            MI_STRCODDEFAULT := MI_CODIGOCOBRON;
        END IF;   
    END IF;  
    IF UN_CICLO <> 'TODOS' THEN 
        MI_CICLO := 'AND SP_USUARIO.CICLO = '''||UN_CICLO||'''';
    ELSE 
        MI_CICLO := '';
    END IF;  
    IF MI_STRACTESTADO NOT IN (0) THEN 
        MI_ESTADO := 'AND NVL(SP_USUARIO.TIPOCOBRO,'''||MI_STRCODDEFAULT||''')  NOT IN ('''||MI_STRCODPERSUASIVO||''', '''||MI_STRCODCOACTIVO||''')';
    ELSE 
        MI_ESTADO := '';
    END IF;  
    MI_SSQL :='SELECT  SP_USUARIO.CICLO
                      ,SP_USUARIO.CODIGORUTA
                      ,SP_USUARIO.TIPOCOBRO
                      ,SP_USUARIO.ANO
                      ,SP_USUARIO.PERIODO
                      ,SP_USUARIO.PERIODOSATRASO
                      ,SP_USUARIO.TOTFACTURAPERACTUAL 
                 FROM SP_USUARIO 
                WHERE SP_USUARIO.COMPANIA             = '''||UN_COMPANIA||''' 
                  '||MI_CICLO||'
                  AND SP_USUARIO.CODIGORUTA           BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                  AND SP_USUARIO.PERIODOSATRASO       BETWEEN '||UN_PERATRASOINI||'      AND '||UN_PERATRASOFIN||'
                  AND SP_USUARIO.TOTFACTURAPERACTUAL  BETWEEN '||UN_DEDUDAINI||'         AND '||UN_DEUDAFIN||'
                  AND SP_USUARIO.BANCOPERPROCESO      IS NULL 
                '||MI_ESTADO||'';
    <<USUARIO>>
    OPEN MI_RS FOR MI_SSQL; 
    LOOP
        FETCH MI_RS INTO  MI_CICLOI,
	                      MI_CODIGORUTA,
	                      MI_TIPOCOBRO,
	                      MI_ANO,
	                      MI_PERIODO,
	                      MI_PERIODOSATRASO,
	                      MI_TOTFACTURAPERACTUAL;
        EXIT WHEN MI_RS%NOTFOUND;
        MI_LNGCONMAN := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                       UN_TABLA    => 'SP_COBROSPERSUASIVOS'
                                      ,UN_CRITERIO => ' SP_COBROSPERSUASIVOS.COMPANIA       = '''||UN_COMPANIA||''' 
                                                        AND SP_COBROSPERSUASIVOS.CODIGORUTA = '''||MI_CODIGORUTA||''' '
                                      ,UN_CAMPO    => 'CONSECUTIVOUSUARIO');
        BEGIN
            BEGIN
                MI_CAMPOS  := 'COMPANIA
                              ,IDCOBRO
                              ,CICLO
                              ,CODIGORUTA
                              ,FECHAACTA
                              ,ANO
                              ,PERIODO
                              ,PERIODOSATRASO
                              ,ESTADO
                              ,CONSECUTIVOUSUARIO
                              ,NOTIFICADO
                              ,DEUDA
                              ,DATE_CREATED
                              ,CREATED_BY';    
                MI_VALORES := '''' || UN_COMPANIA ||''', 
                          LPAD(''' || MI_CONSECUTIVO || ''',10,''0''),
                               ''' || MI_CICLOI ||''',
                               ''' || MI_CODIGORUTA ||''',SYSDATE ,
                                 ' || MI_ANO ||',
                               ''' || MI_PERIODO ||''', 
                               ''' || MI_PERIODOSATRASO || ''', ''A'', 
                               ''' || MI_LNGCONMAN ||''', 0, 
                                  '|| MI_TOTFACTURAPERACTUAL || ', SYSDATE, '''
                                  || UN_USUARIO || '''';
                MI_TABLA:='SP_COBROSPERSUASIVOS';
                MI_RTAC := PCK_DATOS.FC_ACME(
                                     UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I'
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGPERSUASIVO
                       ,UN_TABLAERROR => MI_TABLA
                       );
        END;

        IF MI_STRACTESTADO NOT IN (0) THEN 
            BEGIN
                BEGIN 
                    MI_CAMPOS := 'SP_USUARIO.TIPOCOBRO =  '''||MI_STRCODPERSUASIVO||''' ';
                    MI_CONDICION := '    SP_USUARIO.COMPANIA   = ''' || UN_COMPANIA || ''' 
                                     AND SP_USUARIO.CICLO      = ''' || MI_CICLOI || ''' 
                                     AND SP_USUARIO.CODIGORUTA = ''' || MI_CODIGORUTA || ''' ' ||
                                     ' AND DATE_MODIFIED = SYSDATE ' ||
                                     ' AND MODIFIED_BY = ''' || UN_USUARIO || ''' ';
                    MI_TABLA:='SP_USUARIO';
                    MI_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);   
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACT_USUREGPERS
                           ,UN_TABLAERROR => MI_TABLA);
            END;
        END IF;
        MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
    END LOOP USUARIO;
    CLOSE MI_RS;
    MI_STRFINAL := MI_CONSECUTIVO;
    IF MI_STRFINAL IS NOT NULL AND MI_STRINICIAL IS NOT NULL THEN 
        MI_REGISTRARPERSUASIVO := LPAD(MI_STRINICIAL,10,'0')||','||LPAD(MI_STRFINAL,10,'0');
    ELSE 
        MI_REGISTRARPERSUASIVO := NULL;
    END IF;  
    RETURN MI_REGISTRARPERSUASIVO;     
END  FC_REGISTRARPERSUASIVO; 


  FUNCTION FC_REGISTRARCOACTIVO
  (
   /*
    NAME              : FC_REGISTRARCOACTIVO --> EN ACCESS RegistrarCoactivo
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 29/08/2016
    TIME              : 17:04 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          : AURA LILIANA MONROY GARCÍA
    DATE MODIFIED     : 18/05/2017
    TIME              : 12:04 PM
    DESCRIPTION       : Función que retorna -1(Verdadero) o 0(Falso), realiza un insert a la tabla SP_COBROSJURIDICOS
    MODIFICATIONS     : Se adiciona la excepción cuando el valor del consecutivo ingresado por parámetro es menor al 
                        consecutivo actual de las actas de cobro jurídico. Se adiciona el llamado ala función FC_GENCONSECUTIVO
                        para obtener el valor del consecutivo de la tabla SP_COBROSJURIDICOS
    PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                        UN_CONSECUTIVO	    => Número que identifica el valor con el que se registrará el campo IDCOBRO
                                               en la tabla SP_COBROSJURIDICOS.
                        UN_CICLO            => Ciclo al que pertenece el cobro.
                        UN_CODIGOINICIAL    => Código de Ruta Inicial seleccionado.
                        UN_CODIGOFINAL      => Código de Ruta Final seleccionado.
                        UN_PERATRASOINI    	=> Periodo atrasado inicial
                        UN_PERATRASOFIN     => Periodo atrasado final
                        UN_DEUDAINI			    => Valor de la deuda inicial
                        UN_DEUDAFIN			    => Valor de la deuda final
    @NAME:  registrarCoactivo
    @METHOD:  GET   

    */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONSECUTIVO    IN VARCHAR2,
    UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGOINICIAL  IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CODIGOFINAL    IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_PERATRASOINI   IN VARCHAR2,
    UN_PERATRASOFIN   IN VARCHAR2,
    UN_DEUDAINI       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_DEUDAFIN       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2
    AS
      MI_REGISTROCOACTIVO   VARCHAR2(31 CHAR);
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONSECUTIVO        SP_COBROSJURIDICOS.IDCOBRO%TYPE;
      MI_SSQL               VARCHAR(32000 CHAR);
      MI_IDCOBRO            SP_COBROSJURIDICOS.IDCOBRO%TYPE;
      MI_STRINICIAL         VARCHAR2(15 CHAR);
      MI_STRFINAL           VARCHAR2(15 CHAR);
      MI_CICLO              PCK_SUBTIPOS.TI_CICLO;
      MI_CODIGORUTA         PCK_SUBTIPOS.TI_CODIGORUTA;
      MI_RS                 SYS_REFCURSOR;
      MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;

    BEGIN
      MI_REGISTROCOACTIVO := -1;
      MI_CONSECUTIVO      := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_COBROSJURIDICOS',
                                                              UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||'''',
                                                              UN_CAMPO    => 'IDCOBRO',
                                                              UN_INICIAL  => '1');

      BEGIN
        IF UN_CONSECUTIVO <> ' ' 
        THEN 
          IF TO_NUMBER(UN_CONSECUTIVO) > TO_NUMBER(MI_CONSECUTIVO)
          THEN 
            MI_STRINICIAL  := UN_CONSECUTIVO;
            MI_CONSECUTIVO := UN_CONSECUTIVO;
          ELSE 
            MI_REGISTROCOACTIVO := 1; --El consecutivo debe iniciar en un número mayor que la última acta.                 
          END IF;
          IF MI_REGISTROCOACTIVO IN(1) THEN
             RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END IF;  
        ELSE 
          MI_STRINICIAL := MI_CONSECUTIVO;
        END IF;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGCOACTIVOCONS
        );  
      END;        

      MI_SSQL := 'SELECT  SP_USUARIO.CICLO, 
                          SP_USUARIO.CODIGORUTA 
                    FROM SP_USUARIO 
                   WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||''' 
                     AND SP_USUARIO.CICLO ='''||UN_CICLO||'''
                     AND SP_USUARIO.CODIGORUTA BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                     AND SP_USUARIO.PERIODOSATRASO BETWEEN '||UN_PERATRASOINI||' AND '||UN_PERATRASOFIN||'
                     AND SP_USUARIO.TOTFACTURAPERACTUAL BETWEEN '||UN_DEUDAINI||' AND '||UN_DEUDAFIN||'
                     AND SP_USUARIO.BANCOPERPROCESO IS NULL';
      <<USUARIOS>>
      OPEN MI_RS FOR MI_SSQL;
        MI_CONSECUTIVO := MI_CONSECUTIVO - 1;
        LOOP 
          FETCH MI_RS INTO  MI_CICLO, 
                            MI_CODIGORUTA;
          EXIT WHEN MI_RS%NOTFOUND;
          BEGIN 
            BEGIN
              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

              MI_CAMPOS := 'COMPANIA
                            , IDCOBRO
                            , CICLO
                            , CODIGORUTA
                            , FECHAACTA
                            , CREATED_BY
                            , DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA ||'''
                            , LPAD('''||MI_CONSECUTIVO||''',10,''0'')
                            , '''|| MI_CICLO || '''
                            , ''' || MI_CODIGORUTA || '''
                            , SYSDATE
                            , ''' || UN_USUARIO ||'''
                            , SYSDATE';

              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_COBROSJURIDICOS', 
                                          UN_ACCION    => 'I', 
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END; 
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
           PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGPERSUASIVO
             );
          END;
        END LOOP USUARIOS;
      CLOSE MI_RS;
      MI_STRFINAL := MI_CONSECUTIVO;

        IF MI_RTA IS NOT NULL 
        THEN 
          MI_REGISTROCOACTIVO := LPAD(MI_STRINICIAL,10,'0') || '-' || LPAD(MI_STRFINAL,10,'0');
        ELSE 
          MI_REGISTROCOACTIVO := '-';
        END IF;

      RETURN MI_REGISTROCOACTIVO;

  END FC_REGISTRARCOACTIVO;		 

FUNCTION FC_REGISTRARACTA 
(
 /*
  NAME              : FC_REGISTRARACTA --> EN ACCESS RegistrarActa
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
  DATE MIGRADOR     : 30/08/2016
  TIME              : 17:04 PM
  SOURCE MODULE     : SERVICIOS PUBLICOS
  DESCRIPTION       : Función que retorna -1(Verdadero) o 0(Falso), realiza un insert a la tabla SP_ACTASDESUSPENSION
  MODIFIER          : PABLO ANDRES ESPITIA CUCA
  DATE MODIFIED     : 08/03/2017
  TIME              : 11:12 AM
  DESCRIPCION MODIF.: Manejo de excepciones y depuracion.
  PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                      UN_CONSECUTIVO	    => Número que identifica el valor con el que se registrará el campo IDACTA
                                             en la tabla SP_ACTASDESUSPENSION.
                      UN_CICLO            => Ciclo al que pertenece el acta.
                      UN_CODIGOINICIAL    => Código de Ruta Inicial seleccionado.
                      UN_CODIGOFINAL      => Código de Ruta Final seleccionado.
                      UN_PERATRASOINI    	=> Periodo atrasado inicial
                      UN_PERATRASOFIN     => Periodo atrasado final
                      UN_DEUDAINI			    => Valor de la deuda inicial
                      UN_DEUDAFIN			    => Valor de la deuda final
                      UN_ESTADO 			    => Estado del motivo del acta
                      UN_CONABONOS		    => Indicador que identifica si se incluyen usuarios con abonos.
                      UN_ABONOS 			    => Número de abonos realizados
                      UN_CHAPETAS			    => Indicador que identifica si se incluyen chapetas
                      UN_PQR    			    => Indicador que identifica si se incluye pqr
                      UN_FECHAEMISION			=> Fecha de suspensión del acta
                      UN_SUPERINT			    => Indicador que identifica si se incluye superintendencia.
                      UN_USUARIO          => Codigo del usuario que esta utilizando el formulario.
  @NAME:  registrarActa
  @METHOD:  POST
 */

  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONSECUTIVO    IN VARCHAR2,
  UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO,
  UN_CODIGOINICIAL  IN PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_CODIGOFINAL    IN PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_PERATRASOINI   IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PERATRASOFIN   IN PCK_SUBTIPOS.TI_ENTERO,
  UN_DEUDAINI       IN PCK_SUBTIPOS.TI_DOBLE,
  UN_DEUDAFIN       IN PCK_SUBTIPOS.TI_DOBLE,
  UN_ESTADO         IN VARCHAR2,
  UN_CONABONOS      IN PCK_SUBTIPOS.TI_LOGICO,
  UN_ABONOS         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CHAPETAS       IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PQR            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHAEMISION   IN VARCHAR2,   
  UN_SUPERINT       IN VARCHAR2,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER 
AS 
  MI_PARFILTRODEUDA     PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PAREXCLUIRPQR      PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARMANEJA          PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARFILTRARCHAPETAS PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARGENERACION      PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARFECHACORTE      PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARPQRTRASLADADAS  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
  MI_SQL                PCK_SUBTIPOS.TI_STRSQL;
  MI_SQLSEL             PCK_SUBTIPOS.TI_STRSQL;
  MI_SQLWHE             PCK_SUBTIPOS.TI_STRSQL;
  MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CANT               PCK_SUBTIPOS.TI_ENTERO;
  MI_REGISTRAACTA       PCK_SUBTIPOS.TI_LOGICO;
  MI_TIENEPAGOS         PCK_SUBTIPOS.TI_LOGICO;
  MI_DEBEINSERTAR       PCK_SUBTIPOS.TI_LOGICO;
  MI_ACTA_ANT           PCK_SUBTIPOS.TI_LOGICO;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_RTA                PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;  
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;  
  MI_CONDICIONESTADO    PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICIONFECHA     PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICIONAUX       PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICIONCHAPETAS  PCK_SUBTIPOS.TI_CONDICION;
  MI_IDACTA             SP_ACTASDESUSPENSION.IDACTA%TYPE;
  MI_CODIGORUTA         SP_USUARIO.CODIGORUTA%TYPE;
  MI_CODIGOAUX          SP_USUARIO.CODIGORUTA%TYPE;
  MI_PERIODOSATRASO     SP_USUARIO.PERIODOSATRASO%TYPE;
  MI_ANO                SP_USUARIO.ANO%TYPE;
  MI_PERIODO            SP_USUARIO.PERIODO%TYPE;
  MI_BANCOPERPROCESO    SP_USUARIO.BANCOPERPROCESO%TYPE;
  MI_RS                 SYS_REFCURSOR;
BEGIN
  /*PARAMETRO QUE DETERMINA SI SE DEBE FILTRAR POR DEUDA*/
  MI_PARFILTRODEUDA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'ACTAS DE SUSPENSION SIN FACTURADO ACTUAL',
                                                 UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE),'NO');

  MI_PAREXCLUIRPQR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'EXCLUIR PQR Y FINANCIABLES DEUDA PAGAS',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE),'NO'); 

  MI_PARMANEJA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'MANEJA AMPLIA PLAZO DE SUSPENSION',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE),'NO');     

  MI_PARFILTRARCHAPETAS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'FILTRAR CHAPETAS ACTAS SUSPENSION',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE),'NO');                                           

  MI_PARGENERACION := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                               ,UN_NOMBRE    => 'GENERACION ACTAS DE SUSPENSION CON CHAPETA'
                                               ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                               ,UN_FECHA_PAR => SYSDATE),'NO');

  MI_PARFECHACORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA ,
                                            UN_NOMBRE    => 'FECHA CORTE DE PQR PARA SUI',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE);

  MI_PARPQRTRASLADADAS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'EXCLUIR PQR TRASLADADAS A SUPERINTENDENCIA'
                                                   ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                                   ,UN_FECHA_PAR => SYSDATE),'NO');

  SELECT MAX(IDACTA) 
  INTO MI_IDACTA  
  FROM SP_ACTASDESUSPENSION 
  WHERE SP_ACTASDESUSPENSION.COMPANIA = UN_COMPANIA;

  MI_CONSECUTIVO := TO_NUMBER(NVL(MI_IDACTA,0));

  --CONSECUTIVO AUTOMATICO                  
  IF UN_CONSECUTIVO IN('0') THEN
    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

  --CONSECUTIVO MANUAL  
  ELSE 
    IF TO_NUMBER(UN_CONSECUTIVO) <= MI_CONSECUTIVO THEN
      BEGIN
        MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
        MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;

        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(          
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REGACTACON,
              UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    ELSE 
      MI_CONSECUTIVO := TO_NUMBER(UN_CONSECUTIVO);
    END IF;
  END IF;

  MI_SQLSEL := ' SELECT  
                    SP_USUARIO.CODIGORUTA
                   ,SP_USUARIO.PERIODOSATRASO
                   ,SP_USUARIO.ANO
                   ,SP_USUARIO.PERIODO
                   ,SP_USUARIO.BANCOPERPROCESO ';

  --POSIBLES CONDICIONALES
  IF UN_ESTADO = 'T' THEN 
    MI_CONDICIONESTADO := ' ';
  ELSE 
    MI_CONDICIONESTADO := ' AND SP_USUARIO.ESTADO = '''||UN_ESTADO||'''';
  END IF;

  IF MI_PARMANEJA = 'SI' THEN 
    MI_CONDICIONFECHA := ' AND (   SP_USUARIO.FECHAACTASUSPEN IS NULL 
                                OR TRUNC(SP_USUARIO.FECHAACTASUSPEN) 
                                   < 
                                   TO_DATE('''||UN_FECHAEMISION||''',''DD/MM/YYYY'')) ';
  ELSE 
    MI_CONDICIONFECHA := ' ';
  END IF;

  IF MI_PARGENERACION IN('SI') THEN
    MI_CONDICIONCHAPETAS := ' AND SP_USUARIO.CHAPETAS IN(0) ';
  ELSE
    MI_CONDICIONCHAPETAS := ' ';
  END IF;

  IF MI_PAREXCLUIRPQR = 'SI' THEN
    MI_SQLWHE := ' WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                     AND SP_USUARIO.CICLO    = '||UN_CICLO||'
                     AND SP_USUARIO.CODIGORUTA     BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                     AND SP_USUARIO.PERIODOSATRASO BETWEEN '||UN_PERATRASOINI||' AND '||UN_PERATRASOFIN||'
                     AND (CASE WHEN '''||MI_PARFILTRODEUDA||''' = ''SI'' 
                               THEN SP_USUARIO.TOTALDEUDA 
                               ELSE SP_USUARIO.TOTFACTURAPERACTUAL 
                          END
                         )                         BETWEEN '||UN_DEUDAINI||' AND '||UN_DEUDAFIN||'
                     AND SP_USUARIO.BANCOPERPROCESO IS NULL 
                     '||MI_CONDICIONESTADO||'
                     '||MI_CONDICIONFECHA||' ';

    IF MI_PARFILTRARCHAPETAS = 'SI' THEN
      MI_CONDICIONAUX := CASE UN_CHAPETAS WHEN -1 THEN ' AND SP_USUARIO.CHAPETAS NOT IN(0) ' 
                                          WHEN 0  THEN ' AND SP_USUARIO.CHAPETAS IN(0) '
                                          WHEN 3  THEN ' '
                         END;

      MI_SQLWHE := MI_SQLWHE || MI_CONDICIONAUX;

      MI_CONDICIONAUX := CASE UN_PQR WHEN -1 THEN ' AND SP_USUARIO.EXCLUIRCARTERA IN(0) ' 
                                     WHEN 0  THEN ' AND SP_USUARIO.EXCLUIRCARTERA NOT IN(0) '
                                     WHEN 3  THEN ' '
                         END;

      MI_SQLWHE := MI_SQLWHE || MI_CONDICIONAUX;

      MI_SQLWHE := MI_SQLWHE || ' AND SP_USUARIO.PERIODOSATRASO BETWEEN '||UN_PERATRASOINI||' AND '||UN_PERATRASOFIN||' 
                                  AND SP_USUARIO.TOTFACTURAPERACTUAL >= 0';      

      IF UN_ABONOS IN(3) THEN 
        MI_SQLWHE := MI_SQLWHE || ' AND SP_USUARIO.NOTACREDITO IN(0) ' ;
      END IF;

    ELSE
      MI_SQLWHE := MI_SQLWHE || MI_CONDICIONCHAPETAS;
    END IF;

    --VALIDAR ABONOS
    IF UN_ABONOS IN(3) THEN
      MI_STRSQL := MI_SQLSEL || ' FROM SP_USUARIO 
                                    INNER JOIN (SELECT SP_USUARIO.COMPANIA
                                                      ,SP_USUARIO.CICLO
                                                      ,SP_USUARIO.CODIGORUTA
                                                      ,SP_USUARIO.ANO
                                                      ,SP_USUARIO.PERIODO
                                                      ,SP_USUARIO.CODIGOINTERNO
                                                      ,SUM(SP_USUARIO.NOTACREDITO) SUMADENOTACREDITO 
                                                FROM SP_USUARIO 
                                                  LEFT JOIN SP_ABONOS 
                                                     ON SP_USUARIO.COMPANIA   = SP_ABONOS.COMPANIA
                                                    AND SP_USUARIO.CICLO      = SP_ABONOS.CICLO 
                                                    AND SP_USUARIO.CODIGORUTA = SP_ABONOS.CODIGORUTA 
                                                    AND SP_USUARIO.ANO        = SP_ABONOS.ANO 
                                                    AND SP_USUARIO.PERIODO    = SP_ABONOS.PERIODO 
                                                WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                                                  AND SP_ABONOS.COMPANIA IS NULL
                                                  AND SP_USUARIO.CICLO BETWEEN '||UN_CICLO||' AND '||UN_CICLO||' 
                                                GROUP BY  SP_USUARIO.COMPANIA
                                                  ,SP_USUARIO.CICLO
                                                  ,SP_USUARIO.CODIGORUTA
                                                  ,SP_USUARIO.ANO
                                                  ,SP_USUARIO.PERIODO
                                                  ,SP_USUARIO.CODIGOINTERNO
                                                HAVING SUM(SP_USUARIO.NOTACREDITO)=0
                                               ) USUARIOSSINABONOS
                                    ON SP_USUARIO.COMPANIA    = USUARIOSSINABONOS.COMPANIA
                                    AND SP_USUARIO.CICLO      = USUARIOSSINABONOS.CICLO
                                    AND SP_USUARIO.CODIGORUTA = USUARIOSSINABONOS.CODIGORUTA
                                    AND SP_USUARIO.ANO        = USUARIOSSINABONOS.ANO
                                    AND SP_USUARIO.PERIODO    = USUARIOSSINABONOS.PERIODO
                                  '||MI_SQLWHE||'
                                  ORDER BY SP_USUARIO.CODIGORUTA ';

    ELSIF UN_ABONOS IN(0) THEN
      MI_STRSQL := MI_SQLSEL || ' FROM SP_USUARIO 
                                    INNER JOIN SP_ABONOS 
                                       ON SP_USUARIO.COMPANIA     = SP_ABONOS.COMPANIA
                                      AND SP_USUARIO.CICLO        = SP_ABONOS.CICLO
                                      AND SP_USUARIO.CODIGORUTA   = SP_ABONOS.CODIGORUTA
                                      AND SP_USUARIO.ANO          = SP_ABONOS.ANO
                                      AND SP_USUARIO.PERIODO      = SP_ABONOS.PERIODO
                                   '||MI_SQLWHE||'  
                                   ORDER BY SP_USUARIO.CODIGORUTA';

    ELSE
      MI_STRSQL := MI_SQLSEL || ' FROM SP_USUARIO 
                                    INNER JOIN (SELECT 
                                                   SP_USUARIO.COMPANIA
                                                  ,SP_USUARIO.CICLO
                                                  ,SP_USUARIO.CODIGORUTA
                                                  ,SP_USUARIO.ANO
                                                  ,SP_USUARIO.PERIODO
                                                  ,SP_USUARIO.CODIGOINTERNO
                                                  ,SUM(SP_USUARIO.NOTACREDITO) SUMADENOTACREDITO
                                                FROM SP_USUARIO 
                                                  LEFT JOIN SP_ABONOS 
                                                     ON SP_USUARIO.COMPANIA   = SP_ABONOS.COMPANIA 
                                                    AND SP_USUARIO.CICLO      = SP_ABONOS.CICLO 
                                                    AND SP_USUARIO.CODIGORUTA = SP_ABONOS.CODIGORUTA 
                                                    AND SP_USUARIO.ANO        = SP_ABONOS.ANO 
                                                    AND SP_USUARIO.PERIODO    = SP_ABONOS.PERIODO
                                                WHERE SP_ABONOS.COMPANIA IS NULL
                                                GROUP BY 
                                                   SP_USUARIO.COMPANIA
                                                  ,SP_USUARIO.CICLO
                                                  ,SP_USUARIO.CODIGORUTA
                                                  ,SP_USUARIO.ANO
                                                  ,SP_USUARIO.PERIODO
                                                  ,SP_USUARIO.CODIGOINTERNO
                                                HAVING SUM(SP_USUARIO.NOTACREDITO)=0
                                               ) USUARIOSSINABONOS
                                      ON SP_USUARIO.COMPANIA    = USUARIOSSINABONOS.COMPANIA
                                      AND SP_USUARIO.CICLO      = USUARIOSSINABONOS.CICLO
                                      AND SP_USUARIO.CODIGORUTA = USUARIOSSINABONOS.CODIGORUTA
                                      AND SP_USUARIO.ANO        = USUARIOSSINABONOS.ANO
                                      AND SP_USUARIO.PERIODO    = USUARIOSSINABONOS.PERIODO
                                  '||MI_SQLWHE||' 
                                  ORDER BY SP_USUARIO.CODIGORUTA';
    END IF;
  ELSE
    MI_SQLWHE := ' WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||''' 
                     AND SP_USUARIO.CICLO    = '''||UN_CICLO||'''
                     AND SP_USUARIO.CODIGORUTA     BETWEEN '''||UN_CODIGOINICIAL||''' AND '''||UN_CODIGOFINAL||'''
                     AND SP_USUARIO.PERIODOSATRASO BETWEEN '||UN_PERATRASOINI||' AND '||UN_PERATRASOFIN||'
                     AND (CASE WHEN '''||MI_PARFILTRODEUDA||''' IN(''SI'') 
                               THEN SP_USUARIO.TOTALDEUDA  
                               ELSE SP_USUARIO.TOTFACTURAPERACTUAL 
                          END)                     BETWEEN '||UN_DEUDAINI||' AND  '||UN_DEUDAFIN||'
                     AND SP_USUARIO.BANCOPERPROCESO IS NULL '
                     ||MI_CONDICIONESTADO
                     ||MI_CONDICIONCHAPETAS
                     ||MI_CONDICIONFECHA;

    --VALIDAR ABONOS
    IF UN_CONABONOS NOT IN(0) THEN
      MI_STRSQL := MI_SQLSEL || ' FROM SP_USUARIO ' 
                                  ||MI_SQLWHE ||' 
                                  ORDER BY SP_USUARIO.CODIGORUTA ';
    ELSE
      MI_STRSQL := MI_SQLSEL || ' FROM SP_USUARIO 
                                    INNER JOIN (SELECT 
                                                   SP_USUARIO.COMPANIA
                                                  ,SP_USUARIO.CICLO
                                                  ,SP_USUARIO.CODIGORUTA
                                                  ,SP_USUARIO.ANO
                                                  ,SP_USUARIO.PERIODO
                                                  ,SP_USUARIO.CODIGOINTERNO
                                                  ,SUM(SP_USUARIO.NOTACREDITO) SUMADENOTACREDITO
                                                FROM SP_USUARIO 
                                                  LEFT JOIN SP_ABONOS 
                                                     ON SP_USUARIO.COMPANIA   = SP_ABONOS.COMPANIA 
                                                    AND SP_USUARIO.CICLO      = SP_ABONOS.CICLO 
                                                    AND SP_USUARIO.CODIGORUTA = SP_ABONOS.CODIGORUTA 
                                                    AND SP_USUARIO.ANO        = SP_ABONOS.ANO 
                                                    AND SP_USUARIO.PERIODO    = SP_ABONOS.PERIODO
                                                WHERE SP_ABONOS.COMPANIA IS NULL
                                                GROUP BY 
                                                   SP_USUARIO.COMPANIA
                                                  ,SP_USUARIO.CICLO
                                                  ,SP_USUARIO.CODIGORUTA
                                                  ,SP_USUARIO.ANO
                                                  ,SP_USUARIO.PERIODO
                                                  ,SP_USUARIO.CODIGOINTERNO
                                                HAVING SUM(SP_USUARIO.NOTACREDITO)=0
                                               ) USUARIOSSINABONOS
                                       ON SP_USUARIO.COMPANIA   = USUARIOSSINABONOS.COMPANIA
                                      AND SP_USUARIO.CICLO      = USUARIOSSINABONOS.CICLO
                                      AND SP_USUARIO.CODIGORUTA = USUARIOSSINABONOS.CODIGORUTA
                                      AND SP_USUARIO.ANO        = USUARIOSSINABONOS.ANO
                                      AND SP_USUARIO.PERIODO    = USUARIOSSINABONOS.PERIODO
                                  '||MI_SQLWHE||' 
                                  ORDER BY SP_USUARIO.CODIGORUTA ';
    END IF;
  END IF;

  <<DATOSUSUARIO>>
  OPEN MI_RS FOR MI_STRSQL;
  LOOP
    FETCH MI_RS 
    INTO 
      MI_CODIGORUTA
     ,MI_PERIODOSATRASO
     ,MI_ANO
     ,MI_PERIODO
     ,MI_BANCOPERPROCESO;

    EXIT WHEN  MI_RS%NOTFOUND;

    IF MI_PAREXCLUIRPQR IN('SI') THEN
      MI_TIENEPAGOS := 0;

      MI_SQL := ' SELECT DISTINCT SP_USUARIO.CODIGORUTA 
                     FROM SP_USUARIO 
                       INNER JOIN SP_ORDENTRABAJO 
                          ON SP_USUARIO.COMPANIA   = SP_ORDENTRABAJO.COMPANIA 
                         AND SP_USUARIO.CODIGORUTA = SP_ORDENTRABAJO.CODIGORUTA 
                         AND SP_USUARIO.CICLO      = SP_ORDENTRABAJO.CICLO 
                       INNER JOIN SP_D_ORDENTRABAJO 
                          ON SP_ORDENTRABAJO.COMPANIA = SP_D_ORDENTRABAJO.COMPANIA 
                         AND SP_ORDENTRABAJO.CLASEDOC = SP_D_ORDENTRABAJO.CLASEDOC 
                         AND SP_ORDENTRABAJO.NUMORDEN = SP_D_ORDENTRABAJO.ORDENTRABAJO
                       WHERE SP_USUARIO.COMPANIA                    = '''||UN_COMPANIA||''' 
                         AND SP_USUARIO.CICLO                       = '||UN_CICLO||' 
                         AND SP_USUARIO.CODIGORUTA                  = '''||MI_CODIGORUTA||'''
                         AND (    SP_D_ORDENTRABAJO.TIPORESPUESTA     IS NULL 
                               OR SP_D_ORDENTRABAJO.FECHASOLUCION     IS NULL 
                               OR SP_D_ORDENTRABAJO.FECHANOTIFICACION IS NULL 
                               OR SP_D_ORDENTRABAJO.TIPONOTIFICACION  IS NULL) 
                         AND TRUNC(SP_ORDENTRABAJO.FECHASOLICITUD) >=  TO_DATE('''||MI_PARFECHACORTE||''',''DD/MM/YYYY'')
                         AND SP_ORDENTRABAJO.CLASEDOC               = ''PQR'' 
                         AND SP_ORDENTRABAJO.NUMORDEN IS NOT NULL  
                         AND SP_USUARIO.TOTFACTURAPERACTUAL        >= 0 
                         AND SP_USUARIO.ESTADO NOT IN (''R'',''S'') ';

      IF MI_PARPQRTRASLADADAS IN('SI') THEN

        IF UN_SUPERINT IN('S') THEN
          MI_CONDICIONAUX := ' AND SP_D_ORDENTRABAJO.FECHATRASLADO_SSPD IS NOT NULL ';
        ELSIF UN_SUPERINT IN('U') THEN
          MI_CONDICIONAUX := ' AND SP_D_ORDENTRABAJO.FECHATRASLADO.SSPD IS NULL ';
        ELSE
          MI_CONDICIONAUX := ' ';
        END IF;

        MI_SQL := ' SELECT DISTINCT SP_USUARIO.CODIGORUTA  
                    FROM SP_USUARIO 
                      INNER JOIN SP_ORDENTRABAJO 
                         ON SP_USUARIO.COMPANIA   = SP_ORDENTRABAJO.COMPANIA
                        AND  SP_USUARIO.CICLO     = SP_ORDENTRABAJO.CICLO 
                        AND SP_USUARIO.CODIGORUTA = SP_ORDENTRABAJO.CODIGORUTA
                      INNER JOIN SP_D_ORDENTRABAJO 
                        ON SP_ORDENTRABAJO.COMPANIA   = SP_D_ORDENTRABAJO.COMPANIA 
                        AND SP_ORDENTRABAJO.CLASEDOC  = SP_D_ORDENTRABAJO.CLASEDOC 
                        AND SP_ORDENTRABAJO.NUMORDEN  = SP_D_ORDENTRABAJO.ORDENTRABAJO
                    WHERE SP_USUARIO.COMPANIA                    = '''||UN_COMPANIA||''' 
                      AND SP_USUARIO.CICLO                       = '||UN_CICLO||' 
                      AND SP_USUARIO.CODIGORUTA                  = '''||MI_CODIGORUTA||''' 
                      AND TRUNC(SP_ORDENTRABAJO.FECHASOLICITUD) >= TO_DATE('''||MI_PARFECHACORTE||''',''DD/MM/YYYY'')
                      AND SP_ORDENTRABAJO.CLASEDOC               = ''PQR'' 
                      AND SP_ORDENTRABAJO.NUMORDEN IS NOT NULL 
                      AND SP_USUARIO.TOTFACTURAPERACTUAL        >= 0  
                      AND SP_USUARIO.ESTADO NOT IN (''R'',''S'') '
                      ||MI_CONDICIONAUX;
      END IF;

      BEGIN
        EXECUTE IMMEDIATE MI_SQL INTO MI_CODIGOAUX;

        MI_DEBEINSERTAR := -1;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_DEBEINSERTAR := -1;

          IF MI_BANCOPERPROCESO IS NOT NULL THEN
            MI_TIENEPAGOS := -1;
          END IF;

          SELECT 
            COUNT(1) CANT
          INTO MI_CANT
          FROM SP_USUARIO 
            INNER JOIN SP_ABONOS 
               ON SP_USUARIO.COMPANIA   = SP_ABONOS.COMPANIA 
              AND SP_USUARIO.CICLO      = SP_ABONOS.CICLO 
              AND SP_USUARIO.CODIGORUTA = SP_ABONOS.CODIGORUTA 
              AND SP_USUARIO.PERIODO    = SP_ABONOS.PERIODO
              AND SP_USUARIO.ANO        = SP_ABONOS.ANO 
          WHERE SP_USUARIO.COMPANIA  = UN_COMPANIA
            AND SP_USUARIO.CICLO     = UN_CICLO
            AND SP_USUARIO.PERIODO   = MI_PERIODO
            AND SP_USUARIO.ANO       = MI_ANO
            AND SP_USUARIO.CODIGORUTA= MI_CODIGORUTA
            AND SP_USUARIO.PERIODOSATRASO >0;              

          --tiene abonos y periodos de atraso >0
          IF MI_CANT IS NOT NULL THEN 
            IF MI_TIENEPAGOS IN(0) THEN
              MI_DEBEINSERTAR := 0;
            END IF;
          END IF;
      END;    
    ELSE
      MI_DEBEINSERTAR := -1;
    END IF;

    IF MI_DEBEINSERTAR NOT IN(0) THEN
      MI_ACTA_ANT := 0;

      BEGIN
        SELECT COUNT(SP_ACTASDESUSPENSION.IDACTA)
        INTO MI_CANT
        FROM SP_ACTASDESUSPENSION
        WHERE SP_ACTASDESUSPENSION.COMPANIA   = UN_COMPANIA
          AND SP_ACTASDESUSPENSION.CICLO      = UN_CICLO 
          AND SP_ACTASDESUSPENSION.CODIGORUTA = MI_CODIGORUTA
          AND SP_ACTASDESUSPENSION.ANO        = MI_ANO
          AND SP_ACTASDESUSPENSION.PERIODO    = MI_PERIODO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CANT := 0;
      END;


      IF MI_CANT > 0 THEN 
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ACTASDESUSPENSION';

            MI_CONDICION := ' SP_ACTASDESUSPENSION.COMPANIA   = ''' || UN_COMPANIA || ''' 
                          AND SP_ACTASDESUSPENSION.CICLO      = ' || UN_CICLO ||' 
                          AND SP_ACTASDESUSPENSION.CODIGORUTA = ''' || MI_CODIGORUTA ||''' 
                          AND SP_ACTASDESUSPENSION.ANO        = ' || MI_ANO ||' 
                          AND SP_ACTASDESUSPENSION.PERIODO    = ''' || MI_PERIODO || ''' ';

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'E'
                                       ,UN_CONDICION => MI_CONDICION
                                        );

            MI_ACTA_ANT := -1;                            

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR := MI_CODIGORUTA;
              MI_MSGERROR(2).CLAVE := 'CICLO';
              MI_MSGERROR(2).VALOR := UN_CICLO;

              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END; 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_E_REGACT_CODCIC
                                      ,UN_TABLAERROR => MI_TABLA
                                      ,UN_REEMPLAZOS => MI_MSGERROR
                                       );   
        END;

        IF MI_RTA > 0 THEN
          BEGIN
            BEGIN
              MI_TABLA := 'SP_ACTASDESUSPENSION';

              MI_CAMPOS  := ' COMPANIA
                             ,IDACTA
                             ,CICLO
                             ,CODIGORUTA
                             ,FECHAACTA
                             ,ANO
                             ,PERIODO
                             ,PERIODOSATRASO
                             ,ACTAS_ANT
                             ,DATE_CREATED
                             ,CREATED_BY ';  

              MI_VALORES := ''''|| UN_COMPANIA ||'''
                            ,LPAD('''|| MI_CONSECUTIVO ||''',10,''0'')
                            ,'|| UN_CICLO ||' 
                            ,'''|| MI_CODIGORUTA ||'''
                            ,TO_DATE('''|| UN_FECHAEMISION ||''',''DD/MM/YYYY'')
                            ,'||MI_ANO||'
                            ,''' || MI_PERIODO ||'''
                            ,'|| MI_PERIODOSATRASO ||'
                            ,'|| MI_ACTA_ANT||'
                            ,SYSDATE
                            ,'''||UN_USUARIO||''' ';

              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'I'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_VALORES   => MI_VALORES
                                          );                   

              MI_CONSECUTIVO := MI_CONSECUTIVO +1;                              

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR := MI_CODIGORUTA;
              MI_MSGERROR(2).CLAVE := 'CICLO';
              MI_MSGERROR(2).VALOR := UN_CICLO;

              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END; 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_REGACT_CODCIC
                                      ,UN_TABLAERROR => MI_TABLA
                                      ,UN_REEMPLAZOS => MI_MSGERROR
                                       );   
          END;
        END IF;      
      ELSE
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ACTASDESUSPENSION';

            MI_CAMPOS  := ' COMPANIA
                           ,IDACTA
                           ,CICLO
                           ,CODIGORUTA
                           ,FECHAACTA
                           ,ANO
                           ,PERIODO
                           ,PERIODOSATRASO
                           ,ACTAS_ANT 
                           ,DATE_CREATED
                           ,CREATED_BY ';

            MI_VALORES := ''''|| UN_COMPANIA ||'''
                          ,LPAD('''|| MI_CONSECUTIVO ||''',10,''0'')
                          ,'|| UN_CICLO ||' 
                          ,'''|| MI_CODIGORUTA ||'''
                          ,TO_DATE('''|| UN_FECHAEMISION ||''',''DD/MM/YYYY'')
                          ,'||MI_ANO||'
                          ,'''||MI_PERIODO||'''
                          ,'|| MI_PERIODOSATRASO ||'
                          ,'|| MI_ACTA_ANT||'
                          ,SYSDATE
                          ,'''||UN_USUARIO||''' ';

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'I'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_VALORES   => MI_VALORES
                                        );

          MI_CONSECUTIVO := MI_CONSECUTIVO +1;                               

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            MI_MSGERROR(1).CLAVE := 'CODIGO';
            MI_MSGERROR(1).VALOR := MI_CODIGORUTA;
            MI_MSGERROR(2).CLAVE := 'CICLO';
            MI_MSGERROR(2).VALOR := UN_CICLO;

            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_REGACT_CODCIC
                                    ,UN_TABLAERROR => MI_TABLA
                                    ,UN_REEMPLAZOS => MI_MSGERROR
                                     );   
        END;        
      END IF;
    END IF;

  END LOOP DATOSUSUARIO;
  CLOSE MI_RS;

  RETURN -1;
END FC_REGISTRARACTA;

  FUNCTION FC_NOMBREPERIODOM
  (

       /*
    NAME              : FC_NOMBREPERIODOM --> EN ACCESS NOMBREPERIODOM
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 31/08/2016
    TIME              : 16:04 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que retorna un mes teniendo encuenta un periodo y el parametro de frecuencia periodos de facturacion.
    PARAMETERS        : UN_COMPANIA => Compañia de ingreso a la aplicación
                        UN_PERIODO  => Número del periodo del cual se desea obtnener el nombre
    @NAME:  asignarNombrePeriodoM
    @METHOD:  GET
    */
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO
  )
  RETURN VARCHAR2
    AS
      --MI_ERROR_FUN        NUMBER := GL_ERROR_NUM + 2;
      MI_FRECUENCIA       VARCHAR2(3 CHAR);
      MI_RTA              VARCHAR2(30 CHAR);
      MI_PERIODOANT		    PCK_SUBTIPOS.TI_PERIODO;

    BEGIN

    MI_FRECUENCIA:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                         UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                         UN_FECHA_PAR => SYSDATE);

      MI_RTA:='A'; 
      CASE MI_FRECUENCIA     WHEN 'M' THEN    MI_RTA:= PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO);
                             WHEN 'B' THEN 	  MI_RTA:= CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2-1) 
                                                       WHEN 'Mes no válido' 
                                                       THEN 'TODOS'  
                                                       ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2-1)||'-'|| 
                                                                 CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2) 
                                                                 WHEN 'Mes no válido' 
                                                                 THEN 'TODOS' 
                                                                 ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2) 
                                                                 END
                                                       END;	
                            WHEN 'C' THEN     MI_PERIODOANT :=  CASE  UN_PERIODO*2-2  
                                                                WHEN 0 
                                                                THEN 12 
                                                                ELSE  UN_PERIODO*2-2
                                                                END ;
                                            MI_RTA:= CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODOANT)
                                                    WHEN 'Mes no válido' 
                                                    THEN 'TODOS' 
                                                    ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODOANT)||'-'|| 
                                                            CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2-1)
                                                            WHEN 'Mes no válido' 
                                                            THEN 'TODOS'
                                                            ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*2-1) 
                                                            END
                                                      END;
                          WHEN 'T' THEN 
                                          MI_RTA:= CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*3-2)
                                                  WHEN 'Mes no válido'
                                                  THEN 'TODOS'
                                                  ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*3-2)||'-'||
                                                            CASE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*3)
                                                            WHEN 'Mes no válido'
                                                            THEN 'TODOS' ELSE PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO*3) 
                                                            END 
                                                    END;
          END CASE;	
    RETURN MI_RTA;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOMPERIODO   
       );   

  END FC_NOMBREPERIODOM; 	

  --5
  FUNCTION FC_REGISTRARACTAOPERACION
  /*
    NAME              : FC_REGISTRARACTAOPERACION --> EN ACCESS RegistrarActaOperacion
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 05/09/2016
    TIME              : 08:53 AM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          : LEYDI MILENA CORTÉS FORERO
    DATE MODIFIED     : 17/11/2016
    TIME              : 05:10 PM
    DESCRIPTION       : Función que realiza un insert a la tabla SP_ACTAS_RR, 
                        retorna 1(Verdadero), 0(Falso), -1 Cuando el acta ya ha sido registrada. 
    PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                        UN_TIPOOP  		      => Tipo de Operación del acta de operación a registrar.
                        UN_CICLO            => Ciclo al que pertenece el acta.
                        UN_CODIGORUTA       => Código de Ruta al cual pertenece la operación que se está modificando.
                        UN_INTERNO          => Código interno del código de ruta de la operación.
                        UN_ANO            	=> Año del código de ruta de la operación.  
                        UN_PERIODO          => Periodo del código de ruta de la operación. 
                        UN_FECHAEJECUCION		=> Fecha de ejecución de la operación.
                        UN_HORAEJECUCION		=> Hora de ejecución de la operación. 
                        UN_AFORADOR         => Afordor que realizó el registro de la operación.
                        UN_DESCRIPCION      => Descripción de la operación.
                        UN_PERATRASO        => Periodos de ataraso del código de ruta de la operación.
                        UN_USUARIO          => Usuario que realiza el registro del acta.
                        UN_CLASEOPERACION   => Clase operación. Depende del tipo de operación realizada en la operación.

    MODIFIED BY       : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 17/07/2017
    DESCRIPTION       : Adicion de campos de auditoria. 
                        Manejo de excepciones. 
                        Lineamientos de programacion en PL.

    @NAME:  registrarActaOperacion
    @METHOD:  GET                     
  */
  (
     UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_TIPOOP         IN SP_INFO_TIPO_OPERACION.TIPO_OPERACION%TYPE
    ,UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO
    ,UN_CODIGORUTA     IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_INTERNO        IN SP_ACTAS_RR.CODIGOINTERNO%TYPE
    ,UN_ANO            IN PCK_SUBTIPOS.TI_ANIO
    ,UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO
    ,UN_FECHAEJECUCION IN DATE
    ,UN_HORAEJECUCION  IN DATE
    ,UN_AFORADOR       IN SP_INFO_TIPO_OPERACION.AFORADOR%TYPE
    ,UN_DESCRIPCION    IN SP_INFO_TIPO_OPERACION.DESCRIPCION%TYPE
    ,UN_PERATRASO      IN SP_INFO_TIPO_OPERACION.AFORADOR%TYPE
    ,UN_USUARIO        IN SP_INFO_TIPO_OPERACION.USUARIO%TYPE
    ,UN_CLASEOPERACION IN SP_ACTAS_RR.CLASEOPERACION%TYPE
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS 
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_IDACTA           SP_ACTAS_RR.IDACTA%TYPE DEFAULT NULL;
    MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_A          PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN 
      MI_TABLA_A := 'SP_ACTAS_RR';

      IF UN_TIPOOP IN('003','004') THEN    
        BEGIN
          --Se verifica si ya tuvo acta del mismo tipo para no re insertarla sino imprimir una copia 
          SELECT IDACTA 
          INTO MI_IDACTA
          FROM SP_ACTAS_RR 
          WHERE SP_ACTAS_RR.COMPANIA       = UN_COMPANIA 
            AND SP_ACTAS_RR.CICLO          = UN_CICLO
            AND SP_ACTAS_RR.CODIGORUTA     = UN_CODIGORUTA
            AND SP_ACTAS_RR.ANO            = UN_ANO
            AND SP_ACTAS_RR.PERIODO        = UN_PERIODO 
            AND SP_ACTAS_RR.CLASEOPERACION = UN_CLASEOPERACION;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_A
                                                            ,UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''''
                                                            ,UN_CAMPO    => 'IDACTA');
        END;

        --Existe un acta
        IF MI_IDACTA IS NOT NULL THEN
          RETURN 0;
        END IF;

        MI_CAMPOS := 'COMPANIA
                     ,IDACTA
                     ,CICLO
                     ,CODIGORUTA
                     ,CODIGOINTERNO
                     ,CLASEOPERACION
                     ,ANO
                     ,PERIODO
                     ,FECHAEJECUCION
                     ,HORAEJECUCION
                     ,AFORADOR
                     ,USUARIO
                     ,DESCRIPCION
                     ,PERIODOSATRASO
                     ,CREATED_BY
                     ,DATE_CREATED';

        MI_VALORES := ''''||UN_COMPANIA       ||'''
                      ,'''||MI_CONSECUTIVO    ||'''
                      ,  '||UN_CICLO          ||'
                      ,'''||UN_CODIGORUTA     ||'''
                      ,'''||UN_INTERNO        ||'''
                      ,'''||UN_CLASEOPERACION ||'''
                      ,  '||UN_ANO            ||'
                      ,'''||UN_PERIODO        ||'''
                      ,TO_DATE('''||UN_FECHAEJECUCION||''')
                      ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(UN_HORAEJECUCION,'HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')
                      ,'''||UN_AFORADOR       ||'''
                      ,'''||UN_USUARIO        ||'''
                      ,'''||UN_DESCRIPCION    ||'''
                      ,'''||UN_PERATRASO      ||'''
                      ,'''||UN_USUARIO        ||'''
                      ,SYSDATE';   

        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_A
                                       ,UN_ACCION    => 'I'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_VALORES   => MI_VALORES); 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODACTA';
          MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;
          MI_MSGERROR(2).CLAVE := 'CODRUTA';
          MI_MSGERROR(2).VALOR := UN_CODIGORUTA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_FCRAO_REACTAOPERACION
                                    ,UN_TABLAERROR => MI_TABLA_A
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END IF;

      RETURN -1; /*Adiciono acta*/
  END FC_REGISTRARACTAOPERACION; 

  FUNCTION FC_ARMADOCSOLICITUD
  (
       /*
    NAME              : FC_ARMADOCSOLICITUD --> EN ACCESS ArmaDocSolicitud
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 05/09/2016
    TIME              : 14:25 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que retorna los nombres separados por una coma según los parámetros ingresados.
    PARAMETERS        : UN_COMPANIA    		=> Compañia de ingreso a la aplicación
                        UN_CLASESOLICITUD => Identificador del Tipo de solicitud realizada.
                        UN_SOLICITUD      => Número de la solicitud de servicio.    
    @NAME:  armarDocumentoSolicitud
    @METHOD:  GET 
    */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASESOLICITUD   IN SP_SOLICITUDPORSERVICIO.CODIGO%TYPE,
    UN_SOLICITUD        IN SP_SOLICITUDSERVICIO.NUMERO%TYPE
  )
  RETURN VARCHAR2
  AS 

    MI_DOCSOLICITUD   VARCHAR2(3200 CHAR);
    MI_RS             SYS_REFCURSOR;
    MI_STRSQL         VARCHAR2(1500 CHAR);
    MI_NOMBRE         VARCHAR2(300 CHAR);

  BEGIN 
    MI_STRSQL := 'SELECT SP_DOCUMENTOS_MATRICULA.NOMBRE 
                    FROM SP_SOLICITUDDOCPRESENTADO 
                      INNER JOIN SP_DOCUMENTOS_MATRICULA 
                         ON SP_SOLICITUDDOCPRESENTADO.COMPANIA  = SP_DOCUMENTOS_MATRICULA.COMPANIA 
                        AND SP_SOLICITUDDOCPRESENTADO.DOCUMENTO = SP_DOCUMENTOS_MATRICULA.CODIGO
                   WHERE SP_SOLICITUDDOCPRESENTADO.COMPANIA = '''||UN_COMPANIA||'''
                     AND SP_SOLICITUDDOCPRESENTADO.CLASESOLICITUD ='''||UN_CLASESOLICITUD||'''
                     AND SP_SOLICITUDDOCPRESENTADO.SOLICITUDSERVICIO= '||UN_SOLICITUD||'';
    <<DOCSMATRICULA>>
    OPEN MI_RS FOR MI_STRSQL;
      LOOP 
        FETCH MI_RS INTO MI_NOMBRE;
        EXIT WHEN MI_RS%NOTFOUND;
          MI_DOCSOLICITUD := MI_DOCSOLICITUD || MI_NOMBRE || ', ';
      END LOOP DOCSMATRICULA;
      MI_DOCSOLICITUD := SUBSTR(MI_DOCSOLICITUD,1,LENGTH(MI_DOCSOLICITUD) - 2);
    CLOSE MI_RS;
    RETURN MI_DOCSOLICITUD;

  END  FC_ARMADOCSOLICITUD; 

  FUNCTION FC_NOMBREPERIODODE
    (
     /*
      NAME              : FC_NOMBREPERIODODE En Access --> NombrePeriodoDe
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 06/09/2016
      TIME              : 8:53 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      DESCRIPTION       : Retorna el Nombre del Mes concatenado con la palabra 'de' y seguido con el Año 
      PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                          UN_ANO        => Año al que coresponde el periodo.
                          UN_PERIODO    => Número del periodo del cual se desea obtnener el nombre. 
                          UN_FRECUENCIA => Frecuencia de los periodos de facturación.
      @NAME:  asignarNombrePeriodoDe
      @METHOD:  GET
    */

      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
      UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
      UN_FRECUENCIA   IN PCK_SUBTIPOS.TI_PARAMETRO
    )
    RETURN VARCHAR2

    AS 
     MI_PERIODO_ANT   PCK_SUBTIPOS.TI_PERIODO;
     MI_FRECUENCIA    VARCHAR2(3 CHAR);
     MI_NOMBREPERIODO VARCHAR2(300 CHAR);
     MI_MES           PCK_SUBTIPOS.TI_ENTERO;
     MI_MESUNO        PCK_SUBTIPOS.TI_ENTERO;

    BEGIN 
      IF UN_FRECUENCIA IS NULL OR UN_FRECUENCIA = '' 
      THEN 
        MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION' , 
                                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS , 
                                               UN_FECHA_PAR => SYSDATE);
      ELSE
        MI_FRECUENCIA := UN_FRECUENCIA;
      END IF;

      CASE 
        WHEN MI_FRECUENCIA = 'M' THEN 
          MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO) || ' de ' || UN_ANO;
        WHEN MI_FRECUENCIA = 'B' THEN 
          MI_MES := UN_PERIODO * 2 - 1;
          MI_MESUNO := UN_PERIODO * 2;
          MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) || ' de ' || UN_ANO;
        WHEN MI_FRECUENCIA = 'C' THEN 
          MI_PERIODO_ANT := CASE WHEN UN_PERIODO * 2 - 2 = 0 THEN 12 ELSE UN_PERIODO * 2 - 2 END;
          MI_MES := UN_PERIODO * 2 - 1;
          MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODO_ANT) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || ' de ' || UN_ANO;
        WHEN MI_FRECUENCIA = 'T'THEN 
          MI_MES := UN_PERIODO * 3 - 2;
          MI_MESUNO := UN_PERIODO *  3;
          MI_NOMBREPERIODO := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) || '-' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) || ' de ' || UN_ANO;
    END CASE ;
    RETURN MI_NOMBREPERIODO; 
    EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOMPERIODO   
       );   
    END FC_NOMBREPERIODODE;



  FUNCTION FC_NOMBREPERIODOSIGDE
   (
         /*
      NAME              : FC_NOMBREPERIODOSIGDE En Access --> NombrePeriodoDe
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 06/09/2016
      TIME              : 9:10 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      DESCRIPTION       : Retorna el Nombre del Período siguiente
      PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                          UN_ANO        => Año al que coresponde el periodo.
                          UN_PERIODO    => Número del periodo del cual se desea obtnener el nombre. 
                          UN_FRECUENCIA => Frecuencia de los periodos de facturación.
      @NAME:  asignarNombrePeriodoSigDe
      @METHOD:  GET
    */

      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
      UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
      UN_FRECUENCIA   IN PCK_SUBTIPOS.TI_PARAMETRO
    )
  RETURN VARCHAR2
    AS 
      MI_PERIODOSIGUIENTE  PCK_SUBTIPOS.TI_PERIODO;
      MI_ANO               PCK_SUBTIPOS.TI_ANIO;
      MI_PERIODO           PCK_SUBTIPOS.TI_PERIODO;
      MI_RTA               VARCHAR2(30 CHAR);
      MI_FRECUENCIA        VARCHAR2(3 CHAR);
    BEGIN 
      IF UN_FRECUENCIA IS NULL OR UN_FRECUENCIA = ' ' 
      THEN 
        MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION' , 
                                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS , 
                                               UN_FECHA_PAR => SYSDATE);
      ELSE
        MI_FRECUENCIA := UN_FRECUENCIA;
      END IF;

      MI_PERIODO := UN_PERIODO + 1;

      IF MI_FRECUENCIA = 'M' 
      AND MI_PERIODO > 12 
      THEN 
        MI_ANO := UN_ANO + 1;
        MI_PERIODO := 1;
      ELSIF MI_FRECUENCIA = 'B' 
      AND MI_PERIODO >  6 
      THEN 
        MI_ANO := UN_ANO + 1;
        MI_PERIODO := 1;
      ELSIF MI_FRECUENCIA = 'C' 
      AND MI_PERIODO > 6 
      THEN 
        MI_ANO := UN_ANO + 1;
        MI_PERIODO := 1;
      ELSIF MI_FRECUENCIA = 'T' 
      AND MI_PERIODO > 4 
      THEN 
        MI_ANO := UN_ANO + 1;
        MI_PERIODO := 1;
      ELSE 
        MI_ANO := UN_ANO;
      END IF;

      MI_PERIODOSIGUIENTE := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_PERIODO,
                                                       UN_LONGITUD => 2);
      MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODODE(UN_COMPANIA   => UN_COMPANIA, 
                                                               UN_ANO        => MI_ANO,
                                                               UN_PERIODO    => MI_PERIODOSIGUIENTE,
                                                               UN_FRECUENCIA => MI_FRECUENCIA);

      RETURN MI_RTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOMPERIODO   
       );       
  END FC_NOMBREPERIODOSIGDE; 

  FUNCTION FC_AUTORIZACION_DESVIACION 
  (
     /*
        NAME              : FC_AUTORIZACION_DESVIACION --> EN ACCESS AUTORIZACION_DESVIACION
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
        DATE MIGRADOR     : 06/09/2016
        TIME              : 15:30 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Función que retorna -1(Verdadero) o 0(False)dependediendo del  nit
        PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                            UN_NIT        => Nit de tercero.
        @NAME:  autorizarDesviacion
        @METHOD:  GET
        */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_NIT              IN TERCERO.NIT%TYPE
    )
  RETURN NUMBER
    AS 
      MI_AUTORIZACION   PCK_SUBTIPOS.TI_LOGICO;
      MI_NIT            PCK_SUBTIPOS.TI_PARAMETRO;
    BEGIN
      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                               UN_NOMBRE    => 'PROCESO UNICO DE CRITICA Y DESVIACIÓN',
                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                               UN_FECHA_PAR => SYSDATE) <> 'SI' 
      THEN 
        MI_AUTORIZACION := 0;
        RETURN MI_AUTORIZACION;
      END IF;
      MI_NIT := PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                          UN_NIT      => UN_NIT);
      CASE
      WHEN MI_NIT        = '844000755' THEN --YOPAL
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832000776' THEN --FUNZA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832001512' THEN --MADRID
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '899999714' THEN --CHIA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '890680053' THEN --FUSAGASUGA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '899999717' THEN --PRUEBAS
        MI_AUTORIZACION := -1;
      ELSE
        MI_AUTORIZACION := 0;
      END CASE;
      RETURN MI_AUTORIZACION;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_AUTDESVIACION   
       );
    END FC_AUTORIZACION_DESVIACION;



   FUNCTION FC_AUTORIZACION_FRAUDES 
  (
     /*
        NAME              : FC_AUTORIZACION_FRAUDES --> EN ACCESS AUTORIZACION_FRAUDES
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
        DATE MIGRADOR     : 06/09/2016
        TIME              : 15:40 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Definir si la entidad está autorizada para manejar proceso de Fraudes, 
                            según su Nit y la configuración del parámetro MANEJA CONTROL DE FRAUDES. 
        PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                            UN_NIT        => Nit de tercero.
        @NAME:  autorizarFraudes
        @METHOD:  GET
        */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_NIT              IN TERCERO.NIT%TYPE
  )
  RETURN NUMBER
    AS 
      MI_AUTORIZACION NUMBER;
      MI_NIT          VARCHAR2(30);
    BEGIN
      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                               UN_NOMBRE    => 'MANEJA CONTROL DE FRAUDES',
                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                               UN_FECHA_PAR => SYSDATE) <> 'SI'
      THEN 
        MI_AUTORIZACION := 0;
        RETURN MI_AUTORIZACION;
      END IF;
      MI_NIT := PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                          UN_NIT      => UN_NIT);
      CASE
      WHEN MI_NIT        = '844000755' THEN --YOPAL
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832000776' THEN --FUNZA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832001512' THEN --MADRID
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '899999714' THEN --CHIA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '890680053' THEN --FUSAGASUGA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '899999717' THEN --PRUEBA
        MI_AUTORIZACION := -1;
      ELSE
        MI_AUTORIZACION := 0;
      END CASE;
      RETURN MI_AUTORIZACION;
      EXCEPTION
    WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_AUTFRAUDES   
       );
    END FC_AUTORIZACION_FRAUDES;


  FUNCTION FC_NOMBREPERIODOCORTO 
  (
    /*
        NAME              : FC_NOMBREPERIODOCORTO --> EN ACCESS NombrePeriodoCorto
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
        DATE MIGRADOR     : 08/09/2016
        TIME              : 09:08 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Función que retorna las tres primeras letras del mes concatenado con el año.
        PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                            UN_ANO        => Año al que coresponde el periodo.
                            UN_PERIODO    => Número del periodo del cual se desea obtnener el nombre. 
                            UN_FRECUENCIA => Frecuencia de los periodos de facturación.
        @NAME:  asignarNombrePeriodoCorto
        @METHOD:  GET
        */
        UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
        UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
        UN_FRECUENCIA   IN PCK_SUBTIPOS.TI_PARAMETRO
  )
  RETURN VARCHAR2
    AS 
      MI_FRECUENCIA   PCK_SUBTIPOS.TI_PARAMETRO;
      MI_RTA          VARCHAR2(30);
      MI_MES          PCK_SUBTIPOS.TI_ENTERO;
      MI_MES1         PCK_SUBTIPOS.TI_ENTERO;
      MI_PERIODOANT   PCK_SUBTIPOS.TI_PERIODO;

    BEGIN 
      IF UN_FRECUENCIA IS NULL OR UN_FRECUENCIA <> ' '  THEN 
        MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION' , 
                                               UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS , 
                                               UN_FECHA_PAR => SYSDATE);
      ELSE 
        MI_FRECUENCIA := UN_FRECUENCIA;
      END IF;

      CASE 
        WHEN MI_FRECUENCIA = 'M' THEN
          MI_RTA := SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO)||'',1,3)|| '/' || UN_ANO;
        WHEN MI_FRECUENCIA = 'B' THEN 
          MI_MES := UN_PERIODO * 2 - 1;
          MI_MES1 := UN_PERIODO * 2;
          MI_RTA := SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES)||'',1,3) || '-' || 
                    SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES1)||'',1,3) || '/' || UN_ANO;
        WHEN MI_FRECUENCIA = 'C' THEN 
          MI_MES := UN_PERIODO * 2 - 2;
          MI_PERIODOANT := CASE WHEN MI_MES = 0 THEN 12 ELSE MI_MES END;
          MI_MES1 := UN_PERIODO * 2 -1;
          MI_RTA := SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_PERIODOANT)||'',1,3) || '-' || 
                    SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES1)||'' , 1,3) || '/' || UN_ANO;
        WHEN MI_FRECUENCIA = 'T' THEN 
          MI_MES := UN_PERIODO * 3 - 2;
          MI_MES1 := UN_PERIODO * 3;
          MI_RTA :=  SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES)||'',1,3) || '-' || 
                     SUBSTR(''||PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES1)||'',1,3)||'/'|| UN_ANO;
      END CASE;

      RETURN MI_RTA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOMPERIODO   
       );   
    END FC_NOMBREPERIODOCORTO;  

    FUNCTION FC_ANOSIGUIENTE
   (
     /*
        NAME              : FC_ANOSIGUIENTE --> EN ACCESS AnoSte
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
        DATE MIGRADOR     : 16/09/2016
        TIME              : 14:24 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Función que retorna el año (Solamente el mes, bimestre, o trimestre) siguiente. 
        PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                            UN_ANO        => Año al que coresponde el periodo.
                            UN_PERIODO    => Número del periodo actual. 
                            UN_FRECUENCIA => Frecuencia de los periodos de facturación.
        @NAME:  generarAnoSiguiente
        @METHOD:  GET
        */
        UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
        UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
        UN_FRECUENCIA   IN PCK_SUBTIPOS.TI_PARAMETRO
    )
    RETURN NUMBER 
     AS
      MI_PERIODO1          PCK_SUBTIPOS.TI_PERIODO; 
      MI_ANO1              PCK_SUBTIPOS.TI_ANIO; 
      MI_FRECUENCIA        PCK_SUBTIPOS.TI_PARAMETRO; 
      MI_ANOSIGUIENTE      PCK_SUBTIPOS.TI_ANIO; 
    BEGIN
      IF UN_FRECUENCIA = '' 
      OR UN_FRECUENCIA IS  NULL 
      THEN 
         MI_FRECUENCIA:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION' , 
                                              UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS , 
                                              UN_FECHA_PAR => SYSDATE); 
      ELSE 
        MI_FRECUENCIA:=UN_FRECUENCIA; 
      END IF; 

      MI_ANO1         := UN_ANO;
      MI_PERIODO1     := UN_PERIODO+1; 
      MI_ANOSIGUIENTE := 0; 

      IF MI_FRECUENCIA = 'M' 
      AND MI_PERIODO1 > 12 
      THEN 
         MI_ANO1 := MI_ANO1+1; 
      ELSIF MI_FRECUENCIA = 'B' 
      AND MI_PERIODO1 > 6 
      THEN 
         MI_ANO1 := MI_ANO1+1;
      ELSIF MI_FRECUENCIA = 'C' 
      AND MI_PERIODO1 > 6 
      THEN 
         MI_ANO1 := MI_ANO1+1;
      ELSIF MI_FRECUENCIA = 'T' 
      AND MI_PERIODO1 > 4 
      THEN 
         MI_ANO1 := MI_ANO1+1;
      END IF; 
      MI_ANOSIGUIENTE := MI_ANO1; 

      RETURN MI_ANOSIGUIENTE;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ANIOSIG   
       );  

  END FC_ANOSIGUIENTE;

  FUNCTION FC_PERIODOSIGUIENTE
  (
  /*
        NAME              : FC_PERIODOSIGUIENTE --> EN ACCESS PerSte
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
        DATE MIGRADOR     : 16/09/2016
        TIME              : 14:40 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
        DATE MODIFIED     : 17/02/2017
        TIME              : 12:49 PM
        DESCRIPTION       : Retorna el periodo (solamente el mes, bimestre o trimestre) siguiente.  
        PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                            UN_ANO        => Año al que coresponde el periodo.
                            UN_PERIODO    => Número del periodo actual. 
                            UN_FRECUENCIA => Frecuencia de los periodos de facturación.
        MODIFICATIONS     : SE LLAMA LA FUNCION FC_ANO_PERIODO_SIGUIENTE DEL PAQUETE PCK_SERVICIOS_PUBLICOS, 
                            LA CUAL SI ESTA REALIZANDO CORRECTAMENTE EL PROCESO DE GENERAR EL PERIODO SIGUIENTE.
                            ESTA FUNCION NO ESTABA FUNCIONANDO, YA QUE NO DEVOLVIA EL PERIODO CORRECTAMENTE 
                            (EJ. DEVOLVIA: 4, CUANDO DEBERIA DEVOLVER: '04')
        @NAME:  generarPeriodoSiguiente
        @METHOD:  GET
        */
        UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
        UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
        UN_FRECUENCIA   IN PCK_SUBTIPOS.TI_PARAMETRO
  ) 
    RETURN VARCHAR2
    AS
      MI_PERIODO        PCK_SUBTIPOS.TI_PERIODO;
    BEGIN
     MI_PERIODO := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA     => UN_COMPANIA,
                                                                   UN_ANO          => UN_ANO,
                                                                   UN_PERIODO      => UN_PERIODO,
                                                                   UN_TIPO_RETORNO => '1',
                                                                   UN_FRECUENCIA   => UN_FRECUENCIA);
     RETURN MI_PERIODO; 
  END FC_PERIODOSIGUIENTE;

  PROCEDURE PR_ABONOPRIORIDAD
    /*
    NAME              : PR_ABONOPRIORIDAD En access --> AbonoPrioridad 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 20/09/2016
    TIME              : 17:35
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento encargado de actualizar el campo valorAnt en la tabla SP_D_ABONOS
    PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                        UN_CICLO      => Ciclo al que pertenece el abono.
                        UN_CODIGORUTA => Código de Ruta al cual pertenece el abono que se está modificando.
              UN_CONSECUTIVO => Valor del consecutivo del abono. 
                        UN_PERIODO    => Número del periodo del abono.
                        UN_ANO        => Año al que coresponde el periodo.
                        UN_DBLVALOR   => Valor total factura.
                        UN_DBLDEUDA   => Valor de la deuda actual.
    @NAME:  actualizarAbonoPrioridad
    @METHOD:  POST   
    */
    (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO                    IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA               IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_PERIODO                  IN PCK_SUBTIPOS.TI_PERIODO,
    UN_CONSECUTIVO              IN SP_D_ABONOS.CONSECUTIVO%TYPE,
    UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,
    UN_DBLVALOR                 IN PCK_SUBTIPOS.TI_DOBLE,
    UN_DBLDEUDA                 IN PCK_SUBTIPOS.TI_DOBLE 
    )
    AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_VALORACUMULADO         PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORACUMULADOPER      PCK_SUBTIPOS.TI_DOBLE;
      MI_RS                     SYS_REFCURSOR;  
    BEGIN
    <<CONCEPTOS>>
    FOR MI_RS IN ( SELECT SP_FACTURADO.COMPANIA,
                          SP_FACTURADO.CICLO,
                          SP_FACTURADO.CODIGORUTA,
                          SP_FACTURADO.ANO,
                          SP_FACTURADO.PERIODO,
                          SP_FACTURADO.CONCEPTO,
                          SP_FACTURADO.VALOR_FACTURADO,
                          SP_FACTURADO.DEUDA,
                          SP_FACTURADO.DEUDA           + SP_FACTURADO.VALORFINANT-SP_FACTURADO.VALORABONOANT                 AS TOTDEUDA,
                          SP_FACTURADO.VALOR_FACTURADO + SP_FACTURADO.VALORFINACT-SP_FACTURADO.VALORABONOACT                 AS TOTPERIODO,
                          SP_FACTURADO.VALOR_FACTURADO+SP_FACTURADO.DEUDA+SP_FACTURADO.VALORFINACT+SP_FACTURADO.VALORFINANT-SP_FACTURADO.VALORABONOACT-SP_FACTURADO.VALORABONOANT AS TOTFACT,
                          SP_CONCEPTOS.ORDENABONAR,
                          SP_CONCEPTOS.CODIGO
                     FROM SP_CONCEPTOS
                      INNER JOIN SP_FACTURADO
                         ON (SP_CONCEPTOS.CODIGO     = SP_FACTURADO.CONCEPTO)
                        AND (SP_CONCEPTOS.COMPANIA   = SP_FACTURADO.COMPANIA)
                    WHERE (SP_FACTURADO.COMPANIA  = UN_COMPANIA
                      AND SP_FACTURADO.CICLO      = UN_CICLO
                      AND SP_FACTURADO.CODIGORUTA = UN_CODIGORUTA 
                      AND SP_FACTURADO.ANO        = UN_ANO
                      AND SP_FACTURADO.PERIODO    = UN_PERIODO
                      AND (((SP_FACTURADO.CONCEPTO) BETWEEN 1 AND 48
                      OR (SP_FACTURADO.CONCEPTO)    IN (201,202,203,204,205,206,207,246,247,248,249))
                      AND SP_FACTURADO.CONCEPTO     NOT IN (12,17))
                      AND SP_FACTURADO.VALOR_FACTURADO+SP_FACTURADO.DEUDA+SP_FACTURADO.VALORFINACT+SP_FACTURADO.VALORFINANT-SP_FACTURADO.VALORABONOACT-SP_FACTURADO.VALORABONOANT <>0))
    LOOP

      MI_CAMPOS := 'COMPANIA,'||
                   'CICLO,'||
                   'CODIGORUTA'||
                   ',ANO'||
                   ',PERIODO'||
                   ',CONCEPTO'||
                   ',VALOR'||
                   ',CONSECUTIVO'||
                   ',VALORACT'||
                   ',VALORANT';
      MI_VALORES := '''' || UN_COMPANIA ||
                    ''',' || UN_CICLO ||
                    ',''' || UN_CODIGORUTA ||
                    ''',' || UN_ANO || 
                    ',''' || UN_PERIODO ||
                    ''',''' || MI_RS.CONCEPTO || 
                    ''', 0, ' 
                    || UN_CONSECUTIVO || 
                    ',0,0';
      BEGIN 
       BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_D_ABONOS',
                                               UN_ACCION  => 'I',
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES);
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
       END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
       PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGDEABONO
         );
      END; 

      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                     ''' AND CICLO = ' || UN_CICLO || 
                       ' AND CODIGORUTA = ''' || UN_CODIGORUTA || 
                     ''' AND ANO = ' || UN_ANO || 
                       ' AND PERIODO = ''' || UN_PERIODO || 
                     ''' AND CONCEPTO = '' '|| MI_RS.CONCEPTO || 
                     ''' AND CONSECUTIVO = '||UN_CONSECUTIVO;
      BEGIN
        IF MI_RS.TOTDEUDA >= UN_DBLVALOR 
        THEN
          IF(UN_DBLVALOR - MI_VALORACUMULADO) < MI_RS.TOTDEUDA 
          THEN 

            MI_CAMPOS := ' VALORANT = '||(UN_DBLVALOR - MI_VALORACUMULADO);        
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
            MI_VALORACUMULADO := UN_DBLVALOR;
          ELSE
            MI_CAMPOS := ' VALORANT = ' || MI_RS.TOTDEUDA;        
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
            MI_VALORACUMULADO := UN_DBLVALOR + MI_RS.TOTDEUDA;
          END IF;

        ELSE 
          IF (UN_DBLVALOR - MI_VALORACUMULADO) < MI_RS.TOTDEUDA 
          THEN
            MI_CAMPOS := ' VALORANT = ' || UN_DBLVALOR - MI_VALORACUMULADO;        
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
             MI_VALORACUMULADO := UN_DBLVALOR;
          ELSE 
            MI_CAMPOS := ' VALORANT = '||MI_RS.TOTDEUDA;        
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);  
            MI_VALORACUMULADO := MI_VALORACUMULADO + MI_RS.TOTDEUDA;                                       
          END IF;

          IF((UN_DBLVALOR - UN_DBLDEUDA) - MI_VALORACUMULADO) > 0
          THEN
            IF(UN_DBLVALOR - UN_DBLDEUDA) - MI_VALORACUMULADOPER < MI_RS.TOTPERIODO 
            THEN 
              MI_CAMPOS := ' VALORANT = ' || (UN_DBLVALOR - UN_DBLVALOR) - MI_VALORACUMULADO;          
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);  
              MI_VALORACUMULADOPER := UN_DBLVALOR - UN_DBLDEUDA;
            ELSE
              MI_CAMPOS := ' VALORANT = ' || MI_RS.TOTPERIODO;          
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);  
              MI_VALORACUMULADOPER := MI_VALORACUMULADOPER + MI_RS.TOTPERIODO;        
            END IF;
          END IF;
        END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
    END LOOP CONCEPTOS;

    BEGIN
      MI_CAMPOS := ' VALOR = VALORACT + VALORANT';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                     ''' AND CICLO = ''' || UN_CICLO || 
                     ''' AND CODIGORUTA = ''' || UN_CODIGORUTA || 
                     ''' AND ANO = ''' || UN_ANO || 
                     ''' AND PERIODO = ''' || UN_PERIODO || 
                     ''' AND CONSECUTIVO = ' || UN_CONSECUTIVO;

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
    END;  

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
       PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTDABONOS
         );

  END PR_ABONOPRIORIDAD;


  FUNCTION FC_VALORPAGOCONVENIOS
  /*
    NAME              : FC_VALORPAGOCONVENIOS En access --> ValPagoConv
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Maritza Cáceres Bonilla
    DATE MIGRADOR     : 26/09/2016
    TIME              : 15:00
    SOURCE MODULE     : SysmanSp2016.05.04
    DESCRIPTION       : Función que devuelve el pago de convenios de una factura en la tabla HISTORIA_EXTERNA
    PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                        UN_CICLO      => Ciclo al que pertenece el convenio.
                        UN_CODIGORUTA => Código de Ruta al cual pertenece el convenio.
                        UN_ANO        => Año al que coresponde el convenio.
                        UN_PERIODO    => Número del periodo del convenio.
                        UN_CONVENIO   => Valor correspondiente a si es convenio o no.
    @NAME:  consultarValorPagoConvenios
    @METHOD:  GET
    */
    (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO        IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA   IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_CONVENIO     IN VARCHAR2
  )
    RETURN NUMBER
    AS
     MI_STRSQL        VARCHAR2(3000 CHAR);
     MI_VALPAGOCONV   PCK_SUBTIPOS.TI_DOBLE;
    BEGIN
     MI_VALPAGOCONV:=0;

      IF UN_CONVENIO = 'SI'   THEN
          BEGIN
              SELECT NVL(SUM(SP_HISTORIA_CONVENIOS.TOTAL), 0) TOTAL
              INTO   MI_VALPAGOCONV
              FROM   SP_HISTORIA_CONVENIOS
              WHERE  SP_HISTORIA_CONVENIOS.COMPANIA   = UN_COMPANIA
                AND  SP_HISTORIA_CONVENIOS.CICLO      = UN_CICLO
                AND  SP_HISTORIA_CONVENIOS.CODIGORUTA = UN_CODIGORUTA
                AND  SP_HISTORIA_CONVENIOS.ANO        = UN_ANO
                AND  SP_HISTORIA_CONVENIOS.PERIODO    = UN_PERIODO
                AND  SP_HISTORIA_CONVENIOS.NOCOBRAR   = 0
                AND  SP_HISTORIA_CONVENIOS.BANCO_PAGO IS NULL
                AND  SP_HISTORIA_CONVENIOS.FECHA_PAGO IS NULL ;

          EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
              MI_VALPAGOCONV:=0;
              RETURN MI_VALPAGOCONV;
          END;

      END IF;
      RETURN MI_VALPAGOCONV;
  END FC_VALORPAGOCONVENIOS;


  FUNCTION FC_VALORPAGOTERCERIZADO
  /*
    NAME              : FC_VALORPAGOTERCERIZADO En access --> ValPagoTer
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Maritza Cáceres Bonilla
    DATE MIGRADOR     : 26/09/2016
    TIME              : 16:11
    SOURCE MODULE     : SysmanSp2016.05.04
    DESCRIPTION       : Función que devuelve el pago tercerizado de una factura en la tabla HISTORIA_EXTERNA
    PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                        UN_CICLO      => Ciclo al que pertenece el convenio.
                        UN_CODIGORUTA => Código de Ruta al cual pertenece el convenio.
                        UN_ANO        => Año al que coresponde el convenio.
                        UN_PERIODO    => Número del periodo del convenio.
                        UN_CONVENIO   => Valor correspondiente a si es convenio o no.
    @NAME:  consultarValorPagoTercerizado
    @METHOD:  GET
    */
    (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO        IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA   IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO,
    UN_TERCERIZADO  IN VARCHAR2
  )
  RETURN NUMBER
  AS
   MI_STRSQL          VARCHAR(3200 CHAR);
   MI_VALORPAGOTER    PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
   IF UN_TERCERIZADO = 'SI'  THEN
      BEGIN
          SELECT NVL(VALORASEO, 0) VALORASEO
          INTO MI_VALORPAGOTER
          FROM SP_USUARIO
               INNER JOIN SP_HISTORIA_EXTERNA
                  ON SP_USUARIO.COMPANIA       = SP_HISTORIA_EXTERNA.COMPANIA
                 AND SP_USUARIO.CICLO          = SP_HISTORIA_EXTERNA.CICLO
                 AND SP_USUARIO.CODIGORUTA     = SP_HISTORIA_EXTERNA.CODIGORUTA
                 AND SP_USUARIO.EMPRESAASEOEXT = SP_HISTORIA_EXTERNA.ID_EMPRESA
          WHERE SP_USUARIO.COMPANIA          = UN_COMPANIA
            AND SP_USUARIO.CICLO             = UN_CICLO
            AND SP_USUARIO.CODIGORUTA        = UN_CODIGORUTA
            AND SP_HISTORIA_EXTERNA.ANO      = UN_ANO
            AND SP_HISTORIA_EXTERNA.PERIODO  = UN_PERIODO
            AND  BANCO_PAGO IS NULL
            AND  FECHA_PAGO IS NULL;

      EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
          MI_VALORPAGOTER:=0;
          RETURN MI_VALORPAGOTER;
      END;
      RETURN MI_VALORPAGOTER;
   END IF;

  END FC_VALORPAGOTERCERIZADO;


  PROCEDURE PR_CHARLESPESOAB
      /*
    NAME              : PR_CHARLESPESOAB En access --> CharlesPesoAb 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 27/09/2016
    TIME              : 12:20
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Actualiza el valor, valorant y valoract de la tabla SP_D_ABONOS teniendo encuenta la sumatoria de del valorAnt 
                        y valorAct de la tabla SP_ABONOS
    PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación
                        UN_CICLO       => Ciclo al que pertenece el abono.
                        UN_CODIGORUTA  => Código de Ruta al cual pertenece el abono.
                        UN_ANO         => Año al que coresponde el abono.
                        UN_PERIODO     => Número del periodo del abono.
                        UN_CONSECUTIVO => Valor del consecutivo del abono. 
    @NAME:  charlesPesoAB
    @METHOD:  PUT   

    */
    (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO                    IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA               IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO                  IN PCK_SUBTIPOS.TI_PERIODO,
    UN_CONSECUTIVO              IN SP_ABONOS.CONSECUTIVO%TYPE
    )

    AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_FILES                  PCK_SUBTIPOS.TI_ENTERO;
      MI_CONCEPTO               SP_D_ABONOS.CONCEPTO%TYPE;   
      MI_VALORANT               PCK_SUBTIPOS.TI_DOBLE;   
      MI_RS                     SYS_REFCURSOR;

    BEGIN
      BEGIN
        MI_CAMPOS:='SP_D_ABONOS.VALOR = ROUND(SP_D_ABONOS.VALORACT,0) + ROUND(SP_D_ABONOS.VALORANT,0), 
                    SP_D_ABONOS.VALORACT = ROUND(SP_D_ABONOS.VALORACT,0), 
                    SP_D_ABONOS.VALORANT = ROUND(SP_D_ABONOS.VALORANT,0)';
        MI_CONDICION :='COMPANIA = '''|| UN_COMPANIA ||'''
                        AND CICLO = ' || UN_CICLO || '
                        AND CODIGORUTA = ''' || UN_CODIGORUTA || '''
                        AND ANO = ' || UN_ANO || '
                        AND PERIODO = ''' || UN_PERIODO || '''
                        AND CONSECUTIVO = ' || UN_CONSECUTIVO;

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION); 
      <<ABONOS>>
      FOR MI_RS IN ( SELECT SP_ABONOS.COMPANIA,
                            SP_ABONOS.CONSECUTIVO, 
                            SP_ABONOS.FECHA,
                            SP_ABONOS.VALOR, 
                            SUM(SP_D_ABONOS.VALORANT + SP_D_ABONOS.VALORACT) AS SUMADA, 
                            SP_ABONOS.VALOR - SUM(SP_D_ABONOS.VALORANT + SP_D_ABONOS.VALORACT) AS DIFSUMA 
                       FROM SP_ABONOS 
                          INNER JOIN SP_D_ABONOS
                                 ON SP_ABONOS.COMPANIA    = SP_D_ABONOS.COMPANIA
                                AND SP_ABONOS.CICLO       = SP_D_ABONOS.CICLO
                                AND SP_ABONOS.CODIGORUTA  = SP_D_ABONOS.CODIGORUTA
                                AND SP_ABONOS.ANO         = SP_D_ABONOS.ANO 
                                AND SP_ABONOS.PERIODO     = SP_D_ABONOS.PERIODO 
                                AND SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO 
                      WHERE SP_ABONOS.COMPANIA    = UN_COMPANIA
                        AND SP_ABONOS.CICLO       = UN_CICLO
                        AND SP_ABONOS.CODIGORUTA  = UN_CODIGORUTA 
                        AND SP_ABONOS.ANO         = UN_ANO
                        AND SP_ABONOS.PERIODO     = UN_PERIODO
                        AND SP_ABONOS.CONSECUTIVO = UN_CONSECUTIVO
                      GROUP BY SP_ABONOS.COMPANIA, 
                               SP_ABONOS.CONSECUTIVO, 
                               SP_ABONOS.FECHA, 
                               SP_ABONOS.VALOR)
      LOOP
        IF MI_RS.DIFSUMA <> 0
        THEN
             SELECT COUNT (*),
                    CONCEPTO,
                    VALORANT 
              INTO MI_FILES, MI_CONCEPTO, MI_VALORANT
              FROM SP_D_ABONOS
             WHERE SP_D_ABONOS.COMPANIA =UN_COMPANIA
               AND SP_D_ABONOS.CICLO =UN_CICLO
               AND SP_D_ABONOS.CODIGORUTA =UN_CODIGORUTA
               AND SP_D_ABONOS.ANO = UN_ANO
               AND SP_D_ABONOS.PERIODO = UN_PERIODO
               AND SP_D_ABONOS.CONSECUTIVO = UN_CONSECUTIVO
               AND ROWNUM =1
             GROUP BY CONCEPTO,VALORANT
             ORDER BY CONCEPTO;

            IF MI_FILES <> 0 
            THEN 
              IF MI_VALORANT <> 0 
              THEN
                   MI_CAMPOS := 'VALORANT = VALORANT + ' || MI_RS.DIFSUMA || 
                              ', VALOR = VALOR + ' || MI_RS.DIFSUMA;
                   MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                                   AND CICLO = ' ||UN_CICLO || '
                                   AND CODIGORUTA = ''' || UN_CODIGORUTA || '''
                                   AND ANO = '|| UN_ANO || '
                                   AND PERIODO = '''|| UN_PERIODO || '''
                                   AND CONSECUTIVO = '|| UN_CONSECUTIVO || '
                                   AND CONCEPTO = '|| MI_CONCEPTO;

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION); 
              ELSE
                   MI_CAMPOS := 'VALORACT = VALORACT + ' || MI_RS.DIFSUMA || 
                              ', VALOR = VALOR + ' || MI_RS.DIFSUMA;
                   MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                                   AND CICLO = ' || UN_CICLO || '
                                   AND CODIGORUTA = ''' || UN_CODIGORUTA || '''
                                   AND ANO = '|| UN_ANO || '
                                   AND PERIODO = ''' || UN_PERIODO || '''
                                   AND CONSECUTIVO = ' || UN_CONSECUTIVO || '
                                   AND CONCEPTO = ' || MI_CONCEPTO;

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION);         
              END IF;     
            END IF;  
        END IF;
      END LOOP ABONOS;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
    END;  

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
       PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTDABONOSVAL
         ); 

  END PR_CHARLESPESOAB;

  PROCEDURE PR_DISCRIMINARABONOS
    /*
    NAME              : PR_DISCRIMINARABONOS En access --> discriminarAbonos 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 20/09/2016
    TIME              : 17:35
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento encargado de eliminar del la tabla SP_D_ABONOS registros en los cuales la sumatoria 
                        del valorAct y el valorant sea diferente al valor registrado en la tabla SP_ABONOS.
    PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación.
                        UN_CODIGORUTA => Código de Ruta al cual pertenece el abono.
    @NAME:  discriminarAbonos
    @METHOD:  POST   
    */
    (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGORUTA       IN PCK_SUBTIPOS.TI_CODIGORUTA

    )
    AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_TOTALABONOS            PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTFACTURADO           PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTAB                  PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTDEUDA               PCK_SUBTIPOS.TI_DOBLE;
      MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
      MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE; 
      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR; 

    BEGIN
      MI_TOTALABONOS := 0;
      <<VALIDAR_ABONOS>>
      FOR MI_RS_VALIDA IN (SELECT SP_ABONOS.COMPANIA,
                                  SP_ABONOS.CICLO, 
                                  SP_ABONOS.CODIGORUTA, 
                                  SP_ABONOS.ANO, 
                                  SP_ABONOS.PERIODO, 
                                  SP_ABONOS.VALOR, 
                                  SP_ABONOS.CONSECUTIVO   
                           FROM SP_ABONOS 
                            INNER JOIN SP_CICLO
                                 ON SP_ABONOS.COMPANIA = SP_CICLO.COMPANIA
                                AND SP_ABONOS.ANO      = SP_CICLO.ANO 
                                AND SP_ABONOS.PERIODO  = SP_CICLO.PERIODO
                                AND SP_ABONOS.CICLO    = SP_CICLO.NUMERO   
                          WHERE SP_ABONOS.COMPANIA   = UN_COMPANIA
                            AND SP_ABONOS.CODIGORUTA = UN_CODIGORUTA   
                          GROUP BY SP_ABONOS.COMPANIA, 
                                   SP_ABONOS.CICLO, 
                                   SP_ABONOS.CODIGORUTA, 
                                   SP_ABONOS.ANO, 
                                   SP_ABONOS.PERIODO, 
                                   SP_ABONOS.VALOR, 
                                   SP_ABONOS.CONSECUTIVO  
                          ORDER BY SP_ABONOS.CONSECUTIVO)
      LOOP
        BEGIN
          SELECT SUM(VALORACT + VALORANT) 
            INTO MI_TOTAB 
            FROM SP_D_ABONOS  
           WHERE SP_D_ABONOS.COMPANIA    = UN_COMPANIA
             AND SP_D_ABONOS.CICLO       = MI_RS_VALIDA.CICLO
             AND SP_D_ABONOS.CODIGORUTA  = MI_RS_VALIDA.CODIGORUTA
             AND SP_D_ABONOS.ANO         = MI_RS_VALIDA.ANO
             AND SP_D_ABONOS.PERIODO     = MI_RS_VALIDA.PERIODO
             AND SP_D_ABONOS.CONSECUTIVO = MI_RS_VALIDA.CONSECUTIVO
             AND (((SP_D_ABONOS.CONCEPTO) BETWEEN 1 AND 48
              OR (SP_D_ABONOS.CONCEPTO) IN (201,202,203,204,205,206,207,247,248,249))
             AND (SP_D_ABONOS.CONCEPTO) NOT IN (12,17));
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_TOTAB := 0;
        END;

        IF NVL(MI_TOTAB,0) <> MI_RS_VALIDA.VALOR 
        THEN 
          BEGIN
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                           ''' AND CICLO = ' || MI_RS_VALIDA.CICLO || 
                             ' AND CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || 
                           ''' AND ANO = ' || MI_RS_VALIDA.ANO || ' 
                               AND PERIODO = ''' || MI_RS_VALIDA.PERIODO || 
                           ''' AND CONSECUTIVO = ' || MI_RS_VALIDA.CONSECUTIVO;
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_ABONOS',
                                                     UN_ACCION    => 'E',
                                                     UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
            MI_MSGERROR(1).VALOR := MI_RS_VALIDA.CONSECUTIVO;
            MI_MSGERROR(2).CLAVE := 'PERIODO';
            MI_MSGERROR(2).VALOR := MI_RS_VALIDA.PERIODO;
            MI_MSGERROR(3).CLAVE := 'ANIO';
            MI_MSGERROR(3).VALOR := MI_RS_VALIDA.ANO;
             PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DELDETABONO,
                 UN_REEMPLAZOS  => MI_MSGERROR,
                 UN_TABLAERROR => 'SP_D_ABONOS'  
               );
          END;
          IF MI_RS_VALIDA.VALOR > 0 
          THEN 
            BEGIN
              MI_CAMPOS := 'COMPANIA,'||
                           'CICLO,'||
                           'CODIGORUTA'||
                           ',ANO'||
                           ',PERIODO'||
                           ',CONCEPTO'||
                           ',VALOR'||
                           ',CONSECUTIVO'||
                           ',VALORACT'||
                           ',VALORANT';

              MI_VALORES:='SELECT '''||UN_COMPANIA||''', 
                                  '||MI_RS_VALIDA.CICLO||',
                                  '''||MI_RS_VALIDA.CODIGORUTA||''', 
                                  '||MI_RS_VALIDA.ANO||',
                                  '''||MI_RS_VALIDA.PERIODO||''' ,
                                  CONCEPTO,
                                  0, 
                                  '||MI_RS_VALIDA.CONSECUTIVO||' , 
                                  0,
                                  0
                             FROM SP_FACTURADO
                            WHERE COMPANIA   = ''' || UN_COMPANIA || '''
                              AND CICLO      = ' || MI_RS_VALIDA.CICLO || '
                              AND CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                              AND ANO        = ' || MI_RS_VALIDA.ANO || '
                              AND PERIODO    = ''' || MI_RS_VALIDA.PERIODO || '''';
              BEGIN 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_D_ABONOS',
                                                       UN_ACCION  => 'IS',
                                                       UN_CAMPOS  => MI_CAMPOS,
                                                       UN_VALORES => MI_VALORES);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
              END; 
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
              MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
              MI_MSGERROR(1).VALOR := MI_RS_VALIDA.CONSECUTIVO;
              MI_MSGERROR(2).CLAVE := 'PERIODO';
              MI_MSGERROR(2).VALOR := MI_RS_VALIDA.PERIODO;
              MI_MSGERROR(3).CLAVE := 'ANIO';
              MI_MSGERROR(3).VALOR := MI_RS_VALIDA.ANO;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGDETABONO,
                   UN_REEMPLAZOS  => MI_MSGERROR,
                   UN_TABLAERROR => 'SP_D_ABONOS'
                 );
            END;
            BEGIN
              SELECT SUM(VALOR_FACTURADO + VALORFINANT + VALORFINACT),
                     SUM(SP_FACTURADO.DEUDA) 
                INTO MI_TOTFACTURADO,
                     MI_TOTDEUDA 
                FROM SP_FACTURADO 
               WHERE SP_FACTURADO.COMPANIA   = UN_COMPANIA
                 AND SP_FACTURADO.CICLO      = MI_RS_VALIDA.CICLO
                 AND SP_FACTURADO.CODIGORUTA = UN_CODIGORUTA 
                 AND SP_FACTURADO.ANO        = MI_RS_VALIDA.ANO
                 AND SP_FACTURADO.PERIODO    = MI_RS_VALIDA.PERIODO
                 AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48
                  OR SP_FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                 AND SP_FACTURADO.CONCEPTO NOT IN (12,17);
            EXCEPTION WHEN NO_DATA_FOUND THEN 
              MI_TOTFACTURADO := 0;
              MI_TOTDEUDA := 0;
            END;

            BEGIN
              IF MI_TOTFACTURADO IS NOT NULL 
              THEN 
                IF MI_TOTDEUDA - MI_TOTALABONOS > 0 
                THEN 
                  IF MI_TOTDEUDA - MI_TOTALABONOS > MI_RS_VALIDA.VALOR 
                  THEN
                    MI_MERGEUSING :='SELECT COMPANIA,
                                            CONCEPTO,
                                            PERIODO,
                                            ANO,
                                            CODIGORUTA,
                                            CICLO,
                                            DEUDA,
                                            VALORABONOANT
                                      FROM SP_FACTURADO
                                     WHERE FACTURADO.COMPANIA   = ''' || UN_COMPANIA || '''
                                       AND FACTURADO.CICLO      = ' || MI_RS_VALIDA.CICLO || ' 
                                       AND FACTURADO.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                       AND FACTURADO.ANO        = ' || MI_RS_VALIDA.ANO || '
                                       AND FACTURADO.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || '''
                                       AND (FACTURADO.CONCEPTO BETWEEN 1 AND 48
                                        OR FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                       AND FACTURADO.CONCEPTO NOT IN (12,17) 
                                       AND FACTURADO.DEUDA <> 0';

                    MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.CONCEPTO   = VISTA.CONCEPTO
                                        AND TABLA.PERIODO    = VISTA.PERIODO
                                        AND TABLA.ANO        = VISTA.ANO
                                        AND TABLA.CODIGORUTA = VISTA.CODIGORUTA 
                                        AND TABLA.CICLO      = VISTA.CICLO';

                    MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORANT = ((VISTA.DEUDA - VISTA.VALORABONOANT)*'||MI_RS_VALIDA.VALOR||')/ 
                                        CASE WHEN ' || MI_TOTDEUDA || '-' || MI_TOTALABONOS || ' = 0 THEN
                                            ''1''
                                            ELSE 
                                            '||MI_TOTDEUDA||'-'||MI_TOTALABONOS|| '
                                          END;    
                                         WHERE TABLA.COMPANIA   = ''' || UN_COMPANIA || '''
                                           AND TABLA.CICLO      = ' || MI_RS_VALIDA.CICLO || '
                                           AND TABLA.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                           AND TABLA.ANO        = ' || MI_RS_VALIDA.ANO || '
                                           AND TABLA.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || ''' 
                                           AND (VISTA.CONCEPTO BETWEEN 1 AND 48 
                                            OR VISTA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                           AND VISTA.CONCEPTO NOT IN (12,17) 
                                           AND VISTA.DEUDA <>  0 
                                           AND TABLA.CONSECUTIVO = ' || MI_RS_VALIDA.CONSECUTIVO;                    

                    BEGIN
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO',
                                                             UN_ACCION      => 'MM',
                                                             UN_MERGEUSING  => MI_MERGEUSING,
                                                             UN_MERGEENLACE => MI_MERGEENLACE,
                                                             UN_MERGEEXISTE => MI_MERGEEXISTE);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                  ELSE 
                    MI_MERGEUSING := 'SELECT COMPANIA,
                                             CONCEPTO,
                                             PERIODO,
                                             ANO,
                                             CODIGORUTA,
                                             CICLO,
                                             DEUDA,
                                             VALORABONOANT
                                       FROM SP_FACTURADO
                                      WHERE FACTURADO.COMPANIA   = ''' || UN_COMPANIA || '''
                                        AND FACTURADO.CICLO      = ' || MI_RS_VALIDA.CICLO || ' 
                                        AND FACTURADO.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                        AND FACTURADO.ANO        = ' || MI_RS_VALIDA.ANO || '
                                        AND FACTURADO.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || '''
                                        AND (FACTURADO.CONCEPTO BETWEEN 1 AND 48
                                         OR FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                        AND FACTURADO.CONCEPTO NOT IN (12,17) 
                                        AND FACTURADO.DEUDA <> 0';

                    MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.CONCEPTO   = VISTA.CONCEPTO
                                        AND TABLA.PERIODO    = VISTA.PERIODO
                                        AND TABLA.ANO        = VISTA.ANO
                                        AND TABLA.CODIGORUTA = VISTA.CODIGORUTA 
                                        AND TABLA.CICLO      = VISTA.CICLO';

                    MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORANT = VISTA.DEUDA - VISTA.VALORABONOANT
                                         WHERE TABLA.COMPANIA    = ''' || UN_COMPANIA || '''
                                           AND TABLA.CICLO       = ' || MI_RS_VALIDA.CICLO || '
                                           AND TABLA.CODIGORUTA  = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                           AND TABLA.ANO         = ' || MI_RS_VALIDA.ANO || '
                                           AND TABLA.PERIODO     = ''' || MI_RS_VALIDA.PERIODO || ''' 
                                           AND (VISTA.CONCEPTO BETWEEN 1 AND 48 
                                            OR VISTA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                           AND VISTA.CONCEPTO NOT IN (12,17) 
                                           AND VISTA.DEUDA <> 0 
                                           AND TABLA.CONSECUTIVO = ' || MI_RS_VALIDA.CONSECUTIVO;                    
                    BEGIN 
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO',
                                                             UN_ACCION      => 'MM',
                                                             UN_MERGEUSING  => MI_MERGEUSING,
                                                             UN_MERGEENLACE => MI_MERGEENLACE,
                                                             UN_MERGEEXISTE => MI_MERGEEXISTE);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                    MI_MERGEUSING:='SELECT COMPANIA,
                                           CONCEPTO,
                                           PERIODO,
                                           ANO,
                                           CODIGORUTA,
                                           CICLO,
                                           DEUDA,
                                           VALORABONOANT
                                      FROM SP_FACTURADO
                                     WHERE FACTURADO.COMPANIA   = ''' || UN_COMPANIA || '''
                                       AND FACTURADO.CICLO      = ' || MI_RS_VALIDA.CICLO || ' 
                                       AND FACTURADO.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                       AND FACTURADO.ANO        = ' || MI_RS_VALIDA.ANO || '
                                       AND FACTURADO.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || '''
                                       AND (FACTURADO.CONCEPTO BETWEEN 1 AND 48
                                        OR FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                       AND FACTURADO.CONCEPTO NOT IN (12,17) 
                                       AND FACTURADO.DEUDA <> 0';

                    MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.CONCEPTO   = VISTA.CONCEPTO
                                        AND TABLA.PERIODO    = VISTA.PERIODO
                                        AND TABLA.ANO        = VISTA.ANO
                                        AND TABLA.CODIGORUTA = VISTA.CODIGORUTA 
                                        AND TABLA.CICLO      = VISTA.CICLO';

                    MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORACT = (VISTA.VALOR_FACTURADO * '||MI_RS_VALIDA.VALOR||'-('||MI_TOTDEUDA||'-'||MI_TOTALABONOS||'))/'||MI_TOTFACTURADO||'
                                         WHERE TABLA.COMPANIA   = '''||UN_COMPANIA||'''
                                           AND TABLA.CICLO      = '||MI_RS_VALIDA.CICLO||'
                                           AND TABLA.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                           AND TABLA.ANO        = ' || MI_RS_VALIDA.ANO || '
                                           AND TABLA.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || ''' 
                                           AND (VISTA.CONCEPTO BETWEEN 1 AND 48
                                            OR VISTA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                           AND VISTA.CONCEPTO NOT IN (12,17) 
                                           AND VISTA.DEUDA <> 0 
                                           AND TABLA.CONSECUTIVO = ' || MI_RS_VALIDA.CONSECUTIVO;                    
                    BEGIN 
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO',
                                                             UN_ACCION      => 'MM',
                                                             UN_MERGEUSING  => MI_MERGEUSING,
                                                             UN_MERGEENLACE => MI_MERGEENLACE,
                                                             UN_MERGEEXISTE => MI_MERGEEXISTE);  
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                  END IF;
                ELSE
                  MI_MERGEUSING := 'SELECT COMPANIA,
                                           CONCEPTO,
                                           PERIODO,
                                           ANO,
                                           CODIGORUTA,
                                           CICLO,
                                           DEUDA,
                                           VALORABONOANT
                                     FROM SP_FACTURADO
                                    WHERE FACTURADO.COMPANIA   = ''' || UN_COMPANIA || '''
                                      AND FACTURADO.CICLO      = ' || MI_RS_VALIDA.CICLO || ' 
                                      AND FACTURADO.CODIGORUTA = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                      AND FACTURADO.ANO        = ' || MI_RS_VALIDA.ANO || '
                                      AND FACTURADO.PERIODO    = ''' || MI_RS_VALIDA.PERIODO || '''
                                      AND (FACTURADO.CONCEPTO BETWEEN 1 AND 48
                                       OR FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                      AND FACTURADO.CONCEPTO NOT IN (12,17) 
                                      AND FACTURADO.DEUDA <> 0';

                    MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.CONCEPTO   = VISTA.CONCEPTO
                                        AND TABLA.PERIODO    = VISTA.PERIODO
                                        AND TABLA.ANO        = VISTA.ANO
                                        AND TABLA.CODIGORUTA = VISTA.CODIGORUTA 
                                        AND TABLA.CICLO      = VISTA.CICLO';

                    MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORACT = (VISTA.VALOR_FACTURADO-VISTA.VALORABONOACT)*'||MI_RS_VALIDA.VALOR||')/'||MI_TOTFACTURADO||'-('||MI_TOTALABONOS||'-'||MI_TOTDEUDA||')
                                         WHERE TABLA.COMPANIA    = ''' || UN_COMPANIA || '''
                                           AND TABLA.CICLO       = ' || MI_RS_VALIDA.CICLO || '
                                           AND TABLA.CODIGORUTA  = ''' || MI_RS_VALIDA.CODIGORUTA || '''
                                           AND TABLA.ANO         = ' || MI_RS_VALIDA.ANO || '
                                           AND TABLA.PERIODO     = ''' || MI_RS_VALIDA.PERIODO || ''' 
                                           AND (VISTA.CONCEPTO BETWEEN 1 AND 48 
                                            OR VISTA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)) 
                                           AND VISTA.CONCEPTO NOT IN (12,17) 
                                           AND VISTA.DEUDA <> 0
                                           AND TABLA.CONSECUTIVO = ' || MI_RS_VALIDA.CONSECUTIVO;                    
                    BEGIN  
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO',
                                                             UN_ACCION      => 'MM',
                                                             UN_MERGEUSING  => MI_MERGEUSING,
                                                             UN_MERGEENLACE => MI_MERGEENLACE,
                                                             UN_MERGEEXISTE => MI_MERGEEXISTE);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                END IF;    
              END IF;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
              MI_MSGERROR(1).CLAVE := 'CODRUTA';
              MI_MSGERROR(1).VALOR := MI_RS_VALIDA.CODIGORUTA;
              MI_MSGERROR(2).CLAVE := 'PERIODO';
              MI_MSGERROR(2).VALOR := MI_RS_VALIDA.PERIODO;
              MI_MSGERROR(3).CLAVE := 'ANIO';
              MI_MSGERROR(3).VALOR := MI_RS_VALIDA.ANO;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERR_FACSERP_ACTFACTURADO, 
                   UN_REEMPLAZOS  => MI_MSGERROR,
                   UN_TABLAERROR => 'SP_FACTURADO'
                 );
            END;
           END IF;
          END IF;
          MI_TOTALABONOS := MI_TOTALABONOS + MI_RS_VALIDA.VALOR;
          PCK_SERVICIOS_PUBLICOS_COM2.PR_CHARLESPESOAB(UN_COMPANIA    => UN_COMPANIA,
                                                       UN_CICLO       => MI_RS_VALIDA.CICLO,
                                                       UN_CODIGORUTA  => MI_RS_VALIDA.CODIGORUTA,
                                                       UN_ANO         => MI_RS_VALIDA.ANO,
                                                       UN_PERIODO     => MI_RS_VALIDA.PERIODO,
                                                       UN_CONSECUTIVO => MI_RS_VALIDA.CONSECUTIVO);
      END LOOP VALIDAR_ABONOS; 

  END PR_DISCRIMINARABONOS;


  PROCEDURE PR_ABORECAUDOS
  /*
    NAME              : PR_ABORECAUDOS En access --> aborecaudos 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 27/09/2016
    TIME              : 12:20
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento que  actualiza los campos de VALORREPORTADO, CUPONESREPORTADOS,VALORREGISTRADO , CUPONESREGISTRADOS 
                        de la tabla SP_RECAUDOS
    PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                        UN_FECHA_INICIAL => Fecha inicial del cuadre de recaudos.
                        UN_FECHA_FINAL   => Fecha final del cuadre de recaudos. 
                        UN_STRBANCO      => Banco relacionado en el abono.
    @NAME:  actualizarAbonoRecaudos
    @METHOD:  PUT   

    */
    (

    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA_INICIAL         IN DATE,
    UN_FECHA_FINAL           IN DATE,  
    UN_STRBANCO              IN PCK_SUBTIPOS.TI_BANCO

    )

    AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_CONDBANCO              VARCHAR2(200 CHAR);  
      MI_RS                     SYS_REFCURSOR; 

    BEGIN
      IF UN_STRBANCO IS NULL 
      THEN 
        MI_CONDBANCO := ' ';
      ELSE
        MI_CONDBANCO := ' AND BANCO = ''' || UN_STRBANCO || '''';
      END IF;    

      BEGIN
        MI_CAMPOS := 'VALORREPORTADO = 0, 
                      CUPONESREPORTADOS = 0, 
                      VALORREGISTRADO = 0, 
                      CUPONESREGISTRADOS = 0 ';

        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' ' || 
                        MI_CONDBANCO || 
                        ' AND NUMEROPAQUETE = ''888'' 
                          AND TRUNC(FECHA) BETWEEN TO_DATE(''' || UN_FECHA_INICIAL || ''', ''DD/MM/YYYY'')  AND  TO_DATE(''' || UN_FECHA_FINAL || ''', ''DD/MM/YYYY'')';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_RECAUDOS',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END; 

      <<VALORES_ABONO>>
      FOR MI_RS IN ( SELECT COMPANIA, 
                            TO_CHAR(FECHA, 'DD/MM/YYYY') FECHA,
                            BANCO, 
                            '888' AS NUMEROPAQUETE, 
                            SUM(VALOR) AS SUMAPAGO,
                            COUNT(CONSECUTIVO) AS CUENTAC  
                       FROM SP_ABONOS  
                      WHERE COMPANIA =UN_COMPANIA 
                        AND FECHA BETWEEN UN_FECHA_INICIAL  AND  UN_FECHA_FINAL 
                    GROUP BY COMPANIA, 
                             FECHA, 
                             BANCO
                          )

      LOOP
        BEGIN
          MI_CAMPOS := 'VALORREPORTADO = ' || MI_RS.SUMAPAGO || 
                     ', CUPONESREPORTADOS = ' || MI_RS.CUENTAC || 
                     ', VALORREGISTRADO = ' || MI_RS.SUMAPAGO || 
                     ', CUPONESREGISTRADOS = ' || MI_RS.CUENTAC;

          MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                        ''' AND FECHA = TO_DATE(''' || MI_RS.FECHA || ''',''DD/MM/YYYY'')' || 
                        '   AND BANCO = ''' || MI_RS.BANCO || '''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_RECAUDOS',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END; 
      END LOOP VALORES_ABONO;

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
       PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTCUPONESRECAUDOS
         );

  END PR_ABORECAUDOS;


  PROCEDURE PR_PASAABONOSRECAUDOS
   /*
    NAME              : PR_PASAABONOSRECAUDOS En access --> pasaabonosrecaudos 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 27/09/2016
    TIME              : 12:20
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento que se encarga de elmininar un registro de la tabla SP_D_RECAUDO y lo inserta en la tabla SP_RECAUDOS
    PARAMETERS        : UN_COMPANIA => Compañia de ingreso a la aplicación
                        UN_STRFECHA => Fecha del abono. 
                        UN_STRBANCO => Banco relacionado en el abono.
                        UN_USUARIO  => Usuario que realiza la operación. 
    @NAME:  pasarAbonosRecaudos
    @METHOD:  POST   
    */
    (

    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_STRFECHA        IN DATE,
    UN_STRBANCO        IN PCK_SUBTIPOS.TI_BANCO,
    UN_USUARIO         IN VARCHAR2  

    )

    AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_FILES                  PCK_SUBTIPOS.TI_ENTERO;
      MI_SUMADEVALORACT         PCK_SUBTIPOS.TI_DOBLE;
      MI_SUMADEVALORANT         PCK_SUBTIPOS.TI_DOBLE;
      MI_RS                     SYS_REFCURSOR; 
      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR; 

    BEGIN
      BEGIN
        BEGIN
          MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                        ''' AND TO_CHAR(FECHA,''DD/MM/YYYY'') = ''' || UN_STRFECHA || 
                        ''' AND BANCO = ''' || UN_STRBANCO || 
                        ''' AND NUMEROPAQUETE = ''888''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_D_RECAUDO',
                                                 UN_ACCION    => 'E',
                                                 UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := UN_STRFECHA;
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := UN_STRBANCO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ELIMINADRECAUDO, 
           UN_REEMPLAZOS  => MI_MSGERROR,
           UN_TABLAERROR => 'SP_D_RECAUDO' 
         );
      END;
      BEGIN 
           SELECT SUM(SP_D_ABONOS.VALORACT),
                  SUM(SP_D_ABONOS.VALORANT)
                  INTO MI_SUMADEVALORACT,
                       MI_SUMADEVALORANT
             FROM SP_ABONOS 
                INNER JOIN SP_D_ABONOS 
                   ON SP_ABONOS.COMPANIA    = SP_D_ABONOS.COMPANIA
                  AND SP_ABONOS.CICLO       = SP_D_ABONOS.CICLO
                  AND SP_ABONOS.CODIGORUTA  = SP_D_ABONOS.CODIGORUTA
                  AND SP_ABONOS.ANO         = SP_D_ABONOS.ANO 
                  AND SP_ABONOS.PERIODO     = SP_D_ABONOS.PERIODO
                  AND SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO 
            WHERE SP_ABONOS.COMPANIA = UN_COMPANIA
              AND TO_CHAR(SP_ABONOS.FECHA,'DD/MM/YYYY') = UN_STRFECHA
              AND SP_ABONOS.BANCO = UN_STRBANCO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_SUMADEVALORACT:=0;
        MI_SUMADEVALORANT :=0;
      END;

      IF MI_SUMADEVALORANT = 0 AND MI_SUMADEVALORACT = 0
      THEN 
        BEGIN 
          SELECT COUNT (*) INTO MI_FILES
            FROM SP_RECAUDOS 
           WHERE COMPANIA      = UN_COMPANIA 
             AND TO_CHAR(FECHA,'DD/MM/YYYY') = UN_STRFECHA
             AND BANCO         = UN_STRBANCO
             AND NUMEROPAQUETE ='888';  
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_FILES:=0;
        END;     

        IF MI_FILES > 0 
        THEN
          BEGIN
            MI_CAMPOS := 'COMPANIA,'||
                         'FECHA,'||
                         'BANCO'||
                         ',NUMEROPAQUETE'||
                         ',BARRAS'||
                         ',TIPO'||
                         ',VALORREPORTADO'||
                         ',CUPONESREPORTADOS'||
                         ',VALORREGISTRADO'||
                         ',CUPONESREGISTRADOS'||
                         ',DIFERENCIACUPONES'||
                         ',DIFERENCIAVALORES'||
                         ',USUARIO'||
                         ',COMENTARIOS';

            MI_VALORES := '''' || UN_COMPANIA || 
                          ''', ''' || UN_STRFECHA || 
                          ''',''' || UN_STRBANCO || 
                          ''', ''888'',
                          0, 1, 0, 0, 0, 0, 0, 0,''' 
                          || UN_USUARIO || 
                          ''', ''PAGOS REALIZADOS POR ABONOS''' ;
            BEGIN     
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_RECAUDOS',
                                                     UN_ACCION  => 'I',
                                                     UN_CAMPOS  => MI_CAMPOS,
                                                     UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
           PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGPASABONREC,
               UN_TABLAERROR => 'SP_RECAUDOS' 
             );
          END;
        END IF;  
        <<DATOS_ABONOS>>
        FOR MI_RS IN (  SELECT SP_ABONOS.FECHA,
                               SP_ABONOS.BANCO,
                               SP_D_ABONOS.CONCEPTO,
                               SUM(SP_D_ABONOS.VALORACT) AS SUMADEVALORACT,
                               SUM(SP_D_ABONOS.VALORANT) AS SUMADEVALORANT  
                          FROM SP_ABONOS 
                         INNER JOIN SP_D_ABONOS 
                            ON SP_ABONOS.COMPANIA    = SP_D_ABONOS.COMPANIA   
                           AND SP_ABONOS.CICLO       = SP_D_ABONOS.CICLO
                           AND SP_ABONOS.CODIGORUTA  = SP_D_ABONOS.CODIGORUTA  
                           AND SP_ABONOS.ANO         = SP_D_ABONOS.ANO
                           AND SP_ABONOS.PERIODO     = SP_D_ABONOS.PERIODO  
                           AND SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO
                         WHERE SP_ABONOS.COMPANIA = UN_COMPANIA 
                           AND SP_ABONOS.FECHA    = UN_STRFECHA
                           AND SP_ABONOS.BANCO    = UN_STRBANCO
                          GROUP BY SP_ABONOS.FECHA, 
                                   SP_ABONOS.BANCO, 
                                   SP_D_ABONOS.CONCEPTO)
        LOOP
          BEGIN
            MI_CAMPOS := 'COMPANIA,'||
                         'FECHA,'||
                         'BANCO'||
                         ',NUMEROPAQUETE'||
                         ',CONCEPTO'||
                         ',VALORDEUDA'||
                         ',VALORPAGOPERIODO'||
                         ',VALORABONOACT'||
                         ',VALORABONOANT'||
                         ',VALORFINACT'||
                         ',VALORFINANT'||
                         ',CREDITOABONADO';

            MI_VALORES := '''' || UN_COMPANIA || 
                          ''', ''' || UN_STRFECHA || ''', '''         
                          || UN_STRBANCO || ''', ''888'', ''' 
                          || MI_RS.CONCEPTO || ''',0,0,' 
                          || NVL(MI_RS.SUMADEVALORANT,0) || ',0,0,0';

            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_D_RECAUDO',
                                                     UN_ACCION  => 'I',
                                                     UN_CAMPOS  => MI_CAMPOS,
                                                     UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            MI_MSGERROR(1).CLAVE := 'CONCEPTO';
            MI_MSGERROR(1).VALOR := MI_RS.CONCEPTO;
            MI_MSGERROR(2).CLAVE := 'FECHA';
            MI_MSGERROR(2).VALOR := UN_STRFECHA; 
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGDREC,
               UN_REEMPLAZOS  => MI_MSGERROR,
               UN_TABLAERROR => 'SP_D_RECAUDO' 
             );
          END;
        END LOOP DATOS_ABONOS;

        PCK_SERVICIOS_PUBLICOS_COM2.PR_ABORECAUDOS(UN_COMPANIA      => UN_COMPANIA,
                                                   UN_FECHA_INICIAL => UN_STRFECHA,         
                                                   UN_FECHA_FINAL   => UN_STRFECHA,         
                                                   UN_STRBANCO      => UN_STRBANCO);           

      ELSE
        BEGIN
          BEGIN
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                            ''' AND TO_CHAR(FECHA,''DD/MM/YYYY'') = ''' || UN_STRFECHA || ''' 
                                AND BANCO = ''' || UN_STRBANCO || 
                            ''' AND NUMEROPAQUETE = ''888''';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_RECAUDOS',
                                                   UN_ACCION    => 'E',
                                                   UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := UN_STRFECHA;
          MI_MSGERROR(2).CLAVE := 'BANCO';
          MI_MSGERROR(2).VALOR := UN_STRBANCO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ELIMINARECAUDOS,
             UN_REEMPLAZOS  => MI_MSGERROR,
             UN_TABLAERROR => 'SP_RECAUDOS' 
           );
        END;
      END IF;

    --SET       PeriodosAtraso = PeriodosAtraso - " & ROUND((VlrAbono / (rs!TotDeuda / rs!PERIODOSATRASO)), 0)

  END PR_PASAABONOSRECAUDOS;

  FUNCTION FC_REGISTRARABONO
     /*
    NAME              : FC_REGISTRARABONO En access --> RegistrarAbono 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/09/2016
    TIME              : 09:35
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento que se encarga de actualizar los valores de VALORABONOACT y VALORABONOANT de la tabla SP_FACTURADO
    PARAMETERS        : UN_COMPANIA    	=> Compañia de ingreso a la aplicación
                        UN_CODIGORUTA	  => Código de Ruta del abono.
                        UN_PERIODO	    => Número del periodo del abono.
                        UN_FECHA        => Fecha del abono.
                        UN_BANCO        => Código del banco donde se registra el abono.
                        UN_USUARIO      => Usuario que realiza el registro.
                        UN_ANO    	    => Año en el cual se realiza el abono
                        UN_CICLO        => Ciclo al que pertenece el abono.
                        UN_CONSECUTIVO  => Número del consecutivo del abono.
                        UN_VLRABONO			=> Valor del abono realizado.
    @NAME:  registrarAbono
    @METHOD:  GET   
    */
    (

    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGORUTA          IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_PERIODO             IN PCK_SUBTIPOS.TI_PERIODO,
    UN_FECHA               IN DATE,
    UN_BANCO               IN PCK_SUBTIPOS.TI_BANCO,
    UN_USUARIO             IN VARCHAR2,
    UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_CICLO               IN PCK_SUBTIPOS.TI_CICLO,
    UN_CONSECUTIVO         IN SP_ABONOS.CONSECUTIVO%TYPE,
    UN_VLRABONO            IN PCK_SUBTIPOS.TI_DOBLE

    )

    RETURN VARCHAR2 AS
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
      MI_MERGEREXISTE           PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE; 
      MI_MENSAJERETURN          VARCHAR2(100 CHAR);   
      MI_PERIODOSATRASO         PCK_SUBTIPOS.TI_ENTERO;
      MI_TOTFACTURADO           PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTDEUDA               PCK_SUBTIPOS.TI_DOBLE;
      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN
      BEGIN 
        SELECT SP_USUARIO.PERIODOSATRASO,
               SUM(SP_FACTURADO.VALOR_FACTURADO + SP_FACTURADO.DEUDA), 
               SUM(SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT)
          INTO MI_PERIODOSATRASO,
               MI_TOTFACTURADO,
               MI_TOTDEUDA
          FROM SP_USUARIO 
         INNER JOIN SP_FACTURADO 
            ON SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
           AND SP_USUARIO.CICLO      = SP_FACTURADO.CICLO
           AND SP_USUARIO.ANO        = SP_FACTURADO.ANO 
           AND SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
           AND SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO 
         WHERE SP_FACTURADO.COMPANIA   = UN_COMPANIA
           AND SP_FACTURADO.CICLO      = UN_CICLO
           AND SP_FACTURADO.CODIGORUTA = UN_CODIGORUTA
           AND SP_FACTURADO.ANO        = UN_ANO
           AND SP_FACTURADO.PERIODO    = UN_PERIODO
           AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 49 
            OR SP_FACTURADO.CONCEPTO BETWEEN 201 AND 207 
            OR SP_FACTURADO.CONCEPTO BETWEEN 246 AND 249) 
         GROUP BY SP_FACTURADO.CODIGORUTA, 
                  SP_USUARIO.PERIODOSATRASO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_PERIODOSATRASO := 0;
        MI_TOTFACTURADO   := 0;
        MI_TOTDEUDA       := 0;
      END;

      IF MI_TOTFACTURADO > 0 
      THEN 
        IF UN_VLRABONO > 0 
        THEN 
          IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                   UN_NOMBRE    => 'ABONAR EN ORDEN POR CONCEPTO',
                                   UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS(),
                                   UN_FECHA_PAR => SYSDATE) = 'SI' 
          THEN
             PCK_SERVICIOS_PUBLICOS_COM2.PR_ABONOPRIORIDAD(UN_COMPANIA    => UN_COMPANIA,
                                                           UN_CICLO       => UN_CICLO,
                                                           UN_CODIGORUTA  => UN_CODIGORUTA,
                                                           UN_PERIODO     => UN_PERIODO,
                                                           UN_CONSECUTIVO => UN_CONSECUTIVO,
                                                           UN_ANO         => UN_ANO,
                                                           UN_DBLVALOR    => UN_VLRABONO,
                                                           UN_DBLDEUDA    => MI_TOTDEUDA);
          ELSE
             PCK_SERVICIOS_PUBLICOS_COM2.PR_DISCRIMINARABONOS(UN_COMPANIA   => UN_COMPANIA,
                                                              UN_CODIGORUTA => UN_CODIGORUTA);
          END IF; 

          BEGIN
            BEGIN
              MI_MERGEUSING := 'SELECT SP_D_ABONOS.COMPANIA,
                                     SP_D_ABONOS.CONCEPTO,
                                     SP_D_ABONOS.PERIODO,
                                     SP_D_ABONOS.ANO,
                                     SP_D_ABONOS.CODIGORUTA,
                                     SP_D_ABONOS.CICLO,
                                     SP_D_ABONOS.VALORACT,
                                     SP_D_ABONOS.VALORANT
                                FROM SP_D_ABONOS
                               WHERE SP_D_ABONOS.COMPANIA   = ''' || UN_COMPANIA || '''
                                 AND SP_D_ABONOS.CICLO      = '   || UN_CICLO || '
                                 AND SP_D_ABONOS.CODIGORUTA = ''' || UN_CODIGORUTA || '''
                                 AND SP_D_ABONOS.ANO        = '   || UN_ANO || '
                                 AND SP_D_ABONOS.PERIODO    = ''' || UN_PERIODO || '''
                                AND SP_D_ABONOS.CONSECUTIVO = '   || UN_CONSECUTIVO;

              MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                                  AND TABLA.CICLO      = VISTA.CICLO
                                  AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                                  AND TABLA.ANO        = VISTA.ANO 
                                  AND TABLA.PERIODO    = VISTA.PERIODO
                                  AND TABLA.CONCEPTO   = VISTA.CONCEPTO';

              MI_MERGEREXISTE :=  'UPDATE SET TABLA.VALORABONOACT = TABLA.VALORABONOACT + VISTA.VALORACT,
                                              TABLA.VALORABONOANT = TABLA.VALORABONOANT + VISTA.VALORANT    
                                   WHERE TABLA.COMPANIA   = ''' || UN_COMPANIA || '''
                                     AND TABLA.CICLO      = '   || UN_CICLO || '
                                     AND TABLA.CODIGORUTA = ''' || UN_CODIGORUTA || '''
                                     AND TABLA.ANO        = '   || UN_ANO || '
                                     AND TABLA.PERIODO    = ''' || UN_PERIODO || '''';                    

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SP_FACTURADO',
                                                     UN_ACCION      => 'MM',
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEREXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            MI_MSGERROR(1).CLAVE := 'CODRUTA';
            MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
            MI_MSGERROR(2).CLAVE := 'PERIODO';
            MI_MSGERROR(2).VALOR := UN_PERIODO;
            MI_MSGERROR(3).CLAVE := 'ANIO';
            MI_MSGERROR(3).VALOR := UN_ANO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTABONFACT,
               UN_REEMPLAZOS  => MI_MSGERROR,
               UN_TABLAERROR => 'SP_FACTURADO' 
             );
          END;

          PCK_SERVICIOS_PUBLICOS_COM2.PR_PASAABONOSRECAUDOS(UN_COMPANIA => UN_COMPANIA,
                                                            UN_STRFECHA => UN_FECHA,
                                                            UN_STRBANCO => UN_BANCO,
                                                            UN_USUARIO  => UN_USUARIO);

          IF MI_PERIODOSATRASO > 0 
          AND MI_TOTDEUDA > 0 
          THEN 
            IF UN_VLRABONO < MI_TOTDEUDA 
            THEN
              BEGIN
                MI_CAMPOS := 'PERIODOSATRASO = PERIODOSATRASO - ROUND((' || UN_VLRABONO || '/(' || MI_TOTDEUDA || '/' || MI_PERIODOSATRASO || ')),0)';
                MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA || 
                               ''' AND CICLO      = ''' || UN_CICLO || 
                               ''' AND CODIGORUTA = ''' || UN_CODIGORUTA || 
                               ''' AND ANO        = ''' || UN_ANO || ''' 
                                   AND PERIODO    = ''' || UN_PERIODO || '''';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION); 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
              END;
            ELSE
              BEGIN
                MI_CAMPOS:='PERIODOSATRASO = 0 ';
                MI_CONDICION := ' COMPANIA        = ''' || UN_COMPANIA || 
                               ''' AND CICLO      = ''' || UN_CICLO || 
                               ''' AND CODIGORUTA = ''' || UN_CODIGORUTA || 
                               ''' AND ANO        = ''' || UN_ANO || ''' 
                                   AND PERIODO    = ''' || UN_PERIODO || '''';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION); 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
              END;
            END IF;
          END IF; 
        END IF;    

      ELSE  
        MI_MENSAJERETURN := 'El usuario no tiene pendiente factura por cobrar';
        RETURN MI_MENSAJERETURN;
      END IF;
      MI_MENSAJERETURN := 'Abono Registrado';
      RETURN MI_MENSAJERETURN;
        /* CALCULOFACTURACION(COMPANIA,CICLO,RUTA)*/
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
         MI_MSGERROR(1).CLAVE := 'CODRUTA';
         MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
         PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTPERATRASO,
             UN_REEMPLAZOS  => MI_MSGERROR,
             UN_TABLAERROR => 'SP_USUARIO'
           );

  END FC_REGISTRARABONO;


  FUNCTION FC_ACTUALIZAPAGOTERCERIZADOS
    /*
    NAME              : FC_ACTUALIZAPAGOTERCERIZADOS En access --> ActPagoTer
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Maritza Caceres Bonilla
    DATE MIGRADOR     : 29/08/2016
    TIME              : 02:12
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Funcion que actualiza el pago de una Factura en la tabla HISTORIA_EXTERNA
    PARAMETERS        : UN_COMPANIA    	=> Compañia de ingreso a la aplicación.
                        UN_CICLO        => Ciclo al que pertenece el acta.
                        UN_CODIGORUTA   => Código de Ruta Inicial seleccionado.
                        UN_ANO        	=> Año en el cual se realiza el abono.
                        UN_PERIODO	    => Número del periodo del abono.
                        UN_FECHA        => Fecha del abono.
                        UN_BANCO        => Código del banco en el que está registrado el abono.
                        UN_PAQUETE      => Código del paquete .
                        UN_REVERSA    	=> Indicador de si maneja reversar abonos.
                        UN_PAGODOBLE    => Indicador de si maneja pago doble.
                        UN_TERCER		    => Valor para indicar si maneja tercero.
    @NAME:  actualizarPagoTercerizados
    @METHOD: GET
    */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO           IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA      IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO,
    UN_FECHA           IN DATE,
    UN_BANCO           IN SP_BANCOS.CODIGO%TYPE,
    UN_PAQUETE         IN SP_HISTORIA_CONVENIOS.PAQUETE_PAGO%TYPE,
    UN_REVERSA         IN PCK_SUBTIPOS.TI_LOGICO,
    UN_PAGODOBLE       IN PCK_SUBTIPOS.TI_LOGICO,
    UN_TERCER          IN VARCHAR2
    )
  RETURN NUMBER
    AS
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_ACTUALIZAPAGOTER   PCK_SUBTIPOS.TI_LOGICO;
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    IF UN_TERCER = 'SI'
    THEN
      MI_CONDICION := '    COMPANIA   = ''' || UN_COMPANIA || '''
                       AND CICLO      = ''' || UN_CICLO || '''
                       AND CODIGORUTA = ''' || UN_CODIGORUTA || '''
                       AND ANO  = ' || UN_ANO || '
                       AND PERIODO  = ''' || UN_PERIODO || '''';

      IF UN_REVERSA = -1
      THEN
        IF UN_PAGODOBLE = -1
        THEN
            MI_CAMPOS := 'FECHA_PAGO_DOBLE = NULL,
                          BANCO_PAGO_DOBLE = NULL,
                          PAQUETE_PAGO_DOBLE = NULL,
                          REVERSADO = -1';

             MI_CONDICION := MI_CONDICION || ' AND TRUNC(FECHA_PAGO_DOBLE) =  TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                            AND BANCO_PAGO_DOBLE = ''' || UN_BANCO || '''
                                            AND PAQUETE_PAGO_DOBLE = ''' || UN_PAQUETE || '''';
        ELSE
            MI_CAMPOS := 'FECHA_PAGO   = NULL,
                          BANCO_PAGO   = NULL,
                          PAQUETE_PAGO = NULL,
                          REVERSADO    = -1';

            MI_CONDICION := MI_CONDICION || ' AND TRUNC(FECHA_PAGO) =  TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                           AND  BANCO_PAGO = ''' || UN_BANCO || '''
                                           AND  PAQUETE_PAGO = ''' || UN_PAQUETE || '''';

        END IF;
      ELSE
            MI_CONDICION := MI_CONDICION || ' AND NVL(BANCO_PAGO,'' '') = '' ''
                                            AND FECHA_PAGO IS NULL ';

            IF UN_PAGODOBLE = -1
            THEN
              MI_CAMPOS := MI_CAMPOS || 'FECHA_PAGO_DOBLE =  TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                       BANCO_PAGO_DOBLE = ''' || UN_BANCO || ''',
                                       PAQUETE_PAGO_DOBLE = ''' || UN_PAQUETE || ''' ';
            ELSE
              MI_CAMPOS := MI_CAMPOS || 'FECHA_PAGO =  TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                       BANCO_PAGO = ''' || UN_BANCO || ''',
                                       PAQUETE_PAGO = ''' || UN_PAQUETE || ''' ';
            END IF;
          /*  BEGIN
            MI_STRVAL:='SELECT SUM(SP_HISTORIA_CONVENIOS.TOTAL) AS TOTAL
                        FROM SP_HISTORIA_CONVENIOS ' || MI_CONDICION;
            EXECUTE IMMEDIATE MI_STRVAL INTO MI_VALORTOTAL;

            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VALORCONVENIOS:=0;
            END;
            MI_VALORCONVENIOS:=MI_VALORTOTAL;     */
      END IF;
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_HISTORIA_EXTERNA',
                                    UN_ACCION    => 'M',
                                    UN_CAMPOS    => MI_CAMPOS,
                                    UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
      MI_ACTUALIZAPAGOTER := -1;
    END IF;

    RETURN MI_ACTUALIZAPAGOTER;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'PERIODO';
        MI_MSGERROR(2).VALOR := UN_PERIODO;
        MI_MSGERROR(3).CLAVE := 'ANIO';
        MI_MSGERROR(3).VALOR := UN_ANO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTHISTCONVENIOS,
             UN_REEMPLAZOS  => MI_MSGERROR,
             UN_TABLAERROR => 'SP_HISTORIA_CONVENIOS'
           );

  END FC_ACTUALIZAPAGOTERCERIZADOS;


  FUNCTION FC_ACTUALIZARPAGOCONVENIOS
    /*
    NAME              : FC_ACTUALIZARPAGOCONVENIOS En access --> ActPagoConve
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Maritza Caceres Bonilla
    DATE MIGRADOR     : 29/08/2016
    TIME              : 03:25
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Funcion que actualiza el pago de una Factura en la tabla HISTORIA_EXTERNA
    PARAMETERS        : UN_COMPANIA    	=> Compañia de ingreso a la aplicación.
                        UN_CICLO        => Ciclo al que pertenece el acta.
                        UN_CODIGORUTA   => Código de Ruta Inicial seleccionado.
                        UN_ANO        	=> Año en el cual se realiza el abono.
                        UN_PERIODO	    => Número del periodo del abono.
                        UN_FECHA        => Fecha del abono.
                        UN_BANCO        => Código del banco en el que está registrado el abono.
                        UN_PAQUETE      => Código del paquete .
                        UN_REVERSA    	=> Indicador de si maneja reversar abonos.
                        UN_PAGODOBLE    => Indicador de si maneja pago doble.
                        UN_CONVENIO     => Valor para indicar si maneja convenios.
    @NAME:  actualizarPagoConvenios
    @METHOD: GET
    */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO           IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA      IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO,
    UN_FECHA           IN DATE,
    UN_BANCO           IN SP_BANCOS.CODIGO%TYPE,
    UN_PAQUETE         IN SP_HISTORIA_CONVENIOS.PAQUETE_PAGO%TYPE,
    UN_REVERSA         IN PCK_SUBTIPOS.TI_LOGICO,
    UN_PAGODOBLE       IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CONVENIO        IN VARCHAR2

  )
  RETURN NUMBER
  AS
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_ACTUALIZAPAGOCONV    PCK_SUBTIPOS.TI_LOGICO;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_STRVAL               VARCHAR2(420 CHAR);
    MI_VALORCONVENIOS       PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORTOTAL           PCK_SUBTIPOS.TI_DOBLE;
    MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_ACTUALIZAPAGOCONV := 0;

    IF UN_CONVENIO = 'SI'
    THEN
       MI_CONDICION := '   COMPANIA   = '''|| UN_COMPANIA||'''
                       AND CICLO      = '''||UN_CICLO||'''
                       AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                       AND ANO        = '||UN_ANO||'
                       AND PERIODO    = '''||UN_PERIODO||'''
                       AND NOCOBRAR   = 0 ';

      IF UN_REVERSA = -1
      THEN
        IF UN_PAGODOBLE = -1
        THEN
            MI_CAMPOS := 'FECHA_PAGO_DOBLE   = NULL,
                          BANCO_PAGO_DOBLE   = NULL,
                          PAQUETE_PAGO_DOBLE = NULL,
                          REVERSADO          = -1';

             MI_CONDICION := MI_CONDICION || 'AND FECHA_PAGO_DOBLE   = TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                              AND BANCO_PAGO_DOBLE   = '''||UN_BANCO||'''
                                              AND PAQUETE_PAGO_DOBLE = '''||UN_PAQUETE||'''';
        ELSE
            MI_CAMPOS := 'FECHA_PAGO   = NULL,
                          BANCO_PAGO   = NULL,
                          PAQUETE_PAGO = NULL,
                          REVERSADO    = -1';

            MI_CONDICION := MI_CONDICION || 'AND TRUNC(FECHA_PAGO)   = TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                             AND BANCO_PAGO   = '''||UN_BANCO||'''
                                             AND PAQUETE_PAGO = '''||UN_PAQUETE||'''';

        END IF;
      ELSE
            MI_CONDICION := MI_CONDICION || 'AND NVL(BANCO_PAGO,'' '') = '' ''
                                             AND FECHA_PAGO IS NULL ' ;

            IF UN_PAGODOBLE = -1
            THEN
              MI_CAMPOS := MI_CAMPOS || 'FECHA_PAGO_DOBLE   = TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                                         BANCO_PAGO_DOBLE   = '''||UN_BANCO||''',
                                         PAQUETE_PAGO_DOBLE = '''||UN_PAQUETE||''' ';
            ELSE
              MI_CAMPOS := MI_CAMPOS || 'FECHA_PAGO   = TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                         BANCO_PAGO   = '''||UN_BANCO||''',
                                         PAQUETE_PAGO = '''||UN_PAQUETE||''' ';
            END IF;
          /*  BEGIN
            MI_STRVAL:='SELECT SUM(SP_HISTORIA_CONVENIOS.TOTAL) AS TOTAL
                        FROM SP_HISTORIA_CONVENIOS ' || MI_CONDICION;
            EXECUTE IMMEDIATE MI_STRVAL INTO MI_VALORTOTAL;

            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VALORCONVENIOS:=0;
            END;
            MI_VALORCONVENIOS:=MI_VALORTOTAL;     */
      END IF;
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SP_HISTORIA_CONVENIOS',
                                    UN_ACCION    => 'M',
                                    UN_CAMPOS    => MI_CAMPOS,
                                    UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END;
      MI_ACTUALIZAPAGOCONV:=-1;
    END IF;

    RETURN MI_ACTUALIZAPAGOCONV;

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'PERIODO';
        MI_MSGERROR(2).VALOR := UN_PERIODO;
        MI_MSGERROR(3).CLAVE := 'ANIO';
        MI_MSGERROR(3).VALOR := UN_ANO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTHISTCONVENIOS,
             UN_REEMPLAZOS  => MI_MSGERROR,
             UN_TABLAERROR => 'SP_HISTORIA_CONVENIOS'
           );

  END FC_ACTUALIZARPAGOCONVENIOS;


  PROCEDURE PR_CALCULAPRODUCREC
  /*
    NAME              : PR_CALCULAPRODUCREC En access --> calculaProducRec 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/09/2016
    TIME              : 09:35
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : 
    PARAMETERS        : UN_COMPANIA    	=> Compañia de ingreso a la aplicación.
                        UN_CICLO        => Ciclo al que pertenece el acta.
                        UN_CODIGORUTA   => Código de Ruta del usuario consultado.
                        UN_ANO        	=> Año del código de ruta.
                        UN_PERIODO	    => Número del periodo del código de ruta.
                        UN_FECHA        => Fecha pago recaudo.
                        UN_BANCO        => Código del banco en el que está registrado el recaudo.
                        UN_PAQUETE      => Código del paquete .
                        UN_REVERSA    	=> Indicador de si maneja reversar pago.
                        UN_PAGODOBLE    => Indicador de si maneja pago doble.
                        UN_CONVENIO     => Valor para indicar si maneja convenios.
    @NAME:  calcularProducRec
    @METHOD:  POST   
    */
    (

    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO                IN PCK_SUBTIPOS.TI_CICLO,
    UN_ANO                  IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO              IN PCK_SUBTIPOS.TI_PERIODO,
    UN_FECHAINI             IN DATE,
    UN_FECHAFIN             IN DATE,
    UN_USUARIOINI           IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_USUARIOFIN           IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_APLICA               IN VARCHAR2,
    UN_CNPRODUCTIVIDAD      IN PCK_SUBTIPOS.TI_ENTERO

    )AS

      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_PARAMETROS             PCK_SUBTIPOS.TI_CONDICION;
      MI_DBLSALDO               PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLUNICO               PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLDOMICILIARIO        PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLCONSUMO             PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLDISTRTOT            PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLRECAUDO             PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLBARRIDO             PCK_SUBTIPOS.TI_DOBLE;
      MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO;
      MI_RS                     SYS_REFCURSOR;
      MI_CODIGORUTA             PCK_SUBTIPOS.TI_CODIGORUTA;
      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      IF UN_APLICA IS NULL 
      THEN   
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                     UN_NOMBRE    => 'DESCONTAR PRODUCTIVIDAD',
                                     UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS(),
                                     UN_FECHA_PAR => SYSDATE),'NO') <> 'SI' 
        THEN 
          RETURN;
        END IF;
      ELSE 
        IF UN_APLICA <> 'SI' 
        THEN 
          RETURN; 
        END IF ;
      END IF;

      IF UN_CNPRODUCTIVIDAD = 0 
      THEN 
        RETURN;
      END IF;

      <<VALORES_PROD>>
      FOR MI_RS IN (SELECT SP_USUARIO.CODIGORUTA,  
                         SP_USUARIO.BANCOPERPROCESO, 
                         SP_USUARIO.FECHAPAGOPERPROCESO, 
                         SP_USUARIO.PAQUETEPAGOPERPROCESO,  
                         SP_FACTURADO.CONCEPTO, 
                         SP_FACTURADO.VALOR_FACTURADO
                         + SP_FACTURADO.DEUDA
                         - SP_FACTURADO.VALORABONOACT
                         - SP_FACTURADO.VALORABONOANT
                         + SP_FACTURADO.VALORFINACT
                         + SP_FACTURADO.VALORFINANT AS PAGO,  
                         SP_USUARIO_PRODUC.ASEOUNICOXREC,
                         SP_USUARIO_PRODUC.ASEODOMICILIARIOXREC,  
                         SP_USUARIO_PRODUC.ASEOBARRIDOXREC,
                         SP_USUARIO_PRODUC.ASEOCONSUMOXREC  
                    FROM SP_USUARIO 
                         INNER JOIN SP_FACTURADO 
                            ON SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
                           AND SP_USUARIO.CICLO      = SP_FACTURADO.CICLO  
                           AND SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                           AND SP_USUARIO.ANO        = SP_FACTURADO.ANO
                           AND SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO  
                         INNER JOIN SP_USUARIO_PRODUC 
                            ON SP_USUARIO.COMPANIA   = SP_USUARIO_PRODUC.COMPANIA
                           AND SP_USUARIO.CICLO      = SP_USUARIO_PRODUC.CICLO  
                           AND SP_USUARIO.CODIGORUTA = SP_USUARIO_PRODUC.CODIGORUTA
                           AND SP_USUARIO.ANO        = SP_USUARIO_PRODUC.ANOFAC
                           AND SP_USUARIO.PERIODO    = SP_USUARIO_PRODUC.PERIODOFAC  
                   WHERE SP_USUARIO.COMPANIA   = UN_COMPANIA 
                     AND SP_USUARIO.CICLO      = UN_CICLO  
                     AND SP_USUARIO.CODIGORUTA BETWEEN UN_USUARIOINI AND UN_USUARIOFIN  
                     AND SP_USUARIO.ANO        = UN_ANO   
                     AND SP_USUARIO.PERIODO    = UN_PERIODO
                     AND TRUNC(SP_USUARIO.FECHAPAGOPERPROCESO) BETWEEN TO_DATE(UN_FECHAINI,'DD/MM/YYYY') AND TO_DATE(UN_FECHAFIN,'DD/MM/YYYY')  
                     AND SP_USUARIO.BANCOPERPROCESO IS NOT NULL  
                     AND SP_FACTURADO.CONCEPTO = UN_CNPRODUCTIVIDAD 
                     AND SP_FACTURADO.VALOR_FACTURADO
                         + SP_FACTURADO.DEUDA
                         - SP_FACTURADO.VALORABONOACT 
                         - SP_FACTURADO.VALORABONOANT
                         + SP_FACTURADO.VALORFINACT 
                         + SP_FACTURADO.VALORFINANT <> 0 )

      LOOP
        MI_DBLUNICO        := 0;
        MI_DBLDOMICILIARIO := 0;
        MI_DBLBARRIDO      := 0;
        MI_DBLCONSUMO      := 0;
        MI_DBLDISTRTOT     :=0 ;
        MI_DBLRECAUDO      := NVL(MI_RS.PAGO,0);

        MI_DBLSALDO := ROUND(NVL(MI_RS.ASEOUNICOXREC,0) 
                           + NVL(MI_RS.ASEODOMICILIARIOXREC,0) 
                           + NVL(MI_RS.ASEOBARRIDOXREC,0) 
                           + NVL(MI_RS.ASEOCONSUMOXREC,0), 0);

        IF MI_DBLSALDO <> 0 
        THEN 
          MI_DBLUNICO        := ROUND((MI_RS.ASEOUNICOXREC/MI_DBLSALDO) * MI_DBLRECAUDO, 0);
          MI_DBLDISTRTOT     := MI_DBLDISTRTOT + MI_DBLUNICO;
          MI_DBLDOMICILIARIO := ROUND((MI_RS.ASEODOMICILIARIOXREC/MI_DBLSALDO) * MI_DBLRECAUDO, 0);
          MI_DBLDISTRTOT     := MI_DBLDISTRTOT + MI_DBLDOMICILIARIO;
          MI_DBLBARRIDO      := ROUND((MI_RS.ASEOBARRIDOXREC/MI_DBLSALDO) * MI_DBLRECAUDO, 0);
          MI_DBLDISTRTOT     := MI_DBLDISTRTOT + MI_DBLBARRIDO;
          MI_DBLCONSUMO      := MI_DBLRECAUDO - MI_DBLDISTRTOT;

          MI_PARAMETROS := 'COMPANIA = ''' || UN_COMPANIA || 
                          ''' AND CICLO = ' || UN_CICLO || 
                            ' AND CODIGORUTA = ''' || MI_RS.CODIGORUTA || 
                          ''' AND FECHA = ' || PCK_SYSMAN_UTL.FC_SDATE(MI_RS.FECHAPAGOPERPROCESO) || 
                            ' AND BANCO = ' || MI_RS.BANCOPERPROCESO || 
                            ' AND PAQUETE = ' || MI_RS.PAQUETEPAGOPERPROCESO;

          MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_RECAUDO_PRODUCTIVIDAD', 
                                                             UN_CRITERIO => MI_PARAMETROS,
                                                             UN_CAMPO    => 'CONSECUTIVO' , 
                                                             UN_INICIAL  => '1');

          MI_CAMPOS := ' COMPANIA,'||
                       '  CICLO,'||
                       '  CODIGORUTA,'||
                       '  FECHA,'||
                       '  BANCO,'||
                       '  PAQUETE,'||
                       '  CONSECUTIVO,'||
                       '  OPERACION,'||
                       '  ASEOUNICO_REC,'||
                       '  ASEODOMICILIARIO_REC,'||
                       '  ASEOBARRIDO_REC,'||
                       '  ASEOCONSUMO_REC,'||
                       '  ANO,'||
                       '  PERIODO';          

          MI_VALORES := '''' || UN_COMPANIA ||
                        ''',' || UN_CICLO || 
                        ',''' || MI_RS.CODIGORUTA || 
                        ''', ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RS.FECHAPAGOPERPROCESO) ||
                        ',''' || MI_RS.BANCOPERPROCESO || 
                        ''',''' || MI_RS.PAQUETEPAGOPERPROCESO || 
                        ''', ' || MI_CONSECUTIVO ||
                        ', ''PAGO'', ' || MI_DBLUNICO || 
                        ',' || MI_DBLDOMICILIARIO || 
                        ',' || MI_DBLBARRIDO || 
                        ',' || MI_DBLCONSUMO || 
                        ',' || UN_ANO ||
                        ',''' || UN_PERIODO || '''';

          BEGIN     
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SP_RECAUDO_PRODUCTIVIDAD',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            MI_CODIGORUTA := MI_RS.CODIGORUTA;
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END; 
        END IF;

      END LOOP VALORES_PROD;

      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN 
         MI_MSGERROR(1).CLAVE := 'CODRUTA';
         MI_MSGERROR(1).VALOR := MI_CODIGORUTA;
         PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGRECPRODUCT,
             UN_REEMPLAZOS => MI_MSGERROR,
             UN_TABLAERROR => 'SP_RECAUDO_PRODUCTIVIDAD'
           );

  END PR_CALCULAPRODUCREC;

  FUNCTION FC_ELIMINARFINANCIABLE(
  /*
    NAME              : FC_ELIMINARFINANCIABLE En access --> Form_Delete en el form SubFinanciables 
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : JAVIER ANDDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 06/10/2016
    TIME              : 04:00 pm
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : 
    PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                        UN_CICLO            => Ciclo al que pertenece el acta.
                        UN_ANO            	=> Año del código de ruta.
                        UN_PERIODO	        => Número del periodo del código de ruta.
                        UN_CODIGORUTA       => Código de Ruta seleccionado.
                        UN_CONCEPTO         => Código del concepto asociado al financiable.
                        UN_USUARIO          => Usuario que realiza la acción (eliminar financiable).
                        UN_MONTOFINANCIAR   => Valor del monto del financiable.
                        UN_TOTALMONTO       => Valor correspondiente al total de los montos para el código de ruta.
                        UN_SALDOFINANCIABLE => Valor del saldo financiable.
                        UN_NUMEROCUOTAS     => Número de cuotas del financiable.
                        UN_NROCUOTA         => Número de cuota.
                        UN_VALORCUOTA       => Valor de la cuota del financiable.
                        UN_BANCOPERPROCESO  => Código del banco relacionado en el código de ruta seleccionado.
                        UN_CODIGOINTERNO    => Código interno del código de ruta seleccionado.

    MODIFY BY         : PABLO ANDRÉS ESPITIA CUCA
    DATE MODIFIED     : 25/07/2017
    DESCRIPTION       : Limitar el tamaño de los parametros y variables mediante el uso de subtipos predefinidos.
                        Manejo de excepciones.
                        Adicion de campos de auditoria.
                        Alineacion de parametros, variables, bloques y sentencias sql.
    RETURN            :  0 => Funcion ejecutada correctamente
                        -1 => Se va a pasar todo el saldo del financiable a una sola cuota, debe volver a calcular (Mensaje).

    @NAME:  eliminarFinanciable
    @METHOD: GET   
  */
     UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO
    ,UN_ANO                IN PCK_SUBTIPOS.TI_ANIO
    ,UN_PERIODO            IN PCK_SUBTIPOS.TI_PERIODO
    ,UN_CODIGORUTA         IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_CONCEPTO           IN SP_CONCEPTOS.CODIGO%TYPE
    ,UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
    ,UN_MONTOFINANCIAR     IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_TOTALMONTO         IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_SALDOFINANCIABLE   IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_NUMEROCUOTAS       IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_NROCUOTA           IN PCK_SUBTIPOS.TI_ENTERO
    ,UN_VALORCUOTA         IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_BANCOPERPROCESO    IN SP_USUARIO.BANCOPERPROCESO%TYPE
    ,UN_CODIGOINTERNO      IN SP_USUARIO.CODIGOINTERNO%TYPE
  )
  RETURN VARCHAR2
  AS
    MI_PERMITEELIMINAR    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_TABLAUPDATE        PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_FI           PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_FA           PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_US           PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_DD           PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE    PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                VARCHAR2(20 CHAR);
    MI_TABLADELETE        PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE    PCK_SUBTIPOS.TI_CONDICION;
    MI_MSG                VARCHAR2(500 CHAR);
    MI_MENSAJE            VARCHAR2(500 CHAR);
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_ERROR_COD          PLS_INTEGER;
    MI_CONCEPTO           SP_FACTURADO.CONCEPTO%TYPE;

  BEGIN
    MI_TABLA_FI := 'SP_FINANCIABLES';
    MI_TABLA_FA := 'SP_FACTURADO';
    MI_TABLA_US := 'SP_USUARIO';
    MI_TABLA_DD := 'SP_D_DEUDAFACTURADAFINANCIADA';

    --Validar pago al banco
    IF UN_BANCOPERPROCESO IS NOT NULL THEN      
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_SP_MSG_FCEF_USUARIOCANCELO);
      END;
    END IF;

    IF UN_NUMEROCUOTAS > 0 AND UN_CONCEPTO NOT IN(12) THEN 
      MI_PERMITEELIMINAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                     ,UN_NOMBRE    => 'PERMITE ELIMINAR FINANCIABLES'
                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                                     ,UN_FECHA_PAR => SYSDATE)
                               ,'NO');

      IF MI_PERMITEELIMINAR IN('NO') THEN 
        MI_CAMPOS := 'VALORCUOTA    =   '||UN_TOTALMONTO||' 
                     ,NROCUOTA      =   '||UN_NROCUOTA  ||'
                     ,MODIFIED_BY   = '''||UN_USUARIO   ||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''
                     AND CICLO      =   '||UN_CICLO     ||'  
                     AND CONCEPTO   =   '||UN_CONCEPTO  ||'  
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                     AND ANO        =   '||UN_ANO       ||'  
                     AND PERIODO    = '''||UN_PERIODO   ||'''';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FI
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_CONDICION => MI_CONDICION);  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODRUTA';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_M_VALORYNUMCUOTA
                                    ,UN_TABLAERROR => MI_TABLA_FI
                                    ,UN_REEMPLAZOS => MI_MSGERROR);          
        END;

        PCK_DATOS.GL_RTA := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(UN_COMPANIA     => UN_COMPANIA
                                                                           ,UN_USUARIO      => UN_USUARIO
                                                                           ,UN_MACROPROCESO => 'FINANCIABLES'
                                                                           ,UN_SUBPROCESO   => 'Edición'
                                                                           ,UN_ANIO         => UN_ANO
                                                                           ,UN_PERIODO      => UN_PERIODO
                                                                           ,UN_CODINTERNO   => UN_CODIGOINTERNO
                                                                           ,UN_DESCRIPCION  => 'Monto: '||UN_MONTOFINANCIAR  ||'
                                                                                              ; Saldo: '||UN_SALDOFINANCIABLE||'
                                                                                             ; Cuotas: '||UN_NUMEROCUOTAS    ||'
                                                                                              ; Cuota: '||UN_NROCUOTA        ||'
                                                                                        ; Valor Cuota: '||UN_VALORCUOTA);

        --Se va a pasar todo el saldo del financiable a una sola cuota, debe volver a calcular.                                                                                        
        RETURN '-1';
      END IF;
    END IF; 

    IF UN_CONCEPTO IN('12') THEN 
      IF (UN_SALDOFINANCIABLE - UN_VALORCUOTA) > -5 AND (UN_SALDOFINANCIABLE - UN_VALORCUOTA) < 5 THEN 
        MI_CAMPOS := 'DEUDA         = (DEUDA + VALORFINANT + VALORFINACT)
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA  ||'''  
                     AND CICLO       =   '||UN_CICLO     ||'    
                     AND CODIGORUTA  = '''||UN_CODIGORUTA||'''    
                     AND ANO         =   '||UN_ANO       ||'    
                     AND PERIODO     = '''||UN_PERIODO   ||'''';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODRUTA';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'ANIO';
          MI_MSGERROR(2).VALOR := UN_ANO;
          MI_MSGERROR(3).CLAVE := 'PERIODO';
          MI_MSGERROR(3).VALOR := UN_PERIODO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_M_DEUDAFACTURADO
                                    ,UN_TABLAERROR => MI_TABLA_FA
                                    ,UN_REEMPLAZOS => MI_MSGERROR);           
        END;

      ELSIF (UN_SALDOFINANCIABLE - UN_VALORCUOTA) > 0 THEN 
        MI_CAMPOS := 'DEUDA         = ((DEUDA + VALORFINANT + VALORFINACT) + 
                                       (('||UN_SALDOFINANCIABLE||' - 
                                        ('||UN_VALORCUOTA||' * (VALORFINANT + VALORFINACT))) / '||UN_VALORCUOTA||'))
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''  
                     AND CICLO      =   '||UN_CICLO     ||'    
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''    
                     AND ANO        =   '||UN_ANO       ||'    
                     AND PERIODO    = '''||UN_PERIODO   ||'''';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODRUTA';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'ANIO';
          MI_MSGERROR(2).VALOR := UN_ANO;
          MI_MSGERROR(3).CLAVE := 'PERIODO';
          MI_MSGERROR(3).VALOR := UN_PERIODO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_M_DEUDAFACTURADO
                                    ,UN_TABLAERROR => MI_TABLA_FA
                                    ,UN_REEMPLAZOS => MI_MSGERROR);           
        END;

        MI_CAMPOS := 'TOTFACTURAPERACTUAL = TOTFACTURAPERACTUAL + '||(UN_SALDOFINANCIABLE - UN_VALORCUOTA)||'
                     ,MODIFIED_BY         = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED       = SYSDATE';

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                     AND CICLO      =   '||UN_CICLO     ||'   
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''   
                     AND ANO        =   '||UN_ANO       ||'   
                     AND PERIODO    = '''||UN_PERIODO   ||'''';

        BEGIN
          BEGIN                      
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_US
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODRUTA';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_M_TOTFACTPERACTUAL
                                    ,UN_TABLAERROR => MI_TABLA_US
                                    ,UN_REEMPLAZOS => MI_MSGERROR);           
        END;
      ELSE  -- Cuando (UN_SALDOFINANCIABLE - UN_VALORCUOTA) < 0
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_MSG_DISTRICONCEPTO);          
        END;
      END IF;

      BEGIN
        MI_CAMPOS := 'VALORFINANT   = 0
                     ,VALORFINACT   = 0 
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                     AND CICLO      =   '||UN_CICLO     ||'   
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''   
                     AND ANO        =   '||UN_ANO       ||'   
                     AND PERIODO    = '''||UN_PERIODO   ||'''';

        BEGIN                      
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_ERROR_COD := PCK_ERRORES.ERR_SP_FCEF_M_VALANTYACTFINFAC;
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        MI_CAMPOS := 'VALOR_FACTURADO = 0 
                     ,MODIFIED_BY     = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED   = SYSDATE';

        MI_CONCEPTO := 12;

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                     AND CICLO      =   '||UN_CICLO     ||'   
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''   
                     AND ANO        =   '||UN_ANO       ||'   
                     AND PERIODO    = '''||UN_PERIODO   ||'''
                     AND CONCEPTO   =   '||MI_CONCEPTO;

        BEGIN                     
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FA
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_ERROR_COD := PCK_ERRORES.ERR_SP_FCEF_M_VALORFACTURFINAN;
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

        MI_CAMPOS := 'VALOR_FACTURADO = VALOR_FACTURADO + '||UN_SALDOFINANCIABLE||'
                     ,MODIFIED_BY     = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED   = SYSDATE';

        MI_CONCEPTO := 250;

        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                     AND CICLO      =   '||UN_CICLO     ||'   
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''   
                     AND ANO        =   '||UN_ANO       ||'   
                     AND PERIODO    = '''||UN_PERIODO   ||'''
                     AND CONCEPTO   =   '||MI_CONCEPTO;

        BEGIN                     
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_FA
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_ERROR_COD := PCK_ERRORES.ERR_SP_FCEF_M_VALORFACTURFINAN;
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;                     

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'CONCEPTO';
        MI_MSGERROR(2).VALOR := MI_CONCEPTO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => MI_ERROR_COD
                                  ,UN_TABLAERROR => MI_TABLA_FA
                                  ,UN_REEMPLAZOS => MI_MSGERROR);   
      END;

      BEGIN                    
        MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                     AND CICLO      =   '||UN_CICLO     ||'   
                     AND CODIGORUTA = '''||UN_CODIGORUTA||'''   
                     AND ANO        =   '||UN_ANO       ||'   
                     AND PERIODO    = '''||UN_PERIODO   ||'''';

        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DD
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := UN_ANO;
        MI_MSGERROR(3).CLAVE := 'PERIODO';
        MI_MSGERROR(3).VALOR := UN_PERIODO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCEF_E_DEUDAFACTUFINANC
                                  ,UN_TABLAERROR => MI_TABLA_DD
                                  ,UN_REEMPLAZOS => MI_MSGERROR);         
      END;
    END IF;

    PCK_DATOS.GL_RTA := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(UN_COMPANIA     => UN_COMPANIA
                                                                       ,UN_USUARIO      => UN_USUARIO
                                                                       ,UN_MACROPROCESO => 'FINANCIABLES'
                                                                       ,UN_SUBPROCESO   => 'Eliminación'
                                                                       ,UN_ANIO         => UN_ANO
                                                                       ,UN_PERIODO      => UN_PERIODO
                                                                       ,UN_CODINTERNO   => UN_CODIGOINTERNO
                                                                       ,UN_DESCRIPCION  => 'Monto: '||UN_MONTOFINANCIAR  ||'
                                                                                          ; Saldo: '||UN_SALDOFINANCIABLE||'
                                                                                         ; Cuotas: '||UN_NUMEROCUOTAS    ||'
                                                                                          ; Cuota: '||UN_NROCUOTA        ||'
                                                                                    ; Valor Cuota: '||UN_VALORCUOTA);
  RETURN '0'; --Funcion ejecutada correctamente
  END FC_ELIMINARFINANCIABLE;


PROCEDURE PR_CALCULAPRODUCTABO 
/*
  NAME              : PR_CALCULAPRODUCTABO En access --> CalculaProducAbo en el sub Abonos del formulario Factura
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : ADRIANA MARITZA CACERES BONILLA
  DATE MIGRADOR     : 10/11/2016
  TIME              : 09:00 am
  SOURCE MODULE     : SERVICIOS PUBLICOS
  DESCRIPTION       : Procedimiento que de acuerdo al total de los abonos, distribuye el saldo por recaudar.
  PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación
                      UN_CICLO      => Ciclo al que pertenece el abono.
                      UN_ANO        => Año con el que se esta trabajando.
                      UN_PERIODO	  => Periodo con el que se esta trabajando.
                      UN_FECHAINI	  => Fecha inicial a revisar.
                      UN_FECHAFIN	  => Fecha final a revisar.
                      UN_USUARIOINI => Usuario inicial a revisar.
                      UN_USUARIOFIN => Usuario final a revisar.
  @NAME:  calcularProductAbo
  @METHOD:  POST   
  */
(
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO,
  UN_ANO           IN PCK_SUBTIPOS.TI_ANIO, 
  UN_PERIODO       IN PCK_SUBTIPOS.TI_PERIODO, 
  UN_FECHAINI      IN TIMESTAMP, 
  UN_FECHAFIN      IN TIMESTAMP,
  UN_USUARIOINI    IN PCK_SUBTIPOS.TI_CODIGORUTA, 
  UN_USUARIOFIN    IN PCK_SUBTIPOS.TI_CODIGORUTA
)
AS
  MI_TABLA         PCK_SUBTIPOS.TI_TABLA; 
  -- Variable que almacenara el nombre de la tabla 
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  -- Variable que almacenara el nombre de los campos a insertar
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES; 
  -- Variable que almacenara el valor de los campos a insertar
  MI_SALDO         PCK_SUBTIPOS.TI_DOBLE;
  -- VAriable que almacenara el saldo
  MI_UNICO         PCK_SUBTIPOS.TI_DOBLE; 
  -- Variable que almacenara el valor del recaudo unico
  MI_DOMICILIARIO  PCK_SUBTIPOS.TI_DOBLE; 
  -- Variable que almacenara el valor del recaudo domiciliario
  MI_BARRIDO       PCK_SUBTIPOS.TI_DOBLE; 
  -- Variable que almacenara el valor de un recaudo
  MI_CONSUMO       PCK_SUBTIPOS.TI_DOBLE; 
  -- VAriable que almacenara el valor del consumo
  MI_DISTRTOT      PCK_SUBTIPOS.TI_DOBLE; 
  -- Variable que almacenara numero del distrito
  MI_RECAUDO       PCK_SUBTIPOS.TI_DOBLE;
  -- Variable que almacenara el valor del recaudo
  MI_CONS          PCK_SUBTIPOS.TI_DOBLE; 
  -- Variable que almacenara el valor del consecutivo a insertar
  MI_RS            SYS_REFCURSOR;
  -- Cursor que almacena el strSql
  MI_RTA           PCK_SUBTIPOS.TI_RTA_ACME;
  -- Almacenara el resultado de la insercion (ACME)
  MI_PARPRODUC     PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN
    MI_PARPRODUC :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                 UN_NOMBRE    =>  'DESCONTAR PRODUCTIVIDAD',
                                                 UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR =>  SYSDATE),'NO');

    IF MI_PARPRODUC = 'SI' THEN
        <<INFO_ABONOS>>
        FOR MI_RS IN (SELECT SP_ABONOS.CODIGORUTA,
                         SP_ABONOS.BANCO,
                         SP_ABONOS.FECHA, 
                         SP_D_ABONOS.VALOR,
                         SP_USUARIO_PRODUC.ASEOUNICOXREC,
                         SP_USUARIO_PRODUC.ASEODOMICILIARIOXREC, 
                         SP_USUARIO_PRODUC.ASEOBARRIDOXREC, 
                         SP_USUARIO_PRODUC.ASEOCONSUMOXREC 
                    FROM (SP_ABONOS 
                      INNER JOIN SP_D_ABONOS 
                         ON SP_ABONOS.COMPANIA    = SP_D_ABONOS.COMPANIA
                        AND SP_ABONOS.CICLO       = SP_D_ABONOS.CICLO 
                        AND SP_ABONOS.CODIGORUTA  = SP_D_ABONOS.CODIGORUTA
                        AND SP_ABONOS.ANO         = SP_D_ABONOS.ANO
                        AND SP_ABONOS.PERIODO     = SP_D_ABONOS.PERIODO
                        AND SP_ABONOS.CONSECUTIVO = SP_D_ABONOS.CONSECUTIVO)
                      INNER JOIN SP_USUARIO_PRODUC 
                         ON SP_ABONOS.COMPANIA    = SP_USUARIO_PRODUC.COMPANIA 
                        AND SP_ABONOS.CICLO       = SP_USUARIO_PRODUC.CICLO 
                        AND SP_ABONOS.CODIGORUTA  = SP_USUARIO_PRODUC.CODIGORUTA 
                        AND SP_ABONOS.ANO         = SP_USUARIO_PRODUC.ANOFAC 
                        AND SP_ABONOS.PERIODO     = SP_USUARIO_PRODUC.PERIODOFAC
                   WHERE SP_ABONOS.COMPANIA       = UN_COMPANIA
                        AND SP_ABONOS.CICLO       = UN_CICLO 
                        AND SP_ABONOS.CODIGORUTA BETWEEN UN_USUARIOINI AND UN_USUARIOFIN 
                        AND SP_ABONOS.ANO         = UN_ANO
                        AND SP_ABONOS.PERIODO     = UN_PERIODO
                        AND SP_ABONOS.FECHA BETWEEN UN_FECHAINI AND UN_FECHAFIN
                        AND SP_D_ABONOS.CONCEPTO  = 35
                        AND SP_D_ABONOS.VALOR NOT IN (0) )
        LOOP
        -- Inicializar valores
        MI_UNICO        := 0;
        MI_DOMICILIARIO := 0;
        MI_BARRIDO      := 0;
        MI_CONSUMO      := 0;
        MI_DISTRTOT     := 0;

        MI_RECAUDO := ABS(NVL(MI_RS.VALOR, 0));
        -- Se distribuye de acuerdo al saldo por recaudar
        MI_SALDO := NVL(MI_RS.ASEOUNICOXREC, 0) + NVL(MI_RS.ASEODOMICILIARIOXREC, 0) + NVL(MI_RS.ASEOBARRIDOXREC, 0) + NVL(MI_RS.ASEOCONSUMOXREC, 0);
        MI_UNICO := (MI_RS.ASEOUNICOXREC / MI_SALDO) * MI_RECAUDO; 
        MI_DISTRTOT := MI_DISTRTOT + MI_UNICO; 
        MI_DOMICILIARIO := (MI_RS.ASEODOMICILIARIOXREC / MI_SALDO) * MI_RECAUDO;
        MI_DISTRTOT := MI_DISTRTOT + MI_DOMICILIARIO; 
        MI_BARRIDO := (MI_RS.ASEOUNICOXREC / MI_SALDO) * MI_RECAUDO; 
        MI_DISTRTOT := MI_DISTRTOT + MI_BARRIDO; 
        MI_CONSUMO := MI_RECAUDO - MI_DISTRTOT; 
        MI_CONS :=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SP_RECAUDO_PRODUCTIVIDAD', 
                                                   UN_CRITERIO => 'COMPANIA      = ''' || UN_COMPANIA||'''
                                                                  AND CICLO      = ''' || UN_CICLO||''' 
                                                                  AND CODIGORUTA = ''' || MI_RS.CODIGORUTA||'''
                                                                  AND FECHA      = ''' || MI_RS.FECHA||'''
                                                                  AND BANCO      = ''' || MI_RS.BANCO||'''
                                                                  AND PAQUETE    = 888', 
                                                   UN_CAMPO    => 'CONSECUTIVO'); 
        BEGIN
          MI_TABLA := 'SP_RECAUDO_PRODUCTIVIDAD'; 

          MI_CAMPOS:='COMPANIA,
                      CICLO,
                      CODIGORUTA,
                      FECHA,
                      BANCO,
                      PAQUETE,
                      CONSECUTIVO,
                      OPERACION,
                      ASEOUNICO_REC, 
                      ASEODOMICILIARIO_REC, 
                      ASEOBARRIDO_REC, 
                      ASEOCONSUMO_REC,
                      ANO, 
                      PERIODO';

          MI_VALORES := '''' || UN_COMPANIA || 
                        ''', ''' || UN_CICLO || 
                        ''', ''' || MI_RS.CODIGORUTA ||
                        ''', ''' || MI_RS.FECHA || 
                        ''', ''' || MI_RS.BANCO || 
                        ''', 888, ' 
                        || MI_CONS || 
                        ', ABONO, '
                        || MI_UNICO || 
                        ', ' || MI_DOMICILIARIO || 
                        ', ' || MI_BARRIDO || 
                        ', ' || MI_CONSUMO ||
                        ', ' || UN_ANO ||
                        ', ''' || UN_PERIODO||''''; 

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                      UN_ACCION    => 'I', 
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_VALORES);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END ;                                                                      
       END LOOP INFO_ABONOS;   
    END IF;
 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_SERV_PUBLICOS_RECAUDO,
        UN_TABLAERROR =>'SP_RECAUDO_PRODUCTIVIDAD'
      );  


END  PR_CALCULAPRODUCTABO ;

  FUNCTION FC_GETFECHATEXTO
  (
     /*
        NAME              : FC_GETFECHATEXTO --> EN ACCESS getFechaTexto
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : CAMILO ANDRÉS PÉREZ DUEÑAS 
        DATE MIGRADOR     : 25/11/2016
        TIME              : 08:49 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Función que retorna la fecha y la hora de un texto.
        PARAMETERS        : UN_COMPANIA  => Compania de ingreso a la aplicacion
                            UN_DATO      => Es es dato a convertir a fecha o a hora 
                            UN_TIPO      => Se ingresa el tipo de parámetro  si es F  manda la fecha si es H manda la hora
        @NAME:	asignarFechaTexto
        @METHOD: 	GET
        */
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_DATO     IN VARCHAR2,
    UN_TIPO     IN VARCHAR2
  )
  RETURN VARCHAR2
    AS 
      MI_RTA    VARCHAR2(50 CHAR); 
    BEGIN     
      CASE 
        WHEN UN_TIPO = 'F' THEN
          MI_RTA := SUBSTR(UN_DATO,5,2) || '/' || SUBSTR(UN_DATO,7,2) || '/' || SUBSTR(UN_DATO,9,2) ;
        WHEN UN_TIPO = 'H' THEN 
           MI_RTA := SUBSTR(UN_DATO,1,2) || ':' || SUBSTR(UN_DATO,3,2);
      END CASE;  
      RETURN MI_RTA;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERRR_SERV_PUBLICOS_FECHATXT);

  END FC_GETFECHATEXTO; 

--28
FUNCTION FC_AUTORIZACION_CARTA 
  /*
    OBJETIVO              : Definir si la entidad está autorizada para manejar proceso de Cartas, 
                            según su Nit y la configuración del parámetro MANEJA MODELO DE CARTAS.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_NIT: Nit de la entidad.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : YESIKA PAOLA BECERRA CASTRO
    FECHA                 : 31/01/2017 08:30 AM
    REALIZADO POR:        : SYSMAN SAS
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :  

    NAME              : FC_AUTORIZACION_CARTA (En Access AUTORIZACION_CARTA del módulo Servicios públicos)
    SOURCE MODULE     : SysmanSp2016.05.04
    @NAME:	validarManejoCartas
    @METHOD: 	GET
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
                             UN_NOMBRE    => 'MANEJA MODELO DE CARTAS',
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
                          ,'899999717')

    THEN
      MI_RTA := -1;
    END IF;

    RETURN MI_RTA;  
    EXCEPTION WHEN NO_DATA_FOUND THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_AUTCARTA);
  END FC_AUTORIZACION_CARTA;

  --29
  FUNCTION FC_AGREGARFINANCIABLE_PERSIG 
    /*
      NAME              : FC_AGREGARFINANCIABLE_PERSIG - Access: FRM_INFO_OPERACION.Form_BeforeUpdate
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRATION    : 18/07/2017
      TIME              : 12:02 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS (74)
      MODIFIED BY       : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Verifica la existencia del periodo siguiente y retorna el año y el periodo (ANIO,PERIODO).
      PARAMETERS        : UN_COMPANIA    		  => Codigo de la compania.
                          UN_ANIOINI          => Anio inicial del financiable.
                          UN_PERIODOINI       => Periodo inicial del financiable.

      @NAME  : agregarFinanciablePerSig
      @METHOD: GET                     
    */
    (
       UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
      ,UN_ANIOINI     IN PCK_SUBTIPOS.TI_ANIO
      ,UN_PERIODOINI  IN PCK_SUBTIPOS.TI_PERIODO
    )
  RETURN VARCHAR2 
  AS
     MI_ANIOPER    VARCHAR2(10 CHAR);
     MI_CANT       PCK_SUBTIPOS.TI_ENTERO;
     MI_ANIOSIG    PCK_SUBTIPOS.TI_ANIO;
     MI_PERIODOSIG PCK_SUBTIPOS.TI_PERIODO;
     MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_ANIOPER := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA     => UN_COMPANIA
                                                                 ,UN_ANO          => UN_ANIOINI
                                                                 ,UN_PERIODO      => UN_PERIODOINI
                                                                 ,UN_TIPO_RETORNO => ',');

    MI_ANIOSIG    := SUBSTR(MI_ANIOPER,1,4);
    MI_PERIODOSIG := SUBSTR(MI_ANIOPER,6,7);

    --Verificar existencia periodo siguiente
    BEGIN
      SELECT COUNT(MES)
      INTO MI_CANT
      FROM SP_PERIODO
      WHERE COMPANIA = UN_COMPANIA
        AND ANO      = MI_ANIOSIG
        AND MES      = MI_PERIODOSIG;

    IF MI_CANT IN(0) THEN 
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'PERIODO';
      MI_MSGERROR(1).VALOR := MI_PERIODOSIG;
      MI_MSGERROR(2).CLAVE := 'ANIO';
      MI_MSGERROR(2).VALOR := MI_ANIOSIG;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_FCAFPS_VALIDAPERSIG
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    RETURN MI_ANIOPER;
  END FC_AGREGARFINANCIABLE_PERSIG;

  --30
  PROCEDURE PR_VERIFICA_ADIFINANCIABLE 
    /*
      NAME              : PR_VERIFICA_ADIFINANCIABLE - Access: FRM_INFO_OPERACION.Form_BeforeUpdate
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRADOR     : 18/07/2017
      TIME              : 10:29 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS (74)
      MODIFIED BY       : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Proceso que verifica las condiciones para adicionar un financiable.
      PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                          UN_CICLO            => Codigo del ciclo
                          UN_CODIGORUTA       => Codigo de ruta del usuario.
                          UN_FECHACREACION    => Fecha de creacion del financiable.

      @NAME: verificarAdicionFinanciable
      @METHOD: GET                     
    */
    (
       UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
      ,UN_CICLO         IN SP_USUARIO.CICLO%TYPE
      ,UN_CODIGORUTA    IN SP_USUARIO.CODIGORUTA%TYPE
      ,UN_FECHACREACION IN DATE
    )
  AS 
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ERROR_COD          PLS_INTEGER; /*Opcion de error */
    MI_FIMM               SP_USUARIO.FIMM%TYPE;
    MI_LECTURA            SP_USUARIO.LECTURA%TYPE; 
    MI_BANCOPERPROCESO    SP_USUARIO.BANCOPERPROCESO%TYPE; 
    MI_PERIODOSNOCOBROFAC SP_USUARIO.PERIODOSNOCOBROFAC%TYPE; 
    MI_FECHAPREPARACION   DATE;
    MI_IND_CALCULADO      PCK_SUBTIPOS.TI_LOGICO; /*Indicador de calculo del ciclo*/
  BEGIN
    BEGIN
      BEGIN
        SELECT 
           FIMM
          ,LECTURA 
          ,BANCOPERPROCESO 
          ,PERIODOSNOCOBROFAC
        INTO
           MI_FIMM
          ,MI_LECTURA
          ,MI_BANCOPERPROCESO
          ,MI_PERIODOSNOCOBROFAC
        FROM SP_USUARIO
        WHERE COMPANIA = UN_COMPANIA
        AND CICLO      = UN_CICLO
        AND CODIGORUTA = UN_CODIGORUTA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_PRAF_USUARIONOEXIST
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    IF MI_LECTURA IN(0) AND MI_BANCOPERPROCESO IS NULL AND MI_PERIODOSNOCOBROFAC IN(0) THEN
      BEGIN
        MI_ERROR_COD := CASE WHEN MI_FIMM IN ('F')
                             THEN PCK_ERRORES.ERR_SP_MSG_PRAF_PERINOCOBROFAC
                             ELSE PCK_ERRORES.ERR_SP_MSG_PRAF_FIMMNOTINCERO
                        END;

        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'LECTURA';
        MI_MSGERROR(1).VALOR := MI_LECTURA;
        MI_MSGERROR(2).CLAVE := 'BANCO';
        MI_MSGERROR(2).VALOR := NVL(MI_BANCOPERPROCESO,'NO REGISTRA');
        MI_MSGERROR(3).CLAVE := 'NPERIODOS';
        MI_MSGERROR(3).VALOR := MI_PERIODOSNOCOBROFAC;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => MI_ERROR_COD
                                  ,UN_REEMPLAZOS => MI_MSGERROR);                
      END;         
    END IF;


    BEGIN
      BEGIN
        SELECT 
           FECHA_PREPARACION
          ,INDCALCULADO
        INTO 
           MI_FECHAPREPARACION
          ,MI_IND_CALCULADO
        FROM SP_CICLO
        WHERE COMPANIA = UN_COMPANIA
          AND NUMERO   = UN_CICLO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CICLO';
      MI_MSGERROR(1).VALOR := UN_CICLO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_PRAF_VERIFICARCICLO
                                ,UN_REEMPLAZOS => MI_MSGERROR);                
    END;

    --Validar que la fecha de preparacion no sea nula
    IF MI_FECHAPREPARACION IS NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CICLO';
      MI_MSGERROR(1).VALOR := UN_CICLO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_MSG_PRAF_VALIDAFECHAPRE
                                ,UN_REEMPLAZOS => MI_MSGERROR);
      END;
    END IF;

    --Validar que la fecha de creacion sea mayor que la de preparacion
    IF UN_FECHACREACION < MI_FECHAPREPARACION THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'FCREACION';
      MI_MSGERROR(1).VALOR := UN_FECHACREACION;
      MI_MSGERROR(2).CLAVE := 'FPREPARACION';
      MI_MSGERROR(2).VALOR := MI_FECHAPREPARACION;
      MI_MSGERROR(3).CLAVE := 'CICLO';
      MI_MSGERROR(3).VALOR := UN_CICLO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_MSG_PRAF_VALIDARFECHACR
                                ,UN_REEMPLAZOS => MI_MSGERROR);
      END;
    END IF;

    --Validar el calculo de ciclo
    IF MI_IND_CALCULADO IN(0) THEN 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CICLO';
      MI_MSGERROR(1).VALOR := UN_CICLO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_MSG_PRAF_VALIDAINDCALCU
                                ,UN_REEMPLAZOS => MI_MSGERROR);
      END;
    END IF;  
  END PR_VERIFICA_ADIFINANCIABLE;

  --31
  FUNCTION FC_ACTUALIZARCONCEPTO_ANTES
    /*
      NAME              : FC_ACTUALIZARCONCEPTO_ANTES -> FRM_INFO_OPERACION.Concepto_BeforeUpdate (Access)
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRATION    : 27/10/2016
      TIME              : 04:34 PM
      DESCRIPTION       : Funcion que ejecuta el proceso antes de actualizar un concepto.
      PARAMETERS        : UN_COMPANIA     => Codigo de la compania con la cual se inicio sesion.
                          UN_CICLO        => Codigo del ciclo.
                          UN_CODIGORUTA   => Codigo de ruta asoicado al ciclo.
                          UN_ANIOINI      => Numero del anio inicial.
                          UN_PERIODOINI   => Numero del periodo inicial.
                          UN_CONCEPTO     => Codigo del concepto asociado al financiable.
      MODIFIED BY       : 
      DATE MODIFIED     : 
      TIME              : 

      @NAME:   actualizarConceptoAntes
      @METHOD: GET
    */
    (
      UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CICLO      IN PCK_SUBTIPOS.TI_CICLO,
      UN_CODIGORUTA IN SP_INFO_TIPO_OPERACION.CODIGORUTA%TYPE,
      UN_ANIOINI    IN PCK_SUBTIPOS.TI_ANIO,
      UN_PERIODOINI IN SP_INFO_TIPO_OPERACION.PERIODO%TYPE,
      UN_CONCEPTO   IN SP_CONCEPTOS.CODIGO%TYPE
    )
  RETURN VARCHAR2 
  AS 
    MI_CANT         PCK_SUBTIPOS.TI_ENTERO;
    MI_PARCONCEPTOS PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ANIOPER      VARCHAR2(7 CHAR);
  BEGIN
    --Si el concepto es nulo
    IF UN_CONCEPTO IS NULL THEN 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_SP_FCSCA_MSG_VALIDACONCEPT);
      END;
    END IF;

    --Si el concepto es 12
    IF UN_CONCEPTO IN(12) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_SP_FCSCA_MSG_VALIDCONCEP12);
      END;
    END IF;

    --Conceptos financiables
    MI_PARCONCEPTOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                ,UN_NOMBRE    => 'CONCEPTOS FINANCIABLES'
                                                ,UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS
                                                ,UN_FECHA_PAR => SYSDATE)
                            ,'-1');

    SELECT COUNT(CONCEPTO)
    INTO MI_CANT
    FROM SP_FINANCIABLES
    WHERE COMPANIA   = UN_COMPANIA
      AND CICLO      = UN_CICLO
      AND CODIGORUTA = UN_CODIGORUTA
      AND PERIODO    = UN_PERIODOINI
      AND CONCEPTO   = UN_CONCEPTO;

    IF MI_CANT NOT IN (0) THEN
      IF INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_PARCONCEPTOS),''''||UN_CONCEPTO||'''') IN(0) THEN
        MI_MSGERROR(1).CLAVE := 'CONCEPTO';
        MI_MSGERROR(1).VALOR := UN_CONCEPTO;

        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_FCACA_MSG_CONCEPTONOFIN
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;      
      ELSE
        MI_ANIOPER := FC_AGREGARFINANCIABLE_PERSIG(UN_COMPANIA   => UN_COMPANIA
                                                  ,UN_ANIOINI    => UN_ANIOINI
                                                  ,UN_PERIODOINI => UN_PERIODOINI);
      END IF;
    END IF;

    RETURN MI_ANIOPER;  
  END FC_ACTUALIZARCONCEPTO_ANTES;  

--32
  PROCEDURE PR_CALCULAPRODUCDOBLE
  /*
    NAME              : PR_CALCULAPRODUCDOBLE En access --> CalculaProducDoble
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 28/09/2017
    TIME              : 04:00 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Procedimiento para actualizar la productividad cuando se realice un pago doble
    PARAMETERS        :
    @NAME:  calcularProductividadDoble
    @METHOD:  POST
    */
  (
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO
    ,UN_ANO           IN PCK_SUBTIPOS.TI_ANIO
    ,UN_PERIODO       IN PCK_SUBTIPOS.TI_PERIODO
    ,UN_FECHAINI      DATE
    ,UN_FECHAFIN      DATE
    ,UN_USUARIOINI    IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_USUARIOFIN    IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_USUARIO       IN SP_RECAUDO_PRODUCTIVIDAD.CREATED_BY%TYPE
  )
  AS

  MI_PARPRODUC        PCK_SUBTIPOS.TI_PARAMETRO;
  MI_RS               SYS_REFCURSOR;
  MI_DBLUNICO         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLDOMICILIARIO  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLBARRIDO       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLCONSUMO       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLDISTRTOT      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLRECAUDO       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_DBLSALDO         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CONS             PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
      MI_PARPRODUC :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                   UN_NOMBRE    =>  'DESCONTAR PRODUCTIVIDAD',
                                                   UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR =>  SYSDATE),'NO');

      IF MI_PARPRODUC = 'SI' THEN
          <<DOBLE>>
          FOR MI_RS IN
              (SELECT P.CODIGORUTA, P.BANCO, P.FECHA, P.NUMEROPAQUETE, D.VALOR, U.ASEOUNICOXREC,
                      U.ASEODOMICILIARIOXREC,U.ASEOBARRIDOXREC, U.ASEOCONSUMOXREC
               FROM   (SP_PAGO P INNER JOIN SP_D_PAGOSDOBLES D
                             ON   P.COMPANIA      = D.COMPANIA
                             AND  P.FECHA         = D.FECHA
                             AND  P.BANCO         = D.BANCO
                             AND  P.NUMEROPAQUETE = D.NUMEROPAQUETE
                             AND  P.CODIGORUTA    = D.CODIGORUTA)
                         INNER JOIN SP_USUARIO_PRODUC U
                             ON   P.COMPANIA   = U.COMPANIA
                             AND  P.CICLO      = U.CICLO
                             AND  P.CODIGORUTA = U.CODIGORUTA
                             AND  P.ANO        = U.ANOFAC
                             AND  P.PERIODO    = U.PERIODOFAC
               WHERE   P.COMPANIA =  UN_COMPANIA
                 AND   P.CICLO =  UN_CICLO
                 AND   P.CODIGORUTA BETWEEN UN_USUARIOINI AND UN_USUARIOFIN
                 AND   P.ANO = UN_ANO
                 AND   P.PERIODO = UN_PERIODO
                 AND   TRUNC(P.FECHA) BETWEEN TO_DATE(UN_FECHAINI,'DD/MM/YYYY HH24:MI:SS')  AND  TO_DATE(UN_FECHAFIN,'DD/MM/YYYY HH24:MI:SS')
                 AND   P.OPERACION = 'D'
                 AND   D.CONCEPTO = 35
                 AND   D.VALOR<>0 )
              LOOP
                  MI_DBLUNICO := 0;
                  MI_DBLDOMICILIARIO := 0;
                  MI_DBLBARRIDO := 0;
                  MI_DBLCONSUMO := 0;
                  MI_DBLDISTRTOT := 0;
                  MI_DBLRECAUDO := ABS(MI_RS.VALOR);

                  IF MI_DBLSALDO <> 0 THEN
                      MI_DBLSALDO := ROUND(MI_RS.ASEOUNICOXREC + MI_RS.ASEODOMICILIARIOXREC + MI_RS.ASEOBARRIDOXREC + MI_RS.ASEOCONSUMOXREC);
                      MI_DBLUNICO := ROUND((MI_RS.ASEOUNICOXREC / MI_DBLSALDO) * MI_DBLRECAUDO);
                      MI_DBLDISTRTOT := (MI_DBLDISTRTOT + MI_DBLUNICO);
                      MI_DBLDOMICILIARIO := ROUND((MI_RS.ASEODOMICILIARIOXREC / MI_DBLSALDO) * MI_DBLRECAUDO);
                      MI_DBLDISTRTOT := (MI_DBLDISTRTOT + MI_DBLDOMICILIARIO);
                      MI_DBLBARRIDO := ROUND((MI_RS.ASEOBARRIDOXREC / MI_DBLSALDO) * MI_DBLRECAUDO, 0);
                      MI_DBLDISTRTOT := (MI_DBLDISTRTOT + MI_DBLBARRIDO);
                      MI_DBLCONSUMO := (MI_DBLRECAUDO - MI_DBLDISTRTOT);

                      BEGIN
                          MI_CONS :=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                                      (UN_TABLA    => 'SP_RECAUDO_PRODUCTIVIDAD',
                                       UN_CRITERIO => ' COMPANIA      = ''' || UN_COMPANIA||'''
                                                       AND CICLO      = ''' || UN_CICLO||'''
                                                       AND CODIGORUTA = ''' || MI_RS.CODIGORUTA||'''
                                                       AND FECHA      = ''' || MI_RS.FECHA||'''
                                                       AND BANCO      = ''' || MI_RS.BANCO||'''
                                                       AND PAQUETE    = '''|| MI_RS.NUMEROPAQUETE ||''' ',
                                       UN_CAMPO    => 'CONSECUTIVO');
                          MI_TABLA := 'SP_RECAUDO_PRODUCTIVIDAD';
                          MI_CAMPOS := 'COMPANIA,
                                        CICLO,
                                        CODIGORUTA,
                                        FECHA,
                                        BANCO,
                                        PAQUETE,
                                        CONSECUTIVO,
                                        OPERACION,
                                        ASEOUNICO_REC,
                                        ASEODOMICILIARIO_REC,
                                        ASEOBARRIDO_REC,
                                        ASEOCONSUMO_REC,
                                        ANO,
                                        PERIODO  ,
                                        CREATED_BY,
                                        DATE_CREATED';

                          MI_VALORES := ' '''|| UN_COMPANIA ||''',
                                        '|| UN_CICLO ||',
                                        '''|| MI_RS.CODIGORUTA ||''' ,
                                        TO_DATE('''|| MI_RS.FECHA ||''',''DD/MM/YYYY HH24:MI:SS''),
                                        '''|| MI_RS.BANCO ||''',
                                        '''||  MI_RS.NUMEROPAQUETE ||''',
                                        '|| MI_CONS ||',
                                        ''DOBLE'',
                                        '|| MI_DBLUNICO ||',
                                        '|| MI_DBLDOMICILIARIO ||',
                                        '|| MI_DBLBARRIDO ||',
                                        '|| MI_DBLCONSUMO ||',
                                        '|| UN_ANO ||',
                                        '''|| UN_PERIODO ||''',
                                        '''|| UN_USUARIO || ''',
                                        SYSDATE ';
                          BEGIN
                              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                                    UN_ACCION    => 'I',
                                                                    UN_CAMPOS    => MI_CAMPOS,
                                                                    UN_CONDICION => MI_VALORES);

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                          PCK_ERR_MSG.RAISE_WITH_MSG
                              (UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGDOBLEPAGOPRODUC
                              ,UN_TABLAERROR => 'SP_RECAUDO_PRODUCTIVIDAD' );
                      END;

                  END IF;

              END LOOP DOBLE;

      END IF;

  END  PR_CALCULAPRODUCDOBLE ;


END PCK_SERVICIOS_PUBLICOS_COM2;