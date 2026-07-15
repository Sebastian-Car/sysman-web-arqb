package com.sysman.predial;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.DecuentoanosControladorEnum;
import com.sysman.predial.enums.DecuentoanosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 20/05/2016
 *
 * @modifier amonroy
 * @version 2, 28/06/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones que son llamadas en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class DecuentoanosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String diaLiminteCons;
    private final String idDescuentoCons;
    private List<Registro> listaano;
    private List<Registro> listames;
    private boolean botonDescuentosVisible;
    private boolean mesMayorAmnistia;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of DecuentoanosControlador
     */
    public DecuentoanosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        diaLiminteCons = "DIA_LIMITE";
        idDescuentoCons = "ID_DESCUENTO";
        mesMayorAmnistia = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.DECUENTOANOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DecuentoanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_DESCUENTOS_ANO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaano();
        cargarListames();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DecuentoanosControladorUrlEnum.URL2945
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListames() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DecuentoanosControladorEnum.ANIO.getValue(), SysmanFunciones
                        .nvl(registro.getCampos().get("ANO"), "").toString());
        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DecuentoanosControladorUrlEnum.URL3330
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

    public void oprimirDescuentosEspeciales() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMDSCTOESPECIALES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ano
     *
     */
    public void cambiarano() {
        // <CODIGO_DESARROLLADO>
        cargarListames();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control mes
     *
     */
    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dia
     *
     */
    public void cambiardia() {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(registro.getCampos().get(diaLiminteCons)
                        .toString()) == 0) {
            registro.getCampos().put(diaLiminteCons, 1);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmesC(int rowNum) {
        int intAmnistia;

        String anioS = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("ANO").toString();
        int anio = Integer.parseInt(anioS);

        intAmnistia = mesesAdministia(anio);

        String mesA = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("MES").toString();
        int mes = Integer.parseInt(mesA);
        if (mes > intAmnistia) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1008"));
            mesMayorAmnistia = true;
            return;
        }

    }

    public void cambiardiaC(int rowNum) {

        String fechaDes;
        int ultDia;

        int mes = Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("MES").toString());
        int ano = Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("ANO").toString());
        String mesU = String.valueOf(mes);
        if (mesU.length() < 2) {
            mesU = "0" + mes;
        }

        fechaDes = "01/" + mesU + "/" + ano + "";
        Date fechaDesu;

        int dia = Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(diaLiminteCons).toString());

        try {
            fechaDesu = SysmanFunciones.convertirAFecha(fechaDes);
            ultDia = SysmanFunciones.ultimoDiaInt(fechaDesu);

            if (ultDia < dia) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1010"));

                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(diaLiminteCons, ultDia);
            }

        }
        catch (ParseException e) {
            Logger.getLogger(DecuentoanosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     * Metodo ejecutado al cambiar el control ano en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiaranoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        this.registro = listaInicial.getDatasource().get(rowNum % 10);
        cargarListames();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA DESCUENTOS ESPECIALES POR PREDIO", modulo,
                            new Date(), true);

            if (parametro == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1009"));
                return;
            }

            botonDescuentosVisible = "SI".equals(parametro) ? true : false;

        }
        catch (SystemException e) {
            Logger.getLogger(DecuentoanosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public int mesesAdministia(int anio) {
        Registro rs;
        int mesesAdministia = 0;

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NUMERO.getName(), anio);

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DecuentoanosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                mesesAdministia = Integer.parseInt(rs.getCampos()
                                .get("MESESAMNISTIA_PREDIAL").toString());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return mesesAdministia;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!validarMesAnmistia() || !validarFechas()) {
            return false;
        }
        long consecutivoDescuentos;
        try {
            consecutivoDescuentos = ejbSysmanUtil
                            .generarConsecutivoConValorInicial(
                                            "IP_DESCUENTOS_ANO",
                                            "COMPANIA = ''" + compania + "''",
                                            idDescuentoCons, "1");

            registro.getCampos().put(idDescuentoCons, consecutivoDescuentos);
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        cargarListames();
        if (!validarRegistro()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB149"));
            return false;
        }
        if (!validarFechas() || mesMayorAmnistia) {
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarFechas() {
        String fechaDes;
        int ultDia;

        int mes = Integer.parseInt(registro.getCampos().get("MES").toString());
        int ano = Integer.parseInt(registro.getCampos().get("ANO").toString());
        String mesU = String.valueOf(mes);
        if (mesU.length() < 2) {
            mesU = "0" + mes;
        }

        fechaDes = "01/" + mesU + "/" + ano + "";
        Date fechaDesu;

        int dia = Integer.parseInt(
                        registro.getCampos().get(diaLiminteCons).toString());

        try {
            fechaDesu = SysmanFunciones.convertirAFecha(fechaDes);
            ultDia = SysmanFunciones.ultimoDiaInt(fechaDesu);

            if (dia > ultDia) {

                dia = ultDia;
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1010"));
            }
            registro.getCampos().put(diaLiminteCons, dia);

        }
        catch (ParseException e) {

            Logger.getLogger(DecuentoanosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return true;
    }

    public boolean validarRegistro() {
        boolean validar = true;

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "ANO")
            || SysmanFunciones.validarCampoVacio(registro.getCampos(), "MES")) {

            validar = false;
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "PORCENTAJE")
            ||
            SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "PORC_INTERES")) {

            validar = false;
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        diaLiminteCons)) {

            validar = false;
        }

        return validar;
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(idDescuentoCons);
        registro.getCampos().remove("NOMBREMES");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        mesMayorAmnistia = false;
        indice = listaInicial.getRowIndex();
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaano() {
        return listaano;
    }

    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    public boolean isBotonDescuentosVisible() {
        return botonDescuentosVisible;
    }

    public void setBotonDescuentosVisible(boolean botonDescuentosVisible) {
        this.botonDescuentosVisible = botonDescuentosVisible;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    private boolean validarMesAnmistia() {
        boolean respuesta = true;
        int intAmnistia;

        String anioS = registro.getCampos().get("ANO").toString();
        int anio = Integer.parseInt(anioS);

        intAmnistia = mesesAdministia(anio);

        String mesA = registro.getCampos().get("MES").toString();
        int mes = Integer.parseInt(mesA);
        if (mes > intAmnistia) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1008"));
            respuesta = false;
            mesMayorAmnistia = true;
        }
        return respuesta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
