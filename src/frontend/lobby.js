import { createText, createButton, getUsername } from './common.js';
function createFriendListItem(name) {
  const li = document.createElement('li');
  li.appendChild(createText(name));
  li.appendChild(createButton('chat', chat(name)));
  li.appendChild(createButton('delete', del(li, name)));
  return li;
}

function createFriendList(usernameList) {
  const ul = document.createElement('ul');
  ul.id = 'friend-list';
  
  for (const name of usernameList) {
    ul.appendChild(createFriendListItem(name));
  }
  return ul;
}

function chat(partner) {
  return () => {
    document.location = './chat.html';
  }
}

function del(li, partner) {
  return () => {
    li.parentElement.removeChild(li);
    // TODO: Use post to update the server side friend list
  }
}

function add(ul) {
  return () => {
    const name = document.getElementById('added-username').value;
    if (name.length >= 1 && name.length <= 32) {
      ul.appendChild(createFriendListItem(name));
      // TODO: Use post to update the server side friend list
    }
  }
}


const ul = createFriendList(['Titus', 'JGR']); // TODO: use the response to build list
document.body.appendChild(ul);
document.getElementById('send-added-username').onclick = add(ul);
