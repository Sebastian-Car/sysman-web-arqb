/*-
 * TodosDescuentosNominasControlador.java
 *
 * 1.0
 * 
 * 09/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Permite mostrar valor de los descuentos
 *
 * @version 1.0, 09/01/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class DescuentosNominasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String anioNomina;

    private String mesNomina;

    private String periodoNomina;

    private String procesoNomina;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TodosDescuentosNominasControlador
     */
    public DescuentosNominasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DESCUENTOSNOMINA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            anioNomina = (String) SessionUtil.getSessionVar("anioNomina");
            mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
            periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
            procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
            archivoDescarga = null;
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

    public void oprimira() {
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();

        try {
            // <CODIGO_DESARROLLADO>
            String condicionPivot = ejbNominaSiete
                            .getPrepararPivotTodosDescuentos(compania,
                                            Integer.parseInt(anioNomina),
                                            Integer.parseInt(mesNomina),
                                            Integer.parseInt(procesoNomina),
                                            periodoNomina);

            if (condicionPivot == null) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2628"));
                return;
            }
            reemplazar.put("compania", compania);
            reemplazar.put("anio", anioNomina);
            reemplazar.put("mes", mesNomina);
            reemplazar.put("condicionpivot", condicionPivot);

            if (SysmanFunciones.esBdSqlServer()) {

                StringBuilder condicionPivot2 = new StringBuilder();
                Pattern p = Pattern
                                .compile("(\\[\\d+[\\.]{1}\\d+[\\_]\\d+\\])");

                Matcher m = p.matcher(condicionPivot);

                while (m.find()) {

                    condicionPivot2.append(m.group(1) + ",");

                }
                condicionPivot2.delete(condicionPivot2.length() - 1,
                                condicionPivot2.length());
                reemplazar.put("condicionpivot2", condicionPivot2);
            }

            String sql = Reporteador.resuelveConsulta("800128TodosDescuentos",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97, "Descuentos");
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SysmanException | SQLException |

                        DRException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
