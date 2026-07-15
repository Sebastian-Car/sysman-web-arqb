create or replace PACKAGE BODY PCK_ALMACEN_COM2 AS

FUNCTION FC_GENCONSECGEN
  /*
    NAME              : FC_GENCONSECGEN  --> EN ACCESS GENCONSECGEN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 02/02/2016
    TIME              : 11:00 AM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : FUNCION QUE GENERA EL CONSECUTIVO PARA LOS MOVIMIENTOS DE ALMACÉN
    MODIFICATIONS     :
  */
  (
    UN_TABLAUNO IN  VARCHAR2,
    UN_TABLADOS IN  VARCHAR2,
    UN_CRITERIO IN  VARCHAR2,
    UN_CAMPO    IN  VARCHAR2
  )
  RETURN NUMBER
  AS
    MI_GENCONSECGEN NUMBER;
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  BEGIN
    BEGIN
      MI_STRSQL := 'SELECT
                         MAX('||UN_CAMPO||') UVALOR
                    FROM
                         '||UN_TABLADOS||'
                   INNER JOIN '||UN_TABLAUNO||'
                      ON '||UN_TABLADOS||'.COMPANIA = '||UN_TABLAUNO||'.COMPANIA
                     AND '||UN_TABLADOS||'.CODIGO = '||UN_TABLAUNO||'.TIPOMOVIMIENTO
                   WHERE
                         '||UN_CRITERIO||'
                   ORDER BY
                         '||UN_CAMPO||' DESC';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_GENCONSECGEN;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_GENCONSECGEN:= NULL;
    END;
    IF MI_GENCONSECGEN IS NULL THEN
        MI_GENCONSECGEN := 1;
    ELSE
        MI_GENCONSECGEN := MI_GENCONSECGEN + 1;
    END IF;
    RETURN MI_GENCONSECGEN;

  END FC_GENCONSECGEN;

  FUNCTION FC_CONSECENTDEV_ACTIVOS
  /*
    NAME              : FC_CONSECENTDEV_ACTIVOS  --> EN ACCESS CONSECENTDEV_ACTIVOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 02/02/2016
    TIME              : 12:00 PM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Retorna el consecutivo de EntDevolutivo de almacén
    MODIFICATIONS     :
  */
  (
    UN_TIPOMOVIMIENTO  IN     VARCHAR2,
    UN_COMPANIA        IN     PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASE           IN     VARCHAR2,
    UN_MODULO          IN     NUMBER
  )
  RETURN NUMBER
  AS

    MI_CONSECENTDEV_ACTIVOS NUMBER;
    MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
    MI_RESULTADO            NUMBER;
    MI_RESVCHAR             VARCHAR2(32000 CHAR);
  BEGIN
    MI_STRSQL := 'SELECT
                         INVENTARIOINICIAL
                    FROM
                         TIPOMOVIMIENTO
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND CODIGO   = '''||UN_TIPOMOVIMIENTO||'''';

    EXECUTE IMMEDIATE MI_STRSQL INTO MI_RESULTADO;

    IF MI_RESULTADO NOT IN (0) THEN
        MI_CONSECENTDEV_ACTIVOS := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CAMBIOS_TIPOACTIVO',
                                                                    UN_CRITERIO => '    COMPANIA          = '''||UN_COMPANIA||'''
                                                                                    AND TIPOMOVIMIENTO    = ''' ||UN_TIPOMOVIMIENTO||'''
                                                                                    AND INVENTARIOINICIAL = 0
                                                                                    AND TIPOMOVIMIENTO    <>''RAI''',
                                                                    UN_CAMPO    => 'CONSECUTIVO');
        RETURN MI_CONSECENTDEV_ACTIVOS;
    END IF;

    MI_RESVCHAR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'CONSECUTIVO UNICO',
                                         UN_MODULO    => UN_MODULO,
                                         UN_FECHA_PAR => SYSDATE);

    IF MI_RESVCHAR = NULL THEN
        MI_CONSECENTDEV_ACTIVOS := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CAMBIOS_TIPOACTIVO',
                                                                    UN_CRITERIO => '    COMPANIA       = '''||UN_COMPANIA||'''
                                                                                    AND TIPOMOVIMIENTO = ''' ||UN_TIPOMOVIMIENTO||'''',
                                                                    UN_CAMPO    => 'CONSECUTIVO');
        RETURN MI_CONSECENTDEV_ACTIVOS;
    ELSE
        IF MI_RESVCHAR = 'N' THEN
            MI_CONSECENTDEV_ACTIVOS := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CAMBIOS_TIPOACTIVO',
                                                                        UN_CRITERIO =>'     COMPANIA       = '''||UN_COMPANIA||'''
                                                                                        AND TIPOMOVIMIENTO = ''' ||UN_TIPOMOVIMIENTO||'''',
                                                                        UN_CAMPO    => 'CONSECUTIVO');
            IF MI_CONSECENTDEV_ACTIVOS = 1 THEN

                MI_STRSQL := 'SELECT
                                     NUMEROINICIAL
                                FROM
                                     TIPOMOVIMIENTO
                               WHERE
                                     COMPANIA = '''||UN_COMPANIA||'''
                                 AND CODIGO   = '''||UN_TIPOMOVIMIENTO||'''';

                EXECUTE IMMEDIATE MI_STRSQL INTO MI_RESULTADO;
                MI_CONSECENTDEV_ACTIVOS := MI_RESULTADO;
            END IF;
            RETURN MI_CONSECENTDEV_ACTIVOS;
        ELSE
            MI_CONSECENTDEV_ACTIVOS := PCK_ALMACEN_COM2.FC_GENCONSECGEN(UN_TABLAUNO => 'CAMBIOS_TIPOACTIVO',
                                                                        UN_TABLADOS => 'TIPOMOVIMIENTO',
                                                                        UN_CRITERIO => '    CAMBIOS_TIPOACTIVO.COMPANIA = '''||UN_COMPANIA||'''
                                                                                        AND TIPOMOVIMIENTO.CLASE        = '''||UN_CLASE||'''
                                                                                        AND INVENTARIOINICIAL           = 0
                                                                                        AND TIPOMOVIMIENTO             <> ''RAI''',
                                                                        UN_CAMPO    => 'CONSECUTIVO');
            IF MI_CONSECENTDEV_ACTIVOS <> 1 THEN
                RETURN MI_CONSECENTDEV_ACTIVOS;
            ELSE
                IF UN_CLASE = 'E' THEN
                    MI_RESULTADO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                          UN_NOMBRE    => 'NUMERO INICIAL ENTRADA',
                                                          UN_MODULO    => UN_MODULO,
                                                          UN_FECHA_PAR => 'SYSDATE');
                ELSE
                    MI_RESULTADO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                          UN_NOMBRE    => 'NUMERO INICIAL SALIDA',
                                                          UN_MODULO    => UN_MODULO,
                                                          UN_FECHA_PAR => 'SYSDATE');
                END IF;

                IF MI_RESULTADO = NULL THEN
                    RETURN MI_CONSECENTDEV_ACTIVOS;
                ELSE
                    RETURN MI_RESULTADO;
                END IF;
            END IF;
        END IF;
    END IF;


  END FC_CONSECENTDEV_ACTIVOS;

  FUNCTION FC_CAMBIARTIPOACTIVO
  /*
    NAME              : FC_CAMBIARTIPOACTIVO  --> EN ACCESS CAMBIARTIPOACTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 04/02/2016
    TIME              : 08:10 AM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 11/05/2017
    TIME              : 4:43 pm
    DESCRIPTION       : Realiza las respectivas actualizaciones para el traslado de los activos.
    MODIFICATIONS     : Se agregan campos de auditoria.
  */
  (
    UN_COMPANIA        IN      PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOVIMIENTO  IN     VARCHAR2,
    UN_CONSECUTIVO     IN     NUMBER,
    UN_FECHA           IN     DATE,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER
  AS
    MI_CAMBIARTIPOACTIVO    NUMBER;
    MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_RS                   SYS_REFCURSOR;
    MI_RSCONSULTA           PCK_SUBTIPOS.TI_STRSQL;
    MI_RSELEMENTO           VARCHAR2(16 CHAR);
    MI_RSSERIE              NUMBER(15,0);
    MI_RSTIPOACTIVO_NUE     VARCHAR(3 CHAR);
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_RSCONSULTA := 'SELECT
                             ELEMENTO,
                             SERIE,
                             TIPOACTIVO_NUE
                        FROM
                             D_CAMBIOS_TIPOACTIVO
                       WHERE
                             COMPANIA       = '''||UN_COMPANIA||'''
                         AND NUMERO         = '||UN_CONSECUTIVO||'
                         AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                         AND TIPOACTIVO_ANT IS NOT NULL';

    OPEN MI_RS FOR MI_RSCONSULTA;
        LOOP
            FETCH MI_RS INTO MI_RSELEMENTO, MI_RSSERIE, MI_RSTIPOACTIVO_NUE;
            EXIT WHEN MI_RS%NOTFOUND;

            BEGIN
              BEGIN
                  MI_TABLA     := 'DEVOLUTIVO';

                  MI_CAMPOS    := 'DEVOLUTIVO.NIIF_TIPO_ACTIVO = '''||MI_RSTIPOACTIVO_NUE||''',
                                   DEVOLUTIVO.FECHAENTRADA     = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY''),
                                   DEVOLUTIVO.FECHAULTMOV      = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY''),
                                   DEVOLUTIVO.DATE_MODIFIED = SYSDATE , DEVOLUTIVO.MODIFIED_BY = '''||UN_USUARIO||'''';

                  MI_CONDICION := '   DEVOLUTIVO.COMPANIA = '''||UN_COMPANIA||'''
                                  AND DEVOLUTIVO.ELEMENTO = '''||MI_RSELEMENTO||'''
                                  AND DEVOLUTIVO.SERIE    =    '||MI_RSSERIE;

                  MI_CAMBIARTIPOACTIVO := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                             UN_ACCION    => 'M',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);

                   MI_MSGERROR(1).CLAVE := 'NIIF_TIPO_ACTIVO';
                   MI_MSGERROR(1).VALOR := MI_RSTIPOACTIVO_NUE;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_TIPONIFF,
                                       UN_REEMPLAZOS => MI_MSGERROR);
            END;
         BEGIN
            BEGIN
                MI_TABLA     := 'D_MOVIMIENTO';

                MI_CAMPOS    := 'D_MOVIMIENTO.NIIF_TIPO_ACTIVO = '''||MI_RSTIPOACTIVO_NUE||''',
                                 D_MOVIMIENTO.DATE_MODIFIED = SYSDATE , D_MOVIMIENTO.MODIFIED_BY = '''||UN_USUARIO||'''';

                MI_CONDICION := '   D_MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                AND D_MOVIMIENTO.ELEMENTO = '''||MI_RSELEMENTO||'''
                                AND D_MOVIMIENTO.SERIE    = '||MI_RSSERIE;

                MI_CAMBIARTIPOACTIVO := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);

                 MI_MSGERROR(1).CLAVE := 'NIIF_TIPO_ACTIVO';
                 MI_MSGERROR(1).VALOR := MI_RSTIPOACTIVO_NUE;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
             END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_TIPONIFF,
                                       UN_REEMPLAZOS => MI_MSGERROR);
            END;
         END LOOP;
    CLOSE MI_RS;
   BEGIN
      BEGIN

          MI_TABLA     := 'D_CAMBIOS_TIPOACTIVO';

          MI_CAMPOS    := ' D_CAMBIOS_TIPOACTIVO.IND_REG = -1,D_CAMBIOS_TIPOACTIVO.DATE_MODIFIED = SYSDATE ,
                            D_CAMBIOS_TIPOACTIVO.MODIFIED_BY = '''||UN_USUARIO||'''';

          MI_CONDICION := '    COMPANIA       = '''||UN_COMPANIA||'''
                           AND NUMERO         = '||UN_CONSECUTIVO||'
                           AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''';

          MI_CAMBIARTIPOACTIVO := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);

          MI_MSGERROR(1).CLAVE := 'IND_REG';
          MI_MSGERROR(1).VALOR := MI_RSTIPOACTIVO_NUE;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
       END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDA_CAMB_TIPOACT,
                             UN_REEMPLAZOS => MI_MSGERROR);
  END;
    BEGIN
        BEGIN
            MI_TABLA     := 'CAMBIOS_TIPOACTIVO';

            MI_CAMPOS    := ' CAMBIOS_TIPOACTIVO.REGISTRADO = -1, CAMBIOS_TIPOACTIVO.DATE_MODIFIED = SYSDATE ,
                              CAMBIOS_TIPOACTIVO.MODIFIED_BY = '''||UN_USUARIO||'''';

            MI_CONDICION := '    COMPANIA       = '''||UN_COMPANIA||'''
                             AND CONSECUTIVO    = '||UN_CONSECUTIVO||'
                             AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''';

            MI_CAMBIARTIPOACTIVO := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);
            MI_MSGERROR(1).CLAVE := 'IND_REG';
            MI_MSGERROR(1).VALOR := MI_RSTIPOACTIVO_NUE;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
       END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST,
                                 UN_REEMPLAZOS => MI_MSGERROR);
     END;
    RETURN MI_CAMBIARTIPOACTIVO;
  END FC_CAMBIARTIPOACTIVO;

  FUNCTION FC_REVERSADOCUMENTOAS
  /*
    NAME              : FC_REVERSADOCUMENTOAS  --> EN ACCESS REVERSADOCUMENTOAS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 08/02/2016
    TIME              : 05:00 PM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
  */(
      UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,
      UN_TIPOMOVASOCIADO IN VARCHAR2,
      UN_MOVASOCIADO     IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_CODIGOELEMENTO  IN VARCHAR2,
      UN_CANTIDAD        IN NUMBER
  )
  RETURN NUMBER
  AS

      MI_REVERSA                VARCHAR2(32000 CHAR);
      MI_RSCANTIDADPORENTREGAR  NUMBER;
      MI_RSCANTIDADAPROBADA     NUMBER;
      MI_RSCANTIDAD             NUMBER;
      MI_RSSALDOCANT            NUMBER;
      MI_RSORDENDESUM           NUMBER;
      MI_RSDEPENDENCIA          VARCHAR2(12 CHAR);
      MI_RSRID                  VARCHAR2(50 CHAR);
      MI_CANTIDAD1              NUMBER;
      MI_CLASEBODEGA            VARCHAR2(2 CHAR);
      MI_RTA                    PCK_SUBTIPOS.TI_RTA_ACME;
      RSREVERSA                 SYS_REFCURSOR;
      MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
      MI_CANTIDAD1 := UN_CANTIDAD;
      CASE
          WHEN UN_TIPOMOVASOCIADO='R'
              THEN
                  MI_REVERSA :=   'SELECT
                                          ROWID RID,
                                          CANTIDADAPROBADA,
                                          CANTIDADPORENTREGAR
                                     FROM
                                          D_ORDENDESUMINISTRO
                                    WHERE
                                          COMPANIA          = ''' || UN_COMPANIA||'''
                                      AND ORDENDESUMINISTRO = ' || UN_MOVASOCIADO || '
                                      AND ELEMENTO          = ''' || UN_CODIGOELEMENTO || '''
                                      AND IND_REG           = -1';

                  OPEN RSREVERSA FOR MI_REVERSA;
                      LOOP
                          FETCH RSREVERSA INTO MI_RSRID,MI_RSCANTIDADAPROBADA, MI_RSCANTIDADPORENTREGAR;
                              EXIT WHEN RSREVERSA%NOTFOUND;
                              IF MI_RSCANTIDADPORENTREGAR = MI_RSCANTIDADAPROBADA THEN
                                 CONTINUE;
                              ELSIF MI_RSCANTIDADAPROBADA > MI_RSCANTIDADPORENTREGAR THEN
                                  IF MI_RSCANTIDADAPROBADA >= MI_CANTIDAD1 THEN
                                      IF (MI_RSCANTIDADPORENTREGAR+ MI_CANTIDAD1) <= MI_RSCANTIDADAPROBADA THEN
                                          MI_RSCANTIDADPORENTREGAR := MI_RSCANTIDADPORENTREGAR + MI_CANTIDAD1;
                                          MI_CANTIDAD1             := 0;
                                      ELSE
                                          MI_CANTIDAD1             := MI_CANTIDAD1 - (MI_RSCANTIDADAPROBADA - MI_RSCANTIDADPORENTREGAR);
                                          MI_RSCANTIDADPORENTREGAR := MI_RSCANTIDADAPROBADA;
                                      END IF;
                                  ELSE
                                      MI_CANTIDAD1 := MI_CANTIDAD1 - (MI_RSCANTIDADAPROBADA - MI_RSCANTIDADPORENTREGAR);
                                      MI_RSCANTIDADPORENTREGAR := MI_RSCANTIDADAPROBADA;
                                  END IF;
                                  BEGIN
                                    BEGIN
                                       MI_TABLA     := 'D_ORDENDESUMINISTRO';
                                       MI_CAMPOS    := 'CANTIDADAPROBADA    = '||MI_RSCANTIDADAPROBADA||',
                                                        CANTIDADPORENTREGAR = ' || MI_RSCANTIDADPORENTREGAR;
                                       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA  => MI_TABLA,
                                                                    UN_ACCION => 'M',
                                                                    UN_CAMPOS => MI_CAMPOS,
                                                                    UN_ROWID  => MI_RSRID);
                                MI_MSGERROR(1).CLAVE := 'CANTIDADPORENTREGAR';
                                        MI_MSGERROR(1).VALOR := MI_RSCANTIDADPORENTREGAR;
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                    END;
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_D_ORDENSUMI,
                                                               UN_REEMPLAZOS => MI_MSGERROR);
                                 END;
                                  IF MI_RSCANTIDADPORENTREGAR > 0 THEN
                                     BEGIN
                                      BEGIN
                                         MI_TABLA     := 'ORDENDESUMINISTRO';
                                         MI_CAMPOS    := 'VACIA = 0';
                                         MI_CONDICION :=  '    COMPANIA = ''' || UN_COMPANIA||'''
                                                           AND NUMERO   = ' || UN_MOVASOCIADO;
                                         MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                      UN_ACCION     => 'M',
                                                                      UN_CAMPOS     => MI_CAMPOS,
                                                                      UN_CONDICION  => MI_CONDICION);
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                     END;
                                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_ORDENSUMI,
                                                                UN_REEMPLAZOS => MI_MSGERROR);
                                   END;
                                 END IF;
                                  EXIT WHEN MI_CANTIDAD1 = 0;
                              END IF;
                      END LOOP;
                  CLOSE RSREVERSA;
          WHEN UN_TIPOMOVASOCIADO IN ('C','J','CCM','V','S','CDC', 'CDS', 'ODC')
              THEN
                  CASE
                      WHEN UN_TIPOMOVASOCIADO IN ('C','CCM','CDC', 'CDS', 'ODC')
                          THEN
                              MI_REVERSA :=   'SELECT
                                                      ROWID RID,
                                                      CANTIDAD,
                                                      SALDOCANT,
                                                      ORDENDESUMINISTRO,
                                                      DEPENDENCIA
                                                 FROM
                                                      D_ORDENDECOMPRA
                                                WHERE
                                                      COMPANIA      = ''' || UN_COMPANIA || '''
                                                  AND CLASEORDEN    = '''|| UN_TIPOMOVASOCIADO || '''
                                                  AND ORDENDECOMPRA = '||UN_MOVASOCIADO||'
                                                  AND ELEMENTO      = ''' || UN_CODIGOELEMENTO ||'''';
                      WHEN UN_TIPOMOVASOCIADO='J'
                          THEN
                              MI_REVERSA :=   'SELECT
                                                      ROWID RID,
                                                      CANTIDAD,
                                                      SALDOCANT,
                                                      ORDENDESUMINISTRO,
                                                      DEPENDENCIA
                                                 FROM
                                                      D_ORDENDECOMPRA
                                                WHERE
                                                      COMPANIA      = ''' || UN_COMPANIA || '''
                                                  AND CLASEORDEN    = ''CCM''
                                                  AND ORDENDECOMPRA = '||UN_MOVASOCIADO||'
                                                  AND ELEMENTO      = ''' || UN_CODIGOELEMENTO ||'''';
                      WHEN UN_TIPOMOVASOCIADO='V'
                          THEN
                              MI_REVERSA :=   'SELECT
                                                      ROWID RID,
                                                      CANTIDAD,
                                                      SALDOCANT,
                                                      ORDENDESUMINISTRO,
                                                      DEPENDENCIA
                                                 FROM
                                                      D_ORDENDECOMPRA
                                                WHERE
                                                      COMPANIA      = ''' || UN_COMPANIA || '''
                                                  AND CLASEORDEN    = ''CDC''
                                                  AND ORDENDECOMPRA = '||UN_MOVASOCIADO||'
                                                  AND ELEMENTO      = ''' || UN_CODIGOELEMENTO ||'''';
                      WHEN UN_TIPOMOVASOCIADO='S'
                          THEN
                              MI_REVERSA :=   'SELECT
                                                      ROWID RID,
                                                      CANTIDAD,
                                                      SALDOCANT,
                                                      ORDENDESUMINISTRO,
                                                      DEPENDENCIA
                                                 FROM
                                                      D_ORDENDECOMPRA
                                                WHERE
                                                      COMPANIA      = ''' || UN_COMPANIA || '''
                                                  AND CLASEORDEN    = ''CDS''
                                                  AND ORDENDECOMPRA = '||UN_MOVASOCIADO||'
                                                  AND ELEMENTO      = ''' || UN_CODIGOELEMENTO ||'''';
                  END CASE;
                  OPEN RSREVERSA FOR MI_REVERSA;
                      LOOP
                          FETCH RSREVERSA INTO MI_RSRID,MI_RSCANTIDAD, MI_RSSALDOCANT,MI_RSORDENDESUM, MI_RSDEPENDENCIA;
                          EXIT WHEN RSREVERSA%NOTFOUND;
                              MI_CLASEBODEGA := PCK_ALMACEN_COM2.FC_GETCLASEBODEGA(UN_COMPANIA    => UN_COMPANIA,
                                                                                   UN_DEPENDENCIA => MI_RSDEPENDENCIA);
                              IF MI_RSORDENDESUM <> 0 AND MI_CLASEBODEGA = 20 THEN
                                  MI_RTA := PCK_ALMACEN_COM2.FC_REVERSADOCUMENTOAS(UN_COMPANIA        => UN_COMPANIA,
                                                                                   UN_TIPOMOVASOCIADO => 'R',
                                                                                   UN_MOVASOCIADO     => MI_RSORDENDESUM,
                                                                                   UN_CODIGOELEMENTO  => UN_CODIGOELEMENTO,
                                                                                   UN_CANTIDAD        => MI_CANTIDAD1);
                              END IF;
                              IF MI_RSCANTIDAD > MI_CANTIDAD1 AND MI_RSSALDOCANT + MI_CANTIDAD1 <= MI_RSCANTIDAD THEN
                                  MI_RSSALDOCANT := MI_RSSALDOCANT + MI_CANTIDAD1;
                                  MI_CANTIDAD1   := 0;
                              ELSIF MI_RSCANTIDAD > MI_CANTIDAD1 AND MI_RSSALDOCANT + MI_CANTIDAD1 > MI_RSCANTIDAD THEN
                                  MI_CANTIDAD1   := MI_CANTIDAD1 - (MI_RSCANTIDAD - MI_RSSALDOCANT);
                                  MI_RSSALDOCANT := MI_RSCANTIDAD;
                              ELSIF MI_RSCANTIDAD <= MI_CANTIDAD1 THEN
                                  MI_CANTIDAD1   := MI_CANTIDAD1 - (MI_RSCANTIDAD - MI_RSSALDOCANT);
                                  MI_RSSALDOCANT := MI_RSCANTIDAD;
                              END IF;
                              BEGIN
                                BEGIN
                                   MI_TABLA     := 'D_ORDENDECOMPRA';
                                   MI_CAMPOS    := 'SALDOCANT = '||MI_RSSALDOCANT;
                                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA  => MI_TABLA,
                                                                UN_ACCION => 'M',
                                                                UN_CAMPOS => MI_CAMPOS,
                                                                UN_ROWID  => MI_RSRID);
                                    MI_MSGERROR(1).CLAVE := 'SALDOANT';
                                    MI_MSGERROR(1).VALOR := MI_RSSALDOCANT;
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                 END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUA_SALDO_ANT,
                                                           UN_REEMPLAZOS => MI_MSGERROR);
                              END;
                              CASE
                                  WHEN UN_TIPOMOVASOCIADO IN ('C','CCM','CDC', 'CDS', 'ODC')
                                      THEN
                                          BEGIN
                                            BEGIN
                                               MI_TABLA     := 'ORDENDECOMPRA';
                                               MI_CAMPOS    := 'VACIA = ''N''';
                                               MI_CONDICION :=  '     COMPANIA   = ''' || UN_COMPANIA||'''
                                                                  AND CLASEORDEN = '''||UN_TIPOMOVASOCIADO||'''
                                                                  AND NUMERO     = ' || UN_MOVASOCIADO;
                                               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                            UN_ACCION     => 'M',
                                                                            UN_CAMPOS     => MI_CAMPOS,
                                                                            UN_CONDICION  => MI_CONDICION);
                                            MI_MSGERROR(1).CLAVE := 'NUMERO';
                                            MI_MSGERROR(1).VALOR := UN_MOVASOCIADO;
                                            MI_MSGERROR(2).CLAVE := 'CLASEORDEN';
                                            MI_MSGERROR(2).VALOR := UN_TIPOMOVASOCIADO;
                                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                           END;
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_VACIA,
                                                                     UN_REEMPLAZOS => MI_MSGERROR);
                                         END;
                                  WHEN UN_TIPOMOVASOCIADO='J'
                                      THEN
                                        BEGIN
                                          BEGIN
                                           MI_TABLA     := 'ORDENDECOMPRA';
                                           MI_CAMPOS    := 'VACIA = ''N''';
                                           MI_CONDICION :=  '   COMPANIA   = ''' || UN_COMPANIA||'''
                                                            AND CLASEORDEN = ''CCM''
                                                            AND NUMERO     = ' || UN_MOVASOCIADO;
                                           MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                        UN_ACCION     => 'M',
                                                                        UN_CAMPOS     => MI_CAMPOS,
                                                                        UN_CONDICION  => MI_CONDICION);
                                            MI_MSGERROR(1).CLAVE := 'NUMERO';
                                            MI_MSGERROR(1).VALOR := UN_MOVASOCIADO;
                                            MI_MSGERROR(2).CLAVE := 'CLASEORDEN';
                                            MI_MSGERROR(2).VALOR := 'CCM';
                                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                          END;
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_VACIA,
                                                                     UN_REEMPLAZOS => MI_MSGERROR);
                                        END;
                                 WHEN UN_TIPOMOVASOCIADO='V'
                                      THEN
                                      BEGIN
                                        BEGIN
                                          MI_TABLA     := 'ORDENDECOMPRA';
                                          MI_CAMPOS    := 'VACIA = ''N''';
                                          MI_CONDICION :=  '    COMPANIA   = ''' || UN_COMPANIA||'''
                                                            AND CLASEORDEN = ''CDC''
                                                            AND NUMERO     = ' || UN_MOVASOCIADO;
                                          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                       UN_ACCION     => 'M',
                                                                       UN_CAMPOS     => MI_CAMPOS,
                                                                       UN_CONDICION  => MI_CONDICION);
                                          MI_MSGERROR(1).CLAVE := 'NUMERO';
                                          MI_MSGERROR(1).VALOR := UN_MOVASOCIADO;
                                          MI_MSGERROR(2).CLAVE := 'CLASEORDEN';
                                          MI_MSGERROR(2).VALOR := 'CDC';
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                        END;
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_VACIA,
                                                                   UN_REEMPLAZOS => MI_MSGERROR);
                                      END;
                                  WHEN UN_TIPOMOVASOCIADO='S'
                                      THEN
                                      BEGIN
                                        BEGIN
                                          MI_TABLA     := 'ORDENDECOMPRA';
                                          MI_CAMPOS    := 'VACIA = ''N''';
                                          MI_CONDICION :=  '    COMPANIA   = ''' || UN_COMPANIA||'''
                                                            AND CLASEORDEN = ''CDS''
                                                            AND NUMERO     = ' || UN_MOVASOCIADO;
                                          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                       UN_ACCION     => 'M',
                                                                       UN_CAMPOS     => MI_CAMPOS,
                                                                       UN_CONDICION  => MI_CONDICION);
                                          MI_MSGERROR(1).CLAVE := 'NUMERO';
                                          MI_MSGERROR(1).VALOR := UN_MOVASOCIADO;
                                          MI_MSGERROR(2).CLAVE := 'CLASEORDEN';
                                          MI_MSGERROR(2).VALOR := 'CDS';
                                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                        END;
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_VACIA,
                                                                   UN_REEMPLAZOS => MI_MSGERROR);
                                      END;
                              END CASE;
                              EXIT WHEN MI_CANTIDAD1 = 0;
                      END LOOP;
                  CLOSE RSREVERSA;
      ELSE 
        MI_CANTIDAD1 := 0;
      END CASE;
      RETURN -1;
  END FC_REVERSADOCUMENTOAS;

  FUNCTION FC_GETCLASEBODEGA(
  /*
    NAME              : FC_GETCLASEBODEGA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 08/02/2016
    TIME              : 05:00 PM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Retorna la clase de bodega de una dependencia ingresada por parámetro.
    MODIFICATIONS     :
  */
      UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_DEPENDENCIA IN VARCHAR2
  )
  RETURN VARCHAR2
  AS
  MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_CLASEBODEGA      VARCHAR2(2 CHAR);
  BEGIN
      MI_STRSQL := 'SELECT
                           CLASE_BODEGA
                      FROM
                           DEPENDENCIA
                     WHERE
                           COMPANIA = '''||UN_COMPANIA||'''
                       AND CODIGO = '''||UN_DEPENDENCIA||'''';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CLASEBODEGA;
      RETURN MI_CLASEBODEGA;
  END FC_GETCLASEBODEGA;

  FUNCTION FC_ACTRES
  /*
    NAME              : FC_ACTRES  --> EN ACCESS ACTRES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : OSCAR TORRES CORREDOR
    DATE MIGRADOR     : 10/02/2016
    TIME              : 10:00 AM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Realiza actualización a la tabla inventario, a los campos especificados por el parámetro de la función
    MODIFICATIONS     :
  */
  (
    UN_STRELEMENT     IN VARCHAR2,
    UN_DIF            IN NUMBER,
    UN_NOMBRECAMPO    IN VARCHAR2,
    UN_STRCOMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
  ) RETURN VARCHAR2 AS

    MI_TRANSACCION    VARCHAR2(200 CHAR);
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
   BEGIN
     BEGIN
      BEGIN
        MI_TABLA     := 'INVENTARIO';
        MI_CAMPOS    := UN_NOMBRECAMPO||' = '|| UN_NOMBRECAMPO||' + '||UN_DIF;
        MI_CONDICION := '    COMPANIA       = ''' || UN_STRCOMPANIA || '''
                         AND CODIGOELEMENTO = ''' || UN_STRELEMENT || '''';
        MI_TRANSACCION := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
         MI_MSGERROR(1).CLAVE := 'UN_NOMBRECAMPO';
         MI_MSGERROR(1).VALOR := UN_NOMBRECAMPO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_INVEN,
                               UN_REEMPLAZOS => MI_MSGERROR);
  END;
  RETURN MI_TRANSACCION;
  END FC_ACTRES;

FUNCTION FC_RETRESPONSABLE
 /*
    NAME              : FC_RETRESPONSABLE  --> EN ACCESS RETRESPONSABLE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : OSCAR TORRES CORREDOR
    DATE MIGRADOR     : 10/02/2016
    TIME              : 10:00 AM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Retorna el responsable una dependencia
    MODIFICATIONS     :
  */
(
  UN_STRDEPENDENCIA IN VARCHAR2,
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA

) RETURN VARCHAR2 AS
  MI_RESPONSABLE VARCHAR(3200 CHAR);
  BEGIN
  -- FIRST()
  SELECT
         RESPONSABLE INTO  MI_RESPONSABLE
    FROM
         DEPENDENCIA_RESPONSABLE
   WHERE
         DEPENDENCIA = UN_STRDEPENDENCIA
     AND COMPANIA    = UN_COMPANIA
     AND JEFEUNIDAD NOT IN(0)
     AND ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_RESPONSABLE := PCK_DATOS.FC_CONS_TERCERO;
  RETURN MI_RESPONSABLE;
END FC_RETRESPONSABLE;

FUNCTION FC_ACTUALIZA_DOC_ASOCIADO
 /*
    NAME              : FC_ACTUALIZA_DOC_ASOCIADO  --> EN ACCESS ACTUALIZADOCASOCIADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : OSCAR TORRES CORREDOR
    DATE MIGRADOR     : 10/02/2016
    TIME              : 10:00 AM
    SOURCE MODULE     : SysmanAl2015.12.02_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
  */
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA ,
  UN_TIPOMOVIMIENTO        IN VARCHAR2 ,
  UN_MOVIMIENTO            IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_INDREG                IN NUMBER DEFAULT 0
) RETURN NUMBER AS
  MI_TOTALDOC              NUMBER;
  MI_INTCODIGO             NUMBER;
  MI_IND_REG               NUMBER;
  MI_CANT_ACTUALIZAR       NUMBER;
  MI_CLASEORDEN            VARCHAR2(3200 CHAR);
  MI_DEPENDENCIA           VARCHAR2(3200 CHAR);
  MI_RESPONSABLE           VARCHAR2(3200 CHAR);
  MI_PARAMETRO             VARCHAR2(200 CHAR):='20';
--VARIABLES DEL CURSOR RSTIPOMOVIMIENTO
  MI_CLASE                VARCHAR2(200 CHAR);
  MI_CONCEPTO             VARCHAR2(200 CHAR);
  MI_COMPANIA             VARCHAR2(200 CHAR);
  MI_CLASE_DOC_ASOCIADO   VARCHAR2(200 CHAR);
  MI_SIGNO                VARCHAR2(200 CHAR);
  MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
  MI_RSSUMAORDEN          NUMBER;
  MI_RSDOCASOCIADO        NUMBER;
-- VARIABLES DEL CURSOR RSMOVIMIENTOS
  MI_TERCERO              VARCHAR2(200 CHAR);
  MI_TIPOMOVASOCIADO      VARCHAR2(200 CHAR);
  MI_MOVASOCIADO          VARCHAR2(200 CHAR);
  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TIPOELEMENTO         VARCHAR2(200 CHAR);
  MI_ELEMENTO             NUMBER;
  MI_CANTIDAD             NUMBER;
  BEGIN
    SELECT
         COMPANIA,
         CLASEDOCASOCIADO,
         CLASE,
         CONCEPTO
         INTO
         MI_COMPANIA,
         MI_CLASE_DOC_ASOCIADO,
         MI_CLASE,
         MI_CONCEPTO
    FROM
         TIPOMOVIMIENTO
   WHERE
         COMPANIA  =  UN_COMPANIA
     AND CODIGO    =  UN_TIPOMOVIMIENTO;
  SELECT
         TERCERO,
         TIPOMOVASOCIADO,
         MOVASOCIADO
         INTO
         MI_TERCERO,
         MI_TIPOMOVASOCIADO,
         MI_MOVASOCIADO
    FROM
         MOVIMIENTO
   WHERE
         COMPANIA       = UN_COMPANIA
     AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
     AND NUMERO         = UN_MOVIMIENTO;
  IF  MI_CLASE_DOC_ASOCIADO = 'I' THEN
      MI_IND_REG := -1;
  ELSE
      MI_IND_REG := 0;
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'C' OR MI_CLASE_DOC_ASOCIADO = 'ODC' OR MI_CLASE_DOC_ASOCIADO = 'OC' THEN
    MI_CLASEORDEN := CASE WHEN MI_CLASE_DOC_ASOCIADO = 'OC' THEN 'OC' ELSE 'ODC' END;
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'I' THEN
      MI_CLASEORDEN := 'ODI';
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'S' OR MI_CLASE_DOC_ASOCIADO = 'CDS' OR MI_CLASE_DOC_ASOCIADO = 'CS' THEN
   MI_CLASEORDEN := CASE WHEN MI_CLASE_DOC_ASOCIADO = 'CS' THEN 'CS' ELSE 'CDS' END;
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'V' OR MI_CLASE_DOC_ASOCIADO = 'CDC' OR MI_CLASE_DOC_ASOCIADO = 'CV' THEN
    MI_CLASEORDEN := CASE WHEN MI_CLASE_DOC_ASOCIADO = 'CV' THEN 'CV' ELSE 'CDC' END;
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'J' THEN
      MI_CLASEORDEN := 'CCM';
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'PD' THEN
      MI_CLASEORDEN := 'PD';
  END IF;
  IF  MI_CLASE_DOC_ASOCIADO = 'CM' THEN
      MI_CLASEORDEN := 'CM';
  END IF;
  --RSTIPOMOVIMIENTO
  DECLARE CURSOR RSD_MOVIMIENTOS IS (SELECT
                                           COMPANIA,
                                           ELEMENTO,
                                           CODIGO,
                                           SERIE,
                                           FECHA,
                                           CANTIDAD,
                                           VALORUNITARIO,
                                           VALORTOTAL,
                                           TIPOMOVIMIENTO,
                                           MOVIMIENTO,
                                           ORDENDESUMINISTRO,
                                           CODIGO_AFECT,
                                           TIPOMOVASOCIADO,
                                           MOVASOCIADO
                                      FROM
                                           D_MOVIMIENTO
                                     WHERE
                                           COMPANIA        =  MI_COMPANIA
                                       AND TIPOMOVIMIENTO  =  UN_TIPOMOVIMIENTO
                                       AND MOVIMIENTO      =  UN_MOVIMIENTO
                                       AND (IND_REG         =  MI_IND_REG OR CASE WHEN MI_CLASE_DOC_ASOCIADO = 'M' THEN 0 ELSE UN_INDREG END = -1)); --JM 26/02/2025 CC 1013
  BEGIN
  --FOR RSMOVIMIENTO IN RSMOVIMIENTOS LOOP
      IF  MI_CONCEPTO = 'L' AND MI_CLASE = 'S' THEN
          FOR RSD_MOVIMIENTO IN RSD_MOVIMIENTOS LOOP
           BEGIN
              BEGIN
                 MI_TABLA     := 'RESPONSABILIDADES';
                 MI_CAMPOS    := 'COMPANIA,
                                  RESPONSABLE,
                                  CODIGO,
                                  ELEMENTO,
                                  SERIE,
                                  FECHAPERDIDA,
                                  CANTIDAD,
                                  VALORUNITARIO,
                                  VALORTOTAL';
                 MI_VALORES   := ''''|| RSD_MOVIMIENTO.COMPANIA||''',
                                 '''|| MI_TERCERO ||''',
                                 '|| PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'RESPONSABILIDADES',
                                                                      UN_CRITERIO => '    COMPANIA    = '''|| MI_COMPANIA ||'''
                                                                                      AND RESPONSABLE =  '''|| MI_TERCERO ||'''',
                                                                      UN_CAMPO    => 'CODIGO',
                                                                      UN_INICIAL  => '1')||',
                                 '''|| RSD_MOVIMIENTO.ELEMENTO ||''',
                                 '|| RSD_MOVIMIENTO.SERIE ||',
                                 '|| 'TO_DATE('''||RSD_MOVIMIENTO.FECHA ||''', ''DD/MM/YYYY'')'||',
                                 '|| RSD_MOVIMIENTO.CANTIDAD ||',
                                 '|| RSD_MOVIMIENTO.VALORUNITARIO ||',
                                 '|| RSD_MOVIMIENTO.VALORTOTAL ||'';
                 MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'I',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
             END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_RESPONSABLE,
                                        UN_REEMPLAZOS => MI_MSGERROR);
           END;
          END LOOP;
          -- FIN LOOP RSD_MOVIMIENTO
      END IF;
      IF MI_CONCEPTO = 'DS' AND MI_CLASE = 'S' THEN
          MI_DEPENDENCIA := '40';
          MI_RESPONSABLE := PCK_ALMACEN_COM2.FC_RETRESPONSABLE(MI_DEPENDENCIA, UN_COMPANIA);
          FOR RSD_MOVIMIENTO IN RSD_MOVIMIENTOS LOOP
              BEGIN
                BEGIN
                   MI_TABLA     := 'ELEMENTOBODEGA';
                   MI_CAMPOS    := 'COMPANIA,
                                    DEPENDENCIA,
                                    RESPONSABLE,
                                    ELEMENTO,
                                    CODIGO,
                                    SERIE,
                                    FECHAENTRADA,
                                    CANTIDAD,
                                    VALORUNITARIO,
                                    VALORTOTAL,
                                    TIPOMOVIMIENTO,
                                    MOVIMIENTO';
                     MI_VALORES   := ''''||RSD_MOVIMIENTO.COMPANIA||''',
                                     '''||MI_DEPENDENCIA||''',
                                     '''|| MI_RESPONSABLE||''',
                                     '|| PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'RESPONSABILIDADES',
                                                                          UN_CRITERIO => '    COMPANIA    = '''|| MI_COMPANIA ||'''
                                                                                          AND RESPONSABLE =  '''|| MI_TERCERO ||'''',
                                                                          UN_CAMPO    => 'CODIGO',
                                                                          UN_INICIAL  => '1')||',
                                     ''' || RSD_MOVIMIENTO.ELEMENTO      ||''',
                                     '   || RSD_MOVIMIENTO.SERIE         ||',
                                     '   || 'TO_DATE('''||RSD_MOVIMIENTO.FECHA ||''', ''DD/MM/YYYY'')'||',
                                     '   || RSD_MOVIMIENTO.CANTIDAD      ||',
                                     '   || RSD_MOVIMIENTO.VALORUNITARIO ||',
                                     '   || RSD_MOVIMIENTO.VALORTOTAL    ||',
                                     ''' || RSD_MOVIMIENTO.TIPOMOVIMIENTO||''',
                                     '   || RSD_MOVIMIENTO.MOVIMIENTO    ||'';
                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
                      MI_MSGERROR(1).CLAVE := 'ELEMENTO';
                      MI_MSGERROR(1).VALOR := RSD_MOVIMIENTO.ELEMENTO;
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                 END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_BODEGA_ELEM,
                                            UN_REEMPLAZOS => MI_MSGERROR);
               END;
                 MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                      UN_DIF         => RSD_MOVIMIENTO.CANTIDAD,
                                                      UN_NOMBRECAMPO => 'CANTINSERVIBLE',
                                                      UN_STRCOMPANIA => MI_COMPANIA);
          END LOOP;
          --END LOOP RSD_MOVIMIENTO
      END IF;
      IF  MI_CLASE_DOC_ASOCIADO = 'O' THEN
          RETURN -1;
      ELSIF MI_CLASE_DOC_ASOCIADO = 'C' OR MI_CLASE_DOC_ASOCIADO = 'ODC' OR MI_CLASE_DOC_ASOCIADO = 'S' OR MI_CLASE_DOC_ASOCIADO = 'CDC' OR MI_CLASE_DOC_ASOCIADO = 'CDS' OR MI_CLASE_DOC_ASOCIADO = 'CS' OR MI_CLASE_DOC_ASOCIADO = 'CV' OR MI_CLASE_DOC_ASOCIADO = 'OC' OR MI_CLASE_DOC_ASOCIADO = 'CM' OR MI_CLASE_DOC_ASOCIADO = 'PD' OR MI_CLASE_DOC_ASOCIADO = 'V' OR MI_CLASE_DOC_ASOCIADO = 'CDT' OR MI_CLASE_DOC_ASOCIADO = 'J' OR MI_CLASE_DOC_ASOCIADO = 'I' THEN
            FOR RSD_MOVIMIENTO IN RSD_MOVIMIENTOS LOOP
                IF MI_CLASE = 'S' THEN
                   MI_SIGNO := '+';
                ELSE
                   MI_SIGNO := '-';
                END IF;

                BEGIN
               SELECT DISTINCT T.TIPOELEMENTO, 
                               ELEMENTO, 
                               SUM(CANTIDAD) AS CANT
                      INTO MI_TIPOELEMENTO,
                           MI_ELEMENTO,
                           MI_CANTIDAD 
                      FROM D_MOVIMIENTO D
                      INNER JOIN TIPOMOVIMIENTO T
                       ON D.COMPANIA = T.COMPANIA 
                      AND D.TIPOMOVIMIENTO = T.CODIGO
              WHERE D.COMPANIA = MI_COMPANIA
                AND D.TIPOMOVASOCIADO = RSD_MOVIMIENTO.TIPOMOVASOCIADO
                AND D.MOVASOCIADO = RSD_MOVIMIENTO.MOVASOCIADO
                AND D.ELEMENTO = RSD_MOVIMIENTO.ELEMENTO
                AND D.VALORUNITARIO = RSD_MOVIMIENTO.VALORUNITARIO -- JM  7747628 13/06/2024
            GROUP BY T.TIPOELEMENTO, ELEMENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                   MI_TIPOELEMENTO := '';
                   MI_ELEMENTO := 0;
                   MI_CANTIDAD := 0;
                END;

                BEGIN
                  BEGIN
                     MI_TABLA     := 'D_ORDENDECOMPRA';
                     MI_CAMPOS    := 'SALDOCANT = CANTIDAD '|| MI_SIGNO ||' '|| MI_CANTIDAD;
                     MI_CONDICION := '     COMPANIA                 =  '''|| MI_COMPANIA                              ||''''||
                                     ' AND CLASEORDEN               =  '''|| MI_CLASEORDEN                            ||''''||
                                     ' AND ORDENDECOMPRA            =    '|| NVL(MI_MOVASOCIADO, 0)                   ||
                                     ' AND ELEMENTO                 =  '''|| RSD_MOVIMIENTO.ELEMENTO                  ||''''||
                                     ' AND VALORUNITARIODI          =    '|| NVL(RSD_MOVIMIENTO.VALORUNITARIO, 0)     ||  -- JM INI 7747628 13/06/2024
     /* bcardenas 26/12/2023         ' AND CODIGO                   =  '''|| RSD_MOVIMIENTO.CODIGO_AFECT              ||''''||*/
                                     ' AND NVL(ORDENDESUMINISTRO,0) =    '|| NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'';
                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION);
                     MI_MSGERROR(1).CLAVE := 'SALDOANT';
                     MI_MSGERROR(1).VALOR := RSD_MOVIMIENTO.CANTIDAD;
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUA_SALDO_ANT,
                                           UN_REEMPLAZOS => MI_MSGERROR);
              END;
                FOR RSDOCASOCIADO IN(
                                      SELECT
                                             DEPENDENCIA,
                                             ORDENDESUMINISTRO
                                        FROM
                                             D_ORDENDECOMPRA
                                       WHERE
                                             COMPANIA = MI_COMPANIA
                                         AND CLASEORDEN = MI_CLASEORDEN
                                         AND ORDENDECOMPRA = NVL(MI_MOVASOCIADO, 0)
                                         AND D_ORDENDECOMPRA.ELEMENTO = RSD_MOVIMIENTO.ELEMENTO
                                         AND D_ORDENDECOMPRA.ORDENDESUMINISTRO = NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0))
                LOOP
                BEGIN
                  IF RSDOCASOCIADO.DEPENDENCIA = MI_PARAMETRO THEN
                  BEGIN
                    BEGIN
                      MI_TABLA     := 'D_ORDENDESUMINISTRO';
                      MI_CAMPOS    := 'CANTIDADPORENTREGAR = CANTIDADPORENTREGAR - '|| RSD_MOVIMIENTO.CANTIDAD;
                      MI_CONDICION := '    COMPANIA          = '''|| MI_COMPANIA                             ||'''
                                       AND ORDENDESUMINISTRO =   '|| NVL(RSDOCASOCIADO.ORDENDESUMINISTRO, 0) ||'
                                       AND ELEMENTO          = '''|| RSD_MOVIMIENTO.ELEMENTO                 ||''''; --JM 09/08/2024
                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                   UN_ACCION     => 'M',
                                                   UN_CAMPOS     => MI_CAMPOS,
                                                   UN_CONDICION  => MI_CONDICION);
                     MI_MSGERROR(1).CLAVE := 'CANTIDADPORENTREGAR';
                     MI_MSGERROR(1).VALOR := RSD_MOVIMIENTO.CANTIDAD;
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_D_ORDENSUMI,
                                               UN_REEMPLAZOS => MI_MSGERROR);
                 END;
                        SELECT
                               SUM(CANTIDADPORENTREGAR) INTO MI_RSSUMAORDEN
                          FROM
                               D_ORDENDESUMINISTRO
                         WHERE
                               COMPANIA = MI_COMPANIA
                           AND ORDENDESUMINISTRO = NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0);

                        IF MI_RSSUMAORDEN = 0 THEN
                           MI_SIGNO := '-1';
                        ELSE
                           MI_SIGNO := '0';
                        END IF;
                      BEGIN
                        BEGIN
                          MI_TABLA     := 'ORDENDESUMINISTRO';
                          MI_CAMPOS    := 'VACIA = '||MI_SIGNO;
                          MI_CONDICION := '   COMPANIA  = '''|| MI_COMPANIA ||'''
                                          AND NUMERO    =   '|| NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0)||'';
                          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                       UN_ACCION     => 'M',
                                                       UN_CAMPOS     => MI_CAMPOS,
                                                       UN_CONDICION  => MI_CONDICION);
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_VACIA);
                   END;
                  ELSE
                        IF MI_CLASE = 'E' THEN
                            MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                                 UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD * -1),
                                                                 UN_NOMBRECAMPO => 'CANTRESERVADA',
                                                                 UN_STRCOMPANIA => MI_COMPANIA);
                        ELSE
                            MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                                 UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD),
                                                                 UN_NOMBRECAMPO => 'CANTRESERVADA',
                                                                 UN_STRCOMPANIA => MI_COMPANIA);
                        END IF;
                  END IF;
                -- FIN RSDOCASOCIADO.DEPENDENCIA = MI_PARAMETRO
               IF  MI_CLASE = 'E' THEN
                  MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                       UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD * -1),
                                                       UN_NOMBRECAMPO => 'CANTCOMPORLLEGAR',
                                                       UN_STRCOMPANIA => MI_COMPANIA);
               ELSE
                  MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                       UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD),
                                                       UN_NOMBRECAMPO => 'CANTCOMPORLLEGAR',
                                                       UN_STRCOMPANIA => MI_COMPANIA);
                END IF;
                --END IF NUEVO ---
                END;
                -- FIN RSDOCASOCIADO
                END LOOP;
                SELECT
                       SUM(SALDOCANT) INTO  MI_RSDOCASOCIADO
                  FROM
                       D_ORDENDECOMPRA
                 WHERE
                       COMPANIA =  MI_COMPANIA
                   AND CLASEORDEN = MI_CLASEORDEN
                   AND ORDENDECOMPRA = NVL(MI_MOVASOCIADO, 0);
                IF  MI_RSDOCASOCIADO = 0 THEN
                    MI_SIGNO := 'S';
                ELSE
                    MI_SIGNO := 'N';
                END IF;
               BEGIN
                  BEGIN
                      MI_TABLA     := 'ORDENDECOMPRA';
                      MI_CAMPOS    := 'VACIA = '''||MI_SIGNO||'''';
                      MI_CONDICION := '    COMPANIA   = ''' || MI_COMPANIA||'''
                                       AND CLASEORDEN = ''' || MI_CLASEORDEN||'''
                                       AND NUMERO     = '  || NVL(MI_MOVASOCIADO, 0)||'';
                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                   UN_ACCION     => 'M',
                                                   UN_CAMPOS     => MI_CAMPOS,
                                                   UN_CONDICION  => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                   END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_ORD_COMPVAC);
               END;
                  END LOOP;
                  --END LOOP RSD_MOVIMIENTO
                ELSIF MI_CLASE_DOC_ASOCIADO = 'R' THEN
                    --IF  MI_CLASE = 'S'  THEN
                    FOR RSD_MOVIMIENTO IN RSD_MOVIMIENTOS LOOP
                        IF  MI_CLASE = 'S'  THEN
                            BEGIN
                              BEGIN
                                  MI_TABLA     := 'D_ORDENDESUMINISTRO';
                                  MI_CAMPOS    := 'CANTIDADPORENTREGAR = CANTIDADPORENTREGAR - ' || RSD_MOVIMIENTO.CANTIDAD;
                                  MI_CONDICION :=  '    COMPANIA          = ''' || MI_COMPANIA                              ||'''
                                                    AND ORDENDESUMINISTRO =   ' || NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'
                                                    AND ELEMENTO          =  ''' || RSD_MOVIMIENTO.ELEMENTO                 ||'''';
                                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                               UN_ACCION     => 'M',
                                                               UN_CAMPOS     => MI_CAMPOS,
                                                               UN_CONDICION  => MI_CONDICION);
                                  MI_MSGERROR(1).CLAVE := 'CANTIDADPORENTREGAR';
                                  MI_MSGERROR(1).VALOR := RSD_MOVIMIENTO.CANTIDAD;
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                 END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_D_ORDENSUMI);
                             END;
                           SELECT
                                  SUM(CANTIDADPORENTREGAR) INTO MI_RSSUMAORDEN
                             FROM
                                  D_ORDENDESUMINISTRO
                            WHERE COMPANIA = MI_COMPANIA
                              AND ORDENDESUMINISTRO = NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0);
                           IF  MI_RSSUMAORDEN = 0 THEN
                               BEGIN
                                  BEGIN
                                      MI_TABLA     := 'ORDENDESUMINISTRO';
                                      MI_CAMPOS    := 'VACIA = -1';
                                      MI_CONDICION :=  '   COMPANIA = '''|| MI_COMPANIA                              ||'''
                                                       AND NUMERO   =   '|| NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'';
                                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                   UN_ACCION     => 'M',
                                                                   UN_CAMPOS     => MI_CAMPOS,
                                                                   UN_CONDICION  => MI_CONDICION);
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                   END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_VACIA);
                             END;
                             BEGIN
                                BEGIN
                                    MI_TABLA     := 'ORDENDESUMINISTRO';
                                    MI_CAMPOS    := 'VACIA = -1';
                                    MI_CONDICION :=  '   COMPANIA = '''|| MI_COMPANIA                              ||'''
                                                     AND NUMERO   =   '|| NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'';
                                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                 UN_ACCION     => 'M',
                                                                 UN_CAMPOS     => MI_CAMPOS,
                                                                 UN_CONDICION  => MI_CONDICION);
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                   END;
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_VACIA);
                               END;
                               MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                                    UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD * -1),
                                                                    UN_NOMBRECAMPO => 'CANTRESERVADA',
                                                                    UN_STRCOMPANIA => MI_COMPANIA);
                           END IF;
                      ELSE
                        BEGIN
                            BEGIN
                               MI_TABLA     := 'D_ORDENDESUMINISTRO';
                               MI_CAMPOS    := 'CANTIDADPORENTREGAR = CANTIDADPORENTREGAR - '|| RSD_MOVIMIENTO.CANTIDAD;
                               MI_CONDICION := '     COMPANIA           = ''' || MI_COMPANIA                              ||'''
                                                 AND ORDENDESUMINISTRO  =   ' || NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'
                                                 AND ELEMENTO           = ''' || RSD_MOVIMIENTO.ELEMENTO                  ||'''';
                               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                            UN_ACCION     => 'M',
                                                            UN_CAMPOS     => MI_CAMPOS,
                                                            UN_CONDICION  => MI_CONDICION);
                               MI_MSGERROR(1).CLAVE := 'CANTIDADPORENTREGAR';
                               MI_MSGERROR(1).VALOR := RSD_MOVIMIENTO.CANTIDAD;
                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                               RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                             END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_D_ORDENSUMI);
                     END;
                           IF  MI_RSSUMAORDEN > 0 THEN
                               BEGIN
                                  BEGIN
                                     MI_TABLA     := 'ORDENDESUMINISTRO';
                                     MI_CAMPOS    := 'VACIA = 0';
                                     MI_CONDICION := '    COMPANIA           = ''' || MI_COMPANIA                              ||'''
                                                      AND NUMERO             =   ' || NVL(RSD_MOVIMIENTO.ORDENDESUMINISTRO, 0) ||'';
                                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                                  UN_ACCION     => 'M',
                                                                  UN_CAMPOS     => MI_CAMPOS,
                                                                  UN_CONDICION  => MI_CONDICION);
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                   END;
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_VACIA);
                               END;
                           END IF;
                           MI_RTA := PCK_ALMACEN_COM2.FC_ACTRES(UN_STRELEMENT  => RSD_MOVIMIENTO.ELEMENTO,
                                                                UN_DIF         => (RSD_MOVIMIENTO.CANTIDAD),
                                                                UN_NOMBRECAMPO => 'CANTRESERVADA',
                                                                UN_STRCOMPANIA => MI_COMPANIA);
                        END IF;
                    END LOOP;
          ELSIF MI_CLASE_DOC_ASOCIADO = 'M' AND MI_CLASE = 'S' THEN
                FOR RSD_MOVIMIENTO IN RSD_MOVIMIENTOS LOOP
                   BEGIN
                      BEGIN
                         MI_TABLA     := 'D_MOVIMIENTO';
                         MI_CAMPOS    := 'CANTIDADAFECTADA = CANTIDADAFECTADA + '||RSD_MOVIMIENTO.CANTIDAD  ;
                         MI_CONDICION := '   COMPANIA           = ''' || MI_COMPANIA||'''
                                         AND TIPOMOVIMIENTO     = ''' || NVL(MI_TIPOMOVASOCIADO, 0)||'''
                                         AND MOVIMIENTO         =   ' || NVL(MI_MOVASOCIADO, 0)||'
                                         AND ELEMENTO           =  '''|| RSD_MOVIMIENTO.ELEMENTO||'''
                                         AND CODIGO             =   ' || RSD_MOVIMIENTO.CODIGO||'';
                         MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                       END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_D_MOV_CANT_AFEC);
                   END;
                END LOOP;
          END IF;
          RETURN -1;
  END;
  RETURN -1;
END FC_ACTUALIZA_DOC_ASOCIADO;

PROCEDURE PR_GENERA_INVENTARIO_INICIAL
 /*
    NAME              : PR_GENERA_INVENTARIO_INICIAL En Access --> GeneraInventarioInicial
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 25/01/2017
    TIME              : 16:26 PM
    MODIFIER          : AURA LILIANA MONROY GARCIA
    DATE MODIFIED     : 17/05/2018
    TIME              : 8:43 AM
    DESCRIPTION       :
    MODIFICATIONS     : Se ajustan los datos que se envían para la creación de los movimientos EDI y SII, teniendo en cuenta 
                        los movimientos definidos en las transacciones válidas. Se adiciona la validación de existencia de información 
                        registrada en devolutivo y depreciación relacionada con los elementos de la Orden de Compra que se etá trabajando
  @NAME:  generarInventarioInicial                     
  */
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO_DOC_ASOCIADO     IN TIPOMOVIMIENTO.CODIGO%TYPE,
  UN_NUMERO_DOC_ASOCIADO   IN MOVIMIENTO.NUMERO%TYPE,
  UN_TIPO_MOV_ENTRADA      IN TIPOMOVIMIENTO.CODIGO%TYPE,
  UN_TIPO_MOV_SALIDA       IN TIPOMOVIMIENTO.CODIGO%TYPE,
  UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO 
)AS
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_CRITERIO              PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CLASE_ORDEN           TIPOMOVIMIENTO.CODIGO%TYPE;
  MI_RS                    SYS_REFCURSOR;
  MI_RS                    SYS_REFCURSOR;
  MI_RSS                   SYS_REFCURSOR;
  MI_TIPO                  INVENTARIO.TIPO%TYPE;
  MI_CODIGO                D_MOVIMIENTO.CODIGO%TYPE;
  MI_FECHA                 DATE;
  MI_FECHAS                DATE;
  MI_FECHA_ADQUISICION     DATE;
  MI_DEPENDENCIA           DEPENDENCIA.CODIGO%TYPE;
  MI_RESPONSABLE           PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL_RESPONSABLE  PCK_SUBTIPOS.TI_SUCURSAL;
  MI_MOV_ANTERIOR          TIPOMOVIMIENTO.CODIGO%TYPE;
  MI_MOV_ANTERIOR_SALIDA   TIPOMOVIMIENTO.CODIGO%TYPE;
  MI_PARAMETRO             PCK_SUBTIPOS.TI_PARAMETRO;
  MI_ASOCIADO              PCK_SUBTIPOS.TI_LOGICO;
  MI_DEPENDENCIA_ALMACEN   DEPENDENCIA.CODIGO%TYPE DEFAULT '000000000000';
  MI_RESPONSABLE_PROVEEDOR PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL_PROVEEDOR    PCK_SUBTIPOS.TI_SUCURSAL;
  MI_RESPONSABLE_BODEGA    PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL_BODEGA       PCK_SUBTIPOS.TI_SUCURSAL;
  MI_DEPENDENCIA_PROVEEDOR DEPENDENCIA.CODIGO%TYPE;
  MI_RTA                   PCK_SUBTIPOS.TI_RTA_ACME; 
  MI_EXISTE_DEVOLUTIVO     PCK_SUBTIPOS.TI_LOGICO;
  MI_TOTALMOVIMIENTOS      PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CLASEBODEGAORIGSALIDA ORDENDECOMPRA.CLASE_BODEGA%TYPE;
  MI_CLASEBODEGADESTSALIDA ORDENDECOMPRA.CLASE_BODEGA%TYPE;
  MI_AUXCONTADOR           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_EXISTEREGISTRO        BOOLEAN DEFAULT FALSE;
  MI_PARAMETRO_NIIF 	     PCK_SUBTIPOS.TI_PARAMETRO;
  MI_BODEGA_ALMACEN        PCK_SUBTIPOS.TI_PARAMETRO;
  MI_BODEGA_INSERVIBLES    PCK_SUBTIPOS.TI_PARAMETRO;
  BEGIN

  	MI_PARAMETRO_NIIF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                          ,UN_NOMBRE     => 'MANEJA NIIF EN ALMACEN'
                                                          ,UN_MODULO     => PCK_DATOS.MODULOALMACEN
                                                          ,UN_FECHA_PAR  => SYSDATE);

    MI_BODEGA_ALMACEN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                          ,UN_NOMBRE     => 'BODEGA ALMACEN'
                                                          ,UN_MODULO     => PCK_DATOS.MODULOALMACEN
                                                          ,UN_FECHA_PAR  => SYSDATE);

    MI_BODEGA_INSERVIBLES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                          ,UN_NOMBRE     => 'BODEGA INSERVIBLES'
                                                          ,UN_MODULO     => PCK_DATOS.MODULOALMACEN
                                                          ,UN_FECHA_PAR  => SYSDATE);
    BEGIN
      SELECT CODIGO
      INTO   MI_DEPENDENCIA_ALMACEN
      FROM   DEPENDENCIA
      WHERE  COMPANIA      = UN_COMPANIA 
        AND  CLASE_BODEGA  = '20'
        AND  PRINCIPAL NOT IN (0);  -- Se modifica 12/04/2018 dado que según indicaciones por el indicador se obtiene la principal
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_DEPENDENCIA_ALMACEN:='000000000000';
    END;
   BEGIN
    BEGIN
      MI_CLASE_ORDEN := 'ODI';

      MI_CAMPOS      := 'D_ORDENDECOMPRA.FECHASALIDASERVICIO =  FECHAADQUISICION + 1
                        ,D_ORDENDECOMPRA.MODIFIED_BY         = ''' || UN_USUARIO || ''' 
                        ,D_ORDENDECOMPRA.DATE_MODIFIED       = SYSDATE'; 

      MI_CONDICION   := 'D_ORDENDECOMPRA.COMPANIA            = ''' || UN_COMPANIA || '''
                     AND D_ORDENDECOMPRA.CLASEORDEN          = ''' || UN_TIPO_DOC_ASOCIADO   || '''
                     AND D_ORDENDECOMPRA.ORDENDECOMPRA       =   ' || UN_NUMERO_DOC_ASOCIADO || '  
                     AND D_ORDENDECOMPRA.FECHAADQUISICION    IS NOT NULL
                     AND D_ORDENDECOMPRA.FECHASALIDASERVICIO IS NULL';                     

      MI_RTA := PCK_DATOS.FC_ACME  (UN_TABLA     => 'D_ORDENDECOMPRA'
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   =>SQLCODE
                       ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_UPDATE_DORDCOMPRA);
   END;

   SELECT COUNT(*) TOTAL 
     INTO MI_TOTALMOVIMIENTOS
     FROM MOVIMIENTO
    WHERE COMPANIA = UN_COMPANIA
      AND FECHA > TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA 
                                               ,UN_NOMBRE    => 'FECHA DE CORTE ALMACEN'
                                               ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN
                                               ,UN_FECHA_PAR => SYSDATE), 'DD/MM/YYYY')
     AND TIPOMOVIMIENTO NOT IN ('EDI','SII');

  -- Valida si ya fueron crados Movimientos de Almacen
  /* IF MI_TOTALMOVIMIENTOS > 0 THEN
      BEGIN 
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_CONMOVIMIENTOS);-- No es posible realizar el proceso porque ya existen Movimientos de Almacén Creados
      END;
   END IF;*/


   <<EXTRAER_TIPOMOVIMIENTO>> --RSTIPOMOVIMIENTO
    FOR MI_RS IN(
        SELECT *
        FROM TIPOMOVIMIENTO
        WHERE COMPANIA = UN_COMPANIA 
          AND CODIGO   = UN_TIPO_MOV_ENTRADA
    )
    LOOP
        IF  MI_CLASE_ORDEN = 'ODI' THEN
          <<EXTRAER_ORDENDCOMPRA>>  --RSDOCASOCIADO
          FOR MI_RSS IN(
              SELECT *
              FROM ORDENDECOMPRA
              WHERE COMPANIA         = UN_COMPANIA
                AND CLASEORDEN       = MI_CLASE_ORDEN
                AND NUMERO           = UN_NUMERO_DOC_ASOCIADO
                AND NVL(REALIZADA,0) = 0)
          LOOP
            <<REVISAR_MOVIMIENTOS>>
            FOR MI_RSSS IN(
              SELECT COUNT(*) C
              FROM  MOVIMIENTO
              WHERE COMPANIA       =  UN_COMPANIA
                AND TIPOMOVIMIENTO IN (UN_TIPO_MOV_ENTRADA,UN_TIPO_MOV_SALIDA)
                AND NUMERO         =  UN_NUMERO_DOC_ASOCIADO)
            LOOP
              IF MI_RSSS.C NOT IN (0) THEN              
                BEGIN
                  BEGIN
                    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                                 AND TIPOMOVIMIENTO IN ('''||UN_TIPO_MOV_ENTRADA||''','''||UN_TIPO_MOV_SALIDA||''')
                                 AND MOVIMIENTO      = '||UN_NUMERO_DOC_ASOCIADO||'';
                               --AND TIPOMOVIMIENTO IN ('''||UN_TIPO_MOV_ENTRADA||''','''||UN_TIPO_MOV_SALIDA||''',''ECI'')

                    MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'D_MOVIMIENTO'
                                                 ,UN_ACCION    => 'E'
                                                 ,UN_CONDICION => MI_CONDICION);
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_DELETE_MOVIMIENTO);
                END;


                BEGIN
                  BEGIN
                    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                                 AND TIPOMOVIMIENTO IN ('''||UN_TIPO_MOV_ENTRADA||''','''||UN_TIPO_MOV_SALIDA||''')
                                 AND NUMERO         = '||UN_NUMERO_DOC_ASOCIADO||'';                               
                               -- AND TIPOMOVIMIENTO IN ('''||UN_TIPO_MOV_ENTRADA||''','''||UN_TIPO_MOV_SALIDA||''',''ECI'')

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'MOVIMIENTO'
                                                ,UN_ACCION    => 'E'
                                                ,UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_DELETE_MOVIMIENTO);
                END;
              END IF;
            END LOOP REVISAR_MOVIMIENTOS;


            BEGIN
              BEGIN



                MI_FECHA := PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA(UN_FECHA => MI_RSS.FECHA
                                                              ,UN_DIAS => 1);
                MI_FECHA_ADQUISICION := PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA(UN_FECHA => MI_RSS.FECHAADQUISICION
                                                                         ,UN_DIAS => 1);
                MI_FECHAS := PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA(UN_FECHA => MI_RSS.FECHA
                                                             ,UN_DIAS => 2);
                MI_DEPENDENCIA := MI_RSS.DEPENDENCIA;

                -- OBTIENE LAS DEPENDENCIAS ORIGEN Y DESTINO, LOS RESPONSABLES ORIGEN Y DESTINO PARA LA CREACION DEL EDI
                -- 07/06/2018  @amonroy, Se adiciona manejo de excepciones, error ERR_ALM_DEPENDENCIAS_OD_EDI
                BEGIN 
                  BEGIN                 
                      WITH DEPENDENCIA_ORIGEN AS (
                        SELECT DO.DEPENDENCIA
                              ,DO.RESPONSABLE
                              ,DO.SUCURSAL
                        FROM DEPENDENCIA D
                        INNER JOIN DEPENDENCIA_RESPONSABLE DO
                           ON D.COMPANIA  = DO.COMPANIA
                          AND D.CODIGO    = DO.DEPENDENCIA
                        WHERE D.COMPANIA      = UN_COMPANIA 
                          AND D.CLASE_BODEGA  = MI_RS.CLASE_BODEGA_ORIGEN --10
                          AND D.PRINCIPAL NOT IN (0)
                          AND DO.RESPONSABLEALMACEN NOT IN (0) 
                          AND ROWNUM = 1
                          ),
                      DEPENDENCIA_DESTINO AS (
                        SELECT DD.RESPONSABLE
                              ,DD.SUCURSAL
                        FROM DEPENDENCIA D
                        INNER JOIN DEPENDENCIA_RESPONSABLE DD
                           ON D.COMPANIA  = DD.COMPANIA
                          AND D.CODIGO    = DD.DEPENDENCIA
                        WHERE D.COMPANIA      = UN_COMPANIA 
                          AND D.CLASE_BODEGA  = MI_RS.CLASE_BODEGA_DESTINO  --20
                          AND D.PRINCIPAL NOT IN (0)
                          AND DD.RESPONSABLEALMACEN NOT IN (0) 
                          AND ROWNUM = 1
                          )
                      SELECT DO.DEPENDENCIA
                            ,DO.RESPONSABLE RESPONSABLE_ORIGEN
                            ,DO.SUCURSAL SUCURSAL_ORIGEN
                            ,DD.RESPONSABLE RESPONSABLE_DESTINO
                            ,DD.SUCURSAL SUCURSAL_DESTINO                      
                       INTO MI_DEPENDENCIA_PROVEEDOR
                           ,MI_RESPONSABLE_PROVEEDOR
                           ,MI_SUCURSAL_PROVEEDOR
                           ,MI_RESPONSABLE_BODEGA
                           ,MI_SUCURSAL_BODEGA
                       FROM DEPENDENCIA_ORIGEN DO
                          ,DEPENDENCIA_DESTINO DD;
                    EXCEPTION WHEN  NO_DATA_FOUND THEN 
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_REEMPLAZOS => MI_MSGERROR,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEPENDENCIAS_OD_EDI
                     );       
                END;  


                -- CREACION EDI
                -- 14/06/2018  @amonroy, Se adiciona el envio del campo hora y los campos de auditoria
                MI_CAMPOS := 'COMPANIA
                              ,TIPOMOVIMIENTO
                              ,NUMERO
                              ,TERCERO
                              ,SUCURSAL
                              ,FECHA
                              ,HORA
                              ,DESCRIPCION
                              ,TIPOMOVASOCIADO
                              ,MOVASOCIADO
                              ,FECHAMOVASOCIADO
                              ,VALORDOCASOCIADO
                              ,VALORTOTAL
                              ,VACIA
                              ,BODEGA_ORIGEN
                              ,BODEGA_DESTINO
                              ,CLASE_BODEGA_ORIGEN
                              ,CLASE_BODEGA_DESTINO
                              ,DEPENDENCIA_ORIGEN
                              ,DEPENDENCIA_DESTINO
                              ,RESPONSABLE_ORIGEN
                              ,SUCURSAL_RESORIGEN
                              ,RESPONSABLE_DESTINO
                              ,SUCURSAL_RESDESTINO
                              ,CREATED_BY 
                              ,DATE_CREATED' ;

                 MI_VALORES := '''' || MI_RSS.COMPANIA ||'''
                               ,''' || UN_TIPO_MOV_ENTRADA||'''
                               ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                               ,''' || MI_RSS.RESPONSABLE ||'''
                               ,''' || MI_RSS.SUCURSAL_RESPONSABLE ||'''
                               , TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY HH24:MI:SS') || ''',  ''DD/MM/YYYY HH24:MI:SS'')
                               , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE,''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'')
                               ,''' || NVL(MI_RSS.DESCRIPCION, ' ') ||'''
                               ,''' || UN_TIPO_DOC_ASOCIADO ||'''
                               ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                               ,  ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RSS.FECHA) ||'
                               ,  ' || MI_RSS.VALORTOTAL ||'
                               ,  ' || MI_RSS.VALORTOTAL ||'
                               , ''0''
                               ,''' || MI_RS.CLASE_BODEGA_ORIGEN  || '''
                               ,''' || MI_RS.CLASE_BODEGA_DESTINO || '''
                               ,''' || MI_RS.CLASE_BODEGA_ORIGEN  || '''
                               ,''' || MI_RS.CLASE_BODEGA_DESTINO || '''
                               ,''' || MI_DEPENDENCIA_PROVEEDOR   || ''' 
                               ,''' || MI_DEPENDENCIA_ALMACEN     || '''
                               ,''' || MI_RESPONSABLE_PROVEEDOR   || '''
                               ,''' || MI_SUCURSAL_PROVEEDOR      || '''
                               ,''' || MI_RESPONSABLE_BODEGA      || '''
                               ,''' || MI_SUCURSAL_BODEGA         || '''
                               ,''' || UN_USUARIO                 || ''' 
                               ,SYSDATE ';



                 MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'MOVIMIENTO'
                                           ,UN_ACCION  => 'I'
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_MOVIMIENTO);
            END;

            MI_RESPONSABLE :=  MI_RSS.RESPONSABLE;
            MI_SUCURSAL_RESPONSABLE := MI_RSS.SUCURSAL_RESPONSABLE;

            -- CREACION SII  
            -- 14/06/2018  @amonroy, Se adiciona el envio del campo hora y se suma un dia a la fecha
            -- 16/07/2019  @eamaya, Se adiciona las clases de bodegas de origen y destino porque estaban quemadas  
            IF MI_DEPENDENCIA NOT IN(MI_DEPENDENCIA_ALMACEN)THEN
              BEGIN
                BEGIN


                SELECT CLASE_BODEGA_ORIGEN,
                       CLASE_BODEGA_DESTINO
                INTO  MI_CLASEBODEGAORIGSALIDA ,
                      MI_CLASEBODEGADESTSALIDA 
                FROM TIPOMOVIMIENTO
                WHERE COMPANIA   =UN_COMPANIA
                  AND CODIGO     = UN_TIPO_MOV_SALIDA;

                  MI_CAMPOS := 'COMPANIA
                                ,TIPOMOVIMIENTO
                                ,NUMERO
                                ,TERCERO
                                ,SUCURSAL
                                ,FECHA
                                ,HORA
                                ,DESCRIPCION
                                ,TIPOMOVASOCIADO
                                ,MOVASOCIADO
                                ,FECHAMOVASOCIADO
                                ,VALORDOCASOCIADO
                                ,VALORTOTAL
                                ,VACIA
                                ,BODEGA_ORIGEN
                                ,BODEGA_DESTINO
                                ,CLASE_BODEGA_ORIGEN
                                ,CLASE_BODEGA_DESTINO                                
                                ,DEPENDENCIA_ORIGEN
                                ,DEPENDENCIA_DESTINO
                                ,RESPONSABLE_ORIGEN
                                ,SUCURSAL_RESORIGEN
                                ,RESPONSABLE_DESTINO
                                ,SUCURSAL_RESDESTINO
                                ,CREATED_BY 
                                ,DATE_CREATED' ;                                 

                  MI_VALORES := '''' || MI_RSS.COMPANIA    ||'''
                                ,''' || UN_TIPO_MOV_SALIDA ||'''
                                ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                                ,''' || MI_RSS.RESPONSABLE ||'''
                                ,''' || MI_RSS.SUCURSAL_RESPONSABLE ||'''
                                , TO_DATE(''' || TO_CHAR(MI_FECHA + 1, 'DD/MM/YYYY HH24:MI:SS') || ''',  ''DD/MM/YYYY HH24:MI:SS'')
                                , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE,''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'')
                                ,''' || NVL(MI_RSS.DESCRIPCION, ' ') ||'''
                                ,''' || UN_TIPO_DOC_ASOCIADO ||'''
                                ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                                ,  ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RSS.FECHA)||'
                                ,  ' || MI_RSS.VALORTOTAL ||'
                                ,  ' || MI_RSS.VALORTOTAL ||'
                                , ''0''
                                , '''||MI_CLASEBODEGAORIGSALIDA||'''
                                , '''||MI_CLASEBODEGADESTSALIDA||'''
                                , '''||MI_CLASEBODEGAORIGSALIDA||'''
                                , '''||MI_CLASEBODEGADESTSALIDA||'''
                                ,''' || MI_DEPENDENCIA_ALMACEN ||'''
                                ,''' || MI_DEPENDENCIA         ||'''
                                ,''' || MI_RESPONSABLE_BODEGA  || '''
                                ,''' || MI_SUCURSAL_BODEGA     || '''                                
                                ,''' || MI_RSS.RESPONSABLE     ||'''
                                ,''' || MI_RSS.SUCURSAL_RESPONSABLE ||'''
                               ,''' || UN_USUARIO                 || ''' 
                               ,SYSDATE ';

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'MOVIMIENTO'
                                              ,UN_ACCION  => 'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD =>SQLCODE
                                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_INSERT_MOVIMIENTO);
              END;
            END IF;


            IF MI_RTA = 1 THEN
              <<EXTRAER_NUMERO_DIGITOS>>
               FOR MI_LS IN(
                    SELECT D_ORDENDECOMPRA.*
                          ,INVENTARIO.TIPO
                          ,INVENTARIO.IDENTIFICADOR
                          ,CASE WHEN D_ORDENDECOMPRA.DEPENDENCIA = MI_BODEGA_ALMACEN THEN '20' 
                               WHEN D_ORDENDECOMPRA.DEPENDENCIA = MI_BODEGA_INSERVIBLES THEN '40'
                               ELSE '30' END as BODEGA_DEVOLUTIVO
                    FROM D_ORDENDECOMPRA
                    LEFT JOIN INVENTARIO
                          ON D_ORDENDECOMPRA.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                          AND D_ORDENDECOMPRA.COMPANIA = INVENTARIO.COMPANIA
                    WHERE D_ORDENDECOMPRA.COMPANIA =  MI_RS.COMPANIA
                      AND CLASEORDEN               =  MI_CLASE_ORDEN
                      AND ORDENDECOMPRA            = UN_NUMERO_DOC_ASOCIADO
                      AND D_ORDENDECOMPRA.SERIE IS NOT NULL)
               LOOP
               --@20180608:jreina  Se agrego el tipo M
                 IF MI_LS.TIPO = MI_RS.TIPOELEMENTO OR MI_LS.TIPO = 'N' OR MI_LS.TIPO = 'E' OR MI_LS.TIPO = 'D' OR MI_LS.TIPO = 'M' THEN
                    MI_EXISTEREGISTRO := TRUE;
                    BEGIN
                      SELECT TIPO
                        INTO MI_TIPO
                        FROM INVENTARIO
                       WHERE COMPANIA       = MI_LS.COMPANIA
                         AND CODIGOELEMENTO = MI_LS.ELEMENTO;
                        -- SE ELIMINO VALIDACION DEL TIPO = 'E'

                      MI_MOV_ANTERIOR :=  UN_TIPO_MOV_ENTRADA ;
                      MI_CRITERIO     := 'COMPANIA          = '''|| MI_LS.COMPANIA ||'''
                                         AND TIPOMOVIMIENTO = '''|| MI_MOV_ANTERIOR ||'''
                                         AND MOVIMIENTO     =   '|| UN_NUMERO_DOC_ASOCIADO ||'';
                      MI_CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA     => 'D_MOVIMIENTO'
                                                                    ,UN_CRITERIO => MI_CRITERIO

                                                                    ,UN_CAMPO    => 'CODIGO');
                      BEGIN
                       BEGIN
                       -- CREACION DETALLES EDI
                        MI_CAMPOS := 'COMPANIA
                                      ,TIPOMOVIMIENTO
                                      ,MOVIMIENTO
                                      ,CODIGO
                                      ,ELEMENTO
                                      ,ESPECIFICACION
                                      ,CENTRODECOSTO
                                      ,CANTIDAD
                                      ,SALDOCANT
                                      ,VALORUNITARIO
                                      ,VALORTOTAL
                                      ,IND_REG
                                      ,FECHA
                                      ,HORA
                                      ,ORDENDESUMINISTRO
                                      ,MARCA
                                      ,AJUSTECENTAVOS
                                      ,CANTIDADDOCAS
                                      ,VALORDOCAS
                                      ,SERIE
                                      ,SERIEDEVOLUTIVO
                                      ,ESTADO
                                      ,CREATED_BY 
                                      ,DATE_CREATED';
                                      
						IF MI_PARAMETRO_NIIF = 'SI' THEN
							MI_CAMPOS := MI_CAMPOS || 
									 ',NIIF_VALOR_TOTAL
                                      ,NIIF_VALOR_BASE
                                      ,NIIF_TIPO_ACTIVO
                                      ,APLICANIIF
                                      ,NIIF_VIDA_UTIL';
						END IF;


                        MI_VALORES := ''''||MI_LS.COMPANIA||'''
                                      ,'''||UN_TIPO_MOV_ENTRADA||'''
                                      ,'||UN_NUMERO_DOC_ASOCIADO||'
                                      ,'||MI_CODIGO||'
                                      ,'''||MI_LS.ELEMENTO||'''
                                      ,'''||NVL(MI_LS.ESPECIFICACION, ' ')||'''
                                      ,'''||NVL(MI_LS.CENTRODECOSTO, ' ')||'''
                                      ,''1''
                                      ,''1''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,''-1''
                                      , TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY HH24:MI:SS') || ''',  ''DD/MM/YYYY HH24:MI:SS'')
                                      , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE,''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'')                                                                         
                                      ,'''||NVL(MI_LS.ORDENDESUMINISTRO, 0)||'''
                                      ,'''||NVL(MI_LS.MARCA, '  ')||'''
                                      ,''0''
                                      ,'''||MI_LS.CANTIDAD||'''
                                      ,'''||MI_LS.VLRTOTAL||'''
                                      ,'''||NVL(MI_LS.SERIE,0)||'''
                                      ,'''||NVL(MI_LS.SERIEDEVOLUTIVO, ' ')||'''
                                      ,'''||PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => (NVL(MI_LS.ESTADO, ' ') = ' ')
                                                                 ,UN_SI => 'B'
                                                                 ,UN_NO=> MI_LS.ESTADO)||'''
                                      ,''' || UN_USUARIO || ''' 
                                      ,SYSDATE';

						IF MI_PARAMETRO_NIIF = 'SI' THEN
							MI_VALORES := MI_VALORES||
									  ','''||MI_LS.VLRTOTAL||'''
                                       ,'''||MI_LS.VLRTOTAL||'''
                                       ,'''||MI_LS.NIIF_TIPO_ACTIVO||'''
                                       ,'''||MI_LS.APLICA_NIIF||'''
                                       ,'''||MI_LS.NIIF_VIDA_UTIL||'''';
						END IF;                                       
                                      
                         MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'D_MOVIMIENTO'
                                                      ,UN_ACCION   => 'I'
                                                      ,UN_CAMPOS   => MI_CAMPOS
                                                      ,UN_VALORES  => MI_VALORES);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                       END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO);
                     END;
                     MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                          ,UN_NOMBRE     => 'BODEGA ALMACEN'
                                                          ,UN_MODULO     => PCK_DATOS.MODULOALMACEN
                                                          ,UN_FECHA_PAR  => SYSDATE);
                     MI_PARAMETRO := NVL(MI_PARAMETRO,' ');
                        IF MI_PARAMETRO NOT IN (MI_DEPENDENCIA) THEN
                         BEGIN
                          BEGIN
                           -- CREACION DETALLES SII
                            MI_CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'D_MOVIMIENTO'
                                                                         ,UN_CRITERIO => 'COMPANIA      = '''|| MI_LS.COMPANIA||'''
                                                                                          AND TIPOMOVIMIENTO =  '''||UN_TIPO_MOV_SALIDA||'''
                                                                                          AND MOVIMIENTO     = '||UN_NUMERO_DOC_ASOCIADO||''
                                                                         ,UN_CAMPO    => 'CODIGO');
                            MI_CAMPOS := 'COMPANIA
                                          ,TIPOMOVIMIENTO
                                          ,MOVIMIENTO
                                          ,CODIGO
                                          ,ELEMENTO
                                         ,ESPECIFICACION
                                         ,CENTRODECOSTO
                                         ,CANTIDAD
                                         ,SALDOCANT
                                         ,VALORUNITARIO
                                         ,VALORTOTAL
                                         ,IND_REG
                                         ,FECHA
                                         ,HORA
                                         ,ORDENDESUMINISTRO
                                         ,MARCA
                                         ,AJUSTECENTAVOS
                                         ,CANTIDADDOCAS
                                         ,VALORDOCAS
                                         ,SERIE
                                         ,SERIEDEVOLUTIVO
                                         ,ESTADO
                                         ,CREATED_BY 
                                         ,DATE_CREATED' ;

                            IF MI_PARAMETRO_NIIF = 'SI' THEN
								MI_CAMPOS := MI_CAMPOS || 
										',NIIF_VALOR_TOTAL
	                                     ,NIIF_VALOR_BASE
	                                     ,NIIF_TIPO_ACTIVO
	                                     ,APLICANIIF
	                                     ,NIIF_VIDA_UTIL';
							END IF;     

                            MI_VALORES:= ''''||MI_LS.COMPANIA||'''
                                         ,'''||UN_TIPO_MOV_SALIDA||'''
                                         ,'||UN_NUMERO_DOC_ASOCIADO||'
                                         ,'||MI_CODIGO||'
                                         ,'''||MI_LS.ELEMENTO||'''
                                         ,'''||NVL(MI_LS.ESPECIFICACION, '')||'''
                                         ,'''||NVL(MI_LS.CENTRODECOSTO, '')||'''
                                         ,''1''
                                         ,''1''
                                         ,'''||MI_LS.VALORUNITARIO||'''
                                         ,'''||MI_LS.VALORUNITARIO||'''
                                         ,''-1''
                                         , TO_DATE(''' || TO_CHAR(MI_FECHA + 1, 'DD/MM/YYYY HH24:MI:SS') || ''',  ''DD/MM/YYYY HH24:MI:SS'')
                                         , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE,''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'')                                         
                                         ,'''||NVL(MI_LS.ORDENDESUMINISTRO, 0)||'''
                                         ,'''||NVL(MI_LS.MARCA, '')||'''
                                         ,''0''
                                         ,'''||MI_LS.CANTIDAD||'''
                                         ,'''||MI_LS.VLRTOTAL||'''
                                         ,'''||NVL(MI_LS.SERIE,0)||'''
                                         ,'''||NVL(MI_LS.SERIEDEVOLUTIVO, '')||'''
                                         ,'''||PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => (NVL(MI_LS.ESTADO, '')=' ')
                                                                     ,UN_SI       => 'B'
                                                                     ,UN_NO       => MI_LS.ESTADO)||'''
                                         ,''' || UN_USUARIO || ''' 
                                         ,SYSDATE';

							IF MI_PARAMETRO_NIIF = 'SI' THEN
								MI_VALORES := MI_VALORES||
										','''||MI_LS.VLRTOTAL||'''
	                                     ,'''||MI_LS.VLRTOTAL||'''
	                                     ,'''||MI_LS.NIIF_TIPO_ACTIVO||'''
	                                     ,'''||MI_LS.APLICA_NIIF||'''
	                                     ,'''||MI_LS.NIIF_VIDA_UTIL||'''';
							END IF;                                           


                                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'D_MOVIMIENTO'
                                                            ,UN_ACCION  => 'I'
                                                            ,UN_CAMPOS  => MI_CAMPOS
                                                            ,UN_VALORES => MI_VALORES);
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                          END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO);
                         END;
                        END IF;

                    -- Para validar la existencia de devolutivo
                    SELECT COUNT(*) DEVOLUTIVO
                      INTO MI_AUXCONTADOR
                     FROM DEVOLUTIVO
                    WHERE COMPANIA = UN_COMPANIA 
                      AND ELEMENTO = MI_LS.ELEMENTO 
                      AND SERIE    = NVL(MI_LS.SERIE,0);    

                    IF MI_AUXCONTADOR > 0 THEN 
                        -- Valida la existencia de la depreciación por elemento
                        SELECT COUNT(*) DEPRECIAR
                          INTO MI_AUXCONTADOR
                          FROM DEPRECIAR
                         WHERE COMPANIA = UN_COMPANIA
                           AND ELEMENTO = MI_LS.ELEMENTO 
                           AND SERIE    = NVL(MI_LS.SERIE,0); 

                        IF MI_AUXCONTADOR > 0 THEN 
                          BEGIN
                            BEGIN
                              MI_TABLA     := 'DEPRECIAR';
                              MI_CONDICION := 'COMPANIA   = ''' || UN_COMPANIA || '''
                                           AND ELEMENTO   = ''' || MI_LS.ELEMENTO ||''' 
                                           AND SERIE      =   ' || NVL(MI_LS.SERIE,0);                               

                              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                          ,UN_ACCION    => 'E'
                                                          ,UN_CONDICION => MI_CONDICION);

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                            END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                              MI_MSGERROR(1).CLAVE := 'CODIGO';
                              MI_MSGERROR(1).VALOR :=  MI_LS.ELEMENTO;
                              MI_MSGERROR(2).CLAVE := 'SERIE';
                              MI_MSGERROR(2).VALOR :=  NVL(MI_LS.SERIE,0);
                                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD      => SQLCODE
                                                  ,UN_ERROR_COD   => PCK_ERRORES.ERR_ALMACEN_ELIDEPRECIACION-- No fue posible eliminar las depreciaciones para el elemento --CODIGO-- con serie --SERIE--
                                                  ,UN_TABLAERROR 	=> MI_TABLA
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                          END;

                        END IF;

                        BEGIN
                          BEGIN
                            MI_TABLA     := 'DEVOLUTIVO';
                            MI_CONDICION := 'COMPANIA   = ''' || UN_COMPANIA || '''
                                         AND ELEMENTO   = ''' || MI_LS.ELEMENTO ||''' 
                                         AND SERIE      =   ' || NVL(MI_LS.SERIE,0);                               

                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                        ,UN_ACCION    => 'E'
                                                        ,UN_CONDICION => MI_CONDICION);

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                          END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                            MI_MSGERROR(1).CLAVE := 'CODIGO';
                            MI_MSGERROR(1).VALOR :=  MI_LS.ELEMENTO;
                            MI_MSGERROR(2).CLAVE := 'SERIE';
                            MI_MSGERROR(2).VALOR :=  NVL(MI_LS.SERIE,0);
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD      => SQLCODE
                                                ,UN_ERROR_COD   => PCK_ERRORES.ERRR_ALMACEN_DELETE_MOVIMIENTO-- No fue posible eliminar el devolutivo --CODIGO-- con serie --SERIE--
                                                ,UN_TABLAERROR 	=> MI_TABLA
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                        END;

                    END IF;

                     BEGIN
                      BEGIN
                        MI_CAMPOS := 'COMPANIA
                                      ,ELEMENTO
                                      ,SERIE
                                      ,DEPENDENCIA
                                      ,RESPONSABLE
                                      ,SUCURSAL_RESPONSABLE
                                      ,VALOR
                                      ,ESTADO
                                      ,FECHAADQUISICION
                                      ,FECHAENTRADA
                                      ,ORIGEN
                                      ,NUMEROORIGEN
                                      ,FECHAULTMOV
                                      ,TIPOMOVIMIENTOI
                                      ,MOVIMIENTOI
                                      ,PLACA
                                      ,TIPOELEMENTO
                                      ,DESCRIPCION
                                      ,MARCA
                                      ,SERIEDEVOLUTIVO
                                      ,COSTOAJUSTADO
                                      ,FECHASERVICIO
                                      ,TIPOMOVIMIENTOF
                                      ,MOVIMIENTOF
                                      ,FECHASALIDASERVICIO
                                      ,FECHABODEGA
                                      ,CUANTIAMIN
                                      ,CREATED_BY
                                      ,BODEGA
                                      ,DATE_CREATED';
						
                        IF MI_PARAMETRO_NIIF = 'SI' THEN
							MI_CAMPOS := MI_CAMPOS || 
									 ',APLICA_NIIF
                                      ,NIIF_TIPO_ACTIVO
                                      ,NIIF_VALOR_BASE
                                      ,NIIF_VALORBASE
                                      ,NIIF_VALOR_TOTAL
                                      ,NIIF_FECHAADQUISICION
                                      ,NIIF_VIDA_UTIL
                                      ,NIIF_COSTOAJUSTADO';
						END IF;

                       MI_VALORES := ''''||MI_LS.COMPANIA||'''
                                      ,'''||MI_LS.ELEMENTO||'''
                                      ,'''||NVL(MI_LS.SERIE,0)||'''
                                      ,'''||MI_DEPENDENCIA||'''
                                      ,'''||MI_RESPONSABLE||'''
                                      ,'''||MI_SUCURSAL_RESPONSABLE||'''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,'''||PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => (NVL(MI_LS.ESTADO, ' ')=' ')
                                                                 ,UN_SI       => 'B'
                                                                 ,UN_NO       => MI_LS.ESTADO)||'''
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA   => MI_LS.FECHAADQUISICION)||'
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_LS.FECHABODEGA)||'
                                      ,'''||MI_MOV_ANTERIOR||'''
                                      ,'||UN_NUMERO_DOC_ASOCIADO||'
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHAS)||'
                                      ,'''||MI_MOV_ANTERIOR||'''
                                      ,'||UN_NUMERO_DOC_ASOCIADO||'
                                      ,'''||NVL(MI_LS.IDENTIFICADOR, 'P')||'''
                                      ,'''||MI_LS.TIPO||'''
                                      ,'''||NVL(MI_LS.ESPECIFICACION, ' ')||'''
                                      ,'''||NVL(MI_LS.MARCA,' ')||'''
                                      ,'''||NVL(MI_LS.SERIEDEVOLUTIVO, ' ')||'''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_LS.FECHASALIDASERVICIO)||'
                                      ,'''||UN_TIPO_MOV_SALIDA||'''
                                      ,'||UN_NUMERO_DOC_ASOCIADO||'
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA =>NVL(MI_LS.FECHASALIDASERVICIO,MI_FECHAS))||'
                                      ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA =>NVL(MI_LS.FECHABODEGA,MI_FECHA))||'
                                      ,''0''
                                      ,''' || UN_USUARIO || ''' 
                                      ,'''||MI_LS.BODEGA_DEVOLUTIVO||'''
                                      ,SYSDATE';

                            IF MI_PARAMETRO_NIIF = 'SI' THEN
								MI_VALORES := MI_VALORES||
										  ','''||MI_LS.APLICA_NIIF||'''
	                                       ,'''||MI_LS.NIIF_TIPO_ACTIVO||'''
	                                       ,'''||MI_LS.VALORUNITARIO||'''
	                                       ,'''||MI_LS.VALORUNITARIO||'''
	                                       ,'''||MI_LS.VALORUNITARIO||'''
	                                       ,'''||MI_LS.FECHAADQUISICION||'''
	                                       ,'''||MI_LS.NIIF_VIDA_UTIL||'''
	                                       ,'''||MI_LS.VALORUNITARIO||'''';
							END IF;  

                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'DEVOLUTIVO'
                                                        ,UN_ACCION  => 'I'
                                                        ,UN_CAMPOS  => MI_CAMPOS
                                                        ,UN_VALORES => MI_VALORES);
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_INSERT_DEVOLUTIVO);
                     END;
                    END;            
                 END IF;
              END LOOP EXTRAER_NUMERO_DIGITOS;
              IF NOT MI_EXISTEREGISTRO THEN
              BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                             UN_ERROR_COD=>PCK_ERRORES.ERR_ALM_NO_EXISTE_TIPOELEMENTO);
              END;
              END IF;                                         
                              
              MI_ASOCIADO:=PCK_ALMACEN_COM2.FC_ACTUALIZA_DOC_ASOCIADO(UN_COMPANIA        => UN_COMPANIA
                                                                      ,UN_TIPOMOVIMIENTO  => UN_TIPO_MOV_ENTRADA
                                                                      ,UN_MOVIMIENTO      => UN_NUMERO_DOC_ASOCIADO);
            END IF;
          END LOOP EXTRAER_ORDENDCOMPRA;
      END IF;
    END LOOP EXTRAER_TIPOMOVIMIENTO;
END PR_GENERA_INVENTARIO_INICIAL ;

FUNCTION FC_VIDAUTILRESTANTE(
/*
    NAME              : FC_VIDAUTILRESTANTE  ---> EN ACCESS: VIDAUTILRESTANTE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 29/02/2016
    TIME              : 09:00 AM
    SOURCE MODULE     : SysmanBI2015.11.01_Version_Migracion.accdb
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Retorna la vida útil de una placa.
    MODIFICATIONS     :
*/
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PLACA    IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2 AS
    MI_ERROR_FUN          NUMBER:=GL_ERROR_NUM + 1;
    MI_RTA                VARCHAR2(10);
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
    MI_ELEMENTO           DEVOLUTIVO.ELEMENTO%TYPE;
    MI_SERIE              DEVOLUTIVO.SERIE%TYPE;
    MI_MESESVIDAUTILPLACA DEVOLUTIVO.MESESVIDAUTILPLACA%TYPE;
    MI_TIPOACTIVO         INVENTARIO.TIPOACTIVO%TYPE;
    MI_MESESVIDAUTIL      ACTDEPRECIABLE.MESESVIDAUTIL%TYPE;
    MI_CODIGO             ACTDEPRECIABLE.CODIGO%TYPE;
    MI_NOMBRE             ACTDEPRECIABLE.NOMBRE%TYPE;
    MI_INTVIDAUTIL        NUMBER;
    MI_CONTEO             NUMBER;
    MI_DEPENDENCIA_ALMACEN   VARCHAR2(12 CHAR):='000000000000';
BEGIN

    BEGIN
    SELECT CODIGO
    INTO   MI_DEPENDENCIA_ALMACEN
    FROM   DEPENDENCIA
    WHERE  COMPANIA=UN_COMPANIA 
      AND  CLASE_BODEGA='20'
      AND  ROWNUM=1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
     MI_DEPENDENCIA_ALMACEN:='000000000000';
  END;

    IF UN_PLACA = NULL OR UN_PLACA = '' THEN
        RETURN NULL;
    END IF;
    MI_STRSQL := ' SELECT
                          DEVOLUTIVO.ELEMENTO,
                          DEVOLUTIVO.SERIE,
                          DEVOLUTIVO.MESESVIDAUTILPLACA,
                          INVENTARIO.TIPOACTIVO,
                          ACTDEPRECIABLE.MESESVIDAUTIL,
                          ACTDEPRECIABLE.CODIGO,
                          ACTDEPRECIABLE.NOMBRE
                     FROM
                          DEVOLUTIVO
                    INNER JOIN INVENTARIO
                       ON DEVOLUTIVO.COMPANIA   = INVENTARIO.COMPANIA
                      AND DEVOLUTIVO.ELEMENTO   = INVENTARIO.CODIGOELEMENTO
                    INNER JOIN ACTDEPRECIABLE
                       ON INVENTARIO.TIPOACTIVO = ACTDEPRECIABLE.CODIGO
                    WHERE
                          DEVOLUTIVO.COMPANIA   = '''||UN_COMPANIA||'''
                      AND DEVOLUTIVO.SERIE      = '||UN_PLACA;
    BEGIN                      
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_ELEMENTO,
        MI_SERIE,
        MI_MESESVIDAUTILPLACA,
        MI_TIPOACTIVO,
        MI_MESESVIDAUTIL,
        MI_CODIGO,
        MI_NOMBRE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN ' ';
    END;
    IF MI_CODIGO <> '00' THEN
        IF MI_MESESVIDAUTILPLACA IS NOT NULL THEN
            MI_INTVIDAUTIL := MI_MESESVIDAUTILPLACA;
        ELSE
            MI_INTVIDAUTIL := MI_MESESVIDAUTIL;
        END IF;
    ELSE
        RETURN 0;
    END IF;
-- POR HPV EN AVRIL 10 DE 2018
    MI_STRSQL := 'SELECT
                         COUNT(*)
                  FROM
                         DEPRECIAR
                   WHERE
                         DEPRECIAR.COMPANIA = '''||UN_COMPANIA||'''
                     AND DEPRECIAR.SERIE          = '||UN_PLACA||'
                     AND DEPRECIAR.DEPENDENCIA   NOT IN ( SELECT CODIGO
                                                          FROM   DEPENDENCIA
                                                          WHERE  COMPANIA='''||UN_COMPANIA||'''
                                                           AND  CLASE_BODEGA=''20'')
                     AND DEPRECIAR.VLRDEPRECIACION <> 0';
    BEGIN
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN ' ';
    END;    

    IF MI_CONTEO > 0 THEN
        RETURN (MI_INTVIDAUTIL - MI_CONTEO);
    ELSE
        RETURN MI_INTVIDAUTIL;
    END IF;
END FC_VIDAUTILRESTANTE;

  FUNCTION FC_GENERARCOMPROBANTEALM 
    /*
      NAME              : FC_GENERARCOMPROBANTEALM -- GeneraComprobante_Click en Frm_OrdenInventarioInicial en Access.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 27,28,31/07/2017
      TIME              : 09:34 AM
      SOURCE MODULE     : ALMACÉN -- SysmanAl2017.07.01
      DESCRIPTION       : Función que se encarga de generar el comprobante de almacén.
                          Ruta: Panel Principal\Almacén\Procesos\Compras\Comprobante inventario inicial.
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación.  
                          UN_CLASEORDEN    => Clase orden de compra.
                          UN_NUMERO        => Número de comprobante de inventario.
                          UN_USUARIO       => Código del usuario que genera el comprobante de almacén.
                          UN_DEPENDENCIA   => Código de la dependencia que tiene la orden de trabajo. 
                          UN_RECALCULARDEVOLU => Define si el comprobante tiene registros relacionados ya creados y si se va a crear nuevamente.
      @NAME:  generarComprobanteAlmacen
      @METHOD: GET
    */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASEORDEN             IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NUMERO                 IN ORDENDECOMPRA.NUMERO%TYPE,
    UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO,
    UN_DEPENDENCIA            IN ORDENDECOMPRA.DEPENDENCIA%TYPE,
    UN_RECALCULARDEVOLU       IN VARCHAR2        
  )
  RETURN VARCHAR2 AS 
    MI_LISTASERIE             VARCHAR2(3200 CHAR);
    MI_NUMDETALLE             PCK_SUBTIPOS.TI_ENTERO;
    MI_COMODATO               DEPENDENCIA.COMODATO%TYPE;
    MI_MSGERROREXC            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;  
    MI_RESPUESTA              VARCHAR(30 CHAR);
    MI_MENSAJE                VARCHAR(30 CHAR);
    MI_CLASEBODEGASALIDA      ORDENDECOMPRA.CLASE_BODEGA%TYPE := 0;
	MI_TIPO                   VARCHAR(1 CHAR);										  
  BEGIN
    IF UN_RECALCULARDEVOLU = 'SI'
    THEN
      BEGIN
        BEGIN
          --' ACTUALIZO EL PROCESO
          MI_CAMPOS    := 'REALIZADA     = 0, 
                           MODIFIED_BY   = ''' || UN_USUARIO || ''', ' || 
                         ' DATE_MODIFIED = SYSDATE';
          MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || 
                       ''' AND CLASEORDEN  = ''' || UN_CLASEORDEN ||
                       ''' AND NUMERO      = '   || UN_NUMERO;            
          MI_RTAACME       := PCK_DATOS.FC_ACME (UN_TABLA     => 'ORDENDECOMPRA',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACT_ORDENCOMPRA,
           UN_TABLAERROR => 'ORDENDECOMPRA'
         );                      
      END;

      -- 26/06/2018  @amonroy Se cambia el llamado a la función LISTAGG porque presenta insonsistencias cuando sobrepasa el límite de los 4000 Bytes de un VARCHAR2
      BEGIN
        BEGIN
          SELECT DISTINCT('X') TOTAL
            INTO MI_LISTASERIE
            FROM DEVOLUTIVO  
           WHERE COMPANIA = UN_COMPANIA 
             AND ORIGEN IN ('EDI','ECI','EMI','EAI') 
             AND NUMEROORIGEN = UN_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_LISTASERIE := '0';
        END;    

        IF MI_LISTASERIE NOT LIKE '0' 
        THEN
          MI_CONDICION := '  COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND SERIE IN(SELECT SERIE 
                                             FROM DEVOLUTIVO  
                                            WHERE COMPANIA = ''' || UN_COMPANIA || ''' 
                                              AND ORIGEN IN (''EDI'',''ECI'',''EMI'',''EAI'') 
                                              AND NUMEROORIGEN = ' || UN_NUMERO || ')';
          BEGIN   
            --'primero elimino depreciacion
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'DEPRECIAR',
                                            UN_ACCION    => 'E', 
                                            UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_TABLA   := 'DEPRECIAR';
            MI_MENSAJE := 'la depreciación';
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          BEGIN
          --'elimino movimientoss iba D_DESCARGOBODEGA y D_ENTDEVOLUTIVO
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_MOVIMIENTO',
                                            UN_ACCION    => 'E', 
                                            UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_TABLA   := 'D_MOVIMIENTO';
            MI_MENSAJE := 'los detalles de movimiento';
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
        END IF;
        BEGIN
          --'segundo elimino devolutivo
          MI_CONDICION := '  COMPANIA = ''' || UN_COMPANIA || ''' 
                            AND ORIGEN IN (''EDI'',''ECI'',''EMI'',''EAI'')   
                            AND NUMEROORIGEN = ' || UN_NUMERO;

          MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'DEVOLUTIVO',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          MI_TABLA   := 'DEVOLUTIVO';
          MI_MENSAJE := 'el devolutivo';
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
        BEGIN
          --'elimino movimientos
          MI_CONDICION := '  COMPANIA = ''' || UN_COMPANIA || ''' 
                            AND TIPOMOVASOCIADO = ''' || UN_CLASEORDEN ||   
                        ''' AND MOVASOCIADO     = ' || UN_NUMERO;

          MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'MOVIMIENTO',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          MI_TABLA   := 'MOVIMIENTO';
          MI_MENSAJE := 'los movimientos';
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         MI_MSGERROREXC(1).CLAVE := 'MENSAJE'; 
         MI_MSGERROREXC(1).VALOR := MI_MENSAJE;
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_DEL_COMPALMACEN,
           UN_REEMPLAZOS => MI_MSGERROREXC,
           UN_TABLAERROR => MI_TABLA
         );
      END;
    END IF;
      -----*********************----------------    
    BEGIN  
      BEGIN
        MI_CONDICION := '  COMPANIA = ''' || UN_COMPANIA || ''' 
                       AND TIPOMOVIMIENTO = ''' || UN_CLASEORDEN || '''';

        MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'ERRORALMACEN',
                                        UN_ACCION    => 'E', 
                                        UN_CONDICION => MI_CONDICION);  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        MI_TABLA   := 'ERRORALMACEN';
        MI_MENSAJE := 'los errores de almacén';
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
       MI_MSGERROREXC(1).CLAVE := 'MENSAJE'; 
       MI_MSGERROREXC(1).VALOR := MI_MENSAJE;
       PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_DEL_COMPALMACEN,
         UN_REEMPLAZOS => MI_MSGERROREXC,
         UN_TABLAERROR => MI_TABLA
       );
    END;

    SELECT COUNT(*) NUMERO     
      INTO MI_NUMDETALLE   
      FROM D_ORDENDECOMPRA   
     WHERE COMPANIA      = UN_COMPANIA  
       AND CLASEORDEN    = UN_CLASEORDEN  
       AND ORDENDECOMPRA = UN_NUMERO   
       AND SERIE IS NULL;

    BEGIN
      IF MI_NUMDETALLE > 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
       PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_PLACASGENERADAS
       );                      
    END;
    --FC_REVISARPLACASGENERADAS
    IF FC_REVISARPLACASGENERADAS(UN_COMPANIA   => UN_COMPANIA,
                                 UN_CLASEORDEN => UN_CLASEORDEN,
                                 UN_NUMERO     => UN_NUMERO,
                                 UN_USUARIO    => UN_USUARIO) = 0
    THEN
      RETURN '-1';
    ELSE
      SELECT COMODATO
        INTO MI_COMODATO
        FROM DEPENDENCIA
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO = UN_DEPENDENCIA;

      --16/07/2019 @eamaya Se adiciona validacion para cuando la clase de bodega es diferente a 30 o igual a 90
       BEGIN  
         SELECT CLASE_BODEGA
         INTO MI_CLASEBODEGASALIDA
         FROM ORDENDECOMPRA
         WHERE COMPANIA     = UN_COMPANIA
           AND CLASEORDEN   = UN_CLASEORDEN
           AND NUMERO       = UN_NUMERO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CLASEBODEGASALIDA := '0';
        END;
--15/10/2020 @EAMAYA Se adiciona validacion para que las ordenes de compra de inventario inicial tengan clase de bodega asignada
     BEGIN
      IF UN_CLASEORDEN IN ('ODI') AND MI_CLASEBODEGASALIDA IS NULL THEN
       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
       MI_MSGERROREXC(1).CLAVE := 'NUMERO'; 
       MI_MSGERROREXC(1).VALOR := UN_NUMERO;
       MI_MSGERROREXC(2).CLAVE := 'CLASEORDEN'; 
       MI_MSGERROREXC(2).VALOR := UN_CLASEORDEN;       
       PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD    => SQLCODE,
         UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_NO_CLASEBODEGA,
         UN_REEMPLAZOS => MI_MSGERROREXC
       );                      
    END;

	/*TICKET 7734713(08/02/2024) JCROJAS: Se agrega ciclo con la tabla D_ORDENDECOMPRA para así tener el codigo del elemento y poder extraer el tipo de elemento de la tabla INVENTARIO, 
    con esta información se valida el movimiento de entrada y salida que se debe crear*.*/
    FOR RS IN(SELECT ELEMENTO
              FROM D_ORDENDECOMPRA
              WHERE COMPANIA      = UN_COMPANIA
                AND CLASEORDEN    = UN_CLASEORDEN
                AND ORDENDECOMPRA = UN_NUMERO)
    LOOP
        BEGIN
            SELECT TIPO
                INTO MI_TIPO
            FROM INVENTARIO
            WHERE COMPANIA       = UN_COMPANIA
                AND CODIGOELEMENTO = RS.ELEMENTO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TIPO := '';                
        END;
	
		IF MI_CLASEBODEGASALIDA NOT IN ('90') THEN
			IF MI_TIPO = 'M' THEN 
				PCK_ALMACEN_COM2.PR_GENERA_INVENTARIO_INICIAL(UN_COMPANIA             => UN_COMPANIA,
															  UN_TIPO_DOC_ASOCIADO    => UN_CLASEORDEN,
															  UN_NUMERO_DOC_ASOCIADO  => UN_NUMERO,
															  UN_TIPO_MOV_ENTRADA     => 'EMI',
															  UN_TIPO_MOV_SALIDA      => 'SMI',
															  UN_USUARIO              => UN_USUARIO);
			ELSIF MI_TIPO = 'N' THEN
                PCK_ALMACEN_COM2.PR_GENERA_INVENTARIO_INICIAL(UN_COMPANIA             => UN_COMPANIA,
                                                              UN_TIPO_DOC_ASOCIADO    => UN_CLASEORDEN,
                                                              UN_NUMERO_DOC_ASOCIADO  => UN_NUMERO,
                                                              UN_TIPO_MOV_ENTRADA     => 'EAI',
                                                              UN_TIPO_MOV_SALIDA      => 'SAI',
                                                              UN_USUARIO              => UN_USUARIO);
			ELSE
                PCK_ALMACEN_COM2.PR_GENERA_INVENTARIO_INICIAL(UN_COMPANIA             => UN_COMPANIA,
                                                              UN_TIPO_DOC_ASOCIADO    => UN_CLASEORDEN,
                                                              UN_NUMERO_DOC_ASOCIADO  => UN_NUMERO,
                                                              UN_TIPO_MOV_ENTRADA     => 'EDI',
                                                              UN_TIPO_MOV_SALIDA      => 'SII',
                                                              UN_USUARIO              => UN_USUARIO);
            END IF;
        ELSE 		 
			PCK_ALMACEN_COM2.PR_GENERA_INVENTARIO_INICIAL(UN_COMPANIA             => UN_COMPANIA,
                                                          UN_TIPO_DOC_ASOCIADO    => UN_CLASEORDEN,
                                                          UN_NUMERO_DOC_ASOCIADO  => UN_NUMERO,
                                                          UN_TIPO_MOV_ENTRADA     => 'EIC',
                                                          UN_TIPO_MOV_SALIDA      => 'SIC',
                                                          UN_USUARIO              => UN_USUARIO);
        END IF;           
	END LOOP;		 

     IF MI_CLASEBODEGASALIDA IN ('40')THEN
            PCK_ALMACEN_COM2.PR_GENERA_TERCER_MOVIMIENTO( UN_COMPANIA            => UN_COMPANIA,
                                                          UN_TIPO_DOC_ASOCIADO   => UN_CLASEORDEN,
                                                          UN_NUMERO_DOC_ASOCIADO => UN_NUMERO,
                                                          UN_TIPO_MOVIMIENTO     => 'RDI',  
                                                          UN_USUARIO             => UN_USUARIO);

      ELSE IF MI_CLASEBODEGASALIDA IN ('50')THEN
            PCK_ALMACEN_COM2.PR_GENERA_TERCER_MOVIMIENTO( UN_COMPANIA            => UN_COMPANIA,
                                                          UN_TIPO_DOC_ASOCIADO   => UN_CLASEORDEN,
                                                          UN_NUMERO_DOC_ASOCIADO => UN_NUMERO,
                                                          UN_TIPO_MOVIMIENTO     => 'TDR',  
                                                          UN_USUARIO             => UN_USUARIO);
           END IF;                                               
     END IF;

    END IF;

    RETURN '1';
  END FC_GENERARCOMPROBANTEALM;


  FUNCTION FC_REVISARPLACASGENERADAS
  /*
      NAME              : FC_REVISARPLACASGENERADAS -- RevisaPlacasGeneradas en AlmacenCA en Access.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 27,28/07/2017
      TIME              : 04:27 PM
      SOURCE MODULE     : ALMACÉN -- SysmanAl2017.07.01
      DESCRIPTION       : Función que permite verificar si la placa de un elemento ya se encuentra registrada en el inventario.
                          Retorna 0 (false) si la placa ya está registrada.
                                 -1 (true) si la placa no está registrada.
                          Ruta: Panel Principal\Almacén\Procesos\Compras\Comprobante inventario inicial.
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación.  
                          UN_CLASEORDEN    => Clase orden de compra.
                          UN_NUMERO        => Número de comprobante de inventario.
                          UN_USUARIO       => Código del usuario que genera el comprobante de almacén.
      @NAME:  revisarPlacasGeneradas
      @METHOD: GET
    */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASEORDEN             IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NUMERO                 IN ORDENDECOMPRA.NUMERO%TYPE,
    UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_CONTADOR              PCK_SUBTIPOS.TI_ENTERO := 1;
    MI_STRPROBLEMAS          ERRORALMACEN.MENSAJE%TYPE;
    MI_RESPUESTA             PCK_SUBTIPOS.TI_LOGICO; 
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME               PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROREXC           PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    FOR RS_PLACAS IN (SELECT D_ORDENDECOMPRA.COMPANIA,         
                             D_ORDENDECOMPRA.CLASEORDEN,         
                             D_ORDENDECOMPRA.ORDENDECOMPRA,         
                             D_ORDENDECOMPRA.ELEMENTO,        
                             D_ORDENDECOMPRA.SERIE,          
                             D_ORDENDECOMPRA.SERIEDEVOLUTIVO,          
                             D_ORDENDECOMPRA.SALDOCANT,          
                             DEVOLUTIVO.PLACA,          
                             INVENTARIO.NOMBRELARGO    
                        FROM D_ORDENDECOMPRA 
                             INNER JOIN DEVOLUTIVO 
                                     ON D_ORDENDECOMPRA.COMPANIA = DEVOLUTIVO.COMPANIA 
                                    AND D_ORDENDECOMPRA.SERIE    = DEVOLUTIVO.SERIE  
                             INNER JOIN INVENTARIO 
                                     ON D_ORDENDECOMPRA.COMPANIA = INVENTARIO.COMPANIA   
                                    AND D_ORDENDECOMPRA.ELEMENTO = INVENTARIO.CODIGOELEMENTO       
                       WHERE D_ORDENDECOMPRA.COMPANIA   = UN_COMPANIA         
                         AND D_ORDENDECOMPRA.CLASEORDEN = UN_CLASEORDEN          
                         AND ORDENDECOMPRA              = UN_NUMERO
                         AND D_ORDENDECOMPRA.SALDOCANT NOT IN (0)          
                         AND D_ORDENDECOMPRA.SERIE  IS NOT NULL          
                         AND DEVOLUTIVO.PLACA       IS NOT NULL         
                       ORDER BY D_ORDENDECOMPRA.COMPANIA,          
                                D_ORDENDECOMPRA.SERIE)
    LOOP
      MI_STRPROBLEMAS := 'El elemento ' || RS_PLACAS.ELEMENTO || ' - ' || RS_PLACAS.NOMBRELARGO || ' con placa No ' ||
                         RS_PLACAS.SERIE || ' del Comprobate de Inventario Inicial Nº ' || RS_PLACAS.ORDENDECOMPRA ||
                         ', porque la placa ya se encuentra registrada en el inventario';

      MI_CAMPOS  := 'COMPANIA, 
                     TIPOMOVIMIENTO, 
                     MOVIMIENTO, 
                     CODIGO, 
                     MENSAJE, 
                     CREATED_BY, 
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ''' || 
                    UN_CLASEORDEN || ''', ' || 
                    RS_PLACAS.ORDENDECOMPRA || ', ' || 
                    MI_CONTADOR || ', ''' || 
                    MI_STRPROBLEMAS || ''', ''' || 
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      BEGIN
        BEGIN
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'ERRORALMACEN',
                                           UN_ACCION  => 'I',
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
           MI_MSGERROREXC(1).CLAVE := 'ORDENCOMP'; 
           MI_MSGERROREXC(1).VALOR := RS_PLACAS.ORDENDECOMPRA; 
           MI_MSGERROREXC(2).CLAVE := 'ELEMENTO'; 
           MI_MSGERROREXC(2).VALOR := RS_PLACAS.ELEMENTO || ' - ' || RS_PLACAS.NOMBRELARGO; 
           PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REG_ERRORALMACEN,
             UN_REEMPLAZOS => MI_MSGERROREXC,
             UN_TABLAERROR => 'ERRORALMACEN'
           );                      
      END;
      MI_CONTADOR := MI_CONTADOR + 1;
    END LOOP;

    IF MI_CONTADOR > 1 
    THEN
      MI_RESPUESTA := 0;
    ELSE
      MI_RESPUESTA := -1;
    END IF;

    RETURN MI_RESPUESTA;

  END FC_REVISARPLACASGENERADAS;

