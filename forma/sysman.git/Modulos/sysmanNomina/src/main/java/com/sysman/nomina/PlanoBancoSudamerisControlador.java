/*-
 * PlanoBancoSudamerisControlador.java
 *
 * 1.0
 * 
 * 13/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.nomina.enums.PlanoBancoSudamerisControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sysman.util.SysmanConstantes;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

/**
 * Formulario que permite generacion de archivos planos del banco
 * Sudameris
 *
 * @version 1.0, 13/05/2019
 * @author asana
 * 
 * @version 2.0, 18/06/2019
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class PlanoBancoSudamerisControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String nitCompania;
    
    private String ano;

    private String mes;

    private String periodo;

    private String proceso;

    private String banco;

    private String descripPagoDetallada;

    private String descripPago;

    private String descripAmpliada;

    private String nombreMes;

    private String nombrePeriodo;

    private String nombreEntidad;
    
    private boolean manejaPlanoEstandar; 

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAnio;

    private List<Registro> listaMes;

    private List<Registro> listaPeriodo;

    private RegistroDataModelImpl listaBanco;

    @EJB
    private EjbNominaBancosRemote ejbNominaBancos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PlanoBancoSudamerisControlador
     */
    public PlanoBancoSudamerisControlador() {
        super();
        compania = SessionUtil.getCompania();
        nombreEntidad = SessionUtil.getCompaniaIngreso().getNombre();

        try {
            // 2072
            numFormulario = GeneralCodigoFormaEnum.PLANOBANCOSURAMERIS_CONTROLADOR
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
        ano = validaVacio(SessionUtil.getSessionVar("anioNomina"));
        mes = validaVacio(SessionUtil.getSessionVar("mesNomina"));
        periodo = validaVacio(SessionUtil.getSessionVar("periodoNomina"));
        proceso = validaVacio(SessionUtil.getSessionVar("procesoNomina"));
        cargarListaAnio();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListabancos();

        descripPagoDetallada = "PAGO DE NOMINA VIA ACH SUDAMERIS";
        nombreMes = service.buscarEnLista(mes, "MES",
                        GeneralParameterEnum.NOMBRE.getName(), listaMes);
        nombrePeriodo = service.buscarEnLista(periodo, "PERIODO",
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaPeriodo);
        descripAmpliada = "PAGO PERIODO :".concat(nombreMes).concat(" ")
                        .concat(nombrePeriodo);
        descripPago = "PAGO NOMINA VIA ACH DEL SUDAMERIS";
        
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        
        if(nitCompania.equals("890201222")) {
        	manejaPlanoEstandar = false;
        }else {
        	manejaPlanoEstandar = true;
        }

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

        // </CODIGO_DESARROLLADO>
    }
    // <METODOS_CARGAR_LISTA>

    public void cargarListaAnio() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanoBancoSudamerisControladorUrlEnum.URL28520
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanoBancoSudamerisControladorUrlEnum.URL28521
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanoBancoSudamerisControladorUrlEnum.URL28522
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListabancos() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = PlanoBancoSudamerisControladorUrlEnum.URL28523
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.BANCO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Actualizar en la vista
     *
     *
     */
    public void oprimirPlanoNomina() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String salida = null;
        ByteArrayInputStream streamTexto;
        try {
            salida = ejbNominaBancos.generarPlanoSudameris(
                            compania,
                            Integer.parseInt(ano),
                            Integer.parseInt(mes),
                            periodo,
                            banco,
                            descripPagoDetallada,
                            descripPago,
                            descripAmpliada);

            streamTexto = JsfUtil.serializarPlano(salida);

            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "Nomina" + nombreEntidad + mes + periodo + ano
                                + ".txt");
        }
        catch (NumberFormatException | SystemException | IOException
                        | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton planoTerceros en la vista
     *
     */
    public void oprimirplanoTerceros() {
        archivoDescarga = null;
        /*
         * Se crea variable tipoPlano TAR1000095436 con valor 0:Todos
         * los centros de costo, 1:Plano para un solo centro de costo,
         * 2:Plano para todos los centros de costo excepto el
         * configurado en el parámetro.
         */
        int tipoPlano = 0;
        String centroDeCosto = null;
        String[] nombreArchivo = new String[2];
        String[] archivo = new String[2];
        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "SUDAMERIS PLANO A TERCEROS POR CENTRO DE COSTO",
                            SessionUtil.getModulo(), new Date(), true))) {
                tipoPlano = 1;
                centroDeCosto = ejbSysmanUtil.consultarParametro(compania,
                                "SUDAMERIS PLANO A TERCEROS CODIGO CENTRO DE COSTO INDEPENDIENTE",
                                SessionUtil.getModulo(), new Date(), true);
            }

            archivo[0] = ejbNominaBancos.generarPlanoPagoTerSudameris(
                            compania,
                            Integer.parseInt(ano),
                            Integer.parseInt(mes),
                            periodo,
                            Integer.parseInt(proceso),
                            descripPagoDetallada,
                            descripPago,
                            descripAmpliada,
                            tipoPlano,
                            centroDeCosto);

            ByteArrayInputStream streamPlano1;
            streamPlano1 = JsfUtil.serializarPlano(archivo[0]);

            if (tipoPlano == 1) {
                tipoPlano = 2;
                archivo[1] = ejbNominaBancos.generarPlanoPagoTerSudameris(
                                compania,
                                Integer.parseInt(ano),
                                Integer.parseInt(mes),
                                periodo,
                                Integer.parseInt(proceso),
                                descripPagoDetallada,
                                descripPago,
                                descripAmpliada,
                                tipoPlano,
                                centroDeCosto);

                ByteArrayInputStream streamPlano2;
                streamPlano2 = JsfUtil.serializarPlano(archivo[1]);

                nombreArchivo[0] = SysmanFunciones.concatenar("PROVEADMON",
                                nombreMes.substring(0, 3), ano.substring(2, 4),
                                "_",
                                "CentroCosto", centroDeCosto);
                nombreArchivo[1] = SysmanFunciones.concatenar("PROVEADMON",
                                nombreMes.substring(0, 3), ano.substring(2, 4),
                                "_",
                                "OtrosCentrosDeCosto");

                ByteArrayInputStream[] listaArchivos = { streamPlano1,
                                                         streamPlano2 };
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                listaArchivos, nombreArchivo);
            }
            else {
                archivoDescarga = JsfUtil.getArchivoDescarga(streamPlano1,
                                "PROVEADMON" + nombreMes.substring(0, 3)
                                    + ano.substring(2, 4) + ".txt");
            }
        }
        catch (NumberFormatException | SystemException | IOException
                        | JRException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton informeTerceros en la
     * vista
     *
     */
    public void oprimirinformeTerceros() {
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
    }
    
    public void getInforme(FORMATOS formato)
    {
    	int tipoPlano = 0;
        String centroDeCosto = null;
        String reporte = "002702InformePagoTercerosSudameris";
        String reporte2 = "002703InformePagoTercerosSudamerisCentroCosto";
    	String[] arrayNombresJasper = new String[2];
        Map<String, Object>[] listaParametros = new HashMap[2];
        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> parametros2 = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>(); 
        HashMap<String, Object> reemplazar2 = new HashMap<>(); 
        Map<String, Object> param = new TreeMap<>();
        
    	try {	    	
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        Registro regSudameris = RegistroConverter.toRegistro(
                    requestManager.get(UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                    		PlanoBancoSudamerisControladorUrlEnum.URL28524
                                                                    .getValue())
                                    .getUrl(), param));
	        parametros.put("PR_NOMBRECOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getNombre());
	        String titulo = "PAGO TERCEROS MES "
	                        + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase()+" DE "+ ano;
	        parametros.put("PR_TITULO", titulo);
	        parametros.put("PR_CODIGO_SUDAMERIS", regSudameris.getCampos().get("CODIGO_NACHAM").toString());
	        parametros.put("PR_CUENTA_SUDAMERIS", regSudameris.getCampos().get("CUENTA").toString());
	        parametros.put("PR_TIPO_CUENTA_SUDAMERIS", regSudameris.getCampos().get("TIPO_CUENTA").toString());
	        parametros.put("PR_DESCRIPCION_DETALLADA", descripPagoDetallada);
	        parametros.put("PR_DESCRIPCION_PAGO", descripPago);
	        parametros.put("PR_DESCRIPCION_AMPLIADA", descripAmpliada);
	        
	        reemplazar.put("compania", compania);
	        reemplazar.put("ano", ano);
	        reemplazar.put("mes", mes);
	        reemplazar.put("periodo", periodo);
	        reemplazar.put("proceso", proceso);
	        reemplazar.put("centroCosto", "");   
        	
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "SUDAMERIS PLANO A TERCEROS POR CENTRO DE COSTO",
                            SessionUtil.getModulo(), new Date(), true))) {
                tipoPlano = 1;
                centroDeCosto = ejbSysmanUtil.consultarParametro(compania,
                                "SUDAMERIS PLANO A TERCEROS CODIGO CENTRO DE COSTO INDEPENDIENTE",
                                SessionUtil.getModulo(), new Date(), true);
                reemplazar2.put("compania", compania);
                reemplazar2.put("ano", ano);
                reemplazar2.put("mes", mes);
                reemplazar2.put("periodo", periodo);
                reemplazar2.put("proceso", proceso);
                reemplazar2.put("centroCostoIgual", "AND PERSONAL.ID_CENTRO_DE_COSTO = "+centroDeCosto);
                Reporteador.resuelveConsulta(reporte2,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar2, parametros);
                arrayNombresJasper[0] = reporte2;
                listaParametros[0] = parametros;
                if (tipoPlano == 1) 
                {
                    tipoPlano = 2;
                    reemplazar.put("centroCosto", "AND PERSONAL.ID_CENTRO_DE_COSTO <> "+centroDeCosto);
                    parametros2.put("PR_NOMBRECOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getNombre());			        
			        parametros2.put("PR_TITULO", titulo);
			        parametros2.put("PR_CODIGO_SUDAMERIS", regSudameris.getCampos().get("CODIGO_NACHAM").toString());
			        parametros2.put("PR_CUENTA_SUDAMERIS", regSudameris.getCampos().get("CUENTA").toString());
			        parametros2.put("PR_TIPO_CUENTA_SUDAMERIS", regSudameris.getCampos().get("TIPO_CUENTA").toString());
			        parametros2.put("PR_DESCRIPCION_DETALLADA", descripPagoDetallada);
			        parametros2.put("PR_DESCRIPCION_PAGO", descripPago);
			        parametros2.put("PR_DESCRIPCION_AMPLIADA", descripAmpliada);
                    Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros2);
                    arrayNombresJasper[1] = reporte;
                    listaParametros[1] = parametros2;
                    archivoDescarga = JsfUtil
                                    .exportarComprimidoReportesStreamed(
                                                    arrayNombresJasper,
                                                    listaParametros,
                                                    ConectorPool.ESQUEMA_SYSMAN,
                                                    formato);
                }                
            }
            else
            {
            	Reporteador.resuelveConsulta(
                        reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar,
                        parametros);
		        archivoDescarga = JsfUtil.exportarStreamed(
		                        reporte,
		                        parametros, ConectorPool.ESQUEMA_SYSMAN,
		                        formato);
            }
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ", reporte));
            Logger.getLogger(ResumenPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SQLException | JRException | IOException
                        | DRException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));

            Logger.getLogger(ResumenPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }            
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnio() {
        mes = null;

        cargarListaMes();
    }

    public void cambiarMes() {
        periodo = null;
        nombreMes = service.buscarEnLista(mes, "MES",
                        GeneralParameterEnum.NOMBRE.getName(), listaMes);
        descripAmpliada = "PAGO PERIODO : ".concat(nombreMes);
        cargarListaPeriodo();
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        nombrePeriodo = service.buscarEnLista(periodo, "PERIODO",
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaPeriodo);
        descripAmpliada = "PAGO PERIODO :".concat(nombreMes).concat(" ")
                        .concat(nombrePeriodo);
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux != null
            ? registroAux.getCampos().get("BANCO").toString()
            : "";

    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public String validaVacio(Object variable) {
        return variable == null ? null : variable.toString();
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
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

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
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable descripPagoDetallada
     * 
     * @return descripPagoDetallada
     */
    public String getDescripPagoDetallada() {
        return descripPagoDetallada;
    }

    /**
     * Asigna la variable descripPagoDetallada
     * 
     * @param descripPagoDetallada
     * Variable a asignar en descripPagoDetallada
     */
    public void setDescripPagoDetallada(String descripPagoDetallada) {
        this.descripPagoDetallada = descripPagoDetallada;
    }

    /**
     * Retorna la variable descripPago
     * 
     * @return descripPago
     */
    public String getDescripPago() {
        return descripPago;
    }

    /**
     * Asigna la variable descripPago
     * 
     * @param descripPago
     * Variable a asignar en descripPago
     */
    public void setDescripPago(String descripPago) {
        this.descripPago = descripPago;
    }

    /**
     * Retorna la variable descripAmpliada
     * 
     * @return descripAmpliada
     */
    public String getDescripAmpliada() {
        return descripAmpliada;
    }

    /**
     * Asigna la variable descripAmpliada
     * 
     * @param descripAmpliada
     * Variable a asignar en descripAmpliada
     */
    public void setDescripAmpliada(String descripAmpliada) {
        this.descripAmpliada = descripAmpliada;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

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
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

	public boolean isManejaPlanoEstandar() {
		return manejaPlanoEstandar;
	}

	public void setManejaPlanoEstandar(boolean manejaPlanoEstandar) {
		this.manejaPlanoEstandar = manejaPlanoEstandar;
	}

	public String getNitCompania() {
		return nitCompania;
	}

	public void setNitCompania(String nitCompania) {
		this.nitCompania = nitCompania;
	}

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
