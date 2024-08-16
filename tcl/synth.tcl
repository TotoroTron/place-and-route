# synth.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set viv_project_dir "$root_dir/hdl/vhdl/counter"
set viv_project_name "counter"
set viv_design_name "top_level"
set viv_synthesized_dcp "$root_dir/outputs/synthesized.dcp" 

open_project $viv_project_dir/$viv_project_name.xpr

synth_design -top $viv_design_name

write_checkpoint -force $viv_synthesized_dcp

close_project

# puts "Synthesis complete. Check $viv_synthesized_dcp."
