package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.PedirTerceroControladorEnum;
import com.sysman.presupuesto.enums.PedirTerceroControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author dmaldonado
 * @version 1, 29/06/2016
 * @modifed jsforero
 * @version 2. 27/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @author asana
 * @version 3, 13/06/2017 Se implementa enum en formulario y se ajusta
 * Conexi�n
 */
@ManagedBean
@ViewScoped
public class PedirTerceroControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String tipo;
    private String tercero;
    private String nombreTipo;
    private String nombreTercero;
    private StreamedContent archivoDescarga;
    private String sucursal;
    private boolean cargarTipo;
    private Map<String, Object> parametrosEntrada;
    private String anoComprobante;
    private String tipoComprobante;
    private String numeroComprobante;
    private String mensajeError;
    private String formato;
    private BigInteger consecutivo;
    private boolean registroAutomatico;
    private String objeto;
    private RegistroDataModelImpl listaTipo;
    private RegistroDataModelImpl listaTercero;
    private boolean inactivoBoton;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPresupuestoTresRemote ejbPresupuestoTres;

    /**
     * Creates a new instance of PedirTerceroControlador
     */
    public PedirTerceroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.PEDIR_TERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PedirTerceroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarFlash();
        cargarListaTipo();
        cargarListaTercero();
        abrirFormulario();
        inactivoBoton = false;
    }

    public void cargarFlash() {
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {

            anoComprobante = (String) parametrosEntrada.get("ano");
            tipoComprobante = (String) parametrosEntrada.get("tipoComprobante");
            numeroComprobante = parametrosEntrada
                            .get("numeroComprobante").toString();
            registroAutomatico = "SI".equals(parametrosEntrada
                            .get("registroAutomatico"));
        }
        else {
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarTipo = false; // --------------------------------------------------------------Cambiar
                            // por false
        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "VARIOS TIPOS DE REGISTRO EN GENERACION AUTOMATICA",
                            modulo, new Date(), true))) {
                cargarTipo = true;
            }
        }
        catch (SystemException e) {

            Logger.getLogger(PedirTerceroControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirTerceroControladorUrlEnum.URL7328
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.CLASE.name(), "'RES'");

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PedirTerceroControladorUrlEnum.URL8024
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirAceptar() {
        mensajeError = null;
        archivoDescarga = null;
        ejecutaractualizaMensaje();
        generaInforme(FORMATOS.PDF, formato, consecutivo);
        inactivoBoton = true;

    }

    private void generarRegistro() {

        if (registroAutomatico) {
            mensajeError = idioma.getString("TB_TB901");
            registroAutomatico = true;
            return;
        }
        try {
            // Se verifica que no este afectada, ni modificada esta
            // disponibilidad
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(GeneralParameterEnum.ANO.name(), anoComprobante);
            param.put(PedirTerceroControladorEnum.TIPO_CPTE.getValue(),
                            tipoComprobante);
            param.put(GeneralParameterEnum.COMPROBANTE.name(),
                            numeroComprobante);
            UrlBean urlRs = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PedirTerceroControladorUrlEnum.URL10748
                                                            .getValue());

            Registro rs;

            rs = RegistroConverter.toRegistro(
                            requestManager.get(urlRs.getUrl(), param));
            if (Integer.valueOf(rs.getCampos()
                            .get(PedirTerceroControladorEnum.CONTEO.getValue())
                            .toString()) > 0) {
                mensajeError = idioma.getString("TB_TB898");
                registroAutomatico = false;
                return;
            }

            Map<String, Object> paramrsAux = new TreeMap<>();
            paramrsAux.put(GeneralParameterEnum.COMPANIA.name(), compania);
            UrlBean urlRsAux;
            if (cargarTipo) {
                paramrsAux.put(GeneralParameterEnum.CODIGO.name(), tipo);
            }
            else {
                paramrsAux.put(GeneralParameterEnum.CODIGO.name(), "RES");
            }
            urlRsAux = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PedirTerceroControladorUrlEnum.URL21232
                                                            .getValue());

            Registro rsAux = RegistroConverter.toRegistro(
                            requestManager.get(urlRsAux.getUrl(), paramrsAux));

            if (rsAux != null) {
                tipo = rsAux.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString();
                formato = SysmanFunciones
                                .nvl(rsAux.getCampos().get(
                                                "FORMATO"), "000958CDP")
                                .toString();
            }
            else {
                mensajeError = idioma.getString("TB_TB899");
                registroAutomatico = false;
                return;
            }

            UrlBean urlRs2 = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PedirTerceroControladorUrlEnum.URL25842
                                                            .getValue());
            param.put(GeneralParameterEnum.NUMERO.getName(), param
                            .get(GeneralParameterEnum.COMPROBANTE.getName()));
            param.remove(GeneralParameterEnum.COMPROBANTE.getName());
            param.put(PedirTerceroControladorEnum.STRTIPOCOMPROBANTE.getValue(),
                            param.get(PedirTerceroControladorEnum.TIPO_CPTE
                                            .getValue()));
            param.remove(PedirTerceroControladorEnum.TIPO_CPTE.getValue());
            rs = RegistroConverter.toRegistro(
                            requestManager.get(urlRs2.getUrl(), param));

            if (Integer.valueOf(rs.getCampos()
                            .get(PedirTerceroControladorEnum.CONTEO.getValue())
                            .toString()) <= 0) {
                mensajeError = idioma.getString("TB_TB900");
                registroAutomatico = false;
                return;
            }

            consecutivo = ejbPresupuestoTres.generarRegistroConTipo(
                            compania,
                            Integer.parseInt(anoComprobante),
                            tipo,
                            tercero,
                            sucursal,
                            tipoComprobante,
                            new BigInteger(numeroComprobante),
                            objeto,
                            SessionUtil.getUser().getCodigo());

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametrosSalida = new HashMap<>();
        parametrosSalida.put(PedirTerceroControladorEnum.REGISTROAUTOMATICO
                        .getValue(), false);
        SessionUtil.setFlash(parametrosSalida);
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato, String formatoComprobante,
        BigInteger consecutivo) {

        ComprobantesContPresReporteador comprobantesContPresReporteador = new ComprobantesContPresReporteador(
                        ejbSysmanUtil);
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazar.put("ano", Integer.parseInt(anoComprobante));
        reemplazar.put("tipoCpte", tipo);
        reemplazar.put("numeroPptoInicial", consecutivo);
        reemplazar.put("numeroPptoFinal", consecutivo);
        parametros.put("PR_ANO", Integer.parseInt(anoComprobante));

        String parametro;

                parametro = SysmanFunciones.nvlStr(obtenerParametro("TEXTO EN FORMATO DIS_IN", true), " ");
                parametros.put("PR_TEXTO_EN_FORMATO_DIS_IN", parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO DE SECRETARIA DE HACIENDA", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_CARGO_SECRETARIA_HACIENDA.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE DE SECRETARIA DE HACIENDA", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_SECRETARIA_HACIENDA.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA FUENTE", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_COLUMNA_FUENTE.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA CENTRO COSTO", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_COLUMNA_CENTRO_COSTO.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA AUXILIAR GENERAL", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_COLUMNA_AUXILIAR_GENERAL.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA REFERENCIA", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_COLUMNA_REFERENCIA.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA COD PPTAL", true), " ");

                parametros.put(PedirTerceroControladorEnum.PR_NOMBRE_COLUMNA_COD_PPTAL.getValue(), parametro);

                parametro = SysmanFunciones.nvlStr(obtenerParametro("VISTO BUENO", false), " ");

                parametros.put(PedirTerceroControladorEnum.PR_VISTO_BUENO.getValue(), parametro);
                
                parametro = SysmanFunciones.nvlStr(obtenerParametro("TEXTO DE VENCIMIENTO EN FORMATO CDP", true)," ");
                parametros.put(PedirTerceroControladorEnum.PR_TEXTO_VENCIMIENTO_FORMATO_CDP.getValue(), parametro);
            
        Map<String, Object> valores = new HashMap<>();
        valores.put("informe", formatoComprobante);
        valores.put("formato", formato);
        valores.put("lote", false);
        archivoDescarga = comprobantesContPresReporteador
                        .generarInforme(valores, parametros, reemplazar);
               
             
    }
    
    /**
* metodo que llama para obtener el parametro - 
*/
private String obtenerParametro(String parametro, boolean mayMe) {
      try {
              return ejbSysmanUtil.consultarParametro(compania, parametro, modulo, new Date(), mayMe);
      } catch (SystemException e) {

              Logger.getLogger(PedirTerceroControlador.class.getName()).log(Level.SEVERE, null, e);
              JsfUtil.agregarMensajeError(e.getMessage());
      }
      return null;
}
   
    
    
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void ejecutaractualizaMensaje() {

        generarRegistro();
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4053")
                        .replace("s$tipo$s", tipo)
                        .replace("s$numero$s", numeroComprobante));

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        nombreTipo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        nombreTercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("SUCURSAL"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getNombreTercero() {
        return nombreTercero;
    }

    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCargarTipo() {
        return cargarTipo;
    }

    public void setCargarTipo(boolean cargarTipo) {
        this.cargarTipo = cargarTipo;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public Map<String, Object> getParametrosEntrada() {
        return parametrosEntrada;
    }

    public void setParametrosEntrada(
        Map<String, Object> parametrosEntrada) {
        this.parametrosEntrada = parametrosEntrada;
    }

    /**
     * @return the inactivoBoton
     */
    public boolean isInactivoBoton() {
        return inactivoBoton;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    /**
     * @param inactivoBoton
     * the inactivoBoton to set
     */
    public void setInactivoBoton(boolean inactivoBoton) {
        this.inactivoBoton = inactivoBoton;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
