# run complete flow

# Usage:
# vivado -mode batch -source complete_flow.tcl

# project variables
# set project_name "counter"
set project_dir "/home/bcheng/workspace/dev/place-and-route/hdl/vhdl/counter"
# set design_name "top_level"
# set rtl_dir "/home/bcheng/workspace/dev/place-and-route/hdl/vhdl/counter/counter.srcs/sources_1/new" 
# set xdc_file "/home/bcheng/workspace/dev/place-and-route/hdl/vhdl/counter/counter.srcs/constrs_1/new/constraints.xdc"
# set custom_placed_dcp "/path/to/your/custom_placed_design.dcp"
# set routed_dcp "/path/to/your/routed_design.dcp"
# set bitstream_file "/path/to/your/output.bit"

# create new project
# create_project -force $project_name ./project -part xc7z020clg400-1

# open existing project
open_project $project_dir/$project_name.xpr

# add design files
# file glob -nocomplain $rtl_dir/*.vhd
# foreach file [glob -nocomplain $rtl_dir/*.vhd] {
#     add_files $file
# }
# 
# # Add constraints file
# add_files $xdc_file

# run synthesis
synth_design -top $design_name

# write the synthesized design checkpoint (optional)
write_checkpoint -force synthesized.dcp

close_project

# # load the custom placed design checkpoint from rapidwright
# open_checkpoint $custom_placed_dcp
# 
# # run placement optimization (optional)
# # place_design
# 
# # run routing
# route_design
# 
# # optional physical optimization
# # phys_opt_design
# 
# # save routed design checkpoint
# write_checkpoint -force $routed_dcp
# 
# # generate the bitstream
# write_bitstream -force $bitstream_file
# 
# # close the project
# close_project
# 
# puts "Synthesis, placement, routing, and bitstream generation completed successfully."
