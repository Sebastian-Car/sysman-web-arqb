

function load(){
    $(".enlacestilo").click(function(e){
            e.preventDefault();
            var rutaEstilo=$(this).attr("value");
            $("#linkestilo").attr("href", rutaEstilo);
           });
}