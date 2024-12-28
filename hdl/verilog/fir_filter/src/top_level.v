
module top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 256,
    parameter PIPELINES = 8
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_din_valid,
    input wire i_ready,
    output wire o_ready,
    output wire o_dout,
    output wire o_dout_valid
);

    wire [DATA_WIDTH-1:0] fir_din;
    wire [DATA_WIDTH-1:0] fir_dout;

    wire des_out_valid;

    wire fir_out_valid;
    wire fir_ready;

    wire ser_out_valid;
    wire ser_ready;

    deserializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_din(i_din),
        .i_din_valid(i_din_valid),
        .i_ready(fir_ready),
        .o_ready(o_ready),
        .ov_dout(fir_din),
        .o_dout_valid(des_out_valid)
    );

    fir_filter_direct_form_partially_pipelined
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_DEPTH(FIR_DEPTH),
        .PIPELINES(PIPELINES)
    ) fir_filter_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(fir_din),
        .i_din_valid(des_out_valid),
        .i_ready(ser_ready),
        .o_ready(fir_ready),
        .ov_dout(fir_dout),
        .o_dout_valid(fir_out_valid)
    );

    serializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) serializer_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(fir_dout),
        .i_din_valid(fir_out_valid),
        .i_ready(i_ready),
        .o_ready(ser_ready),
        .o_dout(o_dout),
        .o_dout_valid(ser_out_valid)
    );

    assign o_dout_valid = ser_out_valid;

endmodule
