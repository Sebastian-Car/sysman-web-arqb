package com.sysman.beanbase;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;

public abstract class BeanBaseContinuoAcmeImpl extends AbstractBeanBaseAcme {

    protected String origenDatos;

    protected boolean cargado;
    protected RegistroDataModelImpl listaInicial;
    protected Registro registro;
    
    private AuditoriaService auditoriaService = new AuditoriaService();

    public void cargarForma() {
        if (!cargado) {

            listaInicial = new RegistroDataModelImpl(llave, parametrosListado);
            if (urlListado != null) {
                listaInicial.setUrl(urlListado.getUrl());
                listaInicial.setUrlConteo(urlListado.getUrlConteo().getUrl());
                cargado = true;
            }
        }
    }

    public void agregarRegistroNuevo(ActionEvent actionEvent) {
        if (insertarAntes() && actualizarAntes()) {
            Map<String, Object> parameters = registro.getCampos();
            Map<String, Object> llaves = new HashMap<>();
            try {
                parameters.put("CREATED_BY", SessionUtil.getUser().getCodigo());
                parameters.put("DATE_CREATED", new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(parameters);
                llaves = requestManager.save(urlCreacion.getUrl(),
                                urlCreacion.getMetodo(), parameter);
                auditoriaService.auditar("i",(enumBase != null ? enumBase.getTable() : tabla), llaves, registro.getCampos(), null);
                insertarDespues();
                actualizarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            registro = null;
            registro = new Registro(new HashMap<String, Object>());
            asignarValoresRegistro();
        }

    }

    public void editar(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().remove("RNUM");
        reg.getCampos().remove("RID");
        registro = reg;
        removerCombos();
        if (actualizarAntes()) {
            try {
            	Registro registroInicial = auditoriaService.obtenerDatosAntesContinuo((enumBase != null ? enumBase.getTable() : tabla),registro.getLlave());
            	Map<String, Object> datosIniciales = registroInicial.getCampos();
            	Map<String, Object> parameters = reg.getCampos();
                parameters.putAll(reg.getLlave());
                parameters.put("MODIFIED_BY",
                                SessionUtil.getUser().getCodigo());
                parameters.put("DATE_MODIFIED",
                                new Date());

                Parameter parameter = new Parameter();
                parameter.setFields(parameters);
                int rta = requestManager.update(urlActualizacion.getUrl(),
                                urlActualizacion.getMetodo(), parameter);
                if (rta == 1) {
                    actualizarDespues();
                    auditoriaService.auditar("m",(enumBase != null ? enumBase.getTable() : tabla),registro.getLlave(),registro.getCampos(), datosIniciales);
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(
                                                    "MSM_REGISTRO_MODIFICADO"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            finally {
                reiniciarRegistro();
            }
        }
    }

    public void eliminarReg(Registro reg) {
        registro = reg;
        if (eliminarAntes()) {
            try {
                RequestManager re = new RequestManager();
                int rta = re.delete(urlEliminacion.getUrl(),
                                reg.getLlave());
                if (rta == 1) {
                    eliminarDespues();
                    auditoriaService.auditar("e",(enumBase != null ? enumBase.getTable() : tabla), registro.getLlave(), null, registro.getCampos());
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_ELIMINADO"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            finally {
                reiniciarRegistro();
            }

        }

    }

    private void reiniciarRegistro() {
        registro = null;
        registro = new Registro(new HashMap<String, Object>());
        asignarValoresRegistro();
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

    public RegistroDataModelImpl getListaInicial() {
        return listaInicial;
    }

    public void setListaInicial(RegistroDataModelImpl listaInicial) {
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
