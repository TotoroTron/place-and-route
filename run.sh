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
ROUTE_TCL="$PROJ_DIR/tcl/route.tcl"
SIM_TCL="$PROJ_DIR/tcl/sim.tcl"

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

# Post-Implementation Timing Simulation
if [ "$start_stage" == "sim" ] || [ "$start_stage" == "all" ]; then
    echo "Running Post-Implementation Timing Simulation..."
    vivado -mode batch -source $SIM_TCL -nolog -nojournal
    check_exit_status "Vivado sim"
    echo "Timing SDF and verilog file generated. Check '${TOP_LEVEL}_time_impl.sdf"

    DESIGN_DIR="$PROJ_DIR/hdl/verilog/${DESIGN}"

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

    xvlog "${TOP_LEVEL}_time_impl.v"
    xvlog "$XILINX_VIVADO/data/verilog/src/glbl.v"
    xvlog -sv "$DESIGN_DIR/verif/tb_${TOP_LEVEL}.sv"
    # xvlog -sv "$PROJ_DIR/hdl/vhdl/counter/counter.srcs/sim_1/new/tb_postroute.sv"

    xelab \
        -debug typical -relax -mt 8 -maxdelay \
        -L xpm -L xil_defaultlib -L uvm -L secureip -L unisims_ver -L simprims_ver \
        -transport_int_delays \
        -pulse_r 0 -pulse_int_r 0 -pulse_int_e 0 \
        -snapshot "${TOP_LEVEL}_time_impl" -top "tb_top_level" \
        -sdfroot "$DESIGN_DIR/sim_postroute/${TOP_LEVEL}_time_impl.sdf" \
        -log elaborate.log \
        glbl

    xsim ${TOP_LEVEL}_time_impl -tclbatch xsim_cfg.tcl
    xsim ${TOP_LEVEL}_time_impl.wdb -gui -tclbatch waveform.tcl
    # source /home/bcheng/workspace/tools/oss-cad-suite/environment
    # gtkwave waveform.vcd
fi

# Return to outer dir
cd $PROJ_DIR

# xvlog --incr --relax -L uvm -prj tb_counter_vlog.prj
# xsim tb_counter_time_impl -key {Post-Implementation:sim_1:Timing:tb_counter} -tclbatch tb_counter.tcl -log simulate.log
# xsim tb_counter_time_impl --tclbatch xsim_cfg.tcl -log simulate.log
# xsim tb_counter_time_impl -gui -downgrade_fatal2warning -downgrade_error2warning -tl -tp -log simulate.log
