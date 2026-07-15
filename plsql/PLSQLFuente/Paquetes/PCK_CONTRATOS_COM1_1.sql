create or replace PACKAGE BODY "PCK_CONTRATOS_COM1" AS

--1
PROCEDURE PR_SECTORES

/*
    NAME              : FC_SECTORES --> EN ACCESS EVENTO al abrir el formulario
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 07/10/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : CC2015.06.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : INSERTA DATOS SI EN LA TABLA SECTORES NO ENCUENTRA REGISTROS
    @NAME: insertarSectoresDefault
  */

(

    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO 
)

AS
  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PCKDATOS    VARCHAR2(10000 CHAR);

 BEGIN 
  BEGIN 
    BEGIN
        MI_CAMPOS := 'COMPANIA ,
                      CODIGO,
                      DESCRIPCION,
					  CREATED_BY,
					  DATE_CREATED';

        MI_VALORES := ''''||UN_COMPANIA||''',
                       01,
                       ''Desarrollo Territorial'',
					             '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES);                  

        MI_VALORES := ''''||UN_COMPANIA||''',
                       02,
                       ''Justicia'',
					             '''||UN_USUARIO||''',
					             SYSDATE'; 

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES);                                 

        MI_VALORES := ''''||UN_COMPANIA||''',
                       03,
                       ''Trabajo'',
					             '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       04,
                       ''Vivienda'',
					             '''||UN_USUARIO||''',
					             SYSDATE'; 

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       05,
                       ''Telecomunicaciones'',
					             '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       06,
                       ''Agropecuario'',
					             '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 


        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       07,
                       ''Interior'',
					             '''||UN_USUARIO||''',
					             SYSDATE';   

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       08,
                       ''Desarrollo Economico'',
					             '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       09,
                       ''Hacienda'',
                       '''||UN_USUARIO||''',
					             SYSDATE';      

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       10,
                       ''Saneamiento Fiscal'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       11,
                       ''Funcionamiento no permitido'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       12,
                       ''Transporte'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 


        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       13,
                       ''Minas y Energia'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       14,
                       ''Medio Ambiente'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       15,
                       ''Agua Potable y Saneamiento Basico'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       16,
                       ''Salud'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 


        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       17,
                       ''Educacion'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       18,
                       ''Cultura'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        MI_VALORES :=  ''''||UN_COMPANIA||''',
                       19,
                       ''Sin Clasificar'',
                       '''||UN_USUARIO||''',
					             SYSDATE';

        MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => 'SECTORES',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS,
                                          UN_VALORES   => MI_VALORES); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_INSERT_SECT,
                               UN_REEMPLAZOS => MI_MSGERROR);
   END;                                    

  END PR_SECTORES;



--2
FUNCTION FC_VALORTOTALNOVEDADES 
/*
    NAME              : FC_ACCIONES_CONTROL_CONTRATOS --> EN ACCESS EVENTO en el BotÃ³n
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 19/10/2015
    TIME              : 11:40 AM
    SOURCE MODULE     : CC2015.06.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : INSERTA DATOS A UNA TABLA TEMPORAL 
    @NAME: getTotalValorNovedad
  */
(
  UN_COMPANIA          IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASEORDEN        IN  VARCHAR2,
  UN_ORDENDECOMPRA     IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_CLASENOVEDAD      IN  VARCHAR2
)
RETURN NUMBER
  AS
  MI_RESULTADO              PCK_SUBTIPOS.TI_ENTERO_LARGO;

  BEGIN 
    BEGIN
  SELECT  
         SUM(NOVEDADCONTRATO.VALORTOTAL) 
    INTO MI_RESULTADO
    FROM NOVEDADCONTRATO         
    LEFT JOIN CLASETRANSACCIONC 
      ON NOVEDADCONTRATO.TIPOT           = CLASETRANSACCIONC.TIPOT 
     AND NOVEDADCONTRATO.CLASET          = CLASETRANSACCIONC.CLASET
   WHERE 
         NOVEDADCONTRATO.COMPANIA        = UN_COMPANIA
     AND NOVEDADCONTRATO.CLASEORDEN      = UN_CLASEORDEN          
     AND NOVEDADCONTRATO.ORDENDECOMPRA   = UN_ORDENDECOMPRA
     AND CLASETRANSACCIONC.CLASENOVEDAD  = UN_CLASENOVEDAD;

  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:=0;
  END; 
  RETURN MI_RESULTADO;

  END FC_VALORTOTALNOVEDADES;


--5    
FUNCTION FC_VERIFICACON_ANULACION 
/*
    NAME              : FC_VERIFICACON_ANULACION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 11/11/2015
    TIME              : 12:00 PM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : YESSICA SANA Se agregan excepciones dado que se estaban enviando mensajes al controlador.
    DATE MODIFIED     : 08/08/2017
    TIME              : 12:00 PM
    DESCRIPTION       : Verfica las afectaciones que se tienen si se anula un contrato y retorna el mensaje que especifÃ­ca dichas afectaciones, de lo contrario retorna que el proceso se ha ejecutado de manera correcta.
    @NAME: anularOrdendeCompra
  */

  (
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO           IN VARCHAR2,
  UN_NUMERO         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO  
  )
  RETURN VARCHAR2 
  AS 

  MI_CADENA      VARCHAR2(32000 CHAR);
  MI_I           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_CADENA := '';
    MI_I      := 0;
    --si esta afectado por adiciones por un contrato

    FOR RS IN ( SELECT  
                       CLASEORDEN,
                       NUMERO
                  FROM 
                       ORDENDECOMPRA
                 WHERE 
                       COMPANIA        = UN_COMPANIA
                   AND TIPOAFECTADO    = UN_TIPO
                   AND NUMEROAFECTADO  = UN_NUMERO
               )
    LOOP 
      MI_CADENA := MI_CADENA ||', '|| RS.CLASEORDEN||' - '||RS.NUMERO;
      MI_I:=MI_I+1;
    END LOOP;

    MI_CADENA:=SUBSTR(MI_CADENA,3);

    BEGIN
        IF MI_I<>0 THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        --RAISE_APPLICATION_ERROR (-20000,  'No se permite ANULAR porque el contrato tiene modificaciones por: '||MI_CADENA||'. Si desea borrar debe eliminar estas modificaciones.');
        END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
          MI_REEMPLAZOS(0).CLAVE:= 'CADENA';
          MI_REEMPLAZOS(0).VALOR:= MI_CADENA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_TABLAERROR => 'ORDENDECOMPRA'
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_BORRARMODIFIC
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);
                                      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, 
                                                                  UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DEPPLANONOCORRESP,
                                                                  UN_TABLAERROR => 'ORDENDECOMPRA');
    END;

    --si esta afectado por novedades

    MI_CADENA:='';
    MI_I:=0;

    FOR RS IN ( SELECT 
                       NOVEDADCONTRATO.CLASEORDEN, 
                       NOVEDADCONTRATO.ORDENDECOMPRA, 
                       NOVEDADCONTRATO.NOVEDAD, 
                       CLASETRANSACCIONC.NOMBRE
                  FROM 
                       NOVEDADCONTRATO 
                  LEFT JOIN CLASETRANSACCIONC
                    ON NOVEDADCONTRATO.TIPOT = CLASETRANSACCIONC.TIPOT
                 WHERE  
                       COMPANIA=UN_COMPANIA 
                   AND NOVEDADCONTRATO.CLASEORDEN =UN_TIPO
                   AND NOVEDADCONTRATO.ORDENDECOMPRA=UN_NUMERO
               )
    LOOP 
      MI_CADENA := MI_CADENA ||', '|| RS.NOMBRE||' - '||RS.NOVEDAD;
      MI_I:=MI_I+1;
    END LOOP;

    MI_CADENA:=SUBSTR(MI_CADENA,3);

    BEGIN 
        IF MI_I<>0 THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        --RAISE_APPLICATION_ERROR (No se permite ANULAR porque el contrato tiene novedades por: '||MI_CADENA||'. Si desea borrar debe eliminar estas novedades.);
        END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
          MI_REEMPLAZOS(0).CLAVE:= 'CADENA';
          MI_REEMPLAZOS(0).VALOR:= MI_CADENA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_TABLAERROR => 'NOVEDADCONTRATO'
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_CONTIMODIFIC
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;



    --si esta afectado por almacen

    MI_CADENA:='';
    MI_I:=0;

    FOR RS IN ( SELECT  
                       TIPOMOVIMIENTO.NOMBRE, MOVIMIENTO.NUMERO
                  FROM 
                       MOVIMIENTO 
                  LEFT JOIN TIPOMOVIMIENTO 
                    ON MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                   AND MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                 WHERE 
                       MOVIMIENTO.COMPANIA=UN_COMPANIA
                   AND MOVIMIENTO.TIPOMOVASOCIADO =UN_TIPO 
                   AND MOVIMIENTO.MOVASOCIADO=UN_NUMERO
               )
    LOOP 
      MI_CADENA := MI_CADENA ||', '|| RS.NOMBRE||' - '||RS.NUMERO;
      MI_I:=MI_I+1;
    END LOOP;

    MI_CADENA:=SUBSTR(MI_CADENA,3);

    BEGIN
        IF MI_I<>0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                --RAISE_APPLICATION_ERROR (-20000,  'No se permite ANULAR porque el contrato esta afectado en almacen por: '||MI_CADENA||'. Si desea borrar debe eliminar estas afectaciones');

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE:= 'CADENA';
                MI_REEMPLAZOS(0).VALOR:= MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                          ,UN_TABLAERROR => 'MOVIMIENTO'
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_AFECTPORALMACEN
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
    END;

    --si esta afectado por contabilidad

    MI_CADENA:='';
    MI_I:=0;

    FOR RS IN ( SELECT  
                       TIPO_COMPROBANTE.NOMBRE, 
                       COMPROBANTE_CNT.NUMERO
                  FROM   
                       COMPROBANTE_CNT 
                  LEFT JOIN TIPO_COMPROBANTE 
                    ON COMPROBANTE_CNT.TIPO = TIPO_COMPROBANTE.CODIGO 
                   AND COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA
                 WHERE 
                       COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                   AND COMPROBANTE_CNT.TIPOCONTRATO =UN_TIPO
                   AND COMPROBANTE_CNT.NUMEROCONTRATO =UN_NUMERO
               )
    LOOP 
      MI_CADENA := MI_CADENA ||', '|| RS.NOMBRE||' - '||RS.NUMERO;
      MI_I:=MI_I+1;
    END LOOP;

    MI_CADENA:=SUBSTR(MI_CADENA,3);
    BEGIN
        IF MI_I<>0 THEN
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;   
                --No se permite ANULAR porque el contrato esta afectado en contabilidad por: '||MI_CADENA||'. Si desea borrar debe eliminar estas afectaciones.

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
                MI_REEMPLAZOS(0).CLAVE := 'CADENA';
                MI_REEMPLAZOS(0).VALOR :=  MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_TABLAERROR=> 'COMPROBANTE_CNT',
                                           UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_AFECTPORCONTAB,
                                           UN_REEMPLAZOS=> MI_REEMPLAZOS);
            END;                               
        END IF;
    END;  
    --si esta afectado por presupuesto

    MI_CADENA:='';
    MI_I:=0;

    FOR RS IN ( SELECT 
                       TIPO_COMPROBPP.NOMBRE, 
                       COMPROBANTE_PPTAL.NUMERO
                  FROM 
                       COMPROBANTE_PPTAL 
                  LEFT JOIN TIPO_COMPROBPP 
                    ON COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA
                   AND COMPROBANTE_PPTAL.TIPO = TIPO_COMPROBPP.CODIGO
                 WHERE 
                       COMPROBANTE_PPTAL.COMPANIA=UN_COMPANIA
                   AND COMPROBANTE_PPTAL.TIPOCONTRATO =UN_TIPO
                   AND COMPROBANTE_PPTAL.NUMEROCONTRATO =UN_NUMERO
               )
    LOOP 
      MI_CADENA := MI_CADENA ||', '|| RS.NOMBRE||' - '||RS.NUMERO;
      MI_I:=MI_I+1;
    END LOOP;

    MI_CADENA:=SUBSTR(MI_CADENA,3);
    BEGIN
        IF MI_I<>0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                --No se permite ANULAR porque el contrato esta afectado en presupuesto por: '||MI_CADENA||'. Si desea borrar debe eliminar estas afectaciones

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'CADENA';
                MI_REEMPLAZOS(0).VALOR :=  MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_TABLAERROR=> 'COMPROBANTE_PPTAL',
                                           UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_AFECTPORPRESUP,
                                           UN_REEMPLAZOS=> MI_REEMPLAZOS);
            END;
        END IF;
      END;

  BEGIN 
    BEGIN
      MI_CAMPOS:='ORDENDECOMPRA.IMPRESO = -1, 
                  ORDENDECOMPRA.ESTADO  = ''A'',
                  ORDENDECOMPRA.MODIFIED_BY='''||UN_USUARIO||''',
					        ORDENDECOMPRA.DATE_MODIFIED = SYSDATE';

      MI_CONDICION:='   ORDENDECOMPRA.COMPANIA   = '''||UN_COMPANIA||''' 
                    AND ORDENDECOMPRA.CLASEORDEN = '''||UN_TIPO||''' 
                    AND ORDENDECOMPRA.NUMERO     = '||UN_NUMERO||'';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'ORDENDECOMPRA',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION); 


      MI_MSGERROR(1).CLAVE := 'NUMERO';
      MI_MSGERROR(1).VALOR := UN_NUMERO;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_UPDATE_O_COMP,
                               UN_REEMPLAZOS => MI_MSGERROR);
   END;                            

  RETURN 1;

END FC_VERIFICACON_ANULACION;

--6

FUNCTION FC_ENVIAR_NOVEDADES_A_NOMINA

/*
    NAME              : FC_ENVIAR_NOVEDADES_A_NOMINA en acces -> Enviar_Nomina_Click()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
    DATE MIGRADOR     : 18/11/2015
    TIME              : 12:00 PM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : YESSICA SANA ROJAS   
    DATE MODIFIED     : 22/08/2017 
    TIME              : 02:00 PM
    DESCRIPTION       : El parametro fecha final se recibe en formato "MM/DD/YYYY"
                        UN_COMPANIA           -> ENTIDAD DESDE LA CUAL SE EJECUTA EL PROCESO
                        UN_VALORTOTALNOVEDAD  -> VALOR ENVIADO DESDE EL CONTROLADOR, TENIENDO EN CUENTA EL VALOR DE LAS DEMAS NOVEDADES DE LA OC
                        UN_ORDENDECOMPRA      -> ORDEN DE COMRA A LA CUAL SE LE ADICINA UNA NOVEDAD
                        UN_USUARIO            -> USUARIO QUE REALIZA EN PROCESO EN LA ENTIDAD
    @NAME: enviarNovedadesaNomina
  */
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NOVEDAD           IN VARCHAR2,
  UN_ORDENDECOMPRA     IN VARCHAR2,
  UN_VALORTOTALNOVEDAD IN PCK_SUBTIPOS.TI_DOBLE,
  UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO  
)

RETURN VARCHAR2
AS

  MI_CODIGO                    VARCHAR2(50 CHAR);
  MI_TIPOCONTRATO              VARCHAR2(3 CHAR);
  MI_RESULTADO                 PCK_SUBTIPOS.TI_RTA_ACME;
  MI_NOVEDADNOMINA             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_COMPANIACONTRATISTAS      VARCHAR2(3 CHAR);
  MI_CAMPOS                    PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                   PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;
  MI_FECHAFINAL                VARCHAR2(30);
  MI_DIASCONTRATO              PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VLRPAGO                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_FECHA_INICIO_CT           DATE;
  MI_FECHA_FINAL_CT            DATE;
  MI_VALORCONTRATO             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_EXISTE                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VALORFINAL                PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VTOTAL                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_DBLVALORTOTCONTRATO       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PAGOS                     PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_SALDO                     PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_IDEMPLEADO                VARCHAR2(10 CHAR);
  MI_TOPEVEHICULO              PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MESPAGOCONTRATO           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MESACTUAL                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_HONORARIOS                PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_COMPANIACTUALIZACONTRATOS VARCHAR2(3 CHAR);
  MI_MAXIDDEEMPLEADO           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INDICADORNOMINA           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INDICADORNOMINA1          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_APELLIDO1TERCERO          VARCHAR2(100 CHAR);
  MI_MSGERROR                  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_REEMPLAZOS                PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_NOMBRE                    TERCERO.NOMBRE%TYPE;
  MI_PINDADICION              PCK_SUBTIPOS.TI_LOGICO;
  MI_PTIPOT                   VARCHAR2(10);
  MI_PFECHAINICIAL            DATE;
  MI_PFECHAFINAL              DATE;
  MI_PCLASEORDEN              VARCHAR2(10); 
  MI_PORDENDECOMPRA           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PFECHA_NOVEDAD           DATE;
  MI_PVALORTOTAL              PCK_SUBTIPOS.TI_DOBLE;
  MI_PVLRAQVEHICULO           PCK_SUBTIPOS.TI_DOBLE;
  MI_PVALORFINAL              PCK_SUBTIPOS.TI_DOBLE;
  MI_POBSERVACIONES           NOVEDADCONTRATO.OBSERVACIONES%TYPE;
  MI_PDEPENDENCIA             ORDENDECOMPRA.DEPENDENCIA%TYPE;
  MI_PHONORARIOS              PCK_SUBTIPOS.TI_DOBLE;
  MI_PVEHICULO                PCK_SUBTIPOS.TI_DOBLE;
  MI_PTOPEVEHICULO            PCK_SUBTIPOS.TI_DOBLE; 
  MI_PTIPORETENCION           NOVEDADCONTRATO.TIPO_RETENCION%TYPE;
  MI_PDIASCONTRATO            PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PTERCERO                 ORDENDECOMPRA.TERCERO%TYPE;
  MI_PFECHACONTRATO           DATE;
  MI_PEXPERIENCIA             ORDENDECOMPRA.PLAZODEENTREGA%TYPE;
  MI_PPROFESION               ORDENDECOMPRA.CLASEDISPONIBILIDAD%TYPE;
  MI_PFECHA_NACTO             DATE;



BEGIN
  BEGIN
      SELECT  NOVEDADCONTRATO.INDADICION,
              NOVEDADCONTRATO.TIPOT,
              NOVEDADCONTRATO.FECHAINICIAL,
              NOVEDADCONTRATO.FECHAFINAL,
              ORDENDECOMPRA.CLASEORDEN,
              ORDENDECOMPRA.NUMERO,
              NOVEDADCONTRATO.FECHA AS FECHANOVEDADCONTRATO,
              NOVEDADCONTRATO.VALORTOTAL as VALORTOTALNOVEDAD,
              NOVEDADCONTRATO.VLR_ALQ_VEHICULO,
              ORDENDECOMPRA.VALORTOTAL AS VALORTOTALORDENCOMPRA,
              NOVEDADCONTRATO.OBSERVACIONES,
              ORDENDECOMPRA.DEPENDENCIA,
              ORDENDECOMPRA.HONORARIOS,
              ORDENDECOMPRA.VEHICULO,
              ORDENDECOMPRA.VALORFINAL,
              NOVEDADCONTRATO.TIPO_RETENCION,
              NOVEDADCONTRATO.DIAS_CONTRATO,
              ORDENDECOMPRA.TERCERO,
              ORDENDECOMPRA.FECHA AS FECHAORDENDECOMPRA,
              ORDENDECOMPRA.PLAZODEENTREGA,
              ORDENDECOMPRA.CLASEDISPONIBILIDAD,
              ORDENDECOMPRA.FECHA_NACTO INTO 
              MI_PINDADICION,
              MI_PTIPOT,
              MI_PFECHAINICIAL,
              MI_PFECHAFINAL,
              MI_PCLASEORDEN,
              MI_PORDENDECOMPRA,
              MI_PFECHA_NOVEDAD,
              MI_PVALORTOTAL,
              MI_PVLRAQVEHICULO,
              MI_PVALORFINAL,
              MI_POBSERVACIONES,
              MI_PDEPENDENCIA,
              MI_PHONORARIOS,
              MI_PVEHICULO,
              MI_PTOPEVEHICULO,
              MI_PTIPORETENCION,
              MI_PDIASCONTRATO,
              MI_PTERCERO,
              MI_PFECHACONTRATO,
              MI_PEXPERIENCIA,
              MI_PPROFESION,
              MI_PFECHA_NACTO
        FROM NOVEDADCONTRATO
    INNER JOIN ORDENDECOMPRA
    ON NOVEDADCONTRATO.COMPANIA       = ORDENDECOMPRA.COMPANIA
    AND NOVEDADCONTRATO.CLASEORDEN    = ORDENDECOMPRA.CLASEORDEN
    AND NOVEDADCONTRATO.ORDENDECOMPRA = ORDENDECOMPRA.NUMERO
    WHERE NOVEDADCONTRATO.NOVEDAD     = UN_NOVEDAD
    AND ORDENDECOMPRA.NUMERO          = UN_ORDENDECOMPRA;

END;
  SELECT NOMBRE INTO MI_NOMBRE FROM TERCERO WHERE COMPANIA = UN_COMPANIA AND NIT = MI_PTERCERO;

  MI_CODIGO       := '';
  MI_TIPOCONTRATO := '';
  MI_RESULTADO    := '';

  --BEGIN
  MI_VTOTAL:=MI_PVALORFINAL+UN_VALORTOTALNOVEDAD;  
  MI_COMPANIACTUALIZACONTRATOS:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                      UN_NOMBRE    => 'COMPANIA DE NOMINA PARA ACTUALIZAR CONTRATOS', 
                                                      UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(), 
                                                      UN_FECHA_PAR => SYSDATE);
    SELECT 
           NOVEDADNOMINA 
      INTO MI_NOVEDADNOMINA
      FROM 
           TIPOORDENDECOMPRA
     WHERE 
           COMPANIA = UN_COMPANIA AND
           CODIGO = MI_PCLASEORDEN;

IF MI_NOVEDADNOMINA = -1 THEN
  MI_COMPANIACONTRATISTAS:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'COMPANIA DE NOMINA DE CONTRATISTAS', 
                                                 UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(), 
                                                 UN_FECHA_PAR => SYSDATE);
  MI_INDICADORNOMINA:=0;
  MI_INDICADORNOMINA1:=0;
  BEGIN   
    IF MI_PINDADICION <>0 THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
      --RAISE_APPLICATION_ERROR (-20000,  'La novedad ya se encuentra registrada en nomina de contratistas, no se permite actualizar');
    END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_NOVE_REG);
  END;  

  IF MI_PTIPOT='ACL' THEN

    BEGIN
      BEGIN
          MI_VALORES := ' FECHA_FINAL_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                          MODIFIED_BY    = ''' || UN_USUARIO||''',
					                DATE_MODIFIED  = SYSDATE';

          MI_CONDICION := ' COMPANIA          = ''' || UN_COMPANIA || ''' 
                       AND NUMERO_CONTRATO    = ' || MI_PORDENDECOMPRA || '
                       AND APELLIDO1 = ''NO APLICA''';

          MI_INDICADORNOMINA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_VALORES,
                                                  UN_CONDICION => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
          MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_FECHA_F,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;                                    

    BEGIN 
      BEGIN

        MI_VALORES := ' TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                        MODIFIED_BY    = ''' || UN_USUARIO||''',
					              DATE_MODIFIED  = SYSDATE';
        MI_CONDICION := '    COMPANIA   = ''' || UN_COMPANIA || ''' 
                         AND CLASEORDEN = ''' || MI_PCLASEORDEN || '''
                         AND NUMERO     = ' || MI_PORDENDECOMPRA;

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'ORDENDECOMPRA',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_VALORES,
                                               UN_CONDICION => MI_CONDICION);

        MI_MSGERROR(1).CLAVE := 'FECHALIQUIDACION';
        MI_MSGERROR(1).VALOR := MI_PFECHAFINAL;

        MI_MSGERROR(2).CLAVE := 'NUMERO';
        MI_MSGERROR(2).VALOR := MI_PORDENDECOMPRA;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_FECHA_L,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;  
     BEGIN
      SELECT
             FECHA_INICIO_CT,
             FECHA_FINAL_CT,
             NVL(VALOR_CONTRATO,0) 
        INTO MI_FECHA_INICIO_CT,MI_FECHA_FINAL_CT,MI_VALORCONTRATO
        FROM 
             PERSONAL 
       WHERE 
             COMPANIA = UN_COMPANIA
         AND TIPO_CONTRATO= MI_PCLASEORDEN
         AND NUMERO_CONTRATO= MI_PORDENDECOMPRA;   

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:=0;
    END;  

    MI_DIASCONTRATO:=PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => MI_FECHA_INICIO_CT,
                                                        UN_FECHAFIN => MI_FECHA_FINAL_CT);
    IF MI_DIASCONTRATO<0 THEN
      MI_DIASCONTRATO:=0;
    END IF; 

    MI_VLRPAGO:=PCK_SYSMAN_UTL.FC_ROUND((NVL(MI_VALORCONTRATO,0)/MI_DIASCONTRATO)*30,0);
    MI_VLRPAGO:=NVL(MI_VLRPAGO,0);

     SELECT
            COUNT(*) 
       INTO MI_EXISTE
       FROM 
            PAGOS_CONTRATOS
      WHERE 
            PAGOS_CONTRATOS.COMPANIA            = UN_COMPANIA
        AND PAGOS_CONTRATOS.ID_DE_PROCESO       = '90'
        AND PAGOS_CONTRATOS.ANO                 = EXTRACT(YEAR FROM MI_PFECHA_NOVEDAD) --'" (Me![SubNovedadContrato].Form![fecha]
        AND PAGOS_CONTRATOS.MES                 = LPAD(EXTRACT(MONTH FROM MI_PFECHA_NOVEDAD),2,'0')--'" (Month(Me![SubNovedadContrato].Form![fecha]), 2)
        AND PAGOS_CONTRATOS.PERIODO             = '03'
        AND PAGOS_CONTRATOS.TIPO_CONTRATO       = MI_PCLASEORDEN
        AND PAGOS_CONTRATOS.NUMERO_CONTRATO     = MI_PORDENDECOMPRA
        AND PAGOS_CONTRATOS.MONTO_INICIAL      <> 0; 

     IF MI_EXISTE>0 THEN
        BEGIN 
          BEGIN 

              MI_VALORES :=       'VALOR_CUOTA = '|| MI_VLRPAGO || ', 
                                   VALPAGO     = '|| MI_VLRPAGO || ',
                                   MODIFIED_BY    = ''' || UN_USUARIO||''',
                                   DATE_MODIFIED  = SYSDATE';

              MI_CONDICION := ' COMPANIA          = ''' || UN_COMPANIA || ''' 
                            AND ID_DE_PROCESO     = ''90''
                            AND ANO               = EXTRACT(YEAR FROM TO_DATE(''' || MI_PFECHA_NOVEDAD || ''',''dd/MM/yy''))
                            AND MES               = LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHA_NOVEDAD || ''',''dd/MM/yy'')),2,''0'')
                            AND PERIODO           = ''3''
                            AND TIPO_CONTRATO     = ''' || MI_PCLASEORDEN || '''
                            AND NUMERO_CONTRATO   = ' || MI_PORDENDECOMPRA; 

              MI_INDICADORNOMINA1 := PCK_DATOS.FC_ACME (UN_TABLA     => 'PAGOS_CONTRATOS',
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_VALORES,
                                                        UN_CONDICION => MI_CONDICION);               

              MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
              MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_VALOR_CUO,
                                       UN_REEMPLAZOS => MI_MSGERROR);
           END;                                            


      MI_RESULTADO := 2;
      --MI_RESULTADO || ' Acta de liquidación ';

     ELSE
         BEGIN
             RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
             MI_REEMPLAZOS(0).CLAVE:= 'MES';
             MI_REEMPLAZOS(0).VALOR:= LPAD(EXTRACT(MONTH FROM MI_PFECHA_NOVEDAD),2,'0');
             MI_REEMPLAZOS(1).CLAVE:= 'ANO';
             MI_REEMPLAZOS(1).VALOR:= EXTRACT(YEAR FROM MI_PFECHA_NOVEDAD);
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_NOVEDADITEMREL
                                           ,UN_TABLAERROR => 'D_NOVEDADCONTRATO');
         END;
     END IF;

  ELSIF MI_PTIPOT='ACS' THEN

    BEGIN
      BEGIN
          MI_VALORES := ' FECHA_FINAL_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                          MODIFIED_BY    = ''' || UN_USUARIO||''',
					                DATE_MODIFIED  = SYSDATE';
                          --NOVEDAD        = ''ACS''';                            ********* PENDIENTE CAMPO NO EXISTE EN TABLA PERSONAL
          MI_CONDICION := ' COMPANIA          = ''' || UN_COMPANIA || ''' 
                       AND NUMERO_CONTRATO    = ' || MI_PORDENDECOMPRA || '
                       AND PERSONAL.APELLIDO1 = ''NO APLICA''';

          MI_INDICADORNOMINA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_VALORES,
                                                   UN_CONDICION => MI_CONDICION); 

          MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
          MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_FECHA_F,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;                                           

    MI_RESULTADO:= 3;

    --MI_RESULTADO || ' Acta de suspención ';

  ELSIF MI_PTIPOT='ARI' THEN
   BEGIN 
    BEGIN
        MI_VALORES := ' FECHA_INICIO_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAINICIAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                        FECHA_FINAL_CT  = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                        MODIFIED_BY     = ''' || UN_USUARIO||''',
					              DATE_MODIFIED   = SYSDATE';
                        -- NOVEDAD        = NULL';
        MI_CONDICION := '    COMPANIA           = ''' || UN_COMPANIA || ''' 
                         AND NUMERO_CONTRATO    = ' || MI_PORDENDECOMPRA || '
                         AND PERSONAL.APELLIDO1 = ''NO APLICA''';

        MI_INDICADORNOMINA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_VALORES,
                                                 UN_CONDICION => MI_CONDICION);     

        MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
        MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_FECHAS_CT,
                               UN_REEMPLAZOS => MI_MSGERROR);
   END;                                          

    SELECT 
           COUNT(*) 
      INTO MI_EXISTE
      FROM 
           PAGOS_CONTRATOS
     WHERE 
           PAGOS_CONTRATOS.COMPANIA            = UN_COMPANIA
       AND PAGOS_CONTRATOS.ID_DE_PROCESO       = '90'
       AND PAGOS_CONTRATOS.ANO                 = EXTRACT(YEAR FROM MI_PFECHAINICIAL) --'" (Me![SubNovedadContrato].Form![fecha]
       AND PAGOS_CONTRATOS.MES                 = LPAD(EXTRACT(MONTH FROM MI_PFECHAINICIAL),2,'0')--'" (Month(Me![SubNovedadContrato].Form![fecha]), 2)
       AND PAGOS_CONTRATOS.PERIODO             = '03'
       AND PAGOS_CONTRATOS.TIPO_CONTRATO       = MI_PCLASEORDEN
       AND PAGOS_CONTRATOS.NUMERO_CONTRATO     = MI_PORDENDECOMPRA
       AND PAGOS_CONTRATOS.MONTO_INICIAL      <> 0; 

    IF MI_EXISTE=0 THEN

      BEGIN 
        BEGIN 

            MI_CAMPOS:= 'COMPANIA,
                        ID_DE_PROCESO,
                        ANO, 
                        MES,
                        PERIODO,
                        ID_DE_EMPLEADO,
                        TIPO_CONTRATO,
                        NUMERO_CONTRATO,
                        MONTO_INICIAL, 
                        SALDO, 
                        VALOR_CUOTA, 
                        OBSERVACIONES,
                        VALPAGO,
                        PERIODOCOBRO,
                        PAGO, 
                        VALOR_APLICAR,
                        ID_DE_CONCEPTO
                        ,DATE_CREATED
                        ,CREATED_BY) ';

            MI_VALORES:='SELECT 
                                PAGOS_CONTRATOS.COMPANIA, 
                                PAGOS_CONTRATOS.ID_DE_PROCESO, 
                                MAX(EXTRACT(YEAR FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy''))) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS ANO, 
                                MAX(LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),2,''0'')) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS MES, 
                                PAGOS_CONTRATOS.PERIODO, 
                                MAX(PAGOS_CONTRATOS.ID_DE_EMPLEADO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS ID_DE_EMPLEADO, 
                                PAGOS_CONTRATOS.TIPO_CONTRATO AS TIPO_CONTRATO, 
                                PAGOS_CONTRATOS.NUMERO_CONTRATO AS NUMERO_CONTRATO,
                                MAX(PAGOS_CONTRATOS.MONTO_INICIAL) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS MONTO_INICIAL, 
                                MAX(PAGOS_CONTRATOS.SALDO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS SALDO, 
                                MAX(PAGOS_CONTRATOS.VALOR_CUOTA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS VALOR_CUOTA,
                                ''Reiniciacion de contrato'' AS OBSERVACIONES, 
                                MAX(PAGOS_CONTRATOS.VALPAGO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS VALPAGO, 
                                PAGOS_CONTRATOS.PERIODOCOBRO, 
                                MAX(PAGOS_CONTRATOS.PAGO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS PAGO, 
                                MAX(PAGOS_CONTRATOS.VALOR_APLICAR) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS VALOR_APLICAR,
                                ''002'',
                                SYSDATE
                                ,'''||UN_USUARIO||'''
                           FROM 
                                PAGOS_CONTRATOS
                          WHERE 
                                PAGOS_CONTRATOS.TIPO_CONTRATO = ''' || MI_PCLASEORDEN || '''
                            AND NUMERO_CONTRATO               = ' || MI_PORDENDECOMPRA || '
                       GROUP BY 
                                PAGOS_CONTRATOS.COMPANIA, 
                                PAGOS_CONTRATOS.ID_DE_PROCESO, 
                                PAGOS_CONTRATOS.PERIODO, 
                                PAGOS_CONTRATOS.TIPO_CONTRATO, 
                                PAGOS_CONTRATOS.NUMERO_CONTRATO, 
                                ''Reiniciacion de contrato'', 
                                PAGOS_CONTRATOS.PERIODOCOBRO
                       ORDER BY 
                                MAX(EXTRACT(YEAR FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy''))) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) DESC ,
                                MAX(LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),2,''0'')) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) DESC';

            MI_INDICADORNOMINA1 := PCK_DATOS.FC_ACME (UN_TABLA  => 'PAGOS_CONTRATOS',
                                                     UN_ACCION  => 'IS',
                                                     UN_CAMPOS  => MI_CAMPOS,
                                                     UN_VALORES => MI_CONDICION); 

            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_PAGO_CT,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;                                          

      MI_RESULTADO := 4;
      --MI_RESULTADO || '  Acta de reiniciación del contrato ';
    END IF;

  --ELSIF MI_PTIPOT='CES' THEN
    --OJO*********formular MEJOR DESPUES DE ENERO DE 2009

  ELSIF MI_PTIPOT='ADI' THEN   
    IF NVL(MI_PVALORTOTAL,0)>0 OR NVL(MI_PVLRAQVEHICULO,0)>0 THEN
      IF NVL(MI_PVALORTOTAL,0)>0 THEN
        SELECT 
               COUNT(*) 
          INTO MI_EXISTE
          FROM 
               ORDENDECOMPRA
         WHERE 
               COMPANIA   = UN_COMPANIA
           AND CLASEORDEN = MI_PCLASEORDEN
           AND NUMERO     = MI_PORDENDECOMPRA;

        IF MI_EXISTE > 0 THEN 
          SELECT
                 VALORFINAL
            INTO MI_VALORFINAL
            FROM 
                 ORDENDECOMPRA
           WHERE 
                 COMPANIA   = UN_COMPANIA
             AND CLASEORDEN = MI_PCLASEORDEN
             AND NUMERO     = MI_PORDENDECOMPRA;

          IF MI_VALORFINAL > 0 THEN

            SELECT 
                   COUNT(*)
              INTO MI_EXISTE
              FROM 
                   PERSONAL
             WHERE 
                   COMPANIA         = MI_COMPANIACTUALIZACONTRATOS -----------------------------------NOTA: EL CODIGO ORIGINAL LO COMPARABA CON COMPANIA = '002' QUEMADO
               AND TIPO_CONTRATO    = MI_PCLASEORDEN
               AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA;
            BEGIN 
              IF MI_EXISTE = 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
               -- RAISE_APPLICATION_ERROR (-20000,  'No se ha registrado el acta de inicio en nomina de contratistas, Por favor realice dicho proceso antes de la adiciÃ³n');
              END IF;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTA_INI);

            END;    
            BEGIN 
              BEGIN 
                  MI_VALORES := ' VALORCONTRATO   = ' || NVL(MI_VALORFINAL,0) || '+' || NVL(UN_VALORTOTALNOVEDAD,0) || ',
                                  FECHA_FINAL_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  MODIFIED_BY    = ''' || UN_USUARIO||''',
                                  DATE_MODIFIED  = SYSDATE';

                  MI_CONDICION := '    COMPANIA           = ''' || UN_COMPANIA || ''' 
                                   AND NUMERO_CONTRATO    = ' || MI_PORDENDECOMPRA || '
                                   AND TIPO_CONTRATO      = ''' || MI_PCLASEORDEN || '''
                                   AND PERSONAL.APELLIDO1 = ''NO APLICA''';

                  MI_INDICADORNOMINA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_VALORES,
                                                           UN_CONDICION => MI_CONDICION); 

                  MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
                  MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_VALOR_CT,
                                         UN_REEMPLAZOS => MI_MSGERROR);
             END;                                             

          ELSE

          BEGIN
          --RAISE_APPLICATION_ERROR (-20000,  'Por favor revisar el valor final del contrato desde donde se registra el contrato');
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_VALORF_CT);
          END;

          END IF;    
        END IF;  

        SELECT 
               COUNT(*) 
          INTO MI_EXISTE
          FROM 
               PERSONAL
         WHERE 
               COMPANIA         = UN_COMPANIA
           AND TIPO_CONTRATO    = MI_PCLASEORDEN
           AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA; 

        IF MI_EXISTE > 0 THEN
          SELECT 
                 VALOR_CONTRATO 
            INTO MI_VALORCONTRATO
            FROM 
                 PERSONAL
           WHERE 
                 COMPANIA         = UN_COMPANIA
             AND TIPO_CONTRATO    = MI_PCLASEORDEN
             AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA; 

            MI_DBLVALORTOTCONTRATO:=NVL(MI_VALORCONTRATO,0);
        END IF;

         SELECT  --HISTORICOS_CONTRATOS.ID_DE_EMPLEADO, SE QUITA EL AGRUPAMIENTO YA QUE ES INNECESARIO
                COUNT(*)
           INTO MI_EXISTE
           FROM 
                HISTORICOS_CONTRATOS
          WHERE 
                COMPANIA         = UN_COMPANIA
            AND ID_DE_PROCESO    = '90'
            AND ID_DE_CONCEPTO  In ('002','590')
            AND TIPO_CONTRATO    = MI_PCLASEORDEN
            AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA;
       --GROUP BY HISTORICOS_CONTRATOS.ID_DE_EMPLEADO

        IF MI_EXISTE > 0 THEN 
           SELECT  --HISTORICOS_CONTRATOS.ID_DE_EMPLEADO, SE QUITA EL AGRUPAMIENTO YA QUE ES INNECESARIO
                  SUM(NVL(VALOR_CONCEPTO,0)) AS PAGOS
             INTO MI_PAGOS
             FROM 
                  HISTORICOS_CONTRATOS
            WHERE
                  COMPANIA         = UN_COMPANIA
              AND ID_DE_PROCESO    = '90'
              AND ID_DE_CONCEPTO  In ('002','590')
              AND TIPO_CONTRATO    = MI_PCLASEORDEN
              AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA;
       --GROUP BY HISTORICOS_CONTRATOS.ID_DE_EMPLEADO
        ELSE 
          MI_PAGOS:=0;
        END IF;

        MI_SALDO:=MI_DBLVALORTOTCONTRATO - MI_PAGOS;
        MI_SALDO:=NVL(MI_SALDO,0);

        IF MI_SALDO < 0 THEN
          MI_SALDO:=0;
        END IF;  
        SELECT 
               COUNT(*) 
          INTO MI_EXISTE
          FROM 
               PAGOS_CONTRATOS
         WHERE 
               COMPANIA         =UN_COMPANIA
           AND ID_DE_PROCESO    ='90'
           AND PERIODO          ='03'
           AND TIPO_CONTRATO    =MI_PCLASEORDEN
           AND NUMERO_CONTRATO  =MI_PORDENDECOMPRA
           AND MONTO_INICIAL<>0;

        IF MI_EXISTE>0 THEN
          SELECT 
                 ID_DE_EMPLEADO 
                 INTO MI_IDEMPLEADO
            FROM 
                 PAGOS_CONTRATOS
           WHERE 
                 COMPANIA        =UN_COMPANIA
             AND ID_DE_PROCESO    ='90'
             AND PERIODO          ='03'
             AND TIPO_CONTRATO    =MI_PCLASEORDEN
             AND NUMERO_CONTRATO  =MI_PORDENDECOMPRA
             AND MONTO_INICIAL<>0;

          BEGIN 
            BEGIN 
                MI_VALORES :=  'COMPANIA              = ''' || UN_COMPANIA || ''',
                                ID_DE_PROCESO         = ''90'',
                                ANO                   = ''' ||  EXTRACT(YEAR FROM MI_PFECHAINICIAL) ||  ''',
                                PERIODO               = ''03'',
                                SALDO                 = '|| MI_SALDO ||',
                                OBSERVACIONES         = ''' || MI_POBSERVACIONES || ''',
                                S_VALOR_CON_ADICION   = ' || UN_VALORTOTALNOVEDAD || ',
                                T_VALOR_CON_ADICION   = ' || MI_VTOTAL || ',
                                MODIFIED_BY    = ''' || UN_USUARIO||''',
                                DATE_MODIFIED  = SYSDATE';

                MI_CONDICION :='    COMPANIA            = ''' || UN_COMPANIA || ''' 
                                AND ID_DE_PROCESO       = ''90''
                                AND ANO                 = ' ||  EXTRACT(YEAR FROM MI_PFECHAINICIAL) ||  '
                                AND MES                 = LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),2,''0'')
                                AND NUMERO_CONTRATO     = ' || MI_PORDENDECOMPRA || '
                                AND TIPO_CONTRATO       = ''' || MI_PCLASEORDEN || '''
                                AND ID_DE_EMPLEADO      =''' || MI_IDEMPLEADO;


                MI_INDICADORNOMINA1 := PCK_DATOS.FC_ACME (UN_TABLA     => 'PAGOS_CONTRATOS',
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_VALORES,
                                                          UN_CONDICION => MI_CONDICION); 

                MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
                MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
              END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_PAGO_CT,
                                       UN_REEMPLAZOS => MI_MSGERROR);
           END;  

        ELSE
          BEGIN
          --RAISE_APPLICATION_ERROR (-20000,  'El contrato no registra acta de inicio, no se puede continuar con el proceso');   

            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTAINI);
          END;

        END IF;

      END IF;

      IF NVL(MI_PVLRAQVEHICULO,0)>0 THEN
         SELECT  
                COUNT(*)
           INTO MI_EXISTE
           FROM 
                ORDENDECOMPRA
          WHERE 
                COMPANIA   = UN_COMPANIA
            AND CLASEORDEN = MI_PCLASEORDEN
            AND NUMERO     = MI_PORDENDECOMPRA;

        IF MI_EXISTE > 0 THEN
          SELECT 
                 TOPEVEHICULO 
            INTO MI_TOPEVEHICULO
            FROM 
                 PERSONAL
           WHERE 
                 COMPANIA         = UN_COMPANIA
             AND TIPO_CONTRATO    = MI_PCLASEORDEN
             AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA; 

          IF MI_TOPEVEHICULO >0 THEN
            BEGIN 
              BEGIN
                  MI_VALORES := ' FECHA_FINAL_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  TOPEVEHICULO   = '|| MI_TOPEVEHICULO ||' + '||NVL(MI_PVLRAQVEHICULO,0) || ',
                                  MODIFIED_BY    = ''' || UN_USUARIO||''',
                                  DATE_MODIFIED  = SYSDATE';
                  MI_CONDICION := '             COMPANIA     = ''' || UN_COMPANIA || ''' 
                                   AND NUMERO_CONTRATO       = ' || MI_PORDENDECOMPRA || '
                                   AND TIPO_CONTRATO         = ''' || MI_PCLASEORDEN || '''
                                   AND PERSONAL.APELLIDO1    = ''NO APLICA''';

                  MI_INDICADORNOMINA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_VALORES,
                                                           UN_CONDICION => MI_CONDICION);
                  MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
                  MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_FECHA_F,
                                         UN_REEMPLAZOS => MI_MSGERROR);
             END;                                         

          ELSE
             BEGIN
