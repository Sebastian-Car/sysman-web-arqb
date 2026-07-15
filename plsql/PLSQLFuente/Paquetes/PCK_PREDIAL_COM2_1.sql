create or replace PACKAGE BODY "PCK_PREDIAL_COM2" AS
/**@package:  Predial**/
 FUNCTION FC_MODIFICARRECIBOS  
  /*
        NAME              : FC_MODIFICARRECIBOS -- Comando39_Click en Form_MODIFICACION_DE_PAGOS en Access
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 04/08/2016
        TIME              : 11:05 AM
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : 
        PARAMETERS        :UN_COMPANIA          => Compania de ingreso a la aplicación
                           UN_MODIFICACION      => Parametro usado para  filtrar la condicion al actualizar el campo REGISTRADO en la tabla 
                                                   IP_MODIFICACIONES_PAGOS.
                           UN_TIPORECIBO        => Es usado a su vez como paramentro al llamar la funcion FC_CAMBIARVALORES.
                           UN_TIPOMODIFICACION  => Parametro usado para validar el tipo de modificacion. Puede tomar valores como : 01,02,03,04                      
                           UN_FECHAMODIFICACION => Parametro usado para guardar la fecha de anulacion en la tabla IP_RECIBOS_DE_PAGO
                           UN_CODIGO            => Es usado a su vez como paramentro al llamar la funcion FC_CAMBIARVALORES.
                           UN_FACTURA           => Es usado a su vez como paramentro al llamar la funcion FC_CAMBIARVALORES.
                           UN_FECHA             => Si tipo modificacion en 02 la fecha sera la que entre por este parametro.
                                                   para despues ser procesada correspondientemente.
                           UN_BANCO             => Si tipo modificacion en 01 la fecha sera la que entre por este parametro.
                                                   para despues ser procesada correspondientemente.                           
                           UN_PAQUETE           => Si tipo modificacion en 03 la fecha sera la que entre por este parametro.
                                                   para despues ser procesada correspondientemente.                                                        
                           UN_VALOR             => Es usado a su vez como paramentro al llamar la funcion FC_CAMBIARVALORES. 
                           UN_ANULAR            => Parametro usado para buscar el numero de documento en la tabla IP_RECIBOS_DE_PAGO.
                           UN_ACTIVAR           =>  Es usado a su vez como paramentro al llamar la funcion FC_CAMBIARVALORES. 


        @NAME:  modificarRecibos
        @METHOD:  GET        
    */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODIFICACION         IN VARCHAR2, 
    UN_TIPORECIBO           IN VARCHAR2,
    UN_TIPOMODIFICACION     IN VARCHAR2,
    UN_FECHAMODIFICACION    IN DATE,
    UN_CODIGO               IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_FACTURA              IN PCK_SUBTIPOS.TI_DOCNUM DEFAULT NULL,
    UN_FECHA                IN DATE,
    UN_BANCO                IN IP_BANCOS.CODIGOBANCO%TYPE,
    UN_PAQUETE              IN VARCHAR2,
    UN_VALOR                IN PCK_SUBTIPOS.TI_DOBLE,
    UN_ANULAR               IN PCK_SUBTIPOS.TI_DOCNUM DEFAULT NULL,
    UN_ACTIVAR              IN PCK_SUBTIPOS.TI_DOCNUM DEFAULT NULL,
    UN_CAMPOANTERIOR        IN VARCHAR2,
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
  )RETURN VARCHAR2
  AS

    MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RTACV                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CANT                 PCK_SUBTIPOS.TI_DOBLE;    
  	MI_BANCO     	          IP_BANCOS.CODIGOBANCO%TYPE;
    MI_FECHA                DATE;
    MI_PAQUETE              VARCHAR2(5 CHAR);
    MI_VALORANT             VARCHAR2(250 CHAR);
    MI_PCKDATOS             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION; 
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 

    -- cargarrecords01 en Access
	 <<PAGO_BANCOSDET>>
    FOR RS_BANCOSDET IN (SELECT 
                                PRECOD, 
                                NUMFACTURA,
                                PREVAL, 
                                PAQUETE,
                                PREFEC,
                                PAG_BAN 
                           FROM 
                                IP_PAGO_BANCOSDET 
                          WHERE 
                                COMPANIA   = UN_COMPANIA 
                            AND PRECOD     = UN_CODIGO   
                            AND NUMFACTURA = UN_FACTURA)
      LOOP
          IF UN_TIPOMODIFICACION = '01' OR UN_TIPOMODIFICACION = '02' OR UN_TIPOMODIFICACION = '03' OR UN_TIPOMODIFICACION = '04' THEN
             <<PAGO_BANCOSCAB>>
             FOR RS_BANCOSCAB IN (SELECT 
                                         PREFEC,
                                         PAQUETE,
                                         PAG_BAN,
                                         NROCUPONESACU,
                                         ACUMULADO 
                                    FROM 
                                         IP_PAGO_BANCOSCAB 
                                   WHERE 
                                         COMPANIA = UN_COMPANIA 
                                     AND PREFEC   = RS_BANCOSDET.PREFEC
                                     AND PAQUETE  = RS_BANCOSDET.PAQUETE
                                     AND PAG_BAN  = RS_BANCOSDET.PAG_BAN) 
               LOOP

                  PR_ARREGLARCUPONES(UN_COMPANIA => UN_COMPANIA,
                                     UN_FECHA    => RS_BANCOSDET.PREFEC, 
                                     UN_BANCO    => RS_BANCOSDET.PAG_BAN,
                                     UN_PAQUETE  => RS_BANCOSDET.PAQUETE,
                                     UN_USUARIO  => UN_USUARIO);  

                BEGIN
                  BEGIN
                    MI_CAMPOS        := ' NROCUPONESACU = (' || RS_BANCOSCAB.NROCUPONESACU || ' - 1)' || ', 
                                          ACUMULADO     = (' || RS_BANCOSCAB.ACUMULADO || ' - ' || RS_BANCOSDET.PREVAL ||'),
                                          DATE_MODIFIED = SYSDATE,
                                          MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION     := '      COMPANIA = ''' || UN_COMPANIA || ''' 
                                           AND PREFEC   = ''' || RS_BANCOSDET.PREFEC || '''   
                                           AND PAQUETE  = ''' || RS_BANCOSDET.PAQUETE || ''' 
                                           AND PAG_BAN  = ''' || RS_BANCOSDET.PAG_BAN || '''';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGO_BANCOSCAB',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'NROCUPONESACU';
                     MI_MSGERROR(1).VALOR := RS_BANCOSCAB.NROCUPONESACU;
                     MI_MSGERROR(2).CLAVE := 'ACUMULADO';
                     MI_MSGERROR(2).VALOR := RS_BANCOSCAB.ACUMULADO || ' - ' || RS_BANCOSDET.PREVAL;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CUPO_ACU,
                                             UN_REEMPLAZOS => MI_MSGERROR
                        );
                END;

               END LOOP PAGO_BANCOSCAB;

             IF UN_TIPOMODIFICACION = '01' THEN
                 MI_BANCO    := UN_BANCO;
                 MI_FECHA    := RS_BANCOSDET.PREFEC;
                 MI_PAQUETE  := RS_BANCOSDET.PAQUETE;
                 MI_VALORANT := RS_BANCOSDET.PAG_BAN;
             ELSIF UN_TIPOMODIFICACION = '02' THEN
                 MI_BANCO    := RS_BANCOSDET.PAG_BAN;
                 MI_FECHA    := UN_FECHA;
                 MI_PAQUETE  := RS_BANCOSDET.PAQUETE;
                 MI_VALORANT := RS_BANCOSDET.PREFEC;
             ELSIF UN_TIPOMODIFICACION = '03' THEN
                 MI_BANCO    := RS_BANCOSDET.PAG_BAN;
                 MI_FECHA    := RS_BANCOSDET.PREFEC;
                 MI_PAQUETE  := UN_PAQUETE;
                 MI_VALORANT := RS_BANCOSDET.PAQUETE;
             ELSIF UN_TIPOMODIFICACION = '04' THEN
                 MI_VALORANT := RS_BANCOSDET.PREVAL;

                 MI_RTACV    := FC_CAMBIARVALORES(UN_COMPANIA         => UN_COMPANIA,
                                                  UN_TIPORECIBO       => UN_TIPORECIBO,
                                                  UN_TIPOMODIFICACION => UN_TIPOMODIFICACION,
                                                  UN_BANCO            => MI_BANCO,
                                                  UN_CODIGO           => UN_CODIGO,
                                                  UN_FACTURA          => UN_FACTURA,
                                                  UN_FECHA            => UN_FECHA, 
                                                  UN_VALOR            => UN_VALOR, 
                                                  UN_PAQUETE          => UN_PAQUETE,
                                                  UN_PREFEC           => RS_BANCOSDET.PREFEC, 
                                                  UN_PAG_BAN          => RS_BANCOSDET.PAG_BAN,
                                                  UN_PAQUETERS        => RS_BANCOSDET.PAQUETE,
                                                  UN_ACTIVAR          => UN_ACTIVAR,
                                                  UN_ACUERDO          => NULL,
                                                  UN_NC_ACUERDO       => NULL,
                                                  UN_USUARIO          => UN_USUARIO ); 
                 IF MI_RTACV <> '0' THEN
                   BEGIN 
                     BEGIN   
                       MI_CAMPOS    := UN_CAMPOANTERIOR || ' = ''' || MI_VALORANT || ''',
                                       REGISTRADO            = -1,
                                       DATE_MODIFIED = SYSDATE,
                                       MODIFIED_BY   = '''||UN_USUARIO||''' ' ;

                       MI_CONDICION :='   COMPANIA           = ''' || UN_COMPANIA || '''
                                      AND MODIFICACION       = ''' || UN_MODIFICACION || '''   
                                      AND TIPO_MODIFICACION  = ''' || UN_TIPOMODIFICACION || '''';

                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_MODIFICACIONES_PAGOS',
                                                              UN_ACCION    => 'M',
                                                              UN_CAMPOS    => MI_CAMPOS,
                                                              UN_CONDICION => MI_CONDICION);

                       MI_MSGERROR(1).CLAVE := 'CAMPO';
                       MI_MSGERROR(1).VALOR := UN_CAMPOANTERIOR;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;

                    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_MODIFI_PAG,
                                               UN_REEMPLAZOS => MI_MSGERROR

                          );                                       

                   END;

                 END IF;
                 RETURN MI_RTACV;
             END IF;

             BEGIN

              SELECT
                     COUNT(PAQUETE) PAQUETE 
                INTO 
                     MI_CANT
                FROM 
                     IP_PAGO_BANCOSCAB 
               WHERE 
                     COMPANIA        = UN_COMPANIA 
                 AND TRUNC(PREFEC)   = TRUNC(MI_FECHA)
                 AND PAQUETE         = MI_PAQUETE 
                 AND PAG_BAN         = MI_BANCO;

              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  MI_CANT := 0; 
             END;

             IF MI_CANT IN (0) THEN                                       

                BEGIN
                  BEGIN
                    MI_CAMPOS  := ' COMPANIA
                                   ,PREFEC 
                                   ,PAQUETE 
                                   ,PAG_BAN
                                   ,NROCUPONESACU
                                   ,ACUMULADO
                                   ,CREATED_BY
                                   ,DATE_CREATED';

                   MI_VALORES  := '''' || UN_COMPANIA || '''' || ',
                                  ' || 'TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY') || ''',
                                  ''DD/MM/YYYY HH24:mi:ss'')' || ',
                                  ' || '''' || MI_PAQUETE || '''' || ', 
                                  ' || '''' || MI_BANCO || '''' || ',
                                  1,
                                  ' || RS_BANCOSDET.PREVAL ||',
                                  '''||UN_USUARIO||''',
                                  SYSDATE ';

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_PAGO_BANCOSCAB',
                                                          UN_ACCION=>'I',
                                                          UN_CAMPOS=>MI_CAMPOS,
                                                          UN_VALORES=>MI_VALORES);                

                     MI_MSGERROR(1).CLAVE := 'PREFEC';
                     MI_MSGERROR(1).VALOR := TO_CHAR(MI_FECHA, 'DD/MM/YYYY');

                     MI_MSGERROR(2).CLAVE := 'PAQUETE';
                     MI_MSGERROR(2).VALOR := MI_PAQUETE; 

                     MI_MSGERROR(3).CLAVE := 'PAG_BAN';
                     MI_MSGERROR(3).VALOR := MI_BANCO;

                     MI_MSGERROR(4).CLAVE := 'ACUMULADO';
                     MI_MSGERROR(4).VALOR := RS_BANCOSDET.PREVAL; 

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;



                 EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_PAG_BAN,
                                            UN_REEMPLAZOS => MI_MSGERROR);    

                END;

               MI_RTACV := FC_CAMBIARVALORES(UN_COMPANIA         => UN_COMPANIA,
                                             UN_TIPORECIBO       => UN_TIPORECIBO,
                                             UN_TIPOMODIFICACION => UN_TIPOMODIFICACION,
                                             UN_BANCO            => MI_BANCO,
                                             UN_CODIGO           => UN_CODIGO,
                                             UN_FACTURA          => UN_FACTURA,
                                             UN_FECHA            => UN_FECHA, 
                                             UN_VALOR            => UN_VALOR, 
                                             UN_PAQUETE          => UN_PAQUETE,
                                             UN_PREFEC           => RS_BANCOSDET.PREFEC, 
                                             UN_PAG_BAN          => RS_BANCOSDET.PAG_BAN,
                                             UN_PAQUETERS        => RS_BANCOSDET.PAQUETE,
                                             UN_ACTIVAR          => UN_ACTIVAR,
                                             UN_ACUERDO          => NULL,
                                             UN_NC_ACUERDO       => NULL,
                                             UN_USUARIO          => UN_USUARIO );

             ELSE
                 PR_ARREGLARCUPONES(UN_COMPANIA => UN_COMPANIA, 
                                    UN_FECHA    => MI_FECHA, 
                                    UN_BANCO    => MI_BANCO, 
                                    UN_PAQUETE  => MI_PAQUETE,
                                    UN_USUARIO  => UN_USUARIO);

				 <<PAGO_BANCOSCAB1>>
                 FOR RS_BANCOSCAB1 IN (SELECT 
                                              PREFEC, 
                                              PAQUETE, 
                                              PAG_BAN,
                                              NROCUPONESACU,
                                              ACUMULADO
                                         FROM 
                                              IP_PAGO_BANCOSCAB 
                                        WHERE 
                                              PREFEC  = MI_FECHA
                                          AND PAQUETE = MI_PAQUETE 
                                          AND PAG_BAN = MI_BANCO) 
                     LOOP
                        BEGIN
                          BEGIN        
                            MI_CAMPOS        := ' NROCUPONESACU = (' || RS_BANCOSCAB1.NROCUPONESACU || '+ 1)' || ', 
                                                  ACUMULADO     = (' || RS_BANCOSCAB1.ACUMULADO || ' - ' || RS_BANCOSDET.PREVAL ||'),
                                                  DATE_MODIFIED = SYSDATE,
                                                  MODIFIED_BY   = '''||UN_USUARIO||''' ';

                            MI_CONDICION     := '     COMPANIA  = ''' || UN_COMPANIA ||''' 
                                                  AND PREFEC    = ''' || MI_FECHA || '''
                                                  AND PAQUETE   = ''' || MI_PAQUETE ||'''
                                                  AND PAG_BAN   = ''' || MI_BANCO || '''';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGO_BANCOSCAB',
                                                                   UN_ACCION    => 'M',
                                                                   UN_CAMPOS    => MI_CAMPOS,
                                                                   UN_CONDICION => MI_CONDICION);

                             MI_MSGERROR(1).CLAVE := 'NROCUPONESACU';
                             MI_MSGERROR(1).VALOR := RS_BANCOSCAB1.NROCUPONESACU;
                             MI_MSGERROR(2).CLAVE := 'ACUMULADO';
                             MI_MSGERROR(2).VALOR := RS_BANCOSCAB1.ACUMULADO || ' - ' || RS_BANCOSDET.PREVAL;

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                            END;          

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CUPO_ACU,
                                                     UN_REEMPLAZOS => MI_MSGERROR);    

                        END;
                     END LOOP PAGO_BANCOSCAB1;

                     MI_RTACV := FC_CAMBIARVALORES( UN_COMPANIA         => UN_COMPANIA,
                                                    UN_TIPORECIBO       => UN_TIPORECIBO,
                                                    UN_TIPOMODIFICACION => UN_TIPOMODIFICACION,
                                                    UN_BANCO            => MI_BANCO,
                                                    UN_CODIGO           => UN_CODIGO,
                                                    UN_FACTURA          => UN_FACTURA,
                                                    UN_FECHA            => UN_FECHA, 
                                                    UN_VALOR            => UN_VALOR, 
                                                    UN_PAQUETE          => UN_PAQUETE,
                                                    UN_PREFEC           => RS_BANCOSDET.PREFEC, 
                                                    UN_PAG_BAN          => RS_BANCOSDET.PAG_BAN,
                                                    UN_PAQUETERS        => RS_BANCOSDET.PAQUETE,
                                                    UN_ACTIVAR          => UN_ACTIVAR,
                                                    UN_ACUERDO          => NULL,
                                                    UN_NC_ACUERDO       => NULL,
                                                    UN_USUARIO          => UN_USUARIO); 

             END IF;     

          END IF;
      END LOOP PAGO_BANCOSDET;

      IF UN_TIPOMODIFICACION = '05' THEN
	      <<RECIBOS_DE_PAGO>>
          FOR RS_RECIBO IN (SELECT 
                                   DOCNUM, 
                                   PREANO, 
                                   PREVAL,
                                   ANULADO, 
                                   FECHAANULACION 
                              FROM 
                                   IP_RECIBOS_DE_PAGO
                             WHERE 
                                   COMPANIA = UN_COMPANIA
                               AND PRECOD   = UN_CODIGO 
                               AND DOCNUM   = UN_ANULAR)

              LOOP
                 BEGIN
                    BEGIN
                      MI_CAMPOS        := ' ANULADO        = -1,
                                            FECHAANULACION = TO_DATE(''' || TO_CHAR(UN_FECHAMODIFICACION,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                            DATE_MODIFIED = SYSDATE,
                                            MODIFIED_BY   = '''||UN_USUARIO||''' ';

                      MI_CONDICION     := '   COMPANIA     = ''' || UN_COMPANIA || ''' 
                                            AND PRECOD     = ''' || UN_CODIGO || '''   
                                            AND DOCNUM     = ''' || UN_ANULAR || '''';

                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                             UN_ACCION    => 'M',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);



                         MI_MSGERROR(1).CLAVE := 'FECHAANULACION';
                         MI_MSGERROR(1).VALOR := UN_FECHAMODIFICACION;                                    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_ANU_FECANU,
                                               UN_REEMPLAZOS => MI_MSGERROR);    

                 END;

              END LOOP RECIBOS_DE_PAGO;
            MI_VALORANT := '-1''' || ', FACTURA = ''' || UN_ANULAR;
            MI_RTA      := PCK_DATOS.GL_RTA;

      END IF;

      IF UN_TIPOMODIFICACION = '06' THEN
	     <<RECIBOS_DE_PAGO1>>
          FOR RS_RECIBO IN (SELECT 
                                   DOCNUM,
                                   PREANO, 
                                   PREVAL, 
                                   ANULADO,
                                   NCUOTA,
                                   NCUOTA_ACUERDO,
                                   ACUERDO
                              FROM 
                                   IP_RECIBOS_DE_PAGO
                             WHERE 
                                   COMPANIA = UN_COMPANIA
                               AND PRECOD   = UN_CODIGO 
                               AND DOCNUM   = UN_ACTIVAR )
              LOOP

                 MI_RTACV := FC_CAMBIARVALORES( UN_COMPANIA         => UN_COMPANIA,
                                                UN_TIPORECIBO       => UN_TIPORECIBO,
                                                UN_TIPOMODIFICACION => UN_TIPOMODIFICACION,
                                                UN_BANCO            => MI_BANCO,
                                                UN_CODIGO           => UN_CODIGO,
                                                UN_FACTURA          => UN_FACTURA,
                                                UN_FECHA            => UN_FECHA, 
                                                UN_VALOR            => UN_VALOR, 
                                                UN_PAQUETE          => UN_PAQUETE,
                                                UN_PREFEC           => NULL, 
                                                UN_PAG_BAN          => NULL,
                                                UN_PAQUETERS        => NULL,
                                                UN_ACTIVAR          => UN_ACTIVAR,
                                                UN_ACUERDO          => RS_RECIBO.ACUERDO,
                                                UN_NC_ACUERDO       => RS_RECIBO.NCUOTA_ACUERDO,
                                                UN_USUARIO          => UN_USUARIO); 




              END LOOP RECIBOS_DE_PAGO1;
            MI_VALORANT := '-1''' || ', FACTURA = ''' || UN_ACTIVAR;
      END IF;
     IF PCK_DATOS.GL_RTA IS NOT NULL THEN
        MI_RTA := PCK_DATOS.GL_RTA;
      ELSIF MI_RTACV IS NOT NULL THEN
        MI_RTA := MI_RTACV;
      ELSE
          MI_RTA := '0';
      END IF;
      IF MI_RTA <> '0' THEN
          BEGIN 
            BEGIN       
               MI_CAMPOS    := UN_CAMPOANTERIOR || ' = ''' || MI_VALORANT || ''',
                               REGISTRADO            = -1,
                               DATE_MODIFIED = SYSDATE,
                               MODIFIED_BY   = '''||UN_USUARIO||''' ' ;

               MI_CONDICION :='   COMPANIA           = ''' || UN_COMPANIA || '''
                              AND MODIFICACION       = ''' || UN_MODIFICACION || '''   
                              AND TIPO_MODIFICACION  = ''' || UN_TIPOMODIFICACION || '''';

               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_MODIFICACIONES_PAGOS',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICION);

               MI_MSGERROR(1).CLAVE := 'CAMPO';
               MI_MSGERROR(1).VALOR := UN_CAMPOANTERIOR;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_MODIFI_PAG,
                                        UN_REEMPLAZOS => MI_MSGERROR);                                       
            END;
      END IF;
      RETURN MI_RTA;

  END FC_MODIFICARRECIBOS;				 

  PROCEDURE PR_ARREGLARCUPONES  
  /*
        NAME              : PR_ARREGLARCUPONES -- ArreglaCupones en Access
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 04/08/2016
        TIME              : 11:05 AM
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : 
        PARAMETERS        :UN_COMPANIA           => Compania de ingreso a la aplicación
                           UN_FECHA, UN_PAQUETE  => Son Parametros usados para armar la condicion los cuales definen 
                                                    los campos de la tabla IP_PAGO_BANCOSCAB que van a hacer actualizados.
                           UN_BANCO              => codigo de la entidad bancaria.


        @NAME:  corregirCupones
        @METHOD:  PUT      
    */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA          IN DATE,
    UN_BANCO          IN IP_BANCOS.CODIGOBANCO%TYPE,
    UN_PAQUETE        IN VARCHAR2,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS

    MI_CANT           PCK_SUBTIPOS.TI_DOBLE;
    MI_ACUM           NUMBER(20,0);
    MI_PCKDATOS       PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_PCKDATOS       PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION; 
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    <<PAGO_BANCOSCAB>>
    FOR RS_ARREGLACUPONES IN (SELECT 
                                     NROCUPONESACU 
                                     ,NROCUPONES
                                     ,ACUMULADO 
                                     ,VLRREPORTADO 
                                FROM 
                                     IP_PAGO_BANCOSCAB 
                               WHERE 
                                     COMPANIA        = UN_COMPANIA
                                 AND TRUNC(PREFEC)   = UN_FECHA 
                                 AND PAG_BAN         = UN_BANCO 
                                 AND PAQUETE         = UN_PAQUETE ) 
        LOOP
              SELECT 
                     COUNT(*) CANT 
                INTO 
                     MI_CANT
                FROM 
                     IP_PAGO_BANCOSDET  
               WHERE 
                     TRUNC(PREFEC)  = TRUNC(UN_FECHA) 
                 AND PAG_BAN        = UN_BANCO   
                 AND PAQUETE        = UN_PAQUETE ; 

              SELECT 
                     SUM(PREVAL) ACUM 
                INTO 
                     MI_ACUM
                FROM 
                     IP_PAGO_BANCOSDET 
               WHERE 
                     TRUNC(PREFEC)  = TRUNC(UN_FECHA)  
                 AND PAG_BAN        = UN_BANCO   
                 AND PAQUETE        = UN_PAQUETE ; 
           BEGIN
                  BEGIN
                    MI_CAMPOS        := '   NROCUPONES    = ' || NVL(MI_CANT, 0) || '
                                          , NROCUPONESACU = ' || NVL(MI_CANT, 0) || '
                                          , ACUMULADO     = ' || NVL(MI_ACUM, 0) || '
                                          , VLRREPORTADO  = ' || NVL(MI_ACUM, 0) || '
                                          , DATE_MODIFIED = SYSDATE
                                          , MODIFIED_BY   = '''|| UN_USUARIO|| '''' ;

                    MI_CONDICION     := '       COMPANIA      = ''' || UN_COMPANIA || ''' 
                                            AND TRUNC(PREFEC) = ''' || UN_FECHA || '''' || ' 
                                            AND PAG_BAN       = ''' ||  UN_BANCO || ''' 
                                            AND PAQUETE       = ''' ||  UN_PAQUETE || '''';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGO_BANCOSCAB',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    MI_MSGERROR(1).CLAVE := 'NROCUPONES';
                    MI_MSGERROR(1).VALOR := NVL(MI_CANT, 0);

                    MI_MSGERROR(2).CLAVE := 'NROCUPONESACU';
                    MI_MSGERROR(2).VALOR := NVL(MI_CANT, 0);

                    MI_MSGERROR(1).CLAVE := 'ACUMULADO';
                    MI_MSGERROR(1).VALOR := NVL(MI_ACUM, 0);

                    MI_MSGERROR(2).CLAVE := 'VLRREPORTADO';
                    MI_MSGERROR(2).VALOR := NVL(MI_ACUM, 0);
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAG_BANCAB,
                    UN_REEMPLAZOS => MI_MSGERROR
                  );
                END;


        END LOOP PAGO_BANCOSCAB;


  END PR_ARREGLARCUPONES;		

  FUNCTION FC_CAMBIARVALORES 
  /*
        NAME              : FC_CAMBIARVALORES -- CAMBIARBANCO, CAMBIARFECHA, CAMBIARVALOR, CAMBIARPAQUETE en Access
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 04/08/2016
        TIME              : 11:05 AM
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : 
        PARAMETERS        :UN_COMPANIA         => Compania de ingreso a la aplicación
                           UN_TIPORECIBO       => Parametro que sirve para validar que tipo de actualizacion debe ejecutarse. Este puede tomar valores de 
                                                 1,2,3,4.
                           UN_TIPOMODIFICACION => Parametro que sirve para validar que tipo de actualizacion debe ejecutarse. Este puede tomar valores de 
                                                  1,2,3,4,5,6.
                           UN_BANCO            => Codigo de la entidad Bancaria. 
                           UN_CODIGO           => Codigo del predio a consultar.
                           UN_FACTURA          => Codigo de la factura el cual se desea actualizar.
                           UN_FECHA            => Fecha en la cual se van a hacer los respectivos cambios.
                           UN_VALOR            => Valor de la factura que se quiere actualizar.
                           UN_PREFEC           => Fecha anterior a la cual se van a hacer los cambios.
                           UN_PAG_BAN          => Codigo de la entidad Bancaria a la que se va a hacer el pago.
                           UN_ACTIVAR          => Numero del documento.
                           UN_ACUERDO          => Codigo del acuerdo.
                           UN_NC_ACUERDO       => Cuota del acuerdo.



        @NAME:  modificarValores
        @METHOD:  GET    
    */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPORECIBO           IN VARCHAR2,
    UN_TIPOMODIFICACION     IN VARCHAR2,
    UN_BANCO                IN IP_BANCOS.CODIGOBANCO%TYPE,
    UN_CODIGO               IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_FACTURA              IN PCK_SUBTIPOS.TI_DOCNUM DEFAULT NULL,
    UN_FECHA                IN DATE,
    UN_VALOR                IN PCK_SUBTIPOS.TI_DOBLE,
    UN_PAQUETE              IN VARCHAR2,
    UN_PREFEC               IN DATE,
    UN_PAG_BAN              IN IP_BANCOS.CODIGOBANCO%TYPE,
    UN_PAQUETERS            IN VARCHAR2,
    UN_ACTIVAR              IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_ACUERDO              IN VARCHAR2, 
    UN_NC_ACUERDO           IN VARCHAR2,
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
  )RETURN VARCHAR2
  AS

    MI_PCKDATOS            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPO               VARCHAR2(20 CHAR);
    MI_VALOR               VARCHAR2(100 CHAR);
    MI_CAMPOUP             VARCHAR2(20 CHAR);
    MI_CAMPOUPC            VARCHAR2(20 CHAR);
    MI_CAMPORP             VARCHAR2(20 CHAR);    
    MI_CAMPOUPI            VARCHAR2(20 CHAR);
    MI_CAMPOPBD            VARCHAR2(20 CHAR); 
    MI_CAMPOFCT            VARCHAR2(20 CHAR);
    MI_CONDICIONUP         VARCHAR2(300 CHAR);
    MI_CONDICIONFAB        VARCHAR2(300 CHAR);
    MI_CONDICIONFAC        VARCHAR2(300 CHAR);
    MI_CONDICIONFC         VARCHAR2(300 CHAR);
    MI_CONDICIONRP         VARCHAR2(300 CHAR);
    MI_CONDICIONIUP        VARCHAR2(300 CHAR);
    MI_CONDICIONPBD        VARCHAR2(300 CHAR);
    MI_CONDICIONFCT        VARCHAR2(300 CHAR); 
    MI_CONDICIONUPI        VARCHAR2(300 CHAR);
    MI_RTA            	   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RTAAUX         	   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION; 
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

      IF UN_TIPOMODIFICACION = '01' THEN
          MI_CAMPOUP      := 'BANCOULTIMOABONO';
          MI_CAMPO        := 'PAG_BAN';
          MI_VALOR        := '''' || UN_BANCO || '''';
          MI_CAMPOUPC     := 'PAGBAN_CUOTA';
          MI_CAMPORP      := 'PAG_BANPAG';
          MI_CAMPOPBD     := 'PAG_BAN';
          MI_CAMPOUPI	    := 'PAG_BAN';
          MI_CAMPOFCT	    := 'PAG_BAN';

         MI_CONDICIONUP   := '    COMPANIA           = ''' || UN_COMPANIA || '''
                              AND CODIGO             = ''' || UN_CODIGO || ''' 
                              AND FACTURAULTIMOABONO = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAB := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAC := '     COMPANIA = ''' || UN_COMPANIA || ''' 
                               AND PREDIO   = ''' || UN_CODIGO || ''' 
                               AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFC  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONIUP := '    COMPANIA      = ''' || UN_COMPANIA || ''' 
                              AND CODIGO        = ''' || UN_CODIGO || '''
                              AND FACTURA_CUOTA = ''' || UN_FACTURA || '''';

          MI_CONDICIONPBD := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                              AND PREFEC      = ' || 'TO_DATE(''' || TO_CHAR(UN_PREFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' || ' 
                              AND PAQUETE     = ''' || UN_PAQUETERS || '''' || '
                              AND NUMFACTURA  = ''' ||  UN_FACTURA || '''';

          MI_CONDICIONRP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                               AND DOCNUM  = ''' ||  UN_FACTURA || '''';

          MI_CONDICIONUPI := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND NUM_COM  = ''' || UN_FACTURA || '''';


          MI_CONDICIONFCT := '    COMPANIA = ''' || UN_COMPANIA || '''
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

      ELSIF UN_TIPOMODIFICACION = '02' THEN
          MI_CAMPOUP  	  := 'FECHAULTIMOABONO';
          MI_VALOR    	  := 'TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')';
          MI_CAMPO    	  := 'FECHAPAGO';
          MI_CAMPOUPC  	  := 'PAGFEC_CUOTA';
          MI_CAMPOPBD  	  := 'PREFEC';
          MI_CAMPORP      := 'PREFECPAG';
          MI_CAMPOUPI	    := 'PAG_FEC';
          MI_CAMPOFCT 	  := 'FECHAPAGO';

          MI_CONDICIONUP  := '    COMPANIA           = ''' || UN_COMPANIA || ''' 
                              AND CODIGO             = ''' || UN_CODIGO || '''
                              AND FACTURAULTIMOABONO = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAB := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAC := '    COMPANIA = ''' || UN_COMPANIA || '''
                              AND PREDIO   = ''' || UN_CODIGO || '''
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFC  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONIUP := '    COMPANIA      = ''' || UN_COMPANIA || ''' 
                              AND CODIGO        = ''' || UN_CODIGO || ''' 
                              AND FACTURA_CUOTA = ''' || UN_FACTURA || '''';

          MI_CONDICIONPBD := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                              AND PAG_BAN     = ''' || UN_PAG_BAN || '''
                              AND PAQUETE     = ''' || UN_PAQUETERS || '''' || ' 
                              AND NUMFACTURA  = ''' ||  UN_FACTURA || '''';

          MI_CONDICIONRP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONUPI := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND NUM_COM  = ''' || UN_FACTURA || '''';

          MI_CONDICIONFCT := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

      ELSIF UN_TIPOMODIFICACION = '04' THEN
          MI_CAMPOUP   	  := 'VALORULTIMOABONO';
          MI_CAMPO     	  := 'TOTAL';
          MI_VALOR     	  := '''' || UN_VALOR || '''';
          MI_CAMPOUPC  	  := 'TOTAL_CUOTA';
          MI_CAMPOPBD  	  := 'PREVAL';
          MI_CAMPORP      := 'PREVAL';
          MI_CAMPOUPI	    := 'PAG_VAL';
          MI_CAMPOFCT	    := 'PREVAL';
          MI_CONDICIONUP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAB := '    COMPANIA = ''' || UN_COMPANIA || '''
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAC := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND PREDIO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFC  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONIUP := '    COMPANIA      = ''' || UN_COMPANIA || '''
                              AND CODIGO        = ''' || UN_CODIGO || ''' 
                              AND FACTURA_CUOTA = ''' || UN_FACTURA || '''';

          MI_CONDICIONPBD := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                              AND PAG_BAN     = ''' || UN_PAG_BAN || ''' 
                              AND PAQUETE     = ''' || UN_PAQUETERS || '''' || ' 
                              AND NUMFACTURA  = ''' ||  UN_FACTURA || '''';

          MI_CONDICIONRP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONUPI := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND NUM_COM  = ''' || UN_FACTURA || '''';

          MI_CONDICIONFCT := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

	  ELSIF UN_TIPOMODIFICACION = '06' THEN
          MI_CAMPO     	  := 'DOCNUM';
          MI_CAMPOUP   	  := 'FACTURA_ACUERDO';
          MI_VALOR     	  := '''' || UN_ACTIVAR || '''';
          MI_CAMPOUPC  	  := 'FACTURA_CUOTA';
          MI_CAMPOPBD  	  := 'PREVAL';
          MI_CAMPORP      := 'PREVAL';
          MI_CAMPOUPI	    := 'PAG_VAL';
          MI_CAMPOFCT	    := 'PREVAL';

          MI_CONDICIONUP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''';

          MI_CONDICIONFAB := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONFAC := '    COMPANIA      = ''' || UN_COMPANIA || ''' 
                              AND PREDIO        = ''' || UN_CODIGO || ''' 
                              AND PAGADO        IN (0) 
                              AND CODIGOACUERDO = ''' || UN_ACUERDO || ''' 
                              AND CUOTA         = ''' || UN_NC_ACUERDO;

          MI_CONDICIONFC  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''';

          MI_CONDICIONIUP := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''';

          MI_CONDICIONPBD := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                              AND PAG_BAN     = ''' || UN_PAG_BAN || ''' 
                              AND PAQUETE     = ''' || UN_PAQUETE || '''' || '
                              AND NUMFACTURA  = ''' ||  UN_FACTURA || '''';

          MI_CONDICIONRP  := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND DOCNUM   = ''' || UN_FACTURA || '''';

          MI_CONDICIONUPI := '    COMPANIA = ''' || UN_COMPANIA || '''
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND NUM_COM  = ''' || UN_FACTURA || '''';

          MI_CONDICIONFCT := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND CODIGO   = ''' || UN_CODIGO || '''
                              AND DOCNUM   = ''' || UN_FACTURA || '''';
      END IF;

    IF UN_TIPOMODIFICACION = '01' OR UN_TIPOMODIFICACION = '02' OR UN_TIPOMODIFICACION = '04' OR UN_TIPOMODIFICACION = '06' THEN
          IF UN_TIPORECIBO = '1' THEN -- Recibo de Abono
              IF UN_TIPOMODIFICACION = '06' THEN
                  MI_RTA := '-1';
                  RETURN MI_RTA;
              ELSE 


                BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPOUP ||'= '||MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';                                                     
                    MI_CONDICION := MI_CONDICIONUP;                           
                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPOUP';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USU_PRE,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;

           IF MI_PCKDATOS IN (0) THEN
              MI_RTAAUX := MI_RTAAUX || ' IP_USUARIOS_PREDIAL, ';
           END IF;

                 BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPO ||'= '||MI_VALOR || ',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := MI_CONDICIONFAB;

                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOSABONOS',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPO';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_FAC_ABO,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;

                   IF MI_PCKDATOS IN (0) THEN
                      MI_RTAAUX := MI_RTAAUX || ' IP_FACTURADOSABONOS, ';
                   END IF;
            END IF;           
          ELSIF UN_TIPORECIBO = '2' THEN -- Recibo de Acuerdo

            BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPO ||'= '||MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := MI_CONDICIONFAC;

                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_FACTURADOSACUERDOS',
                                                       UN_ACCION=>'M',
                                                       UN_CAMPOS=>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPO';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_FAC_ACU,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;

              IF MI_PCKDATOS IN (0) THEN
                      MI_RTAAUX := MI_RTAAUX || ' IP_FACTURADOSACUERDOS, ';
              END IF;
              IF UN_TIPOMODIFICACION = '06' THEN
              BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPOUP ||'= '||MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := MI_CONDICIONUP;

                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION=>'M',
                                                       UN_CAMPOS=>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPO';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USU_PRE,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;
                  IF MI_PCKDATOS IN (0) THEN
                      MI_RTAAUX := MI_RTAAUX || ' IP_USUARIOS_PREDIAL, ';
                  END IF;
              END IF;
          ELSIF UN_TIPORECIBO = '3' THEN -- Recibo de Cuotas

               BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPO ||'= '||MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := MI_CONDICIONFC;

                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_FACTURADOSCUOTAS',
                                                       UN_ACCION=>'M',
                                                       UN_CAMPOS=>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPO';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_FAC2_CUO,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;

              IF MI_PCKDATOS IN (0) THEN
                      MI_RTAAUX := MI_RTAAUX || ' IP_FACTURADOSCUOTAS, ';
              END IF;
                BEGIN
                  BEGIN
                    MI_CAMPOS    := MI_CAMPOUPC ||'= '||MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := MI_CONDICIONIUP;

                     MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION=>'M',
                                                       UN_CAMPOS=>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'MI_CAMPO';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USU_PRE,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;
              IF MI_PCKDATOS IN (0) THEN
                      MI_RTAAUX := MI_RTAAUX || ' IP_USUARIOS_PREDIAL, ';
              END IF;
          ELSIF UN_TIPORECIBO = '4' THEN -- Recibo de Vigencias   
              IF UN_TIPOMODIFICACION = '06' THEN
                BEGIN
                  BEGIN
                    MI_CAMPOS    :=  ' RECIBO_ACTUAL = ' || MI_VALOR||',
                                       DATE_MODIFIED = SYSDATE,
                                       MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := '     COMPANIA = ''' || UN_COMPANIA || ''' 
                                        AND CODIGO = ''' || UN_CODIGO || '''';

                    MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION=>'M',
                                                       UN_CAMPOS=>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);

                     MI_MSGERROR(1).CLAVE := 'RECIBO_ACTUAL';
                     MI_MSGERROR(1).VALOR := MI_VALOR;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_REC_ACT,
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;
                      IF MI_PCKDATOS IN (0) THEN
                          MI_RTAAUX := MI_RTAAUX || ' IP_USUARIOS_PREDIAL, ';
                      END IF;
              END IF;
          END IF;
      END IF;

	  IF UN_TIPOMODIFICACION = '06' THEN
          BEGIN
              BEGIN
                MI_CAMPOS    :=   '   ANULADO = -1
                                    , FECHAANULACION = '''  || SYSDATE || ''' ,
                                      DATE_MODIFIED = SYSDATE,
                                      MODIFIED_BY   = '''||UN_USUARIO||''' ';

                MI_CONDICION :=  '     COMPANIA = ''' || UN_COMPANIA || ''' 
                                   AND PRECOD   = ''' || UN_CODIGO || ''' 
                                   AND PAGO     = 0
                                   AND ANULADO  = 0';

                MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_RECIBOS_DE_PAGO',
                                                   UN_ACCION=>'M',
                                                   UN_CAMPOS=>MI_CAMPOS,
                                                   UN_CONDICION=>MI_CONDICION);

                 MI_MSGERROR(1).CLAVE := 'FECHAANULACION';
                 MI_MSGERROR(1).VALOR := SYSDATE;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_FECHA_ANUL,
                                         UN_REEMPLAZOS => MI_MSGERROR);
            END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_RECIBOS_DE_PAGO, ';
         END IF;

          BEGIN
              BEGIN
                MI_CAMPOS    :=   ' ANULADO = 0,
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

                MI_CONDICION :=  '     COMPANIA = ''' || UN_COMPANIA || '''
                                       AND DOCNUM = ''' || UN_ACTIVAR || ''' 
                                       AND PRECOD = ''' || UN_CODIGO || '''';

                MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_RECIBOS_DE_PAGO',
                                                  UN_ACCION=>'M',
                                                  UN_CAMPOS=>MI_CAMPOS,
                                                  UN_CONDICION=>MI_CONDICION);

                 MI_MSGERROR(1).CLAVE := 'FECHAANULACION';
                 MI_MSGERROR(1).VALOR := SYSDATE;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_ANUL,
                                         UN_REEMPLAZOS => MI_MSGERROR);
            END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_RECIBOS_DE_PAGO, ';
         END IF;
    ELSIF UN_TIPOMODIFICACION = '03' THEN

      BEGIN
          BEGIN
            MI_CAMPOS    :=  'PAQUETE = ''' || UN_PAQUETE || ''',
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY   = '''||UN_USUARIO||''' ';

            MI_CONDICION :=   '     COMPANIA = ''' || UN_COMPANIA || ''' 
                                 AND PAG_BAN = ''' || UN_PAG_BAN || ''' 
                                 AND PAQUETE = ''' || UN_PAQUETERS || ''' 
                                 AND NUMFACTURA = ''' || UN_FACTURA || '''';

            MI_PCKDATOS  := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_PAGO_BANCOSDET',
                                               UN_ACCION=>'M',
                                               UN_CAMPOS=>MI_CAMPOS,
                                               UN_CONDICION=>MI_CONDICION);

             MI_MSGERROR(1).CLAVE := 'PAQUETE';
             MI_MSGERROR(1).VALOR := UN_PAQUETE;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_BANC_DET,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;

        IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_PAGO_BANCOSDET, ';
         END IF;

         BEGIN
          BEGIN
            MI_CAMPOS        :=  'PAQUETEPAG = ''' || UN_PAQUETE || ''',
                                  DATE_MODIFIED = SYSDATE,
                                  MODIFIED_BY   = '''||UN_USUARIO||''' ';

            MI_CONDICION     :=   '    COMPANIA = ''' || UN_COMPANIA || ''' 
                                   AND DOCNUM = ''' || UN_FACTURA || '''';

            MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_RECIBOS_DE_PAGO',
                                              UN_ACCION=>'M',
                                              UN_CAMPOS=>MI_CAMPOS,
                                              UN_CONDICION=>MI_CONDICION);

             MI_MSGERROR(1).CLAVE := 'PAQUETE';
             MI_MSGERROR(1).VALOR := UN_PAQUETE;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_REC_PAG,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_RECIBOS_DE_PAGO, ';
         END IF;
	  ELSE

        BEGIN
          BEGIN
            MI_CAMPOS        :=   MI_CAMPOPBD || ' = ' || MI_VALOR||',
                                  DATE_MODIFIED = SYSDATE,
                                  MODIFIED_BY   = '''||UN_USUARIO||''' ';

            MI_CONDICION     :=   MI_CONDICIONPBD;

            MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_PAGO_BANCOSDET',
                                              UN_ACCION=>'M',
                                              UN_CAMPOS=>MI_CAMPOS,
                                              UN_CONDICION=>MI_CONDICION);

             MI_MSGERROR(1).CLAVE := 'PAQUETE';
             MI_MSGERROR(1).VALOR := UN_PAQUETE;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_BANC_DET,
                                     UN_REEMPLAZOS => MI_MSGERROR
                );
        END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_PAGO_BANCOSDET, ';
         END IF;
         -- Actualiza el codigo del banco en los recibos de pago
          BEGIN
            BEGIN
              MI_CAMPOS        :=   MI_CAMPORP || ' = ' || MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

              MI_CONDICION     :=   MI_CONDICIONRP;

              MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_RECIBOS_DE_PAGO',
                                                UN_ACCION=>'M',
                                                UN_CAMPOS=>MI_CAMPOS,
                                                UN_CONDICION=>MI_CONDICION);

               MI_MSGERROR(1).CLAVE := 'PAQUETE';
               MI_MSGERROR(1).VALOR := UN_PAQUETE;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_REC_PAG,
                                       UN_REEMPLAZOS => MI_MSGERROR);
          END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_RECIBOS_DE_PAGO, ';
         END IF;
           BEGIN
            BEGIN
              MI_CAMPOS        :=   MI_CAMPOUPI || ' = ' || MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' ';

              MI_CONDICION     :=   MI_CONDICIONUPI;

              MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_USUARIOS_PREDIAL',
                                                UN_ACCION=>'M',
                                                UN_CAMPOS=>MI_CAMPOS,
                                                UN_CONDICION=>MI_CONDICION);

               MI_MSGERROR(1).CLAVE := 'PAQUETE';
               MI_MSGERROR(1).VALOR := UN_PAQUETE;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USU_PRE,
                                       UN_REEMPLAZOS => MI_MSGERROR
                  );
          END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_USUARIOS_PREDIAL, ';
         END IF;
           BEGIN
            BEGIN
              MI_CAMPOS        :=   MI_CAMPOFCT || ' = ' || MI_VALOR||',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''' '    ;

              MI_CONDICION     :=   MI_CONDICIONFCT;

              MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_FACTURADOS',
                                                UN_ACCION=>'M',
                                                UN_CAMPOS=>MI_CAMPOS,
                                                UN_CONDICION=>MI_CONDICION);

               MI_MSGERROR(1).CLAVE := 'PAQUETE';
               MI_MSGERROR(1).VALOR := UN_PAQUETE;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USU_PRE,
                                       UN_REEMPLAZOS => MI_MSGERROR);
          END;

         IF MI_PCKDATOS IN (0) THEN
               MI_RTAAUX := MI_RTAAUX || ' IP_FACTURADOS ';
         END IF;
    END IF;
    IF MI_PCKDATOS IS NOT NULL THEN
       IF MI_RTAAUX IS NOT NULL THEN
          MI_RTA := MI_RTAAUX;
       ELSE
          MI_RTA := MI_PCKDATOS;
       END IF;
    ELSE
        MI_RTA := '0';
    END IF;
    RETURN MI_RTA;  

  END FC_CAMBIARVALORES;

