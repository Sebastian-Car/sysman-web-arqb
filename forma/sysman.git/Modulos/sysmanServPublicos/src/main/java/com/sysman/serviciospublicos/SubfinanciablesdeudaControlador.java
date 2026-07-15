/*-
 * SubfinanciablesdeudaControlador.Java
 *
 * 1.0
 *
 * 19 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyac�.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.SubfinanciablesdeudaControladorEnum;
import com.sysman.serviciospublicos.enums.SubfinanciablesdeudaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 12/09/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 *
 * -- Modificado por lcortes 16,17/06/2017. Refactorizacion de las consultas para usar dss, ajuste en el metodo cerrarFormulario.
 *
 */
@ManagedBean
@ViewScoped
public class SubfinanciablesdeudaControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String cCodigoRuta;
    private final String cCiclo;

    // <DECLARAR_ATRIBUTOS>

    /***/
    private StreamedContent archivoDescarga;

    /**
     * Almacenara el valor del ciclo que se trae por parametro del formulario que se llama
     */
    private String ciclo;

    /**
     * Almacenara el valor del codigo de ruta que se trae por parametro del formulario que se llama
     */
    private String codigoRuta;

    /**
     * Almacenara el valor del ano que se trae por parametro desde el formulario que se llama
     */
    private String ano;

    /**
     * Almacenara el valor del periodo que se trae por parametros desde el formulario que se llama
     */
    private String periodo;

    /**
     * Almacenara el valor del marcaDetalle que se trae por parametris desde el formulario en que se llama , define si cargaran los valores facturados o los valores facturados de la deuda
     */
    private String marcaDetalleFact;

    /**
     * Variable que almacena la condicion para las consultas que se llaman en los metodos nuevoOrigen y totales
     */
    String condicion;

    /**
     * Atributo que almacena la suma de los valores de la columna Deuda
     */
    private String totalDeuda;

    /**
     * Atributo que almacena la suma de los valores de la columna periodo
     */
    private String totalPeriodo;

    /**
     * Atributo que almacena la suma de los valores de la columna Total
     */
    private String total;
    /**
     * Se debe traer del formulario factura. // FACTURA!txtFimm Almacenara los planos son financiables
     */
    private String txtFimm = "";

    /**
     * Se trae desde el formulario Factura de la tabla SP_USUARIOS Indica la lectura que trae el codigo de ruta seleccionado
     *
     */
    private String lectura = "0";

    /**
     * Almacenara el codigo del banco que se trae del formulario FacturaIntegrado
     */
    String bancoPerProceso;
    /**
     * Numero de la factura que se trae desde el formulario Factura. Numero usado para hacer validaciones al cambiar el check bloqueado
     */
    private String numeroFactura = "1";

    /**
     * Almacenara el valor los periodos de no cobro para realizar validaciones. Se trae como parametro del formulario Factura de la tabla SP_USUARIO
     */
    private String periodoNoCobroFin = "1";
    /**
     * Alamcenara el codigo interno que se trae por parametro desde el formulario Factura
     */
    private String codigoInterno = "";

    /**
     * Almacenara los periodos que no se han cobrado, que se traen del formulario Factura de la tabla SP_USUARIO
     */
    String periodosNoCobradosFac = "";
    /**
     * Almacena el valor si se abre desde boton financiables del formulario factura
     */
    String financiable;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que almacenara el valor del combo concepto */
    private RegistroDataModel listaconcepto;

    /**
     * Lista que almacenara el valor del combo concepto en la grilla
     */
    private RegistroDataModel listaconceptoE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    /**
     * Creates a new instance of SubfinanciablesdeudaControlador
     */
    public SubfinanciablesdeudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigoRuta = "codigoRuta";
        cCiclo = "ciclo";
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFINANCIABLESDEUDA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                // Valores que se traen por parametro del formulario
                // Financiables
                ciclo = parametrosEntrada.get(cCiclo).toString();
                codigoRuta = parametrosEntrada.get(cCodigoRuta).toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                marcaDetalleFact = parametrosEntrada
                                .get("marcaDetalleFact").toString();
                codigoInterno = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigoInterno"), "")
                                .toString();
                bancoPerProceso = SysmanFunciones
                                .nvl(parametrosEntrada.get("bancoperproceso"),
                                                "")
                                .toString();
                txtFimm = SysmanFunciones
                                .nvl(parametrosEntrada.get("txtFimm"), "")
                                .toString();
                numeroFactura = SysmanFunciones
                                .nvl(parametrosEntrada.get("numeroFactura"), "")
                                .toString();
                lectura = SysmanFunciones
                                .nvl(parametrosEntrada.get("lectura"), "")
                                .toString();
                periodoNoCobroFin = SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get("periodosNoCobroFin"), "")
                                .toString();

                periodosNoCobradosFac = SysmanFunciones
                                .nvl(parametrosEntrada.get(
                                                "periodosNoCobradosFac"), "")
                                .toString();
                financiable = SysmanFunciones
                                .nvl(parametrosEntrada.get("financiable"), "")
                                .toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubfinanciablesdeudaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = SubfinanciablesdeudaControladorEnum.PARAM1.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        parametrosListado.put(
                        SubfinanciablesdeudaControladorEnum.PARAM0.getValue(),
                        marcaDetalleFact);

        try {
            if (!SysmanFunciones.concatenar(ano, ",", periodo).equals(
                            ejbServiciosPublicosOcho.obtenerAnioPeriodoActual(
                                            compania,
                                            Integer.parseInt(ciclo)))) {
                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubfinanciablesdeudaControladorUrlEnum.URL0005
                                                                .getValue());
            }
            else if ("1".equals(marcaDetalleFact)) {
                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubfinanciablesdeudaControladorUrlEnum.URL0001
                                                                .getValue());
            }
            else if ("2".equals(marcaDetalleFact)) {
                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubfinanciablesdeudaControladorUrlEnum.URL0002
                                                                .getValue());
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>

    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>

    /**
     * Metodo creado para generar el reporte 001073rptAbonosDiscriminado
     *
     * @param formatos
     */
    public void generarReporte(FORMATOS formatos) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(cCiclo, ciclo);
        reemplazar.put(cCodigoRuta, codigoRuta);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta("001073rptAbonosDiscriminado",
                        Integer.parseInt(modulo), reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001073rptAbonosDiscriminado", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite generar el informe en formaro Pdf.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite generar el informe en formato Excel.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        // </CODIGO_DESARROLLADO>
        if ("1".equals(financiable)) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(cCiclo, ciclo);
            parametros.put(cCodigoRuta, codigoRuta);
            parametros.put("ano", ano);
            parametros.put("periodo", periodo);
            parametros.put("codigoInterno", codigoInterno);
            parametros.put("bancoPerProceso", bancoPerProceso);
            parametros.put("periodosNoCobradosFac", periodosNoCobradosFac);
            parametros.put("txtFimm", txtFimm);
            parametros.put("numeroFactura", numeroFactura);
            parametros.put("lectura", lectura);
            parametros.put("periodosNoCobroFin", periodoNoCobroFin);
            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(parametros);
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FINANCIABLESFACTURAS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        }
        else {
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     *
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>

    private void totales() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(SubfinanciablesdeudaControladorEnum.PARAM0.getValue(),
                        marcaDetalleFact);

        try {
            Registro rs;
            if (!SysmanFunciones.concatenar(ano, ",", periodo).equals(
                            ejbServiciosPublicosOcho.obtenerAnioPeriodoActual(
                                            compania,
                                            Integer.parseInt(ciclo)))) {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubfinanciablesdeudaControladorUrlEnum.URL0005 // 006
                                                                                .getValue())
                                                .getUrl(), param));
            }
            else if ("1".equals(marcaDetalleFact)) {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubfinanciablesdeudaControladorUrlEnum.URL0003
                                                                                .getValue())
                                                .getUrl(), param));

            }
            else {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubfinanciablesdeudaControladorUrlEnum.URL0004
                                                                                .getValue())
                                                .getUrl(), param));
            }

            if (rs == null) {
                totalDeuda = "0";
                totalPeriodo = "0";
                total = "0";
            }
            else {
                totalDeuda = SysmanFunciones.nvlStr(
                                rs.getCampos().get("FDEUDA").toString(), "0");
                totalPeriodo = SysmanFunciones.nvlStr(
                                rs.getCampos().get("FPERIODO").toString(), "0");
                total = SysmanFunciones.nvlStr(
                                rs.getCampos().get("TOTALFIN").toString(), "0");
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        totales();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTotalDeuda() {
        return totalDeuda;
    }

    public void setTotalDeuda(String totalDeuda) {
        this.totalDeuda = totalDeuda;
    }

    public String getTotalPeriodo() {
        return totalPeriodo;
    }

    public void setTotalPeriodo(String totalPeriodo) {
        this.totalPeriodo = totalPeriodo;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModel getListaconcepto() {
        return listaconcepto;
    }

    public RegistroDataModel getListaconceptoE() {
        return listaconceptoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
