package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.SubobjnovedadsControladorEnum;
import com.sysman.bancoproyectos.enums.SubobjnovedadsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 10/09/2015
 * 
 * @version 2, 28/09/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 */

@ManagedBean
@ViewScoped
public class SubobjnovedadsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String codigo;
    private String tipoT;
    private String claseT;
    private String novedad;
    private String dependencia;
    private String auxTotales;
    private String valorInversion;
    private Map<String, Object> ridSolicitudS;
    private List<Registro> listaTotales;
    private String consulta;
    private String proyecto;
    private String vigenciaPeriodo;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of SubobjnovedadsControlador
     */
    public SubobjnovedadsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBOBJNOVEDADS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoT = (String) parametrosEntrada.get("tipoTFiltrarM");
                claseT = (String) parametrosEntrada.get("claseTS");
                novedad = (String) parametrosEntrada.get("codigo");
                dependencia = (String) parametrosEntrada.get("dependencia");
                vigenciaPeriodo = (String) parametrosEntrada.get("vigenciaPeriodo");
                ridSolicitudS = (Map<String, Object>) parametrosEntrada
                                .get("ridSolicitud");
                parametrosEntrada.put("rid", ridSolicitudS);
                parametrosEntrada.remove("ridSolicitud");
            }
            SessionUtil.cleanFlash();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SubobjnovedadsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase=GenericUrlEnum.BP_OBJNOVEDADPROY;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        calcularTotales();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), claseT);
        parametrosListado.put(SubobjnovedadsControladorEnum.PARAM1.getValue(), tipoT);
        parametrosListado.put(SubobjnovedadsControladorEnum.PARAM2.getValue(), novedad);
        parametrosListado.put(SubobjnovedadsControladorEnum.PARAM3.getValue(), dependencia);
    }



    private void calcularTotales() {
        reiniciarTotales();
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASE.getName(), claseT);
            param.put(SubobjnovedadsControladorEnum.PARAM1.getValue(), tipoT);
            param.put(SubobjnovedadsControladorEnum.PARAM2.getValue(), novedad);
            param.put(SubobjnovedadsControladorEnum.PARAM3.getValue(), dependencia);
            listaTotales = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubobjnovedadsControladorUrlEnum.URL10567
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (!listaTotales.isEmpty()) {
            DecimalFormat dblDF = new DecimalFormat("##,###.00");
            valorInversion = dblDF.format(listaTotales.get(0).getCampos().get("INVERSION"));
        }
        else {
            valorInversion = "0";
        }
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
    public void removerCombos() {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public boolean insertarAntes() {

        String criterio = SysmanFunciones.concatenar("COMPANIA =''", compania,
                        "''  AND TIPOT= ''", tipoT, "'' AND CLASET = ''", claseT,
                        "''  AND NOVEDAD = ", novedad, " AND DEPENDENCIA = ''",
                        dependencia,"''");
        try {
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                            "BP_OBJNOVEDADPROY", criterio,
                                            "CODIGO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("TIPOT", tipoT);
        registro.getCampos().put("CLASET", claseT);
        registro.getCampos().put(GeneralParameterEnum.NOVEDAD.getName(),
                        novedad);
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);
        return true;
    }
    
    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cleanFlash();
        Map<String, Object> parametros = new TreeMap<>();
        parametros.put("rid", ridSolicitudS);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_SOLICITUD_CDP_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        calcularTotales();
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        calcularTotales();
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        calcularTotales();
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // NO ESTA IMPLEMENTADO
    }
    
    public Map<String, Object> getRidSolicitudS() {
        return ridSolicitudS;
    }

    public void setRidSolicitudS(Map<String, Object> ridSolicitudS) {
        this.ridSolicitudS = ridSolicitudS;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public List<Registro> getListaTotales() {
        return listaTotales;
    }

    public void setListaTotales(List<Registro> listaTotales) {
        this.listaTotales = listaTotales;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public void reiniciarTotales() {
        valorInversion = "0";
    }

    public String getValorInversion() {
        return valorInversion;
    }

    public void setValorInversion(String valorInversion) {
        this.valorInversion = valorInversion;
    }

    public String getAuxTotales() {
        return auxTotales;
    }

    public void setAuxTotales(String auxTotales) {
        this.auxTotales = auxTotales;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipoT() {
        return tipoT;
    }

    public void setTipoT(String tipoT) {
        this.tipoT = tipoT;
    }

    public String getClaseT() {
        return claseT;
    }

    public void setClaseT(String claseT) {
        this.claseT = claseT;
    }

    public String getNovedad() {
        return novedad;
    }

    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }
}
