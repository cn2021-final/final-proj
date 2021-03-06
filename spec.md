# collaborating

- before implementing a line of spec, mark it as [WORKING] and push it ASAP

- after implementing, mark it as [DONE]

## example

- this feature is under construction [WORKING]

- this feature is done [DONE]

# server spec

## directories

- [username]
  
  - [friend name]
    
    - chat history
    
    - shared files

## chat history format

- a binary file, handled with java RandomAccessFile class

- beginning: 
  
  - long integer, offset of the last unread message. If it's set to the end of the file, every messages are read.
  
  - long integer, offset of the last message

- earliest history first

```
[offset] 1 [username] [content] - normal text
[offset] 2 [username] [filename] - image file
[offset] 3 [username] [filename] - binary data
```

- offset: offset of the last message, long integer

## implementation details

### connection setup

- create a socket [DONE]

- create a directory for server: `server/` [DONE]

- create a new thread for every new client [DONE]

## login

- listen for a UTF username, create a new directory if not exist [DONE]
- proceed to lobby [DONE]

## lobby

- listen for an integer, indicating the mode to perform [DONE]

```
1 - list friends
2 [username] - chat with a friend
3 [username] - add a friend
4 [username] - delete a friend
5 - quit
```

## list friends

- write a `\n`-separated list of usernames [DONE]

- go back to lobby [DONE]

## chat with a friend

- maintain an integer `lastRead` (or any name of choice), indicating the earliest chat history read by this session [WORKING]

- write an integer as a response [DONE]

```
1 - not friends / user doesn't exist
2 - success
```

- if `N = 2`, proceed [DONE]

- otherwise, go back to lobby [DONE]

- listen for an integer, indicating the operation [DONE]

```
1 - text message
2 [suffix] [filesize] - image
3 [suffix] [filesize] - binary data
4 - get image / binary data
5 - more history
6 - check for new messages
7 - exit
```

### text message

- listen for a string, and write to the chat history of the two parties. For format, refer to *#chat history format* [DONE]

- listen for the next opreation [DONE]

### image / binary data

- generate a random string for the filename, preserve the extension (if any), make sure the filename doesn't exist, etc [DONE]

- save the data, write to the chat history. For format, refer to *#chat history format* [DONE]

- listen for the next operation [DONE]

### get image / binary data

- write an integer as a response [DONE]

```
-1 - file doesn't exist
N - file exists, N = file size
```

- if the file exists, write the file [DONE]

### more history

- write an integer as a response

```
-1 - no more history
N - the number of available messages, N = max(10, len(available history))
```

- if `N > 0`, write N lines of history

- `lastRead -= N`

- listen for the next operation

### check for new messages

- write an integer as a response. for the definition of U, refer to *#chat history format* [DONE]

```
N - the number of available messages, N = len(U-messages)
```

- set `lastRead` to `len(history)`, even if `N = 0` [DONE]

- if `N > 0`, write N lines of history [DONE]

- listen for the next operation [DONE]

### exit

- go back to lobby [DONE]

## add a friend

- create a friend directory, and an empty chat history [DONE]

- do the same in the friend's directory [DONE]

- write an integer as a response [DONE]

```
1 - previously not friends
2 - were previously friends
3 - user doesn't exist
```

- go back to lobby

## delete a friend

- remove the friend's directory [DONE]

- remove the user's directory in the friend's directory [DONE]

- write an integer as a response. For the format, refer to *#add a friend* [DONE]

- go back to lobby [DONE]

## quit

- close the connection [DONE]

# client spec

## connection setup / login

- create a socket [DONE]

- prompt for a string for the username [DONE]

- write to the server [DONE]

## lobby

- prompt for a command [DONE]

```
list
chat [username]
add [username]
delete [username]
quit
```

- if the command is invalid, go back to the lobby [DONE]

- otherwise, send the command to the server. The format is specified in server spec [DONE]

## list

- read the list of friends and print it out [DONE]

- go back to the lobby [DONE]

## chat

- fetch new messages [DONE]

- prompt for the command [DONE]

```
[text] - message
/i [filename] - image
/d [filename] - binary data
/g [filename] - get image / binary data
/l - more history
/r - check for new messages
/q - quit
```

### text message

- send the text to the server. For the format, refer to *#text message* in server spec [DONE]

- proceed to check new messages [DONE]

### image / binary data

- check if the file exists (relative path) [DONE]

- send the suffix to the server. For files without a suffix, send an empty string. [DONE]

- send the filesize to the server [DONE]

- dump the binary data to the server [DONE]

- proceed to check new messages [DONE]

### get image / binary data

- receive the filesize [DONE]

