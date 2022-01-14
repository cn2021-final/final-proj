import { createText, createButton, getUsername, postJSON } from './common.js';
function createFriendListItem(name) {
  const li = document.createElement('li');
  li.appendChild(createText(name));
  li.appendChild(createButton('chat', chat(name)));
  li.appendChild(createButton('delete', del(li, name)));
  return li;
}

function createFriendList() {
  const ul = document.createElement('ul');
  ul.id = 'friend-list';

  usernameList = JSON.parse(this.responseText);
  
  for (const name of usernameList) {
    ul.appendChild(createFriendListItem(name));
  }
  document.body.appendChild(ul);
}

function updateFriendList() {
  const ul = document.getElementById('friend-list');

  usernameList = JSON.parse(this.responseText);
  
  for (const name of usernameList) {
    ul.appendChild(createFriendListItem(name));
  }
  document.body.appendChild(ul);
}

function chat(partner) {
  return () => {
    localStorage.setItem('partner', partner);
    document.location = './chat.html';
  }
}

function del(li, partner) {
  return () => {
    li.parentElement.removeChild(li);
    postJSON('delete', JSON.stringify({'sender': getUsername(), 'receiver': partner}), updateFriendList);
  }
}

function add() {
  return () => {
    const name = document.getElementById('added-username').value;
    if (name.length >= 1 && name.length <= 32) {
      postJSON('add', JSON.stringify({'sender': getUsername(), 'receiver': name}), updateFriendList);
    }
  }
}


postJSON('renew', JSON.stringify({'sender': getUsername(), 'receiver': '' }), createFriendList);

document.getElementById('send-added-username').onclick = add();
