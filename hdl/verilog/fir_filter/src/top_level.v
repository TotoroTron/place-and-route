
module top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_din_valid,
    output wire o_dout,
    output wire o_dout_valid
);

    wire [DATA_WIDTH-1:0] fir_din;
    wire [DATA_WIDTH-1:0] fir_dout;
    wire des_out_valid;
    wire fir_out_valid;
    wire ser_out_valid;

    deserializer
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_din(i_din),
        .i_din_valid(i_din_valid),
        .ov_dout(fir_din),
        .o_dout_valid(des_out_valid)
    );

    fir_filter
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_DEPTH(FIR_DEPTH)
    ) fir_filter_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(fir_din),
        .i_din_valid(des_out_valid),
        .ov_dout(fir_dout),
        .o_dout_valid(fir_out_valid)
    );

    serializer
    #(
        .LENGTH(DATA_WIDTH)
    ) serializer_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(fir_dout),
        .i_din_valid(fir_out_valid),
        .o_dout(o_dout),
        .o_dout_valid(ser_out_valid)
    );

    assign o_dout_valid = ser_out_valid;

endmodule
