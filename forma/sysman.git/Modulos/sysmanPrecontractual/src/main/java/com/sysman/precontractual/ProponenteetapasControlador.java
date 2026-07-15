package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoRemote;
import com.sysman.precontractual.enums.ProponenteetapasControladorEnum;
import com.sysman.precontractual.enums.ProponenteetapasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 22/03/2016
 * @modified jguerrero
 * @version 2. 04/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 *
 * @modified lcortes. 21/09/2017. Cambio en el metodo
 * oprimirCotizaInventario para usar el metodo redireccionarForma en
 * lugar de cargarModalDatosFlash.
 * @modified lcortes. 22/09/2017. Se agregan parametros a enviar al
 * formulario Proponenteiteminvenario en el metodo
 * oprimirCotizaInventario.
 */
@ManagedBean
@ViewScoped

public class ProponenteetapasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listaNitproponente;
    private RegistroDataModelImpl listaNitproponenteE;
    private String auxiliar;
    private String tipoContrato;
    private String transaccion;
    private String consecutivo;
    private String idEtapa;
    private String nombreEtapa;
    private HashMap<String, Object> rid;
    private String anio;
    private String condicion;
    private String estadoVigencia;
    private String estadoEtapa;
    private String estadoProceso;
    private int indice;
    private boolean modificarProponente;
    private boolean eliminarProponente;
    private boolean cotizaInventario;
    private String redonValorUnitarioIVA;
    private String digRedoValorUnitarioIVA;
    private String redondeoTotal;
    private String digRedonTotal;
    private String desdeMonitor;
    private String estadoPropMod;
    private String nombreCompleto;

    private boolean bloqueaEstado;

    private static final String TRANSACCION_CONS = ProponenteetapasControladorEnum.TRANSACCION
                    .getValue();

    private static final String PROPONENTE_CONS = ProponenteetapasControladorEnum.PROPONENTE
                    .getValue();

    @EJB
    private EjbPrecontractualUnoRemote ejbPreconActUno;

    /**
     * Creates a new instance of ProponenteetapasControlador
     */
    @SuppressWarnings("unchecked")
    public ProponenteetapasControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        try {
            tipoContrato = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.TIPOCONTRATO_LOWER
                                            .getValue());
            transaccion = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.CONSECUTIVO_TRANSACCION_LOWER
                                            .getValue());
            consecutivo = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.CONSECUTIVO_DETALLE_LOWER
                                            .getValue());
            idEtapa = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.ID_ETAPA_LOWER
                                            .getValue());
            nombreEtapa = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.NOMBRE_ETAPA_LOWER
                                            .getValue());
            rid = (HashMap<String, Object>) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.RID_LOWER
                                            .getValue());
            anio = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.ANIO_LOWER
                                            .getValue());
            condicion = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.CONDICION_LOWER
                                            .getValue());
            estadoVigencia = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.ESTADO_VIGENCIA_LOWER
                                            .getValue());
            estadoEtapa = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.ESTADO_ETAPA_LOWER
                                            .getValue());
            estadoProceso = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.ESTADO_PROCESO_LOWER
                                            .getValue());
            redonValorUnitarioIVA = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.REDON_VAL_UNI_IVA_LOWER
                                            .getValue());
            digRedoValorUnitarioIVA = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.DIG_RED_VAL_UNI_IVA_LOWER
                                            .getValue());
            redondeoTotal = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.REDONDEO_TOTAL_LOWER
                                            .getValue());
            digRedonTotal = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.DIG_REDON_TOTAL
                                            .getValue());
            String valorCotizaInventario = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.COTIZAR_INVENTARIO_LOWER
                                            .getValue());
            desdeMonitor = (String) parametrosEntrada
                            .get(ProponenteetapasControladorEnum.DESDE_MONITOR_LOWER
                                            .getValue());
            if ("C".equals(estadoEtapa) || "S".equals(estadoEtapa)) {
                modificarProponente = false;
                eliminarProponente = false;
            }
            else if ("P".equals(estadoEtapa)) {
                modificarProponente = false;
                eliminarProponente = true;
            }
            else if ("A".equals(estadoEtapa)) {
                modificarProponente = true;
                eliminarProponente = false;
            }
            if ("true".equals(valorCotizaInventario)) {
                cotizaInventario = true;
            }
            else {
                cotizaInventario = false;
            }
            if ("true".equals(desdeMonitor)) {
                modificarProponente = false;
                eliminarProponente = false;
            }
            numFormulario = GeneralCodigoFormaEnum.PROPONENTEETAPAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ProponenteetapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        enumBase = GenericUrlEnum.PROPONENTE;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        cargarListaNitproponente();
        cargarListaNitproponenteE();
        abrirFormulario();
        registro.getCampos()
                        .put(ProponenteetapasControladorEnum.FECHAINSCRIPCION
                                        .getValue(), new Date());

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosListado.put(TRANSACCION_CONS, transaccion);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);

    }

    public void cargarListaNitproponente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProponenteetapasControladorUrlEnum.URL6155
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(TRANSACCION_CONS, transaccion);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

        listaNitproponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PROPONENTE_CONS);

        // 14115 COMPANIA TIPOCONTRATO TRANSACCION CONSECUTIVO
    }

    public void cargarListaNitproponenteE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProponenteetapasControladorUrlEnum.URL6155
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(TRANSACCION_CONS, transaccion);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

        listaNitproponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PROPONENTE_CONS);

    }

    public void oprimirObservaciones(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        PROPONENTE_CONS)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2207"));
        }
        else {
            String[] campos = { ProponenteetapasControladorEnum.TIPOCONTRATO_LOWER
                            .getValue(),
                                ProponenteetapasControladorEnum.CONSECUTIVO_TRANSACCION_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.CONSECUTIVO_DETALLE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.PROPONENTE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.NOMBRE_PROPONENTE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.SUCURSAL_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.ID_ESTAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.NOMBRE_ETAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.MODIFICAR
                                                .getValue(),
                                ProponenteetapasControladorEnum.ESTADO_ETAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.ESTADO_PROPONENTE_LOWER
                                                .getValue() };
            String[] valores = { tipoContrato, transaccion, consecutivo,
                                 retornarString(reg, PROPONENTE_CONS),
                                 retornarString(reg,
                                                 ProponenteetapasControladorEnum.NOMBRECOMPLETO
                                                                 .getValue()),
                                 retornarString(reg,
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName()),
                                 idEtapa,
                                 nombreEtapa,
                                 Boolean.toString(modificarProponente),
                                 estadoEtapa,
                                 retornarString(reg, GeneralParameterEnum.ESTADO
                                                 .getName()) };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.OBSERVACIONES_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos,
                            valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPrerrequisitos(Registro reg, int indice) {
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        PROPONENTE_CONS)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2209"));
        }
        else {
            String[] campos = { ProponenteetapasControladorEnum.TIPOCONTRATO_LOWER
                            .getValue(),
                                ProponenteetapasControladorEnum.CONSECUTIVO_TRANSACCION_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.CONSECUTIVO_DETALLE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.PROPONENTE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.NOMBRE_PROPONENTE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.SUCURSAL_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.ID_ETAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.NOMBRE_ETAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.MODIFICAR
                                                .getValue(),
                                ProponenteetapasControladorEnum.ESTADO_ETAPA_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.ESTADO_PROPONENTE_LOWER
                                                .getValue(),
                                ProponenteetapasControladorEnum.ESTADO_VIGENCIA_LOWER
                                                .getValue() };
            String[] valores = { tipoContrato, transaccion, consecutivo,
                                 retornarString(reg, PROPONENTE_CONS),
                                 retornarString(reg,
                                                 ProponenteetapasControladorEnum.NOMBRECOMPLETO
                                                                 .getValue()),
                                 retornarString(reg,
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName()),
                                 idEtapa,
                                 nombreEtapa,
                                 Boolean.toString(modificarProponente),
                                 estadoEtapa,
                                 retornarString(reg, GeneralParameterEnum.ESTADO
                                                 .getName()),
                                 estadoVigencia };
            SessionUtil.cargarModalDatosFlashCerrar(
                            String.valueOf(GeneralCodigoFormaEnum.PRERREQUISITOSPROPONENTES_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos,
                            valores);
        }
    }

    public void oprimirCotizaInventario(Registro reg, int indice) {
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        PROPONENTE_CONS)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2210"));
        }
        else {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(ProponenteetapasControladorEnum.TIPOCONTRATO_LOWER
                            .getValue(), tipoContrato);
            parametros.put(ProponenteetapasControladorEnum.CONSECUTIVO_TRANSACCION_LOWER
                            .getValue(), transaccion);
            parametros.put(ProponenteetapasControladorEnum.CONSECUTIVO_DETALLE_LOWER
                            .getValue(), consecutivo);
            parametros.put(ProponenteetapasControladorEnum.PROPONENTE_LOWER
                            .getValue(), retornarString(reg, PROPONENTE_CONS));
            parametros.put(ProponenteetapasControladorEnum.NOMBRE_PROPONENTE_LOWER
                            .getValue(),
                            retornarString(reg,
                                            ProponenteetapasControladorEnum.NOMBRECOMPLETO
                                                            .getValue()));
            parametros.put(ProponenteetapasControladorEnum.SUCURSAL_LOWER
                            .getValue(),
                            retornarString(reg, GeneralParameterEnum.SUCURSAL
                                            .getName()));
            parametros.put(ProponenteetapasControladorEnum.ID_ETAPA_LOWER
                            .getValue(), idEtapa);
            parametros.put(ProponenteetapasControladorEnum.NOMBRE_ETAPA_LOWER
                            .getValue(), nombreEtapa);
            parametros.put(ProponenteetapasControladorEnum.ESTADO_ETAPA_LOWER
                            .getValue(), estadoEtapa);
            parametros.put(ProponenteetapasControladorEnum.ESTADO_PROPONENTE_LOWER
                            .getValue(),
                            retornarString(reg, GeneralParameterEnum.ESTADO
                                            .getName()));
            parametros.put(ProponenteetapasControladorEnum.MODIFICAR.getValue(),
                            Boolean.toString(modificarProponente));
            parametros.put(ProponenteetapasControladorEnum.REDON_VAL_UNI_IVA_LOWER
                            .getValue(), redonValorUnitarioIVA);
            parametros.put(ProponenteetapasControladorEnum.DIG_RED_VAL_UNI_IVA_LOWER
                            .getValue(), digRedoValorUnitarioIVA);
            parametros.put(ProponenteetapasControladorEnum.REDONDEO_TOTAL_LOWER
                            .getValue(), redondeoTotal);
            parametros.put(ProponenteetapasControladorEnum.DIG_REDON_TOTAL
                            .getValue(), digRedonTotal);
            parametros.put(ProponenteetapasControladorEnum.COTIZAR_INVENTARIO_LOWER
                            .getValue(), cotizaInventario);

            parametros.put(ProponenteetapasControladorEnum.RID_LOWER
                            .getValue(), rid);
            parametros.put(ProponenteetapasControladorEnum.ANIO_LOWER
                            .getValue(), anio);
            parametros.put(ProponenteetapasControladorEnum.CONDICION_LOWER
                            .getValue(), condicion);
            parametros.put(ProponenteetapasControladorEnum.ESTADO_VIGENCIA_LOWER
                            .getValue(), estadoVigencia);
            parametros.put(ProponenteetapasControladorEnum.ESTADO_PROCESO_LOWER
                            .getValue(), estadoProceso);
            parametros.put(ProponenteetapasControladorEnum.DESDE_MONITOR_LOWER
                            .getValue(), desdeMonitor);

            Direccionador dir = new Direccionador();
            dir.setParametros(parametros);
            dir.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PROPONENTEITEMINVENTARIOS_CONTROLADOR
                                            .getCodigo()));

            SessionUtil.redireccionarForma(dir, SessionUtil.getModulo());
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarESTADO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarESTADOC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        String estado = retornarString(
                        listaInicial.getDatasource().get(rowNum % 10),
                        GeneralParameterEnum.ESTADO.getName());

        if ("AC".equals(estado)) {
            for (int i = 0; i < listaInicial.getRowCount(); i++) {
                lanzarMensaje(estado, i);

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    private void lanzarMensaje(String estado, int i) {
        if ("AC".equals(estado) && (i != listaInicial.getRowIndex())
            && "AC".equals(retornarString(listaInicial.getDatasource().get(i),
                            GeneralParameterEnum.ESTADO.getName()))) {
            JsfUtil.agregarMensajeAlertaDialogo(
                            idioma.getString("TB_TB2212"));
        }

    }

    @Override
    public void abrirFormulario() {
        if (permisos[1]) {
            bloqueaEstado = true;
            registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
                            "EV");
        }
        else {
            bloqueaEstado = false;
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        registro.getCampos().put(TRANSACCION_CONS, transaccion);
        registro.getCampos()
                        .put(ProponenteetapasControladorEnum.CONSECUTIVODETALLE
                                        .getValue(), consecutivo);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>

        try {
            ejbPreconActUno.insertProponentesEtapas(compania, tipoContrato,
                            transaccion,
                            Long.parseLong(consecutivo),
                            retornarString(registro, PROPONENTE_CONS),
                            retornarString(registro,
                                            GeneralParameterEnum.SUCURSAL
                                                            .getName()),
                            cotizaInventario);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        estadoPropMod = retornarString(registro,
                        GeneralParameterEnum.ESTADO.getName());
        registro.getCampos()
                        .remove(ProponenteetapasControladorEnum.NOMBRECOMPLETO
                                        .getValue());
        registro.getCampos().remove(
                        ProponenteetapasControladorEnum.ESTADOLB.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        if ("AC".equals(estadoPropMod)) {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProponenteetapasControladorUrlEnum.URL19279
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
            fields.put(TRANSACCION_CONS, transaccion);
            fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            fields.put(GeneralParameterEnum.ESTADO.getName(), "RE");
            fields.put(PROPONENTE_CONS,
                            registro.getCampos().get(PROPONENTE_CONS));

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            try {
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
            }
            catch (SystemException e1) {

                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

        }
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        Registro regAux;
        int cont = 0;
        String mensaje = idioma.getString("TB_TB2213");
        /**
         * ******** Verificar que el proponente no tenga observaciones
         * asociadas *************
         */

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(TRANSACCION_CONS, transaccion);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
        param.put(PROPONENTE_CONS, registro.getCampos().get(PROPONENTE_CONS));

        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProponenteetapasControladorUrlEnum.URL7571
                                                                            .getValue())
                                            .getUrl(), param));

            String proponenteObs = retornarString(regAux, PROPONENTE_CONS);
            if (!"0".equals(proponenteObs)) {
                mensaje = SysmanFunciones.concatenar(mensaje,
                                idioma.getString("TB_TB2214"));
                cont = +cont;
            }
            /**
             * ******** Verificar que el proponente no tenga
             * prerrequisitos asociados *************
             */

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProponenteetapasControladorUrlEnum.URL7572
                                                                            .getValue())
                                            .getUrl(), param));

            String proponentePre = retornarString(regAux, PROPONENTE_CONS);

            if (!"0".equals(proponentePre)) {
                mensaje = SysmanFunciones.concatenar(mensaje,
                                idioma.getString("TB_TB2215"));
                cont = +cont;
            }

            /**
             * ******** Verificar que el proponente no tenga elementos
             * asociados *************
             */

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProponenteetapasControladorUrlEnum.URL16328
                                                                            .getValue())
                                            .getUrl(), param));

            String proponenteElem = retornarString(regAux, PROPONENTE_CONS);
            if (!"0".equals(proponenteElem)) {
                mensaje = SysmanFunciones.concatenar(mensaje,
                                idioma.getString("TB_TB2216"));
                cont = +cont;
            }
            /**
             * ******** Verificar que el proponente no tenga elementos
             * asociados *************
             */

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProponenteetapasControladorUrlEnum.URL17930
                                                                            .getValue())
                                            .getUrl(), param));

            String proponenteVar = retornarString(regAux, PROPONENTE_CONS);
            if (!"0".equals(proponenteVar)) {
                mensaje = SysmanFunciones.concatenar(mensaje,
                                idioma.getString("TB_TB2217"));
                cont = +cont;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (cont < 1) {
            return true;
        }
        else {
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(mensaje,
                            idioma.getString("TB_TB2218")));
            return false;
        }

        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // Metodo heradado del BeanBase

    }

    public void ejecutarrcCerrar() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(ProponenteetapasControladorEnum.RID_LOWER
                        .getValue(), rid);
        parametros.put(ProponenteetapasControladorEnum.TIPOCONTRATO_LOWER
                        .getValue(), tipoContrato);
        parametros.put(ProponenteetapasControladorEnum.ANIO_LOWER
                        .getValue(), anio);
        parametros.put(ProponenteetapasControladorEnum.CONDICION_LOWER
                        .getValue(), condicion);
        parametros.put(ProponenteetapasControladorEnum.ESTADO_VIGENCIA_LOWER
                        .getValue(), estadoVigencia);
        parametros.put(ProponenteetapasControladorEnum.ESTADO_PROCESO_LOWER
                        .getValue(), estadoProceso);
        parametros.put(ProponenteetapasControladorEnum.DESDE_MONITOR_LOWER
                        .getValue(), desdeMonitor);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    public void cambiarNitproponenteC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().put(
                                        ProponenteetapasControladorEnum.NOMBRECOMPLETO
                                                        .getValue(),
                                        nombreCompleto);

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNitproponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(PROPONENTE_CONS,
                        registroAux.getCampos().get(PROPONENTE_CONS));
        registro.getCampos().put(ProponenteetapasControladorEnum.NOMBRECOMPLETO
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    public void seleccionarFilaNitproponenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, PROPONENTE_CONS);
        registro.getCampos().put(PROPONENTE_CONS, auxiliar);

        nombreCompleto = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), "EV");
        registro.getCampos()
                        .put(ProponenteetapasControladorEnum.FECHAINSCRIPCION
                                        .getValue(), new Date());
    }

    public RegistroDataModelImpl getListaNitproponente() {
        return listaNitproponente;
    }

    public void setListaNitproponente(
        RegistroDataModelImpl listaNitproponente) {
        this.listaNitproponente = listaNitproponente;
    }

    public RegistroDataModelImpl getListaNitproponenteE() {
        return listaNitproponenteE;
    }

    public void setListaNitproponenteE(
        RegistroDataModelImpl listaNitproponenteE) {
        this.listaNitproponenteE = listaNitproponenteE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isModificarProponente() {
        return modificarProponente;
    }

    public void setModificarProponente(boolean modificarProponente) {
        this.modificarProponente = modificarProponente;
    }

    public boolean isEliminarProponente() {
        return eliminarProponente;
    }

    public void setEliminarProponente(boolean eliminarProponente) {
        this.eliminarProponente = eliminarProponente;
    }

    public boolean isCotizaInventario() {
        return cotizaInventario;
    }

    public void setCotizaInventario(boolean cotizaInventario) {
        this.cotizaInventario = cotizaInventario;
    }

    public boolean isBloqueaEstado() {
        return bloqueaEstado;
    }

    public void setBloqueaEstado(boolean bloqueaEstado) {
        this.bloqueaEstado = bloqueaEstado;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
