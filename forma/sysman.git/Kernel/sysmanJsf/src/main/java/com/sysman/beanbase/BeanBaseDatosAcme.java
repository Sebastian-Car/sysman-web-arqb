/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.primefaces.component.panel.Panel;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 */
public abstract class BeanBaseDatosAcme extends AbstractBeanBaseAcme {

    protected Map<String, Object> rid;
    protected String accion;
    protected Map<String, Object> css;
    protected boolean nuevo;
    protected Registro registro;
    protected Map<String, Object> registroIni;
    protected String ridR;

    protected String origenDatos;
    protected String origenGrilla;

    protected RegistroDataModel listaInicial;
    protected RegistroDataModel listaInicialF;

    protected boolean alAbrir;
    protected String nombreConexion;
    protected ConectorPool conectorPool;

    public BeanBaseDatosAcme() {
        conectorPool = new ConectorPool();
        nombreConexion = ConectorPool.ESQUEMA_SYSMAN;
        alAbrir = true;
    }

    public void cargarRegistro(Map<String, Object> llave, String acc,
        int indice) {
        rid = indice != -2 ? null : rid;
        accion = acc;
        css = llave == null ? null : llave;
        try {
            conectorPool.conectar(nombreConexion);
            if (!nuevo) {
                iniciarListas();
                nuevo = true;
                registroIni = null;
            }
            if (css != null) {
                registro = service.getRegistro(conectorPool, origenDatos, tabla,
                                llave);
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
        catch (NamingException | SQLException ex) {
            Logger.getLogger(BeanBaseDatosAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseDatosAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
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
            listaInicial = new RegistroDataModel(nombreConexion,
                            ":FR" + numFormulario + ":TBFR" + numFormulario,
                            origenGrilla, false, llave);
            listaInicialF = new RegistroDataModel(nombreConexion,
                            ":FR" + numFormulario + ":TBFR" + numFormulario,
                            origenGrilla, false, llave);

        }

        if (alAbrir) {
            abrirFormulario();
            alAbrir = false;
        }
        rid = !permisos[2] && !permisos[3] ? null : rid;
        if (rid != null && css == null) {
            cargarRegistro(rid, (String) SysmanFunciones.nvl(accion, "m"), -2);
        }
    }

    public void agregarRegistroNuevo(boolean guardarNuevo) {
        try {
            conectorPool.conectar(nombreConexion);
            if (css == null) {
                if (insertarAntes() && actualizarAntes()) {
                    registro.getCampos().put("CREATED_BY",
                                    SessionUtil.getUser().getCodigo());
                    registro.getCampos().put("DATE_CREATED", new Date());
                    rid = Acciones.insertar(conectorPool, tabla,
                                    registro.getCampos(), llave);
                    insertarDespues();
                    actualizarDespues();
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_INGRESADO"));
                }
                else {
                    return;
                }
                accion = accion.equals(ACCION_INSERTAR) ? null : accion;
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
                if (actualizarAntes()) {
                    registro.getCampos().put("MODIFIED_BY",
                                    SessionUtil.getUser().getCodigo());
                    registro.getCampos().put("DATE_MODIFIED", new Date());
                    Acciones.actualizar(conectorPool, tabla,
                                    registro.getCampos(), registroIni, css);
                    registro.asignarLlaveOLD(llave);
                    css = registro.getLlave();
                    actualizarDespues();
                    registroIni = new HashMap<>(registro.getCampos());
                    JsfUtil.agregarMensajeInformativo(idioma
                                    .getString("MSM_REGISTRO_MODIFICADO"));
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
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException | IOException ex) {
            Logger.getLogger(BeanBaseDatosAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseDatosAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public void eliminarReg(Registro reg) {
        try {
            registro = reg;
            conectorPool.conectar(nombreConexion);
            if (eliminarAntes()) {
                Acciones.eliminar(conectorPool, tabla, reg.getLlave());
                eliminarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(BeanBaseDatosAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseDatosAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
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

    public RegistroDataModel getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModel listaInicial) {
        this.listaInicial = listaInicial;
    }

    public RegistroDataModel getListaInicialF() {
        return listaInicialF;
    }

    public void setListaInicialF(RegistroDataModel listaInicialF) {
        this.listaInicialF = listaInicialF;
    }

    public abstract void cargarRegistro();

    public abstract void iniciarListasSubNulo();

    public abstract void iniciarListasSub();

    public abstract void iniciarListas();

    public abstract void reasignarOrigenGrilla();

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
