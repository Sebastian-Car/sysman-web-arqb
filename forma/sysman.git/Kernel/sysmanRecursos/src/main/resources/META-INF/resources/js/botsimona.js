var imagenSimona = document.getElementById('imagenSimona');
var tarjeta = document.getElementById('tarjeta');
var botonCerrar = document.getElementById('closeButton');
var iframe = document.getElementById('iframeBot'); 
var preUrl = "https://faq.sysman.com.co/webhook/";
var urlBot = preUrl+"09b78ae9-d1e5-40ca-8589-a6aae503523f/chat";
var rutaImagen = "/opt/sysman/data/imagenes/Simona.png";

botonCerrar.style.display = "none";

imagenSimona.addEventListener('click', () => {
  tarjeta.classList.remove('ocultarTarjeta');
  botonCerrar.classList.remove('ocultarboton');
  tarjeta.classList.add('tarjeta');
  botonCerrar.style.display="inline-block";
  imagenSimona.classList.remove('mantenerImgSimona');
  imagenSimona.classList.remove('ocultarimagensimona')
  imagenSimona.classList.add('imagenSimona');
});


botonCerrar.addEventListener('click', () => {
  tarjeta.classList.remove('tarjeta');
  botonCerrar.classList.add('ocultarboton');
  tarjeta.classList.add('ocultarTarjeta');
  imagenSimona.classList.remove('imagenSimona');
  imagenSimona.classList.add('mantenerImgSimona');
});

function  mostrarBot(modulo){
    switch (modulo) {
      case 1: //cuipo
    	  urlBot = preUrl+"c926acea-735b-40dc-ba4e-8fd66c66e51f/chat";
        break;
      case 2: //retro
    	  urlBot = preUrl+"09b78ae9-d1e5-40ca-8589-a6aae503523f/chat";
        break;
      default:
    	  urlBot = preUrl+"09b78ae9-d1e5-40ca-8589-a6aae503523f/chat";
    }
		iframe.data = urlBot;
		if(imagenSimona.className.includes("ocultarimagensimona")){
			imagenSimona.classList.remove('ocultarimagensimona');
			imagenSimona.classList.add('mantenerImgSimona'); 
	   }

}
function  ocultarBot(){
	 tarjeta.classList.remove('tarjeta');
	 botonCerrar.classList.add('ocultarboton');
	 tarjeta.classList.add('ocultarTarjeta');
	 imagenSimona.classList.remove('imagenSimona');
	 imagenSimona.classList.add('mantenerImgSimona');
	 imagenSimona.classList.add('ocultarimagensimona');
}
