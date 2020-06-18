const myCanvas = document.getElementById("my-canvas")
const myContext = myCanvas.getContext("2d")

const img = new Image()
img.src = "./img/r850r.jpg"
img.onload = () => {
	myContext.drawImage(img, 0, 0)
}

function getClickPosition(e) {
	const rect = myCanvas.getBoundingClientRect()
	const xPosition = e.clientX - rect.left;
	const yPosition = e.clientY - rect.top;
	
	console.log("x: " + xPosition + " y: " + yPosition)
}

myCanvas.addEventListener("click", getClickPosition, false);