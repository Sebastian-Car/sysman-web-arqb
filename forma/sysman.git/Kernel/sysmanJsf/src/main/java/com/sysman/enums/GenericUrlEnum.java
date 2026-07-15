/*-
 * GenericUrlEnum.java
 *
 * 1.0
 *
 * 15/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/03/2017
 * @author cmanrique
 *
 * @version 2.0, 03/04/2017 sdaza - Adicionar URL
 *
 * @author ybecerra
 * @version 3, 09/10/2017, Adicionar URL
 *
 *
 */
public enum GenericUrlEnum {

    ZONAS("ZonasControlador", "ZONA", "300C", null, "300U", "300D", "300G"),

    PARAMETROS("ParametrosControlador", "PARAMETRO", "6700C", null, "6700U",
                    null, "6700G"),

    CENTRO_COSTO("CentroscostosControlador", "CENTRO_COSTO", "2000C", "2000R",
                    "2000U", "2000D", "2000G"),

    CENTRO_COSTOESP("CentroscostosControladorESP", "CENTRO_COSTOESP", "2800C",
                    null, "2800U", "2800D", "2800G"),

    DISTRIBUCION_CENTROCOSTO("CentroscostosControladorDistr",
                    "DISTRIBUCION_CENTROCOSTO", "2400C", null, "2400U",
                    "2400D", "24001"),

    AUXILIAR("AuxiliarControlador", "AUXILIAR", "2300C", "2300R", "2300U",
                    "2300D", "2300G"),

    DEPARTAMENTO("DepartamentosControlador", "DEPARTAMENTO", "200C", "200R",
                    "200U", "200D", "200G"),

    CIUDAD("DepartamentosControlador", "CIUDAD", "500C", null, "500U", "500D",
                    "500G"),

    DESTINORECURSOS("DestinorecursosControlador", "DESTINORECURSOS", "10100C",
                    null, "10100U", "10100D", "10100G"),

    USUARIO("CuentasControlador", "USUARIO", "4700C", "4700R", "4700U", "4700D",
                    "4700G"),

    DECLARACIONESESTRATEGICAS("DeclaracionestrategicasControlador",
                    "DECLARACIONESTRATEGICA", "10300C", null, "10300U",
                    null, "10300G"),

    APLICACIONES("AplicacionesControlador", "APLICACIONES", null, null, "5800U",
                    null, "5800G"),

    ANO("AnosperiodosControlador", "ANO", "400C", null, "400U", null, "400G"),

    COMPANIA("CompaniasControlador", "COMPANIA", "5900C", "5900R", "5900U",
                    "5900D", "5900G"),

    CAMBIOSDENIT("CambiosdenitsControlador", "CAMBIOSDENIT", "7600C", "7600R",
                    "7600U", "7600D", "7600G"),

    BANCO("BancosControladorT", "BANCO", "3600C", null, "3600U", "3600D",
                    "3600G"),

    REGIONES("FrmregionesControlador", "REGIONES", "900C", null, "900U", "900D",
                    "900G"),

    ORDENADOR("OrdenadorsControlador", "ORDENADOR", "8400C", null, "8400U",
                    "8400D", "8400G"),

    DEPENDENCIA("DependenciasControlador", "DEPENDENCIA", "6200C", "6200R",
                    "6200U", "6200D", "6200G"),

    FESTIVOS("FestivosControlador", "FESTIVOS", "9600C", null, "9600U", "9600D",
                    "9600G"),

    RETEFUENTE("RetencionsControlador", "RETEFUENTEUVT", "1100C", null, "1100U",
                    "1100D", "1100G"),

    ASEGURADORA("AseguradorasControlador", "ASEGURADORA", "9500C", null,
                    "9500U", "9500D", "9500G"),

    TIPOSDIVISION("FrmtiposdivisionsControlador", "TIPOSDIVISION", "9700C",
                    null, "9700U", "9700D", "9700G"),

    DI_CONTRATO("DigitalizacionContratosControlador", "DI_CONTRATO", "18500C",
                    null, null, "18500D", "18500G"),

    JUZGADOS("JuzgadosControlador", "JUZGADOS", "8900C", null, "8900U", "8900D",
                    "8900G"),

    PAGOPROGRAMADO("FormadepagosControlador", "PAGOPROGRAMADO", "18600C", null,
                    "18600U", null, "18600G"),

    ORDENDESUMINISTRO_REQUISICION("AuxordendesuministrosControlador",
                    "ORDENDESUMINISTRO", null, null, "109001", null,
                    "109002"),

    ASOCIACIONESMUNICIPALES("FrmasociacionesmunicipalesControlador",
                    "ASOCIACIONESMUNICIPALES", "9900C", null, "9900U",
                    "9900D", "9900G"),

    PAISES("PaisesControlador", "PAISES", "100C", null, "100U", "100D", "100G"),

    CONSOLIDADA("CompaniasControlador", "CONSOLIDADA", "6400C", null, "6400U",
                    "6400D", "6400G"),

    BARRIOS("FrmbarriosControlador", "BARRIOS", "10700C", null, "10700U",
                    "10700D", "10700G"),

    D_CAMBIOSDENIT("CambiosdenitsControlador", "D_CAMBIOSDENIT", "9000C", null,
                    "9000U", "9000D", "9000G"),

    USUARIO_D("CuentasControlador", "USUARIO_D", "5100C", null, "5100U",
                    "5100D", "5100G"),

    USUARIO_DEPENDENCIA("CuentasControlador", "USUARIO_DEPENDENCIA", "5200C",
                    null, "5200U", "5200D", "5200G"),

    DEPENDENCIA_RESPONSABLE("DependenciasControlador",
                    "DEPENDENCIA_RESPONSABLE", "7100C", null, "7100U", "7100D",
                    "7100G"),

    GRUPO_COMPANIA("CuentasControlador", "GRUPO_COMPANIA", "5700C", null, null,
                    "5700D", "5700G"),

    MODELO_PLANTILLA("PlantillaswordsControlador", "MODELO_PLANTILLA", "10400C",
                    "10400R", "10400U", "10400D",
                    "10400G"),

    TIPO_ADJUDICACIONES("TipoadjudicacionsControlador", "TIPOADJUDICACION",
                    "10800C", null, "10800U", "10800D",
                    "10800G"),

    GRUPO_MENU("CuentasControlador", "GRUPO_MENU", "5400C", null, "5400U",
                    "5400D", "5400G"),

    DOCUMENTOS("DocumentosPContrato", "DOCUMENTOS", "19100C", null, "19100U",
                    null, "19100G"),

    ORDENDECOMPRA("PcontratosControlador", "ORDENDECOMPRA", "8200C", "8200R",
                    "8200U", "8200D", "8200G"),

    COMPROBANTE_PPTAL("ComprobantepptalsControlador", "COMPROBANTE_PPTAL",
                    "7500C", "7500R", "7500U", "7500D", "7500G"),

    MULTASSANCIONES("MultassancionesControlador", "MULTAS_SANCIONES", "19400C",
                    null, "19400U", "19400D", "19400G"),

    GRUPO_FORMULARIO("CuentasControlador", "GRUPO_FORMULARIO", "5600C", null,
                    "5600U", "5600D", "5600G"),

    NOVEDADCONTRATO("SubnovedadpcontratoControlador", "NOVEDADCONTRATO", null,
                    null, null, null, "12700G"),

    RESPONSABLEDEP("ResponsableControlador", "DEPENDENCIA_RESPONSABLE", "7100C",
                    null, "7100U", "7100D", "7100G"),

    POLIZAS("SubpolizasControlador", "POLIZAS", "19500C", null, "19500U",
                    "19500D", "19500G"),

    RESPONSABLE("ResponsablesControlador", "RESPONSABLE", "6100C", null,
                    "6100U", "6100D", "6100G"),

    FUENTE_RECURSOS("FuenterecursosControlador", "FUENTE_RECURSOS", "3400C",
                    null, "3400U", "3400D", "3400G"),

    RETENCIONES_MINIMAS("RetencionMinimaControlador", "RETENCIONES_MINIMAS",
                    "1000C", null, "1000U", "1000D", "1000G"),

    MES("PeriodosypaagsControlador", "MES", null, null, "700U", null, "700G"),

    TIPOORDENDECOMPRA("TipoordendecomprasControlador", "TIPOORDENDECOMPRA",
                    "7300C", "7300R", "7300U", "7300D",
                    "7300G"),

    MODELO_VARIABLES("PlantillaswordsControlador", "MODELO_VARIABLES", "10000C",
                    null, "10000U", "10000D", "10000G"),

    ANO_ESTADO("PeriodosypaagsControlador", "ANO", null, null, "4010", null,
                    null),

    ORDENDECOMPRAPPTO("OrdendecomprapptosControlador", "ORDENDECOMPRAPPTO",
                    null, null, null, null, "20000G"),

    ORDENDESUMINISTRO("CuentasControlador", "ORDENDESUMINISTRO", "10900C",
                    "10900R", "10900U", "10900D", "10900G"),

    CLASIFICACION_PROPONENTES("PcontratosControlador",
                    "CLASIFICACION_PROPONENTES", "19700C", null, "19700U",
                    "19700D",
                    "19700G"),

    SUPERVISORES("DsupervisoresControlador", "SUPERVISORES", "19600C", null,
                    "19600U", "19600D", "19600G"),

    ES_SUPERVISORES("DsupervisoresControlador", "ES_SUPERVISORES", "20100C",
                    null, "20100U", "20100D", "20100G"),

    PAGO_ESTAMPILLAS("EstampillasControlador", "PAGO_ESTAMPILLAS", "19900C",
                    "19900R", "19900U", null, null),

    TERCEROS_APORTANTES("TercerosAportantesControlador", "TERCEROS_APORTANTES",
                    "20200C", null, "20200U", "20200D",
                    "20200G"),

    DORDENDECOMPRA("SubpcontratosControlador", "D_ORDENDECOMPRA", "11300C",
                    "11300R", "11300U", "11300D", "11300G"),

    MODELO_TABLA("PlantillaswordsControlador", "MODELO_TABLA", "10600C", null,
                    "10600U", "10600D", "10600G"),

    D_ORDENDESUMINISTRO("POrdenDeSuministroControlador", "D_ORDENDESUMINISTRO",
                    "11000C", null, "11000U", "11000D",
                    "11000G"),

    CONSORCIADOS("TercerosControlador", "CONSORCIADOS", "3300C", null, "3300U",
                    "3300D", "3300G"),

    TERCERO("TercerosControlador", "TERCERO", "1400C", "1400R", "1400U",
                    "1400D", "1400G"),

    CHEQUERA("ChequerasControlador", "CHEQUERA", "5300C", null, "5300U",
                    "5300D", "5300G"),

    CHEQUES_ANULADOS("ChequesAnuladosControlador", "CHEQUES_ANULADOS", "6000C",
                    null, null, null, "6000G"),

    CONCEPTOS_SF("ConceptossfsControlador", "CONCEPTOS_SF", "9100C", "9100R",
                    "9100U", "9100D", "9100G"),

    EMBARGOSCT("EmbargosctsControlador", "EMBARGOS_CT", "4000C", null, "4000U",
                    "4000D", "4000G"),

    TERCEROPAGOS("TercerosControlador", "TERCEROPAGOS", "3500C", null, "3500U",
                    "3500D", "3500G"),
    TIPOCLASIFICADORESHIJO("tipoclasificadoreshijocontrolador", "TIPOCLASIFICADORESHIJO", "188900C", "188900R", "188900U",
            "188900D", "1889010"),

    RETENCIONES_CONCEPTO("ConceptossfsControlador", "RETENCIONESCONCEPTO",
                    "9200C", null, "9200U", "9200D", "9200G"),

    TIPORETENCION("TiporetencionsControlador", "TIPORETENCION", "800C", null,
                    "800U", "800D", "800G"),

    INVERSIONRF("InversionrfsControlador", "INVERSIONRF", "7400C", "7400R",
                    "7400U", "7400D", "7400G"),

    HEADERCIERRE("ScierredemesControlador", "HEADERCIERRE", "11600C", null,
                    "11600U", "11600D", "11600G"),

    TIPO_DOCUMENTO("tipoDocuemntosControladorControlador", "TIPO_DOCUMENTO",
                    "600C", null, "600U", "600D", "600G"),

    RECLASIFICAR_NIIF("CambioscodigosControlador", "RECLASIFICAR_NIIF",
                    "21100C", "21100R", "21100U", "21100D",
                    "21100G"),

    COMPROBANTE_CNT("ComprobantecntsControlador", "COMPROBANTE_CNT", "7200C",
                    "7200R", "7200U", "7200D", "7200G"),

    TIPO_COMPROBANTE("TipocomprobantecsControlador", "TIPO_COMPROBANTE",
                    "1500C", "1500R", "1500U", "1500D", "1500G"),
    
    RESOLUCIONDIAN_CT("subresoluciondiansControlador", "RESOLUCIONDIAN_CT",
            "189600C", "189600R", "189600U", "189600D", "189600G"),

    D_RECLASIFICAR_NIIF("CambioscodigosControlador", "D_RECLASIFICAR_NIIF",
                    "21200C", null, "21200U", "21200D",
                    "21200G"),

    RETENCIONES("RetencionesControlador", "RETENCIONES", "1200C", "1200R",
                    "1200U", "1200D", "1200G"),

    PACTESORERIA("PactesoreriacntsControlador", "PACTESORERIA", "9300C", null,
                    "9300U", "9300D", "9300G"),

    DISTRIBUCIONCT_AUXILIARES("DistribucionctauxiliaresControlador",
                    "DISTRIBUCIONCT_AUXILIARES", "4300C", null,
                    "4300U", "4300D", "4300G"),

    SALDOSINICIALES("SaldoinicialsControlador", "SALDOSINICIALES", "4200C",
                    null, "4200U", "4200D", "4200G"),

    DETALLECOMPROBANTECNT("SubdetallecomprobantecntsControlador",
                    "DETALLE_COMPROBANTE_CNT", "3900C", null, "3900U",
                    "3900D", "3900G"),

    REFERENCIAS("ReferenciaControlador", "REFERENCIA", "1300C", "1300R",
                    "1300U", "1300D", "1300G"),

    INVERSIONESRV("InversionrvsControlador", "INVERSIONRV", "8500C", "8500R",
                    "8500U", "8500D", "8500G"),

    PARTIDAS_CONCILIATORIAS("PartidasConciliatoriasControlador",
                    "PARTIDAS_CONCILIATORIAS", "7000C", null, "7000U",
                    "7000D", "7000G"),

    CONSECUTIVOTC("TipocomprobantecsControlador", "CONSECUTIVOTC", "11700C",
                    null, "11700U", "11700D", "11700G"),

    TASAS_INTERES("TasasinteresControlador", "TASAS_INTERES", "8800C", null,
                    "8800U", "8800D", "8800G"),

    CUENTABANCOS("CuentabancosControlador", "CUENTABANCOS", "5500C", "5500R",
                    "5500U", "5500D", "5500G"),

    TIPOPAGO("TipopagosControlador", "TIPOPAGO", "7800C", null, "7800U",
                    "7800D", "7800G"),

    PLANCONTABLE("PlanContableControlador", "PLAN_CONTABLE", "1600C", "1600R",
                    "1600U", "1600D", "1600G"),

    ASIGNACION_RECURSOS("AsignacionrecursosControlador", "ASIGNACION_RECURSOS",
                    "12300C", null, "12300U", "12300D",
                    "12300G"),

    PLAN_PRESUPUESTAL("PlanpresupuestalptosControlador", "PLAN_PRESUPUESTAL",
                    "4500C", "4500R", "4500U", "4500D",
                    "4500G"),

    PLAN_PRESUPUESTALCLASI_ANO("ConfigurarplanpresupuestalControlador", "ANO",
            null, "400R", null, null,
            "4077"),
    
    PLAN_PRESUPUESTALCLASIREGA("ConfigurarplanpresupuestalControlador", "PLAN_PRESUPUESTAL",
            null, "45091", "45090", null,
            "45088"),
    PLAN_PRESUPUESTALCLASI("ConfigurarplanpresupuestalControlador", "PLAN_PRESUPUESTAL",
            null, "45092", "45090", null,
            "45100"),
    
    PLAN_PRESUPUESTALARBOLREGA("ConfigurarplanpresupuestalControlador", "PLAN_PRESUPUESTAL",
            null, "45094", "45095", null,
            "45102"),
    PLAN_PRESUPUESTALARBOL("ConfigurarplanpresupuestalControlador", "PLAN_PRESUPUESTAL",
            null, "45093", "45095", null,
            "45104"),
    
    PLAN_PTIPOVIGENCIA("ConfigurarplanpresupuestalControlador", "TIPOVIGENCIA",
            null, "176600R", "176600U", null,
            "176600G"),
    
    FUENTE_RECURSOSCUIPO("ConfigurarplanpresupuestalControlador", "FUENTE_RECURSOS",
            null, "34069", "34068", null,
            "34066"),
    
    
    PLANPPTALCUENTACNT("PlanContableControlador", "PLAN_PPTAL_CUENTACNT",
                    "5000C", null, "5000U", "5000D", "50003"),

    COMPROBANTE_CNTRETENCION("ComprobantecntretencionsControlador",
                    "COMPROBANTE_CNTRETENCION", "6900C", null, "6900U",
                    "6900D", "6900G"),

    FUENTE_FINANCIACION("FuenteFinanciacionsControlador", "FUENTE_FINANCIACION",
                    "12200C", null, "12200U", "12200D",
                    "12200G"),

    PROYECTOS_PPTAL("ProyectospptalsControlador", "PROYECTOS_PPTAL", "8000C",
                    null, "8000U", "8000D", "8000G"),

    PACPROGRAMADO("SubpacprogcompejecsControlador", "PACPROGRAMADO", "13400C",
                    null, "13400U", "13400D", "13400G"),

    TIPO_COMPROBPP("TipocomprobppsControlador", "TIPO_COMPROBPP", "2500C",
                    "2500R", "2500U", "2500D", "2500G"),

    PACCOMPROMETIDO("SubpacprogcompejecsControlador", "PACCOMPROMETIDO",
                    "13200C", null, "13200U", "13200D", "13200G"),

    PACEJECUTADO("SubpacprogcompejecsControlador", "PACEJECUTADO", "13300C",
                    null, "13300U", "13300D", "13300G"),

    LUGAR("UbicacionsControlador", "LUGAR", "12100C", null, "12100U", "12100D",
                    "12100G"),

    CONSECUTIVOTCP("TipocomprobppsControlador", "CONSECUTIVOTCP", "12500C",
                    null, "12500U", "12500D", "12500G"),

    ACTDEPRECIABLE("ActdepreciablesControlador", "ACTDEPRECIABLE", "15800C",
                    null, "15800U", "15800D", "15800G"),

    BODEGA("BodegasControlador", "BODEGA", "13500C", null, "13500U", "13500D",
                    "13500G"),

    ALMACEN("AlmacenesControlador", "ALMACEN", "14300C", null, "14300U",
                    "14300D", "14300G"),

    CAMBIOCODIGOALMACEN("CambiocodigoalmacenesControlador",
                    "CAMBIOCODIGOALMACEN", "13800C", null, "13800U", "13800D",
                    "13800G"),

    DETALLE_COMPROBANTE_PPTAL("SubdetallecomprobantepptalsControlador",
                    "DETALLE_COMPROBANTE_PPTAL", "3800C", null,
                    "3800U", "3800D", "3800G"),

