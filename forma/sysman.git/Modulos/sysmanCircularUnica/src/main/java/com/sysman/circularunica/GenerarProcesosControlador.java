/*-
 * GenerarProcesosControlador.java
 *
 * 1.0
 * 
 * 12/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.circularunica;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.circularunica.ejb.impl.EjbCircularUnicaCero;
import com.sysman.circularunica.enums.GenerarProcesosControladorEnum;
import com.sysman.circularunica.enums.GenerarProcesosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * Controlador que permite generar los archivos de circular Unica
 *
 * @version 1.0, 12/07/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class GenerarProcesosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Attibuto que almacena el codigo del proceso seleccionadao en el
     * combo de proceso
     */
    private String proceso;
    /**
     * Atributo que almacena el numero del combo mes inicial
     */
    private String mesInicial;
    /**
     * Atributo que almacena el numero del combo mes final
     */
    private String mesFinal;
    /**
     * Atributo que almacena el numero del combo ano
     */
    private String ano;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla MES
     */
    private List<Registro> listaMesInicial;
    /**
     * Lista de registro de la tabla MES
     */
    private List<Registro> listaMesFinal;
    /**
     * Lista de regsitros de la tabla ANO
     */
    private List<Registro> listaAno;
    /**
     * Lista de los informes a generar
     */
    private RegistroDataModelImpl listaProceso;
	private String nombreConsulta;
	private String nombreProceso;
	private String nombreArchivo;
	private String columnas;
	private String concatenado;
	private String separador;

    @EJB
    private EjbCircularUnicaCero ejbCircularUnicaCero;
    
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de GenerarProcesosControlador
     */
    public GenerarProcesosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            // 1855
            numFormulario = GeneralCodigoFormaEnum.GENERAR_PROCESOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaProceso();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso()
    {

    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GenerarProcesosControladorUrlEnum.URL187.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "3");
		param.put(GeneralParameterEnum.SUBTIPO.getName(), "0");
			

		listaProceso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try
        {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarProcesosControladorUrlEnum.URL167
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

        try
        {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarProcesosControladorUrlEnum.URL197
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarProcesosControladorUrlEnum.URL192
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarArchivo(FORMATOS.EXCEL);
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
		generarArchivo(FORMATOS.CSV);
		// </CODIGO_DESARROLLADO>
	}

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarArchivo(ReportesBean.FORMATOS formato)
    {
    	try
        {
            String strSql = "";
   
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mesInicial-1", Integer.valueOf(mesInicial) - 1);
            
            String nomArchivo = idioma.getString("TB_TB4251")
    					.replace("s$fecha$s", SysmanFunciones.concatenar(ano, mesFinal))
    					.replace("s$nombre$s", nombreArchivo);
            
            strSql = Reporteador.resuelveConsulta(
                            nombreConsulta,
                            Integer.parseInt(modulo), reemplazar);
            
            if (formato.equals(FORMATOS.CSV)) {
            	
				if (separador.equals("|")) {
					String concatenadoColum = concatenado.replace(separador, "|| '" + separador + "' ||");

					columnas = columnas.replaceAll("\"", "");

					String sqlCsv = "SELECT '" + columnas + "' FROM DUAL" + " UNION ALL" + " SELECT " + concatenadoColum
							+ " FROM ( " + strSql + ")";

					String cadena = "";

					Statement consultaPlano = null;
					ResultSet resultadoConsulta = null;
					sqlCsv = sqlCsv.replace("'", "''");

					try {
						cadena = ejbCircularUnicaCero.generarProcesoSiaSql(sqlCsv);
			        }
			        catch (NumberFormatException | SystemException e) {
			            logger.error(e.getMessage(), e);
			            JsfUtil.agregarMensajeError(e.getMessage());
			        }
					

					archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(cadena),
							SysmanFunciones.concatenar(nomArchivo, ".csv"));
				} else {
					archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, formato,
							nomArchivo);
				}
			}else{	
				

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                formato, nombreProceso);
			}

        }
        catch ( IOException | SQLException | DRException | JRException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control MesInicial
     * 
     * 
     */
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMesInicial();
        // </CODIGO_DESARROLLADO>
    }
    public void seleccionarFilaProceso(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	proceso = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		nombreConsulta = SysmanFunciones
				.nvl(registroAux.getCampos().get("CONSULTA"), "").toString();
		nombreProceso = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		nombreArchivo = SysmanFunciones
				.nvl(registroAux.getCampos().get(GenerarProcesosControladorEnum.NOMBRE_ARCHIVO.getValue()), "")
				.toString();
		columnas = SysmanFunciones
				.nvl(registroAux.getCampos().get(GenerarProcesosControladorEnum.COLUMNAS.getValue()), "").toString();
		concatenado  =  columnas;
		separador = SysmanFunciones
				.nvl(registroAux.getCampos().get(GenerarProcesosControladorEnum.SEPARADOR.getValue()), "").toString();
		
    	}
    /**
     * 
     * Metodo invocado al ejecutar el comando remoto actualizarAlerta
     * en la vista
     *
     */
    public void ejecutaractualizarAlerta()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeError(idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso()
    {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial()
    {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal()
    {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano)
    {
        this.ano = ano;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
    * @return the listaProceso
    		 */
    		public RegistroDataModelImpl getListaProceso() {
    			return listaProceso;
    		}

    		/**
    		 * @param listaProceso the listaProceso to set
    		 */
    		public void setListaProceso(RegistroDataModelImpl listaProceso) {
    			this.listaProceso = listaProceso;
    		}

    		/**
    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
