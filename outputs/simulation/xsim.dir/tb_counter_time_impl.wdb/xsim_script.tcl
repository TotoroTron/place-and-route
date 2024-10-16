set_param project.enableReportConfiguration 0
load_feature core
current_fileset
xsim {tb_counter_time_impl.wdb} -autoloadwcfg -tclbatch {waveform.tcl}
