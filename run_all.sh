#!/bin/bash

export XILINX_VIVADO=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2
export PATH="$PATH:$XILINX_VIVADO/bin"

export JAVA_HOME=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2/tps/lnx64/jre17.0.7_7
export PATH="$PATH:$JAVA_HOME/bin"

export RAPIDWRIGHT_PATH=/home/bcheng/workspace/tools/RapidWright
export PATH="$PATH:$RAPIDWRIGHT_PATH/bin"
export CLASSPATH=$RAPIDWRIGHT_PATH/bin:$RAPIDWRIGHT_PATH/jars/*
export _JAVA_OPTIONS=-Xmx32736m

PROJ_DIR="/home/bcheng/workspace/dev/place-and-route"
SYNTH_TCL="$PROJ_DIR/tcl/synth.tcl"
ROUTE_TCL="$PROJ_DIR/tcl/route.tcl"


echo "Running Vivado synthesis..."
vivado -mode batch -source $SYNTH_TCL -nolog -nojournal
if [$? -ne 0]; then
    echo "Vivado synthesis failed."
    exit 1
fi
echo "Vivado synthesis completed. Check 'synthesized.dcp'."


echo "Compiling Java files..."
javac src/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi
echo "Java compilation completed."


echo "Running Java placement..."
java -cp "$CLASSPATH:." src.Main
if [ $? -ne 0 ]; then
    echo "Java placement execution failed."
    exit 1
fi
echo "Java placement executed. Check 'logger.txt' for output."


echo "Running Vivado route..."
vivado -mode batch -source $ROUTE_TCL -nolog -nojournal
if [$? -ne 0]; then
    echo "Vivado route failed."
    exit 1
fi
echo "Vivado route completed. Check 'routed.dcp'."