FUNCTION FC_ACT_ULTVIGCANCELADA
 /*
    NAME              : FC_ACTUALIZAR_ULTIMAVIGCANCELADA en acces --> Actualizar_UltimaVigCancelada
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 11/08/2016 
    TIME              : 11:51 AM     
    MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
    DATE MODIFIED     : 05/07/2017
    DESCRIPTION       : 
    MODIFICATIONS     : Agrego el llamado al procedimiento PCK_PREDIAL.PR_AUDITORIA 
                        y los parametros necesarios por el mismo
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_CODINICIAL       => Codigo Inicial del usuario de predial 
                        UN_CODFINAL         => Codigo Final del usuario de predial 
    @NAME:  actualizarUltimaVigenciaCancelada
    @METHOD:  GET
  */
  ( 
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODINICIAL               IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_CODFINAL                 IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_MODCOD                   IN IP_AUDITORIA.MODCOD%TYPE, 
  UN_USUARIO                  IN PCK_SUBTIPOS.TI_USUARIO, 
  UN_DESCRIPCION              IN IP_AUDITORIA.DESCRIPCION%TYPE
  )  
  RETURN VARCHAR2
  AS
  MI_INTANOVALIDO             PCK_SUBTIPOS.TI_ANIO;
  MI_ACTUALIZARRUTINA         BOOLEAN;
  MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL;
  MI_ANOINICIAL               PCK_SUBTIPOS.TI_ANIO;
  MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
  MI_CONTRECIBOS              NUMBER;
  MI_CONTUSUARIOS             NUMBER;
  MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
  MI_MERGEUSING               PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE              PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE              PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_RTA                      PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_MODCCO                   IP_AUDITORIA.MODCCO%TYPE;
