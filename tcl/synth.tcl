# synth.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set design_name "top_level"
set synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

set hdl_dir $root_dir/hdl/vhdl/counter/counter.srcs/sources_1/new
# set hdl_dir $root_dir/hdl/vhdl/and/and.srcs/sources_1/new
set hdl_files [glob -nocomplain -directory $hdl_dir *.vhd]
foreach file $hdl_files {
    puts "reading: $file"
    read_vhdl $file
}

set xdc_dir $root_dir/hdl/vhdl/counter/counter.srcs/constrs_1/new
# set xdc_dir $root_dir/hdl/vhdl/and/and.srcs/constrs_1/new
set xdc_file $xdc_dir/constraints.xdc
read_xdc $xdc_file

synth_design -mode out_of_context -part xc7z020clg400-1 -top $design_name

write_checkpoint -force $synthesized_dcp

exit

# open project <proj.xpr>
# close_project
