
module tap_transposed
#(
    parameter DATA_WIDTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire signed [DATA_WIDTH-1:0] iv_din, // Q1.23: 1 sign bit, 23 fraction bits
    input wire signed [DATA_WIDTH-1:0] iv_weight,
    input wire signed [DATA_WIDTH-1:0] iv_sum,
    output reg signed [DATA_WIDTH-1:0] ov_sum,
    output wire signed [DATA_WIDTH-1:0] ov_dout
);

    reg signed [DATA_WIDTH:0] sum_full = 0;
    reg signed [DATA_WIDTH-1:0] sum_trunc = 0;
    reg signed [DATA_WIDTH*2-1:0] product_full = 0;
    reg signed [DATA_WIDTH-1:0] product_trunc = 0;

    always @(posedge i_clk) begin
        if (i_rst) begin
            ov_sum = 0;
        end else if (i_en) begin
            product_full = iv_din * iv_weight;
            product_trunc = product_full[2*DATA_WIDTH-2:DATA_WIDTH-1];
            // Q1.23 x Q1.23 = Q2.46
            // trunc down to Q1.23

            sum_full = product_trunc + iv_sum;
            ov_sum = sum_full[DATA_WIDTH-1:0];
        end
    end

    assign ov_dout = iv_din;

endmodule