BEGIN

  MI_ACTUALIZARRUTINA := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA,
                                                          UN_NOMBRE    => 'ACTUALIZAR DATOS PAGO EN RUTINA DE MANTENIMIENTO',
                                                          UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                          UN_FECHA_PAR => SYSDATE),'NO')
                                                          ) = 'SI';

  MI_ANOINICIAL := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                       UN_NOMBRE    => 'AÑO INICIAL MANTENIMIENTO ULTIMA VIGENCIA',
                                                       UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                       UN_FECHA_PAR => SYSDATE)
                                                       ,'1900'));

  <<USUARIOS_PREDIAL>>
  FOR RSUSUARIO IN (  SELECT  
                             DISTINCT  IP_USUARIOS_PREDIAL.CODIGO,                          
                             MAX(IP_RECIBOS_DE_PAGO.PREANO) AS PREANO
                        FROM 
                             IP_USUARIOS_PREDIAL
                       INNER JOIN IP_RECIBOS_DE_PAGO
                          ON IP_USUARIOS_PREDIAL.COMPANIA      = IP_RECIBOS_DE_PAGO.COMPANIA
                         AND IP_USUARIOS_PREDIAL.CODIGO        = IP_RECIBOS_DE_PAGO.PRECOD
                         AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN  = IP_RECIBOS_DE_PAGO.NUMERO_ORDEN
                       WHERE 
                             IP_USUARIOS_PREDIAL.CODIGO  BETWEEN UN_CODINICIAL AND UN_CODFINAL
                         AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL
                         AND IP_USUARIOS_PREDIAL.INDBORRADO   = 0
                         AND IP_RECIBOS_DE_PAGO.ANULADO       = 0
                         AND IP_RECIBOS_DE_PAGO.PAGO    NOT IN (0)
                         AND ESABONO                    NOT IN (0)
                       GROUP BY 
                                IP_USUARIOS_PREDIAL.CODIGO,
                                IP_USUARIOS_PREDIAL.PAGO_ANO,
                                IP_USUARIOS_PREDIAL.NUMERO_FACTURA)
  LOOP
  BEGIN 
    BEGIN
      MI_TABLA     := 'IP_FACTURADOS';
      MI_CAMPOS    := ' PAGADO   = 0,
                      MODIFIED_BY = '''||UN_USUARIO||''',
                      DATE_MODIFIED = SYSDATE
                    ';
      MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                    AND PREANO   = ' || RSUSUARIO.PREANO || ' 
                    AND CODIGO   = ''' || RSUSUARIO.CODIGO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);

           MI_MSGERROR(1).CLAVE := 'PAGADO';
           MI_MSGERROR(1).VALOR := 0;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAGO,
                             UN_REEMPLAZOS => MI_MSGERROR);
  END;                                            

  BEGIN                                        
    BEGIN
      MI_TABLA     := 'IP_USUARIOS_PREDIAL';
      MI_CAMPOS    := 'PAGO_ANO= ' || (RSUSUARIO.PREANO - 1) ||',
                      MODIFIED_BY = '''|| UN_USUARIO ||''',
                      DATE_MODIFIED = SYSDATE
                    ';
      MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                      AND CODIGO   = ''' || RSUSUARIO.CODIGO || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);

    MI_MSGERROR(1).CLAVE := 'PAGO_ANO';
    MI_MSGERROR(1).VALOR := (RSUSUARIO.PREANO - 1);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAGO_ANO,
                             UN_REEMPLAZOS => MI_MSGERROR);
  END; 

  END LOOP USUARIOS_PREDIAL;

  BEGIN 
      BEGIN 

      MI_MERGEUSING := 'SELECT 
                               IP_RECIBOS_DE_PAGO.COMPANIA,
                               IP_RECIBOS_DE_PAGO.DOCNUM,
                               -1 NUE_PAGO, 
                               IP_PAGO_BANCOSDET.PAG_BAN,
                               IP_PAGO_BANCOSDET.PREFEC,
                               IP_PAGO_BANCOSDET.PAQUETE
                          FROM 
                               IP_RECIBOS_DE_PAGO 
                         INNER JOIN IP_PAGO_BANCOSDET 
                               ON IP_RECIBOS_DE_PAGO.COMPANIA=IP_PAGO_BANCOSDET.COMPANIA 
                               AND IP_RECIBOS_DE_PAGO.DOCNUM=IP_PAGO_BANCOSDET.NUMFACTURA 
                               AND IP_RECIBOS_DE_PAGO.PRECOD=IP_PAGO_BANCOSDET.PRECOD
                         WHERE 
                               IP_RECIBOS_DE_PAGO.COMPANIA = ''' || UN_COMPANIA || '''
                           AND IP_RECIBOS_DE_PAGO.PRECOD BETWEEN ''' || UN_CODINICIAL || ''' AND ''' || UN_CODFINAL || ''' 
                           AND IP_RECIBOS_DE_PAGO.ANULADO=0 
                           AND IP_RECIBOS_DE_PAGO.PAGO=0';

      MI_MERGEENLACE:='    TABLA.COMPANIA = VISTA.COMPANIA
                       AND TABLA.DOCNUM   = VISTA.DOCNUM';

      MI_MERGEEXISTE := 'UPDATE SET  TABLA.PAGO         = -1,
                                     TABLA.PAG_BANPAG   = VISTA.PAG_BAN,
                                     TABLA.PREFECPAG    = VISTA.PREFEC,
                                     TABLA.PAQUETEPAG   = VISTA.PAQUETE,
                                     TABLA.MODIFIED_BY  = '''|| UN_USUARIO ||''',
                                     TABLA.DATE_MODIFIED =  SYSDATE
                                     '; 

      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'IP_RECIBOS_DE_PAGO',
                                  UN_ACCION      => 'MM' ,
                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                  UN_MERGEENLACE => MI_MERGEENLACE , 
                                  UN_MERGEEXISTE => MI_MERGEEXISTE); 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                             UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_RECIBO_PAG);
  END; 

  IF MI_ACTUALIZARRUTINA THEN
    FOR RSRECIBO IN (SELECT
                            IP_FACTURADOS.CODIGO PRECOD,
                            IP_FACTURADOS.PREANO 
                      FROM  
                            IP_FACTURADOS
                     INNER JOIN IP_RECIBOS_DE_PAGO
                        ON IP_FACTURADOS.CODIGO          = IP_RECIBOS_DE_PAGO.PRECOD
                       AND IP_FACTURADOS.PREANO          >= IP_RECIBOS_DE_PAGO.PREANOI
                       AND IP_FACTURADOS.PREANO          <= IP_RECIBOS_DE_PAGO.PREANOF
                     WHERE 
                           IP_FACTURADOS.CODIGO BETWEEN UN_CODINICIAL AND UN_CODFINAL
                       AND IP_FACTURADOS.PAGADO      NOT IN (0)
                       AND IP_FACTURADOS.TOTAL       NOT IN (0)
                       AND IP_FACTURADOS.INDEXE          IN (0)
                       AND IP_FACTURADOS.INDCAR          IN (0)
                       AND IP_FACTURADOS.INDEXEOTROS     IN (0)
                       AND IP_FACTURADOS.NOCOBRADO       IN (0)
                       AND IP_FACTURADOS.INDPAGO_ACPAG   IN (0)
                       AND IP_RECIBOS_DE_PAGO.ESABONO    IN (0)
                       AND IP_RECIBOS_DE_PAGO.ESACUERDO  IN (0)
                       AND IP_RECIBOS_DE_PAGO.ESCUOTA    IN (0)
                       AND IP_RECIBOS_DE_PAGO.ANULADO    IN (0)
                       AND IP_RECIBOS_DE_PAGO.PAGO       IN (0))
    LOOP
    BEGIN 
      BEGIN
        MI_TABLA     := 'IP_FACTURADOS';
        MI_CAMPOS    := '     PAGADO = 0,
                        MODIFIED_BY = '''|| UN_USUARIO ||''',
                        DATE_MODIFIED = SYSDATE
                      ';
        MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                        AND CODIGO   = ''' || RSRECIBO.PRECOD || '''
                        AND PREANO   = ' || RSRECIBO.PREANO;

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);                

        MI_TABLA     := 'IP_USUARIOS_PREDIAL';
        MI_CAMPOS    := ' PAGO_ANO = ' || (RSRECIBO.PREANO - 1) ||',
                        MODIFIED_BY = '''|| UN_USUARIO ||''',
                        DATE_MODIFIED = SYSDATE
                        ';
        MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                        AND CODIGO   = ''' || RSRECIBO.PRECOD || '''';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);

       MI_MSGERROR(1).CLAVE := 'PAGO_ANO';
       MI_MSGERROR(1).VALOR := (RSRECIBO.PREANO - 1); 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                               UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAGO_ANO,
                               UN_REEMPLAZOS => MI_MSGERROR);
  END;     

    END LOOP;
  END IF;

  MI_CONTUSUARIOS := 0;
  FOR RSUSUARIO IN (  SELECT 
                              IP_USUARIOS_PREDIAL.CODIGO, 
                              IP_USUARIOS_PREDIAL.PAGO_ANO 
                        FROM 
                              IP_USUARIOS_PREDIAL 
                        INNER JOIN ( SELECT
                                             IP_RECIBOS_DE_PAGO.COMPANIA, 
                                             IP_RECIBOS_DE_PAGO.PRECOD CODIGO,
                                             MAX(IP_RECIBOS_DE_PAGO.PREANO)  AS ULTIMO_ANO,
                                             MAX(IP_RECIBOS_DE_PAGO.PREFEC)  AS ULT_FECHA,
                                             MAX(IP_RECIBOS_DE_PAGO.PREVAL)  AS ULT_VALOR,
                                             MAX(IP_RECIBOS_DE_PAGO.PAG_BAN) AS ULT_BANCO,
                                             MAX(IP_RECIBOS_DE_PAGO.DOCNUM)  AS ULT_FACTURA
                                      FROM
                                            IP_RECIBOS_DE_PAGO
                                      WHERE  
                                            IP_RECIBOS_DE_PAGO.COMPANIA         = UN_COMPANIA 
                                        AND IP_RECIBOS_DE_PAGO.PRECOD BETWEEN UN_CODINICIAL AND UN_CODFINAL
                                        AND IP_RECIBOS_DE_PAGO.ANULADO      IN (0)
                                        AND IP_RECIBOS_DE_PAGO.PAGO     NOT IN (0)
                                        AND IP_RECIBOS_DE_PAGO.ESABONO       IN(0)
                                      GROUP BY 
                                            IP_RECIBOS_DE_PAGO.COMPANIA, 
                                            IP_RECIBOS_DE_PAGO.PRECOD,
                                            IP_RECIBOS_DE_PAGO.ANULADO,
                                            IP_RECIBOS_DE_PAGO.PAGO)  TMP_ULTIMO_ANO 

                          ON IP_USUARIOS_PREDIAL.COMPANIA         = TMP_ULTIMO_ANO.COMPANIA 
                         AND IP_USUARIOS_PREDIAL.CODIGO           = TMP_ULTIMO_ANO.CODIGO 
                       WHERE IP_USUARIOS_PREDIAL.COMPANIA         IN(UN_COMPANIA) 
                         AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN     IN(PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL) 
                         AND IP_USUARIOS_PREDIAL.INDBORRADO       IN (0) 
                         AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN (0) 
                         AND IP_USUARIOS_PREDIAL.PAGO_ACUERDO     IN (0)
                          OR IP_USUARIOS_PREDIAL.PAG_BAN          <> TMP_ULTIMO_ANO.ULT_BANCO 
                         AND IP_USUARIOS_PREDIAL.PAGO_ACUERDO     IS NULL  
                          OR IP_USUARIOS_PREDIAL.PAG_FEC          <> TMP_ULTIMO_ANO.ULT_FECHA 
                          OR IP_USUARIOS_PREDIAL.PAGO_ANO         <> TMP_ULTIMO_ANO.ULTIMO_ANO 
                          OR IP_USUARIOS_PREDIAL.PAG_VAL          <> TMP_ULTIMO_ANO.ULT_VALOR  
                          OR IP_USUARIOS_PREDIAL.NUM_COM          <> TMP_ULTIMO_ANO.ULT_FACTURA)
  LOOP 

  MI_INTANOVALIDO := PCK_PREDIAL.FC_PAGOANOVALIDO(UN_COMPANIA => UN_COMPANIA,
                                                  UN_PREDIO   => RSUSUARIO.CODIGO);
  IF MI_INTANOVALIDO <> 0 THEN

    IF NOT MI_ACTUALIZARRUTINA THEN
    BEGIN
      BEGIN

        MI_TABLA     := 'IP_USUARIOS_PREDIAL';
        MI_CAMPOS    := '   PAGO_ANO = ' || MI_INTANOVALIDO || ',
                          MODIFIED_BY = '''|| UN_USUARIO ||''',
                          DATE_MODIFIED = SYSDATE
                        ';
        MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                        AND CODIGO   = ''' || RSUSUARIO.CODIGO || '''
                        ';                    

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);


       MI_MSGERROR(1).CLAVE := 'PAGO_ANO';
       MI_MSGERROR(1).VALOR := MI_INTANOVALIDO;

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                               UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAGO_ANO,
                               UN_REEMPLAZOS => MI_MSGERROR);
  END; 
    ELSE
      MI_CONTRECIBOS := 0;
      FOR RSRECIBO IN( SELECT 
                              DOCNUM,
                              PAQUETEPAG,
                              PREANO,
                              PREVAL,
                              PREANOI,
                              PREANOF,
                              ANULADO,
                              PAGO,
                              PREFECPAG,
                              PAG_BANPAG
                         FROM 
                               IP_RECIBOS_DE_PAGO
                         WHERE 
                               COMPANIA       = UN_COMPANIA
                           AND PRECOD         = RSUSUARIO.CODIGO
                           AND PREANO        >= MI_INTANOVALIDO
                           AND PAGO          <>0
                           AND ANULADO        =0
                           AND NVL(ESABONO,0) IN (0)
                              ) 
      LOOP

        MI_INTANOVALIDO := RSRECIBO.PREANO; 
        IF RSRECIBO.PREFECPAG IS NOT NULL THEN

        BEGIN
          BEGIN

          MI_TABLA := 'IP_FACTURADOS';
          MI_CAMPOS := 'PAGADO    = -1, 
                        PAG_BAN   = ''' || RSRECIBO.PAG_BANPAG || ''',
                        FECHAPAGO = TO_DATE(''' || TO_CHAR(RSRECIBO.PREFECPAG,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                        PREVAL    = ' || RSRECIBO.PREVAL ||',
                        MODIFIED_BY = '''|| UN_USUARIO ||''',
                        DATE_MODIFIED = SYSDATE
                        ';
          MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''             
                        AND CODIGO = ''' || RSUSUARIO.CODIGO || '''';  


          IF RSRECIBO.PREANOI IS NOT NULL AND RSRECIBO.PREANOF IS NOT NULL THEN
            MI_CONDICION :=     MI_CONDICION || '
                            AND PREANO BETWEEN ' || RSRECIBO.PREANOI || '
                            AND ' || RSRECIBO.PREANOF;
          ELSE 
            MI_CONDICION := MI_CONDICION || '
                            AND PREANO = ' || RSRECIBO.PREANO;
          END IF;              
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION);

                MI_MSGERROR(1).CLAVE := 'PAG_BAN';
                MI_MSGERROR(1).VALOR :=  RSRECIBO.PAG_BANPAG;

                MI_MSGERROR(2).CLAVE := 'PREVAL';
                MI_MSGERROR(2).VALOR :=  RSRECIBO.PREVAL;


                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_FACTURADOS,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
        END;

        END IF;  

        MI_INTANOVALIDO := PCK_PREDIAL.FC_PAGOANOVALIDO(UN_COMPANIA => UN_COMPANIA,
                                                        UN_PREDIO   => RSUSUARIO.CODIGO);

        BEGIN 
            BEGIN
            MI_TABLA    := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS   := '    PAGO_ANO = ' || MI_INTANOVALIDO || ',
                                 PAG_FEC = TO_DATE(''' || TO_CHAR(RSRECIBO.PREFECPAG,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                 PAG_VAL = ' || RSRECIBO.PREVAL || ',
                                 NUM_COM = ''' || RSRECIBO.DOCNUM || ''',
                                 PAG_BAN = ''' || RSRECIBO.PAG_BANPAG || '''';              
            MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                            AND CODIGO   = ''' || RSUSUARIO.CODIGO || ''''; 

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_CONDICION => MI_CONDICION); 
            MI_MSGERROR(1).CLAVE := 'NUM_COM';
            MI_MSGERROR(1).VALOR := RSRECIBO.DOCNUM;                                      

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USUPREDIAL,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;

        MI_CONTRECIBOS := MI_CONTRECIBOS + 1;
      END LOOP;

      IF MI_CONTRECIBOS = 0 THEN
        BEGIN 
          BEGIN

            MI_TABLA := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS := '    PAGO_ANO = ' || MI_INTANOVALIDO;

            MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''             
                          AND CODIGO   = ''' || RSUSUARIO.CODIGO || '''';  

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_CONDICION => MI_CONDICION);              

            MI_MSGERROR(1).CLAVE := 'PAGO_ANO';
            MI_MSGERROR(1).VALOR :=  MI_INTANOVALIDO;                                

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAGO_ANO,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;


      END IF;
    END IF;


  END IF;  
MI_CONTUSUARIOS := MI_CONTUSUARIOS +1;  
END LOOP;

BEGIN
  BEGIN
    SELECT NVL(RMOCOD,'NE') RMOCOD 
    INTO MI_MODCCO
    FROM IP_MODICOD 
    WHERE RMOVAR = 'MANTENIMIENTO';

    IF MI_MODCCO IS NULL THEN
      MI_MODCCO := 'NE';
    END IF;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_MODCCO := 'NE';
  END;
  PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA, UN_MODCOD, UN_USUARIO, MI_MODCCO, '-', '-', UN_DESCRIPCION);
END;

IF MI_CONTUSUARIOS = 0 THEN
  MI_RTA := 'TB_TB1200';
  RETURN MI_RTA;
END IF;

MI_RTA := 'MSM_PROCESO_EJECUTADO';
RETURN MI_RTA;

END FC_ACT_ULTVIGCANCELADA;

PROCEDURE PR_VALIDARCODIGOANT
/*
    NAME              : PR_VALIDARCODIGOANT en acces --> ValidarCodigoAnt
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 17/08/2016 
    TIME              : 17:08 PM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : 
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_CODIGO_ANTERIOR  => Codigo Inicial del predio
                        UN_CODIGO_NUEVO     => Codigo Final del predio
    @NAME:  actualizarCodigoAnterior
    @METHOD:  PUT    
  */
(
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO_ANTERIOR         IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_CODIGO_NUEVO            IN PCK_SUBTIPOS.TI_CODPREDIO   
)
AS
  MI_RTA                VARCHAR2(3200 CHAR);
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_PCKDATOS           PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
FOR RS IN (SELECT 
                  CODIGO, 
                  CODIGOANT1, 
                  CODIGOANT2, 
                  CODIGOANT3 

             FROM 
                  IP_USUARIOS_PREDIAL 
            WHERE 
                  COMPANIA = UN_COMPANIA
              AND CODIGO = UN_CODIGO_NUEVO)
LOOP  
  IF RS.CODIGOANT1 = UN_CODIGO_ANTERIOR THEN
    IF RS.CODIGOANT2 IS NOT NULL THEN
      BEGIN 
        BEGIN

          MI_CAMPOS    := '   CODIGOANT3 =''' || RS.CODIGOANT2 || '''';   

          MI_CONDICION := '     COMPANIA = ''' || UN_COMPANIA || '''             
                            AND CODIGO   = ''' || UN_CODIGO_NUEVO || '''';                                  

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION); 
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := 'CODIGOANT3';

          MI_MSGERROR(2).CLAVE := 'CODIGOANT';
          MI_MSGERROR(2).VALOR :=  RS.CODIGOANT2;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_COD_PRE,
                                   UN_REEMPLAZOS => MI_MSGERROR);                                      
        END; 
    END IF;

    IF RS.CODIGOANT1 IS NOT NULL THEN
      BEGIN
        BEGIN
          MI_CAMPOS := '      CODIGOANT2 =''' || RS.CODIGOANT1|| '''';               
          MI_CONDICION := '     COMPANIA = ''' || UN_COMPANIA || '''             
                            AND CODIGO   = ''' || UN_CODIGO_NUEVO || '''';  

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION); 
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := 'CODIGOANT2';

          MI_MSGERROR(2).CLAVE := 'CODIGOANT';
          MI_MSGERROR(2).VALOR :=  RS.CODIGOANT1;   

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_COD_PRE,
                                   UN_REEMPLAZOS => MI_MSGERROR);                                      
        END;            
    END IF;

    BEGIN
      BEGIN
        MI_CAMPOS    := '  CODIGOANT1 = ''' || UN_CODIGO_ANTERIOR|| '''';                
        MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || '''             
                         AND CODIGO   = ''' || UN_CODIGO_NUEVO || '''';                       

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);  

        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := 'CODIGOANT1';

        MI_MSGERROR(2).CLAVE := 'CODIGOANT';
        MI_MSGERROR(2).VALOR :=  UN_CODIGO_ANTERIOR;                                        

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_COD_PRE,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;                                            
  ELSE

    BEGIN
      BEGIN
        MI_CAMPOS    := ' CODIGOANT1 =''' || UN_CODIGO_ANTERIOR|| '''';                
        MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || '''             
                        AND CODIGO   = ''' || UN_CODIGO_NUEVO || '''';                                  

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);      

        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := 'CODIGOANT1';

        MI_MSGERROR(2).CLAVE := 'CODIGOANT';
        MI_MSGERROR(2).VALOR :=  UN_CODIGO_ANTERIOR;                                    

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_COD_PRE,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;    
  END IF; 
END LOOP;
END PR_VALIDARCODIGOANT;				 


