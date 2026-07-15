create or replace PACKAGE BODY PCK_PREDIAL_FIN AS

PROCEDURE PR_CALCULARACUERDOVIG 
  (
  
    /*
    NAME              : PR_CALCULARACUERDOVIG En Access --> CalcularAcuerdoVigencias
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 19/08/2016
    TIME              : 3:55 PM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 11/01/2017 \ 18/07/2017
    TIME              : 3:25 PM
    DESCRIPTION       : Procedimiento que se llama en Usuarios Predial / Botón Financiar/Calcular/Función CrearAcuerdo
                        Inserta a las tablas recibidas por parámetros los acuerdos / Se cambió el estándar de codificación y se agregó manejo de excepciones.
                        /Se agregaron los campos de auditoría a las operaciones de insercion y el usuario como variable de entrada.
    @NAME:  crearAcuedoDePago
    @METHOD:  POST                         
  */

    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ACUERDO          IN VARCHAR2,
    UN_PERIODO          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_T_ACUERDO        IN VARCHAR2,
    UN_T_FACTURADOSACU  IN VARCHAR2,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )

  AS 

  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
  MI_PREDIO        PCK_SUBTIPOS.TI_CODPREDIO;
  MI_PREANOI       PCK_SUBTIPOS.TI_ANIO;
  MI_PREANO        PCK_SUBTIPOS.TI_ANIO;
  RS               SYS_REFCURSOR;
  MI_SSQL          PCK_SUBTIPOS.TI_STRSQL;
  MI_SQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_CODIGO        PCK_SUBTIPOS.TI_CODPREDIO;
  MI_NUMERO_ORDEN  PCK_SUBTIPOS.TI_NUMORDEN;
  MI_C1            PCK_SUBTIPOS.TI_DOBLE;
  MI_C2            PCK_SUBTIPOS.TI_DOBLE;
  MI_C3            PCK_SUBTIPOS.TI_DOBLE;
  MI_C4            PCK_SUBTIPOS.TI_DOBLE;
  MI_C5            PCK_SUBTIPOS.TI_DOBLE;
  MI_C6            PCK_SUBTIPOS.TI_DOBLE;
  MI_C7            PCK_SUBTIPOS.TI_DOBLE;
  MI_C8            PCK_SUBTIPOS.TI_DOBLE;
  MI_C9            PCK_SUBTIPOS.TI_DOBLE;
  MI_C10           PCK_SUBTIPOS.TI_DOBLE;
  MI_C11           PCK_SUBTIPOS.TI_DOBLE;
  MI_C12           PCK_SUBTIPOS.TI_DOBLE;
  MI_C13           PCK_SUBTIPOS.TI_DOBLE;
  MI_C14           PCK_SUBTIPOS.TI_DOBLE;
  MI_C15           PCK_SUBTIPOS.TI_DOBLE;
  MI_C16           PCK_SUBTIPOS.TI_DOBLE;
  MI_C17           PCK_SUBTIPOS.TI_DOBLE;
  MI_C18           PCK_SUBTIPOS.TI_DOBLE;
  MI_C19           PCK_SUBTIPOS.TI_DOBLE;
  MI_C20           PCK_SUBTIPOS.TI_DOBLE;
  MI_TOTAL         PCK_SUBTIPOS.TI_DOBLE;
  MI_PREANO_FAC    PCK_SUBTIPOS.TI_DOBLE;
  MI_INTCUOTA      PCK_SUBTIPOS.TI_DOBLE;
  MI_STRFECHA      DATE;
  MI_FECHA         DATE;
  MI_ANO_ACTUAL    PCK_SUBTIPOS.TI_ANIO;
  MI_ANO_ANTERIOR  PCK_SUBTIPOS.TI_ANIO;
  MI_ANO_ANT       PCK_SUBTIPOS.TI_ANIO;
  MI_CAPITAL       PCK_SUBTIPOS.TI_DOBLE;
  MI_INTERESES     PCK_SUBTIPOS.TI_DOBLE;
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN 

    SELECT TO_CHAR(SYSDATE , 'YYYY') ANO 
    INTO MI_ANO_ACTUAL
    FROM DUAL;

    MI_ANO_ANTERIOR := MI_ANO_ACTUAL - 1;
    MI_ANO_ANT := MI_ANO_ACTUAL - 2;
 BEGIN
   BEGIN     
    MI_SQL := 'SELECT PREDIO 
                      ,PREANOI 
                      ,PREANO
               FROM   '||UN_T_ACUERDO||' 
               WHERE COMPANIA     = '''||UN_COMPANIA||'''
                AND CODIGOACUERDO = '''||UN_ACUERDO||'''';   

       EXECUTE IMMEDIATE MI_SQL  INTO MI_PREDIO , MI_PREANOI, MI_PREANO;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
   END;   
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACUERDOVIG
         );

 END;


    MI_SSQL := 'SELECT  IP_FACTURADOS.CODIGO 
                        ,IP_FACTURADOS.NUMERO_ORDEN
                        ,IP_FACTURADOS.C1
                        ,IP_FACTURADOS.C2 
                        ,IP_FACTURADOS.C3 
                        ,IP_FACTURADOS.C4 
                        ,IP_FACTURADOS.C5 
                        ,IP_FACTURADOS.C6 
                        ,IP_FACTURADOS.C7 
                        ,IP_FACTURADOS.C8 
                        ,IP_FACTURADOS.C9 
                        ,IP_FACTURADOS.C10 
                        ,IP_FACTURADOS.C11 
                        ,IP_FACTURADOS.C12 
                        ,IP_FACTURADOS.C13 
                        ,IP_FACTURADOS.C14 
                        ,IP_FACTURADOS.C15 
                        ,IP_FACTURADOS.C16 
                        ,IP_FACTURADOS.C17 
                        ,IP_FACTURADOS.C18 
                        ,IP_FACTURADOS.C19 
                        ,IP_FACTURADOS.C20 
                        ,IP_FACTURADOS.TOTAL 
                        ,IP_FACTURADOS.PREANO 
                FROM   IP_FACTURADOS 
                WHERE  IP_FACTURADOS.COMPANIA = '''||UN_COMPANIA||''' 
                  AND  IP_FACTURADOS.CODIGO   = '''||MI_PREDIO||''' 
                  AND  IP_FACTURADOS.PREANO   BETWEEN '||MI_PREANOI||' AND '||MI_PREANO||'';
	<<Registrar_Acuerdo>>					
  BEGIN  

    OPEN RS FOR MI_SSQL;
      MI_INTCUOTA := 1;
      --Obtiene el Ultimo Día de la fecha Actual --Función Access UltimoDia
      SELECT LAST_DAY(SYSDATE) FECHA 
      INTO   MI_STRFECHA 
      FROM   DUAL;
      LOOP 
        FETCH RS INTO MI_CODIGO, MI_NUMERO_ORDEN ,MI_C1,MI_C2,MI_C3,MI_C4,MI_C5,MI_C6,MI_C7,MI_C8,MI_C9,MI_C10, 
                      MI_C11,MI_C12,MI_C13,MI_C14,MI_C15,MI_C16,MI_C17,MI_C18,MI_C19,MI_C20,MI_TOTAL,MI_PREANO_FAC;
        EXIT WHEN  RS%NOTFOUND;

          MI_CAPITAL := (MI_TOTAL - MI_C2 - MI_C4);
          MI_INTERESES := MI_C2 + MI_C4;
        IF MI_PREANO_FAC = MI_ANO_ACTUAL THEN 

          MI_CAMPOS := 'COMPANIA
                       ,CODIGOACUERDO
                       ,PREDIO
                       ,NUMERO_ORDEN
                       ,CUOTA,PAGADO
                       ,TOTAL
                       ,CAPITAL
                       ,INTERESES
                       ,INTERES_ACUERDO
                       ,INTERES_RECARGO
                       ,FECHAFACTURADO
                       ,C1
                       ,C2
                       ,C3
                       ,C4
                       ,C5
                       ,C6
                       ,C7
                       ,C8
                       ,C9
                       ,C10
                       ,C11
                       ,C12
                       ,C13
                       ,C14
                       ,C15
                       ,C16
                       ,C17
                       ,C18
                       ,C19
                       ,C20
                       ,PREANOI
                       ,PREANO
                       ,CREATED_BY
                       ,DATE_CREATED';

          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||UN_ACUERDO||'''
                        ,'''||MI_CODIGO||'''
                        ,'''||MI_NUMERO_ORDEN||'''
                        ,'||MI_INTCUOTA||'
                        ,0
                        ,'||MI_TOTAL||'
                        ,'||MI_CAPITAL||'
                        ,'||MI_INTERESES||'
                        ,0 
                        ,0
                        ,'''||MI_STRFECHA||'''
                        ,'||MI_C1||'
                        ,'||MI_C2||'
                        ,'||MI_C3||'
                        ,'||MI_C4||'
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,'||MI_C13||'
                        ,'||MI_C14||'
                        ,'||MI_C15||'
                        ,'||MI_C16||'
                        ,'||MI_C17||'
                        ,'||MI_C18||'
                        ,'||MI_C19||'
                        ,'||MI_C20||'
                        ,'||MI_PREANO_FAC||'
                        ,'||MI_PREANO_FAC||'
                        ,'''||UN_USUARIO||'''
                        ,SYSDATE   ';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>  ''||UN_T_FACTURADOSACU||''
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);  

        ELSIF MI_PREANO_FAC = MI_ANO_ANTERIOR THEN 

          MI_CAMPOS := 'COMPANIA
                       ,CODIGOACUERDO
                       ,PREDIO
                       ,NUMERO_ORDEN
                       ,CUOTA
                       ,PAGADO
                       ,TOTAL
                       ,CAPITAL
                       ,INTERESES
                       ,INTERES_ACUERDO
                       ,INTERES_RECARGO
                       ,FECHAFACTURADO
                       ,C1
                       ,C2
                       ,C3
                       ,C4
                       ,C5
                       ,C6
                       ,C7
                       ,C8
                       ,C9
                       ,C10
                       ,C11
                       ,C12
                       ,C13
                       ,C14
                       ,C15
                       ,C16
                       ,C17
                       ,C18
                       ,C19
                       ,C20
                       ,PREANOI
                       ,PREANO
                       ,CREATED_BY
                       ,DATE_CREATED';

          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||UN_ACUERDO||'''
                        ,'''||MI_CODIGO||'''
                        ,'''||MI_NUMERO_ORDEN||'''
                        ,'||MI_INTCUOTA||'
                        ,0
                        ,'||MI_TOTAL||'
                        ,'||MI_CAPITAL||'
                        ,'||MI_INTERESES||'
                        ,0
                        ,0
                        ,'''||MI_STRFECHA||'''
                        ,0
                        ,0
                        ,0
                        ,0
                        ,'||MI_C1||','||MI_C2||','||MI_C3||','||MI_C4||'
                        ,0
                        ,0
                        ,0
                        ,0
                        ,'||MI_C13||'
                        ,'||MI_C14||'
                        ,'||MI_C15||'
                        ,'||MI_C16||'
                        ,'||MI_C17||'
                        ,'||MI_C18||'
                        ,'||MI_C19||'
                        ,'||MI_C20||'
                        ,'||MI_PREANO_FAC||'
                        ,'||MI_PREANO_FAC||'
                        ,'''||UN_USUARIO||'''
                        ,SYSDATE';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => ''||UN_T_FACTURADOSACU||''
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);                

        ELSIF MI_PREANO_FAC <=  MI_ANO_ANT THEN 

          MI_CAMPOS := 'COMPANIA
                       ,CODIGOACUERDO
                       ,PREDIO
                       ,NUMERO_ORDEN
                       ,CUOTA
                       ,PAGADO
                       ,TOTAL
                       ,CAPITAL
                       ,INTERESES
                       ,INTERES_ACUERDO
                       ,INTERES_RECARGO
                       ,FECHAFACTURADO
                       ,C1
                       ,C2
                       ,C3
                       ,C4
                       ,C5
                       ,C6
                       ,C7
                       ,C8
                       ,C9
                       ,C10
                       ,C11
                       ,C12
                       ,C13
                       ,C14
                       ,C15
                       ,C16
                       ,C17
                       ,C18
                       ,C19
                       ,C20
                       ,PREANOI
                       ,PREANO
                       ,CREATED_BY
                       ,DATE_CREATED';
          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||UN_ACUERDO||'''
                        ,'''||MI_CODIGO||'''
                        ,'''||MI_NUMERO_ORDEN||'''
                        ,'||MI_INTCUOTA||'
                        ,0
                        ,'||MI_TOTAL||'
                        ,'||MI_CAPITAL||'
                        ,'||MI_INTERESES||'
                        ,0
                        ,0
                        ,'''||MI_STRFECHA||'''
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,0
                        ,'||MI_C1||'
                        ,'||MI_C2||'
                        ,'||MI_C3||'
                        ,'||MI_C4||'
                        ,'||MI_C13||'
                        ,'||MI_C14||'
                        ,'||MI_C15||'
                        ,'||MI_C16||'
                        ,'||MI_C17||'
                        ,'||MI_C18||'
                        ,'||MI_C19||'
                        ,'||MI_C20||'
                        ,'||MI_PREANO_FAC||'
                        ,'||MI_PREANO_FAC||'
                        ,'''||UN_USUARIO||'''
                        ,SYSDATE';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => ''||UN_T_FACTURADOSACU||''
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);  
        END IF;   

        MI_CAMPOS := 'NCUOTAS = '||MI_INTCUOTA||',
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY   ='''||UN_USUARIO||''' ';

        MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||''' 
                     AND CODIGOACUERDO = '''||UN_ACUERDO||''' 
                     AND PREDIO        = '''||MI_CODIGO||'''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => ''||UN_T_ACUERDO||''
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS 
                                              ,UN_CONDICION => MI_CONDICION);

        MI_INTCUOTA := MI_INTCUOTA + 1;

        SELECT LAST_DAY(ADD_MONTHS(MI_STRFECHA,UN_PERIODO)) FECHA
        INTO   MI_FECHA
        FROM   DUAL;


        MI_STRFECHA := MI_FECHA;               
      END LOOP Registrar_Acuerdo;
    CLOSE RS;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN      
         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
  END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN   
             MI_MSGERROR(1).CLAVE := 'CAMPO';
             MI_MSGERROR(1).VALOR := MI_CAMPOS;
             /*MI_MSGERROR(2).CLAVE := 'VALOR';
             MI_MSGERROR(2).VALOR := MI_VALORES;*/

            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_REGACUERDO
            , UN_REEMPLAZOS =>  MI_MSGERROR
         );

END PR_CALCULARACUERDOVIG; 

