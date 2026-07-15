package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class NomElecEncabezado {

	private String trackID;
	private Periodo periodo;
	private NumeroSecuenciaXML numeroSecuenciaXML;
	private LugarGeneracionXML lugarGeneracionXML;
	private InformacionGeneral informacionGeneral;
	private Empleador empleador;
	private Trabajador trabajador;
	private Pago pago;
	private FechasPagos fechasPagos;
	private Predecesor predecesor;

	public String getTrackID() {
		return trackID;
	}

	public void setTrackID(String trackID) {
		this.trackID = trackID;
	}

	public Periodo getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}

	public NumeroSecuenciaXML getNumeroSecuenciaXML() {
		return numeroSecuenciaXML;
	}

	public void setNumeroSecuenciaXML(NumeroSecuenciaXML numeroSecuenciaXML) {
		this.numeroSecuenciaXML = numeroSecuenciaXML;
	}

	public LugarGeneracionXML getLugarGeneracionXML() {
		return lugarGeneracionXML;
	}

	public void setLugarGeneracionXML(LugarGeneracionXML lugarGeneracionXML) {
		this.lugarGeneracionXML = lugarGeneracionXML;
	}

	public InformacionGeneral getInformacionGeneral() {
		return informacionGeneral;
	}

	public void setInformacionGeneral(InformacionGeneral informacionGeneral) {
		this.informacionGeneral = informacionGeneral;
	}

	public Empleador getEmpleador() {
		return empleador;
	}

	public void setEmpleador(Empleador empleador) {
		this.empleador = empleador;
	}

	public Trabajador getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}

	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}

	public FechasPagos getFechasPagos() {
		return fechasPagos;
	}

	public void setFechasPagos(FechasPagos fechasPagos) {
		this.fechasPagos = fechasPagos;
	}

	public Predecesor getPredecesor() {
		return predecesor;
	}

	public void setPredecesor(Predecesor predecesor) {
		this.predecesor = predecesor;
	}

	public static class Predecesor {
		private String operacion;
		private String cunePredecesor;
		private String numeroPredecesor;

		public String getOperacion() {
			return operacion;
		}

		public void setOperacion(String operacion) {
			this.operacion = operacion;
		}

		public String getCunePredecesor() {
			return cunePredecesor;
		}

		public void setCunePredecesor(String cunePredecesor) {
			this.cunePredecesor = cunePredecesor;
		}

		public String getNumeroPredecesor() {
			return numeroPredecesor;
		}

		public void setNumeroPredecesor(String numeroPredecesor) {
			this.numeroPredecesor = numeroPredecesor;
		}

	}

	public static class Periodo {
		private String fechaIngreso;
		private String fechaRetiro;
		private String fechaLiquidacionInicio;
		private String fechaLiquidacionFin;
		private BigDecimal tiempoLaborado;

		public String getFechaIngreso() {
			return fechaIngreso;
		}

		public void setFechaIngreso(String fechaIngreso) {
			this.fechaIngreso = fechaIngreso;
		}

		public String getFechaRetiro() {
			return fechaRetiro;
		}

		public void setFechaRetiro(String fechaRetiro) {
			this.fechaRetiro = fechaRetiro;
		}

		public String getFechaLiquidacionInicio() {
			return fechaLiquidacionInicio;
		}

		public void setFechaLiquidacionInicio(String fechaLiquidacionInicio) {
			this.fechaLiquidacionInicio = fechaLiquidacionInicio;
		}

		public String getFechaLiquidacionFin() {
			return fechaLiquidacionFin;
		}

		public void setFechaLiquidacionFin(String fechaLiquidacionFin) {
			this.fechaLiquidacionFin = fechaLiquidacionFin;
		}

		public BigDecimal getTiempoLaborado() {
			return tiempoLaborado;
		}

		public void setTiempoLaborado(BigDecimal tiempoLaborado) {
			this.tiempoLaborado = tiempoLaborado;
		}

	}

	public static class NumeroSecuenciaXML {
		private String prefijo;
		private Long consecutivo;
		private String numero;

		public String getPrefijo() {
			return prefijo;
		}

		public void setPrefijo(String prefijo) {
			this.prefijo = prefijo;
		}

		public Long getConsecutivo() {
			return consecutivo;
		}

		public void setConsecutivo(Long consecutivo) {
			this.consecutivo = consecutivo;
		}

		public String getNumero() {
			return numero;
		}

		public void setNumero(String numero) {
			this.numero = numero;
		}
	}

	public static class LugarGeneracionXML {
		private String pais;
		private String departamento;
		private String municipio;
		private String idioma;

		public String getPais() {
			return pais;
		}

		public void setPais(String pais) {
			this.pais = pais;
		}

		public String getDepartamento() {
			return departamento;
		}

		public void setDepartamento(String departamento) {
			this.departamento = departamento;
		}

		public String getMunicipio() {
			return municipio;
		}

		public void setMunicipio(String municipio) {
			this.municipio = municipio;
		}

		public String getIdioma() {
			return idioma;
		}

		public void setIdioma(String idioma) {
			this.idioma = idioma;
		}
	}

	public static class InformacionGeneral {
		private String periodoNomina;
		private String tipoMoneda;
		private BigDecimal TRM;
		private String notas;

		public String getPeriodoNomina() {
			return periodoNomina;
		}

		public void setPeriodoNomina(String periodoNomina) {
			this.periodoNomina = periodoNomina;
		}

		public String getTipoMoneda() {
			return tipoMoneda;
		}

		public void setTipoMoneda(String tipoMoneda) {
			this.tipoMoneda = tipoMoneda;
		}

		public BigDecimal getTRM() {
			return TRM;
		}

		public void setTRM(BigDecimal tRM) {
			TRM = tRM;
		}

		public String getNotas() {
			return notas;
		}

		public void setNotas(String notas) {
			this.notas = notas;
		}
	}

	public static class Empleador {
		private String razonSocial;
		private String primerApellido;
		private String segundoApellido;
		private String primerNombre;
		private String otrosNombres;
		private String NIT;
		private Integer DV;
		private String pais;
		private String departamento;
		private String municipio;
		private String direccion;

		public String getRazonSocial() {
			return razonSocial;
		}

		public void setRazonSocial(String razonSocial) {
			this.razonSocial = razonSocial;
		}

		public String getPrimerApellido() {
			return primerApellido;
		}

		public void setPrimerApellido(String primerApellido) {
			this.primerApellido = primerApellido;
		}

		public String getSegundoApellido() {
			return segundoApellido;
		}

		public void setSegundoApellido(String segundoApellido) {
			this.segundoApellido = segundoApellido;
		}

		public String getPrimerNombre() {
			return primerNombre;
		}

		public void setPrimerNombre(String primerNombre) {
			this.primerNombre = primerNombre;
		}

		public String getOtrosNombres() {
			return otrosNombres;
		}

		public void setOtrosNombres(String otrosNombres) {
			this.otrosNombres = otrosNombres;
		}

		public String getNIT() {
			return NIT;
		}

		public void setNIT(String nIT) {
			NIT = nIT;
		}

		public Integer getDV() {
			return DV;
		}

		public void setDV(Integer dV) {
			DV = dV;
		}

		public String getPais() {
			return pais;
		}

		public void setPais(String pais) {
			this.pais = pais;
		}

		public String getDepartamento() {
			return departamento;
		}

		public void setDepartamento(String departamento) {
			this.departamento = departamento;
		}

		public String getMunicipio() {
			return municipio;
		}

		public void setMunicipio(String municipio) {
			this.municipio = municipio;
		}

		public String getDireccion() {
			return direccion;
		}

		public void setDireccion(String direccion) {
			this.direccion = direccion;
		}

	}

	public static class Trabajador {
		private String tipoTrabajador;
		private String subtipoTrabajador;
		private Boolean altoRiesgoPension;
		private String tipoDocumento;
		private String numeroDocumento;
		private String primerApellido;
		private String segundoApellido;
		private String primerNombre;
		private String otrosNombres;
		private String paisTrabajo;
		private String departamentoTrabajo;
		private String municipioTrabajo;
		private String direccionTrabajo;
		private Boolean salarioIntegral;
		private String tipoContrato;
		private BigDecimal sueldo;
		private String codigoTrabajador;

		public String getTipoTrabajador() {
			return tipoTrabajador;
		}

		public void setTipoTrabajador(String tipoTrabajador) {
			this.tipoTrabajador = tipoTrabajador;
		}

		public String getSubtipoTrabajador() {
			return subtipoTrabajador;
		}

		public void setSubtipoTrabajador(String subtipoTrabajador) {
			this.subtipoTrabajador = subtipoTrabajador;
		}

		public Boolean getAltoRiesgoPension() {
			return altoRiesgoPension;
		}

		public void setAltoRiesgoPension(Boolean altoRiesgoPension) {
			this.altoRiesgoPension = altoRiesgoPension;
		}

		public String getTipoDocumento() {
			return tipoDocumento;
		}

		public void setTipoDocumento(String tipoDocumento) {
			this.tipoDocumento = tipoDocumento;
		}

		public String getNumeroDocumento() {
			return numeroDocumento;
		}

		public void setNumeroDocumento(String numeroDocumento) {
			this.numeroDocumento = numeroDocumento;
		}

		public String getPrimerApellido() {
			return primerApellido;
		}

		public void setPrimerApellido(String primerApellido) {
			this.primerApellido = primerApellido;
		}

		public String getSegundoApellido() {
			return segundoApellido;
		}

		public void setSegundoApellido(String segundoApellido) {
			this.segundoApellido = segundoApellido;
		}

		public String getPrimerNombre() {
			return primerNombre;
		}

		public void setPrimerNombre(String primerNombre) {
			this.primerNombre = primerNombre;
		}

		public String getOtrosNombres() {
			return otrosNombres;
		}

		public void setOtrosNombres(String otrosNombres) {
			this.otrosNombres = otrosNombres;
		}

		public String getPaisTrabajo() {
			return paisTrabajo;
		}

		public void setPaisTrabajo(String paisTrabajo) {
			this.paisTrabajo = paisTrabajo;
		}

		public String getDepartamentoTrabajo() {
			return departamentoTrabajo;
		}

		public void setDepartamentoTrabajo(String departamentoTrabajo) {
			this.departamentoTrabajo = departamentoTrabajo;
		}

		public String getMunicipioTrabajo() {
			return municipioTrabajo;
		}

		public void setMunicipioTrabajo(String municipioTrabajo) {
			this.municipioTrabajo = municipioTrabajo;
		}

		public String getDireccionTrabajo() {
			return direccionTrabajo;
		}

		public void setDireccionTrabajo(String direccionTrabajo) {
			this.direccionTrabajo = direccionTrabajo;
		}

		public Boolean getSalarioIntegral() {
			return salarioIntegral;
		}

		public void setSalarioIntegral(Boolean salarioIntegral) {
			this.salarioIntegral = salarioIntegral;
		}

		public String getTipoContrato() {
			return tipoContrato;
		}

		public void setTipoContrato(String tipoContrato) {
			this.tipoContrato = tipoContrato;
		}

		public BigDecimal getSueldo() {
			return sueldo;
		}

		public void setSueldo(BigDecimal sueldo) {
			this.sueldo = sueldo;
		}

		public String getCodigoTrabajador() {
			return codigoTrabajador;
		}

		public void setCodigoTrabajador(String codigoTrabajador) {
			this.codigoTrabajador = codigoTrabajador;
		}

	}

	public static class Pago {
		private String forma;
		private String medio;
		private String banco;
		private String tipoCuenta;
		private String numeroCuenta;

		public String getForma() {
			return forma;
		}

		public void setForma(String forma) {
			this.forma = forma;
		}

		public String getMedio() {
			return medio;
		}

		public void setMedio(String medio) {
			this.medio = medio;
		}

		public String getBanco() {
			return banco;
		}

		public void setBanco(String banco) {
			this.banco = banco;
		}

		public String getTipoCuenta() {
			return tipoCuenta;
		}

		public void setTipoCuenta(String tipoCuenta) {
			this.tipoCuenta = tipoCuenta;
		}

		public String getNumeroCuenta() {
			return numeroCuenta;
		}

		public void setNumeroCuenta(String numeroCuenta) {
			this.numeroCuenta = numeroCuenta;
		}

	}

	public static class FechasPagos {
		private List<String> fechaPago;

		public List<String> getFechaPago() {
			return fechaPago;
		}

		public void setFechaPago(List<String> fechas) {
			this.fechaPago = fechas;
		}
	}
}