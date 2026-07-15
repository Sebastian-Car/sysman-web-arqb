create or replace PACKAGE BODY PCK_PREDIAL_COM6 AS

--1
FUNCTION FC_IMPRIMIRCUOTAACUERDODEPAGO
/*
      NAME              : FC_IMPRIMIRCUOTAACUERDODEPAGO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION QUE REALIZA LOS CALCULOS CORRESPONDIENTES PARA LA IMPRESION DEL RECIBO DE PAGO DE IMPUESTO PREDIAL. 
      PARAMETERS        :     
                          UN_CODIGOACUERDO			=> CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR
                          UN_COMPANIA        		=> COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_FECHAVENCIDA			=>       
                          UN_ANULADO			=>   
                          UN_CANCELADO			=>    
                          UN_PAGOCUOTAANTERIOR		=>     
                          UN_CUOTA				=> CUOTA POR LA CUAL SE VA A FILTRAR    
                          UN_FACTURACIONCUOTAANTERIOR	=>     
                          UN_CONTROLARRECIBOS		=>     
                          UN_CODIGOPREDIO			=> CODIGO DEL PREDIO POR EL CUAL SE VA A FILTRAR    

      @NAME: imprimirCuotaAcuerdoDePago
      @METHOD:  GET
    */ 
(
  UN_CODIGOACUERDO             IN   IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_COMPANIA                  IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO                   IN   PCK_SUBTIPOS.TI_USUARIO,
  UN_FECHAVENCIDA              IN   PCK_SUBTIPOS.TI_ENTERO,
  UN_ANULADO                   IN   IP_ACUERDOS.ANULADO%TYPE,
  UN_CANCELADO                 IN   IP_ACUERDOS.CANCELADO%TYPE,
  UN_PAGOCUOTAANTERIOR         IN   PCK_SUBTIPOS.TI_ENTERO,
  UN_CUOTA                     IN   IP_FACTURADOSACUERDOS.CUOTA%TYPE,
  UN_FACTURACIONCUOTAANTERIOR  IN  PCK_SUBTIPOS.TI_ENTERO,
  UN_CONTROLARRECIBOS          IN   PCK_SUBTIPOS.TI_ENTERO,
  UN_CODIGOPREDIO              IN   IP_FACTURADOSACUERDOS.PREDIO%TYPE
)
RETURN VARCHAR2
AS
MI_MSGRETORNO           VARCHAR2(32000 CHAR);
MI_FECHALIMITE          DATE;
MI_CUOTA                IP_FACTURADOSACUERDOS.CUOTA%TYPE;
MI_FECHA                IP_FACTURADOSACUERDOS.FECHAFACTURADO%TYPE;
MI_PAGADO               IP_FACTURADOSACUERDOS.PAGADO%TYPE;
MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
MI_CONTEO               PCK_SUBTIPOS.TI_ENTERO;
MI_PREDIO               IP_FACTURADOSACUERDOS.PREDIO%TYPE;
MI_FECHAFACTURADO       IP_FACTURADOSACUERDOS.FECHAFACTURADO%TYPE;
MI_UCUOTA               IP_FACTURADOSACUERDOS.CUOTA%TYPE;
MI_STOTAL               IP_FACTURADOSACUERDOS.TOTAL%TYPE;
MI_SINTERES_ACUERDO     IP_FACTURADOSACUERDOS.INTERES_ACUERDO%TYPE;
MI_SINTERES_RECARGO     IP_FACTURADOSACUERDOS.INTERES_RECARGO%TYPE;
MI_SC1                  IP_FACTURADOSACUERDOS.C1%TYPE;
MI_SC2                  IP_FACTURADOSACUERDOS.C2%TYPE;
MI_SC3                  IP_FACTURADOSACUERDOS.C3%TYPE;
MI_SC4                  IP_FACTURADOSACUERDOS.C4%TYPE;
MI_SC5                  IP_FACTURADOSACUERDOS.C5%TYPE;
MI_SC6                  IP_FACTURADOSACUERDOS.C6%TYPE;
MI_SC7                  IP_FACTURADOSACUERDOS.C7%TYPE;
MI_SC8                  IP_FACTURADOSACUERDOS.C8%TYPE;
MI_SC9                  IP_FACTURADOSACUERDOS.C9%TYPE;
MI_SC10                 IP_FACTURADOSACUERDOS.C10%TYPE;
MI_SC11                 IP_FACTURADOSACUERDOS.C11%TYPE;
MI_SC12                 IP_FACTURADOSACUERDOS.C12%TYPE;
MI_SC13                 IP_FACTURADOSACUERDOS.C13%TYPE;
MI_SC14                 IP_FACTURADOSACUERDOS.C14%TYPE;
MI_SC15                 IP_FACTURADOSACUERDOS.C15%TYPE;
MI_SC16                 IP_FACTURADOSACUERDOS.C16%TYPE;
MI_SC17                 IP_FACTURADOSACUERDOS.C17%TYPE;
MI_SC18                 IP_FACTURADOSACUERDOS.C18%TYPE;
MI_SC19                 IP_FACTURADOSACUERDOS.C19%TYPE;
MI_SC20                 IP_FACTURADOSACUERDOS.C20%TYPE;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_CONSECUTIVOREAL      IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
MI_PREANOINI            IP_FACTURADOSACUERDOS.PREANOI%TYPE;
MI_UPREANO              IP_FACTURADOSACUERDOS.PREANO%TYPE;
MI_PREVAL               IP_RECIBOS_DE_PAGO.PREVAL%TYPE;
MI_IMPRIMIRFACTURA      PCK_SUBTIPOS.TI_ENTERO;
STR_NUMRECIBO           VARCHAR(1000 CHAR);
TIPOFRA                 VARCHAR(3 CHAR);
MI_NUMEROORDEN          IP_ACUERDOS.NUMERO_ORDEN%TYPE;
MI_DOCNUM               IP_RECIBOS_DE_PAGO.DOCNUM%TYPE;
MI_CALCULO              PCK_SUBTIPOS.TI_ENTERO;

