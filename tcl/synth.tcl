# synth.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

set design "counter"
set src_dir "$root_dir/hdl/vhdl/$design/$design.srcs/sources_1/new"
set verif_dir "$root_dir/hdl/vhdl/$design/$design.srcs/sim_1/new"
set xdc_dir "$root_dir/hdl/vhdl/$design/$design.srcs/constrs_1/new"

# Read in source files
set src_files [glob -nocomplain -directory $src_dir *.vhd]
foreach file $src_files {
    puts "reading: $file"
    read_vhdl $file
}

# Read in verif files
# set verif_files [glob -nocomplain -directory $verif_dir *.vhd]
# foreach file $verif_files {
#     puts "reading: $file"
#     read_vhdl $file
# }

# Read in constraints file
set xdc_file $xdc_dir/constraints.xdc
read_xdc $xdc_file

synth_design -mode out_of_context -part xc7z020clg400-1 -top top_level

write_checkpoint -force $synthesized_dcp

exit
