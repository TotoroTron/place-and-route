// Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
// Copyright 2022-2024 Advanced Micro Devices, Inc. All Rights Reserved.
// --------------------------------------------------------------------------------
// Tool Version: Vivado v.2023.2.2 (lin64) Build 4126759 Thu Feb  8 23:52:05 MST 2024
// Date        : Tue Oct 15 13:18:52 2024
// Host        : bcheng-HP running 64-bit Ubuntu 22.04.5 LTS
// Command     : write_verilog /home/bcheng/workspace/dev/place-and-route/outputs/simulation/top_timesim.v -force -mode
//               timesim -sdf_anno true -sdf_file
//               /home/bcheng/workspace/dev/place-and-route/outputs/simulation/top_timesim.sdf
// Design      : top_level
// Purpose     : This verilog netlist is a timing simulation representation of the design and should not be modified or
//               synthesized. Please ensure that this netlist is used with the corresponding SDF file.
// Device      : xc7z020clg400-1
// --------------------------------------------------------------------------------
`timescale 1 ps / 1 ps
`define XIL_TIMING

module counter
   (i_clk_IBUF,
    SR,
    Q,
    E);
  input i_clk_IBUF;
  input [0:0]SR;
  output [3:0]Q;
  input [0:0]E;

  wire [0:0]E;
  wire [3:0]Q;
  wire [0:0]SR;
  wire i_clk_IBUF;
  wire [3:0]plusOp;

  (* SOFT_HLUTNM = "soft_lutpair1" *) 
  LUT1 #(
    .INIT(2'h1)) 
    \count[0]_i_1 
       (.I0(Q[0]),
        .O(plusOp[0]));
  (* SOFT_HLUTNM = "soft_lutpair1" *) 
  LUT2 #(
    .INIT(4'h6)) 
    \count[1]_i_1 
       (.I0(Q[0]),
        .I1(Q[1]),
        .O(plusOp[1]));
  (* SOFT_HLUTNM = "soft_lutpair0" *) 
  LUT3 #(
    .INIT(8'h78)) 
    \count[2]_i_1 
       (.I0(Q[0]),
        .I1(Q[1]),
        .I2(Q[2]),
        .O(plusOp[2]));
  (* SOFT_HLUTNM = "soft_lutpair0" *) 
  LUT4 #(
    .INIT(16'h7F80)) 
    \count[3]_i_1 
       (.I0(Q[1]),
        .I1(Q[0]),
        .I2(Q[2]),
        .I3(Q[3]),
        .O(plusOp[3]));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[0] 
       (.C(i_clk_IBUF),
        .CE(E),
        .D(plusOp[0]),
        .Q(Q[0]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[1] 
       (.C(i_clk_IBUF),
        .CE(E),
        .D(plusOp[1]),
        .Q(Q[1]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[2] 
       (.C(i_clk_IBUF),
        .CE(E),
        .D(plusOp[2]),
        .Q(Q[2]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[3] 
       (.C(i_clk_IBUF),
        .CE(E),
        .D(plusOp[3]),
        .Q(Q[3]),
        .R(SR));
endmodule

module pps_gen
   (i_clk_IBUF,
    SR,
    E);
  input i_clk_IBUF;
  input [0:0]SR;
  output [0:0]E;

  wire [0:0]E;
  wire [0:0]SR;
  wire \count[0]_i_1__0_n_0 ;
  wire \count[0]_i_3_n_0 ;
  wire [3:0]count_reg;
  wire \count_reg[0]_i_2_n_1 ;
  wire \count_reg[0]_i_2_n_2 ;
  wire \count_reg[0]_i_2_n_3 ;
  wire \count_reg[0]_i_2_n_4 ;
  wire \count_reg[0]_i_2_n_5 ;
  wire \count_reg[0]_i_2_n_6 ;
  wire \count_reg[0]_i_2_n_7 ;
  wire i_clk_IBUF;
  wire o_pulse_i_1_n_0;
  wire [3:3]\NLW_count_reg[0]_i_2_CO_UNCONNECTED ;

  LUT5 #(
    .INIT(32'hAAAAAAEA)) 
    \count[0]_i_1__0 
       (.I0(SR),
        .I1(count_reg[3]),
        .I2(count_reg[0]),
        .I3(count_reg[1]),
        .I4(count_reg[2]),
        .O(\count[0]_i_1__0_n_0 ));
  LUT1 #(
    .INIT(2'h1)) 
    \count[0]_i_3 
       (.I0(count_reg[0]),
        .O(\count[0]_i_3_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[0] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(\count_reg[0]_i_2_n_7 ),
        .Q(count_reg[0]),
        .R(\count[0]_i_1__0_n_0 ));
  CARRY4 \count_reg[0]_i_2 
       (.CI(1'b0),
        .CO({\NLW_count_reg[0]_i_2_CO_UNCONNECTED [3],\count_reg[0]_i_2_n_1 ,\count_reg[0]_i_2_n_2 ,\count_reg[0]_i_2_n_3 }),
        .CYINIT(1'b0),
        .DI({1'b0,1'b0,1'b0,1'b1}),
        .O({\count_reg[0]_i_2_n_4 ,\count_reg[0]_i_2_n_5 ,\count_reg[0]_i_2_n_6 ,\count_reg[0]_i_2_n_7 }),
        .S({count_reg[3:1],\count[0]_i_3_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[1] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(\count_reg[0]_i_2_n_6 ),
        .Q(count_reg[1]),
        .R(\count[0]_i_1__0_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[2] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(\count_reg[0]_i_2_n_5 ),
        .Q(count_reg[2]),
        .R(\count[0]_i_1__0_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[3] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(\count_reg[0]_i_2_n_4 ),
        .Q(count_reg[3]),
        .R(\count[0]_i_1__0_n_0 ));
  LUT5 #(
    .INIT(32'h00000200)) 
    o_pulse_i_1
       (.I0(count_reg[0]),
        .I1(count_reg[2]),
        .I2(count_reg[1]),
        .I3(count_reg[3]),
        .I4(SR),
        .O(o_pulse_i_1_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_pulse_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_pulse_i_1_n_0),
        .Q(E),
        .R(1'b0));
endmodule

(* tps = "1000000" *) 
(* NotValidForBitStream *)
(* \DesignAttr:ENABLE_NOC_NETLIST_VIEW  *) 
(* \DesignAttr:ENABLE_AIE_NETLIST_VIEW  *) 
module top_level
   (i_clk,
    ov_led,
    i_rst);
  (* IO_BUFFER_TYPE = "IBUF" *) input i_clk;
  output [3:0]ov_led;
  (* IO_BUFFER_TYPE = "IBUF" *) input i_rst;

  wire i_clk;
  wire i_clk_IBUF;
  wire i_rst;
  wire i_rst_IBUF;
  wire o_pulse;
  wire [3:0]ov_led;
  wire [3:0]ov_led_OBUF;

initial begin
 $sdf_annotate("/home/bcheng/workspace/dev/place-and-route/outputs/simulation/top_timesim.sdf",,,,"tool_control");
end
  pps_gen CLOCK_ENABLE_GEN
       (.E(o_pulse),
        .SR(i_rst_IBUF),
        .i_clk_IBUF(i_clk_IBUF));
  counter COUNTER
       (.E(o_pulse),
        .Q(ov_led_OBUF),
        .SR(i_rst_IBUF),
        .i_clk_IBUF(i_clk_IBUF));
  (* io_buffer_type = "IBUF" *) 
  IBUF i_clk_IBUF_inst
       (.I(i_clk),
        .O(i_clk_IBUF));
  (* io_buffer_type = "IBUF" *) 
  IBUF i_rst_IBUF_inst
       (.I(i_rst),
        .O(i_rst_IBUF));
  (* io_buffer_type = "OBUF" *) 
  OBUF \ov_led_OBUF[0]_inst 
       (.I(ov_led_OBUF[0]),
        .O(ov_led[0]));
  (* io_buffer_type = "OBUF" *) 
  OBUF \ov_led_OBUF[1]_inst 
       (.I(ov_led_OBUF[1]),
        .O(ov_led[1]));
  (* io_buffer_type = "OBUF" *) 
  OBUF \ov_led_OBUF[2]_inst 
       (.I(ov_led_OBUF[2]),
        .O(ov_led[2]));
  (* io_buffer_type = "OBUF" *) 
  OBUF \ov_led_OBUF[3]_inst 
       (.I(ov_led_OBUF[3]),
        .O(ov_led[3]));
endmodule
`ifndef GLBL
`define GLBL
`timescale  1 ps / 1 ps

module glbl ();

    parameter ROC_WIDTH = 100000;
    parameter TOC_WIDTH = 0;
    parameter GRES_WIDTH = 10000;
    parameter GRES_START = 10000;

//--------   STARTUP Globals --------------
    wire GSR;
    wire GTS;
    wire GWE;
    wire PRLD;
    wire GRESTORE;
    tri1 p_up_tmp;
    tri (weak1, strong0) PLL_LOCKG = p_up_tmp;

    wire PROGB_GLBL;
    wire CCLKO_GLBL;
    wire FCSBO_GLBL;
    wire [3:0] DO_GLBL;
    wire [3:0] DI_GLBL;
   
    reg GSR_int;
    reg GTS_int;
    reg PRLD_int;
    reg GRESTORE_int;

//--------   JTAG Globals --------------
    wire JTAG_TDO_GLBL;
    wire JTAG_TCK_GLBL;
    wire JTAG_TDI_GLBL;
    wire JTAG_TMS_GLBL;
    wire JTAG_TRST_GLBL;

    reg JTAG_CAPTURE_GLBL;
    reg JTAG_RESET_GLBL;
    reg JTAG_SHIFT_GLBL;
    reg JTAG_UPDATE_GLBL;
    reg JTAG_RUNTEST_GLBL;

    reg JTAG_SEL1_GLBL = 0;
    reg JTAG_SEL2_GLBL = 0 ;
    reg JTAG_SEL3_GLBL = 0;
    reg JTAG_SEL4_GLBL = 0;

    reg JTAG_USER_TDO1_GLBL = 1'bz;
    reg JTAG_USER_TDO2_GLBL = 1'bz;
    reg JTAG_USER_TDO3_GLBL = 1'bz;
    reg JTAG_USER_TDO4_GLBL = 1'bz;

    assign (strong1, weak0) GSR = GSR_int;
    assign (strong1, weak0) GTS = GTS_int;
    assign (weak1, weak0) PRLD = PRLD_int;
    assign (strong1, weak0) GRESTORE = GRESTORE_int;

    initial begin
	GSR_int = 1'b1;
	PRLD_int = 1'b1;
	#(ROC_WIDTH)
	GSR_int = 1'b0;
	PRLD_int = 1'b0;
    end

    initial begin
	GTS_int = 1'b1;
	#(TOC_WIDTH)
	GTS_int = 1'b0;
    end

    initial begin 
	GRESTORE_int = 1'b0;
	#(GRES_START);
	GRESTORE_int = 1'b1;
	#(GRES_WIDTH);
	GRESTORE_int = 1'b0;
    end

endmodule
`endif
