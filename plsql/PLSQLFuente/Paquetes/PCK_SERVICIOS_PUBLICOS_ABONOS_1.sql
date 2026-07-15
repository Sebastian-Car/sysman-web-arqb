create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_ABONOS AS

--1
  FUNCTION FC_VALIDARABONO(
        /*
        NAME              : FC_VALIDARABONO --> Se pasa del formulario frmAbonosSub Evento AfterUpdate.
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 11/05/2017
        TIME              : 10:37 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Procedimiento que valida si los abonos se pueden actualizar, eliminar o crear, Se ejecuta desde
                            el trigger BI_SP_ABONOS, Retorna -1 si se puede realizar el pago,0 si no se puede realizar.

        PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                            UN_CODIGORUTA     => Codigo de ruta del usuario que se elimina el abono
                            UN_CICLO          => Ciclo del usuario
                            UN_ANO            => Año del cual se va a eliminar el abono.
                            UN_PERIODO        => Periodo del cual se va a eliminar el abono.
                            UN_FECHAABONO     => Fecha en la que se creo el abono.
                            UN_BANCOABONO     => Banco en el que se pago el abono
                            UN_CONSECUTIVOABONO => Consecutivo unico del abono
                            UN_PAGOCONVENIO    => Valor por pago de convenios
                            UN_PAGOTERCERIZADO => Valor por pago de Aseo tercerizado

        MODIFICATIONS     :

        @NAME:    ValidarAbono
        @METHOD:  POST
        */
   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
  ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
  ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
  ,UN_ABONOAUTORIZADO  IN SP_ABONOS.INDAUTORIZADO%TYPE :=0
  ,UN_BANCOABONO       IN SP_ABONOS.BANCO%TYPE :=''
  ,UN_FECHAABONO       IN SP_ABONOS.FECHA%TYPE :=''
  ,UN_CONSECUTIVO      IN SP_ABONOS.CONSECUTIVO%TYPE :=0
  ,UN_PAGOCONVENIO     IN SP_ABONOS.PAGOCONVENIOS%TYPE  :=0
  ,UN_PAGOTERCERIZADO  IN SP_ABONOS.PAGOTERCERIZADO%TYPE :=0
  ,UN_VALORABONO       IN SP_ABONOS.VALOR%TYPE DEFAULT 0
  ,UN_VALORABONOOLD    IN SP_ABONOS.VALOR%TYPE :=0
  ,UN_ACCION           IN VARCHAR2

  ) RETURN NUMBER AS

    MI_BANCO              SP_USUARIO.BANCOPERPROCESO%TYPE;
    MI_ANOACTUAL          SP_CICLO.ANO%TYPE;
    MI_PERIODOACTUAL      SP_CICLO.PERIODO%TYPE;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ESTADOSITIO        SP_USUARIO.FIMM%TYPE;
    MI_VALORFACTURADO     SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
    MI_LECTURA            SP_USUARIO.LECTURA%TYPE DEFAULT 0;
    MI_PERIODOSNOCOBRO    SP_USUARIO.PERIODOSNOCOBROFAC%TYPE DEFAULT 0;
    MI_TOTALABONOS        SP_ABONOS.VALOR%TYPE DEFAULT 0;
    MI_VALORPAR           PARAMETRO.VALOR%TYPE;
    MI_FECHAPREPARACION   SP_CICLO.FECHA_PREPARACION%TYPE;
    MI_CICLOCALC          SP_CICLO.INDCALCULADO%TYPE;
    MI_AUTORIZABORRADO    SP_USUARIO.AUTORIZARBORRADO%TYPE;
  BEGIN

      BEGIN   --Abonos realizados en periodos diferentes al actual
          BEGIN
              SELECT ANO,PERIODO,
                     INDCALCULADO,FECHA_PREPARACION
              INTO   MI_ANOACTUAL,MI_PERIODOACTUAL,
                     MI_CICLOCALC,MI_FECHAPREPARACION
              FROM   SP_CICLO
              WHERE  COMPANIA    = UN_COMPANIA
                AND  NUMERO      = UN_CICLO;

          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_ANOACTUAL :=0;
              MI_PERIODOACTUAL := ' ';
          END;

          IF UN_ANO <> MI_ANOACTUAL OR UN_PERIODO <> MI_PERIODOACTUAL THEN
              --Este abono no corresponde al periodo actual.
              MI_ANOACTUAL := UN_ANO;
              MI_PERIODOACTUAL := UN_PERIODO;
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'ANIO';
          MI_MSGERROR(1).VALOR := MI_ANOACTUAL;
          MI_MSGERROR(2).CLAVE := 'PERIODO';
          MI_MSGERROR(2).VALOR := MI_PERIODOACTUAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_ABONOS'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONONOPERIODO
                                    ,UN_REEMPLAZOS =>  MI_MSGERROR);
          RETURN 0;
      END;

      BEGIN   --Abonos de usuarios con la factura ya paga.
          BEGIN
              SELECT BANCOPERPROCESO
              INTO   MI_BANCO
              FROM   SP_USUARIO
              WHERE  COMPANIA    = UN_COMPANIA
                AND  CICLO       = UN_CICLO
                AND  CODIGORUTA  = UN_CODIGORUTA;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_BANCO:='';
          END;

          IF NVL(MI_BANCO,' ') <> ' ' THEN
              --El usuario ya registra pago no se permite borrar o crear abono.
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_ABONOS'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONOPAGO);
          RETURN 0;

      END;


      IF UN_ACCION = 'ELIMINAR' THEN
          BEGIN   --Abonos autorizados y pagos
              IF UN_ABONOAUTORIZADO <> 0 AND NVL(UN_BANCOABONO,' ') <> ' ' THEN
                  --Este abono se recaudo por caja, si desea eliminarlo realiza el proceso correspondiente por la opción de Actualización de Pagos.
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONOAUTORIZAPAGO);
              RETURN 0;
          END;
      ELSIF UN_ACCION ='MODIFICAR' THEN
          BEGIN
              IF UN_ABONOAUTORIZADO = 0 THEN  --La unica forma de actualizar un abono es que este autorizado.
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              ELSIF UN_VALORABONO <> UN_VALORABONOOLD THEN  --Si trata de actualizar el abono
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --No se permite actualizar el abono, Si lo desea cambiar debe eliminar el abono y luego adicionar uno nuevo.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_MODABONO);
          END;
      ELSIF UN_ACCION ='INSERTAR' THEN

          BEGIN
              --Cuando se autoriza un abono no se debe validar la fecha y el banco.
              IF UN_ABONOAUTORIZADO = 0 AND NVL(UN_BANCOABONO,' ') = ' ' AND UN_FECHAABONO IS NULL THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              ELSIF UN_ABONOAUTORIZADO <> 0 THEN
                  RETURN 0;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --Debe ingresar el banco y la fecha de registro del abono
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONOSINBANCOFECHA );
              RETURN 0;
          END;

          /*BEGIN
              IF UN_ABONOAUTORIZADO = 0 THEN
                  IF (TO_DATE(MI_FECHAPREPARACION) - TO_DATE(UN_FECHAABONO) ) >0 THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END IF;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --La fecha no es válida, es menor a la fecha del ciclo no se puede registrar el abono.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONOFECHACICLO );
              RETURN 0;
          END;*/

          BEGIN
              IF MI_CICLOCALC = 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El ciclo no está calculado, no se puede registrar el Abono.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONONOCALCULO );
              RETURN 0;
          END;

          BEGIN
              SELECT U.FIMM,SUM(F.VALOR_FACTURADO) AS TOTFACTURADO,
                     U.LECTURA,U.BANCOPERPROCESO, U.PERIODOSNOCOBROFAC,
                     U.AUTORIZARBORRADO
              INTO   MI_ESTADOSITIO, MI_VALORFACTURADO,
                     MI_LECTURA, MI_BANCO, MI_PERIODOSNOCOBRO,
                     MI_AUTORIZABORRADO
              FROM   SP_FACTURADO F INNER JOIN SP_USUARIO U
                ON   F.COMPANIA   = U.COMPANIA
               AND   F.CICLO      = U.CICLO
               AND   F.CODIGORUTA = U.CODIGORUTA
               AND   F.ANO        = U.ANO
               AND   F.PERIODO    = U.PERIODO
              WHERE  F.COMPANIA   = UN_COMPANIA
                AND  F.CICLO      = UN_CICLO
                AND  F.CODIGORUTA = UN_CODIGORUTA
                AND  F.ANO        = UN_ANO
                AND  F.PERIODO    = UN_PERIODO
                AND  (F.CONCEPTO BETWEEN 1 AND 48 OR F.CONCEPTO BETWEEN 201 AND 220 OR F.CONCEPTO BETWEEN 246 AND 250)
              GROUP BY U.FIMM,U.LECTURA,U.BANCOPERPROCESO, U.PERIODOSNOCOBROFAC, U.AUTORIZARBORRADO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              --El usuario --USUARIO-- no tiene facturados o no existe.
              MI_MSGERROR(0).CLAVE := 'USUARIO';
              MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOUSUARIONOEXISTE
                                        ,UN_REEMPLAZOS =>  MI_MSGERROR);
              RETURN 0;
          END;

          BEGIN
              IF MI_AUTORIZABORRADO <>0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El usuario ha sido bloqueado, No se permite registrar abonos.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONOUSUARIONOAUT );
              RETURN 0;
          END;

          BEGIN
              IF NVL(MI_BANCO, ' ') <> ' '  THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El usuario ya pago no se permite registrar Abono
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONOPAGO );
              RETURN 0;
          END;

          BEGIN
              IF MI_PERIODOSNOCOBRO <> 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El usuario tiene periodos de no cobro. No se permite registrar Abono
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOPAGOONOCOBRO );
              RETURN 0;
          END;

          BEGIN
              IF UN_VALORABONO <= 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --No se puede realizar un abono menor o igual a cero
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_REGABONOCERO );
              RETURN 0;
          END;

          BEGIN
              MI_VALORPAR :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                        UN_NOMBRE    =>  'FACTURACION EN SITIO',
                                                        UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

              IF MI_VALORPAR ='SI' AND NVL(MI_ESTADOSITIO,' ') = 'P' THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El usuario está en terreno no se permite ingresar abonos
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOTERRENOFIMM );
              RETURN 0;
          END;

          BEGIN
              IF NVL(MI_ESTADOSITIO, ' ') = 'F' AND MI_LECTURA = 0 AND  NVL(MI_BANCO, ' ') = ' ' AND MI_PERIODOSNOCOBRO = 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --Los planos están en la entidad no se permite ingresar abonos
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_ABONOS'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOENTIDADFIMM );
              RETURN 0;
          END;

          BEGIN
              BEGIN
                  SELECT SUM(VALOR)
                  INTO   MI_TOTALABONOS
                  FROM   SP_ABONOS
                  WHERE  COMPANIA   = UN_COMPANIA
                    AND  CICLO      = UN_CICLO
                    AND  CODIGORUTA = UN_CODIGORUTA
                    AND  ANO        = UN_ANO
                    AND  PERIODO    = UN_PERIODO;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_TOTALABONOS := 0;
              END;

              IF (MI_TOTALABONOS + UN_VALORABONO ) >= MI_VALORFACTURADO THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
              --El total del valor abonado no debe ser mayor o igual que el total de la factura. Registrelo como un pago.
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_ABONOS'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOMAYORAFACTURA);
                  RETURN 0;
          END;
      END IF; --Fin acción insertar

      RETURN -1;

  END FC_VALIDARABONO;

