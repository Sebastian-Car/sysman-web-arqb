package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.UsuariosinmedidormicroControladorUrlEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 23/08/2016
 *
 * @author jguerrero
 * @version 2.0, 21/06/2017 Se le realiza refactoring a la clase.
 */

@ManagedBean
@ViewScoped
public class UsuariosinmedidormicroControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private StreamedContent archivoDescarga;
    private String ciclo;
    private String titulo;
    private String medidor;
    private String auxiliar;
    private RegistroDataModelImpl listaMEDIDOR;
    private RegistroDataModelImpl listaMEDIDORE;
    private Object codigoRuta;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    /**
     * Creates a new instance of UsuariosinmedidormicroControlador
     */
    public UsuariosinmedidormicroControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.USUARIOSINMEDIDORMICRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                titulo = "CICLO: " + ciclo;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(UsuariosinmedidormicroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        tabla = GenericUrlEnum.SP_USUARIO.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosinmedidormicroControladorUrlEnum.URL0001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosinmedidormicroControladorUrlEnum.URL0002
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMEDIDOR() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosinmedidormicroControladorUrlEnum.URL4925
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMEDIDOR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaMEDIDORE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariosinmedidormicroControladorUrlEnum.URL4925
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMEDIDORE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirInforme() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte("001047Usuariosinmedidor");
        // </CODIGO_DESARROLLADO>
    }

    private void getReporte(String reporte) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("ciclo", ciclo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_CICLO", ciclo);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (FileNotFoundException ex) {
            StringBuilder builder = new StringBuilder();

            builder.append(idioma.getString("MSM_INFORME_NO_EXISTE"))
                            .append(" ").append(ex.getMessage())
                            .append(idioma.getString(" ")).append(reporte);

            JsfUtil.agregarMensajeInformativo(builder.toString());

            Logger.getLogger(UsuariosinmedidormicroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(UsuariosinmedidormicroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMEDIDORC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("MEDIDOR",
                        auxiliar);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
    }

    public void onRowSelectMEDIDOR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        medidor = registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
    }

    public void onRowSelectMEDIDORE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarListaMEDIDOR();
        cargarListaMEDIDORE();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        codigoRuta = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        boolean rta = false;
        // <CODIGO_DESARROLLADO>
        try {
            rta = ejbServiciosPublicosOcho.actualizarMedidor(compania, auxiliar,
                            codigoRuta.toString(), Integer.parseInt(ciclo),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;

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
        // heredado del bean base
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getMedidor() {
        return medidor;
    }

    public void setMedidor(String medidor) {
        this.medidor = medidor;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaMEDIDOR() {
        return listaMEDIDOR;
    }

    public void setListaMEDIDOR(RegistroDataModelImpl listaMEDIDOR) {
        this.listaMEDIDOR = listaMEDIDOR;
    }

    public RegistroDataModelImpl getListaMEDIDORE() {
        return listaMEDIDORE;
    }

    public void setListaMEDIDORE(RegistroDataModelImpl listaMEDIDORE) {
        this.listaMEDIDORE = listaMEDIDORE;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
