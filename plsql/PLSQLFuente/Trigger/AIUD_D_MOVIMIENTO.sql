create or replace TRIGGER "AIUD_D_MOVIMIENTO" 
/* 
  NAME              : AIUD_D_MOVIMIENTO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : 
  DATE MIGRADOR     : 
  TIME              : 
  SOURCE MODULE     : 
  MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS - SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MODIFIED     : 27/01/2017                   - 13/01/2022 
  TIME              : 04:30 PM
  DESCRIPTION       : Operaciones y calculos despues de afectar el detalle de movimientos de Almacén.
                      Se cambia a un trigger compuesto para mejorar su funcionamiento
  MODIFIER          : CARLOS MAURICIO ALARCON CASALLAS
  DATE MODIFIED     : 21/04/2022
  TICKET            : 7713136_ALMACEN
  DESCRIPTION       : Se agrega una condicion when delete al UN_VALORUNITARIO que se envia al 
  					  PCK_ALMACEN.PR_CORRECCIONDEVALOR, ya que este valor se estaba enviando como null
  					  lo cual no permitia eliminar el movimiento.
*/
FOR INSERT OR DELETE OR UPDATE OF  
                            CANTIDAD
                            ,PORCIVA
                            ,UBICACION
ON D_MOVIMIENTO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER 
    MI_RTA                 PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA2                CLOB;
	MI_PARAMETRO           VARCHAR2(255 CHAR);
    MI_PARAMETROFL         VARCHAR2(255 CHAR);	--JM CC585 						  
    TYPE REGISTRO IS RECORD 
    (
        COMPANIA                  PCK_SUBTIPOS.TI_COMPANIA,
        CANTIDAD_ANTERIOR         D_MOVIMIENTO.CANTIDAD%TYPE,
        CANTIDAD_NUEVA            D_MOVIMIENTO.CANTIDAD%TYPE,
        FECHAINICIAL              DATE ,
        HORA                      DATE ,
        FECHAINICIALKARDEX        DATE ,
        FECHAFINAL                DATE ,
        ELEMENTOINICIAL           D_MOVIMIENTO.ELEMENTO%TYPE,
        TIPOMOVIMIENTO            D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
        MOVIMIENTO                D_MOVIMIENTO.MOVIMIENTO%TYPE,
        ESPECIFICACION            D_MOVIMIENTO.ESPECIFICACION%TYPE,
        TERCERO                   PCK_SUBTIPOS.TI_TERCERO,
        SUCURSAL                  PCK_SUBTIPOS.TI_SUCURSAL,
        TIPOMOVIMIENTO_AFECT      D_MOVIMIENTO.TIPOMOVIMIENTO_AFECT%TYPE,
        MOVIMIENTO_AFECT          D_MOVIMIENTO.MOVIMIENTO_AFECT%TYPE,
        CODIGO_AFECT              D_MOVIMIENTO.CODIGO_AFECT%TYPE,
        CODIGO                    D_MOVIMIENTO.CODIGO%TYPE,
        SERIE                     D_MOVIMIENTO.SERIE%TYPE,
        CANTIDAD_AFECT            D_MOVIMIENTO.CANTIDAD_AFECT%TYPE,
        INDREG                    D_MOVIMIENTO.IND_REG%TYPE,
        VALORUNITARIO             PCK_SUBTIPOS.TI_DOBLE := 0,
        VALORUNITARIO_ANTERIOR    D_MOVIMIENTO.VALORUNITARIO%TYPE ,
        VALORTOTAL                D_MOVIMIENTO.VALORTOTAL%TYPE ,
        VALORANTERIOR             D_MOVIMIENTO.VALORANTERIOR%TYPE ,
        PORCIVA                   D_MOVIMIENTO.PORCIVA%TYPE :=0,
        PORC_IMPCONSUMO           D_MOVIMIENTO.PORC_IMPCONSUMO%TYPE :=0,
        VALORUNITARIO_ANTESIVA    PCK_SUBTIPOS.TI_DOBLE := 0,
        OPERACION                 D_MOVIMIENTO.OPERACION%TYPE,
        CENTRODECOSTO             D_MOVIMIENTO.CENTRODECOSTO%TYPE,
        TIPOMOVASOCIADO           D_MOVIMIENTO.TIPOMOVASOCIADO%TYPE,
        MOVASOCIADO               D_MOVIMIENTO.MOVASOCIADO%TYPE,
		FUENTEDERECURSO           D_MOVIMIENTO.FUENTEDERECURSO%TYPE,
        REFERENCIA_CNT            D_MOVIMIENTO.REFERENCIA_CNT%TYPE,
        AUXILIAR                  D_MOVIMIENTO.AUXILIAR%TYPE,
        CODIGOPROYECTO            D_MOVIMIENTO.CODIGOPROYECTO%TYPE,
        LOTE                      D_MOVIMIENTO.LOTE%TYPE											
    );  

    TYPE REGISTROS IS TABLE OF REGISTRO INDEX BY BINARY_INTEGER ;
    TABLACT REGISTROS;
    MI_POSACT NUMBER DEFAULT 0;
    MI_FECHA  DATE;
    RTA    NUMBER;
    ACTUALIZA  NUMBER;
