module fir_filter 
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire signed [DATA_WIDTH-1:0] iv_din,
    input wire i_din_valid,
    output wire signed [DATA_WIDTH-1:0] ov_dout,
    output reg o_dout_valid
);

    wire signed [DATA_WIDTH-1:0] buffers [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] sums [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] weights [FIR_DEPTH-1:0];
    `include "weights0.vh" 

    assign ov_dout = sums[FIR_DEPTH-1];

    always @(posedge i_clk) begin
        if (i_rst) begin
            o_dout_valid = 1'b0;
        end else if (i_en) begin
            o_dout_valid = i_din_valid;
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
                .i_en(i_en & i_din_valid),
                .iv_din(iv_din),
                .iv_weight(weights[i]),
                .iv_sum( { (DATA_WIDTH){1'b0} } ),
                .ov_sum(sums[i]),
                .ov_dout(buffers[i])
            );
        end else begin
            tap #(
                .DATA_WIDTH(DATA_WIDTH)
            ) inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(i_en & i_din_valid),
                .iv_din(buffers[i-1]),
                .iv_weight(weights[i]),
                .iv_sum(sums[i-1]),
                .ov_sum(sums[i]),
                .ov_dout(buffers[i])
            );
        end
    end
    endgenerate
endmodule
