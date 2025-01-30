#!/bin/bash

DESIGN="fir_filter"
TOP_LEVEL="top_level"

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
PLACE_TCL="$PROJ_DIR/tcl/vivado_place.tcl"
ROUTE_TCL="$PROJ_DIR/tcl/vivado_route.tcl"
SIM_TCL="$PROJ_DIR/tcl/vivado_sim.tcl"

DESIGN_DIR="$PROJ_DIR/hdl/verilog/${DESIGN}"
TOP_PARAMS_FILE="$DESIGN_DIR/parameters_${TOP_LEVEL}.txt"
XELAB_TOP_PARAMS=""
SYNTH_TOP_PARAMS=""

if [[ ! -f "$TOP_PARAMS_FILE" ]]; then
    echo "Error: parameter file not found at $TOP_PARAMS_FILE"
    exit 1
fi
# read config file and construct xelab -generic_top arguments
while IFS= read -r line; do
    # skip empty lines or lines starting with comment (#)
    [[ -z "$line" || "$line" == \#* ]] && continue
    # append parameter as a -generic_top argument
    XELAB_TOP_PARAMS+="-generic_top $line "
    SYNTH_TOP_PARAMS+="-generic $line "
done <"$TOP_PARAMS_FILE"

start_stage=${1:-all} # Use first argument or defaults to all

check_exit_status() {
    if [ $? -ne 0 ]; then
        echo "$1 failed."
        exit 1
    fi
}

# Vivado Placement Stage
if [ "$start_stage" == "place" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado place..."
    vivado -mode batch -source $PLACE_TCL -nolog -nojournal
    check_exit_status "Vivado place"
    echo "Vivado place completed. Check 'vivado_placed.dcp'."
fi

# Vivado Route Stage
if [ "$start_stage" == "route" ] || [ "$start_stage" == "all" ]; then
    echo "Running Vivado route..."
    vivado -mode batch -source $ROUTE_TCL -nolog -nojournal
    check_exit_status "Vivado route"
    echo "Vivado route completed. Check 'vivado_routed.dcp'."
fi

# Post-Implementation Timing Simulation
if [ "$start_stage" == "sim_postroute" ] || [ "$start_stage" == "all" ]; then
    # cd "$DESIGN_DIR/sim_postroute"
    # rm -r *
    # cd $PROJ_DIR

    echo "Running Post-Implementation Timing Simulation..."
    vivado -mode batch -source $SIM_TCL -nolog -nojournal -tclargs $DESIGN $TOP_LEVEL
    check_exit_status "Vivado sim"

    cd "$DESIGN_DIR/python"
    python3 sine.py
    python3 weights.py

    cd "$DESIGN_DIR/sim_postroute"

    # xsim config tcl
    cat <<EOL >xsim_cfg.tcl
    log_wave -recursive *
    run all
    exit
EOL

    # waveform tcl
    cat <<EOL >waveform.tcl
    create_wave_config; add_wave /; set_property needs_save false [current_wave_config]
EOL

    echo "Beginning xvlog..."
    xvlog "${TOP_LEVEL}_time_impl.v"
    xvlog "$XILINX_VIVADO/data/verilog/src/glbl.v"
    xvlog -sv "$DESIGN_DIR/verif/tb_${TOP_LEVEL}.sv"
    # xvlog -sv "$PROJ_DIR/hdl/vhdl/counter/counter.srcs/sim_1/new/tb_postroute.sv"

    echo "Beginning xelab..."
    xelab \
        -debug typical -relax -mt 8 -maxdelay \
        -transport_int_delays \
        -pulse_r 0 -pulse_int_r 0 -pulse_int_e 0 \
        -timescale 1ns/1ps \
        -snapshot "${TOP_LEVEL}_time_impl" -top "tb_${TOP_LEVEL}" \
        -sdfroot "$DESIGN_DIR/sim_postroute/${TOP_LEVEL}_time_impl.sdf" \
        -log elaborate.log \
        -verbose 0 \
        $XELAB_TOP_PARAMS \
        glbl
    # -L xpm -L xil_defaultlib -L uvm -L secureip -L unisims_ver -L simprims_ver \

    echo "Beginning xsim..."
    xsim ${TOP_LEVEL}_time_impl -tclbatch xsim_cfg.tcl
    xsim ${TOP_LEVEL}_time_impl.wdb -gui -tclbatch waveform.tcl
    # source /home/bcheng/workspace/tools/oss-cad-suite/environment
    # gtkwave waveform.vcd
fi
