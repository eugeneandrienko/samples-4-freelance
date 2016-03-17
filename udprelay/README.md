There is a sample udprelay program.

How to test it fast, using netcat utility:
1. Compile program
2. In terminal, execute nc process, which will act as our UDP server. Use the
next command for it: `netcat -u -l 5555`. So, our server has address 127.0.0.1
and port 5555.
3. In other terminal execute our udprelay with the next command:
`java com.freelancer.Main 5555 127.0.0.1 5556 5557`. There are four command-line
arguments - server port and server IP address. Port for client connection (5556).
And port (5557) from which client's messages will be sent to our server.
4. In other terminal execute nc process, which will act as our client. Use
the next command: `netcat -u 5556`. As you see, client connects to our relay.
5. Type some letters in last terminal window and press Enter. The same letters
should appear in the terminal window with first instance of nc.
6. Switch to that terminal and type some another letters in it and press Enter.
The same letters should appear in the terminal window with nc acts as client.

All done! You can exit from nc and our udprelay by pressing Ctrl+C in all
corresponding terminal windows.
