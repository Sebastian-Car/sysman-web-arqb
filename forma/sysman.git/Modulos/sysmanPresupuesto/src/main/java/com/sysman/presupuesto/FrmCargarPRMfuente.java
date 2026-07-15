/*-
 * FrmCargarPRMfuente.java
 *
 * 1.0
 * 
 * 17/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.enums.FrmCargarPRMfuenteUrlEnum;
import com.sysman.presupuesto.enums.FrmdisresduControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 17/06/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmCargarPRMfuente extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private int anio;
	private String cuentaInicial;
	private String cuentaFinal;
	private String nombreCuentaIni;
	private String nombreCuentaFin;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private StreamedContent archivoDescarga;

	private StringBuilder cadena;

	private ContenedorArchivo contArchivoPlantillaPmr;
	
	
	@EJB
	private EjbPresupuestoCuatroRemote presupuestoCuatroRemote;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCargarPRMfuente
	 */
	public FrmCargarPRMfuente() {
		super();
		compania = SessionUtil.getCompania();

		anio = SysmanFunciones.ano(new Date());
		try {
			numFormulario=2295;
			validarPermisos();

			contArchivoPlantillaPmr = new ContenedorArchivo();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListaAnio();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial(); 

		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		UrlBean urlListAno = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmdisresduControladorUrlEnum.URL3940
						.getValue());
		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(urlListAno.getUrl(), param));
		}
		catch (SystemException e) {
			Logger.getLogger(FrmdisresduControlador.class.getName())
			.log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCargarPRMfuenteUrlEnum.URL0001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCargarPRMfuenteUrlEnum.URL0002
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), anio);
		param.put("CUENTAINICIAL", cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton DescargarPlan
	 * en la vista
	 *
	 *
	 */
	public void oprimirDescargarPlan() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;

		generaReporte(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CargarPlan
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargarPlan() {
		//<CODIGO_DESARROLLADO>
		FileInputStream file = null;
		cadena = new StringBuilder();

		try {
			if (validarArchivo()) {
				String ruta = contArchivoPlantillaPmr.getArchivo()
						.getPath();

				String extension = ruta
						.substring(ruta.indexOf('.'),
								ruta.length())
						.substring(1, ruta.substring(
								ruta.indexOf('.'),
								ruta.length()).length());

				file = new FileInputStream(new File(ruta));

				Workbook workbook = null;

				if (workbook == null) {
                    if ("xls".equals(extension)) {
                        workbook = new HSSFWorkbook(file);
                    }
                    else {
                        workbook = new XSSFWorkbook(file);
                    }
                }

				leerHoja(workbook, 0, 6, cadena, 1);		
				cargarDatos();



				file.close();
				workbook.close();
			}

		} catch (IOException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            try {
                if (file != null) {
                    file.close();
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
		//</CODIGO_DESARROLLADO>
	}


	public boolean validarArchivo() {

		if (contArchivoPlantillaPmr.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		}
		else {
			return true;
		}
	}


	public void leerHoja(Workbook workbook, int hoja, int columnas,
			StringBuilder cadena, int filainicial) {
		cadena.append("TO_CLOB('");
		Sheet sheet = workbook.getSheetAt(hoja);
		Row fila;
		Cell celda;
		int num = 0;
		for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
			fila = sheet.getRow(i);

			if (fila.getCell(0) != null) {

				for (int j = 0; j < columnas; j++) {
					celda = fila.getCell(j);
					if (celda != null) {
						num = num
								+ (celda.getCellType() == 1
								? celda.getStringCellValue()
										.replaceFirst("'", " ").length()
										: NumberToTextConverter
										.toText(celda.getNumericCellValue())
										.length());
						cadena.append(celda.getCellType() == 1
								? celda.getStringCellValue().replaceFirst("'", " ")
										: " ");

					}
					else {
						cadena.append("");
					}
					if (num >= 10000) {
						cadena.append("') || TO_CLOB('");
						num = 0;
					}
					cadena.append(SysmanConstantes.SEPARADOR_COL);
				}
				cadena.append(SysmanConstantes.SEPARADOR_REG);
			}

		}
		cadena.append("')"
				+ "");

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>

	private void generaReporte(FORMATOS formato) {
		try {

			String reporte = "800438CargarPmrFuenteRecursos";


			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("anio", anio);
			reemplazar.put("codigoInicial",cuentaInicial);
			reemplazar.put("codigoFinal",cuentaFinal);


			// MANEJO DE PARAMETROS DEL REPORTE
			Map<String, Object> parametros = new HashMap<>();

			String sql= Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);
			setArchivoDescarga(JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporte));

		}
		catch (JRException | IOException | NumberFormatException  | DRException | SQLException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	
	
	private void cargarDatos() {


		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();

							presupuestoCuatroRemote.cargarPmrFuente(compania, parametro, SessionUtil.getUser().getCodigo());

							JsfUtil.agregarMensajeInformativo(
									idioma.getString(
											"MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombreCuentaIni = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
		cuentaFinal = null;
		nombreCuentaFin = null;
		cargarListaCuentaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombreCuentaFin = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}
	/**
	 * Retorna la variable nombreCuentaIni
	 * 
	 * @return  nombreCuentaIni
	 */
	public String getNombreCuentaIni() {
		return nombreCuentaIni;
	}
	/**
	 * Asigna la variable  nombreCuentaIni
	 * 
	 * @param  nombreCuentaIni
	 * Variable a asignar en  nombreCuentaIni
	 */
	public void setNombreCuentaIni(String nombreCuentaIni) {
		this.nombreCuentaIni = nombreCuentaIni;
	}
	/**
	 * Retorna la variable nombreCuentaFin
	 * 
	 * @return  nombreCuentaFin
	 */
	public String getNombreCuentaFin() {
		return nombreCuentaFin;
	}
	/**
	 * Asigna la variable  nombreCuentaFin
	 * 
	 * @param  nombreCuentaFin
	 * Variable a asignar en  nombreCuentaFin
	 */
	public void setNombreCuentaFin(String nombreCuentaFin) {
		this.nombreCuentaFin = nombreCuentaFin;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	/**
	 * @return the contArchivoPlantillaPmr
	 */
	public ContenedorArchivo getContArchivoPlantillaPmr() {
		return contArchivoPlantillaPmr;
	}
	/**
	 * @param contArchivoPlantillaPmr the contArchivoPlantillaPmr to set
	 */
	public void setContArchivoPlantillaPmr(ContenedorArchivo contArchivoPlantillaPmr) {
		this.contArchivoPlantillaPmr = contArchivoPlantillaPmr;
	}
}
