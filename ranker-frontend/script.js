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
    xhr.open("GET", theUrl, false);
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

function loadJSONtest() {
    const firstItem = jsontest[0];
    console.log(firstItem.mediaId);
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