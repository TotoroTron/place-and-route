# synth.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
# set viv_project_dir "$root_dir/hdl/vhdl/counter"
# set viv_project_name "counter"
set viv_design_name "top_level"
set viv_synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

set hdl_dir $root_dir/hdl/vhdl/counter/counter.srcs/sources_1/new
set hdl_files [glob -nocomplain -directory $hdl_dir *.vhd]

# open_project $viv_project_dir/$viv_project_name.xpr

foreach file $hdl_files {
    puts "reading: $file"
    read_vhdl $file
}

synth_design -mode out_of_context -part xc7z020clg400-1 -top $viv_design_name

write_checkpoint -force $viv_synthesized_dcp

exit

# close_project

# puts "Synthesis complete. Check $viv_synthesized_dcp."
