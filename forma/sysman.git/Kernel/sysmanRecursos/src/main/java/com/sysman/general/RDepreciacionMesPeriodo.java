package com.sysman.general;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.RDepreciacionMesPeriodoEnum;
import com.sysman.general.enums.RDepreciacionMesPeriodoUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodrigueza
 * @version 1, 25/02/2016
 * 
 * @author ybecerra
 * @version 2, 12/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum,
 *          para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class RDepreciacionMesPeriodo extends BeanBaseModal {
	/**
	 * variable que almacena la compañia
	 */
	private final String compania;
	/**
	 * variable que alamcena el modulo
	 */
	private final String modulo;
	/**
	 * variable que almacena el nombre del codigo elemento
	 */
	private final String cCodigoElemento;
	/**
	 * variable que alamcena la condicion que va para la consulta general
	 */
	private final String cCondicion;
	/**
	 * variable que alamcena el gurpo
	 */
	private final String cGrupo;
	/**
	 * variable que alamcena la opcion de menu 1
	 */
	private final String cOpcionMenu1;
	/**
	 * variable que alamcena la opcion de menu 2
	 */
	private final String cOpcionMenu2;
	/**
	 * variable que alamcena la opcion de menu 3
	 */
	private final String cOpcionMenu3;
	/**
	 * variable que alamcena la opcion de menu 4
	 */
	private final String cOpcionMenu4;
	/**
	 * variable que alamcena el parametro
	 */
	private final String cParametro;
	/**
	 * variable que alamcena la opcion en ubicacion pude ser bodega servicio
	 * responsabilidades inservibles
	 */
	private String ubicacion;
	/**
	 * variable que alamcena la opcion en el grupo organizar por agrupado detallado
	 */
	private String agrupado;
	/**
	 * variable que alacena cuando se selecciona un registro del cobobox inicial
	 */
	private String elementoInicial;
	/**
	 * variable que alacena cuando se selecciona un registro del cobobox final
	 */
	private String elementoFinal;
	/**
	 * varible que alamcena el titulo
	 */
	private String titulo;
	/**
	 * variable que almacena el año seleccionado
	 */
	private int ano;
	/**
	 * variable que almacena el mes seleccionado
	 */
	private int mes;
	/**
	 * variable que almacena el estado ubicacion
	 */
	private boolean ubicacionVisible;
	/**
	 * variable que almacena el estado elemento inicial
	 */
	private boolean elementoInicialVisible;
	/**
	 * variable que almacena el estado elemento final
	 */
	private boolean elementoFinalVisible;
	/**
	 * variable que almacena el nombre seleccionado en el combobox inicial nombre
	 * inicial
	 */
	private String nombreElementoInicial;
	/**
	 * variable que almacena el nombre seleccionado en el combobox final nombre
	 * final
	 */
	private String nombreElementoFinal;
	/**
	 * variable que alamcena la inforamcion para descargar (reporte)
	 */
	private StreamedContent archivoDescarga;
	/**
	 * lista los años
	 */
	private List<Registro> listaAno;
	/**
	 * combobox inicial lista el codigo y nombre
	 */
	private RegistroDataModelImpl listaCodigoElementoInicial;
	/**
	 * combobox final lista el codigo y nombre
	 */
	private RegistroDataModelImpl listaCodigoElementoFinal;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Creates a new instance of RDepreciacionMesPeriodo
	 */
	public RDepreciacionMesPeriodo() {
		super();
		numFormulario = GeneralCodigoFormaEnum.R_DEPRECIACION_MES_PERIODO.getCodigo();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cCodigoElemento = RDepreciacionMesPeriodoEnum.CODIGOELEMENTO.getValue();
		cCondicion = RDepreciacionMesPeriodoEnum.CONDICION.getValue();
		cGrupo = RDepreciacionMesPeriodoEnum.GRUPO.getValue();
		cParametro = RDepreciacionMesPeriodoEnum.PARAMETRO.getValue();
		cOpcionMenu1 = RDepreciacionMesPeriodoEnum.OPCION1.getValue();
		cOpcionMenu2 = RDepreciacionMesPeriodoEnum.OPCION2.getValue();
		cOpcionMenu3 = RDepreciacionMesPeriodoEnum.OPCION3.getValue();
		cOpcionMenu4 = RDepreciacionMesPeriodoEnum.OPCION4.getValue();
		try {
			validarPermisos();
		} catch (Exception ex) {
			SessionUtil.redireccionarMenuPermisos();
			Logger.getLogger(RDepreciacionMesPeriodo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * metodo que se llama cuando se incializa el formulario
	 */
	@PostConstruct
	public void inicializar() {
		cargarListaAno();
		cargarListaCodigoElementoInicial();

		abrirFormulario();
	}

	/**
	 * metodo que valida si las variable obligatorias son diferentes de null, vacio
	 * y 0
	 * 
	 * @return
	 */
	private boolean validarVacio() {
		if ((ano != 0) || mes != 0 || !SysmanFunciones.validarVariableVacio(elementoInicial)
				|| !SysmanFunciones.validarVariableVacio(elementoFinal)) {
			return true;
		}
		return false;
	}

	/**
	 * metodo que carga la lista de años
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													RDepreciacionMesPeriodoUrlEnum.URL3523.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que carga las lista de codigos y nombre inicial
	 */
	public void cargarListaCodigoElementoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(RDepreciacionMesPeriodoUrlEnum.URL3953.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CLASE.getName(), RDepreciacionMesPeriodoEnum.CADENA.getValue());

		listaCodigoElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}

	/**
	 * metodo que carga las lista de codigos y nombre final
	 */
	public void cargarListaCodigoElementoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(RDepreciacionMesPeriodoUrlEnum.URL4525.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CLASE.getName(), RDepreciacionMesPeriodoEnum.CADENA.getValue());
		param.put(RDepreciacionMesPeriodoEnum.ELEMENTOINICIAL.getValue(), elementoInicial);

		listaCodigoElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}

	/**
	 * metodo que se llama al cambiar el elemento incial
	 */
	public void cambiarElementoInicial() {
		// heredado del bean base
	}

	/**
	 * metodo que es llamado cuando se presiona el boton pdf
	 */
	public void oprimirPresentar() {
		archivoDescarga = null;
		if (validarVacio()) {
			generarReporte(ReportesBean.FORMATOS.PDF);
		}
	}

	/**
	 * metodo que es llamado cuando se presiona el boton excel
	 */
	public void oprimirExcel() {
		archivoDescarga = null;
		if (validarVacio()) {
			generarReporte(ReportesBean.FORMATOS.EXCEL);
		}
	}

	/**
	 * metodo que se llama al cambiar el año actualizando algunos objetos de la
	 * vista
	 */
	public void cambiarAno() {
		mes = 0;
		elementoInicial = null;
		nombreElementoInicial = null;
		elementoFinal = null;
		nombreElementoFinal = null;
	}

	/**
	 * metodo que tiene la logica al generar el reoprte
	 * 
	 * @param formato
	 */
	public void generarReporte(ReportesBean.FORMATOS formato) {

		String reporte = RDepreciacionMesPeriodoEnum.NIOMBREREPORTE.getValue();

		try {
			String condicion;
			String dEntDevolutivo = "";
			String grupo;

			int parametro = Integer.parseInt(ejbSysmanUtil.consultarParametro(compania,
					RDepreciacionMesPeriodoEnum.PARAMETROSISTEMA.getValue(), modulo, new Date(), true));

			if ("3".equals(ubicacion)) {
				// Inservibles

				if (SysmanFunciones.esBdSqlServer()) {

					dEntDevolutivo = "\n        LEFT JOIN (\n"
							+ "          SELECT  COMPANIA, ELEMENTO, SERIE, TIPOMOVIMIENTO, FECHA \n"
							+ "          FROM    V_BASE_TRANSACCIONESALMACEN \n" + "          WHERE   CLASE = 'T' \n"
							+ "        ) D_ENTDEVOLUTIVO \n"
							+ "        ON  (CDEVOLUTIVOINVENTARIO.COMPANIA = D_ENTDEVOLUTIVO.COMPANIA) \n"
							+ "        AND (CDEVOLUTIVOINVENTARIO.ELEMENTO = D_ENTDEVOLUTIVO.ELEMENTO) \n"
							+ "        AND (CDEVOLUTIVOINVENTARIO.SERIE = D_ENTDEVOLUTIVO.SERIE)";

					condicion = "D_ENTDEVOLUTIVO.TIPOMOVIMIENTO = 'RDI'\n" + "AND     YEAR(D_ENTDEVOLUTIVO.FECHA) "
							+ "<= YEAR(dbo.LAST_DAY(dbo.TO_DATE('01/" + mes + "/" + ano + "','DD/MM/YYYY'))) \n"
							+ "AND     MONTH(D_ENTDEVOLUTIVO.FECHA) " + "<= MONTH(dbo.LAST_DAY(dbo.TO_DATE('01/" + mes
							+ "/" + ano + "','DD/MM/YYYY'))) ";

				} else {
					Acciones.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_ENTORNO.PR_SETFECHAHORA",
							"LAST_DAY(TO_DATE('01/" + mes + "/" + ano + "','DD/MM/YYYY'))");

					dEntDevolutivo = "\n        LEFT JOIN (\n"
							+ "          SELECT  COMPANIA, ELEMENTO, SERIE, TIPOMOVIMIENTO, FECHA \n"
							+ "          FROM    V_BASE_TRANSACCIONESALMACEN \n" + "          WHERE   CLASE = 'T' \n"
							+ "        ) D_ENTDEVOLUTIVO \n"
							+ "        ON  (CDEVOLUTIVOINVENTARIO.COMPANIA = D_ENTDEVOLUTIVO.COMPANIA) \n"
							+ "            AND (CDEVOLUTIVOINVENTARIO.ELEMENTO = D_ENTDEVOLUTIVO.ELEMENTO) \n"
							+ "            AND (CDEVOLUTIVOINVENTARIO.SERIE = D_ENTDEVOLUTIVO.SERIE)";
					condicion = "D_ENTDEVOLUTIVO.TIPOMOVIMIENTO = 'RDI'\n"
							+ "AND     EXTRACT(YEAR FROM D_ENTDEVOLUTIVO.FECHA) "
							+ "<= EXTRACT(YEAR FROM PCK_ENTORNO.FC_GETFECHAHORA) \n"
							+ "AND     EXTRACT(MONTH FROM D_ENTDEVOLUTIVO.FECHA) "
							+ "<= EXTRACT(MONTH FROM PCK_ENTORNO.FC_GETFECHAHORA) ";
				}
				grupo = SysmanFunciones.esBdSqlServer() ? " SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,3)"
						: " SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,3)";

			} else if ("2".equals(ubicacion)) {
				// Servicio
				condicion = "DEPRECIARPERACTUAL.DEPENDENCIA NOT IN (" + "'000000000000','999999999999','999999999902')";
				grupo = SysmanFunciones.esBdSqlServer() ? "SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,3)"
						: "SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,3)";
			} else {
				// Bodega y Responsabilidades
				condicion = " DEPRECIARPERACTUAL.CLASEBODEGA IN ('20') ";
				grupo = SysmanFunciones.esBdSqlServer() ? "SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,3)"
						: "SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,3)";
			}

			String fecha = SysmanFunciones.esBdSqlServer()
					? "dbo.TO_DATE('01/" + SysmanFunciones.strZero(String.valueOf(mes), 2) + "/" + ano
							+ "','DD/MM/YYYY')"
					: "TO_DATE('01/" + SysmanFunciones.strZero(String.valueOf(mes), 2) + "/" + ano + "','DD/MM/YYYY')";

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("elementoInicial", "'" + elementoInicial + "'");
			reemplazar.put("elementoFinal", "'" + elementoFinal + "'");
			reemplazar.put("dEntDevolutivo", dEntDevolutivo);

			if (!validarCondOpcionMenu()) {
				return;
			}
			agregaVariablesReemplazo(reemplazar, parametro, condicion, grupo);

			reemplazar.put(RDepreciacionMesPeriodoEnum.FECHA.getValue(), fecha);
			reporte = RDepreciacionMesPeriodoEnum.NIOMBREREPORTE.getValue();
			String strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			boolean esAgrupado = "1".equals(agrupado);
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(RDepreciacionMesPeriodoEnum.PR_STRSQL.getValue(), strSql);
			parametros.put(RDepreciacionMesPeriodoEnum.PR_FORMS_RDEPRECIACIONMESPERIODO_MES.getValue(),
					new DateFormatSymbols().getMonths()[mes - 1]);
			parametros.put(RDepreciacionMesPeriodoEnum.PR_FORMS_RDEPRECIACIONMESPERIODO_ANO.getValue(), ano);
			parametros.put(RDepreciacionMesPeriodoEnum.PR_AGRUPADO.getValue(), esAgrupado);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (FileNotFoundException e) {
			JsfUtil.agregarMensajeError(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE").replace("s$reporte$s", reporte) + e.getMessage());
			logger.error(e.getMessage(), e);
		}

		catch (JRException | IOException | SysmanException | SQLException | NamingException | NumberFormatException
				| SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * metodo que agrega las variable de reemplazo
	 * 
	 * @param reemplazar
	 * @param parametro
	 * @param condicion
	 * @param grupo
	 */
	private void agregaVariablesReemplazo(HashMap<String, Object> reemplazar, int parametro, String condicion,
			String grupo) {
		if (cOpcionMenu1.equals(SessionUtil.getMenuActual())) {
			reemplazar.put(cCondicion, condicion);
			reemplazar.put(cGrupo, grupo);
			reemplazar.put(cParametro, 3);
		} else if (cOpcionMenu4.equals(SessionUtil.getMenuActual())) {
			reemplazar.put(cCondicion,
					" CDEVOLUTIVOINVENTARIO.COMPANIA = '" + compania + "' "
							+ "AND CDEVOLUTIVOINVENTARIO.ELEMENTO BETWEEN '" + elementoInicial + "' AND '"
							+ elementoFinal + "' " + " AND DEPRECIARPERACTUAL.DEPENDENCIA NOT IN ('999999999999')");
			reemplazar.put(cGrupo, (SysmanFunciones.esBdSqlServer() ? " SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,"
					: " SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,") + parametro + " )");
			reemplazar.put(cParametro, parametro);
		} else if (cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
			reemplazar.put(cCondicion,
					"CDEVOLUTIVOINVENTARIO.COMPANIA = '" + compania + "' "
							+ "AND DEPRECIARPERACTUAL.DEPENDENCIA NOT IN ('999999999999') "
							+ "AND CDEVOLUTIVOINVENTARIO.ELEMENTO BETWEEN '" + elementoInicial + "' AND '"
							+ elementoFinal + "' ");
			reemplazar.put(cGrupo, (SysmanFunciones.esBdSqlServer() ? "SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,"
					: "SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,") + parametro + " )");
			reemplazar.put(cParametro, parametro);
		} else if (cOpcionMenu2.equals(SessionUtil.getMenuActual())) {
			elementoInicial = "00000000";
			elementoFinal = "99999999";

			reemplazar.put(cCondicion,
					"CDEVOLUTIVOINVENTARIO.COMPANIA = '" + compania + "' "
							+ "AND DEPRECIARPERACTUAL.DEPENDENCIA NOT IN ('999999999999') "
							+ "AND CDEVOLUTIVOINVENTARIO.TIPOELEMENTO ='N'");
			reemplazar.put("elementoInicial", "'" + elementoInicial + "'");
			reemplazar.put("elementoFinal", "'" + elementoFinal + "'");
			reemplazar.put(cGrupo, (SysmanFunciones.esBdSqlServer() ? "SUBSTRING(CDEVOLUTIVOINVENTARIO.ELEMENTO,1,"
					: "SUBSTR(CDEVOLUTIVOINVENTARIO.ELEMENTO,0,") + parametro + " )");
			reemplazar.put(cParametro, parametro);
		}
	}

	/**
	 * metodo que valida la opcion de menu
	 * 
	 * @return
	 */
	private boolean validarCondOpcionMenu() {
		if ((cOpcionMenu1.equals(SessionUtil.getMenuActual()) || cOpcionMenu4.equals(SessionUtil.getMenuActual()))
				&& (SysmanFunciones.validarVariableVacio(elementoInicial)
						|| SysmanFunciones.validarVariableVacio(elementoFinal))) {
			JsfUtil.agregarMensajeError("Seleccione los elementos");
			return false;
		} else if (cOpcionMenu3.equals(SessionUtil.getMenuActual())
				&& SysmanFunciones.validarVariableVacio(elementoInicial)) {
			JsfUtil.agregarMensajeError("Seleccione un elemento");
			return false;
		} else if (cOpcionMenu2.equals(SessionUtil.getMenuActual())) {
			elementoInicial = "00000000";
			elementoFinal = "99999999";
		}
		return true;
	}

	/**
	 * metodo que se ejecuta cuando se selecciona un elemento inicial del combobox
	 * grande
	 * 
	 * @param event
	 */
	public void seleccionarFilaCodigoElementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get(cCodigoElemento).toString();
		nombreElementoInicial = registroAux.getCampos().get(RDepreciacionMesPeriodoEnum.NOMBRELARGO.getValue())
				.toString();
		cargarListaCodigoElementoFinal();
	}

	/**
	 * metodo que se ejecuta cuando se selecciona un elemento final del combobox
	 * grande
	 * 
	 * @param event
	 */
	public void seleccionarFilaCodigoElementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = registroAux.getCampos().get(cCodigoElemento).toString();
		nombreElementoFinal = registroAux.getCampos().get(RDepreciacionMesPeriodoEnum.NOMBRELARGO.getValue())
				.toString();
	}

	/**
	 * metodo que se llama al abrir el formulario
	 */
	@Override
	public void abrirFormulario() {
		Calendar calendar = Calendar.getInstance();
		if (cOpcionMenu1.equals(SessionUtil.getMenuActual())) {
			ubicacion = "1";
			agrupado = "1";
			ano = calendar.get(Calendar.YEAR);
			mes = calendar.get(Calendar.MONTH) + 1;
			ubicacionVisible = true;
			titulo = "DEPRECIACIONES DEL MES POR PERIODOS";
			elementoInicialVisible = true;
			elementoFinalVisible = true;
		} else if (cOpcionMenu4.equals(SessionUtil.getMenuActual())
				|| cOpcionMenu3.equals(SessionUtil.getMenuActual())) {
			agrupado = "1";
			ano = calendar.get(Calendar.YEAR);
			mes = calendar.get(Calendar.MONTH) + 1;
			ubicacionVisible = false;
			titulo = "DEPRECIACIONES INMUEBLES POR PERIODOS";
			elementoInicialVisible = true;
			elementoFinalVisible = true;
		} else if (cOpcionMenu2.equals(SessionUtil.getMenuActual())) {
			agrupado = "1";
			ano = calendar.get(Calendar.YEAR);
			mes = calendar.get(Calendar.MONTH) + 1;
			ubicacionVisible = false;
			titulo = "DEPRECIACIONES INMUEBLES POR PERIODOS";
			elementoInicialVisible = false;
			elementoFinalVisible = false;

		}

	}

	/**
	 * metodo get y set
	 * 
	 * @return
	 */
	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getAgrupado() {
		return agrupado;
	}

	public void setAgrupado(String agrupado) {
		this.agrupado = agrupado;
	}

	public String getElementoInicial() {
		return elementoInicial;
	}

	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}

	public String getElementoFinal() {
		return elementoFinal;
	}

	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public String getNombreElementoInicial() {
		return nombreElementoInicial;
	}

	public void setNombreElementoInicial(String nombreElementoInicial) {
		this.nombreElementoInicial = nombreElementoInicial;
	}

	public String getNombreElementoFinal() {
		return nombreElementoFinal;
	}

	public void setNombreElementoFinal(String nombreElementoFinal) {
		this.nombreElementoFinal = nombreElementoFinal;
	}

	public boolean isUbicacionVisible() {
		return ubicacionVisible;
	}

	public void setUbicacionVisible(boolean ubicacionVisible) {
		this.ubicacionVisible = ubicacionVisible;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public boolean isElementoFinalVisible() {
		return elementoFinalVisible;
	}

	public void setElementoFinalVisible(boolean elementoFinalVisible) {
		this.elementoFinalVisible = elementoFinalVisible;
	}

	public boolean isElementoInicialVisible() {
		return elementoInicialVisible;
	}

	public void setElementoInicialVisible(boolean elementoInicialVisible) {
		this.elementoInicialVisible = elementoInicialVisible;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	public RegistroDataModelImpl getListaCodigoElementoInicial() {
		return listaCodigoElementoInicial;
	}

	public void setListaCodigoElementoInicial(RegistroDataModelImpl listaCodigoElementoInicial) {
		this.listaCodigoElementoInicial = listaCodigoElementoInicial;
	}

	public RegistroDataModelImpl getListaCodigoElementoFinal() {
		return listaCodigoElementoFinal;
	}

	public void setListaCodigoElementoFinal(RegistroDataModelImpl listaCodigoElementoFinal) {
		this.listaCodigoElementoFinal = listaCodigoElementoFinal;
	}

}