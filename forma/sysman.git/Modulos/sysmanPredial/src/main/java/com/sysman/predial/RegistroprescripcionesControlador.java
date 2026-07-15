package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.RegistroprescripcionesControladorEnum;
import com.sysman.predial.enums.RegistroprescripcionesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 07/06/2016
 * @modified jguerrero
 * @version 2. 19/07/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class RegistroprescripcionesControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String nOrden;

    private final String cod;
    private String codigo;
    private String codigoPlantilla;
    private String anioPrescripcion;
    private String resolucion;
    private Date fechaPrescripcion;
    private String elabora;
    private String firma;
    private Date fechaPlantilla;
    private List<Registro> listaTxtAnoprescripcion;
    private RegistroDataModelImpl listaTxtCodigo;
    private RegistroDataModelImpl listaPlantilla;

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuat;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public RegistroprescripcionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cod = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROPRESCRIPCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RegistroprescripcionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaTxtCodigo();
        cargarListaPlantilla();
        cargarListaTxtAnoprescripcion();
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {

        asignarOrigenDatos();
        cargarListaTxtCodigo();
        cargarListaPlantilla();
        cargarListaTxtAnoprescripcion();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        // Metodo heredado del BeanBase
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtAnoprescripcion() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTxtAnoprescripcion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroprescripcionesControladorUrlEnum.URL4303
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTxtCodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroprescripcionesControladorUrlEnum.URL4302
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaTxtCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);

    }

    public void cargarListaPlantilla() {
        Map<String, Object> param = new TreeMap<>();
        param.put(RegistroprescripcionesControladorEnum.PARAM0.getValue(),
                        RegistroprescripcionesControladorEnum.PARAM1
                                        .getValue());
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroprescripcionesControladorUrlEnum.URL5072
                                                        .getValue());
        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTxtCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = validarString(registroAux, cod);
    }

    public void seleccionarFilaPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoPlantilla = validarString(registroAux, cod);
        fechaPlantilla = (Date) registroAux.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName());
    }

    public void oprimirCmdRegistrar() {
        // <CODIGO_DESARROLLADO>

        try {

            String anoMaximo = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB3084"),
                            SessionUtil.getModulo(),
                            new Date(), true);

            if (anoMaximo == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1262"));
                return;
            }

            if (Integer.valueOf(anioPrescripcion) <= Integer
                            .parseInt(anoMaximo)) {
                String observacion = idioma.getString("TB_TB1263");
                observacion = observacion.replace("s$resolucion$s",
                                resolucion);
                observacion = observacion.replace(
                                "s$fechaPrescripcion$s",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaPrescripcion));
                observacion = observacion.replace("s$elaborado$s",
                                elabora);
                observacion = observacion.replace("s$firmado$s", firma);

                ejbPredialCuat.registrarPreinscripcion(
                                Integer.parseInt(anioPrescripcion),
                                SessionUtil.getUser().getCodigo(), compania,
                                codigo, SysmanFunciones.convertirAFechaCadena(
                                                fechaPrescripcion,
                                                "dd/MM/yyyy"),
                                resolucion, observacion);

                JsfUtil.agregarMensajeInformativo(idioma
                                .getString("MSM_PROCESO_EJECUTADO"));

                codigo = null;
                anioPrescripcion = anoMaximo;
                fechaPrescripcion = null;
                resolucion = null;
                elabora = null;
                firma = null;

            }
            else {

                String msj;

                int anoM = Integer.valueOf(anoMaximo) + 1;

                msj = idioma.getString("TB_TB1265");
                msj = msj.replace("s$parametro$s", String.valueOf(anoM));

                JsfUtil.agregarMensajeInformativo(msj);

            }

        }
        catch (ParseException | NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RegistroprescripcionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnExo() {
        // <CODIGO_DESARROLLADO>
        try {
            String strNombreDocumento = "RegistroPrescripcion" + codigo;
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = codigoPlantilla;
            valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$nOrden$s",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            variablesConsultaW.put("s$codigo$s", "'" + codigo + "'");
            variablesConsultaW.put("s$resolucion$s", "'" + resolucion + "'");
            variablesConsultaW.put("s$anioPrescripcion$s",
                            "'" + anioPrescripcion + "'");
            variablesConsultaW.put("s$fechaPrescripcion$s", "'"
                + SysmanFunciones.convertirAFechaCadena(fechaPrescripcion)
                + "'");
            variablesConsultaW.put("s$anioPrescripcion2$s", anioPrescripcion);

            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash("281", SessionUtil.getModulo(),
                            campos,
                            valores);
        }
        catch (ParseException e) {
            Logger.getLogger(RegistroprescripcionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            String aux = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            idioma.getString("TB_TB3084"),
                                            SessionUtil.getModulo(),
                                            new Date(), true), "")
                            .toString();
            if (!"".equals(aux)) {
                anioPrescripcion = aux;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(RegistroprescripcionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoPlantilla() {
        return codigoPlantilla;
    }

    public void setCodigoPlantilla(String codigoPlantilla) {
        this.codigoPlantilla = codigoPlantilla;
    }

    public String getAnioPrescripcion() {
        return anioPrescripcion;
    }

    public void setAnioPrescripcion(String anioPrescripcion) {
        this.anioPrescripcion = anioPrescripcion;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public Date getFechaPrescripcion() {
        return fechaPrescripcion;
    }

    public void setFechaPrescripcion(Date fechaPrescripcion) {
        this.fechaPrescripcion = fechaPrescripcion;
    }

    public String getElabora() {
        return elabora;
    }

    public void setElabora(String elabora) {
        this.elabora = elabora;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public Date getFechaPlantilla() {
        return fechaPlantilla;
    }

    public void setFechaPlantilla(Date fechaPlantilla) {
        this.fechaPlantilla = fechaPlantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTxtAnoprescripcion() {
        return listaTxtAnoprescripcion;
    }

    public void setListaTxtAnoprescripcion(
        List<Registro> listaTxtAnoprescripcion) {
        this.listaTxtAnoprescripcion = listaTxtAnoprescripcion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTxtCodigo() {
        return listaTxtCodigo;
    }

    public void setListaTxtCodigo(RegistroDataModelImpl listaTxtCodigo) {
        this.listaTxtCodigo = listaTxtCodigo;
    }

    public RegistroDataModelImpl getListaPlantilla() {
        return listaPlantilla;
    }

    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
        this.listaPlantilla = listaPlantilla;
    }

    private String validarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
}
