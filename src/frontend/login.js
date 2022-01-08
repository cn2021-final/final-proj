document.getElementById('send-my-username').onclick = function() {
  username = document.getElementById('my-username').value;
  if (1 <= username.length && username.length <= 32) {
    document.location = './lobby.html?username=' + username;
  } else
    alert('Name should be of length 1 ~ 32');
}