PROCEDURE PR_GENERA_TERCER_MOVIMIENTO
 /*
    NAME              : PR_GENERA_TERCER_MOVIMIENTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 16/07/2019
    TIME              : 09:30 AM
    MODIFIER          : 
    DATE MODIFIED     : 17/05/2018
    TIME              : 8:43 AM
    DESCRIPTION       : Crea el movimiento RDI o TDR dependendien la clase de bodega de salida que tenga la orden de compra
    MODIFICATIONS     : 

  @NAME:  generarTercerMovimiento
  */
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO_DOC_ASOCIADO     IN TIPOMOVIMIENTO.CODIGO%TYPE,
  UN_NUMERO_DOC_ASOCIADO   IN MOVIMIENTO.NUMERO%TYPE,
  UN_TIPO_MOVIMIENTO       IN TIPOMOVIMIENTO.CODIGO%TYPE,  
  UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO 
)AS
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_CRITERIO               PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_FECHA                  DATE;
  MI_DEPENDENCIA_ALMACEN    DEPENDENCIA.CODIGO%TYPE DEFAULT '000000000000';  
  MI_RESPONSABLE_PROVEEDOR  PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL_PROVEEDOR     PCK_SUBTIPOS.TI_SUCURSAL;
  MI_CODIGO                 D_MOVIMIENTO.CODIGO%TYPE;
  MI_DEPENDENCIA_PROVEEDOR  DEPENDENCIA.CODIGO%TYPE;
  MI_RESPONSABLE_BODEGA     PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL_BODEGA        PCK_SUBTIPOS.TI_SUCURSAL;  
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;      

