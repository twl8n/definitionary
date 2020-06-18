/* const myCanvas = document.getElementById("my-canvas")
const myContext = myCanvas.getContext("2d") */

/* onload image draw (changed to canvas background css)
const img = new Image()
img.src = "./img/r850r.jpg"
img.onload = () => {
	myContext.drawImage(img, 0, 0)
} */

function drawTextBox(e) {
	// Draw text box at click location.
	const rect = myCanvas.getBoundingClientRect()
	var x = e.clientX - rect.left;
	var y = e.clientY - rect.top;
	
	console.log("Int: x: " + x + " y: " + y)
	
	var size = prompt("Enter the size of the box as x,y");
	console.log(size);
	
	var res = size.split(",");
	var xSize = parseInt(res[0]);
	var ySize = parseInt(res[1]);
	
	myContext.beginPath();
	myContext.fillRect(x,y,xSize,ySize);
	console.log(x,y,xSize,ySize);
	myContext.stroke();
	
}

function drawTextBox2(e) {
	// Draw text box at click location. Box sizes to text automatically.
	const rect = myCanvas.getBoundingClientRect()
	var x = e.clientX - rect.left;
	var y = e.clientY - rect.top;
	
	console.log("Int: x: " + x + " y: " + y);
	
	var txt = prompt("Enter text");
	

	var font = '12pt Arial';
	myContext.font = font;
	myContext.textBaseline = 'top';
	var width = myContext.measureText(txt).width;
	var height = myContext.measureText(txt).height;
	
	myContext.fillStyle = '#f50';
	myContext.fillRect(x,y,width + 10,parseInt(font,10)+6);
	
	myContext.fillStyle = '#000';
	
	myContext.fillText(txt,x + 5,y + 2);
	
	
}


	


function drawSVGTextBox(e) {
	//draw SVG text box at cursor location
	
	const rect = svg1.getBoundingClientRect();
	
	var x = e.clientX - rect.left;
	var y = e.clientY - rect.top;
	console.log("x: " + x + " y: " + y);
	
	var txt = prompt("Enter text");
	var t = document.createTextNode(txt);
	
	element = document.createElementNS("http://www.w3.org/2000/svg", "text");
	element.setAttributeNS(null, 'x', x);
	element.setAttributeNS(null, 'y', y);

	
	element.appendChild(t);
	
	console.log(element);

	svg1.appendChild(element);
	
	SVGRect = element.getBBox();
	var rect1 = document.createElementNS("http://www.w3.org/2000/svg", "rect");
	rect1.setAttributeNS(null,"x",SVGRect.x);
	rect1.setAttributeNS(null,"y",SVGRect.y);
	rect1.setAttributeNS(null,"width",SVGRect.width);
	rect1.setAttributeNS(null,"height",SVGRect.height);
	rect1.setAttributeNS(null,"fill","cyan");
	rect1.setAttributeNS(null,"onclick","remove");
	
	textElem = svg1.getElementById(element);
	console.log(textElem);

	
	svg1.appendChild(rect1);
	console.log(rect1);
	svg1.appendChild(element);	
	
}

//svg1.addEventListener("click",drawSVGTextBox,false);



myCanvas.addEventListener("click", drawTextBox2, false);


