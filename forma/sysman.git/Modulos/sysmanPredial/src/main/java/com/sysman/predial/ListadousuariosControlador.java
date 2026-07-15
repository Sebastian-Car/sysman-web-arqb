package com.sysman.predial;

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
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.ListadousuariosControladorEnum;
import com.sysman.predial.enums.ListadousuariosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 31/05/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambia el llamado del codigo del
 * formulario y actualizacion de ConectorPool
 * 
 * @modifier amonroy
 * @version 3, 07/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class ListadousuariosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "CODIGO"
     */
    private final String codigo;
    /**
     * Constante que almacena la cadena "PREDIAL_LISGRAL"
     */
    private final String predialLis;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean soloPredios;
    private boolean nombres;
    private boolean nombreVisible;
    private boolean palabraVisible;
    private boolean clave;
    private String ordenado;
    private String codigoInicial;
    private String codigoFinal;
    private String direccionInicial;
    private String direccionFinal;
    private Integer avaluoInicial;
    private Integer avaluoFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String palabraClave;
    private String palabra;
    private String ordenamiento;
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbPredialOchoRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL_COM8
     */
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;
    /**
     * Atributo que almacena el valor del parametro
     * "FORMATO LISTADO GENERAL DE PREDIOS"
     */
    private String parametro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ListadousuariosControlador
     */
    public ListadousuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigo = "CODIGO";
        predialLis = "PREDIAL_LISGRAL";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADOUSUARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ListadousuariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        nombreVisible = nombres = true;
        ordenado = "1";
        indicador = true;
        nombreInicial = "Todos";
        nombreFinal = "Todos";
        direccionInicial = "Todas";
        direccionFinal = "Todas";
        avaluoInicial = 0;
        avaluoFinal = 999999999;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadousuariosControladorUrlEnum.URL5251
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadousuariosControladorUrlEnum.URL6370
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(ListadousuariosControladorEnum.MICODIGO.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto MensajeParametro
     * en la vista
     *
     * Evalua si el valor del parametro esta nulo y envia un mensaje
     * al usuario
     *
     */
    public void ejecutarMensajeParametro() {
        // <CODIGO_DESARROLLADO>
        if (parametro == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB507"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo llamado en generarInforme
     *
     * @return visible
     */
    public int indica() {
        int visible;
        if (indicador) {
            visible = 1;

        }
        else {
            visible = 0;

        }
        return visible;
    }

    /**
     * Metodo llamao en generarInforme
     *
     * @param parametro
     * @return parReporte
     */
    public String parReporte(String parametro) {
        String parReporte;
        if ((parametro == null) || !parametro.equals(predialLis)) {
            parReporte = "000852LISTADOGENERAL";
        }
        else {
            parReporte = "000850PREDIALLISGRAL";
        }
        return parReporte;
    }

    /**
     * Si la cadena a evaluar no es vacia, agrega las comillas para
     * enviar en la funcion
     * 
     * @param cadenaEvaluar
     * cadena de texto a evaluar
     * @return nulo o la cadena entrecomillada
     */
    private String evaluarNulo(String cadenaEvaluar) {
        return cadenaEvaluar != null ? "'" + cadenaEvaluar + "'"
            : cadenaEvaluar;
    }

    /**
     * Realiza los reemplazos y envio de parametros para la generacion
     * de informes
     * 
     * @param formato
     * Formato en el que se desea generar el reporte
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            String condicion = ejbPredialOcho.prepararListadoGeneral(
                            compania,
                            clave,
                            evaluarNulo(palabraClave),
                            nombres,
                            evaluarNulo(nombreInicial),
                            evaluarNulo(nombreFinal),
                            evaluarNulo(direccionInicial),
                            evaluarNulo(direccionFinal),
                            indicador,
                            soloPredios,
                            Integer.parseInt(ordenado));

            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO LISTADO GENERAL DE PREDIOS",
                            modulo,
                            new Date(),
                            true);

            ejecutarMensajeParametro();

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("condicion", condicion);
            reemplazar.put("avaluoInicial", avaluoInicial);
            reemplazar.put("avaluoFinal", avaluoFinal);
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");

            Map<String, Object> parametros = new HashMap<>();
            String prCodigos = idioma.getString("TB_TB3296");
            prCodigos = prCodigos.replace("s$codigoInicial$s", codigoInicial);
            prCodigos = prCodigos.replace("s$codigoFinal$s", codigoFinal);
            parametros.put("PR_CODIGOS", prCodigos);
            // Validacion para visualizar las columnas INDCAR e INDEX
            // en el informe 000852LISTADOGENERAL
            parametros.put("PR_VISIBLE", indicador ? 1 : 0);
            parametros.put("PR_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(parReporte(parametro),
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte(parametro),
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarEntrenom() {
        nombreVisible = nombres;
        palabraVisible = !nombres;

        if (nombres) {
            clave = false;
            nombreVisible = true;
        }
        else {
            clave = true;
        }

    }

    public void cambiarPclave() {
        palabraVisible = clave;
        nombreVisible = !clave;
        if (clave) {
            nombres = false;
            palabraVisible = true;
        }
        else {
            nombres = true;
        }
    }

    public void cambiarindica() {
        if (indicador) {
            soloPredios = false;
        }

    }

    public void cambiarindex() {
        if (soloPredios) {
            indicador = false;
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean getSoloPredios() {
        return soloPredios;
    }

    public void setSoloPredios(boolean soloPredios) {
        this.soloPredios = soloPredios;
    }

    public boolean getNombres() {
        return nombres;
    }

    public void setNombres(boolean nombres) {
        this.nombres = nombres;
    }

    public boolean isNombreVisible() {
        return nombreVisible;
    }

    public void setNombreVisible(boolean nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public boolean getClave() {
        return clave;
    }

    public void setClave(boolean clave) {
        this.clave = clave;
    }

    public boolean isPalabraVisible() {
        return palabraVisible;
    }

    public void setPalabraVisible(boolean palabraVisible) {
        this.palabraVisible = palabraVisible;
    }

    public String getOrdenado() {
        return ordenado;
    }

    public void setOrdenado(String ordenado) {
        this.ordenado = ordenado;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getDireccionInicial() {
        return direccionInicial;
    }

    public void setDireccionInicial(String direccionInicial) {
        this.direccionInicial = direccionInicial;
    }

    public String getDireccionFinal() {
        return direccionFinal;
    }

    public void setDireccionFinal(String direccionFinal) {
        this.direccionFinal = direccionFinal;
    }

    public Integer getAvaluoInicial() {
        return avaluoInicial;
    }

    public void setAvaluoInicial(Integer avaluoInicial) {
        this.avaluoInicial = avaluoInicial;
    }

    public Integer getAvaluoFinal() {
        return avaluoFinal;
    }

    public void setAvaluoFinal(Integer avaluoFinal) {
        this.avaluoFinal = avaluoFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getPalabraClave() {
        return palabraClave;
    }

    public void setPalabraClave(String palabraClave) {
        this.palabraClave = palabraClave;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getOrdenamiento() {
        return ordenamiento;
    }

    public void setOrdenamiento(String ordenamiento) {
        this.ordenamiento = ordenamiento;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
