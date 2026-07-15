/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.primefaces.component.panel.Panel;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 */
public abstract class BeanBaseDatosAcmeImpl extends AbstractBeanBaseAcme {

    protected Map<String, Object> rid;
    protected String accion;
    protected Map<String, Object> css;
    protected boolean nuevo;
    protected Registro registro;
    protected Map<String, Object> registroIni;
    protected String ridR;

    protected String origenDatos;
    protected String origenGrilla;

    protected RegistroDataModelImpl listaInicial;
    protected RegistroDataModelImpl listaInicialF;

    protected boolean alAbrir;
    
    private AuditoriaService auditoriaService = new AuditoriaService();

    public BeanBaseDatosAcmeImpl() {
        alAbrir = true;
    }

    public void cargarRegistro(Map<String, Object> llave, String acc,
        int indice) {
        rid = indice != -2 ? null : rid;
        accion = acc;
        css = llave == null ? null : llave;
        try {
            if (!nuevo) {
                iniciarListas();
                nuevo = true;
                registroIni = null;
            }
            if (css != null) {
                registro = RegistroConverter.toRegistro(
                                requestManager.get(urlLectura.getUrl(), llave));
                registro.setLlave(llave);
                iniciarListasSub();
                registro.setIndice(indice);
                registroIni = new HashMap<>(registro.getCampos());

            }
            else {
                iniciarListasSubNulo();
                registro = new Registro(new HashMap<String, Object>());

            }
            cargarRegistro();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarRegistro(Map<String, Object> llave, String acc) {
        cargarRegistro(llave, acc, 0);
    }

    public void cargarRegistroNuevo() {
        cargarRegistro(null, ACCION_INSERTAR, 0);
    }

    public void cargarLista() throws IOException {
        if (listaInicial == null) {
            listaInicial = new RegistroDataModelImpl(llave, parametrosListado);
            listaInicialF = new RegistroDataModelImpl(llave, parametrosListado);
            listaInicial.setUrl(urlListado.getUrl());
            listaInicial.setUrlConteo(urlListado.getUrlConteo().getUrl());
            listaInicialF.setUrl(urlListado.getUrl());
            listaInicialF.setUrlConteo(urlListado.getUrlConteo().getUrl());
        }

        if (alAbrir) {
            abrirFormulario();
            alAbrir = false;
        }
        rid = !permisos[2] && !permisos[3] ? null : rid;
        if (rid != null && css == null) {
            cargarRegistro(rid, (String) SysmanFunciones.nvl(accion,  permisos[2] ? ACCION_MODIFICAR : ACCION_VER), -2);
        }
    }

    public void agregarRegistroNuevo(boolean guardarNuevo) {
        try {
            if (css == null) {
                if (insertarAntes() && actualizarAntes()) {
                    Map<String, Object> parameters = registro.getCampos();
                    parameters.put("CREATED_BY",
                                    SessionUtil.getUser().getCodigo());
                    parameters.put("DATE_CREATED", new Date());
                    Parameter parameter = new Parameter();
                    parameter.setFields(parameters);
                    rid = requestManager.save(urlCreacion.getUrl(),
                                    urlCreacion.getMetodo(), parameter);
                    insertarDespues();
                    actualizarDespues();
                    auditoriaService.auditar("i",(enumBase != null ? enumBase.getTable() : tabla),rid,parameters,null);
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_INGRESADO"));
                }
                else {
                    return;
                }
                accion = ACCION_INSERTAR.equals(accion) ? null : accion;
                if (!guardarNuevo && permisos[2]) {
                    accion = accion == null ? "m" : accion;
                    cargarRegistro(rid, accion, -2);
                }
                else if (!permisos[2]) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    Panel panelNuevo = (Panel) context.getViewRoot()
                                    .findComponent(":FR" + numFormulario
                                        + "_nuevo:nuevoPanel");
                    Panel panelLista = (Panel) context.getViewRoot()
                                    .findComponent(":FR" + numFormulario
                                        + ":lista");
                    panelNuevo.setVisible(guardarNuevo);
                    panelLista.setVisible(!guardarNuevo);
                    registro = new Registro(new HashMap<String, Object>());
                    accion = null;
                    css = null;
                }
            }
            else {
                Map<String, Object> mapaIni = new HashMap<>(
                                registro.getCampos());
                if (actualizarAntes()) {
                	Registro registroInicial = RegistroConverter.toRegistro(
                            requestManager.get(urlLectura.getUrl(), registro.getLlave()));
                	registro.getCampos().put("MODIFIED_BY",
                                    SessionUtil.getUser().getCodigo());
                    registro.getCampos().put("DATE_MODIFIED",
                                    new Date());
                    Map<String, Object> parameters = new HashMap<>(
                                    registro.getCampos());
                    parameters.remove("DATE_CREATED");
                    parameters.remove("CREATED_BY");

                    parameters.putAll(css);
                    Parameter parameter = new Parameter();
                    parameter.setFields(parameters);
                    int rta = requestManager.update(urlActualizacion.getUrl(),
                                    urlActualizacion.getMetodo(), parameter);
                    if (rta == 1) {
                        registro.setCampos(mapaIni);
                        registro.asignarLlave(llave);
                        actualizarDespues();
                        auditoriaService.auditar("m",(enumBase != null ? enumBase.getTable() : tabla),registro.getLlave(),registro.getCampos(),registroInicial.getCampos());
                        JsfUtil.agregarMensajeInformativo(
                                        idioma.getString(
                                                        "MSM_REGISTRO_MODIFICADO"));
                    }
                }
                else {
                    return;
                }
                if (guardarNuevo && permisos[2]) {
                    registro = new Registro(new HashMap<String, Object>());
                    accion = null;
                    css = null;
                }
            }
        }
        catch (SystemException ex) {
            ejecutarAlfallarInsercionActualizacion();
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void eliminarReg(Registro reg) {
        registro = reg;
        if (eliminarAntes()) {
            try {
            	Registro registroInicial = RegistroConverter.toRegistro(
                        requestManager.get(urlLectura.getUrl(), reg.getLlave()));
                RequestManager re = new RequestManager();
                int rta = re.delete(urlEliminacion.getUrl(),
                                reg.getLlave());
                if (rta == 1) {
                    eliminarDespues();
                    auditoriaService.auditar("e",(enumBase != null ? enumBase.getTable() : tabla),reg.getLlave(), null, (registroInicial != null ? registroInicial.getCampos() : null));
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_ELIMINADO"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void guardarNuevo() {
        agregarRegistroNuevo(true);
        if (accion == null) {
            cargarRegistroNuevo();
        }
    }

    public void seleccionarFilaBusqueda(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        listaInicial.setFilters(listaInicialF.getFilters());
        listaInicial.load(0);
        cargarRegistro(registroAux.getLlave(), accion, registroAux.getIndice());
    }

    private void ir(int i) {
        int tamPag = listaInicial.getPageSize();
        switch (i) {
        case 1:
            irPrimero(tamPag);
            break;
        case 2:
            irAnterior(tamPag);
            break;
        case 3:
            irSiguiente(tamPag);
            break;
        case 4:
            irUltimo(tamPag);
            break;
        default:
            break;
        }
    }

    private void irPrimero(int tamPag) {
        if (registro.getIndice() > tamPag) {
            listaInicial.load(0);
        }
        cargarRegistro(listaInicial.getDatasource().get(0).getLlave(),
                        accion, 0);
    }

    private void irAnterior(int tamPag) {
        int ir = registro.getIndice() == 0 ? listaInicial.getRowCount() - 1
            : registro.getIndice() - 1;
        boolean ultimo = false;
        if ((ir + 1) % tamPag == 0
            && ir != listaInicial.getRowCount() - 1) {
            listaInicial.load((ir + 1) - tamPag);
            ir = tamPag - 1;
        }
        else if (ir == listaInicial.getRowCount() - 1) {
            int aux = listaInicial.getRowCount() % tamPag;
            ultimo = true;
            aux = aux == 0 ? tamPag : aux;
            listaInicial.load(listaInicial.getRowCount() - (aux));
            ir = ir % tamPag;
        }
        else {
            ir = ir % tamPag;
        }
        cargarRegistro(listaInicial.getDatasource().get(ir).getLlave(),
                        accion,
                        ultimo ? listaInicial.getRowCount() - 1
                            : registro.getIndice() - 1);
    }

    public void irSiguiente(int tamPag) {
        int ir = registro.getIndice() == listaInicial.getRowCount() - 1 ? 0
            : registro.getIndice() + 1;
        boolean primero = false;
        if (ir == 0) {
            listaInicial.load(ir);
            primero = true;
        }
        else if (ir % tamPag == 0) {
            listaInicial.load(ir);
        }
        ir = ir % tamPag;
        cargarRegistro(listaInicial.getDatasource().get(ir).getLlave(),
                        accion,
                        primero ? 0 : registro.getIndice() + 1);
    }

    public void irUltimo(int tamPag) {
        if (registro.getIndice() < listaInicial.getRowCount() - tamPag) {
            int factor = listaInicial.getRowCount() % tamPag == 0
                ? listaInicial.getRowCount() - tamPag
                : listaInicial.getRowCount()
                    - (listaInicial.getRowCount() % tamPag);
            listaInicial.load(factor);
        }
        cargarRegistro(listaInicial.getDatasource()
                        .get(listaInicial.getDatasource().size() - 1)
                        .getLlave(), accion,
                        listaInicial.getRowCount() - 1);
    }

    public void irPrimero() {
        ir(1);
    }

    public void irAnterior() {
        ir(2);
    }

    public void irSiguiente() {
        ir(3);
    }

    public void irUltimo() {
        ir(4);
    }

    public void precargarRegistro() {
        if (!permisos[2]) {
            FacesContext context = FacesContext.getCurrentInstance();
            Panel panelNuevo = (Panel) context.getViewRoot().findComponent(
                            ":FR" + numFormulario + "_nuevo:nuevoPanel");
            Panel panelLista = (Panel) context.getViewRoot()
                            .findComponent(":FR" + numFormulario + ":lista");
            panelNuevo.setVisible(true);
            panelLista.setVisible(false);
        }
    }

    @Override
    public void buscarUrls() {
        super.buscarUrls();
        urlLectura = enumBase.getReadKey() != null
            ? UrlServiceUtil.getUrlBeanById(enumBase.getReadKey())
            : null;

    }

    public void ejecutarAlfallarInsercionActualizacion() {

    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Map<String, Object> getRegistroIni() {
        return registroIni;
    }

    public void setRegistroIni(Map<String, Object> registroIni) {
        this.registroIni = registroIni;
    }

    public String getOrigenDatos() {
        return origenDatos;
    }

    public void setOrigenDatos(String origenDatos) {
        this.origenDatos = origenDatos;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getRidR() {
        return ridR;
    }

    public void setRidR(String ridR) {
        this.ridR = ridR;
    }

    public String getOrigenGrilla() {
        return origenGrilla;
    }

    public void setOrigenGrilla(String origenGrilla) {
        this.origenGrilla = origenGrilla;
    }

    public RegistroDataModelImpl getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModelImpl listaInicial) {
        this.listaInicial = listaInicial;
    }

    public RegistroDataModelImpl getListaInicialF() {
        return listaInicialF;
    }

    public void setListaInicialF(RegistroDataModelImpl listaInicialF) {
        this.listaInicialF = listaInicialF;
    }

    public abstract void cargarRegistro();

    public abstract void iniciarListasSubNulo();

    public abstract void iniciarListasSub();

    public abstract void iniciarListas();

    public abstract void asignarOrigenDatos();

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public Map<String, Object> getCss() {
        return css;
    }

    public void setCss(Map<String, Object> css) {
        this.css = css;
    }

}
