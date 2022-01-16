import { createText, createButton, getUsername, getPartner } from './common.js';

function createChatListItem(item) {
  const li = document.createElement('li');
  li.appendChild(createText(item[1] + ': '));
  if (item[0] == 1) { // normal text
    li.appendChild(createText(item[2]));
  } else {
    const sender = item[1];
    const receiver = (sender != getUsername()) ? getUsername() : getPartner();
    const path = `./${sender}/${receiver}/${item[2]}`;
    if (item[0] == 2) { // image
      const img = document.createElement('img');
      img.src = path;
      li.appendChild(img);
    } else { // binary
      const a = document.createElement('a');
      a.href = path;
      a.download = true;
      a.appendChild(createText(item[2]));
      li.appendChild(a);
    }
  }
  return li;
}

function appendChatList(chatLog) {
  let ul = getUl();
  chatLog.reverse();
  for (const item of chatLog) {
    ul.appendChild(createChatListItem(item));
  }
}

function prependChatList() {
  let chatLog = JSON.parse(this.responseText);
  let ul = getUl();
  for (const item of chatLog) {
    ul.insertBefore(createChatListItem(item), ul.firstChild);
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
