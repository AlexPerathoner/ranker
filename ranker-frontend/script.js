function inviaNick() {
    let nick=document.getElementById("nick").value;
    let theUrl = "http://localhost:8080/load-series?username="+nick

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            localStorage.setItem('nick',nick);
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
    // implementare risposta json del server con i due anime scelti
}

function loadSeries(){
    let theUrl = "localhost:8080/get-next-comparison?username="+localStorage.getItem("nick");

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            let my_Json_array = this.response;
        } else {
            console.error(xhr.statusText);
        }
    };

    xhr.onerror = (e) => {
        console.error(xhr.statusText);
    };
    xhr.send(null);
}

function response(nick){
    let theUrl = "localhost:8080/add-link?betterId=102351&worseId=99147&username="+localStorage.getItem("nick");

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            loadSeries();
        } else {
            console.error(xhr.statusText);
        }
    };

    xhr.onerror = (e) => {
        console.error(xhr.statusText);
    };
    xhr.send(null);
}