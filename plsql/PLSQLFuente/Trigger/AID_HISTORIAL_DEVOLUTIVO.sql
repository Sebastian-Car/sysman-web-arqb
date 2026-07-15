CREATE OR REPLACE TRIGGER AID_HISTORIAL_DEVOLUTIVO
FOR INSERT OR DELETE
ON HISTORIAL_UBICACION
COMPOUND TRIGGER

    TYPE T_CLAVE IS RECORD (
        COMPANIA   HISTORIAL_UBICACION.COMPANIA%TYPE,
        ELEMENTO   HISTORIAL_UBICACION.ELEMENTO%TYPE,
        SERIE      HISTORIAL_UBICACION.SERIE%TYPE
    );

    TYPE T_CLAVES IS TABLE OF T_CLAVE INDEX BY PLS_INTEGER;

    MI_CLAVES      T_CLAVES;
    MI_IDX         PLS_INTEGER := 0;

    AFTER EACH ROW IS
    BEGIN
        MI_IDX := MI_IDX + 1;

        IF INSERTING THEN

            MI_CLAVES(MI_IDX).COMPANIA := :NEW.COMPANIA;
            MI_CLAVES(MI_IDX).ELEMENTO := :NEW.ELEMENTO;
            MI_CLAVES(MI_IDX).SERIE    := :NEW.SERIE;

        ELSE

            MI_CLAVES(MI_IDX).COMPANIA := :OLD.COMPANIA;
            MI_CLAVES(MI_IDX).ELEMENTO := :OLD.ELEMENTO;
            MI_CLAVES(MI_IDX).SERIE    := :OLD.SERIE;

        END IF;

    END AFTER EACH ROW;

    AFTER STATEMENT IS

        MI_UBICACION HISTORIAL_UBICACION.UBICACION%TYPE;

    BEGIN
        FOR I IN 1 .. MI_IDX LOOP

            BEGIN
                SELECT      UBICACION
                  INTO      MI_UBICACION
                  FROM (
                        SELECT      UBICACION
                          FROM      HISTORIAL_UBICACION
                         WHERE      COMPANIA = MI_CLAVES(I).COMPANIA
                           AND      ELEMENTO = MI_CLAVES(I).ELEMENTO
                           AND      SERIE    = MI_CLAVES(I).SERIE
                      ORDER BY      FECHA DESC ,ID_HISTORIAL DESC
                       )
                 WHERE      ROWNUM = 1;

                UPDATE      DEVOLUTIVO
                   SET      UBICACION = MI_UBICACION
                 WHERE      COMPANIA = MI_CLAVES(I).COMPANIA
                   AND      ELEMENTO = MI_CLAVES(I).ELEMENTO
                   AND      SERIE    = MI_CLAVES(I).SERIE;

            EXCEPTION
                WHEN NO_DATA_FOUND THEN

                    UPDATE      DEVOLUTIVO
                       SET      UBICACION = NULL
                     WHERE      COMPANIA = MI_CLAVES(I).COMPANIA
                       AND      ELEMENTO = MI_CLAVES(I).ELEMENTO
                       AND      SERIE    = MI_CLAVES(I).SERIE;

            END;
        END LOOP;
    END AFTER STATEMENT;
END AID_HISTORIAL_DEVOLUTIVO;