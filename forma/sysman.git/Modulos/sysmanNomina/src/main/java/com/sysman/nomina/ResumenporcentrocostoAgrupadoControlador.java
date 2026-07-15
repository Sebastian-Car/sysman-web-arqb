/*-
 * ResumenporcentrocostoAgrupadoControlador.java
 *
 * 1.0
 *
 * 05/04/2021
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenporcentrocostoAgrupadoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 05/04/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class ResumenporcentrocostoAgrupadoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * constante quye almacena el modulo
     */
    private final String modulo;
    private static final String ID_DE_CONCEPTO = "ID_DE_CONCEPTO";
    private String ano;
    private String opcion;
    private String ano1;
    private String validacionRepor;
    /**
     * variable que almacena el ano1
     */
    private String ano2;
    /**
     * variable que almacena el año2
     */
    private String mes1;
    /**
     * variable que almacena el mes1
     */
    private String mes2;
    /**
     * variable que almacena el mes2
     */
    private String periodo1;
    /**
     * variable que almacena el periodo1
     */
    private String periodo2;
    /**
     * variable que almacena el periodo2
     */
    private String proceso;
    /**
     * variable que almacena el proceso
     */
    private String concepto1;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private String CentroCosto;
    /**
     * variable que almacena el centro de costo
     */
    private String selCentroCosto;
    /**
     * variable que valida la seleccion del centro de costo
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Variable lista Ano1
     */
    private List<Registro> listaAno1;
    /**
     * variable lista Ano2
     */
    private List<Registro> listaAno2;
    /**
     * variable lista Ano2
     */
    private List<Registro> listaMes1;
    /**
     * varaible lista mes1
     */
    private List<Registro> listaMes2;
    /**
     * variable lista mes2
     */
    private List<Registro> listaPeriodo1;
    /**
     * variable lista periodo1
     */
    private List<Registro> listaPeriodo2;
    /**
     * variabke lista periodo2
     */
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * variable lista proceso
     */
    private List<Registro> listaCentroCosto;
    /**
     * 
     */
    private RegistroDataModelImpl listaConcepto1;
    private RegistroDataModel listaEmpleadoI;
    private RegistroDataModel listaEmpleadof;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ResumenporcentrocostoAgrupadoControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public ResumenporcentrocostoAgrupadoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
        ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
        ano1 = ano2 = ano;
        mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
        mes2 = mes1;
        periodo1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
        periodo2 = periodo1;
        try
        {
            // 2253
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_POR_CENTROCOSTO_AGRUPADO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumenporcentrocostoAgrupadoControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConcepto1();
        cargarListaCentroCosto();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        opcion = "1";
        // <CODIGO_DESARROLLADO>
        /*
         * FR2253-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 1, Me.Name Me.Caption = NombreEmpresa(0) Opcion_AfterUpdate End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno1
     *
     */
    public void cargarListaAno1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4751
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAno2
     *
     */
    public void cargarListaAno2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4751
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        try
        {
            listaMes1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4752
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes2
     *
     */
    public void cargarListaMes2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano2);
        try
        {
            listaMes2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4752
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo1
     *
     */
    public void cargarListaPeriodo1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        try
        {
            listaPeriodo1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4753
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo2
     *
     */
    public void cargarListaPeriodo2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano2);
        param.put(GeneralParameterEnum.MES.getName(), mes2);
        try
        {
            listaPeriodo2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4753
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4750
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaConcepto1
     *
     */
    public void cargarListaConcepto1()
    {
        String urlEnumId = ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4755.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ID_DE_CONCEPTO);

    }

    public void cargarListaCentroCosto()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            listaCentroCosto = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenporcentrocostoAgrupadoControladorUrlEnum.URL4754
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "1";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentart en la vista
     *
     *
     */
    public void oprimirPresentart()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "1";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // archivoDescarga=null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PARAFISCAL en la vista
     *
     *
     */
    public void oprimirPARAFISCAL()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "3";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton salud en la vista
     *
     *
     */
    public void oprimirsalud()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "5";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PENSION en la vista
     *
     *
     */
    public void oprimirPENSION()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "6";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RIESGOS en la vista
     *
     *
     */
    public void oprimirRIESGOS()
    {
        // <CODIGO_DESARROLLADO>
        validacionRepor = "7";
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton LISTADOCAJA en la vista
     *
     *
     */
    public void oprimirLISTADOCAJA()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CONCEPTO en la vista
     *
     *
     */
    public void oprimirCONCEPTO()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano1
     * 
     * 
     */
    public void cambiarAno1()
    {
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    /**
     * Metodo ejecutado al cambiar el control Ano2
     * 
     * 
     */
    public void cambiarAno2()
    {
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    /**
     * Metodo ejecutado al cambiar el control Mes1
     * 
     * 
     */
    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes2
     * 
     * 
     */
    public void cambiarMes2()
    {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Proceso
     * 
     * 
     */
    public void cambiarProceso()
    {
        // <CODIGO_DESARROLLADO>
        ano1 = null;
        ano2 = null;
        mes1 = null;
        mes2 = null;
        periodo1 = null;
        periodo2 = null;
        cargarListaAno1();
        cargarListaAno2();
        // </CODIGO_DESARROLLADO>
    }

    public void getReporte(FORMATOS formatos)
    {
        archivoDescarga = null;
        String reporte = null;
        String nombreInforme = "";
        String centroP = "";
        if (opcion.equals("1"))
        {
            centroP = "";
        }
        else
        {
            centroP = "AND CENTRO_COSTO.CODIGO = '" + selCentroCosto + "'";
        }
        try
        {

            if (validacionRepor.equals("6"))
            {
                reporte = "002237ResumenAutoPensionCCNarino";
                nombreInforme = "002237ResumenAutoPensionCCNarino";
            }
            else if (validacionRepor.equals("3"))
            {
                reporte = "00249LISTADOPARAFISCALESSTRNUEVOCCOK";
                nombreInforme = "00249LISTADOPARAFISCALESSTRNUEVOCCOK";
            }
            else if (validacionRepor.equals("7"))
            {
                reporte = "002280PlanillaresumenautoriesgosucursalCCNUEVOcc";
                nombreInforme = "002280PlanillaresumenautoriesgosucursalCCNUEVOcc";
            }
            else if (validacionRepor.equals("5"))
            {
                reporte = "002229ResumenAutoSaludNarino";
                nombreInforme = "002229ResumenAutoSaludNarino";
            }

            else if (validacionRepor.equals("1"))
            {
                reporte = "002247ResumenPorCentroCostoNarino";
                nombreInforme = "002247ResumenPorCentroCostoNarino";
            }
            /*
             * else { reporte = "002247ResumenPorCentroCostoNarino"; nombreInforme ="002247ResumenPorCentroCostoNarino"; }
             */

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proceso", proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("anio2", ano2);
            reemplazar.put("mes1", mes1);
            reemplazar.put("mes2", mes2);
            reemplazar.put("periodo1", periodo1);
            reemplazar.put("periodo2", periodo2);
            reemplazar.put("centroP", centroP);

            Map<String, Object> parametros = new HashMap<>();
            String parametroEntreRepo = SysmanFunciones.concatenar("De: ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1);

            String parametroEntre = idioma.getString("TB_TB3745")
                            .replace("s$mes1$s", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes1)])
                            .replace("s$ano1$s", ano1).replace("s$periodo1$s", periodo1)
                            .replace("s$mes2$s", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes2)])
                            .replace("s$ano2$s", ano2).replace("s$periodo2$s", periodo2);

            String jefeseguridadSocial = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN ELABORA RESUMEN SEGURIDAD SOCIAL",
                            modulo, new Date(), false);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN ELABORA SEGURIDAD SOCIAL",
                            modulo, new Date(), false);
            String nombreAutoriza = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                            modulo, new Date(), false);

            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL JEFE DE NOMINA", modulo, new Date(), false);

            parametros.put("PR_ANO1", ano1);
            parametros.put("PR_MES1", mes1);
            parametros.put("PR_PERIODO1", periodo1);
            parametros.put("PR_ENTRE", parametroEntre);
            parametros.put("PR_ENTREREPO", parametroEntreRepo);
            parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRE_DE_QUIEN_ELABORA_RESUMEN_SEGURIDAD_SOCIAL", jefeseguridadSocial);
            parametros.put("PR_CARGO_DE_QUIEN_ELABORA_SEGURIDAD_SOCIAL", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nombreAutoriza);
            parametros.put("PR_CARGO_DEL_JEFE_DE_NOMINA", jefeNomina);

            String strsql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);

            if (service.getConteoConsulta(strsql) <= 0)
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                return;
            }

            parametros.put("PR_STRSQL", strsql);

            archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (JRException | IOException | SysmanException | SystemException ex)
        {
            Logger.getLogger(ResumenporcentrocostoAgrupadoControlador.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaConcepto1
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto1(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        concepto1 = registroAux.getCampos().get(ID_DE_CONCEPTO).toString();
    }

    public void onRowSelectEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        selCentroCosto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

    }

    public void cambiarOpcion()
    {
        // <CODIGO_DESARROLLADO>

        // <CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano1
     * 
     * @return ano1
     */
    public String getAno1()
    {
        return ano1;
    }

    /**
     * Asigna la variable ano1
     * 
     * @param ano1
     * Variable a asignar en ano1
     */
    public void setAno1(String ano1)
    {
        this.ano1 = ano1;
    }

    /**
     * Retorna la variable ano2
     * 
     * @return ano2
     */
    public String getAno2()
    {
        return ano2;
    }

    /**
     * Asigna la variable ano2
     * 
     * @param ano2
     * Variable a asignar en ano2
     */
    public void setAno2(String ano2)
    {
        this.ano2 = ano2;
    }

    /**
     * Retorna la variable mes1
     * 
     * @return mes1
     */
    public String getMes1()
    {
        return mes1;
    }

    /**
     * Asigna la variable mes1
     * 
     * @param mes1
     * Variable a asignar en mes1
     */
    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    /**
     * Retorna la variable mes2
     * 
     * @return mes2
     */
    public String getMes2()
    {
        return mes2;
    }

    /**
     * Asigna la variable mes2
     * 
     * @param mes2
     * Variable a asignar en mes2
     */
    public void setMes2(String mes2)
    {
        this.mes2 = mes2;
    }

    /**
     * Retorna la variable periodo1
     * 
     * @return periodo1
     */
    public String getPeriodo1()
    {
        return periodo1;
    }

    /**
     * Asigna la variable periodo1
     * 
     * @param periodo1
     * Variable a asignar en periodo1
     */
    public void setPeriodo1(String periodo1)
    {
        this.periodo1 = periodo1;
    }

    /**
     * Retorna la variable periodo2
     * 
     * @return periodo2
     */
    public String getPeriodo2()
    {
        return periodo2;
    }

    /**
     * Asigna la variable periodo2
     * 
     * @param periodo2
     * Variable a asignar en periodo2
     */
    public void setPeriodo2(String periodo2)
    {
        this.periodo2 = periodo2;
    }

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
     * Retorna la variable concepto1
     * 
     * @return concepto1
     */
    public String getConcepto1()
    {
        return concepto1;
    }

    /**
     * Asigna la variable concepto1
     * 
     * @param concepto1
     * Variable a asignar en concepto1
     */
    public void setConcepto1(String concepto1)
    {
        this.concepto1 = concepto1;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
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
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    /**
     * Retorna la lista listaAno2
     * 
     * @return listaAno2
     */
    public List<Registro> getListaAno2()
    {
        return listaAno2;
    }

    /**
     * Asigna la lista listaAno2
     * 
     * @param listaAno2
     * Variable a asignar en listaAno2
     */
    public void setListaAno2(List<Registro> listaAno2)
    {
        this.listaAno2 = listaAno2;
    }

    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    /**
     * Retorna la lista listaMes2
     * 
     * @return listaMes2
     */
    public List<Registro> getListaMes2()
    {
        return listaMes2;
    }

    /**
     * Asigna la lista listaMes2
     * 
     * @param listaMes2
     * Variable a asignar en listaMes2
     */
    public void setListaMes2(List<Registro> listaMes2)
    {
        this.listaMes2 = listaMes2;
    }

    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    /**
     * Retorna la lista listaPeriodo2
     * 
     * @return listaPeriodo2
     */
    public List<Registro> getListaPeriodo2()
    {
        return listaPeriodo2;
    }

    /**
     * Asigna la lista listaPeriodo2
     * 
     * @param listaPeriodo2
     * Variable a asignar en listaPeriodo2
     */
    public void setListaPeriodo2(List<Registro> listaPeriodo2)
    {
        this.listaPeriodo2 = listaPeriodo2;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso()
    {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConcepto1
     * 
     * @return listaConcepto1
     */
    public RegistroDataModelImpl getListaConcepto1()
    {
        return listaConcepto1;
    }

    /**
     * Asigna la lista listaConcepto1
     * 
     * @param listaConcepto1
     * Variable a asignar en listaConcepto1
     */
    public void setListaConcepto1(RegistroDataModelImpl listaConcepto1)
    {
        this.listaConcepto1 = listaConcepto1;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * @return the selCentroCosto
     */
    public String getSelCentroCosto()
    {
        return selCentroCosto;
    }

    /**
     * @param selCentroCosto
     * the selCentroCosto to set
     */
    public void setSelCentroCosto(String selCentroCosto)
    {
        this.selCentroCosto = selCentroCosto;
    }

    /**
     * @return the listaCentroCosto
     */
    public List<Registro> getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    /**
     * @param listaCentroCosto
     * the listaCentroCosto to set
     */
    public void setListaCentroCosto(List<Registro> listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    public RegistroDataModel getListaEmpleadoI()
    {
        return listaEmpleadoI;
    }

    public void setListaEmpleadoI(RegistroDataModel listaEmpleadoI)
    {
        this.listaEmpleadoI = listaEmpleadoI;
    }

    public RegistroDataModel getListaEmpleadof()
    {
        return listaEmpleadof;
    }

    /**
     * @return the opcion
     */
    public String getOpcion()
    {
        return opcion;
    }

    /**
     * @param opcion
     * the opcion to set
     */
    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

}
