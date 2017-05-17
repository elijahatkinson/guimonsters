===============================
Running the Server in Windows
===============================

In order to see the server in action,
make sure port 7777 is forwarded for TCP in your local network.

Then run MudServer.bat.  If MudServer.bat doesnt launch the server in
a new windows command prompt then you need to manually execute the GuiMonstersServer.jar from the command line.

To do this open a new command window, navigate to the directory that this readme is in and type:
java -jar GuiMonstersServer.jar

If this command fails, then you dont have the proper java environment variables set up.
In this case, you need to copy GuiMonstersServer.jar into the directory where java is installed:

Something similar to:
C:\Program Files\Java\jdk1.7.0_17\bin

Launch a command window and navigate to the above directory and then execute the command:
java -jar GuiMonstersServer.jar

================================
Running the Client in Windows
================================
Double click on the GuiMonstersClient.jar file.
If it doesnt run, copy it to your java install directory (same steps you performed with the server jar).
Then launch a new command window and navigate to your java install directory.

Finally execute the command:
java -jar GuiMonstersClient.jar