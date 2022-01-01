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
T [content] - normal text
I [filename] - image file
B [filename] - binary data
```

## implementation details

### connection setup

- read the ip address from argv

- create a socket

- for a client, use one coroutine

# client spec

TODO