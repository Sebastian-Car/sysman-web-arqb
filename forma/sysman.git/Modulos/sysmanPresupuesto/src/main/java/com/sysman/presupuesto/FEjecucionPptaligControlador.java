/*-
 * FEjecucionPptaligControlador.java
 *
 * 1.0
 * 
 * 23/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.kernel.templates.annotations.Refactoring;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FEjecucionPptaligControladorEnum;
import com.sysman.presupuesto.enums.FEjecucionPptaligControladorUrlEnum;
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

/**
 * Formulario que genera el reporte de ejecución presupuestal.
 *
 * @version 1.0, 23/09/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class FEjecucionPptaligControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que almacena el valor de la casilla de verificacion
     * auxiliar del formulario
     */
    private boolean auxiliar;
    /**
     * variable que almacena el valor del codigo de la cuenta inicial
     * seleccionado en el combo cuenta inicial del formulario
     */
    private String cuentaInicial;
    /**
     * variable que almacena el valor del codigo de la cuenta final
     * seleccionado en el combo cuenta final del formulario
     */
    private String cuentaFinal;
    /**
     * variable que almacena el valor del mes seleccionado
     */
    private String mes;
    /**
     * variable que almacena el valor del anio seleccionado
     */
    private String anio;
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
     * lista de registros del combo mes
     */
    private List<Registro> listaMes;
    /**
     * lista de registros del combo ano
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista de registros del combo cuenta inicial del formulario
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * lista de regidtros del combo cuenta final del formulario
     */
    private RegistroDataModelImpl listaCuentaFinal;
    
    private boolean indFuenteCuipo;
    
  

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FEjecucionPptaligControlador
     */
    public FEjecucionPptaligControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            numFormulario = GeneralCodigoFormaEnum.F_EJECUCION_P_PTALIG_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        cargarListaCuentaInicial();

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
        /*
         * FR2106-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        Map<String, Object> parametro = new TreeMap<>();

        parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametro.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FEjecucionPptaligControladorUrlEnum.URL5091
                                                                            .getValue())
                                            .getUrl(),
                            parametro));
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

        Map<String, Object> parametro = new TreeMap<>();

        parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FEjecucionPptaligControladorUrlEnum.URL5295
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametro));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FEjecucionPptaligControladorUrlEnum.URL5495
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FEjecucionPptaligControladorUrlEnum.URL6186
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
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
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInformes(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirExcel() {
        archivoDescarga = null;
        generarInformes(ReportesBean.FORMATOS.EXCEL97);
            
        }
    
    /**
     * Metodo ejecutado al darle clic en el boton pdf o excel
     * 
     * @param formato
     */
    public void generarInformes(ReportesBean.FORMATOS formato) {
        
        
        try {
        String reporte;
        if(indFuenteCuipo) {
    		reporte = "002717FEjecucionPptalIG";
    	}
    	else {
    		reporte = "002049FEjecucionPptalIG";
    	}
        Map<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        
        reemplazos.put("anio", anio);
        reemplazos.put("mes", mes);
        reemplazos.put("cuentaInicial", cuentaInicial);
        reemplazos.put("cuentaFinal", cuentaFinal);
        reemplazos.put("condicionaux", auxiliar== false?"AND EJECUCIONPPTALIG.MOVIMIENTO In (0)":"");
        
        
        String paramteroFirma1 = ejbSysmanUtilRemote.consultarParametro(compania, "FIRMA 1 EN PRESUPUESTO", modulo, new Date(), true);
        String paramteroFirma2 = ejbSysmanUtilRemote.consultarParametro(compania, "FIRMA 2 EN PRESUPUESTO", modulo, new Date(), true);
        String paramteroCargo1 = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO 1 EN PRESUPUESTO", modulo, new Date(), true);
        String paramteroCargo2 = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO 2 EN PRESUPUESTO", modulo, new Date(), true);
        
        Reporteador.resuelveConsulta(reporte,  Integer.parseInt(modulo), reemplazos, parametros);
        
        parametros.put("PR_FIRMA_1_EN_PRESUPUESTO", paramteroFirma1 );
        parametros.put("PR_FIRMA_2_EN_PRESUPUESTO", paramteroFirma2 );
        parametros.put("PR_CARGO_1_EN_PRESUPUESTO", paramteroCargo1 );
        parametros.put("PR_CARGO_2_EN_PRESUPUESTO", paramteroCargo2 );
        
     

    
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            
        }
        catch (JRException | IOException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
          
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        mes = null;
        cuentaInicial=null;
        cuentaFinal=null;
        cargarListaMes();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }
    
    
    public void cambiarAuxiliar() {
   }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        cuentaFinal = null;
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
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public boolean getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
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
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    
    public boolean isIndFuenteCuipo() {
  		return indFuenteCuipo;
  	}

  	public void setIndFuenteCuipo(boolean indFuenteCuipo) {
  		this.indFuenteCuipo = indFuenteCuipo;
  	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
