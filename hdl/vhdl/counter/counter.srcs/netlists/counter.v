// Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
// Copyright 2022-2024 Advanced Micro Devices, Inc. All Rights Reserved.
// --------------------------------------------------------------------------------
// Tool Version: Vivado v.2023.2.2 (lin64) Build 4126759 Thu Feb  8 23:52:05 MST 2024
// Date        : Fri Aug  2 15:17:02 2024
// Host        : bcheng-HP running 64-bit Ubuntu 22.04.4 LTS
// Command     : write_verilog
//               /home/bcheng/workspace/dev/place-and-route/hdl/vhdl/counter/counter.srcs/sources_1/netlists/counter.v
// Design      : top_level
// Purpose     : This is a Verilog netlist of the current design or from a specific cell of the design. The output is an
//               IEEE 1364-2001 compliant Verilog HDL file that contains netlist information obtained from the input
//               design files.
// Device      : xc7z020clg400-1
// --------------------------------------------------------------------------------
`timescale 1 ps / 1 ps

module counter
   (Q,
    SR,
    E,
    i_clk_IBUF_BUFG);
  output [3:0]Q;
  input [0:0]SR;
  input [0:0]E;
  input i_clk_IBUF_BUFG;

  wire [0:0]E;
  wire [3:0]Q;
  wire [0:0]SR;
  wire i_clk_IBUF_BUFG;
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
       (.C(i_clk_IBUF_BUFG),
        .CE(E),
        .D(plusOp[0]),
        .Q(Q[0]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[1] 
       (.C(i_clk_IBUF_BUFG),
        .CE(E),
        .D(plusOp[1]),
        .Q(Q[1]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[2] 
       (.C(i_clk_IBUF_BUFG),
        .CE(E),
        .D(plusOp[2]),
        .Q(Q[2]),
        .R(SR));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[3] 
       (.C(i_clk_IBUF_BUFG),
        .CE(E),
        .D(plusOp[3]),
        .Q(Q[3]),
        .R(SR));
endmodule

module pps_gen
   (E,
    SR,
    i_clk_IBUF_BUFG);
  output [0:0]E;
  input [0:0]SR;
  input i_clk_IBUF_BUFG;

  wire \<const0> ;
  wire \<const1> ;
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
  wire i_clk_IBUF_BUFG;
  wire o_pulse__0;

  GND GND
       (.G(\<const0> ));
  VCC VCC
       (.P(\<const1> ));
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
       (.C(i_clk_IBUF_BUFG),
        .CE(\<const1> ),
        .D(\count_reg[0]_i_2_n_7 ),
        .Q(count_reg[0]),
        .R(\count[0]_i_1__0_n_0 ));
  CARRY4 \count_reg[0]_i_2 
       (.CI(\<const0> ),
        .CO({\count_reg[0]_i_2_n_1 ,\count_reg[0]_i_2_n_2 ,\count_reg[0]_i_2_n_3 }),
        .CYINIT(\<const0> ),
        .DI({\<const0> ,\<const0> ,\<const0> ,\<const1> }),
        .O({\count_reg[0]_i_2_n_4 ,\count_reg[0]_i_2_n_5 ,\count_reg[0]_i_2_n_6 ,\count_reg[0]_i_2_n_7 }),
        .S({count_reg[3:1],\count[0]_i_3_n_0 }));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[1] 
       (.C(i_clk_IBUF_BUFG),
        .CE(\<const1> ),
        .D(\count_reg[0]_i_2_n_6 ),
        .Q(count_reg[1]),
        .R(\count[0]_i_1__0_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[2] 
       (.C(i_clk_IBUF_BUFG),
        .CE(\<const1> ),
        .D(\count_reg[0]_i_2_n_5 ),
        .Q(count_reg[2]),
        .R(\count[0]_i_1__0_n_0 ));
  FDRE #(
    .INIT(1'b0)) 
    \count_reg[3] 
       (.C(i_clk_IBUF_BUFG),
        .CE(\<const1> ),
        .D(\count_reg[0]_i_2_n_4 ),
        .Q(count_reg[3]),
        .R(\count[0]_i_1__0_n_0 ));
  LUT4 #(
    .INIT(16'h0200)) 
    o_pulse
       (.I0(count_reg[3]),
        .I1(count_reg[1]),
        .I2(count_reg[2]),
        .I3(count_reg[0]),
        .O(o_pulse__0));
  FDRE #(
    .INIT(1'b0)) 
    o_pulse_reg
       (.C(i_clk_IBUF_BUFG),
        .CE(\<const1> ),
        .D(o_pulse__0),
        .Q(E),
        .R(SR));
endmodule

(* tps = "1000000" *) 
(* STRUCTURAL_NETLIST = "yes" *)
module top_level
   (i_clk,
    i_rst,
    ov_led);
  input i_clk;
  input i_rst;
  output [3:0]ov_led;

  wire i_clk;
  wire i_clk_IBUF;
  wire i_clk_IBUF_BUFG;
  wire i_rst;
  wire i_rst_IBUF;
  wire o_pulse;
  wire [3:0]ov_led;
  wire [3:0]ov_led_OBUF;

  pps_gen CLOCK_ENABLE_GEN
       (.E(o_pulse),
        .SR(i_rst_IBUF),
        .i_clk_IBUF_BUFG(i_clk_IBUF_BUFG));
  counter COUNTER
       (.E(o_pulse),
        .Q(ov_led_OBUF),
        .SR(i_rst_IBUF),
        .i_clk_IBUF_BUFG(i_clk_IBUF_BUFG));
  BUFG i_clk_IBUF_BUFG_inst
       (.I(i_clk_IBUF),
        .O(i_clk_IBUF_BUFG));
  IBUF i_clk_IBUF_inst
       (.I(i_clk),
        .O(i_clk_IBUF));
  IBUF i_rst_IBUF_inst
       (.I(i_rst),
        .O(i_rst_IBUF));
  OBUF \ov_led_OBUF[0]_inst 
       (.I(ov_led_OBUF[0]),
        .O(ov_led[0]));
  OBUF \ov_led_OBUF[1]_inst 
       (.I(ov_led_OBUF[1]),
        .O(ov_led[1]));
  OBUF \ov_led_OBUF[2]_inst 
       (.I(ov_led_OBUF[2]),
        .O(ov_led[2]));
  OBUF \ov_led_OBUF[3]_inst 
       (.I(ov_led_OBUF[3]),
        .O(ov_led[3]));
endmodule
