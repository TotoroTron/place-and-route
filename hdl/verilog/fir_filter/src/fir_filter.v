module fir_filter 
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 128
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din_valid,
    input wire signed [DATA_WIDTH-1:0] iv_din,
    output wire signed [DATA_WIDTH-1:0] ov_dout,
    output wire o_dout_valid,
    output wire [FIR_DEPTH-1:0] prod_overflow,
    output wire [FIR_DEPTH-1:0] sum_overflow
);

    wire signed [DATA_WIDTH-1:0] buffers [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] sums [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] weights [FIR_DEPTH-1:0];
    `include "weights.vh" 

    assign ov_dout = sums[FIR_DEPTH-1];

    always @(posedge i_clk) begin
        if (i_rst) begin
            o_dout_valid <= { (LENGHT){1'b0} };
        end else if (i_en) begin
            o_dout_valid <= i_din_valid;
        end
    end

    genvar i;
    generate
    for (i = 0; i < FIR_DEPTH; i = i + 1)begin
        if (i==0) begin
            tap #(
                .DATA_WIDTH(DATA_WIDTH)
            ) inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(i_en),
                .iv_din(iv_din),
                .iv_weight(weights[i]),
                .iv_sum( { (DATA_WIDTH){1'b0} } ),
                .ov_sum(sums[i]),
                .ov_dout(buffers[i]),
                .o_prod_overflow(prod_overflow[i]),
                .o_sum_overflow(sum_overflow[i])
            );
        end else begin
            tap #(
                .DATA_WIDTH(DATA_WIDTH)
            ) inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(i_en),
                .iv_din(buffers[i-1]),
                .iv_weight(weights[i]),
                .iv_sum(sums[i-1]),
                .ov_sum(sums[i]),
                .ov_dout(buffers[i]),
                .o_prod_overflow(prod_overflow[i]),
                .o_sum_overflow(sum_overflow[i])
            );
        end
    end
    endgenerate

endmodule