PROCEDURE PR_LIQUIDAINTERESRECARGO
  /*
  NAME              : PR_LIQUIDAINTERESRECARGO  --> EN ACCESS liquidaInteresRecargo
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
  DATE MIGRADOR     : 18/08/2016
  TIME              : 08:20 AM
  SOURCE MODULE     : PredialP2016.05.06
  DESCRIPTION       : Actualiza el valor del campo INTERES_RECARGO a cero en un CODIGOACUERDO específico.
  @NAME: liquidaInteresRecargo
  @METHOD: PUT
  */
  (
   -- Parametro que recibe el numero de la entidad que se calcula
	UN_COMPANIA 		           IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del acuerdo para actualizar el valor del interes recargo
	UN_CODIGOACUERDO 	         IN IP_ACUERDOS.CODIGOACUERDO%TYPE
	)
  AS

    -- Variable que almacenara el valor de la suma de recargo, retornado en la consulta
    MI_SUMARECARGO           PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara la cadena que contiene los campos, que se van na actualizar en la consulta
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la condicion a usar en la actualizacion.
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    BEGIN
      SELECT 	SUM(IP_FACTURADOSACUERDOS.INTERES_RECARGO)
        INTO 	MI_SUMARECARGO
        FROM 	IP_FACTURADOSACUERDOS
       WHERE 	IP_FACTURADOSACUERDOS.COMPANIA   	  = UN_COMPANIA
         AND 	IP_FACTURADOSACUERDOS.CODIGOACUERDO	= UN_CODIGOACUERDO
         AND 	IP_FACTURADOSACUERDOS.PAGADO       	= 0;

    EXCEPTION
     WHEN NO_DATA_FOUND THEN
       MI_SUMARECARGO := 0;
     END;

    BEGIN
      IF MI_SUMARECARGO       > 0 THEN
         MI_CAMPOS            := 'IP_FACTURADOSACUERDOS.INTERES_RECARGO   = 0';
         MI_CONDICION         := 'IP_FACTURADOSACUERDOS.COMPANIA          = '''||UN_COMPANIA     ||'''                                                            
                                  AND IP_FACTURADOSACUERDOS.CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''  
                                  AND IP_FACTURADOSACUERDOS.PAGADO        = 0';

         PCK_DATOS.GL_RTA     := PCK_DATOS.FC_ACME(UN_TABLA    =>'IP_FACTURADOSACUERDOS',
                                                   UN_ACCION   =>'M',
                                                   UN_CAMPOS   =>MI_CAMPOS, 
                                                   UN_CONDICION=>MI_CONDICION
                                                   );
      END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_LIQUIDAINTRECARGO
  ); 
END PR_LIQUIDAINTERESRECARGO;


PROCEDURE PR_LIQUIDAINTERESACUERDO
  /*
    NAME              : PR_LIQUIDAINTERESACUERDO  --> EN ACCESS liquidaInteresRecargo
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
    DATE MIGRADOR     : 17/08/2016
    TIME              : 17:40 PM 
    SOURCE MODULE     : PredialP2016.05.06
    DESCRIPTION       : Realiza copia del INTERES_ACUERDO en el campo COPIAINTERES_ACUERDO y Actualiza su valor a cero.
    @NAME: liquidarInteresAcuerdo
    @METHOD: PUT     
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
	UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero de codigo del acuerdo, al que se le realizara la copia
	UN_CODIGOACUERDO 	         IN IP_ACUERDOS.CODIGOACUERDO%TYPE
  )
  AS

    -- Variable que almacenara el valor de la suma, que retorna la consulta
    MI_SUMAINTERES           PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara los campos a ser actualizados en la funcion
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la condicion usada para hacer la actualizacion en la funcion
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    BEGIN
      SELECT 	SUM(IP_FACTURADOSACUERDOS.INTERES_ACUERDO)
        INTO 	MI_SUMAINTERES
        FROM 	IP_FACTURADOSACUERDOS
       WHERE 	IP_FACTURADOSACUERDOS.COMPANIA   	  = UN_COMPANIA
         AND 	IP_FACTURADOSACUERDOS.CODIGOACUERDO	= UN_CODIGOACUERDO
         AND 	IP_FACTURADOSACUERDOS.PAGADO       	=0;
   EXCEPTION
     WHEN NO_DATA_FOUND THEN
       MI_SUMAINTERES:=0;
     END;

      BEGIN  
      IF MI_SUMAINTERES        > 0 THEN
         MI_CAMPOS             := 'IP_FACTURADOSACUERDOS.COPIAINTERES_ACUERDO = IP_FACTURADOSACUERDOS.INTERES_ACUERDO';
         MI_CONDICION          := 'IP_FACTURADOSACUERDOS.COMPANIA             = '''||UN_COMPANIA     ||'''                
                                   AND IP_FACTURADOSACUERDOS.CODIGOACUERDO    = '''||UN_CODIGOACUERDO||'''                
                                   AND IP_FACTURADOSACUERDOS.PAGADO           = 0';

         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA    =>'IP_FACTURADOSACUERDOS',
                                                    UN_ACCION   =>'M',
                                                    UN_CAMPOS   =>MI_CAMPOS,
                                                    UN_CONDICION=> MI_CONDICION
                                                    );

         MI_CAMPOS             := 'IP_FACTURADOSACUERDOS.INTERES_ACUERDO      = 0';
         MI_CONDICION          := 'IP_FACTURADOSACUERDOS.COMPANIA             = '''||UN_COMPANIA     ||'''                
                                   AND IP_FACTURADOSACUERDOS.CODIGOACUERDO    = '''||UN_CODIGOACUERDO||'''               
                                   AND IP_FACTURADOSACUERDOS.PAGADO           = 0';

         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA    =>'IP_FACTURADOSACUERDOS',
                                                    UN_ACCION   =>'M', 
                                                    UN_CAMPOS   =>MI_CAMPOS, 
                                                    UN_CONDICION=>MI_CONDICION
                                                    );
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_LIQUIDAINTERESACU
  );

END PR_LIQUIDAINTERESACUERDO;


PROCEDURE PR_REVIERTEINTERESPAGANT
  /*
  NAME              : PR_REVIERTEINTERESPAGANT  --> EN ACCESS revierteInterésPagoAnticipado
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
  DATE MIGRADOR     : 18/08/2016
  TIME              : 09:20 AM
  SOURCE MODULE     : PredialP2016.05.06
  DESCRIPTION       : Actualiza el valor del campo INTERES_RECARGO pasando el valor existente en el campo COPIAINTERES_ACUERDO
  @NAME: revertirInteresPagoAnticipado
  @METHOD: PUT
  */
  (
   -- Parametro que recibe el numero de la entidad que se calcula
	UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del acuerdo para actualizar el valor del interes recargo
	UN_CODIGOACUERDO           IN IP_ACUERDOS.CODIGOACUERDO%TYPE
  )
   AS
    --  MI_ERROR_FUN   			   PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 3;
      -- Variable que almacenara el valor de la suma  que retorna la consulta
      MI_SUMAINTERESACUERDO  PCK_SUBTIPOS.TI_DOBLE;
      -- Variable que almacenara la cadena de campos, que se van a actualizar en la funcion 
      MI_CAMPOS      			   PCK_SUBTIPOS.TI_CAMPOS;
      -- Variable que almacenara la condicion que se va a usar para hacer la actualizacion
      MI_CONDICION   			   PCK_SUBTIPOS.TI_CONDICION;
   BEGIN
    BEGIN
      SELECT 	SUM(IP_FACTURADOSACUERDOS.INTERES_ACUERDO)
        INTO 	MI_SUMAINTERESACUERDO
        FROM 	IP_FACTURADOSACUERDOS
       WHERE 	IP_FACTURADOSACUERDOS.COMPANIA   	  = UN_COMPANIA
         AND 	IP_FACTURADOSACUERDOS.CODIGOACUERDO	= UN_CODIGOACUERDO
         AND 	IP_FACTURADOSACUERDOS.PAGADO       	= 0;

  EXCEPTION
      WHEN NO_DATA_FOUND THEN
           MI_SUMAINTERESACUERDO:=0;  
      END;

     BEGIN 
        IF MI_SUMAINTERESACUERDO  IN (0) THEN	    
           MI_CAMPOS              := 'IP_FACTURADOSACUERDOS.INTERES_ACUERDO   = IP_FACTURADOSACUERDOS.COPIAINTERES_ACUERDO ';
           MI_CONDICION           := 'IP_FACTURADOSACUERDOS.COMPANIA          = '''||UN_COMPANIA     ||'''                
                                      AND IP_FACTURADOSACUERDOS.CODIGOACUERDO = '''||UN_CODIGOACUERDO||'''               
                                      AND IP_FACTURADOSACUERDOS.PAGADO        = 0';

            PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA    =>'IP_FACTURADOSACUERDOS',
                                                       UN_ACCION   =>'M',
                                                       UN_CAMPOS   =>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION
                                                       );
        END IF;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_REVIERTEINTPAGANT
  );

END PR_REVIERTEINTERESPAGANT;

  FUNCTION FC_CALCULARCUOTASACUERDO
     /*
      NAME              : FC_CALCULARCUOTASACUERDO En Access --> CmdCalcularAcuerdo_Click() en Form_Usuarios_Predial
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 07/02/2017
      TIME              : 05:00 PM
      SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
      DESCRIPTION       : Proceso que permite realizar el cálculo de intereses por mora en las cuotas adeudadas.
      PARAMETERS        : UN_COMPANIA    	 => Compañia de ingreso a la aplicación
                          UN_CODIGOACUERDO => Código del acuerdo al que se realizará el cálculo.
                          UN_INTERES       => Valor del interés del acuerdo seleccionado en la aplicación.
                          UN_USUARIO       => Usuario que realiza el registro.
                          UN_FECHACORTE    => Fecha actual en la cual se realiza el cálculo.
                          UN_CODIGO        => Código del predio al cual se realiza el cálculo del acuerdo.

      @NAME:  calcularCuotasAcuerdo
      @METHOD:  GET
      */
    (
      UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODIGOACUERDO        IN IP_ACUERDOS.CODIGOACUERDO%TYPE,
      UN_ANULADO              IN PCK_SUBTIPOS.TI_LOGICO,
      UN_USUARIO              IN VARCHAR2,
      UN_FECHACORTE           IN TIMESTAMP,
      UN_CODIGO               IN PCK_SUBTIPOS.TI_CODPREDIO
    )
  RETURN VARCHAR2 AS 
      MI_TIPOINTERES          VARCHAR2(6 CHAR);
      MI_DIGITOSREDONDEO      PCK_SUBTIPOS.TI_PARAMETRO;
      MI_RTA                  VARCHAR2(10 CHAR);
      MI_RTAACME              PCK_SUBTIPOS.TI_RTA_ACME;
      MI_TASARECARGO          PCK_SUBTIPOS.TI_PORCENTAJE;
      MI_PERIODICIDADCOBRO    PCK_SUBTIPOS.TI_PARAMETRO;
      MI_TIEMPOMORA           PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORRECARGO         PCK_SUBTIPOS.TI_DOBLE;
  BEGIN

    IF UN_ANULADO = 0
    THEN
        MI_TIPOINTERES := SUBSTR(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                               UN_NOMBRE    => 'INTERES COMPUESTO - FINANCIACION - RECARGO',
                                               UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                               UN_FECHA_PAR => SYSDATE),4,2);

        BEGIN                                       
          IF MI_TIPOINTERES <> 'SI'
          AND MI_TIPOINTERES <> 'NO'
          THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END IF;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_TIPO_INTERES
           );
        END;


        IF FC_CALCULARRECARGOSACUERDO(UN_COMPANIA         => UN_COMPANIA,
                                      UN_CODIGOACUERDO    => UN_CODIGOACUERDO,
                                      UN_INTERESCOMPUESTO => MI_TIPOINTERES,
                                      UN_FECHACORTE       => TO_DATE(TRUNC(UN_FECHACORTE), 'DD/MM/YYYY')) = 0
        THEN
          MI_RTA := '-1';
        ELSE
          PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA,     
                                   UN_CODMOD      => UN_CODIGO,
                                   UN_OPEMOD      => UN_USUARIO,
                                   UN_CCOMOD      => '247',
                                   UN_VANMOD      => 'N/A',
                                   UN_VNUMOD      => 'N/A',
                                   UN_DESCRIPCION => 'Predio ' || UN_CODIGO || ' Acuerdo: ' || UN_CODIGOACUERDO,
                                   UN_FECMOD      => SYSDATE,
                                   UN_HORMOD      => SYSDATE);
          MI_RTA := 'OK';
        END IF;
    END IF;

    IF MI_RTA IS NULL
        THEN
          MI_RTA := 'NO';
    END IF;
    RETURN MI_RTA;
  END FC_CALCULARCUOTASACUERDO;

  FUNCTION FC_CALCULARRECARGOSACUERDO
    /*
      NAME              : FC_CALCULARRECARGOSACUERDO En Access --> CalcularRecargosAcuerdo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 07/02/2017
      TIME              : 03:00 PM
      SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
      DESCRIPTION       : Proceso que permite realizar el cálculo de los recargos de acuerdo.
      PARAMETERS        : UN_COMPANIA    	    => Compañia de ingreso a la aplicación
                          UN_CODIGOACUERDO	  => Código del acuerdo al que se realizará el cálculo.
                          UN_INTERESCOMPUESTO => Equivale al valor del parámetro INTERES COMPUESTO - FINANCIACION - RECARGO.
                          UN_FECHACORTE    	  => Fecha actual en la cual se realiza el cálculo.

      @NAME:  calcularRecargosAcuerdo
      @METHOD:  GET
      */
    (
      UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODIGOACUERDO        IN IP_ACUERDOS.CODIGOACUERDO%TYPE,
      UN_INTERESCOMPUESTO     IN VARCHAR2,
      UN_FECHACORTE           IN TIMESTAMP
    )
  RETURN NUMBER AS 
      MI_TIPOINTERES          VARCHAR2(6 CHAR);
      MI_DIGITOSREDONDEO      PCK_SUBTIPOS.TI_PARAMETRO;
      MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
      MI_RTAACME              PCK_SUBTIPOS.TI_RTA_ACME;
      MI_TASARECARGO          PCK_SUBTIPOS.TI_PORCENTAJE;
      MI_PERIODICIDADCOBRO    PCK_SUBTIPOS.TI_PARAMETRO;
      MI_TIEMPOMORA           PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORRECARGO         PCK_SUBTIPOS.TI_DOBLE;
      MI_NOMBREMES            VARCHAR2(12 CHAR);
      MI_MES                  PCK_SUBTIPOS.TI_ENTERO;
      MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    MI_RTA := 0;  
    MI_DIGITOSREDONDEO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                    UN_NOMBRE    => 'DIGITOS DE REDONDEO PARA ACUERDOS',
                                                    UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                    UN_FECHA_PAR => SYSDATE), 3) *-1;

    <<FACT_ACUERDOS>>
    FOR RS_FACACT IN(SELECT CUOTA,
                            CAPITAL,
                            FECHAFACTURADO,
                            INTERESES,
                            INTERES_ACUERDO,
                            INTERES_RECARGO
                       FROM IP_FACTURADOSACUERDOS 
                      WHERE COMPANIA = UN_COMPANIA
                       AND CODIGOACUERDO = UN_CODIGOACUERDO 
                        AND PAGADO = 0 )
    LOOP
      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                               UN_NOMBRE    => 'ACUERDOS PAGO - USAR TASA VIGENTE',
                               UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                               UN_FECHA_PAR => SYSDATE) = 'SI'
      THEN
        MI_TASARECARGO := FC_CONSULTARTASAINTERESVIGENTE(UN_COMPANIA  => UN_COMPANIA);

        BEGIN
          IF MI_TASARECARGO = 0
          THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END IF;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN
          SELECT EXTRACT(MONTH FROM SYSDATE)
            INTO MI_MES
          FROM DUAL;
          MI_NOMBREMES := PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES);
          MI_MSGERROR(1).CLAVE := 'MES';
          MI_MSGERROR(1).VALOR := MI_NOMBREMES;
          PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_TASA_INT_VIG,
             UN_REEMPLAZOS => MI_MSGERROR
           );
          RETURN MI_RTA;
        END;
      ELSE
        BEGIN
          SELECT RECARGO 
            INTO MI_TASARECARGO
            FROM IP_ACUERDOS 
           WHERE COMPANIA = UN_COMPANIA
             AND CODIGOACUERDO = UN_CODIGOACUERDO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TASARECARGO := 0;
        END;
      END IF;

      MI_PERIODICIDADCOBRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                    UN_NOMBRE    => 'PERIODICIDAD DE COBRO INT. RECARGO',
                                                    UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                    UN_FECHA_PAR => SYSDATE);

      IF MI_PERIODICIDADCOBRO IS NULL
      OR MI_PERIODICIDADCOBRO = ''
      THEN
        MI_PERIODICIDADCOBRO := 'D';
      END IF;

      IF MI_PERIODICIDADCOBRO = 'M'
      THEN
        SELECT FLOOR(MONTHS_BETWEEN(TO_DATE(TRUNC(UN_FECHACORTE),'DD/MM/YYYY'),
               TO_DATE(TRUNC(RS_FACACT.FECHAFACTURADO),'DD/MM/YYYY'))) 
          INTO MI_TIEMPOMORA
          FROM DUAL;

        IF MI_TIEMPOMORA < 0 
        THEN
          MI_TIEMPOMORA := 0;
        END IF;

        IF UN_INTERESCOMPUESTO = 'SI'
        THEN
          MI_VALORRECARGO := ROUND((RS_FACACT.CAPITAL + RS_FACACT.INTERESES + RS_FACACT.INTERES_ACUERDO) * MI_TIEMPOMORA * MI_TASARECARGO, MI_DIGITOSREDONDEO);
        ELSE
          MI_VALORRECARGO := ROUND(RS_FACACT.CAPITAL * MI_TIEMPOMORA * MI_TASARECARGO, MI_DIGITOSREDONDEO);
        END IF;
      ELSE
        SELECT (TO_DATE(TRUNC(UN_FECHACORTE),'DD/MM/YYYY')- TO_DATE(TRUNC(RS_FACACT.FECHAFACTURADO),'DD/MM/YYYY'))
          INTO MI_TIEMPOMORA
          FROM DUAL;

        IF MI_TIEMPOMORA < 0 
        THEN
          MI_TIEMPOMORA := 0;
        END IF;

        IF UN_INTERESCOMPUESTO = 'SI'
        THEN
          MI_VALORRECARGO := ROUND((RS_FACACT.CAPITAL + RS_FACACT.INTERESES + RS_FACACT.INTERES_ACUERDO) * MI_TIEMPOMORA * (MI_TASARECARGO / 30), MI_DIGITOSREDONDEO);
        ELSE
          MI_VALORRECARGO := ROUND(RS_FACACT.CAPITAL * MI_TIEMPOMORA * (MI_TASARECARGO / 30), MI_DIGITOSREDONDEO);
        END IF;
      END IF;

      BEGIN 
        MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOSACUERDOS',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => 'INTERES_RECARGO = ' || NVL(MI_VALORRECARGO,0),
                                         UN_CONDICION => ' COMPANIA = ' ||  UN_COMPANIA ||
                                                        ' AND CODIGOACUERDO = ' ||  UN_CODIGOACUERDO  ||
                                                        ' AND CUOTA = ' ||  RS_FACACT.CUOTA  ||
                                                        ' AND PAGADO = 0');
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    END LOOP FACT_ACUERDOS;

    BEGIN     
      MI_RTAACME := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_FACTURADOSACUERDOS',
                                       UN_ACCION    => 'M',
                                       UN_CAMPOS    => 'TOTAL = (C1 + C2 + C3 + C4 + C5 + C6 + C7 + C8 + C9 + C10 + C11 + C12 + C13 + C14 + C15 + C16 + C17 + C18 + C19 + C20 + NVL(INTERES_ACUERDO,0) + INTERES_RECARGO)',
                                       UN_CONDICION => ' COMPANIA = ' ||  UN_COMPANIA ||
                                                        ' AND CODIGOACUERDO = ' ||  UN_CODIGOACUERDO  ||
                                                        ' AND PAGADO = 0');
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    IF MI_RTAACME IS NOT NULL
    THEN
      MI_RTA := -1;
    END IF;

    RETURN MI_RTA;

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PREDIAL THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD    => SQLCODE,
             UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACT_FACTACUERDO,
             UN_TABLAERROR => 'IP_FACTURADOSACUERDOS'
           );
    RETURN 0;
  END FC_CALCULARRECARGOSACUERDO;

  FUNCTION FC_CONSULTARTASAINTERESVIGENTE
    /*
      NAME              : FC_CONSULTARTASAINTERESVIGENTE En Access --> TasaInteresVigente() en FuncionesFormulacion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 07/02/2017
      TIME              : 05:30 PM
      SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
      DESCRIPTION       : Proceso que permite consultar la tasa de interés vigente.
      PARAMETERS        : UN_COMPANIA => Compañia de ingreso a la aplicación.

      @NAME:  consultarTasaInteresVigente
      @METHOD:  GET
      */
    (
      UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    )
  RETURN VARCHAR2 AS 
    MI_TASAINTERESVIGENTE     VARCHAR2(20 CHAR);
  BEGIN
    BEGIN 
      SELECT TASAMENSUAL 
        INTO MI_TASAINTERESVIGENTE
        FROM IP_TASASINTERES 
       WHERE COMPANIA = UN_COMPANIA
         AND ANO = EXTRACT(YEAR FROM SYSDATE) 
         AND MES = EXTRACT(MONTH FROM SYSDATE);
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TASAINTERESVIGENTE := 0;
    END;
  RETURN MI_TASAINTERESVIGENTE;

  END FC_CONSULTARTASAINTERESVIGENTE;

PROCEDURE PR_MANEJAPAGOANTICIPADO
  /*
  NAME              : FC_CALCULARRECARGOSACUERDO En Access --> CalcularRecargosAcuerdo
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 07/02/2017
  TIME              : 16:00 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL PredialP2016.05.06
  @NAME:  manejarPagoAnticipado
  @METHOD:  GET
  */
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGO_ACUERDO       IN IP_ACUERDOS.CODIGOACUERDO%TYPE,
  UN_ACUERDO              IN PCK_SUBTIPOS.TI_ENTERO,
  UN_RECARGO              IN PCK_SUBTIPOS.TI_ENTERO
)
  AS
  MI_PARAMETRO            VARCHAR2(3000 CHAR);
  MI_CUOTAM               VARCHAR2(3000 CHAR);
  MI_FECHAFAC             DATE;
  MI_MSGRETORNO           VARCHAR2(3000 CHAR);
  MI_INTERESCOMPUESTO     VARCHAR2(3000 CHAR);
  MI_FECHACORTE           DATE;
  MI_USUARIO              VARCHAR2(3000 CHAR);
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_CALCULO              PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  MI_FECHACORTE := SYSDATE;
  MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                        ,UN_NOMBRE    => 'MANEJA PAGOS ANTICIPADOS EN ACUERDOS'
                                        ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                        ,UN_FECHA_PAR => SYSDATE);

  MI_INTERESCOMPUESTO :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                ,UN_NOMBRE    => 'INTERES COMPUESTO - FINANCIACION - RECARGO'
                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                ,UN_FECHA_PAR => SYSDATE);                                       
  MI_INTERESCOMPUESTO := SUBSTR(MI_INTERESCOMPUESTO,1,2);

  IF MI_PARAMETRO = 'SI' THEN
    BEGIN
     BEGIN
        SELECT MAX(IP_FACTURADOSACUERDOS.CUOTA),
               MAX(IP_FACTURADOSACUERDOS.FECHAFACTURADO)
        INTO   MI_CUOTAM
              ,MI_FECHAFAC
        FROM   IP_FACTURADOSACUERDOS 
        WHERE  IP_FACTURADOSACUERDOS.CODIGOACUERDO = TO_NUMBER(UN_CODIGO_ACUERDO)
        AND ROWNUM = 1;

        IF MI_CUOTAM IS NULL AND MI_FECHAFAC IS NULL THEN 
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END IF;   
     END;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD     =>SQLCODE
                                 ,UN_ERROR_COD   =>PCK_ERRORES.ER_PREDIAL_CUOTA_FECHA
                                 ,UN_REEMPLAZOS  => MI_MSGERROR);
    END; 

    IF UN_ACUERDO = 1 THEN
      PCK_PREDIAL_FIN.PR_LIQUIDAINTERESACUERDO( UN_COMPANIA       => UN_COMPANIA
                                               ,UN_CODIGOACUERDO  => UN_CODIGO_ACUERDO);                                              
    ELSE
       PCK_PREDIAL_FIN.PR_REVIERTEINTERESPAGANT( UN_COMPANIA       => UN_COMPANIA
                                                ,UN_CODIGOACUERDO  => UN_CODIGO_ACUERDO);                                                    
    END IF;

    IF UN_RECARGO = 1 THEN
      PCK_PREDIAL_FIN.PR_LIQUIDAINTERESRECARGO( UN_COMPANIA       => UN_COMPANIA
                                               ,UN_CODIGOACUERDO  => UN_CODIGO_ACUERDO);                                          
    ELSE
      BEGIN
        MI_CALCULO := FC_CALCULARRECARGOSACUERDO(UN_COMPANIA         => UN_COMPANIA
                                                ,UN_CODIGOACUERDO    => UN_CODIGO_ACUERDO
                                                ,UN_INTERESCOMPUESTO => MI_INTERESCOMPUESTO
                                                ,UN_FECHACORTE       => MI_FECHACORTE);

        IF  MI_CALCULO = 0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END IF; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE
                                     ,UN_ERROR_COD => PCK_ERRORES.ER_PREDIAL_CALCULO_RECARGO);
      END;

    END IF;

   BEGIN
    BEGIN
      MI_CAMPOS := 'TOTAL = (C1 + C2 + C3 + C4 + C5 + C6 + C7 + C8 + C9 + C10 + C11 + C12 + C13 + C14 + C15 + C16 + C17 + C18 + C19 + C20 + INTERES_ACUERDO + INTERES_RECARGO)';                                     

      MI_CONDICION := 'CODIGOACUERDO = '''||UN_CODIGO_ACUERDO||'''';                                         

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA     => 'IP_FACTURADOSACUERDOS'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);                                             

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END ;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ER_UPDATE_FACTURADOSACUERDOS
                                );                                     

   END; 

  END IF;


END PR_MANEJAPAGOANTICIPADO;

FUNCTION FC_CREARACUERDO
  /*
  NAME              : FC_CREARACUERDO En Access --> CrearAcuerdo
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 08/02/2017
  TIME              : 14:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Función que se llama en Usuarios Predial / Botón Financiar - Previo
                      Funcion que se encarga de insertar los acuerdos en la tabla IP_ACUERDOS ó TMP_IP_ACUERDOS
                      ,además de actualizar las tablas IP_FACTURADOS,IP_USUARIOS_PREDIAL,IP_RECIBOS_DE_PAGO.


  @NAME:  crearAcuerdo
  @METHOD:  POST    
  */     
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NOMBRECOMPANIA    IN VARCHAR2, 
  UN_PREDIO            IN VARCHAR2,
  UN_PERIODO           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_NUMEROORDEN       IN VARCHAR2,
  UN_IDRES             IN VARCHAR2,
  UN_NOMBRERES         IN VARCHAR2,
  UN_DIRECCIONRES      IN VARCHAR2,
  UN_TELEFONORES       IN VARCHAR2,
  UN_NCUOTAS           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_INTERES           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_RECARGO           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_RESOLUCION        IN VARCHAR2,
  UN_USUARIO           IN VARCHAR2,
  UN_RECSOPORTE        IN VARCHAR2,
  UN_APLICADSCESP      IN PCK_SUBTIPOS.TI_LOGICO, 
  UN_PREELIMINAR       IN PCK_SUBTIPOS.TI_LOGICO,  
  UN_INDABONOINICIAL   IN PCK_SUBTIPOS.TI_LOGICO,  
  UN_VLRABONOINICIAL   IN VARCHAR2,
  UN_NITCOMPANIA       IN VARCHAR2,
  UN_ACUERDOPASTO      IN PCK_SUBTIPOS.TI_LOGICO 
)
RETURN NUMBER 
AS
  MI_TACUERDO           VARCHAR2(3000 CHAR);
  MI_TFACTURADOSACU     VARCHAR2(3000 CHAR);
  MI_TIPOINTERES        VARCHAR2(3000 CHAR);
  MI_INTERESCOMPUESTO   VARCHAR2(3000 CHAR);
  MI_RECSOPORTE         VARCHAR2(3000 CHAR);
  MI_MANINTCOMPUESTO    BOOLEAN;
  MI_RSFACTURADOS       SYS_REFCURSOR;
  MI_NACUERDO           PCK_SUBTIPOS.TI_ENTERO;
  MI_ULTIMOACUERDO      PCK_SUBTIPOS.TI_ENTERO;
  MI_CONCINTERESES      VARCHAR2(3000 CHAR);
  MI_CONCINTERESESCOPIA VARCHAR2(3000 CHAR);
  MI_STRSQL             VARCHAR2(3000 CHAR);
  MI_POSSEP             PCK_SUBTIPOS.TI_ENTERO;
  MI_CONCEPTO           PCK_SUBTIPOS.TI_ENTERO;
  MI_DBLMONTOINTERES    PCK_SUBTIPOS.TI_ENTERO;
  MI_DBLMONTOCAPITAL    PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_GRUPO              VARCHAR2(3000 CHAR);
  MI_RTA                PCK_SUBTIPOS.TI_ENTERO;
  MI_PARAMETROPREDIAL   VARCHAR2(3000 CHAR);
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ETAPA              VARCHAR2(1000 CHAR);
  MI_MANDSCESPECIALES   VARCHAR2(3000 CHAR);
  MI_FECHAPARAMETRO     VARCHAR2(3000 CHAR);
  MI_FECHAFINAL         DATE;
  MI_LLAMADOPOR         VARCHAR2(3000 CHAR); 
  MI_APLICADESC         BOOLEAN; 
  MI_PROYECCIONINICIAL  PCK_PREDIAL.TPROYECCION;
  MI_FECHALIMITE        DATE;
  MI_ANOMENOR           PCK_SUBTIPOS.TI_ENTERO;
  MI_ANOMAYOR           PCK_SUBTIPOS.TI_ENTERO;
  MI_ANOVALIDO          PCK_SUBTIPOS.TI_ENTERO;
  MI_TOTALFINANCIAR     PCK_SUBTIPOS.TI_ENTERO;
  MI_CONTEO             PCK_SUBTIPOS.TI_ENTERO;
  MI_PREANOF            PCK_SUBTIPOS.TI_ENTERO;
  MI_PREANOI            PCK_SUBTIPOS.TI_ENTERO;
  MI_PREVAL             PCK_SUBTIPOS.TI_ENTERO;
  MI_TOTALDESC          PCK_SUBTIPOS.TI_ENTERO;


BEGIN
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  MI_RECSOPORTE := UN_RECSOPORTE;
  MI_ETAPA := ' Etapa1: Cargando parámetros';
  MI_MANDSCESPECIALES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                ,UN_NOMBRE    => 'MANEJA OPCION DESCUENTOS ESPECIALES'
                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                ,UN_FECHA_PAR => SYSDATE); 

  MI_FECHAPARAMETRO  :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                ,UN_NOMBRE    => 'FECHA LIMITE DE APLICACION DESCUENTO ESPECIAL - ACUERDO DE PAGO'
                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                ,UN_FECHA_PAR => SYSDATE);   

  BEGIN
    MI_FECHALIMITE := TO_DATE(MI_FECHAPARAMETRO,'DD/MM/YYYY');  

    EXCEPTION WHEN OTHERS THEN
    MI_FECHALIMITE := NULL;  

  END;

  MI_RTA := 0;                         
  MI_LLAMADOPOR := 'Acuerdo';

  IF MI_MANDSCESPECIALES = 'SI' THEN

        MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'APLICAR DESCUENTOS EN ACUERDOS DE PAGO'
                                                    ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                    ,UN_FECHA_PAR => SYSDATE);
            IF MI_PARAMETROPREDIAL = 'NO' THEN
              MI_APLICADESC := FALSE;
             -- MI_APLICADESCESP := 'SI';

            ELSE  
              MI_APLICADESC := TRUE;
            --  MI_APLICADESCESP := 'SI';
            END IF; 

        MI_ETAPA := ' Etapa2: Proceso de cálculo';

       --MI_RTA := Calpred_Usuario; PENDIENTE

        MI_ETAPA := ' Etapa3: Proceso de cálculo de proyecciones';


   --SE ESTA EVALUANDO LA MIGRACIÓN DE ESTE BLOQUE DE ACCESS A WEB.
      /*  MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'CALCULO LEY 10/03/2015'
                                                    ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                    ,UN_FECHA_PAR => SYSDATE);

        IF  MI_PARAMETROPREDIAL = 'SI' THEN  --AND  NuevoValidaPredioProy(Forms!Usuarios_Predial!CODIGO) THEN                                             
         MI_PROYECCIONINICIAL := PCK_PREDIAL.FC_CALCULAR_PROYECCIONINICIAL(UN_COMPANIA => UN_COMPANIA,UN_CODPREDIO => UN_PREDIO,UN_ANOINICIAL => UN_ANOMENOR ,UN_ANOFINAL => UN_ANOMAYOR); 

         PCK_PREDIAL.PR_PRIMERAJUSTE_PROYECCION(UN_COMPANIA     => UN_COMPANIA
                                                ,UN_CODPREDIO   => UN_PREDIO
                                                ,UN_ANOINICIAL  => UN_ANOMENOR
                                                ,UN_INDLOTES    => BOOLEAN
                                                ,UN_INDVERIFICAR_AVALUO  => BOOLEAN
                                                ,UN_PROY_INICIAL  => MI_PROYECCIONINICIAL);     

        END IF; */


      /*  BEGIN
      IF MI_RTA = FALSE THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END IF;
      --AQUI EXCEPCION
    END;  */

        MI_ETAPA := ' Etapa4: Calculando fecha final del acuerdo';

        MI_FECHAFINAL := PCK_PREDIAL_FIN.FC_FECHAFINAL_ACUERDO(UN_COMPANIA     => UN_COMPANIA
                                              ,UN_FECHAACUERDO =>  SYSDATE
                                              ,UN_NCUOTAS      =>  UN_NCUOTAS
                                              ,UN_PERIODICIDAD => UN_PERIODO);  

      BEGIN  
       IF MI_FECHAFINAL >  CASE WHEN MI_FECHALIMITE IS NULL THEN MI_FECHAFINAL ELSE MI_FECHALIMITE END THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              MI_MSGERROR(1).CLAVE := 'FECHALIMITE';
              MI_MSGERROR(1).VALOR := MI_FECHALIMITE;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD =>SQLCODE
                       ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_CUOTAS_ALTAS
                       ,UN_REEMPLAZOS => MI_MSGERROR);
      END;  
  END IF;



  MI_LLAMADOPOR := '';

  MI_INTERESCOMPUESTO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'INTERES COMPUESTO - FINANCIACION - RECARGO'
                                                    ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                    ,UN_FECHA_PAR => SYSDATE);

  MI_INTERESCOMPUESTO := SUBSTR(MI_INTERESCOMPUESTO,1,2);   


  IF MI_INTERESCOMPUESTO NOT IN ('SI','NO')
  THEN
     BEGIN 
       BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

       END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD =>SQLCODE
                   ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_CONF_PARINTERESC
                    );
 END;
  END IF;


 BEGIN 
  IF UN_NCUOTAS = 0 OR UN_NCUOTAS IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD =>SQLCODE
                   ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_NUMERO_CUOTAS
                    );
 END;

 BEGIN
 IF UN_IDRES IS NOT NULL AND UN_NOMBRERES IS NOT NULL AND UN_DIRECCIONRES IS NOT NULL THEN
 BEGIN 
  IF UN_NCUOTAS IS NOT NULL --AND UN_INTERES IS NOT NULL AND UN_RECARGO IS NOT NULL 
  AND UN_TELEFONORES IS NOT NULL THEN

    BEGIN
      MI_STRSQL := 'SELECT    SUM(TOTAL) AS TOTALFINANCIAR
                    FROM      IP_FACTURADOS 
                    WHERE     CODIGO = '''||UN_PREDIO||''' 
                      AND NUMERO_ORDEN = ''001'' 
                      AND PAGADO = 0 
                      AND FINANCIAR <> 0 ';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_TOTALFINANCIAR;
        IF MI_TOTALFINANCIAR IS NULL THEN
          MI_TOTALFINANCIAR := 0;
        END IF;                 

    END;    


        MI_ETAPA := ' Etapa4.1: Consultando Facturados';
       BEGIN 
        MI_STRSQL := 'SELECT  MIN(PREANO) 
                      FROM    IP_FACTURADOS 
                      WHERE   CODIGO = '''||UN_PREDIO||''' 
                        AND NUMERO_ORDEN = ''001'' 
                        AND PAGADO = 0 
                        AND FINANCIAR <> 0';

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_ANOMENOR;
        IF MI_ANOMENOR IS NULL THEN
          MI_ANOMENOR := 0;
        END IF;  
       END;

       BEGIN 
        SELECT  MAX(PREANO) 
        INTO MI_ANOMAYOR
        FROM    IP_FACTURADOS 
        WHERE   CODIGO = UN_PREDIO 
          AND NUMERO_ORDEN = '001' 
          AND PAGADO = 0 
          AND FINANCIAR <> 0;
        IF MI_ANOMAYOR IS NULL THEN
          MI_ANOMAYOR := 0;
        END IF;  
       END; 

        MI_ETAPA := ' Etapa4.2: Consultando acuerdos';


        MI_STRSQL := 'SELECT  MAX(PREANO) AS ANO  
                      FROM    IP_ACUERDOS 
                      WHERE   PREDIO = '''||UN_PREDIO||'''  
                        AND  CANCELADO = 0 
                        AND ANULADO = 0';

         EXECUTE IMMEDIATE MI_STRSQL INTO MI_ANOVALIDO;   
         IF MI_ANOVALIDO IS NULL THEN

            MI_STRSQL := 'SELECT MIN(PREANO) AS ANO 
                          FROM   IP_FACTURADOS 
                          WHERE  CODIGO = '''||UN_PREDIO||''' 
                            AND NUMERO_ORDEN = ''001'' 
                            AND PAGADO = 0';

           EXECUTE IMMEDIATE MI_STRSQL INTO MI_ANOVALIDO; 

         END IF;



     IF UN_PREELIMINAR = 0 THEN

      MI_STRSQL := 'SELECT IP_RECIBOS_DE_PAGO.PREANOF
                          ,IP_RECIBOS_DE_PAGO.PREANOI
                          ,IP_RECIBOS_DE_PAGO.PREVAL PREVAL
                          ,IP_RECIBOS_DE_PAGO.C13 TOTALDESC  
                    FROM        IP_RECIBOS_DE_PAGO    
                    WHERE          IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                      AND  IP_RECIBOS_DE_PAGO.PRECOD = '''||UN_PREDIO||'''
                      AND  IP_RECIBOS_DE_PAGO.PAGO IN(0)     
                      AND  IP_RECIBOS_DE_PAGO.ANULADO IN(0)     
                      AND  IP_RECIBOS_DE_PAGO.ESABONO IN(0)     
                      AND  IP_RECIBOS_DE_PAGO.ESACUERDO IN(0)    
                      AND  IP_RECIBOS_DE_PAGO.ESCUOTA IN(0)     
                      AND  IP_RECIBOS_DE_PAGO.ACUERDO IS NULL';

       EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

        IF MI_CONTEO >=1 THEN           
          OPEN MI_RSFACTURADOS FOR MI_STRSQL;
            LOOP
              EXIT WHEN MI_RSFACTURADOS%NOTFOUND;
             FETCH MI_RSFACTURADOS INTO MI_PREANOF,MI_PREANOI,MI_PREVAL,MI_TOTALDESC; 


              MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                          ,UN_NOMBRE    => 'MANEJA CONTROL DE RECIBO PARA ACUERDOS DE PAGO'
                                                          ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                          ,UN_FECHA_PAR => SYSDATE); 
              IF  MI_PARAMETROPREDIAL = 'SI' THEN

                  IF MI_PREANOI <> MI_ANOMENOR OR MI_PREANOF <> MI_ANOMAYOR THEN
                   BEGIN
                      BEGIN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;          
                       END;    

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_RECNOVIGE); 

                    END;   
                  ELSE
                     BEGIN
                          IF MI_TOTALFINANCIAR <> NVL(MI_PREVAL,0) + ABS(MI_TOTALDESC) THEN 
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
                          END IF;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_TOTALFACTURA);   

                      END;     
                  END IF;            
                MI_RECSOPORTE := UN_RECSOPORTE;             

              ELSE 
              --Si cumple las validaciones de vigencias y valor total se asigna el numero de factura a la variable que se enviara a la función de creación del acuerdo de pago
                MI_RECSOPORTE := '999999999';
              END IF;


           END LOOP;   
        END IF;
      END IF;




       MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'PERMITE FINANCIAR POR VIGENCIA NO ACUMULADA'
                                                    ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                    ,UN_FECHA_PAR => SYSDATE);  


     BEGIN  
       IF (MI_ANOMENOR >  MI_ANOVALIDO +1  AND UN_NITCOMPANIA NOT IN ('8912800003') AND UN_NITCOMPANIA NOT IN ('891.808.260-0')) AND MI_PARAMETROPREDIAL = 'NO' THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

       ELSE
       BEGIN
        IF MI_ANOMENOR <> 0 AND MI_ANOMAYOR <> 0 THEN

          MI_LLAMADOPOR := 'Financiar';

          MI_ETAPA := ' Etapa5: Creando acuerdo de pago';

             SELECT MAX(CODIGOACUERDO) 
              INTO MI_ULTIMOACUERDO
              FROM IP_ACUERDOS; 

             IF MI_ULTIMOACUERDO IS NOT NULL THEN
                  MI_NACUERDO := NVL(MI_ULTIMOACUERDO,0)+1;

                  ELSE
                  MI_NACUERDO := 1;
                  END IF; 

              IF UN_PREELIMINAR <> 0 THEN
                  MI_TACUERDO       := 'IP_TMP_ACUERDOS';
                  MI_TFACTURADOSACU := 'IP_TMP_FACTURADOSACUERDOS';


                MI_CONDICION := 'CODIGOACUERDO = '''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>   MI_NACUERDO
                                                                      ,UN_LONGITUD => 10)||'''';
                                --AND PREDIO = '||UN_PREDIO||'';
                  BEGIN
                   BEGIN
                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TFACTURADOSACU
                                        ,UN_ACCION  => 'E'
                                        ,UN_CONDICION => MI_CONDICION);                                                           

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                     END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DELETE_FACUERDO
                                                    );

                    END;                                                     

                 BEGIN
                  BEGIN
                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TACUERDO
                                                          ,UN_ACCION  => 'E'
                                                          ,UN_CONDICION => MI_CONDICION);  
                                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                   END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DELETE_ACUERDO
                                                    );

                 END;                                           


              ELSE      
                  MI_TACUERDO       := 'IP_ACUERDOS';
                  MI_TFACTURADOSACU := 'IP_FACTURADOSACUERDOS';

              END IF;    

              MI_TIPOINTERES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA         => UN_COMPANIA
                                                            ,UN_NOMBRE    => 'TIPO FECHA AL DIA PARA ABONOS'
                                                            ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                            ,UN_FECHA_PAR => SYSDATE); 

              IF MI_TIPOINTERES = 'INTERESCOMPUESTO' THEN
                MI_MANINTCOMPUESTO := TRUE;
              ELSE
                MI_MANINTCOMPUESTO:= FALSE;
              END IF;




                  MI_CONCINTERESES := NVL((PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                                ,UN_NOMBRE    => 'ACUERDOS DE PAGO - CONCEPTOS INTERESES'
                                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                                ,UN_FECHA_PAR => SYSDATE)) ,'2,4');   


                   MI_CONCINTERESES := REPLACE(REPLACE(MI_CONCINTERESES, ' ',''),';',',');
                   MI_CONCINTERESESCOPIA := MI_CONCINTERESES;


                  MI_STRSQL := '';

                  WHILE LENGTH(MI_CONCINTERESESCOPIA) <> 0
                      LOOP

                      MI_POSSEP := INSTR(MI_CONCINTERESESCOPIA,',',1);

                          IF MI_POSSEP = 0 THEN 
                            MI_CONCEPTO := MI_CONCINTERESESCOPIA;
                            MI_CONCINTERESESCOPIA := '';
                          ELSE
                            MI_CONCEPTO := SUBSTR(MI_CONCINTERESESCOPIA,1,INSTR(MI_CONCINTERESESCOPIA,',',1)-1);
                            MI_CONCINTERESESCOPIA := SUBSTR(MI_CONCINTERESESCOPIA,INSTR(MI_CONCINTERESESCOPIA,',',1)+1,LENGTH(MI_CONCINTERESESCOPIA));
                          END IF;

                        MI_STRSQL := ''||MI_STRSQL|| ' IP_FACTURADOS.C'||MI_CONCEPTO||' +' ;   
                  END LOOP;

                  IF MI_STRSQL IS NOT NULL THEN

                     MI_STRSQL := 'SUM('||SUBSTR(MI_STRSQL,1,LENGTH(MI_STRSQL)-1)||')';

                  ELSE
                    MI_STRSQL := '0';

                  END IF;

                  MI_STRSQL := 'SELECT SUM(IP_FACTURADOS.TOTAL) - '||MI_STRSQL|| '   AS CAPITAL , '||MI_STRSQL|| ' AS INTERESES ';

                  MI_STRSQL := ''||MI_STRSQL|| ' 
                                FROM  IP_FACTURADOS 
                                WHERE CODIGO IN('''||UN_PREDIO||''') 
                                  AND PREANO BETWEEN '||MI_ANOMENOR ||' AND '||MI_ANOMAYOR||' 
                                  AND PAGADO = 0 AND FINANCIAR <> 0';

                   EXECUTE IMMEDIATE MI_STRSQL INTO MI_DBLMONTOCAPITAL , MI_DBLMONTOINTERES;             

                  BEGIN
                   BEGIN 

                         MI_CAMPOS := 'COMPANIA
                                      , CODIGOACUERDO
                                      , PREDIO
                                      , NUMERO_ORDEN
                                      , ID_RESP
                                      , NOMBRE_RESP
                                      , DIRECCION_RESP
                                      , TELEFONO_RESP
                                      , MONTOCAPITAL
                                      , MONTOINTERESES
                                      , NCUOTAS
                                      , INTERES
                                      , RECARGO
                                      , FECHAACUERDO
                                      , CANCELADO
                                      , PREANOI
                                      , PREANO
                                      , RESOLUCION
                                      , ANULADO
                                      , CREATED_BY  
                                      , RECIBO_SOPORTE
                                      , APLICA_DSCESP';

                         MI_VALORES := ''''||UN_COMPANIA||'''
                                        , '''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>   MI_NACUERDO
                                                                      ,UN_LONGITUD => 10)||'''
                                        , '''||UN_PREDIO||'''
                                        , '''||UN_NUMEROORDEN||'''
                                        , '''||UN_IDRES||''' 
                                        , '''||UN_NOMBRERES||'''
                                        , '''||UN_DIRECCIONRES||''' 
                                        , '''||UN_TELEFONORES||''' 
                                        , '||NVL(MI_DBLMONTOCAPITAL, 0)||'
                                        , '||NVL(MI_DBLMONTOINTERES, 0)||'
                                        , '||UN_NCUOTAS||'
                                        , '||UN_INTERES||'
                                        , '||UN_RECARGO||'
                                        , '''||TO_DATE(SYSDATE, 'DD/MM/YYYY')||''' 
                                        , 0
                                        , '||MI_ANOMENOR||' 
                                        , '||MI_ANOMAYOR||' 
                                        , '''||UN_RESOLUCION||'''
                                        , 0
                                        , '''||UN_USUARIO||''' 
                                        , '''||MI_RECSOPORTE||''' 
                                        , '||UN_APLICADSCESP||'';

                         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TACUERDO
                                                                ,UN_ACCION  => 'I'
                                                                ,UN_CAMPOS  => MI_CAMPOS
                                                                ,UN_VALORES => MI_VALORES);  

                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                     END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                MI_MSGERROR(1).CLAVE := 'TACUERDO';
                                MI_MSGERROR(1).VALOR := MI_TACUERDO;
                                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TACUERDO
                                                    ,UN_REEMPLAZOS => MI_MSGERROR);

                    END;   

              IF  UN_PREELIMINAR = 0 THEN

                MI_CAMPOS := 'INDPAGO_ACPAG = -1
                              , OBSERVACIONES = ''AP '||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO =>   MI_NACUERDO
                                                                               ,UN_LONGITUD => 10)||'''';

                MI_CONDICION := 'PREANO BETWEEN '||MI_ANOMENOR||' AND '||MI_ANOMAYOR||'
                            AND CODIGO = '''||UN_PREDIO||'''';


                BEGIN
                 BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'IP_FACTURADOS'
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                 END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_IPFACTURADOS);

               END;  
               BEGIN
                  BEGIN                                        
                      MI_CAMPOS := 'PAGO_ACUERDO= -1';

                      MI_CONDICION := 'CODIGO = '''||UN_PREDIO||'''';


                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                                             ,UN_ACCION    => 'M'
                                                             ,UN_CAMPOS    => MI_CAMPOS
                                                             ,UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
                  END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_UPDATE_IPUSPRE);

               END;                                           



                  --Se debe incluir en el recibo de soporte el codigo del acuerdo de pago al cual se relaciona, se hace mientras el recibo de soporte sea diferente de 999999999
                    IF MI_RECSOPORTE <> '999999999' Then

                      BEGIN
                        BEGIN
                          MI_CAMPOS := 'ACUERDO='''||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>   MI_NACUERDO
                                                                        ,UN_LONGITUD => 10)||'''';

                          MI_CONDICION := 'DOCNUM = '''||MI_RECSOPORTE||'''';


                          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'IP_RECIBOS_DE_PAGO'
                                                                 ,UN_ACCION    => 'M'
                                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                                 ,UN_CONDICION => MI_CONDICION); 

                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
                        END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_RECIBOPAGO);


                      END;                                                       
                    END IF;

              END IF;

                MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                            ,UN_NOMBRE    => 'TRABAJA CUOTAS ACUERDO SEGUN VIGENCIAS ADEUDADAS'
                                                            ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                            ,UN_FECHA_PAR => SYSDATE); 
              IF MI_PARAMETROPREDIAL = 'SI' THEN 

                PCK_PREDIAL_FIN.PR_CALCULARACUERDOVIG(UN_COMPANIA         => UN_COMPANIA 
                                                    ,UN_ACUERDO         => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO =>   MI_NACUERDO
                                                                                                    ,UN_LONGITUD => 10)
                                                    ,UN_PERIODO         => UN_PERIODO
                                                    ,UN_T_ACUERDO       => MI_TACUERDO
                                                    ,UN_T_FACTURADOSACU => MI_TFACTURADOSACU
                                                    ,UN_USUARIO         => UN_USUARIO);


              ELSE                                          
                PCK_PREDIAL_FIN.PR_CALCULARACUERDO(UN_COMPANIA          => UN_COMPANIA 
                                                   ,UN_ACUERDO          => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>   MI_NACUERDO
                                                                                         ,UN_LONGITUD => 10 )
                                                   ,UN_PERIODO          => UN_PERIODO
                                                   ,UN_TACUERDO         => MI_TACUERDO 
                                                   ,UN_TFACTURADOSACU   => MI_TFACTURADOSACU 
                                                   ,UN_INTERESCOMPUESTO => MI_INTERESCOMPUESTO  
                                                   ,UN_LLAMADOPOR       => MI_LLAMADOPOR 
                                                   ,UN_INDABONOINICIAL  => UN_INDABONOINICIAL
                                                   ,UN_VLRABONOINICIAL  => UN_VLRABONOINICIAL
                                                   ,UN_NITCOMPANIA      => UN_NITCOMPANIA
                                                   ,UN_ACUERDOPASTO     => UN_ACUERDOPASTO
                                                   ,UN_ANOMENOR         => MI_ANOMENOR
                                                   ,UN_ANOMAYOR         => MI_ANOMAYOR);

                    MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                            ,UN_NOMBRE    => 'DISTRIBUCION DE ACUERDO POR PRORATEO (R), PRIORIDAD (P)'
                                                            ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                            ,UN_FECHA_PAR => SYSDATE); 



                 IF MI_PARAMETROPREDIAL = 'R' THEN
                    MI_PARAMETROPREDIAL:= 'DESARROLLANDO';

                   PCK_PREDIAL_FIN.PR_DISTRIBUIRACUERDO(UN_COMPANIA        => UN_COMPANIA,
                                                        UN_PREDIO          => UN_PREDIO, 
                                                        UN_ACUERDO         => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NACUERDO,
                                                                                                          UN_LONGITUD => 10), 
                                                        UN_PREANOI         => MI_ANOMENOR, 
                                                        UN_PREANO          => MI_ANOMAYOR,
                                                        UN_T_FACTURADOSACU => MI_TFACTURADOSACU);


                  ELSE 
                    IF UN_NOMBRECOMPANIA = 'ALCALDIA DE FUSAGASUGA' THEN




                     PCK_PREDIAL_FIN.PR_DISTRIBUIRACUERDO(UN_COMPANIA        => UN_COMPANIA,
                                    UN_PREDIO          => UN_PREDIO, 
                                    UN_ACUERDO         => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NACUERDO,
                                                                                      UN_LONGITUD => 10), 
                                    UN_PREANOI         => MI_ANOMENOR, 
                                    UN_PREANO          => MI_ANOMAYOR,
                                    UN_T_FACTURADOSACU => MI_TFACTURADOSACU);
                    ELSE IF UN_NOMBRECOMPANIA = 'MUNICIPIO DE ACACIAS' THEN  

                     PCK_PREDIAL_COM6.PR_DISTRIBUIRACUERDOACACIAS(UN_COMPANIA       => UN_COMPANIA,
                                                                  UN_CODIGOPREDIO   => UN_PREDIO,
                                                                  UN_CODIGOACUERDO  => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NACUERDO,
                                                                                                                 UN_LONGITUD => 10), 
                                                                  UN_PREANOI         => MI_ANOMENOR, 
                                                                  UN_PREANO          => MI_ANOMAYOR,
                                                                  UN_TABLA           => MI_TFACTURADOSACU);
                    ELSE 
                         MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                        ,UN_NOMBRE    => 'DISTRIBUCION DE ACUERDO POR PRORATEO (R), PRIORIDAD (P)'
                                                        ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                        ,UN_FECHA_PAR => SYSDATE); 
                      IF MI_PARAMETROPREDIAL = 'R' THEN




                     PCK_PREDIAL_FIN.PR_DISTRIBUIRACUERDO(UN_COMPANIA        => UN_COMPANIA,
                                    UN_PREDIO          => UN_PREDIO, 
                                    UN_ACUERDO         => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NACUERDO,
                                                                                      UN_LONGITUD => 10), 
                                    UN_PREANOI         => MI_ANOMENOR, 
                                    UN_PREANO          => MI_ANOMAYOR,
                                    UN_T_FACTURADOSACU => MI_TFACTURADOSACU);
                      ELSE 



                     PCK_PREDIAL_FIN.PR_DISTRIBUIRACUERDO(UN_COMPANIA        => UN_COMPANIA,
                                    UN_PREDIO          => UN_PREDIO, 
                                    UN_ACUERDO         => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NACUERDO,
                                                                                      UN_LONGITUD => 10), 
                                    UN_PREANOI         => MI_ANOMENOR, 
                                    UN_PREANO          => MI_ANOMAYOR,
                                    UN_T_FACTURADOSACU => MI_TFACTURADOSACU);     

                      END IF;

                    END IF;
                    END IF;

                 END IF;


                  MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                ,UN_NOMBRE    => 'DISTRIBUIR INTERES Y CAPITAL DE ACUERDO DESDE EL DETALLE'
                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                ,UN_FECHA_PAR => SYSDATE); 

                  IF MI_PARAMETROPREDIAL  = 'SI' THEN

                            PCK_PREDIAL_FIN.PR_RECALCULAR_CAPITAL_AC(UN_COMPANIA         => UN_COMPANIA
                                                                     ,UN_CODIGOACUERDO   =>  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO =>   MI_NACUERDO
                                                                                                    ,UN_LONGITUD => 10)
                                                                     ,UN_PERIODO         => UN_PERIODO
                                                                     ,UN_TACUERDO        => MI_TACUERDO
                                                                     ,UN_TFACTURADOSACU  => MI_TFACTURADOSACU
                                                                     ,UN_MANEJACOMPUESTO => MI_MANINTCOMPUESTO);   

                  END IF;
              END IF; 

               MI_RTA := 1;
           BEGIN                     
            IF MI_RTA = 1 THEN 

              RETURN MI_RTA;
            ELSE
                     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END IF;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        MI_MSGERROR(1).CLAVE := 'ETAPA';
                        MI_MSGERROR(1).VALOR := MI_ETAPA;
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD =>SQLCODE
                                     ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_CREACIONAPAGO
                                     ,UN_REEMPLAZOS => MI_MSGERROR);


           END;     

        ELSE

            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END IF;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                MI_MSGERROR(1).CLAVE := 'ETAPA';
                MI_MSGERROR(1).VALOR := MI_ETAPA;
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE
                             ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_SELVIGENCIA
                             ,UN_REEMPLAZOS => MI_MSGERROR);


       END;


       END IF;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                MI_MSGERROR(1).CLAVE := 'ANOVALIDO';
                MI_MSGERROR(1).VALOR := MI_ANOVALIDO;
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE
                             ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_ANIOVALIDO
                             ,UN_REEMPLAZOS => MI_MSGERROR);
     END;

  ELSE
     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END IF;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                MI_MSGERROR(1).CLAVE := 'ETAPA';
                MI_MSGERROR(1).VALOR := MI_ETAPA;
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE
                             ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DATOSACPAGO
                             ,UN_REEMPLAZOS => MI_MSGERROR);

 END;

  ELSE
     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END IF;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                MI_MSGERROR(1).CLAVE := 'ETAPA';
                MI_MSGERROR(1).VALOR := MI_ETAPA;
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE
                             ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DATOSRESPACPAGO
                             ,UN_REEMPLAZOS => MI_MSGERROR);
 END;
 RETURN MI_RTA;
END FC_CREARACUERDO;

PROCEDURE PR_CALCULARACUERDO
  /*
  NAME              : PR_CALCULARACUERD En Access --> CalcularAcuerdo
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 09/02/2017
  TIME              : 10:22 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Procedimiento llamado por la función PCK_PREDIAL_FIN.FC_CREARACUERDO
                      Prcedimiento que se encarga de calcular los acuerdo creados mediante el uso de las tablas temporales
                      IP_TMP_AP_CUOTAS,y otras enviadas cómo parámetros.


  @NAME:  calcularAcuerdo
  @METHOD:  POST   
  */ 
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ACUERDO           IN VARCHAR2,
  UN_PERIODO           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TACUERDO          IN VARCHAR2,
  UN_TFACTURADOSACU    IN VARCHAR2,
  UN_INTERESCOMPUESTO  IN VARCHAR2,
  UN_LLAMADOPOR        IN VARCHAR2,
  UN_INDABONOINICIAL   IN PCK_SUBTIPOS.TI_LOGICO, 
  UN_VLRABONOINICIAL   IN VARCHAR2,
  UN_NITCOMPANIA       IN VARCHAR2,
  UN_ACUERDOPASTO      IN PCK_SUBTIPOS.TI_LOGICO, 
  UN_ANOMENOR          IN PCK_SUBTIPOS.TI_ENTERO,
  UN_ANOMAYOR          IN PCK_SUBTIPOS.TI_ENTERO
)AS
  MI_ABONOINICIAL      PCK_SUBTIPOS.TI_LOGICO;
  MI_VALORABONOINICIAL VARCHAR2(3000 CHAR);
  MI_STRSQL            VARCHAR2(3000 CHAR);
  MI_RSTMP             SYS_REFCURSOR;
  MI_RSFACTURADOS      SYS_REFCURSOR;
  MI_RSACUERDOS        SYS_REFCURSOR;
  MI_C20               PCK_SUBTIPOS.TI_ENTERO;
  MI_DIGITOSREDONDEO   PCK_SUBTIPOS.TI_ENTERO;
  MI_CANCELADO         PCK_SUBTIPOS.TI_ENTERO;
  MI_TOTAL             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CAPITAL           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERESES         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_ACINTERES         NUMBER(9,6);
  MI_INTERESACUERDO    PCK_SUBTIPOS.TI_DOBLE;
  MI_INTERESRECARGO    PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAFACTURADO    DATE;
  MI_FECHAFACTURA      DATE; 
  MI_CODIGOACUERDO     PCK_SUBTIPOS.TI_ENTERO;
  MI_PREDIO            VARCHAR2(3000 CHAR);
  MI_NUMEROORDEN       VARCHAR2(3000 CHAR);
  MI_CUOTA             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PARAMETROPREDIAL  VARCHAR2(3000 CHAR);
  MI_TASAVIGENTE       PCK_SUBTIPOS.TI_PORCENTAJE;
  MI_DIASDIF           PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES; 
  MI_INTERESERECARGO   PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CONTEO            PCK_SUBTIPOS.TI_ENTERO;
  MI_MONTOCAPITAL      PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MONTOINTERESES    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TCAPITAL          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MCAPITAL          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TINTERESES        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_MINTERESES        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_RESULCAP          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_RESIDCAP          PCK_SUBTIPOS.TI_ENTERO_LARGO; 
  MI_NCUOTAS           PCK_SUBTIPOS.TI_ENTERO;
  MI_RESULINT          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_RESIDINT          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTCOMPUESTO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TOTALINTERESES    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERES_ACUERDO   PCK_SUBTIPOS.TI_ENTERO_LARGO;   
  MI_CAPACTUAL         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTACTUAL         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_ID                PCK_SUBTIPOS.TI_ENTERO;   
  MI_INTERES           VARCHAR2(3000 CHAR);
  MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN  
EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';

  MI_NUMEROORDEN := '001';
  IF UN_LLAMADOPOR ='Financiar' THEN
    MI_ABONOINICIAL := NVL(UN_INDABONOINICIAL,0);
    MI_VALORABONOINICIAL := NVL(UN_VLRABONOINICIAL,0);

    ELSE IF UN_LLAMADOPOR = 'Calcular' THEN 
  BEGIN
      MI_STRSQL := 'SELECT C20
                    FROM  '||UN_TFACTURADOSACU||'
                    WHERE CODIGOACUERDO IN('||UN_ACUERDO||') 
                      AND CUOTA IN(0)';

     EXECUTE IMMEDIATE MI_STRSQL INTO MI_C20;

     EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_ABONOINICIAL := 0;
         MI_VALORABONOINICIAL := 0;

   END;       
      IF MI_C20 IS NOT NULL THEN
          MI_ABONOINICIAL := 1;
          MI_VALORABONOINICIAL := MI_C20;


      END IF;
    END IF;
  END IF;


       MI_CONDICION := 'CODIGOACUERDO = '||UN_ACUERDO||'';
      BEGIN
       BEGIN
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_TMPCUOTAS'
                            ,UN_ACCION  => 'E'
                            ,UN_CONDICION => MI_CONDICION);                                                           

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

         END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               MI_MSGERROR(1).CLAVE := 'ACUERDO';
               MI_MSGERROR(1).VALOR := UN_ACUERDO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DELETE_TMPCUOTAS
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;    
   MI_DIGITOSREDONDEO := 0;
   BEGIN
    IF UN_ACUERDO IS NULL THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    ELSE
     BEGIN
      MI_STRSQL := ' SELECT CANCELADO 
                     FROM '||UN_TACUERDO||' 
                     WHERE CODIGOACUERDO IN('''||UN_ACUERDO||''')';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CANCELADO; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
        BEGIN

          IF MI_CANCELADO = -1 THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

          ELSE    

          MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                      ,UN_NOMBRE    => 'ACUERDOS PAGO - USAR TASA VIGENTE'
                                                      ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                      ,UN_FECHA_PAR => SYSDATE); 

                IF MI_PARAMETROPREDIAL = 'SI' THEN
                      MI_TASAVIGENTE := FC_TASAINTERESVIGENTE();
                ELSE

                  MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                              ,UN_NOMBRE    => 'tasa de interes vigente'
                                                              ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                              ,UN_FECHA_PAR => SYSDATE);


                  IF REGEXP_COUNT(MI_PARAMETROPREDIAL,'[^0-9]') > 1 THEN
                    BEGIN
                      BEGIN 

                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 

                      END;  

                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_FORMATOPARAM
                                                 );


                    END;   
                  END IF; 

                MI_TASAVIGENTE := ROUND((TO_NUMBER(MI_PARAMETROPREDIAL))/ 30,6); 



                 END IF;      

           MI_STRSQL := ' SELECT PREDIO
                                 , CUOTA
                                 , TOTAL
                                 , CAPITAL
                                 , INTERESES
                                 , INTERES_ACUERDO
                                 , INTERES_RECARGO
                                 , FECHAFACTURADO 
                          FROM '||UN_TFACTURADOSACU||' 
                          WHERE CODIGOACUERDO IN('''||UN_ACUERDO||''')
                            AND PAGADO        = 0' ;

          EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

          IF MI_CONTEO >=1 THEN           
            OPEN MI_RSFACTURADOS FOR MI_STRSQL;
              LOOP

                 FETCH MI_RSFACTURADOS INTO MI_PREDIO,MI_CUOTA,MI_TOTAL, MI_CAPITAL, MI_INTERESES, MI_INTERESACUERDO, MI_INTERESRECARGO, MI_FECHAFACTURADO; 
                  EXIT WHEN MI_RSFACTURADOS%NOTFOUND;

                  MI_DIASDIF := SYSDATE - MI_FECHAFACTURADO;

                 IF MI_DIASDIF > 0 THEN

                  MI_INTERESERECARGO := ROUND(MI_CAPITAL * MI_TASAVIGENTE * MI_DIASDIF , MI_DIGITOSREDONDEO);

                  MI_TOTAL := MI_CAPITAL + MI_INTERESES + MI_INTERESACUERDO + MI_INTERESRECARGO;

                  MI_CAMPOS := 'INTERES_RECARGO = '''||MI_INTERESERECARGO||'''
                              ,TOTAL = '''||MI_TOTAL||'''';

                  MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA||'''
                                  AND CODIGOACUERDO  = '''||UN_ACUERDO||'''
                                  AND PREDIO         = '||MI_PREDIO||'
                                  AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||'''
                                  AND CUOTA          = '''||MI_CUOTA||'''';


                  BEGIN
                   BEGIN
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TFACTURADOSACU
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END ;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                              MI_MSGERROR(1).CLAVE := 'TABLA';
                              MI_MSGERROR(1).VALOR := UN_TFACTURADOSACU;
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                            UN_EXC_COD =>SQLCODE
                                                            ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_FACTURACU
                                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END; 
                 END IF;




            END LOOP;

          ELSE
                MI_STRSQL := ' SELECT PREDIO
                                      ,MONTOCAPITAL
                                      ,MONTOINTERESES
                                      ,NCUOTAS
                                      ,INTERES
                               FROM '||UN_TACUERDO||' 
                               WHERE CODIGOACUERDO IN('''||UN_ACUERDO||''')'; 

                     EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

                      MI_CONDICION := 'CODIGOACUERDO = '||UN_ACUERDO||'';

                      BEGIN

                       BEGIN
                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_TMP_AP_CUOTAS'
                                            ,UN_ACCION  => 'E'
                                            ,UN_CONDICION => MI_CONDICION);                                                           

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                         END;

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                               MI_MSGERROR(1).CLAVE := 'ACUERDO';
                               MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                                        UN_EXC_COD =>SQLCODE
                                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DELETE_TMPCUOTAS
                                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

                        END; 

                     IF MI_CONTEO >=1 THEN   

                          OPEN MI_RSACUERDOS FOR MI_STRSQL;
                          LOOP

                           FETCH MI_RSACUERDOS INTO MI_PREDIO,MI_MONTOCAPITAL,MI_MONTOINTERESES,MI_NCUOTAS,MI_ACINTERES; 

                            EXIT WHEN MI_RSACUERDOS%NOTFOUND;    

                                  IF UN_INDABONOINICIAL <> 0 AND UN_VLRABONOINICIAL > 0 THEN

                                    IF UN_VLRABONOINICIAL >= (MI_MONTOCAPITAL + MI_MONTOINTERESES) THEN
                                    BEGIN
                                     BEGIN
                                       RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

                                     END;
                                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                   UN_EXC_COD =>SQLCODE
                                                   ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_VALORABONO);
                                    END; 
                                    END IF; 

                                    MI_CAMPOS:='COMPANIA
                                                ,CODIGOACUERDO
                                                ,ID
                                                ,CAPITAL
                                                ,INTERESES
                                                ,INTERES_ACUERDO
                                                ,CUOTA';

                                    MI_CAPITAL := ROUND(UN_VLRABONOINICIAL * (MI_MONTOCAPITAL/(MI_MONTOCAPITAL + MI_MONTOINTERESES)),0);

                                    MI_INTERES := ROUND(UN_VLRABONOINICIAL * (MI_MONTOINTERESES/(MI_MONTOCAPITAL + MI_MONTOINTERESES)),0);

                                    MI_CUOTA := ROUND(UN_VLRABONOINICIAL * (MI_MONTOCAPITAL/(MI_MONTOCAPITAL + MI_MONTOINTERESES)),0) + ROUND(UN_VLRABONOINICIAL * (MI_MONTOINTERESES/(MI_MONTOCAPITAL + MI_MONTOINTERESES)),0);

                                    MI_VALORES :=''''||UN_COMPANIA||'''
                                                  ,'''||UN_ACUERDO||'''
                                                  ,0
                                                  ,'''||MI_CAPITAL||'''
                                                  ,'''||MI_INTERES||'''
                                                  ,0
                                                  ,'''||MI_CUOTA||'''';

                                     BEGIN
                                      BEGIN

                                         PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'IP_TMP_AP_CUOTAS'
                                                                                 ,UN_ACCION  => 'I'
                                                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                                                 ,UN_VALORES => MI_VALORES);  

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                      END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                  MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                  MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                     END; 


                                  MI_TCAPITAL   := MI_MONTOCAPITAL - (UN_VLRABONOINICIAL * (MI_MONTOCAPITAL / (MI_MONTOCAPITAL + MI_MONTOINTERESES)));
                                  MI_TINTERESES := MI_MONTOINTERESES - (UN_VLRABONOINICIAL * (MI_MONTOINTERESES / (MI_MONTOCAPITAL + MI_MONTOINTERESES)));

                                  ELSE
                                     MI_TCAPITAL   := MI_MONTOCAPITAL;  
                                     MI_TINTERESES := MI_MONTOINTERESES;

                                  END IF;

                                  MI_RESULCAP := ROUND((MI_TCAPITAL/MI_NCUOTAS),MI_DIGITOSREDONDEO);
                                  MI_MCAPITAL := MI_RESULCAP * MI_NCUOTAS;

                                 -- ESTA EXPRESION ESTARIA HACIENDO LO MISMO EN AMBOS CASOS 
                                 -- IF MI_MCAPITAL > MI_TCAPITAL THEN

                                    MI_MCAPITAL := MI_RESULCAP * MI_NCUOTAS;
                                    MI_RESIDCAP := MI_TCAPITAL -  MI_MCAPITAL;

                                 /* ELSE IF  MI_MCAPITAL < MI_TCAPITAL THEN 
                                    MI_RESIDCAP := MI_TCAPITAL -  MI_MCAPITAL;
                                  END IF;  
                                  END IF;*/  

                                  MI_RESULINT   := ROUND((MI_TINTERESES / MI_NCUOTAS),MI_DIGITOSREDONDEO);
                                  MI_MINTERESES := MI_RESULINT * MI_NCUOTAS;

                                -- ESTA EXPRESION ESTARIA HACIENDO LO MISMO EN AMBOS CASOS 
                                --  IF MI_MINTERESES > MI_TINTERESES THEN 

                                   MI_RESIDINT := MI_TINTERESES - MI_MINTERESES;

                                --  ELSE IF  MI_MINTERESES < MI_TINTERESES THEN
                                --  MI_RESIDINT := MI_TINTERESES - MI_MINTERESES;

                                --  END IF;
                                --  END IF;

                                  <<INSERTAR_IP_TMP_AP_CUOTAS>>
                                    FOR NCUOTA IN 1 .. MI_NCUOTAS LOOP

                                        IF UN_INTERESCOMPUESTO  = 'SI' THEN
                                          IF NCUOTA = MI_NCUOTAS THEN

                                            MI_CAPITAL := ROUND(MI_RESULCAP + MI_RESIDCAP, MI_DIGITOSREDONDEO);

                                            MI_INTERESES := ROUND(MI_RESULINT + NVL(MI_RESIDINT,0),MI_DIGITOSREDONDEO);

                                            MI_INTERES_ACUERDO := ROUND((MI_TCAPITAL + MI_TINTERESES)* NVL(MI_ACINTERES,0) * UN_PERIODO,MI_DIGITOSREDONDEO);

                                            MI_INTCOMPUESTO := NVL(MI_INTCOMPUESTO,0) + MI_INTERES_ACUERDO;

                                            MI_TOTALINTERESES := ROUND(NVL(MI_TOTALINTERESES,0) + MI_INTERES_ACUERDO, MI_DIGITOSREDONDEO );

                                            MI_CUOTA := ROUND(MI_CAPITAL + MI_INTERESES + MI_INTERES_ACUERDO ,MI_DIGITOSREDONDEO);

                                            MI_CAPACTUAL    := (MI_TCAPITAL - MI_CAPITAL) + MI_INTCOMPUESTO;

                                            MI_INTACTUAL    := (MI_TINTERESES - MI_INTERESES);

                                            BEGIN
                                             BEGIN

                                             MI_CAMPOS:='COMPANIA
                                                        ,CODIGOACUERDO
                                                        ,ID
                                                        ,CAPITAL
                                                        ,INTERESES
                                                        ,INTERES_ACUERDO
                                                        ,CUOTA';

                                              MI_VALORES := ''''||UN_COMPANIA||'''
                                                            ,'''||UN_ACUERDO||'''
                                                            ,'''||NCUOTA||'''
                                                            ,'''||MI_CAPITAL||'''
                                                            ,'''||MI_INTERESES||'''
                                                            ,'''||MI_INTERES_ACUERDO||'''
                                                            ,'''||MI_CUOTA||'''';



                                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_AP_CUOTAS'
                                                                                    ,UN_ACCION    => 'I'
                                                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                                                    ,UN_VALORES => MI_VALORES); 

                                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                             END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                  MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                  MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                            END;
                                          ELSE

                                            MI_CAPITAL := ROUND(MI_RESULCAP , MI_DIGITOSREDONDEO);

                                            MI_INTERESES := ROUND(MI_RESULINT,MI_DIGITOSREDONDEO);

                                            MI_INTERES_ACUERDO := ROUND((MI_TCAPITAL + MI_TINTERESES)* MI_ACINTERES * UN_PERIODO,MI_DIGITOSREDONDEO);

                                            MI_INTCOMPUESTO := NVL(MI_INTCOMPUESTO,0) + MI_INTERES_ACUERDO;
                                            MI_TOTALINTERESES := ROUND(NVL(MI_TOTALINTERESES,0) + MI_INTERES_ACUERDO, MI_DIGITOSREDONDEO );
                                            MI_CUOTA := MI_CAPITAL + MI_INTERESES + MI_INTERES_ACUERDO;
                                            MI_CAPACTUAL    := (MI_TCAPITAL - MI_CAPITAL) + MI_INTCOMPUESTO;
                                            MI_INTACTUAL    := (MI_TINTERESES - MI_INTERESES);


                                            MI_CAMPOS:='COMPANIA
                                                        ,CODIGOACUERDO
                                                        ,ID
                                                        ,CAPITAL
                                                        ,INTERESES
                                                        ,INTERES_ACUERDO
                                                        ,CUOTA';


                                            MI_VALORES := ''''||UN_COMPANIA||'''
                                                          ,'''||UN_ACUERDO||'''
                                                          ,'''||NCUOTA||'''
                                                          ,'''||MI_CAPITAL||'''
                                                          ,'''||MI_INTERESES||'''
                                                          ,'''||MI_INTERES_ACUERDO||'''
                                                          ,'''||MI_CUOTA||'''';


                                            BEGIN
                                             BEGIN               

                                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_AP_CUOTAS'
                                                                                    ,UN_ACCION    => 'I'
                                                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                                                    ,UN_VALORES => MI_VALORES); 

                                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                             END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                  MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                  MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                            END;

                                          END IF;

                                        ELSE 
                                          IF NCUOTA = MI_NCUOTAS THEN

                                            MI_CAPITAL := ROUND(MI_RESULCAP + MI_RESIDCAP, MI_DIGITOSREDONDEO);

                                            MI_INTERESES := ROUND(MI_RESULINT  + NVL(MI_RESIDINT,0),MI_DIGITOSREDONDEO);

                                            MI_INTERES_ACUERDO := ROUND(MI_TCAPITAL * MI_ACINTERES * UN_PERIODO,MI_DIGITOSREDONDEO);

                                            MI_TOTALINTERESES := ROUND(NVL(MI_TOTALINTERESES,0) + MI_INTERES_ACUERDO, MI_DIGITOSREDONDEO );
                                            MI_CUOTA := ROUND(MI_CAPITAL + MI_INTERESES +  MI_INTERES_ACUERDO ,MI_DIGITOSREDONDEO);

                                            MI_CAPACTUAL    := (MI_TCAPITAL - MI_CAPITAL) + MI_INTCOMPUESTO;

                                            MI_CAMPOS:='COMPANIA
                                                        ,CODIGOACUERDO
                                                        ,ID
                                                        ,CAPITAL
                                                        ,INTERESES
                                                        ,INTERES_ACUERDO
                                                        ,CUOTA';


                                            MI_VALORES := ''''||UN_COMPANIA||'''
                                                          ,'''||UN_ACUERDO||'''
                                                          ,'''||NCUOTA||'''
                                                          ,'''||MI_CAPITAL||'''
                                                          ,'''||MI_INTERESES||'''
                                                          ,'''||MI_INTERES_ACUERDO||'''
                                                          ,'''||MI_CUOTA||'''';



                                            BEGIN
                                             BEGIN               

                                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_AP_CUOTAS'
                                                                                    ,UN_ACCION    => 'I'
                                                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                                                    ,UN_VALORES => MI_VALORES); 

                                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                             END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                  MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                  MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                            END;
                                          ELSE

                                            MI_CAPITAL := ROUND(MI_RESULCAP , MI_DIGITOSREDONDEO);

                                            MI_INTERESES := ROUND(MI_RESULINT,MI_DIGITOSREDONDEO);

                                            MI_INTERESACUERDO := ROUND(MI_TCAPITAL * MI_ACINTERES * UN_PERIODO,MI_DIGITOSREDONDEO);

                                            MI_TOTALINTERESES := ROUND(NVL(MI_TOTALINTERESES,0) + MI_INTERESACUERDO , MI_DIGITOSREDONDEO );
                                            MI_CUOTA := MI_CAPITAL + MI_INTERESES + MI_INTERESACUERDO;

                                            MI_CAPACTUAL      := MI_TCAPITAL - MI_CAPITAL;

                                            MI_CAMPOS:='COMPANIA
                                                        ,CODIGOACUERDO
                                                        ,ID
                                                        ,CAPITAL
                                                        ,INTERESES
                                                        ,INTERES_ACUERDO
                                                        ,CUOTA';


                                            MI_VALORES := ''''||UN_COMPANIA||'''
                                                          ,'''||UN_ACUERDO||'''
                                                          ,'''||NCUOTA||'''
                                                          ,'''||MI_CAPITAL||'''
                                                          ,'''||MI_INTERESES||'''
                                                          ,'''||MI_INTERES_ACUERDO||'''
                                                          ,'''||MI_CUOTA||'''';
                                            BEGIN
                                             BEGIN               

                                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_AP_CUOTAS'
                                                                                    ,UN_ACCION    => 'I'
                                                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                                                    ,UN_VALORES => MI_VALORES); 

                                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                             END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                  MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                  MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                            END;
                                          END IF;

                                        END IF;

                                       MI_TCAPITAL := MI_CAPACTUAL;
                                       MI_TINTERESES := MI_INTACTUAL;

                                    END LOOP INSERTAR_IP_TMP_AP_CUOTAS;



                                  MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                                          ,UN_NOMBRE    => 'MANEJA INTERESES EQUITATIVOS'
                                                                          ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                                          ,UN_FECHA_PAR => SYSDATE);

                                  IF MI_PARAMETROPREDIAL = 'SI' THEN

                                    MI_TOTALINTERESES := MI_TOTALINTERESES / MI_NCUOTAS;


                                        MI_STRSQL := 'SELECT ID
                                                             , CAPITAL
                                                             , INTERESES
                                                             ,INTERES_ACUERDO 
                                                      FROM IP_TMP_AP_CUOTAS WHERE ID <> 0'; 


                                       EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  
                                        IF MI_CONTEO >=1 THEN 
                                            OPEN MI_RSTMP FOR MI_STRSQL;
                                            LOOP

                                             FETCH MI_RSTMP INTO MI_ID,MI_CAPITAL,MI_INTERESES,MI_INTERES_ACUERDO; 
                                               EXIT WHEN MI_RSTMP%NOTFOUND;       
                                                        MI_CAMPOS :=  'INTERES_ACUERDO = '''||NVL(ROUND(MI_TOTALINTERESES,MI_DIGITOSREDONDEO),0)||'''
                                                                      ,CUOTA = '''||ROUND(MI_CAPITAL + MI_INTERESES + MI_INTERES_ACUERDO)||'''';


                                                        MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA||'''
                                                                        AND CODIGOACUERDO  = '''||UN_ACUERDO||'''
                                                                        AND ID             = '||MI_ID||'';

                                            BEGIN
                                             BEGIN               

                                               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_TMP_AP_CUOTAS'
                                                                                      ,UN_ACCION    => 'M'
                                                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                                                      ,UN_CONDICION => MI_CONDICION);

                                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                                             END ;

                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN

                                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                UN_EXC_COD =>SQLCODE
                                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_TMPAPCUOTA
                                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                                            END;

                                            END LOOP; 

                                          END IF;  

                                  END IF;

                                  MI_STRSQL := 'SELECT ID
                                                       ,CAPITAL
                                                       ,INTERESES
                                                       ,INTERES_ACUERDO
                                                       ,CUOTA 
                                                FROM IP_TMP_AP_CUOTAS'; 

                                     EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

                                        IF MI_CONTEO >=1 THEN  
                                            OPEN MI_RSTMP FOR MI_STRSQL;
                                            LOOP

                                             FETCH MI_RSTMP INTO MI_ID,MI_CAPITAL,MI_INTERESES, MI_INTERESACUERDO,MI_CUOTA;
                                             EXIT WHEN MI_RSTMP%NOTFOUND; 
                                             IF MI_ID = 0 OR MI_ID = 1 THEN

                                                  IF UN_NITCOMPANIA = '8912800003' AND UN_ACUERDOPASTO <> 0 THEN

                                                     MI_FECHAFACTURA := TO_DATE('31/03/2010','dd/mm/yyyy');

                                                  ELSE 
                                                    MI_FECHAFACTURA := TO_DATE(SYSDATE,'dd/mm/yyyy');
                                                  END IF;

                                             ELSE
                                                MI_FECHAFACTURA := ADD_MONTHS(SYSDATE,UN_PERIODO);
                                             END IF;

                                                  MI_PARAMETROPREDIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                                                ,UN_NOMBRE    => 'CUOTAS DE ACUERDO CALCULADAS A FINAL DE CADA MES'
                                                                                ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                                                ,UN_FECHA_PAR => SYSDATE);

                                                   IF MI_PARAMETROPREDIAL = 'SI' AND  MI_ID > 0 THEN 
                                                        MI_FECHAFACTURADO := LAST_DAY(MI_FECHAFACTURA);                                        
                                                   ELSE
                                                        MI_FECHAFACTURADO := MI_FECHAFACTURA;
                                                   END IF;


                                                   BEGIN
                                                     BEGIN

                                                                 MI_CAMPOS := 'COMPANIA
                                                                            , CODIGOACUERDO
                                                                            , PREDIO
                                                                            , NUMERO_ORDEN
                                                                            , CUOTA
                                                                            , PAGADO
                                                                            , CAPITAL
                                                                            , INTERESES
                                                                            , INTERES_ACUERDO
                                                                            , PREANOI
                                                                            , PREANO
                                                                            , TOTAL
                                                                            , FECHAFACTURADO';

                                                               MI_VALORES := ''''||UN_COMPANIA||'''
                                                                              , '''||UN_ACUERDO||'''
                                                                              , '''||MI_PREDIO||'''
                                                                              , '''||MI_NUMEROORDEN||'''
                                                                              , '''||MI_ID||'''
                                                                              , 0 
                                                                              , '||MI_CAPITAL||'
                                                                              , '||MI_INTERESES||'
                                                                              , '||MI_INTERESACUERDO||'
                                                                              , '||UN_ANOMENOR||' 
                                                                              , '||UN_ANOMAYOR||' 
                                                                              , '||MI_CUOTA||'
                                                                              , '''||MI_FECHAFACTURADO||'''';

                                                               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => UN_TFACTURADOSACU
                                                                                                      ,UN_ACCION  => 'I'
                                                                                                      ,UN_CAMPOS  => MI_CAMPOS
                                                                                                      ,UN_VALORES => MI_VALORES);  

                                                                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                                                           END;

                                                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                                                                MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                                MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                                MI_MSGERROR(2).CLAVE := 'PREDIO';
                                                                MI_MSGERROR(2).VALOR := MI_PREDIO;
                                                                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                                          UN_EXC_COD =>SQLCODE
                                                                                          ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_INSERT_TFACTACU
                                                                                          ,UN_REEMPLAZOS => MI_MSGERROR);

                                                          END;   


                                                      MI_FECHAFACTURA := ADD_MONTHS(MI_FECHAFACTURA,UN_PERIODO);

                                            END LOOP; 

                                         END IF; 


                                         MI_CONDICION := 'CODIGOACUERDO = '||UN_ACUERDO||'';

                                          BEGIN

                                           BEGIN
                                           PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_TMP_AP_CUOTAS'
                                                                ,UN_ACCION  => 'E'
                                                                ,UN_CONDICION => MI_CONDICION);                                                           

                                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                   

                                             END;

                                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                                   MI_MSGERROR(1).CLAVE := 'ACUERDO';
                                                   MI_MSGERROR(1).VALOR := UN_ACUERDO;
                                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                            UN_EXC_COD =>SQLCODE
                                                                            ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_DELETE_TMPCUOTAS
                                                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

                                            END; 

                        END LOOP;

                     END IF;

          END IF; 

         END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                   ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_PAGO_CANCELADO);


        END;

    END IF;        
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                   ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_ACUERDO_VACIO);
   END;    


   MI_CAMPOS := 'INTERES_RECARGO = 0';

   MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                AND CODIGOACUERDO  = '''||UN_ACUERDO||'''
                AND PREDIO         = '||MI_PREDIO||'
                AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||'''
                AND CUOTA          = '''||MI_CUOTA||'''
                AND INTERES_RECARGO IS NULL';


   BEGIN
    BEGIN
           PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TFACTURADOSACU
                                                                 ,UN_ACCION    => 'M'
                                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                                 ,UN_CONDICION => MI_CONDICION); 

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'TABLA';
          MI_MSGERROR(1).VALOR := UN_TFACTURADOSACU;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_FACTURACU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);                                      

   END;                                                          


END PR_CALCULARACUERDO;


PROCEDURE PR_RECALCULAR_CAPITAL_AC
  /*
  NAME              : RECALCULAR_CAPITAL_AC En Access --> ReCalcular_Capital_Ac
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 13/02/2017
  TIME              : 15:00 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Procedimiento llamado por la función PCK_PREDIAL_FIN.FC_CREARACUERDO
                    Prcedimiento que se encarga de recalcular los acuerdos creados haciendo actualizaciones a tablas ingresadas por parámetros.

  @NAME:  reCalcularCapitalAc
  @METHOD:  POST   

  */  
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOACUERDO   IN VARCHAR2,
  UN_PERIODO         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TACUERDO        IN VARCHAR2,
  UN_TFACTURADOSACU  IN VARCHAR2,
  UN_MANEJACOMPUESTO IN BOOLEAN
)AS 
  MI_CONCINTERESES       VARCHAR2(3000 CHAR);
  MI_COPIACONCINTERESES  VARCHAR2(3000 CHAR);
  MI_PARAMETROPREDIAL    VARCHAR2(3000 CHAR);
  MI_STRSQL              PCK_SUBTIPOS.TI_STRSQL; 
  MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS; 
  MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
  MI_RSFACTURADOS        SYS_REFCURSOR;  
  MI_RSCONCEP            SYS_REFCURSOR;   
  MI_PORCINT             NUMBER(9,6);
  MI_NCUOTAS             PCK_SUBTIPOS.TI_ENTERO;
  MI_CONTEO              PCK_SUBTIPOS.TI_ENTERO;
  MI_VIGANT              PCK_SUBTIPOS.TI_ENTERO;
  MI_VIGDFR              PCK_SUBTIPOS.TI_ENTERO;  
  MI_POSSEP              PCK_SUBTIPOS.TI_ENTERO;
  MI_CONCEPTO            PCK_SUBTIPOS.TI_ENTERO; 
  MI_DBLMONTOCAPITAL     PCK_SUBTIPOS.TI_ENTERO;
  MI_DBLMONTOINTERES     PCK_SUBTIPOS.TI_ENTERO;
  MI_DBLMONTOCAPITALTOT  PCK_SUBTIPOS.TI_ENTERO;
  MI_DBLMONTOINTERESTOT  PCK_SUBTIPOS.TI_ENTERO;
  MI_PREDIO              VARCHAR(3000 CHAR);
  MI_NUMEROORDEN         VARCHAR(3000 CHAR);      
  MI_CUOTA               PCK_SUBTIPOS.TI_ENTERO;
  MI_STOTAL              PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERESES           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CAPITAL             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERESACUERDO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERESRECARGO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_INTERESCUOTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CTOTAL              PCK_SUBTIPOS.TI_ENTERO;
  MI_CINTERESACUERDO     PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TCAPITAL            PCK_SUBTIPOS.TI_ENTERO;
  MI_TINTERES            PCK_SUBTIPOS.TI_ENTERO;
  MI_TOTINTERESACU       PCK_SUBTIPOS.TI_ENTERO;
  MI_DIGITOSREDONDEO     PCK_SUBTIPOS.TI_ENTERO;



BEGIN
      MI_CONCINTERESES := NVL((PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'ACUERDOS DE PAGO - CONCEPTOS INTERESES'
                                                    ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                    ,UN_FECHA_PAR => SYSDATE)) ,'2,4');   


     MI_CONCINTERESES := REPLACE(REPLACE(MI_CONCINTERESES, ' ',''),';',',');

     MI_CONCINTERESES := PCK_SYSMAN_UTL.FC_IIF(SUBSTR(MI_CONCINTERESES,LENGTH(MI_CONCINTERESES),1) = ',' , SUBSTR(MI_CONCINTERESES,1,LENGTH(MI_CONCINTERESES)-1),MI_CONCINTERESES);


     MI_DIGITOSREDONDEO := NVL((PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                  ,UN_NOMBRE    => 'DIGITOS DE REDONDEO PARA ACUERDOS'
                                                  ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                  ,UN_FECHA_PAR => SYSDATE)) ,3)* -1;   

    MI_STRSQL := 'SELECT INTERES, NCUOTAS 
                   FROM  '||UN_TACUERDO||' 
                   WHERE CODIGOACUERDO IN('''||UN_CODIGOACUERDO||''') ';

     EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;   

       IF MI_CONTEO >=1 THEN   
          OPEN MI_RSFACTURADOS FOR MI_STRSQL;
          LOOP

            FETCH MI_RSFACTURADOS INTO MI_PORCINT,MI_NCUOTAS; 

             EXIT WHEN MI_RSFACTURADOS%NOTFOUND;                   
          END LOOP;
       ELSE
        MI_PORCINT:= 0;
        MI_NCUOTAS := 0;
       END IF;   


     MI_STRSQL := ' SELECT C.VIGANT, C.VIGDFR 
                    FROM '||UN_TACUERDO||' A INNER JOIN IP_CONCEPTOS  C ON A.PREANO = C.ANO 
                    WHERE C.COMPANIA IN('''||UN_COMPANIA||''')
                      AND A.CODIGOACUERDO IN ('''||UN_CODIGOACUERDO||''') 
                      AND C.CODIGO IN ('||MI_CONCINTERESES||')';  

     EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;              

       IF MI_CONTEO >=1 THEN   
          OPEN MI_RSCONCEP FOR MI_STRSQL;
          LOOP 

            FETCH MI_RSCONCEP INTO MI_VIGANT,MI_VIGDFR; 
              EXIT WHEN MI_RSCONCEP%NOTFOUND;
              IF NVL(MI_VIGANT,0) <> 0 THEN
                MI_CONCINTERESES := MI_CONCINTERESES ||','|| MI_VIGANT;
              END IF;
              IF NVL(MI_VIGDFR,0) <> 0 THEN
                MI_CONCINTERESES := MI_CONCINTERESES ||','|| MI_VIGDFR;
              END IF;

          END LOOP;

       END IF;   

      MI_COPIACONCINTERESES :=  MI_CONCINTERESES;

      MI_STRSQL := '';

      WHILE LENGTH(MI_COPIACONCINTERESES) <> 0 
        LOOP
          MI_POSSEP := INSTR(MI_COPIACONCINTERESES,',',1);

            IF MI_POSSEP = 0 THEN

              MI_CONCEPTO := MI_COPIACONCINTERESES;
              MI_COPIACONCINTERESES := '';
            ELSE 
              MI_CONCEPTO := SUBSTR(MI_COPIACONCINTERESES,1,INSTR(MI_COPIACONCINTERESES,',',1)-1);
              MI_COPIACONCINTERESES := SUBSTR(MI_COPIACONCINTERESES,INSTR(MI_COPIACONCINTERESES,',',1)+1,LENGTH(MI_COPIACONCINTERESES));
            END IF;
            MI_STRSQL := ''||MI_STRSQL||' NVL(C'||MI_CONCEPTO||',0) +';
        END LOOP;

        IF MI_STRSQL IS NOT NULL THEN
          MI_STRSQL := SUBSTR(MI_STRSQL,1,LENGTH(MI_STRSQL)-1);
        ELSE 
          MI_STRSQL := '';
        END IF;
        MI_STRSQL := 'SELECT PREDIO
                            , NUMERO_ORDEN
                            , CUOTA
                            , TOTAL STOTAL
                            , INTERESES
                            , CAPITAL
                            , INTERES_ACUERDO
                            , INTERES_RECARGO
                            ,'||MI_STRSQL||' ';

        MI_STRSQL := ''||MI_STRSQL||'  INTERES_CUOTA
                  FROM  '||UN_TFACTURADOSACU||'   
                  WHERE CODIGOACUERDO IN ('''||UN_CODIGOACUERDO||''')
                  ORDER BY CUOTA';                         

        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;              

        MI_DBLMONTOINTERESTOT := 0;
        MI_DBLMONTOCAPITALTOT := 0; 

        IF MI_CONTEO >=1 THEN   
          OPEN MI_RSFACTURADOS FOR MI_STRSQL;
            LOOP 

              FETCH MI_RSFACTURADOS INTO MI_PREDIO,MI_NUMEROORDEN,MI_CUOTA,MI_STOTAL,MI_INTERESES,MI_CAPITAL,MI_INTERESACUERDO,MI_INTERESRECARGO,MI_INTERESCUOTA; 
              EXIT WHEN MI_RSFACTURADOS%NOTFOUND;

              MI_DBLMONTOINTERES := 0;
              MI_DBLMONTOCAPITAL := 0;     

              MI_DBLMONTOINTERES:= NVL(MI_INTERESCUOTA,0);
              MI_DBLMONTOINTERESTOT := MI_DBLMONTOINTERESTOT + MI_DBLMONTOINTERES;
              MI_DBLMONTOCAPITAL := NVL(MI_STOTAL,0)- NVL(MI_INTERESACUERDO,0) - NVL(MI_INTERESRECARGO,0) - MI_DBLMONTOINTERES;
              MI_DBLMONTOCAPITALTOT := MI_DBLMONTOCAPITALTOT + MI_DBLMONTOCAPITAL;

              MI_CAMPOS := 'INTERESES = '||MI_DBLMONTOINTERES||'
                           ,CAPITAL  = '||MI_DBLMONTOCAPITAL||'';

              MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                          AND CODIGOACUERDO  = '''||UN_CODIGOACUERDO||'''
                          AND PREDIO         = '||MI_PREDIO||'
                          AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||'''
                          AND CUOTA          = '''||MI_CUOTA||'''';


                  BEGIN
                   BEGIN
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TFACTURADOSACU
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END ;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                              MI_MSGERROR(1).CLAVE := 'TABLA';
                              MI_MSGERROR(1).VALOR := UN_TFACTURADOSACU;
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                            UN_EXC_COD =>SQLCODE
                                                            ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_FACTURACU
                                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END; 

                    MI_CAMPOS := 'MONTOCAPITAL  = '||MI_DBLMONTOCAPITALTOT||'
                                 ,MONTOINTERESES = '||MI_DBLMONTOCAPITAL||'';

                    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                                AND CODIGOACUERDO  = '''||UN_CODIGOACUERDO||'''
                                AND PREDIO         = '||MI_PREDIO||'
                                AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||''''; 

                  BEGIN
                     BEGIN
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TACUERDO
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END ;

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                MI_MSGERROR(1).CLAVE := 'TABLA';
                                MI_MSGERROR(1).VALOR := UN_TACUERDO;
                                           PCK_ERR_MSG.RAISE_WITH_MSG(
                                                              UN_EXC_COD =>SQLCODE
                                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_IPACUERDO
                                                              ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;

                  MI_TCAPITAL := MI_DBLMONTOCAPITALTOT;
                  MI_TINTERES := MI_DBLMONTOINTERESTOT;
                  MI_TOTINTERESACU := 0;
                  MI_DBLMONTOINTERES := 0;

                  IF UN_MANEJACOMPUESTO THEN
                      IF MI_CUOTA = 0 THEN
                       MI_DBLMONTOINTERES := 0;
                      ELSE
                       MI_DBLMONTOINTERES := ROUND((MI_TCAPITAL + MI_TINTERES + MI_TOTINTERESACU) * MI_PORCINT * UN_PERIODO, MI_DIGITOSREDONDEO);
                      END IF; 

                      MI_TCAPITAL := MI_TCAPITAL - MI_CAPITAL;
                      MI_TINTERES := MI_TINTERES - MI_INTERESES;
                      MI_TOTINTERESACU := MI_TOTINTERESACU + MI_DBLMONTOINTERES; 

                  ELSE
                      IF MI_CUOTA = 0 THEN
                        MI_DBLMONTOINTERES := 0;
                      ELSE
                       MI_DBLMONTOINTERES := ROUND(MI_TCAPITAL * MI_PORCINT * UN_PERIODO, MI_DIGITOSREDONDEO);
                      END IF; 

                      MI_TCAPITAL := MI_TCAPITAL - MI_CAPITAL;
                      MI_TOTINTERESACU := MI_TOTINTERESACU + MI_DBLMONTOINTERES; 


                  END IF;  

                    MI_CTOTAL:= MI_CAPITAL + MI_INTERESES + MI_INTERESRECARGO + MI_DBLMONTOINTERES;

                    MI_CAMPOS := 'INTERES_ACUERDO =  '''||MI_DBLMONTOINTERES||'''
                                  ,TOTAL = '''||MI_CTOTAL||''' '; 

                    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                                  AND CODIGOACUERDO  = '''||UN_CODIGOACUERDO||'''
                                  AND PREDIO         = '||MI_PREDIO||'
                                  AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||'''
                                  AND CUOTA          = '''||MI_CUOTA||'''';


                      BEGIN
                       BEGIN
                         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TFACTURADOSACU
                                                               ,UN_ACCION    => 'M'
                                                               ,UN_CAMPOS    => MI_CAMPOS
                                                               ,UN_CONDICION => MI_CONDICION);


                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END ;

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                  MI_MSGERROR(1).CLAVE := 'TABLA';
                                  MI_MSGERROR(1).VALOR := UN_TFACTURADOSACU;
                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                UN_EXC_COD =>SQLCODE
                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_FACTURACU
                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                      END; 


            MI_PARAMETROPREDIAL :=(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                          ,UN_NOMBRE    => 'MANEJA INTERESES EQUITATIVOS'
                                                          ,UN_MODULO    =>  PCK_DATOS.MODULOPREDIAL
                                                          ,UN_FECHA_PAR => SYSDATE));      

           IF NVL(MI_PARAMETROPREDIAL,'NO') = 'SI' THEN

            MI_DBLMONTOINTERES  := ROUND(MI_TOTINTERESACU / MI_NCUOTAS, MI_DIGITOSREDONDEO);
            MI_TOTINTERESACU := MI_TOTINTERESACU - (MI_DBLMONTOINTERES * MI_NCUOTAS);

                  IF MI_CUOTA <> 0 THEN
                    IF MI_CUOTA <> MI_NCUOTAS THEN

                      MI_CTOTAL := MI_CAPITAL + MI_INTERESES + MI_INTERESRECARGO + MI_DBLMONTOINTERES;

                      MI_CAMPOS := 'INTERES_ACUERDO = '||MI_DBLMONTOINTERES||'
                                    ,TOTAL = '||MI_CTOTAL||'';

                    ELSE
                      MI_CTOTAL := MI_CAPITAL + MI_INTERESES + MI_INTERESRECARGO + MI_DBLMONTOINTERES + MI_TOTINTERESACU; 

                      MI_CINTERESACUERDO := MI_DBLMONTOINTERES + MI_TOTINTERESACU;
                      MI_CAMPOS := 'INTERES_ACUERDO = '||MI_CINTERESACUERDO||'
                                    ,TOTAL = '||MI_CTOTAL||'';
                    END IF;

                    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                                AND CODIGOACUERDO  = '''||UN_CODIGOACUERDO||'''
                                AND PREDIO         = '||MI_PREDIO||'
                                AND NUMERO_ORDEN   = '''||MI_NUMEROORDEN||'''
                                AND CUOTA          = '''||MI_CUOTA||'''';


                      BEGIN
                       BEGIN
                         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => UN_TFACTURADOSACU
                                                               ,UN_ACCION    => 'M'
                                                               ,UN_CAMPOS    => MI_CAMPOS
                                                               ,UN_CONDICION => MI_CONDICION);


                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END ;

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                  MI_MSGERROR(1).CLAVE := 'TABLA';
                                  MI_MSGERROR(1).VALOR := UN_TFACTURADOSACU;
                                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                UN_EXC_COD =>SQLCODE
                                                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDATE_FACTURACU
                                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                      END; 
                  END IF;
           END IF;

          END LOOP;

        END IF;     

