var anchoModal = 0;
var contador = 0;

function asignarFileUnico() {
	$(".funico input[type=file]").on("change", function () {
		if ($('.ui-fileupload-content li')[0] != undefined) {
			var menUno = $('.ui-fileupload-content li')[0].children[0].innerHTML;
			var menDos = $('.ui-fileupload-content li')[0].children[1].innerHTML;
			var wvar = this.id;
			wvar = wvar.substring(wvar.lastIndexOf(":") + 1, wvar.lastIndexOf("_"));
			PF(wvar).clearMessages();
			menUno += ' ' + menDos;
			mostrarMensajeError(menUno);
		}
	});
}
function asignarEventoContGrande() {
	$('.ui-overlaypanel-content').on("keyup", function (event) {
		if (event.keyCode == 27) {
			var idPl = this.offsetParent.id;
			idPl = idPl.substring(idPl.lastIndexOf(":") + 1, idPl.length);
			PF(idPl).hide();
			return false;
		}
	});
}


function cargaTab() {

	// (vmolano - 21/06/2016): código para evitar que se redireccione atras al
	// presionar BACKSPACE.
	$(document).on("keydown",function(e){
		if(e.which===8 && !$(e.target).is("input:not([readonly]),textarea:not([readonly])")){
			e.preventDefault();
		}
	});

	$('body').on('keydown', 'input', function (e) {
		if (e.keyCode === 13) {
			var self = $(this);
			var form = self.parents('form:eq(0)');
			var focusable;
			var next;
			var claseFiltro = 'ui-column-filter';
			var indice;
			var clase = '';
			focusable = form.find('input,a,select,button,textarea').filter(':visible:enabled');
			indice = self.attr('tabindex');
			indice++;
			clase = self[0].className;
			if (clase.indexOf(claseFiltro) > -1) {
				focusable = focusable.filter('.ui-column-filter');
				next = focusable.eq(focusable.index(this) + 1);
				if (next.length) {
					next.focus();
				} else {
					focusable[0].focus();
				}
			} else {
				var cont = 0;
				var valid = false;
				if (isNaN(indice)) {
					return false;
				}
				while (focusable.length > cont) {
					next = $('[tabindex=' + indice + ']').not('span .ui-paginator-page,.ui-paginator-next,.ui-paginator-last');
					if (next.length) {
						for (var i = 0; i < next.length; i++) {
							if (!next[i].disabled && !next[i].hidden && $(next[i]).is(':visible')) {
								next.focus();
								valid = true;
								break;
							}
						}
					}
					if (valid) {
						break;
					}
					indice++;
					cont++;
					if (cont === focusable.length && !next.length) {
						indice = 0;
						cont = 0;
					}
				}
			}
			return false;
		}
	});
}

function onfocusCBG(temp) {
	ind = false;
	temp = temp.replace('CB', 'BTCB');
	temp = temp.replace(/:/g, '\\:');
	$('#' + temp).focus();
	ind = true
}

function onfocusBTCBG(temp) {
	temp = temp.replace('BTCB', 'CB');
	temp = temp.replace(/:/g, '\\:');
	$('#' + temp).addClass('ui-state-focus');
}

function onblurBTCBG(temp) {
	temp = temp.replace('BTCB', 'CB');
	temp = temp.replace(/:/g, '\\:');
	$('#' + temp).blur();
}



function ocultarEditor() {
	$('.ui-row-editor span.ui-icon-pencil').each(function () {
		$(this).css('visibility', 'hidden');
	});
}

function mostarEditor() {
	$('.ui-row-editor span.ui-icon-pencil').each(function () {
		$(this).css('visibility', 'visible');
	});
}


