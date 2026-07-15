package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.services.RegistroDataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 31/05/2016
 * 
 * @author asana
 * @version 2, 13/06/2017, se implementa enum en formulario, ajusta
 * Conexion, se modifica ruta.
 * 
 * @author eamaya
 * @version 3.0, 18/07/2017, Proceso de Refactoring DSS eliminación de
 * código inutilizable
 * 
 */
@ManagedBean
@ViewScoped
public class SubavaluosreservasControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String trpranCons;
    private final String trpporCons;
    private final String trpcodCons;
    // <DECLARAR_ATRIBUTOS>
    private String codigoPredio;
    private String nomPropietario;
    private String direccionPredio;
    private String numeroOrden;
    private boolean indReserva;
    private Map<String, Object> rid;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModel listatrpcod;
    private RegistroDataModel listatrpcodE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of SubavaluosreservasControlador
     */
    public SubavaluosreservasControlador() {
        super();
        compania = SessionUtil.getCompania();
        trpranCons = "TRPRAN";
        trpporCons = "TRPPOR";
        trpcodCons = "TRPCOD";
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBAVALUOSRESERVAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
                numeroOrden = (String) parametrosEntrada.get("nroOrden");
                nomPropietario = (String) parametrosEntrada
                                .get("nomPropietario");
                direccionPredio = (String) parametrosEntrada
                                .get("direccionPredio");
                indReserva = (boolean) parametrosEntrada.get("indReserva");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(SubavaluosreservasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.IP_FACTURADOSRESERVA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatrpcod();
        cargarListatrpcodE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoPredio);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatrpcod() {
        // METODO_NO_IMPLEMENTADO
    }

    public void cargarListatrpcodE() {
        // METODO_NO_IMPLEMENTADO
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartrpcod() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPreano() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(trpcodCons, "");
        registro.getCampos().put(trpporCons, "");
        registro.getCampos().put(trpranCons, "");
        cargarListatrpcod();
        cargarListatrpcodE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartrpcodC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAvaluoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelecttrpcod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(trpcodCons,
                        registroAux.getCampos().get(trpcodCons));
        registro.getCampos().put(trpcodCons,
                        registroAux.getCampos().get(trpcodCons));
        registro.getCampos().put(trpporCons,
                        registroAux.getCampos().get(trpporCons));
        registro.getCampos().put(trpranCons,
                        registroAux.getCampos().get(trpranCons));
    }

    public void seleccionarFilatrpcodE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(trpcodCons);
        registro.getCampos().put(trpporCons,
                        registroAux.getCampos().get(trpporCons));
        registro.getCampos().put(trpranCons,
                        registroAux.getCampos().get(trpranCons));
    }

    // </METODOS_COMBOS_GRANDES>
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
        registro.getCampos().put("CODIGO", codigoPredio);
        registro.getCampos().put("NUMERO_ORDEN", numeroOrden);
        registro.getCampos().put("PREVAL", "0");
        registro.getCampos().put("SALDOCREDITO", "0");
        registro.getCampos().put("ABONOS", "0");
        registro.getCampos().put("INDHIP", "0");
        registro.getCampos().put("INDCAR", "0");
        registro.getCampos().put("INDEXE", "0");
        registro.getCampos().put("ANTERIOR_RURAL", "0");
        registro.getCampos().put("TOTAL", "0");
        registro.getCampos().put("C1", "0");
        registro.getCampos().put("C2", "0");
        registro.getCampos().put("C3", "0");
        registro.getCampos().put("C4", "0");
        registro.getCampos().put("C5", "0");
        registro.getCampos().put("C7", "0");
        registro.getCampos().put("C8", "0");
        registro.getCampos().put("C9", "0");
        registro.getCampos().put("C10", "0");
        registro.getCampos().put("C11", "0");
        registro.getCampos().put("C12", "0");
        registro.getCampos().put("C13", "0");
        registro.getCampos().put("C14", "0");
        registro.getCampos().put("C15", "0");
        registro.getCampos().put("C17", "0");
        registro.getCampos().put("C18", "0");
        registro.getCampos().put("C19", "0");
        registro.getCampos().put("C20", "0");
        registro.getCampos().put("PAGADO", "0");
        registro.getCampos().put("NOCOBRADO", "0");
        registro.getCampos().put("AVALUOANT", "0");
        registro.getCampos().put("AREAHE", "0");
        registro.getCampos().put("AREAM2", "0");
        registro.getCampos().put("TOTALABONADO", "0");
        registro.getCampos().put("VALORULTIMOABONO", "0");
        registro.getCampos().put("INDPAGO_ACPAG", "0");
        registro.getCampos().put("FINANCIAR", "0");
        registro.getCampos().put("CNT", "0");
        registro.getCampos().put("C1_CNT", "0");
        registro.getCampos().put("TOTALABONADO1", "0");
        registro.getCampos().put("VALORULTIMOABONO1", "0");
        registro.getCampos().put("TOTALABONADO2", "0");
        registro.getCampos().put("VALORULTIMOABONO2", "0");
        registro.getCampos().put("INDPAGO_CUOTAS", "0");
        registro.getCampos().put("TOTALCUOTA", "0");
        registro.getCampos().put("PAGNCUOTA", "0");
        registro.getCampos().put("TOTALCUOTA1", "0");
        registro.getCampos().put("PAGNCUOTA1", "0");
        registro.getCampos().put("TOTALCUOTA2", "0");
        registro.getCampos().put("PAGNCUOTA2", "0");
        registro.getCampos().put("C18_CNT", "0");
        registro.getCampos().put("TOTALABONO1", "0");
        registro.getCampos().put("TOTALABONO2", "0");
        registro.getCampos().put("PREANO_EXONERADO", "0");
        registro.getCampos().put("C1_CPY", "0");
        registro.getCampos().put("IND_C1CPY", "0");
        registro.getCampos().put("C3_CPY", "0");
        registro.getCampos().put("IND_C3CPY", "0");
        registro.getCampos().put("SINDESCUENTOS", "0");
        registro.getCampos().put("DESC_INTERES", "0");
        registro.getCampos().put("C1_EXE", "0");
        registro.getCampos().put("C2_EXE", "0");
        registro.getCampos().put("C3_EXE", "0");
        registro.getCampos().put("C4_EXE", "0");
        registro.getCampos().put("INDEXEOTROS", "0");
        registro.getCampos().put("PAGNCUOTA", "0");
        registro.getCampos().put("C13_EXE", "0");
        registro.getCampos().put("C14_EXE", "0");
        registro.getCampos().put("C15_EXE", "0");
        registro.getCampos().put("C16_EXE", "0");
        registro.getCampos().put("C17_EXE", "0");
        registro.getCampos().put("C18_EXE", "0");
        registro.getCampos().put("C19_EXE", "0");
        registro.getCampos().put("C20_EXE", "0");
        registro.getCampos().put("DESESPC13", "0");
        registro.getCampos().put("DESESPC2", "0");
        registro.getCampos().put("DESESPC4", "0");
        registro.getCampos().put("DESESPC14", "0");
        registro.getCampos().put("DESESPC15", "0");
        registro.getCampos().put("DESESPC16", "0");
        registro.getCampos().put("DESESPC17", "0");
        registro.getCampos().put("DESESPC18", "0");
        registro.getCampos().put("DESESPC19", "0");
        registro.getCampos().put("DESESPC20", "0");
        registro.getCampos().put("IND_PROCESOJUD", "0");
        registro.getCampos().put("AREA_CONSTRUIDA", "0");
        registro.getCampos().put("TRP_POR", "0");
        registro.getCampos().put("ES_AUTOAVALUO", "0");
        registro.getCampos().put("CONSECUTIVO", "0");
        registro.getCampos().put("PORCENTAJE_RESERVA", "0");
        registro.getCampos().put("TEMPORAL", "0");
        registro.getCampos().put("IND_EXEINT_DUP", "0");
        registro.getCampos().put("APROBADOFISC", "0");
        registro.getCampos().put("INCR_AUTOAVALUO", "0");
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
        // metodo heredado
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", rid);
        parametros.put("codigoPredio", codigoPredio);
        parametros.put("nroOrden", numeroOrden);
        parametros.put("nomPropietario", nomPropietario);
        parametros.put("direccionPredio", direccionPredio);
        parametros.put("indReserva", indReserva);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBAVALUOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar("retornoFormulario", "retorna");
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public String getDireccionPredio() {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio) {
        this.direccionPredio = direccionPredio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModel getListatrpcod() {
        return listatrpcod;
    }

    public void setListatrpcod(RegistroDataModel listatrpcod) {
        this.listatrpcod = listatrpcod;
    }

    public RegistroDataModel getListatrpcodE() {
        return listatrpcodE;
    }

    public void setListatrpcodE(RegistroDataModel listatrpcodE) {
        this.listatrpcodE = listatrpcodE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
