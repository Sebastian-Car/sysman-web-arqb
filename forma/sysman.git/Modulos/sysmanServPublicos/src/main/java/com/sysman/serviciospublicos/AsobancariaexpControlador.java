/*-
 * AsobancariaexpControlador.java
 *
 * 1.0
 *
 * 26/12/2016
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.impl.EjbServiciosPublicosCuatro;
import com.sysman.serviciospublicos.enums.AsobancariaexpControladorUrlEnum;

import java.io.IOException;
import java.util.Date;
import java.util.List;
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
 * Controlador de la forma Asobancariaexp asociado al formulario
 * "Exportar a Asobancaria".
 *
 * @version 1.0, 26/12/2016
 * @author yrojas
 * @modified jguerrero
 * @version 2. 15/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class AsobancariaexpControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el valor CODIGORUTA
     */
    private final String codigoRutaCons;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo asociado al check del formulario y que es usado para
     * determinar la forma en la que se genera el informe.
     */
    private boolean planoATH;

    /**
     * Atributo asociado al parametro
     * "MANEJA PLANOS ATH SIN NUMERO DE FACTURA" y que es usado para
     * determinar si son o no visibles algunos componentes.
     */
    private boolean manejaPlanosATH;

    /**
     * Atributo asociado al valor del ciclo que se va a manejar en la
     * consulta del combo.
     */
    private String ciclo;

    /**
     * Atributo asociado al codigo inicial del formulario y que es
     * usado para la generacion del reporte correspondiente.
     */
    private String codigoInicial;

    /**
     * Atributo asociado al codigo final del formulario y que es usado
     * para la generacion del reporte correspondiente.
     */
    private String codigoFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que contiene la informacion de los detalles del combo
     * ciclo.
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que contiene la informacion de los detalles del combo
     * codigo inicial.
     */
    private RegistroDataModelImpl listaCodInicial;

    /**
     * Lista que contiene la informacion de los detalles del combo
     * codigo final.
     */
    private RegistroDataModelImpl listaCodFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbServiciosPublicosCuatro ejbServPubCua;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de AsobancariaexpControlador
     */
    public AsobancariaexpControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoRutaCons = GeneralParameterEnum.CODIGORUTA.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.ASOBANCARIAEXP_CONTROLADOR
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
     * clase del Bean ha sido creado. En este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios.
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaCodInicial();
        cargarListaCodFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado en el metodo inicializar. Se ejecutan
     * las acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            String parFacturacionPlanoATH = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA PLANOS ATH SIN NUMERO DE FACTURA",
                            SessionUtil.getModulo(),
                            new Date(), true);

            manejaPlanosATH = ("SI").equals(parFacturacionPlanoATH);
            if (parFacturacionPlanoATH == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2737"));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AsobancariaexpControladorUrlEnum.URL6652
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCodInicial
     *
     */
    public void cargarListaCodInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsobancariaexpControladorUrlEnum.URL6653
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    /**
     * Carga la lista listaCodFinal
     *
     */
    public void cargarListaCodFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AsobancariaexpControladorUrlEnum.URL6654
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put("CODIGORUTAINI", codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo que genera el archivo plano dependiendo de si
     * manejaPlanosATH es verdadero y de si el check esta o no
     * seleccionado.
     */
    private void generarReporte() {
        String cadena;

        String nombreArchivo = "Asobancaria.dat";

        try {
            if (manejaPlanosATH) {
                if (planoATH) {

                    cadena = ejbServPubCua.enviarPlanosAsobancaria(compania,
                                    Integer.parseInt(ciclo), codigoInicial,
                                    codigoFinal,
                                    SessionUtil.getUser().getCodigo(), true);

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    JsfUtil.serializarPlano(cadena),
                                    nombreArchivo);

                }
                else {

                    cadena = ejbServPubCua.enviarPlanosAsobancaria(compania,
                                    Integer.parseInt(ciclo), codigoInicial,
                                    codigoFinal,
                                    SessionUtil.getUser().getCodigo(), false);

                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    JsfUtil.serializarPlano(cadena),
                                    nombreArchivo);

                }

            }
            else {

                cadena = ejbServPubCua.enviarPlanosAsobancaria(compania,
                                Integer.parseInt(ciclo), codigoInicial,
                                codigoFinal,
                                SessionUtil.getUser().getCodigo(), false);

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(cadena), nombreArchivo);

            }
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar en la vista.
     *
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar en la vista y que
     * llama el metodo que genera el archivo plano.
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el Ciclo.
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";

        cargarListaCodInicial();
        cargarListaCodFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodInicial.
     *
     * @param event
     * Objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCodInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRutaCons).toString();
        codigoFinal = null;
        cargarListaCodFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodFinal.
     *
     * @param event
     * Objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRutaCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Metodo que retorna la variable booleana planoATH
     *
     * @return Variable de planoATH
     */
    public boolean isPlanoATH() {
        return planoATH;
    }

    /**
     * Metodo que asigna la variable booleana planoATH.
     *
     * @param planoATH
     * Variable a asignar en planoATH
     */
    public void setPlanoATH(boolean planoATH) {
        this.planoATH = planoATH;
    }

    /**
     * Retorna la variable ciclo.
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo.
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoInicial.
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial.
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal.
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal.
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Metodo que retorna la variable booleana manejaPlanosATH.
     *
     * @return Variable de manejaPlanosATH
     */
    public boolean isManejaPlanosATH() {
        return manejaPlanosATH;
    }

    /**
     * Metodo que asigna la variable booleana manejaPlanosATH.
     *
     * @param manejaPlanosATH
     * Variable a asignar en manejaPlanosATH
     */
    public void setManejaPlanosATH(boolean manejaPlanosATH) {
        this.manejaPlanosATH = manejaPlanosATH;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo.
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo.
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodInicial.
     *
     * @return listaCodInicial
     */
    public RegistroDataModelImpl getListaCodInicial() {
        return listaCodInicial;
    }

    /**
     * Asigna la lista listaCodInicial.
     *
     * @param listaCodInicial
     * Variable a asignar en listaCodInicial
     */
    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial) {
        this.listaCodInicial = listaCodInicial;
    }

    /**
     * Retorna la lista listaCodFinal.
     *
     * @return listaCodFinal
     */
    public RegistroDataModelImpl getListaCodFinal() {
        return listaCodFinal;
    }

    /**
     * Asigna la lista listaCodFinal
     *
     * @param listaCodFinal
     * Variable a asignar en listaCodFinal
     */
    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal) {
        this.listaCodFinal = listaCodFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
