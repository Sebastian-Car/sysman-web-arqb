SELECT PCK_SYSMAN_UTL.FC_PAR(     UN_COMPANIA  => s$compania$s
                                 ,UN_NOMBRE    => 'TIPO DE ACTO ADMINISTRATIVO VAL PARA LAS CUENTAS POR PAGAR' 
                                 ,UN_MODULO    => '99'
                                 ,UN_FECHA_PAR => SYSDATE)
       ,'s$numeroActo$s' NUMERO_ACTO_ADM
       ,TO_CHAR(TO_DATE('s$fechaActo$s','DD/MM/YYYY'),'DD-MM-YYYY') FECHA_ACTO_ADM                  
       , PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s
                              ,UN_NOMBRE    => 'FUENTE FUT PARA CIFRA DE CONTROL VAL' 
                              ,UN_MODULO    => '99'
                              ,UN_FECHA_PAR => SYSDATE)                         
       ,SUM(CXP_CONSTITUIDAS) 
       ,SUM(PAGOS)
FROM (s$consultabase$s)