END PR_RECALCULAR_CAPITAL_AC;


FUNCTION FC_FECHAFINAL_ACUERDO
  /*
  NAME              : FC_FECHAFINAL_ACUERDO En Access --> FinalAcuerdo
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 15/02/2017
  TIME              : 12:45 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Función llámada por por la función PCK_PREDIAL_FIN.FC_CREARACUERDO
                      Esta función se encarga de calcular y retornar la fecha final del acuerdo.

  @NAME:  optenerFechaFinalAcuerdo
  @METHOD:  GET  
  */     
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHAACUERDO      IN DATE,
  UN_NCUOTAS           IN NUMBER,
  UN_PERIODICIDAD      IN NUMBER
)RETURN DATE
AS 
  MI_FECHAAUX     DATE;

BEGIN
  MI_FECHAAUX  := UN_FECHAACUERDO;

  FOR I IN  1..UN_NCUOTAS LOOP
      MI_FECHAAUX := ADD_MONTHS(MI_FECHAAUX,UN_PERIODICIDAD);

  END LOOP;

  RETURN MI_FECHAAUX;
END  FC_FECHAFINAL_ACUERDO;

FUNCTION FC_TASAINTERESVIGENTE
  /*
  NAME              : FC_TASAINTERESVIGENTE En Access --> TasaInteresVigente
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 09/02/2017
  TIME              : 16:31 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Función llámada por por el procedimiento PCK_PREDIAL_FIN.PR_CALCULARACUERDO
                      Esta función se encarga de retornar el valor de la tasa vigente de la fecha actual.

  @NAME:  obtenerFechaFinalAcuerdo
  @METHOD:  GET  
  */

