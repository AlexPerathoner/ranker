const canvas = document.getElementById('cont');
const slider = document.getElementById('slider-scale')
let SCALE = 100
const DAMPING_FACTOR = 0.85
const svgns = "http://www.w3.org/2000/svg";

let nodes = []
let edges = []
let pageRankValues = []
let clickedCircle = null

setInterval(recalculateNodesWeight, 1000);
canvas.addEventListener("click", (e => {drawCircle(e.pageX, e.pageY)}))
slider.oninput = function() {
    SCALE=this.value
    recalculateNodesWeight()
}

function resetPageRankValue() {
    for(let i=0; i<nodes.length; i++) {
        pageRankValues[nodes[i].id] = 1/nodes.length
    }
}

function circleClicked(elem) {
    if(clickedCircle) { // Checking if another circle has already been clicked
        edges.push([clickedCircle.id,elem.id])
        drawLine(clickedCircle, elem)
        clickedCircle = null
    } else {
        clickedCircle = elem // Saving first clicked circle
    }
}

function recalculateNodesWeight() {
    nodes.forEach(nodeElement => {
        let pageRankValue = getPageRankOfNode(nodeElement.id)
        pageRankValues[nodeElement.id] = pageRankValue
        nodeElement.setAttribute('r', pageRankValue*SCALE);
    })
    console.log(pageRankValues)
}

function getPageRankOfNode(elemId) {
    let accumulator = 0
    getNodesLinkingTo(elemId).forEach(nodeId => {
        accumulator += pageRankValues[nodeId] / getNumberOfLinksFromNode(nodeId)
    })
    return (1 - DAMPING_FACTOR) / nodes.length + accumulator * DAMPING_FACTOR
}

function getNodesLinkingTo(elemId) {
    let nodes = []
    edges.forEach(edge => {
        if(edge[1] == elemId) {
            nodes.push(edge[0])
        }
    })
    return nodes
}

function getNumberOfLinksFromNode(elemId) {
    let n = 0
    edges.forEach(edge => {
        if(edge[0] == elemId) {
            n++
        }
    })
    return n
}


// DRAWING

function drawCircle(x, y) {
    let container = document.getElementById('cont');
    let circle = document.createElementNS(svgns, 'circle');
    circle.setAttributeNS(null, 'cx', x);
    circle.setAttributeNS(null, 'cy', y);
    circle.setAttributeNS(null, 'r', 30);
    circle.setAttributeNS(null, 'style', 'fill: #777; stroke: black; stroke-width: 3px;');
    circle.id = nodes.length
    clickedCircle = null
    circle.addEventListener("click", (event) => {
        event.cancelBubble = true
        event.stopPropagation()
        console.log("Cliked " + circle.id)
        circleClicked(circle)
    })
    container.appendChild(circle);
    nodes.push(circle)
    resetPageRankValue()
}

function drawLine(a, b) {
    let container = document.getElementById('cont');
    let id = 'path' + edges.length
    let line = buildLine(a, b, id)
    container.insertBefore(line, container.firstChild);
    drawArrow(id)
    resetPageRankValue()
}

function drawArrow(id) {
    let xlinkNS = 'http://www.w3.org/1999/xlink';
    let container = document.getElementById("arrows");
    let arrow = document.createElementNS(svgns, 'textPath');
    arrow.setAttributeNS(xlinkNS, 'href', '#' + id);
    arrow.setAttributeNS(null, 'startOffset', '30%');
    arrow.innerHTML = "➤"
    container.appendChild(arrow)
}

function buildLine(a, b, id) {
    let line = document.createElementNS(svgns, 'line');
    line.setAttributeNS(null, 'x1', a.cx.baseVal.value);
    line.setAttributeNS(null, 'y1', a.cy.baseVal.value);
    line.setAttributeNS(null, 'x2', b.cx.baseVal.value);
    line.setAttributeNS(null, 'y2', b.cy.baseVal.value);
    line.setAttributeNS(null, 'id', id);
    line.setAttributeNS(null, 'style', 'stroke: black; stroke-width: 3px');
    return line
}

function reset() {
    nodes = []
    edges = []
    pageRankValues = []
    clickedCircle = null
    let container = document.getElementById('cont');
    container.innerHTML = ""
}
