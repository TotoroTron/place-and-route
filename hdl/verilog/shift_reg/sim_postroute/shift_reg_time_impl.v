// Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
// Copyright 2022-2024 Advanced Micro Devices, Inc. All Rights Reserved.
// --------------------------------------------------------------------------------
// Tool Version: Vivado v.2023.2.2 (lin64) Build 4126759 Thu Feb  8 23:52:05 MST 2024
// Date        : Wed Oct 30 14:45:04 2024
// Host        : bcheng-HP running 64-bit Ubuntu 22.04.5 LTS
// Command     : write_verilog
//               /home/bcheng/workspace/dev/place-and-route/hdl/verilog/shift_reg/sim_postroute/shift_reg_time_impl.v
//               -force -mode timesim -nolib -sdf_anno true
// Design      : top_level
// Purpose     : This verilog netlist is a timing simulation representation of the design and should not be modified or
//               synthesized. Please ensure that this netlist is used with the corresponding SDF file.
// Device      : xc7z020clg400-1
// --------------------------------------------------------------------------------
`timescale 1 ps / 1 ps
`define XIL_TIMING

module shift_reg
   (i_clk_IBUF,
    Q,
    SR,
    D);
  input i_clk_IBUF;
  output [0:0]Q;
  input [0:0]SR;
  input [0:0]D;

  wire [0:0]D;
  wire [0:0]Q;
  wire [0:0]SR;
  wire i_clk_IBUF;
  wire [31:1]p_0_in;

  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[0] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(D),
        .Q(p_0_in[1]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[10] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[10]),
        .Q(p_0_in[11]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[11] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[11]),
        .Q(p_0_in[12]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[12] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[12]),
        .Q(p_0_in[13]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[13] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[13]),
        .Q(p_0_in[14]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[14] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[14]),
        .Q(p_0_in[15]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[15] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[15]),
        .Q(p_0_in[16]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[16] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[16]),
        .Q(p_0_in[17]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[17] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[17]),
        .Q(p_0_in[18]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[18] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[18]),
        .Q(p_0_in[19]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[19] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[19]),
        .Q(p_0_in[20]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[1] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[1]),
        .Q(p_0_in[2]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[20] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[20]),
        .Q(p_0_in[21]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[21] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[21]),
        .Q(p_0_in[22]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[22] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[22]),
        .Q(p_0_in[23]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[23] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[23]),
        .Q(p_0_in[24]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[24] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[24]),
        .Q(p_0_in[25]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[25] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[25]),
        .Q(p_0_in[26]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[26] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[26]),
        .Q(p_0_in[27]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[27] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[27]),
        .Q(p_0_in[28]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[28] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[28]),
        .Q(p_0_in[29]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[29] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[29]),
        .Q(p_0_in[30]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[2] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[2]),
        .Q(p_0_in[3]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[30] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[30]),
        .Q(p_0_in[31]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[31] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[31]),
        .Q(Q),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[3] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[3]),
        .Q(p_0_in[4]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[4] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[4]),
        .Q(p_0_in[5]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[5] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[5]),
        .Q(p_0_in[6]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[6] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[6]),
        .Q(p_0_in[7]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[7] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[7]),
        .Q(p_0_in[8]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[8] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[8]),
        .Q(p_0_in[9]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[9] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(p_0_in[9]),
        .Q(p_0_in[10]),
        .R(SR));
endmodule

(* LENGTH = "32" *) 
(* NotValidForBitStream *)
(* \DesignAttr:ENABLE_NOC_NETLIST_VIEW  *) 
(* \DesignAttr:ENABLE_AIE_NETLIST_VIEW  *) 
module top_level
   (i_clk,
    i_rst,
    o_dout,
    i_din);
  input i_clk;
  input i_rst;
  output o_dout;
  input i_din;

  wire i_clk;
  wire i_clk_IBUF;
  wire i_din;
  wire i_din_IBUF;
  wire i_rst;
  wire i_rst_IBUF;
  wire o_dout;
  wire o_dout_OBUF;

initial begin
 $sdf_annotate("shift_reg_time_impl.sdf",,,,"tool_control");
end
  (* io_buffer_type = "ibuf" *) 
  IBUF i_clk_IBUF_inst
       (.I(i_clk),
        .O(i_clk_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_din_IBUF_inst
       (.I(i_din),
        .O(i_din_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_rst_IBUF_inst
       (.I(i_rst),
        .O(i_rst_IBUF));
  (* io_buffer_type = "obuf" *) 
  OBUF o_dout_OBUF_inst
       (.I(o_dout_OBUF),
        .O(o_dout));
  shift_reg shift_reg_0
       (.D(i_din_IBUF),
        .Q(o_dout_OBUF),
        .SR(i_rst_IBUF),
        .i_clk_IBUF(i_clk_IBUF));
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
