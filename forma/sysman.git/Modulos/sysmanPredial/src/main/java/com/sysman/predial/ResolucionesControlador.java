package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.ResolucionesControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 10/06/2016
 *
 * @author lcortes
 * @version 2, 17/07/2017. Refactorizacion de codigo para uso de dss.
 */
@ManagedBean
@ViewScoped
public class ResolucionesControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private List<Registro> listaCmbAno;

    /**
     * Creates a new instance of ResolucionesControlador
     */
    public ResolucionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESOLUCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_IGAC_RESOLUCIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCmbAno();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionesControladorUrlEnum.URL2264
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
    public void oprimirDetalle(Registro reg, int rowNum) {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "anio", "resolucion", "pais", "departamento",
                            "municipio", "fecha" };
        Object[] valores = { reg.getCampos().get("ANO").toString(),
                             reg.getCampos().get("RESOLUCION").toString(),
                             reg.getCampos().get("PAIS").toString(),
                             reg.getCampos().get("DEPARTAMENTO").toString(),
                             reg.getCampos().get("MUNICIPIO").toString(),
                             reg.getCampos().get("FECHAINGRESOSISTEMA") };
        SessionUtil.redireccionar("/resolucionessub.sysman", campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("PAIS",
                        SessionUtil.getCompaniaIngreso().getCodigoPais());
        registro.getCampos().put("DEPARTAMENTO", SessionUtil
                        .getCompaniaIngreso().getCodigoDepartamento());
        registro.getCampos().put("MUNICIPIO",
                        SessionUtil.getCompaniaIngreso().getCodigoCiudad());
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

    @Override
    public void removerCombos() {
        // Metodo que se genera a partir del bean base
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("PAIS");
        registro.getCampos()
                        .remove(GeneralParameterEnum.DEPARTAMENTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.MUNICIPIO.getName());
        registro.getCampos().remove(GeneralParameterEnum.RESOLUCION.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("REGISTRADO");
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo que es heredado del bean base
    }

    public List<Registro> getListaCmbAno() {
        return listaCmbAno;
    }

    public void setListaCmbAno(List<Registro> listaCmbAno) {
        this.listaCmbAno = listaCmbAno;
    }
}
