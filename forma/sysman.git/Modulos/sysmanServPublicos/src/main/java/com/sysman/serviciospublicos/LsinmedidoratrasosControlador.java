/*-
 * LsinmedidoratrasosControlador.java
 *
 * 1.0
 *
 * 27/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LsinmedidoratrasosControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar el informe con el listado de Usuarios
 * sin medidor con periodos de atraso
 *
 * @version 1.0, 27/10/2016
 * @author ybecerra
 * 
 * @author eamaya
 * @version 2.0, 07/06/2017 Proceso de Refactoring y Manejo de EJBs
 */
@ManagedBean
@ViewScoped

public class LsinmedidoratrasosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por la cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del ciclo seleccionado en el
     * combo ciclo del formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena la condicion a enviar en al consulta del
     * informe, la condicion se selecciona en el combo condicion del
     * formulario
     */
    private String condicion;
    /**
     * Atributo que almacena el numero ingresado en el campo periodos
     * de atraso del formulario
     */
    private String atraso;
    /**
     * Atributo que almacena el ano del ciclo seleccionado
     */
    private int ano;

    /**
     * Atributo que almacena el periodo del ciclo seleccionado
     */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena la lista del combo Ciclo.
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LsinmedidoratrasosControlador
     */
    public LsinmedidoratrasosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LSINMEDIDORATRASOS_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
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
         * FR1159-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsinmedidoratrasosControladorUrlEnum.URL5296
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
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
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * Metodo que se ejecuta al hacer clic al boton pdf o excel del
     * formulario
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            String reporte = "001177LSinMedidorAtraso";

            String cond = "AND SP_USUARIO.PERIODOSATRASO ";
            String nombreCond = "";

            switch (condicion) {
            case "1":
                cond = cond + " = ";
                nombreCond = "Igual ";
                break;
            case "2":
                cond = cond + " < ";
                nombreCond = "Menor que ";
                break;
            case "3":
                cond = cond + " > ";
                nombreCond = "Mayor que ";
                break;

            default:
                break;
            }

            cond = cond + atraso;

            String facturacionSitio;
            String param = ejbSysmanUtil.consultarParametro(compania,
                            "FACTURACION EN SITIO", modulo, new Date(), false);

            if (param == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1693"));
                return;
            }
            else if ("SI".equals(param)) {
                facturacionSitio = "AND SP_USUARIO.FIMM IN('F')";
            }
            else {
                facturacionSitio = "";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "'" + ciclo + "'");
            reemplazar.put("facturacionSitio", facturacionSitio);
            reemplazar.put("cond", cond);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", "Ciclo: " + ciclo + ";" + " "
                + idioma.getString("TG_PERIODO6") + " "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .valueOf(periodo.replace("0", ""))]
                + "/" + ano);

            parametros.put("PR_ATRASO",
                            idioma.getString("TB_TB3293") + " " + nombreCond
                                + " " + atraso);
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("NUMERO").toString(), "");
        ano = Integer.parseInt(SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("ANO").toString(), ""));
        periodo = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("PERIODO").toString(), "");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable condicion
     *
     * @return condicion
     */
    public String getCondicion() {
        return condicion;
    }

    /**
     * Asigna la variable condicion
     *
     * @param condicion
     * Variable a asignar en condicion
     */
    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    /**
     * Retorna la variable atraso
     *
     * @return atraso
     */
    public String getAtraso() {
        return atraso;
    }

    /**
     * Asigna la variable atraso
     *
     * @param atraso
     * Variable a asignar en atraso
     */
    public void setAtraso(String atraso) {
        this.atraso = atraso;
    }

    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
