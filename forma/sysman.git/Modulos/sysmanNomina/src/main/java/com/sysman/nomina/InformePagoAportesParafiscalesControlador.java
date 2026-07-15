/*-
 * InformePagoAportesParafiscalesControlador.java
 *
 * 1.0
 * 
 * 22/02/2018
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InformePagoAportesParafiscalesControladorEnum;
import com.sysman.nomina.enums.InformePagoAportesParafiscalesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario access informePagoAportesParafiscales a
 * web controlador InformePagoAportesParafiscalesControlador forma
 * InformePagoAportesParafiscalesControlador.java.xhtml creacion de
 * menu para abrir el formulario modal, creacion de properties para el
 * formulario modal, asi como generacion del informe
 * Listado_Parafiscales_Mensual a partir de un boton.
 *
 * @version 1.0, 22/02/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class InformePagoAportesParafiscalesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
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
    private String ano;
    /**
     * 
     */
    private String mes;
    /**
     * 
     */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */

    private int opcResumen;

    private int opcListado;
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * 
     */
    private List<Registro> listacmbProceso;
    /**
     * 
     */
    private List<Registro> listaAno;
    /**
     * 
     */
    private List<Registro> listaMes;
    /**
     * 
     */
    private List<Registro> listaPeriodo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * InformePagoAportesParafiscalesControlador
     */
    public InformePagoAportesParafiscalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = (String) SessionUtil.getSessionVar("anioNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        try {
            // 1721
            numFormulario = GeneralCodigoFormaEnum.INFORME_PAGOAPORTESPARAFISCALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        cargarListacmbProceso();
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
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
        /*
         * FR1721-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbProceso
     *
     * 
     */
    public void cargarListacmbProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacmbProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformePagoAportesParafiscalesControladorUrlEnum.URL_200
                                                                            .getValue())
                                            .getUrl(), param));
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
     * 
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformePagoAportesParafiscalesControladorEnum.ID_PROCESO
                        .getValue(), proceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformePagoAportesParafiscalesControladorUrlEnum.URL_226
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMes
     *
     * 
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformePagoAportesParafiscalesControladorEnum.PROCESO
                        .getValue(), proceso);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformePagoAportesParafiscalesControladorUrlEnum.URL_252
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     * 
     */
    public void cargarListaPeriodo() {
        // 471029
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(InformePagoAportesParafiscalesControladorEnum.PROCESO
                        .getValue(), proceso);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformePagoAportesParafiscalesControladorUrlEnum.URL_281
                                                                            .getValue())
                                            .getUrl(), param));
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
     * Metodo ejecutado al oprimir el boton PdfPresentar en la vista
     *
     * 
     *
     */
    public void oprimirPdfPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * 
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        // remplazos consulta
        Map<String, Object> remplazar = new HashMap<>();
        String reporte;
        remplazar.put("idProceso", proceso);
        remplazar.put("ano", ano);
        remplazar.put("mes", mes);
        remplazar.put("idPeriodo", periodo);
        // parametros reporte

        // EJB PARAMS
        Map<String, Object> params = new HashMap<>();
        params.put("PR_FORMS_INFORME_COMPENSACIONF_ANO1", ano);
        params.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        params.put("PR_FORMS_INFORME_COMPENSACIONF_MES1",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)]);
        String usuario = SysmanFunciones.concatenar(
                        SessionUtil.getUser().getNombre1(), " ",
                        SessionUtil.getUser().getApellido1(), " ",
                        SessionUtil.getUser().getApellido2());
        params.put("PR_GETUSER", usuario);
        params.put("PR_MOSTRAR",
                        "900.334.265-3".equals(SessionUtil
                                        .getCompaniaIngreso().getNit())
                                            ? false
                                            : true);

        try {
            String parCargoAutNomina = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "CARGO DE QUIEN AUTORIZA NOMINA",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", parCargoAutNomina);

            String parNombreAutNomina = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            parNombreAutNomina);

            String parCargoTesoreroPagador = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "CARGO DEL TESORERO PAGADOR",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            parCargoTesoreroPagador);

            String parNombreTesoreroPagador = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            parNombreTesoreroPagador);

            String parCargoDelGerente = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "CARGO DEL GERENTE",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_CARGO_DEL_GERENTE",
                            parCargoDelGerente);

            String parNombreDelGerente = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            "NOMBRE DEL GERENTE",
                            SessionUtil.getModulo(), new Date(), true);
            params.put("PR_NOMBRE_DEL_GERENTE",
                            parNombreDelGerente);

            params.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            ejbSysmanUtil.consultarParametro(
                                            SessionUtil.getCompania(),
                                            "NOMBRE DE QUIEN REVISA NOMINA",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            params.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            ejbSysmanUtil.consultarParametro(
                                            SessionUtil.getCompania(),
                                            "CARGO DE QUIEN REVISA NOMINA",
                                            SessionUtil.getModulo(), new Date(),
                                            true));

            params.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                            ejbSysmanUtil.consultarParametro(
                                            SessionUtil.getCompania(),
                                            "NOMBRE JEFE RECURSOS HUMANOS",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            params.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                            ejbSysmanUtil.consultarParametro(
                                            SessionUtil.getCompania(),
                                            "CARGO JEFE RECURSOS HUMANOS",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            params.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            if (opcListado == 1) {
                reporte = "001702ListadoParafiscalesMensual";
            }
            else if (opcListado == 3) {
            	params.put("PR_FORMS_INFORME_COMPENSACIONF_PERIODO1", periodo);
                reporte = "002471PagoCajasxCDC";
            }
            else if (opcListado == 4) {
            	params.put("PR_FORMS_INFORME_COMPENSACIONF_PERIODO1", periodo);
                reporte = "002902ListadoParafiscalesXGC";
            }
            else if (opcListado == 5) {
            	params.put("PR_FORMS_INFORME_COMPENSACIONF_PERIODO1", periodo);
                reporte = "002948ListadoParafiscalesXGCyCC";
            }
            else {
                params.put("PR_FORMS_INFORME_COMPENSACIONF_PERIODO1", periodo);
                reporte = "001987PagoCajasCompensacion";
            }

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            remplazar,
                            params);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            params, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control cmbProceso
     * 
     * 
     * 
     */
    public void cambiarcmbProceso() {
        // <CODIGO_DESARROLLADO>
        ano = null;
        mes = null;
        periodo = null;
        cargarListaAno();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
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
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
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
     * Retorna la lista listacmbProceso
     * 
     * @return listacmbProceso
     */
    public List<Registro> getListacmbProceso() {
        return listacmbProceso;
    }

    /**
     * Asigna la lista listacmbProceso
     * 
     * @param listacmbProceso
     * Variable a asignar en listacmbProceso
     */
    public void setListacmbProceso(List<Registro> listacmbProceso) {
        this.listacmbProceso = listacmbProceso;
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

    public int getOpcResumen() {
        return opcResumen;
    }

    public void setOpcResumen(int opcResumen) {
        this.opcResumen = opcResumen;
    }

    public int getOpcListado() {
        return opcListado;
    }

    public void setOpcListado(int opcListado) {
        this.opcListado = opcListado;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
