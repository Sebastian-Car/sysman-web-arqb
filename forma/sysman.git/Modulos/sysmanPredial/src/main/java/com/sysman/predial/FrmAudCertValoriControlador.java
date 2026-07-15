/*-
 * FrmAudCertValoriControlador.java
 *
 * 1.0
 *
 * 14/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

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
import com.sysman.predial.enums.FrmAudCertValoriControladorEnum;
import com.sysman.predial.enums.FrmAudCertValoriControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
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
 * @version 1.0, 14/02/2017
 * @author jsforero
 * @version 2, 29/06/2017 jrodriguezr Se refactoriza el c�digo SQL de
 * las listas para utilizar DSS. Tambi�n los llamados a funciones,
 * procedimientos y m�todos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class FrmAudCertValoriControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String codigo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el destino inicial
     */
    private String destinoInicial;
    /**
     * Variable que almacena el destino final
     */
    private String destinoFinal;
    /**
     * Variable que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la fecha final
     */
    private Date fechaFinal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * registrodatamodel que almacena la lista de destino inicial
     */
    private RegistroDataModelImpl listadestinoini;
    /**
     * registrodatamodel que almacena la lista de destino final
     */
    private RegistroDataModelImpl listadestinofin;

    /**
     * Archivo que se genera al oprimir descargar
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmAudCertValoriControlador
     */
    public FrmAudCertValoriControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_AUD_CERT_VALORI_CONTROLADOR
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
        cargarListadestinoini();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

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
     * Carga la lista listadestinoini
     *
     */
    public void cargarListadestinoini() {
        // 393001
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAudCertValoriControladorUrlEnum.URL4892
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listadestinoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /**
     *
     * Carga la lista listadestinofin
     *
     */
    public void cargarListadestinofin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAudCertValoriControladorUrlEnum.URL5528
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAudCertValoriControladorEnum.DESTINOINICIAL.getValue(),
                        destinoInicial);
        listadestinofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton cmdgenerar en la vista
     *
     *
     */

    /**
     * Metodo que llama oprimircmdgenerar y le pasa el formato excel
     */
    public void oprimircmdgenerarExcel() {
        oprimircmdgenerar(ReportesBean.FORMATOS.EXCEL97);
    }

    /**
     * Metodo que llama oprimircmdgenerar y le pasa el formato pdf
     */
    public void oprimircmdgenerarPdf() {
        oprimircmdgenerar(ReportesBean.FORMATOS.PDF);
    }

    /**
     * Metodo que genera el archivo dependiendo el formato pasado.
     *
     * @param formato:
     * formato del archivo a generar
     */
    public void oprimircmdgenerar(ReportesBean.FORMATOS formato) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            /*
             * Determina si deben ordenar los recaudos por fecha y
             * hora.
             */
            String reporte = "001410inflistadovalorizacion";

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("destinoInicial", destinoInicial);
            reemplazar.put("destinoFinal", destinoFinal);

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            parametros.put("PR_FORMS_FRM_AUDCERTVALORI_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));

            parametros.put("PR_FORMS_FRM_AUDCERTVALORI_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadestinoini
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladestinoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        destinoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        destinoFinal = "";
        cargarListadestinofin();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadestinofin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladestinofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        destinoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable destinoInicial
     *
     * @return destinoInicial
     */
    public String getDestinoInicial() {
        return destinoInicial;
    }

    /**
     * Asigna la variable destinoInicial
     *
     * @param destinoInicial
     * Variable a asignar en destinoInicial
     */
    public void setDestinoInicial(String destinoInicial) {
        this.destinoInicial = destinoInicial;
    }

    /**
     * Retorna la variable destinoFinal
     *
     * @return destinoFinal
     */
    public String getDestinoFinal() {
        return destinoFinal;
    }

    /**
     * Asigna la variable destinoFinal
     *
     * @param destinoFinal
     * Variable a asignar en destinoFinal
     */
    public void setDestinoFinal(String destinoFinal) {
        this.destinoFinal = destinoFinal;
    }
    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listadestinoini
     *
     * @return listadestinoini
     */
    public RegistroDataModelImpl getListadestinoini() {
        return listadestinoini;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Asigna la lista listadestinoini
     *
     * @param listadestinoini
     * Variable a asignar en listadestinoini
     */
    public void setListadestinoini(RegistroDataModelImpl listadestinoini) {
        this.listadestinoini = listadestinoini;
    }

    /**
     * Retorna la lista listadestinofin
     *
     * @return listadestinofin
     */
    public RegistroDataModelImpl getListadestinofin() {
        return listadestinofin;
    }

    /**
     * Asigna la lista listadestinofin
     *
     * @param listadestinofin
     * Variable a asignar en listadestinofin
     */
    public void setListadestinofin(RegistroDataModelImpl listadestinofin) {
        this.listadestinofin = listadestinofin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
