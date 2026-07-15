CREATE OR REPLACE TRIGGER "AU_SP_USUARIO" 
  /*
      NAME              : AU_SP_USUARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 05/10/2017
      TIME              : 2:15 PM
      SOURCE MODULE     :
      <MODIFIER         :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :

  */
FOR UPDATE ON SP_USUARIO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

    TYPE USUARIO IS RECORD
    (
       COMPANIA             PCK_SUBTIPOS.TI_COMPANIA
      ,CICLO                SP_USUARIO.CICLO%TYPE
      ,CODIGOINTERNO        SP_USUARIO.CODIGOINTERNO%TYPE
      ,CODIGORUTA           SP_USUARIO.CODIGORUTA%TYPE
      ,ANO                  SP_USUARIO.ANO%TYPE
      ,PERIODO              SP_USUARIO.PERIODO%TYPE
      ,CAMBIAMATRICULA      PCK_SUBTIPOS.TI_LOGICO := 0
      ,NUEMATRICULA         SP_USUARIO.MATRICULA%TYPE DEFAULT ''
      ,CAMBIACODEXTERNO     PCK_SUBTIPOS.TI_LOGICO := 0
      ,NUE_CODIGOEXTERNO    SP_USUARIO.CODIGO_EXTERNO%TYPE DEFAULT ''
      ,MODIFICADOPOR        SP_USUARIO.MODIFIED_BY%TYPE
    );
    TYPE TI_USUARIOS IS TABLE OF USUARIO INDEX BY BINARY_INTEGER ;

  MI_TUSAURIOS          TI_USUARIOS;
  MI_POS                NUMBER DEFAULT 0;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PERMITECAMBIO      BOOLEAN;
  MI_RTA                PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
  MI_TOTFACT            PCK_SUBTIPOS.TI_DOBLE;

BEFORE EACH ROW IS
BEGIN
     MI_PERMITECAMBIO := TRUE;
