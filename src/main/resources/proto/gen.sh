#!/usr/bin/env bash
echo "start to protoc file"

currentPath=$(pwd)
echo "current path is : "$currentPath

cd ../../java
targetPath=$(pwd)
echo "target path is : "$targetPath

cd $currentPath

for dirlist in $(ls *.proto)
do
    protoc --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java --java_out=${targetPath} --grpc-java_out=${targetPath} ${dirlist}
    echo "finish proto : "${dirlist}
done