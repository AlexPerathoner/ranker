function inviaNick() {
    let nick=document.getElementById("nick").value;
    let theUrl = "http://localhost:8080/load-series?username="+nick

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            window.location.href = 'serie.html';
        } else {
            console.error(xhr.statusText);
        }
    };
    document.getElementById("form-container").style.display="none";
    document.getElementById("imm-container").style.display="block";


    xhr.onerror = (e) => {
        console.error(xhr.statusText);
    };
    xhr.send(null);
    // todo
    // una volta che il server ha risposto alla richiesta, cambia pagina (serie.html)
    // ...document..location = series...
}