BEGIN

  <<EXTRAER_TIPOMOVIMIENTO>>  
  FOR MI_RS IN(
        SELECT CLASE_BODEGA_ORIGEN,CLASE_BODEGA_DESTINO
        FROM TIPOMOVIMIENTO
        WHERE COMPANIA = UN_COMPANIA 
          AND CODIGO   = UN_TIPO_MOVIMIENTO
    )
    LOOP
     <<EXTRAER_ORDENDCOMPRA>>
      FOR MI_RSS IN(
              SELECT RESPONSABLE,
                     SUCURSAL_RESPONSABLE,
                     DESCRIPCION,
                     FECHA,
                     VALORTOTAL
              FROM ORDENDECOMPRA
              WHERE COMPANIA         = UN_COMPANIA
                AND CLASEORDEN       = UN_TIPO_DOC_ASOCIADO
                AND NUMERO           = UN_NUMERO_DOC_ASOCIADO
                AND NVL(REALIZADA,0) = 0
                )
          LOOP

           -- OBTIENE LAS DEPENDENCIAS ORIGEN Y DESTINO, LOS RESPONSABLES ORIGEN Y DESTINO PARA LA CREACION DEL MOVIMIENTO

              MI_FECHA := PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA(UN_FECHA => MI_RSS.FECHA
                                                              ,UN_DIAS => 1);

   BEGIN 
                  BEGIN                 
                      WITH DEPENDENCIA_ORIGEN AS (
                        SELECT DO.DEPENDENCIA
                              ,DO.RESPONSABLE
                              ,DO.SUCURSAL
                        FROM DEPENDENCIA D
                        INNER JOIN DEPENDENCIA_RESPONSABLE DO
                           ON D.COMPANIA  = DO.COMPANIA
                          AND D.CODIGO    = DO.DEPENDENCIA
                        WHERE D.COMPANIA      = UN_COMPANIA 
                          AND D.CLASE_BODEGA  = MI_RS.CLASE_BODEGA_DESTINO 
                          AND DO.RESPONSABLEALMACEN NOT IN (0) 
                          AND DO.RESPONSABLE=MI_RSS.RESPONSABLE
                          AND ROWNUM = 1
                          ),
                      DEPENDENCIA_DESTINO AS (
                        SELECT DD.RESPONSABLE
                              ,DD.SUCURSAL
                        FROM DEPENDENCIA D
                        INNER JOIN DEPENDENCIA_RESPONSABLE DD
                           ON D.COMPANIA  = DD.COMPANIA
                          AND D.CODIGO    = DD.DEPENDENCIA
                        WHERE D.COMPANIA      = UN_COMPANIA 
                          AND D.CLASE_BODEGA  = MI_RS.CLASE_BODEGA_DESTINO  
                          AND DD.RESPONSABLEALMACEN NOT IN (0) 
                          AND DD.RESPONSABLE=MI_RSS.RESPONSABLE
                          AND ROWNUM = 1
                          )
                      SELECT DO.DEPENDENCIA
                            ,DO.RESPONSABLE RESPONSABLE_ORIGEN
                            ,DO.SUCURSAL SUCURSAL_ORIGEN
                            ,DD.RESPONSABLE RESPONSABLE_DESTINO
                            ,DD.SUCURSAL SUCURSAL_DESTINO                      
                       INTO MI_DEPENDENCIA_PROVEEDOR
                           ,MI_RESPONSABLE_PROVEEDOR
                           ,MI_SUCURSAL_PROVEEDOR
                           ,MI_RESPONSABLE_BODEGA
                           ,MI_SUCURSAL_BODEGA
                       FROM DEPENDENCIA_ORIGEN DO
                          ,DEPENDENCIA_DESTINO DD;
                    EXCEPTION WHEN  NO_DATA_FOUND THEN 
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_REEMPLAZOS => MI_MSGERROR,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEPENDENCIAS_OD_EDI
                     );       
                END;     

    BEGIN
     BEGIN
                MI_CAMPOS := 'COMPANIA
                              ,TIPOMOVIMIENTO
                              ,NUMERO
                              ,TERCERO
                              ,SUCURSAL
                              ,FECHA
                              ,HORA
                              ,DESCRIPCION
                              ,TIPOMOVASOCIADO
                              ,MOVASOCIADO
                              ,FECHAMOVASOCIADO
                              ,VALORDOCASOCIADO
                              ,VALORTOTAL
                              ,VACIA
                              ,BODEGA_ORIGEN
                              ,BODEGA_DESTINO
                              ,CLASE_BODEGA_ORIGEN
                              ,CLASE_BODEGA_DESTINO
                              ,DEPENDENCIA_ORIGEN
                              ,DEPENDENCIA_DESTINO
                              ,RESPONSABLE_ORIGEN
                              ,SUCURSAL_RESORIGEN
                              ,RESPONSABLE_DESTINO
                              ,SUCURSAL_RESDESTINO
                              ,CREATED_BY 
                              ,DATE_CREATED' ;

                 MI_VALORES := '''' || UN_COMPANIA ||'''
                               ,''' || UN_TIPO_MOVIMIENTO||'''
                               ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                               ,''' || MI_RSS.RESPONSABLE ||'''
                               ,''' || MI_RSS.SUCURSAL_RESPONSABLE||'''
                               ,  ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA + 1) ||'
                               , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE + (1/86400),''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') -- RM 20/08/2019 Se modifica linea para sume un segundo a la hora del movimiento y asi evitar que quede con la misma hora de la salida al servicio                                                                      
                               ,''' || NVL(MI_RSS.DESCRIPCION, ' ') ||'''
                               ,''' || UN_TIPO_DOC_ASOCIADO ||'''
                               ,  ' || UN_NUMERO_DOC_ASOCIADO ||'
                               ,  ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RSS.FECHA) ||'
                               ,  ' || MI_RSS.VALORTOTAL ||'
                               ,  ' || MI_RSS.VALORTOTAL ||'
                               , ''0''
                               ,''' || MI_RS.CLASE_BODEGA_ORIGEN  || '''
                               ,''' || MI_RS.CLASE_BODEGA_DESTINO || '''
                               ,''' || MI_RS.CLASE_BODEGA_ORIGEN  || '''
                               ,''' || MI_RS.CLASE_BODEGA_DESTINO || '''
                               ,''' || MI_DEPENDENCIA_PROVEEDOR   || ''' 
                               ,''' || MI_DEPENDENCIA_PROVEEDOR     || '''
                               ,''' || MI_RESPONSABLE_PROVEEDOR   || '''
                               ,''' || MI_SUCURSAL_PROVEEDOR      || '''
                               ,''' || MI_RESPONSABLE_PROVEEDOR     || '''
                               ,''' || MI_SUCURSAL_PROVEEDOR         || '''
                               ,''' || UN_USUARIO                 || ''' 
                               ,SYSDATE ';                            


                 PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'MOVIMIENTO'
                                           ,UN_ACCION  => 'I'
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);

                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_MOVIMIENTO);
            END;


               FOR MI_LS IN(
                    SELECT D_ORDENDECOMPRA.*
                          ,INVENTARIO.TIPO
                          ,INVENTARIO.IDENTIFICADOR
                    FROM D_ORDENDECOMPRA
                    LEFT JOIN INVENTARIO
                          ON D_ORDENDECOMPRA.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                          AND D_ORDENDECOMPRA.COMPANIA = INVENTARIO.COMPANIA
                    WHERE D_ORDENDECOMPRA.COMPANIA = UN_COMPANIA
                      AND CLASEORDEN               = UN_TIPO_DOC_ASOCIADO
                      AND ORDENDECOMPRA            = UN_NUMERO_DOC_ASOCIADO
                      AND D_ORDENDECOMPRA.SERIE IS NOT NULL)
               LOOP            

                      MI_CRITERIO     := 'COMPANIA          = '''|| UN_COMPANIA ||'''
                                         AND TIPOMOVIMIENTO = '''|| UN_TIPO_MOVIMIENTO ||'''
                                         AND MOVIMIENTO     =   '|| UN_NUMERO_DOC_ASOCIADO ||'';
                      MI_CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA     => 'D_MOVIMIENTO'
                                                                    ,UN_CRITERIO => MI_CRITERIO

                                                                    ,UN_CAMPO    => 'CODIGO');                
                      BEGIN
                       BEGIN
                       -- CREACION DETALLES 
                        MI_CAMPOS := 'COMPANIA
                                      ,TIPOMOVIMIENTO
                                      ,MOVIMIENTO
                                      ,CODIGO
                                      ,ELEMENTO
                                      ,ESPECIFICACION
                                      ,CENTRODECOSTO
                                      ,CANTIDAD
                                      ,SALDOCANT
                                      ,VALORUNITARIO
                                      ,VALORTOTAL
                                      ,IND_REG
                                      ,FECHA
                                      ,HORA
                                      ,ORDENDESUMINISTRO
                                      ,MARCA
                                      ,AJUSTECENTAVOS
                                      ,CANTIDADDOCAS
                                      ,VALORDOCAS
                                      ,SERIE
                                      ,SERIEDEVOLUTIVO
                                      ,ESTADO
                                      ,CREATED_BY 
                                      ,DATE_CREATED';

                        MI_VALORES := ''''||UN_COMPANIA||'''
                                      ,'''||UN_TIPO_MOVIMIENTO||'''
                                      ,'||UN_NUMERO_DOC_ASOCIADO||'
                                      ,'||MI_CODIGO||'
                                      ,'''||MI_LS.ELEMENTO||'''
                                      ,'''||NVL(MI_LS.ESPECIFICACION, ' ')||'''
                                      ,'''||NVL(MI_LS.CENTRODECOSTO, ' ')||'''
                                      ,''1''
                                      ,''1''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,'''||MI_LS.VALORUNITARIO||'''
                                      ,''-1''
                                      ,  ' || PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA + 1) ||'
                                      , TO_DATE(''30/12/1899 '' || TO_CHAR(SYSDATE + (1/86400),''HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'')   -- RM 20/08/2019 Se modifica linea para sume un segundo a la hora del movimiento y asi evitar que quede con la misma hora de la salida al servicio                                                                      
                                      ,'''||NVL(MI_LS.ORDENDESUMINISTRO, 0)||'''
                                      ,'''||NVL(MI_LS.MARCA, '  ')||'''
                                      ,''0''
                                      ,'''||MI_LS.CANTIDAD||'''
                                      ,'''||MI_LS.VLRTOTAL||'''
                                      ,'''||NVL(MI_LS.SERIE,0)||'''
                                      ,'''||NVL(MI_LS.SERIEDEVOLUTIVO, ' ')||'''
                                      ,'''||PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => (NVL(MI_LS.ESTADO, ' ') = ' ')
                                                                 ,UN_SI => 'B'
                                                                 ,UN_NO=> MI_LS.ESTADO)||'''
                                      ,''' || UN_USUARIO || ''' 
                                      ,SYSDATE ';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'D_MOVIMIENTO'
                                                      ,UN_ACCION   => 'I'
                                                      ,UN_CAMPOS   => MI_CAMPOS
                                                      ,UN_VALORES  => MI_VALORES);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                       END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO);
                     END;                    

               END LOOP; 
      END LOOP EXTRAER_ORDENDCOMPRA;

    END LOOP EXTRAER_TIPOMOVIMIENTO;        

