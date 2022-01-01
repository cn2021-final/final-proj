.ONESHELL:

all:
	make server
	make client
	make clean

server:
	cd src
	javac Server.java
	jar cfvm ../release/Server.jar server/Server.manifest Server.class */*.class

client:
	cd src
	javac Client.java
	jar cfvm ../release/Client.jar client/Client.manifest Client.class */*.class

clean:
	cd src
	find . -name '*.class' -exec rm -rf {} +