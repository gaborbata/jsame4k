// based on the book: Core HTML5 Canvas by David Geary
function getMousePosition(event) {
    var boundingRect = canvas.getBoundingClientRect();
    return {
        x: (event.clientX - boundingRect.left) * (canvas.width / boundingRect.width),
        y: (event.clientY - boundingRect.top) * (canvas.height / boundingRect.height)
    };
}