BEGIN
  IF PCK_PREDIAL.FC_PERMISOACCION(UN_COMPANIA => UN_COMPANIA,
                                  UN_MODULO => PCK_DATOS.MODULOPREDIAL,
                                  UN_ACCION => 'USUARIOS QUE GENERAN FACTURACION', 
                                  UN_USUARIO => UN_USUARIO)= 1 OR PCK_PREDIAL.FC_PERMISOACCION(UN_COMPANIA => UN_COMPANIA,
                                                                                               UN_MODULO => PCK_DATOS.MODULOPREDIAL,
                                                                                               UN_ACCION => 'USUARIOS DE PROCESOS QUE GENERAN FACTURACION', 
                                                                                               UN_USUARIO => UN_USUARIO)=1 
  THEN



    SELECT MAX(FACTURADOSACUERDOS.CUOTA) CUOTA,
           MAX(FACTURADOSACUERDOS.FECHAFACTURADO) FECHA
    INTO   MI_CUOTA, 
           MI_FECHA
    FROM IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
    WHERE FACTURADOSACUERDOS.CODIGOACUERDO = UN_CODIGOACUERDO;

    IF UN_FECHAVENCIDA = 1 THEN
        MI_FECHALIMITE:=SYSDATE;
    ELSE 
      MI_FECHALIMITE:=MI_FECHA;
    END IF;

    IF UN_ANULADO <> 0 THEN
      BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACUERDOANULADO
                          );
      END;
    END IF;

    IF UN_CANCELADO <> 0 THEN 
      BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACUERDOANULADO
                          );
      END;
    END IF;

    SELECT   PAGADO
    INTO     MI_PAGADO
    FROM     IP_FACTURADOSACUERDOS FACTURADOSACUERDOS 
    WHERE    CODIGOACUERDO = UN_CODIGOACUERDO
      AND CUOTA = UN_CUOTA;

    IF MI_PAGADO <> 0 THEN 
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_CUOTAPAGADA
                          );
        END;
    ELSE
      MI_STRSQL:= ' SELECT *
                    FROM   IP_FACTURADOSACUERDOS FACTURADOSACUERDOS 
                    WHERE  CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''
                      AND  CUOTA         < '''||UN_CUOTA||''' 
                      AND  PAGADO        = 0';
      EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;

      IF MI_CONTEO >= 1 THEN 
        IF UN_FACTURACIONCUOTAANTERIOR = 1 THEN
          MI_IMPRIMIRFACTURA := 1;
        ELSE
          MI_IMPRIMIRFACTURA := 0;
        END IF;
      ELSE
        MI_IMPRIMIRFACTURA := 1;
      END IF;

      IF MI_IMPRIMIRFACTURA = 1 THEN

          SELECT  PREDIO,
                  MAX(FECHAFACTURADO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) FECHAFACTURADO,
                  MAX(CUOTA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) CUOTA,
                  SUM(TOTAL) STOTAL,
                  SUM(INTERES_ACUERDO) SINTERES_ACUERDO,
                  SUM(INTERES_RECARGO) SINTERES_RECARGO,
                  SUM(C1)   SC1,
                  SUM(C2)   SC2,
                  SUM(C3)   SC3,
                  SUM(C4)   SC4,
                  SUM(C5)   SC5,
                  SUM(C6)   SC6,
                  SUM(C7)   SC7,
                  SUM(C8)   SC8,
                  SUM(C9)   SC9,
                  SUM(C10)  SC10,
                  SUM(C11)  SC11,
                  SUM(C12)  SC12,
                  SUM(C13)  SC13,
                  SUM(C14)  SC14,
                  SUM(C15)  SC15,
                  SUM(C16)  SC16,
                  SUM(C17)  SC17,
                  SUM(C18)  SC18,
                  SUM(C19)  SC19,
                  SUM(C20)  SC20
          INTO    MI_PREDIO,
                  MI_FECHAFACTURADO,
                  MI_UCUOTA,
                  MI_STOTAL,
                  MI_SINTERES_ACUERDO,
                  MI_SINTERES_RECARGO,
                  MI_SC1,
                  MI_SC2,
                  MI_SC3,
                  MI_SC4,
                  MI_SC5,
                  MI_SC6,
                  MI_SC7,
                  MI_SC8,
                  MI_SC9,
                  MI_SC10,
                  MI_SC11,
                  MI_SC12,
                  MI_SC13,
                  MI_SC14,
                  MI_SC15,
                  MI_SC16,
                  MI_SC17,
                  MI_SC18,
                  MI_SC19,
                  MI_SC20
                FROM IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
                WHERE CODIGOACUERDO= UN_CODIGOACUERDO
                  AND PREDIO         = UN_CODIGOPREDIO
                  AND CUOTA         <= UN_CUOTA
                  AND PAGADO         = 0
                  GROUP BY PREDIO;

        IF UN_CONTROLARRECIBOS= 1 THEN 
          IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'HABILITA PAGO CUOTA VIGENCIA ANTERIOR', 
                                   UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                   UN_FECHA_PAR => SYSDATE)= 'SI' THEN 
            MI_CAMPOS := 'ANULADO        = -1, 
                          FECHAANULACION = TO_DATE(SYSDATE, ''DD/MM/YYYY HH24:MI:SS''),
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = '''||UN_USUARIO||'''';
            MI_CONDICION := ' PRECOD        = '''||MI_PREDIO||''' 
                              AND PAGO      = 0 
                              AND ANULADO   = 0  
                              AND ESACUERDO <> 0';
            BEGIN 
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_RECIBOS_DE_PAGO', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTRECIBOSPAGO
                           );
            END;
          END IF;     
        END IF;

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJA NUMERACION UNICA', 
                                 UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                 UN_FECHA_PAR => SYSDATE)= 'SI' THEN
          TIPOFRA := 'N';
        ElSE
          TIPOFRA := 'A';
        END IF;

        BEGIN
          BEGIN
            WITH NUMEROSDEFACTURA AS (
              SELECT    CONSECUTIVOREAL
              FROM     IP_NUMEROSDEFACTURA 
              WHERE    COMPANIA = UN_COMPANIA
                AND    TIPO = 'A'
                AND    ACTIVO NOT IN (0) 
              ORDER BY  SECUENCIA DESC)
            SELECT CONSECUTIVOREAL
            INTO MI_CONSECUTIVOREAL
            FROM NUMEROSDEFACTURA
            WHERE ROWNUM=1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_NUMFACTURA
                        );
        END;

        STR_NUMRECIBO := NVL(MI_CONSECUTIVOREAL,1)+1;

        MI_CAMPOS := 'CONSECUTIVOREAL = ''' || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                                         UN_LONGITUD  =>  9)|| '''
                      , DATE_MODIFIED = SYSDATE
                      , MODIFIED_BY = '''||UN_USUARIO||''' ';

        MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                        AND TIPO = ''' || TIPOFRA || '''
                        AND ACTIVO = -1 ';
        BEGIN 
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_NUMEROSDEFACTURA', 
                                                   UN_ACCION    =>  'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS, 
                                                   UN_CONDICION =>  MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTNUMFACTURA
                        );
        END;

        MI_STRSQL := 'SELECT *  
                      FROM   IP_RECIBOS_DE_PAGO 
                      WHERE  DOCNUM= '''|| PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                                     UN_LONGITUD  =>  9)||'''';
        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;

        IF MI_CONTEO >= 1 THEN
          BEGIN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
            END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD =>SQLCODE,
                              UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_NUMRECIBO
                            );
          END;
        END IF;

            MI_SC1 :=NVL(MI_SC1, 0);
            MI_SC2 :=NVL(MI_SC2, 0);
            MI_SC3 :=NVL(MI_SC3, 0);
            MI_SC4 :=NVL(MI_SC4, 0);
            MI_SC5 :=NVL(MI_SC5, 0);
            MI_SC6 :=NVL(MI_SC6, 0);
            MI_SC7 :=NVL(MI_SC7, 0);
            MI_SC8 :=NVL(MI_SC8, 0);
            MI_SC9 :=NVL(MI_SC9, 0);
            MI_SC10 :=NVL(MI_SC10, 0);
            MI_SC11 := NVL(MI_SC11, 0);
            MI_SC12 := NVL(MI_SC12, 0);

            SELECT MIN(PREANOI) PREANOINI, MAX(PREANO) UPREANO
            INTO MI_PREANOINI, MI_UPREANO
            FROM IP_FACTURADOSACUERDOS 
            WHERE CODIGOACUERDO= UN_CODIGOACUERDO 
              AND CUOTA <= MI_CUOTA  
              AND PAGADO = 0;

            IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'HABILITA PAGO CUOTA VIGENCIA ANTERIOR', 
                                     UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                     UN_FECHA_PAR => SYSDATE)= 'SI' THEN 
              IF MI_PREANOINI IS NOT NULL AND  MI_UPREANO IS NOT NULL THEN
                IF NVL(MI_UPREANO, 0) = EXTRACT (YEAR FROM SYSDATE) - 1 THEN
                    MI_SC5 := NVL(MI_SC1, 0);
                    MI_SC6 := NVL(MI_SC2, 0);
                    MI_SC7 := NVL(MI_SC3, 0);
                    MI_SC8 := NVL(MI_SC4, 0);
                    MI_SC1 := 0;
                    MI_SC2 := 0;
                    MI_SC3 := 0;
                    MI_SC4 := 0;
                ELSE
                  IF NVL(MI_UPREANO, 0) < EXTRACT (YEAR FROM SYSDATE) - 1 THEN 
                    MI_SC9 := NVL(MI_SC1, 0);
                    MI_SC10 := NVL(MI_SC2, 0);
                    MI_SC11 := NVL(MI_SC3, 0);
                    MI_SC12 := NVL(MI_SC4, 0);
                    MI_SC1 := 0;
                    MI_SC2 := 0;
                    MI_SC3 := 0;
                    MI_SC4 := 0;
                  END IF;
                END IF;
              END IF;
            END IF;

            SELECT  NUMERO_ORDEN 
            INTO    MI_NUMEROORDEN
            FROM  IP_ACUERDOS
            WHERE COMPANIA      = UN_COMPANIA
              AND CODIGOACUERDO = UN_CODIGOACUERDO
              AND PREDIO        = MI_PREDIO;

            IF MI_CONTEO = 0 THEN
              MI_CAMPOS := 'PRECOD,PREANOI,PREANO,PREFEC,DOCNUM,PREVAL,PREFECLIM, FECHAFACACUERDO, PREUSU,
                            C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, 
                            C20, PAGO, ANULADO, ESACUERDO,NCUOTA_ACUERDO, INTERES_ACUERDO, INTERES_RECARGO, 
                            ACUERDO, IND_MULTIFECHAS, COMPANIA, NUMERO_ORDEN, PREANOF, CREATED_BY, DATE_CREATED';
              MI_VALORES := ''''||MI_PREDIO||''', 
                            NVL('||MI_PREANOINI||', 0), 
                            NVL('||MI_UPREANO||', 0), 
                            TO_DATE(''' || TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') ||''', ''DD/MM/YYYY HH24:MI:SS''), 
                            '''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                           UN_LONGITUD  =>  9)||''', 
                            '||MI_STOTAL||', 
                            TO_DATE('''||TO_CHAR(MI_FECHALIMITE,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                            TO_DATE('''||TO_CHAR(MI_FECHAFACTURADO,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                            '''||UN_USUARIO||''', 
                            '||MI_SC1||', 
                            '||MI_SC2||',
                            '||MI_SC3||',
                            '|| MI_SC4||',
                            '||MI_SC5||',
                            '||MI_SC6||',
                            '||MI_SC7||',
                            '||MI_SC8||',
                            '|| MI_SC9||',
                            '||MI_SC10||',
                            '||MI_SC11||',
                            '||MI_SC12||',
                            NVL('||MI_SC13||', 0), 
                            NVL('||MI_SC14||', 0),
                            NVL('||MI_SC15||', 0),
                            NVL('||MI_SC16||', 0),
                            NVL('||MI_SC17||', 0),
                            NVL('||MI_SC18||', 0),
                            NVL('||MI_SC19||', 0),
                            NVL('||MI_SC20||', 0),
                            0, 
                            0, 
                            -1, 
                            '''||MI_UCUOTA||''', 
                            NVL('||MI_SINTERES_ACUERDO||', 0), 
                            NVL('||MI_SINTERES_RECARGO||', 0), 
                            NVL('''||UN_CODIGOACUERDO||''',''0''),
                            0,
                            '''||UN_COMPANIA||''',
                            '''||MI_NUMEROORDEN||''',
                            0,
                            '''||UN_USUARIO||''',
                            TO_DATE('''||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')';

              BEGIN 
                BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'IP_RECIBOS_DE_PAGO', 
                                                         UN_ACCION  =>  'I', 
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_INSRECIBOPAGO
                            );
              END;

              MI_CAMPOS := 'DOCNUM = ''' || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                                      UN_LONGITUD  =>  9)||'''
                          , DATE_MODIFIED = SYSDATE
                          , MODIFIED_BY = '''||UN_USUARIO||'''                                                                                                    ';
              MI_CONDICION := 'CODIGOACUERDO      = '''||UN_CODIGOACUERDO||'''
                               AND CUOTA          <= '''||UN_CUOTA||'''
                               AND NVL(PAGADO,0)  = 0';
              BEGIN 
                BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_FACTURADOSACUERDOS', 
                                                         UN_ACCION    =>  'M', 
                                                         UN_CAMPOS    =>  MI_CAMPOS, 
                                                         UN_CONDICION =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTFACACUERDO
                              );
              END;
              FOR MI_RS IN (SELECT CUOTA CUOTAAF,TOTAL TOTALAF,C1 C1AF,C2 C2AF,C3 C3AF,C4 C4AF,C5 C5AF,C6 C6AF,C7 C7AF,
              C8 C8AF,C9 C9AF,C10 C10AF,C11 C11AF,C12 C12AF,C13 C13AF,C14 C14AF,C15 C15AF,C16 C16AF,C17 C17AF,C18 C18AF,C19 C19AF,C20 C20AF, 
              INTERES_ACUERDO INTERES_ACUERDOAF, INTERES_RECARGO INTERES_RECARGOAF 
                          FROM   IP_FACTURADOSACUERDOS FACTURADOSACUERDOS 
                          WHERE  CODIGOACUERDO = UN_CODIGOACUERDO
                          AND  CUOTA         <= UN_CUOTA 
                          AND  PAGADO        = 0)
              LOOP
                  MI_CAMPOS :=  'COMPANIA,
                              CUOTA,
                              TOTAL,
                              INTERES_ACUERDO,
                              INTERES_RECARGO,
                              DOCNUM,
                              PREANO,
                              CONSECUTIVO,
                              C1,
                              C2,
                              C3,
                              C4,
                              C5,
                              C6,
                              C7,
                              C8,
                              C9,
                              C10,
                              C11,
                              C12,
                              C13,
                              C14,
                              C15,
                              C16,
                              C17,
                              C18,
                              C19,
                              C20,
                              CREATED_BY, 
                              DATE_CREATED';
                MI_VALORES:=''''||UN_COMPANIA||''', 
                            '''||MI_RS.CUOTAAF||''', 
                            '''||MI_RS.TOTALAF||''',
                            '''||MI_RS.INTERES_ACUERDOAF||''',
                            '''||MI_RS.INTERES_RECARGOAF||''',
                            '''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                           UN_LONGITUD  =>  9)||''',
                            NVL('||MI_UPREANO||', 0),
                            '||PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  'IP_DETALLE_RECIBOPAGO', 
                                                                UN_CRITERIO =>  'COMPANIA ='''||UN_COMPANIA||''' 
                                                                                AND DOCNUM='''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                                                                                          UN_LONGITUD  =>  9)||'''', 
                                                                UN_CAMPO    =>  'CONSECUTIVO')||',
                            '||MI_RS.C1AF||',
                            '||MI_RS.C2AF||',
                            '||MI_RS.C3AF||',
                            '||MI_RS.C4AF||',
                            '||MI_RS.C5AF||',
                            '||MI_RS.C6AF||',
                            '||MI_RS.C7AF||',
                            '||MI_RS.C8AF||',
                            '||MI_RS.C9AF||',
                            '||MI_RS.C10AF||',
                            '||MI_RS.C11AF||',
                            '||MI_RS.C12AF||',
                            '||MI_RS.C13AF||',
                            '||MI_RS.C14AF||',
                            '||MI_RS.C15AF||',
                            '||MI_RS.C16AF||',
                            '||MI_RS.C17AF||',
                            '||MI_RS.C18AF||',
                            '||MI_RS.C19AF||',
                            '||MI_RS.C20AF||',
                            '''||UN_USUARIO||''',
                            TO_DATE('''||TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')';
               BEGIN 
                  BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'IP_DETALLE_RECIBOPAGO', 
                                                           UN_ACCION  =>  'I', 
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_INSDETRECIBOPAGO
                              );
                END;
              END LOOP;


            END IF;
            SELECT  PREVAL 
            INTO    MI_PREVAL 
            FROM IP_RECIBOS_DE_PAGO 
            WHERE DOCNUM = PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                     UN_LONGITUD  =>  9);

            MI_CAMPOS := 'FACTURA_ACUERDO =  ''' || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                                              UN_LONGITUD  =>  9) || ''' , 
                          TOTAL_ACUERDO   = '''||MI_PREVAL|| ''',
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = '''||UN_USUARIO||'''';
            MI_CONDICION := 'CODIGO = ''' ||MI_PREDIO||'''';
            BEGIN 
              BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_USUARIOS_PREDIAL', 
                                                     UN_ACCION    =>  'M', 
                                                     UN_CAMPOS    =>  MI_CAMPOS, 
                                                     UN_CONDICION =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTACUERUSUARIO
                            );
              END;

            MI_CAMPOS := 'NUMERO_FACTURA  =  '''||999999999||''' , 
                          RECIBO_ACTUAL   = '''||999999999|| ''' ,
                          DATE_MODIFIED = SYSDATE ,
                          MODIFIED_BY = '''||UN_USUARIO||'''';
            MI_CONDICION := 'CODIGO = ''' ||MI_PREDIO||'''';
            BEGIN 
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'IP_USUARIOS_PREDIAL', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_PREDIAL_ACTACUERUSUARIO
                            );
            END;
            MI_DOCNUM:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    =>  STR_NUMRECIBO,
                                                 UN_LONGITUD  =>  9);    
      ELSE
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_PROCESOCANCELADO
                          );
        END;
      END IF;
    END IF;
  ELSE
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
      END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>SQLCODE,
                        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_NOPERMISOS
                      );
    END;
  END IF;
  RETURN MI_DOCNUM;
END FC_IMPRIMIRCUOTAACUERDODEPAGO;

--2
FUNCTION FC_VERIFICARFEHALIMITECUOTA
/*
      NAME              : FC_VERIFICARFEHALIMITECUOTA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION  QUE VERIFICA SI LA FECHA DE PAGO DE LA CUOTA DE UN ACUERDO YA SE VENCIO
      PARAMETERS        : 
                          UN_CODIGOACUERDO	=> CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR
                          UN_CUOTA				  => CUOTA POR LA CUAL SE VA A FILTRAR   

      @NAME: verificarFechaLimiteCuota
      @METHOD:  GET
    */ 
(
  UN_CODIGOACUERDO IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_CUOTA         IN IP_FACTURADOSACUERDOS.CUOTA%TYPE
)
RETURN NUMBER
AS 
MI_RETORNO        PCK_SUBTIPOS.TI_ENTERO:=0;
MI_CUOTA          VARCHAR2(32000 CHAR);
MI_FECHA          DATE;
BEGIN
  SELECT FACTURADOSACUERDOS.CUOTA CUOTA,
         FACTURADOSACUERDOS.FECHAFACTURADO FECHA
  INTO   MI_CUOTA, 
         MI_FECHA
  FROM IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
  WHERE FACTURADOSACUERDOS.CODIGOACUERDO = UN_CODIGOACUERDO
    AND CUOTA                            = UN_CUOTA;

  IF MI_FECHA < SYSDATE THEN
    MI_RETORNO := 1;
  END IF;
  RETURN MI_RETORNO;
END;

--3
FUNCTION FC_VERIFICARPAGOCUOTAANTERIOR
/*
      NAME              : FC_VERIFICARPAGOCUOTAANTERIOR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION  QUE VERIFICA SI EXISTEN CUOTAS PENDIENTES A PARTIR DE LA MAXIMA CUOTA
      PARAMETERS        : 
                          UN_CODIGOACUERDO	=> CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR  
      @NAME: verificarPagoCuotaAnterior
      @METHOD:  GET
    */ 
(
  UN_CODIGOACUERDO IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE
)
RETURN NUMBER
AS 
MI_RETORNO        PCK_SUBTIPOS.TI_ENTERO:=0;
MI_CUOTA          VARCHAR2(32000 CHAR);
MI_FECHA          DATE;
MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
BEGIN  
   SELECT MAX(FACTURADOSACUERDOS.CUOTA) CUOTA,
          MAX(FACTURADOSACUERDOS.FECHAFACTURADO) FECHA
  INTO    MI_CUOTA, 
          MI_FECHA
  FROM IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
  WHERE FACTURADOSACUERDOS.CODIGOACUERDO = UN_CODIGOACUERDO;

  MI_STRSQL:= 'SELECT *
               FROM   IP_FACTURADOSACUERDOS FACTURADOSACUERDOS 
               WHERE  CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''
                AND   CUOTA         < '''||MI_CUOTA||''' 
                AND   PAGADO        = 0';
   EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;
   IF MI_CONTEO >=1 THEN 
    MI_RETORNO:=1;
   END IF;

   RETURN MI_RETORNO;
END;

--4
FUNCTION FC_VERIFICARFACCUOTAANT
/*
      NAME              : FC_VERIFICARFACCUOTAANT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION  QUE VERIFICA SI EXISTEN CUOTAS PENDIENTES A PARTIR DE LA CUOTA INGRESADA POR PARAMETRO
      PARAMETERS        : 
                          UN_CODIGOACUERDO	=> CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR
                          UN_CUOTA				  => CUOTA POR LA CUAL SE VA A FILTRAR

      @NAME: verificarFacCuotaAnterior
      @METHOD:  GET
    */ 
(
  UN_CODIGOACUERDO IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_CUOTA         IN IP_FACTURADOSACUERDOS.CUOTA%TYPE
)
RETURN NUMBER
AS 
MI_RETORNO        PCK_SUBTIPOS.TI_ENTERO:=0;
MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
BEGIN
   MI_STRSQL:= 'SELECT *
                FROM   IP_FACTURADOSACUERDOS FACTURADOSACUERDOS 
                WHERE  CODIGOACUERDO  = '''||UN_CODIGOACUERDO||'''
                  AND  CUOTA          < '''||UN_CUOTA||''' 
                  AND  PAGADO         = 0';
  EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;
  IF MI_CONTEO >= 1 THEN 
    MI_RETORNO:=1;
   END IF;
   RETURN MI_RETORNO;
END;

--5
FUNCTION FC_VERIFICARANULRECPENDIENTE
/*
      NAME              : FC_VERIFICARANULRECPENDIENTE
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION QUE VERIFICA SI EXISTEN RECIBOS PENDIENTES POR ANULAR
      PARAMETERS        : 
                          UN_CODIGOACUERDO	=> CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR
                          UN_CODIGOPREDIO		=> CODIGO DEL PREDIO POR EL CUAL SE VA A FILTRAR
                          UN_COMPANIA       => COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_CUOTA				  => CUOTA POR LA CUAL SE VA A FILTRAR

      @NAME: verificarAnulacionReciboPendiente
      @METHOD:  GET
    */ 
(
  UN_CODIGOACUERDO  IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_CODIGOPREDIO   IN IP_FACTURADOSACUERDOS.PREDIO%TYPE,
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CUOTA          IN IP_FACTURADOSACUERDOS.CUOTA%TYPE
)
RETURN NUMBER
AS 
MI_RETORNO        PCK_SUBTIPOS.TI_ENTERO:=0;
MI_CUOTA          VARCHAR2(32000 CHAR);
MI_PREDIO         VARCHAR2(32000 CHAR);
MI_FECHA          DATE;
MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
MI_FECHAFACTURADO IP_FACTURADOSACUERDOS.FECHAFACTURADO%TYPE;
MI_UCUOTA         IP_FACTURADOSACUERDOS.CUOTA%TYPE;

BEGIN
  SELECT MAX(FACTURADOSACUERDOS.CUOTA) CUOTA,
         MAX(FACTURADOSACUERDOS.FECHAFACTURADO) FECHA
  INTO   MI_CUOTA, 
         MI_FECHA
  FROM IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
  WHERE FACTURADOSACUERDOS.CODIGOACUERDO = UN_CODIGOACUERDO;

    SELECT  PREDIO,
            MAX(FECHAFACTURADO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) FECHAFACTURADO,
            MAX(CUOTA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) CUOTA
    INTO    MI_PREDIO,
            MI_FECHAFACTURADO,
            MI_UCUOTA
    FROM    IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
    WHERE CODIGOACUERDO  =  UN_CODIGOACUERDO
      AND PREDIO         =  UN_CODIGOPREDIO
      AND CUOTA          <= MI_CUOTA
      AND PAGADO         =  0
      GROUP BY PREDIO;

   MI_STRSQL:= 'SELECT    DOCNUM
                FROM      IP_RECIBOS_DE_PAGO RECIBOS_DE_PAGO 
                WHERE     PRECOD = '''||MI_PREDIO||''' 
                AND CASE WHEN PAGO      = NULL THEN 0 ELSE PAGO END        = 0 
                AND CASE WHEN ANULADO   = NULL THEN 0 ELSE ANULADO END     = 0 
                AND CASE WHEN ESACUERDO = NULL THEN 0 ELSE  ESACUERDO END  <> 0 
                AND NCUOTA_ACUERDO <= ' || UN_CUOTA;
  EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;

  IF MI_CONTEO >= 1 THEN
     MI_RETORNO:=1;
  END IF;
  RETURN  MI_RETORNO;
END FC_VERIFICARANULRECPENDIENTE;

--6
FUNCTION FC_CUOTASRECIBOSIMP
/*
      NAME              : FC_CUOTASRECIBOSIMP
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     :
      DESCRIPTION       : FUNCION QUE REALIZA LOS CALCULOS CORRESPONDIENTES PARA LA IMPRESION DEL RECIBO DE PAGO DE IMPUESTO PREDIAL. 
      PARAMETERS        :     


      @NAME: obtenerCuotasRecibos
      @METHOD:  GET
    */ 
(
	UN_ACUERDO  IN IP_RECIBOS_DE_PAGO.ACUERDO%TYPE,
	UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_RECIBO   IN PCK_SUBTIPOS.TI_DOCNUM
)
RETURN VARCHAR2
AS
	    MI_RETORNO      VARCHAR2(50 CHAR);
      MI_CUOTAMIN     IP_FACTURADOSACUERDOS.CUOTA%TYPE; 
      MI_CUOTAMAX     IP_FACTURADOSACUERDOS.CUOTA%TYPE;
BEGIN
 BEGIN


  SELECT   MIN(FACTURADOSACUERDOS.CUOTA), MAX(FACTURADOSACUERDOS.CUOTA)
  INTO     MI_CUOTAMIN, MI_CUOTAMAX
  FROM    IP_FACTURADOSACUERDOS  FACTURADOSACUERDOS 
  WHERE  FACTURADOSACUERDOS.CODIGOACUERDO = UN_ACUERDO
    AND  FACTURADOSACUERDOS.PREDIO        = UN_PREDIO
    AND  FACTURADOSACUERDOS.DOCNUM        = UN_RECIBO;

  MI_RETORNO:=MI_CUOTAMIN || ' - ' || MI_CUOTAMAX;

  IF MI_CUOTAMIN = MI_CUOTAMAX THEN
    MI_RETORNO:=MI_CUOTAMIN;
  END IF;

	RETURN MI_RETORNO;

     EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
 END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACUERDOVIG
         );
END FC_CUOTASRECIBOSIMP;

--7

FUNCTION FC_REMPLAZATARIFAS 
/*
      NAME              : FC_REMPLAZATARIFAS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN SEBASTIAN FORERO NOGUERA 
      DATE MIGRADOR     : 17/02/2017
      TIME              : 12:15 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : FUNCION QUE REMPLAZA LAS TARIFAS DE UN AÑO POR LAS DE OTRO AÑO SELECCIONADO Y RETORNA EL NUMERO DE FILAS INSERTADAS
      PARAMETERS        :     UN_COMPANIA        => COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                              UN_ANOREMPLAZADO   => AÑO DEL CUAL LAS TARIFAS VAN A SER REMPLAZADAS
                              UN_ANOANTERIOR     => AÑO DEL CUAL VAN A VENIR LOS VALORES DE LAS TARIFAS PARA REMPLAZAR      
                              UN_USUARIO         => USUARIO QUE HACE LA MODIFICAION  
                              UN_INCREMENTO      => INCREMENTO PARA ALMACENAR LAS TARFIAS    
      @NAME            : reemplazarTarifas
      @METHOD:  GET
    */ 
(
UN_COMPANIA                   IN PCK_SUBTIPOS.TI_COMPANIA,
UN_ANOREMPLAZADO              IN PCK_SUBTIPOS.TI_ANIO,
UN_ANOANTERIOR                IN PCK_SUBTIPOS.TI_ANIO,
UN_USUARIO                    IN PCK_SUBTIPOS.TI_USUARIO,
UN_INCREMENTO                 IN PCK_SUBTIPOS.TI_DOBLE
)
RETURN NUMBER
AS  
MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
MI_RETORNO                  NUMBER;

BEGIN  
    BEGIN
      BEGIN
            MI_CONDICION := 'COMPANIA = ''' ||UN_COMPANIA|| '''
                            AND TRPANO = '||UN_ANOREMPLAZADO||'';

            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => 'IP_TARIFAS',
                                                  UN_ACCION => 'E',
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ELIMANIOTARIFA
                            );
    END;

        MI_CAMPOS :=' AMES1,
                      AMES10,
                      AMES11,
                      AMES12,
                      AMES2,
                      AMES3,
                      AMES4,
                      AMES5,
                      AMES6,
                      AMES7,
                      AMES8,
                      AMES9,
                      BASECAR,
                      COMPANIA,
                      CONTROL,
                      CREATED_BY,
                      DATE_CREATED,
                      GRUPO_TARIFA,
                      IAMES1,
                      IAMES10,
                      IAMES11,
                      IAMES12,
                      IAMES2,
                      IAMES3,
                      IAMES4,
                      IAMES5,
                      IAMES6,
                      IAMES7,
                      IAMES8,
                      IAMES9,
                      INTCAR,
                      INTCARVAL,
                      PORCBOMB,
                      PORCDESCUENTOS,
                      SOBRETASA,
                      TARTRP,
                      TIPO_PREDIO,
                      TRPALUMBRADO,
                      TRPANO,
                      TRPCAR,
                      TRPCARVAL,
                      TRPCOD,
                      TRPDES,
                      TRPFOR,
                      TRPINC,
                      TRPINT,
                      TRPINTVAL,
                      TRPPOR,
                      TRPRAN,
                      TRPRAN1,
                      TRPRAN2,
                      TRPRUR,
                      TRPURB,
                      TRPVAL ';

        MI_VALORES := 'SELECT AMES1,
                              AMES10,
                              AMES11,
                              AMES12,
                              AMES2,
                              AMES3,
                              AMES4,
                              AMES5,
                              AMES6,
                              AMES7,
                              AMES8,
                              AMES9,
                              BASECAR,
                              COMPANIA,
                              CONTROL,
                              '''||UN_USUARIO||''',
                              SYSDATE,
                              GRUPO_TARIFA,
                              IAMES1,
                              IAMES10,
                              IAMES11,
                              IAMES12,
                              IAMES2,
                              IAMES3,
                              IAMES4,
                              IAMES5,
                              IAMES6,
                              IAMES7,
                              IAMES8,
                              IAMES9,
                              INTCAR,
                              INTCARVAL,
                              PORCBOMB,
                              PORCDESCUENTOS,
                              SOBRETASA,
                              TARTRP,
                              TIPO_PREDIO,
                              TRPALUMBRADO,
                              '||UN_ANOREMPLAZADO||',
                              TRPCAR,
                              TRPCARVAL,
                              TRPCOD,
                              TRPDES,
                              TRPFOR,
                              '||UN_INCREMENTO||',
                              TRPINT,
                              TRPINTVAL,
                              TRPPOR,
                              TRPRAN,
                              TRPRAN1,
                              TRPRAN2,
                              TRPRUR,
                              TRPURB,
                              TRPVAL
                        FROM IP_TARIFAS
                        WHERE COMPANIA ='''||UN_COMPANIA||'''
                        AND TRPANO     ='||UN_ANOANTERIOR||'';
    BEGIN 
        BEGIN 
            MI_RETORNO:=PCK_DATOS.FC_ACME(	UN_TABLA    => 'IP_TARIFAS',
                                            UN_ACCION   => 'IS',
                                            UN_CAMPOS   => MI_CAMPOS,
                                            UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                         
        END ;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_INSTARIFAS
                            );
    END;
     RETURN MI_RETORNO;
END FC_REMPLAZATARIFAS;

--8 -- 27/07/2017 se elimina PR_INDICAEXENTOS porque hace los mismo que PR_HABILITAR_EXENTO. Esta ultima es la que esta 
    --            siendo usada actualmente

--9 -- 27/07/2017 se elimina PR_INDICAEXENTOSANULAR porque hace los mismo que PR_DESHABILITAR_EXENTO. Esta ultima es la que esta 
    --            siendo usada actualmente

--10
FUNCTION FC_IMPORTAR_IGAC_TIPO_UNO 
/*
  NAME              : FC_IMPORTAR_IGAC_TIPO_UNO --> En access importar_IGAC_Tipo_Uno
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRACION    : 17/02/2017
  TIME              : 5:00 PM
  DESCRIPTION       : PROCEDIMIENTO QUE  AGREGA LOS VALORES DE LA TABLA IP_.
  PARAMETERS        :     UN_COMPANIA       => COMPAÑIA CON LA QUE SE LLEVA A CABO EL PROCESO.
                          UN_USUARIO        => USUARIO QUE REALIZA EL PROCESO
                          UN_FECHACORTE     => FECHA DE REALIZACION DEL PROCESO
                          UN_IND_TOTAL      => INDICADOR PARA REALIZAR EL PROCESO COMPLETO
                          UN_NOMBRECOMPANIA => NOMBRE DE LA COMPAÑIA CON EL QUE SE REALIZA EL PROCESO
  MODIFIER          : YESSICA SANA - EN PASO 6 DE 9 POR INDICACIONES SE MODIFICA RIGTH POR LEFT Y ORDEN DE LAS TABLAS.
  DATE MODIFIED     : 26/07/2017
  TIME              : 10:20 AM
  SOURCE MODULE     : PredialP2017.01.06VB
  @NAME             : importarIGACTipoUno
  @METHOD           : GET
  */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_FECHACORTE     IN DATE,
    UN_IND_TOTAL      IN PCK_SUBTIPOS.TI_LOGICO,
    UN_NOMBRECOMPANIA IN VARCHAR2 
)
RETURN PCK_SUBTIPOS.TI_LOGICO 
AS 
    MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING  PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_EXISTEN     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RTA         PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
--CONTAR PREDIOS O VALIDAR QUE EXISTAN
    BEGIN 
      SELECT COUNT(1) CODIGO  
        INTO MI_EXISTEN 
        FROM IP_USUARIOS_PREDIAL
       WHERE COMPANIA = UN_COMPANIA;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTEN:=0;
    END;
    IF MI_EXISTEN < 1 THEN 
        --Paso 4 de 9. Insertando usuarios nuevos...
        MI_TABLA:='IP_USUARIOS_PREDIAL';
        MI_CAMPOS:= '  DEPARTAMENTO
                     , MUNICIPIO
                     , CODIGO
                     , TIPO
                     , NUMERO_ORDEN
                     , NUMERO_REGISTROS
                     , NOMBRE
                     , DIGITO
                     , TIPO_NIT
                     , NIT
                     , DIRECCION
                     , DESTINO_ECONOMICO
                     , AREA_HA
                     , AREA_M2
                     , AREA_CONSTRUIDA
                     , AREA_CONST_ANT
                     , AVALUO_ANO
                     , CONDICION_TRIBUTARIA
                     , NOVEDAD
                     , RESOLUCION
                     , MUTACION
                     , FECHA_MOVIMIENTO
                     , PAGO_ANO
                     , ANO_CONSTRUCCION
                     , DATE_CREATED
                     , CREATED_BY '; 
        MI_VALORES:=' SELECT    
                            IGAC_TIPO_UNO.DEPARTAMENTO
                          , IGAC_TIPO_UNO.MUNICIPIO
                          , IGAC_TIPO_UNO.CODIGO
                          , IGAC_TIPO_UNO.TIPO
                          , IGAC_TIPO_UNO.NUMERO_ORDEN
                          , IGAC_TIPO_UNO.NUMERO_REGISTROS
                          , IGAC_TIPO_UNO.NOMBRE
                          , IGAC_TIPO_UNO.DIGITO
                          , IGAC_TIPO_UNO.TIPO_NIT
                          , IGAC_TIPO_UNO.NIT
                          , IGAC_TIPO_UNO.DIRECCION
                          , IGAC_TIPO_UNO.DESTINO_ECONOMICO
                          , IGAC_TIPO_UNO.AREA_HA
                          , IGAC_TIPO_UNO.AREA_M2
                          , IGAC_TIPO_UNO.AREA_CONSTRUIDA
                          , IGAC_TIPO_UNO.AREA_CONST_ANT
                          , IGAC_TIPO_UNO.AVALUO_ANO
                          , IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                          , IGAC_TIPO_UNO.NOVEDAD
                          , IGAC_TIPO_UNO.RESOLUCION
                          , IGAC_TIPO_UNO.MUTACION
                          , TO_DATE(FECHA_MOVIMIENTO,''DD/MM/YYYY HH24:MI:SS'') EXPR1
                          , EXTRACT ( YEAR FROM TO_DATE(FECHA_MOVIMIENTO,''DD/MM/YYYY HH24:MI:SS''))-1
                          , CASE WHEN IGAC_TIPO_UNO.AREA_CONSTRUIDA NOT IN (0)
                                 THEN EXTRACT (YEAR FROM TO_DATE(FECHA_MOVIMIENTO,''DD/MM/YYYY HH24:MI:SS''))
                                 ELSE 0
                            END
                          , SYSDATE
                          ,'''||UN_USUARIO||'''    
                     FROM IP_IGAC_TIPO_UNO IGAC_TIPO_UNO 
                    WHERE COMPANIA = '''||UN_COMPANIA||'''';
        BEGIN 
            BEGIN 
                MI_RTA := PCK_DATOS.FC_ACME (
                                              UN_TABLA   => MI_TABLA 
                                             ,UN_ACCION  => 'IS'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES 
                                             );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            --ERROR Insertando usuarios nuevos ----
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_USUA_NUEVO
                       ,UN_TABLAERROR => MI_TABLA 
                        );
        END;
        IF MI_RTA IN (0) THEN 
           --No existen usuarios, pero no se pudo insertar usuarios nuevos. Por favor, consulte con el proveedor del programa 
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_NOEXIS_USUA_NUEVO
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
        END IF;
    ELSE
        --Paso 5 de 9. Actualizando predios eliminados sin deuda..."
        MI_TABLA:='IP_USUARIOS_PREDIAL';
        MI_MERGEUSING:= 'SELECT 
                                  IP_USUARIOS_PREDIAL.COMPANIA
                                 ,IP_USUARIOS_PREDIAL.CODIGO
                                 ,IP_USUARIOS_PREDIAL.NUMERO_ORDEN
                            FROM IP_USUARIOS_PREDIAL
                                LEFT JOIN IP_IGAC_TIPO_UNO 
                                    ON  IP_USUARIOS_PREDIAL.COMPANIA     = IP_IGAC_TIPO_UNO.COMPANIA 
                                    AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_IGAC_TIPO_UNO.NUMERO_ORDEN 
                                    AND IP_USUARIOS_PREDIAL.CODIGO       = IP_IGAC_TIPO_UNO.CODIGO 
                           WHERE IP_USUARIOS_PREDIAL.COMPANIA = '''||UN_COMPANIA||'''
                             AND IP_IGAC_TIPO_UNO.CODIGO      IS NULL 
                             AND IP_USUARIOS_PREDIAL.PAGO_ANO = EXTRACT(YEAR FROM SYSDATE) ';
        MI_MERGEENLACE:='      TABLA.COMPANIA     = VISTA.COMPANIA 
                           AND TABLA.NUMERO_ORDEN = VISTA.NUMERO_ORDEN 
                           AND TABLA.CODIGO       = VISTA.CODIGO   ';
        MI_MERGEEXISTE:=' UPDATE SET 
                              TABLA.INDBORRADO    = -1
                             ,TABLA.FECHABORRADO  = TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')
                             ,TABLA.BORRADOPOR    = '''||UN_USUARIO||''' 
                             ,TABLA.MODIFIED_BY   = '''||UN_USUARIO||''' 
                             ,TABLA.DATE_MODIFIED = SYSDATE  ';
        BEGIN 
          BEGIN
              MI_RTA := PCK_DATOS.FC_ACME (
                                            UN_TABLA       => MI_TABLA 
                                           ,UN_ACCION      => 'MM'
                                           ,UN_MERGEUSING  => MI_MERGEUSING
                                           ,UN_MERGEENLACE => MI_MERGEENLACE
                                           ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                           );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;                  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN  
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACT_PRED_SINDEUDA
                       ,UN_TABLAERROR => MI_TABLA
                      );
        END;
        IF UN_IND_TOTAL NOT IN (0) THEN 
            --Paso 6 de 9. Verificando cambios en los datos anteriores...
            BEGIN 
                FOR MI_RS IN ( 
                    SELECT 
                         IP_USUARIOS_PREDIAL.COMPANIA
                        ,IP_USUARIOS_PREDIAL.CODIGO
                        ,IP_USUARIOS_PREDIAL.NUMERO_ORDEN
                        ,IP_USUARIOS_PREDIAL.PAIS  
                        ,IP_USUARIOS_PREDIAL.DEPARTAMENTO
                        ,IP_USUARIOS_PREDIAL.MUNICIPIO
                        ,IP_USUARIOS_PREDIAL.TIPO
                        ,NVL(IP_USUARIOS_PREDIAL.NUMERO_REGISTROS,' ') NUMERO_REGISTROS
                        ,IP_USUARIOS_PREDIAL.NOMBRE 
                        ,IP_USUARIOS_PREDIAL.DIRECCION 
                        ,IP_USUARIOS_PREDIAL.DIGITO
                        ,IP_USUARIOS_PREDIAL.TIPO_NIT
                        ,IP_USUARIOS_PREDIAL.NIT
                        ,IP_USUARIOS_PREDIAL.DESTINO_ECONOMICO
                        ,NVL(IP_USUARIOS_PREDIAL.AREA_HA,0) AREA_HA
                        ,NVL(IP_USUARIOS_PREDIAL.AREA_M2,0) AREA_M2
                        ,NVL(IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA,0) AREA_CONSTRUIDA
                        ,IP_USUARIOS_PREDIAL.AREA_CONST_ANT
                        ,NVL(IP_USUARIOS_PREDIAL.AVALUO_ANO,0) AVALUO_ANO
                        ,NVL(IP_USUARIOS_PREDIAL.CONDICION_TRIBUTARIA,' ') CONDICION_TRIBUTARIA
                        ,CASE WHEN NVL(IP_USUARIOS_PREDIAL.NOVEDAD,' ') = ' '
                              THEN ' ' 
                              ELSE IP_USUARIOS_PREDIAL.NOVEDAD
                         END NOVEDAD
                        ,IP_USUARIOS_PREDIAL.RESOLUCION
                        ,IP_USUARIOS_PREDIAL.MUTACION
                        ,IP_USUARIOS_PREDIAL.FECHA_MOVIMIENTO
                        ,IP_USUARIOS_PREDIAL.INDBORRADO
                    FROM  IP_USUARIOS_PREDIAL
                        LEFT JOIN IP_IGAC_TIPO_UNO 
                            ON     IP_IGAC_TIPO_UNO.COMPANIA        = IP_USUARIOS_PREDIAL.COMPANIA  
                            AND    IP_IGAC_TIPO_UNO.CODIGO          = IP_USUARIOS_PREDIAL.CODIGO
                            AND    IP_IGAC_TIPO_UNO.NUMERO_ORDEN    = IP_USUARIOS_PREDIAL.NUMERO_ORDEN 
                            AND    IP_IGAC_TIPO_UNO.TIPO_NIT        = IP_USUARIOS_PREDIAL.TIPO_NIT
                            AND    IP_IGAC_TIPO_UNO.NOMBRE          = IP_USUARIOS_PREDIAL.NOMBRE
                            AND    IP_IGAC_TIPO_UNO.DIRECCION       = IP_USUARIOS_PREDIAL.DIRECCION 
                            AND    IP_IGAC_TIPO_UNO.NIT             = IP_USUARIOS_PREDIAL.NIT
                            AND    IP_IGAC_TIPO_UNO.AREA_HA         = IP_USUARIOS_PREDIAL.AREA_HA
                            AND    IP_IGAC_TIPO_UNO.AREA_M2         = IP_USUARIOS_PREDIAL.AREA_M2
                            AND    IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA = IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA
                          --  AND    IP_IGAC_TIPO_UNO.DIGITO          = IP_USUARIOS_PREDIAL.DIGITO
                   WHERE IP_USUARIOS_PREDIAL.COMPANIA = UN_COMPANIA
                     AND IP_IGAC_TIPO_UNO.COMPANIA IS NULL)
                LOOP
                  -- "Paso 7 de 9. INSERTA CAMBIOS IGAC"
                    MI_CAMPOS:= '  COMPANIA
                                 , PAIS
                                 , DEPARTAMENTO
                                 , MUNICIPIO
                                 , CODIGO
                                 , TIPO
                                 , NUMERO_ORDEN
                                 , NUMERO_REGISTROS
                                 , NOMBRE
                                 , TIPO_NIT
                                 , NIT
                                 , DIRECCION
                                 , DESTINO_ECONOMICO
                                 , AREA_HA
                                 , AREA_M2
                                 , AREA_CONSTRUIDA
                                 , AREA_CONST_ANT
                                 , AVALUO_ANO
                                 , CONDICION_TRIBUTARIA
                                 , NOVEDAD
                                 , RESOLUCION
                                 , MUTACION
                                 , DATE_CREATED
                                 , CREATED_BY 
                                 , TIPOTRANSACCION
                                 , FECHATRANSACCION ';

                    MI_VALORES:= '  '''||MI_RS.COMPANIA||''' 
                                  , '''||MI_RS.PAIS||''' 
                                  , '''||MI_RS.DEPARTAMENTO||''' 
                                  , '''||MI_RS.MUNICIPIO||''' 
                                  , '''||MI_RS.CODIGO||''' 
                                  , '''||MI_RS.TIPO||''' 
                                  , '''||MI_RS.NUMERO_ORDEN||''' 
                                  , '''||MI_RS.NUMERO_REGISTROS||''' 
                                  , '''||MI_RS.NOMBRE||'''  
                                  , '''||MI_RS.TIPO_NIT||'''  
                                  , '''||MI_RS.NIT||'''  
                                  , '''||MI_RS.DIRECCION||''' 
                                  , '''||MI_RS.DESTINO_ECONOMICO||''' 
                                  , '||NVL(MI_RS.AREA_HA,0)||'  
                                  , '||NVL(MI_RS.AREA_M2,0)||' 
                                  , '||NVL(MI_RS.AREA_CONSTRUIDA,0)||' 
                                  , '||NVL(MI_RS.AREA_CONST_ANT,0)||' 
                                  , '||MI_RS.AVALUO_ANO||' 
                                  , '''||MI_RS.CONDICION_TRIBUTARIA||'''  
                                  , '''||MI_RS.NOVEDAD||''' 
                                  , '''||MI_RS.RESOLUCION||''' 
                                  , '''||MI_RS.MUTACION||'''
                                  , SYSDATE 
                                  , '''||UN_USUARIO||'''  
                                  , '''||CASE WHEN MI_RS.INDBORRADO NOT IN (0)
                                            THEN 'B'
                                            ELSE 'M'
                                       END||''' 
                                  , TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'') ' ;
                    MI_TABLA:= 'IP_CAMBIOSIGAC';
                    BEGIN 
                        MI_RTA := PCK_DATOS.FC_ACME (
                                                      UN_TABLA   => MI_TABLA 
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES 
                                                     );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                END LOOP;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACT_CAMBIOS_IGACV
                           ,UN_TABLAERROR => MI_TABLA
                            );           
            END;  
            -- agrego los usuarios nuevos
            -- Paso 7 de 9. Preparando informe de modificaciones...
            --Se controlan fechas nulas enviando la fecha del sistema

            BEGIN           
                MI_TABLA:= 'IP_CAMBIOSIGAC';
                MI_CAMPOS:= '  COMPANIA
                             , PAIS
                             , DEPARTAMENTO
                             , MUNICIPIO
                             , CODIGO
                             , TIPO
                             , NUMERO_ORDEN
                             , NUMERO_REGISTROS
                             , NOMBRE
                             , TIPO_NIT
                             , NIT
                             , DIRECCION
                             , DESTINO_ECONOMICO
                             , AREA_HA
                             , AREA_M2
                             , AREA_CONSTRUIDA
                             , AREA_CONST_ANT
                             , AVALUO_ANO
                             , CONDICION_TRIBUTARIA
                             , NOVEDAD
                             , RESOLUCION
                             , MUTACION
                             , FECHA_MOVIMIENTO
                             , TIPOTRANSACCION
                             , FECHATRANSACCION
                             , DATE_CREATED
                             , CREATED_BY';
                MI_VALORES:= ' SELECT 
                                   IP_IGAC_TIPO_UNO.COMPANIA
                                 , IP_IGAC_TIPO_UNO.PAIS
                                 , IP_IGAC_TIPO_UNO.DEPARTAMENTO
                                 , IP_IGAC_TIPO_UNO.MUNICIPIO
                                 , IP_IGAC_TIPO_UNO.CODIGO
                                 , IP_IGAC_TIPO_UNO.TIPO
                                 , IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                                 , IP_IGAC_TIPO_UNO.NUMERO_REGISTROS
                                 , IP_IGAC_TIPO_UNO.NOMBRE
                                 , IP_IGAC_TIPO_UNO.TIPO_NIT
                                 , IP_IGAC_TIPO_UNO.NIT
                                 , IP_IGAC_TIPO_UNO.DIRECCION
                                 , IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO
                                 , IP_IGAC_TIPO_UNO.AREA_HA
                                 , IP_IGAC_TIPO_UNO.AREA_M2
                                 , IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA
                                 , IP_IGAC_TIPO_UNO.AREA_CONST_ANT
                                 , IP_IGAC_TIPO_UNO.AVALUO_ANO
                                 , IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                                 , IP_IGAC_TIPO_UNO.NOVEDAD
                                 , IP_IGAC_TIPO_UNO.RESOLUCION 
                                 , IP_IGAC_TIPO_UNO.MUTACION
                                 , TO_CHAR(CASE WHEN TO_DATE(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') = TO_DATE(''01/01/1900'', ''DD/MM/YYYY HH24:MI:SS'') 
                                        THEN SYSDATE
                                        ELSE TO_DATE(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') 
                                   END  , ''DD/MM/YYYY'') EXPR1
                                 , ''N'' EXPR2
                                 , CASE WHEN TO_DATE(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') = TO_DATE(''01/01/1900'', ''DD/MM/YYYY HH24:MI:SS'') 
                                                THEN SYSDATE
                                                ELSE TO_DATE(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') 
                                           END FECHA_MOVIMIENTO
                                 , SYSDATE DATE_MODIFIED
                                 , '''||UN_USUARIO||''' USUARIO
                            FROM IP_IGAC_TIPO_UNO 
                                LEFT JOIN IP_USUARIOS_PREDIAL 
                                    ON   IP_IGAC_TIPO_UNO.COMPANIA     = IP_USUARIOS_PREDIAL.COMPANIA
                                    AND  IP_IGAC_TIPO_UNO.NUMERO_ORDEN = IP_USUARIOS_PREDIAL.NUMERO_ORDEN
                                    AND  IP_IGAC_TIPO_UNO.CODIGO       = IP_USUARIOS_PREDIAL.CODIGO
                            WHERE IP_USUARIOS_PREDIAL.CODIGO IS NULL';

                BEGIN 
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA   => MI_TABLA 
                                                 ,UN_ACCION  => 'IS'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES 
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INS_CAMBIOS_IGACV
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
            BEGIN 
                MI_TABLA:='IP_USUARIOS_PREDIAL';
                MI_VALORES:= '(COMPANIA
                              ,CODIGO
                              ,NUMERO_ORDEN
                              ,DEPARTAMENTO
                              ,MUNICIPIO
                              ,PAIS
                              ,AREA_CONST_ANT
                              ,MUTACION
                              ,AVALUO_ANO
                              ,AREA_CONSTRUIDA
                              ,AREA_M2
                              ,AREA_HA
                              ,SUCURSAL
                              ,NIT
                              ,CODIGO_EQUIVALENTE
                              ,TIPO
                              ,CONDICION_TRIBUTARIA
                              ,NOVEDAD
                              ,RESOLUCION
                              ,DESTINO_ECONOMICO
                              ,DIRECCION
                              ,FECHA_MOVIMIENTO
                              ,DATE_CREATED
                              ,TIPO_NIT
                              ,VIGENCIA
                              ,NOMBRE
                              ,NUMERO_REGISTROS
                              ,CREATED_BY)   
                        SELECT 
                           IP_IGAC_TIPO_UNO.COMPANIA
                          ,IP_IGAC_TIPO_UNO.CODIGO
                          ,IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                          ,IP_IGAC_TIPO_UNO.DEPARTAMENTO
                          ,IP_IGAC_TIPO_UNO.MUNICIPIO
                          ,IP_IGAC_TIPO_UNO.PAIS
                          ,IP_IGAC_TIPO_UNO.AREA_CONST_ANT
                          ,IP_IGAC_TIPO_UNO.MUTACION
                          ,IP_IGAC_TIPO_UNO.AVALUO_ANO
                          ,IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA
                          ,IP_IGAC_TIPO_UNO.AREA_M2
                          ,IP_IGAC_TIPO_UNO.AREA_HA
                          ,IP_IGAC_TIPO_UNO.SUCURSAL
                          ,IP_IGAC_TIPO_UNO.NIT
                          ,IP_IGAC_TIPO_UNO.CODIGO_EQUIVALENTE
                          ,IP_IGAC_TIPO_UNO.TIPO
                          ,IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                          ,IP_IGAC_TIPO_UNO.NOVEDAD
                          ,IP_IGAC_TIPO_UNO.RESOLUCION
                          ,IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO
                          ,IP_IGAC_TIPO_UNO.DIRECCION
                          ,IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO
                          ,IP_IGAC_TIPO_UNO.DATE_CREATED
                          ,IP_IGAC_TIPO_UNO.TIPO_NIT 
                          ,'||EXTRACT(YEAR FROM UN_FECHACORTE)||' VIGENCIA
                          ,IP_IGAC_TIPO_UNO.NOMBRE
                          ,IP_IGAC_TIPO_UNO.NUMERO_REGISTROS
                          ,IP_IGAC_TIPO_UNO.CREATED_BY
                       FROM IP_IGAC_TIPO_UNO
                           LEFT JOIN IP_USUARIOS_PREDIAL
                              ON  IP_IGAC_TIPO_UNO.COMPANIA       = IP_USUARIOS_PREDIAL.COMPANIA 
                              AND IP_IGAC_TIPO_UNO.CODIGO         = IP_USUARIOS_PREDIAL.CODIGO 
                              AND IP_IGAC_TIPO_UNO.NUMERO_ORDEN   = IP_USUARIOS_PREDIAL.NUMERO_ORDEN 
                       WHERE IP_USUARIOS_PREDIAL.CODIGO IS NULL';
                BEGIN 
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA   => MI_TABLA 
                                                 ,UN_ACCION  => 'IS'
                                                 ,UN_VALORES => MI_VALORES 
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INS_CAMBIOS_IGACV
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
            -- Se agregan los usuarios no incluidos en esta resolucion pero que existen en la base de datos actual
            MI_TABLA:= 'IP_CAMBIOSIGAC';
            MI_VALORES:= ' (COMPANIA
                         , PAIS
                         , DEPARTAMENTO
                         , MUNICIPIO
                         , CODIGO
                         , TIPO
                         , NUMERO_ORDEN
                         , NUMERO_REGISTROS
                         , NOMBRE
                         , TIPO_NIT
                         , NIT
                         , DIRECCION
                         , DESTINO_ECONOMICO
                         , AREA_HA
                         , AREA_M2
                         , AREA_CONSTRUIDA
                         , AREA_CONST_ANT
                         , AVALUO_ANO
                         , CONDICION_TRIBUTARIA
                         , NOVEDAD
                         , RESOLUCION
                         , MUTACION
                         , FECHA_MOVIMIENTO
                         , TIPOTRANSACCION
                         , FECHATRANSACCION
                         , DATE_CREATED
                         , CREATED_BY )
                  SELECT DISTINCT 
                              U.COMPANIA
                            , U.PAIS
                            , U.DEPARTAMENTO
                            , U.MUNICIPIO
                            , U.CODIGO
                            , U.TIPO
                            , U.NUMERO_ORDEN
                            , U.NUMERO_REGISTROS
                            , SUBSTR(U.NOMBRE, 0,30) NOMBRE
                            , CASE WHEN U.TIPO_NIT IS NULL 
                                   THEN ''X'' 
                                   ELSE U.TIPO_NIT 
                              END TIPO_NIT
                            , U.NIT
                            , SUBSTR(U.DIRECCION, 0, 30) DIRECCION
                            , U.DESTINO_ECONOMICO
                            , U.AREA_HA
                            , U.AREA_M2
                            , U.AREA_CONSTRUIDA
                            , U.AREA_CONST_ANT
                            , U.AVALUO_ANO
                            , U.CONDICION_TRIBUTARIA
                            , U.NOVEDAD
                            , U.RESOLUCION
                            , CASE WHEN U.MUTACION IS NULL 
                                   THEN ''016'' 
                                   ELSE U.MUTACION 
                              END MUTACION
                            , TO_CHAR(U.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') FECHA_MOVIMIENTO
                            , ''X'' TIPOTRANSACCION
                            , TO_CHAR(SYSDATE, ''DD/MM/YYYY HH24:MI:SS'') FECHA_TRANSACCION
                            , SYSDATE DATECREATED
                            , '''||UN_USUARIO||''' USUARIO
                       FROM IP_USUARIOS_PREDIAL U 
                           INNER JOIN IP_IGAC_TIPO_UNO I 
                               ON   U.COMPANIA     = I.COMPANIA 
                               AND  U.NUMERO_ORDEN = I.NUMERO_ORDEN 
                               AND  U.CODIGO       = I.CODIGO 
                      WHERE U.COMPANIA = '''||UN_COMPANIA||'''
                        AND (U.CODIGO_NO_ACTIVO = 0 
                             OR U.CODIGO_NO_ACTIVO IS NULL)
                        AND (U.INDBORRADO = 0 
                             OR U.INDBORRADO IS NULL) 
                        AND I.CODIGO IS NULL 
                      ORDER BY U.CODIGO';
            BEGIN 
                BEGIN  
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA   => MI_TABLA 
                                                 ,UN_ACCION  => 'IS'
                                                 ,UN_VALORES => MI_VALORES 
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSCAMBIOSIGACNUE
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
        END IF;
        --Actualizo los usuarios del predial con respecto al archivo del igac
        IF 'ALCALDIA DE FUSAGASUGA'= UN_NOMBRECOMPANIA THEN 
            MI_TABLA:='IP_USUARIOS_PREDIAL';
            MI_MERGEUSING:= ' SELECT 
                                  IP_IGAC_TIPO_UNO.CODIGO
                                , IP_IGAC_TIPO_UNO.COMPANIA
                                , IP_IGAC_TIPO_UNO.TIPO
                                , IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                                , IP_IGAC_TIPO_UNO.NUMERO_REGISTROS
                                , IP_IGAC_TIPO_UNO.NOMBRE
                                , IP_IGAC_TIPO_UNO.TIPO_NIT
                                , IP_IGAC_TIPO_UNO.NIT
                                , IP_IGAC_TIPO_UNO.DIRECCION
                                , IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO
                                , IP_IGAC_TIPO_UNO.AREA_HA
                                , IP_IGAC_TIPO_UNO.AREA_M2
                                , IP_IGAC_TIPO_UNO.AREA_CONST_ANT
                                , IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA
                                , IP_IGAC_TIPO_UNO.AVALUO_ANO
                                , IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                                , IP_IGAC_TIPO_UNO.NOVEDAD
                                , IP_IGAC_TIPO_UNO.RESOLUCION
                                , IP_IGAC_TIPO_UNO.MUTACION
                                , IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO 
                             FROM IP_USUARIOS_PREDIAL 
                                INNER JOIN IP_IGAC_TIPO_UNO 
                                    ON  IP_USUARIOS_PREDIAL.COMPANIA     = IP_IGAC_TIPO_UNO.COMPANIA
                                    AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                                    AND IP_USUARIOS_PREDIAL.CODIGO       = IP_IGAC_TIPO_UNO.CODIGO ';
            MI_MERGEENLACE:= '    VISTA.COMPANIA          = TABLA.COMPANIA 
                              AND VISTA.NUMERO_ORDEN      = TABLA.NUMERO_ORDEN 
                              AND VISTA.CODIGO            = TABLA.CODIGO';

            MI_MERGEEXISTE:= 'UPDATE SET  
                                     TABLA.TIPO                 = VISTA.TIPO
                                    ,TABLA.NUMERO_REGISTROS     = VISTA.NUMERO_REGISTROS
                                    ,TABLA.NOMBRE               = VISTA.NOMBRE
                                    ,TABLA.TIPO_NIT             = VISTA.TIPO_NIT
                                    ,TABLA.NIT                  = VISTA.NIT
                                    ,TABLA.DIRECCION            = VISTA.DIRECCION
                                    ,TABLA.DESTINO_ECONOMICO    = VISTA.DESTINO_ECONOMICO
                                    ,TABLA.AREA_HA              = VISTA.AREA_HA
                                    ,TABLA.AREA_M2              = VISTA.AREA_M2
                                    ,TABLA.AREA_CONST_ANT       =  VISTA.AREA_CONSTRUIDA
                                    ,TABLA.AREA_CONSTRUIDA      =  VISTA.AREA_CONSTRUIDA
                                    ,TABLA.AVALUO_ANO           = VISTA.AVALUO_ANO
                                    ,TABLA.CONDICION_TRIBUTARIA = VISTA.CONDICION_TRIBUTARIA
                                    ,TABLA.NOVEDAD              = VISTA.NOVEDAD
                                    ,TABLA.RESOLUCION           = VISTA.RESOLUCION
                                    ,TABLA.MUTACION             = VISTA.MUTACION
                                    ,TABLA.FECHA_MOVIMIENTO     = TO_DATE(VISTA.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'')
                                    ,TABLA.DATE_MODIFIED        = SYSDATE 
                                    ,TABLA.MODIFIED_BY          = '''||UN_USUARIO||''' ';
            BEGIN 
                BEGIN 
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA       => MI_TABLA 
                                                 ,UN_ACCION      => 'MM'
                                                 ,UN_MERGEUSING  => MI_MERGEUSING
                                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INS_ACT_USUA_PRED
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
        ELSE   
            -- "Paso 8 de 9. Modificando predios."
            -- Se quita la actualizaciÓn del campo numero_orden,
            -- debido a que hace parte de una llave compuesta
            MI_TABLA:='IP_USUARIOS_PREDIAL';
            MI_MERGEUSING:= ' SELECT  
                                    IP_IGAC_TIPO_UNO.COMPANIA
                                  , IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                                  , IP_IGAC_TIPO_UNO.CODIGO
                                  , IP_IGAC_TIPO_UNO.TIPO
                                  , IP_IGAC_TIPO_UNO.NUMERO_REGISTROS
                                  , IP_IGAC_TIPO_UNO.NOMBRE
                                  , IP_IGAC_TIPO_UNO.TIPO_NIT
                                  , IP_IGAC_TIPO_UNO.NIT
                                  , IP_IGAC_TIPO_UNO.DIRECCION
                                  , IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO
                                  , IP_IGAC_TIPO_UNO.AREA_HA
                                  , IP_IGAC_TIPO_UNO.AREA_M2
                                  , IP_IGAC_TIPO_UNO.AREA_CONST_ANT
                                  , IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA
                                  , IP_IGAC_TIPO_UNO.AVALUO_ANO
                                  , IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                                  , IP_IGAC_TIPO_UNO.NOVEDAD
                                  , IP_IGAC_TIPO_UNO.RESOLUCION
                                  , IP_IGAC_TIPO_UNO.MUTACION
                                  , IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO
                                  , 0 INDBORRADO
                                  , CASE WHEN IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA IN (0) 
                                              AND IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA > 0 
                                         THEN EXTRACT (YEAR FROM TO_DATE(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO,''DD/MM/YYYY HH24:MI:SS'')) 
                                         ELSE IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA 
                                    END ANO_CONSTRUCCION
                             FROM IP_USUARIOS_PREDIAL 
                                INNER JOIN IP_IGAC_TIPO_UNO 
                                    ON  IP_USUARIOS_PREDIAL.COMPANIA     = IP_IGAC_TIPO_UNO.COMPANIA
                                    AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_IGAC_TIPO_UNO.NUMERO_ORDEN 
                                    AND IP_USUARIOS_PREDIAL.CODIGO       = IP_IGAC_TIPO_UNO.CODIGO 
                              WHERE IP_USUARIOS_PREDIAL.COMPANIA = '''||UN_COMPANIA||''' ';

            MI_MERGEENLACE:= '    VISTA.COMPANIA     = TABLA.COMPANIA 
                              AND VISTA.NUMERO_ORDEN = TABLA.NUMERO_ORDEN
                              AND VISTA.CODIGO       = TABLA.CODIGO '; 

            MI_MERGEEXISTE:= 'UPDATE SET 
                                  TABLA.TIPO                 = VISTA.TIPO, 
                                  TABLA.NUMERO_REGISTROS     = VISTA.NUMERO_REGISTROS,
                                  TABLA.NOMBRE               = VISTA.NOMBRE,  
                                  TABLA.TIPO_NIT             = VISTA.TIPO_NIT, 
                                  TABLA.NIT                  = VISTA.NIT, 
                                  TABLA.DIRECCION            = VISTA.DIRECCION,  
                                  TABLA.DESTINO_ECONOMICO    = VISTA.DESTINO_ECONOMICO, 
                                  TABLA.AREA_HA              = VISTA.AREA_HA, 
                                  TABLA.AREA_M2              = VISTA.AREA_M2,  
                                  TABLA.AREA_CONST_ANT       = VISTA.AREA_CONSTRUIDA,
                                  TABLA.AREA_CONSTRUIDA      = VISTA.AREA_CONSTRUIDA, 
                                  TABLA.AVALUO_ANO           = VISTA.AVALUO_ANO,  
                                  TABLA.CONDICION_TRIBUTARIA = VISTA.CONDICION_TRIBUTARIA,
                                  TABLA.NOVEDAD              = VISTA.NOVEDAD,  
                                  TABLA.RESOLUCION           = VISTA.RESOLUCION, 
                                  TABLA.MUTACION             = VISTA.MUTACION, 
                                  TABLA.FECHA_MOVIMIENTO     = TO_DATE(VISTA.FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS''), 
                                  TABLA.INDBORRADO           = VISTA.INDBORRADO, 
                                  TABLA.ANO_CONSTRUCCION     = VISTA.ANO_CONSTRUCCION, 
                                  TABLA.MODIFIED_BY          = '''||UN_USUARIO||''', 
                                  TABLA.DATE_MODIFIED        = SYSDATE
                                  ';

            BEGIN 
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA       => MI_TABLA 
                                                 ,UN_ACCION      => 'MM'
                                                 ,UN_MERGEUSING  => MI_MERGEUSING
                                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACT_USUA_PREDIGAC
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
        END IF;
        /*
        FOR MI_RS IN (    
            SELECT  
                    IP_IGAC_TIPO_UNO.COMPANIA
                   ,IP_IGAC_TIPO_UNO.CODIGO
                   ,IP_IGAC_TIPO_UNO.NUMERO_ORDEN 
              FROM IP_IGAC_TIPO_UNO 
                  LEFT JOIN IP_USUARIOS_PREDIAL 
                    ON     IP_IGAC_TIPO_UNO.COMPANIA     = IP_USUARIOS_PREDIAL.COMPANIA
                    AND    IP_IGAC_TIPO_UNO.NUMERO_ORDEN = IP_USUARIOS_PREDIAL.NUMERO_ORDEN
                    AND    IP_IGAC_TIPO_UNO.CODIGO       = IP_USUARIOS_PREDIAL.CODIGO
             WHERE IP_IGAC_TIPO_UNO.COMPANIA = UN_COMPANIA  
               AND IP_USUARIOS_PREDIAL.CODIGO IS NULL)
        LOOP
        -- Insertando predios nuevos...
            -- "Paso 9 de 9.
            -- strEtapa = "9.1"
            MI_TABLA:= 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS:= '   COMPANIA
                          , PAIS
                          , VIGENCIA
                          , DEPARTAMENTO
                          , MUNICIPIO
                          , CODIGO
                          , TIPO
                          , NUMERO_ORDEN
                          , NUMERO_REGISTROS
                          , NOMBRE
                          , TIPO_NIT
                          , NIT
                          , DIRECCION
                          , DESTINO_ECONOMICO
                          , AREA_HA
                          , AREA_M2
                          , AREA_CONSTRUIDA
                          , AREA_CONST_ANT
                          , AVALUO_ANO
                          , CONDICION_TRIBUTARIA
                          , NOVEDAD
                          , RESOLUCION
                          , MUTACION
                          , FECHA_MOVIMIENTO
                          , PAGO_ANO
                          , ANO_CONSTRUCCION';
            MI_VALORES:= ' SELECT 
                                 IP_IGAC_TIPO_UNO.COMPANIA
                               , IP_IGAC_TIPO_UNO.PAIS
                               , SUBSTR(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, 7, 10) VIGENCIA
                               , IP_IGAC_TIPO_UNO.DEPARTAMENTO
                               , IP_IGAC_TIPO_UNO.MUNICIPIO
                               , IP_IGAC_TIPO_UNO.CODIGO
                               , IP_IGAC_TIPO_UNO.TIPO
                               , IP_IGAC_TIPO_UNO.NUMERO_ORDEN
                               , IP_IGAC_TIPO_UNO.NUMERO_REGISTROS
                               , IP_IGAC_TIPO_UNO.NOMBRE
                               , IP_IGAC_TIPO_UNO.TIPO_NIT
                               , IP_IGAC_TIPO_UNO.NIT
                               , IP_IGAC_TIPO_UNO.DIRECCION
                               , IP_IGAC_TIPO_UNO.DESTINO_ECONOMICO
                               , IP_IGAC_TIPO_UNO.AREA_HA
                               , IP_IGAC_TIPO_UNO.AREA_M2
                               , IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA
                               , IP_IGAC_TIPO_UNO.AREA_CONST_ANT
                               , IP_IGAC_TIPO_UNO.AVALUO_ANO
                               , IP_IGAC_TIPO_UNO.CONDICION_TRIBUTARIA
                               , IP_IGAC_TIPO_UNO.NOVEDAD
                               , IP_IGAC_TIPO_UNO.RESOLUCION
                               , IP_IGAC_TIPO_UNO.MUTACION
                               , TO_DATE(FECHA_MOVIMIENTO, ''DD/MM/YYYY HH24:MI:SS'') EXPR1
                               , SUBSTR(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, 7, 10) -1 FECHA_MOVIMIENTO
                               , CASE WHEN IP_IGAC_TIPO_UNO.AREA_CONSTRUIDA NOT IN (0) 
                                      THEN SUBSTR(IP_IGAC_TIPO_UNO.FECHA_MOVIMIENTO, 7, 10) 
                                      ELSE ''0'' END ANO_CONSTRUCCION
                          FROM IP_IGAC_TIPO_UNO 
                         WHERE IP_IGAC_TIPO_UNO.COMPANIA     = '''||MI_RS.COMPANIA||'''
                           AND IP_IGAC_TIPO_UNO.CODIGO       = '''||MI_RS.CODIGO||''' 
                           AND IP_IGAC_TIPO_UNO.NUMERO_ORDEN = '''||MI_RS.NUMERO_ORDEN||''' ';

            BEGIN 
                BEGIN 
                    MI_RTA := PCK_DATOS.FC_ACME (
                                                  UN_TABLA   => MI_TABLA 
                                                 ,UN_ACCION  => 'IS'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES 
                                                 );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INS_USUA_PREDIGAC
                           ,UN_TABLAERROR => MI_TABLA
                          );
            END;
        END LOOP;*/
    END IF;
    RETURN -1;
END FC_IMPORTAR_IGAC_TIPO_UNO;

--11
FUNCTION FC_VALIDATIPORESOLUCION
/*
  NAME              : FC_VALIDATIPORESOLUCION --> En access ValidaTipoResolucion
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRACION    : 22/02/2017
  TIME              : 11:00 AM
  DESCRIPTION       : FUNCION QUE valida la primera linea del plano que se desea subir.
  PARAMETERS        :     UN_PRIMERALINEA  => LINEA INICIAL DEL ARCHIVO A IMPORTAR

  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PredialP2017.01.06VB
  @NAME             : validarTipoResolucion
  @METHOD           : GET
  */
( 
    UN_PRIMERALINEA IN VARCHAR2 
)RETURN VARCHAR2 
/*
'*****JAAB 07ABR09
'valida la primera linea del plano que se desea subir:
'Si posiciones 25,26,27 y 28 = ej: 003S = Tipo Uno
'contrario posicion 31 = C ó I y posición 32 = # = Tipo Mes
*/
AS
MI_VALIDATIPORESOLUCIONU VARCHAR2(150 CHAR);
BEGIN
    IF ASCII(SUBSTR(UN_PRIMERALINEA,25,25)) BETWEEN  48 AND 57 THEN 
        IF ASCII(SUBSTR(UN_PRIMERALINEA,26,26)) BETWEEN 48 AND 57 THEN 
             IF ASCII(SUBSTR(UN_PRIMERALINEA,27,27)) BETWEEN 48 AND 57 THEN 
                IF (ASCII(SUBSTR(UN_PRIMERALINEA,28,28)) >= 65 
                     AND ASCII(SUBSTR(UN_PRIMERALINEA,27,27)) <= 90
                   )  
                   OR 
                   (ASCII(SUBSTR(UN_PRIMERALINEA,28,28)) >= 97
                    AND ASCII(SUBSTR(UN_PRIMERALINEA,27,27)) <= 122
                    ) THEN
                    MI_VALIDATIPORESOLUCIONU:= 'Tipo Uno';
                ELSIF ASCII(SUBSTR(UN_PRIMERALINEA,31,31)) = 67
                      OR ASCII(SUBSTR(UN_PRIMERALINEA,31,31)) = 73 THEN 
                    IF ASCII(SUBSTR(UN_PRIMERALINEA,32,32)) >= 48
                       AND ASCII(SUBSTR(UN_PRIMERALINEA,32,32)) <= 57 THEN 
                            MI_VALIDATIPORESOLUCIONU:= 'Tipo Mes';
                    END IF;
                END IF;
            END IF;
        END IF;
    ELSE 
        MI_VALIDATIPORESOLUCIONU:= 'Otro Plano'; 
    END IF;
   RETURN MI_VALIDATIPORESOLUCIONU;
END FC_VALIDATIPORESOLUCION;


PROCEDURE PR_DISTRIBUIRACUERDOACACIAS
/*
      NAME              : PR_DISTRIBUIRACUERDOACACIAS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : UN_COMPANIA       => COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_CODIGOPREDIO   => CODIGO DEL PREDIO POR EL CUAL SE VA A FILTRAR   
                          UN_CODIGOACUERDO  => CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR  
                          UN_PREANOI        => AÑO INICIAL POR EL CUAL SE VA A FILTRAR 
                          UN_PREANO         => AÑO FINAL POR EL CUAL SE VA A FILTRAR
                          UN_TABLA          => TABLA EN LA CUAL SE VA A REALIZAR LA OPERACION
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    distribuirAcuerdoAcacias
    */
(
  UN_COMPANIA               IN      PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOPREDIO           IN      IP_FACTURADOSACUERDOS.PREDIO%TYPE,
  UN_CODIGOACUERDO          IN      IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_PREANOI                IN      IP_RECIBOS_DE_PAGO.PREANOI%TYPE,
  UN_PREANO                 IN      IP_RECIBOS_DE_PAGO.PREANOI%TYPE,
  UN_TABLA                  IN      PCK_SUBTIPOS.TI_TABLA
)
AS
MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
MI_DBLAJUSTE                      PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLVALORVIGENCIA               PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLINTERESVIGENCIA             PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLINTERESCARVIGENCIA          PCK_SUBTIPOS.TI_DOBLE := 0;
MI_DBLCAPITALVIGENCIA             PCK_SUBTIPOS.TI_DOBLE := 0;
MI_DBLCARVIGENCIA                 PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLBOMBEROSVIGENCIA            PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESANT             PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESCARANT          PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCAPITALANT             PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCARANT                 PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOBOMBEROSANT            PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESANTANT          PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESCARANTANT       PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCAPITALANTANT          PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCARANTANT              PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOBOMBEROSANTANT         PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESANTANTANT       PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOINTERESCARANTANTANT    PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCAPITALANTANTANT       PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOCARANTANTANT           PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_DBLABONOBOMBEROSANTANTANT      PCK_SUBTIPOS.TI_DOBLE:= 0;
MI_NCUOTA                         PCK_SUBTIPOS.TI_ENTERO:= 0;
MI_INTPREANOPENDIENTE             PCK_SUBTIPOS.TI_ENTERO;
MI_SALDOADISTRIBUIR               PCK_SUBTIPOS.TI_DOBLE;
MI_MAXCUOTA                       IP_TMP_FACTURADOSACUERDOS.CUOTA%TYPE;
MI_STRSQL                         PCK_SUBTIPOS.TI_STRSQL;
MI_RSFA                           SYS_REFCURSOR;
MI_RSFAC2                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC6                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC10                        PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC4                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC8                         PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC12                        PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC3                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC7                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC11                        PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC14                        PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC1                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC5                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC9                         PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAC13                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC15                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC16                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC17                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC18                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC19                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFAC20                        PCK_SUBTIPOS.TI_DOBLE;
MI_RSFATOTAL                      PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAINTERES_RECARGO            PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAINTERES_ACUERDO            PCK_SUBTIPOS.TI_DOBLE; 
MI_RSFAPREANOI                    IP_TMP_FACTURADOSACUERDOS.PREANOI%TYPE; 
MI_RSFACUOTA                      IP_TMP_FACTURADOSACUERDOS.CUOTA%TYPE;
MI_TOTAL                          PCK_SUBTIPOS.TI_DOBLE;

BEGIN
  MI_CAMPOS := 'C1=0, C2=0, C3=0, C4=0, C5=0, C6=0, C7=0, C8=0, C9=0, C10=0, C11=0, C12=0,C13=0, C14=0, C15=0, C16=0, C17=0, C18=0, C19=0, C20=0, PREANOI=0, PREANO=0';
  MI_CONDICION := 'CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''
                   AND COMPANIA ='''||UN_COMPANIA||'''';

  MI_INTPREANOPENDIENTE := UN_PREANOI;

  BEGIN 
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  UN_TABLA, 
                                             UN_ACCION    =>  'M', 
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ACT_FACT_ACU
                );
  END;

  <<FACTURADOS>>
  FOR RS IN ( SELECT C1,
                     C2,
                     C3,
                     C4,
                     C14,
                     PREANO,
                     NUMERO_ORDEN
              FROM  IP_FACTURADOS 
              WHERE CODIGO = UN_CODIGOPREDIO  
                AND PREANO BETWEEN UN_PREANOI AND UN_PREANO
              ORDER BY PREANO) 
  LOOP
    MI_DBLCAPITALVIGENCIA     :=  MI_DBLCAPITALVIGENCIA+RS.C1;
    MI_DBLCARVIGENCIA         :=  MI_DBLCARVIGENCIA+RS.C3;
    MI_DBLINTERESVIGENCIA     :=  MI_DBLINTERESVIGENCIA+ RS.C2;
    MI_DBLINTERESCARVIGENCIA  :=  MI_DBLINTERESCARVIGENCIA + RS.C4;
    MI_DBLBOMBEROSVIGENCIA    :=  MI_DBLBOMBEROSVIGENCIA + RS.C14;
    MI_DBLVALORVIGENCIA       :=  MI_DBLCAPITALVIGENCIA+ MI_DBLINTERESVIGENCIA+ MI_DBLCARVIGENCIA + MI_DBLBOMBEROSVIGENCIA + MI_DBLINTERESCARVIGENCIA;

    MI_STRSQL := 'SELECT  C2, C6, C10, C4, C8,C12, C3, C7, C11, C14, C1, C5, C9, TOTAL, INTERES_RECARGO, INTERES_ACUERDO, PREANOI, CUOTA
                  FROM   '||UN_TABLA||' 
                  WHERE  CODIGOACUERDO  = ''' ||UN_CODIGOACUERDO||'''
                    AND  CUOTA          >= '  ||MI_NCUOTA       ||' 
                    ORDER BY CUOTA';


    <<ACUERDOSFACTURADOS>>
    OPEN MI_RSFA FOR MI_STRSQL;
    LOOP
    FETCH MI_RSFA INTO MI_RSFAC2, MI_RSFAC6, MI_RSFAC10, MI_RSFAC4, MI_RSFAC8,MI_RSFAC12, MI_RSFAC3, MI_RSFAC7, MI_RSFAC11, MI_RSFAC14, MI_RSFAC1, MI_RSFAC5, MI_RSFAC9, MI_RSFATOTAL, MI_RSFAINTERES_RECARGO, MI_RSFAINTERES_ACUERDO, MI_RSFAPREANOI, MI_RSFACUOTA;
    EXIT WHEN MI_RSFA%NOTFOUND;
      MI_DBLABONOINTERESANT := 0;
      MI_DBLABONOINTERESCARANT := 0;
      MI_DBLABONOCAPITALANT := 0;
      MI_DBLABONOCARANT := 0;
      MI_DBLABONOBOMBEROSANT := 0;
      MI_DBLABONOINTERESANTANT := 0;
      MI_DBLABONOINTERESCARANTANT := 0;
      MI_DBLABONOCAPITALANTANT := 0;
      MI_DBLABONOCARANTANT := 0;
      MI_DBLABONOBOMBEROSANTANT := 0;
      MI_DBLABONOINTERESANTANTANT := 0;
      MI_DBLABONOINTERESCARANTANTANT := 0;
      MI_DBLABONOCAPITALANTANTANT := 0;
      MI_DBLABONOCARANTANTANT := 0;
      MI_DBLABONOBOMBEROSANTANTANT := 0;

      MI_DBLABONOINTERESANT := MI_DBLABONOINTERESANT + MI_RSFAC2;
      MI_DBLABONOINTERESANTANT := MI_DBLABONOINTERESANTANT + MI_RSFAC6;
      MI_DBLABONOINTERESANTANTANT := MI_DBLABONOINTERESANTANTANT + MI_RSFAC10;

      MI_DBLABONOINTERESCARANT := MI_DBLABONOINTERESCARANT + MI_RSFAC4;
      MI_DBLABONOINTERESCARANTANT := MI_DBLABONOINTERESCARANTANT + MI_RSFAC8;
      MI_DBLABONOINTERESCARANTANTANT := MI_DBLABONOINTERESCARANTANTANT + MI_RSFAC12;

      MI_DBLABONOCARANT := MI_DBLABONOCARANT + MI_RSFAC3;
      MI_DBLABONOCARANTANT := MI_DBLABONOCARANTANT + MI_RSFAC7;
      MI_DBLABONOCARANTANTANT := MI_DBLABONOCARANTANTANT + MI_RSFAC11;

      MI_DBLABONOBOMBEROSANT := MI_DBLABONOBOMBEROSANT + MI_RSFAC14;

      MI_DBLABONOCAPITALANT := MI_DBLABONOCAPITALANT + MI_RSFAC1;
      MI_DBLABONOCAPITALANTANT := MI_DBLABONOCAPITALANTANT + MI_RSFAC5;
      MI_DBLABONOCAPITALANTANTANT := MI_DBLABONOCAPITALANTANTANT + MI_RSFAC9;

      MI_SALDOADISTRIBUIR := NVL(MI_RSFATOTAL, 0) - NVL(MI_RSFAINTERES_RECARGO, 0) - NVL(MI_RSFAINTERES_ACUERDO, 0) - NVL(MI_DBLABONOINTERESANT, 0) - 
                          NVL(MI_DBLABONOINTERESCARANT, 0) - NVL(MI_DBLABONOCAPITALANT, 0) - NVL(MI_DBLABONOCARANT, 0) - NVL(MI_DBLABONOBOMBEROSANT, 0) - 
                          NVL(MI_DBLABONOINTERESANTANT, 0) - NVL(MI_DBLABONOINTERESCARANTANT, 0) - NVL(MI_DBLABONOCAPITALANTANT, 0) - 
                          NVL(MI_DBLABONOCARANTANT, 0) - NVL(MI_DBLABONOINTERESANTANTANT, 0) - NVL(MI_DBLABONOINTERESCARANTANTANT, 0) - 
                          NVL(MI_DBLABONOCAPITALANTANTANT, 0) - NVL(MI_DBLABONOCARANTANTANT, 0);

      MI_CAMPOS  := 'PREANOI = '|| MI_INTPREANOPENDIENTE;
      MI_CONDICION := ' COMPANIA              = '''|| UN_COMPANIA      ||
                      ''' AND  CODIGOACUERDO = '''|| UN_CODIGOACUERDO ||
                      ''' AND CUOTA          = '  || MI_RSFACUOTA;

      BEGIN 
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  UN_TABLA, 
                                                 UN_ACCION    =>  'M', 
                                                 UN_CAMPOS    =>  MI_CAMPOS,
                                                 UN_CONDICION =>  MI_CONDICION );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ACT_ANIO_FACT_ACU
                      );
      END;
      <<CONCEPTOS>>
      FOR RSCO IN (SELECT CODIGO, 
                          ESCAPITALVIGENCIA, 
                          ESINTERESVIGENCIA 
                   FROM  IP_CONCEPTOS  
                   WHERE  ANO     =  RS.PREANO  
                    AND PRIORIDAD <> 0 
                    ORDER BY PRIORIDAD DESC)
      LOOP
        IF  MI_DBLCAPITALVIGENCIA <> 0 OR MI_DBLINTERESVIGENCIA <> 0 OR 
            MI_DBLCARVIGENCIA <> 0 OR MI_DBLBOMBEROSVIGENCIA <> 0 OR MI_DBLINTERESCARVIGENCIA <> 0 
        THEN
          IF RSCO.CODIGO = 3 THEN
            IF (MI_SALDOADISTRIBUIR >= MI_DBLCARVIGENCIA) AND (MI_DBLCARVIGENCIA <> 0) THEN
              MI_DBLABONOCARANT := 0;
              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
                MI_CAMPOS  := 'C3 = '|| (MI_RSFAC3 + MI_DBLCARVIGENCIA);
              ELSIF RS.PREANO = (EXTRACT(YEAR FROM SYSDATE) - 1) THEN
                MI_CAMPOS  := 'C7 = '|| (MI_RSFAC7 + MI_DBLCARVIGENCIA);
              ELSIF RS.PREANO <= (EXTRACT(YEAR FROM SYSDATE) - 2) THEN
                MI_CAMPOS  := 'C11 = '|| (MI_RSFAC11 + MI_DBLCARVIGENCIA);
              END IF;

              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );                 



              MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_DBLCARVIGENCIA;
              MI_DBLCARVIGENCIA := 0;

              IF  MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                  MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 
              THEN
                  MI_NCUOTA := MI_RSFACUOTA;
                  MI_CAMPOS  := 'PREANO ='|| RS.PREANO;
                  PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                             UN_TABLA         => UN_TABLA,
                                             UN_CODIGO        => RSCO.CODIGO, 
                                             UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                             UN_CAMPOS        => MI_CAMPOS, 
                                             UN_CUOTA         => MI_RSFACUOTA,
                                             UN_CONDICION     => '0'); 


              END IF;                
            ELSIF (MI_SALDOADISTRIBUIR < MI_DBLCARVIGENCIA) AND (MI_DBLCARVIGENCIA <> 0) 
            THEN
              MI_DBLABONOCARANT := 0;

              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE)THEN
                MI_CAMPOS  := 'C3 = '||  (MI_RSFAC3 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
                MI_CAMPOS  := 'C7 = '||  (MI_RSFAC7 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
                MI_CAMPOS  := 'C11 = '|| (MI_RSFAC11 + MI_SALDOADISTRIBUIR);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,  
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_DBLCARVIGENCIA := MI_DBLCARVIGENCIA - MI_SALDOADISTRIBUIR;
              MI_SALDOADISTRIBUIR := 0;

              MI_CAMPOS  := 'PREANO ='|| RS.PREANO;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,  
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA,
                                         UN_CONDICION     => '0');                      

              IF  MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND MI_DBLCARVIGENCIA = 0 AND 
                  MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
              ELSE
                MI_INTPREANOPENDIENTE := RS.PREANO;
              END IF; 
            END IF;
          ELSIF RSCO.CODIGO = 14 THEN
            IF (MI_SALDOADISTRIBUIR >= MI_DBLBOMBEROSVIGENCIA) AND (MI_DBLBOMBEROSVIGENCIA <> 0) THEN
              MI_DBLABONOBOMBEROSANT := 0;

              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
                MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_DBLBOMBEROSVIGENCIA);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
                MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_DBLBOMBEROSVIGENCIA);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
                MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_DBLBOMBEROSVIGENCIA);
              END IF;

              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA, 
                                         UN_CODIGO        => RSCO.CODIGO,
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_DBLBOMBEROSVIGENCIA;
              MI_DBLBOMBEROSVIGENCIA := 0;
              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
                MI_CAMPOS  := 'PREANO ='|| RS.PREANO;
                PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                           UN_TABLA         => UN_TABLA, 
                                           UN_CODIGO        => RSCO.CODIGO, 
                                           UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                           UN_CAMPOS        => MI_CAMPOS, 
                                           UN_CUOTA         => MI_RSFACUOTA, 
                                           UN_CONDICION     => '0');      
              END IF;    
            ELSIF (MI_SALDOADISTRIBUIR < MI_DBLBOMBEROSVIGENCIA) AND (MI_DBLBOMBEROSVIGENCIA <> 0) THEN
              MI_DBLABONOBOMBEROSANT := 0;

              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
              MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
              MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
              MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_SALDOADISTRIBUIR);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA, 
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_DBLBOMBEROSVIGENCIA := MI_DBLBOMBEROSVIGENCIA - MI_SALDOADISTRIBUIR;
              MI_SALDOADISTRIBUIR := 0;
              MI_CAMPOS  := 'PREANO ='|| RS.PREANO;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA,
                                         UN_CONDICION     => '0');

              IF  MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                  MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
              ELSE
                  MI_INTPREANOPENDIENTE := RS.PREANO;
              END IF;
            END IF;
          ELSIF RSCO.CODIGO = 1 THEN
            IF (MI_SALDOADISTRIBUIR >= MI_DBLCAPITALVIGENCIA) AND (MI_DBLCAPITALVIGENCIA <> 0) THEN
              MI_DBLABONOCAPITALANT := 0;

              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
                 MI_CAMPOS  := 'C1 = '|| (MI_RSFAC1 + MI_DBLCAPITALVIGENCIA);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
                  MI_CAMPOS  := 'C5 = '|| (MI_RSFAC5 + MI_DBLCAPITALVIGENCIA);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
                  MI_CAMPOS  := 'C9 = '|| (MI_RSFAC9 + MI_DBLCAPITALVIGENCIA);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_DBLCAPITALVIGENCIA;
              MI_DBLCAPITALVIGENCIA := 0;
              IF  MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                  MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN

                MI_NCUOTA  := MI_RSFACUOTA;
                MI_CAMPOS  := 'PREANO ='|| RS.PREANO;
                PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                           UN_TABLA         => UN_TABLA,
                                           UN_CODIGO        => RSCO.CODIGO, 
                                           UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                           UN_CAMPOS        => MI_CAMPOS, 
                                           UN_CUOTA         => MI_RSFACUOTA,
                                           UN_CONDICION     => '0');    
              END IF;                
            ELSIF (MI_SALDOADISTRIBUIR < MI_DBLCAPITALVIGENCIA) AND (MI_DBLCAPITALVIGENCIA <> 0) THEN
              MI_DBLABONOINTERESANT := 0;   
              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
                   MI_CAMPOS  := 'C1 = '|| (MI_RSFAC1 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
                   MI_CAMPOS  := 'C5 = '|| (MI_RSFAC5 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
                   MI_CAMPOS  := 'C9 = '|| (MI_RSFAC9 + MI_SALDOADISTRIBUIR);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_DBLCAPITALVIGENCIA := MI_DBLCAPITALVIGENCIA - MI_SALDOADISTRIBUIR;
              MI_SALDOADISTRIBUIR := 0;
              MI_CAMPOS  := 'PREANO ='|| RS.PREANO;

              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA,
                                         UN_CONDICION     => '0');

              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                  MI_NCUOTA := MI_RSFACUOTA;
              ELSE
                  MI_INTPREANOPENDIENTE := RS.PREANO;
              END IF;
            END IF;
          ELSIF  RSCO.CODIGO = 2 THEN
            IF (MI_SALDOADISTRIBUIR >= MI_DBLINTERESVIGENCIA) AND (MI_DBLINTERESVIGENCIA <> 0) THEN
              MI_DBLABONOINTERESANT := 0;

              IF RS.PREANO = EXTRACT( YEAR FROM SYSDATE) THEN
                  MI_CAMPOS  := 'C2 = '|| (MI_RSFAC2 + MI_DBLINTERESVIGENCIA);
              ELSIF RS.PREANO = (EXTRACT( YEAR FROM SYSDATE) - 1) THEN
                  MI_CAMPOS  := 'C6 = '|| (MI_RSFAC6 + MI_DBLINTERESVIGENCIA);
              ELSIF RS.PREANO <= (EXTRACT( YEAR FROM SYSDATE) - 2) THEN
                  MI_CAMPOS  := 'C10 = '|| (MI_RSFAC10 + MI_DBLINTERESVIGENCIA);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_DBLINTERESVIGENCIA;
              MI_DBLINTERESVIGENCIA := 0;
              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                 MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
                MI_CAMPOS  := 'PREANO ='|| RS.PREANO;

                PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                           UN_TABLA         => UN_TABLA,
                                           UN_CODIGO        => RSCO.CODIGO, 
                                           UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                           UN_CAMPOS        => MI_CAMPOS, 
                                           UN_CUOTA         => MI_RSFACUOTA,
                                           UN_CONDICION     => '0');

              END IF;               
            ELSIF (MI_SALDOADISTRIBUIR < MI_DBLINTERESVIGENCIA) AND (MI_DBLINTERESVIGENCIA <> 0) THEN
              MI_DBLABONOINTERESANT := 0;
              IF RS.PREANO = EXTRACT(YEAR FROM SYSDATE) THEN
                  MI_CAMPOS  := 'C2 = '|| (MI_RSFAC2 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO = (EXTRACT(YEAR FROM SYSDATE) - 1) THEN
                  MI_CAMPOS  := 'C6 = '|| (MI_RSFAC6 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO <= (EXTRACT(YEAR FROM SYSDATE) - 2) THEN
                  MI_CAMPOS  := 'C10 = '|| (MI_RSFAC10 + MI_SALDOADISTRIBUIR);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );
              MI_DBLINTERESVIGENCIA := MI_DBLINTERESVIGENCIA - MI_SALDOADISTRIBUIR;
              MI_SALDOADISTRIBUIR := 0;
              MI_CAMPOS  := 'PREANO ='|| RS.PREANO;

              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      =>UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO =>UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA,
                                         UN_CONDICION     => '0');
              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                 MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
              ELSE
                  MI_INTPREANOPENDIENTE := RS.PREANO;
              END IF;
            END IF;
          ELSIF RSCO.CODIGO = 4 THEN
            IF (MI_SALDOADISTRIBUIR >= MI_DBLINTERESCARVIGENCIA) AND (MI_DBLINTERESCARVIGENCIA <> 0) THEN
              MI_DBLABONOINTERESCARANT := 0;

              IF RS.PREANO = EXTRACT(YEAR FROM SYSDATE) THEN
                  MI_CAMPOS  := 'C4 = '|| (MI_RSFAC4 + MI_DBLINTERESCARVIGENCIA);
              ELSIF RS.PREANO = (EXTRACT(YEAR FROM SYSDATE) - 1) THEN
                  MI_CAMPOS  := 'C8 = '|| (MI_RSFAC8 + MI_DBLINTERESCARVIGENCIA);
              ELSIF RS.PREANO <= (EXTRACT(YEAR FROM SYSDATE) - 2) THEN
                  MI_CAMPOS  := 'C12 = '|| (MI_RSFAC12 + MI_DBLINTERESCARVIGENCIA);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );
              MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_DBLINTERESCARVIGENCIA;
              MI_DBLINTERESCARVIGENCIA := 0;

              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND MI_DBLCARVIGENCIA = 0 AND 
                 MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
                MI_CAMPOS  := 'PREANO ='|| RS.PREANO;

                PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                           UN_TABLA         => UN_TABLA,
                                           UN_CODIGO        => RSCO.CODIGO, 
                                           UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                           UN_CAMPOS        => MI_CAMPOS, 
                                           UN_CUOTA         => MI_RSFACUOTA,
                                           UN_CONDICION     => '0');
              END IF;
            ELSIF (MI_SALDOADISTRIBUIR < MI_DBLINTERESCARVIGENCIA) AND (MI_DBLINTERESCARVIGENCIA <> 0) THEN
              MI_DBLABONOINTERESCARANT := 0;

              IF RS.PREANO = EXTRACT(YEAR FROM SYSDATE) THEN
                  MI_CAMPOS  := 'C4 = '|| (MI_RSFAC4 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO = (EXTRACT(YEAR FROM SYSDATE) - 1) THEN
                  MI_CAMPOS  := 'C8 = '|| (MI_RSFAC8 + MI_SALDOADISTRIBUIR);
              ELSIF RS.PREANO <= (EXTRACT(YEAR FROM SYSDATE) - 2) THEN
                  MI_CAMPOS  := 'C12 = '|| (MI_RSFAC12 + MI_SALDOADISTRIBUIR);
              END IF;
              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA );

              MI_DBLINTERESCARVIGENCIA := MI_DBLINTERESCARVIGENCIA - MI_SALDOADISTRIBUIR;
              MI_SALDOADISTRIBUIR := 0;
              MI_CAMPOS  := 'PREANO ='|| RS.PREANO;

              PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                         UN_TABLA         => UN_TABLA,
                                         UN_CODIGO        => RSCO.CODIGO, 
                                         UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                         UN_CAMPOS        => MI_CAMPOS, 
                                         UN_CUOTA         => MI_RSFACUOTA,
                                         UN_CONDICION     => '0');
              IF MI_DBLCAPITALVIGENCIA = 0 AND MI_DBLINTERESVIGENCIA = 0 AND 
                 MI_DBLCARVIGENCIA = 0 AND MI_DBLBOMBEROSVIGENCIA = 0 AND MI_DBLINTERESCARVIGENCIA = 0 THEN
                MI_NCUOTA := MI_RSFACUOTA;
              ELSE
                MI_INTPREANOPENDIENTE := RS.PREANO;
              END IF;
            END IF;
          END IF;
        END IF;
      END LOOP CONCEPTOS;
    END LOOP ACUERDOSFACTURADOS;
    CLOSE MI_RSFA;
    MI_DBLVALORVIGENCIA := 0;
    MI_DBLINTERESVIGENCIA := 0;
    MI_DBLCAPITALVIGENCIA := 0;
    MI_DBLBOMBEROSVIGENCIA := 0;
    MI_DBLINTERESCARVIGENCIA := 0;
  END LOOP FACTURADOS;

  MI_STRSQL := 'SELECT MAX(CUOTA) MAXCUOTA 
                FROM  '||UN_TABLA||' 
                WHERE CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''';
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_MAXCUOTA;

  IF MI_MAXCUOTA IS NOT NULL THEN
    MI_NCUOTA:=MI_MAXCUOTA;
  END IF;

  MI_STRSQL := 'SELECT SUM((TOTAL)-(C1+C2+C3+C4+C5+C6+C7+C8+C9+C10+C11+C12+C13+C14+C15+C16+C17+C18+C19+C20+INTERES_ACUERDO+INTERES_RECARGO)) AJUSTE
                FROM '||UN_TABLA||' 
                WHERE CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''';
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_TOTAL;

  IF MI_TOTAL IS NOT NULL THEN
    MI_DBLAJUSTE:=NVL(MI_TOTAL,0);
  END IF;

  IF MI_DBLAJUSTE > 0 THEN
    MI_STRSQL := 'SELECT C1, C2, C3, C4, C5, C6, C7, C8, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, CUOTA 
                  FROM '||UN_TABLA||' 
                  WHERE CODIGOACUERDO = '''||UN_CODIGOACUERDO||''' 
                    AND CUOTA         = '||MI_NCUOTA;
    <<ACUERDOSFACTURADOSAJUSTE>>   
    OPEN MI_RSFA FOR MI_STRSQL;
    LOOP
    FETCH MI_RSFA INTO MI_RSFAC1, MI_RSFAC2, MI_RSFAC3, MI_RSFAC4, MI_RSFAC5,MI_RSFAC6, MI_RSFAC7, MI_RSFAC8, MI_RSFAC8, MI_RSFAC9, MI_RSFAC10, MI_RSFAC11, MI_RSFAC12, MI_RSFAC13, MI_RSFAC14, MI_RSFAC15, MI_RSFAC16, MI_RSFAC17, MI_RSFAC18, MI_RSFAC19, MI_RSFAC20, MI_RSFACUOTA;
    EXIT WHEN MI_RSFA%NOTFOUND;  
      MI_CAMPOS:=NULL;
      IF MI_RSFAC1 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C1 = '|| (MI_RSFAC1 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC2 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C2 = '|| (MI_RSFAC2 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC3 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C3 = '|| (MI_RSFAC3 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC4 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C4 = '|| (MI_RSFAC4 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC5 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C5 = '|| (MI_RSFAC5 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC6 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C6 = '|| (MI_RSFAC6 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC7 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C7 = '|| (MI_RSFAC7 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC8 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C8 = '|| (MI_RSFAC8 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC9 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C9 = '|| (MI_RSFAC9 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC10 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C10 = '|| (MI_RSFAC10 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC11 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C11 = '|| (MI_RSFAC11 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC12 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C12 = '|| (MI_RSFAC12 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC13 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C13 = '|| (MI_RSFAC13 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC14 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C14 = '|| (MI_RSFAC14 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC15 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C15 = '|| (MI_RSFAC15 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC16 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C16 = '|| (MI_RSFAC16 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC17 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C17 = '|| (MI_RSFAC17 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC18 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C18 = '|| (MI_RSFAC18 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC19 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C19 = '|| (MI_RSFAC19 + MI_DBLAJUSTE);
      ELSIF MI_RSFAC20 >= MI_DBLAJUSTE THEN
          MI_CAMPOS  := 'C20 = '|| (MI_RSFAC20 + MI_DBLAJUSTE);
      END IF;
      IF MI_CAMPOS IS NOT NULL THEN
      PR_ACTUALIZARACUFACTURADOS(UN_COMPANIA      => UN_COMPANIA,
                                 UN_TABLA         => UN_TABLA,
                                 UN_CODIGO        => '',
                                 UN_CODIGOACUERDO => UN_CODIGOACUERDO, 
                                 UN_CAMPOS        => MI_CAMPOS, 
                                 UN_CUOTA         => MI_RSFACUOTA );        
      END IF;
    END LOOP ACUERDOSFACTURADOSAJUSTE;
    CLOSE MI_RSFA;
  END IF;
END PR_DISTRIBUIRACUERDOACACIAS;

PROCEDURE PR_ACTUALIZARACUFACTURADOS
/*
      NAME              : PR_ACTUALIZARACUERDOSFACTURADOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : UN_COMPANIA       => COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_TABLA          => TABLA EN LA CUAL SE VA A REALIZAR LA OPERACION
                          UN_CODIGO         => CODIGO DEL CONCEPTO RELACIONADO CON EL ACUERDO     
                          UN_CODIGOACUERDO  => CODIGO DEL ACUERDO POR EL CUAL SE VA A FILTRAR  
                          UN_CAMPOS         => CAMPOS A ACTUALIZAR EN LA TABLA 
                          UN_CUOTA          => NUMERO DE CUOTA POR EL CUAL SE VA A FILTRAR
                          UN_CONDICION      => PARAMETRO QUE IDENTIFICA SI SE ACTUALIZO UN PREANO O NO
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    actualizarAcuerdosFacturados 
    */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TABLA           IN PCK_SUBTIPOS.TI_TABLA,
  UN_CODIGO          IN IP_CONCEPTOS.CODIGO%TYPE,
  UN_CODIGOACUERDO   IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
  UN_CAMPOS          IN PCK_SUBTIPOS.TI_CAMPOS,
  UN_CUOTA           IN IP_TMP_FACTURADOSACUERDOS.CUOTA%TYPE,
  UN_CONDICION       IN VARCHAR2 := '1'
)
AS
MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_CONDICION := 'COMPANIA = '''             || UN_COMPANIA      ||
                  ''' AND  CODIGOACUERDO = '''|| UN_CODIGOACUERDO ||
                  ''' AND CUOTA = '           || UN_CUOTA;

  BEGIN 
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  UN_TABLA, 
                                             UN_ACCION    =>  'M', 
                                             UN_CAMPOS    =>  UN_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION );
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
             MI_MSGERROR(1).CLAVE := 'CODIGO';
             MI_MSGERROR(1).VALOR := 'con concepto numero '||UN_CODIGO;
             MI_MSGERROR(2).CLAVE := 'ANIO';
             IF UN_CONDICION='0' THEN
              MI_MSGERROR(2).VALOR := ' el año del';
             ELSE
              MI_MSGERROR(2).VALOR := 'el';
             END IF;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_CON_FACT_ACU,
                          UN_REEMPLAZOS =>  MI_MSGERROR

          );
  END;
END PR_ACTUALIZARACUFACTURADOS;

--15
FUNCTION FC_ACTIVARESERVA
  /*
    OBJETIVO              : Actualizar el porcentaje de reserva del facturado de un usuario de predial.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_CODIGO: Código del usuario de predial.
                            UN_PORCENTAJE: Valor del porcentaje de reserva que será actualizado.
                            UN_VIGENCIA: Año en el que se actualizara el porcentaje de reserva.
                            UN_ORDENPREDIAL: Valor del numero de orden predial.
                            UN_USUARIO: Codigo del usuario que llama la funcion.
                            UN_RESOLUCION: Valor en la resolucion de la reserva.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : PABLO ANDRES ESPITIA CUCA
    FECHA                 : 23/02/2017 11:45 AM
    REALIZADO POR:        : STEFANINI SYSMAN
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    @METHOD: PUT
    @NAME:   activarReserva
  */
(
  UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO		    IN IP_USUARIOS_PREDIAL.CODIGO%TYPE,
  UN_PORCENTAJE   IN PCK_SUBTIPOS.TI_PORCENTAJE,
  UN_VIGENCIA	    IN PCK_SUBTIPOS.TI_ANIO,
  UN_ORDENPREDIAL IN PCK_SUBTIPOS.TI_NUMORDEN,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_RESOLUCION   IN IP_FACTURADOS.RESOLUCION_RESERVA%TYPE
)
RETURN NUMBER
AS
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA	     PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TABLA	   PCK_SUBTIPOS.TI_TABLA;
BEGIN
  MI_TABLA := 'IP_USUARIOS_PREDIAL';

  MI_CAMPOS := ' INDICADOR_RESERVA  = -1
                ,PORCENTAJE_RESERVA = '''||UN_PORCENTAJE||'''
  			        ,MODIFIED_BY        = '''||UN_USUARIO||'''
  			        ,DATE_MODIFIED      = SYSDATE ';

  MI_CONDICION := '    COMPANIA 	   = '''||UN_COMPANIA||'''
                   AND CODIGO   	   = '''||UN_CODIGO||'''
                   AND NUMERO_ORDEN  = '''||UN_ORDENPREDIAL||'''';

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      MI_MSGERROR(2).CLAVE := 'ENTE';
      MI_MSGERROR(2).VALOR := 'usuario';      
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_USUPRE_ACTRES
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END;

  --ACTUALIZAR 'IP_FACTURADOS'
  MI_TABLA := 'IP_FACTURADOS';

  MI_CAMPOS := ' PORCENTAJE_RESERVA = '||UN_PORCENTAJE||'
                ,RESOLUCION_RESERVA = '''||UN_RESOLUCION||'''
                ,MODIFIED_BY   		  = '''||UN_USUARIO||'''
                ,DATE_MODIFIED 		  = SYSDATE';

  MI_CONDICION := MI_CONDICION || ' AND PREANO = '||UN_VIGENCIA;

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      MI_MSGERROR(2).CLAVE := 'ENTE';
      MI_MSGERROR(2).VALOR := 'facturado';       
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_USUPRE_ACTRES
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END; 

  RETURN MI_RTA;
END FC_ACTIVARESERVA;

--16
PROCEDURE PR_CANCELARRESERVA
  /*
    OBJETIVO              : Cancelar el porcentaje de reserva del facturado y el usuario de predial.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_CODIGO: Código del usuario de predial.
                            UN_VIGENCIA: Año en el que se actualizara el porcentaje de reserva.
                            UN_ORDENPREDIAL: Valor del numero de orden predial.
                            UN_USUARIO: Codigo del usuario que llama la funcion.
                            UN_RESOLUCION: Valor en la resolucion de la reserva.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : PABLO ANDRES ESPITIA CUCA
    FECHA                 : 23/02/2017 16:41 AM
    REALIZADO POR:        : STEFANINI SYSMAN
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    @METHOD: PUT
    @NAME:   cancelarReserva
  */
(
  UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO		    IN IP_USUARIOS_PREDIAL.CODIGO%TYPE,
  UN_VIGENCIA	    IN PCK_SUBTIPOS.TI_ANIO,
  UN_ORDENPREDIAL IN PCK_SUBTIPOS.TI_NUMORDEN,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_RESOLUCION   IN IP_FACTURADOS.RESOLUCION_RESERVA%TYPE
)
AS
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA	     PCK_SUBTIPOS.TI_LOGICO;
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TABLA	   PCK_SUBTIPOS.TI_TABLA;
  MI_CANT	     PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  --ACTUALIZAR 'IP_FACTURADOS'
  MI_TABLA := 'IP_FACTURADOS';

  MI_CAMPOS := ' RESOLUCION_DESRESERVA = '''||UN_RESOLUCION||'''
                ,CONSECUTIVO           = 0
                ,PORCENTAJE_RESERVA    = 0
                ,RESOLUCION_RESERVA    = 0
                ,MODIFIED_BY           = '''||UN_USUARIO||'''
                ,DATE_MODIFIED         = SYSDATE';

  MI_CONDICION := '   COMPANIA 	   = '''||UN_COMPANIA||'''
                  AND CODIGO   	   = '''||UN_CODIGO||'''
                  AND NUMERO_ORDEN = '''||UN_ORDENPREDIAL||'''
                  AND PREANO       = '||UN_VIGENCIA;

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      MI_MSGERROR(2).CLAVE := 'ENTE';
      MI_MSGERROR(2).VALOR := 'facturado';      
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_CANCELARRESERVA
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END;

  SELECT COUNT(1) CANT
  INTO MI_CANT
  FROM IP_FACTURADOS
  WHERE COMPANIA     = UN_COMPANIA
    AND CODIGO       = UN_CODIGO
    AND NUMERO_ORDEN = UN_ORDENPREDIAL
    AND RESOLUCION_RESERVA IS NOT NULL
    AND NVL(PORCENTAJE_RESERVA,0) > 0;

  IF MI_CANT IN(0) THEN
    MI_TABLA := 'IP_USUARIOS_PREDIAL';

    MI_CAMPOS := ' INDICADOR_RESERVA  = 0
                  ,PORCENTAJE_RESERVA = 0
                  ,MODIFIED_BY        = '''||UN_USUARIO||'''
                  ,DATE_MODIFIED      = SYSDATE ';

    MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                    AND CODIGO       = '''||UN_CODIGO||'''
                    AND NUMERO_ORDEN = '''||UN_ORDENPREDIAL||'''';

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                 THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGO;
        MI_MSGERROR(2).CLAVE := 'ENTE';
        MI_MSGERROR(2).VALOR := 'usuario';       
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_CANCELARRESERVA
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_MSGERROR
                                  );    
    END;     
  END IF;

END PR_CANCELARRESERVA;

PROCEDURE PR_REVERSARPAGOSEXCEDENTES 
/*
      NAME              : PR_REVERSARPAGOSEXCEDENTES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : 
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    reversarPagosExcedentes
    */
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUM_FACTURA   IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_COD_PREDIO    IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_COD_ACUERDO   IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
    MI_CUOTAXANULAR      PCK_SUBTIPOS.TI_ENTERO;
    MI_CUOTACERO         PCK_SUBTIPOS.TI_DOBLE;
    MI_CUOTASPAGADAS     PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    BEGIN 
        SELECT DISTINCT CUOTA 
          INTO MI_CUOTAXANULAR 
          FROM IP_FACTURADOSACUERDOSEXC 
         WHERE COMPANIA      = UN_COMPANIA
           AND CODIGOACUERDO = UN_COD_ACUERDO  
           AND PREDIO        = UN_COD_PREDIO  
           AND DOCNUM        = UN_NUM_FACTURA  
         ORDER BY CUOTA DESC;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTAXANULAR:=0;
    END;  
    BEGIN 
        SELECT COUNT(CUOTA) CERO 
          INTO MI_CUOTACERO 
          FROM IP_FACTURADOSACUERDOSEXC 
         WHERE COMPANIA      = UN_COMPANIA
           AND CODIGOACUERDO = UN_COD_ACUERDO  
           AND PREDIO        = UN_COD_PREDIO  
           AND CUOTA         = 0;
    EXCEPTION WHEN  NO_DATA_FOUND THEN 
        MI_CUOTACERO:=0;
    END;  
    BEGIN 
        SELECT COUNT(CUOTA) PAGADAS 
          INTO MI_CUOTASPAGADAS
          FROM IP_FACTURADOSACUERDOSEXC 
         WHERE COMPANIA      = UN_COMPANIA
           AND CODIGOACUERDO = UN_COD_ACUERDO
           AND PREDIO        = UN_COD_PREDIO
           AND PAGADO        NOT IN (0);
    EXCEPTION WHEN  NO_DATA_FOUND THEN 
        MI_CUOTASPAGADAS:=0;
    END;  
    IF MI_CUOTACERO > 0 
       AND MI_CUOTAXANULAR + 1 = MI_CUOTASPAGADAS THEN
        MI_CUOTAXANULAR:= MI_CUOTASPAGADAS;
    END IF;
    IF MI_CUOTASPAGADAS = MI_CUOTAXANULAR THEN
        BEGIN 
            BEGIN 
                MI_TABLA:= 'IP_ACUERDOSEXC';
                MI_CAMPOS:= ' CANCELADO = 0,
                              MODIFIED_BY = '''||UN_USUARIO||''',
                              DATE_MODIFIED = SYSDATE '; 
                MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                  AND PREDIO        = '''||UN_COD_PREDIO ||''' 
                                  AND CODIGOACUERDO = '''||UN_COD_ACUERDO||''' ';
                PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_COD_ACUERDO';
                MI_REEMPLAZOS(1).VALOR:=UN_COD_ACUERDO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOEXCEDENTE1
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN 
                MI_TABLA:= 'IP_FACTURADOSACUERDOSEXC';
                MI_CAMPOS:= '  DOCNUM    = NULL
                             , PAGADO    = 0
                             , PAG_BAN   = NULL
                             , FECHAPAGO = NULL 
                             , MODIFIED_BY = '''||UN_USUARIO||'''
                             , DATE_MODIFIED = SYSDATE ';
                MI_CONDICION:='    COMPANIA = '''||UN_COMPANIA||'''
                               AND PREDIO   = '''||UN_COD_PREDIO||'''
                               AND DOCNUM   = '''||UN_NUM_FACTURA||''' ';
                PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
                              UN_TABLA     => MI_TABLA
                             ,UN_ACCION    => 'M'
                             ,UN_CAMPOS    => MI_CAMPOS
                             ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOEXCEDENTE2
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN 
                MI_TABLA:= 'IP_RECIBOS_DE_PAGO';
                MI_CAMPOS:= '  PAGO               = 0
                             , ANULADO            = -1
                             , PAG_BANPAG         = NULL
                             , FECHA_REGRECAUDO   = NULL
                             , USUARIO_REGRECAUDO = NULL
                             , PREFECPAG          = NULL
                             , PAQUETEPAG         = NULL
                             , ANULADO_POR        = '''||UN_USUARIO||'''
                             , MODIFIED_BY        = '''||UN_USUARIO||'''
                             , DATE_MODIFIED      = SYSDATE '; 
                MI_CONDICION:='     COMPANIA = '''||UN_COMPANIA||'''
                                AND DOCNUM   =  '''||UN_NUM_FACTURA||''' ';
                PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
                                UN_TABLA     => MI_TABLA
                               ,UN_ACCION    => 'M'
                               ,UN_CAMPOS    => MI_CAMPOS
                               ,UN_CONDICION => MI_CONDICION );   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(0).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOEXCEDENTE3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN 
                MI_TABLA:= 'IP_PAGO_BANCOSDET';
                MI_CONDICION:='    COMPANIA   = '''||UN_COMPANIA||''' 
                               AND PRECOD     = '''||UN_COD_PREDIO||''' 
                               AND NUMFACTURA = '''||UN_NUM_FACTURA||''' ';
                PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(
                                         UN_TABLA     => MI_TABLA
                                        ,UN_ACCION    => 'E'
                                        ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOEXCEDENTE4
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
    END IF;
END PR_REVERSARPAGOSEXCEDENTES;

PROCEDURE PR_REVERSARPAGOSCUOTASACUERDOS
/*
      NAME              : PR_REVERSARPAGOSEXCEDENTES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : 
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    reversarPagosCuotasAcuerdos
    */
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUM_FACTURA   IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_COD_PREDIO    IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_COD_ACUERDO   IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
    UN_ABONOAACUERDO IN PCK_SUBTIPOS.TI_LOGICO
)
AS 
    MI_CUOTAXANULAR      PCK_SUBTIPOS.TI_ENTERO;
    MI_CUOTACERO         PCK_SUBTIPOS.TI_DOBLE;
    MI_CUOTASPAGADAS     PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MERGEUSING        PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE       PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE       PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN
    BEGIN
        SELECT MIN(IP_FACTURADOSACUERDOS.CUOTA ) KEEP (DENSE_RANK FIRST ORDER BY IP_FACTURADOSACUERDOS.CUOTA DESC)CUOTA 
        INTO   MI_CUOTAXANULAR
        FROM   IP_FACTURADOSACUERDOS 
        WHERE  IP_FACTURADOSACUERDOS.COMPANIA      = UN_COMPANIA
          AND  IP_FACTURADOSACUERDOS.CODIGOACUERDO = UN_COD_ACUERDO 
          AND  IP_FACTURADOSACUERDOS.PREDIO        = UN_COD_PREDIO 
          AND  IP_FACTURADOSACUERDOS.DOCNUM        = UN_NUM_FACTURA 
        ORDER BY IP_FACTURADOSACUERDOS.CUOTA DESC;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTAXANULAR:=0;
    END;
    BEGIN        
        SELECT COUNT(IP_FACTURADOSACUERDOS.CUOTA)  CERO 
        INTO   MI_CUOTACERO
        FROM   IP_FACTURADOSACUERDOS 
        WHERE  IP_FACTURADOSACUERDOS.COMPANIA      = UN_COMPANIA
          AND  IP_FACTURADOSACUERDOS.CODIGOACUERDO = UN_COD_ACUERDO 
          AND  IP_FACTURADOSACUERDOS.PREDIO        = UN_COD_PREDIO 
          AND  IP_FACTURADOSACUERDOS.CUOTA         IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTACERO :=0;
    END;
    BEGIN  
        SELECT COUNT(IP_FACTURADOSACUERDOS.CUOTA) PAGADAS
          INTO MI_CUOTASPAGADAS
          FROM IP_FACTURADOSACUERDOS 
         WHERE IP_FACTURADOSACUERDOS.COMPANIA      = UN_COMPANIA 
           AND IP_FACTURADOSACUERDOS.CODIGOACUERDO = UN_COD_ACUERDO 
           AND IP_FACTURADOSACUERDOS.PREDIO        = UN_COD_PREDIO
           AND IP_FACTURADOSACUERDOS.PAGADO        NOT IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTASPAGADAS:=0;
    END;
    IF MI_CUOTACERO > 0 
       AND MI_CUOTAXANULAR + 1 = MI_CUOTASPAGADAS THEN 
        MI_CUOTAXANULAR := MI_CUOTASPAGADAS;
    END IF;   
    IF MI_CUOTASPAGADAS = MI_CUOTAXANULAR THEN
        BEGIN 
            BEGIN 
                --Actualiza los predios que tengan factura de acuerdo
                MI_TABLA := 'IP_USUARIOS_PREDIAL';
                MI_CAMPOS := '   IP_USUARIOS_PREDIAL.PAGO_ANO      = IP_USUARIOS_PREDIAL.PAGO_ANO1
                               , IP_USUARIOS_PREDIAL.PAG_VAL       = IP_USUARIOS_PREDIAL.PAG_VAL1
                               , IP_USUARIOS_PREDIAL.PAG_FEC       = IP_USUARIOS_PREDIAL.PAG_FEC1
                               , IP_USUARIOS_PREDIAL.PAG_BAN       = IP_USUARIOS_PREDIAL.PAG_BAN1
                               , IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                               , IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA        = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO          = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN    = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURA_ACUERDO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    =>'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOACU
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;    
        BEGIN 
            BEGIN 
                MI_CAMPOS := '  IP_USUARIOS_PREDIAL.PAGO_ANO1     = IP_USUARIOS_PREDIAL.PAGO_ANO2
                              , IP_USUARIOS_PREDIAL.PAG_VAL1      = IP_USUARIOS_PREDIAL.PAG_VAL2
                              , IP_USUARIOS_PREDIAL.PAG_FEC1      = IP_USUARIOS_PREDIAL.PAG_FEC2
                              , IP_USUARIOS_PREDIAL.PAG_BAN1      = IP_USUARIOS_PREDIAL.PAG_BAN2
                              , IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              , IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA        = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO          = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN    = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURA_ACUERDO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC2
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN
              MI_CAMPOS := '  IP_USUARIOS_PREDIAL.NUM_COM         = IP_USUARIOS_PREDIAL.NUM_COM1
                            , IP_USUARIOS_PREDIAL.NUM_COM1        = IP_USUARIOS_PREDIAL.NUM_COM2
                            , IP_USUARIOS_PREDIAL.FACTURA_ACUERDO = IP_USUARIOS_PREDIAL.NUM_COM2
                            , IP_USUARIOS_PREDIAL.MODIFIED_BY     = '''||UN_USUARIO||'''
                            , IP_USUARIOS_PREDIAL.DATE_MODIFIED   = SYSDATE ';
              MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA        = '''||UN_COMPANIA||'''
                           AND IP_USUARIOS_PREDIAL.CODIGO          = '''||UN_COD_PREDIO||'''
                           AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN    = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                           AND IP_USUARIOS_PREDIAL.FACTURA_ACUERDO = '''||UN_NUM_FACTURA||'''';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                            UN_TABLA      => MI_TABLA
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS 
                                           ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN 
                MI_CAMPOS := '  IP_USUARIOS_PREDIAL.PAGO_ANO2     = 0
                              , IP_USUARIOS_PREDIAL.PAG_VAL2      = 0
                              , IP_USUARIOS_PREDIAL.PAG_FEC2      = NULL
                              , IP_USUARIOS_PREDIAL.NUM_COM2      = NULL 
                              , IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              , IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA        = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO          = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN    = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURA_ACUERDO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA      => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC4
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN
            BEGIN 
                --Actualiza los acuerdos del predio 
                MI_TABLA := 'IP_ACUERDOS';
                MI_CAMPOS := '  IP_ACUERDOS.CANCELADO     = 0
                              , IP_ACUERDOS.MODIFIED_BY   = '''||UN_USUARIO||'''
                              , IP_ACUERDOS.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_ACUERDOS.COMPANIA      = '''||UN_COMPANIA||'''
                             AND IP_ACUERDOS.CODIGOACUERDO = '''||UN_COD_ACUERDO||'''
                             AND IP_ACUERDOS.PREDIO        = '''||UN_COD_PREDIO||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA      => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_COD_ACUERDO';
                MI_REEMPLAZOS(1).VALOR:=UN_COD_ACUERDO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOACUERDOACU
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN
                --Actualiza el usuario que tenga pago de acuerdo del predio recibido
                MI_TABLA := 'IP_USUARIOS_PREDIAL';
                MI_CAMPOS := '  IP_USUARIOS_PREDIAL.PAGO_ACUERDO  = -1
                              , IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              , IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA     = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO       = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M' 
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC5
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;                                            
        BEGIN 
            BEGIN
                MI_TABLA := 'IP_FACTURADOSACUERDOS';
                --Actualiza las facturas en acuerdos
                MI_CAMPOS := '  IP_FACTURADOSACUERDOS.DOCNUM        = NULL
                              , IP_FACTURADOSACUERDOS.PAGADO        = 0
                              , IP_FACTURADOSACUERDOS.PAG_BAN       = NULL
                              , IP_FACTURADOSACUERDOS.FECHAPAGO     = NULL
                              , IP_FACTURADOSACUERDOS.MODIFIED_BY   = '''||UN_USUARIO||'''
                              , IP_FACTURADOSACUERDOS.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_FACTURADOSACUERDOS.COMPANIA =  '''||UN_COMPANIA||'''
                             AND IP_FACTURADOSACUERDOS.PREDIO   = '''||UN_COD_PREDIO||'''
                             AND IP_FACTURADOSACUERDOS.DOCNUM   = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA 
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC6
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN
            BEGIN 
                --Actualiza los recibos de pago 
                MI_TABLA:='IP_RECIBOS_DE_PAGO';
                MI_CAMPOS := ' IP_RECIBOS_DE_PAGO.PAGO               = 0 
                             , IP_RECIBOS_DE_PAGO.ANULADO            = -1
                             , IP_RECIBOS_DE_PAGO.FECHA_REGRECAUDO   = NULL
                             , IP_RECIBOS_DE_PAGO.USUARIO_REGRECAUDO = NULL 
                             , IP_RECIBOS_DE_PAGO.PREFECPAG          = NULL
                             , IP_RECIBOS_DE_PAGO.PAQUETEPAG         = NULL 
                             , IP_RECIBOS_DE_PAGO.ANULADO_POR        = '''||UN_USUARIO||'''
                             , IP_RECIBOS_DE_PAGO.MODIFIED_BY        = '''||UN_USUARIO||'''
                             , IP_RECIBOS_DE_PAGO.DATE_MODIFIED      = SYSDATE ';
                MI_CONDICION :='IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                             AND IP_RECIBOS_DE_PAGO.DOCNUM  = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(0).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC7
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;        
        --Elimina los pagos de los bancos  de el predio y número de factura recibidos por parámetro
        MI_TABLA:='IP_PAGO_BANCOSDET';
        MI_CONDICION := '     IP_PAGO_BANCOSDET.COMPANIA   = '''||UN_COMPANIA||'''
                          AND IP_PAGO_BANCOSDET.PRECOD     = '''||UN_COD_PREDIO||'''
                          AND IP_PAGO_BANCOSDET.NUMFACTURA = '''||UN_NUM_FACTURA||''' ';
        BEGIN   
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'E'
                                             ,UN_CONDICION => MI_CONDICION);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(0).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC8
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;

        --'(TAR:1000068302; FECHA:13/12/2016; AUTOR:AA)
        --'Se incluye condición para que cuando se reverse un pago de una factura de abono en acuerdo de pago se deje el estado incial del facturado acuerdo
        IF NVL(UN_ABONOAACUERDO, 0) NOT IN (0) 
               THEN
            MI_TABLA:= 'IP_FACTURADOSACUERDOS';
            MI_MERGEUSING := 'SELECT     
                                 FA_CPY.CODIGOACUERDO
                                ,FA_CPY.PREDIO
                                ,FA_CPY.CUOTA
                                ,FA_CPY.DOCNUM
                                ,FA_CPY.PAGADO
                                ,FA_CPY.C1
                                ,FA_CPY.C2
                                ,FA_CPY.C3
                                ,FA_CPY.C4
                                ,FA_CPY.C5
                                ,FA_CPY.C6
                                ,FA_CPY.C7
                                ,FA_CPY.C8
                                ,FA_CPY.C9
                                ,FA_CPY.C10
                                ,FA_CPY.C11
                                ,FA_CPY.C12
                                ,FA_CPY.C13
                                ,FA_CPY.C14
                                ,FA_CPY.C15 
                                ,FA_CPY.C16
                                ,FA_CPY.C17
                                ,FA_CPY.C18
                                ,FA_CPY.C19
                                ,FA_CPY.C20
                                ,FA_CPY.TOTAL
                                ,FA_CPY.CAPITAL
                                ,FA_CPY.INTERESES
                                ,FA_CPY.INTERES_ACUERDO
                                ,FA_CPY.INTERES_RECARGO
                                ,FA_CPY.FECHAFACTURADO
                                ,FA_CPY.PAG_BAN
                                ,FA_CPY.FECHAPAGO
                                ,FA_CPY.PREANOI
                                ,FA_CPY.PREANO
                                ,FA_CPY.INTERESES 
                           FROM IP_FACTURADOSACUERDOS FA 
                              INNER JOIN IP_FACTURADOSACUERDOS_CPY FA_CPY 
                                  ON  FA.COMPANIA      = FA_CPY.COMPANIA
                                  AND FA.CUOTA         = FA_CPY.CUOTA
                                  AND FA.PREDIO        = FA_CPY.PREDIO
                                  AND FA.CODIGOACUERDO = FA_CPY.CODIGOACUERDO
                            WHERE FA_CPY.COMPANIA         = '''||UN_COMPANIA||''' 
                              AND FA_CPY.CODIGOACUERDO    = '''||UN_COD_ACUERDO||'''
                              AND FA_CPY.PREDIO           = '''||UN_COD_PREDIO||''' 
                              AND FA_CPY.DOCNUMGENERADOR  = '''||UN_NUM_FACTURA||''' ';
            MI_MERGEENLACE:= '    	TABLA.COMPANIA      = VISTA.COMPANIA
                                AND TABLA.CUOTA         = VISTA.CUOTA
                                AND TABLA.PREDIO        = VISTA.PREDIO
                                AND TABLA.CODIGOACUERDO = VISTA.CODIGOACUERDO';
            MI_MERGEEXISTE:= ' UPDATE SET 
                           TABLA.CODIGOACUERDO        = VISTA.CODIGOACUERDO
                          ,TABLA.PREDIO               = VISTA.PREDIO
                          ,TABLA.CUOTA                = VISTA.CUOTA
                          ,TABLA.DOCNUM               = VISTA.DOCNUM
                          ,TABLA.PAGADO               = VISTA.PAGADO
                          ,TABLA.C1                   = VISTA.C1
                          ,TABLA.C2                   = VISTA.C2
                          ,TABLA.C3                   = VISTA.C3
                          ,TABLA.C4                   = VISTA.C4
                          ,TABLA.C5                   = VISTA.C5
                          ,TABLA.C6                   = VISTA.C6
                          ,TABLA.C7                   = VISTA.C7
                          ,TABLA.C8                   = VISTA.C8
                          ,TABLA.C9                   = VISTA.C9
                          ,TABLA.C10                  = VISTA.C10
                          ,TABLA.C11                  = VISTA.C11
                          ,TABLA.C12                  = VISTA.C12
                          ,TABLA.C13                  = VISTA.C13
                          ,TABLA.C14                  = VISTA.C14
                          ,TABLA.C15                  = VISTA.C15
                          ,TABLA.C16                  = VISTA.C16
                          ,TABLA.C17                  = VISTA.C17
                          ,TABLA.C18                  = VISTA.C18
                          ,TABLA.C19                  = VISTA.C19
                          ,TABLA.C20                  = VISTA.C20
                          ,TABLA.TOTAL                = VISTA.TOTAL
                          ,TABLA.CAPITAL              = VISTA.CAPITAL
                          ,TABLA.INTERESES            = VISTA.INTERESES
                          ,TABLA.INTERES_ACUERDO      = VISTA.INTERES_ACUERDO
                          ,TABLA.INTERES_RECARGO      = VISTA.INTERES_RECARGO
                          ,TABLA.FECHAFACTURADO       = VISTA.FECHAFACTURADO
                          ,TABLA.PAG_BAN              = VISTA.PAG_BAN
                          ,TABLA.FECHAPAGO             = VISTA.FECHAPAGO
                          ,TABLA.PREANOI               = VISTA.PREANOI
                          ,TABLA.PREANO                = VISTA.PREANO
                          ,TABLA.COPIAINTERES_ACUERDO  = VISTA.INTERESES 
                          ,TABLA.MODIFIED_BY           = '''||UN_USUARIO||'''
                          ,TABLA.DATE_MODIFIED         = SYSDATE ';
		        BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                                  UN_TABLA       => MI_TABLA
                                                 ,UN_ACCION      => 'MM'
                                                 ,UN_MERGEUSING  => MI_MERGEUSING
                                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                  );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                    MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                    MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                    MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                    MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                    MI_REEMPLAZOS(2).CLAVE:='UN_COD_ACUERDO';
                    MI_REEMPLAZOS(2).VALOR:=UN_COD_ACUERDO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOAC9
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
            MI_TABLA := 'IP_FACTURADOSACUERDOS_CPY';	
            MI_CONDICION:= '    COMPANIA        = '''||UN_COMPANIA||''' 
                            AND CODIGOACUERDO   = '''||UN_COD_ACUERDO||''' 
                            AND PREDIO          = '''||UN_COD_PREDIO||''' 
                            AND DOCNUMGENERADOR = '''||UN_NUM_FACTURA||''' ';
            BEGIN
                BEGIN 
                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                      UN_TABLA     => MI_TABLA
                                     ,UN_ACCION    => 'E'
                                     ,UN_CONDICION => MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                    MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                    MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                    MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                    MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                    MI_REEMPLAZOS(2).CLAVE:='UN_COD_ACUERDO';
                    MI_REEMPLAZOS(2).VALOR:=UN_COD_ACUERDO;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOA10
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;  
        END IF; 
    ELSE 
        --Mensaje se envia en el archivo properties
        --   MI_RTA := 'Únicamente puede eliminar la última cuota recaudada del Acuerdo de Pago' || 'El pago no fue Revertido Correctamente';
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
             ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVPAGOUSUARIOA12
              );	
        END;
        --MI_RTA := 'TB_TB1055';
        --RETURN MI_RTA;
    END IF;
END PR_REVERSARPAGOSCUOTASACUERDOS;

PROCEDURE PR_REVERSARPAGOFINAL 
/*
      NAME              : PR_REVERSARPAGOFINAL 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : 
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    reversarPagoFinal
    */
(
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUM_FACTURA IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_COD_PREDIO  IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_PREANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_PREANOI     IN PCK_SUBTIPOS.TI_ANIO,
    UN_PAG_BAN     IN IP_RECIBOS_DE_PAGO.PAG_BANPAG%TYPE,
    UN_PAQUETE     IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE,
    UN_FECHAPAGO   IN DATE,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONT              PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    IF UN_PREANOI IS NULL THEN 
        MI_CONT := 'AND IP_FACTURADOS.PREANO <= '||UN_PREANO||'';
    ELSE 
        MI_CONT := 'AND IP_FACTURADOS.PREANO BETWEEN '||UN_PREANOI||' 
                    AND '||UN_PREANO||'';
    END IF;
    MI_TABLA:='IP_FACTURADOS'; 
    MI_CAMPOS := 'IP_FACTURADOS.PAGADO        = 0 
                 ,IP_FACTURADOS.FECHAPAGO     = NULL
                 ,IP_FACTURADOS.DOCNUM        = NULL
                 ,IP_FACTURADOS.PREVAL        = 0
                 ,IP_FACTURADOS.MODIFIED_BY   = '''||UN_USUARIO||'''
                 ,IP_FACTURADOS.DATE_MODIFIED = SYSDATE ';
    MI_CONDICION := 'IP_FACTURADOS.COMPANIA      = '''||UN_COMPANIA||'''
                 AND IP_FACTURADOS.CODIGO        = '''||UN_COD_PREDIO||'''
                 AND IP_FACTURADOS.NUMERO_ORDEN  = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                 AND IP_FACTURADOS.DOCNUM        = '''||UN_NUM_FACTURA||'''
                 AND IP_FACTURADOS.INDEXE        IN (0)  
                 AND IP_FACTURADOS.NOCOBRADO     IN (0) 
                 AND IP_FACTURADOS.INDPAGO_ACPAG IN (0) 
                   '||MI_CONT||'';
    MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
    MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
    MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
    MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
    BEGIN
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( 
                                          UN_TABLA     => MI_TABLA 
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAFINAL1
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;   
    BEGIN     
        BEGIN 
            --Actualiza los pagos de Tipo s
            MI_TABLA := 'IP_PAGOSDOBLES';
            MI_CAMPOS := ' IP_PAGOSDOBLES.PAGO          = 0
                          ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||'''
                          ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'IP_PAGOSDOBLES.COMPANIA = '''||UN_COMPANIA||'''
                         AND IP_PAGOSDOBLES.PRECOD   = '''||UN_COD_PREDIO||'''
                         AND IP_PAGOSDOBLES.PREANO   BETWEEN '||NVL(UN_PREANOI, UN_PREANO)||' AND '||UN_PREANO||'
                         AND IP_PAGOSDOBLES.TIPO     = ''S''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( 
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAFINAL2
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;  
    BEGIN
        BEGIN
            --Actualiza los recibos de pago que esten el el código del predio , número de factura y la fecha de pago que se reciben por parámetro
            MI_TABLA:='IP_RECIBOS_DE_PAGO';
            MI_CAMPOS := 'IP_RECIBOS_DE_PAGO.PAGO            = 0 
                          ,IP_RECIBOS_DE_PAGO.ANULADO        = -1  
                          ,IP_RECIBOS_DE_PAGO.PREFECPAG      = NULL
                          ,IP_RECIBOS_DE_PAGO.PAQUETEPAG     = NULL  
                          ,IP_RECIBOS_DE_PAGO.FECHAANULACION = SYSDATE
                          ,IP_RECIBOS_DE_PAGO.ANULADO_POR    = '''||UN_USUARIO||'''
                          ,IP_RECIBOS_DE_PAGO.MODIFIED_BY    = '''||UN_USUARIO||'''
                          ,IP_RECIBOS_DE_PAGO.DATE_MODIFIED  = SYSDATE ';
            MI_CONDICION := 'IP_RECIBOS_DE_PAGO.COMPANIA   = '''||UN_COMPANIA||'''
                         AND IP_RECIBOS_DE_PAGO.DOCNUM     = '''||UN_NUM_FACTURA||'''
                         AND IP_RECIBOS_DE_PAGO.PRECOD     = '''||UN_COD_PREDIO||'''
                         AND IP_RECIBOS_DE_PAGO.PAGO       NOT IN(0)
                         AND IP_RECIBOS_DE_PAGO.ANULADO    IN(0)
                         AND IP_RECIBOS_DE_PAGO.PAG_BANPAG        = '''||UN_PAG_BAN||'''
                         AND IP_RECIBOS_DE_PAGO.PAQUETEPAG        = '''||UN_PAQUETE||''' 
                         AND TRUNC(IP_RECIBOS_DE_PAGO.PREFECPAG)  =  TO_DATE('''||UN_FECHAPAGO||''',''DD/MM/YYYY HH24:MI:SS'') '  ;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS 
                                                  ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAFINAL3
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;   
    IF PCK_DATOS.GL_RTA = 0 THEN 
        BEGIN 
            --MI_RTA := 'TB_TB1058';
            --MI_RTA := 'No se reverso correctamente la información del recibo pero se reversaron los facturados.' || 'El pago no fue Revertido Correctamente';
            --RETURN MI_RTA;
            MI_REEMPLAZOS(0).CLAVE:='UN_NUM_FACTURA';
            MI_REEMPLAZOS(0).VALOR:=UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            --MSG
            PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                 ,UN_TABLAERROR => MI_TABLA
                 ,UN_REEMPLAZOS => MI_REEMPLAZOS
                );
        END;
    END IF; 
    --Actualiza el predio recibido por parámetro
    MI_TABLA:='IP_USUARIOS_PREDIAL';
    MI_CAMPOS := ' IP_USUARIOS_PREDIAL.PAGO_ANO       = IP_USUARIOS_PREDIAL.PAGO_ANO1
                  ,IP_USUARIOS_PREDIAL.PAG_VAL        = IP_USUARIOS_PREDIAL.PAG_VAL1
                  ,IP_USUARIOS_PREDIAL.PAG_FEC        = IP_USUARIOS_PREDIAL.PAG_FEC1
                  ,IP_USUARIOS_PREDIAL.PAG_BAN        = IP_USUARIOS_PREDIAL.PAG_BAN1
                  ,IP_USUARIOS_PREDIAL.NUM_COM        = IP_USUARIOS_PREDIAL.NUM_COM1
                  ,IP_USUARIOS_PREDIAL.PAGO_ANO1      = IP_USUARIOS_PREDIAL.PAGO_ANO2
                  ,IP_USUARIOS_PREDIAL.PAG_VAL1       = IP_USUARIOS_PREDIAL.PAG_VAL2
                  ,IP_USUARIOS_PREDIAL.PAG_FEC1       = IP_USUARIOS_PREDIAL.PAG_FEC2
                  ,IP_USUARIOS_PREDIAL.PAG_BAN1       = IP_USUARIOS_PREDIAL.PAG_BAN2
                  ,IP_USUARIOS_PREDIAL.MODIFIED_BY    = '''||UN_USUARIO||'''
                  ,IP_USUARIOS_PREDIAL.DATE_MODIFIED  = SYSDATE ';
    MI_CONDICION := '      IP_USUARIOS_PREDIAL.COMPANIA     = '''||UN_COMPANIA||'''
                       AND IP_USUARIOS_PREDIAL.CODIGO       = '''||UN_COD_PREDIO||'''
                       AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||''' ';
    BEGIN 
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAFINAL4
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;

    BEGIN 
        BEGIN
            MI_TABLA:='IP_PAGO_BANCOSDET';
            MI_CONDICION := '      IP_PAGO_BANCOSDET.COMPANIA   = '''||UN_COMPANIA||'''
                               AND IP_PAGO_BANCOSDET.PRECOD     = '''||UN_COD_PREDIO||'''
                               AND IP_PAGO_BANCOSDET.NUMFACTURA = '''||UN_NUM_FACTURA||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA 
                                                  ,UN_ACCION    => 'E'
                                                  ,UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAFINAL5
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
END PR_REVERSARPAGOFINAL;

