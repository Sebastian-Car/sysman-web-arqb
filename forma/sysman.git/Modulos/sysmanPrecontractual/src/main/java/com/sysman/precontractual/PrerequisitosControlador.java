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
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.PrerequisitosControladorEnum;
import com.sysman.precontractual.enums.PrerequisitosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 24/11/2015
 * 
 * @version 2, 01/08/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se ajusto el redireccinar para que incluya el numero del
 * formulario.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class PrerequisitosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CLASENOMBRE</code>
     */
    private final String cClaseNombre;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>#CAMPO#</code>
     */
    private final String cSharpCampoSharp;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR1</code>
     */
    private final String cValorUno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR2</code>
     */
    private final String cValorDos;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRETIPO</code>
     */
    private final String cNombreTipo;

    private boolean bloqueaValor1;
    private boolean bloqueaValor2;

    /**
     * Atributo que contiene la mascara de los valores que pueden ser
     * ingresados en los campos: <code>VALOR1</code> y
     * <code>VALOR2</code>.
     */
    private String mascaraValor;

    /** Atributo que contiene el tipo de prerequisito seleccionado. */
    private String tipo;

    /**
     * Atributo auxliar el cual es asignado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private List<Registro> listaCLASE;

    /**
     * Creates a new instance of PrerequisitosControlador
     */
    public PrerequisitosControlador() {
        super();

        // 374
        numFormulario = GeneralCodigoFormaEnum.PREREQUISITOS_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cNombreTipo = PrerequisitosControladorEnum.NOMBRETIPO.getValue();
        cClaseNombre = PrerequisitosControladorEnum.CLASENOMBRE.getValue();

        cSharpCampoSharp = PrerequisitosControladorEnum.SHARP_CAMPO_SHARP
                        .getValue();

        cValorUno = PrerequisitosControladorEnum.VALOR_UNO.getValue();
        cValorDos = PrerequisitosControladorEnum.VALOR_DOS.getValue();

        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PREREQUISITOS;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaCLASE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    public List<Registro> getListaCLASE() {
        return listaCLASE;
    }

    public void setListaCLASE(List<Registro> listaCLASE) {
        this.listaCLASE = listaCLASE;
    }

    public boolean isBloqueaValor1() {
        return bloqueaValor1;
    }

    public void setBloqueaValor1(boolean bloqueaValor1) {
        this.bloqueaValor1 = bloqueaValor1;
    }

    public boolean isBloqueaValor2() {
        return bloqueaValor2;
    }

    public void setBloqueaValor2(boolean bloqueaValor2) {
        this.bloqueaValor2 = bloqueaValor2;
    }

    public void cargarListaCLASE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaCLASE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrerequisitosControladorUrlEnum.URL3561
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CAMBIAR>
    /** Metodo ejecutado al cambiar el valor del combo Tipo. */
    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        tipo = SysmanFunciones.nvl(registro.getCampos().get("TIPO"), "")
                        .toString();

        asignarMascara();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo en la fila
     * seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        tipo = SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum)
                                        .getCampos().get("TIPO"), "")
                        .toString();

        asignarMascara();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    public void oprimirClasedePrerequisito(ActionEvent ac) {
        Map<String, Object> param = new HashMap<>();

        Direccionador dir = new Direccionador();
        dir.setParametros(param);

        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCLASEPRES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(dir, modulo);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        tipo = SysmanFunciones.nvl(registro.getCampos().get("TIPO"), "")
                        .toString();

        if (SysmanFunciones.validarVariableVacio(tipo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3635"));
            return false;
        }

        boolean key = verificarFormatoValor(cValorUno)
            && verificarFormatoValor(cValorDos) && verificarRangoValores();

        registro.getCampos().remove(cNombreTipo);
        registro.getCampos().remove(cClaseNombre);

        return key;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario.
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        tipo = SysmanFunciones.nvl(registro.getCampos().get("TIPO"), "")
                        .toString();

        asignarMascara();
    }

    /**
     * Verifica que el valor ingresado en el rango inferior sea menor
     * que el ingresado en el rango superior. Aplica para numeros y
     * fechas.
     * 
     * @return <strong>true</strong>: Si el rango inferior es menor
     * que el superior.
     */
    private boolean verificarRangoValores() {
        switch (tipo) {
        case "3":
            return verificarNumeroInferior();
        case "4":
            return verificarFechaInferior();
        default:
            return true;
        }
    }

    /**
     * Verifica si la fecha del rango inferior es menor a la ingresada
     * en el rango superior.
     * 
     * @return <strong>true</strong>: Si la fecha del rango inferior
     * es menor de la del rango superior.
     */
    private boolean verificarFechaInferior() {
        try {
            Date fechaA = SysmanFunciones.convertirAFecha(
                            registro.getCampos().get(cValorUno).toString());

            Date fechaB = SysmanFunciones.convertirAFecha(
                            registro.getCampos().get(cValorDos).toString());

            if (SysmanFunciones.comparaFechas(fechaB, fechaA)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3637"));

                return false;
            }
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * Verifica si el numero del rango inferior es menor al ingresado
     * en el rango superior.
     * 
     * @return <strong>true</strong>: Si el numero del rango inferior
     * es menor al del rango superior.
     */
    private boolean verificarNumeroInferior() {
        double numeroA = Double.parseDouble(
                        registro.getCampos().get(cValorUno).toString());

        double numeroB = Double.parseDouble(
                        registro.getCampos().get(cValorDos).toString());

        if (numeroA > numeroB) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3636"));

            return false;
        }

        return true;
    }

    private boolean verificarFormatoValor(String campo) {
        String valor = (String) registro.getCampos().get(campo);
        boolean verificar = true;

        try {
            switch (tipo) {
            case "3":
                verificar = validarValorDouble(campo, valor);
                break;
            case "4":
                verificar = validarValorFecha(campo, valor.replace("-", "/"));
                break;
            default:
                break;
            }
        }
        catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3638")
                            .replace(cSharpCampoSharp, campo));

            return false;
        }

        return verificar;
    }

    /**
     * Valida que el valor ingresado en el parametro {@code valor} sea
     * un numero tipo flotante.
     * 
     * @param valor
     * : Valor ingresado en el campo.
     * @param campo
     * : Nombre del campo donde se ingreso el valor.
     * @return true : Si el valor ingresado en el campo corresponde
     * con el tipo de valor.
     */
    private boolean validarValorDouble(String campo, String valor) {
        try {
            double mValor = Double.parseDouble(valor.contains(",")
                ? valor.replace(",", ".") : valor);

            registro.getCampos().put(campo, mValor);
        }
        catch (NumberFormatException nfex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3639")
                            .replace(cSharpCampoSharp, campo));
            return false;
        }

        return true;
    }

    /**
     * Valida que el valor ingresado en el campo sea una fecha con el
     * formato: dd/mm/yyyy.
     * 
     * @param campo
     * : Nombre del campo.
     * @param valor
     * : Valor ingresado en el campo.
     * @return true : Si el valor ingresado en el campo es una fecha
     * con el formato dd/mm/yyyy.
     */
    private boolean validarValorFecha(String campo, String valor) {
        if (!SysmanFunciones.validarFecha(valor)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3640")
                            .replace(cSharpCampoSharp, campo));

            return false;
        }

        registro.getCampos().put(campo, valor);

        return true;
    }

    /**
     * Asigna la mascara que deben tener los campos rango inferior y
     * superior segun el tipo seleccionado.
     */
    private void asignarMascara() {
        mascaraValor = "4".equals(tipo) ? "99/99/9999" : "";
    }

    public String getMascaraValor() {
        return mascaraValor;
    }

    public void setMascaraValor(String mascaraValor) {
        this.mascaraValor = mascaraValor;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

}
