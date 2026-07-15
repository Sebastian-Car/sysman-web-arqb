/*-
 * RespuestaApi.java
 * procesadorCargarPlanContable.java
 * Clase para generar la respuesta de los campos de plan Contable
 * @author Camilo Andrés Pérez Dueñas
 * Paipa, Boyaca.
 */

package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RespuestaCargarPlanContable {
	private String compania;
	private String ano;
	private String codigo;
	private String nombre;
	private String naturaleza;
	private String movimiento;
	private String man_cen_cto;
	private String man_aux_ter;
	private String man_aux_gen;
	private String man_aux_ref;
	private String man_aux_fue;
	private String obliga_tercero;
	private String obliga_centro;
	private String obliga_auxiliar;
	private String obliga_referencia;
	private String obliga_fuente;
	private String dinamica;
	private String presupuesto_anual;
	private String corriente;
	private String formato;
	private String clasecuenta;
	private String bloqueacuenta;
	private String saldoinicial;
	private String saldo0;
	private String saldo1;
	private String saldo2;
	private String saldo3;
	private String saldo4;
	private String saldo5;
	private String saldo6;
	private String saldo7;
	private String saldo8;
	private String saldo9;
	private String saldo10;
	private String saldo11;
	private String saldo12;
	private String saldo13;
	private String neto0;
	private String neto1;
	private String neto2;
	private String neto3;
	private String neto4;
	private String neto5;
	private String neto6;
	private String neto7;
	private String neto8;
	private String neto9;
	private String neto10;
	private String neto11;
	private String neto12;
	private String neto13;
	private String debito0;
	private String debito1;
	private String debito2;
	private String debito3;
	private String debito4;
	private String debito5;
	private String debito6;
	private String debito7;
	private String debito8;
	private String debito9;
	private String debito10;
	private String debito11;
	private String debito12;
	private String debito13;
	private String credito0;
	private String credito1;
	private String credito2;
	private String credito3;
	private String credito4;
	private String credito5;
	private String credito6;
	private String credito7;
	private String credito8;
	private String credito9;
	private String credito10;
	private String credito11;
	private String credito12;
	private String credito13;
	private String ajuste0;
	private String ajuste1;
	private String ajuste2;
	private String ajuste3;
	private String ajuste4;
	private String ajuste5;
	private String ajuste6;
	private String ajuste7;
	private String ajuste8;
	private String ajuste9;
	private String ajuste10;
	private String ajuste11;
	private String ajuste12;
	private String ajuste13;
	private String generadesembolso;
	private String porcretencion;
	private String creditoexterno;
	private String pasarsaldo;
	private String cod_equiv;
	private String transaccional5544;
	private String destino;
	private String formatoegreso;
	private String banco;
	private String permiteconsolidar;
	private String man_fact_arrendamiento;
	private String notransaccional5544;
	private String noreportarreciprocas;
	private String terceroequivalentereciprocas;
	private String conceptoex;
	private String cuenta_bancaria;
	private String terceroex;
	private String sucursalex;
	private String tipodescuento_sia;
	private String codbanco_sia;
	private String numerocuenta_sia;
	private String destinocuentabanco;
	private String codbanco_serec;
	private String numerocuenta_serec;
	private String cuenta_pptal;
	private String esoficial;
	private String fuente;
	private String equivpr_debito;
	private String equivpr_credito;
	private String ivaex;
	private String retepracticada;
	private String reteasumida;
	private String ivacomun;
	private String ivasimplificado;
	private String exdistrital;
	private String id_niif;
	private String codigo_niif;
	private String man_distri_ccosto;
	private String reteica;
	private String cree_practicada;
	private String cree_asumida;
	private String ccbalance;
	private String reportasaldoreciprocas;
	private String men;
	private String verificar_mov;
	private String cod_flujocaja;
	private String created_by;
	private String modified_by;
	private String aplica_deterioro;
	private String deb_reco_det;
	private String cre_reco_det;
	private String deb_caus_det;
	private String cre_caus_det;
	private String deb_rec_det;
	private String cre_rec_det;
	private String date_modified;
	private String date_created;
	private String cheque;
	private String reportar_100;
	private String tercero_reciprocas;
	private String ind_circularunica;
	private String cuentas_maestras_salud;
	private String fecha_conciliacion;
	private String saldo_conciliacion;
	private String observ_conciliacion;
	private String mostrarf1001;
	private String ind_agente_retencion;
	private String ind_sujeto_retencion;
	private String codigo_fut;
	private String naturaleza_cgn;
	private String mostrar_en_flujo;
	private String contraprestacion;
	private String concepto_flujo_cgn;
	private String mostrar_en_flujo_cgn;
	private String debito_reversion_det_actual;
	private String credito_reversion_det_actual;
	private String debito_reversion_det_anterior;
	private String credito_reversion_det_anterior;
	private String cod_equi_cartera;
	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNaturaleza() {
		return naturaleza;
	}
	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
	}
	public String getMovimiento() {
		return movimiento;
	}
	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}
	public String getMan_cen_cto() {
		return man_cen_cto;
	}
	public void setMan_cen_cto(String man_cen_cto) {
		this.man_cen_cto = man_cen_cto;
	}
	public String getMan_aux_ter() {
		return man_aux_ter;
	}
	public void setMan_aux_ter(String man_aux_ter) {
		this.man_aux_ter = man_aux_ter;
	}
	public String getMan_aux_gen() {
		return man_aux_gen;
	}
	public void setMan_aux_gen(String man_aux_gen) {
		this.man_aux_gen = man_aux_gen;
	}
	public String getMan_aux_ref() {
		return man_aux_ref;
	}
	public void setMan_aux_ref(String man_aux_ref) {
		this.man_aux_ref = man_aux_ref;
	}
	public String getMan_aux_fue() {
		return man_aux_fue;
	}
	public void setMan_aux_fue(String man_aux_fue) {
		this.man_aux_fue = man_aux_fue;
	}
	public String getObliga_tercero() {
		return obliga_tercero;
	}
	public void setObliga_tercero(String obliga_tercero) {
		this.obliga_tercero = obliga_tercero;
	}
	public String getObliga_centro() {
		return obliga_centro;
	}
	public void setObliga_centro(String obliga_centro) {
		this.obliga_centro = obliga_centro;
	}
	public String getObliga_auxiliar() {
		return obliga_auxiliar;
	}
	public void setObliga_auxiliar(String obliga_auxiliar) {
		this.obliga_auxiliar = obliga_auxiliar;
	}
	public String getObliga_referencia() {
		return obliga_referencia;
	}
	public void setObliga_referencia(String obliga_referencia) {
		this.obliga_referencia = obliga_referencia;
	}
	public String getObliga_fuente() {
		return obliga_fuente;
	}
	public void setObliga_fuente(String obliga_fuente) {
		this.obliga_fuente = obliga_fuente;
	}
	public String getDinamica() {
		return dinamica;
	}
	public void setDinamica(String dinamica) {
		this.dinamica = dinamica;
	}
	public String getPresupuesto_anual() {
		return presupuesto_anual;
	}
	public void setPresupuesto_anual(String presupuesto_anual) {
		this.presupuesto_anual = presupuesto_anual;
	}
	public String getCorriente() {
		return corriente;
	}
	public void setCorriente(String corriente) {
		this.corriente = corriente;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public String getClasecuenta() {
		return clasecuenta;
	}
	public void setClasecuenta(String clasecuenta) {
		this.clasecuenta = clasecuenta;
	}
	public String getBloqueacuenta() {
		return bloqueacuenta;
	}
	public void setBloqueacuenta(String bloqueacuenta) {
		this.bloqueacuenta = bloqueacuenta;
	}
	public String getSaldoinicial() {
		return saldoinicial;
	}
	public void setSaldoinicial(String saldoinicial) {
		this.saldoinicial = saldoinicial;
	}
	public String getSaldo0() {
		return saldo0;
	}
	public void setSaldo0(String saldo0) {
		this.saldo0 = saldo0;
	}
	public String getSaldo1() {
		return saldo1;
	}
	public void setSaldo1(String saldo1) {
		this.saldo1 = saldo1;
	}
	public String getSaldo2() {
		return saldo2;
	}
	public void setSaldo2(String saldo2) {
		this.saldo2 = saldo2;
	}
	public String getSaldo3() {
		return saldo3;
	}
	public void setSaldo3(String saldo3) {
		this.saldo3 = saldo3;
	}
	public String getSaldo4() {
		return saldo4;
	}
	public void setSaldo4(String saldo4) {
		this.saldo4 = saldo4;
	}
	public String getSaldo5() {
		return saldo5;
	}
	public void setSaldo5(String saldo5) {
		this.saldo5 = saldo5;
	}
	public String getSaldo6() {
		return saldo6;
	}
	public void setSaldo6(String saldo6) {
		this.saldo6 = saldo6;
	}
	public String getSaldo7() {
		return saldo7;
	}
	public void setSaldo7(String saldo7) {
		this.saldo7 = saldo7;
	}
	public String getSaldo8() {
		return saldo8;
	}
	public void setSaldo8(String saldo8) {
		this.saldo8 = saldo8;
	}
	public String getSaldo9() {
		return saldo9;
	}
	public void setSaldo9(String saldo9) {
		this.saldo9 = saldo9;
	}
	public String getSaldo10() {
		return saldo10;
	}
	public void setSaldo10(String saldo10) {
		this.saldo10 = saldo10;
	}
	public String getSaldo11() {
		return saldo11;
	}
	public void setSaldo11(String saldo11) {
		this.saldo11 = saldo11;
	}
	public String getSaldo12() {
		return saldo12;
	}
	public void setSaldo12(String saldo12) {
		this.saldo12 = saldo12;
	}
	public String getSaldo13() {
		return saldo13;
	}
	public void setSaldo13(String saldo13) {
		this.saldo13 = saldo13;
	}
	public String getNeto0() {
		return neto0;
	}
	public void setNeto0(String neto0) {
		this.neto0 = neto0;
	}
	public String getNeto1() {
		return neto1;
	}
	public void setNeto1(String neto1) {
		this.neto1 = neto1;
	}
	public String getNeto2() {
		return neto2;
	}
	public void setNeto2(String neto2) {
		this.neto2 = neto2;
	}
	public String getNeto3() {
		return neto3;
	}
	public void setNeto3(String neto3) {
		this.neto3 = neto3;
	}
	public String getNeto4() {
		return neto4;
	}
	public void setNeto4(String neto4) {
		this.neto4 = neto4;
	}
	public String getNeto5() {
		return neto5;
	}
	public void setNeto5(String neto5) {
		this.neto5 = neto5;
	}
	public String getNeto6() {
		return neto6;
	}
	public void setNeto6(String neto6) {
		this.neto6 = neto6;
	}
	public String getNeto7() {
		return neto7;
	}
	public void setNeto7(String neto7) {
		this.neto7 = neto7;
	}
	public String getNeto8() {
		return neto8;
	}
	public void setNeto8(String neto8) {
		this.neto8 = neto8;
	}
	public String getNeto9() {
		return neto9;
	}
	public void setNeto9(String neto9) {
		this.neto9 = neto9;
	}
	public String getNeto10() {
		return neto10;
	}
	public void setNeto10(String neto10) {
		this.neto10 = neto10;
	}
	public String getNeto11() {
		return neto11;
	}
	public void setNeto11(String neto11) {
		this.neto11 = neto11;
	}
	public String getNeto12() {
		return neto12;
	}
	public void setNeto12(String neto12) {
		this.neto12 = neto12;
	}
	public String getNeto13() {
		return neto13;
	}
	public void setNeto13(String neto13) {
		this.neto13 = neto13;
	}
	public String getDebito0() {
		return debito0;
	}
	public void setDebito0(String debito0) {
		this.debito0 = debito0;
	}
	public String getDebito1() {
		return debito1;
	}
	public void setDebito1(String debito1) {
		this.debito1 = debito1;
	}
	public String getDebito2() {
		return debito2;
	}
	public void setDebito2(String debito2) {
		this.debito2 = debito2;
	}
	public String getDebito3() {
		return debito3;
	}
	public void setDebito3(String debito3) {
		this.debito3 = debito3;
	}
	public String getDebito4() {
		return debito4;
	}
	public void setDebito4(String debito4) {
		this.debito4 = debito4;
	}
	public String getDebito5() {
		return debito5;
	}
	public void setDebito5(String debito5) {
		this.debito5 = debito5;
	}
	public String getDebito6() {
		return debito6;
	}
	public void setDebito6(String debito6) {
		this.debito6 = debito6;
	}
	public String getDebito7() {
		return debito7;
	}
	public void setDebito7(String debito7) {
		this.debito7 = debito7;
	}
	public String getDebito8() {
		return debito8;
	}
	public void setDebito8(String debito8) {
		this.debito8 = debito8;
	}
	public String getDebito9() {
		return debito9;
	}
	public void setDebito9(String debito9) {
		this.debito9 = debito9;
	}
	public String getDebito10() {
		return debito10;
	}
	public void setDebito10(String debito10) {
		this.debito10 = debito10;
	}
	public String getDebito11() {
		return debito11;
	}
	public void setDebito11(String debito11) {
		this.debito11 = debito11;
	}
	public String getDebito12() {
		return debito12;
	}
	public void setDebito12(String debito12) {
		this.debito12 = debito12;
	}
	public String getDebito13() {
		return debito13;
	}
	public void setDebito13(String debito13) {
		this.debito13 = debito13;
	}
	public String getCredito0() {
		return credito0;
	}
	public void setCredito0(String credito0) {
		this.credito0 = credito0;
	}
	public String getCredito1() {
		return credito1;
	}
	public void setCredito1(String credito1) {
		this.credito1 = credito1;
	}
	public String getCredito2() {
		return credito2;
	}
	public void setCredito2(String credito2) {
		this.credito2 = credito2;
	}
	public String getCredito3() {
		return credito3;
	}
	public void setCredito3(String credito3) {
		this.credito3 = credito3;
	}
	public String getCredito4() {
		return credito4;
	}
	public void setCredito4(String credito4) {
		this.credito4 = credito4;
	}
	public String getCredito5() {
		return credito5;
	}
	public void setCredito5(String credito5) {
		this.credito5 = credito5;
	}
	public String getCredito6() {
		return credito6;
	}
	public void setCredito6(String credito6) {
		this.credito6 = credito6;
	}
	public String getCredito7() {
		return credito7;
	}
	public void setCredito7(String credito7) {
		this.credito7 = credito7;
	}
	public String getCredito8() {
		return credito8;
	}
	public void setCredito8(String credito8) {
		this.credito8 = credito8;
	}
	public String getCredito9() {
		return credito9;
	}
	public void setCredito9(String credito9) {
		this.credito9 = credito9;
	}
	public String getCredito10() {
		return credito10;
	}
	public void setCredito10(String credito10) {
		this.credito10 = credito10;
	}
	public String getCredito11() {
		return credito11;
	}
	public void setCredito11(String credito11) {
		this.credito11 = credito11;
	}
	public String getCredito12() {
		return credito12;
	}
	public void setCredito12(String credito12) {
		this.credito12 = credito12;
	}
	public String getCredito13() {
		return credito13;
	}
	public void setCredito13(String credito13) {
		this.credito13 = credito13;
	}
	public String getAjuste0() {
		return ajuste0;
	}
	public void setAjuste0(String ajuste0) {
		this.ajuste0 = ajuste0;
	}
	public String getAjuste1() {
		return ajuste1;
	}
	public void setAjuste1(String ajuste1) {
		this.ajuste1 = ajuste1;
	}
	public String getAjuste2() {
		return ajuste2;
	}
	public void setAjuste2(String ajuste2) {
		this.ajuste2 = ajuste2;
	}
	public String getAjuste3() {
		return ajuste3;
	}
	public void setAjuste3(String ajuste3) {
		this.ajuste3 = ajuste3;
	}
	public String getAjuste4() {
		return ajuste4;
	}
	public void setAjuste4(String ajuste4) {
		this.ajuste4 = ajuste4;
	}
	public String getAjuste5() {
		return ajuste5;
	}
	public void setAjuste5(String ajuste5) {
		this.ajuste5 = ajuste5;
	}
	public String getAjuste6() {
		return ajuste6;
	}
	public void setAjuste6(String ajuste6) {
		this.ajuste6 = ajuste6;
	}
	public String getAjuste7() {
		return ajuste7;
	}
	public void setAjuste7(String ajuste7) {
		this.ajuste7 = ajuste7;
	}
	public String getAjuste8() {
		return ajuste8;
	}
	public void setAjuste8(String ajuste8) {
		this.ajuste8 = ajuste8;
	}
	public String getAjuste9() {
		return ajuste9;
	}
	public void setAjuste9(String ajuste9) {
		this.ajuste9 = ajuste9;
	}
	public String getAjuste10() {
		return ajuste10;
	}
	public void setAjuste10(String ajuste10) {
		this.ajuste10 = ajuste10;
	}
	public String getAjuste11() {
		return ajuste11;
	}
	public void setAjuste11(String ajuste11) {
		this.ajuste11 = ajuste11;
	}
	public String getAjuste12() {
		return ajuste12;
	}
	public void setAjuste12(String ajuste12) {
		this.ajuste12 = ajuste12;
	}
	public String getAjuste13() {
		return ajuste13;
	}
	public void setAjuste13(String ajuste13) {
		this.ajuste13 = ajuste13;
	}
	public String getGeneradesembolso() {
		return generadesembolso;
	}
	public void setGeneradesembolso(String generadesembolso) {
		this.generadesembolso = generadesembolso;
	}
	public String getPorcretencion() {
		return porcretencion;
	}
	public void setPorcretencion(String porcretencion) {
		this.porcretencion = porcretencion;
	}
	public String getCreditoexterno() {
		return creditoexterno;
	}
	public void setCreditoexterno(String creditoexterno) {
		this.creditoexterno = creditoexterno;
	}
	public String getPasarsaldo() {
		return pasarsaldo;
	}
	public void setPasarsaldo(String pasarsaldo) {
		this.pasarsaldo = pasarsaldo;
	}
	public String getCod_equiv() {
		return cod_equiv;
	}
	public void setCod_equiv(String cod_equiv) {
		this.cod_equiv = cod_equiv;
	}
	public String getTransaccional5544() {
		return transaccional5544;
	}
	public void setTransaccional5544(String transaccional5544) {
		this.transaccional5544 = transaccional5544;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public String getFormatoegreso() {
		return formatoegreso;
	}
	public void setFormatoegreso(String formatoegreso) {
		this.formatoegreso = formatoegreso;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getPermiteconsolidar() {
		return permiteconsolidar;
	}
	public void setPermiteconsolidar(String permiteconsolidar) {
		this.permiteconsolidar = permiteconsolidar;
	}
	public String getMan_fact_arrendamiento() {
		return man_fact_arrendamiento;
	}
	public void setMan_fact_arrendamiento(String man_fact_arrendamiento) {
		this.man_fact_arrendamiento = man_fact_arrendamiento;
	}
	public String getNotransaccional5544() {
		return notransaccional5544;
	}
	public void setNotransaccional5544(String notransaccional5544) {
		this.notransaccional5544 = notransaccional5544;
	}
	public String getNoreportarreciprocas() {
		return noreportarreciprocas;
	}
	public void setNoreportarreciprocas(String noreportarreciprocas) {
		this.noreportarreciprocas = noreportarreciprocas;
	}
	public String getTerceroequivalentereciprocas() {
		return terceroequivalentereciprocas;
	}
	public void setTerceroequivalentereciprocas(String terceroequivalentereciprocas) {
		this.terceroequivalentereciprocas = terceroequivalentereciprocas;
	}
	public String getConceptoex() {
		return conceptoex;
	}
	public void setConceptoex(String conceptoex) {
		this.conceptoex = conceptoex;
	}
	public String getCuenta_bancaria() {
		return cuenta_bancaria;
	}
	public void setCuenta_bancaria(String cuenta_bancaria) {
		this.cuenta_bancaria = cuenta_bancaria;
	}
	public String getTerceroex() {
		return terceroex;
	}
	public void setTerceroex(String terceroex) {
		this.terceroex = terceroex;
	}
	public String getSucursalex() {
		return sucursalex;
	}
	public void setSucursalex(String sucursalex) {
		this.sucursalex = sucursalex;
	}
	public String getTipodescuento_sia() {
		return tipodescuento_sia;
	}
	public void setTipodescuento_sia(String tipodescuento_sia) {
		this.tipodescuento_sia = tipodescuento_sia;
	}
	public String getCodbanco_sia() {
		return codbanco_sia;
	}
	public void setCodbanco_sia(String codbanco_sia) {
		this.codbanco_sia = codbanco_sia;
	}
	public String getNumerocuenta_sia() {
		return numerocuenta_sia;
	}
	public void setNumerocuenta_sia(String numerocuenta_sia) {
		this.numerocuenta_sia = numerocuenta_sia;
	}
	public String getDestinocuentabanco() {
		return destinocuentabanco;
	}
	public void setDestinocuentabanco(String destinocuentabanco) {
		this.destinocuentabanco = destinocuentabanco;
	}
	public String getCodbanco_serec() {
		return codbanco_serec;
	}
	public void setCodbanco_serec(String codbanco_serec) {
		this.codbanco_serec = codbanco_serec;
	}
	public String getNumerocuenta_serec() {
		return numerocuenta_serec;
	}
	public void setNumerocuenta_serec(String numerocuenta_serec) {
		this.numerocuenta_serec = numerocuenta_serec;
	}
	public String getCuenta_pptal() {
		return cuenta_pptal;
	}
	public void setCuenta_pptal(String cuenta_pptal) {
		this.cuenta_pptal = cuenta_pptal;
	}
	public String getEsoficial() {
		return esoficial;
	}
	public void setEsoficial(String esoficial) {
		this.esoficial = esoficial;
	}
	public String getFuente() {
		return fuente;
	}
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
	public String getEquivpr_debito() {
		return equivpr_debito;
	}
	public void setEquivpr_debito(String equivpr_debito) {
		this.equivpr_debito = equivpr_debito;
	}
	public String getEquivpr_credito() {
		return equivpr_credito;
	}
	public void setEquivpr_credito(String equivpr_credito) {
		this.equivpr_credito = equivpr_credito;
	}
	public String getIvaex() {
		return ivaex;
	}
	public void setIvaex(String ivaex) {
		this.ivaex = ivaex;
	}
	public String getRetepracticada() {
		return retepracticada;
	}
	public void setRetepracticada(String retepracticada) {
		this.retepracticada = retepracticada;
	}
	public String getReteasumida() {
		return reteasumida;
	}
	public void setReteasumida(String reteasumida) {
		this.reteasumida = reteasumida;
	}
	public String getIvacomun() {
		return ivacomun;
	}
	public void setIvacomun(String ivacomun) {
		this.ivacomun = ivacomun;
	}
	public String getIvasimplificado() {
		return ivasimplificado;
	}
	public void setIvasimplificado(String ivasimplificado) {
		this.ivasimplificado = ivasimplificado;
	}
	public String getExdistrital() {
		return exdistrital;
	}
	public void setExdistrital(String exdistrital) {
		this.exdistrital = exdistrital;
	}
	public String getId_niif() {
		return id_niif;
	}
	public void setId_niif(String id_niif) {
		this.id_niif = id_niif;
	}
	public String getCodigo_niif() {
		return codigo_niif;
	}
	public void setCodigo_niif(String codigo_niif) {
		this.codigo_niif = codigo_niif;
	}
	public String getMan_distri_ccosto() {
		return man_distri_ccosto;
	}
	public void setMan_distri_ccosto(String man_distri_ccosto) {
		this.man_distri_ccosto = man_distri_ccosto;
	}
	public String getReteica() {
		return reteica;
	}
	public void setReteica(String reteica) {
		this.reteica = reteica;
	}
	public String getCree_practicada() {
		return cree_practicada;
	}
	public void setCree_practicada(String cree_practicada) {
		this.cree_practicada = cree_practicada;
	}
	public String getCree_asumida() {
		return cree_asumida;
	}
	public void setCree_asumida(String cree_asumida) {
		this.cree_asumida = cree_asumida;
	}
	public String getCcbalance() {
		return ccbalance;
	}
	public void setCcbalance(String ccbalance) {
		this.ccbalance = ccbalance;
	}
	public String getReportasaldoreciprocas() {
		return reportasaldoreciprocas;
	}
	public void setReportasaldoreciprocas(String reportasaldoreciprocas) {
		this.reportasaldoreciprocas = reportasaldoreciprocas;
	}
	public String getMen() {
		return men;
	}
	public void setMen(String men) {
		this.men = men;
	}
	public String getVerificar_mov() {
		return verificar_mov;
	}
	public void setVerificar_mov(String verificar_mov) {
		this.verificar_mov = verificar_mov;
	}
	public String getCod_flujocaja() {
		return cod_flujocaja;
	}
	public void setCod_flujocaja(String cod_flujocaja) {
		this.cod_flujocaja = cod_flujocaja;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public String getAplica_deterioro() {
		return aplica_deterioro;
	}
	public void setAplica_deterioro(String aplica_deterioro) {
		this.aplica_deterioro = aplica_deterioro;
	}
	public String getDeb_reco_det() {
		return deb_reco_det;
	}
	public void setDeb_reco_det(String deb_reco_det) {
		this.deb_reco_det = deb_reco_det;
	}
	public String getCre_reco_det() {
		return cre_reco_det;
	}
	public void setCre_reco_det(String cre_reco_det) {
		this.cre_reco_det = cre_reco_det;
	}
	public String getDeb_caus_det() {
		return deb_caus_det;
	}
	public void setDeb_caus_det(String deb_caus_det) {
		this.deb_caus_det = deb_caus_det;
	}
	public String getCre_caus_det() {
		return cre_caus_det;
	}
	public void setCre_caus_det(String cre_caus_det) {
		this.cre_caus_det = cre_caus_det;
	}
	public String getDeb_rec_det() {
		return deb_rec_det;
	}
	public void setDeb_rec_det(String deb_rec_det) {
		this.deb_rec_det = deb_rec_det;
	}
	public String getCre_rec_det() {
		return cre_rec_det;
	}
	public void setCre_rec_det(String cre_rec_det) {
		this.cre_rec_det = cre_rec_det;
	}
	public String getDate_modified() {
		return date_modified;
	}
	public void setDate_modified(String date_modified) {
		this.date_modified = date_modified;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	public String getCheque() {
		return cheque;
	}
	public void setCheque(String cheque) {
		this.cheque = cheque;
	}
	public String getReportar_100() {
		return reportar_100;
	}
	public void setReportar_100(String reportar_100) {
		this.reportar_100 = reportar_100;
	}
	public String getTercero_reciprocas() {
		return tercero_reciprocas;
	}
	public void setTercero_reciprocas(String tercero_reciprocas) {
		this.tercero_reciprocas = tercero_reciprocas;
	}
	public String getInd_circularunica() {
		return ind_circularunica;
	}
	public void setInd_circularunica(String ind_circularunica) {
		this.ind_circularunica = ind_circularunica;
	}
	public String getCuentas_maestras_salud() {
		return cuentas_maestras_salud;
	}
	public void setCuentas_maestras_salud(String cuentas_maestras_salud) {
		this.cuentas_maestras_salud = cuentas_maestras_salud;
	}
	public String getFecha_conciliacion() {
		return fecha_conciliacion;
	}
	public void setFecha_conciliacion(String fecha_conciliacion) {
		this.fecha_conciliacion = fecha_conciliacion;
	}
	public String getSaldo_conciliacion() {
		return saldo_conciliacion;
	}
	public void setSaldo_conciliacion(String saldo_conciliacion) {
		this.saldo_conciliacion = saldo_conciliacion;
	}
	public String getObserv_conciliacion() {
		return observ_conciliacion;
	}
	public void setObserv_conciliacion(String observ_conciliacion) {
		this.observ_conciliacion = observ_conciliacion;
	}
	public String getMostrarf1001() {
		return mostrarf1001;
	}
	public void setMostrarf1001(String mostrarf1001) {
		this.mostrarf1001 = mostrarf1001;
	}
	public String getInd_agente_retencion() {
		return ind_agente_retencion;
	}
	public void setInd_agente_retencion(String ind_agente_retencion) {
		this.ind_agente_retencion = ind_agente_retencion;
	}
	public String getInd_sujeto_retencion() {
		return ind_sujeto_retencion;
	}
	public void setInd_sujeto_retencion(String ind_sujeto_retencion) {
		this.ind_sujeto_retencion = ind_sujeto_retencion;
	}
	public String getCodigo_fut() {
		return codigo_fut;
	}
	public void setCodigo_fut(String codigo_fut) {
		this.codigo_fut = codigo_fut;
	}
	public String getNaturaleza_cgn() {
		return naturaleza_cgn;
	}
	public void setNaturaleza_cgn(String naturaleza_cgn) {
		this.naturaleza_cgn = naturaleza_cgn;
	}
	public String getMostrar_en_flujo() {
		return mostrar_en_flujo;
	}
	public void setMostrar_en_flujo(String mostrar_en_flujo) {
		this.mostrar_en_flujo = mostrar_en_flujo;
	}
	public String getContraprestacion() {
		return contraprestacion;
	}
	public void setContraprestacion(String contraprestacion) {
		this.contraprestacion = contraprestacion;
	}
	public String getConcepto_flujo_cgn() {
		return concepto_flujo_cgn;
	}
	public void setConcepto_flujo_cgn(String concepto_flujo_cgn) {
		this.concepto_flujo_cgn = concepto_flujo_cgn;
	}
	public String getMostrar_en_flujo_cgn() {
		return mostrar_en_flujo_cgn;
	}
	public void setMostrar_en_flujo_cgn(String mostrar_en_flujo_cgn) {
		this.mostrar_en_flujo_cgn = mostrar_en_flujo_cgn;
	}
	public String getDebito_reversion_det_actual() {
		return debito_reversion_det_actual;
	}
	public void setDebito_reversion_det_actual(String debito_reversion_det_actual) {
		this.debito_reversion_det_actual = debito_reversion_det_actual;
	}
	public String getCredito_reversion_det_actual() {
		return credito_reversion_det_actual;
	}
	public void setCredito_reversion_det_actual(String credito_reversion_det_actual) {
		this.credito_reversion_det_actual = credito_reversion_det_actual;
	}
	public String getDebito_reversion_det_anterior() {
		return debito_reversion_det_anterior;
	}
	public void setDebito_reversion_det_anterior(String debito_reversion_det_anterior) {
		this.debito_reversion_det_anterior = debito_reversion_det_anterior;
	}
	public String getCredito_reversion_det_anterior() {
		return credito_reversion_det_anterior;
	}
	public void setCredito_reversion_det_anterior(String credito_reversion_det_anterior) {
		this.credito_reversion_det_anterior = credito_reversion_det_anterior;
	}
	public String getCod_equi_cartera() {
		return cod_equi_cartera;
	}
	public void setCod_equi_cartera(String cod_equi_cartera) {
		this.cod_equi_cartera = cod_equi_cartera;
	}
	public String getCompania() {
		return compania;
	}
	public void setCompania(String compania) {
		this.compania = compania;
	}
}