PROCEDURE PR_REVERSARABONOS 
/*
      NAME              : PR_REVERSARPAGOFINAL 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 22/02/2017
      TIME              : 11:10 AM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LA TABLA Y LOS CAMPOS SEGUN LOS PARAMETROS ENVIADOS POR EL PROCEDIMIENTO PR_DISTRIBUIRACUERDOACACIAS. 
      PARAMETERS        : 
      MODIFICATIONS     : 
      @METHOD:  PUT
      @NAME:    reversarAbonos
    */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUM_FACTURA  IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_COD_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
    UN_PREANO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_PREANOI      IN PCK_SUBTIPOS.TI_ANIO
)
AS 
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_ENTROFACAB        PCK_SUBTIPOS.TI_ENTERO;
    MI_MANEJAEXCEDENTES  PARAMETRO.VALOR%TYPE;
    MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ENTROFAC          PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    MI_ENTROFACAB := 1;
    MI_MANEJAEXCEDENTES := NVL(PCK_SYSMAN_UTL.FC_PAR(
                                              UN_COMPANIA  => UN_COMPANIA 
                                             ,UN_NOMBRE    => 'MANEJA EXCEDENTES DE VIG. ANTERIORES'
                                             ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
                                             ,UN_FECHA_PAR => SYSDATE)
                                 ,'NO');
    --Actualiza los facturados 
    BEGIN 
        BEGIN
            MI_TABLA:='IP_FACTURADOS';
            MI_CAMPOS := 'IP_FACTURADOS.PAGADO        = 0 
                         ,IP_FACTURADOS.PAG_BAN       = NULL 
                         ,IP_FACTURADOS.DOCNUM        = NULL
                         ,IP_FACTURADOS.PREVAL        = 0 
                         ,IP_FACTURADOS.FECHAPAGO     = NULL
                         ,IP_FACTURADOS.MODIFIED_BY   = '''||UN_USUARIO||'''
                         ,IP_FACTURADOS.DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'IP_FACTURADOS.COMPANIA = '''||UN_COMPANIA||''' 
                         AND IP_FACTURADOS.CODIGO   = '''||UN_COD_PREDIO||'''
                         AND IP_FACTURADOS.DOCNUM   = '''||UN_NUM_FACTURA||''' ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA 
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADOS_REV
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    IF MI_MANEJAEXCEDENTES = 'SI' THEN
        BEGIN
            BEGIN 
                --Actualiza los pagos dobles de los predios facturados
                MI_TABLA:='IP_PAGOSDOBLES';
                MI_CAMPOS := ' IP_PAGOSDOBLES.PAGO          = 0
                              ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_PAGOSDOBLES.COMPANIA = '''||UN_COMPANIA||''' 
                             AND IP_PAGOSDOBLES.PRECOD   = '''||UN_COD_PREDIO||'''
                             AND IP_PAGOSDOBLES.PREANO   <= '||UN_PREANO||' 
                             AND IP_PAGOSDOBLES.TIPO     = ''D''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( 
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    =>  'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_PREANO';
                MI_REEMPLAZOS(1).VALOR:=UN_PREANO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTPAGOSDOBLESREV
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;    
    ELSE 
        BEGIN
            BEGIN
                MI_TABLA:='IP_PAGOSDOBLES';
                MI_CAMPOS := ' IP_PAGOSDOBLES.PAGO          = 0
                              ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_PAGOSDOBLES.COMPANIA = '''||UN_COMPANIA||''' 
                             AND IP_PAGOSDOBLES.PRECOD   = '''||UN_COD_PREDIO||'''
                             AND IP_PAGOSDOBLES.PREANO   BETWEEN '||UN_PREANOI||'  AND '||UN_PREANO||' 
                             AND IP_PAGOSDOBLES.TIPO     = ''D''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA      => MI_TABLA 
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS 
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_PREANO';
                MI_REEMPLAZOS(1).VALOR:=UN_PREANO;
                MI_REEMPLAZOS(2).CLAVE:='UN_PREANOI';
                MI_REEMPLAZOS(2).VALOR:=UN_PREANOI;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTPAGDOBLESNOREV
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;    
    END IF;
    BEGIN 
        BEGIN
            MI_TABLA:='IP_PAGOSDOBLES';
            MI_CAMPOS := ' IP_PAGOSDOBLES.PAGO           = 0
                          ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||'''
                          ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'IP_PAGOSDOBLES.COMPANIA = '''||UN_COMPANIA||''' 
                         AND IP_PAGOSDOBLES.PRECOD   BETWEEN '||UN_PREANOI||' AND '||UN_PREANO||' 
                         AND IP_PAGOSDOBLES.TIPO     = ''S''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS 
                                                  ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:='UN_PREANO';
            MI_REEMPLAZOS(1).VALOR:=UN_PREANO;
            MI_REEMPLAZOS(2).CLAVE:='UN_PREANOI';
            MI_REEMPLAZOS(2).VALOR:=UN_PREANOI;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTPAGDOBLETIPO_S
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;      
    IF MI_ENTROFACAB = 1 THEN  
        --Actualiza los los predios, el valor de los PAG1 se pasa a los PAG
        BEGIN
            BEGIN 
                MI_TABLA:='IP_USUARIOS_PREDIAL';
                MI_CAMPOS := ' IP_USUARIOS_PREDIAL.PAGO_ANO  = IP_USUARIOS_PREDIAL.PAGO_ANO1 
                              ,IP_USUARIOS_PREDIAL.PAG_VAL   = IP_USUARIOS_PREDIAL.PAG_VAL1
                              ,IP_USUARIOS_PREDIAL.PAG_FEC   = IP_USUARIOS_PREDIAL.PAG_FEC1
                              ,IP_USUARIOS_PREDIAL.PAG_BAN   = IP_USUARIOS_PREDIAL.PAG_BAN1
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA 
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARIO_PAGOS
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;             
        BEGIN
            BEGIN
                --Actualiza los predios, el valor de los PAG2 se pasa a los PAG1  
                MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO1      = IP_USUARIOS_PREDIAL.PAGO_ANO2 
                              ,IP_USUARIOS_PREDIAL.PAG_VAL1      = IP_USUARIOS_PREDIAL.PAG_VAL2
                              ,IP_USUARIOS_PREDIAL.PAG_FEC1      = IP_USUARIOS_PREDIAL.PAG_FEC2
                              ,IP_USUARIOS_PREDIAL.PAG_BAN1      = IP_USUARIOS_PREDIAL.PAG_BAN2
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                            UN_TABLA      => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARIO_PAGO2
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        BEGIN 
            BEGIN 
                MI_CAMPOS := ' IP_USUARIOS_PREDIAL.NUM_COM       = IP_USUARIOS_PREDIAL.NUM_COM1 
                              ,IP_USUARIOS_PREDIAL.NUM_COM1      = IP_USUARIOS_PREDIAL.NUM_COM2
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARIO_PAGO3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END; 
        BEGIN 
            BEGIN 
                MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO2      = NULL 
                              ,IP_USUARIOS_PREDIAL.PAG_VAL2      = NULL
                              ,IP_USUARIOS_PREDIAL.PAG_FEC2      = NULL
                              ,IP_USUARIOS_PREDIAL.PAG_BAN2      = NULL
                              ,IP_USUARIOS_PREDIAL.NUM_COM2      = NULL
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( 
                                              UN_TABLA     => MI_TABLA 
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:=UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARIO_PAGO4
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END; 
    END IF;

    MI_ENTROFAC := 1;
    --Actualiza los predios facturados en abonos con filtro de DOCNUMABONO
    BEGIN 
        BEGIN
          MI_TABLA:='IP_FACTURADOS';
          MI_CAMPOS := ' IP_FACTURADOS.TOTALABONADO      = IP_FACTURADOS.TOTALABONADO1
                        ,IP_FACTURADOS.VALORULTIMOABONO  = IP_FACTURADOS.VALORULTIMOABONO1
                        ,IP_FACTURADOS.FECHAULTIMOABONO  = IP_FACTURADOS.FECHAULTIMOABONO1
                        ,IP_FACTURADOS.DOCNUMABONO       = IP_FACTURADOS.DOCNUMABONO1
                        ,IP_FACTURADOS.ALDIAINTERES      = IP_FACTURADOS.ALDIAINTERES1
                        ,IP_FACTURADOS.ALDIAINTERESCAR   = IP_FACTURADOS.ALDIAINTERES1CAR
                        ,IP_FACTURADOS.MODIFIED_BY       = '''||UN_USUARIO||'''
                        ,IP_FACTURADOS.DATE_MODIFIED     = SYSDATE ';
          MI_CONDICION := 'IP_FACTURADOS.COMPANIA    = '''||UN_COMPANIA||''' 
                       AND IP_FACTURADOS.CODIGO      = '''||UN_COD_PREDIO||'''
                       AND IP_FACTURADOS.DOCNUMABONO = '''||UN_NUM_FACTURA||''' ';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS 
                                       ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_FACTABONOPAGO
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS 
                  );
    END; 
    BEGIN
        BEGIN
            MI_CAMPOS := '   IP_FACTURADOS.TOTALABONADO1       = IP_FACTURADOS.TOTALABONADO2
                            ,IP_FACTURADOS.VALORULTIMOABONO1  = IP_FACTURADOS.VALORULTIMOABONO2
                            ,IP_FACTURADOS.FECHAULTIMOABONO1  = IP_FACTURADOS.FECHAULTIMOABONO2
                            ,IP_FACTURADOS.DOCNUMABONO1       = IP_FACTURADOS.DOCNUMABONO2
                            ,IP_FACTURADOS.ALDIAINTERES1      = IP_FACTURADOS.ALDIAINTERES2
                            ,IP_FACTURADOS.ALDIAINTERES1CAR   = IP_FACTURADOS.ALDIAINTERES2CAR
                            ,IP_FACTURADOS.MODIFIED_BY        = '''||UN_USUARIO||'''
                            ,IP_FACTURADOS.DATE_MODIFIED      = SYSDATE ';
            MI_CONDICION := '	   IP_FACTURADOS.COMPANIA    = '''||UN_COMPANIA||''' 
                             AND IP_FACTURADOS.CODIGO      = '''||UN_COD_PREDIO||'''
                             AND IP_FACTURADOS.DOCNUMABONO = '''||UN_NUM_FACTURA||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;     
    BEGIN 
        BEGIN 
            MI_CAMPOS := 'IP_FACTURADOS.TOTALABONADO2       = 0
                          ,IP_FACTURADOS.VALORULTIMOABONO2  = 0
                          ,IP_FACTURADOS.FECHAULTIMOABONO2  = NULL
                          ,IP_FACTURADOS.DOCNUMABONO2       = NULL
                          ,IP_FACTURADOS.ALDIAINTERES2      = NULL 
                          ,IP_FACTURADOS.MODIFIED_BY        = '''||UN_USUARIO||'''
                          ,IP_FACTURADOS.DATE_MODIFIED      = SYSDATE ';
            MI_CONDICION := 'IP_FACTURADOS.COMPANIA    = '''||UN_COMPANIA||''' 
                           AND IP_FACTURADOS.CODIGO      = '''||UN_COD_PREDIO||'''
                           AND IP_FACTURADOS.DOCNUMABONO = '''||UN_NUM_FACTURA||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                          UN_TABLA      => MI_TABLA 
                                         ,UN_ACCION    =>'M'
                                         ,UN_CAMPOS    => MI_CAMPOS 
                                         ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
    --Actualiza los predios facturados en abonos con filtro de FACTURAULTIMOABONO
    IF MI_ENTROFAC = 1 THEN 
        BEGIN 
            BEGIN 
                MI_TABLA:='IP_USUARIOS_PREDIAL';
                MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO_ABONO       = IP_USUARIOS_PREDIAL.PAGO_ANO_ABONO1
                              ,IP_USUARIOS_PREDIAL.VALORULTIMOABONO    = IP_USUARIOS_PREDIAL.VALORULTIMOABONO1
                              ,IP_USUARIOS_PREDIAL.FECHAULTIMOABONO    = IP_USUARIOS_PREDIAL.FECHAULTIMOABONO1
                              ,IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO  = IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO1
                              ,IP_USUARIOS_PREDIAL.BANCOULTIMOABONO    = IP_USUARIOS_PREDIAL.BANCOULTIMOABONO1
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY         = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED       = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS 
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARBONOPAGO
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      ); 
        END; 
        BEGIN 
            BEGIN 
                MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO_ABONO1       = IP_USUARIOS_PREDIAL.PAGO_ANO_ABONO2
                              ,IP_USUARIOS_PREDIAL.VALORULTIMOABONO1    = IP_USUARIOS_PREDIAL.VALORULTIMOABONO2
                              ,IP_USUARIOS_PREDIAL.FECHAULTIMOABONO1    = IP_USUARIOS_PREDIAL.FECHAULTIMOABONO2
                              ,IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO1  = IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO2
                              ,IP_USUARIOS_PREDIAL.BANCOULTIMOABONO1    = IP_USUARIOS_PREDIAL.BANCOULTIMOABONO2
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY          = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED        = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA     =>  MI_TABLA
                                             ,UN_ACCION    =>  'M'
                                             ,UN_CAMPOS    =>  MI_CAMPOS 
                                             ,UN_CONDICION =>  MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARBONOPAG2
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;   
        BEGIN 
            BEGIN 
                MI_CAMPOS := ' IP_USUARIOS_PREDIAL.PAGO_ANO_ABONO2      = 0
                              ,IP_USUARIOS_PREDIAL.VALORULTIMOABONO2    = 0
                              ,IP_USUARIOS_PREDIAL.FECHAULTIMOABONO2    = NULL
                              ,IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO2  = NULL
                              ,IP_USUARIOS_PREDIAL.BANCOULTIMOABONO2    = NULL
                              ,IP_USUARIOS_PREDIAL.MODIFIED_BY          = '''||UN_USUARIO||'''
                              ,IP_USUARIOS_PREDIAL.DATE_MODIFIED        = SYSDATE ';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA           = '''||UN_COMPANIA||'''
                             AND IP_USUARIOS_PREDIAL.CODIGO             = '''||UN_COD_PREDIO||'''
                             AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN       = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''
                             AND IP_USUARIOS_PREDIAL.FACTURAULTIMOABONO = '''||UN_NUM_FACTURA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS 
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
                MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
                MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
                MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_USUARBONOPAG3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END; 
    END IF;
    BEGIN 
        BEGIN 
            --Actualiza las facturas en abonos 
            MI_TABLA:='IP_FACTURADOSABONOS';
            MI_CAMPOS :=  'IP_FACTURADOSABONOS.PAGADO        = 0 
                          ,IP_FACTURADOSABONOS.PAG_BAN       = NULL 
                          ,IP_FACTURADOSABONOS.FECHAPAGO     = NULL
                          ,IP_FACTURADOSABONOS.MODIFIED_BY   = '''||UN_USUARIO||'''
                          ,IP_FACTURADOSABONOS.DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'IP_FACTURADOSABONOS.COMPANIA = '''||UN_COMPANIA||'''
                         AND IP_FACTURADOSABONOS.DOCNUM   = '''||UN_NUM_FACTURA||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS 
                                                  ,UN_CONDICION => MI_CONDICION);             
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(0).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REV_FACTURAABONOS
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
        --Actualiza los recibos de pago          
        --(TAR:1000062233; FECHA:21/04/2016; AUTOR:AA)
          --'Se corrige la sentencia del UPDATE ya que tenía errores y no permitía ejecutarse, por lo tanto no anulaba los pagos
    BEGIN 
        BEGIN 
            MI_TABLA:='IP_RECIBOS_DE_PAGO';
            MI_CAMPOS := '  PAGO               =  0
                          , ANULADO            = -1
                          , PAG_BANPAG         = NULL
                          , FECHA_REGRECAUDO   = NULL
                          , USUARIO_REGRECAUDO = NULL
                          , PREFECPAG          = NULL
                          , PAQUETEPAG         = NULL
                          , ANULADO_POR        = '''||UN_USUARIO||''' 
                          , MODIFIED_BY        = '''||UN_USUARIO||'''
                          , DATE_MODIFIED      = SYSDATE ';
            MI_CONDICION := '     IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                              AND IP_RECIBOS_DE_PAGO.DOCNUM   = '''||UN_NUM_FACTURA||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                  ,UN_ACCION    =>'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS 
                                                  ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(0).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVE_RECIBOS_PAGO
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
    BEGIN 
        BEGIN 
            --Actualiza los pagos dobles 
            MI_TABLA:='IP_PAGOSDOBLES';
            MI_CAMPOS := ' IP_PAGOSDOBLES.ANULADO       = 0
                          ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||'''
                          ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'IP_PAGOSDOBLES.COMPANIA = '''||UN_COMPANIA||'''
                         AND IP_PAGOSDOBLES.PRECOD   = '''||UN_COD_PREDIO||'''
                         AND IP_PAGOSDOBLES.PREANO   BETWEEN '||UN_PREANOI||' AND '||UN_PREANO||'
                         AND IP_PAGOSDOBLES.TIPO     = ''S''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS 
                                                  ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:= 'UN_PREANOI';
            MI_REEMPLAZOS(1).VALOR:= UN_PREANOI;
            MI_REEMPLAZOS(2).CLAVE:= 'UN_PREANO';
            MI_REEMPLAZOS(2).VALOR:= UN_PREANO;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
    --Elimina los registros en pago_bancosdet de el código del predio y el número de factura que se reciben por parámetro 
    MI_TABLA:='IP_PAGO_BANCOSDET';
    MI_CONDICION := 'IP_PAGO_BANCOSDET.COMPANIA   = '''||UN_COMPANIA||'''
                 AND IP_PAGO_BANCOSDET.PRECOD     = '''||UN_COD_PREDIO||'''
                 AND IP_PAGO_BANCOSDET.NUMFACTURA = '''||UN_NUM_FACTURA||'''';
    BEGIN 
        BEGIN 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'E' 
                                       ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            MI_REEMPLAZOS(0).CLAVE:= 'UN_COD_PREDIO';
            MI_REEMPLAZOS(0).VALOR:= UN_COD_PREDIO;
            MI_REEMPLAZOS(1).CLAVE:= 'UN_NUM_FACTURA';
            MI_REEMPLAZOS(1).VALOR:= UN_NUM_FACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END; 
    --Se llama el procedimiento Actualizar_UltimaVigCancelada_Ant en access
    PCK_PREDIAL.PR_ACT_ULTVIGCANCELADA_ANT(UN_COMPANIA  => UN_COMPANIA
                                           ,UN_CODIGO   => UN_COD_PREDIO);
END PR_REVERSARABONOS;

--21
PROCEDURE PR_CANCELAR_RESERVAS_USUARIO
  /*
    OBJETIVO              : Cancelar el porcentaje de reserva del facturado para todos los anios del 
                            usuario de predial.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_CODIGO: Código del usuario de predial.
                            UN_VIGENCIA: Año en el que se actualizara el porcentaje de reserva.
                            UN_ORDENPREDIAL: Valor del numero de orden predial.
                            UN_USUARIO: Codigo del usuario que llama la funcion.
                            UN_RESOLUCION: Valor en la resolucion de la reserva.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : PABLO ANDRES ESPITIA CUCA
    FECHA                 : 27/02/2017 10:08 AM
    REALIZADO POR:        : STEFANINI SYSMAN
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    @NAME: cancelarReservasUsuario
    @METHOD: PUT
  */
(
  UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO		    IN IP_USUARIOS_PREDIAL.CODIGO%TYPE,
  UN_VIGENCIA	    IN PCK_SUBTIPOS.TI_ANIO,
  UN_ORDENPREDIAL IN PCK_SUBTIPOS.TI_NUMORDEN,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_RESOLUCION   IN IP_FACTURADOS.RESOLUCION_RESERVA%TYPE
)
AS
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA	     PCK_SUBTIPOS.TI_LOGICO;
  MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TABLA	   PCK_SUBTIPOS.TI_TABLA;
  MI_CANT	     PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  <<UFACTURADOS>>  
  FOR MI_I IN (SELECT 
                 F.CODIGO,
                 F.PREANO
               FROM IP_FACTURADOS F
                 INNER JOIN IP_USUARIOS_PREDIAL UP
                    ON F.COMPANIA     = UP.COMPANIA
                   AND F.CODIGO       = UP.CODIGO
                   AND F.NUMERO_ORDEN = UP.NUMERO_ORDEN
               WHERE F.COMPANIA     = UN_COMPANIA
                 AND F.CODIGO       = UN_CODIGO
                 AND F.NUMERO_ORDEN = UN_ORDENPREDIAL
                 AND F.PREANO       > 2013
                 AND F.PREANO       > UP.PAGO_ANO
              ) 
  LOOP
    MI_TABLA := 'IP_FACTURADOS';

    MI_CAMPOS := ' RESOLUCION_DESRESERVA = '''||UN_RESOLUCION||'''
                  ,CONSECUTIVO           = 0
                  ,PORCENTAJE_RESERVA    = 0
                  ,RESOLUCION_RESERVA    = 0
                  ,MODIFIED_BY           = '''||UN_USUARIO||'''
                  ,DATE_MODIFIED         = SYSDATE';

    MI_CONDICION := '   COMPANIA 	   = '''||UN_COMPANIA||'''
                    AND CODIGO   	   = '''||UN_CODIGO||'''
                    AND NUMERO_ORDEN = '''||UN_ORDENPREDIAL||'''
                    AND PREANO       = '||MI_I.PREANO;


    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                 THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGO;
        MI_MSGERROR(2).CLAVE := 'ENTE';
        MI_MSGERROR(2).VALOR := 'facturado';      
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_CANCELARRESERVAS
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_MSGERROR
                                  );    
    END;

  END LOOP UFACTURADOS; 

  MI_TABLA := 'IP_USUARIOS_PREDIAL';

  MI_CAMPOS := ' INDICADOR_RESERVA  = 0
                ,PORCENTAJE_RESERVA = 0
                ,MODIFIED_BY        = '''||UN_USUARIO||'''
                ,DATE_MODIFIED      = SYSDATE ';

  MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                  AND CODIGO       = '''||UN_CODIGO||'''
                  AND NUMERO_ORDEN = '''||UN_ORDENPREDIAL||'''';

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      MI_MSGERROR(2).CLAVE := 'ENTE';
      MI_MSGERROR(2).VALOR := 'usuario';       
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_CANCELARRESERVAS
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END;

END PR_CANCELAR_RESERVAS_USUARIO;

--22
FUNCTION FC_REVERSARPAGO_PAZYSALVO
  /*
    OBJETIVO              : Remover el registro de pago en el banco de paz y salvo.
    PARÁMETROS DE ENTRADA : UN_COMPANIA: Código de la compañia.
                            UN_REFERENCIA: Codigo de la factura.
                            UN_FECHA: Fecha con formato TO_DATE y mascara DD/MM/YYYY HH24:mi:ss
                            UN_PAQUETE: Codigo del paquete.
                            UN_BANCO: Codigo del banco.
                            UN_NUMCUPONES: Cantidad de cupones.
                            UN_ACUMULADO: Valor acumulado.
    PARÁMETROS DE SALIDA  : 
    LIDER TÉCNICO         : PABLO ANDRES ESPITIA CUCA
    FECHA                 : 02/03/2017 17:31 AM
    REALIZADO POR:        : STEFANINI SYSMAN
    FECHA MODIFICACIÓN    :
    LIDER MODIFICACIÓN    :
    REALIZADO POR         :
    OBJETIVO MODIFICACIÓN :

    @NAME: reversarPagoPazYSalvo
    @METHOD: PUT
  */
(
  UN_COMPANIA 	  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_REFERENCIA   IN IP_PAGOS_WEB.REFERENCIA%TYPE,
  UN_FECHA        IN VARCHAR2,
  UN_PAQUETE      IN IP_PAGOS_BANCOSCAB_PAZ.PAQUETE%TYPE,
  UN_BANCO        IN IP_BANCOS.CODIGOBANCO%TYPE,
  UN_NUMCUPONES   IN IP_PAGOS_BANCOSCAB_PAZ.NROCUPONESACU%TYPE,
  UN_ACUMULADO    IN IP_PAGOS_BANCOSCAB_PAZ.VLACUMULADO%TYPE,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER
AS
  MI_CERTIFICADO IP_PAGOS_WEB.CERTIFICADO%TYPE;
  MI_VALOR       PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA	       PCK_SUBTIPOS.TI_LOGICO;
  MI_TABLA	     PCK_SUBTIPOS.TI_TABLA;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  BEGIN
    BEGIN
      SELECT 
         CERTIFICADO
        ,NVL(VALOR,0)
      INTO
         MI_CERTIFICADO
        ,MI_VALOR
      FROM IP_PAGOS_WEB
      WHERE COMPANIA   = UN_COMPANIA
        AND TIPO IN('PS')
        AND REFERENCIA = UN_REFERENCIA;

      EXCEPTION WHEN NO_DATA_FOUND
                THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'REFERENCIA';
      MI_MSGERROR(1).VALOR := UN_REFERENCIA;      

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_NDF_REVPAGPAZS_FAC
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );
  END;

  IF MI_CERTIFICADO IS NOT NULL THEN 
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_MSGERROR(1).CLAVE := 'REFERENCIA';
        MI_MSGERROR(1).VALOR := UN_REFERENCIA;      

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_NDF_REVPAGPAZS_CER
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_MSGERROR
                                  );
    END;
  END IF;

  MI_TABLA := 'IP_PAGOS_WEB';

  MI_CAMPOS := ' PAGO          = 0
                ,FECHA_PAGO    = NULL
  			        ,BANCO_PAGO    = NULL
  			        ,PAQUETE       = NULL
                ,DATE_MODIFIED = SYSDATE
                ,MODIFIED_BY   = '''||UN_USUARIO||'''';

  MI_CONDICION := '     COMPANIA 	 = '''||UN_COMPANIA||'''
                    AND TIPO IN(''PS'')
                    AND REFERENCIA = '''||UN_REFERENCIA||''' ';

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
               THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'REFERENCIA';
      MI_MSGERROR(1).VALOR := UN_REFERENCIA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_REVPAGPAZSA_PAGO
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END;

  IF MI_RTA <= 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'REFERENCIA';
      MI_MSGERROR(1).VALOR := UN_REFERENCIA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_REVPAGPAZSA_PAGO
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );   
    END;
  END IF;

  MI_TABLA := 'IP_PAGOS_BANCOSCAB_PAZ';

  MI_CAMPOS := ' NROCUPONESACU = '||(UN_NUMCUPONES-1)||'
                ,VLACUMULADO   = '||(UN_ACUMULADO-MI_VALOR)||'
                ,DATE_MODIFIED = SYSDATE
                ,MODIFIED_BY   = '''||UN_USUARIO||'''';

  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                AND TRUNC(PREFEC)   = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                AND PAQUETE  = '''||UN_PAQUETE||'''
                AND PAG_BAN  = '''||UN_BANCO||'''';

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PAQUETE';
      MI_MSGERROR(1).VALOR := UN_PAQUETE;
      MI_MSGERROR(2).CLAVE := 'BANCO';
      MI_MSGERROR(2).VALOR := UN_BANCO;      

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_M_REVPAGPAZS_VACUM
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END; 

  MI_TABLA := 'IP_PAGOS_BANCOSDET_PAZ';

  MI_CONDICION := ' COMPANIA      = '''||UN_COMPANIA||'''
                AND REFERENCIA    = '''||UN_REFERENCIA||'''
                AND TIPO          = ''PS'' 
                AND TRUNC(PREFEC) = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                AND PAQUETE       = '''||UN_PAQUETE||'''
                AND PAG_BAN       = '''||UN_BANCO||''' ';  

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'E'
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                THEN RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PAQUETE';
      MI_MSGERROR(1).VALOR := UN_PAQUETE;
      MI_MSGERROR(2).CLAVE := 'BANCO';
      MI_MSGERROR(2).VALOR := UN_BANCO;      

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_E_REVPAGPAZS_REFER
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                );    
  END; 

  RETURN MI_RTA;
END FC_REVERSARPAGO_PAZYSALVO;

--23
PROCEDURE PR_HABILITAR_EXENTO 
  /*
    NAME                  : PR_HABILITAR_EXENTO -> FRMINDICAEXENTO.CmdRegistrar_Click
    AUTHOR                : STEFANINI SYSMAN
    AUTHOR MIGRATION      : PABLO ANDRÉS ESPITIA CUCA
    DATE MIGRATION        : 04/07/2017 
    TIME                  : 12:08 PM
    SOURCE MODULE         : IMPUESTO PREDIAL (60)
    DESCRIPTION           : Habilitar exento para predio.
    PARAMETERS            : UN_COMPANIA: Codigo de la compania.
                            UN_NIVEL_USUARIO: Nivel del usuario que inicio sesion.
                            UN_USUARIO: Codigo del usuario que inicio sesion.
                            UN_IND_EXE_IMPUESTO: Indicador exento de impuesto.
                            UN_IND_EXE_CAR: Indicador exento de car.
                            UN_IND_EXE_OTROS: Indicador exentos de otros.
                            UN_ANIO_DESDE: Anio inicial.
                            UN_ANIO_HASTA: Anio final.
                            UN_CODPREDIO: Codigo del usuario predial.
                            UN_NUMERO_ORDEN: Numero de orden predial.
                            UN_CODRESOLUCION: Numero de resolucion.
                            UN_FECRESOLUCION: Fecha de la resolucion.
                            UN_ELABORADAPOR: Nombre de quien elaboro la resolucion.
                            UN_FIRMADAPOR: Nombre de quien firmo la resolucion.
                            UN_OBSERVACION: Descripcion de la resolucion.

    @NAME: habilitarExento
    @METHOD: POST
  */
(
   UN_COMPANIA 	       IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_NIVEL_USUARIO    IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0
  ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO 
  ,UN_IND_EXE_IMPUESTO IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_IND_EXE_CAR      IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_IND_EXE_OTROS    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_ANIO_DESDE       IN PCK_SUBTIPOS.TI_ANIO
  ,UN_ANIO_HASTA       IN PCK_SUBTIPOS.TI_ANIO
  ,UN_CODPREDIO        IN IP_USUARIOS_PREDIAL.CODIGO%TYPE
  ,UN_NUMERO_ORDEN     IN IP_USUARIOS_PREDIAL.NUMERO_ORDEN%TYPE
  ,UN_CODRESOLUCION    IN IP_CONTROL_EXCENTOS.NRO_RES%TYPE
  ,UN_FECRESOLUCION    IN DATE
  ,UN_ELABORADAPOR     IN IP_CONTROL_EXCENTOS.ELABORADA_POR%TYPE
  ,UN_FIRMADAPOR       IN IP_CONTROL_EXCENTOS.FIRMADA_POR%TYPE
  ,UN_OBSERVACION      IN IP_AUDITORIA.DESCRIPCION%TYPE
)
AS 
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_VIGENCIA_INI PCK_SUBTIPOS.TI_ANIO;
  MI_VIGENCIA_FIN PCK_SUBTIPOS.TI_ANIO;
  MI_KEY          PCK_SUBTIPOS.TI_LOGICO;
  MI_CANT         PCK_SUBTIPOS.TI_ENTERO;
  MI_CONSECUTIVO  IP_CONTROL_EXCENTOS.CODIGO%TYPE;
  MI_ESTADO       IP_CONTROL_EXCENTOS.ESTADO%TYPE;
  MI_RMOCOD       IP_MODICOD.RMOCOD%TYPE;
  MI_ERROR_COD    PLS_INTEGER;         
BEGIN
  MI_ESTADO := 'A';

  /*Validar nivel de usuario*/
  IF UN_NIVEL_USUARIO <= 6 THEN
    BEGIN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_USUARIO;

      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_FHE_VALIDA_NIVELUSU
                                ,UN_REEMPLAZOS => MI_MSGERROR); 
    END;
  END IF;

  /*Validar indicadores*/
  IF (UN_IND_EXE_IMPUESTO + UN_IND_EXE_CAR + UN_IND_EXE_OTROS) IN(0) THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_FHE_VALIDAR_IND
                                ,UN_REEMPLAZOS => MI_MSGERROR); 
    END;    
  END IF;

  /*Verificar existencia de vigencias*/
  BEGIN
    MI_KEY := 0;

    SELECT 
       VIGENCIA_INICIAL
      ,VIGENCIA_FINAL
    INTO
       MI_VIGENCIA_INI
      ,MI_VIGENCIA_FIN
    FROM IP_CONTROL_EXCENTOS
    WHERE COMPANIA     = UN_COMPANIA
      AND PREDIO       = UN_CODPREDIO
      AND NUMERO_ORDEN = UN_NUMERO_ORDEN
      AND ESTADO       = MI_ESTADO
      AND ROWNUM      <= 1;

  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_KEY := -1; /*Permitir crear el exento*/
  END;

  IF MI_KEY IN(0) THEN
    BEGIN
      SELECT COUNT(PREANO)
      INTO MI_CANT
      FROM IP_FACTURADOS
      WHERE COMPANIA     = UN_COMPANIA
        AND CODIGO       = UN_CODPREDIO
        AND NUMERO_ORDEN = UN_NUMERO_ORDEN
        AND PREANO BETWEEN MI_VIGENCIA_INI AND MI_VIGENCIA_FIN
        AND PAGADO IN(0) ;

      MI_ERROR_COD := CASE WHEN MI_CANT IN(0) 
                           THEN PCK_ERRORES.ERR_IP_MSG_PRHE_VERIFICAFACPRE
                           ELSE PCK_ERRORES.ERR_IP_MSG_FHE_VALIDAR_FACTURA
                      END;

      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      --MI_KEY := -1; /*Permitir crear el exento*/
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_CODPREDIO;
      MI_MSGERROR(2).CLAVE := 'VIGENCIAINI';
      MI_MSGERROR(2).VALOR := MI_VIGENCIA_INI;
      MI_MSGERROR(3).CLAVE := 'VIGENCIAFIN';
      MI_MSGERROR(3).VALOR := MI_VIGENCIA_FIN;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => MI_ERROR_COD
                                ,UN_REEMPLAZOS => MI_MSGERROR);       
    END;
  END IF;

  IF MI_KEY NOT IN(0) THEN
    BEGIN
      MI_TABLA := 'IP_CONTROL_EXCENTOS';
      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||'''';

      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA
                                                        ,UN_CRITERIO => MI_CONDICION
                                                        ,UN_CAMPO    => 'CODIGO');

      MI_CAMPOS := 'COMPANIA       
                   ,NUMERO_ORDEN   
                   ,CODIGO
                   ,PREDIO 
                   ,VIGENCIA_INICIAL 
                   ,VIGENCIA_FINAL 
                   ,INDIMP      
                   ,INDCAR     
                   ,INDOTROS 
                   ,ESTADO        
                   ,NRO_RES       
                   ,FECHA_RES        
                   ,ELABORADA_POR  
                   ,FIRMADA_POR 
                   ,CREATED_BY 
                   ,DATE_CREATED';

      MI_VALORES := ''''||UN_COMPANIA        ||'''
                    ,'''||UN_NUMERO_ORDEN    ||'''
                    ,  '||MI_CONSECUTIVO     ||'
                    ,'''||UN_CODPREDIO       ||'''
                    ,  '||UN_ANIO_DESDE      ||'
                    ,  '||UN_ANIO_HASTA      ||'
                    ,  '||UN_IND_EXE_IMPUESTO||'
                    ,  '||UN_IND_EXE_CAR     ||'
                    ,  '||UN_IND_EXE_OTROS   ||'
                    ,'''||MI_ESTADO          ||'''
                    ,'''||UN_CODRESOLUCION   ||'''
                    ,TO_DATE('''||UN_FECRESOLUCION||''',''DD/MM/YYYY'')
                    ,'''||UN_ELABORADAPOR    ||'''
                    ,'''||UN_FIRMADAPOR      ||'''
                    ,'''||UN_USUARIO         ||'''
                    ,SYSDATE';        

      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                       
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_CODPREDIO;
      MI_MSGERROR(2).CLAVE := 'VIGENCIAINI';
      MI_MSGERROR(2).VALOR := UN_ANIO_DESDE;
      MI_MSGERROR(3).CLAVE := 'VIGENCIAFIN';
      MI_MSGERROR(3).VALOR := UN_ANIO_HASTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_I_PRDE_INSERTCONTROLEXC
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;
  END IF;

  MI_CAMPOS := 'INDCAR        =   '||UN_IND_EXE_CAR     ||'
               ,INDEXE        =   '||UN_IND_EXE_IMPUESTO||'
               ,INDEXEOTROS   =   '||UN_IND_EXE_OTROS   ||'
               ,OBSERVACIONES = '''||UN_OBSERVACION     ||'''
               ,MODIFIED_BY   = '''||UN_USUARIO         ||'''
               ,DATE_MODIFIED = SYSDATE';

  MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
               AND CODIGO       = '''||UN_CODPREDIO   ||'''
               AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||'''
               AND PREANO BETWEEN '''||UN_ANIO_DESDE||''' AND '''||UN_ANIO_HASTA||'''
               AND PAGADO IN(0)';               

  MI_TABLA := 'IP_FACTURADOS';

  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'PREDIO';
    MI_MSGERROR(1).VALOR := UN_CODPREDIO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_M_FHE_INDICADORES_EXENT
                              ,UN_TABLAERROR => MI_TABLA
                              ,UN_REEMPLAZOS => MI_MSGERROR);
  END;

  MI_TABLA := 'IP_MODICOD';

  BEGIN
    BEGIN
      SELECT RMOCOD 
      INTO MI_RMOCOD
      FROM IP_MODICOD 
      WHERE RMOVAR = 'INGRESEXCENTO';

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'CAMPO';
    MI_MSGERROR(1).VALOR := 'INGRESEXCENTO';

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_FHE_REGINGRETIRAEXE
                              ,UN_TABLAERROR => MI_TABLA
                              ,UN_REEMPLAZOS => MI_MSGERROR);
  END; 

  PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA
                          ,UN_CODMOD      => UN_CODPREDIO
                          ,UN_OPEMOD      => UN_USUARIO
                          ,UN_CCOMOD      => MI_RMOCOD
                          ,UN_VANMOD      => '-'
                          ,UN_VNUMOD      => '-'
                          ,UN_DESCRIPCION => UN_OBSERVACION);

END PR_HABILITAR_EXENTO;

--24
PROCEDURE PR_DESHABILITAR_EXENTO 
  /*
    NAME                  : PR_DESHABILITAR_EXENTO -> FRMINDICAEXENTO.CmdRegistrar_Click
    AUTHOR                : STEFANINI SYSMAN
    AUTHOR MIGRATION      : PABLO ANDRÉS ESPITIA CUCA
    DATE MIGRATION        : 06/07/2017 
    TIME                  : 08:27 AM
    SOURCE MODULE         : IMPUESTO PREDIAL (60)
    DESCRIPTION           : Deshabilitar exentos asociados al codigo predial.

    PARAMETERS            : UN_COMPANIA: Codigo de la compania.
                            UN_NIVEL_USUARIO: Nivel del usuario que inicio sesion.
                            UN_USUARIO: Codigo del usuario que inicio sesion.
                            UN_IND_EXE_IMPUESTO: Indicador exento de impuesto.
                            UN_IND_EXE_CAR: Indicador exento de car.
                            UN_IND_EXE_OTROS: Indicador exentos de otros.
                            UN_ANIO_DESDE: Anio inicial.
                            UN_ANIO_HASTA: ANio final.
                            UN_CODPREDIO: Codigo del usuario predial.
                            UN_NUMERO_ORDEN: Numero de orden predial.
                            UN_CODRESOLUCION: Numero de resolucion.
                            UN_FECRESOLUCION: Fecha de la resolucion.
                            UN_ELABORADAPOR: Nombre de quien elaboro la resolucion.
                            UN_FIRMADAPOR: Nombre de quien firmo la resolucion.
                            UN_OBSERVACION: Descripcion de la resolucion.

    @NAME: deshabilitarExento
    @METHOD: POST
  */
(
   UN_COMPANIA 	       IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_NIVEL_USUARIO    IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0
  ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO 
  ,UN_IND_EXE_IMPUESTO IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_IND_EXE_CAR      IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_IND_EXE_OTROS    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  ,UN_ANIO_DESDE       IN PCK_SUBTIPOS.TI_ANIO
  ,UN_ANIO_HASTA       IN PCK_SUBTIPOS.TI_ANIO
  ,UN_CODPREDIO        IN IP_USUARIOS_PREDIAL.CODIGO%TYPE
  ,UN_NUMERO_ORDEN     IN IP_USUARIOS_PREDIAL.NUMERO_ORDEN%TYPE
  ,UN_CODRESOLUCION    IN IP_CONTROL_EXCENTOS.NRO_RES%TYPE
  ,UN_FECRESOLUCION    IN DATE
  ,UN_ELABORADAPOR     IN IP_CONTROL_EXCENTOS.ELABORADA_POR%TYPE
  ,UN_FIRMADAPOR       IN IP_CONTROL_EXCENTOS.FIRMADA_POR%TYPE
  ,UN_OBSERVACION      IN IP_AUDITORIA.DESCRIPCION%TYPE
)
AS 
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; /*Reemplazos de errores*/
  MI_CANT         PCK_SUBTIPOS.TI_ENTERO;
  MI_VIGENCIA_INI PCK_SUBTIPOS.TI_ANIO;
  MI_VIGENCIA_FIN PCK_SUBTIPOS.TI_ANIO;  
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;  
  MI_TABLA_CE     PCK_SUBTIPOS.TI_TABLA;  
  MI_TABLA_F      PCK_SUBTIPOS.TI_TABLA; 
  MI_ESTADO       IP_CONTROL_EXCENTOS.ESTADO%TYPE;  
  MI_CODIGOCE     IP_CONTROL_EXCENTOS.CODIGO%TYPE;
  MI_RMOCOD       IP_MODICOD.RMOCOD%TYPE;
  MI_MSG          VARCHAR(20 CHAR);
BEGIN
  MI_ESTADO   := 'A';
  MI_TABLA_CE := 'IP_CONTROL_EXCENTOS';
  MI_TABLA_F  := 'IP_FACTURADOS';

  /*Validar nivel de usuario*/
  IF UN_NIVEL_USUARIO <= 6 THEN
    BEGIN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_USUARIO;

      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_FHE_VALIDA_NIVELUSU
                                ,UN_REEMPLAZOS => MI_MSGERROR); 
    END;
  END IF;

  /*Validar indicadores*/
  IF (UN_IND_EXE_IMPUESTO + UN_IND_EXE_CAR + UN_IND_EXE_OTROS) NOT IN(0) THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_PRDE_VALIDAR_IND
                                ,UN_REEMPLAZOS => MI_MSGERROR); 
    END;    
  END IF;  

  BEGIN
    BEGIN
      SELECT 
         CODIGO
        ,VIGENCIA_INICIAL
        ,VIGENCIA_FINAL
      INTO
         MI_CODIGOCE
        ,MI_VIGENCIA_INI
        ,MI_VIGENCIA_FIN
      FROM IP_CONTROL_EXCENTOS
      WHERE COMPANIA     = UN_COMPANIA
        AND PREDIO       = UN_CODPREDIO
        AND NUMERO_ORDEN = UN_NUMERO_ORDEN
        AND ESTADO       = MI_ESTADO
        AND ROWNUM      <= 1;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    MI_MSGERROR(1).CLAVE := 'PREDIO';
    MI_MSGERROR(1).VALOR := UN_CODPREDIO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_PRDE_VALIDAR_INDACT
                              ,UN_REEMPLAZOS => MI_MSGERROR);     
  END;

  --Verificar vigencias
  BEGIN
    MI_MSG := CASE WHEN MI_VIGENCIA_INI NOT IN(UN_ANIO_DESDE) 
                   THEN 'VIGENCIA INICIAL'
                   WHEN MI_VIGENCIA_FIN NOT IN(UN_ANIO_HASTA)
                   THEN 'VIGENCIA FINAL'
              END;

    IF MI_MSG IS NOT NULL THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'CAMPO';
    MI_MSGERROR(1).VALOR := MI_MSG;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_PRDE_VALIDACAMPOEXC
                              ,UN_REEMPLAZOS => MI_MSGERROR);         
  END;

  --Anular control excento registrado
  BEGIN
    MI_CONDICION :=  'COMPANIA     = '''||UN_COMPANIA    ||'''
                  AND CODIGO       =   '||MI_CODIGOCE    ||'  
                  AND PREDIO       = '''||UN_CODPREDIO   ||'''
                  AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||'''';

    MI_CAMPOS := 'ANULADO_POR       = '''||UN_USUARIO      ||'''
                 ,FECHA_ANULACION   = SYSDATE
                 ,NRO_RESANUL       = '''||UN_CODRESOLUCION||'''
                 ,FECHA_RESANUL     = TO_DATE('''||UN_FECRESOLUCION||''',''DD/MM/YYYY'')
                 ,ELABORADA_PORANUL = '''||UN_ELABORADAPOR ||'''
                 ,FIRMADA_PORANUL   = '''||UN_FIRMADAPOR   ||'''
                 ,ESTADO            = ''C''
                 ,DATE_MODIFIED     = SYSDATE
                 ,MODIFIED_BY       = '''||UN_USUARIO      ||'''';

    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_CE
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;      

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'PREDIO';
    MI_MSGERROR(1).VALOR := UN_CODPREDIO;
    MI_MSGERROR(2).CLAVE := 'CODIGO';
    MI_MSGERROR(2).VALOR := MI_CODIGOCE;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_M_PRDE_ANULARCONTROLEXC
                              ,UN_TABLAERROR => MI_TABLA_CE
                              ,UN_REEMPLAZOS => MI_MSGERROR);         
  END;

  BEGIN
    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||''' 
                 AND CODIGO       = '''||UN_CODPREDIO   ||'''
                 AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||'''
                 AND PREANO BETWEEN '||UN_ANIO_DESDE||' AND '||UN_ANIO_HASTA||'
                 AND PAGADO IN(0) ';

    MI_CAMPOS := 'INDEXE        = 0
                 ,INDCAR        = 0
                 ,INDEXEOTROS   = 0
                 ,DATE_MODIFIED = SYSDATE
                 ,MODIFIED_BY   = '''||UN_USUARIO||'''';

    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_F
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'PREDIO';
    MI_MSGERROR(1).VALOR := UN_CODPREDIO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_M_PRDE_DEMARCARINDFACTU
                              ,UN_TABLAERROR => MI_TABLA_F
                              ,UN_REEMPLAZOS => MI_MSGERROR);     
  END;

  BEGIN
    BEGIN
      SELECT RMOCOD 
      INTO MI_RMOCOD
      FROM IP_MODICOD 
      WHERE RMOVAR = 'INGRESORETIRAREXCENTO';

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'CAMPO';
    MI_MSGERROR(1).VALOR := 'INGRESORETIRAREXCENTO';

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_IP_MSG_FHE_REGINGRETIRAEXE
                              ,UN_REEMPLAZOS => MI_MSGERROR);
  END;   

  PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA
                          ,UN_CODMOD      => UN_CODPREDIO
                          ,UN_OPEMOD      => UN_USUARIO
                          ,UN_CCOMOD      => MI_RMOCOD
                          ,UN_VANMOD      => '-'
                          ,UN_VNUMOD      => '-'
                          ,UN_DESCRIPCION => UN_OBSERVACION);  

END PR_DESHABILITAR_EXENTO;

END PCK_PREDIAL_COM6;