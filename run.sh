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
if [ "$start_stage" == "synth" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado synthesis..."
    vivado -mode batch -source $SYNTH_TCL -nolog -nojournal
    check_exit_status "Vivado synthesis"
    echo "Vivado synthesis completed. Check 'synthesized.dcp'."
fi

# Gradle Build Stage (replaces Java Compilation)
if [ "$start_stage" == "compile" ] || [ "$start_stage" == "all" ]; then
    echo "Building Java project with Gradle..."
    cd $PROJ_DIR/java
    gradle build
    check_exit_status "Gradle build"
    echo "Gradle build completed."
fi

# Gradle Run Stage (replaces Java Placement)
if [ "$start_stage" == "place" ] || [ "$start_stage" == "all" ]; then
    rm $PROJ_DIR/outputs/printout/*.txt
    echo "Running Java placement with Gradle..."
    cd $PROJ_DIR/java
    gradle run
    check_exit_status "Gradle run"
    echo "Java placement executed. Check 'logger.txt' for output."
fi

# Return to outer dir
cd $PROJ_DIR

# Vivado Route Stage
if [ "$start_stage" == "route" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado route..."
    vivado -mode batch -source $ROUTE_TCL -nolog -nojournal
    check_exit_status "Vivado route"
    echo "Vivado route completed. Check 'routed.dcp'."
fi
