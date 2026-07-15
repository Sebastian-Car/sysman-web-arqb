CREATE OR REPLACE TRIGGER "BI_SP_PAGO" 
  /*
      NAME              : BI_SP_PAGO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 28/07/2017
      TIME              : 10:29 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Valida e inserta los pagos

  */
FOR INSERT ON SP_PAGO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

 MI_RTA              NUMBER DEFAULT 0;
 MI_CONSECUTIVOABONO SP_ABONOS.CONSECUTIVO%TYPE DEFAULT 0 ;

 MI_COMPANIA         SP_PAGO.COMPANIA%TYPE;
 MI_CODIGORUTA       SP_PAGO.CODIGORUTA%TYPE DEFAULT ' ';
 MI_CICLO            SP_PAGO.CICLO%TYPE;
 MI_ANOPAGO          SP_PAGO.ANO%TYPE;
 MI_PERIODOPAGO      SP_PAGO.PERIODO%TYPE;
 MI_FECHAPAGO        SP_PAGO.FECHA%TYPE;
 MI_CREADOPOR        SP_PAGO.CREATED_BY%TYPE;
 MI_FECHA            VARCHAR2(10 CHAR);
 MI_RTABARRAS        VARCHAR2(50 CHAR);
 MI_CAMBIOCICLO      SP_USUARIO.CAMBIOCICLORUTA%TYPE;
 MI_AUTABONO         BOOLEAN;
 MI_UNCODTER         BOOLEAN;
 MI_UNCODCONV        BOOLEAN;
 MI_NIT              COMPANIA.NITCOMPANIA%TYPE;

