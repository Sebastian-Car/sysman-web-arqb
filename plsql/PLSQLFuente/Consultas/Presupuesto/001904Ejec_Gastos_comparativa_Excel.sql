SELECT 
	 COMPANIA "Compañía"
	,ANO "Año"
	,NOMBRE "Nombre"
	,CODIGO "Código"
	,APROPIADO "Apropiado"
	,TOTALAPROPIADO "Total Apropiado"
	,ADICION "Adición"
	,REDUCCION "Reducción"
	,TRASLADOS "Traslado"
	,APLAZAMIENTOS "Aplazamiento"
	,DISPONIBILIDADANTERIOR "DisponibilidadA"
	,DISPONIBILIDADMES "DisponibilidadP"
	,DISPONIBILIDADF "DisponibilidadF"
	,REGISTROSMESANT "RegistrosA"
	,REGISTROSMES "RegistrosP" 
	,REGISTROSF "RegistrosF"
	,NATURALEZA "Naturaleza"
	,SALDOPORCOMPROMETER "SaldoxComprometer"
	,VARMENSUALDIS "VarDisMes"
	,VARRESMES "VarResMes"
	,SALDODISPONIBLE "SaldoDisponible" 
	,PORCEJECUTADO "% Ejecutado"
	,POREJECUTAR "% Por Ejecutar"
FROM 
(
 s$consultaBase$s	
)
ORDER BY CODIGO
