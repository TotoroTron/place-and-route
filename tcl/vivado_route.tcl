set root_dir "/home/bcheng/workspace/dev/place-and-route"
set placed_dcp "$root_dir/outputs/vivado_placed.dcp"
set routed_dcp "$root_dir/outputs/vivado_routed.dcp"
set bitstream_file "$root_dir/outputs/output.bit"

open_checkpoint $placed_dcp

route_design

# phys_opt_design

write_checkpoint -force $routed_dcp

# write_bitstream -force $bitstream_file

exit

# close_project

# puts "Routing and bitstream generation complete. Check $routed_dcp and $bitstream_file"