PROCEDURE PR_HAGATRASLADO 
/*
    NAME              : PR_HAGATRASLADO en acces --> HagaTraslado
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 20/08/2016 
    TIME              : 08:08 AM     
    MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS/ ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 
    DESCRIPTION       : 
    MODIFICATIONS     : Se agrega la insercion para la tabla IP_ACUERDOS / 
                        SE AJUSTA PARA QUE ALMACENE LOS CAMPOS DE AUDITORIA EN LAS TABLAS, 
                        SE ELIMINA LOS RERTORNOS TB CON EL FIN DE QUE SOLO LLAME AL PROCEDIMIENTO 
                        DE AUDITORIA CUANDO EL PROCESO SE EJECUTE COMPLETAMENTE. POR ELLO LA FUNCION SE PASO A PROCEDIMIENTO
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_CODIGO_ANTERIOR  => Codigo Inicial del predio
                        UN_CODIGO_NUEVO     => Codigo Final del predio
                        UN_USUARIO          => Usuario el cual va hacer el proceso.
    @NAME:  realizarTrasladoNuevoCodigo
    @METHOD:  PUT   
  */
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO_ANTERIOR    IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_CODIGO_NUEVO       IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
  UN_OPCION_REGISTRO    IN PCK_SUBTIPOS.TI_LOGICO
)
AS 
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_PCKDATOS           PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CANTIDAD           NUMBER := 0;
BEGIN
-- se hace la copia de los registros del codigo anterior al nuevo en la tabla IP_ACUERDOS

  BEGIN 
    IF UN_OPCION_REGISTRO = 0 THEN 
      SELECT COUNT(*) INTO MI_CANTIDAD
      FROM IP_ACUERDOS
      WHERE COMPANIA = ''||UN_COMPANIA||''
      AND PREDIO = ''||UN_CODIGO_ANTERIOR||'';

      IF MI_CANTIDAD != 0 THEN
        MI_CANTIDAD := -1;
      END IF;
    ELSE
      MI_CANTIDAD := -1;
      MI_CONDICION  := 'COMPANIA   = '''||UN_COMPANIA||'''
                        AND CODIGO = '''||UN_CODIGO_NUEVO||''' ';     
      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_ELIMFACTURADO);  
      END;

      MI_CAMPOS     := 'PAGO_ANO = 0,
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY  = '''||UN_USUARIO||''' ';
      MI_CONDICION  := 'COMPANIA    = '''||UN_COMPANIA||''' 
                        AND CODIGO  = '''||UN_CODIGO_NUEVO||''' '; 
      BEGIN
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    END IF;

    IF MI_CANTIDAD = -1 THEN
        SELECT COUNT(*) INTO MI_CANTIDAD
        FROM IP_ACUERDOS
        WHERE COMPANIA = ''||UN_COMPANIA||''
        AND PREDIO = ''||UN_CODIGO_NUEVO||'';

        IF MI_CANTIDAD = 0 THEN
            MI_CAMPOS := 'ANULADO,                APLICA_DSCESP
                        , CANCELADO             , CODIGOACUERDO
                        , COMPANIA              , CREADO_POR
                        , CREATED_BY            , DATE_CREATED
                        , DATE_MODIFIED         , DIRECCION_RESP
                        , ELIMINADO_POR         , FECHAACUERDO
                        , FECHAANULADO          , ID_RESP,INTERES
                        , MODIFIED_BY           , MONTOCAPITAL
                        , MONTOINTERESES        , NCUOTAS
                        , NOMBRE_RESP           , NUMERO_ORDEN
                        , PREANO                , PREANOI
                        , PREDIO                , RECARGO
                        , RECIBO_SOPORTE        , RESOLUCION
                        , SUCURSAL              , TELEFONO_RESP' ; 

              MI_VALORES := 'SELECT ANULADO           , APLICA_DSCESP
                       , CANCELADO                    , CODIGOACUERDO
                       , COMPANIA                     , CREADO_POR
                       , CREATED_BY                   , DATE_CREATED
                       , DATE_MODIFIED                , DIRECCION_RESP
                       , ELIMINADO_POR                , FECHAACUERDO
                       , FECHAANULADO                 , ID_RESP
                       , INTERES                      , MODIFIED_BY
                       , MONTOCAPITAL                 , MONTOINTERESES
                       , NCUOTAS                      , NOMBRE_RESP
                       , NUMERO_ORDEN                 , PREANO
                       , PREANOI                      , '''||UN_CODIGO_NUEVO||'''
                       , RECARGO                      , RECIBO_SOPORTE
                       , RESOLUCION                   , SUCURSAL
                       , TELEFONO_RESP  
                         FROM IP_ACUERDOS 
                         WHERE COMPANIA = '''||UN_COMPANIA||'''
                         AND PREDIO     = '''||UN_CODIGO_ANTERIOR||''' ';

              BEGIN
                  PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'IP_ACUERDOS',
                                                       UN_ACCION  => 'IS',
                                                       UN_CAMPOS  => MI_CAMPOS,
                                                       UN_VALORES => MI_VALORES);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

        END IF;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
        MI_MSGERROR (1).CLAVE := 'PREDANTERIOR';
        MI_MSGERROR (1).VALOR := UN_CODIGO_ANTERIOR;
        MI_MSGERROR (2).CLAVE := 'PREDNUEVO';
        MI_MSGERROR (2).VALOR := UN_CODIGO_NUEVO;
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                 UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_INSERTIPACUER, 
                                 UN_REEMPLAZOS  => MI_MSGERROR);  
  END;


 BEGIN 
  BEGIN
  MI_CAMPOS  :=        ' COMPANIA,                  CODIGO,
                         NUMERO_ORDEN,              PREANO,
                         CREATED_BY,                DATE_CREATED,
                         DATE_MODIFIED,             MODIFIED_BY,     
                         TRPCOD,                    TRPRAN, 
                         PREFEC,                     DOCNUM,
                         PAG_BAN,                   PREVAL,
                         USUARIO_CREADOR_REGISTRO,  FECHA_CREACION_REGISTRO,
                         USUARIO_MODIFICO_REGISTRO, FECHA_MODIFICACION_REGISTRO,
                         AVALUO,                    TRPFOR,
                         PREIND,                    SALDOCREDITO,
                         ABONOS,                    INDHIP,
                         INDCAR,                    INDEXE,
                         ANTERIOR_RURAL,            TOTAL,
                         C1,C2,                     C3,C4,
                         C5,C6,                     C7,C8,
                         C9,C10,                    C11,C12,
                         C13,C14,                   C15,C16,
                         C17,C18,                   C19,C20, 
                         PAGADO,                    TRPPOR,
                         NOCOBRADO,                 OBSERVACIONES,
                         NUMRESOLUCION,             AVALUOANT,
                         TARIFAANT,                 AREAHE,
                         AREAM2,                    USUARIO, 
                         FECHAPAGO,                 FECHAPRESCRIPCION, 
                         TOTALABONADO,              VALORULTIMOABONO, 
                         FECHAULTIMOABONO,          ALDIAINTERES, 
                         INDPAGO_ACPAG,             FINANCIAR, 
                         CNT,                       C1_CNT,
                         TOTALABONADO1    ,         VALORULTIMOABONO1,
                         FECHAULTIMOABONO1,         ALDIAINTERES1, 
                         TOTALABONADO2,             VALORULTIMOABONO2,
                         FECHAULTIMOABONO2,         ALDIAINTERES2, 
                         DOCNUMABONO,               DOCNUMABONO1, 
                         DOCNUMABONO2,              INDPAGO_CUOTAS,
                         TOTALCUOTA,                PAGFECCUOTA, 
                         PAGNCUOTA,                 DOCNUMCUOTA, 
                         TOTALCUOTA1,               PAGFECCUOTA1, 
                         PAGNCUOTA1,                DOCNUMCUOTA1, 
                         TOTALCUOTA2,               PAGFECCUOTA2, 
                         PAGNCUOTA2,                DOCNUMCUOTA2, 
                         C18_CNT,                   TOTALABONO1, 
                         TOTALABONO2,               PREANO_EXONERADO, 
                         C1_CPY,                    IND_C1CPY, 
                         ALDIAINTERESCAR,           ALDIAINTERES1CAR, 
                         ALDIAINTERES2CAR,          C3_CPY,
                         IND_C3CPY,                 SINDESCUENTOS, 
                         AUDITADESC,                DESC_INTERES, 
                         C1_EXE,                    C2_EXE, 
                         C3_EXE,                    C4_EXE, 
                         INDEXEOTROS,               C13_EXE, 
                         C14_EXE,                   C15_EXE, 
                         C16_EXE,                   C17_EXE, 
                         C18_EXE,                   C19_EXE, 
                         C20_EXE,                   DESESPC13, 
                         DESESPC2,                  DESESPC4, 
                         DESESPC14,                 DESESPC15, 
                         DESESPC16,                 DESESPC17, 
                         DESESPC18,                 DESESPC19, 
                         DESESPC20,                 IND_PROCESOJUD, 
                         AREA_CONSTRUIDA';

  MI_VALORES := 'SELECT 
                        COMPANIA,                  '''|| UN_CODIGO_NUEVO ||''',
                        NUMERO_ORDEN,              PREANO,
                        CREATED_BY,                DATE_CREATED,
                        DATE_MODIFIED,             MODIFIED_BY,
                        TRPCOD,                    TRPRAN, 
                        PREFEC,                    DOCNUM, 
                        PAG_BAN,                   PREVAL, 
                        USUARIO_CREADOR_REGISTRO,  FECHA_CREACION_REGISTRO, 
                        USUARIO_MODIFICO_REGISTRO, FECHA_MODIFICACION_REGISTRO,
                        AVALUO,                    TRPFOR, 
                        PREIND,                    SALDOCREDITO, 
                        ABONOS,                    INDHIP, 
                        INDCAR,                    INDEXE,   
                        ANTERIOR_RURAL,            TOTAL, 
                        C1, C2,                    C3, C4,
                        C5, C6,                    C7, C8,
                        C9, C10,                   C11, C12,
                        C13, C14,                  C15, C16, 
                        C17, C18,                  C19, C20, 
                        PAGADO,                    TRPPOR, 
                        NOCOBRADO,                 OBSERVACIONES, 
                        NUMRESOLUCION,             AVALUOANT, 
                        TARIFAANT,                 AREAHE, 
                        AREAM2,                    USUARIO, 
                        FECHAPAGO,                 FECHAPRESCRIPCION, 
                        TOTALABONADO,              VALORULTIMOABONO, 
                        FECHAULTIMOABONO,          ALDIAINTERES, 
                        INDPAGO_ACPAG,             FINANCIAR, 
                        CNT,                       C1_CNT, 
                        TOTALABONADO1,             VALORULTIMOABONO1, 
                        FECHAULTIMOABONO1,         ALDIAINTERES1, 
                        TOTALABONADO2,             VALORULTIMOABONO2, 
                        FECHAULTIMOABONO2,         ALDIAINTERES2, 
                        DOCNUMABONO,               DOCNUMABONO1, 
                        DOCNUMABONO2,              INDPAGO_CUOTAS, 
                        TOTALCUOTA,                PAGFECCUOTA, 
                        PAGNCUOTA,                 DOCNUMCUOTA, 
                        TOTALCUOTA1,               PAGFECCUOTA1, 
                        PAGNCUOTA1,                DOCNUMCUOTA1, 
                        TOTALCUOTA2,               PAGFECCUOTA2, 
                        PAGNCUOTA2,                DOCNUMCUOTA2, 
                        C18_CNT,                   TOTALABONO1, 
                        TOTALABONO2,               PREANO_EXONERADO, 
                        C1_CPY,                    IND_C1CPY, 
                        ALDIAINTERESCAR,           ALDIAINTERES1CAR, 
                        ALDIAINTERES2CAR,          C3_CPY, 
                        IND_C3CPY,                 SINDESCUENTOS, 
                        AUDITADESC,                DESC_INTERES, 
                        C1_EXE,                    C2_EXE, 
                        C3_EXE,                    C4_EXE, 
                        INDEXEOTROS,               C13_EXE, 
                        C14_EXE,                   C15_EXE, 
                        C16_EXE,                   C17_EXE, 
                        C18_EXE,                   C19_EXE, 
                        C20_EXE,                   DESESPC13, 
                        DESESPC2,                  DESESPC4, 
                        DESESPC14,                 DESESPC15, 
                        DESESPC16,                 DESESPC17, 
                        DESESPC18,                 DESESPC19, 
                        DESESPC20,                 IND_PROCESOJUD, 
                        AREA_CONSTRUIDA

                   FROM 
                        IP_FACTURADOS
                  WHERE 
                        COMPANIA     = ''' || UN_COMPANIA || '''
                    AND CODIGO       = '''|| UN_CODIGO_ANTERIOR ||'''
                    AND NUMERO_ORDEN = ''' || PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL || '''';


   MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                    UN_ACCION    => 'IS',
                                    UN_CAMPOS    => MI_CAMPOS, 
                                    UN_VALORES   => MI_VALORES);                   

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_FACT);                                      
    END;   

  IF MI_PCKDATOS > 0 THEN
    BEGIN 
      BEGIN
        MI_CAMPOS    := ' PROCESO_DE_COBRO = 0,
                          DATE_MODIFIED =SYSDATE,
                          MODIFIED_BY  = '''||UN_USUARIO||'''';              

        MI_CONDICION := '       COMPANIA = ''' || UN_COMPANIA || '''             
                            AND CODIGO   = ''' || UN_CODIGO_ANTERIOR || '''';

        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION);   

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRO_COBRO);                                      
    END;                                 

    FOR RSUP IN(SELECT 
                       PAGO_ANO,
                       PAG_FEC,
                       NUM_COM,
                       PAG_VAL,
                       PAG_BAN,
                       OBSERVACIONES_DE_PAGOS,
                       OBSERVACIO,
                       PROCESO_DE_COBRO PROCESO_DE_COBROF,
                       PAGO_ANO1,
                       PAG_FEC1,
                       NUM_COM1,
                       PAG_VAL1,
                       PAG_BAN1,
                       PAGO_ANO2,
                       PAG_FEC2,
                       NUM_COM2,
                       PAG_VAL2,
                       PAG_BAN2,
                       PAGO_ANO_ABONO,
                       FECHAULTIMOABONO,
                       VALORULTIMOABONO,
                       FACTURAULTIMOABONO,
                       BANCOULTIMOABONO,
                       PAGO_ANO_ABONO1,
                       FECHAULTIMOABONO1,
                       VALORULTIMOABONO1,
                       FACTURAULTIMOABONO1,
                       BANCOULTIMOABONO1,
                       PAGO_ANO_ABONO2,
                       FECHAULTIMOABONO2,
                       VALORULTIMOABONO2,
                       FACTURAULTIMOABONO2,
                       BANCOULTIMOABONO2
                     FROM 
                            IP_USUARIOS_PREDIAL
                     WHERE  
                            CODIGO= UN_CODIGO_ANTERIOR)
    LOOP
    BEGIN 
      BEGIN
          MI_CAMPOS := ' PAGO_ANO               = ' || NVL(RSUP.PAGO_ANO, 0) || ',
                         NUM_COM                = ''' || RSUP.NUM_COM || ''', 
                         PAG_VAL                = ' || NVL(RSUP.PAG_VAL, 0) || ', 
                         PAG_BAN                = ''' || RSUP.PAG_BAN || ''', 
                         PAG_FEC                = TO_DATE(''' || TO_CHAR(RSUP.PAG_FEC,'DD/MM/YYYY') || ''',''DD/MM/YYYY''), 
                         PAGO_ANO1              = ' || NVL(RSUP.PAGO_ANO1, 0) || ', 
                         NUM_COM1               = ''' || RSUP.NUM_COM1 || ''', 
                         PAG_VAL1               =  '|| NVL(RSUP.PAG_VAL1, 0) || ', 
                         PAG_BAN1               = ''' || RSUP.PAG_BAN1 || ''', 
                         PAG_FEC1               =  TO_DATE(''' || TO_CHAR(RSUP.PAG_FEC1,'DD/MM/YYYY') || ''',''DD/MM/YYYY''), 
                         PAGO_ANO2              = ' || NVL(RSUP.PAGO_ANO2, 0) || ', 
                         NUM_COM2               = ''' || RSUP.NUM_COM2 || ''', 
                         PAG_VAL2               = ' || NVL(RSUP.PAG_VAL2, 0) ||' ,
                         PAG_BAN2               = ''' || RSUP.PAG_BAN2 || ''', 
                         PAG_FEC2               =  TO_DATE(''' || TO_CHAR(RSUP.PAG_FEC2,'DD/MM/YYYY') || ''',''DD/MM/YYYY''), 
                         OBSERVACIONES_DE_PAGOS = ''.' || RSUP.OBSERVACIONES_DE_PAGOS || ''', 
                         OBSERVACIO             = ''CAMBIO DE CEDULA (ANT ' || UN_CODIGO_ANTERIOR || '). ' || RSUP.OBSERVACIO || ''', 
                         PROCESO_DE_COBRO       = ' || NVL(RSUP.PROCESO_DE_COBROF, 0)||',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY  = '''||UN_USUARIO||''' ';              

          MI_CONDICION := '     COMPANIA = ''' || UN_COMPANIA || '''             
                            AND CODIGO   = ''' || UN_CODIGO_NUEVO || '''';   

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_CONDICION => MI_CONDICION);  


      MI_MSGERROR(1).CLAVE := 'PAGO_ANO';
      MI_MSGERROR(1).VALOR := NVL(RSUP.PAGO_ANO, 0);

      MI_MSGERROR(2).CLAVE := 'NUM_COM';
      MI_MSGERROR(2).VALOR :=  RSUP.NUM_COM;   

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRED_USU,                                      
                               UN_REEMPLAZOS => MI_MSGERROR);        
    END; 


      PCK_PREDIAL_COM2.PR_VALIDARCODIGOANT(UN_COMPANIA        => UN_COMPANIA,
                                           UN_CODIGO_ANTERIOR => UN_CODIGO_ANTERIOR,
                                           UN_CODIGO_NUEVO    => UN_CODIGO_NUEVO);

      IF MI_PCKDATOS > 0 THEN

        SELECT 
               COUNT(*) INTO MI_PCKDATOS
          FROM 
               IP_FACTURADOSABONOS 
         WHERE 
               COMPANIA = UN_COMPANIA
           AND CODIGO   = UN_CODIGO_ANTERIOR;

        IF MI_PCKDATOS > 0 THEN   
          BEGIN
            BEGIN

                MI_CAMPOS :=          'COMPANIA,     
                                       CODIGO,
                                       NUMERO_ORDEN,  
                                       PREANO,
                                       DOCNUM,
                                       PAGADO,
                                       C1,C2,
                                       C3,C4,
                                       C5,C6,
                                       C7,C8,
                                       C9,C10,
                                       C11,C12,
                                       C13,C14,
                                       C15,C16,
                                       C17,C18, 
                                       C19,C20,
                                       TOTAL,
                                       FECHAFACTURADO,
                                       PAG_BAN,       
                                       FECHAPAGO,
                                       OBSERVACIONES,
                                       CREATED_BY,
                                       DATE_CREATED,
                                       DATE_MODIFIED,
                                       MODIFIED_BY';

                MI_VALORES := ' SELECT 
                                        COMPANIA,   
                                        ''' || UN_CODIGO_NUEVO  || ''',
                                        NUMERO_ORDEN,
                                        PREANO,
                                        DOCNUM,
                                        PAGADO,
                                        C1,C2,
                                        C3,C4,
                                        C5,C6,
                                        C7,C8,
                                        C9,C10,
                                        C11,C12,
                                        C13,C14,
                                        C15,C16,
                                        C17,C18,
                                        C19,C20,
                                        TOTAL,
                                        FECHAFACTURADO,
                                        PAG_BAN,
                                        FECHAPAGO,
                                        OBSERVACIONES,
                                        CREATED_BY,
                                        DATE_CREATED,
                                        DATE_MODIFIED,
                                        MODIFIED_BY

                                  FROM 
                                       IP_FACTURADOSABONOS 
                                 WHERE
                                       COMPANIA = ''' || UN_COMPANIA || '''
                                   AND CODIGO   = ''' || UN_CODIGO_ANTERIOR ||'''';


                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOSABONOS',
                                                 UN_ACCION    => 'IS',
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_VALORES   => MI_VALORES);  

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_ABO);                                      
          END;   


          IF MI_PCKDATOS > 0 THEN
          BEGIN
            BEGIN
                MI_CAMPOS := 'PAGO_ANO_ABONO        = ' || NVL(RSUP.PAGO_ANO_ABONO, 0) || ', 
                              FACTURAULTIMOABONO    = ''' || RSUP.FACTURAULTIMOABONO || ''', 
                              VALORULTIMOABONO      = ' || NVL(RSUP.VALORULTIMOABONO, 0) || ', 
                              BANCOULTIMOABONO      = ''' || RSUP.BANCOULTIMOABONO || ''', 
                              FECHAULTIMOABONO      = TO_DATE(''' || TO_CHAR(RSUP.FECHAULTIMOABONO,'DD/MM/YYYY') || ''',''DD/MM/YYYY''), 
                              PAGO_ANO_ABONO1       = ' || NVL(RSUP.PAGO_ANO_ABONO1, 0) || ',
                              FACTURAULTIMOABONO1   = ''' || RSUP.FACTURAULTIMOABONO1 || ''',
                              VALORULTIMOABONO1     = ' || NVL(RSUP.VALORULTIMOABONO1, 0) || ',
                              BANCOULTIMOABONO1     = ''' || RSUP.BANCOULTIMOABONO1 || ''',
                              FECHAULTIMOABONO1     = TO_DATE(''' || TO_CHAR(RSUP.FECHAULTIMOABONO1,'DD/MM/YYYY') || ''',''DD/MM/YYYY''), 
                              PAGO_ANO_ABONO2       = ' || NVL(RSUP.PAGO_ANO_ABONO2, 0) || ', 
                              FACTURAULTIMOABONO2   = ''' || RSUP.FACTURAULTIMOABONO2 || ''', 
                              VALORULTIMOABONO2     = ' || NVL(RSUP.VALORULTIMOABONO2, 0) || ', 
                              BANCOULTIMOABONO2     = ''' || RSUP.BANCOULTIMOABONO2 || ''', 
                              FECHAULTIMOABONO2     = TO_DATE(''' || TO_CHAR(RSUP.FECHAULTIMOABONO2,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY  = '''||UN_USUARIO||''' ';

                MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''             
                              AND CODIGO   = ''' || UN_CODIGO_NUEVO || ''''; 

                MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);    

                MI_MSGERROR(1).CLAVE := 'PAGO_ANO_ABONO';
                MI_MSGERROR(1).VALOR := NVL(RSUP.PAGO_ANO_ABONO, 0);

                MI_MSGERROR(2).CLAVE := 'FACTURAULTIMOABONO';
                MI_MSGERROR(2).VALOR :=  RSUP.FACTURAULTIMOABONO;    

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_ABONOS,                                      
                                       UN_REEMPLAZOS => MI_MSGERROR);        
            END; 


          END IF;
        END IF;
        BEGIN 
          BEGIN
            MI_CAMPOS    := ' INDBORRADO = -1,
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY  = '''||UN_USUARIO||''' ';              
            MI_CONDICION := '     COMPANIA = ''' || UN_COMPANIA || '''             
                              AND CODIGO   = ''' || UN_CODIGO_ANTERIOR || '''';                        

            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION); 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_INDBORR,                                      
                                   UN_REEMPLAZOS => MI_MSGERROR);        
        END;                              

     END IF;
    END LOOP;

  END IF;


            PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA,
                                   UN_CODMOD      => UN_CODIGO_NUEVO,
                                   UN_OPEMOD      => UN_USUARIO,
                                   UN_CCOMOD      => '605',
                                   UN_VANMOD      => UN_CODIGO_ANTERIOR,
                                   UN_VNUMOD      => UN_CODIGO_NUEVO,
                                   UN_DESCRIPCION => 'Se realizó el cambio de cédula catastral exitosamente');

END PR_HAGATRASLADO;


PROCEDURE PR_REGISTRARPAGOANOANTE
/*
    NAME              : PR_REGISTRARPAGOANOANTE en access --> PREDIAL_REGISTROPAGOANOSANTER boton Registrar
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 23/08/2016 
    TIME              : 08:08 AM     
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 
    DESCRIPTION       : Proceso de registro de pagos de años anteriores 
    MODIFICATIONS     : Cambio de los retornos TB por excepciones,adicion de los campos de auditori de los insert y update 
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_V_50             => Es un booleano que valida si es Pago Por Años Anteriores o no.
                        UN_V_DAMNIFICADOS   => Es un booleano que vaida si es damnificado.
                        UN_FECHACORTE       => Valida la ultima fecha de pago.
                        UN_CODPREDIO        => Codigo del predio asigando al usuario.
                        UN_ANOFIN           => Año  maximo para realizar el proceso
                        UN_CODPREDIO

    @NAME:  registroDePagoVigenciaAnterior
    @METHOD:  GET   
  */
(
    UN_COMPANIA				  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_V_50					    IN VARCHAR2,
    UN_V_DAMNIFICADOS	  IN VARCHAR2,
    UN_FECHACORTE			  IN VARCHAR2,
    UN_CODPREDIO			  IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CODIGOBANCO		  IN IP_BANCOS.CODIGOBANCO%TYPE,
    UN_ANOFIN				    IN PCK_SUBTIPOS.TI_ANIO,
    UN_NRORECIBO			  IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_TOTALPAGADO			IN PCK_SUBTIPOS.TI_DOBLE,
    UN_TARIFAAP				  IN VARCHAR2,
    UN_TRPCOD				    IN VARCHAR2,
    UN_OBSERVACIONES		IN VARCHAR2,
    UN_USUARIO				  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
	MI_VLRRETORNO				        VARCHAR2(4000 CHAR);
	MI_OBS_PAGOS				        VARCHAR2(4000 CHAR);
	MI_PAGO_ANO					        PCK_SUBTIPOS.TI_ANIO;
	MI_OBSERVACIONES_DE_PAGOS	  VARCHAR2(4000 CHAR);
	MI_CAMPOS                 	PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                 	PCK_SUBTIPOS.TI_VALORES;
	MI_CONDICION              	PCK_SUBTIPOS.TI_CONDICION;
	MI_RTA		                	PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
	CURSOR MI_CURUSUPRED IS SELECT 
                                 PAGO_ANO,
                                 OBSERVACIONES_DE_PAGOS 
                            FROM 
                                 IP_USUARIOS_PREDIAL
                           WHERE 
                                 CODIGO       = UN_CODPREDIO
                             AND NUMERO_ORDEN = PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL;
BEGIN
	MI_OBS_PAGOS := 'Pago Por Años Anteriores';
	IF UN_V_50 = 'true' THEN
    MI_OBS_PAGOS := MI_OBS_PAGOS || ' Pago Damnificado';
  END IF;

  IF UN_V_DAMNIFICADOS = 'true' THEN
    MI_OBS_PAGOS := MI_OBS_PAGOS || ' Pago 50%';
  END IF;

  IF NVL(UN_FECHACORTE, ' ') = ' ' OR NVL(UN_CODPREDIO, ' ') = ' ' OR NVL(UN_CODIGOBANCO, ' ') = ' ' OR NVL(UN_ANOFIN, -1) = -1 OR NVL(UN_NRORECIBO, ' ') = ' ' OR NVL(UN_TOTALPAGADO, -1) = -1 OR NVL(UN_TARIFAAP, ' ') = ' ' THEN
   BEGIN
    BEGIN

    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_FALTADATOS
             );  
   END; 
  ELSE
    IF NVL(UN_TRPCOD, NULL) IS NULL THEN
    BEGIN
     BEGIN

        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
     END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_PREDIOSINTARIFA
             );  
    END; 
    END IF;
  END IF;

  OPEN MI_CURUSUPRED;
  FETCH MI_CURUSUPRED INTO MI_PAGO_ANO, MI_OBSERVACIONES_DE_PAGOS;

  IF (MI_CURUSUPRED%FOUND) THEN
    IF NVL(MI_PAGO_ANO, 0) = 0 THEN
      BEGIN
       BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
       END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_PREDIOSINANIOPAG
             );  
      END; 
    END IF;

    MI_OBS_PAGOS := MI_OBS_PAGOS || '-' || MI_OBSERVACIONES_DE_PAGOS;
    BEGIN
      BEGIN
          MI_CAMPOS := 'PAG_FEC               = TO_DATE('''||UN_FECHACORTE||'''), 
                        NUM_COM               = ''' || UN_NRORECIBO|| ''',
                        PAG_VAL               = ' || (UN_TOTALPAGADO) || ',
                        PAGO_ANO              = ' || UN_ANOFIN || ',
                        INDPAGOANOANTE        = -1, 
                        OBSERVACIONES_ACUERDO = ''' || UN_OBSERVACIONES || ''' , 
                        PAG_BAN               = ''' || UN_CODIGOBANCO || ''' ,
                        MODIFIED_BY     = '''||UN_USUARIO||''' ,
                        DATE_MODIFIED   = SYSDATE';

          MI_CONDICION := '   CODIGO       = ''' ||  UN_CODPREDIO || '''' || ' 
                          AND NUMERO_ORDEN = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''';

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'IP_USUARIOS_PREDIAL',
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_CONDICION => MI_CONDICION); 

          MI_MSGERROR(1).CLAVE := 'NUM_COM';
          MI_MSGERROR(1).VALOR := UN_NRORECIBO;

          MI_MSGERROR(2).CLAVE := 'PAG_VAL';
          MI_MSGERROR(2).VALOR :=  UN_TOTALPAGADO;    

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAG_ANT,                                      
                                 UN_REEMPLAZOS => MI_MSGERROR);        
      END; 

    SELECT COUNT('X') INTO MI_RTA
    FROM IP_FACTURADOS
    WHERE  CODIGO = UN_CODPREDIO
    AND PREANO = UN_ANOFIN;

    IF MI_RTA = 0 THEN

    BEGIN
      BEGIN
        MI_CAMPOS  := 'CODIGO,
                       NUMERO_ORDEN, 
                       PREANO, 
                       TRPCOD,
                       TRPRAN,
                       PAGADO,
                       OBSERVACIONES,
                       FECHAPAGO, 
                       DOCNUM,
                       PREVAL,
                       PAG_BAN, 
                       COMPANIA,
                       CREATED_BY,
                       DATE_CREATED';

        MI_VALORES := ''''||UN_CODPREDIO||''',
                       '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||''',
                       '||UN_ANOFIN||',
                       '''||UN_TRPCOD||''' ,
                       ''01'',
                       -1,
                       ''Registro de Pago Año Anterior'',
                       TO_DATE('''||UN_FECHACORTE||'''),
                       '''||UN_NRORECIBO||''',
                       '||UN_TOTALPAGADO||',
                       '''||UN_CODIGOBANCO||''',
                       '''||UN_COMPANIA||''', 
                       '''||UN_USUARIO||''',
                       SYSDATE';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                    UN_ACCION    => 'I',
                                    UN_CAMPOS    => MI_CAMPOS, 
                                    UN_VALORES   => MI_VALORES);  

        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODPREDIO;

        MI_MSGERROR(2).CLAVE := 'DOCNUM';
        MI_MSGERROR(2).VALOR :=  UN_NRORECIBO;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_FACTU,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;   

    ELSE
      BEGIN 
        BEGIN
          MI_CAMPOS := ' FECHAPAGO = TO_DATE('''||UN_FECHACORTE||''') , 
                         DOCNUM = ''' || UN_NRORECIBO || ''', 
                         PREVAL = ' || UN_TOTALPAGADO || ', 
                         TOTAL = ' || UN_TOTALPAGADO || ',
                         OBSERVACIONES = ''Registro de Pago Año Anterior '',
                         PAGADO = -1,
                         PAG_BAN = ''' || UN_CODIGOBANCO || ''',
                         COMPANIA = ''' || UN_COMPANIA || ''',
                         MODIFIED_BY     = '''||UN_USUARIO||''' ,
                         DATE_MODIFIED   = SYSDATE';

          MI_CONDICION := '   CODIGO = ''' || UN_CODPREDIO || ''' 
                          AND NUMERO_ORDEN = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||''' 
                          AND PREANO BETWEEN ' || (MI_PAGO_ANO + 1) || ' 
                          AND ' || UN_ANOFIN;


            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS, 
                                        UN_CONDICION => MI_CONDICION);  

            MI_MSGERROR(1).CLAVE := 'DOCNUM';
            MI_MSGERROR(1).VALOR := UN_NRORECIBO;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_DOC_NUM,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;                           


    END IF;
    BEGIN 
      BEGIN
        MI_CAMPOS := ' COMPANIA,
                       NUMCOM,
                       INDACUERDO,
                       PAG_VAL, 
                       ANOFIN, 
                       PAG_BAN, 
                       CODPRED,
                       NUMERO_ORDEN, 
                       FECHAACCESO, 
                       USUARIO, 
                       PAG_FEC,
                       CREATED_BY,
                       DATE_CREATED';

        MI_VALORES := ''''||UN_COMPANIA||''',
                      '''||UN_NRORECIBO||''',
                      0,
                      '||UN_TOTALPAGADO||',
                      '||UN_ANOFIN||',
                      '''||UN_CODIGOBANCO||''',
                      '''||UN_CODPREDIO||''',
                      '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||''',
                      SYSDATE, 
                      '''||UN_USUARIO||''',
                      TO_DATE('''||UN_FECHACORTE||'''),
                      '''||UN_USUARIO||''',
                       SYSDATE';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_BITPAGOSANOSANT',
                                          UN_ACCION    => 'I',
                                          UN_CAMPOS    => MI_CAMPOS, 
                                          UN_VALORES   => MI_VALORES);  


        MI_MSGERROR(1).CLAVE := 'NUMCOM';
        MI_MSGERROR(1).VALOR := UN_NRORECIBO;


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
         END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_BITPAG,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;
  ELSE 
   BEGIN
    BEGIN

      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_PREDIONOENCONTRADO
             ); 
   END; 

  END IF;

