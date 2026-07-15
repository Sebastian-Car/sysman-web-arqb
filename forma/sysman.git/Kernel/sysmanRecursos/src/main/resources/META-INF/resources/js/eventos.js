/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function cambiarId(objeto) {
    var longitud = objeto.value.length;
    if (longitud <= 6 && longitud != 1 && longitud % 2 != 0) {
        PF("CB1").selectValue('');
        PF("CB2").selectValue('');
        objeto.value = '';
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'La longitud de la cuenta NO es correcta.',
            'severity': 'error'});
        return false;
    }
}


function selecciononarCombosMesesMovimientos(idSeleccionado) { 
    var mesIni = parseInt(PF('CB11').input[0].value);
    var mesFin = parseInt(PF('CB12').input[0].value);
    if (isNaN(mesIni) || isNaN(mesFin)) {

        PF(idSeleccionado).revert();
        PF(idSeleccionado).selectValue(PF(idSeleccionado).input[0].value);
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe selecionar el mes.',
            'severity': 'error'});
        return false;
    }

    else if (mesIni > mesFin) {
        PF(idSeleccionado).revert();
        PF(idSeleccionado).selectValue(PF(idSeleccionado).input[0].value);
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'El mes final debe ser posterior al inicial.',
            'severity': 'error'});
        return false;
    }

    return true;
}

function validarDetallePorConcepto() {
    var concepto = document.getElementById('FR80_nuevo:CB285').getAttribute('value');
    var tipoEmpleado = PF('CB286').getSelectedValue();
    var todos = PF('CK95').isChecked();
    var result = true;
    if (concepto == null) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe seleccionar un concepto.',
            'severity': 'error'});
        result = false;
    } else if (tipoEmpleado == '' && todos == false) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe seleccionar un tipo de empleado.',
            'severity': 'error'});
        result = false;
    }
    return result;
}
function enviarConsultaCCto() {
    var mesIni = PF('CB72').input[0].value;
    var mesFin = PF('CB73').input[0].value;
    var anio = PF('CB74').input[0].value;
    var rta = true;
    if (anio == "") {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe seleccionar el año a consultar.',
            'severity': 'error'});
        rta = false;
    }

    if (mesIni == "") {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe seleccionar el mes inicial.',
            'severity': 'error'});
        rta = false;
    }

    if (mesFin == "") {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Debe seleccionar el mes final.',
            'severity': 'error'});
        rta = false;
    }


    if (parseInt(mesIni) > parseInt(mesFin)) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'El mes final no puede ser inferior al mes inicial.',
            'severity': 'error'});
        rta = false;
    }

    return rta;
}
function validarCambioRiesgo() {
    var fondo = PF('CB94').input[0].value;
    var fecha = PF('CP563').input[0].value;
    var rta = true;
    if (fondo == "") {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para realizar el proceso de actualización falta seleccionar el Fondo de Riesgos',
            'severity': 'error'});
        rta = false;
    }
    if (fecha == "") {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para realizar el proceso de actualización falta seleccionar la fecha',
            'severity': 'error'});
        rta = false;
    }
    return rta;
}

function visualizarCamposComision() {
    if (PF('CB124').getSelectedValue() != 6) {
        $('#FR47_nuevo\\:TS5\\:LB631').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:LB627').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:LB628').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:LB629').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:CP620').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:CP621').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:CP622').css("display", "none");
        PF('CP620').input[0].value = null;
        PF('CP621').input[0].value = null;
        PF('CP622').inputExternal[0].value = null;
    } else {
        $('#FR47_nuevo\\:TS5\\:LB631').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:LB627').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:LB628').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:LB629').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:CP620').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:CP621').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:CP622').css("display", "block"); 
    }
    return true; 
}

