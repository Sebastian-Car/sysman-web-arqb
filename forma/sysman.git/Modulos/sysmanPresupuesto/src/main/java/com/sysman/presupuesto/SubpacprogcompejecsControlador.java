package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.SubpacprogcompejecsControladorEnum;
import com.sysman.presupuesto.enums.SubpacprogcompejecsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 3, 25/06/2016 12:53:49 -- Modificado por dmaldonado
 * @version 4, 24/04/2017 mzanguna -- Refactorizacion y ajustes sonar.
 * @version 5, 24/04/2017 spina Refactorizacion ejb
 * @author asana, @version 6, Se ajusta enum en formulario y se ajusta conexion
 *         dado que se definia pero no se utilizaba.
 * @author gfigueredo
 * @version 6, 07/07/2021, Se añade función {@link #validaDiferencia() para
 * validar los datos de diferencia y credito.
 * @see #validaDiferencia()
 */
@ManagedBean
@ViewScoped

public class SubpacprogcompejecsControlador extends BeanBaseContinuoAcmeImpl {
	private final String compania;

	private String consecutivo;
	private String ano;
	private String tipoComprobante;
	private String numeroComprobante;

	private String subPac;
	private String cuenta;
	private String encabezadoTipo;
	private String txtValor;
	private String totalCredito = "0";
	private String totalDebito = "0";
	private double diferencia = 0;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	private String titulo;
	private String nombreComprobante;
	private int indice;
	private String claseComprobante;
	private boolean bloqDebito;
	private boolean bloqCredito;
	private boolean bloqDebitoCont;
	private boolean bloqCreditoCont;
	private double valor = 0;

	private boolean permiteGuardar = true;

	private boolean inserta = false;

	/**
	 * Creates a new instance of SubpacprogcompejecsControlador
	 */
	public SubpacprogcompejecsControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.SUBPACPROGCOMPEJECS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(SubpacprogcompejecsControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void init() {
		cargarFlash();
		switch (subPac) {
		case "C":
			enumBase = GenericUrlEnum.PACCOMPROMETIDO;
			buscarLlave();
			titulo = idioma.getString("TB_TB881");
			break;
		case "E":
			enumBase = GenericUrlEnum.PACEJECUTADO;
			buscarLlave();
			titulo = idioma.getString("TB_TB882");
			break;
		case "P":

			enumBase = GenericUrlEnum.PACPROGRAMADO;
			buscarLlave();
			titulo = idioma.getString("TG_PAC_PROGRAMADO");
			break;
		default:
			break;
		}
		reasignarOrigen();

		registro = new Registro();
		registro.getCampos().put(SubpacprogcompejecsControladorEnum.MOVDEBITO.getValue(), 0);
		registro.getCampos().put(SubpacprogcompejecsControladorEnum.MOVCREDITO.getValue(), 0);
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	public void cargarFlash() {
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {

			cuenta = (String) parametrosEntrada.get("cuenta");
			consecutivo = (String) parametrosEntrada.get("consecutivo");
			ano = (String) parametrosEntrada.get("ano");
			tipoComprobante = (String) parametrosEntrada.get("tipoComprobante");
			nombreComprobante = (String) parametrosEntrada.get("nombreComprobante");
			numeroComprobante = (String) parametrosEntrada.get("numeroComprobante");

			subPac = (String) parametrosEntrada.get("subPac");
			claseComprobante = (String) parametrosEntrada.get("claseComprobante");
			valor = Double.parseDouble((String) parametrosEntrada.get("valor"));
			if (("C").equals(subPac)) {
				if (("DRO").equals(claseComprobante)) {
					bloqDebito = true;
					bloqCredito = false;
				} else {
					bloqDebito = false;
					bloqCredito = true;
				}
			} else if (("E").equals(subPac)) {
				if (("DRO").equals(claseComprobante) || ("ING").equals(claseComprobante)
						|| ("AIN").equals(claseComprobante)) {
					bloqDebito = true;
					bloqCredito = false;
				} else {
					bloqDebito = false;
					bloqCredito = true;
				}
			} else if (("P").equals(subPac)) {
				if (("DMR").equals(claseComprobante)) {
					bloqDebito = true;
					bloqCredito = false;
				} else {
					bloqDebito = false;
					bloqCredito = true;
				}
			}

		} else {
			SessionUtil.redireccionarMenuPermisos();
		}

		encabezadoTipo = nombreComprobante + " No. - ";
	}

	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(SubpacprogcompejecsControladorEnum.ANO.getValue(), ano);
		parametrosListado.put(SubpacprogcompejecsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoComprobante);
		parametrosListado.put(SubpacprogcompejecsControladorEnum.NUMCPTE.getValue(), numeroComprobante);
		parametrosListado.put(SubpacprogcompejecsControladorEnum.CONSEC.getValue(), consecutivo);
		parametrosListado.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

	}

	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
		if (("C").equals(subPac)) {
			if (("DRO").equals(claseComprobante)) {
				bloqDebitoCont = true;
				bloqCreditoCont = false;
			} else {
				bloqDebitoCont = false;
				bloqCreditoCont = true;
			}
		} else if (("E").equals(subPac)) {
			if (("DRO").equals(claseComprobante) || ("ING").equals(claseComprobante)
					|| ("AIN").equals(claseComprobante)) {
				bloqDebitoCont = true;
				bloqCreditoCont = false;
			} else {
				bloqDebitoCont = false;
				bloqCreditoCont = true;
			}
		} else if (("P").equals(subPac)) {
			if (("DMR").equals(claseComprobante)) {
				bloqDebitoCont = true;
				bloqCreditoCont = false;
			} else {
				bloqDebitoCont = false;
				bloqCreditoCont = true;
			}
		}

	}

	/**
	 * Metodo que se ejecuta cuando se filtra desde la grilla
	 */

	public void ejecutaractualizarTotales() {
		cargarTotales();
	}

	public void cargarTotales() {

		Map<String, Object> parametros;
		if (SysmanFunciones.nvl(listaInicial, "") != "") {
			parametros = listaInicial.getFilters();

		} else {
			parametros = new HashMap<>();
		}
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(SubpacprogcompejecsControladorEnum.ANO.getValue(), ano);
		parametros.put(SubpacprogcompejecsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoComprobante);
		parametros.put(SubpacprogcompejecsControladorEnum.NUMCPTE.getValue(), numeroComprobante);
		parametros.put(SubpacprogcompejecsControladorEnum.CONSEC.getValue(), consecutivo);
		parametros.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

		parametros.put("FECHA", parametros.get("campos['FECHA']"));
		parametros.remove("campos['FECHA']");

		parametros.put("MOV_DEBITO", parametros.get("campos['MOV_DEBITO']"));
		parametros.remove("campos['MOV_DEBITO']");

		parametros.put("MOV_CREDITO", parametros.get("campos['MOV_CREDITO']"));
		parametros.remove("campos['MOV_CREDITO']");

		String urlServicio = "";
		switch (subPac) {
		case "C":
			// PACCOMPROMETIDO
			urlServicio = SubpacprogcompejecsControladorUrlEnum.URL8535.getValue();
			break;
		case "E":
			// PACEJECUTADO
			urlServicio = SubpacprogcompejecsControladorUrlEnum.URL8533.getValue();
			break;
		case "P":
			urlServicio = SubpacprogcompejecsControladorUrlEnum.URL8534.getValue();
			break;
		default:
			break;
		}

		try {

			HashMap<String, Object> regAux;

			UrlBean urlReg = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlServicio);

			regAux = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), parametros).getFields();

			double deb = Double.parseDouble(regAux.get("SUMADEBITO").toString());
			double cre = Double.parseDouble(regAux.get("SUMACREDITO").toString());

			DecimalFormat df = new DecimalFormat("#,##0.00");
			totalDebito = df.format(deb);
			totalCredito = df.format(cre);
			diferencia = deb - cre;

		}

		catch (SystemException e) {

			Logger.getLogger(SubpacprogcompejecsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}

	/**
	 * Método encargado de validar el total de diferencia con respecto a los
	 * créditos Cuando se inserta un registro, se realiza la validación pero se
	 * permite guardar los datos. El mensaje es informativo. Cuando se edita un
	 * registro, se realiza la validación pero no se permite guardar los datos. El
	 * mensaje es de alerta.
	 * 
	 */
	public void validaDiferencia() {
		double deb = 0;
		double cre = 0;
		for (int i = 0; i <= listaInicial.getDatasource().size() - 1; i++) {
			deb += +Double.parseDouble(listaInicial.getDatasource().get(i % 10).getCampos()
					.get(SubpacprogcompejecsControladorEnum.MOVDEBITO.getValue()).toString());

			cre += Double.parseDouble(listaInicial.getDatasource().get(i % 10).getCampos()
					.get(SubpacprogcompejecsControladorEnum.MOVCREDITO.getValue()).toString());
		}

		DecimalFormat df = new DecimalFormat("#,##0.00");

		double dif = 0;
		permiteGuardar = true;
		String debitoIngresa;
		String creditoIngresa;
		if (inserta) {
			debitoIngresa = registro.getCampos().get(SubpacprogcompejecsControladorEnum.MOVDEBITO.getValue())
					.toString();
			creditoIngresa = registro.getCampos().get(SubpacprogcompejecsControladorEnum.MOVCREDITO.getValue())
					.toString();
			dif = (deb - cre) + (Double.parseDouble(debitoIngresa) - Double.parseDouble(creditoIngresa));
			inserta = false;

			if (Math.round(dif) > Math.round(valor)) {
				String mensaje = "El Valor Total de los Compromisos: " + df.format(dif)
				+ " debe ser igual al Valor del Registro: " + df.format(valor);
				JsfUtil.agregarMensajeInformativo(mensaje);
			}
		} else {
			dif = deb - cre;

			if (Math.round(dif) != Math.round(valor)) {
				permiteGuardar = false;
				String mensaje = "El Valor Total de los Compromisos: " + df.format(dif)
				+ " debe ser igual al Valor del Registro: " + df.format(valor);
				JsfUtil.agregarMensajeAlerta(mensaje);
			}
		}

	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>

	public void cambiarMovdebitoC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// <CODIGO_DESARROLLADO>
		double debito = Double.parseDouble(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(SubpacprogcompejecsControladorEnum.MOVDEBITO.getValue()).toString());
		if (Double.doubleToRawLongBits(debito) != 0) {
			bloqCreditoCont = true;
		} else {
			bloqCreditoCont = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMovcreditoC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// <CODIGO_DESARROLLADO>
		double credito = Double.parseDouble(listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(SubpacprogcompejecsControladorEnum.MOVCREDITO.getValue()).toString());
		if (Double.doubleToRawLongBits(credito) != 0) {
			bloqDebitoCont = true;
		} else {
			bloqDebitoCont = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR902-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore
		 * Me!txtValor = Nz(Valor, 0) End Sub
		 */
		cargarTotales();
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR902-ANTES_INSERTAR Private Sub Form_BeforeInsert(Cancel As Integer)
		 * Anterior End Sub
		 */
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
		registro.getCampos().put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComprobante);
		registro.getCampos().put(GeneralParameterEnum.COMPROBANTE.getName(), numeroComprobante);
		registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(), cuenta);
		registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

		inserta = true;
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		if (("C").equals(subPac)) {
			if (("DRO").equals(claseComprobante)) {
				bloqDebito = true;
				bloqCredito = false;
			} else {
				bloqDebito = false;
				bloqCredito = true;
			}
		} else if (("E").equals(subPac)) {
			if (("DRO").equals(claseComprobante) || ("ING").equals(claseComprobante)
					|| ("AIN").equals(claseComprobante)) {
				bloqDebito = true;
				bloqCredito = false;
			} else {
				bloqDebito = false;
				bloqCredito = true;
			}
		} else if (("P").equals(subPac)) {
			if (("DMR").equals(claseComprobante)) {
				bloqDebito = true;
				bloqCredito = false;
			} else {
				bloqDebito = false;
				bloqCredito = true;
			}
		}
		/*
		 * FR902-DESPUES_INSERTAR Private Sub Form_AfterInsert() Anterior End Sub
		 */
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		validaDiferencia();
		// </CODIGO_DESARROLLADO>
		return permiteGuardar;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		/*
		 * FR902-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate() Dim iActual As
		 * Integer Dim XX As Boolean Dim strClaseMov As String Dim dbactual As
		 * DAO.Database Dim rs As DAO.Recordset Dim qdf As QueryDef Dim SALDO As Double
		 * Dim strobliga As String Dim DifAnterior As Double Dim DifActual As Double
		 * DifAnterior = vAnterior(1) - vAnterior(2) DifActual = Me!Mov_debito -
		 * Me!Mov_credito strobliga = Nz(TraerParametro(
		 * "CONTROLAR PAC PROGRAMADO CONTRA PAC APROPIADO", Getcompany()), "NO") Set
		 * dbactual = CurrentDb() 'Sobregiro Solamente del mes ' If strObliga = "SI"
		 * Then ' Set qdf = dbActual.QueryDefs("ResumenDeUnMes") '
		 * qdf.Parameters!NoCompania = Getcompany() ' qdf.Parameters!NoAno = GetYear() '
		 * qdf.Parameters!NoMes = Month(Me!Fecha) ' qdf.Parameters!NoCuenta =
		 * GetCuenta() ' Set rs = qdf.OpenRecordset ' Dim DifAnterior As Double ' Dim
		 * DifActual As Double ' DifAnterior = vAnterior(1) - vAnterior(2) ' DifActual =
		 * Me!Mov_debito - Me!Mov_credito ' Saldo = rs!Pac_Apropiado -
		 * (rs!Pac_Programado - DifAnterior + DifActual) ' If Saldo < 0 Then ' msgbox
		 * "Con esta programaciÃ³n queda sobregirado en el PAC Programado para el mes de "
		 * & NombreDeMes(Month(Me!Fecha)) & " en: " & format(Saldo * -1,
		 * "###,###,###,###.00"), vbInformation + vbOKOnly, "Advertencia..." ' End If '
		 * ' Sobregiro acumulado hasta el mes ' Set qdf =
		 * dbActual.QueryDefs("cp_resumen") ' qdf.Parameters!NoCompania = Getcompany() '
		 * qdf.Parameters!NoAno = GetYear() ' qdf.Parameters!NoMes = Month(Me!Fecha) '
		 * qdf.Parameters!NoCuenta = GetCuenta() ' Set rs = qdf.OpenRecordset ' Saldo =
		 * rs!Pac_Apropiado - (rs!Pac_Programado - DifAnterior + DifActual) ' If Saldo <
		 * 0 Then ' msgbox
		 * "Con esta programaciÃ³n queda Sobregirado en el PAC Programado Acumulado a el mes de "
		 * & NombreDeMes(Month(Me!Fecha)) & " en: " & format(Saldo * -1,
		 * "###,###,###,###.00"), vbInformation + vbOKOnly, "Advertencia..." ' End If '
		 * End If iActual = 0 If Year(Me!Fecha) > GetYear() Then strClaseMov =
		 * cnsVigenciaFutura Else strClaseMov = cnsPACProgramado End If If Me.Fecha <>
		 * vAnterior(0) Then XX = ActPto0(dbactual, strClaseMov, Getcompany(),
		 * GetYear(), Month(vAnterior(0)), Me!Cuenta, vAnterior(1), vAnterior(2),
		 * DifAnterior, 0, 0, 0) XX = ActPto0(dbactual, strClaseMov, Getcompany(),
		 * GetYear(), Month(Me!Fecha), Me!Cuenta, 0, 0, 0, Me!Mov_debito,
		 * Me!Mov_credito, DifActual) iActual = iActual + 1 End If If iActual = 0 And
		 * (vAnterior(1) <> Me.Mov_debito Or vAnterior(2) <> Me.Mov_credito) Then XX =
		 * ActPto0(dbactual, strClaseMov, Getcompany(), GetYear(), Month(Me!Fecha),
		 * Me!Cuenta, vAnterior(1), vAnterior(2), DifAnterior, Me!Mov_debito,
		 * Me!Mov_credito, DifActual) End If Anterior If Me.Mov_debito <> 0 Then
		 * Me.Mov_credito.Enabled = False Else Me.Mov_credito.Enabled = True End If If
		 * Me.Mov_credito <> 0 Then Me.Mov_debito.Enabled = False Else
		 * Me.Mov_debito.Enabled = True End If Exit Sub End Sub
		 */
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR902-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As Integer) On Error GoTo
		 * err_Form_Delete If Me.Mov_debito <> 0 Or Me.Mov_credito <> 0 Then Cancel =
		 * True If Me.Mov_debito <> 0 Then MsgBox "Tiene valores en Debito.",
		 * vbInformation + vbOKOnly, "No Se Permite Borrar" Me.Mov_debito.SetFocus End
		 * If If Me.Mov_credito <> 0 Then MsgBox "Tiene valores en Credito.",
		 * vbInformation + vbOKOnly, "No Se Permite Borrar" Me.Mov_credito.SetFocus End
		 * If Exit Sub End If Exit_Form_Delete: Exit Sub err_Form_Delete: ErrorBox Err,
		 * "Eliminar Registro" Resume Exit_Form_Delete End Sub
		 */
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarTotales();
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public void removerCombos() {
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());
		registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
		registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
		registro.getCampos().remove(GeneralParameterEnum.CUENTA.getName());
		registro.getCampos().remove(GeneralParameterEnum.CENTRO_COSTO.getName());
		registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());

	}

	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	@Override
	public void asignarValoresRegistro() {
		registro.getCampos().put(SubpacprogcompejecsControladorEnum.MOVDEBITO.getValue(), 0);
		registro.getCampos().put(SubpacprogcompejecsControladorEnum.MOVCREDITO.getValue(), 0);
	}

	// <SET_GET_ATRIBUTOS>
	public String getNumeroComprobante() {
		return numeroComprobante;
	}

	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getEncabezadoTipo() {
		return encabezadoTipo;
	}

	public void setEncabezadoTipo(String encabezadoTipo) {
		this.encabezadoTipo = encabezadoTipo;
	}

	public String getTxtValor() {
		return txtValor;
	}

	public void setTxtValor(String txtValor) {
		this.txtValor = txtValor;
	}

	public String getTotalCredito() {
		return totalCredito;
	}

	public void setTotalCredito(String totalCredito) {
		this.totalCredito = totalCredito;
	}

	public String getTotalDebito() {
		return totalDebito;
	}

	public void setTotalDebito(String totalDebito) {
		this.totalDebito = totalDebito;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public double getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(double diferencia) {
		this.diferencia = diferencia;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public boolean isBloqDebito() {
		return bloqDebito;
	}

	public void setBloqDebito(boolean bloqDebito) {
		this.bloqDebito = bloqDebito;
	}

	public boolean isBloqCredito() {
		return bloqCredito;
	}

	public void setBloqCredito(boolean bloqCredito) {
		this.bloqCredito = bloqCredito;
	}

	public boolean isBloqDebitoCont() {
		return bloqDebitoCont;
	}

	public void setBloqDebitoCont(boolean bloqDebitoCont) {
		this.bloqDebitoCont = bloqDebitoCont;
	}

	public boolean isBloqCreditoCont() {
		return bloqCreditoCont;
	}

	public void setBloqCreditoCont(boolean bloqCreditoCont) {
		this.bloqCreditoCont = bloqCreditoCont;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
