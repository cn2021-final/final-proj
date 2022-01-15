document.getElementById('send-my-username').onclick = function() {
  username = document.getElementById('my-username').value;
  if (1 <= username.length && username.length <= 32) {
    localStorage.setItem('username', username);
    fetch('/login', {
      method: 'POST',
      body: JSON.stringify({
        sender: username
      })
    }).then(function () {
      document.location = './lobby.html';
    });
  } else
    alert('Name should be of length 1 ~ 32');
}