function cargarIframe() {
	var documento = $(window.parent.document);
	var iframe = $(documento).find('iframe');
	var padre = $(iframe[0].parentNode);
	var abuelo = $(iframe[0].parentNode.parentElement);
	if (abuelo.hasClass('ui-overlay-visible')) {
		padre.addClass('overFlowVisible');
		padre.css('height', padre[0].offsetHeight + 'px');
		abuelo.addClass('overFlowVisible');
		abuelo.css('height', abuelo[0].offsetHeight + 'px');
		iframe.addClass('heightIframe');
		
		setInterval(function() {
			
			iframe=$(documento).find('iframe')
			
			var tituloUno = $(iframe).contents().find(".tituloModal").html();
			
			tituloUno= tituloUno?tituloUno:$(iframe).contents().find("iframe").contents().find("title").html();
			
			$(documento).find(".ui-dialog-title").html(tituloUno);
	
		} , 1000);
		
		
	} else {
		if ($(iframe[0]).attr("id")!= "imagepgframe") {
			setTimeout(cargarIframe, 200);		
		}
	}

}

function cargarModalIframe(){
	console.log("modalIframe");

	var documento = $(window.parent.parent.document);
	var iframe = $(documento).find('iframe');
	var padre = $(iframe[0].parentNode);
	var abuelo = $(iframe[0].parentNode.parentElement);
	
	var rightIFrame = 0;



	if (anchoModal == 0) {

		anchoModal = $(iframe[0]).width();		
		var iframeFinal = iframe[0];

		if ($(iframe[0]).contents().find("iframe").length > 0) {
			iframeFinal = $(iframe[0]).contents().find("iframe");
		}

		var antes = 0;
		var actual = 0;
		var posFinal = 0;
		var max = 0;

		if ($(iframeFinal).contents().find('.ui-growl-item').length == 0 && $(iframeFinal).contents().find(".bodyModalDatos").length == 0) {
			//if ($(iframeFinal).contents().find('.ui-growl-item').length == 0 ) {
			console.log("if");
			max = $(abuelo).width()-30;

			$(iframeFinal).contents().find(".radiusModal").css("width",max);
			$(padre).css("width",max+30);

			var vent = window.parent.parent.innerWidth;
			$(iframeFinal).css("width", vent > 900 ? 900 : vent);

			$(iframe[0]).css("width", vent > 900 ? 900 : vent);

			antes = parseInt($(iframeFinal).css('width').replace("px",""));
			
			if($(window.parent.parent.document)[0].activeElement.id.indexOf('960206') > 0){
				console.log("proceso de ajuste modal");
				$(abuelo).css('height', '600px');
				$(padre).css('height', '600px');
				$(iframeFinal).contents().find(".radiusModal").css("width",max);
				$(window.parent.parent.document).find('iframe').contents().find('iframe').css('height', '555px');
				$(window.parent.parent.document).find('iframe').contents().find('iframe').css('width', '659px');
				//$(window.parent.parent.document).find('iframe').contents().find('iframe').attr('scrolling', 'yes');
			}
		}else{
			console.log("else");


			if( $(iframeFinal).contents().find('.ui-growl-item').length > 0){
				console.log("else1");
				max = window.parent.parent.document.querySelector(".ui-dialog-content.overFlowVisible").style.width;
				max = max.replace("px","");
				max = parseInt(max)-30;

				antes = parseInt($(iframeFinal).contents().find('.radiusModal').css('width').replace("px",""));
				
			}
		}


		setTimeout(function(){

			if ($(iframeFinal).contents().find(".dgInterno").length>0) {

				var izq = $(iframeFinal).contents().find(".dgInterno").css("left").replace("px","");
				var posicionFinal;
				if (izq=="auto") {
					var totalModal = $(iframeFinal).contents().find(".radiusModal").width();
					var totalDg = $(iframeFinal).contents().find(".dgInterno").width();
					posicionFinal = (totalModal - totalDg)/2;
				}
				else{
					posicionFinal =(izq-((antes-max)/2));
				}

				$(iframeFinal).contents().find("body").append("<style>.dgInterno{ left: "+ posicionFinal +"px !important; } </style>");
				$(iframeFinal).contents().find(".dgInterno").animate({'opacity':'1', 'margin-top':'20px'},200);			
			}

		},50);

		$(iframeFinal).find(".radiusModal").css("border","1px solid #4f81bd");
		actual = parseInt($(".ui-growl").css('right').replace("px",""));
		posFinal = actual+(antes-max);
		$(iframeFinal).contents().find("body").append("<style> .ui-growl{ right:"+ posFinal +"px !important; z-index:999999 !important;  } </style>");
	}
	$(".radiusModal").css("border","1px solid #4f81bd");
	$("iframe").contents().find(".radiusModal").css("border","1px solid #4f81bd");
	$("iframe").contents().find("iframe").contents().find(".radiusModal").css("border","1px solid #4f81bd");
	$("iframe").contents().find("iframe").contents().find(".dgInterno").animate({'opacity':'1', 'margin-top':'20px'},200);
}

