package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;

import com.sysman.util.SysmanFunciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Auxilios.Auxilio;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Bonificaciones.Bonificacion;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.BonoEPCTVs.BonoEPCTV;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Compensaciones.Compensacion;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.HorasExtrasDevengadas.HorasExtras;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.HuelgasLegales.HuelgaLegal;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Incapacidades.Incapacidad;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Licencias.LicenciaMP;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Licencias.LicenciaNR;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Licencias.LicenciaR;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.OtrosConceptos.OtroConcepto;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Vacaciones.VacacionesCompensadas;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados.Vacaciones.VacacionesComunes;

/**
 * Builder que convierte un modelo {@link NomElecGeneral} en el XML de nómina
 * electrónica con la estructura exigida por la DIAN para envío a Invoway.
 *
 * <p>El XML generado se construye manualmente con {@link StringBuilder} en lugar
 * de JAXB, para tener control preciso sobre qué etiquetas se incluyen: los campos
 * nulos o con valor cero se omiten del XML (excepto los marcados como obligatorios
 * con {@link #appendTagObligatorio}).</p>
 *
 * <p>Soporta dos estructuras según el tipo de documento:</p>
 * <ul>
 *   <li><b>Nómina base</b> ({@code esAjuste = false}): genera {@code <nomina>}
 *       con encabezado, devengados, deducciones y totales.</li>
 *   <li><b>Nómina de ajuste</b> ({@code esAjuste = true}): genera
 *       {@code <nominaAjuste>} que incluye además el bloque {@code <predecesor>}
 *       con la referencia al documento original, y envuelve el contenido
 *       en {@code <datosAjuste>}.</li>
 * </ul>
 */
public class NominaXmlBuilder {

	 /**
     * Punto de entrada del builder. Genera el XML completo a partir del modelo.
     *
     * @param data      Modelo con todos los datos de la nómina del empleado,
     *                  construido por {@link ResumentotalcuneControlador#generarModeloXml}.
     * @param esAjuste  {@code true} para generar estructura de nómina de ajuste,
     *                  {@code false} para nómina base.
     * @return          String con el XML del documento listo para normalizar y descargar.
     */
	public String build(NomElecGeneral data, boolean esAjuste) {

		if (esAjuste) {
			StringBuilder xml = new StringBuilder();

			xml.append("<nominaAjuste>");


			if (data.getEncabezado().getPredecesor() != null) {

				xml.append("<predecesor>");

				appendTag(xml, "operacion", data.getEncabezado().getPredecesor().getOperacion());
				appendTag(xml, "numeroPredecesor", data.getEncabezado().getPredecesor().getNumeroPredecesor());

				xml.append("</predecesor>");
			}

			xml.append("<datosAjuste>");
			appendEncabezado(xml, data.getEncabezado());
			appendDevengados(xml, data.getDevengados());
			appendDeducciones(xml, data.getDeducciones());
			appendTotales(xml, data.getTotalesGenerales());
			xml.append("</datosAjuste>");
			xml.append("</nominaAjuste>");

			return xml.toString();

		} else {

			StringBuilder xml = new StringBuilder();

			xml.append("<nomina>");

			appendEncabezado(xml, data.getEncabezado());

			appendDevengados(xml, data.getDevengados());
			appendDeducciones(xml, data.getDeducciones());
			appendTotales(xml, data.getTotalesGenerales());

			xml.append("</nomina>");

			return xml.toString();
		}
	}

