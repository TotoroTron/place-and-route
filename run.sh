#!/bin/bash

export XILINX_VIVADO=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2
export PATH="$PATH:$XILINX_VIVADO/bin"

export JAVA_HOME=/home/bcheng/workspace/tools/Xilinx/Vivado/2023.2/tps/lnx64/jre17.0.7_7
export PATH="$JAVA_HOME/bin:$PATH"

export RAPIDWRIGHT_PATH=/home/bcheng/workspace/tools/RapidWright
export PATH="$PATH:$RAPIDWRIGHT_PATH/bin"
export CLASSPATH=$RAPIDWRIGHT_PATH/bin:$RAPIDWRIGHT_PATH/jars/*
export _JAVA_OPTIONS=-Xmx32736m

PROJ_DIR="/home/bcheng/workspace/dev/place-and-route"
SYNTH_TCL="$PROJ_DIR/tcl/synth.tcl"
ROUTE_TCL="$PROJ_DIR/tcl/route.tcl"


start_stage=${1:-all} # Use first argument or defaults to all 

check_exit_status() {
    if [ $? -ne 0 ]; then
        echo "$1 failed."
        exit 1
    fi
}

# Vivado Synthesis Stage
if [ "$start_stage" == "synthesis" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado synthesis..."
    vivado -mode batch -source $SYNTH_TCL -nolog -nojournal
    check_exit_status "Vivado synthesis"
    echo "Vivado synthesis completed. Check 'synthesized.dcp'."
fi

# Java Compilation Stage
if [ "$start_stage" == "compile" ] || [ "$start_stage" == "all" ]; then
    echo "Compiling Java files..."
    javac src/*.java
    check_exit_status "Java compilation"
    echo "Java compilation completed."
fi

# Java Placement Stage
if [ "$start_stage" == "placement" ] || [ "$start_stage" == "all" ]; then
    echo "Running Java placement..."
    java -cp "$CLASSPATH:." src.Main
    check_exit_status "Java placement execution"
    echo "Java placement executed. Check 'logger.txt' for output."
fi

# Vivado Route Stage
if [ "$start_stage" == "route" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado route..."
    vivado -mode batch -source $ROUTE_TCL -nolog -nojournal
    check_exit_status "Vivado route"
    echo "Vivado route completed. Check 'routed.dcp'."
fi
