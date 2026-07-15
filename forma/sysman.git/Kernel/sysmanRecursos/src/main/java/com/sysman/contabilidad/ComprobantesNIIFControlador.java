package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadDosGeneralRemote;
import com.sysman.contabilidad.enums.ComprobantesNIIFControladorEnum;
import com.sysman.contabilidad.enums.ComprobantesNIIFControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author otorres
 * @version 1, 12/04/2016
 *
 * @author acaceres
 * @version 2, 25/07/2016
 *
 * @author lcortes
 * @version 3, 29,30/12/2016 16/17/25/01/2017
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 4, 05/04/2017
 * 
 * @author asana
 * @version 5, 12/06/2017 Redireccion de formulario.
 */
@ManagedBean
@ViewScoped

public class ComprobantesNIIFControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;

    /**
     * Atributo que almacenara el numero del modulo con el que se esta
     * trabajando
     */
    private final String modulo;

    /**
     * Constante que almacenara el valor de
     * GeneralParameterEnum.CODIGO.getName()
     */
    private final String codigoC;

    /**
     * Atributo que almacenara el tipo inicial del comprobante
     * seleccionado
     */
    private String companiaNiif;

    /**
     * Atributo que almacenara el tipo inicial del comprobante
     * seleccionado
     */
    private String tipoInicial;

    /**
     * Atributo que almacenara el tipo final del comprobante
     * seleccionado
     */
    private String tipoFinal;

    /**
     * Atributo que almacenara el anio con el que se esta trabajando
     */
    private String anio;

    /**
     * Atributo que almacenara el nombre del tipo inicial de
     * comprobante seleccioando
     */
    private String nombreTipoIni;

    /**
     * Atributo que almacenara el nonmbre del tipo final de
     * comprobante seleccionado
     */
    private String nombreTipoFin;

    /**
     * Atributo que almacenara el mes inicial seleccionado por el
     * usuario
     */
    private int mesInicial;

    /**
     * Atributo que almacenara el mes final seleccionado por el
     * usuario
     */
    private int mesFinal;

    /**
     * Lista que almacenara los anios a seleccionar
     */
    private List<Registro> listaAno;

    /**
     * Lista que almacenara los tipos de comprobante a seleccionar
     * como inicial
     */
    private RegistroDataModelImpl listaTipoInicial;

    /**
     * Lista que almacenara los tipos de comprobante a seleccionar
     * como final
     */
    private RegistroDataModelImpl listaTipoFinal;

    private StreamedContent archivoDescarga;

    private boolean verBotones;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadDosGeneralRemote ejbContabilidadDosGeneral;

    /**
     * Creates a new instance of ComprobantesNIIFControlador
     */
    public ComprobantesNIIFControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoC = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTES_NIIFCONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ComprobantesNIIFControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        try {
            companiaNiif = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB3292"),
                            SessionUtil.getModulo(), new Date(), true);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        verBotones = false;
        if (("104160301").equals(SessionUtil.getMenuActual())) {
            verBotones = true;
        }

        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mesInicial = Integer.parseInt(
                        Integer.toString(SysmanFunciones.mes(new Date())));
        mesFinal = Integer.parseInt(
                        Integer.toString(SysmanFunciones.mes(new Date())));
        cargarListaAno();
        cargarListaTipoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo usado para cargar la lista de los anios a seleccionar
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantesNIIFControladorUrlEnum.URL5706
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo usado para cargar la lista de los tipos de comprobante a
     * seleccionar como tipo inicial
     */
    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantesNIIFControladorUrlEnum.URL6210
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);

    }

    /**
     * Metodo usado para cargar la lista de los tipos de comprobante a
     * seleccionar como tipo final
     */
    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantesNIIFControladorUrlEnum.URL7119
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ComprobantesNIIFControladorEnum.PARAM0.getValue(),
                        String.valueOf(tipoInicial));

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    /**
     * Metodo que se ejecuta al oprimir el botón Aceptar
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (mesInicial <= mesFinal) {
            archivoDescarga = null;
            aceptarejecutarNiifLotes();
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB695"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CopiaCompania en la vista
     *
     *
     */
    public void oprimirCopiaCompania() {
        // <CODIGO_DESARROLLADO>
        try {
            String respuesta = ejbContabilidadDosGeneral
                            .crearCompaniaNiifLotes(compania, companiaNiif);
            if (Integer.valueOf(respuesta) >= 0) {
                String mensaje = idioma.getString("TB_TB2719")
                                .replace("s$registros$s", respuesta);
                JsfUtil.agregarMensajeInformativo(mensaje);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ConfComprobante en la
     * vista
     *
     *
     */
    public void oprimirConfComprobante() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.TIPOCOMPROBANTECONTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.getRuta();

        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarejecutarNiifLotes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se selecciona un mes inicial
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = 0;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al dar aceptar en el dialogo del boton
     * Aceptar
     */
    public void aceptarejecutarNiifLotes() {
        // <CODIGO_DESARROLLADO>

        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "ENTIDAD APLICA NIIF",
                            SessionUtil.getModulo(), new Date(), true))) {
                // <CODIGO_DESARROLLADO>

                String cadena = ejbContabilidadDosGeneral
                                .contabilizarComprobantesContablesNiif(
                                                compania, mesInicial, mesFinal,
                                                tipoInicial,
                                                tipoFinal,
                                                Integer.parseInt(anio),
                                                SessionUtil.getUser()
                                                                .getCodigo());

                archivoDescarga = null;
                String mensaje = "";
                String[] resultados = cadena.split(",");
                switch (resultados[0]) {
                case "-1":
                    mensaje = idioma.getString("TB_TB2751")
                        + resultados[1];
                    break;
                case "-2":
                    mensaje = idioma.getString("TB_TB2752").replace(
                                    "s$compNiif$s",
                                    resultados[1]);
                    mensaje = mensaje.replace("s$anio$s", anio);
                    break;
                case "-3":
                    mensaje = idioma.getString("TB_TB2753");
                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    JsfUtil.serializarPlano(resultados[1]),
                                    "Comprobantes sin detalle.txt");
                    break;
                default:
                    break;
                }

                if ("OK".equals(resultados[0])) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
                }
                else if ("".equals(mensaje)
                    && (Integer.parseInt(resultados[0]) > 0)) {
                    mensaje = idioma.getString("TB_TB2754").replace(
                                    "s$inconsistencias$s", resultados[0]);
                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    JsfUtil.serializarPlano(
                                                    resultados[1]),
                                    "Cuentas NIIF configuradas que no existen en plan contable.txt");
                }

                JsfUtil.agregarMensajeInformativo(mensaje);
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB696"));
            }
        }
        catch (SystemException | JRException | IOException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            Logger.getLogger(ComprobantesNIIFControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo usado para generar en informe de los comprobantes con
     * cuentas NIIF
     *
     * @param formato
     * Pdf, Excel
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put("anio", anio);
        Reporteador.resuelveConsulta("000628CUENTASSINNIIIF",
                        Integer.parseInt(modulo), reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed("000628CUENTASSINNIIIF",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que se ejecuta cuando se selecciona un tipo Inicial
     *
     * @param event
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(codigoC) == null ? ""
            : registroAux.getCampos().get(codigoC).toString();
        nombreTipoIni = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
        nombreTipoFin = "";
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    /**
     * Metodo que se ejecuta cuando se selecciona un tipo final
     *
     * @param event
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(codigoC) == null ? ""
            : registroAux.getCampos().get(codigoC).toString();
        nombreTipoFin = registroAux.getCampos().get("NOMBRE") == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();
    }

    /**
     * Retorna la variable companiaNiif
     *
     * @return companiaNiif
     */
    public String getCompaniaNiif() {
        return companiaNiif;
    }

    /**
     * Asigna la variable companiaNiif
     *
     * @param companiaNiif
     * Variable a asignar en companiaNiif
     */
    public void setCompaniaNiif(String companiaNiif) {
        this.companiaNiif = companiaNiif;
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVerBotones() {
        return verBotones;
    }

    public void setVerBotones(boolean verBotones) {
        this.verBotones = verBotones;
    }

    public String getNombreTipoIni() {
        return nombreTipoIni;
    }

    public void setNombreTipoIni(String nombreTipoIni) {
        this.nombreTipoIni = nombreTipoIni;
    }

    public String getNombreTipoFin() {
        return nombreTipoFin;
    }

    public void setNombreTipoFin(String nombreTipoFin) {
        this.nombreTipoFin = nombreTipoFin;
    }

}