	private void appendEncabezado(StringBuilder xml, NomElecEncabezado e) {

		if (e == null)
			return;

		appendTag(xml, "trackID", e.getTrackID());

		// ================= PERIODO =================
		if (e.getPeriodo() != null) {

			xml.append("<periodo>");

			appendTag(xml, "fechaIngreso", e.getPeriodo().getFechaIngreso());
			appendTag(xml, "fechaRetiro", e.getPeriodo().getFechaRetiro());
			appendTag(xml, "fechaLiquidacionInicio", e.getPeriodo().getFechaLiquidacionInicio());
			appendTag(xml, "fechaLiquidacionFin", e.getPeriodo().getFechaLiquidacionFin());
			appendTag(xml, "tiempoLaborado", e.getPeriodo().getTiempoLaborado());

			xml.append("</periodo>");
		}

		// ================= NUMERO SECUENCIA =================
		if (e.getNumeroSecuenciaXML() != null) {

			xml.append("<numeroSecuenciaXML>");

			appendTag(xml, "prefijo", e.getNumeroSecuenciaXML().getPrefijo());
			appendTag(xml, "consecutivo", e.getNumeroSecuenciaXML().getConsecutivo());
			appendTag(xml, "numero", e.getNumeroSecuenciaXML().getNumero());

			xml.append("</numeroSecuenciaXML>");
		}

		// ================= LUGAR GENERACION =================
		if (e.getLugarGeneracionXML() != null) {

			xml.append("<lugarGeneracionXML>");

			appendTag(xml, "pais", e.getLugarGeneracionXML().getPais());
			appendTag(xml, "departamento", e.getLugarGeneracionXML().getDepartamento());
			appendTag(xml, "municipio", e.getLugarGeneracionXML().getMunicipio());
			appendTag(xml, "idioma", e.getLugarGeneracionXML().getIdioma());

			xml.append("</lugarGeneracionXML>");
		}

		// ================= INFORMACION GENERAL =================
		if (e.getInformacionGeneral() != null) {

			xml.append("<informacionGeneral>");

			appendTag(xml, "periodoNomina", e.getInformacionGeneral().getPeriodoNomina());
			appendTag(xml, "tipoMoneda", e.getInformacionGeneral().getTipoMoneda());
			appendTag(xml, "TRM", e.getInformacionGeneral().getTRM());
			appendTag(xml, "notas", e.getInformacionGeneral().getNotas());

			xml.append("</informacionGeneral>");
		}

		// ================= EMPLEADOR =================
		if (e.getEmpleador() != null) {

			xml.append("<empleador>");

			appendTag(xml, "razonSocial", e.getEmpleador().getRazonSocial());
			appendTag(xml, "primerApellido", e.getEmpleador().getPrimerApellido());
			appendTag(xml, "segundoApellido", e.getEmpleador().getSegundoApellido());
			appendTag(xml, "primerNombre", e.getEmpleador().getPrimerNombre());
			appendTag(xml, "otrosNombres", e.getEmpleador().getOtrosNombres());
			appendTag(xml, "NIT", e.getEmpleador().getNIT());
			appendTag(xml, "DV", e.getEmpleador().getDV());
			appendTag(xml, "pais", e.getEmpleador().getPais());
			appendTag(xml, "departamento", e.getEmpleador().getDepartamento());
			appendTag(xml, "municipio", e.getEmpleador().getMunicipio());
			appendTag(xml, "direccion", e.getEmpleador().getDireccion());

			xml.append("</empleador>");
		}

		// ================= TRABAJADOR =================
		if (e.getTrabajador() != null) {

			xml.append("<trabajador>");

			appendTag(xml, "tipoTrabajador", e.getTrabajador().getTipoTrabajador());
			appendTagObligatorio(xml, "subtipoTrabajador", e.getTrabajador().getSubtipoTrabajador());
			appendTag(xml, "altoRiesgoPension", e.getTrabajador().getAltoRiesgoPension());
			appendTag(xml, "tipoDocumento", e.getTrabajador().getTipoDocumento());
			appendTag(xml, "numeroDocumento", e.getTrabajador().getNumeroDocumento());
			appendTag(xml, "primerApellido", e.getTrabajador().getPrimerApellido());
			appendTagObligatorio(xml, "segundoApellido", SysmanFunciones.nvl(e.getTrabajador().getSegundoApellido(),"VACIO"));
			appendTag(xml, "primerNombre", e.getTrabajador().getPrimerNombre());
			appendTag(xml, "otrosNombres", e.getTrabajador().getOtrosNombres());
			appendTag(xml, "paisTrabajo", e.getTrabajador().getPaisTrabajo());
			appendTag(xml, "departamentoTrabajo", e.getTrabajador().getDepartamentoTrabajo());
			appendTag(xml, "municipioTrabajo", e.getTrabajador().getMunicipioTrabajo());
			appendTag(xml, "direccionTrabajo", e.getTrabajador().getDireccionTrabajo());
			appendTag(xml, "salarioIntegral", e.getTrabajador().getSalarioIntegral());
			appendTag(xml, "tipoContrato", e.getTrabajador().getTipoContrato());
			appendTag(xml, "sueldo", e.getTrabajador().getSueldo());
			appendTag(xml, "codigoTrabajador", e.getTrabajador().getCodigoTrabajador());

			xml.append("</trabajador>");
		}

		// ================= PAGO =================
		if (e.getPago() != null) {

			xml.append("<pago>");

			appendTag(xml, "forma", e.getPago().getForma());
			appendTag(xml, "medio", e.getPago().getMedio());
			appendTag(xml, "banco", e.getPago().getBanco());
			appendTag(xml, "tipoCuenta", e.getPago().getTipoCuenta());
			appendTag(xml, "numeroCuenta", e.getPago().getNumeroCuenta());

			xml.append("</pago>");
		}

		// ================= FECHAS PAGOS =================
		if (e.getFechasPagos() != null && e.getFechasPagos().getFechaPago() != null) {

			xml.append("<fechasPagos>");

			for (Object fecha : e.getFechasPagos().getFechaPago()) {
				appendTag(xml, "fecha", fecha);
			}

			xml.append("</fechasPagos>");
		}
	}

