function createText(text) {
  return document.createTextNode(text);
}

function createButton(text, handler) {
  const button = document.createElement('button');
  button.appendChild(createText(text));
  button.onclick = handler;
  return button;
}

function getUsername() {
  return localStorage.getItem('username');
}

function getPartner() {
  return localStorage.getItem('partner');
}

export { createText, createButton, getUsername, getPartner };