--            RAISE_APPLICATION_ERROR (-20000,  'Por favor revisar el valor maximo a pagar en alquiler de vehiculo desde donde se registra el contrato');
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_VALOR_ALQU);
              END;
          END IF;  
        END IF;    

      END IF;
    ELSE
      BEGIN
  --      RAISE_APPLICATION_ERROR (-20000,  'El campo valor o valor alquiler vehiculo debe ser diligenciado con el valor de la adiciÃ³n debido a que este dato va a modificar el pago');

          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_VALOR_ADI);
        END;

    END IF;

    BEGIN 
      BEGIN 
          MI_VALORES    := ' INDADICION = -1 ,
                             MODIFIED_BY    = ''' || UN_USUARIO||''',
					                   DATE_MODIFIED  = SYSDATE';

          MI_CONDICION  := 'COMPANIA            = ''' || UN_COMPANIA || ''' 
                        AND CLASEORDEN          = ''' || MI_PCLASEORDEN || '''
                        AND NOVEDAD             = ' || UN_NOVEDAD || '
                        AND TIPOT               = ''' || MI_PTIPOT ||'''
                        AND ORDENDECOMPRA       = '|| MI_PORDENDECOMPRA;

           PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'NOVEDADCONTRATO',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_VALORES,
                                                  UN_CONDICION => MI_CONDICION);              


            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_NOVE_CT,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;                                       

    --bloquear cuando paso la ADI a nomina de contratistas - Se debe bloquear lols controles ----------------------------------------------------------

    MI_RESULTADO := 5;
    -- MI_RESULTADO || ' Adicion al contrato ';

  ELSIF MI_PTIPOT='ACI' THEN  
    IF MI_PDEPENDENCIA IS NULL OR MI_PDEPENDENCIA = '' THEN
      RAISE_APPLICATION_ERROR (-20000,  'No existe la dependencia, por favor revise la dependencia a la cual esta enviando la novedad');
    END IF; 

    SELECT 
           COUNT(*) 
      INTO MI_EXISTE
      FROM 
           PAGOS_CONTRATOS
     WHERE 
           COMPANIA         = UN_COMPANIA
       AND TIPO_CONTRATO    = MI_PCLASEORDEN
       AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA; 

    IF MI_EXISTE > 0 THEN
      SELECT 
             TO_NUMBER(MES)
        INTO MI_MESPAGOCONTRATO
        FROM 
             PAGOS_CONTRATOS
       WHERE 
             COMPANIA         = UN_COMPANIA
         AND TIPO_CONTRATO    = MI_PCLASEORDEN
         AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA; 

      SELECT 
             TO_NUMBER(EXTRACT(MONTH FROM SYSDATE)) 
        INTO MI_MESACTUAL
        FROM DUAL ; 
      BEGIN
        IF MI_MESPAGOCONTRATO <> MI_MESACTUAL THEN
          --RAISE_APPLICATION_ERROR (-20000,  'La novedad no se puede actualizar en nomina porque fue registrada en mes(es) anterior(es)');
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_NOM);
      END;
    END IF;

    SELECT 
           COUNT(*)
      INTO MI_EXISTE 
      FROM PERSONAL
     WHERE COMPANIA         = UN_COMPANIA
       AND APELLIDO1        = 'NO APLICA'
       AND TIPO_CONTRATO    = MI_PCLASEORDEN
       AND NUMERO_CONTRATO  = MI_PORDENDECOMPRA;

    IF MI_EXISTE > 0 THEN
      --(If MsgBox("El contrato " ORDENDECOMPRA & " ya existe en nÃ³mina de contratistas."  & "Desea Continuar y actualizar la informaciÃ³n?", vbInformation + vbYesNo, "Sysman Software") = vbNo Then
                  --Exit Sub
        --Else
      --Crear el registro si no existe.  ********** REVERSADO   ***************   
        SELECT 
               ID_DE_EMPLEADO 
          INTO MI_IDEMPLEADO
          FROM 
               PERSONAL
         WHERE 
               COMPANIA         =UN_COMPANIA
           AND APELLIDO1        ='NO APLICA'
           AND TIPO_CONTRATO    =MI_PCLASEORDEN
           AND NUMERO_CONTRATO  =MI_PORDENDECOMPRA;

      BEGIN 
        BEGIN 
            MI_VALORES :=  'FECHA_INICIO_CT  = '''|| MI_PFECHAINICIAL || ''',
                            SALARIO_BASE_IBC = '|| MI_PHONORARIOS ||' ,
                            tope_veh_mensual = '|| MI_PVEHICULO ||' ,
                            topevehiculo     = '|| MI_PTOPEVEHICULO ||', 
                            VALORCONTRATO    = '|| UN_VALORTOTALNOVEDAD || ',
                            FECHA_FINAL_CT   = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                            DEPENDENCIA      = '|| MI_PDEPENDENCIA ||',               
                            TIPORET          = '''|| MI_PTIPORETENCION || '''
                            MODIFIED_BY      = ''' || UN_USUARIO||''',
					                  DATE_MODIFIED    = SYSDATE'; 

                            -- AUXILIAR = '''|| UN_AUXILIAR ||''', 
                            -- NOMBREAUXILIAR = '''|| UN_NOMBREAUXILIAR ||''', --  PENDIENTE POR CREAR CAMPOS (NO ESTA DEFINIDO AÃšN) *****

            MI_CONDICION :='    COMPANIA            = ''' || UN_COMPANIA || ''' 
                            AND APELLIDO1           =''NO APLICA''
                            AND NUMERO_CONTRATO     = ' || MI_PORDENDECOMPRA || '
                            AND TIPO_CONTRATO       = ''' || MI_PCLASEORDEN;

            MI_INDICADORNOMINA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_VALORES,
                                                   UN_CONDICION => MI_CONDICION); 

            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_CONT_P,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;                                       

         SELECT 
                NVL(VALORFINAL,0), 
                NVL(HONORARIOS,0) 
           INTO MI_VALORFINAL,
                MI_HONORARIOS
           FROM  
                ORDENDECOMPRA
          WHERE 
                COMPANIA      =UN_COMPANIA
            AND CLASEORDEN    =MI_PCLASEORDEN
            AND NUMERO        =MI_PORDENDECOMPRA;

      IF MI_HONORARIOS <= 0 THEN
        MI_HONORARIOS:=PCK_SYSMAN_UTL.FC_ROUND(MI_VALORFINAL/NVL(MI_PDIASCONTRATO,0)*30,0);
      END IF;

      BEGIN 
        BEGIN 
            MI_VALORES :=  '  MONTO_INICIAL         = ' || UN_VALORTOTALNOVEDAD || ',
                              SALDO                 = ' || UN_VALORTOTALNOVEDAD || ',
                              VALOR_CUOTA           = ' || UN_VALORTOTALNOVEDAD || ',
                              OBSERVACIONES         = ''' || MI_POBSERVACIONES || ''',
                              VALPAGO               = '|| MI_HONORARIOS || ',
                              MODIFIED_BY           = ''' || UN_USUARIO||''',
					                    DATE_MODIFIED         = SYSDATE';


            MI_CONDICION :='    COMPANIA          = ''' || UN_COMPANIA || ''' 
                            AND ID_DE_PROCESO     = ''90''
                            AND ANO               = EXTRACT(YEAR FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')) 
                            AND MES               = LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),2,''0'')
                            AND NUMERO_CONTRATO   = ' || MI_PORDENDECOMPRA || '
                            AND TIPO_CONTRATO     = ''' || MI_PCLASEORDEN || '''
                            AND ID_DE_EMPLEADO    =''' || MI_IDEMPLEADO;

            MI_INDICADORNOMINA1 := PCK_DATOS.FC_ACME (UN_TABLA     => 'PAGOS_CONTRATOS',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_VALORES,
                                                      UN_CONDICION => MI_CONDICION); 

            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_MONTO_INI,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;


    ELSE 
      MI_RESULTADO := 1;

      --MI_RESULTADO || 'La novedad de iniciacion del contrato aplica en el momento de enviar el contrato a nomina, debido 
      --                 a que el contrato no puede ser pasado a nomina, mientras no tenga acta de iniciacion.';

      SELECT 
             NVL(MAX(ID_DE_EMPLEADO),0)
        INTO MI_MAXIDDEEMPLEADO
        FROM 
             PERSONAL 
       WHERE 
             COMPANIA=MI_COMPANIACTUALIZACONTRATOS;

      SELECT 
             APELLIDO1
        INTO MI_APELLIDO1TERCERO
        FROM 
             TERCERO
       WHERE 
             NIT=MI_NOMBRE;

      BEGIN 
        BEGIN 
            MI_CAMPOS:= ' COMPANIA, 
                          ID_DE_EMPLEADO, 
                          NUMERO_DCTO, 
                          TIPO_CONTRATO, 
                          NUMERO_CONTRATO, 
                          NOMBRES, 
                          VALOR_CONTRATO, 
                          FECHA_INICIO_CT,      
                          TOPEVEHICULO,          
                          FECHANCTO, 
                          DEPENDENCIA,        
                          TIPORET,
                          APELLIDO1,
                          SEXO
                          ,DATE_CREATED
                          ,CREATED_BY';
                          -- AUXILIAR, 
                          -- NOMBREAUXILIAR, 
                           -- DIAS_CONTRATO,
                           -- PROFESION, 
                           -- EXPERIENCIA, 

                           --SEXO    -> CAMOPO OBLIGATORIO EN PERSOAL Y NO EXISTE EN TERCERO

            MI_VALORES:='''' || UN_COMPANIA ||''', 
                          ' || (MI_MAXIDDEEMPLEADO+1) || ', 
                          ''' || MI_NOMBRE || ''',
                          ''' || MI_PCLASEORDEN || ''',
                          ''' || MI_PORDENDECOMPRA || ''',
                          ''' || MI_PTERCERO || ''',
                          ''' || MI_PVALORFINAL || ''',
                          ''' || MI_PFECHACONTRATO || ''', 
                          ' || NVL(MI_PTOPEVEHICULO, 0) || ', 
                          ''' || MI_PFECHA_NACTO || ''',
                         ''' || MI_PDEPENDENCIA || ''',
                          ''' || NVL(MI_PTIPORETENCION,0) ||'''
                          ''' || MI_APELLIDO1TERCERO ||''',''M''
                          ,SYSDATE
                          ,'''||UN_USUARIO||'';

                          -- ''' || UN_AUXILIAR || ''',
                          -- ''' || UN_NOMBREAUXILIAR || ''',
                          --  ' || MI_PDIASCONTRATO || ',
                          -- ''' || MI_PPROFESION || ''',
                          --    ''' || MI_PEXPERIENCIA || ''', 

            MI_INDICADORNOMINA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                      UN_ACCION    => 'I',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_VALORES   => MI_VALORES); 

            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_PERS,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;


      BEGIN 
        BEGIN
            MI_CAMPOS:= ' COMPANIA, 
                          ID_DE_PROCESO, 
                          ANO, 
                          MES, 
                          PERIODO, 
                          ID_DE_EMPLEADO, 
                          ID_DE_CONCEPTO, 
                          TIPO_CONTRATO, 
                          NUMERO_CONTRATO, 
                          MONTO_INICIAL, 
                          SALDO, 
                          VALOR_CUOTA, 
                          OBSERVACIONES, 
                          VALPAGO, 
                          PERIODOCOBRO, 
                          PAGO, 
                          VALOR_APLICAR, 
                          T_VALOR_CON_ADICION
                          ,DATE_CREATED
                          ,CREATED_BY';

            MI_VALORES:='''' || UN_COMPANIA ||''', 
                          '' 90 '', ' ||
                          TO_CHAR(MI_PFECHAINICIAL,'YYYY') || ',' ||
                          LPAD(TO_CHAR(MI_PFECHAINICIAL,'MM'),2) || ',                          
                          ''03'',
                          ''' || (MI_MAXIDDEEMPLEADO+1) || ''',
                          ''002'',
                          ''' || MI_PCLASEORDEN || ''',
                          ''' || MI_PORDENDECOMPRA || ''',
                          ' || MI_PVALORFINAL || ', 
                          ' || MI_PVALORFINAL || ', 
                          ' || NVL(MI_PHONORARIOS, 0) || ',
                          ''' || MI_POBSERVACIONES || ''',
                          ' || NVL(MI_PHONORARIOS, 0) || ',
                          ''03'',
                          0,
                          0,
                          ' || MI_VTOTAL || '
                          ,SYSDATE
                          ,'''||UN_USUARIO||'';


            MI_INDICADORNOMINA1  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PAGOS_CONTRATOS',
                                                      UN_ACCION    => 'I',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_VALORES   => MI_VALORES); 

            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_PAGO_CT,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;

      BEGIN 
        BEGIN 
          MI_VALORES :=  'FECHA_INICIO_CT     = '''|| MI_PFECHAINICIAL || ''',
                          SALARIO_BASE_IBC    = '|| MI_PHONORARIOS ||' ,
                          tope_veh_mensual    = '|| NVL(MI_PVEHICULO,0) ||' ,
                          topevehiculo        = '|| MI_PTOPEVEHICULO ||', 
                          VALORCONTRATO       = '|| UN_VALORTOTALNOVEDAD || ',
                          FECHA_FINAL_CT      = '''|| MI_PFECHAFINAL || ''',
                          DEPENDENCIA         = '|| MI_PDEPENDENCIA ||', 
                          TIPORET='''|| MI_PTIPORETENCION || ',
                          MODIFIED_BY    = ''' || UN_USUARIO||''',
					                DATE_MODIFIED  = SYSDATE'; 

                         -- AUXILIAR            = '''|| UN_AUXILIAR ||''', 
                         -- NOMBREAUXILIAR      = '''|| UN_NOMBREAUXILIAR ||''', 

          MI_CONDICION :='    COMPANIA            = ''' || UN_COMPANIA || ''' 
                          AND APELLIDO1           =''NO APLICA''
                          AND NUMERO_CONTRATO     = ' || MI_PORDENDECOMPRA || '
                          AND TIPO_CONTRATO       = ''' || MI_PCLASEORDEN;


          MI_INDICADORNOMINA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_VALORES,
                                                    UN_VALORES   => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
          MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_PERS,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;

         SELECT 
                NVL(VALORFINAL,0), 
                NVL(HONORARIOS,0) 
           INTO MI_VALORFINAL,
                MI_HONORARIOS
          FROM  
                ORDENDECOMPRA
          WHERE 
                COMPANIA      =UN_COMPANIA
            AND CLASEORDEN    =MI_PCLASEORDEN
            AND NUMERO        =MI_PORDENDECOMPRA;

      IF MI_HONORARIOS <= 0 THEN
        MI_HONORARIOS:=PCK_SYSMAN_UTL.FC_ROUND(MI_VALORFINAL/NVL(MI_PDIASCONTRATO,0)*30,0);
      END IF;
      BEGIN 
        BEGIN 
            MI_VALORES :=  '  COMPANIA              = ' || UN_COMPANIA || ',
                              ID_DE_PROCESO         = ''90'',
                              ANO                   = ' || TO_CHAR(MI_PFECHAINICIAL,'YYYY') || ',
                              MES                   = ' || TO_CHAR(MI_PFECHAINICIAL,'MM') || ',
                              PERIODO               =''03'',
                              MONTO_INICIAL         = ' || MI_VALORFINAL || ',
                              SALDO                 = ' || MI_VALORFINAL || ',
                              VALOR_CUOTA           = ' || MI_PHONORARIOS || ',
                              OBSERVACIONES         = ''' || MI_POBSERVACIONES || ''',
                              VALPAGO               = '|| MI_HONORARIOS ||',
                              PERIODOCOBRO          = ''03'' 
                              MODIFIED_BY    = ''' || UN_USUARIO||''',
					                    DATE_MODIFIED  = SYSDATE';


            MI_CONDICION :='    COMPANIA          = ''' || UN_COMPANIA || ''' 
                            AND ID_DE_PROCESO     = ''90''
                            AND ANO               = EXTRACT(YEAR FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),
                            AND MES               = LPAD(EXTRACT(MONTH FROM TO_DATE(''' || MI_PFECHAINICIAL || ''',''dd/MM/yy'')),2,''0''),
                            AND NUMERO_CONTRATO   = ' || MI_PORDENDECOMPRA || '
                            AND TIPO_CONTRATO     = ''' || MI_PCLASEORDEN || '''
                            AND ID_DE_EMPLEADO    =''' || MI_IDEMPLEADO;


            MI_INDICADORNOMINA1  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PAGOS_CONTRATOS',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_VALORES,
                                                      UN_VALORES   => MI_CONDICION);
            MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
            MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_PAGO_CT,
                                   UN_REEMPLAZOS => MI_MSGERROR);
       END;                                          

      MI_RESULTADO := 7;
      --MI_RESULTADO || ' iniciación de contrato ';
    END IF;      
  END IF;                


  IF MI_PTIPOT='ATM' THEN    
    SELECT 
           COUNT(*) 
      INTO MI_EXISTE
      FROM 
           ORDENDECOMPRA
     WHERE 
           COMPANIA   = UN_COMPANIA
       AND CLASEORDEN = MI_PCLASEORDEN
       AND NUMERO     = MI_PORDENDECOMPRA;

    IF MI_EXISTE > 0 THEN 
      SELECT    
             VALORFINAL
        INTO MI_VALORFINAL
        FROM 
             ORDENDECOMPRA
       WHERE 
             COMPANIA   = UN_COMPANIA
         AND CLASEORDEN = MI_PCLASEORDEN
         AND NUMERO     = MI_PORDENDECOMPRA;

      IF MI_VALORFINAL > 0 THEN
        BEGIN 
          BEGIN 
              MI_VALORES := '  FECHA_FINAL_CT = TO_DATE(''' || TO_CHAR(MI_PFECHAFINAL,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                VALORCONTRATO           = 0,
                                MODIFIED_BY    = ''' || UN_USUARIO||''',
					                      DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := '   COMPANIA                = ''' || UN_COMPANIA || ''' 
                              AND NUMERO_CONTRATO         = ' || MI_PORDENDECOMPRA || '
                              AND TIPO_CONTRATO           = ''' || MI_PCLASEORDEN || '''
                              AND PERSONAL.APELLIDO1      =''NO APLICA''';

              MI_INDICADORNOMINA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_VALORES,
                                                      UN_VALORES   => MI_CONDICION);

              MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
              MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_VALOR_CT,
                                     UN_REEMPLAZOS => MI_MSGERROR);
         END;                                           

      END IF;
    END IF;  
  END IF;

  IF MI_INDICADORNOMINA <> 0 AND MI_INDICADORNOMINA1 <> 0 THEN 

     BEGIN 
        BEGIN 
          MI_VALORES := '     INDICADOR_NOMINA=-1,
                              MODIFIED_BY    = ''' || UN_USUARIO||''',
                              DATE_MODIFIED  = SYSDATE';

          MI_CONDICION := '   COMPANIA                = ''' || UN_COMPANIA || ''' 
                              AND ORDENDECOMPRA         = ' || MI_PORDENDECOMPRA || '
                              AND CLASEORDEN           = ''' || MI_PCLASEORDEN || '''
                              AND NOVEDAD      ='|| UN_NOVEDAD; 

          MI_INDICADORNOMINA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_VALORES,
                                                      UN_VALORES   => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'NUMERO_CONTRATO';
          MI_MSGERROR(1).VALOR := MI_PORDENDECOMPRA;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUA_IND_NOM,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;                                              


  END IF;
  MI_RESULTADO := 8; 
  --MI_RESULTADO || ' ';

ELSE 
  BEGIN 
  --RAISE_APPLICATION_ERROR (-20000,  'Este tipo de contrato no esta configurado con indicador de actualiza novedad a nomina, no es posible enviar la novedad');  
  RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_NOVE_NOMI);
  END;
END IF; 
RETURN MI_RESULTADO;

END FC_ENVIAR_NOVEDADES_A_NOMINA;

FUNCTION FC_FORMAT_FECHA_MINUTAS
/*
    NAME              : FC_FORMAT_FECHA_MINUTAS en acces -> formatfechaminutas()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 10/12/2015
    TIME              : 5:00 PM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Devuelve una fecha en formato en letras con el numero del dÃ¬a dentro de parÃ¨ntesis
    @NAME: getDiasMinuta
  */
(
  UN_FECHA     IN   DATE
)
RETURN VARCHAR2
AS
MI_RESULTADO                PCK_SUBTIPOS.TI_RTA_ACME;
MI_DIA                      PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_MES                      VARCHAR2(50 CHAR);
MI_ANIO                     VARCHAR(50 CHAR);

BEGIN

MI_DIA:=TO_CHAR(UN_FECHA,'DD');
MI_MES:=TO_CHAR(UN_FECHA,'MM');
MI_ANIO:=TO_CHAR(UN_FECHA,'YYYY');

IF MI_ANIO < 2000 OR MI_ANIO IS NULL THEN
    RETURN 'ND';
END IF;

CASE MI_DIA
   WHEN 1 THEN
        MI_RESULTADO := 'un (1) dia';
    WHEN 2 THEN
        MI_RESULTADO := 'dos (2) dias';
    WHEN 3 THEN
        MI_RESULTADO := 'tres (3) dias';
    WHEN 4 THEN
        MI_RESULTADO := 'cuatro (4) dias';
    WHEN 5 THEN
        MI_RESULTADO := 'cinco (5) dias';
    WHEN 6 THEN
        MI_RESULTADO := 'seis (6) dias';
    WHEN 7 THEN
        MI_RESULTADO := 'siete (7) dias';
    WHEN 8 THEN
        MI_RESULTADO := 'ocho (8) dias';
    WHEN 9 THEN
        MI_RESULTADO := 'nueve (9) dias';
    WHEN 10 THEN
        MI_RESULTADO := 'diez (10) dias';
    WHEN 11 THEN
        MI_RESULTADO := 'once (11) dias';
    WHEN 12 THEN
        MI_RESULTADO := 'doce (12) dias';
    WHEN 13 THEN
        MI_RESULTADO := 'trece (13) dias';
    WHEN 14 THEN
        MI_RESULTADO := 'catorce (14) dias';
    WHEN 15 THEN
        MI_RESULTADO := 'quince (15) dias';
    WHEN 16 THEN
        MI_RESULTADO := 'diez y seis (16) dias';
    WHEN 17 THEN
        MI_RESULTADO := 'diez y siete (17) dias';
    WHEN 18 THEN
        MI_RESULTADO := 'diez y ocho (18) dias';
    WHEN 19 THEN
        MI_RESULTADO := 'diez y nueve (19) dias';
    WHEN 20 THEN
        MI_RESULTADO := 'veinte (20) dias';
    WHEN 21 THEN
        MI_RESULTADO := 'veinte y un (21) dias';
    WHEN 22 THEN
        MI_RESULTADO := 'veinte y dos (22) dias';
    WHEN 23 THEN
        MI_RESULTADO := 'veinte y tres (23) dias';
    WHEN 24 THEN
        MI_RESULTADO := 'veinte y cuatro (24) dias';
    WHEN 25 THEN
        MI_RESULTADO := 'veinte y cinco (25) dias';
    WHEN 26 THEN
        MI_RESULTADO := 'veinte y seis (26) dias';
    WHEN 27 THEN
        MI_RESULTADO := 'veinte y siete (27) dias';
    WHEN 28 THEN
        MI_RESULTADO := 'veinte y ocho (28) dias';
    WHEN 29 THEN
        MI_RESULTADO := 'veinte y nueve (29) dias';
    WHEN 30 THEN
        MI_RESULTADO := 'treinta (30) dias';
    WHEN 31 THEN
        MI_RESULTADO := 'treinta y un (31) dias ';
END CASE;

RETURN MI_RESULTADO ||' del mes de '|| PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_MES) || ' de ' || LOWER(SUBSTR(PCK_SYSMAN_UTL.FC_VALOR_LETRAS(MI_ANIO),0,LENGTH(PCK_SYSMAN_UTL.FC_VALOR_LETRAS(MI_ANIO))-11));

END FC_FORMAT_FECHA_MINUTAS;

FUNCTION FC_POLIZAS
/*
    NAME              : FC_POLIZAS en acces -> Polizas()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 11/12/2015
    TIME              : 12:00 AM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getPoliza
  */
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN       IN VARCHAR2,
  UN_ORDENDECOMPRA    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TIPO             IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS
MI_RESULTADO                PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN


FOR RS IN( SELECT 
                  TIPOPOLIZA.DESCRIPCION,
                  POLIZAS.CODIGO, 
                  POLIZAS.aseguradora, 
                  POLIZAS.VIGENCIADESDE AS inicio, 
                  POLIZAS.VIGENCIAHASTA AS fin, 
                  ASEGURADORA.NOMBRE
             FROM 
                  POLIZAS
             LEFT JOIN ASEGURADORA 
               ON ASEGURADORA.NitAseguradora = POLIZAS.aseguradora
             LEFT JOIN TIPOPOLIZA 
               ON POLIZAS.TIPO = TIPOPOLIZA.CODIGO   
            WHERE 
                  POLIZAS.COMPANIA         = UN_COMPANIA
              AND POLIZAS.CLASEORDEN       = UN_CLASEORDEN
              AND POLIZAS.ORDENDECOMPRA    = UN_ORDENDECOMPRA
       ) LOOP

IF UN_TIPO=1 THEN
    MI_RESULTADO:=MI_RESULTADO ||''|| NVL(RS.CODIGO,'')||'. ';
ELSIF UN_TIPO =2 THEN    
    MI_RESULTADO:=MI_RESULTADO ||''|| NVL(RS.NOMBRE,'')||'. ';
ELSE
    MI_RESULTADO:=MI_RESULTADO ||''|| NVL(RS.CODIGO,'') ||'. '|| NVL(RS.NOMBRE,'')||'. ';
END IF;

END LOOP;

RETURN MI_RESULTADO;
END FC_POLIZAS;


FUNCTION FC_POLIZASRESOLUCIONAPRO 
/*
    NAME              : FC_POLIZAS en acces -> PolizasResolucionApro()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 15/12/2015
    TIME              : 9:00 AM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getPolizaResolucion
  */
(
  UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN     IN  VARCHAR2,
  UN_ORDENDECOMPRA  IN  PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS
MI_POLIZAS          PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_POLIZA           VARCHAR(3200 CHAR);
MI_NUMPOLIZA        VARCHAR(3200 CHAR);
BEGIN
MI_POLIZAS   :=0;
MI_POLIZA    :='';
MI_NUMPOLIZA :='';

FOR RS IN( SELECT  
                  TIPOPOLIZA.DESCRIPCION,
                  POLIZAS.CONSECUTIVO, 
                  POLIZAS.CODIGO, 
                  POLIZAS.ASEGURADORA, 
                  POLIZAS.VALORASEGURADO,
                  POLIZAS.VIGENCIADESDE AS INICIO, 
                  POLIZAS.VIGENCIAHASTA AS FIN
             FROM 
                  POLIZAS 
             INNER JOIN TIPOPOLIZA 
               ON POLIZAS.TIPO = TIPOPOLIZA.CODIGO   
           WHERE  
                  POLIZAS.COMPANIA         = UN_COMPANIA
             AND  POLIZAS.CLASEORDEN       = UN_CLASEORDEN
             AND  POLIZAS.ORDENDECOMPRA    = UN_ORDENDECOMPRA
       ) LOOP 

        IF RS.CODIGO <> MI_NUMPOLIZA THEN
            IF MI_POLIZAS > 0 THEN

                MI_POLIZA:=MI_POLIZA ||' y ';

            END IF;

            MI_POLIZA:=MI_POLIZA||' '|| NVL(RS.CODIGO,'') ||' de la compania de seguros '||NVL(RS.ASEGURADORA,'')||' que ampara: ';

            MI_POLIZAS:=MI_POLIZAS+1;
        ELSE

            MI_POLIZA:=MI_POLIZA ||', ';    

        END IF;

        MI_POLIZA := MI_POLIZA ||' '|| NVL(RS.DESCRIPCION,'') || ' con una vigencia comprendida entre el ' || NVL(RS.INICIO, '') || ' y el ' || NVL(RS.FIN, '') || ' con un valor asegurado de ' || NVL(RS.VALORASEGURADO, '');

        MI_NUMPOLIZA := RS.CODIGO;
END LOOP;
MI_POLIZA:=SUBSTR(MI_POLIZA,1,LENGTH(MI_POLIZA)-3);

IF MI_POLIZAS > 1 THEN
    MI_POLIZA:='la(s) poliza(s) Numero(s) '|| MI_POLIZA;
ELSE 
    MI_POLIZA:='la poliza Numero '|| MI_POLIZA;    
END IF;

RETURN MI_POLIZA; 
END FC_POLIZASRESOLUCIONAPRO;

FUNCTION FC_POLIZASRESOLUCIONAPRO2
/*
    NAME              : FC_POLIZAS en acces -> PolizasResolucionApro2()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 15/12/2015
    TIME              : 12:00 AM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getPolizaResolucion2
  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN     IN VARCHAR2,
  UN_ORDENDECOMPRA  IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS
MI_POLIZAS          PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_POLIZA           VARCHAR(3200 CHAR);
MI_NUMPOLIZA        VARCHAR(3200 CHAR);

BEGIN
MI_POLIZA:=0;
MI_POLIZA    :='';
MI_NUMPOLIZA :='';

FOR RS IN(SELECT 
                 TIPOPOLIZA.DESCRIPCION,
                 POLIZAS.CODIGO, 
                 POLIZAS.ASEGURADORA, 
                 POLIZAS.VIGENCIADESDE AS INICIO, 
                 POLIZAS.VIGENCIAHASTA AS FIN
            FROM 
                 POLIZAS
            INNER JOIN TIPOPOLIZA 
              ON POLIZAS.TIPO = TIPOPOLIZA.CODIGO   
           WHERE 
                 POLIZAS.COMPANIA         = UN_COMPANIA
             AND POLIZAS.CLASEORDEN       = UN_CLASEORDEN
             AND POLIZAS.ORDENDECOMPRA    = UN_ORDENDECOMPRA
       ) LOOP 

      MI_POLIZA:=MI_POLIZA ||' '||  NVL(RS.CODIGO,'') ||' de la compania de seguros '|| NVL(RS.ASEGURADORA,'');
      MI_POLIZAS:=MI_POLIZAS+1;
        END LOOP;


IF MI_POLIZAS > 1 THEN
    MI_POLIZA:='las polizas Nos'|| MI_POLIZA;
ELSE 
    MI_POLIZA:='la poliza No'|| MI_POLIZA;    
END IF;

RETURN MI_POLIZA; 
END FC_POLIZASRESOLUCIONAPRO2;

FUNCTION FC_DETALLECARGOSUPERVISORES
/*
    NAME              : FC_DETALLECARGOSUPERVISORES en acces -> DetalleCargoSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 16:00 
    SOURCE MODULE     : sysmanTT2015.10.02
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/08/2017
    TIME              : 02:30 PM 
    DESCRIPTION       : 
    @NAME:            getDetalleSupervisorCargo
    @METHOD:          GET
  */
(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN      IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_ORDENDECOMPRA   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2
AS 
    MI_CADENA               PCK_SUBTIPOS.TI_STRSQL;
    MI_SUPERVISOR           PCK_SUBTIPOS.TI_STRSQL;
    MI_CARGO                TERCERO.CARGO%TYPE; 
    MI_RS                   SYS_REFCURSOR;
    MI_CONTROLASUPERVISORES PARAMETRO.VALOR%TYPE;
BEGIN
    MI_CONTROLASUPERVISORES:=PCK_SYSMAN_UTL.FC_PAR(
    	                                    UN_COMPANIA  => UN_COMPANIA
    	                                   ,UN_NOMBRE    => 'CONTROLA SUPERVISORES POR VIGENCIA'
    	                                   ,UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS()
    	                                   ,UN_FECHA_PAR => SYSDATE);

    MI_CONTROLASUPERVISORES:=NVL(MI_CONTROLASUPERVISORES,'NO');
    IF MI_CONTROLASUPERVISORES='SI' THEN

        MI_CADENA:='SELECT TERCERO.CARGO
                      FROM SUPERVISORES
                          INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL    
                    WHERE SUPERVISORES.COMPANIA       = '''||UN_COMPANIA||'''
                      AND SUPERVISORES.CLASEORDEN     = '''||UN_CLASEORDEN||'''
                      AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                      AND SUPERVISORES.VIGENCIA       NOT IN (0) 
                    ORDER BY  TERCERO.NOMBRE
                             ,TERCERO.CARGO
                             ,TERCERO.PROFESION';
    ELSE
        MI_CADENA:='SELECT TERCERO.CARGO
                      FROM SUPERVISORES
                          INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL    
                     WHERE SUPERVISORES.COMPANIA       = '''||UN_COMPANIA||'''
                       AND SUPERVISORES.CLASEORDEN     = '''||UN_CLASEORDEN||'''
                       AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                     ORDER BY  TERCERO.NOMBRE
                              ,TERCERO.CARGO
                              ,TERCERO.PROFESION';
    END IF;

    OPEN MI_RS FOR MI_CADENA;
    LOOP
        FETCH MI_RS INTO MI_CARGO;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_SUPERVISOR:=MI_SUPERVISOR || ' ' || MI_CARGO || ' y '; 
        END LOOP;
    CLOSE MI_RS; 
    MI_SUPERVISOR:=SUBSTR(MI_SUPERVISOR,0,LENGTH(MI_SUPERVISOR)-3); 
    RETURN MI_SUPERVISOR; 
END FC_DETALLECARGOSUPERVISORES; 

FUNCTION FC_DETALLENOMBRESUPERVISORES
/*
    NAME              : FC_DETALLECEDSUPERVISORES en acces -> DetalleCedSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 17:00 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getDetalleSupervisorNombre
  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN      IN VARCHAR2,
  UN_ORDENDECOMPRA   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS
    MI_CADENA            VARCHAR(1000 CHAR);
    MI_SUPERVISOR        VARCHAR(3200 CHAR);
    MI_NOMBRE            VARCHAR(200 CHAR); 
    MI_RS                SYS_REFCURSOR;
    MI_CONTROLASUPERVISORES VARCHAR2(10 CHAR);
BEGIN
    MI_CADENA     :='';
    MI_SUPERVISOR :='';
    MI_NOMBRE     :=''; 

    MI_CONTROLASUPERVISORES:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'CONTROLA SUPERVISORES POR VIGENCIA',
                                                   UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(),
                                                   UN_FECHA_PAR => SYSDATE);

    MI_CONTROLASUPERVISORES:=NVL(MI_CONTROLASUPERVISORES,'NO');

    IF MI_CONTROLASUPERVISORES='SI' THEN

      MI_CADENA:='SELECT TERCERO.PROFESION
                      FROM SUPERVISORES
                          INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL 
                  WHERE SUPERVISORES.COMPANIA       = '''|| UN_COMPANIA ||''' 
                    AND SUPERVISORES.CLASEORDEN     = '''|| UN_CLASEORDEN ||'''
                    AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                    AND SUPERVISORES.VIGENCIA       = -1
                  ORDER BY
                        TERCERO.NOMBRE,
                        TERCERO.CARGO,
                        TERCERO.PROFESION';
    ELSE

      MI_CADENA:='SELECT TERCERO.PROFESION
                      FROM SUPERVISORES
                          INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL 
                   WHERE SUPERVISORES.COMPANIA       = '''|| UN_COMPANIA ||''' 
                     AND SUPERVISORES.CLASEORDEN     = '''|| UN_CLASEORDEN ||'''
                     AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                   ORDER BY
                         TERCERO.NOMBRE,
                         TERCERO.CARGO,
                         TERCERO.PROFESION';
    END IF;

    OPEN MI_RS FOR MI_CADENA;
      LOOP

        FETCH MI_RS INTO MI_NOMBRE;

          EXIT WHEN MI_RS%NOTFOUND;

          MI_SUPERVISOR:=MI_SUPERVISOR || ' ' || MI_NOMBRE || ' y ';

        END LOOP;
    CLOSE MI_RS; 

    MI_SUPERVISOR:=SUBSTR(MI_SUPERVISOR,0,LENGTH(MI_SUPERVISOR)-3); 
    RETURN MI_SUPERVISOR; 

END FC_DETALLENOMBRESUPERVISORES; 

--10
FUNCTION FC_DETALLECEDSUPERVISORES
/*
    NAME              : FC_DETALLECEDSUPERVISORES en acces -> DetalleCedSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 16:00 
    SOURCE MODULE     : sysmanTT2015.10.02
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/08/2017
    TIME              : 02:30 PM 
    DESCRIPTION       : 
    @NAME:            getDetalleSupervisorCedula
    @METHOD:          GET
  */
(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN      IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_ORDENDECOMPRA   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS 
    MI_CADENA               PCK_SUBTIPOS.TI_STRSQL;
    MI_SUPERVISOR           PCK_SUBTIPOS.TI_STRSQL;
    MI_CEDULA               SUPERVISORES.CEDULA%TYPE; 
    MI_RS                   SYS_REFCURSOR;
    MI_CONTROLASUPERVISORES PARAMETRO.VALOR%TYPE;
BEGIN
    MI_CONTROLASUPERVISORES:=PCK_SYSMAN_UTL.FC_PAR(
    	                                    UN_COMPANIA  => UN_COMPANIA
    	                                   ,UN_NOMBRE    => 'CONTROLA SUPERVISORES POR VIGENCIA'
    	                                   ,UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS()
    	                                   ,UN_FECHA_PAR => SYSDATE);

    MI_CONTROLASUPERVISORES:=NVL(MI_CONTROLASUPERVISORES,'NO');
    IF MI_CONTROLASUPERVISORES='SI' THEN

        MI_CADENA:='SELECT SUPERVISORES.CEDULA
                      FROM SUPERVISORES
                     WHERE SUPERVISORES.COMPANIA       = '''||UN_COMPANIA||'''
                       AND SUPERVISORES.CLASEORDEN     = '''||UN_CLASEORDEN||'''
                       AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                       AND SUPERVISORES.VIGENCIA       NOT IN (0) 
                     ORDER BY SUPERVISORES.CEDULA';
    ELSE
        MI_CADENA:='SELECT SUPERVISORES.CEDULA
                      FROM SUPERVISORES
                     WHERE SUPERVISORES.COMPANIA       = '''||UN_COMPANIA||'''
                       AND SUPERVISORES.CLASEORDEN     = '''||UN_CLASEORDEN||'''
                       AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                     ORDER BY SUPERVISORES.CEDULA';
    END IF;

    OPEN MI_RS FOR MI_CADENA;
    LOOP
        FETCH MI_RS INTO MI_CEDULA;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_SUPERVISOR:=MI_SUPERVISOR || ' ' || MI_CEDULA || ' y '; 
        END LOOP;
    CLOSE MI_RS; 
    MI_SUPERVISOR:=SUBSTR(MI_SUPERVISOR,0,LENGTH(MI_SUPERVISOR)-3); 
    RETURN MI_SUPERVISOR; 
END FC_DETALLECEDSUPERVISORES;   

FUNCTION FC_CONSEC_ADICIONES
/*
    NAME              : FC_CONSEC_ADICIONES en acces -> ConsecAdiciones()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 18/01/2015
    TIME              : 08:22 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getConsecutivoOrdenCompra
  */
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_TIPOAFECTADO     IN VARCHAR2,
  UN_NUMEROAFECTADO   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN NUMBER 
AS
MI_MAXCONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN


SELECT
       NVL(MAX(ORDENDECOMPRA.CONSECUTIVOADICIONES),0) 
  INTO MI_MAXCONSECUTIVO
  FROM 
       ORDENDECOMPRA 
 WHERE 
       COMPANIA =UN_COMPANIA
   AND TIPOAFECTADO = UN_TIPOAFECTADO
   AND NUMEROAFECTADO= UN_NUMEROAFECTADO;   

RETURN MI_MAXCONSECUTIVO+1;


END FC_CONSEC_ADICIONES;

FUNCTION FC_POLIZASRESOLUCIONINDIV
/*
    NAME              : FC_CONSEC_ADICIONES en acces -> ConsecAdiciones()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 18/01/2015
    TIME              : 08:22 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getResolucionIndividual
  */
(
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_CLASEORDEN    IN VARCHAR2, 
  UN_ORDENDECOMPRA IN PCK_SUBTIPOS.TI_ENTERO_LARGO) 
RETURN VARCHAR2
AS
 MI_RESOPOLIINDIV VARCHAR2(3200 CHAR);
 MI_CONTADOR      PCK_SUBTIPOS.TI_ENTERO_LARGO;
 BEGIN   
 MI_CONTADOR:=0;
  FOR RS IN (SELECT 
                    TIPOPOLIZA.DESCRIPCION,
                    POLIZAS.INDIMPRESION,
                    POLIZAS.CODIGO,
                    POLIZAS.ASEGURADORA,
                    POLIZAS.VIGENCIADESDE AS INICIO,
                    POLIZAS.VIGENCIAHASTA AS FIN
               FROM 
                    POLIZAS 
               LEFT JOIN TIPOPOLIZA 
                 ON POLIZAS.TIPO = TIPOPOLIZA.CODIGO 
              WHERE 
                    COMPANIA       = UN_COMPANIA 
                AND CLASEORDEN     = UN_CLASEORDEN 
                AND ORDENDECOMPRA  = UN_ORDENDECOMPRA)
  LOOP
    MI_CONTADOR := MI_CONTADOR+1;
    MI_RESOPOLIINDIV := MI_RESOPOLIINDIV||NVL(RS.CODIGO, '')||' de la compañia de seguros '||NVL(RS.ASEGURADORA, '');
  END LOOP;
  IF MI_CONTADOR > 1 THEN
       MI_RESOPOLIINDIV := 'las polizas Nos'|| MI_RESOPOLIINDIV;
  ELSE
       MI_RESOPOLIINDIV := 'la poliza No'||MI_RESOPOLIINDIV;
  END IF;

   RETURN MI_RESOPOLIINDIV;
END FC_POLIZASRESOLUCIONINDIV;

FUNCTION FC_DETALLECONTSUPERVISORES
/*
    NAME              : FC_DETALLECEDSUPERVISORES en acces -> DetalleCedSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 17:00 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getDetalleSupervisorContrato
  */
(
  UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN     IN  VARCHAR2,
  UN_ORDENDECOMPRA  IN  PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS

MI_CADENA            VARCHAR(1000 CHAR);
MI_SUPERVISOR        VARCHAR(3200 CHAR);
MI_NUMEROCONTRATO    VARCHAR(200 CHAR); 
MI_RS                SYS_REFCURSOR;
MI_CONTROLASUPERVISORES VARCHAR2(10 CHAR);
BEGIN
MI_CADENA            :='';
MI_SUPERVISOR        :='';
MI_NUMEROCONTRATO    :=''; 

MI_CONTROLASUPERVISORES:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                               UN_NOMBRE    => 'CONTROLA SUPERVISORES POR VIGENCIA',
                                               UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(),
                                               UN_FECHA_PAR => SYSDATE);

MI_CONTROLASUPERVISORES:=NVL(MI_CONTROLASUPERVISORES,'NO');

IF MI_CONTROLASUPERVISORES='SI' THEN

  MI_CADENA:='SELECT 
                     SUPERVISORES.NUMEROCONTRATO
                FROM 
                     SUPERVISORES
                INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL 
               WHERE 
                     SUPERVISORES.COMPANIA       ='''|| UN_COMPANIA ||''' 
                 AND SUPERVISORES.CLASEORDEN     ='''|| UN_CLASEORDEN ||'''
                 AND SUPERVISORES.NUMEROCONTRATO ='||UN_ORDENDECOMPRA||'
                 AND SUPERVISORES.VIGENCIA = -1
                 ORDER BY
                         TERCERO.NOMBRE,
                         TERCERO.CARGO,
                         TERCERO.PROFESION' ;
ELSE

  MI_CADENA:='SELECT 
                     SUPERVISORES.NUMEROCONTRATO
                FROM 
                     SUPERVISORES
                 INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL      
               WHERE 
                     SUPERVISORES.COMPANIA       = '''|| UN_COMPANIA ||''' 
                 AND SUPERVISORES.CLASEORDEN     = '''|| UN_CLASEORDEN ||'''
                 AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                  ORDER BY
                         TERCERO.NOMBRE,
                         TERCERO.CARGO,
                         TERCERO.PROFESION';
END IF;

OPEN MI_RS FOR MI_CADENA;
  LOOP
    FETCH MI_RS INTO MI_NUMEROCONTRATO;
      EXIT WHEN MI_RS%NOTFOUND;
       MI_SUPERVISOR:=MI_SUPERVISOR || ' ' || MI_NUMEROCONTRATO || ' y ';
   END LOOP;
CLOSE MI_RS; 
MI_SUPERVISOR:=SUBSTR(MI_SUPERVISOR,0,LENGTH(MI_SUPERVISOR)-3); 
RETURN MI_SUPERVISOR; 
END FC_DETALLECONTSUPERVISORES; 


FUNCTION FC_DETALLEPROFSUPERVISORES
/*
    NAME              : FC_DETALLECEDSUPERVISORES en acces -> DetalleCedSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 17:00 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getDetalleSupervisorProfesion
  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_CLASEORDEN      IN VARCHAR2,
  UN_ORDENDECOMPRA   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 
AS

MI_CADENA            VARCHAR(1000 CHAR);
MI_SUPERVISOR        VARCHAR(3200 CHAR);
MI_PROFESION         VARCHAR(200 CHAR); 
MI_RS                SYS_REFCURSOR;
MI_CONTROLASUPERVISORES VARCHAR2(10 CHAR);
BEGIN
MI_CADENA     :='';
MI_SUPERVISOR :='';
MI_PROFESION  :=''; 

MI_CONTROLASUPERVISORES:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                               UN_NOMBRE    => 'CONTROLA SUPERVISORES POR VIGENCIA',
                                               UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(),
                                               UN_FECHA_PAR => SYSDATE);

MI_CONTROLASUPERVISORES:=NVL(MI_CONTROLASUPERVISORES,'NO');

IF MI_CONTROLASUPERVISORES='SI' THEN

  MI_CADENA:='SELECT 
                     TERCERO.PROFESION
                FROM 
                     SUPERVISORES
                INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL      
               WHERE 
                     SUPERVISORES.COMPANIA       = '''|| UN_COMPANIA ||''' 
                 AND SUPERVISORES.CLASEORDEN     = '''|| UN_CLASEORDEN ||'''
                 AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
                 AND SUPERVISORES.VIGENCIA       = -1
               ORDER BY
                         TERCERO.NOMBRE,
                         TERCERO.CARGO,
                         TERCERO.PROFESION';
ELSE
  MI_CADENA:='SELECT 
                     TERCERO.PROFESION
                FROM
                     SUPERVISORES
                INNER JOIN TERCERO
                              ON  SUPERVISORES.COMPANIA = TERCERO.COMPANIA
                              AND SUPERVISORES.CEDULA   = TERCERO.NIT
                              AND SUPERVISORES.SUCURSAL = TERCERO.SUCURSAL           
               WHERE 
                     SUPERVISORES.COMPANIA       = '''|| UN_COMPANIA ||''' 
                 AND SUPERVISORES.CLASEORDEN     = '''|| UN_CLASEORDEN ||'''
                 AND SUPERVISORES.NUMEROCONTRATO = '||UN_ORDENDECOMPRA||'
               ORDER BY
                         TERCERO.NOMBRE,
                         TERCERO.CARGO,
                         TERCERO.PROFESION';
END IF;

OPEN MI_RS FOR MI_CADENA;

  LOOP

    FETCH MI_RS INTO MI_PROFESION;

      EXIT WHEN MI_RS%NOTFOUND;

       MI_SUPERVISOR:=MI_SUPERVISOR || ' ' || MI_PROFESION || ' y ';

   END LOOP;

CLOSE MI_RS; 

MI_SUPERVISOR:=SUBSTR(MI_SUPERVISOR,0,LENGTH(MI_SUPERVISOR)-3); 

RETURN MI_SUPERVISOR; 

END FC_DETALLEPROFSUPERVISORES; 

FUNCTION FC_DETALLERESULTADOS(
/*
    NAME              : FC_DETALLECEDSUPERVISORES en acces -> DetalleCedSupervisores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ
    DATE MIGRADOR     : 29/12/2015
    TIME              : 17:00 
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME: getDetalleResultado
  */
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCONTRATO IN VARCHAR2, 
  UN_NUMERO       IN PCK_SUBTIPOS.TI_ENTERO_LARGO

)RETURN VARCHAR2
 AS
  MI_RESULTADOS  VARCHAR2(3200 CHAR);
BEGIN    
MI_RESULTADOS  :='';
 FOR RS IN(SELECT 
                  NOVEDADCONTRATO.RESULTADOS
             FROM 
                  NOVEDADCONTRATO 
            WHERE 
                  NOVEDADCONTRATO.COMPANIA      = UN_COMPANIA 
              AND NOVEDADCONTRATO.CLASEORDEN    = UN_TIPOCONTRATO 
              AND NOVEDADCONTRATO.ORDENDECOMPRA = UN_NUMERO
            ORDER BY 
                  NOVEDADCONTRATO.COMPANIA,
                  NOVEDADCONTRATO.CLASEORDEN,
                  NOVEDADCONTRATO.ORDENDECOMPRA)
    LOOP
       MI_RESULTADOS := RS.RESULTADOS;
    END LOOP;
  RETURN MI_RESULTADOS;
END FC_DETALLERESULTADOS;

 FUNCTION FC_CONSEC_CONTRATOS
   /*
      NAME              : FC_CONSEC_CONTRATOS en access -> ConsecContrato()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
      DATE MIGRADOR     : 19/01/2016
      TIME              : 9:43 
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : AURA LILIANA MONROY GARCÍA
      DATE MODIFIED     : 17/01/2017
      TIME MODIFIED     : 05:30 PM
      MODIFICATIONS     : Se reestructura el proceso para la generación del número consecutivo del contrato, cambiando la estructura original definida en Access. 
                          Se elimina el parámetro UN_NUMERACIONUNICA de la versión anterior, debido a que este es un valor que puede ser calculado      
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_CLASECONTRATO  => Indica la clase de contrato de la orden de compra que se desea generar el consecutivo
                          UN_ANIOVIGENCIA   => Año en que se trabaja ese contrato

    @NAME:   ConsecContrato
    @METHOD: GET     
    */
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,  
    UN_CLASECONTRATO     IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_ANIOVIGENCIA      IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER 
  AS

    MI_NUMEROINICIAL      PCK_SUBTIPOS.TI_ENTERO_LARGO ;
    MI_NUMERO             PCK_SUBTIPOS.TI_ENTERO_LARGO ;
    MI_CONSECCONTRATO     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_NUMERACIONUNICA    PCK_SUBTIPOS.TI_LOGICO;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    -- Consulta si la clase de contrato posee configuración de numeración única
     MI_NUMEROINICIAL     := -1;
    MI_NUMERO             := -1;
    BEGIN
      BEGIN          

        SELECT 
               TIPOORDENDECOMPRA.NUMERACIONUNICA 
          INTO MI_NUMERACIONUNICA
          FROM 
               TIPOORDENDECOMPRA
         WHERE 
               TIPOORDENDECOMPRA.COMPANIA        = UN_COMPANIA
           AND TIPOORDENDECOMPRA.CODIGO          = UN_CLASECONTRATO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;        
      END; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
      MI_MSGERROR(1).CLAVE := 'TIPOORDEN';
      MI_MSGERROR(1).VALOR :=  UN_CLASECONTRATO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_NUMERACIONUNICA,
                        UN_TABLAERROR => 'TIPOORDENDECOMPRA',
                        UN_REEMPLAZOS => MI_MSGERROR
                      );    
    END;

    -- Calcula el valor del consecutivo para los contratos con numeración única 
    IF MI_NUMERACIONUNICA NOT IN (0) THEN 
      --  Número Inicial definido para esa clase de contrato
      BEGIN          
        SELECT 
               TIPOORDENDECOMPRA.NUMEROINICIAL 
          INTO MI_NUMEROINICIAL
          FROM 
               TIPOORDENDECOMPRA
         WHERE 
               TIPOORDENDECOMPRA.COMPANIA = UN_COMPANIA
           AND TIPOORDENDECOMPRA.CODIGO   = UN_CLASECONTRATO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_NUMEROINICIAL:=0;
      END;
      -- Retorna Máximo consecutivo que ha sido asignado en esa clase de contrato
      BEGIN
        SELECT 
               MAX(ORDENDECOMPRA.NUMERO) 
          INTO MI_NUMERO
          FROM 
               ORDENDECOMPRA 
         WHERE 
               ORDENDECOMPRA.COMPANIA                 = UN_COMPANIA
           AND ORDENDECOMPRA.CLASEORDEN               = UN_CLASECONTRATO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_NUMERO := NULL;
      END; 

      -- Asigna el consecutivo
      IF MI_NUMERO IS NULL OR MI_NUMERO < MI_NUMEROINICIAL THEN

        MI_CONSECCONTRATO := MI_NUMEROINICIAL;

      ELSIF MI_NUMERO >= MI_NUMEROINICIAL THEN 

        MI_CONSECCONTRATO := MI_NUMERO + 1;

      END IF;   

    -- Calcula el valor del consecutivo para los contratos sin numeración única 
    ELSE
      -- Calcula Inicial 
      MI_NUMEROINICIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'CONSECUTIVO INICIAL CONTRATOS SIN NUMERACION UNICA', 
                                                UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS(),
                                                UN_FECHA_PAR => SYSDATE);

    -- Máximo asignado a los contratos sin numeración
      BEGIN
        SELECT 
               MAX(ORDENDECOMPRA.NUMERO) 
          INTO MI_NUMERO
          FROM ORDENDECOMPRA 
         INNER JOIN TIPOORDENDECOMPRA 
            ON ORDENDECOMPRA.COMPANIA           = TIPOORDENDECOMPRA.COMPANIA
           AND ORDENDECOMPRA.CLASEORDEN         = TIPOORDENDECOMPRA.CODIGO
         WHERE 
               ORDENDECOMPRA.COMPANIA                   = UN_COMPANIA 
           AND TIPOORDENDECOMPRA.NUMERACIONUNICA        IN (0);

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_NUMERO := 0;
      END; 

      -- Asigna el consecutivo
      BEGIN
        BEGIN
          IF MI_NUMEROINICIAL IS NULL THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;   
          ELSIF MI_NUMERO IS NULL OR MI_NUMERO < MI_NUMEROINICIAL THEN
              MI_CONSECCONTRATO := MI_NUMEROINICIAL;    
          ELSIF MI_NUMERO >= MI_NUMEROINICIAL THEN 
              MI_CONSECCONTRATO := MI_NUMERO + 1;
          END IF;          
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    =>SQLCODE,
                UN_ERROR_COD  =>PCK_ERRORES.ERRR_CONTRATOS_PARNUMERACION
                );   
      END;

      BEGIN
        BEGIN    
          -- Actualiza el valor del consecutivo para cuentas sin numeración única
          MI_TABLA      := 'PARAMETRO';
          MI_CAMPOS     := 'VALOR = ''' || MI_CONSECCONTRATO ||''',
                            MODIFIED_BY    = ''' || UN_USUARIO||''',
					                  DATE_MODIFIED  = SYSDATE';
          MI_CONDICION  := '    COMPANIA = ''' ||UN_COMPANIA|| ''' 
                            AND NOMBRE   = ''CONSECUTIVO ACTUAL CONTRATOS SIN NUMERACION UNICA''';

          MI_RTA        := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                              UN_ACCION    => 'M', 
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    =>SQLCODE,
            UN_ERROR_COD  =>PCK_ERRORES.ERRR_CONTRATOS_ACTPAR,
            UN_TABLAERROR =>'PARAMETRO'
          );
      END;

    END IF;  
   RETURN MI_CONSECCONTRATO;     
  END FC_CONSEC_CONTRATOS; 

  --17
  PROCEDURE PR_IMPORTARPRECONTRACTUAL
  /*
    NAME              : PR_IMPORTARPRECONTRACTUAL 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRATION    : 03/04/2017
    TIME              : 04:06 PM
    SOURCE MODULE     : CONTROL CONTRATOS (9); sysmanTT2015.07.02 
    DESCRIPTION       :     
    MODIFIED BY       : (06/10/2017) PABLO ANDRES ESPITIA CUCA
    TIME MODIFIED     : 
    MODIFICATIONS     : Se reestructura el proceso para importar la informacion de precontractual a contratos. 
                        (06/10/2017) Indentar PLSQL. Manejo de excepciones.

    PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                        UN_NUMEROORDEN    => El numero del contrato al que se va a copiar la informacion.
                        UN_CLASEORDEN     => Indica la clase de contrato de la orden de compra
                        UN_ESTUDIOPREVIO  => numero de estudio previo al que se va a copiar la informacion.
                        UN_USUARIO        => Usuario que ingreso a la aplicación
                        UN_USUARIO        => Usuario que ingreso a la aplicación


    @NAME:   importarPrecontractual 
  */
  (
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_NUMEROORDEN   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
     UN_CLASEORDEN    IN ORDENDECOMPRA.CLASEORDEN%TYPE,
     UN_ESTUDIOPREVIO IN ES_ESTPREVIO.COD_ESTUDIO%TYPE,
     UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
     UN_NUMPROCESO    IN ORDENDECOMPRA.NOPROCESO%TYPE   
  )
  AS
    MI_COD_ESTUDIO          ES_ESTPREVIO.COD_ESTUDIO%TYPE;
    MI_NOMBREESTUDIOT       ES_ESTPREVIO.NOMBRE_ESTUDIO%TYPE;
    MI_TABLA_O              PCK_SUBTIPOS.TI_TABLA DEFAULT 'ORDENDECOMPRA';
    MI_TABLA_OD             PCK_SUBTIPOS.TI_TABLA DEFAULT 'ORDENDECOMPRAPPTO';
    MI_TABLA_D              PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_ORDENDECOMPRA';
    MI_TABLA_S              PCK_SUBTIPOS.TI_TABLA DEFAULT 'SUPERVISORES';
    MI_CAMPOS               PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_CODIGO               TIPOADJUDICACION.CODIGO%TYPE;
    MI_OBJETOCON            ES_ESTPREVIO.OBJETO_CONTRATO%TYPE;
    MI_DESCRIPCION          ES_ESTPREVIO.ESPECIFICACION_TEC%TYPE;
    MI_CONSIDERANDOS        ES_ESTPREVIO.DEF_NECESIDAD%TYPE;
    MI_OBL_CONTRATISTA      ES_ESTPREVIO.OBL_CONTRATISTA%TYPE;
    MI_OBL_ENTIDAD          ES_ESTPREVIO.OBL_ENTIDAD%TYPE;
    MI_FORMA_PAGO           ES_ESTPREVIO.FORMA_PAGOS%TYPE;
    MI_VALORTOTAL           ES_ESTPREVIO.COSTOTOTALEST%TYPE;
    MI_VALORFINAL           ES_ESTPREVIO.COSTOTOTALEST%TYPE;
    MI_SUPERVISION          ES_ESTPREVIO.SUPERVISION%TYPE;
    MI_NUM_PROCESO          ES_ESTPREVIO.NUM_PROCESO%TYPE;
    MI_PLAZO_EJECUCION      ES_ESTPREVIO.PLAZO_EJECUCION%TYPE;
    MI_CARGO_INTERVENTOR    ES_ESTPREVIO.CARGO_INTERVENTOR%TYPE;
    MI_SUCURSAL_SUPERVISION ES_ESTPREVIO.SUCURSAL_SUPERVISION%TYPE;
    MI_NOMINTERVENTOR       TERCERO.NOMBRE%TYPE;
    MI_PYE_CONTRATISTA      ES_ESTPREVIO.PYE_CONTRATISTA%TYPE;
    MI_FUND_JURIDICOS       ES_ESTPREVIO.FUND_JURIDICOS%TYPE;
    MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
    MI_LOCALIZACION         ES_LOCALIZA.UBICACION%TYPE;
    MI_REEMPLAZOS           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_IMPGARANTIAS         ORDENDECOMPRA.GARANTIA%TYPE;
    MI_TIPOADJUDICACION     ORDENDECOMPRA.TIPOADJUDICACION%TYPE;
    MI_CONSIDERANDOS_CLOB   ES_ESTPREVIO.DEF_NECESIDAD%TYPE;
    MI_DESCRIPCION_CLOB     ES_ESTPREVIO.ESPECIFICACION_TEC%TYPE;
  BEGIN
    BEGIN
      SELECT TIPOADJUDICACION
      INTO MI_TIPOADJUDICACION
      FROM ORDENDECOMPRA
      WHERE COMPANIA   = UN_COMPANIA
        AND CLASEORDEN = UN_CLASEORDEN
        AND NUMERO     = UN_NUMEROORDEN;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TIPOADJUDICACION := NULL;
    END;

    IF MI_TIPOADJUDICACION IS NOT NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_CC_NDF_PRIP_TIPOADJUDACION);
      END;
    END IF;

    BEGIN
      BEGIN 
        SELECT  
          ES_ESTPREVIO.COD_ESTUDIO
         ,SUBSTR(ES_ESTPREVIO.NOMBRE_ESTUDIO,1,255)  NOMBREESTUDIOT
         ,TIPOADJUDICACION.CODIGO
         ,SUBSTR(ES_ESTPREVIO.ESPECIFICACION_TEC,1,4000) DESCRIPCION
         ,SUBSTR(ES_ESTPREVIO.OBJETO_CONTRATO,1,255) OBJETOCON
         ,ES_ESTPREVIO.DEF_NECESIDAD                 CONSIDERANDOS
         ,ES_ESTPREVIO.OBL_CONTRATISTA
         ,ES_ESTPREVIO.OBL_ENTIDAD
         ,ES_ESTPREVIO.FORMA_PAGOS                   FORMA_PAGO
         ,ES_ESTPREVIO.COSTOTOTALEST                 VALORTOTAL
         ,ES_ESTPREVIO.COSTOTOTALEST                 VALORFINAL
         ,ES_ESTPREVIO.SUPERVISION
         ,ES_ESTPREVIO.NUM_PROCESO
         ,ES_ESTPREVIO.PLAZO_EJECUCION
         ,ES_ESTPREVIO.CARGO_INTERVENTOR
         ,ES_ESTPREVIO.SUCURSAL_SUPERVISION
         ,TERCERO.NOMBRE                             NOMINTERVENTOR
         ,ES_ESTPREVIO.PYE_CONTRATISTA
         ,ES_ESTPREVIO.FUND_JURIDICOS
        INTO 
          MI_COD_ESTUDIO
         ,MI_NOMBREESTUDIOT
         ,MI_CODIGO
         ,MI_DESCRIPCION
         ,MI_OBJETOCON
         ,MI_CONSIDERANDOS
         ,MI_OBL_CONTRATISTA
         ,MI_OBL_ENTIDAD
         ,MI_FORMA_PAGO
         ,MI_VALORTOTAL
         ,MI_VALORFINAL
         ,MI_SUPERVISION
         ,MI_NUM_PROCESO
         ,MI_PLAZO_EJECUCION
         ,MI_CARGO_INTERVENTOR
         ,MI_SUCURSAL_SUPERVISION
         ,MI_NOMINTERVENTOR
         ,MI_PYE_CONTRATISTA
         ,MI_FUND_JURIDICOS
        FROM ES_ESTPREVIO 
          INNER JOIN TIPOADJUDICACION 
             ON ES_ESTPREVIO.COMPANIA      = TIPOADJUDICACION.COMPANIA 
            AND ES_ESTPREVIO.TIPO_CONTRATO = TIPOADJUDICACION.CODIGOPRECONTRATO 
          INNER JOIN TERCERO 
             ON ES_ESTPREVIO.COMPANIA             = TERCERO.COMPANIA 
            AND ES_ESTPREVIO.SUPERVISION          = TERCERO.NIT
            AND ES_ESTPREVIO.SUCURSAL_SUPERVISION = TERCERO.SUCURSAL
        WHERE ES_ESTPREVIO.COMPANIA    = UN_COMPANIA 
          AND ES_ESTPREVIO.COD_ESTUDIO = UN_ESTUDIOPREVIO;

      EXCEPTION WHEN NO_DATA_FOUND THEN  
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
      MI_REEMPLAZOS(1).CLAVE := 'ESTUDIO';
      MI_REEMPLAZOS(1).VALOR := UN_ESTUDIOPREVIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CC_NDF_FCIP_ESTUDIOPREVIO
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_CONSIDERANDOS_CLOB   := PCK_SYSMAN_UTL.FC_CLOB_EXPRESION(UN_TEXTO => MI_CONSIDERANDOS); 
    
    MI_CAMPOS:= 'DESC_ESTPREVIOS       = '''|| NVL(MI_COD_ESTUDIO         ,'0' ) ||''' 
                ,DESTUDIOSPREVIOS      = '''|| NVL(MI_NOMBREESTUDIOT      ,'ND') ||''' 
                ,TIPOADJUDICACION      = '''|| NVL(MI_CODIGO              ,'ND') ||''' 
                ,OBJETOCONTRATO        = '''|| NVL(MI_OBJETOCON           ,'0' ) ||''' 
                ,DESCRIPCION           = '''|| NVL(MI_DESCRIPCION         ,'ND') ||'''  
                ,CONSIDERANDOS         = '  || NVL(MI_CONSIDERANDOS_CLOB  ,'ND') ||  ' 
                ,OBLIGACIONC           = '''|| NVL(MI_OBL_CONTRATISTA     ,'ND') ||''' 
                ,OBLIGACIONESENTIDAD   = '''|| NVL(MI_OBL_ENTIDAD         ,'ND') ||''' 
                ,FORMADEPAGO           = '''|| NVL(MI_FORMA_PAGO          ,'ND') ||''' 
                ,CEDULAINTERVENTOR     = '''|| NVL(MI_SUPERVISION         ,'ND') ||''' 
                ,INTERVENTOR           = '''|| NVL(MI_NOMINTERVENTOR      ,'ND') ||''' 
                ,CARGOINTERVENTOR      = '''|| NVL(MI_CARGO_INTERVENTOR   ,'ND') ||''' 
                ,SUCURSALINTERVENTOR   = '''|| NVL(MI_SUCURSAL_SUPERVISION,'ND') ||''' 
                ,VALORTOTAL            =   '|| NVL(MI_VALORTOTAL          ,0   ) ||'   
                ,VALORFINAL            =   '|| NVL(MI_VALORFINAL          ,0   ) ||'   
                ,NOPROCESO             = '''|| NVL(MI_NUM_PROCESO         ,0   ) ||''' 
                ,DURACION              = '''|| NVL(MI_PLAZO_EJECUCION     ,0   ) ||''' 
                ,FACULTADESCONTRATISTA = '''|| NVL(MI_PYE_CONTRATISTA     ,'ND') ||''' 
                ,NORMASAPLICA          = '''|| NVL(MI_FUND_JURIDICOS      ,'ND') ||''' 
                ,IMPORTARPRE           = -1   
                ,MODIFIED_BY           = '''|| UN_USUARIO                        ||''' 
                ,DATE_MODIFIED         = SYSDATE';

    MI_CONDICION := 'ORDENDECOMPRA.COMPANIA   = '''||UN_COMPANIA   ||'''
                 AND ORDENDECOMPRA.NUMERO     =   '||UN_NUMEROORDEN||'
                 AND ORDENDECOMPRA.CLASEORDEN = '''||UN_CLASEORDEN ||'''';

    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_O
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_ESTUDIOPREVIO';
      MI_REEMPLAZOS(1).VALOR := UN_ESTUDIOPREVIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECONT
                                ,UN_TABLAERROR => MI_TABLA_O
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_CONDICION:= 'COMPANIA      = '''|| UN_COMPANIA    ||''' 
                AND ORDENDECOMPRA =   '|| UN_NUMEROORDEN ||'
                AND CLASEORDEN    = '''|| UN_CLASEORDEN  ||''''; 
    BEGIN 
      BEGIN 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
      MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON1
                                ,UN_TABLAERROR => MI_TABLA_D
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_VALORES:= '(COMPANIA
                  ,CLASEORDEN
                  ,ORDENDECOMPRA
                  ,CODIGO
                  ,DEPENDENCIA
                  ,ELEMENTO
                  ,ESPECIFICACION
                  ,CANTIDAD
                  ,VALORUNITARIO
                  ,PORCIVA
                  ,PORCDESC
                  ,VLRIVA
                  ,VLRTOTAL
                  ,SALDOCANT
                  ,DATE_CREATED
                  ,CREATED_BY
                  ) 
                  SELECT 
                    '''||UN_COMPANIA   ||'''             
                   ,'''||UN_CLASEORDEN ||'''             
                   ,  '||UN_NUMEROORDEN||'               
                   ,ROWNUM                               
                   ,NVL(DEPENDENCIA   ,''999999999999'') 
                   ,NVL(ELEMENTO      ,''ND''          ) 
                   ,NVL(ESPECIFICACION,''ND''          ) 
                   ,CANTIDAD 
                   ,VALORUNITARIO
                   ,PORCIVA
                   ,PORCDESCUENTO
                   ,VALORIVA
                   ,VLRTOTAL
                   ,CANTIDAD                              
                   ,SYSDATE
                   ,'''||UN_USUARIO||'''
                  FROM ES_DITEM_E 
                  WHERE ES_DITEM_E.COMPANIA    = '''||UN_COMPANIA     ||''' 
                    AND ES_DITEM_E.COD_ESTUDIO = '''||UN_ESTUDIOPREVIO||'''';
    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_D
                                             ,UN_ACCION  => 'IS'
                                             ,UN_VALORES => MI_VALORES);   

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
      MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON2
                                ,UN_TABLAERROR => MI_TABLA_D
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_VALORES:= '(COMPANIA
                  ,CLASEORDEN
                  ,NUMERO
                  ,ANOPPTO
                  ,TIPOPPTO
                  ,NUMEROPPTO
                  ,FECHA
                  ,RUBRO
                  ,DATE_CREATED
                  ,CREATED_BY
                  ) 
                  SELECT 
                    DETALLE_COMPROBANTE_PPTAL.COMPANIA
                   ,'''||UN_CLASEORDEN ||''' 
                   ,  '||UN_NUMEROORDEN||' 
                   ,DETALLE_COMPROBANTE_PPTAL.ANO
                   ,DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE
                   ,DETALLE_COMPROBANTE_PPTAL.COMPROBANTE 
                   ,DETALLE_COMPROBANTE_PPTAL.FECHA
                   ,ES_RUBROS_EST_DIS.CODIGO_CUENTA
                   ,SYSDATE
                   ,'''||UN_USUARIO    ||'''
                  FROM ES_RUBROS_EST_DIS 
                    INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                       ON  ES_RUBROS_EST_DIS.COMPANIA   = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
                      AND ES_RUBROS_EST_DIS.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO
                      AND ES_RUBROS_EST_DIS.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE
                      AND ES_RUBROS_EST_DIS.ANO         = DETALLE_COMPROBANTE_PPTAL.ANO 
                      AND ES_RUBROS_EST_DIS.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE
                     LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                       ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA 
                      AND DETALLE_COMPROBANTE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO 
                      AND DETALLE_COMPROBANTE_PPTAL.CUENTA   = PLAN_PRESUPUESTAL.ID 
                  WHERE ES_RUBROS_EST_DIS.COMPANIA    = '''||UN_COMPANIA     ||'''
                    AND ES_RUBROS_EST_DIS.COD_ESTUDIO = '''||UN_ESTUDIOPREVIO||'''';

    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_OD
                                             ,UN_ACCION  => 'IS'
                                             ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN   
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
      MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON3
                                ,UN_TABLAERROR => MI_TABLA_OD
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    <<CONCATENAGARANTIAS>>
    FOR MI_RS IN (SELECT 
                    ROWNUM CONSEC
                   ,ES_POLIZA_EST.AMPARO
                   ,ES_POLIZA_EST.PORCENTAJE
                   ,TIPOPOLIZA.DESCRIPCION TIPOPOLIZA,
                   ES_POLIZA_EST.UNIDAD_MEDIDA
                  FROM ES_POLIZA_EST
                  INNER JOIN TIPOPOLIZA
                     ON ES_POLIZA_EST.TIPOPOLIZA = TIPOPOLIZA.CODIGO
                  WHERE ES_POLIZA_EST.COMPANIA    = UN_COMPANIA
                    AND ES_POLIZA_EST.COD_ESTUDIO = UN_ESTUDIOPREVIO 
                  ORDER BY ES_POLIZA_EST.COD_POLIZA) 
    LOOP
      MI_IMPGARANTIAS := MI_IMPGARANTIAS              ||
                         MI_RS.CONSEC                 ||
                         '. De '                      ||
                         MI_RS.TIPOPOLIZA             ||                         
                     CASE WHEN MI_RS.UNIDAD_MEDIDA = 'PORCENTAJE'
                       THEN  
                         ', por el '                  ||
                         MI_RS.PORCENTAJE             ||
                         '% del valor del mismo, por '
                      ELSE
                         ', por el valor '            ||
                         MI_RS.PORCENTAJE             ||
                         ', por '
                      END
                         ||
                         MI_RS.AMPARO                 ||
                         CHR(9);
    END LOOP CONCATENAGARANTIAS;

    MI_CAMPOS := 'GARANTIA      = '''||NVL(MI_IMPGARANTIAS,'ND')||'''
                 ,DATE_MODIFIED = SYSDATE
                 ,MODIFIED_BY   = '''||UN_USUARIO               ||'''';

    MI_CONDICION := 'ORDENDECOMPRA.COMPANIA   = '''||UN_COMPANIA   ||'''
                 AND ORDENDECOMPRA.NUMERO     =   '||UN_NUMEROORDEN||'
                 AND ORDENDECOMPRA.CLASEORDEN = '''||UN_CLASEORDEN ||'''';
    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_O
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
      MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON4
                                ,UN_TABLAERROR => MI_TABLA_O
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    BEGIN 
      SELECT NVL(UBICACION,'ND') LOCALIZACION 
      INTO MI_LOCALIZACION 
      FROM ES_LOCALIZA 
      WHERE ES_LOCALIZA.COMPANIA    = UN_COMPANIA 
        AND ES_LOCALIZA.COD_ESTUDIO = UN_ESTUDIOPREVIO;

    EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        MI_LOCALIZACION:=NULL;
      WHEN TOO_MANY_ROWS THEN
        MI_LOCALIZACION:=NULL;
    END;       

    IF MI_LOCALIZACION IS NOT NULL THEN
      MI_CAMPOS := 'LUGARDEENTREGA = '''||MI_LOCALIZACION||''' 
                   ,DATE_MODIFIED  = SYSDATE
                   ,MODIFIED_BY    = '''||UN_USUARIO     ||'''';

      MI_CONDICION:= 'ORDENDECOMPRA.COMPANIA   = '''||UN_COMPANIA   ||'''
                  AND ORDENDECOMPRA.NUMERO     =   '||UN_NUMEROORDEN||'
                  AND ORDENDECOMPRA.CLASEORDEN = '''||UN_CLASEORDEN ||'''';

      BEGIN 
        BEGIN 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_O
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        MI_REEMPLAZOS(1).CLAVE:='UN_NUMEROORDEN';
        MI_REEMPLAZOS(1).VALOR:=UN_NUMEROORDEN;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON5
                                  ,UN_TABLAERROR => MI_TABLA_O
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END IF;

    /* (vmolano - 22/07/2016): para relacionar la orden de compra y el estudio previo origen // item 2 - tar 3000000328 */
    MI_CAMPOS := 'NOPROCESO     = '''||UN_NUMPROCESO||''' 
                 ,DATE_MODIFIED = SYSDATE
                 ,MODIFIED_BY   = '''||UN_USUARIO   ||'''';

    MI_CONDICION := 'ORDENDECOMPRA.COMPANIA   = '''||UN_COMPANIA   ||''' 
                 AND ORDENDECOMPRA.NUMERO     =   '||UN_NUMEROORDEN||' 
                 AND ORDENDECOMPRA.CLASEORDEN = '''||UN_CLASEORDEN ||'''';

    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_O
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
      MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON6
                                ,UN_TABLAERROR => MI_TABLA_O
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    <<CREASUPERVISORES>>
    FOR RS IN (SELECT  
                 CEDULA
                ,SUCURSAL 
               FROM ES_SUPERVISORES 
               WHERE COMPANIA       = UN_COMPANIA
                 AND NUMERO_ESTUDIO = UN_ESTUDIOPREVIO)
    LOOP
      MI_CAMPOS:= 'COMPANIA
                  ,CLASEORDEN
                  ,NUMEROCONTRATO
                  ,CEDULA
                  ,SUCURSAL
                  ,CREATED_BY
                  ,DATE_CREATED';

      MI_VALORES:= ''''||UN_COMPANIA   ||'''
                   ,'''||UN_CLASEORDEN ||'''
                   ,  '||UN_NUMEROORDEN||'
                   ,'''||RS.CEDULA     ||'''
                   ,'''||RS.SUCURSAL   ||'''
                   ,'''||UN_USUARIO    ||'''
                   ,SYSDATE';

      BEGIN 
        BEGIN 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_S
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
        MI_REEMPLAZOS(1).CLAVE := 'UN_NUMEROORDEN';
        MI_REEMPLAZOS(1).VALOR := UN_NUMEROORDEN;
        MI_REEMPLAZOS(2).CLAVE := 'RS.CEDULA';
        MI_REEMPLAZOS(2).VALOR := RS.CEDULA;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_IMPORTARPRECON7
                                  ,UN_TABLAERROR => MI_TABLA_S
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END LOOP CREASUPERVISORES;

  END PR_IMPORTARPRECONTRACTUAL;

