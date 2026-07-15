/*-
 * CopiarConfEquivNiifControlador.java
 *
 * 1.0
 * 
 * 23/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.CopiarConfEquivNiifControladorEnum;
import com.sysman.contabilidad.enums.CopiarConfEquivNiifControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario Copiar
 * Configuracion equivalente NIIF en Access
 * "FRM_Copiarconfiguracionequivalenteniif", el cual es llamado desde
 * Contabilidad\Mantenimiento\Utilidades MGC\Copiar Configuracion
 * Equivalente NIIF
 * 
 * @version 1.0, 23/11/2016
 * @author amonroy
 * @modified jguerrero
 * @version 2. 17/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * @author asana
 * @version 3, 12/06/2017 se implementa enum en formulario.
 */
@ManagedBean
@ViewScoped

public class CopiarConfEquivNiifControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el modulo en el que se esta trabajando
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la compania equivalente niif
     */
    private String companianiif;
    /**
     * Atributo que almacena el anio fuente seleccionado en el
     * comboBox anoFuente
     */
    private String anioFuente;
    /**
     * Atributo que almacena el anio destino seleccionado en el
     * comboBox anoDestino
     */
    private String anioDestino;
    /**
     * Atreibuto en el que se define el titulo del archivo plano que
     * se genera *
     */
    private String tituloPlano;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de anoFuente
     */
    private List<Registro> listaAnofuente;
    /**
     * Listado de registros para el comboBox de anoDestino
     */
    private List<Registro> listaAnodestino;
    /**
     * Caracteres usados para la generacion del archivo plano
     */
    private final String lineaFormato;
    /**
     * Indica si se han presentado inconsistencias en el proceso de
     * copiar la configuracion equivalente niif
     */
    private boolean inconsistencias;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbContabilidadTres para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_CONTABILIDAD3
     */
    @EJB
    private EjbContabilidadTresRemote ejbContabilidadTres;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CopiarConfEquivNiifControlador
     */
    public CopiarConfEquivNiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        lineaFormato = "-------------------------------------------------------------\r\n";

        try {
            numFormulario = GeneralCodigoFormaEnum.COPIAR_CONF_EQUIV_NIIF_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anioFuente = String
                            .valueOf(Calendar.getInstance().get(Calendar.YEAR)
                                - 1);
            anioDestino = String.valueOf(
                            Calendar.getInstance().get(Calendar.YEAR));
            inconsistencias = false;
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
        try {
            companianiif = ejbSysmanUtil.consultarParametro(
                            compania, idioma.getString("TB_TB2200"), modulo,
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (SysmanFunciones.validarVariableVacio(companianiif)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2201"));
            return;
        }
        cargarListaAnofuente();
        cargarListaAnodestino();
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
    /**
     * 
     * Carga la lista listaAnofuente
     */
    public void cargarListaAnofuente() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnofuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarConfEquivNiifControladorUrlEnum.URL4953
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
     * Carga la lista listaAnodestino
     *
     */
    public void cargarListaAnodestino() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioFuente);

        try {
            listaAnodestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarConfEquivNiifControladorUrlEnum.URL5310
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
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     * Realiza la configuracion de NIIF en las cuentas del anio
     * destino, genera un archivo de texto plano con las cuentas que
     * no han sido definidas en el anio destino pero que poseen NIIF
     * en el anio origen
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // Verifica que exista plan contable para el anio de destino
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioDestino);

            List<Registro> l = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarConfEquivNiifControladorUrlEnum.URL6412
                                                                            .getValue())
                                            .getUrl(), param));

            if (l.isEmpty()) {
                String mensaje = idioma.getString("TB_TB2231");
                mensaje = mensaje.replace("s$anioDestino$s", anioDestino);
                JsfUtil.agregarMensajeInformativo(mensaje);
            }

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.ANO.getName(), anioFuente);

            Registro r = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarConfEquivNiifControladorUrlEnum.URL264
                                                                            .getValue())
                                            .getUrl(), params));

            int conNiif = Integer
                            .parseInt(r.getCampos().get("CUENTAS").toString());
            if (conNiif > 0) {

                ejbContabilidadTres.copiarConfiguracionEquivalenteNIIF(compania,
                                Integer.parseInt(anioFuente),
                                Integer.parseInt(anioDestino));

                generarPlano();
            }
            else {
                String msj = idioma.getString("TB_TB2234")
                                .replace("s$aniofuente$s", anioFuente);
                JsfUtil.agregarMensajeInformativo(msj);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Arma el contenido del archivo plano en el que se envian las
     * cuentas que no han sido configuradas en el anio de destino
     */
    public void generarPlano() {

        tituloPlano = idioma.getString("TB_TB2264");
        tituloPlano = tituloPlano.replace("s$anioDestino$s", anioDestino);
        try {

            Map<String, Object> parametrosPlano = new TreeMap<>();
            parametrosPlano.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosPlano.put(CopiarConfEquivNiifControladorEnum.PARAM0
                            .getValue(), anioFuente);
            parametrosPlano.put(CopiarConfEquivNiifControladorEnum.PARAM1
                            .getValue(), anioDestino);
            parametrosPlano.put(CopiarConfEquivNiifControladorEnum.PARAM2
                            .getValue(), companianiif);

            List<Registro> list = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CopiarConfEquivNiifControladorUrlEnum.URL9266
                                                                            .getValue())
                                            .getUrl(), parametrosPlano));

            if (!list.isEmpty()) {

                StringBuilder strPlano = new StringBuilder();
                strPlano.append(lineaFormato
                    + idioma.getString("TB_TB3078")
                    + lineaFormato);
                for (Registro registro : list) {
                    String strCompania = SysmanFunciones
                                    .nvl(registro.getCampos().get("COMPANIA"),
                                                    "")
                                    .toString();
                    String anio = SysmanFunciones
                                    .nvl(registro.getCampos().get("ANO"), "")
                                    .toString();
                    String codigo = SysmanFunciones
                                    .nvl(registro.getCampos().get("CODIGO"), "")
                                    .toString();
                    strPlano.append("| "
                        + SysmanFunciones.padr(strCompania, 18, " ")
                        + "| "
                        + SysmanFunciones.padr(anio, 18, " ")
                        + "| "
                        + SysmanFunciones.padr(codigo, 18, " ")
                        + "|\r\n"
                        + lineaFormato);
                }

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(strPlano.toString()),
                                tituloPlano);
                inconsistencias = true;
            }
            ejecutarMensajeArchivo();

        }
        catch (JRException | IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    public void ejecutarMensajeArchivo() {
        if (inconsistencias) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1638"));
        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1637"));
        }
        return;
    }

    // <METODOS_CAMBIAR>
    /**
     * Se actualiza el listado del anio destino al cambiar el anio
     * fuente
     */
    public void cambiarAnofuente() {
        anioDestino = null;
        cargarListaAnodestino();
    }

    public void cambiarAnodestino() {
        // <CODIGO_DESARROLLADO>
        verificarAnioEnNiif();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica si el anio destino seleccionado se encuentra
     * configurado en la compania Niif
     */
    private void verificarAnioEnNiif() {
        Registro existe = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), companianiif);
        parametros.put(CopiarConfEquivNiifControladorEnum.PARAM3
                        .getValue(), anioDestino);

        UrlBean urlano = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CopiarConfEquivNiifControladorUrlEnum.URL001
                                                        .getValue());
        try {
            existe = RegistroConverter.toRegistro(
                            requestManager.get(urlano.getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((existe != null)
            && "0".equals(existe.getCampos().get("NUMERO").toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3241"));
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioFuente
     * 
     * @return anioFuente
     */
    public String getAnioFuente() {
        return anioFuente;
    }

    /**
     * Asigna la variable anioFuente
     * 
     * @param anioFuente
     * Variable a asignar en anioFuente
     */
    public void setAnioFuente(String anioFuente) {
        this.anioFuente = anioFuente;
    }

    /**
     * Retorna la variable anioDestino
     * 
     * @return anioDestino
     */
    public String getAnioDestino() {
        return anioDestino;
    }

    /**
     * Asigna la variable anioDestino
     * 
     * @param anioDestino
     * Variable a asignar en anioDestino
     */
    public void setAnioDestino(String anioDestino) {
        this.anioDestino = anioDestino;
    }

    /**
     * Retorna la variable tituloPlano
     * 
     * @return tituloPlano
     */
    public String getTituloPlano() {
        return tituloPlano;
    }

    /**
     * Asigna la variable tituloPlano
     * 
     * @param tituloPlano
     * Variable a asignar
     */
    public void setTituloPlano(String tituloPlano) {
        this.tituloPlano = tituloPlano;
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
    /**
     * Retorna la lista listaAnofuente
     * 
     * @return listaAnofuente
     */
    public List<Registro> getListaAnofuente() {
        return listaAnofuente;
    }

    /**
     * Asigna la lista listaAnofuente
     * 
     * @param listaAnofuente
     * Variable a asignar en listaAnofuente
     */
    public void setListaAnofuente(List<Registro> listaAnofuente) {
        this.listaAnofuente = listaAnofuente;
    }

    /**
     * Retorna la lista listaAnodestino
     * 
     * @return listaAnodestino
     */
    public List<Registro> getListaAnodestino() {
        return listaAnodestino;
    }

    /**
     * Asigna la lista listaAnodestino
     * 
     * @param listaAnodestino
     * Variable a asignar en listaAnodestino
     */
    public void setListaAnodestino(List<Registro> listaAnodestino) {
        this.listaAnodestino = listaAnodestino;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
