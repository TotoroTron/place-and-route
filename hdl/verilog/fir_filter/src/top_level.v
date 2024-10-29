
module top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 128
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_din,
    output wire [DATA_WIDTH-1:0] ov_dout
);

    fir_filter
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_DEPTH(FIR_DEPTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(iv_din),
        .ov_dout(ov_dout)
    );

endmodule