	// ================= DEVENGADOS ========================

	private void appendDevengados(StringBuilder xml, NomElecDevengados d) {

		if (d == null)
			return;

		xml.append("<devengados>");

		// BASICO
		if (d.getBasico() != null) {
			xml.append("<basico>");
			appendTagObligatorio(xml, "diasTrabajados", d.getBasico().getDiasTrabajados());
			appendTagObligatorio(xml, "sueldoTrabajado", d.getBasico().getSueldoTrabajado());
			xml.append("</basico>");
		}

		// TRANSPORTE
		if (d.getTransporte() != null) {
		    StringBuilder temp = new StringBuilder();
		    
		    appendTag(temp, "auxilioTransporte", d.getTransporte().getAuxilioTransporte());
		    appendTag(temp, "viaticoManutAlojS", d.getTransporte().getViaticoManuAlojS());
		    appendTag(temp, "viaticoManutAlojNS", d.getTransporte().getViaticoManuAlojNS());

		    appendContainer(xml, "transporte", temp);
		}
		

		// HORAS EXTRAS
		if (d.getHorasExtrasDevengadas() != null && d.getHorasExtrasDevengadas().getHorasExtras() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    for (HorasExtras h : d.getHorasExtrasDevengadas().getHorasExtras()) {

		        StringBuilder tempHijo = new StringBuilder();

		        appendTag(tempHijo, "tipo", homologarTipoHoraExtra(h.getTipo()));
		        appendTag(tempHijo, "horaInicio", h.getHoraInicio());
		        appendTag(tempHijo, "horaFin", h.getHoraFin());
		        appendTag(tempHijo, "cantidad", h.getCantidad());
		        appendTag(tempHijo, "porcentaje", h.getPorcentaje());
		        appendTag(tempHijo, "pago", h.getPago());

		        appendContainer(tempPadre, "horasExtras", tempHijo);
		    }

		    appendContainer(xml, "horasExtrasDevengadas", tempPadre);
		}

		// ================= VACACIONES =================
		if (d.getVacaciones() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    // ================= VACACIONES COMUNES =================
		    if (d.getVacaciones().getVacacionesComunes() != null) {
		        for (VacacionesComunes vc : d.getVacaciones().getVacacionesComunes()) {

		            if (vc.getCantidad() == null 
		                || vc.getCantidad().equals("0") 
		                || vc.getPago() == null) {
		                continue; // 
		            }

		            StringBuilder tempHijo = new StringBuilder();

		            appendTag(tempHijo, "fechaInicio", vc.getFechaInicio());
		            appendTag(tempHijo, "fechaFin", vc.getFechaFin());

		            appendTagObligatorio(tempHijo, "cantidad", vc.getCantidad());
		            appendTagObligatorio(tempHijo, "pago", vc.getPago());

		            appendContainer(tempPadre, "vacacionesComunes", tempHijo);
		        }
		    }

		    // ================= VACACIONES COMPENSADAS =================
		    if (d.getVacaciones().getVacacionesCompensadas() != null) {
		        for (VacacionesCompensadas vcomp : d.getVacaciones().getVacacionesCompensadas()) {

		            if (vcomp.getCantidad() == null 
		                || vcomp.getCantidad().equals("0") 
		                || vcomp.getPago() == null) {
		                continue; // 
		            }

		            StringBuilder tempHijo = new StringBuilder();

		            appendTagObligatorio(tempHijo, "cantidad", vcomp.getCantidad());
		            appendTagObligatorio(tempHijo, "pago", vcomp.getPago());

		            appendContainer(tempPadre, "vacacionesCompensadas", tempHijo);
		        }
		    }

		    if (tempPadre.length() > 0) {
		        appendContainer(xml, "vacaciones", tempPadre);
		    }
		}

		// PRIMAS
		boolean tienePrimas = 
			       (d.getPrimas().getCantidad() != null && d.getPrimas().getCantidad() > 0)
			    || (d.getPrimas().getPago() != null && d.getPrimas().getPago().compareTo(BigDecimal.ZERO) > 0)
			    || (d.getPrimas().getPagoNS() != null && d.getPrimas().getPagoNS().compareTo(BigDecimal.ZERO) > 0);

			if (tienePrimas) {

			    StringBuilder temp = new StringBuilder();

			    Double cantidad = d.getPrimas().getCantidad() != null 
			            ? d.getPrimas().getCantidad() 
			            : 0.0;

			    BigDecimal pago = d.getPrimas().getPago() != null 
			            ? d.getPrimas().getPago() 
			            : BigDecimal.ZERO;

			    BigDecimal pagoNS = d.getPrimas().getPagoNS() != null 
			            ? d.getPrimas().getPagoNS() 
			            : BigDecimal.ZERO;

			    appendTagObligatorio(temp, "cantidad", cantidad);
			    appendTagObligatorio(temp, "pago", pago);
			    appendTagObligatorio(temp, "pagoNS", pagoNS);

			    appendContainer(xml, "primas", temp);
			}

		// CESANTIAS
		if (d.getCesantias() != null) {

		    StringBuilder temp = new StringBuilder();

		    boolean tieneCesantias = 
		           (d.getCesantias().getPago() != null 
		            && d.getCesantias().getPago().compareTo(BigDecimal.ZERO) > 0)
		        || (d.getCesantias().getPorcentaje() != null 
		            && d.getCesantias().getPorcentaje() != 0)
		        || (d.getCesantias().getPagoIntereses() != null 
		            && d.getCesantias().getPagoIntereses().compareTo(BigDecimal.ZERO) > 0);

		    if (tieneCesantias) {

		        appendTagObligatorio(temp, "pago", d.getCesantias().getPago());
		        appendTagObligatorio(temp, "porcentaje", d.getCesantias().getPorcentaje());
		        BigDecimal pagoIntereses = d.getCesantias().getPagoIntereses() != null
		                ? d.getCesantias().getPagoIntereses()
		                : BigDecimal.ZERO;

		        appendTagObligatorio(temp, "pagoIntereses", pagoIntereses);

		        appendContainer(xml, "cesantias", temp);
		    }
		}

		// INCAPACIDADES
		if (d.getIncapacidades() != null && d.getIncapacidades().getIncapacidad() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    for (Incapacidad inc : d.getIncapacidades().getIncapacidad()) {

		        if (inc.getTipo() == null || "0".equals(inc.getTipo())) {
		            continue;
		        }

		        if (inc.getCantidad() == null || inc.getCantidad().doubleValue() <= 0) {
		            continue;
		        }

		        StringBuilder tempHijo = new StringBuilder();

		        appendTag(tempHijo, "fechaInicio", inc.getFechaInicio());
		        appendTag(tempHijo, "fechaFin", inc.getFechaFin());

		        appendTagObligatorio(tempHijo, "cantidad", inc.getCantidad());
		        appendTag(tempHijo, "tipo", inc.getTipo());
		        appendTagObligatorio(tempHijo, "pago", inc.getPago());

		        appendContainer(tempPadre, "incapacidad", tempHijo);
		    }

		    if (tempPadre.length() > 0) {
		        appendContainer(xml, "incapacidades", tempPadre);
		    }
		}

		// LICENCIAS
		if (d.getLicencias() != null) {

			StringBuilder tempPadre = new StringBuilder();

			// LicenciaMP
			if (d.getLicencias().getLicenciaMP() != null) {
				for (LicenciaMP l : d.getLicencias().getLicenciaMP()) {

					StringBuilder tempHijo = new StringBuilder();

					appendTag(tempHijo, "fechaInicio", l.getFechaInicio());
					appendTag(tempHijo, "fechaFin", l.getFechaFin());
					appendTag(tempHijo, "cantidad", l.getCantidad());
					appendTag(tempHijo, "pago", l.getPago());

					appendContainer(tempPadre, "licenciaMP", tempHijo);
				}
			}

			// LicenciaR
			if (d.getLicencias().getLicenciaR() != null) {
				for (LicenciaR l : d.getLicencias().getLicenciaR()) {

					StringBuilder tempHijo = new StringBuilder();

					appendTag(tempHijo, "fechaInicio", l.getFechaInicio());
					appendTag(tempHijo, "fechaFin", l.getFechaFin());
					appendTag(tempHijo, "cantidad", l.getCantidad());
					appendTag(tempHijo, "pago", l.getPago());

					appendContainer(tempPadre, "licenciaR", tempHijo);
				}
			}

			// LicenciaNR
			if (d.getLicencias().getLicenciaNR() != null) {
				for (LicenciaNR l : d.getLicencias().getLicenciaNR()) {

					StringBuilder tempHijo = new StringBuilder();

					appendTag(tempHijo, "fechaInicio", l.getFechaInicio());
					appendTag(tempHijo, "fechaFin", l.getFechaFin());
					appendTag(tempHijo, "cantidad", l.getCantidad());

					appendContainer(tempPadre, "licenciaNR", tempHijo);
				}
			}

			appendContainer(xml, "licencias", tempPadre);
		}

		// BONIFICACIONES
		if (d.getBonificaciones() != null && d.getBonificaciones().getBonificacion() != null) {

			StringBuilder tempPadre = new StringBuilder();

			for (Bonificacion b : d.getBonificaciones().getBonificacion()) {

				StringBuilder tempHijo = new StringBuilder();

				appendTag(tempHijo, "bonificacionS", b.getBonificacionS());
				appendTag(tempHijo, "bonificacionNS", b.getBonificacionNS());

				appendContainer(tempPadre, "bonificacion", tempHijo);
			}

			appendContainer(xml, "bonificaciones", tempPadre);
		}

		// AUXILIOS
		if (d.getAuxilios() != null && d.getAuxilios().getAuxilio() != null) {

			StringBuilder tempPadre = new StringBuilder();

			for (Auxilio a : d.getAuxilios().getAuxilio()) {

				StringBuilder tempHijo = new StringBuilder();

				appendTag(tempHijo, "auxilioS", a.getAuxilioS());
				appendTag(tempHijo, "auxilioNS", a.getAuxilioNS());

				appendContainer(tempPadre, "auxilio", tempHijo);
			}

			appendContainer(xml, "auxilios", tempPadre);
		}

		// HUELGAS LEGALES
		if (d.getHuelgasLegales() != null && d.getHuelgasLegales().getHuelgaLegal() != null) {

			StringBuilder tempPadre = new StringBuilder();

			for (HuelgaLegal h : d.getHuelgasLegales().getHuelgaLegal()) {

				StringBuilder tempHijo = new StringBuilder();

				appendTag(tempHijo, "fechaInicio", h.getFechaInicio());
				appendTag(tempHijo, "fechaFin", h.getFechaFin());
				appendTag(tempHijo, "cantidad", h.getCantidad());

				appendContainer(tempPadre, "huelgaLegal", tempHijo);
			}

			appendContainer(xml, "huelgasLegales", tempPadre);
		}

		// OTROS CONCEPTOS
		if (d.getOtrosConceptos() != null) {

			xml.append("<otrosConceptos>");

			if (d.getOtrosConceptos().getOtroConcepto() != null) {
				for (OtroConcepto oc : d.getOtrosConceptos().getOtroConcepto()) {

					xml.append("<otroConcepto>");
					appendTag(xml, "descripcionConcepto", oc.getDescripcionConcepto());
					appendTag(xml, "conceptoS", oc.getConceptoS());
					appendTag(xml, "conceptoNS", oc.getConceptoNS());
					xml.append("</otroConcepto>");
				}
			}

			xml.append("</otrosConceptos>");
		}

		// COMPENSACIONES
		if (d.getCompensaciones() != null && d.getCompensaciones().getCompensacion() != null) {

			StringBuilder tempPadre = new StringBuilder();

			for (Compensacion c : d.getCompensaciones().getCompensacion()) {

				StringBuilder tempHijo = new StringBuilder();

				appendTag(tempHijo, "compensacionO", c.getCompensacionO());
				appendTag(tempHijo, "compensacionE", c.getCompensacionE());

				appendContainer(tempPadre, "compensacion", tempHijo);
			}

			appendContainer(xml, "compensaciones", tempPadre);
		}

		// BONOEPCTVs
		if (d.getBonoEPCTVs() != null && d.getBonoEPCTVs().getBonoEPCTV() != null) {

			StringBuilder tempPadre = new StringBuilder();

			for (BonoEPCTV b : d.getBonoEPCTVs().getBonoEPCTV()) {

				StringBuilder tempHijo = new StringBuilder();

				appendTag(tempHijo, "pagoS", b.getPagoS());
				appendTag(tempHijo, "pagoNS", b.getPagoNS());
				appendTag(tempHijo, "pagoAlimentacionS", b.getPagoAlimentacionS());
				appendTag(tempHijo, "pagoAlimentacionNS", b.getPagoAlimentacionNS());

				appendContainer(tempPadre, "bonoEPCTV", tempHijo);
			}

			appendContainer(xml, "bonoEPCTVs", tempPadre);
		}

		// COMISIONES
		if (d.getComisiones() != null && d.getComisiones().getComision() != null) {

			StringBuilder temp = new StringBuilder();

			for (Object com : d.getComisiones().getComision()) {
				appendTag(temp, "comision", com);
			}

			appendContainer(xml, "comisiones", temp);
		}

		// PAGOS TERCEROS
		if (d.getPagosTerceros() != null && d.getPagosTerceros().getPagoTercero() != null) {

			StringBuilder temp = new StringBuilder();

			for (Object pt : d.getPagosTerceros().getPagoTercero()) {
				appendTag(temp, "pagoTercero", pt);
			}

			appendContainer(xml, "pagosTerceros", temp);
		}

		// ANTICIPOS
		if (d.getAnticipos() != null && d.getAnticipos().getAnticipo() != null) {

			StringBuilder temp = new StringBuilder();

			for (Object a : d.getAnticipos().getAnticipo()) {
				appendTag(temp, "anticipo", a);
			}

			appendContainer(xml, "anticipos", temp);
		}

		// OTROS DEVENGOS
		if (d.getOtrosDevengos() != null) {

		    StringBuilder temp = new StringBuilder();

		    appendTag(temp, "dotacion", d.getOtrosDevengos().getDotacion());
		    appendTag(temp, "apoyoSost", d.getOtrosDevengos().getApoyoSost());
		    appendTag(temp, "teletrabajo", d.getOtrosDevengos().getTeletrabajo());
		    appendTag(temp, "bonifRetiro", d.getOtrosDevengos().getBonifRetiro());
		    appendTag(temp, "indemnizacion", d.getOtrosDevengos().getIndemnizacion());
		    appendTag(temp, "reintegro", d.getOtrosDevengos().getReintegro());

		    appendContainer(xml, "otrosDevengos", temp);
		}

		xml.append("</devengados>");
		
	}

