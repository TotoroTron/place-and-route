
module top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 128
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_rx_end,
    output wire o_tx_end,
    output wire o_dout
);

    wire [DATA_WIDTH-1:0] fir_input;
    wire [DATA_WIDTH-1:0] fir_output;
    wire des_dout_valid;
    wire fir_dout_valid;

    control_unit
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_rx_end(i_rx_end)
        .o_des_valid(des_dout_valid),
        .o_fir_valid(fir_dout_valid)
    );

    deserializer
    #(
        .LENGTH(DATA_WIDTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_rx_end(i_rx_end),
        .i_din(i_din),
        .ov_dout(fir_input)
    );

    fir_filter
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_DEPTH(FIR_DEPTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_rx_end),
        .iv_din(fir_input),
        .ov_dout(fir_output)
    );

    serializer
    #(
        .LENGTH(DATA_WIDTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .o_tx_end(o_tx_end),
        .i_din(fir_output),
        .ov_dout(o_dout)
    );

endmodule
