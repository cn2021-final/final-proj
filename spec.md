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

- a text file

- earliest history first

```
[U/R] T [username] [content] - normal text
[U/R] I [username] [filename] - image file
[U/R] B [username] [filename] - binary data
U/R - unread / read
```

## implementation details

### connection setup

- create a socket [DONE]

- create a directory for server: `server/` [DONE]

- create a new thread for every new client [DONE]

## login

- listen for a UTF username, create a new directory if not exist [DONE]
- proceed to lobby [WORKING]

## lobby

- listen for an integer, indicating the mode to perform [WORKING]

```
1 - list friends
2 [username] - chat with a friend
3 [username] - add a friend
4 [username] - delete a friend
5 - quit
```

## list friends

- write a `\n`-separated list of usernames [WORKING]

- go back to lobby [WORKING]

## chat with a friend

- maintain an integer `lastRead` (or any name of choice), indicating the earliest chat history read by this session

- write an integer as a response

```
-1 - not friends / user doesn't exist
N - the number of unread messages
```

- if `N > 0`, write N lines of history

- otherwise, go back to lobby

- set `lastRead` to `len(history)-N`

- listen for an integer, indicating the operation

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

- listen for a string, and write to the chat history of the two parties. For format, refer to *#chat history format*

- listen for the next opreation

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

- create a friend directory, and an empty chat history [WORKING]

- do the same in the friend's directory [WORKING]

- write an integer as a response [WORKING]

```
1 - success
2 - already friends
3 - user doesn't exist
```

- go back to lobby

## delete a friend

- remove the friend's directory [WORKING]

- remove the user's directory in the friend's friend list [WORKING]

- write an integer as a response [WORKING]

```
1 - success
2 - not friends / user doesn't exist
```

- go back to lobby [WORKING]

## quit

- close the connection

# client spec

## connection setup / login

- create a socket

- prompt for a string for the username [DONE]

- write to the server [DONE]

## lobby

- prompt for a command

```
list
chat [username]
add [username]
delete [username]
quit
```

- if the command is invalid, go to the lobby

- otherwise, send the command to the server. The format is specified in server spec

## list

- read the list of friends and print it out [WORKING]

- go back to the lobby [WORKING]

## chat

- receive the number of unread messages

- receive the messages

- prompt for the command 

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

- send the text to the server. For the format, refer to *#text message* in server spec

- proceed to check new messages

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

- receive the response from the server [WORKING]

- go back to lobby [WORKING]

## delete

- receive the response from the server [WORKING]

- go back to lobby [WORKING]

## quit

- close the connection with the server

- exit