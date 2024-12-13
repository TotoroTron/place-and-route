module mac_systolic
#(
    parameter DATA_WIDTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_a,
    input wire [DATA_WIDTH-1:0] iv_b,
    output reg [DATA_WIDTH-1:0] ov_a,
    output reg [DATA_WIDTH-1:0] ov_b,
    output reg [DATA_WIDTH-1:0] ov_sum,


);

    wire signed [DATA_WIDTH*2-1:0] prod_full;
    wire signed [DATA_WIDTH*2-1:0] sum_full;
    wire signed [DATA_WIDTH*2-1:0] sum_full_d1;

    assign prod_full = iv_a * iv_b;
    assign sum_full = prod_full + sum_full_d1;
    assign ov_sum = sum_full_d1[DATA_WIDTH-1:0];

    always @(posedge i_clk) begin
        if (i_rst) begin
            ov_a <= 0;
            ov_b <= 0;
            sum_full_d1 <= 0;
        end else if (i_en) begin
            ov_a <= iv_a;
            ov_b <= ov_b;
            sum_full_d1 <= sum_full;
        end
    end

endmodule
