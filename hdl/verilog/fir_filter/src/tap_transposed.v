

module tap_transposed
#(
    parameter DATA_WIDTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire signed [DATA_WIDTH-1:0] iv_din,
    input wire signed [DATA_WIDTH-1:0] iv_weight,
    input wire signed [DATA_WIDTH-1:0] iv_sum,
    output reg signed [DATA_WIDTH-1:0] ov_sum,
    output wire signed [DATA_WIDTH-1:0] ov_dout
    // output reg o_prod_overflow,
    // output reg o_sum_overflow
);

    reg signed [DATA_WIDTH:0] sum_full = 0;
    reg signed [DATA_WIDTH-1:0] sum_trunc = 0;
    reg signed [DATA_WIDTH*2-1:0] product_full = 0;
    reg signed [DATA_WIDTH-1:0] product_trunc = 0;

    localparam MIN_VALUE = -2**(DATA_WIDTH-1);
    localparam MAX_VALUE = 2**(DATA_WIDTH-1)-1;
    // example: DATA_WIDTH=8: if (prod < -128 or prod > 127) then overflow!

    always @(iv_din or iv_weight or iv_sum) begin

        product_full = iv_din * iv_weight;
        product_trunc = product_full[2*DATA_WIDTH-1:DATA_WIDTH];

        sum_full = product_trunc + iv_sum;
        sum_trunc = sum_full[DATA_WIDTH-1:0];
    end

    always @(posedge i_clk) begin
        if (i_rst) begin
            ov_sum = 0;
        end else if (i_en) begin
            ov_sum = sum_trunc;
        end
    end

    assign ov_dout = iv_din;

endmodule