    VALORIZACIONDEVOLUTIVO("DetalleavaluovalorizacionsControlador",
                    "VALORIZACIONDEVOLUTIVO", "14900C", null, "14900U",
                    "14900D", "14900G"),

    SALDO_PLAN_PPTAL("PacsaldopptalsControlador", "SALDO_PLAN_PPTAL", null,
                    null, null, null, null),

    ESTADO_DEVOLUTIVO("EstadodevolutivosControlador", "ESTADODEVOLUTIVO",
                    "14500C", null, "14500U", "14500D", "14500G"),

    CAMBIOS_TIPOACTIVO("EntdevolutivoactivosControlador", "CAMBIOS_TIPOACTIVO",
                    "15600C", "15600R", "15600U", "15600D",
                    "15600G"),

    DEVOLUTIVO("SubdevolutivosControlador", "DEVOLUTIVO", null, null, "14100U",
                    null, "14100G"),

    MODALIDAD("FrmarchivosvariosControlador", "MODALIDAD", "16400C", null,
                    "16400U", "16400D", "16400G"),

    NOTARIAESCRITURA("FrmarchivosvariosControlador", "NOTARIA_ESCRITURA",
                    "17000C", null, "17000U", "17000D", "17000G"),

    SECTORINMUEBLE("FrmarchivosvariosControlador", "SECTOR_INMUEBLE", "17200C",
                    null, "17200U", "17200D", "17200G"),

    SERVICIOSPUBLICOS("FrmarchivosvariosControlador", "SERVICIOS_PUBLICOS",
                    "17300C", null, "17300U", "17300D",
                    "17300G"),

    BIUBICACION("FrmarchivosvariosControlador", "BI_UBICACION", "17700C", null,
                    "17700U", "17700D", "17700G"),

    USOS("FrmarchivosvariosControlador", "USOS", "18100C", null, "18100U",
                    "18100D", "18100G"),

    TIPOVIA("FrmarchivosvariosControlador", "TIPO_VIA", "18000C", null,
                    "18000U", "18000D", "18000G"),

    ESTADOVIA("FrmarchivosvariosControlador", "ESTADO_VIA", "17800C", null,
                    "17800U", "17800D", "17800G"),

    ITEMS("FrmarchivosvariosControlador", "ITEMS", "17600C", null, "17600U",
                    "17600D", "17600G"),

    DOCASOCIADO("DocasociadosControlador", "DOCASOCIADO", "14000C", null,
                    "14000U", "14000D", "14000G"),

    REGLA_HORARIO("FrmreglahorariosControlador", "REGLAHORARIO", "14400C", null,
                    "14400U", "14400D", "14400G"),

    TIPO_EVENTO_DEVOLUTIVO("FrmtipoeventodevolutivosControlador",
                    "TIPO_EVENTO_DEVOLUTIVO", null, null, "14600U",
                    "14600D", "14600G"),

    MOVIMIENTO("MovimientosControlador", "MOVIMIENTO", "4100C", "4100R",
                    "4100U", "4100D", "4100G"),

    INVENTARIO("InventariosControlador", "INVENTARIO", "11200C", "11200R",
                    "11200U", "11200D", "11200G"),

    VIAS("FrmviasControlador", "VIAS", "13600C", "13600R", "13600U", "13600D",
                    "13600G"),

    COMPROBANTE_CNTBANCOS("ComprobanteCntBancosControlador",
                    "COMPROBANTE_CNTBANCOS", "8700C", null, "8700U", "8700D",
                    "8700G"),

    LOCALIZACIONES("LocalizacionsControlador", "LOCALIZACION", "15400C", null,
                    "15400U", "15400D", "15400G"),

    D_MOVIMIENTO("MovimientosControlador", "D_MOVIMIENTO", "11900C", null,
                    "11900U", "11900D", "11900G"),

    INVENTARIOCONTABILIDAD("SubinventcontabilidadsControlador",
                    "INVENTARIOCONTABILIDAD", "15900C", "15900R", "15900U",
                    "15900D", "15900G"),

    PREDIOS("FrmprediosControlador", "PREDIOS", "13700C", "13700R", "13700U",
                    "13700D", "13700G"),

    SERVICIOS_PREDIO("FrmprediosControlador", "SERVICIOS_PREDIO", "22100C",
                    null, "22100U", "22100D", "22100G"),

    USOS_PREDIO("FrmprediosControlador", "USOS_PREDIO", "22000C", null,
                    "22000U", "22000D", "22000G"),

    VIAS_ITEMS("FrmviasControlador", "VIAS_ITEMS", null, null, "17500U",
                    "17500D", "17500G"),

    HIST_AVALUO_VIA("FrmviasControlador", "HIST_AVALUO_VIA", "17400C", null,
                    "17400U", "17400D", "17400G"),

    ADICION_VIAS("FrmviasControlador", "ADICION_VIAS", null, null, null, null,
                    "18200G"),

    POLIZAS_ACTIVOS("PolizasControlador", "POLIZAS_ACTIVOS", "16700C", "16700R",
                    "16700U", "16700D", "16700G"),

    D_POLIZAS_ACTIVOS("PolizasControlador", "D_POLIZAS_ACTIVOS", "16800C", null,
                    null, "16800D", "16800G"),

    REQUISITOS_PRESTAMO("RequisitosPrestsControlador", "REQUISITOS_PRESTAMO",
                    "16600C", null, "16600U", "16600D",
                    "16600G"),

    D_CAMBIOS_TIPOACTIVO("SdentdevolutivoactivosControlador",
                    "D_CAMBIOS_TIPOACTIVO", "16100C", null, "16100U",
                    "16100D", "16100G"),

    V_BASE_CUENTAALMACEN("RevisarcuentaalmacensControlador",
                    "V_BASE_CUENTAALMACEN", null, null, null, null, "16900G"),

    DIA_BLOQUEO("DiabloqueosControlador", "DIA_BLOQUEO", null, null, "22400U",
                    null, "22400G"),

    ALMACEN_CONTABILIDAD("SubcontainvensControlador", "ALMACENCONTABILIDAD",
                    "18300C", null, "18300U", "18300D",
                    "18300G"),

    ADICIONES("SubpredioadicionsControlador", "ADICIONES", null, null, null,
                    null, null),

    HIST_AVALUOS("SubpredioavaluosControlador", "HIST_AVALUOS", "15200C",
                    "15200R", "15200U", null, null),

    REQUISITOS_TEVENTO("TipoEventoReqControlador", "REQUISITOS_TEVENTO",
                    "16500C", null, "16500U", "16500D", "16500G"),

    TIPO_EVENTO("TipoEventosControlador", "TIPO_EVENTO", "15000C", null,
                    "15000U", "15000D", "15000G"),

    TIPO_SOLICITANTE("TipoSolicitanteControlador", "TIPO_SOLICITANTE", "16000C",
                    null, "16000U", "16000D", "16000G"),

    TIPO_MOVIMIENTO("TipomovimientosControlador", "TIPOMOVIMIENTO", "13900C",
                    "13900R", "13900U", "13900D", "13900G"),

    UNIDAD("UnidadControlador", "UNIDAD", "15500C", null, "15500U", "15500D",
                    "15500G"),

    SP_AFORADORES("AforadoresControlador", "SP_AFORADORES", "36200C", null,
                    "36200U", "36200D", "36200G"),

    SP_DESVIACIONES("DesviacionesControlador", "SP_DESVIACIONES", null,
                    "33400R", "33400U", null, "33400G"),

    SP_HISTORIA_EXTERNA_DESACTIVA("AseoUrbanoControlador",
                    "SP_HISTORIA_EXTERNA_DESACTIVA", null, null, null, null,
                    "294001"),

    SP_HISTORIA_EXTERNA("AseoUrbanoControlador", "SP_HISTORIA_EXTERNA", null,
                    null, null, null, null),

    SP_BANCOS("BancosspsControlador", "SP_BANCOS", "34500C", null, "34500U",
                    "34500D", "34500G"),

    SP_ABONOS("AbonosfacturasControlador", "SP_ABONOS", "23300C", null,
                    "23300U", "23300D", "233003"),

    SP_CERTIFICADOSESTRATIFICACION("CertificadoestratificacionsControlador",
                    "SP_CERTIFICADOSESTRATIFICACION", "34200C",
                    "34200R", "34200U", "34200D", "34200G"),

    SP_CODIGOSCIIU("CiiusControlador", "SP_CODIGOSCIIU", "33800C", null,
                    "33800U", "33800D", "33800G"),

    SP_CICLO("CiclosControlador", "SP_CICLO", "21400C", "21400R", "21400U",
                    "21400D", "21400G"),

    SP_CLASEPROBLEMA("ClaseproblemasControlador", "SP_CLASEPROBLEMA", "34100C",
                    "34100R", "34100U", "34100D", "34100G"),

    SP_PROBLEMA("ClaseproblemasControlador", "SP_PROBLEMA", "23400C", null,
                    "23400U", "23400D", null),

    SP_CONCEPTOS("ConceptosSpControlador", "SP_CONCEPTOS", "21500C", "21500R",
                    "21500U", "21500D", "21500G"),

    SP_CODIGOS_MODIFICACION("CodigosmodificacionsControlador",
                    "SP_CODIGOS_MODIFICACION", "33700C", null, "33700U",
                    "33700D", "33700G"),

    SP_USUARIO("UsuariosControlador", "SP_USUARIO", null, "21300R", "21300U",
                    null, "21300G"),

    SP_DESVIACIONES_CARTA("DesviacionesControlador", "SP_DESVIACIONES_CARTA",
                    "33300C", null, "33300U", "33300D",
                    "333001"),

    SP_DOCUMENTOS_MATRICULA("DocumentosmatriculasControlador",
                    "SP_DOCUMENTOS_MATRICULA", "32900C", null, "32900U",
                    "32900D", "32900G"),

    SP_RESPUESTA_MODELO_TIPO("DesviacionesControlador",
                    "SP_RESPUESTA_MODELO_TIPO", "26000C", null, "26000U",
                    "26000D",
                    "260001"),

    SP_DESVIACIONES_HISTORIA("DesviacionesControlador",
                    "SP_DESVIACIONES_HISTORIA", null, null, null, null,
                    "332001"),

    SP_ESTADOSCOBRO("EstadoscobrosControlador", "SP_ESTADOSCOBRO", "31100C",
                    null, "31100U", "31100D", "31100G"),

    SP_ESTRATOS("EstratosControlador", "SP_ESTRATOS", "" + "31000C", null,
                    "31000U", "31000D", "31000G"),

    SP_HISTORIA_CONVENIOS("FacturaConveniosControlador",
                    "SP_HISTORIA_CONVENIOS", null, null, "29700U", null,
                    "29700G"),

    SP_RESPUESTA_MODELO_PLANTILLA("CartapreguntapropsControlador",
                    "SP_RESPUESTA_MODELO_PLANTILLA", "26100C", null,
                    "26100U", "26100D", "26100G"),

    SP_FINANCIABLESDEDEUDA("FinanciablesdedeudasControlador",
                    "SP_FINANCIABLESDEDEUDA", "30600C", "30600R", "30600U",
                    "30600D", "30600G"),

    SP_FINANCIABLES("FinanciablespsControlador", "SP_FINANCIABLES", "30700C",
                    null, null, "30700D", "30700G"),

    SP_FRAUDES("FrmfraudesControlador", "SP_FRAUDES", "30100C", "30100R",
                    "30100U", "30100D", "30100G"),

    SP_DETALLEDISPOSICION("FrmdetalledisposicionsControlador",
                    "SP_DETALLEDISPOSICION", "33000C", null, "33000U",
                    "33000D", "33000G"),

    SP_FRAUDES_CARTA("FrmfraudesexternosControlador", "SP_FRAUDES_CARTA",
                    "30000C", null, "30000U", "30000D", "30000G"),

    SP_MODIFICACIONES("FrmmodificacionesdeudasControlador", "SP_MODIFICACIONES",
                    "28400C", "28400R", "28400U", "28400D",
                    "28400G"),

    SP_PARAMETROFACTURACION("ImpresionfacturasControlador",
                    "SP_PARAMETROFACTURACION", "23200C", "23200R", "23200U",
                    null, null),

    SP_MODIFICACIONESDEUDA("FrmmodificacionesdeudasControlador",
                    "SP_MODIFICACIONESDEUDA", "28300C", null, "28300U",
                    "28300D", "28300G"),

    SP_REDESACUEDUCTO("FrmredesacueductosControlador", "SP_REDESACUEDUCTO",
                    "26900C", "26900R", "26900U", "26900D",
                    "26900G"),

    SP_TARIFA_INT_RECA("FrmtarifasintrecargoControlador", "SP_TARIFA_INT_RECA",
                    "25200C", null, "25200U", "25200D",
                    null),

    INCENTIVOS_ECONOMICOS("GestiondesincentivosControlador", "BARRIOS", null,
                    null, "107005", null, "107003"),

    SP_MARCAS_MEDIDOR("MarcasmedidorsControlador", "SP_MARCAS_MEDIDOR",
                    "29000C", null, "29000U", "29000D", "29000G"),

    SP_MEDIDOR("MedidoresControlador", "SP_MEDIDOR", "28900C", null, "28900U",
                    "28900D", "28900G"),

    SP_NUMEROSDEFACTURA("NumerosdefacturaspControlador", "SP_NUMEROSDEFACTURA",
                    "23100C", null, "23100U", "23100D",
                    "23100G"),

    SP_MULTIUSUARIOS("MultiusuariosControlador", "SP_MULTIUSUARIOS", "28100C",
                    null, "28100U", "28100D", "28100G"),

    SP_PERIODO("PeriodosSpsControlador", "SP_PERIODO", "22700C", null, "22700U",
                    "22700D", "22700G"),

    SP_ORDENTRABAJO("OrdentrabajosControlador", "SP_ORDENTRABAJO", "27800C",
                    "27800R", "27800U", null, "27800G"),

    SP_RANGO("RangosControlador", "SP_RANGO", "27200C", null, "27200U",
                    "27200D", "27200G"),

    SP_SERVICIO("ServiciospsControlador", "SP_SERVICIO", "25800C", null,
                    "25800U", "25800D", "25800G"),

    SP_D_ABONOS("SubabonosControlador", "SP_D_ABONOS", null, null, null, null,
                    "32800G"),

    SP_TIPOVIVIENDA("TipoviviendasControlador", "SP_TIPOVIVIENDA", "24600C",
                    null, "24600U", "24600D", "24600G"),

    SP_TIPORESPUESTA_PQR("TiporespuestaspqrsControlador",
                    "SP_TIPORESPUESTA_PQR", "24700C", null, "24700U", "24700D",
                    "24700G"),

    SP_TIPOPREDIO("TipoprediospsControlador", "SP_TIPOPREDIO", "24800C", null,
                    "24800U", "24800D", "24800G"),

    SP_SOLICITUDSERVICIO("SolicitudserviciosControlador",
                    "SP_SOLICITUDSERVICIO", "25400C", "25400R", "25400U",
                    "25400D", "25400G"),

    SP_TARIFAS("TarifasspControlador", "SP_TARIFAS", "22900C", "22900R",
                    "22900U", "22900D", "22900G"),

    SP_USOS("UsosControlador", "SP_USOS", "24200C", null, "24200U", "24200D",
                    "24200G"),

    SP_UNIDADESRESIDENCIALES("UnidadesresidencialesControlador",
                    "SP_UNIDADESRESIDENCIALES", "24300C", null, "24300U",
                    "24300D", "24300G"),

    SP_D_ORDENTRABAJO("OrdentrabajosControlador", "SP_D_ORDENTRABAJO", "32600C",
                    null, "32600U", "32600D", "32600G"),

    SP_ORDENDOCPRESENTADO("OrdentrabajosControlador", "SP_ORDENDOCPRESENTADO",
                    "27900C", null, "27900U", "27900D",
                    "27900G"),

    SP_ORDENTRABAJONOVEDADES("OrdentrabajosControlador",
                    "SP_ORDENTRABAJONOVEDADES", "27700C", null, "27700U",
                    "27700D",
                    "27700G"),

    SP_TBLHIST_SALDO_CREDITO("SubfrmhistsaldocreditosControlador",
                    "SP_TBLHIST_SALDO_CREDITO", null, null, null, null,
                    null),

    IP_BANCOS("BancosdepagosControlador", "IP_BANCOS", "37500C", null, "37500U",
                    "37500D", "37500G"),

    IP_CLASE_PREDIOS("ClaseprediosControlador", "IP_CLASE_PREDIOS", "38000C",
                    null, "38000U", "38000D", "38000G"),

    IP_CONCEPTOS("ConceptosControlador", "IP_CONCEPTOS", "38100C", null,
                    "38100U", "38100D", "38100G"),

    IP_DESCUENTOS("DescuentosControlador", "IP_DESCUENTOS", "38900C", null,
                    "38900U", "38900D", "38900G"),

    IP_CUOTAS("CuotasControlador", "IP_CUOTAS", "39000C", null, "39000U",
                    "39000D", "39000G"),

    IP_TABLA_ESTRATOS("EstratossocioeconomicosControlador", "IP_TABLA_ESTRATOS",
                    "38300C", null, "38300U", "38300D",
                    "38300G"),

    IP_USUARIOS_PREDIAL("UsuariospredialsControlador", "IP_USUARIOS_PREDIAL",
                    null, "36700R", "36700U", null, "36700G"),

    IP_DESCUENTOS_ANO("DecuentoanosControlador", "IP_DESCUENTOS_ANO", "38800C",
                    null, "38800U", "38800D", "38800G"),

    IP_FACTURADOSACUERDOS("FacturadosacuerdosdetsControlador",
                    "IP_FACTURADOSACUERDOS", null, null, null, null,
                    "368006"),

    IP_FORMATO_CERTIFICADOS("FormatocertificadosControlador",
                    "IP_FORMATO_CERTIFICADOS", "37700C", null, "37700U",
                    "37700D", "37700G"),

    IP_FACTOR_EXPONENCIAL("FactorexpsControlador", "IP_FACTOR_EXPONENCIAL",
                    "39100C", null, "39100U", "39100D",
                    "39100G"),

    IP_CLASE_MUTACION("FrmclasemutacionsControlador", "IP_CLASE_MUTACION",
                    "38200C", null, "38200U", "38200D",
                    "38200G"),

    IP_TARIFASRESERVA("FrmconftarifasreservasControlador", "IP_TARIFASRESERVA",
                    "39500C", null, "39500U", "39500D",
                    "39500G"),

    IP_CONF_DESCUENTOSESPECIALES("FrmconfigurarporcentajesdescespsControlador",
                    "IP_CONF_DESCUENTOSESPECIALES",
                    "39600C", null, "39600U", "39600D", "39600G"),

    IP_CONFIGURACIONTARIFAS("FrmconfiguraciontarifasControlador",
                    "IP_CONFIGURACIONTARIFAS", "39700C", null, "39700U",
                    "39700D", "39700G"),

    IP_DESTINO_DOCUMENTOS("FrmdestinosControlador", "IP_DESTINO_DOCUMENTOS",
                    "39300C", null, "39300U", "39300D",
                    "39300G"),

    IP_DESCUENTOS_ESPECIALES("FrmdsctoespecialesControlador",
                    "IP_DESCUENTOS_ESPECIALES", "39800C", null, "39800U",
                    "39800D", "39800G"),

