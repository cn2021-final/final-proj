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
    
    - chat history - hard linked to [friend name]/[username]/chat history
    
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

- create a directory for server: `server/`

- create a new thread for every new client [DONE]

## login

- listen for a UTF username, create a new directory if not exist

## lobby

- listen for an integer, indicating the mode to perform

```
1 - list friends
2 [username] - chat with a friend
3 [username] - add a friend
4 [username] - delete a friend
```

## list friends

- write a `\n`-separated list of usernames

- go back to lobby

## chat with a friend

- maintain an integer `lastRead` (or any name of choice), indicating the earliest chat history read by this session

- write an integer as a response

```
-1 - not friends / user doesn't exist
N - the number of available messages, N = max(10, len(history))
```

- if `N > 0`, write N lines of history

- otherwise, go back to lobby

- set `lastRead` to `len(history)-N`

- listen for an integer, indicating the operation

```
1 - text message
2 - image
3 - binary data
4 - get image / binary data
5 - more history
6 - check for new messages
7 - exit
```

### text message

- listen for a string, and write to the chat history. For format, refer to *#chat history format*

- listen for the next opreation

### image / binary data

- generate a random string for the filename, preserve the extension (if any), make sure the filename doesn't exist, etc

- save the data, write to the chat history. For format, refer to *#chat history format*

- write a line as a response

```
[filename]
```

- listen for the next operation

### get image / binary data

- write an integer as a response

```
-1 - file doesn't exist
1 - file exists
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

- create a friend directory, and an empty chat history

- create the same directory in the friend's directory, and hard-link the chat history

- write an integer as a response

```
1 - success
2 - already friends
3 - user doesn't exist
```

- go back to lobby

## delete a friend

- remove the friend's directory

- remove the user's directory in the friend's friend list

- write an integer as a response

```
1 - success
2 - not friends / user doesn't exist
```

- go back to lobby

# client spec

TODO