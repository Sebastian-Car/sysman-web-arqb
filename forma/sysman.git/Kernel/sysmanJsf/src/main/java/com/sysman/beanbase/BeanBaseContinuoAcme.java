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

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 */
public abstract class BeanBaseContinuoAcme extends AbstractBeanBaseAcme {

    protected String origenDatos;

    protected boolean cargado;
    protected RegistroDataModel listaInicial;
    protected Registro registro;
    protected String nombreConexion;
    protected ConectorPool conectorPool;

    public BeanBaseContinuoAcme() {
        nombreConexion = ConectorPool.ESQUEMA_SYSMAN;
        conectorPool = new ConectorPool();
    }

    public void cargarForma() {
        if (!cargado) {
            listaInicial = new RegistroDataModel(nombreConexion,
                            ":FRFR" + numFormulario + ":TBFR" + numFormulario,
                            origenDatos, llave);

            cargado = true;
        }
    }

    public void agregarRegistroNuevo(ActionEvent actionEvent) {
        try {
            conectorPool.conectar(nombreConexion);
            if (insertarAntes() && actualizarAntes()) {
                registro.getCampos().put("CREATED_BY",
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put("DATE_CREATED", new Date());
                Acciones.insertar(conectorPool, tabla, registro.getCampos());
                insertarDespues();
                actualizarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));

            }
            registro = null;
            registro = new Registro(new HashMap<String, Object>());
            asignarValoresRegistro();
        }
        catch (InstantiationException | ClassNotFoundException | SQLException
                        | NamingException
                        | IllegalAccessException ex) {
            Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public void editar(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("RNUM");
            reg.getCampos().remove("RID");
            registro = reg;
            removerCombos();
            conectorPool.conectar(nombreConexion);
            if (actualizarAntes()) {
                int conteo = 0;
                registro.getCampos().put("MODIFIED_BY",
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put("DATE_MODIFIED", new Date());
                conteo = Acciones.actualizar(conectorPool, tabla,
                                registro.getCampos(), registro.getLlave());
                if (conteo > 0) {
                    actualizarDespues();
                    JsfUtil.agregarMensajeInformativo(idioma
                                    .getString("MSM_REGISTRO_MODIFICADO"));
                }
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                registro = null;
                registro = new Registro(new HashMap<String, Object>());
                asignarValoresRegistro();
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public void eliminarReg(Registro reg) {
        registro = reg;
        try {
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
            Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                registro = null;
                registro = new Registro(new HashMap<String, Object>());
                asignarValoresRegistro();
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(BeanBaseContinuoAcme.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public abstract void asignarValoresRegistro();

    public String getOrigenDatos() {
        return origenDatos;
    }

    public void setOrigenDatos(String origenDatos) {
        this.origenDatos = origenDatos;
    }

    public boolean isCargado() {
        return cargado;
    }

    public void setCargado(boolean cargado) {
        this.cargado = cargado;
    }

    public RegistroDataModel getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModel listaInicial) {
        this.listaInicial = listaInicial;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public abstract void removerCombos();

    public abstract void cancelarEdicion(RowEditEvent event);

    public abstract void reasignarOrigen();

}