END BEFORE EACH ROW;

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN
    IF :NEW.CAMBIOCICLORUTA = 0 THEN
        IF :NEW.AUTORIZAMODIFICACION = 0 THEN  --No debe actualizar los campos basicos de usuario
            BEGIN
                IF :NEW.CODIGORUTA <> :OLD.CODIGORUTA THEN
                    MI_PERMITECAMBIO := FALSE;
                END IF;

                IF (:NEW.CODIGOINTERNO <> :OLD.CODIGOINTERNO) OR (:NEW.DIRTECNICA <> :OLD.DIRTECNICA) OR (:NEW.DIRCORRESPONDENCIA <> :OLD.DIRCORRESPONDENCIA) OR (:NEW.DIRGUIA <> :OLD.DIRGUIA) OR (:NEW.CODIGOCATASTRAL <> :OLD.CODIGOCATASTRAL) OR (:NEW.POSTAL <> :OLD.POSTAL) OR (:NEW.CODIGOMAPA <> :OLD.CODIGOMAPA) OR
                   (:NEW.MEDIDOR <> :OLD.MEDIDOR) OR (:NEW.FECHAINSTALACION <> :OLD.FECHAINSTALACION) OR (:NEW.ESTADO <> :OLD.ESTADO)  OR (:NEW.TIPOPREDIO <> :OLD.TIPOPREDIO) OR (:NEW.CIIU <> :OLD.CIIU) OR (:NEW.NOMBRES <> :OLD.NOMBRES) OR
                   (:NEW.PRIMERAPELLIDO <> :OLD.PRIMERAPELLIDO) OR (:NEW.SEGUNDOAPELLIDO <> :OLD.SEGUNDOAPELLIDO) OR (:NEW.TIPODOCUMENTO <> :OLD.TIPODOCUMENTO) OR (:NEW.NIT <> :OLD.NIT) OR (:NEW.TELEFONO <> :OLD.TELEFONO) OR (:NEW.EMAIL <> :OLD.EMAIL) OR (:NEW.FAX <> :OLD.FAX) OR (:NEW.GEOREFERENCIACION <> :OLD.GEOREFERENCIACION) OR
                   (:NEW.FECHAINSCRIPCION <> :OLD.FECHAINSCRIPCION) OR (:NEW.DIAMETROACOMETIDA <> :OLD.DIAMETROACOMETIDA) OR (:NEW.NUMEROACOMETIDA <> :OLD.NUMEROACOMETIDA) OR (:NEW.SECTOR <> :OLD.SECTOR) OR (:NEW.SECCION <> :OLD.SECCION) OR (:NEW.MANZANA <> :OLD.MANZANA) OR (:NEW.LADO <> :OLD.LADO) OR
                   (:NEW.SECTHIDRAULICO <> :OLD.SECTHIDRAULICO) OR (:NEW.SUBSECTOR <> :OLD.SUBSECTOR) OR (:NEW.REDHIDRAULICA <> :OLD.REDHIDRAULICA) OR (:NEW.BARRIO <> :OLD.BARRIO) OR (:NEW.CODIGO_EXTERNO <> :OLD.CODIGO_EXTERNO) OR (:NEW.NOTADEBITO <> :OLD.NOTADEBITO) OR (:NEW.PISO <> :OLD.PISO) OR (:NEW.CODIGODANE <> :OLD.CODIGODANE) OR
                   (:NEW.USO <> :OLD.USO) OR (:NEW.ESTRATO <> :OLD.ESTRATO) OR  (:NEW.ESTRATOASEO <> :OLD.ESTRATOASEO) OR (:NEW.PESOASEO <> :OLD.PESOASEO) OR (:NEW.FRECUENCIAASEOSEMANA <> :OLD.FRECUENCIAASEOSEMANA) OR (:NEW.ESTRATOALUMBRADO <> :OLD.ESTRATOALUMBRADO) OR
                   (:NEW.PERIODOSNOCOBROFIN <> :OLD.PERIODOSNOCOBROFIN) OR (:NEW.ANOENTRADANUEVOUSUARIO <> :OLD.ANOENTRADANUEVOUSUARIO) OR (:NEW.ACUEDUCTO <> :OLD.ACUEDUCTO) OR (:NEW.ALCANTARILLADO <> :OLD.ALCANTARILLADO) OR (:NEW.ALUMBRADO <> :OLD.ALUMBRADO) OR (:NEW.ASEO <> :OLD.ASEO) OR (:NEW.ASEOBARRIDO <> :OLD.ASEOBARRIDO) OR
                   (:NEW.INDDESHABITADO <> :OLD.INDDESHABITADO) OR (:NEW.INQUILINATOS <> :OLD.INQUILINATOS) OR (:NEW.TOTALIZADOR <> :OLD.TOTALIZADOR) OR (:NEW.CODTOTALIZADOR <> :OLD.CODTOTALIZADOR) OR (:NEW.PLANTA <> :OLD.PLANTA) OR (:NEW.COMENTARIOS <> :OLD.COMENTARIOS) OR (:NEW.FREC_RECO <> :OLD.FREC_RECO) OR
                   (:NEW.FREC_BARRI <> :OLD.FREC_BARRI) OR (:NEW.TIPODEAFORO <> :OLD.TIPODEAFORO) OR (:NEW.HOGAR_COMUNITARIO <> :OLD.HOGAR_COMUNITARIO) OR (:NEW.CARACTERIZACION_ALC <> :OLD.CARACTERIZACION_ALC)

                THEN
                    MI_PERMITECAMBIO := FALSE;
                END IF;

                IF NOT MI_PERMITECAMBIO THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                --No se encuentra autorizado para modificar este usuario.
                MI_MSGERROR(0).CLAVE := 'RUTA';
                MI_MSGERROR(0).VALOR := :NEW.CODIGORUTA;
                PCK_ERR_MSG.RAISE_WITH_MSG
                                    (UN_EXC_COD    => SQLCODE
                                    ,UN_TABLAERROR => 'SP_USUARIO'
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_USUARIONOMODIFICACIONES
                                    ,UN_REEMPLAZOS =>  MI_MSGERROR);
            END;
        END IF; --Fin autoriza Modificación


        IF NVL(:OLD.MATRICULA,' ') <> NVL(:NEW.MATRICULA,' ') OR NVL(:OLD.CODIGO_EXTERNO,' ') <> NVL(:NEW.CODIGO_EXTERNO,' ') THEN
            MI_POS := MI_POS + 1;
            MI_TUSAURIOS(MI_POS).COMPANIA := :OLD.COMPANIA;
            MI_TUSAURIOS(MI_POS).CODIGOINTERNO := :NEW.CODIGOINTERNO;
            MI_TUSAURIOS(MI_POS).CODIGORUTA := :OLD.CODIGORUTA;
            MI_TUSAURIOS(MI_POS).CICLO := :NEW.CICLO;
            MI_TUSAURIOS(MI_POS).ANO := :NEW.ANO;
            MI_TUSAURIOS(MI_POS).PERIODO := :NEW.PERIODO;
            MI_TUSAURIOS(MI_POS).MODIFICADOPOR := :NEW.MODIFIED_BY;

            IF NVL(:OLD.MATRICULA,' ') <> NVL(:NEW.MATRICULA,' ') THEN
                MI_TUSAURIOS(MI_POS).CAMBIAMATRICULA := -1;
                MI_TUSAURIOS(MI_POS).NUEMATRICULA := :NEW.MATRICULA;
            END IF;

            IF NVL(:OLD.CODIGO_EXTERNO,' ') <> NVL(:NEW.CODIGO_EXTERNO,' ') THEN
                MI_TUSAURIOS(MI_POS).CAMBIACODEXTERNO := -1;
                MI_TUSAURIOS(MI_POS).NUE_CODIGOEXTERNO := :NEW.CODIGO_EXTERNO;
            END IF;

        END IF;


        IF NVL(:OLD.ESTRATO,' ') <> NVL(:NEW.ESTRATO, ' ') THEN
            MI_RTA := PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI
                        ( UN_COMPANIA       => :OLD.COMPANIA
                         ,UN_CICLO          => :OLD.CICLO
                         ,UN_CODIGO         => :OLD.CODIGORUTA
                         ,UN_VALFINAL       => :NEW.ESTRATO
                         ,UN_VALINICIAL     => :OLD.ESTRATO
                         ,UN_CAMPOACTUAL    => 'ESTRATO'
                         ,UN_CAMPOANTERIOR  => 'ESTRATO_ANT'
                         ,UN_PERIODO        => :OLD.PERIODO
                         ,UN_USUARIO        => :NEW.MODIFIED_BY );
        END IF;

        IF NVL(:OLD.USO,' ') <> NVL(:NEW.USO,' ') THEN
            MI_RTA := PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI
                        ( UN_COMPANIA       => :OLD.COMPANIA
                         ,UN_CICLO          => :OLD.CICLO
                         ,UN_CODIGO         => :OLD.CODIGORUTA
                         ,UN_VALFINAL       => :NEW.USO
                         ,UN_VALINICIAL     => :OLD.USO
                         ,UN_CAMPOACTUAL    => 'USO'
                         ,UN_CAMPOANTERIOR  => 'USO_ANT'
                         ,UN_PERIODO        => :OLD.PERIODO
                         ,UN_USUARIO        => :NEW.MODIFIED_BY );
        END IF;

        MI_RTA := PCK_SERVICIOS_PUBLICOS_COM7.FC_SUSCRIPTORTGR
                    ( UN_COMPANIA                => :OLD.COMPANIA
                     ,UN_CICLO                   => :OLD.CICLO
                     ,UN_CODIGORUTA              => :OLD.CODIGORUTA
                     ,UN_CODIGOINTERNO           => :NEW.CODIGOINTERNO
                     ,UN_ANO                     => :OLD.ANO
                     ,UN_PERIODO                 => :OLD.PERIODO
                     ,UN_ANTESTADO               => :OLD.ESTADO
                     ,UN_NUEESTADO               => :NEW.ESTADO
                     ,UN_VALIDACAMBIOMATRICULA   => 0
                     ,UN_TOTALDEUDA              => :NEW.TOTALDEUDA
                     ,UN_BANCOPERPROCESO         => :NEW.BANCOPERPROCESO
                     ,UN_ANTHOGARCOMUNI          => :OLD.HOGAR_COMUNITARIO
                     ,UN_NUEHOGARCOMUNI          => :NEW.HOGAR_COMUNITARIO
                     ,UN_TOTFACTURA              => :NEW.TOTFACTURAPERACTUAL
                     ,UN_USO                     => :NEW.USO
                     ,UN_ESTRATO                 => :NEW.ESTRATO
                     ,UN_ANTMATRICULA            => :OLD.MATRICULA
                     ,UN_NUEMATRICULA            => :NEW.MATRICULA
                     ,UN_CAMBIOINDASEO           => CASE WHEN :NEW.ASEO <> :OLD.ASEO THEN -1 ELSE 0 END
                     ,UN_CAMBIOASEOBARRIDO       => CASE WHEN :NEW.ASEOBARRIDO <> :OLD.ASEOBARRIDO THEN -1 ELSE 0 END
                     ,UN_EMPRESAASEOEXT          => :NEW.EMPRESAASEOEXT
                     ,UN_PINTADEUDATERCE         => :NEW.PINTADEUDATERCE
                     ,UN_DBLTOTAL                => MI_TOTFACT
                     ,UN_ACCION                  => 'ACTUALIZAR'
                     ,UN_USUARIO                  => :NEW.MODIFIED_BY );
    END IF; --Fin cambio ruta o ciclo
