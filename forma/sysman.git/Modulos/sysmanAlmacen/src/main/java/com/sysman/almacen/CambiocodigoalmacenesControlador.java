package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.CambiocodigoalmacenesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * @version 1, 02/12/2015
 * @author yrojas
 * @version 2, 27/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se modifico controlador segun especificaciones del
 * SonarLint. El llamado a Acciones fue cambiado por la invocaci�n de
 * los EJB
 * 
 * @version 3, 04/05/2017, pespitia:<br>
 * Se ajusto la operacion principal de insertar en el controlador.
 * <br>
 * Se reemplazo el texto quemado por texto en bean.
 */
@ManagedBean
@ViewScoped
public class CambiocodigoalmacenesControlador extends BeanBaseContinuoAcmeImpl {

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCero;

    private final String compania;
    private final String cambioCons;
    private final String codigoElementoAndCons;

    private RegistroDataModelImpl listaCodigoElementoAnt;
    private RegistroDataModelImpl listaCodigoElementoAntE;
    private String auxiliar;
    private String menuActual;
    private String tipoCambio;
    private boolean dialogoRCVisible;
    private int indice;

    /**
     * Creates a new instance of CambiocodigoalmacenesControlador
     */
    public CambiocodigoalmacenesControlador() {
        super();
        compania = SessionUtil.getCompania();
        cambioCons = "CAMBIO";
        codigoElementoAndCons = "CODIGOELEMENTOANT";
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOCODIGOALMACENES_CONTROLADOR.getCodigo();

            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
            if ("100303".equals(menuActual)) {
                tipoCambio = "C";
            }

            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(CambiocodigoalmacenesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CAMBIOCODIGOALMACEN;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro(new HashMap<String, Object>());
        asignarValoresRegistro();
        cargarListaCodigoElementoAnt();
        cargarListaCodigoElementoAntE();

        abrirFormulario();

    }

    @Override
    public void asignarValoresRegistro() {
        registro = new Registro();
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put("TIPOCAMBIO", tipoCambio);
    }

    public boolean isDialogoRCVisible() {
        return dialogoRCVisible;
    }

    public void setDialogoRCVisible(boolean dialogoRCVisible) {
        this.dialogoRCVisible = dialogoRCVisible;
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("TIPO", tipoCambio);
    }

    public void cargarListaCodigoElementoAnt() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiocodigoalmacenesControladorUrlEnum.URL3574
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoElementoAnt = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoAndCons);
    }

    public void cargarListaCodigoElementoAntE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiocodigoalmacenesControladorUrlEnum.URL3932
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoElementoAntE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoAndCons);
    }

    public void aceptardialogRealizaCambio() {
        //HEREDADO DEL BEAN BASE
    }

    public void cancelardialogRealizaCambio() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCambio() {
        // <CODIGO_DESARROLLADO>
        // dialogoRCVisible = true
        if ((boolean) registro.getCampos().get(cambioCons)) // </CODIGO_DESARROLLADO>
        {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3096"));
        }
    }

    public void cambiarCambioC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum).getCampos()
                        .get(cambioCons)) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3096"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoElementoAnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoElementoAndCons,
                        registroAux.getCampos().get(codigoElementoAndCons));
    }

    public void seleccionarFilaCodigoElementoAntE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElementoAndCons),
                                        "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            long codigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "CAMBIOCODIGOALMACEN",
                            "COMPANIA = ''" + compania + "''", "CODIGO", "1");

            parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);

            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);
        }
        catch (SystemException ex) {
            Logger.getLogger(CambiocodigoalmacenesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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

        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());

        boolean realizaCambio;
        try {
            // <CODIGO_DESARROLLADO
            if (indice != -1) {
                realizaCambio = (boolean) registro.getCampos().get(cambioCons);
            }
            else {
                realizaCambio = (boolean) listaInicial.getDatasource()
                                .get(indice).getCampos().get(cambioCons);
            }
            if (realizaCambio) {
                String respuesta = ejbAlmacenCero.actualizarElementoInventario(
                                compania,
                                registro.getCampos().get(codigoElementoAndCons)
                                                .toString(),
                                registro.getCampos().get("CODIGOELEMENTONUE")
                                                .toString(),
                                tipoCambio);
                if ("OK".equalsIgnoreCase(respuesta) || respuesta == "OK") {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB869"));
                    return true;
                }
                else {
                    JsfUtil.agregarMensajeError(respuesta);
                    return false;
                    // </CODIGO_DESARROLLADO>
                }
            }
            else {
                return true;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(CambiocodigoalmacenesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
            return false;
        }

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

    /**
     * 
     * @param registro
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    @Override
    public void removerCombos() {
        // Metodo heredado
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public RegistroDataModelImpl getListaCodigoElementoAnt() {
        return listaCodigoElementoAnt;
    }

    public void setListaCodigoElementoAnt(
        RegistroDataModelImpl listaCodigoElementoAnt) {
        this.listaCodigoElementoAnt = listaCodigoElementoAnt;
    }

    public RegistroDataModelImpl getListaCodigoElementoAntE() {
        return listaCodigoElementoAntE;
    }

    public void setListaCodigoElementoAntE(
        RegistroDataModelImpl listaCodigoElementoAntE) {
        this.listaCodigoElementoAntE = listaCodigoElementoAntE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

}
