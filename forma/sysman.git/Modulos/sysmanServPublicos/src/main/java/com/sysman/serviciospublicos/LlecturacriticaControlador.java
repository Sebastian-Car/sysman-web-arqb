/*-
 * LlecturacriticaControlador.java
 *
 * 1.0
 * 
 * 05/10/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LlecturacriticaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * 
 *
 * @version 1.0, 05/10/2016
 * @author jguerrero
 * @modified jguerrero
 * @version 2. 07/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class LlecturacriticaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de
     * sesiÃ³n correspondiente
     */

    /**
     * Atributo en el que se almacena temporalmente el codigo de la
     * compañia que se encuentra logeada
     */

    private final String compania;
    private final String cicloCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo en el que se almancena temporalmente el ciclo luego de
     * seleccionarlo de el combo cilo.
     */
    private String ciclo;

    /**
     * El atributo limiteSuperior almancena temporalmente el dato
     * sumistrado en el formulario en el campo del % superior
     */

    private String limiteSuperior;

    /**
     * El atributo limiteInferior almancena de temporalmente el dato
     * sumistrado en el formulario en el campo del % inferior
     */
    private String limiteInferior;

    /**
     * Es atributo de la clase StreamedContent en el que ayuda con la
     * ayuda de descargar de archivos
     */

    private StreamedContent archivoDescarga;

    /**
     * El atributo nombrePeriodo almancena de temporalmente el
     * resultado de la funcion nombrePeriodo en el cual se encuentra
     * almacenada en el paquete de serviciospublicos.
     */
    private String nombrePeriodo;

    /**
     * El atributo titulo almancena temporalemte el ciclo y el
     * nombrePeriodo que es usado para la generacion de los informes.
     */
    private String titulo;

    /**
     * El atributo codigoInicial almancena de temporalmente el dato
     * correspondiente al codigoruta de un ciclo determinado
     */
    private String codigoInicial;

    /**
     * El atributo codigoFinal almancena de temporalmente el dato
     * correspondiente al codigoruta de un ciclo determinado
     */
    private String codigoFinal;

    /**
     * El atributo problemaAforo almancena de temporalmente el dato
     * correspondiente al check que se encuentra en el formulario
     * llecturaCritica
     */
    private boolean problemaAforo;
    /**
     * El atributo porcentaje almancena de temporalmente el dato
     * correspondiente al check que se encuentra en el formulario
     * llecturaCritica
     */
    private boolean porcentaje;

    /**
     * la Constante limiteSuperiorCons almacena una cadena de
     * carateres en la cual es usada para agregar los datos del campo
     * limeteSuperior al hashMap de reemplazarReportes para su
     * respectivo proceso
     * 
     */
    private final String limiteSuperiorCons;

    /**
     * la Constante limiteInferiorCons almacena una cadena de
     * carateres en la cual es usada para agregar los datos del campo
     * limeteInferior al hashMap de reemplazarReportes para su
     * respectivo proceso
     * 
     */
    private final String limiteInferiorCons;
    /**
     * la Constante tituloCons almacena una cadena de carateres al
     * hashMap de parametrosReportes para su respectivo proceso
     * 
     */
    private final String tituloCons;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * El atributo listaCiclo de la clase RegistroDataModel almancena
     * temporalmente los datos del combo del ciclo Estos datos son:
     * ciclo,codigoInicial y codigoFinal.
     */

    private RegistroDataModelImpl listaCiclo;
    private static final String NUMEROCONS = GeneralParameterEnum.NUMERO
                    .getName();

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LlecturacriticaControlador
     */
    public LlecturacriticaControlador() {
        super();
        limiteSuperiorCons = "limiteSuperior";
        limiteInferiorCons = "limiteInferior";
        tituloCons = "PR_TITULO";
        cicloCons = "ciclo";

        compania = SessionUtil.getCompania();

        numFormulario = GeneralCodigoFormaEnum.LLECTURACRITICA_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <INI_ADICIONAL>
        // </INI_ADICIONAL>

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
         * FR1132-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LlecturacriticaControladorUrlEnum.URL7331
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NUMEROCONS);
        // 214060
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * El metodo oprimirImprimir se encarga de validar que los campos:
     * % Superior y % Inferior del formulario llecturaCritica. Además,
     * invoca el metodo LogicaReportes en el cual exporta en formato
     * EXCEL
     */

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        if ((Integer.parseInt(limiteSuperior) < 0)
            && (Integer.parseInt(limiteInferior) < 0)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1731"));
            limiteInferior = null;
            limiteSuperior = null;
            return;

        }
        logicaReportes(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * El metodo oprimirImprimir se encarga de validar que los campos:
     * % Superior y % Inferior del formulario llecturaCritica. Además,
     * invoca el metodo LogicaReportes en el cual exporta en formato
     * PDF
     */

    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>

        if ((Integer.parseInt(limiteSuperior) < 0)
            && (Integer.parseInt(limiteInferior) < 0)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1731"));
            limiteInferior = null;
            limiteSuperior = null;
            return;

        }
        logicaReportes(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * El metodo seleccionarFilaCiclo se encarga de almancenar en los
     * atributos ciclo,nombrePeriodo,CodigoInicial,CodoFinal lo
     * seleccionado del combo ciclo del formulario llecturaCritica
     * 
     * @param event
     */

    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = validarCampo(registroAux, NUMEROCONS);
        nombrePeriodo = validarCampo(registroAux, "NOMBREPERIODO");

        codigoInicial = "'"
            + validarCampo(registroAux, "CODIGOINICIAL") + "'";
        codigoFinal = "'"
            + validarCampo(registroAux, "CODIGOFINAL") + "'";
        titulo = "Ciclo: " + ciclo + ". Periodo: "
            + nombrePeriodo;

    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(String limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public String getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(String limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public boolean isProblemaAforo() {
        return problemaAforo;
    }

    public void setProblemaAforo(boolean problemaAforo) {
        this.problemaAforo = problemaAforo;
    }

    public boolean isPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(boolean porcentaje) {
        this.porcentaje = porcentaje;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * El metodo GenInforme que recibe como parametros el formato en
     * que se desea generar el informe, el nombre del reporte a
     * generar, los parametros que son necesarios para la creacion de
     * cualquier informe.
     * 
     * @param formato
     * @param reporte
     * @param parametros
     * @param reemplazar
     */

    public void genInforme(ReportesBean.FORMATOS formato,
        String reporte, Map<String, Object> parametros,
        Map<String, Object> reemplazar) {
        try {
            archivoDescarga = null;

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo recibe como parametro el formato en el cual se
     * desea generar el informe. Además, dependiendo de los check del
     * formulario llecturaCritica genera 4 tipos de formularios
     * diferentes. los cuales son: 001128LCriticaConsumoporcaforo,
     * 001128LCriticaConsumoporcaforo, 001138LCriticaConsumoporc,
     * 001141LCriticaConsumo
     * 
     * @param formato
     */
    private void logicaReportes(ReportesBean.FORMATOS formato) {
        HashMap<String, Object> reemplazarReportes;
        Map<String, Object> parametrosReportes;
        archivoDescarga = null;

        if (problemaAforo) {
            if (porcentaje) {

                reemplazarReportes = new HashMap<>();
                reemplazarReportes.put(limiteSuperiorCons, limiteSuperior);
                reemplazarReportes.put(limiteInferiorCons, limiteInferior);
                reemplazarReportes.put(cicloCons, ciclo);
                parametrosReportes = new HashMap<>();
                parametrosReportes.put(tituloCons, titulo);
                genInforme(formato,
                                "001128LCriticaConsumoporcaforo",
                                parametrosReportes,
                                reemplazarReportes);
            }
            else {
                reemplazarReportes = new HashMap<>();
                reemplazarReportes.put("codigoInicial", codigoInicial);
                reemplazarReportes.put("codigoFinal", codigoFinal);
                reemplazarReportes.put(cicloCons, ciclo);

                parametrosReportes = new HashMap<>();
                parametrosReportes.put(tituloCons, titulo);
                genInforme(formato,
                                "001133LCriticaConsumoaforo",
                                parametrosReportes,
                                reemplazarReportes);
            }
        }
        else {
            if (porcentaje) {
                reemplazarReportes = new HashMap<>();
                reemplazarReportes.put(limiteSuperiorCons, limiteSuperior);
                reemplazarReportes.put(limiteInferiorCons, limiteInferior);
                reemplazarReportes.put(cicloCons, ciclo);

                parametrosReportes = new HashMap<>();
                parametrosReportes.put(tituloCons, titulo);
                genInforme(formato,
                                "001138LCriticaConsumoporc", parametrosReportes,
                                reemplazarReportes);
            }
            else {

                reemplazarReportes = new HashMap<>();
                reemplazarReportes.put(limiteSuperiorCons, limiteSuperior);
                reemplazarReportes.put(limiteInferiorCons, limiteInferior);
                reemplazarReportes.put(cicloCons, ciclo);

                parametrosReportes = new HashMap<>();
                parametrosReportes.put(tituloCons, titulo);
                genInforme(formato,
                                "001141LCriticaConsumo", parametrosReportes,
                                reemplazarReportes);

            }
        }
    }

    private String validarCampo(Registro registro, String campo) {
        return SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)
            ? "" : registro.getCampos().get(campo).toString();

    }

}