END  PR_GENERA_TERCER_MOVIMIENTO;             

PROCEDURE PR_MOVSPOSTERIORESAUX
 /*
    NAME              : PR_MOVSPOSTERIORESAUX
    AUTHORS           : JEIMMY CAROLINA ROJAS GUERRERO
    DESCRIPTION       : CONTROLA QUE NO SE PUEDAN REALIZAR MOVIMIENTOS ANTERIORES A LA ULTIMA FECHA DE LOS MOVIMIENTOS 
                        TENIENDO EN CUENTA PLACA,ELEMENTO,FUENTEDERECURSO,REFERENCIA_CNT,AUXILIAR,CODIGOPROYECTO,CENTRODECOSTO,LOTE,BODEGA ORIGEN Y DESTINO.
  */
  (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA               IN DATE,
    UN_HORA                IN DATE,
    UN_ELEMENTO            IN VARCHAR2,
    UN_SERIE               IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_TIPOMOVIMIENTO      IN VARCHAR2 ,
    UN_MOVIMIENTO          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_FUENTER             IN D_MOVIMIENTO.FUENTEDERECURSO%TYPE,
    UN_REFERENCIA          IN D_MOVIMIENTO.REFERENCIA_CNT%TYPE,
    UN_AUXILIAR            IN D_MOVIMIENTO.AUXILIAR%TYPE,
    UN_PROYECTO            IN D_MOVIMIENTO.CODIGOPROYECTO%TYPE,
    UN_CCOSTO              IN D_MOVIMIENTO.CENTRODECOSTO%TYPE,
    UN_LOTE                IN D_MOVIMIENTO.LOTE%TYPE
  )