function validarFechasComision() {
    if (PF('CP620').input[0].value != "" && PF('CP621').input[0].value != "") {
        if (PF('CP620').getDate() > PF('CP621').getDate()) {
            PF('CP621').input[0].value = null;
            PF('CP622').inputExternal[0].value = 0;
            PF('alert').renderMessage({'summary': 'Mensaje de validación',
                'detail': 'La fecha de final de comisión no puede ser menor a la fecha de inicio. Por favor verificar los datos ingresados',
                'severity': 'error'});
            return false;
        }
    }
    return true;
}



function validarFechas(fechaInicial, fechaFinal) {
    if (PF(fechaInicial).input[0].value != "" && PF(fechaFinal).input[0].value != "") {
        if (PF(fechaInicial).getDate() > PF(fechaFinal).getDate()) {
            PF(fechaFinal).input[0].value = null;
            PF('alert').renderMessage({'summary': 'Mensaje de Validación',
                'detail': 'La Fecha Inicial no puede ser mayor que la Fecha Final',
                'severity': 'error'});
            return false;
        }
    }
    return true;

}

function validarFechasMsj(fechaInicial, fechaFinal, mensaje) {
    if (PF(fechaInicial).input[0].value != "" && PF(fechaFinal).input[0].value != "") {
        if (PF(fechaInicial).getDate() > PF(fechaFinal).getDate()) {
            PF(fechaFinal).input[0].value = null;
            PF('alert').renderMessage({'summary': 'Mensaje de Validación',
                'detail': mensaje,
                'severity': 'error'});
            return false;
        }
    }
    return true;

}




function validarFactorRH() {
    var valor = PF('CP601').jq[0].value;
    if ((valor != "-") && (valor != "+")) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para el factor RH ingresar el simbolo + ó -',
            'severity': 'error'});
        PF('CP601').jq[0].value = null;
        return false;
    }
    return true;
}

function validarCartaProrroga() {
    var meses = parseInt(PF('CP608').inputExternal[0].value);
    if (PF('CP611').jq[0].value == "" || PF('CP611').jq[0].value == null) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para generar la carta de prorroga se requiere ingresar el número de resolución con la cual se aprobó la prorroga.',
            'severity': 'error'});
        return false;
    }
    if (PF('CP612').input[0].value == "" || PF('CP612').input[0].value == null) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para generar la carta de prorroga se requiere ingresar la fecha de la resolución con la cual se aprobó la prorroga.', 
            'severity': 'error'});
        return false;
    }
    if (PF('CP610').input[0].value == "" || PF('CP610').input[0].value == null) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para generar la carta de prorroga se requiere ingresar la fecha de finalización del contrato anterior.',
            'severity': 'error'});
        return false;
    }

    if (PF('CP607').input[0].value == "" || PF('CP607').input[0].value == null) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Para generar la carta de prorroga se requiere ingresar la fecha de finalización del contrato.',
            'severity': 'error'});
        return false;
    }
    if (meses <= 0) {
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Los meses de prorroga deben ser mayores a cero',
            'severity': 'error'});
        return false;
    }
    return true;

}


function validarTipoVinculacion() {
    var valor = "";
    if ((PF('CB118').getSelectedValue() == 2) || (PF('CB118').getSelectedValue() == 3)
            || (PF('CB118').getSelectedValue() == 4) || (PF('CB118').getSelectedValue() == 5)
            || (PF('CB118').getSelectedValue() == 6) || (PF('CB118').getSelectedValue() == 7)
            || (PF('CB109').getSelectedValue() == '99')) {
        valor = "block";
        PF('alert').renderMessage({'summary': 'Mensaje de validación',
            'detail': 'Ha seleccionado un tipo de vinculación en la cual se requiere ingresar los datos de la sección inferior correspondiente a los Datos del Causante',
            'severity': 'info'});
    } else {
        valor = "none";
        PF('CP659').jq[0].value = null;
        PF('CP660').jq[0].value = null;
        PF('CP661').jq[0].value = null;
        PF('CP662').jq[0].value = null;
        PF('CP663').jq[0].value = null;
        PF('CB155').input[0].value = null;
    }

    $('#FR47_nuevo\\:TS5\\:LB694').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB677').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB678').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB679').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB680').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB681').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:LB682').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP659').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP660').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP661').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP662').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP663').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CB155').css("display", valor);
    //$('#FR47_nuevo\\:TS5\\:LB555').css("display", valor);
    //$('#FR47_nuevo\\:TS5\\:CP580').css("display", valor);
    return true;
}

