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

    document.getElementById('json-container').style.display="flex"; //enable table

    const tableBody = document.getElementById('json-container');
    jsontest.forEach((item) => { //for every json value retrieved i append the following data
        const animeimg = document.createElement('div');
        animeimg.classList.add('image-wrapper'); //adds class
        animeimg.innerHTML = `
            <h2>${item.media.title.english}</h2>
            <img src="${item.media.coverImage.extraLarge}">
            <div class="buttons">
            <button>Button 1</button>
            </div>
        `;
        tableBody.appendChild(animeimg);

        /*
        const row = document.createElement('tr');
        row.innerHTML = `
          <td>${item.media.title.english}</td>
          <td><img src="${item.media.coverImage.extraLarge}"></td>
          <td>${item.pageRankValue}</td>
        `;
        tableBody.appendChild(row);
         */
    });
}

function response(){
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