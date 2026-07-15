package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmcertnoregistradosControladorEnum;
import com.sysman.predial.enums.FrmcertnoregistradosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
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
 * @author dsuesca
 * @version 1, 13/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 30/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */
@ManagedBean
@ViewScoped
public class FrmcertnoregistradosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String strCodigo;
    private final String consDireccion;
    private final String consTelefono;
    /**
     * Constante definida para almacenar la cadena "TB_TB288"
     */
    private final String cCert;
    /**
     * Constante definida para almacenar la cadena "TB_TB289"
     */
    private final String cPys;
    // <DECLARAR_ATRIBUTOS>
    private String recibo;
    private String expedicion;
    private String destino;
    private String plantilla;
    private String telefono;
    private String direccion;
    private String dadoEn;
    private String valor;
    private String numCer;
    private String nombre;
    private String cedula;
    private String llamadoPor;
    private String codigoPredio;
    private String banco;
    private String txtFechaPago;
    private String pagoAnio;
    private String sucursal;
    private String numOrden;
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTxtDestino;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaRecibo;
    private RegistroDataModelImpl listaTxtExpedicion;
    private RegistroDataModelImpl listaplantilla;
    private boolean generaReciboDePagoPazYSalvo;
    private Date fechaPlantilla;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbPredialOchoRemote ejbPredialOchoRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public FrmcertnoregistradosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigo = "CODIGO";
        cPys = "TB_TB289";
        cCert = "TB_TB288";
        consDireccion = "s$direccion$s";
        consTelefono = "s$telefono$s";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCERTNOREGISTRADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                llamadoPor = parametrosEntrada.get("FORMULARIO").toString();
            }

            titulo = llamadoPor.equals(idioma.getString(cCert))
                ? idioma.getString("TB_TB3487")
                : idioma.getString("TB_TB3488");
            dadoEn = SessionUtil.getCompaniaIngreso().getCiudad();
            valor = "0";
            numCer = generarConsecutivo();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmcertnoregistradosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.removeSessionVar(idioma.getString("TB_TB290"));
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaRecibo();
        cargarListaTxtExpedicion();
        cargarListaplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTxtDestino();
        // </CARGAR_LISTA>
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
        try {
            generaReciboDePagoPazYSalvo = "SI".equalsIgnoreCase(
                            SysmanFunciones.nvl(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            "GENERA RECIBO DE PAGO PARA PAZ Y SALVO",
                                                            modulo, new Date(),
                                                            true),
                                            "NO").toString());
            asignarOrigenDatos();
            cargarListaRecibo();
            abrirFormulario();
            cargarListaTxtExpedicion();
            cargarListaplantilla();
            cargarListaTxtDestino();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((listaRecibo == null) || listaRecibo.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB260"));
        }
    }

    @Override
    public void asignarOrigenDatos() {
        // METODO NO IMPLEMENTADO
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTxtDestino() {
        try {
            listaTxtDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertnoregistradosControladorUrlEnum.URL5730
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Se elimina filtro No Registrado debido a que no puede tener
     * este valor porque en el modelo actual tiene integridad con
     * IP_USUARIOSPREDIAL
     **/
    public void cargarListaRecibo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcertnoregistradosControladorUrlEnum.URL6316
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmcertnoregistradosControladorEnum.TIPO.getValue(), "PS");
        param.put(GeneralParameterEnum.CODIGO.getName(), "999999999999999");

        listaRecibo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "REFERENCIA");
    }

    public void cargarListaTxtExpedicion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmcertnoregistradosControladorEnum.PARAM1.getValue(),
                        SessionUtil.getCompaniaIngreso()
                                        .getCodigoPais());

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcertnoregistradosControladorUrlEnum.URL8489
                                                        .getValue());
        listaTxtExpedicion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NOMBRECIUDAD");
    }

    public void cargarListaplantilla() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmcertnoregistradosControladorEnum.TIPO.getValue(), "13");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcertnoregistradosControladorUrlEnum.URL9425
                                                        .getValue());
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaRecibo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        recibo = registroAux.getCampos().get("REFERENCIA").toString();
        if ("".equals(SysmanFunciones
                        .nvl(registroAux.getCampos().get("CERTIFICADO"), "")
                        .toString())) {
            cedula = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("NIT_NO_INSCRITRO"), "")
                            .toString();
            valor = SysmanFunciones
                            .nvl(registroAux.getCampos().get("VALOR"), "")
                            .toString();
            nombre = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("NOMBRE_NO_INSCRITRO"), "")
                            .toString();
            codigoPredio = SysmanFunciones
                            .nvl(registroAux.getCampos().get("CODIGO_PREDIO"),
                                            "")
                            .toString();
            banco = SysmanFunciones
                            .nvl(registroAux.getCampos().get("BANCO_PAGO"), "")
                            .toString();
            txtFechaPago = SysmanFunciones
                            .nvl(registroAux.getCampos().get("FECHA_PAGO"), "")
                            .toString();
            pagoAnio = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ANO_GENERADO"),
                                            "")
                            .toString();
            sucursal = SysmanFunciones
                            .nvl(registroAux.getCampos().get("SUCURSAL"), "")
                            .toString();
            numOrden = SysmanFunciones
                            .nvl(registroAux.getCampos().get("NUMERO_ORDEN"),
                                            "")
                            .toString();
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB262"));
        }

    }

    public void seleccionarFilaTxtExpedicion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        expedicion = registroAux.getCampos().get("NOMBRECIUDAD").toString();
    }

    public void seleccionarFilaplantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantilla = registroAux.getCampos().get(strCodigo).toString();
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    private void registrarCertificado() {
        String codPred = codigoPredio == null ? "999999999999999"
            : codigoPredio;
        String pagAn = pagoAnio == null ? "2016" : pagoAnio;
        String pagBan = banco == null ? "03" : banco;
        String pagFe = txtFechaPago;
        String numeroOrden = numOrden == null ? "999" : numOrden;
        String sucu = sucursal == null ? "999" : sucursal;
        String formulario = llamadoPor.equals(idioma.getString(cCert))
            ? "0" : "1";
        try {
            numCer = ejbPredialOchoRemote.registrarCertificado(compania,
                            formulario, codPred, direccion, nombre, cedula,
                            numeroOrden, Integer.parseInt(valor), sucu,
                            SessionUtil.getUser().getCodigo(), recibo,
                            destino, pagBan, pagFe == null ? null
                                : SysmanFunciones.convertirAFecha(pagFe),
                            Integer.parseInt(pagAn));
        }
        catch (NumberFormatException | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCmdImprimir() {
        if (validarUsuarios()) {

            listaplantilla.load();
            if (listaplantilla.getDatasource().isEmpty()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB263"));
            }
            else {
                String strNombreDocumento = idioma.getString("TB_TB264");
                String[] campos = new String[3];
                String[] valores = new String[3];
                String firma = "";
                String cargo = "";
                campos[0] = idioma.getString("TB_TB265");
                campos[1] = idioma.getString("TB_TB266");
                campos[2] = idioma.getString("TB_TB267");

                valores[0] = plantilla;
                valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
                valores[2] = strNombreDocumento;
                try {
                    firma = ejbSysmanUtilRemote.consultarParametro(compania,
                                    "NOMBRE TESORERO", modulo, new Date(),
                                    false);
                    cargo = ejbSysmanUtilRemote.consultarParametro(compania,
                                    "CARGO TESORERO", modulo, new Date(),
                                    false);

                    HashMap<String, String> variablesConsultaW = new HashMap<>();
                    variablesConsultaW.put("s$nombre$s", "'" + nombre + "'");
                    variablesConsultaW.put("s$cedula$s", "'" + cedula + "'");
                    variablesConsultaW.put("s$expedicion$s",
                                    "'" + expedicion + "'");
                    variablesConsultaW.put("s$destino$s", "'"
                        + service.buscarEnLista(destino, "CODIGO",
                                        "DESCRIPCION", listaTxtDestino)
                        + "'");

                    variablesConsultaW.put("s$dadoen$s", "'" + dadoEn + "'");
                    variablesConsultaW.put("s$firma$s", "'" + firma + "'");
                    variablesConsultaW.put("s$cargo$s", "'" + cargo + "'");
                    variables(variablesConsultaW);

                    registrarCertificado();

                    variablesConsultaW.put("s$numCer$s",
                                    "'Número: " + numCer + "'");

                    variablesConsultaW.put("s$fecha$s", "'"
                        + SysmanFunciones.convertirAFechaCadena(new Date())
                        + "'");

                    variablesConsultaW.put("s$fechavencimiento$s",
                                    "'" + SysmanFunciones.convertirAFechaCadena(
                                                    SysmanFunciones.sumarRestarMesesFecha(
                                                                    new Date(),
                                                                    1))
                                        + "'");
                    variablesConsultaW.put("s$valor$s", valor);

                    // variables por parametro para documento word
                    SessionUtil.setSessionVar("variablesConsultaWord",
                                    variablesConsultaW);
                    SessionUtil.cargarModalDatosFlash(Integer
                                    .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                    .getCodigo()),
                                    SessionUtil.getModulo(), campos, valores);
                }
                catch (SystemException | ParseException e) {
                    Logger.getLogger(FrmcertnoregistradosControlador.class
                                    .getName()).log(Level.SEVERE, null, e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
        }

        // </CODIGO_DESARROLLADO>
    }

    private void variables(HashMap<String, String> variablesConsultaW) {
        if (direccion.isEmpty()) {
            variablesConsultaW.put(consDireccion, "' '");
        }
        else {
            String dir = idioma.getString("TB_TB3023").replace(consDireccion,
                            direccion);
            variablesConsultaW.put(consDireccion,
                            "" + dir + "");
        }

        if (telefono.isEmpty()) {
            variablesConsultaW.put(consTelefono, "' '");
        }
        else {
            String tel = idioma.getString("TB_TB3024").replace(consTelefono,
                            telefono);
            variablesConsultaW.put(consTelefono, "" + tel + "");
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    public boolean validarUsuarios() {
        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(FrmcertnoregistradosControladorEnum.PARAM2.getValue(),
                            cedula);
            param.put(GeneralParameterEnum.NOMBRE.getName(), nombre);
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertnoregistradosControladorUrlEnum.URL18797
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((rs != null) && (rs.getCampos().get(strCodigo) != null)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB277") + " "
                                + rs.getCampos().get(strCodigo).toString()
                                + "." + idioma.getString("TB_TB287") + ".");
            return false;
        }

        return true;
    }

    /**
     * Realiza el calculo del consecutivo con el que se generara el
     * certificado
     * 
     * @return Numero del certificado a generar
     */
    private String generarConsecutivo() {
        String consecutivo = null;
        String pTipo = llamadoPor.equals(idioma.getString(cCert)) ? "C" : "P";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(FrmcertnoregistradosControladorEnum.TIPO.getValue(), pTipo);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertnoregistradosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                long actual = Long.parseLong(rs.getCampos()
                                .get(FrmcertnoregistradosControladorEnum.CONSECUTIVOREAL
                                                .getValue())
                                .toString());
                consecutivo = SysmanFunciones
                                .strZero(String.valueOf(actual + 1),
                                                Integer.parseInt(rs.getCampos()
                                                                .get(FrmcertnoregistradosControladorEnum.DIGITOS
                                                                                .getValue())
                                                                .toString()));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            valor = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(
                            compania, "TARIFA CERTIFICADO CATASTRAL", modulo,
                            new Date(), false),
                            "0").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        if (llamadoPor.equals(idioma.getString(cCert))) {
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PREDIALCERTCATASTRAL_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if (llamadoPor.equals(idioma.getString(cPys))) {
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PREDIALCERTPAZYSALVOCONPRE_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public String getExpedicion() {
        return expedicion;
    }

    public void setExpedicion(String expedicion) {
        this.expedicion = expedicion;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDadoEn() {
        return dadoEn;
    }

    public void setDadoEn(String dadoEn) {
        this.dadoEn = dadoEn;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getNumCer() {
        return numCer;
    }

    public void setNumCer(String numCer) {
        this.numCer = numCer;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTxtDestino() {
        return listaTxtDestino;
    }

    public void setListaTxtDestino(List<Registro> listaTxtDestino) {
        this.listaTxtDestino = listaTxtDestino;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaRecibo() {
        return listaRecibo;
    }

    public void setListaRecibo(RegistroDataModelImpl listaRecibo) {
        this.listaRecibo = listaRecibo;
    }

    public RegistroDataModelImpl getListaTxtExpedicion() {
        return listaTxtExpedicion;
    }

    public void setListaTxtExpedicion(
        RegistroDataModelImpl listaTxtExpedicion) {
        this.listaTxtExpedicion = listaTxtExpedicion;
    }

    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isGeneraReciboDePagoPazYSalvo() {
        return generaReciboDePagoPazYSalvo;
    }

    public void setGeneraReciboDePagoPazYSalvo(
        boolean generaReciboDePagoPazYSalvo) {
        this.generaReciboDePagoPazYSalvo = generaReciboDePagoPazYSalvo;
    }

    // </SET_GET_ADICIONALES>
}