function validarPensionados() {
    var valor = "";

    if (PF('CB109').input[0].value == '99') {
        valor = "none";

        $('#FR47_nuevo\\:TS5\\:LB548').html("Inicio Pensión :");
        $('#FR47_nuevo\\:TS5\\:LB611').html("Fecha Fallecido ó Retiro :");
        $('#FR47_nuevo\\:TS5\\:LB11652').css("display", "none");
        $('#FR47_nuevo\\:TS5\\:CP12800').css("display", "none");
        PF('TS5').enable(8);
    } else {
        valor = "block";

        $('#FR47_nuevo\\:TS5\\:LB548').html("Fecha de Ingreso :");
        $('#FR47_nuevo\\:TS5\\:LB611').html("Fecha de Retiro :");
        $('#FR47_nuevo\\:TS5\\:LB11652').css("display", "block");
        $('#FR47_nuevo\\:TS5\\:CP12800').css("display", "block");
        PF('TS5').disable(8);
    }
//dias interrupcion
    $('#FR47_nuevo\\:TS5\\:LB550').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP576').css("display", valor);
//categoria
    $('#FR47_nuevo\\:TS5\\:LB554').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CB108').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:BTCB108').css("display", valor);
//sueldo
    $('#FR47_nuevo\\:TS5\\:LB557').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP581').css("display", valor);

//fecha sueldo
    $('#FR47_nuevo\\:TS5\\:LB558').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP582').css("display", valor);
//calzado
    $('#FR47_nuevo\\:TS5\\:LB623').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP616').css("display", valor);
//chaqueta
    $('#FR47_nuevo\\:TS5\\:LB624').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP617').css("display", valor);
//pantalon
    $('#FR47_nuevo\\:TS5\\:LB625').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP618').css("display", valor);
//camisa
    $('#FR47_nuevo\\:TS5\\:LB626').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP619').css("display", valor);
//terminacion contrato
    $('#FR47_nuevo\\:TS5\\:LB612').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP607').css("display", valor);
//ingreso distrito
    $('#FR47_nuevo\\:TS5\\:LB630').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:CP613').css("display", valor);
//retencion
//    $('#FR47_nuevo\\:TS5\\:LB586').css("display", valor);
//    $('#FR47_nuevo\\:TS5\\:CB123').css("display", valor);
//    $('#FR47_nuevo\\:TS5\\:LB587').css("display", valor);
//    $('#FR47_nuevo\\:TS5\\:CP592').css("display", valor);
//    $('#FR47_nuevo\\:TS5\\:LB588').css("display", valor);
//    $('#FR47_nuevo\\:TS5\\:CP593').css("display", valor);
    $('#FR47_nuevo\\:TS5\\:BT61').css("display", valor);
//valor contrato
    //   $('#FR47_nuevo\\:TS5\\:LB555').css("display", valor);
    //   $('#FR47_nuevo\\:TS5\\:CP580').css("display", valor);

    return true;


}

function validarInformeNomina() {
    var validado = true;
    var radio1 = document.getElementById('FR55_nuevo:RDOP6:0');
    var radio3 = document.getElementById('FR55_nuevo:RDOP6:2');
    if (radio1.getAttribute('checked') == 'checked') {

        var idEmpleado = document.getElementById('FR55_nuevo:CB182').getAttribute('value');
        if (idEmpleado == null) {
            PF('alert').renderMessage({'summary': 'Mensaje de validación',
                'detail': 'Seleccione del cuadro de lista el Codigo del Empleado',
                'severity': 'warn'});
            validado = false;
        }
    }

    else if (radio3.getAttribute('checked') == 'checked') { 

        var centroCosto = document.getElementById('FR55_nuevo:CB183').getAttribute('value');
        if (centroCosto == null) {
            PF('alert').renderMessage({'summary': 'Mensaje de validación',
                'detail': 'Seleccione del cuadro de lista el Codigo del Centro de Costo',
                'severity': 'warn'});
            validado = false;
        }
    }

    return validado;
}