--2
  PROCEDURE PR_REGISTRARABONO(

      /*
      NAME              : PR_REGISTRARABONO --> Se pasa del formulario frmAbonosSub.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 09/05/2017
      TIME              : 08:15 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento que se ejecuta al insertar un abono en la tabla SP_ABONO,
                          Crea type de los detalles de abono distribuido y los inserta en la tabla SP_D_ABONO.

      PARAMETERS        : UN_COMPANIA => Compañia actual del suscriptor
                          UN_CICLO    => Ciclo al que pertenece el suscriptor
                          UN_CODIGORUTA => Codigo de ruta del suscriptor
                          UN_ANO => Año en el que se encuentra el suscriptor
                          UN_PERIODO => Periodo en el que se encuentra el suscriptor
                          UN_CONSECUTIVOABONO => Consecutivo con el que se registro el abono
                          UN_VALORABONO => Valor total del abono que registro el suscriptor
                          UN_PAGOCONVENIOS => Abono realizado por convenios
                          UN_PAGOTERCERIZADO => Abono realizado por Aseo tercerizado
                          UN_FECHAABONO => Fecha de realización del abono
                          UN_BANCOABONO => Banco donde se pago el abono
                          UN_USUARIO => Usuario de la aplicación que registro el abono

      MODIFICATIONS     :

      @NAME:    RegistrarAbono
      @METHOD:  POST
      */
       UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
      ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
      ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
      ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
      ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
      ,UN_CONSECUTIVOABONO IN SP_ABONOS.CONSECUTIVO%TYPE
      ,UN_VALORABONO       IN SP_ABONOS.VALOR%TYPE
      ,UN_PAGOCONVENIOS    IN SP_ABONOS.PAGOCONVENIOS%TYPE DEFAULT 0
      ,UN_PAGOTERCERIZADO  IN SP_ABONOS.PAGOTERCERIZADO%TYPE DEFAULT 0
      ,UN_FECHAABONO       IN DATE
      ,UN_BANCOABONO       IN SP_ABONOS.BANCO%TYPE
      ,UN_USUARIO          IN SP_D_ABONOS.CREATED_BY%TYPE
  ) AS
  MI_PARAMETRO            PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARAMETRODESCUENTO   PCK_SUBTIPOS.TI_PARAMETRO;
  MI_SERVICIOSRECLAMO     SP_USUARIO.SERVNORECLAMADO%TYPE DEFAULT ' ';
  MI_TOTDEUDA             SP_FACTURADO.DEUDA%TYPE;
  MI_ABONODIST            TI_ABONO;

  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PARSERVNORECLAMADO   BOOLEAN DEFAULT FALSE;
  MI_RTA                  PCK_SUBTIPOS.TI_LOGICO;
  MI_PERATRASO            SP_USUARIO.PERIODOSATRASO%TYPE;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;
  MI_NUMPQR               SP_D_ABONOS.NUM_PQR%TYPE DEFAULT 0;


  BEGIN
      MI_PARAMETRO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                UN_NOMBRE    =>  'FACTURA SERVICIOS NO RECLAMADOS',
                                                UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_PARAMETRODESCUENTO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    =>  'MANEJA CONTROL DE DESCUENTOS POR CONCEPTOS',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_PARSERVNORECLAMADO := CASE WHEN MI_PARAMETRO = 'SI' THEN TRUE ELSE FALSE END;

      IF MI_PARSERVNORECLAMADO  THEN
          BEGIN
              SELECT SERVNORECLAMADO
              INTO   MI_SERVICIOSRECLAMO
              FROM   SP_USUARIO
              WHERE  COMPANIA     = UN_COMPANIA
                AND  CICLO        = UN_CICLO
                AND  CODIGORUTA   = UN_CODIGORUTA
                AND  ANO          = UN_ANO
                AND  PERIODO      = UN_PERIODO;

              MI_SERVICIOSRECLAMO := CASE WHEN INSTR(MI_SERVICIOSRECLAMO,',') > 0
                                          THEN PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_SERVICIOSRECLAMO)
                                          ELSE MI_SERVICIOSRECLAMO
                                     END;
          EXCEPTION WHEN NO_DATA_FOUND THEN
               --El usuario --USUARIO-- no tiene facturados o no existe.
              MI_MSGERROR(0).CLAVE := 'USUARIO';
               MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_TABLAERROR => 'SP_USUARIO'
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOUSUARIONOEXISTE
                                         ,UN_REEMPLAZOS =>  MI_MSGERROR);
          END;

      END IF;

      BEGIN
          SELECT U.PERIODOSATRASO,SUM(F.DEUDA - F.VALORABONOANT)
          INTO   MI_PERATRASO, MI_TOTDEUDA
          FROM   (SP_FACTURADO F  INNER JOIN SP_USUARIO U
                  ON F.COMPANIA = U.COMPANIA
                  AND F.CICLO = U.CICLO
                  AND F.CODIGORUTA = U.CODIGORUTA
                  AND F.ANO = U.ANO
                  AND F.PERIODO = U.PERIODO)
                  INNER JOIN SP_CONCEPTOS C
                  ON F.COMPANIA = C.COMPANIA
                  AND F.CONCEPTO = C.CODIGO
          WHERE  F.COMPANIA   = UN_COMPANIA
            AND  F.CICLO      = UN_CICLO
            AND  F.CODIGORUTA = UN_CODIGORUTA
            AND  F.ANO        = UN_ANO
            AND  (F.CONCEPTO BETWEEN 1 AND 49 OR F.CONCEPTO BETWEEN 201 AND 220 OR F.CONCEPTO BETWEEN 246 AND 249)
            AND  (CASE WHEN MI_SERVICIOSRECLAMO <> ' ' THEN C.SERVICIO ELSE '1' END )
                  IN
                 (CASE WHEN MI_SERVICIOSRECLAMO <> ' ' THEN
                      MI_SERVICIOSRECLAMO
                  ELSE '1'
                  END )
          GROUP BY U.PERIODOSATRASO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
           --El usuario --USUARIO-- no tiene facturados o no existe.
          MI_MSGERROR(0).CLAVE := 'USUARIO';
          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_FACTURADO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOUSUARIONOEXISTE
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
      END;

      MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                UN_NOMBRE    =>  'ABONAR EN ORDEN POR CONCEPTO',
                                                UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR =>  SYSDATE),'NO');

      --Si maneja conceptos negativos realiza la distribución por discriminación no en orden
      IF (MI_PARSERVNORECLAMADO OR MI_PARAMETRO = 'SI') AND MI_PARAMETRODESCUENTO ='NO' THEN
          MI_ABONODIST :=  FC_ABONOPRIORIDADIND
              (UN_COMPANIA         => UN_COMPANIA
              ,UN_CICLO            => UN_CICLO
              ,UN_CODIGORUTA       => UN_CODIGORUTA
              ,UN_ANO              => UN_ANO
              ,UN_PERIODO          => UN_PERIODO
              ,UN_CONSECUTIVO      => UN_CONSECUTIVOABONO
              ,UN_VALORABONO       => UN_VALORABONO
              ,UN_DBLDEUDA         => MI_TOTDEUDA );
      ELSE
          --Carga Type con la distribución final del abono.
          MI_ABONODIST := FC_DISCRIMINARABONOSIND
              (UN_COMPANIA         => UN_COMPANIA
              ,UN_CICLO            => UN_CICLO
              ,UN_CODIGORUTA       => UN_CODIGORUTA
              ,UN_ANO              => UN_ANO
              ,UN_PERIODO          => UN_PERIODO
              ,UN_VALORABONO       => UN_VALORABONO
              ,UN_CONSECUTIVO      => UN_CONSECUTIVOABONO);

      END IF;

      --Convenios tercerizado
      MI_PARAMETRO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                UN_NOMBRE    =>  'ABONOS PRIORIDAD TERCERIZADO Y CONVENIOS',
                                                UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR =>  SYSDATE),'NO');
      IF MI_PARAMETRO = 'SI' THEN
          IF UN_PAGOCONVENIOS <> 0 THEN
              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                          (UN_COMPANIA     => UN_COMPANIA
                          ,UN_CICLO        => UN_CICLO
                          ,UN_CODIGORUTA   => UN_CODIGORUTA
                          ,UN_ANO          => UN_ANO
                          ,UN_PERIODO      => UN_PERIODO
                          ,UN_FECHA        => UN_FECHAABONO
                          ,UN_BANCO        => UN_BANCOABONO
                          ,UN_PAQUETE      => '888'
                          ,UN_REVERSA      => 0
                          ,UN_PAGODOBLE    => 0
                          ,UN_CONVENIO     => 'SI'  );

          END IF;

          IF UN_PAGOTERCERIZADO <> 0 THEN
              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                          (UN_COMPANIA      => UN_COMPANIA
                          ,UN_CICLO         => UN_CICLO
                          ,UN_CODIGORUTA    => UN_CODIGORUTA
                          ,UN_ANO           => UN_ANO
                          ,UN_PERIODO       => UN_PERIODO
                          ,UN_FECHA         => UN_FECHAABONO
                          ,UN_BANCO         => UN_BANCOABONO
                          ,UN_PAQUETE       => '888'
                          ,UN_REVERSA       => 0
                          ,UN_PAGODOBLE     => 0
                          ,UN_TERCER        => 'SI' );

          END IF;

      END IF;


      --Se crea el encabezado en la tabla SP_RECAUDOS
      BEGIN
          MI_TABLA := 'SP_RECAUDOS';
          MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

          MI_MERGEENLACE := '     COMPANIA      = '''|| UN_COMPANIA || '''
                              AND FECHA         = TO_DATE('''|| TO_CHAR(UN_FECHAABONO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                              AND BANCO         = '''|| UN_BANCOABONO ||'''
                              AND NUMEROPAQUETE = ''888'' ';

          MI_MERGEEXISTE := ' UPDATE SET  VALORREPORTADO      = VALORREPORTADO + '|| UN_VALORABONO ||'
                                         ,CUPONESREPORTADOS   = CUPONESREPORTADOS + 1
                                         ,VALORREGISTRADO     = VALORREGISTRADO + '|| UN_VALORABONO ||'
                                         ,CUPONESREGISTRADOS  = CUPONESREGISTRADOS + 1
                                         ,MODIFIED_BY         = '''|| UN_USUARIO ||'''
                                         ,DATE_MODIFIED       = SYSDATE ';

          MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                      FECHA,
                                      BANCO,
                                      NUMEROPAQUETE,
                                      BARRAS,
                                      TIPO,
                                      VALORREPORTADO,
                                      CUPONESREPORTADOS,
                                      VALORREGISTRADO,
                                      CUPONESREGISTRADOS,
                                      USUARIO,
                                      COMENTARIOS,
                                      CREATED_BY,
                                      DATE_CREATED )
                              VALUES ('''|| UN_COMPANIA ||'''   ,
                                      TO_DATE('''|| TO_CHAR(UN_FECHAABONO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ,
                                      '''|| UN_BANCOABONO ||''' ,
                                      ''888'' ,
                                      0,
                                      ''1'',
                                      '|| UN_VALORABONO ||' ,
                                      1,
                                      '|| UN_VALORABONO ||' ,
                                      1,
                                      '''|| CASE WHEN NVL(UN_USUARIO,' ') = ' ' THEN 'DESCONOCIDO' ELSE UN_USUARIO END ||''' ,
                                      ''PAGOS REALIZADOS POR ABONOS'',
                                      '''|| UN_USUARIO ||''' ,
                                      SYSDATE )  ';

          BEGIN
              MI_FILAS := PCK_DATOS.FC_ACME
                              (UN_TABLA       => MI_TABLA
                              ,UN_ACCION      => 'IM'
                              ,UN_MERGEUSING  => MI_MERGEUSING
                              ,UN_MERGEENLACE => MI_MERGEENLACE
                              ,UN_MERGEEXISTE => MI_MERGEEXISTE
                              ,UN_MERGENOEXIS => MI_MERGENOEXIS);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'ABONO';
          MI_MSGERROR(2).VALOR := UN_VALORABONO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                     UN_ERROR_COD => PCK_ERRORES.ERR_REGISTRARABONOCABEZARECAUD,
                                     UN_REEMPLAZOS => MI_MSGERROR);
      END;

      --Actualiza la pqr de los servicios no RECLAMADOS
      IF MI_PARSERVNORECLAMADO THEN
          BEGIN
              SELECT ORD.NUMORDEN
              INTO   MI_NUMPQR
              FROM   SP_ORDENTRABAJO ORD INNER JOIN SP_D_ORDENTRABAJO DORD
                     ON  ORD.COMPANIA = DORD.COMPANIA
                     AND ORD.CLASEDOC = DORD.CLASEDOC
                     AND ORD.NUMORDEN = DORD.ORDENTRABAJO
              WHERE  ORD.COMPANIA   = UN_COMPANIA
                AND  ORD.CODIGORUTA = UN_CODIGORUTA
                AND  ORD.ACTIVO <> 0
                AND  ORD.CLASEDOC = 'ORD'
                AND  DORD.FECHASOLUCION  IS NULL
                AND  DORD.TIPONOTIFICACION IS NULL
                AND  DORD.FECHANOTIFICACION IS NULL
                AND  DORD.TIPORESPUESTA IS NULL
                AND  ROWNUM = 1
              ORDER BY ORD.FECHASOLICITUD DESC;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_NUMPQR := 0;
          END;
      END IF;

      --Inserción hacia la tabla sp_d_abonos, Esto hace que el trigger de SP_D_ABONOS actualice facturados y d_recaudos.
      FOR i IN MI_ABONODIST.FIRST .. MI_ABONODIST.LAST
      LOOP
          BEGIN
              IF MI_ABONODIST.EXISTS(i) THEN
                  MI_TABLA := 'SP_D_ABONOS';
                  MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                  MI_MERGEENLACE := '     COMPANIA    = '''|| UN_COMPANIA || '''
                                      AND CICLO       = '|| UN_CICLO ||'
                                      AND CODIGORUTA  = '''|| UN_CODIGORUTA ||'''
                                      AND ANO         = '|| UN_ANO ||'
                                      AND PERIODO     = '''|| UN_PERIODO ||'''
                                      AND CONCEPTO    = '|| MI_ABONODIST(i).CONCEPTO ||'
                                      AND CONSECUTIVO = '|| MI_ABONODIST(i).CONSECUTIVO ||'
                                      ';

                  MI_MERGEEXISTE := ' UPDATE SET VALOR          =  '|| MI_ABONODIST(i).VALOR ||'
                                                 ,VALORACT      =  '|| MI_ABONODIST(i).VALORACT ||'
                                                 ,VALORANT      =  '|| MI_ABONODIST(i).VALORANT ||'
                                                 ,NUM_PQR        = '|| MI_NUMPQR ||'
                                                 ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                                 ,DATE_MODIFIED = SYSDATE';

                  MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                              CICLO,
                                              CODIGORUTA,
                                              ANO,
                                              PERIODO,
                                              CONCEPTO,
                                              CONSECUTIVO,
                                              VALOR,
                                              VALORACT,
                                              VALORANT,
                                              NUM_PQR,
                                              CREATED_BY,
                                              DATE_CREATED)
                                      VALUES ('''||  MI_ABONODIST(i).COMPANIA ||'''   ,
                                              '|| MI_ABONODIST(i).CICLO ||'           ,
                                              '''|| MI_ABONODIST(i).CODIGORUTA ||'''  ,
                                              '|| MI_ABONODIST(i).ANO ||'             ,
                                              '''|| MI_ABONODIST(i).PERIODO ||'''     ,
                                              '|| MI_ABONODIST(i).CONCEPTO ||'        ,
                                              '|| MI_ABONODIST(i).CONSECUTIVO ||'     ,
                                              '|| MI_ABONODIST(i).VALOR ||'           ,
                                              '|| MI_ABONODIST(i).VALORACT ||'        ,
                                              '|| MI_ABONODIST(i).VALORANT ||'        ,
                                              '|| MI_NUMPQR ||'                       ,
                                              '''|| UN_USUARIO ||'''                  ,
                                              SYSDATE )  ';

                  BEGIN
                      MI_FILAS := PCK_DATOS.FC_ACME
                                      (UN_TABLA       => MI_TABLA
                                      ,UN_ACCION      => 'IM'
                                      ,UN_MERGEUSING  => MI_MERGEUSING
                                      ,UN_MERGEENLACE => MI_MERGEENLACE
                                      ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                      ,UN_MERGENOEXIS => MI_MERGENOEXIS);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'USUARIO';
              MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
              MI_MSGERROR(2).CLAVE := 'CONCEPTO';
              MI_MSGERROR(2).VALOR := MI_ABONODIST(i).CONCEPTO ;
              MI_MSGERROR(3).CLAVE := 'VALOR';
              MI_MSGERROR(3).VALOR := MI_ABONODIST(i).VALOR ;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_DISTRIBUCIONABONO,
                                         UN_REEMPLAZOS => MI_MSGERROR);
          END;
      END LOOP;


      BEGIN   --Actualiza los periodos de atraso del usuario si el abono
          IF MI_PERATRASO > 0 AND MI_TOTDEUDA > 0 THEN

              MI_TABLA     := ' SP_USUARIO ';

              MI_CONDICIONACME := '    COMPANIA   = '''|| UN_COMPANIA ||'''
                                   AND CICLO      =   '|| UN_CICLO ||'
                                   AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                   AND ANO        =   '|| UN_ANO ||'
                                   AND PERIODO    = '''|| UN_PERIODO ||''' ';

              IF UN_VALORABONO < MI_TOTDEUDA THEN
                  MI_CAMPOS := 'PERIODOSATRASO = (PERIODOSATRASO - '|| ROUND((UN_VALORABONO / (MI_TOTDEUDA / MI_PERATRASO)) ) ||'  ) ';
              ELSE
                  MI_CAMPOS := 'PERIODOSATRASO = 0';
              END IF;

              BEGIN
                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICIONACME);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;

          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN

          MI_MSGERROR(1).CLAVE := 'CICLO';
          MI_MSGERROR(1).VALOR :=  UN_CICLO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUALIZACALCCICLO,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;




  END PR_REGISTRARABONO;

--3 
  PROCEDURE PR_REGISTRARABONODETALLE(

     /*
     NAME              : PR_REGISTRARABONODETALLE --> Se pasa del formulario frmAbonosSub.
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 05/06/2017
     TIME              : 10:22 AM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Procedimiento que se ejecuta al insertar un registro en la tabla SP_D_ABONO
                         Actualiza los valores en las tablas SP_FACTURADO, SP_D_RECAUDO, SP_D_RECAUDO_ABONO

     PARAMETERS        :

     MODIFICATIONS     :

     @NAME:    RegistrarAbonoDetalle
     @METHOD:  POST
     */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
     ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
     ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
     ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
     ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
     ,UN_CONSECUTIVO      IN SP_D_ABONOS.CONSECUTIVO%TYPE
     ,UN_CONCEPTO         IN SP_D_ABONOS.CONCEPTO%TYPE
     ,UN_VALORACT         IN SP_D_ABONOS.VALORACT%TYPE
     ,UN_VALORANT         IN SP_D_ABONOS.VALORANT%TYPE
     ,UN_USUARIO          IN SP_D_ABONOS.CREATED_BY%TYPE
  ) AS

  MI_BANCO   SP_ABONOS.BANCO%TYPE;
  MI_FECHA   DATE;
  MI_VALOR   SP_ABONOS.VALOR%TYPE;

  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;

  BEGIN
      BEGIN
          --Actualizo los facturados en la tabla facturado
          PR_ACTUALIZARABONOFACTURADO
              ( UN_COMPANIA     => UN_COMPANIA
               ,UN_CICLO        => UN_CICLO
               ,UN_CODIGORUTA   => UN_CODIGORUTA
               ,UN_ANO          => UN_ANO
               ,UN_PERIODO      => UN_PERIODO
               ,UN_CONCEPTO     => UN_CONCEPTO
               ,UN_ABONOPERIODO => UN_VALORACT
               ,UN_ABONODEUDA   => UN_VALORANT
               ,UN_OPERACION    => 'SUMA'
               ,UN_USUARIO      => UN_USUARIO );

      END ;

      BEGIN
          SELECT BANCO,FECHA,VALOR
          INTO   MI_BANCO,MI_FECHA,MI_VALOR
          FROM   SP_ABONOS
          WHERE  COMPANIA = UN_COMPANIA
          AND  CICLO    = UN_CICLO
          AND  CODIGORUTA = UN_CODIGORUTA
          AND  ANO   = UN_ANO
          AND  PERIODO = UN_PERIODO
          AND CONSECUTIVO = UN_CONSECUTIVO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_MSGERROR(1).CLAVE := 'ANO';
          MI_MSGERROR(1).VALOR := UN_ANO;
          MI_MSGERROR(2).CLAVE := 'PERIORO';
          MI_MSGERROR(2).VALOR := UN_PERIODO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_NODATOSABONO ,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_REEMPLAZOS => MI_MSGERROR);
      END;

      BEGIN
          --Creación en la tabla D_RECAUDO_USUARIO
          MI_TABLA := 'SP_D_RECAUDO_USUARIO';
          MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

          MI_MERGEENLACE := '     COMPANIA        = '''|| UN_COMPANIA || '''
                              AND CICLO           = '|| UN_CICLO ||'
                              AND CODIGORUTA      = '''|| UN_CODIGORUTA ||'''
                              AND TRUNC(FECHA)    = TO_DATE('''|| TO_CHAR(MI_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                              AND BANCO           = '''|| MI_BANCO ||'''
                              AND NUMEROPAQUETE   = ''888''
                              AND CONCEPTO        = '|| UN_CONCEPTO ||'
                              AND CONSECUTIVO     = '|| UN_CONSECUTIVO ||'
                              AND TIPOPAGO        = ''A'' ';

          MI_MERGEEXISTE := ' UPDATE SET VALORABONOACT    =  '|| UN_VALORACT ||'
                                         ,VALORABONOANT      =  '|| UN_VALORANT ||'
                                         ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                         ,DATE_MODIFIED = SYSDATE
                                          ';

          MI_MERGENOEXIS := ' INSERT (
                                      COMPANIA,
                                      CICLO,
                                      CODIGORUTA,
                                      FECHA,
                                      BANCO,
                                      NUMEROPAQUETE,
                                      CONCEPTO,
                                      CONSECUTIVO,
                                      TIPOPAGO,
                                      ANO,
                                      PERIODO,
                                      VALORABONOACT,
                                      VALORABONOANT,
                                      CREATED_BY,
                                      DATE_CREATED)
                              VALUES ('''||  UN_COMPANIA ||'''    ,
                                      '|| UN_CICLO ||'            ,
                                      '''|| UN_CODIGORUTA ||'''   ,
                                      TO_DATE('''|| TO_CHAR(MI_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ,
                                      '''|| MI_BANCO ||'''        ,
                                      ''888''                     ,
                                      '|| UN_CONCEPTO ||'         ,
                                      '|| UN_CONSECUTIVO ||'      ,
                                      ''A''                       ,
                                      '|| UN_ANO ||'              ,
                                      '''|| UN_PERIODO ||'''      ,
                                      '|| UN_VALORACT ||'         ,
                                      '|| UN_VALORANT ||'         ,
                                      '''|| UN_USUARIO ||'''      ,
                                      SYSDATE )  ';

          BEGIN
              MI_FILAS := PCK_DATOS.FC_ACME
                              (UN_TABLA       => MI_TABLA
                              ,UN_ACCION      => 'IM'
                              ,UN_MERGEUSING  => MI_MERGEUSING
                              ,UN_MERGEENLACE => MI_MERGEENLACE
                              ,UN_MERGEEXISTE => MI_MERGEEXISTE
                              ,UN_MERGENOEXIS => MI_MERGENOEXIS);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN

          MI_MSGERROR(1).CLAVE := 'CONCEPTO';
          MI_MSGERROR(1).VALOR :=  UN_CONCEPTO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ABONODRECAUDOUSUARIO,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;


  END PR_REGISTRARABONODETALLE;

--4  
FUNCTION FC_DISCRIMINARABONOSIND
/*
NAME              : FC_DISCRIMINARABONOSIND En access --> discriminarAbonos
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 01/06/2017
TIME              : 08:49 AM
DESCRIPTION       : Procedimiento encargado de crear y distribuir el abono en el type de d_Abonos.
SOURCE MODULE     : SERVICIOS PUBLICOS

PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación.
                    UN_CODIGORUTA => Código de Ruta al cual pertenece el abono.
@NAME:  discriminarAbonos
@METHOD:  POST
*/
(
   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
  ,UN_CODIGORUTA       IN PCK_SUBTIPOS.TI_CODIGORUTA
  ,UN_ANO              IN SP_ABONOS.ANO%TYPE
  ,UN_PERIODO          IN SP_ABONOS.PERIODO%TYPE
  ,UN_VALORABONO       IN SP_ABONOS.VALOR%TYPE
  ,UN_CONSECUTIVO      IN SP_ABONOS.CONSECUTIVO%TYPE
)
RETURN TI_ABONO AS

MI_TOTFACTURADO           PCK_SUBTIPOS.TI_DOBLE;
MI_RS                     SYS_REFCURSOR;
MI_TABONO                 TI_ABONO;
MI_TOTDEUDA               PCK_SUBTIPOS.TI_DOBLE;

BEGIN


  IF UN_VALORABONO > 0 THEN
      --Saca el valor de la deuda para verificar si el valor de la deuda es mayor que el valor del abono
      BEGIN
          SELECT SUM(VALOR_FACTURADO + VALORFINANT + VALORFINACT),
                 SUM(SP_FACTURADO.DEUDA - VALORABONOANT)
          INTO   MI_TOTFACTURADO,
                 MI_TOTDEUDA
          FROM   SP_FACTURADO
          WHERE  SP_FACTURADO.COMPANIA   = UN_COMPANIA
            AND  SP_FACTURADO.CICLO      = UN_CICLO
            AND  SP_FACTURADO.CODIGORUTA = UN_CODIGORUTA
            AND  SP_FACTURADO.ANO        = UN_ANO
            AND  SP_FACTURADO.PERIODO    = UN_PERIODO
            AND  (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 OR SP_FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
            AND  SP_FACTURADO.CONCEPTO NOT IN (12,17);
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TOTFACTURADO := 0;
          MI_TOTDEUDA := 0;
      END;

      --Distribución de abonos en type
      BEGIN
          <<DISTABONO>>
          FOR MI_RS IN
          (
              SELECT COMPANIA,
                     CICLO,
                     CODIGORUTA,
                     ANO,
                     PERIODO,
                     CONCEPTO,
                     DEUDA,
                     VALORABONOANT,
                     VALOR_FACTURADO
              FROM   SP_FACTURADO
              WHERE  COMPANIA   = UN_COMPANIA
                AND  CICLO      = UN_CICLO
                AND  CODIGORUTA = UN_CODIGORUTA
                AND  ANO        = UN_ANO
                AND  PERIODO    = UN_PERIODO
                AND  (CONCEPTO BETWEEN 1 AND 48 OR CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
                AND  CONCEPTO NOT IN (12,17)
                AND  (VALOR_FACTURADO + DEUDA + VALORFINACT + VALORFINANT - VALORABONOACT - VALORABONOANT) <>0
          )
          LOOP
              MI_TABONO(MI_RS.CONCEPTO).COMPANIA := MI_RS.COMPANIA;
              MI_TABONO(MI_RS.CONCEPTO).CICLO := MI_RS.CICLO;
              MI_TABONO(MI_RS.CONCEPTO).CODIGORUTA := MI_RS.CODIGORUTA;
              MI_TABONO(MI_RS.CONCEPTO).ANO := MI_RS.ANO;
              MI_TABONO(MI_RS.CONCEPTO).PERIODO := MI_RS.PERIODO;
              MI_TABONO(MI_RS.CONCEPTO).CONCEPTO := MI_RS.CONCEPTO;
              MI_TABONO(MI_RS.CONCEPTO).VALOR := 0;
              MI_TABONO(MI_RS.CONCEPTO).CONSECUTIVO := UN_CONSECUTIVO ;
              MI_TABONO(MI_RS.CONCEPTO).VALORANT := 0;
              MI_TABONO(MI_RS.CONCEPTO).VALORACT := 0;

              IF MI_TOTDEUDA - UN_VALORABONO > 0 THEN --Abono solo a la deuda
                  IF MI_RS.DEUDA >0 OR (MI_RS.CONCEPTO = 249 AND MI_RS.DEUDA <> 0 )  THEN
                    --MI_TABONO(MI_RS.CONCEPTO).VALORANT := (( (MI_RS.DEUDA - MI_RS.VALORABONOANT) * UN_VALORABONO ) / MI_TOTDEUDA - UN_VALORABONO ) ;
                    MI_TABONO(MI_RS.CONCEPTO).VALORANT := (( (MI_RS.DEUDA - MI_RS.VALORABONOANT) * UN_VALORABONO ) / MI_TOTDEUDA) ;
                  END IF;
              ELSE  --Abono tanto a facturado como a deuda
                  IF MI_RS.DEUDA > 0 OR (MI_RS.CONCEPTO = 249 AND MI_RS.DEUDA <> 0) THEN
                      MI_TABONO(MI_RS.CONCEPTO).VALORANT := (MI_RS.DEUDA - MI_RS.VALORABONOANT);
                  END IF;
                  IF MI_RS.VALOR_FACTURADO > 0 OR (MI_RS.CONCEPTO = 249 AND MI_RS.VALOR_FACTURADO <> 0) THEN
                      MI_TABONO(MI_RS.CONCEPTO).VALORACT :=  ((MI_RS.VALOR_FACTURADO *  (UN_VALORABONO - MI_TOTDEUDA ))  / MI_TOTFACTURADO);
                  END IF;
              END IF;

          END LOOP DISTABONO;


      END;


  END IF; --Fin if valor 0

  PR_CHARLESPESOABIND(UN_TI_DABONO    => MI_TABONO,
                      UN_VALORABONO   => UN_VALORABONO);
  RETURN MI_TABONO;

END FC_DISCRIMINARABONOSIND;

--5
  PROCEDURE PR_CHARLESPESOABIND
      /*
    NAME              : PR_CHARLESPESOABIND En access --> CharlesPesoAb
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 02/06/2017
    TIME              : 10:05 AM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    DESCRIPTION       : Actualiza los valores de la distribución de los abonos ajustandoles los valores decimales.
    PARAMETERS        : UN_TI_DABONO: Type con los datos distribuidos del detalle del abono
                        UN_VALORABONO: Valor total del abono.

    @NAME:  charlesPesoAB
    @METHOD:  GET

    */
    (
       UN_TI_DABONO       IN OUT    TI_ABONO
      ,UN_VALORABONO      IN SP_ABONOS.VALOR%TYPE
    ) AS
      MI_DIFSUMA          SP_D_ABONOS.VALOR%TYPE DEFAULT 0;
      MI_SUMAABONODIST    SP_D_ABONOS.VALOR%TYPE DEFAULT 0;
      MI_CONCEPTO         SP_D_ABONOS.CONCEPTO%TYPE DEFAULT 0;

  BEGIN
      FOR i IN UN_TI_DABONO.FIRST .. UN_TI_DABONO.LAST
      LOOP
          IF UN_TI_DABONO.EXISTS(i) THEN
            IF MI_CONCEPTO = 0 THEN
                MI_CONCEPTO := UN_TI_DABONO(i).CONCEPTO;
            END IF;
            UN_TI_DABONO(i).VALOR := (ROUND(UN_TI_DABONO(i).VALORACT,0) + ROUND(UN_TI_DABONO(i).VALORANT,0));
            UN_TI_DABONO(i).VALORACT := ROUND(UN_TI_DABONO(i).VALORACT,0);
            UN_TI_DABONO(i).VALORANT := ROUND(UN_TI_DABONO(i).VALORANT,0);

            MI_SUMAABONODIST := MI_SUMAABONODIST + (UN_TI_DABONO(i).VALORANT + UN_TI_DABONO(i).VALORACT);
          END IF;

      END LOOP;

      MI_DIFSUMA := (UN_VALORABONO - MI_SUMAABONODIST);

      IF UN_TI_DABONO(MI_CONCEPTO).VALORANT <>0 THEN
          UN_TI_DABONO(MI_CONCEPTO).VALORANT := (UN_TI_DABONO(MI_CONCEPTO).VALORANT + MI_DIFSUMA);
          UN_TI_DABONO(MI_CONCEPTO).VALOR := (UN_TI_DABONO(MI_CONCEPTO).VALOR + MI_DIFSUMA);
      ELSE
          UN_TI_DABONO(MI_CONCEPTO).VALORACT := (UN_TI_DABONO(MI_CONCEPTO).VALORACT + MI_DIFSUMA);
          UN_TI_DABONO(MI_CONCEPTO).VALOR := (UN_TI_DABONO(MI_CONCEPTO).VALOR + MI_DIFSUMA);
      END IF;

  END PR_CHARLESPESOABIND;

--6
  FUNCTION FC_ABONOPRIORIDADIND
  /*
  NAME              : FC_ABONOPRIORIDADIND En access --> AbonoPrioridad
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
  DATE MIGRADOR     : 08/06/2017
  TIME              : 02:04 PM
  DESCRIPTION       : Procedimiento encargado de crear y distribuir el abono en el type de d_Abonos,
                      Se envia el valor total del abono permitido a distribuir esto por si el UN_USUARIO
                      tiene reclamaciones o solo va a abonar a algun servicio.
  SOURCE MODULE     : SERVICIOS PUBLICOS

  PARAMETERS        : UN_COMPANIA    => Compañia en la que está trabajando
                      UN_CICLO       => Ciclo al que pertenece el usuario
                      UN_CODIGORUTA  => Codigo de ruta del usuario
                      UN_ANO         => Año en el que se encuentra el usuario
                      UN_PERIODO     => Periodo en el que se encuentra el usuurio.
                      UN_CONSECUTIVO => Consecutivo del abono que se está distribuyendo
                      UN_VALORABONO  => Valor total del abono que registra el usuario
                      UN_DBLDEUDA    => Valor de la deuda actual del usuario, aqui se resta los abonos que tiene el usuario.

  @NAME:  obtenerAbonosPrioridad
  @METHOD:  GET
  */
  (
     UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
    ,UN_CODIGORUTA       IN PCK_SUBTIPOS.TI_CODIGORUTA
    ,UN_ANO              IN SP_ABONOS.ANO%TYPE
    ,UN_PERIODO          IN SP_ABONOS.PERIODO%TYPE
    ,UN_CONSECUTIVO      IN SP_ABONOS.CONSECUTIVO%TYPE
    ,UN_VALORABONO       IN PCK_SUBTIPOS.TI_DOBLE
    ,UN_DBLDEUDA         IN PCK_SUBTIPOS.TI_DOBLE
  )
  RETURN TI_ABONO AS

  MI_TABONO               TI_ABONO;
  MI_PARAMETRO            PCK_SUBTIPOS.TI_PARAMETRO;
  MI_SERVICIOSRECLAMO     SP_USUARIO.SERVNORECLAMADO%TYPE DEFAULT ' ';
  MI_RS                   SYS_REFCURSOR;
  MI_VALORACUMULADO       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_VALORACUMULADOPER    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

      MI_PARAMETRO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                UN_NOMBRE    =>  'FACTURA SERVICIOS NO RECLAMADOS',
                                                UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR =>  SYSDATE),'NO');

      IF MI_PARAMETRO = 'SI' THEN
          BEGIN
              SELECT SERVNORECLAMADO
              INTO   MI_SERVICIOSRECLAMO
              FROM   SP_USUARIO
              WHERE  COMPANIA     = UN_COMPANIA
                AND  CICLO        = UN_CICLO
                AND  CODIGORUTA   = UN_CODIGORUTA
                AND  ANO          = UN_ANO
                AND  PERIODO      = UN_PERIODO;

              MI_SERVICIOSRECLAMO := CASE WHEN INSTR(MI_SERVICIOSRECLAMO,',') > 0
                                          THEN PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_SERVICIOSRECLAMO)
                                          ELSE MI_SERVICIOSRECLAMO
                                     END;

          EXCEPTION WHEN NO_DATA_FOUND THEN
              --El usuario --USUARIO-- no tiene facturados o no existe.
              MI_MSGERROR(0).CLAVE := 'USUARIO';
              MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_USUARIO'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGABONOUSUARIONOEXISTE
                                        ,UN_REEMPLAZOS =>  MI_MSGERROR);
          END;
      END IF;

      BEGIN
          <<DISTRABONO>>
          FOR MI_RS IN
          (
              SELECT F.COMPANIA,
                     F.CICLO,
                     F.CODIGORUTA,
                     F.ANO,
                     F.PERIODO,
                     F.CONCEPTO,
                     F.VALOR_FACTURADO,
                     F.DEUDA,
                     F.DEUDA  + F.VALORFINANT-F.VALORABONOANT AS TOTDEUDA,
                     F.VALOR_FACTURADO + F.VALORFINACT-F.VALORABONOACT AS TOTPERIODO,
                     F.VALOR_FACTURADO+F.DEUDA+F.VALORFINACT+F.VALORFINANT-F.VALORABONOACT-F.VALORABONOANT AS TOTFACT,
                     C.ORDENABONAR,
                     C.CODIGO

              FROM (SP_FACTURADO F  INNER JOIN SP_USUARIO U
                      ON F.COMPANIA = U.COMPANIA
                      AND F.CICLO = U.CICLO
                      AND F.CODIGORUTA = U.CODIGORUTA
                      AND F.ANO = U.ANO
                      AND F.PERIODO = U.PERIODO)
                      INNER JOIN SP_CONCEPTOS C
                      ON F.COMPANIA = C.COMPANIA
                      AND F.CONCEPTO = C.CODIGO
              WHERE F.COMPANIA   = UN_COMPANIA
                AND F.CICLO      = UN_CICLO
                AND F.CODIGORUTA = UN_CODIGORUTA
                AND F.ANO        = UN_ANO
                AND (( F.CONCEPTO BETWEEN 1 AND 48 OR F.CONCEPTO BETWEEN 201 AND 220 OR F.CONCEPTO BETWEEN 246 AND 249 )
                AND (F.CONCEPTO) NOT IN (12,17))
                AND (F.VALOR_FACTURADO + F.DEUDA + F.VALORFINACT + F.VALORFINANT -F.VALORABONOACT - F.VALORABONOANT ) <> 0
                AND (CASE WHEN MI_SERVICIOSRECLAMO <> ' ' THEN C.SERVICIO ELSE '1' END )
                      IN
                    (CASE WHEN MI_SERVICIOSRECLAMO <> ' ' THEN
                          MI_SERVICIOSRECLAMO
                      ELSE '1'
                      END )
              ORDER BY C.ORDENABONAR,C.CODIGO
          )
          LOOP
              MI_TABONO(MI_RS.CONCEPTO).COMPANIA := MI_RS.COMPANIA;
              MI_TABONO(MI_RS.CONCEPTO).CICLO := MI_RS.CICLO;
              MI_TABONO(MI_RS.CONCEPTO).CODIGORUTA := MI_RS.CODIGORUTA;
              MI_TABONO(MI_RS.CONCEPTO).ANO := MI_RS.ANO;
              MI_TABONO(MI_RS.CONCEPTO).PERIODO := MI_RS.PERIODO;
              MI_TABONO(MI_RS.CONCEPTO).CONCEPTO := MI_RS.CONCEPTO;
              MI_TABONO(MI_RS.CONCEPTO).VALOR := 0;
              MI_TABONO(MI_RS.CONCEPTO).CONSECUTIVO := UN_CONSECUTIVO ;
              MI_TABONO(MI_RS.CONCEPTO).VALORANT := 0;
              MI_TABONO(MI_RS.CONCEPTO).VALORACT := 0;

              IF MI_RS.TOTDEUDA >= UN_VALORABONO THEN --Si el abono solo alcanza para la deuda.

                    IF(UN_VALORABONO - MI_VALORACUMULADO) < MI_RS.TOTDEUDA THEN
                        MI_TABONO(MI_RS.CONCEPTO).VALORANT := (UN_VALORABONO - MI_VALORACUMULADO);
                        MI_VALORACUMULADO := UN_VALORABONO;
                    ELSE
                        MI_TABONO(MI_RS.CONCEPTO).VALORANT  := MI_RS.TOTDEUDA;
                        MI_VALORACUMULADO := MI_VALORACUMULADO + MI_RS.TOTDEUDA;
                    END IF;

              ELSE

                    IF (UN_VALORABONO - MI_VALORACUMULADO) < MI_RS.TOTDEUDA THEN
                        MI_TABONO(MI_RS.CONCEPTO).VALORANT  := UN_VALORABONO - MI_VALORACUMULADO;
                        MI_VALORACUMULADO := UN_VALORABONO;
                    ELSE
                        MI_TABONO(MI_RS.CONCEPTO).VALORANT  := MI_RS.TOTDEUDA;
                        MI_VALORACUMULADO := MI_VALORACUMULADO + MI_RS.TOTDEUDA;
                    END IF;

                    IF ((UN_VALORABONO - UN_DBLDEUDA) - MI_VALORACUMULADO) > 0 THEN
                        IF (UN_VALORABONO - UN_DBLDEUDA) - MI_VALORACUMULADOPER < MI_RS.TOTPERIODO THEN
                            MI_TABONO(MI_RS.CONCEPTO).VALORACT  := ((UN_VALORABONO - UN_DBLDEUDA) - MI_VALORACUMULADOPER);
                            MI_VALORACUMULADOPER := UN_VALORABONO - UN_DBLDEUDA;
                        ELSE
                            MI_TABONO(MI_RS.CONCEPTO).VALORACT  := MI_RS.TOTPERIODO;
                            MI_VALORACUMULADOPER := MI_VALORACUMULADOPER + MI_RS.TOTPERIODO;
                        END IF;
                    END IF;

              END IF;

          END LOOP DISTRABONO;

          FOR i IN MI_TABONO.FIRST .. MI_TABONO.LAST
          LOOP
              IF MI_TABONO.EXISTS(i) THEN
                  MI_TABONO(i).VALOR := (MI_TABONO(i).VALORACT + MI_TABONO(i).VALORANT);
              END IF;
          END LOOP;

      END;
      RETURN MI_TABONO;
  END FC_ABONOPRIORIDADIND;

--7
  PROCEDURE PR_ACTUALIZARABONOFACTURADO
  (
    /*
    NAME              : PR_ACTUALIZARABONOFACTURADO --> Se pasa de
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 17/05/2017
    TIME              : 03:37 AM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Procedimiento Para restar o sumar los valores de los abonos que se eliminen en la tabla facturado,
                        Se llama desde los trigger BI_SP_D_ABONOS, BD_SP_D_ABONOS

    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_CICLO          => Ciclo del usuario
                        UN_CODIGORUTA     => Codigo de ruta del usuario que se elimina el abono
                        UN_ANO            => Año del cual se va a eliminar el abono.
                        UN_PERIODO        => Periodo del usuario.
                        UN_CONCEPTO       => Concepto que se actualiza en el facturado.
                        UN_ABONOPERIODO   => Valor abonado al periodo actual.
                        UN_ABONODEUDA     => Valor abonado a la deuda del usuario.
                        UN_OPERACION      => Resta cuando se elimina un abono,Suma cuando se ingresa un nuevo abono


    MODIFICATIONS     :

    @NAME:    ActualizaAbonoFacturado
    @METHOD:  POST
    */
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO        IN PCK_SUBTIPOS.TI_CICLO
   ,UN_CODIGORUTA   IN SP_USUARIO.CODIGORUTA%TYPE
   ,UN_ANO          IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_CONCEPTO     IN SP_FACTURADO.CONCEPTO%TYPE
   ,UN_ABONOPERIODO IN SP_FACTURADO.VALORABONOACT%TYPE
   ,UN_ABONODEUDA   IN SP_FACTURADO.VALORABONOACT%TYPE
   ,UN_OPERACION    IN VARCHAR2
   ,UN_USUARIO      IN SP_FACTURADO.CREATED_BY%TYPE
  ) AS

  MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS      PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
        MI_TABLA := 'SP_FACTURADO';
        IF UN_OPERACION = 'RESTA' THEN --Cuando elimina un abono.
            MI_CAMPOS := ' VALORABONOACT = VALORABONOACT - '|| UN_ABONOPERIODO ||',
                           VALORABONOANT = VALORABONOANT - '|| UN_ABONODEUDA ||'   ';
        ELSE  --SUMA Cuando ingresa un nuevo abono.
            MI_CAMPOS := ' VALORABONOACT = VALORABONOACT + '|| UN_ABONOPERIODO ||',
                           VALORABONOANT = VALORABONOANT + '|| UN_ABONODEUDA ||'   ';
        END IF;

        MI_CAMPOS:= MI_CAMPOS || ' ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                   ,DATE_MODIFIED = SYSDATE ';

        MI_CONDICION := 'COMPANIA   = '''|| UN_COMPANIA ||'''
                         AND CICLO      = '|| UN_CICLO ||'
                         AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                         AND ANO        = '|| UN_ANO ||'
                         AND PERIODO    = '''|| UN_PERIODO ||'''
                         AND CONCEPTO = '|| UN_CONCEPTO ||'  ';

        MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                       UN_ACCION    => 'M',
                                       UN_CAMPOS    => MI_CAMPOS,
                                       UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
    MI_MSGERROR(1).CLAVE := 'USUARIO';
    MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
    MI_MSGERROR(2).CLAVE := 'CONCEPTO';
    MI_MSGERROR(2).VALOR :=  UN_CONCEPTO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_ACTABONOFACTURADO,
                               UN_TABLAERROR => MI_TABLA,
                               UN_REEMPLAZOS => MI_MSGERROR);
  END PR_ACTUALIZARABONOFACTURADO;


--8
PROCEDURE PR_REGISTRARRECAUDO(

    /*
    NAME              : PR_REGISTRARRECAUDO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 13/07/2017
    TIME              : 16:15 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Procedimiento que se encarga de insertar o actualizar los recaudos
                        por concepto en la tabla SP_D_RECAUDO

    PARAMETERS        : UN_COMPANIA => Compania a la que pertenece el usuario
                        UN_FECHARECAUDO => Fecha en la que se registro el recaudo
                        UN_BANCO => Banco en el que se registro el recaudo
                        UN_PAQUETE => Paquete al que pertenece el recaudo
                        UN_CONCEPTO => Concepto que se está recaudando
                        UN_TIPO_RECAUDO => Tipo de recaudo, Abono, pago doble, Pago
                        UN_ABONOACT => Valor del abono al periodo actual
                        UN_ABONOANT => Valos del abono a deuda
                        UN_USUARIO => Usuario de la aplicación que registra el recaudo

    MODIFICATIONS     :

    @NAME:    RegistrarRecaudo
    @METHOD:  POST
    */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_FECHARECAUDO     IN DATE
   ,UN_BANCO            IN SP_D_RECAUDO.BANCO%TYPE
   ,UN_PAQUETE          IN SP_D_RECAUDO.NUMEROPAQUETE%TYPE
   ,UN_CONCEPTO         IN SP_D_RECAUDO.CONCEPTO%TYPE
   ,UN_TIPO_RECAUDO     IN VARCHAR
   ,UN_VALORDEUDA       IN SP_D_RECAUDO.VALORDEUDA%TYPE := 0
   ,UN_VALORPERIODO     IN SP_D_RECAUDO.VALORPAGOPERIODO%TYPE := 0
   ,UN_VALORFIN_ACT     IN SP_D_RECAUDO.VALORFINACT%TYPE := 0
   ,UN_VALORFIN_ANT     IN SP_D_RECAUDO.VALORFINANT%TYPE := 0
   ,UN_CREDITOABONADO   IN SP_D_RECAUDO.CREDITOABONADO%TYPE :=0
   ,UN_ABONOACT         IN SP_D_RECAUDO.VALORABONOACT%TYPE := 0
   ,UN_ABONOANT         IN SP_D_RECAUDO.VALORABONOANT%TYPE := 0
   ,UN_USUARIO          IN SP_D_RECAUDO.CREATED_BY%TYPE

) AS

MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;

BEGIN

    IF UN_TIPO_RECAUDO = 'A' THEN  --Registro de Abonos
        BEGIN
            MI_TABLA := 'SP_D_RECAUDO';
            MI_MERGEUSING := 'SELECT 1 FROM DUAL';
            MI_MERGEENLACE := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                                AND TRUNC(FECHA)  = TO_DATE('''|| TO_CHAR(UN_FECHARECAUDO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                AND BANCO         = '''|| UN_BANCO ||'''
                                AND NUMEROPAQUETE = '''|| UN_PAQUETE ||'''
                                AND CONCEPTO      = '''|| UN_CONCEPTO ||'''  ';

            --Suma a los abonos que ya existen.
            MI_MERGEEXISTE := 'UPDATE SET VALORABONOACT = VALORABONOACT + '|| UN_ABONOACT ||'
                                         ,VALORABONOANT = VALORABONOANT + '|| UN_ABONOANT ||'
                                         ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                         ,DATE_MODIFIED = SYSDATE ';

            MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                        FECHA,
                                        BANCO,
                                        NUMEROPAQUETE,
                                        CONCEPTO,
                                        VALORABONOACT,
                                        VALORABONOANT,
                                        CREATED_BY,
                                        DATE_CREATED)
                                VALUES ('''|| UN_COMPANIA ||''',
                                        TO_DATE('''|| TO_CHAR(UN_FECHARECAUDO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ,
                                        '''|| UN_BANCO ||''' ,
                                        '''|| UN_PAQUETE ||''' ,
                                        '|| UN_CONCEPTO ||' ,
                                        '|| UN_ABONOACT ||' ,
                                        '|| UN_ABONOANT ||' ,
                                        '''|| UN_USUARIO ||''' ,
                                        SYSDATE)' ;

            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME
                                (UN_TABLA       => MI_TABLA
                                ,UN_ACCION      => 'IM'
                                ,UN_MERGEUSING  => MI_MERGEUSING
                                ,UN_MERGEENLACE => MI_MERGEENLACE
                                ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                ,UN_MERGENOEXIS => MI_MERGENOEXIS);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(0).CLAVE := 'CONCEPTO';
            MI_MSGERROR(0).VALOR := UN_CONCEPTO;
            MI_MSGERROR(1).CLAVE := 'SUMAVALOR';
            MI_MSGERROR(1).VALOR := (UN_ABONOACT + UN_ABONOANT);
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_REGISTRARABONODRECAUDO,
                                       UN_REEMPLAZOS => MI_MSGERROR);
        END;
    ELSE
        BEGIN
            MI_TABLA := 'SP_D_RECAUDO';
            MI_MERGEUSING :=  'SELECT 1 FROM DUAL';
            MI_MERGEENLACE := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                                AND TRUNC(FECHA)  = TO_DATE('''|| TO_CHAR(UN_FECHARECAUDO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                AND BANCO         = '''|| UN_BANCO ||'''
                                AND NUMEROPAQUETE = '''|| UN_PAQUETE ||'''
                                AND CONCEPTO      = '''|| UN_CONCEPTO ||'''  ';

            MI_MERGEEXISTE := 'UPDATE SET  VALORDEUDA        = VALORDEUDA  + '|| UN_VALORDEUDA ||'
                                          ,VALORPAGOPERIODO  = VALORPAGOPERIODO + '|| UN_VALORPERIODO ||'
                                          ,VALORFINACT       = VALORFINACT + '|| UN_VALORFIN_ACT ||'
                                          ,VALORFINANT       = VALORFINANT + '|| UN_VALORFIN_ANT ||'
                                          ,CREDITOABONADO    = CREDITOABONADO + '|| UN_CREDITOABONADO ||'
                                          ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                          ,DATE_MODIFIED = SYSDATE ';

            MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                        FECHA,
                                        BANCO,
                                        NUMEROPAQUETE,
                                        CONCEPTO,
                                        VALORDEUDA,
                                        VALORPAGOPERIODO,
                                        VALORFINACT,
                                        VALORFINANT,
                                        CREDITOABONADO,
                                        CREATED_BY,
                                        DATE_CREATED)
                                VALUES ('''|| UN_COMPANIA ||''',
                                        TO_DATE(''' || TO_CHAR(UN_FECHARECAUDO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                                        '''|| UN_BANCO ||''' ,
                                        '''|| UN_PAQUETE ||''' ,
                                        '|| UN_CONCEPTO ||' ,
                                        '|| UN_VALORDEUDA ||' ,
                                        '|| UN_VALORPERIODO ||',
                                        '|| UN_VALORFIN_ACT ||',
                                        '|| UN_VALORFIN_ANT ||',
                                        '|| UN_CREDITOABONADO ||',
                                        '''|| UN_USUARIO ||''' ,
                                        SYSDATE )' ;
            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME
                                (UN_TABLA       => MI_TABLA
                                ,UN_ACCION      => 'IM'
                                ,UN_MERGEUSING  => MI_MERGEUSING
                                ,UN_MERGEENLACE => MI_MERGEENLACE
                                ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                ,UN_MERGENOEXIS => MI_MERGENOEXIS);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(0).CLAVE := 'CONCEPTO';
            MI_MSGERROR(0).VALOR := UN_CONCEPTO;
            MI_MSGERROR(1).CLAVE := 'SUMPAGO';
            MI_MSGERROR(1).VALOR := (UN_VALORDEUDA + UN_VALORPERIODO + UN_VALORFIN_ACT + UN_VALORFIN_ANT + UN_CREDITOABONADO);
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_REGPAGODRECAUDO ,
                                       UN_REEMPLAZOS => MI_MSGERROR);
        END;

    END IF;


END PR_REGISTRARRECAUDO;

--9
  PROCEDURE PR_ELIMINARABONO(
     /*
     NAME              : PR_ELIMINARABONO --> Se pasa del formulario frmAbonosSub.
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 05/07/2017
     TIME              : 08:40 AM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Procedimiento al eliminar un abono en la tabla SP_ABONO en el trigger
                         BD_SP_ABONOS

     PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                         UN_CODIGORUTA     => Codigo de ruta del usuario que se elimina el abono
                         UN_CICLO          => Ciclo del usuario
                         UN_ANO            => Año del cual se va a eliminar el abono.
                         UN_PERIODO        => Periodo del cual se va a eliminar el abono.
                         UN_CONSECUTIVOABONO => Consecutivo unico del abono
                         UN_FECHAABONO     => Fecha en la que se creo el abono.
                         UN_BANCOABONO     => Banco en el que se pago el abono
                         UN_PAGOCONVENIOS  => Valor por pago de convenios
                         UN_PAGOTERCERIZADO => Valor por pago de Aseo tercerizado

     MODIFICATIONS     :

     @NAME:    EliminarAbono
     @METHOD:  POST
   */

   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
  ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
  ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
  ,UN_CONSECUTIVOABONO IN SP_ABONOS.CONSECUTIVO%TYPE
  ,UN_FECHAABONO       IN SP_ABONOS.FECHA%TYPE
  ,UN_BANCOABONO       IN SP_ABONOS.BANCO%TYPE
  ,UN_VALORABONO       IN SP_ABONOS.VALOR%TYPE
  ,UN_PAGOCONVENIOS    IN SP_ABONOS.PAGOCONVENIOS%TYPE
  ,UN_PAGOTERCERIZADO  IN SP_ABONOS.PAGOTERCERIZADO%TYPE
  ,UN_USUARIO          IN SP_ABONOS.CREATED_BY%TYPE
  ) AS

  MI_RTA             PCK_SUBTIPOS.TI_ENTERO   DEFAULT 0;
  MI_RTACALCULO      VARCHAR2(300 CHAR);

  MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
  MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE     PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_FILAS           PCK_SUBTIPOS.TI_ENTERO;

  BEGIN
      BEGIN
        MI_TABLA := 'SP_D_ABONOS';
        MI_CONDICION := ' COMPANIA        = '''|| UN_COMPANIA||'''
                          AND CICLO       = '|| UN_CICLO ||'
                          AND CODIGORUTA  = '''|| UN_CODIGORUTA ||'''
                          AND ANO         = '|| UN_ANO ||'
                          AND PERIODO     = '''|| UN_PERIODO ||'''
                          AND CONSECUTIVO = '|| UN_CONSECUTIVOABONO ||' ';
        BEGIN
            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                       ,UN_ACCION     => 'E'
                                       ,UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'USUARIO';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA ;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_BORRARABONODETALLE,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;

      --Funcion para actualizar los convenios
      PCK_SERVICIOS_PUBLICOS_ABONOS.PR_ELIMINACONVENIOS
          ( UN_COMPANIA        => UN_COMPANIA
           ,UN_CICLO           => UN_CICLO
           ,UN_CODIGORUTA      => UN_CODIGORUTA
           ,UN_ANO             => UN_ANO
           ,UN_PERIODO         => UN_PERIODO
           ,UN_FECHAABONO      => UN_FECHAABONO
           ,UN_BANCOABONO      => UN_BANCOABONO
           ,UN_PAGOCONVENIO    => UN_PAGOCONVENIOS
           ,UN_PAGOTERCERIZADO => UN_PAGOTERCERIZADO);


      --Abonos de productividad
      PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD
          ( UN_COMPANIA   => UN_COMPANIA
           ,UN_CICLO      => UN_CICLO
           ,UN_USUARIO    => UN_CODIGORUTA
           ,UN_FECHA      => UN_FECHAABONO
           ,UN_BANCO      => UN_BANCOABONO
           ,UN_PAQUETE    => '888'
           ,UN_OPERACION  => 'ABONO');


      --Actualizo la tabla SP_RECAUDO reverzando el abono en el encabezado.
      BEGIN
          MI_TABLA := 'SP_RECAUDOS';
          MI_CONDICION := '   COMPANIA      = '''|| UN_COMPANIA || '''
                          AND FECHA         = '''|| UN_FECHAABONO ||'''
                          AND BANCO         = '''|| UN_BANCOABONO ||'''
                          AND NUMEROPAQUETE = ''888'' ';

          MI_CAMPOS := 'VALORREPORTADO      = VALORREPORTADO - '|| UN_VALORABONO ||'
                       ,CUPONESREPORTADOS   = CUPONESREPORTADOS - 1
                       ,VALORREGISTRADO     = VALORREGISTRADO - '|| UN_VALORABONO ||'
                       ,CUPONESREGISTRADOS  = CUPONESREGISTRADOS - 1
                       ,MODIFIED_BY         = '''|| UN_USUARIO ||'''
                       ,DATE_MODIFIED       = SYSDATE ';
          BEGIN
                MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'ABONO';
          MI_MSGERROR(2).VALOR :=  UN_VALORABONO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONOCABEZARECAUDO,
                                 UN_TABLAERROR => MI_TABLA,
                                 UN_REEMPLAZOS => MI_MSGERROR);

      END;

      BEGIN
          MI_TABLA := 'SP_D_RECAUDO_USUARIO';
          MI_CONDICION := 'COMPANIA           = '''|| UN_COMPANIA||'''
                           AND CICLO          = '|| UN_CICLO ||'
                           AND CODIGORUTA     = '''|| UN_CODIGORUTA ||'''
                           AND FECHA          = '''|| UN_FECHAABONO ||'''
                           AND BANCO          = '''||UN_BANCOABONO ||'''
                           AND NUMEROPAQUETE  =  ''888''
                           AND CONSECUTIVO    = '|| UN_CONSECUTIVOABONO ||'
                           AND TIPOPAGO       =  ''A'' ';

          BEGIN
             MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                        ,UN_ACCION     => 'E'
                                        ,UN_CONDICION  => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA ;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_BORRARDETALLERECAUDOUSUARI,
                                    UN_TABLAERROR => MI_TABLA,
                                    UN_REEMPLAZOS => MI_MSGERROR);
      END;


  END PR_ELIMINARABONO;

--10  
  PROCEDURE PR_ELIMINARABONODETALLE(
     /*
     NAME              : PR_ELIMINARABONODETALLEDETALLE --> Se pasa del formulario frmAbonosSub.
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 09/05/2017
     TIME              : 08:15 AM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Procedimiento que se ejecuta al eliminar los detalles de un abono en
                         SP_ABONOS, y despues de validarse las condiciones en el trigger BD_SP_D_ABONOS

     PARAMETERS        : UN_COMPANIA         => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                         UN_CODIGORUTA       => Codigo de ruta del usuario que se elimina el abono
                         UN_CICLO            => Ciclo del usuario
                         UN_ANO              => Año del cual se va a eliminar el abono.
                         UN_PERIODO          => Periodo del cual se va a eliminar el abono.
                         UN_FECHAABONO       => Fecha en la que se creo el abono.
                         UN_CONSECUTIVOABONO => Consecutivo unico del abono.
                         UN_CONCEPTO         => Concepto que se está eliminando.

     MODIFICATIONS     :

     @NAME:    EliminarAbonoDetalle
     @METHOD:  POST
   */

   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
  ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
  ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
  ,UN_CONSECUTIVOABONO IN SP_ABONOS.CONSECUTIVO%TYPE
  ,UN_CONCEPTO         IN SP_FACTURADO.CONCEPTO%TYPE
  ,UN_ABONOPERIODO     IN SP_FACTURADO.VALORABONOACT%TYPE
  ,UN_ABONODEUDA       IN SP_FACTURADO.VALORABONOANT%TYPE
  ,UN_USUARIO          IN SP_ABONOS.CREATED_BY%TYPE

  ) AS

  MI_RTA             PCK_SUBTIPOS.TI_ENTERO   DEFAULT 0;
  MI_RTACALCULO      VARCHAR2(300 CHAR);

  MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
  MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE     PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;

  BEGIN
   --Reversa los abonos de la tabla facturado
  BEGIN
      PR_ACTUALIZARABONOFACTURADO
          ( UN_COMPANIA     => UN_COMPANIA
           ,UN_CICLO        => UN_CICLO
           ,UN_CODIGORUTA   => UN_CODIGORUTA
           ,UN_ANO          => UN_ANO
           ,UN_PERIODO      => UN_PERIODO
           ,UN_CONCEPTO     => UN_CONCEPTO
           ,UN_ABONOPERIODO => UN_ABONOPERIODO
           ,UN_ABONODEUDA   => UN_ABONODEUDA
           ,UN_OPERACION    => 'RESTA'
           ,UN_USUARIO      => UN_USUARIO );
  END;



  END PR_ELIMINARABONODETALLE;


--11
  PROCEDURE PR_ELIMINACONVENIOS(
       /*
       NAME              : PR_ELIMINACONVENIOS --> Se pasa del formulario frmAbonosSub.
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 04/07/2017
       TIME              : 08:15 AM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Procedimiento para actualizar los abonos de los convenios cuando un abono se elimina.

       PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_CICLO          => Ciclo del usuario
                           UN_CODIGORUTA     => Codigo de ruta del usuario que se elimina el abono
                           UN_ANO            => Año del cual se va a eliminar el abono.
                           UN_PERIODO        => Periodo del cual se va a eliminar el abono.
                           UN_FECHAABONO     => Fecha en la que se creo el abono.
                           UN_BANCOABONO     => Banco en el que se pago el abono
       MODIFICATIONS     :

       @NAME:    EliminarAbonoConvenio
       @METHOD:  POST
     */

     UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
    ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
    ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
    ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
    ,UN_FECHAABONO       IN SP_ABONOS.FECHA%TYPE
    ,UN_BANCOABONO       IN SP_ABONOS.BANCO%TYPE
    ,UN_PAGOCONVENIO     IN SP_ABONOS.PAGOCONVENIOS%TYPE
    ,UN_PAGOTERCERIZADO  IN SP_ABONOS.PAGOTERCERIZADO%TYPE 

  ) AS
  MI_PARAMETRO    PCK_SUBTIPOS.TI_PARAMETRO;
  MI_RTA          PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                          UN_NOMBRE    =>  'ABONOS PRIORIDAD TERCERIZADO Y CONVENIOS',
                                          UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                          UN_FECHA_PAR =>  SYSDATE),'NO');
    IF MI_PARAMETRO = 'SI' THEN
        IF UN_PAGOCONVENIO > 0 THEN
            MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                        (UN_COMPANIA    => UN_COMPANIA
                        ,UN_CICLO       => UN_CICLO
                        ,UN_CODIGORUTA  => UN_CODIGORUTA
                        ,UN_ANO         => UN_ANO
                        ,UN_PERIODO     => UN_PERIODO
                        ,UN_FECHA       => UN_FECHAABONO
                        ,UN_BANCO       => UN_BANCOABONO
                        ,UN_PAQUETE     => '888'
                        ,UN_REVERSA     => -1
                        ,UN_PAGODOBLE   => 0
                        ,UN_CONVENIO    => MI_PARAMETRO );
        END IF;

        IF UN_PAGOTERCERIZADO > 0 THEN
            MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                        (UN_COMPANIA      => UN_COMPANIA
                        ,UN_CICLO         => UN_CICLO
                        ,UN_CODIGORUTA    => UN_CODIGORUTA
                        ,UN_ANO           => UN_ANO
                        ,UN_PERIODO       => UN_PERIODO
                        ,UN_FECHA         => UN_FECHAABONO
                        ,UN_BANCO         => UN_BANCOABONO
                        ,UN_PAQUETE       => '888'
                        ,UN_REVERSA       => -1
                        ,UN_PAGODOBLE     => 0
                        ,UN_TERCER        => MI_PARAMETRO);
        END IF;
    END IF;

  END PR_ELIMINACONVENIOS;
--12
  PROCEDURE PR_ELIMINARRECAUDO(
     /*
     NAME              : PR_ELIMINARRECAUDO
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 14/07/2017
     TIME              : 10:25 AM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Procedimiento al eliminar un recaudo en la tabla SP_D_RECAUDI_USUARIO
                         en el trigger BD_SP_D_RECAUDO_USUARIO

     PARAMETERS        : UN_COMPANIA => Compania a la que pertenece el usuario
                         UN_FECHARECAUDO => Fecha en la que se registro el recaudo
                         UN_BANCO => Banco en el que se registro el recaudo
                         UN_PAQUETE => Paquete al que pertenece el recaudo
                         UN_CONCEPTO => Concepto que se está recaudando
                         UN_TIPO_RECAUDO => Tipo de recaudo, Abono, pago doble, Pago
                         UN_ABONOACT => Valor del abono al periodo actual
                         UN_ABONOANT => Valos del abono a deuda
                         UN_USUARIO => Usuario de la aplicación que registra el recaudo

     MODIFICATIONS     :

     @NAME:    EliminarRecaudo
     @METHOD:  POST
   */

   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_FECHARECAUDO     IN DATE
  ,UN_BANCO            IN SP_D_RECAUDO.BANCO%TYPE
  ,UN_PAQUETE          IN SP_D_RECAUDO.NUMEROPAQUETE%TYPE
  ,UN_CONCEPTO         IN SP_D_RECAUDO.CONCEPTO%TYPE
  ,UN_TIPO_RECAUDO     IN VARCHAR
  ,UN_VALORDEUDA       IN SP_D_RECAUDO.VALORDEUDA%TYPE := 0
  ,UN_VALORPERIODO     IN SP_D_RECAUDO.VALORPAGOPERIODO%TYPE := 0
  ,UN_VALORFIN_ACT     IN SP_D_RECAUDO.VALORFINACT%TYPE := 0
  ,UN_VALORFIN_ANT     IN SP_D_RECAUDO.VALORFINANT%TYPE := 0
  ,UN_CREDITOABONADO   IN SP_D_RECAUDO.CREDITOABONADO%TYPE :=0
  ,UN_ABONOACT         IN SP_D_RECAUDO.VALORABONOACT%TYPE
  ,UN_ABONOANT         IN SP_D_RECAUDO.VALORABONOANT%TYPE
  ,UN_USUARIO          IN SP_D_RECAUDO.CREATED_BY%TYPE
  ) AS
  MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_FILAS           PCK_SUBTIPOS.TI_ENTERO;

  BEGIN

      IF UN_TIPO_RECAUDO = 'A' THEN  --Reversa de abonos
          BEGIN
              MI_TABLA := 'SP_D_RECAUDO';
              MI_CAMPOS :='VALORABONOACT = VALORABONOACT - '|| UN_ABONOACT ||'
                          ,VALORABONOANT = VALORABONOANT - '|| UN_ABONOANT ||'
                          ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                          ,DATE_MODIFIED = SYSDATE ';

              MI_CONDICION := '   COMPANIA      = '''|| UN_COMPANIA ||'''
                              AND TRUNC(FECHA)  = TO_DATE('''|| TO_CHAR(UN_FECHARECAUDO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                              AND BANCO         = '''|| UN_BANCO ||'''
                              AND NUMEROPAQUETE = '''|| UN_PAQUETE ||'''
                              AND CONCEPTO      = '''|| UN_CONCEPTO ||'''  ';

              BEGIN
                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG
                  (UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONODRECAUDO,
                   UN_TABLAERROR => MI_TABLA);
          END;
      ELSE
          BEGIN
              MI_TABLA := 'SP_D_RECAUDO';
              MI_CAMPOS := ' VALORDEUDA        = VALORDEUDA  - '|| UN_VALORDEUDA ||'
                            ,VALORPAGOPERIODO  = VALORPAGOPERIODO - '|| UN_VALORPERIODO ||'
                            ,VALORFINACT       = VALORFINACT - '|| UN_VALORFIN_ACT ||'
                            ,VALORFINANT       = VALORFINANT - '|| UN_VALORFIN_ANT ||'
                            ,CREDITOABONADO    = CREDITOABONADO - '|| UN_CREDITOABONADO ||'
                            ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                            ,DATE_MODIFIED = SYSDATE ';

              MI_CONDICION := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                                  AND TRUNC(FECHA)  = TO_DATE('''|| TO_CHAR(UN_FECHARECAUDO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                  AND BANCO         = '''|| UN_BANCO ||'''
                                  AND NUMEROPAQUETE = '''|| UN_PAQUETE ||'''
                                  AND CONCEPTO      = '''|| UN_CONCEPTO ||'''  ';
              BEGIN
                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(0).CLAVE := 'CONCEPTO';
              MI_MSGERROR(0).VALOR := UN_CONCEPTO;
              PCK_ERR_MSG.RAISE_WITH_MSG
                  (UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGODRECAUDOUS,
                   UN_TABLAERROR => MI_TABLA,
                   UN_REEMPLAZOS => MI_MSGERROR);

          END;
      END IF;



  END PR_ELIMINARRECAUDO;

--13
  FUNCTION FC_DATOSCODIGOBARRAS(

      /*
      NAME              : FC_DATOSCODIGOBARRAS --> Se pasa del formulario frmAbonosSub.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 30/08/2017
      TIME              : 14:25 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Función que parte el codigo de barras y devuelve su valor, fecha, y datos basicos del usuario.

      PARAMETERS        : UN_COMPANIA => Compañia con la que se está trabajando.
                          UN_CODIGOBARRAS => Codigo de barras regostrado en la tabla SP_PAGO
                          UN_CODIGOINTERNO => Parámetro in out que se envia desde PlSql, Si se envia desde forma se debe enviar vacio.
                          UN_FECHA => Parámetro in out que se envia desde PlSql, Si se envia desde forma se debe enviar vacio.
                          UN_DATORESPUESTA => Dato que se desea retornar,Recibe los parámetros:
                                              C = Retorna el codigo interno del codigo de barras
                                              F = Fecha del codigo de barras
                                              V = Retorna el valor en string que se viene en el codigo de barras.
                                              CR = Retorna el codigo de ruta del suscriptor
                                              AN = Retorna el año en el que está el usaurio
                                              PE = Retorna el periodo en el que está el usuario
                                              CC = Retorna el ciclo del usuario.


      MODIFICATIONS     :

      @NAME:    ObtenerValorCodigoBarras
      @METHOD:  GET
      */
       UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA
      ,UN_CODIGOBARRAS      IN SP_PAGO.CODIGOBARRAS%TYPE
      ,UN_CODIGOINTERNO     IN OUT SP_USUARIO.CODIGOINTERNO%TYPE
      ,UN_CODIGORUTA        IN OUT SP_USUARIO.CODIGORUTA%TYPE
      ,UN_ANOUSUARIO        IN OUT SP_USUARIO.ANO%TYPE
      ,UN_PERIODOUSUARIO    IN OUT SP_USUARIO.PERIODO%TYPE
      ,UN_CICLOUSUARIO      IN OUT SP_USUARIO.CICLO%TYPE
      ,UN_FECHA             IN OUT VARCHAR2
      ,UN_DATORESPUESTA     IN VARCHAR2 := 'V'


  ) RETURN VARCHAR2 AS

   MI_VALOR        SP_PAGO.VALORPAGO%TYPE DEFAULT 0;
   MI_RTA          VARCHAR2(40 CHAR) DEFAULT ' ';
   MI_PAR          BOOLEAN;


   MI_POSINICIAL   PCK_SUBTIPOS.TI_ENTERO;
   MI_POSFINAL     PCK_SUBTIPOS.TI_ENTERO;
   MI_POSVLRINI    PCK_SUBTIPOS.TI_ENTERO;
   MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
      BEGIN
          UN_CODIGOINTERNO := ' ';
          UN_FECHA := NULL;

          IF LENGTH(UN_CODIGOBARRAS) > 20 THEN
              MI_POSINICIAL := INSTR(UN_CODIGOBARRAS,'8020') + 4;
              MI_POSFINAL := INSTR(UN_CODIGOBARRAS, '3900');
              IF MI_POSFINAL = 0 THEN
                  MI_POSFINAL := LENGTH(UN_CODIGOBARRAS) - 1;
              END IF;

              MI_POSVLRINI := MI_POSFINAL + 4;
              IF MI_POSVLRINI < (LENGTH(UN_CODIGOBARRAS) - 1) THEN --Valor total de la factura
                  MI_VALOR := TO_NUMBER(SUBSTR(UN_CODIGOBARRAS, mi_POSVLRINI, 10));
              END IF;

              BEGIN
                  IF LENGTH(SUBSTR(UN_CODIGOBARRAS, MI_POSINICIAL, MI_POSFINAL - MI_POSINICIAL)) > 7 THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END IF;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  --La referencia de pago no pertenece a una factura valida.
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_PAGO'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_CODIGOBARRAS);
              END;

              UN_CODIGOINTERNO := TO_CHAR(SUBSTR(UN_CODIGOBARRAS, MI_POSINICIAL, MI_POSFINAL - MI_POSINICIAL));
              BEGIN
                  BEGIN
                      SELECT CODIGOINTERNO,CODIGORUTA,ANO,PERIODO,CICLO
                      INTO   UN_CODIGOINTERNO,UN_CODIGORUTA,UN_ANOUSUARIO,UN_PERIODOUSUARIO,UN_CICLOUSUARIO
                      FROM   SP_USUARIO
                      WHERE  COMPANIA = UN_COMPANIA
                        AND  CODIGOINTERNO = UN_CODIGOINTERNO;
                  EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'CODIGOINTERNO';
                  MI_MSGERROR(0).VALOR := UN_CODIGOINTERNO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_USUARIO'
                                            ,UN_REEMPLAZOS =>  MI_MSGERROR
                                            ,UN_ERROR_COD  => CASE WHEN NVL(UN_CODIGORUTA,' ') =' ' THEN PCK_ERRORES.ERR_PAGO_CODIGOBARRASNOUSUARIO ELSE PCK_ERRORES.ERR_PAGO_CODIGOINTERNOMASDEUNO END  );
              END;

          ELSE
              MI_PAR :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                      (UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'RECAUDO CON CODIGO DE RUTA',
                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                       UN_FECHA_PAR => SYSDATE) = 'SI'
                         THEN TRUE ELSE FALSE END;

              IF NOT  MI_PAR THEN
                  BEGIN
                      IF LENGTH(UN_CODIGOBARRAS) > 7  THEN
                         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      ELSE
                         UN_CODIGOINTERNO := SUBSTR(UN_CODIGOBARRAS,1,7);
                      END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                               ,UN_TABLAERROR => 'SP_PAGO'
                                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_ERRORCODIGOBARRAS);
                 END;
              END IF;

              BEGIN
                  BEGIN
                      IF MI_PAR THEN
                        SELECT CODIGOINTERNO,CODIGORUTA,ANO,PERIODO,CICLO
                        INTO   UN_CODIGOINTERNO,UN_CODIGORUTA,UN_ANOUSUARIO,UN_PERIODOUSUARIO,UN_CICLOUSUARIO
                        FROM   SP_USUARIO
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  CODIGORUTA = UN_CODIGOBARRAS;
                      ELSE
                        SELECT CODIGOINTERNO,CODIGORUTA,ANO,PERIODO,CICLO
                        INTO   UN_CODIGOINTERNO,UN_CODIGORUTA,UN_ANOUSUARIO,UN_PERIODOUSUARIO,UN_CICLOUSUARIO
                        FROM   SP_USUARIO
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  CODIGOINTERNO = UN_CODIGOINTERNO;
                      END IF;    

                  EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'CODIGOINTERNO';
                  MI_MSGERROR(0).VALOR := UN_CODIGOINTERNO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_USUARIO'
                                            ,UN_REEMPLAZOS =>  MI_MSGERROR
                                            ,UN_ERROR_COD  => CASE WHEN NVL(UN_CODIGORUTA,' ') =' ' THEN PCK_ERRORES.ERR_PAGO_CODIGOBARRASNOUSUARIO ELSE PCK_ERRORES.ERR_PAGO_CODIGOINTERNOMASDEUNO END  );
              END;

          END IF;

          IF SUBSTR(UN_CODIGOBARRAS, 1, 2) = '96' THEN --PORQUE TIENE CONTROL DE FECHAS
              UN_FECHA := SUBSTR(UN_CODIGOBARRAS, 10); -- LA FECHA EN FORMATO AAAAMMDD
          END IF;

      END;

      IF UN_DATORESPUESTA = 'V' THEN  --Valor del pago
          MI_RTA := TO_CHAR(MI_VALOR);
      ELSIF UN_DATORESPUESTA = 'F' THEN -- Fecha del codigo de barras en formato AAAAMMDD
          MI_RTA := UN_FECHA;
      ELSIF UN_DATORESPUESTA = 'C' THEN --Codigo interno.
          MI_RTA := UN_CODIGOINTERNO;
      ELSIF UN_DATORESPUESTA = 'CR' THEN  --Codigo de ruta.
          MI_RTA := UN_CODIGORUTA;
      ELSIF UN_DATORESPUESTA ='AN' THEN   --Año del usuario
          MI_RTA := TO_CHAR(UN_ANOUSUARIO);
      ELSIF UN_DATORESPUESTA ='PE' THEN   --Periodo del usaurio
          MI_RTA := UN_PERIODOUSUARIO;
      ELSIF UN_DATORESPUESTA = 'CC' THEN  --Ciclo del usuario
          MI_RTA:= TO_CHAR(UN_CICLOUSUARIO);
      END IF;

      RETURN MI_RTA;
  END FC_DATOSCODIGOBARRAS;

--14
FUNCTION FC_OPERACIONPAGO(
      /*
      NAME              : FC_OPERACIONPAGO --> Se pasa del formulario en Access sub_pago
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 29/08/2017
      TIME              : 10:37 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Funcion que retorna el tipo de operación que se va a registrar en un pago.
                          Retorna los siguientes valores:
                          1 = Primer Pago. No pregunta nada.
                          2 = Segundo Pago, En forma debe preguntar solo si se envia MI_FECHABARRAS: Según el código de barras, este pago es atrasado. Desea continuar?.
                          D = Pago Doble. En forma preguntar Si pertenece menu cajero no debe dejarlo registrar si no debe preguntar: Desea registrarlo como pago doble? Si desea registrarlo, este quedara como un saldo credito a favor del usuario.
                          A = Abono, En forma pregunta: Este usuario tiene un abono autorizado, aún desea registrar el abono".

      PARAMETERS        : UN_COMPANIA => Compañia del usuario que realiza el pago.
                          UN_FECHAPAGO => Fecha en la cual se realiza el pago.
                          UN_CODIGOINTERNO => Codigo interno del usuario va a pagar.
                          MI_FECHABARRAS => Si el pago se registra con codigo de barras se envia la fecha en formato aaaammdd.

      MODIFICATIONS     :

      @NAME:    ObtenerOperacionPago
      @METHOD:  GET
      */
 UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
,UN_FECHAPAGO           IN DATE
,UN_CODIGOINTERNO       IN SP_PAGO.CODIGOINTERNO%TYPE
,UN_CODIGOBARRAS        IN SP_PAGO.CODIGOBARRAS%TYPE
) RETURN VARCHAR2 AS

 MI_PAR                 BOOLEAN;
 MI_PARFECHAUSUARIO     BOOLEAN;
 MI_VALORABONO          SP_ABONOS.VALOR%TYPE DEFAULT 0;
 MI_OPERACION           SP_PAGO.OPERACION%TYPE;

 MI_CODIGORUTA          SP_USUARIO.CODIGORUTA%TYPE DEFAULT ' ';
 MI_ANOPAGO             SP_USUARIO.ANO%TYPE;
 MI_PERIODOPAGO         SP_USUARIO.PERIODO%TYPE;
 MI_CICLO               SP_USUARIO.CICLO%TYPE;
 MI_FECHALIMITE         DATE;
 MI_FECHALIMITE2        DATE;
 MI_PERIODOSATRASO      SP_USUARIO.PERIODOSATRASO%TYPE;
 MI_FECHAPAGO1          DATE;
 MI_BANCOPERPROCESO     SP_USUARIO.BANCOPERPROCESO%TYPE;

 MI_VALORBARRAS         SP_PAGO.VALORPAGO%TYPE;
 MI_FECHABARRAS         VARCHAR2(15 CHAR);
 MI_CODINTERNOBARRAS    SP_USUARIO.CODIGOINTERNO%TYPE;


 MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN
    BEGIN

        MI_VALORBARRAS := TO_NUMBER(PCK_SERVICIOS_PUBLICOS_ABONOS.FC_DATOSCODIGOBARRAS
            (UN_COMPANIA       => UN_COMPANIA
            ,UN_CODIGOBARRAS   => UN_CODIGOBARRAS
            ,UN_CODIGOINTERNO  => MI_CODINTERNOBARRAS
            ,UN_CODIGORUTA     => MI_CODIGORUTA
            ,UN_ANOUSUARIO     => MI_ANOPAGO
            ,UN_PERIODOUSUARIO => MI_PERIODOPAGO
            ,UN_CICLOUSUARIO   => MI_CICLO
            ,UN_FECHA          => MI_FECHABARRAS
            ,UN_DATORESPUESTA  => 'V')
            );

        BEGIN
            SELECT CODIGORUTA,CICLO,FECHALIMITE,FECHALIMITE2,
                   PERIODOSATRASO,BANCOPERPROCESO
            INTO   MI_CODIGORUTA,MI_CICLO,MI_FECHALIMITE,MI_FECHALIMITE2,
                   MI_PERIODOSATRASO,MI_BANCOPERPROCESO
            FROM   SP_USUARIO
            WHERE  COMPANIA = UN_COMPANIA
              AND  CODIGOINTERNO = UN_CODIGOINTERNO;
        EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN --El código interno No existe, no se permite realizar el pago.
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(0).CLAVE := 'CODINTERNO';
        MI_MSGERROR(0).VALOR := UN_CODIGOINTERNO;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE
                                   ,UN_ERROR_COD => CASE WHEN MI_CODIGORUTA = ' ' THEN PCK_ERRORES.ERR_PAGO_NOEXISTEUSUARIO ELSE PCK_ERRORES.ERR_PAGO_CODIGOINTERNOMASDEUNO END
                                   ,UN_TABLAERROR => 'SP_USUARIO'
                                   ,UN_REEMPLAZOS =>  MI_MSGERROR);
    END;


    MI_PAR :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                        (UN_COMPANIA  => UN_COMPANIA,
                         UN_NOMBRE    => 'PERMITE AUTORIZACION DE ABONOS',
                         UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                         UN_FECHA_PAR => SYSDATE) = 'SI'
               THEN TRUE ELSE FALSE END;

    MI_PARFECHAUSUARIO := CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                            (UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'PERMITE DISTINTAS FECHAS LIMITE PARA UN CICLO',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI'
                          THEN TRUE ELSE FALSE END;

    IF MI_PAR THEN
        BEGIN
            BEGIN
                BEGIN
                    SELECT VALOR
                    INTO   MI_VALORABONO
                    FROM   SP_ABONOS
                    WHERE  COMPANIA = UN_COMPANIA
                      AND  CICLO = MI_CICLO
                      AND  CODIGORUTA = MI_CODIGORUTA
                      AND  INDAUTORIZADO <> 0
                      AND  BANCO IS NULL;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_VALORABONO := 0;
                END;
            EXCEPTION WHEN TOO_MANY_ROWS THEN --Solo puede existir una autorización de abonos
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_PAGO_AUTORIZAABONOMASDEUNO,
                                       UN_TABLAERROR => 'SP_ABONOS');
        END;

        IF MI_VALORABONO > 0 THEN
            --Se debe preguntar en forma si se desea registrar el abono
            MI_OPERACION := 'A'; --Abono
        END IF;
    END IF;

    IF MI_OPERACION <> 'A' THEN
        IF MI_PARFECHAUSUARIO THEN
            IF MI_FECHALIMITE2 IS NOT NULL THEN
                IF UN_FECHAPAGO <= MI_FECHALIMITE2 THEN
                    MI_OPERACION := '1'; --Primer pago.
                ELSE
                    IF MI_PERIODOSATRASO = 1 THEN
                        MI_OPERACION := '2';
                    ELSE
                        MI_OPERACION := '1';
                    END IF;
                END IF;
            ELSIF MI_FECHALIMITE IS NOT NULL THEN
                IF MI_FECHALIMITE < UN_FECHAPAGO THEN
              IF MI_PERIODOSATRASO = 1 THEN
                MI_OPERACION := '2';
              ELSE
                MI_OPERACION := '1';
              END IF;
             END IF;
            END IF;

            IF NVL(MI_FECHABARRAS, ' ') <> ' ' THEN
            IF MI_FECHALIMITE2 IS NOT NULL THEN
              IF TO_NUMBER(TO_CHAR(MI_FECHALIMITE2, 'YYYYMMDD')) > TO_NUMBER(MI_FECHABARRAS) THEN
                  MI_OPERACION := '2';
              END IF;
            END IF;
          END IF;
        ELSE
            BEGIN
                BEGIN
                    SELECT FECHAPAGO1
                    INTO   MI_FECHAPAGO1
                    FROM   SP_CICLO
                    WHERE  COMPANIA = UN_COMPANIA
                      AND  NUMERO   = MI_CICLO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CODIGORUTA := ' ';
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE
                                           ,UN_ERROR_COD => PCK_ERRORES.ERR_SP_NDF_PRAF_VERIFICARCICLO
                                           ,UN_TABLAERROR => 'SP_CICLO');
             END;

            IF MI_FECHAPAGO1 IS NOT NULL THEN
                IF UN_FECHAPAGO <= MI_FECHAPAGO1 THEN
                  MI_OPERACION := '1';
                ELSE
                  MI_OPERACION := '2';
                END IF;
            ELSIF MI_FECHALIMITE IS NOT NULL THEN
                IF MI_FECHALIMITE < UN_FECHAPAGO THEN
                  MI_OPERACION := '2';
                END IF;
            END IF;

            IF NVL(MI_FECHABARRAS, ' ') <> ' ' THEN
                IF MI_FECHAPAGO1 IS NOT NULL THEN
                    IF TO_NUMBER(TO_CHAR(MI_FECHAPAGO1, 'YYYYMMDD')) > TO_NUMBER(MI_FECHABARRAS) THEN
                        MI_OPERACION := '2';
                    END IF;
                END IF;
            END IF;


        END IF;  --Fin parámetro

        IF NVL(MI_BANCOPERPROCESO,' ') <> ' ' THEN --Pago doble
            MI_OPERACION := 'D';
        END IF;
    END IF;

    RETURN MI_OPERACION;


END FC_OPERACIONPAGO;


--15
  FUNCTION FC_VALIDARPAGO(
        /*
        NAME              : FC_VALIDARPAGO --> Se pasa del formulario en Access sub_pago
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 28/08/2017
        TIME              : 10:37 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Procedimiento que valida si un pago se puede realizar.
                            el trigger BI_SP_PAGO, Retorna -1 si se puede realizar el pago,0 si no se puede realizar.

        PARAMETERS        :

        MODIFICATIONS     :

        @NAME:    ValidarPago
        @METHOD:  POST
        */
   UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_FECHAPAGO           IN DATE
  ,UN_CODIGOINTERNO       IN SP_PAGO.CODIGOINTERNO%TYPE
  ,UN_CODIGOBARRAS        IN SP_PAGO.CODIGOBARRAS%TYPE
  ,UN_OPERACION           IN SP_PAGO.OPERACION%TYPE
  ,UN_VALORTER            IN SP_PAGO.PAGOTERCERIZADO%TYPE
  ,UN_VALORCONVE          IN SP_PAGO.PAGOCONVENIOS%TYPE

  ) RETURN NUMBER AS

   MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_FIMM                SP_USUARIO.FIMM%TYPE DEFAULT ' ';
   MI_PARSITIO            BOOLEAN;
   MI_FECHAPREPARACION    DATE;
   MI_CICLO               SP_CICLO.NUMERO%TYPE;
   MI_CODIGORUTA          SP_USUARIO.CODIGORUTA%TYPE;
   MI_ANO                 SP_CICLO.ANO%TYPE;
   MI_PERIODO             SP_CICLO.PERIODO%TYPE;
   MI_TOTFACTURAPERACT    SP_USUARIO.TOTFACTURAPERACTUAL%TYPE;
   MI_SUMFACTURADO        SP_FACTURADO.VALOR_FACTURADO%TYPE;

   MI_VALORBARRAS         SP_PAGO.VALORPAGO%TYPE;
   MI_FECHABARRAS         VARCHAR2(15 CHAR);
   MI_CODINTERNOBARRAS    SP_USUARIO.CODIGOINTERNO%TYPE;

  BEGIN

      MI_PARSITIO :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                          (UN_COMPANIA  => UN_COMPANIA,
                           UN_NOMBRE    => 'FACTURACION EN SITIO',
                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                           UN_FECHA_PAR => SYSDATE) = 'SI'
                      THEN TRUE ELSE FALSE END;





      --Obtiene el valor que se viene en el codigo de barras
      MI_VALORBARRAS := TO_NUMBER(PCK_SERVICIOS_PUBLICOS_ABONOS.FC_DATOSCODIGOBARRAS
          (UN_COMPANIA       => UN_COMPANIA
          ,UN_CODIGOBARRAS   => UN_CODIGOBARRAS
          ,UN_CODIGOINTERNO  => MI_CODINTERNOBARRAS
          ,UN_CODIGORUTA     => MI_CODIGORUTA
          ,UN_ANOUSUARIO     => MI_ANO
          ,UN_PERIODOUSUARIO => MI_PERIODO
          ,UN_CICLOUSUARIO   => MI_CICLO
          ,UN_FECHA          => MI_FECHABARRAS
          ,UN_DATORESPUESTA  => 'V'));

      BEGIN
          BEGIN
              SELECT U.FIMM, C.FECHA_PREPARACION,C.NUMERO,U.CODIGORUTA,  C.ANO, C.PERIODO,U.TOTFACTURAPERACTUAL
              INTO   MI_FIMM,MI_FECHAPREPARACION,MI_CICLO,MI_CODIGORUTA,MI_ANO,MI_PERIODO,MI_TOTFACTURAPERACT
              FROM   SP_USUARIO U INNER JOIN SP_CICLO C ON U.COMPANIA = C.COMPANIA AND U.CICLO = C.NUMERO
                              AND U.ANO = C.ANO AND U.PERIODO = C.PERIODO
              WHERE  U.COMPANIA = UN_COMPANIA
                AND  U.CODIGOINTERNO = UN_CODIGOINTERNO;

          EXCEPTION WHEN NO_DATA_FOUND THEN
              --El código interno –CODIGOINTERNO— No existe, no se permite realizar el pago.
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODINTERNO';
          MI_MSGERROR(1).VALOR := UN_CODIGOINTERNO;
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                     ,UN_TABLAERROR => 'SP_PAGO'
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_NOEXISTEUSUARIO
                                     ,UN_REEMPLAZOS =>  MI_MSGERROR);
          RETURN 0;
      END;
      --Validaciones generales
      BEGIN
          IF MI_PARSITIO AND NVL(MI_FIMM, ' ') = 'P' THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODIGOINTERNO';
          MI_MSGERROR(1).VALOR := UN_CODIGOINTERNO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_PAGO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_USUARIOTERRENO
                                    ,UN_REEMPLAZOS =>  MI_MSGERROR);
          RETURN 0;
      END;

      BEGIN
          IF MI_PARSITIO AND NVL(MI_FIMM, ' ') = ' '  THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --El usuario se preparó pero no ha sido calculado. No se permite registrar pago
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_PAGO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_USUARIONOFIMM);
          RETURN 0;
      END;

      BEGIN
          IF MI_FECHAPREPARACION IS NULL THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --La fecha de preparación del ciclo se encuentra nula. Por favor informar al administrador del sistema.
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_PAGO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_SINFECHAPREPARACION);
          RETURN 0;
      END;

      BEGIN
          IF UN_FECHAPAGO < MI_FECHAPREPARACION THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --La fecha de pago es inferior a la fecha de preparación del ciclo al cual corresponde el usuario
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_PAGO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_FECHAPREPARACIONMENOR);
          RETURN 0;
      END;



      BEGIN
          IF (MI_VALORBARRAS <> (MI_TOTFACTURAPERACT + UN_VALORTER + UN_VALORCONVE)) AND MI_VALORBARRAS <> 0 THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --El valor del código de barras no coincide con el valor actual del sistema. Por favor verifique
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_PAGO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_DIFBARRASTOTFACTURA);
          RETURN 0;
      END;

      IF UN_OPERACION	<> 'D' THEN
          BEGIN
              SELECT SUM(VALOR_FACTURADO + DEUDA) AS SUMAFAC
              INTO   MI_SUMFACTURADO
              FROM   SP_FACTURADO
              WHERE  COMPANIA   = UN_COMPANIA
                AND  CICLO      = MI_CICLO
                AND  CODIGORUTA = MI_CODIGORUTA
                AND  ANO        = MI_ANO
                AND  PERIODO    = MI_PERIODO
                AND  (CONCEPTO BETWEEN 0 AND 50 OR CONCEPTO BETWEEN 201 AND 220 OR CONCEPTO BETWEEN 246 AND 249 );
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_SUMFACTURADO := 0;
          END;

          BEGIN
              IF MI_TOTFACTURAPERACT <> MI_SUMFACTURADO THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El total de la factura no coincide con los valores facturados en el sistema, Verifique los valores con el encargado de facturación, No se permite registrar pago
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => 'SP_PAGO'
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_DIFVALORESFACTUSUARIO);
              RETURN 0;
          END;
      END IF;



      RETURN -1;

  END FC_VALIDARPAGO;

--16
  FUNCTION FC_VALORAPAGAR(

      /*
      NAME              : FC_VALORAPAGAR --> Se pasa del formulario frmAbonosSub.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 31/08/2017
      TIME              : 11:29 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Función que en base a la operación del pago determina el valor que se tiene que pagar.

      PARAMETERS        : UN_COMPANIA => Compañia con la que se está trabajando.
                          UN_CICLO => Ciclo del usuario que va a pagar.
                          UN_CODIGORUTA => Codigo de ruta del usuario que va a pagar.
                          UN_CONSECUTIVOABONO => Parámetro de entrada y salida que se utiliza en el trigger para registrar el abono.
                          UN_OPERACION => Operación o tipo de pago que el usaurio presenta recibe los siguientes valores:
                          1 = Primer Pago.
                          2 = Segundo Pago.
                          D = Pago Doble.
                          A = Abono.
      MODIFICATIONS     :

      @NAME:    ObtenerValoraPagar
      @METHOD:  GET
      */
       UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA
      ,UN_CICLO             IN SP_PAGO.CICLO%TYPE
      ,UN_CODIGORUTA        IN SP_USUARIO.CODIGOINTERNO%TYPE
      ,UN_OPERACION         IN SP_PAGO.OPERACION%TYPE
      ,UN_CONSECUTIVOABONO  IN OUT SP_ABONOS.CONSECUTIVO%TYPE

  ) RETURN NUMBER AS

   MI_VALOR               SP_PAGO.VALORPAGO%TYPE DEFAULT 0;
   MI_VALORABONO          SP_ABONOS.VALOR%TYPE DEFAULT 0;

   MI_TOTFACTURAPERACTUAL SP_USUARIO.TOTFACTURAPERACTUAL%TYPE DEFAULT 0;
   MI_TOTFACTURAPAGO2     SP_USUARIO.TOTFACTURAPAGO2%TYPE DEFAULT 0;
   MI_PERIODOSATRASO      SP_USUARIO.PERIODOSATRASO%TYPE DEFAULT 0;

   MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_PARRECARGOSIGFEC    BOOLEAN;
   MI_PARFECHAUSUARIO     BOOLEAN;
   MI_CONSECUTIVO         SP_ABONOS.CONSECUTIVO%TYPE DEFAULT 0;

  BEGIN

      IF UN_OPERACION ='A' THEN   --Autorización de abonos;
          BEGIN
              BEGIN
                  SELECT VALOR,CONSECUTIVO
                  INTO   MI_VALORABONO,MI_CONSECUTIVO
                  FROM   SP_ABONOS
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  CICLO = UN_CICLO
                    AND  CODIGORUTA = UN_CODIGORUTA
                    AND  INDAUTORIZADO <> 0
                    AND  BANCO IS NULL;
              EXCEPTION WHEN TOO_MANY_ROWS THEN --Solo puede existir una autorización de abonos
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_PAGO_AUTORIZAABONOMASDEUNO,
                                         UN_TABLAERROR => 'SP_ABONOS');
          END;
          UN_CONSECUTIVOABONO := MI_CONSECUTIVO;
          MI_VALOR := MI_VALORABONO;
      ELSE
          BEGIN
              BEGIN
                  SELECT TOTFACTURAPERACTUAL,TOTFACTURAPAGO2,PERIODOSATRASO
                  INTO   MI_TOTFACTURAPERACTUAL,MI_TOTFACTURAPAGO2,MI_PERIODOSATRASO
                  FROM   SP_USUARIO
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  CICLO  = UN_CICLO
                    AND  CODIGORUTA = UN_CODIGORUTA;
              EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(0).CLAVE := 'CODINTERNO';
              MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
              PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_NOEXISTEUSUARIO
                                         ,UN_TABLAERROR => 'SP_USUARIO'
                                         ,UN_REEMPLAZOS => MI_MSGERROR);
          END;

          MI_PARRECARGOSIGFEC :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                      (UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                       UN_FECHA_PAR => SYSDATE) = 'SI'
                                  THEN TRUE ELSE FALSE END;

          MI_PARFECHAUSUARIO :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                      (UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'PERMITE DISTINTAS FECHAS LIMITE PARA UN CICLO',
                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                       UN_FECHA_PAR => SYSDATE) = 'SI'
                                  THEN TRUE ELSE FALSE END;

          IF UN_OPERACION = '1' THEN
              MI_VALOR := MI_TOTFACTURAPERACTUAL;
          ELSIF UN_OPERACION =  '2' THEN
              IF MI_PARRECARGOSIGFEC THEN
                  MI_VALOR := MI_TOTFACTURAPERACTUAL;
              ELSE
                  IF MI_PARFECHAUSUARIO THEN
                      IF MI_PERIODOSATRASO = 1 THEN
                          MI_VALOR := MI_TOTFACTURAPAGO2;
                      ELSE
                          MI_VALOR := MI_TOTFACTURAPERACTUAL;
                      END IF;
                  ELSE
                      MI_VALOR := MI_TOTFACTURAPAGO2;
                  END IF;
              END IF;
          ELSIF UN_OPERACION = 'D' THEN
              IF MI_PARRECARGOSIGFEC THEN
                  MI_VALOR := MI_TOTFACTURAPERACTUAL;
              ELSE
                  IF MI_PARFECHAUSUARIO  THEN
                  IF MI_PERIODOSATRASO = 1 THEN
                    MI_VALOR := MI_TOTFACTURAPAGO2;
                  ELSE
                    MI_VALOR := MI_TOTFACTURAPERACTUAL;
                  END IF;
                ELSE
                  MI_VALOR := MI_TOTFACTURAPAGO2;
                END IF;
              END IF;
          END IF;
      END IF;

      UN_CONSECUTIVOABONO := MI_CONSECUTIVO;
      RETURN MI_VALOR;
  END FC_VALORAPAGAR;

--17
  PROCEDURE PR_REGISTRARPAGO(

      /*
      NAME              : PR_REGISTRARPAGO --> Se pasa del formulario ActualizacionPagos.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 28/08/2017
      TIME              : 15:11 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento que se ejecuta al insertar un pago en la tabla SP_PAGO.

      PARAMETERS        :

      MODIFICATIONS     :

      @NAME:    RegistrarPago
      @METHOD:  POST
      */
      UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
     ,UN_FECHAPAGO           IN DATE
     ,UN_BANCOPAGO           IN SP_PAGO.BANCO%TYPE
     ,UN_NUMEROPAQUETEPAGO   IN SP_PAGO.NUMEROPAQUETE%TYPE
     ,UN_CONSECUTIVO         IN SP_PAGO.CONSECUTIVO%TYPE
     ,UN_CODIGOINTERNO       IN SP_PAGO.CODIGOINTERNO%TYPE
     ,UN_CODIGORUTA          IN SP_PAGO.CODIGORUTA%TYPE
     ,UN_CICLO               IN SP_PAGO.CICLO%TYPE
     ,UN_ANO                 IN SP_PAGO.ANO%TYPE
     ,UN_PERIODO             IN SP_PAGO.PERIODO%TYPE
     ,UN_VALORPAGO           IN SP_PAGO.VALORPAGO%TYPE
     ,UN_OPERACION           IN SP_PAGO.OPERACION%TYPE
     ,UN_CONSECUTIVOABONO    IN SP_ABONOS.CONSECUTIVO%TYPE := 0
     ,UN_USUARIO             IN SP_PAGO.CREATED_BY%TYPE
  ) AS

  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TOTFACTURAPERACTUAL  SP_USUARIO.TOTFACTURAPERACTUAL%TYPE DEFAULT 0;
  MI_TOTFACTURAPAGO2      SP_USUARIO.TOTFACTURAPAGO2%TYPE DEFAULT 0;
  MI_SEGFECHAPERSIG       BOOLEAN;
  MI_DBLCREDITO           SP_PAGO.VALORPAGO%TYPE DEFAULT 0;
  MI_CODSALDOCREDITO      SP_TBLHIST_SALDO_CREDITO.CODSALDOCREDITO%TYPE;
  MI_CONSECUTIVOCREDITO   SP_TBLHIST_SALDO_CREDITO.CONSECUTIVO%TYPE;
  MI_NIT                  COMPANIA.NITCOMPANIA%TYPE;

  BEGIN
      BEGIN

          BEGIN
              SELECT  NITCOMPANIA
              INTO    MI_NIT
              FROM    COMPANIA
              WHERE   CODIGO = UN_COMPANIA;
          END;

          IF UN_OPERACION = 'A' THEN  --Pago de autorización de abonos
              BEGIN
                  MI_TABLA     := ' SP_ABONOS';
                  MI_CAMPOS    := ' FECHA         =  TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                    ,BANCO         = '''|| UN_BANCOPAGO ||'''
                                    ,MODIFIED_BY   = '''|| UN_USUARIO || '''
                                    ,DATE_MODIFIED = SYSDATE ';

                  MI_CONDICIONACME := '        COMPANIA    = '''|| UN_COMPANIA || '''
                                           AND CICLO       = '|| UN_CICLO ||'
                                           AND CODIGORUTA  = '''|| UN_CODIGORUTA ||'''
                                           AND ANO         = '|| UN_ANO ||'
                                           AND PERIODO     = '''|| UN_PERIODO ||'''
                                           AND CONSECUTIVO = '|| UN_CONSECUTIVOABONO ||'  ';
                  BEGIN
                      --Lanza l trigger BU_SP_ABONOS
                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'FECHA';
                  MI_MSGERROR(0).VALOR := UN_FECHAPAGO;
                  MI_MSGERROR(1).CLAVE := 'BANCO';
                  MI_MSGERROR(1).VALOR := UN_BANCOPAGO;
                  MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                  MI_MSGERROR(2).VALOR := UN_CONSECUTIVOABONO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_ABONOS'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGO_ACTUALIZARBANFECABONO
                                            ,UN_REEMPLAZOS =>  MI_MSGERROR);
              END;

          ELSE
              MI_SEGFECHAPERSIG := CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                          (UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                           UN_FECHA_PAR => SYSDATE) = 'SI'
                                 THEN TRUE ELSE FALSE END;
              BEGIN
                  BEGIN
                      SELECT TOTFACTURAPERACTUAL,TOTFACTURAPAGO2
                      INTO   MI_TOTFACTURAPERACTUAL,MI_TOTFACTURAPAGO2
                      FROM   SP_USUARIO
                      WHERE  COMPANIA   = UN_COMPANIA
                        AND  CICLO      = UN_CICLO
                        AND  CODIGORUTA = UN_CODIGORUTA;

                  EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN --El código interno No existe, no se permite realizar el pago.
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'CODINTERNO';
                  MI_MSGERROR(0).VALOR := UN_CODIGOINTERNO;
                  PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE
                                             ,UN_ERROR_COD => PCK_ERRORES.ERR_PAGO_NOEXISTEUSUARIO
                                              ,UN_TABLAERROR => 'SP_USUARIO'
                                              ,UN_REEMPLAZOS =>  MI_MSGERROR);
              END;

              IF UN_OPERACION ='2' THEN
                  IF NOT MI_SEGFECHAPERSIG THEN
                      --Inserta el concepto 247 en el facturado.
                      BEGIN
                          MI_TABLA     := ' SP_FACTURADO';
                          MI_MERGEUSING :=  ' SELECT 1 FROM DUAL ' ;

                          MI_MERGEENLACE := '    COMPANIA     = '''|| UN_COMPANIA ||'''
                                             AND CICLO        = '|| UN_CICLO ||'
                                             AND CODIGORUTA   = '''|| UN_CODIGORUTA ||'''
                                             AND ANO          = '|| UN_ANO ||'
                                             AND PERIODO      =  '''|| UN_PERIODO ||'''
                                             AND CONCEPTO     = ''247''   ';

                          MI_MERGEEXISTE := ' UPDATE SET  VALOR_FACTURADO     = '|| (MI_TOTFACTURAPAGO2 - MI_TOTFACTURAPERACTUAL) ||'
                                                         ,CREATED_BY          = '''|| UN_USUARIO ||'''
                                                         ,DATE_CREATED        = SYSDATE ';

                          MI_MERGENOEXIS := 'INSERT (COMPANIA,
                                                     CICLO,
                                                     CODIGORUTA,
                                                     ANO,
                                                     PERIODO,
                                                     CONCEPTO,
                                                     VALOR_FACTURADO,
                                                     CREATED_BY,
                                                     DATE_CREATED)
                                             VALUES (''' || UN_COMPANIA || ''',
                                                      ' || UN_CICLO || ',
                                                    ''' || UN_CODIGORUTA || ''',
                                                      ' || UN_ANO || ',
                                                    ''' || UN_PERIODO || ''',
                                                      ''247'',
                                                      ' || (MI_TOTFACTURAPAGO2 - MI_TOTFACTURAPERACTUAL) || ',
                                                      '''|| UN_USUARIO ||''',
                                                      SYSDATE)  ';
                          BEGIN
                              MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                           UN_ACCION      => 'IM',
                                                           UN_MERGEUSING  => MI_MERGEUSING,
                                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                                           UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                           UN_MERGENOEXIS => MI_MERGENOEXIS);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                          MI_MSGERROR(0).CLAVE := 'RUTA';
                          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                          MI_MSGERROR(1).CLAVE := 'VALOR';
                          MI_MSGERROR(1).VALOR := (MI_TOTFACTURAPAGO2 - MI_TOTFACTURAPERACTUAL);

                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                     UN_TABLAERROR => 'SP_FACTURADO',
                                                     UN_ERROR_COD => PCK_ERRORES.ERR_PAGOCONCEPTO247,
                                                     UN_REEMPLAZOS => MI_MSGERROR);
                      END;

                  END IF;
              END IF;  --Fin si no es operacion 2

              IF UN_OPERACION = '1' OR UN_OPERACION = '2' THEN
                  BEGIN
                        --Creación en la tabla D_RECAUDO_USUARIO
                      MI_TABLA := 'SP_D_RECAUDO_USUARIO';
                      MI_MERGEUSING  := ' SELECT COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,CONCEPTO,
                                                 DEUDA,VALORABONOANT,VALOR_FACTURADO,VALORABONOACT,
                                                 VALORFINACT,VALORFINANT,CREDITOABONADO
                                          FROM   SP_FACTURADO
                                           WHERE COMPANIA        = '''|| UN_COMPANIA || '''
                                             AND CICLO           = '|| UN_CICLO ||'
                                             AND CODIGORUTA      = '''|| UN_CODIGORUTA ||'''
                                             AND ANO             = '|| UN_ANO ||'
                                             AND PERIODO         = '''|| UN_PERIODO ||'''
                                             AND CONCEPTO NOT IN (12,49,250)
                      ' ;

                      MI_MERGEENLACE := '       TABLA.COMPANIA        = VISTA.COMPANIA
                                            AND TABLA.CICLO           = VISTA.CICLO
                                            AND TABLA.CODIGORUTA      = VISTA.CODIGORUTA
                                            AND TRUNC(TABLA.FECHA)    = TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                            AND TABLA.BANCO           = '''|| UN_BANCOPAGO ||'''
                                            AND TABLA.NUMEROPAQUETE   = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                            AND TABLA.CONCEPTO        = VISTA.CONCEPTO
                                            AND TABLA.CONSECUTIVO     = '|| UN_CONSECUTIVO ||'
                                            AND TABLA.TIPOPAGO        = '''|| UN_OPERACION ||''' ';

                      MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORDEUDA        =  (VISTA.DEUDA - VISTA.VALORABONOANT)
                                                    ,TABLA.VALORPAGOPERIODO  =  (VISTA.VALOR_FACTURADO - VISTA.VALORABONOACT)
                                                    ,TABLA.VALORFINACT       =  (VISTA.VALORFINACT)
                                                    ,TABLA.VALORFINANT       =  (VISTA.VALORFINANT)
                                                    ,TABLA.CREDITOABONADO    =  (VISTA.CREDITOABONADO)
                                                    ,TABLA.MODIFIED_BY       =  '''|| UN_USUARIO ||'''
                                                    ,TABLA.DATE_MODIFIED     = SYSDATE ';

                      MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                                  CICLO,
                                                  CODIGORUTA,
                                                  FECHA,
                                                  BANCO,
                                                  NUMEROPAQUETE,
                                                  CONCEPTO,
                                                  CONSECUTIVO,
                                                  TIPOPAGO,
                                                  ANO,
                                                  PERIODO,
                                                  VALORDEUDA,
                                                  VALORPAGOPERIODO,
                                                  VALORFINACT,
                                                  VALORFINANT,
                                                  CREDITOABONADO,
                                                  CREATED_BY,
                                                  DATE_CREATED)
                                          VALUES ('''||  UN_COMPANIA ||'''    ,
                                                  '|| UN_CICLO ||'            ,
                                                  '''|| UN_CODIGORUTA ||'''   ,
                                                  TO_DATE(''' || TO_CHAR(UN_FECHAPAGO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                                                  '''|| UN_BANCOPAGO ||'''        ,
                                                  '|| UN_NUMEROPAQUETEPAGO ||'          ,
                                                  VISTA.CONCEPTO,
                                                  '|| UN_CONSECUTIVO ||'      ,
                                                  '''|| UN_OPERACION ||'''    ,
                                                  '|| UN_ANO ||'              ,
                                                  '''|| UN_PERIODO ||'''      ,
                                                  (VISTA.DEUDA - VISTA.VALORABONOANT),
                                                  (VISTA.VALOR_FACTURADO - VISTA.VALORABONOACT),
                                                  (VISTA.VALORFINACT),
                                                  (VISTA.VALORFINANT),
                                                  (VISTA.CREDITOABONADO),
                                                  '''|| UN_USUARIO ||'''      ,
                                                  SYSDATE )  ';

                      BEGIN
                          MI_FILAS := PCK_DATOS.FC_ACME
                                            (UN_TABLA       => MI_TABLA
                                            ,UN_ACCION      => 'IM'
                                            ,UN_MERGEUSING  => MI_MERGEUSING
                                            ,UN_MERGEENLACE => MI_MERGEENLACE
                                            ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                            ,UN_MERGENOEXIS => MI_MERGENOEXIS);
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                        MI_MSGERROR(1).CLAVE := 'RUTA';
                        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGODRECAUSUARIO,
                                                 UN_TABLAERROR => MI_TABLA,
                                                 UN_REEMPLAZOS => MI_MSGERROR);
                  END;
              END IF; --Fin validación operación 1 0 2

              IF UN_OPERACION ='D' THEN
                  MI_DBLCREDITO := UN_VALORPAGO;
                  BEGIN
                      MI_TABLA := 'SP_D_PAGOSDOBLES';
                      MI_MERGEUSING  := ' SELECT COMPANIA,CICLO,CODIGORUTA,CONCEPTO,VALOR_FACTURADO,DEUDA
                                          FROM   SP_FACTURADO
                                          WHERE  COMPANIA        = '''|| UN_COMPANIA || '''
                                            AND  CICLO           = '|| UN_CICLO ||'
                                            AND  CODIGORUTA      = '''|| UN_CODIGORUTA ||'''
                                            AND  ANO             = '|| UN_ANO ||'
                                            AND  PERIODO         = '''|| UN_PERIODO ||'''
                                            AND  CONCEPTO NOT IN (250) ' ;

                      MI_MERGEENLACE := '    TABLA.COMPANIA = VISTA.COMPANIA
                                         AND TRUNC(TABLA.FECHA) =  TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                         AND TABLA.BANCO = '''|| UN_BANCOPAGO ||'''
                                         AND TABLA.NUMEROPAQUETE = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                         AND TABLA.CICLO  = VISTA.CICLO
                                         AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                                         AND TABLA.CONCEPTO = VISTA.CONCEPTO ';

                      MI_MERGEEXISTE := ' UPDATE SET VALOR = (VISTA.DEUDA + VISTA.VALOR_FACTURADO)
                                                    ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                                    ,DATE_MODIFIED = SYSDATE ';

                      MI_MERGENOEXIS := '  INSERT (COMPANIA,
                                                   FECHA,
                                                   BANCO,
                                                   NUMEROPAQUETE,
                                                   CICLO,
                                                   CODIGORUTA,
                                                   CONCEPTO,
                                                   VALOR,
                                                   CREATED_BY,
                                                   DATE_CREATED)
                                           VALUES (VISTA.COMPANIA,
                                                   TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ,
                                                   '''|| UN_BANCOPAGO ||''' ,
                                                   '''|| UN_NUMEROPAQUETEPAGO ||''' ,
                                                   VISTA.CICLO,
                                                   VISTA.CODIGORUTA,
                                                   VISTA.CONCEPTO,
                                                   (VISTA.DEUDA + VISTA.VALOR_FACTURADO),
                                                   '''|| UN_USUARIO ||''',
                                                   SYSDATE)  ';

                      BEGIN
                          MI_FILAS := PCK_DATOS.FC_ACME
                                         (UN_TABLA       => MI_TABLA
                                         ,UN_ACCION      => 'IM'
                                         ,UN_MERGEUSING  => MI_MERGEUSING
                                         ,UN_MERGEENLACE => MI_MERGEENLACE
                                         ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                         ,UN_MERGENOEXIS => MI_MERGENOEXIS);
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG
                          (UN_EXC_COD    => SQLCODE,
                           UN_ERROR_COD  => PCK_ERRORES.ERR_REGPAGODOBLE,
                           UN_TABLAERROR => MI_TABLA,
                           UN_REEMPLAZOS => MI_MSGERROR);
                  END;

                  BEGIN   --Actualiza el saldo credito en usuario.
                      MI_TABLA := ' SP_USUARIO ';
                      MI_CAMPOS := ' NOTACREDITO      = NOTACREDITO + '|| MI_DBLCREDITO ||'
                                    ,MODIFIED_BY      = '''|| UN_USUARIO ||'''
                                    ,DATE_MODIFIED    = SYSDATE ';

                      MI_CONDICIONACME := '     COMPANIA   = '''|| UN_COMPANIA ||'''
                                           AND  CICLO      = '|| UN_CICLO ||'
                                           AND  CODIGORUTA = '''|| UN_CODIGORUTA ||'''   ';

                      BEGIN
                          MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                         UN_ACCION    => 'M',
                                                         UN_CAMPOS    => MI_CAMPOS,
                                                         UN_CONDICION => MI_CONDICIONACME);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                               MI_MSGERROR(1).CLAVE := 'RUTA';
                               MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;

                               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                          UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGONOTACREDITO ,
                                                          UN_TABLAERROR => MI_TABLA,
                                                          UN_REEMPLAZOS => MI_MSGERROR);
                  END;

                  BEGIN
                      MI_TABLA := 'SP_TBLHIST_SALDO_CREDITO';
                      MI_CONSECUTIVOCREDITO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                                                  (UN_TABLA    => MI_TABLA
                                                  ,UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''  '
                                                  ,UN_CAMPO    => 'CONSECUTIVO');

                      MI_CODSALDOCREDITO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                                              (UN_TABLA    => MI_TABLA
                                              ,UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''  '
                                              ,UN_CAMPO    => 'CODSALDOCREDITO');

                      MI_CAMPOS := 'COMPANIA,
                                    CICLO,
                                    CODIGORUTA,
                                    ANO,
                                    PERIODO,
                                    VALOR_SALDO,
                                    FECHA,
                                    CONSECUTIVO,
                                    USUARIOREG,
                                    BANCO,
                                    CODINTERNO,
                                    HORAREG,
                                    FECHAREG,
                                    PERIODOCREADO,
                                    ANOCREADO,
                                    CODSALDOCREDITO ,
                                    TIPOSALDOCREDITO,
                                    NUMEROPAQUETEPAGO,
                                    CONSECUTIVOPAGO,
                                    CREATED_BY,
                                    DATE_CREATED ';

                      MI_VALORES := ' '''|| UN_COMPANIA ||'''     ,
                                      '|| UN_CICLO ||'            ,
                                      '''|| UN_CODIGORUTA ||'''   ,
                                      '|| UN_ANO ||'              ,
                                      '''|| UN_PERIODO ||'''      ,
                                      '|| MI_DBLCREDITO ||'       ,
                                       TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'') ,
                                       '|| MI_CONSECUTIVOCREDITO ||' ,
                                       '''|| UN_USUARIO ||'''     ,
                                       '''|| UN_BANCOPAGO ||'''   ,
                                       ''' || UN_CODIGOINTERNO ||''',
                                       SYSDATE                    ,
                                       SYSDATE                    ,
                                       '''|| UN_PERIODO ||'''     ,
                                       '|| UN_ANO ||'             ,
                                       '|| MI_CODSALDOCREDITO ||' ,
                                       3                          ,
                                      '''|| UN_NUMEROPAQUETEPAGO ||''' ,
                                      '|| UN_CONSECUTIVO ||',
                                       '''|| UN_USUARIO ||'''      ,
                                       SYSDATE ';
                      BEGIN
                          MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_VALORES);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_REGPAGODOBLECREDITO
                                                 ,UN_TABLAERROR => 'SP_D_PAGOSDOBLES'
                                                 ,UN_REEMPLAZOS => MI_MSGERROR);
                  END;

                  MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                              ( UN_COMPANIA    => UN_COMPANIA
                               ,UN_CICLO       => UN_CICLO
                               ,UN_CODIGORUTA  => UN_CODIGORUTA
                               ,UN_ANO         => UN_ANO
                               ,UN_PERIODO     => UN_PERIODO
                               ,UN_FECHA       => UN_FECHAPAGO
                               ,UN_BANCO       => UN_BANCOPAGO
                               ,UN_PAQUETE     => UN_NUMEROPAQUETEPAGO
                               ,UN_REVERSA     => 0
                               ,UN_PAGODOBLE   => -1
                               ,UN_TERCER      => PCK_SYSMAN_UTL.FC_PAR
                                                      (UN_COMPANIA  => UN_COMPANIA,
                                                       UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR => SYSDATE) );

                  MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                              (UN_COMPANIA     => UN_COMPANIA
                              ,UN_CICLO        => UN_CICLO
                              ,UN_CODIGORUTA   => UN_CODIGORUTA
                              ,UN_ANO          => UN_ANO
                              ,UN_PERIODO      => UN_PERIODO
                              ,UN_FECHA        => UN_FECHAPAGO
                              ,UN_BANCO        => UN_BANCOPAGO
                              ,UN_PAQUETE      => UN_NUMEROPAQUETEPAGO
                              ,UN_REVERSA      => 0
                              ,UN_PAGODOBLE    => -1
                              ,UN_CONVENIO     => CASE WHEN PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS
                                                              (UN_COMPANIA => UN_COMPANIA
                                                              ,UN_NIT      => MI_NIT) <> 0
                                                       THEN 'SI'
                                                       ELSE 'NO'
                                                  END );

                  --Productividad Doble se debe dejar en after stament del trigger de pago.
              ELSE
                  --Actualiza tabla Usuario.
                  BEGIN
                      MI_TABLA := ' SP_USUARIO ';
                      MI_CAMPOS := ' FECHAPAGOPERPROCESO  = TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                    ,PAQUETEPAGOPERPROCESO = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                    ,NOFECHAPAGOPERPROCESO =  '''|| UN_OPERACION ||'''
                                    ,BANCOPERPROCESO = '''|| UN_BANCOPAGO ||'''
                                    ,RECAUDADOPROCESO = '|| UN_VALORPAGO ||'  ';

                      IF NOT MI_SEGFECHAPERSIG AND UN_OPERACION = '2' THEN
                          MI_CAMPOS := MI_CAMPOS || ',TOTFACTURAPERACTUAL = TOTFACTURAPAGO2';
                      END IF;

                      MI_CONDICIONACME := '     COMPANIA   = '''|| UN_COMPANIA ||'''
                                           AND  CICLO      = '|| UN_CICLO ||'
                                           AND  CODIGORUTA = '''|| UN_CODIGORUTA ||'''   ';

                      BEGIN
                          MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                         UN_ACCION    => 'M',
                                                         UN_CAMPOS    => MI_CAMPOS,
                                                         UN_CONDICION => MI_CONDICIONACME);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGOUSUARIO
                                                 ,UN_TABLAERROR => 'SP_D_PAGOSDOBLES'
                                                 ,UN_REEMPLAZOS => MI_MSGERROR);
                  END;

                  MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                              ( UN_COMPANIA    => UN_COMPANIA
                               ,UN_CICLO       => UN_CICLO
                               ,UN_CODIGORUTA  => UN_CODIGORUTA
                               ,UN_ANO         => UN_ANO
                               ,UN_PERIODO     => UN_PERIODO
                               ,UN_FECHA       => UN_FECHAPAGO
                               ,UN_BANCO       => UN_BANCOPAGO
                               ,UN_PAQUETE     => UN_NUMEROPAQUETEPAGO
                               ,UN_REVERSA     => 0
                               ,UN_PAGODOBLE   => 0
                               ,UN_TERCER      => PCK_SYSMAN_UTL.FC_PAR
                                                      (UN_COMPANIA  => UN_COMPANIA,
                                                       UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR => SYSDATE) );

                  MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                              (UN_COMPANIA     => UN_COMPANIA
                              ,UN_CICLO        => UN_CICLO
                              ,UN_CODIGORUTA   => UN_CODIGORUTA
                              ,UN_ANO          => UN_ANO
                              ,UN_PERIODO      => UN_PERIODO
                              ,UN_FECHA        => UN_FECHAPAGO
                              ,UN_BANCO        => UN_BANCOPAGO
                              ,UN_PAQUETE      => UN_NUMEROPAQUETEPAGO
                              ,UN_REVERSA      => 0
                              ,UN_PAGODOBLE    => 0
                              ,UN_CONVENIO     => CASE WHEN PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS
                                                           (UN_COMPANIA => UN_COMPANIA
                                                           ,UN_NIT      => MI_NIT) <> 0
                                                       THEN 'SI'
                                                       ELSE 'NO'
                                                  END );

              END IF;

          END IF; --Fin validación si no es abono
      END;

  END PR_REGISTRARPAGO;

--18
  PROCEDURE PR_ELIMINARPAGO(

      /*
      NAME              : PR_ELIMINARPAGO --> Se pasa del formulario ActualizacionPagos.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 02/10/2017
      TIME              : 02:04 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento que se ejecuta al eliminar un pago en la tabla SP_PAGO.

      PARAMETERS        :

      MODIFICATIONS     :

      @NAME:    EliminarPago
      @METHOD:  DELETE
      */
      UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
     ,UN_FECHAPAGO           IN DATE
     ,UN_BANCOPAGO           IN SP_PAGO.BANCO%TYPE
     ,UN_NUMEROPAQUETEPAGO   IN SP_PAGO.NUMEROPAQUETE%TYPE
     ,UN_CONSECUTIVO         IN SP_PAGO.CONSECUTIVO%TYPE
     ,UN_CODIGORUTA          IN SP_PAGO.CODIGORUTA%TYPE
     ,UN_CICLO               IN SP_PAGO.CICLO%TYPE
     ,UN_ANO                 IN SP_PAGO.ANO%TYPE
     ,UN_PERIODO             IN SP_PAGO.PERIODO%TYPE
     ,UN_VALORPAGO           IN SP_PAGO.VALORPAGO%TYPE
     ,UN_OPERACION           IN SP_PAGO.OPERACION%TYPE
     ,UN_USUARIO             IN SP_PAGO.CREATED_BY%TYPE
  ) AS

  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_BANCOPERPROCESO      SP_USUARIO.BANCOPERPROCESO%TYPE;
  MI_ANOUSUARIO           SP_USUARIO.ANO%TYPE;
  MI_PERIODOUSUARIO       SP_USUARIO.PERIODO%TYPE;
  MI_PAR                  BOOLEAN;

  MI_CONSECUTIVOABONO     SP_ABONOS.CONSECUTIVO%TYPE DEFAULT 0;
  MI_VLRRECARGO           SP_FACTURADO.VALOR_FACTURADO%TYPE;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
  MI_NIT                  COMPANIA.NITCOMPANIA%TYPE;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;


  BEGIN
      BEGIN
          SELECT ANO,PERIODO,BANCOPERPROCESO
          INTO   MI_ANOUSUARIO,MI_PERIODOUSUARIO,MI_BANCOPERPROCESO
          FROM   SP_USUARIO
          WHERE  COMPANIA = UN_COMPANIA
            AND  CODIGORUTA = UN_CODIGORUTA
            AND  CICLO = UN_CICLO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          --El Usuario del pago --USUARIOPAGO-- ya no existe
          MI_MSGERROR(0).CLAVE := 'USUARIOPAGO';
          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                     ,UN_TABLAERROR => 'SP_USUARIO'
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGONOEXISUSUARIO
                                     ,UN_REEMPLAZOS =>  MI_MSGERROR);
      END;

      BEGIN
          IF (MI_ANOUSUARIO <> UN_ANO) AND (MI_PERIODOUSUARIO <> UN_PERIODO) THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --El periodo del usuario --RUTA--, No corresponde al periodo actual.
          MI_MSGERROR(0).CLAVE := 'RUTA';
          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGOCONPERIODODIFERENTEACT
                                    ,UN_REEMPLAZOS =>  MI_MSGERROR );
      END;

      MI_PAR :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                            (UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'PERMITE AUTORIZACION DE ABONOS',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI'
                      THEN TRUE ELSE FALSE
                 END;

      IF MI_PAR AND UN_OPERACION = 'A' THEN
          BEGIN
              IF NVL(MI_BANCOPERPROCESO, ' ') <> ' ' THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --El usuario ya pago no se permite Borrar Abono
              MI_MSGERROR(0).CLAVE := 'RUTA';
              MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_PAGOABONOUSUARIOPAGO
                                        ,UN_REEMPLAZOS =>  MI_MSGERROR );
          END;

          BEGIN
              SELECT CONSECUTIVO
              INTO   MI_CONSECUTIVOABONO
              FROM   SP_ABONOS
              WHERE  COMPANIA = UN_COMPANIA
                AND  CICLO = UN_CICLO
                AND  CODIGORUTA = UN_CODIGORUTA
                AND  TRUNC(FECHA) = UN_FECHAPAGO
                AND  BANCO = UN_BANCOPAGO
                AND  VALORABONADOPERIODO = UN_VALORPAGO;
          EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN
              --No se encontró ningun abono que coincida con la información suminstrada.
              MI_MSGERROR(0).CLAVE := 'RUTA';
              MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
              MI_MSGERROR(1).CLAVE := 'VALOR';
              MI_MSGERROR(1).VALOR := UN_VALORPAGO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGOABONONOEXISTE
                                        ,UN_REEMPLAZOS =>  MI_MSGERROR );
          END;

          BEGIN   --Quita el indicador de autorizado para eliminarlo correctamente.
              MI_TABLA := 'SP_ABONOS';
              MI_CAMPOS := 'INDAUTORIZADO = 0 ';
              MI_CONDICIONACME := '    COMPANIA  = '''|| UN_COMPANIA ||'''
                                   AND CICLO = '|| UN_CICLO ||'
                                   AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                   AND ANO = '|| UN_ANO ||'
                                   AND PERIODO = '''|| UN_PERIODO ||'''
                                   AND CONSECUTIVO = '|| MI_CONSECUTIVOABONO ||'  ';
              BEGIN
                  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICIONACME);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              --Se presentó error al reversar el abono autorizado: --CODABONO-- , para el usuario: --RUTA-- .
              MI_MSGERROR(0).CLAVE := 'CODABONO';
              MI_MSGERROR(0).VALOR :=  MI_CONSECUTIVOABONO;
              MI_MSGERROR(1).CLAVE := 'RUTA';
              MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPACTAUTORIZADO,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_REEMPLAZOS => MI_MSGERROR);
          END;

          BEGIN
              MI_TABLA := 'SP_ABONOS';
              MI_CONDICIONACME := '    COMPANIA  = '''|| UN_COMPANIA ||'''
                                   AND CICLO = '|| UN_CICLO ||'
                                   AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                   AND ANO = '|| UN_ANO ||'
                                   AND PERIODO = '''|| UN_PERIODO ||'''
                                   AND CONSECUTIVO = '|| MI_CONSECUTIVOABONO ||'  ';

              BEGIN
                  --Se ejecuta el trigger desde abonos
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'E'
                                             ,UN_CONDICION => MI_CONDICIONACME);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN
              --Se presentó error al eliminar el abono desde pagos.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_TABLAERROR => MI_TABLA
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGOABONO );
          END;
      ELSE
          BEGIN
               SELECT  NITCOMPANIA
               INTO    MI_NIT
               FROM    COMPANIA
               WHERE   CODIGO = UN_COMPANIA;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_NIT := ' ';
          END;

          IF UN_OPERACION = '1' OR UN_OPERACION = '2' THEN
              BEGIN
                  SELECT VALOR_FACTURADO
                  INTO   MI_VLRRECARGO
                  FROM   SP_FACTURADO
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  CICLO = UN_CICLO
                    AND  CODIGORUTA = UN_CODIGORUTA
                    AND  ANO  = UN_ANO
                    AND  PERIODO =  UN_PERIODO
                    AND  CONCEPTO = 247;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_VLRRECARGO := 0;
              END;

              MI_PAR :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                    (UN_COMPANIA  => UN_COMPANIA,
                                     UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                     UN_FECHA_PAR => SYSDATE) = 'SI'
                              THEN TRUE ELSE FALSE
                         END;

              BEGIN
                  MI_TABLA := ' SP_USUARIO ';
                  MI_CAMPOS := ' FECHAPAGOPERPROCESO  = NULL
                                ,PAQUETEPAGOPERPROCESO = NULL
                                ,NOFECHAPAGOPERPROCESO =  NULL
                                ,BANCOPERPROCESO = NULL
                                ,RECAUDADOPROCESO = 0 ';

                  IF NOT MI_PAR AND UN_OPERACION = '2' THEN
                      MI_CAMPOS := MI_CAMPOS ||  ',TOTFACTURAPERACTUAL = TOTFACTURAPERACTUAL - '|| MI_VLRRECARGO ||' ' ;
                  END IF;

                  MI_CONDICIONACME := '     COMPANIA   = '''|| UN_COMPANIA ||'''
                                       AND  CICLO      = '|| UN_CICLO ||'
                                       AND  CODIGORUTA = '''|| UN_CODIGORUTA ||'''   ';

                  BEGIN
                      MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  --Se presentó error actualizar los datos de pago en usuario, para el suscriptor: --RUTA--
                  MI_MSGERROR(0).CLAVE := 'RUTA';
                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => MI_TABLA
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGOUSUARIO
                                            ,UN_REEMPLAZOS => MI_MSGERROR);
              END;

              IF NOT MI_PAR AND UN_OPERACION = '2' THEN  --Actualiza el concepto 247 a cero
                  BEGIN
                      MI_TABLA := 'SP_FACTURADO';
                      MI_CAMPOS := 'VALOR_FACTURADO = 0';
                      MI_CONDICIONACME :='     COMPANIA ='''|| UN_COMPANIA ||'''
                                          AND  CICLO = '|| UN_CICLO ||'
                                          AND  CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                          AND  ANO  = '|| UN_ANO ||'
                                          AND  PERIODO = '''|| UN_PERIODO ||'''
                                          AND  CONCEPTO = 247 ';

                      BEGIN
                          MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                         UN_ACCION    => 'M',
                                                         UN_CAMPOS    => MI_CAMPOS,
                                                         UN_CONDICION => MI_CONDICIONACME);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      --Se presentó error al eliminar el concepto recargo en el facturado del usuario: --RUTA--
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_TABLAERROR => MI_TABLA
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGORECARGO247
                                                ,UN_REEMPLAZOS => MI_MSGERROR);
                  END;
              END IF;

              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                          ( UN_COMPANIA    => UN_COMPANIA
                           ,UN_CICLO       => UN_CICLO
                           ,UN_CODIGORUTA  => UN_CODIGORUTA
                           ,UN_ANO         => UN_ANO
                           ,UN_PERIODO     => UN_PERIODO
                           ,UN_FECHA       => UN_FECHAPAGO
                           ,UN_BANCO       => UN_BANCOPAGO
                           ,UN_PAQUETE     => UN_NUMEROPAQUETEPAGO
                           ,UN_REVERSA     => -1
                           ,UN_PAGODOBLE   => 0
                           ,UN_TERCER      => PCK_SYSMAN_UTL.FC_PAR
                                                  (UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE) );


              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                          (UN_COMPANIA     => UN_COMPANIA
                          ,UN_CICLO        => UN_CICLO
                          ,UN_CODIGORUTA   => UN_CODIGORUTA
                          ,UN_ANO          => UN_ANO
                          ,UN_PERIODO      => UN_PERIODO
                          ,UN_FECHA        => UN_FECHAPAGO
                          ,UN_BANCO        => UN_BANCOPAGO
                          ,UN_PAQUETE      => UN_NUMEROPAQUETEPAGO
                          ,UN_REVERSA      => -1
                          ,UN_PAGODOBLE    => 0
                          ,UN_CONVENIO     => CASE WHEN PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS
                                                       (UN_COMPANIA => UN_COMPANIA
                                                       ,UN_NIT      => MI_NIT) <> 0
                                                   THEN 'SI'
                                                   ELSE 'NO'
                                              END );


              PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD
                  ( UN_COMPANIA   => UN_COMPANIA
                   ,UN_CICLO      => UN_CICLO
                   ,UN_USUARIO    => UN_USUARIO
                   ,UN_FECHA      => UN_FECHAPAGO
                   ,UN_BANCO      => UN_BANCOPAGO
                   ,UN_PAQUETE    => UN_NUMEROPAQUETEPAGO
                   ,UN_OPERACION  => 'PAGO' );

              MI_PAR :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                                     (UN_COMPANIA  => UN_COMPANIA,
                                      UN_NOMBRE    => 'PERMITE CARGAR Y RECAUDAR NOVEDADES EXTERNAS',
                                      UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                      UN_FECHA_PAR => SYSDATE) = 'SI'
                               THEN TRUE ELSE FALSE
                         END;

              IF MI_PAR THEN
                  BEGIN
                      MI_TABLA := 'SP_FACTURADO_EXTERNO';
                      MI_CAMPOS := ' VALOR_RECAUDO = 0
                                    ,FECHA_PAGO = NULL
                                    ,BANCO_PAGO = NULL
                                    ,PAQUETE_PAGO = NULL
                                    ,CODIGO_BARRA =NULL ';

                      MI_CONDICIONACME := '    COMPANIA = '''|| UN_COMPANIA ||'''
                                           AND CICLO = '|| UN_CICLO ||'
                                           AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                           AND ANO = '|| UN_ANO ||'
                                           AND PERIODO = '''|| UN_PERIODO ||'''
                                           AND ANULADO = 0 ';

                      BEGIN
                          MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICIONACME);
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      --Se presentó error al eliminar los facturados externos del usuario: --RUTA--.
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR :=  UN_CODIGORUTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGOFACTEXTERNO,
                                                 UN_TABLAERROR => MI_TABLA,
                                                 UN_REEMPLAZOS => MI_MSGERROR);
                  END;
              END IF;  --Fin parámetro fac externo

              BEGIN
                  MI_TABLA := 'SP_D_RECAUDO_USUARIO';
                  MI_CONDICIONACME := '    COMPANIA   = '''|| UN_COMPANIA ||'''
                                       AND CICLO = '|| UN_CICLO ||'
                                       AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                       AND TRUNC(FECHA) =  TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                       AND BANCO = '''|| UN_BANCOPAGO ||'''
                                       AND NUMEROPAQUETE = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                       AND CONSECUTIVO = '|| UN_CONSECUTIVO ||'
                                       AND TIPOPAGO = '''|| UN_OPERACION ||'''  ';

                  BEGIN
                      --Ejecuta el trigger para actualizar en d_recaudo
                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                 ,UN_ACCION     => 'E'
                                                 ,UN_CONDICION  => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'RUTA';
                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGODRECUSUARIO,
                                             UN_TABLAERROR => MI_TABLA);
              END;

          ELSIF UN_OPERACION = 'D' THEN
              BEGIN
                  MI_TABLA := 'SP_D_PAGOSDOBLES';
                  MI_CONDICIONACME := '    COMPANIA = '''|| UN_COMPANIA ||'''
                                       AND TRUNC(FECHA) = TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                       AND BANCO = '''|| UN_BANCOPAGO ||'''
                                     AND NUMEROPAQUETE = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                       AND CICLO  = '|| UN_CICLO ||'
                                       AND CODIGORUTA ='''|| UN_CODIGORUTA ||'''   ';
                  BEGIN
                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                                 ,UN_ACCION    => 'E'
                                                 ,UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'RUTA';
                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGODOBLE,
                                             UN_TABLAERROR => MI_TABLA);
              END;

              BEGIN   --Actualiza el saldo credito en usuario.
                  MI_TABLA := ' SP_USUARIO ';
                  MI_CAMPOS := ' NOTACREDITO      = NOTACREDITO - '|| UN_VALORPAGO ||'
                                ,MODIFIED_BY      = '''|| UN_USUARIO ||'''
                                ,DATE_MODIFIED    = SYSDATE ';

                  MI_CONDICIONACME := '     COMPANIA   = '''|| UN_COMPANIA ||'''
                                       AND  CICLO      = '|| UN_CICLO ||'
                                       AND  CODIGORUTA = '''|| UN_CODIGORUTA ||'''   ';

                  BEGIN
                      MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                          --Se presentó error al actualizar los datos de saldo crédito en usuario para el usuario: --RUTA--
                          MI_MSGERROR(1).CLAVE := 'RUTA';
                          MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGONOTACREDITO ,
                                                     UN_TABLAERROR => MI_TABLA,
                                                     UN_REEMPLAZOS => MI_MSGERROR);
              END;

              BEGIN
                  MI_TABLA := 'SP_TBLHIST_SALDO_CREDITO';
                  MI_CONDICIONACME := '    COMPANIA   = '''|| UN_COMPANIA ||'''
                                       AND CICLO  = '|| UN_CICLO ||'
                                       AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                       AND ANO    = '|| UN_ANO ||'
                                       AND PERIODO    = '''|| UN_PERIODO ||'''
                                       AND BANCO  = '''|| UN_BANCOPAGO ||'''
                                       AND TRUNC(FECHA) =  TO_DATE('''|| TO_CHAR(UN_FECHAPAGO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                       AND NUMEROPAQUETEPAGO = '''|| UN_NUMEROPAQUETEPAGO ||'''
                                       AND CONSECUTIVOPAGO = '|| UN_CONSECUTIVO ||'  ';
                  BEGIN
                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'E'
                                                  ,UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN TOO_MANY_ROWS OR NO_DATA_FOUND  THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => MI_TABLA
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARPAGODOBLECREDITO );
              END;


              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                          ( UN_COMPANIA    => UN_COMPANIA
                           ,UN_CICLO       => UN_CICLO
                           ,UN_CODIGORUTA  => UN_CODIGORUTA
                           ,UN_ANO         => UN_ANO
                           ,UN_PERIODO     => UN_PERIODO
                           ,UN_FECHA       => UN_FECHAPAGO
                           ,UN_BANCO       => UN_BANCOPAGO
                           ,UN_PAQUETE     => UN_NUMEROPAQUETEPAGO
                           ,UN_REVERSA     => -1
                           ,UN_PAGODOBLE   => -1
                           ,UN_TERCER      => PCK_SYSMAN_UTL.FC_PAR
                                                  (UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE) );

              MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                          (UN_COMPANIA     => UN_COMPANIA
                          ,UN_CICLO        => UN_CICLO
                          ,UN_CODIGORUTA   => UN_CODIGORUTA
                          ,UN_ANO          => UN_ANO
                          ,UN_PERIODO      => UN_PERIODO
                          ,UN_FECHA        => UN_FECHAPAGO
                          ,UN_BANCO        => UN_BANCOPAGO
                          ,UN_PAQUETE      => UN_NUMEROPAQUETEPAGO
                          ,UN_REVERSA      => -1
                          ,UN_PAGODOBLE    => -1
                          ,UN_CONVENIO     => CASE WHEN PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS
                                                          (UN_COMPANIA => UN_COMPANIA
                                                          ,UN_NIT      => MI_NIT) <> 0
                                                   THEN 'SI'
                                                   ELSE 'NO'
                                              END );

              PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD
                  ( UN_COMPANIA   => UN_COMPANIA
                   ,UN_CICLO      => UN_CICLO
                   ,UN_USUARIO    => UN_USUARIO
                   ,UN_FECHA      => UN_FECHAPAGO
                   ,UN_BANCO      => UN_BANCOPAGO
                   ,UN_PAQUETE    => UN_NUMEROPAQUETEPAGO
                   ,UN_OPERACION  => 'DOBLE' );

          END IF;--Fin operaciones 1 o 2 o D



      END IF; --Fin parámetro y operación abonos


  END PR_ELIMINARPAGO;


END PCK_SERVICIOS_PUBLICOS_ABONOS;