function cargarImagen(id) {
	try {
		$(id).context.getElementById(id).src = $(id).context.getElementById(id).src.replace('?pfdrid_c=true', '');
	} catch (e) {

	}
}

function cargarCombosFila(indice, tbfr) {
	$('[id*=' + tbfr + '\\:' + indice + '\\:][id$=_panel]').each(function () {
		var id = this.id.replace('_panel', '');
		try {
			id = id.replace(/:/g, '\\:');
			var ancho = $('#' + id)[0].offsetWidth;
			this.style['width'] = ancho + 'px';
		} catch (e) {

		}
	}
	);
}

function cargarModalZoom(){
	var documento = $(window.parent.parent.document);
	var iframe = $(documento).find('iframe');
	var padre = $(iframe[0].parentNode);
	var abuelo = $(iframe[0].parentNode.parentElement);


	var rightIFrame = 0;

	var iframeFinal = iframe[0];

	if ($(iframe[0]).contents().find("iframe").length > 0) {
		iframeFinal = $(iframe[0]).contents().find("iframe");
	}
	var zoomWidth = parseInt($(iframeFinal).contents().find("#zoom").width());
	anchoModal = parseInt(anchoModal);
	var leftZoom = ((anchoModal/2) - (zoomWidth/2));

	$(iframeFinal).contents().find("body").append("<style id='zoomStyle'> #zoomOverlay{width:"+ (anchoModal-2)+"px !important; border-radius: 7px !important; } #zoom{width: 45% !important; left:27.5% !important; } </style>");
	$(iframeFinal).contents().find("#zoom").css("width", (anchoModal-(leftZoom*2))+"px");
	console.log(anchoModal);
	console.log(zoomWidth);


}

function gestionarZoom(root){

	//Captura evento de presionar Shift + F2
	$(root).on("keydown","input:not(.ui-column-filter):not(.ui-selectonemenu-filter):not(.hasDatepicker):not([type='password']),textarea:not(.ui-column-filter):not(.ui-selectonemenu-filter):not(.hasDatepicker):not([type='password']):not(.content)", function(e){
		if(e.which===113 && e.shiftKey){
			var root = document;
			e.preventDefault();
			if ($(root).find("#zoomOverlay").length > 0 ){
				return;
			}
			var elemento = e.target;
			var eventos = $._data(elemento, "events");

			//Obtiene la propiedades del elemento que genero el evento para pasarlas al cuadro de zoom
			//lineanAnt: var soloLectura = $(elemento).attr("readonly")?" readonly='readonly' ":"";
			var soloLectura = " readonly='readonly' ";
			var longitud = $(elemento).attr("maxlength")?" maxlength='"+ $(elemento).attr("maxlength") +"'":"";

			//Genera estructura del cuadro de zoom
			var html = "<div id='zoomOverlay'>																				"+
			"	<div id='zoom'>																					"+
			"		<div class='title'>Zoom</div>														"+
			"		<textarea "+ soloLectura + longitud + " class='content'>" + elemento.value.trim() + "</textarea>	"+
			"		<div class='actions'>																		"+
			"			<button id='aceptar'>Aceptar</button>													"+
			"			<button id='cancelar'>Cancelar</button>													"+
			"		</div>																						"+
			"	</div>																							"+
			"<div>";

			//Invoca las acciones del zoom
			cargarZoom(root, html);
			generarEventosZoom(root, elemento, eventos);	
		}/*else{
			$("#zoom").find(".content").val(e.target.value);
		}*/
	});
}

