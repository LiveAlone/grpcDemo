@echo off
echo --------------start gen idls---------------
SetLocal EnableDelayedExpansion   

CD %~dp0
SET CONFIG_PATH=%~dp0
CD ../../java
SET GEN_PATH=%cd%
CD %CONFIG_PATH%

FOR /D %%i IN (*) DO (IF EXIST %%i/*.proto (protoc --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java --grpc-java_out=%GEN_PATH% --proto_path=proto %%i/*.proto) & echo %%i done)

EndLocal

echo --------------idls done---------------

PAUSE