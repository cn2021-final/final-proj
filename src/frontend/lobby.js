import { createText, createButton, getUsername } from './common.js';
function createFriendListItem(name) {
  const li = document.createElement('li');
  li.appendChild(createText(name));
  li.appendChild(createButton('chat', chat(name)));
  li.appendChild(createButton('delete', del(name)));
  return li;
}

function updateFriendList(response) {
  let ul = document.getElementById('friend-list');
  if (ul !== null)
    ul.parentElement.removeChild(ul);
  ul = document.createElement('ul');
  ul.id = 'friend-list';
  let usernameList = response;
  console.log(typeof(usernameList));
  console.log(usernameList);
  
  for (const name of usernameList) {
    if (name.length === 0) continue;
    ul.appendChild(createFriendListItem(name));
  }
  document.body.appendChild(ul);
}

function renew(firstTime=false) {
  fetch('/renew', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      sender: getUsername(),
      receiver: ''
    })
  })
    .then((response) => response.json())
    .then(updateFriendList)
    .catch((reason) => console.log(reason));
}

function chat(partner) {
  return () => {
    localStorage.setItem('partner', partner);
    document.location = './chat.html';
  }
}

function add() {
  return () => {
    const name = document.getElementById('added-username').value;
    if (name.length >= 1 && name.length <= 32) {
      fetch('/add', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          sender: getUsername(),
          receiver: name
        })
      })
        .then((response) => response.json())
        .then(updateFriendList)
        .catch((reason) => console.log(reason));
    }
  }
}

function del(partner) {
  return () => {
    fetch('/delete', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
        sender: getUsername(),
        receiver: partner
      })
    })
      .then((response) => response.json())
      .then(updateFriendList)
      .catch((reason) => console.log(reason));
  }
}

renew();
const updateInterval = 10000;
document.getElementById('renew').onclick = renew;
document.getElementById('send-added-username').onclick = add();

setInterval(renew, updateInterval);
