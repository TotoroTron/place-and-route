set root_dir "/home/bcheng/workspace/dev/place-and-route"
set design_name "top_level"
set dcp_file "$root_dir/outputs/routed.dcp" 

open_checkpoint $dcp_file

set sdf_file "$root_dir/outputs/simulation/top_timesim.sdf"
set verilog_file "$root_dir/outputs/simulation/top_timesim.v"
write_sdf $sdf_file -force
write_verilog $verilog_file -force -mode timesim -sdf_anno true -sdf_file $sdf_file 

# launch_simulation 
exit

# https://adaptivesupport.amd.com/s/article/63988?language=en_US
# https://docs.amd.com/r/en-US/ug835-vivado-tcl-commands/write_verilog