	private void appendDeducciones(StringBuilder xml, NomElecDeducciones d) {

		if (d == null)
		    return;

		StringBuilder tempDeducciones = new StringBuilder();

		// ================= SALUD  =================
		{
		    StringBuilder temp = new StringBuilder();

		    BigDecimal porcentaje = d.getSalud() != null && d.getSalud().getPorcentaje() != null
		            ? d.getSalud().getPorcentaje()
		            : BigDecimal.valueOf(4.00);

		    BigDecimal deduccion = d.getSalud() != null && d.getSalud().getDeduccion() != null
		            ? d.getSalud().getDeduccion()
		            : BigDecimal.ZERO;

		    appendTagObligatorio(temp, "porcentaje", porcentaje);
		    appendTagObligatorio(temp, "deduccion", deduccion);

		    appendContainer(tempDeducciones, "salud", temp);
		}

		// ================= FONDO PENSION  =================
		{
		    StringBuilder temp = new StringBuilder();

		    BigDecimal porcentaje = d.getFondoPension() != null && d.getFondoPension().getPorcentaje() != null
		            ? d.getFondoPension().getPorcentaje()
		            : BigDecimal.valueOf(4.00);

		    BigDecimal deduccion = d.getFondoPension() != null && d.getFondoPension().getDeduccion() != null
		            ? d.getFondoPension().getDeduccion()
		            : BigDecimal.ZERO;

		    appendTagObligatorio(temp, "porcentaje", porcentaje);
		    appendTagObligatorio(temp, "deduccion", deduccion);

		    appendContainer(tempDeducciones, "fondoPension", temp);
		}


		// ================= FONDO SP =================
		if (d.getFondoSP() != null) {

			if (d.getFondoSP() != null) {

			    BigDecimal deduccionSP = (d.getFondoSP().getDeduccionSP() != null 
			                             ? d.getFondoSP().getDeduccionSP() 
			                             : BigDecimal.ZERO);

			    BigDecimal deduccionSub = (BigDecimal) (d.getFondoSP().getDeduccionSub() != null 
			                              ? d.getFondoSP().getDeduccionSub() 
			                              : BigDecimal.ZERO);

			    boolean tieneSP = deduccionSP.compareTo(BigDecimal.ZERO) > 0 
			                     || deduccionSub.compareTo(BigDecimal.ZERO) > 0;

			    if (tieneSP) {
			        StringBuilder temp = new StringBuilder();

			        appendTag(temp, "porcentaje", d.getFondoSP().getPorcentaje());
			        appendTag(temp, "deduccionSP", deduccionSP);
			        appendTag(temp, "porcentajeSub", d.getFondoSP().getPorcentajeSub());
			        appendTag(temp, "deduccionSub", deduccionSub);

			        appendContainer(tempDeducciones, "fondoSP", temp);
			    }
			}

		}


		// ================= SINDICATOS =================
		if (d.getSindicatos() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    for (NomElecDeducciones.Sindicato s : d.getSindicatos()) {

		        StringBuilder tempHijo = new StringBuilder();

		        appendTagObligatorio(tempHijo, "porcentaje", s.getPorcentaje());
		        appendTag(tempHijo, "deduccion", s.getDeduccion());

		        appendContainer(tempPadre, "sindicato", tempHijo);
		    }

		    appendContainer(tempDeducciones, "sindicatos", tempPadre);
		}

//		appendContainer(xml, "deducciones", tempDeducciones);

		// ================= SANCIONES =================
		if (d.getSanciones() != null && d.getSanciones().getSanciones() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    for (NomElecDeducciones.Sancion s : d.getSanciones().getSanciones()) {

		        StringBuilder tempHijo = new StringBuilder();

		        appendTag(tempHijo, "sancionPublica", s.getSancionPublic());
		        appendTag(tempHijo, "sancionPrivada", s.getSancionPriv());

		        appendContainer(tempPadre, "sancion", tempHijo);
		    }

		    appendContainer(tempDeducciones, "sanciones", tempPadre);
		}


		// ================= LIBRANZAS =================
		if (d.getLibranzas() != null) {

		    StringBuilder tempPadre = new StringBuilder();

		    for (NomElecDeducciones.Libranza l : d.getLibranzas()) {

		        StringBuilder tempHijo = new StringBuilder();

		        appendTag(tempHijo, "descripcion", l.getDescripcion());
		        appendTag(tempHijo, "deduccion", l.getDeduccion());

		        appendContainer(tempPadre, "libranza", tempHijo);
		    }

		    appendContainer(tempDeducciones, "libranzas", tempPadre);
		}


		// ================= PAGOS TERCEROS =================
		if (d.getPagosTerceros() != null && d.getPagosTerceros().getPagos() != null) {

		    StringBuilder temp = new StringBuilder();

		    for (Double pt : d.getPagosTerceros().getPagos()) {
		        appendTag(temp, "pagoTercero", pt);
		    }

		    appendContainer(tempDeducciones, "pagosTerceros", temp);
		}


		// ================= ANTICIPOS =================
		if (d.getAnticipos() != null && d.getAnticipos().getAnticipos() != null) {

		    StringBuilder temp = new StringBuilder();

		    for (Double a : d.getAnticipos().getAnticipos()) {
		        appendTag(temp, "anticipo", a);
		    }

		    appendContainer(tempDeducciones, "anticipos", temp);
		}


		// ================= OTRAS DEDUCCIONES =================
		if (d.getOtrasDeducciones() != null) {

		    StringBuilder temp = new StringBuilder();

		    appendTag(temp, "otraDeduccion", d.getOtrasDeducciones().getOtraDeduccion());

		    appendContainer(tempDeducciones, "otrasDeducciones", temp);
		}

		// ================= CAMPOS SIMPLES =================

		appendTag(tempDeducciones, "pensionVoluntaria", d.getPensionVoluntaria());
		appendTag(tempDeducciones, "retencionFuente", d.getRetencionFuente());
		appendTag(tempDeducciones, "AFC", d.getAfc());
		appendTag(tempDeducciones, "cooperativa", d.getCooperativa());
		appendTag(tempDeducciones, "embargoFiscal", d.getEmbargoFiscal());
		appendTag(tempDeducciones, "planComplementarios", d.getPlanComplementarios());
		appendTag(tempDeducciones, "educacion", d.getEducacion());
		appendTag(tempDeducciones, "reintegro", d.getReintegro());
		appendTag(tempDeducciones, "deuda", d.getDeuda());

	    xml.append("<deducciones>");
	    xml.append(tempDeducciones);
	    xml.append("</deducciones>");
		
	}