AFTER EACH ROW IS
BEGIN
	MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(:NEW.COMPANIA,'MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN',PCK_DATOS.MODULOALMACEN,SYSDATE),'NO'); 
    MI_PARAMETROFL := NVL(PCK_SYSMAN_UTL.FC_PAR(:NEW.COMPANIA,'MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN',PCK_DATOS.MODULOALMACEN,SYSDATE),'NO'); --JM CC585 
    ACTUALIZA:=0;
    IF UPDATING THEN
        IF :OLD.COMPANIA            <>:NEW.COMPANIA OR
            :OLD.TIPOMOVIMIENTO     <>:NEW.TIPOMOVIMIENTO OR
            :OLD.MOVIMIENTO         <>:NEW.MOVIMIENTO OR
            :OLD.CODIGO             <>:NEW.CODIGO  THEN
            ACTUALIZA:=1;
        ELSE
            MI_POSACT := MI_POSACT+1;
            TABLACT(MI_POSACT).COMPANIA                 :=:OLD.COMPANIA;
            TABLACT(MI_POSACT).FECHAINICIAL             :=TO_DATE(NVL(PCK_SYSMAN_UTL.FC_PAR(:NEW.COMPANIA,'FECHA DE CORTE PARA INICIO DEL ALMACEN',10,SYSDATE),SYSDATE),'DD/MM/YYYY');
            TABLACT(MI_POSACT).FECHAINICIALKARDEX       :=:OLD.FECHA;
            TABLACT(MI_POSACT).FECHAFINAL               :=:NEW.FECHA; 
            TABLACT(MI_POSACT).CANTIDAD_ANTERIOR        :=:OLD.CANTIDAD;
            TABLACT(MI_POSACT).CANTIDAD_NUEVA           :=:NEW.CANTIDAD;
            TABLACT(MI_POSACT).ELEMENTOINICIAL          :=:NEW.ELEMENTO;
            TABLACT(MI_POSACT).TIPOMOVIMIENTO           :=:OLD.TIPOMOVIMIENTO;
            TABLACT(MI_POSACT).MOVIMIENTO               :=:OLD.MOVIMIENTO;
            TABLACT(MI_POSACT).TIPOMOVIMIENTO_AFECT     :=:OLD.TIPOMOVIMIENTO_AFECT;
			TABLACT(MI_POSACT).TERCERO                  :=:NEW.TERCERO;  
            TABLACT(MI_POSACT).SUCURSAL                 :=:NEW.SUCURSAL; 
            TABLACT(MI_POSACT).MOVIMIENTO_AFECT         :=:OLD.MOVIMIENTO_AFECT;
            TABLACT(MI_POSACT).CODIGO_AFECT             :=:OLD.CODIGO_AFECT;
            TABLACT(MI_POSACT).CANTIDAD_AFECT           :=:OLD.CANTIDAD_AFECT; 
            TABLACT(MI_POSACT).INDREG                   :=:NEW.IND_REG;
            TABLACT(MI_POSACT).PORCIVA                  :=:NEW.PORCIVA;
            TABLACT(MI_POSACT).PORC_IMPCONSUMO          :=:NEW.PORC_IMPCONSUMO;
            TABLACT(MI_POSACT).VALORUNITARIO_ANTESIVA   :=:NEW.VLRUNITARIO_ANTESIVA;
            TABLACT(MI_POSACT).CODIGO                   :=:NEW.CODIGO; 
            TABLACT(MI_POSACT).VALORUNITARIO            :=:NEW.VALORUNITARIO; 
            TABLACT(MI_POSACT).VALORUNITARIO_ANTERIOR   :=:OLD.VALORUNITARIO; 
            TABLACT(MI_POSACT).VALORTOTAL               :=:NEW.VALORTOTAL;
            TABLACT(MI_POSACT).VALORANTERIOR            :=:OLD.VALORTOTAL;
            TABLACT(MI_POSACT).HORA                     :=:OLD.HORA; 
            TABLACT(MI_POSACT).SERIE                    :=:OLD.SERIE; 
            TABLACT(MI_POSACT).OPERACION                :='UPDATE';
            TABLACT(MI_POSACT).CENTRODECOSTO            :=:OLD.CENTRODECOSTO;
            TABLACT(MI_POSACT).CENTRODECOSTO            :=:NEW.CENTRODECOSTO;
            TABLACT(MI_POSACT).TIPOMOVASOCIADO          :=:OLD.TIPOMOVASOCIADO;
            TABLACT(MI_POSACT).MOVASOCIADO              :=:OLD.MOVASOCIADO;
			TABLACT(MI_POSACT).FUENTEDERECURSO          :=:NEW.FUENTEDERECURSO;
            TABLACT(MI_POSACT).REFERENCIA_CNT           :=:NEW.REFERENCIA_CNT;
            TABLACT(MI_POSACT).AUXILIAR                 :=:NEW.AUXILIAR;
            TABLACT(MI_POSACT).CODIGOPROYECTO           :=:NEW.CODIGOPROYECTO;
            TABLACT(MI_POSACT).LOTE                     :=:NEW.LOTE;																  
        END IF;
    END IF;
    IF DELETING OR ACTUALIZA<>0 THEN
        MI_POSACT := MI_POSACT+1;
        TABLACT(MI_POSACT).COMPANIA             :=:OLD.COMPANIA;
        TABLACT(MI_POSACT).FECHAINICIALKARDEX   :=:OLD.FECHA;
        TABLACT(MI_POSACT).FECHAFINAL           :=:OLD.FECHA; 
        TABLACT(MI_POSACT).CANTIDAD_ANTERIOR    :=:OLD.CANTIDAD;
        TABLACT(MI_POSACT).CANTIDAD_NUEVA       :=:OLD.CANTIDAD;
        TABLACT(MI_POSACT).ELEMENTOINICIAL      :=:OLD.ELEMENTO;
        TABLACT(MI_POSACT).TIPOMOVIMIENTO       :=:OLD.TIPOMOVIMIENTO;
        TABLACT(MI_POSACT).MOVIMIENTO           :=:OLD.MOVIMIENTO; 
        TABLACT(MI_POSACT).TIPOMOVIMIENTO_AFECT :=:OLD.TIPOMOVIMIENTO_AFECT;
        TABLACT(MI_POSACT).MOVIMIENTO_AFECT     :=:OLD.MOVIMIENTO_AFECT;
        TABLACT(MI_POSACT).CODIGO_AFECT         :=:OLD.CODIGO_AFECT;
        TABLACT(MI_POSACT).CANTIDAD_AFECT       :=:OLD.CANTIDAD_AFECT; 
        TABLACT(MI_POSACT).CODIGO               :=:OLD.CODIGO; 
        TABLACT(MI_POSACT).SERIE                :=:OLD.SERIE; 
        TABLACT(MI_POSACT).INDREG               :=:OLD.IND_REG;
        TABLACT(MI_POSACT).HORA                 :=:OLD.HORA;
        TABLACT(MI_POSACT).CENTRODECOSTO        :=:OLD.CENTRODECOSTO;
        TABLACT(MI_POSACT).TIPOMOVASOCIADO      :=:OLD.TIPOMOVASOCIADO;
        TABLACT(MI_POSACT).MOVASOCIADO          :=:OLD.MOVASOCIADO;
        TABLACT(MI_POSACT).VALORTOTAL           :=:OLD.VALORTOTAL;
		TABLACT(MI_POSACT).FUENTEDERECURSO      :=:OLD.FUENTEDERECURSO;
        TABLACT(MI_POSACT).REFERENCIA_CNT       :=:OLD.REFERENCIA_CNT;
        TABLACT(MI_POSACT).AUXILIAR             :=:OLD.AUXILIAR;
        TABLACT(MI_POSACT).CODIGOPROYECTO       :=:OLD.CODIGOPROYECTO;
        TABLACT(MI_POSACT).LOTE                 :=:OLD.LOTE;															  
        TABLACT(MI_POSACT).OPERACION            :='DELETE';


   END IF;

    IF INSERTING  OR ACTUALIZA<>0 THEN
        MI_POSACT := MI_POSACT+1;
        TABLACT(MI_POSACT).COMPANIA               :=:NEW.COMPANIA;
        TABLACT(MI_POSACT).FECHAINICIAL           :=TO_DATE(NVL(PCK_SYSMAN_UTL.FC_PAR(:NEW.COMPANIA,'FECHA DE CORTE PARA INICIO DEL ALMACEN',10,SYSDATE),SYSDATE),'DD/MM/YYYY');
        TABLACT(MI_POSACT).HORA                   :=:NEW.HORA; 
        TABLACT(MI_POSACT).FECHAINICIALKARDEX     :=:NEW.FECHA;
        TABLACT(MI_POSACT).FECHAFINAL             :=:NEW.FECHA; 
        TABLACT(MI_POSACT).CANTIDAD_ANTERIOR      :=0;
        TABLACT(MI_POSACT).CANTIDAD_NUEVA         :=:NEW.CANTIDAD;
        TABLACT(MI_POSACT).CODIGO                 :=:NEW.CODIGO; 
        TABLACT(MI_POSACT).SERIE                  :=:NEW.SERIE; 
        TABLACT(MI_POSACT).ELEMENTOINICIAL        :=:NEW.ELEMENTO;
        TABLACT(MI_POSACT).TIPOMOVIMIENTO         :=:NEW.TIPOMOVIMIENTO;
        TABLACT(MI_POSACT).MOVIMIENTO             :=:NEW.MOVIMIENTO; 
        TABLACT(MI_POSACT).ESPECIFICACION         :=:NEW.ESPECIFICACION;  
        TABLACT(MI_POSACT).TERCERO                :=:NEW.TERCERO;  
        TABLACT(MI_POSACT).SUCURSAL               :=:NEW.SUCURSAL; 
        TABLACT(MI_POSACT).INDREG                 :=:NEW.IND_REG;
        TABLACT(MI_POSACT).VALORUNITARIO          :=:NEW.VALORUNITARIO; 
        TABLACT(MI_POSACT).VALORUNITARIO_ANTERIOR :=0; 
        TABLACT(MI_POSACT).VALORTOTAL             :=:NEW.VALORTOTAL;
        TABLACT(MI_POSACT).PORCIVA                :=:NEW.PORCIVA;
        TABLACT(MI_POSACT).PORC_IMPCONSUMO        :=:NEW.PORC_IMPCONSUMO;
        TABLACT(MI_POSACT).VALORUNITARIO_ANTESIVA :=:NEW.VLRUNITARIO_ANTESIVA;
        TABLACT(MI_POSACT).OPERACION              :='INSERT';
        --7713193_Almacen drangel
        TABLACT(MI_POSACT).CENTRODECOSTO          :=:NEW.CENTRODECOSTO;
		TABLACT(MI_POSACT).TIPOMOVIMIENTO_AFECT   :=:NEW.TIPOMOVIMIENTO_AFECT;
        TABLACT(MI_POSACT).MOVIMIENTO_AFECT       :=:NEW.MOVIMIENTO_AFECT;
        TABLACT(MI_POSACT).TIPOMOVASOCIADO        :=:NEW.TIPOMOVASOCIADO;
        TABLACT(MI_POSACT).MOVASOCIADO            :=:NEW.MOVASOCIADO;															  
		TABLACT(MI_POSACT).CODIGO_AFECT           :=:NEW.CODIGO_AFECT;															  
		TABLACT(MI_POSACT).FUENTEDERECURSO        :=:NEW.FUENTEDERECURSO;
        TABLACT(MI_POSACT).REFERENCIA_CNT         :=:NEW.REFERENCIA_CNT;
        TABLACT(MI_POSACT).AUXILIAR               :=:NEW.AUXILIAR;
        TABLACT(MI_POSACT).CODIGOPROYECTO         :=:NEW.CODIGOPROYECTO;
        TABLACT(MI_POSACT).LOTE                   :=:NEW.LOTE;																
    END IF;

