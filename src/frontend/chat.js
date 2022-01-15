import { createText, createButton, getUsername, getPartner } from './common.js';

function createChatListItem(item) {
  const li = document.createElement('li');
  li.appendChild(createText(item.user + ': '));
  if (item.type == 1) { // normal text
    li.appendChild(createText(item.content));
  } else if (item.type == 2) { // image
    const img = document.createElement('img');
    img.src = item.content;
    li.appendChild(img);
  } else { // binary
    const a = document.createElement('a');
    a.href = item.content;
    a.download = true;
    a.appendChild(createText(item.content));
    li.appendChild(a);
  }
  return li;
}

function appendChatList(chatLog) {
  let ul = getUl();
  for (const item of chatLog) {
    ul.appendChild(createChatListItem(item));
  }
}

function prependChatList() {
  let chatLog = JSON.parse(this.responseText);
  let ul = getUl();
  for (const item of chatLog.slice().reverse()) {
    ul.insertBefore(createChatListItem.apply(this, item), ul.firstChild);
  }
}

function getUl() {
  return document.getElementById('chat-list');
}

function getSendedText() {
  return document.getElementById('text-message').value;
}


function lobby() {
  document.location = './lobby.html';
}

function setTitle(partner) {
  const title = 'Chat with ' + partner;
  document.title = title;
  const h1 = document.getElementsByTagName('h1')[0];
  h1.textContent = title;
}

function loadMore() {
}

function refresh() {
  fetch("/refresh",{
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      sender: getUsername(),
      receiver: getPartner(),
    })
  })
  .then((response) => response.json())
  .then(appendChatList)
  .catch((reason) => console.log(reason));
}

function sendText() {
  fetch("/send-text",{
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      sender: getUsername(),
      receiver: getPartner(),
      content: getSendedText()
    })
  });
  // TODO: update chat history accordingly
}

function sendFile(isImage=false) {
  return () => {
    const fileField = document.getElementById(isImage ? 'image' : 'file').files[0];
    const username = getUsername();
    const partner = getPartner();
    const filename = fileField.name;
    fetch((isImage ? "/send-image" : "/send-file") + `/${username}/${partner}/${filename}`,{
      method: "POST",
      headers: {'Content-Type': (isImage ? 'image/png' : 'application/octet-stream')},
      body: fileField});
    // TODO: update chat history accordingly
  }
}

setTitle(getPartner());
const ul = document.createElement('ul');
ul.id = 'chat-list';
document.body.insertBefore(ul, document.getElementById('before-chat'));
// TODO: load initial chat history
document.getElementById('lobby').onclick = lobby;
document.getElementById('load-more').onclick = loadMore;
document.getElementById('refresh').onclick = refresh;
document.getElementById('send-text').onclick = sendText;
document.getElementById('send-image').onclick = sendFile(true);
document.getElementById('send-file').onclick = sendFile(false);
