import { createText, createButton, getUsername } from './common.js';

function createChatListItem(type, name, message) {
  const li = document.createElement('li');
  li.appendChild(createText(name + ': '));
  if (type == 1) { // normal text
    li.appendChild(createText(message));
  } else if (type == 2) { // image
    const img = document.createElement('img');
    img.src = message;
    li.appendChild(img);
  } else { // binary
    const a = document.createElement('a');
    a.href = message;
    a.download = true;
    a.appendChild(createText(message));
    li.appendChild(a);
  }
  return li;
}

function appendChatList(ul, chatLog) {
  for (const item of chatLog) {
    ul.appendChild(createChatListItem.apply(this, item));
  }
}

function prependChatList(ul, chatLog) {
  for (const item of chatLog.slice().reverse()) {
    ul.insertBefore(createChatListItem.apply(this, item), ul.firstChild);
  }
}

function lobby() {
  document.location = './lobby.html';
}

function loadMore() {
  // TODO: Send a post request to load more history
  const moreLog = log2;
  prependChatList(ul, moreLog);
}

function refresh() {
  // TODO: send a post request to load new messages
  const moreLog = log1;
  appendChatList(ul, moreLog);
}

function sendText() {
  fetch("/send-text",{
    method: "POST",
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      sender: "a",
      receiver: "b",
      content: "content"
    })
  });
  // TODO: send a post request to update chat history
  refresh();
}

function sendImage() {
  // TODO: send a post request to update chat history
  refresh();
}

function sendFile() {
  // TODO: send a post request to update chat history
  refresh();
}

const log1 = [
  [1, 'Titus', 'Hi'],
  [1, 'titusjgr', 'Hey'],
];

const log2 = [
  [2, 'Titus', 'https://1.bp.blogspot.com/-7MEg_48n4Gc/YHDkTOuiU5I/AAAAAAABdnc/a1kgLWWDiIEbTbWL7v8dSVKxjjxv-ALgQCNcBGAsYHQ/s90-c/vegetable_lettuce.png'],
  [3, 'titusjgr', 'https://git.musl-libc.org/cgit/musl/plain/src/aio/aio_suspend.c']
];

const ul = document.createElement('ul');
ul.id = 'chat-list';
document.body.insertBefore(ul, document.getElementById('before-chat'));
appendChatList(ul, log1);
document.getElementById('lobby').onclick = lobby;
document.getElementById('load-more').onclick = loadMore;
document.getElementById('refresh').onclick = refresh;
document.getElementById('send-text').onclick = sendText;
document.getElementById('send-image').onclick = sendImage;
document.getElementById('send-file').onclick = sendFile;