	// ================= TOTALES ===========================

	private void appendTotales(StringBuilder xml, NomElecTotales t) {

	    if (t == null)
	        return;

	    StringBuilder temp = new StringBuilder();

	    appendTag(temp, "redondeo", t.getRedondeo());
	    appendTagObligatorio(temp, "devengadosTotal", t.getDevengadosTotal());
	    appendTagObligatorio(temp, "deduccionesTotal", t.getDeduccionesTotal());
	    appendTagObligatorio(temp, "comprobanteTotal", t.getComprobanteTotal());

	    if (temp.length() > 0) {
	        xml.append("<totales>");
	        xml.append(temp);
	        xml.append("</totales>");
	    }
	}

	// ================= UTIL ==============================

	private void appendTag(StringBuilder xml, String tag, Object value) {

		if (value == null)
			return;

		if (value instanceof Number) {
			if (((Number) value).doubleValue() == 0)
				return;
		}

		if (value instanceof String) {

			String str = ((String) value).trim();

			if (str.isEmpty() || "null".equalsIgnoreCase(str))
				return;

			try {
				if (Double.parseDouble(str) == 0)
					return;
			} catch (NumberFormatException e) {
			}
		}

		xml.append("<").append(tag).append(">").append(value).append("</").append(tag).append(">");
	}
	