BEFORE EACH ROW IS
BEGIN
    --VALIDA POR CADA FILA QUE SE PUEDA INSERTAR
    --Se obtienen los datos del usuario con parámetros IN OUT, En base al codigo de barras.

    MI_RTABARRAS := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_DATOSCODIGOBARRAS
                    ( UN_COMPANIA          => :NEW.COMPANIA
                     ,UN_CODIGOBARRAS      => :NEW.CODIGOBARRAS
                     ,UN_CODIGOINTERNO     => :NEW.CODIGOINTERNO
                     ,UN_CODIGORUTA        => :NEW.CODIGORUTA
                     ,UN_ANOUSUARIO        => :NEW.ANO
                     ,UN_PERIODOUSUARIO    => :NEW.PERIODO
                     ,UN_CICLOUSUARIO      => :NEW.CICLO
                     ,UN_FECHA             => MI_FECHA);

    MI_COMPANIA := :NEW.COMPANIA;
    MI_CODIGORUTA := :NEW.CODIGORUTA;
    MI_CICLO := :NEW.CICLO;
    MI_ANOPAGO := :NEW.ANO;
    MI_PERIODOPAGO := :NEW.PERIODO;
    MI_FECHAPAGO := :NEW.FECHA;
    MI_CREADOPOR := :NEW.CREATED_BY;

    :NEW.OPERACION := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_OPERACIONPAGO
                        (UN_COMPANIA      => :NEW.COMPANIA
                        ,UN_FECHAPAGO     => :NEW.FECHA
                        ,UN_CODIGOINTERNO => :NEW.CODIGOINTERNO
                        ,UN_CODIGOBARRAS  => :NEW.CODIGOBARRAS);


    :NEW.VALORPAGO := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_VALORAPAGAR
                      (UN_COMPANIA         => :NEW.COMPANIA
                      ,UN_CICLO            => :NEW.CICLO
                      ,UN_CODIGORUTA       => :NEW.CODIGORUTA
                      ,UN_OPERACION        => :NEW.OPERACION
                      ,UN_CONSECUTIVOABONO => MI_CONSECUTIVOABONO);

    MI_AUTABONO :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                      (UN_COMPANIA  => :NEW.COMPANIA,
                       UN_NOMBRE    => 'PERMITE AUTORIZACION DE ABONOS',
                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                       UN_FECHA_PAR => SYSDATE) = 'SI'
                    THEN TRUE ELSE FALSE END;

    IF NOT MI_AUTABONO THEN
        MI_UNCODTER :=  CASE WHEN  PCK_SYSMAN_UTL.FC_PAR
                            (UN_COMPANIA  => :NEW.COMPANIA,
                             UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI'
                             AND  PCK_SYSMAN_UTL.FC_PAR
                            (UN_COMPANIA  => :NEW.COMPANIA,
                             UN_NOMBRE    => 'PROCESO TERCERIZADO CON UN SOLO CÓDIGO DE BARRAS',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI'
                        THEN TRUE ELSE FALSE END;

        IF MI_UNCODTER  THEN
            :NEW.PAGOTERCERIZADO := PCK_SERVICIOS_PUBLICOS_COM2.FC_VALORPAGOTERCERIZADO
                              (UN_COMPANIA    => :NEW.COMPANIA
                              ,UN_CICLO       => :NEW.CICLO
                              ,UN_CODIGORUTA  => :NEW.CODIGORUTA
                              ,UN_ANO         => :NEW.ANO
                              ,UN_PERIODO     => :NEW.PERIODO
                              ,UN_TERCERIZADO => 'SI');
        ELSE
            :NEW.PAGOTERCERIZADO := 0;
        END IF;

        BEGIN
            SELECT  NITCOMPANIA
            INTO    MI_NIT
            FROM    COMPANIA
            WHERE   CODIGO = :NEW.COMPANIA;
        END;

        MI_UNCODCONV := CASE WHEN PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS
                                (UN_COMPANIA => :NEW.COMPANIA
                                ,UN_NIT      => MI_NIT) <> 0
                        THEN TRUE ELSE FALSE END;

        IF MI_UNCODCONV AND NOT MI_AUTABONO THEN
              :NEW.PAGOCONVENIOS := PCK_SERVICIOS_PUBLICOS_COM2.FC_VALORPAGOCONVENIOS
                                  (UN_COMPANIA    => :NEW.COMPANIA
                                  ,UN_CICLO       => :NEW.CICLO
                                  ,UN_CODIGORUTA  => :NEW.CODIGORUTA
                                  ,UN_ANO         => :NEW.ANO
                                  ,UN_PERIODO     => :NEW.PERIODO
                                  ,UN_CONVENIO    => 'SI');
        ELSE
            :NEW.PAGOCONVENIOS := 0;
        END IF;
    ELSE
        :NEW.PAGOTERCERIZADO := 0;
        :NEW.PAGOCONVENIOS := 0;
    END IF;

    MI_RTA := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_VALIDARPAGO
              ( UN_COMPANIA          => :NEW.COMPANIA
               ,UN_FECHAPAGO         => :NEW.FECHA
               ,UN_CODIGOINTERNO     => :NEW.CODIGOINTERNO
               ,UN_CODIGOBARRAS      => :NEW.CODIGOBARRAS
               ,UN_OPERACION         => :NEW.OPERACION
               ,UN_VALORTER          => :NEW.PAGOTERCERIZADO
               ,UN_VALORCONVE        => :NEW.PAGOCONVENIOS);
END BEFORE EACH ROW;

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN
    IF MI_RTA <>0 THEN  --Si se permite Insertar.
      --Registra los detalle de los abonos.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_REGISTRARPAGO
            (UN_COMPANIA            => :NEW.COMPANIA
            ,UN_FECHAPAGO           => :NEW.FECHA
            ,UN_BANCOPAGO           => :NEW.BANCO
            ,UN_NUMEROPAQUETEPAGO   => :NEW.NUMEROPAQUETE
            ,UN_CONSECUTIVO         => :NEW.CONSECUTIVO
            ,UN_CODIGOINTERNO       => :NEW.CODIGOINTERNO
            ,UN_CODIGORUTA          => :NEW.CODIGORUTA
            ,UN_CICLO               => :NEW.CICLO
            ,UN_ANO                 => :NEW.ANO
            ,UN_PERIODO             => :NEW.PERIODO
            ,UN_VALORPAGO           => :NEW.VALORPAGO
            ,UN_OPERACION           => :NEW.OPERACION
            ,UN_CONSECUTIVOABONO    => MI_CONSECUTIVOABONO
            ,UN_USUARIO             => :NEW.CREATED_BY );
    END IF;

END AFTER EACH ROW;


AFTER STATEMENT IS --Ejecución despues de una consulta DML
BEGIN
    PCK_SERVICIOS_PUBLICOS_COM2.PR_CALCULAPRODUCDOBLE
        ( UN_COMPANIA    => MI_COMPANIA
         ,UN_CICLO       => MI_CICLO
         ,UN_ANO         => MI_ANOPAGO
         ,UN_PERIODO     => MI_PERIODOPAGO
         ,UN_FECHAINI    => MI_FECHA
         ,UN_FECHAFIN    => MI_FECHA
         ,UN_USUARIOINI  => MI_CODIGORUTA
         ,UN_USUARIOFIN  => MI_CODIGORUTA
         ,UN_USUARIO     => MI_CREADOPOR );
END AFTER STATEMENT;


END;
