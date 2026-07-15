/*-
 * InformesSieesControlador.java
 *
 * 1.0
 * 
 * 16/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;

/**
 * Clase migrada para generar el informe del SIEE
 *
 * @version 1.0, 16/11/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class InformesSieesControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atirbuto que almacena el codigo del modulo por el cual se
     * ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de InformesSieesControlador
     */
    public InformesSieesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1986
            numFormulario = GeneralCodigoFormaEnum.INFORME_SIEE_CONTROLADOR
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
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarExcel();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite generar el archivo excel del plan indicativo
     * por cuatrenio
     */
    public void generarExcel() {

        String consultaBase = "800260InformeSIEE";
        try {
            int anoVigencia = Integer.parseInt(SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                                            "52",
                                            new Date(), true),
                            String.valueOf(SysmanFunciones.ano(new Date()))));
            HashMap<String, Object> reemplazoUno = new HashMap<>();
            HashMap<String, Object> reemplazoDos = new HashMap<>();
            HashMap<String, Object> reemplazoTres = new HashMap<>();
            HashMap<String, Object> reemplazoCuatro = new HashMap<>();
            reemplazoUno.put("ano", anoVigencia);
            reemplazoDos.put("ano", anoVigencia + 1);
            reemplazoTres.put("ano", anoVigencia + 2);
            reemplazoCuatro.put("ano", anoVigencia + 3);
            String sqlUno = Reporteador.resuelveConsulta(consultaBase,
                            Integer.parseInt(modulo), reemplazoUno);
            String sqlDos = Reporteador.resuelveConsulta(consultaBase,
                            Integer.parseInt(modulo), reemplazoDos);
            String sqlTres = Reporteador.resuelveConsulta(consultaBase,
                            Integer.parseInt(modulo), reemplazoTres);
            String sqlCuatro = Reporteador.resuelveConsulta(consultaBase,
                            Integer.parseInt(modulo), reemplazoCuatro);
            String[] consultas = { sqlUno, sqlDos, sqlTres, sqlCuatro };
            String[] hojas = { String.valueOf(anoVigencia),
                               String.valueOf(anoVigencia + 1),
                               String.valueOf(anoVigencia + 2),
                               String.valueOf(anoVigencia + 3) };
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consultas,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL,
                            hojas);

        }
        catch (NumberFormatException | SystemException | DRException
                        | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto MensajeAlerta en
     * la vista
     *
     */
    public void ejecutarMensajeAlerta() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeError(idioma.getString(
                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