    IP_RESOLUCIONES_COPROPIETARIOS("FrmpropietariosigacsControlador",
                    "IP_RESOLUCIONES_COPROPIETARIOS", "40300C", null,
                    "40300U", "40300D", "40300G"),

    IP_NOTIFICACIONES("FrmnotiususControlador", "IP_NOTIFICACIONES", null, null,
                    "40200U", null, "40200G"),

    IP_MODIFICACIONES_PAGOS("ModificaciondepagosControlador",
                    "IP_MODIFICACIONES_PAGOS", "40700C", "40700R", "40700U",
                    "40700D", "40700G"),

    IP_NUMEROSDEFACTURA("NumerosdefacturasControlador", "IP_NUMEROSDEFACTURA",
                    "40000C", null, "40000U", null,
                    "40000G"),

    IP_PAGO_BANCOSCAB("PredialregispagbansControlador", "IP_PAGO_BANCOSCAB",
                    "40800C", "40800R", "40800U", "40800D",
                    "40800G"),

    IP_PROYECTOS_APORTESVOL("ProyectosaportesvolsControlador",
                    "IP_PROYECTOS_APORTESVOL", "39200C", null, "39200U",
                    "39200D", "39200G"),

    IP_PAGOS_BANCOSCAB_PAZ("RegistropagobancospazsControlador",
                    "IP_PAGOS_BANCOSCAB_PAZ", "41400C", "41400R", "41400U",
                    "41400D", "41400G"),

    IP_IGAC_RESOLUCIONES("ResolucionesControlador", "IP_IGAC_RESOLUCIONES",
                    "40600C", null, "40600U", "40600D",
                    "40600G"),

    IP_FACTURADOS("SubavaluosControlador", "IP_FACTURADOS", "38500C", null,
                    "38500U", "38500D", "38500G"),

    IP_FACTURADOSRESERVA("SubavaluosreservasControlador",
                    "IP_FACTURADOSRESERVA", null, null, null, null, "41700G"),

    IP_PAGOSDOBLES("PagosdoblesControlador", "IP_PAGOSDOBLES", "38600C", null,
                    "38600U", "38600D", "38600G"),

    IP_TARIFAS_ESPECIALES("TarifasespecialesControlador",
                    "IP_TARIFAS_ESPECIALES", "41900C", null, "41900U", "41900D",
                    "41900G"),

    IP_TARIFAS("TarifasControlador", "IP_TARIFAS", "37600C", null, "37600U",
                    "37600D", "37600G"),

    IP_TASAINTERES_FECHAS("TasainteresfechasControlador",
                    "IP_TASAINTERES_FECHAS", "42000C", null, "42000U", "42000D",
                    "42000G"),

    IP_TASASINTERES("TasasinterespredialsControlador", "IP_TASASINTERES",
                    "42100C", null, "42100U", "42100D", "42100G"),

    IP_TIPO_PREDIO("TipoprediosControlador", "IP_TIPO_PREDIO", "37900C", null,
                    "37900U", "37900D", "37900G"),

    SP_EMPRESAS_TERCERIZA("SpempresasControlador", "SP_EMPRESAS_TERCERIZA",
                    "31900C", "31900R", "31900U", "31900D",
                    "31900G"),

    SP_EMPRESAS_CONVENIO("SpempresasconveControlador", "SP_EMPRESAS_CONVENIO",
                    "32000C", "32000R", "32000U", "32000D",
                    "32000G"),

    IP_IGAC_RESOLUCIONESDET("ResolucionessubsControlador",
                    "IP_IGAC_RESOLUCIONESDET", "42200C", "42200R", "42200U",
                    "42200D", "42200G"),

    CLASENOVEDAD("ClasenovedadsControlador", "CLASENOVEDAD", "42500C", null,
                    "42500U", "42500D", "42500G"),

    CLASETIPOORDENDECOMPRA("ClasetipoordendecomprasControlador",
                    "CLASETIPOORDENDECOMPRA", "42600C", null, "42600U",
                    "42600D", "42600G"),

    CLASIFICA_DOCUMENTOS("ClasificadocumentosControlador",
                    "CLASIFICA_DOCUMENTOS", "19000C", null, "19000U", "19000D",
                    "19000G"),

    CAMARA_COMERCIO("CamaracomerciosControlador", "CAMARA_COMERCIO", "1700C",
                    null, "1700U", "1700D", "1700G"),

    CODIGO_RUP("CodigorupsControlador", "CODIGO_RUP", "20700C", null, "20700U",
                    "20700D", "20700G"),

    CODIGOSDIAN("DiansControlador", "CODIGOSDIAN", "42800C", null, "42800U",
                    "42800D", "42800G"),

    SP_USUARIO_LECTURAS("CorreccioncriticasControlador", "SP_USUARIO", "213221",
                    "213218", "213220", null, "213216"),

    D_NOVEDADCONTRATO("DnovedadcontratosControlador", "D_NOVEDADCONTRATO",
                    "42900C", null, "42900U", "42900D",
                    "42900G"),

    DOCUMENTOS_CONTRATOS("DocumentosdecontratosControlador",
                    "DOCUMENTOS_CONTRATOS", "42700C", null, "42700U", "42700D",
                    "42700G"),

    SECTORES("SectoresControlador", "SECTORES", "20300C", null, "20300U",
                    "20300D", "20300G"),

    SOLICITUDDISPONIBILIDAD("SolicitudDisponibilidadControlador",
                    "SOLICITUDDISPONIBILIDAD", "43100C", "43100R",
                    "43100U", "43100D", "43100G"),

    SP_RECAUDOS("ActualizacionpagosControlador", "SP_RECAUDOS", "22800C",
                    "22800R", "22800U", "22800D", "22800G"),

    TIPOCONTRATO_CGR("TipocontratocgrsControlador", "TIPOCONTRATO_CGR",
                    "43400C", null, "43400U", "43400D", "43400G"),

    BPPLANINDEJECUTADO_CONTRATO("SubbpplanindejecutadocontratosControlador",
                    "BPPLANINDEJECUTADO_CONTRATO", "43200C",
                    null, "43200U", "43200D", "43200G"),

    TIPOCONTRATOS("TipocontratosicesControlador", "TIPOCONTRATOS", "43500C",
                    null, "43500U", "43500D", "43500G"),

    TIPOCONTRATO_SECOP("TipocontratosecopsControlador", "TIPOCONTRATO_SECOP",
                    "20500C", null, "20500U", "20500D",
                    "20500G"),

    CLASETRANSACCIONC("TiposdenovedadsControlador", "CLASETRANSACCIONC",
                    "43700C", "43700R", "43700U", "43700D",
                    "43700G"),

    ASIGNACIONES("AsignacionControlador", "ASIGNACIONES", "44000C", null,
                    "44000U", "44000D", "44000G"),

    URGENCIAMANIFIESTA("UrgenciamanifiestasControlador", "URGENCIAMANIFIESTA",
                    "43600C", null, "43600U", "43600D",
                    "43600G"),

    HERRAMIENTAS("HerramientasControlador", "HERRAMIENTAS", "44200C", null,
                    "44200U", "44200D", "44200G"),

    D_MANTENIMIENTO("DmantenimpreventivosControlador", "D_MANTENIMIENTO",
                    "44100C", null, "44100U", "44100D", "44100G"),

    TIPOPOLIZA("TipopolizasControlador", "TIPOPOLIZA", "19800C", null, "19800U",
                    "19800D", "19800G"),

    LUGAR_PARQUEO("LugarparqueosControlador", "LUGAR_PARQUEO", "44800C", null,
                    "44800U", "44800D", "44800G"),

    MARCA("MarcaControlador", "MARCA", "44900C", null, "44900U", "44900D",
                    "44900G"),

    PROCEDENCIA("ProcedenciasControlador", "PROCEDENCIA", "45000C", null,
                    "45000U", "45000D", "45000G"),

    MANTENIMIENTO_MOV_ASOCIADO("MantenimientomovasociadosControlador",
                    "MANTENIMIENTO_MOV_ASOCIADO", "45100C", null,
                    "45100U", "45100D", "45100G"),

    PARTEFUNCIONAL("PartefuncionalControlador", "PARTEFUNCIONAL", "45200C",
                    null, "45200U", "45200D", "45200G"),

    TALLER("TallersControlador", "TALLER", "44400C", "44400R", "44400U",
                    "44400D", "44400G"),

    RESTRICCIONESPASE("RestriccionespasesControlador", "RESTRICCIONESPASE",
                    "45400C", null, "45400U", "45400D",
                    "45400G"),

    TAREAMANTENI("TareamanteniControlador", "TAREAMANTENI", "44300C", null,
                    "44300U", "44300D", "44300G"),

    VEHICULOACCESORIOS("VehiculoaccesoriosControlador", "VEHICULOACCESORIOS",
                    "45800C", null, "45800U", "45800D",
                    "45800G"),

    VINCULACIONCONDUCTOR("VinculacionsControlador", "VINCULACIONCONDUCTOR",
                    "45300C", null, "45300U", "45300D",
                    "45300G"),

    MANTENIMIENTO("ProgramacionmantenimientosControlador", "MANTENIMIENTO",
                    "44700C", "44700R", "44700U", "44700D",
                    "44700G"),

    BANCOS_NOMINA("BancosControlador", "BANCOS_NOMINA", "45900C", "45900R",
                    "45900U", "45900D", "45900G"),

    TIPO_DE_AUTOMOTOR("TipodeautomotorsControlador", "TIPO_DE_AUTOMOTOR",
                    "45600C", null, "45600U", "45600D", "45600G"),

    SP_ELEMENTOS("InventarioSpsControlador", "SP_ELEMENTOS", "32200C", null,
                    "32200U", "32200D", "32200G"),

    TIPO_DE_COMBUSTIBLE("TipodecombustiblesControlador", "TIPO_DE_COMBUSTIBLE",
                    "46000C", null, "46000U", "46000D",
                    "46000G"),

    ES_ASP_TECN("AspectosTecnicosControlador", "ES_ASP_TECN", "46400C", null,
                    "46400U", "46400D", "46400G"),

    CAUSARETIRO("CausaderetirosControlador", "CAUSARETIRO", "46500C", null,
                    "46500U", "46500D", "46500G"),

    CARGOS("CargosControlador", "CARGOS", "46300C", "46300R", "46300U",
                    "46300D", "46300G"),

    CLASE_DEMANDANTE("ClasedemandantesControlador", "CLASE_DEMANDANTE",
                    "46900C", null, "46900U", "46900D", "46900G"),

    CONCEPTOS_AGR("ConceptosagrsControlador", "CONCEPTOS_AGR", "47000C", null,
                    "47000U", "47000D", "47000G"),

    ARCHIVOS_D_TRANSACCION("AnexosestudiospreviosControlador",
                    "ARCHIVOS_D_TRANSACCION", "46800C", null, "46800U",
                    "46800D", "46800G"),

    ARCHIVOS_ES_ESTPREVIO("AnexosestudiospreviosControlador",
                    "ARCHIVOS_ES_ESTPREVIO", "46600C", null, "46600U",
                    "46600D", "46600G"),

    CONCEPTOS("ConceptosbsControlador", "CONCEPTOS", "15100C", "15100R",
                    "15100U", "15100D", "15100G"),

    ES_D_ETAPA("EpetapaControlador", "ES_D_ETAPA", "47300C", null, "47300U",
                    "47300D", "47300G"),

    ES_ITEMS_E("EpitemsestproysControlador", "ES_ITEMS_E", "47400C", "47400R",
                    "47400U", "47400D", "47400G"),

    CONCEPTO_CENTROCOSTO("ConceptosbsControlador", "CONCEPTO_CENTROCOSTO",
                    "47600C", null, "47600U", "47600D",
                    "47600G"),

    ES_RIESGO("EpriesgosControlador", "ES_RIESGO", "47800C", "47800R", "47800U",
                    "47800D", "47800G"),

    ES_T_RIESGO("EptriesgosControlador", "ES_T_RIESGO", "47900C", null,
                    "47900U", "47900D", "47900G"),

    ES_DITEM_E("EpitemsestproysControlador", "ES_DITEM_E", "47700C", null,
                    "47700U", "47700D", "47700G"),

    ES_CONTRATO("EscontratosControlador", "ES_CONTRATO", "48200C", null,
                    "48200U", "48200D", "48200G"),

    ES_COSTO_PER("EscostoperControlador", "ES_COSTO_PER", "48400C", null,
                    "48400U", "48400D", "48400G"),

    ES_ETAPA("EsetapasControlador", "ES_ETAPA", "47200C", null, "47200U",
                    "47200D", "47200G"),

    ES_CRITERIOS("EscriteriosyfactoresControlador", "ES_CRITERIOS", "48300C",
                    null, "48300U", "48300D", "48300G"),

    ES_DFACTORES("EsfactoresporestproysControlador", "ES_DFACTORES", "48900C",
                    null, "48900U", "48900D", "48900G"),

    ES_TASP_TECN("EstasptecsControlador", "ES_TASP_TECN", "46700C", null,
                    "46700U", "46700D", "46700G"),

    ES_TIPOCONTRATO("EstcontratosControlador", "ES_TIPOCONTRATO", "11100C",
                    null, "11100U", "11100D", "11100G"),

    ETAPA_PR_DOC("EtapaPreDocControlador", "ETAPA_PR_DOC", "49000C", null,
                    "49000U", "49000D", null),

    CLASE_PRERREQUISITO("FrmclasepresControlador", "CLASE_PRERREQUISITO",
                    "49100C", null, "49100U", "49100D", "49100G"),

    ES_RUBROS_EST_DIS("EsrubrosestdisproysControlador", "ES_RUBROS_EST_DIS",
                    "49200C", null, "49200U", "49200D",
                    "49200G"),

    ES_DETA_ESTPR("FrmcumplimientoproysControlador", "ES_DETA_ESTPR", "49300C",
                    null, "49300U", "49300D", null),

    ES_COTIZACIONES("FrmcotizacionesproysControlador", "ES_COTIZACIONES",
                    "49400C", null, "49400U", "49400D", "49400G"),

    ES_ESTPREVIO("FrmestprevioproysControlador", "ES_ESTPREVIO", "48100C",
                    "48100R", "48100U", "48100D", "48100G"),

    ES_ESTPREVIO_UNSPSC("FrmestpreviounspscsControlador", "ES_ESTPREVIO_UNSPSC",
                    "50400C", null, "50400U", "50400D",
                    "50400G"),

    CODIGOS_UNSPSC("FrmcodigosunspscsControlador", "CODIGOS_UNSPSC", "50600C",
                    null, "50600U", "50600D", "50600G"),

    ES_PROY_METAS("FrmesmetasControlador", "ES_PROY_METAS", "49800C", null,
                    "49800U", "49800D", null),

    ES_EST_PROY("FrmestproyControlador", "ES_EST_PROY", "50900C", "50900R",
                    "50900U", "50900D", "50900G"),

    ES_METAS_PI("FrmestproyControlador", "ES_METAS_PI", "51000C", null,
                    "51000U", "51000D", "51000G"),

    ES_ESTPREVIO_EXPERIENCIA("FrmestprevioexperienciasControlador",
                    "ES_ESTPREVIO_EXPERIENCIA", "50500C", null,
                    "50500U", "50500D", "50500G"),

    ES_RIES_ESTPR("FrmriesgosproysControlador", "ES_RIES_ESTPR", "51500C",
                    "51500R", "51500U", "51500D", "51500G"),

    ES_POLIZA_EST("FrmpolizasproysControlador", "ES_POLIZA_EST", "51200C", null,
                    "51200U", "51200D", "51200G"),

    TTP_CLASE_CRITERIO("FrmtipcriteriosControlador", "TTP_CLASE_CRITERIO",
                    "51800C", null, "51800U", "51800D",
                    "51800G"),

    ES_ASP_ESTPR("FrmsoporteproysControlador", "ES_ASP_ESTPR", "52200C", null,
                    "52200U", "52200D", "52200G"),

    ES_LOCALIZA("FrmsubestudiolocalizacionproysControlador", "ES_LOCALIZA",
                    "52100C", null, "52100U", "52100D",
                    "52100G"),

    ES_RUBROS_PROY("FrmrubrosproyControlador", "ES_RUBROS_PROY", "51700C", null,
                    "51700U", "51700D", "51700G"),

    INVENTARIO_PARQUE_AUTOMOTOR("InventarioparqueautomotorControlador",
                    "INVENTARIO_PARQUE_AUTOMOTOR", "50700C",
                    "50700R", "50700U", "50700D", "50700G"),

    TRANSACCION("TransaccionsControlador", "TRANSACCION", "18800C", "18800R",
                    "18800U", "18800D", "18800G"),

    OBSERVACIONES("ObservacionesControlador", "OBSERVACIONES", "52700C",
                    "52700R", "52700U", "52700D", "52700G"),

    ES_MODELO("MresolucionsControlador", "ES_MODELO", "52400C", null, "52400U",
                    "52400D", "52400G"),

    PREREQUISITOS("PrerequisitosControlador", "PREREQUISITOS", "52800C", null,
                    "52800U", "52800D", "52800G"),

    PRE_REQUISITOS_ETAPA("PRE_REQUISITOS_ETAPA", "PRE_REQUISITOS_ETAPA",
                    "53000C", null, "53000U", "53000D", "53000G"),

    EVENTOS_CALENDARIO("EventosCalendariosControlador", "EVENTOS_CALENDARIO",
                    "53100C", null, "53100U", "53100D",
                    "53100G"),

    OBSERVACIONES_ETA("ObservacionesetapasControlador", "OBSERVACIONES_ETA",
                    "52900C", "52900R", "52900U", "52900D",
                    "52900G"),

    PROPONENTE("ProponenteetapasControlador", "PROPONENTE", "52000C", null,
                    "52000U", "52000D", "52000G"),

    TIPOCONTRATO_PR("TipocontratosControlador", "TIPOCONTRATO_PR", "18400C",
                    "18400R", "18400U", "18400D", "18400G"),

    ETAPA_PR("TipocontratosControlador", "ETAPA_PR", "49700C", null, "49700U",
                    "49700D", null),

    VARIABLE("TipocontratosControlador", "VARIABLE", "52500C", null, "52500U",
                    "52500D", null), VARIABLE_PROPONENTE(
                                    "VariableproponentesControlador",
                                    "VARIABLE_PROPONENTE", "51900C", null,
                                    "51900U", "51900D", "51900G"),

    PRERREQUISITOS_PROPONENTE("PrerrequisitosproponentesControlador",
                    "PRERREQUISITOS_PROPONENTE", null, null, "53200U",
                    null, "53200G"),

    CUOTASPARTES("CuotaspartesControlador", "CUOTASPARTES", "53600C", null,
                    "53600U", "53600D", "53600G"),

    NOVEDADES("DeduciblesControlador", "NOVEDADES", "53800C", null, "53800U",
                    "53800D", "53800G"),

    CUOTASPARTES_DETALLE("CuotaspartesdetallesControlador",
                    "CUOTASPARTES_DETALLE", "53900C", "53900R", "53900U",
                    "53900D", "53900G"),

    CERTIFICADO_PLAN_COMPRAS("FrmcertplancomprasControlador",
                    "CERTIFICADO_PLAN_COMPRAS", "54100C", "54100R", "54100U",
                    "54100D", "54100G"),

    ACTPLAN_DE_COMPRAS("PactualplancomprasControlador", "ACTPLAN_DE_COMPRAS",
                    "54300C", "54300R", "54300U", "54300D",
                    "54300G"),

