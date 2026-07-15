create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM7 AS
--1
  FUNCTION FC_CALCULOFACTURACION

  /*
        NAME              : FC_CALCULOFACTURACION --> En Access CalculoFacturacion
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 07/02/2017
        TIME              : 04:07 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Cálculo de facturación de servicios publicos

        PARAMETERS        : UN_COMPANIA          => Compañia en la que se esta trabajando
                            UN_INTCICLO          => Ciclo que se va a calcular
                            UN_STRCODIGOINICIAL  => Codigo desde el cual se va a comenzar el calculo.
                            UN_STRCODIGOFINAL    => Codigo de ruta final.
                            UN_ENSERIE           => Define si el cálculo es individual o por lote.
                            UN_FINAL             => Si maneja prefacturación y este es el cálculo definitivo.
                            UN_USUARIO           => Usuario con el que se ingresa a la aplicación

        MODIFICATIONS     :

        @NAME:    calcularFacturacion
        @METHOD:  POST
  */
  (
   UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_INTCICLO              IN PCK_SUBTIPOS.TI_CICLO
  ,UN_STRCODIGOINICIAL      IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_STRCODIGOFINAL        IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ENSERIE               IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0   --Define si es el cálculo individual o en serie
  ,UN_FINAL                 IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_USUARIO               IN SP_USUARIO.CREATED_BY%TYPE :=''
  )

  RETURN VARCHAR2

  AS
  MI_NIT                     COMPANIA.NITCOMPANIA%TYPE;
  MI_RSUSUARIOS              TI_USUARIO;
  MI_RSTARIFAS               TTARIFAS;
  MI_FACTURADOACT            TI_FACTURADO;
  MI_PARAMETROS              TI_PARAMETRO;         --Type de parámetros que se utilizaran en el cálculo de facturación
  MI_CONCEPTOS               TI_CONCEPTO;
  MI_CONSUMOMANUSU           NUMBER(1);            --Variable por usuario que determina si tiene consumos manuales, Afecta tipo cálculo y consumos
  MI_CONSUMOMICRO            NUMBER(1);
  MI_VALIDACALCULOUSUARIO    VARCHAR2(300 CHAR);    --Variable que almacena si se puede o no calcular un ususario, Si es cálculo individual muestra mensaje de error
  MI_CODERROR                PCK_SUBTIPOS.TI_ENTERO;
  MI_TASARECARGO             SP_CICLO.TASARECARGO%TYPE;
  MI_PREFACTURANDO           SP_CICLO.PREFACTURANDO%TYPE      DEFAULT 0;
  MI_APLICADESCUENTO         SP_CICLO.APLICADESCUENTO%TYPE    DEFAULT 0;
  MI_TIPOCALCULO             VARCHAR2(2 CHAR);
  MI_RTA                     VARCHAR2(300 CHAR);
  MI_CONSUMO                 SP_USUARIO.CONSUMO%TYPE;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_APLICAHISTDESVIACION    PCK_SUBTIPOS.TI_LOGICO;
  MI_TOTALFACT               NUMBER(20,0) DEFAULT 0;
  MI_ETAPA                   VARCHAR2(100 CHAR);
  MI_RSFACTURADOS            SYS_REFCURSOR;
  MI_RSCONCEPTOS             SYS_REFCURSOR;
  MI_NUMCONCEPTOS            SP_CONCEPTOS.CODIGO%TYPE      DEFAULT 0;

  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_MERGEENLACE             PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE             PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS             PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MTL                     NUMBER(20,0);
  MI_CUENTA                  NUMBER(20,0);
  MI_DEUDA                   SP_USUARIO.TOTALDEUDA%TYPE        DEFAULT 0;
  MI_DEUDAASEO               SP_USUARIO.DEUDAASEO%TYPE         DEFAULT 0;
  MI_DEUDAALUMBRADO          SP_USUARIO.DEUDAALUMBRADO%TYPE    DEFAULT 0;
  MI_C                       SP_FACTURADO.CONCEPTO%TYPE        DEFAULT 0;
  MI_CALDESCCONCEPTOUSU      PCK_SUBTIPOS.TI_LOGICO            DEFAULT 0;
  MI_VALPRODUC               SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_NUMSALDOS               PCK_SUBTIPOS.TI_ENTERO_LARGO      DEFAULT 0;
  MI_FINAL                   PCK_SUBTIPOS.TI_LOGICO            DEFAULT 0;

  BEGIN

    -- Nit de la compañia para validar las autorizaciones
    SELECT  NITCOMPANIA
    INTO    MI_NIT
    FROM    COMPANIA
    WHERE   CODIGO = UN_COMPANIA;

    --Carga los parámetros de facturación
    MI_ETAPA := 'parámetros de facturación';
    MI_PARAMETROS := FC_CARGARPARFACTU(UN_COMPANIA => UN_COMPANIA,
                                          UN_NIT  => MI_NIT);

    MI_NUMCONCEPTOS := TO_NUMBER(MI_PARAMETROS('NUMCONCEPTOS').VALOR);

    BEGIN
       BEGIN
           SELECT TASARECARGO   , PREFACTURANDO   , APLICADESCUENTO
           INTO   MI_TASARECARGO, MI_PREFACTURANDO, MI_APLICADESCUENTO
           FROM   SP_CICLO
           WHERE  COMPANIA = UN_COMPANIA
             AND  NUMERO   = UN_INTCICLO;

           EXCEPTION WHEN NO_DATA_FOUND THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
           MI_MSGERROR(1).CLAVE := 'CICLO';
           MI_MSGERROR(1).VALOR := UN_INTCICLO;
           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                      UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_CICLOCALCULO,
                                     UN_TABLAERROR => 'SP_CICLO',
                                     UN_REEMPLAZOS => MI_MSGERROR);
    END;

    IF UN_ENSERIE <>0 THEN --Se pasa codigo del frm calculo en lote a plsql.
        BEGIN
            BEGIN
                MI_TABLA := 'SP_ERRORCALCULO';
                MI_CONDICIONACME := '    COMPANIA  = ''' || UN_COMPANIA || '''
                                     AND CICLO     =    '|| UN_INTCICLO ||'';

                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA      => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                       UN_CONDICION => MI_CONDICIONACME);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR(1).CLAVE := 'CICLO';
                MI_MSGERROR(1).VALOR := UN_INTCICLO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ELIMINARERRCALC ,
                                          UN_TABLAERROR => MI_TABLA,
                                          UN_REEMPLAZOS => MI_MSGERROR);
        END;
    END IF;

    IF MI_PARAMETROS('MANPREFACTURACION').VALOR ='NO' THEN
         MI_PREFACTURANDO := 0;
    END IF;

    IF MI_PREFACTURANDO = 0 THEN
         BEGIN
             BEGIN
                 MI_TABLA     := ' SP_CICLO ';
                 MI_CAMPOS    := ' INDCALCULADO      = '|| (-1) ||'
                                  ,INDBLOQUEOMANUAL  = '|| (0) ||'
                                  ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                                  ,DATE_MODIFIED     = SYSDATE ';

                 MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                                  AND NUMERO = '|| UN_INTCICLO ||'   ';

                 MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICIONACME);
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
             END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
             MI_MSGERROR(1).CLAVE := 'CICLO';
             MI_MSGERROR(1).VALOR :=  UN_INTCICLO;

             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUALIZACALCCICLO,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_MSGERROR);
         END;
    END IF;

    <<CONCEPTOS>>
    FOR MI_RSCONCEPTOS IN
    (
      SELECT CODIGO,INTERES,IVA,RECARGO,SERVICIO,VALOR,PORC_DESCUENTO
      FROM   SP_CONCEPTOS
      WHERE  COMPANIA  = UN_COMPANIA
        AND  (CODIGO <=50 OR CODIGO = MI_NUMCONCEPTOS -1 OR CODIGO BETWEEN 201 AND 207)
    )
    LOOP
         IF MI_RSCONCEPTOS.CODIGO = MI_NUMCONCEPTOS -1 THEN
             MI_CONCEPTOS(51).RECARGO := MI_RSCONCEPTOS.RECARGO;
             MI_CONCEPTOS(51).VALOR   := MI_RSCONCEPTOS.VALOR;
         ELSE
             MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).INTERES := MI_RSCONCEPTOS.INTERES;     --30
             MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).IVA := MI_RSCONCEPTOS.IVA;             --47
             MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).RECARGO := MI_RSCONCEPTOS.RECARGO;     --16
             MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).SERVICIO := MI_RSCONCEPTOS.SERVICIO;
             MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).VALOR := MI_RSCONCEPTOS.VALOR;

             IF MI_PARAMETROS('CALDESCUENTOCONCEPTO').VALOR ='SI' THEN
                  MI_CONCEPTOS(MI_RSCONCEPTOS.CODIGO).DESCUENTO := MI_RSCONCEPTOS.PORC_DESCUENTO;
             END IF;

         END IF;
    END LOOP CONCEPTOS;

    --Seleccion de usuarios a liquidar
    BEGIN
     SELECT  COMPANIA,CICLO,ANO,PERIODO,CODIGORUTA,CODIGOINTERNO, USO,ESTRATO,CONSUMOMANUAL,BANCOPERPROCESO,
             FIMM,TIPOLECTURA,ESTADO,PERIODOSNOCOBROFAC,CONSUMO1,CONSUMO2,CONSUMO3,CONSUMO4,CONSUMO5,CONSUMO6,
             PROBLEMALECTURA,LECTURA,LECTURA1,LECTURA2,LECTURA3,LECTURA4,LECTURA5,LECTURA6,CHAPETAS,INDDESHABITADO,
             TIPOCALCULO,CRITICACONSUMO,ANOCAMBIOMEDIDOR,CONSUMOMEDIDORANT,INDMEDCAMBIADO,PORCENTAJEAPLICAR,CONSUMOACU,
             ACUMULADO,ACUMULADOPER,PERIODOSINLECTURA,INDCALCULADO,DESCONTADO,CARGOS_FIJOS_CERO,DESVIOSIGNIFICATIVO,
             METROSDESVIACION,TOTALIZADOR,NUMERODIGITOS,PERIODOCAMBIOMEDIDOR,ACUEDUCTO,PORMICRO,INQUILINATOS,
             TOTFACTURAPERACTUAL,TOTFACTURAPAGO2,INDPREPARADO,BANCOPERANTERIOR,PERIODOSATRASO,CONSUMO,CONSUMOPROM,CONSUMOALC,
             CONSUMOBASICO,CONSUMOCOMPLEMENTARIO,CONSUMOSUNTUARIO,CONSUMOBASICOALC,CONSUMOCOMPLEMENTARIOALC,
             CONSUMOSUNTUARIOALC,ANOENTRADANUEVOUSUARIO,PERENTRADANUEVOUSUARIO,SUBSINMEDICION, SOBRESINMEDICION,
             SUB_TASAAMBIENTALAC,SOBRE_TASAAMBIENTALAC ,COSTOCONSUMOAC,CARGOFIJO,ACSINMEDICION,VALCONSUMO,
             VALCONSUMOPROM,TOTALACUEDUCTO,SUBALCSINMEDICION,SOBREALCSINMEDICION,SUB_TASAAMBIENTALALC,COSTOCONSUMOAL,
             CARGOFIJOAL,ALSINMEDICION,ALCANTARILLADO,ASEO,SOBRE_TASAAMBIENTALALC,TOTALALCANTARILLADO,VALORBASICO,
             VALORCOMPLEMENTARIO,VALORSUNTUARIO,SUBFIJO,SUBCONSUMOAC,SUBACUEDUCTO,SOBREFIJO,SOBRECONSUMOAC,
             SOBREACUEDUCTO,SUBSIDIO,SOBREPRECIO,VALORALCBASICO,VALORALCCOMPLEMENTARIO,VALORALCSUNTUARIO,SUBFIJOALC,SUBCONSUMOAL,
             SUBALCANTARILLADO,SOBREALCANTARILLADO ,SOBREFIJOALC,SOBRECONSUMOAL,TOTALASEO,TAFNA_720,TAFA_720,
             SUBCCS_720,SOBCCS_720,SUBCBLS_720,SOBCBLS_720,SUBCLUS_720,SOBCLUS_720,SUBCRT_720,
             SOBCRT_720,SUBCDF_720,SOBCDF_720,SUBCTL_720,SOBCTL_720,SUBVBA_720,SOBVBA_720,FRECUENCIAASEOSEMANA,
             PESOASEO,ESTRATOASEO,APARTAMENTO,ASEOBARRIDO,CALCULODESH,SEPARACIONENFUENTE,NOPUERTAPUERTA,VDESHABITADO,
             NOTADEBITO,VASEOUNICO,VASEODOMICILIARIO,VASEOBARRIDO,VASEOCONSUMO,COSTOREALASEO,SUBBARR_LIM,SOBBARR_LIM,
             SUBMANEJO_REC,SOBMANEJO_REC,SUBRECOLECCION,SOBRECOLECCION,SUBTRAMO,SOBTRAMO,SOBDESHABITADO,SUBDESHABITADO,
             SUBDISP_FINAL,SOBDISP_FINAL,SUBASEO,SOBREASEO,VTRAMOEXCEDENTE,VALASEOCCS_720,VALASEOCBLS_720,VALASEOCLUS_720,
             VALASEOCRT_720,VALASEOCDF_720,VALASEOCTL_720,VALASEOVBA_720,INDFACTURADO,DEUDAASEO,RECARGOASEO,AJUSTEDECENAASEO,
             TOTFACTURAASEO,TOTFACTURAASEOPAGO2,FACTURA,COSTOFIJOAC, COSTOM3AC,TARIFAM3BASICOAC, TARIFAM3COMPLEMENTARIOAC,
             TARIFAM3SUNTUARIOAC,COSTOM3AL, COSTOFIJOAL, TARIFAM3BASICOAL,TARIFAM3COMPLEMENTARIOAL,TARIFAM3SUNTUARIOAL,DEUDAINICIAL,
             LECTURAANTINICIAL,LECTURAINICIAL,PERIODOSATRASOINICIAL,EMPRESAASEOEXT,PINTADEUDATERCE,ALUMBRADO,FIJOALUMBRADO,
             ESTRATOALUMBRADO,HOGAR_COMUNITARIO,APLICA_RES493,PERIODOSNOCOBROFIN,TOTALDEUDA,NOTACREDITO,RECARGOACUEDUCTO,
             RECARGOALC,RECARGOALUMBRADO, SINDESCUENTO, INCENTIVO , ABONOS ,INDFINANINICIAL,NOFECHAPAGOPERANTERIOR,TOTFACTURAPERANTERIOR,
             TOTALALUMBRADO,DEUDAALUMBRADO,TOTALFACTURAALUMBRADO,CREDITOABONADO,
             0 COBRADOS,0 NUMSALDOS
     BULK COLLECT INTO MI_RSUSUARIOS
     FROM    SP_USUARIO
     WHERE   COMPANIA = UN_COMPANIA
       AND   CICLO    = UN_INTCICLO
       AND   CODIGORUTA  BETWEEN UN_STRCODIGOINICIAL AND  UN_STRCODIGOFINAL;

     IF MI_RSUSUARIOS.COUNT =0 THEN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
     END IF;

     MI_CUENTA := MI_RSUSUARIOS.COUNT;

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CALCULOFACT,
                                UN_TABLAERROR => 'SP_USUARIO');
    END;
  --<<USUARIOS>>
    FOR i IN MI_RSUSUARIOS.FIRST .. MI_RSUSUARIOS.LAST
    LOOP
     BEGIN
       BEGIN
         --Valida si el usuario puede ser calculado
         MI_ETAPA := 'Validación de usuarios';
         MI_VALIDACALCULOUSUARIO:= FC_VALIDAUSAURIOCALC(UN_COMPANIA    => UN_COMPANIA,
                                                        UN_RSUSUARIO   => MI_RSUSUARIOS(i),
                                                        UN_PARAMETROS  => MI_PARAMETROS,
                                                        UN_CODERROR    => MI_CODERROR,
                                                        UN_USUARIO     => UN_USUARIO) ;

         --Valida si existen tarifas para el usuario a calcular
         MI_RSTARIFAS :=NULL;

         MI_RSTARIFAS := FC_CARGATARIFAS(UN_COMPANIA             => UN_COMPANIA
                                        ,UN_ANO                  => MI_RSUSUARIOS(i).ANO
                                        ,UN_PERIODO              => MI_RSUSUARIOS(i).PERIODO
                                        ,UN_USO                  => MI_RSUSUARIOS(i).USO
                                        ,UN_ESTRATO              => MI_RSUSUARIOS(i).ESTRATO
                                        ,UN_VALIDATARIFA         => MI_VALIDACALCULOUSUARIO );

         IF INSTR(MI_VALIDACALCULOUSUARIO,'No existe tarifa para el uso')>0 THEN
            MI_CODERROR := 5;
         END IF;

         IF MI_VALIDACALCULOUSUARIO ='C' THEN --Estado que permite calcular el usuario correctamente.
             --Limpia en cada vuelta los conceptos de cada usuario
             MI_FACTURADOACT.DELETE;

             IF MI_PARAMETROS('RECARGOSEGFECHADESDETARIFA').VALOR ='SI' THEN
                 MI_TASARECARGO := MI_RSTARIFAS.PORCENTAJEFECHA2;
             END IF;


             IF NVL(MI_RSUSUARIOS(i).BANCOPERANTERIOR,'') <> '' AND MI_RSUSUARIOS(i).PERIODOSATRASO <>0 THEN --pendiente y no tiene saldos de financiables
                 MI_RSUSUARIOS(i).PERIODOSATRASO :=  0;
             END IF;
             IF MI_RSUSUARIOS(i).NUMERODIGITOS =0 AND (MI_RSUSUARIOS(i).LECTURA + MI_RSUSUARIOS(i).LECTURA1 + MI_RSUSUARIOS(i).LECTURA2) <>0 THEN
                 MI_RSUSUARIOS(i).NUMERODIGITOS := 4;
             END IF;

             MI_RSUSUARIOS(i).INDCALCULADO := -1;

             MI_ETAPA := 'Validación faturados y deuda por concepto';
             MI_DEUDA     := 0;
             MI_DEUDAASEO := 0;

             <<DEUDA>>
             FOR MI_RSFACTURADOS IN
             (
              SELECT CONCEPTO, VALOR_FACTURADO , DEUDA, VALOR_FACTURADOANT, DEUDAANT, VALOR_FACTURADOIN, DEUDAIN
              FROM   SP_FACTURADO
              WHERE  COMPANIA    = UN_COMPANIA
                AND  CICLO       = UN_INTCICLO
                AND  CODIGORUTA  = MI_RSUSUARIOS(i).CODIGORUTA
                AND  ANO         = MI_RSUSUARIOS(i).ANO
                AND  PERIODO     = MI_RSUSUARIOS(i).PERIODO
             )
             LOOP
                 MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).DEUDA := MI_RSFACTURADOS.DEUDA;
                 MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).FACTURADOANT := MI_RSFACTURADOS.VALOR_FACTURADOANT;
                 MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).DEUDAANT := MI_RSFACTURADOS.DEUDAANT;
                 MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).FACTURADOIN := MI_RSFACTURADOS.VALOR_FACTURADOIN;
                 MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).DEUDAIN := MI_RSFACTURADOS.DEUDAIN;

                 MI_C := MI_RSFACTURADOS.CONCEPTO;

                 MI_ETAPA := 'Conceptos manuales';
                 IF INSTR(MI_PARAMETROS('CMANUAL').VALOR ,MI_RSFACTURADOS.CONCEPTO) >0 THEN
                    MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).FACTURADO := MI_RSFACTURADOS.VALOR_FACTURADO;
                 ELSE
                    IF MI_RSFACTURADOS.VALOR_FACTURADO <>0 THEN
                        IF MI_RSFACTURADOS.CONCEPTO <=50 THEN
                            IF MI_RSUSUARIOS(i).PERIODOSNOCOBROFIN <= 50 THEN
                                MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).FACTURADO := MI_CONCEPTOS(MI_RSFACTURADOS.CONCEPTO).VALOR;
                            END IF;
                        ELSE
                            MI_FACTURADOACT(MI_RSFACTURADOS.CONCEPTO).FACTURADO := 0;
                        END IF;
                    END IF;
                 END IF;


                 IF (MI_C > 0 AND MI_C <= 50) OR (MI_C >= MI_NUMCONCEPTOS - 3) OR (MI_C >= 201 AND MI_C <= 220) THEN
                    MI_DEUDA := MI_DEUDA + MI_RSFACTURADOS.DEUDA;
                    IF (MI_C >= 20 AND MI_C <= 22 OR MI_C = 3 OR MI_C = 23 OR MI_C = MI_NUMCONCEPTOS - 2 OR MI_C = 48) OR (MI_C >= 201 AND MI_C <= 207) THEN
                        MI_DEUDAASEO := MI_RSFACTURADOS.DEUDA;
                    END IF;

                    IF MI_C = 18 OR MI_C = 31 THEN
                        MI_DEUDAALUMBRADO := MI_RSFACTURADOS.DEUDA;
                    END IF;
                 END IF;

             END LOOP DEUDA;

             MI_ETAPA := 'Deuda inicial concepto 25';
             IF MI_RSUSUARIOS(i).DEUDAINICIAL <> 0 AND MI_RSUSUARIOS(i).ANOENTRADANUEVOUSUARIO = MI_RSUSUARIOS(i).ANO
              AND MI_RSUSUARIOS(i).PERENTRADANUEVOUSUARIO = MI_RSUSUARIOS(i).PERIODO THEN

                 MI_FACTURADOACT(25).DEUDA := MI_RSUSUARIOS(i).DEUDAINICIAL;
                 MI_FACTURADOACT(25).DEUDAANT := MI_RSUSUARIOS(i).DEUDAINICIAL;
                 MI_DEUDA := MI_DEUDA + MI_RSUSUARIOS(i).DEUDAINICIAL;
             END IF;

             --InicializaVardeCal
             MI_RSUSUARIOS(i).CRITICACONSUMO := 'N';
             MI_RSUSUARIOS(i).COSTOCONSUMOAC := 0;
             MI_RSUSUARIOS(i).COSTOCONSUMOAL := 0;
             MI_RSUSUARIOS(i).COSTOREALASEO := 0;
             MI_RSUSUARIOS(i).DEUDAASEO := 0;
             MI_RSUSUARIOS(i).TOTALACUEDUCTO := 0;
             MI_RSUSUARIOS(i).TOTALALCANTARILLADO := 0;
             MI_RSUSUARIOS(i).TOTALASEO := 0;
             MI_RSUSUARIOS(i).SUBALCSINMEDICION := 0;
             MI_RSUSUARIOS(i).SOBREALCSINMEDICION := 0;
             MI_RSUSUARIOS(i).FACTURA := 0;
             MI_RSUSUARIOS(i).CALCULODESH := 0;
             MI_RSUSUARIOS(i).RECARGOACUEDUCTO := 0;
             MI_RSUSUARIOS(i).RECARGOALC := 0;
             MI_RSUSUARIOS(i).RECARGOALUMBRADO := 0;
             MI_RSUSUARIOS(i).RECARGOASEO := 0;
             MI_RSUSUARIOS(i).AJUSTEDECENAASEO := 0;
             MI_RSUSUARIOS(i).TOTFACTURAASEO := 0;
             MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2 := 0;


             MI_ETAPA := 'Validación consumos manuales';
             MI_CONSUMOMANUSU :=0;
             IF ( MI_PARAMETROS('CONSUMOMANUAL').VALOR ='SI' AND MI_RSUSUARIOS(i).CONSUMOMANUAL <>0)
              OR (MI_PARAMETROS('PROCESOMICRO').VALOR ='SI' AND MI_RSUSUARIOS(i).CONSUMOMANUAL <>0) THEN

               MI_CONSUMOMANUSU :=-1;

             END IF;

             MI_CONSUMOMICRO :=0;
             IF MI_PARAMETROS('AUTOMICRO').VALOR ='SI' AND MI_RSUSUARIOS(i).PORMICRO <>0 THEN
                 MI_CONSUMOMICRO :=-1;
             END IF;

             IF MI_RSUSUARIOS(i).ACUEDUCTO <> 0 AND MI_APLICADESCUENTO <> 0 THEN
                  IF MI_PARAMETROS('CALDESCUENTOCONCEPTO').VALOR ='SI' AND MI_RSUSUARIOS(i).SINDESCUENTO =0 THEN
                      MI_CALDESCCONCEPTOUSU := -1;
                  END IF;
             END IF;

             --Establece el tipo de cálculo
             MI_ETAPA := 'Tipo de cálculo';
             MI_TIPOCALCULO := FC_TIPOCALCULO(UN_COMPANIA        => UN_COMPANIA,
                                              UN_RSUSUARIO       => MI_RSUSUARIOS(i),
                                              UN_CONSUMOMANUSU   => MI_CONSUMOMANUSU,
                                              UN_PARAMETROS      => MI_PARAMETROS);


             --Evalua si tiene proceso de desviación y necesita guardar la historia
             MI_ETAPA := 'Validación historia desviación';
             MI_APLICAHISTDESVIACION :=0;
             IF (MI_PARAMETROS('FACTSITIO').VALOR ='SI' AND MI_PARAMETROS('DESVIAENSITIO').VALOR ='SI' OR MI_PARAMETROS('AUTODESVIA').VALOR ='SI')  THEN
                 IF MI_RSUSUARIOS(i).DESVIOSIGNIFICATIVO <>0 OR MI_RSUSUARIOS(i).METROSDESVIACION <>0 THEN
                     MI_APLICAHISTDESVIACION :=-1;
                 END IF;
             END IF;

             IF MI_PARAMETROS('RES151').VALOR ='SI' AND MI_PARAMETROS('CALRES151').VALOR ='SI' AND MI_RSUSUARIOS(i).DESVIOSIGNIFICATIVO <>0 THEN
                 MI_APLICAHISTDESVIACION :=-1;

                 IF ((MI_PARAMETROS('FACTSITIO').VALOR = 'SI' AND MI_PARAMETROS('DESVIAENSITIO').VALOR ='SI') OR (MI_PARAMETROS('COBRARDESVPERACT').VALOR ='SI' AND MI_PARAMETROS('FACTSITIO').VALOR='NO')  )
                     AND MI_RSUSUARIOS(i).METROSDESVIACION <>0 AND MI_RSUSUARIOS(i).DESVIOSIGNIFICATIVO <>0  THEN
                     --No aplica historia desviación dado que esta se crea cuando suben el plano de sitio
                     MI_APLICAHISTDESVIACION := 0;
                 END IF;
             END IF;

             MI_MTL := NVL(PCK_SERVICIOS_PUBLICOS_COM5.FC_NUMERO_MULTIUSUARIOS(UN_COMPANIA    =>  UN_COMPANIA
                                                                              ,UN_CODIGORUTA  =>  MI_RSUSUARIOS(i).CODIGORUTA ),0);

             IF MI_APLICAHISTDESVIACION =-1 THEN   --Si tiene que armar la historia de desviacion con el consumo real
                 --Calculo con consumo real paar usuarios con desviaciones
                 --Cuando se cobra desviacion se guarda una historia de como quedaria y como queda con lo real cobrado
                 MI_ETAPA := 'Desviaciones';
                 MI_CONSUMO := FC_CONSUMOS( UN_COMPANIA          => UN_COMPANIA
                                           ,UN_RSUSUARIO         => MI_RSUSUARIOS(i)
                                           ,UN_TIPOCALCULO       => MI_TIPOCALCULO
                                           ,UN_CONSUMOMANUSU     => MI_CONSUMOMANUSU
                                           ,UN_PARAMETROS        => MI_PARAMETROS
                                           ,UN_MENSAJEFRAUDE     => MI_VALIDACALCULOUSUARIO
                                           ,UN_ENSERIE           => UN_ENSERIE
                                           ,UN_RSTARIFA          => MI_RSTARIFAS
                                           ,UN_CONSUMODEFINITIVO => 0);
                 --Calculo Acu y Alc
                 MI_ETAPA := 'Cálculo Acueducto desviaciones';
                 MI_TOTALFACT := FC_CALCULOACUEDUCTO (UN_COMPANIA     => UN_COMPANIA
                                                     ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                                     ,UN_RSTARIFA     => MI_RSTARIFAS
                                                     ,UN_TIPOCALCULO  => MI_TIPOCALCULO
                                                     ,UN_PARAMETROS   => MI_PARAMETROS
                                                     ,UN_FACTURADOACT => MI_FACTURADOACT);

                 MI_ETAPA := 'Cálculo Alcantarillado desviaciones';
                 MI_TOTALFACT := MI_TOTALFACT +
                                  FC_CALCULOALCANTARILLADO(UN_COMPANIA     => UN_COMPANIA
                                                          ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                                          ,UN_RSTARIFA     => MI_RSTARIFAS
                                                          ,UN_TIPOCALCULO  => MI_TIPOCALCULO
                                                          ,UN_PARAMETROS   => MI_PARAMETROS
                                                          ,UN_FACTURADOACT => MI_FACTURADOACT);

                 MI_ETAPA := 'Historia desviaciones';
                 PR_ARMAHISTORIADESVIACION (UN_COMPANIA      => UN_COMPANIA,
                                            UN_RSUSUARIO     => MI_RSUSUARIOS(i),
                                            UN_FACTURADOACT  => MI_FACTURADOACT,
                                            UN_FACTURADO     => 0,
                                            UN_BOLPORMICRO   => MI_CONSUMOMICRO,
                                            UN_BOLCONMANUAL  => MI_CONSUMOMANUSU,
                                            UN_PARAMETROS    => MI_PARAMETROS,
                                            UN_USUARIO       => UN_USUARIO);

             END IF;

             MI_ETAPA := 'Cálculo Consumos';
             MI_CONSUMO := FC_CONSUMOS( UN_COMPANIA          => UN_COMPANIA
                                       ,UN_RSUSUARIO         => MI_RSUSUARIOS(i)
                                       ,UN_TIPOCALCULO       => MI_TIPOCALCULO
                                       ,UN_CONSUMOMANUSU     => MI_CONSUMOMANUSU
                                       ,UN_PARAMETROS        => MI_PARAMETROS
                                       ,UN_MENSAJEFRAUDE     => MI_VALIDACALCULOUSUARIO
                                       ,UN_ENSERIE           => UN_ENSERIE
                                       ,UN_RSTARIFA          => MI_RSTARIFAS
                                       ,UN_CONSUMODEFINITIVO => -1);

             IF MI_MTL > 0 THEN  --Si tiene multiusuarios
                  MI_VALIDACALCULOUSUARIO := 'C';
                  MI_TOTALFACT :=0;
                  MI_TOTALFACT := FC_CALCULOMULTIUSUARIOS(UN_COMPANIA       => UN_COMPANIA
                                                        ,UN_RSUSUARIO      => MI_RSUSUARIOS(i)
                                                        ,UN_PARAMETROS     => MI_PARAMETROS
                                                        ,UN_FACTURADOACT   => MI_FACTURADOACT
                                                        ,UN_VALIDATARIFA   => MI_VALIDACALCULOUSUARIO
                                                        ,UN_USUARIO        => UN_USUARIO);

                  IF UN_ENSERIE <>0 AND MI_VALIDACALCULOUSUARIO <> 'C' THEN
                      MI_CODERROR := 5;
                      PR_INSERTAERRORCALCULO(UN_COMPANIA           => UN_COMPANIA
                                            ,UN_CODIGORUTA         => MI_RSUSUARIOS(i).CODIGORUTA
                                            ,UN_CICLO              => MI_RSUSUARIOS(i).CICLO
                                            ,UN_CODIGOERRORINTERNO => MI_CODERROR
                                            ,UN_MENSAJE            => MI_VALIDACALCULOUSUARIO);
                  END IF;
             ELSE

                 --Calculo definitivo
                 --Calculo Acu y Alc
                 MI_ETAPA := 'Cálculo Acueducto';
                 MI_TOTALFACT :=0;
                 MI_TOTALFACT := FC_CALCULOACUEDUCTO (UN_COMPANIA     => UN_COMPANIA
                                                     ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                                     ,UN_RSTARIFA     => MI_RSTARIFAS
                                                     ,UN_TIPOCALCULO  => MI_TIPOCALCULO
                                                     ,UN_PARAMETROS   => MI_PARAMETROS
                                                     ,UN_FACTURADOACT => MI_FACTURADOACT);

                 MI_ETAPA := 'Cálculo Alcantarillado';
                 MI_TOTALFACT := MI_TOTALFACT +
                                  FC_CALCULOALCANTARILLADO(UN_COMPANIA     => UN_COMPANIA
                                                          ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                                          ,UN_RSTARIFA     => MI_RSTARIFAS
                                                          ,UN_TIPOCALCULO  => MI_TIPOCALCULO
                                                          ,UN_PARAMETROS   => MI_PARAMETROS
                                                          ,UN_FACTURADOACT => MI_FACTURADOACT);

                 MI_ETAPA := 'Cálculo Aseo';
                 MI_TOTALFACT := MI_TOTALFACT +
                                 FC_CALCULOASEO(UN_COMPANIA     => UN_COMPANIA
                                               ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                               ,UN_RSTARIFA     => MI_RSTARIFAS
                                               ,UN_PARAMETROS   => MI_PARAMETROS
                                               ,UN_FACTACT      => MI_FACTURADOACT
                                               ,UN_USUARIO      => UN_USUARIO);
             END IF;

             IF MI_APLICAHISTDESVIACION =-1   THEN
                 MI_ETAPA := 'Historia desviaciones definitiva';
                 PR_ARMAHISTORIADESVIACION (UN_COMPANIA      => UN_COMPANIA,
                                            UN_RSUSUARIO     => MI_RSUSUARIOS(i),
                                            UN_FACTURADOACT  => MI_FACTURADOACT,
                                            UN_FACTURADO     => -1,
                                            UN_BOLPORMICRO   => MI_CONSUMOMICRO,
                                            UN_BOLCONMANUAL  => MI_CONSUMOMANUSU,
                                            UN_PARAMETROS    => MI_PARAMETROS,
                                            UN_USUARIO       => UN_USUARIO);
             END IF;

             MI_RSUSUARIOS(i).SUBSIDIO    := (MI_RSUSUARIOS(i).SUBACUEDUCTO + MI_RSUSUARIOS(i).SUBALCANTARILLADO + MI_RSUSUARIOS(i).SUBASEO);
             MI_RSUSUARIOS(i).SOBREPRECIO := (MI_RSUSUARIOS(i).SOBREACUEDUCTO + MI_RSUSUARIOS(i).SOBREALCANTARILLADO + MI_RSUSUARIOS(i).SOBREASEO);

             MI_ETAPA := 'Cáculo Desincentivo';
             IF MI_PARAMETROS('RES493').VALOR ='SI' AND MI_CONCEPTOS.EXISTS(44) THEN
                IF MI_PARAMETROS('PROMDES').VALOR ='NO' THEN
                    IF MI_RSTARIFAS.USOSUPERSERVICIOS = '1' AND MI_RSUSUARIOS(i).LECTURA <> MI_RSUSUARIOS(i).LECTURA1   AND MI_RSUSUARIOS(i).INQUILINATOS = 0 AND MI_RSUSUARIOS(i).HOGAR_COMUNITARIO = 0 THEN
                        IF (MI_RSUSUARIOS(i).LECTURA - MI_RSUSUARIOS(i).LECTURA1) > TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR) THEN
                            MI_FACTURADOACT(44).FACTURADO := ((MI_RSUSUARIOS(i).LECTURA - MI_RSUSUARIOS(i).LECTURA1) - TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR)) *
                                                             TO_NUMBER(MI_PARAMETROS('VALORTARIFAPLENA493').VALOR) ;
                        ELSE
                            MI_FACTURADOACT(44).FACTURADO := 0;
                        END IF;
                        MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(44).FACTURADO;
                    END IF;

                ELSE
                     IF MI_RSTARIFAS.USOSUPERSERVICIOS = '1' AND MI_RSUSUARIOS(i).TIPOCALCULO = 'P' AND MI_RSUSUARIOS(i).INQUILINATOS = 0 AND MI_RSUSUARIOS(i).HOGAR_COMUNITARIO = 0 THEN
                        IF MI_RSUSUARIOS(i).CONSUMOPROM > TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR) THEN
                            MI_FACTURADOACT(44).FACTURADO := ((MI_RSUSUARIOS(i).CONSUMOPROM + MI_RSUSUARIOS(i).METROSDESVIACION ) - TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR)  ) *
                                                                TO_NUMBER(MI_PARAMETROS('VALORTARIFAPLENA493').VALOR);
                        END IF;
                     ELSIF MI_RSTARIFAS.USOSUPERSERVICIOS = '1' AND MI_RSUSUARIOS(i).LECTURA <> MI_RSUSUARIOS(i).LECTURA1 AND MI_RSUSUARIOS(i).INQUILINATOS = 0 AND MI_RSUSUARIOS(i).HOGAR_COMUNITARIO = 0 THEN
                         IF MI_RSUSUARIOS(i).CONSUMO > TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR) THEN
                            MI_FACTURADOACT(44).FACTURADO :=  (MI_RSUSUARIOS(i).CONSUMO - TO_NUMBER(MI_PARAMETROS('METRAJEDESINCENTIVO').VALOR) ) *
                                                              TO_NUMBER(MI_PARAMETROS('VALORTARIFAPLENA493').VALOR);
                         ELSE
                            MI_FACTURADOACT(44).FACTURADO := 0;
                         END IF;
                         MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(44).FACTURADO;
                     END IF;
                END IF;

                IF MI_PARAMETROS('CONTROLRES493').VALOR ='SI' AND MI_RSUSUARIOS(i).APLICA_RES493 =0 THEN
                    --Para solo cobrar a los usuarios que tengan indicador y asi permitir la nivelación de los ciclos anterioremente facturados.
                    MI_FACTURADOACT(44).FACTURADO := 0;
                END IF;
             END IF;--Fin Desincentivo

             --Concepto alumbrado.
             IF MI_PARAMETROS('FALUMBRADO').VALOR ='SI' AND MI_RSUSUARIOS(i).PERIODOSNOCOBROFAC <=0 AND MI_RSUSUARIOS(i).ALUMBRADO <>0 THEN
                  MI_ETAPA := 'Concepto Alumbrado';
                  BEGIN
                      SELECT  TARIFAALUMBRADO
                      INTO    MI_RSUSUARIOS(i).FIJOALUMBRADO
                      FROM    SP_TARIFAS
                      WHERE   COMPANIA = UN_COMPANIA
                        AND   ANO      = MI_RSUSUARIOS(i).ANO
                        AND   PERIODO  = MI_RSUSUARIOS(i).PERIODO
                        AND   USO      = CASE WHEN MI_PARAMETROS('BLSEPARARALUMBRADO').VALOR ='NO' THEN
                                                 MI_RSUSUARIOS(i).USO
                                              ELSE
                                                 MI_PARAMETROS('STRUSOALUMBRADO').VALOR
                                         END
                        AND   ESTRATO  = MI_RSUSUARIOS(i).ESTRATOALUMBRADO;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_RSUSUARIOS(i).FIJOALUMBRADO := 0;
                  END;
                  MI_FACTURADOACT(18).FACTURADO := MI_RSUSUARIOS(i).FIJOALUMBRADO;
                  MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(18).FACTURADO;
             END IF;

             MI_ETAPA := 'Cálculo financiables';
             IF MI_RSUSUARIOS(i).PERIODOSNOCOBROFIN <=0 THEN
                 MI_TOTALFACT := MI_TOTALFACT + FC_CALCULOFINANCIABLES(UN_COMPANIA      => UN_COMPANIA
                                                                      ,UN_INTCICLO      => UN_INTCICLO
                                                                      ,UN_CODIGORUTA    => MI_RSUSUARIOS(i).CODIGORUTA
                                                                      ,UN_ANIO          => MI_RSUSUARIOS(i).ANO
                                                                      ,UN_PERIODO       => MI_RSUSUARIOS(i).PERIODO
                                                                      ,UN_FACTURADOACT  => MI_FACTURADOACT
                                                                      ,UN_PARAMETROS    => MI_PARAMETROS
                                                                      ,UN_CONCEPTOS     => MI_CONCEPTOS
                                                                      ,UN_NUMEROFINAN   => MI_NUMSALDOS);

             END IF;
             --Se pasa ciclo de conceptos a ciclo de deuda en tabla facturado
             MI_C := MI_NUMCONCEPTOS;
             MI_FACTURADOACT(MI_C).FACTURADO := MI_DEUDA;
             MI_TOTALFACT := MI_TOTALFACT + MI_DEUDA;
             MI_RSUSUARIOS(i).TOTALDEUDA := MI_DEUDA;
             MI_RSUSUARIOS(i).DEUDAASEO  := MI_DEUDAASEO;

             MI_ETAPA := 'Cálculo Recargos';
             MI_TOTALFACT := MI_TOTALFACT +  FC_CALCULORECARGOS(UN_COMPANIA     => UN_COMPANIA
                                                               ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                                               ,UN_FACTURADOACT => MI_FACTURADOACT
                                                               ,UN_PARAMETROS   => MI_PARAMETROS
                                                               ,UN_CONCEPTOS    => MI_CONCEPTOS
                                                               ,UN_RSTARIFA     => MI_RSTARIFAS);

             MI_ETAPA := 'Ajuste al peso empresa aseo independiente';
             IF MI_PARAMETROS('ASEOINDEPENDIENTE').VALOR ='SI' THEN
                --Ajuste al peso a la factura de aseo.
                MI_FACTURADOACT(MI_NUMCONCEPTOS -2).FACTURADO := FC_AJUSTEALPESO( UN_VALOR              => MI_RSUSUARIOS(i).TOTFACTURAASEO
                                                                                 ,UN_AJUSTEDECENA       => TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR)
                                                                                 ,UN_REDONDEOPORENCIMA  => MI_PARAMETROS('BLREDONDEOPORENCIMA').VALOR );

                MI_RSUSUARIOS(i).AJUSTEDECENAASEO := MI_FACTURADOACT(MI_NUMCONCEPTOS -2).FACTURADO;
             ELSE
                MI_RSUSUARIOS(i).AJUSTEDECENAASEO := 0;
             END IF;


             IF MI_CONCEPTOS.EXISTS(TO_NUMBER(MI_PARAMETROS('CNDESCUENTOCONTROLADO').VALOR)) THEN --Valida si existe el concepto del descuento.
                MI_ETAPA := 'Descuentos por concepto';
                MI_C := TO_NUMBER(MI_PARAMETROS('CNDESCUENTOCONTROLADO').VALOR);

                MI_FACTURADOACT(MI_C).FACTURADO := FC_DESCUENTOCONCEPTO( UN_COMPANIA      => UN_COMPANIA
                                                                        ,UN_INTCICLO      => UN_INTCICLO
                                                                        ,UN_CODIGORUTA    => MI_RSUSUARIOS(i).CODIGORUTA
                                                                        ,UN_ANIO          => MI_RSUSUARIOS(i).ANO
                                                                        ,UN_PERIODO       => MI_RSUSUARIOS(i).PERIODO
                                                                        ,UN_SOLOELIMINA   => CASE WHEN MI_CALDESCCONCEPTOUSU =0 THEN -1 ELSE 0 END
                                                                        ,UN_CONCEPTOS     => MI_CONCEPTOS
                                                                        ,UN_FACTURADOACT  => MI_FACTURADOACT);


                IF MI_PARAMETROS('CALDESCUENTOCONTROLADO').VALOR = 'SI' AND MI_PARAMETROS('PERMITEDESCUENTOTOTFACTURA').VALOR = 'NO' THEN
                  MI_ETAPA := 'Descuentos Controlado';
                  MI_FACTURADOACT(MI_C).FACTURADO := FC_DESCUENTOCONTROL(UN_COMPANIADES   => UN_COMPANIA
                                                                        ,UN_INTCICLODES   => UN_INTCICLO
                                                                        ,UN_CODIGORUTADES => MI_RSUSUARIOS(i).CODIGORUTA
                                                                        ,UN_ANIODES       => MI_RSUSUARIOS(i).ANO
                                                                        ,UN_PERIODODES    => MI_RSUSUARIOS(i).PERIODO
                                                                        ,UN_FACTURADOACT  => MI_FACTURADOACT
                                                                        ,UN_USUARIO       => UN_USUARIO);
                  --para controlar el cobro del desincentivo por usuario
                  IF MI_RSUSUARIOS(i).INCENTIVO = 0 THEN
                      MI_FACTURADOACT(MI_C).FACTURADO := 0;
                  END IF;
                END IF;
             END IF;

             MI_ETAPA := 'Total Abonos';
             BEGIN
                SELECT SUM(VALOR) VALOR
                INTO   MI_FACTURADOACT(49).FACTURADO
                FROM   SP_ABONOS
                WHERE  COMPANIA   = UN_COMPANIA
                  AND  CICLO      = UN_INTCICLO
                  AND  CODIGORUTA = MI_RSUSUARIOS(i).CODIGORUTA
                  AND  ANO        = MI_RSUSUARIOS(i).ANO
                  AND  PERIODO    = MI_RSUSUARIOS(i).PERIODO
                HAVING SUM(VALOR) >0;

                EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_FACTURADOACT(49).FACTURADO := 0;
             END;

             MI_ETAPA := 'Total de la factura';
             MI_TOTALFACT := 0; --Se realiza esto debido a los conceptos manuales
             FOR i IN MI_FACTURADOACT.FIRST .. MI_FACTURADOACT.LAST
             LOOP
                 IF MI_FACTURADOACT.EXISTS(i) AND i <> 49 AND ((i > 0 AND i <= 50) OR (i >= 201 AND i <= 220) OR (i > 245 AND i < 251)) THEN
                    MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(i).FACTURADO;
                 END IF;
             END LOOP;

             MI_FACTURADOACT(49).FACTURADO := -1 * MI_FACTURADOACT(49).FACTURADO;
             MI_RSUSUARIOS(i).ABONOS := MI_FACTURADOACT(49).FACTURADO;
             MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(49).FACTURADO;


             MI_ETAPA := 'Descuento controlado al total de la factura';
             MI_C := TO_NUMBER(MI_PARAMETROS('CNDESCUENTOCONTROLADO').VALOR);
             IF MI_CONCEPTOS.EXISTS(MI_C) AND MI_PARAMETROS('CALDESCUENTOCONTROLADO').VALOR = 'SI' AND MI_PARAMETROS('PERMITEDESCUENTOTOTFACTURA').VALOR ='SI' THEN
                MI_FACTURADOACT(MI_C).FACTURADO := FC_DESCUENTOCONTROLTOTFACTURA(UN_COMPANIADEST    => UN_COMPANIA
                                                                                ,UN_INTCICLODEST    => UN_INTCICLO
                                                                                ,UN_CODIGORUTADEST  => MI_RSUSUARIOS(i).CODIGORUTA
                                                                                ,UN_ANIODEST        => MI_RSUSUARIOS(i).ANO
                                                                                ,UN_PERIODODEST     => MI_RSUSUARIOS(i).PERIODO
                                                                                ,UN_FACTURADOACTT   => MI_FACTURADOACT
                                                                                ,UN_TOTALFACTURA    => MI_TOTALFACT
                                                                                ,UN_USUARIO         => UN_USUARIO);

                MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(MI_C).FACTURADO;
             END IF;

             MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := TRUNC(MI_TOTALFACT * (1 + MI_TASARECARGO / 100) + 0.5);

             IF MI_RSUSUARIOS(i).INDFINANINICIAL <>0 THEN
                --En access se tomaba el valor del parámetro de función ValorTotal que es opcional, pero este nunca se enviaba
                MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL :=0;
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := 0;
             END IF;

             IF TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR) <> 0 THEN
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := TRUNC(MI_RSUSUARIOS(i).TOTFACTURAPAGO2 / TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR) + 0.501) *
                                                    TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR);
             END IF;

             MI_ETAPA := 'Recargo segunda fecha en periodo siguiente';
             IF MI_PARAMETROS('RECARGO').VALOR = 'SI' THEN
                IF NVL(MI_RSUSUARIOS(i).BANCOPERANTERIOR, '') <> '' AND MI_RSUSUARIOS(i).NOFECHAPAGOPERANTERIOR = '2' THEN
                    MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO := TRUNC(MI_RSUSUARIOS(i).TOTFACTURAPERANTERIOR * (MI_TASARECARGO / 100) + 0.5 );
                ELSE
                    MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO := 0;
                END IF;
             ELSE
                --MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO := MI_RSUSUARIOS(i).TOTFACTURAPAGO2 - MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL;
                MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO := 0;
             END IF;

             IF MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO < 0 THEN
                MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO := 0;
             END IF;
             MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(MI_NUMCONCEPTOS -3).FACTURADO;

             MI_ETAPA := 'Ajuste al peso';
             IF (MI_PARAMETROS('FINSITU').VALOR <> 'SI' OR (MI_PARAMETROS('FINSITU').VALOR = 'SI' AND NVL(MI_RSUSUARIOS(i).FIMM,'') = 'F')) THEN
                MI_FACTURADOACT(MI_NUMCONCEPTOS - 1).FACTURADO := FC_AJUSTEALPESO(UN_VALOR              => MI_TOTALFACT
                                                                                 ,UN_AJUSTEDECENA       => TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR)
                                                                                 ,UN_REDONDEOPORENCIMA  => MI_PARAMETROS('BLREDONDEOPORENCIMA').VALOR );

                MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(MI_NUMCONCEPTOS -1).FACTURADO;
             END IF;

             MI_ETAPA := 'Factura segundo pago';
             MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL := MI_TOTALFACT;
             MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := TRUNC(MI_TOTALFACT * (1 + MI_TASARECARGO / 100) + 0.5);

             IF TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR) <>0 THEN
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := TRUNC(MI_RSUSUARIOS(i).TOTFACTURAPAGO2 / TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR) + 0.501) *
                                                    TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR);
             END IF;

             IF MI_PARAMETROS('RECARGO').VALOR = 'SI' THEN
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL;
             ELSE
                IF MI_RSTARIFAS.RECARGOFIJO <> 0 THEN
                    MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL + MI_RSTARIFAS.RECARGOFIJO;
                    MI_FACTURADOACT(MI_NUMCONCEPTOS - 3).FACTURADO := MI_RSUSUARIOS(i).TOTFACTURAPAGO2 - MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL;
                END IF;
             END IF;

             MI_ETAPA := 'Segundo pago de aseo';
             IF NOT (MI_PARAMETROS('PROCTERCERIZADO').VALOR ='SI' AND NVL(MI_RSUSUARIOS(i).EMPRESAASEOEXT, '') <> '' AND MI_RSUSUARIOS(i).PINTADEUDATERCE =0 )THEN
                MI_RSUSUARIOS(i).TOTFACTURAASEO := MI_RSUSUARIOS(i).TOTFACTURAASEO + MI_RSUSUARIOS(i).DEUDAASEO + MI_FACTURADOACT(MI_NUMCONCEPTOS -2).FACTURADO;
                MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2 := TRUNC(MI_RSUSUARIOS(i).TOTFACTURAASEO * (1 + MI_TASARECARGO / 100) + 0.5);
                IF MI_PARAMETROS('ASEOINDEPENDIENTE').VALOR ='SI' THEN
                   MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2 := FC_AJUSTEALPESO(UN_VALOR              => MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2
                                                                          ,UN_AJUSTEDECENA       => TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR)
                                                                          ,UN_REDONDEOPORENCIMA  => MI_PARAMETROS('BLREDONDEOPORENCIMA').VALOR );
                END IF;
             ELSE
                MI_RSUSUARIOS(i).TOTFACTURAASEO := 0;
                MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2 := 0;
             END IF;

             MI_ETAPA := 'Cálculo productividad';
             IF MI_PARAMETROS('CALPRODUCTIVIDAD').VALOR ='SI' AND MI_RSUSUARIOS(i).TOTFACTURAASEO > 0 THEN
                MI_C := TO_NUMBER(MI_PARAMETROS('CNPRODUCTIVIDAD').VALOR);
                IF MI_CONCEPTOS.EXISTS(MI_C) THEN
                    MI_VALPRODUC := MI_FACTURADOACT(3).FACTURADO + MI_FACTURADOACT(20).FACTURADO + MI_FACTURADOACT(21).FACTURADO +
                                    MI_FACTURADOACT(22).FACTURADO + MI_FACTURADOACT(23).FACTURADO + MI_FACTURADOACT(48).FACTURADO +
                                    MI_FACTURADOACT(46).FACTURADO;

                    MI_FACTURADOACT(MI_C).FACTURADO := FC_CALCULOPRODUCTIVIDAD (UN_COMPANIA   => UN_COMPANIA
                                                                               ,UN_STRUSUARIO => MI_RSUSUARIOS(i).CODIGORUTA
                                                                               ,UN_INTCICLO   => UN_INTCICLO
                                                                               ,UN_INTANO     => MI_RSUSUARIOS(i).ANO
                                                                               ,UN_STRPERIODO => MI_RSUSUARIOS(i).PERIODO
                                                                               ,UN_TOTASEO    => MI_VALPRODUC
                                                                               ,UN_USUARIO    => UN_USUARIO);

                    MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(MI_C).FACTURADO - MI_FACTURADOACT(MI_NUMCONCEPTOS -1).FACTURADO;
                    MI_ETAPA := 'Ajuste al peso productividad';
                    MI_FACTURADOACT(MI_NUMCONCEPTOS -1).FACTURADO := FC_AJUSTEALPESO(UN_VALOR              => MI_TOTALFACT
                                                                                 ,UN_AJUSTEDECENA       => TO_NUMBER(MI_PARAMETROS('AJUSDECENA').VALOR)
                                                                                 ,UN_REDONDEOPORENCIMA  => MI_PARAMETROS('BLREDONDEOPORENCIMA').VALOR );

                    MI_TOTALFACT := MI_TOTALFACT + MI_FACTURADOACT(MI_NUMCONCEPTOS -1).FACTURADO;
                    MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL := MI_TOTALFACT;
                END IF;
             END IF;

             MI_ETAPA := 'Periodos de atraso para usuarios nuevos';
             IF MI_FACTURADOACT(MI_NUMCONCEPTOS).FACTURADO = 0 AND MI_NUMSALDOS = 0 AND
              NOT (MI_RSUSUARIOS(i).ANOENTRADANUEVOUSUARIO = MI_RSUSUARIOS(i).ANO AND MI_RSUSUARIOS(i).PERENTRADANUEVOUSUARIO = MI_RSUSUARIOS(i).PERIODO) THEN
                  MI_RSUSUARIOS(i).PERIODOSATRASO := 0;
             END IF;

             IF MI_RSUSUARIOS(i).FIMM <> 'F' THEN
                MI_RSUSUARIOS(i).FIMM := ' ';
             END IF;

             MI_ETAPA := 'Totales alumbrado';
             MI_RSUSUARIOS(i).TOTALALUMBRADO := CASE WHEN MI_FACTURADOACT.EXISTS(18) THEN MI_FACTURADOACT(18).FACTURADO ELSE 0 END;
             MI_RSUSUARIOS(i).DEUDAALUMBRADO := MI_DEUDAALUMBRADO;
             MI_RSUSUARIOS(i).TOTALFACTURAALUMBRADO := MI_RSUSUARIOS(i).TOTALALUMBRADO +
                                                       CASE WHEN MI_FACTURADOACT.EXISTS(31) THEN MI_FACTURADOACT(31).FACTURADO ELSE 0 END +
                                                       MI_DEUDAALUMBRADO;

             MI_ETAPA := 'Actualización de conceptos';
             PR_ACTUALIZAFACTURADOS(UN_COMPANIA     => UN_COMPANIA
                                   ,UN_RSUSUARIO    => MI_RSUSUARIOS(i)
                                   ,UN_FACTURADOACT => MI_FACTURADOACT
                                   ,UN_PARAMETROS   => MI_PARAMETROS
                                   ,UN_USUARIO      => UN_USUARIO
                                   ,UN_PRIMERAVEZ   => CASE WHEN MI_PREFACTURANDO <>0 THEN
                                                              -1
                                                            WHEN MI_RSUSUARIOS(i).INDFACTURADO <> 0 THEN
                                                               0
                                                            ELSE
                                                              -1
                                                       END);

             MI_ETAPA := 'Distribución financiables de deuda';
             PR_DISTRIBUIRCUOTA12(UN_COMPANIA   => UN_COMPANIA
                                 ,UN_CODIGORUTA => MI_RSUSUARIOS(i).CODIGORUTA
                                 ,UN_ANIO       => MI_RSUSUARIOS(i).ANO
                                 ,UN_PERIODO    => MI_RSUSUARIOS(i).PERIODO
                                 ,UN_CICLO      => MI_RSUSUARIOS(i).CICLO
                                 ,UN_USUARIO    => UN_USUARIO);


             MI_ETAPA := 'Modificaciones al facturado';
             MI_TOTALFACT := FC_MODIFICACIONESFACTURADO(UN_COMPANIA   => UN_COMPANIA
                                                       ,UN_CODIGORUTA => MI_RSUSUARIOS(i).CODIGORUTA
                                                       ,UN_ANIO       => MI_RSUSUARIOS(i).ANO
                                                       ,UN_PERIODO    => MI_RSUSUARIOS(i).PERIODO
                                                       ,UN_INTCICLO   => MI_RSUSUARIOS(i).CICLO);
             IF MI_TOTALFACT <> 0 THEN
                IF MI_TOTALFACT <0 THEN
                    MI_TOTALFACT := 0;
                END IF;
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := MI_RSUSUARIOS(i).TOTFACTURAPAGO2 - (MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL -  MI_TOTALFACT);
                MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL := MI_TOTALFACT;
             END IF;

             MI_RSUSUARIOS(i).CREDITOABONADO := 0;
             IF MI_RSUSUARIOS(i).NOTACREDITO > 0 THEN
                MI_RSUSUARIOS(i).CREDITOABONADO := FC_DISTRIBUIRSALDOSCREDITO(UN_COMPANIA    => UN_COMPANIA
                                                                             ,UN_CICLO       => MI_RSUSUARIOS(i).CICLO
                                                                             ,UN_CODIGORUTA  => MI_RSUSUARIOS(i).CODIGORUTA
                                                                             ,UN_ANO         => MI_RSUSUARIOS(i).ANO
                                                                             ,UN_PERIODO     => MI_RSUSUARIOS(i).PERIODO
                                                                             ,UN_NOTACREDITO => MI_RSUSUARIOS(i).NOTACREDITO
                                                                             ,UN_FACTURADO   => MI_RSUSUARIOS(i).INDFACTURADO
                                                                             ,UN_USUARIO     => UN_USUARIO);

                MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL := MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL - MI_RSUSUARIOS(i).CREDITOABONADO;
                MI_RSUSUARIOS(i).TOTFACTURAPAGO2 := TRUNC(MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL *
                                                    CASE WHEN MI_PARAMETROS('RECARGO').VALOR = 'NO'
                                                         THEN (1 + MI_TASARECARGO / 100)
                                                         ELSE 1
                                                    END + 0.5);

                PR_VALORDESSALDOCREDITO (UN_COMPANIA   => UN_COMPANIA
                                        ,UN_ANO        => MI_RSUSUARIOS(i).ANO
                                        ,UN_PERIODO    => MI_RSUSUARIOS(i).PERIODO
                                        ,UN_CODIGORUTA => MI_RSUSUARIOS(i).CODIGORUTA
                                        ,UN_CICLO      => MI_RSUSUARIOS(i).CICLO
                                        ,UN_TOTFACT    => MI_RSUSUARIOS(i).CREDITOABONADO
                                        ,UN_USUARIO    => UN_USUARIO);

             END IF;

             MI_ETAPA := 'Indicadores usuario calculado';
             IF MI_RSUSUARIOS(i).INDFACTURADO = 0 THEN
                MI_RSUSUARIOS(i).INDFACTURADO := -1;
             END IF;
             MI_RSUSUARIOS(i).INDPREPARADO := 0;

             MI_ETAPA := 'Actualización valores usuario';
             BEGIN
                 BEGIN
                     MI_TABLA     :=' SP_USUARIO ';

                     MI_CAMPOS    :=' PROBLEMALECTURA            = '|| MI_RSUSUARIOS(i).PROBLEMALECTURA ||'
                                     ,TIPOCALCULO                = '''|| MI_RSUSUARIOS(i).TIPOCALCULO || '''
                                     ,CRITICACONSUMO             = ''' || MI_RSUSUARIOS(i).CRITICACONSUMO || '''
                                     ,INDMEDCAMBIADO             = '|| MI_RSUSUARIOS(i).INDMEDCAMBIADO ||'
                                     ,ACUMULADOPER               = '|| MI_RSUSUARIOS(i).ACUMULADOPER ||'
                                     ,DESCONTADO                 = '|| MI_RSUSUARIOS(i).DESCONTADO ||'
                                     ,PERIODOSATRASO             = '|| MI_RSUSUARIOS(i).PERIODOSATRASO ||'
                                     ,NUMERODIGITOS              = '|| MI_RSUSUARIOS(i).NUMERODIGITOS ||'
                                     ,INDCALCULADO               = '|| MI_RSUSUARIOS(i).INDCALCULADO ||'
                                     ,ACUMULADO                  = '|| NVL(MI_RSUSUARIOS(i).ACUMULADO,0) ||'
                                     ,PORCENTAJEAPLICAR          = '|| MI_RSUSUARIOS(i).PORCENTAJEAPLICAR ||'
                                     ,LECTURA                    = '|| MI_RSUSUARIOS(i).LECTURA ||'
                                     ,LECTURA1                   = '|| MI_RSUSUARIOS(i).LECTURA1 ||'
                                     ,CONSUMO1                   = '|| MI_RSUSUARIOS(i).CONSUMO1 ||'
                                     ,CONSUMO2                   = '|| MI_RSUSUARIOS(i).CONSUMO2 ||'
                                     ,CONSUMO3                   = '|| MI_RSUSUARIOS(i).CONSUMO3 ||'
                                     ,CONSUMO4                   = '|| MI_RSUSUARIOS(i).CONSUMO4 ||'
                                     ,CONSUMO5                   = '|| MI_RSUSUARIOS(i).CONSUMO5 ||'
                                     ,CONSUMO6                   = '|| MI_RSUSUARIOS(i).CONSUMO6 ||'
                                     ,CONSUMOPROM                = '|| MI_RSUSUARIOS(i).CONSUMOPROM ||'
                                     ,CONSUMO                    = '|| MI_RSUSUARIOS(i).CONSUMO ||'
                                     ,CONSUMOBASICO              = '|| MI_RSUSUARIOS(i).CONSUMOBASICO ||'
                                     ,CONSUMOCOMPLEMENTARIO      = '|| MI_RSUSUARIOS(i).CONSUMOCOMPLEMENTARIO ||'
                                     ,CONSUMOSUNTUARIO           = '|| MI_RSUSUARIOS(i).CONSUMOSUNTUARIO ||'
                                     ,CONSUMOBASICOALC           = '|| MI_RSUSUARIOS(i).CONSUMOBASICOALC ||'
                                     ,CONSUMOCOMPLEMENTARIOALC   = '|| MI_RSUSUARIOS(i).CONSUMOCOMPLEMENTARIOALC ||'
                                     ,CONSUMOSUNTUARIOALC        = '|| MI_RSUSUARIOS(i).CONSUMOSUNTUARIOALC ||'
                                     ,SUB_TASAAMBIENTALAC        = '|| MI_RSUSUARIOS(i).SUB_TASAAMBIENTALAC ||'
                                     ,SOBRE_TASAAMBIENTALAC      = '|| MI_RSUSUARIOS(i).SOBRE_TASAAMBIENTALAC ||'
                                     ,COSTOCONSUMOAC             = '|| MI_RSUSUARIOS(i).COSTOCONSUMOAC ||'
                                     ,CARGOFIJO                  = '|| MI_RSUSUARIOS(i).CARGOFIJO ||'
                                     ,ACSINMEDICION              = '|| MI_RSUSUARIOS(i).ACSINMEDICION ||'
                                     ,TOTALACUEDUCTO             = '|| MI_RSUSUARIOS(i).TOTALACUEDUCTO ||'
                                     ,VALCONSUMO                 = '|| MI_RSUSUARIOS(i).VALCONSUMO ||'
                                     ,VALCONSUMOPROM             = '|| MI_RSUSUARIOS(i).VALCONSUMOPROM ||'
                                     ,TOTALALCANTARILLADO        = '|| MI_RSUSUARIOS(i).TOTALALCANTARILLADO ||'
                                     ,SUB_TASAAMBIENTALALC       = '|| MI_RSUSUARIOS(i).SUB_TASAAMBIENTALALC ||'
                                     ,SOBRE_TASAAMBIENTALALC     = '|| MI_RSUSUARIOS(i).SOBRE_TASAAMBIENTALALC ||'
                                     ,COSTOCONSUMOAL             = '|| MI_RSUSUARIOS(i).COSTOCONSUMOAL ||'
                                     ,CARGOFIJOAL                = '|| MI_RSUSUARIOS(i).CARGOFIJOAL ||'
                                     ,ALSINMEDICION              = '|| MI_RSUSUARIOS(i).ALSINMEDICION ||'
                                     ,VALORBASICO                = '|| MI_RSUSUARIOS(i).VALORBASICO ||'
                                     ,VALORCOMPLEMENTARIO        = '|| MI_RSUSUARIOS(i).VALORCOMPLEMENTARIO ||'
                                     ,VALORSUNTUARIO             = '|| MI_RSUSUARIOS(i).VALORSUNTUARIO ||'
                                     ,SUBFIJO                    = '|| MI_RSUSUARIOS(i).SUBFIJO ||'
                                     ,SUBCONSUMOAC               = '|| MI_RSUSUARIOS(i).SUBCONSUMOAC ||'
                                     ,SUBACUEDUCTO               = '|| MI_RSUSUARIOS(i).SUBACUEDUCTO ||'
                                     ,SOBREFIJO                  = '|| MI_RSUSUARIOS(i).SOBREFIJO ||'
                                     ,SUBSIDIO                   = '|| MI_RSUSUARIOS(i).SUBSIDIO ||'
                                     ,SOBREPRECIO                = '|| MI_RSUSUARIOS(i).SOBREPRECIO ||'
                                     ,SOBRECONSUMOAC             = '|| MI_RSUSUARIOS(i).SOBRECONSUMOAC ||'
                                     ,SOBREACUEDUCTO             = '|| MI_RSUSUARIOS(i).SOBREACUEDUCTO ||'
                                     ,SUBSINMEDICION             = '|| MI_RSUSUARIOS(i).SUBSINMEDICION ||'
                                     ,SOBRESINMEDICION           = '|| MI_RSUSUARIOS(i).SOBRESINMEDICION ||'
                                     ,VALORALCBASICO             = '|| MI_RSUSUARIOS(i).VALORALCBASICO ||'
                                     ,VALORALCCOMPLEMENTARIO     = '|| MI_RSUSUARIOS(i).VALORALCCOMPLEMENTARIO ||'
                                     ,VALORALCSUNTUARIO          = '|| MI_RSUSUARIOS(i).VALORALCSUNTUARIO ||'
                                     ,SUBFIJOALC                 = '|| MI_RSUSUARIOS(i).SUBFIJOALC ||'
                                     ,SUBCONSUMOAL               = '|| MI_RSUSUARIOS(i).SUBCONSUMOAL ||'
                                     ,SUBALCANTARILLADO          = '|| MI_RSUSUARIOS(i).SUBALCANTARILLADO ||'
                                     ,SOBREALCANTARILLADO        = '|| MI_RSUSUARIOS(i).SOBREALCANTARILLADO ||'
                                     ,SOBREFIJOALC               = '|| MI_RSUSUARIOS(i).SOBREFIJOALC ||'
                                     ,SOBRECONSUMOAL             = '|| MI_RSUSUARIOS(i).SOBRECONSUMOAL ||'
                                     ,SUBALCSINMEDICION          = '|| MI_RSUSUARIOS(i).SUBALCSINMEDICION ||'
                                     ,SOBREALCSINMEDICION        = '|| MI_RSUSUARIOS(i).SOBREALCSINMEDICION ||'
                                     ,TOTALASEO                  = '|| MI_RSUSUARIOS(i).TOTALASEO ||'
                                     ,SUBCCS_720                 = '|| MI_RSUSUARIOS(i).SUBCCS_720 ||'
                                     ,SOBCCS_720                 = '|| MI_RSUSUARIOS(i).SOBCCS_720 ||'
                                     ,SUBCBLS_720                = '|| MI_RSUSUARIOS(i).SUBCBLS_720 ||'
                                     ,SOBCBLS_720                = '|| MI_RSUSUARIOS(i).SOBCBLS_720 ||'
                                     ,SUBCLUS_720                = '|| MI_RSUSUARIOS(i).SUBCLUS_720 ||'
                                     ,SOBCLUS_720                = '|| MI_RSUSUARIOS(i).SOBCLUS_720 ||'
                                     ,SUBCRT_720                 = '|| MI_RSUSUARIOS(i).SUBCRT_720 ||'
                                     ,SOBCRT_720                 = '|| MI_RSUSUARIOS(i).SOBCRT_720 ||'
                                     ,SUBCDF_720                 = '|| MI_RSUSUARIOS(i).SUBCDF_720 ||'
                                     ,SOBCDF_720                 = '|| MI_RSUSUARIOS(i).SOBCDF_720 ||'
                                     ,SUBCTL_720                 = '|| MI_RSUSUARIOS(i).SUBCTL_720 ||'
                                     ,SOBCTL_720                 = '|| MI_RSUSUARIOS(i).SOBCTL_720 ||'
                                     ,SUBVBA_720                 = '|| MI_RSUSUARIOS(i).SUBVBA_720 ||'
                                     ,SOBVBA_720                 = '|| MI_RSUSUARIOS(i).SOBVBA_720 ||'
                                     ,CALCULODESH                = '|| MI_RSUSUARIOS(i).CALCULODESH ||'
                                     ,COSTOREALASEO              = '|| MI_RSUSUARIOS(i).COSTOREALASEO ||'
                                     ,SUBBARR_LIM                = '|| MI_RSUSUARIOS(i).SUBBARR_LIM ||'
                                     ,SOBBARR_LIM                = '|| MI_RSUSUARIOS(i).SOBBARR_LIM ||'
                                     ,SUBMANEJO_REC              = '|| MI_RSUSUARIOS(i).SUBMANEJO_REC ||'
                                     ,SOBMANEJO_REC              = '|| MI_RSUSUARIOS(i).SOBMANEJO_REC ||'
                                     ,SUBDISP_FINAL              = '|| MI_RSUSUARIOS(i).SUBDISP_FINAL ||'
                                     ,SOBDISP_FINAL              = '|| MI_RSUSUARIOS(i).SOBDISP_FINAL ||'
                                     ,SUBRECOLECCION             = '|| MI_RSUSUARIOS(i).SUBRECOLECCION ||'
                                     ,SOBRECOLECCION             = '|| MI_RSUSUARIOS(i).SOBRECOLECCION ||'
                                     ,SUBTRAMO                   = '|| MI_RSUSUARIOS(i).SUBTRAMO ||'
                                     ,SOBTRAMO                   = '|| MI_RSUSUARIOS(i).SOBTRAMO ||'
                                     ,SUBDESHABITADO             = '|| MI_RSUSUARIOS(i).SUBDESHABITADO ||'
                                     ,SOBDESHABITADO             = '|| MI_RSUSUARIOS(i).SOBDESHABITADO ||'
                                     ,SUBASEO                    = '|| MI_RSUSUARIOS(i).SUBASEO ||'
                                     ,SOBREASEO                  = '|| MI_RSUSUARIOS(i).SOBREASEO ||'
                                     ,VASEOUNICO                 = '|| MI_RSUSUARIOS(i).VASEOUNICO ||'
                                     ,VASEODOMICILIARIO          = '|| MI_RSUSUARIOS(i).VASEODOMICILIARIO ||'
                                     ,VASEOBARRIDO               = '|| MI_RSUSUARIOS(i).VASEOBARRIDO ||'
                                     ,VASEOCONSUMO               = '|| MI_RSUSUARIOS(i).VASEOCONSUMO ||'
                                     ,VTRAMOEXCEDENTE            = '|| MI_RSUSUARIOS(i).VTRAMOEXCEDENTE ||'
                                     ,VDESHABITADO               = '|| MI_RSUSUARIOS(i).VDESHABITADO ||'
                                     ,VALASEOCCS_720             = '|| MI_RSUSUARIOS(i).VALASEOCCS_720   ||'
                                     ,VALASEOCBLS_720            = '|| MI_RSUSUARIOS(i).VALASEOCBLS_720  ||'
                                     ,VALASEOCLUS_720            = '|| MI_RSUSUARIOS(i).VALASEOCLUS_720  ||'
                                     ,VALASEOCRT_720             = '|| MI_RSUSUARIOS(i).VALASEOCRT_720   ||'
                                     ,VALASEOCDF_720             = '|| MI_RSUSUARIOS(i).VALASEOCDF_720   ||'
                                     ,VALASEOCTL_720             = '|| MI_RSUSUARIOS(i).VALASEOCTL_720   ||'
                                     ,VALASEOVBA_720             = '|| MI_RSUSUARIOS(i).VALASEOVBA_720   ||'
                                     ,INDFACTURADO               = '|| MI_RSUSUARIOS(i).INDFACTURADO ||'
                                     ,INDPREPARADO               = '|| MI_RSUSUARIOS(i).INDPREPARADO ||'
                                     ,DEUDAASEO                  = '|| MI_RSUSUARIOS(i).DEUDAASEO ||'
                                     ,RECARGOASEO                = '|| MI_RSUSUARIOS(i).RECARGOASEO ||'
                                     ,AJUSTEDECENAASEO           = '|| MI_RSUSUARIOS(i).AJUSTEDECENAASEO ||'
                                     ,TOTFACTURAASEO             = '|| MI_RSUSUARIOS(i).TOTFACTURAASEO ||'
                                     ,TOTFACTURAASEOPAGO2        = '|| MI_RSUSUARIOS(i).TOTFACTURAASEOPAGO2 ||'
                                     ,FACTURA                    = '|| MI_RSUSUARIOS(i).FACTURA ||'
                                     ,COSTOFIJOAC                = '|| MI_RSUSUARIOS(i).COSTOFIJOAC ||'
                                     ,COSTOM3AC                  = '|| MI_RSUSUARIOS(i).COSTOM3AC ||'
                                     ,TARIFAM3BASICOAC           = '|| MI_RSUSUARIOS(i).TARIFAM3BASICOAC ||'
                                     ,TARIFAM3COMPLEMENTARIOAC   = '|| MI_RSUSUARIOS(i).TARIFAM3COMPLEMENTARIOAC ||'
                                     ,TARIFAM3SUNTUARIOAC        = '|| MI_RSUSUARIOS(i).TARIFAM3SUNTUARIOAC ||'
                                     ,COSTOM3AL                  = '|| MI_RSUSUARIOS(i).COSTOM3AL ||'
                                     ,COSTOFIJOAL                = '|| MI_RSUSUARIOS(i).COSTOFIJOAL ||'
                                     ,TARIFAM3BASICOAL           = '|| MI_RSUSUARIOS(i).TARIFAM3BASICOAL ||'
                                     ,TARIFAM3COMPLEMENTARIOAL   = '|| MI_RSUSUARIOS(i).TARIFAM3COMPLEMENTARIOAL ||'
                                     ,TARIFAM3SUNTUARIOAL        = '|| MI_RSUSUARIOS(i).TARIFAM3SUNTUARIOAL ||'
                                     ,FIJOALUMBRADO              = '|| MI_RSUSUARIOS(i).FIJOALUMBRADO ||'
                                     ,TOTALDEUDA                 = '|| MI_RSUSUARIOS(i).TOTALDEUDA ||'
                                     ,RECARGOACUEDUCTO           = '|| MI_RSUSUARIOS(i).RECARGOACUEDUCTO ||'
                                     ,RECARGOALUMBRADO           = '|| MI_RSUSUARIOS(i).RECARGOALUMBRADO ||'
                                     ,RECARGOALC                 = '|| MI_RSUSUARIOS(i).RECARGOALC ||'
                                     ,ABONOS                     = '|| MI_RSUSUARIOS(i).ABONOS ||'
                                     ,TOTFACTURAPERACTUAL        = '|| MI_RSUSUARIOS(i).TOTFACTURAPERACTUAL ||'
                                     ,TOTFACTURAPAGO2            = '|| MI_RSUSUARIOS(i).TOTFACTURAPAGO2 ||'
                                     ,FIMM                       = ''' || MI_RSUSUARIOS(i).FIMM || '''
                                     ,TOTALALUMBRADO             = '|| MI_RSUSUARIOS(i).TOTALALUMBRADO ||'
                                     ,DEUDAALUMBRADO             = '|| MI_RSUSUARIOS(i).DEUDAALUMBRADO ||'
                                     ,TOTALFACTURAALUMBRADO      = '|| MI_RSUSUARIOS(i).TOTALFACTURAALUMBRADO ||'
                                     ,CREDITOABONADO             = '|| MI_RSUSUARIOS(i).CREDITOABONADO ||'  ';

                     IF UN_ENSERIE <> 0 THEN
                          MI_CAMPOS := MI_CAMPOS || '
                                      ,CREATED_BY                 = '''|| UN_USUARIO ||'''
                                      ,DATE_CREATED               = SYSDATE ';
                      ELSE
                          MI_CAMPOS := MI_CAMPOS || '
                                      ,MODIFIED_BY                 = '''|| UN_USUARIO ||'''
                                      ,DATE_MODIFIED               = SYSDATE ';
                     END IF;


                     MI_CONDICIONACME :=' COMPANIA      = ''' || UN_COMPANIA || '''
                                         AND CICLO      = ' || MI_RSUSUARIOS(i).CICLO || '
                                         AND CODIGORUTA = '''|| MI_RSUSUARIOS(i).CODIGORUTA||''' ';

                     MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICIONACME);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

                 END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                 MI_MSGERROR(1).CLAVE := 'USUARIO';
                 MI_MSGERROR(1).VALOR := MI_RSUSUARIOS(i).CODIGORUTA;
                 MI_MSGERROR(2).CLAVE := 'ANIO';
                 MI_MSGERROR(2).VALOR := MI_RSUSUARIOS(i).ANO;
                 MI_MSGERROR(3).CLAVE := 'PERIODO';
                 MI_MSGERROR(3).VALOR := MI_RSUSUARIOS(i).PERIODO;
                 MI_MSGERROR(4).CLAVE := 'USO';
                 MI_MSGERROR(4).VALOR := MI_RSUSUARIOS(i).USO;
                 MI_MSGERROR(5).CLAVE := 'ESTRATO';
                 MI_MSGERROR(5).VALOR := MI_RSUSUARIOS(i).ESTRATO;

                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTCALCULOUSUARIO,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_MSGERROR);
             END;


         ELSE
            IF UN_ENSERIE <>0 THEN
                PR_INSERTAERRORCALCULO(UN_COMPANIA            => UN_COMPANIA
                                       ,UN_CODIGORUTA         => MI_RSUSUARIOS(i).CODIGORUTA
                                       ,UN_CICLO              => MI_RSUSUARIOS(i).CICLO
                                       ,UN_CODIGOERRORINTERNO => MI_CODERROR
                                       ,UN_MENSAJE            => MI_VALIDACALCULOUSUARIO);
            END IF;
         END IF; --Cierre condicion Valida calcularusuario
       EXCEPTION WHEN OTHERS THEN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END;
     EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
           MI_MSGERROR(1).CLAVE := 'USUARIO';
           MI_MSGERROR(1).VALOR := MI_RSUSUARIOS(i).CODIGORUTA;
           MI_MSGERROR(2).CLAVE := 'CICLO';
           MI_MSGERROR(2).VALOR := MI_RSUSUARIOS(i).CICLO;
           MI_MSGERROR(3).CLAVE := 'ANIO';
           MI_MSGERROR(3).VALOR := MI_RSUSUARIOS(i).ANO;
           MI_MSGERROR(4).CLAVE := 'PERIODO';
           MI_MSGERROR(4).VALOR := MI_RSUSUARIOS(i).PERIODO;
           MI_MSGERROR(5).CLAVE := 'ETAPA';
           MI_MSGERROR(5).VALOR := MI_ETAPA;

           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_USUARIOCALCULO,
                                      UN_TABLAERROR => 'SP_USUARIO',
                                      UN_REEMPLAZOS => MI_MSGERROR);
     END;
    END LOOP;

   --Elimina los datos de los types
    MI_RSUSUARIOS.DELETE;
    MI_FACTURADOACT.DELETE;

    IF UN_ENSERIE =0 THEN      --Si es cálculo individual se muestran los mensajes de error'
       --MI_VALIDACALCULOUSUARIO := 'TOTAL REGISTROSMZ' || MI_CUENTA;
       IF MI_VALIDACALCULOUSUARIO <> 'C' THEN
           MI_RTA := MI_VALIDACALCULOUSUARIO;
       ELSE
           MI_RTA :='Proceso ejecutado exitosamente';
       END IF;
    ELSE
       MI_RTA :='Proceso ejecutado exitosamente';
    END IF;


    BEGIN
        MI_FINAL := NVL(UN_FINAL,0);
        IF UN_ENSERIE <> 0 THEN
            MI_TABLA     := ' SP_CICLO ';
            MI_CONDICIONACME := '    COMPANIA   = '''|| UN_COMPANIA || '''
                                     AND NUMERO = '|| UN_INTCICLO ||'   ';

            MI_CAMPOS :=' ';

            IF (MI_PARAMETROS('MANPREFACTURACION').VALOR ='SI' AND NVL(MI_FINAL,0) <>0) OR (MI_PARAMETROS('MANPREFACTURACION').VALOR <> 'SI') THEN
                MI_CAMPOS :=' INDCALCULADO  = '|| (-1) ||'
                             ,INDPREPARADO  = '|| (0) ||'   ';
            END IF;

            IF MI_PARAMETROS('MANPREFACTURACION').VALOR = 'SI' AND MI_PREFACTURANDO <>0 AND NVL(MI_FINAL,0) <> 0  THEN
                MI_CAMPOS :=  MI_CAMPOS ||  ',PREFACTURANDO       = '|| (0) ||'
                                             ,FECHA_CIERREPREFAC  = SYSDATE ';

            END IF;

            IF NVL(MI_CAMPOS,' ') <> ' ' THEN
                BEGIN
                    MI_CAMPOS := MI_CAMPOS || ',MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                               ,DATE_MODIFIED = SYSDATE ';
                    MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICIONACME);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
            END IF;
        END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CICLO';
            MI_MSGERROR(1).VALOR :=  UN_INTCICLO;
            MI_MSGERROR(2).CLAVE := 'CONSULTA';
            MI_MSGERROR(2).VALOR :=  MI_CAMPOS;


            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTCACLCICLO,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_REEMPLAZOS => MI_MSGERROR);
    END;

    RETURN MI_RTA;

  END FC_CALCULOFACTURACION;

--2
  FUNCTION FC_CARGARPARFACTU
  (
  /*
    NAME              : FC_CARGARPARFACTU --> EN ACCESS CargaParFactu
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 08/02/2017
    TIME              : 03:30 PM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :

    PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_NT         => Nit para validar procesos de autorizacion de procesos
    MODIFICATIONS     :

    @NAME:    calcularProgresividad
    @METHOD:  GET
  */
     UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_NIT		     IN COMPANIA.NITCOMPANIA%TYPE

  )RETURN TI_PARAMETRO AS
     MI_CARGAPARAMETROS   TI_PARAMETRO;
  BEGIN

      MI_CARGAPARAMETROS('MANINTRECT').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'MANEJA INTERES Y RECARGO POR TARIFA',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CONTROLRES493').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'CONTROL DE RES493 POR USUARIO',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CONMIXTO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'MANEJA CONSUMOS MIXTOS',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('MICROSITIO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'MANEJA MICROMEDICION EN SITIO',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('DESVIAENSITIO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'MANEJA DESVIACION SIGNIFICATIVA EN SITIO',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('IVA1CUOTA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'MANEJA IVA EN FINANCIABLES',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INTCUOTASNOINTERESMEDIDOR').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                         UN_NOMBRE    =>  'MESES NO COBRO INTERESES MEDIDOR',
                                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                         UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('NUMCONCEPTOS').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                            UN_NOMBRE    =>  'NUMERO MAXIMO CONCEPTOS',
                                                                            UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR =>  SYSDATE),'250');

      MI_CARGAPARAMETROS('AJUSDECENA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'FACTOR DE AJUSTE TOTAL',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('FRECUENCIA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'FRECUENCIA PERIODOS DE FACTURACION',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'');

      MI_CARGAPARAMETROS('FINSITU').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'FACTURACION EN SITIO',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('FALUMBRADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'ALUMBRADO PUBLICO',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'NO');


      MI_CARGAPARAMETROS('DESCUENTAACUMULADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                  UN_NOMBRE    =>  'DESCONTAR ACUMULADO SIN LECTURA',
                                                                                  UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('DBLPERIODOSDESCUENTAACUMULADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                             UN_NOMBRE    =>  'PERIODOS DESCONTAR ACUMULADO SIN LECTURA',
                                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                             UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('RECARGO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('SUBSIDIO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'DISCRIMINAR SUBSIDIO REAL',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('ASEOINDEPENDIENTE').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                 UN_NOMBRE    =>  'EMPRESA DE ASEO INDEPENDIENTE',
                                                                                 UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                 UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('STRUSOCONRETENCION').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                  UN_NOMBRE    =>  'USOS EN QUE APLICA AUTORETENCION',
                                                                                  UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR =>  SYSDATE),'');

      MI_CARGAPARAMETROS('STRUSOALUMBRADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                               UN_NOMBRE    =>  'USO ESPECIAL PARA ALUMBRADO',
                                                                               UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                               UN_FECHA_PAR =>  SYSDATE),'');

      MI_CARGAPARAMETROS('INTMAXDESH').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'MAXIMO CONSUMO DESHABITADOS',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('TIPOFACT').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'FACTURACION DE RIEGO',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('COBRARPORPESO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'COBRAR ASEO POR PESO',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');


      MI_CARGAPARAMETROS('ALCSM').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                   UN_NOMBRE    =>  'TARIFA S.M PARA USUARIOS CON SOLO ALCANTARILLADO',
                                                                   UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                   UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CASEOCONSUMO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'COBRAR SOLO ASEOCONSUMO CUANDO PESOASEO>1',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('MANRES351').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'MANEJA RES/ 351',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TASAAMBIENTAL').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'CALCULAR TASA AMBIENTAL DE FORMA INDEPENDIENTE',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CONASEOBARRIDORU').VALOR := CASE WHEN	(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                           UN_NOMBRE    =>  'CALCULAR TASA AMBIENTAL DE FORMA INDEPENDIENTE',
                                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                           UN_FECHA_PAR =>  SYSDATE),''))=''
                                                      THEN 'SI'
                                                      ELSE  (NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                       UN_NOMBRE    =>  'CALCULAR ASEO BARRIDO Y LIMPIEZA A RURALES',
                                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                       UN_FECHA_PAR =>  SYSDATE),'NO'))
                                                      END;

      MI_CARGAPARAMETROS('PROCTERCERIZADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                   UN_NOMBRE    =>  'MANEJA PROCESO TERCERIZADO',
                                                                   UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                   UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('VALORTARIFAPLENA493').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'VALOR TARIFA PLENA RES 493',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('METRAJEDESINCENTIVO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'METRO DE DESINCENTIVO RES 493',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('COBRARVALORPORSERVICIO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                    UN_NOMBRE    =>  'COBRAR VALOR DE MORA POR SERVICIO ACTIVO',
                                                                                    UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                    UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TRAMOXASEO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                      UN_NOMBRE    =>  'MANEJA TRAMO EXCEDENTE POR PESOASEO',
                                                                      UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                      UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('RES493').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                    UN_NOMBRE    =>  'APLICA RESOLUCION 493',
                                                                    UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                    UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('PROMDES').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                     UN_NOMBRE    =>  'CALCULA PROMEDIO PARA DESINCENTIVO',
                                                                     UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                     UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('RES151').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                    UN_NOMBRE    =>  'APLICA RESOLUCIÓN 151 DEL 2001',
                                                                    UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                    UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALRES151').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'APLICA CÁLCULO RESOLUCIÓN 151 DEL 2001',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('DESMETAC').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                      UN_NOMBRE    =>  'DESCONTAR METRAJE ACUEDUCTO',
                                                                      UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                      UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALCONSUS').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'PERMITE CALCULAR CONSUMO A SUSPENDIDOS',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('COMMANXPESO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'MANEJA COMERCIO Y MANEJO POR PESOASEO',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('COBASEODES').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'COBRAR ASEO DESHABITADOS',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'');

      MI_CARGAPARAMETROS('DESHMENOR10').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'COBRAR TARIFA DESHABITADOS MENORES DE 10',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('SINMED3MESES').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'TARIFA SIN MEDICION MAYOR A TRES MESES',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLREDONDEOPORENCIMA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                 UN_NOMBRE    =>  'REDONDEO SIEMPRE POR ENCIMA',
                                                                                 UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                 UN_FECHA_PAR =>  SYSDATE),'NO');


      MI_CARGAPARAMETROS('BLDESHAB').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'COBRAR SIN MEDICION CON CONSUMOS IGUALES',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INTDESHABPERIODOS').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                 UN_NOMBRE    =>  'PERIODOS IGUALES PARA CONSUMO SIN MEDICION',
                                                                                 UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                 UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('INDAPLICA21').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'APLICA RESOLUCION 21 DEL 97',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INDAPLICA233').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                          UN_NOMBRE    =>  'APLICA RESOLUCION CRA 233',
                                                                          UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                          UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INDCALCULARPROMEDIOS').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                  UN_NOMBRE    =>  'IMPEDIR CALCULO POR PROMEDIOS',
                                                                                  UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLRECARGOSCREDITO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                               UN_NOMBRE    =>  'COBRA RECARGO SI HAY NOTA CREDITO',
                                                                               UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                               UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLRECARGOASEOINDEPENDIENTE').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                        UN_NOMBRE    =>  'MANEJAR RECARGOS DE ASEO SEPARADOS',
                                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLRECARGOALCINDEPENDIENTE').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                       UN_NOMBRE    =>  'MANEJAR RECARGOS DE ALCANTARILLADO SEPARADOS',
                                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLRECARGOALUMBRADOINDEPENDIENTE').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                             UN_NOMBRE    =>  'MANEJAR RECARGOS DE ALUMBRADO SEPARADOS',
                                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                             UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('BLSEPARARALUMBRADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                UN_NOMBRE    =>  'SEPARAR CATEGORIA DE ALUMBRADO',
                                                                                UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALSUSPENDIDO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'PERMITE CALCULO SUSPENDIDOS',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');


      MI_CARGAPARAMETROS('CALCORTADOASEO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                              UN_NOMBRE    =>  'CALCULA SOLO ASEO EN CORTADOS',
                                                                              UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                              UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('FACTSITIO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                       UN_NOMBRE    =>  'FACTURACION EN SITIO',
                                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                       UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALPRODUCTIVIDAD').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                              UN_NOMBRE    =>  'DESCONTAR PRODUCTIVIDAD',
                                                                              UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                              UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CNPRODUCTIVIDAD').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'CONCEPTO PARA DESCONTAR PRODUCTIVIDAD',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('APLICACN668').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'APLICA CONCEPTO 668 DE 2011',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TARIFAS668').VALOR := UPPER( PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'CLASIFICACIONES SUPERSERVICIOS CON 668 DE 2011',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE));

      IF MI_CARGAPARAMETROS('APLICACN668').VALOR = 'SI' THEN
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_CARGAPARAMETROS('TARIFAS668').VALOR);
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'RESIDENCIAL','1');
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'COMERCIAL','2');
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'INDUSTRIAL','3');
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'OFICIAL','4');
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'ESPECIAL','5');
          MI_CARGAPARAMETROS('TARIFAS668').VALOR := REPLACE(MI_CARGAPARAMETROS('TARIFAS668').VALOR,'TEMPORAL','6');
      END IF;

      MI_CARGAPARAMETROS('SUBSOBREDESHA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'MANEJA SUBSIDIO Y SOBREPRECIO A DESHABITADOS',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TOTACONCODIGO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'MANEJA CODIGO TOTALIZADOR',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INDACUMULADOPER').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'CONTROLA DESCUENTO ACUMULADO POR PERIODO',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('DESHABXCOM').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'MANEJA ASEO DESHABITADO POR COMPONENTES',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CONSUBSOBRETASA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                             UN_NOMBRE    =>  'MANEJA SUBSIDIO Y SOBREPRECIO TASAS AMBIENTALES',
                                                                             UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                             UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CONSUMOMANUAL').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'MANEJA CONSUMO MANUAL',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('MANPREFACTURACION').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                               UN_NOMBRE    =>  'MANEJA PREFACTURACIÓN',
                                                                               UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                               UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALDESCUENTOCONTROLADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                      UN_NOMBRE    =>  'MANEJA CONTROL DE DESCUENTOS POR CONCEPTOS',
                                                                                      UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                      UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CALDESCUENTOCONCEPTO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                      UN_NOMBRE    =>  'CALCULAR DESCUENTO CON PORCENTAJE POR CONCEPTO',
                                                                                      UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                      UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CNDESCUENTOCONTROLADO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                     UN_NOMBRE    =>  'CONCEPTO DE CONTROL DE DESCUENTOS',
                                                                                     UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                     UN_FECHA_PAR =>  SYSDATE),'0');


      MI_CARGAPARAMETROS('AUTODESVIA').VALOR := CASE WHEN (PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_DESVIACION(UN_COMPANIA  => UN_COMPANIA,
                                                                                                                  UN_NIT  	   => UN_NIT)	) =-1
                                                  THEN 'SI'
                                                  ELSE 'NO'
                                                END;



      MI_CARGAPARAMETROS('AUTOMICRO').VALOR  := CASE WHEN(PCK_SERVICIOS_PUBLICOS.FC_AUTORIZACION_MICROMEDICION(UN_COMPANIA  => UN_COMPANIA,
                                                                                                               UN_NIT  	    => UN_NIT)  )=-1
                                                  THEN 'SI'
                                                  ELSE 'NO'
                                                END;

      MI_CARGAPARAMETROS('PROCESOMICRO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                            UN_NOMBRE    =>  'MANEJA PROCESO DE MICROMEDICION',
                                                                            UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CHAPETASDESH').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                            UN_NOMBRE    =>  'CHAPETAS COBRAN ASEO DESHABITADO',
                                                                            UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('COBRARDESVPERACT').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                UN_NOMBRE    =>  'COBRAR DESVIACION EN PERIODO ACTUAL',
                                                                                UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('MANEJA720').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'APLICA RESOLUCION CRA 720',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('INICIO720').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                         UN_NOMBRE    =>  'PERIODO INICIO RES 720 ASEO',
                                                                         UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                         UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TONELADASAFOTARVAR').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                  UN_NOMBRE    =>  'RES 720 TARIFAS VARIABLES POR TONELADAS AFORADAS',
                                                                                  UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('TAR720MANUALESAFORO').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                   UN_NOMBRE    =>  'RES 720 TARIFAS MANUALES',
                                                                                   UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                   UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CICLOEXCLUIDO720').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                                UN_NOMBRE    =>  'RES 720 CICLO EXCLUIDO',
                                                                                UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                                UN_FECHA_PAR =>  SYSDATE),'0');

      MI_CARGAPARAMETROS('TSIMMALC').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                        UN_NOMBRE    =>  'TARIFA S.M ALCANTARRILLADO',
                                                                        UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                        UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('PERMITEDESCUENTOTOTFACTURA').VALOR := CASE WHEN (PCK_SERVICIOS_PUBLICOS_COM6.FC_AUTORIZACION_DESCTOTFACTURA(UN_COMPANIA  => UN_COMPANIA,
                                                                              UN_NIT  	 => UN_NIT)	) =1
                                                                THEN 'SI'
                                                                ELSE 'NO'
                                                                END;

      MI_CARGAPARAMETROS('CALCOMERMANRURAL').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                              UN_NOMBRE    =>  'CALCULAR COMERCIALIZACION Y RECAUDO A RURALES',
                                                                              UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                              UN_FECHA_PAR =>  SYSDATE),'NO');  

      MI_CARGAPARAMETROS('UNIDADESIND').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                           UN_NOMBRE    =>  'MANEJA UNIDADES INDEPENDIENTES',
                                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                           UN_FECHA_PAR =>  SYSDATE),'NO');  


      MI_CARGAPARAMETROS('TARHABITADOSINDDESH').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                               UN_NOMBRE    =>  'RES 720 NO COBRAR DESHABITADO CON CONSUMO',
                                                                               UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                               UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('RECARGOSEGFECHADESDETARIFA').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                            UN_NOMBRE    =>  'MANEJA RECARGO SEGUNDA FECHA POR USO Y ESTRATO',
                                                                            UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR =>  SYSDATE),'NO');

      MI_CARGAPARAMETROS('CMANUAL').VALOR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                    UN_NOMBRE    =>  'CONCEPTOS MANUALES',
                                                                    UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                                    UN_FECHA_PAR =>  SYSDATE),'0');

      RETURN MI_CARGAPARAMETROS;
  END FC_CARGARPARFACTU;

--3
  FUNCTION FC_CARGATARIFAS
  /*
        @NAME:    cargarTarifas
        @METHOD:  GET
*/
  (
   UN_COMPANIA               IN     PCK_SUBTIPOS.TI_COMPANIA
  ,UN_ANO                    IN     SP_USUARIO.ANO%TYPE
  ,UN_PERIODO                IN     SP_USUARIO.PERIODO%TYPE
  ,UN_USO                    IN     SP_USUARIO.USO%TYPE
  ,UN_ESTRATO                IN     SP_USUARIO.ESTRATO%TYPE
  ,UN_VALIDATARIFA           IN OUT VARCHAR2
  ) RETURN  TTARIFAS
   AS
  MI_TARIFAS   TTARIFAS;
  BEGIN
    BEGIN
        SELECT  COMPANIA,ANO,PERIODO,USO,CARGOFIJO,METROS_NO_COBRADOS,CONSUMOBASICO,CONSUMOCOMPLEMENTARIO,
                CONSUMOSUNTUARIO,M3SINMEDICION,CONSUMOPROM_USOS_ESTRATOS, SINMEDICION, SINMEDICIONALC,
                SUBSINMEDICION ,SOBRESINMEDICION ,SUBALCSINMEDICION  ,SOBREALCSINMEDICION,PRECIO_METROS_NO_COBRADOS,
                TARIFABASICO, TARIFACOMPLEMENTARIO, TARIFASUNTUARIO ,VALORTASAAMBIENTALAC  ,SUB_TASAAMBIENTALAC ,
                SOBRE_TASAAMBIENTALAC, COSTOM3AC,FIJOALCANTARILLADO,ALCBASICO,ALCCOMPLEMENTARIO,ALCSUNTUARIO,
                VALORTASAAMBIENTALALC ,SUB_TASAAMBIENTALALC,SOBRE_TASAAMBIENTALALC ,COSTOM3AL,PORCALCANTARILLADO,
                SUBBASICO,SUBCOMPLEMENTARIO ,SOBREFIJO,SOBREBASICO,SOBRECOMPLEMENTARIO,SOBRESUNTUARIO,SUBFIJO,SUBSUNTUARIO,
                SUBALCBASICO,SUBALCCOMPLEMENTARIO,SUBALCSUNTUARIO,SOBREALCBASICO,SOBREALCCOMPLEMENTARIO,SOBREALCSUNTUARIO,
                SOBREALCFIJO,SUBALCFIJO,CCS_720, SUBCCS_720, SOBRECCS_720, CBLS_720, SUBCBLS_720, SOBRECBLS_720, CLUS_720,
                SUBCLUS_720, SOBRECLUS_720, CRT_720, SUBCRT_720, SOBRECRT_720, CDF_720 , SUBCDF_720, SOBRECDF_720, CTL_720,
                SUBCTL_720, SOBRECTL_720, VBA_720, SUBVBA_720, SOBREVBA_720, DES_CCS_720, DES_SUBCCS_720, DES_SOBRECCS_720,
                DES_CBLS_720 , DES_SUBCBLS_720, DES_SOBRECBLS_720, DES_CLUS_720, DES_SUBCLUS_720, DES_SOBRECLUS_720,
                DES_CRT_720, DES_SUBCRT_720, DES_SOBRECRT_720, DES_CDF_720, DES_SUBCDF_720, DES_SOBRECDF_720 , DES_CTL_720,
                DES_SUBCTL_720, DES_SOBRECTL_720, DES_VBA_720, DES_SUBVBA_720, DES_SOBREVBA_720,VALORFREC1,VALORFREC2,VALORFREC3,
                VALORFREC4,VALORFREC5,VALORFREC6,VALORFREC7,VALORFREC8,VALORFREC9,SUBFREC1,SUBFREC2,SUBFREC3,SUBFREC4,SUBFREC5,
                SUBFREC6,SUBFREC7,SUBFREC8,SUBFREC9,SOBREFREC1,SOBREFREC2,SOBREFREC3,SOBREFREC4,SOBREFREC5,SOBREFREC6,SOBREFREC7,
                SOBREFREC8,SOBREFREC9,ASEODOMICILIARIO,ASEOCONSUMO,RECTRANSBARRIDOLIMP,DISPFINALBARRIDOLIMP,PESOASEO1,ASEOUNICO,
                TARIFAASEO1,SOBREASEOTARIFA1,SUBASEOTARIFA1,PESOASEO2,TARIFAASEO2 ,SOBREASEOTARIFA2,SUBASEOTARIFA2,PESOASEO3 ,
                TARIFAASEO3 ,SOBREASEOTARIFA3,SUBASEOTARIFA3,ASEOBARRIDO ,TRAMOEXCEDENTE,DES_ASEOUNICO,DES_ASEDOMICILIARIO ,
                DES_ASEBARRIDO,DES_ASEOCONSUMO,ASEODESHABITADOS,DESCSEPARACIONENFUENTE,DESCNOPUERTAPUERTA,
                DES_TRAMOEXCEDENTE,COSTOM3AS,COSTOM3REC ,COSTOM3PN ,SUBASEOBARRIDO,SOBREASEOBARRIDO,SUBTRAMOEX,SOBRETRAMOEX,
                DES_SUBASEOUNICO,DES_SOBREASEOUNICO,SOBREASEOUNICO,SUBASEOUNICO,SUBASEODOMICILIARIO,SOBREASEODOMICILIARIO,
                SUBASEOCONSUMO,SOBREASEOCONSUMO,DES_SUBASEDOMICILIARIO,DES_SOBREASEDOMICILIARIO,DES_SUBASEBARRIDO,DES_SOBREASEBARRIDO,
                DES_SUBASEOCONSUMO,DES_SOBREASEOCONSUMO,DES_SUBTRAMOEXCEDENTE,DES_SOBRETRAMOEXCEDENTE,SUBASEO_DESHABITADO,
                SOBREASEO_DESHABITADO ,COSTOFIJOAC, COSTOFIJOAL,USOSUPERSERVICIOS,POR_RECARGO668,RECARGOFIJOMORA,PORCENTAJEFECHA2,RECARGOFIJO,
                COSTO_CCS_720  CCS,COSTO_CBLS_720  CBLS,COSTO_CLUS_720  CLUS,COSTO_CRT_720  CRT,COSTO_CDF_720  CDF,COSTO_CTL_720 CTL,
                COSTO_TRBL_720  TRBL ,COSTO_TRLU_720  TRLU,COSTO_TRRA_720  TRRA,COSTO_VBA_720  VBA,COSTO_FCS_720  FCS,
                COSTO_FCS1_720  FCS1
        INTO   MI_TARIFAS
        FROM   SP_TARIFAS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO      = UN_ANO
          AND  PERIODO  = UN_PERIODO
          AND  USO      = UN_USO
          AND  ESTRATO  = UN_ESTRATO;


    EXCEPTION WHEN NO_DATA_FOUND THEN
        UN_VALIDATARIFA:='No existe tarifa para el uso: '|| UN_USO || ' ,Estrato: '|| UN_ESTRATO || ', Periodo: ' ;
        UN_VALIDATARIFA:= UN_VALIDATARIFA || ' ' || PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO (UN_COMPANIA   => UN_COMPANIA ,
                                                                                      UN_ANO        => UN_ANO,
                                                                                      UN_PERIODO    => UN_PERIODO,
                                                                                      UN_FRECUENCIA => NULL) || ' ';
        MI_TARIFAS := NULL;
    END;
    RETURN MI_TARIFAS;
  END FC_CARGATARIFAS;

--4  
  FUNCTION FC_TIPOCALCULO
  (
  /*
    NAME              : FC_TIPOCALCULO --> Se separo de la funcion en access CalculoFacturacion
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 07/02/2017
    TIME              : 08:45 AM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Establece el tipo de calculo que un usuario puede tener retorna los siguentes valores:
                        P = Promedio = Cambia variable consumo por consumo promedio
                        D = deshabitado = Solo se cobra cargos fijos No se les calcula consumo promedio
                        H = chapetas = Lo mismo que deshabitado
                        C = Consumo = Calculo normal
                        T = Tarifa sin medicion Sin consumos.

    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones
                        UN_CONSUMOMANUSU  => Parametro que determina si el usaurio tiene consumos manuales.
                        UN_PARAMETROS     => Type con todos los parametros cargados en FC_CARGARPARFACTU
    MODIFICATIONS     :

    @NAME:    obtenerTipoCalculo
    @METHOD:  GET
  */
     UN_COMPANIA       IN     PCK_SUBTIPOS.TI_COMPANIA
    ,UN_RSUSUARIO      IN OUT TUSUARIO
    ,UN_CONSUMOMANUSU  IN     PCK_SUBTIPOS.TI_LOGICO
    ,UN_PARAMETROS     IN     TI_PARAMETRO

  ) RETURN VARCHAR2

  AS
  MI_TIPOCALCULO            VARCHAR2(2 CHAR);
  MI_BLDP                   NUMBER(1);
  MI_CONSUMO                NUMBER(20,2);
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION              BOOLEAN;        --Se crea para condiciones demasiado largas


  BEGIN

  /*TIPOCALCULO
  P = Promedio = Cambia variable consumo por consumo promedio
  D = deshabitado = Solo se cobra cargos fijos No se les calcula consumo promedio
  H = chapetas = Lo mismo que deshabitado
  C = Consumo
  T = Tarifa sin medicion Sin consumos, pero si con consumo promedio (En access si no tiene consumo y no tiene promedio se cobra tarifa sin medicion, Si tiene consumo promedio pasa a ser tipo de calculo P)
  */
    BEGIN

        MI_TIPOCALCULO := 'C';
        MI_CONSUMO := UN_RSUSUARIO.LECTURA - UN_RSUSUARIO.LECTURA1;

        IF (UN_RSUSUARIO.INDDESHABITADO <>0 OR UN_RSUSUARIO.CHAPETAS <>0 ) AND (UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1) THEN
          IF UN_RSUSUARIO.INDDESHABITADO <>0 THEN
            MI_TIPOCALCULO := 'D';
          ELSE
            MI_TIPOCALCULO := 'H';
          END IF;

        ELSIF UN_CONSUMOMANUSU = 0 THEN
          MI_CONDICION := UN_RSUSUARIO.NUMERODIGITOS > 0 AND MI_CONSUMO <= 0
                          AND (UN_RSUSUARIO.LECTURA = 0 Or UN_RSUSUARIO.LECTURA <= UN_RSUSUARIO.LECTURA1)
                          AND UN_RSUSUARIO.ANO || UN_RSUSUARIO.PERIODO <> UN_RSUSUARIO.ANOCAMBIOMEDIDOR || UN_RSUSUARIO.PERIODOCAMBIOMEDIDOR ;

          IF MI_CONDICION THEN
            MI_TIPOCALCULO := 'P';
            UN_RSUSUARIO.PROBLEMALECTURA :=-1;
          ELSE
            MI_TIPOCALCULO := 'C';
          END IF;
        END IF;

        MI_CONDICION := UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1
                        AND (UN_RSUSUARIO.ESTADO ='S' OR UN_RSUSUARIO.ESTADO = 'C') AND UN_RSUSUARIO.DESVIOSIGNIFICATIVO =0
                        AND UN_CONSUMOMANUSU = 0;

        IF MI_CONDICION THEN
          MI_TIPOCALCULO := 'C';

        ELSIF (UN_RSUSUARIO.ESTADO ='S' OR UN_RSUSUARIO.ESTADO ='C') AND UN_PARAMETROS('CALCONSUS').VALOR ='NO'  THEN
          MI_TIPOCALCULO :='C' ;
        END IF;


        --Tiene proceso de desviación se cobra promedio
        IF ( (UN_PARAMETROS('FACTSITIO').VALOR ='SI' AND UN_PARAMETROS('DESVIAENSITIO').VALOR ='SI') OR UN_PARAMETROS('AUTODESVIA').VALOR ='SI')
          AND UN_RSUSUARIO.DESVIOSIGNIFICATIVO <>0  THEN

          MI_TIPOCALCULO :='P';

        END IF;
        --CalculoDesincentivo
        IF UN_PARAMETROS('RES151').VALOR ='SI' AND UN_PARAMETROS('CALRES151').VALOR ='SI' AND UN_PARAMETROS('AUTODESVIA').VALOR ='NO'   THEN
          IF  UN_RSUSUARIO.DESVIOSIGNIFICATIVO <> 0  THEN
            MI_TIPOCALCULO :='P';
          END IF;
        END IF;

        MI_CONDICION := (UN_RSUSUARIO.ACUEDUCTO <>0 AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <= 0 AND
                         (UN_RSUSUARIO.ESTADO = 'A' Or (UN_RSUSUARIO.ESTADO = 'C' AND UN_PARAMETROS('CALCORTADOASEO').VALOR ='NO' )
                          Or (UN_RSUSUARIO.ESTADO = 'S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR='SI'))
                        );
        IF MI_CONDICION Then
          IF  UN_RSUSUARIO.INDDESHABITADO = 0 AND UN_PARAMETROS('BLDESHAB').VALOR = 'SI' THEN
            --Se evaluan respecto a los periodos del parámetro PERIODOS IGUALES PARA CONSUMO SIN MEDICION
              MI_BLDP :=-1;
              IF (UN_RSUSUARIO.LECTURA1 <> UN_RSUSUARIO.LECTURA2) AND TO_NUMBER(UN_PARAMETROS('INTDESHABPERIODOS').VALOR) > 2 THEN
                MI_BLDP :=0;
              END IF;
              IF (UN_RSUSUARIO.LECTURA2 <> UN_RSUSUARIO.LECTURA3) AND TO_NUMBER(UN_PARAMETROS('INTDESHABPERIODOS').VALOR) > 3 THEN
                MI_BLDP :=0;
              END IF;
              IF (UN_RSUSUARIO.LECTURA3 <> UN_RSUSUARIO.LECTURA4) AND TO_NUMBER(UN_PARAMETROS('INTDESHABPERIODOS').VALOR) > 4 THEN
                MI_BLDP :=0;
              END IF;
              IF (UN_RSUSUARIO.LECTURA4 <> UN_RSUSUARIO.LECTURA5) AND TO_NUMBER(UN_PARAMETROS('INTDESHABPERIODOS').VALOR) > 5 THEN
                MI_BLDP :=0;
              END IF;
              IF (UN_RSUSUARIO.LECTURA5 <> UN_RSUSUARIO.LECTURA6) AND TO_NUMBER(UN_PARAMETROS('INTDESHABPERIODOS').VALOR) > 6 THEN
                MI_BLDP :=0;
              END IF;

              IF MI_BLDP =-1 AND MI_CONSUMO =0 THEN
                MI_TIPOCALCULO := 'T';
              END IF;

          ELSIF (UN_RSUSUARIO.NUMERODIGITOS = 0 Or UN_RSUSUARIO.LECTURA = 0) AND UN_RSUSUARIO.LECTURA1 = 0 Or UN_RSUSUARIO.LECTURA2 = 0 AND MI_CONSUMO = 0  THEN
              IF NOT (  (UN_RSUSUARIO.INDDESHABITADO <> 0 Or UN_RSUSUARIO.CHAPETAS <> 0) AND (UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1)  ) THEN
                MI_TIPOCALCULO := 'T';
              END IF;
          END IF;
        END IF;

        MI_CONDICION := UN_RSUSUARIO.LECTURA <> 0 AND UN_RSUSUARIO.LECTURA1 <> 0 AND UN_RSUSUARIO.LECTURA1 >= UN_RSUSUARIO.LECTURA
                        AND MI_CONSUMO = 0 AND UN_RSUSUARIO.INDDESHABITADO = 0 AND UN_RSUSUARIO.CHAPETAS = 0
                        AND NOT (UN_RSUSUARIO.CARGOS_FIJOS_CERO <> 0  AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND UN_RSUSUARIO.ESTADO ='A') ;

        IF MI_CONDICION THEN
            MI_TIPOCALCULO := 'T';
        END IF;

        IF UN_CONSUMOMANUSU <>0 THEN
            MI_TIPOCALCULO := 'C';
        END IF;

        UN_RSUSUARIO.TIPOCALCULO := MI_TIPOCALCULO;
        RETURN MI_TIPOCALCULO;

    EXCEPTION WHEN OTHERS THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;

  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
    MI_MSGERROR(1).CLAVE := 'USUARIO';
    MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_TIPOCALCULO,
                               UN_TABLAERROR => 'SP_USUARIO',
                               UN_REEMPLAZOS => MI_MSGERROR);

  END FC_TIPOCALCULO;

--5  
  FUNCTION FC_VALIDAUSAURIOCALC
  (

  /*
   NAME              : FC_VALIDAUSAURIOCALC --> Se separo de la funcion en access CalculoFacturacion
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
   DATE MIGRADOR     : 10/02/2017
   TIME              : 10:35 AM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Funcion que valida si un usuario puede ser calculado o no Se llama desde FC_CALCULOFACTURACION
   PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones
                       UN_PARAMETROS     => Type con todos los parametros cargados en FC_CARGARPARFACTU
   MODIFICATIONS     :

   @NAME:    validarUsuarioCalcular
   @METHOD:  GET
  */

  UN_COMPANIA       IN     PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO      IN     TUSUARIO
  ,UN_PARAMETROS     IN     TI_PARAMETRO
  ,UN_CODERROR       IN OUT PCK_SUBTIPOS.TI_ENTERO
  ,UN_USUARIO        IN SP_USUARIO.CREATED_BY%TYPE :=''

  ) RETURN VARCHAR2

  AS
  MI_VALIDA         VARCHAR2(150 CHAR);
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TOTFACTURA     SP_USUARIO.TOTFACTURAPERACTUAL%TYPE;
  MI_INDPREPARADO   SP_USUARIO.INDPREPARADO%TYPE;

  MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;

  BEGIN
   BEGIN
     MI_VALIDA :='C';  --Estado correcto para calcular
     UN_CODERROR := 0;

     MI_TOTFACTURA := UN_RSUSUARIO.TOTFACTURAPERACTUAL;
     MI_INDPREPARADO := UN_RSUSUARIO.INDPREPARADO;

     IF NVL(UN_RSUSUARIO.BANCOPERPROCESO, ' ') <> ' ' Then
         MI_VALIDA := 'El usuario ya pagó. No se permite recalcular';
         UN_CODERROR := 1;
     END IF;

     IF UN_PARAMETROS('FINSITU').VALOR = 'SI' THEN  --Facturacion en sitio
         IF UN_RSUSUARIO.FIMM = 'P' Then
               MI_VALIDA := 'El usuario está en terreno. No se permite recalcular';
               UN_CODERROR := 8;
         END IF;

         /*IF UN_RSUSUARIO.FIMM = 'F' Then
               MI_VALIDA := 7;
         End If*/ -- Pendiente por verificar en access necesita permiso para recalcular

         IF UN_RSUSUARIO.TIPOLECTURA <> 'L' AND UN_RSUSUARIO.TIPOLECTURA <> ' ' Then
              MI_VALIDA := 'La lectura de Fimm no fue exitosa';
              UN_CODERROR := 10;
         END IF;
     END IF;

     IF UN_RSUSUARIO.ESTADO ='R' THEN
         MI_VALIDA := 'El usuario está retirado';
         UN_CODERROR := 9;
         MI_TOTFACTURA := 0;
     END IF;

     IF UN_RSUSUARIO.ESTADO ='S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR ='NO' THEN
         MI_VALIDA := 'El usuario está suspendido';
         UN_CODERROR := 2;
         MI_TOTFACTURA := 0;
     END IF;

     IF UN_RSUSUARIO.PERIODOSNOCOBROFAC <>0 THEN
         MI_VALIDA := 'El usuario tiene periodos de No Cobro. No se permite calcular';
         UN_CODERROR := 3;
         MI_INDPREPARADO := 0;
         MI_TOTFACTURA := 0;
     END IF;

     BEGIN
         BEGIN
           IF MI_VALIDA <> 'C' THEN
               MI_CAMPOS :='  TOTFACTURAPERACTUAL = '|| MI_TOTFACTURA ||'
                             ,INDPREPARADO        = '|| MI_INDPREPARADO ||'
                             ,MODIFIED_BY         = '''|| UN_USUARIO ||'''
                             ,DATE_MODIFIED       = SYSDATE ';

               MI_TABLA :='SP_USUARIO';

               MI_CONDICION := '    COMPANIA   = ''' || UN_COMPANIA || '''
                                AND CICLO      = '|| UN_RSUSUARIO.CICLO ||'
                                AND CODIGORUTA = ''' || UN_RSUSUARIO.CODIGORUTA || '''
                                AND ANO        = '|| UN_RSUSUARIO.ANO||'
                                AND PERIODO    = ''' || UN_RSUSUARIO.PERIODO || '''  ';

               MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
           END IF;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
         END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTVALIDACALCULO,
                                      UN_TABLAERROR => MI_TABLA);
     END;
     RETURN MI_VALIDA;


   EXCEPTION WHEN OTHERS THEN
     RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
   END;

  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
   MI_MSGERROR(1).CLAVE := 'USUARIO';
   MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_VALIDACALCULO,
                              UN_TABLAERROR => 'SP_USUARIO',
                              UN_REEMPLAZOS => MI_MSGERROR);


  END FC_VALIDAUSAURIOCALC;

--6
  FUNCTION FC_CONSUMOS
  (

     /*
       NAME              : FC_CONSUMOS --> Se separo y unifico de la funcion en access CalculoFacturacion
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 01/02/2017
       TIME              : 02:11 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Establece los consumos de acueducto y alcantarillado con sus respectivos rangos de consumo

       PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones y actualizaciones
                           UN_TIPOCALCULO    => Tipo de calculo con el que se va a calcular el consumo
                           UN_CONSUMOMANUSU  => Parámetro que detarmina si el usuario tiene consumos manuales
                           UN_PARAMETROS     => Type con todos los parametros cargados en FC_CARGARPARFACTU
                           UN_MENSAJEFRAUDE  => Mensaje que se establece para el usuario si se tienen fraudes
                           UN_ENSERIE        => Define si se está calculando individual o en lote
                           UN_RSTARIFA       => Type de tipo registro de las tarifas de usuario.
       MODIFICATIONS     :

       @NAME:    obtenerConsumosCalculo
       @METHOD:  GET
     */
    UN_COMPANIA            IN      PCK_SUBTIPOS.TI_COMPANIA
   ,UN_RSUSUARIO           IN OUT  TUSUARIO
   ,UN_TIPOCALCULO         IN      SP_USUARIO.TIPOCALCULO%TYPE
   ,UN_CONSUMOMANUSU       IN      PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
   ,UN_PARAMETROS          IN      TI_PARAMETRO
   ,UN_MENSAJEFRAUDE       IN OUT  VARCHAR2
   ,UN_ENSERIE             IN      PCK_SUBTIPOS.TI_LOGICO
   ,UN_RSTARIFA            IN      TTARIFAS
   ,UN_CONSUMODEFINITIVO   IN      PCK_SUBTIPOS.TI_LOGICO  DEFAULT -1   --Indica si se tiene que calcular la desviacion con el consumo real o con el consumo definitivo

  ) RETURN NUMBER
  AS
  MI_CONSUMOPROM             SP_USUARIO.CONSUMOPROM%TYPE               DEFAULT 0;
  MI_CNSTEMP                 SP_USUARIO.CONSUMO%TYPE                   DEFAULT 0;
  MI_MSJESTADOFRAUDE         VARCHAR2(15);
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;

  MI_RSMEDIDORCAMBIO         SYS_REFCURSOR;
  MI_RSFRAUDES               SYS_REFCURSOR;
  MI_PORCENTAJECONSUMO       SP_USUARIO.PORCENTAJEAPLICAR%TYPE         DEFAULT 0;
  --Acueducto
  MI_CONSUMO                 SP_USUARIO.CONSUMO%TYPE                   DEFAULT 0;
  MI_CONSUMO1                SP_USUARIO.CONSUMOBASICO%TYPE             DEFAULT 0;
  MI_CONSUMO2                SP_USUARIO.CONSUMOCOMPLEMENTARIO%TYPE     DEFAULT 0;
  MI_CONSUMO3                SP_USUARIO.CONSUMOSUNTUARIO%TYPE          DEFAULT 0;
  --Alcantarillado
  MI_CONSUMOALC              NUMBER(20,2);    --Consumo Alc, Es el mismo que Acu excepto que tenga consumos manuales
  MI_CONSUMO1ALC             SP_USUARIO.CONSUMOBASICOALC%TYPE          DEFAULT 0;
  MI_CONSUMO2ALC             SP_USUARIO.CONSUMOCOMPLEMENTARIOALC%TYPE  DEFAULT 0;
  MI_CONSUMO3ALC             SP_USUARIO.CONSUMOSUNTUARIOALC%TYPE       DEFAULT 0;

  MI_INQ                     SP_USUARIO.INQUILINATOS%TYPE              DEFAULT 1; --Numero Inquilinatos
  MI_COBRADOS                SP_TARIFAS.METROS_NO_COBRADOS%TYPE        DEFAULT 0;
  MI_PROMEDIOTEMP            SP_USUARIO.CONSUMOPROM%TYPE;

  BEGIN
   BEGIN
     <<CONSUMO>>
       MI_CONSUMO:=0;

       IF (UN_RSUSUARIO.ANOENTRADANUEVOUSUARIO = UN_RSUSUARIO.ANO) AND (UN_RSUSUARIO.PERENTRADANUEVOUSUARIO = UN_RSUSUARIO.PERIODO) THEN
            UN_RSUSUARIO.CONSUMO1 := UN_RSUSUARIO.CONSUMOPROM;
            UN_RSUSUARIO.CONSUMO2 := UN_RSUSUARIO.CONSUMOPROM;
            UN_RSUSUARIO.CONSUMO3 := UN_RSUSUARIO.CONSUMOPROM;
            UN_RSUSUARIO.CONSUMO4 := UN_RSUSUARIO.CONSUMOPROM;
            UN_RSUSUARIO.CONSUMO5 := UN_RSUSUARIO.CONSUMOPROM;
            UN_RSUSUARIO.CONSUMO6 := UN_RSUSUARIO.CONSUMOPROM;
       END IF;

       IF UN_RSUSUARIO.ANOENTRADANUEVOUSUARIO = UN_RSUSUARIO.ANO And UN_RSUSUARIO.PERENTRADANUEVOUSUARIO = UN_RSUSUARIO.PERIODO THEN
            IF UN_RSUSUARIO.LECTURAANTINICIAL <> 0 AND UN_RSUSUARIO.LECTURA1 <> 0 THEN
                UN_RSUSUARIO.LECTURA1 := UN_RSUSUARIO.LECTURAANTINICIAL;
                UN_RSUSUARIO.PERIODOSATRASO := UN_RSUSUARIO.PERIODOSATRASOINICIAL;
            END IF;
            IF UN_RSUSUARIO.LECTURAINICIAL <> 0 And UN_RSUSUARIO.LECTURA <> 0 THEN
                UN_RSUSUARIO.LECTURA := UN_RSUSUARIO.LECTURAINICIAL;
            END IF;
       END IF;

       IF UN_PARAMETROS('FRECUENCIA').VALOR ='M' THEN
           MI_CONSUMOPROM :=PCK_SYSMAN_UTL.FC_ROUND(((UN_RSUSUARIO.CONSUMO1 + UN_RSUSUARIO.CONSUMO2 + UN_RSUSUARIO.CONSUMO3 + UN_RSUSUARIO.CONSUMO4 + UN_RSUSUARIO.CONSUMO5 + UN_RSUSUARIO.CONSUMO6)  / 6) + 0.001 );
       ELSE
           MI_CONSUMOPROM :=PCK_SYSMAN_UTL.FC_ROUND(((UN_RSUSUARIO.CONSUMO1 + UN_RSUSUARIO.CONSUMO2 + UN_RSUSUARIO.CONSUMO3  ) / 3) + 0.001 );
       END IF;

       UN_RSUSUARIO.PROBLEMALECTURA := 0;
       IF UN_RSUSUARIO.LECTURA <> 0 And UN_RSUSUARIO.NUMERODIGITOS > 0 THEN
         MI_CONSUMO := UN_RSUSUARIO.LECTURA - UN_RSUSUARIO.LECTURA1;   --Consumo Normal por diferencia de lecturas
       END IF;

       UN_RSUSUARIO.CRITICACONSUMO := 'N';  --Normal
       IF MI_CONSUMO < 0 THEN
         IF UN_RSUSUARIO.LECTURA < UN_RSUSUARIO.LECTURA1 THEN    --Problemas de medidores y lecturas invertidas
             IF UN_RSUSUARIO.LECTURA1 >= (POWER(10,UN_RSUSUARIO.NUMERODIGITOS)- 500) And UN_RSUSUARIO.LECTURA <= 500 THEN
               MI_CONSUMO := POWER(10,UN_RSUSUARIO.NUMERODIGITOS) - UN_RSUSUARIO.LECTURA1 + UN_RSUSUARIO.LECTURA;
               UN_RSUSUARIO.CRITICACONSUMO := 'V';  -- Medidor dió la Vuelta
             ELSE
               UN_RSUSUARIO.CRITICACONSUMO := 'I';  -- Medidor Invertido
               UN_RSUSUARIO.PROBLEMALECTURA:= -1;
               MI_CONSUMO := 0;
             END IF;
         ELSE
             UN_RSUSUARIO.CRITICACONSUMO := 'E';  -- Error en Lectura
             UN_RSUSUARIO.PROBLEMALECTURA:= -1;
             MI_CONSUMO := 0;
         END IF;
       END IF;

       IF UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 THEN
         UN_RSUSUARIO.CRITICACONSUMO := 'T'; -- medidor Trancado
       END IF;
       IF MI_CONSUMO > MI_CONSUMOPROM * 2 And MI_CONSUMO > 30 THEN
         UN_RSUSUARIO.CRITICACONSUMO := 'E'; -- Consumo Elevado
       END IF;

       --Suma el consumo antes de cambio de medidor cuando el cambio se hizo en el periodo en proceso
       IF (UN_RSUSUARIO.ANO || UN_RSUSUARIO.PERIODO) = (UN_RSUSUARIO.ANOCAMBIOMEDIDOR || UN_RSUSUARIO.PERIODOCAMBIOMEDIDOR) THEN
         IF UN_PARAMETROS('CONMIXTO').VALOR ='SI' THEN
           <<CAMBIOMEDIDOR>>
           FOR MI_RSMEDIDORCAMBIO IN
           (
             SELECT LECTURAINICIAL
             FROM   SP_MEDIDOR
             WHERE  COMPANIA   = UN_COMPANIA
               AND  CICLO      = UN_RSUSUARIO.CICLO
               AND  CODIGORUTA = UN_RSUSUARIO.CODIGORUTA
               AND  ANO        = UN_RSUSUARIO.ANO
               AND  PERIODO    = UN_RSUSUARIO.PERIODO
               AND  ELEMCAMBIO = 'Medidor'
           )
           LOOP
             MI_CONSUMO := UN_RSUSUARIO.LECTURA - MI_RSMEDIDORCAMBIO.LECTURAINICIAL;
           END LOOP CAMBIOMEDIDOR;
         END IF;
         MI_CONSUMO := MI_CONSUMO + UN_RSUSUARIO.CONSUMOMEDIDORANT;
         UN_RSUSUARIO.INDMEDCAMBIADO := -1;
       END IF;

       IF UN_RSUSUARIO.PORCENTAJEAPLICAR <= 0 OR UN_RSUSUARIO.PORCENTAJEAPLICAR = 1 Then
           UN_RSUSUARIO.PORCENTAJEAPLICAR := 100;
       END IF;

       MI_PORCENTAJECONSUMO := UN_RSUSUARIO.PORCENTAJEAPLICAR / 100;
       MI_CONSUMO := floor(MI_CONSUMO * MI_PORCENTAJECONSUMO);

       --Consumos manuales
       IF UN_CONSUMOMANUSU <> 0 THEN
         MI_CONSUMO := UN_RSUSUARIO.CONSUMOACU;
       END IF;

       --Consumos acumualados
       IF (UN_RSUSUARIO.ACUMULADO  <>0 AND UN_RSUSUARIO.LECTURA <= MI_CONSUMO ) AND (UN_RSUSUARIO.PERIODOSINLECTURA = TO_NUMBER(UN_PARAMETROS('DBLPERIODOSDESCUENTAACUMULADO').VALOR)) THEN
           UN_RSUSUARIO.ACUMULADO:= 0;
       END IF;

       MI_CNSTEMP :=0;
       IF UN_PARAMETROS('DESCUENTAACUMULADO').VALOR ='SI' THEN
         IF MI_CONSUMO >0 AND UN_RSUSUARIO.PERIODOSINLECTURA >0 AND UN_RSUSUARIO.ACUMULADO > 0 THEN
           IF TO_NUMBER(UN_PARAMETROS('DBLPERIODOSDESCUENTAACUMULADO').VALOR) = 0 OR (TO_NUMBER(UN_PARAMETROS('DBLPERIODOSDESCUENTAACUMULADO').VALOR) > 0
             AND UN_RSUSUARIO.PERIODOSINLECTURA <= TO_NUMBER(UN_PARAMETROS('DBLPERIODOSDESCUENTAACUMULADO').VALOR)) THEN
               MI_CNSTEMP := MI_CONSUMO;
               IF UN_RSUSUARIO.INDCALCULADO <>0 AND UN_PARAMETROS('INDACUMULADOPER').VALOR ='SI' THEN
                   MI_CONSUMO := MI_CONSUMO - UN_RSUSUARIO.ACUMULADOPER;
               ELSE
                   IF MI_CONSUMO < UN_RSUSUARIO.ACUMULADO THEN
                       UN_RSUSUARIO.ACUMULADOPER := MI_CONSUMO;
                   ELSE
                       UN_RSUSUARIO.ACUMULADOPER := UN_RSUSUARIO.ACUMULADO;
                   END IF;
               END IF;
               IF MI_CONSUMO < 0 THEN
                 MI_CONSUMO :=0;
               END IF;
           END IF;
         END IF;
       END IF;
       --Actualización de consumos acumulados si quedan para el siguiente periodo
       IF NOT(UN_RSUSUARIO.INDCALCULADO <>0 AND UN_PARAMETROS('INDACUMULADOPER').VALOR ='SI') THEN
           IF UN_RSUSUARIO.DESCONTADO = 0 THEN
               UN_RSUSUARIO.ACUMULADO := CASE WHEN (UN_RSUSUARIO.ACUMULADO - MI_CNSTEMP)<0 THEN 0 END;
               UN_RSUSUARIO.DESCONTADO := -1;
           END IF;
       END IF;


       IF UN_RSUSUARIO.DESVIOSIGNIFICATIVO =0 AND UN_RSUSUARIO.METROSDESVIACION <> 0 THEN   --Desviaciones cerradas en un periodo y cobradas en otro
          MI_CONSUMO:= MI_CONSUMO + UN_RSUSUARIO.METROSDESVIACION;
       END IF;

       IF UN_TIPOCALCULO ='P' THEN --Si es consumo promedio, Si tiene desviaciones activas cobra promedio se evalua en funcion consumos
          IF UN_CONSUMODEFINITIVO = -1 THEN
               MI_PROMEDIOTEMP := MI_CONSUMOPROM;
               IF UN_RSUSUARIO.DESVIOSIGNIFICATIVO <>0 AND MI_CONSUMOPROM =0 THEN --Usuarios con Proceso de desviacion y consumo promedio cero
                   MI_PROMEDIOTEMP:= UN_RSTARIFA.CONSUMOPROM_USOS_ESTRATOS;
               END IF;
               MI_CONSUMO := MI_PROMEDIOTEMP + UN_RSUSUARIO.METROSDESVIACION;
          END IF;
       END IF;

       --No cobrar consumo a suscriptores suspendidos o cortados.
       IF (UN_RSUSUARIO.ESTADO ='S' OR UN_RSUSUARIO.ESTADO ='C') AND UN_PARAMETROS('CALCONSUS').VALOR ='NO'  THEN
           MI_CONSUMO := 0;
           --Si las lecturas son diferentes revisa si el suscriptor tiene proceso de fraude activo para informar al usuario.
           IF  UN_RSUSUARIO.LECTURA <> UN_RSUSUARIO.LECTURA1 AND UN_ENSERIE = 0 THEN
               <<FRAUDES>>
               MI_MSJESTADOFRAUDE :=CASE WHEN UN_RSUSUARIO.ESTADO ='C' THEN 'CORTADO' ELSE 'SUSPENDIDO' END ;
               FOR MI_RSFRAUDES IN
               (
                 SELECT CONSECUTIVO
                 FROM   SP_FRAUDES
                 WHERE  COMPANIA     = UN_COMPANIA
                   AND  CODIGORUTA   = UN_RSUSUARIO.CODIGORUTA
                   AND  ESTADO       ='A'
               )
               LOOP
                 UN_MENSAJEFRAUDE   := 'Suscriptor ' || MI_MSJESTADOFRAUDE  || ' con proceso de fraude número ' || MI_RSFRAUDES.CONSECUTIVO || ' Activo';
               END LOOP FRAUDES;
               IF UN_MENSAJEFRAUDE ='C' OR UN_MENSAJEFRAUDE =NULL THEN  --Viene del estado correcto de validacion de calculo
                   UN_MENSAJEFRAUDE :='Suscriptor ' || MI_MSJESTADOFRAUDE  || ' con consumo, posible FRAUDE.'  ;
               END IF;
           END IF;
       END IF;

       --Consumos en ceros, si se cobra o no los cargos fijos cuando el consumo = 0 y el usuario está activo
       IF UN_RSUSUARIO.CARGOS_FIJOS_CERO <> 0  AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND UN_RSUSUARIO.ESTADO ='A' THEN
           MI_CONSUMO := 0;
       END IF;

       IF UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND (UN_RSUSUARIO.ESTADO ='S' OR UN_RSUSUARIO.ESTADO = 'C')
          AND UN_RSUSUARIO.DESVIOSIGNIFICATIVO =0 AND UN_CONSUMOMANUSU = 0 THEN
           MI_CONSUMO :=0;
       END IF;

       --Procesos de micromediciones
       IF UN_RSUSUARIO.PORMICRO <> 0 AND UN_PARAMETROS('FACTSITIO').VALOR ='SI' AND UN_PARAMETROS('MICROSITIO').VALOR ='SI'
        AND UN_RSUSUARIO.DESVIOSIGNIFICATIVO =0 AND UN_RSUSUARIO.FIMM ='F' THEN
           MI_CONSUMO := PCK_SERVICIOS_PUBLICOS_COM5.FC_MICROMEDICIONSITIO(UN_COMPANIA   => UN_COMPANIA ,
                                                                           UN_CICLO      => UN_RSUSUARIO.CICLO,
                                                                           UN_CODIGORUTA => UN_RSUSUARIO.CODIGORUTA,
                                                                           UN_ANIO       => UN_RSUSUARIO.ANO,
                                                                           UN_PERIODO    => UN_RSUSUARIO.PERIODO ,
                                                                           UN_MOMENTO    => 'C',
                                                                           UN_USUARIO    => '');
       END IF;


       --Calculo Totalizadores
       IF NVL(UN_RSUSUARIO.TOTALIZADOR,'') ='T' THEN
           MI_CONSUMO := MI_CONSUMO - FC_CONSUMOTOTALIZADOR (UN_COMPANIA      => UN_COMPANIA
                                                            ,UN_RSUSUARIO     => UN_RSUSUARIO
                                                            ,UN_TOTACONCODIGO => CASE WHEN (UN_PARAMETROS('TOTACONCODIGO').VALOR <>'SI')
                                                                                      THEN 0
                                                                                      ELSE -1
                                                                                  END);
           MI_CONSUMO := CASE WHEN MI_CONSUMO < 0
                               THEN 0
                               ELSE MI_CONSUMO
                          END;
       END IF;

       --Calculo Macromedidores
       IF NVL(UN_RSUSUARIO.TOTALIZADOR,'') ='M' THEN
           MI_CONSUMO := MI_CONSUMO - FC_CONSUMOMACROMEDIDOR (UN_COMPANIA  => UN_COMPANIA
                                                            ,UN_RSUSUARIO => UN_RSUSUARIO);
           MI_CONSUMO := CASE WHEN MI_CONSUMO < 0
                               THEN 0
                               ELSE MI_CONSUMO
                          END;
       END IF;

       MI_INQ := CASE WHEN UN_RSUSUARIO.INQUILINATOS >0
                      THEN UN_RSUSUARIO.INQUILINATOS
                      ELSE 1
                 END;

       --Consumos no cobrados por tarifa,
       IF UN_PARAMETROS('DESMETAC').VALOR ='SI' THEN
          IF MI_CONSUMO - UN_RSTARIFA.METROS_NO_COBRADOS >0 THEN
              MI_CONSUMO := MI_CONSUMO - UN_RSTARIFA.METROS_NO_COBRADOS;
              MI_COBRADOS := UN_RSTARIFA.METROS_NO_COBRADOS;
          ELSE
              MI_COBRADOS := MI_CONSUMO;
              MI_CONSUMO :=0;
          END IF;
       END IF;

       --Calculo consumos por rangos
       IF MI_CONSUMO < UN_RSTARIFA.CONSUMOBASICO * MI_INQ THEN
          MI_CONSUMO1 := MI_CONSUMO;
       ELSE
          MI_CONSUMO1 := UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
          IF MI_CONSUMO <= UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ THEN
              MI_CONSUMO2 := MI_CONSUMO - UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
          ELSE
              MI_CONSUMO2 := UN_RSTARIFA.CONSUMOCOMPLEMENTARIO - UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
              IF MI_CONSUMO > UN_RSTARIFA.CONSUMOSUNTUARIO * MI_INQ THEN
                  MI_CONSUMO3 := MI_CONSUMO - UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ;
              END IF;
          END IF;
       END IF;

       IF UN_PARAMETROS('DESMETAC').VALOR ='SI' THEN
          MI_CONSUMO := MI_CONSUMO + MI_COBRADOS;
       END IF;

       IF UN_TIPOCALCULO ='T' THEN
          MI_CONSUMO := UN_RSTARIFA.M3SINMEDICION;
       END IF;

       --Calculo consumo Alcantarillado CalculaConsumoAlc
       MI_CONSUMOALC := MI_CONSUMO;
       IF NOT(UN_PARAMETROS('AUTODESVIA').VALOR ='SI' AND MI_CONSUMOALC <>0 ) THEN
            MI_CONSUMOALC := 0;
       END IF;

       IF UN_CONSUMOMANUSU = 0 THEN
            IF UN_PARAMETROS('DESMETAC').VALOR ='SI' THEN --En acueducto se desconto los metros para los rangos, Para alc debe tomar los rangos correctos
                MI_CONSUMOALC :=  MI_CONSUMO;
                IF MI_CONSUMO < UN_RSTARIFA.CONSUMOBASICO * MI_INQ Then
                   MI_CONSUMO1ALC := MI_CONSUMO;
                ELSE
                   MI_CONSUMO1ALC := UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
                   IF MI_CONSUMO <= UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ Then
                      MI_CONSUMO2ALC := MI_CONSUMO - UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
                   ELSE
                      MI_CONSUMO2ALC := (UN_RSTARIFA.CONSUMOCOMPLEMENTARIO - UN_RSTARIFA.CONSUMOBASICO ) * MI_INQ;
                      IF MI_CONSUMO > UN_RSTARIFA.CONSUMOSUNTUARIO * MI_INQ Then
                         MI_CONSUMO3ALC := MI_CONSUMO - UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ;
                      END IF;
                   END IF;
                END IF;
            ELSE
                MI_CONSUMOALC :=  MI_CONSUMO;
                MI_CONSUMO1ALC := MI_CONSUMO1;
                MI_CONSUMO2ALC := MI_CONSUMO2;
                MI_CONSUMO3ALC := MI_CONSUMO3;
            END IF;
       ELSE
            IF NOT(UN_PARAMETROS('AUTODESVIA').VALOR ='SI' AND MI_CONSUMOALC <>0 ) THEN    --Maneja consumo manual de alcantarillado
                MI_CONSUMOALC := UN_RSUSUARIO.CONSUMOALC;
            END IF;

            IF MI_CONSUMOALC < UN_RSTARIFA.CONSUMOBASICO THEN
                MI_CONSUMO1ALC := MI_CONSUMOALC;
            ELSE
                MI_CONSUMO1ALC := UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
                IF MI_CONSUMOALC <= UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ THEN
                    MI_CONSUMO2ALC := MI_CONSUMOALC - UN_RSTARIFA.CONSUMOBASICO * MI_INQ;
                ELSE
                    MI_CONSUMO2ALC := (UN_RSTARIFA.CONSUMOCOMPLEMENTARIO - UN_RSTARIFA.CONSUMOBASICO) * MI_INQ;
                    IF MI_CONSUMOALC > UN_RSTARIFA.CONSUMOSUNTUARIO * MI_INQ THEN
                       MI_CONSUMO3ALC := MI_CONSUMOALC - UN_RSTARIFA.CONSUMOCOMPLEMENTARIO * MI_INQ;
                    END IF;
                END IF;
            END IF;
       END IF;

       --Si tiene facturación en sitio y no llego de terreno no se cobra consumo
       IF UN_PARAMETROS('FINSITU').VALOR ='SI' THEN
           IF  NVL(UN_RSUSUARIO.FIMM,'') <> 'F' OR (NVL(UN_RSUSUARIO.TIPOLECTURA,' ') =' ' AND NVL(UN_RSUSUARIO.FIMM,' ') =' ' AND UN_RSUSUARIO.INDPREPARADO <> 0) THEN
              MI_CONSUMO :=0;
              MI_CONSUMO1 :=0;
              MI_CONSUMO2 := 0;
              MI_CONSUMO3 := 0;

              MI_CONSUMOALC := 0;
              MI_CONSUMO1ALC := 0;
              MI_CONSUMO2ALC := 0;
              MI_CONSUMO3ALC := 0;
           END IF;
       END IF;

       --Actualización de los campos en el type de usuarios.
       --Acueducto
       UN_RSUSUARIO.CONSUMOPROM := MI_CONSUMOPROM;
       UN_RSUSUARIO.CONSUMO := MI_CONSUMO;
       UN_RSUSUARIO.CONSUMOBASICO := MI_CONSUMO1;
       UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO := MI_CONSUMO2;
       UN_RSUSUARIO.CONSUMOSUNTUARIO := MI_CONSUMO3;
       --Alcantarillado
       UN_RSUSUARIO.CONSUMOBASICOALC := MI_CONSUMO1ALC;
       UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC := MI_CONSUMO2ALC;
       UN_RSUSUARIO.CONSUMOSUNTUARIOALC := MI_CONSUMO3ALC;
       --Este campo no existe en la tabla usuario es una variable que se usa en calculo cargo fijo
       UN_RSUSUARIO.COBRADOS := MI_COBRADOS;

       RETURN MI_CONSUMO;

       /*IF MI_CONSUMO < 0 THEN
           RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END IF;  */


   END;

  /*EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
     MI_MSGERROR(1).CLAVE := 'USUARIO';
     MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CONSUMOS,
                                UN_TABLAERROR => 'SP_USUARIO',
                                UN_REEMPLAZOS => MI_MSGERROR);
  */
  END FC_CONSUMOS;

--7  
  FUNCTION FC_CONSUMOTOTALIZADOR
  (
    /*
      NAME              : FC_CONSUMOTOTALIZADOR --> Se unifican las funciones de access CalcularTotalizador y CalcularTotalizadorConCodigo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 14/02/2017
      TIME              : 03:45 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Devuelve el consumo que se le debe restar a un totalizador de sus respectivos
                          usuarios de area comun.

      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones
                          UN_TOTACONCODIGO  => Parametro de función que determina que tipo de totalizador se va a calcular,
                                               Si viene en -1 se busca los usuarios de area comun por el codigo interno del totalizador
      MODIFICATIONS     :

      @NAME:    obtenerConsumoTotalizador
      @METHOD:  GET
    */
   UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO           IN TUSUARIO
  ,UN_TOTACONCODIGO       IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ) RETURN NUMBER
  AS
  MI_RSTOTALIZADOR        SYS_REFCURSOR;
  MI_CONSUMOTOTALIZADOR   SP_USUARIO.CONSUMO%TYPE;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
      IF UN_TOTACONCODIGO <>0 THEN
        BEGIN
              SELECT SUM(UC.CONSUMO) CONSUMO
              INTO   MI_CONSUMOTOTALIZADOR
              FROM   SP_USUARIO TOT INNER JOIN SP_USUARIO UC ON
                     (TOT.COMPANIA      = UC.COMPANIA) AND (TOT.CODIGOINTERNO = UC.CODTOTALIZADOR) AND (TOT.CICLO = UC.CICLO)
              WHERE  TOT.COMPANIA       = UN_COMPANIA
                AND  TOT.CICLO				  = UN_RSUSUARIO.CICLO
                AND  TOT.CODIGOINTERNO  = UN_RSUSUARIO.CODIGOINTERNO
                AND  TOT.TOTALIZADOR		= 'T'
                AND  UC.TOTALIZADOR		  = 'S' ;

        EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CONSUMOTOTALIZADOR :=0;
        END;

      ELSE    --Totalizadores que se configuran en orden de codigo de ruta
        BEGIN
          BEGIN
            MI_CONSUMOTOTALIZADOR :=0;
            <<TOTALIZADORSICOD>>
            FOR MI_RSTOTALIZADOR IN
             (
              SELECT  COMPANIA,CICLO,CODIGORUTA,CONSUMO, TOTALIZADOR
              FROM    SP_USUARIO
              WHERE   COMPANIA = UN_COMPANIA
                AND   CICLO = UN_RSUSUARIO.CICLO
                AND   CODIGORUTA BETWEEN ' ' AND UN_RSUSUARIO.CODIGORUTA
             ORDER BY COMPANIA,CICLO,CODIGORUTA
            )
            LOOP
              IF MI_RSTOTALIZADOR.CODIGORUTA <> UN_RSUSUARIO.CODIGORUTA  THEN
                IF MI_RSTOTALIZADOR.TOTALIZADOR <> 'S' THEN
                  EXIT TOTALIZADORSICOD;
                END IF;
                MI_CONSUMOTOTALIZADOR := MI_CONSUMOTOTALIZADOR + MI_RSTOTALIZADOR.CONSUMO;
              END IF;
            END LOOP TOTALIZADORSICOD;

          EXCEPTION WHEN OTHERS THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_TOTALIZASINCOD ,
                                     UN_TABLAERROR => 'SP_USUARIO',
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END IF;
      RETURN NVL(MI_CONSUMOTOTALIZADOR,0);
  END FC_CONSUMOTOTALIZADOR;

--8
  FUNCTION FC_CONSUMOMACROMEDIDOR
  (
    /*
      NAME              : FC_CONSUMOMACROMEDIDOR --> En Access CalcularMacromedidor
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 15/02/2017
      TIME              : 08:30 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Devuelve el consumo que se le debe restar a un macromedidor de sus respectivos
                          usuarios totalizadores
      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones
                          UN_TOTACONCODIGO  => Parametro de función que determina que tipo de totalizador se va a calcular,
                                               Si viene en -1 se busca los usuarios de area comun por el codigo interno del totalizador
      MODIFICATIONS     :

      @NAME:    obtenerConsumoMacromedidor
      @METHOD:  GET
    */
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_RSUSUARIO           IN TUSUARIO
   ) RETURN NUMBER
   AS
  MI_CONSUMOMACRO       SP_USUARIO.CONSUMO%TYPE;
  BEGIN
    BEGIN

      SELECT SUM(TOT.CONSUMO) CONSUMO
      INTO   MI_CONSUMOMACRO
      FROM   SP_USUARIO MC INNER JOIN SP_USUARIO TOT  ON   (MC.COMPANIA = TOT.COMPANIA)
       AND   (MC.CODIGOINTERNO = TOT.CODMACROMEDIDOR) AND (MC.CICLO = TOT.CICLO)
      WHERE  MC.COMPANIA      = UN_COMPANIA
        AND  MC.CICLO         = UN_RSUSUARIO.CICLO
        AND  MC.CODIGOINTERNO = UN_RSUSUARIO.CODIGOINTERNO
        AND  MC.TOTALIZADOR   = 'M'
        AND  TOT.TOTALIZADOR  = 'T' ;

    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CONSUMOMACRO :=0;
    END;
    RETURN NVL(MI_CONSUMOMACRO,0);

  END FC_CONSUMOMACROMEDIDOR;

--9  
  FUNCTION FC_CALCULOACUEDUCTO
    /*  
      @NAME:    obtenerCalculoAcueducto
      @METHOD:  GET
    */
  (
  UN_COMPANIA            IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO           IN OUT  TUSUARIO
  ,UN_RSTARIFA            IN      TTARIFAS
  ,UN_TIPOCALCULO         IN      SP_USUARIO.TIPOCALCULO%TYPE
  ,UN_PARAMETROS          IN      TI_PARAMETRO
  ,UN_FACTURADOACT        IN OUT  TI_FACTURADO
  ) RETURN NUMBER
  AS
  MI_CONDICIONACU    BOOLEAN;
  MI_TOTALACU        NUMBER(20,0)    DEFAULT 0;
  MI_SUBACU          SP_USUARIO.SUBCONSUMOAC%TYPE;
  MI_SOBREACU        SP_USUARIO.SOBRECONSUMOAC%TYPE;


  BEGIN
    MI_CONDICIONACU := UN_RSUSUARIO.ACUEDUCTO <>0 AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <=0 AND
                       (UN_RSUSUARIO.ESTADO = 'A' OR (UN_RSUSUARIO.ESTADO = 'C' AND UN_PARAMETROS('CALCORTADOASEO').VALOR ='NO')
                                                  OR (UN_RSUSUARIO.ESTADO = 'S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' )
                       );
    PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ACUEDUCTO');

    UN_FACTURADOACT(1).FACTURADO :=0;       --Cargo Fijo
    UN_FACTURADOACT(2).FACTURADO :=0;       --Consumo
    UN_FACTURADOACT(5).FACTURADO :=0;       --Consumo Promedio
    UN_FACTURADOACT(19).FACTURADO :=  0;    --Tarifa sin medición
    UN_FACTURADOACT(43).FACTURADO :=  0;    --Tasa ambiental

    IF MI_CONDICIONACU THEN

        UN_RSUSUARIO.COSTOFIJOAC                := UN_RSTARIFA.COSTOFIJOAC;
        UN_RSUSUARIO.COSTOM3AC                  := UN_RSTARIFA.COSTOM3AC;
        UN_RSUSUARIO.TARIFAM3BASICOAC           := UN_RSTARIFA.TARIFABASICO;
        UN_RSUSUARIO.TARIFAM3COMPLEMENTARIOAC   := UN_RSTARIFA.TARIFACOMPLEMENTARIO;
        UN_RSUSUARIO.TARIFAM3SUNTUARIOAC        := UN_RSTARIFA.TARIFASUNTUARIO;

        UN_FACTURADOACT(1).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.CARGOFIJO + (UN_RSUSUARIO.COBRADOS * UN_RSTARIFA.PRECIO_METROS_NO_COBRADOS));
        IF UN_TIPOCALCULO ='T' THEN  --Tarifa sin medición
            UN_FACTURADOACT(19).FACTURADO :=  PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SINMEDICION);
            UN_RSUSUARIO.SUBSINMEDICION :=  PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SUBSINMEDICION);
            UN_RSUSUARIO.SOBRESINMEDICION := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBRESINMEDICION);
        END IF;

        UN_FACTURADOACT(2).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOBASICO         * UN_RSTARIFA.TARIFABASICO ) +
                                        PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO * UN_RSTARIFA.TARIFACOMPLEMENTARIO ) +
                                        PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOSUNTUARIO      * UN_RSTARIFA.TARIFASUNTUARIO );

        IF UN_RSUSUARIO.ESTADO = 'S' AND  UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' Then
            IF NOT (UN_RSUSUARIO.LECTURA <> UN_RSUSUARIO.LECTURA1 AND UN_PARAMETROS('CALCONSUS').VALOR = 'SI') THEN
                UN_FACTURADOACT(2).FACTURADO :=0;
            END IF;
        ELSE
            IF UN_PARAMETROS('TASAAMBIENTAL').VALOR ='SI' AND UN_FACTURADOACT(2).FACTURADO > 0 THEN
                UN_FACTURADOACT(43).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.VALORTASAAMBIENTALAC *
                                                 (UN_RSUSUARIO.CONSUMOBASICO + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO + UN_RSUSUARIO.CONSUMOSUNTUARIO));
                IF UN_PARAMETROS('CONSUBSOBRETASA').VALOR ='SI' THEN
                    UN_RSUSUARIO.SUB_TASAAMBIENTALAC := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SUB_TASAAMBIENTALAC *
                                                        (UN_RSUSUARIO.CONSUMOBASICO + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO + UN_RSUSUARIO.CONSUMOSUNTUARIO));

                    UN_RSUSUARIO.SOBRE_TASAAMBIENTALAC := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBRE_TASAAMBIENTALAC *
                                                          (UN_RSUSUARIO.CONSUMOBASICO + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO + UN_RSUSUARIO.CONSUMOSUNTUARIO));
                END IF;
            END IF;
        END IF;

        UN_RSUSUARIO.COSTOCONSUMOAC := floor((UN_RSUSUARIO.CONSUMO * UN_RSTARIFA.COSTOM3AC)  + 0.5);

        IF UN_TIPOCALCULO ='T' THEN
            UN_FACTURADOACT(2).FACTURADO := 0;
        END IF;

        IF UN_TIPOCALCULO ='P' AND UN_PARAMETROS('INDCALCULARPROMEDIOS').VALOR ='NO' THEN
            IF UN_RSUSUARIO.PERIODOSINLECTURA > 3 AND UN_PARAMETROS('SINMED3MESES').VALOR = 'SI' Then
                UN_FACTURADOACT(19).FACTURADO :=  PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SINMEDICION);
                UN_FACTURADOACT(2).FACTURADO :=0;
                UN_FACTURADOACT(5).FACTURADO :=0;
            ELSE
                UN_FACTURADOACT(5).FACTURADO := UN_FACTURADOACT(2).FACTURADO;
                UN_FACTURADOACT(2).FACTURADO :=0;
            END IF;
        END IF;

        IF UN_RSUSUARIO.PORCENTAJEAPLICAR <> 100 AND UN_RSUSUARIO.INDDESHABITADO <>0 THEN
            UN_FACTURADOACT(2).FACTURADO := 0;
        END IF;

        UN_RSUSUARIO.CARGOFIJO := UN_FACTURADOACT(1).FACTURADO;
        UN_RSUSUARIO.ACSINMEDICION := UN_FACTURADOACT(19).FACTURADO;
        UN_RSUSUARIO.VALCONSUMO := UN_FACTURADOACT(2).FACTURADO;
        UN_RSUSUARIO.VALCONSUMOPROM := UN_FACTURADOACT(5).FACTURADO;

        --Calculo subsidios y sobreprecios Acueducto
        MI_SUBACU := TRUNC(UN_RSTARIFA.SUBFIJO + UN_RSUSUARIO.CONSUMOBASICO         * UN_RSTARIFA.SUBBASICO +
                                                 UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO * UN_RSTARIFA.SUBCOMPLEMENTARIO +
                                                 UN_RSUSUARIO.CONSUMOSUNTUARIO      * UN_RSTARIFA.SUBSUNTUARIO + 0.5);

        MI_SOBREACU := TRUNC(UN_RSTARIFA.SOBREFIJO + UN_RSUSUARIO.CONSUMOBASICO         * UN_RSTARIFA.SOBREBASICO +
                                                     UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO * UN_RSTARIFA.SOBRECOMPLEMENTARIO +
                                                     UN_RSUSUARIO.CONSUMOSUNTUARIO      * UN_RSTARIFA.SOBRESUNTUARIO + 0.5);

        UN_RSUSUARIO.VALORBASICO         := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOBASICO * UN_RSTARIFA.TARIFABASICO,2);
        UN_RSUSUARIO.VALORCOMPLEMENTARIO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO * UN_RSTARIFA.TARIFACOMPLEMENTARIO,2);
        UN_RSUSUARIO.VALORSUNTUARIO      := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOSUNTUARIO * UN_RSTARIFA.TARIFASUNTUARIO,2);
        UN_RSUSUARIO.SUBFIJO             := TRUNC(UN_RSTARIFA.SUBFIJO + 0.5);

        IF MI_SUBACU = TRUNC(UN_RSTARIFA.SUBFIJO) OR UN_RSUSUARIO.CONSUMO =0 THEN
            UN_RSUSUARIO.SUBCONSUMOAC := 0;
        ELSE
            UN_RSUSUARIO.SUBCONSUMOAC := MI_SUBACU - UN_RSUSUARIO.SUBFIJO;
        END IF;

        UN_RSUSUARIO.SUBACUEDUCTO := MI_SUBACU + UN_RSUSUARIO.SUBSINMEDICION;
        UN_RSUSUARIO.SOBREFIJO    := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBREFIJO);

        IF MI_SOBREACU =0 OR UN_RSUSUARIO.CONSUMO= 0 THEN
            UN_RSUSUARIO.SOBRECONSUMOAC :=0;
        ELSE
            UN_RSUSUARIO.SOBRECONSUMOAC := MI_SOBREACU - UN_RSUSUARIO.SOBREFIJO;
        END IF;

        UN_RSUSUARIO.SOBREACUEDUCTO := MI_SOBREACU + UN_RSUSUARIO.SOBRESINMEDICION;

        IF UN_PARAMETROS('SUBSIDIO').VALOR ='SI' THEN   --'Cálculo especial subsidios especial
            UN_RSUSUARIO.SOBREFIJO      := TRUNC(UN_RSUSUARIO.CARGOFIJO - UN_RSTARIFA.COSTOFIJOAC + 0.5);
            UN_RSUSUARIO.SOBRECONSUMOAC := TRUNC(UN_RSUSUARIO.VALORBASICO + UN_RSUSUARIO.VALORCOMPLEMENTARIO + UN_RSUSUARIO.VALORSUNTUARIO -
                                                (UN_RSUSUARIO.CONSUMOBASICO + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO + UN_RSUSUARIO.CONSUMOSUNTUARIO) *
                                                UN_RSTARIFA.COSTOM3AC + 0.5);
            UN_RSUSUARIO.SOBREACUEDUCTO := UN_RSUSUARIO.SOBREFIJO + UN_RSUSUARIO.SOBRECONSUMOAC;
            IF UN_RSUSUARIO.SOBREACUEDUCTO <0 THEN
                UN_RSUSUARIO.SUBFIJO        := (UN_RSUSUARIO.SOBREFIJO * -1);
                UN_RSUSUARIO.SUBCONSUMOAC   := (UN_RSUSUARIO.SOBRECONSUMOAC * -1);
                UN_RSUSUARIO.SUBACUEDUCTO   := (UN_RSUSUARIO.SOBREACUEDUCTO * -1);
                UN_RSUSUARIO.SOBREFIJO      := 0;
                UN_RSUSUARIO.SOBRECONSUMOAC := 0;
                UN_RSUSUARIO.SOBREACUEDUCTO := 0;
            END IF;

        END IF;

    END IF; --Fin de validación Acu

    --Total de acueducto
    FOR i IN UN_FACTURADOACT.FIRST .. UN_FACTURADOACT.LAST
    LOOP
        IF UN_FACTURADOACT.EXISTS(i) AND i IN(1,2,5,19,43) THEN
          MI_TOTALACU := MI_TOTALACU + UN_FACTURADOACT(i).FACTURADO;
        END IF;
    END LOOP;

    UN_RSUSUARIO.TOTALACUEDUCTO := MI_TOTALACU;

    RETURN MI_TOTALACU;

  END FC_CALCULOACUEDUCTO;

--10
  FUNCTION FC_CALCULOALCANTARILLADO
    /*  
      @NAME:    obtenerCalculoAlcantarillado
      @METHOD:  GET
    */
  (
  UN_COMPANIA            IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO           IN OUT  TUSUARIO
  ,UN_RSTARIFA            IN      TTARIFAS
  ,UN_TIPOCALCULO         IN      SP_USUARIO.TIPOCALCULO%TYPE
  ,UN_PARAMETROS          IN      TI_PARAMETRO
  ,UN_FACTURADOACT        IN OUT  TI_FACTURADO
  ) RETURN NUMBER
  AS
  MI_CONDICIONALC    BOOLEAN;
  MI_TOTALALC        NUMBER(20,0)    DEFAULT 0;
  MI_SUBALCA         SP_USUARIO.SUBCONSUMOAL%TYPE;
  MI_SOBREALCA       SP_USUARIO.SOBRECONSUMOAL%TYPE;


  BEGIN
  MI_CONDICIONALC :=  UN_RSUSUARIO.ALCANTARILLADO <>0 AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <= 0 AND
                      (UN_RSUSUARIO.ESTADO = 'A' OR (UN_RSUSUARIO.ESTADO = 'C' AND UN_PARAMETROS('CALCORTADOASEO').VALOR ='NO' )
                                                 OR (UN_RSUSUARIO.ESTADO = 'S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' )
                      );
  PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ALCANTARILLADO');

  UN_FACTURADOACT(4).FACTURADO :=0;   --Cargo fijo
  UN_FACTURADOACT(24).FACTURADO :=0;  --Tarifa sin medición
  UN_FACTURADOACT(33).FACTURADO :=0;  --Consumo
  UN_FACTURADOACT(45).FACTURADO :=0;  --Tasa ambiental

  IF MI_CONDICIONALC THEN

      UN_RSUSUARIO.COSTOM3AL                  := UN_RSTARIFA.COSTOM3AL;
      UN_RSUSUARIO.COSTOFIJOAL                := UN_RSTARIFA.COSTOFIJOAL;
      UN_RSUSUARIO.TARIFAM3BASICOAL           := UN_RSTARIFA.ALCBASICO;
      UN_RSUSUARIO.TARIFAM3COMPLEMENTARIOAL   := UN_RSTARIFA.ALCCOMPLEMENTARIO;
      UN_RSUSUARIO.TARIFAM3SUNTUARIOAL        := UN_RSTARIFA.ALCSUNTUARIO;

      IF UN_TIPOCALCULO ='T' OR (UN_PARAMETROS('TSIMMALC').VALOR ='SI' AND UN_RSUSUARIO.CONSUMO =0) THEN  --Tarifa sin medición
          UN_FACTURADOACT(24).FACTURADO    := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SINMEDICIONALC);
          UN_RSUSUARIO.SUBALCSINMEDICION   := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SUBALCSINMEDICION);
          UN_RSUSUARIO.SOBREALCSINMEDICION := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBREALCSINMEDICION);

      ELSIF UN_PARAMETROS('ALCSM').VALOR ='SI' AND UN_RSUSUARIO.ALCANTARILLADO <>0 AND UN_RSUSUARIO.ACUEDUCTO=0 AND UN_RSUSUARIO.ASEO =0 THEN
          UN_FACTURADOACT(24).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SINMEDICIONALC);
      END IF;

      UN_FACTURADOACT(4).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.FIJOALCANTARILLADO);

      UN_FACTURADOACT(33).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOBASICOALC * UN_RSTARIFA.ALCBASICO ) +
                                       PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC * UN_RSTARIFA.ALCCOMPLEMENTARIO) +
                                       PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOSUNTUARIOALC * UN_RSTARIFA.ALCSUNTUARIO) ;

      IF UN_RSUSUARIO.ESTADO ='S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' THEN
          IF NOT (UN_RSUSUARIO.LECTURA <> UN_RSUSUARIO.LECTURA1 AND UN_PARAMETROS('CALCONSUS').VALOR = 'SI') THEN
              UN_FACTURADOACT(33).FACTURADO :=0;
          END IF;
      END IF;

      IF UN_PARAMETROS('TASAAMBIENTAL').VALOR ='SI' AND UN_FACTURADOACT(33).FACTURADO > 0 THEN
          UN_FACTURADOACT(45).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.VALORTASAAMBIENTALALC *
                                          (UN_RSUSUARIO.CONSUMOBASICOALC + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC + UN_RSUSUARIO.CONSUMOSUNTUARIOALC)
                                          );

          IF UN_PARAMETROS('CONSUBSOBRETASA').VALOR ='SI' THEN
              UN_RSUSUARIO.SUB_TASAAMBIENTALALC :=  PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUB_TASAAMBIENTALALC *
                                                    (UN_RSUSUARIO.CONSUMOBASICOALC + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC + UN_RSUSUARIO.CONSUMOSUNTUARIOALC)
                                                    );

              UN_RSUSUARIO.SOBRE_TASAAMBIENTALALC := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBRE_TASAAMBIENTALALC *
                                                     (UN_RSUSUARIO.CONSUMOBASICOALC + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC + UN_RSUSUARIO.CONSUMOSUNTUARIOALC)
                                                     );
          END IF;
      END IF;

      IF UN_RSUSUARIO.PORCENTAJEAPLICAR <> 100 AND UN_RSUSUARIO.INDDESHABITADO <>0 THEN
          UN_FACTURADOACT(33).FACTURADO := 0;
      END IF;

      UN_RSUSUARIO.COSTOCONSUMOAL := TRUNC(UN_RSUSUARIO.CONSUMO * UN_RSTARIFA.COSTOM3AL + 0.5 );

      IF UN_FACTURADOACT(4).FACTURADO =0 AND UN_FACTURADOACT(33).FACTURADO =0 AND UN_RSUSUARIO.PORCENTAJEAPLICAR <>0 THEN
          UN_FACTURADOACT(4).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND((UN_FACTURADOACT(1).FACTURADO + UN_FACTURADOACT(2).FACTURADO +
                                                                  UN_FACTURADOACT(5).FACTURADO) *
                                                                  UN_RSTARIFA.PORCALCANTARILLADO);
      END IF;

      UN_RSUSUARIO.CARGOFIJOAL := UN_FACTURADOACT(4).FACTURADO;
      UN_RSUSUARIO.ALSINMEDICION := UN_FACTURADOACT(24).FACTURADO;

      --Calculo subsidios y sobreprecios Alcantarillado
      MI_SUBALCA := TRUNC(UN_RSTARIFA.SUBALCFIJO + UN_RSUSUARIO.CONSUMOBASICOALC         * UN_RSTARIFA.SUBALCBASICO         +
                                                   UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC * UN_RSTARIFA.SUBALCCOMPLEMENTARIO +
                                                   UN_RSUSUARIO.CONSUMOSUNTUARIOALC      * UN_RSTARIFA.SUBALCSUNTUARIO + 0.5);

      MI_SOBREALCA := TRUNC(UN_RSTARIFA.SOBREALCFIJO + UN_RSUSUARIO.CONSUMOBASICOALC         * UN_RSTARIFA.SOBREALCBASICO         +
                                                       UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC * UN_RSTARIFA.SOBREALCCOMPLEMENTARIO +
                                                       UN_RSUSUARIO.CONSUMOSUNTUARIOALC      * UN_RSTARIFA.SOBREALCSUNTUARIO + 0.5);

      UN_RSUSUARIO.VALORALCBASICO         := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOBASICOALC * UN_RSTARIFA.ALCBASICO ,2);
      UN_RSUSUARIO.VALORALCCOMPLEMENTARIO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC *
                                                                     UN_RSTARIFA.ALCCOMPLEMENTARIO ,2);
      UN_RSUSUARIO.VALORALCSUNTUARIO      := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.CONSUMOSUNTUARIOALC * UN_RSTARIFA.ALCSUNTUARIO ,2);
      UN_RSUSUARIO.SUBFIJOALC             := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SUBALCFIJO);
      UN_RSUSUARIO.SUBCONSUMOAL           := MI_SUBALCA - UN_RSUSUARIO.SUBFIJOALC;

      IF UN_FACTURADOACT(24).FACTURADO <>0 THEN
          UN_RSUSUARIO.SUBALCANTARILLADO   := MI_SUBALCA   + PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUBALCSINMEDICION);
          UN_RSUSUARIO.SOBREALCANTARILLADO := MI_SOBREALCA + PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SOBREALCSINMEDICION);
      ELSE
          UN_RSUSUARIO.SUBALCANTARILLADO   := MI_SUBALCA;
          UN_RSUSUARIO.SOBREALCANTARILLADO := MI_SOBREALCA;
      END IF;
      UN_RSUSUARIO.SOBREFIJOALC   := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.SOBREALCFIJO);
      UN_RSUSUARIO.SOBRECONSUMOAL := MI_SOBREALCA - UN_RSUSUARIO.SOBREFIJOALC;

      IF UN_PARAMETROS('SUBSIDIO').VALOR ='SI' THEN   --'Cálculo especial subsidios especial
          UN_RSUSUARIO.SOBREFIJOALC   := TRUNC(UN_RSUSUARIO.CARGOFIJOAL - UN_RSTARIFA.COSTOFIJOAL + 0.5);
          UN_RSUSUARIO.SOBRECONSUMOAL := TRUNC(UN_RSUSUARIO.VALORALCBASICO + UN_RSUSUARIO.VALORALCCOMPLEMENTARIO + UN_RSUSUARIO.VALORALCSUNTUARIO -
                            (UN_RSUSUARIO.CONSUMOBASICOALC + UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC + UN_RSUSUARIO.CONSUMOSUNTUARIOALC) *
                                               UN_RSTARIFA.COSTOM3AL + 0.5);
          UN_RSUSUARIO.SOBREALCANTARILLADO :=  UN_RSUSUARIO.SOBREFIJO + UN_RSUSUARIO.SOBRECONSUMOAL;

          IF UN_RSUSUARIO.SOBREALCANTARILLADO <0 THEN
              UN_RSUSUARIO.SUBFIJOALC          := (UN_RSUSUARIO.SOBREFIJOALC * -1);
              UN_RSUSUARIO.SUBCONSUMOAL        := (UN_RSUSUARIO.SOBRECONSUMOAL * -1);
              UN_RSUSUARIO.SUBALCANTARILLADO   := (UN_RSUSUARIO.SOBREALCANTARILLADO * -1);
              UN_RSUSUARIO.SOBREFIJOALC        := 0;
              UN_RSUSUARIO.SOBRECONSUMOAL      := 0;
              UN_RSUSUARIO.SOBREALCANTARILLADO := 0;
          END IF;
      END IF;

  END IF; --Fin Validación Alc



  FOR i IN UN_FACTURADOACT.FIRST .. UN_FACTURADOACT.LAST
  LOOP
      IF UN_FACTURADOACT.EXISTS(i) AND i IN(4,24,33,45) THEN
        MI_TOTALALC := MI_TOTALALC + UN_FACTURADOACT(i).FACTURADO;
      END IF;
  END LOOP;

  UN_RSUSUARIO.TOTALALCANTARILLADO := MI_TOTALALC;

  RETURN MI_TOTALALC;

  END FC_CALCULOALCANTARILLADO;

--11
  PROCEDURE PR_ARMAHISTORIADESVIACION
  (
     /*
       NAME              : PR_ARMAHISTORIADESVIACION --> En Access ArmaHisDesviacion
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 23/02/2017
       TIME              : 04:40 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Guarda la historia de desviación en 2 momentos, Cuando se guarda el facturado real
                           , Cuando se guarda el facturado y consumos cobrados

       PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones y actualizaciones
                           UN_FACTURADOACT   => Type de conceptos facturados por usuario.
                           UN_FACTURADO      => Establece el momento, Cuando es Real o cuando es lo cobrado
                           UN_BOLPORMICRO    => Establece si el usuario tiene consumo por micromedición
                           UN_BOLCONMANUAL   => Establece si el usuario tiene consumos manuales.
       MODIFICATIONS     :

       @NAME:    armarHistoriaDesviacion
       @METHOD:  POST
     */
   UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO          IN TUSUARIO
  ,UN_FACTURADOACT       IN TI_FACTURADO
  ,UN_FACTURADO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_BOLPORMICRO        IN PCK_SUBTIPOS.TI_LOGICO
  ,UN_BOLCONMANUAL       IN PCK_SUBTIPOS.TI_LOGICO
  ,UN_PARAMETROS         IN      TI_PARAMETRO
  ,UN_USUARIO            IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS

  MI_RSDES            SYS_REFCURSOR;
  MI_FILAS            PCK_SUBTIPOS.TI_ENTERO;
  MI_TABLA            PCK_SUBTIPOS.TI_STRSQL;
  MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS      PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_DBLCONSUMOACU    SP_USUARIO.CONSUMO%TYPE;
  MI_DBLCONSUMOALC    SP_USUARIO.CONSUMO%TYPE;
  MI_DBLVALORACU      SP_FACTURADO.VALOR_FACTURADO%TYPE;
  MI_DBLVALORALC      SP_FACTURADO.VALOR_FACTURADO%TYPE;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    IF UN_PARAMETROS('DESVIAENSITIO').VALOR ='NO' THEN
        <<DESVIACION>>
        FOR MI_RSDES IN
         (
            SELECT CONSECUTIVO
            FROM   SP_DESVIACIONES
            WHERE  COMPANIA     = UN_COMPANIA
              AND  CICLO        = UN_RSUSUARIO.CICLO
              AND  CODIGORUTA   = UN_RSUSUARIO.CODIGORUTA
              AND  (ESTADO ='A' OR (ANOCIERRE = UN_RSUSUARIO.ANO AND PERIODOCIERRE = UN_RSUSUARIO.PERIODO ))
              AND  ANO || PERIODO <= UN_RSUSUARIO.ANO || UN_RSUSUARIO.PERIODO
        )
        LOOP
            BEGIN
                MI_DBLCONSUMOACU := UN_RSUSUARIO.CONSUMO;
                MI_DBLCONSUMOALC := UN_RSUSUARIO.CONSUMO;
                MI_DBLVALORACU   := UN_FACTURADOACT(2).FACTURADO + UN_FACTURADOACT(5).FACTURADO;
                MI_DBLVALORALC   := UN_FACTURADOACT(33).FACTURADO;

                MI_TABLA       := 'SP_DESVIACIONES_HISTORIA';
                MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                MI_MERGEENLACE := '    COMPANIA     = ''' || UN_COMPANIA || '''
                                   AND DESVIACION   =   ' || MI_RSDES.CONSECUTIVO || '
                                   AND CICLO        =   ' || UN_RSUSUARIO.CICLO || '
                                   AND CODIGORUTA   = ''' || UN_RSUSUARIO.CODIGORUTA || '''
                                   AND ANO          =   ' || UN_RSUSUARIO.ANO || '
                                   AND PERIODO      = ''' || UN_RSUSUARIO.PERIODO || '''  ';

                IF UN_FACTURADO <>0 THEN
                    MI_MERGEEXISTE := ' UPDATE SET LECTURAINICIAL       = ' || UN_RSUSUARIO.LECTURA1 || ',
                                                   LECTURAFINAL         = ' || UN_RSUSUARIO.LECTURA || ',
                                                   CONSUMOACUFACTURADO  = ' || MI_DBLCONSUMOACU || ',
                                                   CONSUMOALCFACTURADO  = ' || MI_DBLCONSUMOALC || ',
                                                   VALORACUFACTURADO    = ' || MI_DBLVALORACU || ',
                                                   VALORALCFACTURADO    = ' || MI_DBLVALORALC || ',
                                                   PORMICRO             = ' || UN_BOLPORMICRO * 1 || ',
                                                   CONSUMOMANUAL        = ' || CASE WHEN UN_BOLPORMICRO <>0 THEN 0 ELSE UN_BOLCONMANUAL *1 END || ' ';

                ELSE
                    MI_MERGEEXISTE := ' UPDATE SET CONSUMOACUREAL  = ' || MI_DBLCONSUMOACU || ',
                                                   CONSUMOALCREAL  = ' || MI_DBLCONSUMOALC || ',
                                                   VALORACUREAL    = ' || MI_DBLVALORACU || ',
                                                   VALORALCREAL    = ' || MI_DBLVALORALC || '  ';

                END IF;

                MI_MERGEEXISTE := MI_MERGEEXISTE || ',MODIFIED_BY    = '''|| UN_USUARIO ||'''
                                                     ,DATE_MODIFIED  = SYSDATE  ';

                MI_MERGENOEXIS := 'INSERT (COMPANIA, DESVIACION, CICLO, CODIGORUTA, ANO, PERIODO, CONSUMOACUREAL, CONSUMOALCREAL, VALORACUREAL, VALORALCREAL,CREATED_BY,DATE_CREATED)
                                   VALUES (''' || UN_COMPANIA || ''',' || MI_RSDES.CONSECUTIVO || ',' || UN_RSUSUARIO.CICLO || ',''' || UN_RSUSUARIO.CODIGORUTA || ''',
                                           ' || UN_RSUSUARIO.ANO || ', ''' || UN_RSUSUARIO.PERIODO || ''',' || MI_DBLCONSUMOACU || ',' || MI_DBLCONSUMOALC || ',
                                           ' || MI_DBLVALORACU || ',' || MI_DBLVALORALC || ','''|| UN_USUARIO ||''',SYSDATE)  ';
                BEGIN
                    MI_FILAS     := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                      UN_ACCION      => 'IM',
                                                      UN_MERGEUSING  => MI_MERGEUSING,
                                                      UN_MERGEENLACE => MI_MERGEENLACE,
                                                      UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                      UN_MERGENOEXIS => MI_MERGENOEXIS);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR(1).CLAVE := 'USUARIO';
                MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_HISTDESVIACION,
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END LOOP DESVIACION;
    END IF;

  END PR_ARMAHISTORIADESVIACION;

--12
  FUNCTION FC_CALCULOASEO
  (
  /*
   NAME              : FC_CALCULOASEO --> En Access calculoDeAseoNoTercerizado
   AUTHORS           : SYSMAN  SAS
   AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
   DATE MIGRADOR     : 24/02/2017
   TIME              : 04:17 PM
   SOURCE MODULE     : SERVICIOS PUBLICOS
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Cálculo de facturados y subsidios de aseo.

   PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                       UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones
                       UN_RSTARIFA       => Type de tarifa con el cual se va a calcular.
                       UN_PARAMETROS     => Type de parámetros los cuales influyen en el calculo.
                       UN_FACTACT        => Type de conceptos facturados, los cuales la función devuelve con su respectivo valor

   MODIFICATIONS     :

   @NAME:    CalcularAseo
   @METHOD:  POST
  */
  UN_COMPANIA               IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO             IN OUT  TUSUARIO
  ,UN_RSTARIFA              IN      TTARIFAS
  ,UN_PARAMETROS            IN      TI_PARAMETRO
  ,UN_FACTACT               IN OUT  TI_FACTURADO
  ,UN_USUARIO               IN SP_USUARIO.CREATED_BY%TYPE :=''
  ) RETURN NUMBER
  AS

  MI_RSUNIDEDES              SYS_REFCURSOR;
  MI_TOTALASEO               NUMBER(20,0)                            DEFAULT 0;
  MI_CALCULONORMAL           BOOLEAN                                 DEFAULT TRUE;
  MI_TARIFASEO               TTARIFAS;
  MI_VALIDATARIFA            VARCHAR2(300 CHAR);
  MI_SUMTRBL_TRRA            NUMBER(20,6)                            DEFAULT 0;
  MI_CONDICION               BOOLEAN                                 DEFAULT FALSE;
  MI_MANEJA720               BOOLEAN                                 DEFAULT FALSE;
  MI_INDDESH                 PCK_SUBTIPOS.TI_LOGICO                  DEFAULT 0;
  MI_SUM_TRBL_VBA_720        NUMBER(20,6)                            DEFAULT 0;
  MI_OPERACION720            NUMBER(20,6)                            DEFAULT 0;
  MI_SUBSIDIO720             BOOLEAN                                 DEFAULT FALSE;
  MI_HAYUNIDADES             BOOLEAN                                 DEFAULT FALSE;
  MI_TERCERIZA               BOOLEAN                                 DEFAULT FALSE;

  MI_NOMUSO                  SP_USOS.NOMBRE%TYPE;
  MI_PESOASEO                SP_USUARIO.PESOASEO%TYPE;
  MI_RSESTRATO               SP_USUARIO.ESTRATO%TYPE;
  MI_FRECUENCIAASEO          SP_TARIFAS.VALORFREC1%TYPE              DEFAULT 0;
  MI_RSFRECUENCIAASEO        SP_USUARIO.FRECUENCIAASEOSEMANA%TYPE    DEFAULT 0;
  MI_TASEODOMICILIARIO       SP_TARIFAS.ASEODOMICILIARIO%TYPE        DEFAULT 0;
  MI_ASEO                    NUMBER(20,2)                            DEFAULT 0;
  MI_SOBREASEOTARIFA         SP_TARIFAS.SOBREASEOTARIFA1%TYPE        DEFAULT 0;
  MI_SUBASEOTARIFA           SP_TARIFAS.SUBASEOTARIFA1%TYPE          DEFAULT 0;
  MI_SOBREASEOUNICO          SP_TARIFAS.SOBREASEOUNICO%TYPE          DEFAULT 0;
  MI_SUBASEOUNICO            SP_TARIFAS.SUBASEOUNICO%TYPE            DEFAULT 0;
  MI_SUBPESOASEO             SP_USUARIO.PESOASEO%TYPE                DEFAULT 0;


  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ETAPA                   VARCHAR2(100 CHAR);
  BEGIN

    --Aseo normal
    MI_ETAPA := 'Inicializando facturados';
    UN_FACTACT(3).FACTURADO := 0;   --Barrido limpieza
    UN_FACTACT(20).FACTURADO := 0;  --Tratamiento y disposición final
    UN_FACTACT(21).FACTURADO := 0;  --Comercialización y manejo
    UN_FACTACT(22).FACTURADO := 0;  --Recolección y transporte
    UN_FACTACT(46).FACTURADO := 0;  --Tramo excedente
    UN_FACTACT(48).FACTURADO := 0;  --Aseo deshabitado
    --Aseo Resolución 720
    UN_FACTACT(201).FACTURADO := 0;
    UN_FACTACT(202).FACTURADO := 0;
    UN_FACTACT(203).FACTURADO := 0;
    UN_FACTACT(204).FACTURADO := 0;
    UN_FACTACT(205).FACTURADO := 0;
    UN_FACTACT(206).FACTURADO := 0;
    UN_FACTACT(207).FACTURADO := 0;

    PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ASEO');

    IF UN_PARAMETROS('PROCTERCERIZADO').VALOR ='SI' AND NVL(UN_RSUSUARIO.EMPRESAASEOEXT, '') <> '' AND UN_RSUSUARIO.PINTADEUDATERCE =0 THEN
        MI_TERCERIZA := TRUE;
    END IF;

    IF MI_TERCERIZA THEN  --Maneja aseo independiente no va a los facturados, no se calcula por sysman
        BEGIN
            BEGIN
                MI_TABLA         := ' SP_HISTORIA_EXTERNA ' ;
                MI_CAMPOS        := '  FACTURA       = NULL
                                      ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                      ,DATE_MODIFIED = SYSDATE ';

                MI_CONDICIONACME := '     COMPANIA    = '''|| UN_COMPANIA ||'''
                                      AND CICLO       =   '|| UN_RSUSUARIO.CICLO ||'
                                      AND CODIGORUTA  = '''|| UN_RSUSUARIO.CODIGORUTA ||'''
                                      AND ANO         =   '|| UN_RSUSUARIO.ANO ||'
                                      AND PERIODO     = '''|| UN_RSUSUARIO.PERIODO ||'''
                                      AND ID_EMPRESA  = '''|| UN_RSUSUARIO.EMPRESAASEOEXT ||'''';

                MI_FILAS         := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICIONACME);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'USUARIO';
            MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
            MI_MSGERROR(2).CLAVE := 'CICLO';
            MI_MSGERROR(2).VALOR := UN_RSUSUARIO.CICLO;
            MI_MSGERROR(3).CLAVE := 'EMPRESA';
            MI_MSGERROR(3).VALOR := UN_RSUSUARIO.EMPRESAASEOEXT;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ACTASEOTERCERIZA,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_REEMPLAZOS => MI_MSGERROR);
        END;

    ELSE
        MI_ETAPA := 'Validación resolución 720';
        MI_MANEJA720 := CASE WHEN UN_PARAMETROS('MANEJA720').VALOR ='SI'
                             THEN TRUE
                             ELSE FALSE
                        END;
        MI_CONDICION := (UN_RSUSUARIO.ANO || ',' || UN_RSUSUARIO.PERIODO) < UN_PARAMETROS('INICIO720').VALOR
                        OR UN_RSUSUARIO.CICLO = TO_NUMBER(UN_PARAMETROS('CICLOEXCLUIDO720').VALOR);
        IF MI_CONDICION THEN
            MI_MANEJA720 := FALSE;
        END IF;

        MI_RSFRECUENCIAASEO := UN_RSUSUARIO.FRECUENCIAASEOSEMANA;

        MI_NOMUSO := UPPER(PCK_SERVICIOS_PUBLICOS.FC_NOMBREUSO(UN_COMPANIA  => UN_COMPANIA,
                                                               UN_CODIGO    => UN_RSUSUARIO.USO));

        MI_PESOASEO := UN_RSUSUARIO.PESOASEO;
        MI_RSESTRATO := UN_RSUSUARIO.ESTRATOASEO;
        MI_INDDESH := UN_RSUSUARIO.INDDESHABITADO;
        IF UN_PARAMETROS('CHAPETASDESH').VALOR ='SI' AND UN_RSUSUARIO.CHAPETAS <>0 THEN
            MI_INDDESH:= -1;
        END IF;

        MI_TARIFASEO := UN_RSTARIFA;    --Toma las tarifas pertenecientes al usuario
        FOR MI_RSUNIDEDES IN (
            SELECT * FROM (
                SELECT 0 TIPO, USO, ESTRATOASEO, PESOASEO, DESHABITADO, CONSECUTIVO
                FROM   SP_UNIDADESRESIDENCIALES --Aseo con unidades residenciales
                WHERE  COMPANIA   = UN_COMPANIA
                  AND  CODIGORUTA = CASE WHEN UN_PARAMETROS('UNIDADESIND').VALOR ='SI'
                                         THEN UN_RSUSUARIO.CODIGORUTA
                                         ELSE 'XYZ'
                                    END
                UNION
                SELECT 1 TIPO,'' USO,'' ESTRATOASEO,0 PESOASEO,0 DESHABITADO,0 CONSECUTIVO
                FROM   DUAL   --ASEO NORMAL
            ) ORDER BY TIPO
        )
        LOOP
            MI_VALIDATARIFA :='C';
            UN_FACTACT(3).FACTURADO := 0;
            UN_FACTACT(20).FACTURADO := 0;
            UN_FACTACT(21).FACTURADO := 0;
            UN_FACTACT(22).FACTURADO := 0;
            UN_FACTACT(46).FACTURADO := 0;
            UN_FACTACT(48).FACTURADO := 0;
            UN_FACTACT(201).FACTURADO := 0;
            UN_FACTACT(202).FACTURADO := 0;
            UN_FACTACT(203).FACTURADO := 0;
            UN_FACTACT(204).FACTURADO := 0;
            UN_FACTACT(205).FACTURADO := 0;
            UN_FACTACT(206).FACTURADO := 0;
            UN_FACTACT(207).FACTURADO := 0;

            IF MI_RSUNIDEDES.TIPO = 0 OR (MI_RSUNIDEDES.TIPO = 1 AND MI_CALCULONORMAL) THEN
                IF MI_RSUNIDEDES.TIPO <> 1 THEN
                    --LLena el type de tarifas con las que estan configuradas en las unidades
                    /*
                    rsUso = rsUnidades!USO
                     */
                    MI_ETAPA := 'Tarifas unidades residenciales';
                    MI_RSESTRATO := MI_RSUNIDEDES.ESTRATOASEO;
                    MI_PESOASEO := MI_RSUNIDEDES.PESOASEO;
                    MI_NOMUSO := UPPER(PCK_SERVICIOS_PUBLICOS.FC_NOMBREUSO(UN_COMPANIA  => UN_COMPANIA,
                                                                            UN_CODIGO    => MI_RSUNIDEDES.USO));
                    MI_HAYUNIDADES := TRUE;
                    MI_INDDESH := MI_RSUNIDEDES.DESHABITADO;
                    MI_TARIFASEO := FC_CARGATARIFAS(UN_COMPANIA             => UN_COMPANIA
                                                   ,UN_ANO                  => UN_RSUSUARIO.ANO
                                                   ,UN_PERIODO              => UN_RSUSUARIO.PERIODO
                                                   ,UN_USO                  => MI_RSUNIDEDES.USO
                                                   ,UN_ESTRATO              => MI_RSUNIDEDES.ESTRATOASEO
                                                   ,UN_VALIDATARIFA         => MI_VALIDATARIFA );
                END IF;
                MI_CALCULONORMAL := FALSE;

                PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ASEO');
                IF MI_VALIDATARIFA = 'C' THEN  --Valida si existe la tarifa
                    IF UN_RSUSUARIO.ASEO <> 0 AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <= 0 AND MI_MANEJA720 THEN --Aseo 720
                        MI_SUMTRBL_TRRA := MI_TARIFASEO.TRBL + MI_TARIFASEO.TRLU + MI_TARIFASEO.TRRA;

                        IF MI_HAYUNIDADES = FALSE THEN
                            MI_CONDICION := UN_RSUSUARIO.CONSUMO <> 0 AND UN_RSUSUARIO.CONSUMO >= TO_NUMBER(UN_PARAMETROS('INTMAXDESH').VALOR)
                                            AND MI_INDDESH <>0;
                            IF MI_CONDICION THEN
                                MI_INDDESH :=0;
                            ELSIF UN_PARAMETROS('TARHABITADOSINDDESH').VALOR ='SI' AND UN_RSUSUARIO.CONSUMO <>0 AND MI_INDDESH <>0 THEN
                                MI_INDDESH :=0;
                            END IF;
                        END IF;

                        UN_FACTACT(201).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                  THEN MI_TARIFASEO.DES_CCS_720
                                                                                  ELSE MI_TARIFASEO.CCS_720
                                                                             END);
                        UN_RSUSUARIO.SUBCCS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                  THEN MI_TARIFASEO.DES_SUBCCS_720
                                                                                  ELSE MI_TARIFASEO.SUBCCS_720
                                                                             END);
                        UN_RSUSUARIO.SOBCCS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                   THEN MI_TARIFASEO.DES_SOBRECCS_720
                                                                                   ELSE MI_TARIFASEO.SOBRECCS_720
                                                                           END);

                        UN_FACTACT(202).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                  THEN MI_TARIFASEO.DES_CBLS_720
                                                                                   ELSE MI_TARIFASEO.CBLS_720
                                                                             END);
                        UN_RSUSUARIO.SUBCBLS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                               THEN MI_TARIFASEO.DES_SUBCBLS_720
                                                                               ELSE MI_TARIFASEO.SUBCBLS_720
                                                                            END);
                        UN_RSUSUARIO.SOBCBLS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                THEN MI_TARIFASEO.DES_SOBRECBLS_720
                                                                                ELSE MI_TARIFASEO.SOBRECBLS_720
                                                                            END);

                        UN_FACTACT(203).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                  THEN MI_TARIFASEO.DES_CLUS_720
                                                                                   ELSE MI_TARIFASEO.CLUS_720
                                                                             END);
                        UN_RSUSUARIO.SUBCLUS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                 THEN MI_TARIFASEO.DES_SUBCLUS_720
                                                                                 ELSE MI_TARIFASEO.SUBCLUS_720
                                                                            END);
                        UN_RSUSUARIO.SOBCLUS_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                 THEN MI_TARIFASEO.DES_SOBRECLUS_720
                                                                                 ELSE MI_TARIFASEO.SOBRECLUS_720
                                                                            END);


                        IF ( UN_RSUSUARIO.TAFNA_720 <> 0 Or UN_RSUSUARIO.TAFA_720 <> 0) AND MI_INDDESH =0  THEN --Aseo Aforado
                            MI_ETAPA := 'Cálculo aseo aforado resolución 720';
                            MI_SUM_TRBL_VBA_720 := MI_SUMTRBL_TRRA + UN_RSUSUARIO.TAFNA_720;

                            IF MI_TARIFASEO.FCS1 = 0 AND MI_TARIFASEO.FCS > 0 Then --Subsidio
                                MI_SUBSIDIO720 := TRUE;
                            ELSE
                                MI_SUBSIDIO720 := FALSE;
                            END IF;
                            MI_ETAPA := 'Cálculo Subsidios aseo aforado 720';
                            IF UN_PARAMETROS('TAR720MANUALESAFORO').VALOR ='SI' THEN    --Tarifas manuales tomar lo que esta en tarifas sin hacer ninguna operación
                                UN_FACTACT(204).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.CRT_720);
                                UN_FACTACT(205).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.CDF_720);
                                UN_FACTACT(206).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.CTL_720);
                                UN_FACTACT(207).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.VBA_720);
                                MI_ETAPA := 'Cálculo Subsidios aseo manual 720';
                                IF MI_SUBSIDIO720 THEN
                                    UN_RSUSUARIO.SUBCRT_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SUBCRT_720);
                                    UN_RSUSUARIO.SUBCDF_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SUBCDF_720);
                                    UN_RSUSUARIO.SUBCTL_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SUBCTL_720);
                                    UN_RSUSUARIO.SUBVBA_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SUBVBA_720);

                                    UN_RSUSUARIO.SOBCRT_720 := 0;
                                    UN_RSUSUARIO.SOBCDF_720 := 0;
                                    UN_RSUSUARIO.SOBCTL_720 := 0;
                                    UN_RSUSUARIO.SOBVBA_720 := 0;
                                ELSE
                                    UN_RSUSUARIO.SOBCRT_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SOBRECRT_720);
                                    UN_RSUSUARIO.SOBCDF_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SOBRECDF_720);
                                    UN_RSUSUARIO.SOBCTL_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SOBRECTL_720);
                                    UN_RSUSUARIO.SOBVBA_720 := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SOBREVBA_720);

                                    UN_RSUSUARIO.SUBCRT_720 := 0;
                                    UN_RSUSUARIO.SUBCDF_720 := 0;
                                    UN_RSUSUARIO.SUBCTL_720 := 0;
                                    UN_RSUSUARIO.SUBVBA_720 := 0;
                                END IF;

                            ELSE --Cálculo tarifa aforada
                                --Cálculo Recolección y transporte (CRT)
                                MI_OPERACION720           := (MI_TARIFASEO.CRT * MI_SUM_TRBL_VBA_720);
                                UN_FACTACT(204).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(
                                                                CASE WHEN MI_SUBSIDIO720
                                                                     THEN (MI_OPERACION720) - (MI_OPERACION720 * MI_TARIFASEO.FCS)
                                                                     ELSE (MI_OPERACION720) + (MI_OPERACION720 * MI_TARIFASEO.FCS1)
                                                                END
                                                             );
                                UN_RSUSUARIO.SUBCRT_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                              CASE WHEN MI_SUBSIDIO720
                                                                   THEN MI_OPERACION720 * MI_TARIFASEO.FCS
                                                                   ELSE 0
                                                              END
                                                             );

                                UN_RSUSUARIO.SOBCRT_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                              CASE WHEN MI_SUBSIDIO720 = FALSE
                                                                   THEN MI_OPERACION720 * MI_TARIFASEO.FCS1
                                                                   ELSE 0
                                                              END
                                                             );

                                --Cálculo Disposición final (CDF)
                                MI_OPERACION720           := MI_TARIFASEO.CDF * MI_SUM_TRBL_VBA_720;
                                UN_FACTACT(205).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(
                                                                CASE WHEN MI_SUBSIDIO720
                                                                     THEN (MI_OPERACION720) - (MI_OPERACION720  * MI_TARIFASEO.FCS)
                                                                     ELSE (MI_OPERACION720) + (MI_OPERACION720  * MI_TARIFASEO.FCS1)
                                                                END
                                                             );

                                UN_RSUSUARIO.SUBCDF_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                                 CASE WHEN MI_SUBSIDIO720
                                                                      THEN MI_OPERACION720 * MI_TARIFASEO.FCS
                                                                      ELSE 0
                                                                 END
                                                             );

                                UN_RSUSUARIO.SOBCDF_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                                 CASE WHEN MI_SUBSIDIO720 = FALSE
                                                                      THEN MI_OPERACION720 * MI_TARIFASEO.FCS1
                                                                      ELSE 0
                                                                 END
                                                             );

                                --Cálculo Tratamiento de lixiviados (CTL)
                                MI_OPERACION720           :=  MI_TARIFASEO.CTL * MI_SUM_TRBL_VBA_720;
                                UN_FACTACT(206).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(
                                                                CASE WHEN MI_SUBSIDIO720
                                                                     THEN (MI_OPERACION720) - (MI_OPERACION720  * MI_TARIFASEO.FCS)
                                                                     ELSE (MI_OPERACION720) + (MI_OPERACION720  * MI_TARIFASEO.FCS1)
                                                                END
                                                             );

                                UN_RSUSUARIO.SUBCTL_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                               CASE WHEN MI_SUBSIDIO720
                                                                    THEN MI_OPERACION720 * MI_TARIFASEO.FCS
                                                                    ELSE 0
                                                               END
                                                             );

                                UN_RSUSUARIO.SOBCTL_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                                 CASE WHEN MI_SUBSIDIO720 = FALSE
                                                                      THEN MI_OPERACION720 * MI_TARIFASEO.FCS1
                                                                      ELSE 0
                                                                 END
                                                             );
                                -- Cálculo Valor base de aprovechamiento por tonelada (VBA)
                                MI_OPERACION720           := MI_TARIFASEO.VBA * UN_RSUSUARIO.TAFA_720;
                                UN_FACTACT(207).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(
                                                                CASE WHEN MI_SUBSIDIO720
                                                                     THEN (MI_OPERACION720) - (MI_OPERACION720  * MI_TARIFASEO.FCS)
                                                                     ELSE (MI_OPERACION720) + (MI_OPERACION720  * MI_TARIFASEO.FCS1)
                                                                END
                                                             );

                                UN_RSUSUARIO.SUBVBA_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                               CASE WHEN MI_SUBSIDIO720
                                                                    THEN MI_OPERACION720 * MI_TARIFASEO.FCS
                                                                    ELSE 0
                                                               END
                                                             );

                                UN_RSUSUARIO.SOBVBA_720   := PCK_SYSMAN_UTL.FC_ROUND(
                                                                 CASE WHEN MI_SUBSIDIO720 = FALSE
                                                                      THEN MI_OPERACION720 * MI_TARIFASEO.FCS1
                                                                      ELSE 0
                                                                 END
                                                             );

                            END IF;

                            IF UN_PARAMETROS('TONELADASAFOTARVAR').VALOR ='SI' THEN
                                MI_ETAPA := 'Cálculo aseo tarifas variables 720';
                                UN_FACTACT(204).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_FACTACT(204).FACTURADO *
                                                                                     UN_RSUSUARIO.TAFNA_720);

                                UN_RSUSUARIO.SUBCRT_720 := CASE WHEN MI_SUBSIDIO720
                                                                THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUBCRT_720 * UN_RSUSUARIO.TAFNA_720)
                                                                ELSE 0
                                                           END;

                                UN_RSUSUARIO.SOBCRT_720:= CASE WHEN MI_SUBSIDIO720 = FALSE
                                                                THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SOBCRT_720 * UN_RSUSUARIO.TAFNA_720)
                                                                ELSE 0
                                                           END;

                                UN_FACTACT(205).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_FACTACT(205).FACTURADO *
                                                                                     UN_RSUSUARIO.TAFNA_720);

                                UN_RSUSUARIO.SUBCDF_720 := CASE WHEN MI_SUBSIDIO720
                                                               THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUBCDF_720 * UN_RSUSUARIO.TAFNA_720)
                                                               ELSE 0
                                                          END;

                                UN_RSUSUARIO.SOBCDF_720:= CASE WHEN MI_SUBSIDIO720 = FALSE
                                                               THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SOBCDF_720 * UN_RSUSUARIO.TAFNA_720)
                                                               ELSE 0
                                                          END;

                                UN_FACTACT(206).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_FACTACT(206).FACTURADO *
                                                                                     UN_RSUSUARIO.TAFNA_720);

                                UN_RSUSUARIO.SUBCTL_720 := CASE WHEN MI_SUBSIDIO720
                                                             THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUBCTL_720 * UN_RSUSUARIO.TAFNA_720)
                                                             ELSE 0
                                                        END;

                                UN_RSUSUARIO.SOBCTL_720:= CASE WHEN MI_SUBSIDIO720 = FALSE
                                                             THEN PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SOBCTL_720 * UN_RSUSUARIO.TAFNA_720)
                                                             ELSE 0
                                                        END;
                            END IF;

                        ELSE    --Resolución 720 Normal
                            MI_ETAPA := 'Cálculo aseo normal 720';

                            UN_FACTACT(204).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_CRT_720
                                                                                      ELSE MI_TARIFASEO.CRT_720
                                                                                 END);

                            UN_RSUSUARIO.SUBCRT_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_SUBCRT_720
                                                                                      ELSE MI_TARIFASEO.SUBCRT_720
                                                                                 END);
                            UN_RSUSUARIO.SOBCRT_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_SOBRECRT_720
                                                                                      ELSE MI_TARIFASEO.SOBRECRT_720
                                                                                 END);

                            UN_FACTACT(205).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_CDF_720
                                                                                      ELSE MI_TARIFASEO.CDF_720
                                                                                 END);
                            UN_RSUSUARIO.SUBCDF_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                    THEN MI_TARIFASEO.DES_SUBCDF_720
                                                                                    ELSE MI_TARIFASEO.SUBCDF_720
                                                                               END);
                            UN_RSUSUARIO.SOBCDF_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                    THEN MI_TARIFASEO.DES_SOBRECDF_720
                                                                                    ELSE MI_TARIFASEO.SOBRECDF_720
                                                                               END);

                            UN_FACTACT(206).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_CTL_720
                                                                                      ELSE MI_TARIFASEO.CTL_720
                                                                                 END);
                            UN_RSUSUARIO.SUBCTL_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                    THEN MI_TARIFASEO.DES_SUBCTL_720
                                                                                    ELSE MI_TARIFASEO.SUBCTL_720
                                                                               END);
                            UN_RSUSUARIO.SOBCTL_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                     THEN MI_TARIFASEO.DES_SOBRECTL_720
                                                                                     ELSE MI_TARIFASEO.SOBRECTL_720
                                                                                END);

                            UN_FACTACT(207).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                      THEN MI_TARIFASEO.DES_VBA_720
                                                                                      ELSE MI_TARIFASEO.VBA_720
                                                                                 END);
                            UN_RSUSUARIO.SUBVBA_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                    THEN MI_TARIFASEO.DES_SUBVBA_720
                                                                                    ELSE MI_TARIFASEO.SUBVBA_720
                                                                                END);
                            UN_RSUSUARIO.SOBVBA_720 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_INDDESH <>0
                                                                                    THEN MI_TARIFASEO.DES_SOBREVBA_720
                                                                                    ELSE MI_TARIFASEO.SOBREVBA_720
                                                                                 END);

                        END IF; --Fin 720
                        MI_ETAPA := 'Total Subsidios Sobreprecios aseo 720';
                        UN_RSUSUARIO.VALASEOCCS_720   := UN_FACTACT(201).FACTURADO;
                        UN_RSUSUARIO.VALASEOCBLS_720  := UN_FACTACT(202).FACTURADO;
                        UN_RSUSUARIO.VALASEOCLUS_720  := UN_FACTACT(203).FACTURADO;
                        UN_RSUSUARIO.VALASEOCRT_720   := UN_FACTACT(204).FACTURADO;
                        UN_RSUSUARIO.VALASEOCDF_720   := UN_FACTACT(205).FACTURADO;
                        UN_RSUSUARIO.VALASEOCTL_720   := UN_FACTACT(206).FACTURADO;
                        UN_RSUSUARIO.VALASEOVBA_720   := UN_FACTACT(207).FACTURADO;

                        UN_RSUSUARIO.SUBASEO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SUBCCS_720  + UN_RSUSUARIO.SUBCBLS_720 +
                                                                        UN_RSUSUARIO.SUBCLUS_720 + UN_RSUSUARIO.SUBCRT_720 +
                                                                        UN_RSUSUARIO.SUBCDF_720  + UN_RSUSUARIO.SUBCTL_720 +
                                                                        UN_RSUSUARIO.SUBVBA_720);

                        UN_RSUSUARIO.SOBREASEO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSUSUARIO.SOBCCS_720 + UN_RSUSUARIO.SOBCBLS_720 +
                                                                          UN_RSUSUARIO.SOBCLUS_720 + UN_RSUSUARIO.SOBCRT_720 +
                                                                          UN_RSUSUARIO.SOBCDF_720 + UN_RSUSUARIO.SOBCTL_720 +
                                                                          UN_RSUSUARIO.SOBVBA_720);



                    ELSIF UN_RSUSUARIO.ASEO <>0 AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <=0 AND
                            (UN_RSUSUARIO.ESTADO = 'A' OR (UN_RSUSUARIO.ESTADO = 'C' AND UN_PARAMETROS('CALCORTADOASEO').VALOR ='NO')
                                                       OR (UN_RSUSUARIO.ESTADO = 'S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR ='SI' )) THEN
                        --Ant Resolución de aseo
                        MI_ETAPA := 'Cálculo aseo resolución 351';
                        IF MI_RSFRECUENCIAASEO <>0 THEN
                            MI_ETAPA := 'Frecuencia de aseo';
                            IF MI_RSFRECUENCIAASEO =1 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC1;
                            ELSIF MI_RSFRECUENCIAASEO =2 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC2;
                            ELSIF MI_RSFRECUENCIAASEO =3 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC3;
                            ELSIF MI_RSFRECUENCIAASEO =4 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC4;
                            ELSIF MI_RSFRECUENCIAASEO =5 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC5;
                            ELSIF MI_RSFRECUENCIAASEO =6 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC6;
                            ELSIF MI_RSFRECUENCIAASEO =7 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC7;
                            ELSIF MI_RSFRECUENCIAASEO =8 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC8;
                            ELSIF MI_RSFRECUENCIAASEO =9 THEN
                                MI_FRECUENCIAASEO := MI_TARIFASEO.VALORFREC9;
                            END IF;
                        END IF;
                        MI_ETAPA := 'Cálculo tarifa aseo domiciliario.';
                        IF MI_TARIFASEO.ASEODOMICILIARIO <> 0 THEN --Aseo domiciliario
                            MI_TASEODOMICILIARIO := MI_TARIFASEO.ASEODOMICILIARIO;
                        ELSE
                            IF MI_FRECUENCIAASEO <>0 THEN
                                IF INSTR(MI_NOMUSO,'RESIDENCIAL') <>0 THEN
                                    MI_TASEODOMICILIARIO := CASE WHEN MI_PESOASEO <> 0
                                                                 THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEOCONSUMO *
                                                                                              MI_PESOASEO + MI_FRECUENCIAASEO)
                                                                 ELSE MI_FRECUENCIAASEO
                                                            END;
                                ELSE
                                    MI_TASEODOMICILIARIO := CASE WHEN MI_PESOASEO <> 0
                                                                 THEN PCK_SYSMAN_UTL.FC_ROUND(MI_FRECUENCIAASEO * MI_PESOASEO)
                                                                 ELSE MI_FRECUENCIAASEO
                                                            END;
                                END IF;
                            END IF;
                        END IF;
                        MI_ETAPA := 'Cálculo peso aseo.';
                        IF UN_PARAMETROS('MANRES351').VALOR <> 'SI'  THEN
                            IF INSTR(MI_NOMUSO,'RESIDENCIAL') <=0  THEN
                                MI_ASEO := MI_PESOASEO * MI_TARIFASEO.ASEOCONSUMO;
                            END IF;
                        ELSE
                            IF MI_FRECUENCIAASEO >0 THEN
                                MI_ASEO := (MI_PESOASEO * MI_FRECUENCIAASEO) + MI_TARIFASEO.ASEOCONSUMO + MI_TARIFASEO.RECTRANSBARRIDOLIMP;
                            ELSE
                                MI_ASEO := (MI_PESOASEO * MI_TARIFASEO.ASEOCONSUMO) + MI_TARIFASEO.RECTRANSBARRIDOLIMP;
                            END IF;
                        END IF;

                        IF UN_PARAMETROS('COBRARPORPESO').VALOR ='SI' AND INSTR(MI_NOMUSO,'COMERCIAL') >0 AND MI_RSESTRATO ='00' THEN
                            MI_ETAPA := 'Cobrar por peso aseo.';
                            IF MI_PESOASEO >0 AND MI_PESOASEO <= MI_TARIFASEO.PESOASEO1 THEN
                                MI_ASEO := MI_TARIFASEO.TARIFAASEO1;
                                MI_SOBREASEOTARIFA :=MI_TARIFASEO.SOBREASEOTARIFA1 ;
                                MI_SUBASEOTARIFA := MI_TARIFASEO.SUBASEOTARIFA1;
                            ELSIF MI_PESOASEO >= MI_TARIFASEO.PESOASEO1 AND MI_PESOASEO <= MI_TARIFASEO.PESOASEO2 THEN
                                MI_ASEO := MI_TARIFASEO.TARIFAASEO2;
                                MI_SOBREASEOTARIFA :=MI_TARIFASEO.SOBREASEOTARIFA2 ;
                                MI_SUBASEOTARIFA := MI_TARIFASEO.SUBASEOTARIFA2;
                            ELSIF MI_PESOASEO >= MI_TARIFASEO.PESOASEO2 AND MI_PESOASEO <= MI_TARIFASEO.PESOASEO3 THEN
                                MI_ASEO := MI_TARIFASEO.TARIFAASEO3;
                                MI_SOBREASEOTARIFA :=MI_TARIFASEO.SOBREASEOTARIFA3;
                                MI_SUBASEOTARIFA := MI_TARIFASEO.SUBASEOTARIFA3;
                            END IF;
                        END IF;
                        MI_ETAPA := 'Cálculo facturados aseo.';
                        UN_FACTACT(3).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEOUNICO );
                        IF UN_PARAMETROS('MANRES351').VALOR ='SI' AND INSTR(MI_NOMUSO,'RESIDENCIAL') =0 THEN
                            UN_FACTACT(20).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TASEODOMICILIARIO * MI_PESOASEO
                                                                                + MI_TARIFASEO.DISPFINALBARRIDOLIMP);
                        ELSE
                            UN_FACTACT(20).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TASEODOMICILIARIO + MI_TARIFASEO.DISPFINALBARRIDOLIMP);
                        END IF;

                        UN_FACTACT(22).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_ASEO);

                        IF UN_PARAMETROS('MANRES351').VALOR <> 'SI' THEN
                            UN_FACTACT(21).FACTURADO := UN_FACTACT(20).FACTURADO + UN_FACTACT(22).FACTURADO;
                            UN_FACTACT(20).FACTURADO := 0;
                            UN_FACTACT(22).FACTURADO := 0;
                            IF MI_PESOASEO > 1 AND UN_PARAMETROS('CASEOCONSUMO').VALOR ='SI' THEN
                                UN_FACTACT(3).FACTURADO := 0;
                            END IF;
                        END IF;

                        IF UN_PARAMETROS('COBRARPORPESO').VALOR ='SI' AND INSTR(MI_NOMUSO,'COMERCIAL') >0 AND MI_RSESTRATO ='00' THEN
                            UN_FACTACT(22).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TASEODOMICILIARIO + MI_ASEO);
                            UN_FACTACT(20).FACTURADO := 0;
                            UN_FACTACT(21).FACTURADO := 0;
                        END IF;
                        MI_ETAPA := 'Calculo facturados aseo Barrido.';
                        IF MI_TARIFASEO.ASEOBARRIDO <> 0 THEN
                            UN_FACTACT(21).FACTURADO := 0;
                            IF UN_RSUSUARIO.ASEOBARRIDO <>0 THEN
                                UN_FACTACT(21).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEOBARRIDO *
                                                                                    CASE WHEN UN_PARAMETROS('COMMANXPESO').VALOR ='SI'
                                                                                         THEN MI_PESOASEO
                                                                                         ELSE 1
                                                                                    END);
                            END IF;
                        END IF;

                        --En access: Se utilizo el campo APARTAMENTO para almacenar el valor del indicador de tramo excedente
                        IF UN_PARAMETROS('MANRES351').VALOR ='SI' AND UN_RSUSUARIO.APARTAMENTO <> 0 THEN
                            MI_ETAPA := 'Calculo facturados aseo tramo excedente.';
                            UN_FACTACT(46).FACTURADO :=  PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.TRAMOEXCEDENTE *
                                                                                 CASE WHEN UN_PARAMETROS('TRAMOXASEO').VALOR ='SI'
                                                                                      THEN MI_PESOASEO
                                                                                      ELSE 1
                                                                                 END);
                        END IF;

                        --Aseo Deshabitado
                        IF UN_PARAMETROS('COBASEODES').VALOR ='SI' AND MI_INDDESH <>0 THEN
                            MI_ETAPA := 'Calculo aseo deshabitado.';
                            IF UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND UN_PARAMETROS('MANRES351').VALOR <> 'SI' THEN
                                UN_FACTACT(3).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEODESHABITADOS);
                                UN_FACTACT(20).FACTURADO := 0;
                                UN_FACTACT(21).FACTURADO := 0;
                                UN_FACTACT(22).FACTURADO := 0;
                                UN_RSUSUARIO.VDESHABITADO := UN_FACTACT(3).FACTURADO;

                            ELSIF (UN_PARAMETROS('MANRES351').VALOR ='SI' AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1
                                    AND UN_PARAMETROS('FACTSITIO').VALOR ='NO')
                              OR (UN_RSUSUARIO.CONSUMO <> 0 AND UN_RSUSUARIO.CONSUMO <= TO_NUMBER(UN_PARAMETROS('INTMAXDESH').VALOR)
                                    AND UN_PARAMETROS('DESHMENOR10').VALOR='SI')
                              OR (UN_PARAMETROS('FACTSITIO').VALOR ='SI')
                              OR MI_HAYUNIDADES THEN

                                IF UN_PARAMETROS('DESHABXCOM').VALOR ='SI' THEN
                                    MI_ETAPA := 'Calculo facturados aseo deshabitado por componente.';
                                    UN_FACTACT(3).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEOUNICO);
                                    UN_FACTACT(20).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEDOMICILIARIO);
                                    UN_FACTACT(21).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEBARRIDO);
                                    UN_FACTACT(22).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEOCONSUMO);
                                    UN_FACTACT(46).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_TRAMOEXCEDENTE);
                                    UN_RSUSUARIO.CALCULODESH := -1;
                                ELSE
                                    MI_ETAPA := 'Calculo facturados aseo deshabitado.';
                                    UN_FACTACT(3).FACTURADO := 0;
                                    UN_FACTACT(20).FACTURADO := 0;
                                    UN_FACTACT(21).FACTURADO := 0;
                                    UN_FACTACT(22).FACTURADO := 0;
                                    UN_FACTACT(46).FACTURADO := 0;
                                    UN_FACTACT(48).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEODESHABITADOS);
                                    UN_RSUSUARIO.VDESHABITADO := UN_FACTACT(48).FACTURADO;
                                END IF;
                            END IF;
                        END IF;

                        IF UN_PARAMETROS('MANRES351').VALOR ='SI' THEN
                            IF UN_PARAMETROS('INDAPLICA233').VALOR ='SI' AND
                                ( (UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1)
                                OR (UN_RSUSUARIO.CONSUMO <= TO_NUMBER(UN_PARAMETROS('INTMAXDESH').VALOR) And UN_PARAMETROS('DESHMENOR10').VALOR = 'SI'
                                    AND UN_PARAMETROS('DESHMENOR10').VALOR='SI')
                                OR (UN_RSUSUARIO.PORCENTAJEAPLICAR <> 100)
                                OR (UN_PARAMETROS('FINSITU').VALOR ='SI' AND NVL(UN_RSUSUARIO.FIMM, ' ') = ' ') ) THEN

                                MI_ETAPA := 'Calculo aseo Resolución Cra 233.';
                                UN_FACTACT(3).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEODESHABITADOS);
                                UN_RSUSUARIO.VDESHABITADO := UN_FACTACT(3).FACTURADO;
                                UN_FACTACT(20).FACTURADO := 0;
                                UN_FACTACT(21).FACTURADO := 0;
                                UN_FACTACT(22).FACTURADO := 0;
                            END IF;

                        ELSIF MI_INDDESH <>0 AND (
                               (UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND UN_PARAMETROS('FACTSITIO').VALOR='SI')
                            OR (UN_RSUSUARIO.CONSUMO <= TO_NUMBER(UN_PARAMETROS('INTMAXDESH').VALOR)
                                 AND UN_PARAMETROS('DESHMENOR10').VALOR='SI')
                            OR (UN_PARAMETROS('FACTSITIO').VALOR ='SI')
                            OR (MI_HAYUNIDADES) ) THEN

                            IF UN_PARAMETROS('DESHABXCOM').VALOR ='SI' THEN
                                UN_FACTACT(3).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEOUNICO);
                                UN_FACTACT(20).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEDOMICILIARIO);
                                UN_FACTACT(21).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEBARRIDO);
                                UN_FACTACT(22).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_ASEOCONSUMO);
                                UN_FACTACT(46).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.DES_TRAMOEXCEDENTE);
                                UN_RSUSUARIO.CALCULODESH := -1;
                            ELSE
                                UN_FACTACT(3).FACTURADO := 0;
                                UN_FACTACT(20).FACTURADO := 0;
                                UN_FACTACT(21).FACTURADO := 0;
                                UN_FACTACT(22).FACTURADO := 0;
                                UN_FACTACT(46).FACTURADO := 0;
                                UN_FACTACT(48).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEODESHABITADOS);
                                UN_RSUSUARIO.VDESHABITADO := UN_FACTACT(48).FACTURADO;
                            END IF;

                        END IF;

                        IF UN_PARAMETROS('MANRES351').VALOR <>'SI' THEN
                            UN_FACTACT(20).FACTURADO := CASE WHEN UN_FACTACT(22).FACTURADO > 0
                                                             THEN 0
                                                             ELSE UN_FACTACT(20).FACTURADO
                                                        END;
                        END IF;

                        IF UN_PARAMETROS('INDAPLICA21').VALOR='SI' AND UN_PARAMETROS('DESHABXCOM').VALOR ='NO' AND MI_INDDESH <>0
                            AND UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1  THEN
                            UN_FACTACT(24).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_FACTACT(24).FACTURADO * 0.4 );
                        END IF;

                        IF UN_PARAMETROS('MANRES351').VALOR ='SI' THEN
                            IF UN_RSUSUARIO.SEPARACIONENFUENTE <> 0 AND UN_FACTACT(20).FACTURADO > 0 THEN
                                UN_FACTACT(20).FACTURADO := UN_FACTACT(20).FACTURADO - MI_TARIFASEO.DESCSEPARACIONENFUENTE;
                            END IF;

                            IF UN_RSUSUARIO.NOPUERTAPUERTA <>0 AND UN_FACTACT(22).FACTURADO > 0  THEN
                                UN_FACTACT(22).FACTURADO := UN_FACTACT(22).FACTURADO - MI_TARIFASEO.DESCNOPUERTAPUERTA;
                            END IF;
                        END IF;

                        --Subsidios y sobreprecios

                        MI_ETAPA := 'Calculo Subsidios y sobreprecios Resolución 351.';
                        IF UN_PARAMETROS('CONASEOBARRIDORU').VALOR ='NO' AND UN_RSUSUARIO.NOTADEBITO = 'R' THEN
                            UN_FACTACT(3).FACTURADO := 0;
                            MI_SOBREASEOUNICO := 0;
                            MI_SUBASEOUNICO := 0;
                            IF UN_PARAMETROS('CALCOMERMANRURAL').VALOR ='NO' AND UN_RSUSUARIO.ASEOBARRIDO =0 THEN
                                UN_FACTACT(21).FACTURADO := 0;
                            END IF;
                        ELSE
                            MI_SOBREASEOUNICO := MI_TARIFASEO.SOBREASEOUNICO;
                            MI_SUBASEOUNICO := MI_TARIFASEO.SUBASEOUNICO;
                        END IF;
                        MI_ETAPA := 'Cálculo valores aseo en usuario.';
                        UN_RSUSUARIO.VASEOUNICO := UN_FACTACT(3).FACTURADO;
                        UN_RSUSUARIO.VASEODOMICILIARIO := UN_FACTACT(20).FACTURADO;
                        UN_RSUSUARIO.VASEOBARRIDO := UN_FACTACT(21).FACTURADO;
                        UN_RSUSUARIO.VASEOCONSUMO := UN_FACTACT(22).FACTURADO;

                        IF UN_PARAMETROS('MANRES351').VALOR = 'SI' AND UN_RSUSUARIO.APARTAMENTO <> 0 Then
                            UN_RSUSUARIO.VTRAMOEXCEDENTE := UN_FACTACT(46).FACTURADO;
                        END IF;

                        MI_ETAPA := 'Actualización Costo real aseo.';
                        UN_RSUSUARIO.COSTOREALASEO := TRUNC(MI_TARIFASEO.COSTOM3AS + MI_TARIFASEO.COSTOM3REC +
                                                            MI_TARIFASEO.COSTOM3PN * MI_PESOASEO + 0.5);


                        UN_RSUSUARIO.SUBBARR_LIM   := MI_SUBASEOUNICO;
                        UN_RSUSUARIO.SOBBARR_LIM   := MI_SOBREASEOUNICO;
                        UN_RSUSUARIO.SUBMANEJO_REC := MI_TARIFASEO.SUBASEOBARRIDO;
                        UN_RSUSUARIO.SOBMANEJO_REC := MI_TARIFASEO.SOBREASEOBARRIDO;

                        IF MI_RSFRECUENCIAASEO >0 THEN
                            MI_ETAPA := 'Subsidios con frecuencia de aseo.';
                            UN_RSUSUARIO.SUBDISP_FINAL := MI_TARIFASEO.SUBASEODOMICILIARIO;
                            UN_RSUSUARIO.SOBDISP_FINAL := MI_TARIFASEO.SOBREASEODOMICILIARIO;

                            IF UN_PARAMETROS('COBRARPORPESO').VALOR = 'SI' AND MI_PESOASEO <= MI_TARIFASEO.PESOASEO3 THEN
                                IF INSTR(MI_NOMUSO,'COMERCIAL') >0 AND MI_RSESTRATO ='00' THEN
                                    UN_RSUSUARIO.SUBRECOLECCION := MI_SUBASEOTARIFA;
                                    UN_RSUSUARIO.SOBRECOLECCION := MI_SOBREASEOTARIFA;
                                END IF;
                            ELSE
                                UN_RSUSUARIO.SUBRECOLECCION := MI_TARIFASEO.SUBASEOCONSUMO;
                                UN_RSUSUARIO.SOBRECOLECCION := MI_TARIFASEO.SOBREASEOCONSUMO;
                            END IF;
                            MI_SUBPESOASEO := CASE WHEN UN_PARAMETROS('MANRES351').VALOR ='SI'
                                                  THEN MI_PESOASEO
                                                  ELSE 1
                                              END;

                            IF MI_RSFRECUENCIAASEO =1 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC1 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC1 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =2 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC2 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC2 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =3 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC3 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC3 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =4 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC4 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC4 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =5 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC5 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC5 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =6 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC6 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC6 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =7 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC7 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC7 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =8 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC8 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC8 * MI_SUBPESOASEO;
                            ELSIF MI_RSFRECUENCIAASEO =9 THEN
                                UN_RSUSUARIO.SUBRECOLECCION := UN_RSUSUARIO.SUBRECOLECCION + MI_TARIFASEO.SUBFREC9 * MI_SUBPESOASEO;
                                UN_RSUSUARIO.SOBRECOLECCION := UN_RSUSUARIO.SOBRECOLECCION + MI_TARIFASEO.SOBREFREC9 * MI_SUBPESOASEO;
                            END IF;

                        ELSE
                            IF UN_PARAMETROS('COBRARPORPESO').VALOR = 'SI' AND MI_PESOASEO <= MI_TARIFASEO.PESOASEO3 THEN
                                IF INSTR(MI_NOMUSO,'COMERCIAL') >0 AND MI_RSESTRATO ='00' THEN
                                    UN_RSUSUARIO.SUBDISP_FINAL := MI_TARIFASEO.SUBASEODOMICILIARIO;
                                    UN_RSUSUARIO.SOBDISP_FINAL := MI_TARIFASEO.SOBREASEODOMICILIARIO;
                                    UN_RSUSUARIO.SUBRECOLECCION := MI_SUBASEOTARIFA;
                                    UN_RSUSUARIO.SOBRECOLECCION := MI_SOBREASEOTARIFA;
                                END IF;
                            ELSE
                                    UN_RSUSUARIO.SUBDISP_FINAL := MI_TARIFASEO.SUBASEODOMICILIARIO * MI_PESOASEO;
                                    UN_RSUSUARIO.SOBDISP_FINAL := MI_TARIFASEO.SOBREASEODOMICILIARIO * MI_PESOASEO;
                                    UN_RSUSUARIO.SUBRECOLECCION := MI_TARIFASEO.SUBASEOCONSUMO;
                                    UN_RSUSUARIO.SOBRECOLECCION := MI_TARIFASEO.SOBREASEOCONSUMO;
                                    IF UN_PARAMETROS('MANRES351').VALOR ='SI' AND INSTR(MI_NOMUSO,'COMERCIAL') > 0 AND MI_TARIFASEO.ASEOBARRIDO <>0
                                       AND UN_RSUSUARIO.ASEOBARRIDO <>0 AND UN_PARAMETROS('COMMANXPESO').VALOR ='SI' THEN

                                        UN_RSUSUARIO.SUBMANEJO_REC := MI_TARIFASEO.SUBASEOBARRIDO * MI_PESOASEO;
                                        UN_RSUSUARIO.SOBMANEJO_REC := MI_TARIFASEO.SOBREASEOBARRIDO * MI_PESOASEO;
                                    END IF;

                            END IF;

                            IF UN_PARAMETROS('MANRES351').VALOR ='SI' AND UN_RSUSUARIO.APARTAMENTO <>0 THEN
                                UN_RSUSUARIO.SUBTRAMO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SUBTRAMOEX  *
                                                                                 CASE WHEN UN_PARAMETROS('TRAMOXASEO').VALOR = 'SI'
                                                                                      THEN MI_PESOASEO
                                                                                      ELSE 1
                                                                                 END);
                                UN_RSUSUARIO.SOBTRAMO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.SOBRETRAMOEX  *
                                                                                  CASE WHEN UN_PARAMETROS('TRAMOXASEO').VALOR = 'SI'
                                                                                       THEN MI_PESOASEO
                                                                                       ELSE 1
                                                                                  END);
                            END IF;

                        END IF;

                        MI_CONDICION := MI_INDDESH <> 0 AND ((UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1
                                        AND  UN_PARAMETROS('FACTSITIO').VALOR ='NO')
                                        OR ( UN_PARAMETROS('FACTSITIO').VALOR ='SI' )
                                        OR (UN_RSUSUARIO.CONSUMO > 0 AND UN_RSUSUARIO.CONSUMO <= TO_NUMBER(UN_PARAMETROS('INTMAXDESH').VALOR)
                                            AND UN_PARAMETROS('DESHMENOR10').VALOR = 'SI' )
                                        OR MI_HAYUNIDADES);


                        IF MI_CONDICION THEN
                            PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ASEO');
                            IF UN_PARAMETROS('DESHABXCOM').VALOR ='SI' THEN
                                IF UN_PARAMETROS('CONASEOBARRIDORU').VALOR = 'NO' AND UN_RSUSUARIO.NOTADEBITO ='R' THEN
                                    UN_FACTACT(3).FACTURADO := 0;
                                    IF UN_PARAMETROS('CALCOMERMANRURAL').VALOR ='SI' AND UN_RSUSUARIO.ASEOBARRIDO = 0 THEN
                                        UN_FACTACT(21).FACTURADO := 0;
                                    END IF;
                                ELSE
                                    UN_RSUSUARIO.SUBBARR_LIM := TRUNC(MI_TARIFASEO.DES_SUBASEOUNICO + 0.5 );
                                    UN_RSUSUARIO.SOBBARR_LIM := TRUNC(MI_TARIFASEO.DES_SOBREASEOUNICO + 0.5);
                                END IF;
                                UN_RSUSUARIO.SUBDISP_FINAL  := MI_TARIFASEO.DES_SUBASEDOMICILIARIO;
                                UN_RSUSUARIO.SOBDISP_FINAL  := MI_TARIFASEO.DES_SOBREASEDOMICILIARIO;
                                UN_RSUSUARIO.SUBMANEJO_REC  := MI_TARIFASEO.DES_SUBASEBARRIDO;
                                UN_RSUSUARIO.SOBMANEJO_REC  := MI_TARIFASEO.DES_SOBREASEBARRIDO;
                                UN_RSUSUARIO.SUBRECOLECCION := MI_TARIFASEO.DES_SUBASEOCONSUMO;
                                UN_RSUSUARIO.SOBRECOLECCION := MI_TARIFASEO.DES_SOBREASEOCONSUMO;
                                UN_RSUSUARIO.SUBTRAMO       := MI_TARIFASEO.DES_SUBTRAMOEXCEDENTE;
                                UN_RSUSUARIO.SOBTRAMO       := MI_TARIFASEO.DES_SOBRETRAMOEXCEDENTE;
                                UN_RSUSUARIO.SUBDESHABITADO := 0;
                                UN_RSUSUARIO.SOBDESHABITADO := 0;
                            ELSE
                                IF UN_PARAMETROS('SUBSOBREDESHA').VALOR ='SI' THEN
                                    UN_RSUSUARIO.SUBDESHABITADO := MI_TARIFASEO.SUBASEO_DESHABITADO;
                                    UN_RSUSUARIO.SOBDESHABITADO := MI_TARIFASEO.SOBREASEO_DESHABITADO;
                                END IF;

                            END IF;

                        END IF;
                        MI_ETAPA := 'Redondeo subsidios sobreprecios en usuario.';
                        --Redondeo de subsidios  RedondeaSubSobreAseo
                        UN_RSUSUARIO.SUBBARR_LIM    := TRUNC(UN_RSUSUARIO.SUBBARR_LIM + 0.5);
                        UN_RSUSUARIO.SOBBARR_LIM    := TRUNC(UN_RSUSUARIO.SOBBARR_LIM + 0.5);
                        UN_RSUSUARIO.SUBDISP_FINAL  := TRUNC(UN_RSUSUARIO.SUBDISP_FINAL + 0.5);
                        UN_RSUSUARIO.SOBDISP_FINAL  := TRUNC(UN_RSUSUARIO.SOBDISP_FINAL + 0.5);
                        UN_RSUSUARIO.SUBMANEJO_REC  := TRUNC(UN_RSUSUARIO.SUBMANEJO_REC + 0.5);
                        UN_RSUSUARIO.SOBMANEJO_REC  := TRUNC(UN_RSUSUARIO.SOBMANEJO_REC + 0.5);
                        UN_RSUSUARIO.SUBRECOLECCION := TRUNC(UN_RSUSUARIO.SUBRECOLECCION + 0.5);
                        UN_RSUSUARIO.SOBRECOLECCION := TRUNC(UN_RSUSUARIO.SOBRECOLECCION + 0.5);
                        UN_RSUSUARIO.SUBTRAMO       := TRUNC(UN_RSUSUARIO.SUBTRAMO + 0.5);
                        UN_RSUSUARIO.SOBTRAMO       := TRUNC(UN_RSUSUARIO.SOBTRAMO + 0.5);
                        UN_RSUSUARIO.SUBDESHABITADO := TRUNC(UN_RSUSUARIO.SUBDESHABITADO + 0.5);
                        UN_RSUSUARIO.SOBDESHABITADO := TRUNC(UN_RSUSUARIO.SOBDESHABITADO + 0.5);

                        UN_RSUSUARIO.SUBASEO   := TRUNC(UN_RSUSUARIO.SUBBARR_LIM + UN_RSUSUARIO.SUBDISP_FINAL +
                                                         UN_RSUSUARIO.SUBMANEJO_REC + UN_RSUSUARIO.SUBRECOLECCION +
                                                         UN_RSUSUARIO.SUBTRAMO + 0.5);

                        UN_RSUSUARIO.SOBREASEO := TRUNC(UN_RSUSUARIO.SOBBARR_LIM + UN_RSUSUARIO.SOBDISP_FINAL +
                                                        UN_RSUSUARIO.SOBMANEJO_REC + UN_RSUSUARIO.SOBRECOLECCION +
                                                        UN_RSUSUARIO.SOBTRAMO + 0.5);


                        IF UN_RSUSUARIO.ASEOBARRIDO <> 0 AND UN_PARAMETROS('MANRES351').VALOR = 'NO' THEN
                            IF MI_INDDESH = 0 THEN
                                UN_FACTACT(21).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_TARIFASEO.ASEOBARRIDO);
                                UN_RSUSUARIO.VASEOBARRIDO := UN_FACTACT(21).FACTURADO;
                            ELSE
                                UN_FACTACT(21).FACTURADO := 0;
                                UN_RSUSUARIO.VASEOBARRIDO:= 0;
                            END IF;
                        END IF;

                        MI_CONDICION := (UN_PARAMETROS('SUBSIDIO').VALOR = 'SI' AND
                                        (UN_RSUSUARIO.ESTADO = 'A' OR (UN_RSUSUARIO.ESTADO = 'C' AND UN_PARAMETROS('CALCORTADOASEO').VALOR ='NO' )
                                                                   OR (UN_RSUSUARIO.ESTADO = 'S' AND UN_PARAMETROS('CALSUSPENDIDO').VALOR='SI' )))
                                        AND ((UN_RSUSUARIO.ASEO <>0 OR UN_RSUSUARIO.ASEOBARRIDO <>0) AND UN_RSUSUARIO.PERIODOSNOCOBROFAC <= 0);
                        IF MI_CONDICION THEN
                            MI_ETAPA := 'Discriminar subsidio real.';
                            PR_LIMPIASUBSOBRE(UN_RSUSUARIO,'ASEO');
                            IF MI_TARIFASEO.COSTOM3AS <>0 THEN
                                IF TRUNC(UN_FACTACT(21).FACTURADO - MI_TARIFASEO.COSTOM3AS + 0.5) < 0 THEN
                                    UN_RSUSUARIO.SUBBARR_LIM := ABS(TRUNC(UN_FACTACT(21).FACTURADO - MI_TARIFASEO.COSTOM3AS ));
                                ELSE
                                    UN_RSUSUARIO.SOBBARR_LIM := TRUNC(UN_FACTACT(21).FACTURADO - MI_TARIFASEO.COSTOM3AS);
                                END IF;
                                UN_RSUSUARIO.SOBREASEO := TRUNC(UN_FACTACT(21).FACTURADO - MI_TARIFASEO.COSTOM3AS + 0.5);
                            END IF;
                            IF MI_TARIFASEO.COSTOM3REC <> 0 And MI_PESOASEO = 0 THEN
                                IF TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(20).FACTURADO - MI_TARIFASEO.COSTOM3REC + 0.5) < 0 THEN
                                    UN_RSUSUARIO.SUBMANEJO_REC := ABS(TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(20).FACTURADO -
                                                                            MI_TARIFASEO.COSTOM3REC + 0.5) - UN_RSUSUARIO.SOBREASEO);
                                ELSE
                                    UN_RSUSUARIO.SOBMANEJO_REC := TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(20).FACTURADO -
                                                                        MI_TARIFASEO.COSTOM3REC + 0.5) - UN_RSUSUARIO.SOBREASEO;
                                END IF;
                                UN_RSUSUARIO.SOBREASEO := TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(20).FACTURADO - MI_TARIFASEO.COSTOM3REC + 0.5);
                            END IF;
                            IF MI_TARIFASEO.COSTOM3PN <> 0 AND MI_PESOASEO <> 0 THEN
                                IF TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(22).FACTURADO - MI_TARIFASEO.COSTOM3PN * MI_PESOASEO + 0.5) < 0 THEN
                                    UN_RSUSUARIO.SUBMANEJO_REC := ABS(TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(22).FACTURADO -
                                                                            MI_TARIFASEO.COSTOM3PN * MI_PESOASEO + 0.5) - UN_RSUSUARIO.SOBREASEO);
                                ELSE
                                    UN_RSUSUARIO.SOBMANEJO_REC := TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(22).FACTURADO -
                                                                        MI_TARIFASEO.COSTOM3PN  * MI_PESOASEO + 0.5) - UN_RSUSUARIO.SOBREASEO;
                                END IF;
                                UN_RSUSUARIO.SOBREASEO := TRUNC(UN_RSUSUARIO.SOBREASEO + UN_FACTACT(22).FACTURADO -
                                                               MI_TARIFASEO.COSTOM3PN * MI_PESOASEO + 0.5);
                            END IF;
                            IF UN_RSUSUARIO.SOBREASEO < 0 THEN
                                UN_RSUSUARIO.SUBASEO := -UN_RSUSUARIO.SOBREASEO;
                                UN_RSUSUARIO.SOBREASEO := 0;
                            END IF;
                        END IF;


                    ELSE --No maneja aseo
                        UN_FACTACT(3).FACTURADO := 0;
                        UN_FACTACT(20).FACTURADO := 0;
                        UN_FACTACT(21).FACTURADO := 0;
                        UN_FACTACT(22).FACTURADO := 0;
                        UN_FACTACT(46).FACTURADO := 0;
                        UN_RSUSUARIO.SUBASEO := 0;
                        UN_RSUSUARIO.SOBREASEO := 0;
                    END IF; --Fin Aseo
                END IF;
            END IF; --Fin principal for aseo

            IF MI_HAYUNIDADES THEN  --Se calcularon unidades residenciales
                BEGIN
                    BEGIN
                        MI_TABLA     :=' SP_UNIDADESRESIDENCIALES ';

                        MI_CAMPOS    :=' BARRIDO                = '|| UN_FACTACT(3).FACTURADO ||'
                                        ,BARRIDO_SUB            = '|| UN_RSUSUARIO.SUBBARR_LIM ||'
                                        ,BARRIDO_SOBRE          = '|| UN_RSUSUARIO.SOBBARR_LIM ||'
                                        ,DISPOFINAL             = '|| UN_FACTACT(20).FACTURADO ||'
                                        ,DISPOFINAL_SUB         = '|| UN_RSUSUARIO.SUBDISP_FINAL ||'
                                        ,DISPOFINAL_SOBRE       = '|| UN_RSUSUARIO.SOBDISP_FINAL ||'
                                        ,MANEJORECA             = '|| UN_FACTACT(21).FACTURADO ||'
                                        ,MANEJORECA_SUB         = '|| UN_RSUSUARIO.SUBMANEJO_REC ||'
                                        ,MANEJORECA_SOBRE       = '|| UN_RSUSUARIO.SOBMANEJO_REC ||'
                                        ,RECOLECCION_TRA        = '|| UN_FACTACT(22).FACTURADO ||'
                                        ,RECOLECCION_TRA_SUB    = '|| UN_RSUSUARIO.SUBRECOLECCION ||'
                                        ,RECOLECCION_TRA_SOBRE  = '|| UN_RSUSUARIO.SOBRECOLECCION ||'
                                        ,TRAMOEXCE              = '|| UN_FACTACT(46).FACTURADO ||'
                                        ,TRAMOEXCE_SUB          = '|| UN_RSUSUARIO.SUBTRAMO ||'
                                        ,TRAMOEXCE_SOBRE        = '|| UN_RSUSUARIO.SOBTRAMO ||'
                                        ,SUBASEO                = '|| UN_RSUSUARIO.SUBASEO ||'
                                        ,SOBREASEO              = '|| UN_RSUSUARIO.SOBREASEO ||'
                                        ,VDESHABITADO           = '|| UN_RSUSUARIO.VDESHABITADO ||'
                                        ,SUBDESHABITADO         = '|| UN_RSUSUARIO.SUBDESHABITADO ||'
                                        ,SOBREDESHABITADO       = '|| UN_RSUSUARIO.SOBDESHABITADO ||'
                                        ,VALASEOCCS_720         = '|| UN_FACTACT(201).FACTURADO ||'
                                        ,SUBCCS_720             = '|| UN_RSUSUARIO.SUBCCS_720 ||'
                                        ,SOBCCS_720             = '|| UN_RSUSUARIO.SOBCCS_720 ||'
                                        ,VALASEOCBLS_720        = '|| UN_FACTACT(202).FACTURADO ||'
                                        ,SUBCBLS_720            = '|| UN_RSUSUARIO.SUBCBLS_720 ||'
                                        ,SOBCBLS_720            = '|| UN_RSUSUARIO.SOBCBLS_720 ||'
                                        ,VALASEOCLUS_720        = '|| UN_FACTACT(203).FACTURADO ||'
                                        ,SUBCLUS_720            = '|| UN_RSUSUARIO.SUBCLUS_720 ||'
                                        ,SOBCLUS_720            = '|| UN_RSUSUARIO.SOBCLUS_720 ||'
                                        ,VALASEOCRT_720         = '|| UN_FACTACT(204).FACTURADO ||'
                                        ,SUBCRT_720             = '|| UN_RSUSUARIO.SUBCRT_720 ||'
                                        ,SOBCRT_720             = '|| UN_RSUSUARIO.SOBCRT_720 ||'
                                        ,VALASEOCDF_720         = '|| UN_FACTACT(205).FACTURADO ||'
                                        ,SUBCDF_720             = '|| UN_RSUSUARIO.SUBCDF_720 ||'
                                        ,SOBCDF_720             = '|| UN_RSUSUARIO.SOBCDF_720 ||'
                                        ,VALASEOCTL_720         = '|| UN_FACTACT(206).FACTURADO ||'
                                        ,SUBCTL_720             = '|| UN_RSUSUARIO.SUBCTL_720 ||'
                                        ,SOBCTL_720             = '|| UN_RSUSUARIO.SOBCTL_720 ||'
                                        ,VALASEOVBA_720         = '|| UN_FACTACT(207).FACTURADO ||'
                                        ,SUBVBA_720             = '|| UN_RSUSUARIO.SUBVBA_720 ||'
                                        ,SOBVBA_720             = '|| UN_RSUSUARIO.SOBVBA_720 ||'
                                        ,MODIFIED_BY            = '''|| UN_USUARIO ||'''
                                        ,DATE_MODIFIED          = SYSDATE  ';

                        MI_CONDICIONACME :=' COMPANIA       = ''' || UN_COMPANIA || '''
                                            AND CODIGORUTA  = ''' || UN_RSUSUARIO.CODIGORUTA || '''
                                            AND CONSECUTIVO = '|| MI_RSUNIDEDES.CONSECUTIVO||' ';

                        MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICIONACME);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                    END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                    MI_MSGERROR(1).CLAVE := 'USUARIO';
                    MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUNIDADESASEO,
                                               UN_TABLAERROR => MI_TABLA,
                                               UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END IF;

        END LOOP;

        IF MI_HAYUNIDADES THEN
            SELECT  SUM(BARRIDO)  SUMABARRIDO,                          SUM(BARRIDO_SUB)  SUMABARRIDO_SUB,
                    SUM(BARRIDO_SOBRE)  SUMABARRIDO_SOBRE,              SUM(DISPOFINAL)  SUMADISPOFINAL,
                    SUM(DISPOFINAL_SUB)  SUMADISPOFINAL_SUB,            SUM(DISPOFINAL_SOBRE)  SUMADISPOFINAL_SOBRE,
                    SUM(MANEJORECA)  SUMAMANEJORECA,                    SUM(MANEJORECA_SUB)  SUMAMANEJORECA_SUB,
                    SUM(MANEJORECA_SOBRE)  SUMAMANEJORECA_SOBRE,        SUM(RECOLECCION_TRA)  SUMARECOLECCION_TRA,
                    SUM(RECOLECCION_TRA_SUB) SUMARECOLECCION_TRA_SUB,   SUM(RECOLECCION_TRA_SOBRE)  SUMARECOLECCION_TRA_SOBRE,
                    SUM(TRAMOEXCE)  SUMATRAMOEXCE,                      SUM(TRAMOEXCE_SUB)  SUMATRAMOEXCE_SUB,
                    SUM(TRAMOEXCE_SOBRE)  SUMATRAMOEXCE_SOBRE,          SUM(SUBASEO)  SUMASUBASEO,
                    SUM(SOBREASEO)  SUMASOBREEO,                        SUM(VDESHABITADO)  SUMAVDESHABITADO,
                    SUM(SUBDESHABITADO)  SUMASUBDESHABITADO,            SUM(SOBREDESHABITADO) SUMASOBREDESHABITADO,
                    SUM(VALASEOCCS_720)  SUMVALASEOCCS_720,             SUM(SUBCCS_720)  SUMSUBCCS_720,
                    SUM(SOBCCS_720) SUMSOBCCS_720,                      SUM(VALASEOCBLS_720) SUMVALASEOCBLS_720,
                    SUM(SUBCBLS_720) SUMSUBCBLS_720,                    SUM(SOBCBLS_720) SUMSOBCBLS_720,
                    SUM(VALASEOCLUS_720)  SUMVALASEOCLUS_720,           SUM(SUBCLUS_720) SUMSUBCLUS_720,
                    SUM(SOBCLUS_720) SUMSOBCLUS_720,                    SUM(VALASEOCRT_720)  SUMVALASEOCRT_720,
                    SUM(SUBCRT_720) SUMSUBCRT_720,                      SUM(SOBCRT_720) SUMSOBCRT_720,
                    SUM(VALASEOCDF_720)  SUMVALASEOCDF_720,             SUM(SUBCDF_720) SUMSUBCDF_720,
                    SUM(SOBCDF_720) SUMSOBCDF_720,                      SUM(VALASEOCTL_720)  SUMVALASEOCTL_720,
                    SUM(SUBCTL_720) SUMSUBCTL_720,                      SUM(SOBCTL_720) SUMSOBCTL_720,
                    SUM(VALASEOVBA_720)  SUMVALASEOVBA_720,             SUM(SUBVBA_720) SUMSUBVBA_720,
                    SUM(SOBVBA_720)  SUMSOBVBA_720

            INTO    UN_FACTACT(3).FACTURADO,                            UN_RSUSUARIO.SUBBARR_LIM,
                    UN_RSUSUARIO.SOBBARR_LIM,                           UN_FACTACT(20).FACTURADO,
                    UN_RSUSUARIO.SUBDISP_FINAL,                         UN_RSUSUARIO.SOBDISP_FINAL,
                    UN_FACTACT(21).FACTURADO,                           UN_RSUSUARIO.SUBMANEJO_REC,
                    UN_RSUSUARIO.SOBMANEJO_REC,                         UN_FACTACT(22).FACTURADO,
                    UN_RSUSUARIO.SUBRECOLECCION,                        UN_RSUSUARIO.SOBRECOLECCION,
                    UN_FACTACT(46).FACTURADO,                           UN_RSUSUARIO.SUBTRAMO,
                    UN_RSUSUARIO.SOBTRAMO,                              UN_RSUSUARIO.SUBASEO,
                    UN_RSUSUARIO.SOBREASEO,                             UN_FACTACT(48).FACTURADO,
                    UN_RSUSUARIO.SUBDESHABITADO,                        UN_RSUSUARIO.SOBDESHABITADO,
                    UN_FACTACT(201).FACTURADO,                          UN_RSUSUARIO.SUBCCS_720,
                    UN_RSUSUARIO.SOBCCS_720,                            UN_FACTACT(202).FACTURADO,
                    UN_RSUSUARIO.SUBCBLS_720,                           UN_RSUSUARIO.SOBCBLS_720,
                    UN_FACTACT(203).FACTURADO,                          UN_RSUSUARIO.SUBCLUS_720,
                    UN_RSUSUARIO.SOBCLUS_720,                           UN_FACTACT(204).FACTURADO,
                    UN_RSUSUARIO.SUBCRT_720,                            UN_RSUSUARIO.SOBCRT_720,
                    UN_FACTACT(205).FACTURADO,                          UN_RSUSUARIO.SUBCDF_720,
                    UN_RSUSUARIO.SOBCDF_720,                            UN_FACTACT(206).FACTURADO,
                    UN_RSUSUARIO.SUBCTL_720,                            UN_RSUSUARIO.SOBCTL_720,
                    UN_FACTACT(207).FACTURADO,                          UN_RSUSUARIO.SUBVBA_720,
                    UN_RSUSUARIO.SOBVBA_720

            FROM    SP_UNIDADESRESIDENCIALES
            WHERE   COMPANIA   = UN_COMPANIA
              AND   CODIGORUTA = UN_RSUSUARIO.CODIGORUTA;

            UN_RSUSUARIO.VASEOUNICO := UN_FACTACT(3).FACTURADO;
            UN_RSUSUARIO.VASEODOMICILIARIO := UN_FACTACT(20).FACTURADO;
            UN_RSUSUARIO.VASEOBARRIDO := UN_FACTACT(21).FACTURADO;
            UN_RSUSUARIO.VASEOCONSUMO := UN_FACTACT(22).FACTURADO;
            UN_RSUSUARIO.VTRAMOEXCEDENTE := UN_FACTACT(46).FACTURADO;
            UN_RSUSUARIO.VDESHABITADO := UN_FACTACT(48).FACTURADO;

            UN_RSUSUARIO.VALASEOCCS_720   := UN_FACTACT(201).FACTURADO;
            UN_RSUSUARIO.VALASEOCBLS_720  := UN_FACTACT(202).FACTURADO;
            UN_RSUSUARIO.VALASEOCLUS_720  := UN_FACTACT(203).FACTURADO;
            UN_RSUSUARIO.VALASEOCRT_720   := UN_FACTACT(204).FACTURADO;
            UN_RSUSUARIO.VALASEOCDF_720   := UN_FACTACT(205).FACTURADO;
            UN_RSUSUARIO.VALASEOCTL_720   := UN_FACTACT(206).FACTURADO;
            UN_RSUSUARIO.VALASEOVBA_720   := UN_FACTACT(207).FACTURADO;
        END IF;
    END IF;--Fin Validacion tercerizado

    FOR i IN UN_FACTACT.FIRST .. UN_FACTACT.LAST
    LOOP
        IF UN_FACTACT.EXISTS(i) AND i IN(3,20,21,22,46,48,201,202,203,204,205,206,207) THEN
          MI_TOTALASEO := MI_TOTALASEO + UN_FACTACT(i).FACTURADO;
        END IF;
    END LOOP;

    UN_RSUSUARIO.TOTALASEO := MI_TOTALASEO;
    UN_RSUSUARIO.TOTFACTURAASEO := MI_TOTALASEO;
    RETURN MI_TOTALASEO;

  EXCEPTION WHEN OTHERS THEN
     MI_MSGERROR(1).CLAVE := 'ETAPA';
     MI_MSGERROR(1).VALOR := MI_ETAPA;
     MI_MSGERROR(2).CLAVE := 'USO';
     MI_MSGERROR(2).VALOR := CASE WHEN MI_HAYUNIDADES THEN MI_NOMUSO ELSE UN_RSUSUARIO.USO END;
     MI_MSGERROR(3).CLAVE := 'ESTRATO';
     MI_MSGERROR(3).VALOR := CASE WHEN MI_HAYUNIDADES THEN MI_RSESTRATO ELSE UN_RSUSUARIO.ESTRATO END;
     MI_MSGERROR(4).CLAVE := 'USUARIO';
     MI_MSGERROR(4).VALOR := UN_RSUSUARIO.CODIGORUTA;
     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CALCULOASEO,
                               UN_TABLAERROR => 'SP_USUARIO',
                               UN_REEMPLAZOS => MI_MSGERROR);

  END FC_CALCULOASEO;

--13
  PROCEDURE PR_LIMPIASUBSOBRE(
       /*
       NAME              : PR_LIMPIASUBSOBRE --> Se unifica de la función calculoFacturacion
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 06/03/2017
       TIME              : 04:01 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Procedimiento que se encarga de limpiar los subsidios y sobreprecios de los
                           principales servicios.

       PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_SERVICIO       => Define a que servicio se van a limpiar los subsidios y
                                                sobreprecios.

       MODIFICATIONS     :

       @NAME:    limpiarSubSobre
       @METHOD:  POST
     */
  UN_RSUSUARIO          IN OUT TUSUARIO
  ,UN_SERVICIO          SP_SERVICIO.NOMBRE%TYPE
  ) AS
  BEGIN
    IF UN_SERVICIO = 'ACUEDUCTO' THEN

        UN_RSUSUARIO.COSTOFIJOAC            := 0;
        UN_RSUSUARIO.COSTOM3AC              := 0;
        UN_RSUSUARIO.TARIFAM3BASICOAC       := 0;
        UN_RSUSUARIO.TARIFAM3COMPLEMENTARIOAC := 0;
        UN_RSUSUARIO.TARIFAM3SUNTUARIOAC    := 0;
        UN_RSUSUARIO.SUBSINMEDICION         := 0;
        UN_RSUSUARIO.SOBRESINMEDICION       := 0;
        UN_RSUSUARIO.SUB_TASAAMBIENTALAC    := 0;
        UN_RSUSUARIO.SOBRE_TASAAMBIENTALAC  := 0;
        UN_RSUSUARIO.ACSINMEDICION          := 0;
        UN_RSUSUARIO.CARGOFIJO              := 0;
        UN_RSUSUARIO.VALORBASICO            := 0;
        UN_RSUSUARIO.VALORCOMPLEMENTARIO    := 0;
        UN_RSUSUARIO.VALORSUNTUARIO         := 0;
        UN_RSUSUARIO.SUBFIJO                := 0;
        UN_RSUSUARIO.SUBCONSUMOAC           := 0;
        UN_RSUSUARIO.SUBACUEDUCTO           := 0;
        UN_RSUSUARIO.SOBREFIJO              := 0;
        UN_RSUSUARIO.SOBRECONSUMOAC         := 0;
        UN_RSUSUARIO.SOBREACUEDUCTO         := 0;
        UN_RSUSUARIO.TOTALACUEDUCTO         := 0;

    ELSIF UN_SERVICIO ='ALCANTARILLADO' THEN
        UN_RSUSUARIO.COSTOM3AL              := 0;
        UN_RSUSUARIO.COSTOFIJOAL            := 0;
        UN_RSUSUARIO.TARIFAM3BASICOAL       := 0;
        UN_RSUSUARIO.TARIFAM3COMPLEMENTARIOAL := 0;
        UN_RSUSUARIO.TARIFAM3SUNTUARIOAL    := 0;

        UN_RSUSUARIO.CARGOFIJOAL            := 0;
        UN_RSUSUARIO.VALORALCBASICO         := 0;
        UN_RSUSUARIO.VALORALCCOMPLEMENTARIO := 0;
        UN_RSUSUARIO.VALORALCSUNTUARIO      := 0;
        UN_RSUSUARIO.SUBFIJOALC             := 0;
        UN_RSUSUARIO.SUBCONSUMOAL           := 0;
        UN_RSUSUARIO.SOBREFIJOALC           := 0;
        UN_RSUSUARIO.SOBRECONSUMOAL         := 0;
        UN_RSUSUARIO.SUBALCSINMEDICION      := 0;
        UN_RSUSUARIO.SOBREALCSINMEDICION    := 0;
        UN_RSUSUARIO.SUB_TASAAMBIENTALALC   := 0;
        UN_RSUSUARIO.SOBRE_TASAAMBIENTALALC := 0;
        UN_RSUSUARIO.COSTOCONSUMOAL         := 0;
        UN_RSUSUARIO.ALSINMEDICION          := 0;
        UN_RSUSUARIO.TOTALALCANTARILLADO    := 0;
        UN_RSUSUARIO.SUBALCANTARILLADO      := 0;
        UN_RSUSUARIO.SOBREALCANTARILLADO    := 0;

    ELSIF UN_SERVICIO ='ASEO' THEN
        UN_RSUSUARIO.SUBBARR_LIM    := 0;
        UN_RSUSUARIO.SOBBARR_LIM    := 0;
        UN_RSUSUARIO.SUBDISP_FINAL  := 0;
        UN_RSUSUARIO.SOBDISP_FINAL  := 0;
        UN_RSUSUARIO.SUBMANEJO_REC  := 0;
        UN_RSUSUARIO.SOBMANEJO_REC  := 0;
        UN_RSUSUARIO.SUBRECOLECCION := 0;
        UN_RSUSUARIO.SOBRECOLECCION := 0;
        UN_RSUSUARIO.SUBTRAMO       := 0;
        UN_RSUSUARIO.SOBTRAMO       := 0;
        UN_RSUSUARIO.SUBDESHABITADO := 0;
        UN_RSUSUARIO.SOBDESHABITADO := 0;
        UN_RSUSUARIO.VASEOUNICO     := 0;
        UN_RSUSUARIO.VASEODOMICILIARIO := 0;
        UN_RSUSUARIO.VASEOBARRIDO   := 0;
        UN_RSUSUARIO.VASEOCONSUMO   := 0;
        UN_RSUSUARIO.VDESHABITADO   := 0;
        UN_RSUSUARIO.VTRAMOEXCEDENTE := 0;
        UN_RSUSUARIO.TOTFACTURAASEO := 0;

        --Subsidios 720
        UN_RSUSUARIO.SUBCCS_720     := 0;
        UN_RSUSUARIO.SOBCCS_720     := 0;
        UN_RSUSUARIO.SUBCBLS_720    := 0;
        UN_RSUSUARIO.SOBCBLS_720    := 0;
        UN_RSUSUARIO.SUBCLUS_720    := 0;
        UN_RSUSUARIO.SOBCLUS_720    := 0;
        UN_RSUSUARIO.SUBCRT_720     := 0;
        UN_RSUSUARIO.SOBCRT_720     := 0;
        UN_RSUSUARIO.SUBCDF_720     := 0;
        UN_RSUSUARIO.SOBCDF_720     := 0;
        UN_RSUSUARIO.SUBCTL_720     := 0;
        UN_RSUSUARIO.SOBCTL_720     := 0;
        UN_RSUSUARIO.SUBVBA_720     := 0;
        UN_RSUSUARIO.SOBVBA_720     := 0;
        UN_RSUSUARIO.SUBASEO        := 0;
        UN_RSUSUARIO.SOBREASEO      := 0;

        UN_RSUSUARIO.VALASEOCCS_720  := 0;
        UN_RSUSUARIO.VALASEOCBLS_720  := 0;
        UN_RSUSUARIO.VALASEOCLUS_720  := 0;
        UN_RSUSUARIO.VALASEOCRT_720  := 0;
        UN_RSUSUARIO.VALASEOCDF_720  := 0;
        UN_RSUSUARIO.VALASEOCTL_720  := 0;
        UN_RSUSUARIO.VALASEOVBA_720 := 0;


        UN_RSUSUARIO.TOTALASEO      := 0;



    END IF;
  END PR_LIMPIASUBSOBRE;

--14
  FUNCTION FC_CALCULOMULTIUSUARIOS
  (
    /*
      NAME              : FC_CALCULOMULTIUSUARIOS --> En Access MULTIUSUARIOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 06/03/2017
      TIME              : 10:07 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Cálculo de multiusuarios con sus respectivos servicios de acueducto alcantarillado y aseo

      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones

      MODIFICATIONS     :

      @NAME:    calcularMultiusuario
      @METHOD:  POST
    */
  UN_COMPANIA              IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO             IN OUT  TUSUARIO
  ,UN_PARAMETROS            IN      TI_PARAMETRO
  ,UN_FACTURADOACT          IN OUT  TI_FACTURADO
  ,UN_VALIDATARIFA          IN OUT  VARCHAR2
  ,UN_USUARIO               IN      SP_USUARIO.CREATED_BY%TYPE :=''
  ) RETURN NUMBER
  AS

  MI_RSMULTI                 SYS_REFCURSOR;
  MI_TARIFAMULTI             TTARIFAS;
  MI_TOTALMTL                NUMBER(20,0)       DEFAULT 0;
  MI_VALIDATARIFA            VARCHAR2(300 CHAR) DEFAULT 'C';
  MI_VALIDATARIFASEO         VARCHAR2(300 CHAR) DEFAULT 'C';
  MI_PORCENTAJE              SP_MULTIUSUARIOS.PORCENTAJE%TYPE;
  MI_CANTIDAD                SP_MULTIUSUARIOS.CANTIDAD%TYPE;
  MI_FACTURADOMULTI          TI_FACTURADO;
  MI_CONDICION               BOOLEAN;
  MI_MANEJA720               BOOLEAN;
  MI_ASEODESH720             BOOLEAN    DEFAULT FALSE;
  MI_CONTADOR                NUMBER(20,0) DEFAULT 0;
  MI_NOMUSO                  SP_USOS.NOMBRE%TYPE;

  MI_CONSUMOTOTAL            SP_USUARIO.CONSUMO%TYPE                   DEFAULT 0;

  MI_CONSUMOMTL              SP_USUARIO.CONSUMO%TYPE                   DEFAULT 0;
  MI_CONSUMO1MTL             SP_USUARIO.CONSUMOBASICO%TYPE             DEFAULT 0;
  MI_CONSUMO2MTL             SP_USUARIO.CONSUMOCOMPLEMENTARIO%TYPE     DEFAULT 0;
  MI_CONSUMO3MTL             SP_USUARIO.CONSUMOSUNTUARIO%TYPE          DEFAULT 0;

  MI_TOTALCONSUMOBASICO      SP_USUARIO.CONSUMOBASICO%TYPE             DEFAULT 0;
  MI_TOTALCONSUMOCOMPL       SP_USUARIO.CONSUMOCOMPLEMENTARIO%TYPE     DEFAULT 0;
  MI_TOTALCONSUMOSUNT        SP_USUARIO.CONSUMOSUNTUARIO%TYPE          DEFAULT 0;
  MI_VALORBASICO             SP_USUARIO.VALORBASICO%TYPE               DEFAULT 0;
  MI_VALORCOMPLEMENTARIO     SP_USUARIO.VALORCOMPLEMENTARIO%TYPE       DEFAULT 0;
  MI_VALORSUNTUARIO          SP_USUARIO.VALORSUNTUARIO%TYPE            DEFAULT 0;
  MI_SUBFIJO                 SP_USUARIO.SUBFIJO%TYPE                   DEFAULT 0;
  MI_SUB_TASAAMBIENTALAC     SP_USUARIO.SUB_TASAAMBIENTALAC%TYPE       DEFAULT 0;
  MI_SOBRE_TASAAMBIENTALAC   SP_USUARIO.SOBRE_TASAAMBIENTALAC%TYPE     DEFAULT 0;
  MI_ACSINMEDICION           SP_USUARIO.ACSINMEDICION%TYPE             DEFAULT 0;
  MI_CARGOFIJO               SP_USUARIO.CARGOFIJO%TYPE                 DEFAULT 0;
  MI_SUBCONSUMOAC            SP_USUARIO.SUBCONSUMOAC%TYPE              DEFAULT 0;
  MI_SUBACUEDUCTO            SP_USUARIO.SUBACUEDUCTO%TYPE              DEFAULT 0;
  MI_SOBREFIJO               SP_USUARIO.SOBREFIJO%TYPE                 DEFAULT 0;
  MI_SOBRECONSUMOAC          SP_USUARIO.SOBRECONSUMOAC%TYPE            DEFAULT 0;
  MI_SOBREACUEDUCTO          SP_USUARIO.SOBREACUEDUCTO%TYPE            DEFAULT 0;
  MI_SOBRESINMEDICION        SP_USUARIO.SOBRESINMEDICION%TYPE          DEFAULT 0;
  MI_SUBSINMEDICION          SP_USUARIO.SUBSINMEDICION%TYPE            DEFAULT 0;
  MI_TOTALACUEDUCTO          SP_USUARIO.TOTALACUEDUCTO%TYPE            DEFAULT 0;

  MI_CARGOFIJOAL             SP_USUARIO.CARGOFIJOAL%TYPE               DEFAULT 0;
  MI_VALORALCBASICO          SP_USUARIO.VALORALCBASICO%TYPE            DEFAULT 0;
  MI_VALORALCCOMPLEMENTARIO  SP_USUARIO.VALORALCCOMPLEMENTARIO%TYPE    DEFAULT 0;
  MI_VALORALCSUNTUARIO       SP_USUARIO.VALORALCSUNTUARIO%TYPE         DEFAULT 0;
  MI_SUBFIJOALC              SP_USUARIO.SUBFIJOALC%TYPE                DEFAULT 0;
  MI_SUBCONSUMOAL            SP_USUARIO.SUBCONSUMOAL%TYPE              DEFAULT 0;
  MI_SOBREFIJOALC            SP_USUARIO.SOBREFIJOALC%TYPE              DEFAULT 0;
  MI_SOBRECONSUMOAL          SP_USUARIO.SOBRECONSUMOAL%TYPE            DEFAULT 0;
  MI_SUBALCSINMEDICION       SP_USUARIO.SUBALCSINMEDICION%TYPE         DEFAULT 0;
  MI_SOBREALCSINMEDICION     SP_USUARIO.SOBREALCSINMEDICION%TYPE       DEFAULT 0;
  MI_SUB_TASAAMBIENTALALC    SP_USUARIO.SUB_TASAAMBIENTALALC%TYPE      DEFAULT 0;
  MI_SOBRE_TASAAMBIENTALALC  SP_USUARIO.SOBRE_TASAAMBIENTALALC%TYPE    DEFAULT 0;
  MI_COSTOCONSUMOAL          SP_USUARIO.COSTOCONSUMOAL%TYPE            DEFAULT 0;
  MI_ALSINMEDICION           SP_USUARIO.ALSINMEDICION%TYPE             DEFAULT 0;
  MI_TOTALALCANTARILLADO     SP_USUARIO.TOTALALCANTARILLADO%TYPE       DEFAULT 0;
  MI_SUBALCANTARILLADO       SP_USUARIO.SUBALCANTARILLADO%TYPE         DEFAULT 0;
  MI_SOBREALCANTARILLADO     SP_USUARIO.SOBREALCANTARILLADO%TYPE       DEFAULT 0;

  MI_SUBCCS_720               SP_USUARIO.SUBCCS_720%TYPE                DEFAULT 0;
  MI_SOBCCS_720               SP_USUARIO.SOBCCS_720%TYPE                DEFAULT 0;
  MI_SUBCBLS_720              SP_USUARIO.SUBCBLS_720%TYPE               DEFAULT 0;
  MI_SOBCBLS_720              SP_USUARIO.SOBCBLS_720%TYPE               DEFAULT 0;
  MI_SUBCLUS_720              SP_USUARIO.SUBCLUS_720%TYPE               DEFAULT 0;
  MI_SOBCLUS_720              SP_USUARIO.SOBCLUS_720%TYPE               DEFAULT 0;
  MI_SUBCRT_720               SP_USUARIO.SUBCRT_720%TYPE                DEFAULT 0;
  MI_SOBCRT_720               SP_USUARIO.SOBCRT_720%TYPE                DEFAULT 0;
  MI_SUBCDF_720               SP_USUARIO.SUBCDF_720%TYPE                DEFAULT 0;
  MI_SOBCDF_720               SP_USUARIO.SOBCDF_720%TYPE                DEFAULT 0;
  MI_SUBCTL_720               SP_USUARIO.SUBCTL_720%TYPE                DEFAULT 0;
  MI_SOBCTL_720               SP_USUARIO.SOBCTL_720%TYPE                DEFAULT 0;
  MI_SUBVBA_720               SP_USUARIO.SUBVBA_720%TYPE                DEFAULT 0;
  MI_SOBVBA_720               SP_USUARIO.SOBVBA_720%TYPE                DEFAULT 0;
  MI_SUBASEO                  SP_USUARIO.SUBASEO%TYPE                   DEFAULT 0;
  MI_SOBREASEO                SP_USUARIO.SOBREASEO%TYPE                 DEFAULT 0;
  MI_TOTALASEO                SP_USUARIO.TOTALASEO%TYPE                 DEFAULT 0;
  MI_TOTFACTURAASEO           SP_USUARIO.TOTFACTURAASEO%TYPE            DEFAULT 0;

  MI_SUBBARR_LIM              SP_USUARIO.SUBBARR_LIM%TYPE               DEFAULT 0;
  MI_SOBBARR_LIM              SP_USUARIO.SOBBARR_LIM%TYPE               DEFAULT 0;
  MI_SUBDISP_FINAL            SP_USUARIO.SUBDISP_FINAL%TYPE             DEFAULT 0;
  MI_SOBDISP_FINAL            SP_USUARIO.SOBDISP_FINAL%TYPE             DEFAULT 0;
  MI_SUBMANEJO_REC            SP_USUARIO.SUBMANEJO_REC%TYPE             DEFAULT 0;
  MI_SOBMANEJO_REC            SP_USUARIO.SOBMANEJO_REC%TYPE             DEFAULT 0;
  MI_SUBRECOLECCION           SP_USUARIO.SUBRECOLECCION%TYPE            DEFAULT 0;
  MI_SOBRECOLECCION           SP_USUARIO.SOBRECOLECCION%TYPE            DEFAULT 0;
  MI_SUBTRAMO                 SP_USUARIO.SUBTRAMO%TYPE                  DEFAULT 0;
  MI_SOBTRAMO                 SP_USUARIO.SOBTRAMO%TYPE                  DEFAULT 0;
  MI_SUBDESHABITADO           SP_USUARIO.SUBDESHABITADO%TYPE            DEFAULT 0;
  MI_SOBDESHABITADO           SP_USUARIO.SOBDESHABITADO%TYPE            DEFAULT 0;
  MI_COSTOREALASEO            SP_USUARIO.COSTOREALASEO%TYPE             DEFAULT 0;

  MI_VASEOUNICO               SP_USUARIO.VASEOUNICO%TYPE                DEFAULT 0;
  MI_VASEODOMICILIARIO        SP_USUARIO.VASEODOMICILIARIO%TYPE         DEFAULT 0;
  MI_VASEOBARRIDO             SP_USUARIO.VASEOBARRIDO%TYPE              DEFAULT 0;
  MI_VASEOCONSUMO             SP_USUARIO.VASEOCONSUMO%TYPE              DEFAULT 0;
  MI_VTRAMOEXCEDENTE          SP_USUARIO.VTRAMOEXCEDENTE%TYPE           DEFAULT 0;
  MI_VDESHABITADO             SP_USUARIO.VDESHABITADO%TYPE              DEFAULT 0;

  MI_VALASEOCCS_720           SP_USUARIO.VALASEOCCS_720%TYPE            DEFAULT 0;
  MI_VALASEOCBLS_720          SP_USUARIO.VALASEOCBLS_720%TYPE           DEFAULT 0;
  MI_VALASEOCLUS_720          SP_USUARIO.VALASEOCLUS_720%TYPE           DEFAULT 0;
  MI_VALASEOCRT_720           SP_USUARIO.VALASEOCRT_720%TYPE            DEFAULT 0;
  MI_VALASEOCDF_720           SP_USUARIO.VALASEOCDF_720%TYPE            DEFAULT 0;
  MI_VALASEOCTL_720           SP_USUARIO.VALASEOCTL_720%TYPE            DEFAULT 0;
  MI_VALASEOVBA_720           SP_USUARIO.VALASEOVBA_720%TYPE            DEFAULT 0;


  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

   MI_MANEJA720 := CASE WHEN UN_PARAMETROS('MANEJA720').VALOR ='SI'
                        THEN TRUE
                        ELSE FALSE
                   END;

   MI_CONDICION := (UN_RSUSUARIO.ANO || ',' || UN_RSUSUARIO.PERIODO) < UN_PARAMETROS('INICIO720').VALOR
                    OR UN_RSUSUARIO.CICLO = TO_NUMBER(UN_PARAMETROS('CICLOEXCLUIDO720').VALOR);

   IF MI_CONDICION THEN
       MI_MANEJA720 := FALSE;
   END IF;

   IF MI_MANEJA720 AND UN_RSUSUARIO.INDDESHABITADO <>0 THEN --Se cobra una sola vez el aseo con la tarifa principal del usuario.
       MI_ASEODESH720 := TRUE;
   END IF;

   UN_FACTURADOACT(1).FACTURADO  := 0;
   UN_FACTURADOACT(2).FACTURADO  := 0;
   UN_FACTURADOACT(5).FACTURADO  := 0;
   UN_FACTURADOACT(19).FACTURADO := 0;
   UN_FACTURADOACT(43).FACTURADO := 0;
   UN_FACTURADOACT(4).FACTURADO :=  0;
   UN_FACTURADOACT(24).FACTURADO  := 0;
   UN_FACTURADOACT(33).FACTURADO  := 0;
   UN_FACTURADOACT(45).FACTURADO := 0;
   UN_FACTURADOACT(3).FACTURADO := 0;
   UN_FACTURADOACT(20).FACTURADO := 0;
   UN_FACTURADOACT(21).FACTURADO := 0;
   UN_FACTURADOACT(22).FACTURADO := 0;
   UN_FACTURADOACT(46).FACTURADO := 0;
   UN_FACTURADOACT(48).FACTURADO := 0;
   UN_FACTURADOACT(201).FACTURADO  := 0;
   UN_FACTURADOACT(202).FACTURADO  := 0;
   UN_FACTURADOACT(203).FACTURADO  := 0;
   UN_FACTURADOACT(204).FACTURADO  := 0;
   UN_FACTURADOACT(205).FACTURADO  := 0;
   UN_FACTURADOACT(206).FACTURADO  := 0;
   UN_FACTURADOACT(207).FACTURADO  := 0;

   MI_CONSUMOTOTAL := UN_RSUSUARIO.CONSUMO;
   <<MULTIUSUARIO>>
   FOR MI_RSMULTI IN (
       SELECT COMPANIA,CICLO,CODIGORUTA,CONSECUTIVO,USO,ESTRATO,ESTRATOASEO,
              PORCENTAJE, PESOASEO,CANTIDAD,TAFNA_720,TAFA_720
       FROM   SP_MULTIUSUARIOS
       WHERE  COMPANIA   = UN_COMPANIA
         AND  CICLO      = UN_RSUSUARIO.CICLO
         AND  CODIGORUTA = UN_RSUSUARIO.CODIGORUTA
   )

   LOOP
       MI_FACTURADOMULTI.DELETE;
       --Cambia los consumos dependiendo de cada porcentaje
       MI_PORCENTAJE := MI_RSMULTI.PORCENTAJE;  --Se saca al momento de ingresar los multiusuarios.
       MI_CONSUMOMTL := MI_CONSUMOTOTAL * MI_PORCENTAJE;
       MI_CANTIDAD := MI_RSMULTI.CANTIDAD;
       MI_CONTADOR := MI_CONTADOR + 1;

       --Carga las tarifas para acueducto y alcantarillado del multiusuario
       MI_VALIDATARIFA := 'C'; --Cambia si no existe tarifa.
       MI_TARIFAMULTI := FC_CARGATARIFAS(UN_COMPANIA       => UN_COMPANIA
                                        ,UN_ANO            => UN_RSUSUARIO.ANO
                                        ,UN_PERIODO        => UN_RSUSUARIO.PERIODO
                                        ,UN_USO            => MI_RSMULTI.USO
                                        ,UN_ESTRATO        => MI_RSMULTI.ESTRATO
                                        ,UN_VALIDATARIFA   => MI_VALIDATARIFA);

        IF MI_VALIDATARIFA = 'C' THEN  --Si existen las tarifas

           IF MI_CONSUMOMTL < MI_TARIFAMULTI.CONSUMOBASICO * MI_CANTIDAD THEN
               MI_CONSUMO1MTL := MI_CONSUMOMTL;
               MI_TOTALCONSUMOBASICO := MI_TOTALCONSUMOBASICO + MI_CONSUMO1MTL;
           ELSE
               MI_CONSUMO1MTL := MI_TARIFAMULTI.CONSUMOBASICO * MI_CANTIDAD;
               MI_TOTALCONSUMOBASICO := MI_TOTALCONSUMOBASICO + MI_CONSUMO1MTL;

               IF MI_CONSUMOMTL <= MI_TARIFAMULTI.CONSUMOCOMPLEMENTARIO * MI_CANTIDAD THEN
                   MI_CONSUMO2MTL := MI_CONSUMOMTL - MI_TARIFAMULTI.CONSUMOBASICO * MI_CANTIDAD;
                   MI_TOTALCONSUMOCOMPL := MI_TOTALCONSUMOCOMPL +  MI_CONSUMO2MTL;
               ELSE
                   MI_CONSUMO2MTL := (MI_TARIFAMULTI.CONSUMOCOMPLEMENTARIO - MI_TARIFAMULTI.CONSUMOBASICO) * MI_CANTIDAD;
                   MI_TOTALCONSUMOCOMPL := MI_TOTALCONSUMOCOMPL +  MI_CONSUMO2MTL;
                   IF MI_CONSUMOMTL > MI_TARIFAMULTI.CONSUMOSUNTUARIO * MI_CANTIDAD THEN
                       MI_CONSUMO3MTL := MI_CONSUMOMTL - MI_TARIFAMULTI.CONSUMOCOMPLEMENTARIO * MI_CANTIDAD;
                       MI_TOTALCONSUMOSUNT := MI_TOTALCONSUMOSUNT + MI_CONSUMO3MTL;
                   END IF;
               END IF;
           END IF;


           --Acueducto
           UN_RSUSUARIO.CONSUMOBASICO := MI_CONSUMO1MTL;
           UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO := MI_CONSUMO2MTL;
           UN_RSUSUARIO.CONSUMOSUNTUARIO := MI_CONSUMO3MTL;
           --Cambia la tarifa multiplicandole el porcentaje
           MI_TARIFAMULTI.CARGOFIJO := MI_TARIFAMULTI.CARGOFIJO * MI_PORCENTAJE;
           MI_TARIFAMULTI.SUBFIJO := MI_TARIFAMULTI.SUBFIJO * MI_PORCENTAJE;
           MI_TARIFAMULTI.SOBREFIJO := MI_TARIFAMULTI.SOBREFIJO * MI_PORCENTAJE;

           MI_TARIFAMULTI.SINMEDICION := MI_TARIFAMULTI.SINMEDICION * MI_PORCENTAJE;
           MI_TARIFAMULTI.SUBSINMEDICION := MI_TARIFAMULTI.SUBSINMEDICION * MI_PORCENTAJE;
           MI_TARIFAMULTI.SOBRESINMEDICION := MI_TARIFAMULTI.SOBRESINMEDICION * MI_PORCENTAJE;

           --Llama a la función general de acueducto
           MI_TOTALMTL :=  FC_CALCULOACUEDUCTO (UN_COMPANIA     => UN_COMPANIA
                                               ,UN_RSUSUARIO    => UN_RSUSUARIO
                                               ,UN_RSTARIFA     => MI_TARIFAMULTI
                                               ,UN_TIPOCALCULO  => UN_RSUSUARIO.TIPOCALCULO
                                               ,UN_PARAMETROS   => UN_PARAMETROS
                                               ,UN_FACTURADOACT => MI_FACTURADOMULTI);

           --Acumula los facturados
           UN_FACTURADOACT(1).FACTURADO  := UN_FACTURADOACT(1).FACTURADO + MI_FACTURADOMULTI(1).FACTURADO;
           UN_FACTURADOACT(2).FACTURADO  := UN_FACTURADOACT(2).FACTURADO + MI_FACTURADOMULTI(2).FACTURADO;
           UN_FACTURADOACT(5).FACTURADO  := UN_FACTURADOACT(5).FACTURADO +  MI_FACTURADOMULTI(5).FACTURADO;
           UN_FACTURADOACT(19).FACTURADO := UN_FACTURADOACT(19).FACTURADO + MI_FACTURADOMULTI(19).FACTURADO;
           UN_FACTURADOACT(43).FACTURADO := UN_FACTURADOACT(43).FACTURADO + MI_FACTURADOMULTI(43).FACTURADO;

           --Variables que acumulan los totales que se calculan en usuario las cuales pasan a ser el definitivo al final.
           MI_SUB_TASAAMBIENTALAC := MI_SUB_TASAAMBIENTALAC + UN_RSUSUARIO.SUB_TASAAMBIENTALAC;
           MI_SOBRE_TASAAMBIENTALAC := MI_SOBRE_TASAAMBIENTALAC + UN_RSUSUARIO.SOBRE_TASAAMBIENTALAC;
           MI_ACSINMEDICION := MI_ACSINMEDICION + UN_RSUSUARIO.ACSINMEDICION;
           MI_CARGOFIJO := MI_CARGOFIJO + UN_RSUSUARIO.CARGOFIJO;
           MI_VALORBASICO := MI_VALORBASICO + UN_RSUSUARIO.VALORBASICO;
           MI_VALORCOMPLEMENTARIO := MI_VALORCOMPLEMENTARIO + UN_RSUSUARIO.VALORCOMPLEMENTARIO;
           MI_VALORSUNTUARIO := MI_VALORSUNTUARIO + UN_RSUSUARIO.VALORSUNTUARIO;
           MI_SUBFIJO := MI_SUBFIJO + UN_RSUSUARIO.SUBFIJO;
           MI_SUBCONSUMOAC := MI_SUBCONSUMOAC + UN_RSUSUARIO.SUBCONSUMOAC;
           MI_SUBACUEDUCTO := MI_SUBACUEDUCTO + UN_RSUSUARIO.SUBACUEDUCTO;
           MI_SOBREFIJO := MI_SOBREFIJO + UN_RSUSUARIO.SOBREFIJO;
           MI_SOBRECONSUMOAC := MI_SOBRECONSUMOAC + UN_RSUSUARIO.SOBRECONSUMOAC;
           MI_SOBREACUEDUCTO := MI_SOBREACUEDUCTO + UN_RSUSUARIO.SOBREACUEDUCTO;
           MI_SOBRESINMEDICION := MI_SOBRESINMEDICION + UN_RSUSUARIO.SOBRESINMEDICION;
           MI_SUBSINMEDICION := MI_SUBSINMEDICION + UN_RSUSUARIO.SUBSINMEDICION;
           MI_TOTALACUEDUCTO := MI_TOTALACUEDUCTO + UN_RSUSUARIO.TOTALACUEDUCTO;

           --Alcantarillado
           UN_RSUSUARIO.CONSUMOBASICOALC := MI_CONSUMO1MTL;
           UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC := MI_CONSUMO2MTL;
           UN_RSUSUARIO.CONSUMOSUNTUARIOALC := MI_CONSUMO3MTL;

           MI_TARIFAMULTI.FIJOALCANTARILLADO := MI_TARIFAMULTI.FIJOALCANTARILLADO * MI_PORCENTAJE;
           MI_TARIFAMULTI.SUBALCFIJO := MI_TARIFAMULTI.SUBALCFIJO * MI_PORCENTAJE;
           MI_TARIFAMULTI.SOBREALCFIJO := MI_TARIFAMULTI.SOBREALCFIJO * MI_PORCENTAJE;

           MI_TARIFAMULTI.SINMEDICIONALC := MI_TARIFAMULTI.SINMEDICIONALC * MI_PORCENTAJE;
           MI_TARIFAMULTI.SUBALCSINMEDICION := MI_TARIFAMULTI.SUBALCSINMEDICION * MI_PORCENTAJE;
           MI_TARIFAMULTI.SOBREALCSINMEDICION := MI_TARIFAMULTI.SOBREALCSINMEDICION * MI_PORCENTAJE;

           --Llama a la función general de alcantarillado
           MI_TOTALMTL :=  MI_TOTALMTL + FC_CALCULOALCANTARILLADO(UN_COMPANIA     => UN_COMPANIA
                                                                 ,UN_RSUSUARIO    => UN_RSUSUARIO
                                                                 ,UN_RSTARIFA     => MI_TARIFAMULTI
                                                                 ,UN_TIPOCALCULO  => UN_RSUSUARIO.TIPOCALCULO
                                                                 ,UN_PARAMETROS   => UN_PARAMETROS
                                                                 ,UN_FACTURADOACT => MI_FACTURADOMULTI);


           --Acumula facturados Alcantarillado
           UN_FACTURADOACT(4).FACTURADO := UN_FACTURADOACT(4).FACTURADO + MI_FACTURADOMULTI(4).FACTURADO;
           UN_FACTURADOACT(24).FACTURADO  := UN_FACTURADOACT(24).FACTURADO + MI_FACTURADOMULTI(24).FACTURADO;
           UN_FACTURADOACT(33).FACTURADO  := UN_FACTURADOACT(33).FACTURADO + MI_FACTURADOMULTI(33).FACTURADO;
           UN_FACTURADOACT(45).FACTURADO := UN_FACTURADOACT(45).FACTURADO + MI_FACTURADOMULTI(45).FACTURADO;

           MI_CARGOFIJOAL := MI_CARGOFIJOAL + UN_RSUSUARIO.CARGOFIJOAL;
           MI_VALORALCBASICO := MI_VALORALCBASICO + UN_RSUSUARIO.VALORALCBASICO;
           MI_VALORALCCOMPLEMENTARIO := MI_VALORALCCOMPLEMENTARIO + UN_RSUSUARIO.VALORALCCOMPLEMENTARIO;
           MI_VALORALCSUNTUARIO := MI_VALORALCSUNTUARIO + UN_RSUSUARIO.VALORALCSUNTUARIO;
           MI_SUBFIJOALC := MI_SUBFIJOALC + UN_RSUSUARIO.SUBFIJOALC;
           MI_SUBCONSUMOAL := MI_SUBCONSUMOAL + UN_RSUSUARIO.SUBCONSUMOAL;
           MI_SOBREFIJOALC := MI_SOBREFIJOALC + UN_RSUSUARIO.SOBREFIJOALC;
           MI_SOBRECONSUMOAL := MI_SOBRECONSUMOAL + UN_RSUSUARIO.SOBRECONSUMOAL;
           MI_SUBALCSINMEDICION := MI_SUBALCSINMEDICION + UN_RSUSUARIO.SUBALCSINMEDICION;
           MI_SOBREALCSINMEDICION := MI_SOBREALCSINMEDICION + UN_RSUSUARIO.SOBREALCSINMEDICION;
           MI_SUB_TASAAMBIENTALALC := MI_SUB_TASAAMBIENTALALC + UN_RSUSUARIO.SUB_TASAAMBIENTALALC;
           MI_SOBRE_TASAAMBIENTALALC := MI_SOBRE_TASAAMBIENTALALC + UN_RSUSUARIO.SOBRE_TASAAMBIENTALALC;
           MI_COSTOCONSUMOAL := MI_COSTOCONSUMOAL + UN_RSUSUARIO.COSTOCONSUMOAL;
           MI_ALSINMEDICION := MI_ALSINMEDICION + UN_RSUSUARIO.ALSINMEDICION;
           MI_TOTALALCANTARILLADO := MI_TOTALALCANTARILLADO + UN_RSUSUARIO.TOTALALCANTARILLADO;
           MI_SUBALCANTARILLADO := MI_SUBALCANTARILLADO + UN_RSUSUARIO.SUBALCANTARILLADO;
           MI_SOBREALCANTARILLADO := MI_SOBREALCANTARILLADO + UN_RSUSUARIO.SOBREALCANTARILLADO;


           --Carga las tarifas de aseo.
           MI_VALIDATARIFASEO :='C'; --Cambia en FC_CARGATARIFAS, si no existe tarifas
           MI_TARIFAMULTI := FC_CARGATARIFAS(UN_COMPANIA       => UN_COMPANIA
                                            ,UN_ANO            => UN_RSUSUARIO.ANO
                                            ,UN_PERIODO        => UN_RSUSUARIO.PERIODO
                                            ,UN_USO            => MI_RSMULTI.USO
                                            ,UN_ESTRATO        => CASE WHEN MI_ASEODESH720 THEN UN_RSUSUARIO.ESTRATOASEO ELSE MI_RSMULTI.ESTRATOASEO END
                                            ,UN_VALIDATARIFA   => MI_VALIDATARIFASEO);


            IF MI_VALIDATARIFASEO ='C'  THEN
                IF MI_ASEODESH720 AND MI_CONTADOR > 1 THEN  --Solo se puede calcular una sola vez Aseo deshabitado con 720.
                   MI_VALIDATARIFASEO :='Multiusuario con 720 deshabitado';
                END IF;

               --Validaciones especiales res 351 aseo
                IF MI_ASEODESH720 = FALSE THEN
                   UN_RSUSUARIO.PESOASEO := MI_RSMULTI.PESOASEO;
                   MI_TARIFAMULTI.ASEOCONSUMO := MI_TARIFAMULTI.ASEOCONSUMO * MI_CANTIDAD;
                   MI_TARIFAMULTI.ASEOBARRIDO := MI_TARIFAMULTI.ASEOBARRIDO * MI_CANTIDAD;
                   MI_TARIFAMULTI.RECTRANSBARRIDOLIMP := MI_TARIFAMULTI.RECTRANSBARRIDOLIMP * MI_CANTIDAD;
                   MI_TARIFAMULTI.ASEOUNICO := MI_TARIFAMULTI.ASEOUNICO * MI_CANTIDAD;

                   MI_NOMUSO := UPPER(PCK_SERVICIOS_PUBLICOS.FC_NOMBREUSO(UN_COMPANIA  => UN_COMPANIA,
                                                                          UN_CODIGO    => MI_TARIFAMULTI.USO));
                   IF MI_TARIFAMULTI.ASEODOMICILIARIO <> 0 THEN  --Validación multiusuario
                       IF INSTR(MI_NOMUSO,'RESIDENCIAL') <> 0 THEN
                           MI_TARIFAMULTI.ASEODOMICILIARIO := MI_TARIFAMULTI.ASEODOMICILIARIO * MI_CANTIDAD;
                       ELSE
                           MI_TARIFAMULTI.ASEODOMICILIARIO := MI_TARIFAMULTI.ASEODOMICILIARIO * MI_RSMULTI.PESOASEO;
                       END IF;
                       MI_TARIFAMULTI.ASEODOMICILIARIO := MI_TARIFAMULTI.ASEODOMICILIARIO + (MI_TARIFAMULTI.DISPFINALBARRIDOLIMP * MI_CANTIDAD);
                   END IF;

                   MI_TARIFAMULTI.ASEODESHABITADOS := MI_TARIFAMULTI.ASEODESHABITADOS * MI_CANTIDAD;

                    IF NOT(UN_RSUSUARIO.LECTURA = UN_RSUSUARIO.LECTURA1 AND UN_RSUSUARIO.INDDESHABITADO <>0) THEN
                         MI_TARIFAMULTI.SUBASEOUNICO := MI_TARIFAMULTI.SUBASEOUNICO * MI_CANTIDAD;
                         MI_TARIFAMULTI.SOBREASEOUNICO := MI_TARIFAMULTI.SOBREASEOUNICO * MI_CANTIDAD;

                         MI_TARIFAMULTI.SUBASEOBARRIDO := MI_TARIFAMULTI.SUBASEOBARRIDO * MI_CANTIDAD;
                         MI_TARIFAMULTI.SOBREASEOBARRIDO := MI_TARIFAMULTI.SOBREASEOBARRIDO * MI_CANTIDAD;

                         MI_TARIFAMULTI.SUBASEODOMICILIARIO := MI_TARIFAMULTI.SUBASEODOMICILIARIO * MI_CANTIDAD;
                         MI_TARIFAMULTI.SOBREASEODOMICILIARIO := MI_TARIFAMULTI.SOBREASEODOMICILIARIO * MI_CANTIDAD;
                    END IF;

                END IF;


                 MI_TOTALMTL :=  MI_TOTALMTL + FC_CALCULOASEO(UN_COMPANIA     => UN_COMPANIA
                                                             ,UN_RSUSUARIO    => UN_RSUSUARIO
                                                             ,UN_RSTARIFA     => MI_TARIFAMULTI
                                                             ,UN_PARAMETROS   => UN_PARAMETROS
                                                             ,UN_FACTACT      => MI_FACTURADOMULTI
                                                             ,UN_USUARIO      => UN_USUARIO);

                 --720
                 UN_FACTURADOACT(201).FACTURADO  := UN_FACTURADOACT(201).FACTURADO + (MI_FACTURADOMULTI(201).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(202).FACTURADO  := UN_FACTURADOACT(202).FACTURADO + (MI_FACTURADOMULTI(202).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(203).FACTURADO  := UN_FACTURADOACT(203).FACTURADO + (MI_FACTURADOMULTI(203).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(204).FACTURADO  := UN_FACTURADOACT(204).FACTURADO + (MI_FACTURADOMULTI(204).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(205).FACTURADO  := UN_FACTURADOACT(205).FACTURADO + (MI_FACTURADOMULTI(205).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(206).FACTURADO  := UN_FACTURADOACT(206).FACTURADO + (MI_FACTURADOMULTI(206).FACTURADO * MI_CANTIDAD);
                 UN_FACTURADOACT(207).FACTURADO  := UN_FACTURADOACT(207).FACTURADO + (MI_FACTURADOMULTI(207).FACTURADO * MI_CANTIDAD);

                 MI_VALASEOCCS_720    := MI_VALASEOCCS_720 + (UN_RSUSUARIO.VALASEOCCS_720);
                 MI_VALASEOCBLS_720   := MI_VALASEOCBLS_720 + (UN_RSUSUARIO.VALASEOCBLS_720);
                 MI_VALASEOCLUS_720   := MI_VALASEOCLUS_720 + (UN_RSUSUARIO.VALASEOCLUS_720);
                 MI_VALASEOCRT_720    := MI_VALASEOCRT_720 + (UN_RSUSUARIO.VALASEOCRT_720);
                 MI_VALASEOCDF_720    := MI_VALASEOCDF_720 + (UN_RSUSUARIO.VALASEOCDF_720);
                 MI_VALASEOCTL_720    := MI_VALASEOCTL_720 + (UN_RSUSUARIO.VALASEOCTL_720);
                 MI_VALASEOVBA_720    := MI_VALASEOVBA_720 + (UN_RSUSUARIO.VALASEOVBA_720);

                 MI_SUBCCS_720  :=  MI_SUBCCS_720  + (UN_RSUSUARIO.SUBCCS_720 * MI_CANTIDAD);
                 MI_SOBCCS_720  :=  MI_SOBCCS_720  + (UN_RSUSUARIO.SOBCCS_720 * MI_CANTIDAD);
                 MI_SUBCBLS_720 :=  MI_SUBCBLS_720 + (UN_RSUSUARIO.SUBCBLS_720 * MI_CANTIDAD);
                 MI_SOBCBLS_720 :=  MI_SOBCBLS_720 + (UN_RSUSUARIO.SOBCBLS_720 * MI_CANTIDAD);
                 MI_SUBCLUS_720 :=  MI_SUBCLUS_720 + (UN_RSUSUARIO.SUBCLUS_720 * MI_CANTIDAD);
                 MI_SOBCLUS_720 :=  MI_SOBCLUS_720 + (UN_RSUSUARIO.SOBCLUS_720 * MI_CANTIDAD);
                 MI_SUBCRT_720  :=  MI_SUBCRT_720  + (UN_RSUSUARIO.SUBCRT_720 * MI_CANTIDAD);
                 MI_SOBCRT_720  :=  MI_SOBCRT_720  + (UN_RSUSUARIO.SOBCRT_720 * MI_CANTIDAD);
                 MI_SUBCDF_720  :=  MI_SUBCDF_720  + (UN_RSUSUARIO.SUBCDF_720 * MI_CANTIDAD);
                 MI_SOBCDF_720  :=  MI_SOBCDF_720  + (UN_RSUSUARIO.SOBCDF_720 * MI_CANTIDAD);
                 MI_SUBCTL_720  :=  MI_SUBCTL_720  + (UN_RSUSUARIO.SUBCTL_720 * MI_CANTIDAD);
                 MI_SOBCTL_720  :=  MI_SOBCTL_720  + (UN_RSUSUARIO.SOBCTL_720 * MI_CANTIDAD);
                 MI_SUBVBA_720  :=  MI_SUBVBA_720  + (UN_RSUSUARIO.SUBVBA_720 * MI_CANTIDAD);
                 MI_SOBVBA_720  :=  MI_SOBVBA_720  + (UN_RSUSUARIO.SOBVBA_720 * MI_CANTIDAD);
                 MI_SUBASEO     :=  MI_SUBASEO     + (UN_RSUSUARIO.SUBASEO * MI_CANTIDAD);
                 MI_SOBREASEO   :=  MI_SOBREASEO   + (UN_RSUSUARIO.SOBREASEO * MI_CANTIDAD);

                 MI_TOTALASEO   :=  MI_TOTALASEO   + (UN_RSUSUARIO.TOTALASEO * CASE WHEN MI_ASEODESH720 THEN MI_CANTIDAD ELSE 1 END);


                 --Aseo normal

                 UN_FACTURADOACT(3).FACTURADO   := UN_FACTURADOACT(3).FACTURADO + (MI_FACTURADOMULTI(3).FACTURADO);
                 UN_FACTURADOACT(20).FACTURADO  := UN_FACTURADOACT(20).FACTURADO + (MI_FACTURADOMULTI(20).FACTURADO);
                 UN_FACTURADOACT(21).FACTURADO  := UN_FACTURADOACT(21).FACTURADO + (MI_FACTURADOMULTI(21).FACTURADO);
                 UN_FACTURADOACT(22).FACTURADO  := UN_FACTURADOACT(22).FACTURADO + (MI_FACTURADOMULTI(22).FACTURADO);
                 UN_FACTURADOACT(46).FACTURADO  := UN_FACTURADOACT(46).FACTURADO + MI_FACTURADOMULTI(46).FACTURADO;
                 UN_FACTURADOACT(48).FACTURADO  := UN_FACTURADOACT(48).FACTURADO + (MI_FACTURADOMULTI(48).FACTURADO);

                 MI_VASEOUNICO        := MI_VASEOUNICO + (UN_RSUSUARIO.VASEOUNICO);
                 MI_VASEODOMICILIARIO := MI_VASEODOMICILIARIO + (UN_RSUSUARIO.VASEODOMICILIARIO);
                 MI_VASEOBARRIDO      := MI_VASEOBARRIDO + (UN_RSUSUARIO.VASEOBARRIDO);
                 MI_VASEOCONSUMO      := MI_VASEOCONSUMO + (UN_RSUSUARIO.VASEOCONSUMO);
                 MI_VTRAMOEXCEDENTE   := MI_VTRAMOEXCEDENTE + (UN_RSUSUARIO.VTRAMOEXCEDENTE);
                 MI_VDESHABITADO      := MI_VDESHABITADO + (UN_RSUSUARIO.VDESHABITADO);

                 MI_SUBBARR_LIM    := MI_SUBBARR_LIM + (UN_RSUSUARIO.SUBBARR_LIM);
                 MI_SOBBARR_LIM    := MI_SOBBARR_LIM + (UN_RSUSUARIO.SOBBARR_LIM);
                 MI_SUBDISP_FINAL  := MI_SUBDISP_FINAL + (UN_RSUSUARIO.SUBDISP_FINAL);
                 MI_SOBDISP_FINAL  := MI_SOBDISP_FINAL + (UN_RSUSUARIO.SOBDISP_FINAL);
                 MI_SUBMANEJO_REC  := MI_SUBMANEJO_REC + (UN_RSUSUARIO.SUBMANEJO_REC);
                 MI_SOBMANEJO_REC  := MI_SOBMANEJO_REC + (UN_RSUSUARIO.SOBMANEJO_REC);
                 MI_SUBRECOLECCION := MI_SUBRECOLECCION + (UN_RSUSUARIO.SUBRECOLECCION);
                 MI_SOBRECOLECCION := MI_SOBRECOLECCION + (UN_RSUSUARIO.SOBRECOLECCION);
                 MI_SUBTRAMO       := MI_SUBTRAMO + (UN_RSUSUARIO.SUBTRAMO);
                 MI_SOBTRAMO       := MI_SOBTRAMO + (UN_RSUSUARIO.SOBTRAMO);
                 MI_SUBDESHABITADO := MI_SUBDESHABITADO + (UN_RSUSUARIO.SUBDESHABITADO);
                 MI_SOBDESHABITADO := MI_SOBDESHABITADO + (UN_RSUSUARIO.SOBDESHABITADO);
                 MI_COSTOREALASEO  := MI_COSTOREALASEO  + (UN_RSUSUARIO.COSTOREALASEO * MI_CANTIDAD);
                 MI_TOTFACTURAASEO := MI_TOTFACTURAASEO + (UN_RSUSUARIO.TOTFACTURAASEO);



                      --Actualiza los valores en la tabla sp_multiusuarios
                    BEGIN
                         BEGIN
                             MI_TABLA := ' SP_MULTIUSUARIOS ';
                             MI_CAMPOS := ' SUBAC                = '|| (UN_RSUSUARIO.SUBACUEDUCTO  * MI_PORCENTAJE) || '
                                           ,SOBREAC              = '|| (UN_RSUSUARIO.SOBREACUEDUCTO * MI_PORCENTAJE) || '
                                           ,CONSUMOAC            = '|| (UN_RSUSUARIO.VALORBASICO + UN_RSUSUARIO.VALORCOMPLEMENTARIO + UN_RSUSUARIO.VALORSUNTUARIO) || '
                                           ,SUBALC               = '|| (UN_RSUSUARIO.SUBALCANTARILLADO * MI_PORCENTAJE) ||'
                                           ,SOBREALC             = '|| (UN_RSUSUARIO.SOBREALCANTARILLADO * MI_PORCENTAJE)  ||'
                                           ,CONSUMOALC           = '|| (UN_RSUSUARIO.VALORALCBASICO + UN_RSUSUARIO.VALORALCCOMPLEMENTARIO + UN_RSUSUARIO.VALORALCSUNTUARIO) ||'
                                           ,VALASEOCCS_720       = '|| (MI_FACTURADOMULTI(201).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCCS_720           = '|| (UN_RSUSUARIO.SUBCCS_720 * MI_CANTIDAD) ||'
                                           ,SOBCCS_720           = '|| (UN_RSUSUARIO.SOBCCS_720 * MI_CANTIDAD) ||'
                                           ,VALASEOCBLS_720      = '|| (MI_FACTURADOMULTI(202).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCBLS_720          = '|| (UN_RSUSUARIO.SUBCBLS_720 * MI_CANTIDAD) ||'
                                           ,SOBCBLS_720          = '|| (UN_RSUSUARIO.SOBCBLS_720 * MI_CANTIDAD) ||'
                                           ,VALASEOCLUS_720      = '|| (MI_FACTURADOMULTI(203).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCLUS_720          = '|| (UN_RSUSUARIO.SUBCLUS_720 * MI_CANTIDAD) ||'
                                           ,SOBCLUS_720          = '|| (UN_RSUSUARIO.SUBCLUS_720 * MI_CANTIDAD) ||'
                                           ,VALASEOCRT_720       = '|| (MI_FACTURADOMULTI(204).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCRT_720           = '|| (UN_RSUSUARIO.SUBCRT_720 * MI_CANTIDAD) ||'
                                           ,SOBCRT_720           = '|| (UN_RSUSUARIO.SOBCRT_720 * MI_CANTIDAD) ||'
                                           ,VALASEOCDF_720       = '|| (MI_FACTURADOMULTI(205).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCDF_720           = '|| (UN_RSUSUARIO.SUBCDF_720 * MI_CANTIDAD) ||'
                                           ,SOBCDF_720           = '|| (UN_RSUSUARIO.SOBCDF_720 * MI_CANTIDAD) ||'
                                           ,VALASEOCTL_720       = '|| (MI_FACTURADOMULTI(206).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBCTL_720           = '|| (UN_RSUSUARIO.SUBCTL_720 * MI_CANTIDAD) ||'
                                           ,SOBCTL_720           = '|| (UN_RSUSUARIO.SOBCTL_720 * MI_CANTIDAD) ||'
                                           ,VALASEOVBA_720       = '|| (MI_FACTURADOMULTI(207).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBVBA_720           = '|| (UN_RSUSUARIO.SUBVBA_720 * MI_CANTIDAD) ||'
                                           ,SOBVBA_720           = '|| (UN_RSUSUARIO.SOBVBA_720 * MI_CANTIDAD) ||'
                                           ,CONSUMOASEO          = '|| (MI_FACTURADOMULTI(22).FACTURADO * MI_CANTIDAD) ||'
                                           ,SUBRECOLECCION       = '|| (UN_RSUSUARIO.SUBRECOLECCION) ||'
                                           ,SOBRECOLECCION       = '|| (UN_RSUSUARIO.SOBRECOLECCION) ||'
                                           ,SUBBARR_LIM          = '|| (UN_RSUSUARIO.SUBBARR_LIM) ||'
                                           ,SOBBARR_LIM          = '|| (UN_RSUSUARIO.SOBBARR_LIM) ||'
                                           ,SUBDISP_FINAL        = '|| (UN_RSUSUARIO.SUBDISP_FINAL) ||'
                                           ,SOBDISP_FINAL        = '|| (UN_RSUSUARIO.SOBDISP_FINAL) ||'
                                           ,SUBMANEJO_REC        = '|| (UN_RSUSUARIO.SUBMANEJO_REC) ||'
                                           ,SOBMANEJO_REC        = '|| (UN_RSUSUARIO.SOBMANEJO_REC) ||'
                                           ,FECHACALCULADO       = SYSDATE
                                           ,MODIFIED_BY          = '''|| UN_USUARIO ||'''
                                           ,DATE_MODIFIED        = SYSDATE  ';

                             MI_CONDICIONACME :=' COMPANIA       = ''' || UN_COMPANIA || '''
                                                 AND CICLO       = ' || MI_RSMULTI.CICLO || '
                                                 AND CODIGORUTA  = ''' ||MI_RSMULTI.CODIGORUTA || '''
                                                 AND CONSECUTIVO = ' || MI_RSMULTI.CONSECUTIVO ||'
                                                 AND USO         = ''' || MI_RSMULTI.USO || '''
                                                 AND ESTRATO     = ''' || MI_RSMULTI.ESTRATO || '''
                                                 AND ESTRATOASEO = ''' || MI_RSMULTI.ESTRATOASEO ||''' ';



                             MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS,
                                                            UN_CONDICION => MI_CONDICIONACME);

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                         MI_MSGERROR(1).CLAVE := 'MULTIUSUARIO';
                         MI_MSGERROR(1).VALOR :=  MI_RSMULTI.CODIGORUTA;
                         MI_MSGERROR(2).CLAVE := 'CICLO';
                         MI_MSGERROR(2).VALOR :=  MI_RSMULTI.CICLO;
                         MI_MSGERROR(3).CLAVE := 'USO';
                         MI_MSGERROR(3).VALOR :=  MI_RSMULTI.USO;
                         MI_MSGERROR(4).CLAVE := 'ESTRATO';
                         MI_MSGERROR(4).VALOR :=  MI_RSMULTI.ESTRATO;
                         MI_MSGERROR(5).CLAVE := 'CONSECUTIVO';
                         MI_MSGERROR(5).VALOR :=  MI_RSMULTI.CONSECUTIVO;

                         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERR_ACTUALIZARMULTIUSUARIO,
                                                    UN_TABLAERROR => MI_TABLA,
                                                    UN_REEMPLAZOS => MI_MSGERROR);
                    END;
            ELSE
                UN_VALIDATARIFA := 'Multiusuario: '|| MI_RSMULTI.CODIGORUTA ||' ' || MI_VALIDATARIFASEO;
            END IF;  --Fin Validación tarifa aseo.                  

        ELSE
            UN_VALIDATARIFA := 'Multiusuario: '|| MI_RSMULTI.CODIGORUTA ||' ' || MI_VALIDATARIFA;
        END IF; -- Fin validación de si no existen las tarifas.
  END LOOP MULTIUSUARIO;

  --Coloca los valores definitivos a cobrar.
  UN_RSUSUARIO.CONSUMO := MI_CONSUMOTOTAL;
  UN_RSUSUARIO.CONSUMOBASICO := MI_TOTALCONSUMOBASICO;
  UN_RSUSUARIO.CONSUMOCOMPLEMENTARIO := MI_TOTALCONSUMOCOMPL;
  UN_RSUSUARIO.CONSUMOSUNTUARIO := MI_TOTALCONSUMOSUNT;

  UN_RSUSUARIO.CONSUMOBASICOALC := MI_TOTALCONSUMOBASICO;
  UN_RSUSUARIO.CONSUMOCOMPLEMENTARIOALC := MI_TOTALCONSUMOCOMPL;
  UN_RSUSUARIO.CONSUMOSUNTUARIOALC := MI_TOTALCONSUMOSUNT;

  --Acueducto
  UN_RSUSUARIO.SOBRESINMEDICION      := MI_SOBRESINMEDICION;
  UN_RSUSUARIO.SUB_TASAAMBIENTALAC   := MI_SUB_TASAAMBIENTALAC;
  UN_RSUSUARIO.SOBRE_TASAAMBIENTALAC := MI_SOBRE_TASAAMBIENTALAC;
  UN_RSUSUARIO.ACSINMEDICION         := MI_ACSINMEDICION;
  UN_RSUSUARIO.CARGOFIJO             := MI_CARGOFIJO;
  UN_RSUSUARIO.VALORBASICO           := MI_VALORBASICO;
  UN_RSUSUARIO.VALORCOMPLEMENTARIO   := MI_VALORCOMPLEMENTARIO;
  UN_RSUSUARIO.VALORSUNTUARIO        := MI_VALORSUNTUARIO;
  UN_RSUSUARIO.SUBFIJO               := MI_SUBFIJO;
  UN_RSUSUARIO.SUBCONSUMOAC          := MI_SUBCONSUMOAC;
  UN_RSUSUARIO.SUBACUEDUCTO          := MI_SUBACUEDUCTO;
  UN_RSUSUARIO.SOBREFIJO             := MI_SOBREFIJO;
  UN_RSUSUARIO.SOBRECONSUMOAC        := MI_SOBRECONSUMOAC;
  UN_RSUSUARIO.SOBREACUEDUCTO        := MI_SOBREACUEDUCTO;
  UN_RSUSUARIO.SOBRESINMEDICION      := MI_SOBRESINMEDICION;
  UN_RSUSUARIO.SUBSINMEDICION        := MI_SUBSINMEDICION;
  UN_RSUSUARIO.TOTALACUEDUCTO        := MI_TOTALACUEDUCTO;

  --Alcantarillado
  UN_RSUSUARIO.CARGOFIJOAL            := MI_CARGOFIJOAL;
  UN_RSUSUARIO.VALORALCBASICO         := MI_VALORALCBASICO;
  UN_RSUSUARIO.VALORALCCOMPLEMENTARIO := MI_VALORALCCOMPLEMENTARIO;
  UN_RSUSUARIO.VALORALCSUNTUARIO      := MI_VALORALCSUNTUARIO;
  UN_RSUSUARIO.SUBFIJOALC             := MI_SUBFIJOALC;
  UN_RSUSUARIO.SUBCONSUMOAL           := MI_SUBCONSUMOAL;
  UN_RSUSUARIO.SOBREFIJOALC           := MI_SOBREFIJOALC;
  UN_RSUSUARIO.SOBRECONSUMOAL         := MI_SOBRECONSUMOAL;
  UN_RSUSUARIO.SUBALCSINMEDICION      := MI_SUBALCSINMEDICION;
  UN_RSUSUARIO.SOBREALCSINMEDICION    := MI_SOBREALCSINMEDICION;
  UN_RSUSUARIO.SUB_TASAAMBIENTALALC   := MI_SUB_TASAAMBIENTALALC;
  UN_RSUSUARIO.SOBRE_TASAAMBIENTALALC := MI_SOBRE_TASAAMBIENTALALC;
  UN_RSUSUARIO.COSTOCONSUMOAL         := MI_COSTOCONSUMOAL;
  UN_RSUSUARIO.ALSINMEDICION          := MI_ALSINMEDICION;
  UN_RSUSUARIO.TOTALALCANTARILLADO    := MI_TOTALALCANTARILLADO;
  UN_RSUSUARIO.SUBALCANTARILLADO      := MI_SUBALCANTARILLADO;
  UN_RSUSUARIO.SOBREALCANTARILLADO    := MI_SOBREALCANTARILLADO;

  --Aseo
  --720
  UN_RSUSUARIO.SUBCCS_720     := MI_SUBCCS_720;
  UN_RSUSUARIO.SOBCCS_720     := MI_SOBCCS_720;
  UN_RSUSUARIO.SUBCBLS_720    := MI_SUBCBLS_720;
  UN_RSUSUARIO.SOBCBLS_720    := MI_SOBCBLS_720;
  UN_RSUSUARIO.SOBCLUS_720    := MI_SOBCLUS_720;
  UN_RSUSUARIO.SUBCLUS_720    := MI_SUBCLUS_720;
  UN_RSUSUARIO.SUBCRT_720     := MI_SUBCRT_720;
  UN_RSUSUARIO.SOBCRT_720     := MI_SOBCRT_720;
  UN_RSUSUARIO.SUBCDF_720     := MI_SUBCDF_720;
  UN_RSUSUARIO.SOBCDF_720     := MI_SOBCDF_720;
  UN_RSUSUARIO.SUBCTL_720     := MI_SUBCTL_720;
  UN_RSUSUARIO.SOBCTL_720     := MI_SOBCTL_720;
  UN_RSUSUARIO.SUBVBA_720     := MI_SUBVBA_720;
  UN_RSUSUARIO.SOBVBA_720     := MI_SOBVBA_720;
  UN_RSUSUARIO.SUBASEO        := MI_SUBASEO;
  UN_RSUSUARIO.SOBREASEO      := MI_SOBREASEO;

  UN_RSUSUARIO.SUBBARR_LIM    := MI_SUBBARR_LIM;
  UN_RSUSUARIO.SOBBARR_LIM    := MI_SOBBARR_LIM;
  UN_RSUSUARIO.SUBDISP_FINAL  := MI_SUBDISP_FINAL;
  UN_RSUSUARIO.SOBDISP_FINAL  := MI_SOBDISP_FINAL;
  UN_RSUSUARIO.SUBMANEJO_REC  := MI_SUBMANEJO_REC;
  UN_RSUSUARIO.SOBMANEJO_REC  := MI_SOBMANEJO_REC;
  UN_RSUSUARIO.SUBRECOLECCION := MI_SUBRECOLECCION;
  UN_RSUSUARIO.SOBRECOLECCION := MI_SOBRECOLECCION;
  UN_RSUSUARIO.SUBTRAMO       := MI_SUBTRAMO;
  UN_RSUSUARIO.SOBTRAMO       := MI_SOBTRAMO;
  UN_RSUSUARIO.SUBDESHABITADO := MI_SUBDESHABITADO;
  UN_RSUSUARIO.SOBDESHABITADO := MI_SOBDESHABITADO;
  UN_RSUSUARIO.COSTOREALASEO  := MI_COSTOREALASEO;
  UN_RSUSUARIO.TOTFACTURAASEO := MI_TOTFACTURAASEO;

  UN_RSUSUARIO.VASEOUNICO     := MI_VASEOUNICO;
  UN_RSUSUARIO.VASEODOMICILIARIO := MI_VASEODOMICILIARIO;
  UN_RSUSUARIO.VASEOBARRIDO   := MI_VASEOBARRIDO;
  UN_RSUSUARIO.VASEOCONSUMO   := MI_VASEOCONSUMO;
  UN_RSUSUARIO.VTRAMOEXCEDENTE := MI_VTRAMOEXCEDENTE;
  UN_RSUSUARIO.VDESHABITADO   := MI_VDESHABITADO;

  UN_RSUSUARIO.VALASEOCCS_720 := MI_VALASEOCCS_720;
  UN_RSUSUARIO.VALASEOCBLS_720 := MI_VALASEOCBLS_720;
  UN_RSUSUARIO.VALASEOCLUS_720 := MI_VALASEOCLUS_720;
  UN_RSUSUARIO.VALASEOCRT_720 := MI_VALASEOCRT_720;
  UN_RSUSUARIO.VALASEOCDF_720 := MI_VALASEOCDF_720;
  UN_RSUSUARIO.VALASEOCTL_720 := MI_VALASEOCTL_720;
  UN_RSUSUARIO.VALASEOVBA_720 := MI_VALASEOVBA_720;

  UN_RSUSUARIO.TOTALASEO      := MI_TOTALASEO;

  --Elimina los registros del type.
  MI_FACTURADOMULTI.DELETE;
  MI_TOTALMTL := 0;
  FOR i IN UN_FACTURADOACT.FIRST .. UN_FACTURADOACT.LAST
  LOOP
     IF UN_FACTURADOACT.EXISTS(i) AND i IN(1,2,5,19,43,4,24,33,45,3,20,21,22,46,48,201,202,203,204,205,206,207) THEN
       MI_TOTALMTL := MI_TOTALMTL + UN_FACTURADOACT(i).FACTURADO;
     END IF;
  END LOOP;

  RETURN NVL(MI_TOTALMTL,0);

  END FC_CALCULOMULTIUSUARIOS;

--15
  FUNCTION FC_CALCULOFINANCIABLES
    /*
       @NAME:    obtenerCalculoFinanciables
       @METHOD:  GET
     */
  (
  UN_COMPANIA            IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_INTCICLO            IN      PCK_SUBTIPOS.TI_CICLO
  ,UN_CODIGORUTA          IN      SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ANIO                IN      SP_USUARIO.ANO%TYPE
  ,UN_PERIODO             IN      SP_USUARIO.PERIODO%TYPE
  ,UN_FACTURADOACT        IN OUT  TI_FACTURADO
  ,UN_PARAMETROS          IN      TI_PARAMETRO
  ,UN_CONCEPTOS           IN      TI_CONCEPTO
  ,UN_NUMEROFINAN         IN OUT  PCK_SUBTIPOS.TI_ENTERO_LARGO
  ) RETURN NUMBER
  AS

  MI_TOTALFINANCIABLES     NUMBER(20,0)                        DEFAULT 0;
  MI_VALORIVA              SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;
  MI_RSFINANCIABLE         SYS_REFCURSOR;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

  UN_FACTURADOACT(30).FACTURADO := 0;
  UN_NUMEROFINAN := 0;
  <<FINANCIABLES>>
  FOR MI_RSFINANCIABLE IN
   (
      SELECT CODIGORUTA,CONCEPTO,SALDOFINANCIABLE,VALORCUOTA,NROCUOTA,NUMEROCUOTAS,BLOQUEADO,MONTOFINANCIAR
      FROM   SP_FINANCIABLES
      WHERE  COMPANIA    = UN_COMPANIA
        AND  CICLO       = UN_INTCICLO
        AND  CODIGORUTA  = UN_CODIGORUTA
        AND  ANO         = UN_ANIO
        AND  PERIODO     = UN_PERIODO
      ORDER  BY CICLO, CODIGORUTA, CONCEPTO,ANO,PERIODO
   )
  LOOP
    BEGIN
       BEGIN
          IF MI_RSFINANCIABLE.BLOQUEADO =0 THEN
              IF MI_RSFINANCIABLE.SALDOFINANCIABLE >0 THEN
                  UN_NUMEROFINAN := UN_NUMEROFINAN + 1;
                  IF MI_RSFINANCIABLE.SALDOFINANCIABLE > MI_RSFINANCIABLE.VALORCUOTA THEN
                      UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSFINANCIABLE.VALORCUOTA);
                  ELSE
                      UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSFINANCIABLE.SALDOFINANCIABLE);
                  END IF;

                  IF MI_RSFINANCIABLE.NROCUOTA < TO_NUMBER(UN_PARAMETROS('INTCUOTASNOINTERESMEDIDOR').VALOR) AND MI_RSFINANCIABLE.CONCEPTO = 7 THEN
                      UN_FACTURADOACT(57).FACTURADO := 0;
                  END IF;

                  IF MI_RSFINANCIABLE.NUMEROCUOTAS > 1 AND MI_RSFINANCIABLE.CONCEPTO < 200 THEN
                      --Se calcula concepto pero no se guarda en la consulta de facturación
                      UN_FACTURADOACT(50 + MI_RSFINANCIABLE.CONCEPTO).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSFINANCIABLE.SALDOFINANCIABLE *
                                                                                    UN_CONCEPTOS(MI_RSFINANCIABLE.CONCEPTO).INTERES / 100, 0);

                      UN_FACTURADOACT(30).FACTURADO := UN_FACTURADOACT(30).FACTURADO + UN_FACTURADOACT(50 + MI_RSFINANCIABLE.CONCEPTO).FACTURADO;
                  END IF;

                  IF UN_PARAMETROS('IVA1CUOTA').VALOR ='SI' THEN
                      IF MI_RSFINANCIABLE.CONCEPTO <> 12 AND ((MI_RSFINANCIABLE.NROCUOTA = 1 AND MI_RSFINANCIABLE.SALDOFINANCIABLE = MI_RSFINANCIABLE.MONTOFINANCIAR) Or MI_RSFINANCIABLE.NROCUOTA = 0) THEN
                          UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO + 150).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(TRUNC(MI_RSFINANCIABLE.MONTOFINANCIAR *
                                                                              UN_CONCEPTOS(MI_RSFINANCIABLE.CONCEPTO).IVA) / 100, 2);

                          MI_VALORIVA := MI_VALORIVA + UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO + 150).FACTURADO;
                      END IF;
                  END IF;
                  MI_TOTALFINANCIABLES := MI_TOTALFINANCIABLES + UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO).FACTURADO;
              END IF;
          ELSE
              IF UN_FACTURADOACT.EXISTS(MI_RSFINANCIABLE.CONCEPTO) THEN
                  UN_FACTURADOACT(MI_RSFINANCIABLE.CONCEPTO).FACTURADO :=0;
              END IF;
          END IF;

          EXCEPTION WHEN OTHERS THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END;

       EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR := MI_RSFINANCIABLE.CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'CONCEPTO';
          MI_MSGERROR(2).VALOR := MI_RSFINANCIABLE.CONCEPTO;
          MI_MSGERROR(3).CLAVE := 'VLRCUOTA';
          MI_MSGERROR(3).VALOR := MI_RSFINANCIABLE.VALORCUOTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_CALCFINANCIABLES ,
                                     UN_TABLAERROR => 'SP_USUARIO',
                                     UN_REEMPLAZOS => MI_MSGERROR);
    END;
  END LOOP FINANCIABLES;

  IF MI_VALORIVA <>0 THEN
       UN_FACTURADOACT(47).FACTURADO := MI_VALORIVA;
       --El iva se cálcula en la primera cuota del financiable o en la sumatoria de los recargos.
  END IF;

  RETURN MI_TOTALFINANCIABLES;

  END FC_CALCULOFINANCIABLES;

--16
  FUNCTION FC_CALCULORECARGOS
      /*
       @NAME:    obtenerCalculoRecargos
       @METHOD:  GET
     */
  (
  UN_COMPANIA            IN      PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO           IN OUT  TUSUARIO
  ,UN_FACTURADOACT        IN OUT  TI_FACTURADO
  ,UN_PARAMETROS          IN      TI_PARAMETRO
  ,UN_CONCEPTOS           IN      TI_CONCEPTO
  ,UN_RSTARIFA            IN      TTARIFAS
  ) RETURN NUMBER
  AS

  MI_TOTRECARGOS           NUMBER(20,0)                        DEFAULT 0;
  MI_NUMCONCEPTOS          SP_CONCEPTOS.CODIGO%TYPE            DEFAULT 0;
  MI_PORRECARGO            SP_CONCEPTOS.RECARGO%TYPE           DEFAULT 0;

  MI_DBLRECARGOS           SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;
  MI_DBLRECARGOSASEO       SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;
  MI_DBLRECARGOSALC        SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;
  MI_DBLRECARGOSALUMBRADO  SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;
  MI_VALORIVA              SP_FACTURADO.VALOR_FACTURADO%TYPE   DEFAULT 0;


  MI_APLICAUS668           BOOLEAN                        DEFAULT FALSE;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN
    IF NOT (UN_PARAMETROS('BLRECARGOSCREDITO').VALOR ='NO' AND UN_RSUSUARIO.NOTACREDITO <=0) THEN
        --No se utilizan los conceptos del 100 al 150, Solo son base para cálcular los conceptos:
        --16 Recargo Acueducto o General si no maneja recargo pOR servicio.
        --39 Recargo Alcantarillado
        --23 Recargo de Aseo
        --31 Recargo Alumbrado
        --47 Iva
        MI_NUMCONCEPTOS := TO_NUMBER(UN_PARAMETROS('NUMCONCEPTOS').VALOR);
        UN_FACTURADOACT(16).FACTURADO := 0;

        MI_APLICAUS668 := CASE WHEN INSTR(UN_PARAMETROS('TARIFAS668').VALOR,UN_RSTARIFA.USOSUPERSERVICIOS)>0 THEN TRUE ELSE FALSE END;
        FOR i IN 1 .. 51 LOOP
        BEGIN
            BEGIN
                MI_PORRECARGO := UN_CONCEPTOS(i).RECARGO;
                IF UN_PARAMETROS('APLICACN668').VALOR = 'SI' AND MI_APLICAUS668 THEN
                    IF UN_CONCEPTOS(i).RECARGO >0 THEN
                        MI_PORRECARGO := UN_RSTARIFA.POR_RECARGO668;
                    ELSE
                        MI_PORRECARGO := 0;
                    END IF;
                END IF;

                IF i = 51 AND UN_FACTURADOACT.EXISTS(MI_NUMCONCEPTOS - 1) THEN
                    UN_FACTURADOACT(i + 100).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(TRUNC(UN_FACTURADOACT(MI_NUMCONCEPTOS - 1).DEUDA * MI_PORRECARGO) / 100);
                ELSIF UN_FACTURADOACT.EXISTS(i) THEN
                    UN_FACTURADOACT(i + 100).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND((UN_FACTURADOACT(i).DEUDA* MI_PORRECARGO) / 100);
                    IF UN_PARAMETROS('IVA1CUOTA').VALOR <> 'SI' THEN
                        UN_FACTURADOACT(i + 150).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(TRUNC(UN_FACTURADOACT(i).FACTURADO * UN_CONCEPTOS(i).IVA) / 100);
                        MI_VALORIVA := MI_VALORIVA + UN_FACTURADOACT(i + 150).FACTURADO;
                    END IF;
                END IF;

                IF UN_FACTURADOACT.EXISTS(i) THEN
                    IF (UN_PARAMETROS('BLRECARGOASEOINDEPENDIENTE').VALOR = 'SI') AND (i = 3 OR i = 20 OR i = 21 OR i = 22 OR NVL(UN_CONCEPTOS(i).SERVICIO, '') = '03') THEN
                      MI_DBLRECARGOSASEO := MI_DBLRECARGOSASEO + UN_FACTURADOACT(i + 100).FACTURADO;
                    ELSIF (UN_PARAMETROS('BLRECARGOALUMBRADOINDEPENDIENTE').VALOR = 'SI') AND (i = 18 OR NVL(UN_CONCEPTOS(i).SERVICIO, '') = '04') THEN
                      MI_DBLRECARGOSALUMBRADO := MI_DBLRECARGOSALUMBRADO + UN_FACTURADOACT(i + 100).FACTURADO;
                    ELSIF (UN_PARAMETROS('BLRECARGOALCINDEPENDIENTE').VALOR = 'SI') AND (NVL(UN_CONCEPTOS(i).SERVICIO, '') = '02') THEN
                      MI_DBLRECARGOSALC := MI_DBLRECARGOSALC + UN_FACTURADOACT(i + 100).FACTURADO;
                    ELSE
                      MI_DBLRECARGOS := MI_DBLRECARGOS + UN_FACTURADOACT(i + 100).FACTURADO;
                    END IF;
                END IF;

                EXCEPTION WHEN OTHERS THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                MI_MSGERROR(1).VALOR := i;
                MI_MSGERROR(2).CLAVE := 'USUARIO';
                MI_MSGERROR(2).VALOR := UN_RSUSUARIO.CODIGORUTA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_CALCRECARGOS ,
                                           UN_TABLAERROR => 'SP_USUARIO',
                                           UN_REEMPLAZOS => MI_MSGERROR);

        END;
        END LOOP;

        IF (UN_RSTARIFA.RECARGOFIJOMORA <> 0 AND MI_DBLRECARGOS <> 0) OR (UN_RSTARIFA.RECARGOFIJOMORA <> 0 AND MI_DBLRECARGOS = 0) THEN
            IF UN_PARAMETROS('COBRARVALORPORSERVICIO').VALOR ='SI' THEN
                IF NVL(UN_RSUSUARIO.BANCOPERANTERIOR,'') = '' THEN
                    IF UN_RSUSUARIO.ACUEDUCTO <>0 THEN
                        UN_FACTURADOACT(16).FACTURADO := UN_FACTURADOACT(16).FACTURADO + PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.RECARGOFIJOMORA);
                    END IF;
                    IF UN_RSUSUARIO.ALCANTARILLADO <>0 THEN
                        UN_FACTURADOACT(16).FACTURADO := UN_FACTURADOACT(16).FACTURADO + PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.RECARGOFIJOMORA);
                    END IF;
                    IF UN_RSUSUARIO.ASEO <>0 THEN
                        UN_FACTURADOACT(16).FACTURADO := UN_FACTURADOACT(16).FACTURADO + PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.RECARGOFIJOMORA);
                    END IF;
                END IF;
            ELSE
                UN_FACTURADOACT(16).FACTURADO := PCK_SYSMAN_UTL.FC_ROUND(UN_RSTARIFA.RECARGOFIJOMORA);
            END IF;
        ELSE
            UN_FACTURADOACT(16).FACTURADO := MI_DBLRECARGOS;
        END IF;

        UN_FACTURADOACT(23).FACTURADO := MI_DBLRECARGOSASEO;
        UN_FACTURADOACT(31).FACTURADO := MI_DBLRECARGOSALUMBRADO;

        IF UN_PARAMETROS('BLRECARGOALCINDEPENDIENTE').VALOR ='SI' THEN
            UN_FACTURADOACT(39).FACTURADO := MI_DBLRECARGOSALC;
            UN_RSUSUARIO.RECARGOALC := UN_FACTURADOACT(39).FACTURADO;
        END IF;

        UN_RSUSUARIO.RECARGOACUEDUCTO := UN_FACTURADOACT(16).FACTURADO;
        UN_RSUSUARIO.RECARGOASEO      := UN_FACTURADOACT(23).FACTURADO;
        UN_RSUSUARIO.RECARGOALUMBRADO := UN_FACTURADOACT(31).FACTURADO;

    END IF;

    MI_TOTRECARGOS :=  CASE WHEN UN_FACTURADOACT.EXISTS(16) THEN UN_FACTURADOACT(16).FACTURADO ELSE 0 END +  
                       CASE WHEN UN_FACTURADOACT.EXISTS(23) THEN UN_FACTURADOACT(23).FACTURADO ELSE 0 END + 
                       CASE WHEN UN_FACTURADOACT.EXISTS(31) THEN UN_FACTURADOACT(31).FACTURADO ELSE 0 END +
                       CASE WHEN UN_FACTURADOACT.EXISTS(39) THEN UN_FACTURADOACT(39).FACTURADO ELSE 0 END;

    RETURN MI_TOTRECARGOS;


  END FC_CALCULORECARGOS;

--17
  FUNCTION FC_AJUSTEALPESO
  (
    /*
      NAME              : FC_AJUSTEALPESO --> Se separa de la función de cálculo dado que se llama varias veces
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 21/03/2017
      TIME              : 03:015 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Cálcula el valor del ajuste al peso de una factura.

      PARAMETERS        : UN_VALOR             => Valor total de la factura que se debe realizar ajuste al peso.
                          UN_AJUSTEDECENA      => Factor de ajuste
                          UN_REDONDEOPORENCIMA => Define si se desea redondear por encima del mil mas cercano o no.

      MODIFICATIONS     :

      @NAME:    obtenerAjustePeso
      @METHOD:  POST
    */
   UN_VALOR             IN SP_FACTURADO.VALOR_FACTURADO%TYPE
  ,UN_AJUSTEDECENA      IN NUMBER
  ,UN_REDONDEOPORENCIMA IN PCK_SUBTIPOS.TI_PARAMETRO

  ) RETURN NUMBER
  AS
  MI_VLRAJUSTE    SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  BEGIN
    IF UN_AJUSTEDECENA <> 0 THEN
        IF UN_REDONDEOPORENCIMA ='SI' THEN
            MI_VLRAJUSTE := TRUNC(UN_VALOR / UN_AJUSTEDECENA + 0.999) * UN_AJUSTEDECENA - UN_VALOR;
        ELSE
            MI_VLRAJUSTE := TRUNC(UN_VALOR / UN_AJUSTEDECENA + 0.501) * UN_AJUSTEDECENA - UN_VALOR;
        END IF;
    ELSE
        MI_VLRAJUSTE := 0;
    END IF;
    RETURN MI_VLRAJUSTE;
  END FC_AJUSTEALPESO;

 --18 
  FUNCTION FC_DESCUENTOCONCEPTO
  (
    /*
      NAME              : FC_DESCUENTOCONCEPTO --> En Access FncDescuentoConcepto
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 21/03/2017
      TIME              : 04:29 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Cálculo de descuentos por porcentaje para cada concepto, Devuelve el valor del descuento que se va a facturar

      PARAMETERS        : UN_COMPANIA    => Compañia con la que se está trabajando.
                          UN_INTCICLO    => Ciclo al cual pertenece el usuario.
                          UN_CODIGORUTA  => Codigo del usuario
                          UN_ANIO        => Año del usuarios
                          UN_PERIODO     => Periodo del usuario
                          UN_SOLOELIMINA => Indicador que determina si solo elimina los descuentos.
                          UN_CONCEPTOS   => Type de conceptos los cuales se utilizan para calcular el descuento por concepto.

      MODIFICATIONS     :

      @NAME:    obtenerCalculoDescuentoConcepto
      @METHOD:  POST
    */
   UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_INTCICLO          IN PCK_SUBTIPOS.TI_CICLO
  ,UN_CODIGORUTA        IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ANIO              IN SP_USUARIO.ANO%TYPE
  ,UN_PERIODO           IN SP_USUARIO.PERIODO%TYPE
  ,UN_SOLOELIMINA       IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_CONCEPTOS         IN TI_CONCEPTO
  ,UN_FACTURADOACT      IN TI_FACTURADO

  ) RETURN NUMBER
  AS
  MI_VDESCR                SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_VALCN                 NUMBER(20,2) DEFAULT 0;
  MI_FNCDES                NUMBER(20,2) DEFAULT 0;
  MI_FNCDESR               NUMBER(20,0) DEFAULT 0;

  MI_IULTIMO               SP_FACTURADO.CONCEPTO%TYPE  DEFAULT 0;

  MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME         PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
  MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    BEGIN
        BEGIN
            MI_TABLA := 'SP_USUARIO_DESCU';
            MI_CONDICIONACME := '   COMPANIA        = ''' || UN_COMPANIA || '''
                                AND CICLO           = '|| UN_INTCICLO ||'
                                AND CODIGORUTA      = '''|| UN_CODIGORUTA ||'''
                                AND ANO_INI         = '|| UN_ANIO ||'
                                AND PERIODO_INI     = ''' || UN_PERIODO || '''
                                AND ANOFAC          = '|| UN_ANIO ||'
                                AND PERIODOFAC      = ''' || UN_PERIODO || '''
                                AND PORPORCENTAJE   <> 0  ';

            MI_FILAS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                           UN_ACCION    => 'E',
                                           UN_CONDICION => MI_CONDICIONACME);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'USUARIO';
            MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARDESCCONCEPTO,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_MSGERROR);
    END;

    IF UN_SOLOELIMINA = 0 THEN
        FOR i IN 1 .. 51 LOOP
            IF UN_FACTURADOACT.EXISTS(i) AND i <> 12 AND i <> 17 AND i <> 49 THEN
                IF UN_FACTURADOACT(i).FACTURADO * UN_CONCEPTOS(i).DESCUENTO <> 0 THEN
                  BEGIN
                    BEGIN
                        MI_VALCN   := PCK_SYSMAN_UTL.FC_ROUND(UN_FACTURADOACT(i).FACTURADO * UN_CONCEPTOS(i).DESCUENTO / 100, 2);
                        MI_TABLA   := 'SP_USUARIO_DESCU';
                        MI_CAMPOS  := 'COMPANIA,
                                       CICLO,
                                       CODIGORUTA,
                                       ANO_INI,
                                       PERIODO_INI,
                                       ANOFAC,
                                       PERIODOFAC,
                                       CONCEPTO,
                                       VALOR_INI,
                                       VALOR_FAC,
                                       PORC_DESCUENTO,
                                       BASE,
                                       PORPORCENTAJE ';
                        MI_VALORES  := ' '''|| UN_COMPANIA || '''               ,
                                           '|| UN_INTCICLO ||'                  ,
                                         '''|| UN_CODIGORUTA ||'''              ,
                                           '|| UN_ANIO ||'                      ,
                                         '''|| UN_PERIODO || '''                ,
                                           '|| UN_ANIO ||'                      ,
                                         '''|| UN_PERIODO || '''                ,
                                           '|| i ||'                            ,
                                           '|| MI_VALCN ||'                     ,
                                           '|| MI_VALCN ||'                     ,
                                           '|| UN_CONCEPTOS(i).DESCUENTO ||'    ,
                                           '|| UN_FACTURADOACT(i).FACTURADO ||' ,
                                           '|| -1 ||'  ';

                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                        IF MI_FILAS >0 THEN
                            MI_FNCDES := MI_FNCDES + MI_VALCN;
                            MI_IULTIMO:= i;
                        END IF;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                    END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                        MI_MSGERROR(1).CLAVE := 'USUARIO';
                        MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
                        MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                        MI_MSGERROR(2).VALOR :=  i;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGCONCEPTODESC,
                                                  UN_TABLAERROR => MI_TABLA,
                                                  UN_REEMPLAZOS => MI_MSGERROR);
                  END;
                END IF;
            END IF;
        END LOOP;

        MI_FNCDESR := PCK_SYSMAN_UTL.FC_ROUND(MI_FNCDES);

        IF MI_FNCDES <> MI_FNCDESR THEN
         BEGIN
            BEGIN
                MI_TABLA     :=' SP_USUARIO_DESCU ';

                MI_CAMPOS    :=' VALOR_FAC    = VALOR_FAC - '|| PCK_SYSMAN_UTL.FC_ROUND(MI_FNCDES - MI_FNCDESR, 2) ||'
                                ,VALOR_INI    = VALOR_INI - '|| PCK_SYSMAN_UTL.FC_ROUND(MI_FNCDES - MI_FNCDESR, 2) ||' ';

                MI_CONDICIONACME := '   COMPANIA        = ''' || UN_COMPANIA || '''
                                    AND CICLO           = '|| UN_INTCICLO ||'
                                    AND CODIGORUTA      = '''|| UN_CODIGORUTA ||'''
                                    AND ANO_INI         = '|| UN_ANIO ||'
                                    AND PERIODO_INI     = ''' || UN_PERIODO || '''
                                    AND ANOFAC          = '|| UN_ANIO ||'
                                    AND PERIODOFAC      = ''' || UN_PERIODO || '''
                                    AND CONCEPTO       = '|| MI_IULTIMO ||'  ';

                MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICIONACME);

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR(1).CLAVE := 'USUARIO';
                MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
                MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                MI_MSGERROR(2).VALOR :=  MI_IULTIMO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTCONCEPTODESC ,
                                          UN_TABLAERROR => MI_TABLA,
                                          UN_REEMPLAZOS => MI_MSGERROR);
         END;
        END IF;
    END IF; --Fin si solo elimina

    RETURN MI_FNCDESR * -1;


  END FC_DESCUENTOCONCEPTO;

 --19 
  FUNCTION FC_DESCUENTOCONTROL
  (
    /*
      NAME              : FC_DESCUENTOCONTROL --> En Access FncDescuentoControl
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 22/03/2017
      TIME              : 12:19 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Cálculo de descuentos controlados, Estos conceptos se cargan desde un utilitario en el cual el usuario establece los valores a descontar.

      PARAMETERS        : UN_COMPANIADES    => Compañia con la que se está trabajando.
                          UN_INTCICLODES    => Ciclo al cual pertenece el usuario.
                          UN_CODIGORUTADES  => Codigo del usuario
                          UN_ANIODES        => Año del usuarios
                          UN_PERIODODES     => Periodo del usuario
                          UN_FACTURADOACT   => Type de conceptos facturados sobre los cuales se realizara el descuento.

      MODIFICATIONS     :

      @NAME:    obtenerCalculoDescuentoControlados
      @METHOD:  POST
    */
   UN_COMPANIADES          IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_INTCICLODES          IN PCK_SUBTIPOS.TI_CICLO
  ,UN_CODIGORUTADES        IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ANIODES              IN SP_USUARIO.ANO%TYPE
  ,UN_PERIODODES           IN SP_USUARIO.PERIODO%TYPE
  ,UN_FACTURADOACT         IN TI_FACTURADO
  ,UN_USUARIO              IN SP_USUARIO.CREATED_BY%TYPE :=''

  ) RETURN NUMBER
  AS
  MI_VDESCUENTO            SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_VALCN                 NUMBER(20) DEFAULT 0;
  MI_RSDES                 SYS_REFCURSOR;

  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
      <<DESCUENTO>>
      FOR MI_RSDES IN
      (
          SELECT ANO_INI, PERIODO_INI, CONCEPTO, VALOR_INI VALOR
          FROM   SP_USUARIO_DESCU
          WHERE  COMPANIA      = UN_COMPANIADES
            AND  CICLO         = UN_INTCICLODES
            AND  CODIGORUTA    = UN_CODIGORUTADES
            AND  ANOFAC        = UN_ANIODES
            AND  PERIODOFAC    = UN_PERIODODES
            AND  PORPORCENTAJE = 0
      )
      LOOP
          IF UN_FACTURADOACT.EXISTS(MI_RSDES.CONCEPTO) THEN   --Se asegura que el concepto exista para hacerle el correspondiente descuento
              IF (UN_FACTURADOACT(MI_RSDES.CONCEPTO).FACTURADO + UN_FACTURADOACT(MI_RSDES.CONCEPTO).DEUDA) > MI_RSDES.VALOR THEN
                  MI_VALCN := MI_RSDES.VALOR;
              ELSE
                  MI_VALCN := (UN_FACTURADOACT(MI_RSDES.CONCEPTO).FACTURADO + UN_FACTURADOACT(MI_RSDES.CONCEPTO).DEUDA);
              END IF;

              MI_VDESCUENTO := MI_VDESCUENTO + MI_VALCN;

              BEGIN
                  BEGIN
                      MI_TABLA  := 'SP_USUARIO_DESCU';
                      MI_CAMPOS :=' VALOR_FAC         = '|| MI_VALCN ||'
                                   ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                                   ,DATE_MODIFIED     = SYSDATE  ';

                      MI_CONDICIONACME :='     COMPANIA      = '''|| UN_COMPANIADES ||'''
                                          AND  CICLO         =   '|| UN_INTCICLODES ||'
                                          AND  CODIGORUTA    = '''|| UN_CODIGORUTADES ||'''
                                          AND  ANOFAC        =   '|| UN_ANIODES ||'
                                          AND  PERIODOFAC    = '''|| UN_PERIODODES ||'''
                                          AND  PORPORCENTAJE = 0
                                          AND  ANO_INI       =   '|| MI_RSDES.ANO_INI ||'
                                          AND PERIODO_INI    = '''|| MI_RSDES.PERIODO_INI ||'''
                                          AND CONCEPTO       =   '|| MI_RSDES.CONCEPTO ||' ';

                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(1).CLAVE := 'USUARIO';
                      MI_MSGERROR(1).VALOR := UN_CODIGORUTADES;
                      MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                      MI_MSGERROR(2).VALOR := MI_RSDES.CONCEPTO;
                      MI_MSGERROR(3).CLAVE := 'VALOR';
                      MI_MSGERROR(3).VALOR := MI_VALCN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTDESCCONTROL ,
                                                 UN_TABLAERROR => MI_TABLA,
                                                 UN_REEMPLAZOS => MI_MSGERROR);
              END;

          END IF;
      END LOOP DESCUENTO;

      RETURN PCK_SYSMAN_UTL.FC_ROUND(MI_VDESCUENTO) * -1;

  END FC_DESCUENTOCONTROL;

 --20 
  FUNCTION FC_DESCUENTOCONTROLTOTFACTURA
  (
   /*
     NAME              : FC_DESCUENTOCONTROLTOTFACTURA --> En Access FncDescuentoControlTotFactura
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 22/03/2017
     TIME              : 03:08 PM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Cálculo de descuentos controlados, Este se aplica al total de la factura y no a un solo concepto.

     PARAMETERS        : UN_COMPANIADEST    => Compañia con la que se está trabajando.
                         UN_INTCICLODEST    => Ciclo al cual pertenece el usuario.
                         UN_CODIGORUTADEST  => Codigo del usuario
                         UN_ANIODEST        => Año del usuarios
                         UN_ANIODEST     => Periodo del usuario
                         UN_FACTURADOACT   => Type de conceptos facturados sobre los cuales se realizara el descuento.

     MODIFICATIONS     :

     @NAME:    obtenerDescuentoTotalFactura
     @METHOD:  GET
   */
   UN_COMPANIADEST          IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_INTCICLODEST          IN PCK_SUBTIPOS.TI_CICLO
  ,UN_CODIGORUTADEST        IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ANIODEST              IN SP_USUARIO.ANO%TYPE
  ,UN_PERIODODEST           IN SP_USUARIO.PERIODO%TYPE
  ,UN_FACTURADOACTT         IN TI_FACTURADO
  ,UN_TOTALFACTURA          IN SP_USUARIO.TOTFACTURAPERACTUAL%TYPE
  ,UN_USUARIO               IN SP_USUARIO.CREATED_BY%TYPE :=''

  ) RETURN NUMBER
  AS
  MI_VDESCUENTO            SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_VALCN                 NUMBER(20) DEFAULT 0;
  MI_RSDES                 SYS_REFCURSOR;

  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
     <<DESCUENTOFACTURA>>
     FOR MI_RSDES IN
     (
         SELECT ANO_INI, PERIODO_INI, CONCEPTO, VALOR_INI VALOR
         FROM   SP_USUARIO_DESCU
         WHERE  COMPANIA      = UN_COMPANIADEST
           AND  CICLO         = UN_INTCICLODEST
           AND  CODIGORUTA    = UN_CODIGORUTADEST
           AND  ANOFAC        = UN_ANIODEST
           AND  PERIODOFAC    = UN_ANIODEST
           AND  PORPORCENTAJE = 0
     )
     LOOP
         IF UN_TOTALFACTURA > MI_RSDES.VALOR THEN
             MI_VALCN := MI_RSDES.VALOR;
         ELSE
             MI_VALCN := UN_TOTALFACTURA;
         END IF;

         MI_VDESCUENTO := MI_VDESCUENTO + MI_VALCN;

         BEGIN
             BEGIN
                 MI_TABLA  := 'SP_USUARIO_DESCU';
                 MI_CAMPOS :=' VALOR_FAC              = '|| MI_VALCN ||'
                              ,MODIFIED_BY            = '''|| UN_USUARIO ||'''
                              ,DATE_MODIFIED          = SYSDATE   ';

                 MI_CONDICIONACME :='     COMPANIA      = '''|| UN_COMPANIADEST ||'''
                                     AND  CICLO         =   '|| UN_INTCICLODEST ||'
                                     AND  CODIGORUTA    = '''|| UN_CODIGORUTADEST ||'''
                                     AND  ANOFAC        =   '|| UN_ANIODEST ||'
                                     AND  PERIODOFAC    = '''|| UN_ANIODEST ||'''
                                     AND  PORPORCENTAJE = 0
                                     AND  ANO_INI       =   '|| MI_RSDES.ANO_INI ||'
                                     AND PERIODO_INI    = '''|| MI_RSDES.PERIODO_INI ||'''
                                     AND CONCEPTO       =   '|| MI_RSDES.CONCEPTO ||' ';

                 MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICIONACME);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
             END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                 MI_MSGERROR(1).CLAVE := 'USUARIO';
                 MI_MSGERROR(1).VALOR := UN_CODIGORUTADEST;
                 MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                 MI_MSGERROR(2).VALOR := MI_RSDES.CONCEPTO;
                 MI_MSGERROR(3).CLAVE := 'VALOR';
                 MI_MSGERROR(3).VALOR := MI_VALCN;

                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTDESCCONTROL ,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_MSGERROR);
         END;

     END LOOP DESCUENTOFACTURA;

     RETURN PCK_SYSMAN_UTL.FC_ROUND(MI_VDESCUENTO) * -1;

  END FC_DESCUENTOCONTROLTOTFACTURA;

--21
  FUNCTION FC_CALCULOPRODUCTIVIDAD
  (
      /*
        NAME              : FC_CALCULOPRODUCTIVIDAD --> En Access CalculaProductividad
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 23/03/2017
        TIME              : 04:15 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Cálculo del valor del descuento por productividad

        PARAMETERS        : UN_COMPANIA    => Compañia con la que se está trabajando.
                            UN_STRUSUARIO  => Codigo de ruta del usuario a calcular el descuento por productividad
                            UN_INTCICLO    => Ciclo al que pertenece el usuario.
                            UN_INTANO      => Año de facturación del usuario.
                            UN_STRPERIODO  => Periodo de facturación del usuario.
                            UN_TOTASEO     => Valor total del aseo facturado.
        MODIFICATIONS     :

        @NAME:    obtenerCalculoDescuentoConcepto
        @METHOD:  POST
      */
   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_STRUSUARIO       IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_INTCICLO         IN PCK_SUBTIPOS.TI_CICLO
  ,UN_INTANO           IN SP_USUARIO.ANO%TYPE
  ,UN_STRPERIODO       IN SP_USUARIO.PERIODO%TYPE
  ,UN_TOTASEO          IN SP_FACTURADO.VALOR_FACTURADO%TYPE
  ,UN_USUARIO          IN SP_USUARIO.CREATED_BY%TYPE :=''

  ) RETURN NUMBER
  AS
   MI_VALOR        SP_FACTURADO.VALOR_FACTURADO%TYPE      DEFAULT 0;
   MI_DBLDISTR     SP_USUARIO_PRODUC.ASEOBARRIDO_INI%TYPE DEFAULT 0;
   MI_DBLDISTRTOT  SP_USUARIO_PRODUC.ASEOBARRIDO_INI%TYPE DEFAULT 0;
   MI_DBLSALDO     SP_USUARIO_PRODUC.ASEOBARRIDO_INI%TYPE DEFAULT 0;
   MI_RSPROD       SYS_REFCURSOR;

   MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
   MI_CONDICIONACME         PCK_SUBTIPOS.TI_CONDICION;
   MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
   MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN
      IF UN_TOTASEO <>0 THEN
          <<PRODUCTIVIDAD>>
          FOR MI_RSPROD IN
          (
              SELECT ASEOUNICO_INI,ASEODOMICILIARIO_INI,ASEOBARRIDO_INI,ASEOCONSUMO_INI,
                     ASEOUNICO,ASEODOMICILIARIO,ASEOBARRIDO,ASEOCONSUMO
              FROM   SP_USUARIO_PRODUC
              WHERE  COMPANIA     = UN_COMPANIA
                AND  CICLO        = UN_INTCICLO
                AND  CODIGORUTA   = UN_STRUSUARIO
                AND  ANOFAC       = UN_INTANO
                AND  PERIODOFAC   = UN_STRPERIODO
          )
          LOOP
              MI_DBLSALDO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEOUNICO_INI + MI_RSPROD.ASEODOMICILIARIO_INI + MI_RSPROD.ASEOBARRIDO_INI +
                                                    MI_RSPROD.ASEOCONSUMO_INI );
              BEGIN
                BEGIN
                  IF UN_TOTASEO < MI_DBLSALDO THEN
                      MI_DBLDISTRTOT :=  (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEOUNICO_INI / MI_DBLSALDO) * UN_TOTASEO)) +
                                         (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEODOMICILIARIO_INI / MI_DBLSALDO) * UN_TOTASEO)) +
                                         (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEOBARRIDO_INI / MI_DBLSALDO) * UN_TOTASEO));


                      MI_TABLA := 'SP_USUARIO_PRODUC';
                      MI_CAMPOS := ' ASEOUNICO        =  '|| (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEOUNICO_INI / MI_DBLSALDO) * UN_TOTASEO)) ||'
                                    ,ASEODOMICILIARIO =  '|| (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEODOMICILIARIO_INI / MI_DBLSALDO) * UN_TOTASEO)) ||'
                                    ,ASEOBARRIDO      =  '|| (PCK_SYSMAN_UTL.FC_ROUND((MI_RSPROD.ASEOBARRIDO_INI / MI_DBLSALDO) * UN_TOTASEO)) ||'
                                    ,ASEOCONSUMO      =  '|| (UN_TOTASEO - MI_DBLDISTRTOT) ||'
                                    ,MODIFIED_BY      = '''|| UN_USUARIO ||'''
                                    ,DATE_MODIFIED    = SYSDATE  ';

                      MI_CONDICIONACME := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                                           AND  CICLO        = '|| UN_INTCICLO ||'
                                           AND  CODIGORUTA   = '''|| UN_STRUSUARIO ||'''
                                           AND  ANOFAC       = '|| UN_INTANO ||'
                                           AND  PERIODOFAC   = '''|| UN_STRPERIODO ||'''  ';

                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);

                      MI_VALOR := UN_TOTASEO * -1;
                  ELSE
                      MI_DBLDISTRTOT := (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEOUNICO_INI)) +
                                        (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEODOMICILIARIO_INI)) +
                                        (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEOBARRIDO_INI));

                      MI_TABLA := 'SP_USUARIO_PRODUC';
                      MI_CAMPOS := ' ASEOUNICO        = '|| (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEOUNICO_INI)) || '
                                    ,ASEODOMICILIARIO = '|| (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEODOMICILIARIO_INI)) || '
                                    ,ASEOBARRIDO      = '|| (PCK_SYSMAN_UTL.FC_ROUND(MI_RSPROD.ASEOBARRIDO_INI)) || '
                                    ,ASEOCONSUMO      = '|| (MI_DBLSALDO - MI_DBLDISTRTOT) || '
                                    ,MODIFIED_BY      = '''|| UN_USUARIO ||'''
                                    ,DATE_MODIFIED    = SYSDATE   ';

                      MI_CONDICIONACME := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                                           AND  CICLO        = '|| UN_INTCICLO ||'
                                           AND  CODIGORUTA   = '''|| UN_STRUSUARIO ||'''
                                           AND  ANOFAC       = '|| UN_INTANO ||'
                                           AND  PERIODOFAC   = '''|| UN_STRPERIODO ||'''  ';

                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);
                      MI_VALOR :=  MI_DBLSALDO * -1;
                  END IF;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(1).CLAVE := 'USUARIO';
                  MI_MSGERROR(1).VALOR :=  UN_STRUSUARIO;
                  MI_MSGERROR(2).CLAVE := 'CICLO';
                  MI_MSGERROR(2).VALOR :=  UN_INTCICLO;
                  MI_MSGERROR(3).CLAVE := 'VALOR';
                  MI_MSGERROR(3).VALOR :=  MI_DBLDISTRTOT;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTCALCPRODUCTIV ,
                                              UN_TABLAERROR => MI_TABLA,
                                              UN_REEMPLAZOS => MI_MSGERROR);
              END;
          END LOOP PRODUCTIVIDAD;
      END IF;
      RETURN MI_VALOR;
  END FC_CALCULOPRODUCTIVIDAD;

--22
  PROCEDURE PR_ACTUALIZAFACTURADOS
  (
     /*
       NAME              : PR_ACTUALIZAFACTURADOS --> En Access ActualizaFacturados
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 23/02/2017
       TIME              : 04:40 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Actualiza los facturados definitivos en la tabla facturado

       PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_RSUSUARIO      => Viene del type principal de usuarios, Datos de usuario para validaciones y actualizaciones
                           UN_FACTURADOACT   => Type de conceptos facturados por usuario.
       MODIFICATIONS     :

       @NAME:    actualizarFacturados
       @METHOD:  POST
     */
   UN_COMPANIA           IN     PCK_SUBTIPOS.TI_COMPANIA
  ,UN_RSUSUARIO          IN     TUSUARIO
  ,UN_FACTURADOACT       IN OUT TI_FACTURADO
  ,UN_PRIMERAVEZ         IN     PCK_SUBTIPOS.TI_LOGICO       DEFAULT 0
  ,UN_PARAMETROS         IN     TI_PARAMETRO
  ,UN_USUARIO            IN     SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS

  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING              PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE             PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE             PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS             PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION               PCK_SUBTIPOS.TI_CONDICION;
  MI_NUMCONCEPTOSAC          SP_CONCEPTOS.CODIGO%TYPE      DEFAULT 0;

  BEGIN
    MI_NUMCONCEPTOSAC := TO_NUMBER(UN_PARAMETROS('NUMCONCEPTOS').VALOR);
    FOR i IN UN_FACTURADOACT.FIRST .. UN_FACTURADOACT.LAST
    LOOP
        BEGIN
            BEGIN
                IF UN_FACTURADOACT.EXISTS(i) AND ((i > 0 AND i <= 50) OR (i >= 201 AND i <= 220) OR (i > 245 AND i < 251)) THEN
                  IF UN_PRIMERAVEZ <>0  THEN
                      UN_FACTURADOACT(i).FACTURADOIN := UN_FACTURADOACT(i).FACTURADO;
                      UN_FACTURADOACT(i).DEUDAIN := UN_FACTURADOACT(i).DEUDA;
                  END IF;

                  IF i <> (MI_NUMCONCEPTOSAC - 3) OR UN_PARAMETROS('RECARGO').VALOR = 'SI' THEN
                    IF UN_FACTURADOACT(i).FACTURADO <>0 THEN
                        MI_TABLA     := ' SP_FACTURADO';
                        MI_MERGEUSING :=  ' SELECT 1 FROM DUAL ' ;

                        MI_MERGEENLACE := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                                            AND CICLO        = '|| UN_RSUSUARIO.CICLO ||'
                                            AND CODIGORUTA   = '''|| UN_RSUSUARIO.CODIGORUTA ||'''
                                            AND ANO          = '|| UN_RSUSUARIO.ANO ||'
                                            AND PERIODO      =  '''|| UN_RSUSUARIO.PERIODO ||'''
                                            AND CONCEPTO     = '|| i ||'   ';

                        MI_MERGEEXISTE := ' UPDATE SET  VALOR_FACTURADO     = '|| NVL(UN_FACTURADOACT(i).FACTURADO,0) ||'
                                                       ,DEUDA               = '|| NVL(UN_FACTURADOACT(i).DEUDA,0) ||'
                                                       ,VALOR_FACTURADOANT  = '|| NVL(UN_FACTURADOACT(i).FACTURADOANT,0) ||'
                                                       ,DEUDAANT            = '|| NVL(UN_FACTURADOACT(i).DEUDAANT,0) ||'
                                                       ,CREDITOABONADO      = '|| 0 ||'
                                                       ,CREATED_BY          = '''|| UN_USUARIO ||'''
                                                       ,DATE_CREATED        = SYSDATE
                                                        ';
                        IF UN_PRIMERAVEZ <> 0 THEN
                            MI_MERGEEXISTE :=  MI_MERGEEXISTE || ' ,VALOR_FACTURADOIN  = '|| NVL(UN_FACTURADOACT(i).FACTURADOIN,0) ||'
                                                                   ,DEUDAIN            = '|| NVL(UN_FACTURADOACT(i).DEUDAIN,0) ||' ';
                        END IF;



                        MI_MERGENOEXIS := 'INSERT (COMPANIA,
                                                   CICLO,
                                                   CODIGORUTA,
                                                   ANO,
                                                   PERIODO,
                                                   CONCEPTO,
                                                   VALOR_FACTURADO,
                                                   DEUDA,
                                                   VALOR_FACTURADOANT,
                                                   DEUDAANT,
                                                   CREDITOABONADO,
                                                   VALOR_FACTURADOIN,
                                                   DEUDAIN,
                                                   CREATED_BY,
                                                   DATE_CREATED)
                                           VALUES (''' || UN_COMPANIA || ''',
                                                     ' || UN_RSUSUARIO.CICLO || ',
                                                   ''' || UN_RSUSUARIO.CODIGORUTA || ''',
                                                     ' || UN_RSUSUARIO.ANO || ',
                                                   ''' || UN_RSUSUARIO.PERIODO || ''',
                                                     ' || i || ',
                                                     ' || NVL(UN_FACTURADOACT(i).FACTURADO,0) || ',
                                                     ' || NVL(UN_FACTURADOACT(i).DEUDA,0) || ',
                                                     ' || NVL(UN_FACTURADOACT(i).FACTURADOANT,0) || ',
                                                     ' || NVL(UN_FACTURADOACT(i).DEUDAANT,0) || ',
                                                     ' || 0 || ',
                                                     ' || CASE WHEN UN_PRIMERAVEZ <>0 THEN NVL(UN_FACTURADOACT(i).FACTURADOIN,0) ELSE 0 END ||',
                                                     ' || CASE WHEN UN_PRIMERAVEZ <>0 THEN NVL(UN_FACTURADOACT(i).DEUDAIN,0) ELSE 0 END ||' ,
                                                     '''|| UN_USUARIO ||''',
                                                     SYSDATE)  ';

                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                      UN_ACCION      => 'IM',
                                                      UN_MERGEUSING  => MI_MERGEUSING,
                                                      UN_MERGEENLACE => MI_MERGEENLACE,
                                                      UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                      UN_MERGENOEXIS => MI_MERGENOEXIS);

                    ELSE --Casos en los que cálculo la primera vez un valor y despues se cambian condiciones y debe actualizar en cero
                        MI_TABLA  := ' SP_FACTURADO';

                        MI_CAMPOS := '   VALOR_FACTURADO     = '|| 0 ||'
                                        ,DEUDA               = '|| NVL(UN_FACTURADOACT(i).DEUDA,0) ||'
                                        ,VALOR_FACTURADOANT  = '|| NVL(UN_FACTURADOACT(i).FACTURADOANT,0) ||'
                                        ,DEUDAANT            = '|| NVL(UN_FACTURADOACT(i).DEUDAANT,0) ||'
                                        ,CREDITOABONADO      = '|| 0 ||'
                                        ,CREATED_BY          = '''|| UN_USUARIO ||'''
                                        ,DATE_CREATED        = SYSDATE';

                        MI_CONDICION := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                                          AND CICLO        = '|| UN_RSUSUARIO.CICLO ||'
                                          AND CODIGORUTA   = '''|| UN_RSUSUARIO.CODIGORUTA ||'''
                                          AND ANO          = '|| UN_RSUSUARIO.ANO ||'
                                          AND PERIODO      =  '''|| UN_RSUSUARIO.PERIODO ||'''
                                          AND CONCEPTO     = '|| i ||'   ';

                        MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION);
                    END IF;
                  END IF;
                END IF;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE OR PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
           MI_MSGERROR(1).CLAVE := 'USUARIO';
           MI_MSGERROR(1).VALOR := UN_RSUSUARIO.CODIGORUTA;
           MI_MSGERROR(2).CLAVE := 'CICLO';
           MI_MSGERROR(2).VALOR := UN_RSUSUARIO.CICLO;
           MI_MSGERROR(3).CLAVE := 'CONCEPTO';
           MI_MSGERROR(3).VALOR := i;
           MI_MSGERROR(4).CLAVE := 'FACTURADO';
           MI_MSGERROR(4).VALOR := UN_FACTURADOACT(i).FACTURADO;

           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                      UN_ERROR_COD => PCK_ERRORES.ERR_ACTUALIZACALCFACTURADO,
                                      UN_REEMPLAZOS => MI_MSGERROR);

        END;
    END LOOP;


  END PR_ACTUALIZAFACTURADOS;

--23 
  FUNCTION FC_MODIFICACIONESFACTURADO(
     /*
       NAME              : FC_MODIFICACIONESFACTURADO --> En Access ActualizaModificacion
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 22/03/2017
       TIME              : 12:19 PM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Cálculo los conceptos al facturado que fueron modificados manualmente, Devuelve el valor total de la factura teniendo en cuenta
                           las modificaciones.
       PARAMETERS        : UN_COMPANIADES    => Compañia con la que se está trabajando.
                           UN_INTCICLODES    => Ciclo al cual pertenece el usuario.
                           UN_CODIGORUTADES  => Codigo del usuario
                           UN_ANIODES        => Año del usuarios
                           UN_PERIODODES     => Periodo del usuario
                           UN_FACTURADOACT   => Type de conceptos facturados sobre los cuales se realizara el descuento.

       MODIFICATIONS     :

       @NAME:    obtenerModificacionesFacturado
       @METHOD:  GET
     */
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA         IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_ANIO               IN SP_USUARIO.ANO%TYPE
  ,UN_PERIODO            IN SP_USUARIO.PERIODO%TYPE
  ,UN_INTCICLO           IN PCK_SUBTIPOS.TI_CICLO

  ) RETURN NUMBER
  AS
  MI_VALORMODIFICA          SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_FILAS                  PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN
     BEGIN
         MI_TABLA := 'SP_FACTURADO';
         --Se adiciona condicion al concepto 250 debido que hay problemas de datos y los profesionales de soporte
         --ingresaron dichos datos para modificar el facturado.
         MI_MERGEUSING := 'SELECT  COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO, CONCEPTO
                                   ,MAX(VRNUE) KEEP (DENSE_RANK LAST ORDER BY FECHA) VALOR
                           FROM    SP_MODIFICACIONESDEUDA
                           WHERE   COMPANIA          = '''|| UN_COMPANIA ||'''
                             AND   CICLO             = '|| UN_INTCICLO ||'
                             AND   ANO               = '|| UN_ANIO ||'
                             AND   PERIODO           = '''|| UN_PERIODO ||'''
                             AND   TIPOMODIFICACION  = ''2''
                             AND   CODIGORUTA        = '''|| UN_CODIGORUTA ||'''
                             AND   CONCEPTO          <> ''250''
                             GROUP BY  COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO, CONCEPTO ';


         MI_MERGEENLACE := '     TABLA.COMPANIA   = VISTA.COMPANIA
                             AND TABLA.CICLO      = VISTA.CICLO
                             AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                             AND TABLA.ANO        = VISTA.ANO
                             AND TABLA.PERIODO    = VISTA.PERIODO
                             AND TABLA.CONCEPTO   = VISTA.CONCEPTO ';

         MI_MERGEEXISTE := 'UPDATE SET TABLA.VALOR_FACTURADO = VISTA.VALOR';


         MI_FILAS := PCK_DATOS.FC_ACME(UN_ACCION      => 'MM',
                                       UN_TABLA       => MI_TABLA,
                                       UN_MERGEUSING  => MI_MERGEUSING,
                                       UN_MERGEENLACE => MI_MERGEENLACE,
                                       UN_MERGEEXISTE => MI_MERGEEXISTE);

         IF MI_FILAS <> 0 THEN
             BEGIN
                 SELECT SUM(VALOR_FACTURADO + DEUDA) VALOR
                 INTO   MI_VALORMODIFICA
                 FROM   SP_FACTURADO
                 WHERE  COMPANIA     = UN_COMPANIA
                   AND  CICLO        = UN_INTCICLO
                   AND  CODIGORUTA   = UN_CODIGORUTA
                   AND  ANO          = UN_ANIO
                   AND  PERIODO      = UN_PERIODO
                   AND  (CONCEPTO BETWEEN 1 AND 49 OR CONCEPTO BETWEEN 201 AND 220 OR CONCEPTO BETWEEN 246 AND 249);

                 EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_VALORMODIFICA := 0;
             END;
             IF MI_VALORMODIFICA = 0 THEN  --Casos en las modificaciones son para todo el facturado, Se envia -1 para que actualice los campos de usuario a 0.
                MI_VALORMODIFICA := -1;
             END IF;
         ELSE
             MI_VALORMODIFICA := 0;
         END IF;

         RETURN MI_VALORMODIFICA;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

     END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
         MI_MSGERROR(1).CLAVE := 'USUARIO';
         MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CALCMODFACTURADO ,
                                    UN_REEMPLAZOS => MI_MSGERROR);

  END FC_MODIFICACIONESFACTURADO;

--24
  PROCEDURE PR_DISTRIBUIRCUOTA12
  (
      /*
        NAME              : PR_DISTRIBUIRCUOTA12 --> En Access DistribuirCuota12Bien
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 27/03/2017
        TIME              : 02:05 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Distribución de los financiables de deuda.

        PARAMETERS        : UN_COMPANIA       => Compañia en la que se esta trabajando.
                            UN_CODIGORUTAIN   => Codigo de ruta inicial del usuario.
                            UN_CODIGORUTAFIN  => Codigo de ruta final del usuario.
                            UN_ANIO           => Año actual de facturación.
                            UN_PERIODO        => Periodo actual.
                            UN_CICLO          => Ciclo de facturacíon.

        MODIFICATIONS     :

        @NAME:    distribuirFinanciableDeuda
        @METHOD:  POST
      */
     UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CODIGORUTA          IN SP_USUARIO.CODIGORUTA%TYPE
    ,UN_ANIO                IN SP_USUARIO.ANO%TYPE
    ,UN_PERIODO             IN SP_USUARIO.PERIODO%TYPE
    ,UN_CICLO               IN PCK_SUBTIPOS.TI_CICLO
    ,UN_USUARIO             IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS

  MI_RS                   SYS_REFCURSOR;
  MI_RSD_DEUDA            SYS_REFCURSOR;
  MI_PERIODOFIN           SP_FINANCIABLESDEDEUDA.PERIODO%TYPE;
  MI_ANOFIN               SP_FINANCIABLESDEDEUDA.ANO%TYPE;
  MI_HAYDATOS             BOOLEAN DEFAULT FALSE;

  MI_TOTALSALDOFINANT     SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINANT%TYPE DEFAULT 0;
  MI_TOTALSALDOFINACT     SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINACT%TYPE DEFAULT 0;
  MI_VALORFIN             SP_FACTURADO.VALORFINACT%TYPE DEFAULT 0;
  MI_VALORFINANT          SP_FACTURADO.VALORFINANT%TYPE DEFAULT 0;
  MI_ACUMDEUDA            SP_FACTURADO.VALORFINACT%TYPE DEFAULT 0;
  MI_DIFERENCIASALDO      SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_DIFERENCIAPAGO       SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_DIFERENCIACUOTA      SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;

  MI_COUNTREGISTO         PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
  MI_CONTADOR             PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
  MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN
      <<FINAN12>>
      FOR MI_RS IN
      (
        SELECT F.COMPANIA, F.CODIGORUTA, F.CONCEPTO, F.VALOR_FACTURADO, F.CICLO, F.ANO, F.PERIODO, FINAN.MONTOFINANCIAR, FINAN.NUMEROCUOTAS,
               FINAN.SALDOFINANCIABLE, FINAN.VALORCUOTA, (FINAN.MONTOFINANCIAR - FINAN.SALDOFINANCIABLE) VALORPAGADO
        FROM   SP_FACTURADO F INNER JOIN SP_FINANCIABLES FINAN
                                ON  (F.CONCEPTO   = FINAN.CONCEPTO)
                            AND (F.PERIODO    = FINAN.PERIODO)
                            AND (F.ANO        = FINAN.ANO)
                            AND (F.CODIGORUTA = FINAN.CODIGORUTA)
                            AND (F.CICLO      = FINAN.CICLO)
                            AND (F.COMPANIA   = FINAN.COMPANIA)
        WHERE  F.COMPANIA   = UN_COMPANIA
          AND  F.CODIGORUTA = UN_CODIGORUTA
          AND  F.CONCEPTO   = 12
          AND  F.VALOR_FACTURADO <> 0
        AND  F.CICLO      = UN_CICLO
        AND  F.ANO        = UN_ANIO
        AND  F.PERIODO    = UN_PERIODO
      )
      LOOP
          MI_ACUMDEUDA :=0;
          MI_VALORFIN :=0;
          MI_DIFERENCIASALDO := 0;
          MI_DIFERENCIAPAGO := 0;
          MI_DIFERENCIACUOTA := 0;
          BEGIN
              --Ultimo periodo financables.
              SELECT MAX(ANO) KEEP (DENSE_RANK LAST ORDER BY ANO || PERIODO) ANIO,
                     MAX(PERIODO) KEEP (DENSE_RANK LAST ORDER BY ANO || PERIODO) PERIODO
              INTO   MI_ANOFIN,
                     MI_PERIODOFIN
              FROM   SP_FINANCIABLESDEDEUDA
              WHERE  COMPANIA = UN_COMPANIA
                AND  USUARIO  = UN_CODIGORUTA
              HAVING MAX(ANO) KEEP (DENSE_RANK LAST ORDER BY ANO || PERIODO) <> NULL ;

              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_ANOFIN := 0;
                  MI_PERIODOFIN := '';
          END;

          --Totales Financiable Act y Ant
          BEGIN
              MI_HAYDATOS := TRUE;
              SELECT SUM(SALDOFINANT) TOTALSALDOFINANT,
                     SUM(SALDOFINACT) TOTALSALDOFINACT,
                     COUNT(CODIGORUTA) COUNTREGISTO
              INTO   MI_TOTALSALDOFINANT,
                     MI_TOTALSALDOFINACT,
                     MI_COUNTREGISTO
              FROM   SP_D_DEUDAFACTURADAFINANCIADA
              WHERE  COMPANIA   = UN_COMPANIA
                AND  CICLO      = UN_CICLO
                AND  CODIGORUTA = MI_RS.CODIGORUTA
                AND  ANO        = MI_ANOFIN
                AND  PERIODO    = MI_PERIODOFIN
              HAVING SUM(SALDOFINANT) <> NULL AND SUM(SALDOFINACT) <> NULL;

              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_TOTALSALDOFINANT := 0;
                  MI_TOTALSALDOFINACT := 0;
                  MI_COUNTREGISTO := 0;
                  MI_HAYDATOS := FALSE;
          END;

          IF MI_HAYDATOS THEN
              <<FINANDEUDA>>
              FOR MI_RSD_DEUDA IN
              (
                SELECT COMPANIA, CICLO, CODIGORUTA, ANO, PERIODO,CONCEPTO, SALDOFINACT, SALDOFINANT
                FROM   SP_D_DEUDAFACTURADAFINANCIADA
                WHERE  COMPANIA   = UN_COMPANIA
                  AND  CICLO      = UN_CICLO
                  AND  CODIGORUTA = MI_RS.CODIGORUTA
                  AND  ANO        = MI_ANOFIN
                  AND  PERIODO    = MI_PERIODOFIN
              )
              LOOP
                  MI_CONTADOR := MI_CONTADOR + 1;
                  BEGIN
                    BEGIN
                      MI_TABLA := 'SP_FACTURADO';
                      MI_CAMPOS := ' VALORFINACT   = '||0 ||'
                                    ,VALORFINANT   = '||0 ||'
                                    ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                    ,DATE_MODIFIED = SYSDATE   ';

                      MI_CONDICIONACME :='    COMPANIA    = ''' || UN_COMPANIA ||'''
                                          AND CICLO       = '|| UN_CICLO ||'
                                          AND CODIGORUTA  = '''|| MI_RS.CODIGORUTA ||'''
                                          AND ANO         ='|| UN_ANIO ||'
                                          AND PERIODO     = '''|| UN_PERIODO || ''' ';

                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICIONACME);

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(1).CLAVE := 'USUARIO';
                      MI_MSGERROR(1).VALOR :=  MI_RS.CODIGORUTA ;

                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTFINANFACTU ,
                                                 UN_TABLAERROR => MI_TABLA,
                                                 UN_REEMPLAZOS => MI_MSGERROR);
                  END;

                  IF MI_RS.VALORPAGADO > MI_TOTALSALDOFINANT THEN
                      IF MI_TOTALSALDOFINACT <> 0 THEN
                          MI_VALORFIN := PCK_SYSMAN_UTL.FC_ROUND((MI_RS.VALOR_FACTURADO * MI_RSD_DEUDA.SALDOFINACT) / MI_TOTALSALDOFINACT);
                      ELSE
                          MI_VALORFIN := 0;
                      END IF;
                      MI_ACUMDEUDA := MI_ACUMDEUDA + MI_VALORFIN;

                      IF MI_CONTADOR = MI_COUNTREGISTO THEN
                         IF MI_RS.VALOR_FACTURADO > MI_ACUMDEUDA THEN
                            MI_VALORFIN := MI_VALORFIN + (MI_RS.VALOR_FACTURADO - MI_ACUMDEUDA);
                         ELSIF MI_RS.VALOR_FACTURADO < MI_ACUMDEUDA THEN
                            IF MI_VALORFIN < 0 THEN
                               MI_VALORFIN := MI_VALORFIN + (MI_ACUMDEUDA - MI_RS.VALOR_FACTURADO) * -1;
                            ELSE
                               MI_VALORFIN := MI_VALORFIN - (MI_ACUMDEUDA - MI_RS.VALOR_FACTURADO);
                            END IF;
                         END IF;
                      END IF;

                      BEGIN
                          BEGIN
                              MI_TABLA       := 'SP_FACTURADO';
                              MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                              MI_MERGEENLACE := '    COMPANIA     = ''' || UN_COMPANIA || '''
                                                 AND CICLO        = '|| UN_CICLO ||'
                                                 AND CODIGORUTA   = '|| MI_RS.CODIGORUTA ||'
                                                 AND ANO          = '|| UN_ANIO ||'
                                                 AND PERIODO      = '''|| UN_PERIODO ||'''
                                                 AND CONCEPTO     = '|| MI_RSD_DEUDA.CONCEPTO ||'  ';

                              MI_MERGEEXISTE := ' UPDATE SET VALORFINACT  = '|| MI_VALORFIN ||'
                                                            ,MODIFIED_BY            = '''|| UN_USUARIO ||'''
                                                            ,DATE_MODIFIED          = SYSDATE   ';

                              MI_MERGENOEXIS := ' INSERT ( COMPANIA, CICLO, CODIGORUTA, ANO, PERIODO, CONCEPTO, VALOR_FACTURADO, DEUDA, VALOR_FACTURADOANT, DEUDAANT,
                                                           VALOR_FACTURADOIN, DEUDAIN, RECAUDADOPERIODO, DOBLEPAGO, SALDOCREDITO, ABONO, CREDITOABONADO, VALORFINACT,
                                                           VALORFINANT, PORFINANCIACION, VALORABONOACT, VALORABONOANT, VALORABONODEUDA, CREATED_BY, DATE_CREATED )
                                                  VALUES ('''|| UN_COMPANIA ||''', '|| UN_CICLO ||', '''|| MI_RS.CODIGORUTA ||''', '|| UN_ANIO ||', '''|| UN_PERIODO ||''',
                                                          '|| MI_RSD_DEUDA.CONCEPTO ||', ' || 0 || ' ,' || 0 || ' , ' || 0 || ' ,' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' ,
                                                           ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , '|| MI_VALORFIN ||' , ' || 0 || ', ' || 0 || ' , ' || 0 || ' ,
                                                           ' || 0 || ' , ' || 0 || ' ,'''|| UN_USUARIO ||''', SYSDATE ) ';

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
                              MI_MSGERROR(1).CLAVE := 'USUARIO';
                              MI_MSGERROR(1).VALOR := MI_RS.CODIGORUTA;
                              MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                              MI_MSGERROR(2).VALOR := MI_RSD_DEUDA.CONCEPTO;
                              MI_MSGERROR(3).CLAVE := 'FINACT';
                              MI_MSGERROR(3).VALOR := MI_VALORFIN;
                              MI_MSGERROR(4).CLAVE := 'FINANT';
                              MI_MSGERROR(4).VALOR := 0;
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                         UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_DISTRIBUCION12,
                                                         UN_REEMPLAZOS => MI_MSGERROR);
                      END;

                  ELSE
                      MI_DIFERENCIASALDO := MI_TOTALSALDOFINANT - MI_RS.VALORPAGADO;
                      IF MI_DIFERENCIASALDO >= MI_RS.VALOR_FACTURADO THEN

                          IF MI_TOTALSALDOFINANT <> 0 THEN
                              MI_VALORFINANT := PCK_SYSMAN_UTL.FC_ROUND((MI_RS.VALOR_FACTURADO *  MI_RSD_DEUDA.SALDOFINANT) / MI_TOTALSALDOFINANT);
                          ELSE
                              MI_VALORFINANT := 0;
                          END IF;
                          MI_ACUMDEUDA := MI_ACUMDEUDA + MI_VALORFINANT;

                          IF MI_CONTADOR = MI_COUNTREGISTO THEN
                              IF MI_RS.VALOR_FACTURADO > MI_ACUMDEUDA THEN
                                  MI_VALORFINANT := MI_VALORFINANT + (MI_RS.VALOR_FACTURADO - MI_ACUMDEUDA);
                              ELSIF MI_RS.VALOR_FACTURADO < MI_ACUMDEUDA THEN
                                  MI_VALORFINANT := MI_VALORFINANT - (MI_ACUMDEUDA - MI_RS.VALOR_FACTURADO);
                              END IF;
                          END IF;

                          BEGIN
                              BEGIN
                                  MI_TABLA       := 'SP_FACTURADO';
                                  MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                                  MI_MERGEENLACE := '    COMPANIA     = ''' || UN_COMPANIA || '''
                                                     AND CICLO        = '|| UN_CICLO ||'
                                                     AND CODIGORUTA   = '|| MI_RS.CODIGORUTA ||'
                                                     AND ANO          = '|| UN_ANIO ||'
                                                     AND PERIODO      = '''|| UN_PERIODO ||'''
                                                     AND CONCEPTO     = '|| MI_RSD_DEUDA.CONCEPTO ||'  ';

                                  MI_MERGEEXISTE := ' UPDATE SET VALORFINANT  = '|| MI_VALORFINANT ||' ';

                                  MI_MERGENOEXIS := ' INSERT ( COMPANIA, CICLO, CODIGORUTA, ANO, PERIODO, CONCEPTO, VALOR_FACTURADO, DEUDA, VALOR_FACTURADOANT, DEUDAANT,
                                                               VALOR_FACTURADOIN, DEUDAIN, RECAUDADOPERIODO, DOBLEPAGO, SALDOCREDITO, ABONO, CREDITOABONADO, VALORFINACT,
                                                               VALORFINANT, PORFINANCIACION, VALORABONOACT, VALORABONOANT, VALORABONODEUDA )
                                                      VALUES ('''|| UN_COMPANIA ||''', '|| UN_CICLO ||', '''|| MI_RS.CODIGORUTA ||''', '|| UN_ANIO ||', '''|| UN_PERIODO ||''',
                                                              '|| MI_RSD_DEUDA.CONCEPTO ||', ' || 0 || ' ,' || 0 || ' , ' || 0 || ' ,' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' ,
                                                               ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , '|| 0 ||' , ' || MI_VALORFINANT || ', ' || 0 || ' , ' || 0 || ' ,
                                                               ' || 0 || ' , ' || 0 || ' ) ';
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                              END;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                                  MI_MSGERROR(1).CLAVE := 'USUARIO';
                                  MI_MSGERROR(1).VALOR := MI_RS.CODIGORUTA;
                                  MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                                  MI_MSGERROR(2).VALOR := MI_RSD_DEUDA.CONCEPTO;
                                  MI_MSGERROR(3).CLAVE := 'FINACT';
                                  MI_MSGERROR(3).VALOR := 0;
                                  MI_MSGERROR(4).CLAVE := 'FINANT';
                                  MI_MSGERROR(4).VALOR := MI_VALORFINANT;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                             UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_DISTRIBUCION12,
                                                             UN_REEMPLAZOS => MI_MSGERROR);
                          END;

                      ELSE
                          MI_DIFERENCIAPAGO := MI_RS.VALOR_FACTURADO - MI_DIFERENCIASALDO;
                          MI_DIFERENCIACUOTA := MI_RS.VALOR_FACTURADO - MI_DIFERENCIAPAGO;

                          IF MI_TOTALSALDOFINANT <> 0 THEN
                              MI_VALORFINANT := PCK_SYSMAN_UTL.FC_ROUND((MI_DIFERENCIACUOTA * MI_RSD_DEUDA.SALDOFINANT) / MI_TOTALSALDOFINANT);
                          ELSE
                              MI_VALORFINANT := 0;
                          END IF;

                          IF MI_TOTALSALDOFINACT <> 0 THEN
                              MI_VALORFIN := PCK_SYSMAN_UTL.FC_ROUND((MI_DIFERENCIAPAGO * MI_RSD_DEUDA.SALDOFINACT) / MI_TOTALSALDOFINACT);
                          ELSE
                              MI_VALORFIN := 0;
                          END IF;
                          MI_ACUMDEUDA := MI_ACUMDEUDA + MI_VALORFIN + MI_VALORFINANT;

                          IF MI_CONTADOR = MI_COUNTREGISTO THEN
                              IF MI_RS.VALOR_FACTURADO > MI_ACUMDEUDA THEN
                                  MI_VALORFIN := MI_VALORFIN + (MI_RS.VALOR_FACTURADO - MI_ACUMDEUDA);
                              ELSIF MI_RS.VALOR_FACTURADO < MI_ACUMDEUDA THEN
                                  IF MI_VALORFIN < 0 THEN
                                      MI_VALORFIN := MI_VALORFIN + (MI_ACUMDEUDA - MI_RS.VALOR_FACTURADO) * -1;
                                  ELSE
                                      MI_VALORFIN := MI_VALORFIN - (MI_ACUMDEUDA - MI_RS.VALOR_FACTURADO);
                                  END IF;
                              END IF;
                          END IF;

                          BEGIN
                              BEGIN
                                  MI_TABLA       := 'SP_FACTURADO';
                                  MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                                  MI_MERGEENLACE := '    COMPANIA     = ''' || UN_COMPANIA || '''
                                                     AND CICLO        = '|| UN_CICLO ||'
                                                     AND CODIGORUTA   = '|| MI_RS.CODIGORUTA ||'
                                                     AND ANO          = '|| UN_ANIO ||'
                                                     AND PERIODO      = '''|| UN_PERIODO ||'''
                                                     AND CONCEPTO     = '|| MI_RSD_DEUDA.CONCEPTO ||'  ';

                                  MI_MERGEEXISTE := ' UPDATE SET VALORFINANT   = '|| MI_VALORFINANT ||'
                                                                ,VALORFINACT   = '|| MI_VALORFIN ||'
                                                                ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                                                ,DATE_MODIFIED = SYSDATE   ';

                                  MI_MERGENOEXIS := ' INSERT ( COMPANIA, CICLO, CODIGORUTA, ANO, PERIODO, CONCEPTO, VALOR_FACTURADO, DEUDA, VALOR_FACTURADOANT, DEUDAANT,
                                                                 VALOR_FACTURADOIN, DEUDAIN, RECAUDADOPERIODO, DOBLEPAGO, SALDOCREDITO, ABONO, CREDITOABONADO, VALORFINACT,
                                                                 VALORFINANT, PORFINANCIACION, VALORABONOACT, VALORABONOANT, VALORABONODEUDA,CREATED_BY , DATE_CREATED)
                                                      VALUES ('''|| UN_COMPANIA ||''', '|| UN_CICLO ||', '''|| MI_RS.CODIGORUTA ||''', '|| UN_ANIO ||', '''|| UN_PERIODO ||''',
                                                                '|| MI_RSD_DEUDA.CONCEPTO ||', ' || 0 || ' ,' || 0 || ' , ' || 0 || ' ,' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' ,
                                                                 ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , ' || 0 || ' , '|| 0 ||' , ' || MI_VALORFINANT || ', ' || 0 || ' , ' || 0 || ' ,
                                                                 ' || 0 || ' , ' || 0 || ','''|| UN_USUARIO ||''',SYSDATE ) ';

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
                                  MI_MSGERROR(1).CLAVE := 'USUARIO';
                                  MI_MSGERROR(1).VALOR := MI_RS.CODIGORUTA;
                                  MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                                  MI_MSGERROR(2).VALOR := MI_RSD_DEUDA.CONCEPTO;
                                  MI_MSGERROR(3).CLAVE := 'FINACT';
                                  MI_MSGERROR(3).VALOR := MI_VALORFIN;
                                  MI_MSGERROR(4).CLAVE := 'FINANT';
                                  MI_MSGERROR(4).VALOR := MI_VALORFINANT;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                             UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_DISTRIBUCION12,
                                                             UN_REEMPLAZOS => MI_MSGERROR);
                          END;
                      END IF;

                  END IF;
              END LOOP FINANDEUDA;



          ELSE
              PR_DISTRIBUIRDEUDA12(UN_COMPANIA     => MI_RS.COMPANIA
                                  ,UN_CODIGORUTA   => MI_RS.CODIGORUTA
                                  ,UN_ANIO         => MI_RS.ANO
                                  ,UN_PERIODO      => MI_RS.PERIODO
                                  ,UN_CICLO        => MI_RS.CICLO
                                  ,UN_USUARIO      => UN_USUARIO);
          END IF; --Fin validacion datos.

      END LOOP FINAN12;
  END PR_DISTRIBUIRCUOTA12;

--25
  PROCEDURE PR_DISTRIBUIRDEUDA12
  (
      /*
        NAME              : PR_DISTRIBUIRCUOTA12 --> En Access DistribuirDeuda12
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 28/03/2017
        TIME              : 11:23 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Distribución del concepto 12.

        PARAMETERS        : UN_COMPANIA       => Compañia en la que se esta trabajando.
                            UN_CODIGORUTAIN   => Codigo de ruta inicial del usuario.
                            UN_CODIGORUTAFIN  => Codigo de ruta final del usuario.
                            UN_ANIO           => Año actual de facturación.
                            UN_PERIODO        => Periodo actual.
                            UN_CICLO          => Ciclo de facturacíon.

        MODIFICATIONS     :

        @NAME:    distribuirDeudaFinanciable12
        @METHOD:  POST
      */
     UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CODIGORUTA          IN SP_USUARIO.CODIGORUTA%TYPE
    ,UN_ANIO                IN SP_USUARIO.ANO%TYPE
    ,UN_PERIODO             IN SP_USUARIO.PERIODO%TYPE
    ,UN_CICLO               IN PCK_SUBTIPOS.TI_CICLO
    ,UN_USUARIO             IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS
  MI_VALORFACTURADORS     SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_RS2                  SYS_REFCURSOR;
  MI_TOTALFAC             SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_TOTALFACIN           SP_FACTURADO.VALOR_FACTURADOIN%TYPE DEFAULT 0;
  MI_DEUDA                SP_FACTURADO.VALORFINACT%TYPE DEFAULT 0;
  MI_VALORFINANT          SP_FACTURADO.VALORFINANT%TYPE DEFAULT 0;
  MI_VALORFINACT          SP_FACTURADO.VALORFINACT%TYPE DEFAULT 0;
  MI_ACUMULADOR           SP_FACTURADO.VALORFINACT%TYPE DEFAULT 0;

  MI_COUNTREGISTO         PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
  MI_CONTADOR             PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;

  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME        PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS                PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;



  BEGIN
    --Se cambia RS por llenar unicamente variable valor facturado
    BEGIN
        SELECT VALOR_FACTURADO
        INTO   MI_VALORFACTURADORS
        FROM   SP_FACTURADO
        WHERE  COMPANIA         = UN_COMPANIA
          AND  CICLO            = UN_CICLO
          AND  CODIGORUTA       = UN_CODIGORUTA
          AND  ANO              = UN_ANIO
          AND  PERIODO          = UN_PERIODO
          AND  CONCEPTO         = 12
          AND  VALOR_FACTURADO <> 0;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VALORFACTURADORS := 0;
    END;

    IF MI_VALORFACTURADORS <>0 THEN
        <<FACTURADORS2>>
        FOR MI_RS2 IN
        (
            SELECT COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,CONCEPTO,VALOR_FACTURADO, VALOR_FACTURADOIN,VALORFINACT,VALORFINANT
            FROM SP_FACTURADO
            WHERE COMPANIA     = UN_COMPANIA
              AND CICLO        = UN_CICLO
              AND CODIGORUTA   = UN_CODIGORUTA
              AND ANO          = UN_ANIO
              AND PERIODO      = UN_PERIODO
              AND ((CONCEPTO BETWEEN 1 AND 48 OR CONCEPTO BETWEEN 201 AND 220 OR CONCEPTO BETWEEN 246 AND 249 ) AND CONCEPTO NOT IN(12,17))
            ORDER BY VALOR_FACTURADO,VALOR_FACTURADOIN
        )
        LOOP
            --Total de la factura
            MI_CONTADOR := MI_CONTADOR + 1;
            BEGIN
                SELECT SUM(VALOR_FACTURADO + DEUDA)  TOTFACTURA,
                       SUM(VALOR_FACTURADOIN + DEUDAIN) TOTFACTURAIN,
                       SUM(VALORFINACT + VALORFINANT ) DEUDAFIN,
                       COUNT(CODIGORUTA) AS CUENTACOD
                INTO   MI_TOTALFAC,
                       MI_TOTALFACIN,
                       MI_DEUDA,
                       MI_COUNTREGISTO
                FROM   SP_FACTURADO
                WHERE  COMPANIA     = UN_COMPANIA
                  AND  CICLO        = UN_CICLO
                  AND  CODIGORUTA   = UN_CODIGORUTA
                  AND  ANO          = UN_ANIO
                  AND  PERIODO      = UN_PERIODO
                  AND  ((CONCEPTO BETWEEN 1 AND 48 OR CONCEPTO BETWEEN 201 AND 220 OR CONCEPTO BETWEEN 246 AND 249 ) AND CONCEPTO NOT IN(12,17))
                HAVING SUM(VALOR_FACTURADO + DEUDA)  <> NULL AND SUM(VALOR_FACTURADOIN + DEUDAIN) <> NULL;

                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TOTALFAC := 0;
                    MI_TOTALFACIN := 0;
                    MI_DEUDA := 0;
            END;

            MI_VALORFINANT := 0;
            MI_VALORFINACT := 0;

            IF MI_DEUDA <> MI_VALORFACTURADORS THEN
                IF MI_TOTALFAC <>0 THEN
                    IF MI_RS2.VALOR_FACTURADO <> 0 THEN
                        MI_VALORFINANT := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORFACTURADORS * MI_RS2.VALOR_FACTURADO) / MI_TOTALFAC);
                        MI_VALORFINACT := 0;
                        MI_ACUMULADOR := MI_ACUMULADOR + MI_VALORFINANT;

                        IF MI_CONTADOR = MI_COUNTREGISTO THEN
                            MI_VALORFINANT := MI_VALORFINANT + MI_VALORFACTURADORS - MI_ACUMULADOR;
                        END IF;
                    ELSE
                        MI_VALORFINANT := 0;
                        MI_VALORFINACT := 0;
                    END IF;

                ELSIF MI_TOTALFACIN <>0 THEN
                    IF MI_RS2.VALOR_FACTURADOIN <> 0 THEN
                        MI_VALORFINACT := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORFACTURADORS * MI_RS2.VALOR_FACTURADOIN) / MI_TOTALFACIN);
                        MI_VALORFINANT := 0;
                        MI_ACUMULADOR := MI_ACUMULADOR + MI_VALORFINACT;

                        IF MI_CONTADOR = MI_COUNTREGISTO THEN
                            MI_VALORFINACT := MI_VALORFINACT + MI_VALORFACTURADORS - MI_ACUMULADOR;
                        END IF;

                    ELSE
                        MI_VALORFINANT := 0;
                        MI_VALORFINACT := 0;
                    END IF;

                END IF;
            END IF;

            BEGIN
                BEGIN
                    MI_TABLA     :=' SP_FACTURADO ';

                    MI_CAMPOS    :=' VALORFINANT   = '|| MI_VALORFINANT ||'
                                    ,VALORFINACT   = '|| MI_VALORFINACT ||'
                                    ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                                    ,DATE_MODIFIED = SYSDATE   ';

                    MI_CONDICIONACME :='   COMPANIA     = '''|| UN_COMPANIA ||'''
                                        AND CICLO        =   '|| MI_RS2.CICLO ||'
                                        AND CODIGORUTA   = '''|| MI_RS2.CODIGORUTA ||'''
                                        AND ANO          =   '|| MI_RS2.ANO ||'
                                        AND PERIODO      = '''|| MI_RS2.PERIODO ||'''
                                        AND CONCEPTO     =   '|| MI_RS2.CONCEPTO ||'  ';

                    MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICIONACME);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                    MI_MSGERROR(1).CLAVE := 'USUARIO';
                    MI_MSGERROR(1).VALOR := MI_RS2.CODIGORUTA;
                    MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                    MI_MSGERROR(2).VALOR := MI_RS2.CONCEPTO;
                    MI_MSGERROR(3).CLAVE := 'FINACT';
                    MI_MSGERROR(3).VALOR := MI_VALORFINACT;
                    MI_MSGERROR(4).CLAVE := 'FINANT';
                    MI_MSGERROR(4).VALOR := MI_VALORFINANT;

                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DISTDEUDA12 ,
                                               UN_TABLAERROR => MI_TABLA,
                                               UN_REEMPLAZOS => MI_MSGERROR);
            END;




        END LOOP FACTURADORS2;


    END IF;  --Fin validación si hay datos.


  END PR_DISTRIBUIRDEUDA12;

--26
  FUNCTION FC_DISTRIBUIRSALDOSCREDITO
  (
      /*
        NAME              : FC_DISTRIBUIRSALDOSCREDITO --> En Access DistribuirSaldosCredito
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 28/03/2017
        TIME              : 03:47 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Realiza la distribución del saldo credito del usuario, crea el valor del concepto 17 y lo inserta en la tabla facturado.


        PARAMETERS        : UN_COMPANIA          => Compañia en la que se esta trabajando.
                            UN_CICLO             => Ciclo que se va a calcular.
                            UN_CODIGORUTA        => Codigo de ruta del usuario a calcular.
                            UN_ANO               => Año de facturación del usuario
                            UN_PERIODO           => Periodo de facturación del usuario
                            UN_NOTACREDITO       => Valor del saldo credito del usuario
                            UN_FACTURADO         => Indicador que se usa para evaluar si se actualiza el facturado inicial del usaurio.

        MODIFICATIONS     :

        @NAME:    calcularFacturacion
        @METHOD:  POST
      */
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO
   ,UN_CODIGORUTA         IN SP_USUARIO.CODIGORUTA%TYPE
   ,UN_ANO                IN SP_USUARIO.ANO%TYPE
   ,UN_PERIODO            IN SP_USUARIO.PERIODO%TYPE
   ,UN_NOTACREDITO        IN SP_USUARIO.NOTACREDITO%TYPE
   ,UN_FACTURADO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
   ,UN_USUARIO            IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  RETURN NUMBER

  AS

  MI_RSCREDITO               SYS_REFCURSOR;
  MI_DBLVALORACUMULADO       SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_VALORSALDOCREDITO       SP_FACTURADO.VALOR_FACTURADO%TYPE DEFAULT 0;
  MI_TOTALFACTURADO          SP_USUARIO.TOTFACTURAPERACTUAL%TYPE DEFAULT 0;
  MI_VALSALDOCREDITO         SP_FACTURADO.SALDOCREDITO%TYPE DEFAULT 0;
  MI_VALCREDITOABONADO       SP_FACTURADO.CREDITOABONADO%TYPE DEFAULT 0;

  MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
  MI_MERGEUSING              PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE             PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE             PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS             PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN

      BEGIN
          BEGIN
              MI_TABLA := 'SP_FACTURADO';
              MI_CAMPOS    := ' CREDITOABONADO  = '|| 0 ||'
                               ,SALDOCREDITO    = '|| 0 ||'
                               ,MODIFIED_BY     = '''|| UN_USUARIO ||'''
                               ,DATE_MODIFIED   = SYSDATE  ';

              MI_CONDICIONACME := '   COMPANIA   = '''|| UN_COMPANIA || '''
                                  AND CICLO      = '|| UN_CICLO ||'
                                  AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                  AND ANO        = '|| UN_ANO ||'
                                  AND PERIODO    = '''|| UN_PERIODO ||'''  ';

              MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICIONACME);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'USUARIO';
              MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INISALDOCREDITO,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_REEMPLAZOS => MI_MSGERROR);
      END;

      <<SALDOCREDITO>>
      FOR MI_RSCREDITO IN
      (
          SELECT * FROM
          (
              SELECT F.COMPANIA, F.CICLO, F.CODIGORUTA, F.ANO, F.PERIODO, F.CONCEPTO, F.VALOR_FACTURADO, F.DEUDA,
                     (F.VALOR_FACTURADO + F.DEUDA + F.VALORFINACT + F.VALORFINANT - F.VALORABONOACT - F.VALORABONOANT) TOTFACT,
                     C.ORDENABONAR, U.TOTFACTURAPERACTUAL, 1 ORDEN
              FROM   SP_FACTURADO F INNER JOIN SP_USUARIO U ON
                              F.COMPANIA   = U.COMPANIA AND
                            F.CICLO	     = U.CICLO    AND
                            F.CODIGORUTA = U.CODIGORUTA AND
                            F.ANO		 = U.ANO AND
                            F.PERIODO	 = U.PERIODO
                            INNER JOIN SP_CONCEPTOS C ON
                                      F.COMPANIA	 = C.COMPANIA AND
                          F.CONCEPTO	 = C.CODIGO
              WHERE  F.COMPANIA   = UN_COMPANIA
                AND  F.CICLO      = UN_CICLO
                AND  F.CODIGORUTA = UN_CODIGORUTA
                AND  F.ANO        = UN_ANO
                AND  F.PERIODO    = UN_PERIODO
                AND  ((F.CONCEPTO BETWEEN 1 AND 48 OR (F.CONCEPTO) BETWEEN 201 AND 220 OR F.CONCEPTO IN (246,247,248,249)) AND F.CONCEPTO NOT IN (12,17))
                AND  ((F.VALOR_FACTURADO + F.DEUDA + F.VALORFINACT + F.VALORFINANT - F.VALORABONOACT -F.VALORABONOANT)<0)

              UNION ALL

              SELECT F.COMPANIA, F.CICLO, F.CODIGORUTA, F.ANO, F.PERIODO, F.CONCEPTO, F.VALOR_FACTURADO, F.DEUDA,
                     (F.VALOR_FACTURADO + F.DEUDA + F.VALORFINACT + F.VALORFINANT - F.VALORABONOACT - F.VALORABONOANT) TOTFACT,
                     C.ORDENABONAR, U.TOTFACTURAPERACTUAL, 2 ORDEN
              FROM   SP_FACTURADO F INNER JOIN SP_USUARIO U ON
                              F.COMPANIA   = U.COMPANIA AND
                            F.CICLO	     = U.CICLO    AND
                            F.CODIGORUTA = U.CODIGORUTA AND
                            F.ANO		 = U.ANO AND
                            F.PERIODO	 = U.PERIODO
                            INNER JOIN SP_CONCEPTOS C ON
                                      F.COMPANIA	 = C.COMPANIA AND
                          F.CONCEPTO	 = C.CODIGO
              WHERE  F.COMPANIA   = UN_COMPANIA
                AND  F.CICLO      = UN_CICLO
                AND  F.CODIGORUTA = UN_CODIGORUTA
                AND  F.ANO        = UN_ANO
                AND  F.PERIODO    = UN_PERIODO
                AND  ((F.CONCEPTO BETWEEN 1 AND 48 OR (F.CONCEPTO) BETWEEN 201 AND 220 OR F.CONCEPTO IN (246,247,248,249)) AND F.CONCEPTO NOT IN (12,17))
                AND  ((F.VALOR_FACTURADO + F.DEUDA + F.VALORFINACT + F.VALORFINANT - F.VALORABONOACT -F.VALORABONOANT)>0)
              ORDER BY ORDENABONAR
          )
          ORDER BY ORDEN

      )
      LOOP
          MI_TOTALFACTURADO := MI_RSCREDITO.TOTFACTURAPERACTUAL;
          IF (UN_NOTACREDITO - MI_DBLVALORACUMULADO) < MI_RSCREDITO.TOTFACT THEN
              MI_VALCREDITOABONADO := PCK_SYSMAN_UTL.FC_ROUND(UN_NOTACREDITO - MI_DBLVALORACUMULADO);
              MI_VALSALDOCREDITO := PCK_SYSMAN_UTL.FC_ROUND(UN_NOTACREDITO - MI_DBLVALORACUMULADO);
              MI_DBLVALORACUMULADO := UN_NOTACREDITO;
          ELSE
              MI_VALCREDITOABONADO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSCREDITO.TOTFACT);
              MI_VALSALDOCREDITO := PCK_SYSMAN_UTL.FC_ROUND(MI_RSCREDITO.TOTFACT);
              MI_DBLVALORACUMULADO := MI_DBLVALORACUMULADO + MI_RSCREDITO.TOTFACT;
          END IF;

          BEGIN
              BEGIN
                  MI_TABLA := 'SP_FACTURADO';
                  MI_CAMPOS    := ' CREDITOABONADO  = '|| MI_VALCREDITOABONADO ||'
                                   ,SALDOCREDITO    = '|| MI_VALSALDOCREDITO ||'
                                   ,MODIFIED_BY     = '''|| UN_USUARIO ||'''
                                   ,DATE_MODIFIED   = SYSDATE   ';

                  MI_CONDICIONACME := '   COMPANIA   = '''|| UN_COMPANIA || '''
                                      AND CICLO      = '|| UN_CICLO ||'
                                      AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                      AND ANO        = '|| UN_ANO ||'
                                      AND PERIODO    = '''|| UN_PERIODO ||'''
                                      AND CONCEPTO   = '|| MI_RSCREDITO.CONCEPTO ||' ';

                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICIONACME);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(1).CLAVE := 'USUARIO';
                  MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
                  MI_MSGERROR(2).CLAVE := 'CONCEPTO';
                  MI_MSGERROR(2).VALOR :=  MI_RSCREDITO.CONCEPTO ;
                  MI_MSGERROR(3).CLAVE := 'VALORCRE';
                  MI_MSGERROR(3).VALOR :=  MI_RSCREDITO.CONCEPTO ;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DISSALDOCREDITO ,
                                             UN_TABLAERROR => MI_TABLA,
                                             UN_REEMPLAZOS => MI_MSGERROR);
          END;
      END LOOP SALDOCREDITO;

      --Inserta o actualiza el concepto 17.
      BEGIN
          MI_TABLA     := ' SP_FACTURADO';
          MI_MERGEUSING :=  ' SELECT 1 FROM DUAL ' ;

          MI_MERGEENLACE := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                              AND CICLO        = '|| UN_CICLO ||'
                              AND CODIGORUTA   = '''|| UN_CODIGORUTA ||'''
                              AND ANO          = '|| UN_ANO ||'
                              AND PERIODO      =  '''|| UN_PERIODO ||'''
                              AND CONCEPTO     = '|| 17 ||'   ';

          MI_MERGEEXISTE := ' UPDATE SET   VALOR_FACTURADO     = '|| (-1 * MI_DBLVALORACUMULADO) ||'
                                          ,MODIFIED_BY         = '''|| UN_USUARIO ||'''
                                          ,DATE_MODIFIED       = SYSDATE   ';

          MI_MERGENOEXIS := 'INSERT (COMPANIA,
                                    CICLO,
                                    CODIGORUTA,
                                    ANO,
                                    PERIODO,
                                    CONCEPTO,
                                    VALOR_FACTURADO,
                                    VALOR_FACTURADOIN,
                                    CREATED_BY,
                                    DATE_MODIFIED)
                            VALUES (''' || UN_COMPANIA || ''',
                                      ' || UN_CICLO || ',
                                    ''' || UN_CODIGORUTA || ''',
                                      ' || UN_ANO || ',
                                    ''' || UN_PERIODO || ''',
                                      ' || 17 || ',
                                      ' || (-1 * MI_DBLVALORACUMULADO) || ',
                                      ' || CASE WHEN UN_FACTURADO =0 THEN (-1 * MI_DBLVALORACUMULADO) ELSE 0 END || ',
                                      '''|| UN_USUARIO ||''',
                                      SYSDATE )  ';

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
              MI_MSGERROR(1).CLAVE := 'USUARIO';
              MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
              MI_MSGERROR(2).CLAVE := 'VALOR';
              MI_MSGERROR(2).VALOR := MI_DBLVALORACUMULADO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_CPT17SALDOCREDITO,
                                         UN_TABLAERROR => MI_TABLA,
                                         UN_REEMPLAZOS => MI_MSGERROR);
      END;
      MI_VALORSALDOCREDITO := MI_DBLVALORACUMULADO;
      RETURN MI_VALORSALDOCREDITO;

  END FC_DISTRIBUIRSALDOSCREDITO;

--27
  PROCEDURE PR_VALORDESSALDOCREDITO
  (
      /*
        NAME              : PR_VALORDESSALDOCREDITO --> En Access ValorDesSalCred
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 29/03/2017
        TIME              : 08:25 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Actualiza el valor descontado del saldo credito que se facturo


        PARAMETERS        : UN_COMPANIA          => Compañia en la que se esta trabajando.
                            UN_ANO               => Año de facturación del usuario
                            UN_PERIODO           => Periodo de facturación del usuario
                            UN_CODIGORUTA        => Codigo de ruta del usuario a calcular.
                            UN_CICLO             => Ciclo que se va a calcular.
                            UN_TOTFAC            => Total saldo credito facturado

        MODIFICATIONS     :

        @NAME:    calcularFacturacion
        @METHOD:  POST
      */
     UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_ANO                IN SP_USUARIO.ANO%TYPE
    ,UN_PERIODO            IN SP_USUARIO.PERIODO%TYPE
    ,UN_CODIGORUTA         IN SP_USUARIO.CODIGORUTA%TYPE
    ,UN_CICLO              IN PCK_SUBTIPOS.TI_CICLO
    ,UN_TOTFACT            IN SP_USUARIO.CREDITOABONADO%TYPE
    ,UN_USUARIO            IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS
  MI_RSCRE            SYS_REFCURSOR;
  MI_VALORDESC        SP_TBLHIST_SALDO_CREDITO.VALORDES%TYPE;
  MI_TOTFAC           SP_USUARIO.CREDITOABONADO%TYPE DEFAULT 0;

  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICIONACME    PCK_SUBTIPOS.TI_CONDICION;
  MI_FILAS            PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;


  BEGIN
      MI_TOTFAC := UN_TOTFACT;
      <<CREDITODES>>
      FOR MI_RSCRE IN
      (
          SELECT COMPANIA, CICLO, CODIGORUTA, ANO, PERIODO, VALOR_SALDO, VALORDES, CONSECUTIVO
          FROM   SP_TBLHIST_SALDO_CREDITO T
          WHERE  COMPANIA   = UN_COMPANIA
            AND  CICLO      = UN_CICLO
            AND  CODIGORUTA = UN_CODIGORUTA
            AND  ANO        = UN_ANO
            AND  PERIODO    = UN_PERIODO
          ORDER  BY CONSECUTIVO
      )
      LOOP
          IF MI_TOTFAC > MI_RSCRE.VALOR_SALDO THEN
              MI_VALORDESC := MI_RSCRE.VALOR_SALDO;
              MI_TOTFAC := MI_TOTFAC - MI_RSCRE.VALOR_SALDO;
          ELSIF MI_TOTFAC <= MI_RSCRE.VALOR_SALDO THEN
              MI_VALORDESC := MI_TOTFAC;
              MI_TOTFAC := 0;
          ELSIF MI_TOTFAC = 0 THEN
              MI_VALORDESC :=0;
          END IF;

          BEGIN
              BEGIN
                  MI_TABLA := 'SP_TBLHIST_SALDO_CREDITO';
                  MI_CAMPOS := ' VALORDES  = '|| MI_VALORDESC ||'
                                ,MODIFIED_BY            = '''|| UN_USUARIO ||'''
                                ,DATE_MODIFIED          = SYSDATE   ';

                  MI_CONDICIONACME := '     COMPANIA    = '''|| UN_COMPANIA ||'''
                                       AND  CICLO       = '|| UN_CICLO ||'
                                       AND  CODIGORUTA  = '''|| UN_CODIGORUTA ||'''
                                       AND  ANO         = '|| UN_ANO ||'
                                       AND  PERIODO     = '''|| UN_PERIODO ||'''
                                       AND  CONSECUTIVO = '|| MI_RSCRE.CONSECUTIVO ||'   ';

                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICIONACME);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(1).CLAVE := 'USUARIO';
                  MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
                  MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                  MI_MSGERROR(2).VALOR :=  MI_RSCRE.CONSECUTIVO;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CREDIODESCONTADO,
                                             UN_TABLAERROR => MI_TABLA,
                                             UN_REEMPLAZOS => MI_MSGERROR);
          END;

      END LOOP CREDITODES;



  END PR_VALORDESSALDOCREDITO;

--28
  PROCEDURE PR_INSERTAERRORCALCULO
  (
     /*
       NAME              : PR_INSERTAERRORCALCULO --> Se separo de la funcion en access ManejarError
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
       DATE MIGRADOR     : 29/03/2017
       TIME              : 10:17 AM
       SOURCE MODULE     : SERVICIOS PUBLICOS
       MODIFIER          :
       DATE MODIFIED     :
       TIME              :
       DESCRIPTION       : Procedimiento que inserta los errores de calculo que se registraron durante el proceso de cálculi
       PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                           UN_CODIGORUTA         => Codigo de ruta del usuario.
                           UN_CODIGOERRORINTERNO => Codigo de interno del error.
                           UN_MENSAJE            => Descripción del error.
       MODIFICATIONS     :

       @NAME:    registrarError
       @METHOD:  POST
     */
   UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA           IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CICLO                IN SP_USUARIO.CICLO%TYPE
  ,UN_CODIGOERRORINTERNO   IN SP_ERRORCALCULO.CODIGOERROR%TYPE
  ,UN_MENSAJE              IN SP_ERRORCALCULO.MENSAJE%TYPE
  ,UN_USUARIO              IN SP_USUARIO.CREATED_BY%TYPE :=''
  )
  AS
  MI_CONSECUTIVO     SP_ERRORCALCULO.CONSECUTIVO%TYPE;
  MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
  MI_FILAS           PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>'SP_ERRORCALCULO',
                                                       UN_CRITERIO =>'COMPANIA ='''||UN_COMPANIA||''' ',
                                                       UN_CAMPO    => 'CONSECUTIVO');

    BEGIN
        MI_TABLA := 'SP_ERRORCALCULO';
        MI_CAMPOS := 'COMPANIA,
                      CONSECUTIVO,
                      CICLO,
                      CODIGORUTA,
                      CODIGOERROR,
                      MENSAJE,
                      FECHA,
                      CREATED_BY,
                      DATE_CREATED ';

        MI_VALORES := ' '''||UN_COMPANIA||''',
                          '||MI_CONSECUTIVO||',
                          '||UN_CICLO||',
                        '''||UN_CODIGORUTA||''',
                        '''||UN_CODIGOERRORINTERNO||''',
                        '''||UN_MENSAJE ||''',
                        SYSDATE,
                        '''||UN_USUARIO ||''',
                        SYSDATE';

        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_ERRORCALCULO',
                                        UN_ACCION  => 'I',
                                        UN_CAMPOS  => MI_CAMPOS,
                                        UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

    END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'USUARIO';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGISTROERRCALC,
                                   UN_TABLAERROR => 'SP_USUARIO',
                                   UN_REEMPLAZOS => MI_MSGERROR);
  END PR_INSERTAERRORCALCULO;

--29  
  PROCEDURE PR_RANGOUSUARIOSCICLO (
      /*
      NAME              : PR_RANGOUSUARIOSCICLO --> En Access ActualizarRangos.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 12/05/2017
      TIME              : 12:27 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento que se encarga de actualizar los rangos de usuarios por cada ciclo en la tabla SP_CICLO
      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
      MODIFICATIONS     :

      @NAME:    ActualizarRangos
      @METHOD:  POST
      */
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ) AS
  MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
  MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE     PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_RTA             PCK_SUBTIPOS.TI_ENTERO   DEFAULT 0;

  BEGIN
      BEGIN
          MI_TABLA := 'SP_CICLO';
          MI_MERGEUSING := ' SELECT COMPANIA,
                                    CICLO NUMERO,
                                    MAX(CODIGORUTA) KEEP (DENSE_RANK FIRST ORDER BY CODIGORUTA) CODIGORUTAINICIAL,
                                    MAX(CODIGORUTA) KEEP (DENSE_RANK LAST ORDER BY CODIGORUTA) CODIGORUTAFINAL
                                    FROM SP_USUARIO
                             WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                             GROUP  BY COMPANIA,CICLO ';

          MI_MERGEENLACE := ' VISTA.COMPANIA     = TABLA.COMPANIA
                              AND  VISTA.NUMERO  = TABLA.NUMERO';

          MI_MERGEEXISTE := 'UPDATE SET TABLA.CODIGOINICIAL  = VISTA.CODIGORUTAINICIAL,
                                        TABLA.CODIGOFINAL    = VISTA.CODIGORUTAFINAL';

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                      UN_ACCION      => 'MM',
                                      UN_MERGEUSING  => MI_MERGEUSING,
                                      UN_MERGEENLACE => MI_MERGEENLACE,
                                      UN_MERGEEXISTE => MI_MERGEEXISTE);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ER_RANGOCICLO);

  END PR_RANGOUSUARIOSCICLO;

--30  
  PROCEDURE PR_ACTUALIZAR_MEDIDORES
   /*
        NAME              : PR_ACTUALIZAR_MEDIDORES
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
        DATE MIGRADOR     : 20/09/2017
        TIME              : 09:30 AM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        MODIFICATIONS     : 
        DESCRIPTION       : PROCEDIMIENTO QUE PERMITE ASIGNAR AL USUARIO UN MEDIDOR Y ACTUALIZAR LOS ESTADOS DEL MEDIDOR
                            ANTERIOR Y DEL ACTUAL.
        PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                            UN_CICLO      => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACIÓN.
                            UN_CODIGORUTA => CODIGORUTA POR EL CUAL SE VA A FILTRAR LA INFORMACIÓN.
                            UN_MEDIDOR    => NUEVO MEDIDOR A ASIGNAR AL USUARIO
                            UN_USUARIO    => USUARIO QUE ESTA REALIZANDO LA OPERACION

        @NAME:    actualizarEstadoMedidores
        @METHOD:  PUT
      */
  (
      UN_COMPANIA 		    IN  PCK_SUBTIPOS.TI_COMPANIA,
      UN_CICLO            IN  SP_USUARIO.CICLO%TYPE,
      UN_CODIGORUTA       IN  SP_USUARIO.CODIGORUTA%TYPE,
      UN_MEDIDOR          IN  SP_USUARIO.MEDIDOR%TYPE,
      UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_MEDIDOR        SP_USUARIO.MEDIDOR%TYPE;
    MI_LECTURA        SP_MEDIDOR.LECTMEDANT%TYPE;
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    SELECT MEDIDOR
      INTO MI_MEDIDOR
    FROM SP_USUARIO
    WHERE COMPANIA = UN_COMPANIA
      AND CICLO      = UN_CICLO
      AND CODIGORUTA = UN_CODIGORUTA;

    MI_TABLA :='SP_MEDIDOR';
    MI_CAMPOS:='MODIFIED_BY   = '''||UN_USUARIO||''','||
               'DATE_MODIFIED = SYSDATE,'||
               'ESTADO        = ''M''';                    

    MI_CONDICION:='COMPANIA           = '''|| UN_COMPANIA ||
                  ''' AND CONSECUTIVO = '  || MI_MEDIDOR  ||'';     
    BEGIN
      BEGIN 
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                             UN_ACCION    =>  'M',
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'MEDIDOR';
        MI_MSGERROR(1).VALOR := MI_MEDIDOR;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTESTADOMED
        );  
    END;



    MI_TABLA :='SP_USUARIO';
    MI_CAMPOS:='MODIFIED_BY   = '''||UN_USUARIO||''','||
               'DATE_MODIFIED = SYSDATE,'||
               'MEDIDOR        = '||UN_MEDIDOR||'';                    

    MI_CONDICION:='COMPANIA           = ''' || UN_COMPANIA   ||
                  ''' AND CICLO       = '   || UN_CICLO      || 
                  '   AND CODIGORUTA  = ''' || UN_CODIGORUTA ||'''';     
    BEGIN
      BEGIN 
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                             UN_ACCION    =>  'M',
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTMEDUSUARIO
        );  
    END;

    SELECT LECTURA
    INTO MI_LECTURA
    FROM SP_USUARIO
    WHERE COMPANIA   = UN_COMPANIA
      AND CICLO      = UN_CICLO
      AND CODIGORUTA = UN_CODIGORUTA;

    MI_TABLA :='SP_MEDIDOR';
    MI_CAMPOS:='MODIFIED_BY   = '''||UN_USUARIO||''','||
               'DATE_MODIFIED = SYSDATE,'||
               'ESTADO        = ''B'',
                LECTMEDANT    = '||MI_LECTURA||'';                    

    MI_CONDICION:='COMPANIA           = '''|| UN_COMPANIA ||
                  ''' AND CONSECUTIVO = '  || UN_MEDIDOR  ||'';     
    BEGIN
      BEGIN 
        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                             UN_ACCION    =>  'M',
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'MEDIDOR';
        MI_MSGERROR(1).VALOR := UN_MEDIDOR;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTESTADOMED
        );  
    END;
  END PR_ACTUALIZAR_MEDIDORES;

--31
  FUNCTION FC_SUSCRIPTORTGR(
        /*
        NAME              : FC_SUSCRIPTORTGR --> Se pasa del formulario usuario
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 18/10/2017
        TIME              : 02:37 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Función que se ejecuta al cambio o inserción en la tabla SP_USUARIO

        PARAMETERS        : Campos de la tabla usuario que se llenan desde los triger de inserción y actualización.
                            UN_DBLTOTAL: Parámetro IN Out para actualizar el totfacturaperactual de usuario cuando tiene financiables iniciales.
                            UN_ACCION: Recibe INSERTAR Y ACTUALIZAR, Acciones que se envían desde sus correspondientes trigers.
                            UN_USUARIO
        MODIFICATIONS     :

        @NAME:    SuscroptoresTgr
        @METHOD:  GET
        */

   UN_COMPANIA                IN SP_USUARIO.COMPANIA%TYPE
  ,UN_CICLO                   IN SP_USUARIO.CICLO%TYPE
  ,UN_CODIGORUTA              IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CODIGOINTERNO           IN SP_USUARIO.CODIGOINTERNO%TYPE
  ,UN_ANO                     IN SP_USUARIO.ANO%TYPE
  ,UN_PERIODO                 IN SP_USUARIO.PERIODO%TYPE
  ,UN_ANTESTADO               IN SP_USUARIO.ESTADO%TYPE :=''
  ,UN_NUEESTADO               IN SP_USUARIO.ESTADO%TYPE :=''
  ,UN_VALIDACAMBIOMATRICULA   IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_TOTALDEUDA              IN SP_USUARIO.TOTALDEUDA%TYPE :=0
  ,UN_BANCOPERPROCESO         IN SP_USUARIO.BANCOPERPROCESO%TYPE := ''
  ,UN_ANTHOGARCOMUNI          IN SP_USUARIO.HOGAR_COMUNITARIO%TYPE :=0
  ,UN_NUEHOGARCOMUNI          IN SP_USUARIO.HOGAR_COMUNITARIO%TYPE :=0
  ,UN_TOTFACTURA              IN SP_USUARIO.TOTFACTURAPERACTUAL%TYPE :=0
  ,UN_USO                     IN SP_USUARIO.USO%TYPE :=''
  ,UN_ESTRATO                 IN SP_USUARIO.ESTRATO%TYPE :=''
  ,UN_ANTMATRICULA            IN SP_USUARIO.MATRICULA%TYPE :=''
  ,UN_NUEMATRICULA            IN SP_USUARIO.MATRICULA%TYPE :=''
  ,UN_TIPOPREDIO              IN SP_USUARIO.TIPOPREDIO%TYPE :=''
  ,UN_CLASESOLICITUD          IN SP_USUARIO.CLASESOLICITUD%TYPE := ''
  ,UN_CAMBIOINDASEO           IN PCK_SUBTIPOS.TI_LOGICO :=0
  ,UN_CAMBIOASEOBARRIDO       IN PCK_SUBTIPOS.TI_LOGICO :=0
  ,UN_EMPRESAASEOEXT          IN SP_USUARIO.EMPRESAASEOEXT%TYPE :=''
  ,UN_PINTADEUDATERCE         IN SP_USUARIO.PINTADEUDATERCE%TYPE :=0
  ,UN_SOLICITUD               IN SP_USUARIO.SOLICITUD%TYPE := ''
  ,UN_VALIDACAMBIOCODEXTERNO  IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_NUECODIGO_EXTERNO       IN SP_USUARIO.CODIGO_EXTERNO%TYPE := ''
  ,UN_DBLTOTAL                IN OUT PCK_SUBTIPOS.TI_DOBLE
  ,UN_INSERTAFACTUSUARIO      IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_ACCION                  IN VARCHAR2
  ,UN_USUARIO                 IN SP_USUARIO.CREATED_BY%TYPE :=''
  ) RETURN NUMBER AS

   MI_SUMAFINAN          SP_FINANCIABLES.SALDOFINANCIABLE%TYPE;
   MI_SUMAFINANPERACT    SP_FINANCIABLES.SALDOFINANCIABLE%TYPE;
   MI_CUENTARS           INTEGER DEFAULT 0;
   MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_PERMITEHOGAR       SP_TARIFAS.PERMITE_HOGAR%TYPE DEFAULT 0;
   MI_RS                 SYS_REFCURSOR;
   MI_ANOFINAN           SP_USUARIO.ANO%TYPE;
   MI_PERIODOFINAN       SP_USUARIO.PERIODO%TYPE;

   MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
   MI_FILAS              PCK_SUBTIPOS.TI_ENTERO;
   MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
   MI_CONSEC             SP_TBL_HIST_NOVEDADES_USU.IDNOVEDAD%TYPE;
   MI_DESCNOVEDAD        SP_TBL_HIST_NOVEDADES_USU.DESCRIPCION%TYPE;
   MI_ESTADONOVEDAD      SP_TBL_HIST_NOVEDADES_USU.ESTADO%TYPE;
   MI_RTA                PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
  BEGIN

      BEGIN
          IF UN_CODIGOINTERNO = 0 THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          --El código interno no puede ser cero, para el usuario: --RUTA—
          MI_MSGERROR(0).CLAVE := 'RUTA';
          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_USUARIO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUCODINTERNOCERO
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
          RETURN 0;
      END;

      IF UN_ACCION = 'ACTUALIZAR' THEN
          IF UN_VALIDACAMBIOMATRICULA <> 0 OR UN_VALIDACAMBIOCODEXTERNO <> 0  THEN
              IF UN_VALIDACAMBIOMATRICULA <> 0 THEN
                  BEGIN
                      SELECT COUNT(0) CUENTA
                      INTO   MI_CUENTARS
                      FROM   SP_USUARIO
                      WHERE  COMPANIA =  UN_COMPANIA
                        AND  MATRICULA =  UN_NUEMATRICULA;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_CUENTARS := 0;
                  END;

                  BEGIN
                      IF MI_CUENTARS > 1 THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      MI_MSGERROR(1).CLAVE := 'MATRICULA';
                      MI_MSGERROR(1).VALOR := UN_NUEMATRICULA ;
                      --No se puede modificar el número de matrícula: --MATRICULA--  , Para el usuario: --RUTA--, El número de la matrícula ya fue relacionada en otro usuario.
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_TABLAERROR => 'SP_USUARIO'
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOMISMAMATRICULA
                                                ,UN_REEMPLAZOS =>  MI_MSGERROR);
                      RETURN 0;
                  END;
              END IF;
              IF UN_VALIDACAMBIOCODEXTERNO <> 0 THEN
                  BEGIN
                      SELECT COUNT(0) CUENTA
                      INTO   MI_CUENTARS
                      FROM   SP_USUARIO
                      WHERE  CODIGO_EXTERNO = UN_NUECODIGO_EXTERNO;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_CUENTARS := 0;
                  END;

                  BEGIN
                      IF MI_CUENTARS > 1 THEN
                          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                      END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                      MI_MSGERROR(1).CLAVE := 'EXTERNO';
                      MI_MSGERROR(1).VALOR := UN_NUECODIGO_EXTERNO;
                      --No se puede modificar el codigo externo: --EXTERNO--  , Para el usuario: --RUTA--, Ya fue relacionado en otro usuario.
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_TABLAERROR => 'SP_USUARIO'
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUACAMBIOCODEXTERNO
                                                ,UN_REEMPLAZOS =>  MI_MSGERROR);
                      RETURN 0;
                  END;
              END IF;

          ELSE 
              --Cambio de estado
              IF NVL(UN_ANTESTADO,' ') <> NVL(UN_NUEESTADO,' ')  THEN
                  BEGIN
                      MI_SUMAFINAN := 0;
                      SELECT SUM(SALDOFINANCIABLE) AS SUMADESALDOFINANCIABLE,
                             SUM(CASE WHEN  PERIODO = UN_PERIODO
                                  THEN SALDOFINANCIABLE
                                  ELSE 0
                                 END )  AS SUMAACT
                      INTO   MI_SUMAFINAN,MI_SUMAFINANPERACT
                      FROM   SP_FINANCIABLES
                      WHERE  COMPANIA = UN_COMPANIA
                        AND  CICLO = UN_CICLO
                        AND  PERIODO > = UN_PERIODO
                        AND  CODIGORUTA = UN_CODIGORUTA
                        AND  ANO =  UN_ANO
                      HAVING SUM(SALDOFINANCIABLE) <> 0;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_SUMAFINAN := 0;
                  END;

                  IF NVL(UN_NUEESTADO,' ') <> 'A' THEN
                      BEGIN
                          IF MI_SUMAFINAN > 0 THEN
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END IF;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                          --El usuario –RUTA--  tiene Financiables con un saldo total de –SALDO--, No se permite cambiar al estado:  --ESTADO--.
                          MI_MSGERROR(0).CLAVE := 'RUTA';
                          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                          MI_MSGERROR(1).CLAVE := 'SALDO';
                          MI_MSGERROR(1).VALOR := MI_SUMAFINAN;
                          MI_MSGERROR(2).CLAVE := 'ESTADO';
                          MI_MSGERROR(2).VALOR := MI_SUMAFINAN;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                    ,UN_TABLAERROR => 'SP_USUARIO'
                                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUACAMBIOESTADOFINAN
                                                    ,UN_REEMPLAZOS => MI_MSGERROR);
                          RETURN 0;
                      END;
                  END IF;

                  IF MI_SUMAFINANPERACT > 0 THEN
                      BEGIN
                          MI_TABLA := 'SP_TBL_HIST_NOVEDADES_USU';

                          MI_CONSEC := TO_CHAR(PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                                              (UN_TABLA    => MI_TABLA
                                              ,UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''  '
                                              ,UN_CAMPO    => 'IDNOVEDAD'));

                          MI_ESTADONOVEDAD := CASE WHEN UN_NUEESTADO = 'A' THEN 'Activo'
                                              ELSE CASE WHEN UN_NUEESTADO = 'S' THEN 'Suspendido'
                                                 ELSE CASE WHEN UN_NUEESTADO = 'C' THEN 'Cortado'
                                                    ELSE 'Retirado'
                                                    END
                                                 END
                                              END;


                          MI_CAMPOS := ' COMPANIA
                                        ,CICLO
                                        ,CODIGORUTA
                                        ,IDNOVEDAD
                                        ,ESTADO
                                        ,FECHA
                                        ,DESCRIPCION
                                        ,CREATED_BY
                                        ,DATE_CREATED ';

                          MI_DESCNOVEDAD := '
                                              Cambiado por '|| UN_USUARIO ||'.  Deuda = '|| UN_TOTALDEUDA ||'.  Total Facturado =
                                              ' || UN_TOTFACTURA ||  CASE WHEN UN_BANCOPERPROCESO IS NULL THEN ' Valor en Mora.  Saldo Financiables = '|| MI_SUMAFINANPERACT ||' ' ELSE '. Ultima Factura pagada' END  ||'
                                              ';

                          MI_VALORES :='  '''|| UN_COMPANIA ||'''
                                        ,'|| UN_CICLO ||'
                                        ,'''|| UN_CODIGORUTA ||'''
                                        ,'|| MI_CONSEC ||'
                                        ,'''|| MI_ESTADONOVEDAD ||'''
                                        ,SYSDATE
                                        ,'''|| MI_DESCNOVEDAD || '''
                                        ,'''|| UN_USUARIO ||'''
                                        ,SYSDATE ';
                          BEGIN
                              MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_VALORES);
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                          --Error al registrar la novedad de cambio de estado para el usuario: --RUTA--.
                          MI_MSGERROR(0).CLAVE := 'RUTA';
                          MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                    ,UN_TABLAERROR => MI_TABLA
                                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUAESTADOREGNOVEDAD
                                                    ,UN_REEMPLAZOS => MI_MSGERROR);
                      END;
                  END IF;

                  BEGIN   --Auditoria de cambio de estado
                      MI_RTA := PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI
                                  ( UN_COMPANIA       => UN_COMPANIA
                                   ,UN_CICLO          => UN_CICLO
                                   ,UN_CODIGO         => UN_CODIGORUTA
                                   ,UN_VALFINAL       => UN_NUEESTADO
                                   ,UN_VALINICIAL     => UN_ANTESTADO
                                   ,UN_CAMPOACTUAL    => 'ESTADO'
                                   ,UN_CAMPOANTERIOR  => 'ESTADO_ANT'
                                   ,UN_PERIODO        => UN_PERIODO
                                   ,UN_USUARIO        => UN_USUARIO );
                  END;

              END IF;

              BEGIN
                  IF UN_ANTHOGARCOMUNI <> UN_NUEHOGARCOMUNI THEN
                      IF UN_NUEHOGARCOMUNI <> 0 THEN
                          IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                       UN_NOMBRE    =>  'CONTROLA HOGAR COMUNITARIO DESDE TARIFAS',
                                                       UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN
                              BEGIN
                                  SELECT PERMITE_HOGAR
                                  INTO   MI_PERMITEHOGAR
                                  FROM   SP_TARIFAS
                                  WHERE  COMPANIA = UN_COMPANIA
                                    AND  ANO = UN_ANO
                                    AND  PERIODO = UN_PERIODO
                                    AND  USO = UN_USO
                                    AND  ESTRATO = UN_ESTRATO
                                    AND  PERMITE_HOGAR = 0;
                              EXCEPTION WHEN NO_DATA_FOUND THEN
                                  MI_PERMITEHOGAR := 0;
                              END;

                              BEGIN
                                  IF MI_PERMITEHOGAR <> 0 THEN
                                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                                  END IF;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                                  --El Hogar comunitario se controla por tarifas no se puede asignar, Usuario: --RUTA--
                                  MI_MSGERROR(0).CLAVE := 'RUTA';
                                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                            ,UN_TABLAERROR => 'SP_USUARIO'
                                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUAHOGARCOMUNIT
                                                            ,UN_REEMPLAZOS => MI_MSGERROR);
                                  RETURN 0;

                              END;
                          END IF;
                      END IF;
                  END IF;
              END;

              IF UN_CAMBIOINDASEO <> 0 OR UN_CAMBIOASEOBARRIDO <> 0 THEN
                  BEGIN
                      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                   UN_NOMBRE    =>  'MANEJA PROCESO TERCERIZADO',
                                                   UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN

                          IF NVL(UN_EMPRESAASEOEXT,' ') <> ' ' AND UN_PINTADEUDATERCE = 0 THEN
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                          END IF;
                      END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                      MI_MSGERROR(0).CLAVE := 'RUTA';
                      MI_MSGERROR(0).VALOR := UN_CODIGORUTA ;
                      --PCK_ERRORES.ERR_USUAINDASEOTERCERIZADO = El servicio de Aseo para el suscriptor: --RUTA--, esta tercerizado.
                      --PCK_ERRORES.ERR_USUAINDABARRIDOTERCERIZADO = El servicio de Barrido y Limpieza para el Suscriptor: --RUTA-- , esta tercerizado.
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_TABLAERROR => 'SP_USUARIO'
                                                ,UN_ERROR_COD  => CASE WHEN UN_CAMBIOASEOBARRIDO <> 0 THEN PCK_ERRORES.ERR_USUAINDABARRIDOTERCERIZADO ELSE PCK_ERRORES.ERR_USUAINDASEOTERCERIZADO END
                                                ,UN_REEMPLAZOS =>  MI_MSGERROR);
                  END;
              END IF;
          END IF;

      ELSIF UN_ACCION = 'INSERTAR' THEN
          IF UN_INSERTAFACTUSUARIO = 0 THEN
              BEGIN
                  IF UN_CLASESOLICITUD IS NULL OR UN_SOLICITUD IS NULL THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END IF;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  --No se puede crear un usuario sin solicitud de servicio, Los usuarios se crean en P.Q.R.\Solicitudes de Servicios
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_USUARIO'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOSINSOLICITUD);
                  RETURN 0;
              END;

              BEGIN
                  IF NVL(UN_TIPOPREDIO,' ') = ' ' THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END IF;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  --Se debe seleccionar el Tipo del Predio para el usuario: --RUTA--.
                  MI_MSGERROR(0).CLAVE := 'RUTA';
                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_USUARIO'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOSINTPREDIO
                                            ,UN_REEMPLAZOS => MI_MSGERROR);
                  RETURN 0;
              END;

              BEGIN
                  MI_TABLA := 'SP_SOLICITUDFINANCIABLES';
                  MI_CAMPOS := ' ANOINICIAL =  '|| UN_ANO ||'
                                ,PERIODOINICIAL =  '''|| UN_PERIODO ||'''   ';

                  MI_CONDICION := 'COMPANIA   = '''|| UN_COMPANIA ||'''
                              AND  CLASESOLICITUD = '''|| UN_CLASESOLICITUD ||'''
                              AND  SOLICITUD = '|| UN_SOLICITUD ||'
                              AND  ((ANOINICIAL || PERIODOINICIAL) <= ('|| UN_ANO || UN_PERIODO   ||'  )
                                    OR (ANOINICIAL || PERIODOINICIAL) IS NULL )
                               ';

                  BEGIN
                      MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(0).CLAVE := 'RUTA';
                  MI_MSGERROR(0).VALOR := UN_CODIGORUTA;
                  --Se presentó error al actualizar el año y periodo de los financiables para el nuevo usuario: --RUTA--
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            ,UN_TABLAERROR => 'SP_SOLICITUDFINANCIABLES'
                                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOACTSOLICITFINAN
                                            ,UN_REEMPLAZOS =>  MI_MSGERROR);
                  RETURN 0;

              END;

              PCK_SERVICIOS_PUBLICOS_COM3.PR_AUDITARMODIF
                  ( UN_COMPANIA  	 => UN_COMPANIA
                   ,UN_FORMORIGEN  => 'USUARIO'
                   ,UN_INTTIPO	 => 1
                   ,UN_CAMPO  	 => 'Compañía '|| UN_COMPANIA ||', Uso '|| NVL(UN_USO,' ') ||', Estrato '|| NVL(UN_ESTRATO,' ') ||', Año '|| UN_ANO ||', Periodo '|| UN_PERIODO ||' '
                   ,UN_USUARIO 	 => UN_USUARIO );

              UN_DBLTOTAL := 0;
              BEGIN
                  SELECT SUM(INICIAL)  TOTINICIAL
                  INTO   UN_DBLTOTAL
                  FROM   SP_SOLICITUDFINANCIABLES
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  CLASESOLICITUD = UN_CLASESOLICITUD
                    AND  SOLICITUD = UN_SOLICITUD
                    AND  INICIAL <>0;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                  UN_DBLTOTAL := 0;
              END;

          ELSIF UN_INSERTAFACTUSUARIO <> 0 THEN
              BEGIN
                  <<FINANUSUARIO>>
                  FOR MI_RS IN
                  (
                      SELECT COMPANIA, CLASESOLICITUD, SOLICITUD, CONCEPTO, VALOR, INICIAL, CUOTAS,
                             ANOINICIAL, PERIODOINICIAL
                      FROM   SP_SOLICITUDFINANCIABLES
                      WHERE  COMPANIA = UN_COMPANIA
                        AND  CLASESOLICITUD = UN_CLASESOLICITUD
                        AND  SOLICITUD = UN_SOLICITUD
                  )
                  LOOP
                      IF MI_RS.INICIAL <> 0 THEN
                          BEGIN
                              MI_TABLA := 'SP_FACTURADO';
                              MI_CAMPOS := ' COMPANIA
                                            ,CICLO
                                            ,CODIGORUTA
                                            ,CONCEPTO
                                            ,ANO
                                            ,PERIODO
                                            ,VALOR_FACTURADO
                                            ,CREATED_BY
                                            ,DATE_CREATED ';

                              MI_VALORES := ' '''|| UN_COMPANIA || '''
                                             ,'|| UN_CICLO ||'
                                             ,'''|| UN_CODIGORUTA ||'''
                                             ,'|| MI_RS.CONCEPTO ||'
                                             ,'|| MI_RS.ANOINICIAL ||'
                                             ,'''|| MI_RS.PERIODOINICIAL ||'''
                                             ,'||  MI_RS.INICIAL ||'
                                             ,'''|| UN_USUARIO ||'''
                                             ,SYSDATE  ';

                              BEGIN
                                  MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                            UN_ACCION  => 'I',
                                                            UN_CAMPOS  => MI_CAMPOS,
                                                            UN_VALORES => MI_VALORES);

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                              MI_MSGERROR(0).CLAVE := 'FACINI';
                              MI_MSGERROR(0).VALOR := MI_RS.INICIAL;
                              MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                              MI_MSGERROR(1).VALOR := MI_RS.CONCEPTO;
                              MI_MSGERROR(2).CLAVE := 'RUTA';
                              MI_MSGERROR(2).VALOR := UN_CODIGORUTA;
                              --Se presentó error al insertar el facturado inicial por: --FACINI-- , Concepto: --CONCEPTO-- , Para el usuario: --RUTA--.
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOFACTURADOINI ,
                                                        UN_TABLAERROR => MI_TABLA,
                                                        UN_REEMPLAZOS => MI_MSGERROR);
                              RETURN 0;
                          END;

                      END IF;

                      IF (MI_RS.VALOR - MI_RS.INICIAL) > 0 THEN
                          BEGIN
                              MI_TABLA := 'SP_FINANCIABLES';
                              MI_CAMPOS := ' COMPANIA
                                            ,CICLO
                                            ,CODIGORUTA
                                            ,ANO
                                            ,PERIODO
                                            ,CONCEPTO
                                            ,MONTOFINANCIAR
                                            ,NUMEROCUOTAS
                                            ,SALDOFINANCIABLE
                                            ,VALORCUOTA
                                            ,ANOINICIAL
                                            ,PERIODOINICIAL
                                            ,CREATED_BY
                                            ,DATE_CREATED  ';

                              IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                           UN_NOMBRE    =>  'COBRO DE SOL. SERVICIOS PERIODO ACTUAL',
                                                           UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                                           UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN
                                  MI_ANOFINAN := MI_RS.ANOINICIAL;
                                  MI_PERIODOFINAN := MI_RS.PERIODOINICIAL;
                              ELSE
                                  MI_ANOFINAN := TO_NUMBER(PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE
                                                              (UN_COMPANIA     => UN_COMPANIA
                                                              ,UN_ANO          => MI_RS.ANOINICIAL
                                                              ,UN_PERIODO      => MI_RS.PERIODOINICIAL
                                                              ,UN_TIPO_RETORNO => 0
                                                              ,UN_FRECUENCIA   => NULL ));

                                  MI_PERIODOFINAN := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE
                                                      (UN_COMPANIA     => UN_COMPANIA
                                                      ,UN_ANO          => MI_RS.ANOINICIAL
                                                      ,UN_PERIODO      => MI_RS.PERIODOINICIAL
                                                      ,UN_TIPO_RETORNO => 1
                                                      ,UN_FRECUENCIA   => NULL );
                              END IF;

                              MI_VALORES := ' '''|| UN_COMPANIA ||'''
                                             ,'|| UN_CICLO ||'
                                             ,'''|| UN_CODIGORUTA ||'''
                                             ,'|| MI_ANOFINAN ||'
                                             ,'''|| MI_PERIODOFINAN || '''
                                             ,'|| MI_RS.CONCEPTO ||'
                                           ,'|| (MI_RS.VALOR - MI_RS.INICIAL) || '
                                             ,'|| MI_RS.CUOTAS ||'
                                             ,'|| (MI_RS.VALOR - MI_RS.INICIAL) || '
                                             ,'|| ROUND( (MI_RS.VALOR - MI_RS.INICIAL) / MI_RS.CUOTAS, 2) ||'
                                             ,'|| MI_RS.ANOINICIAL || '
                                             ,'''|| MI_RS.PERIODOINICIAL ||'''
                                             ,'''|| UN_USUARIO ||'''
                                             ,SYSDATE ';

                              BEGIN
                                  MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                UN_ACCION  => 'I',
                                                                UN_CAMPOS  => MI_CAMPOS,
                                                                UN_VALORES => MI_VALORES);


                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                              END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                              MI_MSGERROR(0).CLAVE := 'FINAN';
                              MI_MSGERROR(0).VALOR := (MI_RS.VALOR - MI_RS.INICIAL);
                              MI_MSGERROR(1).CLAVE := 'RUTA';
                              MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
                              --Se presentó error al insertar los financiables por un valor de: --FINAN--,  para el usuario: --RUTA--.
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIOINSFINANCIABLE,
                                                         UN_TABLAERROR => MI_TABLA,
                                                         UN_REEMPLAZOS => MI_MSGERROR);
                              RETURN 0;
                          END;

                      END IF;
                  END LOOP FINANUSUARIO;
              END;

          END IF;

      END IF;

      RETURN -1;

  END FC_SUSCRIPTORTGR;

--32
  FUNCTION FC_ACTUALIZARSUSPENDIDOS(
          /*
          NAME              : FC_ACTUALIZARSUSPENDIDOS --> En access CamEstado En formulario Usuario
          AUTHORS           : SYSMAN  SAS
          AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
          DATE MIGRADOR     : 25/10/2017
          TIME              : 12:18 PM
          SOURCE MODULE     : SERVICIOS PUBLICOS
          MODIFIER          :
          DATE MODIFIED     :
          TIME              :
          DESCRIPTION       : Función para actualizar los facturados de los usuarios suspendidos

          PARAMETERS        : UN_COMPANIA: Compañia a la que pertenece el usuario
                              UN_CICLO: Ciclo al que pertenece el usuario
                              UN_CODIGORUTA: Codigo de ruta del usuario
                              UN_ANO: Año actual al que pertenece el usuario.
                              UN_PERIODO: Periodo actual al que pertenece el usuario.
                              UN_BANCOPAGO: Banco de pago del usuario
                              UN_DESDETRIGGER: Valor logico para determinar si la función se llama desde Trigger.
                              UN_USUARIO: Usuario que modifica.
          MODIFICATIONS     :

          @NAME:    ActualizaSuspendidos
          @METHOD:  GET
          */
       UN_COMPANIA                IN SP_USUARIO.COMPANIA%TYPE
      ,UN_CICLO                   IN SP_USUARIO.CICLO%TYPE
      ,UN_CODIGORUTA              IN SP_USUARIO.CODIGORUTA%TYPE
      ,UN_CODIGOINTERNO           IN SP_USUARIO.CODIGOINTERNO%TYPE
      ,UN_ANO                     IN SP_USUARIO.ANO%TYPE
      ,UN_PERIODO                 IN SP_USUARIO.PERIODO%TYPE
      ,UN_BANCOPAGO               IN SP_USUARIO.BANCOPERPROCESO%TYPE
      ,UN_DESDETRIGGER            IN PCK_SUBTIPOS.TI_LOGICO := 0
      ,UN_USUARIO                 IN SP_USUARIO.MODIFIED_BY%TYPE
  ) RETURN NUMBER AS

   MI_ULTIMOANO               SP_FACTURADO.ANO%TYPE DEFAULT 0;
   MI_RTA                     PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
   MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
   MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
   MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
   MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_CANTPAGOS               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;

  BEGIN

      BEGIN
          SELECT NVL(MAX(ANO),0) ULTANO
          INTO   MI_ULTIMOANO
          FROM   SP_FACTURADO
          WHERE  COMPANIA = UN_COMPANIA
            AND  CICLO = UN_CICLO
            AND  CODIGORUTA = UN_CODIGORUTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_ULTIMOANO :=0;
      END;

      IF MI_ULTIMOANO <> 0 THEN
          BEGIN
              MI_TABLA := 'SP_FACTURADO';
              MI_CAMPOS := ' ANO  = '|| UN_ANO ||'
                            ,PERIODO = '''|| UN_PERIODO ||'''
                            ,MODIFIED_BY  = '''|| UN_USUARIO ||'''
                            ,DATE_MODIFIED  = SYSDATE ';

              MI_CONDICIONACME := ' COMPANIA = '''|| UN_COMPANIA ||'''
                                AND CICLO    = '|| UN_CICLO ||'
                                AND CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                                AND ANO = '|| MI_ULTIMOANO ||' ';

              BEGIN
                  MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICIONACME);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(0).CLAVE := 'ANOANT';
              MI_MSGERROR(0).VALOR :=  MI_ULTIMOANO;
              MI_MSGERROR(1).CLAVE := 'ANOACT';
              MI_MSGERROR(1).VALOR :=  UN_ANO;
              MI_MSGERROR(1).CLAVE := 'RUTA';
              MI_MSGERROR(1).VALOR :=  UN_CODIGORUTA;
              --Se presentó error al actualizar los facturados del año: --ANOANT-- Al año: --ANOACT--, Para el usuario: --RUTA--.
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_ACTSUSPENDIDOSFACTUACT,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_MSGERROR);
              RETURN 0;
          END;

          IF NVL(UN_BANCOPAGO, ' ') <> ' ' THEN
              BEGIN
                  SELECT COUNT(0) CUENTAPAGO
                  INTO   MI_CANTPAGOS
                  FROM   SP_PAGO
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  CODIGOINTERNO = UN_CODIGOINTERNO
                    AND  PERIODO = UN_PERIODO
                    AND  ANO = UN_PERIODO;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_CANTPAGOS := 0;
              END;

              IF MI_CANTPAGOS = 0 THEN
                  IF UN_DESDETRIGGER = 0 THEN --Para que reciba la respuesta desde el trigger y desde el trigger actualice.
                      BEGIN
                          MI_TABLA := 'SP_USUARIO';
                          MI_CAMPOS := ' BANCOPERPROCESO = NULL
                                        ,FECHAPAGOPERPROCESO = NULL
                                        ,PAQUETEPAGOPERPROCESO = NULL
                                        ,NOFECHAPAGOPERPROCESO = NULL
                                        ,RECAUDADOPROCESO = 0
                                        ,TOTFACTURAPERACTUAL = 0 ';

                          MI_CONDICIONACME := ' COMPANIA = '''|| UN_COMPANIA || '''
                                            AND CICLO = '|| UN_CICLO ||'
                                            AND CODIGORUTA = '''|| UN_CODIGORUTA ||''' ';
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
                          MI_MSGERROR(0).VALOR :=  UN_CODIGORUTA;
                          --Se presentó error al actualizar los datos de pago del usuario: --RUTA--,  inactivo.
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_ACTSUSPENDIDOSUSUARIOPAG,
                                                  UN_TABLAERROR => MI_TABLA,
                                                  UN_REEMPLAZOS => MI_MSGERROR);
                          RETURN 0;
                      END;
                  END IF;
              END IF;
          END IF;

          MI_RTA := -1;
      ELSE --El usuario no tiene facturados anteriores al periodo actual
          MI_RTA := 0;
      END IF;

      RETURN MI_RTA;

  END FC_ACTUALIZARSUSPENDIDOS;

--33
  FUNCTION FC_CREARCODIGOINTERNO(
      /*
      NAME              : FC_CREARCODIGOINTERNO -->En Access crearcodigointerno, Se pasa del formulario usuario
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 26/10/2017
      TIME              : 09:07 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Función que crea el codigo interno del usuario

      PARAMETERS        : UN_COMPANIA: Compañia del usuario
                          UN_CICLO: Ciclo al cual pertenece el usuario a crear el codigo interno
                          UN_NUMREGISTRO:
      MODIFICATIONS     :

      @NAME:    CrearCodigoInterno
      @METHOD:  GET
      */

    UN_COMPANIA      IN SP_USUARIO.COMPANIA%TYPE
   ,UN_CICLO         IN SP_USUARIO.CICLO%TYPE
   ,UN_NUMREGISTRO   IN OUT SP_USUARIO.NUMEROREGISTRO%TYPE
  )RETURN VARCHAR2 AS

  MI_CODINTERNO      SP_USUARIO.CODIGOINTERNO%TYPE ;
  MI_NUMREG          SP_USUARIO.NUMEROREGISTRO%TYPE DEFAULT 0;
  MI_COMPANIA        SP_USUARIO.COMPANIA%TYPE;
  BEGIN
      BEGIN
          SELECT MAX(SUBSTR(CODIGOINTERNO,2,5)) CODIGO
          INTO   MI_NUMREG
          FROM   SP_USUARIO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_NUMREG := 0;
      END;

      MI_NUMREG := MI_NUMREG + 1;
      UN_NUMREGISTRO := MI_NUMREG;
      MI_NUMREG := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => MI_NUMREG
                                            ,UN_LONGITUD => 5);

      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA
                                  ,UN_NOMBRE    =>  'MANEJA CODIGO INTERNO POR COMPANIA'
                                  ,UN_MODULO    =>  PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                  ,UN_FECHA_PAR =>  SYSDATE),'NO') = 'NO' THEN

          MI_CODINTERNO := TO_CHAR(UN_CICLO || MI_NUMREG || PCK_SYSMAN_UTL.FC_DCH(UN_NUMERO => UN_CICLO || MI_NUMREG));
      ELSE
          MI_COMPANIA := PCK_SYSMAN_UTL.FC_RIGHT(UN_STRVALOR => UN_COMPANIA
                                                ,UN_DIGITOS  => 1);

          MI_CODINTERNO := TO_CHAR(MI_COMPANIA || MI_NUMREG || PCK_SYSMAN_UTL.FC_DCH(UN_NUMERO => TO_NUMBER(MI_COMPANIA) || MI_NUMREG));
      END IF;


      RETURN MI_CODINTERNO;

  END FC_CREARCODIGOINTERNO;



END PCK_SERVICIOS_PUBLICOS_COM7;