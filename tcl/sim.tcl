set root_dir "/home/bcheng/workspace/dev/place-and-route"
set design_name "top_level"
set dcp_file "$root_dir/outputs/routed.dcp" 

open_checkpoint $dcp_file
set design "shift_reg"

set sdf_file "$root_dir/hdl/verilog/${design}/sim_postroute/${design}_time_impl.sdf"
set verilog_file "$root_dir/hdl/verilog/${design}/sim_postroute/${design}_time_impl.v"
# write_verilog $verilog_file -force -mode timesim -sdf_anno true -sdf_file $sdf_file 
write_verilog $verilog_file -force -mode timesim -nolib -sdf_anno true
write_sdf $sdf_file -force -mode timesim -process_corner slow 

# launch_simulation 
exit

# https://adaptivesupport.amd.com/s/article/63988?language=en_US
# https://docs.amd.com/r/en-US/ug835-vivado-tcl-commands/write_verilog