AS
    MI_BODEGAO             VARCHAR2(10 CHAR);
    MI_BODEGAD             VARCHAR2(10 CHAR);
    MI_POSTERIORES         NUMBER;
    MI_ERRORES             VARCHAR2(32000 CHAR);
    VAR1                   DATE;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    BEGIN
        SELECT BODEGA_ORIGEN,BODEGA_DESTINO
        INTO MI_BODEGAO,MI_BODEGAD
        FROM MOVIMIENTO
        WHERE COMPANIA = UN_COMPANIA
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND NUMERO = UN_MOVIMIENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_BODEGAO := '0';
        MI_BODEGAD := '0';
    END;
        
    MI_POSTERIORES := 0;
    MI_ERRORES := ' ';
    VAR1 := TO_DATE(TO_CHAR(UN_FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(UN_HORA,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS');
  
    FOR RS IN(SELECT D_MOVIMIENTO.TIPOMOVIMIENTO,
                D_MOVIMIENTO.MOVIMIENTO,
                D_MOVIMIENTO.FECHA,
                D_MOVIMIENTO.HORA
              FROM D_MOVIMIENTO
              INNER JOIN MOVIMIENTO
              ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
              AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
              AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
              WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                AND D_MOVIMIENTO.ELEMENTO = UN_ELEMENTO
                AND D_MOVIMIENTO.SERIE = UN_SERIE
                AND D_MOVIMIENTO.IND_REG  NOT IN (0)
                AND D_MOVIMIENTO.FUENTEDERECURSO = UN_FUENTER
                AND D_MOVIMIENTO.REFERENCIA_CNT = UN_REFERENCIA
                AND D_MOVIMIENTO.AUXILIAR = UN_AUXILIAR
                AND D_MOVIMIENTO.CODIGOPROYECTO = UN_PROYECTO
                AND D_MOVIMIENTO.CENTRODECOSTO = UN_CCOSTO
                AND D_MOVIMIENTO.LOTE = UN_LOTE
                AND (MOVIMIENTO.BODEGA_ORIGEN = MI_BODEGAO
                    OR MOVIMIENTO.BODEGA_DESTINO = MI_BODEGAD)
                AND TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS') > VAR1)
    LOOP
        MI_POSTERIORES := MI_POSTERIORES + 1;
        MI_ERRORES     := MI_ERRORES || CHR(13) || CHR(10) || RS.TIPOMOVIMIENTO || '-' || RS.MOVIMIENTO || '-' || TO_CHAR(RS.FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(RS.HORA,'HH24:MI:SS') ;
    END LOOP;
  
    IF MI_POSTERIORES > 0 THEN
        MI_MSGERROR(1).CLAVE := 'LISTADO';
        MI_MSGERROR(1).VALOR := MI_ERRORES;
        
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => -20000
                                  ,UN_TABLAERROR => 'D_MOVIMIENTO'
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REALIZAR_EXISTMOV
                                  ,UN_REEMPLAZOS => MI_MSGERROR);
    END IF;
END PR_MOVSPOSTERIORESAUX;			  
FUNCTION FC_BUSCAEXISTAUX
/* NAME              : FC_BUSCAEXISTAUX
   AUTHORS           : JEIMMY CAROLINA ROJAS GUERRERO
   DATE              : 17/07/2025
   DESCRIPTION       : MUESTRA EL VALOR DE LAS EXISTENCIAS PARA UN ELEMENTO TENIENDO
					   EN CUENTA LOS AUXILIARES DE LA TABLA INVENTARIO_BODEGA.
*/
(
	UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO    IN VARCHAR2,
	UN_BODEGA	   IN VARCHAR2,
	UN_FUENTER	   IN VARCHAR2,
	UN_REFERENCIA  IN VARCHAR2,
	UN_AUXILIAR    IN VARCHAR2,
	UN_PROYECTO    IN VARCHAR2,
	UN_CENTROCOSTO IN VARCHAR2,
	UN_LOTE 	   IN VARCHAR2
)
RETURN NUMBER
AS
	MI_EXISTENCIA  PCK_SUBTIPOS.TI_DOBLE := 0;
BEGIN
  SELECT CANTIDAD
  INTO MI_EXISTENCIA
  FROM INVENTARIO_BODEGA
  WHERE COMPANIA = UN_COMPANIA
      AND ELEMENTO = UN_ELEMENTO
      AND BODEGA = UN_BODEGA
      AND FUENTEDERECURSO = UN_FUENTER
      AND REFERENCIA = UN_REFERENCIA
      AND AUXILIAR = UN_AUXILIAR
      AND PROYECTO = UN_PROYECTO
      AND CENTRODECOSTO = UN_CENTROCOSTO
      AND LOTE = UN_LOTE;
  RETURN NVL(MI_EXISTENCIA,0);
  EXCEPTION WHEN NO_DATA_FOUND THEN
  RETURN 0;
END FC_BUSCAEXISTAUX;
PROCEDURE PR_KARDEXAUXINVBOD
 /*
      NAME              : PR_KARDEXAUXINVBOD
      AUTHORS           : JEIMMY CAROLINA ROJAS GUERRERO
      DATE              : 18/07/2025
      DESCRIPTION       : Procedimiento que actualiza los campos CANTIDAD,VLRUNITARIO y VALORTOTAL de la tabla INVENTARIO_BODEGA
						  cuando el valor del parametro MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN sea SI.
*/
(
	UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
	UN_ELEMENTO         IN  PCK_SUBTIPOS.TI_ELEMENTO
)
AS
	MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
	MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN
	BEGIN
		MI_TABLA := 'INVENTARIO_BODEGA';
        MI_MERGEUSING := 'SELECT SALDO.COMPANIA,SALDO.ELEMENTO,
							SALDO.CODIGOPROYECTO,SALDO.BODEGA,
                            SALDO.FUENTEDERECURSO,SALDO.AUXILIAR,
                            SALDO.REFERENCIA_CNT,SALDO.CENTRODECOSTO,SALDO.LOTE,
                            SUM(SALDO.CANT_ENTRADA) ENTRADA_CANT,
                            SUM(SALDO.CANT_SALIDA) SALIDA_CANT,
                            SUM(SALDO.CANT_ENTRADA - SALDO.CANT_SALIDA) SALDO_FECHA,
                            SUM(SALDO.TOT_ENTRADA - SALDO.TOT_SALIDA) TOTAL
                          FROM(
							SELECT D_MOVIMIENTO.COMPANIA,
                                    D_MOVIMIENTO.CODIGOPROYECTO,
                                            (CASE WHEN MOVIMIENTO.CLASE_BODEGA_DESTINO IN (20) 
                                                THEN BODEGA_DESTINO 
                                            END) BODEGA,
                                            D_MOVIMIENTO.ELEMENTO,
                                            INVENTARIO.NOMBRELARGO, 
                                            UNIDAD.NOMBRE UNIDAD,
                                            SUM(D_MOVIMIENTO.CANTIDAD) CANT_ENTRADA,
                                            SUM(D_MOVIMIENTO.VALORTOTAL) TOT_ENTRADA,
                                            0 CANT_SALIDA,
                                            0 TOT_SALIDA,
                                            D_MOVIMIENTO.MOVASOCIADO,
                                            D_MOVIMIENTO.FUENTEDERECURSO,
                                            D_MOVIMIENTO.AUXILIAR,
                                            D_MOVIMIENTO.REFERENCIA_CNT,
                                            D_MOVIMIENTO.CENTRODECOSTO,
                                            D_MOVIMIENTO.LOTE
                                    FROM MOVIMIENTO 
                                    INNER JOIN D_MOVIMIENTO
                                    ON MOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                    AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                    AND MOVIMIENTO.NUMERO = D_MOVIMIENTO.MOVIMIENTO
                                    INNER JOIN INVENTARIO 
                                    ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                                    AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO 
                                    INNER JOIN TIPOMOVIMIENTO
                                    ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                    AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                    INNER JOIN UNIDAD
                                    ON INVENTARIO.UNIDAD = UNIDAD.UNIDAD
                                    WHERE MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                    AND INVENTARIO.TIPO IN(''C'')
                                    AND (CASE WHEN MOVIMIENTO.CLASE_BODEGA_DESTINO IN (20) 
                                            THEN BODEGA_DESTINO 
                                        END) IS NOT NULL
                                    GROUP BY D_MOVIMIENTO.COMPANIA,
                                        D_MOVIMIENTO.CODIGOPROYECTO,
                                        (CASE WHEN MOVIMIENTO.CLASE_BODEGA_DESTINO IN (20) 
                                            THEN BODEGA_DESTINO 
                                        END),
                                        D_MOVIMIENTO.ELEMENTO,
                                        INVENTARIO.NOMBRELARGO, 
                                        UNIDAD.NOMBRE,
                                        D_MOVIMIENTO.MOVASOCIADO,
                                        D_MOVIMIENTO.FUENTEDERECURSO,
                                        D_MOVIMIENTO.AUXILIAR,
                                        D_MOVIMIENTO.REFERENCIA_CNT,
                                        D_MOVIMIENTO.CENTRODECOSTO,
                                        D_MOVIMIENTO.LOTE
                                    UNION ALL
                                    SELECT D_MOVIMIENTO.COMPANIA, 
                                        D_MOVIMIENTO.CODIGOPROYECTO,
                                        (CASE WHEN MOVIMIENTO.CLASE_BODEGA_ORIGEN IN (20)
                                            THEN BODEGA_ORIGEN 
                                        END) BODEGA,
                                        D_MOVIMIENTO.ELEMENTO,
                                        INVENTARIO.NOMBRELARGO, 
                                        UNIDAD.NOMBRE UNIDAD,
                                        0 CANT_ENTRADA,
                                        0 TOT_ENTRADA,
                                        SUM(D_MOVIMIENTO.CANTIDAD) CANT_SALIDA,
                                        SUM(D_MOVIMIENTO.VALORTOTAL) TOT_SALIDA,
                                        D_MOVIMIENTO.MOVASOCIADO,
                                        D_MOVIMIENTO.FUENTEDERECURSO,
                                        D_MOVIMIENTO.AUXILIAR,
                                        D_MOVIMIENTO.REFERENCIA_CNT,
                                        D_MOVIMIENTO.CENTRODECOSTO,
                                        D_MOVIMIENTO.LOTE
                                    FROM MOVIMIENTO 
                                    INNER JOIN D_MOVIMIENTO
                                    ON MOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                    AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                    AND MOVIMIENTO.NUMERO = D_MOVIMIENTO.MOVIMIENTO
                                    INNER JOIN INVENTARIO 
                                    ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                                    AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO 
                                    INNER JOIN TIPOMOVIMIENTO
                                    ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                    AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                    INNER JOIN UNIDAD
                                    ON INVENTARIO.UNIDAD = UNIDAD.UNIDAD
                                    WHERE MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                    AND INVENTARIO.TIPO IN(''C'')
                                    AND (CASE WHEN MOVIMIENTO.CLASE_BODEGA_ORIGEN IN (20)
                                            THEN BODEGA_ORIGEN 
                                        END) IS NOT NULL
                                    GROUP BY D_MOVIMIENTO.COMPANIA, 
                                        D_MOVIMIENTO.CODIGOPROYECTO,
                                        (CASE WHEN MOVIMIENTO.CLASE_BODEGA_ORIGEN IN (20)
                                            THEN BODEGA_ORIGEN 
                                        END),
                                        D_MOVIMIENTO.ELEMENTO,
                                        INVENTARIO.NOMBRELARGO, 
                                        UNIDAD.NOMBRE,
                                        D_MOVIMIENTO.MOVASOCIADO,
                                        D_MOVIMIENTO.FUENTEDERECURSO,
                                        D_MOVIMIENTO.AUXILIAR,
                                        D_MOVIMIENTO.REFERENCIA_CNT,
                                        D_MOVIMIENTO.CENTRODECOSTO,
                                        D_MOVIMIENTO.LOTE) SALDO
                          GROUP BY SALDO.COMPANIA,SALDO.ELEMENTO,
							SALDO.CODIGOPROYECTO,SALDO.BODEGA,
                            SALDO.FUENTEDERECURSO,SALDO.AUXILIAR,
                            SALDO.REFERENCIA_CNT,SALDO.CENTRODECOSTO,
                            SALDO.LOTE';
            
        MI_MERGEENLACE:='TABLA.COMPANIA = VISTA.COMPANIA
                         AND TABLA.ELEMENTO = VISTA.ELEMENTO
                         AND TABLA.BODEGA = VISTA.BODEGA
                         AND TABLA.FUENTEDERECURSO = VISTA.FUENTEDERECURSO
                         AND TABLA.REFERENCIA = VISTA.REFERENCIA_CNT
                         AND TABLA.AUXILIAR = VISTA.AUXILIAR
                         AND TABLA.PROYECTO = VISTA.CODIGOPROYECTO
                         AND TABLA.CENTRODECOSTO = VISTA.CENTRODECOSTO
                         AND TABLA.LOTE = VISTA.LOTE';
                    
        MI_MERGEEXISTE := 'UPDATE SET TABLA.CANTIDAD = VISTA.SALDO_FECHA,
							TABLA.VLRUNITARIO = CASE WHEN VISTA.SALDO_FECHA > 0 THEN VISTA.TOTAL/VISTA.SALDO_FECHA ELSE 0 END,
                            TABLA.VALORTOTAL = CASE WHEN VISTA.SALDO_FECHA > 0 THEN VISTA.TOTAL ELSE 0 END,
                            DATE_MODIFIED = SYSDATE
                           WHERE TABLA.CANTIDAD <> VISTA.SALDO_FECHA
							OR (TABLA.CANTIDAD = 0 AND (TABLA.VLRUNITARIO <> 0 OR TABLA.VALORTOTAL <> 0))
                            OR (TABLA.CANTIDAD > 0 AND (TABLA.VLRUNITARIO = 0 OR TABLA.VALORTOTAL = 0))';
        BEGIN
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA
                                                 ,UN_ACCION => 'MM'
                                                 ,UN_MERGEUSING => MI_MERGEUSING
                                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE);
            
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
			RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
    END;
END PR_KARDEXAUXINVBOD;
END PCK_ALMACEN_COM2;