    DETALLE_PLAN_COMPRAS("DplancompraselemsControlador", "DETALLE_PLAN_COMPRAS",
                    "11400C", null, "11400U", "11400D",
                    "11400G"),

    REQUISICION("PrequisicionsControlador", "REQUISICION", "54400C", "54400R",
                    "54400U", "54400D", "54400G"),

    ACTDETPLAN_COMPRAS("PactualplancomprasControlador", "ACTDETPLAN_COMPRAS",
                    "54600C", null, "54600U", "54600D",
                    "54600G"),

    SECCIONALES("SeccionalesControlador", "SECCIONALES", "54700C", null,
                    "54700U", "54700D", "54700G"),

    PROPUESTA("PropuestareqsControlador", "PROPUESTA", "55000C", null, "55000U",
                    "55000D", "55000G"),

    DETALLEREQUIS("PropuestareqsControlador", "DETALLEREQUIS", "19300C", null,
                    "19300U", "19300D", "19300G"),

    PLAN_DE_COMPRAS("PlandecompraselemsControlador", "PLAN_DE_COMPRAS",
                    "54200C", "54200R", "54200U", "54200D",
                    "54200G"),

    BP_PLAN_INDICATIVO("BpplanindicativosControlador", "BP_PLAN_INDICATIVO",
                    "55200C", "55200R", "55200U", "55200D",
                    "55200G"),

    D_TRANSACCION("TransaccionsControlador", "D_TRANSACCION", "52300C", null,
                    "52300U", "52300D", "52300G"),

    SUBPROYECTO("SubproyectosControlador", "SUBPROYECTO", "54500C", null,
                    "54500U", "54500D", "54500G"),

    BPTIPONOVEDAD("FrmbptiponovedadpsControlador", "BPTIPONOVEDAD", "21800C",
                    "21800R", "21800U", "21800D", "21800G"),

    BPGASTOSDEINVERSION("FrmbpgastosdeinversionsControlador",
                    "BPGASTOSDEINVERSION", "55700C", null, "55700U", "55700D",
                    "55700G"),

    BP_ACTIVIDADES("FrmactividadesControlador", "BP_ACTIVIDADES", "55800C",
                    null, "55800U", "55800D", "55800G"),

    BP_CODIGOS_FUT("FrmcodigosfutsControlador", "BP_CODIGOS_FUT", "56000C",
                    null, "56000U", "56000D", "56000G"),

    COMPONENTES("FrmcomponentesControlador", "COMPONENTES", "20600C", "20600R",
                    "20600U", "20600D", "20600G"),

    FUENTESFINANCIACION("FrmfuentesfinanciacionsControlador",
                    "FUENTESFINANCIACION", "56200C", "56200R", "56200U",
                    "56200D", "56200G"),

    BP_INDICADORES_PI("FrmindicadoresbpsControlador", "BP_INDICADORES_PI",
                    "51100C", null, "51100U", "51100D",
                    "51100G"),

    BP_ENTIDADES("FrmentidadesControlador", "BP_ENTIDADES", "51600C", null,
                    "51600U", "51600D", "51600G"),

    DESTINACION_SCHIP("DestinoRecursoscgrControlador", "DESTINACION_SCHIP",
                    "56900C", null, "56900U", "56900D",
                    "56900G"),

    FINALIDAD_GASTO_SCHIP("FinalidadgastosControlador", "FINALIDAD_GASTO_SCHIP",
                    "57000C", null, "57000U", "57000D",
                    "57000G"),

    COMPONENTES_ACTIVIDADES("FrmcomponentesactividadesControlador",
                    "COMPONENTES_ACTIVIDADES", "51300C", null, "51300U",
                    "51300D", "51300G"),

    RECURSO_SCHIP("RecursosControlador", "RECURSO_SCHIP", "57400C", null,
                    "57400U", "57400D", "57400G"),

    OEI_INGRESOS_SCHIP("OrigenespecificoingresosControlador",
                    "OEI_INGRESOS_SCHIP", "57300C", null, "57300U", "57300D",
                    "57300G"),

    OEI_GASTOS_SCHIP("OrigenespecificogastosControlador", "OEI_GASTOS_SCHIP",
                    "57500C", null, "57500U", "57500D",
                    "57500G"),

    BP_SECTOR_FICHA_TECNICA("FrmmodelofichasControlador",
                    "BP_SECTOR_FICHA_TECNICA", "56500C", "56500R", "56500U",
                    "56500D", "56500G"),

    BP_MODELO_FICHA_TECNICA("FrmmodelofichasControlador",
                    "BP_MODELO_FICHA_TECNICA", "56400C", null, "56400U",
                    "56400D",
                    null),

    BP_RUBRO_INVERSION("FrmrubroinversionsControlador", "BP_RUBRO_INVERSION",
                    "59000C", "59000R", "59000U", "59000D",
                    "59000G"),

    SECTORDNP("FrmsectoresdnpsControlador", "SECTORDNP", "56800C", null,
                    "56800U", "56800D", "56800G"),

    BPNOVEDADPROYECTO("FrmSolicitudCdpControlador", "BPNOVEDADPROYECTO",
                    "13000C", "13000R", "13000U", "13000D",
                    "13000G"),

    BP_TIPOSCOMPONENTES("FrmtipocomponentesControlador", "BP_TIPOSCOMPONENTES",
                    "56100C", null, "56100U", "56100D",
                    "56100G"),

    SUBFUENTESFINANCIACION("FrmsubfuentesfinanciacionsControlador",
                    "SUBFUENTESFINANCIACION", "59200C", null, "59200U",
                    "59200D", "59200G"),

    BP_D_NOVEDADPROYECTO("FrmsubdnovedadesproyectosControlador",
                    "BP_D_NOVEDADPROYECTO", null, null, null, "13100D",
                    null),

    TIPOSPROYECTO("FrmtiposproyectosControlador", "TIPOSPROYECTO", "59400C",
                    null, "59400U", "59400D", "59400G"),

    BPTIPOSNOVEDADTECNICA("FrmtiponovedadtecnicasControlador",
                    "BPTIPOSNOVEDADTECNICA", "59700C", null, "59700U",
                    "59700D", "59700G"),

    BP_PLAN_INDICATIVO_FUENTES("FrmvalorfuentesControlador",
                    "BP_PLAN_INDICATIVO_FUENTES", "60000C", null, "60000U",
                    "60000D", "60000G"),

    PROYECTOLOCALIZACION("FrmsubproyectoslocalizacionsControlador",
                    "PROYECTOLOCALIZACION", "59600C", null, "59600U",
                    "59600D", "59600G"),

    PALABRAS("FrmprocesosControlador", "PALABRAS", "59900C", null, "59900U",
                    "59900D", "59900G"),

    BP_NIVEL_CUMPLIMIENTO("NivelcumplimientosControlador",
                    "BP_NIVEL_CUMPLIMIENTO", "55600C", null, "55600U", "55600D",
                    "55600G"),

    BP_NIVEL_PLAN_IND("NivelplanindsControlador", "BP_NIVEL_PLAN_IND", "55400C",
                    null, "55400U", "55400D", "55400G"),

    BP_RUBRO_INVERSION_DET("FrmrubroinversionsControlador",
                    "BP_RUBRO_INVERSION_DET", "57600C", null, "57600U",
                    "57600D", null),

    PREGUNTAS("PreguntasControlador", "PREGUNTAS", "59800C", null, "59800U",
                    "59800D", "59800G"),

    BP_PROYECTO_PLAN_INDICATIVO("SubbpproyectoplanindicativosControlador",
                    "BP_PROYECTO_PLAN_INDICATIVO", "50300C",
                    null, "50300U", "50300D", "50300G"),

    PROYECTOS("FrmproyectosControlador", "PROYECTOS", "3200C", "3200R", "3200U",
    		"3200D", "3200G"),

    BPPROYECTONOVEDADESTECNICAS("FrmproyectosControlador",
                    "BPPROYECTONOVEDADESTECNICAS", "59100C", null, "59100U",
                    "59100D", "59100G"),

    PREGUNTAS_PROYECTOS("FrmproyectosControlador", "PREGUNTAS_PROYECTOS",
                    "59300C", null, "59300U", "59300D", "59300G"),

    BP_OBJNOVEDADPROY("SubobjnovedadsControlador", "BP_OBJNOVEDADPROY",
                    "60300C", null, "60300U", "60300D", "60300G"),

    BP_PROYECTOS_MODIFICACIONES("SubmodificacionesproyectosControlador",
                    "BP_PROYECTOS_MODIFICACIONES", "60400C", null,
                    "60400U", "60400D", "60400G"),

    BP_PLAN_INDICATIVO_METAS("SubbpplanindicativometasControlador",
                    "BP_PLAN_INDICATIVO_METAS", "43300C", null,
                    "43300U", "43300D", "43300G"),

    BP_PROYECTOSRUBROS("SubproyectorubrosControlador", "BP_PROYECTOSRUBROS",
                    "60100C", "60100R", "60100U", "60100D",
                    "60100G"),

    BP_PROYFUENTESFINANCIACION("SubproyfuentesfinanciacionsControlador",
                    "BP_PROYFUENTESFINANCIACION", "60600C", null,
                    "60600U", "60600D", "60600G"),

    ESCALAFON("EscalafonsControlador", "ESCALAFON", "46200C", "46200R",
                    "46200U", "46200D", "46200G"),

    TIPOS_DOCUMENTOS("DocumentosControlador", "TIPOS_DOCUMENTOS", "20900C",
                    null, "20900U", "20900D", "20900G"),

    BENEFICIARIOS_UPC("BeneficiariosupcsControlador", "BENEFICIARIOS_UPC",
                    "60800C", "60800R", "60800U", "60800D",
                    "60800G"),

    PERSONAL("PersonalsControlador", "PERSONAL", "21000C", "21000R", "21000U",
                    "21000D", "21000G"),

    BPRESPONSABLEPROYECTO("SubresponsablesproyectosControlador",
                    "BPRESPONSABLEPROYECTO", "60200C", null, "60200U",
                    "60200D", "60200G"),

    ENCARGOS("EncargosControlador", "ENCARGOS", "61300C", "61300R", "61300U",
                    "61300D", "61300G"),

    EMBARGOS("EmbargosControlador", "EMBARGOS", "61100C", "61100R", "61100U",
                    "61100D", "61100G"),

    ESTABLECIMIENTOS_DOCENTES("EstablecimientosControlador",
                    "ESTABLECIMIENTOS_DOCENTES", "61400C", "61400R", "61400U",
                    "61400D", "61400G"),

    ESTADO_CIVIL("EstadocivilsControlador", "ESTADO_CIVIL", "61500C", null,
                    "61500U", "61500D", "61500G"),

    FINANCIABLES_DE_NOMINA("FinanciablesControlador", "FINANCIABLES_DE_NOMINA",
                    "61600C", "61600R", "61600U", "61600D",
                    "61600G"),

    FONDO("FondosControlador", "FONDO", "61700C", "61700R", "61700U", "61700D",
                    "61700G"),

    FORMANOMBRAMIENTO("FormanombramientosControlador", "FORMANOMBRAMIENTO",
                    "61800C", null, "61800U", "61800D",
                    "61800G"),

    RETENCIONTIPO("FrmretenciontiposControlador", "RETENCIONTIPO", "61900C",
                    null, "61900U", "61900D", "61900G"),

    HISTORICOS("HistoricosControlador", "HISTORICOS", "62000C", null, "62000U",
                    "62000D", "62000G"),

    INCAPACIDADES("IncapacidadesControlador", "INCAPACIDADES", "62100C",
                    "62100R", "62100U", "62100D", "62100G"),

    CAMBIOSDEFONDO("CambiosFondoControlador", "CAMBIOSDEFONDO", "61000C", null,
                    null, null, "61000G"),

    LICENCIAS("LicenciasControlador", "LICENCIAS", "62600C", "62600R", "62600U",
                    "62600D", "62600G"),

    INTERRUPCION_VACACIONES("InterrupcionvacacionesControlador",
                    "INTERRUPCION_VACACIONES", "62400C", "62400R",
                    "62400U", "62400D", "62400G"),

    PARENTESCO("ParentescosControlador", "PARENTESCO", "60900C", null, "60900U",
                    "60900D", "60900G"),

    OFICINA_BANCO_AGRARIO("OficinabancoagrariosControlador",
                    "OFICINA_BANCO_AGRARIO", "61200C", null, "61200U",
                    "61200D", "61200G"),

    UNIDADPROYECTOS("FrmunidadproyectosControlador", "UNIDADPROYECTOS",
                    "55300C", null, "55300U", "55300D", "55300G"),

    PARAMETROS_DE_ENTRADA("ParametrosdeentradasControlador",
                    "PARAMETROS_DE_ENTRADA", "63000C", "63000R", "63000U",
                    "63000D", "63000G"),

    PATRONALES("ParametrosdeentradasControlador", "PATRONALES", "63100C", null,
                    "63100U", "63100D", null),

    PERIODOS("PeriodosControlador", "PERIODOS", "47100C", null, "47100U",
                    "47100D", "47100G"),

    PROFESIONES("ProfesionesControlador", "PROFESIONES", "63900C", null,
                    "63900U", "63900D", "63900G"),

    PROYECCIONES("ProyeccionesControlador", "PROYECCIONES", "65300C", null,
                    "65300U", "65300D", null),

    PROYECTOSPERSONAL("ProyectosporpersonasControlador", "PROYECTOSPERSONAL",
                    "65400C", null, "65400U", "65400D",
                    "65400G"),

    PROGRAMACION("FrmprogramacionfinancieraControlador", "PROGRAMACION",
                    "57100C", "57100R", "57100U", "57100D",
                    "57100G"),

    HISTORIAL_DE_CARGOS("PersonalsControlador", "HISTORIAL_DE_CARGOS", "65600C",
                    null, "65600U", "65600D", "65600G"),

    DETALLE_PROFESIONES("PersonalsControlador", "DETALLE_PROFESIONES", "65700C",
                    null, "65700U", "65700D", "65700G"),

    SEDES("SedesControlador", "SEDES", "63300C", null, "63300U", "63300D",
                    "63300G"),

    TIPO_ENFERMEDADES("TipoenfermedadesControlador", "TIPO_ENFERMEDADES",
                    "62300C", null, "62300U", "62300D", "62300G"),

    TIPO_EMBARGO("TipoEmbargosControlador", "TIPO_EMBARGO", "3700C", null,
                    "3700U", "3700D", "3700G"),

    TIPO_INCAPACIDAD("TipoincapacidadsControlador", "TIPO_INCAPACIDAD",
                    "62200C", null, "62200U", "62200D", "62200G"),

    TIPOS_LICENCIA("TipolicenciasControlador", "TIPOS_LICENCIA", "62700C", null,
                    "62700U", "62700D", "62700G"),

    TIPO_RETENCIONES("TiposretencionesControlador", "TIPO_RETENCIONES",
                    "65900C", null, "65900U", "65900D", "65900G"),

    TIPOS_DE_EMPLEADO("TiposdeempleadosControlador", "TIPOS_DE_EMPLEADO",
                    "54000C", null, "54000U", "54000D", "54000G"),

    VACACIONES("VacacionesControlador", "VACACIONES", "62500C", "62500R",
                    "62500U", "62500D", "62500G"),

    RETENCIONPERSONAL("RetencionpersonalsControlador", "RETENCIONPERSONAL",
                    "66000C", null, "66000U", "66000D",
                    "66000G"),

    SF_ESTRATO("EstratosfgControlador", "SF_ESTRATO", "66700C", null, "66700U",
                    "66700D", "66700G"),

    SF_OBJETO_COBRO("FacturacionconceptosControlador", "SF_OBJETO_COBRO",
                    "66600C", "66600R", "66600U", "66600D",
                    "66600G"),

    SF_ABONOS("FrmconsultaabonosControlador", "SF_ABONOS",
                    null, "101700R", "101700U", "101700D", "101700G"),

    SF_DETALLE_FACTURA("", "SF_DETALLE_FACTURA", null, null, null, null, null),

    SP_PAGO("", "SP_PAGO", null, null, null, "27600D", null),

    SF_TARIFA("SftarifaControlador", "SF_TARIFA", "66800C", null, "66800U",
                    "66800D", "66800G"),

    SF_CONCEPTOS("SfconceptosControlador", "SF_CONCEPTOS", "66300C", "66300R",
                    "66300U", "66300D", "66300G"),

    SF_CONCEPTOS_DEPENDIENTES("ConceptosdependientesControlador",
                    "SF_CONCEPTOS_DEPENDIENTES", "67100C", null, "67100U",
                    "67100D", "67100G"),

    SF_GRUPOS_CONCEPTOS("FrmGruposConceptosControlador", "SF_GRUPOS_CONCEPTOS",
                    "66200C", "66200R", "66200U", "66200D",
                    "66200G"),

    SF_DETALLE_GRUPOS_CONCEPTOS("FrmGruposConceptosControlador",
                    "SF_DETALLE_GRUPOS_CONCEPTOS", "67200C", null,
                    "67200U", "67200D", null),

    GRUPO_PLANOS("GrupoPlanosControlador", "GRUPO_PLANOS", "67300C", null,
                    "67300U", "67300D", "67300G"),

    PLANOS("FrmPlanosControlador", "PLANOS", "67400C", null, "67400U", "67400D",
                    "67400G"),

    SF_DETALLE_COBRO("FacturacionconceptosControlador", "SF_DETALLE_COBRO",
                    "67500C", null, "67500U", "67500D",
                    "67500G"),

    DETALLE_PLANOS_SQL("FrmDetallePlanosSqlsControlador", "DETALLE_PLANOS_SQL",
                    "67600C", "67600R", "67600U", "67600D",
                    "67600G"),

    TIPOCOBRO("FrmTipoCobroSfControlador", "SF_TIPO_COBRO", "66500C", "66500R",
                    "66500U", "66500D", "66500G"),

    SF_FACTURA("", "SF_FACTURA", null, null, null, null, null),

    SF_CREE_CONCEPTOS("FrmcreeconfiguracionsControlador", "SF_CREE_CONCEPTOS",
                    "67700C", "67700R", "67700U", "67700D",
                    null),

    SF_CUENTAS_AUXTERCERO("FrmctaauxtercerosControlador",
                    "SF_CUENTAS_AUXTERCERO", "68000C", null, "68000U", "68000D",
                    "68000G"),

    SF_TEMP_DETALLE_ACUERDO("", "SF_TEMP_DETALLE_ACUERDO", null, null, null,
                    null, null),

    SF_TEMP_DETALLE_CUOTA("", "SF_TEMP_DETALLE_CUOTA", null, null, null, null,
                    null),

    NAT_TIPO_CAUSA("TipocausasControlador", "NAT_TIPO_CAUSA", "68700C", null,
                    "68700U", "68700D", "68700G"),

    NAT_PRIMA_SERVICIOS("SubAuxilioMControlador", "NAT_PRIMA_SERVICIOS",
                    "68600C", null, "68600U", "68600D", "68600G"),

    NAT_CLASES_DE_PRUEBA("ClasesDePruebasControlador", "NAT_CLASES_DE_PRUEBA",
                    "68900C", null, "68900U", "68900D",
                    "68900G"),

    NAT_CLASES_DE_CONVOCATORIA("ClasesdeconvocatoriasControlador",
                    "NAT_CLASES_DE_CONVOCATORIA", "68800C", null,
                    "68800U", "68800D", "68800G"),