function validarPresentarRerpoteKardex() {
    var validado = true;
    validado = validado && validarFechaDesdeHastaRA();
    var radio1 = document.getElementById('FR72_nuevo:RDOP12:1');
    if (radio1.getAttribute('checked') == 'checked') {

        validado = validado && validarSeleccionoEmpleado();
        //}
    } else {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Solo válido para un empleado, seleccione del cuadro de lista el Empleado',
            'severity': 'warn'});
        validado = false;
    }
    return validado;
}

function validarPresentarReporteAcumulado() {
    var validado = true;
    validado = validado && validarFechaDesdeHastaRA();
    var radio1 = document.getElementById('FR72_nuevo:RDOP12:1');
    if (radio1.getAttribute('checked') == 'checked') {
        validado = validado && validarSeleccionoEmpleado();
    }
    return validado;
}

function validarFechaDesdeHastaRA() {
    var validado = true;
    var pad = "00";
    var ano1 = PF('CB244').input[0].value;
    var ano2 = PF('CB245').input[0].value;
    var mes1 = PF('CB246').input[0].value;
    var mes2 = PF('CB247').input[0].value;
    var periodo1 = PF('CB248').input[0].value;
    var periodo2 = PF('CB249').input[0].value;
    var proceso = PF('CB250').input[0].value;

    if (periodo1 == "" || periodo2 == "" || ano1 == "" || ano2 == "" || mes1 == "" || mes2 == "" || proceso == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Se deben ingresar todos los parámetros',
            'severity': 'warn'});
        return false;
    }

    if (ano1 + (pad + mes1).slice(-pad.length) + (pad + periodo1).slice(-pad.length) > ano2 + (pad + mes2).slice(-pad.length) + (pad + periodo2).slice(-pad.length)) {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'El Rango Inferior debe ser menor que el Rango Superior',
            'severity': 'warn'});
        return false;
    }


    return validado;
}

function validarFechaDesdeHasta(A1, M1, P1, A2, M2, P2) {
    var validado = true;
    var pad = "00";
    var ano1 = PF(A1).input[0].value;
    var ano2 = PF(A2).input[0].value;
    var mes1 = PF(M1).input[0].value;
    var mes2 = PF(M2).input[0].value;
    var periodo1 = PF(P1).input[0].value;
    var periodo2 = PF(P2).input[0].value;



    if (periodo1 == "" || periodo2 == "" || ano1 == "" || ano2 == "" || mes1 == "" || mes2 == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Se deben ingresar todos los parámetros',
            'severity': 'warn'});
        return false;
    }

    if (ano1 + (pad + PF(M1).input[0].value).slice(-pad.length) + (pad + PF(P1).input[0].value).slice(-pad.length) > ano2 + (pad + PF(M2).input[0].value).slice(-pad.length) + (pad + PF(P2).input[0].value).slice(-pad.length)) {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'El Rango Inferior debe ser menor que el Rango Superior',
            'severity': 'warn'});
        return false;
    }
    return validado;
}



function validarSeleccionoEmpleado() {
    var validado = true;
    var empleado = document.getElementById("FR72_nuevo:CB251").getAttribute('value');
    if (empleado == null || empleado == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Seleccione del cuadro de lista el Empleado',
            'severity': 'warn'});
        validado = false;
    } else {
        validado = true;
    }
    return validado;
}

function validarSeleccionarCombo(combo, mensaje) {
    var validado = true;

    var comboText = PF(combo).input[0].value;
    if (comboText == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': mensaje,
            'severity': 'warn'});
        validado = false;
    } else {
        validado = true;
    }

    return validado;
}

