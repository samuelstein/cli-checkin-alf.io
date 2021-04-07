# Alf.io Check-in CLI App

Small commandline app to be used for check-in people via the great [alf.io](https://alf.io) ticket reservation system.

## Requirements
- Java 11
- USB QR-Code Scanner as HID with **German Keyboard** Layout

## Options
```
-e,--event <arg>   event name   
-k,--key <arg>     api key   
-l,--list          display upcoming events   
-u,--url <arg>     alf.io ticket system url
```

## Run
```
java -jar alf-io-cli-app -e <event_name> -k <alf.io_api_key> -u <alf.io_system _url
```