END AFTER EACH ROW;


AFTER STATEMENT IS
BEGIN
    IF MI_RTA <> 0 THEN
        FOR i IN 1..MI_POS
        LOOP
            MI_RTA := PCK_SERVICIOS_PUBLICOS_COM7.FC_SUSCRIPTORTGR
                        (   UN_COMPANIA                => MI_TUSAURIOS(i).COMPANIA
                           ,UN_VALIDACAMBIOMATRICULA   => MI_TUSAURIOS(i).CAMBIAMATRICULA
                           ,UN_VALIDACAMBIOCODEXTERNO  => MI_TUSAURIOS(i).CAMBIACODEXTERNO
                           ,UN_CICLO                   => MI_TUSAURIOS(i).CICLO
                           ,UN_CODIGORUTA              => MI_TUSAURIOS(i).CODIGORUTA
                           ,UN_ANO                     => MI_TUSAURIOS(i).ANO
                           ,UN_PERIODO                 => MI_TUSAURIOS(i).PERIODO
                           ,UN_CODIGOINTERNO           => MI_TUSAURIOS(i).CODIGOINTERNO
                           ,UN_NUEMATRICULA            => MI_TUSAURIOS(i).NUEMATRICULA
                           ,UN_NUECODIGO_EXTERNO       => MI_TUSAURIOS(i).NUE_CODIGOEXTERNO
                           ,UN_DBLTOTAL                => MI_TOTFACT
                           ,UN_ACCION                  => 'ACTUALIZAR'
                           ,UN_USUARIO                  => MI_TUSAURIOS(i).MODIFICADOPOR
                        );

        END LOOP;

    END IF;

END AFTER STATEMENT;

END;