END PR_REGISTRARPAGOANOANTE;				 


PROCEDURE PR_AUSUBAVALUOSDOS
/*
    NAME              : PR_AUSUBAVALUOSDOS en access --> SUBABALUOS Despues de actualizar
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 23/08/2016 
    TIME              : 08:08 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Proceso de registro de pagos de años anteriores
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_RESOLUCION       => Numero de Resolucion que se va a actualizar
                        UN_PREANO           => Valida que el año sea menor al ultimo año de pago
                        UN_FECHACORTE       => Parametro que especifica la fecha de corte.
                        UN_CODPREDIO        => Codigo del predio asigando al usuario.
                        UN_CODIGOBANCO      => Condigo de la entidad bancaria.
                        UN_ANOFIN           => Año  maximo para realizar el proceso

    @NAME:  actualizarAvaluoAnterior
    @METHOD:  PUT   
  */
(
	UN_COMPANIA				  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_RESOLUCION			  IN VARCHAR2,
	UN_PREANO				    IN PCK_SUBTIPOS.TI_ANIO,
	UN_CODIGO				    IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_ULTIMO_ANIO			IN PCK_SUBTIPOS.TI_ANIO,
	UN_AVALUO				    IN PCK_SUBTIPOS.TI_DOBLE,
	UN_TRPCOD				    IN VARCHAR2
)
AS
	MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
	MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
	MI_RTA		          PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  IF TO_NUMBER(UN_PREANO) <= TO_NUMBER(UN_ULTIMO_ANIO) THEN
   BEGIN
    BEGIN
      MI_CAMPOS := 'NUMRESOLUCION= ''' || UN_RESOLUCION || ''', 
                    PREIND = ''M'',
                    AVALUOANT='||UN_AVALUO||', 
                    TARIFAANT='''||UN_TRPCOD||''',
                    PAGADO=-1';

      MI_CONDICION := '    PREANO='||UN_PREANO||' 
                       AND CODIGO='''||UN_CODIGO||'''';

      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'IP_FACTURADOS',
                                 UN_ACCION    => 'M',
                                 UN_CAMPOS    => MI_CAMPOS, 
                                 UN_CONDICION => MI_CONDICION);                   

      MI_MSGERROR(1).CLAVE := 'NUMRESOLUCION';
      MI_MSGERROR(1).VALOR := UN_RESOLUCION;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_NUMRESOL,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END; 

  ELSE

    BEGIN
      BEGIN
        MI_CAMPOS := 'NUMRESOLUCION= ''' || UN_RESOLUCION || ''',
                      PREIND = ''M'',
                      AVALUOANT='||UN_AVALUO||',
                      TARIFAANT='''||UN_TRPCOD||''',
                      PAGADO=0';

        MI_CONDICION := '   PREANO='||UN_PREANO||' 
                        AND CODIGO='''||UN_CODIGO||'''';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                    UN_ACCION    => 'M',
                                    UN_CAMPOS    => MI_CAMPOS, 
                                    UN_CONDICION => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'NUMRESOLUCION';
          MI_MSGERROR(1).VALOR := UN_RESOLUCION;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_NUMRESOL,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;                           

  END IF;
END PR_AUSUBAVALUOSDOS;

FUNCTION FC_CLICKPROPIETARIOS
/*
    NAME              : FC_CLICKPROPIETARIOS en access --> RESOLUCIONES boton Propietarios
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 26/08/2016 
    TIME              : 10:08 AM     
    MODIFIER          : JORGE ALBERTO LOZANO CHARRY
    DATE MODIFIED     : 29/08/2016
    DESCRIPTION       : Proceso de llenado de tabla temporal de Copropietarios de un predio
    MODIFICATIONS     : Proceso de llenado de tabla de Copropietarios de un predio (IP_RESOLUCIONES_COPROPIETARIOS)
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicación
                        UN_RESOLUCION       => Numero de Resolucion que se va a actualizar
                        UN_CONSECUTIVO      => El numero del consecutivo el cual se va a actualizar.
                        UN_RESOLUCION       => Numero de la resolucion a actualzar.

    @NAME:  insertarCopropietarios
    @METHOD:  GET   
*/
(
	UN_COMPANIA 		  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_PAIS 			    IN VARCHAR2,
	UN_DEPARTAMENTO   IN VARCHAR2,
	UN_MUNICIPIO 		  IN VARCHAR2,
	UN_RESOLUCION 	  IN VARCHAR2,
  UN_CONSECUTIVO 	  IN NUMBER,
	UN_ANO 				    IN PCK_SUBTIPOS.TI_ANIO,
	UN_CODIGO 			  IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_USUARIO 			  IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2
AS
	MI_VLRRETORNO			VARCHAR2(4000);
	MI_CODIGO			  	PCK_SUBTIPOS.TI_CODPREDIO;
  MI_CONDICION     	PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPOS        	PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       	PCK_SUBTIPOS.TI_VALORES;
  MI_RTA		       	PCK_SUBTIPOS.TI_RTA_ACME;

BEGIN
	BEGIN
		SELECT
           CODIGO INTO MI_CODIGO
      FROM   
           IP_USUARIOS_PREDIAL
     WHERE COMPANIA = UN_COMPANIA 
       AND CODIGO   = UN_CODIGO
       AND ROWNUM   = 1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_CODIGO := NULL;
  END;

  IF MI_CODIGO IS NOT NULL THEN

    BEGIN 
      BEGIN

        MI_CONDICION:='COMPANIA= ''' || UN_COMPANIA  || ''''||
                      'AND CODIGO  = ''' || UN_CODIGO    || '''';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_RESOLUCIONES_COPROPIETARIOS',
                                    UN_ACCION     => 'E',
                                    UN_CONDICION  => MI_CONDICION);    

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DELETE_RESOL,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;                            


    BEGIN
      BEGIN
        MI_CAMPOS :='RESOLUCION,
                     COMPANIA,
                     PAIS,
                     DEPARTAMENTO,
                     MUNICIPIO,
                     CONSECUTIVO,
                     ANO,
                     TIPOREGISTRO,
                     CODIGO, 
                     NUMERO_ORDEN,
                     NOMBRE,
                     TIPO_NIT,
                     NIT,
                     INDBORRADO,
                     DATE_CREATED,
                     CREATED_BY';

         MI_VALORES:= 'SELECT                             
                    ''' || UN_RESOLUCION|| ''',                                
                    ''' || UN_COMPANIA || ''',                              
                    ''' || UN_PAIS || ''',                               
                    ''' || UN_DEPARTAMENTO || ''',                               
                    ''' || UN_MUNICIPIO || ''',                               
                    ' || UN_CONSECUTIVO || ',                                
                    ''' || UN_ANO || ''',                                
                    NULL AS TIPOREGISTRO,                             
                    CODIGO,                              
                    NUMERO_ORDEN,                             
                    NOMBRE,                             
                    TIPO_NIT,                             
                    NIT,                             
                    INDBORRADO,
                    SYSDATE,
                    '''||UN_USUARIO||''' USUARIO
                    FROM                             
                    IP_USUARIOS_PREDIAL                      
                    WHERE                               
                      COMPANIA ='''||UN_COMPANIA||''''||
                    ' AND '||
                    ' CODIGO ='''||UN_CODIGO||'''';

        MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA     => 'IP_RESOLUCIONES_COPROPIETARIOS',
                                     UN_ACCION    => 'IS',
                                     UN_CAMPOS    => MI_CAMPOS, 
                                     UN_VALORES   => MI_VALORES);

      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;                               

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_RESOL,
                               UN_REEMPLAZOS => MI_MSGERROR);                                      
    END;                              

  ELSE
        MI_VLRRETORNO := 'La cedula catastral ' || UN_CODIGO || ' no se encuentra registrada en la Base de Datos.';
  END IF;

  RETURN MI_VLRRETORNO;

END FC_CLICKPROPIETARIOS;

FUNCTION FC_ACEPTARPROPIETARIOS
/*
    NAME              : FC_ACEPTARPROPIETARIOS en access --> FRMPROPIETARIOS_IGAC boton Aceptar
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 26/08/2016 
    TIME              : 10:08 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Proceso para agregar propietarios a un predio
    MODIFICATIONS     :
    PARAMETERS        : UN_RESOLUCION       => Numero de Resolucion que se va a actualizar
                        UN_PAIS             => Codigo del pais al que pertenece el usuario.
                        UN_DEPARTAMENTO     => Codigo del departamente al que pertenece el usuario .
                        UN_MUNICIPIO        => Codigo del municio al qu epertenece el usuario que se va a registrar.
                        UN_CONSECUTIVO      => El numero del consecutivo el cual se va a actualizar.
                        UN_ANO              => Año en el cual se va a registrar el usuario.
                        UN_CODIGO           => Codigo del usuario que se va a registrar.
                        UN_USUARIO          => Usuario al que se va a regisrar.
                        UN_COMPANIA         => Compania de ingreso a la aplicación


    @NAME:  insertarUsuariosResolucionIgac
    @METHOD:  GET   
*/
(
  UN_RESOLUCION		   IN VARCHAR2,
  UN_PAIS      		   IN VARCHAR2,
  UN_DEPARTAMENTO    IN VARCHAR2,
  UN_MUNICIPIO 		   IN VARCHAR2,
  UN_ANO 				     IN PCK_SUBTIPOS.TI_ANIO,
  UN_CODIGO 			   IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_USUARIO 			   IN PCK_SUBTIPOS.TI_USUARIO,
  UN_COMPANIA			   IN PCK_SUBTIPOS.TI_COMPANIA
)
RETURN NUMBER
AS
	MI_VLRRETORNO			  VARCHAR2(4000 CHAR);
  MI_CONSECUTIVO			NUMBER;
  MI_NOMBRE				    VARCHAR2(4000 CHAR);
  MI_TIPO_NIT				  VARCHAR2(4000 CHAR);
  MI_NIT 					    VARCHAR2(4000 CHAR);
  MI_CODIGO				    VARCHAR2(4000 CHAR);
  MI_RESOLUCION			  VARCHAR2(4000 CHAR);
  MI_NUMERO_ORDEN			VARCHAR2(4000 CHAR);
  MI_CAMPOS				    PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES				  PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_PAGO_ANO				  VARCHAR2(4000 CHAR);
  MI_PAG_FEC				  VARCHAR2(4000 CHAR);
  MI_PAG_VAL				  VARCHAR2(4000 CHAR);
  MI_NUM_COM				  VARCHAR2(4000 CHAR);
  MI_PAG_BAN				  VARCHAR2(4000 CHAR);
  MI_CANT_REG				  NUMBER;
  MI_RTA		          PCK_SUBTIPOS.TI_LOGICO;
  RS_USUARIO			 	  IP_USUARIOS_PREDIAL%ROWTYPE;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  RS_PROPIETARIOS     SYS_REFCURSOR;
  MI_AUX              PCK_SUBTIPOS.TI_CAMPOS;
  MI_NODATOS	        PCK_SUBTIPOS.TI_LOGICO;
BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
    SELECT 
            NVL(MAX(CONSECUTIVO)+1, 1) INTO MI_CONSECUTIVO
      FROM    
            IP_IGAC_RESOLUCIONESDET
      WHERE  
            DEPARTAMENTO = UN_DEPARTAMENTO 
        AND MUNICIPIO    = UN_MUNICIPIO 
        AND RESOLUCION   = UN_RESOLUCION 
        AND ANO          = UN_ANO   
        AND COMPANIA     = UN_COMPANIA;

    SELECT 
           * INTO RS_USUARIO
      FROM   
           IP_USUARIOS_PREDIAL
     WHERE CODIGO   = UN_CODIGO
      AND  COMPANIA = UN_COMPANIA
      AND  ROWNUM = 1;

    IF RS_USUARIO.CODIGO IS NOT NULL THEN
    	FOR RS_PROPIETARIOS IN (SELECT * FROM IP_RESOLUCIONES_COPROPIETARIOS WHERE COMPANIA=UN_COMPANIA AND CODIGO=UN_CODIGO)
    	LOOP

    		IF RS_PROPIETARIOS.TIPOREGISTRO = 'M' THEN
          BEGIN
            SELECT 
                   NOMBRE, 
                   TIPO_NIT, 
                   NIT, 
                   RESOLUCION INTO MI_NOMBRE, 
                   MI_TIPO_NIT, 
                   MI_NIT, 
                   MI_RESOLUCION
              FROM   
                   IP_USUARIOS_PREDIAL
             WHERE CODIGO = RS_PROPIETARIOS.CODIGO 
             AND NUMERO_ORDEN =  RS_PROPIETARIOS.NUMERO_ORDEN;
          BEGIN
            BEGIN
              MI_CAMPOS := 'DEPARTAMENTO, 
                            MUNICIPIO, 
                            RESOLUCION, 
                            CONSECUTIVO, 
                            CODIGO,
                            CANCELAINSCRIBE, 
                            TIPOREGISTRO, 
                            REGISTRADO, 
                            NUMEROORDEN, 
                            DIRECCION, 
                            AREATERRENO,
                            NOMBRE, 
                            TIPODOCUMENTO, 
                            NUMERODOCUMENTO, 
                            ANTNOMBRE, 
                            ANTTIPODOCUMENTO, 
                            ANTNUMERODOCUMENTO, 
                            ANTRESOLUCION,
                            AREACONSTRUIDA, 
                            AVALUO, 
                            ANTDIRECCION, 
                            ANTAREATERRENO, 
                            ANTCONSTRUIDA,
                            ANTAVALUO,
                            AREATERENOM2, 
                            AREAHECTARES, 
                            ANTAREAHECTARES,
                            TARIFA, 
                            ANTTARIFA, 
                            ULTIMO_ANIO, 
                            CODIGO_PADRE, 
                            ANO, 
                            COMPANIA, 
                            PAIS, 
                            VIGENCIA, 
                            ANO_CONSTRUCCION, 
                            TRPRAN,
                            DATE_CREATED,
                            CREATED_BY';

              MI_VALORES := ''''||UN_DEPARTAMENTO||''',
                            '''||UN_MUNICIPIO||''', 
                            '''||UN_RESOLUCION||''', 
                            '||MI_CONSECUTIVO||', 
                            '''||RS_PROPIETARIOS.CODIGO||''', 
                            '''||RS_PROPIETARIOS.TIPOREGISTRO||''', 
                            ''1'', 
                            -1, 
                            '''||RS_PROPIETARIOS.NUMERO_ORDEN||''', 
                            '''||RS_USUARIO.Direccion||''', 
                            '''||RS_USUARIO.AREA_M2||''',
                            '''||RS_PROPIETARIOS.NOMBRE||''', 
                            '''||RS_PROPIETARIOS.TIPO_NIT||''', 
                            '''||RS_PROPIETARIOS.NIT||''',
                            '''||MI_NOMBRE||''' , 
                            '''||MI_TIPO_NIT||''', 
                            '''||MI_NIT||''', 
                            '''||MI_RESOLUCION||''', 
                            '||RS_USUARIO.AREA_CONSTRUIDA||', 
                            '''||RS_USUARIO.AVALUO_ANO||''', 
                            '''||RS_USUARIO.DIRECCION||''',
                            '||RS_USUARIO.AREA_M2||', 
                            '''||RS_USUARIO.AREA_CONSTRUIDA||''', 
                            '||RS_USUARIO.AVALUO_ANO||', 
                            '|| RS_USUARIO.AREA_M2||', 
                            '||RS_USUARIO.AREA_HA||', 
                            '||RS_USUARIO.AREA_HA||', 
                            '''||RS_USUARIO.TRPCOD||''', 
                            '''||RS_USUARIO.TRPCOD||''', 
                            '||RS_USUARIO.PAGO_ANO||', 
                            '''||RS_PROPIETARIOS.CODIGO||''', 
                            '||UN_ANO||',
                            '''||UN_COMPANIA||''',
                            '''||UN_PAIS||''',
                            '||RS_USUARIO.VIGENCIA||', 
                            '||RS_USUARIO.ANO_CONSTRUCCION||', 
                            '''||RS_USUARIO.TRPRAN||''',
                            SYSDATE,
                            '''||UN_USUARIO||'''';

             MI_RTA	:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                         UN_ACCION    => 'I',
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_VALORES   => MI_VALORES);

              MI_MSGERROR(1).CLAVE := 'RESOLUCION';
              MI_MSGERROR(1).VALOR := UN_RESOLUCION;                               

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_IGAC,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;                            
          BEGIN
            BEGIN
                MI_CAMPOS := '  TIPO_NIT        = '''||RS_PROPIETARIOS.TIPO_NIT||''',
                                NIT             = '''||RS_PROPIETARIOS.NIT||''',
                                NOMBRE          = '''||RS_PROPIETARIOS.NOMBRE||''', 
                                RESOLUCION      = '''||UN_RESOLUCION||''', 
                                INDBORRADO      = '||RS_PROPIETARIOS.INDBORRADO||',
                                AREA_M2         = '||RS_USUARIO.AREA_M2||', 
                                AREA_CONSTRUIDA = '||RS_USUARIO.AREA_CONSTRUIDA||',
                                AREA_HA         = '||RS_USUARIO.AREA_HA||', 
                                PAGO_ANO        = '||RS_USUARIO.PAGO_ANO||', 
                                AVALUO_ANO      = '||RS_USUARIO.AVALUO_ANO||',
                                PAG_FEC         = '''||RS_USUARIO.PAG_FEC||''', 
                                PAG_VAL         = '||RS_USUARIO.PAG_VAL||',
                                NUM_COM         = '''||RS_USUARIO.NUM_COM||''', 
                                PAG_BAN         = '''||RS_USUARIO.PAG_BAN|| ''',
                                DATE_MODIFIED   = SYSDATE,
                                MODIFIED_BY     = '''|| UN_USUARIO ||'''';

                MI_CONDICION := '   CODIGO       = '''||RS_PROPIETARIOS.CODIGO||''' 
                                AND NUMERO_ORDEN = '''||RS_PROPIETARIOS.NUMERO_ORDEN||'''';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);

                MI_MSGERROR(1).CLAVE := 'NUMRESOLUCION';
                MI_MSGERROR(1).VALOR := UN_RESOLUCION;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_NUMRESOL,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;                             



          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RTA := 0;
          END;
        ELSIF RS_PROPIETARIOS.TIPOREGISTRO = 'C' THEN
          BEGIN
            SELECT 
                   NOMBRE, 
                  TIPO_NIT, 
                  NIT, 
                  RESOLUCION INTO MI_NOMBRE, 
                  MI_TIPO_NIT, 
                  MI_NIT, 
                  MI_RESOLUCION
            FROM  
                  IP_USUARIOS_PREDIAL
            WHERE 
                  CODIGO = RS_PROPIETARIOS.CODIGO 
              AND NUMERO_ORDEN =  RS_PROPIETARIOS.NUMERO_ORDEN;
          BEGIN
            BEGIN
                MI_CAMPOS := 'DEPARTAMENTO, 
                              MUNICIPIO, 
                              RESOLUCION, 
                              CONSECUTIVO, 
                              CODIGO,
                              CANCELAINSCRIBE, 
                              TIPOREGISTRO, 
                              REGISTRADO, 
                              NUMEROORDEN, 
                              DIRECCION,
                              AREATERRENO,
                              NOMBRE, 
                              TIPODOCUMENTO, 
                              NUMERODOCUMENTO, 
                              ANTNOMBRE,
                              ANTTIPODOCUMENTO, 
                              ANTNUMERODOCUMENTO, 
                              ANTRESOLUCION,
                              AREACONSTRUIDA,
                              AVALUO, 
                              ANTDIRECCION, 
                              ANTAREATERRENO, 
                              ANTCONSTRUIDA,
                              ANTAVALUO, 
                              AREATERENOM2, 
                              AREAHECTARES, 
                              ANTAREAHECTARES,
                              TARIFA, 
                              ANTTARIFA, 
                              ULTIMO_ANIO,
                              CODIGO_PADRE, 
                              ANO, 
                              COMPANIA, 
                              PAIS, 
                              VIGENCIA, 
                              ANO_CONSTRUCCION, 
                              TRPRAN,
                              DATE_CREATED,
                              CREATED_BY';

                MI_VALORES := ''''||UN_DEPARTAMENTO||''',
                              '''||UN_MUNICIPIO||''',
                              '''||UN_RESOLUCION||''', 
                              '||MI_CONSECUTIVO||', 
                              '''||RS_PROPIETARIOS.CODIGO||''',                           
                              '''||RS_PROPIETARIOS.TIPOREGISTRO||''', 
                              ''1'',
                              -1, 
                              '''||RS_PROPIETARIOS.NUMERO_ORDEN||''', 
                              '''||RS_USUARIO.Direccion||''',
                              '''||RS_USUARIO.AREA_M2||''',
                              '''||RS_PROPIETARIOS.NOMBRE||''',
                              '''||RS_PROPIETARIOS.TIPO_NIT||''', 
                              '''||RS_PROPIETARIOS.NIT||''',
                              '''||MI_NOMBRE||''' , 
                              '''||MI_TIPO_NIT||''', 
                              '''||MI_NIT||''', 
                              '''||MI_RESOLUCION||''', 
                              '||RS_USUARIO.AREA_CONSTRUIDA||', 
                              '''||RS_USUARIO.AVALUO_ANO||''', 
                              '''||RS_USUARIO.DIRECCION||''',
                              '||RS_USUARIO.AREA_M2||', 
                              '''||RS_USUARIO.AREA_CONSTRUIDA||''', 
                              '||RS_USUARIO.AVALUO_ANO||', 
                              '|| RS_USUARIO.AREA_M2||', 
                              '||RS_USUARIO.AREA_HA||', 
                              '||RS_USUARIO.AREA_HA||', 
                              '''||RS_USUARIO.TRPCOD||''', 
                              '''||RS_USUARIO.TRPCOD||''', 
                              '||RS_USUARIO.PAGO_ANO||', 
                              '''||RS_PROPIETARIOS.CODIGO||''', 
                              '||UN_ANO||','''||UN_COMPANIA||''',
                              '''||UN_PAIS||''',
                              '||RS_USUARIO.VIGENCIA||', 
                              '||RS_USUARIO.ANO_CONSTRUCCION||', 
                              '''||RS_USUARIO.TRPRAN||''',
                              SYSDATE,
                              '''|| UN_USUARIO ||'''';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                            UN_ACCION    => 'I',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_VALORES   => MI_VALORES);  


                MI_MSGERROR(1).CLAVE := 'RESOLUCION';
                MI_MSGERROR(1).VALOR := UN_RESOLUCION;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_RESOL_IGAC,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;  

          BEGIN 
            BEGIN
                MI_CAMPOS    := 'INDBORRADO    = -1, 
                                 RESOLUCION    = '''||UN_RESOLUCION||''',
                                 DATE_MODIFIED = SYSDATE,
                                 MODIFIED_BY   = '''||UN_USUARIO||'''';
                MI_CONDICION := '    CODIGO = '''||RS_PROPIETARIOS.CODIGO||''' 
                                 AND NUMERO_ORDEN = '''||RS_PROPIETARIOS.NUMERO_ORDEN||'''';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);   

                MI_MSGERROR(1).CLAVE := 'RESOLUCION';
                MI_MSGERROR(1).VALOR := UN_RESOLUCION;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_INDBORR_IG,
                                     UN_REEMPLAZOS => MI_MSGERROR);                                      
          END;  


            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RTA := 0;
          END;

        ELSIF RS_PROPIETARIOS.TIPOREGISTRO = 'I' THEN
        BEGIN 
            BEGIN
                MI_NODATOS:=1;
                SELECT CODIGO INTO MI_AUX
                 FROM IP_USUARIOS_PREDIAL
               WHERE NUMERO_ORDEN = RS_PROPIETARIOS.NUMERO_ORDEN
                AND CODIGO        = RS_USUARIO.CODIGO;
               EXCEPTION WHEN no_data_found THEN
                  MI_NODATOS:=0;
            END;

            IF MI_NODATOS = 0 THEN  

                     BEGIN
                           BEGIN

                          MI_CAMPOS := 'CODIGO,
                                        NUMERO_ORDEN, 
                                        DEPARTAMENTO, 
                                        MUNICIPIO, 
                                        TIPO,
                                        TIPO_NIT, 
                                        NIT, 
                                        NOMBRE, 
                                        DIRECCION,
                                        AREA_HA, 
                                        AREA_M2, 
                                        AREA_CONSTRUIDA,
                                        AVALUO_ANO, 
                                        PAGO_ANO, 
                                        PAG_FEC,
                                        PAG_VAL,
                                        NUM_COM, 
                                        PAG_BAN,
                                        INDBORRADO, 
                                        TRPCOD, 
                                        CODIGO_NO_ACTIVO, 
                                        RESOLUCION, 
                                        COMPANIA, 
                                        PAIS, 
                                        VIGENCIA, 
                                        ANO_CONSTRUCCION, 
                                        TRPRAN, 
                                        SUCURSAL,
                                        DATE_CREATED,
                                        CREATED_BY';

                          MI_VALORES := ''''||RS_USUARIO.CODIGO||''', 
                                        '''||RS_PROPIETARIOS.NUMERO_ORDEN||''', 
                                        '''||UN_DEPARTAMENTO||''', 
                                        '''||UN_MUNICIPIO||''', 
                                        1,
                                        '''||RS_PROPIETARIOS.TIPO_NIT||''', 
                                        '''||RS_PROPIETARIOS.NIT||''', 
                                        '''||RS_PROPIETARIOS.NOMBRE||''', 
                                        '''||RS_USUARIO.DIRECCION||''', 
                                        '||RS_USUARIO.AREA_HA||', 
                                        '||RS_USUARIO.AREA_M2||', 
                                        '||RS_USUARIO.AREA_CONSTRUIDA||', 
                                        '||RS_USUARIO.AVALUO_ANO||', 
                                        '||RS_USUARIO.PAGO_ANO||', 
                                        '''||RS_USUARIO.PAG_FEC||''', 
                                        '||RS_USUARIO.PAG_VAL||', 
                                        '''||RS_USUARIO.NUM_COM||''', 
                                        '''||RS_USUARIO.PAG_BAN||''',
                                        '||RS_PROPIETARIOS.INDBORRADO||',
                                        '''||RS_USUARIO.TRPCOD||''', 
                                        0,
                                        '''||UN_RESOLUCION||''',
                                        '''||UN_COMPANIA||''',
                                        '''||UN_PAIS||''',
                                        '||RS_USUARIO.VIGENCIA||', 
                                        '||RS_USUARIO.ANO_CONSTRUCCION||', 
                                        '''||RS_USUARIO.TRPRAN||''', 
                                        '''||RS_PROPIETARIOS.SUCURSAL||''',
                                        SYSDATE,
                                        '''|| UN_USUARIO ||'''';

                           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_VALORES   => MI_VALORES);

                           MI_MSGERROR(1).CLAVE := 'NOMBRE';
                           MI_MSGERROR(1).VALOR := RS_PROPIETARIOS.NOMBRE;

                           MI_MSGERROR(2).CLAVE := 'NIT';
                           MI_MSGERROR(2).VALOR := RS_PROPIETARIOS.NIT;                            

                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                            END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_PRED_USUA,
                                                   UN_REEMPLAZOS => MI_MSGERROR);                                      
                        END;     

            END IF;       

                   BEGIN  
                        BEGIN

                          MI_CAMPOS := 'DEPARTAMENTO,
                                        MUNICIPIO,
                                        RESOLUCION, 
                                        CONSECUTIVO, 
                                        CODIGO,
                                        CANCELAINSCRIBE, 
                                        TIPOREGISTRO, 
                                        REGISTRADO, 
                                        NUMEROORDEN, 
                                        DIRECCION, 
                                        AREATERRENO,
                                        NOMBRE,
                                        TIPODOCUMENTO, 
                                        NUMERODOCUMENTO,
                                        AREACONSTRUIDA, 
                                        AVALUO, 
                                        ANTDIRECCION, 
                                        ANTAREATERRENO, 
                                        ANTCONSTRUIDA,
                                        ANTAVALUO, 
                                        AREATERENOM2, 
                                        AREAHECTARES, 
                                        ANTAREAHECTARES,
                                        TARIFA, 
                                        ANTTARIFA, 
                                        ULTIMO_ANIO, 
                                        CODIGO_PADRE, 
                                        ANO, 
                                        COMPANIA, 
                                        PAIS, 
                                        VIGENCIA, 
                                        ANO_CONSTRUCCION, 
                                        TRPRAN,
                                        DATE_CREATED,
                                        CREATED_BY';

                          MI_VALORES := ''''||UN_DEPARTAMENTO||''', 
                                        '''||UN_MUNICIPIO||''', 
                                        '''||UN_RESOLUCION||''', 
                                        '||MI_CONSECUTIVO||', 
                                        '''||RS_PROPIETARIOS.CODIGO||''', 
                                        '''||RS_PROPIETARIOS.TIPOREGISTRO||''', 
                                        ''1'', 
                                        -1, 
                                        '''||RS_PROPIETARIOS.NUMERO_ORDEN||''', 
                                        '''||RS_USUARIO.DIRECCION||''', 
                                        '''||RS_USUARIO.AREA_M2||''', 
                                        '''||RS_PROPIETARIOS.NOMBRE||''', 
                                        '''||RS_PROPIETARIOS.TIPO_NIT||''', 
                                        '''||RS_PROPIETARIOS.NIT||''',
                                        ''' ||RS_USUARIO.AREA_CONSTRUIDA||''', 
                                        '''||RS_USUARIO.AVALUO_ANO||''', 
                                        '''||RS_USUARIO.DIRECCION||''', 
                                        '''||RS_USUARIO.AREA_M2||''', 
                                        '''||RS_USUARIO.AREA_CONSTRUIDA||''',
                                        '''||RS_USUARIO.AVALUO_ANO||''', 
                                        '||RS_USUARIO.AREA_M2||', 
                                        '||RS_USUARIO.AREA_HA||', 
                                        '||RS_USUARIO.AREA_HA||', 
                                        '''||RS_USUARIO.TRPCOD||''', 
                                        '''||RS_USUARIO.TRPCOD||''', 
                                        '||RS_USUARIO.PAGO_ANO||', 
                                        '''||RS_PROPIETARIOS.CODIGO||''', 
                                        '||UN_ANO||',
                                        '''||UN_COMPANIA||''',
                                        '''||UN_PAIS||''',
                                        '||RS_USUARIO.VIGENCIA||', 
                                        '||RS_USUARIO.ANO_CONSTRUCCION||', 
                                        '''||RS_USUARIO.TRPRAN||''',
                                        SYSDATE,
                                        '''||UN_USUARIO||'''';

                          MI_MSGERROR(1).CLAVE := 'RESOLUCION';
                          MI_MSGERROR(1).VALOR := UN_RESOLUCION;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_VALORES   => MI_VALORES);


                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                              END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_IGAC,
                                                   UN_REEMPLAZOS => MI_MSGERROR);   
                      END;


        END;                             

          BEGIN
            SELECT 
                  CODIGO, 
                  NUMERO_ORDEN, 
                  NOMBRE INTO MI_CODIGO, 
                  MI_NUMERO_ORDEN, 
                  MI_NOMBRE
            FROM   
                  IP_USUARIOS_PREDIAL
            WHERE  
                  CODIGO       = RS_PROPIETARIOS.CODIGO 
              AND NUMERO_ORDEN =  RS_PROPIETARIOS.NUMERO_ORDEN;       
            BEGIN
                 BEGIN

                      MI_CAMPOS := 'TIPO_NIT      = '''||RS_PROPIETARIOS.TIPO_NIT||''',
                                    NIT           = '''||RS_PROPIETARIOS.NIT||''', 
                                    NOMBRE        = '''||RS_PROPIETARIOS.NOMBRE||''', 
                                    INDBORRADO    = '||RS_PROPIETARIOS.INDBORRADO||', 
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||'''';

                      MI_CONDICION := '    CODIGO = '''||RS_USUARIO.CODIGO||''' 
                                       AND NUMERO_ORDEN = '''||RS_PROPIETARIOS.NUMERO_ORDEN||'''';

                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_CONDICION => MI_CONDICION);  

                      MI_MSGERROR(1).CLAVE := 'NOMBRE';
                      MI_MSGERROR(1).VALOR := RS_PROPIETARIOS.NOMBRE;

                      MI_MSGERROR(2).CLAVE := 'NIT';
                      MI_MSGERROR(2).VALOR := RS_PROPIETARIOS.NIT;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_USUARIO_P,
                                           UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;

                  SELECT CODIGO,   
                         NUMERO_ORDEN,    
                         NOMBRE,    
                         PAGO_ANO,    
                         PAG_FEC,    
                         PAG_VAL,    
                         NUM_COM,    
                         PAG_BAN INTO MI_CODIGO, 
                         MI_NUMERO_ORDEN, 
                         MI_NOMBRE, 
                         MI_PAGO_ANO, 
                         MI_PAG_FEC, 
                         MI_PAG_VAL, 
                         MI_NUM_COM, 
                         MI_PAG_BAN
                  FROM   
                         IP_USUARIOS_PREDIAL
                  WHERE  
                         NUMERO_ORDEN = PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL 
                    AND  CODIGO = RS_USUARIO.CODIGO;

                BEGIN
                  BEGIN
                    MI_CAMPOS := 'PAGO_ANO      = '||MI_PAGO_ANO||', 
                                  PAG_FEC       = '''||MI_PAG_FEC||''', 
                                  PAG_VAL       = '||MI_PAG_VAL||', 
                                  NUM_COM       = '''||MI_NUM_COM||''', 
                                  PAG_BAN       = '''||MI_PAG_BAN||''',
                                  DATE_MODIFIED = SYSDATE,
                                  MODIFIED_BY   = '''||UN_USUARIO||'''';

                    MI_CONDICION := '    CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                     AND NUMERO_ORDEN = '''||RS_PROPIETARIOS.NUMERO_ORDEN||'''';

                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION);

                      MI_MSGERROR(1).CLAVE := 'NUM_COM';
                      MI_MSGERROR(1).VALOR := MI_NUM_COM;

                      MI_MSGERROR(2).CLAVE := 'PAGO_ANO';
                      MI_MSGERROR(2).VALOR := MI_PAGO_ANO;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PAG_ANO,
                                           UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;



          EXCEPTION WHEN NO_DATA_FOUND THEN
            BEGIN
              BEGIN
              MI_CAMPOS := 'CODIGO,
                            NUMERO_ORDEN, 
                            DEPARTAMENTO, 
                            MUNICIPIO, 
                            TIPO,
                            TIPO_NIT, 
                            NIT, 
                            NOMBRE, 
                            DIRECCION,
                            AREA_HA, 
                            AREA_M2, 
                            AREA_CONSTRUIDA,
                            AVALUO_ANO, 
                            PAGO_ANO, 
                            PAG_FEC,
                            PAG_VAL,
                            NUM_COM, 
                            PAG_BAN,
                            INDBORRADO, 
                            TRPCOD, 
                            CODIGO_NO_ACTIVO, 
                            RESOLUCION, 
                            COMPANIA, 
                            PAIS, 
                            VIGENCIA, 
                            ANO_CONSTRUCCION, 
                            TRPRAN, 
                            SUCURSAL,
                            DATE_CREATED,
                            CREATED_BY';

              MI_VALORES := ''''||RS_USUARIO.CODIGO||''', 
                            '''||RS_PROPIETARIOS.NUMERO_ORDEN||''', 
                            '''||UN_DEPARTAMENTO||''', 
                            '''||UN_MUNICIPIO||''', 
                            1,
                            '''||RS_PROPIETARIOS.TIPO_NIT||''', 
                            '''||RS_PROPIETARIOS.NIT||''', 
                            '''||RS_PROPIETARIOS.NOMBRE||''', 
                            '''||RS_USUARIO.DIRECCION||''', 
                            '||RS_USUARIO.AREA_HA||', 
                            '||RS_USUARIO.AREA_M2||', 
                            '||RS_USUARIO.AREA_CONSTRUIDA||', 
                            '||RS_USUARIO.AVALUO_ANO||', 
                            '||RS_USUARIO.PAGO_ANO||', 
                            '''||RS_USUARIO.PAG_FEC||''', 
                            '||RS_USUARIO.PAG_VAL||', 
                            '''||RS_USUARIO.NUM_COM||''', 
                            '''||RS_USUARIO.PAG_BAN||''',
                            '||RS_PROPIETARIOS.INDBORRADO||',
                            '''||RS_USUARIO.TRPCOD||''', 
                            0,
                            '''||UN_RESOLUCION||''',
                            '''||UN_COMPANIA||''',
                            '''||UN_PAIS||''',
                            '||RS_USUARIO.VIGENCIA||', 
                            '||RS_USUARIO.ANO_CONSTRUCCION||', 
                            '''||RS_USUARIO.TRPRAN||''', 
                            '''||RS_PROPIETARIOS.SUCURSAL||''',
                            SYSDATE,
                            '''|| UN_USUARIO ||'''';

               MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                           UN_ACCION    => 'I',
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_VALORES   => MI_VALORES);

               MI_MSGERROR(1).CLAVE := 'NOMBRE';
               MI_MSGERROR(1).VALOR := RS_PROPIETARIOS.NOMBRE;

               MI_MSGERROR(2).CLAVE := 'NIT';
               MI_MSGERROR(2).VALOR := RS_PROPIETARIOS.NIT;                            

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_PRED_USUA,
                                       UN_REEMPLAZOS => MI_MSGERROR);                                      
            END;                            


          END;
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        END IF;
    	END LOOP;
    ELSE
    	MI_RTA := 0;
    END IF;

    SELECT 
           NVL(COUNT(NUMERO_REGISTROS), 0) INTO MI_CANT_REG
      FROM   
           IP_USUARIOS_PREDIAL
     WHERE  
           CODIGO = MI_CODIGO AND INDBORRADO = 0 AND CODIGO_NO_ACTIVO =  0;

    IF MI_CANT_REG > 0 THEN

    	BEGIN
        BEGIN
          MI_CAMPOS := 'NUMERO_REGISTROS = '''||LPAD(MI_CANT_REG, 3, '0')||''',
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''|| UN_USUARIO ||'''';
          MI_CONDICION := 'CODIGO = '''||RS_USUARIO.CODIGO||'''';

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_CONDICION => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := RS_USUARIO.CODIGO;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_NUM_REG,
                                 UN_REEMPLAZOS => MI_MSGERROR);                                      
      END;      


    END IF;
    IF MI_RTA IS NULL THEN 
      MI_RTA:=-2;
    END IF;

    RETURN MI_RTA;

END FC_ACEPTARPROPIETARIOS;		

FUNCTION FC_ACTUALIZA_NUM_ORDEN
/*
    NAME              : FC_ACTUALIZA_NUM_ORDEN en access --> Actualiza_NumOrden
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 30/08/2016 
    TIME              : 12:11 AM     
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 27/07/2017
    DESCRIPTION       : Proceso para actualizar el campo NUMERO_REGISTROS de la tabla IP_USUARIOS_PREDIAL
    MODIFICATIONS     : Se agregaron campos de auditoria
    PARAMETERS        :   UN_COMPANIA   => Compania por la cual se ingresa a la aplicacion
                          UN_CODIGO     => Codigo usado en la condicion para actualizar el numero de registros en la tabla IP_USUARIOS_PREDIAL.
                          UN_USUARIO    => Usuario ingresado en la aplicacion

    @NAME:  actualizarNumeroDeOrdenPredios
    @METHOD:  GET   
*/
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO 				IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_USUARIO			  IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2
AS
	MI_VLRRETORNO			VARCHAR2(4000 CHAR);
	MI_CANT					  NUMBER;
	MI_RTA		        VARCHAR2(4000 CHAR);
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    SELECT 
           NVL(COUNT(CODIGO), 0) INTO MI_CANT
      FROM   
           IP_USUARIOS_PREDIAL
     WHERE COMPANIA     = UN_COMPANIA
       AND CODIGO       = UN_CODIGO
       AND CODIGO_NO_ACTIVO IN(0)
       AND INDBORRADO IN(0);

    IF MI_CANT > 0 THEN
      BEGIN
        BEGIN

        MI_CAMPOS := 'NUMERO_REGISTROS = '''||LPAD(MI_CANT, 3, '0')||''' ,MODIFIED_BY = '''||UN_USUARIO||''',DATE_MODIFIED = SYSDATE ';
      	MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CODIGO = '''||UN_CODIGO||'''';

      	MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL', 
                                    UN_ACCION    => 'M', 
                                    UN_CAMPOS    => MI_CAMPOS ,
                                    UN_CONDICION => MI_CONDICION);

        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGO;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_NUM_REG,
                                 UN_REEMPLAZOS => MI_MSGERROR);                                      
      END;   


        MI_VLRRETORNO := 'Proceso finalizado';

    ELSE
        MI_VLRRETORNO := 'Se presento algún tipo de inconveniente y no fue posible actualizar el numero de propietarios del predio';
    END IF;

    RETURN MI_VLRRETORNO;
END FC_ACTUALIZA_NUM_ORDEN;

FUNCTION FC_REGISTRAR
/*

    NAME              : FC_REGISTRAR
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : 
    TIME              : 
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 27/07/2017 03:51 PM
    DESCRIPTION       : SE AGREGARON CAMPOS DE AUDITORIA A LLAMADOS DE ACCIONES CRUD
    MODIFICATIONS     : 
    PARAMETERS        : UN_DEPARTAMENTO       => Codigo del departamento del usuario que se va a regsitrar.
                        UN_MUNICIPIO          => Codigo del municipio del usuario.
                        UN_RESOLUCION         => Codigo de la resolucion del usuario al que se va actualizar.
                        UN_RADICACION         => Fecha de la radicacion de la resolucionl
                        UN_COMPANIA           => Compania de ingreso al sistema
                        UN_USUARIO            => Usuario que se va a registrar en el sistema.
                        UN_PAGO_ANO           => Año en el cual fue registrado el pago.
                        UN_PAG_VAL            => Parametro usado para armar la condicion al actualziar el registro.
                        UN_PAG_BAN            => Codigo del pago el cual se registro el pago.
                        UN_PAG_VAL            => Codigo usado en la condicion para actualizar el numero de registros en la tabla IP_USUARIOS_PREDIAL.
                        UN_NUM_COM            => Numero del comprobante a registrar.
                        UN_CODPADRE           => Codigo del predio anterior.
                        UN_PAGFEC             => Fecha del pago realizado


    @NAME:  insertarUsuariosDesdeResoluciones
    @METHOD:  GET
*/    
(
	UN_DEPARTAMENTO		IN VARCHAR2,
	UN_MUNICIPIO		  IN VARCHAR2,
	UN_RESOLUCION		  IN VARCHAR2,
	UN_RADICACION		  IN VARCHAR2,
	UN_COMPANIA			  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_USUARIO			  IN PCK_SUBTIPOS.TI_USUARIO,
	UN_PAGO_ANO 		  IN PCK_SUBTIPOS.TI_ANIO,
  UN_PAG_VAL 		   	IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PAG_BAN 		  	IN VARCHAR2,
  UN_NUM_COM 		  	IN PCK_SUBTIPOS.TI_DOCNUM,
  UN_CODPADRE		  	IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_PAGFEC			    IN VARCHAR2,
  UN_TRPPOR			    IN PCK_SUBTIPOS.TI_DOBLE
)
RETURN VARCHAR2
AS
   	MI_VLRRETORNO			    VARCHAR2(4000 CHAR);
   	MI_PCKDATOS           NUMBER;
   	MI_MENSAJE				    VARCHAR2(4000 CHAR);
   	MI_INDBORRADO			    NUMBER;
   	MI_CODIGO_NO_ACTIVO		NUMBER;
   	MI_RTA					      NUMBER;
   	MI_AUX					      NUMBER;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONTADOR           NUMBER;
    MI_AUXHOR             TIMESTAMP;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN   
	SELECT
         NVL(COUNT(*), 0) INTO MI_AUX
    FROM 
         IP_IGAC_RESOLUCIONESDET
   WHERE 
         DEPARTAMENTO = UN_DEPARTAMENTO
     AND MUNICIPIO    = UN_MUNICIPIO
     AND RESOLUCION   = UN_RESOLUCION
     AND (REGISTRADO  IN(0) OR REGISTRADO=NULL);

    IF MI_AUX = 0 THEN
        MI_VLRRETORNO := 'No se encontraron detalles de la resolución sin registrar';
        RETURN MI_VLRRETORNO;
    END IF;

    FOR RS IN(SELECT  COMPANIA,
                      PAIS,
                      ANO_CONSTRUCCION,
                      DEPARTAMENTO,
                      MUNICIPIO,
                      TIPOREGISTRO,
                      RESOLUCION,
                      RADICACION,
                      CODIGO,
                      NUMEROORDEN,
                      TOTALREGISTROS,
                      NOMBRE,
                      TIPODOCUMENTO,
                      NUMERODOCUMENTO,
                      DIRECCION,
                      DESTINOECONOMICO,
                      AREAHECTARES,
                      AREACONSTRUIDA,
                      AVALUO,
                      VIGENCIA,
                      CONDICIONTRIS,
                      AREATERENOM2,
                      OBSERVACION,
                      ULTIMO_ANIO,
                      TARIFA,
                      TRPRAN,
                      CANCELAINSCRIBE,
                      CONSECUTIVO,
                      ANO,
                      ANTRESOLUCION,
                      ANTNOMBRE,
                      ANTTIPODOCUMENTO,
                      ANTNUMERODOCUMENTO,
                      ANTDIRECCION,
                      ANTDESTINOECONOMICO,
                      ANTAREATERRENO,
                      ANTAREAHECTARES,
                      ANTCONSTRUIDA,
                      ANTANO_CONSTRUCCION,
                      ANTAVALUO,
                      ANTCONDICION,
                      ANTVIGENCIA,
                      ANTULTIMO_ANIO,
                      UBICACION,
                      UBICACIONANT,
                      ANTTARIFA
              FROM   
                   IP_IGAC_RESOLUCIONESDET
             WHERE 
                   DEPARTAMENTO = UN_DEPARTAMENTO
               AND MUNICIPIO    = UN_MUNICIPIO
               AND RESOLUCION   = UN_RESOLUCION
               AND (REGISTRADO  IN(0) OR REGISTRADO IS NULL))
    LOOP
        IF RS.CANCELAINSCRIBE = 'I' THEN
           BEGIN
            BEGIN
                  MI_CAMPOS := '  COMPANIA,
                                  PAIS, 
                                  ANO_CONSTRUCCION, 
                                  DEPARTAMENTO, 
                                  MUNICIPIO, 
                                  TIPO , 
                                  RESOLUCION, 
                                  CODIGO, 
                                  NUMERO_ORDEN,
                                  NUMERO_REGISTROS, 
                                  NOMBRE, 
                                  TIPO_NIT, 
                                  NIT, 
                                  DIRECCION, 
                                  DESTINO_ECONOMICO, 
                                  AREA_HA, 
                                  AREA_CONSTRUIDA,
                                  AVALUO_ANO, 
                                  VIGENCIA, 
                                  CONDICION_TRIBUTARIA, 
                                  AREA_M2, 
                                  OBSERVACIO, 
                                  INDBORRADO, 
                                  CODIGO_NO_ACTIVO,
                                  PAGO_ANO, 
                                  TRPCOD, 
                                  TRPRAN, 
                                  FECHA_MOVIMIENTO,
                                  CREATED_BY,
                                  DATE_CREATED';

                  MI_VALORES := ' SELECT
                                         COMPANIA, 
                                         PAIS, 
                                         ANO_CONSTRUCCION, 
                                         DEPARTAMENTO, 
                                         MUNICIPIO, 
                                         TIPOREGISTRO, 
                                         RESOLUCION || '' / '' || RADICACION AS RES, 
                                         CODIGO,
                                         NUMEROORDEN, 
                                         TOTALREGISTROS, 
                                         NOMBRE, 
                                         TIPODOCUMENTO, 
                                         NUMERODOCUMENTO, 
                                         DIRECCION, 
                                         DESTINOECONOMICO,
                                         TO_NUMBER(AREAHECTARES), 
                                         TO_NUMBER(AREACONSTRUIDA), 
                                         TO_NUMBER(AVALUO), 
                                         TO_NUMBER(VIGENCIA), 
                                         CONDICIONTRIS, 
                                         TO_NUMBER(AREATERENOM2),
                                         OBSERVACION, 
                                         0, 
                                         0,
                                         ULTIMO_ANIO, 
                                         TARIFA, 
                                         TRPRAN, 
                                         TO_DATE('''||UN_RADICACION||'''),
                                         '''||UN_USUARIO||''',                                  
                                         SYSDATE                                    
                                  FROM   
                                         IP_IGAC_RESOLUCIONESDET
                                  WHERE  COMPANIA     = '''||UN_COMPANIA||'''
                                    AND  CODIGO       = '''||RS.CODIGO||''' 
                                    AND  NUMEROORDEN  = '''||RS.NUMEROORDEN||'''';

                  MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                               UN_ACCION    => 'IS',
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_VALORES   => MI_VALORES);


                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_U_PRED,
                                         UN_TABLAERROR => 'IP_USUARIOS_PREDIAL');                                      
              END; 



          BEGIN
            BEGIN

                  IF MI_PCKDATOS > 0 THEN
                      MI_MENSAJE := PCK_PREDIAL_COM2.FC_ACTUALIZA_NUM_ORDEN(UN_COMPANIA => UN_COMPANIA,
                                                                            UN_CODIGO   => RS.CODIGO,
                                                                            UN_USUARIO  => UN_USUARIO);
                      IF MI_MENSAJE = 'Proceso finalizado' THEN
                          MI_CAMPOS    := 'REGISTRADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||'''
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          MI_CAMPOS    := 'OBS_REGISTRO = '''' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';

                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||'''
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 


                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '006', 
                                                   UN_VANMOD      => '0', 
                                                   UN_VNUMOD      => '0', 
                                                   UN_DESCRIPCION => 'Se traslado la historia del predio pero se presentaron inconvenientes al pasar los abonos, el predio anterior sigue activo');
                      ELSE

                          MI_CAMPOS := '  OBS_REGISTRO = ''Se presentó algún tipo de inconveniente y no fue posible actualizar el numero de propietarios del predio'', 
                                          MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                          MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                           AND PAIS = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                           AND ANO = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                      END IF;
                  ELSE
                    BEGIN
                        SELECT   
                               INDBORRADO, CODIGO_NO_ACTIVO INTO MI_INDBORRADO, MI_CODIGO_NO_ACTIVO
                          FROM   
                               IP_USUARIOS_PREDIAL
                         WHERE  
                               CODIGO =  RS.CODIGO
                           AND NUMERO_ORDEN = RS.NUMEROORDEN;

                        IF MI_INDBORRADO IS NOT NULL AND MI_CODIGO_NO_ACTIVO IS NOT NULL THEN
                            IF MI_INDBORRADO != 0 OR MI_CODIGO_NO_ACTIVO != 0 THEN
                                MI_CAMPOS := 'OBS_REGISTRO = ''El predio ya existe pero se encuentra inactivo'' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                                MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                                 AND PAIS = '''||RS.PAIS||''' 
                                                 AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                 AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                 AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                 AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                 AND ANO = '||RS.ANO;

                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS, 
                                                            UN_CONDICION => MI_CONDICION);

                            ELSE
                                MI_CAMPOS := 'OBS_REGISTRO = ''El predio ya existe y se encuentra activo'' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                                MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||'''
                                                 AND PAIS = '''||RS.PAIS||''' 
                                                 AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                 AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                 AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                 AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                 AND ANO = '||RS.ANO;

                               MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS, 
                                                           UN_CONDICION => MI_CONDICION);



                                MI_CAMPOS := 'REGISTRADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                                MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                                 AND PAIS = '''||RS.PAIS||''' 
                                                 AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                 AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                 AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                 AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                 AND ANO = '||RS.ANO;

                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS, 
                                                            UN_CONDICION => MI_CONDICION);

                            END IF;
                            MI_MENSAJE := PCK_PREDIAL_COM2.FC_ACTUALIZA_NUM_ORDEN(UN_COMPANIA => UN_COMPANIA,
                                                                                  UN_CODIGO   => RS.CODIGO,
                                                                                  UN_USUARIO  => UN_USUARIO);
                        ELSE
                            MI_CAMPOS := 'OBS_REGISTRO = ''El predio no quedó inscrito.'' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                            MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                             AND PAIS         = '''||RS.PAIS||''' 
                                             AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                             AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                             AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                             AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                             AND ANO          = '||RS.ANO;

                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_CAMPOS, 
                                                        UN_CONDICION => MI_CONDICION);                 

                        END IF;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_MENSAJE := '';
                    END;
                  END IF;

                  MI_CAMPOS := 'PAGO_ANO      = '||UN_PAGO_ANO||',
                                PAG_VAL       = '||UN_PAG_VAL||',
                                PAG_BAN       = '''||UN_PAG_BAN||''',
                                NUM_COM       = '''||UN_NUM_COM||''',
                                CODIGOANT1    = '''||UN_CODPADRE||''',
                                MODIFIED_BY   = '''||UN_USUARIO||''', 
                                DATE_MODIFIED = SYSDATE';

                  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA|| ''' AND CODIGO = '''||RS.CODIGO||'''';


                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION); 

                  MI_CAMPOS := 'PAG_FEC = TO_DATE('''||UN_PAGFEC||''') , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||''' AND CODIGO = '''||RS.CODIGO||'''';

                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION); 




                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                MI_MSGERROR(1).CLAVE := 'CANCELAINSCRIBE';
                MI_MSGERROR(1).VALOR := 'I';                        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CANCELAINS,
                                           UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;

              ELSIF RS.CANCELAINSCRIBE = 'C' THEN
                BEGIN
                  BEGIN
                    BEGIN
                          SELECT 
                                 INDBORRADO, 
                                 CODIGO_NO_ACTIVO INTO MI_INDBORRADO, 
                                 MI_CODIGO_NO_ACTIVO
                            FROM     
                                 IP_USUARIOS_PREDIAL
                           WHERE CODIGO =  RS.CODIGO
                             AND NUMERO_ORDEN = RS.NUMEROORDEN;

                          IF MI_INDBORRADO IS NOT NULL AND MI_CODIGO_NO_ACTIVO IS NOT NULL THEN
                              IF MI_INDBORRADO != 0 OR MI_CODIGO_NO_ACTIVO != 0 THEN
                                  MI_CAMPOS := 'OBS_REGISTRO = ''El estado del predio es borrado o inactivo. El proceso se ha cancelado'', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                                  MI_CONDICION := 'COMPANIA = '''||RS.COMPANIA||''' AND
                                                   PAIS = '''||RS.PAIS||''' AND
                                                   DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' AND
                                                   MUNICIPIO = '''||RS.MUNICIPIO||''' AND
                                                   RESOLUCION = '''||RS.RESOLUCION||''' AND
                                                   CONSECUTIVO = '''||RS.CONSECUTIVO||''' AND
                                                   ANO = '||RS.ANO;

                                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                              UN_ACCION    => 'M',
                                                              UN_CAMPOS    => MI_CAMPOS, 
                                                              UN_CONDICION => MI_CONDICION); 

                              ELSE
                                  MI_CAMPOS := 'INDBORRADO    = -1 ,
                                                FECHABORRADO  = SYSDATE,
                                                BORRADOPOR    = ''RES #'||UN_RESOLUCION||''',
                                                MODIFIED_BY   = '''||UN_USUARIO||''', 
                                                DATE_MODIFIED = SYSDATE';

                                  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                                                    AND CODIGO       = '''||RS.CODIGO||'''
                                                    AND NUMERO_ORDEN = '''||RS.NUMEROORDEN||'''';

                                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                              UN_ACCION    => 'M',
                                                              UN_CAMPOS    => MI_CAMPOS, 
                                                              UN_CONDICION => MI_CONDICION); 

                                  IF MI_RTA > 0 THEN
                                      MI_MENSAJE := PCK_PREDIAL_COM2.FC_ACTUALIZA_NUM_ORDEN(UN_COMPANIA => UN_COMPANIA,
                                                                                            UN_CODIGO   => RS.CODIGO,
                                                                                            UN_USUARIO  => UN_USUARIO);

                                      IF MI_MENSAJE = 'Proceso finalizado' THEN
                                          MI_CAMPOS := 'REGISTRADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                                           AND PAIS         = '''||RS.PAIS||'''
                                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                                           AND ANO          = '||RS.ANO;

                                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                                      UN_ACCION    => 'M',
                                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                                      UN_CONDICION => MI_CONDICION);   



                                          MI_CAMPOS := 'OBS_REGISTRO = '''' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                                          MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                                           AND PAIS = '''||RS.PAIS||''' 
                                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                           AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                           AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                           AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                           AND ANO = '||RS.ANO;

                                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                                      UN_ACCION    => 'M',
                                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                                      UN_CONDICION => MI_CONDICION);

                                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                                   UN_CODMOD      => RS.CODIGO, 
                                                                   UN_OPEMOD      => UN_USUARIO, 
                                                                   UN_CCOMOD      => '021', 
                                                                   UN_VANMOD      => '0', 
                                                                   UN_VNUMOD      => '-1', 
                                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)');

                                      ELSE
                                          MI_CAMPOS := 'OBS_REGISTRO = ''Se presentó algún tipo de inconveniente y no fue posible actualizar el numero de propietarios del predio'',
                                                        MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                                          MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                                           AND PAIS = '''||RS.PAIS||''' 
                                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                           AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                           AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                           AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                           AND ANO = '||RS.ANO;

                                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                                      UN_ACCION    => 'M',
                                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                                      UN_CONDICION => MI_CONDICION);

                                      END IF;
                                  ELSE
                                    MI_CAMPOS := 'OBS_REGISTRO = ''No se canceló el predio correctamente'' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                                    MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||'''
                                                     AND PAIS = '''||RS.PAIS||''' 
                                                     AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                                     AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                                     AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                                     AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                                     AND ANO = '||RS.ANO;

                                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                                UN_ACCION    => 'M',
                                                                UN_CAMPOS    => MI_CAMPOS, 
                                                                UN_CONDICION => MI_CONDICION);                 

                                  END IF;
                              END IF;
                          ELSE
                              MI_CAMPOS := 'OBS_REGISTRO = ''El predio no se encuentra registrado en el sistema'' , 
                                            MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                              MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                               AND PAIS         = '''||RS.PAIS||''' 
                                               AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                               AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                               AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                               AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                               AND ANO          = '||RS.ANO;

                              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS, 
                                                          UN_CONDICION => MI_CONDICION);                 

                          END IF;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                          MI_MENSAJE := '';
                        END;



                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
                MI_MSGERROR(1).CLAVE := 'CANCELAINSCRIBE';
                MI_MSGERROR(1).VALOR := 'C';                      
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CANCELAINS,
                                           UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;
              ELSIF RS.CANCELAINSCRIBE = 'M' THEN
                BEGIN
                  BEGIN
                  SELECT  
                         NVL(COUNT(*), 0) INTO MI_AUX
                    FROM  
                         IP_USUARIOS_PREDIAL
                   WHERE 
                         CODIGO = RS.CODIGO
                     AND INDBORRADO = 0 
                     AND CODIGO_NO_ACTIVO = 0;

                  IF MI_AUX = 0 THEN
                      MI_CAMPOS := 'OBS_REGISTRO = ''El código del predio no se encontró registrado en el sistema'', 
                                    MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                      MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                       AND PAIS         = '''||RS.PAIS||''' 
                                       AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                       AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                       AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                       AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                       AND ANO          = '||RS.ANO;

                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_CONDICION => MI_CONDICION);

                  END IF;

                  MI_CONTADOR := 1;
                  FOR RS_USUARIO IN (SELECT 
                                            COMPANIA,
                                            CODIGO,
                                            NUMERO_ORDEN,
                                            RESOLUCION,
                                            NOMBRE,
                                            TIPO_NIT,
                                            NIT,
                                            DIRECCION,
                                            DESTINO_ECONOMICO,
                                            AREA_HA,
                                            AREA_M2,
                                            TRPCOD,
                                            AREA_CONSTRUIDA,
                                            ANO_CONSTRUCCION,
                                            AVALUO_ANO,
                                            CONDICION_TRIBUTARIA,
                                            VIGENCIA,
                                            PAGO_ANO,
                                            OBSERVACIO,
                                            UBICACION
                                       FROM 
                                            IP_USUARIOS_PREDIAL
                                      WHERE COMPANIA    = UN_COMPANIA
                                        AND CODIGO      = RS.CODIGO
                                        AND INDBORRADO IN(0) 
                                        AND CODIGO_NO_ACTIVO IN(0))
                  LOOP
                    IF NVL(RS.RESOLUCION, ' ') != ' ' THEN
                        MI_CAMPOS := 'ANTRESOLUCION = '''||NVL(RS_USUARIO.RESOLUCION, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
                        MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                         AND PAIS         = '''||RS.PAIS||''' 
                                         AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                         AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                         AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                         AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                         AND ANO          = '||RS.ANO;

                        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS, 
                                                    UN_CONDICION => MI_CONDICION);                  


                        MI_CAMPOS := 'RESOLUCION = '''||NVL(RS.RESOLUCION, ' ')||''' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                        MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                         AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                         AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS, 
                                                    UN_CONDICION => MI_CONDICION);                    

                        PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                 UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                 UN_OPEMOD      => UN_USUARIO, 
                                                 UN_CCOMOD      => '112', 
                                                 UN_VANMOD      => RS.ANTRESOLUCION, 
                                                 UN_VNUMOD      => RS_USUARIO.RESOLUCION, 
                                                 UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                 UN_FECMOD      => SYSDATE,
                                                 UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                        MI_CONTADOR := MI_CONTADOR+1;
                    END IF;

                      IF NVL(RS.NOMBRE, ' ') != ' ' AND RS.NUMEROORDEN = RS_USUARIO.NUMERO_ORDEN THEN
                          MI_CAMPOS := 'ANTNOMBRE = '''||NVL(RS_USUARIO.NOMBRE, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          MI_CAMPOS := 'NOMBRE = '''||NVL(RS.NOMBRE, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||'''
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION); 

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '007', 
                                                   UN_VANMOD      => RS.ANTNOMBRE, 
                                                   UN_VNUMOD      => RS_USUARIO.NOMBRE, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));


                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.TIPODOCUMENTO, ' ') != ' ' AND RS.NUMEROORDEN = RS_USUARIO.NUMERO_ORDEN THEN
                          MI_CAMPOS    := 'ANTTIPODOCUMENTO = '''||NVL(RS_USUARIO.TIPO_NIT, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||'''
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          MI_CAMPOS    := 'TIPO_NIT = '''||NVL(RS.TIPODOCUMENTO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);                  

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '008', 
                                                   UN_VANMOD      => RS.ANTTIPODOCUMENTO, 
                                                   UN_VNUMOD      => RS_USUARIO.TIPO_NIT, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400)); 

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.NUMERODOCUMENTO, ' ') != ' ' AND RS.NUMEROORDEN = RS_USUARIO.NUMERO_ORDEN THEN
                          MI_CAMPOS    := 'ANTNUMERODOCUMENTO = '''||NVL(RS_USUARIO.NIT, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||'''
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          MI_CAMPOS := 'NIT = '''||NVL(RS.NUMERODOCUMENTO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';

                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 


                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '009', 
                                                   UN_VANMOD      => RS.ANTNUMERODOCUMENTO, 
                                                   UN_VNUMOD      => RS_USUARIO.NIT, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400)); 


                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.DIRECCION, ' ') != ' ' THEN
                          MI_CAMPOS := 'ANTDIRECCION = '''||NVL(RS_USUARIO.DIRECCION, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS := 'DIRECCION = '''||NVL(RS.DIRECCION, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '011', 
                                                   UN_VANMOD      => RS.ANTDIRECCION, 
                                                   UN_VNUMOD      => RS_USUARIO.DIRECCION, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.DESTINOECONOMICO, ' ') != ' ' THEN
                          MI_CAMPOS := 'ANTDESTINOECONOMICO = '''||NVL(RS_USUARIO.DESTINO_ECONOMICO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);


                          MI_CAMPOS := 'DESTINO_ECONOMICO = '''||NVL(RS.DESTINOECONOMICO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '017', 
                                                   UN_VANMOD      => RS.ANTDESTINOECONOMICO, 
                                                   UN_VNUMOD      => RS_USUARIO.DESTINO_ECONOMICO, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.AREATERENOM2, -1) != -1 THEN
                          MI_CAMPOS := 'ANTAREATERRENO = '||((NVL(RS_USUARIO.AREA_HA, 0) * 10000) + NVL(RS_USUARIO.AREA_M2, 0)) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS := 'AREATERRENO = '||((NVL(RS_USUARIO.AREA_HA, 0) * 10000) + RS.AREATERENOM2) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS := 'AREA_M2 = '||NVL(RS.AREATERENOM2, 0) || ', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '014', 
                                                   UN_VANMOD      => RS.ANTAREATERRENO, 
                                                   UN_VNUMOD      => RS_USUARIO.AREA_M2, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));



                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.AREAHECTARES, -1) != -1 THEN
                          MI_CAMPOS := 'ANTAREAHECTARES = '||TO_NUMBER(NVL(RS_USUARIO.AREA_HA, 0)) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||'''
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);                 

                          MI_CAMPOS := 'AREA_HA = '||NVL(RS.AREAHECTARES, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);                 

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '013', 
                                                   UN_VANMOD      => RS.ANTAREAHECTARES, 
                                                   UN_VNUMOD      => RS_USUARIO.AREA_HA, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400)); 


                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.TARIFA, ' ') != ' ' THEN
                          MI_CAMPOS    := 'ANTTARIFA = '''||NVL(RS_USUARIO.TRPCOD, ' ')||''' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          MI_CAMPOS := 'TRPCOD = '''||NVL(RS.TARIFA, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '233', 
                                                   UN_VANMOD      => RS.TARIFA, 
                                                   UN_VNUMOD      => RS_USUARIO.TRPCOD, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400)); 

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.AREACONSTRUIDA, -1) != -1 THEN
                          MI_CAMPOS    := 'ANTCONSTRUIDA = '||NVL(RS_USUARIO.AREA_CONSTRUIDA, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);                 

                          IF RS_USUARIO.AREA_CONSTRUIDA != TO_NUMBER(RS.AREACONSTRUIDA) THEN
                              MI_CAMPOS    := 'TRPCOD = '''||NVL(RS.TARIFA, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                              MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                               AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                               AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          END IF;

                          MI_CAMPOS    := 'AREA_CONSTRUIDA = '||NVL(RS.AREACONSTRUIDA, 0) || ', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '233', 
                                                   UN_VANMOD      => RS.ANTCONSTRUIDA, 
                                                   UN_VNUMOD      => RS_USUARIO.AREA_CONSTRUIDA, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400)); 

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.ANO_CONSTRUCCION, -1) != -1 THEN
                          MI_CAMPOS    := 'ANTANO_CONSTRUCCION = '||NVL(RS_USUARIO.ANO_CONSTRUCCION, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);                 

                          MI_CAMPOS    := 'ANO_CONSTRUCCION = '||NVL(RS.ANO_CONSTRUCCION, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '018', 
                                                   UN_VANMOD      => RS.ANTANO_CONSTRUCCION, 
                                                   UN_VNUMOD      => RS_USUARIO.ANO_CONSTRUCCION, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));


                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.AVALUO, -1) != -1 THEN
                          MI_CAMPOS    := 'ANTAVALUO = '||NVL(RS_USUARIO.AVALUO_ANO, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'AVALUO_ANO       = '||NVL(RS.AVALUO, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||'''
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'AVALUO = '||NVL(RS.AVALUO, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                                            AND CODIGO = '''||RS.CODIGO||''' 
                                            AND PREANO = '||RS.VIGENCIA;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '016', 
                                                   UN_VANMOD      => RS.ANTAVALUO, 
                                                   UN_VNUMOD      => RS_USUARIO.AVALUO_ANO, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.CONDICIONTRIS, ' ') != ' ' THEN
                          MI_CAMPOS    := 'ANTCONDICION = '||NVL(RS_USUARIO.CONDICION_TRIBUTARIA, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA = '''||RS.COMPANIA||''' 
                                           AND PAIS = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO = '''||RS.CONSECUTIVO||''' 
                                           AND ANO = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'CONDICION_TRIBUTARIA = '''||NVL(RS.CONDICIONTRIS, ' ')||''' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '019', 
                                                   UN_VANMOD      => RS.ANTCONDICION, 
                                                   UN_VNUMOD      => RS_USUARIO.CONDICION_TRIBUTARIA, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.VIGENCIA, -1) != -1 THEN
                          MI_CAMPOS    := 'ANTVIGENCIA = '||NVL(RS_USUARIO.VIGENCIA, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'VIGENCIA = '||NVL(RS.VIGENCIA, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '020', 
                                                   UN_VANMOD      => RS.ANTVIGENCIA, 
                                                   UN_VNUMOD      => RS_USUARIO.VIGENCIA, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));


                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.ULTIMO_ANIO, 0) != 0 THEN
                          MI_CAMPOS    := 'ANTULTIMO_ANIO = '||NVL(RS_USUARIO.PAGO_ANO, 0) ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||'''
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                         MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'PAGO_ANO = '||NVL(RS.ULTIMO_ANIO, 0)||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||'''
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION); 

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '039', 
                                                   UN_VANMOD      => RS.ANTULTIMO_ANIO, 
                                                   UN_VNUMOD      => RS_USUARIO.PAGO_ANO, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.OBSERVACION, ' ') != ' ' THEN
                          MI_CAMPOS := 'ANTOBSERVACION = '''||NVL(RS_USUARIO.OBSERVACIO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS := 'OBSERVACIO = '''||NVL(RS.RESOLUCION, ' ')||'-'||NVL(RS_USUARIO.OBSERVACIO, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                      END IF;

                      IF NVL(RS.UBICACION, ' ') != ' ' THEN
                          MI_CAMPOS    := 'UBICACIONANT = '''||NVL(RS_USUARIO.UBICACION, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '   COMPANIA      = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'UBICACION = '''||NVL(RS.UBICACION, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '   COMPANIA      = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);

                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '021', 
                                                   UN_VANMOD      => RS.UBICACIONANT, 
                                                   UN_VNUMOD      => RS_USUARIO.UBICACION, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      IF NVL(RS.TARIFA, ' ') != ' ' THEN
                          MI_CAMPOS    := 'ANTTARIFA = '''||NVL(RS_USUARIO.TRPCOD, 0)||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                           AND PAIS         = '''||RS.PAIS||''' 
                                           AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                           AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                                           AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                           AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                           AND ANO          = '||RS.ANO;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);

                          MI_CAMPOS    := 'TRPCOD = '''||NVL(RS.TARIFA, ' ')||''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                          MI_CONDICION := '   COMPANIA      = '''||RS_USUARIO.COMPANIA||''' 
                                           AND CODIGO       = '''||RS_USUARIO.CODIGO||''' 
                                           AND NUMERO_ORDEN = '''||RS_USUARIO.NUMERO_ORDEN||'''';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);
                          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_CODMOD      => RS_USUARIO.CODIGO, 
                                                   UN_OPEMOD      => UN_USUARIO, 
                                                   UN_CCOMOD      => '046', 
                                                   UN_VANMOD      => RS.ANTTARIFA, 
                                                   UN_VNUMOD      => RS_USUARIO.TRPCOD, 
                                                   UN_DESCRIPCION => 'Resolución tipo Mes No '||UN_RESOLUCION||' (Manual)',
                                                   UN_FECMOD      => SYSDATE,
                                                   UN_HORMOD      => SYSDATE+(MI_CONTADOR/86400));

                          MI_CONTADOR := MI_CONTADOR+1;
                      END IF;

                      MI_CAMPOS := 'TRPCOD = '''|| RS.TARIFA||''', 
                                    TRPPOR = '||(UN_TRPPOR / 1000)||',
                                    MODIFIED_BY = '''||UN_USUARIO||''', 
                                    DATE_MODIFIED = SYSDATE';

                      MI_CONDICION := '  COMPANIA = '''||UN_COMPANIA||'''
                                        AND CODIGO       = '''||RS.CODIGO||'''
                                        AND PREANO       = '||RS.VIGENCIA||'
                                        AND NUMERO_ORDEN = '''||RS.NUMEROORDEN||'''';

                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_CONDICION => MI_CONDICION);

              END LOOP;

                   MI_MENSAJE := PCK_PREDIAL_COM2.FC_ACTUALIZA_NUM_ORDEN( UN_COMPANIA => UN_COMPANIA,
                                                                          UN_CODIGO   => RS.CODIGO,
                                                                          UN_USUARIO  => UN_USUARIO);



                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                MI_MSGERROR(1).CLAVE := 'CANCELAINSCRIBE';
                MI_MSGERROR(1).VALOR := 'M';                        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CANCELAINS,
                                           UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;
            ELSE
                  MI_CAMPOS := 'OBS_REGISTRO = ''No se ha definido si el registro corresponde a una inscripción, modificación o cancelación'' ,
                                MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                                   AND PAIS         = '''||RS.PAIS||''' 
                                   AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                                   AND MUNICIPIO    = '''||RS.MUNICIPIO||'''
                                   AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                                   AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                                   AND ANO          = '||RS.ANO;

                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION);

                  GOTO SigRegistro;
              END IF;

              MI_CAMPOS := 'REGISTRADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';

              MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||''' 
                               AND PAIS         = '''||RS.PAIS||''' 
                               AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                               AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                               AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                               AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                               AND ANO          = '||RS.ANO;


               MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_CONDICION => MI_CONDICION);



              MI_CAMPOS    := 'OBS_REGISTRO = '''', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
              MI_CONDICION := '    COMPANIA     = '''||RS.COMPANIA||'''
                               AND PAIS         = '''||RS.PAIS||''' 
                               AND DEPARTAMENTO = '''||RS.DEPARTAMENTO||''' 
                               AND MUNICIPIO    = '''||RS.MUNICIPIO||''' 
                               AND RESOLUCION   = '''||RS.RESOLUCION||''' 
                               AND CONSECUTIVO  = '''||RS.CONSECUTIVO||''' 
                               AND ANO          = '||RS.ANO;

              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_IGAC_RESOLUCIONESDET',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS, 
                                          UN_CONDICION => MI_CONDICION);

              <<SigRegistro>>
                NULL;
          END LOOP;

          MI_VLRRETORNO := 'Proceso finalizado';
          RETURN MI_VLRRETORNO;


END FC_REGISTRAR;

PROCEDURE PR_INCREMENTARAVALUOS 
(
/*
        NAME              : PR_INCREMENTARAVALUOS 
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
        DATE MIGRADOR     : 21/02/2017
        TIME              : 16:18 PM
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : 
        PARAMETERS        :UN_COMPANIA           => Compania de ingreso a la aplicación
                           UN_CODIGO => Código del predio
                           UN_NUMERO_ORDEN => Número de orden del predio
                           UN_AVALUO => Un avalúo del predio
                           UN_AVALUO_ANO => Un avalúo del predio por año


        @NAME:  incrementarAvaluos
        @METHOD:  PUT      
    */
	UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CODIGO       IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN,
	UN_AVALUO   	  IN PCK_SUBTIPOS.TI_DOBLE,
	UN_AVALUO_ANO 	IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  UN_ANO          IN PCK_SUBTIPOS.TI_ANIO
)

  AS 
    MI_POSICION 		PCK_SUBTIPOS.TI_ENTERO ;
		MI_AVALUO   		  PCK_SUBTIPOS.TI_DOBLE;
		MI_STRSQL   		  PCK_SUBTIPOS.TI_STRSQL;
		MI_ULTIMODEAVALUO PCK_SUBTIPOS.TI_DOBLE;                               
  BEGIN 
  EXECUTE IMMEDIATE    ' ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  		MI_POSICION := 0;
		<<INCREMENTAAVALUOS>>
		FOR RS IN (	SELECT  IP_FACTURADOS.PREANO
					FROM IP_FACTURADOS 
						INNER JOIN IP_TARIFAS 
							ON IP_FACTURADOS.PREANO = IP_TARIFAS.TRPANO 
							AND IP_FACTURADOS.TRPCOD = IP_TARIFAS.TRPCOD 
							AND IP_FACTURADOS.TRPRAN = IP_TARIFAS.TRPRAN 
					WHERE IP_FACTURADOS.NUMERO_ORDEN = UN_COMPANIA 
						AND IP_FACTURADOS.CODIGO = UN_CODIGO 
						AND IP_FACTURADOS.NOCOBRADO IN(0) 
						AND IP_FACTURADOS.INDPAGO_ACPAG IN(0) 
						AND IP_FACTURADOS.PAGADO IN(0) 
						AND  IP_FACTURADOS.PREANO > UN_ANO
					ORDER BY IP_FACTURADOS.PREANO)

			LOOP 
				MI_AVALUO := UN_AVALUO + ( UN_AVALUO * ((UN_AVALUO_ANO / 100) * MI_POSICION));
				BEGIN
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS', 
                                      UN_ACCION    => 'M', 
                                      UN_CAMPOS    => 'AVALUO  = ' || MI_AVALUO || ', AVALUOANT = ' || UN_AVALUO_ANO , 
                                      UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CODIGO      = '''|| UN_CODIGO || '''
                                                      AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN ||'''
													  AND NOCOBRADO IN(0)
													  AND INDPAGO_ACPAG IN(0)
													  AND PAGADO IN(0)
													  AND PREANO = '||RS.PREANO );
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
				END;
			MI_POSICION := MI_POSICION + 1;	
			END LOOP INCREMENTAAVALUOS;

		BEGIN
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL', 
                                     UN_ACCION    => 'M', 
                                     UN_CAMPOS    => 'AVALUO_ANO  = ' || MI_AVALUO || ', AVALUO_ANT = ' || UN_AVALUO_ANO , 
                                     UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CODIGO      = '''|| UN_CODIGO || '''
                                                      AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN ||'''');
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
            THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
		END;	

		/*	MI_STRSQL := 'WITH PREDIOS_ULTIMOFACTURADO AS 
								(SELECT IP_FACTURADOS.COMPANIA,
									IP_FACTURADOS.CODIGO, 
									IP_FACTURADOS.NUMERO_ORDEN, 
									MAX(IP_FACTURADOS.PREANO) AS ULTIMO_FACTURADO
								FROM IP_FACTURADOS
								GROUP BY IP_FACTURADOS.COMPANIA,
										IP_FACTURADOS.CODIGO, 
										IP_FACTURADOS.NUMERO_ORDEN)  
							SELECT  IP_FACTURADOS.AVALUO AS ULTIMODEAVALUO 
							FROM PREDIOS_ULTIMOFACTURADO
								INNER JOIN IP_FACTURADOS 
									ON PREDIOS_ULTIMOFACTURADO.COMPANIA           = IP_FACTURADOS.COMPANIA
									AND PREDIOS_ULTIMOFACTURADO.CODIGO            = IP_FACTURADOS.CODIGO 
									AND PREDIOS_ULTIMOFACTURADO.NUMERO_ORDEN      = IP_FACTURADOS.NUMERO_ORDEN
									AND PREDIOS_ULTIMOFACTURADO.ULTIMO_FACTURADO  = IP_FACTURADOS.PREANO 
							WHERE IP_FACTURADOS.COMPANIA = '''||UN_COMPANIA||'''
								AND IP_FACTURADOS.CODIGO = '''||UN_CODIGO||''' 
								AND IP_FACTURADOS.NUMERO_ORDEN= '''||UN_NUMERO_ORDEN||'''';
			BEGIN	
				EXECUTE IMMEDIATE MI_STRSQL INTO MI_ULTIMODEAVALUO;
  	      EXCEPTION WHEN NO_DATA_FOUND THEN
					RETURN;
			END;	

			BEGIN
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL', 
                                     UN_ACCION    => 'M', 
                                     UN_CAMPOS    => 'AVALUO_ANT  = AVALUO_ANO', 
                                     UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CODIGO      = '''|| UN_CODIGO || '''
                                                      AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN ||'''');

			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
				THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
			END;
			BEGIN
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL', 
                                     UN_ACCION    => 'M', 
                                     UN_CAMPOS    => 'AVALUO_ANO  = '||MI_ULTIMODEAVALUO, 
                                     UN_CONDICION => '   COMPANIA   = ''' || UN_COMPANIA || '''
                                                      AND CODIGO      = '''|| UN_CODIGO || '''
                                                      AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN ||'''');
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
				THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
			END;*/

	EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN 
		PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_INCREMENTO_AVALUO
         ); 

END PR_INCREMENTARAVALUOS;

--14
FUNCTION FC_ARMACONSULTAESTADOCUENTA
(
  /*

    NAME              : FC_ARMACONSULTAESTADOCUENTA --> 
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 13/07/2017
    TIME              : 12:00 PM 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Funcion que devuelve la condicion a reemplazar en el informe del formulario Estado Cuenta
    TIME              : 
    MODIFICATIONS     : 
    PARAMETERS        : 	UN_COMPANIA         =>	Compania de ingreso a la aplicación
                          UN_NIT 				      => Nit seleccionado  
                          UN_CODPREDIO        => Codigo de precio seleccionado
                          UN_PROPIETARIO      => Nombre del usuario seleccionado
                          UN_PORUNUSUARIO     => Check Por usuario del formulario 
                          UN_PORPREDIO        => Check Por predio del formulario
                          UN_NUMORDEN         => Numero de orden del usuario seleccionado
                          UN_NOMBRECOMPANIA 	=> Nombre de la compania de ingreso a la aplicacion 


    @NAME:  armaConsultaEstadoCuenta
    @METHOD:  GET
*/    

	UN_COMPANIA     	IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_NIT 				    IN VARCHAR2,
	UN_CODPREDIO    	IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_PROPIETARIO  	IN VARCHAR2,
	UN_PORUNUSUARIO 	IN PCK_SUBTIPOS.TI_LOGICO,
	UN_PORPREDIO    	IN PCK_SUBTIPOS.TI_LOGICO,
	UN_NUMORDEN     	IN PCK_SUBTIPOS.TI_NUMORDEN,
	UN_NOMBRECOMPANIA 	IN VARCHAR2
)
RETURN CLOB 
	AS 	
		 MI_CADENA      CLOB:='';
		 MI_STRSQL      PCK_SUBTIPOS.TI_STRSQL; 
		 MI_PAGADO      PCK_SUBTIPOS.TI_LOGICO;
		 MI_CODIGO     	PCK_SUBTIPOS.TI_CODPREDIO;
		 MI_PARAMETRO   PCK_SUBTIPOS.TI_PARAMETRO;
		 MI_RTA         PCK_SUBTIPOS.TI_LOGICO;

	BEGIN

		IF UN_PORUNUSUARIO <> 0
		THEN 
			BEGIN 
				BEGIN 
					IF UN_NIT = '' OR UN_NIT IS NULL 
					THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
					END IF;
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PORUSUARIO,
                            UN_TABLAERROR => 'IP_USUARIOS_PREDIAL');    
			END;	
		ELSE
			BEGIN
				BEGIN 
					IF UN_CODPREDIO = '' OR UN_CODPREDIO IS NULL 
					THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
					END IF;
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PORPREDIO,
                            UN_TABLAERROR => 'IP_USUARIOS_PREDIAL'); 	
			END;
		END IF;
    BEGIN
			BEGIN 
				MI_STRSQL := '	SELECT DISTINCT IP_FACTURADOS.PAGADO 
								FROM IP_FACTURADOS 
								WHERE IP_FACTURADOS.COMPANIA = '''||UN_COMPANIA||''' 
									AND IP_FACTURADOS.CODIGO = '''||UN_CODPREDIO||''' 
									AND IP_FACTURADOS.PAGADO IN(0)
									AND IP_FACTURADOS.NOCOBRADO IN(0)';
			EXECUTE IMMEDIATE MI_STRSQL INTO MI_PAGADO;
			EXCEPTION WHEN NO_DATA_FOUND THEN
				RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
			END;
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
					PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PREDIODEUDA,
                       UN_TABLAERROR => 'IP_FACTURADOS'); 	
		END;
    MI_PARAMETRO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(
                                          UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'INCLUYE PAGOS DE ABONO EN ESTADO DE CUENTA'
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
                                         ,UN_FECHA_PAR => SYSDATE)
                           ,'NO');
    IF 	UN_PORUNUSUARIO <> 0 
		THEN 
			MI_CADENA := '	AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '''||UN_NUMORDEN||'''
                      AND IP_USUARIOS_PREDIAL.NOMBRE 		 = '''||UN_PROPIETARIO||'''
                      AND IP_USUARIOS_PREDIAL.NIT          = '''||UN_NIT||''' 
                      AND IP_FACTURADOS.PAGADO IN(0)
                      AND (IP_FACTURADOS.NOCOBRADO IN(0) OR IP_FACTURADOS.NOCOBRADO IS NULL)';
		ELSIF UN_PORPREDIO <> 0
		THEN 
			IF MI_PARAMETRO = 'SI'
			THEN 
				BEGIN
					MI_STRSQL := '	SELECT DISTINCT
									CASE WHEN IP_USUARIOS_PREDIAL.FECHAULTIMOABONO > IP_USUARIOS_PREDIAL.PAG_FEC THEN 1 ELSE 0 END VALOR
									FROM IP_USUARIOS_PREDIAL 
									WHERE IP_USUARIOS_PREDIAL.COMPANIA 	 = '''||UN_COMPANIA||'''
										AND IP_USUARIOS_PREDIAL.CODIGO   = '''||UN_CODPREDIO||'''
										AND IP_USUARIOS_PREDIAL.PAG_FEC IS NOT NULL
										AND IP_USUARIOS_PREDIAL.FECHAULTIMOABONO  IS NOT NULL';
				EXECUTE IMMEDIATE MI_STRSQL INTO MI_RTA;					
				EXCEPTION WHEN NO_DATA_FOUND THEN
					MI_RTA := 0;
				END;
				IF MI_RTA <> 0
				THEN 
					MI_CADENA := '	AND (IP_USUARIOS_PREDIAL.CODIGO      = '''||UN_CODPREDIO||'''
									AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '''||UN_NUMORDEN||'''
									AND IP_FACTURADOS.PAGADO            IN(0) 
									AND IP_FACTURADOS.NOCOBRADO         IN(0)
									OR (IP_USUARIOS_PREDIAL.NUMERO_ORDEN ='''||UN_NUMORDEN||'''
										AND IP_FACTURADOS.PAGADO 		IN(0)
										AND (IP_FACTURADOS.NOCOBRADO   	IS NULL 
										AND (IP_FACTURADOS.NOCOBRADO   	IN(0)
										OR IP_FACTURADOS.NOCOBRADO   	IS NULL))))';		
				END IF;

			IF MI_CADENA IS NULL
			THEN 
				MI_CADENA := '	AND IP_USUARIOS_PREDIAL.CODIGO = '''||UN_CODPREDIO||''' 
                        AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '''||UN_NUMORDEN||''' 
                        AND IP_FACTURADOS.PAGADO IN(0)
                        AND (IP_FACTURADOS.NOCOBRADO IN(0) OR IP_FACTURADOS.NOCOBRADO IS NULL)';
			END IF;
		END IF;
    END IF;
		IF UN_NOMBRECOMPANIA = 'ALCALDIA DE FUSAGASUGA'
		THEN 
			IF UN_PORUNUSUARIO <> 0
			THEN 
				MI_CADENA := '	AND IP_USUARIOS_PREDIAL.NIT           = '''||UN_NIT||'''
                        AND IP_USUARIOS_PREDIAL.NOMBRE        = '''||UN_PROPIETARIO||'''';
			ELSE
				MI_CADENA := '	AND IP_USUARIOS_PREDIAL.CODIGO        = '''||UN_CODPREDIO||'''';
			END IF;	
		END IF;
	RETURN 	MI_CADENA;
END FC_ARMACONSULTAESTADOCUENTA;	

FUNCTION FC_DIFERIR_ENC_MOD
  /*
    NAME              : FC_DIFERIR_ENC_MOD 
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : JUAN DANILO ORDUZ R
    DATE              : 21/08/2020
    TIME              : 16:48 
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : INGRESA A NOVEDADES LA NOVEDAD DE ENCARGOS CUANDO SE MODIFICA LA FECHA FINAL DE UN ENCARGO BUCARAMANGA
    @NAME             : difereirEncMod
    @METHOD           : POST

    */
(
  UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ID_EMPLEADO  IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_OPCION       IN  VARCHAR2                       DEFAULT NULL,
  UN_FECHAINICIO  IN  DATE,
  UN_FECHAFINAL   IN  DATE,
  UN_PORGASTO     IN  PCK_SUBTIPOS.TI_PORCENTAJE     DEFAULT 0,
  UN_SALARIO      IN  PCK_SUBTIPOS.TI_DOBLE          DEFAULT 0,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
  RETURN NUMBER AS 

MI_MES     NUMBER(2,0);
MI_ANO     NUMBER(4,0);
MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
MI_DIFERIDO PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_RTA     PCK_SUBTIPOS.TI_LOGICO;

BEGIN
MI_MES := TO_NUMBER(TO_CHAR(UN_FECHAINICIO, 'MM'));
MI_ANO := TO_NUMBER(TO_CHAR(UN_FECHAINICIO, 'YYYY'));
MI_DIFERIDO:= PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => UN_FECHAINICIO,
                                                UN_FECHAFIN => UN_FECHAFINAL
                                                );
   
MI_CONDICION :='  NOVEDADES.COMPANIA = '||UN_COMPANIA||'
		  AND NOVEDADES.ID_DE_EMPLEADO = '||UN_ID_EMPLEADO||'
		  AND NOVEDADES.PERIODO = '||UN_PERIODO||'
		  AND NOVEDADES.ID_DE_PROCESO = '||UN_PROCESO||'
		  AND NOVEDADES.ID_DE_CONCEPTO IN (10,11,15)
		  AND NOVEDADES.MES >= '||MI_MES||'
		  AND NOVEDADES.ANO >= '||MI_ANO;
          

    BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'NOVEDADES'
                                            ,UN_ACCION  => 'E'
                                            ,UN_CONDICION => MI_CONDICION);                                                           

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 
    
    END;      
    MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR_ENC(
                                     UN_COMPANIA     => UN_COMPANIA,
                                     UN_PROCESO      => UN_PROCESO,
                                     UN_ID_EMPLEADO  => UN_ID_EMPLEADO,
                                     UN_DIFERIDO     => MI_DIFERIDO,
                                     UN_OPCION       => UN_OPCION,
                                     UN_FECHAINICIO  => UN_FECHAINICIO,
                                     UN_PORGASTO     => UN_PORGASTO,
                                     UN_SALARIO      => UN_SALARIO,
                                     UN_USUARIO      => UN_USUARIO
                                     );
    
				
RETURN MI_RTA; 
 
END FC_DIFERIR_ENC_MOD;

END PCK_PREDIAL_COM2;