END AFTER EACH ROW;
AFTER STATEMENT IS
BEGIN
    FOR i IN 1..MI_POSACT LOOP
        /*
        * Se incluye el parametro UN_VALORUNITARIOENTRADA, para actualizar los datos cuando se modifica
        * el detalle en la entrada y sincronizar el valor del detalle con el devolutivo
        */
        PCK_ALMACEN.PR_CORRECCIONDEVALOR (
                    UN_COMPANIA         =>   TABLACT(MI_POSACT).COMPANIA,
                    UN_TIPOMOVIMIENTO   =>   TABLACT(MI_POSACT).TIPOMOVIMIENTO,
                    UN_MOVIMIENTO       =>   TABLACT(MI_POSACT).MOVIMIENTO, 
                    UN_ELEMENTO         =>   TABLACT(MI_POSACT).ELEMENTOINICIAL,
                    UN_SERIE            =>   TABLACT(MI_POSACT).SERIE,
                    UN_VALORUNITARIO    =>   CASE TABLACT(MI_POSACT).OPERACION
                                        WHEN 'INSERT' THEN TABLACT(MI_POSACT).VALORUNITARIO
                                        WHEN 'UPDATE' THEN TABLACT(MI_POSACT).VALORUNITARIO - TABLACT(MI_POSACT).VALORUNITARIO_ANTERIOR
                                        WHEN 'DELETE' THEN TABLACT(MI_POSACT).VALORTOTAL * -1
                                        ELSE TABLACT(MI_POSACT).VALORUNITARIO_ANTERIOR * -1
                                    END,
                    UN_VALORUNITARIOENTRADA => CASE TABLACT(MI_POSACT).OPERACION
                                        WHEN 'DELETE' THEN 0
                                        ELSE TABLACT(MI_POSACT).VALORUNITARIO
                                        END,
                    UN_ESPECIFICACION   => CASE TABLACT(MI_POSACT).OPERACION
                                        WHEN 'DELETE' THEN 'PLACA ANULADA'
                                        ELSE TABLACT(MI_POSACT).ESPECIFICACION
                                        END );  

        PCK_ALMACEN_COM5.PR_DESAGREGAR_ELMT_ENTR(TABLACT(MI_POSACT).COMPANIA,
                                   TABLACT(MI_POSACT).TIPOMOVASOCIADO,
                                   TABLACT(MI_POSACT).MOVASOCIADO,
                                   TABLACT(MI_POSACT).ELEMENTOINICIAL,
                                    CASE TABLACT(MI_POSACT).OPERACION
                                        WHEN 'INSERT' THEN TABLACT(MI_POSACT).CANTIDAD_NUEVA
                                        WHEN 'UPDATE' THEN TABLACT(MI_POSACT).CANTIDAD_NUEVA - TABLACT(MI_POSACT).CANTIDAD_ANTERIOR
                                        WHEN 'DELETE' THEN 0 END,
                                   TABLACT(MI_POSACT).VALORTOTAL,
                                   TABLACT(MI_POSACT).TIPOMOVIMIENTO,
                                   TABLACT(MI_POSACT).VALORUNITARIO);

        IF TABLACT(MI_POSACT).INDREG IN (0) AND NOT DELETING THEN
            --VERIFICA SI EL ELEMENTO TIENE MOVIMIENTOS POSTERIORES      
			IF MI_PARAMETRO = 'SI' THEN
                PCK_ALMACEN_COM2.PR_MOVSPOSTERIORESAUX(
                            UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA,
                            UN_FECHA          => TABLACT(MI_POSACT).FECHAINICIALKARDEX,
                            UN_HORA           => TABLACT(MI_POSACT).HORA,
                            UN_ELEMENTO       => TABLACT(MI_POSACT).ELEMENTOINICIAL,
                            UN_SERIE          => TABLACT(MI_POSACT).SERIE,
                            UN_TIPOMOVIMIENTO => TABLACT(MI_POSACT).TIPOMOVIMIENTO,
                            UN_MOVIMIENTO     => TABLACT(MI_POSACT).MOVIMIENTO,
                            UN_FUENTER        => TABLACT(MI_POSACT).FUENTEDERECURSO,
                            UN_REFERENCIA     => TABLACT(MI_POSACT).REFERENCIA_CNT,
                            UN_AUXILIAR       => TABLACT(MI_POSACT).AUXILIAR,
                            UN_PROYECTO       => TABLACT(MI_POSACT).CODIGOPROYECTO,
                            UN_CCOSTO         => TABLACT(MI_POSACT).CENTRODECOSTO,
                            UN_LOTE           => TABLACT(MI_POSACT).LOTE);
            ELSE													   
				PCK_ALMACEN.PR_MOVIMIENTOSPOSTERIORES(
							UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA, 
							UN_FECHA          => TABLACT(MI_POSACT).FECHAINICIALKARDEX, 
							UN_HORA           => TABLACT(MI_POSACT).HORA, 
							UN_ELEMENTO       => TABLACT(MI_POSACT).ELEMENTOINICIAL, 
                            UN_SERIE          => TABLACT(MI_POSACT).SERIE); 													   
			END IF;	   
            BEGIN
            
                MI_RTA:=PCK_ALMACEN.FC_INSERTARDETALLES( 
                            UN_COMPANIA                => TABLACT(MI_POSACT).COMPANIA
                            ,UN_FECHAINICIAL           => TABLACT(MI_POSACT).FECHAINICIAL
                            ,UN_FECHAFINAL             => TABLACT(MI_POSACT).FECHAFINAL
                            ,UN_ELEMENTOINICIAL        => TABLACT(MI_POSACT).ELEMENTOINICIAL
                            ,UN_CANTIDAD               => TABLACT(MI_POSACT).CANTIDAD_NUEVA
                            ,UN_CODIGO                 => TABLACT(MI_POSACT).CODIGO
                            ,UN_SERIE                  => TABLACT(MI_POSACT).SERIE
                            ,UN_TIPOMOVIMIENTO         => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                            ,UN_MOVIMIENTO             => TABLACT(MI_POSACT).MOVIMIENTO
                            ,UN_ESPECIFICACION         => TABLACT(MI_POSACT).ESPECIFICACION
                            ,UN_TERCERO                => TABLACT(MI_POSACT).TERCERO
                            ,UN_SUCURSAL               => TABLACT(MI_POSACT).SUCURSAL
                            ,UN_VALORUNITARIO          => TABLACT(MI_POSACT).VALORUNITARIO
                            ,UN_VALORTOTAL             => TABLACT(MI_POSACT).VALORTOTAL
                            ,UN_PORCIVA                => TABLACT(MI_POSACT).PORCIVA
                            ,UN_PORCIMPCONSUMO         => TABLACT(MI_POSACT).PORC_IMPCONSUMO
                            ,UN_VALORUNITARIO_ANTESIVA =>TABLACT(MI_POSACT).VALORUNITARIO_ANTESIVA
                            ,UN_CENTRODECOSTO          =>TABLACT(MI_POSACT).CENTRODECOSTO 
							,UN_TIPOMOVIMIENTO_AFECT   =>TABLACT(MI_POSACT).TIPOMOVIMIENTO_AFECT 
                            ,UN_MOVIMIENTO_AFECT       =>TABLACT(MI_POSACT).MOVIMIENTO_AFECT
							,UN_CODIGO_AFECT           =>TABLACT(MI_POSACT).CODIGO_AFECT
							,UN_FUENTER                =>TABLACT(MI_POSACT).FUENTEDERECURSO
                            ,UN_REFERENCIA             => TABLACT(MI_POSACT).REFERENCIA_CNT
                            ,UN_AUXILIAR               => TABLACT(MI_POSACT).AUXILIAR
                            ,UN_PROYECTO               => TABLACT(MI_POSACT).CODIGOPROYECTO
                            ,UN_LOTE                   => TABLACT(MI_POSACT).LOTE);
                            --NULL;
                IF MI_RTA = 'FALSE' THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;                     
                END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                    ,UN_TABLAERROR => 'D_MOVIMIENTO'
                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_NOEXISTSALEL
                    );
            END;

            MI_RTA:=PCK_ALMACEN.FC_ACTUALIZAVALORTOTAL(
                            UN_COMPANIA                => TABLACT(MI_POSACT).COMPANIA
                            ,UN_TIPOMOVIMIENTO         => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                            ,UN_MOVIMIENTO             => TABLACT(MI_POSACT).MOVIMIENTO
                            ,UN_PORCIVA                => TABLACT(MI_POSACT).PORCIVA
                            ,UN_VALORUNITARIO          => TABLACT(MI_POSACT).VALORUNITARIO
                            ,UN_CANTIDAD_NUEVA         => TABLACT(MI_POSACT).CANTIDAD_NUEVA
                            ,UN_CODIGO                 => TABLACT(MI_POSACT).CODIGO);

            BEGIN
                BEGIN
                    SELECT  FECHA
                    INTO    MI_FECHA
                    FROM(
                        SELECT   TIPOMOVIMIENTO
                                ,MOVIMIENTO
                                ,ELEMENTO
                                ,CODIGO
                                ,FECHA
                        FROM     D_MOVIMIENTO
                        WHERE    COMPANIA = TABLACT(MI_POSACT).COMPANIA
                          AND    ELEMENTO = TABLACT(MI_POSACT).ELEMENTOINICIAL
                          AND    FECHA    < TO_DATE(LPAD(EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAINICIALKARDEX), 4) ||'/' ||
                                                    LPAD(EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAINICIALKARDEX), 2) ||'/' || 
                                                    LPAD(EXTRACT(DAY FROM LAST_DAY( TABLACT(MI_POSACT).FECHAINICIALKARDEX)),2, '0') ,'YYYY/MM/DD')
                          AND    IND_REG  NOT IN (0)          
                        ORDER BY TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(HORA,'HH24:MI:SS'), 'DD/MM/YYYY HH24:MI:SS') DESC
                        ) TABLA
                    WHERE ROWNUM=1;
                    EXCEPTION WHEN OTHERS THEN
                        MI_FECHA := TABLACT(MI_POSACT).FECHAINICIALKARDEX;
                END ; 
                MI_RTA2:=PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM (
                                    UN_COMPANIA             => TABLACT(MI_POSACT).COMPANIA
                                    , UN_INTANOINICIAL      =>  EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTMESINICIAL      => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTANOFINAL        => EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTMESFINAL        => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_STRELEMENTOINICIAL => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                    , UN_STRELEMENTOFINAL   => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                    , UN_KARDEXGENERAL      => 0
                                    , UN_FECHAINICIAL       => TABLACT(MI_POSACT).FECHAINICIAL
                                , UN_FECHAFINAL         => TABLACT(MI_POSACT).FECHAFINAL
                                , UN_TIPOMOVIMIENTO     => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                                , UN_MOVIMIENTO         => TABLACT(MI_POSACT).MOVIMIENTO);	
                IF MI_RTA2 = 'FALSE' THEN
                    MI_REEMPLAZOS(1).CLAVE := 'MENSAJE';
                    MI_REEMPLAZOS(1).VALOR := SUBSTR(MI_RTA2,1,1000);
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
                END IF;
          EXCEPTION  WHEN OTHERS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD => SQLCODE,
                   UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONALIZADO,
                   UN_REEMPLAZOS => MI_REEMPLAZOS);    
          END;
            PCK_ALMACEN_COM5.PR_ACT_COMPONENTE (
                                UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA
                                ,UN_TIPO          => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                                ,UN_MOVIMIENTO    => TABLACT(MI_POSACT).MOVIMIENTO
                                ,UN_CODIGO        => TABLACT(MI_POSACT).CODIGO);

        ELSIF NOT TABLACT(MI_POSACT).INDREG=0 AND UPDATING THEN
            MI_RTA:=PCK_ALMACEN.FC_ACTUALIZAVALORTOTAL(
                            UN_COMPANIA                => TABLACT(MI_POSACT).COMPANIA
                            ,UN_TIPOMOVIMIENTO         => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                            ,UN_MOVIMIENTO             => TABLACT(MI_POSACT).MOVIMIENTO
                            ,UN_PORCIVA                => TABLACT(MI_POSACT).PORCIVA
                            ,UN_VALORUNITARIO          => TABLACT(MI_POSACT).VALORUNITARIO
                            ,UN_CANTIDAD_NUEVA         => TABLACT(MI_POSACT).CANTIDAD_NUEVA
                            ,UN_CODIGO                 => TABLACT(MI_POSACT).CODIGO);
            MI_RTA2:=PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM (
                                    UN_COMPANIA             => TABLACT(MI_POSACT).COMPANIA
                                    , UN_INTANOINICIAL      =>  EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTMESINICIAL      => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTANOFINAL        => EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_INTMESFINAL        => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                    , UN_STRELEMENTOINICIAL => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                    , UN_STRELEMENTOFINAL   => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                    , UN_KARDEXGENERAL      => 0
									, UN_FECHAINICIAL       => TABLACT(MI_POSACT).FECHAINICIAL
									, UN_FECHAFINAL         => TABLACT(MI_POSACT).FECHAFINAL
									, UN_TIPOMOVIMIENTO     => TABLACT(MI_POSACT).TIPOMOVIMIENTO
									, UN_MOVIMIENTO         => TABLACT(MI_POSACT).MOVIMIENTO);															  
            PCK_ALMACEN_COM5.PR_ACT_COMPONENTE (
                                UN_COMPANIA      => TABLACT(MI_POSACT).COMPANIA
                                ,UN_TIPO         => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                                ,UN_MOVIMIENTO    => TABLACT(MI_POSACT).MOVIMIENTO
                                ,UN_CODIGO        => TABLACT(MI_POSACT).CODIGO );


        ELSIF NOT TABLACT(MI_POSACT).INDREG=0 AND DELETING THEN   
            BEGIN 
                MI_RTA:=PCK_ALMACEN.FC_DEVUELVESALDOPEPS( 
                                                 UN_COMPANIA             => TABLACT(MI_POSACT).COMPANIA
                                                ,UN_TIPOMOVIMIENTO       => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                                                ,UN_TIPOMOVIMIENTO_AFECT => TABLACT(MI_POSACT).TIPOMOVIMIENTO_AFECT
                                                ,UN_ELEMENTO             => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                                ,UN_MOVIMIENTO           => TABLACT(MI_POSACT).MOVIMIENTO_AFECT
                                                ,UN_CODIGO               => TABLACT(MI_POSACT).CODIGO_AFECT
                                                ,UN_CANTIDAD_NUEVA       => TABLACT(MI_POSACT).CANTIDAD_NUEVA
                                                ,UN_CANTIDAD_AFECTADA    => TABLACT(MI_POSACT).CANTIDAD_AFECT
                                                ,UN_SERIE                => TABLACT(MI_POSACT).SERIE);
                                                --NULL;
                IF MI_RTA = 'FALSE' THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;                    
                END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                    ,UN_TABLAERROR => 'D_MOVIMIENTO'
                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ELIMINAR_DEPRE
                    );
            END;

            MI_RTA:=PCK_ALMACEN.FC_ACTUALIZAVALORTOTAL(
                         UN_COMPANIA               => TABLACT(MI_POSACT).COMPANIA
                        ,UN_TIPOMOVIMIENTO         => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                        ,UN_MOVIMIENTO             => TABLACT(MI_POSACT).MOVIMIENTO
                        ,UN_PORCIVA                => 0
                        ,UN_VALORUNITARIO          => 0
                        ,UN_CANTIDAD_NUEVA         => 0
                        ,UN_CODIGO                 => 0
                        );
          BEGIN
            MI_RTA2:=PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM (
                                 UN_COMPANIA           => TABLACT(MI_POSACT).COMPANIA
                                ,UN_INTANOINICIAL      =>  EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                ,UN_INTMESINICIAL      => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                ,UN_INTANOFINAL        => EXTRACT(YEAR FROM TABLACT(MI_POSACT).FECHAFINAL)
                                ,UN_INTMESFINAL        => EXTRACT(MONTH FROM TABLACT(MI_POSACT).FECHAFINAL)
                                ,UN_STRELEMENTOINICIAL => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                ,UN_STRELEMENTOFINAL   => TABLACT(MI_POSACT).ELEMENTOINICIAL
                                ,UN_KARDEXGENERAL      => 0
								,UN_FECHAINICIAL       => TABLACT(MI_POSACT).FECHAINICIAL
                                ,UN_FECHAFINAL         => TABLACT(MI_POSACT).FECHAFINAL
                                ,UN_TIPOMOVIMIENTO     => TABLACT(MI_POSACT).TIPOMOVIMIENTO
                                ,UN_MOVIMIENTO         => TABLACT(MI_POSACT).MOVIMIENTO																
                                );
            
            IF MI_RTA2 = 'FALSE' THEN
                MI_REEMPLAZOS(1).CLAVE := 'MENSAJE';
                MI_REEMPLAZOS(1).VALOR := SUBSTR(MI_RTA2,1,1000);
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
            END IF;
          EXCEPTION  WHEN OTHERS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONALIZADO,
                                       UN_REEMPLAZOS => MI_REEMPLAZOS
                                       );    
          END;


            MI_RTA:=PCK_ALMACEN.FC_ACTUALIZADEVOLUTIVO(
                            UN_COMPANIA => TABLACT(MI_POSACT).COMPANIA
                        ,UN_ELEMENTO => TABLACT(MI_POSACT).ELEMENTOINICIAL
                        ,UN_SERIE    => TABLACT(MI_POSACT).SERIE); 

            MI_RTA :=PCK_ALMACEN_COM2.FC_ACTUALIZA_DOC_ASOCIADO(UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA ,
                                                                UN_TIPOMOVIMIENTO => TABLACT(MI_POSACT).TIPOMOVIMIENTO ,
                                                                UN_MOVIMIENTO     => TABLACT(MI_POSACT).MOVIMIENTO,
                                                                UN_INDREG => -1 );                  
        END IF;       

        --bloque adcionado eamaya 31/05/2018
        IF TABLACT(MI_POSACT).INDREG NOT IN (0) AND NOT INSERTING THEN
            --VERIFICA SI EL ELEMENTO TIENE MOVIMIENTOS POSTERIORES      
			IF MI_PARAMETRO = 'SI' THEN
                PCK_ALMACEN_COM2.PR_MOVSPOSTERIORESAUX(
                            UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA,
                            UN_FECHA          => TABLACT(MI_POSACT).FECHAINICIALKARDEX,
                            UN_HORA           => TABLACT(MI_POSACT).HORA,
                            UN_ELEMENTO       => TABLACT(MI_POSACT).ELEMENTOINICIAL,
                            UN_SERIE          => TABLACT(MI_POSACT).SERIE,
                            UN_TIPOMOVIMIENTO => TABLACT(MI_POSACT).TIPOMOVIMIENTO,
                            UN_MOVIMIENTO     => TABLACT(MI_POSACT).MOVIMIENTO,
                            UN_FUENTER        => TABLACT(MI_POSACT).FUENTEDERECURSO,
                            UN_REFERENCIA     => TABLACT(MI_POSACT).REFERENCIA_CNT,
                            UN_AUXILIAR       => TABLACT(MI_POSACT).AUXILIAR,
                            UN_PROYECTO       => TABLACT(MI_POSACT).CODIGOPROYECTO,
                            UN_CCOSTO         => TABLACT(MI_POSACT).CENTRODECOSTO,
                            UN_LOTE           => TABLACT(MI_POSACT).LOTE);
            ELSE														  
				PCK_ALMACEN.PR_MOVIMIENTOSPOSTERIORES(
								UN_COMPANIA       => TABLACT(MI_POSACT).COMPANIA, 
								UN_FECHA          => TABLACT(MI_POSACT).FECHAINICIALKARDEX, 
								UN_HORA           => TABLACT(MI_POSACT).HORA, 
								UN_ELEMENTO       => TABLACT(MI_POSACT).ELEMENTOINICIAL, 													   
								UN_SERIE          => TABLACT(MI_POSACT).SERIE); 
			END IF;
        ---fin bloque adcionado eamaya 31/05/2018                     
        END IF;              
  END LOOP;                                   
  END AFTER STATEMENT;
END;
