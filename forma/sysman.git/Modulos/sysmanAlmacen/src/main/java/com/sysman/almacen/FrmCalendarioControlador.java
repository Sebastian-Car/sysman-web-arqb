package com.sysman.almacen;

import com.sysman.almacen.ejb.impl.EjbPrestamosCero;
import com.sysman.almacen.enums.FrmCalendarioControladorEnum;
import com.sysman.almacen.enums.FrmCalendarioControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 15/02/2016
 *
 * -- Modificado por lcortes 27,28/04/2017. Refactorizacion de codigo
 * de las listas para utilizar dss y se ajusta los llamados a
 * funciones, procedimientos y metodos de la clase Acciones.
 */
@ManagedBean
@ViewScoped
public class FrmCalendarioControlador extends BeanBaseDatosAcme {

    private final String compania;
    private final String codSerie;
    private final String codElemento;
    private Object[][] d;
    private String mes;
    private String anio;
    private String elemento;
    private String serie;
    private String fechaAct;
    private String descripcion;
    private List<Registro> listaAnio;
    private RegistroDataModelImpl listaDevolutivo;

    @EJB
    private EjbPrestamosCero ejbPrestamos;

    /**
     * Creates a new instance of FrmCalendarioControlador
     */
    public FrmCalendarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        codSerie = "SERIE";
        codElemento = "ELEMENTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CALENDARIO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmCalendarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        try {
            d = new Object[43][4];
            fechaAct = "Hoy:   "
                + SysmanFunciones.convertirAFechaCadena(new Date());
            anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                            Calendar.YEAR));
            mes = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                            Calendar.MONTH)
                + 1);
        }
        catch (ParseException ex) {
            Logger.getLogger(FrmCalendarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        if (permisos == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            permisos = form.getPermisos();
            if ((permisos == null) || !permisos[3]) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
        }
        cargarListaAnio();
        cargarListaDevolutivo();
        abrirFormulario();
    }

    public void cargarListaAnio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCalendarioControladorUrlEnum.URL122
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmCalendarioControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDevolutivo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCalendarioControladorUrlEnum.URL4476
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDevolutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "SERIE");
    }

    public void cambiarMESACT() {
        // <CODIGO_DESARROLLADO>
        if (validarCampos()) {
            crearCalendario(true);
        }
        else if ((anio != null) && !"".equals(anio) && (mes != null)
            && !"".equals(mes)) {
            crearCalendario(false);
        }
        else {
            limpiarCalendario();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        if (validarCampos()) {
            crearCalendario(true);
        }
        else if ((anio != null) && !"".equals(anio) && (mes != null)
            && !"".equals(mes)) {
            crearCalendario(false);
        }
        else {
            limpiarCalendario();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDevolutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        serie = registroAux.getCampos().get(codSerie).toString();
        elemento = registroAux.getCampos().get(codElemento).toString();
        descripcion = registroAux.getCampos().get("DESCRIPCION").toString();
        if (validarCampos()) {
            crearCalendario(true);
        }
        else if ((anio != null) && !"".equals(anio) && (mes != null)
            && !"".equals(mes)) {
            crearCalendario(false);
        }
        else {
            limpiarCalendario();
        }
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getFechaAct() {
        return fechaAct;
    }

    public void setFechaAct(String fechaAct) {
        this.fechaAct = fechaAct;
    }

    public Object[][] getD() {
        return d;
    }

    public void setD(Object[][] d) {
        this.d = d;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        crearCalendario(false);
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(
        List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    public RegistroDataModelImpl getListaDevolutivo() {
        return listaDevolutivo;
    }

    public void setListaDevolutivo(RegistroDataModelImpl listaDevolutivo) {
        this.listaDevolutivo = listaDevolutivo;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void crearCalendario(boolean conDatos) {
        limpiarCalendario();
        try {
            List<Registro> aux;
            int diaInicial;
            int diaFinal;

            Date fecha = SysmanFunciones
                            .convertirAFecha("01/" + mes + "/" + anio);
            diaInicial = fechaDeLunes(SysmanFunciones.getParteFecha(fecha,
                            Calendar.DAY_OF_WEEK));
            diaFinal = SysmanFunciones.getParteFecha(
                            SysmanFunciones.sumarRestarDiasFecha(SysmanFunciones
                                            .sumarRestarMesesFecha(fecha, 1),
                                            -1),
                            Calendar.DAY_OF_MONTH);
            Date fechaFin = SysmanFunciones
                            .convertirAFecha(diaFinal + "/" + mes + "/" + anio);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(FrmCalendarioControladorEnum.PARAM0.getValue(), mes);

            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmCalendarioControladorUrlEnum.URL8890
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            definirColorDia(diaInicial, diaFinal, aux, conDatos,
                            fecha, fechaFin);

        }
        catch (SystemException | ParseException ex) {
            Logger.getLogger(FrmCalendarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    private void definirColorDia(int diaInicial, int diaFinal,
        List<Registro> aux, boolean conDatos, Date fecha, Date fechaFin) {
        for (int i = 1; i <= diaFinal; i++) {
            boolean auxColor = false;
            d[(i + diaInicial) - 1][0] = i;
            d[(i + diaInicial) - 1][2] = true;
            for (Registro aux1 : aux) {
                if (String.valueOf(i).equals(aux1.getCampos().get("FESTIVO")
                                .toString())) {
                    auxColor = true;
                }
            }
            if (condDiaInicial(auxColor, i, diaInicial)) {
                d[(i + diaInicial) - 1][3] = "#ED1C24";
            }
            else {
                d[(i + diaInicial) - 1][3] = "#000000";
            }

            generarExlusion(conDatos, fecha, fechaFin, diaInicial);
        }

    }

    private boolean condDiaInicial(boolean auxColor, int i, int diaInicial) {
        if (auxColor || (((i + diaInicial) - 1) == 7)
            || (((i + diaInicial) - 1) == 14)
            || (((i + diaInicial) - 1) == 21)) {
            return true;
        }
        if ((((i + diaInicial) - 1) == 28)
            || (((i + diaInicial) - 1) == 35)
            || (((i + diaInicial) - 1) == 42)) {
            return true;
        }
        return false;
    }

    public void generarExlusion(boolean conDatos, Date fecha, Date fechaFin,
        int diaInicial) {
        try {
            List<Registro> aux;

            if (conDatos) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
                param.put(GeneralParameterEnum.SERIE.getName(), serie);

                aux = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmCalendarioControladorUrlEnum.URL10901
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (aux.isEmpty()) {
                    ejbPrestamos.generarExclusion(compania, fecha, fechaFin,
                                    elemento, Integer.parseInt(serie), -1);
                }
                else {
                    ejbPrestamos.generarExclusion(compania, fecha, fechaFin,
                                    aux.get(0).getCampos().get(codElemento)
                                                    .toString(),
                                    Integer.parseInt(aux.get(0).getCampos()
                                                    .get(codSerie).toString()),
                                    -1);

                    procedimientoListaNoVacia(aux, fecha, fechaFin);
                }

                Map<String, Object> paramAux = new TreeMap<>();
                paramAux.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                paramAux.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
                paramAux.put(GeneralParameterEnum.SERIE.getName(), serie);
                paramAux.put(GeneralParameterEnum.ANO.getName(), anio);
                paramAux.put(FrmCalendarioControladorEnum.PARAM0.getValue(),
                                mes);

                aux = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmCalendarioControladorUrlEnum.URL406
                                                                                                .getValue())
                                                                .getUrl(),
                                                paramAux));

                if (aux.isEmpty()) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3113"));
                }
                else {
                    partirFecha(aux, diaInicial);
                }
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmCalendarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void partirFecha(List<Registro> aux, int diaInicial) {
        int j;
        for (Registro aux1 : aux) {
            j = SysmanFunciones.getParteFecha(
                            (Date) aux1.getCampos()
                                            .get("FECHA_INICIO"),
                            Calendar.DAY_OF_MONTH);
            d[(j + diaInicial) - 1][1] = aux1.getCampos()
                            .get("LISTADO");
        }
    }

    public void procedimientoListaNoVacia(List<Registro> aux, Date fecha,
        Date fechaFin) {
        try {
            for (int i = 0; i <= aux.size(); i++) {
                ejbPrestamos.generarExclusion(compania, fecha, fechaFin,
                                aux.get(0).getCampos().get(codElemento)
                                                .toString(),
                                Integer.parseInt(aux.get(0).getCampos()
                                                .get(codSerie).toString()),
                                0);

            }
        }
        catch (NumberFormatException
                        | SystemException ex) {
            Logger.getLogger(FrmCalendarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public int fechaDeLunes(int number) {
        if (number != 1) {
            return number - 1;
        }
        else {
            return 7;
        }

    }

    public void limpiarCalendario() {
        for (Object[] d2 : d) {
            d2[0] = "";
            d2[1] = "";
            d2[2] = false;
        }
    }

    public boolean validarCampos() {
        if (SysmanFunciones.validarVariableVacio(serie)
            || SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mes)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3111"));
            return false;
        }

        return true;
    }

    // Metodo sin implementar
    public void renderizar() {
        // NO SE IMPLEMENTA
    }

    // Metodo sin implementar
    @Override
    public void cargarRegistro() {
        // NO SE IMPLEMENTA
    }

    // Metodo sin implementar
    @Override
    public void iniciarListasSubNulo() {
        // NO SE IMPLEMENTA
    }

    // Metodo sin implementar
    @Override
    public void iniciarListasSub() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void iniciarListas() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void reasignarOrigenGrilla() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void asignarOrigenDatos() {
        // NO SE IMPLEMENTA
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }
}
