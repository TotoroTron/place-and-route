# set design "fir_filter"

set design [lindex $argv 0]
set top_params [join [lrange $argv 1 end] " "]

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

# set design "counter"
# set src_dir "$root_dir/hdl/vhdl/$design/$design.srcs/sources_1/new"
# set verif_dir "$root_dir/hdl/vhdl/$design/$design.srcs/sim_1/new"
# set xdc_dir "$root_dir/hdl/vhdl/$design/$design.srcs/constrs_1/new"

set src_dir "$root_dir/hdl/verilog/$design/src"
set verif_dir "$root_dir/hdl/verilog/$design/verif"
set xdc_dir "$root_dir/hdl/verilog/$design/constrs"

# Read in source files
set src_files [glob -nocomplain -directory $src_dir {*.[v, sv]}]
foreach file $src_files {
    puts "reading: $file"
    # read_verilog $file
    if {[string match *.sv $file]} {
        read_verilog -sv $file
    } else {
        read_verilog $file
    }
}

# read in constraints file
set xdc_file $xdc_dir/constraints.xdc
read_xdc $xdc_file

set top_params [join [lrange $argv 1 end] " "]
puts "synth.tcl: Received parameters: $top_params"

set cmd "synth_design -mode out_of_context -part xc7z020clg400-1 -fsm_extraction user_encoding -top top_level $top_params"
# set cmd "synth_design -mode out_of_context -part xc7z020clg400-1 -top top_level $top_params"
puts "synth.tcl: Running command: $cmd"

# execute synth_design command
eval $cmd

write_checkpoint -force $synthesized_dcp

exit
