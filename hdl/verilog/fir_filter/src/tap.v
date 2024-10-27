
module tap
#(
    parameter DATA_WIDTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_din,
    input wire [DATA_WIDTH-1:0] iv_weight,
    input wire [DATA_WIDTH-1:0] iv_sum,
    output wire [DATA_WIDTH-1:0] ov_sum,
    output reg [DATA_WIDTH-1:0] ov_dout,
    output reg o_prod_overflow,
    output reg o_sum_overflow
);

    reg [DATA_WIDTH:0] sum_full;
    reg [DATA_WIDTH-1:0] sum_trunc;
    reg [DATA_WIDTH*2-1:0] product_full;
    reg [DATA_WIDTH-1:0] product_trunc;

    localparam MIN_VALUE = -2**(DATA_WIDTH-1);
    localparam MAX_VALUE = 2**(DATA_WIDTH-1)-1;
    // example: DATA_WIDTH=8: if (prod < -128 or prod > 127) then overflow!

    always @(iv_din, iv_weight, iv_sum) begin
        prod_overflow = 0;
        sum_overflow = 0;

        product_full = signed(iv_din) * signed(iv_weight);
        product_trunc = product_full[DATA_WIDTH-1:0];

        sum_full = signed(product_trunc + iv_sum);
        sum_trunc = sum_full[DATA_WIDTH-1:0];

        if (signed(product_full) < MIN_VALUE or signed(product_full) > MAX_VALUE) begin
            prod_overflow = 1;
        end

        if (signed(sum_full) < MIN_VALUE or signed(sum_full) > MAX_VALUE) begin
            sum_overflow = 1;
        end;
    end

    always @(posedge i_clk) begin
        if (i_rst) begin
            ov_dout = 0;
        end else if (i_en) begin
            ov_dout = iv_din;
        end
    end

    assign ov_sum = sum_trunc;

endmodule