    NAT_ACTIVIDADES("NatSubActividadesControlador", "NAT_ACTIVIDADES", "69000C",
                    null, "69000U", "69000D", "69000G"),

    NAT_DIARIOS("DiariosControlador", "NAT_DIARIOS", "69100C", null, "69100U",
                    "69100D", "69100G"),

    NAT_PUBLICACIONES("NatpublicacionesControlador", "NAT_PUBLICACIONES",
                    "69200C", null, "69200U", "69200D", "69200G"),

    NAT_TIPO_EVALUACION("TipoEvaluacionControlador", "NAT_TIPO_EVALUACION",
                    "69400C", null, "69400U", "69400D",
                    "69400G"),

    NAT_IDIOMAS("IdiomasControlador", "NAT_IDIOMAS", "69300C", null, "69300U",
                    "69300D", "69300G"),

    NAT_DATOS_PERSONALES("NatdatospersonalesControlador",
                    "NAT_DATOS_PERSONALES", "68500C", "68500R", "68500U",
                    "68500D", "68500G"),

    NAT_INCENTIVOS("NatsubincentivosControlador", "NAT_INCENTIVOS", "69500C",
                    null, "69500U", "69500D", "69500G"),

    NAT_EDUCACION_BASICAYMEDIA("SubAcademicasControlador",
                    "NAT_EDUCACION_BASICAYMEDIA", "69800C", "69800R", "69800U",
                    "69800D", "69800G"),

    NAT_COMPENSATORIO("NatSubCompensatoriosControlador", "NAT_COMPENSATORIO",
                    "70100C", null, "70100U", "70100D",
                    "70100G"),

    NAT_EXPERIENCIA_LABORAL("ExperienciaLaboralsControlador",
                    "NAT_EXPERIENCIA_LABORAL", "70500C", "70500R", "70500U",
                    "70500D", "70500G"),

    NAT_NOMBRAMIENTO("NatsubnombramientoControlador", "NAT_NOMBRAMIENTO",
                    "69700C", "69700R", "69700U", "69700D",
                    "69700G"),

    NAT_FORMACION_ACADEMICA("SubAcademicasControlador",
                    "NAT_FORMACION_ACADEMICA", "70600C", null, "70600U",
                    "70600D",
                    "70600G"),

    NAT_OTROS_ESTUDIOS("SubAcademicasControlador", "NAT_OTROS_ESTUDIOS",
                    "71300C", null, "71300U", "71300D", "71300G"),

    NAT_SEGURIDAD_SOCIAL("NatSubArpsControlador", "NAT_SEGURIDAD_SOCIAL",
                    "71000C", null, "71000U", "71000D", "71000G"),

    NAT_TIPO_ACTO_ADTIVO("TipoactoadtivosControlador", "NAT_TIPO_ACTO_ADTIVO",
                    "70000C", null, "70000U", "70000D",
                    "70000G"),

    NAT_PRIMA_TECNICA("NatsubprimatecnicasControlador", "NAT_PRIMA_TECNICA",
                    "71800C", "71800R", "71800U", "71800D",
                    "71800G"),

    NAT_CALIFICACION_PRUEBAS("CalificacionPruebasInscritosControlador",
                    "NAT_CALIFICACION_PRUEBAS", "72100C", null,
                    "72100U", "72100D", null),

    SF_COTIZACION("FrmcotizacionsControlador", "SF_COTIZACION", "67800C",
                    "67800R", "67800U", "67800D", "67800G"),

    SF_DETALLE_COTIZACION("FrmcotizacionsControlador", "SF_DETALLE_COTIZACION",
                    "68300C", null, "68300U", "68300D",
                    "68300G"),

    FAMILIARES("FamiliaresControlador", "FAMILIARES", "72300C", "72300R",
                    "72300U", "72300D", "72300G"),

    SST_CRONOGRAMA("FrmCronogramasStsControlador", "SST_CRONOGRAMA", "72400C",
                    "72400R", "72400U", "72400D", "72400G"),

    SST_TIPO_TRANSACCION("FrmtipotransaccionsstsControlador",
                    "SST_TIPO_TRANSACCION", "72700C", null, "72700U",
                    "72700D", "72700G"),

    ELEMENTO_PROTECCION_PERSONAL("FrmAsignElementosProtPersonalControlador",
                    "ELEMENTO_PROTECCION_PERSONAL", "72900C",
                    null, "72900U", "72900D", "72900G"),

    SST_TIPORACI("frmtiporacisstControlador", "SST_TIPORACI", "72500C", null,
                    "72500U", null, "72500G"),

    SST_CLASE_EVENTO("TipoClaseEventoSstsControlador", "SST_CLASE_EVENTO",
                    "72800C", null, "72800U", "72800D",
                    "72800G"),

    SST_TIPO_ACTIVIDAD("frmtipoactividadsst", "SST_TIPO_ACTIVIDAD", "72600C",
                    null, "72600U", "72600D", "72600G"),

    NAT_HISTORIA_CLINICA("NatHistoriaClinicaControlador",
                    "NAT_HISTORIA_CLINICA", "73100C", "73100R", "73100U",
                    "73100D", "73100G"),

    NAT_SEGURIDAD_INDUSTRIAL("NatincidentestrabajosstsControlador",
                    "NAT_SEGURIDAD_INDUSTRIAL", "73500C", "73500R",
                    "73500U", "73500D", "73500G"),

    NOVEDADES_AUTOLIQUIDACION("RiesgosControlador", "NOVEDADES_AUTOLIQUIDACION",
                    "73600C", "73600R", "73600U", "73600D",
                    null),

    SST_D_CRONOGRAMA("SubcronogramasstsControlador", "SST_D_CRONOGRAMA",
                    "73800C", null, "73800U", "73800D", "73800G"),

    NAT_ENFERMEDAD_PERSONAL("ExamenMedicosControlador",
                    "NAT_ENFERMEDAD_PERSONAL", "73700C", "73700R", "73700U",
                    "73700D", "73700G"),

    SST_TRANSACCIONES("FrmtransaccionessstsControlador", "SST_TRANSACCIONES",
                    "73900C", "73900R", "73900U", "73900D",
                    "73900G"),

    SST_ELEMENTO_PROTECCION("FrmelementosprotpersonalsControlador",
                    "SST_ELEMENTO_PROTECCION", "73000C", null, "73000U",
                    "73000D", "73000G"),

    NAT_PLANEACIONACTIVIDADES("PlaneacionActividadSstsControlador",
                    "NAT_PLANEACIONACTIVIDADES", "74100C", "74100R",
                    "74100U", "74100D", "74100G"),

    SST_D_TRANSACCIONES("FrmsubtransaccionessstsControlador",
                    "SST_D_TRANSACCIONES", "74200C", null, "74200U", "74200D",
                    "74200G"),

    NAT_COMISION("NatsubcomisionsControlador", "NAT_COMISION", "74300C",
                    "74300R", "74300U", "74300D", "74300G"),

    CESANTIAS("PersonalcesantiasControlador", "CESANTIAS", "74400C", null,
                    "74400U", "74400D", "74400G"),

    ALMACENCONTABILIDADCC("CAlmacenContabilidadTraCcControlador",
                    "ALMACENCONTABILIDADCC", "74500C", null, "74500U",
                    "74500D", null),

    SERVICIO_ASOCIADO("ServicioAsociadoControlador", "SERVICIO_ASOCIADO",
                    "74600C", null, "74600U", "74600D", "74600G"),

    CUENTAS_CONCEPTOS_FACT_CNT("RevisainterfacefactsControlador",
                    "CUENTAS_CONCEPTOS_FACT_CNT", "74700C", null,
                    "74700U", "74700D", "74700G"),

    AUTOSER_CONSULTAS("FrmConsultasControlador", "AUTOSER_CONSULTAS", null,
                    null, null, null, "75100G"),

    EV_CRITERIOS_EVALUACION("FrmCriteriosEvaluacionControlador",
                    "EV_CRITERIOS_EVALUACION", "75200C", null, "75200U",
                    "75200D", "75200G"),

    EV_MANUAL("FrmevmanualsControlador", "EV_MANUAL", "75300C", "75300R",
                    "75300U", "75300D", "75300G"),

    EV_TIPO_REQUISITO("frmtiporequisitoControlador", "EV_TIPO_REQUISITO",
                    "75400C", null, "75400U", "75400D", "75400G"),

    EV_FUNCIONES("FrmevfuncionesControlador", "EV_FUNCIONES", "75700C",
                    "75700R", "75700U", "75700D", "75700G"),

    EV_REQUISITOS("frmevrequisitosControlador", "EV_REQUISITOS", "75800C",
                    "75800R", "75800U", "75800D", "75800G"),

    NAT_ACTIVIDADESPROGRAMADAS("FrmProgramacionActividadesSSTControlador",
                    "NAT_ACTIVIDADESPROGRAMADAS", "74000C",
                    "74000R", "74000U", "74000D", "74000G"),

    EV_MOTIVO_MEJORAMIENTO("FrmmotivomejorasControlador",
                    "EV_MOTIVO_MEJORAMIENTO", "75900C", null, "75900U",
                    "75900D",
                    "75900G"),

    EV_TIPO_COMPETENCIAS("FrmtipocompetenciasControlador",
                    "EV_TIPO_COMPETENCIAS", "75500C", null, "75500U", "75500D",
                    "75500G"),

    EV_TIPO_FUNCION("FrmevtipofuncionesControlador", "EV_TIPO_FUNCION",
                    "75600C", null, "75600U", "75600D", "75600G"),

    VI_LEGALIZACION_VIATICOS("LegalizacionViaticosControlador",
                    "VI_LEGALIZACION_VIATICOS", "76000C", "76000R",
                    "76000U", "76000D", "76000G"),

    VI_AREAMISIONAL("AreasmisionalesControlador", "VI_AREAMISIONAL", "76300C",
                    null, "76300U", "76300D", "76300G"),

    VI_DETALLE_LEGALIZA_VIATICOS("SubLegalizacionViaticosControlador",
                    "VI_DETALLE_LEGALIZA_VIATICOS", null, null,
                    "76500U", null, "76500G"),

    VI_TIPOSOLICITUD("TipoSolicitudControlador", "VI_TIPOSOLICITUD", "76800C",
                    null, "76800U", "76800D", "76800G"),

    TASADECAMBIO("SubtasadecambiosControlador", "TASADECAMBIO", "76700C", null,
                    "76700U", "76700D", "76700G"),

    VI_CONCEPTO_VIATICOS("FrmconceptoviaticosControlador",
                    "VI_CONCEPTO_VIATICOS", "76600C", "76600R", "76600U",
                    "76600D", "76600G"),

    VI_TIPOLUGAR("TipoLugarControlador", "VI_TIPOLUGAR", "77000C", null,
                    "77000U", "77000D", "77000G"),

    VI_DETALLE_VIATICOS("SfviaticosControlador", "VI_DETALLE_VIATICOS",
                    "76400C", null, "76400U", "76400D", "76400G"),

    VI_TIPOVISITA("TipovisitasControlador", "VI_TIPOVISITA", "76900C", null,
                    "76900U", "76900D", "76900G"),

    VI_VIATICOS("FrmViaticosControlador", "VI_VIATICOS", "76100C", "76100R",
                    "76100U", "76100D", "76100G"),

    SALARIOS_MINIMOS("SalarioMinimoViaticosControlador", "SALARIOS_MINIMOS",
                    "93000C", null, "93000U", "93000D",
                    "93000G"),

    EV_META_DEPENDENCIAS("frmmetadependenciaControlador",
                    "EV_META_DEPENDENCIAS", "77200C", null, "77200U", "77200D",
                    "77200G"),

    EV_TIPO_DE_EVALUACION("FrmtipoevaluacionsControlador",
                    "EV_TIPO_DE_EVALUACION", "93400C", null, "93400U", "93400D",
                    "93400G"),

    EV_CALIFICACION_COMPETENCIA("FrmcalificacioncompetenciasControlador",
                    "EV_CALIFICACION_COMPETENCIA", "93300C", null,
                    "93300U", "93300D", "93300G"),

    MONEDA("MonedasControlador", "MONEDA", "6600C", "6600R", "6600U", "6600D",
                    "6600G"),

    EV_COMPETENCIAS("FrmevcompetenciasControlador", "EV_COMPETENCIAS", "77300C",
                    "77300R", "77300U", "77300D",
                    "77300G"),

    VI_DETALLE_CATEGORIA_CONCEPTO("FrmCategoriasConceptosControlador",
                    "VI_DETALLE_CATEGORIA_CONCEPTO", "93100C", null,
                    "93100U", "93100D", "93100G"),

    NAT_QUINQUENIO("NatsubquinqueniosControlador", "NAT_QUINQUENIO", "93700C",
                    null, "93700U", "93700D", "93700G"),

    AUT_PERSONAL("frm_actdatospersonalsControlador", "AUT_PERSONAL", "93500C",
                    "93500R", "93500U", "93500D", "93500G"),

    EV_EVALUADOR_EVALUADO("FrmevaluadorevaluadosControlador",
                    "EV_EVALUADOR_EVALUADO", "93900C", "93900R", "93900U",
                    "93900D", "93900G"),

    NAT_APERTURA("AperturasControlador", "NAT_APERTURA", "70800C", "70800R",
                    "70800U", "70800D", "70800G"),

    NAT_RELACION_DIARIOS("RelaciondiariosControlador", "NAT_RELACION_DIARIOS",
                    "94100C", "94100R", "94100U", "94100D",
                    "94100G"),

    EV_APORTADO_POR("FrmaportadoporsControlador", "EV_APORTADO_POR", "94000C",
                    null, "94000U", "94000D", "94000G"),

    NAT_RELACION_PRUEBAS("RelacionpruebasControlador", "NAT_RELACION_PRUEBAS",
                    "94200C", null, "94200U", "94200D",
                    "94200G"),

    EV_GRUPO("frmgruposControlador", "EV_GRUPO", "94300C", null, "94300U",
                    "94300D", "94300G"),

    NAT_COMITE_SELECCION("ComiteseleccionsControlador", "NAT_COMITE_SELECCION",
                    "94600C", null, "94600U", "94600D",
                    "94600G"),

    EV_CRITERIO_GRUPO("frmgrupocriterio", "EV_CRITERIO_GRUPO", "94500C", null,
                    "94500U", "94500D", "94500G"),

    EV_EVALUACIONES("FrmEvaluacionesControlador", "EV_EVALUACIONES", "94700C",
                    "94700R", "94700U", "94700D", "94700G"),

    AUT_FAMILIARES("frmactdatospersonalsControlador", "AUT_FAMILIARES",
                    "94800C", "94800R", "94800U", "94800D",
                    "94800G"),

    NAT_RIESGO_LABORAL("FactoresRiesgoControlador", "NAT_RIESGO_LABORAL",
                    "73200C", "73200R", "73200U", "73200D",
                    "73200G"),

    NAT_CONCEPTO_RIESGO("FactoresRiesgoControlador", "NAT_CONCEPTO_RIESGO",
                    "73300C", null, "73300U", "73300D",
                    "73300G"),

    NAT_APERTURA_INSCRITOS("frmgrupocriterio", "NAT_APERTURA_INSCRITOS",
                    "72200C", null, "72200U", "72200D", "72200G"),

    AUT_TERCERO("frm_actdatospersonalsControlador", "AUT_TERCERO", null, null,
                    null, null, "95100G"),

    TIPO_SERV_MEDICO("TiposervmedicosControlador", "TIPO_SERV_MEDICO", "95200C",
                    null, "95200U", "95200D", "95200G"),

    NAT_TIPO_PREMIO("frmtipopremiosControlador", "NAT_TIPO_PREMIO", "77400C",
                    null, "77400U", "77400D", "77400G"),

    NAT_ENTIDADESCAPACITACION("EntidadesCapacitacionControlador",
                    "NAT_ENTIDADESCAPACITACION", "75000C", "75000R",
                    "75000U", "75000D", "75000G"),

    EV_COMPETENCIAS_EMPLEADO("FrmevcompetenciasempleadosControlador",
                    "EV_COMPETENCIAS_EMPLEADO", "94900C", "94900R",
                    "94900U", "94900D", "94900G"),

    NAT_CURSOS_CARRERAS("CursosCarrerasControlador", "NAT_CURSOS_CARRERAS",
                    "95500C", "95500R", "95500U", "95500D",
                    "95500G"),

    TIPO_DOCUMENTO_ANEXO("TipoDocumentoAnexosControlador",
                    "TIPO_DOCUMENTO_ANEXO", "95600C", null, "95600U", "95600D",
                    "95600G"),

    NAT_ACTIVIDADESINSCRITOS("ActividadesinscritosControlador",
                    "NAT_ACTIVIDADESINSCRITOS", "77500C", null, "77500U",
                    "77500D", "77500G"),

    SST_DOCUMENTO_ANEXO("DocumentoAnexosControlador", "SST_DOCUMENTO_ANEXO",
                    "95700C", null, "95700U", "95700D",
                    "95700G"),

    NAT_SOLICITUD_FINANCIAMIENTO_A("SolicitudesfinanciamientosControlador",
                    "NAT_SOLICITUD_FINANCIAMIENTO_A", "95800C",
                    "95800R", "95800U", "95800D", "95800G"),

    NAT_REUNION_COMISIONPERSONAL("RepresentantereunioncomisionsControlador",
                    "NAT_REUNION_COMISIONPERSONAL", "95900C",
                    "95900R", "95900U", "95900D", "95900G"),

    PROCESOS("FrmWFProcesosControlador", "PROCESOS", "98800C", "98800R",
                    "98800U", "98800D", "98800G"),

    NAT_ASISTENTESCOMITE("AdreunioncomitepersonalsControlador",
                    "NAT_ASISTENTESCOMITE", "96100C", null, null, "96100D",
                    "96100G"), NAT_COTIZACIONESACTIVIDADES(
                                    "CotizacionesActividadesControlador",
                                    "NAT_COTIZACIONESACTIVIDADES",
                                    "74900C", "74900R", "74900U", "74900D",
                                    "74900G"),

    NAT_PREMIACION("FrmpremiacionsControlador", "NAT_PREMIACION", "96000C",
                    null, "96000U", "96000D", "96000G"),

    NAT_JURIDENTIFICACION("JuridentificacionsControlador",
                    "NAT_JURIDENTIFICACION", "99200C", "99200R", "99200U",
                    "99200D", "99200G"),

    SST_DETALLE_DOCUMENTO_ANEXO("DetalleDocumentoAnexosControlador",
                    "SST_DETALLE_DOCUMENTO_ANEXO", "99400C", null,
                    "99400U", "99400D", "99400G"),

    NAT_JURSERVICIOS("ServiciospersonajuridicasControlador", "NAT_JURSERVICIOS",
                    "99500C", null, "99500U", "99500D",
                    "99500G"),

    TIPOTRAMITES("FrmTipoTramitesControlador", "TIPOTRAMITES", "99700C", null,
                    "99700U", "99700D", "99700G"),

    EV_DETALLE_EVALUACION("FrmEvaluacionesDetControlador",
                    "EV_DETALLE_EVALUACION", "99900C", "99900R", "99900U",
                    "99900D", "99900G"),

    EV_ACCIONES_MEJORA("FrmevaccionesmejorasControlador", "EV_ACCIONES_MEJORA",
                    "100000C", "100000R", "100000U",
                    "100000D", "100000G"),

