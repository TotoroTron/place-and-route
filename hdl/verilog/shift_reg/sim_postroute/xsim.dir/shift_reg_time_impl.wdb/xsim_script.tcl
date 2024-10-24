set_param project.enableReportConfiguration 0
load_feature core
current_fileset
xsim {shift_reg_time_impl.wdb} -autoloadwcfg -tclbatch {waveform.tcl}
