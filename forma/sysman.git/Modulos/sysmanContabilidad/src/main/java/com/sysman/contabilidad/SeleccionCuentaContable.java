/*-
 * SeleccionCuentaContable.java
 *
 * 1.0
 * 
 * 2/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.SeleccionCuentaContableUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

/**
 * Formulario que lista las cuentas con sus repectivos auxiliares en
 * una grilla. Se usa desde el formualrio del plan contable
 *
 * @version 1.0, 02/11/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class SeleccionCuentaContable extends BeanBaseContinuoNAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String ano;

    private String codigo;
    // <DECLARAR_ATRIBUTOS>

    private String nombreCuenta;

    private String indicador;

    private StreamedContent archivoDescarga;

    private Direccionador direccionador;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SeleccionCuentaContable
     */
    public SeleccionCuentaContable() {
        super();
        compania = SessionUtil.getCompania();
        direccionador = new Direccionador();
        try {
            numFormulario = GeneralCodigoFormaEnum.SELECCION_CUENTA_CONTABLE
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            nombreCuenta = parametrosEntrada
                            .get(GeneralParameterEnum.NOMBRE.getName())
                            .toString();
            ano = parametrosEntrada.get("ano").toString();
            codigo = parametrosEntrada.get("codigo").toString();

            indicador = parametrosEntrada.get("indicador").toString();

            direccionador = (Direccionador) parametrosEntrada
                            .get("direccionador");

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
        tabla = "";
        reasignarOrigen();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("ANIO", ano);

        parametrosListado.put("CODIGOF", codigo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeleccionCuentaContableUrlEnum.URL3665
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton VerCuenta
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirVerCuenta(Registro reg, int indice) {
        String id = reg.getCampos().get("ID").toString();
        String nombre = reg.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        switch (indicador) {
        case "grafica":
            descargarGrafica(id, nombre);
            break;

        case "movimientos":
            redireccionarMovimientos(id);
            break;
        case "saldos":
            redireccionarSaldos(id);
            break;
        default:
            break;
        }
    }

    public void descargarGrafica(String id, String nombre) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE_CUENTA", nombre);
            parametros.put("PR_CUENTA", id);
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("id", id);
            reemplazar.put("anio", ano);
            Reporteador.resuelveConsulta("001152graficaSaldosCT",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001152graficaSaldosCT",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void redireccionarMovimientos(String id) {

        direccionador.getParametros().put("cuenta", id);
        RequestContext.getCurrentInstance().closeDialog(direccionador);

    }

    private void redireccionarSaldos(String id) {
        direccionador.getParametros().put(GeneralParameterEnum.CUENTA.getName(),
                        id);
        RequestContext.getCurrentInstance().closeDialog(direccionador);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreCuenta
     * 
     * @return nombreCuenta
     */
    public String getNombreCuenta() {
        return nombreCuenta;
    }

    /**
     * Asigna la variable nombreCuenta
     * 
     * @param nombreCuenta
     * Variable a asignar en nombreCuenta
     */
    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
