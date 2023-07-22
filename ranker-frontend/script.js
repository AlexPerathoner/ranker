const jsontest = [
    {
        "mediaId": 102351,
        "media": {
            "title": {
                "romaji": "Tokyo Ghoul:re 2",
                "english": "Tokyo Ghoul:re 2",
                "userPreferred": "Tokyo Ghoul:re 2"
            },
            "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx102351-yD3Ty9YZFMsf.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx102351-yD3Ty9YZFMsf.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx102351-yD3Ty9YZFMsf.jpg",
                "color": "#f1e4c9"
            }
        },
        "id": 102351,
        "pageRankValue": 0.0
    },
    {
        "mediaId": 99147,
        "media": {
            "title": {
                "romaji": "Shingeki no Kyojin 3",
                "english": "Attack on Titan Season 3",
                "userPreferred": "Shingeki no Kyojin 3"
            },
            "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx99147-5RXELRvwjFl6.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx99147-5RXELRvwjFl6.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx99147-5RXELRvwjFl6.jpg",
                "color": "#4386e4"
            }
        },
        "id": 99147,
        "pageRankValue": 0.0
    }
]

let arrayButton= [];


//controlla se il nick esiste su anilist, se esiste carica la sua anilist
function inviaNick() {
    let nick=document.getElementById("nick").value;
    let theUrl = "http://localhost:8080/load-series?username="+nick
    console.log(theUrl);

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        localStorage.setItem('nick',nick);
        window.location.href = 'serie.html';
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
}

//metodo finale da usare quando possiamo prendere gli oggetti json da controller
function loadSeries(){
    let theUrl = "localhost:8080/get-next-comparison?username="+localStorage.getItem("nick");

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, false);
    xhr.onload = (e) => {
       if (xhr.readyState === 4 && xhr.status === 200) {
            let my_Json_obj = this.response;
            document.getElementById('json-container').style.display="flex"; //enable table
            const tableBody = document.getElementById('json-container');
            my_Json_obj.forEach((item, index) => {
                const animeimg = document.createElement('div');
                animeimg.classList.add('image-wrapper'); // Aggiunge la classe 'image-wrapper' al div

                const title = document.createElement('h2');
                title.textContent = item.media.title.english;
                animeimg.appendChild(title);

                const image = document.createElement('img');
                image.src = item.media.coverImage.extraLarge;
                animeimg.appendChild(image);

                const buttonsDiv = document.createElement('div');
                buttonsDiv.classList.add('buttons');

                const button = document.createElement('button');
                button.type="button";
                button.value=item.mediaId;
                button.onclick = function() {
                    response(item.mediaId);
                };
                arrayButton[index]=item.mediaId;
                button.textContent = 'Choose';
                buttonsDiv.appendChild(button);

                animeimg.appendChild(buttonsDiv);

                tableBody.appendChild(animeimg);

                if (index === 1) {
                    const preferContainer = document.createElement('div');
                    preferContainer.classList.add('prefer-container');

                    const testoPrefer = document.createElement('h1');
                    testoPrefer.classList.add('question');
                    testoPrefer.style.paddingTop="250px";
                    testoPrefer.innerHTML = 'WHICH DO YOU <span class="prefer-linebreak">PREFER?</span>';
                    preferContainer.appendChild(testoPrefer);

                    const buttonsDiv = document.createElement('div');
                    buttonsDiv.classList.add('buttons');

                    const preferButton = document.createElement('button');
                    button.type="button";
                    preferButton.value="0";
                    preferButton.onclick = function() {
                        response("0");
                    };
                    arrayButton[2]=0;
                    preferButton.style.marginTop="220px";
                    preferButton.textContent = 'Equal';
                    buttonsDiv.appendChild(preferButton);
                    preferContainer.appendChild(buttonsDiv);

                    tableBody.insertBefore(preferContainer, animeimg);
                }
           });
       } else {
            console.error(xhr.statusText);
       }
    };

    xhr.onerror = (e) => {
        console.error(xhr.statusText);
    };
    xhr.send(null);
}

//metodo prova per stampare oggetti json
function loadJSONtest() {

    document.getElementById('json-container').style.display = "flex"; //enable table

    const tableBody = document.getElementById('json-container');
    jsontest.forEach((item, index) => {
        const animeimg = document.createElement('div');
        animeimg.classList.add('image-wrapper'); // Aggiunge la classe 'image-wrapper' al div

        const title = document.createElement('h2');
        title.textContent = item.media.title.english;
        animeimg.appendChild(title);

        const image = document.createElement('img');
        image.src = item.media.coverImage.extraLarge;
        animeimg.appendChild(image);

        const buttonsDiv = document.createElement('div');
        buttonsDiv.classList.add('buttons');

        const button = document.createElement('button');
        button.type="button";
        button.value=item.mediaId;
        button.onclick = function() {
            response(item.mediaId);
        };
        arrayButton[index]=item.mediaId;
        button.textContent = 'Choose';
        buttonsDiv.appendChild(button);

        animeimg.appendChild(buttonsDiv);

        tableBody.appendChild(animeimg);

        if (index === 1) {
            const preferContainer = document.createElement('div');
            preferContainer.classList.add('prefer-container');

            const testoPrefer = document.createElement('h1');
            testoPrefer.classList.add('question');
            testoPrefer.style.paddingTop="250px";
            testoPrefer.innerHTML = 'WHICH DO YOU <span class="prefer-linebreak">PREFER?</span>';
            preferContainer.appendChild(testoPrefer);

            const buttonsDiv = document.createElement('div');
            buttonsDiv.classList.add('buttons');

            const preferButton = document.createElement('button');
            button.type="button";
            preferButton.value="0";
            preferButton.onclick = function() {
                response("0");
            };
            arrayButton[2]=0;
            preferButton.style.marginTop="220px";
            preferButton.textContent = 'Equal';
            buttonsDiv.appendChild(preferButton);
            preferContainer.appendChild(buttonsDiv);

            tableBody.insertBefore(preferContainer, animeimg);
        }
    });

}

//restituisce la scelta dell'utente e carica la prossima coppia
function response(id){
    let theUrl;
    if(id==="0"){
        theUrl = "localhost:8080/add-link?betterId=0&worseId=0&username="+localStorage.getItem("nick");
    } else if(id===arrayButton[0]){
        theUrl = "localhost:8080/add-link?betterId="+id+"&worseId="+arrayButton[1]+"&username="+localStorage.getItem("nick");
    } else {
        theUrl = "localhost:8080/add-link?betterId="+id+"&worseId="+arrayButton[0]+"&username="+localStorage.getItem("nick");
    }

    const xhr = new XMLHttpRequest();
    xhr.open("GET", theUrl, true);
    xhr.onload = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const myDiv = document.getElementById('json-container');
            myDiv.innerHTML = '';
            arrayButton = [];
            loadJSONtest();
        } else {
            console.error(xhr.statusText);
        }
    };

    xhr.onerror = (e) => {
        console.error(xhr.statusText);
    };
    xhr.send(null);

    return 0;
}

//todo
//aggiungere pulsante per quando gli anime son uguali
//rendere le immagini cliccabili
