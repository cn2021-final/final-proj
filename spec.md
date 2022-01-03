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

- beginning: long integer, offset of the last unread message. If it's set to the end of the file, every messages are read.

- earliest history first

```
1 [username] [content] - normal text
2 [username] [filename] - image file
3 [username] [filename] - binary data
```

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
2 [filesize] - image
3 [filesize] - binary data
4 - get image / binary data
5 - more history
6 - check for new messages
7 - exit
```

### text message

- listen for a string, and write to the chat history of the two parties. For format, refer to *#chat history format* [DONE]

- listen for the next opreation [DONE]

### image / binary data

- generate a random string for the filename, preserve the extension (if any), make sure the filename doesn't exist, etc

- save the data, write to the chat history. For format, refer to *#chat history format*

- listen for the next operation

### get image / binary data

- write an integer as a response

```
-1 - file doesn't exist
N - file exists, N = file size
```

- if the file exists, write the file

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

- write an integer as a response. for the definition of U, refer to *#chat history format*

```
N - the number of available messages, N = len(U-messages)
```

- set `lastRead` to `len(history)`, even if `N = 0`

- if `N > 0`, write N lines of history

- listen for the next operation

### exit

- go back to lobby

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

- close the connection

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

- check if the file exists (relative path)

- send the filesize to the server

- dump the binary data to the server

- proceed to check new messages

### get image / binary data

- receive the filesize

- receive the data

- proceed to prompt for new commands

### more history

- read the length of history

- read the histories, then prompt for new commands

### new messages

- read the length of messages

- receive the data, and print them out

- proceed to prompt for new commands

### quit

- go back to lobby

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

#### list friends

- input: username

- output: a `\n`-separated list of usernames

#### add

- input: adder username, added username

- output: 1 (previously not friends), 2 (were friends), 3 (user doesn't exist)

#### delete

- input: deleter username, deleted username

- output: refer to add

### chat with a friend

#### text message

- input: sender username, receiver username, message

- output: none

#### image / binary data

- input: sender username, receiver username, filename

- output: none

#### read history

- input: sender username, receiver username

- output: all unread histories

- update the range of read history

#### more history

- input: sender username, receiver username

- output: max(10, len(available unread history before first read history)) lines of unread history right before the first read history

- update the range of read history

#### get image / binary data

- input: sender username, receiver username, filename

- output: the address of the file on the server, or some error if the upload is not done or not found

## interface

### lobby

- list all the friends, click on a name to select further operations

#### delete

- delete the selected friend and refresh the friends list

#### add

- input a username, and add it if it exists and isn't previously a friend

#### chat

- go to the chats page

### chatting

#### text

- input text in a box and press the button to send

- reload the latest history

#### image / binary data

- click a button, select the file to upload

- reload the latest history

#### new messages

- reload the latest history

#### more history

- found when a user scrolls up the chat history

- load unloaded history right before the first loaded history

#### back to lobby

- go back to the lobby page