FUNCTION FC_EXTRAERVALORES 
/*
      NAME              : FC_EXTRAERVALORES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 07/04/2017
      TIME              : 11:30 AM
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se reestructura el proceso para actualizar valores en el formulario pcontrato. 

      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_CLASEORDEN     => Indica la clase de contrato de la orden de compra
                          UN_NUMERO         => El numero del contrato 
                          UN_VALORFINAL     => eL VALOR TOTAL DEL CONTRATO.
                          UN_USUARIO        => Usuario que ingreso a la aplicación



    @NAME:   extraerValores
    @METHOD: GET     
    */
(
     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN IN VARCHAR2
    ,UN_NUMERO     IN PCK_SUBTIPOS.TI_ENTERO_LARGO
    ,UN_VALORFINAL IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
) 
RETURN VARCHAR2
AS 
    MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_IVA             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORFINAL      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUBTOTAL        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUBDESCUENTO    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORTOTAL_REAL PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALOR_NOVEDADES PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALOR_INICIAL   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORTOTAL      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_AJUSTEALPESO    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_EVALVALOR       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIGITOSREDONDEO PCK_SUBTIPOS.TI_ENTERO ;
    MI_RTA             PCK_SUBTIPOS.TI_ENTERO;
    MI_MANEJCUATROXMIL VARCHAR2(10 CHAR);
    MI_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA;
    MI_VLRTOTAL_CREDITO PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

