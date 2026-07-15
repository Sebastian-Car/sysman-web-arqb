/*-
 * PlanoMorosoControlador.java
 *
 * 1.0
 *
 * 20/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCincoRemote;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase encargada de hacer el proceso de generar los deudores morosos y generar un informe
 *
 * @version 1.0, 20/02/2017
 * @author jguerrero
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 10/07/2017 - refactorizo dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class PlanoMorosoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena temporalmente lo seleccionado en el check del formulario
     */
    private String checkCedulasNulas;
    /**
     * Variable que almacena temporalmente lo digitado en el formulario en el campo deuda maxima
     */
    private String deudaMaxima;
    /**
     * Variable que almacena temporalmente lo seleccionado en la fecha de corte en el formulario
     */
    private Date fechaCorte;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbPredialCincoRemote ejbPredialCinco;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PlanoMorosoControlador
     */
    public PlanoMorosoControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.PLANO_MOROSO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton generar en la vista
     *
     *
     */
    public void oprimirgenerar()
    {
        // <CODIGO_DESARROLLADO>
        logicaPlanoMoroso(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton generarExcel en la vista
     *
     *
     */
    public void oprimirgenerarExcel()
    {
        // <CODIGO_DESARROLLADO>
        logicaPlanoMoroso(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto mensajeInformativo en la vista
     *
     *
     */
    public void ejecutarmensajeInformativo()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control deuda
     *
     *
     */
    public void cambiardeuda()
    {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(deudaMaxima) <= 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2860"));
            deudaMaxima = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkCedulasNulas
     *
     * @return checkCedulasNulas
     */
    public String getCheckCedulasNulas()
    {
        return checkCedulasNulas;
    }

    /**
     * Asigna la variable checkCedulasNulas
     *
     * @param checkCedulasNulas
     * Variable a asignar en checkCedulasNulas
     */
    public void setCheckCedulasNulas(String checkCedulasNulas)
    {
        this.checkCedulasNulas = checkCedulasNulas;
    }

    /**
     * Retorna la variable deudaMaxima
     *
     * @return deudaMaxima
     */
    public String getDeudaMaxima()
    {
        return deudaMaxima;
    }

    /**
     * Asigna la variable deudaMaxima
     *
     * @param deudaMaxima
     * Variable a asignar en deudaMaxima
     */
    public void setDeudaMaxima(String deudaMaxima)
    {
        this.deudaMaxima = deudaMaxima;
    }

    public Date getFechaCorte()
    {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte)
    {
        this.fechaCorte = fechaCorte;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable fechaCorte
     *
     * @return fechaCorte
     */

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;

        String reporte = "001422PLANOMOROSOS";
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("SINCEDULAS",
                            "true".equals(checkCedulasNulas)
                                ? "WHERE NIT IS NOT NULL" : "");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(PlanoMorosoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void logicaPlanoMoroso(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            ejbPredialCinco.planoMoroso(compania, new BigDecimal(deudaMaxima),
                            fechaCorte,
                            "true".equals(checkCedulasNulas),
                            SessionUtil.getCompaniaIngreso().getNombre());
            genInforme(formato);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
