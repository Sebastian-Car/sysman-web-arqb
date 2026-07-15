package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;
import java.util.List;

public class NomElecDevengados {

	private Basico basico;
	private Transporte transporte;
	private HorasExtrasDevengadas horasExtrasDevengadas;
	private Vacaciones vacaciones;
	private Primas primas;
	private Cesantias cesantias;
	private Incapacidades incapacidades;
	private Licencias licencias;
	private Bonificaciones bonificaciones;
	private Auxilios auxilios;
	private HuelgasLegales huelgasLegales;
	private OtrosConceptos otrosConceptos;
	private Compensaciones compensaciones;
	private BonoEPCTVs bonoEPCTVs;
	private Comisiones comisiones;
	private PagosTerceros pagosTerceros;
	private Anticipos anticipos;
	private OtrosDevengos otrosDevengos;

	public NomElecDevengados() {

	}

	// ==============================
	// BASICO
	// ==============================
	public static class Basico {
		private Double diasTrabajados;
		private BigDecimal sueldoTrabajado;

		public Double getDiasTrabajados() {
			return diasTrabajados;
		}

		public void setDiasTrabajados(Double diasTrabajados) {
			this.diasTrabajados = diasTrabajados;
		}

		public BigDecimal getSueldoTrabajado() {
			return sueldoTrabajado;
		}

		public void setSueldoTrabajado(BigDecimal sueldoTrabajado) {
			this.sueldoTrabajado = sueldoTrabajado;
		}
	}

	// ==============================
	// TRANSPORTE
	// ==============================
	public static class Transporte {
		private BigDecimal auxilioTransporte;
		private BigDecimal viaticoManuAlojS;
		private BigDecimal viaticoManuAlojNS;

		public BigDecimal getAuxilioTransporte() {
			return auxilioTransporte;
		}

		public void setAuxilioTransporte(BigDecimal bigDecimal) {
			this.auxilioTransporte = bigDecimal;
		}

		public BigDecimal getViaticoManuAlojS() {
			return viaticoManuAlojS;
		}

		public void setViaticoManuAlojS(BigDecimal viaticoManuAlojS) {
			this.viaticoManuAlojS = viaticoManuAlojS;
		}

		public BigDecimal getViaticoManuAlojNS() {
			return viaticoManuAlojNS;
		}

		public void setViaticoManuAlojNS(BigDecimal viaticoManuAlojNS) {
			this.viaticoManuAlojNS = viaticoManuAlojNS;
		}
	}

	// ==============================
	// HORAS EXTRAS
	// ==============================
	public static class HorasExtrasDevengadas {
		private List<HorasExtras> horasExtras;

		public static class HorasExtras {
			private String tipo;
			private String horaInicio;
			private String horaFin;
			private Double cantidad;
			private Double porcentaje;
			private Double pago;

			public String getTipo() {
				return tipo;
			}

			public void setTipo(String tipo) {
				this.tipo = tipo;
			}

			public String getHoraInicio() {
				return horaInicio;
			}

			public void setHoraInicio(String horaInicio) {
				this.horaInicio = horaInicio;
			}

			public String getHoraFin() {
				return horaFin;
			}

			public void setHoraFin(String horaFin) {
				this.horaFin = horaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}

			public Double getPorcentaje() {
				return porcentaje;
			}

			public void setPorcentaje(Double porcentaje) {
				this.porcentaje = porcentaje;
			}

			public Double getPago() {
				return pago;
			}

			public void setPago(Double pago) {
				this.pago = pago;
			}
		}

		public List<HorasExtras> getHorasExtras() {
			return horasExtras;
		}

		public void setHorasExtras(List<HorasExtras> horasExtras) {
			this.horasExtras = horasExtras;
		}
	}

	// ==============================
	// VACACIONES
	// ==============================
	public static class Vacaciones {

		private List<VacacionesComunes> vacacionesComunes;
		private List<VacacionesCompensadas> vacacionesCompensadas;