- receive the data [DONE]

- proceed to prompt for new commands [DONE]

### more history

- read the length of history [WORKING]

- read the histories, then prompt for new commands [WORKING]

### new messages

- read the length of messages [DONE]

- receive the data, and print them out [DONE]

- proceed to prompt for new commands [DONE]

### quit

- go back to lobby [DONE]

## add

- receive the response from the server [DONE]

- go back to lobby [DONE]

## delete

- receive the response from the server [DONE]

- go back to lobby [DONE]

## quit

- close the connection with the server [DONE]

- exit [DONE]

# web server

## client library

### lobby

#### list friends [WORKING]

- input: username

- output: a list of usernames

#### add [WORKING]

- input: adder username, added username

- output: 1 (previously not friends), 2 (were friends), 3 (user doesn't exist)

#### delete [WORKING]

- input: deleter username, deleted username

- output: refer to add

### chat with a friend

#### text message [WORKING]

- input: sender username, receiver username, message

- output: none

#### image / binary data [WORKING]

- input: sender username, receiver username, filename

- output: none

#### read history [WORKING]

- input: sender username, receiver username

- output: all unread histories

- update the range of read history

#### more history

- input: sender username, receiver username

- output: max(10, len(available unread history before first read history)) lines of unread history right before the first read history

- update the range of read history

#### get image / binary data [WORKING]

- input: sender username, receiver username, filename

- output: the address of the file on the server, or some error if the upload is not done or not found

## interface [WORKING]

- Used [GitHub - stleary/JSON-java: A reference implementation of a JSON package in Java.](https://github.com/stleary/JSON-java.git), so you need to clone the repo and place it at the same directory as this repo.

- Use `localStorage.setItem()` and `localStorage.getItem()` to store username.

### login

- Accept a username of length 1~32, use a post request to indicate a user login (for new user creation), and use a get request to request the corresponding lobby page
- request: `./login`
- ```json
  {
      "sender": "<sender username>"
  }
  ```

### lobby

- list all the friends, click the buttons next to a name to select further operations

#### friend list response format

```json
[
  "<friend-username1>",
  "<friend-username2>",
  ...
]
```

#### renew

- retrieve the latest friend list

- should send a post request indicating the asking user, and update the list according to the response

- request: `./renew`

```json
{
    "sender": "<sender username>",
    "receiver": ""
}
```

#### delete

- delete the selected friend and refresh the friends list according the response
- should send a post request indicating the deleter and the deleted
- request: `./delete`

```json
{
    "sender": "<sender username>",
    "receiver": "<receiver username>"
}
```

#### add

- input a username, update the server side friend list, and refresh the friends list according to the response
- should send a post request indicating the adder and the added
- request: `./add`

```json
{
    "sender": "<sender username>",
    "receiver": "<receiver username>"
}
```

#### chat

- go to the chats page by requesting the chat page of the user and the other user
- Should first refresh, and check if the other user is still a friend. Then, send a get request indicating the current user and the chatting partner if they are a friend of the current user. This request gets the chatting page.

### chatting

#### chat history response format

```json
[
        [1, sender, content],
        [2, sender, image address on server],
        [3, sender, file address on server],
        [2, sender, image address on server],
        ...
]
```

#### text

- input text in a box and press the button to send

- reload the latest history

- should send a post request indicating the current user, the chatting friend, and the message content

- request: `./send-text`

- ```json
  {
      "sender": "<sender username>",
      "receiver": "<receiver username>",
      "content": "<message content>"
  }
  ```

#### image / binary data

- click a button, select the file to upload
- reload the latest history
- should send one post request indicating the current user, the chatting friend, and the file
- request: `/send-file/<sender path>/<receiver path>/<filename>`
- raw binary file

#### new messages

- reload the latest history

- should send a get request indicating the current user and the chatting friend

- parse the response to get the chat history, or error message if the chatting partner is not a friend

- request: `./refresh`

- ```json
  {
      "sender": "<sender username>",
      "receiver": "<receiver username>"
  }
  ```

#### more history

- found when a user scrolls up the chat history

- load unloaded history right before the first loaded history

- Should send a get request indicating the current user, the chatting friend, the offset, and the number of logs to request

- parse the response to get the chat history  and save the offset (offset is initially -1)

- response: `{"offset": "<offset>", "history": [<chat history>]}`

- request: `./more-history`

- ```json
  {
      "sender": "<sender username>",
      "receiver": "<receiver username>",
      "offset": "<offset>",
      "count": "<count>"
  }
  ```

#### back to lobby

- go back to the lobby page
- should send a get request indicating the current user then gets the lobby page