RETURN NUMBER
AS  
  MI_TASAMENSUAL         NUMBER(10,7);

BEGIN
    MI_TASAMENSUAL := 0;
   BEGIN  
    SELECT TASAMENSUAL 
    INTO  MI_TASAMENSUAL
    FROM  IP_TASASINTERES 
    WHERE ANO = EXTRACT (YEAR FROM SYSDATE) 
      AND MES = EXTRACT (MONTH FROM SYSDATE);

    EXCEPTION WHEN NO_DATA_FOUND THEN        
    MI_TASAMENSUAL := 0;
    END;

   RETURN MI_TASAMENSUAL;

END FC_TASAINTERESVIGENTE;

  PROCEDURE PR_DISTRIBUIRACUERDO
  (
    /*
      NAME              : PR_DISTRIBUIRACUERDO --> EN ACCESS DistribuirAcuerdo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 17/01/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS VALORES DE LOS CONCEPTOS Y LLAMA LAS FUNCIONES QUE DISTRIBUYEN 
                          LOS ACUERDOS DE CAPITAL Y DE INTERES.
      PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_PREDIO          => CODIGO DEL PREDIO DEL QUE SE DESEA DISTRIBUIR ACUERDO.
                          UN_ACUERDO         => CODIGO DEL ACUERDO DEL PREDIO.
                          UN_PREANOI         => ANIO DE LA VIGENCIA INICIAL.
                          UN_PREANO          => ANIO ACTUAL.
                          UN_T_FACTURADOSACU => NOMBRE DE LA TABLA QUE SE VA A MANIPULAR.
      MODIFICATIONS     : 

      @NAME:    distribuirAcuerdo
      @METHOD:  PUT
    */
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PREDIO             IN PCK_SUBTIPOS.TI_CODPREDIO, 
    UN_ACUERDO            IN IP_TMP_ACUERDOS.CODIGOACUERDO%TYPE, 
    UN_PREANOI            IN PCK_SUBTIPOS.TI_ANIO, 
    UN_PREANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_T_FACTURADOSACU    IN PCK_SUBTIPOS.TI_TABLA
  )
  AS
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPO                VARCHAR2 (10 CHAR);
    MI_I                    PCK_SUBTIPOS.TI_ENTERO;
    MI_CAPITAL              PCK_SUBTIPOS.TI_ENTERO;
    MI_INTERES              PCK_SUBTIPOS.TI_ENTERO;

  BEGIN       
    MI_CONDICION := ' CODIGOACUERDO = '''|| UN_ACUERDO ||'''';

    BEGIN
      BEGIN
        <<CAMPOSCONCEPTO>>
        FOR MI_I IN 1..20 LOOP
          MI_CAMPO := 'C'||MI_I;

          MI_CAMPOS := ''||MI_CAMPO||' = 0';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_ACU_ACT_CONC
              );
          END;
        END LOOP CAMPOSCONCEPTO;

        MI_CAMPOS := 'PREANOI = 0, PREANO = 0';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_ACU_ACT_PREAN
            );
        END;                                           
      END;  
    END;

    MI_CAPITAL := FC_DISTRIBUIR_CAPITALINTERES(UN_COMPANIA        => UN_COMPANIA,
                                               UN_PREDIO          => UN_PREDIO,
                                               UN_ACUERDO         => UN_ACUERDO,
                                               UN_PREANOI         => UN_PREANOI,
                                               UN_PREANO          => UN_PREANO,
                                               UN_T_FACTURADOSACU => UN_T_FACTURADOSACU,
                                               UN_ESCAPITAL       => 1);

    MI_INTERES := FC_DISTRIBUIR_CAPITALINTERES(UN_COMPANIA        => UN_COMPANIA,
                                               UN_PREDIO          => UN_PREDIO,
                                               UN_ACUERDO         => UN_ACUERDO,
                                               UN_PREANOI         => UN_PREANOI,
                                               UN_PREANO          => UN_PREANO,
                                               UN_T_FACTURADOSACU => UN_T_FACTURADOSACU,
                                               UN_ESCAPITAL       => 0);

    MI_CAMPOS        := 'TOTAL = (CAPITAL + INTERESES + INTERES_ACUERDO)';
    MI_CONDICION     := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                         AND  PREDIO        = '''|| UN_PREDIO || '''
                         AND  CODIGOACUERDO = '''|| UN_ACUERDO ||'''';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_ACU_ACT_TOTAL
        );
    END;

  END PR_DISTRIBUIRACUERDO;

  FUNCTION FC_DISTRIBUIR_CAPITALINTERES
  (
    /*
      NAME              : FC_DISTRIBUIR_CAPITALINTERES
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 17/01/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE DISTRIBUYE LOS ACUERDOS DE CAPITAL Y DE INTERES DE ACUERDO AL PARAMETRO INGRESADO.
      PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_PREDIO          => CODIGO DEL PREDIO DEL QUE SE DESEA DISTRIBUIR ACUERDO.
                          UN_ACUERDO         => CODIGO DEL ACUERDO DEL PREDIO.
                          UN_PREANOI         => ANIO DE LA VIGENCIA INICIAL.
                          UN_PREANO          => ANIO ACTUAL.
                          UN_T_FACTURADOSACU => NOMBRE DE LA TABLA QUE SE VA A MANIPULAR.
                          UN_ESCAPITAL       => PARAMETRO DE TIPO NUMERICO QUE DETERMINA SI LA FUNCION SE HARA SOBRE EL 
                                                CAPITAL O SOBRE EL INTERES.
      MODIFICATIONS     : 

      @NAME:    distribuirAcuerdoCapitalInteres
      @METHOD:  GET
    */
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PREDIO                 IN PCK_SUBTIPOS.TI_CODPREDIO, 
    UN_ACUERDO                IN IP_TMP_ACUERDOS.CODIGOACUERDO%TYPE, 
    UN_PREANOI                IN PCK_SUBTIPOS.TI_ANIO,
    UN_PREANO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_T_FACTURADOSACU        IN PCK_SUBTIPOS.TI_TABLA,
    UN_ESCAPITAL              IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN NUMBER
  AS
    MI_RSFACTURADOSACUERDOS   SYS_REFCURSOR;
    MI_RSCONCEPTOS            SYS_REFCURSOR;
    MI_RSFACTURADOS           SYS_REFCURSOR;
    MI_SALDOADISTRIBUIR       PCK_SUBTIPOS.TI_DOBLE;
    MI_ANOTRABAJO             PCK_SUBTIPOS.TI_ANIO;
    MI_VALORABONADO           PCK_SUBTIPOS.TI_DOBLE;
    MI_PRIORIDADABONADA       PCK_SUBTIPOS.TI_ENTERO;
    MI_CUOTATRABAJO           PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQLCONCEPTOS        PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQLFACTURADOS       PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQLFACACU           PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOVIGENCIA          VARCHAR2 (30 CHAR);
    MI_VIGENCIACONDICION      VARCHAR2 (30 CHAR);
    MI_PREANO                 PCK_SUBTIPOS.TI_ANIO;
    MI_CCODIGO                IP_FACTURADOS.C1%TYPE;
    MI_CODIGO                 IP_CONCEPTOS.PRIORIDAD%TYPE;
    MI_VIGANT                 IP_CONCEPTOS.PRIORIDAD%TYPE;
    MI_VIGDFR                 IP_CONCEPTOS.PRIORIDAD%TYPE;
    MI_PRIORIDAD              IP_CONCEPTOS.PRIORIDAD%TYPE;
    MI_CODIGOACUERDO          IP_TMP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE;
    MI_PREDIO                 IP_TMP_FACTURADOSACUERDOS.PREDIO%TYPE;
    MI_NUMERO_ORDEN           IP_TMP_FACTURADOSACUERDOS.NUMERO_ORDEN%TYPE;
    MI_CUOTA                  IP_TMP_FACTURADOSACUERDOS.CUOTA%TYPE;
    MI_CAPITAL                IP_TMP_FACTURADOSACUERDOS.CAPITAL%TYPE;
    MI_INTERESES              IP_TMP_FACTURADOSACUERDOS.INTERESES%TYPE;
    MI_PREANOIA               IP_TMP_FACTURADOSACUERDOS.PREANOI%TYPE;
    MI_PREANOA                IP_TMP_FACTURADOSACUERDOS.PREANOI%TYPE;
    MI_VALOR                  PCK_SUBTIPOS.TI_DOBLE;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPO                  VARCHAR2 (10 CHAR);
    MI_CAMPO_CODIGO           VARCHAR2 (10 CHAR);
    MI_CAMPO_VIGANT           VARCHAR2 (10 CHAR);
    MI_CAMPO_VIGDFR           VARCHAR2 (10 CHAR);
    MI_I                      PCK_SUBTIPOS.TI_ENTERO;
    MI_RETORNO                PCK_SUBTIPOS.TI_ENTERO;
    MI_CONTEO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_SALDOADISTRIBUIR := 0;
    MI_ANOTRABAJO := UN_PREANOI;
    MI_PRIORIDADABONADA := 1;
    MI_CUOTATRABAJO := 0;
    MI_VALORABONADO := 0;
    MI_RETORNO := 1;

    IF MI_ANOTRABAJO > UN_PREANO THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_ANIOS
          );
      END;
    END IF;

    MI_CAMPOVIGENCIA := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_ESCAPITAL <> 0,
                                              UN_SI        => 'CAPITAL',
                                              UN_NO        => 'INTERESES');

    MI_VIGENCIACONDICION := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_ESCAPITAL <> 0,
                                                  UN_SI        => 'ESCAPITALVIGENCIA',
                                                  UN_NO        => 'ESINTERESVIGENCIA');

    MI_STRSQLFACACU := 'SELECT    CODIGOACUERDO,
                                  PREDIO,
                                  NUMERO_ORDEN,
                                  CUOTA,
                                  CAPITAL,
                                  INTERESES,
                                  PREANOI,
                                  PREANO
                        FROM      '|| UN_T_FACTURADOSACU ||' 
                        WHERE     COMPANIA      =  ''' || UN_COMPANIA ||'''
                          AND     CODIGOACUERDO IN ('''|| UN_ACUERDO ||''') 
                          AND     CUOTA         >= '   || MI_CUOTATRABAJO ||'
                        ORDER BY  CUOTA';

    <<FACTURADOSACUERDOS>>
    OPEN MI_RSFACTURADOSACUERDOS FOR MI_STRSQLFACACU;
      LOOP
        FETCH MI_RSFACTURADOSACUERDOS
        INTO  MI_CODIGOACUERDO,
              MI_PREDIO,
              MI_NUMERO_ORDEN,
              MI_CUOTA,
              MI_CAPITAL,
              MI_INTERESES,
              MI_PREANOIA,
              MI_PREANOA;
        EXIT WHEN MI_RSFACTURADOSACUERDOS%NOTFOUND;

        IF UN_ESCAPITAL <> 0 THEN
          MI_SALDOADISTRIBUIR := NVL(MI_CAPITAL, 0);
        ELSE
          MI_SALDOADISTRIBUIR := NVL(MI_INTERESES, 0);
        END IF;

        IF MI_PREANOIA = 0 THEN
          MI_CAMPOS        := 'PREANOI = '||MI_ANOTRABAJO;
          MI_CONDICION     := '     COMPANIA      = '''||UN_COMPANIA||'''
                               AND  CODIGOACUERDO = '''||MI_CODIGOACUERDO||'''
                               AND  PREDIO        = '''||MI_PREDIO||'''
                               AND  NUMERO_ORDEN  = '''||MI_NUMERO_ORDEN||'''
                               AND  CUOTA         = '  ||MI_CUOTA;
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_AVIGINI
              );
          END;
        END IF;

       /* MI_STRSQLCONCEPTOS := 'SELECT   CODIGO, 
                                        VIGANT, 
                                        VIGDFR, 
                                        PRIORIDAD
                                  FROM     IP_CONCEPTOS
                               WHERE    COMPANIA                            =  '''|| UN_COMPANIA ||'''
                                 AND    ANO                                 =  '  || MI_ANOTRABAJO ||'
                                 AND    PRIORIDAD                           >= '  || MI_PRIORIDADABONADA ||'
                                 AND    PRIORIDAD                           IS NOT NULL 
                                 AND    NVL('|| MI_VIGENCIACONDICION ||',0) <> 0
                               ORDER BY PRIORIDAD';*/

        WHILE MI_SALDOADISTRIBUIR > 0 LOOP
           MI_STRSQLCONCEPTOS := 'SELECT   CODIGO, 
                                        VIGANT, 
                                        VIGDFR, 
                                        PRIORIDAD
                               FROM     IP_CONCEPTOS
                               WHERE    COMPANIA                            =  '''|| UN_COMPANIA ||'''
                                 AND    ANO                                 =  '  || MI_ANOTRABAJO ||'
                                 AND    PRIORIDAD                           >= '  || MI_PRIORIDADABONADA ||'
                                 AND    PRIORIDAD                           IS NOT NULL 
                                 AND    NVL('|| MI_VIGENCIACONDICION ||',0) <> 0
                               ORDER BY PRIORIDAD';
          <<CONCEPTOS>>
          OPEN MI_RSCONCEPTOS FOR MI_STRSQLCONCEPTOS;
            LOOP
              FETCH MI_RSCONCEPTOS
              INTO  MI_CODIGO, 
                    MI_VIGANT, 
                    MI_VIGDFR, 
                    MI_PRIORIDAD;
              EXIT WHEN MI_RSCONCEPTOS%NOTFOUND OR MI_SALDOADISTRIBUIR = 0;

              MI_CAMPO_CODIGO := 'C'||MI_CODIGO;
              MI_CAMPO_VIGANT := 'C'||MI_VIGANT;
              MI_CAMPO_VIGDFR := 'C'||MI_VIGDFR;

              MI_STRSQLFACTURADOS := 'SELECT  PREANO,
                                              '||MI_CAMPO_CODIGO||'
                                      FROM    IP_FACTURADOS 
                                      WHERE   COMPANIA = '''|| UN_COMPANIA ||'''
                                        AND   CODIGO   = '''|| UN_PREDIO ||'''
                                        AND   PREANO   = '|| MI_ANOTRABAJO ;

              <<FACTURADOS>>
              OPEN MI_RSFACTURADOS FOR MI_STRSQLFACTURADOS;
                LOOP
                  FETCH MI_RSFACTURADOS
                  INTO  MI_PREANO,
                        MI_CCODIGO;
                  EXIT WHEN MI_RSFACTURADOS%NOTFOUND;

                  IF MI_SALDOADISTRIBUIR >= (MI_CCODIGO - MI_VALORABONADO) THEN
                    MI_VALOR := MI_CCODIGO - MI_VALORABONADO;
                    MI_SALDOADISTRIBUIR := MI_SALDOADISTRIBUIR - MI_VALOR;

                    IF MI_VALORABONADO > 0 THEN
                      MI_VALORABONADO := 0;
                      MI_PRIORIDADABONADA := 1;
                    END IF;

                  ELSE
                    MI_VALOR := MI_SALDOADISTRIBUIR;
                    MI_VALORABONADO := MI_VALORABONADO + MI_SALDOADISTRIBUIR;
                    MI_PRIORIDADABONADA := MI_PRIORIDAD;
                    MI_SALDOADISTRIBUIR := 0;
                  END IF;

                  IF MI_PREANO = TO_NUMBER(TO_CHAR(SYSDATE,'YYYY')) THEN
                    MI_CAMPOS := ''||MI_CAMPO_CODIGO||' = '|| MI_CAMPO_CODIGO || ' + (' || MI_VALOR || ')';

                  ELSIF MI_PREANO = (TO_NUMBER(TO_CHAR(SYSDATE,'YYYY')) - 1) THEN
                    MI_CAMPOS := ''||MI_CAMPO_VIGANT||' = '|| MI_CAMPO_VIGANT || ' + (' || MI_VALOR || ')';

                  ELSIF MI_PREANO < (TO_NUMBER(TO_CHAR(SYSDATE,'YYYY')) - 1) THEN
                    MI_CAMPOS := ''||MI_CAMPO_VIGDFR||' = '|| MI_CAMPO_VIGDFR || ' + (' || MI_VALOR || ')';
                  END IF;

                    MI_CONDICION     := '     COMPANIA     = '''|| UN_COMPANIA ||'''
                                       AND  CODIGOACUERDO       = '''|| MI_CODIGOACUERDO ||'''
                                       AND  NUMERO_ORDEN = '''|| MI_NUMERO_ORDEN ||'''
                                       AND  CUOTA        = '  || MI_CUOTA;

                  BEGIN
                    BEGIN
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS,
                                                            UN_CONDICION => MI_CONDICION);

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>SQLCODE,
                        UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_ACT_CON
                      );
                  END;                                      
                END LOOP FACTURADOS;
              CLOSE MI_RSFACTURADOS;
            END LOOP CONCEPTOS;
          CLOSE MI_RSCONCEPTOS;

          IF MI_SALDOADISTRIBUIR > 0 THEN
            MI_ANOTRABAJO := MI_ANOTRABAJO + 1;
            MI_CUOTATRABAJO := MI_CUOTA;

            IF MI_ANOTRABAJO > UN_PREANO THEN
              IF MI_PREANOA = 0 THEN
                MI_CAMPOS        := 'PREANO = '||UN_PREANO;
                MI_CONDICION     := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                                     AND  CODIGOACUERDO = '''|| MI_CODIGOACUERDO ||'''
                                     AND  PREDIO        = '''|| MI_PREDIO ||'''
                                     AND  NUMERO_ORDEN  = '''|| MI_NUMERO_ORDEN ||'''
                                     AND  CUOTA         = '  || MI_CUOTA;

                BEGIN
                  BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD =>SQLCODE,
                      UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_AC_ANIO
                    );
                END;    
              END IF;
                MI_RETORNO := 0;
                RETURN MI_RETORNO;
            END IF;
          END IF;
        END LOOP;

        IF MI_PREANOA = 0 THEN
          MI_CAMPOS        := 'PREANO = '|| MI_ANOTRABAJO;
          MI_CONDICION     := '     COMPANIA      = '''||UN_COMPANIA||'''
                               AND  CODIGOACUERDO = '''||MI_CODIGOACUERDO||'''
                               AND  PREDIO        = '''||MI_PREDIO||'''
                               AND  NUMERO_ORDEN  = '''||MI_NUMERO_ORDEN||'''
                               AND  CUOTA         = '  ||MI_CUOTA;

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_AC_ANIO
              );
          END;    
        END IF;
      END LOOP FACTURADOSACUERDOS;
    CLOSE MI_RSFACTURADOSACUERDOS;

    -- ACTUALIZA EL TOTAL DE CAPITAL O INTERES
    MI_CAMPOVIGENCIA := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_ESCAPITAL <> 0,
                                              UN_SI        => 'CAPITAL',
                                              UN_NO        => 'INTERESES');

    MI_CAMPOS := ''|| MI_CAMPOVIGENCIA|| ' = ';
    MI_STRSQLCONCEPTOS := 'SELECT   CODIGO, 
                                    VIGANT, 
                                    VIGDFR
                           FROM     IP_CONCEPTOS
                           WHERE    COMPANIA                          =  '''|| UN_COMPANIA ||'''
                             AND    ANO                               =  '  || MI_ANOTRABAJO ||'
                             AND    PRIORIDAD                         IS NOT NULL 
                             AND    NVL('||MI_VIGENCIACONDICION||',0) <> 0
                           ORDER BY PRIORIDAD';

    EXECUTE IMMEDIATE 'SELECT COUNT(1)
                       FROM   ('|| MI_STRSQLCONCEPTOS ||')' INTO MI_CONTEO;

    IF MI_CONTEO = 0 THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_CONCERO
          );
      END;
    END IF;

    <<CONCEPTOS2>>
    OPEN MI_RSCONCEPTOS FOR MI_STRSQLCONCEPTOS;
      LOOP
        FETCH MI_RSCONCEPTOS
        INTO  MI_CODIGO, 
              MI_VIGANT, 
              MI_VIGDFR;
        EXIT WHEN MI_RSCONCEPTOS%NOTFOUND;
        MI_CAMPOS := MI_CAMPOS || 'C' ||MI_CODIGO || ' + ' ||
                     PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_VIGANT, 0) <> 0 AND MI_CODIGO <> MI_VIGANT,
                                           UN_SI        => 'C' || MI_VIGANT || ' + ',
                                           UN_NO        => '') ||
                     PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_VIGDFR, 0) <> 0 AND MI_CODIGO <> MI_VIGDFR,
                                           UN_SI        => 'C' || MI_VIGDFR || ' + ',
                                           UN_NO        => '');
      END LOOP CONCEPTOS2;
    CLOSE MI_RSCONCEPTOS;

    MI_CAMPOS := SUBSTR(MI_CAMPOS, 1, LENGTH(MI_CAMPOS) - 3);

    MI_CONDICION := '     COMPANIA      = '''|| UN_COMPANIA ||'''
                     AND  PREDIO        = '''|| UN_PREDIO ||'''
                     AND  CODIGOACUERDO = '''|| UN_ACUERDO ||'''';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => UN_T_FACTURADOSACU,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'CAMPO';
      MI_MSGERROR(1).VALOR := MI_CAMPOS;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_DIS_CAPINT_AC_CAP
          ,UN_REEMPLAZOS =>  MI_MSGERROR
        );
    END;    

    RETURN MI_RETORNO;                                 
  END FC_DISTRIBUIR_CAPITALINTERES;


PROCEDURE PR_CARGARINFORMACIONEXCDENTES
  /*
    NAME              : PR_CARGARINFORMACIONEXCDENTES En Access --> Aceptar_Click()
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 23/02/2017
    TIME              : 10:20 AM

    MODIFIER          : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 27/06/2017
    TIME              : 10:29 AM
    DESCRIPTION       : Estandarización y tipificación de parametros. 
                        Adición de los campos de auditoria.

    SOURCE MODULE     : IMPUESTO PREDIAL (60)
    DESCRIPTION       : Procedimiento que se encarga de insertar los excedentes en la tabla IP_PAGOSDOBLES. Y que se utiliza 
                        en el formulario CARGAR INFORMACIÓN DE EXCEDENTES

    @NAME:  cargarInformacionExcedentes
    @METHOD:  POST    
  */     
(
   UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
  ,UN_NUMEROORDEN          IN IP_USUARIOS_PREDIAL.NUMERO_ORDEN%TYPE
  ,UN_FACTURA              IN IP_PAGOSDOBLES.DOCNUM%TYPE
  ,UN_PREDIO               IN IP_USUARIOS_PREDIAL.CODIGO%TYPE
  ,UN_ANOCAUSOEXCEDENTE    IN PCK_SUBTIPOS.TI_ANIO
  ,UN_ANOAPLICAREXCEDENTE  IN PCK_SUBTIPOS.TI_ANIO
  ,UN_BANCO                IN IP_BANCOS.CODIGOBANCO%TYPE
  ,UN_OBSERVACIONES        IN IP_PAGOSDOBLES.OBSERVACIONES%TYPE
  ,UN_C1                   IN VARCHAR2
  ,UN_C2                   IN VARCHAR2
  ,UN_C3                   IN VARCHAR2
  ,UN_C4                   IN VARCHAR2
  ,UN_C13                  IN VARCHAR2
  ,UN_C14                  IN VARCHAR2
  ,UN_C15                  IN VARCHAR2
  ,UN_C16                  IN VARCHAR2
  ,UN_C17                  IN VARCHAR2
  ,UN_C18                  IN VARCHAR2
  ,UN_C19                  IN VARCHAR2
  ,UN_C20                  IN VARCHAR2
) 
AS
  MI_DESPRED           PCK_SUBTIPOS.TI_PARAMETRO; /*Concepto*/
  MI_DESCAR            PCK_SUBTIPOS.TI_PARAMETRO;
  MI_CONCEPTO          PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_VALORTOTAL        NUMBER;
  MI_VALOR             NUMBER;
  MI_RTA               PCK_SUBTIPOS.TI_RTA_ACME;
  MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO            PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_DESPRED := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                     ,UN_NOMBRE    => 'CONCEPTO DE DESCUENTO'
                                     ,UN_MODULO    => PCK_DATOS.MODULOPREDIAL
                                     ,UN_FECHA_PAR => SYSDATE);

  MI_DESCAR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                    ,UN_NOMBRE    => 'CONCEPTO PARA DESCUENTO CAR'
                                    ,UN_MODULO    => PCK_DATOS.MODULOPREDIAL
                                    ,UN_FECHA_PAR => SYSDATE);  

  MI_CONCEPTO(1).CLAVE := 1;
  MI_CONCEPTO(1).VALOR := UN_C1;

  MI_CONCEPTO(2).CLAVE := 2;
  MI_CONCEPTO(2).VALOR := UN_C2;

  MI_CONCEPTO(3).CLAVE := 3;
  MI_CONCEPTO(3).VALOR := UN_C3;

  MI_CONCEPTO(4).CLAVE := 4;
  MI_CONCEPTO(4).VALOR := UN_C4;

  MI_CONCEPTO(13).CLAVE := 13;
  MI_CONCEPTO(13).VALOR := UN_C13;

  MI_CONCEPTO(14).CLAVE := 14;
  MI_CONCEPTO(14).VALOR := UN_C14;

  MI_CONCEPTO(15).CLAVE := 15;
  MI_CONCEPTO(15).VALOR := UN_C15;

  MI_CONCEPTO(16).CLAVE := 16;
  MI_CONCEPTO(16).VALOR := UN_C16;

  MI_CONCEPTO(17).CLAVE := 17;
  MI_CONCEPTO(17).VALOR := UN_C17;

  MI_CONCEPTO(18).CLAVE := 18;
  MI_CONCEPTO(18).VALOR := UN_C18;

  MI_CONCEPTO(19).CLAVE := 19;
  MI_CONCEPTO(19).VALOR := UN_C19;

  MI_CONCEPTO(20).CLAVE := 20;
  MI_CONCEPTO(20).VALOR := UN_C20;

  MI_VALORTOTAL := 0;

  FOR i IN 1..20 LOOP
    IF i < 5 OR i > 12 THEN 
      IF MI_DESPRED = MI_CONCEPTO(i).CLAVE AND MI_CONCEPTO(i).VALOR > 0 THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'PREDIO';
          MI_MSGERROR(1).VALOR := UN_PREDIO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_DESCUENTOPOSITIVO
                                    ,UN_REEMPLAZOS => MI_MSGERROR);

        END;     
      END IF; 

      IF MI_DESCAR = MI_CONCEPTO(i).CLAVE AND MI_CONCEPTO(i).VALOR > 0 THEN  
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'PREDIO';
          MI_MSGERROR(1).VALOR := UN_PREDIO;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_DESCCARPOSITIVO
                                    ,UN_REEMPLAZOS => MI_MSGERROR);

        END;   
      END IF; 

      MI_VALORTOTAL := MI_VALORTOTAL + TO_NUMBER(MI_CONCEPTO(i).VALOR);
    END IF;
  END LOOP;

  IF UN_OBSERVACIONES IS NULL THEN
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_OBSERVACIONVACIA
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END; 
  END IF;

  MI_STRSQL := 'SELECT COUNT(1)
                FROM  IP_USUARIOS_PREDIAL
                WHERE COMPANIA     = '''||UN_COMPANIA   ||'''
                  AND CODIGO       = '''||UN_PREDIO     ||'''
                  AND NUMERO_ORDEN = '''||UN_NUMEROORDEN||'''
                  AND INDBORRADO       IN(0) 
                  AND CODIGO_NO_ACTIVO IN(0)';

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;                  

  IF MI_CONTEO IN(0) THEN   
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_NOPREDIO
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;
  END IF;

  MI_STRSQL := 'SELECT COUNT(1)
                FROM IP_BANCOS 
                WHERE COMPANIA    = '''||UN_COMPANIA||'''
                  AND CODIGOBANCO = '''||UN_BANCO   ||'''';

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;                  

  IF MI_CONTEO = 0 THEN   
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'BANCO';
      MI_MSGERROR(1).VALOR := UN_BANCO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_NOBANCO
                                ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;
  END IF;   

  MI_STRSQL := 'SELECT COUNT(1)
                FROM IP_PAGOSDOBLES 
                WHERE COMPANIA     = '''||UN_COMPANIA         ||'''
                  AND DOCNUM       = '''||UN_FACTURA          ||'''
                  AND PRECOD       = '''||UN_PREDIO           ||'''
                  AND NUMERO_ORDEN = '''||UN_NUMEROORDEN      ||'''
                  AND PREANO       =   '||UN_ANOCAUSOEXCEDENTE;

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;                  

  IF MI_CONTEO NOT IN(0) THEN   
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'FACTURA';
      MI_MSGERROR(1).VALOR := UN_FACTURA;
      MI_MSGERROR(2).CLAVE := 'PREDIO';
      MI_MSGERROR(2).VALOR := UN_PREDIO;
      MI_MSGERROR(3).CLAVE := 'ANIO';
      MI_MSGERROR(3).VALOR := UN_ANOCAUSOEXCEDENTE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_FACTURADUPLICADA
                                ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;
  END IF;   

  MI_STRSQL := 'SELECT COUNT(1)
                FROM  IP_PAGOSDOBLES 
                WHERE COMPANIA     = '''||UN_COMPANIA         ||'''
                  AND DOCNUM       = '''||UN_FACTURA          ||'''
                  AND PRECOD       = '''||UN_PREDIO           ||'''
                  AND NUMERO_ORDEN = '''||UN_NUMEROORDEN      ||'''
                  AND PREANO       =   '||UN_ANOCAUSOEXCEDENTE||'
                  AND VALOR        = '''||MI_VALORTOTAL       ||'''';                  

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;                  

  IF MI_CONTEO >= 1 THEN   
    MI_RTA := 0;

    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      MI_MSGERROR(1).CLAVE := 'PREDIO';
      MI_MSGERROR(1).VALOR := UN_PREDIO;
      MI_MSGERROR(2).CLAVE := 'ANIO';
      MI_MSGERROR(2).VALOR := UN_ANOCAUSOEXCEDENTE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_PREDIOVALDUPLICADO
                                ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;
  END IF; 

  MI_CAMPOS := 'COMPANIA
               ,DOCNUM
               ,PRECOD
               ,NUMERO_ORDEN
               ,PREANO
               ,VALOR
               ,DATE_CREATED
               ,CREATED_BY
               ,TIPO
               ,OBSERVACIONES
               ,ANO_EXCEDENTE
               ,BANCO
               ,C1
               ,C2
               ,C3
               ,C4
               ,C13
               ,C14
               ,C15
               ,C16
               ,C17
               ,C18
               ,C19
               ,C20';

  MI_VALORES := ''''||UN_COMPANIA           ||'''
                ,'''||UN_FACTURA            ||'''
                ,'''||UN_PREDIO             ||'''
                ,'''||UN_NUMEROORDEN        ||'''
                ,  '||UN_ANOCAUSOEXCEDENTE  ||'
                ,  '||MI_VALORTOTAL         ||'
                ,SYSDATE
                ,'''||UN_USUARIO            ||'''
                ,''D''
                ,'''||UN_OBSERVACIONES      ||'''
                ,  '||UN_ANOAPLICAREXCEDENTE||'
                ,'''||UN_BANCO              ||'''
                ,  '||UN_C1                 ||'
                ,  '||UN_C2                 ||'
                ,  '||UN_C3                 ||'
                ,  '||UN_C4                 ||'
                ,  '||UN_C13                ||'
                ,  '||UN_C14                ||'
                ,  '||UN_C15                ||'
                ,  '||UN_C16                ||'
                ,  '||UN_C17                ||'
                ,  '||UN_C18                ||'
                ,  '||UN_C19                ||'
                ,  '||UN_C20                ||'';                      

  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA  => 'IP_PAGOSDOBLES'
                                             ,UN_ACCION => 'I'
                                             ,UN_CAMPOS => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
    MI_MSGERROR(1).CLAVE := 'PREDIO';
    MI_MSGERROR(1).VALOR := UN_PREDIO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ER_PREDIAL_INSERT_IPPAGODOBLE
                              ,UN_REEMPLAZOS => MI_MSGERROR);
  END;                                               
END PR_CARGARINFORMACIONEXCDENTES;  
END PCK_PREDIAL_FIN;