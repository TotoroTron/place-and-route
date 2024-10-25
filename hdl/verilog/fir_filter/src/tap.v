
module tap
    #(
        parameter DATA_WIDTH = 24
    )
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire [DATA_WIDTH-1:0] iv_din,
        input wire [DATA_WIDTH-1:0] iv_weight,
        input wire [DATA_WIDTH-1:0] iv_sum,
        output wire [DATA_WIDTH-1:0] ov_sum,
        output reg [DATA_WIDTH-1:0] ov_dout
    );

    reg [DATA_WIDTH-1:0] sum;
    reg [DATA_WIDTH-1:0] product;

    always @(iv_din, iv_weight, iv_sum) begin
        product = signed(iv_din) * signed(iv_weight);
        sum = signed(product + iv_sum);
    end

    always @(posedge i_clk) begin
        if (i_rst) begin
            ov_dout = 0;
        end else if (i_en) begin
            ov_dout = iv_din;
        end 
    end

    assign ov_sum = sum;

endmodule
