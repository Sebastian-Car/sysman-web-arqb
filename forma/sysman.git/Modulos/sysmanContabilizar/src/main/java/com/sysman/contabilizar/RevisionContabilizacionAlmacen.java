/*-
 * RevisionContabilizacionAlmacen.java
 *
 * 1.0
 * 
 * 30 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.enums.RevisionContabilizacionAlmacenUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite revisar el proceso de contabilizar en
 * almacen
 *
 * @version 1.0, 30/07/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class RevisionContabilizacionAlmacen extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el numero del modulo
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el mes seleccionado
     */
    private String mes;
    /**
     * Variable que almacena el anio seleccionado
     */
    private String anio;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Variable que almacena el valor del parametro DIGITOS AGRUPACION
     * INVENTARIO
     */
    private String digitosAgrupacion;

    /**
     * Variable que almacena el valor del parametro FECHA DE CORTE
     * PARA INICIO DEL ALMACEN
     */

    private Date fechaCorteAlamcen;

    /**
     * Variable que almacena el titulo de la opcion de menu
     */
    private String tituloFormulario;
    
    private boolean conciliacion;
    

	private String encabezado;
    
    private Date fecha;
    private Date fechaFin;
    


	// </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los meses
     */
    private List<Registro> listaMes;
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de RevisionContabilizacionAlmacen
     */
    public RevisionContabilizacionAlmacen() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        
       
        try {
       
            numFormulario = GeneralCodigoFormaEnum.REVISION_CONTABILIZACION_ALMACEN
                            .getCodigo();
            validarPermisos();
            
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	 fecha = new Date();
    	 fechaFin = new Date();
    	 String menu = SessionUtil.getMenuActual();

        try {
            digitosAgrupacion = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);

            fechaCorteAlamcen = SysmanFunciones.convertirAFecha(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                                            modulo,
                                            new Date(),
                                        false));
            if("96030103".equals(menu)) {   
            conciliacion = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
     				compania,"INFORME CONCILIACION ALMACEN",
     				SessionUtil.getModulo(),new Date(),true ),"NO"));
            }

        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        cargarTitulo();
    }

    private void cargarTitulo() {

        String menu = SessionUtil.getMenuActual();
       

        switch (menu) {
        case "96030101":
            tituloFormulario = idioma.getString("TB_TB4324");
            encabezado = "CUENTA ALMACEN VS BALANCE CONTABLE"; 
            break;
        case "96030102":
            tituloFormulario = idioma.getString("TB_TB4325");
            encabezado = "RELACION DEPRECIACIONES VS CONTABLE"; 
            break;
        case "96030103":
        	if(conciliacion) { 
        		tituloFormulario = idioma.getString("TB_TB4507");
        		encabezado = "CONCILIACIÓN ALMACÉN"; 
        	}
        	else {
        		tituloFormulario = idioma.getString("TB_TB4326");
        		encabezado = "CONTABILIZACION ALMACEN"; 
        	}
            break;
        case "96030104":
            tituloFormulario = idioma.getString("TB_TB4327");
            encabezado = "MOVIMIENTOS NO CONFIGURADOS"; 
            break;
        default:
            tituloFormulario = "";
            encabezado = "";
            break;
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionAlmacenUrlEnum.URL4232
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionAlmacenUrlEnum.URL4624
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL);
    }

    private void generarReporte(FORMATOS formato) {
        String reporte;
        
        if (conciliacion && !validarFechas()) {
            return;
        }

        reporte = seleccionarReporte();

        Map<String, Object> reemplazos = new TreeMap<>();
        Map<String, Object> param = new TreeMap<>();
        param.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                .getNombre().toUpperCase());
        try {
        
        	 if(conciliacion) {
            	 String fechas = SysmanFunciones
         				.convertirAFechaCadena(fecha);
            	 String fechafinal = SysmanFunciones
          				.convertirAFechaCadena(fechaFin);
            	  
            	 anio = new SimpleDateFormat("yyyy").format(fecha);
            	 
            	 mes = new SimpleDateFormat("M").format(fecha);
                 reemplazos.put("fechas", fechas);
                 reemplazos.put("fechafinal", fechafinal);
            }
        	 
        	 reemplazos.put("ano", anio);
             reemplazos.put("mesFinal", mes);
            reemplazos.put("fechaCorte", SysmanFunciones
                            .convertirAFechaCadena(fechaCorteAlamcen));
            reemplazos.put("agrupacion", digitosAgrupacion);
            reemplazos.put("ultimoDia", SysmanFunciones.convertirAFechaCadena(
                            SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                            .convertirAFecha("01/" + mes + "/"
                                                + anio))));
           
            if(conciliacion) {

            	Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
            			reemplazos, param);

            	archivoDescarga = JsfUtil.exportarStreamed(reporte, param,
            			ConectorPool.ESQUEMA_SYSMAN, formato);

            }else {

            	String strSql = Reporteador
            			.resuelveConsulta(reporte,
            					Integer.parseInt(modulo),
            					reemplazos);

            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
            			ConectorPool.ESQUEMA_SYSMAN, formato,
            			tituloFormulario);
            }

        }
        catch (ParseException | JRException | IOException | SQLException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String seleccionarReporte() {
        String reporte = null;

        String menu = SessionUtil.getMenuActual();

        switch (menu) {
        case "96030101":
            reporte = "800327COMPARA_CUENTAALM_BALANCE";
            break;
        case "96030102":
            reporte = "800328COMPARACION_DEP_BALANCE";
            break;
        case "96030103":
        	if(conciliacion) {
        		reporte = "002938ConciliacionAlmacen";	
        	}else {
        		reporte = "800329INTERFACE_ALMACEN";
        	}
            break;
        case "96030104":
            reporte = "800330MOVIMIENTOS_NOCONFIGURADOS";
            break;
        default:

            break;
        }

        return reporte;
    }

    private boolean validarFechas() {
        boolean rta = true;
        String anioFechaInicial = new SimpleDateFormat("yyyy").format(fecha);
        String anioFechaFinal = new SimpleDateFormat("yyyy").format(fechaFin);
        if (fecha == null || fechaFin == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4509"));
            rta = false;
        }else if (fechaFin.before(fecha)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
            
 
        }
        if (!anio.equals(anioFechaInicial)
                || !anio.equals(anioFechaFinal)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4508"));
            fecha = null;
            fechaFin = null;
            rta = false;
        }
       
        return rta;
    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        mes = null;
        cargarListaMes();
        fechaFin = null;
        fecha = null;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }
    
    public String getEncabezado() {
		return encabezado;
	}
	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}
    
    public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
    public boolean isConciliacion() {
		return conciliacion;
	}



	public void setConciliacion(boolean conciliacion) {
		this.conciliacion = conciliacion;
	}
	
	public Date getFechaFin() {
		return fechaFin;
	}



	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}


    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
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
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