    PROYECTOSNOM("FrmproyectosnominasControlador", "PROYECTOSNOM", "100200C",
                    null, "100200U", "100200D", "100200G"),

    EV_EVIDENCIAS("FrmevevidenciasControlador", "EV_EVIDENCIAS", "100300C",
                    "100300R", "100300U", "100300D", "100300G"),

    EV_ACCIONES_MEJORA_COMPORTAMEN(
                    "FrmevaccionesmejorascomportamentalesControlador",
                    "EV_ACCIONES_MEJORA_COMPORTAMEN",
                    "100400C", "100400R", "100400U", "100400D", "100400G"),

    NAT_JUREXPERIENCIA("JurexpysituaactualsControlador", "NAT_JUREXPERIENCIA",
                    "100500C", null, "100500U", "100500D",
                    "100500G"),

    EV_COMPROMISOS_LABORALES("FrmcompromisoslaboralesControlador",
                    "EV_COMPROMISOS_LABORALES", "98500C", "98500R",
                    "98500U", "98500D", "98500G"),

    EV_OBJETIVOS_INSTITUCIONALES("FrmcompromisoslaboralesControlador",
                    "EV_OBJETIVOS_INSTITUCIONALES", "100700C", null,
                    "100700U", "100700D", "100700G"),

    AUT_SOLICITUDES("SolicitudesAutsControlador", "AUT_SOLICITUDES", "100600C",
                    "100600R", "100600U", "100600D",
                    "100600G"),

    EV_VALORACIONES("FrmEvaluacionesControlador", "EV_VALORACION", "101100C",
                    null, "101100U", "101100D", "101100G"),

    PI_INDICADOR("FrmpiindicadorsControlador", "PI_INDICADOR", "56600C", null,
                    "56600U", "56600D", "56600G"),

    BP_EQUIVALENCIAS("FrmequivalenciasContolador", "BP_EQUIVALENCIAS",
                    "101400C", null, "101400U", "101400D",
                    "101400G"),

    PI_TIPO_TRANSACCION("FrmpitipotransaccionsControlador",
                    "PI_TIPO_TRANSACCION", "101500C", null, "101500U",
                    "101500D", "101500G"),

    PI_RELACION_PROD_RTDO("PiRelacionProdRtdosControlador",
                    "PI_RELACION_PROD_RTDO", "102000C", null, "102000U",
                    "102000D", "102000G"),

    PI_TRANSACCION("TransPlanDesarrolloControlador", "PI_TRANSACCION", null,
                    null, null, null, "102300G"),

    BP_TIPORECURSOS("FrmtiporecursosControlador", "BP_TIPORECURSOS", "102500C",
                    null, "102500U", "102500D", "102500G"),

    BP_TIPOSIRECI_INF("FrmtiposirecisControlador", "BP_TIPOSIRECI_INF",
                    "102600C", null, "102600U", "102600D",
                    "102600G"),

    BP_TIPOSIRECI_APSB("FrmtiposirecisControlador", "BP_TIPOSIRECI_APSB",
                    "102700C", null, "102700U", "102700D",
                    "102700G"),

    PI_INDICADOR_META_TRA("PlanindicativosControlador", "PI_INDICADOR_META_TRA",
                    null, null, "102800U", null,
                    "102800G"),

    BP_ARMONIZACIONPD("FrmarmonizacionpdsControlador", "BP_ARMONIZACIONPD",
                    "103000C", null, "103000U", "103000D",
                    "103000G"),

    NAT_REPRESENTANTE("RepresentanteComisionPersonalControlador",
                    "NAT_REPRESENTANTE", "98600C", "98600R", "98600U",
                    "98600D", "98600G"),

    D_SOLICITUDDISPONIBILIDAD("SolicitudDisponibilidadControlador",
                    "D_SOLICITUDDISPONIBILIDAD", "102900C", null,
                    "102900U", "102900D", "102900G"),

    NODOS("FrmNodosControlador", "NODOS", "103500C", "103500R", "103500U",
                    "103500D", "103500G"),

    NODO_VARIABLES("FrmNodoVariablesControlador", "NODO_VARIABLES", "103700C",
                    "103700R", "103700U", "103700D",
                    "103700G"),

    SERIEDOCUMENTAL("FrmseriedocumentalsControlador", "SERIEDOCUMENTAL",
                    "103600C", "103600R", "103600U", "103600D",
                    "103600G"),

    ROLES("FrmrolesControlador", "ROLES", "77700C", null, "77700U", "77700D",
                    "77700G"),

    D_NODOS("FrmDNodosControlador", "D_NODOS", "103800C", null, "103800U",
                    "103800D", "103800G"),

    TRAMITES("FrmTramitesControlador", "TRAMITES", "104200C", "104200R",
                    "104200U", "104200D", "104200G"),

    ROL_USUARIO("FrmRolesControlador", "ROL_USUARIO", "77800C", null, "77800U",
                    "77800D", "77800G"),

    TIPOS("TiposControlador", "TIPOS", "103200C", null, "103200U", "103200D",
                    "103200G"),

    PROCEDENCIA_TRAMITE("FrmprocedenciatramitesControlador",
                    "PROCEDENCIA_TRAMITE", "104000C", "104000R", "104000U",
                    "104000D", "104000G"),

    TIPO_CATEGORIA("TipoCategoriasControlador", "TIPO_CATEGORIA", "104400C",
                    null, "104400U", "104400D", "104400G"),

    D_TRAMITES("", "D_TRAMITES", null, null, null, null, null),

    VARIABLES_DE_TRAMITES("FrmvariablesdetramitesControlador",
                    "VARIABLES_DE_TRAMITES", "104600C", null, "104600U",
                    "104600D", "104600G"),

    NODO_RACI("FrmNodoRaciControlador", "NODO_RACI", "104700C", null, "104700U",
                    "104700D", "104700G"),

    PI_PLAN_INDICATIVO("PlanDeAccionControlador", "PI_PLAN_INDICATIVO",
                    "102200C", null, "102200U", null, "102200G"),

    PI_PLAN_INDICATIVO_FUENTES("PlanDeAccionControlador",
                    "PI_PLAN_INDICATIVO_FUENTES", "104300C", null, "104300U",
                    "104300D", "104300G"),

    D_TRAMITE_VARIABLES("DTramiteVariablesControlador", "D_TRAMITE_VARIABLES",
                    null, null, "104800U", null, "104800G"),

    SST_TRANSACCION_ACTIVIDAD("TransaccionactividadsControlador",
                    "SST_TRANSACCION_ACTIVIDAD", "104900C", null,
                    "104900U", "104900D", "104900G"),

    SST_TESTIGOS("TestigosControlador", "SST_TESTIGOS", "105000C", null,
                    "105000U", "105000D", "105000G"),

    ACCESOS("AccesosControlador", "ACCESOS", "105200C", null, "105200U",
                    "105200D", "105200G"),

    ALERTAS("FalertasControlador", "ALERTAS", "89900C", null, "89900U",
                    "89900D", "89900G"),

    EXCEPCIONES("ExcepcionesControlador", "EXCEPCIONES", "105300C", null,
                    "105300U", "105300D", "105300G"),

    CONSULTAS_RP("ConsultasControlador", "CONSULTAS_RP", "105400C", "105400R",
                    "105400U", "105400D", "105400G"),

    D_CONSULTAS("ConsultasControlador", "D_CONSULTAS", "105700C", null,
                    "105700U", "105700D", "105700G"),

    D_PARAMETROS("ConsultasControlador", "D_PARAMETROS", "105800C", null,
                    "105800U", "105800D", "105800G"),

    IMAGEN_ELEMENTOS("ImagenesElementoControlador", "IMAGEN_ELEMENTOS",
                    "105900C", "105900R", "105900U", "105900D",
                    "105900G"),

    REPORTES("ReportesControlador", "REPORTES", "105500C", "105500R", "105500U",
                    "105500D", "105500G"),

    D_DEPENDENCIA_RESPONSABLE("FrmMaximoRequisicionControlador",
                    "D_DEPENDENCIA_RESPONSABLE", "106000C", "106000R",
                    "106000U", "106000D", "106000G"),

    CAMBIOSDEPLACA("CambiarcodigoelementosControlador", "CAMBIOSDEPLACA",
                    "166100C", "166100R", "166100U", "166100D",
                    "166100G"),

    D_CAMBIOSDEPLACA("CambiarcodigoelementosControlador", "D_CAMBIOSDEPLACA",
                    "166200C", null, "166200U", "166200D",
                    "166200G"),

    EMAIL_PLANTILLA("EmailPlantillasControlador", "EMAIL_PLANTILLA", "166300C",
                    "166300R", "166300U", "166300D",
                    "166300G"),

    EMAIL_DESTINO("EmailPlantillasControlador", "EMAIL_DESTINO", "166400C",
                    null, "166400U", "166400D", "166400G"),

    TABLAS_AUDITAR("TablasauditarsControlador", "TABLAS_AUDITAR", null, null,
                    "166500U", null, "166500G"),

    COLUMNAS_AUDITAR("ColumnasAuditarsControlador", "COLUMNAS_AUDITAR", null,
                    null, "166600U", null, "166600G"),

    LOG_MENUS("LogMenusControlador", "LOG_MENUS", null, null, null, null,
                    "166800G"),

    LOG_ACCESO("LogAccesosControlador", "LOG_ACCESO", null, null, null, null,
                    "166700G"),

    LOG_AUDITORIA("LogauditoriasControlador", "LOG_AUDITORIA", null, null, null,
                    null, "166900G"),

    D_PARAMETROSCONSULTAS("ConsultasControlador", "D_PARAMETROSCONSULTAS",
                    "167000C", null, "167000U", "167000D",
                    "167000G"),

    SST_DOCUMENTO_PLANEACION("FrmDetalleAnexoControlador",
                    "SST_DOCUMENTO_PLANEACION", "167400C", null, "167400U",
                    "167400D", "167400G"),

    EQUIVALENTECNT_PRESUPUESTAL("EquivalenteCntPresupuestalControlador",
                    "EQUIVALENTECNT_PRESUPUESTAL", "167500C", null,
                    "167500U", "167500D", "167500G"),

    D_PARAMETROSIMP("ConfiguracionParametrosControlador", "D_PARAMETROSIMP",
                    null, "167300R", "167300U", null,
                    "167300G"),

    TERCERO_RETENCIONES("RetencionesPorTerceroControlador",
                    "TERCERO_RETENCIONES", "167100C", null, "167100U",
                    "167100D", "167100G"),

    SST_DOCUMENTO_INSCRITO("FrmDetalleDocumentoInscrito",
                    "SST_DOCUMENTO_INSCRITO", "167600C", null, "167600U",
                    "167600D", "167600G"),

    SST_CLASE_AGENTE("ClaseAgentesControlador", "SST_CLASE_AGENTE", "167700C",
                    null, "167700U", "167700D", "167700G"),

    SST_AGENTES("AgentesControlador", "SST_AGENTES", "167800C", null, "167800U",
                    "167800D", "167800G"),

    SST_DOCUMENTO_BIENESTARYCAP("FrmDetalleDocumentoBienestaryCap",
                    "SST_DOCUMENTO_BIENESTARYCAP", "167900C", null,
                    "167900U", "167900D", "167900G"),

    CORREOS_SALIDA("FcorreossalidasControlador", "CORREOS_SALIDA", "98000C",
                    null, "98000U", "98000D", "98000G"),

    INSTITUCIONES_EDUCATIVAS("InstitucionesEducativasControlador",
                    "INSTITUCIONES_EDUCATIVAS", "168200C", null,
                    "168200U", "168200D", "168200G"),

    D_VACACIONES("SubformdetallevacacionesControlador", "D_VACACIONES", null,
                    null, "167200U", null, "167200G"),

    DOCUMENTOS_REQUERIDOS("DocumentosRequeridosControlador",
                    "DOCUMENTOS_REQUERIDOS", "168000C", null, "168000U",
                    "168000D", "168000G"),

    DOCUMENTOS_PRESENTADOS("RequisitosposesionsControlador",
                    "DOCUMENTOS_PRESENTADOS", "168100C", null, "168100U", null,
                    "168100G"),

    PI_PLAN_INDICATIVO_OBS("FrmPlanIndicativoIndiControlador",
                    "PI_PLAN_INDICATIVO_OBS", "168300C", null, "168300U",
                    "168300D", null),

    PLAN_CIRCULAR_UNICA("plancircularunicasControlador", "PLAN_CIRCULAR_UNICA",
                    "168400C", null, "168400U", "168400D",
                    "168400G"),

    DESICIONRECURSO("DesicionRecusoControlador", "DESICIONRECURSO", "168500C",
                    "168500R", "168500U", "168500D", null),

    ACUERDOS_COMERCIALES("FrmacuerdoscomercialesControlador",
                    "ACUERDOS_COMERCIALES", "168800C", null, "168800U",
                    "168800D", "168800G"),

    EV_DETALLE_COMPACORDADOS("CompromisosAcordadosControlador",
                    "EV_DETALLE_COMPACORDADOS", null, null, "168900U",
                    "168900D", "168900G"),

    SST_DOCUMENTO_LISTELEGIBLES("FrmDetalleListadoElegiblesControlador",
                    "SST_DOCUMENTO_LISTELEGIBLES", "169600C", null,
                    "169600U", "169600D", "169600G"),

    EV_RECLAMACIONOBJECION("ReclamacionesControlador", "EV_RECLAMACIONOBJECION",
                    "169800C", "169800R", "169800U",
                    "169800D", "169800G"),

    ACUERDOS_COMERCIALES_P3("FrmacuerdoscomercialesControlador",
                    "ACUERDOS_COMERCIALES_P3", "170100C", null, "170100U",
                    "170100D", "170100G"),

    PLAN_SIA("PlansiasControlador", "TIPOCAMPO_SIA", "170000C", null, "170000U",
                    "170000D", "170000G"),

    D_PARAMETROS_PLANOS("PlansiasControlador", "D_PARAMETROS_PLANOS", "169500C",
                    null, "169500U", "169500D", "169500G"),

    PLAN_PPTAL_CONFIG("ConfigurarplanpptalsControlador", "PLAN_PPTAL_CONFIG",
                    "103100C", null, "103100U", "103100D",
                    "103100G"),

    PROYECCIONES_TRAMITE("FrmProyeccionesTramitesControlador",
                    "PROYECCIONES_TRAMITE", null, null, "6500U", null,
                    "6500G"),

    TIPO_COBERTURA("UnidadmedidacoberturasControlador", "TIPO_COBERTURA",
                    "170300C", null, "170300U", "170300D",
                    "170300G"),

    ES_PROGPAGOS_ESTPREVIO("FormadepagosControlador", "ES_PROGPAGOS_ESTPREVIO",
                    "170400C", null, "170400U", null,
                    "170400G"),

    ES_FUENTE_ETAPA_TIPO("FrmEsFuente", "ES_FUENTE_ETAPA_TIPO", "170500C", null,
                    "170500U", "170500D", "170500G"),

    ES_PROBABILIDAD_VALORACION("FrmProbabilidadValoracionControlador",
                    "ES_PROBABILIDAD_VALORACION", "170600C", null,
                    "170600U", "170600D", "170600G"),

    ES_IMPACTO("FrmEsImpacto", "ES_IMPACTO", "170700C", null, "170700U",
                    "170700D", "170700G"),

    SF_CONTRATOS("FrmcontratosControlador", "SF_CONTRATOS", "66400C", "66400R",
                    "66400U", "66400D", "66400G"),

    VI_RANGOS_VIATICOS("RangoViaticosControlador", "VI_RANGOS_VIATICOS",
                    "170800C", null, "170800U", "170800D",
                    "170800G"),

    PI_PLAN_INDICATIVO_METAS("PlanDeMetasControlador",
                    "PI_PLAN_INDICATIVO_METAS", null, null, null, null,
                    "170200G"),

    SF_DETALLE_CONTRATO("FrmcontratosControlador", "SF_DETALLE_CONTRATO",
                    "68200C", null, "68200U", "68200D", "68200G"),

    TERCEROOPS("TercerosControlador", "TERCERO_OPS", "170900C", null, "170900U",
                    "170900D", "170900G"),

    URLSERVICIO("FrmURLServiciosControlador", "URLSERVICIO", null, null,
                    "171000U", null, "171000G"),

    SF_TARIFAS_BASE("FrmTarifasBaseControlador", "SF_TARIFAS_BASE", "171100C",
                    null, "171100U", "171100D", "171100G"),

    SF_DESCRIPCIONES_ESTAND("FrmDescripcionesControlador",
                    "SF_DESCRIPCIONES_ESTAND", "171200C", null, "171200U",
                    "171200D", "171200G"),

    PI_CODIGOS_BPIN("CodigosBpinsControlador", "PI_CODIGOS_BPIN", "171300C",
                    null, "171300U", "171300D", "171300G"),

    PI_PROGRAMACION_FISICA("FrmprogramacionfisicasControlador",
                    "PI_PROGRAMACION_FISICA", "171600C", null, "171600U",
                    "171600D", "171600G"),

    ELEMENTOSCOMPONENTES("EstructuraComponenteControlador",
                    "ELEMENTOSCOMPONENTES", "171400C", null, "171400U",
                    "171400D", "171400G"),

    DETALLE_ELEMENTOSCOMPONENTES("EstructuraComponenteControlador",
                    "DETALLE_ELEMENTOSCOMPONENTES", "171500C", null,
                    "171500U", "171500D", "171500G"),

    ES_DISP_MANUAL("RubrosdisponibilidadmanualsControlador", "ES_DISP_MANUAL",
                    "171700C", null, "171700U", "171700D",
                    "171700G"),

    TIPOCOMPONENTE("TipoComponenteControlador", "TIPOCOMPONENTE", "171800C",
                    null, "171800U", "171800D", "171800G"),

    TRANSACCIONMODELO("InformeComponentesControlador", "TRANSACCIONMODELO",
                    "172000C", "172000R", "172000U", "172000D",
                    "172000G"),

    D_TRANSACCIONMODELO("DTransaccionesModeloControlador",
                    "D_TRANSACCIONMODELO", "172100C", "172100R", "172100U",
                    "172100D", "172100G"),

    TRANSACCIONES("DatostransaccionsControlador", "TRANSACCIONES", "172300C",
                    "172300R", "172300U", "172300D",
                    "172300G"),

    BASESNOVEDADES("BasesNovedadesControlador", "BASESNOVEDADES", null, null,
                    "172200U", "172200D", "172200G"),

    EP_DETALLE_ACTIVIDADES("DetalleactividadesControlador",
                    "EP_DETALLE_ACTIVIDADES", "172400C", null, "172400U",
                    "172400D", "172400G"),

    VI_CLASE_TRANSPORTE("FrmTipoTransportesControlador", "VI_CLASE_TRANSPORTE",
                    "172500C", null, "172500U", "172500D",
                    "172500G"),

    CUMPLIMIENTO_ACTIVIDADES("CumplimientoactividadesControlador",
                    "ACTIVIDADES_NOVEDAD", null, null, "172600U",
                    "172600D", "172600G"),

    D_TRANSACCIONES("DtransaccionesControlador", "D_TRANSACCIONES", "172700C",
                    "172700R", "172700U", "172700D",
                    "172700G"),

    SST_GRUPO("FrmSstGruposControlador", "SST_GRUPO", "172800C", null,
                    "172800U", "172800D", "172800G"),

