@echo off
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xms1024m -Xmx2g -jar ASimPlatform.jar 
