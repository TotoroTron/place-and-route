set root_dir "/home/bcheng/workspace/dev/place-and-route"
set design_name "top_level"
set synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

open_checkpoint $placed_dcp

set verif_dir $root_dir/hdl/vhdl/counter/counter.srcs/sim_1/new
set verif_files [glob -nocomplain -directory $hdl_dir *.vhd]
foreach file $verif_files {
    puts "reading verif file: $file"
    read_vhdl $file
}

