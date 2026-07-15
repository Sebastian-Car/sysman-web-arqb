/*-
 * GeneraProcesoSiaControlador.java
 *
 * 1.0
 * 
 * 13/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.impl.EjbChipFutUno;
import com.sysman.chipfut.enums.GeneraProcesoSiaControladorEnum;
import com.sysman.chipfut.enums.GeneraProcesoSiaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbReportesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Se generan informes .
 *
 * @version 1.0, 13/07/2018
 * @author asana
 * @version 1.1, 15/02/2022
 * @author gfigueredo
 * Ticket 7709219. Se modifica la función {@link #generarInforme(FORMATOS)}, 
 * para que cuando el separador del archivo sea "|", serialice el informe, y cuando sea ","
 * genere el archivo csv normalmente.
 * @see #generarInforme(FORMATOS)
 * 
 */
@ManagedBean
@ViewScoped
public class GeneraProcesoSiaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 *  
	 */
	private String proceso;
	/**
	 * 
	 */
	private String mesInicial;
	/**
	 * 
	 */
	private String mesFinal;
	/**
	 * 
	 */
	private String ano;
	/**
	 * Atributo que almacena el titulo del formulario con el nombre del departamento
	 * que tiene asosiado la compania
	 */
	private String titulo;
	/**
	 * Atributo que almacena el nombre del informe seleccioando en el combo de
	 * Formulario a generar
	 */
	private String nombreProceso;
	/**
	 * Atributo que almacena el numero de digitos digitados en el campo digitos
	 */
	private String digitos;
	/**
	 * Atributo que almacena el codigo de la consulta a resolver, segun el registro
	 * seleccionado en el combo Informe a Generar
	 */
	private String nombreConsulta;
	/**
	 * Atributo que almacena el nombre del archivo del registro seleccionado.
	 */
	private String nombreArchivo;
	/**
	 * Atributo que almacena las columnas de la consulta
	 */
	private String columnas;
	
	private String concatenado;
	

	/**
	 * Atributo que almacena el separador de columnas
	 */
	private String separador;
	/**
	 * Atributo que valida si se hace visible
	 */
	private boolean visibleDigitos = false;
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaMesInicial;
	private List<Registro> listaMesFinal;
	private List<Registro> listaAno;
	//
        private String menuCleoplatra;

	@EJB
	private EjbReportesRemote ejbReportes;
    @EJB
    private EjbChipFutUno ejbChipFutUno;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	// </DECLARAR_LISTAS>
	/**
	 * Registro para cargar el codigo de departamento relacionado en la compania
	 */
	Registro rsDepartamento;
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista de registros de las consultas del SIA en informes entes
	 */
	private RegistroDataModelImpl listaProceso;
	
	private String vigenciaAppui;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de GeneraProcesoSiaControlador
	 */
	public GeneraProcesoSiaControlador() {
		super();
		compania = SessionUtil.getCompania();
		menuCleoplatra = SessionUtil.getMenuActual();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_GENERAPROCESOSIA_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		abrirFormulario();
		// <CARGAR_LISTA>
		cargarListaAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		if(menuCleoplatra.equals("99060203")) {
                   cargarListaProcesoCleopatra(7);
		}else {
    		    cargarListaProceso();
		}
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>

	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
	    digitos = "6";
            ano = String.valueOf(SysmanFunciones.ano(new Date()));
            if(!menuCleoplatra.equals("99060203")) {
                    HashMap<String, Object> param = new HashMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                    try {
                            rsDepartamento = RegistroConverter
                                            .toRegistro(
                                                            requestManager.get(
                                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            GeneraProcesoSiaControladorUrlEnum.URL8762.getValue())
                                                                            .getUrl(),
                                                                            param));
                            titulo = idioma.getString("TB_TB4246").replace("s$departamento$s",
                                            rsDepartamento.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString().toUpperCase());
                            
                    } catch (SystemException e) {
                            JsfUtil.agregarMensajeError(e.getMessage());
                            logger.error(e.getMessage(), e);
                    }
            }else {
                    titulo = GeneralParameterEnum.GENERARPLANOSCLEOPATRA.getName();
            }
            cargarListaMesInicial();
            cargarListaMesFinal();

		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaProceso
	 */
	public void cargarListaProceso() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraProcesoSiaControladorUrlEnum.URL175.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "2");
		param.put(GeneraProcesoSiaControladorEnum.SUBTIPO.getValue(),
				rsDepartamento.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()));

		listaProceso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listaMesInicial = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GeneraProcesoSiaControladorUrlEnum.URL5973.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaMesFinal
	 */
	public void cargarListaMesFinal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

		try {
			listaMesFinal = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GeneraProcesoSiaControladorUrlEnum.URL4509.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GeneraProcesoSiaControladorUrlEnum.URL3933.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}
	/**
         * carga lista de proceso cleopatra tipo 7
         */
	public void cargarListaProcesoCleopatra(int tipo) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GeneraProcesoSiaControladorUrlEnum.URL175003.getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TIPO.getName(), tipo);
            param.put(GeneralParameterEnum.SUBTIPO.getName(), "0");
            param.put(GeneralParameterEnum.CODINI.getName(), "1");
            param.put(GeneralParameterEnum.CODFIN.getName(), "20");
            

            listaProceso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());

    }
	

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar en la vista
	 *
	 */
	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.CSV);
		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(FORMATOS formato) {

		try {

			if ("1".equals(proceso) && digitos.isEmpty()) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4252"));
				return;
			}
			String nomArchivo = idioma.getString("TB_TB4251")
					.replace("s$fecha$s", SysmanFunciones.concatenar(ano, mesFinal))
					.replace("s$nombre$s", nombreArchivo);
			
			// Parametro fecha corte almacen
            String fechaC = ejbSysmanUtil.consultarParametro(compania,
                            "FECHA DE CORTE PARA INICIO DEL ALMACEN", SessionUtil.getModulo(),
                            new Date(), false);
            
            Date fechaCorte = SysmanFunciones.convertirAFecha(fechaC,
            		"dd/MM/yyyy");
            
			Map<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("ano", ano);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesInicial-1", Integer.valueOf(mesInicial) - 1);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("digitos", digitos);
			reemplazar.put("fechaCorte",
                    SysmanFunciones.formatearFecha(fechaCorte));
	        String Vigencias = String.format("'%s'", vigenciaAppui.replace(",", "','"));
			reemplazar.put("vigenciasAppui", Vigencias);

			String sql = Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(SessionUtil.getModulo()),
					reemplazar);

			if (formato.equals(FORMATOS.CSV)) {
				/*
				 * gfigueredo
				 * Ticket 7709219
				 * validación por tipo de separador
				 */
				if (separador.equals("|")) {
					String concatenadoColum = concatenado.replace(separador, "|| '" + separador + "' ||");

					columnas = columnas.replaceAll("\"", "");

					String sqlCsv = "SELECT '" + columnas + "' FROM DUAL" + " UNION ALL" + " SELECT " + concatenadoColum
							+ " FROM ( " + sql + ")";

					String cadena = "";
					int carac_esp = 0;
					Statement consultaPlano = null;
					ResultSet resultadoConsulta = null;
					sqlCsv = sqlCsv.replace("'", "''");

					try {
						carac_esp = nombreConsulta.equals("800704RelacionContractualTotal")?1:0;
						cadena = ejbChipFutUno.generarProcesoSiaSql(sqlCsv,carac_esp);
			        }
			        catch (NumberFormatException | SystemException e) {
			            logger.error(e.getMessage(), e);
			            JsfUtil.agregarMensajeError(e.getMessage());
			        }
					

					archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(cadena),
							SysmanFunciones.concatenar(nomArchivo, ".csv"));
				} else {
					archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato,
							nomArchivo);
				}
			} else {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato,
						nomArchivo);
			}

		} catch (JRException | IOException | SQLException | DRException | SysmanException | SystemException | ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control MesInicial
	 * 
	 */
	public void cambiarMesInicial() {
		// <CODIGO_DESARROLLADO>
		cargarListaMesFinal();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		cargarListaMesInicial();

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProceso
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProceso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proceso = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		nombreProceso = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();

		nombreConsulta = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneraProcesoSiaControladorEnum.CONSULTA.getValue()), "").toString();
		nombreArchivo = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneraProcesoSiaControladorEnum.NOMBRE_ARCHIVO.getValue()), "")
				.toString();
		columnas = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneraProcesoSiaControladorEnum.COLUMNAS.getValue()), "").toString();
		concatenado  =  columnas;
		separador = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneraProcesoSiaControladorEnum.SEPARADOR.getValue()), "").toString();
		vigenciaAppui = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneraProcesoSiaControladorEnum.VIGENCIAS_APPUI.getValue()), "").toString();
		
		if(!menuCleoplatra.equals("99060203")) {

		if ("1".equals(proceso)) {
			visibleDigitos = true;
		} else {
			visibleDigitos = false;
		}
		}

	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proceso
	 * 
	 * @return proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * Asigna la variable proceso
	 * 
	 * @param proceso Variable a asignar en proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}

	/**
	 * Asigna la variable mesInicial
	 * 
	 * @param mesInicial Variable a asignar en mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}

	/**
	 * Asigna la variable mesFinal
	 * 
	 * @param mesFinal Variable a asignar en mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable titulo
	 * 
	 * @return titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Asigna la variable titulo
	 * 
	 * @param titulo Variable a asignar en titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Retorna la variable titulo
	 * 
	 * @return titulo
	 */
	public String getNombreProceso() {
		return nombreProceso;
	}

	/**
	 * Asigna la variable titulo
	 * 
	 * @param titulo Variable a asignar en titulo
	 */
	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}

	/**
	 * Retorna la variable digitos
	 * 
	 * @return digitos
	 */
	public String getDigitos() {
		return digitos;
	}

	/**
	 * Asigna la variable digitos
	 * 
	 * @param digitos Variable a asignar en digitos
	 */
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}

	/**
	 * Retorna la variable visibleDigitos
	 * 
	 * @return visibleDigitos
	 */
	public boolean isVisibleDigitos() {
		return visibleDigitos;
	}

	/**
	 * Asigna la variable visibleDigitos
	 * 
	 * @param visibleDigitos Variable a asignar en visibleDigitos
	 */
	public void setVisibleDigitos(boolean visibleDigitos) {
		this.visibleDigitos = visibleDigitos;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaMesInicial
	 * 
	 * @return listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}

	/**
	 * Asigna la lista listaMesInicial
	 * 
	 * @param listaMesInicial Variable a asignar en listaMesInicial
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}

	/**
	 * Retorna la lista listaMesFinal
	 * 
	 * @return listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}

	/**
	 * Asigna la lista listaMesFinal
	 * 
	 * @param listaMesFinal Variable a asignar en listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}

	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public RegistroDataModelImpl getListaProceso() {
		return listaProceso;
	}

	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso Variable a asignar en listaProceso
	 */
	public void setListaProceso(RegistroDataModelImpl listaProceso) {
		this.listaProceso = listaProceso;
	}
	
	public String getConcatenado() {
		return concatenado;
	}

	public void setConcatenado(String concatenado) {
		this.concatenado = concatenado;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
