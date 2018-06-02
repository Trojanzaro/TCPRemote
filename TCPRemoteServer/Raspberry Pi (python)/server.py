import RPi.GPIO as GPIO
import socket as socket

GPIO.setmode(1, BCM);


server = socket.socket(socket.AF_NET, socket,SOCK_STREAM);
server.bind((gethostname(), 1234));
server.listen();

while True:
	(client, addr) = server.acept();
	
	