	private void appendTagObligatorio(StringBuilder xml, String tag, Object value) {
		
		if (value != null) {
			xml.append("<").append(tag).append(">").append(value).append("</").append(tag).append(">");
		}
		
	}
	
	
	private void appendContainer(StringBuilder xml, String tag, StringBuilder content) {
	    if (content.length() == 0) return;

	    xml.append("<").append(tag).append(">");
	    xml.append(content);
	    xml.append("</").append(tag).append(">");
	}
	
	/**
     * Convierte el código interno de tipo de hora extra de Sysman al código
     * numérico exigido por la DIAN.
     * Mapeo: {@code HED}→{@code 1}, {@code HEN}→{@code 2}, {@code HRN}→{@code 3},
     * {@code HEDDF}→{@code 4}, {@code HRDDF}→{@code 5}, {@code HENDF}→{@code 6},
     * {@code HRNDF}→{@code 7}.
     */
	public static String homologarTipoHoraExtra(String tipo) {

	    if (tipo == null)
	        return null;

	    switch (tipo) {

	        case "HED":
	            return "1"; // Hora Extra Diurna

	        case "HEN":
	            return "2"; // Hora Extra Nocturna

	        case "HRN":
	            return "3"; // Hora Recargo Nocturno

	        case "HEDDF":
	            return "4"; // Hora Extra Diurna Dominical/Festivo

	        case "HRDDF":
	            return "5"; // Hora Recargo Diurno Dominical/Festivo

	        case "HENDF":
	            return "6"; // Hora Extra Nocturna Dominical/Festivo

	        case "HRNDF":
	            return "7"; // Hora Recargo Nocturno Dominical/Festivo

	        default:
	            return tipo;
	    }
	}
	
}