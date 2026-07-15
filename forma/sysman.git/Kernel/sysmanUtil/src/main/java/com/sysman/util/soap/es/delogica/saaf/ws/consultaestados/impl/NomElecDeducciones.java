package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NomElecDeducciones {

	private Salud salud;
	private FondoPension fondoPension;
	private FondoSP fondoSP;
	private List<Sindicato> sindicatos = new ArrayList<>();
	private Sanciones sanciones;
	private List<Libranza> libranzas = new ArrayList<>();
	private PagosTerceros pagosTerceros;
	private Anticipos anticipos;
	private OtrasDeducciones otrasDeducciones;

	private Double pensionVoluntaria;
	private Double retencionFuente;
	private Double afc;
	private Double cooperativa;
	private Double embargoFiscal;
	private Double planComplementarios;
	private Double educacion;
	private Double reintegro;
	private Double deuda;
	
	
	public NomElecDeducciones() {

	}

	// =========================
	// ===== SALUD =============
	// =========================

	public static class Salud {
		private BigDecimal porcentaje;
		private BigDecimal deduccion;

		public BigDecimal getPorcentaje() {
			return porcentaje;
		}

		public void setPorcentaje(BigDecimal d) {
			this.porcentaje = d;
		}

		public BigDecimal getDeduccion() {
			return deduccion;
		}

		public void setDeduccion(BigDecimal deduccion) {
			this.deduccion = deduccion;
		}
	}

	// =========================
	// ===== FONDO PENSION =====
	// =========================

	public static class FondoPension {
		private BigDecimal porcentaje;
		private BigDecimal deduccion;

		public BigDecimal getPorcentaje() {
			return porcentaje;
		}

		public void setPorcentaje(BigDecimal d) {
			this.porcentaje = d;
		}

		public BigDecimal getDeduccion() {
			return deduccion;
		}

		public void setDeduccion(BigDecimal deduccion) {
			this.deduccion = deduccion;
		}
	}

	// =========================
	// ===== FONDO SP ==========
	// =========================

	public static class FondoSP {
		private BigDecimal porcentaje;
		private BigDecimal deduccionSP;
		private BigDecimal porcentajeSub;
		private BigDecimal deduccionSub;

		public BigDecimal getPorcentaje() {
			return porcentaje;
		}

		public void setPorcentaje(BigDecimal porcentaje) {
			this.porcentaje = porcentaje;
		}

		public BigDecimal getDeduccionSP() {
			return deduccionSP;
		}

		public void setDeduccionSP(BigDecimal deduccionSP) {
			this.deduccionSP = deduccionSP;
		}

		public BigDecimal getPorcentajeSub() {
			return porcentajeSub;
		}

		public void setPorcentajeSub(BigDecimal porcentajeSub) {
			this.porcentajeSub = porcentajeSub;
		}

		public BigDecimal getDeduccionSub() {
			return deduccionSub;
		}

		public void setDeduccionSub(BigDecimal deduccionSub) {
			this.deduccionSub = deduccionSub;
		}
	}

	// ===== SINDICATOS =========

	public static class Sindicatos {
		private List<Sindicato> sindicados = new ArrayList<>();

		public List<Sindicato> getSindicatos() {
			return sindicados;
		}

		public void setSindicatos(List<Sindicato> sindicados) {
			this.sindicados = sindicados;
		}
	}
	
	// =========================
	// ===== SINDICATO =========
	// =========================

	public static class Sindicato {
		private Double porcentaje;
		private Double deduccion;

		public Double getPorcentaje() {
			return porcentaje;
		}

		public void setPorcentaje(Double porcentaje) {
			this.porcentaje = porcentaje;
		}

		public Double getDeduccion() {
			return deduccion;
		}

		public void setDeduccion(Double deduccion) {
			this.deduccion = deduccion;
		}
	}

	// =========================
	// ===== SANCIONES =========
	// =========================

	public static class Sanciones {
		private List<Sancion> sanciones = new ArrayList<>();

		public List<Sancion> getSanciones() {
			return sanciones;
		}

		public void setSanciones(List<Sancion> sanciones) {
			this.sanciones = sanciones;
		}
	}

	public static class Sancion {
		private Double sancionPublic;
		private Double sancionPriv;

		public Double getSancionPublic() {
			return sancionPublic;
		}

		public void setSancionPublic(Double sancionPublic) {
			this.sancionPublic = sancionPublic;
		}

		public Double getSancionPriv() {
			return sancionPriv;
		}

		public void setSancionPriv(Double sancionPriv) {
			this.sancionPriv = sancionPriv;
		}
	}

	// =========================
	// ===== LIBRANZA ==========
	// =========================

	public static class Libranza {
		private String descripcion;
		private Double deduccion;

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public Double getDeduccion() {
			return deduccion;
		}

		public void setDeduccion(Double deduccion) {
			this.deduccion = deduccion;
		}
	}

	// =========================
	// ===== PAGOS TERCEROS ====
	// =========================

	public static class PagosTerceros {
		private List<Double> pagos = new ArrayList<>();

		public List<Double> getPagos() {
			return pagos;
		}

		public void setPagos(List<Double> pagos) {
			this.pagos = pagos;
		}
	}

	// =========================
	// ===== ANTICIPOS =========
	// =========================

	public static class Anticipos {
		private List<Double> anticipos = new ArrayList<>();

		public List<Double> getAnticipos() {
			return anticipos;
		}

		public void setAnticipos(List<Double> anticipos) {
			this.anticipos = anticipos;
		}
	}

	// =========================
	// ===== OTRAS DEDUCCIONES =
	// =========================

	public static class OtrasDeducciones {

		private List<Double> otraDeduccion = new ArrayList<>();

		public List<Double> getOtraDeduccion() {
			return otraDeduccion;
		}

		public void setOtraDeduccion(List<Double> otraDeduccion) {
			this.otraDeduccion = otraDeduccion;
		}
		
	}

	// =========================
	// ===== GETTERS RAIZ ======
	// =========================

	public Salud getSalud() {
		return salud;
	}

	public void setSalud(Salud salud) {
		this.salud = salud;
	}

	public FondoPension getFondoPension() {
		return fondoPension;
	}

	public void setFondoPension(FondoPension fondoPension) {
		this.fondoPension = fondoPension;
	}

	public FondoSP getFondoSP() {
		return fondoSP;
	}

	public void setFondoSP(FondoSP fondoSP) {
		this.fondoSP = fondoSP;
	}

	public List<Sindicato> getSindicatos() {
		return sindicatos;
	}

	public void setSindicatos(List<Sindicato> sindicatos) {
		this.sindicatos = sindicatos;
	}

	public Sanciones getSanciones() {
		return sanciones;
	}

	public void setSanciones(Sanciones sanciones) {
		this.sanciones = sanciones;
	}

	public List<Libranza> getLibranzas() {
		return libranzas;
	}

	public void setLibranzas(List<Libranza> libranzas) {
		this.libranzas = libranzas;
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

	public OtrasDeducciones getOtrasDeducciones() {
		return otrasDeducciones;
	}

	public void setOtrasDeducciones(OtrasDeducciones otrasDeducciones) {
		this.otrasDeducciones = otrasDeducciones;
	}

	public Double getPensionVoluntaria() {
		return pensionVoluntaria;
	}

	public void setPensionVoluntaria(Double pensionVoluntaria) {
		this.pensionVoluntaria = pensionVoluntaria;
	}

	public Double getRetencionFuente() {
		return retencionFuente;
	}

	public void setRetencionFuente(Double retencionFuente) {
		this.retencionFuente = retencionFuente;
	}

	public Double getAfc() {
		return afc;
	}

	public void setAfc(Double afc) {
		this.afc = afc;
	}

	public Double getCooperativa() {
		return cooperativa;
	}

	public void setCooperativa(Double cooperativa) {
		this.cooperativa = cooperativa;
	}

	public Double getEmbargoFiscal() {
		return embargoFiscal;
	}

	public void setEmbargoFiscal(Double embargoFiscal) {
		this.embargoFiscal = embargoFiscal;
	}

	public Double getPlanComplementarios() {
		return planComplementarios;
	}

	public void setPlanComplementarios(Double planComplementarios) {
		this.planComplementarios = planComplementarios;
	}

	public Double getEducacion() {
		return educacion;
	}

	public void setEducacion(Double educacion) {
		this.educacion = educacion;
	}

	public Double getReintegro() {
		return reintegro;
	}

	public void setReintegro(Double reintegro) {
		this.reintegro = reintegro;
	}

	public Double getDeuda() {
		return deuda;
	}

	public void setDeuda(Double deuda) {
		this.deuda = deuda;
	}
	
	

}