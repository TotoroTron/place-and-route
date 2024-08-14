# vivado_synth.tcl

set project_dir "/home/bcheng/workspace/dev/place-and-route/"
set viv_project_dir "$project_dir/hdl/vhdl/counter"
set viv_project_name "counter"
set design_name "top_level"
set viv_synthesized_dcp "$project_dir/tcl/synthesized.dcp" 

open_project $viv_project_dir/$viv_project_name.xpr

synth_design -top $design_name

write_checkpoint -force viv_synthesized_dcp

close_project

puts "Synthesis complete. Check $viv_synthesized_dcp"

