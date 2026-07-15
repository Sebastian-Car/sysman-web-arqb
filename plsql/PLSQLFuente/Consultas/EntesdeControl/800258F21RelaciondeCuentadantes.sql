SELECT PERSONAL_HISTORICO.NOMBRES||' '||PERSONAL_HISTORICO.APELLIDO1||' '||PERSONAL_HISTORICO.APELLIDO   "(C) Nombre",
       PERSONAL_HISTORICO.NUMERO_DCTO                                                                    "(N) Cedula",
       PERSONAL_HISTORICO.NOMBRE_EXPEDIDA_CED                                                            "(N) Lugar De Expedicion",
       PERSONAL_HISTORICO.NOMBRE_DE_CARGO                                                                "(C) Cargo",
       PERSONAL_HISTORICO.DIRECCION                                                                      "(C) Direccion",
       PERSONAL_HISTORICO.TELEFONOS                                                                      "(N) Telefono",
       PERSONAL_HISTORICO.ASEGURADORA                                                                    "(C) Compañia De Seguros",
       PERSONAL_HISTORICO.N_POLIZA                                                                       "(C) Numero De Poliza",
       PERSONAL.HISTORICO.FECHA_INIC                                                                     "(F) Vigencia Inicial",
       PERSONAL.HISTORICO.FECHA_VENCE                                                                    "(F) Vigencia Final",
       PERSONAL.HISTORICO.VLR_ASEG                                                                       "(D) Valor Asegurado"
FROM PERSONAL_HISTORICO
WHERE PERSONAL_HISTORICO.COMPANIA =s$compania$s
   AND PERSONAL_HISTORICO.ESTADO_ACTUAL =1
   AND PERSONAL_HISTORICO.MANEJA_POLIZA=-1
   AND PERSONAL_HISTORICO.FEHCA_VENCE BETWEEN TO_NUMBER(TO_CHAR(s$mesInicial$s,'MM')) AND   TO_NUMBER(TO_CHAR(s$mesFinal$s,'MM')