    SST_FACTOR("FrmFactoresControlador", "SST_FACTOR", "172900C", null,
                    "172900U", "172900D", "172900G"),

    SST_MATRIZ_RIESGOS("FrmMatrizRiesgosControlador", "SST_MATRIZ_RIESGOS",
                    "173100C", "173100R", "173100U", "173100D",
                    "173100G"),

    NIIF_INVENTARIOCONTA("CAlmacenContabilidadDepNiidCc",
                    "NIIF_INVENTARIOCONTA", "173000C", null, "173000U",
                    "173000D",
                    null),

    TIPOTRANSACCION("FrmtipotransaccionsControlador", "TIPOTRANSACCION",
                    "171900C", "171900R", "171900U", "171900D",
                    "171900G"),

    SST_DETALLE_MATRIZ_RIESGOS("FrmSubMatrizRiesgosControlador",
                    "SST_DETALLE_MATRIZ_RIESGOS", "173300C", null,
                    "173300U", "173300D", "173300G"),

    TRANSACCIONMODELO_CC("cCostoTransaccionModelosControlador",
                    "TRANSACCIONMODELO_CC", "173400C", null, "173400U",
                    "173400D", "173400G"),

    SST_PROCESOS("FrmProcesosRiesgosControlador", "SST_PROCESOS", "173600C",
                    null, "173600U", "173600D", "173600G"),

    IG_INDICADORES_BASICOS("FrmindbasicosControlador", "IG_INDICADORES_BASICOS",
                    "174100C", null, "174100U", "174100D",
                    "174100G"),

    ESTPREVIO_PLANACCION("FrmplanaccionestpreviosControlador",
                    "ESTPREVIO_PLANACCION", "174000C", "174000R", null,
                    "174000D", "174000G"),

    SEGUIMIENTO_RECIPROCAS("SeguimientoReciprocasControlador",
                    "SEGUIMIENTO_RECIPROCAS", "174200C", null, "174200U",
                    "174200D", "174200G"),

    DOCUMENTO_CORRECCIONVALOR("documentosCorreccionValorControlador",
                    "DOCUMENTO_CORRECCIONVALOR", "174500C", null,
                    "174500U", "174500D", "174500G"),

    IG_DETALLE_INDICADORES_BASICOS("MonitorinControlador",
                    "IG_DETALLE_INDICADORES_BASICOS", null, null, null,
                    "174600D", "174600G"),

    REEXPRESARVIDAUTIL("FrmReexpresarVidaUtilControlador", "REEXPRESARVIDAUTIL",
                    "174900C", null, "174900U", "174900D",
                    "174900G"),

    FUENTESFUT("FuentesFutsControlador", "FUENTESFUT", "169000C", null,
                    "169000U", "169000D", "169000G"),

    RETENCIONES_REGIMEN("ConfiguracionRetencionesControlador",
                    "RETENCIONES_REGIMEN", "175300C", null, "175300U",
                    "175300D", "175300G"),

    RETENCIONCONCEPTO("ConceptosRetencionsControlador", "RETENCIONCONCEPTO",
                    "175400C", null, "175400U", "175400D",
                    "175400G"),

    CONFIGURACION_EXOGENA("ConfigurarPlanContableExsControlador",
                    "CONFIGURACION_EXOGENA", "175600C", null, "175600U",
                    "175600D", null),

    RETENCIONES_CIIU("RetencionesciiusControlador", "RETENCIONES_CIIU",
                    "175800C", null, "175800U", "175800D",
                    "175800G"),

    PREGUNTAS_NOVEDAD("FrmSolicitudCdpControlador", "PREGUNTAS_NOVEDAD",
                    "175900C", "175900R", "175900U", "175900D",
                    "175900G"),

    FORMATOS("FormatosControlador", "FORMATOS", "175700C", null, "175700U",
                    "175700D", "175700G"),

    CONCEPTOSEX("ConfigurarConceptosControlador", "CONCEPTOSEX", "4900C", null,
                    "4900U", "4900D", "4900G"),

    PERTINENCIA_NOVEDAD("PertinenciaNovedadControlador",
                    "BP_PERTINENCIA_NOVEDAD", "176200C", "176200R", "176200U",
                    null, null),

    PLAN_PPTAL_CTABANCARIA("PlanpresupuestalptosControlador",
                    "PLAN_PPTAL_CTABANCARIA", "176300C", null, "176300U",
                    "176300D", "176300G"),

    PLAN_SCHIP("CodigosfutsControlador", "PLAN_SCHIP", "51400C", null, "51400U",
                    "51400D", "51400G"),

    VI_DOCUMENTO_ANEXO("AnexoDocumentoViaticos", "VI_DOCUMENTO_ANEXO",
                    "176400C", null, "176400U", "176400D",
                    "176400G"),

    VP_PROYECTOS("ValorizacionProyectosControlador", "VP_PROYECTOS", "176700C",
                    "176700R", "176700U", "176700D",
                    "176700G"),

    VP_BENEFICIARIOS("PlusvaliaBeneficiariosControlador", "VP_BENEFICIARIOS",
                    "176800C", "176800R", "176800U",
                    "176800D", "176800G"),

    VP_CONCEPTOS("PlusvaliaConceptosControlador",
                    "VP_CONCEPTOS", "176900C", "176900R", "176900U", "176900D",
                    "176900G"),

    CONCEPTOSANO("ConceptosDiansControlador", "CONCEPTOS_ANO", "177000C", null,
                    "177000U",
                    "177000D", "177000G"),

    VP_HECHOS_PROYECTOS("PlusvaliaHechoGeneradorControlador",
                    "VP_HECHOS_PROYECTOS", "177600C", "177600R", "177600U",
                    "177600D",
                    "177600G"),

    VP_HECHOS_BENEFICIARIOS("PlusvaliaHechoGeneradorControlador",
                    "VP_HECHOS_BENEFICIARIOS", "177700C", "177700R", "177700U",
                    "177700D",
                    "177700G"),

    VP_FACTORES("ValorizacionFactoresControlador",
                    "VP_FACTORES", "178000C", "178000R", "178000U",
                    "178000D",
                    "178000G"),

    PAGOESPECIAL("PagosespecialesControlador", "PAGOESPECIAL", "178100C",
                    "178100R",
                    "178100U", "178100D", "178100G"),

    CLASEFONDOAPORTE("FrmConfAportesSindicatosControlador", "CLASEFONDOAPORTE",
                    "178600C", null,
                    "178600U", "178600D", "178600G"),

    PERSONAL_APORTE("FrmAsigAportesSindicatoEmpleadosControlador",
                    "PERSONAL_APORTE",
                    "178700C", null,
                    "178700U", "178700D", "178700G"),

    CONCEPTOS_BASE_APORTE("FrmConceptosBaseAportesControlador",
                    "CONCEPTOS_BASE_APORTE", "179200C", null, "179200U",
                    "179200D", "179200G"),

    VP_DOMINIOS("PlusvaliaDominiosHechoControlador",
                    "VP_DOMINIOS", "178500C", "178500R", "178500U",
                    "178500D",
                    "178500G"),

    VP_NORMA_URBANISTICA("PlusvaliaNormaUrbanisticaControlador",
                    "VP_NORMA_URBANISTICA", "179100C", "179100R", "179100U",
                    "179100D",
                    "179100G"),

    CARTERA_CUENTA("ControldeCarteraControlador",
                    "CARTERA_CUENTA", "179300C", "179300R", "179300U",
                    "179300D",
                    "179300G"),

    PLAN_FLUJO_EFECTIVO("FrmConfigurarFlujoEfectivoControlador",
                    "PLAN_FLUJO_EFECTIVO", "179400C", null, "179400U",
                    "179400D", "179400G"),

    CODIGOSFUT("CodigoschipfutsControlador", "CODIGOSFUT", "169300C", null,
                    "169300U", "169300D", "169300G"),

    CODIGOS_FUT_FORMULARIO("CodigosfutformulariosControlador",
                    "CODIGOS_FUT_FORMULARIO", "169900C", null,
                    "169900U", "169900D", "169900G"),

    TIPOLOGIACONT("TipologiaContControlador",
                    "TIPOLOGIACONT", "20400C", null,
                    "20400U", "20400D", "20400G"),

    FUENTESFUTCATEGORIAS("FuentesCategoriasControlador", "FUENTESFUTCATEGORIAS",
                    "168600C", null,
                    "168600U", "168600D", "168600G"),

    HECHOVICTIMIZANTE("HechovictimizantesControlador", "HECHOVICTIMIZANTE",
                    "168700C", null, "168700U", "168700D", "168700G"),

    INVENTARIO_FISICO("FrmValidarInventarioFisicoControlador",
                    "INVENTARIO_FISICO", null, null,
                    null, "180000D", null),

    ORGANIGRAMA("ConfiguracionOrganigramasControlador", "ORGANIGRAMA",
                    "180100C", null,
                    "180100U", "180100D", "180100G"),

    GN_BANCO_APLICACION("ConfigurarBancoControlador", "GN_BANCO_APLICACION",
                    "180300C", null,
                    "180300U", "180300D", "180300G"),

    GN_PAGO_BANCO_REC("FrmConfigurarRecaudoPagosControlador",
                    "GN_PAGO_BANCO_REC", null, null,
                    null, "180400D", "180400G"),

    NIVEL_AGRUPAMIENTO("FrmNivelAgrupamientosControlador",
                    "NIVEL_AGRUPAMIENTO", "180600C", null,
                    "180600U", "180600D", "180600G"),

    CONFIGURACION_ANEXOS("FrmAnexosControlador",
                    "CONFIGURACION_ANEXOS", "180800C", null,
                    "180800U", "180800D", null),
    
    CONFIGURACION_ANEXOSHV("FrmAnexoshvControlador",
            "CONFIGURACION_ANEXOSHV", "198300C", null,
            "198300U", "198300D", "198300G"),

    SEGURIDADSOCIALNOV("FrmseguridadsocnovsControlador",
                    "SEGURIDADSOCIALNOV", "181300C", null,
                    "181300U", "181300D", "181300G"),

    PA_ANEXOS_BASE("FrmanexoproyectosControlador",
                    "PA_ANEXOS_BASE", "181200C", null,
                    "181200U", null, "181200G"),

    PB_POBLACION_BENEFICIADA("FrmPoblacionBeneficiadasControlador",
                    "PB_POBLACION_BENEFICIADA", "181400C", null,
                    "181400U", null, "181400G"),

    CM_CAPITULOS_CIE("FrmCapitulosCiesControlador", "CM_CAPITULOS_CIE",
                    "181500C", null, "181500U", "181500D", "181500G"),

    CM_CODIGOS_CIE("FrmCodigosCiesControlador", "CM_CODIGOS_CIE",
                    "181600C", null, "181600U", "181600D", "181600G"),

    CM_CODIGOS_CUMS("FrmCodigosCumControlador", "CM_CODIGOS_CUMS",
                    "181700C", "181700R", "181700U", "181700D", "181700G"),

    CM_CODIGOS_CUPS("FrmCodigosCupsControlador", "CM_CODIGOS_CUPS",
                    "181900C", null, "181900U", "181900D", "181900G"),

    CM_CAUSACION_AUTOMATICA("FrmConfiguracionInterfazControlador",
                    "CM_CAUSACION_AUTOMATICA",
                    "182000C", "182000R", "182000U", "182000D", "182000G"),

    CM_DET_CAUSACION_AUTOMATICA("FrmConfiguracionInterfazControlador",
                    "CM_DET_CAUSACION_AUTOMATICA",
                    "182100C", null, "182100U", "182100D", "182100G"),

    D_NODO_DISPARA("FrmNodoDisparaControlador",
                    "D_NODO_DISPARA",
                    "182400C", null, "182400U", "182400D", "182400G"),

    CM_IMPORTAR_RIPS("FrmImportarRipsControlador", "CM_IMPORTARRIPS",
                    "182300C", "182300R", "182300U", "182300D", "182300G"),

    ARCHIVO_CENTRAL("ArchivocentralsControlador", "ARCHIVO_CENTRAL",
                    "182500C", "182500R", "182500U", "182500D", "182500G"),

    BIENES_INMUEBLES("InmueblesisControlador", "BIENES_INMUEBLES",
                    "182600C", "182600R", "182600U", "182600D", "182600G"),

    CM_ARCHIVO_TRANSACCIONES("FrmCausarRipsControlador",
                    "CM_ARCHIVO_TRANSACCIONES",
                    "182800C", null, "182800U", "182800D", "182800G"),

    MOTIVO_DEVOLUTIVO("FrmCausarRipsControlador", "MOTIVO_DEVOLUTIVO",
                    "183000C", null, "183000U", "183000D", "183000G"),

    TIPO_MEDIO("FrmTipoMedioControlador", "TIPO_MEDIO",
                    "182900C", null, "182900U", "182900D", "182900G"),

    BP_ODS("FrmObjetivosDesSostControlador", "BP_ODS", "183100C", "183100R",
                    "183100U", "183100D", "183100G"),

    BP_ODS_META("FrmObjetivosDesSostControlador", "BP_ODS_META", "183200C",
                    "183200R", "183200U", "183200D", "183200G"),

    CM_CODIGOS_GENERALES_GLOSAS("FrmCodigosGeneralesGlosasControlador",
                    "CM_CODIGOS_GENERALES_GLOSAS", "183300C",
                    null, "183300U", "183300D", "183300G"),

    CM_CODIGOS_ESPEC_GLOSAS("FrmCodigosEspecificosGlosasControlador",
                    "CM_CODIGOS_ESPEC_GLOSAS", "183400C",
                    null, "183400U", "183400D", "183400G"),

    CM_CODIFICACION_GLOSAS("FrmCodificacionGlosasControlador",
                    "CM_CODIFICACION_GLOSAS", "183500C",
                    null, "183500U", "183500D", "183500G"),

    CM_AUDITORIA_GLOSAS("FrmAuditoriaGlosasControlador", "CM_AUDITORIA_GLOSAS",
                    "183600C",
                    "183600R", "183600U", "183600D", "183600G"),

    INDICADOR_DETERIORO("IndicadorDeterioroControlador", "DEVOLUTIVO",
                    null, null, "141145", null, "141143"),

    REGISTRO_DETERIORO("RegistroDeterioroControlador", "REEXPRESARVIDAUTIL",
                    "174900C", null, "1749003", "174900D", "1749001"),

    SF_LUGARES("FrmLugaresControlador", "SF_LUGAR",
                    "183700C", null, "183700U", "183700D", "183700G"),

    SF_UBICACIONES("FrmUbicacionesControlador", "SF_UBICACION", "183800C", null,
                    "183800U", "183800D", "183800G"),

    SF_LOCALES("FrmLocalesControlador", "SF_LOCALES", "183900C", null,
                    "183900U", "183900D", "183900G"),

    PLAN_FLUJO_EFECTIVO_CGN("FrmConfigurarFlujoEfectivoCgnControlador",
                    "PLAN_FLUJO_EFECTIVO_CGN", "184000C", null, "184000U",
                    "184000D", "184000G"),

    TEMPORALDEUDAPREDIO("FrmCargarDatosPredialControlador",
                    "TEMPORALDEUDAPREDIO", "184100C", null, null,
                    null, null),

    IMPUESTO_TEMPORAL("ImpuestotemporalControlador", "IMPUESTO_TEMPORAL",
                    "184200C", "184200R", "184200U",
                    "184200D", "184200G"),

    GRUPO_PERSONAL("FrmGrupoPersonalControlador", "GRUPO_PERSONAL",
                    "184400C", "184400R", "184400U",
                    "184400D", "184400G"),

    WF_DEPENDENCIAS("FrmwfDependenciasControlador", "WF_DEPENDENCIAS",
                    "184500C", "184500R", "184500U",
                    "184500D", "184500G"),

    PROCESO_VARIABLE("FrmProcesosVariablesControlador", "PROCESO_VARIABLE",
                    "184600C",
                    "184600R", "184600U", "184600D",
                    "184600G"),

    TIPOS_FE("FrmResponsabilidadFsControlador", "TIPOS_FE", "184800C", null,
                    "184800U", "184800D", "184800G"),

    TRAMITE_VARIABLE("FrmWFInformaciongeneralControlador", "TRAMITE_VARIABLE",
                    null, null, "184700U", null, "184700G"),

    TRAMITE_DEUDA("FrmWFInformaciongeneralControlador", "TRAMITE_DEUDA",
                    "184900C", null, "184900U", null, "184900G"),

    CONF_CERTIFICADO("ConfCertificadoDianControlador", "CONF_CERTIFICADO", null,
                    null, "185100U", null, "185100G"),

    TERCERO_OBFISCALES("TercerosControlador", "TERCERO_OBFISCALES", "185200C",
                    null, "185200U",
                    "185200D", "185200G"),

    DET_RANGO_FACT("FrmGestionFacturacion", "DET_RANGO_FACT", "185400C", null,
                    null, null, "185400G"),

    ESTADO_DOC_DIAN("FrmEstadoDocDianControlador", "ESTADO_DOC_DIAN", "185800C",
                    null,
                    null, null, "185800G"),

    TEMP_COMPROBANTE_CNTRETENCION("ComprobanteCntRetencionSdControlador",
                    "TEMP_COMPROBANTE_CNTRETENCION", "186100C", null, "186100U",
                    "186100D", "186100G"),

    TEMP_BORRADO_FACTURAS("FrmEliminarFaeControlador", "TEMP_BORRADO_FACTURAS",
                    "186200C", null, null,
                    null, "186200G"),

    SALUD_PENSIONADOS("FrmSaludPensionadosControlador", "SALUD_PENSIONADOS",
                    "186300C", null, "186300U", "186300D", "186300G"),

    POLITICA_CONTRASENAS("FrmPoliticasContrasenasControlador",
                    "POLITICA_CONTRASENAS",
                    "186400C", null, "186400U", "186400D", "186400G"),

    SF_TASAS_INTERES("FrmTasasInteresSfControlador", "SF_TASAS_INTERES",
                    "186600C", null, "186600U", "186600D", "186600G"),

    DEUDOR_SOLIDARIO("TercerosControlador", "DEUDOR_SOLIDARIO",
                    "186800C", null, "186800U", "186800D", "186800G"),

    CUIPO_CODIGO_CCEPT("FrmCodigoCceptsControlador", "CUIPO_CODIGO_CCEPT",
                    "186900C", null, "186900U", "186900D", "186900G"),

    CUIPO_CODIGO_CPC_DANE("FrmCodigoCpcsControlador", "CUIPO_CODIGO_CPC_DANE",
                    "187000C", null, "187000U", "187000D", "187000G"),

    CUIPO_SECCION_PPTAL("FrmSeccionPptalControlador", "CUIPO_SECCION_PPTAL",
                    "187100C", null, "187100U", "187100D", "187100G"),

    CUIPO_SECTORES("FrmSectoresCuipoControlador", "CUIPO_SECTORES",
                    "187200C", null, "187200U", "187200D", "187200G"),

    CUIPO_PROGRAMA("FrmProgramaCuipoControlador", "CUIPO_PROGRAMA", "187300C",
                    null, "187300U", "187300D", "187300G"),

    CUIPO_CODIGO_PRODUCTO("FrmCodigoProductoCuipoControlador",
                    "CUIPO_CODIGO_PRODUCTO", "187400C",
                    null, "187400U", "187400D", "187400G"),

