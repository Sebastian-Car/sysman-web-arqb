package com.sysman.precontractual;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.MonitoretapasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 10/12/2015
 * 
 * @version 2, 31/08/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se ajusto el redireccionar para que incluya el numero del
 * formulario.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class MonitoretapasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo en el
     * cual el usuario esta interactuando
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    private String tipoContrato;

    private String dependencia;
    private String responsable;
    private String fechaInicial;
    private String tituloFormulario;
    private RegistroDataModel listaFDependencia;
    private RegistroDataModel listaFDependenciaE;
    private RegistroDataModel listaFResponsable;
    private RegistroDataModel listaFResponsableE;
    private String auxiliar;
    private List<Registro> listaFTipo;
    private List<Registro> listaFConsecutivo;
    private List<Registro> listaFAno;
    private List<Registro> listaFEtapa;

    /**
     * Atributo que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of MonitoretapasControlador
     */
    public MonitoretapasControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cAno = GeneralParameterEnum.ANO.getName();

        try {
            // 408
            numFormulario = GeneralCodigoFormaEnum.MONITORETAPAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.TRANSACCION.getTable();
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        abrirFormulario();
        reasignarOrigen();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getUrlBeanById(
                        MonitoretapasControladorUrlEnum.URL7110.getValue());

        parametrosListado.put(cCompania, compania);
    }

    public void oprimirVerTransaccion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        try {
            int anio = Integer.parseInt(reg.getCampos().get(cAno).toString());

            String estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania,
                            anio, Integer.parseInt(modulo), 1);

            if ("E".equals(estado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3501")
                                .replace("#ANIO#", Integer.toString(anio)));

                return;
            }

            // Si el anio esta cerrado
            if ("C".equals(estado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3518")
                                .replace("#ANIO#", Integer.toString(anio)));
            }

            String nomEstado;

            switch (estado) {
            case "A":
                nomEstado = "Activo";
                break;
            case "C":
                nomEstado = "Cerrado";
                break;
            default:
                nomEstado = "E";
                break;
            }

            tipoContrato = (String) reg.getCampos().get("TIPOCONTRATO");

            String nombTipoContrato = (String) reg.getCampos().get("NOM_TIPO");

            String transaccion = reg.getCampos().get("CONSECUTIVO").toString();

            String consecutivoD = reg.getCampos().get("CONSECUTIVODETALLE")
                            .toString();

            fechaInicial = SysmanFunciones.convertirAFechaCadena(
                            (Date) reg.getCampos().get("FECHAINICIAL"),
                            "dd/MM/yyyy");

            String observacion = SysmanFunciones
                            .nvl(reg.getCampos().get("OBSERVACION"), "")
                            .toString();

            String estadoProceso = (String) reg.getCampos().get("ESTADO");

            String estudioPrevio = SysmanFunciones
                            .nvl(reg.getCampos().get("ESTUDIOPREVIO"), "")
                            .toString();

            String condicionTr = SysmanFunciones.concatenar(
                            " AND TRANSACCION.CONSECUTIVO IN('", transaccion,
                            "')");

            boolean desdeMonitor = true;

            Map<String, Object> param = new HashMap<>();
            param.put("rid", reg.getLlave());
            param.put("tipoContrato", tipoContrato);
            param.put("nomTipoContrato", nombTipoContrato);
            param.put("anio", anio);
            param.put("transaccion", transaccion);
            param.put("consecutivo", consecutivoD);
            param.put("fechaIni", fechaInicial);
            param.put("estadoProceso", estadoProceso);
            param.put("observacion", observacion);
            param.put("condicion", condicionTr);
            param.put("estadoVigencia", estado);
            param.put("nomEstado", nomEstado);
            param.put("estudioPrevio", estudioPrevio);
            param.put("menuActual", SessionUtil.getMenuActual());
            param.put("desdeMonitor", String.valueOf(desdeMonitor));

            Direccionador dir = new Direccionador();
            dir.setParametros(param);
            dir.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                            .getCodigo()));

            SessionUtil.redireccionarForma(dir, modulo);
        }
        catch (ParseException | NumberFormatException | SystemException ex) {
            Logger.getLogger(MonitoretapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaFTipo() {
        return listaFTipo;
    }

    public void setListaFTipo(List<Registro> listaFTipo) {
        this.listaFTipo = listaFTipo;
    }

    public List<Registro> getListaFConsecutivo() {
        return listaFConsecutivo;
    }

    public void setListaFConsecutivo(List<Registro> listaFConsecutivo) {
        this.listaFConsecutivo = listaFConsecutivo;
    }

    public List<Registro> getListaFAno() {
        return listaFAno;
    }

    public void setListaFAno(List<Registro> listaFAno) {
        this.listaFAno = listaFAno;
    }

    public List<Registro> getListaFEtapa() {
        return listaFEtapa;
    }

    public void setListaFEtapa(List<Registro> listaFEtapa) {
        this.listaFEtapa = listaFEtapa;
    }

    public RegistroDataModel getListaFDependencia() {
        return listaFDependencia;
    }

    public void setListaFDependencia(RegistroDataModel listaFDependencia) {
        this.listaFDependencia = listaFDependencia;
    }

    public RegistroDataModel getListaFDependenciaE() {
        return listaFDependenciaE;
    }

    public void setListaFDependenciaE(RegistroDataModel listaFDependenciaE) {
        this.listaFDependenciaE = listaFDependenciaE;
    }

    public RegistroDataModel getListaFResponsable() {
        return listaFResponsable;
    }

    public void setListaFResponsable(RegistroDataModel listaFResponsable) {
        this.listaFResponsable = listaFResponsable;
    }

    public RegistroDataModel getListaFResponsableE() {
        return listaFResponsableE;
    }

    public void setListaFResponsableE(RegistroDataModel listaFResponsableE) {
        this.listaFResponsableE = listaFResponsableE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }
}