//Crea el cuadro de zoom y da el foco al textArea interno
function cargarZoom(root, html){
	$(root).find("body").prepend(html);

	$("#zoom").animate({'top':'5%', 'opacity':'1'},300,function(){
		$("#zoom").find("textarea").focus();
		$("#zoom").find("textarea").val($("#zoom").find("textarea").val() + " ");
		$("#zoom").find("textarea").val($("#zoom").find("textarea").val().trim());
	});

	if (anchoModal != 0) {
		cargarModalZoom();
	}
}

//Genera los eventos necesarios para interactuar con el cuadro de zoom
function generarEventosZoom(root, elemento, eventos){
	var valorActual = $(elemento).val();
	var event;


	//Captura el evento de presionar la tecla Esc
	$("#zoom").on("keydown", function(e){
		if (e.which===27) {
			if ($(root).find("#zoom").length > 0 ){
				cerrarZoom(valorActual, elemento);
			}
		}
	});


	//Captura el evento click sobre los botones del cuadro de zoom
	$("#zoom").find("button").on("click",function(e){
		var nuevoValor = valorActual;

		if (e.target.id=="aceptar") {
			nuevoValor = $("#zoom").find(".content").val();
		} 
		cerrarZoom(nuevoValor, elemento);	
	});
	var boolevent = false;
	console.log(eventos);
	/*Object.keys(eventos).map(function(e) {

		if (e.includes('keydown') ) {
			event = eventos[e];
			$("#zoom").find(".content").on(e, event[0].handler);
			boolevent = true;
		}else if(e.includes('keypress')){
			event = eventos[e];
			$("#zoom").find(".content").on(e, event[0].handler);
		}
	});*/



}

//Actualiza el valor, elimina el cuadro de zoom y devuelve el foco al elemento original.
function cerrarZoom(nuevoValor, elemento){
	console.log(nuevoValor);
	$(elemento).val(nuevoValor + " ");
	$(elemento).focus();
	console.log($(elemento).val().trim());
	$(elemento).val($(elemento).val().trim());
	$("#zoomOverlay").remove();
	$("#zoomStyle").remove();	
}

/**
 * Busca el id del contenerdor de pestania unico del formulario
 */
function buscarPestaniaUnica(){
	var id=$(".ui-tabs")[0].id;
	id=id.substring(id.lastIndexOf(':')+1);
	return id;
} 

/*
 * Recorre las pestanias del contenedor de pestanias ingresado por parametro
 * y vuelve a selececionar la primera pestania, se desarrolla debido a un
 * Bug de Primefaces, el cual impide recargar un datatable dentro de una
 * pestania que no se visualizado
 */
function recorrerPestania(idTs){
	for(var i=0; i<PF(idTs).getLength() ;i++){ 
		PF(idTs).select(i); 
	}
	PF(idTs).select(0);
}

/*
 * Recorre las pestanias del contenedor de pestanias unico del formulario 
 * y vuelve a selececionar la primera pestania, se desarrolla debido a un
 * Bug de Primefaces, el cual impide recargar un datatable dentro de una
 * pestania que no se visualizado
 */
function recorrerPestaniaUnica(){
	 recorrerPestania(buscarPestaniaUnica());
}


function cargarEditorHtml(){
	console.log("carga editor");
	
	var html = "<div id='fondo' style='background: rgba(0, 0, 0, .4); position: absolute; top: 0; left: 0; width: 100%; height: 100%;' >algo</div>"
	
	$("body").append(html);

console.log("carga editor1");
	
	$("#editor").css("top","118px");
	$("#editor").animate({"opacity":1},400);	
	
	activarEditor();
	console.log("carga editor2");
	
	$("#fondo").click(function(){
		activarEditor();
		
		$("#editor").animate({"opacity":0},200, function(){
			
				$("#editor").css("top","-1118px");	
			
			 
		});
		$("body").find("#fondo").remove();
		
		actualizarHtml();
		console.log("cerrar Editor");
	});
	
}	