    CUIPO_POLITICA("FrmPolitaCuipoControlador", "CUIPO_POLITICA", "187500C",
                    null, "187500U", "187500D", "187500G"),

    CUIPO_FUENTES("FrmFuentesCuipoControlador", "CUIPO_FUENTES", "187600C",
                    null, "187600U", "187600D", "187600G"),
    
    TIPO_RECURSO_SGR("FrmTipoRecursoSGRControlador", "TIPO_RECURSO_SGR", "187700C",
            null, "187700U", "187700D", "187700G"),
    
    CONFIG_EMAIL("ConfigurarEmailControlador", "CONFIG_EMAIL", "187800C", "187800R", "187800U", null, "187800G"),
    
    CLASIFICADORES("FrmclasificadoresControlador", "CLASECLASIFICADOR", "188300C", null,
            "188300U", "188300D", "188300G"),
   
    TIPOCLASIFICADOR("FrmTipoClasificadoresControlador", "TIPOCLASIFICADOR", "188400C", "188400R",
            "188400U", "188400D", "188400G"),
    
    CM_CLASE_CUENTA("FrmclasecuentaControlador","CM_CLASECUENTA","188500C", "188500R", "188500U", "188500D", "188500G"),
    
    CM_ESTADO_CUENTA("FrmclasecuentaControlador","CM_ESTADOCUENTA","188600C", "188600R", "188600U", "188600D", "188600G"),
    
    TIPOVIGENCIA("FrmTipovigenciaControlador","TIPOVIGENCIA",null, "176600R","176600U",null, "176600G"),
    
    CAUSALES_INEXISTENCIA("CausalesinexistenciasControlador", "CAUSALES_INEXISTENCIA", "189000C", 
    		null, "189000U", "189000D", "189000G"),
    
    ROL_SOLICITUDES("RolsolicitudesControlador", "ROL_SOLICITUDES", "189200C", 
    		null, "189200U", "189200D", "189200G"),
    
    CERTIFICADO_INEXISTENCIA("CertificadoinexistenciasControlador", "CERTIFICADO_INEXISTENCIA", "189300C", 
    		"189300R", "189300U", "189300D", "189300G"),
                    
    CM_AUDITORES("FrmauditoresControlador", "CM_AUDITORES",
            "188800C", null, "188800U", "188800D", "188800G"),
    
    CM_CODIGOS_GLOSAS_FACTURA("FrmAuditoriaGlosasControlador", "CM_CODIGOS_GLOSAS_FACTURA",
            "189400C", null, "189400U", "189400D", "189400G"),
    
    CONF_CERTIFICADONOM("ConfCertificadoDianNomControlador", "CONF_CERTIFICADONOMINA", null,
            null, "188200U", null, "188200G"),
    
    ACTIVIDADESCIIU_CONTROLADOR("ActividadesciiusControlador", "CIIU","189700C", null, "189700U", null, "189700G"),
    
    RANGOS_PRIMA_ANTIGUEDAD("RangosprimaantiguedadControlador","RANGOS_PRIMA_ANTIGUEDAD", "191700C", 
    		null, "191700U", "191700D", "191700G"),
    
    CONCEPTOSDOCSOPORTEDIAN("ConceptosdocsoportedianControlador","CONCEPTOS_CUDS", "189500C", 
    		"189500R", "189500U", "189500D", "189500G"),
    
    CONFIG_MARCA_BLANCA("MarcablancaControlador", "CONFIG_MARCA_BLANCA", null,
            "189800R", "189800U", null, "189800G"),
    
    BALANCE_FISCAL("FrmbalancefiscalimputacionsControlador", "BALANCE_FISCAL", "190000C",
            "190000R", "190000U", "190000D", "190000G"),
    
    DETALLE_BALANCE_FISCAL("FrmdetallebalancefiscalsControlador", "DETALLE_BALANCE_FISCAL", "190100C",
            "190100R", "190100U", "190100D", "190100G"),
    
	GRUPO_CONTABLE("GrupocontablesControlador", "GRUPO_CONTABLE", null,
            null, "64800U", null, "64800G"),
	
	DISTRIBUCION_ICLD("FrmdistribucionicldControlador", "DISTRIBUCION_ICLD", "190400C",
            "190400R", "190400U", "190400D", "190400G"),
        
	AYUDA_PROCESOS("ProcesosayudaControlador", "AYUDA_PROCESOS", "190600C", null, "190600U", "190600D", "190600G"),
 
	AYUDA_TAREAS("TareasayudaControlador", "AYUDA_TAREAS", "190800C", null, "190800U", "190800D", "190800G"),

	INFORMES_ENTES("ConfigEntesControlador", "INFORMES_ENTES", "175000C", null, "175000U", null, "175000G"),

	RECLASIFICAR_NIIF_MENSUAL("FrmreclasificacionmensualControlador", "RECLASIFICAR_NIIF_MENSUAL", "191000C", "191000R",
			"191000U", "191000D", "191000G"),

	D_RECLASIFICAR_NIIF_MENSUAL("FrmreclasificacionmensualControlador", "D_RECLASIFICAR_NIIF_MENSUAL", "191100C", null,
			"191100U", "191100D", "191100G"),

	INF_AUDITORIA("ConsultaauditoriasControlador", "INF_AUDITORIA", "190900C", null, null, null, "190900G"),

	APROPIACIONES_INICIALES_PPTAL("ConfigurarplanpresupuestalControlador", "APROPIACIONESINICIALES", null, "1884067",
			"120012", null, "1884065"),

	HORAS_EXTRAS("FrmHorasExtrasControlador", "HORAS_EXTRAS", "191300C", null, "191300U", "191300D", "191300G"),

	CENTRO_UTILIDAD("FrmcentroutilidadControlador", "CENTRO_UTILIDAD", "191500C", "191500R", "191500U", "191500D",
			"191500G"),
    	
	FACTORES_CALCULO("FactorescalculoControlador", "FACTORES_CALCULO", "191800C", null, "191800U", "191800D",
			"191800G"),

	D_FACTORES_CALCULO("DFactorescalculoControlador", "D_FACTORES_CALCULO", "191900C", null, "191900U", "191900D",
			"191900G"),
	
	UBICACIONES("FrmUbicacionAlmControlador", "UBICACIONES", "192200C", null, "192200U", "192200D",
			"192200G"),
	
	DEPENDENCIA_UBICACION("FrmUbicacionDependenciaControlador", "DEPENDENCIA_UBICACION", "192300C", null, "192300U", "192300D",
			"192300G"),
	
	TIPO_PAGO("frmTipoPagoControlador", "TIPO_PAGO", "192500C", null, "192500U", "192500D", "192500G"),
	
	REGISTRO_REEXPRESION("ReexpresarVidaUtilControlador", "REEXPRESARVIDAUTIL",
            "174900C", null, "1749003", "174900D", "1749007"),

	TARIFAS_POR_CONCEPTO("FrmTarifasPorConceptosControlador", "TARIFAS_POR_CONCEPTO", "192100C", "192100R",
			 "192100U", "192100D", "192100G"),
	
	D_GRUPO_PERSONAL("DgrupopersonalControlador", "D_GRUPO_PERSONAL", "192000C", null, "192000U", "192000D",
			"192000G"),
	
	PROYCONTRATOS("ProycontratosControlador", "PROYCONTRATOS", "192700C", null, "192700U", "192700D",
			"192700G"),
	
	COFINANCIADORES("CofinanciadoresControlador", "COFINANCIADORES", "192600C", null, "192600U", "192600D",
			"192600G"),
	
	TA_CONCEPTOS("FrmConceptosoServicios","TA_CONCEPTOS", "193000C", "193000R",
			"193000U", "193000D", "193000G"),
	
	TIPO_PROVEEDORES("FrmTipoProveedorControlador", "TA_PROVEEDORES", "193100C", null, "193100U", "193100D", "1931001"),
	
	TA_DETALLE_CONCEPTOS("FrmConceptosoServicios","TA_DETALLE_CONCEPTOS", "193200C", null,
			"193200U", "193200D", "1932001"), 
	
	CAUSACION_AUTOMATICA("FrmCausacionAutomaticaControlador","CAUSACION_AUTOMATICA", "193300C", "193300R",
			"193300U", "193300D", "193300G"),
	
	VLR_NOTA_CREDITO("FrmNotasCreditoControlador", "VLR_NOTA_CREDITO", "193400C",
            null, "193400U", "193400D", "193400G"),
	
	PRORRATEADOS("RetencionesControlador", "PRORRATEADOS", "193700C",
            null, "193700U", "193700D", "1937001"),
	
	DC_PRODUCTO_SUI("FrmproductodistribucioncostosControlador", "DC_PRODUCTO_SUI", "193800C",
            null, "193800U", "193800D", "193800G"),
	
	DC_RECURSO_SUI("FrmrecursodistribucioncostoControlador", "DC_RECURSO_SUI", "193900C",
            null, "193900U", "193900D", "193900G"),
	
	DC_ACTIVIDAD_SUI("FrmactividaddistribucioncostoControlador", "DC_ACTIVIDAD_SUI", "194000C",
            null, "194000U", "194000D", "194000G"),
	
	DC_RECURSO_ACTIVIDAD_SUI("FrmrecursoactividadsControlador", "DC_RECURSO_ACTIVIDAD_SUI", "194100C",
            null, "194100U", "194100D", "194100G"),
	
	REFERENCIADOS("Frmreferenciadoconceptoservicio", "REFERENCIADOS", "193600C", null,
            "193600U", "193600D", "193600G"),
	
	DISTPERSONAL("DistribuirAuxControlador","DISTPERSONAL", "194400C", "194400R",
            "194400U", "194400D", "194400G"),
	
	DISTPERSONALDIARIO("DistribuirAuxControlador","DISTPERSONALDIARIO", "194500C", "194500R",
            "194500U", "194500D", "194500G"),
	
	DISCAPACIDAD("","DISCAPACIDAD", "194800C", null,
            "194800U", "194800D", "194800G"),
	
	TIPO_SERVIDOR_PUBLICO("","TIPO_SERVIDOR_PUBLICO", "194900C", null,
            "194900U", "194900D", "194900G"), 
	
	TIPO_INFORMES_ENTES("TipoInformesEntesControlador", "TIPO_INFORMES_ENTES", null, null, "1907002", null, "190700G"),
	
	ACTIVIDAD_ECONOMICA("FrmActividadEconomicaControlador","ACTIVIDAD_ECONOMICA", "195200C", null,
            "195200U", "195200D", "195200G"),
	
	ACT_ECONOMICA_TERCERO("TercerosControlador","ACT_ECONOMICA_TERCERO", "195300C", null,
            "195300U", "195300D", "195300G"),
	
    INMUEBLE_FACTURACION("InmueblesControlador","INMUEBLE_FACTURACION", "194700C", "194700R",
            "194700U", "194700D", "194700G"),
	
	CONFIGURACION_DETERIORO("FrmconfiguraciondeterioroControlador", "CONFIGURACION_DETERIORO", "195400C",
            null, "195400U", "195400D", "195400G"),
	
	SF_PAGOS_PARCIALES("FrmconsultapagoparcialControlador", "SF_PAGOSPARCIALES",
            null, "195500R", "195500U", "195500D", "195500G"),
	
	ES_ADJUNTOS_ESTPREVIO("FrmadjuntosestudiospreviosControlador", "ES_ADJUNTOS_ESTPREVIO", "195600C", null,
            "195600U", "195600D", "195600G"),
	
	RETENCIONESVENTACNT("CodificarretencionesControlador", "RETENCIONESVENTACNT", 
			"195700C", null, "195700U", "195700D", "195700G"),
	
	PORCENTAJES_FSP_Y_ADICIONAL("PorcentajesFspAdiControlador", "PORCENTAJES_FSP_Y_ADICIONAL", 
			"196000C", null, "196000U", "196000D", "196000G"),
	
	ACUERDO_PAZ("AcuerdoPazControlador", "ACUERDO_PAZ", "196100C", "196100R",
            "196100U", "196100D", "196100G"),
	
	PILARES("AcuerdoPazControlador", "PILARES", "196200C", null,
            "196200U", "196200D", "196200G"),
	
	INICIATIVAS("IniciativasControlador", "INICIATIVAS", "196300C", null,
            "196300U", "196300D", "196300G"),
	
	EJE_ESTRUCTURAL("frmOrdenDepartamentalControlador", "EJE_ESTRUCTURAL", "196400C", "196400R",
            "196400U", "196400D", "196400G"),
	
	MEDIDA_PIGCCT("frmOrdenDepartamentalControlador", "MEDIDA_PIGCCT", "196500C", null,
	            "196500U", "196500D", "196500G"),
	
	ORDENMUNICIPAL("OrdenMunicipalControlador", "ORDENMUNICIPAL", "196600C", null,
            "196600U", "196600D", "196600G"),
	
	DEPRECIACION_ACUMULADA("", "DEPRECIACION_ACUMULADA", "196900C", null,
            "196900U", "196900D", "196900G"),
	
    DI_MOVIMIENTO("DigitalizacionContratosControlador", "DI_MOVIMIENTO", "197000C",
            null, null, "197000D", "197000G"),
    
    URLBI("FrmURLBIControlador", "URL_TABLERO_BI", null, null,
            "196800U", null, "196800G"),

    BP_ORDENNACIONAL("BpordennacionalControlador", "BP_ORDENNACIONAL", "197100C",
            null, null, "197100D", "197100G"),
    
    BP_ORDENDEPARTAMENTAL("BpordendepartamentalControlador", "BP_ORDENDEPARTAMENTAL", "197200C",
            null, null, "197200D", "197200G"),
    
    BP_ORDENMUNICIPAL("BpordenmunicipalControlador", "BP_ORDENMUNICIPAL", "197300C",
            null, null, "197300D", "197300G"), 
    
    CONFIGURACION_RETENCION("FrmConfigRegimenesControlador","CONFIGURACION_RETENCION","197400C",null,
    		"197400U","197400D","197400G"),
    
    DI_POLIZAS_ACTIVOS("DigitalizacionContratosControlador", "DI_POLIZAS_ACTIVOS", "197600C",
            null, null, "197600D", "197600G"),
    
    DEPRECIACION_ACUMULADA_INICIAL("FrmDepreciacionAcumuladaInicialControlador","DEPRECIACION_ACUMULADA_INICIAL","197800C",null,
    		"197800U","197800D","197800G"),
    
    TIPO_INMUEBLE_FAC("TipoinmueblefacControlador","TIPO_INMUEBLE_FAC","198000C",null,
    		"198000U","198000D","198000G"),
    
    EDIFICIOS("EdificioControlador","EDIFICIOS","198100C",null,
    		"198100U","198100D","198100G"),
    
    UBICACION_BIEN("UbicaciondelbienControlador","UBICACION_BIEN","198200C",null,
    		"198200U","198200D","198200G"),
    
    CONTROL_PROCESOS("ParametrosAlmacenControlador","CONTROL_PROCESOS","198400C",null,
    		null, null, null),
    
    BP_POLITICAS_PUBLICAS("BppoliticapublicaControlador","BP_POLITICAS_PUBLICAS","198600C",null,
    		"198600U","198600D","198600G"),
    
    BP_EJES_ESTRATEGICOS("BpejesestategicoControlador","BP_POLITICA_EJE_ESTRATEGICO","198700C",null,
    		"198700U","198700D","198700G"),
    
    CONFIGURAR_CONCEPTOS("FrmConfigurarConceptosControlador", "CONFIGURAR_CONCEPTOS", "198500C", null,
    		"198500U", "198500D", "198500G"),
    
    SECCIONES_CONCEPTOS("frmSeccionescontrolador","SECCIONES_CONCEPTOS","198800C",null,
    		"198800U","198800D","198800G"),
    
    DI_NOVEDADES("DigitalizacionContratosControlador", "DI_NOVEDADES", "199000C",
            null, null, "199000D", "199000G"),
    
    INVENTARIO_FISICO_CONSUMO("FrmInventarioFisico","INVENTARIO_FISICO_CONSUMO","198900C","198900R",
    		"198900U","198900D","198900G"),
    
    AUDITORIA_TABLAS("TablasauditarsControlador", "AUDITORIA_TABLAS", null, null,
            "199100U", null, "199100G"),
    
    UNI_EJECUTORA("UnidadejecutoraControlador", "UNIDAD_EJECUTORA", "199200C",
            null, "199200U", "199200D", "199200G"),
    
    RECURSOS_NATURALES("FrmrecursosnaturalesControlador","RECURSOS_NATURALES","199300C",null,
    		"199300U","199300D","199300G"),
    
    ES_RECURSOSNATURALES("FrmestprevrecursosnatControlador","ES_RECURSOSNATURALES","199400C",null,
    		"199400U","199400D","199400G"),
    
    CONFIG_PORC_BASP("FrmConfiguracionBaspControlador", "CONFIG_PORC_BASP",
            "199600C", null, "199600U", "199600D", "199600G"),
    
    CA_CONCEPTOS_INGRESOS("FrmconceptosingresoautControlador","CA_CONCEPTOS_ING","199700C",null,
    		"199700U","199700D","199700G"),
    
    CA_CAUSACION_INGRESOS("CausacioningresosautControlador","CA_CAUSACION_ING","199800C",null,
    		"199800U","199800D","199800G"),
    
    TIPOS_COMRPROMISOPPTAL("frmtiposcompromisopptalControlador","TIPO_COMPROMISOPPTAL","200100C",null,
    		"200100U","200100D","200100G"),
    
    FACTORES_CALCULO_FORMULADOR("FrmFormuladorNominaControlador","FACTORES_CALCULO_FORMULADOR","199900C",null,
    		null,null,null),
	
	FORMULAS_PROCEDIMIENTO("FrmFormuladorNominaControlador","FORMULAS_PROCEDIMIENTO","200000C","200000R",
			"200000U","200000D","200000G"),

    RANGOS_QUINQUENIO("RangosquinquenioControlador","RANGOS_QUINQUENIO", "200200C", 
    		null, "200200U", "200200D", "200200G"),
    
    HISTORIAL_UBICACION("FrmHistorialUbicacionControlador", "HISTORIAL_UBICACION", null, null, null , null, "2004001"),
    
    SF_CONFIG_SYC("FrmconfigsycControlador","SF_CONFIG_SYC","200500C","200500R",
            "200500U","200500D","200500G"),
    
    SF_CONFIG_SYC_DET("FrmconfigsycControlador","SF_CONFIG_SYC_DET", "200600C", 
            null, "200600U", "200600D", "200600G");
          	

    private final String name;
    private final String table;
    private final String createKey;
    private final String readKey;
    private final String updateKey;
    private final String deleteKey;
    private final String gridKey;

    private GenericUrlEnum(String name, String table, String createKey,
        String readKey, String updateKey,
        String deleteKey, String gridKey) {
        this.name = name;
        this.table = table;
        this.createKey = createKey;
        this.readKey = readKey;
        this.updateKey = updateKey;
        this.deleteKey = deleteKey;
        this.gridKey = gridKey;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @return the createKey
     */
    public String getCreateKey() {
        return createKey;
    }

    /**
     * @return the readKey
     */
    public String getReadKey() {
        return readKey;
    }

    /**
     * @return the updateKey
     */
    public String getUpdateKey() {
        return updateKey;
    }

    /**
     * @return the deleteKey
     */
    public String getDeleteKey() {
        return deleteKey;
    }

    /**
     * @return the gridKey
     */
    public String getGridKey() {
        return gridKey;
    }

}