BEGIN
    MI_DIGITOSREDONDEO:=PCK_SYSMAN_UTL.FC_PAR(
                                       UN_COMPANIA  => UN_COMPANIA
                                      ,UN_NOMBRE    => 'DIGITOS REDONDEO GRAN TOTAL O.C.'
                                      ,UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS
                                      ,UN_FECHA_PAR => SYSDATE);
    MI_MANEJCUATROXMIL:=PCK_SYSMAN_UTL.FC_PAR(
                                       UN_COMPANIA  => UN_COMPANIA
                                      ,UN_NOMBRE    => 'MANEJA CUATRO POR MIL'
                                      ,UN_MODULO    => PCK_DATOS.FC_MODULOCONTRATOS
                                      ,UN_FECHA_PAR => SYSDATE);
    BEGIN    
        SELECT  NVL(SUM(PCK_SYSMAN_UTL.FC_ROUND(
                                  CASE WHEN D_ORDENDECOMPRA.CANTIDAD NOT IN (0)
                                       THEN D_ORDENDECOMPRA.VALORUNITARIO*D_ORDENDECOMPRA.CANTIDAD
                                       ELSE D_ORDENDECOMPRA.VLRTOTAL
                                            +D_ORDENDECOMPRA.VLRDESCUENTO
                                            -D_ORDENDECOMPRA.VLRIVA
                                  END,2)),0) SUMA
               ,NVL(SUM(D_ORDENDECOMPRA.VLRIVA),0) SUMAIVA
               ,NVL(SUM(D_ORDENDECOMPRA.VLRDESCUENTO),0) SUMADESCUENTO
          INTO   MI_SUBTOTAL
               , MI_IVA
               , MI_SUBDESCUENTO
          FROM D_ORDENDECOMPRA
         WHERE COMPANIA      = UN_COMPANIA
           AND CLASEORDEN    = UN_CLASEORDEN
           AND ORDENDECOMPRA = UN_NUMERO
        GROUP BY ORDENDECOMPRA;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_SUBTOTAL := 0;
        MI_IVA := 0;
        MI_SUBDESCUENTO := 0;
    END;

    MI_AJUSTEALPESO:= PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(
                                     (MI_SUBTOTAL - MI_SUBDESCUENTO) 
                                     + MI_IVA,
                                     MI_DIGITOSREDONDEO)
                      - ((MI_SUBTOTAL - MI_SUBDESCUENTO) + MI_IVA), 2);
   -- se ajusto 27/07/2018
    IF MI_AJUSTEALPESO IS NULL THEN 
       MI_AJUSTEALPESO:= 0;
    END IF;
   -- 
    IF 'NO' = MI_MANEJCUATROXMIL THEN 
        MI_EVALVALOR:= (MI_SUBTOTAL - MI_SUBDESCUENTO) + MI_IVA + MI_AJUSTEALPESO + 0;
    ELSE 
        MI_EVALVALOR:= (MI_SUBTOTAL - MI_SUBDESCUENTO) + MI_IVA + MI_AJUSTEALPESO+ MI_AJUSTEALPESO;
    END IF;
    MI_VALORFINAL:= UN_VALORFINAL;
    IF MI_VALORFINAL < MI_EVALVALOR THEN
        RETURN MI_EVALVALOR;
    END IF;
    BEGIN 
        SELECT  C_TOTAL_ADICIONESDK.ORDENDECOMPRA_COMPANIA,
                    SUM(C_TOTAL_ADICIONESDK.NOVEDADCONTRATO_VALORTOTAL) VALOR_NOVEDADES, 
                    C_TOTAL_ADICIONESDK.ORDENDECOMPRA_VALORFINAL VALOR_INICIAL,
                    C_TOTAL_ADICIONESDK.ORDENDECOMPRA_VALORTOTAL, 
                    SUM(C_TOTAL_ADICIONESDK.NOVEDADCONTRATO_VALORTOTAL)
                    +C_TOTAL_ADICIONESDK.ORDENDECOMPRA_VALORFINAL VALORTOTAL_REAL
            INTO  MI_COMPANIA
                 ,MI_VALOR_NOVEDADES
                 ,MI_VALOR_INICIAL
                 ,MI_VALORTOTAL
                 ,MI_VALORTOTAL_REAL
            FROM (
            SELECT ORDENDECOMPRA.COMPANIA          ORDENDECOMPRA_COMPANIA, 
                    ORDENDECOMPRA.CLASEORDEN        ORDENDECOMPRA_CLASEORDEN, 
                    ORDENDECOMPRA.NUMERO            ORDENDECOMPRA_NUMERO, 
                    ORDENDECOMPRA.FECHA             ORDENDECOMPRA_FECHA, 
                    ORDENDECOMPRA.FECHAFINALIZACION ORDENDECOMPRA_FECHAFIN, 
                    NOVEDADCONTRATO.TIPOT           NOVEDADCONTRATO_TIPOT, 
                    CLASETRANSACCIONC.NOMBRE        CLASETRANSACCIONC_NOMBRE, 
                    NOVEDADCONTRATO.NOVEDAD         NOVEDADCONTRATO_NOVEDAD, 
                    NOVEDADCONTRATO.NUMERO          NOVEDADCONTRATO_NUMERO, 
                    0                               NOVEDADCONTRATO_VALORTOTAL,  --MOD JM CC927 27/03/2025  la novedad no debe afectar al valor del contrato 
                    TIPOORDENDECOMPRA.NOMBRE        TIPOORDENDECOMPRA_NOMBRE, 
                    TERCERO.NOMBRE                  TERCERO_NOMBRE, 
                    NOVEDADCONTRATO.FECHA           NOVEDADCONTRATO_FECHA, 
                    ORDENDECOMPRA.VALORTOTAL        ORDENDECOMPRA_VALORTOTAL, 
                    ORDENDECOMPRA.VALORFINAL        ORDENDECOMPRA_VALORFINAL
            FROM ORDENDECOMPRA 
              LEFT JOIN NOVEDADCONTRATO
                ON  ORDENDECOMPRA.COMPANIA    = NOVEDADCONTRATO.COMPANIA 
                AND ORDENDECOMPRA.CLASEORDEN  = NOVEDADCONTRATO.CLASEORDEN 
                AND ORDENDECOMPRA.NUMERO      = NOVEDADCONTRATO.ORDENDECOMPRA
              LEFT JOIN TERCERO 
                ON  ORDENDECOMPRA.COMPANIA  = TERCERO.COMPANIA
                AND ORDENDECOMPRA.TERCERO   = TERCERO.NIT 
                AND ORDENDECOMPRA.SUCURSAL  = TERCERO.SUCURSAL 
              LEFT JOIN TIPOORDENDECOMPRA 
                ON  ORDENDECOMPRA.COMPANIA    = TIPOORDENDECOMPRA.COMPANIA 
                AND ORDENDECOMPRA.CLASEORDEN  = TIPOORDENDECOMPRA.CODIGO 
              LEFT JOIN CLASETRANSACCIONC 
                ON  NOVEDADCONTRATO.TIPOT  = CLASETRANSACCIONC.TIPOT 
                AND NOVEDADCONTRATO.CLASET = CLASETRANSACCIONC.CLASET 
            WHERE ORDENDECOMPRA.COMPANIA    = UN_COMPANIA 
              AND ORDENDECOMPRA.CLASEORDEN  = UN_CLASEORDEN
              AND ORDENDECOMPRA.NUMERO      = UN_NUMERO
              AND NVL(TIPOORDENDECOMPRA.CLASE,'') <>'M'
              AND CLASETRANSACCIONC.CLASENOVEDAD = 'D'
            UNION ALL 
           SELECT TIPOORDENDECOMPRA.COMPANIA        ORDENDECOMPRA_COMPANIA, 
                   ORDENDECOMPRA.TIPOAFECTADO         ORDENDECOMPRA_CLASEORDEN, 
                   ORDENDECOMPRA.NUMEROAFECTADO       ORDENDECOMPRA_NUMERO, 
                   ORDENDECOMPRA.FECHA                ORDENDECOMPRA_FECHA, 
                   ORDENDECOMPRA.FECHAFINALIZACION    ORDENDECOMPRA_FECHAFIN, 
                   TIPOORDENDECOMPRA.CODIGO           NOVEDADCONTRATO_TIPOT, 
                   TIPOORDENDECOMPRA.NOMBRE           CLASETRANSACCIONC_NOMBRE, 
                   ORDENDECOMPRA.NUMERO               NOVEDADCONTRATO_NOVEDAD, 
                   ORDENDECOMPRA.CONSECUTIVOADICIONES NOVEDADCONTRATO_NUMERO, 
                   ORDENDECOMPRA.VALORTOTAL           NOVEDADCONTRATO_VALORTOTAL, 
                   TIPOORDENDECOMPRA.NOMBRE           TIPOORDENDECOMPRA_NOMBRE, 
                   TERCERO.NOMBRE                     TERCERO_NOMBRE, 
                   ORDENDECOMPRA.FECHA                NOVEDADCONTRATO_FECHA, 
                   ORDENDECOMPRA_1.VALORTOTAL         ORDENDECOMPRA_VALORTOTAL, 
                   ORDENDECOMPRA_1.VALORFINAL         ORDENDECOMPRA_VALORFINAL
            FROM TIPOORDENDECOMPRA 
              INNER JOIN ORDENDECOMPRA 
                ON  TIPOORDENDECOMPRA.COMPANIA = ORDENDECOMPRA.COMPANIA 
                AND TIPOORDENDECOMPRA.CODIGO   = ORDENDECOMPRA.CLASEORDEN 
              INNER JOIN TERCERO 
                ON  ORDENDECOMPRA.COMPANIA = TERCERO.COMPANIA 
                AND ORDENDECOMPRA.TERCERO  = TERCERO.NIT
                AND ORDENDECOMPRA.SUCURSAL = TERCERO.SUCURSAL
              INNER JOIN ORDENDECOMPRA ORDENDECOMPRA_1 
                ON  ORDENDECOMPRA.NUMEROAFECTADO = ORDENDECOMPRA_1.NUMERO
                AND ORDENDECOMPRA.TIPOAFECTADO   = ORDENDECOMPRA_1.CLASEORDEN
                AND ORDENDECOMPRA.COMPANIA       = ORDENDECOMPRA_1.COMPANIA
            WHERE TIPOORDENDECOMPRA.COMPANIA    = UN_COMPANIA 
              AND ORDENDECOMPRA.TIPOAFECTADO    = UN_CLASEORDEN 
              AND ORDENDECOMPRA.NUMEROAFECTADO  = UN_NUMERO    
              AND ORDENDECOMPRA.CLASENOVEDAD    IN ('D','Z')
              AND TIPOORDENDECOMPRA.CLASE       = 'M' ) C_TOTAL_ADICIONESDK 
            GROUP BY  C_TOTAL_ADICIONESDK.ORDENDECOMPRA_COMPANIA, 
                      C_TOTAL_ADICIONESDK.ORDENDECOMPRA_CLASEORDEN, 
                      C_TOTAL_ADICIONESDK.ORDENDECOMPRA_NUMERO, 
                      C_TOTAL_ADICIONESDK.ORDENDECOMPRA_VALORFINAL, 
                      C_TOTAL_ADICIONESDK.ORDENDECOMPRA_VALORTOTAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMPANIA        :=NULL;
        MI_VALOR_NOVEDADES :=NULL;
        MI_VALOR_INICIAL   :=NULL;
        MI_VALORTOTAL      :=NULL;
        MI_VALORTOTAL_REAL :=NULL;
    END;
    
    BEGIN 
        SELECT (ORDENDECOMPRA.VALORFINAL + NVL(ADICIONES.VALORTOTAL,0) + NVL(NOTA_CREDITO.VALOR,0))
            INTO MI_VLRTOTAL_CREDITO
            FROM ORDENDECOMPRA
            LEFT JOIN (
                SELECT COMPANIA, TIPOAFECTADO, NUMEROAFECTADO, SUM(VALORTOTAL) VALORTOTAL
                FROM ORDENDECOMPRA
                WHERE ORDENDECOMPRA.COMPANIA = UN_COMPANIA  
                AND ORDENDECOMPRA.TIPOAFECTADO = UN_CLASEORDEN
                AND ORDENDECOMPRA.NUMEROAFECTADO = UN_NUMERO
                GROUP BY COMPANIA, TIPOAFECTADO, NUMEROAFECTADO) ADICIONES
            ON ADICIONES.COMPANIA = ORDENDECOMPRA.COMPANIA
            AND ADICIONES.TIPOAFECTADO = ORDENDECOMPRA.CLASEORDEN
            AND ADICIONES.NUMEROAFECTADO = ORDENDECOMPRA.NUMERO
            LEFT JOIN (
                SELECT COMPANIA, CLASEORDEN, ORDENDECOMPRA, SUM(VALOR) VALOR 
                FROM VLR_NOTA_CREDITO
                WHERE VLR_NOTA_CREDITO.COMPANIA = UN_COMPANIA
                AND VLR_NOTA_CREDITO.CLASEORDEN = UN_CLASEORDEN
                AND VLR_NOTA_CREDITO.ORDENDECOMPRA = UN_NUMERO
                GROUP BY COMPANIA, CLASEORDEN, ORDENDECOMPRA) NOTA_CREDITO 
            ON NOTA_CREDITO.COMPANIA = ORDENDECOMPRA.COMPANIA
            AND NOTA_CREDITO.CLASEORDEN = ORDENDECOMPRA.CLASEORDEN
            AND NOTA_CREDITO.ORDENDECOMPRA = ORDENDECOMPRA.NUMERO
            WHERE ORDENDECOMPRA.COMPANIA = UN_COMPANIA  
                AND ORDENDECOMPRA.CLASEORDEN = UN_CLASEORDEN
                AND ORDENDECOMPRA.NUMERO = UN_NUMERO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VLRTOTAL_CREDITO := UN_VALORFINAL;
    END;
    
    MI_TABLA:='ORDENDECOMPRA';
    IF MI_COMPANIA IS NOT NULL THEN 
        BEGIN 
            MI_CAMPOS:=' VALORTOTAL = '||CASE WHEN MI_VALORTOTAL_REAL IS NULL
                                              THEN UN_VALORFINAL
                                              ELSE MI_VALORTOTAL_REAL
                                         END||
                       ',VLR_DEVUELTO = '||MI_VLRTOTAL_CREDITO||             
                       ',MODIFIED_BY = '''||UN_USUARIO||''' '||
                       ',DATE_MODIFIED = SYSDATE';
            MI_CONDICION:= '     COMPANIA   = '''||UN_COMPANIA||'''
                             AND CLASEORDEN = '''||UN_CLASEORDEN||'''
                             AND NUMERO     = '||UN_NUMERO;
            BEGIN 
                MI_RTA:=PCK_DATOS.FC_ACME(
                                   UN_TABLA     => MI_TABLA
                                 , UN_ACCION    => 'M'
                                 , UN_CAMPOS    => MI_CAMPOS
                                 , UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='NUMERO';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
             PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_EXTRAERVALORES1
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
        END;
    END IF;
    BEGIN 
        MI_CAMPOS:=' VALORIVA       = '||MI_IVA||
                   ' ,VALORDESCUENTO = '|| MI_SUBDESCUENTO||
                   ' ,SUBTOTAL       = '|| MI_SUBTOTAL||
                   ' ,AJUSTEPESO     = '|| MI_AJUSTEALPESO||
                   ' ,MODIFIED_BY    = '''||UN_USUARIO||''' '||
                   ' ,DATE_MODIFIED = SYSDATE';
        MI_CONDICION:= '     COMPANIA   = '''||UN_COMPANIA||'''
                         AND CLASEORDEN = '''||UN_CLASEORDEN||'''
                         AND NUMERO     = '||UN_NUMERO;
        BEGIN 
            MI_RTA:=PCK_DATOS.FC_ACME(
                              UN_TABLA     => MI_TABLA
                             ,UN_ACCION    => 'M'
                             ,UN_CAMPOS    => MI_CAMPOS
                             ,UN_CONDICION => MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            MI_REEMPLAZOS(0).CLAVE:='NUMERO';
            MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_EXTRAERVALORES2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
    END;
    RETURN 'OK';
END FC_EXTRAERVALORES;

FUNCTION FC_COPIARCONTRATO(
/*
      NAME              : FC_COPIARCONTRATO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 07/04/2017
      TIME              : 04:44 PM
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se reestructura el proceso para actualizar valores en el formulario pcontrato. 

      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_CLASEORDEN     => Indica la clase de contrato de la orden de compra
                          UN_COPIARDE       => El numero del contrato al que se va a copiar la informacion.
                          UN_NUMERO         => El numero del contrato del cual se va a copiar la informacion.
                          UN_USUARIO        => Usuario que ingreso a la aplicación



    @NAME:   copiarContrato
    @METHOD: GET     
    */
     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_COPIARDE   IN ORDENDECOMPRA.NUMERO%TYPE
    ,UN_VIGENCIA   IN PCK_SUBTIPOS.TI_ANIO
    ,UN_NUMERO     IN ORDENDECOMPRA.NUMERO%TYPE
    ,UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN VARCHAR2
AS 
    MI_ANIO        PCK_SUBTIPOS.TI_ANIO;
    MI_CONSECUTIVO ORDENDECOMPRA.NUMERO%TYPE;
    MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    BEGIN 
       SELECT EXTRACT(YEAR FROM FECHA) 
         INTO MI_ANIO 
         FROM ORDENDECOMPRA 
        WHERE COMPANIA   = UN_COMPANIA  
          AND CLASEORDEN = UN_CLASEORDEN  
          AND NUMERO     = UN_COPIARDE;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_ANIO:= NULL;
    END;
    IF MI_ANIO IS NOT NULL  THEN
        MI_CONSECUTIVO:=PCK_CONTRATOS_COM1.FC_CONSEC_CONTRATOS(
                                           UN_COMPANIA      => UN_COMPANIA
                                          ,UN_CLASECONTRATO => UN_CLASEORDEN
                                          ,UN_ANIOVIGENCIA  => MI_ANIO
                                          ,UN_USUARIO       => UN_USUARIO);
   END IF;
    MI_TABLA  :=  'ORDENDECOMPRA';
    MI_CAMPOS :=  'COMPANIA,'||
                        'CLASEORDEN,'||
                        'NUMERO,'||
                        'TERCERO,'||
                        'SUCURSAL,'||
                        'DEPENDENCIA,'||
                        'DESCRIPCION,'||
                        'PORCIVAGLOBAL,'||
                        'PORCDESCGLOBAL,'||
                        'SUBTOTAL,'||
                        'VALORIVA,'||
                        'VALORDESCUENTO,'||
                        'VALORTOTAL,'||
                        'VACIA,'||
                        'COD_REQUISICION,'||
                        'NUMCOTIZACION,'||
                        'REALIZADA,'||
                        'NUMDOCUMENTO,'||
                        'FORMADEPAGO,'||
                        'LUGARDEENTREGA,'||
                        'PLAZODEENTREGA,'||
                        'CONTRATACION,'||
                        'VALORDISPONIBILIDAD,'||
                        'ANULADA,'||
                        'RUBROCOMPRAS,'||
                        'ANULADO,'||
                        'NOTAS,'||
                        'VALORFINAL,'||
                        'CERTIFICACIONEXPEDIDA,'||
                        'ESTADO,'||
                        'DURACION,'||
                        'VALORDELOSPAGOS,'||
                        'NORMASAPLICA,'||
                        'ORDENADOR,'||
                        'SUCURSAL_ORDENADOR,'||
                        'CLASEDISPONIBILIDAD,'||
                        'VALORDOLARES,'||
                        'TIPOMONEDA,'||
                        'IMPRESO,'||
                        'OBLIGACIONC,'||
                        'RESPONSABLE,'||
                        'SUCURSAL_RESPONSABLE,'||
                        'NUMEROCONTRATO,'||
                        'CLASERESERVA,'||
                        'VALORRESERVA,'||
                        'CATEGORIA,'||
                        'AJUSTEPESO,'||
                        'RESOLUCION1,'||
                        'RESOLUCION2,'||
                        'RESOLUCION3,'||
                        'OBLIGACIONESENTIDAD,'||
                        'ABONOS,'||
                        'ADICIONES,'||
                        'REGISTROBPI,'||
                        'ESTUDIOSPREVIOS,'||
                        'TERMINOSDEREFERENCIA,'||
                        'LICITACIONOCONCURSO,'||
                        'CLASE,'||
                        'VALORPAGOS,'||
                        'FECHAPAGOS,'||
                        'NOMBRECONTRATISTA,'||
                        'CEDULACONTRATISTA,'||
                        'SUCURSALCONTRATISTA,'||
                        'FACULTADESCONTRATISTA,'||
                        'PORCADMINISTRACIONOP,'||
                        'PORCIMPREVISTOSOP,'||
                        'PORCUTILIDADESOP,'||
                        'VALORCOSTODIRECTO,'||
                        'IVASOBREUTILIDADESOP,'||
                        'VALORTOTALOP,'||
                        'TIPOADJUDICACION,'||
                        'CAPACIDADTOTALCONTRATACION,'||
                        'CAPACIDADRESIDUAL,'||
                        'DESTINO,'||
                        'PORCESTADOEJECUCION,'||
                        'CLASIFICACIONCC,'||
                        'CUMPLIMIENTO,'||
                        'TIPOINTERVENTOR,'||
                        'ENTRECAUDA,'||
                        'NUMRECIBO,'||
                        'VALRECIBO,'||
                        'MONEDAPAGO,'||
                        'MONEDAREF,'||
                        'CANON,'||
                        'CANONINCLUIDOIVA,'||
                        'TIPOINMUEBLE,'||
                        'NUMEROINMUEBLE,'||
                        'CONTRATOPRINCIPAL,'||
                        'PRORROGASAUTOMATICAS,'||
                        'TIPOSOCIEDAD,'||
                        'DESCRIPCIONINMUEBLE,'||
                        'DIRECCIONINMUEBLE,'||
                        'OBSERVACIONESINMUEBLE,'||
                        'OBJETOCONTRATO,'||
                        'VALORINTERVENCION,'||
                        'REGISTRADOPC,'||
                        'IVAARRENDAMIENTO,'||
                        'TIPOAFECTADO,'||
                        'NUMEROAFECTADO,'||
                        'INFORMESAUDITORIA,'||
                        'NROINVITACION,'||
                        'NROLICITACION,'||
                        'ACTOAPERTURA,'||
                        'USUARIOAFECTO,'||
                        'UNIDAD_PLAZO,'||
                        'CARGOINTERVENTOR,'||
                        'CEDULAINTERVENTOR,'||
                        'SUCURSALINTERVENTOR,'||
                        'INTERVENTOR,'||
                        'RECPUBLICACION,'||
                        'CONSIDERANDOS,'||
                        'GARANTIA,'||
                        'REQUISITOSEJECUCION,'||
                        'CONSECUTIVOADICIONES,'||
                        'ACTUALIZAPLANDECOMPRAS,'||
                        'NOPROCESO,'||
                        'ESTADOINMUEBLE,'||
                        'COMPONENTEBP,'||
                        'NUMPERSONASCONTRATADAS,'||
                        'NUMEROCONTRATOINTERV,'||
                        'TIPO_MONTO,'||
                        'ACTA_LIQUIDACION,'||
                        'ACTO_ADMIN_INTERVENTOR,'||
                        'NUMEROACTA,'||
                        'TIPOLOGIA,'||
                        'CLASECONTRATO,'||
                        'REPORTAR_SICE,'||
                        'CONSECUTIVOWF,'||
                        'PROFESION,'||
                        'HONORARIOS,'||
                        'VEHICULO,'||
                        'TOPEVEHICULO,'||
                        'EXPERIENCIA,'||
                        'DESC_ESTPREVIOS,'||
                        'ODC_BANCO,'||
                        'NO_CUENTA,'||
                        'TIPO_CUENTA,'||
                        'DOCUMENTOSDELCONTRATO_AUX,'||
                        'CODEQUIVALENTE,'||
                        'SECTOR,'||
                        'LEY,'||
                        'APORTESTE,'||
                        'APORTESEN,'||
                        'TIPODIA,'||
                        'VALORMONEDAREF,'||
                        'TIPOCONTRATISTA,'||
                        'CODPAIS,'||
                        'PORADMINISTRACION,'||
                        'PORINPREVISTOS,'||
                        'PORUTILIDADES,'||
                        'VALOR_ANTICIPO,'||
                        'PLANDECOMPRASPORLOTE,'||
                        'CLASENOVEDAD,'||
                        'EJECUTOR,'||
                        'NOMBRE_PROGRAMA,'||
                        'NOMBRE_PROYECTO,'||
                        'TIPO_CONTRATO_CAS,'||
                        'NITCESION,'||
                        'SUCURSALCESION,'||
                        'PERS_INV_CONTRATO,'||
                        'PORC_ANTICIPO,'||
                        'TIPOCONTRATACION,'||
                        'INDICADORCUMPLIMIENTO,'||
                        'INDICADORENVIO,'||
                        'CALIFICACION,'||
                        'R6_CLASIFICACION,'||
                        'VALORTOTALCONIVA,'||
                        'APORTESTE_AD,'||
                        'SECCIONAL,'||
                        'TIPO_ADQUISICION,'||
                        'AJUSTE_DEC_MANUAL,'||
                        'CONTACTO,'||
                        'IMPORTARPRE,'||
                        'ANEXOPOLIZA,'||
                        'RESOAPOLIZA,'||
                        'CONVENIO,'||
                        'CLASE_CONVENIO,'||
                        'NUM_CONVENIO,'||
                        'PRECIOS_UNITARIOS,'||
                        'ORDENADOR_MODIF,'||
                        'SUCURSAL_MODIF,'||
                        'RESOPOLIZA,'||
                        'EMERGENCIAIN,'||
                        'MODALIDAD,'||
                        'FUNCION,'||
                        'TIPOCONTR_SECOP,'||
                        'ACTUALIZARPLANDECOMPRAS,'||
                        'PROYECTO,'||
                        'CLASE_APORTES,'||
                        'DESTUDIOSPREVIOS,'||
                        'FENVIODEPENDENCIA,'||
                        'FRECEPCIONR,'||
                        'FENNVIODEPENDENCIA,'||
                        'FINALIZACION,'||
                        'FECHAFIJACION,'||
                        'FECHADESFIJACION,'||
                        'FECHAFIRMA,'||
                        'FECHAPOLIZAS,'||
                        'FECHARECIBO,'||
                        'FECHALIQUIDACION,'||
                        'FECHASUSCRIPCION,'||
                        'FECHAANULACION,'||
                        'FECHAPUBLICACION,'||
                        'FECHALEGALIZACION,'||
                        'FECHADILIGENCIAMIENTO,'||
                        'FECHARECPUBLIC,'||
                        'FECHAPREPLIEGOS,'||
                        'FECHATERDEFINITIVOS,'||
                        'FECHAAPERTURA,'||
                        'FECHACIERRE,'||
                        'FECHAEVALUACION,'||
                        'FECHAADJUDICACION,'||
                        'FECHAINICIO,'||
                        'FECHAPSUSPENSION,'||
                        'FECHAREINICIO,'||
                        'FECHACREACION,'||
                        'FECHAMODIFICACION,'||
                        'FECHADEENTREGA,'||
                        'FECHAINICIAL,'||
                        'FECHAFINAL,'||
                        'FECHAVENCIMIENTO,'||
                        'FECHAINVITACION,'||
                        'FECHAAFECTO,'||
                        'FECHA_NACTO,'||
                        'FECHABANCOPROYECTOS,'||
                        'FECHAMODPOLIZA,'||
                        'FECHAFINALIZACION,'||
                        'FECHA,'||
                        'FFIRMAS,'||
                        'FENVIORESERVA,'||
                        'DATE_CREATED,'||
                        'CREATED_BY';
        MI_VALORES   := 'SELECT COMPANIA,'||
                              ' CLASEORDEN,'||
                              ' ''' || MI_CONSECUTIVO || ''','||
                              ' TERCERO,'||
                              ' SUCURSAL,'||
                              ' DEPENDENCIA,'||
                              ' DESCRIPCION,'||
                              ' PORCIVAGLOBAL,'||
                              ' PORCDESCGLOBAL,'||
                              ' SUBTOTAL,'||
                              ' VALORIVA,'||
                              ' VALORDESCUENTO,'||
                              ' VALORTOTAL,'||
                              ' VACIA,'||
                              ' COD_REQUISICION,'||
                              ' NUMCOTIZACION,'||
                              ' REALIZADA,'||
                              ' NUMDOCUMENTO,'||
                              ' FORMADEPAGO,'||
                              ' LUGARDEENTREGA,'||
                              ' PLAZODEENTREGA,'||
                              ' CONTRATACION,'||
                              ' VALORDISPONIBILIDAD,'||
                              ' ANULADA,'||
                              ' RUBROCOMPRAS,'||
                              ' ANULADO,'||
                              ' NOTAS,'||
                              ' VALORFINAL,'||
                              ' CERTIFICACIONEXPEDIDA,'||
                              ' ESTADO,'||
                              ' DURACION,'||
                              ' VALORDELOSPAGOS,'||
                              ' NORMASAPLICA,'||
                              ' ORDENADOR,'||
                              ' SUCURSAL_ORDENADOR,'||
                              ' CLASEDISPONIBILIDAD,'||
                              ' VALORDOLARES,'||
                              ' TIPOMONEDA,'||
                              ' IMPRESO,'||
                              ' OBLIGACIONC,'||
                              ' RESPONSABLE,'||
                              ' SUCURSAL_RESPONSABLE,'||
                              ' NUMEROCONTRATO,'||
                              ' CLASERESERVA,'||
                              ' VALORRESERVA,'||
                              ' CATEGORIA,'||
                              ' AJUSTEPESO,'||
                              ' RESOLUCION1,'||
                              ' RESOLUCION2,'||
                              ' RESOLUCION3,'||
                              ' OBLIGACIONESENTIDAD,'||
                              ' ABONOS,'||
                              ' ADICIONES,'||
                              ' REGISTROBPI,'||
                              ' ESTUDIOSPREVIOS,'||
                              ' TERMINOSDEREFERENCIA,'||
                              ' LICITACIONOCONCURSO,'||
                              ' CLASE,'||
                              ' VALORPAGOS,'||
                              ' FECHAPAGOS,'||
                              ' NOMBRECONTRATISTA,'||
                              ' CEDULACONTRATISTA,'||
                              ' SUCURSALCONTRATISTA,'||
                              ' FACULTADESCONTRATISTA,'||
                              ' PORCADMINISTRACIONOP,'||
                              ' PORCIMPREVISTOSOP,'||
                              ' PORCUTILIDADESOP,'||
                              ' VALORCOSTODIRECTO,'||
                              ' IVASOBREUTILIDADESOP,'||
                              ' VALORTOTALOP,'||
                              ' TIPOADJUDICACION,'||
                              ' CAPACIDADTOTALCONTRATACION,'||
                              ' CAPACIDADRESIDUAL,'||
                              ' DESTINO,'||
                              ' PORCESTADOEJECUCION,'||
                              ' CLASIFICACIONCC,'||
                              ' CUMPLIMIENTO,'||
                              ' TIPOINTERVENTOR,'||
                              ' ENTRECAUDA,'||
                              ' NUMRECIBO,'||
                              ' VALRECIBO,'||
                              ' MONEDAPAGO,'||
                              ' MONEDAREF,'||
                              ' CANON,'||
                              ' CANONINCLUIDOIVA,'||
                              ' TIPOINMUEBLE,'||
                              ' NUMEROINMUEBLE,'||
                              ' CONTRATOPRINCIPAL,'||
                              ' PRORROGASAUTOMATICAS,'||
                              ' TIPOSOCIEDAD,'||
                              ' DESCRIPCIONINMUEBLE,'||
                              ' DIRECCIONINMUEBLE,'||
                              ' OBSERVACIONESINMUEBLE,'||
                              ' OBJETOCONTRATO,'||
                              ' VALORINTERVENCION,'||
                              ' REGISTRADOPC,'||
                              ' IVAARRENDAMIENTO,'||
                              ' TIPOAFECTADO,'||
                              ' NUMEROAFECTADO,'||
                              ' INFORMESAUDITORIA,'||
                              ' NROINVITACION,'||
                              ' NROLICITACION,'||
                              ' ACTOAPERTURA,'||
                              ' USUARIOAFECTO,'||
                              ' UNIDAD_PLAZO,'||
                              ' CARGOINTERVENTOR,'||
                              ' CEDULAINTERVENTOR,'||
                              ' SUCURSALINTERVENTOR,'||
                              ' INTERVENTOR,'||
                              ' RECPUBLICACION,'||
                              ' CONSIDERANDOS,'||
                              ' GARANTIA,'||
                              ' REQUISITOSEJECUCION,'||
                              ' CONSECUTIVOADICIONES,'||
                              ' ACTUALIZAPLANDECOMPRAS,'||
                              ' NOPROCESO,'||
                              ' ESTADOINMUEBLE,'||
                              ' COMPONENTEBP,'||
                              ' NUMPERSONASCONTRATADAS,'||
                              ' NUMEROCONTRATOINTERV,'||
                              ' TIPO_MONTO,'||
                              ' ACTA_LIQUIDACION,'||
                              ' ACTO_ADMIN_INTERVENTOR,'||
                              ' NUMEROACTA,'||
                              ' TIPOLOGIA,'||
                              ' CLASECONTRATO,'||
                              ' REPORTAR_SICE,'||
                              ' CONSECUTIVOWF,'||
                              ' PROFESION,'||
                              ' HONORARIOS,'||
                              ' VEHICULO,'||
                              ' TOPEVEHICULO,'||
                              ' EXPERIENCIA,'||
                              ' DESC_ESTPREVIOS,'||
                              ' ODC_BANCO,'||
                              ' NO_CUENTA,'||
                              ' TIPO_CUENTA,'||
                              ' DOCUMENTOSDELCONTRATO_AUX,'||
                              ' CODEQUIVALENTE,'||
                              ' SECTOR,'||
                              ' LEY,'||
                              ' APORTESTE,'||
                              ' APORTESEN,'||
                              ' TIPODIA,'||
                              ' VALORMONEDAREF,'||
                              ' TIPOCONTRATISTA,'||
                              ' CODPAIS,'||
                              ' PORADMINISTRACION,'||
                              ' PORINPREVISTOS,'||
                              ' PORUTILIDADES,'||
                              ' VALOR_ANTICIPO,'||
                              ' PLANDECOMPRASPORLOTE,'||
                              ' CLASENOVEDAD,'||
                              ' EJECUTOR,'||
                              ' NOMBRE_PROGRAMA,'||
                              ' NOMBRE_PROYECTO,'||
                              ' TIPO_CONTRATO_CAS,'||
                              ' NITCESION,'||
                              ' SUCURSALCESION,'||
                              ' PERS_INV_CONTRATO,'||
                              ' PORC_ANTICIPO,'||
                              ' TIPOCONTRATACION,'||
                              ' INDICADORCUMPLIMIENTO,'||
                              ' INDICADORENVIO,'||
                              ' CALIFICACION,'||
                              ' R6_CLASIFICACION,'||
                              ' VALORTOTALCONIVA,'||
                              ' APORTESTE_AD,'||
                              ' SECCIONAL,'||
                              ' TIPO_ADQUISICION,'||
                              ' AJUSTE_DEC_MANUAL,'||
                              ' CONTACTO,'||
                              ' IMPORTARPRE,'||
                              ' ANEXOPOLIZA,'||
                              ' RESOAPOLIZA,'||
                              ' CONVENIO,'||
                              ' CLASE_CONVENIO,'||
                              ' NUM_CONVENIO,'||
                              ' PRECIOS_UNITARIOS,'||
                              ' ORDENADOR_MODIF,'||
                              ' SUCURSAL_MODIF,'||
                              ' RESOPOLIZA,'||
                              ' EMERGENCIAIN,'||
                              ' MODALIDAD,'||
                              ' FUNCION,'||
                              ' TIPOCONTR_SECOP,'||
                              ' ACTUALIZARPLANDECOMPRAS,'||
                              ' PROYECTO,'||
                              ' CLASE_APORTES,'||
                              ' DESTUDIOSPREVIOS,'||
                              ' FENVIODEPENDENCIA,'||
                              ' FRECEPCIONR,'||
                              ' FENNVIODEPENDENCIA,'||
                              ' FINALIZACION,'||
                              ' FECHAFIJACION,'||
                              ' FECHADESFIJACION,'||
                              ' FECHAFIRMA,'||
                              ' FECHAPOLIZAS,'||
                              ' FECHARECIBO,'||
                              ' FECHALIQUIDACION,'||
                              ' FECHASUSCRIPCION,'||
                              ' FECHAANULACION,'||
                              ' FECHAPUBLICACION,'||
                              ' FECHALEGALIZACION,'||
                              ' FECHADILIGENCIAMIENTO,'||
                              ' FECHARECPUBLIC,'||
                              ' FECHAPREPLIEGOS,'||
                              ' FECHATERDEFINITIVOS,'||
                              ' FECHAAPERTURA,'||
                              ' FECHACIERRE,'||
                              ' FECHAEVALUACION,'||
                              ' FECHAADJUDICACION,'||
                              ' FECHAINICIO,'||
                              ' FECHAPSUSPENSION,'||
                              ' FECHAREINICIO,'||
                              ' FECHACREACION,'||
                              ' FECHAMODIFICACION,'||
                              ' FECHADEENTREGA,'||
                              ' FECHAINICIAL,'||
                              ' FECHAFINAL,'||
                              ' FECHAVENCIMIENTO,'||
                              ' FECHAINVITACION,'||
                              ' FECHAAFECTO,'||
                              ' FECHA_NACTO,'||
                              ' FECHABANCOPROYECTOS,'||
                              ' FECHAMODPOLIZA,'||
                              ' FECHAFINALIZACION,'||
                              ' FECHA,'||
                              ' FFIRMAS,'||
                              ' FENVIORESERVA, '||
                              ' SYSDATE ,'||
                              ' '''||UN_USUARIO||''''||
                       '   FROM ORDENDECOMPRA '||
                       '  WHERE COMPANIA   = ''' || UN_COMPANIA ||
                     '''    AND CLASEORDEN = ''' || UN_CLASEORDEN ||
                     '''    AND NUMERO     =   ' || UN_COPIARDE ;


        BEGIN 
            BEGIN 
                PCK_DATOS.GL_RTA:=          PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'IS',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_VALORES   => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
             MI_REEMPLAZOS(1).CLAVE := 'UN_COPIARDE';
             MI_REEMPLAZOS(1).VALOR := UN_COPIARDE;
             MI_REEMPLAZOS(2).CLAVE := 'NUMERO';
             MI_REEMPLAZOS(2).VALOR := MI_CONSECUTIVO;

            PCK_ERR_MSG.RAISE_WITH_MSG(    
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_COPIARCONTRATO1
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
        END;
        MI_TABLA:='D_ORDENDECOMPRA';
        MI_VALORES:= ' (COMPANIA, '||
                     '  CLASEORDEN, '||
                     '  ORDENDECOMPRA, '||
                     '  CODIGO, '||
                     '  ORDENDESUMINISTRO, '||
                     '  DEPENDENCIA, '||
                     '  ELEMENTO, '||
                     '  ESPECIFICACION, '||
                     '  CENTRODECOSTO, '||
                     '  CANTIDAD, '||
                     '  SALDOCANT, '||
                     '  VALORUNITARIO, '||
                     '  PORCIVA, '||
                     '  PORCDESC, '||
                     '  VLRTOTAL, '||
                     '  MARCA, '||
                     '  VLRIVA, '||
                     '  VLRDESCUENTO, '||
                     '  CUATROXMIL, '||
                     '  CODIGOCUBS, '||
                     '  IMPREVISTOS, '||
                     '  PAGOTERCEROS, '||
                     '  CREATED_BY, '||
                     '  DATE_CREATED)'||
                     ' SELECT   '''||UN_COMPANIA||''' COMPANIA, '||
                     '          '''||UN_CLASEORDEN||''' CLASEORDEN,   '|| 
                     '          '''||MI_CONSECUTIVO||''' ORDENDECOMPRA,    '||
                     '          D_ORDENDECOMPRA.CODIGO,    '||
                     '          D_ORDENDECOMPRA.ORDENDESUMINISTRO,   '||
                     '          D_ORDENDECOMPRA.DEPENDENCIA,  '||
                     '          D_ORDENDECOMPRA.ELEMENTO,   '||
                     '          D_ORDENDECOMPRA.ESPECIFICACION,  '||
                     '          D_ORDENDECOMPRA.CENTRODECOSTO,    '||
                     '          D_ORDENDECOMPRA.CANTIDAD,     '||
                     '          D_ORDENDECOMPRA.SALDOCANT,    '||
                     '          D_ORDENDECOMPRA.VALORUNITARIO,   '||
                     '          D_ORDENDECOMPRA.PORCIVA,   '||
                     '          D_ORDENDECOMPRA.PORCDESC,   '||
                     '          D_ORDENDECOMPRA.VLRTOTAL,    '||
                     '          D_ORDENDECOMPRA.MARCA,   '||
                     '          D_ORDENDECOMPRA.VLRIVA,     '||
                     '          D_ORDENDECOMPRA.VLRDESCUENTO,   '||
                     '          D_ORDENDECOMPRA.CUATROXMIL,    '||
                     '          D_ORDENDECOMPRA.CODIGOCUBS,    '||
                     '          D_ORDENDECOMPRA.IMPREVISTOS,    '||
                     '          D_ORDENDECOMPRA.PAGOTERCEROS, '||
                     '          ''' ||UN_USUARIO||''','||
					           '            SYSDATE ' ||
                     '  FROM D_ORDENDECOMPRA 
                       WHERE D_ORDENDECOMPRA.COMPANIA      = '''||UN_COMPANIA||'''
                         AND D_ORDENDECOMPRA.CLASEORDEN    = '''||UN_CLASEORDEN||'''
                         AND D_ORDENDECOMPRA.ORDENDECOMPRA = '''||UN_COPIARDE||''' ';
        BEGIN
            BEGIN 
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA   => MI_TABLA
                                           ,UN_ACCION  => 'IS'
                                           ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
              MI_REEMPLAZOS(1).CLAVE := 'UN_COPIARDE';
              MI_REEMPLAZOS(1).VALOR := UN_COPIARDE;
              MI_REEMPLAZOS(2).CLAVE := 'NUMERO';
              MI_REEMPLAZOS(2).VALOR := MI_CONSECUTIVO;
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_COPIARCONTRATO2
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
        END;
        RETURN 'Proceso ejecutado exitosamente.';
END FC_COPIARCONTRATO;

PROCEDURE PR_SELECCIONARREQUISICIONES
/*
      NAME              : PR_SELECCIONARREQUISICIONES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/04/2017
      TIME              : 09:00 AM
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se trae el proceso que se realiza antes de oprimir el boton seleccionar requisiciones

      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_USUARIO        => Usuario que ingreso a la aplicación



    @NAME:   seleccionarRequisiciones
    @METHOD: PUT     
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO    
)
AS  
   MI_VLRTOTAL   PCK_SUBTIPOS.TI_DOBLE;
   MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
   MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
   MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    <<RECORREORDENES>> 
    FOR MI_RS IN (SELECT   SUM(D_ORDENDESUMINISTRO.CANTIDADTT) CANTIDADTOTAL
                         , D_ORDENDESUMINISTRO.ORDENDESUMINISTRO 
                 FROM D_ORDENDESUMINISTRO 
                WHERE D_ORDENDESUMINISTRO.COMPANIA = UN_COMPANIA
                GROUP BY   D_ORDENDESUMINISTRO.ORDENDESUMINISTRO
                         , D_ORDENDESUMINISTRO.COMPANIA)
    LOOP
        MI_VLRTOTAL:= NVL(MI_RS.CANTIDADTOTAL,0);
        MI_TABLA:= 'ORDENDESUMINISTRO';
        IF MI_VLRTOTAL > 0 THEN 
            MI_CAMPOS:=' VACIA = 0
                        ,DATE_MODIFIED = SYSDATE
                        ,MODIFIED_BY   = '''||UN_USUARIO||'''  ';
            MI_CONDICION:= '     COMPANIA = '''||UN_COMPANIA||'''
                             AND NUMERO   = '||MI_RS.ORDENDESUMINISTRO;
         ELSE
             MI_TABLA:='ORDENDESUMINISTRO';
             MI_CAMPOS:=' VACIA         = -1
                         ,DATE_MODIFIED = SYSDATE
                         ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
             MI_CONDICION:='    COMPANIA = '''||UN_COMPANIA||'''
                            AND NUMERO   = '||MI_RS.ORDENDESUMINISTRO;
         END IF;
         BEGIN 
             BEGIN 
                 PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION );
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='ORDENDESUMINISTRO';
                MI_REEMPLAZOS(0).VALOR:=MI_RS.ORDENDESUMINISTRO;
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
             END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_SELECCIONREQUI1
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );  
        END;
    END LOOP RECORREORDENES;
END PR_SELECCIONARREQUISICIONES;


PROCEDURE PR_INSERTAPPTO
/*
      NAME              : PR_INSERTAPPTO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/04/2017
      TIME              : 10:25 AM
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se trae el proceso que se realiza cuando se confirma la seleccion de los rubros.

      PARAMETERS        : UN_COMPANIA      =>  Compañia de ingreso a la aplicación    
                          UN_CLASEORDEN    =>  Claseorden del formulario
                          UN_NUMERO        =>  Numero de orden del formulario
                          UN_CLASEDISP     =>  Clase de disponibilidad del formulario
                          UN_NUMERODISPSEL =>  numero de disponibilidad  o rubro seleccionado
                          UN_FECHASELEC    =>  Fecha del rubro seleccionado
                          UN_TERCERO       =>  Tercero del formulario
                          UN_SUCURSAL      =>  Sucursal del formulario
                          UN_USUARIO       =>  Usuario que ingreso a la aplicación
    @NAME:   insertaPpto
    @METHOD: POST     
    */
(
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN    IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_NUMERO        IN ORDENDECOMPRA.NUMERO%TYPE
    ,UN_CLASEDISP     IN ORDENDECOMPRAPPTO.TIPOPPTO%TYPE 
    ,UN_NUMERODISPSEL IN ORDENDECOMPRAPPTO.NUMEROPPTO%TYPE   
    ,UN_FECHASELEC    IN VARCHAR2
    ,UN_TERCERO       IN PCK_SUBTIPOS.TI_TERCERO 
    ,UN_SUCURSAL      IN PCK_SUBTIPOS.TI_SUCURSAL  
    ,UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO

)
AS 
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NUMERO     ORDENDECOMPRA.NUMERO%TYPE;
    MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
BEGIN   
    BEGIN 
      -- amonroy (17/11/2018) Se adiciona DISTINCT en la consulta 
      MI_STRSQL := 'SELECT DISTINCT NUMERO 
                    FROM ORDENDECOMPRAPPTO
                    WHERE COMPANIA    = '''||UN_COMPANIA||'''
                      AND CLASEORDEN  = '''||UN_CLASEORDEN||'''
                      AND NUMERO      =   '||UN_NUMERO||'
                      AND TIPOPPTO    = '''||UN_CLASEDISP||'''
                      AND NUMEROPPTO  =   '||UN_NUMERODISPSEL||'
                      AND TERCERO     = '''||UN_TERCERO||''' 
                      AND SUCURSAL    = '''||UN_SUCURSAL||'''';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_TABLA:='ORDENDECOMPRAPPTO';
    <<RECORRECPTES>>
    FOR MI_RS IN (
         SELECT DISTINCT COMPROBANTE_PPTAL.DESCRIPCION
                ,DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                ,DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                ,DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                ,DETALLE_COMPROBANTE_PPTAL.CUENTA RUBRO
                ,COMPROBANTE_PPTAL.TIPO TIPOPPTO
                ,DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                ,DETALLE_COMPROBANTE_PPTAL.ANO ANOPPTO
           FROM COMPROBANTE_PPTAL 
               INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                   ON  COMPROBANTE_PPTAL.NUMERO   = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE 
                   AND COMPROBANTE_PPTAL.TIPO     = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE 
                   AND COMPROBANTE_PPTAL.ANO      = DETALLE_COMPROBANTE_PPTAL.ANO 
                   AND COMPROBANTE_PPTAL.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
           WHERE COMPROBANTE_PPTAL.COMPANIA  = UN_COMPANIA 
             AND COMPROBANTE_PPTAL.TIPO      = UN_CLASEDISP 
             AND COMPROBANTE_PPTAL.NUMERO    = UN_NUMERODISPSEL )
    LOOP  
        MI_CAMPOS:= ' COMPANIA
                     ,CLASEORDEN
                     ,NUMERO
                     ,ANOPPTO
                     ,TIPOPPTO
                     ,NUMEROPPTO
                     ,RUBRO
                     ,CENTRO_COSTO
                     ,TERCERO
                     ,SUCURSAL
                     ,AUXILIAR
                     ,REFERENCIA
                     ,FUENTE_RECURSO
                     ,FECHA 
                     ,DATE_CREATED
                     ,CREATED_BY';
        MI_VALORES:= ''''||UN_COMPANIA||'''
                     ,'''||UN_CLASEORDEN||'''
                     ,'''||UN_NUMERO||'''
                     ,'||MI_RS.ANOPPTO||'
                     ,'''||MI_RS.TIPOPPTO||'''
                     ,'||UN_NUMERODISPSEL||'
                     ,'''||MI_RS.RUBRO||'''
                     ,'''||MI_RS.CENTRO_COSTO||'''
                     ,'''||UN_TERCERO||'''
                     ,'''||UN_SUCURSAL||'''
                     ,'''||MI_RS.AUXILIAR||'''
                     ,'''||MI_RS.REFERENCIA||'''
                     ,'''||MI_RS.FUENTE_RECURSO||'''
                     ,TO_DATE('''||UN_FECHASELEC||''',''DD/MM/YYYY'')
                     ,SYSDATE
                     ,'''||UN_USUARIO||'''';
        BEGIN 
            BEGIN 
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA => MI_TABLA
                                           ,UN_ACCION => 'I'
                                           ,UN_CAMPOS => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_NUMERODISPSEL';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERODISPSEL;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUMERO';
                MI_REEMPLAZOS(1).VALOR:=UN_NUMERO;
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_INSERTAPPTOCONT
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
        END;
    END LOOP RECORRECPTES; 
    END;
