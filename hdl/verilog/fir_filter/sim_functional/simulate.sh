#!/bin/bash

DESIGN=fir_filter
TOP_LEVEL=top_level

check_status() {
    if [ $? -ne 0 ]; then
        echo "Error: $1 failed. Exiting."
        exit 1
    fi
}

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

root_dir="/home/bcheng/workspace/dev/place-and-route"
sim_dir="$root_dir/hdl/verilog/$DESIGN/sim_functional"
src_dir="$root_dir/hdl/verilog/$DESIGN/src"
verif_dir="$root_dir/hdl/verilog/$DESIGN/verif"

# generate sine.mem
cd $verif_dir
python3 sine.py

# generate weights.mem
cd $src_dir
python3 weights.py

cd $sim_dir

# Read source files and log
src_files=("$src_dir"/*.v)
for file in "${src_files[@]}"; do
    if [ -f "$file" ]; then
        xvlog "$file"
        check_status "xvlog for $file"
    fi
done

# Read verification files (SystemVerilog) and log
verif_files=("$verif_dir"/*.sv)
for file in "${verif_files[@]}"; do
    if [ -f "$file" ]; then
        xvlog -sv "$file"
        check_status "xvlog for $file"
    fi
done

# Elaboration
xelab -debug typical -top "tb_$TOP_LEVEL" -snapshot my_tb_snap \
    -timescale 10ns/1ns \
    -L xpm # -L xil_defaultlib -L uvm -L secureip -L unisims_ver -L simprims_ver

check_status "xelab"

# Simulation
xsim my_tb_snap --tclbatch xsim_cfg.tcl
check_status "xsim"

# Open the wavefile in Vivado
xsim my_tb_snap.wdb -gui -tclbatch waveform.tcl
