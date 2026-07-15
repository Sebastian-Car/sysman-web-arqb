/*-
 * SubabonosControlador.java
 *
 * 1.0
 *
 * 17/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.SubabonosControladorEnum;
import com.sysman.serviciospublicos.enums.SubabonosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 17/01/2017
 * @author acaceres
 *
 * @author eamaya
 * @version 2.0 , 16/06/2017 Proceso de Refactoring
 *
 * @author mzanguna
 * @version 2.1, 17/07/2017 Se adiciona condición consecutivo en la consulta principal.
 *
 * @author mzanguna
 * @version 2.2, 23/08/2017 Se ajusta el metodo cargarTotales.
 */
@ManagedBean
@ViewScoped

public class SubabonosControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo en el cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;

    /**
     * Constante que almacenara la cadena "CODIGO"
     */
    private final String codigoC;

    /**
     * Constante que almacenara la cadena "ciclo"
     */
    private final String cicloC;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo usado para almacenar el codigo de ruta que se trae desde el formulario abonos factura
     */
    private String codigoRuta;

    /**
     * Atributo usado para almacenar el ciclo que de trae desde el formulario abonos factura
     */
    private String ciclo;

    /**
     * Atributo usado para almacenar el ano que se trae desde el formulario abonos factura
     */
    private String ano;

    /**
     * Atributo usado para almacenra el periodo que se trae desde el formulario abonos factura
     */
    private String periodo;

    /**
     * Atributo usado para almacenar el consecutivo que se trae desde el formulario abonos factura
     */
    private String consecutivo;

    /**
     * Atributo usado para almacenar la suma del valor Anterior en el formulario
     */
    private String totalValAnt;

    /**
     * Atributo usado para almacenar la suma del valor actual en el formulario
     */
    private String totalValAct;

    /**
     * Atributo usado para almacenar la suma del valor total del abono.
     */
    private String totalAb;

    /**
     * Constante que almacenara el formato en el que se presentaran los valores totales de los subformularios
     */
    private final String formatoMoneda;

    /**
     * Atributo que almacena el codigo del banco que se trae desde el formulario abonos
     */
    private String bancoperproceso;

    /**
     * Atributo que almacena el codigo interno que se trae desde el formulario abonos
     */
    private String codigoInterno;

    /**
     * Atributo que almacena el txtfimm que se trae desde el formulario Abonos
     */
    private String txtFimm = "";

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     *
     */
    private RegistroDataModelImpl listaTexto23;
    /**
     *
     */
    private RegistroDataModelImpl listaTexto23E;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    /**
     * Esta variable se usa para almacenar el año actual del ciclo.
     */
    private String anioAct;

    /**
     * Esta variable se usa para almacenar el periodo actual del ciclo.
     */
    private String perAct;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubabonosControlador
     */
    public SubabonosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        formatoMoneda = "$ #,##0.00";
        codigoC = "CODIGO";
        cicloC = "ciclo";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBABONOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {

                /* Parametros que se traen del formulario Factura */
                ciclo = parametrosEntrada.get(cicloC).toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                codigoRuta = parametrosEntrada.get("codigoRuta").toString();
                consecutivo = parametrosEntrada.get("consecutivo")
                                .toString();
                txtFimm = parametrosEntrada.get("txtFimm").toString();
                bancoperproceso = parametrosEntrada.get("bancoperproceso")
                                .toString();
                codigoInterno = parametrosEntrada.get("codigoInterno")
                                .toString();

                anioAct = parametrosEntrada.get("anioAct").toString();
                perAct = parametrosEntrada.get("periodoAct").toString();

            }
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.SP_D_ABONOS;

        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTexto23();
        cargarListaTexto23E();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        cargarTotales();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);

        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);

        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaTexto23
     *
     *
     */
    public void cargarListaTexto23()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubabonosControladorUrlEnum.URL8735
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTexto23 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    /**
     *
     * Carga la lista listaTexto23
     *
     */
    public void cargarListaTexto23E()
    {
        listaTexto23E = listaTexto23;

    }

    // </METODOS_CARGAR_LISTA>

    /**
     * Metodo usado para cargar los totales en el formulario detalle de abonos.
     */
    public void cargarTotales()
    {

        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);

        param.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);

        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);

        HashMap<String, Object> registroTotales = new HashMap();
        try
        {

            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(SubabonosControladorUrlEnum.URL9349
                                            .getValue());
            registroTotales = (HashMap<String, Object>) requestManager.get(urlReg.getUrl(), param).getFields();
            if (Integer.parseInt(
                            SysmanFunciones.nvl(registroTotales.get(SubabonosControladorEnum.TOTALABONO.getValue()), 0).toString()) > 0)
            {

                totalValAnt = new java.text.DecimalFormat(formatoMoneda)
                                .format(
                                                Double.parseDouble(
                                                                registroTotales.get("VALORANT").toString()));
                totalValAct = new java.text.DecimalFormat(formatoMoneda)
                                .format(
                                                Double.parseDouble(
                                                                registroTotales.get("VALORACT").toString()));
                totalAb = new java.text.DecimalFormat(formatoMoneda).format(
                                Double.parseDouble(registroTotales.get("TOTALAB").toString()));

            }
            else
            {
                reiniciarTotales();
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo usado para reiniciar los valores de los totales del formulario en 0
     */
    private void reiniciarTotales()
    {
        totalValAnt = "0";
        totalValAct = "0";
        totalAb = "0";
    }

    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton cmdAbono en la vista
     *
     *
     */
    public void oprimircmdAbono()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo creado para generar el reporte 001073rptAbonosDiscriminado
     *
     * @param formatos
     */
    public void generarReporte(FORMATOS formatos)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(cicloC, ciclo);
            reemplazar.put("codigoRuta", codigoRuta);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta("001073rptAbonosDiscriminado",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001073rptAbonosDiscriminado", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTexto23
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto23(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO",
                        registroAux.getCampos().get(codigoC));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTexto23
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto23E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(codigoC);
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1260-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     *
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        boolean autorizarBorradoC = false;
        String formularioRedireccion = Integer
                        .toString(GeneralCodigoFormaEnum.ABONOSFACTURAS_CONTROLADOR
                                        .getCodigo());
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(cicloC, ciclo);
        parametros.put("codigoruta", codigoRuta);
        parametros.put("ano", anioAct);
        parametros.put("periodo", perAct);
        parametros.put("bancoperproceso", bancoperproceso);
        parametros.put("codigoInterno", codigoInterno);
        parametros.put("txtFimm", txtFimm);
        parametros.put("autorizarBorrado", autorizarBorradoC);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(formularioRedireccion);
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // Metodo que se hereda del bean base
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // Metodo que se hereda del bean base
    }

    // <SET_GET_ATRIBUTOS>
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTexto23
     *
     * @return listaTexto23
     */
    public RegistroDataModelImpl getListaTexto23()
    {
        return listaTexto23;
    }

    /**
     * Asigna la lista listaTexto23
     *
     * @param listaTexto23
     * Variable a asignar en listaTexto23
     */
    public void setListaTexto23(RegistroDataModelImpl listaTexto23)
    {
        this.listaTexto23 = listaTexto23;
    }

    /**
     * Retorna la lista listaTexto23
     *
     * @return listaTexto23
     */
    public RegistroDataModelImpl getListaTexto23E()
    {
        return listaTexto23E;
    }

    /**
     * Asigna la lista listaTexto23
     *
     * @param listaTexto23
     * Variable a asignar en listaTexto23
     */
    public void setListaTexto23E(RegistroDataModelImpl listaTexto23E)
    {
        this.listaTexto23E = listaTexto23E;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getTotalValAnt()
    {
        return totalValAnt;
    }

    public void setTotalValAnt(String totalValAnt)
    {
        this.totalValAnt = totalValAnt;
    }

    public String getTotalValAct()
    {
        return totalValAct;
    }

    public void setTotalValAct(String totalValAct)
    {
        this.totalValAct = totalValAct;
    }

    public String getTotalAb()
    {
        return totalAb;
    }

    public void setTotalAb(String totalAb)
    {
        this.totalAb = totalAb;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
