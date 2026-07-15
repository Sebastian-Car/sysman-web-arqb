create or replace PACKAGE BODY PCK_PREDIAL_COM5 AS

--1
PROCEDURE PR_CALCABONOENACUERDO
    /*
    NAME              : PR_CALCABONOENACUERDO En access --> CalcAbonoEnAcuerdo 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 13/02/2017
    TIME              : 02:53
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON 
    DATE MODIFIED     : 15/11/2017
    SOURCE MODULE     : IMPUESTO PREDIAL
    DESCRIPTION       : Procedimiento encargado calcuar el capital y los intereses del acuerdo. 
    PARAMETERS        : UN_COMPANIA                Codigo de la compania con la que se logea el usuario 
                        UN_NUMERO_ORDEN            Orden del propietario del predio, usualmente se trabaja con el orden 001 
                        UN_ACUERDO                 Codigo del acuerdo de pago al cua se desea abonar
                        UN_PREDIO                  Codigo del predio relacionado al acuerdo de pago
                        UN_CUOTA                   Numero de la cuota a la cual se desea aplicar el abono 
                        UN_VALORCUOTA              Valor de la cuota en el momento del firmar acuerdo de pago
                        UN_VALORABONO              Valor que se desea abonar al acuerdo de pago
                        UN_STRTIPO                 Tipo de abono, Disminucion de cuotas o disminucion de valor
                        UN_NUMCUOTAS               Numero total de cuotas del acuerdo de pago

    @NAME:  actualizarAbonoPrioridad
    @METHOD:  POST   
    */
    (
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN, 
    UN_ACUERDO                 IN VARCHAR2,
    UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CUOTA                   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_VALORCUOTA              IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_VALORABONO              IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_STRTIPO                 IN VARCHAR2,
    UN_NUMCUOTAS               IN PCK_SUBTIPOS.TI_ENTERO_LARGO
    )
    AS


      MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
      MI_VALORACUMULADO         PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORACUMULADOPER      PCK_SUBTIPOS.TI_DOBLE;
      MI_RSACUERDOS             SYS_REFCURSOR; 
      MI_RSFACACUERDOS          SYS_REFCURSOR; 
      MI_RSTMP                  SYS_REFCURSOR; 
      MI_RS_AUX                 SYS_REFCURSOR; 
      MI_SQL                    VARCHAR(32000 CHAR);
      MI_DBLMONTOCAPITAL        PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLTASAINTERES         PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLPORCCAPITAL         PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLMONTOINTERESES      PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLPORCINTERESES       PCK_SUBTIPOS.TI_DOBLE;
      MI_DBLMONTODIFERENCIA     PCK_SUBTIPOS.TI_DOBLE;
      MI_TMPMONTOCAPITAL        PCK_SUBTIPOS.TI_DOBLE;
      MI_TMPMONTOINTERESES      PCK_SUBTIPOS.TI_DOBLE;
      MI_NUMCUOTASRESTANTES     PCK_SUBTIPOS.TI_DOBLE;
      MI_TMPCAPITALRESTANTE     PCK_SUBTIPOS.TI_DOBLE;
      MI_TMPINTERESRESTANTE     PCK_SUBTIPOS.TI_DOBLE;
      MI_TCAPITAL               PCK_SUBTIPOS.TI_DOBLE;
      MI_TINTERESES             PCK_SUBTIPOS.TI_DOBLE;
      MI_INTPERIODICIDAD        PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTALINTERESESACU      PCK_SUBTIPOS.TI_DOBLE;
      MI_INTERES_ACUERDO        PCK_SUBTIPOS.TI_DOBLE;

      MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
      MI_TIPOINTERESACU         VARCHAR(32000 CHAR);
      MI_RTA                    PCK_SUBTIPOS.TI_RTA_ACME;


    BEGIN

      BEGIN
        BEGIN
           MI_CONDICION := '     COMPANIA           = '''||UN_COMPANIA||'''
                             AND CODIGOACUERDO      = '''||UN_ACUERDO||'''
                             AND PREDIO             = '''||UN_PREDIO||'''';

            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                        UN_ACCION    => 'E',
                                        UN_CONDICION => MI_CONDICION); 

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DELETE_TMPABOACU);
      END;                      


      BEGIN
       SELECT
              NVL(MONTOCAPITAL,0) MONTOCAPITAL,
              NVL(MONTOINTERESES,0) MONTOINTERESES,
              NVL(INTERES,0) INTERES
         INTO MI_DBLMONTOCAPITAL,
              MI_DBLMONTOINTERESES,
              MI_DBLTASAINTERES
         FROM 
              IP_ACUERDOS
        WHERE
              COMPANIA      = UN_COMPANIA
          AND CODIGOACUERDO = UN_ACUERDO
          AND PREDIO        = UN_PREDIO
          AND NUMERO_ORDEN  = UN_NUMERO_ORDEN;

        EXCEPTION WHEN NO_DATA_FOUND THEN 
           MI_DBLMONTOCAPITAL   := 0;
           MI_DBLMONTOINTERESES := 0;
           MI_DBLTASAINTERES    := 0;

      END; 

        MI_DBLPORCCAPITAL     := MI_DBLMONTOCAPITAL/(MI_DBLMONTOCAPITAL+MI_DBLMONTOINTERESES);
        MI_DBLPORCINTERESES   := MI_DBLMONTOINTERESES/(MI_DBLMONTOCAPITAL+MI_DBLMONTOINTERESES);

      MI_DBLMONTODIFERENCIA := UN_VALORABONO - UN_VALORCUOTA;

      MI_TMPMONTOCAPITAL    := MI_DBLMONTOCAPITAL;
      MI_TMPMONTOINTERESES  := MI_DBLMONTOINTERESES;

      MI_NUMCUOTASRESTANTES := UN_NUMCUOTAS-UN_CUOTA;

      BEGIN
        BEGIN

            MI_CAMPOS := 'COMPANIA,CODIGOACUERDO,PREDIO,
                          CUOTA,DOCNUM,PAGADO,
                          C1,C2,C3,C4,C5,C6,C7,
                          C8,C9,C10,C11,C12,C13,
                          C14,C15,C16,C17,C18,
                          C19,C20,TOTAL,CAPITAL,
                          INTERESES,INTERES_ACUERDO,INTERES_RECARGO,
                          FECHAFACTURADO,PAG_BAN,
                          FECHAPAGO,PREANOI,PREANO';

            MI_VALORES := 'SELECT 
                                  COMPANIA,CODIGOACUERDO,PREDIO,
                                  CUOTA,DOCNUM,PAGADO,
                                  C1,C2,C3,C4,C5,C6,C7,
                                  C8,C9,C10,C11,C12,C13,
                                  C14,C15,C16,C17,C18,
                                  C19,C20,TOTAL,CAPITAL,
                                  INTERESES,INTERES_ACUERDO,INTERES_RECARGO,
                                  FECHAFACTURADO,PAG_BAN,
                                  FECHAPAGO,PREANOI,PREANO
                             FROM 
                                  IP_FACTURADOSACUERDOS
                            WHERE
                                  COMPANIA        = '''||UN_COMPANIA||'''
                              AND CODIGOACUERDO   = '''||UN_ACUERDO||'''
                              AND PREDIO          = '''||UN_PREDIO||'''
                              AND NUMERO_ORDEN    = '''||UN_NUMERO_ORDEN||'''
                              AND CUOTA           < '||UN_CUOTA;

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'IP_TMP_FACT_ABONO_EN_ACUERDO', 
                                        UN_ACCION  => 'IS', 
                                        UN_CAMPOS  => MI_CAMPOS, 
                                        UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;              

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_TMPABOACU);
      END;                            

    BEGIN 
      BEGIN

          MI_CAMPOS := 'COMPANIA,CODIGOACUERDO,PREDIO,
                        CUOTA,DOCNUM,PAGADO,
                        C1,C2,C3,C4,C5,C6,C7,
                        C8,C9,C10,C11,C12,C13,
                        C14,C15,C16,C17,C18,
                        C19,C20,TOTAL,CAPITAL,
                        INTERESES,INTERES_ACUERDO,INTERES_RECARGO,
                        FECHAFACTURADO,PAG_BAN,
                        FECHAPAGO,PREANOI,PREANO';



          MI_VALORES := 'SELECT 
                                COMPANIA,CODIGOACUERDO,PREDIO,
                                CUOTA,DOCNUM,PAGADO,
                                0 AS C1,0 AS C2,0 AS C3,
                                0 AS C4,0 AS C5,0 AS C6,
                                0 AS C7,0 AS C8,0 AS C9,
                                0 AS C10,0 AS C11,0 AS C12,
                                0 AS C13,0 AS C14,0 AS C15,
                                 0 AS C16,0 AS C17,0 AS C18,
                                0 AS C19,0 AS C20,
                                TOTAL,CAPITAL,INTERESES,
                                INTERES_ACUERDO,INTERES_RECARGO,
                                FECHAFACTURADO,PAG_BAN,FECHAPAGO,
                                PREANOI,PREANO
                           FROM 
                                IP_FACTURADOSACUERDOS
                          WHERE 
                                COMPANIA      = '''||UN_COMPANIA||'''
                            AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                            AND PREDIO        = '''||UN_PREDIO||'''
                            AND NUMERO_ORDEN  = '''||UN_NUMERO_ORDEN||'''
                            AND CUOTA        >= '||UN_CUOTA;

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'IP_TMP_FACT_ABONO_EN_ACUERDO', 
                                      UN_ACCION  => 'IS', 
                                      UN_CAMPOS  => MI_CAMPOS, 
                                      UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;              

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_TMPABOACU);
    END;                            

    <<CALCULAR_CAPITAL>>                               
    FOR MI_RSFACACUERDOS IN( SELECT 
                                    CUOTA ,
                                    CAPITAL,
                                    INTERESES
                               FROM 
                                    IP_FACTURADOSACUERDOS
                              WHERE 
                                    COMPANIA      = UN_COMPANIA
                                AND CODIGOACUERDO = UN_ACUERDO
                                AND PREDIO        = UN_PREDIO
                                AND NUMERO_ORDEN  = UN_NUMERO_ORDEN)

      LOOP

      BEGIN 
        BEGIN
        IF MI_TMPMONTOCAPITAL >0 THEN 
          --PRIMER BLOQUE
          IF MI_RSFACACUERDOS.CUOTA < UN_CUOTA THEN 


                  MI_CAMPOS    := ' CAPITAL ='||MI_RSFACACUERDOS.CAPITAL;

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND PREDIO        = '''||UN_PREDIO||'''
                                    AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);                   


                 MI_TMPMONTOCAPITAL := MI_TMPMONTOCAPITAL- MI_RSFACACUERDOS.CAPITAL;                              
           END IF;       
           -- FIN PRIMER BLOQUE

           --SEGUNDO BLOQUE
          IF  MI_RSFACACUERDOS.CUOTA= UN_CUOTA THEN 

                  MI_CAMPOS    := ' CAPITAL ='||PCK_SYSMAN_UTL.FC_ROUND(MI_RSFACACUERDOS.CAPITAL+(MI_DBLMONTODIFERENCIA*MI_DBLPORCCAPITAL),0);

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND PREDIO        = '''||UN_PREDIO||'''
                                    AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);  



               MI_TMPMONTOCAPITAL    := MI_TMPMONTOCAPITAL - PCK_SYSMAN_UTL.FC_ROUND(MI_RSFACACUERDOS.CAPITAL+(MI_DBLMONTODIFERENCIA*MI_DBLPORCCAPITAL),0);                              
               MI_TMPCAPITALRESTANTE := MI_TMPMONTOCAPITAL;
          END IF;    
           --FIN SEGUNDO BLOQUE

           --TERCER BLOQUE
          IF MI_RSFACACUERDOS.CUOTA > UN_CUOTA  THEN 
            -- BLOQUE A
              IF UN_STRTIPO = 'C' THEN 
                IF MI_TMPMONTOCAPITAL > MI_RSFACACUERDOS.CAPITAL THEN 

                      MI_CAMPOS    := ' CAPITAL = '||MI_RSFACACUERDOS.CAPITAL;

                      MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                        AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                        AND PREDIO        = '''||UN_PREDIO||'''
                                        AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION); 


                  MI_TMPMONTOCAPITAL := MI_TMPMONTOCAPITAL- MI_RSFACACUERDOS.CAPITAL;

                ELSE

                  MI_CAMPOS    := ' CAPITAL = '||MI_TMPMONTOCAPITAL;

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND PREDIO        = '''||UN_PREDIO||'''
                                    AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION); 
                  MI_TMPMONTOCAPITAL := 0;
                END IF;
              END IF;  
              -- FIN BLOQUE A  

              -- BLOQUE B
              IF UN_STRTIPO = 'V' THEN 
                  IF MI_TMPMONTOCAPITAL > PCK_SYSMAN_UTL.FC_ROUND(MI_TMPCAPITALRESTANTE/UN_NUMCUOTAS,0) THEN 

                     MI_CAMPOS    := ' CAPITAL ='||PCK_SYSMAN_UTL.FC_ROUND(MI_TMPCAPITALRESTANTE/MI_NUMCUOTASRESTANTES,0);
                     MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                       AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                       AND PREDIO        = '''||UN_PREDIO||'''
                                       AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION); 

                    MI_TMPMONTOCAPITAL := MI_TMPMONTOCAPITAL - PCK_SYSMAN_UTL.FC_ROUND(MI_TMPCAPITALRESTANTE/MI_NUMCUOTASRESTANTES,0);

                  ELSE

                    MI_CAMPOS    := ' CAPITAL ='||MI_TMPMONTOCAPITAL;
                    MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                      AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                      AND PREDIO        = '''||UN_PREDIO||'''
                                      AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION); 
                    MI_TMPMONTOCAPITAL := 0;
                  END IF;
              END IF;
              -- FIN BLOQUE B
          END IF;
            -- FIN TERCER BLOQUE
        -- ELSE DEL IF PRICIPAL   
        ELSE 
            MI_CAMPOS    := ' CAPITAL = 0';
            MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                              AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                              AND PREDIO        = '''||UN_PREDIO||'''
                              AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_CONDICION => MI_CONDICION); 
        END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_CAPIT_TMP);
      END;

         --TERMINA IF  PRINCIPAL MI_TMPMONTOCAPITAL >0  
      BEGIN 
        BEGIN

          IF MI_TMPMONTOINTERESES > 0 THEN 
              --BLOQUE A 
              IF MI_RSFACACUERDOS.CUOTA < UN_CUOTA THEN 

                  MI_CAMPOS    := ' INTERESES ='||MI_RSFACACUERDOS.INTERESES;

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND PREDIO        = '''||UN_PREDIO||'''
                                    AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION); 

                  MI_TMPMONTOINTERESES := MI_TMPMONTOINTERESES - MI_RSFACACUERDOS.INTERESES;
              END IF;
              -- FIN BLOQUE A
              --BLOQUE B
              IF MI_RSFACACUERDOS.CUOTA = UN_CUOTA THEN 

                  MI_CAMPOS    := ' INTERESES ='|| PCK_SYSMAN_UTL.FC_ROUND(MI_RSFACACUERDOS.INTERESES+MI_DBLMONTODIFERENCIA*MI_DBLPORCINTERESES,0);

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND PREDIO        = '''||UN_PREDIO||'''
                                    AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION); 

                  MI_TMPMONTOINTERESES  := MI_TMPMONTOINTERESES- PCK_SYSMAN_UTL.FC_ROUND(MI_RSFACACUERDOS.INTERESES+(MI_DBLMONTODIFERENCIA*MI_DBLPORCINTERESES),0);                             
                  MI_TMPINTERESRESTANTE := MI_TMPMONTOINTERESES;
              END IF;
              --FIN BLOQUE B

              -- BLOQUE C
              IF MI_RSFACACUERDOS.CUOTA >UN_CUOTA THEN 
              --PRIMER BLOQUE
                  IF UN_STRTIPO = 'C' THEN 
                    IF MI_TMPMONTOINTERESES > MI_RSFACACUERDOS.INTERESES THEN 

                      MI_CAMPOS    := ' INTERESES ='||MI_RSFACACUERDOS.INTERESES;

                      MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                        AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                        AND PREDIO        = '''||UN_PREDIO||'''
                                        AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION); 

                      MI_TMPMONTOINTERESES := MI_TMPMONTOINTERESES - MI_RSFACACUERDOS.INTERESES;

                    ELSE

                        MI_CAMPOS    := ' INTERESES ='||MI_TMPMONTOINTERESES;

                        MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                          AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                          AND PREDIO        = '''||UN_PREDIO||'''
                                          AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION); 

                        MI_TMPMONTOINTERESES := 0;

                    END IF;
                  END IF;
                --FIN PRIMER BLOQUE   
               IF UN_STRTIPO = 'V' THEN 
                     IF MI_TMPMONTOINTERESES > PCK_SYSMAN_UTL.FC_ROUND(MI_TMPINTERESRESTANTE/ UN_NUMCUOTAS,0) THEN 

                        MI_CAMPOS    := ' INTERESES ='||PCK_SYSMAN_UTL.FC_ROUND (MI_TMPINTERESRESTANTE/MI_NUMCUOTASRESTANTES,0);

                        MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                          AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                          AND PREDIO        = '''||UN_PREDIO||'''
                                          AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION); 

                        MI_TMPMONTOINTERESES := MI_TMPMONTOINTERESES- PCK_SYSMAN_UTL.FC_ROUND(MI_TMPINTERESRESTANTE/MI_NUMCUOTASRESTANTES,0);   



                     ELSE 
                        MI_CAMPOS    := ' INTERESES ='||MI_TMPMONTOINTERESES;

                        MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                          AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                          AND PREDIO        = '''||UN_PREDIO||'''
                                          AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION); 

                        MI_TMPMONTOINTERESES := 0;   
                     END IF;     
               END IF;
            END IF ;
              --  
          ELSE 
              MI_CAMPOS    := ' INTERESES = 0';

              MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                AND PREDIO        = '''||UN_PREDIO||'''
                                AND CUOTA         = '|| MI_RSFACACUERDOS.CUOTA;

              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION); 

              END IF;  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_INTER_TMP);
      END;

   END LOOP;


     IF UN_STRTIPO = 'C' THEN

        BEGIN 
          BEGIN

             MI_CONDICION := '     COMPANIA           = '''||UN_COMPANIA||'''
                               AND CODIGOACUERDO      = '''||UN_ACUERDO||'''
                               AND PREDIO             = '''||UN_PREDIO||'''
                               AND (CAPITAL+INTERESES) = 0';

              MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                          UN_ACCION    => 'E',
                                          UN_CONDICION => MI_CONDICION);  

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DELETE_TMPABOACU);
        END;           

      END IF; 

     MI_TIPOINTERESACU :=  SUBSTR( PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                        UN_NOMBRE    => 'INTERES COMPUESTO - FINANCIACION - RECARGO',
                                                        UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL(),
                                                        UN_FECHA_PAR => SYSDATE), 1,2);
      BEGIN                                     
        IF NOT ('SI' = MI_TIPOINTERESACU OR 'NO'= MI_TIPOINTERESACU  ) THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;          
        END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PAR_TIPOINTACU
                     );
        END;

       MI_TCAPITAL          := MI_DBLMONTOCAPITAL;
       MI_TINTERESES        := MI_DBLMONTOINTERESES;
       MI_TOTALINTERESESACU :=0;
       MI_INTPERIODICIDAD   := 1;

       BEGIN 
          BEGIN 
             FOR MI_RSTMP IN ( SELECT 
                                      CUOTA,
                                      CAPITAL,
                                      INTERESES,
                                      INTERES_ACUERDO,
                                      INTERES_RECARGO
                                 FROM 
                                      IP_TMP_FACT_ABONO_EN_ACUERDO
                                WHERE 
                                      COMPANIA      = UN_COMPANIA
                                  AND CODIGOACUERDO = UN_ACUERDO
                                  AND PREDIO        = UN_PREDIO)

             LOOP
                IF MI_RSTMP.CUOTA > UN_CUOTA THEN
                  IF 'SI' = MI_TIPOINTERESACU  THEN

                      MI_INTERES_ACUERDO:= PCK_SYSMAN_UTL.FC_ROUND ((MI_TCAPITAL+MI_TINTERESES)*MI_DBLTASAINTERES*MI_INTPERIODICIDAD,0);

                       MI_CAMPOS    := ' INTERES_ACUERDO = '||MI_INTERES_ACUERDO;

                       MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                         AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                         AND PREDIO        = '''||UN_PREDIO||'''
                                         AND CUOTA         = '|| MI_RSTMP.CUOTA;

                      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION); 

                  ELSE 
                       MI_INTERES_ACUERDO := PCK_SYSMAN_UTL.FC_ROUND(MI_TCAPITAL * MI_DBLTASAINTERES * MI_INTPERIODICIDAD, 0);

                        MI_CAMPOS    := ' INTERES_ACUERDO ='||MI_INTERES_ACUERDO;

                        MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                          AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                          AND PREDIO        = '''||UN_PREDIO||'''
                                          AND CUOTA         = '|| MI_RSTMP.CUOTA;

                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION); 
                  END IF;

                  MI_TCAPITAL          := (MI_TCAPITAL -MI_RSTMP.CAPITAL);
                  MI_TINTERESES        := (MI_TINTERESES - MI_RSTMP.INTERESES);
                  MI_TOTALINTERESESACU := MI_TOTALINTERESESACU +MI_INTERES_ACUERDO;

                ELSE  
                  MI_TCAPITAL := (MI_TCAPITAL -MI_RSTMP.CAPITAL);
                  MI_TINTERESES := (MI_TINTERESES - MI_RSTMP.INTERESES);
                END IF;
             END LOOP;

             IF (NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                          UN_NOMBRE    => 'MANEJA INTERESES EQUITATIVOS',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL(),
                                          UN_FECHA_PAR => SYSDATE),'NO')) = 'SI' THEN 

                MI_TOTALINTERESESACU := MI_TOTALINTERESESACU / UN_NUMCUOTAS;

                FOR MI_RS_AUX IN ( SELECT 
                                          CUOTA,
                                          CAPITAL,
                                          INTERESES,
                                          INTERES_ACUERDO,
                                          INTERES_RECARGO
                                     FROM 
                                          IP_TMP_FACT_ABONO_EN_ACUERDO
                                    WHERE 
                                          COMPANIA      = UN_COMPANIA
                                      AND CODIGOACUERDO = UN_ACUERDO
                                      AND PREDIO        = UN_PREDIO 
                                      AND CUOTA         > UN_CUOTA) 
                 LOOP

                    MI_CAMPOS    := ' INTERES_ACUERDO ='||PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALINTERESESACU, 0);

                    MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                      AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                      AND PREDIO        = '''||UN_PREDIO||'''
                                      AND CUOTA         >'||UN_CUOTA;

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION); 
                 END LOOP;
             END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_INTACU_TMP);
    END;       

  END PR_CALCABONOENACUERDO;

--2
PROCEDURE PR_DISTRIBABONOENACUERDO 

/*
    NAME              : PR_DISTRIBABONOENACUERDO 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/02/2017
    TIME              : 12:40
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON 
    DATE MODIFIED     : 16/11/2017
    SOURCE MODULE     : IMPUESTO PREDIAL
    DESCRIPTION       : Procedimiento encargado de calcular los usuarios morosos e insertarlo en IP_TEMP_DEUDORES 
    PARAMETERS        : UN_COMPANIA                Codigo de la compania con la que se logea el usuario 
                        UN_ACUERDO                 Codigo del acuerdo de pago al cua se desea abonar
                        UN_PREDIO                  Codigo del predio relacionado al acuerdo de pago
                        UN_CUOTA                   Numero de la cuota a la cual se desea aplicar el abono 
                        UN_STRTIPO                 Tipo de abono, Disminucion de cuotas o disminucion de valor
                        UN_NUMCUOTAS               Numero de cuotas restantes del acuerdo de pago

    @NAME:  distribuirAbonoEnAcuerdo
 */

(
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ACUERDO                 IN VARCHAR2,
    UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CUOTA                   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_STRTIPO                 IN VARCHAR2,
    UN_NUMCUOTAS               IN PCK_SUBTIPOS.TI_ENTERO_LARGO

) AS 
      MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                 PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION               PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA                     PCK_SUBTIPOS.TI_RTA_ACME;
      MI_RS_FAC_ACUERDOS         SYS_REFCURSOR; 
      MI_SALDO_A_DISTRIBUIR      PCK_SUBTIPOS.TI_DOBLE;
      MI_CUOTA_fAC_ACUERDO       PCK_SUBTIPOS.TI_DOBLE;
      MI_RS_TMP_FAC_ACUERDOS     SYS_REFCURSOR; 
      MI_RS_CONCEPTOS            SYS_REFCURSOR; 
      MI_VALOR_ABONADO           PCK_SUBTIPOS.TI_DOBLE;
      MI_PRIORIDAD_ABONADA       PCK_SUBTIPOS.TI_DOBLE;
      MI_CUOTA_TRABAJO           PCK_SUBTIPOS.TI_ENTERO;
      MI_CONCEPTO                VARCHAR2(10 CHAR);
      MI_VALOR_FAC_ACU           PCK_SUBTIPOS.TI_DOBLE;
      MI_VALOR_TMP_FAC_ACU       PCK_SUBTIPOS.TI_DOBLE;
      MI_STRSQL                  VARCHAR2(1000 CHAR);
      MI_STRSQL_TMP_FAC_ACU      VARCHAR2(1000 CHAR);
      MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN

  MI_CUOTA_FAC_ACUERDO  := UN_CUOTA;
  MI_SALDO_A_DISTRIBUIR := 0;
  MI_PRIORIDAD_ABONADA  := 1;
  MI_CUOTA_TRABAJO      := UN_CUOTA;
  MI_VALOR_ABONADO      := 0;

  <<SIGUIENTE_CUOTA_FAC_ACUERDO>>
  IF MI_CUOTA_FAC_ACUERDO > UN_NUMCUOTAS THEN 
    GOTO AHORA_INTERESES;
  END IF;

  FOR  MI_RS_TMP_FAC_ACUERDOS IN(SELECT
                                        CAPITAL,
                                        CUOTA
                                   FROM 
                                        IP_TMP_FACT_ABONO_EN_ACUERDO
                                  WHERE 
                                        COMPANIA       =  UN_COMPANIA 
                                    AND CODIGOACUERDO  =  UN_ACUERDO
                                    AND CUOTA          >= MI_CUOTA_TRABAJO
                                  ORDER BY CUOTA)
  LOOP

    IF MI_SALDO_A_DISTRIBUIR = 0  THEN 

      MI_SALDO_A_DISTRIBUIR := NVL(MI_RS_TMP_FAC_ACUERDOS.CAPITAL,0);

    END IF;

    FOR MI_RS_CONCEPTOS IN (SELECT 
                                   CODIGO,
                                   PRIORIDAD_ABON_EN_ACU
                              FROM 
                                   IP_CONCEPTOS
                             WHERE
                                   COMPANIA                  = UN_COMPANIA
                               AND ANO                       = EXTRACT(YEAR FROM SYSDATE)
                               AND PRIORIDAD_ABON_EN_ACU    >= MI_PRIORIDAD_ABONADA
                               AND PRIORIDAD_ABON_EN_ACU    IS NOT NULL
                               AND NVL(ESCAPITALABACU,0) NOT IN 0
                             ORDER BY PRIORIDAD_ABON_EN_ACU )

    LOOP
      BEGIN 
        BEGIN 

            MI_CONCEPTO:= 'C'||MI_RS_CONCEPTOS.CODIGO;
            BEGIN
                MI_STRSQL:= ' SELECT
                                     C'||MI_RS_CONCEPTOS.CODIGO||' CONCEPTO
                                FROM 
                                     IP_FACTURADOSACUERDOS 
                               WHERE 
                                     COMPANIA      = '''||UN_COMPANIA||'''
                                 AND CODIGOACUERDO = '''||UN_ACUERDO||'''    
                                 AND PREDIO        = '''||UN_PREDIO||'''
                                 AND CUOTA         = '||MI_CUOTA_FAC_ACUERDO ;

                EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_FAC_ACU;

            EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_VALOR_FAC_ACU := NULL;
        END;

          BEGIN    
              MI_STRSQL_TMP_FAC_ACU :=' SELECT
                                               C'||MI_RS_CONCEPTOS.CODIGO||' CONCEPTO
                                          FROM 
                                               IP_TMP_FACT_ABONO_EN_ACUERDO 
                                         WHERE 
                                               COMPANIA      = '''||UN_COMPANIA||'''
                                           AND CODIGOACUERDO = '''||UN_ACUERDO||'''    
                                           AND PREDIO        = '''||UN_PREDIO||'''
                                           AND CUOTA         = '||MI_RS_TMP_FAC_ACUERDOS.CUOTA ;

               EXECUTE IMMEDIATE MI_STRSQL_TMP_FAC_ACU INTO MI_VALOR_TMP_FAC_ACU;

               EXCEPTION WHEN NO_DATA_FOUND THEN 

               MI_VALOR_TMP_FAC_ACU := NULL;
          END;      

              IF MI_VALOR_FAC_ACU IS NOT NULL  AND  MI_VALOR_TMP_FAC_ACU IS NOT NULL THEN 
                IF MI_SALDO_A_DISTRIBUIR >= (MI_VALOR_FAC_ACU - MI_VALOR_ABONADO) THEN 

                    MI_CAMPOS    := MI_CONCEPTO||'='||(MI_VALOR_TMP_FAC_ACU+(MI_VALOR_FAC_ACU-MI_VALOR_ABONADO));

                    MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                      AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                      AND CUOTA         = '|| MI_RS_TMP_FAC_ACUERDOS.CUOTA;

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);  

                    MI_SALDO_A_DISTRIBUIR := MI_SALDO_A_DISTRIBUIR-(MI_VALOR_FAC_ACU-MI_VALOR_ABONADO);

                    IF MI_VALOR_ABONADO > 0 THEN 

                      MI_VALOR_ABONADO     := 0;
                      MI_PRIORIDAD_ABONADA := 1;

                    END IF;  

                ELSE

                  MI_CAMPOS    := MI_CONCEPTO||'='||(MI_VALOR_TMP_FAC_ACU+MI_SALDO_A_DISTRIBUIR);

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND CUOTA         = '|| MI_RS_TMP_FAC_ACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);  


                  MI_VALOR_ABONADO      := MI_VALOR_ABONADO +MI_SALDO_A_DISTRIBUIR;
                  MI_PRIORIDAD_ABONADA  := MI_RS_CONCEPTOS.PRIORIDAD_ABON_EN_ACU;
                  MI_SALDO_A_DISTRIBUIR :=0 ;

                END IF;

                IF MI_SALDO_A_DISTRIBUIR = 0 THEN 
                  GOTO SIGUIENTE_CUOTA;
                END IF; 
              END IF;   

          MI_MSGERROR(1).CLAVE := 'CONCEPTO';
          MI_MSGERROR(1).VALOR := MI_CONCEPTO;                             


          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUA_CONC_TMP,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;

   END LOOP;

    <<SIGUIENTE_CUOTA>>
    IF MI_SALDO_A_DISTRIBUIR > 0 THEN 

        MI_CUOTA_FAC_ACUERDO := MI_CUOTA_FAC_ACUERDO+1;
        MI_CUOTA_TRABAJO     := MI_RS_TMP_FAC_ACUERDOS.CUOTA;
        GOTO SIGUIENTE_CUOTA_FAC_ACUERDO;
    ELSE
      MI_CUOTA_TRABAJO := MI_CUOTA_TRABAJO +1;
    END IF;

 END LOOP;

  <<AHORA_INTERESES>>

    MI_CUOTA_FAC_ACUERDO  := UN_CUOTA;
    MI_SALDO_A_DISTRIBUIR := 0;
    MI_PRIORIDAD_ABONADA  := 1;
    MI_CUOTA_TRABAJO      := UN_CUOTA;
    MI_VALOR_ABONADO      := 0;

  <<SIGUIENTECUOTAFACACUERDOINT>>  

  IF MI_CUOTA_FAC_ACUERDO > UN_NUMCUOTAS THEN 
    GOTO CUADRE_A_LAS_MALAS;
  END IF;


  -----------  INTERESES

  FOR  MI_RS_TMP_FAC_ACUERDOS IN(SELECT
                                        INTERESES,
                                        CUOTA
                                   FROM 
                                        IP_TMP_FACT_ABONO_EN_ACUERDO
                                  WHERE 
                                        COMPANIA       =  UN_COMPANIA 
                                    AND CODIGOACUERDO  =  UN_ACUERDO
                                    AND CUOTA          >= MI_CUOTA_TRABAJO
                                  ORDER BY CUOTA)
  LOOP
    IF MI_SALDO_A_DISTRIBUIR = 0  THEN 

      MI_SALDO_A_DISTRIBUIR := NVL(MI_RS_TMP_FAC_ACUERDOS.INTERESES,0);

    END IF;

    FOR MI_RS_CONCEPTOS IN (SELECT 
                                   CODIGO,
                                   PRIORIDAD_ABON_EN_ACU
                              FROM 
                                   IP_CONCEPTOS
                             WHERE
                                   COMPANIA                  = UN_COMPANIA
                               AND ANO                       = EXTRACT(YEAR FROM SYSDATE)
                               AND PRIORIDAD_ABON_EN_ACU    >= MI_PRIORIDAD_ABONADA
                               AND PRIORIDAD_ABON_EN_ACU    IS NOT NULL
                               AND NVL(ESINTERESABACU,0) NOT IN 0
                             ORDER BY PRIORIDAD_ABON_EN_ACU )

    LOOP
      BEGIN   
        BEGIN 
            MI_CONCEPTO:= 'C'||MI_RS_CONCEPTOS.CODIGO;
            BEGIN

              MI_STRSQL:= ' SELECT
                                   C'||MI_RS_CONCEPTOS.CODIGO||' CONCEPTO
                              FROM 
                                   IP_FACTURADOSACUERDOS 
                             WHERE 
                                   COMPANIA      = '''||UN_COMPANIA||'''
                               AND CODIGOACUERDO = '''||UN_ACUERDO||'''    
                               AND PREDIO        = '''||UN_PREDIO||'''
                               AND CUOTA         = '||MI_CUOTA_FAC_ACUERDO ;

                EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_FAC_ACU;

                EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_VALOR_FAC_ACU:= NULL;
             END;   

             BEGIN  
                 MI_STRSQL_TMP_FAC_ACU :=' SELECT
                                                   C'||MI_RS_CONCEPTOS.CODIGO||' CONCEPTO
                                              FROM 
                                                   IP_TMP_FACT_ABONO_EN_ACUERDO 
                                             WHERE 
                                                   COMPANIA      = '''||UN_COMPANIA||'''
                                               AND CODIGOACUERDO = '''||UN_ACUERDO||'''    
                                               AND PREDIO        = '''||UN_PREDIO||'''
                                               AND CUOTA         = '||MI_RS_TMP_FAC_ACUERDOS.CUOTA ;

                   EXECUTE IMMEDIATE MI_STRSQL_TMP_FAC_ACU INTO MI_VALOR_TMP_FAC_ACU;

                  EXCEPTION WHEN NO_DATA_FOUND THEN 
                  MI_VALOR_TMP_FAC_ACU:= NULL;
             END;      

              IF MI_VALOR_TMP_FAC_ACU IS NOT NULL  AND MI_VALOR_FAC_ACU IS NOT NULL THEN 
                IF MI_SALDO_A_DISTRIBUIR >= (MI_VALOR_FAC_ACU - MI_VALOR_ABONADO) THEN 

                    MI_CAMPOS    := MI_CONCEPTO||'='||(MI_VALOR_TMP_FAC_ACU+(MI_VALOR_FAC_ACU-MI_VALOR_ABONADO));

                    MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                      AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                      AND CUOTA         = '|| MI_RS_TMP_FAC_ACUERDOS.CUOTA;

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);  

                    MI_SALDO_A_DISTRIBUIR := MI_SALDO_A_DISTRIBUIR-(MI_VALOR_FAC_ACU-MI_VALOR_ABONADO);

                    IF MI_VALOR_ABONADO > 0 THEN 

                      MI_VALOR_ABONADO := 0;
                      MI_PRIORIDAD_ABONADA := 1;

                    END IF;  

                ELSE     

                  MI_CAMPOS    := MI_CONCEPTO||'='||(MI_VALOR_TMP_FAC_ACU+MI_SALDO_A_DISTRIBUIR);

                  MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                    AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                    AND CUOTA         = '|| MI_RS_TMP_FAC_ACUERDOS.CUOTA;

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);  

                  MI_VALOR_ABONADO      := MI_VALOR_ABONADO + MI_SALDO_A_DISTRIBUIR;
                  MI_PRIORIDAD_ABONADA  := MI_RS_CONCEPTOS.PRIORIDAD_ABON_EN_ACU;
                  MI_SALDO_A_DISTRIBUIR := 0;

                END IF;

                IF MI_SALDO_A_DISTRIBUIR = 0 THEN 
                  GOTO SIGUIENTE_CUOTA_INT;
                END IF; 
              END IF;

        MI_MSGERROR(1).CLAVE := 'CONCEPTO';
        MI_MSGERROR(1).VALOR := MI_CONCEPTO;                             


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUA_CONC_INTTMP,
                                 UN_REEMPLAZOS => MI_MSGERROR);
    END;   
    END LOOP;

    <<SIGUIENTE_CUOTA_INT>>
    IF MI_SALDO_A_DISTRIBUIR > 0 THEN 

        MI_CUOTA_FAC_ACUERDO := MI_CUOTA_FAC_ACUERDO+1;
        MI_CUOTA_TRABAJO     := MI_RS_TMP_FAC_ACUERDOS.CUOTA;
        GOTO SIGUIENTECUOTAFACACUERDOINT;
    ELSE
      MI_CUOTA_TRABAJO := MI_CUOTA_TRABAJO +1;
    END IF;

 END LOOP;
 RETURN;
  ---------- FIN INTERESES
   <<CUADRE_A_LAS_MALAS>>
   IF MI_CUOTA_FAC_ACUERDO > (UN_NUMCUOTAS+1) THEN 
    RETURN;
    ELSE 
    ----  CUADRE A LAS MALAS 
    FOR  MI_RS_TMP_FAC_ACUERDOS IN(SELECT
                                        INTERESES,
                                        CUOTA
                                   FROM 
                                        IP_TMP_FACT_ABONO_EN_ACUERDO
                                  WHERE 
                                        COMPANIA       =  UN_COMPANIA 
                                    AND CODIGOACUERDO  =  UN_ACUERDO
                                    AND CUOTA          >= UN_CUOTA
                                  ORDER BY CUOTA)
  LOOP

    FOR MI_RS_CONCEPTOS IN (SELECT 
                                   CODIGO,
                                   PRIORIDAD_ABON_EN_ACU
                              FROM 
                                   IP_CONCEPTOS
                             WHERE
                                   COMPANIA                  = UN_COMPANIA
                               AND ANO                       = EXTRACT(YEAR FROM SYSDATE)
                               AND PRIORIDAD_ABON_EN_ACU    >= MI_PRIORIDAD_ABONADA
                               AND PRIORIDAD_ABON_EN_ACU    IS NOT NULL
                               AND NVL(ESCAPITALVIGENCIA,0) NOT IN 0
                             ORDER BY PRIORIDAD_ABON_EN_ACU )

    LOOP

    MI_CONCEPTO:= 'C'||MI_RS_CONCEPTOS.CODIGO;
     BEGIN  
         MI_STRSQL_TMP_FAC_ACU :=' SELECT
                                           C'||MI_RS_CONCEPTOS.CODIGO||' CONCEPTO
                                      FROM 
                                           IP_TMP_FACT_ABONO_EN_ACUERDO 
                                     WHERE 
                                           COMPANIA      = '''||UN_COMPANIA||'''
                                       AND CODIGOACUERDO = '''||UN_ACUERDO||'''    
                                       AND PREDIO        = '''||UN_PREDIO||'''
                                       AND CUOTA         >= '||MI_CUOTA_FAC_ACUERDO ;

           EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_TMP_FAC_ACU;

          EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_VALOR_TMP_FAC_ACU:= NULL;
     END;    

      IF MI_VALOR_TMP_FAC_ACU <> NULL THEN 
         BEGIN 
            BEGIN 

                MI_CAMPOS    := MI_CONCEPTO||'='||(MI_VALOR_TMP_FAC_ACU+MI_SALDO_A_DISTRIBUIR);

                MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                  AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                                  AND CUOTA         = '|| MI_CUOTA_TRABAJO;

                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);  


                MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                MI_MSGERROR(1).VALOR := MI_CONCEPTO;                             


                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUA_CONC_TMP,
                                         UN_REEMPLAZOS => MI_MSGERROR);
            END; 

        END IF;   
           MI_CUOTA_TRABAJO      := MI_CUOTA_TRABAJO+1;
           MI_CUOTA_FAC_ACUERDO  := MI_CUOTA_TRABAJO;
           MI_SALDO_A_DISTRIBUIR :=0;
           MI_SALDO_A_DISTRIBUIR := MI_SALDO_A_DISTRIBUIR-(MI_VALOR_FAC_ACU-MI_VALOR_ABONADO);

           GOTO SIGUIENTECUOTAFACACUERDOINT;

       END LOOP;
    END LOOP;   
    --- FIN CUADRE A LAS MALAS
   END IF;
   RETURN;
END PR_DISTRIBABONOENACUERDO;
--3


PROCEDURE PR_TOTALIZARABONOENACUERDO
/*
    NAME              : PR_TOTALIZARABONOENACUERDO 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/02/2017
    TIME              : 12:40
    SOURCE MODULE     : IMPUESTO PREDIAL

    @NAME:  totalizarAbonoEnAcuerdo
 */
(
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ACUERDO                 IN VARCHAR2,
    UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO

)AS 
      MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                 PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION               PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA                     PCK_SUBTIPOS.TI_RTA_ACME;
      MI_STRSQL                  VARCHAR2(1000 CHAR);
      MI_CONCEPTOS               VARCHAR2(1000 CHAR);   
      MI_RS                      SYS_REFCURSOR; 
BEGIN

  FOR MI_RS IN (SELECT 
                       CODIGO,
                       VIGANT,
                       VIGDFR
                  FROM 
                       IP_CONCEPTOS
                 WHERE 
                       COMPANIA              = UN_COMPANIA
                   AND ANO                   = EXTRACT (YEAR FROM SYSDATE)
                   AND PRIORIDAD_ABON_EN_ACU IS NOT NULL
                   AND NVL(ESCAPITALABACU,0) <> 0
                 ORDER BY 
                       PRIORIDAD )

  LOOP
  MI_CONCEPTOS:=MI_CONCEPTOS||'C'||MI_RS.CODIGO||'+';

  END LOOP;


   MI_CAMPOS    := 'CAPITAL =('||SUBSTR(MI_CONCEPTOS,1,LENGTH(MI_CONCEPTOS)-1)||')';

   MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                     AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                     AND PREDIO        = '||UN_PREDIO;

   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                UN_ACCION    => 'M',
                                UN_CAMPOS    => MI_CAMPOS,
                                UN_CONDICION => MI_CONDICION);  




  ---INTERESES
  MI_CONCEPTOS:='';
   FOR MI_RS IN (SELECT 
                       CODIGO,
                       VIGANT,
                       VIGDFR
                  FROM 
                       IP_CONCEPTOS
                 WHERE 
                       COMPANIA              = UN_COMPANIA
                   AND ANO                   = EXTRACT (YEAR FROM SYSDATE)
                   AND PRIORIDAD_ABON_EN_ACU IS NOT NULL
                   AND NVL(ESINTERESABACU,0) <> 0
                 ORDER BY 
                       PRIORIDAD )

  LOOP
  MI_CONCEPTOS:=MI_CONCEPTOS||'C'||MI_RS.CODIGO||'+';

  END LOOP;


   MI_CAMPOS    := 'INTERESES =('||SUBSTR(MI_CONCEPTOS,1,LENGTH(MI_CONCEPTOS)-1)||')';

   MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                     AND CODIGOACUERDO = '''||UN_ACUERDO||'''
                     AND PREDIO        = '||UN_PREDIO;

   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_FACT_ABONO_EN_ACUERDO',
                                UN_ACCION    => 'M',
                                UN_CAMPOS    => MI_CAMPOS,
                                UN_CONDICION => MI_CONDICION);  





END PR_TOTALIZARABONOENACUERDO;

--4

FUNCTION FC_IMPRIMIRFACTABONOENACUERDO
/*
    NAME              : FC_IMPRIMIRFACTABONOENACUERDO 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/02/2017
    TIME              : 12:40
    SOURCE MODULE     : IMPUESTO PREDIAL

   @NAME:  imprimirFactAbonoEnAcuerdo
 */
   (UN_COMPANIA                       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO_ORDEN                   IN PCK_SUBTIPOS.TI_NUMORDEN, 
    UN_ACUERDO                        IN VARCHAR2,
    UN_PREDIO                         IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CUOTA                          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_STRTIPO                        IN VARCHAR2,
    UN_ANULACION                      IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO                        IN VARCHAR2) 

    RETURN VARCHAR2
 AS 
    MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RS_FAC_ACUERDOS                SYS_REFCURSOR; 
    MI_SALDO_A_DISTRIBUIR             PCK_SUBTIPOS.TI_DOBLE;
    MI_CUOTA_fAC_ACUERDO              PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_TMP_FAC_ACUERDOS            SYS_REFCURSOR; 
    MI_RS_CONCEPTOS                   SYS_REFCURSOR; 
    MI_USAURIOSFACTURACION            NUMBER;
    MI_USAURIOSPROCESOFACTURACION     NUMBER;
    MI_STRSQL                         PCK_SUBTIPOS.TI_STRSQL;
    MI_C1                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C2                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C3                             PCK_SUBTIPOS.TI_DOBLE;  
    MI_C4                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C5                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C6                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C7                             PCK_SUBTIPOS.TI_DOBLE;
    MI_C8                             PCK_SUBTIPOS.TI_DOBLE;    
    MI_C9                             PCK_SUBTIPOS.TI_DOBLE;  
    MI_C10                            PCK_SUBTIPOS.TI_DOBLE;      
    MI_C11                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C12                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C13                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C14                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C15                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C16                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C17                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C18                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C19                            PCK_SUBTIPOS.TI_DOBLE;
    MI_C20                            PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL                          PCK_SUBTIPOS.TI_DOBLE;
    MI_CAPITAL                        PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERESES                      PCK_SUBTIPOS.TI_DOBLE;  
    MI_INTERES_ACUERDO                PCK_SUBTIPOS.TI_DOBLE;  
    MI_INTERES_RECARGO                PCK_SUBTIPOS.TI_DOBLE;
    MI_PREVAL                         PCK_SUBTIPOS.TI_DOBLE;
    MI_CONSECUTIVO                    PCK_SUBTIPOS.TI_ENTERO;      
    MI_CRITERIO                       VARCHAR2(1000 CHAR);
    MI_CODACUERDO                     IP_TMP_FACT_ABONO_EN_ACUERDO.CODIGOACUERDO%TYPE;
    MI_PREDIO                         IP_TMP_FACT_ABONO_EN_ACUERDO.PREDIO%TYPE;
    MI_TIPO_FRA                       VARCHAR2(2 CHAR);
    MI_MANEJA_NUMERACION_UNICA        VARCHAR(2 CHAR);
    MI_SECUENCIA                      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSECUTIVOREAL                PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_STR_NUM_RECIBO                 IP_TMP_FACT_ABONO_EN_ACUERDO.DOCNUM%TYPE;
    MI_PRECOD                         IP_RECIBOS_DE_PAGO.PRECOD%TYPE; 
    MI_PREFEC                         IP_RECIBOS_DE_PAGO.PREFEC%TYPE;
    MI_PAGO                           PCK_SUBTIPOS.TI_LOGICO;
    MI_FECHA_AUX                      DATE;
    MI_PREANO                         PCK_SUBTIPOS.TI_ANIO;
    MI_PREANO_I                       PCK_SUBTIPOS.TI_ANIO;          
    MI_MSGERROR                       PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN

MI_USAURIOSFACTURACION := PCK_PREDIAL.FC_PERMISOACCION(UN_COMPANIA => UN_COMPANIA,
                                                       UN_MODULO   => PCK_DATOS.FC_MODULOPREDIAL,
                                                       UN_ACCION   => 'USUARIOS QUE GENERAN FACTURACION',
                                                       UN_USUARIO  => UN_USUARIO
                                                       );

MI_USAURIOSPROCESOFACTURACION := PCK_PREDIAL.FC_PERMISOACCION(UN_COMPANIA => UN_COMPANIA,
                                                       UN_MODULO   => PCK_DATOS.FC_MODULOPREDIAL,
                                                       UN_ACCION   => 'USUARIOS DE PROCESOS QUE GENERAN FACTURACION',
                                                       UN_USUARIO  => UN_USUARIO
                                                       );


IF  MI_USAURIOSFACTURACION = -1 OR  MI_USAURIOSPROCESOFACTURACION = -1 THEN 

   SELECT 
          IP_TMP_FACT_ABONO_EN_ACUERDO.CODIGOACUERDO,
          IP_TMP_FACT_ABONO_EN_ACUERDO.PREDIO,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C1,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C2,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C3,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C4,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C5,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C6,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C7,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C8,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C9,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C10,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C11,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C12,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C13,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C14,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C15,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C16,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C17,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C18,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C19,
          IP_TMP_FACT_ABONO_EN_ACUERDO.C20,
          IP_TMP_FACT_ABONO_EN_ACUERDO.TOTAL,
          IP_TMP_FACT_ABONO_EN_ACUERDO.CAPITAL,
          IP_TMP_FACT_ABONO_EN_ACUERDO.INTERESES,
          IP_TMP_FACT_ABONO_EN_ACUERDO.INTERES_ACUERDO,
          IP_TMP_FACT_ABONO_EN_ACUERDO.INTERES_RECARGO,
          IP_FACTURADOSACUERDOS.PREANOI,
          IP_FACTURADOSACUERDOS.PREANO
     INTO  
          MI_CODACUERDO,
          MI_PREDIO,
          MI_C1,MI_C2,MI_C3,MI_C4,MI_C5,
          MI_C6,MI_C7,MI_C8,MI_C9,MI_C10,
          MI_C11,MI_C12,MI_C13,MI_C14,
          MI_C15,MI_C16,MI_C17,MI_C18,
          MI_C19,MI_C20,
          MI_TOTAL,
          MI_CAPITAL,
          MI_INTERESES,
          MI_INTERES_ACUERDO,
          MI_INTERES_RECARGO,
          MI_PREANO_I,
          MI_PREANO
    FROM 
         IP_TMP_FACT_ABONO_EN_ACUERDO
    LEFT JOIN IP_FACTURADOSACUERDOS 
      ON IP_TMP_FACT_ABONO_EN_ACUERDO.COMPANIA       = IP_FACTURADOSACUERDOS.COMPANIA
     AND IP_TMP_FACT_ABONO_EN_ACUERDO.CODIGOACUERDO  = IP_FACTURADOSACUERDOS.CODIGOACUERDO
     AND IP_TMP_FACT_ABONO_EN_ACUERDO.PREDIO         = IP_FACTURADOSACUERDOS.PREDIO
     AND IP_TMP_FACT_ABONO_EN_ACUERDO.CUOTA          = IP_FACTURADOSACUERDOS.CUOTA
   WHERE 
         IP_TMP_FACT_ABONO_EN_ACUERDO.COMPANIA      = UN_COMPANIA
     AND IP_TMP_FACT_ABONO_EN_ACUERDO.CODIGOACUERDO = UN_ACUERDO
     AND IP_TMP_FACT_ABONO_EN_ACUERDO.CUOTA         = UN_CUOTA;

   IF MI_CODACUERDO IS NOT NULL THEN 

        IF UN_ANULACION = -1 THEN 
           BEGIN 
              BEGIN 
                 MI_CAMPOS    := 'ANULADO = -1, FECHAANULACION = SYSDATE ';

                 MI_CONDICION := '     COMPANIA  = '''||UN_COMPANIA||'''
                                   AND PRECOD    = '''||UN_PREDIO||'''
                                   AND PAGO      = 0 
                                   AND ANULADO   = 0 
                                   AND ESACUERDO NOT IN (0)';

                 MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);  

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ANULACION);
        END;     


        END IF;


       MI_MANEJA_NUMERACION_UNICA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA ,
                                                           UN_NOMBRE    => 'MANEJA NUMERACION UNICA' ,
                                                           UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL,
                                                           UN_FECHA_PAR => SYSDATE);


        IF MI_MANEJA_NUMERACION_UNICA = 'SI' THEN 
          MI_TIPO_FRA := 'N';
        ELSE
          MI_TIPO_FRA := 'A';
        END IF;

       BEGIN  

          SELECT 
                  SECUENCIA,
                  CONSECUTIVOREAL
            INTO 
                  MI_SECUENCIA,
                  MI_CONSECUTIVOREAL
            FROM IP_NUMEROSDEFACTURA
           WHERE COMPANIA = UN_COMPANIA
             AND TIPO = 'N'
             AND ACTIVO IN (-1)
           ORDER BY SECUENCIA DESC;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_STR_NUM_RECIBO := '000000001';
      END ;

         MI_STR_NUM_RECIBO := TO_CHAR(NVL(MI_CONSECUTIVOREAL,0)+1);

         BEGIN 
            BEGIN 

               MI_CAMPOS    := ' CONSECUTIVOREAL = '''||MI_STR_NUM_RECIBO||'''';

               MI_CONDICION := '     COMPANIA  = '''||UN_COMPANIA||'''
                                 AND SECUENCIA = '||MI_SECUENCIA||'
                                 AND TIPO      = '''||MI_TIPO_FRA||''' 
                                 AND ACTIVO    IN (-1)';

               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_NUMEROSDEFACTURA',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_NUMRECIB);
      END;


       BEGIN 
         SELECT 
                PRECOD, 
                PREFEC,
                PAGO 
            INTO 
                MI_PRECOD,
                MI_PREFEC,
                MI_PAGO
           FROM IP_RECIBOS_DE_PAGO 
           WHERE COMPANIA =UN_COMPANIA 
             AND DOCNUM = PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9);

            EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_PRECOD := '0';

       END;      

       BEGIN 
          IF MI_PRECOD <> '0'  THEN 

             IF MI_PAGO = -1 THEN 

                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

              ELSE  

                RETURN 'EL RECIBO YA ESTA IMPRESO.';

              END IF;


          ELSE 
              BEGIN 
                BEGIN 
                     MI_CAMPOS  := ' COMPANIA,
                                     NUMERO_ORDEN,
                                     PREANOF, 
                                     PRECOD,PREANOI,
                                     PREANO,PREFEC,
                                     DOCNUM,PREVAL,
                                     PREFECLIM, 
                                     FECHAFACACUERDO, 
                                     PREUSU, 
                                     C1, C2, C3, C4,C5, 
                                     C6, C7, C8, C9, C10,
                                     C11, C12, C13, C14,
                                     C15, C16, C17, C18,
                                     C19, C20,
                                     PAGO, ANULADO, 
                                     ESACUERDO,NCUOTA_ACUERDO, 
                                     INTERES_ACUERDO,
                                     INTERES_RECARGO, ACUERDO,
                                     IND_MULTIFECHAS,ABONOAACUERDO,
                                     TIPOABONOAACUERDO';

                     MI_VALORES  := '''' || UN_COMPANIA || ''',
                                    '''||UN_NUMERO_ORDEN||''',
                                    '||MI_PREANO||',
                                    ''' || UN_PREDIO || ''',
                                    '||MI_PREANO_I||',
                                    '||MI_PREANO||',
                                    SYSDATE,
                                    '''|| PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9)||''',
                                    '||MI_TOTAL||',
                                    TO_DATE('''||LAST_DAY(SYSDATE)||''',''DD/MM/YYYY HH24:mi:ss''),
                                    NULL,
                                    '''||UN_USUARIO||''',
                                    '||MI_C1||','||MI_C2||','||MI_C3||','||MI_C4||','||MI_C5||',
                                    '||MI_C6||','||MI_C7||','||MI_C8||','||MI_C9||','||MI_C10||',
                                    '||MI_C11||','||MI_C12||','||MI_C13||','||MI_C14||','||MI_C15||',
                                    '||MI_C16||','||MI_C17||','||MI_C18||','||MI_C19||','||MI_C20||',
                                    0,
                                    0,
                                    -1,
                                    '||UN_CUOTA||',
                                    '||MI_INTERES_ACUERDO||',
                                    '||MI_INTERES_RECARGO||',
                                    '''||UN_ACUERDO||''',
                                    0,
                                    -1,
                                    '''||UN_STRTIPO||'''';

                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_RECIBOS_DE_PAGO',
                                                            UN_ACCION=>'I',
                                                            UN_CAMPOS=>MI_CAMPOS,
                                                            UN_VALORES=>MI_VALORES);    



                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REG_RECIB);
          END;



            MI_CRITERIO := '   COMPANIA = '''||UN_COMPANIA||''' 
                           AND DOCNUM   = '''||PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9)||'''';

            MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => 'IP_DETALLE_RECIBOPAGO',
                                                               UN_CRITERIO=>MI_CRITERIO,
                                                               UN_CAMPO => 'CONSECUTIVO',
                                                               UN_INICIAL => 0);                                          

               BEGIN 
                  BEGIN 
                     MI_CAMPOS  := ' COMPANIA,
                                     CONSECUTIVO,
                                     PREANO,
                                     DOCNUM,
                                     C1, C2, C3, C4,C5, 
                                     C6, C7, C8, C9, C10,
                                     C11, C12, C13, C14,
                                     C15, C16, C17, C18,
                                     C19, C20,
                                     INTERES_ACUERDO,
                                     INTERES_RECARGO';

                     MI_VALORES  := '''' || UN_COMPANIA || ''',
                                    '||MI_CONSECUTIVO||',
                                    '||MI_PREANO||',
                                    '''|| PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9)||''',
                                    '||MI_C1||','||MI_C2||','||MI_C3||','||MI_C4||','||MI_C5||',
                                    '||MI_C6||','||MI_C7||','||MI_C8||','||MI_C9||','||MI_C10||',
                                    '||MI_C11||','||MI_C12||','||MI_C13||','||MI_C14||','||MI_C15||',
                                    '||MI_C16||','||MI_C17||','||MI_C18||','||MI_C19||','||MI_C20||',
                                   '||MI_INTERES_ACUERDO||',
                                    '||MI_INTERES_RECARGO;

                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_DETALLE_RECIBOPAGO',
                                                  UN_ACCION=>'I',
                                                  UN_CAMPOS=>MI_CAMPOS,
                                                  UN_VALORES=>MI_VALORES);      


                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REG_RECIBDET);
            END;


             BEGIN 
                BEGIN 
                   MI_CAMPOS    := ' DOCNUM = '''||PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9)||'''';

                   MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                     AND CODIGOACUERDO = '||UN_ACUERDO||'
                                     AND NUMERO_ORDEN  = '''||UN_NUMERO_ORDEN||'''
                                     AND CUOTA         = '||UN_CUOTA||' 
                                     AND NVL(PAGADO,0) = 0';

                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOSACUERDOS',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);                                         

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_DOCNUM_FACT);
          END;                                

          END IF;

          MI_MSGERROR(1).CLAVE := 'PRECOD';
          MI_MSGERROR(1).VALOR := MI_PRECOD;

          MI_MSGERROR(2).CLAVE := 'FECHA';
          MI_MSGERROR(2).VALOR := TO_CHAR(MI_PREFEC,'DD/MM/YYYY');

          MI_MSGERROR(3).CLAVE := 'ES_PAGADO';
          MI_MSGERROR(3).VALOR := ' Y ESTA PAGADO ' ;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIB_PAG,
                                     UN_REEMPLAZOS => MI_MSGERROR);
      END;


       SELECT 
              PREVAL
         INTO MI_PREVAL  
         FROM 
              IP_RECIBOS_DE_PAGO
        WHERE 
              COMPANIA = UN_COMPANIA
          AND DOCNUM   = PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9);

            BEGIN 
                BEGIN 

                    MI_CAMPOS    := ' FACTURA_ACUERDO = '''||PCK_SYSMAN_UTL.FC_STRZERO(MI_STR_NUM_RECIBO,9)||''',
                                     TOTAL_ACUERDO='||MI_PREVAL;

                    MI_CONDICION := '     COMPANIA      = '''||UN_COMPANIA||'''
                                       AND CODIGO = '''||UN_PREDIO||'''';

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);     

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_FACT_ACU);
          END;      


   END IF;    


END IF;
  RETURN 'OK';
END FC_IMPRIMIRFACTABONOENACUERDO;

--5
 PROCEDURE PR_FACTURARABONOENACUERDO 
/*
    NAME              : PR_FACTURARABONOENACUERDO 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/02/2017
    TIME              : 12:40
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON 
    DATE MODIFIED     : 15/11/2017
    SOURCE MODULE     : IMPUESTO PREDIAL
    DESCRIPTION       : Procedimiento de validacion de datos requeridos para ejecutar proceso de abono a cuotas en acuerdos de pago 
    PARAMETERS        : UN_COMPANIA                Codigo de la compania con la que se logea el usuario 
                        UN_NUMERO_ORDEN            Orden del propietario del predio, usualmente se trabaja con el orden 001 
                        UN_ACUERDO                 Codigo del acuerdo de pago al cua se desea abonar
                        UN_PREDIO                  Codigo del predio relacionado al acuerdo de pago
                        UN_CUOTA                   Numero de la cuota a la cual se desea aplicar el abono 
                        UN_VALORCUOTA              Valor de la cuota en el momento del firmar acuerdo de pago
                        UN_VALORABONO              Valor que se desea abonar al acuerdo de pago
                        UN_STRTIPO                 Tipo de abono, Disminucion de cuotas o disminucion de valor
                        UN_ANULACION               Configuracion del parametro CONTROLAR RECIBOS POR USUARIO para ver si se anula o no facturas activa,
                        UN_FACTURAR                su valor indica si el proceso de calculo es preliminar o definitivo, 
                        UN_USUARIO                 usuario logeado y que ejecuta el proceso de abono 
    @NAME:  facturarAbonoEnAcuerdo
 */

   (
    UN_COMPANIA                       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMERO_ORDEN                   IN PCK_SUBTIPOS.TI_NUMORDEN, 
    UN_ACUERDO                        IN VARCHAR2,
    UN_PREDIO                         IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CUOTA                          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_VALOR_CUOTA                    IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VALO_RABONO                    IN PCK_SUBTIPOS.TI_DOBLE,
    UN_STRTIPO                        IN VARCHAR2,
    UN_ANULACION                      IN PCK_SUBTIPOS.TI_LOGICO,
    UN_FACTURAR                       IN PCK_SUBTIPOS.TI_LOGICO, 
    UN_USUARIO                        IN VARCHAR2
   ) 
AS 
  MI_NCUOTAS                          NUMBER;
  MI_RTA                              VARCHAR2(1000 CHAR);
  MI_MSGERROR                         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PENDIENTEPAGO                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_ACUERDOCANCELADO                 PCK_SUBTIPOS.TI_LOGICO;
  MI_ACUERDOANULADO                   PCK_SUBTIPOS.TI_LOGICO;
BEGIN


  -- VALIDAR QUE EL ACUERDO DE PAGO ESTE ACTIVO PARA REALIZAR EL PROCESO 

  BEGIN
    BEGIN
      SELECT CANCELADO, ANULADO
      INTO   MI_ACUERDOCANCELADO, MI_ACUERDOANULADO
      FROM   IP_ACUERDOS
      WHERE  COMPANIA       = UN_COMPANIA
      AND    CODIGOACUERDO  = UN_ACUERDO
      AND    PREDIO         = UN_PREDIO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    IF ((MI_ACUERDOCANCELADO <> 0) OR (MI_ACUERDOANULADO <>0 )) THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END IF;

    IF UN_CUOTA <= 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END IF;

    EXCEPTION WHEN   PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;
      MI_MSGERROR(2).CLAVE := 'NROACUERDO';
      MI_MSGERROR(2).VALOR :=  UN_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ABACUERDOSVAL,
                                 UN_REEMPLAZOS => MI_MSGERROR);  
  END;
  -- VALIDAR QUE NO EXISTAN CUOTAS SIN PAGAR ANTERIORES A LA CUOTA A LA CUAL SE DESEA ABONAR

  BEGIN
    BEGIN    
      SELECT  COUNT(CUOTA) CANTCUOTAS
      INTO    MI_NCUOTAS
      FROM    IP_FACTURADOSACUERDOS
      WHERE   COMPANIA       = UN_COMPANIA
      AND     CODIGOACUERDO  = UN_ACUERDO
      AND     PREDIO         = UN_PREDIO
      AND     CUOTA          < UN_CUOTA
      AND     PAGADO         IN (0); 

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NCUOTAS :=0;
    END;

    --IF MI_NCUOTAS 
  END;
  -- VALIDAR QUE EL TOTAL PENDIENTE POR PAGAR DEL ACUERDO NO SEA MENOR AL VALOR POR ABONAR

  BEGIN
    BEGIN
      SELECT  SUM(TOTAL) PENDIENTEPAGO
      INTO    MI_PENDIENTEPAGO
      FROM    IP_FACTURADOSACUERDOS
      WHERE   COMPANIA      = UN_COMPANIA
      AND     NUMERO_ORDEN  = UN_NUMERO_ORDEN
      AND     CODIGOACUERDO = UN_ACUERDO
      AND     PREDIO        = UN_PREDIO
      AND     PAGADO        = 0;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PENDIENTEPAGO := 0;
    END;


    IF MI_PENDIENTEPAGO = 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;
      MI_MSGERROR(2).CLAVE := 'NROACUERDO';
      MI_MSGERROR(2).VALOR :=  UN_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ABACUERDOPENDPAGO,
                                 UN_REEMPLAZOS => MI_MSGERROR);  

  END;

  BEGIN 
    SELECT  COUNT(CODIGOACUERDO) -1 AS NCUOTAS
    INTO    MI_NCUOTAS
    FROM    IP_FACTURADOSACUERDOS
    WHERE   COMPANIA      = UN_COMPANIA
    AND     NUMERO_ORDEN  = UN_NUMERO_ORDEN
    AND     CODIGOACUERDO = UN_ACUERDO
    AND     PREDIO        = UN_PREDIO;

    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_NCUOTAS :=0 ;
  END;

  BEGIN
    IF MI_NCUOTAS < 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;
      MI_MSGERROR(2).CLAVE := 'NROACUERDO';
      MI_MSGERROR(2).VALOR :=  UN_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ABACUERDOSCUOTAS,
                                 UN_REEMPLAZOS => MI_MSGERROR);  
  END;

  PCK_PREDIAL_COM5.PR_CALCABONOENACUERDO(UN_COMPANIA     => UN_COMPANIA,
                                         UN_NUMERO_ORDEN => UN_NUMERO_ORDEN,
                                         UN_ACUERDO      => UN_ACUERDO,
                                         UN_PREDIO       => UN_PREDIO,
                                         UN_CUOTA        => UN_CUOTA,
                                         UN_VALORCUOTA   => UN_VALOR_CUOTA,
                                         UN_VALORABONO   => UN_VALO_RABONO,
                                         UN_STRTIPO      => UN_STRTIPO,
                                         UN_NUMCUOTAS    => MI_NCUOTAS);

  PCK_PREDIAL_COM5.PR_DISTRIBABONOENACUERDO(UN_COMPANIA  => UN_COMPANIA,
                                            UN_ACUERDO   => UN_ACUERDO,
                                            UN_PREDIO    => UN_PREDIO ,
                                            UN_CUOTA     => UN_CUOTA,
                                            UN_STRTIPO   => UN_STRTIPO,
                                            UN_NUMCUOTAS => MI_NCUOTAS);   

  PCK_PREDIAL_COM5.PR_TOTALIZARABONOENACUERDO(UN_COMPANIA => UN_COMPANIA,
                                              UN_ACUERDO  => UN_ACUERDO,
                                              UN_PREDIO   => UN_PREDIO);        

  IF  UN_FACTURAR IN (-1) THEN 

    MI_RTA:= PCK_PREDIAL_COM5.FC_IMPRIMIRFACTABONOENACUERDO(UN_COMPANIA     => UN_COMPANIA,
                                                            UN_NUMERO_ORDEN => UN_NUMERO_ORDEN,
                                                            UN_ACUERDO      => UN_ACUERDO,
                                                            UN_PREDIO       => UN_PREDIO,            
                                                            UN_CUOTA        => UN_CUOTA,
                                                            UN_STRTIPO      => UN_STRTIPO,
                                                            UN_ANULACION    => UN_ANULACION,
                                                            UN_USUARIO      => UN_USUARIO);
  END IF;

END PR_FACTURARABONOENACUERDO;

--6

PROCEDURE PR_PLANO_MOROSO 
 /*
    NAME              : PR_PLANO_MOROSO En access --> Plano_Morosos 
    AUTHORS           : STEFANINI SYSMAN   
    AUTHOR MIGRACION  : Jonathan Enrique Guerrero Torres
    DATE MIGRADOR     : 23/02/2017
    TIME              : 12:40
    SOURCE MODULE     : IMPUESTO PREDIAL
    DESCRIPTION       : Procedimiento encargado de calcular los usuarios morosos e insertarlo en IP_TEMP_DEUDORES 
    PARAMETERS        : UN_COMPANIA        => Compañia de ingreso a la aplicación.
                        UN_NUMERO_ORDEN    => 
                        UN_VALOR           => El valor de referencia para calcular los Usuarios Morosos
                        UN_FECHA_CORTE     => Fecha proveniente del formulario para calular los morosos.
                        UN_SIN_CEDULA      => Parametro que proviene del formulario y sirve para calcular los morosos sin cedula o con cedula
                        UN_NOMBRE_COMPANIA => Nombre de la compania Ingreso

    @NAME:  planoMoroso
    @METHOD:  POST   
    */

( UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VALOR                        IN PCK_SUBTIPOS.TI_DOBLE,
  UN_FECHA_CORTE                  IN DATE,
  UN_SIN_CEDULA                   IN PCK_SUBTIPOS.TI_LOGICO,
  UN_NOMBRE_COMPANIA              IN VARCHAR2)

AS   
  MI_CAMPOS                          PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                         PCK_SUBTIPOS.TI_VALORES;  
  MI_RTA                             PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CONDICION                       PCK_SUBTIPOS.TI_CONDICION; 
  MI_MSGERROR                        PCK_SUBTIPOS.TI_CLAVEVALOR;

  MI_CONSULTA_RSC                    VARCHAR2(10000 CHAR);
  MI_RSC                             SYS_REFCURSOR; 
  MI_RS_DISCRIMIADO                  SYS_REFCURSOR; 
  MI_PRIMERO_DE_PREANO_1_RSC         IP_FACTURADOS.PREANO%TYPE;
  MI_FECHA_RSC                       DATE;
  MI_CODIGO_RSC                      IP_USUARIOS_PREDIAL.CODIGO%TYPE; 
  MI_NIT_RSC                         IP_USUARIOS_PREDIAL.NIT%TYPE;           
  MI_NOMBRE_RSC                      IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
  MI_DIGITO_RSC                      IP_USUARIOS_PREDIAL.DIGITO%TYPE;
  MI_TOTAL_DEUDA_RSC                 IP_FACTURADOS.C2%TYPE; 
  MI_EN_PROCESO_RSC                  IP_TEMP_DEUDORES.ENPROCESO%TYPE;
  MI_TIPO_NIT_RSC                    IP_USUARIOS_PREDIAL.TIPO_NIT%TYPE;  

  MI_PREDIO                          IP_USUARIOS_PREDIAL.CODIGO%TYPE;
  MI_DEUDA                           PCK_SUBTIPOS.TI_DOBLE;

  MI_NIT_RSD                         IP_USUARIOS_PREDIAL.NIT%TYPE;           
  MI_NOMBRE_RSD                      IP_USUARIOS_PREDIAL.NOMBRE%TYPE;
  MI_NUMERO_OBLIGACION_RSD           PCK_SUBTIPOS.TI_ENTERO;
  MI_DIGITO_RSD                      IP_USUARIOS_PREDIAL.DIGITO%TYPE;   
  MI_JURIDICA_RSD                    IP_TEMP_DEUDORES.JURIDICA%TYPE;
  MI_DEUDA_PREDIAL_RSD               PCK_SUBTIPOS.TI_DOBLE;
  MI_TIPO_DEUDOR_RSD                 IP_TEMP_DEUDORES.TIPO_DEUDOR%TYPE;   
  MI_TIPO_IDENTIFICACION_RSD         IP_TEMP_DEUDORES.TIPO_IDENTIFICACION%TYPE;
  MI_FECHA_VEN_OBLIGA_RSD            IP_TEMP_DEUDORES.FECHA_VENCIMIENTO_OBLIGACION%TYPE;
  MI_EN_PROCESO_RSD                  IP_TEMP_DEUDORES.ENPROCESO%TYPE;
  MI_CON_ACUERDO_RSD                 IP_TEMP_DEUDORES.CON_ACUERDO%TYPE; 
  MI_NUMERO_ORDEN                    IP_TEMP_DEUDORES.NUMERO_ORDEN%TYPE; 
  MI_PREDIO_RSD                      IP_USUARIOS_PREDIAL.CODIGO%TYPE;  
  MI_TERMINO_EXTINCION_OBLIGA_RS     VARCHAR2(100 CHAR);

  MI_PARAMETRO_PREDIAL               BOOLEAN;

  MI_CONSECUTIVO                     PCK_SUBTIPOS.TI_ENTERO;
  MI_CANTIDAD_MESES                  PCK_SUBTIPOS.TI_ENTERO;

  TYPE  MI_REG_DEUDORES IS TABLE OF IP_TEMP_DEUDORES%ROWTYPE;
  MI_VEC_DEUDORES MI_REG_DEUDORES;

BEGIN


  MI_CONDICION  :=   'COMPANIA   = '''||UN_COMPANIA||'''';     

  PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_TEMP_DEUDORES',
                                       UN_ACCION    => 'E',
                                       UN_CONDICION => MI_CONDICION);


  IF UN_NOMBRE_COMPANIA = 'Alcaldía de Madrid' THEN 

    MI_CONSULTA_RSC := ' SELECT 
                                DISTINCT 
                                MIN(FACTURADOS.PREANO)  AS PRIMERODEPREANO1,
                                MIN(USUARIOS_PREDIAL.PAG_FEC)                                        AS FECHA,
                                MIN(USUARIOS_PREDIAL.CODIGO)                                         AS CODIGO,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.DIGITO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                SUM(FACTURADOS.C1+FACTURADOS.C2+FACTURADOS.C3+FACTURADOS.C4
                                  +FACTURADOS.C13+FACTURADOS.C14+FACTURADOS.C15+FACTURADOS.C16+
                                  FACTURADOS.C17+FACTURADOS.C18+FACTURADOS.C19+FACTURADOS.C20)       AS TOTALDEUDA
                           FROM 
                                IP_FACTURADOS FACTURADOS
                          INNER JOIN IP_USUARIOS_PREDIAL USUARIOS_PREDIAL
                             ON FACTURADOS.COMPANIA     = USUARIOS_PREDIAL.COMPANIA
                            AND FACTURADOS.CODIGO       = USUARIOS_PREDIAL.CODIGO
                            AND FACTURADOS.NUMERO_ORDEN = USUARIOS_PREDIAL.NUMERO_ORDEN
                          WHERE 
                                FACTURADOS.COMPANIA                 ='''||UN_COMPANIA||'''
                            AND USUARIOS_PREDIAL.INDBORRADO         NOT IN (-1)
                            AND USUARIOS_PREDIAL.CODIGO_NO_ACTIVO   NOT IN (-1)
                            AND FACTURADOS.NOCOBRADO                NOT IN (-1)
                            AND USUARIOS_PREDIAL.INDEXE             IN (0)
                            AND FACTURADOS.PREANO                   < EXTRACT( YEAR FROM SYSDATE)
                          GROUP BY 
                                USUARIOS_PREDIAL.CODIGO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                USUARIOS_PREDIAL.PAG_FEC,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.AREA_CONSTRUIDA,
                                USUARIOS_PREDIAL.DIRECCION,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.DIGITO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                FACTURADOS.PAGADO,
                                USUARIOS_PREDIAL.INDEXE,
                                FACTURADOS.NOCOBRADO
                         HAVING MIN(FACTURADOS.PREANO)         < EXTRACT (YEAR FROM SYSDATE)';
  ELSE 
    IF SYSDATE > '30/06/'||EXTRACT(YEAR FROM SYSDATE) THEN 
      MI_CONSULTA_RSC :='SELECT 
                                DISTINCT 
                                MIN(FACTURADOS.PREANO)  AS PRIMERODEPREANO1,
                                MIN(USUARIOS_PREDIAL.PAG_FEC)                                        AS FECHA,
                                MIN(USUARIOS_PREDIAL.CODIGO)                                         AS CODIGO,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.DIGITO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                SUM(FACTURADOS.C1+FACTURADOS.C2+FACTURADOS.C3+FACTURADOS.C4
                                  +FACTURADOS.C13+FACTURADOS.C14+FACTURADOS.C15+FACTURADOS.C16+
                                  FACTURADOS.C17+FACTURADOS.C18+FACTURADOS.C19+FACTURADOS.C20)       AS TOTALDEUDA

                          FROM 
                               IP_FACTURADOS FACTURADOS
                         INNER JOIN IP_USUARIOS_PREDIAL USUARIOS_PREDIAL
                            ON FACTURADOS.COMPANIA     = USUARIOS_PREDIAL.COMPANIA
                           AND FACTURADOS.NUMERO_ORDEN = USUARIOS_PREDIAL.NUMERO_ORDEN
                           AND FACTURADOS.CODIGO       = USUARIOS_PREDIAL.CODIGO
                         WHERE 
                               USUARIOS_PREDIAL.COMPANIA            = '''||UN_COMPANIA||'''
                           AND USUARIOS_PREDIAL.INDBORRADO          IN (0)
                           AND USUARIOS_PREDIAL.CODIGO_NO_ACTIVO    IN (0)
                           AND FACTURADOS.INDPAGO_ACPAG             IN (0)
                           AND FACTURADOS.PAGADO                    IN (0)
                           AND USUARIOS_PREDIAL.INDEXE              IN (0)
                           AND FACTURADOS.NOCOBRADO                 IN (0)
                           AND FACTURADOS.PREANO                    <= EXTRACT (YEAR FROM SYSDATE)
                         GROUP BY
                               USUARIOS_PREDIAL.NOMBRE,
                               USUARIOS_PREDIAL.TIPO_NIT,
                               USUARIOS_PREDIAL.NIT,
                               USUARIOS_PREDIAL.DIGITO,
                               USUARIOS_PREDIAL.NUMERO_ORDEN,
                               CASE WHEN PROCESO_DE_COBRO NOT IN (0) OR USUARIOS_PREDIAL.IND_PROCESOJUD NOT IN (0) THEN 4 ELSE 1 END,
                               FACTURADOS.PAGADO,
                               USUARIOS_PREDIAL.INDEXE,
                               FACTURADOS.NOCOBRADO';
    ELSE   
      MI_CONSULTA_RSC := 'SELECT * FROM (SELECT 
                                                DISTINCT
                                                MIN(FACTURADOS.PREANO)                                                        AS PRIMERODEPREANO1,
                                                TO_DATE(TO_CHAR(MIN(USUARIOS_PREDIAL.PAG_FEC),''DD/MM/YYYY''),''DD/MM/YYYY'') AS FECHA,
                                                MIN(USUARIOS_PREDIAL.CODIGO)                                                  AS CODIGO,
                                                USUARIOS_PREDIAL.NIT,
                                                USUARIOS_PREDIAL.TIPO_NIT,
                                                USUARIOS_PREDIAL.NOMBRE,
                                                USUARIOS_PREDIAL.DIGITO,
                                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                                SUM(FACTURADOS.C1+FACTURADOS.C2+FACTURADOS.C3+FACTURADOS.C4
                                                 +FACTURADOS.C13+FACTURADOS.C14+FACTURADOS.C15+FACTURADOS.C16+
                                                 FACTURADOS.C17+FACTURADOS.C18+FACTURADOS.C19+FACTURADOS.C20)   AS TOTALDEUDA

                                           FROM 
                                                IP_FACTURADOS FACTURADOS
                                          INNER JOIN IP_USUARIOS_PREDIAL USUARIOS_PREDIAL
                                             ON FACTURADOS.COMPANIA     = USUARIOS_PREDIAL.COMPANIA 
                                            AND FACTURADOS.NUMERO_ORDEN = USUARIOS_PREDIAL.NUMERO_ORDEN 
                                            AND  FACTURADOS.CODIGO     = USUARIOS_PREDIAL.CODIGO
                                          WHERE 
                                                USUARIOS_PREDIAL.COMPANIA          ='''||UN_COMPANIA||'''
                                            AND USUARIOS_PREDIAL.INDBORRADO        IN (0)
                                            AND USUARIOS_PREDIAL.CODIGO_NO_ACTIVO  IN (0)
                                            AND FACTURADOS.INDPAGO_ACPAG           IN (0)
                                            AND FACTURADOS.PREANO                  <= EXTRACT (YEAR FROM SYSDATE)
                                            AND FACTURADOS.PAGADO                  IN (0)
                                            AND USUARIOS_PREDIAL.INDEXE            IN (0)
                                            AND FACTURADOS.NOCOBRADO               IN (0)
                                          GROUP BY 
                                                USUARIOS_PREDIAL.NOMBRE,
                                                USUARIOS_PREDIAL.TIPO_NIT,
                                                USUARIOS_PREDIAL.NIT,
                                                USUARIOS_PREDIAL.DIGITO,
                                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                                CASE WHEN PROCESO_DE_COBRO NOT IN (0) OR USUARIOS_PREDIAL.IND_PROCESOJUD NOT IN (0) THEN 4 ELSE  1 END ,
                                                FACTURADOS.PAGADO,
                                                USUARIOS_PREDIAL.INDEXE,
                                                FACTURADOS.NOCOBRADO ) 

                          WHERE TOTALDEUDA > '||UN_VALOR;
    END IF;
  END IF;

 MI_CONSECUTIVO := 1;


    IF UN_SIN_CEDULA = 0 THEN 
        OPEN MI_RSC FOR MI_CONSULTA_RSC;
        LOOP
            FETCH MI_RSC INTO MI_PRIMERO_DE_PREANO_1_RSC,MI_FECHA_RSC,MI_CODIGO_RSC,MI_NIT_RSC,MI_TIPO_NIT_RSC,MI_NOMBRE_RSC,MI_DIGITO_RSC,MI_NUMERO_ORDEN,MI_TOTAL_DEUDA_RSC;

             EXIT WHEN MI_RSC%NOTFOUND;

            IF MI_PRIMERO_DE_PREANO_1_RSC < EXTRACT (YEAR FROM UN_FECHA_CORTE) 
              OR  ROUND(MONTHS_BETWEEN(TO_DATE(UN_FECHA_CORTE,'DD/MM/YYYY'),NVL(MI_FECHA_RSC,'01/01/'||(EXTRACT (YEAR FROM SYSDATE)-1)))) >= 6 THEN 

                  MI_PREDIO_RSD            := MI_CODIGO_RSC;
                  MI_NIT_RSD               := MI_NIT_RSC;
                  MI_NOMBRE_RSD            := MI_NOMBRE_RSC;
                  MI_NUMERO_OBLIGACION_RSD := MI_CONSECUTIVO;


                  IF MI_NIT_RSC IS NULL THEN 
                     MI_JURIDICA_RSD := 0;
                  ELSE 
                      IF MI_NIT_RSC BETWEEN '800000000' AND '999999999' THEN 
                          MI_JURIDICA_RSD := -1;
                      ELSE
                          MI_JURIDICA_RSD := 0;
                      END IF;    
                  END IF;

                  MI_DIGITO_RSD := MI_DIGITO_RSC;

                  IF MI_NUMERO_ORDEN = '001' THEN 

                      MI_DEUDA             := MI_TOTAL_DEUDA_RSC;
                      MI_TIPO_DEUDOR_RSD   := '01';
                      MI_PREDIO            := MI_CODIGO_RSC;
                      MI_DEUDA_PREDIAL_RSD := MI_TOTAL_DEUDA_RSC;

                  ELSE   
                     IF MI_CODIGO_RSC =  MI_PREDIO THEN 
                        MI_DEUDA_PREDIAL_RSD := MI_DEUDA;
                     ELSE 
                        MI_DEUDA_PREDIAL_RSD := MI_TOTAL_DEUDA_RSC;
                     END IF;
                     MI_TIPO_DEUDOR_RSD := '02';

                  END IF;

                  IF MI_TIPO_NIT_RSC = '' OR MI_TIPO_NIT_RSC IS NULL THEN 
                    MI_TIPO_IDENTIFICACION_RSD := 'C';
                  ELSE 
                    MI_TIPO_IDENTIFICACION_RSD :=MI_TIPO_NIT_RSC;
                  END IF;

                  MI_FECHA_VEN_OBLIGA_RSD := MI_FECHA_RSC;

                  IF MI_EN_PROCESO_RSC = 1 THEN 
                      MI_EN_PROCESO_RSD := 'SIN LEYENDA';
                  ELSE 
                      MI_EN_PROCESO_RSD := 'EN DISCUSIÓN JUDICIAL-DEMANDA';
                  END IF;
                  MI_CON_ACUERDO_RSD := 0;
                  MI_TERMINO_EXTINCION_OBLIGA_RS :=  ROUND(MONTHS_BETWEEN(SYSDATE,TRUNC(MI_FECHA_RSC)));

                  --- SE DEJA LA VALIDACION DE ABAJO PUESTO QUE HAY DATOS INCONCISTENTES  EJEMPLO HAY UNA FECHA DEL AÑO 0014 ENTONCES A LA HORA 
                  -- DE HACER EL CALCULO DEL TERMINO_EXTINCION OBLIGRACION EL RESULTADO DA UNA CIFRA DE 5 DIGITOS LO CUAL IMPIDE EL INSERT 
                  -- PUESTO EN LA BASE DE DATOS EL CAMPO TIENE UN MAXIMO DE 4 CHAR
                  IF MI_TERMINO_EXTINCION_OBLIGA_RS > 9999 THEN 
                    CONTINUE;
                  END IF;

                  BEGIN 
                    BEGIN 
                      MI_CAMPOS  := ' COMPANIA,
                                      PREDIO, 
                                      NUMERO_ORDEN, 
                                      NIT,
                                      NOMBRE,
                                      NUMERO_OBLIGACION,
                                      JURIDICA,
                                      TIPO_IDENTIFICACION,
                                      DIGITO,
                                      TIPO_DEUDOR,
                                      FECHA_VENCIMIENTO_OBLIGACION,
                                      TERMINO_EXTINCION_OBLIGACION,
                                      DEUDAPREDIAL,
                                      CON_ACUERDO,
                                      ENPROCESO';

                       MI_VALORES  := '''' || UN_COMPANIA || ''',
                                      ''' ||MI_CODIGO_RSC || ''',
                                      ''' ||MI_NUMERO_ORDEN|| ''',
                                      ''' ||MI_NIT_RSC||''',
                                      ''' ||REPLACE(MI_NOMBRE_RSC,CHR(39),CHR(39)||CHR(39))||''',
                                      '||MI_CONSECUTIVO||',
                                      '||MI_JURIDICA_RSD||',
                                      '''||MI_TIPO_IDENTIFICACION_RSD||''',
                                      '||MI_DIGITO_RSD||',
                                      '''||MI_TIPO_DEUDOR_RSD||''',
                                      TO_DATE('''||TO_CHAR(MI_FECHA_VEN_OBLIGA_RSD,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                      '''||MI_TERMINO_EXTINCION_OBLIGA_RS||''',
                                      '||MI_DEUDA_PREDIAL_RSD||',
                                      '||MI_CON_ACUERDO_RSD||',
                                      '''||MI_EN_PROCESO_RSD||'''';

                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_TEMP_DEUDORES',
                                                              UN_ACCION=>'I',
                                                              UN_CAMPOS=>MI_CAMPOS,
                                                              UN_VALORES=>MI_VALORES);   
                    MI_MSGERROR(1).CLAVE := 'NOMBRE';
                    MI_MSGERROR(1).VALOR := MI_NOMBRE_RSC;

                    MI_MSGERROR(2).CLAVE := 'NIT';
                    MI_MSGERROR(2).VALOR :=  MI_NIT_RSC;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_DEUDORES,
                                             UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;  

                  MI_CONSECUTIVO := MI_CONSECUTIVO +1;

             END IF;
        END LOOP;
    --ELSE SIN CEDULA    
    ELSE
        OPEN MI_RSC FOR MI_CONSULTA_RSC;
        LOOP

            FETCH MI_RSC INTO MI_PRIMERO_DE_PREANO_1_RSC,MI_FECHA_RSC,MI_CODIGO_RSC,MI_NIT_RSC,MI_TIPO_NIT_RSC,MI_NOMBRE_RSC,MI_DIGITO_RSC,MI_NUMERO_ORDEN,MI_TOTAL_DEUDA_RSC;
            EXIT WHEN MI_RSC%NOTFOUND;

            IF MI_NIT_RSC IS NULL OR MI_NIT_RSC = '0' THEN 
             CONTINUE;
            END IF;

            IF MI_NIT_RSC = '3269489' THEN 
              RETURN ;
            END IF;

            IF MI_PRIMERO_DE_PREANO_1_RSC < EXTRACT (YEAR FROM UN_FECHA_CORTE) 
                OR  ROUND(MONTHS_BETWEEN(TO_DATE(UN_FECHA_CORTE,'DD/MM/YYYY'),NVL(MI_FECHA_RSC,'01/01/'||(EXTRACT (YEAR FROM SYSDATE)-1)))) >= 6  THEN 

                MI_PREDIO_RSD            := MI_CODIGO_RSC;
                MI_NIT_RSD               := MI_NIT_RSC;
                MI_NOMBRE_RSD            := MI_NOMBRE_RSC;
                MI_NUMERO_OBLIGACION_RSD := MI_CONSECUTIVO;

                IF MI_NIT_RSC BETWEEN '800000000' AND '999999999' THEN 
                  MI_JURIDICA_RSD := -1;
                ELSE
                  MI_JURIDICA_RSD := 0;
                END IF;

                MI_DIGITO_RSD := MI_DIGITO_RSC;

                IF MI_NUMERO_ORDEN = '001'  THEN 
                    MI_DEUDA               := MI_TOTAL_DEUDA_RSC;
                    MI_TIPO_DEUDOR_RSD     := '01';
                    MI_PREDIO              := MI_CODIGO_RSC;
                    MI_DEUDA_PREDIAL_RSD   := MI_TOTAL_DEUDA_RSC;
                ELSE 
                    IF MI_CODIGO_RSC = MI_PREDIO THEN 
                        MI_DEUDA_PREDIAL_RSD := MI_DEUDA;
                    ELSE
                        MI_DEUDA_PREDIAL_RSD := MI_TOTAL_DEUDA_RSC;
                    END IF;
                    MI_TIPO_DEUDOR_RSD := '02';
                END IF;


                IF MI_TIPO_NIT_RSC = '' OR MI_TIPO_NIT_RSC IS NULL  THEN 

                  MI_TIPO_IDENTIFICACION_RSD := 'C';
                ELSE  
                  MI_TIPO_IDENTIFICACION_RSD := MI_TIPO_NIT_RSC;
                END IF;

                MI_FECHA_VEN_OBLIGA_RSD := MI_FECHA_RSC;

                IF MI_EN_PROCESO_RSC = 1 THEN 
                    MI_EN_PROCESO_RSD :='SIN LEYENDA';
                ELSE
                    MI_EN_PROCESO_RSD :='EN DISCUSIÓN JUDICIAL-DEMANDA';
                END IF;

                MI_CON_ACUERDO_RSD             := 0;
                MI_TERMINO_EXTINCION_OBLIGA_RS :=  ROUND(MONTHS_BETWEEN(SYSDATE,TRUNC(MI_FECHA_RSC)));

               --- SE DEJA LA VALIDACION DE ABAJO PUESTO QUE HAY DATOS INCONCISTENTES  EJEMPLO HAY UNA FECHA DEL AÑO 0014 ENTONCES A LA HORA 
               -- DE HACER EL CALCULO DEL TERMINO_EXTINCION OBLIGRACION EL RESULTADO DA UNA CIFRA DE 5 DIGITOS LO CUAL IMPIDE EL INSERT 
               -- PUESTO EN LA BASE DE DATOS EL CAMPO TIENE UN MAXIMO DE 4 CHAR
               IF MI_TERMINO_EXTINCION_OBLIGA_RS > 9999 THEN 
                 CONTINUE;
               END IF;
               BEGIN 
                  BEGIN
                     MI_CAMPOS  := ' COMPANIA,
                                      PREDIO, 
                                      NUMERO_ORDEN, 
                                      NIT,
                                      NOMBRE,
                                      NUMERO_OBLIGACION,
                                      JURIDICA,
                                      TIPO_IDENTIFICACION,
                                      DIGITO,
                                      TIPO_DEUDOR,
                                      FECHA_VENCIMIENTO_OBLIGACION,
                                      TERMINO_EXTINCION_OBLIGACION,
                                      DEUDAPREDIAL,
                                      CON_ACUERDO,
                                      ENPROCESO';

                       MI_VALORES  := '''' || UN_COMPANIA || ''',
                                      ''' ||MI_CODIGO_RSC || ''',
                                      ''' ||MI_NUMERO_ORDEN|| ''',
                                      ''' ||MI_NIT_RSC||''',
                                      ''' ||REPLACE(MI_NOMBRE_RSC,CHR(39),CHR(39)||CHR(39))||''',
                                      '||MI_CONSECUTIVO||',
                                      '||MI_JURIDICA_RSD||',
                                      '''||MI_TIPO_IDENTIFICACION_RSD||''',
                                      '||MI_DIGITO_RSD||',
                                      '''||MI_TIPO_DEUDOR_RSD||''',
                                      TO_DATE('''||TO_CHAR(MI_FECHA_VEN_OBLIGA_RSD,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                      '''||MI_TERMINO_EXTINCION_OBLIGA_RS||''',
                                      '||MI_DEUDA_PREDIAL_RSD||',
                                      '||MI_CON_ACUERDO_RSD||',
                                      '''||MI_EN_PROCESO_RSD||'''';

                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_TEMP_DEUDORES ',
                                                              UN_ACCION=>'I',
                                                              UN_CAMPOS=>MI_CAMPOS,
                                                              UN_VALORES=>MI_VALORES);   

                   MI_MSGERROR(1).CLAVE := 'NOMBRE';
                   MI_MSGERROR(1).VALOR := MI_NOMBRE_RSC;

                   MI_MSGERROR(2).CLAVE := 'NIT';
                   MI_MSGERROR(2).VALOR :=  MI_NIT_RSC;

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

                 END;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_DEUDORES,
                                            UN_REEMPLAZOS => MI_MSGERROR);                                      
             END; 

                MI_CONSECUTIVO := MI_CONSECUTIVO +1;

            END IF;
        END LOOP;
    END IF;
 -- EN IF CEDULA

 -- VALIDACION CON PARAMTRO  

 MI_PARAMETRO_PREDIAL := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA,
                                                          UN_NOMBRE    => 'SUBE DEUDORES A PLANOMOROSOS CON ACUERDO VENCIDO',
                                                          UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                          UN_FECHA_PAR => SYSDATE),'NO')
                                                          ) = 'SI';

  IF MI_PARAMETRO_PREDIAL THEN 
     IF UN_SIN_CEDULA = 0 THEN
         FOR MI_RSC IN ( SELECT 
                                USUARIOS_PREDIAL.CODIGO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                MIN(FACTURADOSACUERDOS.FECHAFACTURADO) AS FECHA,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.DIGITO,
                                SUM(FACTURADOSACUERDOS.TOTAL) AS TOTALDEUDA,
                                CASE WHEN PROCESO_DE_COBRO = -1  OR USUARIOS_PREDIAL.IND_PROCESOJUD = -1 THEN 4 ELSE 1 END AS ENPROCESO
                           FROM 
                                (IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
                          INNER JOIN IP_ACUERDOS ACUERDOS
                             ON FACTURADOSACUERDOS.COMPANIA       = ACUERDOS.COMPANIA
                            AND FACTURADOSACUERDOS.NUMERO_ORDEN   = ACUERDOS.NUMERO_ORDEN
                            AND FACTURADOSACUERDOS.PREDIO         = ACUERDOS.PREDIO
                            AND FACTURADOSACUERDOS.CODIGOACUERDO  = ACUERDOS.CODIGOACUERDO)
                          INNER JOIN IP_USUARIOS_PREDIAL USUARIOS_PREDIAL
                             ON  ACUERDOS.COMPANIA    = USUARIOS_PREDIAL.COMPANIA
                            AND ACUERDOS.NUMERO_ORDEN = USUARIOS_PREDIAL.NUMERO_ORDEN
                            AND  ACUERDOS.PREDIO      = USUARIOS_PREDIAL.CODIGO
                          WHERE 
                                USUARIOS_PREDIAL.COMPANIA         = UN_COMPANIA
                            AND USUARIOS_PREDIAL.INDBORRADO       IN (0)
                            AND USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN (0)
                            AND USUARIOS_PREDIAL.INDEXE           IN (0)
                            AND ACUERDOS.ANULADO                  IN (0)
                            AND ACUERDOS.CANCELADO                IN (0)
                            AND FACTURADOSACUERDOS.PAGADO         IN (0)
                            AND FACTURADOSACUERDOS.FECHAFACTURADO < SYSDATE
                          GROUP BY 
                                USUARIOS_PREDIAL.CODIGO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.DIGITO,
                                CASE WHEN PROCESO_DE_COBRO = -1  OR USUARIOS_PREDIAL.IND_PROCESOJUD = -1 THEN 4 ELSE 1 END)

         LOOP
            MI_CANTIDAD_MESES := ROUND(MONTHS_BETWEEN(TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY'),NVL(TO_CHAR(MI_RSC.FECHA,'DD/MM/YYYY'),'01/01/'||(EXTRACT (YEAR FROM SYSDATE)-1))));

            IF MI_CANTIDAD_MESES  >= 6  THEN 

             MI_PREDIO_RSD             := MI_RSC.CODIGO;   
             MI_NIT_RSD                := MI_RSC.NIT;
             MI_NOMBRE_RSD             := MI_RSC.NOMBRE;
             MI_NUMERO_OBLIGACION_RSD  := MI_CONSECUTIVO;
             MI_CON_ACUERDO_RSD        := -1;

             IF MI_RSC.NIT IS NULL THEN 
                MI_JURIDICA_RSD := 0;
             ELSE 
                IF MI_RSC.NIT BETWEEN '800000000'  AND '999999999' THEN 
                    MI_JURIDICA_RSD := -1;
                ELSE 
                    MI_JURIDICA_RSD := 0;
                END IF;
             END IF;

             MI_DIGITO_RSD := MI_RSC.DIGITO;

             IF MI_RSC.NUMERO_ORDEN = '001' THEN 

                MI_DEUDA             := MI_RSC.TOTALDEUDA;
                MI_TIPO_DEUDOR_RSD   := '01';
                MI_PREDIO            := MI_RSC.CODIGO;
                MI_DEUDA_PREDIAL_RSD := MI_RSC.TOTALDEUDA;
             ELSE 
                IF MI_RSC.CODIGO = MI_PREDIO THEN 
                    MI_DEUDA_PREDIAL_RSD := MI_RSC.TOTALDEUDA;

                ELSE
                    MI_DEUDA_PREDIAL_RSD := MI_RSC.TOTALDEUDA;
                END IF;
                    MI_TIPO_DEUDOR_RSD   := '02';

             END IF;

             IF MI_RSC.TIPO_NIT = '' OR MI_RSC.TIPO_NIT IS NULL THEN 
                MI_TIPO_IDENTIFICACION_RSD := 'C';

             ELSE 
                MI_TIPO_IDENTIFICACION_RSD := MI_RSC.TIPO_NIT;
             END IF;

             MI_FECHA_VEN_OBLIGA_RSD := MI_RSC.FECHA;

             IF MI_RSC.ENPROCESO = 1 THEN 
                MI_EN_PROCESO_RSD := 'SIN LEYENDA';
             ELSE 
                MI_EN_PROCESO_RSD := 'EN DISCUSIÓN JUDICIAL-DEMANDA';
             END IF;
              MI_TERMINO_EXTINCION_OBLIGA_RS :=  ROUND(MONTHS_BETWEEN(SYSDATE,TRUNC(MI_FECHA_RSC)));

              --- SE DEJA LA VALIDACION DE ABAJO PUESTO QUE HAY DATOS INCONCISTENTES  EJEMPLO HAY UNA FECHA DEL AÑO 0014 ENTONCES A LA HORA 
               -- DE HACER EL CALCULO DEL TERMINO_EXTINCION OBLIGRACION EL RESULTADO DA UNA CIFRA DE 5 DIGITOS LO CUAL IMPIDE EL INSERT 
               -- PUESTO EN LA BASE DE DATOS EL CAMPO TIENE UN MAXIMO DE 4 CHAR
               IF MI_TERMINO_EXTINCION_OBLIGA_RS > 9999 THEN 
                 CONTINUE;
               END IF;
                  BEGIN 
                      BEGIN 
                          MI_CAMPOS  := ' COMPANIA,
                                          PREDIO, 
                                          NUMERO_ORDEN, 
                                          NIT,
                                          NOMBRE,
                                          NUMERO_OBLIGACION,
                                          JURIDICA,
                                          TIPO_IDENTIFICACION,
                                          DIGITO,
                                          TIPO_DEUDOR,
                                          FECHA_VENCIMIENTO_OBLIGACION,
                                          TERMINO_EXTINCION_OBLIGACION,
                                          DEUDAPREDIAL,
                                          CON_ACUERDO,
                                          ENPROCESO';

                          MI_VALORES  := '''' || UN_COMPANIA || '''' || ',
                                         ''' ||MI_RSC.CODIGO || ''',
                                         ''' ||MI_RSC.NUMERO_ORDEN|| ''',
                                         ''' ||MI_RSC.NIT||''',
                                         ''' ||MI_RSC.NOMBRE||''',
                                         '||MI_CONSECUTIVO||',
                                         '||MI_JURIDICA_RSD||',
                                         '''||MI_TIPO_IDENTIFICACION_RSD||''',
                                         '||MI_DIGITO_RSD||',
                                         '''||MI_TIPO_DEUDOR_RSD||''',
                                         TO_DATE('''||TO_CHAR(MI_FECHA_VEN_OBLIGA_RSD,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                         '''||MI_TERMINO_EXTINCION_OBLIGA_RS||''',
                                         '||MI_DEUDA_PREDIAL_RSD||',
                                         '||MI_CON_ACUERDO_RSD||',
                                         '''||MI_EN_PROCESO_RSD||'''';

                          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_TEMP_DEUDORES ',
                                                                 UN_ACCION=>'I',
                                                                 UN_CAMPOS=>MI_CAMPOS,
                                                                 UN_VALORES=>MI_VALORES); 
                      MI_MSGERROR(1).CLAVE := 'NOMBRE';
                      MI_MSGERROR(1).VALOR := MI_NOMBRE_RSC;

                      MI_MSGERROR(2).CLAVE := 'NIT';
                      MI_MSGERROR(2).VALOR :=  MI_NIT_RSC;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

                  END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_DEUDORES,
                                             UN_REEMPLAZOS => MI_MSGERROR);                                      
                END;                  



             MI_CONSECUTIVO := MI_CONSECUTIVO +1;
            END IF;
         END LOOP;
     -- FIN ELSE CEDULA
     ELSE 
           FOR MI_RSC IN (SELECT 
                                USUARIOS_PREDIAL.CODIGO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                MIN(FACTURADOSACUERDOS.FECHAFACTURADO) AS FECHA,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.DIGITO,
                                SUM(FACTURADOSACUERDOS.TOTAL) AS TOTALDEUDA,
                                CASE WHEN PROCESO_DE_COBRO = -1  OR USUARIOS_PREDIAL.IND_PROCESOJUD = -1 THEN 4 ELSE 1 END AS ENPROCESO
                           FROM 
                                (IP_FACTURADOSACUERDOS FACTURADOSACUERDOS
                          INNER JOIN IP_ACUERDOS ACUERDOS
                             ON FACTURADOSACUERDOS.COMPANIA= ACUERDOS.COMPANIA
                            AND FACTURADOSACUERDOS.NUMERO_ORDEN   = ACUERDOS.NUMERO_ORDEN
                            AND FACTURADOSACUERDOS.PREDIO         = ACUERDOS.PREDIO
                            AND FACTURADOSACUERDOS.CODIGOACUERDO  = ACUERDOS.CODIGOACUERDO)
                          INNER JOIN IP_USUARIOS_PREDIAL USUARIOS_PREDIAL
                             ON  ACUERDOS.COMPANIA    = USUARIOS_PREDIAL.COMPANIA
                            AND ACUERDOS.NUMERO_ORDEN = USUARIOS_PREDIAL.NUMERO_ORDEN
                            AND  ACUERDOS.PREDIO      = USUARIOS_PREDIAL.CODIGO
                          WHERE 
                                USUARIOS_PREDIAL.COMPANIA         = UN_COMPANIA
                            AND USUARIOS_PREDIAL.INDBORRADO       IN (0)
                            AND USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN (0)
                            AND USUARIOS_PREDIAL.INDEXE           IN (0)
                            AND ACUERDOS.ANULADO                  IN (0)
                            AND ACUERDOS.CANCELADO                IN (0)
                            AND FACTURADOSACUERDOS.PAGADO         IN (0)
                            AND FACTURADOSACUERDOS.FECHAFACTURADO < SYSDATE
                          GROUP BY 
                                USUARIOS_PREDIAL.CODIGO,
                                USUARIOS_PREDIAL.NUMERO_ORDEN,
                                USUARIOS_PREDIAL.NOMBRE,
                                USUARIOS_PREDIAL.TIPO_NIT,
                                USUARIOS_PREDIAL.NIT,
                                USUARIOS_PREDIAL.DIGITO,
                                CASE WHEN PROCESO_DE_COBRO = -1  OR USUARIOS_PREDIAL.IND_PROCESOJUD = -1 THEN 4 ELSE 1 END)

         LOOP
            IF MI_RSC.NIT IS NULL OR MI_RSC.NIT = 0 THEN 
              CONTINUE;
            END IF;

            IF MI_RSC.NIT = '3269489' THEN 
              RETURN;
            END IF;
            MI_CANTIDAD_MESES := ROUND(MONTHS_BETWEEN(TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY'),NVL(TO_CHAR(MI_RSC.FECHA,'DD/MM/YYYY'),'01/01/'||(EXTRACT (YEAR FROM SYSDATE)-1))));

            IF MI_CANTIDAD_MESES  >= 6  THEN 
              MI_PREDIO_RSD            := MI_RSC.CODIGO;
              MI_NIT_RSD               := MI_RSC.NIT;
              MI_NOMBRE_RSD            := MI_RSC.NOMBRE;
              MI_NUMERO_OBLIGACION_RSD := MI_CONSECUTIVO;

              IF MI_RSC.NIT BETWEEN '800000000' AND '999999999' THEN 
                  MI_JURIDICA_RSD  := -1;
              ELSE 
                  MI_JURIDICA_RSD  := 0;
              END IF;

              MI_DIGITO_RSD := MI_RSC.DIGITO;

              IF MI_RSC.NUMERO_ORDEN = '001' THEN 
                  MI_DEUDA             := MI_RSC.TOTALDEUDA;
                  MI_TIPO_DEUDOR_RSD   := '01';
                  MI_PREDIO            := MI_RSC.CODIGO;
                  MI_DEUDA_PREDIAL_RSD := MI_RSC.TOTALDEUDA;

              ELSE 
                IF MI_RSC.CODIGO = MI_PREDIO THEN 
                  MI_DEUDA_PREDIAL_RSD := MI_DEUDA;
                ELSE
                  MI_DEUDA_PREDIAL_RSD := MI_RSC.TOTALDEUDA;
                END IF;
                MI_TIPO_DEUDOR_RSD := '02';  
              END IF;

              IF MI_RSC.TIPO_NIT = '' OR MI_RSC.TIPO_NIT IS NULL THEN 
                  MI_TIPO_IDENTIFICACION_RSD := 'C';
              ELSE 
                  MI_TIPO_IDENTIFICACION_RSD := MI_RSC.TIPO_NIT;
              END IF;

              MI_FECHA_VEN_OBLIGA_RSD := MI_RSC.FECHA;
              MI_CON_ACUERDO_RSD      := -1;

              IF MI_RSC.ENPROCESO = 1 THEN 
                MI_EN_PROCESO_RSD := 'SIN LEYENDA';
              ELSE 
                MI_EN_PROCESO_RSD := 'EN DISCUSIÓN JUDICIAL-DEMANDA';
              END IF;

              MI_TERMINO_EXTINCION_OBLIGA_RS :=  ROUND(MONTHS_BETWEEN(SYSDATE,TRUNC(MI_FECHA_RSC)));
              --- SE DEJA LA VALIDACION DE ABAJO PUESTO QUE HAY DATOS INCONCISTENTES  EJEMPLO HAY UNA FECHA DEL AÑO 0014 ENTONCES A LA HORA 
              -- DE HACER EL CALCULO DEL TERMINO_EXTINCION OBLIGRACION EL RESULTADO ES UNA CIFRA DE 5 DIGITOS LO CUAL IMPIDE EL INSERT 
              -- YA QUE EN LA BASE DE DATOS EL CAMPO TIENE UN MAXIMO DE 4 CHAR
              IF MI_TERMINO_EXTINCION_OBLIGA_RS > 9999 THEN 
                CONTINUE;
              END IF;
                BEGIN 
                  BEGIN 
                      MI_CAMPOS  := ' COMPANIA,
                                      PREDIO, 
                                      NUMERO_ORDEN, 
                                      NIT,
                                      NOMBRE,
                                      NUMERO_OBLIGACION,
                                      JURIDICA,
                                      TIPO_IDENTIFICACION,
                                      DIGITO,
                                      TIPO_DEUDOR,
                                      FECHA_VENCIMIENTO_OBLIGACION,
                                      TERMINO_EXTINCION_OBLIGACION,
                                      DEUDAPREDIAL,
                                      CON_ACUERDO,
                                      ENPROCESO';

                     MI_VALORES  := '''' || UN_COMPANIA || '''' || ',
                                    ''' ||MI_RSC.CODIGO || ''',
                                    ''' ||MI_RSC.NUMERO_ORDEN|| ''',
                                    ''' ||MI_RSC.NIT||''',
                                    ''' ||MI_RSC.NOMBRE||''',
                                    '||MI_RSC.NUMERO_ORDEN||',
                                    '||MI_JURIDICA_RSD||',
                                    '''||MI_TIPO_IDENTIFICACION_RSD||''',
                                    '||MI_DIGITO_RSD||',
                                    '''||MI_TIPO_DEUDOR_RSD||''',
                                    TO_DATE('''||TO_CHAR(MI_FECHA_VEN_OBLIGA_RSD,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                    '''||MI_TERMINO_EXTINCION_OBLIGA_RS||''',
                                    '||MI_DEUDA_PREDIAL_RSD||',
                                    '||MI_CON_ACUERDO_RSD||',
                                    '''||MI_EN_PROCESO_RSD||'''';

                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_TEMP_DEUDORES ',
                                                            UN_ACCION  => 'I',
                                                            UN_CAMPOS  => MI_CAMPOS,
                                                            UN_VALORES => MI_VALORES); 
                   MI_MSGERROR(1).CLAVE := 'NOMBRE';
                   MI_MSGERROR(1).VALOR := MI_NOMBRE_RSC;

                   MI_MSGERROR(2).CLAVE := 'NIT';
                   MI_MSGERROR(2).VALOR :=  MI_NIT_RSC;

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

                 END;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_DEUDORES,
                                            UN_REEMPLAZOS => MI_MSGERROR);                                      
             END;                                         

              MI_CONSECUTIVO := MI_CONSECUTIVO +1;


            END IF;

         END LOOP;
     END IF;
  END IF;


  WITH AGRUPADA AS(SELECT 
                          DEUDORES.TIPO_IDENTIFICACION,
                          DEUDORES.NIT,
                          DEUDORES.NOMBRE,
                          DEUDORES.CON_ACUERDO,
                          SUM(DEUDORES.DEUDAPREDIAL) AS DEUDA_AGRUP,
                          SUM(DEUDORES.DEUDAPREDIAL) AS TDEUDAPREDIAL             
                     FROM IP_TEMP_DEUDORES DEUDORES
                    WHERE COMPANIA = UN_COMPANIA
                   HAVING SUM(DEUDORES.DEUDAPREDIAL) > UN_VALOR
                    GROUP BY DEUDORES.TIPO_IDENTIFICACION,
                          DEUDORES.NIT ,
                          DEUDORES.NOMBRE,
                          DEUDORES.CON_ACUERDO             
                    ORDER BY DEUDORES.TIPO_IDENTIFICACION,
                          DEUDORES.NIT)


  SELECT 
         DISTINCT IPD.* 
    BULK COLLECT INTO MI_VEC_DEUDORES
    FROM 
         IP_TEMP_DEUDORES IPD
   INNER JOIN AGRUPADA AG 
      ON (IPD.TIPO_IDENTIFICACION=AG.TIPO_IDENTIFICACION
     AND IPD.NIT=AG.NIT 
     AND IPD.NOMBRE=AG.NOMBRE 
     AND IPD.CON_ACUERDO=AG.CON_ACUERDO);


  MI_CONDICION  :=   'COMPANIA   = '''||UN_COMPANIA||'''';     

  PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_TEMP_DEUDORES ',
                                       UN_ACCION    => 'E',
                                       UN_CONDICION => MI_CONDICION);

  FORALL I IN 1..MI_VEC_DEUDORES.COUNT SAVE EXCEPTIONS
  INSERT INTO IP_TEMP_DEUDORES VALUES MI_VEC_DEUDORES (I);


END PR_PLANO_MOROSO;

--7
  FUNCTION FC_IMPORTARASOBANCARIA
    /*
      NAME              : FC_IMPORTARASOBANCARIA -- Asobancaria_importar en ImpPredial en Access.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 22,23,27,28,29,30/06/2017, 04,05,06,10,11,12,13/07/2017
      TIME              : 04:14 PM
      SOURCE MODULE     : PREDIAL 
      DESCRIPTION       : Función que permite registrar los pagos de Asobancaria.
                          Ruta: Panel Principal\Impuesto Predial\Procesos Ocasionales\Pagos Asobancaria.
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación.  
                          UN_TAMANIO       => Valor que se obtiene de la división del tamaño total del archivo en 162.
                          UN_CADENA        => Cadena que contiene los datos separados por punto y coma (;) de los pagos de 
                                              Asobancaria que se van a registrar.
                          UN_RANGO         => Cantidad de veces que se deberá recorrer la cadena UN_CADENA, corresponde al número de filas
                                              de la información del archivo seleccionado en el formulario.
                          UN_USUARIO       => Código del usuario que realiza el registro de pagos de Asobancaria.
                          UN_NUMEROORDEN   => Numero de orden del usuario.
                          UN_MODULO        => Código del múdulo de predial.
                          UN_CODIGOBANCO   => Código del banco seleccionado en el formulario.
                          UN_NOMBREARCHIVO => Nombre del archivo seleccionado en el formulario.

      @NAME:  importarAsobancaria
      @METHOD:  GET
      */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_TAMANIO                IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CADENA                 IN CLOB,
    UN_RANGO                  IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO,
    UN_NUMEROORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_MODULO                 IN VARCHAR2,
    UN_CODIGOBANCO            IN VARCHAR2,
    UN_NOMBREARCHIVO          IN VARCHAR2
  )
  RETURN VARCHAR2 AS 
    MI_LINEA                  PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_I                      PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_FACTMULTIPLICACION     PCK_SUBTIPOS.TI_DOBLE;
    MI_MANAPORTE              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VLRAPORTE              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_RTAASOBANTABLA         VARCHAR2(60 CHAR);
    MI_CCPTAPORTE             VARCHAR2(30 CHAR);
    MI_FECHARECAUDO           VARCHAR2(10 CHAR);
    MI_PAQUETEA               VARCHAR2(10 CHAR);
    MI_REGISTROSA             VARCHAR2(10 CHAR);
    MI_VALORA                 VARCHAR2(20 CHAR);  
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_IDASOBANCARIA          VARCHAR(20 CHAR);
    MI_REFERENCIA             IP_ASOBANCARIA_DETALLE.CODIGO%TYPE;
    MI_RECIBOPAGO             IP_ASOBANCARIA_DETALLE.CODIGO%TYPE;
    MI_TOTALRECIBO            IP_ASOBANCARIA_DETALLE.VALOR%TYPE;
    MI_OBSERVACION            IP_ASOBANCARIA_ERROR.DESCRIPCION%TYPE;
    MI_PRECOD                 IP_RECIBOS_DE_PAGO.PRECOD%TYPE;
    MI_DOCNUM                 IP_RECIBOS_DE_PAGO.DOCNUM%TYPE;
    MI_CUENTA                 PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO := 1;
    MI_NUMEROERRORES          PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_NUMEROPAGOS            PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_CONPREDIO              PCK_SUBTIPOS.TI_LOGICO;
    MI_CODPREDIO              PCK_SUBTIPOS.TI_CODPREDIO;
    MI_STRMENSAJE             VARCHAR2(100 CHAR);
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;
    MI_CONDICIONSTR           PCK_SUBTIPOS.TI_CONDICION;
    MI_PREVAL                 IP_RECIBOS_DE_PAGO.PREVAL%TYPE;
    MI_ANULADO                IP_RECIBOS_DE_PAGO.ANULADO%TYPE; 
    MI_PAGO                   IP_RECIBOS_DE_PAGO.PAGO%TYPE;
    MI_ESACUERDO              IP_RECIBOS_DE_PAGO.ESACUERDO%TYPE;
    MI_ACUERDO                IP_RECIBOS_DE_PAGO.ACUERDO%TYPE;
    MI_ESABONO                IP_RECIBOS_DE_PAGO.ESABONO%TYPE;
    MI_ESCUOTA                IP_RECIBOS_DE_PAGO.ESCUOTA%TYPE;
    MI_NCUOTA                 IP_RECIBOS_DE_PAGO.NCUOTA%TYPE;
    MI_PREANIO                IP_RECIBOS_DE_PAGO.PREANO%TYPE;
    MI_TIPOABONOACUERDO       IP_RECIBOS_DE_PAGO.TIPOABONOAACUERDO%TYPE;
    MI_UNICOANIO              IP_RECIBOS_DE_PAGO.UNICO_ANO%TYPE;
    MI_C1                     IP_RECIBOS_DE_PAGO.C1%TYPE;
    MI_C2                     IP_RECIBOS_DE_PAGO.C2%TYPE;
    MI_C3                     IP_RECIBOS_DE_PAGO.C3%TYPE;
    MI_C4                     IP_RECIBOS_DE_PAGO.C4%TYPE;
    MI_C5                     IP_RECIBOS_DE_PAGO.C5%TYPE;
    MI_C6                     IP_RECIBOS_DE_PAGO.C6%TYPE;
    MI_C7                     IP_RECIBOS_DE_PAGO.C7%TYPE;
    MI_C8                     IP_RECIBOS_DE_PAGO.C8%TYPE;
    MI_C9                     IP_RECIBOS_DE_PAGO.C9%TYPE;
    MI_C10                    IP_RECIBOS_DE_PAGO.C10%TYPE;
    MI_C11                    IP_RECIBOS_DE_PAGO.C11%TYPE;
    MI_C12                    IP_RECIBOS_DE_PAGO.C12%TYPE;
    MI_C13                    IP_RECIBOS_DE_PAGO.C13%TYPE;
    MI_C14                    IP_RECIBOS_DE_PAGO.C14%TYPE;
    MI_C15                    IP_RECIBOS_DE_PAGO.C15%TYPE;
    MI_C16                    IP_RECIBOS_DE_PAGO.C16%TYPE;
    MI_C17                    IP_RECIBOS_DE_PAGO.C17%TYPE;
    MI_C18                    IP_RECIBOS_DE_PAGO.C18%TYPE;
    MI_C19                    IP_RECIBOS_DE_PAGO.C19%TYPE;
    MI_C20                    IP_RECIBOS_DE_PAGO.C20%TYPE;
    MI_VALORAPORTE            IP_RECIBOS_DE_PAGO.VALOR_APORTE%TYPE;
    MI_CODIGO                 IP_USUARIOS_PREDIAL.CODIGO%TYPE;
    MI_COMPANIA               PCK_SUBTIPOS.TI_COMPANIA;
    MI_PREFEC                 IP_PAGO_BANCOSCAB.PREFEC%TYPE;
    MI_PAQUETE                IP_PAGO_BANCOSCAB.PAQUETE%TYPE;
    MI_PAGBAN                 IP_PAGO_BANCOSCAB.PAG_BAN%TYPE;
    MI_NROCUPONES             IP_PAGO_BANCOSCAB.NROCUPONES%TYPE;
    MI_VLRREPORTADO           IP_PAGO_BANCOSCAB.VLRREPORTADO%TYPE;
    MI_NROCUPONESACU          IP_PAGO_BANCOSCAB.NROCUPONESACU%TYPE;
    MI_ACUMULADO              IP_PAGO_BANCOSCAB.ACUMULADO%TYPE;
    MI_FECHABANCARIALOG       IP_ASOBANCARIA_LOG.FECHA%TYPE;
    MI_HORABANLOG             IP_ASOBANCARIA_LOG.HORA%TYPE;
    MI_CANT                   PCK_SUBTIPOS.TI_ENTERO;
    MI_ACUM                   PCK_SUBTIPOS.TI_ENTERO;
    MI_T_SPLIT		            PCK_SYSMAN_UTL.T_SPLIT; 
    MI_RTASPLIT               PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROREXC            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    MI_MANAPORTE := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'MANEJA APORTE VOLUNTARIO',
                                              UN_MODULO    => UN_MODULO,
                                              UN_FECHA_PAR => SYSDATE), 'NO');

    MI_VLRAPORTE := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'CONCEPTO PARA APORTE VOLUNTARIO',
                                              UN_MODULO    => UN_MODULO,
                                              UN_FECHA_PAR => SYSDATE), 'NA');

    IF MI_VLRAPORTE = 'NA' 
    THEN
      MI_CCPTAPORTE := '0';
    ELSE
      IF MI_VLRAPORTE > 13
      AND MI_VLRAPORTE <= 20
      AND MI_MANAPORTE = 'SI'
      THEN
        MI_CCPTAPORTE := MI_VLRAPORTE;
      ELSE
        MI_CCPTAPORTE := '0'; 
        MI_MANAPORTE := 'NO';
      END IF;
    END IF;

    MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''';

    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_ASOBANCARIA_ENC_ARC',
                                    UN_ACCION    => 'E', 
                                    UN_CONDICION => MI_CONDICION);

    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_ASOBANCARIA_ENC_LOT',
                                    UN_ACCION    => 'E', 
                                    UN_CONDICION => MI_CONDICION);                                

    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_ASOBANCARIA_DETALLE',
                                    UN_ACCION    => 'E', 
                                    UN_CONDICION => MI_CONDICION);

    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_ASOBANCARIA_CON_LOT',
                                    UN_ACCION    => 'E', 
                                    UN_CONDICION => MI_CONDICION);

    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_ASOBANCARIA_CON_ARC',
                                    UN_ACCION    => 'E', 
                                    UN_CONDICION => MI_CONDICION);

    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || UN_CADENA || '', 
                                              UN_DELIMITADOR  => ';');
    <<EVALUA_CONTENIDOLINEA>>
    FOR MI_I IN 1..UN_RANGO LOOP
      IF UN_TAMANIO = LENGTH(TO_CHAR(MI_T_SPLIT(MI_I)))
      THEN
        <<DEFINEFACTMULT>>
        WHILE MI_LINEA < UN_TAMANIO LOOP
          IF MI_LINEA = 0 
          THEN
            MI_FACTMULTIPLICACION := MI_LINEA;
          ELSE
            MI_FACTMULTIPLICACION := 162 * MI_LINEA;
          END IF;

          MI_RTAASOBANTABLA := FC_ESTABLECERTABLAINSERASOBANC(UN_COMPANIA           => UN_COMPANIA,
                                                              UN_USUARIO            => UN_USUARIO,
                                                              UN_FACTMULTIPLICACION => MI_FACTMULTIPLICACION,
                                                              UN_LINEA              => MI_LINEA,
                                                              UN_LINEA_ARCHIVO      => TRIM(TO_CHAR(MI_T_SPLIT(MI_I))));
        END LOOP DEFINEFACTMULT;
      ELSE
        MI_FACTMULTIPLICACION := 0;

        MI_RTAASOBANTABLA := FC_ESTABLECERTABLAINSERASOBANC(UN_COMPANIA           => UN_COMPANIA,
                                                            UN_USUARIO            => UN_USUARIO,
                                                            UN_FACTMULTIPLICACION => MI_FACTMULTIPLICACION,
                                                            UN_LINEA              => MI_LINEA,
                                                            UN_LINEA_ARCHIVO      => TRIM(TO_CHAR(MI_T_SPLIT(MI_I))));
        MI_LINEA := MI_LINEA + 1;
      END IF;
      MI_RTASPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || MI_RTAASOBANTABLA || '', 
                                                 UN_DELIMITADOR  => ';');
      IF MI_RTASPLIT(1) = '01'
      THEN
        MI_FECHARECAUDO := MI_RTASPLIT(2);
      ELSIF MI_RTASPLIT(1) = '08'
      THEN
        MI_PAQUETEA   := MI_RTASPLIT(2);
        MI_REGISTROSA := MI_RTASPLIT(3);
        MI_VALORA     := MI_RTASPLIT(4); 
      END IF;
    END LOOP EVALUA_CONTENIDOLINEA;

    --****** fnroAsobancaria
    MI_IDASOBANCARIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'CONSECUTIVO ASOBANCARIA',
                                              UN_MODULO    => UN_MODULO,
                                              UN_FECHA_PAR => SYSDATE);
    BEGIN
      IF MI_IDASOBANCARIA IS NULL
      THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           MI_MSGERROREXC(1).CLAVE := 'PARAMETRO'; 
           MI_MSGERROREXC(1).VALOR := 'CONSECUTIVO ASOBANCARIA'; 
           PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_CONFPARCONSASOBAN,
             UN_REEMPLAZOS => MI_MSGERROREXC 
           );                      
    END;

    BEGIN
      BEGIN
        MI_RTAACME :=  PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => ' VALOR = ' || MI_IDASOBANCARIA || ' + 1',
                                          UN_CONDICION => ' COMPANIA = ''' || UN_COMPANIA ||
                                                        ''' AND NOMBRE = ''CONSECUTIVO ASOBANCARIA'''); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         MI_MSGERROREXC(1).CLAVE := 'PARAMETRO'; 
         MI_MSGERROREXC(1).VALOR := 'CONSECUTIVO ASOBANCARIA'; 
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTPARCONSASOBAN,
           UN_REEMPLAZOS => MI_MSGERROREXC,
           UN_TABLAERROR => 'PARAMETRO'
         );                      
    END;
    --****** fnroAsobancaria

    BEGIN 
      SELECT COUNT(*) CUENTA     
        INTO MI_CUENTA
        FROM IP_ASOBANCARIA_ENC_ARC     
       WHERE COMPANIA = UN_COMPANIA;

      IF MI_CUENTA = 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VERIFICAR_ENC_ARC,
           UN_TABLAERROR => 'IP_ASOBANCARIA_ENC_ARC'
         );
    END; 
    BEGIN 
      SELECT COUNT(*) CUENTA     
        INTO MI_CUENTA
        FROM IP_ASOBANCARIA_DETALLE     
       WHERE COMPANIA = UN_COMPANIA;

      IF MI_CUENTA = 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VERIFICAR_DETALLE,
           UN_TABLAERROR => 'IP_ASOBANCARIA_DETALLE'
         );
    END; 
    BEGIN 
      SELECT COUNT(*) CUENTA     
        INTO MI_CUENTA
        FROM IP_ASOBANCARIA_CON_ARC     
       WHERE COMPANIA = UN_COMPANIA;

      IF MI_CUENTA = 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VERIFICAR_CON_ARC,
           UN_TABLAERROR => 'IP_ASOBANCARIA_CON_ARC'
         );
    END; 
    BEGIN  
      SELECT COUNT(*) CUENTA     
        INTO MI_CUENTA
        FROM IP_ASOBANCARIA_CON_LOT     
       WHERE COMPANIA = UN_COMPANIA;

      IF MI_CUENTA = 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_VERIFICAR_CON_LOT,
           UN_TABLAERROR => 'IP_ASOBANCARIA_CON_LOT'
         );
    END;  

    -- String strLisAux
    <<DATOS_ASOBANDET>>
    FOR RS_DATOS IN (SELECT COMPANIA,              
                            ID,                    
                            TIPO_REGISTRO,         
                            CODIGO,                
                            VALOR,                 
                            PROCEDENCIA,           
                            MEDIOS_PAGO,           
                            NUMERO_OPERACION,      
                            NUMERO_AUTORIZACION,   
                            CODIGO_ENTIDAD,        
                            CODIGO_SUCURSAL,       
                            SECUENCIA,             
                            CAUSAL                 
                       FROM IP_ASOBANCARIA_DETALLE 
                      WHERE COMPANIA = UN_COMPANIA)
    LOOP
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'MANEJA CODIGO DE BARRAS MULTIIMPUESTO',
                                   UN_MODULO    => UN_MODULO,
                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI'
      THEN
        MI_REFERENCIA  := SUBSTR(RS_DATOS.CODIGO, 25, 24);
        MI_RECIBOPAGO  := SUBSTR(RS_DATOS.CODIGO, 40, 9);
        MI_TOTALRECIBO := SUBSTR(RS_DATOS.VALOR, 1, 12);

        IF SUBSTR(MI_REFERENCIA, 1, 5) = '60000'
        THEN
          MI_OBSERVACION := 'El recibo de pago No. ' || MI_REFERENCIA || ' no corresponde a un pago de impuesto predial';

          MI_CAMPOS  := 'COMPANIA, 
                         CODIGO, 
                         FACTURA, 
                         FECHA, 
                         DESCRIPCION, 
                         BANCO, 
                         ID, 
                         CONSECUTIVO, 
                         CREATED_BY, 
                         DATE_CREATED';
          MI_VALORES := '''' || UN_COMPANIA || ''', ''' || 
                        SUBSTR(RS_DATOS.CODIGO, 25, 15) || ''', ''' || 
                        MI_RECIBOPAGO || ''', ''' || 
                        MI_FECHARECAUDO || ''', ''' || 
                        MI_OBSERVACION || ''', ''' || 
                        UN_CODIGOBANCO || ''', ' || 
                        MI_IDASOBANCARIA || ', ' || 
                        MI_CONSECUTIVO || ', ''' || 
                        UN_USUARIO || ''',    
                        SYSDATE' ;
          BEGIN
            BEGIN
              MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                               UN_ACCION  => 'I',
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
               MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                 UN_REEMPLAZOS => MI_MSGERROREXC,
                 UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
               );                      
          END;
          MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
          MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
        END IF; -- MI REF = 6000
        BEGIN 
          BEGIN
            SELECT PRECOD
              INTO MI_PRECOD
              FROM IP_RECIBOS_DE_PAGO     
             WHERE COMPANIA = UN_COMPANIA    
               AND DOCNUM   = MI_RECIBOPAGO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          IF MI_PRECOD IS NOT NULL
          THEN
            MI_CODPREDIO := MI_PRECOD;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
          MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REVISA_RECIBOPAGO,
             UN_REEMPLAZOS => MI_MSGERROREXC,
             UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
         );
        END; 

        MI_CONPREDIO := 0; 

      ELSE 
        MI_CODPREDIO   := SUBSTR(RS_DATOS.CODIGO, 25, 15);  
        MI_RECIBOPAGO  := SUBSTR(RS_DATOS.CODIGO, 40, 9);
        MI_TOTALRECIBO := SUBSTR(RS_DATOS.VALOR, 1, 12);  
        MI_STRMENSAJE  := FC_VALIDARFACTURAMF(UN_COMPANIA    => UN_COMPANIA, 
                                              UN_NOFACTURA   => MI_RECIBOPAGO,
                                              UN_FECHAPAGO   => MI_FECHARECAUDO,
                                              UN_NUMEROORDEN => UN_NUMEROORDEN,
                                              UN_MODULO      => UN_MODULO,
                                              UN_USUARIO     => UN_USUARIO);

        IF MI_STRMENSAJE <> 'Proceso finalizado'
        THEN
           MI_CAMPOS := 'COMPANIA, 
                         CODIGO, 
                         FACTURA, 
                         FECHA, 
                         DESCRIPCION, 
                         BANCO, 
                         ID, 
                         CONSECUTIVO, 
                         CREATED_BY, 
                         DATE_CREATED';
          MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                        MI_CODPREDIO || ''', ''' ||
                        MI_RECIBOPAGO || ''', ''' || 
                        MI_FECHARECAUDO || ''', ''' || 
                        MI_STRMENSAJE || ''', ''' ||
                        UN_CODIGOBANCO || ''', ' ||
                        MI_IDASOBANCARIA || ', ' ||
                        MI_CONSECUTIVO || ', ''' ||
                        UN_USUARIO || ''',    
                        SYSDATE' ;
          BEGIN
            BEGIN
              MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                               UN_ACCION  => 'I',
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
               MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                 UN_REEMPLAZOS => MI_MSGERROREXC,
                 UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
               );                      
          END;

          MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
          MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
        END IF;
        MI_CONPREDIO := -1; 
      END IF; -- PARÁMETRO MANEJA CODIGO DE BARRAS MULTIIMPUESTO

      --**********---- rsRecibo
      IF MI_CONPREDIO NOT IN (0)
      THEN
        MI_CONDICIONSTR := '   AND PRECOD =  ''' || MI_CODPREDIO || '''';
      ELSE
        MI_CONDICIONSTR := NULL;  
      END IF;
      MI_STRSQL := 'SELECT PREVAL,      
                           ANULADO,      
                           PAGO,       
                           ESACUERDO,      
                           ACUERDO,        
                           ESABONO,       
                           ESCUOTA,       
                           NCUOTA,       
                           TIPOABONOAACUERDO,       
                           PREANO,       
                           UNICO_ANO,      
                           VALOR_APORTE,       
                           C1, C2,  C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20  ,
                           PRECOD, DOCNUM
                      FROM IP_RECIBOS_DE_PAGO        
                     WHERE COMPANIA = ''' || UN_COMPANIA || '''        
                       AND DOCNUM   = ''' || MI_RECIBOPAGO || '''' ||
                       MI_CONDICIONSTR;   
      BEGIN                    
        EXECUTE IMMEDIATE MI_STRSQL INTO
          MI_PREVAL, MI_ANULADO, MI_PAGO, MI_ESACUERDO, MI_ACUERDO, MI_ESABONO, MI_ESCUOTA, MI_NCUOTA, MI_TIPOABONOACUERDO, 
          MI_PREANIO, MI_UNICOANIO, MI_VALORAPORTE, MI_C1, MI_C2, MI_C3, MI_C4, MI_C5, MI_C6, MI_C7, MI_C8, MI_C9, MI_C10, MI_C11, MI_C12,
          MI_C13, MI_C14, MI_C15, MI_C16, MI_C17, MI_C18,  MI_C19,  MI_C20, MI_PRECOD, MI_DOCNUM;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PREVAL := NULL;
      END;

      IF MI_PREVAL IS NOT NULL 
      THEN
        IF MI_TOTALRECIBO = MI_PREVAL
        THEN
          --** rsUsuario
          MI_STRSQL := 'SELECT CODIGO                              
                          FROM IP_USUARIOS_PREDIAL                 
                         WHERE COMPANIA     = ''' || UN_COMPANIA || '''     
                           AND CODIGO       = ''' || MI_CODPREDIO || '''   
                           AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''';
          BEGIN                    
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_CODIGO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CODIGO := NULL;
          END; 

          IF MI_CODIGO IS NOT NULL
          THEN
            IF MI_PAGO NOT IN (0)
            THEN
              MI_OBSERVACION := 'El recibo de pago _No. ' || MI_RECIBOPAGO || ' se encuentra pago.';
              MI_CAMPOS := 'COMPANIA, 
                            CODIGO, 
                            FACTURA, 
                            FECHA, 
                            DESCRIPCION, 
                            BANCO, 
                            ID, 
                            CONSECUTIVO, 
                            CREATED_BY, 
                            DATE_CREATED';         
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            MI_CODPREDIO || ''', ''' ||
                            MI_RECIBOPAGO || ''', ''' || 
                            MI_FECHARECAUDO || ''', ''' ||   
                            MI_OBSERVACION || ''', ''' ||
                            UN_CODIGOBANCO || ''', ' ||
                            MI_IDASOBANCARIA || ', ' ||
                            MI_CONSECUTIVO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                   );                      
              END;
              MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
              MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
            END IF; -- rsRecibo.getCampos().get("ANULADO")

            IF MI_ANULADO NOT IN (0)
            THEN 
              MI_OBSERVACION := 'El recibo de pago _No. ' ||  SUBSTR(RS_DATOS.CODIGO, 40, 9) || ' se encuentra anulado';
              MI_CAMPOS := 'COMPANIA, 
                            CODIGO, 
                            FACTURA, 
                            FECHA, 
                            DESCRIPCION, 
                            BANCO, 
                            ID, 
                            CONSECUTIVO, 
                            CREATED_BY, 
                            DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            MI_CODPREDIO || ''', ''' ||
                            MI_RECIBOPAGO || ''', ''' || 
                            MI_FECHARECAUDO || ''', ''' || 
                            MI_OBSERVACION || ''', ''' ||
                            UN_CODIGOBANCO || ''', ' ||
                            MI_IDASOBANCARIA || ', ' ||
                            MI_CONSECUTIVO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                   );                      
              END;
              MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
              MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
            END IF; -- rsRecibo.getCampos().get("PAGO")

            IF (MI_ESACUERDO NOT IN (0)
            AND MI_ACUERDO IS NULL)
            OR (MI_ESACUERDO = 0
            AND MI_ACUERDO IS NOT NULL)
            THEN
              MI_OBSERVACION := 'La factura es soporte del acuerdo de pago ' ||  MI_ACUERDO ;
              MI_CAMPOS := 'COMPANIA, 
                            CODIGO, 
                            FACTURA, 
                            FECHA, 
                            DESCRIPCION, 
                            BANCO, 
                            ID, 
                            CONSECUTIVO, 
                            CREATED_BY, 
                            DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            MI_CODPREDIO || ''', ''' ||
                            MI_RECIBOPAGO || ''', ''' || 
                            MI_FECHARECAUDO || ''', ''' ||   
                            MI_OBSERVACION || ''', ''' ||
                            UN_CODIGOBANCO || ''', ' ||
                            MI_IDASOBANCARIA || ', ' ||
                            MI_CONSECUTIVO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                   );                      
              END;
              MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
              MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
            ELSIF MI_ESABONO NOT IN (0) -- ELSE IF rsRecibo.getCampos().get("ESABONO")
            THEN
              PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_ABONO(UN_COMPANIA      => UN_COMPANIA,
                                                          UN_RECIBO        => MI_RECIBOPAGO,
                                                          UN_PREDIO        => MI_CODPREDIO,
                                                          UN_BANCO         => UN_CODIGOBANCO,
                                                          UN_FECHA_RECAUDO => MI_FECHARECAUDO,
                                                          UN_NUMERO_ORDEN  => UN_NUMEROORDEN,
                                                          UN_MODULO        => UN_MODULO,
                                                          UN_USER          => UN_USUARIO,
                                                          UN_VALOR_RECIBO  => MI_TOTALRECIBO);
            ELSIF MI_ESCUOTA NOT IN (0) -- ELSE IF rsRecibo.getCampos().get("ESCUOTA")
            THEN
              PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_CUOTA(UN_COMPANIA      => UN_COMPANIA,
                                                          UN_RECIBO        => MI_RECIBOPAGO,
                                                          UN_PREDIO        => MI_CODPREDIO,
                                                          UN_BANCO         => UN_CODIGOBANCO,
                                                          UN_FECHA_RECAUDO => MI_FECHARECAUDO,
                                                          UN_NUMERO_ORDEN  => UN_NUMEROORDEN,
                                                          UN_MODULO        => UN_MODULO,
                                                          UN_USER          => UN_USUARIO,
                                                          UN_VALOR_RECIBO  => MI_TOTALRECIBO,
                                                          UN_NRO_CUOTA     => MI_NCUOTA,
                                                          UN_VIGENCIA      => MI_PREANIO);
            ELSIF MI_ESACUERDO NOT IN (0) -- ELSE IF rsRecibo.getCampos().get(strEsAcuerdo)
            AND NVL(MI_TIPOABONOACUERDO, 0) NOT IN(0)
            THEN
              PCK_PREDIAL_COM1.PR_REGISTRAR_REC_ABONO_ACUERDO(UN_COMPANIA      => UN_COMPANIA,
                                                              UN_RECIBO        => MI_RECIBOPAGO,
                                                              UN_PREDIO        => MI_CODPREDIO,
                                                              UN_BANCO         => UN_CODIGOBANCO,
                                                              UN_FECHA_RECAUDO => MI_FECHARECAUDO,
                                                              UN_NUMERO_ORDEN  => UN_NUMEROORDEN,
                                                              UN_MODULO        => UN_MODULO,
                                                              UN_USER          => UN_USUARIO,
                                                              UN_TOTAL_RECIBO  => MI_TOTALRECIBO,
                                                              UN_PAQUETE       => MI_PAQUETEA );
            ELSIF MI_ESACUERDO NOT IN (0) 
            THEN
              PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_ACUERDO (UN_COMPANIA                => UN_COMPANIA,
                                            UN_RECIBO                  => MI_RECIBOPAGO,
                                            UN_PREDIO                  => MI_CODPREDIO,
                                            UN_BANCO                   => UN_CODIGOBANCO,
                                            UN_FECHA_RECAUDO           => MI_FECHARECAUDO,
                                            UN_NUMERO_ORDEN            => UN_NUMEROORDEN,
                                            UN_MODULO                  => UN_MODULO,
                                            UN_USER                    => UN_USUARIO,
                                            UN_VALOR_RECIBO            => MI_TOTALRECIBO );
            ELSIF MI_UNICOANIO NOT IN (0)-- ELSE IF rsRecibo.getCampos().get("UNICO_ANO")
            THEN
              PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_UNICO_ANO(UN_COMPANIA      => UN_COMPANIA,
                                                              UN_RECIBO        => MI_RECIBOPAGO,
                                                              UN_PREDIO        => MI_CODPREDIO,
                                                              UN_BANCO         => UN_CODIGOBANCO,
                                                              UN_FECHA_RECAUDO => MI_FECHARECAUDO,
                                                              UN_NUMERO_ORDEN  => UN_NUMEROORDEN,
                                                              UN_MODULO        => UN_MODULO,
                                                              UN_VALOR_RECIBO  => MI_TOTALRECIBO,
                                                              UN_VIGENCIA      => MI_PREANIO,
                                                              UN_USUARIO       => UN_USUARIO);
            ELSE -- LÍNEA DE CÓDIGO 717
              PCK_PREDIAL_COM1.PR_REGISTRAR_RECAUDO_VIGENCIAS(UN_COMPANIA       => UN_COMPANIA,
                                                              UN_RECIBO         => MI_RECIBOPAGO,
                                                              UN_PREDIO         => MI_CODPREDIO,
                                                              UN_BANCO          => UN_CODIGOBANCO,
                                                              UN_FECHA_RECAUDO  => MI_FECHARECAUDO,
                                                              UN_NUMERO_ORDEN   => UN_NUMEROORDEN,
                                                              UN_VALOR_RECIBO   => MI_TOTALRECIBO,
                                                              UN_VIGENCIA_FINAL => MI_PREANIO,
                                                              UN_USUARIO        => UN_USUARIO);
            END IF; 

            --*** rsBancosCab
            MI_STRSQL := 'SELECT PREFEC, PAQUETE, PAG_BAN, NROCUPONES, VLRREPORTADO, NROCUPONESACU, ACUMULADO        
                            FROM IP_PAGO_BANCOSCAB         
                           WHERE COMPANIA = ''' || UN_COMPANIA || '''           
                             AND PREFEC  = TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY'')         
                             AND PAQUETE = ''' || MI_PAQUETEA ||
                         ''' AND PAG_BAN = ''' || UN_CODIGOBANCO || '''';
            BEGIN                    
              EXECUTE IMMEDIATE MI_STRSQL INTO
                MI_PREFEC, MI_PAQUETE, MI_PAGBAN, MI_NROCUPONES, MI_VLRREPORTADO, MI_NROCUPONESACU, MI_ACUMULADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
             --asig_prom := cn(1);
             null;
            END;

            IF MI_PREFEC IS NULL -- rsBancosCab == null
            THEN
              MI_CAMPOS  := 'COMPANIA, 
                             PREFEC, 
                             PAQUETE, 
                             PAG_BAN, 
                             NROCUPONES, 
                             VLRREPORTADO, 
                             NROCUPONESACU, 
                             ACUMULADO, 
                             CREATED_BY, 
                             DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ' ||
                            'TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ''' || 
                            MI_PAQUETEA || ''', ''' || 
                            UN_CODIGOBANCO || ''', ' ||
                            MI_REGISTROSA || ', ' ||
                            MI_VALORA || ', ' ||
                            '1, ' || 
                            MI_TOTALRECIBO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ; 
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_PAGO_BANCOSCAB',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'FECHA'; 
                   MI_MSGERROREXC(1).VALOR := MI_FECHARECAUDO; 
                   MI_MSGERROREXC(2).CLAVE := 'PAQUETE'; 
                   MI_MSGERROREXC(2).VALOR := MI_PAQUETEA; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_PAGOBANCOSCAB,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_PAGO_BANCOSCAB'
                   );                      
              END;
            ELSE
              MI_CAMPOS    := ' NROCUPONESACU = ' || MI_NROCUPONESACU ||  ' + 1, 
                                ACUMULADO     = ' || MI_ACUMULADO || ' + ' ||  MI_TOTALRECIBO || ', 
                                MODIFIED_BY   = ''' || UN_USUARIO || ''', ' || 
                              ' DATE_MODIFIED = SYSDATE';

              MI_CONDICION := ' COMPANIA  =   ''' || UN_COMPANIA || 
                          ''' AND PREFEC  = TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss'') ' ||
                            ' AND PAQUETE = ''' || MI_PAQUETEA || 
                          ''' AND PAG_BAN = ''' || UN_CODIGOBANCO || ''''; 
              BEGIN
                BEGIN
                  MI_RTAACME   :=  PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGO_BANCOSCAB',
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICION); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'FECHA'; 
                   MI_MSGERROREXC(1).VALOR := MI_FECHARECAUDO; 
                   MI_MSGERROREXC(2).CLAVE := 'PAQUETE'; 
                   MI_MSGERROREXC(2).VALOR := MI_PAQUETEA; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGOBANCOSCAB,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_PAGO_BANCOSCAB'
                   );                      
              END;
            END IF; -- FIN rsBancosCab == null
            --*** rsBancosCab

            --*** strBancosDet
            MI_STRSQL := 'SELECT COMPANIA                                
                            FROM IP_PAGO_BANCOSDET                       
                           WHERE  COMPANIA     = ''' || UN_COMPANIA || '''        
                             AND  NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''        
                             AND  PREFEC       = TO_DATE(''' || MI_FECHARECAUDO || ''',''DD/MM/YYYY'')      
                             AND  PAQUETE      = ''' || MI_PAQUETEA || '''       
                             AND  PAG_BAN      = ''' || UN_CODIGOBANCO || '''          
                             AND  PRECOD       = ''' || MI_CODPREDIO || '''       
                             AND  NUMFACTURA   = ''' || MI_RECIBOPAGO || '''';
            --*** strBancosDet

            BEGIN                    
              EXECUTE IMMEDIATE MI_STRSQL INTO MI_COMPANIA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_COMPANIA := NULL;
            END;

            IF MI_COMPANIA IS NULL --rsBancosDet == null
            THEN
              MI_CAMPOS := 'COMPANIA, 
                            NUMERO_ORDEN, 
                            PREFEC, 
                            PAQUETE, 
                            PAG_BAN, 
                            PRECOD, 
                            ANO, 
                            PREVAL, 
                            NUMFACTURA, 
                            USUARIO, 
                            C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, 
                            C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, 
                            CREATED_BY, 
                            DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            UN_NUMEROORDEN || ''', ' ||
                            'TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ''' || 
                            MI_PAQUETEA || ''', ''' || 
                            UN_CODIGOBANCO || ''', ''' ||
                            MI_CODPREDIO || ''', ' ||
                            MI_PREANIO || ', ' ||
                            MI_PREVAL || ', ''' || 
                            MI_RECIBOPAGO || 
                            ''', ''ASOBANCARIA'', ' ||
                            MI_C1 || ', ' || MI_C2 || ', ' || MI_C3 || ', ' || MI_C4 || ', ' || MI_C5 || ', ' ||
                            MI_C6 || ', ' || MI_C7 || ', ' || MI_C8 || ', ' || MI_C9 || ', ' || MI_C10 || ', ' ||
                            MI_C11 || ', ' || MI_C12 || ', ' || MI_C13 || ', ' || MI_C14 || ', ' || MI_C15 || ', ' ||
                            MI_C16 || ', ' || MI_C17 || ', ' || MI_C18 || ', ' || MI_C19 || ', ' || MI_C20 || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_PAGO_BANCOSDET',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'FECHA'; 
                   MI_MSGERROREXC(1).VALOR := MI_FECHARECAUDO; 
                   MI_MSGERROREXC(2).CLAVE := 'CODPREDIO'; 
                   MI_MSGERROREXC(2).VALOR := MI_CODPREDIO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_PAGOBANCOSDET,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_PAGO_BANCOSDET'
                   );                      
              END;

              MI_CAMPOS    := 'PAGO = -1, 
                               PREFECPAG = TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''),
                               PAG_BANPAG = ''' || UN_CODIGOBANCO || ''',  
                               PAQUETEPAG = ''' || MI_PAQUETEA || ''', 
                               FECHA_REGRECAUDO = SYSDATE, 
                               USUARIO_REGRECAUDO = ''' || UN_USUARIO || ''', 
                               MODIFIED_BY  = ''' || UN_USUARIO || ''', 
                               DATE_MODIFIED = SYSDATE';
              MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
                            ''' AND DOCNUM = ''' || MI_RECIBOPAGO || 
                            ''' AND PRECOD = ''' || MI_CODPREDIO || '''';         
              BEGIN
                BEGIN
                  MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);  
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   MI_MSGERROREXC(2).CLAVE := 'CODPREDIO'; 
                   MI_MSGERROREXC(2).VALOR := MI_CODPREDIO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_RECIBOSDEPAGO,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
                   );                      
              END;   

              MI_CAMPOS := 'COMPANIA, 
                            NUMERO_ORDEN, 
                            ID, 
                            CODIGO, 
                            FACTURA, 
                            PREVAL, 
                            FECHA, 
                            BANCO, 
                            ID_LOG, 
                            CREATED_BY, 
                            DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            UN_NUMEROORDEN || ''', ' ||
                            MI_IDASOBANCARIA || ', ''' || 
                            MI_CODPREDIO || ''', ''' || 
                            MI_RECIBOPAGO || ''', ' ||
                            MI_TOTALRECIBO || ', ' ||
                            'TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ''' ||
                            UN_CODIGOBANCO || ''', ' || 
                            MI_IDASOBANCARIA || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_PAGOS',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);  
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   MI_MSGERROREXC(2).CLAVE := 'CODPREDIO'; 
                   MI_MSGERROREXC(2).VALOR := MI_CODPREDIO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANPAGOS,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_ASOBANCARIA_PAGOS'
                   );                      
              END;        
              MI_NUMEROPAGOS := MI_NUMEROPAGOS + 1;
            ELSE
              MI_CAMPOS := 'COMPANIA, 
                            CODIGO, 
                            FACTURA, 
                            FECHA, 
                            DESCRIPCION, 
                            BANCO, 
                            ID, 
                            CONSECUTIVO, 
                            CREATED_BY, 
                            DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            MI_CODPREDIO || ''', ''' ||
                            MI_RECIBOPAGO || ''', ''' || 
                            MI_FECHARECAUDO || ''', ''' ||
                            'El pago ya se encuentra registrado.'', ''' || 
                            UN_CODIGOBANCO || ''', ' ||
                            MI_IDASOBANCARIA || ', ' ||
                            MI_CONSECUTIVO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE' ;
              BEGIN
                BEGIN
                  MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                   UN_ACCION  => 'I',
                                                   UN_CAMPOS  => MI_CAMPOS,
                                                   UN_VALORES => MI_VALORES);              
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                   MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                     UN_REEMPLAZOS => MI_MSGERROREXC,
                     UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                   );                      
              END;

              MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
              MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
            END IF; --rsBancosDet == null
          ELSE
            MI_OBSERVACION := 'El predio ' || MI_CODPREDIO || '  no existe en el sistema.';

            MI_CAMPOS  := 'COMPANIA, 
                           CODIGO, 
                           FACTURA, 
                           FECHA, 
                           DESCRIPCION,
                           BANCO, 
                           ID, 
                           CONSECUTIVO, 
                           CREATED_BY, 
                           DATE_CREATED';
            MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                            MI_CODPREDIO || ''', ''' ||
                            MI_RECIBOPAGO || ''', ''' || 
                            MI_FECHARECAUDO || ''', ''' ||   
                            MI_OBSERVACION || ''', ''' || 
                            UN_CODIGOBANCO || ''', ' ||
                            MI_IDASOBANCARIA || ', ' ||
                            MI_CONSECUTIVO || ', ''' ||
                            UN_USUARIO || ''',    
                            SYSDATE';
            BEGIN
              BEGIN
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                 UN_ACCION  => 'I',
                                                 UN_CAMPOS  => MI_CAMPOS,
                                                 UN_VALORES => MI_VALORES);  
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                 MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                   UN_REEMPLAZOS => MI_MSGERROREXC,
                   UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                 );                  
            END;

            MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
            MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
          END IF; -- rsUsuario != null
          --** rsUsuario
        ELSE
          IF MI_MANAPORTE = 'SI' -- "SI".equals(manAporte)
          AND MI_TOTALRECIBO = MI_PREVAL + MI_VALORAPORTE
          THEN
            MI_CAMPOS    := 'PAGO = -1, 
                             INDAPORTE =-1, 
                             C' || MI_CCPTAPORTE || ' = ' || MI_VALORAPORTE ||
                             ', PREVAL = ' || MI_VALORAPORTE || 
                             ', MODIFIED_BY = ''' || UN_USUARIO || '''
                              , DATE_MODIFIED = SYSDATE';
            MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
                            ''' AND DOCNUM = ''' || MI_RECIBOPAGO || '''';
            BEGIN
              BEGIN
                MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                 MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                 MI_MSGERROREXC(2).CLAVE := 'CODPREDIO'; 
                 MI_MSGERROREXC(2).VALOR := MI_CODPREDIO; 
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_RECIBOSDEPAGO,
                   UN_REEMPLAZOS => MI_MSGERROREXC,
                   UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
                 );                      
            END;   
          ELSE
            MI_OBSERVACION := 'El total del recibo No. ' || MI_RECIBOPAGO || ' difiere con el valor almacenado en el sistema.';

            MI_CAMPOS  := 'COMPANIA, 
                           CODIGO, 
                           FACTURA, 
                           FECHA, 
                           DESCRIPCION, 
                           BANCO, 
                           ID, 
                           CONSECUTIVO, 
                           CREATED_BY, 
                           DATE_CREATED'; 
            MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                              MI_CODPREDIO || ''', ''' ||
                              MI_RECIBOPAGO || ''', ''' ||    
                              MI_FECHARECAUDO || ''', ''' ||   
                              MI_OBSERVACION || ''', ''' || 
                              UN_CODIGOBANCO || ''', ' ||
                              MI_IDASOBANCARIA || ', ' ||
                              MI_CONSECUTIVO || ', ''' ||
                              UN_USUARIO || ''',    
                              SYSDATE' ;
            BEGIN
              BEGIN
                MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                                 UN_ACCION  => 'I',
                                                 UN_CAMPOS  => MI_CAMPOS,
                                                 UN_VALORES => MI_VALORES);  
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                 MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
                   UN_REEMPLAZOS => MI_MSGERROREXC,
                   UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
                 );                  
            END;

            MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
            MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
          END IF; -- "SI".equals(manAporte)

        END IF; -- MI_TOTALRECIBO = MI_PREVAL
      ELSE
        MI_OBSERVACION := 'El recibo de pago No. ' || MI_RECIBOPAGO || ' no existe en el sistema para el predio ' || MI_CODPREDIO || '.';

        MI_CAMPOS  := 'COMPANIA, 
                       CODIGO, 
                       FACTURA, 
                       FECHA, 
                       DESCRIPCION, 
                       BANCO, 
                       ID, 
                       CONSECUTIVO, 
                       CREATED_BY, 
                       DATE_CREATED'; 
        MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                          MI_CODPREDIO || ''', ''' ||
                          MI_RECIBOPAGO || ''', ''' || 
                          MI_FECHARECAUDO || ''', ''' ||
                          MI_OBSERVACION || ''', ''' || 
                          UN_CODIGOBANCO || ''', ' ||
                          MI_IDASOBANCARIA || ', ' ||
                          MI_CONSECUTIVO || ', ''' ||
                          UN_USUARIO || ''',    
                          SYSDATE' ;
        BEGIN
          BEGIN
            MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_ERROR',
                                             UN_ACCION  => 'I',
                                             UN_CAMPOS  => MI_CAMPOS,
                                             UN_VALORES => MI_VALORES);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
             MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
             MI_MSGERROREXC(1).VALOR := MI_RECIBOPAGO; 
             PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANERROR,
               UN_REEMPLAZOS => MI_MSGERROREXC,
               UN_TABLAERROR => 'IP_ASOBANCARIA_ERROR'
             );                  
        END;  

        MI_CONSECUTIVO   := MI_CONSECUTIVO + 1;
        MI_NUMEROERRORES := MI_NUMEROERRORES + 1;
      END IF; -- rsRecibo NO ES NULL
    --**********---- rsRecibo
    END LOOP DATOS_ASOBANDET; -- String strLisAux

    MI_FECHABANCARIALOG := TO_CHAR(SYSDATE, 'DD/MM/YYYY');
    MI_HORABANLOG       := TO_CHAR(SYSDATE, 'HH24:MI:SS');

    MI_CAMPOS  := 'COMPANIA, 
                   USUARIO, 
                   FECHA, 
                   HORA, 
                   BANCO, 
                   NUMERO_PAGOS, 
                   NUMERO_ERRORES, 
                   UBICACION_ARCHIVO, 
                   ID, 
                   FECHARECAUDO, 
                   CREATED_BY, 
                   DATE_CREATED'; 
    MI_VALORES := '''' || UN_COMPANIA || ''', ''' ||
                  UN_USUARIO || ''', ''' ||
                  MI_FECHABANCARIALOG || ''', ''' ||
                  MI_HORABANLOG || ''', ''' ||
                  UN_CODIGOBANCO || ''', ' || 
                  MI_NUMEROPAGOS || ', ' ||
                  MI_NUMEROERRORES || ', ''' || 
                  UN_NOMBREARCHIVO || ''', ' ||
                  MI_IDASOBANCARIA || ', ' ||
                  'TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ''' ||
                  UN_USUARIO || ''',    
                  SYSDATE';               
    BEGIN
      BEGIN
        MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_ASOBANCARIA_LOG',
                                         UN_ACCION  => 'I',
                                         UN_CAMPOS  => MI_CAMPOS,
                                         UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
         MI_MSGERROREXC(1).CLAVE := 'NOMARCHIVO'; 
         MI_MSGERROREXC(1).VALOR := UN_NOMBREARCHIVO; 
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_REG_ASOBANCLOG,
           UN_REEMPLAZOS => MI_MSGERROREXC,
           UN_TABLAERROR => 'IP_ASOBANCARIA_LOG'
         );                  
    END;  

    MI_STRSQL := 'SELECT NVL(COUNT(*),0) CANT, NVL(SUM(PREVAL),0) ACUM         
                    FROM IP_PAGO_BANCOSDET               
                   WHERE COMPANIA = ''' || UN_COMPANIA || '''            
                     AND PREFEC = TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss'')             
                     AND PAG_BAN = ''' || UN_CODIGOBANCO || 
                '''  AND PAQUETE = ''' || MI_PAQUETEA || '''';

    BEGIN                    
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CANT, MI_ACUM;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CANT := NULL;
    END;

    IF MI_CANT IS NOT NULL
    THEN
      MI_CAMPOS    := ' NROCUPONESACU  = ' || MI_CANT ||
                      ', NROCUPONES    = ' || MI_CANT ||
                      ', ACUMULADO     = ' || MI_ACUM ||
                      ', VLRREPORTADO  = ' || MI_ACUM || 
                      ', MODIFIED_BY   = ''' || UN_USUARIO || '''
                       , DATE_MODIFIED = SYSDATE';
      MI_CONDICION := ' COMPANIA      = ''' || UN_COMPANIA ||
                      ''' AND PREFEC  = TO_DATE(''' || MI_FECHARECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss'')  
                          AND PAG_BAN = ''' || UN_CODIGOBANCO ||            
                     '''  AND PAQUETE = ''' || MI_PAQUETEA || '''';
      BEGIN
        BEGIN
          MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGO_BANCOSCAB',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           MI_MSGERROREXC(1).CLAVE := 'FECHA'; 
           MI_MSGERROREXC(1).VALOR := MI_FECHARECAUDO; 
           MI_MSGERROREXC(2).CLAVE := 'PAQUETE'; 
           MI_MSGERROREXC(2).VALOR := MI_PAQUETEA; 
           PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACT_PAGOBANCOSCAB,
             UN_REEMPLAZOS => MI_MSGERROREXC,
             UN_TABLAERROR => 'IP_PAGO_BANCOSCAB'
           );                      
      END;
    END IF;

    RETURN 'Proceso ejecutado';
  END FC_IMPORTARASOBANCARIA;

 --8 
  FUNCTION FC_ESTABLECERTABLAINSERASOBANC 
    /*
      NAME              : FC_ESTABLECERTABLAINSERASOBANC -- asobancariatablas en ImpPredial en Access.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 05/07/2017
      TIME              : 10:14 AM
      SOURCE MODULE     : PREDIAL 
      DESCRIPTION       : Función que permite definir esegún el tipo de pago asobancaria la tabla en la cual se debe registrar
                          la información del pago.
                          Ruta: Panel Principal\Impuesto Predial\Procesos Ocasionales\Pagos Asobancaria.
      PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación.  
                          UN_USUARIO            => Código del usuario que realiza el registro de pagos de Asobancaria.
                          UN_FACTMULTIPLICACION => Factor de multiplicación definido para el registro.
                          UN_LINEA              => Número de línea al que correponde el pago a registrar.
                          UN_LINEA_ARCHIVO      => Información de cada línea del archivo a registrar el pago asobancaria.


      @NAME:  establecerTablaAsobancaria
      @METHOD:  GET
      */
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO,
    UN_FACTMULTIPLICACION     IN PCK_SUBTIPOS.TI_DOBLE,
    UN_LINEA                  IN PCK_SUBTIPOS.TI_ENTERO,
    UN_LINEA_ARCHIVO          IN VARCHAR2 
  )
  RETURN VARCHAR2 AS 
    MI_TIPOASOBANCARIA        VARCHAR2(2 CHAR);   
    MI_FECHARECAUDO           VARCHAR2(10 CHAR);
    MI_PAQUETEA               VARCHAR2(10 CHAR);
    MI_REGISTROSA             VARCHAR2(10 CHAR);
    MI_VALORA                 VARCHAR2(20 CHAR);
    MI_LINEAARCHIVO           VARCHAR2(200 CHAR);
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;    
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_RTAACME                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RESPUESTA              VARCHAR2(60 CHAR);
    MI_MSGERROREXC            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_LINEAARCHIVO := REGEXP_REPLACE(UN_LINEA_ARCHIVO, '[[:space:]]');
    MI_TIPOASOBANCARIA := SUBSTR(MI_LINEAARCHIVO, 1 + UN_FACTMULTIPLICACION, 2);

    IF MI_TIPOASOBANCARIA = '01'
    THEN
      MI_CAMPOS  := 'COMPANIA,
                     ID,
                     TIPO_REGISTRO,
                     NIT_EMPRESA,
                     FECHA_RECAUDO,
                     CODIGO_ENTIDAD,
                     NUMERO_CUENTA,
                     FECHA_ARCHIVO,
                     HORA,
                     MODIFICADOR,
                     TIPO_CUENTA, 
                     CREATED_BY, 
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ' || 
                    UN_LINEA || 
                    ', ''01'', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 10) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 13 + UN_FACTMULTIPLICACION, 8) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 21 + UN_FACTMULTIPLICACION, 3) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 24 + UN_FACTMULTIPLICACION, 17) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 41 + UN_FACTMULTIPLICACION, 8) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 49 + UN_FACTMULTIPLICACION, 4) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 53 + UN_FACTMULTIPLICACION, 1) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 54 + UN_FACTMULTIPLICACION, 2) || ''', ''' ||
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      MI_TABLA   := 'IP_ASOBANCARIA_ENC_ARC';   

      MI_FECHARECAUDO := SUBSTR(MI_LINEAARCHIVO, 13 + UN_FACTMULTIPLICACION, 8);
      MI_FECHARECAUDO := SUBSTR(MI_FECHARECAUDO, 7 + UN_FACTMULTIPLICACION, 2) || '/' ||
                         SUBSTR(MI_FECHARECAUDO, 5 + UN_FACTMULTIPLICACION, 2) || '/' ||
                         SUBSTR(MI_FECHARECAUDO, 1 + UN_FACTMULTIPLICACION, 4);
      MI_RESPUESTA    := '01;' || MI_FECHARECAUDO;                    
    ELSIF MI_TIPOASOBANCARIA = '05'
    THEN
      MI_CAMPOS  := 'COMPANIA, 
                     ID, 
                     TIPO_REGISTRO, 
                     EAN, 
                     NUMERO_LOTE,  
                     CREATED_BY,  
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ' ||
                    UN_LINEA ||
                    ', ''05'', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 13) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 18 + UN_FACTMULTIPLICACION, 2) || ''', ''' ||
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      MI_TABLA   := 'IP_ASOBANCARIA_ENC_LOT'; 
      MI_RESPUESTA := MI_TIPOASOBANCARIA;
    ELSIF MI_TIPOASOBANCARIA = '06'
    THEN
      MI_CAMPOS  := 'COMPANIA, 
                     ID, 
                     TIPO_REGISTRO, 
                     CODIGO, 
                     VALOR, 
                     PROCEDENCIA, 
                     MEDIOS_PAGO, 
                     NUMERO_OPERACION, 
                     NUMERO_AUTORIZACION, 
                     CODIGO_ENTIDAD, 
                     CODIGO_SUCURSAL, 
                     SECUENCIA, 
                     CAUSAL, 
                     CREATED_BY, 
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ' ||
                    UN_LINEA ||
                    ', ''06'', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 48) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 51 + UN_FACTMULTIPLICACION, 14) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 65 + UN_FACTMULTIPLICACION, 2) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 67 + UN_FACTMULTIPLICACION, 2) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 69 + UN_FACTMULTIPLICACION, 6) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 75 + UN_FACTMULTIPLICACION, 6) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 81 + UN_FACTMULTIPLICACION, 3) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 84 + UN_FACTMULTIPLICACION, 4) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 88 + UN_FACTMULTIPLICACION, 7) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 95 + UN_FACTMULTIPLICACION, 3) || ''', ''' ||
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      MI_TABLA   := 'IP_ASOBANCARIA_DETALLE'; 
      MI_RESPUESTA := MI_TIPOASOBANCARIA;
    ELSIF MI_TIPOASOBANCARIA = '08'
    THEN
      MI_CAMPOS  := 'COMPANIA, 
                     ID, 
                     TIPO_REGISTRO, 
                     TOTAL_REGISTROS, 
                     VALOR_TOTAL, 
                     NUMERO_LOTE, 
                     CREATED_BY, 
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ' ||
                    UN_LINEA ||
                    ', ''08'', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 9) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 12 + UN_FACTMULTIPLICACION, 18) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 32 + UN_FACTMULTIPLICACION, 2) || ''', ''' ||
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      MI_TABLA   := 'IP_ASOBANCARIA_CON_LOT'; 

      MI_PAQUETEA   := SUBSTR(MI_LINEAARCHIVO, 32 + UN_FACTMULTIPLICACION, 2);
      MI_REGISTROSA := SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 9);
      MI_VALORA     := SUBSTR(MI_LINEAARCHIVO, 12 + UN_FACTMULTIPLICACION, 18);

      MI_RESPUESTA  := '08;' || MI_PAQUETEA || ';' || MI_REGISTROSA || ';' || MI_VALORA;
    ELSIF MI_TIPOASOBANCARIA = '09'
    THEN
      MI_CAMPOS  := 'COMPANIA, 
                     ID, 
                     TIPO_REGISTRO, 
                     TOTAL_REGISTROS, 
                     VALOR_TOTAL, 
                     CREATED_BY, 
                     DATE_CREATED';
      MI_VALORES := '''' || UN_COMPANIA || ''', ' ||
                    UN_LINEA ||
                    ', ''09'', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 3 + UN_FACTMULTIPLICACION, 9) || ''', ''' ||
                    SUBSTR(MI_LINEAARCHIVO, 12 + UN_FACTMULTIPLICACION, 18) || ''', ''' ||
                    UN_USUARIO || ''',    
                    SYSDATE' ;
      MI_TABLA   := 'IP_ASOBANCARIA_CON_ARC'; 
      MI_RESPUESTA := MI_TIPOASOBANCARIA;
    END IF;

    IF MI_RESPUESTA IS NOT NULL
    THEN
      BEGIN
        BEGIN
          MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                           UN_ACCION  => 'I',
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           MI_MSGERROREXC(1).CLAVE := 'TIPO'; 
           MI_MSGERROREXC(1).VALOR := MI_TIPOASOBANCARIA; 
           PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ASOBANCARIATABLAS,
             UN_REEMPLAZOS => MI_MSGERROREXC,
             UN_TABLAERROR => MI_TABLA
           );                      
      END;
    ELSE
      RETURN 'N.A';
    END IF;
    RETURN MI_RESPUESTA;
  END FC_ESTABLECERTABLAINSERASOBANC;

 --9 
  FUNCTION FC_VALIDARFACTURAMF 
    /*
      NAME              : FC_VALIDARFACTURAMF -- ValidarFacturaMF en MultiFechas   en Access.
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 27/06/2017
      TIME              : 09:28 AM
      SOURCE MODULE     : PREDIAL 
      DESCRIPTION       : Función que permite validar la información de los recibos de los pagos de Asobancaria.
                          Ruta: Panel Principal\Impuesto Predial\Procesos Ocasionales\Pagos Asobancaria.
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación.  
                          UN_NOFACTURA     => Número de recibo de pago.
                          UN_FECHAPAGO     => Fecha en que se realizó el recaudo.
                          UN_NUMEROORDEN   => Numero de orden del usuario.
                          UN_MODULO        => Código del múdulo de predial.
                          UN_USUARIO       => Código del usuario que realiza el registro de pagos de Asobancaria.

      @NAME:  validarFacturaMF
      @METHOD:  GET
      */
    (
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_NOFACTURA          IN VARCHAR2,
      UN_FECHAPAGO          IN VARCHAR2,
      UN_NUMEROORDEN        IN PCK_SUBTIPOS.TI_NUMORDEN,
      UN_MODULO             IN VARCHAR2,
      UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
    )
  RETURN VARCHAR2 AS 

    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_RTAACME            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
    MI_MES                PCK_SUBTIPOS.TI_ENTERO;
    MI_CONCDESC           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONCDESCAR         PCK_SUBTIPOS.TI_PARAMETRO;  
    MI_CAMPOCONS          VARCHAR2(30 CHAR);
    MI_STRFECHA           VARCHAR2(30 CHAR);
    MI_FECHA              IP_RECIBOS_MULTIFECHA.FECHA1%TYPE;
    MI_DESC               PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL              PCK_SUBTIPOS.TI_DOBLE;
    MI_IMPACT             PCK_SUBTIPOS.TI_DOBLE;
    MI_INTACT             PCK_SUBTIPOS.TI_DOBLE;
    MI_IMPANT             PCK_SUBTIPOS.TI_DOBLE;
    MI_INTANT             PCK_SUBTIPOS.TI_DOBLE;
    MI_IMPDR              PCK_SUBTIPOS.TI_DOBLE;
    MI_INTDR              PCK_SUBTIPOS.TI_DOBLE;
    MI_DOCNUM             PCK_SUBTIPOS.TI_DOBLE;
    MI_INDAPORTE          PCK_SUBTIPOS.TI_LOGICO;  
    MI_INDANULADO         PCK_SUBTIPOS.TI_LOGICO;  
    MI_INDPAGO            PCK_SUBTIPOS.TI_LOGICO;  
    MI_PREVAL             PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORAPORTE        PCK_SUBTIPOS.TI_DOBLE;
    MI_DESCCAR            PCK_SUBTIPOS.TI_DOBLE;
    MI_MSGERROREXC        PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    MI_MES := TO_NUMBER(SUBSTR(UN_FECHAPAGO, 4, 2));

    MI_CONCDESC   := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'CONCEPTO DE DESCUENTO',
                                               UN_MODULO    => UN_MODULO,
                                               UN_FECHA_PAR => SYSDATE), '');

    MI_CONCDESCAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'CONCEPTO PARA DESCUENTO CAR',
                                               UN_MODULO    => UN_MODULO,
                                               UN_FECHA_PAR => SYSDATE), '');
    <<DATOS_RECIBOSMULTIFECHA>>
    FOR RS_RECIBOS IN (SELECT CASE MI_MES
                              WHEN 1  THEN TO_CHAR(TRUNC(FECHA1),'DD/MM/YY')
                              WHEN 2  THEN TO_CHAR(TRUNC(FECHA2),'DD/MM/YY')
                              WHEN 3  THEN TO_CHAR(TRUNC(FECHA3),'DD/MM/YY')
                              WHEN 4  THEN TO_CHAR(TRUNC(FECHA4),'DD/MM/YY')
                              WHEN 5  THEN TO_CHAR(TRUNC(FECHA5),'DD/MM/YY')
                              WHEN 6  THEN TO_CHAR(TRUNC(FECHA6),'DD/MM/YY')
                              WHEN 7  THEN TO_CHAR(TRUNC(FECHA7),'DD/MM/YY')
                              WHEN 8  THEN TO_CHAR(TRUNC(FECHA8),'DD/MM/YY')
                              WHEN 9  THEN TO_CHAR(TRUNC(FECHA9),'DD/MM/YY')
                              WHEN 10 THEN TO_CHAR(TRUNC(FECHA10),'DD/MM/YY')
                              WHEN 11 THEN TO_CHAR(TRUNC(FECHA11),'DD/MM/YY')
                              WHEN 12 THEN TO_CHAR(TRUNC(FECHA12),'DD/MM/YY')
                          END FECHA,
                          CASE MI_MES
                              WHEN 1  THEN DESC1
                              WHEN 2  THEN DESC2
                              WHEN 3  THEN DESC3
                              WHEN 4  THEN DESC4
                              WHEN 5  THEN DESC5
                              WHEN 6  THEN DESC6
                              WHEN 7  THEN DESC7
                              WHEN 8  THEN DESC8
                              WHEN 9  THEN DESC9
                              WHEN 10 THEN DESC10
                              WHEN 11 THEN DESC11
                              WHEN 12 THEN DESC12
                          END DESCS,
                          CASE MI_MES
                              WHEN 1  THEN TOTAL1
                              WHEN 2  THEN TOTAL2
                              WHEN 3  THEN TOTAL3
                              WHEN 4  THEN TOTAL4
                              WHEN 5  THEN TOTAL5
                              WHEN 6  THEN TOTAL6
                              WHEN 7  THEN TOTAL7
                              WHEN 8  THEN TOTAL8
                              WHEN 9  THEN TOTAL9
                              WHEN 10 THEN TOTAL10
                              WHEN 11 THEN TOTAL11
                              WHEN 12 THEN TOTAL12
                          END TOTAL,
                          CASE MI_MES
                              WHEN 1  THEN IMP_ACT1
                              WHEN 2  THEN IMP_ACT2
                              WHEN 3  THEN IMP_ACT3
                              WHEN 4  THEN IMP_ACT4
                              WHEN 5  THEN IMP_ACT5
                              WHEN 6  THEN IMP_ACT6
                              WHEN 7  THEN IMP_ACT7
                              WHEN 8  THEN IMP_ACT8
                              WHEN 9  THEN IMP_ACT9
                              WHEN 10 THEN IMP_ACT10
                              WHEN 11 THEN IMP_ACT11
                              WHEN 12 THEN IMP_ACT12
                          END IMP_ACT,
                          CASE MI_MES
                              WHEN 1  THEN INT_ACT1
                              WHEN 2  THEN INT_ACT2
                              WHEN 3  THEN INT_ACT3
                              WHEN 4  THEN INT_ACT4
                              WHEN 5  THEN INT_ACT5
                              WHEN 6  THEN INT_ACT6
                              WHEN 7  THEN INT_ACT7
                              WHEN 8  THEN INT_ACT8
                              WHEN 9  THEN INT_ACT9
                              WHEN 10 THEN INT_ACT10
                              WHEN 11 THEN INT_ACT11
                              WHEN 12 THEN INT_ACT12
                          END INT_ACT,
                          CASE MI_MES
                              WHEN 1  THEN IMP_ANT1
                              WHEN 2  THEN IMP_ANT2
                              WHEN 3  THEN IMP_ANT3
                              WHEN 4  THEN IMP_ANT4
                              WHEN 5  THEN IMP_ANT5
                              WHEN 6  THEN IMP_ANT6
                              WHEN 7  THEN IMP_ANT7
                              WHEN 8  THEN IMP_ANT8
                              WHEN 9  THEN IMP_ANT9
                              WHEN 10 THEN IMP_ANT10
                              WHEN 11 THEN IMP_ANT11
                              WHEN 12 THEN IMP_ANT12
                          END IMP_ANT,
                          CASE MI_MES
                              WHEN 1  THEN INT_ANT1
                              WHEN 2  THEN INT_ANT2
                              WHEN 3  THEN INT_ANT3
                              WHEN 4  THEN INT_ANT4
                              WHEN 5  THEN INT_ANT5
                              WHEN 6  THEN INT_ANT6
                              WHEN 7  THEN INT_ANT7
                              WHEN 8  THEN INT_ANT8
                              WHEN 9  THEN INT_ANT9
                              WHEN 10 THEN INT_ANT10
                              WHEN 11 THEN INT_ANT11
                              WHEN 12 THEN INT_ANT12
                          END INT_ANT,
                          CASE MI_MES
                              WHEN 1  THEN IMP_DR1  
                              WHEN 2  THEN IMP_DR2  
                              WHEN 3  THEN IMP_DR3  
                              WHEN 4  THEN IMP_DR4
                              WHEN 5  THEN IMP_DR5
                              WHEN 6  THEN IMP_DR6
                              WHEN 7  THEN IMP_DR7
                              WHEN 8  THEN IMP_DR8
                              WHEN 9  THEN IMP_DR9
                              WHEN 10 THEN IMP_DR10
                              WHEN 11 THEN IMP_DR11
                              WHEN 12 THEN IMP_DR12
                          END IMP_DR,
                          CASE MI_MES
                              WHEN 1  THEN INT_DR1
                              WHEN 2  THEN INT_DR2
                              WHEN 3  THEN INT_DR3
                              WHEN 4  THEN INT_DR4
                              WHEN 5  THEN INT_DR5
                              WHEN 6  THEN INT_DR6
                              WHEN 7  THEN INT_DR7
                              WHEN 8  THEN INT_DR8
                              WHEN 9  THEN INT_DR9
                              WHEN 10 THEN INT_DR10
                              WHEN 11 THEN INT_DR11
                              WHEN 12 THEN INT_DR12
                          END INT_DR,
                          DOCNUM
                         FROM IP_RECIBOS_MULTIFECHA               
                        WHERE COMPANIA     = UN_COMPANIA    
                          AND NUMERO_ORDEN = UN_NUMEROORDEN     
                          AND DOCNUM       = UN_NOFACTURA 
                          AND PAGO IN (0)                         
                          AND ANULADO IN (0))
    LOOP
      IF RS_RECIBOS.FECHA IS NOT NULL
      AND RS_RECIBOS.FECHA <> '01/01/1900'
      THEN
        MI_CAMPOS    := ' PREFECLIM = TO_DATE(''' || RS_RECIBOS.FECHA || ''', ''DD/MM/YYYY HH24:mi:ss''), 
                         C13 = ' || RS_RECIBOS.DESCS || ', PREVAL = ' || RS_RECIBOS.TOTAL || 
                      ', C1 = ' || RS_RECIBOS.IMP_ACT || ', C2 = ' || RS_RECIBOS.INT_ACT || 
                      ', C5 = ' || RS_RECIBOS.IMP_ANT || ', C6 = ' || RS_RECIBOS.INT_ANT ||
                      ', C9 = ' || RS_RECIBOS.IMP_DR ||  ', C10 = ' || RS_RECIBOS.INT_DR || 
                      ', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';

        MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || 
                      ''' AND DOCNUM = ''' || RS_RECIBOS.DOCNUM || '''';
        BEGIN
          BEGIN
            MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
             MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
             MI_MSGERROREXC(1).VALOR := UN_NOFACTURA; 
             MI_MSGERROREXC(2).CLAVE := 'DOCNUM'; 
             MI_MSGERROREXC(2).VALOR := RS_RECIBOS.DOCNUM; 
             PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTRECIBOSDEPAGOV,                                                                                                                                                                                                                                                                                                                                                                                                                      
               UN_REEMPLAZOS => MI_MSGERROREXC,
               UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
             );                      
        END;   
        IF MI_RTAACME = 0
        THEN
          RETURN 'Proceso multifechas - Los valores no se asignaron correctamente';
        ELSE
          RETURN 'Proceso finalizado';
        END IF;


      ELSE
        <<INFO_RECMULTIFECHAPORMESES>>
        FOR MI_I IN 1..12 LOOP
          MI_STRSQL := 'SELECT FECHA'    || MI_I ||
                             ', DESC'    || MI_I ||
                             ', TOTAL'   || MI_I || 
                             ', IMP_ACT' || MI_I ||
                             ', INT_ACT' || MI_I ||
                             ', IMP_ANT' || MI_I ||
                             ', INT_ANT' || MI_I ||
                             ', IMP_DR'  || MI_I ||
                             ', INT_DR'  || MI_I ||
                             ', DOCNUM      
                             FROM IP_RECIBOS_MULTIFECHA               
                            WHERE COMPANIA     = ''' || UN_COMPANIA || '''       
                              AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''        
                              AND DOCNUM       = ''' || UN_NOFACTURA || '''     
                              AND PAGO IN (0)                         
                              AND ANULADO IN (0) ';
          BEGIN                    
            EXECUTE IMMEDIATE MI_STRSQL INTO
              MI_FECHA, MI_DESC, MI_TOTAL, MI_IMPACT, MI_INTACT, MI_IMPANT, MI_INTANT, MI_IMPDR, MI_INTDR, MI_DOCNUM;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FECHA := NULL;
          END;

          IF MI_FECHA IS NOT NULL 
          AND UN_FECHAPAGO > MI_FECHA
          THEN

            MI_CAMPOS    := ' PREFECLIM = ''' || MI_FECHA || ''',  
                           C13 = ' || MI_DESC || ', PREVAL = ' || MI_TOTAL || 
                        ', C1 = ' || MI_IMPACT || ', C2 = ' || MI_INTACT || 
                        ', C5 = ' || MI_IMPANT || ', C6 = ' || MI_INTANT ||
                        ', C9 = ' || MI_IMPDR ||  ', C10 = ' || MI_INTDR || 
                        ', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';

            MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || 
                          ''' AND DOCNUM = ''' || MI_DOCNUM || '''';
            BEGIN
              BEGIN
                MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                 MI_MSGERROREXC(1).VALOR := UN_NOFACTURA; 
                 MI_MSGERROREXC(2).CLAVE := 'DOCNUM'; 
                 MI_MSGERROREXC(2).VALOR := MI_DOCNUM; 
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTRECIBOSDEPAGOV,                                                                                                                                                                                                                                                                                                                                                                                                                      
                   UN_REEMPLAZOS => MI_MSGERROREXC,
                   UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
                 );                      
            END;     

            IF MI_RTAACME = 0
            THEN
              RETURN 'Proceso multifechas - Los valores no se asignaron correctamente';
            ELSE
              RETURN 'Proceso finalizado';
            END IF;  
          END IF;
        END LOOP INFO_RECMULTIFECHAPORMESES;
      END IF;
    END LOOP DATOS_RECIBOSMULTIFECHA;

    MI_STRSQL := 'SELECT ''X'' RECIBO     
                    FROM IP_RECIBOS_DE_PAGO                  
                   WHERE COMPANIA     = ''' || UN_COMPANIA || '''       
                     AND DOCNUM       = ''' || UN_NOFACTURA ||  '''      
                     AND PAGO IN (0)        
                     AND ANULADO IN (0)';      
    BEGIN                    
      EXECUTE IMMEDIATE MI_STRSQL INTO
          MI_CAMPOCONS;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CAMPOCONS := NULL;
    END;

    IF MI_CAMPOCONS IS NOT NULL
    THEN
      <<INFO_RECPAGOPORMESES>>
      FOR MI_I IN 1..12 LOOP
          MI_STRSQL := 'SELECT FECHA' || MI_MES ||
                              ', TO_CHAR(TRUNC(FECHA' || MI_I || '), ''DD/MM/YYYY'')' ||
                              ', DESC'    || MI_I ||
                              ', INDAPORTE ' ||
                              ', TOTAL'   || MI_I || 
                              ', VALOR_APORTE' ||
                              ', INT' || MI_I ||
                              ', INTCAR' || MI_I ||
                              ', DESC_CAR' || MI_I || 
                              ', DOCNUM      
                        FROM IP_RECIBOS_DE_PAGO                  
                       WHERE COMPANIA     = ''' || UN_COMPANIA || '''       
                         AND DOCNUM       = ''' || UN_NOFACTURA ||  '''      
                         AND PAGO IN (0)        
                         AND ANULADO IN (0) ';
          BEGIN                    
            EXECUTE IMMEDIATE MI_STRSQL INTO
              MI_FECHA, MI_STRFECHA, MI_DESC, MI_INDAPORTE, MI_TOTAL, MI_VALORAPORTE, MI_INTACT, MI_INTANT, MI_DESCCAR, MI_DOCNUM;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FECHA := NULL;
          END;

          IF MI_FECHA IS NOT NULL 
          AND MI_STRFECHA <> '01/01/1900'
          AND TO_DATE(UN_FECHAPAGO) > TO_DATE(MI_STRFECHA)
          THEN
            IF MI_CONCDESC IS NULL
            OR MI_CONCDESC = '  '
            THEN
              MI_CONCDESC := '13';
            END IF;

            IF MI_TOTAL IS NOT NULL
            THEN 
              BEGIN
                IF MI_INDAPORTE NOT IN (0)
                THEN
                  MI_PREVAL := MI_TOTAL + MI_VALORAPORTE;
                ELSE
                  MI_PREVAL := MI_TOTAL;
                END IF;
                IF MI_CONCDESCAR IS NULL
                OR MI_CONCDESCAR = ' '
                OR MI_CONCDESCAR = '_'
                OR MI_CONCDESCAR = '0'
                THEN
                  MI_CAMPOS    := ' PREFECLIM = TO_DATE(''' || MI_STRFECHA || ''', ''DD/MM/YYYY HH24:mi:ss''), 
                                    C' || MI_CONCDESC || ' = ' || MI_DESC ||
                                  ', PREVAL = ' || MI_PREVAL ||
                                  ', C2 = ' || MI_INTACT || -->INT || MES 
                                  ', C4 = ' || MI_INTANT || -->INTCAR || MES
                                  ', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || 
                                  ''' AND DOCNUM = ''' || MI_DOCNUM || '''';
                  BEGIN
                    MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                ELSE
                  MI_CAMPOS    := ' PREFECLIM = TO_DATE(''' || MI_STRFECHA || ''', ''DD/MM/YYYY HH24:mi:ss''), 
                                    C' || MI_CONCDESC || ' = ' || MI_DESC ||
                                  ', PREVAL = ' || MI_PREVAL ||
                                  ', C2 = ' || MI_INTACT || -->INT || MES 
                                  ', C4 = ' || MI_INTANT || -->INTCAR || MES
                                  ', C' || MI_CONCDESCAR || ' = ' ||  MI_DESCCAR || 
                                  ', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || 
                                  ''' AND DOCNUM = ''' || MI_DOCNUM || '''';
                  BEGIN
                    MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);  
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                END IF;

                IF MI_RTAACME = 0
                THEN
                  RETURN 'Proceso multifechas - Los valores no se asignaron correctamente';
                ELSE
                  RETURN 'Proceso finalizado';
                END IF;    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 MI_MSGERROREXC(1).CLAVE := 'NUMRECIBO'; 
                 MI_MSGERROREXC(1).VALOR := UN_NOFACTURA; 
                 MI_MSGERROREXC(2).CLAVE := 'DOCNUM'; 
                 MI_MSGERROREXC(2).VALOR := MI_DOCNUM; 
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_ACTRECIBOSDEPAGOV,                                                                                                                                                                                                                                                                                                                                                                                                                      
                   UN_REEMPLAZOS => MI_MSGERROREXC,
                   UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO'
                 );                      
              END;   
            END IF;
          END IF;
      END LOOP INFO_RECPAGOPORMESES;
    ELSE
      MI_STRSQL := 'SELECT IP_RECIBOS_DE_PAGO.ANULADO,         
                           IP_RECIBOS_DE_PAGO.PAGO        
                      FROM IP_RECIBOS_DE_PAGO        
                     WHERE IP_RECIBOS_DE_PAGO.COMPANIA = ''' || UN_COMPANIA || '''      
                       AND IP_RECIBOS_DE_PAGO.DOCNUM   = ''' || UN_NOFACTURA || '''';

      BEGIN                    
        EXECUTE IMMEDIATE MI_STRSQL INTO
          MI_INDANULADO, MI_INDPAGO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_INDANULADO := NULL;
      END;

      IF MI_INDANULADO IS NULL
      THEN
        RETURN 'La factura no se encuentra registrada en el sistema o su estado no es activo';
      ELSIF MI_INDANULADO NOT IN (0)
      THEN
        RETURN 'La factura se encuentra Anulada.';
      ELSIF MI_INDPAGO NOT IN (0) 
      THEN
        RETURN 'El pago ya se encuentra registrado.';
      END IF;

    END IF;

    RETURN 'Proceso finalizado';

  END FC_VALIDARFACTURAMF;

END PCK_PREDIAL_COM5;