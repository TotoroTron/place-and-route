#!/bin/bash

# export RAPIDWRIGHT_PATH=/home/bcheng/workspace/tools/RapidWright
# export CLASSPATH=$RAPIDWRIGHT_PATH/bin:$RAPIDWRIGHT_PATH/jars/*

# project_dir="/home/bcheng/workspace/dev/place-and-route/hdl/vhdl/counter"

set project_dir "/home/bcheng/workspace/dev/place-and-route/"
vivado_synth_tcl = "$project_dir/tcl/vivado_synth.tcl" 
rapidwright_py = "$project_dir/python/rapidwright.py"
vivado_route_tcl = "$project_dir/tcl/vivado_route.tcl"



echo "Running Vivado synthesis..."
vivado -mode batch -source $vivado_synth_tcl
if [ $? -ne 0 ]; then
    echo "Vivado synthesis failed."
    exit 1
fi


echo "Running RapidWright placement..."
python3 $rapidwright_py
if [ $? -ne 0 ]; then
    echo "RapidWright placement failed."
    exit 1
fi


echo "Running Vivado routing and bitstream generation..."
vivado -mode batch -source $vivado_route_tcl
if [ $? -ne 0]; then
    echo "Vivado routing and bitstream failed."
    exit 1
fi


echo "Full flow completes successfully."
