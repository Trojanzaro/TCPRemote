# TCPRemote

This is an app for android phones which will allow to send TCP messages to a server in Python for a Raspberry Pi or C/C++ for Arduino

The app has many buttons that look and function like a remote control.

Every time a single button is clicked a new TCP Socket is started and sends a command message to the server which will use those commands in order to power things such as mechanical or solid state relays, motors, servos and many other devices whih will all be connected to the same HUB.
The thinking is that instead of creating many different "internet of things" devices with one IP each, we can add many devices to one ip and manage them all through the sae remote just like the TV at home.