END PR_INSERTAPPTO; 

FUNCTION FC_ACTUALIZAIVADETALLE
/*
      NAME              : PR_ACTUALIZAIVADETALLE
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/04/2017
      TIME              : 12:30 M
      SOURCE MODULE     : sysmanTT2015.07.02
      DESCRIPTION       :
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     : Se trae el proceso que se realiza cuando se confirma la actualizacion del valor del iva.
      PARAMETERS        : UN_COMPANIA        =>  Compañia de ingreso a la aplicación
                          UN_CLASEORDEN      =>  Claseorden del formulario
                          UN_NUMERO          =>  Numero de orden del formulario
                          UN_PORCIVAGLOBAL   => Valor del parametro enviado del formulario
                          UN_ROUNDVALORIVAOC => Valor del parametro enviado del formulario
                          UN_ROUNDVLRTOTALOC => Valor del parametro enviado del formulario
                          UN_ROUNDVALORUNIOC => Valor del parametro enviado del formulario
                          UN_DIGREDOVLUNIIVA => Valor del parametro enviado del formulario
                          UN_DIGROUNDVLRIVA  => Valor del parametro enviado del formulario
                          UN_DIGREDONTOTAL   => Valor del parametro enviado del formulario
                          UN_USUARIO         => Usuario que ingreso a la aplicación
    --NAME:   actualizaIvaDetalle
    --METHOD: POST
    */
