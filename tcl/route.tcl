# route.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set placed_dcp "$root_dir/placed.dcp"
set viv_routed_dcp "$root_dir/routed.dcp"
set bitstream_file "$root_dir/output.bit"

open_checkpoint $placed_dcp

route_design

# phys_opt_design

write_checkpoint -force $viv_routed_dcp

# write_bitstream -force $bitstream_file

close_project

# puts "Routing and bitstream generation complete. Check $routed_dcp and $bitstream_file"