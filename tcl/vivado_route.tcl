# vivado_route.tcl

set project_dir "/home/bcheng/workspace/dev/place-and-route/"
set custom_placed_dcp "$proj_dir/placed.dcp"
set viv_routed_dcp "$proj_dir/routed.dcp"
set bitstream_file "$proj_dir/output.bit"

open_checkpoint $custom_placed_dcp

route_design

# phys_opt_design

write_checkpoint -force $viv_routed_dcp

write_bitstream -force $bitstream_file

close_project

puts "Routing and bitstream generation complete. $routed_dcp and $bitstream_file"

