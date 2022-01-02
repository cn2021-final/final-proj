build:
	cd src && \
	javac Server.java -d ../bin && \
	javac Client.java -d ../bin && \
	cp server.sh ../server && \
	cp client.sh ../client

clean:
	rm -rf bin/* server client