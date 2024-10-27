module fir_filter 
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

    reg [DATA_WIDTH-1:0] buffers [FIR_DEPTH-1:0];
    reg [DATA_WIDTH-1:0] weights [FIR_DEPTH-1:0];
    reg [DATA_WIDTH-1:0] sums [FIR_DEPTH-1:0];
    reg [FIR_DEPTH-1:0] prod_overflow;
    reg [FIR_DEPTH-1:0] sum_overflow;

    assign ov_dout = sums[FIR_DEPTH-1];

    genvar i;
    generate
        for (i = 0; i < FIR_DEPTH; i++) begin
            if (i==1) begin
                tap #(
                    .DATA_WIDTH(DATA_WIDTH)
                ) inst (
                    .i_clk(i_clk),
                    .i_rst(i_rst),
                    .i_en(i_en),
                    .iv_din(iv_din),
                    .iv_weight(weights[i]),
                    .iv_sum(signed(0)),
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
                    .iv_din(iv_din),
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
