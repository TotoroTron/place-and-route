
set root_dir "/home/bcheng/workspace/dev/place-and-route"
set synthesized_dcp "$root_dir/outputs/checkpoints/synthesized.dcp" 
set vivado_placed_dcp "$root_dir/outputs/checkpoints/vivado_placed.dcp"

open_checkpoint $synthesized_dcp

place_design

write_checkpoint -force $vivado_placed_dcp

exit
