package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.ProgramacionpagosControladorEnum;
import com.sysman.contabilidad.enums.ProgramacionpagosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 19/05/2016
 * @modified jsforero
 * 
 * @version 2. 10/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @version 3. 21/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 */
@ManagedBean
@ViewScoped
public class ProgramacionpagosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String programadoConst;
    private final String fechaConst;
    private String anio;
    private String mes;
    private String usuario;
    private boolean signo;
    private List<Registro> listaAno;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private boolean manejaProgramacion;

    /**
     * Creates a new instance of ProgramacionpagosControlador
     */
    public ProgramacionpagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        programadoConst = "PROGRAMADO";
        fechaConst = "FECHAPROGPAGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMACIONPAGOS_CONTROLADOR.getCodigo();
            validarPermisos();

        }
        catch (Exception ex) {
            Logger.getLogger(ProgramacionpagosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        try {
            tabla = ProgramacionpagosControladorEnum.COMPROBANTE_CNT.getValue();
            buscarLlave();

            signo = "SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "PROGRAMACION DE PAGOS GENERAL",
                                            modulo,
                                            new Date(), true),
                            "NO")) ? true : false;
            
            manejaProgramacion = "SI".equals(SysmanFunciones.nvl(
                    ejbSysmanUtil.consultarParametro(
                                    compania,
                                    "MANEJA PROGRAMACION DE PAGOS CREMIL",
                                    modulo,
                                    new Date(), true),
                    "NO")) ? true : false;
            
            reasignarOrigen();
            registro = new Registro();
            cargarListaAno();
            abrirFormulario();
        }
        catch (SystemException ex) {
            Logger.getLogger(ProgramacionpagosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.name(), anio);
        parametrosListado.put(ProgramacionpagosControladorEnum.MES.getValue(),
                        mes);
        parametrosListado.put(GeneralParameterEnum.USUARIO.name(), usuario);
        
        if (manejaProgramacion) {
        	
        	   urlListado = UrlServiceUtil.getInstance()
                       .getUrlServiceByUrlByEnumID(
                                       ProgramacionpagosControladorUrlEnum.URL72116
                                       .getValue());
        	
        }else {
        	
        if (signo) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionpagosControladorUrlEnum.URL1023
                                            .getValue());

        }
        else {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionpagosControladorUrlEnum.URL6547
                                            .getValue());
        }
        
        }

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProgramacionpagosControladorUrlEnum.URL7225
                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionpagosControladorUrlEnum.URL426
                                            .getValue());
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param));
        }
        catch (SystemException ex) {
            Logger.getLogger(ComprobanteDiferidoControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
        }
    }
 
    public void cambiarAno() {
        reasignarOrigen();
    }

    public void cambiarMes() {
        reasignarOrigen();
        }

    public void cambiarPROGRAMADOC(int rowNum) {
 
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(programadoConst).equals(true)) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            fechaConst,
                            new Date());
        }
        else {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
            .put(fechaConst, null);
        }
  
    }

    public void cambiarFECHAPROGPAGOC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(fechaConst) == null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            programadoConst,
                            false);
        }
        else {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            programadoConst,
                            true);
        }
   
    }


    @Override
    public void abrirFormulario() {
 
        usuario = SessionUtil.getUser().getCodigo();
        anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        mes = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.MONTH)
                        + 1);
        reasignarOrigen();
     
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
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
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("ABONADO_SALDO");
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("TIPO");
        registro.getCampos().remove("NUMERO");
        registro.getCampos().remove("FECHA");
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("VLR_DOCUMENTO");
        registro.getCampos().remove("VLRAGIRAR");
        registro.getCampos().remove("SALDO");
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
        // Metodo heredado

    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    // <SET_GET_ATRIBUTOS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean getSigno() {
        return signo;
    }

    public void setSigno(boolean signo) {
        this.signo = signo;
    }


    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

}