function validarSeleccionarComboGrande(combo) {
    var validado = true;
    var empleado = document.getElementById(combo).value;
    if (empleado == "") {
        validado = false;
    } else {
        validado = true;
    }

    return validado;
}

function validarSeleccionarComboGrandeM(combo, mensaje) {
    var validado = true;
    var empleado = document.getElementById(combo).value;
    if (empleado == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': mensaje,
            'severity': 'warn'});
        validado = false;
    } else {
        validado = true;
    }

    return validado;
}



function validarPAMP(Pr, A, M, P) {
    var validado = true;
    var proceso = PF(Pr).input[0].value;
    var anio = PF(A).input[0].value;
    var mes = PF(M).input[0].value;
    var periodo = PF(P).input[0].value;
    if (proceso == "" || anio == "" || mes == "" || periodo == "") {
        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Faltan parámetros, seleccione el todos los datos en las listas desplegables',
            'severity': 'warn'});
        validado = validado && false;
    }

    return validado;
}

function validarPresentarReporteDane(anio1, mes1, periodo1, anio2, mes2, periodo2, nivel, checkNivel) {
    var validado = true;
    validado = validado && validarFechaDesdeHasta(anio1, mes1, periodo1, anio2, mes2, periodo2);

    var checkNivel = document.getElementById('FR100_nuevo:CK105_input');
    //var checkNivel = PF('CK105').input[0].value;

    if (checkNivel.getAttribute('checked') == 'checked' && !validarSeleccionarComboGrande("FR100_nuevo:CB342_input")) {

        PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
            'detail': 'Debe seleccionar el nivel o quitar el indicador de nivel',
            'severity': 'warn'});
        validado = false;
    }
    return validado;
}


function limpiarCamposObservacionesPlanPrimaDic() {
    PF('CP3522').jq[0].value = '';
}

function cambiarVisibilidadOrdenadorCompCnt(){
	window.parent.$('#FR560_nuevo\\:BT2136').click();
} 

function posicionModalArriba(id, top){
	var intento = window.setInterval(function(){
		var dialog = document.getElementById(id);
		
		if (dialog!=null) {
			console.log("subir");
			$(dialog).animate({'top': top},300, "easeInCubic");
			
			var pos = $(dialog).css('top');
			console.log("posicion:", pos);
			if (pos==top) {
				console.log("limpia");
				window.clearInterval(intento);
			}
		}
	}, 500);
}

function filaSeleccionada(e){
	var boton = $(document.getElementById(e.source));
	var fila = $(boton).parent().parent();
	var tableBody = $(fila).parent();
	$(tableBody).find("tr").removeClass("filaSeleccionada");	
	$(fila).addClass("filaSeleccionada");	
	$(document.getElementById('FR1116_nuevo:LB26618')).addClass("etiquetaSeccion");
	$(document.getElementById('FR1116_nuevo:LB26618')).css('border', '#AAAAAA solid 1px');
}

function filaSeleccionadaActivarRegistro(e){
	var tabla = $(document.getElementById(e.source));
	$(tabla).find(".ui-datatable-scrollable-body").find("tr").removeClass("filaSeleccionada");		
	$(document.getElementById('FR1116_nuevo:LB26618')).addClass("etiquetaSeccion");
	$(document.getElementById('FR1116_nuevo:LB26618')).css('border', '#AAAAAA solid 1px');
}

function vacio() {

}

setInterval(function(){
	var altura = $("#FR1771_nuevo\\:PLSCB6278.subirPanel").css("top");
	if (altura!="30px"){
		$("#FR1771_nuevo\\:PLSCB6278.subirPanel").animate({top:"30px"},500);
	}
},500)

setInterval(function(){
	var altura = $("#FR1771_nuevo\\:PLSCB5897.subirPanelR").css("top");
		if (altura!="30px"){
		$("#FR1771_nuevo\\:PLSCB5897.subirPanelR").animate({top:"30px"},500);
	}
},500)