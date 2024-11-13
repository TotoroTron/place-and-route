// Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
// Copyright 2022-2024 Advanced Micro Devices, Inc. All Rights Reserved.
// --------------------------------------------------------------------------------
// Tool Version: Vivado v.2023.2.2 (lin64) Build 4126759 Thu Feb  8 23:52:05 MST 2024
// Date        : Tue Nov 12 21:45:40 2024
// Host        : bcheng-HP running 64-bit Ubuntu 22.04.5 LTS
// Command     : write_verilog
//               /home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/sim_postroute/fir_filter_time_impl.v
//               -force -mode timesim -nolib -sdf_anno true
// Design      : top_level
// Purpose     : This verilog netlist is a timing simulation representation of the design and should not be modified or
//               synthesized. Please ensure that this netlist is used with the corresponding SDF file.
// Device      : xc7z020clg400-1
// --------------------------------------------------------------------------------
`timescale 1 ps / 1 ps
`define XIL_TIMING

module RAM32X1S_UNIQ_BASE_
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD1
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD10
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD11
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD12
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD13
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD14
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD15
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD16
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD17
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD18
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD19
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD2
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD20
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD21
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD22
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD23
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD3
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD4
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD5
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD6
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD7
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD8
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module RAM32X1S_HD9
   (O,
    A0,
    A1,
    A2,
    A3,
    A4,
    D,
    WCLK,
    WE);
  output O;
  input A0;
  input A1;
  input A2;
  input A3;
  input A4;
  input D;
  input WCLK;
  input WE;

  wire A0;
  wire A1;
  wire A2;
  wire A3;
  wire A4;
  wire D;
  wire O;
  wire WCLK;
  wire WE;

  RAMS32 #(
    .INIT(32'h00000000),
    .IS_CLK_INVERTED(1'b0)) 
    SP
       (.ADR0(A0),
        .ADR1(A1),
        .ADR2(A2),
        .ADR3(A3),
        .ADR4(A4),
        .CLK(WCLK),
        .I(D),
        .O(O),
        .WE(WE));
endmodule

module deserializer_fsm
   (o_ready_OBUF,
    des_out_valid,
    o_dout_valid_reg_0,
    Q,
    o_ready_reg_0,
    i_clk_IBUF,
    i_din_valid_IBUF,
    wea,
    i_rst_IBUF,
    i_en_IBUF,
    i_din_IBUF);
  output o_ready_OBUF;
  output des_out_valid;
  output o_dout_valid_reg_0;
  output [23:0]Q;
  input o_ready_reg_0;
  input i_clk_IBUF;
  input i_din_valid_IBUF;
  input [0:0]wea;
  input i_rst_IBUF;
  input i_en_IBUF;
  input i_din_IBUF;

  wire [23:0]Q;
  wire [4:0]counter;
  wire \counter[4]_i_1_n_0 ;
  wire \counter_reg_n_0_[0] ;
  wire \counter_reg_n_0_[1] ;
  wire \counter_reg_n_0_[2] ;
  wire \counter_reg_n_0_[3] ;
  wire \counter_reg_n_0_[4] ;
  wire des_out_valid;
  wire i_clk_IBUF;
  wire i_din_IBUF;
  wire i_din_valid_IBUF;
  wire i_en_IBUF;
  wire i_rst_IBUF;
  wire [3:0]next_state;
  wire \next_state_inferred__0/i__n_0 ;
  wire o_dout_valid_i_1__0_n_0;
  wire o_dout_valid_reg_0;
  wire o_ready_OBUF;
  wire o_ready_i_2_n_0;
  wire o_ready_reg_0;
  wire \ov_dout[23]_i_1_n_0 ;
  wire [23:0]shift_reg;
  wire \shift_reg_reg_n_0_[0] ;
  wire \shift_reg_reg_n_0_[10] ;
  wire \shift_reg_reg_n_0_[11] ;
  wire \shift_reg_reg_n_0_[12] ;
  wire \shift_reg_reg_n_0_[13] ;
  wire \shift_reg_reg_n_0_[14] ;
  wire \shift_reg_reg_n_0_[15] ;
  wire \shift_reg_reg_n_0_[16] ;
  wire \shift_reg_reg_n_0_[17] ;
  wire \shift_reg_reg_n_0_[18] ;
  wire \shift_reg_reg_n_0_[19] ;
  wire \shift_reg_reg_n_0_[1] ;
  wire \shift_reg_reg_n_0_[20] ;
  wire \shift_reg_reg_n_0_[21] ;
  wire \shift_reg_reg_n_0_[22] ;
  wire \shift_reg_reg_n_0_[23] ;
  wire \shift_reg_reg_n_0_[2] ;
  wire \shift_reg_reg_n_0_[3] ;
  wire \shift_reg_reg_n_0_[4] ;
  wire \shift_reg_reg_n_0_[5] ;
  wire \shift_reg_reg_n_0_[6] ;
  wire \shift_reg_reg_n_0_[7] ;
  wire \shift_reg_reg_n_0_[8] ;
  wire \shift_reg_reg_n_0_[9] ;
  wire state;
  wire \state[3]_i_3_n_0 ;
  wire [3:0]state__0;
  wire [0:0]wea;

  (* SOFT_HLUTNM = "soft_lutpair0" *) 
  LUT4 #(
    .INIT(16'h0444)) 
    \counter[0]_i_1 
       (.I0(\counter_reg_n_0_[0] ),
        .I1(state__0[2]),
        .I2(\counter_reg_n_0_[4] ),
        .I3(\counter_reg_n_0_[3] ),
        .O(counter[0]));
  (* SOFT_HLUTNM = "soft_lutpair0" *) 
  LUT5 #(
    .INIT(32'h00606060)) 
    \counter[1]_i_1 
       (.I0(\counter_reg_n_0_[1] ),
        .I1(\counter_reg_n_0_[0] ),
        .I2(state__0[2]),
        .I3(\counter_reg_n_0_[4] ),
        .I4(\counter_reg_n_0_[3] ),
        .O(counter[1]));
  (* \PinAttr:I0:HOLD_DETOUR  = "115" *) 
  LUT6 #(
    .INIT(64'h0078787800000000)) 
    \counter[2]_i_1 
       (.I0(\counter_reg_n_0_[1] ),
        .I1(\counter_reg_n_0_[0] ),
        .I2(\counter_reg_n_0_[2] ),
        .I3(\counter_reg_n_0_[3] ),
        .I4(\counter_reg_n_0_[4] ),
        .I5(state__0[2]),
        .O(counter[2]));
  LUT6 #(
    .INIT(64'h2000000028888888)) 
    \counter[3]_i_1 
       (.I0(state__0[2]),
        .I1(\counter_reg_n_0_[3] ),
        .I2(\counter_reg_n_0_[2] ),
        .I3(\counter_reg_n_0_[0] ),
        .I4(\counter_reg_n_0_[1] ),
        .I5(\counter_reg_n_0_[4] ),
        .O(counter[3]));
  LUT5 #(
    .INIT(32'h00020200)) 
    \counter[4]_i_1 
       (.I0(i_en_IBUF),
        .I1(state__0[1]),
        .I2(state__0[3]),
        .I3(state__0[0]),
        .I4(state__0[2]),
        .O(\counter[4]_i_1_n_0 ));
  LUT6 #(
    .INIT(64'h00008000FF000000)) 
    \counter[4]_i_2__0 
       (.I0(\counter_reg_n_0_[2] ),
        .I1(\counter_reg_n_0_[0] ),
        .I2(\counter_reg_n_0_[1] ),
        .I3(state__0[2]),
        .I4(\counter_reg_n_0_[4] ),
        .I5(\counter_reg_n_0_[3] ),
        .O(counter[4]));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(counter[0]),
        .Q(\counter_reg_n_0_[0] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(counter[1]),
        .Q(\counter_reg_n_0_[1] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(counter[2]),
        .Q(\counter_reg_n_0_[2] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(counter[3]),
        .Q(\counter_reg_n_0_[3] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(counter[4]),
        .Q(\counter_reg_n_0_[4] ),
        .R(i_rst_IBUF));
  LUT6 #(
    .INIT(64'hFFEFFFEDFEEFFEED)) 
    \next_state_inferred__0/i_ 
       (.I0(state__0[0]),
        .I1(state__0[1]),
        .I2(state__0[2]),
        .I3(state__0[3]),
        .I4(i_din_valid_IBUF),
        .I5(wea),
        .O(\next_state_inferred__0/i__n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair2" *) 
  LUT4 #(
    .INIT(16'h0002)) 
    o_dout_valid_i_1__0
       (.I0(state__0[3]),
        .I1(state__0[2]),
        .I2(state__0[1]),
        .I3(state__0[0]),
        .O(o_dout_valid_i_1__0_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_dout_valid_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_dout_valid_i_1__0_n_0),
        .Q(des_out_valid),
        .R(o_ready_reg_0));
  (* SOFT_HLUTNM = "soft_lutpair2" *) 
  LUT4 #(
    .INIT(16'h0110)) 
    o_ready_i_2
       (.I0(state__0[3]),
        .I1(state__0[0]),
        .I2(state__0[1]),
        .I3(state__0[2]),
        .O(o_ready_i_2_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_ready_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_ready_i_2_n_0),
        .Q(o_ready_OBUF),
        .R(o_ready_reg_0));
  LUT5 #(
    .INIT(32'h00800000)) 
    \ov_dout[23]_i_1 
       (.I0(next_state[3]),
        .I1(\counter_reg_n_0_[4] ),
        .I2(\counter_reg_n_0_[3] ),
        .I3(i_rst_IBUF),
        .I4(i_en_IBUF),
        .O(\ov_dout[23]_i_1_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[0] ),
        .Q(Q[0]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[10] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[10] ),
        .Q(Q[10]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[11] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[11] ),
        .Q(Q[11]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[12] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[12] ),
        .Q(Q[12]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[13] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[13] ),
        .Q(Q[13]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[14] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[14] ),
        .Q(Q[14]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[15] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[15] ),
        .Q(Q[15]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[16] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[16] ),
        .Q(Q[16]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[17] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[17] ),
        .Q(Q[17]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[18] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[18] ),
        .Q(Q[18]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[19] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[19] ),
        .Q(Q[19]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[1] ),
        .Q(Q[1]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[20] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[20] ),
        .Q(Q[20]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[21] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[21] ),
        .Q(Q[21]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[22] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[22] ),
        .Q(Q[22]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[23] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[23] ),
        .Q(Q[23]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[2] ),
        .Q(Q[2]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[3] ),
        .Q(Q[3]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[4] ),
        .Q(Q[4]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[5] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[5] ),
        .Q(Q[5]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[6] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[6] ),
        .Q(Q[6]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[7] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[7] ),
        .Q(Q[7]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[8] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[8] ),
        .Q(Q[8]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[9] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1_n_0 ),
        .D(\shift_reg_reg_n_0_[9] ),
        .Q(Q[9]),
        .R(1'b0));
  LUT2 #(
    .INIT(4'hE)) 
    \ov_sum[0]_i_1 
       (.I0(i_rst_IBUF),
        .I1(des_out_valid),
        .O(o_dout_valid_reg_0));
  (* SOFT_HLUTNM = "soft_lutpair14" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[0]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[1] ),
        .O(shift_reg[0]));
  (* SOFT_HLUTNM = "soft_lutpair9" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[10]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[11] ),
        .O(shift_reg[10]));
  (* SOFT_HLUTNM = "soft_lutpair9" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[11]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[12] ),
        .O(shift_reg[11]));
  (* SOFT_HLUTNM = "soft_lutpair8" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[12]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[13] ),
        .O(shift_reg[12]));
  (* SOFT_HLUTNM = "soft_lutpair8" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[13]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[14] ),
        .O(shift_reg[13]));
  (* SOFT_HLUTNM = "soft_lutpair7" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[14]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[15] ),
        .O(shift_reg[14]));
  (* SOFT_HLUTNM = "soft_lutpair7" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[15]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[16] ),
        .O(shift_reg[15]));
  (* SOFT_HLUTNM = "soft_lutpair6" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[16]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[17] ),
        .O(shift_reg[16]));
  (* SOFT_HLUTNM = "soft_lutpair6" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[17]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[18] ),
        .O(shift_reg[17]));
  (* SOFT_HLUTNM = "soft_lutpair5" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[18]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[19] ),
        .O(shift_reg[18]));
  (* SOFT_HLUTNM = "soft_lutpair5" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[19]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[20] ),
        .O(shift_reg[19]));
  (* SOFT_HLUTNM = "soft_lutpair14" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[1]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[2] ),
        .O(shift_reg[1]));
  (* SOFT_HLUTNM = "soft_lutpair4" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[20]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[21] ),
        .O(shift_reg[20]));
  (* SOFT_HLUTNM = "soft_lutpair4" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[21]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[22] ),
        .O(shift_reg[21]));
  (* SOFT_HLUTNM = "soft_lutpair3" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[22]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[23] ),
        .O(shift_reg[22]));
  (* SOFT_HLUTNM = "soft_lutpair3" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[23]_i_1__0 
       (.I0(state__0[2]),
        .I1(i_din_IBUF),
        .O(shift_reg[23]));
  (* SOFT_HLUTNM = "soft_lutpair13" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[2]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[3] ),
        .O(shift_reg[2]));
  (* SOFT_HLUTNM = "soft_lutpair13" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[3]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[4] ),
        .O(shift_reg[3]));
  (* SOFT_HLUTNM = "soft_lutpair12" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[4]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[5] ),
        .O(shift_reg[4]));
  (* SOFT_HLUTNM = "soft_lutpair12" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[5]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[6] ),
        .O(shift_reg[5]));
  (* SOFT_HLUTNM = "soft_lutpair11" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[6]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[7] ),
        .O(shift_reg[6]));
  (* SOFT_HLUTNM = "soft_lutpair11" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[7]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[8] ),
        .O(shift_reg[7]));
  (* SOFT_HLUTNM = "soft_lutpair10" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[8]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[9] ),
        .O(shift_reg[8]));
  (* \PinAttr:I1:HOLD_DETOUR  = "281" *) 
  (* SOFT_HLUTNM = "soft_lutpair10" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[9]_i_1 
       (.I0(state__0[2]),
        .I1(\shift_reg_reg_n_0_[10] ),
        .O(shift_reg[9]));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[0]),
        .Q(\shift_reg_reg_n_0_[0] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[10] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[10]),
        .Q(\shift_reg_reg_n_0_[10] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[11] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[11]),
        .Q(\shift_reg_reg_n_0_[11] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[12] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[12]),
        .Q(\shift_reg_reg_n_0_[12] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[13] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[13]),
        .Q(\shift_reg_reg_n_0_[13] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[14] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[14]),
        .Q(\shift_reg_reg_n_0_[14] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[15] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[15]),
        .Q(\shift_reg_reg_n_0_[15] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[16] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[16]),
        .Q(\shift_reg_reg_n_0_[16] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[17] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[17]),
        .Q(\shift_reg_reg_n_0_[17] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[18] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[18]),
        .Q(\shift_reg_reg_n_0_[18] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[19] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[19]),
        .Q(\shift_reg_reg_n_0_[19] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[1]),
        .Q(\shift_reg_reg_n_0_[1] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[20] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[20]),
        .Q(\shift_reg_reg_n_0_[20] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[21] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[21]),
        .Q(\shift_reg_reg_n_0_[21] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[22] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[22]),
        .Q(\shift_reg_reg_n_0_[22] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[23] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[23]),
        .Q(\shift_reg_reg_n_0_[23] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[2]),
        .Q(\shift_reg_reg_n_0_[2] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[3]),
        .Q(\shift_reg_reg_n_0_[3] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[4]),
        .Q(\shift_reg_reg_n_0_[4] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[5] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[5]),
        .Q(\shift_reg_reg_n_0_[5] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[6] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[6]),
        .Q(\shift_reg_reg_n_0_[6] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[7] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[7]),
        .Q(\shift_reg_reg_n_0_[7] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[8] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[8]),
        .Q(\shift_reg_reg_n_0_[8] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[9] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_1_n_0 ),
        .D(shift_reg[9]),
        .Q(\shift_reg_reg_n_0_[9] ),
        .R(i_rst_IBUF));
  LUT4 #(
    .INIT(16'hFEEB)) 
    \state[0]_i_1 
       (.I0(state__0[3]),
        .I1(state__0[2]),
        .I2(state__0[1]),
        .I3(state__0[0]),
        .O(next_state[0]));
  LUT4 #(
    .INIT(16'h0010)) 
    \state[1]_i_1 
       (.I0(state__0[1]),
        .I1(state__0[3]),
        .I2(state__0[0]),
        .I3(state__0[2]),
        .O(next_state[1]));
  LUT4 #(
    .INIT(16'h0010)) 
    \state[2]_i_1__1 
       (.I0(state__0[3]),
        .I1(state__0[0]),
        .I2(state__0[1]),
        .I3(state__0[2]),
        .O(next_state[2]));
  LUT3 #(
    .INIT(8'h8A)) 
    \state[3]_i_1 
       (.I0(i_en_IBUF),
        .I1(\next_state_inferred__0/i__n_0 ),
        .I2(\state[3]_i_3_n_0 ),
        .O(state));
  LUT4 #(
    .INIT(16'h0010)) 
    \state[3]_i_2 
       (.I0(state__0[1]),
        .I1(state__0[3]),
        .I2(state__0[2]),
        .I3(state__0[0]),
        .O(next_state[3]));
  LUT6 #(
    .INIT(64'hFFEFFFFFFFFFFFFF)) 
    \state[3]_i_3 
       (.I0(\counter_reg_n_0_[1] ),
        .I1(\counter_reg_n_0_[0] ),
        .I2(state__0[2]),
        .I3(\counter_reg_n_0_[2] ),
        .I4(\counter_reg_n_0_[4] ),
        .I5(\counter_reg_n_0_[3] ),
        .O(\state[3]_i_3_n_0 ));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDSE #(
    .INIT(1'b1)) 
    \state_reg[0] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[0]),
        .Q(state__0[0]),
        .S(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[1] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[1]),
        .Q(state__0[1]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[2] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[2]),
        .Q(state__0[2]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[3] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[3]),
        .Q(state__0[3]),
        .R(i_rst_IBUF));
endmodule

module fir_filter_transposed_pipelined
   (wea,
    fir_out_valid,
    \ov_dout_reg[23]_0 ,
    i_clk_IBUF,
    i_rst_IBUF,
    i_en_IBUF,
    Q,
    \ov_sum_reg[23] ,
    ser_ready,
    des_out_valid);
  output [0:0]wea;
  output fir_out_valid;
  output [23:0]\ov_dout_reg[23]_0 ;
  input i_clk_IBUF;
  input i_rst_IBUF;
  input i_en_IBUF;
  input [23:0]Q;
  input \ov_sum_reg[23] ;
  input ser_ready;
  input des_out_valid;

  wire [23:0]Q;
  wire des_out_valid;
  wire fir_out_valid;
  wire i_clk_IBUF;
  wire i_en_IBUF;
  wire i_rst_IBUF;
  wire [0:0]next_state;
  wire \next_state_inferred__0/i__n_0 ;
  wire o_dout_valid_i_1__1_n_0;
  wire o_dout_valid_i_2_n_0;
  wire o_ready_i_1__0_n_0;
  wire \ov_dout[23]_i_1__0_n_0 ;
  wire \ov_dout[23]_i_2_n_0 ;
  wire [23:0]\ov_dout_reg[23]_0 ;
  wire [23:0]ov_sum_reg;
  wire ov_sum_reg_23_sn_1;
  wire [3:0]p_0_in__0;
  wire [3:0]p_0_in__1;
  wire [3:0]sample_addr;
  wire \sample_addr_reg_n_0_[0] ;
  wire \sample_addr_reg_n_0_[1] ;
  wire \sample_addr_reg_n_0_[2] ;
  wire \sample_addr_reg_n_0_[3] ;
  wire [23:0]sample_data;
  wire [3:0]sample_re_addr;
  wire \sample_re_addr[3]_i_1_n_0 ;
  wire \sample_re_addr_reg_n_0_[0] ;
  wire \sample_re_addr_reg_n_0_[1] ;
  wire \sample_re_addr_reg_n_0_[2] ;
  wire \sample_re_addr_reg_n_0_[3] ;
  wire \sample_wr_addr[1]_i_1_n_0 ;
  wire [3:0]sample_wr_addr_reg;
  wire ser_ready;
  wire [3:0]state;
  wire \state[1]_i_1__1_n_0 ;
  wire \state[2]_i_1_n_0 ;
  wire \state[3]_i_1__0_n_0 ;
  wire \state[3]_i_2__0_n_0 ;
  wire \state[3]_i_3__0_n_0 ;
  wire [0:0]wea;
  wire [18:0]weight_data;
  wire [3:0]weight_re_addr_reg;
  wire weight_re_i_1_n_0;
  wire weight_re_reg_n_0;

  assign ov_sum_reg_23_sn_1 = \ov_sum_reg[23] ;
  tap_transposed inst
       (.D(ov_sum_reg),
        .douta(sample_data),
        .i_clk_IBUF(i_clk_IBUF),
        .i_en_IBUF(i_en_IBUF),
        .\ov_sum_reg[23]_0 (ov_sum_reg_23_sn_1),
        .sum_trunc1_0(weight_data));
  LUT4 #(
    .INIT(16'h0116)) 
    \next_state_inferred__0/i_ 
       (.I0(state[0]),
        .I1(state[1]),
        .I2(state[2]),
        .I3(state[3]),
        .O(\next_state_inferred__0/i__n_0 ));
  LUT2 #(
    .INIT(4'h2)) 
    o_dout_valid_i_1__1
       (.I0(o_dout_valid_i_2_n_0),
        .I1(i_rst_IBUF),
        .O(o_dout_valid_i_1__1_n_0));
  LUT6 #(
    .INIT(64'h0003033C00000020)) 
    o_dout_valid_i_2
       (.I0(\ov_dout[23]_i_2_n_0 ),
        .I1(state[0]),
        .I2(state[2]),
        .I3(state[3]),
        .I4(state[1]),
        .I5(fir_out_valid),
        .O(o_dout_valid_i_2_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_dout_valid_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_dout_valid_i_1__1_n_0),
        .Q(fir_out_valid),
        .R(1'b0));
  (* SOFT_HLUTNM = "soft_lutpair25" *) 
  LUT5 #(
    .INIT(32'h00000010)) 
    o_ready_i_1__0
       (.I0(state[0]),
        .I1(state[3]),
        .I2(state[1]),
        .I3(state[2]),
        .I4(i_rst_IBUF),
        .O(o_ready_i_1__0_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_ready_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_ready_i_1__0_n_0),
        .Q(wea),
        .R(1'b0));
  LUT6 #(
    .INIT(64'h0000000000000008)) 
    \ov_dout[23]_i_1__0 
       (.I0(\ov_dout[23]_i_2_n_0 ),
        .I1(state[2]),
        .I2(state[1]),
        .I3(state[3]),
        .I4(state[0]),
        .I5(i_rst_IBUF),
        .O(\ov_dout[23]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'h8000)) 
    \ov_dout[23]_i_2 
       (.I0(weight_re_addr_reg[3]),
        .I1(weight_re_addr_reg[2]),
        .I2(weight_re_addr_reg[0]),
        .I3(weight_re_addr_reg[1]),
        .O(\ov_dout[23]_i_2_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[0]),
        .Q(\ov_dout_reg[23]_0 [0]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[10] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[10]),
        .Q(\ov_dout_reg[23]_0 [10]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[11] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[11]),
        .Q(\ov_dout_reg[23]_0 [11]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[12] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[12]),
        .Q(\ov_dout_reg[23]_0 [12]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[13] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[13]),
        .Q(\ov_dout_reg[23]_0 [13]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[14] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[14]),
        .Q(\ov_dout_reg[23]_0 [14]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[15] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[15]),
        .Q(\ov_dout_reg[23]_0 [15]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[16] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[16]),
        .Q(\ov_dout_reg[23]_0 [16]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[17] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[17]),
        .Q(\ov_dout_reg[23]_0 [17]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[18] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[18]),
        .Q(\ov_dout_reg[23]_0 [18]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[19] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[19]),
        .Q(\ov_dout_reg[23]_0 [19]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[1]),
        .Q(\ov_dout_reg[23]_0 [1]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[20] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[20]),
        .Q(\ov_dout_reg[23]_0 [20]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[21] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[21]),
        .Q(\ov_dout_reg[23]_0 [21]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[22] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[22]),
        .Q(\ov_dout_reg[23]_0 [22]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[23] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[23]),
        .Q(\ov_dout_reg[23]_0 [23]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[2]),
        .Q(\ov_dout_reg[23]_0 [2]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[3]),
        .Q(\ov_dout_reg[23]_0 [3]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[4]),
        .Q(\ov_dout_reg[23]_0 [4]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[5] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[5]),
        .Q(\ov_dout_reg[23]_0 [5]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[6] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[6]),
        .Q(\ov_dout_reg[23]_0 [6]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[7] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[7]),
        .Q(\ov_dout_reg[23]_0 [7]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[8] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[8]),
        .Q(\ov_dout_reg[23]_0 [8]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \ov_dout_reg[9] 
       (.C(i_clk_IBUF),
        .CE(\ov_dout[23]_i_1__0_n_0 ),
        .D(ov_sum_reg[9]),
        .Q(\ov_dout_reg[23]_0 [9]),
        .R(1'b0));
  (* SOFT_HLUTNM = "soft_lutpair31" *) 
  LUT3 #(
    .INIT(8'hB8)) 
    \sample_addr[0]_i_1 
       (.I0(sample_wr_addr_reg[0]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[0] ),
        .O(sample_addr[0]));
  (* SOFT_HLUTNM = "soft_lutpair29" *) 
  LUT3 #(
    .INIT(8'hB8)) 
    \sample_addr[1]_i_1 
       (.I0(sample_wr_addr_reg[1]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[1] ),
        .O(sample_addr[1]));
  (* SOFT_HLUTNM = "soft_lutpair24" *) 
  LUT3 #(
    .INIT(8'hB8)) 
    \sample_addr[2]_i_1 
       (.I0(sample_wr_addr_reg[2]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[2] ),
        .O(sample_addr[2]));
  LUT3 #(
    .INIT(8'hB8)) 
    \sample_addr[3]_i_1 
       (.I0(sample_wr_addr_reg[3]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[3] ),
        .O(sample_addr[3]));
  FDRE #(
    .INIT(1'b0)) 
    \sample_addr_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_addr[0]),
        .Q(\sample_addr_reg_n_0_[0] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_addr_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_addr[1]),
        .Q(\sample_addr_reg_n_0_[1] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_addr_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_addr[2]),
        .Q(\sample_addr_reg_n_0_[2] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_addr_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_addr[3]),
        .Q(\sample_addr_reg_n_0_[3] ),
        .R(i_rst_IBUF));
  (* SOFT_HLUTNM = "soft_lutpair31" *) 
  LUT3 #(
    .INIT(8'hC5)) 
    \sample_re_addr[0]_i_1 
       (.I0(\sample_re_addr_reg_n_0_[0] ),
        .I1(sample_wr_addr_reg[0]),
        .I2(state[1]),
        .O(sample_re_addr[0]));
  (* SOFT_HLUTNM = "soft_lutpair29" *) 
  LUT4 #(
    .INIT(16'h8BB8)) 
    \sample_re_addr[1]_i_1 
       (.I0(sample_wr_addr_reg[1]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[0] ),
        .I3(\sample_re_addr_reg_n_0_[1] ),
        .O(sample_re_addr[1]));
  (* SOFT_HLUTNM = "soft_lutpair24" *) 
  LUT5 #(
    .INIT(32'h8BB8B8B8)) 
    \sample_re_addr[2]_i_1 
       (.I0(sample_wr_addr_reg[2]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[2] ),
        .I3(\sample_re_addr_reg_n_0_[1] ),
        .I4(\sample_re_addr_reg_n_0_[0] ),
        .O(sample_re_addr[2]));
  LUT4 #(
    .INIT(16'h0006)) 
    \sample_re_addr[3]_i_1 
       (.I0(state[1]),
        .I1(state[2]),
        .I2(state[3]),
        .I3(state[0]),
        .O(\sample_re_addr[3]_i_1_n_0 ));
  LUT6 #(
    .INIT(64'h8BB8B8B8B8B8B8B8)) 
    \sample_re_addr[3]_i_2 
       (.I0(sample_wr_addr_reg[3]),
        .I1(state[1]),
        .I2(\sample_re_addr_reg_n_0_[3] ),
        .I3(\sample_re_addr_reg_n_0_[0] ),
        .I4(\sample_re_addr_reg_n_0_[1] ),
        .I5(\sample_re_addr_reg_n_0_[2] ),
        .O(sample_re_addr[3]));
  FDRE #(
    .INIT(1'b0)) 
    \sample_re_addr_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_re_addr[0]),
        .Q(\sample_re_addr_reg_n_0_[0] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_re_addr_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_re_addr[1]),
        .Q(\sample_re_addr_reg_n_0_[1] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_re_addr_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_re_addr[2]),
        .Q(\sample_re_addr_reg_n_0_[2] ),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_re_addr_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\sample_re_addr[3]_i_1_n_0 ),
        .D(sample_re_addr[3]),
        .Q(\sample_re_addr_reg_n_0_[3] ),
        .R(i_rst_IBUF));
  (* SOFT_HLUTNM = "soft_lutpair32" *) 
  LUT1 #(
    .INIT(2'h1)) 
    \sample_wr_addr[0]_i_1 
       (.I0(sample_wr_addr_reg[0]),
        .O(p_0_in__1[0]));
  (* SOFT_HLUTNM = "soft_lutpair32" *) 
  LUT2 #(
    .INIT(4'h9)) 
    \sample_wr_addr[1]_i_1 
       (.I0(sample_wr_addr_reg[0]),
        .I1(sample_wr_addr_reg[1]),
        .O(\sample_wr_addr[1]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair28" *) 
  LUT3 #(
    .INIT(8'hA9)) 
    \sample_wr_addr[2]_i_1 
       (.I0(sample_wr_addr_reg[2]),
        .I1(sample_wr_addr_reg[1]),
        .I2(sample_wr_addr_reg[0]),
        .O(p_0_in__1[2]));
  (* SOFT_HLUTNM = "soft_lutpair28" *) 
  LUT4 #(
    .INIT(16'hAAA9)) 
    \sample_wr_addr[3]_i_1 
       (.I0(sample_wr_addr_reg[3]),
        .I1(sample_wr_addr_reg[2]),
        .I2(sample_wr_addr_reg[0]),
        .I3(sample_wr_addr_reg[1]),
        .O(p_0_in__1[3]));
  FDRE #(
    .INIT(1'b0)) 
    \sample_wr_addr_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\state[2]_i_1_n_0 ),
        .D(p_0_in__1[0]),
        .Q(sample_wr_addr_reg[0]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_wr_addr_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\state[2]_i_1_n_0 ),
        .D(\sample_wr_addr[1]_i_1_n_0 ),
        .Q(sample_wr_addr_reg[1]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_wr_addr_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\state[2]_i_1_n_0 ),
        .D(p_0_in__1[2]),
        .Q(sample_wr_addr_reg[2]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \sample_wr_addr_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\state[2]_i_1_n_0 ),
        .D(p_0_in__1[3]),
        .Q(sample_wr_addr_reg[3]),
        .R(i_rst_IBUF));
  LUT4 #(
    .INIT(16'hFEEB)) 
    \state[0]_i_1__0 
       (.I0(state[3]),
        .I1(state[2]),
        .I2(state[1]),
        .I3(state[0]),
        .O(next_state));
  LUT4 #(
    .INIT(16'h0100)) 
    \state[1]_i_1__1 
       (.I0(state[2]),
        .I1(state[3]),
        .I2(state[1]),
        .I3(state[0]),
        .O(\state[1]_i_1__1_n_0 ));
  LUT4 #(
    .INIT(16'h0004)) 
    \state[2]_i_1 
       (.I0(state[2]),
        .I1(state[1]),
        .I2(state[3]),
        .I3(state[0]),
        .O(\state[2]_i_1_n_0 ));
  LUT6 #(
    .INIT(64'hFFFFFBBBFBBBFBBB)) 
    \state[3]_i_1__0 
       (.I0(\state[3]_i_3__0_n_0 ),
        .I1(\next_state_inferred__0/i__n_0 ),
        .I2(state[3]),
        .I3(ser_ready),
        .I4(des_out_valid),
        .I5(state[0]),
        .O(\state[3]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'h0100)) 
    \state[3]_i_2__0 
       (.I0(state[0]),
        .I1(state[3]),
        .I2(state[1]),
        .I3(state[2]),
        .O(\state[3]_i_2__0_n_0 ));
  LUT6 #(
    .INIT(64'hEAAAAAAAAAAAAAAA)) 
    \state[3]_i_3__0 
       (.I0(state[1]),
        .I1(weight_re_addr_reg[3]),
        .I2(weight_re_addr_reg[2]),
        .I3(weight_re_addr_reg[0]),
        .I4(weight_re_addr_reg[1]),
        .I5(state[2]),
        .O(\state[3]_i_3__0_n_0 ));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDSE #(
    .INIT(1'b1)) 
    \state_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_1__0_n_0 ),
        .D(next_state),
        .Q(state[0]),
        .S(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_1__0_n_0 ),
        .D(\state[1]_i_1__1_n_0 ),
        .Q(state[1]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_1__0_n_0 ),
        .D(\state[2]_i_1_n_0 ),
        .Q(state[2]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_1__0_n_0 ),
        .D(\state[3]_i_2__0_n_0 ),
        .Q(state[3]),
        .R(i_rst_IBUF));
  LUT1 #(
    .INIT(2'h1)) 
    \weight_re_addr[0]_i_1 
       (.I0(weight_re_addr_reg[0]),
        .O(p_0_in__0[0]));
  (* SOFT_HLUTNM = "soft_lutpair30" *) 
  LUT2 #(
    .INIT(4'h6)) 
    \weight_re_addr[1]_i_1 
       (.I0(weight_re_addr_reg[0]),
        .I1(weight_re_addr_reg[1]),
        .O(p_0_in__0[1]));
  (* SOFT_HLUTNM = "soft_lutpair30" *) 
  LUT3 #(
    .INIT(8'h6A)) 
    \weight_re_addr[2]_i_1 
       (.I0(weight_re_addr_reg[2]),
        .I1(weight_re_addr_reg[0]),
        .I2(weight_re_addr_reg[1]),
        .O(p_0_in__0[2]));
  LUT4 #(
    .INIT(16'h7F80)) 
    \weight_re_addr[3]_i_1 
       (.I0(weight_re_addr_reg[1]),
        .I1(weight_re_addr_reg[0]),
        .I2(weight_re_addr_reg[2]),
        .I3(weight_re_addr_reg[3]),
        .O(p_0_in__0[3]));
  FDRE #(
    .INIT(1'b0)) 
    \weight_re_addr_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_2__0_n_0 ),
        .D(p_0_in__0[0]),
        .Q(weight_re_addr_reg[0]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \weight_re_addr_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_2__0_n_0 ),
        .D(p_0_in__0[1]),
        .Q(weight_re_addr_reg[1]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \weight_re_addr_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_2__0_n_0 ),
        .D(p_0_in__0[2]),
        .Q(weight_re_addr_reg[2]),
        .R(i_rst_IBUF));
  FDRE #(
    .INIT(1'b0)) 
    \weight_re_addr_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\state[3]_i_2__0_n_0 ),
        .D(p_0_in__0[3]),
        .Q(weight_re_addr_reg[3]),
        .R(i_rst_IBUF));
  (* SOFT_HLUTNM = "soft_lutpair25" *) 
  LUT5 #(
    .INIT(32'h00000002)) 
    weight_re_i_1
       (.I0(state[2]),
        .I1(state[1]),
        .I2(state[3]),
        .I3(state[0]),
        .I4(i_rst_IBUF),
        .O(weight_re_i_1_n_0));
  FDRE #(
    .INIT(1'b0)) 
    weight_re_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(weight_re_i_1_n_0),
        .Q(weight_re_reg_n_0),
        .R(1'b0));
  xpm_memory_spram xpm_memory_spram_inst
       (.Q({\sample_addr_reg_n_0_[3] ,\sample_addr_reg_n_0_[2] ,\sample_addr_reg_n_0_[1] ,\sample_addr_reg_n_0_[0] }),
        .douta(sample_data),
        .\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] (Q),
        .i_clk_IBUF(i_clk_IBUF),
        .i_en_IBUF(i_en_IBUF),
        .i_rst_IBUF(i_rst_IBUF),
        .wea(wea));
  xpm_memory_sprom xpm_memory_sprom_inst
       (.Q(weight_re_addr_reg),
        .douta(weight_data),
        .ena(weight_re_reg_n_0),
        .i_clk_IBUF(i_clk_IBUF),
        .i_rst_IBUF(i_rst_IBUF));
endmodule

module serializer_fsm
   (ser_ready,
    i_rst,
    o_dout_valid_OBUF,
    Q,
    i_clk_IBUF,
    i_rst_IBUF,
    i_en_IBUF,
    fir_out_valid,
    i_ready_IBUF,
    \shift_reg_reg[23]_0 );
  output ser_ready;
  output i_rst;
  output o_dout_valid_OBUF;
  output [0:0]Q;
  input i_clk_IBUF;
  input i_rst_IBUF;
  input i_en_IBUF;
  input fir_out_valid;
  input i_ready_IBUF;
  input [23:0]\shift_reg_reg[23]_0 ;

  wire [0:0]Q;
  wire \counter[0]_i_1__0_n_0 ;
  wire \counter[1]_i_1__0_n_0 ;
  wire \counter[2]_i_1__0_n_0 ;
  wire \counter[3]_i_1_n_0 ;
  wire \counter[4]_i_1_n_0 ;
  wire \counter[4]_i_2_n_0 ;
  wire \counter[4]_i_3_n_0 ;
  wire \counter[4]_i_4_n_0 ;
  wire [4:0]counter_reg;
  wire fir_out_valid;
  wire i_clk_IBUF;
  wire i_en_IBUF;
  wire i_ready_IBUF;
  wire i_rst;
  wire i_rst_IBUF;
  wire [3:0]next_state;
  wire o_dout_valid_OBUF;
  wire o_dout_valid_i_1_n_0;
  wire ser_ready;
  wire \shift_reg[0]_i_1__0_n_0 ;
  wire \shift_reg[10]_i_1__0_n_0 ;
  wire \shift_reg[11]_i_1__0_n_0 ;
  wire \shift_reg[12]_i_1__0_n_0 ;
  wire \shift_reg[13]_i_1__0_n_0 ;
  wire \shift_reg[14]_i_1__0_n_0 ;
  wire \shift_reg[15]_i_1__0_n_0 ;
  wire \shift_reg[16]_i_1__0_n_0 ;
  wire \shift_reg[17]_i_1__0_n_0 ;
  wire \shift_reg[18]_i_1__0_n_0 ;
  wire \shift_reg[19]_i_1__0_n_0 ;
  wire \shift_reg[1]_i_1__0_n_0 ;
  wire \shift_reg[20]_i_1__0_n_0 ;
  wire \shift_reg[21]_i_1__0_n_0 ;
  wire \shift_reg[22]_i_1__0_n_0 ;
  wire \shift_reg[23]_i_1_n_0 ;
  wire \shift_reg[23]_i_2_n_0 ;
  wire \shift_reg[23]_i_3_n_0 ;
  wire \shift_reg[2]_i_1__0_n_0 ;
  wire \shift_reg[3]_i_1__0_n_0 ;
  wire \shift_reg[4]_i_1__0_n_0 ;
  wire \shift_reg[5]_i_1__0_n_0 ;
  wire \shift_reg[6]_i_1__0_n_0 ;
  wire \shift_reg[7]_i_1__0_n_0 ;
  wire \shift_reg[8]_i_1__0_n_0 ;
  wire \shift_reg[9]_i_1__0_n_0 ;
  wire [23:0]\shift_reg_reg[23]_0 ;
  wire \shift_reg_reg_n_0_[10] ;
  wire \shift_reg_reg_n_0_[11] ;
  wire \shift_reg_reg_n_0_[12] ;
  wire \shift_reg_reg_n_0_[13] ;
  wire \shift_reg_reg_n_0_[14] ;
  wire \shift_reg_reg_n_0_[15] ;
  wire \shift_reg_reg_n_0_[16] ;
  wire \shift_reg_reg_n_0_[17] ;
  wire \shift_reg_reg_n_0_[18] ;
  wire \shift_reg_reg_n_0_[19] ;
  wire \shift_reg_reg_n_0_[1] ;
  wire \shift_reg_reg_n_0_[20] ;
  wire \shift_reg_reg_n_0_[21] ;
  wire \shift_reg_reg_n_0_[22] ;
  wire \shift_reg_reg_n_0_[23] ;
  wire \shift_reg_reg_n_0_[2] ;
  wire \shift_reg_reg_n_0_[3] ;
  wire \shift_reg_reg_n_0_[4] ;
  wire \shift_reg_reg_n_0_[5] ;
  wire \shift_reg_reg_n_0_[6] ;
  wire \shift_reg_reg_n_0_[7] ;
  wire \shift_reg_reg_n_0_[8] ;
  wire \shift_reg_reg_n_0_[9] ;
  wire state;
  wire \state[3]_i_3__1_n_0 ;
  wire [3:0]state__0;

  LUT5 #(
    .INIT(32'h0000222A)) 
    \counter[0]_i_1__0 
       (.I0(i_ready_IBUF),
        .I1(counter_reg[4]),
        .I2(\counter[4]_i_4_n_0 ),
        .I3(counter_reg[3]),
        .I4(counter_reg[0]),
        .O(\counter[0]_i_1__0_n_0 ));
  LUT6 #(
    .INIT(64'h0000222A222A0000)) 
    \counter[1]_i_1__0 
       (.I0(i_ready_IBUF),
        .I1(counter_reg[4]),
        .I2(\counter[4]_i_4_n_0 ),
        .I3(counter_reg[3]),
        .I4(counter_reg[0]),
        .I5(counter_reg[1]),
        .O(\counter[1]_i_1__0_n_0 ));
  LUT3 #(
    .INIT(8'h6A)) 
    \counter[2]_i_1__0 
       (.I0(counter_reg[2]),
        .I1(counter_reg[1]),
        .I2(counter_reg[0]),
        .O(\counter[2]_i_1__0_n_0 ));
  (* \PinAttr:I0:HOLD_DETOUR  = "166" *) 
  LUT5 #(
    .INIT(32'h04FF4000)) 
    \counter[3]_i_1 
       (.I0(counter_reg[4]),
        .I1(i_ready_IBUF),
        .I2(\counter[4]_i_4_n_0 ),
        .I3(\counter[4]_i_2_n_0 ),
        .I4(counter_reg[3]),
        .O(\counter[3]_i_1_n_0 ));
  LUT5 #(
    .INIT(32'hDDD50000)) 
    \counter[4]_i_1 
       (.I0(i_ready_IBUF),
        .I1(counter_reg[4]),
        .I2(\counter[4]_i_4_n_0 ),
        .I3(counter_reg[3]),
        .I4(\counter[4]_i_2_n_0 ),
        .O(\counter[4]_i_1_n_0 ));
  LUT6 #(
    .INIT(64'h0000000000000400)) 
    \counter[4]_i_2 
       (.I0(i_rst_IBUF),
        .I1(i_en_IBUF),
        .I2(state__0[2]),
        .I3(state__0[3]),
        .I4(state__0[1]),
        .I5(state__0[0]),
        .O(\counter[4]_i_2_n_0 ));
  LUT5 #(
    .INIT(32'hFFFF8000)) 
    \counter[4]_i_3 
       (.I0(counter_reg[0]),
        .I1(counter_reg[1]),
        .I2(counter_reg[2]),
        .I3(counter_reg[3]),
        .I4(counter_reg[4]),
        .O(\counter[4]_i_3_n_0 ));
  LUT3 #(
    .INIT(8'h80)) 
    \counter[4]_i_4 
       (.I0(counter_reg[2]),
        .I1(counter_reg[1]),
        .I2(counter_reg[0]),
        .O(\counter[4]_i_4_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_2_n_0 ),
        .D(\counter[0]_i_1__0_n_0 ),
        .Q(counter_reg[0]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_2_n_0 ),
        .D(\counter[1]_i_1__0_n_0 ),
        .Q(counter_reg[1]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_2_n_0 ),
        .D(\counter[2]_i_1__0_n_0 ),
        .Q(counter_reg[2]),
        .R(\counter[4]_i_1_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[3] 
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(\counter[3]_i_1_n_0 ),
        .Q(counter_reg[3]),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \counter_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\counter[4]_i_2_n_0 ),
        .D(\counter[4]_i_3_n_0 ),
        .Q(counter_reg[4]),
        .R(\counter[4]_i_1_n_0 ));
  LUT4 #(
    .INIT(16'h0006)) 
    o_dout_valid_i_1
       (.I0(state__0[2]),
        .I1(state__0[3]),
        .I2(state__0[0]),
        .I3(state__0[1]),
        .O(o_dout_valid_i_1_n_0));
  FDRE #(
    .INIT(1'b0)) 
    o_dout_valid_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(o_dout_valid_i_1_n_0),
        .Q(o_dout_valid_OBUF),
        .R(i_rst));
  LUT2 #(
    .INIT(4'hB)) 
    o_ready_i_1
       (.I0(i_rst_IBUF),
        .I1(i_en_IBUF),
        .O(i_rst));
  FDRE #(
    .INIT(1'b0)) 
    o_ready_reg
       (.C(i_clk_IBUF),
        .CE(1'b1),
        .D(next_state[2]),
        .Q(ser_ready),
        .R(i_rst));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[0]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[1] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [0]),
        .I3(state__0[1]),
        .O(\shift_reg[0]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[10]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[11] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [10]),
        .I3(state__0[1]),
        .O(\shift_reg[10]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[11]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[12] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [11]),
        .I3(state__0[1]),
        .O(\shift_reg[11]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[12]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[13] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [12]),
        .I3(state__0[1]),
        .O(\shift_reg[12]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[13]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[14] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [13]),
        .I3(state__0[1]),
        .O(\shift_reg[13]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[14]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[15] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [14]),
        .I3(state__0[1]),
        .O(\shift_reg[14]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[15]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[16] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [15]),
        .I3(state__0[1]),
        .O(\shift_reg[15]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[16]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[17] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [16]),
        .I3(state__0[1]),
        .O(\shift_reg[16]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[17]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[18] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [17]),
        .I3(state__0[1]),
        .O(\shift_reg[17]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[18]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[19] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [18]),
        .I3(state__0[1]),
        .O(\shift_reg[18]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[19]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[20] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [19]),
        .I3(state__0[1]),
        .O(\shift_reg[19]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[1]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[2] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [1]),
        .I3(state__0[1]),
        .O(\shift_reg[1]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[20]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[21] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [20]),
        .I3(state__0[1]),
        .O(\shift_reg[20]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[21]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[22] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [21]),
        .I3(state__0[1]),
        .O(\shift_reg[21]_i_1__0_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair36" *) 
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[22]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[23] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [22]),
        .I3(state__0[1]),
        .O(\shift_reg[22]_i_1__0_n_0 ));
  LUT6 #(
    .INIT(64'h0000011000010110)) 
    \shift_reg[23]_i_1 
       (.I0(i_rst),
        .I1(state__0[2]),
        .I2(state__0[0]),
        .I3(state__0[1]),
        .I4(state__0[3]),
        .I5(\shift_reg[23]_i_3_n_0 ),
        .O(\shift_reg[23]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair36" *) 
  LUT2 #(
    .INIT(4'h8)) 
    \shift_reg[23]_i_2 
       (.I0(state__0[1]),
        .I1(\shift_reg_reg[23]_0 [23]),
        .O(\shift_reg[23]_i_2_n_0 ));
  LUT6 #(
    .INIT(64'hEAAA0000FFFFFFFF)) 
    \shift_reg[23]_i_3 
       (.I0(counter_reg[3]),
        .I1(counter_reg[0]),
        .I2(counter_reg[1]),
        .I3(counter_reg[2]),
        .I4(counter_reg[4]),
        .I5(i_ready_IBUF),
        .O(\shift_reg[23]_i_3_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[2]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[3] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [2]),
        .I3(state__0[1]),
        .O(\shift_reg[2]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[3]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[4] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [3]),
        .I3(state__0[1]),
        .O(\shift_reg[3]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[4]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[5] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [4]),
        .I3(state__0[1]),
        .O(\shift_reg[4]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[5]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[6] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [5]),
        .I3(state__0[1]),
        .O(\shift_reg[5]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[6]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[7] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [6]),
        .I3(state__0[1]),
        .O(\shift_reg[6]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[7]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[8] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [7]),
        .I3(state__0[1]),
        .O(\shift_reg[7]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[8]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[9] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [8]),
        .I3(state__0[1]),
        .O(\shift_reg[8]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'hF888)) 
    \shift_reg[9]_i_1__0 
       (.I0(\shift_reg_reg_n_0_[10] ),
        .I1(state__0[3]),
        .I2(\shift_reg_reg[23]_0 [9]),
        .I3(state__0[1]),
        .O(\shift_reg[9]_i_1__0_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[0] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[0]_i_1__0_n_0 ),
        .Q(Q),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[10] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[10]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[10] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[11] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[11]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[11] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[12] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[12]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[12] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[13] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[13]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[13] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[14] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[14]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[14] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[15] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[15]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[15] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[16] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[16]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[16] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[17] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[17]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[17] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[18] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[18]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[18] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[19] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[19]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[19] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[1] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[1]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[1] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[20] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[20]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[20] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[21] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[21]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[21] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[22] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[22]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[22] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[23] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[23]_i_2_n_0 ),
        .Q(\shift_reg_reg_n_0_[23] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[2] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[2]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[2] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[3] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[3]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[3] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[4] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[4]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[4] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[5] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[5]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[5] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[6] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[6]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[6] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[7] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[7]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[7] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[8] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[8]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[8] ),
        .R(1'b0));
  FDRE #(
    .INIT(1'b0)) 
    \shift_reg_reg[9] 
       (.C(i_clk_IBUF),
        .CE(\shift_reg[23]_i_1_n_0 ),
        .D(\shift_reg[9]_i_1__0_n_0 ),
        .Q(\shift_reg_reg_n_0_[9] ),
        .R(1'b0));
  LUT4 #(
    .INIT(16'hFEEB)) 
    \state[0]_i_1__1 
       (.I0(state__0[3]),
        .I1(state__0[2]),
        .I2(state__0[1]),
        .I3(state__0[0]),
        .O(next_state[0]));
  (* SOFT_HLUTNM = "soft_lutpair35" *) 
  LUT4 #(
    .INIT(16'h0010)) 
    \state[1]_i_1__0 
       (.I0(state__0[2]),
        .I1(state__0[3]),
        .I2(state__0[0]),
        .I3(state__0[1]),
        .O(next_state[1]));
  LUT4 #(
    .INIT(16'h0010)) 
    \state[2]_i_1__0 
       (.I0(state__0[2]),
        .I1(state__0[3]),
        .I2(state__0[1]),
        .I3(state__0[0]),
        .O(next_state[2]));
  LUT4 #(
    .INIT(16'hAAA8)) 
    \state[3]_i_1__1 
       (.I0(i_en_IBUF),
        .I1(\state[3]_i_3__1_n_0 ),
        .I2(state__0[2]),
        .I3(state__0[1]),
        .O(state));
  (* SOFT_HLUTNM = "soft_lutpair35" *) 
  LUT4 #(
    .INIT(16'h0010)) 
    \state[3]_i_2__1 
       (.I0(state__0[0]),
        .I1(state__0[1]),
        .I2(state__0[2]),
        .I3(state__0[3]),
        .O(next_state[3]));
  LUT6 #(
    .INIT(64'hFFFF3000AAAAFFFF)) 
    \state[3]_i_3__1 
       (.I0(fir_out_valid),
        .I1(counter_reg[3]),
        .I2(counter_reg[4]),
        .I3(\counter[4]_i_4_n_0 ),
        .I4(state__0[0]),
        .I5(state__0[3]),
        .O(\state[3]_i_3__1_n_0 ));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDSE #(
    .INIT(1'b1)) 
    \state_reg[0] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[0]),
        .Q(state__0[0]),
        .S(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[1] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[1]),
        .Q(state__0[1]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[2] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[2]),
        .Q(state__0[2]),
        .R(i_rst_IBUF));
  (* FSM_ENCODED_STATES = "S0:0001,S1:0010,S2:0100,S3:1000," *) 
  FDRE #(
    .INIT(1'b0)) 
    \state_reg[3] 
       (.C(i_clk_IBUF),
        .CE(state),
        .D(next_state[3]),
        .Q(state__0[3]),
        .R(i_rst_IBUF));
endmodule

module tap_transposed
   (D,
    douta,
    sum_trunc1_0,
    \ov_sum_reg[23]_0 ,
    i_en_IBUF,
    i_clk_IBUF);
  output [23:0]D;
  input [23:0]douta;
  input [18:0]sum_trunc1_0;
  input \ov_sum_reg[23]_0 ;
  input i_en_IBUF;
  input i_clk_IBUF;

  wire [23:0]D;
  wire [23:0]douta;
  wire i_clk_IBUF;
  wire i_en_IBUF;
  wire \ov_sum[0]_i_3_n_0 ;
  wire \ov_sum[0]_i_4_n_0 ;
  wire \ov_sum[0]_i_5_n_0 ;
  wire \ov_sum[0]_i_6_n_0 ;
  wire \ov_sum[12]_i_2_n_0 ;
  wire \ov_sum[12]_i_3_n_0 ;
  wire \ov_sum[12]_i_4_n_0 ;
  wire \ov_sum[12]_i_5_n_0 ;
  wire \ov_sum[16]_i_2_n_0 ;
  wire \ov_sum[16]_i_3_n_0 ;
  wire \ov_sum[16]_i_4_n_0 ;
  wire \ov_sum[16]_i_5_n_0 ;
  wire \ov_sum[20]_i_2_n_0 ;
  wire \ov_sum[20]_i_3_n_0 ;
  wire \ov_sum[20]_i_4_n_0 ;
  wire \ov_sum[20]_i_5_n_0 ;
  wire \ov_sum[4]_i_2_n_0 ;
  wire \ov_sum[4]_i_3_n_0 ;
  wire \ov_sum[4]_i_4_n_0 ;
  wire \ov_sum[4]_i_5_n_0 ;
  wire \ov_sum[8]_i_2_n_0 ;
  wire \ov_sum[8]_i_3_n_0 ;
  wire \ov_sum[8]_i_4_n_0 ;
  wire \ov_sum[8]_i_5_n_0 ;
  wire \ov_sum_reg[0]_i_2_n_0 ;
  wire \ov_sum_reg[0]_i_2_n_1 ;
  wire \ov_sum_reg[0]_i_2_n_2 ;
  wire \ov_sum_reg[0]_i_2_n_3 ;
  wire \ov_sum_reg[0]_i_2_n_4 ;
  wire \ov_sum_reg[0]_i_2_n_5 ;
  wire \ov_sum_reg[0]_i_2_n_6 ;
  wire \ov_sum_reg[0]_i_2_n_7 ;
  wire \ov_sum_reg[12]_i_1_n_0 ;
  wire \ov_sum_reg[12]_i_1_n_1 ;
  wire \ov_sum_reg[12]_i_1_n_2 ;
  wire \ov_sum_reg[12]_i_1_n_3 ;
  wire \ov_sum_reg[12]_i_1_n_4 ;
  wire \ov_sum_reg[12]_i_1_n_5 ;
  wire \ov_sum_reg[12]_i_1_n_6 ;
  wire \ov_sum_reg[12]_i_1_n_7 ;
  wire \ov_sum_reg[16]_i_1_n_0 ;
  wire \ov_sum_reg[16]_i_1_n_1 ;
  wire \ov_sum_reg[16]_i_1_n_2 ;
  wire \ov_sum_reg[16]_i_1_n_3 ;
  wire \ov_sum_reg[16]_i_1_n_4 ;
  wire \ov_sum_reg[16]_i_1_n_5 ;
  wire \ov_sum_reg[16]_i_1_n_6 ;
  wire \ov_sum_reg[16]_i_1_n_7 ;
  wire \ov_sum_reg[20]_i_1_n_1 ;
  wire \ov_sum_reg[20]_i_1_n_2 ;
  wire \ov_sum_reg[20]_i_1_n_3 ;
  wire \ov_sum_reg[20]_i_1_n_4 ;
  wire \ov_sum_reg[20]_i_1_n_5 ;
  wire \ov_sum_reg[20]_i_1_n_6 ;
  wire \ov_sum_reg[20]_i_1_n_7 ;
  wire \ov_sum_reg[23]_0 ;
  wire \ov_sum_reg[4]_i_1_n_0 ;
  wire \ov_sum_reg[4]_i_1_n_1 ;
  wire \ov_sum_reg[4]_i_1_n_2 ;
  wire \ov_sum_reg[4]_i_1_n_3 ;
  wire \ov_sum_reg[4]_i_1_n_4 ;
  wire \ov_sum_reg[4]_i_1_n_5 ;
  wire \ov_sum_reg[4]_i_1_n_6 ;
  wire \ov_sum_reg[4]_i_1_n_7 ;
  wire \ov_sum_reg[8]_i_1_n_0 ;
  wire \ov_sum_reg[8]_i_1_n_1 ;
  wire \ov_sum_reg[8]_i_1_n_2 ;
  wire \ov_sum_reg[8]_i_1_n_3 ;
  wire \ov_sum_reg[8]_i_1_n_4 ;
  wire \ov_sum_reg[8]_i_1_n_5 ;
  wire \ov_sum_reg[8]_i_1_n_6 ;
  wire \ov_sum_reg[8]_i_1_n_7 ;
  wire [23:0]p_0_in;
  wire [18:0]sum_trunc1_0;
  wire sum_trunc1__0_n_100;
  wire sum_trunc1__0_n_101;
  wire sum_trunc1__0_n_102;
  wire sum_trunc1__0_n_103;
  wire sum_trunc1__0_n_104;
  wire sum_trunc1__0_n_105;
  wire sum_trunc1__0_n_99;
  wire sum_trunc1_n_100;
  wire sum_trunc1_n_101;
  wire sum_trunc1_n_102;
  wire sum_trunc1_n_103;
  wire sum_trunc1_n_104;
  wire sum_trunc1_n_105;
  wire sum_trunc1_n_106;
  wire sum_trunc1_n_107;
  wire sum_trunc1_n_108;
  wire sum_trunc1_n_109;
  wire sum_trunc1_n_110;
  wire sum_trunc1_n_111;
  wire sum_trunc1_n_112;
  wire sum_trunc1_n_113;
  wire sum_trunc1_n_114;
  wire sum_trunc1_n_115;
  wire sum_trunc1_n_116;
  wire sum_trunc1_n_117;
  wire sum_trunc1_n_118;
  wire sum_trunc1_n_119;
  wire sum_trunc1_n_120;
  wire sum_trunc1_n_121;
  wire sum_trunc1_n_122;
  wire sum_trunc1_n_123;
  wire sum_trunc1_n_124;
  wire sum_trunc1_n_125;
  wire sum_trunc1_n_126;
  wire sum_trunc1_n_127;
  wire sum_trunc1_n_128;
  wire sum_trunc1_n_129;
  wire sum_trunc1_n_130;
  wire sum_trunc1_n_131;
  wire sum_trunc1_n_132;
  wire sum_trunc1_n_133;
  wire sum_trunc1_n_134;
  wire sum_trunc1_n_135;
  wire sum_trunc1_n_136;
  wire sum_trunc1_n_137;
  wire sum_trunc1_n_138;
  wire sum_trunc1_n_139;
  wire sum_trunc1_n_140;
  wire sum_trunc1_n_141;
  wire sum_trunc1_n_142;
  wire sum_trunc1_n_143;
  wire sum_trunc1_n_144;
  wire sum_trunc1_n_145;
  wire sum_trunc1_n_146;
  wire sum_trunc1_n_147;
  wire sum_trunc1_n_148;
  wire sum_trunc1_n_149;
  wire sum_trunc1_n_150;
  wire sum_trunc1_n_151;
  wire sum_trunc1_n_152;
  wire sum_trunc1_n_153;
  wire sum_trunc1_n_24;
  wire sum_trunc1_n_25;
  wire sum_trunc1_n_26;
  wire sum_trunc1_n_27;
  wire sum_trunc1_n_28;
  wire sum_trunc1_n_29;
  wire sum_trunc1_n_30;
  wire sum_trunc1_n_31;
  wire sum_trunc1_n_32;
  wire sum_trunc1_n_33;
  wire sum_trunc1_n_34;
  wire sum_trunc1_n_35;
  wire sum_trunc1_n_36;
  wire sum_trunc1_n_37;
  wire sum_trunc1_n_38;
  wire sum_trunc1_n_39;
  wire sum_trunc1_n_40;
  wire sum_trunc1_n_41;
  wire sum_trunc1_n_42;
  wire sum_trunc1_n_43;
  wire sum_trunc1_n_44;
  wire sum_trunc1_n_45;
  wire sum_trunc1_n_46;
  wire sum_trunc1_n_47;
  wire sum_trunc1_n_48;
  wire sum_trunc1_n_49;
  wire sum_trunc1_n_50;
  wire sum_trunc1_n_51;
  wire sum_trunc1_n_52;
  wire sum_trunc1_n_53;
  wire sum_trunc1_n_58;
  wire sum_trunc1_n_59;
  wire sum_trunc1_n_60;
  wire sum_trunc1_n_61;
  wire sum_trunc1_n_62;
  wire sum_trunc1_n_63;
  wire sum_trunc1_n_64;
  wire sum_trunc1_n_65;
  wire sum_trunc1_n_66;
  wire sum_trunc1_n_67;
  wire sum_trunc1_n_68;
  wire sum_trunc1_n_69;
  wire sum_trunc1_n_70;
  wire sum_trunc1_n_71;
  wire sum_trunc1_n_72;
  wire sum_trunc1_n_73;
  wire sum_trunc1_n_74;
  wire sum_trunc1_n_75;
  wire sum_trunc1_n_76;
  wire sum_trunc1_n_77;
  wire sum_trunc1_n_78;
  wire sum_trunc1_n_79;
  wire sum_trunc1_n_80;
  wire sum_trunc1_n_81;
  wire sum_trunc1_n_82;
  wire sum_trunc1_n_83;
  wire sum_trunc1_n_84;
  wire sum_trunc1_n_85;
  wire sum_trunc1_n_86;
  wire sum_trunc1_n_87;
  wire sum_trunc1_n_88;
  wire sum_trunc1_n_89;
  wire sum_trunc1_n_90;
  wire sum_trunc1_n_91;
  wire sum_trunc1_n_92;
  wire sum_trunc1_n_93;
  wire sum_trunc1_n_94;
  wire sum_trunc1_n_95;
  wire sum_trunc1_n_96;
  wire sum_trunc1_n_97;
  wire sum_trunc1_n_98;
  wire sum_trunc1_n_99;
  wire [3:3]\NLW_ov_sum_reg[20]_i_1_CO_UNCONNECTED ;
  wire NLW_sum_trunc1_CARRYCASCOUT_UNCONNECTED;
  wire NLW_sum_trunc1_MULTSIGNOUT_UNCONNECTED;
  wire NLW_sum_trunc1_OVERFLOW_UNCONNECTED;
  wire NLW_sum_trunc1_PATTERNBDETECT_UNCONNECTED;
  wire NLW_sum_trunc1_PATTERNDETECT_UNCONNECTED;
  wire NLW_sum_trunc1_UNDERFLOW_UNCONNECTED;
  wire [17:0]NLW_sum_trunc1_BCOUT_UNCONNECTED;
  wire [3:0]NLW_sum_trunc1_CARRYOUT_UNCONNECTED;
  wire NLW_sum_trunc1__0_CARRYCASCOUT_UNCONNECTED;
  wire NLW_sum_trunc1__0_MULTSIGNOUT_UNCONNECTED;
  wire NLW_sum_trunc1__0_OVERFLOW_UNCONNECTED;
  wire NLW_sum_trunc1__0_PATTERNBDETECT_UNCONNECTED;
  wire NLW_sum_trunc1__0_PATTERNDETECT_UNCONNECTED;
  wire NLW_sum_trunc1__0_UNDERFLOW_UNCONNECTED;
  wire [29:0]NLW_sum_trunc1__0_ACOUT_UNCONNECTED;
  wire [17:0]NLW_sum_trunc1__0_BCOUT_UNCONNECTED;
  wire [3:0]NLW_sum_trunc1__0_CARRYOUT_UNCONNECTED;
  wire [47:31]NLW_sum_trunc1__0_P_UNCONNECTED;
  wire [47:0]NLW_sum_trunc1__0_PCOUT_UNCONNECTED;

  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[0]_i_3 
       (.I0(p_0_in[3]),
        .I1(D[3]),
        .O(\ov_sum[0]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[0]_i_4 
       (.I0(p_0_in[2]),
        .I1(D[2]),
        .O(\ov_sum[0]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[0]_i_5 
       (.I0(p_0_in[1]),
        .I1(D[1]),
        .O(\ov_sum[0]_i_5_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[0]_i_6 
       (.I0(p_0_in[0]),
        .I1(D[0]),
        .O(\ov_sum[0]_i_6_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[12]_i_2 
       (.I0(p_0_in[15]),
        .I1(D[15]),
        .O(\ov_sum[12]_i_2_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[12]_i_3 
       (.I0(p_0_in[14]),
        .I1(D[14]),
        .O(\ov_sum[12]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[12]_i_4 
       (.I0(p_0_in[13]),
        .I1(D[13]),
        .O(\ov_sum[12]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[12]_i_5 
       (.I0(p_0_in[12]),
        .I1(D[12]),
        .O(\ov_sum[12]_i_5_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[16]_i_2 
       (.I0(p_0_in[19]),
        .I1(D[19]),
        .O(\ov_sum[16]_i_2_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[16]_i_3 
       (.I0(p_0_in[18]),
        .I1(D[18]),
        .O(\ov_sum[16]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[16]_i_4 
       (.I0(p_0_in[17]),
        .I1(D[17]),
        .O(\ov_sum[16]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[16]_i_5 
       (.I0(p_0_in[16]),
        .I1(D[16]),
        .O(\ov_sum[16]_i_5_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[20]_i_2 
       (.I0(D[23]),
        .I1(p_0_in[23]),
        .O(\ov_sum[20]_i_2_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[20]_i_3 
       (.I0(p_0_in[22]),
        .I1(D[22]),
        .O(\ov_sum[20]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[20]_i_4 
       (.I0(p_0_in[21]),
        .I1(D[21]),
        .O(\ov_sum[20]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[20]_i_5 
       (.I0(p_0_in[20]),
        .I1(D[20]),
        .O(\ov_sum[20]_i_5_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[4]_i_2 
       (.I0(p_0_in[7]),
        .I1(D[7]),
        .O(\ov_sum[4]_i_2_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[4]_i_3 
       (.I0(p_0_in[6]),
        .I1(D[6]),
        .O(\ov_sum[4]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[4]_i_4 
       (.I0(p_0_in[5]),
        .I1(D[5]),
        .O(\ov_sum[4]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[4]_i_5 
       (.I0(p_0_in[4]),
        .I1(D[4]),
        .O(\ov_sum[4]_i_5_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[8]_i_2 
       (.I0(p_0_in[11]),
        .I1(D[11]),
        .O(\ov_sum[8]_i_2_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[8]_i_3 
       (.I0(p_0_in[10]),
        .I1(D[10]),
        .O(\ov_sum[8]_i_3_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[8]_i_4 
       (.I0(p_0_in[9]),
        .I1(D[9]),
        .O(\ov_sum[8]_i_4_n_0 ));
  LUT2 #(
    .INIT(4'h6)) 
    \ov_sum[8]_i_5 
       (.I0(p_0_in[8]),
        .I1(D[8]),
        .O(\ov_sum[8]_i_5_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[0] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[0]_i_2_n_7 ),
        .Q(D[0]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[0]_i_2 
       (.CI(1'b0),
        .CO({\ov_sum_reg[0]_i_2_n_0 ,\ov_sum_reg[0]_i_2_n_1 ,\ov_sum_reg[0]_i_2_n_2 ,\ov_sum_reg[0]_i_2_n_3 }),
        .CYINIT(1'b0),
        .DI(p_0_in[3:0]),
        .O({\ov_sum_reg[0]_i_2_n_4 ,\ov_sum_reg[0]_i_2_n_5 ,\ov_sum_reg[0]_i_2_n_6 ,\ov_sum_reg[0]_i_2_n_7 }),
        .S({\ov_sum[0]_i_3_n_0 ,\ov_sum[0]_i_4_n_0 ,\ov_sum[0]_i_5_n_0 ,\ov_sum[0]_i_6_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[10] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[8]_i_1_n_5 ),
        .Q(D[10]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[11] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[8]_i_1_n_4 ),
        .Q(D[11]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[12] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[12]_i_1_n_7 ),
        .Q(D[12]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[12]_i_1 
       (.CI(\ov_sum_reg[8]_i_1_n_0 ),
        .CO({\ov_sum_reg[12]_i_1_n_0 ,\ov_sum_reg[12]_i_1_n_1 ,\ov_sum_reg[12]_i_1_n_2 ,\ov_sum_reg[12]_i_1_n_3 }),
        .CYINIT(1'b0),
        .DI(p_0_in[15:12]),
        .O({\ov_sum_reg[12]_i_1_n_4 ,\ov_sum_reg[12]_i_1_n_5 ,\ov_sum_reg[12]_i_1_n_6 ,\ov_sum_reg[12]_i_1_n_7 }),
        .S({\ov_sum[12]_i_2_n_0 ,\ov_sum[12]_i_3_n_0 ,\ov_sum[12]_i_4_n_0 ,\ov_sum[12]_i_5_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[13] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[12]_i_1_n_6 ),
        .Q(D[13]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[14] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[12]_i_1_n_5 ),
        .Q(D[14]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[15] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[12]_i_1_n_4 ),
        .Q(D[15]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[16] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[16]_i_1_n_7 ),
        .Q(D[16]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[16]_i_1 
       (.CI(\ov_sum_reg[12]_i_1_n_0 ),
        .CO({\ov_sum_reg[16]_i_1_n_0 ,\ov_sum_reg[16]_i_1_n_1 ,\ov_sum_reg[16]_i_1_n_2 ,\ov_sum_reg[16]_i_1_n_3 }),
        .CYINIT(1'b0),
        .DI(p_0_in[19:16]),
        .O({\ov_sum_reg[16]_i_1_n_4 ,\ov_sum_reg[16]_i_1_n_5 ,\ov_sum_reg[16]_i_1_n_6 ,\ov_sum_reg[16]_i_1_n_7 }),
        .S({\ov_sum[16]_i_2_n_0 ,\ov_sum[16]_i_3_n_0 ,\ov_sum[16]_i_4_n_0 ,\ov_sum[16]_i_5_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[17] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[16]_i_1_n_6 ),
        .Q(D[17]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[18] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[16]_i_1_n_5 ),
        .Q(D[18]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[19] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[16]_i_1_n_4 ),
        .Q(D[19]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[1] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[0]_i_2_n_6 ),
        .Q(D[1]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[20] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[20]_i_1_n_7 ),
        .Q(D[20]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[20]_i_1 
       (.CI(\ov_sum_reg[16]_i_1_n_0 ),
        .CO({\NLW_ov_sum_reg[20]_i_1_CO_UNCONNECTED [3],\ov_sum_reg[20]_i_1_n_1 ,\ov_sum_reg[20]_i_1_n_2 ,\ov_sum_reg[20]_i_1_n_3 }),
        .CYINIT(1'b0),
        .DI({1'b0,p_0_in[22:20]}),
        .O({\ov_sum_reg[20]_i_1_n_4 ,\ov_sum_reg[20]_i_1_n_5 ,\ov_sum_reg[20]_i_1_n_6 ,\ov_sum_reg[20]_i_1_n_7 }),
        .S({\ov_sum[20]_i_2_n_0 ,\ov_sum[20]_i_3_n_0 ,\ov_sum[20]_i_4_n_0 ,\ov_sum[20]_i_5_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[21] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[20]_i_1_n_6 ),
        .Q(D[21]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[22] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[20]_i_1_n_5 ),
        .Q(D[22]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[23] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[20]_i_1_n_4 ),
        .Q(D[23]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[2] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[0]_i_2_n_5 ),
        .Q(D[2]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[3] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[0]_i_2_n_4 ),
        .Q(D[3]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[4] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[4]_i_1_n_7 ),
        .Q(D[4]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[4]_i_1 
       (.CI(\ov_sum_reg[0]_i_2_n_0 ),
        .CO({\ov_sum_reg[4]_i_1_n_0 ,\ov_sum_reg[4]_i_1_n_1 ,\ov_sum_reg[4]_i_1_n_2 ,\ov_sum_reg[4]_i_1_n_3 }),
        .CYINIT(1'b0),
        .DI(p_0_in[7:4]),
        .O({\ov_sum_reg[4]_i_1_n_4 ,\ov_sum_reg[4]_i_1_n_5 ,\ov_sum_reg[4]_i_1_n_6 ,\ov_sum_reg[4]_i_1_n_7 }),
        .S({\ov_sum[4]_i_2_n_0 ,\ov_sum[4]_i_3_n_0 ,\ov_sum[4]_i_4_n_0 ,\ov_sum[4]_i_5_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[5] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[4]_i_1_n_6 ),
        .Q(D[5]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[6] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[4]_i_1_n_5 ),
        .Q(D[6]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[7] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[4]_i_1_n_4 ),
        .Q(D[7]),
        .R(\ov_sum_reg[23]_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[8] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[8]_i_1_n_7 ),
        .Q(D[8]),
        .R(\ov_sum_reg[23]_0 ));
  (* ADDER_THRESHOLD = "11" *) 
  CARRY4 \ov_sum_reg[8]_i_1 
       (.CI(\ov_sum_reg[4]_i_1_n_0 ),
        .CO({\ov_sum_reg[8]_i_1_n_0 ,\ov_sum_reg[8]_i_1_n_1 ,\ov_sum_reg[8]_i_1_n_2 ,\ov_sum_reg[8]_i_1_n_3 }),
        .CYINIT(1'b0),
        .DI(p_0_in[11:8]),
        .O({\ov_sum_reg[8]_i_1_n_4 ,\ov_sum_reg[8]_i_1_n_5 ,\ov_sum_reg[8]_i_1_n_6 ,\ov_sum_reg[8]_i_1_n_7 }),
        .S({\ov_sum[8]_i_2_n_0 ,\ov_sum[8]_i_3_n_0 ,\ov_sum[8]_i_4_n_0 ,\ov_sum[8]_i_5_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \ov_sum_reg[9] 
       (.C(i_clk_IBUF),
        .CE(i_en_IBUF),
        .D(\ov_sum_reg[8]_i_1_n_6 ),
        .Q(D[9]),
        .R(\ov_sum_reg[23]_0 ));
  (* METHODOLOGY_DRC_VIOS = "{SYNTH-13 {cell *THIS*}}" *) 
  DSP48E1 #(
    .ACASCREG(0),
    .ADREG(1),
    .ALUMODEREG(0),
    .AREG(0),
    .AUTORESET_PATDET("NO_RESET"),
    .A_INPUT("DIRECT"),
    .BCASCREG(0),
    .BREG(0),
    .B_INPUT("DIRECT"),
    .CARRYINREG(0),
    .CARRYINSELREG(0),
    .CREG(1),
    .DREG(1),
    .INMODEREG(0),
    .MASK(48'h3FFFFFFFFFFF),
    .MREG(0),
    .OPMODEREG(0),
    .PATTERN(48'h000000000000),
    .PREG(0),
    .SEL_MASK("MASK"),
    .SEL_PATTERN("PATTERN"),
    .USE_DPORT("FALSE"),
    .USE_MULT("MULTIPLY"),
    .USE_PATTERN_DETECT("NO_PATDET"),
    .USE_SIMD("ONE48")) 
    sum_trunc1
       (.A({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,sum_trunc1_0}),
        .ACIN({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .ACOUT({sum_trunc1_n_24,sum_trunc1_n_25,sum_trunc1_n_26,sum_trunc1_n_27,sum_trunc1_n_28,sum_trunc1_n_29,sum_trunc1_n_30,sum_trunc1_n_31,sum_trunc1_n_32,sum_trunc1_n_33,sum_trunc1_n_34,sum_trunc1_n_35,sum_trunc1_n_36,sum_trunc1_n_37,sum_trunc1_n_38,sum_trunc1_n_39,sum_trunc1_n_40,sum_trunc1_n_41,sum_trunc1_n_42,sum_trunc1_n_43,sum_trunc1_n_44,sum_trunc1_n_45,sum_trunc1_n_46,sum_trunc1_n_47,sum_trunc1_n_48,sum_trunc1_n_49,sum_trunc1_n_50,sum_trunc1_n_51,sum_trunc1_n_52,sum_trunc1_n_53}),
        .ALUMODE({1'b0,1'b0,1'b0,1'b0}),
        .B({1'b0,douta[16:0]}),
        .BCIN({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .BCOUT(NLW_sum_trunc1_BCOUT_UNCONNECTED[17:0]),
        .C({1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1}),
        .CARRYCASCIN(1'b0),
        .CARRYCASCOUT(NLW_sum_trunc1_CARRYCASCOUT_UNCONNECTED),
        .CARRYIN(1'b0),
        .CARRYINSEL({1'b0,1'b0,1'b0}),
        .CARRYOUT(NLW_sum_trunc1_CARRYOUT_UNCONNECTED[3:0]),
        .CEA1(1'b0),
        .CEA2(1'b0),
        .CEAD(1'b0),
        .CEALUMODE(1'b0),
        .CEB1(1'b0),
        .CEB2(1'b0),
        .CEC(1'b0),
        .CECARRYIN(1'b0),
        .CECTRL(1'b0),
        .CED(1'b0),
        .CEINMODE(1'b0),
        .CEM(1'b0),
        .CEP(1'b0),
        .CLK(1'b0),
        .D({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .INMODE({1'b0,1'b0,1'b0,1'b0,1'b0}),
        .MULTSIGNIN(1'b0),
        .MULTSIGNOUT(NLW_sum_trunc1_MULTSIGNOUT_UNCONNECTED),
        .OPMODE({1'b0,1'b0,1'b0,1'b0,1'b1,1'b0,1'b1}),
        .OVERFLOW(NLW_sum_trunc1_OVERFLOW_UNCONNECTED),
        .P({sum_trunc1_n_58,sum_trunc1_n_59,sum_trunc1_n_60,sum_trunc1_n_61,sum_trunc1_n_62,sum_trunc1_n_63,sum_trunc1_n_64,sum_trunc1_n_65,sum_trunc1_n_66,sum_trunc1_n_67,sum_trunc1_n_68,sum_trunc1_n_69,sum_trunc1_n_70,sum_trunc1_n_71,sum_trunc1_n_72,sum_trunc1_n_73,sum_trunc1_n_74,sum_trunc1_n_75,sum_trunc1_n_76,sum_trunc1_n_77,sum_trunc1_n_78,sum_trunc1_n_79,sum_trunc1_n_80,sum_trunc1_n_81,sum_trunc1_n_82,sum_trunc1_n_83,sum_trunc1_n_84,sum_trunc1_n_85,sum_trunc1_n_86,sum_trunc1_n_87,sum_trunc1_n_88,sum_trunc1_n_89,sum_trunc1_n_90,sum_trunc1_n_91,sum_trunc1_n_92,sum_trunc1_n_93,sum_trunc1_n_94,sum_trunc1_n_95,sum_trunc1_n_96,sum_trunc1_n_97,sum_trunc1_n_98,sum_trunc1_n_99,sum_trunc1_n_100,sum_trunc1_n_101,sum_trunc1_n_102,sum_trunc1_n_103,sum_trunc1_n_104,sum_trunc1_n_105}),
        .PATTERNBDETECT(NLW_sum_trunc1_PATTERNBDETECT_UNCONNECTED),
        .PATTERNDETECT(NLW_sum_trunc1_PATTERNDETECT_UNCONNECTED),
        .PCIN({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .PCOUT({sum_trunc1_n_106,sum_trunc1_n_107,sum_trunc1_n_108,sum_trunc1_n_109,sum_trunc1_n_110,sum_trunc1_n_111,sum_trunc1_n_112,sum_trunc1_n_113,sum_trunc1_n_114,sum_trunc1_n_115,sum_trunc1_n_116,sum_trunc1_n_117,sum_trunc1_n_118,sum_trunc1_n_119,sum_trunc1_n_120,sum_trunc1_n_121,sum_trunc1_n_122,sum_trunc1_n_123,sum_trunc1_n_124,sum_trunc1_n_125,sum_trunc1_n_126,sum_trunc1_n_127,sum_trunc1_n_128,sum_trunc1_n_129,sum_trunc1_n_130,sum_trunc1_n_131,sum_trunc1_n_132,sum_trunc1_n_133,sum_trunc1_n_134,sum_trunc1_n_135,sum_trunc1_n_136,sum_trunc1_n_137,sum_trunc1_n_138,sum_trunc1_n_139,sum_trunc1_n_140,sum_trunc1_n_141,sum_trunc1_n_142,sum_trunc1_n_143,sum_trunc1_n_144,sum_trunc1_n_145,sum_trunc1_n_146,sum_trunc1_n_147,sum_trunc1_n_148,sum_trunc1_n_149,sum_trunc1_n_150,sum_trunc1_n_151,sum_trunc1_n_152,sum_trunc1_n_153}),
        .RSTA(1'b0),
        .RSTALLCARRYIN(1'b0),
        .RSTALUMODE(1'b0),
        .RSTB(1'b0),
        .RSTC(1'b0),
        .RSTCTRL(1'b0),
        .RSTD(1'b0),
        .RSTINMODE(1'b0),
        .RSTM(1'b0),
        .RSTP(1'b0),
        .UNDERFLOW(NLW_sum_trunc1_UNDERFLOW_UNCONNECTED));
  (* METHODOLOGY_DRC_VIOS = "{SYNTH-13 {cell *THIS*}}" *) 
  DSP48E1 #(
    .ACASCREG(0),
    .ADREG(1),
    .ALUMODEREG(0),
    .AREG(0),
    .AUTORESET_PATDET("NO_RESET"),
    .A_INPUT("CASCADE"),
    .BCASCREG(0),
    .BREG(0),
    .B_INPUT("DIRECT"),
    .CARRYINREG(0),
    .CARRYINSELREG(0),
    .CREG(1),
    .DREG(1),
    .INMODEREG(0),
    .MASK(48'h3FFFFFFFFFFF),
    .MREG(0),
    .OPMODEREG(0),
    .PATTERN(48'h000000000000),
    .PREG(0),
    .SEL_MASK("MASK"),
    .SEL_PATTERN("PATTERN"),
    .USE_DPORT("FALSE"),
    .USE_MULT("MULTIPLY"),
    .USE_PATTERN_DETECT("NO_PATDET"),
    .USE_SIMD("ONE48")) 
    sum_trunc1__0
       (.A({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .ACIN({sum_trunc1_n_24,sum_trunc1_n_25,sum_trunc1_n_26,sum_trunc1_n_27,sum_trunc1_n_28,sum_trunc1_n_29,sum_trunc1_n_30,sum_trunc1_n_31,sum_trunc1_n_32,sum_trunc1_n_33,sum_trunc1_n_34,sum_trunc1_n_35,sum_trunc1_n_36,sum_trunc1_n_37,sum_trunc1_n_38,sum_trunc1_n_39,sum_trunc1_n_40,sum_trunc1_n_41,sum_trunc1_n_42,sum_trunc1_n_43,sum_trunc1_n_44,sum_trunc1_n_45,sum_trunc1_n_46,sum_trunc1_n_47,sum_trunc1_n_48,sum_trunc1_n_49,sum_trunc1_n_50,sum_trunc1_n_51,sum_trunc1_n_52,sum_trunc1_n_53}),
        .ACOUT(NLW_sum_trunc1__0_ACOUT_UNCONNECTED[29:0]),
        .ALUMODE({1'b0,1'b0,1'b0,1'b0}),
        .B({douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23],douta[23:17]}),
        .BCIN({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .BCOUT(NLW_sum_trunc1__0_BCOUT_UNCONNECTED[17:0]),
        .C({1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1,1'b1}),
        .CARRYCASCIN(1'b0),
        .CARRYCASCOUT(NLW_sum_trunc1__0_CARRYCASCOUT_UNCONNECTED),
        .CARRYIN(1'b0),
        .CARRYINSEL({1'b0,1'b0,1'b0}),
        .CARRYOUT(NLW_sum_trunc1__0_CARRYOUT_UNCONNECTED[3:0]),
        .CEA1(1'b0),
        .CEA2(1'b0),
        .CEAD(1'b0),
        .CEALUMODE(1'b0),
        .CEB1(1'b0),
        .CEB2(1'b0),
        .CEC(1'b0),
        .CECARRYIN(1'b0),
        .CECTRL(1'b0),
        .CED(1'b0),
        .CEINMODE(1'b0),
        .CEM(1'b0),
        .CEP(1'b0),
        .CLK(1'b0),
        .D({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .INMODE({1'b0,1'b0,1'b0,1'b0,1'b0}),
        .MULTSIGNIN(1'b0),
        .MULTSIGNOUT(NLW_sum_trunc1__0_MULTSIGNOUT_UNCONNECTED),
        .OPMODE({1'b1,1'b0,1'b1,1'b0,1'b1,1'b0,1'b1}),
        .OVERFLOW(NLW_sum_trunc1__0_OVERFLOW_UNCONNECTED),
        .P({NLW_sum_trunc1__0_P_UNCONNECTED[47:31],p_0_in,sum_trunc1__0_n_99,sum_trunc1__0_n_100,sum_trunc1__0_n_101,sum_trunc1__0_n_102,sum_trunc1__0_n_103,sum_trunc1__0_n_104,sum_trunc1__0_n_105}),
        .PATTERNBDETECT(NLW_sum_trunc1__0_PATTERNBDETECT_UNCONNECTED),
        .PATTERNDETECT(NLW_sum_trunc1__0_PATTERNDETECT_UNCONNECTED),
        .PCIN({sum_trunc1_n_106,sum_trunc1_n_107,sum_trunc1_n_108,sum_trunc1_n_109,sum_trunc1_n_110,sum_trunc1_n_111,sum_trunc1_n_112,sum_trunc1_n_113,sum_trunc1_n_114,sum_trunc1_n_115,sum_trunc1_n_116,sum_trunc1_n_117,sum_trunc1_n_118,sum_trunc1_n_119,sum_trunc1_n_120,sum_trunc1_n_121,sum_trunc1_n_122,sum_trunc1_n_123,sum_trunc1_n_124,sum_trunc1_n_125,sum_trunc1_n_126,sum_trunc1_n_127,sum_trunc1_n_128,sum_trunc1_n_129,sum_trunc1_n_130,sum_trunc1_n_131,sum_trunc1_n_132,sum_trunc1_n_133,sum_trunc1_n_134,sum_trunc1_n_135,sum_trunc1_n_136,sum_trunc1_n_137,sum_trunc1_n_138,sum_trunc1_n_139,sum_trunc1_n_140,sum_trunc1_n_141,sum_trunc1_n_142,sum_trunc1_n_143,sum_trunc1_n_144,sum_trunc1_n_145,sum_trunc1_n_146,sum_trunc1_n_147,sum_trunc1_n_148,sum_trunc1_n_149,sum_trunc1_n_150,sum_trunc1_n_151,sum_trunc1_n_152,sum_trunc1_n_153}),
        .PCOUT(NLW_sum_trunc1__0_PCOUT_UNCONNECTED[47:0]),
        .RSTA(1'b0),
        .RSTALLCARRYIN(1'b0),
        .RSTALUMODE(1'b0),
        .RSTB(1'b0),
        .RSTC(1'b0),
        .RSTCTRL(1'b0),
        .RSTD(1'b0),
        .RSTINMODE(1'b0),
        .RSTM(1'b0),
        .RSTP(1'b0),
        .UNDERFLOW(NLW_sum_trunc1__0_UNDERFLOW_UNCONNECTED));
endmodule

(* DATA_WIDTH = "24" *) (* ECO_CHECKSUM = "b8421ce5" *) (* FIR_DEPTH = "16" *) 
(* NotValidForBitStream *)
(* \DesignAttr:ENABLE_NOC_NETLIST_VIEW  *) 
(* \DesignAttr:ENABLE_AIE_NETLIST_VIEW  *) 
module top_level
   (i_clk,
    i_rst,
    i_en,
    i_din,
    i_din_valid,
    i_ready,
    o_ready,
    o_dout,
    o_dout_valid);
  input i_clk;
  input i_rst;
  input i_en;
  input i_din;
  input i_din_valid;
  input i_ready;
  output o_ready;
  output o_dout;
  output o_dout_valid;

  wire des_out_valid;
  wire deserializer_inst_n_2;
  wire [23:0]fir_din;
  wire fir_filter_inst_n_10;
  wire fir_filter_inst_n_11;
  wire fir_filter_inst_n_12;
  wire fir_filter_inst_n_13;
  wire fir_filter_inst_n_14;
  wire fir_filter_inst_n_15;
  wire fir_filter_inst_n_16;
  wire fir_filter_inst_n_17;
  wire fir_filter_inst_n_18;
  wire fir_filter_inst_n_19;
  wire fir_filter_inst_n_2;
  wire fir_filter_inst_n_20;
  wire fir_filter_inst_n_21;
  wire fir_filter_inst_n_22;
  wire fir_filter_inst_n_23;
  wire fir_filter_inst_n_24;
  wire fir_filter_inst_n_25;
  wire fir_filter_inst_n_3;
  wire fir_filter_inst_n_4;
  wire fir_filter_inst_n_5;
  wire fir_filter_inst_n_6;
  wire fir_filter_inst_n_7;
  wire fir_filter_inst_n_8;
  wire fir_filter_inst_n_9;
  wire fir_out_valid;
  wire fir_ready;
  wire i_clk;
  wire i_clk_IBUF;
  wire i_din;
  wire i_din_IBUF;
  wire i_din_valid;
  wire i_din_valid_IBUF;
  wire i_en;
  wire i_en_IBUF;
  wire i_ready;
  wire i_ready_IBUF;
  wire i_rst;
  wire i_rst_IBUF;
  wire o_dout;
  wire o_dout_OBUF;
  wire o_dout_valid;
  wire o_dout_valid_OBUF;
  wire o_ready;
  wire o_ready_OBUF;
  wire ser_ready;
  wire serializer_inst_n_1;

initial begin
 $sdf_annotate("fir_filter_time_impl.sdf",,,,"tool_control");
end
  deserializer_fsm deserializer_inst
       (.Q(fir_din),
        .des_out_valid(des_out_valid),
        .i_clk_IBUF(i_clk_IBUF),
        .i_din_IBUF(i_din_IBUF),
        .i_din_valid_IBUF(i_din_valid_IBUF),
        .i_en_IBUF(i_en_IBUF),
        .i_rst_IBUF(i_rst_IBUF),
        .o_dout_valid_reg_0(deserializer_inst_n_2),
        .o_ready_OBUF(o_ready_OBUF),
        .o_ready_reg_0(serializer_inst_n_1),
        .wea(fir_ready));
  fir_filter_transposed_pipelined fir_filter_inst
       (.Q(fir_din),
        .des_out_valid(des_out_valid),
        .fir_out_valid(fir_out_valid),
        .i_clk_IBUF(i_clk_IBUF),
        .i_en_IBUF(i_en_IBUF),
        .i_rst_IBUF(i_rst_IBUF),
        .\ov_dout_reg[23]_0 ({fir_filter_inst_n_2,fir_filter_inst_n_3,fir_filter_inst_n_4,fir_filter_inst_n_5,fir_filter_inst_n_6,fir_filter_inst_n_7,fir_filter_inst_n_8,fir_filter_inst_n_9,fir_filter_inst_n_10,fir_filter_inst_n_11,fir_filter_inst_n_12,fir_filter_inst_n_13,fir_filter_inst_n_14,fir_filter_inst_n_15,fir_filter_inst_n_16,fir_filter_inst_n_17,fir_filter_inst_n_18,fir_filter_inst_n_19,fir_filter_inst_n_20,fir_filter_inst_n_21,fir_filter_inst_n_22,fir_filter_inst_n_23,fir_filter_inst_n_24,fir_filter_inst_n_25}),
        .\ov_sum_reg[23] (deserializer_inst_n_2),
        .ser_ready(ser_ready),
        .wea(fir_ready));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_clk_IBUF_inst
       (.I(i_clk),
        .O(i_clk_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_din_IBUF_inst
       (.I(i_din),
        .O(i_din_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_din_valid_IBUF_inst
       (.I(i_din_valid),
        .O(i_din_valid_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_en_IBUF_inst
       (.I(i_en),
        .O(i_en_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_ready_IBUF_inst
       (.I(i_ready),
        .O(i_ready_IBUF));
  (* io_buffer_type = "ibuf" *) 
  IBUF i_rst_IBUF_inst
       (.I(i_rst),
        .O(i_rst_IBUF));
  (* io_buffer_type = "obuf" *) 
  OBUF o_dout_OBUF_inst
       (.I(o_dout_OBUF),
        .O(o_dout));
  (* io_buffer_type = "obuf" *) 
  OBUF o_dout_valid_OBUF_inst
       (.I(o_dout_valid_OBUF),
        .O(o_dout_valid));
  (* io_buffer_type = "obuf" *) 
  OBUF o_ready_OBUF_inst
       (.I(o_ready_OBUF),
        .O(o_ready));
  serializer_fsm serializer_inst
       (.Q(o_dout_OBUF),
        .fir_out_valid(fir_out_valid),
        .i_clk_IBUF(i_clk_IBUF),
        .i_en_IBUF(i_en_IBUF),
        .i_ready_IBUF(i_ready_IBUF),
        .i_rst(serializer_inst_n_1),
        .i_rst_IBUF(i_rst_IBUF),
        .o_dout_valid_OBUF(o_dout_valid_OBUF),
        .ser_ready(ser_ready),
        .\shift_reg_reg[23]_0 ({fir_filter_inst_n_2,fir_filter_inst_n_3,fir_filter_inst_n_4,fir_filter_inst_n_5,fir_filter_inst_n_6,fir_filter_inst_n_7,fir_filter_inst_n_8,fir_filter_inst_n_9,fir_filter_inst_n_10,fir_filter_inst_n_11,fir_filter_inst_n_12,fir_filter_inst_n_13,fir_filter_inst_n_14,fir_filter_inst_n_15,fir_filter_inst_n_16,fir_filter_inst_n_17,fir_filter_inst_n_18,fir_filter_inst_n_19,fir_filter_inst_n_20,fir_filter_inst_n_21,fir_filter_inst_n_22,fir_filter_inst_n_23,fir_filter_inst_n_24,fir_filter_inst_n_25}));
endmodule

(* ADDR_WIDTH_A = "4" *) (* ADDR_WIDTH_B = "4" *) (* AUTO_SLEEP_TIME = "0" *) 
(* BYTE_WRITE_WIDTH_A = "24" *) (* BYTE_WRITE_WIDTH_B = "24" *) (* CASCADE_HEIGHT = "0" *) 
(* CLOCKING_MODE = "0" *) (* ECC_BIT_RANGE = "7:0" *) (* ECC_MODE = "0" *) 
(* ECC_TYPE = "none" *) (* IGNORE_INIT_SYNTH = "0" *) (* MAX_NUM_CHAR = "0" *) 
(* MEMORY_INIT_FILE = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/weights.mem" *) (* MEMORY_INIT_PARAM = "0" *) (* MEMORY_OPTIMIZATION = "true" *) 
(* MEMORY_PRIMITIVE = "0" *) (* MEMORY_SIZE = "384" *) (* MEMORY_TYPE = "3" *) 
(* MESSAGE_CONTROL = "0" *) (* NUM_CHAR_LOC = "0" *) (* P_ECC_MODE = "0" *) 
(* P_ENABLE_BYTE_WRITE_A = "0" *) (* P_ENABLE_BYTE_WRITE_B = "0" *) (* P_MAX_DEPTH_DATA = "16" *) 
(* P_MEMORY_OPT = "yes" *) (* P_MEMORY_PRIMITIVE = "0" *) (* P_MIN_WIDTH_DATA = "24" *) 
(* P_MIN_WIDTH_DATA_A = "24" *) (* P_MIN_WIDTH_DATA_B = "24" *) (* P_MIN_WIDTH_DATA_ECC = "24" *) 
(* P_MIN_WIDTH_DATA_LDW = "4" *) (* P_MIN_WIDTH_DATA_SHFT = "24" *) (* P_NUM_COLS_WRITE_A = "1" *) 
(* P_NUM_COLS_WRITE_B = "1" *) (* P_NUM_ROWS_READ_A = "1" *) (* P_NUM_ROWS_READ_B = "1" *) 
(* P_NUM_ROWS_WRITE_A = "1" *) (* P_NUM_ROWS_WRITE_B = "1" *) (* P_SDP_WRITE_MODE = "yes" *) 
(* P_WIDTH_ADDR_LSB_READ_A = "0" *) (* P_WIDTH_ADDR_LSB_READ_B = "0" *) (* P_WIDTH_ADDR_LSB_WRITE_A = "0" *) 
(* P_WIDTH_ADDR_LSB_WRITE_B = "0" *) (* P_WIDTH_ADDR_READ_A = "4" *) (* P_WIDTH_ADDR_READ_B = "4" *) 
(* P_WIDTH_ADDR_WRITE_A = "4" *) (* P_WIDTH_ADDR_WRITE_B = "4" *) (* P_WIDTH_COL_WRITE_A = "24" *) 
(* P_WIDTH_COL_WRITE_B = "24" *) (* RAM_DECOMP = "auto" *) (* READ_DATA_WIDTH_A = "24" *) 
(* READ_DATA_WIDTH_B = "24" *) (* READ_LATENCY_A = "1" *) (* READ_LATENCY_B = "1" *) 
(* READ_RESET_VALUE_A = "0" *) (* READ_RESET_VALUE_B = "0" *) (* RST_MODE_A = "SYNC" *) 
(* RST_MODE_B = "SYNC" *) (* SIM_ASSERT_CHK = "0" *) (* USE_EMBEDDED_CONSTRAINT = "0" *) 
(* USE_MEM_INIT = "1" *) (* USE_MEM_INIT_MMI = "0" *) (* VERSION = "0" *) 
(* WAKEUP_TIME = "0" *) (* WRITE_DATA_WIDTH_A = "24" *) (* WRITE_DATA_WIDTH_B = "24" *) 
(* WRITE_MODE_A = "1" *) (* WRITE_MODE_B = "1" *) (* WRITE_PROTECT = "1" *) 
(* XPM_MODULE = "TRUE" *) (* keep_hierarchy = "soft" *) (* rsta_loop_iter = "24" *) 
(* rstb_loop_iter = "24" *) 
module xpm_memory_base
   (sleep,
    clka,
    rsta,
    ena,
    regcea,
    wea,
    addra,
    dina,
    injectsbiterra,
    injectdbiterra,
    douta,
    sbiterra,
    dbiterra,
    clkb,
    rstb,
    enb,
    regceb,
    web,
    addrb,
    dinb,
    injectsbiterrb,
    injectdbiterrb,
    doutb,
    sbiterrb,
    dbiterrb);
  input sleep;
  input clka;
  input rsta;
  input ena;
  input regcea;
  input [0:0]wea;
  input [3:0]addra;
  input [23:0]dina;
  input injectsbiterra;
  input injectdbiterra;
  output [23:0]douta;
  output sbiterra;
  output dbiterra;
  input clkb;
  input rstb;
  input enb;
  input regceb;
  input [0:0]web;
  input [3:0]addrb;
  input [23:0]dinb;
  input injectsbiterrb;
  input injectdbiterrb;
  output [23:0]doutb;
  output sbiterrb;
  output dbiterrb;

  wire \<const0> ;
  wire [3:0]addra;
  wire clka;
  wire [18:0]\^douta ;
  wire ena;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[0]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[10]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[11]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[12]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[13]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[14]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[15]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[16]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[17]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[1]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[2]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[3]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[4]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[5]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[6]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[7]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[8]_i_1_n_0 ;
  wire \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[9]_i_1_n_0 ;
  wire rsta;
  wire sleep;

  assign dbiterra = \<const0> ;
  assign dbiterrb = \<const0> ;
  assign douta[23] = \<const0> ;
  assign douta[22] = \<const0> ;
  assign douta[21] = \<const0> ;
  assign douta[20] = \<const0> ;
  assign douta[19] = \<const0> ;
  assign douta[18:0] = \^douta [18:0];
  assign doutb[23] = \<const0> ;
  assign doutb[22] = \<const0> ;
  assign doutb[21] = \<const0> ;
  assign doutb[20] = \<const0> ;
  assign doutb[19] = \<const0> ;
  assign doutb[18] = \<const0> ;
  assign doutb[17] = \<const0> ;
  assign doutb[16] = \<const0> ;
  assign doutb[15] = \<const0> ;
  assign doutb[14] = \<const0> ;
  assign doutb[13] = \<const0> ;
  assign doutb[12] = \<const0> ;
  assign doutb[11] = \<const0> ;
  assign doutb[10] = \<const0> ;
  assign doutb[9] = \<const0> ;
  assign doutb[8] = \<const0> ;
  assign doutb[7] = \<const0> ;
  assign doutb[6] = \<const0> ;
  assign doutb[5] = \<const0> ;
  assign doutb[4] = \<const0> ;
  assign doutb[3] = \<const0> ;
  assign doutb[2] = \<const0> ;
  assign doutb[1] = \<const0> ;
  assign doutb[0] = \<const0> ;
  assign sbiterra = \<const0> ;
  assign sbiterrb = \<const0> ;
  GND GND
       (.G(\<const0> ));
  (* SOFT_HLUTNM = "soft_lutpair15" *) 
  LUT4 #(
    .INIT(16'hEE92)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[0]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[0]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair20" *) 
  LUT4 #(
    .INIT(16'hD370)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[10]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[10]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair20" *) 
  LUT4 #(
    .INIT(16'h2073)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[11]_i_1 
       (.I0(addra[3]),
        .I1(addra[1]),
        .I2(addra[0]),
        .I3(addra[2]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[11]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair21" *) 
  LUT4 #(
    .INIT(16'hB483)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[12]_i_1 
       (.I0(addra[2]),
        .I1(addra[3]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[12]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair21" *) 
  LUT4 #(
    .INIT(16'h1BF4)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[13]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[13]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair22" *) 
  LUT4 #(
    .INIT(16'h26DA)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[14]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[14]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair22" *) 
  LUT4 #(
    .INIT(16'hC73D)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[15]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[0]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[15]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair23" *) 
  LUT4 #(
    .INIT(16'hD8A4)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[16]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[16]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair23" *) 
  LUT4 #(
    .INIT(16'hECC8)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[17]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[0]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[17]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair15" *) 
  LUT4 #(
    .INIT(16'h89DD)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[1]_i_1 
       (.I0(addra[3]),
        .I1(addra[1]),
        .I2(addra[0]),
        .I3(addra[2]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[1]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair16" *) 
  LUT4 #(
    .INIT(16'hCC86)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[2]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[0]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[2]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair16" *) 
  LUT4 #(
    .INIT(16'hFCB9)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[3]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[3]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair17" *) 
  LUT4 #(
    .INIT(16'h0373)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[4]_i_1 
       (.I0(addra[0]),
        .I1(addra[3]),
        .I2(addra[2]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[4]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair17" *) 
  LUT4 #(
    .INIT(16'h173A)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[5]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[5]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair18" *) 
  LUT4 #(
    .INIT(16'h545B)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[6]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[1]),
        .I3(addra[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[6]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair18" *) 
  LUT4 #(
    .INIT(16'hA6ED)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[7]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[0]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[7]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair19" *) 
  LUT4 #(
    .INIT(16'h88CB)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[8]_i_1 
       (.I0(addra[3]),
        .I1(addra[2]),
        .I2(addra[0]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[8]_i_1_n_0 ));
  (* SOFT_HLUTNM = "soft_lutpair19" *) 
  LUT4 #(
    .INIT(16'hA2B5)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[9]_i_1 
       (.I0(addra[3]),
        .I1(addra[0]),
        .I2(addra[2]),
        .I3(addra[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[9]_i_1_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[0] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[0]_i_1_n_0 ),
        .Q(\^douta [0]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[10] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[10]_i_1_n_0 ),
        .Q(\^douta [10]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[11] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[11]_i_1_n_0 ),
        .Q(\^douta [11]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[12] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[12]_i_1_n_0 ),
        .Q(\^douta [12]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[13] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[13]_i_1_n_0 ),
        .Q(\^douta [13]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[14] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[14]_i_1_n_0 ),
        .Q(\^douta [14]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[15] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[15]_i_1_n_0 ),
        .Q(\^douta [15]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[16] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[16]_i_1_n_0 ),
        .Q(\^douta [16]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[17] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[17]_i_1_n_0 ),
        .Q(\^douta [17]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[18] 
       (.C(clka),
        .CE(ena),
        .D(addra[3]),
        .Q(\^douta [18]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[1] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[1]_i_1_n_0 ),
        .Q(\^douta [1]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[2] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[2]_i_1_n_0 ),
        .Q(\^douta [2]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[3] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[3]_i_1_n_0 ),
        .Q(\^douta [3]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[4] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[4]_i_1_n_0 ),
        .Q(\^douta [4]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[5] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[5]_i_1_n_0 ),
        .Q(\^douta [5]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[6] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[6]_i_1_n_0 ),
        .Q(\^douta [6]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[7] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[7]_i_1_n_0 ),
        .Q(\^douta [7]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[8] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[8]_i_1_n_0 ),
        .Q(\^douta [8]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[9] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg[9]_i_1_n_0 ),
        .Q(\^douta [9]),
        .R(rsta));
endmodule

(* ADDR_WIDTH_A = "4" *) (* ADDR_WIDTH_B = "4" *) (* AUTO_SLEEP_TIME = "0" *) 
(* BYTE_WRITE_WIDTH_A = "24" *) (* BYTE_WRITE_WIDTH_B = "24" *) (* CASCADE_HEIGHT = "0" *) 
(* CLOCKING_MODE = "0" *) (* ECC_BIT_RANGE = "7:0" *) (* ECC_MODE = "0" *) 
(* ECC_TYPE = "none" *) (* IGNORE_INIT_SYNTH = "0" *) (* MAX_NUM_CHAR = "0" *) 
(* MEMORY_INIT_FILE = "none" *) (* MEMORY_INIT_PARAM = "0" *) (* MEMORY_OPTIMIZATION = "true" *) 
(* MEMORY_PRIMITIVE = "0" *) (* MEMORY_SIZE = "384" *) (* MEMORY_TYPE = "0" *) 
(* MESSAGE_CONTROL = "0" *) (* NUM_CHAR_LOC = "0" *) (* ORIG_REF_NAME = "xpm_memory_base" *) 
(* P_ECC_MODE = "0" *) (* P_ENABLE_BYTE_WRITE_A = "0" *) (* P_ENABLE_BYTE_WRITE_B = "0" *) 
(* P_MAX_DEPTH_DATA = "16" *) (* P_MEMORY_OPT = "yes" *) (* P_MEMORY_PRIMITIVE = "0" *) 
(* P_MIN_WIDTH_DATA = "24" *) (* P_MIN_WIDTH_DATA_A = "24" *) (* P_MIN_WIDTH_DATA_B = "24" *) 
(* P_MIN_WIDTH_DATA_ECC = "24" *) (* P_MIN_WIDTH_DATA_LDW = "4" *) (* P_MIN_WIDTH_DATA_SHFT = "24" *) 
(* P_NUM_COLS_WRITE_A = "1" *) (* P_NUM_COLS_WRITE_B = "1" *) (* P_NUM_ROWS_READ_A = "1" *) 
(* P_NUM_ROWS_READ_B = "1" *) (* P_NUM_ROWS_WRITE_A = "1" *) (* P_NUM_ROWS_WRITE_B = "1" *) 
(* P_SDP_WRITE_MODE = "yes" *) (* P_WIDTH_ADDR_LSB_READ_A = "0" *) (* P_WIDTH_ADDR_LSB_READ_B = "0" *) 
(* P_WIDTH_ADDR_LSB_WRITE_A = "0" *) (* P_WIDTH_ADDR_LSB_WRITE_B = "0" *) (* P_WIDTH_ADDR_READ_A = "4" *) 
(* P_WIDTH_ADDR_READ_B = "4" *) (* P_WIDTH_ADDR_WRITE_A = "4" *) (* P_WIDTH_ADDR_WRITE_B = "4" *) 
(* P_WIDTH_COL_WRITE_A = "24" *) (* P_WIDTH_COL_WRITE_B = "24" *) (* RAM_DECOMP = "auto" *) 
(* READ_DATA_WIDTH_A = "24" *) (* READ_DATA_WIDTH_B = "24" *) (* READ_LATENCY_A = "1" *) 
(* READ_LATENCY_B = "1" *) (* READ_RESET_VALUE_A = "0" *) (* READ_RESET_VALUE_B = "0" *) 
(* RST_MODE_A = "SYNC" *) (* RST_MODE_B = "SYNC" *) (* SIM_ASSERT_CHK = "0" *) 
(* USE_EMBEDDED_CONSTRAINT = "0" *) (* USE_MEM_INIT = "1" *) (* USE_MEM_INIT_MMI = "0" *) 
(* VERSION = "0" *) (* WAKEUP_TIME = "0" *) (* WRITE_DATA_WIDTH_A = "24" *) 
(* WRITE_DATA_WIDTH_B = "24" *) (* WRITE_MODE_A = "1" *) (* WRITE_MODE_B = "1" *) 
(* WRITE_PROTECT = "1" *) (* XPM_MODULE = "TRUE" *) (* keep_hierarchy = "soft" *) 
(* rsta_loop_iter = "24" *) (* rstb_loop_iter = "24" *) 
module xpm_memory_base__parameterized0
   (sleep,
    clka,
    rsta,
    ena,
    regcea,
    wea,
    addra,
    dina,
    injectsbiterra,
    injectdbiterra,
    douta,
    sbiterra,
    dbiterra,
    clkb,
    rstb,
    enb,
    regceb,
    web,
    addrb,
    dinb,
    injectsbiterrb,
    injectdbiterrb,
    doutb,
    sbiterrb,
    dbiterrb);
  input sleep;
  input clka;
  input rsta;
  input ena;
  input regcea;
  input [0:0]wea;
  input [3:0]addra;
  input [23:0]dina;
  input injectsbiterra;
  input injectdbiterra;
  output [23:0]douta;
  output sbiterra;
  output dbiterra;
  input clkb;
  input rstb;
  input enb;
  input regceb;
  input [0:0]web;
  input [3:0]addrb;
  input [23:0]dinb;
  input injectsbiterrb;
  input injectdbiterrb;
  output [23:0]doutb;
  output sbiterrb;
  output dbiterrb;

  wire \<const0> ;
  wire [3:0]addra;
  wire clka;
  wire [23:0]dina;
  wire [23:0]douta;
  wire ena;
  wire [23:0]\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 ;
  wire p_0_in;
  wire rsta;
  wire sleep;
  wire [0:0]wea;

  assign dbiterra = \<const0> ;
  assign dbiterrb = \<const0> ;
  assign doutb[23] = \<const0> ;
  assign doutb[22] = \<const0> ;
  assign doutb[21] = \<const0> ;
  assign doutb[20] = \<const0> ;
  assign doutb[19] = \<const0> ;
  assign doutb[18] = \<const0> ;
  assign doutb[17] = \<const0> ;
  assign doutb[16] = \<const0> ;
  assign doutb[15] = \<const0> ;
  assign doutb[14] = \<const0> ;
  assign doutb[13] = \<const0> ;
  assign doutb[12] = \<const0> ;
  assign doutb[11] = \<const0> ;
  assign doutb[10] = \<const0> ;
  assign doutb[9] = \<const0> ;
  assign doutb[8] = \<const0> ;
  assign doutb[7] = \<const0> ;
  assign doutb[6] = \<const0> ;
  assign doutb[5] = \<const0> ;
  assign doutb[4] = \<const0> ;
  assign doutb[3] = \<const0> ;
  assign doutb[2] = \<const0> ;
  assign doutb[1] = \<const0> ;
  assign doutb[0] = \<const0> ;
  assign sbiterra = \<const0> ;
  assign sbiterrb = \<const0> ;
  GND GND
       (.G(\<const0> ));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[0] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [0]),
        .Q(douta[0]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[10] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [10]),
        .Q(douta[10]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[11] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [11]),
        .Q(douta[11]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[12] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [12]),
        .Q(douta[12]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[13] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [13]),
        .Q(douta[13]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[14] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [14]),
        .Q(douta[14]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[15] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [15]),
        .Q(douta[15]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[16] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [16]),
        .Q(douta[16]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[17] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [17]),
        .Q(douta[17]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[18] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [18]),
        .Q(douta[18]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[19] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [19]),
        .Q(douta[19]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[1] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [1]),
        .Q(douta[1]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[20] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [20]),
        .Q(douta[20]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[21] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [21]),
        .Q(douta[21]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[22] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [22]),
        .Q(douta[22]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [23]),
        .Q(douta[23]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[2] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [2]),
        .Q(douta[2]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[3] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [3]),
        .Q(douta[3]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[4] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [4]),
        .Q(douta[4]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[5] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [5]),
        .Q(douta[5]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[6] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [6]),
        .Q(douta[6]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[7] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [7]),
        .Q(douta[7]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[8] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [8]),
        .Q(douta[8]),
        .R(rsta));
  FDRE #(
    .INIT(1'b0)) 
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[9] 
       (.C(clka),
        .CE(ena),
        .D(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [9]),
        .Q(douta[9]),
        .R(rsta));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "0" *) 
  (* ram_slice_end = "0" *) 
  RAM32X1S_UNIQ_BASE_ \gen_wr_a.gen_word_narrow.mem_reg_0_15_0_0 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[0]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [0]),
        .WCLK(clka),
        .WE(p_0_in));
  LUT2 #(
    .INIT(4'h8)) 
    \gen_wr_a.gen_word_narrow.mem_reg_0_15_0_0_i_1 
       (.I0(wea),
        .I1(ena),
        .O(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "10" *) 
  (* ram_slice_end = "10" *) 
  RAM32X1S_HD1 \gen_wr_a.gen_word_narrow.mem_reg_0_15_10_10 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[10]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [10]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "11" *) 
  (* ram_slice_end = "11" *) 
  RAM32X1S_HD2 \gen_wr_a.gen_word_narrow.mem_reg_0_15_11_11 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[11]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [11]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "12" *) 
  (* ram_slice_end = "12" *) 
  RAM32X1S_HD3 \gen_wr_a.gen_word_narrow.mem_reg_0_15_12_12 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[12]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [12]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "13" *) 
  (* ram_slice_end = "13" *) 
  RAM32X1S_HD4 \gen_wr_a.gen_word_narrow.mem_reg_0_15_13_13 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[13]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [13]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "14" *) 
  (* ram_slice_end = "14" *) 
  RAM32X1S_HD5 \gen_wr_a.gen_word_narrow.mem_reg_0_15_14_14 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[14]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [14]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "15" *) 
  (* ram_slice_end = "15" *) 
  RAM32X1S_HD6 \gen_wr_a.gen_word_narrow.mem_reg_0_15_15_15 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[15]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [15]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "16" *) 
  (* ram_slice_end = "16" *) 
  RAM32X1S_HD7 \gen_wr_a.gen_word_narrow.mem_reg_0_15_16_16 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[16]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [16]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "17" *) 
  (* ram_slice_end = "17" *) 
  RAM32X1S_HD8 \gen_wr_a.gen_word_narrow.mem_reg_0_15_17_17 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[17]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [17]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "18" *) 
  (* ram_slice_end = "18" *) 
  RAM32X1S_HD9 \gen_wr_a.gen_word_narrow.mem_reg_0_15_18_18 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[18]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [18]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "19" *) 
  (* ram_slice_end = "19" *) 
  RAM32X1S_HD10 \gen_wr_a.gen_word_narrow.mem_reg_0_15_19_19 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[19]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [19]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "1" *) 
  (* ram_slice_end = "1" *) 
  RAM32X1S_HD11 \gen_wr_a.gen_word_narrow.mem_reg_0_15_1_1 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[1]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [1]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "20" *) 
  (* ram_slice_end = "20" *) 
  RAM32X1S_HD12 \gen_wr_a.gen_word_narrow.mem_reg_0_15_20_20 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[20]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [20]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "21" *) 
  (* ram_slice_end = "21" *) 
  RAM32X1S_HD13 \gen_wr_a.gen_word_narrow.mem_reg_0_15_21_21 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[21]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [21]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "22" *) 
  (* ram_slice_end = "22" *) 
  RAM32X1S_HD14 \gen_wr_a.gen_word_narrow.mem_reg_0_15_22_22 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[22]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [22]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "23" *) 
  (* ram_slice_end = "23" *) 
  RAM32X1S_HD15 \gen_wr_a.gen_word_narrow.mem_reg_0_15_23_23 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[23]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [23]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "2" *) 
  (* ram_slice_end = "2" *) 
  RAM32X1S_HD16 \gen_wr_a.gen_word_narrow.mem_reg_0_15_2_2 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[2]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [2]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "3" *) 
  (* ram_slice_end = "3" *) 
  RAM32X1S_HD17 \gen_wr_a.gen_word_narrow.mem_reg_0_15_3_3 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[3]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [3]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "4" *) 
  (* ram_slice_end = "4" *) 
  RAM32X1S_HD18 \gen_wr_a.gen_word_narrow.mem_reg_0_15_4_4 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[4]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [4]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "5" *) 
  (* ram_slice_end = "5" *) 
  RAM32X1S_HD19 \gen_wr_a.gen_word_narrow.mem_reg_0_15_5_5 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[5]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [5]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "6" *) 
  (* ram_slice_end = "6" *) 
  RAM32X1S_HD20 \gen_wr_a.gen_word_narrow.mem_reg_0_15_6_6 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[6]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [6]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "7" *) 
  (* ram_slice_end = "7" *) 
  RAM32X1S_HD21 \gen_wr_a.gen_word_narrow.mem_reg_0_15_7_7 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[7]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [7]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "8" *) 
  (* ram_slice_end = "8" *) 
  RAM32X1S_HD22 \gen_wr_a.gen_word_narrow.mem_reg_0_15_8_8 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[8]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [8]),
        .WCLK(clka),
        .WE(p_0_in));
  (* INIT = "32'h00000000" *) 
  (* RTL_RAM_BITS = "384" *) 
  (* RTL_RAM_NAME = "gen_wr_a.gen_word_narrow.mem_reg" *) 
  (* RTL_RAM_TYPE = "RAM_SP" *) 
  (* XILINX_LEGACY_PRIM = "RAM16X1S" *) 
  (* XILINX_TRANSFORM_PINMAP = "GND:A4" *) 
  (* ram_addr_begin = "0" *) 
  (* ram_addr_end = "15" *) 
  (* ram_offset = "0" *) 
  (* ram_slice_begin = "9" *) 
  (* ram_slice_end = "9" *) 
  RAM32X1S_HD23 \gen_wr_a.gen_word_narrow.mem_reg_0_15_9_9 
       (.A0(addra[0]),
        .A1(addra[1]),
        .A2(addra[2]),
        .A3(addra[3]),
        .A4(1'b0),
        .D(dina[9]),
        .O(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg0 [9]),
        .WCLK(clka),
        .WE(p_0_in));
endmodule

module xpm_memory_spram
   (douta,
    i_clk_IBUF,
    i_rst_IBUF,
    i_en_IBUF,
    wea,
    Q,
    \gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] );
  output [23:0]douta;
  input i_clk_IBUF;
  input i_rst_IBUF;
  input i_en_IBUF;
  input [0:0]wea;
  input [3:0]Q;
  input [23:0]\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] ;

  wire [3:0]Q;
  wire [23:0]douta;
  wire [23:0]\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] ;
  wire i_clk_IBUF;
  wire i_en_IBUF;
  wire i_rst_IBUF;
  wire [0:0]wea;
  wire NLW_xpm_memory_base_inst_dbiterra_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_dbiterrb_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_sbiterra_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_sbiterrb_UNCONNECTED;
  wire [23:0]NLW_xpm_memory_base_inst_doutb_UNCONNECTED;

  (* ADDR_WIDTH_A = "4" *) 
  (* ADDR_WIDTH_B = "4" *) 
  (* AUTO_SLEEP_TIME = "0" *) 
  (* BYTE_WRITE_WIDTH_A = "24" *) 
  (* BYTE_WRITE_WIDTH_B = "24" *) 
  (* CASCADE_HEIGHT = "0" *) 
  (* CLOCKING_MODE = "0" *) 
  (* ECC_BIT_RANGE = "7:0" *) 
  (* ECC_MODE = "0" *) 
  (* ECC_TYPE = "none" *) 
  (* IGNORE_INIT_SYNTH = "0" *) 
  (* KEEP_HIERARCHY = "soft" *) 
  (* MAX_NUM_CHAR = "0" *) 
  (* MEMORY_INIT_FILE = "none" *) 
  (* MEMORY_INIT_PARAM = "0" *) 
  (* MEMORY_OPTIMIZATION = "true" *) 
  (* MEMORY_PRIMITIVE = "0" *) 
  (* MEMORY_SIZE = "384" *) 
  (* MEMORY_TYPE = "0" *) 
  (* MESSAGE_CONTROL = "0" *) 
  (* NUM_CHAR_LOC = "0" *) 
  (* P_ECC_MODE = "no_ecc" *) 
  (* P_ENABLE_BYTE_WRITE_A = "0" *) 
  (* P_ENABLE_BYTE_WRITE_B = "0" *) 
  (* P_MAX_DEPTH_DATA = "16" *) 
  (* P_MEMORY_OPT = "yes" *) 
  (* P_MEMORY_PRIMITIVE = "auto" *) 
  (* P_MIN_WIDTH_DATA = "24" *) 
  (* P_MIN_WIDTH_DATA_A = "24" *) 
  (* P_MIN_WIDTH_DATA_B = "24" *) 
  (* P_MIN_WIDTH_DATA_ECC = "24" *) 
  (* P_MIN_WIDTH_DATA_LDW = "4" *) 
  (* P_MIN_WIDTH_DATA_SHFT = "24" *) 
  (* P_NUM_COLS_WRITE_A = "1" *) 
  (* P_NUM_COLS_WRITE_B = "1" *) 
  (* P_NUM_ROWS_READ_A = "1" *) 
  (* P_NUM_ROWS_READ_B = "1" *) 
  (* P_NUM_ROWS_WRITE_A = "1" *) 
  (* P_NUM_ROWS_WRITE_B = "1" *) 
  (* P_SDP_WRITE_MODE = "yes" *) 
  (* P_WIDTH_ADDR_LSB_READ_A = "0" *) 
  (* P_WIDTH_ADDR_LSB_READ_B = "0" *) 
  (* P_WIDTH_ADDR_LSB_WRITE_A = "0" *) 
  (* P_WIDTH_ADDR_LSB_WRITE_B = "0" *) 
  (* P_WIDTH_ADDR_READ_A = "4" *) 
  (* P_WIDTH_ADDR_READ_B = "4" *) 
  (* P_WIDTH_ADDR_WRITE_A = "4" *) 
  (* P_WIDTH_ADDR_WRITE_B = "4" *) 
  (* P_WIDTH_COL_WRITE_A = "24" *) 
  (* P_WIDTH_COL_WRITE_B = "24" *) 
  (* RAM_DECOMP = "auto" *) 
  (* READ_DATA_WIDTH_A = "24" *) 
  (* READ_DATA_WIDTH_B = "24" *) 
  (* READ_LATENCY_A = "1" *) 
  (* READ_LATENCY_B = "1" *) 
  (* READ_RESET_VALUE_A = "0" *) 
  (* READ_RESET_VALUE_B = "0" *) 
  (* RST_MODE_A = "SYNC" *) 
  (* RST_MODE_B = "SYNC" *) 
  (* SIM_ASSERT_CHK = "0" *) 
  (* USE_EMBEDDED_CONSTRAINT = "0" *) 
  (* USE_MEM_INIT = "1" *) 
  (* USE_MEM_INIT_MMI = "0" *) 
  (* VERSION = "0" *) 
  (* WAKEUP_TIME = "0" *) 
  (* WRITE_DATA_WIDTH_A = "24" *) 
  (* WRITE_DATA_WIDTH_B = "24" *) 
  (* WRITE_MODE_A = "1" *) 
  (* WRITE_MODE_B = "1" *) 
  (* WRITE_PROTECT = "1" *) 
  (* XPM_MODULE = "TRUE" *) 
  (* rsta_loop_iter = "24" *) 
  (* rstb_loop_iter = "24" *) 
  xpm_memory_base__parameterized0 xpm_memory_base_inst
       (.addra(Q),
        .addrb({1'b0,1'b0,1'b0,1'b0}),
        .clka(i_clk_IBUF),
        .clkb(1'b0),
        .dbiterra(NLW_xpm_memory_base_inst_dbiterra_UNCONNECTED),
        .dbiterrb(NLW_xpm_memory_base_inst_dbiterrb_UNCONNECTED),
        .dina(\gen_rd_a.gen_rd_a_synth_template.gen_rf_narrow_reg.douta_reg_reg[23] ),
        .dinb({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .douta(douta),
        .doutb(NLW_xpm_memory_base_inst_doutb_UNCONNECTED[23:0]),
        .ena(i_en_IBUF),
        .enb(1'b0),
        .injectdbiterra(1'b0),
        .injectdbiterrb(1'b0),
        .injectsbiterra(1'b0),
        .injectsbiterrb(1'b0),
        .regcea(1'b0),
        .regceb(1'b0),
        .rsta(i_rst_IBUF),
        .rstb(1'b0),
        .sbiterra(NLW_xpm_memory_base_inst_sbiterra_UNCONNECTED),
        .sbiterrb(NLW_xpm_memory_base_inst_sbiterrb_UNCONNECTED),
        .sleep(1'b0),
        .wea(wea),
        .web(1'b0));
endmodule

module xpm_memory_sprom
   (douta,
    i_clk_IBUF,
    i_rst_IBUF,
    ena,
    Q);
  output [18:0]douta;
  input i_clk_IBUF;
  input i_rst_IBUF;
  input ena;
  input [3:0]Q;

  wire [3:0]Q;
  wire [18:0]douta;
  wire ena;
  wire i_clk_IBUF;
  wire i_rst_IBUF;
  wire NLW_xpm_memory_base_inst_dbiterra_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_dbiterrb_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_sbiterra_UNCONNECTED;
  wire NLW_xpm_memory_base_inst_sbiterrb_UNCONNECTED;
  wire [23:19]NLW_xpm_memory_base_inst_douta_UNCONNECTED;
  wire [23:0]NLW_xpm_memory_base_inst_doutb_UNCONNECTED;

  (* ADDR_WIDTH_A = "4" *) 
  (* ADDR_WIDTH_B = "4" *) 
  (* AUTO_SLEEP_TIME = "0" *) 
  (* BYTE_WRITE_WIDTH_A = "24" *) 
  (* BYTE_WRITE_WIDTH_B = "24" *) 
  (* CASCADE_HEIGHT = "0" *) 
  (* CLOCKING_MODE = "0" *) 
  (* ECC_BIT_RANGE = "7:0" *) 
  (* ECC_MODE = "0" *) 
  (* ECC_TYPE = "none" *) 
  (* IGNORE_INIT_SYNTH = "0" *) 
  (* KEEP_HIERARCHY = "soft" *) 
  (* MAX_NUM_CHAR = "0" *) 
  (* MEMORY_INIT_FILE = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/weights.mem" *) 
  (* MEMORY_INIT_PARAM = "0" *) 
  (* MEMORY_OPTIMIZATION = "true" *) 
  (* MEMORY_PRIMITIVE = "0" *) 
  (* MEMORY_SIZE = "384" *) 
  (* MEMORY_TYPE = "3" *) 
  (* MESSAGE_CONTROL = "0" *) 
  (* NUM_CHAR_LOC = "0" *) 
  (* P_ECC_MODE = "no_ecc" *) 
  (* P_ENABLE_BYTE_WRITE_A = "0" *) 
  (* P_ENABLE_BYTE_WRITE_B = "0" *) 
  (* P_MAX_DEPTH_DATA = "16" *) 
  (* P_MEMORY_OPT = "yes" *) 
  (* P_MEMORY_PRIMITIVE = "auto" *) 
  (* P_MIN_WIDTH_DATA = "24" *) 
  (* P_MIN_WIDTH_DATA_A = "24" *) 
  (* P_MIN_WIDTH_DATA_B = "24" *) 
  (* P_MIN_WIDTH_DATA_ECC = "24" *) 
  (* P_MIN_WIDTH_DATA_LDW = "4" *) 
  (* P_MIN_WIDTH_DATA_SHFT = "24" *) 
  (* P_NUM_COLS_WRITE_A = "1" *) 
  (* P_NUM_COLS_WRITE_B = "1" *) 
  (* P_NUM_ROWS_READ_A = "1" *) 
  (* P_NUM_ROWS_READ_B = "1" *) 
  (* P_NUM_ROWS_WRITE_A = "1" *) 
  (* P_NUM_ROWS_WRITE_B = "1" *) 
  (* P_SDP_WRITE_MODE = "yes" *) 
  (* P_WIDTH_ADDR_LSB_READ_A = "0" *) 
  (* P_WIDTH_ADDR_LSB_READ_B = "0" *) 
  (* P_WIDTH_ADDR_LSB_WRITE_A = "0" *) 
  (* P_WIDTH_ADDR_LSB_WRITE_B = "0" *) 
  (* P_WIDTH_ADDR_READ_A = "4" *) 
  (* P_WIDTH_ADDR_READ_B = "4" *) 
  (* P_WIDTH_ADDR_WRITE_A = "4" *) 
  (* P_WIDTH_ADDR_WRITE_B = "4" *) 
  (* P_WIDTH_COL_WRITE_A = "24" *) 
  (* P_WIDTH_COL_WRITE_B = "24" *) 
  (* RAM_DECOMP = "auto" *) 
  (* READ_DATA_WIDTH_A = "24" *) 
  (* READ_DATA_WIDTH_B = "24" *) 
  (* READ_LATENCY_A = "1" *) 
  (* READ_LATENCY_B = "1" *) 
  (* READ_RESET_VALUE_A = "0" *) 
  (* READ_RESET_VALUE_B = "0" *) 
  (* RST_MODE_A = "SYNC" *) 
  (* RST_MODE_B = "SYNC" *) 
  (* SIM_ASSERT_CHK = "0" *) 
  (* USE_EMBEDDED_CONSTRAINT = "0" *) 
  (* USE_MEM_INIT = "1" *) 
  (* USE_MEM_INIT_MMI = "0" *) 
  (* VERSION = "0" *) 
  (* WAKEUP_TIME = "0" *) 
  (* WRITE_DATA_WIDTH_A = "24" *) 
  (* WRITE_DATA_WIDTH_B = "24" *) 
  (* WRITE_MODE_A = "1" *) 
  (* WRITE_MODE_B = "1" *) 
  (* WRITE_PROTECT = "1" *) 
  (* XPM_MODULE = "TRUE" *) 
  (* rsta_loop_iter = "24" *) 
  (* rstb_loop_iter = "24" *) 
  xpm_memory_base xpm_memory_base_inst
       (.addra(Q),
        .addrb({1'b0,1'b0,1'b0,1'b0}),
        .clka(i_clk_IBUF),
        .clkb(1'b0),
        .dbiterra(NLW_xpm_memory_base_inst_dbiterra_UNCONNECTED),
        .dbiterrb(NLW_xpm_memory_base_inst_dbiterrb_UNCONNECTED),
        .dina({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .dinb({1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0,1'b0}),
        .douta({NLW_xpm_memory_base_inst_douta_UNCONNECTED[23:19],douta}),
        .doutb(NLW_xpm_memory_base_inst_doutb_UNCONNECTED[23:0]),
        .ena(ena),
        .enb(1'b0),
        .injectdbiterra(1'b0),
        .injectdbiterrb(1'b0),
        .injectsbiterra(1'b0),
        .injectsbiterrb(1'b0),
        .regcea(1'b0),
        .regceb(1'b0),
        .rsta(i_rst_IBUF),
        .rstb(1'b0),
        .sbiterra(NLW_xpm_memory_base_inst_sbiterra_UNCONNECTED),
        .sbiterrb(NLW_xpm_memory_base_inst_sbiterrb_UNCONNECTED),
        .sleep(1'b0),
        .wea(1'b0),
        .web(1'b0));
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