(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN      IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_NUMERO          IN ORDENDECOMPRA.NUMERO%TYPE
    ,UN_PORCIVAGLOBAL   IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_ROUNDVALORIVAOC IN VARCHAR2
    ,UN_ROUNDVLRTOTALOC IN VARCHAR2
    ,UN_ROUNDVALORUNIOC IN VARCHAR2
    ,UN_DIGREDOVLUNIIVA IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_DIGROUNDVLRIVA  IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_DIGREDONTOTAL   IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORUNITARIODI PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORUNITARIO   PCK_SUBTIPOS.TI_DOBLE;
    MI_CANTIDAD        PCK_SUBTIPOS.TI_DOBLE;
    MI_IVA             PCK_SUBTIPOS.TI_DOBLE;
    MI_PORCIVAGLOBAL   PCK_SUBTIPOS.TI_DOBLE;
    MI_PORCDESC        PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL           PCK_SUBTIPOS.TI_DOBLE;
    MI_DESCUENTO       PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRTOTAL        PCK_SUBTIPOS.TI_DOBLE;
    MI_RTA             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_PRECIOS_UNITARIOS  ORDENDECOMPRA.PRECIOS_UNITARIOS%TYPE;
    MI_PORC_ADMIN         ORDENDECOMPRA.PORADMINISTRACION%TYPE;
    MI_PORC_IMPRE         ORDENDECOMPRA.PORINPREVISTOS%TYPE;
    MI_PORC_UTILI         ORDENDECOMPRA.PORUTILIDADES%TYPE;
    MI_PARAM_AIU          VARCHAR2(10);
    MI_APLICA_AIU         BOOLEAN := FALSE;
BEGIN
    MI_TABLA:= 'D_ORDENDECOMPRA';
    --(CC:3656_CFBARRERA_INI:Ajuste para calcular el valor AUI cuando se cambia el valor IVA_GLOBAL)
    BEGIN
        SELECT OC.PRECIOS_UNITARIOS
              ,OC.PORADMINISTRACION
              ,OC.PORINPREVISTOS
              ,OC.PORUTILIDADES
        INTO  MI_PRECIOS_UNITARIOS
             ,MI_PORC_ADMIN
             ,MI_PORC_IMPRE
             ,MI_PORC_UTILI
        FROM  ORDENDECOMPRA OC
        WHERE OC.COMPANIA   = UN_COMPANIA
          AND OC.CLASEORDEN = UN_CLASEORDEN
          AND OC.NUMERO     = UN_NUMERO;
          
        MI_PARAM_AIU := PCK_SYSMAN_UTL.FC_PAR(
                            UN_COMPANIA  => UN_COMPANIA
                           ,UN_NOMBRE    => 'APLICAR AIU EN ENTRADAS DE ALMACEN'
                           ,UN_MODULO    => -1
                           ,UN_FECHA_PAR => SYSDATE
                           ,UN_IND_MAYUS => -1
                        );
                        
        IF MI_PRECIOS_UNITARIOS <> 0 AND MI_PARAM_AIU = 'SI' THEN
            MI_APLICA_AIU := TRUE;
        END IF;
        
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_APLICA_AIU := FALSE;
    END;--(CC:3656_CFBARRERA_FIN)

    IF UN_PORCIVAGLOBAL NOT IN (0) THEN
        <<RECORREDETALLE>>
        FOR MI_RS IN (SELECT D_ORDENDECOMPRA.CODIGO,
                             D_ORDENDECOMPRA.VALORUNITARIODI
                            ,D_ORDENDECOMPRA.VALORUNITARIO
                            ,D_ORDENDECOMPRA.CANTIDAD
                            ,D_ORDENDECOMPRA.PORCDESC
                            ,D_ORDENDECOMPRA.VLRTOTAL
                      FROM D_ORDENDECOMPRA
                     WHERE D_ORDENDECOMPRA.COMPANIA         = UN_COMPANIA
                       AND D_ORDENDECOMPRA.CLASEORDEN       = UN_CLASEORDEN
                       AND D_ORDENDECOMPRA.ORDENDECOMPRA    = UN_NUMERO)
        LOOP
            MI_VALORUNITARIO:= NVL(MI_RS.VALORUNITARIO,0);
            MI_CANTIDAD:= NVL(MI_RS.CANTIDAD,0);
            MI_PORCDESC:= NVL(MI_RS.PORCDESC,0);
            MI_TOTAL:= MI_RS.VALORUNITARIO * MI_CANTIDAD;
            MI_DESCUENTO:= PCK_SYSMAN_UTL.FC_ROUND((MI_TOTAL * MI_RS.PORCDESC) / 100, 2);
            IF 'NO' = UN_ROUNDVALORIVAOC THEN
                MI_IVA:= (PCK_SYSMAN_UTL.FC_ROUND(
                                         (MI_TOTAL - MI_DESCUENTO)
                                            * (1 + (UN_PORCIVAGLOBAL / 100))
                                         ,UN_DIGROUNDVLRIVA)
                          - MI_TOTAL)
                         + MI_DESCUENTO;
            ELSE
                MI_IVA:= PCK_SYSMAN_UTL.FC_ROUND(
                                        MI_RS.VALORUNITARIO
                                        * (UN_PORCIVAGLOBAL / 100)
                                       ,UN_DIGROUNDVLRIVA)
                        * MI_CANTIDAD;
            END IF;
            MI_TOTAL:= MI_TOTAL - MI_DESCUENTO + MI_IVA;
            IF MI_CANTIDAD > 0 THEN
                MI_VALORUNITARIODI:= CASE WHEN 'SI' = UN_ROUNDVALORUNIOC
                                          THEN PCK_SYSMAN_UTL.FC_ROUND(
                                                              MI_TOTAL/MI_CANTIDAD
                                                             ,UN_DIGREDOVLUNIIVA)
                                          ELSE PCK_SYSMAN_UTL.FC_ROUND(
                                                              MI_TOTAL/MI_CANTIDAD
                                                             ,2)
                                     END;
                MI_VLRTOTAL:= CASE WHEN 'SI' = UN_ROUNDVLRTOTALOC
                                   THEN PCK_SYSMAN_UTL.FC_ROUND(
                                                       MI_VALORUNITARIODI * MI_CANTIDAD
                                                      ,UN_DIGREDONTOTAL)
                                   ELSE PCK_SYSMAN_UTL.FC_ROUND(
                                                       MI_VALORUNITARIODI * MI_CANTIDAD
                                                      ,2)
                              END;
            ELSE
                MI_VALORUNITARIODI:= 0;
                MI_VLRTOTAL:= 0;
            END IF;
            
            MI_CAMPOS:= ' VALORUNITARIODI = '||MI_VALORUNITARIODI||
                        ',PORCDESC        = '||MI_PORCDESC||
                        ',PORCIVA         = '||UN_PORCIVAGLOBAL||
                        ',VLRTOTAL        = '||MI_VLRTOTAL||
                        ',VLRIVA          = '||MI_IVA||
                        ',MODIFIED_BY     = '''||UN_USUARIO||'''
                         ,DATE_MODIFIED   = SYSDATE';
            MI_CONDICION:= '    COMPANIA      = '''||UN_COMPANIA||'''
                            AND CLASEORDEN    = '''||UN_CLASEORDEN||'''
                            AND ORDENDECOMPRA = '''||UN_NUMERO||'''
                            AND CODIGO        = '''||MI_RS.CODIGO||'''';
            BEGIN
                BEGIN
                    MI_RTA:= PCK_DATOS.FC_ACME(
                                                 UN_TABLA     => MI_TABLA
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPOS
                                                ,UN_CONDICION => MI_CONDICION  );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='UN_NUMERO';
                    MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ACTUALIZAIVADET
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;
       END LOOP RECORREDETALLE;
       --(CC:3656_CFBARRERA_INI)
       IF MI_APLICA_AIU THEN
            PCK_CONTRATOS_COM2.PR_RECALCULAR_AIU(
                UN_COMPANIA   => UN_COMPANIA
               ,UN_CLASEORDEN => UN_CLASEORDEN
               ,UN_NUMERO     => UN_NUMERO
               ,UN_PORC_ADMIN => MI_PORC_ADMIN
               ,UN_PORC_IMPRE => MI_PORC_IMPRE
               ,UN_PORC_UTILI => MI_PORC_UTILI
            );
        END IF;
       --(CC:3656_CFBARRERA_FIN)
       
    END IF;
    IF MI_RTA <> 0 THEN
      RETURN -1;
    ELSE
      RETURN 0;
    END IF;
END FC_ACTUALIZAIVADETALLE; 

FUNCTION FC_CALCULARTOTALPAGOS
/*
      NAME              : FC_CALCULARTOTALPAGOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/04/2017
      TIME              : 12:30 M
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se trae el proceso que se realiza en el formulario a PLSQ.L

      PARAMETERS        : UN_COMPANIA        =>  Compañia de ingreso a la aplicación    
                          UN_CLASEORDEN      =>  Claseorden del formulario
                          UN_NUMERO          =>  Numero de orden del formulario
                          UN_CLASECONTABLE   =>  valor del parametro CLASE CONTABLE MANTENIMIENTO CONTRATOS PAGOS
    @NAME:   calculartotalpagos
    @METHOD: GET     
    */
(
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN    IN ORDENDECOMPRA.CLASEORDEN%TYPE 
    ,UN_NUMERO        IN ORDENDECOMPRA.NUMERO%TYPE
    ,UN_CLASECONTABLE IN TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE 

)RETURN NUMBER
AS
    MI_CONDICIONEGR  PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICIONCHA  PCK_SUBTIPOS.TI_CONDICION;
    MI_GROUPBY       PCK_SUBTIPOS.TI_CONDICION; 
    MI_RTA           PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMAEGR       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMACHA       PCK_SUBTIPOS.TI_DOBLE;
    MI_SQL           PCK_SUBTIPOS.TI_STRSQL;
    MI_STREGR        PCK_SUBTIPOS.TI_STRSQL; 
    MI_STRCHA         PCK_SUBTIPOS.TI_STRSQL; 
BEGIN
    IF 'E' = UN_CLASECONTABLE THEN 
        MI_CONDICIONEGR:= ' AND TIPO_COMPROBANTE.CLASE_CONTABLE = ''E'' ';
        MI_CONDICIONCHA:= ' AND COMPROBANTE_CNT.TIPO            =''CHA'' ';
    ELSE
        MI_CONDICIONEGR:= ' AND TIPO_COMPROBANTE.CLASE_CONTABLE =''P'' 
                            AND COMPROBANTE_CNT.IND_ANTICIPO    = 0';
        MI_CONDICIONCHA:= ' AND TIPO_COMPROBANTE.CLASE_CONTABLE = ''T'' ';
    END IF;
    MI_SQL:= 'SELECT COMPROBANTE_CNT.COMPANIA,
                     COMPROBANTE_CNT.ANO,
                     COMPROBANTE_CNT.TIPO,
                     SUM(COMPROBANTE_CNT.VLR_DOCUMENTO) SUMADEVLR_DOCUMENTO, 
                     COMPROBANTE_CNT.TIPOCONTRATO, 
                     COMPROBANTE_CNT.NUMEROCONTRATO 
                FROM COMPROBANTE_CNT 
                    INNER JOIN TIPO_COMPROBANTE 
                       ON  COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA 
                       AND COMPROBANTE_CNT.TIPO     = TIPO_COMPROBANTE.CODIGO
               WHERE COMPROBANTE_CNT.COMPANIA        ='''||UN_COMPANIA||'''
                 AND COMPROBANTE_CNT.TIPOCONTRATO    ='''||UN_CLASEORDEN||'''
                 AND COMPROBANTE_CNT.NUMEROCONTRATO  ='||UN_NUMERO;
    MI_GROUPBY:= 'GROUP BY COMPROBANTE_CNT.COMPANIA,
                           COMPROBANTE_CNT.ANO, 
                           COMPROBANTE_CNT.TIPO, 
                           COMPROBANTE_CNT.TIPOCONTRATO, 
                           COMPROBANTE_CNT.NUMEROCONTRATO';
-- EGRESOS
    MI_STREGR:='SELECT NVL(SUM(SUMADEVLR_DOCUMENTO),0) FROM ( '||MI_SQL||MI_CONDICIONEGR||MI_GROUPBY||' )';
    BEGIN   
        EXECUTE IMMEDIATE  MI_STREGR INTO MI_SUMAEGR; 
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_SUMAEGR:=0; 
    END;

-- CHA
    MI_STRCHA:='SELECT NVL(SUM(SUMADEVLR_DOCUMENTO),0)  FROM ('||MI_SQL||MI_CONDICIONCHA||MI_GROUPBY||' ) ';
    BEGIN
        EXECUTE IMMEDIATE MI_STRCHA INTO MI_SUMACHA; 
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_SUMACHA:=0; 
    END;
    MI_RTA:= MI_SUMAEGR - MI_SUMACHA;
RETURN MI_RTA;
END FC_CALCULARTOTALPAGOS;

FUNCTION FC_ELIMINARORDENDECOMPRA
/*
      NAME              : FC_ELIMINARORDENDECOMPRA 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 25/04/2017
      TIME              : 10:20 M
      SOURCE MODULE     : sysmanTT2015.07.02 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se trae el proceso que se realiza en el formulario en el evento de eliminar antes a PLSQL

      PARAMETERS        : UN_COMPANIA        =>  Compañia de ingreso a la aplicación    
                          UN_CLASEORDEN      =>  Claseorden del formulario
                          UN_NUMERO          =>  Numero de orden del formulario
    @NAME:   eliminarOrdendeCompra
    @METHOD: GET     
    */
(
     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN IN ORDENDECOMPRA.CLASEORDEN%TYPE
    ,UN_NUMERO     IN ORDENDECOMPRA.NUMERO%TYPE 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS  
    MI_EXISTE       VARCHAR2(1 CHAR);
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_AFECTACIONES PCK_SUBTIPOS.TI_STRSQL;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    BEGIN
        SELECT DISTINCT 'X'  
          INTO MI_EXISTE 
          FROM D_ORDENDECOMPRA
         WHERE  COMPANIA     = UN_COMPANIA 
           AND CLASEORDEN    = UN_CLASEORDEN
           AND ORDENDECOMPRA = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN
            MI_REEMPLAZOS(0).CLAVE:='NUMERO';
            MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'D_ORDENDECOMPRA'
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN1
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                   );
        END;
    END IF;
    --Si esta afectado por adiciones
    BEGIN
        FOR MI_RS IN ( 
            SELECT CLASEORDEN,   
                   NUMERO  
              FROM ORDENDECOMPRA 
             WHERE COMPANIA       =UN_COMPANIA 
               AND TIPOAFECTADO   =UN_CLASEORDEN 
               AND NUMEROAFECTADO =UN_NUMERO)
        LOOP
            MI_AFECTACIONES:= ','||MI_RS.CLASEORDEN||' - '||MI_RS.NUMERO;
        END LOOP;
        IF MI_AFECTACIONES IS NOT NULL THEN 
            BEGIN 
                MI_REEMPLAZOS(0).CLAVE:='MI_AFECTACIONES';
                MI_REEMPLAZOS(0).VALOR:=SUBSTR(MI_AFECTACIONES,2,LENGTH(MI_AFECTACIONES));
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_TABLAERROR => 'ORDENDECOMPRA'
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN2
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                           );
            END;
        END IF;
    END;

    BEGIN 
        SELECT DISTINCT 'X'
          INTO MI_EXISTE
          FROM ORDENDECOMPRAPPTO
         WHERE ORDENDECOMPRAPPTO.COMPANIA   =UN_COMPANIA
           AND ORDENDECOMPRAPPTO.CLASEORDEN =UN_CLASEORDEN
           AND ORDENDECOMPRAPPTO.NUMERO     =UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN  
            MI_REEMPLAZOS(0).CLAVE:='NUMERO';
            MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'ORDENDECOMPRAPPTO'
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN3
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                   );
        END;
    END IF;
    BEGIN 
        SELECT DISTINCT 'X' 
          INTO MI_EXISTE
          FROM PAGO_ESTAMPILLAS     
         WHERE COMPANIA   = UN_COMPANIA 
           AND CLASEORDEN = UN_CLASEORDEN 
           AND NUMERO     = UN_NUMERO; 
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        MI_TABLA:='PAGO_ESTAMPILLAS';
        MI_CONDICION:='     COMPANIA      ='''||UN_COMPANIA||''' 
                        AND CLASEORDEN    ='''||UN_CLASEORDEN||'''
                        AND ORDENDECOMPRA ='||UN_NUMERO;
        BEGIN 
            BEGIN 
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA => MI_TABLA
                                           ,UN_ACCION => 'E'
                                           ,UN_CONDICION => MI_CONDICION
                                           );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='NUMERO';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN4
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                   ); 
        END;
    END IF;
    BEGIN 
        SELECT DISTINCT 'X' 
          INTO MI_EXISTE
          FROM PAGOPROGRAMADOHISTORICOS     
         WHERE COMPANIA      =UN_COMPANIA 
           AND CLASEORDEN    =UN_CLASEORDEN 
           AND ORDENDECOMPRA =UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN
            MI_REEMPLAZOS(0).CLAVE:='NUMERO';
            MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN6
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );     
        END;
    END IF;

    BEGIN 
        SELECT DISTINCT 'X' 
          INTO MI_EXISTE
          FROM PAGOPROGRAMADO     
         WHERE COMPANIA      =UN_COMPANIA 
           AND CLASEORDEN    =UN_CLASEORDEN 
           AND ORDENDECOMPRA =UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN 
            BEGIN
                MI_TABLA:='PAGOPROGRAMADO';
                MI_CONDICION:='     COMPANIA      ='''||UN_COMPANIA||''' 
                                AND CLASEORDEN    ='''||UN_CLASEORDEN||'''
                                AND ORDENDECOMPRA ='||UN_NUMERO;    
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA     => MI_TABLA
                                           ,UN_ACCION    => 'E'
                                           ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='NUMERO';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN5
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       ); 
        END;
    END IF;
    BEGIN 
        SELECT DISTINCT 'X' 
          INTO MI_EXISTE
          FROM DOCUMENTOS     
         WHERE COMPANIA      =UN_COMPANIA 
           AND CLASEORDEN    =UN_CLASEORDEN 
           AND NUMERO = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN
            MI_REEMPLAZOS(0).CLAVE:='NUMERO';
            MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;
            RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_ELIMINAR_ORDEN7
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );     
        END;
    END IF;
    RETURN -1;
END FC_ELIMINARORDENDECOMPRA;

PROCEDURE PR_ACTUALIZAR_DETACTIVIDADES 
/*
    NAME              : PR_ACTUALIZAR_DETACTIVIDADES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN LEONARDO MALAVER JIMÉNEZ
    DATE MIGRADOR     : 26/09/2018
    TIME              : 04:34 PM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite transferir los detalles 

      @NAME:    actualizarDetallesActividades
      @METHOD:  POST
  */
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_COD_ESTUDIO    IN EP_DETALLE_ACTIVIDADES.COD_ESTUDIO%TYPE,
      UN_COD_CONTRATO   IN DETALLE_ACTIVIDADES.COD_CONTRATO%TYPE,
      UN_TIPO_CONTRATO  IN DETALLE_ACTIVIDADES.TIPO_CONTRATO%TYPE,
      UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )     
  AS 
      MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
      MI_EXISTE                     PCK_SUBTIPOS.TI_ENTERO;

    BEGIN

   FOR RS IN 
    (
        SELECT  CODIGO, 
                ACTIVIDAD,                                          
                OBLIGACION_CONTRACTUAL
        FROM EP_DETALLE_ACTIVIDADES
        WHERE COMPANIA    = UN_COMPANIA
        AND   COD_ESTUDIO = UN_COD_ESTUDIO  
    )
    LOOP
      SELECT COUNT (1)
      INTO MI_EXISTE
      FROM DETALLE_ACTIVIDADES
      WHERE COMPANIA  = UN_COMPANIA
      AND CODIGO      = RS.CODIGO;

      IF MI_EXISTE       = 0 THEN

        MI_CAMPOS      :=   'COMPANIA,                                          
                            CODIGO,                                          
                            COD_CONTRATO,                                          
                            TIPO_CONTRATO,                                          
                            ACTIVIDAD,                                          
                            OBLIGACION_CONTRACTUAL,                                                                                
                            DATE_CREATED,                                           
                            CREATED_BY';

        MI_VALORES     :=''''||UN_COMPANIA||''',                                          
                            '||RS.CODIGO||',                                          
                            '||UN_COD_CONTRATO||',                                          
                            '''||UN_TIPO_CONTRATO||''',                                          
                            '''||RS.ACTIVIDAD||''',                                          
                            '''||RS.OBLIGACION_CONTRACTUAL||''',                   
                            SYSDATE,                                          
                            ''' || UN_USUARIO ||'''' ; 

      BEGIN     
       BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'DETALLE_ACTIVIDADES', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
       END;


       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE, 
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_TRANSFERIRDETACTIVIDADES
                                        );
       END;   
      END IF; 

    END LOOP; 

END PR_ACTUALIZAR_DETACTIVIDADES;

END PCK_CONTRATOS_COM1;