/*-
 * FrmSolicitudSectorial.java
 *
 * 1.0
 * 
 * 16/02/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.bancoproyectos.enums.FrmSolicitudSectorialUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 16/02/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmSolicitudSectorial extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String proyecto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipoSolicitud;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreTipoSol;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreProyecto;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProyecto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTipo;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmSolicitudSectorial
	 */
	public FrmSolicitudSectorial() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		fechaInicial = new Date();
		fechaFinal = new Date();
		try {
			numFormulario= GeneralCodigoFormaEnum.SOLICITUDES_CON_SECTORIAL.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProyecto(); 
		cargarListaTipo();
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
	 * Carga la lista listaProyecto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProyecto(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmSolicitudSectorialUrlEnum.URL32003.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTipo(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmSolicitudSectorialUrlEnum.URL218001
						.getValue());

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"TIPOT");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT_pdf
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBT_pdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT_excel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBT_excel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	
	// <METODO GENERAR INFORME>
    public void generarInforme(FORMATOS formato) {
        if (!validarFechas()) {
            return;
        }
    	try {
    		
    		String informe = "002905SolicitudesDetalleSectorial";
    		
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("proyecto", proyecto);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("tipoSolicitud", tipoSolicitud);

            Reporteador.resuelveConsulta(informe,Integer.parseInt(modulo), reemplazar, parametros);

            // Parametros diseño reporte
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_PROYECTO", proyecto);
            parametros.put("PR_PROYECTO_NOMBRE", nombreProyecto);
            parametros.put("PR_TIPO_SOLICITUD", tipoSolicitud);
            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(informe,parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()); 
        }
    }
    
    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }	
	
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProyecto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyecto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyecto = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
		nombreProyecto = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoSolicitud= registroAux.getCampos().get("TIPOT").toString();
		nombreTipoSol = registroAux.getCampos().get("NOMBRE").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proyecto
	 * 
	 * @return  proyecto
	 */
	public String getProyecto() {
		return proyecto;
	}
	/**
	 * Asigna la variable  proyecto
	 * 
	 * @param  proyecto
	 * Variable a asignar en  proyecto
	 */
	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	/**
	 * Retorna la variable tipoSolicitud
	 * 
	 * @return  tipoSolicitud
	 */
	public String getTipoSolicitud() {
		return tipoSolicitud;
	}
	/**
	 * Asigna la variable  tipoSolicitud
	 * 
	 * @param  tipoSolicitud
	 * Variable a asignar en  tipoSolicitud
	 */
	public void setTipoSolicitud(String tipoSolicitud) {
		this.tipoSolicitud = tipoSolicitud;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * Retorna la variable nombreTipoSol
	 * 
	 * @return  nombreTipoSol
	 */
	public String getNombreTipoSol() {
		return nombreTipoSol;
	}
	/**
	 * Asigna la variable  nombreTipoSol
	 * 
	 * @param  nombreTipoSol
	 * Variable a asignar en  nombreTipoSol
	 */
	public void setNombreTipoSol(String nombreTipoSol) {
		this.nombreTipoSol = nombreTipoSol;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaProyecto
	 * 
	 * @return listaProyecto
	 */
	public RegistroDataModelImpl getListaProyecto() {
		return listaProyecto;
	}
	/**
	 * Asigna la lista listaProyecto
	 * 
	 * @param listaProyecto
	 * Variable a asignar en  listaProyecto
	 */
	public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
		this.listaProyecto = listaProyecto;
	}
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}
	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en  listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
