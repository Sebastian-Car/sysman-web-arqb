/*-
 * FrmusuariosfacturadorecaudadoControlador.java
 *
 * 1.0
 *
 * 09/11/2016
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
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrmusuariosfacturadorecaudadoControladorEnum;
import com.sysman.serviciospublicos.enums.FrmusuariosfacturadorecaudadoControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario FrmusuariosfacturadorecaudadoControlador
 *
 * @version 1.0, 09/11/2016
 * @author cperez
 * 
 * @version 2, 01/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */

@ManagedBean
@ViewScoped
public class FrmusuariosfacturadorecaudadoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo del combo ciclo Inicial
     */
    private String cicloInicial;
    /**
     * Obtiene el ciclo del combo ciclo Final
     */
    private String cicloFinal;
    /*
     * variable constante para el nombre "NUMERO"
     */
    private String numero;

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
    /**
     * Necesario para obtener mandar la lista del ciclo Inicial
     */
    private List<Registro> listaCiclo;
    /**
     * Necesario para obtener mandar la lista del ciclo Final
     */
    private List<Registro> listaCiclo1;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmusuariosfacturadorecaudadoControlador
     */
    public FrmusuariosfacturadorecaudadoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMUSUARIOSFACTURADORECAUDADO_CONTROLADOR.getCodigo();
            numero = "NUMERO";
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaCiclo1();
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
    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmusuariosfacturadorecaudadoControladorUrlEnum.URL4728
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
     * Carga la lista listaCiclo1
     */
    public void cargarListaCiclo1() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(FrmusuariosfacturadorecaudadoControladorEnum.PARAM0.getValue(),cicloInicial);
        
        try {
            listaCiclo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmusuariosfacturadorecaudadoControladorUrlEnum.URL5146
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
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long cicloIni = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIni > cicloFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1798"));
        }
        else {
            genInforme(FORMATOS.PDF, "001228InfUsuariosFacturadoRecaudado");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long cicloIni = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIni > cicloFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1798"));
        }
        else {
            genInforme(FORMATOS.EXCEL, "001228InfUsuariosFacturadoRecaudado");
        }
        // </CODIGO_DESARROLLADO>
    }

    /*
     * Metodo para generar el informe ya sea de pdf o excel
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("cicloInicial", cicloInicial);
        reemplazar.put("cicloFinal", cicloFinal);
        reemplazar.put("compania", compania);
        Map<String, Object> parametros = new HashMap<>();
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
        parametros.put("PR_FORMS_FRMUSUARIOSFACTURADORECAUDADO_CICLO_INICIAL",
                        cicloInicial);
        parametros.put("PR_FORMS_FRMUSUARIOSFACTURADORECAUDADO_CICLO_FINAL",
                        cicloFinal);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo1
     * 
     * 
     */
    public void cambiarCiclo() {
        //<CODIGO_DESARROLLADO>
            cicloFinal=null;
            cargarListaCiclo1();
        //</CODIGO_DESARROLLADO>
    }
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
        cicloInicial = registroAux.getCampos().get(numero).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCiclo1
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = registroAux.getCampos().get(numero).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaCiclo1
     *
     * @return listaCiclo1
     */
    public List<Registro> getListaCiclo1() {
        return listaCiclo1;
    }

    /**
     * Asigna la lista listaCiclo1
     *
     * @param listaCiclo1
     * Variable a asignar en listaCiclo1
     */
    public void setListaCiclo1(List<Registro> listaCiclo1) {
        this.listaCiclo1 = listaCiclo1;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
