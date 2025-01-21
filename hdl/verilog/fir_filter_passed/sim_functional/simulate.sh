#!/bin/bash

DESIGN=fir_filter
TOP_LEVEL=top_level

FILTER_DEPTH=256
NUM_PIPELINES=8

check_exit_status() {
    if [ $? -ne 0 ]; then
        echo "Error: $1 failed. Exiting."
        exit 1
    fi
}

PROJ_DIR=/home/bcheng/workspace/dev/place-and-route/
DESIGN_DIR="$PROJ_DIR/hdl/verilog/${DESIGN}"

echo "Running Functional Simulation..."

# generate sine.mem and weights.mem
cd "$DESIGN_DIR/python"
python3 sine.py
python3 weights.py "$FILTER_DEPTH" "$NUM_PIPELINES"
python3 generate_xpm_spram.py "$NUM_PIPELINES"

cd "$DESIGN_DIR/sim_functional"
cat <<EOL >xsim_cfg.tcl
log_wave -recursive *
run all
exit
EOL
cat <<EOL >waveform.tcl
create_wave_config; add_wave /; set_property needs_save false [current_wave_config]
EOL

cd "$DESIGN_DIR/sim_functional"
# Read source files and log
src_files=("$DESIGN_DIR"/src/*.{v,sv})
for file in "${src_files[@]}"; do
    if [ -f "$file" ]; then
        if [[ "$file" == *.sv ]]; then
            xvlog -sv "$file"
        else
            xvlog "$file"
        fi
        check_exit_status "xvlog for $file"
    fi
done

# Read verification files and log
verif_files=("$DESIGN_DIR"/verif/*.sv)
for file in "${verif_files[@]}"; do
    if [ -f "$file" ]; then
        xvlog -sv "$file"
        check_exit_status "xvlog for $file"
    fi
done

# Elaboration
xelab -debug typical -top "tb_$TOP_LEVEL" -snapshot my_tb_snap \
    -timescale 1ps/1ps \
    -L xpm # -L xil_defaultlib -L uvm -L secureip -L unisims_ver -L simprims_ver

check_exit_status "xelab"

# Simulation
xsim my_tb_snap --tclbatch xsim_cfg.tcl
check_exit_status "xsim"

# Open the wavefile in Vivado
xsim my_tb_snap.wdb -gui -tclbatch waveform.tcl