		public static class VacacionesComunes {
			private String fechaInicio;
			private String fechaFin;
			private String cantidad;
			private BigDecimal pago;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public String getCantidad() {
				return cantidad;
			}

			public void setCantidad(String string) {
				this.cantidad = string;
			}

			public BigDecimal getPago() {
				return pago;
			}

			public void setPago(BigDecimal pago) {
				this.pago = pago;
			}
		}

		public static class VacacionesCompensadas {
			private Double cantidad;
			private BigDecimal pago;

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}

			public BigDecimal getPago() {
				return pago;
			}

			public void setPago(BigDecimal pago) {
				this.pago = pago;
			}
		}

		public List<VacacionesComunes> getVacacionesComunes() {
			return vacacionesComunes;
		}

		public void setVacacionesComunes(List<VacacionesComunes> vacacionesComunes) {
			this.vacacionesComunes = vacacionesComunes;
		}

		public List<VacacionesCompensadas> getVacacionesCompensadas() {
			return vacacionesCompensadas;
		}

		public void setVacacionesCompensadas(List<VacacionesCompensadas> vacacionesCompensadas) {
			this.vacacionesCompensadas = vacacionesCompensadas;
		}
	}

	// ==============================
	// PRIMAS
	// ==============================
	public static class Primas {
		private Double cantidad;
		private BigDecimal pago;
		private BigDecimal pagoNS;

		public Double getCantidad() {
			return cantidad;
		}

		public void setCantidad(Double cantidad) {
			this.cantidad = cantidad;
		}

		public BigDecimal getPago() {
			return pago;
		}

		public void setPago(BigDecimal pago) {
			this.pago = pago;
		}

		public BigDecimal getPagoNS() {
			return pagoNS;
		}

		public void setPagoNS(BigDecimal pagoNS) {
			this.pagoNS = pagoNS;
		}
	}

	// ==============================
	// CESANTIAS
	// ==============================
	public static class Cesantias {
		private BigDecimal pago;
		private Double porcentaje;
		private BigDecimal pagoIntereses;

		public BigDecimal getPago() {
			return pago;
		}

		public void setPago(BigDecimal pago) {
			this.pago = pago;
		}

		public Double getPorcentaje() {
			return porcentaje;
		}

		public void setPorcentaje(Double porcentaje) {
			this.porcentaje = porcentaje;
		}

		public BigDecimal getPagoIntereses() {
			return pagoIntereses;
		}

		public void setPagoIntereses(BigDecimal pagoIntereses) {
			this.pagoIntereses = pagoIntereses;
		}
	}

	public static class Incapacidades {

		private List<Incapacidad> incapacidad;

		public static class Incapacidad {
			private String fechaInicio;
			private String fechaFin;
			private Double cantidad;
			private String tipo;
			private BigDecimal pago;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}

			public String getTipo() {
				return tipo;
			}

			public void setTipo(String tipo) {
				this.tipo = tipo;
			}

			public BigDecimal getPago() {
				return pago;
			}

			public void setPago(BigDecimal pago) {
				this.pago = pago;
			}
		}

		public List<Incapacidad> getIncapacidad() {
			return incapacidad;
		}

		public void setIncapacidad(List<Incapacidad> incapacidad) {
			this.incapacidad = incapacidad;
		}
	}

	public static class Licencias {

		private List<LicenciaMP> licenciaMP;
		private List<LicenciaR> licenciaR;
		private List<LicenciaNR> licenciaNR;

		public static class LicenciaMP {
			private String fechaInicio;
			private String fechaFin;
			private Double cantidad;
			private BigDecimal pago;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}

			public BigDecimal getPago() {
				return pago;
			}

			public void setPago(BigDecimal pago) {
				this.pago = pago;
			}
		}

		public static class LicenciaR {
			private String fechaInicio;
			private String fechaFin;
			private Double cantidad;
			private BigDecimal pago;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}

			public BigDecimal getPago() {
				return pago;
			}

			public void setPago(BigDecimal pago) {
				this.pago = pago;
			}
		}

		public static class LicenciaNR {
			private String fechaInicio;
			private String fechaFin;
			private Double cantidad;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}
		}

		public List<LicenciaMP> getLicenciaMP() {
			return licenciaMP;
		}

		public void setLicenciaMP(List<LicenciaMP> licenciaMP) {
			this.licenciaMP = licenciaMP;
		}

		public List<LicenciaR> getLicenciaR() {
			return licenciaR;
		}

		public void setLicenciaR(List<LicenciaR> licenciaR) {
			this.licenciaR = licenciaR;
		}

		public List<LicenciaNR> getLicenciaNR() {
			return licenciaNR;
		}

		public void setLicenciaNR(List<LicenciaNR> licenciaNR) {
			this.licenciaNR = licenciaNR;
		}
	}

	public static class Bonificaciones {

		private List<Bonificacion> bonificacion;

		public static class Bonificacion {
			private Double bonificacionS;
			private Double bonificacionNS;

			public Double getBonificacionS() {
				return bonificacionS;
			}

			public void setBonificacionS(Double bonificacionS) {
				this.bonificacionS = bonificacionS;
			}

			public Double getBonificacionNS() {
				return bonificacionNS;
			}

			public void setBonificacionNS(Double bonificacionNS) {
				this.bonificacionNS = bonificacionNS;
			}
		}

		public List<Bonificacion> getBonificacion() {
			return bonificacion;
		}

		public void setBonificacion(List<Bonificacion> bonificacion) {
			this.bonificacion = bonificacion;
		}
	}

	public static class Auxilios {

		private List<Auxilio> auxilio;

		public static class Auxilio {
			private BigDecimal auxilioS;
			private BigDecimal auxilioNS;

			public BigDecimal getAuxilioS() {
				return auxilioS;
			}

			public void setAuxilioS(BigDecimal auxilioS) {
				this.auxilioS = auxilioS;
			}

			public BigDecimal getAuxilioNS() {
				return auxilioNS;
			}

			public void setAuxilioNS(BigDecimal auxilioNS) {
				this.auxilioNS = auxilioNS;
			}
		}

		public List<Auxilio> getAuxilio() {
			return auxilio;
		}

		public void setAuxilio(List<Auxilio> auxilio) {
			this.auxilio = auxilio;
		}
	}

	public static class HuelgasLegales {

		private List<HuelgaLegal> huelgaLegal;

		public static class HuelgaLegal {
			private String fechaInicio;
			private String fechaFin;
			private Double cantidad;

			public String getFechaInicio() {
				return fechaInicio;
			}

			public void setFechaInicio(String fechaInicio) {
				this.fechaInicio = fechaInicio;
			}

			public String getFechaFin() {
				return fechaFin;
			}

			public void setFechaFin(String fechaFin) {
				this.fechaFin = fechaFin;
			}

			public Double getCantidad() {
				return cantidad;
			}

			public void setCantidad(Double cantidad) {
				this.cantidad = cantidad;
			}
		}

		public List<HuelgaLegal> getHuelgaLegal() {
			return huelgaLegal;
		}

		public void setHuelgaLegal(List<HuelgaLegal> huelgaLegal) {
			this.huelgaLegal = huelgaLegal;
		}
	}

	public static class OtrosConceptos {

		private List<OtroConcepto> otroConcepto;

		public static class OtroConcepto {
			private String descripcionConcepto;
			private Double conceptoS;
			private Double conceptoNS;

			public String getDescripcionConcepto() {
				return descripcionConcepto;
			}

			public void setDescripcionConcepto(String descripcionConcepto) {
				this.descripcionConcepto = descripcionConcepto;
			}

			public Double getConceptoS() {
				return conceptoS;
			}

			public void setConceptoS(Double conceptoS) {
				this.conceptoS = conceptoS;
			}

			public Double getConceptoNS() {
				return conceptoNS;
			}

			public void setConceptoNS(Double conceptoNS) {
				this.conceptoNS = conceptoNS;
			}
		}

		public List<OtroConcepto> getOtroConcepto() {
			return otroConcepto;
		}

		public void setOtroConcepto(List<OtroConcepto> otroConcepto) {
			this.otroConcepto = otroConcepto;
		}
	}

	public static class Compensaciones {

		private List<Compensacion> compensacion;

		public static class Compensacion {
			private Double compensacionO;
			private Double compensacionE;

			public Double getCompensacionO() {
				return compensacionO;
			}

			public void setCompensacionO(Double compensacionO) {
				this.compensacionO = compensacionO;
			}

			public Double getCompensacionE() {
				return compensacionE;
			}

			public void setCompensacionE(Double compensacionE) {
				this.compensacionE = compensacionE;
			}
		}

		public List<Compensacion> getCompensacion() {
			return compensacion;
		}

		public void setCompensacion(List<Compensacion> compensacion) {
			this.compensacion = compensacion;
		}
	}

	public static class BonoEPCTVs {

		private List<BonoEPCTV> bonoEPCTV;

		public static class BonoEPCTV {
			private BigDecimal pagoS;
			private BigDecimal pagoNS;
			private BigDecimal pagoAlimentacionS;
			private BigDecimal pagoAlimentacionNS;

			public BigDecimal getPagoS() {
				return pagoS;
			}

			public void setPagoS(BigDecimal pagoS) {
				this.pagoS = pagoS;
			}

			public BigDecimal getPagoNS() {
				return pagoNS;
			}

			public void setPagoNS(BigDecimal pagoNS) {
				this.pagoNS = pagoNS;
			}

			public BigDecimal getPagoAlimentacionS() {
				return pagoAlimentacionS;
			}

			public void setPagoAlimentacionS(BigDecimal pagoAlimentacionS) {
				this.pagoAlimentacionS = pagoAlimentacionS;
			}

			public BigDecimal getPagoAlimentacionNS() {
				return pagoAlimentacionNS;
			}

			public void setPagoAlimentacionNS(BigDecimal pagoAlimentacionNS) {
				this.pagoAlimentacionNS = pagoAlimentacionNS;
			}
		}

		public List<BonoEPCTV> getBonoEPCTV() {
			return bonoEPCTV;
		}

		public void setBonoEPCTV(List<BonoEPCTV> bonoEPCTV) {
			this.bonoEPCTV = bonoEPCTV;
		}
	}

	public static class Comisiones {

		private List<Double> comision;

		public List<Double> getComision() {
			return comision;
		}

		public void setComision(List<Double> comision) {
			this.comision = comision;
		}

	}

	public static class PagosTerceros {

		private List<BigDecimal> pagoTercero;

		public List<BigDecimal> getPagoTercero() {
			return pagoTercero;
		}

		public void setPagoTercero(List<BigDecimal> pagoTercero) {
			this.pagoTercero = pagoTercero;
		}
	}

	public static class Anticipos {

		private List<Double> anticipo;

		public List<Double> getAnticipo() {
			return anticipo;
		}

		public void setAnticipo(List<Double> anticipo) {
			this.anticipo = anticipo;
		}
	}

	public static class OtrosDevengos {

		private BigDecimal dotacion;
		private BigDecimal apoyoSost;
		private BigDecimal teletrabajo;
		private BigDecimal bonifRetiro;
		private BigDecimal indemnizacion;
		private BigDecimal reintegro;

		public BigDecimal getDotacion() {
			return dotacion;
		}

		public void setDotacion(BigDecimal dotacion) {
			this.dotacion = dotacion;
		}

		public BigDecimal getApoyoSost() {
			return apoyoSost;
		}

		public void setApoyoSost(BigDecimal apoyoSost) {
			this.apoyoSost = apoyoSost;
		}

		public BigDecimal getTeletrabajo() {
			return teletrabajo;
		}

		public void setTeletrabajo(BigDecimal teletrabajo) {
			this.teletrabajo = teletrabajo;
		}

		public BigDecimal getBonifRetiro() {
			return bonifRetiro;
		}

		public void setBonifRetiro(BigDecimal bonifRetiro) {
			this.bonifRetiro = bonifRetiro;
		}

		public BigDecimal getIndemnizacion() {
			return indemnizacion;
		}

		public void setIndemnizacion(BigDecimal indemnizacion) {
			this.indemnizacion = indemnizacion;
		}

		public BigDecimal getReintegro() {
			return reintegro;
		}

		public void setReintegro(BigDecimal reintegro) {
			this.reintegro = reintegro;
		}
	}

	// Getters y Setters generales

	public Incapacidades getIncapacidades() {
		return incapacidades;
	}

	public Basico getBasico() {
		return basico;
	}

	public void setBasico(Basico basico) {
		this.basico = basico;
	}

	public Transporte getTransporte() {
		return transporte;
	}

	public void setTransporte(Transporte transporte) {
		this.transporte = transporte;
	}

	public HorasExtrasDevengadas getHorasExtrasDevengadas() {
		return horasExtrasDevengadas;
	}

	public void setHorasExtrasDevengadas(HorasExtrasDevengadas horasExtrasDevengadas) {
		this.horasExtrasDevengadas = horasExtrasDevengadas;
	}

	public Vacaciones getVacaciones() {
		return vacaciones;
	}

	public void setVacaciones(Vacaciones vacaciones) {
		this.vacaciones = vacaciones;
	}

	public Primas getPrimas() {
		return primas;
	}

	public void setPrimas(Primas primas) {
		this.primas = primas;
	}

	public Cesantias getCesantias() {
		return cesantias;
	}

	public void setCesantias(Cesantias cesantias) {
		this.cesantias = cesantias;
	}

	public void setIncapacidades(Incapacidades incapacidades) {
		this.incapacidades = incapacidades;
	}

	public Licencias getLicencias() {
		return licencias;
	}

	public void setLicencias(Licencias licencias) {
		this.licencias = licencias;
	}

	public Bonificaciones getBonificaciones() {
		return bonificaciones;
	}

	public void setBonificaciones(Bonificaciones bonificaciones) {
		this.bonificaciones = bonificaciones;
	}

	public Auxilios getAuxilios() {
		return auxilios;
	}

	public void setAuxilios(Auxilios auxilios) {
		this.auxilios = auxilios;
	}

	public HuelgasLegales getHuelgasLegales() {
		return huelgasLegales;
	}

	public void setHuelgasLegales(HuelgasLegales huelgasLegales) {
		this.huelgasLegales = huelgasLegales;
	}

	public OtrosConceptos getOtrosConceptos() {
		return otrosConceptos;
	}

	public void setOtrosConceptos(OtrosConceptos otrosConceptos) {
		this.otrosConceptos = otrosConceptos;
	}

	public Compensaciones getCompensaciones() {
		return compensaciones;
	}

	public void setCompensaciones(Compensaciones compensaciones) {
		this.compensaciones = compensaciones;
	}

	public BonoEPCTVs getBonoEPCTVs() {
		return bonoEPCTVs;
	}

	public void setBonoEPCTVs(BonoEPCTVs bonoEPCTVs) {
		this.bonoEPCTVs = bonoEPCTVs;
	}

	public Comisiones getComisiones() {
		return comisiones;
	}

	public void setComisiones(Comisiones comisiones) {
		this.comisiones = comisiones;
	}

	public PagosTerceros getPagosTerceros() {
		return pagosTerceros;
	}

	public void setPagosTerceros(PagosTerceros pagosTerceros) {
		this.pagosTerceros = pagosTerceros;
	}

	public Anticipos getAnticipos() {
		return anticipos;
	}

	public void setAnticipos(Anticipos anticipos) {
		this.anticipos = anticipos;
	}

	public OtrosDevengos getOtrosDevengos() {
		return otrosDevengos;
	}

	public void setOtrosDevengos(OtrosDevengos otrosDevengos) {
		this.otrosDevengos = otrosDevengos;
	}

}