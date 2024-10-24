set_param project.enableReportConfiguration 0
load_feature core
current_fileset
xsim {my_tb_snap.wdb} -autoloadwcfg -tclbatch {waveform.tcl}
