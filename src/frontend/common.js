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

function postJSON(action, jsonString, callback, asynchronous=true) {
  let oReq = new XMLHttpRequest();
  oReq.addEventListener('load', callback);
  oReq.addEventListener('error', (e) => console.log(e));
  oReq.open("POST", './' + action, asynchronous);
  oReq.setRequestHeader('Content-Type', 'application/json');
  oReq.send(jsonString);
}

export { createText, createButton, getUsername, postJSON };
