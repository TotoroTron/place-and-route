#!/bin/bash

export XILINX_VIVADO=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2
export PATH="$PATH:$XILINX_VIVADO/bin"

export JAVA_HOME=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2/tps/lnx64/jre17.0.7_7
export PATH="$PATH:$JAVA_HOME/bin"

export RAPIDWRIGHT_PATH=/home/bcheng/workspace/tools/RapidWright
export PATH="$PATH:$RAPIDWRIGHT_PATH/bin"
export CLASSPATH=$RAPIDWRIGHT_PATH/bin:$RAPIDWRIGHT_PATH/jars/*
export _JAVA_OPTIONS=-Xmx32736m



echo "Compiling Java files..."
javac src/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi


echo "Compilation successful."


echo "Running the Main class..."
java -cp "$CLASSPATH:." src.Main
if [ $? -ne 0 ]; then
    echo "Execution failed."
    exit 1
fi


echo "Execution successful. Check 'logger.txt' for output."
