
function drawSVGTextBox(e,n) {
	//draw SVG text box at cursor location
	
	if (e.target.getAttribute('id') == "svg1"){
		console.log("targeted " + e.target.getAttribute('id'));
		
		const rect = svg1.getBoundingClientRect();
		
		var x = e.clientX - rect.left;
		var y = e.clientY - rect.top;
		console.log("x: " + x + " y: " + y);
		
		var txt = prompt("Enter text");
		var t = document.createTextNode(txt);
		
		var stringID = x + "-" + y;
		
		element = document.createElementNS("http://www.w3.org/2000/svg", "text");
		element.setAttributeNS(null, 'x', x);
		element.setAttributeNS(null, 'y', y);
		element.setAttributeNS(null, 'id',stringID);

		
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
		rect1.setAttributeNS(null,"id","rect" + stringID);
		
		console.log(rect1);

		
		svg1.appendChild(rect1);
		svg1.appendChild(element);	
	
	}
	else {
		console.log(e.target);
		
		id = e.target.getAttribute('id')
		
		console.log(id);
		e.target.remove();
		svg1.getElementById('rect'+ id).remove();
		
	}
	
}

function searchByID(id) {
	//Query DB/JSON for coords
	if (svg1.getElementById(id) != null) {
		var coord = id.split("-");
		var xCoord = coord[0];
		var yCoord = coord[1];
		//can query JSON for x,y coord, load from JSON
		//return "exists", query db and draw?
	}
	else {
		//do something else
	}
}
svg1.addEventListener("click", drawSVGTextBox, false);


//myCanvas.addEventListener("click", drawTextBox2, false);


