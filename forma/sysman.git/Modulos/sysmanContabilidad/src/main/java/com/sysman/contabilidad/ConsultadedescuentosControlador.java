/*-
 * ConsultadedescuentosControlador.java
 *
 * 1.0
 * 
 * 18/04/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.contabilidad.enums.ConsultadedescuentosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;

import com.sysman.contabilidad.ejb.EjbContabilidadSeisLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/04/2022
 * @author carenas
 */
@ManagedBean
@ViewScoped
public class ConsultadedescuentosControlador extends BeanBaseDatosAcme{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private final String modulo;

	private String tipo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
    @EJB
    private EjbContabilidadSeisLocal EjbContabilidadSeisRemote;
	
	private RegistroDataModelImpl listaTipo;
	private Date fechaInicial;
	private Date fechaFinal;
	private String parametro;
	private String pivot;
	private boolean porFuente;
	private String reporte;
	
	
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de ConsultadedescuentosControlador
	 */
	public ConsultadedescuentosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		fechaInicial = new Date();
		fechaFinal = new Date();
		tipo = SysmanConstantes.DEFECTOINICIAL_STRING;
		try {
			// 2350;
			numFormulario = GeneralCodigoFormaEnum.CONSULTA_DE_DESCUENTOS
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTAS_SUBFORM>
		//</CARGAR_LISTAS_SUBFORM>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		//</CARGAR_LISTAS_SUBFORM_NULL>
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
		cargarListaTipo();
		abrirFormulario();
		
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos="";	
	}
	/**
	 * Se realiza la asignacion de la variable origenGrilla por la
	 * consulta correspondiente de la grilla del formulario, se hace
	 * la asignacion de dicha consulta a los objetos listaInicial y
	 * listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void reasignarOrigenGrilla() {
		origenGrilla="";
		if (listaInicial != null) {
			listaInicial.setOrigen(origenGrilla);
		}
		if (listaInicialF != null) {
			listaInicialF.setOrigen(origenGrilla);
		}
	}
	//<METODOS_CARGAR_LISTA>	
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
						ConsultadedescuentosControladorUrlEnum.URL001
						.getValue());

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
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
			tipo = registroAux.getCampos().get("CODIGO").toString();

		
		//Registro registroAux = (Registro) event.getObject();
		//        tipo= registroAux.getCampos().get("CODIGO");
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Generar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws ParseException 
	 *
	 */
	public void oprimirGenerar() {

        try {
        	try {
				 parametro = ejbSysmanUtil.consultarParametro(compania,
                         "CLASE CONTABLE EN INFORME DE DESCUENTOS", modulo,
                         new Date(), true);
				 				 
		            pivot = EjbContabilidadSeisRemote.getPivotPlanContable(compania, fechaInicial,
		            		fechaFinal, parametro,
		            		tipo);
		            if (pivot == null) {
		                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2065"));
		                return;
		            }
			} catch (SystemException e1) {
		
				e1.printStackTrace();
			}

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("parametro", parametro);
            reemplazar.put("tipo", tipo);
            reemplazar.put("pivot", pivot);
            
            if (porFuente) {
            	reporte = "002968CONSULTADEDESCUENTOS";
            }           
            else {
            	reporte = "800523CONSULTADEDESCUENTOS";
            }
            
          
            String sql = Reporteador.resuelveConsulta(
            		reporte,
                    Integer.parseInt(modulo), reemplazar);
            try {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
				        ConectorPool.ESQUEMA_SYSMAN,
				        ReportesBean.FORMATOS.EXCEL97,reporte);
			} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
        catch (SQLException | JRException | IOException
                | DRException ex) {
    Logger.getLogger(ConsultadedescuentosControlador.class.getName())
                    .log(Level.SEVERE, null, ex);
        } catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2350-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   formularioAbrir GetIdModulo, Me.Name
   DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
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
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	public boolean isPorFuente() {
		return porFuente;
	}
	public void setPorFuente(boolean porFuente) {
		this.porFuente = porFuente;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	
	
	
	
	
}
