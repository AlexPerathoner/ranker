function inviaNick() {
    let nick=document.getElementById("nick").value;

    let theUrl = "http://localhost:8080/load-series?username="+nick
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
    xmlHttp.send( null );
    // todo
    // usare un altro metodo per fare la richiesta get al server backend
    // subito dopo aver inviato la richiesta fai apparire un qualcosa che mostra che sta caricando
    // una volta che il server ha risposto alla richiesta, cambia pagina
    // ...document..location = series...
}