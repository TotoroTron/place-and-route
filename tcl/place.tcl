# place.tcl

set root_dir "/home/bcheng/workspace/dev/place-and-route"
set placed_dcp "$root_dir/outputs/checkpoints/placed.dcp"

open_checkpoint $placed_dcp
report_utilization
exit
