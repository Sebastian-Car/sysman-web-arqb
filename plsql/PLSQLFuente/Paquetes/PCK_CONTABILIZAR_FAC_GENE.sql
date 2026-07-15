create or replace PACKAGE PCK_CONTABILIZAR_FAC_GENE AS 
 --En este se debe insertar todo lo referente a contabilizar de Fcturación general
FUNCTION FC_CONCEPTOSINCONFIGURACION
    (UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANIO                  IN PCK_SUBTIPOS.TI_ANIO
    )
    RETURN CLOB;

END PCK_CONTABILIZAR_FAC_GENE;