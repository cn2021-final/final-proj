build:
	cd src && \
	javac Server.java -d ../bin && \
	javac Client.java -d ../bin && \
	javac -cp .:../lib/json-java.jar WebServer.java -d ../bin && \
	cp server.sh ../server && \
	cp client.sh ../client && \
	cp web.sh ../web

clean:
	rm -rf bin/* server client web serverDir libDir
