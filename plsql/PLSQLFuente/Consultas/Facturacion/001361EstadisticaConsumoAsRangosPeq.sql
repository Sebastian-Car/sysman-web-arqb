SELECT Qry_PesoAseoEstTot.COMPANIA,
  Qry_PesoAseoEstTot.NumUsuarios,
  Qry_PesoAseoEstTot.PeriodoUnico,
  Qry_PesoAseoEstTot.TipoProductor,
  CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR('001','PESO ASEO EN TONELADAS','74',SYSDATE),'NO') = 'NO' THEN SUMADEPESOASEO ELSE SUMADEPESOASEO*0.25 END  PESOASEO,
  Qry_PesoAseoEstTot.ANO,
  Qry_PesoAseoEstTot.PERIODO,
  Qry_PesoAseoEstTot.USO,
  Qry_PesoAseoEstTot.ESTRATO,
  Qry_PesoAseoEstTot.SumaDeConcepto21,
  Qry_PesoAseoEstTot.SumaDeConcepto20,
  Qry_PesoAseoEstTot.SumaDeConcepto22,
  Qry_PesoAseoEstTot.SumaDeConcepto3,
  Qry_PesoAseoEstTot.SumaDeConcepto48,
  Qry_PesoAseoEstTot.SumaDeConcepto46,
  Qry_PesoAseoEstTot.SumaDeSumaDeTOTFACTURAASEO,
  Qry_PesoAseoEstTot.USO|| '- '|| SP_USOS.NOMBRE  NOMBREUSO,
  Qry_PesoAseoEstTot.SumaDeConcepto21+Qry_PesoAseoEstTot.SumaDeConcepto20+Qry_PesoAseoEstTot.SumaDeConcepto22+Qry_PesoAseoEstTot.SumaDeConcepto3+Qry_PesoAseoEstTot.SumaDeConcepto48 AS totaseo,
  Qry_PesoAseoEstTot.ciclo
FROM sp_Qry_PesoAseoEstTot Qry_PesoAseoEstTot
LEFT JOIN  SP_USOS
ON SP_USOS.COMPANIA=Qry_PesoAseoEstTot.COMPANIA
AND SP_USOS.CODIGO=Qry_PesoAseoEstTot.USO

WHERE Qry_PesoAseoEstTot.compania=s$compania$s
AND Qry_PesoAseoEstTot.TIPOPRODUCTOR =s$tipoProductor1$s
AND Qry_PesoAseoEstTot.ANO BETWEEN s$anoInicial$s AND s$anoFinal$s
AND Qry_PesoAseoEstTot.PERIODO  BETWEEN s$periodoInicial$s AND s$periodoFinal$s

s$cicloCondicion$s
